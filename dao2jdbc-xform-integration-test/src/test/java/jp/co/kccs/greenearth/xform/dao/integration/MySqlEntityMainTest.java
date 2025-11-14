package jp.co.kccs.greenearth.xform.dao.integration;

public class MySqlEntityMainTest extends EntityMainTest {
	@Override
	protected String getCommonSettingPath()  {
		return "inputFile/settings_mysql.yaml";
	}
}
