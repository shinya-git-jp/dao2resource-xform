package jp.co.kccs.greenearth.xform.dao.code.cmd;

import jp.co.kccs.greenearth.commons.GFrameworkException;
import jp.co.kccs.greenearth.commons.GFrameworkUtils;
import jp.co.kccs.greenearth.framework.dao.db.GDBVirtualEntity;
import jp.co.kccs.greenearth.xform.code.dao.core.GCrudType;
import jp.co.kccs.greenearth.xform.code.dao.core.GDao2JdbcXFormCodeResult;
import jp.co.kccs.greenearth.xform.code.dao.core.GDao2XFormScriptConverter;
import jp.co.kccs.greenearth.xform.code.dao.core.creation.GVeCategoryMetadataLoader;
import jp.co.kccs.greenearth.xform.code.dao.core.creation.GVeMetadataLoader;
import jp.co.kccs.greenearth.xform.code.dao.core.service.GDao2XFormCodeTransform;
import jp.co.kccs.greenearth.xform.code.dao.core.service.GDao2XFormSqlResult;
import jp.co.kccs.greenearth.xform.code.jdbc.core.export.GExporter;
import jp.co.kccs.greenearth.xform.code.jdbc.core.export.GExporterForm;
import jp.co.kccs.greenearth.xform.code.jdbc.core.export.GExporterFormImpl;
import jp.co.kccs.greenearth.xform.code.jdbc.core.export.GExporterResult;
import jp.co.kccs.greenearth.xform.code.jdbc.core.service.GXFormCodeResult;
import jp.co.kccs.greenearth.xform.code.jdbc.core.service.GXFormCodeTransformService;
import jp.co.kccs.greenearth.xform.code.jdbc.core.service.GXFormScript;
import jp.co.kccs.greenearth.xform.dao.common.*;
import jp.co.kccs.greenearth.xform.dao.dto.GDao2VirtualEntity;
import jp.co.kccs.greenearth.xform.jdbc.configuration.GCommonSettingParser;
import jp.co.kccs.greenearth.xform.jdbc.configuration.GXFormSettingHolder;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

import static jp.co.kccs.greenearth.xform.dao.common.GDao2Utils.executeSQL;
import static jp.co.kccs.greenearth.xform.dao.common.GDao2Utils.getSchema;

