package jp.co.kccs.greenearth.xform.code.jdbc.service.function;

import jp.co.kccs.greenearth.commons.GFrameworkException;
import jp.co.kccs.greenearth.framework.dao.db.GDBVirtualEntity;
import jp.co.kccs.greenearth.xform.code.dao.core.creation.GVeCategoryMetadataLoader;
import jp.co.kccs.greenearth.xform.code.dao.core.creation.GVeMetadataLoader;
import jp.co.kccs.greenearth.xform.dao.common.GVECategory;
import jp.co.kccs.greenearth.xform.dao.common.GVENode;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static jp.co.kccs.greenearth.xform.old.common.GXFormOldUtils.executeSQL;
import static jp.co.kccs.greenearth.xform.old.common.GXFormOldUtils.getSchema;

@Component
public class FetchVeIdByCategoryIdFunction {
	public List<String> handle(String input) {
		return fetchVeIdByCategoryId(input);
	}

	private List<String> fetchVeIdByCategoryId(String categoryId) {
		if (Objects.isNull(categoryId) || categoryId.isEmpty()) {
			List<String> rootCategoryId = getRootCategoryIds();
			List<String> result = new ArrayList<>();
			for (String childCategoryId : rootCategoryId) {
				result.addAll(fetchVeIdByCategoryId(childCategoryId));
			}
			return result;
		}
		GVeMetadataLoader metadataLoader = new GVeCategoryMetadataLoader();
		GVENode<GDBVirtualEntity> virtualEntityGVENode  = metadataLoader.create(categoryId);
		return loadCategoryChild(virtualEntityGVENode);
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

	private List<String> loadCategoryChild(GVENode<GDBVirtualEntity> virtualEntityGVENode) {
		List<String> veIds = new ArrayList<>();
		if (virtualEntityGVENode instanceof GVECategory gveCategory) {
			for (Object child : gveCategory.getChildren()) {
				veIds.addAll(loadCategoryChild((GVENode<GDBVirtualEntity>) child));
			}
			return veIds;
		} else {
			return List.of(virtualEntityGVENode.getId());
		}
	}
}
