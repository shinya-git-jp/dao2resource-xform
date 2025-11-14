package jp.co.kccs.greenearth.xform.dao.integration;

import jp.co.kccs.greenearth.xform.code.jdbc.core.GCompoundApiImpl;
import jp.co.kccs.greenearth.xform.code.jdbc.core.GTerminatorApi;
import jp.co.kccs.greenearth.xform.code.jdbc.core.GWhereApi;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class Db2CodeMainTest extends CodeMainTest {

	@Override
	protected String getCommonSettingPath() {
		return "inputFile/settings_db2.yaml";
	}

	@Override
	public String getDeleteSql_WherePartsPK_reg_delete() {
		return "DELETE FROM\n" +
				"    " + dbSchema + ".jdbc_tool_test_pk_uk_mainmaster jdbc_tool_test_pk_uk_mainmaster\n" +
				"WHERE\n" +
				"    jdbc_tool_test_pk_uk_mainmaster.objectID = ?";
	}

	@Override
	public String getDeleteSql_wherePartsUK_reg_delete() {
		return "DELETE FROM\n" +
				"    " + dbSchema + ".jdbc_tool_test_pk_uk_mainmaster jdbc_tool_test_pk_uk_mainmaster\n" +
				"WHERE\n" +
				"    (\n" +
				"        jdbc_tool_test_pk_uk_mainmaster.ItemCD = ?\n" +
				"        AND jdbc_tool_test_pk_uk_mainmaster.ItemNA = ?\n" +
				"    )";
	}

	@Override
	public String getDeleteSql_wherePartsPKAndUK_reg_delete() {
		return "DELETE FROM\n" +
				"    " + dbSchema + ".jdbc_tool_test_pk_uk_mainmaster jdbc_tool_test_pk_uk_mainmaster\n" +
				"WHERE\n" +
				"    (\n" +
				"        jdbc_tool_test_pk_uk_mainmaster.ItemCD = ?\n" +
				"        AND jdbc_tool_test_pk_uk_mainmaster.ItemNA = ?\n" +
				"        AND jdbc_tool_test_pk_uk_mainmaster.objectID = ?\n" +
				"    )";
	}

	@Override
	public String getDeleteSql_wherePartsNotCondition_reg_delete() {
		return "DELETE FROM\n" +
				"    " + dbSchema + ".jdbc_tool_test_pk_uk_mainmaster jdbc_tool_test_pk_uk_mainmaster";
	}

	@Override
	public String getDeleteSql_wherePartsNotPKOrUK_reg_delete() {
		return "DELETE FROM\n" +
				"    " + dbSchema + ".jdbc_tool_test_pk_uk_mainmaster jdbc_tool_test_pk_uk_mainmaster\n" +
				"WHERE\n" +
				"    jdbc_tool_test_pk_uk_mainmaster.UnitPrice = ?";
	}

	@Override
	public String getDeleteSql_searchExpWherePartsLikeF_reg_delete() {
		return "DELETE FROM\n" +
				"    " + dbSchema + ".jdbc_tool_test_mainmaster jdbc_tool_test_mainmaster\n" +
				"WHERE\n" +
				"    jdbc_tool_test_mainmaster.ItemCD LIKE ? ESCAPE '/'";
	}

	@Override
	public String getDeleteSql_searchExpWherePartsLikeFB_reg_delete() {
		return "DELETE FROM\n" +
				"    " + dbSchema + ".jdbc_tool_test_unitmaster jdbc_tool_test_unitmaster\n" +
				"WHERE\n" +
				"    jdbc_tool_test_unitmaster.Name LIKE ? ESCAPE '/'";
	}

	@Override
	public String getDeleteSql_searchExpWherePartsLikeB_reg_delete() {
		return "DELETE FROM\n" +
				"    " + dbSchema + ".jdbc_tool_test_ordermaster jdbc_tool_test_ordermaster\n" +
				"WHERE\n" +
				"    jdbc_tool_test_ordermaster.CorporateNA LIKE ? ESCAPE '/'";
	}

	@Override
	public String getDeleteSql_searchExpWherePartsEquals_reg_delete() {
		return "DELETE FROM\n" +
				"    " + dbSchema + ".jdbc_tool_test_mainmaster jdbc_tool_test_mainmaster\n" +
				"WHERE\n" +
				"    (\n" +
				"        jdbc_tool_test_mainmaster.ItemCD = ?\n" +
				"        AND jdbc_tool_test_mainmaster.UnitPrice <> ?\n" +
				"    )";
	}

	@Override
	public String getDeleteSql_searchExpWherePartsCompare_reg_delete() {
		return "DELETE FROM\n" +
				"    " + dbSchema + ".jdbc_tool_test_mainmaster jdbc_tool_test_mainmaster\n" +
				"WHERE\n" +
				"    (\n" +
				"        jdbc_tool_test_mainmaster.UnitPrice > ?\n" +
				"        AND jdbc_tool_test_mainmaster.SellPrice < ?\n" +
				"        AND jdbc_tool_test_mainmaster.Flag <= ?\n" +
				"        AND jdbc_tool_test_mainmaster.PriceFlag >= ?\n" +
				"    )";
	}

	@Override
	public String getDeleteSql_searchExpWherePartsOptionTrim_reg_delete() {
		return "DELETE FROM\n" +
				"    " + dbSchema + ".jdbc_tool_test_mainmaster jdbc_tool_test_mainmaster\n" +
				"WHERE\n" +
				"    jdbc_tool_test_mainmaster.UnitPrice IN (100, 101, 102)\n" +
				"    AND (\n" +
				"        jdbc_tool_test_mainmaster.objectID LIKE ? ESCAPE '/'\n" +
				"        AND jdbc_tool_test_mainmaster.UnitPrice >= ?\n" +
				"    )";
	}

	@Override
	public String getDeleteSql_filterExpWherePartsLikeF_reg_delete() {
		return "DELETE FROM\n" +
				"    " + dbSchema + ".jdbc_tool_test_mainmaster jdbc_tool_test_mainmaster\n" +
				"WHERE\n" +
				"    jdbc_tool_test_mainmaster.ItemCD LIKE 'item%'";
	}

	@Override
	public String getDeleteSql_filterExpWherePartsLikeFB_reg_delete() {
		return "DELETE FROM\n" +
				"    " + dbSchema + ".jdbc_tool_test_unitmaster jdbc_tool_test_unitmaster\n" +
				"WHERE\n" +
				"    jdbc_tool_test_unitmaster.Name LIKE '%name%'";
	}

	@Override
	public String getDeleteSql_filterExpWherePartsLikeB_reg_delete() {
		return "DELETE FROM\n" +
				"    " + dbSchema + ".jdbc_tool_test_ordermaster jdbc_tool_test_ordermaster\n" +
				"WHERE\n" +
				"    jdbc_tool_test_ordermaster.CorporateNA LIKE '%na'";
	}

	@Override
	public String getDeleteSql_filterExpWherePartsEquals_reg_delete() {
		return "DELETE FROM\n" +
				"    " + dbSchema + ".jdbc_tool_test_mainmaster jdbc_tool_test_mainmaster\n" +
				"WHERE\n" +
				"    (\n" +
				"        jdbc_tool_test_mainmaster.objectID = '1001'\n" +
				"        AND jdbc_tool_test_mainmaster.ItemCD <> '9001'\n" +
				"    )";
	}

	@Override
	public String getDeleteSql_filterExpWherePartsCompare_reg_delete() {
		return "DELETE FROM\n" +
				"    " + dbSchema + ".jdbc_tool_test_mainmaster jdbc_tool_test_mainmaster\n" +
				"WHERE\n" +
				"    (\n" +
				"        jdbc_tool_test_mainmaster.UnitPrice > 100\n" +
				"        AND jdbc_tool_test_mainmaster.UnitPrice < 500\n" +
				"        AND jdbc_tool_test_mainmaster.SellPrice <= 1000\n" +
				"        AND jdbc_tool_test_mainmaster.SellPrice >= 300\n" +
				"    )";
	}

	@Override
	public String getDeleteSql_filterExpWherePartsNull_reg_delete() {
		return "DELETE FROM\n" +
				"    " + dbSchema + ".jdbc_tool_test_mainmaster jdbc_tool_test_mainmaster\n" +
				"WHERE\n" +
				"    (\n" +
				"        jdbc_tool_test_mainmaster.ItemCD IS NULL\n" +
				"        AND jdbc_tool_test_mainmaster.UnitPrice IS NOT NULL\n" +
				"    )";
	}

	@Override
	public String getDeleteSql_filterExpWherePartsIn_reg_delete() {
		return "DELETE FROM\n" +
				"    " + dbSchema + ".jdbc_tool_test_mainmaster jdbc_tool_test_mainmaster\n" +
				"WHERE\n" +
				"    (\n" +
				"        jdbc_tool_test_mainmaster.ItemCD IN ('item010', '1001')\n" +
				"        AND jdbc_tool_test_mainmaster.UnitPrice NOT IN (10, 20, 30)\n" +
				"    )";
	}

	@Override
	protected String getTimestampCommand() {
		return "CURRENT TIMESTAMP";
	}

	@Override
	public String getDeleteSql_veType_inq() {
		return "DELETE FROM\n" +
				"    " + dbSchema + ".jdbc_tool_test_mainmaster jdbc_tool_test_mainmaster\n" +
				"WHERE\n" +
				"    (\n" +
				"        jdbc_tool_test_mainmaster.ItemCD = ?\n" +
				"        AND jdbc_tool_test_mainmaster.UnitPrice <> ?\n" +
				"    )";
	}

	@Override
	public String getDeleteSql_emptyColumn() {
		return "DELETE FROM\n" +
				"    " + dbSchema + ".jdbc_tool_test_emptyColumn jdbc_tool_test_emptyColumn";
	}

	@Override
	public String getDeleteSql_fullCase() {
		return "DELETE FROM\n" +
				"    " + dbSchema + ".jdbc_tool_test_fullCaseMainEntity jdbc_tool_test_fullCaseMainEntity\n" +
				"WHERE\n" +
				"    (\n" +
				"        jdbc_tool_test_fullCaseMainEntity.StringColumn = '9001'\n" +
				"        AND jdbc_tool_test_fullCaseMainEntity.IntColumn = 8001\n" +
				"    )\n" +
				"    AND (\n" +
				"        jdbc_tool_test_fullCaseMainEntity.NStringColumn = ?\n" +
				"        AND jdbc_tool_test_fullCaseMainEntity.DateTimeColumn = ?\n" +
				"        AND jdbc_tool_test_fullCaseMainEntity.CompanyCD = ?\n" +
				"    )";
	}

	@Override
	public String getDeleteSql_multipleUK() {
		return "DELETE FROM\n" +
				"    " + dbSchema + ".jdbc_tool_test_multipleUK jdbc_tool_test_multipleUK\n" +
				"WHERE\n" +
				"    (\n" +
				"        jdbc_tool_test_multipleUK.objectID = ?\n" +
				"        AND jdbc_tool_test_multipleUK.NStringColumn = ?\n" +
				"    )";
	}

	@Override
	public String getDeleteSql_noPrimaryKey() {
		return "DELETE FROM\n" +
				"    " + dbSchema + ".jdbc_tool_test_noPrimaryKey jdbc_tool_test_noPrimaryKey\n" +
				"WHERE\n" +
				"    jdbc_tool_test_noPrimaryKey.StringColumn = ?";
	}

	@Override
	public String getDeleteSql_noUnionKey() {
		return "DELETE FROM\n" +
				"    " + dbSchema + ".jdbc_tool_test_noUnionKey jdbc_tool_test_noUnionKey\n" +
				"WHERE\n" +
				"    jdbc_tool_test_noUnionKey.StringColumn = ?";
	}

	@Override
	public String getDeleteSql_noPrimaryKeyAndUnionKey() {
		return "DELETE FROM\n" +
				"    " + dbSchema + ".jdbc_tool_test_noPrimaryKeyAndUnionKey jdbc_tool_test_noPrimaryKeyAndUnionKey\n" +
				"WHERE\n" +
				"    jdbc_tool_test_noPrimaryKeyAndUnionKey.StringColumn = ?";
	}

	public void testExport_join_mix_useForeignKeyFalse() {
//		doCustomConfig(getUseForeignKeyFalseConfigPath());
		SettingRequest settingRequest = getCommonSetting();
		settingRequest.getTransform().put("useForeignKey", false);
		settingRequest.getTransform().put("useExpMap", false);
		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_join_mix.xml");
		ApiRequest apiRequest =  parseRequestFromFile(settingRequest,"jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_join_mix.yaml");

		List<CodeResponse> result = convertVe2Code(apiRequest);
		assertEquals("SELECT", result.get(0).getType());
		assertEquals("仮想表ID: E44ADEF7B30549449EDF193ECFB66631", result.get(0).getDescription());
		assertTrue(assertSqlWithJoin("SELECT\n" +
						"    jdbc_tool_test_pk_uk_mainmaster.objectID,\n" +
						"    jdbc_tool_test_pk_uk_mainmaster.ItemCD,\n" +
						"    jdbc_tool_test_pk_uk_mainmaster.ItemNA,\n" +
						"    jdbc_tool_test_pk_uk_mainmaster.UnitPrice,\n" +
						"    jdbc_tool_test_pk_uk_mainmaster.SellPrice,\n" +
						"    jdbc_tool_test_pk_uk_mainmaster.Flag,\n" +
						"    jdbc_tool_test_pk_uk_mainmaster.PriceFlag,\n" +
						"    jdbc_tool_test_pk_uk_mainmaster.ExclusiveFG,\n" +
						"    jdbc_tool_test_pk_uk_mainmaster.RegisteredDT,\n" +
						"    jdbc_tool_test_pk_uk_mainmaster.UpdatedDT\n" +
						"FROM\n" +
						"    " + dbSchema + ".jdbc_tool_test_pk_uk_mainmaster jdbc_tool_test_pk_uk_mainmaster\n" +
						"${join}"+
						"WHERE\n" +
						"    jdbc_tool_test_pk_uk_mainmaster.objectID = ?",
				List.of("    LEFT OUTER JOIN " + dbSchema + ".jdbc_tool_test_unitmaster jdbc_tool_test_unitmaster ON jdbc_tool_test_pk_uk_mainmaster.objectID = jdbc_tool_test_unitmaster.Code\n",
						"    INNER JOIN " + dbSchema + ".jdbc_tool_test_mainmaster jdbc_tool_test_mainmaster ON jdbc_tool_test_pk_uk_mainmaster.objectID = jdbc_tool_test_mainmaster.objectID\n",
						"    RIGHT OUTER JOIN " + dbSchema + ".jdbc_tool_test_ordermaster jdbc_tool_test_ordermaster ON jdbc_tool_test_pk_uk_mainmaster.ItemCD = jdbc_tool_test_ordermaster.objectID\n"),
				result.get(0).getSqlScript()));

		String whereParamsParts = "Object[] whereParams = new Object[]{};\n";
		String groupByApiStr = "select(\"jdbc_tool_test_pk_uk_mainmaster\", \"jdbc_tool_test_pk_uk_mainmaster\")\n" +
				".fields(colsAll())\n" +
				"${join}" +
				".where(exp($(\"jdbc_tool_test_pk_uk_mainmaster.objectID = ?\"), whereParams))";
		assertTrue(assertSqlWithJoin(
				whereParamsParts + "List<GRecord> result = " + groupByApiStr + "\n" + ".findList();",
				List.of(".leftOuterJoin(\"jdbc_tool_test_unitmaster\", \"jdbc_tool_test_unitmaster\", exp($(\"jdbc_tool_test_pk_uk_mainmaster.objectID = jdbc_tool_test_unitmaster.Code\")))\n",
						".innerJoin(\"jdbc_tool_test_mainmaster\", \"jdbc_tool_test_mainmaster\", exp($(\"jdbc_tool_test_pk_uk_mainmaster.objectID = jdbc_tool_test_mainmaster.objectID\")))\n",
						".rightOuterJoin(\"jdbc_tool_test_ordermaster\", \"jdbc_tool_test_ordermaster\", exp($(\"jdbc_tool_test_pk_uk_mainmaster.ItemCD = jdbc_tool_test_ordermaster.objectID\")))\n"
				),
				result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE).toString())
		));
		assertTrue(assertSqlWithJoin(
				whereParamsParts + "GRecord result = " + groupByApiStr + "\n" + ".findRecord();",
				List.of(".leftOuterJoin(\"jdbc_tool_test_unitmaster\", \"jdbc_tool_test_unitmaster\", exp($(\"jdbc_tool_test_pk_uk_mainmaster.objectID = jdbc_tool_test_unitmaster.Code\")))\n",
						".innerJoin(\"jdbc_tool_test_mainmaster\", \"jdbc_tool_test_mainmaster\", exp($(\"jdbc_tool_test_pk_uk_mainmaster.objectID = jdbc_tool_test_mainmaster.objectID\")))\n",
						".rightOuterJoin(\"jdbc_tool_test_ordermaster\", \"jdbc_tool_test_ordermaster\", exp($(\"jdbc_tool_test_pk_uk_mainmaster.ItemCD = jdbc_tool_test_ordermaster.objectID\")))\n"
				),
				result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE).toString())
		));
		assertTrue(assertSqlWithJoin(
				whereParamsParts + "GRecordSet result = " + groupByApiStr + "\n" + ".findRecordSet();",
				List.of(".leftOuterJoin(\"jdbc_tool_test_unitmaster\", \"jdbc_tool_test_unitmaster\", exp($(\"jdbc_tool_test_pk_uk_mainmaster.objectID = jdbc_tool_test_unitmaster.Code\")))\n",
						".innerJoin(\"jdbc_tool_test_mainmaster\", \"jdbc_tool_test_mainmaster\", exp($(\"jdbc_tool_test_pk_uk_mainmaster.objectID = jdbc_tool_test_mainmaster.objectID\")))\n",
						".rightOuterJoin(\"jdbc_tool_test_ordermaster\", \"jdbc_tool_test_ordermaster\", exp($(\"jdbc_tool_test_pk_uk_mainmaster.ItemCD = jdbc_tool_test_ordermaster.objectID\")))\n"
				),
				result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE).toString())
		));
	}

	@Test
	public void testExport_join_mix() {

		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_join_mix.xml");
		ApiRequest apiRequest =  parseRequestFromFile(getCommonSetting(),"jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_join_mix.yaml");

		List<CodeResponse> result = convertVe2Code(apiRequest);
		
		assertEquals("SELECT", result.get(0).getType());
		assertEquals("仮想表ID: E44ADEF7B30549449EDF193ECFB66631", result.get(0).getDescription());
		assertTrue(assertSqlWithJoin("SELECT\n" +
						"    jdbc_tool_test_pk_uk_mainmaster.objectID,\n" +
						"    jdbc_tool_test_pk_uk_mainmaster.ItemCD,\n" +
						"    jdbc_tool_test_pk_uk_mainmaster.ItemNA,\n" +
						"    jdbc_tool_test_pk_uk_mainmaster.UnitPrice,\n" +
						"    jdbc_tool_test_pk_uk_mainmaster.SellPrice,\n" +
						"    jdbc_tool_test_pk_uk_mainmaster.Flag,\n" +
						"    jdbc_tool_test_pk_uk_mainmaster.PriceFlag,\n" +
						"    jdbc_tool_test_pk_uk_mainmaster.ExclusiveFG,\n" +
						"    jdbc_tool_test_pk_uk_mainmaster.RegisteredDT,\n" +
						"    jdbc_tool_test_pk_uk_mainmaster.UpdatedDT\n" +
						"FROM\n" +
						"    " + dbSchema + ".jdbc_tool_test_pk_uk_mainmaster jdbc_tool_test_pk_uk_mainmaster\n" +
						"${join}" +
						"WHERE\n" +
						"    jdbc_tool_test_pk_uk_mainmaster.objectID = ?",
				List.of("    LEFT OUTER JOIN " + dbSchema + ".jdbc_tool_test_unitmaster jdbc_tool_test_unitmaster ON jdbc_tool_test_pk_uk_mainmaster.objectID = jdbc_tool_test_unitmaster.Code\n",
						"    INNER JOIN " + dbSchema + ".jdbc_tool_test_mainmaster jdbc_tool_test_mainmaster ON jdbc_tool_test_pk_uk_mainmaster.objectID = jdbc_tool_test_mainmaster.objectID\n",
						"    RIGHT OUTER JOIN " + dbSchema + ".jdbc_tool_test_ordermaster jdbc_tool_test_ordermaster ON jdbc_tool_test_pk_uk_mainmaster.ItemCD = jdbc_tool_test_ordermaster.objectID\n"
				),
				result.get(0).getSqlScript()));

		String whereParamsParts = "Map<String, Object> whereParams = new HashMap<>();\n" +
				"whereParams.put(\"jdbc_tool_test_pk_uk_mainmaster.objectID\",);\n";
		String groupByApiStr = "select(\"jdbc_tool_test_pk_uk_mainmaster\", \"jdbc_tool_test_pk_uk_mainmaster\")\n" +
				".fields(colsAll())\n" +
				"${join}" +
				".where(expMap($(\"jdbc_tool_test_pk_uk_mainmaster.objectID = :jdbc_tool_test_pk_uk_mainmaster.objectID\"), whereParams))";
		List<String> joins = List.of(".leftOuterJoinFK(\"FK_UnitMaster\", \"jdbc_tool_test_unitmaster\")\n",
				".innerJoinFK(\"FK_MainMaster\", \"jdbc_tool_test_mainmaster\")\n",
				".rightOuterJoinFK(\"FK_OrderMaster\", \"jdbc_tool_test_ordermaster\")\n");
		assertTrue(assertSqlWithJoin(whereParamsParts + "List<GRecord> result = " + groupByApiStr + "\n" + ".findList();"
				, joins,
				result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE).toString())));

		assertTrue(assertSqlWithJoin(whereParamsParts + "GRecord result = " + groupByApiStr + "\n" + ".findRecord();",
				joins, result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE).toString())));
		assertTrue(assertSqlWithJoin(whereParamsParts + "GRecordSet result = " + groupByApiStr + "\n" + ".findRecordSet();",
				joins, result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE).toString())));
	}

	@Test
	public void testExport_searchExpWhereParts_JoinColumn() {

		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_searchExpWhereParts_JoinColumn.xml");
		ApiRequest apiRequest =  parseRequestFromFile(getCommonSetting(),"jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_searchExpWhereparts_joincolumn.yaml");

		List<CodeResponse> result = convertVe2Code(apiRequest);
		
		assertEquals("SELECT", result.get(0).getType());
		assertEquals("仮想表ID: 0B064A3729EC4D65BB5A663562B96B51", result.get(0).getDescription());

		assertTrue(assertSqlWithJoin("SELECT\n" +
						"    jdbc_tool_test_pk_uk_mainmaster.objectID,\n" +
						"    jdbc_tool_test_pk_uk_mainmaster.ItemCD,\n" +
						"    jdbc_tool_test_pk_uk_mainmaster.ItemNA,\n" +
						"    jdbc_tool_test_pk_uk_mainmaster.UnitPrice,\n" +
						"    jdbc_tool_test_pk_uk_mainmaster.SellPrice,\n" +
						"    jdbc_tool_test_pk_uk_mainmaster.Flag,\n" +
						"    jdbc_tool_test_pk_uk_mainmaster.PriceFlag,\n" +
						"    jdbc_tool_test_pk_uk_mainmaster.ExclusiveFG,\n" +
						"    jdbc_tool_test_pk_uk_mainmaster.RegisteredDT,\n" +
						"    jdbc_tool_test_pk_uk_mainmaster.UpdatedDT,\n" +
						"    jdbc_tool_test_mainmaster.objectID,\n" +
						"    jdbc_tool_test_unitmaster.Code,\n" +
						"    jdbc_tool_test_ordermaster.SlipNO,\n" +
						"    jdbc_tool_test_mainmaster.SellPrice\n" +
						"FROM\n" +
						"    " + dbSchema + ".jdbc_tool_test_pk_uk_mainmaster jdbc_tool_test_pk_uk_mainmaster\n" +
						"${join}" +
						"WHERE\n" +
						"    (\n" +
						"        jdbc_tool_test_pk_uk_mainmaster.objectID = ?\n" +
						"        AND jdbc_tool_test_unitmaster.Code = ?\n" +
						"        AND jdbc_tool_test_mainmaster.objectID = ?\n" +
						"        AND jdbc_tool_test_ordermaster.SlipNO LIKE ? ESCAPE '/'\n" +
						"        AND jdbc_tool_test_pk_uk_mainmaster.ItemCD LIKE ? ESCAPE '/'\n" +
						"        AND jdbc_tool_test_pk_uk_mainmaster.ItemNA LIKE ? ESCAPE '/'\n" +
						"    )",
				List.of("    LEFT OUTER JOIN " + dbSchema + ".jdbc_tool_test_unitmaster jdbc_tool_test_unitmaster ON jdbc_tool_test_pk_uk_mainmaster.objectID = jdbc_tool_test_unitmaster.Code\n",
						"    INNER JOIN " + dbSchema + ".jdbc_tool_test_mainmaster jdbc_tool_test_mainmaster ON jdbc_tool_test_pk_uk_mainmaster.objectID = jdbc_tool_test_mainmaster.objectID\n",
						"    RIGHT OUTER JOIN " + dbSchema + ".jdbc_tool_test_ordermaster jdbc_tool_test_ordermaster ON jdbc_tool_test_pk_uk_mainmaster.ItemCD = jdbc_tool_test_ordermaster.objectID\n"
				), result.get(0).getSqlScript()));

		String whereParamsParts = "Map<String, Object> whereParams = new HashMap<>();\n" +
				"whereParams.put(\"jdbc_tool_test_unitmaster.Code\",);\n" +
				"whereParams.put(\"jdbc_tool_test_pk_uk_mainmaster.ItemCD\",);\n" +
				"whereParams.put(\"jdbc_tool_test_pk_uk_mainmaster.ItemNA\",);\n" +
				"whereParams.put(\"jdbc_tool_test_pk_uk_mainmaster.objectID\",);\n" +
				"whereParams.put(\"jdbc_tool_test_ordermaster.SlipNO\",);\n" +
				"whereParams.put(\"jdbc_tool_test_mainmaster.objectID\",);\n";
		String groupByApiStr = "select(\"jdbc_tool_test_pk_uk_mainmaster\", \"jdbc_tool_test_pk_uk_mainmaster\")\n" +
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
				"col(\"jdbc_tool_test_mainmaster.SellPrice\"))\n" +
				"${join}" +
				".where(expMap($($(\"jdbc_tool_test_pk_uk_mainmaster.objectID = :jdbc_tool_test_pk_uk_mainmaster.objectID\").AND(\"jdbc_tool_test_unitmaster.Code = :jdbc_tool_test_unitmaster.Code\").AND(\"jdbc_tool_test_mainmaster.objectID = :jdbc_tool_test_mainmaster.objectID\").AND(\"jdbc_tool_test_ordermaster.SlipNO LIKE $CONCAT(:jdbc_tool_test_ordermaster.SlipNO, '%') ESCAPE '/'\").AND(\"jdbc_tool_test_pk_uk_mainmaster.ItemCD LIKE $CONCAT('%', $CONCAT(:jdbc_tool_test_pk_uk_mainmaster.ItemCD, '%')) ESCAPE '/'\").AND(\"jdbc_tool_test_pk_uk_mainmaster.ItemNA LIKE $CONCAT('%', :jdbc_tool_test_pk_uk_mainmaster.ItemNA) ESCAPE '/'\")), whereParams))";

		List<String> joins = List.of(".leftOuterJoinFK(\"FK_UnitMaster\", \"jdbc_tool_test_unitmaster\")\n",
				".innerJoinFK(\"FK_MainMaster\", \"jdbc_tool_test_mainmaster\")\n",
				".rightOuterJoinFK(\"FK_OrderMaster\", \"jdbc_tool_test_ordermaster\")\n");
		assertTrue(assertSqlWithJoin(whereParamsParts + "List<GRecord> result = " + groupByApiStr + "\n" + ".findList();", joins, result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE).toString())));
		assertTrue(assertSqlWithJoin(whereParamsParts + "GRecord result = " + groupByApiStr + "\n" + ".findRecord();", joins, result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE).toString())));
		assertTrue(assertSqlWithJoin(whereParamsParts + "GRecordSet result = " + groupByApiStr + "\n" + ".findRecordSet();", joins, result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE).toString())));
	}

	@Test
	public void testExport_filterExpWhereParts_JoinColumn_overrideAlias() {
//		doCustomConfig(getForceAliasColumnConfigPath());

		SettingRequest settingRequest = getCommonSetting();
		settingRequest.getTransform().put("useForeignKey", true);
		settingRequest.getTransform().put("useExpMap", false);
		settingRequest.getTransform().put("forceAliasColumn", true);
		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_filterExpWhereParts_JoinColumn.xml");
		ApiRequest apiRequest =  parseRequestFromFile(settingRequest,"jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_filterExpWhereparts_joincolumn.yaml");

		List<CodeResponse> result = convertVe2Code(apiRequest);
		
		assertEquals("SELECT", result.get(0).getType());
		assertEquals("仮想表ID: 395DA3D2A7C541FDAFF20CC7AF7BBFE8", result.get(0).getDescription());
		assertTrue(assertSqlWithJoin("SELECT\n" +
				"    jdbc_tool_test_pk_uk_mainmaster.objectID,\n" +
				"    jdbc_tool_test_pk_uk_mainmaster.ItemCD,\n" +
				"    jdbc_tool_test_pk_uk_mainmaster.ItemNA,\n" +
				"    jdbc_tool_test_pk_uk_mainmaster.UnitPrice,\n" +
				"    jdbc_tool_test_pk_uk_mainmaster.SellPrice,\n" +
				"    jdbc_tool_test_pk_uk_mainmaster.Flag,\n" +
				"    jdbc_tool_test_pk_uk_mainmaster.PriceFlag,\n" +
				"    jdbc_tool_test_pk_uk_mainmaster.ExclusiveFG,\n" +
				"    jdbc_tool_test_pk_uk_mainmaster.RegisteredDT,\n" +
				"    jdbc_tool_test_pk_uk_mainmaster.UpdatedDT,\n" +
				"    jdbc_tool_test_mainmaster.objectID,\n" +
				"    jdbc_tool_test_unitmaster.Code,\n" +
				"    jdbc_tool_test_ordermaster.SlipNO,\n" +
				"    jdbc_tool_test_mainmaster.SellPrice\n" +
				"FROM\n" +
				"    " + dbSchema + ".jdbc_tool_test_pk_uk_mainmaster jdbc_tool_test_pk_uk_mainmaster\n" +
				"${join}" +
				"WHERE\n" +
				"    (\n" +
				"        jdbc_tool_test_pk_uk_mainmaster.objectID = '1001'\n" +
				"        AND jdbc_tool_test_unitmaster.Code = '1001'\n" +
				"        AND jdbc_tool_test_mainmaster.objectID = '1001'\n" +
				"        AND jdbc_tool_test_ordermaster.SlipNO LIKE '100%'\n" +
				"        AND jdbc_tool_test_pk_uk_mainmaster.ItemCD LIKE '%100%'\n" +
				"        AND jdbc_tool_test_pk_uk_mainmaster.ItemNA LIKE '%gef%'\n" +
				"    )", List.of(
				"    LEFT OUTER JOIN " + dbSchema + ".jdbc_tool_test_unitmaster jdbc_tool_test_unitmaster ON jdbc_tool_test_pk_uk_mainmaster.objectID = jdbc_tool_test_unitmaster.Code\n",
				"    INNER JOIN " + dbSchema + ".jdbc_tool_test_mainmaster jdbc_tool_test_mainmaster ON jdbc_tool_test_pk_uk_mainmaster.objectID = jdbc_tool_test_mainmaster.objectID\n",
				"    RIGHT OUTER JOIN " + dbSchema + ".jdbc_tool_test_ordermaster jdbc_tool_test_ordermaster ON jdbc_tool_test_pk_uk_mainmaster.ItemCD = jdbc_tool_test_ordermaster.objectID\n"
		), result.get(0).getSqlScript()));

		String joinColumnFilterExpApiStr = "select(\"jdbc_tool_test_pk_uk_mainmaster\", \"jdbc_tool_test_pk_uk_mainmaster\")\n" +
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
				"col(\"jdbc_tool_test_mainmaster.SellPrice\", \"MainMasterSellPrice\"))\n" +
				"${join}"+
				".where(exp($($(\"jdbc_tool_test_pk_uk_mainmaster.objectID = '1001'\").AND(\"jdbc_tool_test_unitmaster.Code = '1001'\").AND(\"jdbc_tool_test_mainmaster.objectID = '1001'\").AND(\"jdbc_tool_test_ordermaster.SlipNO LIKE '100%'\").AND(\"jdbc_tool_test_pk_uk_mainmaster.ItemCD LIKE '%100%'\").AND(\"jdbc_tool_test_pk_uk_mainmaster.ItemNA LIKE '%gef%'\"))))";

		List<String> joins = List.of(".leftOuterJoinFK(\"FK_UnitMaster\", \"jdbc_tool_test_unitmaster\")\n",
				".innerJoinFK(\"FK_MainMaster\", \"jdbc_tool_test_mainmaster\")\n",
				".rightOuterJoinFK(\"FK_OrderMaster\", \"jdbc_tool_test_ordermaster\")\n");
		assertTrue(assertSqlWithJoin("List<GRecord> result = " + joinColumnFilterExpApiStr + "\n" + ".findList();", joins, result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE).toString())));
		assertTrue(assertSqlWithJoin("GRecord result = " + joinColumnFilterExpApiStr + "\n" + ".findRecord();", joins, result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE).toString())));
		assertTrue(assertSqlWithJoin("GRecordSet result = " + joinColumnFilterExpApiStr + "\n" + ".findRecordSet();", joins, result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE).toString())));
	}

	@Test
	public void testExport_filterExpWhereParts_JoinColumn() {
		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_filterExpWhereParts_JoinColumn.xml");
		ApiRequest apiRequest =  parseRequestFromFile(getCommonSetting(),"jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_filterExpWhereparts_joincolumn.yaml");

		List<CodeResponse> result = convertVe2Code(apiRequest);
		
		assertEquals("SELECT", result.get(0).getType());
		assertEquals("仮想表ID: 395DA3D2A7C541FDAFF20CC7AF7BBFE8", result.get(0).getDescription());
		assertTrue(assertSqlWithJoin("SELECT\n" +
						"    jdbc_tool_test_pk_uk_mainmaster.objectID,\n" +
						"    jdbc_tool_test_pk_uk_mainmaster.ItemCD,\n" +
						"    jdbc_tool_test_pk_uk_mainmaster.ItemNA,\n" +
						"    jdbc_tool_test_pk_uk_mainmaster.UnitPrice,\n" +
						"    jdbc_tool_test_pk_uk_mainmaster.SellPrice,\n" +
						"    jdbc_tool_test_pk_uk_mainmaster.Flag,\n" +
						"    jdbc_tool_test_pk_uk_mainmaster.PriceFlag,\n" +
						"    jdbc_tool_test_pk_uk_mainmaster.ExclusiveFG,\n" +
						"    jdbc_tool_test_pk_uk_mainmaster.RegisteredDT,\n" +
						"    jdbc_tool_test_pk_uk_mainmaster.UpdatedDT,\n" +
						"    jdbc_tool_test_mainmaster.objectID,\n" +
						"    jdbc_tool_test_unitmaster.Code,\n" +
						"    jdbc_tool_test_ordermaster.SlipNO,\n" +
						"    jdbc_tool_test_mainmaster.SellPrice\n" +
						"FROM\n" +
						"    " + dbSchema + ".jdbc_tool_test_pk_uk_mainmaster jdbc_tool_test_pk_uk_mainmaster\n" +
						"${join}" +
						"WHERE\n" +
						"    (\n" +
						"        jdbc_tool_test_pk_uk_mainmaster.objectID = '1001'\n" +
						"        AND jdbc_tool_test_unitmaster.Code = '1001'\n" +
						"        AND jdbc_tool_test_mainmaster.objectID = '1001'\n" +
						"        AND jdbc_tool_test_ordermaster.SlipNO LIKE '100%'\n" +
						"        AND jdbc_tool_test_pk_uk_mainmaster.ItemCD LIKE '%100%'\n" +
						"        AND jdbc_tool_test_pk_uk_mainmaster.ItemNA LIKE '%gef%'\n" +
						"    )", List.of(
						"    LEFT OUTER JOIN " + dbSchema + ".jdbc_tool_test_unitmaster jdbc_tool_test_unitmaster ON jdbc_tool_test_pk_uk_mainmaster.objectID = jdbc_tool_test_unitmaster.Code\n",
						"    INNER JOIN " + dbSchema + ".jdbc_tool_test_mainmaster jdbc_tool_test_mainmaster ON jdbc_tool_test_pk_uk_mainmaster.objectID = jdbc_tool_test_mainmaster.objectID\n",
						"    RIGHT OUTER JOIN " + dbSchema + ".jdbc_tool_test_ordermaster jdbc_tool_test_ordermaster ON jdbc_tool_test_pk_uk_mainmaster.ItemCD = jdbc_tool_test_ordermaster.objectID\n"),
				result.get(0).getSqlScript()));

		String joinColumnFilterExpApiStr = "select(\"jdbc_tool_test_pk_uk_mainmaster\", \"jdbc_tool_test_pk_uk_mainmaster\")\n" +
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
				"col(\"jdbc_tool_test_mainmaster.SellPrice\"))\n" +
				"${join}" +
				".where(expMap($($(\"jdbc_tool_test_pk_uk_mainmaster.objectID = '1001'\").AND(\"jdbc_tool_test_unitmaster.Code = '1001'\").AND(\"jdbc_tool_test_mainmaster.objectID = '1001'\").AND(\"jdbc_tool_test_ordermaster.SlipNO LIKE '100%'\").AND(\"jdbc_tool_test_pk_uk_mainmaster.ItemCD LIKE '%100%'\").AND(\"jdbc_tool_test_pk_uk_mainmaster.ItemNA LIKE '%gef%'\")), Collections.emptyMap()))";

		List<String> joins = List.of(".leftOuterJoinFK(\"FK_UnitMaster\", \"jdbc_tool_test_unitmaster\")\n",
				".innerJoinFK(\"FK_MainMaster\", \"jdbc_tool_test_mainmaster\")\n",
				".rightOuterJoinFK(\"FK_OrderMaster\", \"jdbc_tool_test_ordermaster\")\n");
		assertTrue(assertSqlWithJoin("List<GRecord> result = " + joinColumnFilterExpApiStr + "\n" + ".findList();", joins, result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE).toString())));
		assertTrue(assertSqlWithJoin("GRecord result = " + joinColumnFilterExpApiStr + "\n" + ".findRecord();", joins, result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE).toString())));
		assertTrue(assertSqlWithJoin("GRecordSet result = " + joinColumnFilterExpApiStr + "\n" + ".findRecordSet();", joins, result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE).toString())));
	}

	@Test
	public void testExport_fullCase() {

		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_fullcase.xml");
		ApiRequest apiRequest =  parseRequestFromFile(getCommonSetting(),"jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_fullcase.yaml");
		List<CodeResponse> result = convertVe2Code(apiRequest);
		
		assertEquals(4, result.size());
		assertEquals("INSERT", result.get(0).getType());
		assertEquals("仮想表ID: 35B6AB911C524EE69A30E29BF9A2904A", result.get(0).getDescription());
		assertEquals("INSERT INTO\n" +
				"    " + dbSchema + ".jdbc_tool_test_fullCaseMainEntity(objectID, StringColumn, IntColumn, NStringColumn, DateTimeColumn, YMColumn, CurrencyColumn, LongColumn, CompanyCD, ExclusiveFG, RegisteredPerson, UpdatedPerson, RegisteredDT, UpdatedDT)\n" +
				"VALUES\n" +
				"    " + "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " + getTimestampCommand() + ", " + getTimestampCommand() + ")", result.get(0).getSqlScript());

		Map<String, Object> insertCodeResult = (result.get(0)).getVirtualEntity();
		assertEquals(19, ((List<Map<String,Object>>)insertCodeResult.get("columns")).size());
		assertEquals("Map<String, Object> absoluteVirtualColumn0 = new HashMap<>();\n" +
				"absoluteVirtualColumn0.put(\"name\", \"virtualVolumn01\");\n" +
				"absoluteVirtualColumn0.put(\"displayName\", \"virtualVolumn01\");\n" +
				"absoluteVirtualColumn0.put(\"fixedValue\", \"virtualVolumn01V\");\n" +
				"absoluteVirtualColumn0.put(\"aggregateFunction\", \"-\");\n" +
				"absoluteVirtualColumn0.put(\"format\", \"virtualVolumn01F\");\n" +
				"Map<String, Object> absoluteVirtualColumn1 = new HashMap<>();\n" +
				"absoluteVirtualColumn1.put(\"name\", \"virtualVolumn02\");\n" +
				"absoluteVirtualColumn1.put(\"displayName\", \"virtualVolumn02\");\n" +
				"absoluteVirtualColumn1.put(\"fixedValue\", \"virtualVolumn02V\");\n" +
				"absoluteVirtualColumn1.put(\"aggregateFunction\", \"-\");\n" +
				"absoluteVirtualColumn1.put(\"format\", \"virtualVolumn02F\");\n", insertCodeResult.get("absoluteVirtualColumnCode"));

		String insertExecuteApiStr = "insert(\"jdbc_tool_test_fullCaseMainEntity\")\n" + ".values(record)";
		String insertRecord = "GRecord record = createRecord();\n" +
				"record.setObject(\"objectID\",);\n" +
				"record.setObject(\"StringColumn\",);\n" +
				"record.setObject(\"IntColumn\",);\n" +
				"record.setObject(\"NStringColumn\",);\n" +
				"record.setObject(\"DateTimeColumn\",);\n" +
				"record.setObject(\"YMColumn\",);\n" +
				"record.setObject(\"CurrencyColumn\",);\n" +
				"record.setObject(\"LongColumn\",);\n" +
				"record.setObject(\"CompanyCD\",);\n" +
				"record.setObject(\"ExclusiveFG\",);\n" +
				"record.setObject(\"RegisteredPerson\",);\n" +
				"record.setObject(\"UpdatedPerson\",);\n" +
				"record.setObject(\"RegisteredDT\",);\n" +
				"record.setObject(\"UpdatedDT\",);\n";

		assertEquals(insertRecord + "\n" + "int count = " + insertExecuteApiStr + "\n" + ".execute();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, null).toString()));

		String insertExecuteBatchApiStr = "insert(\"jdbc_tool_test_fullCaseMainEntity\")\n" + ".values(recordList)";
		StringBuilder insertRecordList = new StringBuilder();
		insertRecordList.append("List<GRecord> recordList = new ArrayList<>();").append("\n");
		insertRecordList.append(insertRecord);
		insertRecordList.append("recordList.add(record);").append("\n");

		assertEquals(insertRecordList + "\n" + "int count = " + insertExecuteBatchApiStr + "\n" + ".executeBatch();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, null).toString()));
		assertEquals(insertRecordList + "\n" + "int count = " + insertExecuteBatchApiStr + "\n" + ".executeList();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, null).toString()));


		assertEquals("UPDATE", result.get(1).getType());
		assertEquals("仮想表ID: 35B6AB911C524EE69A30E29BF9A2904A", result.get(1).getDescription());
		assertEquals("UPDATE\n" +
				"    " + dbSchema + ".jdbc_tool_test_fullCaseMainEntity jdbc_tool_test_fullCaseMainEntity\n" +
				"SET\n" +
				"    StringColumn = ?,\n" +
				"    IntColumn = ?,\n" +
				"    NStringColumn = ?,\n" +
				"    DateTimeColumn = ?,\n" +
				"    YMColumn = ?,\n" +
				"    CurrencyColumn = ?,\n" +
				"    LongColumn = ?,\n" +
				"    CompanyCD = ?,\n" +
				"    ExclusiveFG = ?,\n" +
				"    RegisteredPerson = ?,\n" +
				"    RegisteredDT = ?,\n" +
				"    UpdatedPerson = ?,\n" +
				"    UpdatedDT = ?\n" +
				"WHERE\n" +
				"    (\n" +
				"        jdbc_tool_test_fullCaseMainEntity.StringColumn = '9001'\n" +
				"        AND jdbc_tool_test_fullCaseMainEntity.IntColumn = 8001\n" +
				"    )\n" +
				"    AND (\n" +
				"        jdbc_tool_test_fullCaseMainEntity.NStringColumn = ?\n" +
				"        AND jdbc_tool_test_fullCaseMainEntity.DateTimeColumn = ?\n" +
				"        AND jdbc_tool_test_fullCaseMainEntity.CompanyCD = ?\n" +
				"    )", result.get(1).getSqlScript());

		Map<String, Object> updateCodeResult = (result.get(1)).getVirtualEntity();
		assertEquals(19, ((List<Map<String,Object>>)updateCodeResult.get("columns")).size());
		assertEquals("Map<String, Object> absoluteVirtualColumn0 = new HashMap<>();\n" +
				"absoluteVirtualColumn0.put(\"name\", \"virtualVolumn01\");\n" +
				"absoluteVirtualColumn0.put(\"displayName\", \"virtualVolumn01\");\n" +
				"absoluteVirtualColumn0.put(\"fixedValue\", \"virtualVolumn01V\");\n" +
				"absoluteVirtualColumn0.put(\"aggregateFunction\", \"-\");\n" +
				"absoluteVirtualColumn0.put(\"format\", \"virtualVolumn01F\");\n" +
				"Map<String, Object> absoluteVirtualColumn1 = new HashMap<>();\n" +
				"absoluteVirtualColumn1.put(\"name\", \"virtualVolumn02\");\n" +
				"absoluteVirtualColumn1.put(\"displayName\", \"virtualVolumn02\");\n" +
				"absoluteVirtualColumn1.put(\"fixedValue\", \"virtualVolumn02V\");\n" +
				"absoluteVirtualColumn1.put(\"aggregateFunction\", \"-\");\n" +
				"absoluteVirtualColumn1.put(\"format\", \"virtualVolumn02F\");\n", updateCodeResult.get("absoluteVirtualColumnCode"));

		String updateRecord = "GRecord record = createRecord();\n" +
				"record.setObject(\"StringColumn\",);\n" +
				"record.setObject(\"IntColumn\",);\n" +
				"record.setObject(\"NStringColumn\",);\n" +
				"record.setObject(\"DateTimeColumn\",);\n" +
				"record.setObject(\"YMColumn\",);\n" +
				"record.setObject(\"CurrencyColumn\",);\n" +
				"record.setObject(\"LongColumn\",);\n" +
				"record.setObject(\"CompanyCD\",);\n" +
				"record.setObject(\"ExclusiveFG\",);\n" +
				"record.setObject(\"RegisteredPerson\",);\n" +
				"record.setObject(\"RegisteredDT\",);\n" +
				"record.setObject(\"UpdatedPerson\",);\n" +
				"record.setObject(\"UpdatedDT\",);\n";

		String whereParamsParts = "Map<String, Object> whereParams = new HashMap<>();\n" +
				"whereParams.put(\"jdbc_tool_test_fullCaseMainEntity.DateTimeColumn\",);\n" +
				"whereParams.put(\"jdbc_tool_test_fullCaseMainEntity.NStringColumn\",);\n" +
				"whereParams.put(\"jdbc_tool_test_fullCaseMainEntity.CompanyCD\",);\n";
		String whereParamsPartsPKUK = "Object[] whereParams = new Object[]{,,};\n";
		StringBuilder updateRecordList = new StringBuilder();
		updateRecordList.append("List<GRecord> recordList = new ArrayList<>();\n");
		updateRecordList.append(updateRecord);
		updateRecordList.append("recordList.add(record);\n");

		String updateExecuteApiStr = "update(\"jdbc_tool_test_fullCaseMainEntity\")\n" + ".set(record)";

		String updateWhereParts = ".where(expMap($($(\"jdbc_tool_test_fullCaseMainEntity.StringColumn = '9001'\").AND(\"jdbc_tool_test_fullCaseMainEntity.IntColumn = 8001\")).AND($(\"jdbc_tool_test_fullCaseMainEntity.NStringColumn = :jdbc_tool_test_fullCaseMainEntity.NStringColumn\").AND(\"jdbc_tool_test_fullCaseMainEntity.DateTimeColumn = :jdbc_tool_test_fullCaseMainEntity.DateTimeColumn\").AND(\"jdbc_tool_test_fullCaseMainEntity.CompanyCD = $TRIM(:jdbc_tool_test_fullCaseMainEntity.CompanyCD)\")), whereParams))";

		assertEquals(whereParamsParts + updateRecord + "\n" + "int count = " + updateExecuteApiStr + "\n" + updateWhereParts + "\n" + ".execute();",
				result.get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE).toString()));
		assertEquals(updateRecord + "\n" + "int count = " + updateExecuteApiStr + "\n" + ".wherePK()\n" + ".execute();",
				result.get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_PK).toString()));
		assertEquals(updateRecord + "\n" + "int count = " + updateExecuteApiStr + "\n" + ".whereUK(\"MultipleColumnUK\")\n" + "//.whereUK(\"OtherUKName\")\n" + "//.whereUK(\"SingleColumnUK\")\n" + ".execute();",
				result.get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_UK).toString()));

		String updateExecuteBatchApiStr = "update(\"jdbc_tool_test_fullCaseMainEntity\")\n" +
				".set(recordList)";

		assertEquals(updateRecordList + "\n" + "int count = " + updateExecuteBatchApiStr + "\n"+ ".wherePK()\n"+ ".executeBatch();", result.get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_PK).toString()));
		assertEquals(updateRecordList + "\n" + "int count = " + updateExecuteBatchApiStr + "\n"+ ".whereUK(\"MultipleColumnUK\")\n" + "//.whereUK(\"OtherUKName\")\n" + "//.whereUK(\"SingleColumnUK\")\n"+ ".executeBatch();", result.get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_UK).toString()));
		assertEquals(updateRecordList + "\n" + "int count = " + updateExecuteBatchApiStr + "\n"+ ".wherePK()\n"+ ".executeList();", result.get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_PK).toString()));
		assertEquals(updateRecordList + "\n" + "int count = " + updateExecuteBatchApiStr + "\n"+ ".whereUK(\"MultipleColumnUK\")\n" + "//.whereUK(\"OtherUKName\")\n" + "//.whereUK(\"SingleColumnUK\")\n"+ ".executeList();", result.get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_UK).toString()));

		assertEquals("SELECT", result.get(2).getType());
		assertEquals("仮想表ID: 35B6AB911C524EE69A30E29BF9A2904A", result.get(2).getDescription());
		assertTrue(assertSqlWithJoin("SELECT\n" +
						"    MAX(jdbc_tool_test_fullCaseMainEntity.objectID),\n" +
						"    SUM(jdbc_tool_test_fullCaseMainEntity.StringColumn),\n" +
						"    AVG(jdbc_tool_test_fullCaseMainEntity.IntColumn),\n" +
						"    MIN(jdbc_tool_test_fullCaseMainEntity.NStringColumn),\n" +
						"    MAX(jdbc_tool_test_fullCaseMainEntity.DateTimeColumn),\n" +
						"    COUNT(jdbc_tool_test_fullCaseMainEntity.YMColumn),\n" +
						"    MAX(jdbc_tool_test_fullCaseMainEntity.CurrencyColumn),\n" +
						"    MAX(jdbc_tool_test_fullCaseMainEntity.LongColumn),\n" +
						"    MAX(jdbc_tool_test_fullCaseMainEntity.CompanyCD),\n" +
						"    MAX(jdbc_tool_test_fullCaseMainEntity.ExclusiveFG),\n" +
						"    MAX(jdbc_tool_test_fullCaseMainEntity.RegisteredPerson),\n" +
						"    MAX(jdbc_tool_test_fullCaseMainEntity.RegisteredDT),\n" +
						"    MAX(jdbc_tool_test_fullCaseMainEntity.UpdatedPerson),\n" +
						"    MAX(jdbc_tool_test_fullCaseMainEntity.UpdatedDT),\n" +
						"    MAX(jdbc_tool_test_fullCaseInnerJoinEntity.StringColumnInner),\n" +
						"    MAX(jdbc_tool_test_fullCaseRightJoinEntity.IntColumnRight),\n" +
						"    MAX(jdbc_tool_test_fullCaseLeftJoinEntity.NStringColumnLeft)\n" +
						"FROM\n" +
						"    " + dbSchema + ".jdbc_tool_test_fullCaseMainEntity jdbc_tool_test_fullCaseMainEntity\n" +
						"${join}" +
						"WHERE\n" +
						"    (\n" +
						"        jdbc_tool_test_fullCaseMainEntity.StringColumn = '9001'\n" +
						"        AND jdbc_tool_test_fullCaseMainEntity.IntColumn = 8001\n" +
						"    )\n" +
						"    AND (\n" +
						"        jdbc_tool_test_fullCaseMainEntity.NStringColumn = ?\n" +
						"        AND jdbc_tool_test_fullCaseMainEntity.DateTimeColumn = ?\n" +
						"        AND jdbc_tool_test_fullCaseMainEntity.CompanyCD = ?\n" +
						"    )\n" +
						"GROUP BY\n" +
						"    jdbc_tool_test_fullCaseMainEntity.CurrencyColumn,\n" +
						"    jdbc_tool_test_fullCaseMainEntity.LongColumn\n" +
						"ORDER BY\n" +
						"    SUM(jdbc_tool_test_fullCaseMainEntity.StringColumn) ASC,\n" +
						"    MIN(jdbc_tool_test_fullCaseMainEntity.NStringColumn) DESC",
				List.of(	"    INNER JOIN " + dbSchema + ".jdbc_tool_test_fullCaseInnerJoinEntity jdbc_tool_test_fullCaseInnerJoinEntity ON jdbc_tool_test_fullCaseMainEntity.StringColumn = jdbc_tool_test_fullCaseInnerJoinEntity.StringColumnInner\n",
						"    AND jdbc_tool_test_fullCaseInnerJoinEntity.NStringColumnInner = '9001'\n" +
								"    RIGHT OUTER JOIN " + dbSchema + ".jdbc_tool_test_fullCaseRightJoinEntity jdbc_tool_test_fullCaseRightJoinEntity ON jdbc_tool_test_fullCaseMainEntity.NStringColumn = jdbc_tool_test_fullCaseRightJoinEntity.NStringColumnRight\n",
						"    AND jdbc_tool_test_fullCaseRightJoinEntity.StringColumnRight = '7001'\n" +
								"    LEFT OUTER JOIN " + dbSchema + ".jdbc_tool_test_fullCaseLeftJoinEntity jdbc_tool_test_fullCaseLeftJoinEntity ON jdbc_tool_test_fullCaseMainEntity.IntColumn = jdbc_tool_test_fullCaseLeftJoinEntity.IntColumnLeft\n",
						"    AND jdbc_tool_test_fullCaseLeftJoinEntity.LongColumnLeft = 8001\n"
				),result.get(2).getSqlScript()));
		Map<String, Object> selectCodeResult = (result.get(2)).getVirtualEntity();
		assertEquals(19, ((List<Map<String,Object>>)selectCodeResult.get("columns")).size());
		assertEquals("Map<String, Object> absoluteVirtualColumn0 = new HashMap<>();\n" +
				"absoluteVirtualColumn0.put(\"name\", \"virtualVolumn01\");\n" +
				"absoluteVirtualColumn0.put(\"displayName\", \"virtualVolumn01\");\n" +
				"absoluteVirtualColumn0.put(\"fixedValue\", \"virtualVolumn01V\");\n" +
				"absoluteVirtualColumn0.put(\"aggregateFunction\", \"-\");\n" +
				"absoluteVirtualColumn0.put(\"format\", \"virtualVolumn01F\");\n" +
				"Map<String, Object> absoluteVirtualColumn1 = new HashMap<>();\n" +
				"absoluteVirtualColumn1.put(\"name\", \"virtualVolumn02\");\n" +
				"absoluteVirtualColumn1.put(\"displayName\", \"virtualVolumn02\");\n" +
				"absoluteVirtualColumn1.put(\"fixedValue\", \"virtualVolumn02V\");\n" +
				"absoluteVirtualColumn1.put(\"aggregateFunction\", \"-\");\n" +
				"absoluteVirtualColumn1.put(\"format\", \"virtualVolumn02F\");\n", selectCodeResult.get("absoluteVirtualColumnCode"));

		String selectApiStr = "select(\"jdbc_tool_test_fullCaseMainEntity\", \"jdbc_tool_test_fullCaseMainEntity\")\n" +
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
				"col(\"jdbc_tool_test_fullCaseLeftJoinEntity.NStringColumnLeft\", GColumn.FuncType.MAX))\n" +
				".groupBy(col(\"jdbc_tool_test_fullCaseMainEntity.CurrencyColumn\"),col(\"jdbc_tool_test_fullCaseMainEntity.LongColumn\"))\n" +
				"${join}" +
				".orderBy(asc(\"jdbc_tool_test_fullCaseMainEntity.StringColumn\"),desc(\"jdbc_tool_test_fullCaseMainEntity.NStringColumn\"))";
		List<String> joins = List.of(".innerJoin(\"jdbc_tool_test_fullCaseInnerJoinEntity\", \"jdbc_tool_test_fullCaseInnerJoinEntity\", expMap($(\"jdbc_tool_test_fullCaseMainEntity.StringColumn = jdbc_tool_test_fullCaseInnerJoinEntity.StringColumnInner\").AND(\"jdbc_tool_test_fullCaseInnerJoinEntity.NStringColumnInner = '9001'\"), Collections.emptyMap()))\n",
				".rightOuterJoin(\"jdbc_tool_test_fullCaseRightJoinEntity\", \"jdbc_tool_test_fullCaseRightJoinEntity\", expMap($(\"jdbc_tool_test_fullCaseMainEntity.NStringColumn = jdbc_tool_test_fullCaseRightJoinEntity.NStringColumnRight\").AND(\"jdbc_tool_test_fullCaseRightJoinEntity.StringColumnRight = '7001'\"), Collections.emptyMap()))\n",
				".leftOuterJoin(\"jdbc_tool_test_fullCaseLeftJoinEntity\", \"jdbc_tool_test_fullCaseLeftJoinEntity\", expMap($(\"jdbc_tool_test_fullCaseMainEntity.IntColumn = jdbc_tool_test_fullCaseLeftJoinEntity.IntColumnLeft\").AND(\"jdbc_tool_test_fullCaseLeftJoinEntity.LongColumnLeft = 8001\"), Collections.emptyMap()))\n"
		);

		String selectWhereParts = ".where(expMap($($(\"jdbc_tool_test_fullCaseMainEntity.StringColumn = '9001'\").AND(\"jdbc_tool_test_fullCaseMainEntity.IntColumn = 8001\")).AND($(\"jdbc_tool_test_fullCaseMainEntity.NStringColumn = :jdbc_tool_test_fullCaseMainEntity.NStringColumn\").AND(\"jdbc_tool_test_fullCaseMainEntity.DateTimeColumn = :jdbc_tool_test_fullCaseMainEntity.DateTimeColumn\").AND(\"jdbc_tool_test_fullCaseMainEntity.CompanyCD = $TRIM(:jdbc_tool_test_fullCaseMainEntity.CompanyCD)\")), whereParams))";

		assertTrue(assertSqlWithJoin(whereParamsParts + "List<GRecord> result = " + selectApiStr + "\n" + selectWhereParts + "\n"+ ".findList();", joins, result.get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE).toString())));
		assertTrue(assertSqlWithJoin(whereParamsPartsPKUK + "List<GRecord> result = " + selectApiStr + "\n"+ ".wherePK(whereParams)\n"+ ".findList();", joins, result.get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE_PK).toString())));
		assertTrue(assertSqlWithJoin(whereParamsPartsPKUK + "List<GRecord> result = " + selectApiStr + "\n"+ ".whereUK(\"MultipleColumnUK\", whereParams)\n" + "//.whereUK(\"OtherUKName\", whereParams)\n" + "//.whereUK(\"SingleColumnUK\", whereParams)\n"+ ".findList();", joins, result.get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE_UK).toString())));
		assertTrue(assertSqlWithJoin(whereParamsParts + "GRecord result = " + selectApiStr + "\n" + selectWhereParts + "\n"+ ".findRecord();", joins, result.get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE).toString())));
		assertTrue(assertSqlWithJoin(whereParamsPartsPKUK + "GRecord result = " + selectApiStr + "\n"+ ".wherePK(whereParams)\n"+ ".findRecord();", joins, result.get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE_PK).toString())));
		assertTrue(assertSqlWithJoin(whereParamsPartsPKUK + "GRecord result = " + selectApiStr + "\n"+ ".whereUK(\"MultipleColumnUK\", whereParams)\n" + "//.whereUK(\"OtherUKName\", whereParams)\n" + "//.whereUK(\"SingleColumnUK\", whereParams)\n"+ ".findRecord();", joins, result.get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE_UK).toString())));
		assertTrue(assertSqlWithJoin(whereParamsParts + "GRecordSet result = " + selectApiStr + "\n" + selectWhereParts + "\n"+ ".findRecordSet();", joins, result.get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE).toString())));
		assertTrue(assertSqlWithJoin(whereParamsPartsPKUK + "GRecordSet result = " + selectApiStr + "\n"+ ".wherePK(whereParams)\n"+ ".findRecordSet();", joins, result.get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE_PK).toString())));
		assertTrue(assertSqlWithJoin(whereParamsPartsPKUK + "GRecordSet result = " + selectApiStr + "\n"+ ".whereUK(\"MultipleColumnUK\", whereParams)\n" + "//.whereUK(\"OtherUKName\", whereParams)\n" + "//.whereUK(\"SingleColumnUK\", whereParams)\n"+ ".findRecordSet();", joins, result.get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE_UK).toString())));

		assertEquals("DELETE", result.get(3).getType());
		assertEquals("仮想表ID: 35B6AB911C524EE69A30E29BF9A2904A", result.get(3).getDescription());
		assertEquals(getDeleteSql_fullCase(), result.get(3).getSqlScript());

		Map<String, Object> deleteCodeResult = (result.get(3)).getVirtualEntity();
		assertEquals(19, ((List<Map<String,Object>>)deleteCodeResult.get("columns")).size());
		assertEquals("Map<String, Object> absoluteVirtualColumn0 = new HashMap<>();\n" +
				"absoluteVirtualColumn0.put(\"name\", \"virtualVolumn01\");\n" +
				"absoluteVirtualColumn0.put(\"displayName\", \"virtualVolumn01\");\n" +
				"absoluteVirtualColumn0.put(\"fixedValue\", \"virtualVolumn01V\");\n" +
				"absoluteVirtualColumn0.put(\"aggregateFunction\", \"-\");\n" +
				"absoluteVirtualColumn0.put(\"format\", \"virtualVolumn01F\");\n" +
				"Map<String, Object> absoluteVirtualColumn1 = new HashMap<>();\n" +
				"absoluteVirtualColumn1.put(\"name\", \"virtualVolumn02\");\n" +
				"absoluteVirtualColumn1.put(\"displayName\", \"virtualVolumn02\");\n" +
				"absoluteVirtualColumn1.put(\"fixedValue\", \"virtualVolumn02V\");\n" +
				"absoluteVirtualColumn1.put(\"aggregateFunction\", \"-\");\n" +
				"absoluteVirtualColumn1.put(\"format\", \"virtualVolumn02F\");\n", selectCodeResult.get("absoluteVirtualColumnCode"));

		String deleteApiStr = "delete(\"jdbc_tool_test_fullCaseMainEntity\")";

		String deleteWhereParts = ".where(expMap($($(\"jdbc_tool_test_fullCaseMainEntity.StringColumn = '9001'\").AND(\"jdbc_tool_test_fullCaseMainEntity.IntColumn = 8001\")).AND($(\"jdbc_tool_test_fullCaseMainEntity.NStringColumn = :jdbc_tool_test_fullCaseMainEntity.NStringColumn\").AND(\"jdbc_tool_test_fullCaseMainEntity.DateTimeColumn = :jdbc_tool_test_fullCaseMainEntity.DateTimeColumn\").AND(\"jdbc_tool_test_fullCaseMainEntity.CompanyCD = $TRIM(:jdbc_tool_test_fullCaseMainEntity.CompanyCD)\")), whereParams))";

		assertEquals(whereParamsParts + "int count = " + deleteApiStr + "\n" + deleteWhereParts + "\n"+ ".execute();", result.get(3).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE).toString()));

		String deleteExecutePKWhereParts = "GRecord whereParams = createRecord();\n" +
				"whereParams.setObject(\"objectID\",);\n";


		String deleteExecuteListPKWhereParts = deleteExecutePKWhereParts +
				"List<GRecord> whereParamsList = new ArrayList<>();\n" +
				"whereParamsList.add(whereParams);\n";

		String deleteExecuteUKWhereParts = "GRecord whereParams = createRecord();\n" +
				"whereParams.setObject(\"StringColumn\",);\n" +
				"whereParams.setObject(\"IntColumn\",);\n" +
				"whereParams.setObject(\"NStringColumn\",);\n" +
				"//ユーニックキー「OtherUKName」用のカラム\n" +
				"/*whereParams.setObject(\"IntColumn\",);\n" +
				"whereParams.setObject(\"NStringColumn\",);\n" +
				"whereParams.setObject(\"DateTimeColumn\",);\n" +
				"whereParams.setObject(\"YMColumn\",);\n" +
				"*/\n" +
				"//ユーニックキー「SingleColumnUK」用のカラム\n" +
				"/*whereParams.setObject(\"StringColumn\",);\n" +
				"*/\n";
		String deleteExecuteListUKWhereParts = deleteExecuteUKWhereParts +
				"List<GRecord> whereParamsList = new ArrayList<>();\n" +
				"whereParamsList.add(whereParams);\n";

		assertEquals(deleteExecutePKWhereParts + "\n" + "int count = " + deleteApiStr + "\n"+ ".wherePK(whereParams)\n"+ ".execute();", result.get(3).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_PK).toString()));
		assertEquals(deleteExecuteUKWhereParts + "\n" + "int count = " + deleteApiStr + "\n"+ ".whereUK(\"MultipleColumnUK\", whereParams)\n" + "//.whereUK(\"OtherUKName\", whereParams)\n" + "//.whereUK(\"SingleColumnUK\", whereParams)\n"+ ".execute();", result.get(3).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_UK).toString()));

		assertEquals(deleteExecuteListPKWhereParts + "\n" + "int count = " + deleteApiStr + "\n"+ ".wherePK(whereParamsList)\n"+ ".executeList();", result.get(3).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_PK).toString()));
		assertEquals(deleteExecuteListUKWhereParts + "\n" + "int count = " + deleteApiStr + "\n"+ ".whereUK(\"MultipleColumnUK\", whereParamsList)\n" + "//.whereUK(\"OtherUKName\", whereParamsList)\n" + "//.whereUK(\"SingleColumnUK\", whereParamsList)\n"+ ".executeList();", result.get(3).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_UK).toString()));

		assertEquals(deleteExecuteListPKWhereParts + "\n" + "int count = " + deleteApiStr + "\n"+ ".wherePK(whereParamsList)\n"+ ".executeBatch();", result.get(3).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_PK).toString()));
		assertEquals(deleteExecuteListUKWhereParts + "\n" + "int count = " + deleteApiStr + "\n"+ ".whereUK(\"MultipleColumnUK\", whereParamsList)\n" + "//.whereUK(\"OtherUKName\", whereParamsList)\n" + "//.whereUK(\"SingleColumnUK\", whereParamsList)\n"+ ".executeBatch();", result.get(3).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_UK).toString()));
	}

	@Test
	public void testExport_join_mix_useForeignKeyDefault() {
//		doCustomConfig(getUseForeignKeyDefaultConfigPath());
		SettingRequest settingRequest = getCommonSetting();
		settingRequest.getTransform().put("useForeignKey", false);
		settingRequest.getTransform().put("useExpMap", false);
		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_join_mix.xml");
		ApiRequest apiRequest =  parseRequestFromFile(settingRequest,"jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_join_mix.yaml");

		List<CodeResponse> result = convertVe2Code(apiRequest);
		
		assertEquals("SELECT", result.get(0).getType());
		assertEquals("仮想表ID: E44ADEF7B30549449EDF193ECFB66631", result.get(0).getDescription());
		assertTrue(assertSqlWithJoin("SELECT\n" +
						"    jdbc_tool_test_pk_uk_mainmaster.objectID,\n" +
						"    jdbc_tool_test_pk_uk_mainmaster.ItemCD,\n" +
						"    jdbc_tool_test_pk_uk_mainmaster.ItemNA,\n" +
						"    jdbc_tool_test_pk_uk_mainmaster.UnitPrice,\n" +
						"    jdbc_tool_test_pk_uk_mainmaster.SellPrice,\n" +
						"    jdbc_tool_test_pk_uk_mainmaster.Flag,\n" +
						"    jdbc_tool_test_pk_uk_mainmaster.PriceFlag,\n" +
						"    jdbc_tool_test_pk_uk_mainmaster.ExclusiveFG,\n" +
						"    jdbc_tool_test_pk_uk_mainmaster.RegisteredDT,\n" +
						"    jdbc_tool_test_pk_uk_mainmaster.UpdatedDT\n" +
						"FROM\n" +
						"    " + dbSchema + ".jdbc_tool_test_pk_uk_mainmaster jdbc_tool_test_pk_uk_mainmaster\n" +
						"${join}" +
						"WHERE\n" +
						"    jdbc_tool_test_pk_uk_mainmaster.objectID = ?",
				List.of("    LEFT OUTER JOIN " + dbSchema + ".jdbc_tool_test_unitmaster jdbc_tool_test_unitmaster ON jdbc_tool_test_pk_uk_mainmaster.objectID = jdbc_tool_test_unitmaster.Code\n",
						"    INNER JOIN " + dbSchema + ".jdbc_tool_test_mainmaster jdbc_tool_test_mainmaster ON jdbc_tool_test_pk_uk_mainmaster.objectID = jdbc_tool_test_mainmaster.objectID\n",
						"    RIGHT OUTER JOIN " + dbSchema + ".jdbc_tool_test_ordermaster jdbc_tool_test_ordermaster ON jdbc_tool_test_pk_uk_mainmaster.ItemCD = jdbc_tool_test_ordermaster.objectID\n"
				), result.get(0).getSqlScript()));
		String whereParamsParts = "Object[] whereParams = new Object[]{};\n";
		String groupByApiStr = "select(\"jdbc_tool_test_pk_uk_mainmaster\", \"jdbc_tool_test_pk_uk_mainmaster\")\n" +
				".fields(colsAll())\n" +
				"${join}" +
				".where(exp($(\"jdbc_tool_test_pk_uk_mainmaster.objectID = ?\"), whereParams))";
		List<String> joins = List.of(
				".leftOuterJoin(\"jdbc_tool_test_unitmaster\", \"jdbc_tool_test_unitmaster\", exp($(\"jdbc_tool_test_pk_uk_mainmaster.objectID = jdbc_tool_test_unitmaster.Code\")))\n",
				".innerJoin(\"jdbc_tool_test_mainmaster\", \"jdbc_tool_test_mainmaster\", exp($(\"jdbc_tool_test_pk_uk_mainmaster.objectID = jdbc_tool_test_mainmaster.objectID\")))\n",
				".rightOuterJoin(\"jdbc_tool_test_ordermaster\", \"jdbc_tool_test_ordermaster\", exp($(\"jdbc_tool_test_pk_uk_mainmaster.ItemCD = jdbc_tool_test_ordermaster.objectID\")))\n"
		);

		assertTrue(assertSqlWithJoin(whereParamsParts + "List<GRecord> result = " + groupByApiStr + "\n"+ ".findList();", joins, result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE).toString().toString())));
		assertTrue(assertSqlWithJoin(whereParamsParts + "GRecord result = " + groupByApiStr + "\n"+ ".findRecord();", joins, result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE).toString().toString())));
		assertTrue(assertSqlWithJoin(whereParamsParts + "GRecordSet result = " + groupByApiStr + "\n"+ ".findRecordSet();", joins, result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE).toString().toString())));
	}

	@Test
	public void testExport_searchExpWhereParts_JoinColumn_optionTrim() {

		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_searchExpWhereParts_JoinColumn_optionTrim.xml");
		ApiRequest apiRequest =  parseRequestFromFile(getCommonSetting(),"jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_searchExpWhereparts_joincolumn_optionTrim.yaml");

		List<CodeResponse> result = convertVe2Code(apiRequest);
		
		assertEquals("SELECT", result.get(0).getType());
		assertEquals("仮想表ID: 940E79601B9143D687A8500F9915F7D8", result.get(0).getDescription());
		assertTrue(assertSqlWithJoin("SELECT\n" +
						"    jdbc_tool_test_pk_uk_mainmaster.objectID,\n" +
						"    jdbc_tool_test_pk_uk_mainmaster.ItemCD,\n" +
						"    jdbc_tool_test_pk_uk_mainmaster.ItemNA,\n" +
						"    jdbc_tool_test_pk_uk_mainmaster.UnitPrice,\n" +
						"    jdbc_tool_test_pk_uk_mainmaster.SellPrice,\n" +
						"    jdbc_tool_test_pk_uk_mainmaster.Flag,\n" +
						"    jdbc_tool_test_pk_uk_mainmaster.PriceFlag,\n" +
						"    jdbc_tool_test_pk_uk_mainmaster.ExclusiveFG,\n" +
						"    jdbc_tool_test_pk_uk_mainmaster.RegisteredDT,\n" +
						"    jdbc_tool_test_pk_uk_mainmaster.UpdatedDT,\n" +
						"    jdbc_tool_test_mainmaster.objectID,\n" +
						"    jdbc_tool_test_unitmaster.Code,\n" +
						"    jdbc_tool_test_ordermaster.SlipNO,\n" +
						"    jdbc_tool_test_mainmaster.SellPrice\n" +
						"FROM\n" +
						"    " + dbSchema + ".jdbc_tool_test_pk_uk_mainmaster jdbc_tool_test_pk_uk_mainmaster\n" +
						"${join}" +
						"WHERE\n" +
						"    (\n" +
						"        jdbc_tool_test_pk_uk_mainmaster.objectID = ?\n" +
						"        AND jdbc_tool_test_unitmaster.Code = ?\n" +
						"        AND jdbc_tool_test_mainmaster.objectID = ?\n" +
						"        AND jdbc_tool_test_ordermaster.SlipNO LIKE ? ESCAPE '/'\n" +
						"        AND jdbc_tool_test_pk_uk_mainmaster.ItemCD LIKE ? ESCAPE '/'\n" +
						"        AND jdbc_tool_test_pk_uk_mainmaster.ItemNA LIKE ? ESCAPE '/'\n" +
						"    )"
				, List.of("    LEFT OUTER JOIN " + dbSchema + ".jdbc_tool_test_unitmaster jdbc_tool_test_unitmaster ON jdbc_tool_test_pk_uk_mainmaster.objectID = jdbc_tool_test_unitmaster.Code\n",
						"    INNER JOIN " + dbSchema + ".jdbc_tool_test_mainmaster jdbc_tool_test_mainmaster ON jdbc_tool_test_pk_uk_mainmaster.objectID = jdbc_tool_test_mainmaster.objectID\n",
						"    RIGHT OUTER JOIN " + dbSchema + ".jdbc_tool_test_ordermaster jdbc_tool_test_ordermaster ON jdbc_tool_test_pk_uk_mainmaster.ItemCD = jdbc_tool_test_ordermaster.objectID\n"
				), result.get(0).getSqlScript()));

		String whereParamsParts = "Map<String, Object> whereParams = new HashMap<>();\n" +
				"whereParams.put(\"jdbc_tool_test_unitmaster.Code\",);\n" +
				"whereParams.put(\"jdbc_tool_test_pk_uk_mainmaster.ItemCD\",);\n" +
				"whereParams.put(\"jdbc_tool_test_pk_uk_mainmaster.ItemNA\",);\n" +
				"whereParams.put(\"jdbc_tool_test_pk_uk_mainmaster.objectID\",);\n" +
				"whereParams.put(\"jdbc_tool_test_ordermaster.SlipNO\",);\n" +
				"whereParams.put(\"jdbc_tool_test_mainmaster.objectID\",);\n";
		String groupByApiStr = "select(\"jdbc_tool_test_pk_uk_mainmaster\", \"jdbc_tool_test_pk_uk_mainmaster\")\n" +
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
				"col(\"jdbc_tool_test_mainmaster.SellPrice\"))\n" +
				"${join}" +
				".where(expMap($($(\"jdbc_tool_test_pk_uk_mainmaster.objectID = [:jdbc_tool_test_pk_uk_mainmaster.objectID]\").AND(\"jdbc_tool_test_unitmaster.Code = $TRIM(:jdbc_tool_test_unitmaster.Code)\").AND(\"jdbc_tool_test_mainmaster.objectID = $TRIM([:jdbc_tool_test_mainmaster.objectID])\").AND(\"jdbc_tool_test_ordermaster.SlipNO LIKE $CONCAT(:jdbc_tool_test_ordermaster.SlipNO, '%') ESCAPE '/'\").AND(\"jdbc_tool_test_pk_uk_mainmaster.ItemCD LIKE $CONCAT('%', $CONCAT(:jdbc_tool_test_pk_uk_mainmaster.ItemCD, '%')) ESCAPE '/'\").AND(\"jdbc_tool_test_pk_uk_mainmaster.ItemNA LIKE $CONCAT('%', :jdbc_tool_test_pk_uk_mainmaster.ItemNA) ESCAPE '/'\")), whereParams))";

		List<String> joins = List.of(".leftOuterJoinFK(\"FK_UnitMaster\", \"jdbc_tool_test_unitmaster\")\n",
				".innerJoinFK(\"FK_MainMaster\", \"jdbc_tool_test_mainmaster\")\n",
				".rightOuterJoinFK(\"FK_OrderMaster\", \"jdbc_tool_test_ordermaster\")\n");
		assertTrue(assertSqlWithJoin(whereParamsParts + "List<GRecord> result = " + groupByApiStr + "\n"+ ".findList();", joins,result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE).toString())));
		assertTrue(assertSqlWithJoin(whereParamsParts + "GRecord result = " + groupByApiStr + "\n"+ ".findRecord();", joins, result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE).toString())));
		assertTrue(assertSqlWithJoin(whereParamsParts + "GRecordSet result = " + groupByApiStr + "\n"+ ".findRecordSet();", joins, result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE).toString())));
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
		return "DELETE FROM\n" +
				"    " + dbSchema + ".jdbc_tool_test_fullCaseMainEntity jdbc_tool_test_fullCaseMainEntity\n" +
				"WHERE\n" +
				"    (\n" +
				"        (\n" +
				"            jdbc_tool_test_fullCaseMainEntity.objectID = '1001'\n" +
				"            AND jdbc_tool_test_fullCaseMainEntity.StringColumn = '2002'\n" +
				"        )\n" +
				"        OR jdbc_tool_test_fullCaseMainEntity.IntColumn = 3003\n" +
				"    )\n" +
				"    AND (\n" +
				"        (\n" +
				"            jdbc_tool_test_fullCaseMainEntity.NStringColumn = ?\n" +
				"            AND jdbc_tool_test_fullCaseMainEntity.DateTimeColumn = ?\n" +
				"        )\n" +
				"        OR jdbc_tool_test_fullCaseMainEntity.YMColumn = ?\n" +
				"        AND jdbc_tool_test_fullCaseMainEntity.CompanyCD = ?\n" +
				"    )";
	}
	protected String getDeleteSql_testExport_expMapCase2() {
		return "DELETE FROM\n" +
				"    " + dbSchema + ".jdbc_tool_test_fullCaseMainEntity jdbc_tool_test_fullCaseMainEntity\n" +
				"WHERE\n" +
				"    (\n" +
				"        (\n" +
				"            jdbc_tool_test_fullCaseMainEntity.objectID = '1001'\n" +
				"            AND jdbc_tool_test_fullCaseMainEntity.StringColumn = '2002'\n" +
				"        )\n" +
				"        OR (\n" +
				"            jdbc_tool_test_fullCaseMainEntity.IntColumn = 3003\n" +
				"            AND jdbc_tool_test_fullCaseMainEntity.NStringColumn = '4004'\n" +
				"        )\n" +
				"    )\n" +
				"    AND (\n" +
				"        (\n" +
				"            jdbc_tool_test_fullCaseMainEntity.DateTimeColumn = ?\n" +
				"            AND jdbc_tool_test_fullCaseMainEntity.YMColumn = ?\n" +
				"        )\n" +
				"        OR (\n" +
				"            jdbc_tool_test_fullCaseMainEntity.CurrencyColumn = ?\n" +
				"            AND jdbc_tool_test_fullCaseMainEntity.LongColumn = ?\n" +
				"        )\n" +
				"        AND jdbc_tool_test_fullCaseMainEntity.CompanyCD = ?\n" +
				"    )";
	}
	protected String getDeleteSql_testExport_expMapCase3() {
		return "DELETE FROM\n" +
				"    " + dbSchema + ".jdbc_tool_test_fullCaseMainEntity jdbc_tool_test_fullCaseMainEntity\n" +
				"WHERE\n" +
				"    (\n" +
				"        (\n" +
				"            jdbc_tool_test_fullCaseMainEntity.objectID = '1001'\n" +
				"            AND jdbc_tool_test_fullCaseMainEntity.StringColumn = '2002'\n" +
				"        )\n" +
				"        OR (\n" +
				"            jdbc_tool_test_fullCaseMainEntity.IntColumn = 3003\n" +
				"            AND jdbc_tool_test_fullCaseMainEntity.NStringColumn = '4004'\n" +
				"            OR (\n" +
				"                jdbc_tool_test_fullCaseMainEntity.DateTimeColumn = '5005'\n" +
				"                AND jdbc_tool_test_fullCaseMainEntity.YMColumn = '6006'\n" +
				"            )\n" +
				"        )\n" +
				"    )\n" +
				"    AND (\n" +
				"        (\n" +
				"            jdbc_tool_test_fullCaseMainEntity.CurrencyColumn = ?\n" +
				"            AND jdbc_tool_test_fullCaseMainEntity.LongColumn = ?\n" +
				"        )\n" +
				"        OR (\n" +
				"            jdbc_tool_test_fullCaseMainEntity.CompanyCD = ?\n" +
				"            AND jdbc_tool_test_fullCaseMainEntity.ExclusiveFG = ?\n" +
				"            OR (\n" +
				"                jdbc_tool_test_fullCaseMainEntity.RegisteredPerson = ?\n" +
				"                AND jdbc_tool_test_fullCaseMainEntity.RegisteredDT = ?\n" +
				"            )\n" +
				"        )\n" +
				"    )";
	}
	protected String getDeleteSql_testExport_expMapCase4() {
		return "DELETE FROM\n" +
				"    " + dbSchema + ".jdbc_tool_test_fullCaseMainEntity jdbc_tool_test_fullCaseMainEntity\n" +
				"WHERE\n" +
				"    (\n" +
				"        (\n" +
				"            jdbc_tool_test_fullCaseMainEntity.objectID = ?\n" +
				"            AND jdbc_tool_test_fullCaseMainEntity.StringColumn = ?\n" +
				"        )\n" +
				"        OR jdbc_tool_test_fullCaseMainEntity.IntColumn = ?\n" +
				"        AND jdbc_tool_test_fullCaseMainEntity.NStringColumn = ?\n" +
				"    )";
	}
}
