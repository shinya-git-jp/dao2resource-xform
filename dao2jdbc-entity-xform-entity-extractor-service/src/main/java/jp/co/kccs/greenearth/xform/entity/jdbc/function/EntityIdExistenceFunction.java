package jp.co.kccs.greenearth.xform.entity.jdbc.function;

import jp.co.kccs.greenearth.commons.GFrameworkException;
import jp.co.kccs.greenearth.xform.entity.dao.core.loader.GDatabaseEntityMetadataLoader;
import jp.co.kccs.greenearth.xform.entity.dao.core.loader.GSEntityMetadataLoader;
import jp.co.kccs.greenearth.xform.entity.jdbc.NotFoundException;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class EntityIdExistenceFunction {
	public String handle(Map<String, Object> parameter) {
		if (!parameter.containsKey("id") || !parameter.containsKey("type")) {
			throw new GFrameworkException("id and type parameters must be exist");
		}
		String entityId = (String) parameter.get("id");
		String type = (String) parameter.get("type");
		if (type.equals("entityId")) {
			try {
				(new GSEntityMetadataLoader()).load(entityId);
			} catch (Exception exception) {
				throw new NotFoundException("Entity ID is not exist");
			}
		} else {
			List<String> entities = (new GDatabaseEntityMetadataLoader()).load(entityId);
			if (entities.isEmpty()) {
				throw new NotFoundException("Database ID is not exist or empty");
			}
		}
		return "ID is exist";
	}
}
