package jp.co.kccs.greenearth.xform.entity.dao.core.loader;

import jp.co.kccs.greenearth.framework.dao.db.GDBVirtualEntity;
import jp.co.kccs.greenearth.framework.dao.db.GDBVirtualEntityImpl;
import jp.co.kccs.greenearth.framework.dao.db.repository.GDBEntity;
import jp.co.kccs.greenearth.framework.dao.db.repository.GDBEntityFactory;
import jp.co.kccs.greenearth.framework.dao.db.repository.GForeignKey;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static jp.co.kccs.greenearth.xform.old.common.GXFormOldUtils.executeSQL;
import static jp.co.kccs.greenearth.xform.old.common.GXFormOldUtils.getSchema;

public class GSEntityMetadataLoader implements GEntityMetadataLoader<String, GDBEntity> {
	@Override
	public GDBEntity load(String entityId) {
		GDBEntity gdbEntity = GDBEntityFactory.getDBEntity(entityId);
		gdbEntity.setVirtualEntity(getDbEntityForeignKey(entityId));
		return gdbEntity;
	}
	private GDBVirtualEntity getDbEntityForeignKey(String entityId) {
		GDBVirtualEntity ve = new GDBVirtualEntityImpl();
		try {
			List<Map<String, Object>> resultSet = executeSQL(String.format("Select objectID AS \"objectID\" from %s.SForeignKey where sourceEntityObjectID =  '%s'", getSchema(), entityId));
			List<String> resultList = new ArrayList<>();
			for (Map<String, Object> stringObjectMap : resultSet) {
				resultList.add((String) stringObjectMap.get("objectID"));
			}
			resultList.forEach(result-> {
				GForeignKey foreignKey = GDBEntityFactory.getForeignKey(result);
				ve.addForeignKey(foreignKey);
			});
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return ve;
	}
}
