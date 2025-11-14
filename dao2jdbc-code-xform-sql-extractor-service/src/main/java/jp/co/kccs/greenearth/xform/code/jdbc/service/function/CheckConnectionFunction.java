package jp.co.kccs.greenearth.xform.code.jdbc.service.function;

import jp.co.kccs.greenearth.commons.db.GConnectionManager;
import jp.co.kccs.greenearth.xform.code.jdbc.service.utility.Utils;
import jp.co.kccs.greenearth.xform.configuration.GDbCommonSetting;
import jp.co.kccs.greenearth.xform.configuration.GXFormSettingHolder;
import jp.co.kccs.greenearth.xform.old.common.GXFormOldUtils;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.SQLException;

@Component
public class CheckConnectionFunction {
	public String handle(Object input) {
		try {
			String key = GXFormOldUtils.getDbCommonSettingEncoded(GXFormSettingHolder.getSetting(GDbCommonSetting.class));
			Connection connection = GConnectionManager.getInstance().catchConnection(key);
			boolean result = connection.isValid(10);
			if (!result) {
				return "error";
			}
		} catch (SQLException e) {
			return Utils.createErrorResponse(e);
		}
		return "Success!";
	}
}
