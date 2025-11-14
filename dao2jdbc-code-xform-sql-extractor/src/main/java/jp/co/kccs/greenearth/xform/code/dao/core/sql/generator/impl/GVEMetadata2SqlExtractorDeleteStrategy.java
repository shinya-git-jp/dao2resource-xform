package jp.co.kccs.greenearth.xform.code.dao.core.sql.generator.impl;

import jp.co.kccs.greenearth.framework.dao.db.GDBVirtualEntity;
import jp.co.kccs.greenearth.framework.dao.db.GDBVirtualEntityFactory;
import jp.co.kccs.greenearth.xform.code.dao.core.sql.GVEMetadata2SqlExtractorStrategy;

import static jp.co.kccs.greenearth.xform.dao.common.GDao2Utils.setParameterToGSearchParameter;

/**
 * @author KCCS XXXXX
 * @create GEF_NEXT_DATE
 * @since GEF_NEXT_VERSION
 */
public class GVEMetadata2SqlExtractorDeleteStrategy implements GVEMetadata2SqlExtractorStrategy {
	@Override
	public boolean canGenerate(GDBVirtualEntity virtualEntity) {
		return virtualEntity.getVeType().equals("1") || virtualEntity.getVeType().equals("0");
	}

	@Override
	public String generate(String veId) {
		GDBVirtualEntity deleteEntity = GDBVirtualEntityFactory.getVirtualEntity(veId);
		return deleteEntity.getDeleteSQL(setParameterToGSearchParameter(deleteEntity.createSearchParameter()));
	}

}
