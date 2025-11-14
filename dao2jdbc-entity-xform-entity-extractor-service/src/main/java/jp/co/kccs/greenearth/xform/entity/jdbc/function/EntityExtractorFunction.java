package jp.co.kccs.greenearth.xform.entity.jdbc.function;

import jp.co.kccs.greenearth.commons.GFrameworkException;
import jp.co.kccs.greenearth.framework.dao.db.repository.GDBEntity;
import jp.co.kccs.greenearth.xform.dao.common.GDao2Utils;
import jp.co.kccs.greenearth.xform.dao.dto.GDao2VirtualEntity;
import jp.co.kccs.greenearth.xform.entity.dao.core.loader.GSEntityMetadataLoader;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class EntityExtractorFunction {
	public GDao2VirtualEntity.Entity handle(Map<String, Object> parameter) {
		if (!parameter.containsKey("entityId")) {
			throw new GFrameworkException("entityId parameters must be exist");
		}
		String entityId = (String) parameter.get("entityId");
		GDBEntity dbEntity = null;
		try {
			dbEntity = (new GSEntityMetadataLoader()).load(entityId);
		} catch (Exception exception) {
			throw new GFrameworkException(exception);
		}
		GDao2VirtualEntity.Entity entity = GDao2Utils.convertFrom(dbEntity);
		return entity;
	}



}
