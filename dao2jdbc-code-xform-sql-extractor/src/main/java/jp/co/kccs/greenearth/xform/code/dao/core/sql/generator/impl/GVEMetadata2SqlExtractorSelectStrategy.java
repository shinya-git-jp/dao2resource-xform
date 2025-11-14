package jp.co.kccs.greenearth.xform.code.dao.core.sql.generator.impl;

import jp.co.kccs.greenearth.framework.dao.GDAOException;
import jp.co.kccs.greenearth.framework.dao.db.GDBVirtualColumn;
import jp.co.kccs.greenearth.framework.dao.db.GDBVirtualEntity;
import jp.co.kccs.greenearth.framework.dao.db.GDBVirtualEntityFactory;
import jp.co.kccs.greenearth.framework.dao.db.GSearchParameter;
import jp.co.kccs.greenearth.framework.dao.db.data.GBinary;
import jp.co.kccs.greenearth.xform.code.dao.core.sql.GVEMetadata2SqlExtractorStrategy;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author KCCS XXXXX
 * @create GEF_NEXT_DATE
 * @since GEF_NEXT_VERSION
 */
public class GVEMetadata2SqlExtractorSelectStrategy implements GVEMetadata2SqlExtractorStrategy {

	@Override
	public boolean canGenerate(GDBVirtualEntity virtualEntity) {
		return virtualEntity.getVeType().equals("0") || virtualEntity.getVeType().equals("1");
	}

	@Override
	public String generate(String veId) {
		GDBVirtualEntity selectEntity = null;
		try {
			selectEntity = GDBVirtualEntityFactory.getVirtualEntity(veId);
		} catch (GDAOException e) {
			throw new RuntimeException(e);
		}
		GSearchParameter searchParameter = selectEntity.createSearchParameter();
		setDataToGSearchParameter(searchParameter);
		return selectEntity.getSelectSQL(searchParameter);
	}

	private void setDataToGSearchParameter(GSearchParameter parameter) {
		for (int i = 0; i < parameter.getConditionColumnSize(); i++) {
			GDBVirtualColumn dbVirtualColumn = parameter.getSearchCondition().getConditionColumn(i).getDBVirtualColumn();
			if (dbVirtualColumn.isTypeString()) {
				parameter.set(i, "");
			}
			if (dbVirtualColumn.isTypeInteger()) {
				parameter.set(i, 0);
			}
			if (dbVirtualColumn.isTypeDate()) {
				parameter.set(i, new Date());
			}
			if (dbVirtualColumn.isTypeDecimal()) {
				parameter.set(i, new BigDecimal(0));
			}
			if (dbVirtualColumn.isTypeNumber()) {
				parameter.set(i, 0f);
			}
			if (dbVirtualColumn.isTypeLong()) {
				parameter.set(i, 0L);
			}
			if (dbVirtualColumn.isTypeBinary()) {
				parameter.set(i, new GBinary());
			}
		}
	}
}
