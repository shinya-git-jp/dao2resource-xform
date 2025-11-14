package jp.co.kccs.greenearth.xform.dao.code.cmd;

import jp.co.kccs.greenearth.xform.code.jdbc.core.export.GExporterResult;
import jp.co.kccs.greenearth.xform.code.jdbc.core.service.GXFormCodeResult;
import jp.co.kccs.greenearth.xform.jdbc.common.GCrudType;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class GDao2XFormCodeCmdServiceOracleTest extends GDao2XFormCodeCmdServiceTest {
	
	@Override
	protected String getDBType() {
		return "Oracle";
	}
	
	@Override
	protected String getSettingsFilePath() {
		return "inputFile/settings_oracle.yaml";
	}

	@Override
	protected String getForceAliasColumnConfigPath() {
		return "jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/settings_oracle_forceAliasColumn.yaml";
	}

	@Override
	protected String getUseForeignKeyFalseConfigPath() {
		return "jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/settings_oracle_useforeignkeyfalse.yaml";
	}

	@Override
	protected String getUseForeignKeyDefaultConfigPath() {
		return "jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/settings_oracle_useforeignkeydefault.yaml";
	}

	@Override
	protected String getTimestampCommand() {
		return "SYSDATE";
	}
}
