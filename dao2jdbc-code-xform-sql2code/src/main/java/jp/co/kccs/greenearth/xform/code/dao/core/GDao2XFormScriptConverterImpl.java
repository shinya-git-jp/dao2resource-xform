/*
 * Copyright 2024 Kyocera Communication Systems Co., Ltd All rights reserved.
 */
package jp.co.kccs.greenearth.xform.code.dao.core;

import jp.co.kccs.greenearth.commons.GFrameworkException;
import jp.co.kccs.greenearth.commons.GFrameworkUtils;
import jp.co.kccs.greenearth.commons.collection.GListMap;
import jp.co.kccs.greenearth.commons.collection.GListMapImpl;
import jp.co.kccs.greenearth.xform.code.jdbc.core.GLikeType;
import jp.co.kccs.greenearth.xform.code.jdbc.core.GTrimType;
import jp.co.kccs.greenearth.xform.code.jdbc.core.service.GXFormScript;
import jp.co.kccs.greenearth.xform.code.jdbc.core.service.GXFormScriptBuilder;
import jp.co.kccs.greenearth.xform.dao.dto.GDao2VirtualEntity;
import jp.co.kccs.greenearth.xform.jdbc.common.*;

import java.util.*;

import static jp.co.kccs.greenearth.xform.jdbc.common.GCommonUtils.getGenerateType;
import static jp.co.kccs.greenearth.xform.jdbc.common.GCommonUtils.isExclusion;

/**
 * @author KCCS XXXXX
 * @create GEF_NEXT_DATE
 * @since GEF_NEXT_VERSION
 */
public class GDao2XFormScriptConverterImpl implements GDao2XFormScriptConverter {
	private static final Map<String, GLikeType> LIKE_TYPE_MAP = Map.of(
			"LIKE_PART", GLikeType.LIKE_PART,
			"LIKE_SUFFIX", GLikeType.LIKE_SUFFIX,
			"LIKE_PREFIX", GLikeType.LIKE_PREFIX
	);

	@Override
	public GXFormScript convert(String sqlScript, GCrudType crudType, GDao2VirtualEntity virtualEntity) {
		if (Objects.isNull(virtualEntity)) {
			throw new GFrameworkException("Virtual Entity cannot be null.");
		}
		if (Objects.isNull(crudType)) {
			throw new GFrameworkException("Crud type cannot be null.");
		}
		GXFormEntity entity = convertFromDBEntity(virtualEntity.getRefEntity());
		GListMap<GXFormEntityColumn, AdditionalAttribute> filterColumns = createFilterColumnWithAdditionalAttribute(entity, virtualEntity);
		GListMap<GXFormEntityColumn, AdditionalAttribute> searchColumns = createSearchColumnWithAdditionalAttribute(entity, virtualEntity);
		GListMap<GXFormEntityColumn, String> selectColumns = getSelectColumns(entity, virtualEntity);
		GXFormScriptBuilder builder = buildScript(sqlScript, crudType, virtualEntity, entity);
		filterColumns.entrySet().forEach(columnAdditionalAttributeEntry -> {
			addWhereColumnToBuilder(builder, columnAdditionalAttributeEntry);
		});
		searchColumns.entrySet().forEach(columnAdditionalAttributeEntry -> {
			addWhereColumnToBuilder(builder, columnAdditionalAttributeEntry);
		});
		selectColumns.entrySet().forEach(columnWithAlias -> {
			addSelectColumnToBuilder(builder, columnWithAlias);
		});
		GXFormScript gxFormScript = builder.build();
		return gxFormScript;
	}

