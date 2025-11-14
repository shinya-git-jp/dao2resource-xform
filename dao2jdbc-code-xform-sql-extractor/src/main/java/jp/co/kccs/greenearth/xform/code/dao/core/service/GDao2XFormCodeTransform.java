/*
 * Copyright 2024 Kyocera Communication Systems Co., Ltd All rights reserved.
 */
package jp.co.kccs.greenearth.xform.code.dao.core.service;


import jp.co.kccs.greenearth.commons.GFrameworkUtils;
import jp.co.kccs.greenearth.xform.code.dao.core.GCrudType;

/**
 * DaoからJDBCのSQL骨格を抽出するインターフェース.<br/>
 *
 * @create GEF_NEXT_DATE
 * @author KCSS yangfeng
 * @since GEF_NEXT_VERSION
 */
public interface GDao2XFormCodeTransform {

	/**
	 * DaoからJDBCのSQL骨格を抽出する.<br/>
	 * 単一VeIdの場合.<br/>
	 *
	 * @create GEF_NEXT_DATE
	 * @author KCSS yangfeng
	 * @since GEF_NEXT_VERSION
	 *
	 * @param veId
	 * @return GDAO2JDBCCodeResult
	 */
	GDao2XFormSqlResult transformByVeId(String veId, GCrudType crudType);

	static GDao2XFormCodeTransform getInstance() {
		return GFrameworkUtils.getComponent(GDao2XFormCodeTransform.class.getName());
	}
}
