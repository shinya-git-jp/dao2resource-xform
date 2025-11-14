/*
 * Copyright 2024 Kyocera Communication Systems Co., Ltd All rights reserved.
 */
package jp.co.kccs.greenearth.xform.code.dao.core.creation;

import jp.co.kccs.greenearth.framework.dao.db.GDBVirtualEntity;
import jp.co.kccs.greenearth.framework.dao.db.GDBVirtualEntityFactory;
import jp.co.kccs.greenearth.xform.dao.common.GVEItem;
import jp.co.kccs.greenearth.xform.dao.common.GVEItemImpl;
import jp.co.kccs.greenearth.xform.dao.common.GVENode;

/**
 * @author KCCS XXXXX
 * @create GEF_NEXT_DATE
 * @since GEF_NEXT_VERSION
 */
public class GVeIdMetadataLoaderImpl implements GVeMetadataLoader {
	@Override
	public GVENode<GDBVirtualEntity> create(String id) {
		GVEItem<GDBVirtualEntity> node = new GVEItemImpl<>();
		node.setId(id);
		node.setData(GDBVirtualEntityFactory.getVirtualEntity(id));
		return node;
	}
}
