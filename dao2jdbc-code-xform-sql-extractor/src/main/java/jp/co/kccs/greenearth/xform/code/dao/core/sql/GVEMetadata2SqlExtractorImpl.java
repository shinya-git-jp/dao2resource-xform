package jp.co.kccs.greenearth.xform.code.dao.core.sql;

import jp.co.kccs.greenearth.framework.dao.db.GDBVirtualEntity;
import jp.co.kccs.greenearth.xform.code.dao.core.GCrudType;

import java.util.Map;


/**
 * @author KCCS XXXXX
 * @create GEF_NEXT_DATE
 * @since GEF_NEXT_VERSION
 */
public class GVEMetadata2SqlExtractorImpl implements GVEMetadata2SqlExtractor {
	private Map<GCrudType, GVEMetadata2SqlExtractorStrategy> strategyMap;
	public GVEMetadata2SqlExtractorImpl(Map<GCrudType, GVEMetadata2SqlExtractorStrategy> strategyMap) {
		this.strategyMap = strategyMap;
	}
	@Override
	public String extract(GDBVirtualEntity entity, GCrudType crudType) {
		GVEMetadata2SqlExtractorStrategy generatorStrategy = strategyMap.get(crudType);
		if (generatorStrategy.canGenerate(entity)) {
			return generatorStrategy.generate(entity.getObjectID());
		}
		return null;
	}

}
