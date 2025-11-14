/*
 * Copyright 2024 Kyocera Communication Systems Co., Ltd All rights reserved.
 */
package jp.co.kccs.greenearth.xform.dao.code.cmd;


import jp.co.kccs.greenearth.xform.code.dao.core.GCrudType;

import java.util.List;

/**
 * DaoからJDBCのSQL骨格を抽出する用パラメータのインターフェース.<br/>
 *
 * @create GEF_NEXT_DATE
 * @author KCSS yangfeng
 * @since GEF_NEXT_VERSION
 */
public interface GDao2XFormCodeParameter {

	/**
	 * IDを取得する.<br/>
	 *
	 * @create GEF_NEXT_DATE
	 * @author KCSS yangfeng
	 * @since GEF_NEXT_VERSION
	 */
	String getId();

	/**
	 * Crud操作のリストを取得する.<br/>
	 *
	 * @create GEF_NEXT_DATE
	 * @author KCSS yangfeng
	 * @since GEF_NEXT_VERSION
	 */
	List<GCrudType> getCrudTypes();
	boolean isCategory();
	void setId(String id);
	void setCategoryFlag(boolean categoryFlag);
	void setCrudTypes(List<GCrudType> crudTypes);
}
