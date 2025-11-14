package jp.co.kccs.greenearth.xform.dao.integration;

public class MySqlCodeMainTest extends CodeMainTest {
	@Override
	protected String getCommonSettingPath()  {
		return "inputFile/settings_mysql.yaml";
	}

	@Override
	protected String getTimestampCommand() {
		return "CURRENT_TIMESTAMP";
	}

}
