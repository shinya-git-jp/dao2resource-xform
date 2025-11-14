/*
 * Copyright 2024 Kyocera Communication Systems Co., Ltd All rights reserved.
 */
package jp.co.kccs.greenearth.xform.dao.common;

import java.util.List;

/**
 * @author KCCS XXXXX
 * @create GEF_NEXT_DATE
 * @since GEF_NEXT_VERSION
 */
public interface GVECategory<T> extends GVENode {

	List<GVENode<T>> getChildren();
	void addChildren(GVENode<T> node);
	void deleteChildren(GVENode<T> node);
	int getSize();
}