	private GListMap<GXFormEntityColumn, String> getSelectColumns(GXFormEntity entity, GDao2VirtualEntity veMetadata) {
		GListMap<GXFormEntityColumn, String> selectColumns = new GListMapImpl<>();
		List<GDao2VirtualEntity.ForeignKey> foreignKeys = Objects.isNull(veMetadata.getForeignKeys()) ? Collections.emptyList() : veMetadata.getForeignKeys();
		List<GDao2VirtualEntity.VirtualColumn> columns = Objects.isNull(veMetadata.getColumns()) ? Collections.emptyList() : veMetadata.getColumns();

		Map<String, GXFormEntityColumn> entityColumnMap = combineEntityAndForeignKeyMap(entity, foreignKeys.stream().toList());

		for (GDao2VirtualEntity.VirtualColumn virtualColumn : columns.stream().filter(col-> Objects.nonNull(col.getRefColumn())).toList()) {
			GXFormEntityColumn entityColumn = entityColumnMap.get(
					String.format("%s",
							virtualColumn.getRefColumn()
					)
			);
			selectColumns.put(entityColumn, virtualColumn.getName());
		}
		return selectColumns;
	}
	private GXFormScriptBuilder buildScript(String script, GCrudType crudType, GDao2VirtualEntity virtualEntity, GXFormEntity entity) {
		return GXFormScript.getBuilder()
				.script(script)
				.crudType(GCrudType.valueOf(crudType.toString()))
				.description("仮想表ID: " + virtualEntity.getVeId())
				.addEntity(entity);
	}
	private void addWhereColumnToBuilder(GXFormScriptBuilder builder, Map.Entry<GXFormEntityColumn, AdditionalAttribute> columnAdditionalAttributeEntry) {
		AdditionalAttribute additionalAttribute = columnAdditionalAttributeEntry.getValue();
		builder.addScriptColumn()
				.fromColumn(columnAdditionalAttributeEntry.getKey())
				.trimValue(additionalAttribute.trimValue)
				.optional(additionalAttribute.optional)
				.like(additionalAttribute.likeType)
				.isWhereColumn(true)
				.finish();
	}
	private void addSelectColumnToBuilder(GXFormScriptBuilder builder, Map.Entry<GXFormEntityColumn, String> selectColumns) {
		builder.addScriptColumn()
				.fromColumn(selectColumns.getKey())
				.alias(selectColumns.getValue())
				.isSelectColumn(true)
				.finish();
	}
	private GListMap<GXFormEntityColumn, AdditionalAttribute> createFilterColumnWithAdditionalAttribute(GXFormEntity entity, GDao2VirtualEntity veMetadata) {
		GListMap<GXFormEntityColumn, AdditionalAttribute> filterColumns = new GListMapImpl<>();
		List<GDao2VirtualEntity.ForeignKey> foreignKeys = Objects.isNull(veMetadata.getForeignKeys()) ? Collections.emptyList() : veMetadata.getForeignKeys();
		List<GDao2VirtualEntity.FilterCondition> filterConditions = Objects.isNull(veMetadata.getFilterConditions()) ? Collections.emptyList() : veMetadata.getFilterConditions();
		Map<String, GXFormEntityColumn> entityColumnMap = combineEntityAndForeignKeyMap(entity, foreignKeys.stream().toList());
		List<GDao2VirtualEntity.FilterCondition> conditionColumns = filterConditions.stream().filter(conditionColumn -> Objects.nonNull(conditionColumn.getColumn())).toList();
		for (GDao2VirtualEntity.FilterCondition conditionColumn : conditionColumns) {
			if (!conditionColumn.getComparisonOperator().equals("IN") &&
					!conditionColumn.getComparisonOperator().equals("NOT_IN") &&
					!conditionColumn.getComparisonOperator().equals("IS_NULL") &&
					!conditionColumn.getComparisonOperator().equals("IS_NOT_NULL") &&
					!conditionColumn.getComparisonOperator().equals("LIKE_PREFIX") &&
					!conditionColumn.getComparisonOperator().equals("LIKE_SUFFIX") &&
					!conditionColumn.getComparisonOperator().equals("LIKE_PART")
			) {
				GXFormEntityColumn entityColumn = entityColumnMap.get(
						String.format("%s.%s",
								conditionColumn.getEntity(),
								conditionColumn.getColumn())
				);
				filterColumns.put(new GXFormEntityColumnImpl(entityColumn, entityColumn.getEntity()), createAdditionalAttribute(conditionColumn));

			}
		}
		return filterColumns;
	}
	private GListMap<GXFormEntityColumn, AdditionalAttribute> createSearchColumnWithAdditionalAttribute(GXFormEntity entity, GDao2VirtualEntity veMetadata) {
		GListMap<GXFormEntityColumn, AdditionalAttribute> searchColumns = new GListMapImpl<>();
		List<GDao2VirtualEntity.ForeignKey> foreignKeys = Objects.isNull(veMetadata.getForeignKeys()) ? Collections.emptyList() : veMetadata.getForeignKeys();
		List<GDao2VirtualEntity.SearchCondition> searchConditions = Objects.isNull(veMetadata.getSearchConditions()) ? Collections.emptyList() : veMetadata.getSearchConditions();
		Map<String, GXFormEntityColumn> entityColumnMap = combineEntityAndForeignKeyMap(entity, foreignKeys.stream().toList());

		List<GDao2VirtualEntity.SearchCondition> conditionColumns = searchConditions.stream().filter(conditionColumn -> Objects.nonNull(conditionColumn.getColumn())).toList();
		for (GDao2VirtualEntity.SearchCondition conditionColumn : conditionColumns) {
			GXFormEntityColumn entityColumn = entityColumnMap.get(
					String.format("%s.%s",
							conditionColumn.getEntity(),
							conditionColumn.getColumn())
			);
			searchColumns.put(new GXFormEntityColumnImpl(entityColumn, entityColumn.getEntity()), createAdditionalAttribute(conditionColumn));
		}
		return searchColumns;
	}
	private AdditionalAttribute createAdditionalAttribute(GDao2VirtualEntity.SearchCondition conditionColumn) {
		AdditionalAttribute additionalAttribute = new AdditionalAttribute();
		if (conditionColumn.isTrim()) {
			additionalAttribute.trimValue = GTrimType.BOTH;
		}
		if (conditionColumn.isOptional()) {
			additionalAttribute.optional = true;
		}

		if (LIKE_TYPE_MAP.containsKey(conditionColumn.getComparisonOperator())) {
			additionalAttribute.likeType = LIKE_TYPE_MAP.get(conditionColumn.getComparisonOperator());
		}
		return additionalAttribute;
	}
	private AdditionalAttribute createAdditionalAttribute(GDao2VirtualEntity.FilterCondition conditionColumn) {
		AdditionalAttribute additionalAttribute = new AdditionalAttribute();
		if (LIKE_TYPE_MAP.containsKey(conditionColumn.getComparisonOperator())) {
			additionalAttribute.likeType = LIKE_TYPE_MAP.get(conditionColumn.getComparisonOperator());
		}
		return additionalAttribute;
	}
	private Map<String, GXFormEntityColumn> combineEntityAndForeignKeyMap(GXFormEntity entity, List<GDao2VirtualEntity.ForeignKey> foreignKeys) {
		List<GXFormEntity> foreignKeyEntities = createForeignKeyEntityMap(foreignKeys.stream().toList());
		Map<String, GXFormEntityColumn> columnMap = new HashMap<>();
		entity.getColumns().forEach(col-> {
			columnMap.put(String.format("%s.%s", entity.getName(), col.getName()), col);
		});
		foreignKeyEntities.forEach(fkEntity-> {
			fkEntity.getColumns().forEach(col-> {
				columnMap.put(String.format("%s.%s", fkEntity.getName(), col.getName()), col);
			});
		});
		return columnMap;
	}
	private List<GXFormEntity> createForeignKeyEntityMap(List<GDao2VirtualEntity.ForeignKey> foreignKeys) {
		List<GXFormEntity> foreignKeyMap = new ArrayList<>();
		for (GDao2VirtualEntity.ForeignKey foreignKey : foreignKeys) {
			foreignKeyMap.add(convertFromDBEntity(foreignKey.getReferenceEntity()));
		}
		return foreignKeyMap;
	}


