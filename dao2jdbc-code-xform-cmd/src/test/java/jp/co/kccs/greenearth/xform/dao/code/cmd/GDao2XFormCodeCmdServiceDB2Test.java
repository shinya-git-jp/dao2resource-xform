package jp.co.kccs.greenearth.xform.dao.code.cmd;

import jp.co.kccs.greenearth.xform.code.dao.core.GDao2JdbcXFormCodeResult;
import jp.co.kccs.greenearth.xform.code.jdbc.core.GCompoundApiImpl;
import jp.co.kccs.greenearth.xform.code.jdbc.core.GTerminatorApi;
import jp.co.kccs.greenearth.xform.code.jdbc.core.GWhereApi;
import jp.co.kccs.greenearth.xform.code.jdbc.core.export.GExporterResult;
import jp.co.kccs.greenearth.xform.code.jdbc.core.service.GXFormCodeResult;
import jp.co.kccs.greenearth.xform.dao.dto.GDao2VirtualEntity;
import jp.co.kccs.greenearth.xform.jdbc.common.GCrudType;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class GDao2XFormCodeCmdServiceDB2Test extends GDao2XFormCodeCmdServiceTest {

	@Override
	protected String getDBType() {
		return "DB2";
	}
	
	@Override
	protected String getSettingsFilePath() {
		return "inputFile/settings_db2.yaml";
	}

	@Override
	protected String getForceAliasColumnConfigPath() {
		return "jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/settings_db2_forceAliasColumn.yaml";
	}

	@Override
	protected String getUseForeignKeyFalseConfigPath() {
		return "jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/settings_db2_useforeignkeyfalse.yaml";
	}

	@Override
	protected String getUseForeignKeyDefaultConfigPath() {
		return "jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/settings_db2_useforeignkeydefault.yaml";
	}

	@Override
	public String getDeleteSql_WherePartsPK_reg_delete() {
		return "DELETE FROM" + System.lineSeparator() +
				"    " + dbSchema + ".jdbc_tool_test_pk_uk_mainmaster jdbc_tool_test_pk_uk_mainmaster" + System.lineSeparator() +
				"WHERE" + System.lineSeparator() +
				"    jdbc_tool_test_pk_uk_mainmaster.objectID = ?";
	}

	@Override
	public String getDeleteSql_wherePartsUK_reg_delete() {
		return "DELETE FROM" + System.lineSeparator() +
				"    " + dbSchema + ".jdbc_tool_test_pk_uk_mainmaster jdbc_tool_test_pk_uk_mainmaster" + System.lineSeparator() +
				"WHERE" + System.lineSeparator() +
				"    (" + System.lineSeparator() +
				"        jdbc_tool_test_pk_uk_mainmaster.ItemCD = ?" + System.lineSeparator() +
				"        AND jdbc_tool_test_pk_uk_mainmaster.ItemNA = ?" + System.lineSeparator() +
				"    )";
	}

	@Override
	public String getDeleteSql_wherePartsPKAndUK_reg_delete() {
		return "DELETE FROM" + System.lineSeparator() +
				"    " + dbSchema + ".jdbc_tool_test_pk_uk_mainmaster jdbc_tool_test_pk_uk_mainmaster" + System.lineSeparator() +
				"WHERE" + System.lineSeparator() +
				"    (" + System.lineSeparator() +
				"        jdbc_tool_test_pk_uk_mainmaster.ItemCD = ?" + System.lineSeparator() +
				"        AND jdbc_tool_test_pk_uk_mainmaster.ItemNA = ?" + System.lineSeparator() +
				"        AND jdbc_tool_test_pk_uk_mainmaster.objectID = ?" + System.lineSeparator() +
				"    )";
	}

	@Override
	public String getDeleteSql_wherePartsNotCondition_reg_delete() {
		return "DELETE FROM" + System.lineSeparator() +
				"    " + dbSchema + ".jdbc_tool_test_pk_uk_mainmaster jdbc_tool_test_pk_uk_mainmaster";
	}

	@Override
	public String getDeleteSql_wherePartsNotPKOrUK_reg_delete() {
		return "DELETE FROM" + System.lineSeparator() +
				"    " + dbSchema + ".jdbc_tool_test_pk_uk_mainmaster jdbc_tool_test_pk_uk_mainmaster" + System.lineSeparator() +
				"WHERE" + System.lineSeparator() +
				"    jdbc_tool_test_pk_uk_mainmaster.UnitPrice = ?";
	}

	@Override
	public String getDeleteSql_searchExpWherePartsLikeF_reg_delete() {
		return "DELETE FROM" + System.lineSeparator() +
				"    " + dbSchema + ".jdbc_tool_test_mainmaster jdbc_tool_test_mainmaster" + System.lineSeparator() +
				"WHERE" + System.lineSeparator() +
				"    jdbc_tool_test_mainmaster.ItemCD LIKE ? ESCAPE '/'";
	}

	@Override
	public String getDeleteSql_searchExpWherePartsLikeFB_reg_delete() {
		return "DELETE FROM" + System.lineSeparator() +
				"    " + dbSchema + ".jdbc_tool_test_unitmaster jdbc_tool_test_unitmaster" + System.lineSeparator() +
				"WHERE" + System.lineSeparator() +
				"    jdbc_tool_test_unitmaster.Name LIKE ? ESCAPE '/'";
	}

	@Override
	public String getDeleteSql_searchExpWherePartsLikeB_reg_delete() {
		return "DELETE FROM" + System.lineSeparator() +
				"    " + dbSchema + ".jdbc_tool_test_ordermaster jdbc_tool_test_ordermaster" + System.lineSeparator() +
				"WHERE" + System.lineSeparator() +
				"    jdbc_tool_test_ordermaster.CorporateNA LIKE ? ESCAPE '/'";
	}

	@Override
	public String getDeleteSql_searchExpWherePartsEquals_reg_delete() {
		return "DELETE FROM" + System.lineSeparator() +
				"    " + dbSchema + ".jdbc_tool_test_mainmaster jdbc_tool_test_mainmaster" + System.lineSeparator() +
				"WHERE" + System.lineSeparator() +
				"    (" + System.lineSeparator() +
				"        jdbc_tool_test_mainmaster.ItemCD = ?" + System.lineSeparator() +
				"        AND jdbc_tool_test_mainmaster.UnitPrice <> ?" + System.lineSeparator() +
				"    )";
	}

	@Override
	public String getDeleteSql_searchExpWherePartsCompare_reg_delete() {
		return "DELETE FROM" + System.lineSeparator() +
				"    " + dbSchema + ".jdbc_tool_test_mainmaster jdbc_tool_test_mainmaster" + System.lineSeparator() +
				"WHERE" + System.lineSeparator() +
				"    (" + System.lineSeparator() +
				"        jdbc_tool_test_mainmaster.UnitPrice > ?" + System.lineSeparator() +
				"        AND jdbc_tool_test_mainmaster.SellPrice < ?" + System.lineSeparator() +
				"        AND jdbc_tool_test_mainmaster.Flag <= ?" + System.lineSeparator() +
				"        AND jdbc_tool_test_mainmaster.PriceFlag >= ?" + System.lineSeparator() +
				"    )";
	}

	@Override
	public String getDeleteSql_searchExpWherePartsOptionTrim_reg_delete() {
		return "DELETE FROM" + System.lineSeparator() +
				"    " + dbSchema + ".jdbc_tool_test_mainmaster jdbc_tool_test_mainmaster" + System.lineSeparator() +
				"WHERE" + System.lineSeparator() +
				"    jdbc_tool_test_mainmaster.UnitPrice IN (100, 101, 102)" + System.lineSeparator() +
				"    AND (" + System.lineSeparator() +
				"        jdbc_tool_test_mainmaster.objectID LIKE ? ESCAPE '/'" + System.lineSeparator() +
				"        AND jdbc_tool_test_mainmaster.UnitPrice >= ?" + System.lineSeparator() +
				"    )";
	}

	@Override
	public String getDeleteSql_filterExpWherePartsLikeF_reg_delete() {
		return "DELETE FROM" + System.lineSeparator() +
				"    " + dbSchema + ".jdbc_tool_test_mainmaster jdbc_tool_test_mainmaster" + System.lineSeparator() +
				"WHERE" + System.lineSeparator() +
				"    jdbc_tool_test_mainmaster.ItemCD LIKE 'item%'";
	}

	@Override
	public String getDeleteSql_filterExpWherePartsLikeFB_reg_delete() {
		return "DELETE FROM" + System.lineSeparator() +
				"    " + dbSchema + ".jdbc_tool_test_unitmaster jdbc_tool_test_unitmaster" + System.lineSeparator() +
				"WHERE" + System.lineSeparator() +
				"    jdbc_tool_test_unitmaster.Name LIKE '%name%'";
	}

	@Override
	public String getDeleteSql_filterExpWherePartsLikeB_reg_delete() {
		return "DELETE FROM" + System.lineSeparator() +
				"    " + dbSchema + ".jdbc_tool_test_ordermaster jdbc_tool_test_ordermaster" + System.lineSeparator() +
				"WHERE" + System.lineSeparator() +
				"    jdbc_tool_test_ordermaster.CorporateNA LIKE '%na'";
	}

	@Override
	public String getDeleteSql_filterExpWherePartsEquals_reg_delete() {
		return "DELETE FROM" + System.lineSeparator() +
				"    " + dbSchema + ".jdbc_tool_test_mainmaster jdbc_tool_test_mainmaster" + System.lineSeparator() +
				"WHERE" + System.lineSeparator() +
				"    (" + System.lineSeparator() +
				"        jdbc_tool_test_mainmaster.objectID = '1001'" + System.lineSeparator() +
				"        AND jdbc_tool_test_mainmaster.ItemCD <> '9001'" + System.lineSeparator() +
				"    )";
	}

	@Override
	public String getDeleteSql_filterExpWherePartsCompare_reg_delete() {
		return "DELETE FROM" + System.lineSeparator() +
				"    " + dbSchema + ".jdbc_tool_test_mainmaster jdbc_tool_test_mainmaster" + System.lineSeparator() +
				"WHERE" + System.lineSeparator() +
				"    (" + System.lineSeparator() +
				"        jdbc_tool_test_mainmaster.UnitPrice > 100" + System.lineSeparator() +
				"        AND jdbc_tool_test_mainmaster.UnitPrice < 500" + System.lineSeparator() +
				"        AND jdbc_tool_test_mainmaster.SellPrice <= 1000" + System.lineSeparator() +
				"        AND jdbc_tool_test_mainmaster.SellPrice >= 300" + System.lineSeparator() +
				"    )";
	}

	@Override
	public String getDeleteSql_filterExpWherePartsNull_reg_delete() {
		return "DELETE FROM" + System.lineSeparator() +
				"    " + dbSchema + ".jdbc_tool_test_mainmaster jdbc_tool_test_mainmaster" + System.lineSeparator() +
				"WHERE" + System.lineSeparator() +
				"    (" + System.lineSeparator() +
				"        jdbc_tool_test_mainmaster.ItemCD IS NULL" + System.lineSeparator() +
				"        AND jdbc_tool_test_mainmaster.UnitPrice IS NOT NULL" + System.lineSeparator() +
				"    )";
	}

	@Override
	public String getDeleteSql_filterExpWherePartsIn_reg_delete() {
		return "DELETE FROM" + System.lineSeparator() +
				"    " + dbSchema + ".jdbc_tool_test_mainmaster jdbc_tool_test_mainmaster" + System.lineSeparator() +
				"WHERE" + System.lineSeparator() +
				"    (" + System.lineSeparator() +
				"        jdbc_tool_test_mainmaster.ItemCD IN ('item010', '1001')" + System.lineSeparator() +
				"        AND jdbc_tool_test_mainmaster.UnitPrice NOT IN (10, 20, 30)" + System.lineSeparator() +
				"    )";
	}

	@Override
	protected String getTimestampCommand() {
		return "CURRENT TIMESTAMP";
	}

	@Override
	public String getDeleteSql_veType_inq() {
		return "DELETE FROM" + System.lineSeparator() +
				"    " + dbSchema + ".jdbc_tool_test_mainmaster jdbc_tool_test_mainmaster" + System.lineSeparator() +
				"WHERE" + System.lineSeparator() +
				"    (" + System.lineSeparator() +
				"        jdbc_tool_test_mainmaster.ItemCD = ?" + System.lineSeparator() +
				"        AND jdbc_tool_test_mainmaster.UnitPrice <> ?" + System.lineSeparator() +
				"    )";
	}

	@Override
	public String getDeleteSql_emptyColumn() {
		return "DELETE FROM" + System.lineSeparator() +
				"    " + dbSchema + ".jdbc_tool_test_emptyColumn jdbc_tool_test_emptyColumn";
	}

	@Override
	public String getDeleteSql_fullCase() {
		return "DELETE FROM" + System.lineSeparator() +
				"    " + dbSchema + ".jdbc_tool_test_fullCaseMainEntity jdbc_tool_test_fullCaseMainEntity" + System.lineSeparator() +
				"WHERE" + System.lineSeparator() +
				"    (" + System.lineSeparator() +
				"        jdbc_tool_test_fullCaseMainEntity.StringColumn = '9001'" + System.lineSeparator() +
				"        AND jdbc_tool_test_fullCaseMainEntity.IntColumn = 8001" + System.lineSeparator() +
				"    )" + System.lineSeparator() +
				"    AND (" + System.lineSeparator() +
				"        jdbc_tool_test_fullCaseMainEntity.NStringColumn = ?" + System.lineSeparator() +
				"        AND jdbc_tool_test_fullCaseMainEntity.DateTimeColumn = ?" + System.lineSeparator() +
				"        AND jdbc_tool_test_fullCaseMainEntity.CompanyCD = ?" + System.lineSeparator() +
				"    )";
	}

	@Override
	public String getDeleteSql_multipleUK() {
		return "DELETE FROM" + System.lineSeparator() +
				"    " + dbSchema + ".jdbc_tool_test_multipleUK jdbc_tool_test_multipleUK" + System.lineSeparator() +
				"WHERE" + System.lineSeparator() +
				"    (" + System.lineSeparator() +
				"        jdbc_tool_test_multipleUK.objectID = ?" + System.lineSeparator() +
				"        AND jdbc_tool_test_multipleUK.NStringColumn = ?" + System.lineSeparator() +
				"    )";
	}

	@Override
	public String getDeleteSql_noPrimaryKey() {
		return "DELETE FROM" + System.lineSeparator() +
				"    " + dbSchema + ".jdbc_tool_test_noPrimaryKey jdbc_tool_test_noPrimaryKey" + System.lineSeparator() +
				"WHERE" + System.lineSeparator() +
				"    jdbc_tool_test_noPrimaryKey.StringColumn = ?";
	}

	@Override
	public String getDeleteSql_noUnionKey() {
		return "DELETE FROM" + System.lineSeparator() +
				"    " + dbSchema + ".jdbc_tool_test_noUnionKey jdbc_tool_test_noUnionKey" + System.lineSeparator() +
				"WHERE" + System.lineSeparator() +
				"    jdbc_tool_test_noUnionKey.StringColumn = ?";
	}

	@Override
	public String getDeleteSql_noPrimaryKeyAndUnionKey() {
		return "DELETE FROM" + System.lineSeparator() +
				"    " + dbSchema + ".jdbc_tool_test_noPrimaryKeyAndUnionKey jdbc_tool_test_noPrimaryKeyAndUnionKey" + System.lineSeparator() +
				"WHERE" + System.lineSeparator() +
				"    jdbc_tool_test_noPrimaryKeyAndUnionKey.StringColumn = ?";
	}

	public void testExport_join_mix_useForeignKeyFalse() {
		doCustomConfig(getUseForeignKeyFalseConfigPath());
		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_join_mix.xml");
		String conditionFilePath = getConditionFilePath("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_join_mix.yaml");
		GExporterResult<List<GXFormCodeResult>, byte[]> result = service.export(conditionFilePath, settingsFilePath);

		assertEquals("html", result.getMediaType());
		assertEquals(1, result.getData().size());
		assertEquals(GCrudType.SELECT, result.getData().get(0).getType());
		assertEquals("仮想表ID: E44ADEF7B30549449EDF193ECFB66631", result.getData().get(0).getDescription());
		assertTrue(assertSqlWithJoin("SELECT" + System.lineSeparator() +
				"    jdbc_tool_test_pk_uk_mainmaster.objectID," + System.lineSeparator() +
				"    jdbc_tool_test_pk_uk_mainmaster.ItemCD," + System.lineSeparator() +
				"    jdbc_tool_test_pk_uk_mainmaster.ItemNA," + System.lineSeparator() +
				"    jdbc_tool_test_pk_uk_mainmaster.UnitPrice," + System.lineSeparator() +
				"    jdbc_tool_test_pk_uk_mainmaster.SellPrice," + System.lineSeparator() +
				"    jdbc_tool_test_pk_uk_mainmaster.Flag," + System.lineSeparator() +
				"    jdbc_tool_test_pk_uk_mainmaster.PriceFlag," + System.lineSeparator() +
				"    jdbc_tool_test_pk_uk_mainmaster.ExclusiveFG," + System.lineSeparator() +
				"    jdbc_tool_test_pk_uk_mainmaster.RegisteredDT," + System.lineSeparator() +
				"    jdbc_tool_test_pk_uk_mainmaster.UpdatedDT" + System.lineSeparator() +
				"FROM" + System.lineSeparator() +
				"    " + dbSchema + ".jdbc_tool_test_pk_uk_mainmaster jdbc_tool_test_pk_uk_mainmaster" + System.lineSeparator() +
				"${join}"+
				"WHERE" + System.lineSeparator() +
				"    jdbc_tool_test_pk_uk_mainmaster.objectID = ?",
		List.of("    LEFT OUTER JOIN " + dbSchema + ".jdbc_tool_test_unitmaster jdbc_tool_test_unitmaster ON jdbc_tool_test_pk_uk_mainmaster.objectID = jdbc_tool_test_unitmaster.Code" + System.lineSeparator(),
				"    INNER JOIN " + dbSchema + ".jdbc_tool_test_mainmaster jdbc_tool_test_mainmaster ON jdbc_tool_test_pk_uk_mainmaster.objectID = jdbc_tool_test_mainmaster.objectID" + System.lineSeparator(),
				"    RIGHT OUTER JOIN " + dbSchema + ".jdbc_tool_test_ordermaster jdbc_tool_test_ordermaster ON jdbc_tool_test_pk_uk_mainmaster.ItemCD = jdbc_tool_test_ordermaster.objectID" + System.lineSeparator()),
		result.getData().get(0).getSqlScript()));

		String whereParamsParts = "Object[] whereParams = new Object[]{};" + System.lineSeparator();
		String groupByApiStr = "select(\"jdbc_tool_test_pk_uk_mainmaster\", \"jdbc_tool_test_pk_uk_mainmaster\")" + System.lineSeparator() +
				".fields(colsAll())" + System.lineSeparator() +
				"${join}" +
				".where(exp($(\"jdbc_tool_test_pk_uk_mainmaster.objectID = ?\"), whereParams))";
		assertTrue(assertSqlWithJoin(
			whereParamsParts + "List<GRecord> result = " + groupByApiStr + System.lineSeparator() + ".findList();",
				List.of(".leftOuterJoin(\"jdbc_tool_test_unitmaster\", \"jdbc_tool_test_unitmaster\", exp($(\"jdbc_tool_test_pk_uk_mainmaster.objectID = jdbc_tool_test_unitmaster.Code\")))" + System.lineSeparator(),
						".innerJoin(\"jdbc_tool_test_mainmaster\", \"jdbc_tool_test_mainmaster\", exp($(\"jdbc_tool_test_pk_uk_mainmaster.objectID = jdbc_tool_test_mainmaster.objectID\")))" + System.lineSeparator(),
						".rightOuterJoin(\"jdbc_tool_test_ordermaster\", \"jdbc_tool_test_ordermaster\", exp($(\"jdbc_tool_test_pk_uk_mainmaster.ItemCD = jdbc_tool_test_ordermaster.objectID\")))" + System.lineSeparator()
				),
				result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE))
		));
		assertTrue(assertSqlWithJoin(
				whereParamsParts + "GRecord result = " + groupByApiStr + System.lineSeparator() + ".findRecord();",
				List.of(".leftOuterJoin(\"jdbc_tool_test_unitmaster\", \"jdbc_tool_test_unitmaster\", exp($(\"jdbc_tool_test_pk_uk_mainmaster.objectID = jdbc_tool_test_unitmaster.Code\")))" + System.lineSeparator(),
						".innerJoin(\"jdbc_tool_test_mainmaster\", \"jdbc_tool_test_mainmaster\", exp($(\"jdbc_tool_test_pk_uk_mainmaster.objectID = jdbc_tool_test_mainmaster.objectID\")))" + System.lineSeparator(),
						".rightOuterJoin(\"jdbc_tool_test_ordermaster\", \"jdbc_tool_test_ordermaster\", exp($(\"jdbc_tool_test_pk_uk_mainmaster.ItemCD = jdbc_tool_test_ordermaster.objectID\")))" + System.lineSeparator()
				),
				result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE))
		));
		assertTrue(assertSqlWithJoin(
				whereParamsParts + "GRecordSet result = " + groupByApiStr + System.lineSeparator() + ".findRecordSet();",
				List.of(".leftOuterJoin(\"jdbc_tool_test_unitmaster\", \"jdbc_tool_test_unitmaster\", exp($(\"jdbc_tool_test_pk_uk_mainmaster.objectID = jdbc_tool_test_unitmaster.Code\")))" + System.lineSeparator(),
						".innerJoin(\"jdbc_tool_test_mainmaster\", \"jdbc_tool_test_mainmaster\", exp($(\"jdbc_tool_test_pk_uk_mainmaster.objectID = jdbc_tool_test_mainmaster.objectID\")))" + System.lineSeparator(),
						".rightOuterJoin(\"jdbc_tool_test_ordermaster\", \"jdbc_tool_test_ordermaster\", exp($(\"jdbc_tool_test_pk_uk_mainmaster.ItemCD = jdbc_tool_test_ordermaster.objectID\")))" + System.lineSeparator()
				),
				result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE))
		));
	}

	@Test
	public void testExport_join_mix() {

		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_join_mix.xml");
		String conditionFilePath = getConditionFilePath("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_join_mix.yaml");
		GExporterResult<List<GXFormCodeResult>, byte[]> result = service.export(conditionFilePath, settingsFilePath);

		assertEquals("html", result.getMediaType());
		assertEquals(1, result.getData().size());
		assertEquals(GCrudType.SELECT, result.getData().get(0).getType());
		assertEquals("仮想表ID: E44ADEF7B30549449EDF193ECFB66631", result.getData().get(0).getDescription());
		assertTrue(assertSqlWithJoin("SELECT" + System.lineSeparator() +
				"    jdbc_tool_test_pk_uk_mainmaster.objectID," + System.lineSeparator() +
				"    jdbc_tool_test_pk_uk_mainmaster.ItemCD," + System.lineSeparator() +
				"    jdbc_tool_test_pk_uk_mainmaster.ItemNA," + System.lineSeparator() +
				"    jdbc_tool_test_pk_uk_mainmaster.UnitPrice," + System.lineSeparator() +
				"    jdbc_tool_test_pk_uk_mainmaster.SellPrice," + System.lineSeparator() +
				"    jdbc_tool_test_pk_uk_mainmaster.Flag," + System.lineSeparator() +
				"    jdbc_tool_test_pk_uk_mainmaster.PriceFlag," + System.lineSeparator() +
				"    jdbc_tool_test_pk_uk_mainmaster.ExclusiveFG," + System.lineSeparator() +
				"    jdbc_tool_test_pk_uk_mainmaster.RegisteredDT," + System.lineSeparator() +
				"    jdbc_tool_test_pk_uk_mainmaster.UpdatedDT" + System.lineSeparator() +
				"FROM" + System.lineSeparator() +
				"    " + dbSchema + ".jdbc_tool_test_pk_uk_mainmaster jdbc_tool_test_pk_uk_mainmaster" + System.lineSeparator() +
				"${join}" +
				"WHERE" + System.lineSeparator() +
				"    jdbc_tool_test_pk_uk_mainmaster.objectID = ?",
				List.of("    LEFT OUTER JOIN " + dbSchema + ".jdbc_tool_test_unitmaster jdbc_tool_test_unitmaster ON jdbc_tool_test_pk_uk_mainmaster.objectID = jdbc_tool_test_unitmaster.Code" + System.lineSeparator(),
						"    INNER JOIN " + dbSchema + ".jdbc_tool_test_mainmaster jdbc_tool_test_mainmaster ON jdbc_tool_test_pk_uk_mainmaster.objectID = jdbc_tool_test_mainmaster.objectID" + System.lineSeparator(),
						"    RIGHT OUTER JOIN " + dbSchema + ".jdbc_tool_test_ordermaster jdbc_tool_test_ordermaster ON jdbc_tool_test_pk_uk_mainmaster.ItemCD = jdbc_tool_test_ordermaster.objectID" + System.lineSeparator()
				),
				result.getData().get(0).getSqlScript()));

		String whereParamsParts = "Map<String, Object> whereParams = new HashMap<>();" + System.lineSeparator() +
				"whereParams.put(\"jdbc_tool_test_pk_uk_mainmaster.objectID\",);" + System.lineSeparator();
		String groupByApiStr = "select(\"jdbc_tool_test_pk_uk_mainmaster\", \"jdbc_tool_test_pk_uk_mainmaster\")" + System.lineSeparator() +
				".fields(colsAll())" + System.lineSeparator() +
				"${join}" +
				".where(expMap($(\"jdbc_tool_test_pk_uk_mainmaster.objectID = :jdbc_tool_test_pk_uk_mainmaster.objectID\"), whereParams))";
		List<String> joins = List.of(".leftOuterJoinFK(\"FK_UnitMaster\", \"jdbc_tool_test_unitmaster\")" + System.lineSeparator(),
				".innerJoinFK(\"FK_MainMaster\", \"jdbc_tool_test_mainmaster\")" + System.lineSeparator(),
				".rightOuterJoinFK(\"FK_OrderMaster\", \"jdbc_tool_test_ordermaster\")" + System.lineSeparator());
		assertTrue(assertSqlWithJoin(whereParamsParts + "List<GRecord> result = " + groupByApiStr + System.lineSeparator() + ".findList();"
				, joins,
				result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE))));

		assertTrue(assertSqlWithJoin(whereParamsParts + "GRecord result = " + groupByApiStr + System.lineSeparator() + ".findRecord();",
				joins, result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE))));
		assertTrue(assertSqlWithJoin(whereParamsParts + "GRecordSet result = " + groupByApiStr + System.lineSeparator() + ".findRecordSet();",
				joins, result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE))));
	}

	@Test
	public void testExport_searchExpWhereParts_JoinColumn() {

		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_searchExpWhereParts_JoinColumn.xml");
		String conditionFilePath = getConditionFilePath("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_searchExpWhereparts_joincolumn.yaml");
		GExporterResult<List<GXFormCodeResult>, byte[]> result = service.export(conditionFilePath, settingsFilePath);

		assertEquals("html", result.getMediaType());
		assertEquals(1, result.getData().size());
		assertEquals(GCrudType.SELECT, result.getData().get(0).getType());
		assertEquals("仮想表ID: 0B064A3729EC4D65BB5A663562B96B51", result.getData().get(0).getDescription());

		assertTrue(assertSqlWithJoin("SELECT" + System.lineSeparator() +
						"    jdbc_tool_test_pk_uk_mainmaster.objectID," + System.lineSeparator() +
						"    jdbc_tool_test_pk_uk_mainmaster.ItemCD," + System.lineSeparator() +
						"    jdbc_tool_test_pk_uk_mainmaster.ItemNA," + System.lineSeparator() +
						"    jdbc_tool_test_pk_uk_mainmaster.UnitPrice," + System.lineSeparator() +
						"    jdbc_tool_test_pk_uk_mainmaster.SellPrice," + System.lineSeparator() +
						"    jdbc_tool_test_pk_uk_mainmaster.Flag," + System.lineSeparator() +
						"    jdbc_tool_test_pk_uk_mainmaster.PriceFlag," + System.lineSeparator() +
						"    jdbc_tool_test_pk_uk_mainmaster.ExclusiveFG," + System.lineSeparator() +
						"    jdbc_tool_test_pk_uk_mainmaster.RegisteredDT," + System.lineSeparator() +
						"    jdbc_tool_test_pk_uk_mainmaster.UpdatedDT," + System.lineSeparator() +
						"    jdbc_tool_test_mainmaster.objectID," + System.lineSeparator() +
						"    jdbc_tool_test_unitmaster.Code," + System.lineSeparator() +
						"    jdbc_tool_test_ordermaster.SlipNO," + System.lineSeparator() +
						"    jdbc_tool_test_mainmaster.SellPrice" + System.lineSeparator() +
						"FROM" + System.lineSeparator() +
						"    " + dbSchema + ".jdbc_tool_test_pk_uk_mainmaster jdbc_tool_test_pk_uk_mainmaster" + System.lineSeparator() +
						"${join}" +
				"WHERE" + System.lineSeparator() +
						"    (" + System.lineSeparator() +
						"        jdbc_tool_test_pk_uk_mainmaster.objectID = ?" + System.lineSeparator() +
						"        AND jdbc_tool_test_unitmaster.Code = ?" + System.lineSeparator() +
						"        AND jdbc_tool_test_mainmaster.objectID = ?" + System.lineSeparator() +
						"        AND jdbc_tool_test_ordermaster.SlipNO LIKE ? ESCAPE '/'" + System.lineSeparator() +
						"        AND jdbc_tool_test_pk_uk_mainmaster.ItemCD LIKE ? ESCAPE '/'" + System.lineSeparator() +
						"        AND jdbc_tool_test_pk_uk_mainmaster.ItemNA LIKE ? ESCAPE '/'" + System.lineSeparator() +
						"    )",
				List.of("    LEFT OUTER JOIN " + dbSchema + ".jdbc_tool_test_unitmaster jdbc_tool_test_unitmaster ON jdbc_tool_test_pk_uk_mainmaster.objectID = jdbc_tool_test_unitmaster.Code" + System.lineSeparator(),
				"    INNER JOIN " + dbSchema + ".jdbc_tool_test_mainmaster jdbc_tool_test_mainmaster ON jdbc_tool_test_pk_uk_mainmaster.objectID = jdbc_tool_test_mainmaster.objectID" + System.lineSeparator(),
				"    RIGHT OUTER JOIN " + dbSchema + ".jdbc_tool_test_ordermaster jdbc_tool_test_ordermaster ON jdbc_tool_test_pk_uk_mainmaster.ItemCD = jdbc_tool_test_ordermaster.objectID" + System.lineSeparator()
		), result.getData().get(0).getSqlScript()));

		String whereParamsParts = "Map<String, Object> whereParams = new HashMap<>();" + System.lineSeparator() +
				"whereParams.put(\"jdbc_tool_test_unitmaster.Code\",);" + System.lineSeparator() +
				"whereParams.put(\"jdbc_tool_test_pk_uk_mainmaster.ItemCD\",);" + System.lineSeparator() +
				"whereParams.put(\"jdbc_tool_test_pk_uk_mainmaster.ItemNA\",);" + System.lineSeparator() +
				"whereParams.put(\"jdbc_tool_test_pk_uk_mainmaster.objectID\",);" + System.lineSeparator() +
				"whereParams.put(\"jdbc_tool_test_ordermaster.SlipNO\",);" + System.lineSeparator() +
				"whereParams.put(\"jdbc_tool_test_mainmaster.objectID\",);" + System.lineSeparator();
		String groupByApiStr = "select(\"jdbc_tool_test_pk_uk_mainmaster\", \"jdbc_tool_test_pk_uk_mainmaster\")" + System.lineSeparator() +
				".fields(col(\"jdbc_tool_test_pk_uk_mainmaster.objectID\")," +
				"col(\"jdbc_tool_test_pk_uk_mainmaster.ItemCD\")," +
				"col(\"jdbc_tool_test_pk_uk_mainmaster.ItemNA\")," +
				"col(\"jdbc_tool_test_pk_uk_mainmaster.UnitPrice\")," +
				"col(\"jdbc_tool_test_pk_uk_mainmaster.SellPrice\")," +
				"col(\"jdbc_tool_test_pk_uk_mainmaster.Flag\")," +
				"col(\"jdbc_tool_test_pk_uk_mainmaster.PriceFlag\")," +
				"col(\"jdbc_tool_test_pk_uk_mainmaster.ExclusiveFG\")," +
				"col(\"jdbc_tool_test_pk_uk_mainmaster.RegisteredDT\")," +
				"col(\"jdbc_tool_test_pk_uk_mainmaster.UpdatedDT\")," +
				"col(\"jdbc_tool_test_mainmaster.objectID\")," +
				"col(\"jdbc_tool_test_unitmaster.Code\")," +
				"col(\"jdbc_tool_test_ordermaster.SlipNO\")," +
				"col(\"jdbc_tool_test_mainmaster.SellPrice\"))" + System.lineSeparator() +
				"${join}" +
				".where(expMap($($(\"jdbc_tool_test_pk_uk_mainmaster.objectID = :jdbc_tool_test_pk_uk_mainmaster.objectID\").AND(\"jdbc_tool_test_unitmaster.Code = :jdbc_tool_test_unitmaster.Code\").AND(\"jdbc_tool_test_mainmaster.objectID = :jdbc_tool_test_mainmaster.objectID\").AND(\"jdbc_tool_test_ordermaster.SlipNO LIKE $CONCAT(:jdbc_tool_test_ordermaster.SlipNO, '%') ESCAPE '/'\").AND(\"jdbc_tool_test_pk_uk_mainmaster.ItemCD LIKE $CONCAT('%', $CONCAT(:jdbc_tool_test_pk_uk_mainmaster.ItemCD, '%')) ESCAPE '/'\").AND(\"jdbc_tool_test_pk_uk_mainmaster.ItemNA LIKE $CONCAT('%', :jdbc_tool_test_pk_uk_mainmaster.ItemNA) ESCAPE '/'\")), whereParams))";

		List<String> joins = List.of(".leftOuterJoinFK(\"FK_UnitMaster\", \"jdbc_tool_test_unitmaster\")" + System.lineSeparator(),
				".innerJoinFK(\"FK_MainMaster\", \"jdbc_tool_test_mainmaster\")" + System.lineSeparator(),
				".rightOuterJoinFK(\"FK_OrderMaster\", \"jdbc_tool_test_ordermaster\")" + System.lineSeparator());
		assertTrue(assertSqlWithJoin(whereParamsParts + "List<GRecord> result = " + groupByApiStr + System.lineSeparator() + ".findList();", joins, result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE))));
		assertTrue(assertSqlWithJoin(whereParamsParts + "GRecord result = " + groupByApiStr + System.lineSeparator() + ".findRecord();", joins, result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE))));
		assertTrue(assertSqlWithJoin(whereParamsParts + "GRecordSet result = " + groupByApiStr + System.lineSeparator() + ".findRecordSet();", joins, result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE))));
	}

	@Test
	public void testExport_filterExpWhereParts_JoinColumn_overrideAlias() {
		doCustomConfig(getForceAliasColumnConfigPath());
		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_filterExpWhereParts_JoinColumn.xml");
		String conditionFilePath = getConditionFilePath("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_filterExpWhereparts_joincolumn.yaml");
		GExporterResult<List<GXFormCodeResult>, byte[]> result = service.export(conditionFilePath, settingsFilePath);

		assertEquals("html", result.getMediaType());
		assertEquals(1, result.getData().size());
		assertEquals(GCrudType.SELECT, result.getData().get(0).getType());
		assertEquals("仮想表ID: 395DA3D2A7C541FDAFF20CC7AF7BBFE8", result.getData().get(0).getDescription());
		assertTrue(assertSqlWithJoin("SELECT" + System.lineSeparator() +
				"    jdbc_tool_test_pk_uk_mainmaster.objectID," + System.lineSeparator() +
				"    jdbc_tool_test_pk_uk_mainmaster.ItemCD," + System.lineSeparator() +
				"    jdbc_tool_test_pk_uk_mainmaster.ItemNA," + System.lineSeparator() +
				"    jdbc_tool_test_pk_uk_mainmaster.UnitPrice," + System.lineSeparator() +
				"    jdbc_tool_test_pk_uk_mainmaster.SellPrice," + System.lineSeparator() +
				"    jdbc_tool_test_pk_uk_mainmaster.Flag," + System.lineSeparator() +
				"    jdbc_tool_test_pk_uk_mainmaster.PriceFlag," + System.lineSeparator() +
				"    jdbc_tool_test_pk_uk_mainmaster.ExclusiveFG," + System.lineSeparator() +
				"    jdbc_tool_test_pk_uk_mainmaster.RegisteredDT," + System.lineSeparator() +
				"    jdbc_tool_test_pk_uk_mainmaster.UpdatedDT," + System.lineSeparator() +
				"    jdbc_tool_test_mainmaster.objectID," + System.lineSeparator() +
				"    jdbc_tool_test_unitmaster.Code," + System.lineSeparator() +
				"    jdbc_tool_test_ordermaster.SlipNO," + System.lineSeparator() +
				"    jdbc_tool_test_mainmaster.SellPrice" + System.lineSeparator() +
				"FROM" + System.lineSeparator() +
				"    " + dbSchema + ".jdbc_tool_test_pk_uk_mainmaster jdbc_tool_test_pk_uk_mainmaster" + System.lineSeparator() +
				"${join}" +
				"WHERE" + System.lineSeparator() +
				"    (" + System.lineSeparator() +
				"        jdbc_tool_test_pk_uk_mainmaster.objectID = '1001'" + System.lineSeparator() +
				"        AND jdbc_tool_test_unitmaster.Code = '1001'" + System.lineSeparator() +
				"        AND jdbc_tool_test_mainmaster.objectID = '1001'" + System.lineSeparator() +
				"        AND jdbc_tool_test_ordermaster.SlipNO LIKE '100%'" + System.lineSeparator() +
				"        AND jdbc_tool_test_pk_uk_mainmaster.ItemCD LIKE '%100%'" + System.lineSeparator() +
				"        AND jdbc_tool_test_pk_uk_mainmaster.ItemNA LIKE '%gef%'" + System.lineSeparator() +
				"    )", List.of(
						"    LEFT OUTER JOIN " + dbSchema + ".jdbc_tool_test_unitmaster jdbc_tool_test_unitmaster ON jdbc_tool_test_pk_uk_mainmaster.objectID = jdbc_tool_test_unitmaster.Code" + System.lineSeparator(),
						"    INNER JOIN " + dbSchema + ".jdbc_tool_test_mainmaster jdbc_tool_test_mainmaster ON jdbc_tool_test_pk_uk_mainmaster.objectID = jdbc_tool_test_mainmaster.objectID" + System.lineSeparator(),
						"    RIGHT OUTER JOIN " + dbSchema + ".jdbc_tool_test_ordermaster jdbc_tool_test_ordermaster ON jdbc_tool_test_pk_uk_mainmaster.ItemCD = jdbc_tool_test_ordermaster.objectID" + System.lineSeparator()
				), result.getData().get(0).getSqlScript()));

		String joinColumnFilterExpApiStr = "select(\"jdbc_tool_test_pk_uk_mainmaster\", \"jdbc_tool_test_pk_uk_mainmaster\")" + System.lineSeparator() +
				".fields(col(\"jdbc_tool_test_pk_uk_mainmaster.objectID\", \"objectID\")," +
				"col(\"jdbc_tool_test_pk_uk_mainmaster.ItemCD\", \"ItemCD\")," +
				"col(\"jdbc_tool_test_pk_uk_mainmaster.ItemNA\", \"ItemNA\")," +
				"col(\"jdbc_tool_test_pk_uk_mainmaster.UnitPrice\", \"UnitPrice\")," +
				"col(\"jdbc_tool_test_pk_uk_mainmaster.SellPrice\", \"SellPrice\")," +
				"col(\"jdbc_tool_test_pk_uk_mainmaster.Flag\", \"Flag\")," +
				"col(\"jdbc_tool_test_pk_uk_mainmaster.PriceFlag\", \"PriceFlag\")," +
				"col(\"jdbc_tool_test_pk_uk_mainmaster.ExclusiveFG\", \"ExclusiveFG\")," +
				"col(\"jdbc_tool_test_pk_uk_mainmaster.RegisteredDT\", \"RegisteredDT\")," +
				"col(\"jdbc_tool_test_pk_uk_mainmaster.UpdatedDT\", \"UpdatedDT\")," +
				"col(\"jdbc_tool_test_mainmaster.objectID\", \"mainMasterObjectID\")," +
				"col(\"jdbc_tool_test_unitmaster.Code\", \"UnitMasterCode\")," +
				"col(\"jdbc_tool_test_ordermaster.SlipNO\", \"OrderMasterSlipNO\")," +
				"col(\"jdbc_tool_test_mainmaster.SellPrice\", \"MainMasterSellPrice\"))" + System.lineSeparator() +
				"${join}"+
				".where(exp($($(\"jdbc_tool_test_pk_uk_mainmaster.objectID = '1001'\").AND(\"jdbc_tool_test_unitmaster.Code = '1001'\").AND(\"jdbc_tool_test_mainmaster.objectID = '1001'\").AND(\"jdbc_tool_test_ordermaster.SlipNO LIKE '100%'\").AND(\"jdbc_tool_test_pk_uk_mainmaster.ItemCD LIKE '%100%'\").AND(\"jdbc_tool_test_pk_uk_mainmaster.ItemNA LIKE '%gef%'\"))))";

		List<String> joins = List.of(".leftOuterJoinFK(\"FK_UnitMaster\", \"jdbc_tool_test_unitmaster\")" + System.lineSeparator(),
				".innerJoinFK(\"FK_MainMaster\", \"jdbc_tool_test_mainmaster\")" + System.lineSeparator(),
				".rightOuterJoinFK(\"FK_OrderMaster\", \"jdbc_tool_test_ordermaster\")" + System.lineSeparator());
		assertTrue(assertSqlWithJoin("List<GRecord> result = " + joinColumnFilterExpApiStr + System.lineSeparator() + ".findList();", joins, result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE))));
		assertTrue(assertSqlWithJoin("GRecord result = " + joinColumnFilterExpApiStr + System.lineSeparator() + ".findRecord();", joins, result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE))));
		assertTrue(assertSqlWithJoin("GRecordSet result = " + joinColumnFilterExpApiStr + System.lineSeparator() + ".findRecordSet();", joins, result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE))));
	}

	@Test
	public void testExport_filterExpWhereParts_JoinColumn() {
		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_filterExpWhereParts_JoinColumn.xml");
		String conditionFilePath = getConditionFilePath("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_filterExpWhereparts_joincolumn.yaml");
		GExporterResult<List<GXFormCodeResult>, byte[]> result = service.export(conditionFilePath, settingsFilePath);

		assertEquals("html", result.getMediaType());
		assertEquals(1, result.getData().size());
		assertEquals(GCrudType.SELECT, result.getData().get(0).getType());
		assertEquals("仮想表ID: 395DA3D2A7C541FDAFF20CC7AF7BBFE8", result.getData().get(0).getDescription());
		assertTrue(assertSqlWithJoin("SELECT" + System.lineSeparator() +
				"    jdbc_tool_test_pk_uk_mainmaster.objectID," + System.lineSeparator() +
				"    jdbc_tool_test_pk_uk_mainmaster.ItemCD," + System.lineSeparator() +
				"    jdbc_tool_test_pk_uk_mainmaster.ItemNA," + System.lineSeparator() +
				"    jdbc_tool_test_pk_uk_mainmaster.UnitPrice," + System.lineSeparator() +
				"    jdbc_tool_test_pk_uk_mainmaster.SellPrice," + System.lineSeparator() +
				"    jdbc_tool_test_pk_uk_mainmaster.Flag," + System.lineSeparator() +
				"    jdbc_tool_test_pk_uk_mainmaster.PriceFlag," + System.lineSeparator() +
				"    jdbc_tool_test_pk_uk_mainmaster.ExclusiveFG," + System.lineSeparator() +
				"    jdbc_tool_test_pk_uk_mainmaster.RegisteredDT," + System.lineSeparator() +
				"    jdbc_tool_test_pk_uk_mainmaster.UpdatedDT," + System.lineSeparator() +
				"    jdbc_tool_test_mainmaster.objectID," + System.lineSeparator() +
				"    jdbc_tool_test_unitmaster.Code," + System.lineSeparator() +
				"    jdbc_tool_test_ordermaster.SlipNO," + System.lineSeparator() +
				"    jdbc_tool_test_mainmaster.SellPrice" + System.lineSeparator() +
				"FROM" + System.lineSeparator() +
				"    " + dbSchema + ".jdbc_tool_test_pk_uk_mainmaster jdbc_tool_test_pk_uk_mainmaster" + System.lineSeparator() +
				"${join}" +
				"WHERE" + System.lineSeparator() +
				"    (" + System.lineSeparator() +
				"        jdbc_tool_test_pk_uk_mainmaster.objectID = '1001'" + System.lineSeparator() +
				"        AND jdbc_tool_test_unitmaster.Code = '1001'" + System.lineSeparator() +
				"        AND jdbc_tool_test_mainmaster.objectID = '1001'" + System.lineSeparator() +
				"        AND jdbc_tool_test_ordermaster.SlipNO LIKE '100%'" + System.lineSeparator() +
				"        AND jdbc_tool_test_pk_uk_mainmaster.ItemCD LIKE '%100%'" + System.lineSeparator() +
				"        AND jdbc_tool_test_pk_uk_mainmaster.ItemNA LIKE '%gef%'" + System.lineSeparator() +
				"    )", List.of(
			"    LEFT OUTER JOIN " + dbSchema + ".jdbc_tool_test_unitmaster jdbc_tool_test_unitmaster ON jdbc_tool_test_pk_uk_mainmaster.objectID = jdbc_tool_test_unitmaster.Code" + System.lineSeparator(),
				"    INNER JOIN " + dbSchema + ".jdbc_tool_test_mainmaster jdbc_tool_test_mainmaster ON jdbc_tool_test_pk_uk_mainmaster.objectID = jdbc_tool_test_mainmaster.objectID" + System.lineSeparator(),
				"    RIGHT OUTER JOIN " + dbSchema + ".jdbc_tool_test_ordermaster jdbc_tool_test_ordermaster ON jdbc_tool_test_pk_uk_mainmaster.ItemCD = jdbc_tool_test_ordermaster.objectID" + System.lineSeparator()),
				result.getData().get(0).getSqlScript()));

		String joinColumnFilterExpApiStr = "select(\"jdbc_tool_test_pk_uk_mainmaster\", \"jdbc_tool_test_pk_uk_mainmaster\")" + System.lineSeparator() +
				".fields(col(\"jdbc_tool_test_pk_uk_mainmaster.objectID\")," +
				"col(\"jdbc_tool_test_pk_uk_mainmaster.ItemCD\")," +
				"col(\"jdbc_tool_test_pk_uk_mainmaster.ItemNA\")," +
				"col(\"jdbc_tool_test_pk_uk_mainmaster.UnitPrice\")," +
				"col(\"jdbc_tool_test_pk_uk_mainmaster.SellPrice\")," +
				"col(\"jdbc_tool_test_pk_uk_mainmaster.Flag\")," +
				"col(\"jdbc_tool_test_pk_uk_mainmaster.PriceFlag\")," +
				"col(\"jdbc_tool_test_pk_uk_mainmaster.ExclusiveFG\")," +
				"col(\"jdbc_tool_test_pk_uk_mainmaster.RegisteredDT\")," +
				"col(\"jdbc_tool_test_pk_uk_mainmaster.UpdatedDT\")," +
				"col(\"jdbc_tool_test_mainmaster.objectID\")," +
				"col(\"jdbc_tool_test_unitmaster.Code\")," +
				"col(\"jdbc_tool_test_ordermaster.SlipNO\")," +
				"col(\"jdbc_tool_test_mainmaster.SellPrice\"))" + System.lineSeparator() +
				"${join}" +
				".where(expMap($($(\"jdbc_tool_test_pk_uk_mainmaster.objectID = '1001'\").AND(\"jdbc_tool_test_unitmaster.Code = '1001'\").AND(\"jdbc_tool_test_mainmaster.objectID = '1001'\").AND(\"jdbc_tool_test_ordermaster.SlipNO LIKE '100%'\").AND(\"jdbc_tool_test_pk_uk_mainmaster.ItemCD LIKE '%100%'\").AND(\"jdbc_tool_test_pk_uk_mainmaster.ItemNA LIKE '%gef%'\")), Collections.emptyMap()))";

		List<String> joins = List.of(".leftOuterJoinFK(\"FK_UnitMaster\", \"jdbc_tool_test_unitmaster\")" + System.lineSeparator(),
				".innerJoinFK(\"FK_MainMaster\", \"jdbc_tool_test_mainmaster\")" + System.lineSeparator(),
				".rightOuterJoinFK(\"FK_OrderMaster\", \"jdbc_tool_test_ordermaster\")" + System.lineSeparator());
		assertTrue(assertSqlWithJoin("List<GRecord> result = " + joinColumnFilterExpApiStr + System.lineSeparator() + ".findList();", joins, result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE))));
		assertTrue(assertSqlWithJoin("GRecord result = " + joinColumnFilterExpApiStr + System.lineSeparator() + ".findRecord();", joins, result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE))));
		assertTrue(assertSqlWithJoin("GRecordSet result = " + joinColumnFilterExpApiStr + System.lineSeparator() + ".findRecordSet();", joins, result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE))));
	}

	@Test
	public void testExport_fullCase() {

		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_fullcase.xml");
		String conditionFilePath = getConditionFilePath("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_fullcase.yaml");
		GExporterResult<List<GXFormCodeResult>, byte[]> result = service.export(conditionFilePath, settingsFilePath);

		assertEquals("html", result.getMediaType());
		assertEquals(4, result.getData().size());
		assertEquals(GCrudType.INSERT, result.getData().get(0).getType());
		assertEquals("仮想表ID: 35B6AB911C524EE69A30E29BF9A2904A", result.getData().get(0).getDescription());
		assertEquals("INSERT INTO" + System.lineSeparator() +
				"    " + dbSchema + ".jdbc_tool_test_fullCaseMainEntity(objectID, StringColumn, IntColumn, NStringColumn, DateTimeColumn, YMColumn, CurrencyColumn, LongColumn, CompanyCD, ExclusiveFG, RegisteredPerson, UpdatedPerson, RegisteredDT, UpdatedDT)" + System.lineSeparator() +
				"VALUES" + System.lineSeparator() +
				"(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " + getTimestampCommand() + ", " + getTimestampCommand() + ")", result.getData().get(0).getSqlScript());

		GDao2VirtualEntity insertCodeResult = ((GDao2JdbcXFormCodeResult)result.getData().get(0)).getVirtualEntity();
		assertEquals(19, insertCodeResult.getColumns().size());
		assertEquals("Map<String, Object> absoluteVirtualColumn0 = new HashMap<>();" + System.lineSeparator() +
				"absoluteVirtualColumn0.put(\"name\", \"virtualVolumn01\");" + System.lineSeparator() +
				"absoluteVirtualColumn0.put(\"displayName\", \"virtualVolumn01\");" + System.lineSeparator() +
				"absoluteVirtualColumn0.put(\"fixedValue\", \"virtualVolumn01V\");" + System.lineSeparator() +
				"absoluteVirtualColumn0.put(\"aggregateFunction\", \"-\");" + System.lineSeparator() +
				"absoluteVirtualColumn0.put(\"format\", \"virtualVolumn01F\");" + System.lineSeparator() +
				"Map<String, Object> absoluteVirtualColumn1 = new HashMap<>();" + System.lineSeparator() +
				"absoluteVirtualColumn1.put(\"name\", \"virtualVolumn02\");" + System.lineSeparator() +
				"absoluteVirtualColumn1.put(\"displayName\", \"virtualVolumn02\");" + System.lineSeparator() +
				"absoluteVirtualColumn1.put(\"fixedValue\", \"virtualVolumn02V\");" + System.lineSeparator() +
				"absoluteVirtualColumn1.put(\"aggregateFunction\", \"-\");" + System.lineSeparator() +
				"absoluteVirtualColumn1.put(\"format\", \"virtualVolumn02F\");" + System.lineSeparator(), insertCodeResult.getAbsoluteVirtualColumnCode());

		String insertExecuteApiStr = "insert(\"jdbc_tool_test_fullCaseMainEntity\")" + System.lineSeparator() + ".values(record)";
		String insertRecord = "GRecord record = createRecord();" + System.lineSeparator() +
				"record.setObject(\"objectID\",);" + System.lineSeparator() +
				"record.setObject(\"StringColumn\",);" + System.lineSeparator() +
				"record.setObject(\"IntColumn\",);" + System.lineSeparator() +
				"record.setObject(\"NStringColumn\",);" + System.lineSeparator() +
				"record.setObject(\"DateTimeColumn\",);" + System.lineSeparator() +
				"record.setObject(\"YMColumn\",);" + System.lineSeparator() +
				"record.setObject(\"CurrencyColumn\",);" + System.lineSeparator() +
				"record.setObject(\"LongColumn\",);" + System.lineSeparator() +
				"record.setObject(\"CompanyCD\",);" + System.lineSeparator() +
				"record.setObject(\"ExclusiveFG\",);" + System.lineSeparator() +
				"record.setObject(\"RegisteredPerson\",);" + System.lineSeparator() +
				"record.setObject(\"UpdatedPerson\",);" + System.lineSeparator() +
				"record.setObject(\"RegisteredDT\",);" + System.lineSeparator() +
				"record.setObject(\"UpdatedDT\",);" + System.lineSeparator();

		assertEquals(insertRecord + System.lineSeparator() + "int count = " + insertExecuteApiStr + System.lineSeparator() + ".execute();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, null)));

		String insertExecuteBatchApiStr = "insert(\"jdbc_tool_test_fullCaseMainEntity\")" + System.lineSeparator() + ".values(recordList)";
		StringBuilder insertRecordList = new StringBuilder();
		insertRecordList.append("List<GRecord> recordList = new ArrayList<>();").append(System.lineSeparator());
		insertRecordList.append(insertRecord);
		insertRecordList.append("recordList.add(record);").append(System.lineSeparator());

		assertEquals(insertRecordList + System.lineSeparator() + "int count = " + insertExecuteBatchApiStr + System.lineSeparator() + ".executeBatch();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, null)));
		assertEquals(insertRecordList + System.lineSeparator() + "int count = " + insertExecuteBatchApiStr + System.lineSeparator() + ".executeList();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, null)));


		assertEquals(GCrudType.UPDATE, result.getData().get(1).getType());
		assertEquals("仮想表ID: 35B6AB911C524EE69A30E29BF9A2904A", result.getData().get(1).getDescription());
		assertEquals("UPDATE" + System.lineSeparator() +
				"    " + dbSchema + ".jdbc_tool_test_fullCaseMainEntity jdbc_tool_test_fullCaseMainEntity" + System.lineSeparator() +
				"SET" + System.lineSeparator() +
				"    StringColumn = ?," + System.lineSeparator() +
				"    IntColumn = ?," + System.lineSeparator() +
				"    NStringColumn = ?," + System.lineSeparator() +
				"    DateTimeColumn = ?," + System.lineSeparator() +
				"    YMColumn = ?," + System.lineSeparator() +
				"    CurrencyColumn = ?," + System.lineSeparator() +
				"    LongColumn = ?," + System.lineSeparator() +
				"    CompanyCD = ?," + System.lineSeparator() +
				"    ExclusiveFG = ?," + System.lineSeparator() +
				"    RegisteredPerson = ?," + System.lineSeparator() +
				"    RegisteredDT = ?," + System.lineSeparator() +
				"    UpdatedPerson = ?," + System.lineSeparator() +
				"    UpdatedDT = ?" + System.lineSeparator() +
				"WHERE" + System.lineSeparator() +
				"    (" + System.lineSeparator() +
				"        jdbc_tool_test_fullCaseMainEntity.StringColumn = '9001'" + System.lineSeparator() +
				"        AND jdbc_tool_test_fullCaseMainEntity.IntColumn = 8001" + System.lineSeparator() +
				"    )" + System.lineSeparator() +
				"    AND (" + System.lineSeparator() +
				"        jdbc_tool_test_fullCaseMainEntity.NStringColumn = ?" + System.lineSeparator() +
				"        AND jdbc_tool_test_fullCaseMainEntity.DateTimeColumn = ?" + System.lineSeparator() +
				"        AND jdbc_tool_test_fullCaseMainEntity.CompanyCD = ?" + System.lineSeparator() +
				"    )", result.getData().get(1).getSqlScript());

		GDao2VirtualEntity updateCodeResult = ((GDao2JdbcXFormCodeResult)result.getData().get(1)).getVirtualEntity();
		assertEquals(19, updateCodeResult.getColumns().size());
		assertEquals("Map<String, Object> absoluteVirtualColumn0 = new HashMap<>();" + System.lineSeparator() +
				"absoluteVirtualColumn0.put(\"name\", \"virtualVolumn01\");" + System.lineSeparator() +
				"absoluteVirtualColumn0.put(\"displayName\", \"virtualVolumn01\");" + System.lineSeparator() +
				"absoluteVirtualColumn0.put(\"fixedValue\", \"virtualVolumn01V\");" + System.lineSeparator() +
				"absoluteVirtualColumn0.put(\"aggregateFunction\", \"-\");" + System.lineSeparator() +
				"absoluteVirtualColumn0.put(\"format\", \"virtualVolumn01F\");" + System.lineSeparator() +
				"Map<String, Object> absoluteVirtualColumn1 = new HashMap<>();" + System.lineSeparator() +
				"absoluteVirtualColumn1.put(\"name\", \"virtualVolumn02\");" + System.lineSeparator() +
				"absoluteVirtualColumn1.put(\"displayName\", \"virtualVolumn02\");" + System.lineSeparator() +
				"absoluteVirtualColumn1.put(\"fixedValue\", \"virtualVolumn02V\");" + System.lineSeparator() +
				"absoluteVirtualColumn1.put(\"aggregateFunction\", \"-\");" + System.lineSeparator() +
				"absoluteVirtualColumn1.put(\"format\", \"virtualVolumn02F\");" + System.lineSeparator(), updateCodeResult.getAbsoluteVirtualColumnCode());

		String updateRecord = "GRecord record = createRecord();" + System.lineSeparator() +
				"record.setObject(\"StringColumn\",);" + System.lineSeparator() +
				"record.setObject(\"IntColumn\",);" + System.lineSeparator() +
				"record.setObject(\"NStringColumn\",);" + System.lineSeparator() +
				"record.setObject(\"DateTimeColumn\",);" + System.lineSeparator() +
				"record.setObject(\"YMColumn\",);" + System.lineSeparator() +
				"record.setObject(\"CurrencyColumn\",);" + System.lineSeparator() +
				"record.setObject(\"LongColumn\",);" + System.lineSeparator() +
				"record.setObject(\"CompanyCD\",);" + System.lineSeparator() +
				"record.setObject(\"ExclusiveFG\",);" + System.lineSeparator() +
				"record.setObject(\"RegisteredPerson\",);" + System.lineSeparator() +
				"record.setObject(\"RegisteredDT\",);" + System.lineSeparator() +
				"record.setObject(\"UpdatedPerson\",);" + System.lineSeparator() +
				"record.setObject(\"UpdatedDT\",);" + System.lineSeparator();

		String whereParamsParts = "Map<String, Object> whereParams = new HashMap<>();" + System.lineSeparator() +
				"whereParams.put(\"jdbc_tool_test_fullCaseMainEntity.DateTimeColumn\",);" + System.lineSeparator() +
				"whereParams.put(\"jdbc_tool_test_fullCaseMainEntity.NStringColumn\",);" + System.lineSeparator() +
				"whereParams.put(\"jdbc_tool_test_fullCaseMainEntity.CompanyCD\",);" + System.lineSeparator();
		String whereParamsPartsPKUK = "Object[] whereParams = new Object[]{,,};" + System.lineSeparator();
		StringBuilder updateRecordList = new StringBuilder();
		updateRecordList.append("List<GRecord> recordList = new ArrayList<>();" + System.lineSeparator());
		updateRecordList.append(updateRecord);
		updateRecordList.append("recordList.add(record);" + System.lineSeparator());

		String updateExecuteApiStr = "update(\"jdbc_tool_test_fullCaseMainEntity\")" + System.lineSeparator() + ".set(record)";

		String updateWhereParts = ".where(expMap($($(\"jdbc_tool_test_fullCaseMainEntity.StringColumn = '9001'\").AND(\"jdbc_tool_test_fullCaseMainEntity.IntColumn = 8001\")).AND($(\"jdbc_tool_test_fullCaseMainEntity.NStringColumn = :jdbc_tool_test_fullCaseMainEntity.NStringColumn\").AND(\"jdbc_tool_test_fullCaseMainEntity.DateTimeColumn = :jdbc_tool_test_fullCaseMainEntity.DateTimeColumn\").AND(\"jdbc_tool_test_fullCaseMainEntity.CompanyCD = $TRIM(:jdbc_tool_test_fullCaseMainEntity.CompanyCD)\")), whereParams))";

		assertEquals(whereParamsParts + updateRecord + System.lineSeparator() + "int count = " + updateExecuteApiStr + System.lineSeparator() + updateWhereParts + System.lineSeparator() + ".execute();",
				result.getData().get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE)));
		assertEquals(updateRecord + System.lineSeparator() + "int count = " + updateExecuteApiStr + System.lineSeparator() + ".wherePK()" + System.lineSeparator() + ".execute();",
				result.getData().get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_PK)));
		assertEquals(updateRecord + System.lineSeparator() + "int count = " + updateExecuteApiStr + System.lineSeparator() + ".whereUK(\"MultipleColumnUK\")" + System.lineSeparator() + "//.whereUK(\"OtherUKName\")" + System.lineSeparator() + "//.whereUK(\"SingleColumnUK\")" + System.lineSeparator() + ".execute();",
				result.getData().get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_UK)));

		String updateExecuteBatchApiStr = "update(\"jdbc_tool_test_fullCaseMainEntity\")" + System.lineSeparator() +
				".set(recordList)";

		assertEquals(updateRecordList + "" + System.lineSeparator() + "int count = " + updateExecuteBatchApiStr + System.lineSeparator()+ ".wherePK()" + System.lineSeparator()+ ".executeBatch();", result.getData().get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_PK)));
		assertEquals(updateRecordList + "" + System.lineSeparator() + "int count = " + updateExecuteBatchApiStr + System.lineSeparator()+ ".whereUK(\"MultipleColumnUK\")" + System.lineSeparator() + "//.whereUK(\"OtherUKName\")" + System.lineSeparator() + "//.whereUK(\"SingleColumnUK\")" + System.lineSeparator()+ ".executeBatch();", result.getData().get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_UK)));
		assertEquals(updateRecordList + "" + System.lineSeparator() + "int count = " + updateExecuteBatchApiStr + System.lineSeparator()+ ".wherePK()" + System.lineSeparator()+ ".executeList();", result.getData().get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_PK)));
		assertEquals(updateRecordList + "" + System.lineSeparator() + "int count = " + updateExecuteBatchApiStr + System.lineSeparator()+ ".whereUK(\"MultipleColumnUK\")" + System.lineSeparator() + "//.whereUK(\"OtherUKName\")" + System.lineSeparator() + "//.whereUK(\"SingleColumnUK\")" + System.lineSeparator()+ ".executeList();", result.getData().get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_UK)));

		assertEquals(GCrudType.SELECT, result.getData().get(2).getType());
		assertEquals("仮想表ID: 35B6AB911C524EE69A30E29BF9A2904A", result.getData().get(2).getDescription());
		assertTrue(assertSqlWithJoin("SELECT" + System.lineSeparator() +
						"    MAX(jdbc_tool_test_fullCaseMainEntity.objectID)," + System.lineSeparator() +
						"    SUM(jdbc_tool_test_fullCaseMainEntity.StringColumn)," + System.lineSeparator() +
						"    AVG(jdbc_tool_test_fullCaseMainEntity.IntColumn)," + System.lineSeparator() +
						"    MIN(jdbc_tool_test_fullCaseMainEntity.NStringColumn)," + System.lineSeparator() +
						"    MAX(jdbc_tool_test_fullCaseMainEntity.DateTimeColumn)," + System.lineSeparator() +
						"    COUNT(jdbc_tool_test_fullCaseMainEntity.YMColumn)," + System.lineSeparator() +
						"    MAX(jdbc_tool_test_fullCaseMainEntity.CurrencyColumn)," + System.lineSeparator() +
						"    MAX(jdbc_tool_test_fullCaseMainEntity.LongColumn)," + System.lineSeparator() +
						"    MAX(jdbc_tool_test_fullCaseMainEntity.CompanyCD)," + System.lineSeparator() +
						"    MAX(jdbc_tool_test_fullCaseMainEntity.ExclusiveFG)," + System.lineSeparator() +
						"    MAX(jdbc_tool_test_fullCaseMainEntity.RegisteredPerson)," + System.lineSeparator() +
						"    MAX(jdbc_tool_test_fullCaseMainEntity.RegisteredDT)," + System.lineSeparator() +
						"    MAX(jdbc_tool_test_fullCaseMainEntity.UpdatedPerson)," + System.lineSeparator() +
						"    MAX(jdbc_tool_test_fullCaseMainEntity.UpdatedDT)," + System.lineSeparator() +
						"    MAX(jdbc_tool_test_fullCaseInnerJoinEntity.StringColumnInner)," + System.lineSeparator() +
						"    MAX(jdbc_tool_test_fullCaseRightJoinEntity.IntColumnRight)," + System.lineSeparator() +
						"    MAX(jdbc_tool_test_fullCaseLeftJoinEntity.NStringColumnLeft)" + System.lineSeparator() +
						"FROM" + System.lineSeparator() +
						"    " + dbSchema + ".jdbc_tool_test_fullCaseMainEntity jdbc_tool_test_fullCaseMainEntity" + System.lineSeparator() +
						"${join}" +
						"WHERE" + System.lineSeparator() +
						"    (" + System.lineSeparator() +
						"        jdbc_tool_test_fullCaseMainEntity.StringColumn = '9001'" + System.lineSeparator() +
						"        AND jdbc_tool_test_fullCaseMainEntity.IntColumn = 8001" + System.lineSeparator() +
						"    )" + System.lineSeparator() +
						"    AND (" + System.lineSeparator() +
						"        jdbc_tool_test_fullCaseMainEntity.NStringColumn = ?" + System.lineSeparator() +
						"        AND jdbc_tool_test_fullCaseMainEntity.DateTimeColumn = ?" + System.lineSeparator() +
						"        AND jdbc_tool_test_fullCaseMainEntity.CompanyCD = ?" + System.lineSeparator() +
						"    )" + System.lineSeparator() +
						"GROUP BY" + System.lineSeparator() +
						"    jdbc_tool_test_fullCaseMainEntity.CurrencyColumn," + System.lineSeparator() +
						"    jdbc_tool_test_fullCaseMainEntity.LongColumn" + System.lineSeparator() +
						"ORDER BY" + System.lineSeparator() +
						"    SUM(jdbc_tool_test_fullCaseMainEntity.StringColumn) ASC," + System.lineSeparator() +
						"    MIN(jdbc_tool_test_fullCaseMainEntity.NStringColumn) DESC",
				List.of(	"    INNER JOIN " + dbSchema + ".jdbc_tool_test_fullCaseInnerJoinEntity jdbc_tool_test_fullCaseInnerJoinEntity ON jdbc_tool_test_fullCaseMainEntity.StringColumn = jdbc_tool_test_fullCaseInnerJoinEntity.StringColumnInner" + System.lineSeparator(),
				"    AND jdbc_tool_test_fullCaseInnerJoinEntity.NStringColumnInner = '9001'" + System.lineSeparator() +
				"    RIGHT OUTER JOIN " + dbSchema + ".jdbc_tool_test_fullCaseRightJoinEntity jdbc_tool_test_fullCaseRightJoinEntity ON jdbc_tool_test_fullCaseMainEntity.NStringColumn = jdbc_tool_test_fullCaseRightJoinEntity.NStringColumnRight" + System.lineSeparator(),
				"    AND jdbc_tool_test_fullCaseRightJoinEntity.StringColumnRight = '7001'" + System.lineSeparator() +
				"    LEFT OUTER JOIN " + dbSchema + ".jdbc_tool_test_fullCaseLeftJoinEntity jdbc_tool_test_fullCaseLeftJoinEntity ON jdbc_tool_test_fullCaseMainEntity.IntColumn = jdbc_tool_test_fullCaseLeftJoinEntity.IntColumnLeft" + System.lineSeparator(),
				"    AND jdbc_tool_test_fullCaseLeftJoinEntity.LongColumnLeft = 8001" + System.lineSeparator()
		),result.getData().get(2).getSqlScript()));
		GDao2VirtualEntity selectCodeResult = ((GDao2JdbcXFormCodeResult)result.getData().get(2)).getVirtualEntity();
		assertEquals(19, selectCodeResult.getColumns().size());
		assertEquals("Map<String, Object> absoluteVirtualColumn0 = new HashMap<>();" + System.lineSeparator() +
				"absoluteVirtualColumn0.put(\"name\", \"virtualVolumn01\");" + System.lineSeparator() +
				"absoluteVirtualColumn0.put(\"displayName\", \"virtualVolumn01\");" + System.lineSeparator() +
				"absoluteVirtualColumn0.put(\"fixedValue\", \"virtualVolumn01V\");" + System.lineSeparator() +
				"absoluteVirtualColumn0.put(\"aggregateFunction\", \"-\");" + System.lineSeparator() +
				"absoluteVirtualColumn0.put(\"format\", \"virtualVolumn01F\");" + System.lineSeparator() +
				"Map<String, Object> absoluteVirtualColumn1 = new HashMap<>();" + System.lineSeparator() +
				"absoluteVirtualColumn1.put(\"name\", \"virtualVolumn02\");" + System.lineSeparator() +
				"absoluteVirtualColumn1.put(\"displayName\", \"virtualVolumn02\");" + System.lineSeparator() +
				"absoluteVirtualColumn1.put(\"fixedValue\", \"virtualVolumn02V\");" + System.lineSeparator() +
				"absoluteVirtualColumn1.put(\"aggregateFunction\", \"-\");" + System.lineSeparator() +
				"absoluteVirtualColumn1.put(\"format\", \"virtualVolumn02F\");" + System.lineSeparator(), selectCodeResult.getAbsoluteVirtualColumnCode());

		String selectApiStr = "select(\"jdbc_tool_test_fullCaseMainEntity\", \"jdbc_tool_test_fullCaseMainEntity\")" + System.lineSeparator() +
				".fields(col(\"jdbc_tool_test_fullCaseMainEntity.objectID\", GColumn.FuncType.MAX)," +
				"col(\"jdbc_tool_test_fullCaseMainEntity.StringColumn\", GColumn.FuncType.SUM)," +
				"col(\"jdbc_tool_test_fullCaseMainEntity.IntColumn\", GColumn.FuncType.AVG)," +
				"col(\"jdbc_tool_test_fullCaseMainEntity.NStringColumn\", GColumn.FuncType.MIN)," +
				"col(\"jdbc_tool_test_fullCaseMainEntity.DateTimeColumn\", GColumn.FuncType.MAX)," +
				"col(\"jdbc_tool_test_fullCaseMainEntity.YMColumn\", GColumn.FuncType.COUNT)," +
				"col(\"jdbc_tool_test_fullCaseMainEntity.CurrencyColumn\", GColumn.FuncType.MAX)," +
				"col(\"jdbc_tool_test_fullCaseMainEntity.LongColumn\", GColumn.FuncType.MAX)," +
				"col(\"jdbc_tool_test_fullCaseMainEntity.CompanyCD\", GColumn.FuncType.MAX)," +
				"col(\"jdbc_tool_test_fullCaseMainEntity.ExclusiveFG\", GColumn.FuncType.MAX)," +
				"col(\"jdbc_tool_test_fullCaseMainEntity.RegisteredPerson\", GColumn.FuncType.MAX)," +
				"col(\"jdbc_tool_test_fullCaseMainEntity.RegisteredDT\", GColumn.FuncType.MAX)," +
				"col(\"jdbc_tool_test_fullCaseMainEntity.UpdatedPerson\", GColumn.FuncType.MAX)," +
				"col(\"jdbc_tool_test_fullCaseMainEntity.UpdatedDT\", GColumn.FuncType.MAX)," +
				"col(\"jdbc_tool_test_fullCaseInnerJoinEntity.StringColumnInner\", GColumn.FuncType.MAX)," +
				"col(\"jdbc_tool_test_fullCaseRightJoinEntity.IntColumnRight\", GColumn.FuncType.MAX)," +
				"col(\"jdbc_tool_test_fullCaseLeftJoinEntity.NStringColumnLeft\", GColumn.FuncType.MAX))" + System.lineSeparator() +
				".groupBy(col(\"jdbc_tool_test_fullCaseMainEntity.CurrencyColumn\"),col(\"jdbc_tool_test_fullCaseMainEntity.LongColumn\"))" + System.lineSeparator() +
				"${join}" +
				".orderBy(asc(\"jdbc_tool_test_fullCaseMainEntity.StringColumn\"),desc(\"jdbc_tool_test_fullCaseMainEntity.NStringColumn\"))";
		List<String> joins = List.of(".innerJoin(\"jdbc_tool_test_fullCaseInnerJoinEntity\", \"jdbc_tool_test_fullCaseInnerJoinEntity\", expMap($(\"jdbc_tool_test_fullCaseMainEntity.StringColumn = jdbc_tool_test_fullCaseInnerJoinEntity.StringColumnInner\").AND(\"jdbc_tool_test_fullCaseInnerJoinEntity.NStringColumnInner = '9001'\"), Collections.emptyMap()))" + System.lineSeparator(),
				".rightOuterJoin(\"jdbc_tool_test_fullCaseRightJoinEntity\", \"jdbc_tool_test_fullCaseRightJoinEntity\", expMap($(\"jdbc_tool_test_fullCaseMainEntity.NStringColumn = jdbc_tool_test_fullCaseRightJoinEntity.NStringColumnRight\").AND(\"jdbc_tool_test_fullCaseRightJoinEntity.StringColumnRight = '7001'\"), Collections.emptyMap()))" + System.lineSeparator(),
				".leftOuterJoin(\"jdbc_tool_test_fullCaseLeftJoinEntity\", \"jdbc_tool_test_fullCaseLeftJoinEntity\", expMap($(\"jdbc_tool_test_fullCaseMainEntity.IntColumn = jdbc_tool_test_fullCaseLeftJoinEntity.IntColumnLeft\").AND(\"jdbc_tool_test_fullCaseLeftJoinEntity.LongColumnLeft = 8001\"), Collections.emptyMap()))" + System.lineSeparator()
		);

		String selectWhereParts = ".where(expMap($($(\"jdbc_tool_test_fullCaseMainEntity.StringColumn = '9001'\").AND(\"jdbc_tool_test_fullCaseMainEntity.IntColumn = 8001\")).AND($(\"jdbc_tool_test_fullCaseMainEntity.NStringColumn = :jdbc_tool_test_fullCaseMainEntity.NStringColumn\").AND(\"jdbc_tool_test_fullCaseMainEntity.DateTimeColumn = :jdbc_tool_test_fullCaseMainEntity.DateTimeColumn\").AND(\"jdbc_tool_test_fullCaseMainEntity.CompanyCD = $TRIM(:jdbc_tool_test_fullCaseMainEntity.CompanyCD)\")), whereParams))";

		assertTrue(assertSqlWithJoin(whereParamsParts + "List<GRecord> result = " + selectApiStr + "" + System.lineSeparator() + selectWhereParts + System.lineSeparator()+ ".findList();", joins, result.getData().get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE))));
		assertTrue(assertSqlWithJoin(whereParamsPartsPKUK + "List<GRecord> result = " + selectApiStr + System.lineSeparator()+ ".wherePK(whereParams)" + System.lineSeparator()+ ".findList();", joins, result.getData().get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE_PK))));
		assertTrue(assertSqlWithJoin(whereParamsPartsPKUK + "List<GRecord> result = " + selectApiStr + System.lineSeparator()+ ".whereUK(\"MultipleColumnUK\", whereParams)" + System.lineSeparator() + "//.whereUK(\"OtherUKName\", whereParams)" + System.lineSeparator() + "//.whereUK(\"SingleColumnUK\", whereParams)" + System.lineSeparator()+ ".findList();", joins, result.getData().get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE_UK))));
		assertTrue(assertSqlWithJoin(whereParamsParts + "GRecord result = " + selectApiStr + "" + System.lineSeparator() + selectWhereParts + System.lineSeparator()+ ".findRecord();", joins, result.getData().get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE))));
		assertTrue(assertSqlWithJoin(whereParamsPartsPKUK + "GRecord result = " + selectApiStr + System.lineSeparator()+ ".wherePK(whereParams)" + System.lineSeparator()+ ".findRecord();", joins, result.getData().get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE_PK))));
		assertTrue(assertSqlWithJoin(whereParamsPartsPKUK + "GRecord result = " + selectApiStr + System.lineSeparator()+ ".whereUK(\"MultipleColumnUK\", whereParams)" + System.lineSeparator() + "//.whereUK(\"OtherUKName\", whereParams)" + System.lineSeparator() + "//.whereUK(\"SingleColumnUK\", whereParams)" + System.lineSeparator()+ ".findRecord();", joins, result.getData().get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE_UK))));
		assertTrue(assertSqlWithJoin(whereParamsParts + "GRecordSet result = " + selectApiStr + "" + System.lineSeparator() + selectWhereParts + System.lineSeparator()+ ".findRecordSet();", joins, result.getData().get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE))));
		assertTrue(assertSqlWithJoin(whereParamsPartsPKUK + "GRecordSet result = " + selectApiStr + System.lineSeparator()+ ".wherePK(whereParams)" + System.lineSeparator()+ ".findRecordSet();", joins, result.getData().get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE_PK))));
		assertTrue(assertSqlWithJoin(whereParamsPartsPKUK + "GRecordSet result = " + selectApiStr + System.lineSeparator()+ ".whereUK(\"MultipleColumnUK\", whereParams)" + System.lineSeparator() + "//.whereUK(\"OtherUKName\", whereParams)" + System.lineSeparator() + "//.whereUK(\"SingleColumnUK\", whereParams)" + System.lineSeparator()+ ".findRecordSet();", joins, result.getData().get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE_UK))));

		assertEquals(GCrudType.DELETE, result.getData().get(3).getType());
		assertEquals("仮想表ID: 35B6AB911C524EE69A30E29BF9A2904A", result.getData().get(3).getDescription());
		assertEquals(getDeleteSql_fullCase(), result.getData().get(3).getSqlScript());

		GDao2VirtualEntity deleteCodeResult = ((GDao2JdbcXFormCodeResult)result.getData().get(3)).getVirtualEntity();
		assertEquals(19, deleteCodeResult.getColumns().size());
		assertEquals("Map<String, Object> absoluteVirtualColumn0 = new HashMap<>();" + System.lineSeparator() +
				"absoluteVirtualColumn0.put(\"name\", \"virtualVolumn01\");" + System.lineSeparator() +
				"absoluteVirtualColumn0.put(\"displayName\", \"virtualVolumn01\");" + System.lineSeparator() +
				"absoluteVirtualColumn0.put(\"fixedValue\", \"virtualVolumn01V\");" + System.lineSeparator() +
				"absoluteVirtualColumn0.put(\"aggregateFunction\", \"-\");" + System.lineSeparator() +
				"absoluteVirtualColumn0.put(\"format\", \"virtualVolumn01F\");" + System.lineSeparator() +
				"Map<String, Object> absoluteVirtualColumn1 = new HashMap<>();" + System.lineSeparator() +
				"absoluteVirtualColumn1.put(\"name\", \"virtualVolumn02\");" + System.lineSeparator() +
				"absoluteVirtualColumn1.put(\"displayName\", \"virtualVolumn02\");" + System.lineSeparator() +
				"absoluteVirtualColumn1.put(\"fixedValue\", \"virtualVolumn02V\");" + System.lineSeparator() +
				"absoluteVirtualColumn1.put(\"aggregateFunction\", \"-\");" + System.lineSeparator() +
				"absoluteVirtualColumn1.put(\"format\", \"virtualVolumn02F\");" + System.lineSeparator(), selectCodeResult.getAbsoluteVirtualColumnCode());

		String deleteApiStr = "delete(\"jdbc_tool_test_fullCaseMainEntity\")";

		String deleteWhereParts = ".where(expMap($($(\"jdbc_tool_test_fullCaseMainEntity.StringColumn = '9001'\").AND(\"jdbc_tool_test_fullCaseMainEntity.IntColumn = 8001\")).AND($(\"jdbc_tool_test_fullCaseMainEntity.NStringColumn = :jdbc_tool_test_fullCaseMainEntity.NStringColumn\").AND(\"jdbc_tool_test_fullCaseMainEntity.DateTimeColumn = :jdbc_tool_test_fullCaseMainEntity.DateTimeColumn\").AND(\"jdbc_tool_test_fullCaseMainEntity.CompanyCD = $TRIM(:jdbc_tool_test_fullCaseMainEntity.CompanyCD)\")), whereParams))";

		assertEquals(whereParamsParts + "int count = " + deleteApiStr + "" + System.lineSeparator() + deleteWhereParts + System.lineSeparator()+ ".execute();", result.getData().get(3).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE)));

		String deleteExecutePKWhereParts = "GRecord whereParams = createRecord();" + System.lineSeparator() +
				"whereParams.setObject(\"objectID\",);" + System.lineSeparator();


		String deleteExecuteListPKWhereParts = deleteExecutePKWhereParts +
				"List<GRecord> whereParamsList = new ArrayList<>();" + System.lineSeparator() +
				"whereParamsList.add(whereParams);" + System.lineSeparator();

		String deleteExecuteUKWhereParts = "GRecord whereParams = createRecord();" + System.lineSeparator() +
				"whereParams.setObject(\"StringColumn\",);" + System.lineSeparator() +
				"whereParams.setObject(\"IntColumn\",);" + System.lineSeparator() +
				"whereParams.setObject(\"NStringColumn\",);" + System.lineSeparator() +
				"//ユーニックキー「OtherUKName」用のカラム" + System.lineSeparator() +
				"/*whereParams.setObject(\"IntColumn\",);" + System.lineSeparator() +
				"whereParams.setObject(\"NStringColumn\",);" + System.lineSeparator() +
				"whereParams.setObject(\"DateTimeColumn\",);" + System.lineSeparator() +
				"whereParams.setObject(\"YMColumn\",);" + System.lineSeparator() +
				"*/" + System.lineSeparator() +
				"//ユーニックキー「SingleColumnUK」用のカラム" + System.lineSeparator() +
				"/*whereParams.setObject(\"StringColumn\",);" + System.lineSeparator() +
				"*/" + System.lineSeparator();
		String deleteExecuteListUKWhereParts = deleteExecuteUKWhereParts +
				"List<GRecord> whereParamsList = new ArrayList<>();" + System.lineSeparator() +
				"whereParamsList.add(whereParams);" + System.lineSeparator();

		assertEquals(deleteExecutePKWhereParts + "" + System.lineSeparator() + "int count = " + deleteApiStr + System.lineSeparator()+ ".wherePK(whereParams)" + System.lineSeparator()+ ".execute();", result.getData().get(3).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_PK)));
		assertEquals(deleteExecuteUKWhereParts + "" + System.lineSeparator() + "int count = " + deleteApiStr + System.lineSeparator()+ ".whereUK(\"MultipleColumnUK\", whereParams)" + System.lineSeparator() + "//.whereUK(\"OtherUKName\", whereParams)" + System.lineSeparator() + "//.whereUK(\"SingleColumnUK\", whereParams)" + System.lineSeparator()+ ".execute();", result.getData().get(3).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_UK)));

		assertEquals(deleteExecuteListPKWhereParts + "" + System.lineSeparator() + "int count = " + deleteApiStr + System.lineSeparator()+ ".wherePK(whereParamsList)" + System.lineSeparator()+ ".executeList();", result.getData().get(3).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_PK)));
		assertEquals(deleteExecuteListUKWhereParts + "" + System.lineSeparator() + "int count = " + deleteApiStr + System.lineSeparator()+ ".whereUK(\"MultipleColumnUK\", whereParamsList)" + System.lineSeparator() + "//.whereUK(\"OtherUKName\", whereParamsList)" + System.lineSeparator() + "//.whereUK(\"SingleColumnUK\", whereParamsList)" + System.lineSeparator()+ ".executeList();", result.getData().get(3).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_UK)));

		assertEquals(deleteExecuteListPKWhereParts + "" + System.lineSeparator() + "int count = " + deleteApiStr + System.lineSeparator()+ ".wherePK(whereParamsList)" + System.lineSeparator()+ ".executeBatch();", result.getData().get(3).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_PK)));
		assertEquals(deleteExecuteListUKWhereParts + "" + System.lineSeparator() + "int count = " + deleteApiStr + System.lineSeparator()+ ".whereUK(\"MultipleColumnUK\", whereParamsList)" + System.lineSeparator() + "//.whereUK(\"OtherUKName\", whereParamsList)" + System.lineSeparator() + "//.whereUK(\"SingleColumnUK\", whereParamsList)" + System.lineSeparator()+ ".executeBatch();", result.getData().get(3).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_UK)));
	}

	@Test
	public void testExport_join_mix_useForeignKeyDefault() {
		doCustomConfig(getUseForeignKeyDefaultConfigPath());
		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_join_mix.xml");
		String conditionFilePath = getConditionFilePath("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_join_mix.yaml");
		GExporterResult<List<GXFormCodeResult>, byte[]> result = service.export(conditionFilePath, settingsFilePath);

		assertEquals("html", result.getMediaType());
		assertEquals(1, result.getData().size());
		assertEquals(GCrudType.SELECT, result.getData().get(0).getType());
		assertEquals("仮想表ID: E44ADEF7B30549449EDF193ECFB66631", result.getData().get(0).getDescription());
		assertTrue(assertSqlWithJoin("SELECT" + System.lineSeparator() + 
						"    jdbc_tool_test_pk_uk_mainmaster.objectID," + System.lineSeparator() +
						"    jdbc_tool_test_pk_uk_mainmaster.ItemCD," + System.lineSeparator() +
						"    jdbc_tool_test_pk_uk_mainmaster.ItemNA," + System.lineSeparator() +
						"    jdbc_tool_test_pk_uk_mainmaster.UnitPrice," + System.lineSeparator() +
						"    jdbc_tool_test_pk_uk_mainmaster.SellPrice," + System.lineSeparator() +
						"    jdbc_tool_test_pk_uk_mainmaster.Flag," + System.lineSeparator() +
						"    jdbc_tool_test_pk_uk_mainmaster.PriceFlag," + System.lineSeparator() +
						"    jdbc_tool_test_pk_uk_mainmaster.ExclusiveFG," + System.lineSeparator() +
						"    jdbc_tool_test_pk_uk_mainmaster.RegisteredDT," + System.lineSeparator() +
						"    jdbc_tool_test_pk_uk_mainmaster.UpdatedDT" + System.lineSeparator() +
						"FROM" + System.lineSeparator() +
						"    " + dbSchema + ".jdbc_tool_test_pk_uk_mainmaster jdbc_tool_test_pk_uk_mainmaster" + System.lineSeparator() +
						"${join}" +
						"WHERE" + System.lineSeparator() +
						"    jdbc_tool_test_pk_uk_mainmaster.objectID = ?",
				List.of("    LEFT OUTER JOIN " + dbSchema + ".jdbc_tool_test_unitmaster jdbc_tool_test_unitmaster ON jdbc_tool_test_pk_uk_mainmaster.objectID = jdbc_tool_test_unitmaster.Code" + System.lineSeparator(),
						"    INNER JOIN " + dbSchema + ".jdbc_tool_test_mainmaster jdbc_tool_test_mainmaster ON jdbc_tool_test_pk_uk_mainmaster.objectID = jdbc_tool_test_mainmaster.objectID" + System.lineSeparator(),
						"    RIGHT OUTER JOIN " + dbSchema + ".jdbc_tool_test_ordermaster jdbc_tool_test_ordermaster ON jdbc_tool_test_pk_uk_mainmaster.ItemCD = jdbc_tool_test_ordermaster.objectID" + System.lineSeparator()
				), result.getData().get(0).getSqlScript()));
		String whereParamsParts = "Object[] whereParams = new Object[]{};" + System.lineSeparator();
		String groupByApiStr = "select(\"jdbc_tool_test_pk_uk_mainmaster\", \"jdbc_tool_test_pk_uk_mainmaster\")" + System.lineSeparator() +
				".fields(colsAll())" + System.lineSeparator() +
				"${join}" +
				".where(exp($(\"jdbc_tool_test_pk_uk_mainmaster.objectID = ?\"), whereParams))";
		List<String> joins = List.of(
				".leftOuterJoin(\"jdbc_tool_test_unitmaster\", \"jdbc_tool_test_unitmaster\", exp($(\"jdbc_tool_test_pk_uk_mainmaster.objectID = jdbc_tool_test_unitmaster.Code\")))" + System.lineSeparator(),
				".innerJoin(\"jdbc_tool_test_mainmaster\", \"jdbc_tool_test_mainmaster\", exp($(\"jdbc_tool_test_pk_uk_mainmaster.objectID = jdbc_tool_test_mainmaster.objectID\")))" + System.lineSeparator(),
				".rightOuterJoin(\"jdbc_tool_test_ordermaster\", \"jdbc_tool_test_ordermaster\", exp($(\"jdbc_tool_test_pk_uk_mainmaster.ItemCD = jdbc_tool_test_ordermaster.objectID\")))" + System.lineSeparator()
		);

		assertTrue(assertSqlWithJoin(whereParamsParts + "List<GRecord> result = " + groupByApiStr + System.lineSeparator()+ ".findList();", joins, result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE))));
		assertTrue(assertSqlWithJoin(whereParamsParts + "GRecord result = " + groupByApiStr + System.lineSeparator()+ ".findRecord();", joins, result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE))));
		assertTrue(assertSqlWithJoin(whereParamsParts + "GRecordSet result = " + groupByApiStr + System.lineSeparator()+ ".findRecordSet();", joins, result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE))));
	}

	@Test
	public void testExport_searchExpWhereParts_JoinColumn_optionTrim() {

		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_searchExpWhereParts_JoinColumn_optionTrim.xml");
		String conditionFilePath = getConditionFilePath("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_searchExpWhereparts_joincolumn_optionTrim.yaml");
		GExporterResult<List<GXFormCodeResult>, byte[]> result = service.export(conditionFilePath, settingsFilePath);

		assertEquals("html", result.getMediaType());
		assertEquals(1, result.getData().size());
		assertEquals(GCrudType.SELECT, result.getData().get(0).getType());
		assertEquals("仮想表ID: 940E79601B9143D687A8500F9915F7D8", result.getData().get(0).getDescription());
		assertTrue(assertSqlWithJoin("SELECT" + System.lineSeparator() +
						"    jdbc_tool_test_pk_uk_mainmaster.objectID," + System.lineSeparator() +
						"    jdbc_tool_test_pk_uk_mainmaster.ItemCD," + System.lineSeparator() +
						"    jdbc_tool_test_pk_uk_mainmaster.ItemNA," + System.lineSeparator() +
						"    jdbc_tool_test_pk_uk_mainmaster.UnitPrice," + System.lineSeparator() +
						"    jdbc_tool_test_pk_uk_mainmaster.SellPrice," + System.lineSeparator() +
						"    jdbc_tool_test_pk_uk_mainmaster.Flag," + System.lineSeparator() +
						"    jdbc_tool_test_pk_uk_mainmaster.PriceFlag," + System.lineSeparator() +
						"    jdbc_tool_test_pk_uk_mainmaster.ExclusiveFG," + System.lineSeparator() +
						"    jdbc_tool_test_pk_uk_mainmaster.RegisteredDT," + System.lineSeparator() +
						"    jdbc_tool_test_pk_uk_mainmaster.UpdatedDT," + System.lineSeparator() +
						"    jdbc_tool_test_mainmaster.objectID," + System.lineSeparator() +
						"    jdbc_tool_test_unitmaster.Code," + System.lineSeparator() +
						"    jdbc_tool_test_ordermaster.SlipNO," + System.lineSeparator() +
						"    jdbc_tool_test_mainmaster.SellPrice" + System.lineSeparator() +
						"FROM" + System.lineSeparator() +
						"    " + dbSchema + ".jdbc_tool_test_pk_uk_mainmaster jdbc_tool_test_pk_uk_mainmaster" + System.lineSeparator() +
						"${join}" +
						"WHERE" + System.lineSeparator() +
						"    (" + System.lineSeparator() +
						"        jdbc_tool_test_pk_uk_mainmaster.objectID = ?" + System.lineSeparator() +
						"        AND jdbc_tool_test_unitmaster.Code = ?" + System.lineSeparator() +
						"        AND jdbc_tool_test_mainmaster.objectID = ?" + System.lineSeparator() +
						"        AND jdbc_tool_test_ordermaster.SlipNO LIKE ? ESCAPE '/'" + System.lineSeparator() +
						"        AND jdbc_tool_test_pk_uk_mainmaster.ItemCD LIKE ? ESCAPE '/'" + System.lineSeparator() +
						"        AND jdbc_tool_test_pk_uk_mainmaster.ItemNA LIKE ? ESCAPE '/'" + System.lineSeparator() +
						"    )"
						, List.of("    LEFT OUTER JOIN " + dbSchema + ".jdbc_tool_test_unitmaster jdbc_tool_test_unitmaster ON jdbc_tool_test_pk_uk_mainmaster.objectID = jdbc_tool_test_unitmaster.Code" + System.lineSeparator(),
						"    INNER JOIN " + dbSchema + ".jdbc_tool_test_mainmaster jdbc_tool_test_mainmaster ON jdbc_tool_test_pk_uk_mainmaster.objectID = jdbc_tool_test_mainmaster.objectID" + System.lineSeparator(),
						"    RIGHT OUTER JOIN " + dbSchema + ".jdbc_tool_test_ordermaster jdbc_tool_test_ordermaster ON jdbc_tool_test_pk_uk_mainmaster.ItemCD = jdbc_tool_test_ordermaster.objectID" + System.lineSeparator()
				), result.getData().get(0).getSqlScript()));

		String whereParamsParts = "Map<String, Object> whereParams = new HashMap<>();" + System.lineSeparator() +
				"whereParams.put(\"jdbc_tool_test_unitmaster.Code\",);" + System.lineSeparator() +
				"whereParams.put(\"jdbc_tool_test_pk_uk_mainmaster.ItemCD\",);" + System.lineSeparator() +
				"whereParams.put(\"jdbc_tool_test_pk_uk_mainmaster.ItemNA\",);" + System.lineSeparator() +
				"whereParams.put(\"jdbc_tool_test_pk_uk_mainmaster.objectID\",);" + System.lineSeparator() +
				"whereParams.put(\"jdbc_tool_test_ordermaster.SlipNO\",);" + System.lineSeparator() +
				"whereParams.put(\"jdbc_tool_test_mainmaster.objectID\",);" + System.lineSeparator();
		String groupByApiStr = "select(\"jdbc_tool_test_pk_uk_mainmaster\", \"jdbc_tool_test_pk_uk_mainmaster\")" + System.lineSeparator() +
				".fields(col(\"jdbc_tool_test_pk_uk_mainmaster.objectID\")," +
				"col(\"jdbc_tool_test_pk_uk_mainmaster.ItemCD\")," +
				"col(\"jdbc_tool_test_pk_uk_mainmaster.ItemNA\")," +
				"col(\"jdbc_tool_test_pk_uk_mainmaster.UnitPrice\")," +
				"col(\"jdbc_tool_test_pk_uk_mainmaster.SellPrice\")," +
				"col(\"jdbc_tool_test_pk_uk_mainmaster.Flag\")," +
				"col(\"jdbc_tool_test_pk_uk_mainmaster.PriceFlag\")," +
				"col(\"jdbc_tool_test_pk_uk_mainmaster.ExclusiveFG\")," +
				"col(\"jdbc_tool_test_pk_uk_mainmaster.RegisteredDT\")," +
				"col(\"jdbc_tool_test_pk_uk_mainmaster.UpdatedDT\")," +
				"col(\"jdbc_tool_test_mainmaster.objectID\")," +
				"col(\"jdbc_tool_test_unitmaster.Code\")," +
				"col(\"jdbc_tool_test_ordermaster.SlipNO\")," +
				"col(\"jdbc_tool_test_mainmaster.SellPrice\"))" + System.lineSeparator() +
				"${join}" +
				".where(expMap($($(\"jdbc_tool_test_pk_uk_mainmaster.objectID = [:jdbc_tool_test_pk_uk_mainmaster.objectID]\").AND(\"jdbc_tool_test_unitmaster.Code = $TRIM(:jdbc_tool_test_unitmaster.Code)\").AND(\"jdbc_tool_test_mainmaster.objectID = $TRIM([:jdbc_tool_test_mainmaster.objectID])\").AND(\"jdbc_tool_test_ordermaster.SlipNO LIKE $CONCAT(:jdbc_tool_test_ordermaster.SlipNO, '%') ESCAPE '/'\").AND(\"jdbc_tool_test_pk_uk_mainmaster.ItemCD LIKE $CONCAT('%', $CONCAT(:jdbc_tool_test_pk_uk_mainmaster.ItemCD, '%')) ESCAPE '/'\").AND(\"jdbc_tool_test_pk_uk_mainmaster.ItemNA LIKE $CONCAT('%', :jdbc_tool_test_pk_uk_mainmaster.ItemNA) ESCAPE '/'\")), whereParams))";

		List<String> joins = List.of(".leftOuterJoinFK(\"FK_UnitMaster\", \"jdbc_tool_test_unitmaster\")" + System.lineSeparator(),
				".innerJoinFK(\"FK_MainMaster\", \"jdbc_tool_test_mainmaster\")" + System.lineSeparator(),
				".rightOuterJoinFK(\"FK_OrderMaster\", \"jdbc_tool_test_ordermaster\")" + System.lineSeparator());
		assertTrue(assertSqlWithJoin(whereParamsParts + "List<GRecord> result = " + groupByApiStr + System.lineSeparator()+ ".findList();", joins,result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE))));
		assertTrue(assertSqlWithJoin(whereParamsParts + "GRecord result = " + groupByApiStr + System.lineSeparator()+ ".findRecord();", joins, result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE))));
		assertTrue(assertSqlWithJoin(whereParamsParts + "GRecordSet result = " + groupByApiStr + System.lineSeparator()+ ".findRecordSet();", joins, result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE))));
	}

	private boolean assertSqlWithJoin(String sql, List<String> joins, String target) {
		List<String> sqlPatterns = getSqlPatterns(sql, joins);
		for (String sqlPattern : sqlPatterns) {
			if (sqlPattern.equals(target)) {
				return true;
			}
		}
		return false;
	}

	private List<String> getSqlPatterns(String sql, List<String> joins) {
		List<String> joinPermutations = getJoinPermutations(joins);
		List<String> sqlPatterns = new ArrayList<>();
		for (String join : joinPermutations) {
			sqlPatterns.add(sql.replace("${join}", join));
		}
		return sqlPatterns;
	}

	private List<String> getJoinPermutations(List<String> joinList) {
		List<List<String>> joinPermutationsResult = getPermutations(joinList);
		List<String> joinPermutations = new ArrayList<>();
		for (List<String> joins : joinPermutationsResult) {
			String result = "";
			for (String join : joins) {
				result += join;
			}
			joinPermutations.add(result);
		}
		return joinPermutations;
	}

	public <T> List<List<T>> getPermutations(List<T> list) {
		List<List<T>> result = new ArrayList<>();
		if (list.size() == 0) {
			result.add(new ArrayList<>());
			return result;
		}
		T firstElement = list.get(0);
		List<T> remainingElements = list.subList(1, list.size());
		List<List<T>> permutationsOfRest = getPermutations(remainingElements);

		for (List<T> perm : permutationsOfRest) {
			for (int i = 0; i <= perm.size(); i++) {
				List<T> permCopy = new ArrayList<>(perm);
				permCopy.add(i, firstElement);
				result.add(permCopy);
			}
		}
		return result;
	}
	
	protected String getDeleteSql_testExport_expMapCase1() {
		return "DELETE FROM" + System.lineSeparator() +
				"    " + dbSchema + ".jdbc_tool_test_fullCaseMainEntity jdbc_tool_test_fullCaseMainEntity" + System.lineSeparator() +
				"WHERE" + System.lineSeparator() +
				"    (" + System.lineSeparator() +
				"        (" + System.lineSeparator() +
				"            jdbc_tool_test_fullCaseMainEntity.objectID = '1001'" + System.lineSeparator() +
				"            AND jdbc_tool_test_fullCaseMainEntity.StringColumn = '2002'" + System.lineSeparator() +
				"        )" + System.lineSeparator() +
				"        OR jdbc_tool_test_fullCaseMainEntity.IntColumn = 3003" + System.lineSeparator() +
				"    )" + System.lineSeparator() +
				"    AND (" + System.lineSeparator() +
				"        (" + System.lineSeparator() +
				"            jdbc_tool_test_fullCaseMainEntity.NStringColumn = ?" + System.lineSeparator() +
				"            AND jdbc_tool_test_fullCaseMainEntity.DateTimeColumn = ?" + System.lineSeparator() +
				"        )" + System.lineSeparator() +
				"        OR jdbc_tool_test_fullCaseMainEntity.YMColumn = ?" + System.lineSeparator() +
				"        AND jdbc_tool_test_fullCaseMainEntity.CompanyCD = ?" + System.lineSeparator() +
				"    )";
	}
	protected String getDeleteSql_testExport_expMapCase2() {
		return "DELETE FROM" + System.lineSeparator() +
				"    " + dbSchema + ".jdbc_tool_test_fullCaseMainEntity jdbc_tool_test_fullCaseMainEntity" + System.lineSeparator() +
				"WHERE" + System.lineSeparator() +
				"    (" + System.lineSeparator() +
				"        (" + System.lineSeparator() +
				"            jdbc_tool_test_fullCaseMainEntity.objectID = '1001'" + System.lineSeparator() +
				"            AND jdbc_tool_test_fullCaseMainEntity.StringColumn = '2002'" + System.lineSeparator() +
				"        )" + System.lineSeparator() +
				"        OR (" + System.lineSeparator() +
				"            jdbc_tool_test_fullCaseMainEntity.IntColumn = 3003" + System.lineSeparator() +
				"            AND jdbc_tool_test_fullCaseMainEntity.NStringColumn = '4004'" + System.lineSeparator() +
				"        )" + System.lineSeparator() +
				"    )" + System.lineSeparator() +
				"    AND (" + System.lineSeparator() +
				"        (" + System.lineSeparator() +
				"            jdbc_tool_test_fullCaseMainEntity.DateTimeColumn = ?" + System.lineSeparator() +
				"            AND jdbc_tool_test_fullCaseMainEntity.YMColumn = ?" + System.lineSeparator() +
				"        )" + System.lineSeparator() +
				"        OR (" + System.lineSeparator() +
				"            jdbc_tool_test_fullCaseMainEntity.CurrencyColumn = ?" + System.lineSeparator() +
				"            AND jdbc_tool_test_fullCaseMainEntity.LongColumn = ?" + System.lineSeparator() +
				"        )" + System.lineSeparator() +
				"        AND jdbc_tool_test_fullCaseMainEntity.CompanyCD = ?" + System.lineSeparator() +
				"    )";
	}
	protected String getDeleteSql_testExport_expMapCase3() {
		return "DELETE FROM" + System.lineSeparator() +
				"    " + dbSchema + ".jdbc_tool_test_fullCaseMainEntity jdbc_tool_test_fullCaseMainEntity" + System.lineSeparator() +
				"WHERE" + System.lineSeparator() +
				"    (" + System.lineSeparator() +
				"        (" + System.lineSeparator() +
				"            jdbc_tool_test_fullCaseMainEntity.objectID = '1001'" + System.lineSeparator() +
				"            AND jdbc_tool_test_fullCaseMainEntity.StringColumn = '2002'" + System.lineSeparator() +
				"        )" + System.lineSeparator() +
				"        OR (" + System.lineSeparator() +
				"            jdbc_tool_test_fullCaseMainEntity.IntColumn = 3003" + System.lineSeparator() +
				"            AND jdbc_tool_test_fullCaseMainEntity.NStringColumn = '4004'" + System.lineSeparator() +
				"            OR (" + System.lineSeparator() +
				"                jdbc_tool_test_fullCaseMainEntity.DateTimeColumn = '5005'" + System.lineSeparator() +
				"                AND jdbc_tool_test_fullCaseMainEntity.YMColumn = '6006'" + System.lineSeparator() +
				"            )" + System.lineSeparator() +
				"        )" + System.lineSeparator() +
				"    )" + System.lineSeparator() +
				"    AND (" + System.lineSeparator() +
				"        (" + System.lineSeparator() +
				"            jdbc_tool_test_fullCaseMainEntity.CurrencyColumn = ?" + System.lineSeparator() +
				"            AND jdbc_tool_test_fullCaseMainEntity.LongColumn = ?" + System.lineSeparator() +
				"        )" + System.lineSeparator() +
				"        OR (" + System.lineSeparator() +
				"            jdbc_tool_test_fullCaseMainEntity.CompanyCD = ?" + System.lineSeparator() +
				"            AND jdbc_tool_test_fullCaseMainEntity.ExclusiveFG = ?" + System.lineSeparator() +
				"            OR (" + System.lineSeparator() +
				"                jdbc_tool_test_fullCaseMainEntity.RegisteredPerson = ?" + System.lineSeparator() +
				"                AND jdbc_tool_test_fullCaseMainEntity.RegisteredDT = ?" + System.lineSeparator() +
				"            )" + System.lineSeparator() +
				"        )" + System.lineSeparator() +
				"    )";
	}
	protected String getDeleteSql_testExport_expMapCase4() {
		return "DELETE FROM" + System.lineSeparator() +
				"    " + dbSchema + ".jdbc_tool_test_fullCaseMainEntity jdbc_tool_test_fullCaseMainEntity" + System.lineSeparator() +
				"WHERE" + System.lineSeparator() +
				"    (" + System.lineSeparator() +
				"        (" + System.lineSeparator() +
				"            jdbc_tool_test_fullCaseMainEntity.objectID = ?" + System.lineSeparator() +
				"            AND jdbc_tool_test_fullCaseMainEntity.StringColumn = ?" + System.lineSeparator() +
				"        )" + System.lineSeparator() +
				"        OR jdbc_tool_test_fullCaseMainEntity.IntColumn = ?" + System.lineSeparator() +
				"        AND jdbc_tool_test_fullCaseMainEntity.NStringColumn = ?" + System.lineSeparator() +
				"    )";
	}
}
