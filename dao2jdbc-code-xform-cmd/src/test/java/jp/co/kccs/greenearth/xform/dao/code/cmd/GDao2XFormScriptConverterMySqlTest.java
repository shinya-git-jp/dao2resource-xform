package jp.co.kccs.greenearth.xform.dao.code.cmd;

/**
 * @author KCCS XXXXX
 * @create GEF_NEXT_DATE
 * @since GEF_NEXT_VERSION
 */
public class GDao2XFormScriptConverterMySqlTest extends GDao2XFormScriptConverterTest {

	@Override
	protected String getSettingsFilePath() {
		return "inputFile/settings_mysql.yaml";
	}

	@Override
	protected String getTimestampCommand() {
		return "CURRENT_TIMESTAMP";
	}
}
