/*
 * Copyright 2024 Kyocera Communication Systems Co., Ltd All rights reserved.
 */
package jp.co.kccs.greenearth.xform.code.dao.core.service;

import jp.co.kccs.greenearth.framework.dao.db.*;
import jp.co.kccs.greenearth.xform.code.dao.core.GCrudType;
import jp.co.kccs.greenearth.xform.code.dao.core.sql.GVEMetadata2SqlExtractor;
import jp.co.kccs.greenearth.xform.dao.common.GVEItem;
import jp.co.kccs.greenearth.xform.code.dao.core.creation.GVeIdMetadataLoaderImpl;
import jp.co.kccs.greenearth.xform.code.dao.core.creation.GVeMetadataLoader;

/**
 * DaoからJDBCのSQL骨格を抽出するインターフェース<br/>
 * の実現クラス.<br/>
 * @create GEF_NEXT_DATE
 * @author KCSS yangfeng
 * @since GEF_NEXT_VERSION
 */
public class GDao2XFormCodeTransformImpl implements GDao2XFormCodeTransform {

	@Override
	public GDao2XFormSqlResult transformByVeId(String veId, GCrudType crudType) {
		GVEItem<GDBVirtualEntity> virtualEntity = convertVeIdParameterToVE(veId);
		String result = transformByVeIdAndCrudType(virtualEntity, crudType);
		return new GDao2XFormSqlResultImpl(result, crudType, virtualEntity.getData());
	}

	private String transformByVeIdAndCrudType(GVEItem<GDBVirtualEntity> virtualEntity, GCrudType crudType) {
		GVEMetadata2SqlExtractor sqlExtractor = GVEMetadata2SqlExtractor.getInstance();
		return sqlExtractor.extract(virtualEntity.getData(), crudType);
	}
	private GVEItem<GDBVirtualEntity> convertVeIdParameterToVE(String veId) {
		GVeMetadataLoader veMetadataLoader = new GVeIdMetadataLoaderImpl();
		return  (GVEItem<GDBVirtualEntity>) veMetadataLoader.create(veId);
	}


}