public class GDao2XFormCodeCmdServiceImpl implements GDao2XFormCodeCmdService<List<GXFormCodeResult>, byte[]> {
	private static final List<GCrudType> ALL_CRUD_TYPE = List.of(
			GCrudType.DELETE,
		 	GCrudType.UPDATE,
			GCrudType.INSERT,
			GCrudType.SELECT
	);
	@Override
	public GExporterResult<List<GXFormCodeResult>, byte[]> export(String filePath, String settingFile) {
		setDaoCommonSetting(settingFile);
		List<GXFormCodeResult> results = new ArrayList<>();
		if (Objects.isNull(filePath)) {
			results.addAll(exportAll());
		} else {
			results.addAll(exportByParameters(filePath));
		}

		return exportResult(results, "html");
	}
	private List<GXFormCodeResult> exportByParameters(String filePath) {
		List<GXFormCodeResult> results = new ArrayList<>();
		try {
			List<GDao2XFormCodeParameter> parameters = createInputParameter(filePath);
			parameters.forEach(parameter-> {
				results.addAll(exportByParameter(parameter));
			});
			return results;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	private List<GXFormCodeResult> exportByParameter(GDao2XFormCodeParameter parameter) {
		List<GXFormCodeResult> results = new ArrayList<>();
		if (parameter.isCategory()) {
			results.addAll(flattenCategory(transformByCategoryId(parameter)));
		} else {
			results.addAll(transformByVeId(parameter).getData());
		}
		return results;
	}
	private GVENode<List<GXFormCodeResult>> transformCategoryItemToCode(GVENode<?> node, GDao2XFormCodeParameter converterParameter) {
		GDao2XFormCodeParameter parameter = createParameter(node, true, converterParameter.getCrudTypes());
		if (node instanceof GVECategory<?>) {
			return transformByCategoryId(parameter);
		}
		parameter.setCategoryFlag(false);
		GVEItem<List<GXFormCodeResult>> veNode = transformByVeId(parameter);
		veNode.setData(appendCategoryIdToCodeResultsDescription(converterParameter.getId(), veNode.getData()));
		return veNode;
	}
	private List<GXFormCodeResult> appendCategoryIdToCodeResultsDescription(String categoryId, List<GXFormCodeResult> codeResults) {
		Map<String, String> categoryNameMap = new HashMap<>();
		codeResults.forEach(codeResult-> {
			codeResult.setDescription(String.format("%s (カテゴリID: %s)", codeResult.getDescription(), categoryId));
			((GDao2JdbcXFormCodeResult) codeResult).getVirtualEntity().setCategoryId(categoryId);
			if (!categoryNameMap.containsKey(categoryId)) {
				categoryNameMap.put(categoryId, getCategoryName(categoryId));
			}
			((GDao2JdbcXFormCodeResult) codeResult).getVirtualEntity().setCategoryName(categoryNameMap.get(categoryId));
		});
		return codeResults;
	}
	private String getCategoryName(String categoryId) {
		String resourceId = null;
		try {
			List<Map<String, Object>> resultSet = executeSQL(String.format("Select displaynameresourceid AS \"resourceId\" from %s.SCategory where objectid = '%s'", getSchema(), categoryId));
			resourceId = (String) resultSet.get(0).get("resourceId");;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		if (Objects.nonNull(resourceId)) {
			return GSLocalizationResources.getMessage(resourceId, GDao2Utils.getLocale());
		}
		return "-";
	}
	private GDao2XFormCodeParameter createParameter(GVENode<?> node, boolean isCategory, List<GCrudType> crudTypes) {
		GDao2XFormCodeParameter parameter = new GDao2XFormCodeParameterImpl();
		parameter.setId(node == null ? "" : node.getId());
		parameter.setCategoryFlag(isCategory);
		parameter.setCrudTypes(crudTypes);
		return parameter;
	}
	private GVECategory<List<GXFormCodeResult>> transformByCategoryId(GDao2XFormCodeParameter parameter) {
		GVeMetadataLoader veMetadataLoader = new GVeCategoryMetadataLoader();
		GVECategory<GDBVirtualEntity> virtualEntityGroup = (GVECategory<GDBVirtualEntity>) veMetadataLoader.create(parameter.getId());
		GVECategory<List<GXFormCodeResult>> categoryResult = new GVECategoryImpl<>();
		virtualEntityGroup.getChildren().forEach(
				node -> {
					categoryResult.addChildren(transformCategoryItemToCode(node, parameter));
				}
		);
		return categoryResult;
	}
	private GVEItem<List<GXFormCodeResult>> transformByVeId(GDao2XFormCodeParameter parameter) {
		GVEItem<List<GXFormCodeResult>> listGVEItem = new GVEItemImpl<>();
		List<GXFormCodeResult> results = new ArrayList<>();
		parameter.getCrudTypes().forEach(crudType -> {
			GDao2XFormCodeTransform transformer = GDao2XFormCodeTransform.getInstance();
			GDao2XFormSqlResult sqlResult = transformer.transformByVeId(parameter.getId(), crudType);
			if (Objects.nonNull(sqlResult.getSqlScript())) {
				GDao2VirtualEntity virtualEntity = GDao2Utils.convertFrom(sqlResult.getVirtualEntity());
				GXFormScript gxFormScript = GDao2XFormScriptConverter.getInstance().convert(sqlResult.getSqlScript(), fromDaoCrudType(sqlResult.getType()), virtualEntity);
				GXFormCodeResult codeResult = GXFormCodeTransformService.getInstance().transform(gxFormScript);
				results.add(GDao2JdbcXFormCodeResult.from(codeResult, virtualEntity));
			}
		});
		listGVEItem.setId(parameter.getId());
		listGVEItem.setData(results);
		return listGVEItem;
	}

	private jp.co.kccs.greenearth.xform.jdbc.common.GCrudType fromDaoCrudType(GCrudType crudType) {
		return jp.co.kccs.greenearth.xform.jdbc.common.GCrudType.valueOf(crudType.toString());
	}
	private List<GDao2XFormCodeParameter> createInputParameter(String filePath) throws IOException {
		GDao2XFormCodeCmdInputParser parser = GFrameworkUtils.getComponent(GDao2XFormCodeCmdInputParser.class);
		return parser.parse(filePath, "UTF-8");
	}
	private List<GXFormCodeResult> exportAll() {
		List<GXFormCodeResult> results = new ArrayList<>();
		for (GVECategory<List<GXFormCodeResult>> resultList : transformAll()) {
			results.addAll(flattenCategory(resultList));
		}
		return results;
	}
	private List<GVECategory<List<GXFormCodeResult>>> transformAll() {
		List<GVECategory<List<GXFormCodeResult>>> categories = new ArrayList<>();
		GVeMetadataLoader veMetadataLoader = new GVeCategoryMetadataLoader();
		for (String rootCategoryId : getRootCategoryIds()) {
			GVECategory<GDBVirtualEntity> virtualEntityGroup = (GVECategory<GDBVirtualEntity>) veMetadataLoader.create(rootCategoryId);
			categories.add(transformByCategoryId(createParameter(virtualEntityGroup, true, ALL_CRUD_TYPE)));
		}
		return categories;
	}
	private List<String> getRootCategoryIds() {
		List<String> categoryIds = new ArrayList<>();
		try {
			List<Map<String, Object>> result = executeSQL(String.format("Select ObjectID AS \"ObjectID\" from %s.SCategory where ParentObjectID IS NULL ORDER BY SortIndex", getSchema()));
			for (Map<String, Object> stringObjectMap : result) {
				categoryIds.add((String) stringObjectMap.get("ObjectID"));
			}
		} catch (SQLException e) {
			throw new GFrameworkException(e);
		}
		return categoryIds;
	}
	private GExporterResult<List<GXFormCodeResult>, byte[]> exportResult(List<GXFormCodeResult> codeResults, String extension) {
		GExporter exporter = GFrameworkUtils.getComponent(GExporter.class);
		GExporterForm<List<GXFormCodeResult>> exporterForm = new GExporterFormImpl();
		exporterForm.setMediaType("html");
		exporterForm.setData(codeResults);
		return (GExporterResult<List<GXFormCodeResult>, byte[]>) exporter.export(exporterForm);
	}
	private List<GXFormCodeResult> flattenCategory(GVECategory<List<GXFormCodeResult>> results) {
		List<GXFormCodeResult> resultList = new ArrayList<>();
		for (GVENode<List<GXFormCodeResult>> child : results.getChildren()) {
			if (child instanceof GVECategory<?>) {
				resultList.addAll(flattenCategory((GVECategory<List<GXFormCodeResult>>) child));
			} else if (child instanceof GVEItem<?>) {
				resultList.addAll(((GVEItem<List<GXFormCodeResult>>) child).getData());
			}
		}
		return resultList;
	}
	private void setDaoCommonSetting(String settingFilePath) {
		GCommonSettingParser<String> commonSettingParser = GFrameworkUtils.getComponent(GDaoCommonSettingParserFilePath.class);
		GDaoCommonSetting daoCommonSetting = (GDaoCommonSetting) commonSettingParser.parse(settingFilePath);
		GDao2Utils.setCommonSetting(daoCommonSetting);
	}
}
