package jp.co.kccs.greenearth.xform.dao.entity.mapper;

import jp.co.kccs.greenearth.xform.jdbc.common.GXFormEntity;

public interface GEntityMetadataMapper<I> {
	GXFormEntity map(I parameter);
}
