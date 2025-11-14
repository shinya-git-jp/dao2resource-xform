package jp.co.kccs.greenearth.xform.entity.dao.core.loader;

import jp.co.kccs.greenearth.commons.GFrameworkException;
import jp.co.kccs.greenearth.commons.db.GConnectionManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static jp.co.kccs.greenearth.xform.old.common.GXFormOldUtils.getSQLExecuter;
import static jp.co.kccs.greenearth.xform.old.common.GXFormOldUtils.getSchema;

public class GDatabaseEntityMetadataLoader implements GEntityMetadataLoader<String, List<String>> {
	@Override
	public List<String> load(String databaseId) {
		if (Objects.isNull(databaseId) || databaseId.isEmpty()) {
			return loadEntities(getAllSEntityIds());
		}
		List<String> entityIds = getEntityIdFromDatabaseId(databaseId);
		return loadEntities(entityIds);
	}
	private List<String> loadEntities(List<String> entityIds) {
		List<String> entities = new ArrayList<>();
		entityIds.forEach(entities::add);
		return entities;
	}
	private List<String> getAllSEntityIds() {
		try {
			ResultSet resultSet = getSQLExecuter().executeQuery(GConnectionManager.getInstance().catchConnection(),
					String.format("Select ObjectID AS \"ObjectID\" from %s.SENTITY", getSchema()));
			List<String> entityIds = new ArrayList<>();
			while (resultSet.next()) {
				entityIds.add(resultSet.getString("ObjectID"));
			}
			return entityIds;
		} catch (SQLException e) {
			throw new GFrameworkException(e);
		}
	}
	private List<String> getEntityIdFromDatabaseId(String databaseId) {
		List<String> entityIds = new ArrayList<>();
		try {
			ResultSet resultSet = getSQLExecuter().executeQuery(GConnectionManager.getInstance().catchConnection(),
					String.format("Select ObjectID AS \"ObjectID\" from %s.SENTITY where DatabaseObjectID = '%s'", getSchema(), databaseId));
			while (resultSet.next()) {
				entityIds.add(resultSet.getString("ObjectID"));
			}

			return entityIds;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
}
