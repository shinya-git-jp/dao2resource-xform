package jp.co.kccs.greenearth.commons.db;

import jp.co.kccs.greenearth.xform.configuration.GDbCommonSetting;
import jp.co.kccs.greenearth.xform.configuration.GXFormSettingHolder;
import jp.co.kccs.greenearth.xform.old.common.GXFormOldUtils;

public class GXFormDataSourceConnectionManager extends GDataSourceConnectionManager {

	@Override
	protected String getMainDataSourceName() {
		return GXFormOldUtils.getDbCommonSettingEncoded(GXFormSettingHolder.getSetting(GDbCommonSetting.class));
	}
}
