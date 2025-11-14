package jp.co.kccs.greenearth.xform.dao.entity.cmd;

import jp.co.kccs.greenearth.commons.GFrameworkUtils;
import jp.co.kccs.greenearth.framework.dao.db.repository.GDBEntity;
import jp.co.kccs.greenearth.xform.dao.common.GDao2Utils;
import jp.co.kccs.greenearth.xform.dao.common.GDaoCommonSetting;
import jp.co.kccs.greenearth.xform.dao.common.GDaoCommonSettingParserFilePath;
import jp.co.kccs.greenearth.xform.dao.dto.GDao2VirtualEntity;
import jp.co.kccs.greenearth.xform.dao.entity.mapper.GEntityMetadataMapper;
import jp.co.kccs.greenearth.xform.dao.entity.mapper.GXFormEntityMapper;
import jp.co.kccs.greenearth.xform.entity.dao.core.loader.GDatabaseEntityMetadataLoader;
import jp.co.kccs.greenearth.xform.entity.dao.core.loader.GEntityMetadataLoader;
import jp.co.kccs.greenearth.xform.entity.dao.core.loader.GSEntityMetadataLoader;
import jp.co.kccs.greenearth.xform.entity.jdbc.core.service.GEntityFileDescriptor;
import jp.co.kccs.greenearth.xform.entity.jdbc.core.service.GEntityFileTransformService;
import jp.co.kccs.greenearth.xform.jdbc.configuration.GCommonSettingParser;
import jp.co.kccs.greenearth.xform.jdbc.configuration.GXFormSettingHolder;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static jp.co.kccs.greenearth.xform.dao.common.GDao2Utils.*;

public class GDao2XFormEntityCmdServiceImpl implements GDao2XFormEntityCmdService {
	@Override
	public List<GEntityFileDescriptor> export(String filePath, String settingFilePath) {
		setDaoCommonSetting(settingFilePath);
		List<String> entityIds = convertToEntityIds(filePath);
		List<GDBEntity> entities = new ArrayList<>();
		GEntityMetadataLoader<String, GDBEntity> loader = new GSEntityMetadataLoader();
		entityIds.forEach(entityId -> {
			GDBEntity entity = loader.load(entityId);
			entities.add(entity);
		});
		GEntityMetadataMapper<GDao2VirtualEntity.Entity> mapper = new GXFormEntityMapper();
		List<GEntityFileDescriptor> descriptors = new ArrayList<>();
		GEntityFileTransformService service = GEntityFileTransformService.getInstance();
		entities.forEach(e-> {
			descriptors.add(service.transform(mapper.map(convertFrom(e))));
		});
		return descriptors;
	}
	private List<String> convertToEntityIds(String filePath) {
		List<String> entityIds = new ArrayList<>();
		if (Objects.isNull(filePath) || filePath.isEmpty()) {
			entityIds.addAll(getAllSEntityIds());
		} else {
			GDao2XFormEntityCmdInputParser parser = GDao2XFormEntityCmdInputParser.getInstance();
			entityIds.addAll(parser.parse(filePath, "UTF-8"));
		}
		return entityIds;
	}

	private void setDaoCommonSetting(String settingFilePath) {
		GCommonSettingParser<String> commonSettingParser = GFrameworkUtils.getComponent(GDaoCommonSettingParserFilePath.class);
		GDaoCommonSetting daoCommonSetting = (GDaoCommonSetting) commonSettingParser.parse(settingFilePath);
		GDao2Utils.setCommonSetting(daoCommonSetting);
	}
	private List<String> getAllSEntityIds() {
		GDatabaseEntityMetadataLoader databaseEntityMetadataLoader = new GDatabaseEntityMetadataLoader();
		return databaseEntityMetadataLoader.load(null);
	}
}
