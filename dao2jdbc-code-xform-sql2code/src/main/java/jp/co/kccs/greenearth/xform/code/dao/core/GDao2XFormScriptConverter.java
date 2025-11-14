/*
 * Copyright 2024 Kyocera Communication Systems Co., Ltd All rights reserved.
 */
package jp.co.kccs.greenearth.xform.code.dao.core;

import jp.co.kccs.greenearth.commons.GFrameworkUtils;
import jp.co.kccs.greenearth.xform.code.jdbc.core.service.GXFormScript;
import jp.co.kccs.greenearth.xform.dao.dto.GDao2VirtualEntity;
import jp.co.kccs.greenearth.xform.jdbc.common.GCrudType;

/**
 * @author KCCS XXXXX
 * @create GEF_NEXT_DATE
 * @since GEF_NEXT_VERSION
 */
public interface GDao2XFormScriptConverter {

	GXFormScript convert(String sqlScript, GCrudType crudType, GDao2VirtualEntity virtualEntity);
	static GDao2XFormScriptConverter getInstance() {
		return GFrameworkUtils.getComponent(GDao2XFormScriptConverter.class);
	}
}
