package jp.co.kccs.greenearth.xform.code.dao.core.sql.generator.impl;

import jp.co.kccs.greenearth.framework.dao.db.GDBVirtualEntity;
import jp.co.kccs.greenearth.framework.dao.db.GDBVirtualEntityFactory;
import jp.co.kccs.greenearth.framework.dao.db.data.GDBRecord;
import jp.co.kccs.greenearth.xform.code.dao.core.sql.GVEMetadata2SqlExtractorStrategy;

import static jp.co.kccs.greenearth.xform.dao.common.GDao2Utils.setParameterToGSearchParameter;

/**
 * @author KCCS XXXXX
 * @create GEF_NEXT_DATE
 * @since GEF_NEXT_VERSION
 */
public class GVEMetadata2SqlExtractorUpdateStrategy implements GVEMetadata2SqlExtractorStrategy {

	@Override
	public boolean canGenerate(GDBVirtualEntity virtualEntity) {
		return virtualEntity.getVeType().equals("1");
	}

	@Override
	public String generate(String veId) {
		GDBVirtualEntity updateEntity = GDBVirtualEntityFactory.getVirtualEntity(veId);
		GDBRecord record = updateEntity.createRecord();
		for (int i = 0; i < updateEntity.getDBVirtualColumnsAtMainEntity().size(); i++) {
			record.setValue(updateEntity.getDBVirtualColumnsAtMainEntity().get(i).getVirtualColumnName(), null);
		}
		return updateEntity.getUpdateSQL(record, setParameterToGSearchParameter(updateEntity.createSearchParameter()));
	}
}
