package jp.co.kccs.greenearth.xform.dao.code.cmd;

public class GDao2XFormCodeCmdServiceMySqlTest extends GDao2XFormCodeCmdServiceTest {

	@Override
	protected String getDBType() {
		return "Mysql";
	}

	@Override
	protected String getSettingsFilePath() {
		return "inputFile/settings_mysql.yaml";
	}

	@Override
	protected String getForceAliasColumnConfigPath() {
		return "jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/settings_mysql_forceAliasColumn.yaml";
	}
	
	@Override
	protected String getTimestampCommand() {
		return "CURRENT_TIMESTAMP";
	}

	@Override
	protected String getUseForeignKeyFalseConfigPath() {
		return "jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/settings_mysql_useforeignkeyfalse.yaml";
	}

	@Override
	protected String getUseForeignKeyDefaultConfigPath() {
		return "jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/settings_mysql_useforeignkeydefault.yaml";
	}
}
