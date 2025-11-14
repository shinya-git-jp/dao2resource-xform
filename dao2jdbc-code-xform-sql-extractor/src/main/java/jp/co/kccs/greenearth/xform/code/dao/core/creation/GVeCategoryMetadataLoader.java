package jp.co.kccs.greenearth.xform.code.dao.core.creation;

import jp.co.kccs.greenearth.commons.GFrameworkException;
import jp.co.kccs.greenearth.framework.dao.db.GDBVirtualEntity;
import jp.co.kccs.greenearth.framework.dao.db.GDBVirtualEntityFactory;
import jp.co.kccs.greenearth.xform.dao.common.GVECategoryImpl;
import jp.co.kccs.greenearth.xform.dao.common.GVEItem;
import jp.co.kccs.greenearth.xform.dao.common.GVEItemImpl;
import jp.co.kccs.greenearth.xform.dao.common.GVENode;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static jp.co.kccs.greenearth.xform.old.common.GXFormOldUtils.executeSQL;
import static jp.co.kccs.greenearth.xform.old.common.GXFormOldUtils.getSchema;

public class GVeCategoryMetadataLoader implements GVeMetadataLoader {
	@Override
	public GVENode<GDBVirtualEntity> create(String id) {
		GVECategoryImpl<GDBVirtualEntity> groupNode = new GVECategoryImpl<>();
		groupNode.setId(id);
		try {
			List<GVENode<GDBVirtualEntity>> children = new ArrayList<>(getVeEntityListByCategoryId(id));
			children.addAll(getSubCategoryList(id));
			groupNode.setChildren(children);
		} catch (SQLException e) {
			throw new GFrameworkException(e);
		}
		return groupNode;
	}
	private List<GVENode<GDBVirtualEntity>> getVeEntityListByCategoryId(String id) throws SQLException {
		List<GVENode<GDBVirtualEntity>> children = new ArrayList<>();
		List<String> resultList = getVeIds(id);
		resultList.forEach(record -> {
			children.add(getVeEntityNodeByCategoryId(record));
		});
		return children;
	}
	private GVEItem<GDBVirtualEntity> getVeEntityNodeByCategoryId(String objectId) {
		GDBVirtualEntity virtualEntity = GDBVirtualEntityFactory.getVirtualEntity(objectId);
		GVEItem<GDBVirtualEntity> node = new GVEItemImpl<>();
		node.setId(virtualEntity.getObjectID());
		node.setData(virtualEntity);
		return node;
	}
	private List<GVENode<GDBVirtualEntity>> getSubCategoryList(String id) throws SQLException {
		List<GVENode<GDBVirtualEntity>> children = new ArrayList<>();
		List<Map<String, Object>> result = executeSQL(String.format("Select ObjectID AS \"ObjectID\" from %s.SCategory where ParentObjectID = '%s' ORDER BY SortIndex", getSchema(), id));
		List<String> resultList = new ArrayList<>();
		for (Map<String, Object> stringObjectMap : result) {
			resultList.add((String) stringObjectMap.get("ObjectID"));
		}
		List<GVENode<GDBVirtualEntity>> childList = new ArrayList<>();
		resultList.forEach(record -> {
			children.add(getSubCategory(record, childList));
		});
		return children;
	}
	private GVENode<GDBVirtualEntity> getSubCategory(String subCategoryId, List<GVENode<GDBVirtualEntity>> childList) {
		GVECategoryImpl<GDBVirtualEntity> node = new GVECategoryImpl<>();
		node.setId(subCategoryId);
		childList.add(create(subCategoryId));
		node.setChildren(childList);
		return node;
	}
	private List<String> getVeIds(String id) {
		try {
			List<Map<String, Object>> result = executeSQL(String.format("Select ObjectID AS \"ObjectID\" from %s.SVirtualEntity where CategoryObjectID = '%s' ORDER BY SortIndex", getSchema(), id));
			List<String> veIds = new ArrayList<>();
			for (Map<String, Object> stringObjectMap : result) {
				veIds.add((String) stringObjectMap.get("ObjectID"));
			}
			return veIds;
		} catch (SQLException e) {
			throw new GFrameworkException(e);
		}
	}
}