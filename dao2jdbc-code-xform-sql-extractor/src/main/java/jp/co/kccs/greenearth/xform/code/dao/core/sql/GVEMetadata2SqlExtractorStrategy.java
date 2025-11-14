package jp.co.kccs.greenearth.xform.code.dao.core.sql;

import jp.co.kccs.greenearth.framework.dao.db.GDBVirtualEntity;

/**
 * @author KCCS XXXXX
 * @create GEF_NEXT_DATE
 * @since GEF_NEXT_VERSION
 */
public interface GVEMetadata2SqlExtractorStrategy {
	boolean canGenerate(GDBVirtualEntity virtualEntity);
	String generate(String veId);
}
