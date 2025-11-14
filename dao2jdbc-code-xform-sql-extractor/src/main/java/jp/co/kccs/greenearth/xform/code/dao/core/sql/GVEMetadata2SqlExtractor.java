package jp.co.kccs.greenearth.xform.code.dao.core.sql;

import jp.co.kccs.greenearth.commons.GFrameworkUtils;
import jp.co.kccs.greenearth.framework.dao.db.GDBVirtualEntity;
import jp.co.kccs.greenearth.xform.code.dao.core.GCrudType;

/**
 * @author KCCS XXXXX
 * @create GEF_NEXT_DATE
 * @since GEF_NEXT_VERSION
 */
public interface GVEMetadata2SqlExtractor {

	String extract(GDBVirtualEntity entity, GCrudType crudType);
	static GVEMetadata2SqlExtractor getInstance() {
		return GFrameworkUtils.getComponent(GVEMetadata2SqlExtractor.class.getName());
	}
}