	private static class AdditionalAttribute {
		private GTrimType trimValue;
		private GLikeType likeType;
		private boolean optional;
	}

	public static GXFormEntity convertFromDBEntity(GDao2VirtualEntity.Entity parameter) {
		GXFormEntityBuilder builder = GXFormEntity.builder()
				.name(parameter.getName())
				.databaseName(parameter.getDatabaseName())
				.phyName(parameter.getName());
		if (Objects.nonNull(parameter.getColumns())) {
			parameter.getColumns().forEach(col-> {
				addColumn(builder, col);
			});
		}
		if (Objects.nonNull(parameter.getPrimaryKey())) {
			addPrimaryKey(builder, parameter.getPrimaryKey());
		}

		if (Objects.nonNull(parameter.getUniqueKeys())) {
			parameter.getUniqueKeys().forEach(uniqueKey-> {
				addUniqueKey(builder, uniqueKey);
			});
		}

		if (Objects.nonNull(parameter.getForeignKeys()) && !parameter.getForeignKeys().isEmpty()) {
			parameter.getForeignKeys().forEach(value -> {
				addForeignKey(builder, value);
			});
		}

		return builder.build();
	}

	private static GXFormEntityBuilder addColumn(GXFormEntityBuilder builder, GDao2VirtualEntity.Column col) {
		String decimalSize = Objects.isNull(col.getDecimalSize()) ? "0" : col.getDecimalSize().equals("-") ? "0" : col.getDecimalSize();
		return builder.addColumn()
				.name(col.getName())
				.type(convertStringToGColumnTypeVE(col.getDataType()))
				.generateType(getGenerateType(col.getName()))
				.phyName(col.getName())
				.exclusiveKey(isExclusion(col.getName()))
				.scale(Integer.parseInt(decimalSize))
				.size(col.getSize())
				.finish();
	}
	private static GXFormEntityBuilder addPrimaryKey(GXFormEntityBuilder builder, GDao2VirtualEntity.Column col) {
		String decimalSize = Objects.isNull(col.getDecimalSize()) ? "0" : col.getDecimalSize().equals("-") ? "0" : col.getDecimalSize();
		return builder.addPrimaryKeyColumn()
				.name(col.getName())
				.type(convertStringToGColumnTypeVE(col.getDataType()))
				.generateType(getGenerateType(col.getName()))
				.phyName(col.getName())
				.exclusiveKey(isExclusion(col.getName()))
				.scale(Integer.parseInt(decimalSize))
				.size(col.getSize())
				.finish();
	}
	private static GXFormEntityBuilder addUniqueKey(GXFormEntityBuilder builder, GDao2VirtualEntity.UniqueKey uniqueKey) {
		GXFormEntityUniqueKeyBuilder uniqueKeyBuilder =	builder.addUniqueKey()
				.name(uniqueKey.getName());
		uniqueKey.getUniqueKeyColumns().forEach(col->{
			uniqueKeyBuilder
					.addColumn()
					.name(col.getName())
					.finish();
		});
		return uniqueKeyBuilder.finish();
	}
	private static GXFormEntityBuilder addForeignKey(GXFormEntityBuilder builder, GDao2VirtualEntity.ForeignKey foreignKey) {
		GXFormEntityForeignKeyBuilder fkBuilder = builder.addForeignKey()
				.name(foreignKey.getName())
				.refEntity(foreignKey.getReferenceEntity().getName());
		for (GDao2VirtualEntity.ForeignKeyColumn fkCol : foreignKey.getForeignKeyColumns()) {
			fkBuilder = buildForeignKey(fkBuilder, fkCol);
		}
		return fkBuilder.finish();
	}
	private static GXFormEntityForeignKeyBuilder buildForeignKey(GXFormEntityForeignKeyBuilder fkBuilder, GDao2VirtualEntity.ForeignKeyColumn foreignKeyColumn) {
		GXFormEntityForeignKeyColumnBuilder fkColBuilder = fkBuilder
				.addColumn();
		if (Objects.nonNull(foreignKeyColumn.getSourceColumn())) {
			fkColBuilder = fkColBuilder.srcColumn(foreignKeyColumn.getSourceColumn());
		}
		String constValue = foreignKeyColumn.getConstValue().equals("-") ? null : foreignKeyColumn.getConstValue();
		return fkColBuilder.refColumn(foreignKeyColumn.getReferenceColumn())
				.constValue(constValue)
				.isNull(false)
				.bindParameter(false)
				.finish();
	}

	public static GColumnType convertStringToGColumnTypeVE(String dataType) {
		if (dataType.equals("INTEGER")) {
			return GColumnType.INTEGER;
		} else if (dataType.equals("STRING")) {
			return GColumnType.VARCHAR;
		} else if (dataType.equals("DECIMAL")) {
			return GColumnType.DOUBLE;
		} else if (dataType.equals("DATE")) {
			return GColumnType.DATE;
		} else if (dataType.equals("LONG")) {
			return GColumnType.LONG;
		}
		return null;
	}
	private static GGenerateType getGenerateType(String phyName) {
		if (Objects.isNull(phyName) || phyName.equals("-")) {
			return null;
		} else {
			String registeredValue = GFrameworkUtils.getProperty("geframe.dao.InsertedDateKey");
			if (phyName.equals(registeredValue)) {
				return GGenerateType.REGISTERED_DATE;
			} else {
				String updatedValue = GFrameworkUtils.getProperty("geframe.dao.UpdatedDateKey");
				if (phyName.equals(updatedValue)) {
					return GGenerateType.UPDATED_DATE;
				}
			}
		}
		return null;
	}
}
