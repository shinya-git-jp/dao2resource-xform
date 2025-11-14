/*
 * Copyright 2024 Kyocera Communication Systems Co., Ltd All rights reserved.
 */
package jp.co.kccs.greenearth.xform.dao.common;

/**
 * SQL骨格を抽出する結果を格納するノードのインターフェース.<br/>
 * @create GEF_NEXT_DATE
 * @author KCSS yangfeng
 * @since GEF_NEXT_VERSION
 */
public interface GVENode<T> {

	/**
	 * IDを取得する.<br/>
	 * @create GEF_NEXT_DATE
	 * @author KCSS yangfeng
	 * @since GEF_NEXT_VERSION
	 *
	 * @return String ID
	 */
	String getId();

	void setId(String id);

}
