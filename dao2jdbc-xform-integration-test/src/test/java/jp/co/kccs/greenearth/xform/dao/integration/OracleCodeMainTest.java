package jp.co.kccs.greenearth.xform.dao.integration;

import jp.co.kccs.greenearth.xform.code.jdbc.core.GCompoundApiImpl;
import jp.co.kccs.greenearth.xform.code.jdbc.core.GTerminatorApi;
import jp.co.kccs.greenearth.xform.code.jdbc.core.GWhereApi;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class OracleCodeMainTest extends CodeMainTest {
	@Override
	protected String getCommonSettingPath()  {
		return "inputFile/settings_oracle.yaml";
	}

	@Override
	protected String getTimestampCommand() {
		return "SYSDATE";
	}
	@Test
	public void testExport_join_mix_useForeignKeyDefault() {
//		doCustomConfig(getUseForeignKeyDefaultConfigPath());
		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_join_mix.xml");
		SettingRequest setting = getCommonSetting();
		setting.getTransform().put("useExpMap", false);
		ApiRequest apiRequest = parseRequestFromFile(setting,"jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_join_mix.yaml");

		List<CodeResponse> result = convertVe2Code(apiRequest);

		assertEquals("SELECT", result.get(0).getType());
		assertEquals("仮想表ID: E44ADEF7B30549449EDF193ECFB66631", result.get(0).getDescription());
		assertEquals("SELECT\n" +
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
				"    gef_jdbc_tool.jdbc_tool_test_pk_uk_mainmaster jdbc_tool_test_pk_uk_mainmaster\n" +
				"    LEFT OUTER JOIN gef_jdbc_tool.jdbc_tool_test_unitmaster jdbc_tool_test_unitmaster ON jdbc_tool_test_pk_uk_mainmaster.objectID = jdbc_tool_test_unitmaster.Code\n" +
				"    INNER JOIN gef_jdbc_tool.jdbc_tool_test_mainmaster jdbc_tool_test_mainmaster ON jdbc_tool_test_pk_uk_mainmaster.objectID = jdbc_tool_test_mainmaster.objectID\n" +
				"    RIGHT OUTER JOIN gef_jdbc_tool.jdbc_tool_test_ordermaster jdbc_tool_test_ordermaster ON jdbc_tool_test_pk_uk_mainmaster.ItemCD = jdbc_tool_test_ordermaster.objectID\n" +
				"WHERE\n" +
				"    jdbc_tool_test_pk_uk_mainmaster.objectID = ?", result.get(0).getSqlScript());

		String whereParamsParts = "Object[] whereParams = new Object[]{};\n";
		String groupByApiStr = "select(\"jdbc_tool_test_pk_uk_mainmaster\", \"jdbc_tool_test_pk_uk_mainmaster\")\n"+
				".fields(colsAll())\n"+
				".leftOuterJoinFK(\"FK_UnitMaster\", \"jdbc_tool_test_unitmaster\")\n" +
				".innerJoinFK(\"FK_MainMaster\", \"jdbc_tool_test_mainmaster\")\n" +
				".rightOuterJoinFK(\"FK_OrderMaster\", \"jdbc_tool_test_ordermaster\")\n"+
				".where(exp($(\"jdbc_tool_test_pk_uk_mainmaster.objectID = ?\"), whereParams))";

		assertEquals(whereParamsParts + "List<GRecord> result = " + groupByApiStr + "\n.findList();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE).toString()));
		assertEquals(whereParamsParts + "GRecord result = " + groupByApiStr + "\n.findRecord();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE).toString()));
		assertEquals(whereParamsParts + "GRecordSet result = " + groupByApiStr + "\n.findRecordSet();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE).toString()));
	}


	@Test
	public void testExport_join_mix() {

		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_join_mix.xml");
		ApiRequest apiRequest = parseRequestFromFile(getCommonSetting(),"jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_join_mix.yaml");
		List<CodeResponse> result = convertVe2Code(apiRequest);

		assertEquals("SELECT", result.get(0).getType());
		assertEquals("仮想表ID: E44ADEF7B30549449EDF193ECFB66631", result.get(0).getDescription());
		assertEquals("SELECT\n" +
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
				"    gef_jdbc_tool.jdbc_tool_test_pk_uk_mainmaster jdbc_tool_test_pk_uk_mainmaster\n" +
				"    LEFT OUTER JOIN gef_jdbc_tool.jdbc_tool_test_unitmaster jdbc_tool_test_unitmaster ON jdbc_tool_test_pk_uk_mainmaster.objectID = jdbc_tool_test_unitmaster.Code\n" +
				"    INNER JOIN gef_jdbc_tool.jdbc_tool_test_mainmaster jdbc_tool_test_mainmaster ON jdbc_tool_test_pk_uk_mainmaster.objectID = jdbc_tool_test_mainmaster.objectID\n" +
				"    RIGHT OUTER JOIN gef_jdbc_tool.jdbc_tool_test_ordermaster jdbc_tool_test_ordermaster ON jdbc_tool_test_pk_uk_mainmaster.ItemCD = jdbc_tool_test_ordermaster.objectID\n" +
				"WHERE\n" +
				"    jdbc_tool_test_pk_uk_mainmaster.objectID = ?", result.get(0).getSqlScript());

		String whereParamsParts = "Map<String, Object> whereParams = new HashMap<>();\n"+
				"whereParams.put(\"jdbc_tool_test_pk_uk_mainmaster.objectID\",);\n";
		String groupByApiStr = "select(\"jdbc_tool_test_pk_uk_mainmaster\", \"jdbc_tool_test_pk_uk_mainmaster\")\n"+
				".fields(colsAll())\n"+
				".leftOuterJoinFK(\"FK_UnitMaster\", \"jdbc_tool_test_unitmaster\")\n" +
				".innerJoinFK(\"FK_MainMaster\", \"jdbc_tool_test_mainmaster\")\n" +
				".rightOuterJoinFK(\"FK_OrderMaster\", \"jdbc_tool_test_ordermaster\")\n"+
				".where(expMap($(\"jdbc_tool_test_pk_uk_mainmaster.objectID = :jdbc_tool_test_pk_uk_mainmaster.objectID\"), whereParams))";

		assertEquals(whereParamsParts + "List<GRecord> result = " + groupByApiStr + "\n.findList();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE).toString()));
		assertEquals(whereParamsParts + "GRecord result = " + groupByApiStr + "\n.findRecord();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE).toString()));
		assertEquals(whereParamsParts + "GRecordSet result = " + groupByApiStr + "\n.findRecordSet();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE).toString()));
	}

	@Test
	public void testExport_filterExpWhereParts_JoinColumn() {
		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_filterExpWhereParts_JoinColumn.xml");
		ApiRequest apiRequest = parseRequestFromFile(getCommonSetting(),"jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_filterExpWhereparts_joincolumn.yaml");
		List<CodeResponse> result = convertVe2Code(apiRequest);

		assertEquals("SELECT", result.get(0).getType());
		assertEquals("仮想表ID: 395DA3D2A7C541FDAFF20CC7AF7BBFE8", result.get(0).getDescription());
		assertEquals("SELECT\n" +
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
				"    gef_jdbc_tool.jdbc_tool_test_pk_uk_mainmaster jdbc_tool_test_pk_uk_mainmaster\n" +
				"    LEFT OUTER JOIN gef_jdbc_tool.jdbc_tool_test_unitmaster jdbc_tool_test_unitmaster ON jdbc_tool_test_pk_uk_mainmaster.objectID = jdbc_tool_test_unitmaster.Code\n" +
				"    INNER JOIN gef_jdbc_tool.jdbc_tool_test_mainmaster jdbc_tool_test_mainmaster ON jdbc_tool_test_pk_uk_mainmaster.objectID = jdbc_tool_test_mainmaster.objectID\n" +
				"    RIGHT OUTER JOIN gef_jdbc_tool.jdbc_tool_test_ordermaster jdbc_tool_test_ordermaster ON jdbc_tool_test_pk_uk_mainmaster.ItemCD = jdbc_tool_test_ordermaster.objectID\n" +
				"WHERE\n" +
				"    (\n" +
				"        jdbc_tool_test_pk_uk_mainmaster.objectID = '1001'\n" +
				"        AND jdbc_tool_test_unitmaster.Code = '1001'\n" +
				"        AND jdbc_tool_test_mainmaster.objectID = '1001'\n" +
				"        AND jdbc_tool_test_ordermaster.SlipNO LIKE '100%'\n" +
				"        AND jdbc_tool_test_pk_uk_mainmaster.ItemCD LIKE '%100%'\n" +
				"        AND jdbc_tool_test_pk_uk_mainmaster.ItemNA LIKE '%gef%'\n" +
				"    )", result.get(0).getSqlScript());

		String joinColumnFilterExpApiStr = "select(\"jdbc_tool_test_pk_uk_mainmaster\", \"jdbc_tool_test_pk_uk_mainmaster\")\n"+
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
				"col(\"jdbc_tool_test_mainmaster.SellPrice\"))\n"+
				".leftOuterJoinFK(\"FK_UnitMaster\", \"jdbc_tool_test_unitmaster\")\n" +
				".innerJoinFK(\"FK_MainMaster\", \"jdbc_tool_test_mainmaster\")\n" +
				".rightOuterJoinFK(\"FK_OrderMaster\", \"jdbc_tool_test_ordermaster\")\n"+
				".where(expMap($($(\"jdbc_tool_test_pk_uk_mainmaster.objectID = '1001'\").AND(\"jdbc_tool_test_unitmaster.Code = '1001'\").AND(\"jdbc_tool_test_mainmaster.objectID = '1001'\").AND(\"jdbc_tool_test_ordermaster.SlipNO LIKE '100%'\").AND(\"jdbc_tool_test_pk_uk_mainmaster.ItemCD LIKE '%100%'\").AND(\"jdbc_tool_test_pk_uk_mainmaster.ItemNA LIKE '%gef%'\")), Collections.emptyMap()))";

		assertEquals("List<GRecord> result = " + joinColumnFilterExpApiStr + "\n.findList();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE).toString()));
		assertEquals("GRecord result = " + joinColumnFilterExpApiStr + "\n.findRecord();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE).toString()));
		assertEquals("GRecordSet result = " + joinColumnFilterExpApiStr + "\n.findRecordSet();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE).toString()));
	}

	@Test
	public void testExport_searchExpWhereParts_JoinColumn_optionTrim() {

		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_searchExpWhereParts_JoinColumn_optionTrim.xml");
		ApiRequest apiRequest = parseRequestFromFile(getCommonSetting(),"jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_searchExpWhereparts_joincolumn_optionTrim.yaml");
		List<CodeResponse> result = convertVe2Code(apiRequest);

		assertEquals("SELECT", result.get(0).getType());
		assertEquals("仮想表ID: 940E79601B9143D687A8500F9915F7D8", result.get(0).getDescription());
		assertEquals("SELECT\n" +
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
				"    gef_jdbc_tool.jdbc_tool_test_pk_uk_mainmaster jdbc_tool_test_pk_uk_mainmaster\n" +
				"    LEFT OUTER JOIN gef_jdbc_tool.jdbc_tool_test_unitmaster jdbc_tool_test_unitmaster ON jdbc_tool_test_pk_uk_mainmaster.objectID = jdbc_tool_test_unitmaster.Code\n" +
				"    INNER JOIN gef_jdbc_tool.jdbc_tool_test_mainmaster jdbc_tool_test_mainmaster ON jdbc_tool_test_pk_uk_mainmaster.objectID = jdbc_tool_test_mainmaster.objectID\n" +
				"    RIGHT OUTER JOIN gef_jdbc_tool.jdbc_tool_test_ordermaster jdbc_tool_test_ordermaster ON jdbc_tool_test_pk_uk_mainmaster.ItemCD = jdbc_tool_test_ordermaster.objectID\n" +
				"WHERE\n" +
				"    (\n" +
				"        jdbc_tool_test_pk_uk_mainmaster.objectID = ?\n" +
				"        AND jdbc_tool_test_unitmaster.Code = ?\n" +
				"        AND jdbc_tool_test_mainmaster.objectID = ?\n" +
				"        AND jdbc_tool_test_ordermaster.SlipNO LIKE ? ESCAPE '/'\n" +
				"        AND jdbc_tool_test_pk_uk_mainmaster.ItemCD LIKE ? ESCAPE '/'\n" +
				"        AND jdbc_tool_test_pk_uk_mainmaster.ItemNA LIKE ? ESCAPE '/'\n" +
				"    )", result.get(0).getSqlScript());

		String whereParamsParts = "Map<String, Object> whereParams = new HashMap<>();\n"+
				"whereParams.put(\"jdbc_tool_test_unitmaster.Code\",);\n"+
				"whereParams.put(\"jdbc_tool_test_pk_uk_mainmaster.ItemCD\",);\n"+
				"whereParams.put(\"jdbc_tool_test_pk_uk_mainmaster.ItemNA\",);\n"+
				"whereParams.put(\"jdbc_tool_test_pk_uk_mainmaster.objectID\",);\n"+
				"whereParams.put(\"jdbc_tool_test_ordermaster.SlipNO\",);\n"+
				"whereParams.put(\"jdbc_tool_test_mainmaster.objectID\",);\n";
		String groupByApiStr = "select(\"jdbc_tool_test_pk_uk_mainmaster\", \"jdbc_tool_test_pk_uk_mainmaster\")\n"+
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
				"col(\"jdbc_tool_test_mainmaster.SellPrice\"))\n"+
				".leftOuterJoinFK(\"FK_UnitMaster\", \"jdbc_tool_test_unitmaster\")\n" +
				".innerJoinFK(\"FK_MainMaster\", \"jdbc_tool_test_mainmaster\")\n" +
				".rightOuterJoinFK(\"FK_OrderMaster\", \"jdbc_tool_test_ordermaster\")\n"+
				".where(expMap($($(\"jdbc_tool_test_pk_uk_mainmaster.objectID = [:jdbc_tool_test_pk_uk_mainmaster.objectID]\").AND(\"jdbc_tool_test_unitmaster.Code = $TRIM(:jdbc_tool_test_unitmaster.Code)\").AND(\"jdbc_tool_test_mainmaster.objectID = $TRIM([:jdbc_tool_test_mainmaster.objectID])\").AND(\"jdbc_tool_test_ordermaster.SlipNO LIKE $CONCAT(:jdbc_tool_test_ordermaster.SlipNO, '%') ESCAPE '/'\").AND(\"jdbc_tool_test_pk_uk_mainmaster.ItemCD LIKE $CONCAT('%', $CONCAT(:jdbc_tool_test_pk_uk_mainmaster.ItemCD, '%')) ESCAPE '/'\").AND(\"jdbc_tool_test_pk_uk_mainmaster.ItemNA LIKE $CONCAT('%', :jdbc_tool_test_pk_uk_mainmaster.ItemNA) ESCAPE '/'\")), whereParams))";

		assertEquals(whereParamsParts + "List<GRecord> result = " + groupByApiStr + "\n.findList();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE).toString()));
		assertEquals(whereParamsParts + "GRecord result = " + groupByApiStr + "\n.findRecord();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE).toString()));
		assertEquals(whereParamsParts + "GRecordSet result = " + groupByApiStr + "\n.findRecordSet();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE).toString()));
	}

	@Test
	public void testExport_join_mix_useForeignKeyFalse() {
//		doCustomConfig(getUseForeignKeyFalseConfigPath());
		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_join_mix.xml");
		SettingRequest request = getCommonSetting();
		request.getTransform().put("useForeignKey", false);
		request.getTransform().put("useExpMap", false);
		ApiRequest apiRequest = parseRequestFromFile(request,"jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_join_mix.yaml");

		List<CodeResponse> result = convertVe2Code(apiRequest);

		assertEquals("SELECT", result.get(0).getType());
		assertEquals("仮想表ID: E44ADEF7B30549449EDF193ECFB66631", result.get(0).getDescription());
		assertEquals("SELECT\n" +
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
				"    gef_jdbc_tool.jdbc_tool_test_pk_uk_mainmaster jdbc_tool_test_pk_uk_mainmaster\n" +
				"    LEFT OUTER JOIN gef_jdbc_tool.jdbc_tool_test_unitmaster jdbc_tool_test_unitmaster ON jdbc_tool_test_pk_uk_mainmaster.objectID = jdbc_tool_test_unitmaster.Code\n" +
				"    INNER JOIN gef_jdbc_tool.jdbc_tool_test_mainmaster jdbc_tool_test_mainmaster ON jdbc_tool_test_pk_uk_mainmaster.objectID = jdbc_tool_test_mainmaster.objectID\n" +
				"    RIGHT OUTER JOIN gef_jdbc_tool.jdbc_tool_test_ordermaster jdbc_tool_test_ordermaster ON jdbc_tool_test_pk_uk_mainmaster.ItemCD = jdbc_tool_test_ordermaster.objectID\n" +
				"WHERE\n" +
				"    jdbc_tool_test_pk_uk_mainmaster.objectID = ?", result.get(0).getSqlScript());

		String whereParamsParts = "Object[] whereParams = new Object[]{};\n";
		String groupByApiStr = "select(\"jdbc_tool_test_pk_uk_mainmaster\", \"jdbc_tool_test_pk_uk_mainmaster\")\n"+
				".fields(colsAll())\n"+
				".leftOuterJoin(\"jdbc_tool_test_unitmaster\", \"jdbc_tool_test_unitmaster\", exp($(\"jdbc_tool_test_pk_uk_mainmaster.objectID = jdbc_tool_test_unitmaster.Code\")))\n" +
				".innerJoin(\"jdbc_tool_test_mainmaster\", \"jdbc_tool_test_mainmaster\", exp($(\"jdbc_tool_test_pk_uk_mainmaster.objectID = jdbc_tool_test_mainmaster.objectID\")))\n" +
				".rightOuterJoin(\"jdbc_tool_test_ordermaster\", \"jdbc_tool_test_ordermaster\", exp($(\"jdbc_tool_test_pk_uk_mainmaster.ItemCD = jdbc_tool_test_ordermaster.objectID\")))\n"+
				".where(exp($(\"jdbc_tool_test_pk_uk_mainmaster.objectID = ?\"), whereParams))";

		assertEquals(whereParamsParts + "List<GRecord> result = " + groupByApiStr + "\n.findList();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE).toString()));
		assertEquals(whereParamsParts + "GRecord result = " + groupByApiStr + "\n.findRecord();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE).toString()));
		assertEquals(whereParamsParts + "GRecordSet result = " + groupByApiStr + "\n.findRecordSet();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE).toString()));
	}

	@Test
	public void testExport_searchExpWhereParts_JoinColumn() {

		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_searchExpWhereParts_JoinColumn.xml");
		ApiRequest apiRequest = parseRequestFromFile(getCommonSetting(),"jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_searchExpWhereparts_joincolumn.yaml");
		List<CodeResponse> result = convertVe2Code(apiRequest);

		assertEquals("SELECT", result.get(0).getType());
		assertEquals("仮想表ID: 0B064A3729EC4D65BB5A663562B96B51", result.get(0).getDescription());
		assertEquals("SELECT\n" +
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
				"    gef_jdbc_tool.jdbc_tool_test_pk_uk_mainmaster jdbc_tool_test_pk_uk_mainmaster\n" +
				"    LEFT OUTER JOIN gef_jdbc_tool.jdbc_tool_test_unitmaster jdbc_tool_test_unitmaster ON jdbc_tool_test_pk_uk_mainmaster.objectID = jdbc_tool_test_unitmaster.Code\n" +
				"    INNER JOIN gef_jdbc_tool.jdbc_tool_test_mainmaster jdbc_tool_test_mainmaster ON jdbc_tool_test_pk_uk_mainmaster.objectID = jdbc_tool_test_mainmaster.objectID\n" +
				"    RIGHT OUTER JOIN gef_jdbc_tool.jdbc_tool_test_ordermaster jdbc_tool_test_ordermaster ON jdbc_tool_test_pk_uk_mainmaster.ItemCD = jdbc_tool_test_ordermaster.objectID\n" +
				"WHERE\n" +
				"    (\n" +
				"        jdbc_tool_test_pk_uk_mainmaster.objectID = ?\n" +
				"        AND jdbc_tool_test_unitmaster.Code = ?\n" +
				"        AND jdbc_tool_test_mainmaster.objectID = ?\n" +
				"        AND jdbc_tool_test_ordermaster.SlipNO LIKE ? ESCAPE '/'\n" +
				"        AND jdbc_tool_test_pk_uk_mainmaster.ItemCD LIKE ? ESCAPE '/'\n" +
				"        AND jdbc_tool_test_pk_uk_mainmaster.ItemNA LIKE ? ESCAPE '/'\n" +
				"    )", result.get(0).getSqlScript());

		String whereParamsParts = "Map<String, Object> whereParams = new HashMap<>();\n"+
				"whereParams.put(\"jdbc_tool_test_unitmaster.Code\",);\n"+
				"whereParams.put(\"jdbc_tool_test_pk_uk_mainmaster.ItemCD\",);\n"+
				"whereParams.put(\"jdbc_tool_test_pk_uk_mainmaster.ItemNA\",);\n"+
				"whereParams.put(\"jdbc_tool_test_pk_uk_mainmaster.objectID\",);\n"+
				"whereParams.put(\"jdbc_tool_test_ordermaster.SlipNO\",);\n"+
				"whereParams.put(\"jdbc_tool_test_mainmaster.objectID\",);\n";
		String groupByApiStr = "select(\"jdbc_tool_test_pk_uk_mainmaster\", \"jdbc_tool_test_pk_uk_mainmaster\")\n"+
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
				"col(\"jdbc_tool_test_mainmaster.SellPrice\"))\n"+
				".leftOuterJoinFK(\"FK_UnitMaster\", \"jdbc_tool_test_unitmaster\")\n" +
				".innerJoinFK(\"FK_MainMaster\", \"jdbc_tool_test_mainmaster\")\n" +
				".rightOuterJoinFK(\"FK_OrderMaster\", \"jdbc_tool_test_ordermaster\")\n"+
				".where(expMap($($(\"jdbc_tool_test_pk_uk_mainmaster.objectID = :jdbc_tool_test_pk_uk_mainmaster.objectID\").AND(\"jdbc_tool_test_unitmaster.Code = :jdbc_tool_test_unitmaster.Code\").AND(\"jdbc_tool_test_mainmaster.objectID = :jdbc_tool_test_mainmaster.objectID\").AND(\"jdbc_tool_test_ordermaster.SlipNO LIKE $CONCAT(:jdbc_tool_test_ordermaster.SlipNO, '%') ESCAPE '/'\").AND(\"jdbc_tool_test_pk_uk_mainmaster.ItemCD LIKE $CONCAT('%', $CONCAT(:jdbc_tool_test_pk_uk_mainmaster.ItemCD, '%')) ESCAPE '/'\").AND(\"jdbc_tool_test_pk_uk_mainmaster.ItemNA LIKE $CONCAT('%', :jdbc_tool_test_pk_uk_mainmaster.ItemNA) ESCAPE '/'\")), whereParams))";

		assertEquals(whereParamsParts + "List<GRecord> result = " + groupByApiStr + "\n.findList();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE).toString()));
		assertEquals(whereParamsParts + "GRecord result = " + groupByApiStr + "\n.findRecord();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE).toString()));
		assertEquals(whereParamsParts + "GRecordSet result = " + groupByApiStr + "\n.findRecordSet();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE).toString()));
	}

	@Test
	public void testExport_filterExpWhereParts_JoinColumn_overrideAlias() {
//		doCustomConfig(getForceAliasColumnConfigPath());
		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_filterExpWhereParts_JoinColumn.xml");
		SettingRequest settingRequest = getCommonSetting();
		settingRequest.getTransform().put("useExpMap", false);
		settingRequest.getTransform().put("forceAliasColumn", true);

		ApiRequest apiRequest = parseRequestFromFile(settingRequest,"jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_filterExpWhereparts_joincolumn.yaml");

		List<CodeResponse> result = convertVe2Code(apiRequest);

		assertEquals("SELECT", result.get(0).getType());
		assertEquals("仮想表ID: 395DA3D2A7C541FDAFF20CC7AF7BBFE8", result.get(0).getDescription());
		assertEquals("SELECT\n" +
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
				"    gef_jdbc_tool.jdbc_tool_test_pk_uk_mainmaster jdbc_tool_test_pk_uk_mainmaster\n" +
				"    LEFT OUTER JOIN gef_jdbc_tool.jdbc_tool_test_unitmaster jdbc_tool_test_unitmaster ON jdbc_tool_test_pk_uk_mainmaster.objectID = jdbc_tool_test_unitmaster.Code\n" +
				"    INNER JOIN gef_jdbc_tool.jdbc_tool_test_mainmaster jdbc_tool_test_mainmaster ON jdbc_tool_test_pk_uk_mainmaster.objectID = jdbc_tool_test_mainmaster.objectID\n" +
				"    RIGHT OUTER JOIN gef_jdbc_tool.jdbc_tool_test_ordermaster jdbc_tool_test_ordermaster ON jdbc_tool_test_pk_uk_mainmaster.ItemCD = jdbc_tool_test_ordermaster.objectID\n" +
				"WHERE\n" +
				"    (\n" +
				"        jdbc_tool_test_pk_uk_mainmaster.objectID = '1001'\n" +
				"        AND jdbc_tool_test_unitmaster.Code = '1001'\n" +
				"        AND jdbc_tool_test_mainmaster.objectID = '1001'\n" +
				"        AND jdbc_tool_test_ordermaster.SlipNO LIKE '100%'\n" +
				"        AND jdbc_tool_test_pk_uk_mainmaster.ItemCD LIKE '%100%'\n" +
				"        AND jdbc_tool_test_pk_uk_mainmaster.ItemNA LIKE '%gef%'\n" +
				"    )", result.get(0).getSqlScript());

		String joinColumnFilterExpApiStr = "select(\"jdbc_tool_test_pk_uk_mainmaster\", \"jdbc_tool_test_pk_uk_mainmaster\")\n"+
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
				"col(\"jdbc_tool_test_mainmaster.SellPrice\", \"MainMasterSellPrice\"))\n"+
				".leftOuterJoinFK(\"FK_UnitMaster\", \"jdbc_tool_test_unitmaster\")\n" +
				".innerJoinFK(\"FK_MainMaster\", \"jdbc_tool_test_mainmaster\")\n" +
				".rightOuterJoinFK(\"FK_OrderMaster\", \"jdbc_tool_test_ordermaster\")\n"+
				".where(exp($($(\"jdbc_tool_test_pk_uk_mainmaster.objectID = '1001'\").AND(\"jdbc_tool_test_unitmaster.Code = '1001'\").AND(\"jdbc_tool_test_mainmaster.objectID = '1001'\").AND(\"jdbc_tool_test_ordermaster.SlipNO LIKE '100%'\").AND(\"jdbc_tool_test_pk_uk_mainmaster.ItemCD LIKE '%100%'\").AND(\"jdbc_tool_test_pk_uk_mainmaster.ItemNA LIKE '%gef%'\"))))";

		assertEquals("List<GRecord> result = " + joinColumnFilterExpApiStr + "\n.findList();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE).toString()));
		assertEquals("GRecord result = " + joinColumnFilterExpApiStr + "\n.findRecord();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE).toString()));
		assertEquals("GRecordSet result = " + joinColumnFilterExpApiStr + "\n.findRecordSet();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE).toString()));
	}

}
