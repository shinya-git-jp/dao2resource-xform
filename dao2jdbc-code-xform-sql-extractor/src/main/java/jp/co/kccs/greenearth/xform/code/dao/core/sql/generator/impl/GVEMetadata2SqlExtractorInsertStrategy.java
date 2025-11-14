package jp.co.kccs.greenearth.xform.code.dao.core.sql.generator.impl;

import jp.co.kccs.greenearth.framework.dao.GDAOException;
import jp.co.kccs.greenearth.framework.dao.db.GDBVirtualEntity;
import jp.co.kccs.greenearth.framework.dao.db.GDBVirtualEntityFactory;
import jp.co.kccs.greenearth.xform.code.dao.core.sql.GVEMetadata2SqlExtractorStrategy;

/**
 * @author KCCS XXXXX
 * @create GEF_NEXT_DATE
 * @since GEF_NEXT_VERSION
 */
public class GVEMetadata2SqlExtractorInsertStrategy implements GVEMetadata2SqlExtractorStrategy {

	@Override
	public boolean canGenerate(GDBVirtualEntity virtualEntity) {
		return virtualEntity.getVeType().equals("1");
	}

	@Override
	public String generate(String veId) {
		GDBVirtualEntity insertEntity = null;
		try {
			insertEntity = GDBVirtualEntityFactory.getVirtualEntity(veId);
		} catch (GDAOException e) {
			throw new RuntimeException(e);
		}

		return insertEntity.getInsertSQL();
	}

}
