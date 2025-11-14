package jp.co.kccs.greenearth.xform.code.jdbc.service.function;

import jp.co.kccs.greenearth.commons.GFrameworkException;
import jp.co.kccs.greenearth.framework.dao.db.GDBVirtualEntity;
import jp.co.kccs.greenearth.xform.code.dao.core.creation.GVeCategoryMetadataLoader;
import jp.co.kccs.greenearth.xform.code.dao.core.creation.GVeIdMetadataLoaderImpl;
import jp.co.kccs.greenearth.xform.code.dao.core.creation.GVeMetadataLoader;
import jp.co.kccs.greenearth.xform.code.jdbc.service.NotFoundException;
import jp.co.kccs.greenearth.xform.dao.common.GVECategory;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class VeIdExistenceFunction {

	public String handle(Map<String, Object> parameter) {
		if (!parameter.containsKey("id") || !parameter.containsKey("type")) {
			throw new GFrameworkException("id and type parameters must be exist");
		}
		String veId = (String) parameter.get("id");
		String type = (String) parameter.get("type");
		try {
			if (type.equals("categoryId")) {
				GVeMetadataLoader categoryLoader = new GVeCategoryMetadataLoader();
				GVECategory<GDBVirtualEntity> entities = (GVECategory<GDBVirtualEntity>) categoryLoader.create(veId);
				if (entities.getSize() == 0) {
					throw new NotFoundException("Category ID is not exist");
				}
			}
			else {
				try {
					GVeMetadataLoader veIdLoader = new GVeIdMetadataLoaderImpl();
					veIdLoader.create(veId);
				} catch (Exception exception) {
					throw new NotFoundException("Virtual Entity ID is not exist");
				}
			}
			return "ID is exist.";
		} catch (Exception exception) {
			if (type.equals("categoryId")) {
				throw new NotFoundException("Category ID is not exist");
			} else {
				throw new NotFoundException("Virtual Entity ID is not exist");
			}
		}
	}
}
