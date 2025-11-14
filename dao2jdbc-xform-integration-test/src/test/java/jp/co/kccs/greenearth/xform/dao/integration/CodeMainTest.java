package jp.co.kccs.greenearth.xform.dao.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import jp.co.kccs.greenearth.commons.GFrameworkProperties;
import jp.co.kccs.greenearth.commons.GFrameworkUtils;
import jp.co.kccs.greenearth.commons.db.GConnectionManager;
import jp.co.kccs.greenearth.commons.utils.GFileUtils;
import jp.co.kccs.greenearth.testutils.dbunit.TestHelper;
import jp.co.kccs.greenearth.xform.code.jdbc.core.GCompoundApiImpl;
import jp.co.kccs.greenearth.xform.code.jdbc.core.GTerminatorApi;
import jp.co.kccs.greenearth.xform.code.jdbc.core.GWhereApi;
import jp.co.kccs.greenearth.xform.dao.common.GDao2Utils;
import jp.co.kccs.greenearth.xform.dao.common.GDaoCommonSetting;
import jp.co.kccs.greenearth.xform.jdbc.configuration.GCommonSetting;
import jp.co.kccs.greenearth.xform.jdbc.configuration.GXFormSettingHolder;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static jp.co.kccs.greenearth.xform.dao.integration.Utils.URL;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public abstract class CodeMainTest {
	protected static String dbSchema;

	@Before
	public void setup() {
		GFrameworkProperties.refleshProperty();
		GFrameworkUtils.initDIContainer(null);
		GConnectionManager connManager = GConnectionManager.getInstance();
		connManager.removeConnections();
		GXFormSettingHolder.clearCommonSetting();
	}
	RestTemplate restTemplate = new RestTemplate();
	protected static Yaml yaml = new Yaml();

	protected abstract String getCommonSettingPath();
	protected SettingRequest getCommonSetting()  {
		try (InputStream input = GFileUtils.getResource(getCommonSettingPath()).openStream()) {
			Map<String, Object> setting = yaml.load(input);
			ObjectMapper objectMapper = new ObjectMapper();
			SettingRequest settingRequest = objectMapper.convertValue(setting, SettingRequest.class);
			return settingRequest;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	@Test
	public void testExport_groupFunction() {

		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_groupFunction.xml");
		ApiRequest apiRequest = parseRequestFromFile(getCommonSetting(),"jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_groupFunction.yaml");

		List<CodeResponse> result = convertVe2Code(apiRequest);
		assertEquals("SELECT", result.get(0).getType());
		assertEquals("仮想表ID: E68EF7B747CA42B8ACF08908804DA0DB", result.get(0).getDescription());
		assertEquals("SELECT\n"+
				"    MAX(jdbc_tool_test_mainmaster.objectID),\n"+
				"    COUNT(jdbc_tool_test_mainmaster.ItemCD),\n"+
				"    SUM(jdbc_tool_test_mainmaster.UnitPrice),\n"+
				"    AVG(jdbc_tool_test_mainmaster.SellPrice),\n"+
				"    MIN(jdbc_tool_test_mainmaster.Flag),\n"+
				"    MAX(jdbc_tool_test_mainmaster.PriceFlag)\n"+
				"FROM\n"+
				"    " + dbSchema + ".jdbc_tool_test_mainmaster jdbc_tool_test_mainmaster\n"+
				"GROUP BY\n"+
				"    jdbc_tool_test_mainmaster.UnitPrice", result.get(0).getSqlScript());
		String apiStr = String.format("select(\"jdbc_tool_test_mainmaster\", \"jdbc_tool_test_mainmaster\")\n" +
				".fields(col(\"jdbc_tool_test_mainmaster.objectID\", GColumn.FuncType.MAX)," +
				"col(\"jdbc_tool_test_mainmaster.ItemCD\", GColumn.FuncType.COUNT)," +
				"col(\"jdbc_tool_test_mainmaster.UnitPrice\", GColumn.FuncType.SUM)," +
				"col(\"jdbc_tool_test_mainmaster.SellPrice\", GColumn.FuncType.AVG)," +
				"col(\"jdbc_tool_test_mainmaster.Flag\", GColumn.FuncType.MIN)," +
				"col(\"jdbc_tool_test_mainmaster.PriceFlag\", GColumn.FuncType.MAX))\n" +
				".groupBy(col(\"jdbc_tool_test_mainmaster.UnitPrice\"))");
		assertEquals("List<GRecord> result = " + apiStr + "\n.findList();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, null).toString().toString()));
		assertNull(result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE).toString().toString()));
		assertNull(result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE_PK).toString().toString()));
		assertNull(result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE_UK).toString().toString()));
		assertEquals("GRecord result = " + apiStr + "\n.findRecord();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, null).toString().toString()));
		assertNull(result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE).toString().toString()));
		assertNull(result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE_PK).toString().toString()));
		assertNull(result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE_UK).toString().toString()));
		assertEquals("GRecordSet result = " + apiStr + "\n.findRecordSet();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, null).toString().toString()));
		assertNull(result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE).toString().toString()));
		assertNull(result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE_PK).toString().toString()));
		assertNull(result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE_UK).toString().toString()));
	}


	@Test
	public void testExport_innerJoin() {
		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_innerJoin.xml");
		ApiRequest apiRequest = parseRequestFromFile(getCommonSetting(),"jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_innerJoin.yaml");
		List<CodeResponse> result = convertVe2Code(apiRequest);
		assertEquals("SELECT", result.get(0).getType());
		assertEquals("仮想表ID: 7042D90E3612454C88F9FE3AC4FD3322", result.get(0).getDescription());
		assertEquals("SELECT\n"+"    jdbc_tool_test_mainmaster.objectID,\n"+
						"    jdbc_tool_test_mainmaster.ItemCD,\n"+
						"    jdbc_tool_test_mainmaster.UnitPrice\n"+
						"FROM\n"+
						"    " + dbSchema + ".jdbc_tool_test_mainmaster jdbc_tool_test_mainmaster\n"+
						"    INNER JOIN " + dbSchema + ".jdbc_tool_test_unitmaster jdbc_tool_test_unitmaster ON jdbc_tool_test_mainmaster.ItemCD = jdbc_tool_test_unitmaster.Code"
				, result.get(0).getSqlScript());
		String innerJoinApiStr = String.format("select(\"jdbc_tool_test_mainmaster\", \"jdbc_tool_test_mainmaster\")\n" +
				".fields(col(\"jdbc_tool_test_mainmaster.objectID\"),col(\"jdbc_tool_test_mainmaster.ItemCD\"),col(\"jdbc_tool_test_mainmaster.UnitPrice\"))\n" +
				".innerJoinFK(\"FK\", \"jdbc_tool_test_unitmaster\")");

		assertEquals("List<GRecord> result = " + innerJoinApiStr + "\n.findList();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, null).toString()));
		assertNull(result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE).toString()));
		assertNull(result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE_PK).toString()));
		assertNull(result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE_UK).toString()));
		assertEquals("GRecord result = " + innerJoinApiStr + "\n.findRecord();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, null).toString()));
		assertNull(result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE).toString()));
		assertNull(result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE_PK).toString()));
		assertNull(result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE_UK).toString()));
		assertEquals("GRecordSet result = " + innerJoinApiStr + "\n.findRecordSet();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, null).toString()));
		assertNull(result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE).toString()));
		assertNull(result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE_PK).toString()));
		assertNull(result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE_UK).toString()));
	}

	@Test
	public void testExport_leftOuterJoin() {
		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_leftOuterJoin.xml");
		ApiRequest apiRequest = parseRequestFromFile(getCommonSetting(),"jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_leftOuterJoin.yaml");
		List<CodeResponse> result = convertVe2Code(apiRequest);
		assertEquals("SELECT", result.get(0).getType());
		assertEquals("仮想表ID: 5949D5C4FF624F538D10A7102A4DFB65", result.get(0).getDescription());
		assertEquals("SELECT\n"+
						"    jdbc_tool_test_mainmaster.objectID,\n"+
						"    jdbc_tool_test_mainmaster.ItemCD,\n"+
						"    jdbc_tool_test_mainmaster.UnitPrice\n"+
						"FROM\n"+
						"    " + dbSchema + ".jdbc_tool_test_mainmaster jdbc_tool_test_mainmaster\n"+
						"    LEFT OUTER JOIN " + dbSchema + ".jdbc_tool_test_ordermaster jdbc_tool_test_ordermaster ON jdbc_tool_test_mainmaster.ItemNA = jdbc_tool_test_ordermaster.CorporateNA"
				, result.get(0).getSqlScript());
		String leftOuterJoinApiStr = String.format("select(\"jdbc_tool_test_mainmaster\", \"jdbc_tool_test_mainmaster\")\n" +
				".fields(col(\"jdbc_tool_test_mainmaster.objectID\"),col(\"jdbc_tool_test_mainmaster.ItemCD\"),col(\"jdbc_tool_test_mainmaster.UnitPrice\"))\n" +
				".leftOuterJoinFK(\"FKleft\", \"jdbc_tool_test_ordermaster\")");

		assertEquals("List<GRecord> result = " + leftOuterJoinApiStr + "\n.findList();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, null).toString()));
		assertNull(result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE).toString()));
		assertNull(result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE_PK).toString()));
		assertNull(result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE_UK).toString()));
		assertEquals("GRecord result = " + leftOuterJoinApiStr + "\n.findRecord();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, null).toString()));
		assertNull(result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE).toString()));
		assertNull(result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE_PK).toString()));
		assertNull(result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE_UK).toString()));
		assertEquals("GRecordSet result = " + leftOuterJoinApiStr + "\n.findRecordSet();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, null).toString()));
		assertNull(result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE).toString()));
		assertNull(result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE_PK).toString()));
		assertNull(result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE_UK).toString()));
	}

	@Test
	public void testExport_rightOuterJoin() {
		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_rightOuterJoin.xml");
		ApiRequest apiRequest = parseRequestFromFile(getCommonSetting(),"jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_rightOuterJoin.yaml");

		List<CodeResponse> result = convertVe2Code(apiRequest);
		assertEquals("SELECT", result.get(0).getType());
		assertEquals("仮想表ID: 9F26B91A99A140DB92ECE5D84178FAE5", result.get(0).getDescription());
		assertEquals("SELECT\n"+
						"    jdbc_tool_test_unitmaster.objectID,\n"+
						"    jdbc_tool_test_unitmaster.Code,\n"+
						"    jdbc_tool_test_unitmaster.Name\n"+
						"FROM\n"+
						"    " + dbSchema + ".jdbc_tool_test_unitmaster jdbc_tool_test_unitmaster\n"+
						"    RIGHT OUTER JOIN " + dbSchema + ".jdbc_tool_test_ordermaster jdbc_tool_test_ordermaster ON jdbc_tool_test_unitmaster.Name = jdbc_tool_test_ordermaster.CorporateNA"
				, result.get(0).getSqlScript());
		String rightOuterJoinApiStr = String.format("select(\"jdbc_tool_test_unitmaster\", \"jdbc_tool_test_unitmaster\")\n" +
				".fields(colsAll())\n" +
				".rightOuterJoinFK(\"FKRight\", \"jdbc_tool_test_ordermaster\")");

		assertEquals("List<GRecord> result = " + rightOuterJoinApiStr + "\n.findList();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, null).toString()));
		assertNull(result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE).toString()));
		assertNull(result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE_PK).toString()));
		assertNull(result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE_UK).toString()));
		assertEquals("GRecord result = " + rightOuterJoinApiStr + "\n.findRecord();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, null).toString()));
		assertNull(result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE).toString()));
		assertNull(result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE_PK).toString()));
		assertNull(result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE_UK).toString()));
		assertEquals("GRecordSet result = " + rightOuterJoinApiStr + "\n.findRecordSet();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, null).toString()));
		assertNull(result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE).toString()));
		assertNull(result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE_PK).toString()));
		assertNull(result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE_UK).toString()));
	}

	@Test
	public void testExport_groupBy() {
		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_groupBy.xml");
		ApiRequest apiRequest = parseRequestFromFile(getCommonSetting(),"jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_groupBy.yaml");
		List<CodeResponse> result = convertVe2Code(apiRequest);assertEquals("SELECT", result.get(0).getType());
		assertEquals("仮想表ID: 0530ABBD2F0A4A0F8D7E6F58776897F7", result.get(0).getDescription());
		assertEquals("SELECT\n"+
				"    MAX(jdbc_tool_test_mainmaster.objectID),\n"+
				"    MAX(jdbc_tool_test_mainmaster.ItemCD),\n"+
				"    MAX(jdbc_tool_test_mainmaster.UnitPrice)\n"+
				"FROM\n"+
				"    " + dbSchema + ".jdbc_tool_test_mainmaster jdbc_tool_test_mainmaster\n"+
				"GROUP BY\n"+
				"    jdbc_tool_test_mainmaster.UnitPrice", result.get(0).getSqlScript());
		String groupByApiStr = String.format("select(\"jdbc_tool_test_mainmaster\", \"jdbc_tool_test_mainmaster\")\n" +
				".fields(col(\"jdbc_tool_test_mainmaster.objectID\", GColumn.FuncType.MAX)," +
				"col(\"jdbc_tool_test_mainmaster.ItemCD\", GColumn.FuncType.MAX)," +
				"col(\"jdbc_tool_test_mainmaster.UnitPrice\", GColumn.FuncType.MAX))\n" +
				".groupBy(col(\"jdbc_tool_test_mainmaster.UnitPrice\"))");

		assertEquals("List<GRecord> result = " + groupByApiStr + "\n.findList();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, null).toString()));
		assertEquals("GRecord result = " + groupByApiStr + "\n.findRecord();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, null).toString()));
		assertEquals("GRecordSet result = " + groupByApiStr + "\n.findRecordSet();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, null).toString()));
	}

	@Test
	public void testExport_multipleGroupBy() {
		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_multipleGroupBy.xml");
		ApiRequest apiRequest = parseRequestFromFile(getCommonSetting(),"jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_multipleGroupBy.yaml");
		List<CodeResponse> result = convertVe2Code(apiRequest);

		assertEquals("SELECT", result.get(0).getType());
		assertEquals("仮想表ID: F61014D80B8A41A8AD7016376CD055C0", result.get(0).getDescription());
		assertEquals("SELECT\n"+
				"    MAX(jdbc_tool_test_mainmaster.objectID),\n"+
				"    MAX(jdbc_tool_test_mainmaster.ItemCD),\n"+
				"    MAX(jdbc_tool_test_mainmaster.UnitPrice)\n"+
				"FROM\n"+
				"    " + dbSchema + ".jdbc_tool_test_mainmaster jdbc_tool_test_mainmaster\n"+
				"GROUP BY\n"+
				"    jdbc_tool_test_mainmaster.UnitPrice,\n"+
				"    jdbc_tool_test_mainmaster.ItemCD", result.get(0).getSqlScript());
		String multipleGroupByApiStr = String.format("select(\"jdbc_tool_test_mainmaster\", \"jdbc_tool_test_mainmaster\")\n" +
				".fields(col(\"jdbc_tool_test_mainmaster.objectID\", GColumn.FuncType.MAX)," +
				"col(\"jdbc_tool_test_mainmaster.ItemCD\", GColumn.FuncType.MAX)," +
				"col(\"jdbc_tool_test_mainmaster.UnitPrice\", GColumn.FuncType.MAX))\n" +
				".groupBy(col(\"jdbc_tool_test_mainmaster.UnitPrice\"),col(\"jdbc_tool_test_mainmaster.ItemCD\"))");

		assertEquals("List<GRecord> result = " + multipleGroupByApiStr + "\n.findList();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, null).toString()));
		assertEquals("GRecord result = " + multipleGroupByApiStr + "\n.findRecord();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, null).toString()));
		assertEquals("GRecordSet result = " + multipleGroupByApiStr + "\n.findRecordSet();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, null).toString()));
	}

	@Test
	public void testExport_orderByAsc() {
		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_orderByAsc.xml");
		ApiRequest apiRequest = parseRequestFromFile(getCommonSetting(),"jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_orderByAsc.yaml");
		List<CodeResponse> result = convertVe2Code(apiRequest);

		assertEquals("SELECT", result.get(0).getType());
		assertEquals("仮想表ID: F8B25A24B8D44D8F9F31D75A4A6E434D", result.get(0).getDescription());
		assertEquals("SELECT\n"+
				"    jdbc_tool_test_mainmaster.objectID,\n"+
				"    jdbc_tool_test_mainmaster.ItemCD,\n"+
				"    jdbc_tool_test_mainmaster.UnitPrice\n"+
				"FROM\n"+
				"    " + dbSchema + ".jdbc_tool_test_mainmaster jdbc_tool_test_mainmaster\n"+
				"ORDER BY\n"+
				"    jdbc_tool_test_mainmaster.UnitPrice ASC", result.get(0).getSqlScript());
		String orderByApiStr = String.format("select(\"jdbc_tool_test_mainmaster\", \"jdbc_tool_test_mainmaster\")\n" +
				".fields(col(\"jdbc_tool_test_mainmaster.objectID\")," +
				"col(\"jdbc_tool_test_mainmaster.ItemCD\")," +
				"col(\"jdbc_tool_test_mainmaster.UnitPrice\"))\n" +
				".orderBy(asc(\"jdbc_tool_test_mainmaster.UnitPrice\"))");

		assertEquals("List<GRecord> result = " + orderByApiStr + "\n.findList();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, null).toString()));
		assertEquals("GRecord result = " + orderByApiStr + "\n.findRecord();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, null).toString()));
		assertEquals("GRecordSet result = " + orderByApiStr + "\n.findRecordSet();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, null).toString()));
	}

	@Test
	public void testExport_multipleOrderByAsc() {
		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_multipleOrderByAsc.xml");
		ApiRequest apiRequest = parseRequestFromFile(getCommonSetting(),"jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_multipleOrderByAsc.yaml");
		List<CodeResponse> result = convertVe2Code(apiRequest);

		assertEquals("SELECT", result.get(0).getType());
		assertEquals("仮想表ID: DD05A220FF144B60A3E7CE4E0C9CA2AA", result.get(0).getDescription());
		assertEquals("SELECT\n"+
				"    jdbc_tool_test_mainmaster.objectID,\n"+
				"    jdbc_tool_test_mainmaster.ItemCD,\n"+
				"    jdbc_tool_test_mainmaster.UnitPrice\n"+
				"FROM\n"+
				"    " + dbSchema + ".jdbc_tool_test_mainmaster jdbc_tool_test_mainmaster\n"+
				"ORDER BY\n"+
				"    jdbc_tool_test_mainmaster.UnitPrice ASC,\n"+
				"    jdbc_tool_test_mainmaster.ItemCD ASC", result.get(0).getSqlScript());
		String multipleOrderByApiStr = String.format("select(\"jdbc_tool_test_mainmaster\", \"jdbc_tool_test_mainmaster\")\n" +
				".fields(col(\"jdbc_tool_test_mainmaster.objectID\")," +
				"col(\"jdbc_tool_test_mainmaster.ItemCD\")," +
				"col(\"jdbc_tool_test_mainmaster.UnitPrice\"))\n" +
				".orderBy(asc(\"jdbc_tool_test_mainmaster.UnitPrice\"),asc(\"jdbc_tool_test_mainmaster.ItemCD\"))");

		assertEquals("List<GRecord> result = " + multipleOrderByApiStr + "\n.findList();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, null).toString()));
		assertEquals("GRecord result = " + multipleOrderByApiStr + "\n.findRecord();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, null).toString()));
		assertEquals("GRecordSet result = " + multipleOrderByApiStr + "\n.findRecordSet();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, null).toString()));
	}

	@Test
	public void testExport_orderByDesc() {
		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_orderByDesc.xml");
		ApiRequest apiRequest = parseRequestFromFile(getCommonSetting(),"jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_orderByDesc.yaml");
		List<CodeResponse> result = convertVe2Code(apiRequest);

		assertEquals("SELECT", result.get(0).getType());
		assertEquals("仮想表ID: C1F0698184FB4E058A5704FA7B87855B", result.get(0).getDescription());
		assertEquals("SELECT\n"+
				"    jdbc_tool_test_mainmaster.objectID,\n"+
				"    jdbc_tool_test_mainmaster.ItemCD,\n"+
				"    jdbc_tool_test_mainmaster.UnitPrice\n"+
				"FROM\n"+
				"    " + dbSchema + ".jdbc_tool_test_mainmaster jdbc_tool_test_mainmaster\n"+
				"ORDER BY\n"+
				"    jdbc_tool_test_mainmaster.UnitPrice DESC", result.get(0).getSqlScript());
		String orderByDescApiStr = String.format("select(\"jdbc_tool_test_mainmaster\", \"jdbc_tool_test_mainmaster\")\n" +
				".fields(col(\"jdbc_tool_test_mainmaster.objectID\")," +
				"col(\"jdbc_tool_test_mainmaster.ItemCD\")," +
				"col(\"jdbc_tool_test_mainmaster.UnitPrice\"))\n" +
				".orderBy(desc(\"jdbc_tool_test_mainmaster.UnitPrice\"))");

		assertEquals("List<GRecord> result = " + orderByDescApiStr + "\n.findList();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, null).toString()));
		assertEquals("GRecord result = " + orderByDescApiStr + "\n.findRecord();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, null).toString()));
		assertEquals("GRecordSet result = " + orderByDescApiStr + "\n.findRecordSet();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, null).toString()));
	}

	@Test
	public void testExport_multipleOrderByDesc() {
		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_multipleOrderByDesc.xml");
		ApiRequest apiRequest = parseRequestFromFile(getCommonSetting(),"jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_multipleOrderByDesc.yaml");
		List<CodeResponse> result = convertVe2Code(apiRequest);

		assertEquals("SELECT", result.get(0).getType());
		assertEquals("仮想表ID: 1FB32673659841CAA624EB1C98C14BA4", result.get(0).getDescription());
		assertEquals("SELECT\n"+
				"    jdbc_tool_test_mainmaster.objectID,\n"+
				"    jdbc_tool_test_mainmaster.ItemCD,\n"+
				"    jdbc_tool_test_mainmaster.UnitPrice\n"+
				"FROM\n"+
				"    " + dbSchema + ".jdbc_tool_test_mainmaster jdbc_tool_test_mainmaster\n"+
				"ORDER BY\n"+
				"    jdbc_tool_test_mainmaster.UnitPrice DESC,\n"+
				"    jdbc_tool_test_mainmaster.ItemCD DESC", result.get(0).getSqlScript());
		String multipleOrderByDescApiStr = String.format("select(\"jdbc_tool_test_mainmaster\", \"jdbc_tool_test_mainmaster\")\n" +
				".fields(col(\"jdbc_tool_test_mainmaster.objectID\")," +
				"col(\"jdbc_tool_test_mainmaster.ItemCD\")," +
				"col(\"jdbc_tool_test_mainmaster.UnitPrice\"))\n" +
				".orderBy(desc(\"jdbc_tool_test_mainmaster.UnitPrice\"),desc(\"jdbc_tool_test_mainmaster.ItemCD\"))");

		assertEquals("List<GRecord> result = " + multipleOrderByDescApiStr + "\n.findList();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, null).toString()));
		assertEquals("GRecord result = " + multipleOrderByDescApiStr + "\n.findRecord();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, null).toString()));
		assertEquals("GRecordSet result = " + multipleOrderByDescApiStr + "\n.findRecordSet();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, null).toString()));
	}

	@Test
	public void testExport_orderByAscDescMix() {
		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_orderByAscDescMix.xml");
		ApiRequest apiRequest = parseRequestFromFile(getCommonSetting(),"jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_orderByAscDescMix.yaml");
		List<CodeResponse> result = convertVe2Code(apiRequest);

		assertEquals("SELECT", result.get(0).getType());
		assertEquals("仮想表ID: EC68E86DD2894D8496765DCD8189C735", result.get(0).getDescription());
		assertEquals("SELECT\n"+
				"    jdbc_tool_test_mainmaster.objectID,\n"+
				"    jdbc_tool_test_mainmaster.ItemCD,\n"+
				"    jdbc_tool_test_mainmaster.UnitPrice\n"+
				"FROM\n"+
				"    " + dbSchema + ".jdbc_tool_test_mainmaster jdbc_tool_test_mainmaster\n"+
				"ORDER BY\n"+
				"    jdbc_tool_test_mainmaster.UnitPrice DESC,\n"+
				"    jdbc_tool_test_mainmaster.ItemCD ASC", result.get(0).getSqlScript());
		String orderByAscDescMixApiStr = String.format("select(\"jdbc_tool_test_mainmaster\", \"jdbc_tool_test_mainmaster\")\n" +
				".fields(col(\"jdbc_tool_test_mainmaster.objectID\")," +
				"col(\"jdbc_tool_test_mainmaster.ItemCD\")," +
				"col(\"jdbc_tool_test_mainmaster.UnitPrice\"))\n" +
				".orderBy(desc(\"jdbc_tool_test_mainmaster.UnitPrice\"),asc(\"jdbc_tool_test_mainmaster.ItemCD\"))");

		assertEquals("List<GRecord> result = " + orderByAscDescMixApiStr + "\n.findList();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, null).toString()));
		assertEquals("GRecord result = " + orderByAscDescMixApiStr + "\n.findRecord();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, null).toString()));
		assertEquals("GRecordSet result = " + orderByAscDescMixApiStr + "\n.findRecordSet();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, null).toString()));
	}

	@Test
	public void testExport_searchExpWherePartsLikeF() {
		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_searchExpWherePartsLikeF.xml");
		ApiRequest apiRequest = parseRequestFromFile(getCommonSetting(),"jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_searchExpWherePartsLikeF.yaml");
		List<CodeResponse> result = convertVe2Code(apiRequest);

		assertEquals("SELECT", result.get(0).getType());
		assertEquals("仮想表ID: 27A84C6E3DCC43939E45785E4088039E", result.get(0).getDescription());
		assertEquals("SELECT\n"+
				"    jdbc_tool_test_mainmaster.objectID,\n"+
				"    jdbc_tool_test_mainmaster.ItemCD,\n"+
				"    jdbc_tool_test_mainmaster.UnitPrice\n"+
				"FROM\n"+
				"    " + dbSchema + ".jdbc_tool_test_mainmaster jdbc_tool_test_mainmaster\n"+
				"WHERE\n"+
				"    jdbc_tool_test_mainmaster.ItemCD LIKE ? ESCAPE '/'", result.get(0).getSqlScript());
		String whereParamsParts = "Map<String, Object> whereParams = new HashMap<>();\n"+
				"whereParams.put(\"jdbc_tool_test_mainmaster.ItemCD\",);\n";
		String searchExpWherePartsLikeFApiStr = "select(\"jdbc_tool_test_mainmaster\", \"jdbc_tool_test_mainmaster\")\n"+
				".fields(col(\"jdbc_tool_test_mainmaster.objectID\"),col(\"jdbc_tool_test_mainmaster.ItemCD\"),col(\"jdbc_tool_test_mainmaster.UnitPrice\"))\n"+
				".where(expMap($(\"jdbc_tool_test_mainmaster.ItemCD LIKE $CONCAT(:jdbc_tool_test_mainmaster.ItemCD, '%') ESCAPE '/'\"), whereParams))";

		assertEquals(whereParamsParts + "List<GRecord> result = " + searchExpWherePartsLikeFApiStr + "\n.findList();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE).toString()));
		assertEquals(whereParamsParts + "GRecord result = " + searchExpWherePartsLikeFApiStr + "\n.findRecord();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE).toString()));
		assertEquals(whereParamsParts + "GRecordSet result = " + searchExpWherePartsLikeFApiStr + "\n.findRecordSet();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE).toString()));
	}

	@Test
	public void testExport_searchExpWherePartsLikeFB() {
		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_searchExpWherePartsLikeFB.xml");
		ApiRequest apiRequest = parseRequestFromFile(getCommonSetting(),"jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_searchExpWherePartsLikeFB.yaml");
		List<CodeResponse> result = convertVe2Code(apiRequest);

		assertEquals("SELECT", result.get(0).getType());
		assertEquals("仮想表ID: F8DA3B5A055A4402945AE83B013884A4", result.get(0).getDescription());
		assertEquals("SELECT\n"+
				"    jdbc_tool_test_unitmaster.objectID,\n"+
				"    jdbc_tool_test_unitmaster.Code,\n"+
				"    jdbc_tool_test_unitmaster.Name\n"+
				"FROM\n"+
				"    " + dbSchema + ".jdbc_tool_test_unitmaster jdbc_tool_test_unitmaster\n"+
				"WHERE\n"+
				"    jdbc_tool_test_unitmaster.Name LIKE ? ESCAPE '/'", result.get(0).getSqlScript());
		String whereParamsParts = "Map<String, Object> whereParams = new HashMap<>();\n"+
				"whereParams.put(\"jdbc_tool_test_unitmaster.Name\",);\n";
		String groupByApiStr = "select(\"jdbc_tool_test_unitmaster\", \"jdbc_tool_test_unitmaster\")\n"+
				".fields(colsAll())\n"+
				".where(expMap($(\"jdbc_tool_test_unitmaster.Name LIKE $CONCAT('%', $CONCAT(:jdbc_tool_test_unitmaster.Name, '%')) ESCAPE '/'\"), whereParams))";

		assertEquals(whereParamsParts + "List<GRecord> result = " + groupByApiStr + "\n.findList();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE).toString()));
		assertEquals(whereParamsParts + "GRecord result = " + groupByApiStr + "\n.findRecord();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE).toString()));
		assertEquals(whereParamsParts + "GRecordSet result = " + groupByApiStr + "\n.findRecordSet();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE).toString()));
	}

	@Test
	public void testExport_searchExpWherePartsLikeB() {
		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_searchExpWherePartsLikeB.xml");
		ApiRequest apiRequest = parseRequestFromFile(getCommonSetting(),"jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_searchExpWherePartsLikeB.yaml");
		List<CodeResponse> result = convertVe2Code(apiRequest);

		assertEquals("SELECT", result.get(0).getType());
		assertEquals("仮想表ID: E463537F2A224BABBAB675A335772A7F", result.get(0).getDescription());
		assertEquals("SELECT\n"+
				"    jdbc_tool_test_ordermaster.objectID,\n"+
				"    jdbc_tool_test_ordermaster.SlipNO,\n"+
				"    jdbc_tool_test_ordermaster.CorporateNA\n"+
				"FROM\n"+
				"    " + dbSchema + ".jdbc_tool_test_ordermaster jdbc_tool_test_ordermaster\n"+
				"WHERE\n"+
				"    jdbc_tool_test_ordermaster.CorporateNA LIKE ? ESCAPE '/'", result.get(0).getSqlScript());
		String whereParamsParts = "Map<String, Object> whereParams = new HashMap<>();\n"+
				"whereParams.put(\"jdbc_tool_test_ordermaster.CorporateNA\",);\n";
		String groupByApiStr = "select(\"jdbc_tool_test_ordermaster\", \"jdbc_tool_test_ordermaster\")\n"+
				".fields(col(\"jdbc_tool_test_ordermaster.objectID\")," +
				"col(\"jdbc_tool_test_ordermaster.SlipNO\")," +
				"col(\"jdbc_tool_test_ordermaster.CorporateNA\"))\n"+
				".where(expMap($(\"jdbc_tool_test_ordermaster.CorporateNA LIKE $CONCAT('%', :jdbc_tool_test_ordermaster.CorporateNA) ESCAPE '/'\"), whereParams))";

		assertEquals(whereParamsParts + "List<GRecord> result = " + groupByApiStr + "\n.findList();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE).toString()));
		assertEquals(whereParamsParts + "GRecord result = " + groupByApiStr + "\n.findRecord();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE).toString()));
		assertEquals(whereParamsParts + "GRecordSet result = " + groupByApiStr + "\n.findRecordSet();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE).toString()));
	}

	@Test
	public void testExport_searchExpWherePartsEquals() {

		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_searchExpWherePartsEquals.xml");
		ApiRequest apiRequest = parseRequestFromFile(getCommonSetting(),"jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_searchExpWherePartsEquals.yaml");
		List<CodeResponse> result = convertVe2Code(apiRequest);

		assertEquals("SELECT", result.get(0).getType());
		assertEquals("仮想表ID: 4E52A89202B145159B8256C53A433A24", result.get(0).getDescription());
		assertEquals("SELECT\n"+
				"    jdbc_tool_test_mainmaster.objectID,\n"+
				"    jdbc_tool_test_mainmaster.ItemCD,\n"+
				"    jdbc_tool_test_mainmaster.UnitPrice\n"+
				"FROM\n"+
				"    " + dbSchema + ".jdbc_tool_test_mainmaster jdbc_tool_test_mainmaster\n"+
				"WHERE\n"+
				"    (\n"+
				"        jdbc_tool_test_mainmaster.ItemCD = ?\n"+
				"        AND jdbc_tool_test_mainmaster.UnitPrice <> ?\n"+
				"    )", result.get(0).getSqlScript());
		String whereParamsParts = "Map<String, Object> whereParams = new HashMap<>();\n"+
				"whereParams.put(\"jdbc_tool_test_mainmaster.UnitPrice\",);\n"+
				"whereParams.put(\"jdbc_tool_test_mainmaster.ItemCD\",);\n";
		String groupByApiStr = String.format("select(\"jdbc_tool_test_mainmaster\", \"jdbc_tool_test_mainmaster\")\n" +
				".fields(col(\"jdbc_tool_test_mainmaster.objectID\")," +
				"col(\"jdbc_tool_test_mainmaster.ItemCD\")," +
				"col(\"jdbc_tool_test_mainmaster.UnitPrice\"))\n" +
				".where(expMap($($(\"jdbc_tool_test_mainmaster.ItemCD = $TRIM([:jdbc_tool_test_mainmaster.ItemCD])\").AND(\"jdbc_tool_test_mainmaster.UnitPrice <> $TRIM([:jdbc_tool_test_mainmaster.UnitPrice])\")), whereParams))");

		assertEquals(whereParamsParts + "List<GRecord> result = " + groupByApiStr + "\n.findList();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE).toString()));
		assertEquals(whereParamsParts + "GRecord result = " + groupByApiStr + "\n.findRecord();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE).toString()));
		assertEquals(whereParamsParts + "GRecordSet result = " + groupByApiStr + "\n.findRecordSet();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE).toString()));
	}

	@Test
	public void testExport_searchExpWherePartsCompare() {

		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_searchExpWherePartsCompare.xml");
		ApiRequest apiRequest = parseRequestFromFile(getCommonSetting(),"jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_searchExpWherePartsCompare.yaml");
		List<CodeResponse> result = convertVe2Code(apiRequest);

		assertEquals("SELECT", result.get(0).getType());
		assertEquals("仮想表ID: 9F4455A3BB6C49438764521C9331F01C", result.get(0).getDescription());
		assertEquals("SELECT\n"+
				"    jdbc_tool_test_mainmaster.objectID,\n"+
				"    jdbc_tool_test_mainmaster.ItemCD,\n"+
				"    jdbc_tool_test_mainmaster.UnitPrice,\n"+
				"    jdbc_tool_test_mainmaster.SellPrice,\n"+
				"    jdbc_tool_test_mainmaster.Flag,\n"+
				"    jdbc_tool_test_mainmaster.PriceFlag\n"+
				"FROM\n"+
				"    " + dbSchema + ".jdbc_tool_test_mainmaster jdbc_tool_test_mainmaster\n"+
				"WHERE\n"+
				"    (\n"+
				"        jdbc_tool_test_mainmaster.UnitPrice > ?\n"+
				"        AND jdbc_tool_test_mainmaster.SellPrice < ?\n"+
				"        AND jdbc_tool_test_mainmaster.Flag <= ?\n"+
				"        AND jdbc_tool_test_mainmaster.PriceFlag >= ?\n"+
				"    )", result.get(0).getSqlScript());
		String whereParamsParts = "Map<String, Object> whereParams = new HashMap<>();\n"+
				"whereParams.put(\"jdbc_tool_test_mainmaster.Flag\",);\n"+
				"whereParams.put(\"jdbc_tool_test_mainmaster.UnitPrice\",);\n"+
				"whereParams.put(\"jdbc_tool_test_mainmaster.PriceFlag\",);\n"+
				"whereParams.put(\"jdbc_tool_test_mainmaster.SellPrice\",);\n";
		String groupByApiStr = String.format("select(\"jdbc_tool_test_mainmaster\", \"jdbc_tool_test_mainmaster\")\n" +
				".fields(col(\"jdbc_tool_test_mainmaster.objectID\")," +
				"col(\"jdbc_tool_test_mainmaster.ItemCD\")," +
				"col(\"jdbc_tool_test_mainmaster.UnitPrice\")," +
				"col(\"jdbc_tool_test_mainmaster.SellPrice\")," +
				"col(\"jdbc_tool_test_mainmaster.Flag\")," +
				"col(\"jdbc_tool_test_mainmaster.PriceFlag\"))\n" +
				".where(expMap($($(\"jdbc_tool_test_mainmaster.UnitPrice > :jdbc_tool_test_mainmaster.UnitPrice\").AND(\"jdbc_tool_test_mainmaster.SellPrice < :jdbc_tool_test_mainmaster.SellPrice\").AND(\"jdbc_tool_test_mainmaster.Flag <= :jdbc_tool_test_mainmaster.Flag\").AND(\"jdbc_tool_test_mainmaster.PriceFlag >= :jdbc_tool_test_mainmaster.PriceFlag\")), whereParams))");

		assertEquals(whereParamsParts + "List<GRecord> result = " + groupByApiStr + "\n.findList();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE).toString()));
		assertEquals(whereParamsParts + "GRecord result = " + groupByApiStr + "\n.findRecord();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE).toString()));
		assertEquals(whereParamsParts + "GRecordSet result = " + groupByApiStr + "\n.findRecordSet();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE).toString()));
	}

	@Test
	public void testExport_searchExpWherePartsOptionTrim() {

		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_searchExpWherePartsOptionTrim.xml");
		ApiRequest apiRequest = parseRequestFromFile(getCommonSetting(),"jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_searchExpWherePartsOptionTrim.yaml");
		List<CodeResponse> result = convertVe2Code(apiRequest);

		assertEquals("SELECT", result.get(0).getType());
		assertEquals("仮想表ID: D6FA080F2A074718821223C549ABEFD4", result.get(0).getDescription());
		assertEquals("SELECT\n"+
				"    jdbc_tool_test_mainmaster.objectID,\n"+
				"    jdbc_tool_test_mainmaster.ItemCD,\n"+
				"    jdbc_tool_test_mainmaster.UnitPrice,\n"+
				"    jdbc_tool_test_mainmaster.RegisteredDT,\n"+
				"    jdbc_tool_test_mainmaster.ExclusiveFG\n"+
				"FROM\n"+
				"    " + dbSchema + ".jdbc_tool_test_mainmaster jdbc_tool_test_mainmaster\n"+
				"    INNER JOIN " + dbSchema + ".jdbc_tool_test_unitmaster jdbc_tool_test_unitmaster ON jdbc_tool_test_mainmaster.ItemCD = jdbc_tool_test_unitmaster.Code\n"+
				"WHERE\n"+
				"    jdbc_tool_test_mainmaster.UnitPrice IN (100, 101, 102)\n"+
				"    AND (\n"+
				"        jdbc_tool_test_mainmaster.objectID LIKE ? ESCAPE '/'\n"+
				"        AND jdbc_tool_test_mainmaster.UnitPrice >= ?\n"+
				"    )\n"+
				"ORDER BY\n"+
				"    jdbc_tool_test_mainmaster.RegisteredDT ASC", result.get(0).getSqlScript());
		String whereParamsParts = "Map<String, Object> whereParams = new HashMap<>();\n"+
				"whereParams.put(\"jdbc_tool_test_mainmaster.UnitPrice\",);\n"+
				"whereParams.put(\"jdbc_tool_test_mainmaster.objectID\",);\n";
		String groupByApiStr = "select(\"jdbc_tool_test_mainmaster\", \"jdbc_tool_test_mainmaster\")\n"+
				".fields(col(\"jdbc_tool_test_mainmaster.objectID\")," +
				"col(\"jdbc_tool_test_mainmaster.ItemCD\")," +
				"col(\"jdbc_tool_test_mainmaster.UnitPrice\")," +
				"col(\"jdbc_tool_test_mainmaster.RegisteredDT\")," +
				"col(\"jdbc_tool_test_mainmaster.ExclusiveFG\"))\n"+
				".innerJoinFK(\"FK\", \"jdbc_tool_test_unitmaster\")\n"+
				".orderBy(asc(\"jdbc_tool_test_mainmaster.RegisteredDT\"))\n"+
				".where(expMap($(\"jdbc_tool_test_mainmaster.UnitPrice IN (100, 101, 102)\").AND($(\"jdbc_tool_test_mainmaster.objectID LIKE $CONCAT('%', $CONCAT($TRIM([:jdbc_tool_test_mainmaster.objectID]), '%')) ESCAPE '/'\").AND(\"jdbc_tool_test_mainmaster.UnitPrice >= $TRIM([:jdbc_tool_test_mainmaster.UnitPrice])\")), whereParams))";

		assertEquals(whereParamsParts + "List<GRecord> result = " + groupByApiStr + "\n.findList();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE).toString()));
		assertEquals(whereParamsParts + "GRecord result = " + groupByApiStr + "\n.findRecord();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE).toString()));
		assertEquals(whereParamsParts + "GRecordSet result = " + groupByApiStr + "\n.findRecordSet();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE).toString()));
	}

	@Test
	public void testExport_filterExpWherePartsFilterLikeF() {

		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_filterExpWherePartsFilterLikeF.xml");
		ApiRequest apiRequest = parseRequestFromFile(getCommonSetting(),"jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_filterExpWherePartsLikeF.yaml");
		List<CodeResponse> result = convertVe2Code(apiRequest);

		assertEquals("SELECT", result.get(0).getType());
		assertEquals("仮想表ID: 5423003851B248BEA1621ACFB69B4D28", result.get(0).getDescription());
		assertEquals("SELECT\n"+
				"    jdbc_tool_test_mainmaster.objectID,\n"+
				"    jdbc_tool_test_mainmaster.ItemCD,\n"+
				"    jdbc_tool_test_mainmaster.UnitPrice\n"+
				"FROM\n"+
				"    " + dbSchema + ".jdbc_tool_test_mainmaster jdbc_tool_test_mainmaster\n"+
				"WHERE\n"+
				"    jdbc_tool_test_mainmaster.ItemCD LIKE 'item%'", result.get(0).getSqlScript());
		String groupByApiStr = "select(\"jdbc_tool_test_mainmaster\", \"jdbc_tool_test_mainmaster\")\n"+
				".fields(col(\"jdbc_tool_test_mainmaster.objectID\")," +
				"col(\"jdbc_tool_test_mainmaster.ItemCD\")," +
				"col(\"jdbc_tool_test_mainmaster.UnitPrice\"))\n"+
				".where(expMap($(\"jdbc_tool_test_mainmaster.ItemCD LIKE 'item%'\"), Collections.emptyMap()))";

		assertEquals("List<GRecord> result = " + groupByApiStr + "\n.findList();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE).toString()));
		assertEquals("GRecord result = " + groupByApiStr + "\n.findRecord();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE).toString()));
		assertEquals("GRecordSet result = " + groupByApiStr + "\n.findRecordSet();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE).toString()));
	}

	@Test
	public void testExport_filterExpWherePartsFilterLikeFB() {

		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_filterExpWherePartsFilterLikeFB.xml");
		ApiRequest apiRequest = parseRequestFromFile(getCommonSetting(),"jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_filterExpWherePartsLikeFB.yaml");
		List<CodeResponse> result = convertVe2Code(apiRequest);

		assertEquals("SELECT", result.get(0).getType());
		assertEquals("仮想表ID: 581587D1CA97431A92611EEC6587C09A", result.get(0).getDescription());
		assertEquals("SELECT\n"+
				"    jdbc_tool_test_unitmaster.objectID,\n"+
				"    jdbc_tool_test_unitmaster.Code,\n"+
				"    jdbc_tool_test_unitmaster.Name\n"+
				"FROM\n"+
				"    " + dbSchema + ".jdbc_tool_test_unitmaster jdbc_tool_test_unitmaster\n"+
				"WHERE\n"+
				"    jdbc_tool_test_unitmaster.Name LIKE '%name%'", result.get(0).getSqlScript());
		String groupByApiStr = "select(\"jdbc_tool_test_unitmaster\", \"jdbc_tool_test_unitmaster\")\n"+
				".fields(colsAll())\n"+
				".where(expMap($(\"jdbc_tool_test_unitmaster.Name LIKE '%name%'\"), Collections.emptyMap()))";

		assertEquals("List<GRecord> result = " + groupByApiStr + "\n.findList();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE).toString()));
		assertEquals("GRecord result = " + groupByApiStr + "\n.findRecord();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE).toString()));
		assertEquals("GRecordSet result = " + groupByApiStr + "\n.findRecordSet();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE).toString()));
	}

	@Test
	public void testExport_filterExpWherePartsFilterLikeB() {

		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_filterExpWherePartsFilterLikeB.xml");

		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_filterExpWherePartsFilterLikeB.xml");
		ApiRequest apiRequest = parseRequestFromFile(getCommonSetting(),"jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_filterExpWherePartsLikeB.yaml");
		List<CodeResponse> result = convertVe2Code(apiRequest);

		assertEquals("SELECT", result.get(0).getType());
		assertEquals("仮想表ID: 4DD746B1BD794D3B83C42272C9615E67", result.get(0).getDescription());
		assertEquals("SELECT\n"+
				"    jdbc_tool_test_ordermaster.objectID,\n"+
				"    jdbc_tool_test_ordermaster.SlipNO,\n"+
				"    jdbc_tool_test_ordermaster.CorporateNA\n"+
				"FROM\n"+
				"    " + dbSchema + ".jdbc_tool_test_ordermaster jdbc_tool_test_ordermaster\n"+
				"WHERE\n"+
				"    jdbc_tool_test_ordermaster.CorporateNA LIKE '%na'", result.get(0).getSqlScript());
		String groupByApiStr = "select(\"jdbc_tool_test_ordermaster\", \"jdbc_tool_test_ordermaster\")\n"+
				".fields(col(\"jdbc_tool_test_ordermaster.objectID\")," +
				"col(\"jdbc_tool_test_ordermaster.SlipNO\")," +
				"col(\"jdbc_tool_test_ordermaster.CorporateNA\"))\n"+
				".where(expMap($(\"jdbc_tool_test_ordermaster.CorporateNA LIKE '%na'\"), Collections.emptyMap()))";

		assertEquals("List<GRecord> result = " + groupByApiStr + "\n.findList();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE).toString()));
		assertEquals("GRecord result = " + groupByApiStr + "\n.findRecord();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE).toString()));
		assertEquals("GRecordSet result = " + groupByApiStr + "\n.findRecordSet();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE).toString()));
	}

	@Test
	public void testExport_filterExpWherePartsEquals() {

		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_filterExpWherePartsEquals.xml");
		ApiRequest apiRequest = parseRequestFromFile(getCommonSetting(),"jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_filterExpWherePartsEquals.yaml");
		List<CodeResponse> result = convertVe2Code(apiRequest);

		assertEquals("SELECT", result.get(0).getType());
		assertEquals("仮想表ID: 184DB91E2A434FCB9F5A94389E690333", result.get(0).getDescription());
		assertEquals("SELECT\n"+
				"    jdbc_tool_test_mainmaster.objectID,\n"+
				"    jdbc_tool_test_mainmaster.ItemCD,\n"+
				"    jdbc_tool_test_mainmaster.UnitPrice\n"+
				"FROM\n"+
				"    " + dbSchema + ".jdbc_tool_test_mainmaster jdbc_tool_test_mainmaster\n"+
				"WHERE\n"+
				"    (\n"+
				"        jdbc_tool_test_mainmaster.objectID = '1001'\n"+
				"        AND jdbc_tool_test_mainmaster.ItemCD <> '9001'\n"+
				"    )", result.get(0).getSqlScript());
		String groupByApiStr = "select(\"jdbc_tool_test_mainmaster\", \"jdbc_tool_test_mainmaster\")\n"+
				".fields(col(\"jdbc_tool_test_mainmaster.objectID\")," +
				"col(\"jdbc_tool_test_mainmaster.ItemCD\")," +
				"col(\"jdbc_tool_test_mainmaster.UnitPrice\"))\n"+
				".where(expMap($($(\"jdbc_tool_test_mainmaster.objectID = '1001'\").AND(\"jdbc_tool_test_mainmaster.ItemCD <> '9001'\")), Collections.emptyMap()))";

		assertEquals("List<GRecord> result = " + groupByApiStr + "\n.findList();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE).toString()));
		assertEquals("GRecord result = " + groupByApiStr + "\n.findRecord();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE).toString()));
		assertEquals("GRecordSet result = " + groupByApiStr + "\n.findRecordSet();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE).toString()));
	}

	@Test
	public void testExport_filterExpWherePartsCompare() {

		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_filterExpWherePartsCompare.xml");
		ApiRequest apiRequest = parseRequestFromFile(getCommonSetting(),"jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_filterExpWherePartsCompare.yaml");
		List<CodeResponse> result = convertVe2Code(apiRequest);

		assertEquals("SELECT", result.get(0).getType());
		assertEquals("仮想表ID: 6C660C8AFC13477589AD71FF629280A2", result.get(0).getDescription());
		assertEquals("SELECT\n"+
				"    jdbc_tool_test_mainmaster.objectID,\n"+
				"    jdbc_tool_test_mainmaster.ItemCD,\n"+
				"    jdbc_tool_test_mainmaster.UnitPrice,\n"+
				"    jdbc_tool_test_mainmaster.SellPrice,\n"+
				"    jdbc_tool_test_mainmaster.Flag,\n"+
				"    jdbc_tool_test_mainmaster.PriceFlag\n"+
				"FROM\n"+
				"    " + dbSchema + ".jdbc_tool_test_mainmaster jdbc_tool_test_mainmaster\n"+
				"WHERE\n"+
				"    (\n"+
				"        jdbc_tool_test_mainmaster.UnitPrice > 100\n"+
				"        AND jdbc_tool_test_mainmaster.UnitPrice < 500\n"+
				"        AND jdbc_tool_test_mainmaster.SellPrice <= 1000\n"+
				"        AND jdbc_tool_test_mainmaster.SellPrice >= 300\n"+
				"    )", result.get(0).getSqlScript());
		String groupByApiStr = "select(\"jdbc_tool_test_mainmaster\", \"jdbc_tool_test_mainmaster\")\n"+
				".fields(col(\"jdbc_tool_test_mainmaster.objectID\")," +
				"col(\"jdbc_tool_test_mainmaster.ItemCD\")," +
				"col(\"jdbc_tool_test_mainmaster.UnitPrice\")," +
				"col(\"jdbc_tool_test_mainmaster.SellPrice\")," +
				"col(\"jdbc_tool_test_mainmaster.Flag\")," +
				"col(\"jdbc_tool_test_mainmaster.PriceFlag\"))\n"+
				".where(expMap($($(\"jdbc_tool_test_mainmaster.UnitPrice > 100\").AND(\"jdbc_tool_test_mainmaster.UnitPrice < 500\").AND(\"jdbc_tool_test_mainmaster.SellPrice <= 1000\").AND(\"jdbc_tool_test_mainmaster.SellPrice >= 300\")), Collections.emptyMap()))";

		assertEquals("List<GRecord> result = " + groupByApiStr + "\n.findList();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE).toString()));
		assertEquals("GRecord result = " + groupByApiStr + "\n.findRecord();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE).toString()));
		assertEquals("GRecordSet result = " + groupByApiStr + "\n.findRecordSet();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE).toString()));
	}

	@Test
	public void testExport_filterExpWherePartsNull() {

		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_filterExpWherePartsNull.xml");
		ApiRequest apiRequest = parseRequestFromFile(getCommonSetting(),"jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_filterExpWherePartsNull.yaml");
		List<CodeResponse> result = convertVe2Code(apiRequest);

		assertEquals("SELECT", result.get(0).getType());
		assertEquals("仮想表ID: AED40FC1076E4D388AC4DA1AB616F95A", result.get(0).getDescription());
		assertEquals("SELECT\n"+
				"    jdbc_tool_test_mainmaster.objectID,\n"+
				"    jdbc_tool_test_mainmaster.ItemCD,\n"+
				"    jdbc_tool_test_mainmaster.UnitPrice\n"+
				"FROM\n"+
				"    " + dbSchema + ".jdbc_tool_test_mainmaster jdbc_tool_test_mainmaster\n"+
				"WHERE\n"+
				"    (\n"+
				"        jdbc_tool_test_mainmaster.ItemCD IS NULL\n"+
				"        AND jdbc_tool_test_mainmaster.UnitPrice IS NOT NULL\n"+
				"    )", result.get(0).getSqlScript());
		String groupByApiStr = "select(\"jdbc_tool_test_mainmaster\", \"jdbc_tool_test_mainmaster\")\n"+
				".fields(col(\"jdbc_tool_test_mainmaster.objectID\")," +
				"col(\"jdbc_tool_test_mainmaster.ItemCD\")," +
				"col(\"jdbc_tool_test_mainmaster.UnitPrice\"))\n"+
				".where(expMap($($(\"jdbc_tool_test_mainmaster.ItemCD IS NULL\").AND(\"jdbc_tool_test_mainmaster.UnitPrice IS NOT NULL\")), Collections.emptyMap()))";

		assertEquals("List<GRecord> result = " + groupByApiStr + "\n.findList();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE).toString()));
		assertEquals("GRecord result = " + groupByApiStr + "\n.findRecord();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE).toString()));
		assertEquals("GRecordSet result = " + groupByApiStr + "\n.findRecordSet();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE).toString()));
	}

	@Test
	public void testExport_filterExpWherePartsIn() {

		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_filterExpWherePartsIn.xml");
		ApiRequest apiRequest = parseRequestFromFile(getCommonSetting(),"jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_filterExpWherePartsIn.yaml");
		List<CodeResponse> result = convertVe2Code(apiRequest);

		assertEquals("SELECT", result.get(0).getType());
		assertEquals("仮想表ID: 64761AEFA524454EBA2094036685F46E", result.get(0).getDescription());
		assertEquals("SELECT\n"+
				"    jdbc_tool_test_mainmaster.objectID,\n"+
				"    jdbc_tool_test_mainmaster.ItemCD,\n"+
				"    jdbc_tool_test_mainmaster.UnitPrice\n"+
				"FROM\n"+
				"    " + dbSchema + ".jdbc_tool_test_mainmaster jdbc_tool_test_mainmaster\n"+
				"WHERE\n"+
				"    (\n"+
				"        jdbc_tool_test_mainmaster.ItemCD IN ('item010', '1001')\n"+
				"        AND jdbc_tool_test_mainmaster.UnitPrice NOT IN (10, 20, 30)\n"+
				"    )", result.get(0).getSqlScript());
		String groupByApiStr = "select(\"jdbc_tool_test_mainmaster\", \"jdbc_tool_test_mainmaster\")\n"+
				".fields(col(\"jdbc_tool_test_mainmaster.objectID\")," +
				"col(\"jdbc_tool_test_mainmaster.ItemCD\")," +
				"col(\"jdbc_tool_test_mainmaster.UnitPrice\"))\n"+
				".where(expMap($($(\"jdbc_tool_test_mainmaster.ItemCD IN ('item010', '1001')\").AND(\"jdbc_tool_test_mainmaster.UnitPrice NOT IN (10, 20, 30)\")), Collections.emptyMap()))";

		assertEquals("List<GRecord> result = " + groupByApiStr + "\n.findList();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE).toString()));
		assertEquals("GRecord result = " + groupByApiStr + "\n.findRecord();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE).toString()));
		assertEquals("GRecordSet result = " + groupByApiStr + "\n.findRecordSet();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE).toString()));
	}

	@Test
	public void testExport_wherePartsPK_inq() {

		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_wherePartsPK_inq.xml");
		ApiRequest apiRequest = parseRequestFromFile(getCommonSetting(),"jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_wherePartsPK_inq.yaml");
		List<CodeResponse> result = convertVe2Code(apiRequest);

		assertEquals("SELECT", result.get(0).getType());
		assertEquals("仮想表ID: 57E66373CB914D9F88DB638EAF6B1699", result.get(0).getDescription());
		assertEquals("SELECT\n"+
				"    jdbc_tool_test_pk_uk_mainmaster.objectID,\n"+
				"    jdbc_tool_test_pk_uk_mainmaster.ItemCD,\n"+
				"    jdbc_tool_test_pk_uk_mainmaster.ItemNA,\n"+
				"    jdbc_tool_test_pk_uk_mainmaster.UnitPrice,\n"+
				"    jdbc_tool_test_pk_uk_mainmaster.SellPrice,\n"+
				"    jdbc_tool_test_pk_uk_mainmaster.Flag,\n"+
				"    jdbc_tool_test_pk_uk_mainmaster.PriceFlag,\n"+
				"    jdbc_tool_test_pk_uk_mainmaster.ExclusiveFG,\n"+
				"    jdbc_tool_test_pk_uk_mainmaster.RegisteredDT,\n"+
				"    jdbc_tool_test_pk_uk_mainmaster.UpdatedDT\n"+
				"FROM\n"+
				"    " + dbSchema + ".jdbc_tool_test_pk_uk_mainmaster jdbc_tool_test_pk_uk_mainmaster\n"+
				"WHERE\n"+
				"    jdbc_tool_test_pk_uk_mainmaster.objectID = ?", result.get(0).getSqlScript());
		String whereParamsParts = "Map<String, Object> whereParams = new HashMap<>();\n"+
				"whereParams.put(\"jdbc_tool_test_pk_uk_mainmaster.objectID\",);\n";
		String groupByApiStr = "select(\"jdbc_tool_test_pk_uk_mainmaster\", \"jdbc_tool_test_pk_uk_mainmaster\")\n"+
				".fields(colsAll())\n"+
				".where(expMap($(\"jdbc_tool_test_pk_uk_mainmaster.objectID = :jdbc_tool_test_pk_uk_mainmaster.objectID\"), whereParams))";

		assertEquals(whereParamsParts + "List<GRecord> result = " + groupByApiStr + "\n.findList();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE).toString()));
		assertEquals(whereParamsParts + "GRecord result = " + groupByApiStr + "\n.findRecord();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE).toString()));
		assertEquals(whereParamsParts + "GRecordSet result = " + groupByApiStr + "\n.findRecordSet();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE).toString()));
	}

	@Test
	public void testExport_wherePartsUK_inq() {

		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_wherePartsUK_inq.xml");
		ApiRequest apiRequest = parseRequestFromFile(getCommonSetting(),"jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_wherePartsUK_inq.yaml");
		List<CodeResponse> result = convertVe2Code(apiRequest);

		assertEquals("SELECT", result.get(0).getType());
		assertEquals("仮想表ID: 4EE394D81C334C2DA3C37670DE3A8277", result.get(0).getDescription());
		assertEquals("SELECT\n"+
				"    jdbc_tool_test_pk_uk_mainmaster.objectID,\n"+
				"    jdbc_tool_test_pk_uk_mainmaster.ItemCD,\n"+
				"    jdbc_tool_test_pk_uk_mainmaster.ItemNA,\n"+
				"    jdbc_tool_test_pk_uk_mainmaster.UnitPrice,\n"+
				"    jdbc_tool_test_pk_uk_mainmaster.SellPrice,\n"+
				"    jdbc_tool_test_pk_uk_mainmaster.Flag,\n"+
				"    jdbc_tool_test_pk_uk_mainmaster.PriceFlag,\n"+
				"    jdbc_tool_test_pk_uk_mainmaster.ExclusiveFG,\n"+
				"    jdbc_tool_test_pk_uk_mainmaster.RegisteredDT,\n"+
				"    jdbc_tool_test_pk_uk_mainmaster.UpdatedDT\n"+
				"FROM\n"+
				"    " + dbSchema + ".jdbc_tool_test_pk_uk_mainmaster jdbc_tool_test_pk_uk_mainmaster\n"+
				"WHERE\n"+
				"    (\n"+
				"        jdbc_tool_test_pk_uk_mainmaster.ItemCD = ?\n"+
				"        AND jdbc_tool_test_pk_uk_mainmaster.ItemNA = ?\n"+
				"    )", result.get(0).getSqlScript());
		String whereParamsParts = "Map<String, Object> whereParams = new HashMap<>();\n"+
				"whereParams.put(\"jdbc_tool_test_pk_uk_mainmaster.ItemCD\",);\n"+
				"whereParams.put(\"jdbc_tool_test_pk_uk_mainmaster.ItemNA\",);\n";
		String groupByApiStr = "select(\"jdbc_tool_test_pk_uk_mainmaster\", \"jdbc_tool_test_pk_uk_mainmaster\")\n"+
				".fields(colsAll())\n"+
				".where(expMap($($(\"jdbc_tool_test_pk_uk_mainmaster.ItemCD = :jdbc_tool_test_pk_uk_mainmaster.ItemCD\").AND(\"jdbc_tool_test_pk_uk_mainmaster.ItemNA = :jdbc_tool_test_pk_uk_mainmaster.ItemNA\")), whereParams))";

		assertEquals(whereParamsParts + "List<GRecord> result = " + groupByApiStr + "\n.findList();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE).toString()));
		assertEquals(whereParamsParts + "GRecord result = " + groupByApiStr + "\n.findRecord();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE).toString()));
		assertEquals(whereParamsParts + "GRecordSet result = " + groupByApiStr + "\n.findRecordSet();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE).toString()));
	}

	@Test
	public void testExport_wherePartsPKAndUK_inq() {

		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_wherePartsPKAndUK_inq.xml");
		ApiRequest apiRequest = parseRequestFromFile(getCommonSetting(),"jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_wherePartsPKAndUK_inq.yaml");
		List<CodeResponse> result = convertVe2Code(apiRequest);

		assertEquals("SELECT", result.get(0).getType());
		assertEquals("仮想表ID: C8BE7F91E3134D9984D2922A390EC098", result.get(0).getDescription());
		assertEquals("SELECT\n"+
				"    jdbc_tool_test_pk_uk_mainmaster.objectID,\n"+
				"    jdbc_tool_test_pk_uk_mainmaster.ItemCD,\n"+
				"    jdbc_tool_test_pk_uk_mainmaster.ItemNA,\n"+
				"    jdbc_tool_test_pk_uk_mainmaster.UnitPrice,\n"+
				"    jdbc_tool_test_pk_uk_mainmaster.SellPrice,\n"+
				"    jdbc_tool_test_pk_uk_mainmaster.Flag,\n"+
				"    jdbc_tool_test_pk_uk_mainmaster.PriceFlag,\n"+
				"    jdbc_tool_test_pk_uk_mainmaster.ExclusiveFG,\n"+
				"    jdbc_tool_test_pk_uk_mainmaster.RegisteredDT,\n"+
				"    jdbc_tool_test_pk_uk_mainmaster.UpdatedDT\n"+
				"FROM\n"+
				"    " + dbSchema + ".jdbc_tool_test_pk_uk_mainmaster jdbc_tool_test_pk_uk_mainmaster\n"+
				"WHERE\n"+
				"    (\n"+
				"        jdbc_tool_test_pk_uk_mainmaster.ItemCD = ?\n"+
				"        AND jdbc_tool_test_pk_uk_mainmaster.ItemNA = ?\n"+
				"        AND jdbc_tool_test_pk_uk_mainmaster.objectID = ?\n"+
				"    )", result.get(0).getSqlScript());
		String whereParamsParts = "Map<String, Object> whereParams = new HashMap<>();\n"+
				"whereParams.put(\"jdbc_tool_test_pk_uk_mainmaster.ItemCD\",);\n"+
				"whereParams.put(\"jdbc_tool_test_pk_uk_mainmaster.ItemNA\",);\n"+
				"whereParams.put(\"jdbc_tool_test_pk_uk_mainmaster.objectID\",);\n";
		String groupByApiStr = "select(\"jdbc_tool_test_pk_uk_mainmaster\", \"jdbc_tool_test_pk_uk_mainmaster\")\n"+
				".fields(colsAll())\n"+
				".where(expMap($($(\"jdbc_tool_test_pk_uk_mainmaster.ItemCD = :jdbc_tool_test_pk_uk_mainmaster.ItemCD\").AND(\"jdbc_tool_test_pk_uk_mainmaster.ItemNA = :jdbc_tool_test_pk_uk_mainmaster.ItemNA\").AND(\"jdbc_tool_test_pk_uk_mainmaster.objectID = :jdbc_tool_test_pk_uk_mainmaster.objectID\")), whereParams))";

		assertEquals(whereParamsParts + "List<GRecord> result = " + groupByApiStr + "\n.findList();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE).toString()));
		assertEquals(whereParamsParts + "GRecord result = " + groupByApiStr + "\n.findRecord();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE).toString()));
		assertEquals(whereParamsParts + "GRecordSet result = " + groupByApiStr + "\n.findRecordSet();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE).toString()));
	}

	@Test
	public void testExport_wherePartsNotPKOrUK_inq() {

		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_wherePartsNotPKOrUK_inq.xml");
		ApiRequest apiRequest = parseRequestFromFile(getCommonSetting(),"jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_wherePartsNotPKOrUK_inq.yaml");
		List<CodeResponse> result = convertVe2Code(apiRequest);

		assertEquals("SELECT", result.get(0).getType());
		assertEquals("仮想表ID: AAD0B7190546440EAB44DE85FB559161", result.get(0).getDescription());
		assertEquals("SELECT\n"+
				"    jdbc_tool_test_pk_uk_mainmaster.objectID,\n"+
				"    jdbc_tool_test_pk_uk_mainmaster.ItemCD,\n"+
				"    jdbc_tool_test_pk_uk_mainmaster.ItemNA,\n"+
				"    jdbc_tool_test_pk_uk_mainmaster.UnitPrice,\n"+
				"    jdbc_tool_test_pk_uk_mainmaster.SellPrice,\n"+
				"    jdbc_tool_test_pk_uk_mainmaster.Flag,\n"+
				"    jdbc_tool_test_pk_uk_mainmaster.PriceFlag,\n"+
				"    jdbc_tool_test_pk_uk_mainmaster.ExclusiveFG,\n"+
				"    jdbc_tool_test_pk_uk_mainmaster.RegisteredDT,\n"+
				"    jdbc_tool_test_pk_uk_mainmaster.UpdatedDT\n"+
				"FROM\n"+
				"    " + dbSchema + ".jdbc_tool_test_pk_uk_mainmaster jdbc_tool_test_pk_uk_mainmaster\n"+
				"WHERE\n"+
				"    jdbc_tool_test_pk_uk_mainmaster.UnitPrice = ?", result.get(0).getSqlScript());
		String whereParamsParts = "Map<String, Object> whereParams = new HashMap<>();\n"+
				"whereParams.put(\"jdbc_tool_test_pk_uk_mainmaster.UnitPrice\",);\n";
		String groupByApiStr = "select(\"jdbc_tool_test_pk_uk_mainmaster\", \"jdbc_tool_test_pk_uk_mainmaster\")\n"+
				".fields(colsAll())\n"+
				".where(expMap($(\"jdbc_tool_test_pk_uk_mainmaster.UnitPrice = :jdbc_tool_test_pk_uk_mainmaster.UnitPrice\"), whereParams))";

		assertEquals(whereParamsParts + "List<GRecord> result = " + groupByApiStr + "\n.findList();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE).toString()));
		assertEquals(whereParamsParts + "GRecord result = " + groupByApiStr + "\n.findRecord();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE).toString()));
		assertEquals(whereParamsParts + "GRecordSet result = " + groupByApiStr + "\n.findRecordSet();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE).toString()));
	}

	@Test
	public void testExport_wherePartsNotCondition_inq() {

		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_wherePartsNotCondition_inq.xml");
		ApiRequest apiRequest = parseRequestFromFile(getCommonSetting(),"jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_wherePartsNotCondition_inq.yaml");
		List<CodeResponse> result = convertVe2Code(apiRequest);

		assertEquals("SELECT", result.get(0).getType());
		assertEquals("仮想表ID: 4BE51C99F70E4FE7ADB3936CBF752599", result.get(0).getDescription());
		assertEquals("SELECT\n"+
				"    jdbc_tool_test_pk_uk_mainmaster.objectID,\n"+
				"    jdbc_tool_test_pk_uk_mainmaster.ItemCD,\n"+
				"    jdbc_tool_test_pk_uk_mainmaster.ItemNA,\n"+
				"    jdbc_tool_test_pk_uk_mainmaster.UnitPrice,\n"+
				"    jdbc_tool_test_pk_uk_mainmaster.SellPrice,\n"+
				"    jdbc_tool_test_pk_uk_mainmaster.Flag,\n"+
				"    jdbc_tool_test_pk_uk_mainmaster.PriceFlag,\n"+
				"    jdbc_tool_test_pk_uk_mainmaster.ExclusiveFG,\n"+
				"    jdbc_tool_test_pk_uk_mainmaster.RegisteredDT,\n"+
				"    jdbc_tool_test_pk_uk_mainmaster.UpdatedDT\n"+
				"FROM\n"+
				"    " + dbSchema + ".jdbc_tool_test_pk_uk_mainmaster jdbc_tool_test_pk_uk_mainmaster", result.get(0).getSqlScript());
		String groupByApiStr = "select(\"jdbc_tool_test_pk_uk_mainmaster\", \"jdbc_tool_test_pk_uk_mainmaster\")\n"+
				".fields(colsAll())";

		assertEquals("List<GRecord> result = " + groupByApiStr + "\n.findList();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, null).toString()));
		assertEquals("GRecord result = " + groupByApiStr + "\n.findRecord();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, null).toString()));
		assertEquals("GRecordSet result = " + groupByApiStr + "\n.findRecordSet();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, null).toString()));
	}

	@Test
	public void testExport_wherePartsPK_reg_update() {

		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_wherePartsPK_reg_update.xml");
		ApiRequest apiRequest = parseRequestFromFile(getCommonSetting(),"jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_wherePartsPK_reg_update.yaml");
		List<CodeResponse> result = convertVe2Code(apiRequest);

		assertEquals("UPDATE", result.get(0).getType());
		assertEquals("仮想表ID: 1FEFE156727646DC9122045339BD3999", result.get(0).getDescription());
		assertEquals("UPDATE\n"+
				"    " + dbSchema + ".jdbc_tool_test_pk_uk_mainmaster jdbc_tool_test_pk_uk_mainmaster\n"+
				"SET\n"+
				"    ItemCD = ?,\n"+
				"    ItemNA = ?,\n"+
				"    UnitPrice = ?,\n"+
				"    SellPrice = ?,\n"+
				"    Flag = ?,\n"+
				"    PriceFlag = ?,\n"+
				"    ExclusiveFG = ?,\n"+
				"    RegisteredDT = ?,\n"+
				"    UpdatedDT = ?\n"+
				"WHERE\n"+
				"    jdbc_tool_test_pk_uk_mainmaster.objectID = ?", result.get(0).getSqlScript());
		String whereParamsParts = "Map<String, Object> whereParams = new HashMap<>();\n"+
				"whereParams.put(\"jdbc_tool_test_pk_uk_mainmaster.objectID\",);\n";
		String recordStr = String.format("GRecord record = createRecord();\n" +
				"record.setObject(\"ItemCD\",);\n" +
				"record.setObject(\"ItemNA\",);\n" +
				"record.setObject(\"UnitPrice\",);\n" +
				"record.setObject(\"SellPrice\",);\n" +
				"record.setObject(\"Flag\",);\n" +
				"record.setObject(\"PriceFlag\",);\n" +
				"record.setObject(\"ExclusiveFG\",);\n" +
				"record.setObject(\"RegisteredDT\",);\n" +
				"record.setObject(\"UpdatedDT\",);\n");
		String executeApiStr = "update(\"jdbc_tool_test_pk_uk_mainmaster\")\n"+
				".set(record)\n";
		String executeApiWhereStr = ".where(expMap($(\"jdbc_tool_test_pk_uk_mainmaster.objectID = :jdbc_tool_test_pk_uk_mainmaster.objectID\"), whereParams))";
		assertEquals(whereParamsParts + recordStr + "\nint count = " + executeApiStr + executeApiWhereStr + "\n.execute();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE).toString()));
		assertEquals(recordStr + "\nint count = " + executeApiStr + ".wherePK()\n"+ ".execute();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_PK).toString()));
		assertEquals(recordStr + "\nint count = " + executeApiStr + ".whereUK(\"jdbc_tool_test_uk\")\n"+ "//.whereUK(\"jdbc_tool_test_uk2\")\n"+ ".execute();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_UK).toString()));

		StringBuilder recordsStr = new StringBuilder();
		recordsStr.append("List<GRecord> recordList = new ArrayList<>();").append("\n");
		recordsStr.append(recordStr);
		recordsStr.append("recordList.add(record);").append("\n");
		String executeListApiStr = "update(\"jdbc_tool_test_pk_uk_mainmaster\")\n"+
				".set(recordList)\n";
		assertEquals(recordsStr + "\nint count = " + executeListApiStr + ".wherePK()\n"+ ".executeBatch();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_PK).toString()));
		assertEquals(recordsStr + "\nint count = " + executeListApiStr + ".whereUK(\"jdbc_tool_test_uk\")\n"+ "//.whereUK(\"jdbc_tool_test_uk2\")\n"+ ".executeBatch();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_UK).toString()));
		assertEquals(recordsStr + "\nint count = " + executeListApiStr + ".wherePK()\n"+ ".executeList();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_PK).toString()));
		assertEquals(recordsStr + "\nint count = " + executeListApiStr + ".whereUK(\"jdbc_tool_test_uk\")\n"+ "//.whereUK(\"jdbc_tool_test_uk2\")\n"+ ".executeList();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_UK).toString()));
	}

	@Test
	public void testExport_wherePartsPK_reg_delete() {

		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_wherePartsPK_reg_delete.xml");
		ApiRequest apiRequest = parseRequestFromFile(getCommonSetting(),"jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_wherePartsPK_reg_delete.yaml");
		List<CodeResponse> result = convertVe2Code(apiRequest);

		assertEquals("DELETE", result.get(0).getType());
		assertEquals("仮想表ID: 1FEFE156727646DC9122045339BD3999", result.get(0).getDescription());
		assertEquals(getDeleteSql_WherePartsPK_reg_delete(), result.get(0).getSqlScript());
		String recordStr = "Map<String, Object> whereParams = new HashMap<>();\n"+
				"whereParams.put(\"jdbc_tool_test_pk_uk_mainmaster.objectID\",);";
		String executeApiStr = "delete(\"jdbc_tool_test_pk_uk_mainmaster\")\n"+
				".where(expMap($(\"jdbc_tool_test_pk_uk_mainmaster.objectID = :jdbc_tool_test_pk_uk_mainmaster.objectID\"), whereParams))";
		assertEquals(recordStr + "\nint count = " + executeApiStr + "\n.execute();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE).toString()));

		StringBuilder recordsStr = new StringBuilder();
		recordsStr.append("GRecord whereParams = createRecord();\n"+
				"whereParams.setObject(\"objectID\",);\n");
		recordsStr.append("List<GRecord> whereParamsList = new ArrayList<>();").append("\n");
		recordsStr.append("whereParamsList.add(whereParams);").append("\n");
		String executeListApiStr = "delete(\"jdbc_tool_test_pk_uk_mainmaster\")\n"+
				".wherePK(whereParamsList)";
		assertEquals(recordsStr + "\nint count = " + executeListApiStr + "\n.executeBatch();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_PK).toString()));
		assertEquals(recordsStr + "\nint count = " + executeListApiStr + "\n.executeList();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_PK).toString()));
	}

	public String getDeleteSql_WherePartsPK_reg_delete() {
		return "DELETE FROM\n"+
				"    " + dbSchema + ".jdbc_tool_test_pk_uk_mainmaster\n"+
				"WHERE\n"+
				"    " + dbSchema + ".jdbc_tool_test_pk_uk_mainmaster.objectID = ?";
	}

	@Test
	public void testExport_wherePartsUK_reg_update() {

		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_wherePartsUK_reg_update.xml");
		ApiRequest apiRequest = parseRequestFromFile(getCommonSetting(),"jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_wherePartsUK_reg_update.yaml");
		List<CodeResponse> result = convertVe2Code(apiRequest);

		assertEquals("UPDATE", result.get(0).getType());
		assertEquals("仮想表ID: 2E5140562F7C48EE966F61BECAD75343", result.get(0).getDescription());
		assertEquals("UPDATE\n"+
				"    " + dbSchema + ".jdbc_tool_test_pk_uk_mainmaster jdbc_tool_test_pk_uk_mainmaster\n"+
				"SET\n"+
				"    ItemCD = ?,\n"+
				"    ItemNA = ?,\n"+
				"    UnitPrice = ?,\n"+
				"    SellPrice = ?,\n"+
				"    Flag = ?,\n"+
				"    PriceFlag = ?,\n"+
				"    ExclusiveFG = ?,\n"+
				"    RegisteredDT = ?,\n"+
				"    UpdatedDT = ?\n"+
				"WHERE\n"+
				"    (\n"+
				"        jdbc_tool_test_pk_uk_mainmaster.ItemCD = ?\n"+
				"        AND jdbc_tool_test_pk_uk_mainmaster.ItemNA = ?\n"+
				"    )", result.get(0).getSqlScript());
		String whereParamsParts = "Map<String, Object> whereParams = new HashMap<>();\n"+
				"whereParams.put(\"jdbc_tool_test_pk_uk_mainmaster.ItemCD\",);\n"+
				"whereParams.put(\"jdbc_tool_test_pk_uk_mainmaster.ItemNA\",);\n";
		String recordStr = "GRecord record = createRecord();\n"+
				"record.setObject(\"ItemCD\",);\n"+
				"record.setObject(\"ItemNA\",);\n"+
				"record.setObject(\"UnitPrice\",);\n"+
				"record.setObject(\"SellPrice\",);\n"+
				"record.setObject(\"Flag\",);\n"+
				"record.setObject(\"PriceFlag\",);\n"+
				"record.setObject(\"ExclusiveFG\",);\n"+
				"record.setObject(\"RegisteredDT\",);\n"+
				"record.setObject(\"UpdatedDT\",);\n";
		String executeApiStr = "update(\"jdbc_tool_test_pk_uk_mainmaster\")\n"+
				".set(record)\n";
		String executeApiWhereStr = ".where(expMap($($(\"jdbc_tool_test_pk_uk_mainmaster.ItemCD = :jdbc_tool_test_pk_uk_mainmaster.ItemCD\").AND(\"jdbc_tool_test_pk_uk_mainmaster.ItemNA = :jdbc_tool_test_pk_uk_mainmaster.ItemNA\")), whereParams))";
		assertEquals(whereParamsParts + recordStr + "\nint count = " + executeApiStr + executeApiWhereStr + "\n.execute();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE).toString()));
		assertEquals(recordStr + "\nint count = " + executeApiStr + ".wherePK()\n"+ ".execute();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_PK).toString()));
		assertEquals(recordStr + "\nint count = " + executeApiStr + ".whereUK(\"jdbc_tool_test_uk\")\n"+ "//.whereUK(\"jdbc_tool_test_uk2\")\n"+ ".execute();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_UK).toString()));

		StringBuilder recordsStr = new StringBuilder();
		recordsStr.append("List<GRecord> recordList = new ArrayList<>();").append("\n");
		recordsStr.append(recordStr);
		recordsStr.append("recordList.add(record);").append("\n");
		String executeListApiStr = "update(\"jdbc_tool_test_pk_uk_mainmaster\")\n"+
				".set(recordList)\n";
		assertEquals(recordsStr + "\nint count = " + executeListApiStr + ".wherePK()\n"+ ".executeBatch();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_PK).toString()));
		assertEquals(recordsStr + "\nint count = " + executeListApiStr + ".whereUK(\"jdbc_tool_test_uk\")\n"+ "//.whereUK(\"jdbc_tool_test_uk2\")\n"+ ".executeBatch();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_UK).toString()));
		assertEquals(recordsStr + "\nint count = " + executeListApiStr + ".wherePK()\n"+ ".executeList();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_PK).toString()));
		assertEquals(recordsStr + "\nint count = " + executeListApiStr + ".whereUK(\"jdbc_tool_test_uk\")\n"+ "//.whereUK(\"jdbc_tool_test_uk2\")\n"+ ".executeList();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_UK).toString()));
	}

	@Test
	public void testExport_wherePartsUK_reg_delete() {

		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_wherePartsUK_reg_delete.xml");
		ApiRequest apiRequest = parseRequestFromFile(getCommonSetting(),"jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_wherePartsUK_reg_delete.yaml");
		List<CodeResponse> result = convertVe2Code(apiRequest);

		assertEquals("DELETE", result.get(0).getType());
		assertEquals("仮想表ID: 2E5140562F7C48EE966F61BECAD75343", result.get(0).getDescription());
		assertEquals(getDeleteSql_wherePartsUK_reg_delete(), result.get(0).getSqlScript());
		String recordStr = String.format("GRecord whereParams = createRecord();\n" +
				"whereParams.setObject(\"ItemCD\",);\n" +
				"whereParams.setObject(\"ItemNA\",);\n" +
				"//ユーニックキー「jdbc_tool_test_uk2」用のカラム\n" +
				"/*whereParams.setObject(\"ItemCD\",);\n" +
				"whereParams.setObject(\"UnitPrice\",);\n" +
				"*/\n");
		String executeApiStr = String.format("delete(\"jdbc_tool_test_pk_uk_mainmaster\")\n" +
				".whereUK(\"jdbc_tool_test_uk\", whereParams)\n" +
				"//.whereUK(\"jdbc_tool_test_uk2\", whereParams)");
		assertEquals(recordStr + "\nint count = " + executeApiStr + "\n.execute();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_UK).toString()));

		StringBuilder recordsStr = new StringBuilder();
		recordsStr.append(recordStr);
		recordsStr.append("List<GRecord> whereParamsList = new ArrayList<>();").append("\n");
		recordsStr.append("whereParamsList.add(whereParams);").append("\n");
		String executeListApiStr = String.format("delete(\"jdbc_tool_test_pk_uk_mainmaster\")\n" +
				".whereUK(\"jdbc_tool_test_uk\", whereParamsList)\n" +
				"//.whereUK(\"jdbc_tool_test_uk2\", whereParamsList)");
		assertEquals(recordsStr + "\nint count = " + executeListApiStr + "\n.executeBatch();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_UK).toString()));
		assertEquals(recordsStr + "\nint count = " + executeListApiStr + "\n.executeList();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_UK).toString()));
	}

	public String getDeleteSql_wherePartsUK_reg_delete() {
		return "DELETE FROM\n"+
				"    " + dbSchema + ".jdbc_tool_test_pk_uk_mainmaster\n"+
				"WHERE\n"+
				"    (\n"+
				"        " + dbSchema + ".jdbc_tool_test_pk_uk_mainmaster.ItemCD = ?\n"+
				"        AND " + dbSchema + ".jdbc_tool_test_pk_uk_mainmaster.ItemNA = ?\n"+
				"    )";
	}

	@Test
	public void testExport_wherePartsPKAndUK_reg_update() {

		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_wherePartsPKAndUK_reg_update.xml");
		ApiRequest apiRequest = parseRequestFromFile(getCommonSetting(),"jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_wherePartsPKAndUK_reg_update.yaml");
		List<CodeResponse> result = convertVe2Code(apiRequest);

		assertEquals("UPDATE", result.get(0).getType());
		assertEquals("仮想表ID: 193814253CB6440E83B1088325C8EFCD", result.get(0).getDescription());
		assertEquals("UPDATE\n"+
				"    " + dbSchema + ".jdbc_tool_test_pk_uk_mainmaster jdbc_tool_test_pk_uk_mainmaster\n"+
				"SET\n"+
				"    ItemCD = ?,\n"+
				"    ItemNA = ?,\n"+
				"    UnitPrice = ?,\n"+
				"    SellPrice = ?,\n"+
				"    Flag = ?,\n"+
				"    PriceFlag = ?,\n"+
				"    ExclusiveFG = ?,\n"+
				"    RegisteredDT = ?,\n"+
				"    UpdatedDT = ?\n"+
				"WHERE\n"+
				"    (\n"+
				"        jdbc_tool_test_pk_uk_mainmaster.ItemCD = ?\n"+
				"        AND jdbc_tool_test_pk_uk_mainmaster.ItemNA = ?\n"+
				"        AND jdbc_tool_test_pk_uk_mainmaster.objectID = ?\n"+
				"    )", result.get(0).getSqlScript());
		String whereParamsParts = "Map<String, Object> whereParams = new HashMap<>();\n"+
				"whereParams.put(\"jdbc_tool_test_pk_uk_mainmaster.ItemCD\",);\n"+
				"whereParams.put(\"jdbc_tool_test_pk_uk_mainmaster.ItemNA\",);\n"+
				"whereParams.put(\"jdbc_tool_test_pk_uk_mainmaster.objectID\",);\n";
		String recordStr = "GRecord record = createRecord();\n"+
				"record.setObject(\"ItemCD\",);\n"+
				"record.setObject(\"ItemNA\",);\n"+
				"record.setObject(\"UnitPrice\",);\n"+
				"record.setObject(\"SellPrice\",);\n"+
				"record.setObject(\"Flag\",);\n"+
				"record.setObject(\"PriceFlag\",);\n"+
				"record.setObject(\"ExclusiveFG\",);\n"+
				"record.setObject(\"RegisteredDT\",);\n"+
				"record.setObject(\"UpdatedDT\",);\n";
		String executeApiStr = "update(\"jdbc_tool_test_pk_uk_mainmaster\")\n"+
				".set(record)\n";
		String executeApiWhereStr = ".where(expMap($($(\"jdbc_tool_test_pk_uk_mainmaster.ItemCD = :jdbc_tool_test_pk_uk_mainmaster.ItemCD\").AND(\"jdbc_tool_test_pk_uk_mainmaster.ItemNA = :jdbc_tool_test_pk_uk_mainmaster.ItemNA\").AND(\"jdbc_tool_test_pk_uk_mainmaster.objectID = :jdbc_tool_test_pk_uk_mainmaster.objectID\")), whereParams))";
		assertEquals(whereParamsParts + recordStr + "\nint count = " + executeApiStr + executeApiWhereStr + "\n.execute();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE).toString()));
		assertEquals(recordStr + "\nint count = " + executeApiStr + ".wherePK()\n"+ ".execute();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_PK).toString()));
		assertEquals(recordStr + "\nint count = " + executeApiStr + ".whereUK(\"jdbc_tool_test_uk\")\n"+ "//.whereUK(\"jdbc_tool_test_uk2\")\n"+ ".execute();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_UK).toString()));

		StringBuilder recordsStr = new StringBuilder();
		recordsStr.append("List<GRecord> recordList = new ArrayList<>();").append("\n");
		recordsStr.append(recordStr);
		recordsStr.append("recordList.add(record);").append("\n");
		String executeListApiStr = "update(\"jdbc_tool_test_pk_uk_mainmaster\")\n"+
				".set(recordList)\n";
		assertEquals(recordsStr + "\nint count = " + executeListApiStr + ".wherePK()"  + "\n.executeBatch();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_PK).toString()));
		assertEquals(recordsStr + "\nint count = " + executeListApiStr + ".whereUK(\"jdbc_tool_test_uk\")\n"+ "//.whereUK(\"jdbc_tool_test_uk2\")"  + "\n.executeBatch();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_UK).toString()));
		assertEquals(recordsStr + "\nint count = " + executeListApiStr + ".wherePK()"  + "\n.executeList();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_PK).toString()));
		assertEquals(recordsStr + "\nint count = " + executeListApiStr + ".whereUK(\"jdbc_tool_test_uk\")\n"+ "//.whereUK(\"jdbc_tool_test_uk2\")"  + "\n.executeList();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_UK).toString()));
	}

	@Test
	public void testExport_wherePartsPKAndUK_reg_delete() {

		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_wherePartsPKAndUK_reg_delete.xml");
		ApiRequest apiRequest = parseRequestFromFile(getCommonSetting(),"jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_wherePartsPKAndUK_reg_delete.yaml");
		List<CodeResponse> result = convertVe2Code(apiRequest);

		assertEquals("DELETE", result.get(0).getType());
		assertEquals("仮想表ID: 193814253CB6440E83B1088325C8EFCD", result.get(0).getDescription());
		assertEquals(getDeleteSql_wherePartsPKAndUK_reg_delete(), result.get(0).getSqlScript());
		String whereParamsParts = "Map<String, Object> whereParams = new HashMap<>();\n"+
				"whereParams.put(\"jdbc_tool_test_pk_uk_mainmaster.ItemCD\",);\n"+
				"whereParams.put(\"jdbc_tool_test_pk_uk_mainmaster.ItemNA\",);\n"+
				"whereParams.put(\"jdbc_tool_test_pk_uk_mainmaster.objectID\",);";

		String executeApiStr = "delete(\"jdbc_tool_test_pk_uk_mainmaster\")\n"+
				".where(expMap($($(\"jdbc_tool_test_pk_uk_mainmaster.ItemCD = :jdbc_tool_test_pk_uk_mainmaster.ItemCD\").AND(\"jdbc_tool_test_pk_uk_mainmaster.ItemNA = :jdbc_tool_test_pk_uk_mainmaster.ItemNA\").AND(\"jdbc_tool_test_pk_uk_mainmaster.objectID = :jdbc_tool_test_pk_uk_mainmaster.objectID\")), whereParams))";
		assertEquals(whereParamsParts + "\nint count = " + executeApiStr + "\n.execute();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE).toString()));
	}

	public String getDeleteSql_wherePartsPKAndUK_reg_delete() {
		return "DELETE FROM\n"+
				"    " + dbSchema + ".jdbc_tool_test_pk_uk_mainmaster\n"+
				"WHERE\n"+
				"    (\n"+
				"        " + dbSchema + ".jdbc_tool_test_pk_uk_mainmaster.ItemCD = ?\n"+
				"        AND " + dbSchema + ".jdbc_tool_test_pk_uk_mainmaster.ItemNA = ?\n"+
				"        AND " + dbSchema + ".jdbc_tool_test_pk_uk_mainmaster.objectID = ?\n"+
				"    )";
	}

	@Test
	public void testExport_wherePartsNotPKOrUK_reg_update() {

		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_wherePartsNotPKOrUK_reg_update.xml");
		ApiRequest apiRequest = parseRequestFromFile(getCommonSetting(),"jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_wherePartsNotPKOrUK_reg_update.yaml");
		List<CodeResponse> result = convertVe2Code(apiRequest);

		assertEquals("UPDATE", result.get(0).getType());
		assertEquals("仮想表ID: 4E800A8CAD8A4071A2A60C0D7E555ECB", result.get(0).getDescription());
		assertEquals("UPDATE\n"+
				"    " + dbSchema + ".jdbc_tool_test_pk_uk_mainmaster jdbc_tool_test_pk_uk_mainmaster\n"+
				"SET\n"+
				"    ItemCD = ?,\n"+
				"    ItemNA = ?,\n"+
				"    UnitPrice = ?,\n"+
				"    SellPrice = ?,\n"+
				"    Flag = ?,\n"+
				"    PriceFlag = ?,\n"+
				"    ExclusiveFG = ?,\n"+
				"    RegisteredDT = ?,\n"+
				"    UpdatedDT = ?\n"+
				"WHERE\n"+
				"    jdbc_tool_test_pk_uk_mainmaster.UnitPrice = ?", result.get(0).getSqlScript());
		String whereParamsParts = "Map<String, Object> whereParams = new HashMap<>();\n"+
				"whereParams.put(\"jdbc_tool_test_pk_uk_mainmaster.UnitPrice\",);\n";
		String recordStr = "GRecord record = createRecord();\n"+
				"record.setObject(\"ItemCD\",);\n"+
				"record.setObject(\"ItemNA\",);\n"+
				"record.setObject(\"UnitPrice\",);\n"+
				"record.setObject(\"SellPrice\",);\n"+
				"record.setObject(\"Flag\",);\n"+
				"record.setObject(\"PriceFlag\",);\n"+
				"record.setObject(\"ExclusiveFG\",);\n"+
				"record.setObject(\"RegisteredDT\",);\n"+
				"record.setObject(\"UpdatedDT\",);\n";
		String executeApiStr = "update(\"jdbc_tool_test_pk_uk_mainmaster\")\n"+
				".set(record)\n";
		String executeApiWhereStr = ".where(expMap($(\"jdbc_tool_test_pk_uk_mainmaster.UnitPrice = :jdbc_tool_test_pk_uk_mainmaster.UnitPrice\"), whereParams))";
		assertEquals(whereParamsParts + recordStr + "\nint count = " + executeApiStr + executeApiWhereStr + "\n.execute();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE).toString()));
		assertEquals(recordStr + "\nint count = " + executeApiStr + ".wherePK()\n"+ ".execute();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_PK).toString()));
		assertEquals(recordStr + "\nint count = " + executeApiStr + ".whereUK(\"jdbc_tool_test_uk\")\n"+ "//.whereUK(\"jdbc_tool_test_uk2\")\n"+ ".execute();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_UK).toString()));

		StringBuilder recordsStr = new StringBuilder();
		recordsStr.append("List<GRecord> recordList = new ArrayList<>();").append("\n");
		recordsStr.append(recordStr);
		recordsStr.append("recordList.add(record);").append("\n");
		String executeListApiStr = "update(\"jdbc_tool_test_pk_uk_mainmaster\")\n"+
				".set(recordList)\n";
		assertEquals(recordsStr + "\nint count = " + executeListApiStr + ".wherePK()\n"+ ".executeBatch();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_PK).toString()));
		assertEquals(recordsStr + "\nint count = " + executeListApiStr + ".whereUK(\"jdbc_tool_test_uk\")\n"+ "//.whereUK(\"jdbc_tool_test_uk2\")\n"+ ".executeBatch();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_UK).toString()));
		assertEquals(recordsStr + "\nint count = " + executeListApiStr + ".wherePK()\n"+ ".executeList();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_PK).toString()));
		assertEquals(recordsStr + "\nint count = " + executeListApiStr + ".whereUK(\"jdbc_tool_test_uk\")\n"+ "//.whereUK(\"jdbc_tool_test_uk2\")\n"+ ".executeList();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_UK).toString()));
	}

	@Test
	public void testExport_wherePartsNotPKOrUK_reg_delete() {

		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_wherePartsNotPKOrUK_reg_delete.xml");
		ApiRequest apiRequest = parseRequestFromFile(getCommonSetting(),"jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_wherePartsNotPKOrUK_reg_delete.yaml");
		List<CodeResponse> result = convertVe2Code(apiRequest);

		assertEquals("DELETE", result.get(0).getType());
		assertEquals("仮想表ID: 4E800A8CAD8A4071A2A60C0D7E555ECB", result.get(0).getDescription());
		assertEquals(getDeleteSql_wherePartsNotPKOrUK_reg_delete(), result.get(0).getSqlScript());
		String whereParamsParts = "Map<String, Object> whereParams = new HashMap<>();\n"+
				"whereParams.put(\"jdbc_tool_test_pk_uk_mainmaster.UnitPrice\",);";
		String executeApiStr = "delete(\"jdbc_tool_test_pk_uk_mainmaster\")\n"+
				".where(expMap($(\"jdbc_tool_test_pk_uk_mainmaster.UnitPrice = :jdbc_tool_test_pk_uk_mainmaster.UnitPrice\"), whereParams))";
		assertEquals(whereParamsParts + "\nint count = " + executeApiStr + "\n.execute();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE).toString()));
	}

	public String getDeleteSql_wherePartsNotPKOrUK_reg_delete() {
		return "DELETE FROM\n"+
				"    " + dbSchema + ".jdbc_tool_test_pk_uk_mainmaster\n"+
				"WHERE\n"+
				"    " + dbSchema + ".jdbc_tool_test_pk_uk_mainmaster.UnitPrice = ?";
	}

	@Test
	public void testExport_wherePartsNotCondition_reg_update() {

		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_wherePartsNotCondition_reg_update.xml");
		ApiRequest apiRequest = parseRequestFromFile(getCommonSetting(),"jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_wherePartsNotCondition_reg_update.yaml");
		List<CodeResponse> result = convertVe2Code(apiRequest);

		assertEquals("UPDATE", result.get(0).getType());
		assertEquals("仮想表ID: 8C75277254C14BC0B310CF2182BFF928", result.get(0).getDescription());
		assertEquals("UPDATE\n"+
				"    " + dbSchema + ".jdbc_tool_test_pk_uk_mainmaster jdbc_tool_test_pk_uk_mainmaster\n"+
				"SET\n"+
				"    ItemCD = ?,\n"+
				"    ItemNA = ?,\n"+
				"    UnitPrice = ?,\n"+
				"    SellPrice = ?,\n"+
				"    Flag = ?,\n"+
				"    PriceFlag = ?,\n"+
				"    ExclusiveFG = ?,\n"+
				"    RegisteredDT = ?,\n"+
				"    UpdatedDT = ?", result.get(0).getSqlScript());
		String recordStr = "GRecord record = createRecord();\n"+
				"record.setObject(\"ItemCD\",);\n"+
				"record.setObject(\"ItemNA\",);\n"+
				"record.setObject(\"UnitPrice\",);\n"+
				"record.setObject(\"SellPrice\",);\n"+
				"record.setObject(\"Flag\",);\n"+
				"record.setObject(\"PriceFlag\",);\n"+
				"record.setObject(\"ExclusiveFG\",);\n"+
				"record.setObject(\"RegisteredDT\",);\n"+
				"record.setObject(\"UpdatedDT\",);\n";
		String executeApiStr = "update(\"jdbc_tool_test_pk_uk_mainmaster\")\n"+
				".set(record)";
		assertEquals(recordStr + "\nint count = " + executeApiStr + "\n.execute();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, null).toString()));
		assertNull(result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE).toString()));
		assertNull(result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_PK).toString()));
		assertNull(result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_UK).toString()));
		assertNull(result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, null).toString()));
		assertNull(result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE).toString()));
		assertNull(result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_PK).toString()));
		assertNull(result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_UK).toString()));
		assertNull(result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, null).toString()));
		assertNull(result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE).toString()));
		assertNull(result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_PK).toString()));
		assertNull(result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_UK).toString()));
	}

	@Test
	public void testExport_wherePartsNotCondition_reg_delete() {

		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_wherePartsNotCondition_reg_delete.xml");
		ApiRequest apiRequest = parseRequestFromFile(getCommonSetting(),"jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_wherePartsNotCondition_reg_delete.yaml");
		List<CodeResponse> result = convertVe2Code(apiRequest);

		assertEquals("DELETE", result.get(0).getType());
		assertEquals("仮想表ID: 8C75277254C14BC0B310CF2182BFF928", result.get(0).getDescription());
		assertEquals(getDeleteSql_wherePartsNotCondition_reg_delete(), result.get(0).getSqlScript());
		String executeApiStr = "delete(\"jdbc_tool_test_pk_uk_mainmaster\")";
		assertEquals("int count = " + executeApiStr + "\n.execute();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, null).toString()));
	}

	public String getDeleteSql_wherePartsNotCondition_reg_delete() {
		return "DELETE FROM\n"+
				"    " + dbSchema + ".jdbc_tool_test_pk_uk_mainmaster";
	}

	@Test
	public void testExport_searchExpWherePartsLikeF_reg_update() {

		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_searchExpWherePartsLikeF_reg_update.xml");
		ApiRequest apiRequest = parseRequestFromFile(getCommonSetting(),"jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_searchExpWherePartsLikeF_reg_update.yaml");
		List<CodeResponse> result = convertVe2Code(apiRequest);

		assertEquals("UPDATE", result.get(0).getType());
		assertEquals("仮想表ID: FBD28623C6794B44ACED6A253DA526E5", result.get(0).getDescription());
		assertEquals("UPDATE\n"+
				"    " + dbSchema + ".jdbc_tool_test_mainmaster jdbc_tool_test_mainmaster\n"+
				"SET\n"+
				"    ItemCD = ?,\n"+
				"    UnitPrice = ?\n"+
				"WHERE\n"+
				"    jdbc_tool_test_mainmaster.ItemCD LIKE ? ESCAPE '/'", result.get(0).getSqlScript());
		String whereParamsParts = "Map<String, Object> whereParams = new HashMap<>();\n"+
				"whereParams.put(\"jdbc_tool_test_mainmaster.ItemCD\",);\n";
		String recordStr = "GRecord record = createRecord();\n"+
				"record.setObject(\"ItemCD\",);\n"+
				"record.setObject(\"UnitPrice\",);\n";
		String executeApiStr = "update(\"jdbc_tool_test_mainmaster\")\n"+
				".set(record)\n";
		String executeApiWhereStr = ".where(expMap($(\"jdbc_tool_test_mainmaster.ItemCD LIKE $CONCAT(:jdbc_tool_test_mainmaster.ItemCD, '%') ESCAPE '/'\"), whereParams))";
		assertEquals(whereParamsParts + recordStr + "\nint count = " + executeApiStr + executeApiWhereStr + "\n.execute();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE).toString()));
		assertEquals(recordStr + "\nint count = " + executeApiStr + ".wherePK()\n"+ ".execute();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_PK).toString()));
		assertEquals(recordStr + "\nint count = " + executeApiStr + ".whereUK(\"MainMasterUniqueKey\")\n"+ ".execute();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_UK).toString()));

		StringBuilder recordsStr = new StringBuilder();
		recordsStr.append("List<GRecord> recordList = new ArrayList<>();").append("\n");
		recordsStr.append(recordStr);
		recordsStr.append("recordList.add(record);").append("\n");
		String executeListApiStr = "update(\"jdbc_tool_test_mainmaster\")\n"+
				".set(recordList)\n";
		assertEquals(recordsStr + "\nint count = " + executeListApiStr + ".wherePK()\n"+ ".executeBatch();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_PK).toString()));
		assertEquals(recordsStr + "\nint count = " + executeListApiStr + ".whereUK(\"MainMasterUniqueKey\")\n"+ ".executeBatch();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_UK).toString()));
		assertEquals(recordsStr + "\nint count = " + executeListApiStr + ".wherePK()\n"+ ".executeList();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_PK).toString()));
		assertEquals(recordsStr + "\nint count = " + executeListApiStr + ".whereUK(\"MainMasterUniqueKey\")\n"+ ".executeList();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_UK).toString()));
	}

	@Test
	public void testExport_searchExpWherePartsLikeF_reg_delete() {

		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_searchExpWherePartsLikeF_reg_delete.xml");
		ApiRequest apiRequest = parseRequestFromFile(getCommonSetting(),"jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_searchExpWherePartsLikeF_reg_delete.yaml");
		List<CodeResponse> result = convertVe2Code(apiRequest);

		assertEquals("DELETE", result.get(0).getType());
		assertEquals("仮想表ID: FBD28623C6794B44ACED6A253DA526E5", result.get(0).getDescription());
		assertEquals(getDeleteSql_searchExpWherePartsLikeF_reg_delete(), result.get(0).getSqlScript());
		String whereParamsParts = "Map<String, Object> whereParams = new HashMap<>();\n"+
				"whereParams.put(\"jdbc_tool_test_mainmaster.ItemCD\",);";
		String executeApiStr = "delete(\"jdbc_tool_test_mainmaster\")\n"+
				".where(expMap($(\"jdbc_tool_test_mainmaster.ItemCD LIKE $CONCAT(:jdbc_tool_test_mainmaster.ItemCD, '%') ESCAPE '/'\"), whereParams))";
		assertEquals(whereParamsParts + "\nint count = " + executeApiStr + "\n.execute();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE).toString()));
	}

	public String getDeleteSql_searchExpWherePartsLikeF_reg_delete() {
		return "DELETE FROM\n"+
				"    " + dbSchema + ".jdbc_tool_test_mainmaster\n"+
				"WHERE\n"+
				"    " + dbSchema + ".jdbc_tool_test_mainmaster.ItemCD LIKE ? ESCAPE '/'";
	}

	@Test
	public void testExport_searchExpWherePartsLikeFB_reg_update() {

		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_searchExpWherePartsLikeFB_reg_update.xml");
		ApiRequest apiRequest = parseRequestFromFile(getCommonSetting(),"jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_searchExpWherePartsLikeFB_reg_update.yaml");
		List<CodeResponse> result = convertVe2Code(apiRequest);

		assertEquals("UPDATE", result.get(0).getType());
		assertEquals("仮想表ID: D65A07E6C02546B58A1BD511ED22B508", result.get(0).getDescription());
		assertEquals("UPDATE\n"+
				"    " + dbSchema + ".jdbc_tool_test_unitmaster jdbc_tool_test_unitmaster\n"+
				"SET\n"+
				"    Code = ?,\n"+
				"    Name = ?\n"+
				"WHERE\n"+
				"    jdbc_tool_test_unitmaster.Name LIKE ? ESCAPE '/'", result.get(0).getSqlScript());
		String whereParamsParts = "Map<String, Object> whereParams = new HashMap<>();\n"+
				"whereParams.put(\"jdbc_tool_test_unitmaster.Name\",);\n";
		String recordStr = "GRecord record = createRecord();\n"+
				"record.setObject(\"Code\",);\n"+
				"record.setObject(\"Name\",);\n";
		String executeApiStr = "update(\"jdbc_tool_test_unitmaster\")\n"+
				".set(record)\n";
		String executeApiWhereStr = ".where(expMap($(\"jdbc_tool_test_unitmaster.Name LIKE $CONCAT('%', $CONCAT(:jdbc_tool_test_unitmaster.Name, '%')) ESCAPE '/'\"), whereParams))";
		assertEquals(whereParamsParts + recordStr + "\nint count = " + executeApiStr + executeApiWhereStr + "\n.execute();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE).toString()));
		assertEquals(recordStr + "\nint count = " + executeApiStr + ".wherePK()\n"+ ".execute();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_PK).toString()));
		assertEquals(recordStr + "\nint count = " + executeApiStr + ".whereUK(\"UnitMasterUK\")\n"+ ".execute();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_UK).toString()));

		StringBuilder recordsStr = new StringBuilder();
		recordsStr.append("List<GRecord> recordList = new ArrayList<>();").append("\n");
		recordsStr.append(recordStr);
		recordsStr.append("recordList.add(record);").append("\n");
		String executeListApiStr = "update(\"jdbc_tool_test_unitmaster\")\n"+
				".set(recordList)\n";
		assertEquals(recordsStr + "\nint count = " + executeListApiStr + ".wherePK()\n"+ ".executeBatch();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_PK).toString()));
		assertEquals(recordsStr + "\nint count = " + executeListApiStr + ".whereUK(\"UnitMasterUK\")\n"+ ".executeBatch();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_UK).toString()));
		assertEquals(recordsStr + "\nint count = " + executeListApiStr + ".wherePK()\n"+ ".executeList();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_PK).toString()));
		assertEquals(recordsStr + "\nint count = " + executeListApiStr + ".whereUK(\"UnitMasterUK\")\n"+ ".executeList();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_UK).toString()));
	}

	@Test
	public void testExport_searchExpWherePartsLikeFB_reg_delete() {

		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_searchExpWherePartsLikeFB_reg_delete.xml");
		ApiRequest apiRequest = parseRequestFromFile(getCommonSetting(),"jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_searchExpWherePartsLikeFB_reg_delete.yaml");
		List<CodeResponse> result = convertVe2Code(apiRequest);

		assertEquals("DELETE", result.get(0).getType());
		assertEquals("仮想表ID: D65A07E6C02546B58A1BD511ED22B508", result.get(0).getDescription());
		assertEquals(getDeleteSql_searchExpWherePartsLikeFB_reg_delete(), result.get(0).getSqlScript());
		String whereParamsParts = "Map<String, Object> whereParams = new HashMap<>();\n"+
				"whereParams.put(\"jdbc_tool_test_unitmaster.Name\",);";
		String executeApiStr = "delete(\"jdbc_tool_test_unitmaster\")\n"+
				".where(expMap($(\"jdbc_tool_test_unitmaster.Name LIKE $CONCAT('%', $CONCAT(:jdbc_tool_test_unitmaster.Name, '%')) ESCAPE '/'\"), whereParams))";
		assertEquals(whereParamsParts + "\nint count = " + executeApiStr + "\n.execute();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE).toString()));
	}

	public String getDeleteSql_searchExpWherePartsLikeFB_reg_delete() {
		return "DELETE FROM\n"+
				"    " + dbSchema + ".jdbc_tool_test_unitmaster\n"+
				"WHERE\n"+
				"    " + dbSchema + ".jdbc_tool_test_unitmaster.Name LIKE ? ESCAPE '/'";
	}

	@Test
	public void testExport_searchExpWherePartsLikeB_reg_update() {

		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_searchExpWherePartsLikeB_reg_update.xml");
		ApiRequest apiRequest = parseRequestFromFile(getCommonSetting(),"jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_searchExpWherePartsLikeB_reg_update.yaml");
		List<CodeResponse> result = convertVe2Code(apiRequest);

		assertEquals("UPDATE", result.get(0).getType());
		assertEquals("仮想表ID: 730835BE19ED400F9FAFEF9DCF864057", result.get(0).getDescription());
		assertEquals("UPDATE\n"+
				"    " + dbSchema + ".jdbc_tool_test_ordermaster jdbc_tool_test_ordermaster\n"+
				"SET\n"+
				"    SlipNO = ?,\n"+
				"    CorporateNA = ?\n"+
				"WHERE\n"+
				"    jdbc_tool_test_ordermaster.CorporateNA LIKE ? ESCAPE '/'", result.get(0).getSqlScript());
		String whereParamsParts = "Map<String, Object> whereParams = new HashMap<>();\n"+
				"whereParams.put(\"jdbc_tool_test_ordermaster.CorporateNA\",);\n";
		String recordStr = "GRecord record = createRecord();\n"+
				"record.setObject(\"SlipNO\",);\n"+
				"record.setObject(\"CorporateNA\",);\n";
		String executeApiStr = "update(\"jdbc_tool_test_ordermaster\")\n"+
				".set(record)\n";
		String executeApiWhereStr = ".where(expMap($(\"jdbc_tool_test_ordermaster.CorporateNA LIKE $CONCAT('%', :jdbc_tool_test_ordermaster.CorporateNA) ESCAPE '/'\"), whereParams))";
		assertEquals(whereParamsParts + recordStr + "\nint count = " + executeApiStr + executeApiWhereStr + "\n.execute();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE).toString()));
		assertEquals(recordStr + "\nint count = " + executeApiStr + ".wherePK()\n"+ ".execute();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_PK).toString()));
		assertEquals(recordStr + "\nint count = " + executeApiStr + ".whereUK(\"OrderMasterUK\")\n"+ ".execute();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_UK).toString()));

		StringBuilder recordsStr = new StringBuilder();
		recordsStr.append("List<GRecord> recordList = new ArrayList<>();").append("\n");
		recordsStr.append(recordStr);
		recordsStr.append("recordList.add(record);").append("\n");
		String executeListApiStr = "update(\"jdbc_tool_test_ordermaster\")\n"+
				".set(recordList)\n";
		assertEquals(recordsStr + "\nint count = " + executeListApiStr + ".wherePK()\n"+ ".executeBatch();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_PK).toString()));
		assertEquals(recordsStr + "\nint count = " + executeListApiStr + ".whereUK(\"OrderMasterUK\")\n"+ ".executeBatch();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_UK).toString()));
		assertEquals(recordsStr + "\nint count = " + executeListApiStr + ".wherePK()\n"+ ".executeList();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_PK).toString()));
		assertEquals(recordsStr + "\nint count = " + executeListApiStr + ".whereUK(\"OrderMasterUK\")\n"+ ".executeList();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_UK).toString()));
	}

	@Test
	public void testExport_searchExpWherePartsLikeB_reg_delete() {

		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_searchExpWherePartsLikeB_reg_delete.xml");
		ApiRequest apiRequest = parseRequestFromFile(getCommonSetting(),"jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_searchExpWherePartsLikeB_reg_delete.yaml");
		List<CodeResponse> result = convertVe2Code(apiRequest);

		assertEquals("DELETE", result.get(0).getType());
		assertEquals("仮想表ID: 730835BE19ED400F9FAFEF9DCF864057", result.get(0).getDescription());
		assertEquals(getDeleteSql_searchExpWherePartsLikeB_reg_delete(), result.get(0).getSqlScript());
		String whereParamsParts = "Map<String, Object> whereParams = new HashMap<>();\n"+
				"whereParams.put(\"jdbc_tool_test_ordermaster.CorporateNA\",);";
		String executeApiStr = "delete(\"jdbc_tool_test_ordermaster\")\n"+
				".where(expMap($(\"jdbc_tool_test_ordermaster.CorporateNA LIKE $CONCAT('%', :jdbc_tool_test_ordermaster.CorporateNA) ESCAPE '/'\"), whereParams))";
		assertEquals(whereParamsParts + "\nint count = " + executeApiStr + "\n.execute();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE).toString()));
	}

	public String getDeleteSql_searchExpWherePartsLikeB_reg_delete() {
		return "DELETE FROM\n"+
				"    " + dbSchema + ".jdbc_tool_test_ordermaster\n"+
				"WHERE\n"+
				"    " + dbSchema + ".jdbc_tool_test_ordermaster.CorporateNA LIKE ? ESCAPE '/'";
	}

	@Test
	public void testExport_searchExpWherePartsEquals_reg_update() {

		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_searchExpWherePartsEquals_reg_update.xml");
		ApiRequest apiRequest = parseRequestFromFile(getCommonSetting(),"jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_searchExpWherePartsEquals_reg_update.yaml");
		List<CodeResponse> result = convertVe2Code(apiRequest);

		assertEquals("UPDATE", result.get(0).getType());
		assertEquals("仮想表ID: CC71DD95CF7244DFB80A009930DB3F95", result.get(0).getDescription());
		assertEquals("UPDATE\n"+
				"    " + dbSchema + ".jdbc_tool_test_mainmaster jdbc_tool_test_mainmaster\n"+
				"SET\n"+
				"    ItemCD = ?,\n"+
				"    UnitPrice = ?\n"+
				"WHERE\n"+
				"    (\n"+
				"        jdbc_tool_test_mainmaster.ItemCD = ?\n"+
				"        AND jdbc_tool_test_mainmaster.UnitPrice <> ?\n"+
				"    )", result.get(0).getSqlScript());
		String whereParamsParts = "Map<String, Object> whereParams = new HashMap<>();\n"+
				"whereParams.put(\"jdbc_tool_test_mainmaster.UnitPrice\",);\n"+
				"whereParams.put(\"jdbc_tool_test_mainmaster.ItemCD\",);\n";
		String recordStr = "GRecord record = createRecord();\n"+
				"record.setObject(\"ItemCD\",);\n"+
				"record.setObject(\"UnitPrice\",);\n";
		String executeApiStr = "update(\"jdbc_tool_test_mainmaster\")\n"+
				".set(record)\n";
		String executeApiWhereStr = ".where(expMap($($(\"jdbc_tool_test_mainmaster.ItemCD = $TRIM([:jdbc_tool_test_mainmaster.ItemCD])\").AND(\"jdbc_tool_test_mainmaster.UnitPrice <> $TRIM([:jdbc_tool_test_mainmaster.UnitPrice])\")), whereParams))";
		assertEquals(whereParamsParts + recordStr + "\nint count = " + executeApiStr + executeApiWhereStr + "\n.execute();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE).toString()));
		assertEquals(recordStr + "\nint count = " + executeApiStr + ".wherePK()\n"+ ".execute();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_PK).toString()));
		assertEquals(recordStr + "\nint count = " + executeApiStr + ".whereUK(\"MainMasterUniqueKey\")\n"+ ".execute();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_UK).toString()));

		StringBuilder recordsStr = new StringBuilder();
		recordsStr.append("List<GRecord> recordList = new ArrayList<>();").append("\n");
		recordsStr.append(recordStr);
		recordsStr.append("recordList.add(record);").append("\n");
		String executeListApiStr = "update(\"jdbc_tool_test_mainmaster\")\n"+
				".set(recordList)\n";
		assertEquals(recordsStr + "\nint count = " + executeListApiStr + ".wherePK()\n"+ ".executeBatch();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_PK).toString()));
		assertEquals(recordsStr + "\nint count = " + executeListApiStr + ".whereUK(\"MainMasterUniqueKey\")\n"+ ".executeBatch();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_UK).toString()));
		assertEquals(recordsStr + "\nint count = " + executeListApiStr + ".wherePK()\n"+ ".executeList();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_PK).toString()));
		assertEquals(recordsStr + "\nint count = " + executeListApiStr + ".whereUK(\"MainMasterUniqueKey\")\n"+ ".executeList();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_UK).toString()));
	}

	@Test
	public void testExport_searchExpWherePartsEquals_reg_delete() {

		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_searchExpWherePartsEquals_reg_delete.xml");
		ApiRequest apiRequest = parseRequestFromFile(getCommonSetting(),"jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_searchExpWherePartsEquals_reg_delete.yaml");
		List<CodeResponse> result = convertVe2Code(apiRequest);

		assertEquals("DELETE", result.get(0).getType());
		assertEquals("仮想表ID: CC71DD95CF7244DFB80A009930DB3F95", result.get(0).getDescription());
		assertEquals(getDeleteSql_searchExpWherePartsEquals_reg_delete(), result.get(0).getSqlScript());
		String whereParamsParts = "Map<String, Object> whereParams = new HashMap<>();\n"+
				"whereParams.put(\"jdbc_tool_test_mainmaster.UnitPrice\",);\n"+
				"whereParams.put(\"jdbc_tool_test_mainmaster.ItemCD\",);";
		String executeApiStr = "delete(\"jdbc_tool_test_mainmaster\")\n"+
				".where(expMap($($(\"jdbc_tool_test_mainmaster.ItemCD = $TRIM([:jdbc_tool_test_mainmaster.ItemCD])\").AND(\"jdbc_tool_test_mainmaster.UnitPrice <> $TRIM([:jdbc_tool_test_mainmaster.UnitPrice])\")), whereParams))";
		assertEquals(whereParamsParts + "\nint count = " + executeApiStr + "\n.execute();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE).toString()));
	}

	public String getDeleteSql_searchExpWherePartsEquals_reg_delete() {
		return "DELETE FROM\n"+
				"    " + dbSchema + ".jdbc_tool_test_mainmaster\n"+
				"WHERE\n"+
				"    (\n"+
				"        " + dbSchema + ".jdbc_tool_test_mainmaster.ItemCD = ?\n"+
				"        AND " + dbSchema + ".jdbc_tool_test_mainmaster.UnitPrice <> ?\n"+
				"    )";
	}

	@Test
	public void testExport_searchExpWherePartsCompare_reg_update() {

		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_searchExpWherePartsCompare_reg_update.xml");
		ApiRequest apiRequest = parseRequestFromFile(getCommonSetting(),"jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_searchExpWherePartsCompare_reg_update.yaml");
		List<CodeResponse> result = convertVe2Code(apiRequest);

		assertEquals("UPDATE", result.get(0).getType());
		assertEquals("仮想表ID: 59EF9C5DD58045A9A952B43DDC13F762", result.get(0).getDescription());
		assertEquals("UPDATE\n"+
				"    " + dbSchema + ".jdbc_tool_test_mainmaster jdbc_tool_test_mainmaster\n"+
				"SET\n"+
				"    ItemCD = ?,\n"+
				"    UnitPrice = ?,\n"+
				"    SellPrice = ?,\n"+
				"    Flag = ?,\n"+
				"    PriceFlag = ?\n"+
				"WHERE\n"+
				"    (\n"+
				"        jdbc_tool_test_mainmaster.UnitPrice > ?\n"+
				"        AND jdbc_tool_test_mainmaster.SellPrice < ?\n"+
				"        AND jdbc_tool_test_mainmaster.Flag <= ?\n"+
				"        AND jdbc_tool_test_mainmaster.PriceFlag >= ?\n"+
				"    )", result.get(0).getSqlScript());
		String whereParamsParts = "Map<String, Object> whereParams = new HashMap<>();\n"+
				"whereParams.put(\"jdbc_tool_test_mainmaster.Flag\",);\n"+
				"whereParams.put(\"jdbc_tool_test_mainmaster.UnitPrice\",);\n"+
				"whereParams.put(\"jdbc_tool_test_mainmaster.PriceFlag\",);\n"+
				"whereParams.put(\"jdbc_tool_test_mainmaster.SellPrice\",);\n";
		String recordStr = "GRecord record = createRecord();\n"+
				"record.setObject(\"ItemCD\",);\n"+
				"record.setObject(\"UnitPrice\",);\n"+
				"record.setObject(\"SellPrice\",);\n"+
				"record.setObject(\"Flag\",);\n"+
				"record.setObject(\"PriceFlag\",);\n";
		String executeApiStr = "update(\"jdbc_tool_test_mainmaster\")\n"+
				".set(record)\n";
		String executeApiWhereStr = ".where(expMap($($(\"jdbc_tool_test_mainmaster.UnitPrice > :jdbc_tool_test_mainmaster.UnitPrice\").AND(\"jdbc_tool_test_mainmaster.SellPrice < :jdbc_tool_test_mainmaster.SellPrice\").AND(\"jdbc_tool_test_mainmaster.Flag <= :jdbc_tool_test_mainmaster.Flag\").AND(\"jdbc_tool_test_mainmaster.PriceFlag >= :jdbc_tool_test_mainmaster.PriceFlag\")), whereParams))";
		assertEquals(whereParamsParts + recordStr + "\nint count = " + executeApiStr + executeApiWhereStr + "\n.execute();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE).toString()));
		assertEquals(recordStr + "\nint count = " + executeApiStr + ".wherePK()\n"+ ".execute();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_PK).toString()));
		assertEquals(recordStr + "\nint count = " + executeApiStr + ".whereUK(\"MainMasterUniqueKey\")\n"+ ".execute();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_UK).toString()));

		StringBuilder recordsStr = new StringBuilder();
		recordsStr.append("List<GRecord> recordList = new ArrayList<>();").append("\n");
		recordsStr.append(recordStr);
		recordsStr.append("recordList.add(record);").append("\n");
		String executeListApiStr = "update(\"jdbc_tool_test_mainmaster\")\n"+
				".set(recordList)\n";
		assertEquals(recordsStr + "\nint count = " + executeListApiStr + ".wherePK()\n"+ ".executeBatch();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_PK).toString()));
		assertEquals(recordsStr + "\nint count = " + executeListApiStr + ".whereUK(\"MainMasterUniqueKey\")\n"+ ".executeBatch();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_UK).toString()));
		assertEquals(recordsStr + "\nint count = " + executeListApiStr + ".wherePK()\n"+ ".executeList();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_PK).toString()));
		assertEquals(recordsStr + "\nint count = " + executeListApiStr + ".whereUK(\"MainMasterUniqueKey\")\n"+ ".executeList();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_UK).toString()));
	}

	@Test
	public void testExport_searchExpWherePartsCompare_reg_delete() {

		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_searchExpWherePartsCompare_reg_delete.xml");
		SettingRequest request = getCommonSetting();
		request.getTransform().put("useForeignKey", false);
		ApiRequest apiRequest = parseRequestFromFile(request,"jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_searchExpWherePartsCompare_reg_delete.yaml");
		List<CodeResponse> result = convertVe2Code(apiRequest);

		assertEquals("DELETE", result.get(0).getType());
		assertEquals("仮想表ID: 59EF9C5DD58045A9A952B43DDC13F762", result.get(0).getDescription());
		assertEquals(getDeleteSql_searchExpWherePartsCompare_reg_delete(), result.get(0).getSqlScript());
		String whereParamsParts = "Map<String, Object> whereParams = new HashMap<>();\n"+
				"whereParams.put(\"jdbc_tool_test_mainmaster.Flag\",);\n"+
				"whereParams.put(\"jdbc_tool_test_mainmaster.UnitPrice\",);\n"+
				"whereParams.put(\"jdbc_tool_test_mainmaster.PriceFlag\",);\n"+
				"whereParams.put(\"jdbc_tool_test_mainmaster.SellPrice\",);";
		String executeApiStr = "delete(\"jdbc_tool_test_mainmaster\")\n"+
				".where(expMap($($(\"jdbc_tool_test_mainmaster.UnitPrice > :jdbc_tool_test_mainmaster.UnitPrice\").AND(\"jdbc_tool_test_mainmaster.SellPrice < :jdbc_tool_test_mainmaster.SellPrice\").AND(\"jdbc_tool_test_mainmaster.Flag <= :jdbc_tool_test_mainmaster.Flag\").AND(\"jdbc_tool_test_mainmaster.PriceFlag >= :jdbc_tool_test_mainmaster.PriceFlag\")), whereParams))";
		assertEquals(whereParamsParts + "\nint count = " + executeApiStr + "\n.execute();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE).toString()));
	}

	public String getDeleteSql_searchExpWherePartsCompare_reg_delete() {
		return "DELETE FROM\n"+
				"    " + dbSchema + ".jdbc_tool_test_mainmaster\n"+
				"WHERE\n"+
				"    (\n"+
				"        " + dbSchema + ".jdbc_tool_test_mainmaster.UnitPrice > ?\n"+
				"        AND " + dbSchema + ".jdbc_tool_test_mainmaster.SellPrice < ?\n"+
				"        AND " + dbSchema + ".jdbc_tool_test_mainmaster.Flag <= ?\n"+
				"        AND " + dbSchema + ".jdbc_tool_test_mainmaster.PriceFlag >= ?\n"+
				"    )";
	}

	@Test
	public void testExport_searchExpWherePartsOptionTrim_reg_update() {

		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_searchExpWherePartsOptionTrim_reg_update.xml");
		ApiRequest apiRequest = parseRequestFromFile(getCommonSetting(),"jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_searchExpWherePartsOptionTrim_reg_update.yaml");
		List<CodeResponse> result = convertVe2Code(apiRequest);

		assertEquals("UPDATE", result.get(0).getType());
		assertEquals("仮想表ID: F80DEB51F0004B1ABEB2D8EA44D4644F", result.get(0).getDescription());
		assertEquals("UPDATE\n"+
				"    " + dbSchema + ".jdbc_tool_test_mainmaster jdbc_tool_test_mainmaster\n"+
				"SET\n"+
				"    ItemCD = ?,\n"+
				"    UnitPrice = ?,\n"+
				"    RegisteredDT = ?,\n"+
				"    ExclusiveFG = ?\n"+
				"WHERE\n"+
				"    jdbc_tool_test_mainmaster.UnitPrice IN (100, 101, 102)\n"+
				"    AND (\n"+
				"        jdbc_tool_test_mainmaster.objectID LIKE ? ESCAPE '/'\n"+
				"        AND jdbc_tool_test_mainmaster.UnitPrice >= ?\n"+
				"    )", result.get(0).getSqlScript());
		String whereParamsParts = "Map<String, Object> whereParams = new HashMap<>();\n"+
				"whereParams.put(\"jdbc_tool_test_mainmaster.UnitPrice\",);\n"+
				"whereParams.put(\"jdbc_tool_test_mainmaster.objectID\",);\n";
		String recordStr = "GRecord record = createRecord();\n"+
				"record.setObject(\"ItemCD\",);\n"+
				"record.setObject(\"UnitPrice\",);\n"+
				"record.setObject(\"RegisteredDT\",);\n"+
				"record.setObject(\"ExclusiveFG\",);\n";
		String executeApiStr = "update(\"jdbc_tool_test_mainmaster\")\n"+
				".set(record)\n";
		String executeApiWhereStr = ".where(expMap($(\"jdbc_tool_test_mainmaster.UnitPrice IN (100, 101, 102)\").AND($(\"jdbc_tool_test_mainmaster.objectID LIKE $CONCAT('%', $CONCAT($TRIM([:jdbc_tool_test_mainmaster.objectID]), '%')) ESCAPE '/'\").AND(\"jdbc_tool_test_mainmaster.UnitPrice >= $TRIM([:jdbc_tool_test_mainmaster.UnitPrice])\")), whereParams))";
		assertEquals(whereParamsParts + recordStr + "\nint count = " + executeApiStr + executeApiWhereStr + "\n.execute();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE).toString()));
		assertEquals(recordStr + "\nint count = " + executeApiStr + ".wherePK()\n"+ ".execute();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_PK).toString()));
		assertEquals(recordStr + "\nint count = " + executeApiStr + ".whereUK(\"MainMasterUniqueKey\")\n"+ ".execute();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_UK).toString()));

		StringBuilder recordsStr = new StringBuilder();
		recordsStr.append("List<GRecord> recordList = new ArrayList<>();").append("\n");
		recordsStr.append(recordStr);
		recordsStr.append("recordList.add(record);").append("\n");
		String executeListApiStr = "update(\"jdbc_tool_test_mainmaster\")\n"+
				".set(recordList)\n";
		assertEquals(recordsStr + "\nint count = " + executeListApiStr + ".wherePK()\n"+ ".executeBatch();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_PK).toString()));
		assertEquals(recordsStr + "\nint count = " + executeListApiStr + ".whereUK(\"MainMasterUniqueKey\")\n"+ ".executeBatch();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_UK).toString()));
		assertEquals(recordsStr + "\nint count = " + executeListApiStr + ".wherePK()\n"+ ".executeList();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_PK).toString()));
		assertEquals(recordsStr + "\nint count = " + executeListApiStr + ".whereUK(\"MainMasterUniqueKey\")\n"+ ".executeList();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_UK).toString()));
	}

	@Test
	public void testExport_searchExpWherePartsOptionTrim_reg_delete() {

		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_searchExpWherePartsOptionTrim_reg_delete.xml");
		ApiRequest apiRequest = parseRequestFromFile(getCommonSetting(),"jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_searchExpWherePartsOptionTrim_reg_delete.yaml");
		List<CodeResponse> result = convertVe2Code(apiRequest);

		assertEquals("DELETE", result.get(0).getType());
		assertEquals("仮想表ID: F80DEB51F0004B1ABEB2D8EA44D4644F", result.get(0).getDescription());
		assertEquals(getDeleteSql_searchExpWherePartsOptionTrim_reg_delete(), result.get(0).getSqlScript());
		String whereParamsParts = "Map<String, Object> whereParams = new HashMap<>();\n"+
				"whereParams.put(\"jdbc_tool_test_mainmaster.UnitPrice\",);\n"+
				"whereParams.put(\"jdbc_tool_test_mainmaster.objectID\",);";
		String executeApiStr = "delete(\"jdbc_tool_test_mainmaster\")\n"+
				".where(expMap($(\"jdbc_tool_test_mainmaster.UnitPrice IN (100, 101, 102)\").AND($(\"jdbc_tool_test_mainmaster.objectID LIKE $CONCAT('%', $CONCAT($TRIM([:jdbc_tool_test_mainmaster.objectID]), '%')) ESCAPE '/'\").AND(\"jdbc_tool_test_mainmaster.UnitPrice >= $TRIM([:jdbc_tool_test_mainmaster.UnitPrice])\")), whereParams))";
		assertEquals(whereParamsParts + "\nint count = " + executeApiStr + "\n.execute();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE).toString()));
	}

	public String getDeleteSql_searchExpWherePartsOptionTrim_reg_delete() {
		return "DELETE FROM\n"+
				"    " + dbSchema + ".jdbc_tool_test_mainmaster\n"+
				"WHERE\n"+
				"    " + dbSchema + ".jdbc_tool_test_mainmaster.UnitPrice IN (100, 101, 102)\n"+
				"    AND (\n"+
				"        " + dbSchema + ".jdbc_tool_test_mainmaster.objectID LIKE ? ESCAPE '/'\n"+
				"        AND " + dbSchema + ".jdbc_tool_test_mainmaster.UnitPrice >= ?\n"+
				"    )";
	}

	@Test
	public void testExport_filterExpWherePartsLikeF_reg_update() {

		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_filterExpWherePartsLikeF_reg_update.xml");
		ApiRequest apiRequest = parseRequestFromFile(getCommonSetting(),"jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_filterExpWherePartsLikeF_reg_update.yaml");
		List<CodeResponse> result = convertVe2Code(apiRequest);

		assertEquals("UPDATE", result.get(0).getType());
		assertEquals("仮想表ID: 718905E41FF64047B7AF5DE8E90B51C8", result.get(0).getDescription());
		assertEquals("UPDATE\n"+
				"    " + dbSchema + ".jdbc_tool_test_mainmaster jdbc_tool_test_mainmaster\n"+
				"SET\n"+
				"    ItemCD = ?,\n"+
				"    UnitPrice = ?\n"+
				"WHERE\n"+
				"    jdbc_tool_test_mainmaster.ItemCD LIKE 'item%'", result.get(0).getSqlScript());
		String recordStr = "GRecord record = createRecord();\n"+
				"record.setObject(\"ItemCD\",);\n"+
				"record.setObject(\"UnitPrice\",);\n";
		String executeApiStr = "update(\"jdbc_tool_test_mainmaster\")\n"+
				".set(record)\n";
		String executeApiWhereStr = ".where(expMap($(\"jdbc_tool_test_mainmaster.ItemCD LIKE 'item%'\"), Collections.emptyMap()))";
		assertEquals(recordStr + "\nint count = " + executeApiStr + executeApiWhereStr + "\n.execute();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE).toString()));
		assertEquals(recordStr + "\nint count = " + executeApiStr + ".wherePK()\n"+ ".execute();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_PK).toString()));
		assertEquals(recordStr + "\nint count = " + executeApiStr + ".whereUK(\"MainMasterUniqueKey\")\n"+ ".execute();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_UK).toString()));

		StringBuilder recordsStr = new StringBuilder();
		recordsStr.append("List<GRecord> recordList = new ArrayList<>();").append("\n");
		recordsStr.append(recordStr);
		recordsStr.append("recordList.add(record);").append("\n");
		String executeListApiStr = "update(\"jdbc_tool_test_mainmaster\")\n"+
				".set(recordList)\n";
		assertEquals(recordsStr + "\nint count = " + executeListApiStr + ".wherePK()\n"+ ".executeBatch();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_PK).toString()));
		assertEquals(recordsStr + "\nint count = " + executeListApiStr + ".whereUK(\"MainMasterUniqueKey\")\n"+ ".executeBatch();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_UK).toString()));
		assertEquals(recordsStr + "\nint count = " + executeListApiStr + ".wherePK()\n"+ ".executeList();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_PK).toString()));
		assertEquals(recordsStr + "\nint count = " + executeListApiStr + ".whereUK(\"MainMasterUniqueKey\")\n"+ ".executeList();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_UK).toString()));
	}

	@Test
	public void testExport_filterExpWherePartsLikeF_reg_delete() {

		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_filterExpWherePartsLikeF_reg_delete.xml");
		ApiRequest apiRequest = parseRequestFromFile(getCommonSetting(),"jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_filterExpWherePartsLikeF_reg_delete.yaml");
		List<CodeResponse> result = convertVe2Code(apiRequest);

		assertEquals("DELETE", result.get(0).getType());
		assertEquals("仮想表ID: 718905E41FF64047B7AF5DE8E90B51C8", result.get(0).getDescription());
		assertEquals(getDeleteSql_filterExpWherePartsLikeF_reg_delete(), result.get(0).getSqlScript());
		String executeApiStr = "delete(\"jdbc_tool_test_mainmaster\")\n"+
				".where(expMap($(\"jdbc_tool_test_mainmaster.ItemCD LIKE 'item%'\"), Collections.emptyMap()))";
		assertEquals("int count = " + executeApiStr + "\n.execute();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE).toString()));
	}

	public String getDeleteSql_filterExpWherePartsLikeF_reg_delete() {
		return "DELETE FROM\n"+
				"    " + dbSchema + ".jdbc_tool_test_mainmaster\n"+
				"WHERE\n"+
				"    " + dbSchema + ".jdbc_tool_test_mainmaster.ItemCD LIKE 'item%'";
	}

	@Test
	public void testExport_filterExpWherePartsLikeFB_reg_update() {

		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_filterExpWherePartsLikeFB_reg_update.xml");
		ApiRequest apiRequest = parseRequestFromFile(getCommonSetting(),"jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_filterExpWherePartsLikeFB_reg_update.yaml");
		List<CodeResponse> result = convertVe2Code(apiRequest);

		assertEquals("UPDATE", result.get(0).getType());
		assertEquals("仮想表ID: 80A621FD138E4F28B53494E4DC8CBAAD", result.get(0).getDescription());
		assertEquals("UPDATE\n"+
				"    " + dbSchema + ".jdbc_tool_test_unitmaster jdbc_tool_test_unitmaster\n"+
				"SET\n"+
				"    Code = ?,\n"+
				"    Name = ?\n"+
				"WHERE\n"+
				"    jdbc_tool_test_unitmaster.Name LIKE '%name%'", result.get(0).getSqlScript());
		String recordStr = "GRecord record = createRecord();\n"+
				"record.setObject(\"Code\",);\n"+
				"record.setObject(\"Name\",);\n";
		String executeApiStr = "update(\"jdbc_tool_test_unitmaster\")\n"+
				".set(record)\n";
		String executeApiWhereStr = ".where(expMap($(\"jdbc_tool_test_unitmaster.Name LIKE '%name%'\"), Collections.emptyMap()))";
		assertEquals(recordStr + "\nint count = " + executeApiStr + executeApiWhereStr + "\n.execute();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE).toString()));
		assertEquals(recordStr + "\nint count = " + executeApiStr + ".wherePK()\n"+ ".execute();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_PK).toString()));
		assertEquals(recordStr + "\nint count = " + executeApiStr + ".whereUK(\"UnitMasterUK\")\n"+ ".execute();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_UK).toString()));

		StringBuilder recordsStr = new StringBuilder();
		recordsStr.append("List<GRecord> recordList = new ArrayList<>();").append("\n");
		recordsStr.append(recordStr);
		recordsStr.append("recordList.add(record);").append("\n");
		String executeListApiStr = "update(\"jdbc_tool_test_unitmaster\")\n"+
				".set(recordList)\n";
		assertEquals(recordsStr + "\nint count = " + executeListApiStr + ".wherePK()\n"+ ".executeBatch();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_PK).toString()));
		assertEquals(recordsStr + "\nint count = " + executeListApiStr + ".whereUK(\"UnitMasterUK\")" +  "\n.executeBatch();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_UK).toString()));
		assertEquals(recordsStr + "\nint count = " + executeListApiStr + ".wherePK()\n"+ ".executeList();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_PK).toString()));
		assertEquals(recordsStr + "\nint count = " + executeListApiStr + ".whereUK(\"UnitMasterUK\")" +  "\n.executeList();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_UK).toString()));
	}

	@Test
	public void testExport_filterExpWherePartsLikeFB_reg_delete() {

		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_filterExpWherePartsLikeFB_reg_delete.xml");
		ApiRequest apiRequest = parseRequestFromFile(getCommonSetting(),"jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_filterExpWherePartsLikeFB_reg_delete.yaml");
		List<CodeResponse> result = convertVe2Code(apiRequest);

		assertEquals("DELETE", result.get(0).getType());
		assertEquals("仮想表ID: 80A621FD138E4F28B53494E4DC8CBAAD", result.get(0).getDescription());
		assertEquals(getDeleteSql_filterExpWherePartsLikeFB_reg_delete(), result.get(0).getSqlScript());
		String executeApiStr = "delete(\"jdbc_tool_test_unitmaster\")\n"+
				".where(expMap($(\"jdbc_tool_test_unitmaster.Name LIKE '%name%'\"), Collections.emptyMap()))";
		assertEquals("int count = " + executeApiStr + "\n.execute();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE).toString()));
	}

	public String getDeleteSql_filterExpWherePartsLikeFB_reg_delete() {
		return "DELETE FROM\n"+
				"    " + dbSchema + ".jdbc_tool_test_unitmaster\n"+
				"WHERE\n"+
				"    " + dbSchema + ".jdbc_tool_test_unitmaster.Name LIKE '%name%'";
	}

	@Test
	public void testExport_filterExpWherePartsLikeB_reg_update() {

		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_filterExpWherePartsLikeB_reg_update.xml");
		ApiRequest apiRequest = parseRequestFromFile(getCommonSetting(),"jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_filterExpWherePartsLikeB_reg_update.yaml");
		List<CodeResponse> result = convertVe2Code(apiRequest);

		assertEquals("UPDATE", result.get(0).getType());
		assertEquals("仮想表ID: 59CCE10D855740D89844D3C62AA18461", result.get(0).getDescription());
		assertEquals("UPDATE\n"+
				"    " + dbSchema + ".jdbc_tool_test_ordermaster jdbc_tool_test_ordermaster\n"+
				"SET\n"+
				"    SlipNO = ?,\n"+
				"    CorporateNA = ?\n"+
				"WHERE\n"+
				"    jdbc_tool_test_ordermaster.CorporateNA LIKE '%na'", result.get(0).getSqlScript());
		String recordStr = "GRecord record = createRecord();\n"+
				"record.setObject(\"SlipNO\",);\n"+
				"record.setObject(\"CorporateNA\",);\n";
		String executeApiStr = "update(\"jdbc_tool_test_ordermaster\")\n"+
				".set(record)\n";
		String executeApiWhereStr = ".where(expMap($(\"jdbc_tool_test_ordermaster.CorporateNA LIKE '%na'\"), Collections.emptyMap()))";
		assertEquals(recordStr + "\nint count = " + executeApiStr + executeApiWhereStr + "\n.execute();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE).toString()));
		assertEquals(recordStr + "\nint count = " + executeApiStr + ".wherePK()\n"+ ".execute();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_PK).toString()));
		assertEquals(recordStr + "\nint count = " + executeApiStr + ".whereUK(\"OrderMasterUK\")\n"+ ".execute();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_UK).toString()));

		StringBuilder recordsStr = new StringBuilder();
		recordsStr.append("List<GRecord> recordList = new ArrayList<>();").append("\n");
		recordsStr.append(recordStr);
		recordsStr.append("recordList.add(record);").append("\n");
		String executeListApiStr = "update(\"jdbc_tool_test_ordermaster\")\n"+
				".set(recordList)\n";
		assertEquals(recordsStr + "\nint count = " + executeListApiStr + ".wherePK()\n"+ ".executeBatch();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_PK).toString()));
		assertEquals(recordsStr + "\nint count = " + executeListApiStr + ".whereUK(\"OrderMasterUK\")\n"+ ".executeBatch();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_UK).toString()));
		assertEquals(recordsStr + "\nint count = " + executeListApiStr + ".wherePK()\n"+ ".executeList();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_PK).toString()));
		assertEquals(recordsStr + "\nint count = " + executeListApiStr + ".whereUK(\"OrderMasterUK\")\n"+ ".executeList();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_UK).toString()));
	}

	@Test
	public void testExport_filterExpWherePartsLikeB_reg_delete() {

		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_filterExpWherePartsLikeB_reg_delete.xml");
		ApiRequest apiRequest = parseRequestFromFile(getCommonSetting(),"jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_filterExpWherePartsLikeB_reg_delete.yaml");
		List<CodeResponse> result = convertVe2Code(apiRequest);

		assertEquals("DELETE", result.get(0).getType());
		assertEquals("仮想表ID: 59CCE10D855740D89844D3C62AA18461", result.get(0).getDescription());
		assertEquals(getDeleteSql_filterExpWherePartsLikeB_reg_delete(), result.get(0).getSqlScript());
		String executeApiStr = "delete(\"jdbc_tool_test_ordermaster\")\n"+
				".where(expMap($(\"jdbc_tool_test_ordermaster.CorporateNA LIKE '%na'\"), Collections.emptyMap()))";
		assertEquals("int count = " + executeApiStr + "\n.execute();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE).toString()));
	}

	public String getDeleteSql_filterExpWherePartsLikeB_reg_delete() {
		return "DELETE FROM\n"+
				"    " + dbSchema + ".jdbc_tool_test_ordermaster\n"+
				"WHERE\n"+
				"    " + dbSchema + ".jdbc_tool_test_ordermaster.CorporateNA LIKE '%na'";
	}

	@Test
	public void testExport_filterExpWherePartsEquals_reg_update() {

		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_filterExpWherePartsEquals_reg_update.xml");
		ApiRequest apiRequest = parseRequestFromFile(getCommonSetting(),"jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_filterExpWherePartsEquals_reg_update.yaml");
		List<CodeResponse> result = convertVe2Code(apiRequest);

		assertEquals("UPDATE", result.get(0).getType());
		assertEquals("仮想表ID: 0F791999DDC84198A27A7309D24F6334", result.get(0).getDescription());
		assertEquals("UPDATE\n"+
				"    " + dbSchema + ".jdbc_tool_test_mainmaster jdbc_tool_test_mainmaster\n"+
				"SET\n"+
				"    ItemCD = ?,\n"+
				"    UnitPrice = ?\n"+
				"WHERE\n"+
				"    (\n"+
				"        jdbc_tool_test_mainmaster.objectID = '1001'\n"+
				"        AND jdbc_tool_test_mainmaster.ItemCD <> '9001'\n"+
				"    )", result.get(0).getSqlScript());
		String recordStr = "GRecord record = createRecord();\n"+
				"record.setObject(\"ItemCD\",);\n"+
				"record.setObject(\"UnitPrice\",);\n";
		String executeApiStr = "update(\"jdbc_tool_test_mainmaster\")\n"+
				".set(record)\n";
		String executeApiWhereStr = ".where(expMap($($(\"jdbc_tool_test_mainmaster.objectID = '1001'\").AND(\"jdbc_tool_test_mainmaster.ItemCD <> '9001'\")), Collections.emptyMap()))";
		assertEquals(recordStr + "\nint count = " + executeApiStr + executeApiWhereStr + "\n.execute();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE).toString()));
		assertEquals(recordStr + "\nint count = " + executeApiStr + ".wherePK()\n"+ ".execute();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_PK).toString()));
		assertEquals(recordStr + "\nint count = " + executeApiStr + ".whereUK(\"MainMasterUniqueKey\")\n"+ ".execute();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_UK).toString()));

		StringBuilder recordsStr = new StringBuilder();
		recordsStr.append("List<GRecord> recordList = new ArrayList<>();").append("\n");
		recordsStr.append(recordStr);
		recordsStr.append("recordList.add(record);").append("\n");
		String executeListApiStr = "update(\"jdbc_tool_test_mainmaster\")\n"+
				".set(recordList)\n";
		assertEquals(recordsStr + "\nint count = " + executeListApiStr + ".wherePK()"  + "\n.executeBatch();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_PK).toString()));
		assertEquals(recordsStr + "\nint count = " + executeListApiStr + ".whereUK(\"MainMasterUniqueKey\")\n"+ ".executeBatch();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_UK).toString()));
		assertEquals(recordsStr + "\nint count = " + executeListApiStr + ".wherePK()"  + "\n.executeList();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_PK).toString()));
		assertEquals(recordsStr + "\nint count = " + executeListApiStr + ".whereUK(\"MainMasterUniqueKey\")\n"+ ".executeList();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_UK).toString()));
	}

	@Test
	public void testExport_filterExpWherePartsEquals_reg_delete() {

		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_filterExpWherePartsEquals_reg_delete.xml");
		ApiRequest apiRequest = parseRequestFromFile(getCommonSetting(),"jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_filterExpWherePartsEquals_reg_delete.yaml");
		List<CodeResponse> result = convertVe2Code(apiRequest);

		assertEquals("DELETE", result.get(0).getType());
		assertEquals("仮想表ID: 0F791999DDC84198A27A7309D24F6334", result.get(0).getDescription());
		assertEquals(getDeleteSql_filterExpWherePartsEquals_reg_delete(), result.get(0).getSqlScript());
		String executeApiStr = "delete(\"jdbc_tool_test_mainmaster\")\n"+
				".where(expMap($($(\"jdbc_tool_test_mainmaster.objectID = '1001'\").AND(\"jdbc_tool_test_mainmaster.ItemCD <> '9001'\")), Collections.emptyMap()))";
		assertEquals("int count = " + executeApiStr + "\n.execute();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE).toString()));
	}

	public String getDeleteSql_filterExpWherePartsEquals_reg_delete() {
		return "DELETE FROM\n"+
				"    " + dbSchema + ".jdbc_tool_test_mainmaster\n"+
				"WHERE\n"+
				"    (\n"+
				"        " + dbSchema + ".jdbc_tool_test_mainmaster.objectID = '1001'\n"+
				"        AND " + dbSchema + ".jdbc_tool_test_mainmaster.ItemCD <> '9001'\n"+
				"    )";
	}

	@Test
	public void testExport_filterExpWherePartsCompare_reg_update() {

		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_filterExpWherePartsCompare_reg_update.xml");
		ApiRequest apiRequest = parseRequestFromFile(getCommonSetting(),"jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_filterExpWherePartsCompare_reg_update.yaml");
		List<CodeResponse> result = convertVe2Code(apiRequest);

		assertEquals("UPDATE", result.get(0).getType());
		assertEquals("仮想表ID: 9F42C74C3ACA4DA9815963583F3F6D1D", result.get(0).getDescription());
		assertEquals("UPDATE\n"+
				"    " + dbSchema + ".jdbc_tool_test_mainmaster jdbc_tool_test_mainmaster\n"+
				"SET\n"+
				"    ItemCD = ?,\n"+
				"    UnitPrice = ?,\n"+
				"    SellPrice = ?,\n"+
				"    Flag = ?,\n"+
				"    PriceFlag = ?\n"+
				"WHERE\n"+
				"    (\n"+
				"        jdbc_tool_test_mainmaster.UnitPrice > 100\n"+
				"        AND jdbc_tool_test_mainmaster.UnitPrice < 500\n"+
				"        AND jdbc_tool_test_mainmaster.SellPrice <= 1000\n"+
				"        AND jdbc_tool_test_mainmaster.SellPrice >= 300\n"+
				"    )", result.get(0).getSqlScript());
		String recordStr = "GRecord record = createRecord();\n"+
				"record.setObject(\"ItemCD\",);\n"+
				"record.setObject(\"UnitPrice\",);\n"+
				"record.setObject(\"SellPrice\",);\n"+
				"record.setObject(\"Flag\",);\n"+
				"record.setObject(\"PriceFlag\",);\n";
		String executeApiStr = "update(\"jdbc_tool_test_mainmaster\")\n"+
				".set(record)\n";
		String executeWhereApiStr = ".where(expMap($($(\"jdbc_tool_test_mainmaster.UnitPrice > 100\").AND(\"jdbc_tool_test_mainmaster.UnitPrice < 500\").AND(\"jdbc_tool_test_mainmaster.SellPrice <= 1000\").AND(\"jdbc_tool_test_mainmaster.SellPrice >= 300\")), Collections.emptyMap()))";
		assertEquals(recordStr + "\nint count = " + executeApiStr + executeWhereApiStr + "\n.execute();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE).toString()));
		assertEquals(recordStr + "\nint count = " + executeApiStr + ".wherePK()\n"+ ".execute();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_PK).toString()));
		assertEquals(recordStr + "\nint count = " + executeApiStr + ".whereUK(\"MainMasterUniqueKey\")\n"+ ".execute();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_UK).toString()));

		StringBuilder recordsStr = new StringBuilder();
		recordsStr.append("List<GRecord> recordList = new ArrayList<>();").append("\n");
		recordsStr.append(recordStr);
		recordsStr.append("recordList.add(record);").append("\n");
		String executeListApiStr = "update(\"jdbc_tool_test_mainmaster\")\n"+
				".set(recordList)\n";
		assertEquals(recordsStr + "\nint count = " + executeListApiStr + ".wherePK()"  + "\n.executeBatch();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_PK).toString()));
		assertEquals(recordsStr + "\nint count = " + executeListApiStr + ".whereUK(\"MainMasterUniqueKey\")"  + "\n.executeBatch();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_UK).toString()));
		assertEquals(recordsStr + "\nint count = " + executeListApiStr + ".wherePK()"  + "\n.executeList();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_PK).toString()));
		assertEquals(recordsStr + "\nint count = " + executeListApiStr + ".whereUK(\"MainMasterUniqueKey\")"  + "\n.executeList();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_UK).toString()));
	}

	@Test
	public void testExport_filterExpWherePartsCompare_reg_delete() {

		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_filterExpWherePartsCompare_reg_delete.xml");
		ApiRequest apiRequest = parseRequestFromFile(getCommonSetting(),"jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_filterExpWherePartsCompare_reg_delete.yaml");
		List<CodeResponse> result = convertVe2Code(apiRequest);

		assertEquals("DELETE", result.get(0).getType());
		assertEquals("仮想表ID: 9F42C74C3ACA4DA9815963583F3F6D1D", result.get(0).getDescription());
		assertEquals(getDeleteSql_filterExpWherePartsCompare_reg_delete(), result.get(0).getSqlScript());
		String executeApiStr = "delete(\"jdbc_tool_test_mainmaster\")\n"+
				".where(expMap($($(\"jdbc_tool_test_mainmaster.UnitPrice > 100\").AND(\"jdbc_tool_test_mainmaster.UnitPrice < 500\").AND(\"jdbc_tool_test_mainmaster.SellPrice <= 1000\").AND(\"jdbc_tool_test_mainmaster.SellPrice >= 300\")), Collections.emptyMap()))";
		assertEquals("int count = " + executeApiStr + "\n.execute();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE).toString()));
	}

	public String getDeleteSql_filterExpWherePartsCompare_reg_delete() {
		return "DELETE FROM\n"+
				"    " + dbSchema + ".jdbc_tool_test_mainmaster\n"+
				"WHERE\n"+
				"    (\n"+
				"        " + dbSchema + ".jdbc_tool_test_mainmaster.UnitPrice > 100\n"+
				"        AND " + dbSchema + ".jdbc_tool_test_mainmaster.UnitPrice < 500\n"+
				"        AND " + dbSchema + ".jdbc_tool_test_mainmaster.SellPrice <= 1000\n"+
				"        AND " + dbSchema + ".jdbc_tool_test_mainmaster.SellPrice >= 300\n"+
				"    )";
	}

	@Test
	public void testExport_filterExpWherePartsNullreg_update() {

		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_filterExpWherePartsNull_reg_update.xml");
		ApiRequest apiRequest = parseRequestFromFile(getCommonSetting(),"jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_filterExpWherePartsNull_reg_update.yaml");
		List<CodeResponse> result = convertVe2Code(apiRequest);

		assertEquals("UPDATE", result.get(0).getType());
		assertEquals("仮想表ID: AD3F414042CA40FFA5F91F6DE9997A45", result.get(0).getDescription());
		assertEquals("UPDATE\n"+
				"    " + dbSchema + ".jdbc_tool_test_mainmaster jdbc_tool_test_mainmaster\n"+
				"SET\n"+
				"    ItemCD = ?,\n"+
				"    UnitPrice = ?\n"+
				"WHERE\n"+
				"    (\n"+
				"        jdbc_tool_test_mainmaster.ItemCD IS NULL\n"+
				"        AND jdbc_tool_test_mainmaster.UnitPrice IS NOT NULL\n"+
				"    )", result.get(0).getSqlScript());
		String recordStr = "GRecord record = createRecord();\n"+
				"record.setObject(\"ItemCD\",);\n"+
				"record.setObject(\"UnitPrice\",);\n";
		String executeApiStr = "update(\"jdbc_tool_test_mainmaster\")\n"+
				".set(record)\n";
		String executeApiWhereStr = ".where(expMap($($(\"jdbc_tool_test_mainmaster.ItemCD IS NULL\").AND(\"jdbc_tool_test_mainmaster.UnitPrice IS NOT NULL\")), Collections.emptyMap()))";
		assertEquals(recordStr + "\nint count = " + executeApiStr + executeApiWhereStr + "\n.execute();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE).toString()));
		assertEquals(recordStr + "\nint count = " + executeApiStr + ".wherePK()\n"+ ".execute();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_PK).toString()));
		assertEquals(recordStr + "\nint count = " + executeApiStr + ".whereUK(\"MainMasterUniqueKey\")\n"+ ".execute();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_UK).toString()));

		StringBuilder recordsStr = new StringBuilder();
		recordsStr.append("List<GRecord> recordList = new ArrayList<>();").append("\n");
		recordsStr.append(recordStr);
		recordsStr.append("recordList.add(record);").append("\n");
		String executeListApiStr = "update(\"jdbc_tool_test_mainmaster\")\n"+
				".set(recordList)\n";
		assertEquals(recordsStr + "\nint count = " + executeListApiStr + ".wherePK()\n"+ ".executeBatch();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_PK).toString()));
		assertEquals(recordsStr + "\nint count = " + executeListApiStr + ".whereUK(\"MainMasterUniqueKey\")\n"+ ".executeBatch();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_UK).toString()));
		assertEquals(recordsStr + "\nint count = " + executeListApiStr + ".wherePK()\n"+ ".executeList();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_PK).toString()));
		assertEquals(recordsStr + "\nint count = " + executeListApiStr + ".whereUK(\"MainMasterUniqueKey\")\n"+ ".executeList();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_UK).toString()));
	}

	@Test
	public void testExport_filterExpWherePartsNull_reg_delete() {

		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_filterExpWherePartsNull_reg_delete.xml");
		ApiRequest apiRequest = parseRequestFromFile(getCommonSetting(),"jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_filterExpWherePartsNull_reg_delete.yaml");
		List<CodeResponse> result = convertVe2Code(apiRequest);

		assertEquals("DELETE", result.get(0).getType());
		assertEquals("仮想表ID: AD3F414042CA40FFA5F91F6DE9997A45", result.get(0).getDescription());
		assertEquals(getDeleteSql_filterExpWherePartsNull_reg_delete(), result.get(0).getSqlScript());
		String executeApiStr = "delete(\"jdbc_tool_test_mainmaster\")\n"+
				".where(expMap($($(\"jdbc_tool_test_mainmaster.ItemCD IS NULL\").AND(\"jdbc_tool_test_mainmaster.UnitPrice IS NOT NULL\")), Collections.emptyMap()))";
		assertEquals("int count = " + executeApiStr + "\n.execute();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE).toString()));
	}

	public String getDeleteSql_filterExpWherePartsNull_reg_delete() {
		return "DELETE FROM\n"+
				"    " + dbSchema + ".jdbc_tool_test_mainmaster\n"+
				"WHERE\n"+
				"    (\n"+
				"        " + dbSchema + ".jdbc_tool_test_mainmaster.ItemCD IS NULL\n"+
				"        AND " + dbSchema + ".jdbc_tool_test_mainmaster.UnitPrice IS NOT NULL\n"+
				"    )";
	}

	@Test
	public void testExport_filterExpWherePartsIn_reg_update() {

		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_filterExpWherePartsIn_reg_update.xml");
		ApiRequest apiRequest = parseRequestFromFile(getCommonSetting(),"jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_filterExpWherePartsIn_reg_update.yaml");
		List<CodeResponse> result = convertVe2Code(apiRequest);

		assertEquals("UPDATE", result.get(0).getType());
		assertEquals("仮想表ID: E1F30CCA20B54049B9ADFBD8488168DD", result.get(0).getDescription());
		assertEquals("UPDATE\n"+
				"    " + dbSchema + ".jdbc_tool_test_mainmaster jdbc_tool_test_mainmaster\n"+
				"SET\n"+
				"    ItemCD = ?,\n"+
				"    UnitPrice = ?\n"+
				"WHERE\n"+
				"    (\n"+
				"        jdbc_tool_test_mainmaster.ItemCD IN ('item010', '1001')\n"+
				"        AND jdbc_tool_test_mainmaster.UnitPrice NOT IN (10, 20, 30)\n"+
				"    )", result.get(0).getSqlScript());
		String recordStr = "GRecord record = createRecord();\n"+
				"record.setObject(\"ItemCD\",);\n"+
				"record.setObject(\"UnitPrice\",);\n";
		String executeApiStr = "update(\"jdbc_tool_test_mainmaster\")\n"+
				".set(record)\n";
		String executeApiWhereStr = ".where(expMap($($(\"jdbc_tool_test_mainmaster.ItemCD IN ('item010', '1001')\").AND(\"jdbc_tool_test_mainmaster.UnitPrice NOT IN (10, 20, 30)\")), Collections.emptyMap()))";
		assertEquals(recordStr + "\nint count = " + executeApiStr + executeApiWhereStr + "\n.execute();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE).toString()));
		assertEquals(recordStr + "\nint count = " + executeApiStr + ".wherePK()\n"+ ".execute();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_PK).toString()));
		assertEquals(recordStr + "\nint count = " + executeApiStr + ".whereUK(\"MainMasterUniqueKey\")\n"+ ".execute();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_UK).toString()));

		StringBuilder recordsStr = new StringBuilder();
		recordsStr.append("List<GRecord> recordList = new ArrayList<>();").append("\n");
		recordsStr.append(recordStr);
		recordsStr.append("recordList.add(record);").append("\n");
		String executeListApiStr = "update(\"jdbc_tool_test_mainmaster\")\n"+
				".set(recordList)\n";
		assertEquals(recordsStr + "\nint count = " + executeListApiStr+ ".wherePK()"  + "\n.executeBatch();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_PK).toString()));
		assertEquals(recordsStr + "\nint count = " + executeListApiStr + ".whereUK(\"MainMasterUniqueKey\")\n"+ ".executeBatch();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_UK).toString()));
		assertEquals(recordsStr + "\nint count = " + executeListApiStr+ ".wherePK()"  + "\n.executeList();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_PK).toString()));
		assertEquals(recordsStr + "\nint count = " + executeListApiStr + ".whereUK(\"MainMasterUniqueKey\")\n"+ ".executeList();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_UK).toString()));
	}

	@Test
	public void testExport_filterExpWherePartsIn_reg_delete() {

		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_filterExpWherePartsIn_reg_delete.xml");
		ApiRequest apiRequest = parseRequestFromFile(getCommonSetting(),"jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_filterExpWherePartsIn_reg_delete.yaml");
		List<CodeResponse> result = convertVe2Code(apiRequest);

		assertEquals("DELETE", result.get(0).getType());
		assertEquals("仮想表ID: E1F30CCA20B54049B9ADFBD8488168DD", result.get(0).getDescription());
		assertEquals(getDeleteSql_filterExpWherePartsIn_reg_delete(), result.get(0).getSqlScript());
		String executeApiStr = "delete(\"jdbc_tool_test_mainmaster\")\n"+
				".where(expMap($($(\"jdbc_tool_test_mainmaster.ItemCD IN ('item010', '1001')\").AND(\"jdbc_tool_test_mainmaster.UnitPrice NOT IN (10, 20, 30)\")), Collections.emptyMap()))";
		assertEquals("int count = " + executeApiStr + "\n.execute();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE).toString()));
	}

	public String getDeleteSql_filterExpWherePartsIn_reg_delete() {
		return "DELETE FROM\n"+
				"    " + dbSchema + ".jdbc_tool_test_mainmaster\n"+
				"WHERE\n"+
				"    (\n"+
				"        " + dbSchema + ".jdbc_tool_test_mainmaster.ItemCD IN ('item010', '1001')\n"+
				"        AND " + dbSchema + ".jdbc_tool_test_mainmaster.UnitPrice NOT IN (10, 20, 30)\n"+
				"    )";
	}

	@Test
	public void testExport_join_mix() {

		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_join_mix.xml");
		ApiRequest apiRequest = parseRequestFromFile(getCommonSetting(),"jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_join_mix.yaml");
		List<CodeResponse> result = convertVe2Code(apiRequest);

		assertEquals("SELECT", result.get(0).getType());
		assertEquals("仮想表ID: E44ADEF7B30549449EDF193ECFB66631", result.get(0).getDescription());
		assertEquals("SELECT\n"+
				"    jdbc_tool_test_pk_uk_mainmaster.objectID,\n"+
				"    jdbc_tool_test_pk_uk_mainmaster.ItemCD,\n"+
				"    jdbc_tool_test_pk_uk_mainmaster.ItemNA,\n"+
				"    jdbc_tool_test_pk_uk_mainmaster.UnitPrice,\n"+
				"    jdbc_tool_test_pk_uk_mainmaster.SellPrice,\n"+
				"    jdbc_tool_test_pk_uk_mainmaster.Flag,\n"+
				"    jdbc_tool_test_pk_uk_mainmaster.PriceFlag,\n"+
				"    jdbc_tool_test_pk_uk_mainmaster.ExclusiveFG,\n"+
				"    jdbc_tool_test_pk_uk_mainmaster.RegisteredDT,\n"+
				"    jdbc_tool_test_pk_uk_mainmaster.UpdatedDT\n"+
				"FROM\n"+
				"    " + dbSchema + ".jdbc_tool_test_pk_uk_mainmaster jdbc_tool_test_pk_uk_mainmaster\n"+
				"    LEFT OUTER JOIN gef_jdbc_tool.jdbc_tool_test_unitmaster jdbc_tool_test_unitmaster ON jdbc_tool_test_pk_uk_mainmaster.objectID = jdbc_tool_test_unitmaster.Code\n" +
				"    INNER JOIN gef_jdbc_tool.jdbc_tool_test_mainmaster jdbc_tool_test_mainmaster ON jdbc_tool_test_pk_uk_mainmaster.objectID = jdbc_tool_test_mainmaster.objectID\n" +
				"    RIGHT OUTER JOIN gef_jdbc_tool.jdbc_tool_test_ordermaster jdbc_tool_test_ordermaster ON jdbc_tool_test_pk_uk_mainmaster.ItemCD = jdbc_tool_test_ordermaster.objectID\n"+
				"WHERE\n"+
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
	public void testExport_searchExpWhereParts_JoinColumn() {

		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_searchExpWhereParts_JoinColumn.xml");
		ApiRequest apiRequest = parseRequestFromFile(getCommonSetting(),"jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_searchExpWhereparts_joincolumn.yaml");
		List<CodeResponse> result = convertVe2Code(apiRequest);

		assertEquals("SELECT", result.get(0).getType());
		assertEquals("仮想表ID: 0B064A3729EC4D65BB5A663562B96B51", result.get(0).getDescription());
		assertEquals("SELECT\n"+
				"    jdbc_tool_test_pk_uk_mainmaster.objectID,\n"+
				"    jdbc_tool_test_pk_uk_mainmaster.ItemCD,\n"+
				"    jdbc_tool_test_pk_uk_mainmaster.ItemNA,\n"+
				"    jdbc_tool_test_pk_uk_mainmaster.UnitPrice,\n"+
				"    jdbc_tool_test_pk_uk_mainmaster.SellPrice,\n"+
				"    jdbc_tool_test_pk_uk_mainmaster.Flag,\n"+
				"    jdbc_tool_test_pk_uk_mainmaster.PriceFlag,\n"+
				"    jdbc_tool_test_pk_uk_mainmaster.ExclusiveFG,\n"+
				"    jdbc_tool_test_pk_uk_mainmaster.RegisteredDT,\n"+
				"    jdbc_tool_test_pk_uk_mainmaster.UpdatedDT,\n"+
				"    jdbc_tool_test_mainmaster.objectID,\n"+
				"    jdbc_tool_test_unitmaster.Code,\n"+
				"    jdbc_tool_test_ordermaster.SlipNO,\n"+
				"    jdbc_tool_test_mainmaster.SellPrice\n"+
				"FROM\n"+
				"    " + dbSchema + ".jdbc_tool_test_pk_uk_mainmaster jdbc_tool_test_pk_uk_mainmaster\n"+
				"    LEFT OUTER JOIN gef_jdbc_tool.jdbc_tool_test_unitmaster jdbc_tool_test_unitmaster ON jdbc_tool_test_pk_uk_mainmaster.objectID = jdbc_tool_test_unitmaster.Code\n" +
				"    INNER JOIN gef_jdbc_tool.jdbc_tool_test_mainmaster jdbc_tool_test_mainmaster ON jdbc_tool_test_pk_uk_mainmaster.objectID = jdbc_tool_test_mainmaster.objectID\n" +
				"    RIGHT OUTER JOIN gef_jdbc_tool.jdbc_tool_test_ordermaster jdbc_tool_test_ordermaster ON jdbc_tool_test_pk_uk_mainmaster.ItemCD = jdbc_tool_test_ordermaster.objectID\n"+
				"WHERE\n"+
				"    (\n"+
				"        jdbc_tool_test_pk_uk_mainmaster.objectID = ?\n"+
				"        AND jdbc_tool_test_unitmaster.Code = ?\n"+
				"        AND jdbc_tool_test_mainmaster.objectID = ?\n"+
				"        AND jdbc_tool_test_ordermaster.SlipNO LIKE ? ESCAPE '/'\n"+
				"        AND jdbc_tool_test_pk_uk_mainmaster.ItemCD LIKE ? ESCAPE '/'\n"+
				"        AND jdbc_tool_test_pk_uk_mainmaster.ItemNA LIKE ? ESCAPE '/'\n"+
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
	public void testExport_searchExpWhereParts_JoinColumn_optionTrim() {

		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_searchExpWhereParts_JoinColumn_optionTrim.xml");
		ApiRequest apiRequest = parseRequestFromFile(getCommonSetting(),"jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_searchExpWhereparts_joincolumn_optionTrim.yaml");
		List<CodeResponse> result = convertVe2Code(apiRequest);

		assertEquals("SELECT", result.get(0).getType());
		assertEquals("仮想表ID: 940E79601B9143D687A8500F9915F7D8", result.get(0).getDescription());
		assertEquals("SELECT\n"+
				"    jdbc_tool_test_pk_uk_mainmaster.objectID,\n"+
				"    jdbc_tool_test_pk_uk_mainmaster.ItemCD,\n"+
				"    jdbc_tool_test_pk_uk_mainmaster.ItemNA,\n"+
				"    jdbc_tool_test_pk_uk_mainmaster.UnitPrice,\n"+
				"    jdbc_tool_test_pk_uk_mainmaster.SellPrice,\n"+
				"    jdbc_tool_test_pk_uk_mainmaster.Flag,\n"+
				"    jdbc_tool_test_pk_uk_mainmaster.PriceFlag,\n"+
				"    jdbc_tool_test_pk_uk_mainmaster.ExclusiveFG,\n"+
				"    jdbc_tool_test_pk_uk_mainmaster.RegisteredDT,\n"+
				"    jdbc_tool_test_pk_uk_mainmaster.UpdatedDT,\n"+
				"    jdbc_tool_test_mainmaster.objectID,\n"+
				"    jdbc_tool_test_unitmaster.Code,\n"+
				"    jdbc_tool_test_ordermaster.SlipNO,\n"+
				"    jdbc_tool_test_mainmaster.SellPrice\n"+
				"FROM\n"+
				"    " + dbSchema + ".jdbc_tool_test_pk_uk_mainmaster jdbc_tool_test_pk_uk_mainmaster\n"+
				"    LEFT OUTER JOIN gef_jdbc_tool.jdbc_tool_test_unitmaster jdbc_tool_test_unitmaster ON jdbc_tool_test_pk_uk_mainmaster.objectID = jdbc_tool_test_unitmaster.Code\n" +
				"    INNER JOIN gef_jdbc_tool.jdbc_tool_test_mainmaster jdbc_tool_test_mainmaster ON jdbc_tool_test_pk_uk_mainmaster.objectID = jdbc_tool_test_mainmaster.objectID\n" +
				"    RIGHT OUTER JOIN gef_jdbc_tool.jdbc_tool_test_ordermaster jdbc_tool_test_ordermaster ON jdbc_tool_test_pk_uk_mainmaster.ItemCD = jdbc_tool_test_ordermaster.objectID\n"+
				"WHERE\n"+
				"    (\n"+
				"        jdbc_tool_test_pk_uk_mainmaster.objectID = ?\n"+
				"        AND jdbc_tool_test_unitmaster.Code = ?\n"+
				"        AND jdbc_tool_test_mainmaster.objectID = ?\n"+
				"        AND jdbc_tool_test_ordermaster.SlipNO LIKE ? ESCAPE '/'\n"+
				"        AND jdbc_tool_test_pk_uk_mainmaster.ItemCD LIKE ? ESCAPE '/'\n"+
				"        AND jdbc_tool_test_pk_uk_mainmaster.ItemNA LIKE ? ESCAPE '/'\n"+
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
		assertEquals("SELECT\n"+
				"    jdbc_tool_test_pk_uk_mainmaster.objectID,\n"+
				"    jdbc_tool_test_pk_uk_mainmaster.ItemCD,\n"+
				"    jdbc_tool_test_pk_uk_mainmaster.ItemNA,\n"+
				"    jdbc_tool_test_pk_uk_mainmaster.UnitPrice,\n"+
				"    jdbc_tool_test_pk_uk_mainmaster.SellPrice,\n"+
				"    jdbc_tool_test_pk_uk_mainmaster.Flag,\n"+
				"    jdbc_tool_test_pk_uk_mainmaster.PriceFlag,\n"+
				"    jdbc_tool_test_pk_uk_mainmaster.ExclusiveFG,\n"+
				"    jdbc_tool_test_pk_uk_mainmaster.RegisteredDT,\n"+
				"    jdbc_tool_test_pk_uk_mainmaster.UpdatedDT,\n"+
				"    jdbc_tool_test_mainmaster.objectID,\n"+
				"    jdbc_tool_test_unitmaster.Code,\n"+
				"    jdbc_tool_test_ordermaster.SlipNO,\n"+
				"    jdbc_tool_test_mainmaster.SellPrice\n"+
				"FROM\n"+
				"    " + dbSchema + ".jdbc_tool_test_pk_uk_mainmaster jdbc_tool_test_pk_uk_mainmaster\n"+
				"    LEFT OUTER JOIN gef_jdbc_tool.jdbc_tool_test_unitmaster jdbc_tool_test_unitmaster ON jdbc_tool_test_pk_uk_mainmaster.objectID = jdbc_tool_test_unitmaster.Code\n" +
				"    INNER JOIN gef_jdbc_tool.jdbc_tool_test_mainmaster jdbc_tool_test_mainmaster ON jdbc_tool_test_pk_uk_mainmaster.objectID = jdbc_tool_test_mainmaster.objectID\n" +
				"    RIGHT OUTER JOIN gef_jdbc_tool.jdbc_tool_test_ordermaster jdbc_tool_test_ordermaster ON jdbc_tool_test_pk_uk_mainmaster.ItemCD = jdbc_tool_test_ordermaster.objectID\n"+
				"WHERE\n"+
				"    (\n"+
				"        jdbc_tool_test_pk_uk_mainmaster.objectID = '1001'\n"+
				"        AND jdbc_tool_test_unitmaster.Code = '1001'\n"+
				"        AND jdbc_tool_test_mainmaster.objectID = '1001'\n"+
				"        AND jdbc_tool_test_ordermaster.SlipNO LIKE '100%'\n"+
				"        AND jdbc_tool_test_pk_uk_mainmaster.ItemCD LIKE '%100%'\n"+
				"        AND jdbc_tool_test_pk_uk_mainmaster.ItemNA LIKE '%gef%'\n"+
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

	@Test
	public void testExport_filterExpWhereParts_JoinColumn() {
		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_filterExpWhereParts_JoinColumn.xml");
		ApiRequest apiRequest = parseRequestFromFile(getCommonSetting(),"jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_filterExpWhereparts_joincolumn.yaml");
		List<CodeResponse> result = convertVe2Code(apiRequest);

		assertEquals("SELECT", result.get(0).getType());
		assertEquals("仮想表ID: 395DA3D2A7C541FDAFF20CC7AF7BBFE8", result.get(0).getDescription());
		assertEquals("SELECT\n"+
				"    jdbc_tool_test_pk_uk_mainmaster.objectID,\n"+
				"    jdbc_tool_test_pk_uk_mainmaster.ItemCD,\n"+
				"    jdbc_tool_test_pk_uk_mainmaster.ItemNA,\n"+
				"    jdbc_tool_test_pk_uk_mainmaster.UnitPrice,\n"+
				"    jdbc_tool_test_pk_uk_mainmaster.SellPrice,\n"+
				"    jdbc_tool_test_pk_uk_mainmaster.Flag,\n"+
				"    jdbc_tool_test_pk_uk_mainmaster.PriceFlag,\n"+
				"    jdbc_tool_test_pk_uk_mainmaster.ExclusiveFG,\n"+
				"    jdbc_tool_test_pk_uk_mainmaster.RegisteredDT,\n"+
				"    jdbc_tool_test_pk_uk_mainmaster.UpdatedDT,\n"+
				"    jdbc_tool_test_mainmaster.objectID,\n"+
				"    jdbc_tool_test_unitmaster.Code,\n"+
				"    jdbc_tool_test_ordermaster.SlipNO,\n"+
				"    jdbc_tool_test_mainmaster.SellPrice\n"+
				"FROM\n"+
				"    " + dbSchema + ".jdbc_tool_test_pk_uk_mainmaster jdbc_tool_test_pk_uk_mainmaster\n"+
				"    LEFT OUTER JOIN gef_jdbc_tool.jdbc_tool_test_unitmaster jdbc_tool_test_unitmaster ON jdbc_tool_test_pk_uk_mainmaster.objectID = jdbc_tool_test_unitmaster.Code\n" +
				"    INNER JOIN gef_jdbc_tool.jdbc_tool_test_mainmaster jdbc_tool_test_mainmaster ON jdbc_tool_test_pk_uk_mainmaster.objectID = jdbc_tool_test_mainmaster.objectID\n" +
				"    RIGHT OUTER JOIN gef_jdbc_tool.jdbc_tool_test_ordermaster jdbc_tool_test_ordermaster ON jdbc_tool_test_pk_uk_mainmaster.ItemCD = jdbc_tool_test_ordermaster.objectID\n"+
				"WHERE\n"+
				"    (\n"+
				"        jdbc_tool_test_pk_uk_mainmaster.objectID = '1001'\n"+
				"        AND jdbc_tool_test_unitmaster.Code = '1001'\n"+
				"        AND jdbc_tool_test_mainmaster.objectID = '1001'\n"+
				"        AND jdbc_tool_test_ordermaster.SlipNO LIKE '100%'\n"+
				"        AND jdbc_tool_test_pk_uk_mainmaster.ItemCD LIKE '%100%'\n"+
				"        AND jdbc_tool_test_pk_uk_mainmaster.ItemNA LIKE '%gef%'\n"+
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
	public void testExport_columnAndConstValueMixFK() {

		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_columnAndConstValueMixFK.xml");
		ApiRequest apiRequest = parseRequestFromFile(getCommonSetting(),"jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_columnAndConstValueMixFK_inq.yaml");
		List<CodeResponse> result = convertVe2Code(apiRequest);

		assertEquals("SELECT", result.get(0).getType());
		assertEquals("仮想表ID: AF3EB4CFAFD649868AB42E624707EBD8", result.get(0).getDescription());
		assertEquals("SELECT\n"+
				"    jdcb_tool_test_allColumntype.objectID,\n"+
				"    jdcb_tool_test_allColumntype.StringColumn,\n"+
				"    jdcb_tool_test_allColumntype.IntColumn,\n"+
				"    jdcb_tool_test_allColumntype.NStringColumn,\n"+
				"    jdcb_tool_test_allColumntype.DateTimeColumn,\n"+
				"    jdcb_tool_test_allColumntype.YMColumn,\n"+
				"    jdcb_tool_test_allColumntype.CurrencyColumn,\n"+
				"    jdcb_tool_test_allColumntype.LongColumn,\n"+
				"    jdcb_tool_test_allColumntype.CompanyCD,\n"+
				"    jdcb_tool_test_allColumntype.ExclusiveFG,\n"+
				"    jdcb_tool_test_allColumntype.RegisteredPerson,\n"+
				"    jdcb_tool_test_allColumntype.RegisteredDT,\n"+
				"    jdcb_tool_test_allColumntype.UpdatedPerson,\n"+
				"    jdcb_tool_test_allColumntype.UpdatedDT\n"+
				"FROM\n"+
				"    " + dbSchema + ".jdcb_tool_test_allColumntype jdcb_tool_test_allColumntype\n"+
				"    INNER JOIN " + dbSchema + ".jdbc_tool_test_pk_uk_mainmaster jdbc_tool_test_pk_uk_mainmaster ON jdcb_tool_test_allColumntype.objectID = jdbc_tool_test_pk_uk_mainmaster.objectID\n"+
				"    AND jdbc_tool_test_pk_uk_mainmaster.Flag = 1001\n"+
				"WHERE\n"+
				"    jdcb_tool_test_allColumntype.CompanyCD = ?", result.get(0).getSqlScript());

		String whereParams = "Map<String, Object> whereParams = new HashMap<>();\n"+
				"whereParams.put(\"jdcb_tool_test_allColumntype.CompanyCD\",);\n";
		String joinColumnFilterExpApiStr = "select(\"jdcb_tool_test_allColumntype\", \"jdcb_tool_test_allColumntype\")\n"+
				".fields(colsAll())\n"+
				".innerJoin(\"jdbc_tool_test_pk_uk_mainmaster\", \"jdbc_tool_test_pk_uk_mainmaster\", expMap($(\"jdcb_tool_test_allColumntype.objectID = jdbc_tool_test_pk_uk_mainmaster.objectID\").AND(\"jdbc_tool_test_pk_uk_mainmaster.Flag = 1001\"), Collections.emptyMap()))\n"+
				".where(expMap($(\"jdcb_tool_test_allColumntype.CompanyCD = $TRIM(:jdcb_tool_test_allColumntype.CompanyCD)\"), whereParams))";

		assertEquals(whereParams + "List<GRecord> result = " + joinColumnFilterExpApiStr + "\n.findList();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE).toString()));
		assertEquals(whereParams + "GRecord result = " + joinColumnFilterExpApiStr + "\n.findRecord();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE).toString()));
		assertEquals(whereParams + "GRecordSet result = " + joinColumnFilterExpApiStr + "\n.findRecordSet();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE).toString()));
	}

	@Test
	public void testExport_onlyConstValueFK() {

		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_onlyConstValueFK.xml");
		ApiRequest apiRequest = parseRequestFromFile(getCommonSetting(),"jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_onlyConstValueFK_inq.yaml");
		List<CodeResponse> result = convertVe2Code(apiRequest);

		assertEquals("SELECT", result.get(0).getType());
		assertEquals("仮想表ID: FABF321D11FD48129DBD9D148F66CEAA", result.get(0).getDescription());
		assertEquals("SELECT\n"+
				"    jdcb_tool_test_allColumntype.objectID,\n"+
				"    jdcb_tool_test_allColumntype.StringColumn,\n"+
				"    jdcb_tool_test_allColumntype.IntColumn,\n"+
				"    jdcb_tool_test_allColumntype.NStringColumn,\n"+
				"    jdcb_tool_test_allColumntype.DateTimeColumn,\n"+
				"    jdcb_tool_test_allColumntype.YMColumn,\n"+
				"    jdcb_tool_test_allColumntype.CurrencyColumn,\n"+
				"    jdcb_tool_test_allColumntype.LongColumn,\n"+
				"    jdcb_tool_test_allColumntype.CompanyCD,\n"+
				"    jdcb_tool_test_allColumntype.ExclusiveFG,\n"+
				"    jdcb_tool_test_allColumntype.RegisteredPerson,\n"+
				"    jdcb_tool_test_allColumntype.RegisteredDT,\n"+
				"    jdcb_tool_test_allColumntype.UpdatedPerson,\n"+
				"    jdcb_tool_test_allColumntype.UpdatedDT\n"+
				"FROM\n"+
				"    " + dbSchema + ".jdcb_tool_test_allColumntype jdcb_tool_test_allColumntype\n"+
				"    INNER JOIN " + dbSchema + ".jdbc_tool_test_pk_uk_mainmaster jdbc_tool_test_pk_uk_mainmaster ON jdbc_tool_test_pk_uk_mainmaster.objectID = '1001'\n"+
				"    AND jdbc_tool_test_pk_uk_mainmaster.ItemCD = '9001'\n"+
				"WHERE\n"+
				"    jdcb_tool_test_allColumntype.CompanyCD = ?", result.get(0).getSqlScript());

		String whereParams = "Map<String, Object> whereParams = new HashMap<>();\n"+
				"whereParams.put(\"jdcb_tool_test_allColumntype.CompanyCD\",);\n";
		String joinColumnFilterExpApiStr = "select(\"jdcb_tool_test_allColumntype\", \"jdcb_tool_test_allColumntype\")\n"+
				".fields(colsAll())\n"+  ".innerJoin(\"jdbc_tool_test_pk_uk_mainmaster\", \"jdbc_tool_test_pk_uk_mainmaster\", expMap($(\"jdbc_tool_test_pk_uk_mainmaster.objectID = '1001'\").AND(\"jdbc_tool_test_pk_uk_mainmaster.ItemCD = '9001'\"), Collections.emptyMap()))\n"+
				".where(expMap($(\"jdcb_tool_test_allColumntype.CompanyCD = $TRIM(:jdcb_tool_test_allColumntype.CompanyCD)\"), whereParams))";

		assertEquals(whereParams + "List<GRecord> result = " + joinColumnFilterExpApiStr + "\n.findList();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE).toString()));
		assertEquals(whereParams + "GRecord result = " + joinColumnFilterExpApiStr + "\n.findRecord();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE).toString()));
		assertEquals(whereParams + "GRecordSet result = " + joinColumnFilterExpApiStr + "\n.findRecordSet();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE).toString()));
	}

	protected abstract String getTimestampCommand();

	@Test
	public void testExport_insert() {

		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_insert.xml");
		ApiRequest apiRequest = parseRequestFromFile(getCommonSetting(),"jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_insert.yaml");
		List<CodeResponse> result = convertVe2Code(apiRequest);

		assertEquals("INSERT", result.get(0).getType());
		assertEquals("仮想表ID: 1FEFE156727646DC9122045339BD3999", result.get(0).getDescription());
		assertEquals("INSERT INTO\n"+
				"    " + dbSchema + ".jdbc_tool_test_pk_uk_mainmaster(objectID, ItemCD, ItemNA, UnitPrice, SellPrice, Flag, PriceFlag, ExclusiveFG, RegisteredDT, UpdatedDT)\n"+
				"VALUES\n"+
				"    " + "(?, ?, ?, ?, ?, ?, ?, ?, " + getTimestampCommand() + ", " + getTimestampCommand() + ")", result.get(0).getSqlScript());
		String recordStr = "GRecord record = createRecord();\n"+
				"record.setObject(\"objectID\",);\n"+
				"record.setObject(\"ItemCD\",);\n"+
				"record.setObject(\"ItemNA\",);\n"+
				"record.setObject(\"UnitPrice\",);\n"+
				"record.setObject(\"SellPrice\",);\n"+
				"record.setObject(\"Flag\",);\n"+
				"record.setObject(\"PriceFlag\",);\n"+
				"record.setObject(\"ExclusiveFG\",);\n"+
				"record.setObject(\"RegisteredDT\",);\n"+
				"record.setObject(\"UpdatedDT\",);\n";
		String executeApiStr = "insert(\"jdbc_tool_test_pk_uk_mainmaster\")\n"+
				".values(record)";
		assertEquals(recordStr + "\nint count = " + executeApiStr + "\n.execute();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, null).toString()));

		StringBuilder recordsStr = new StringBuilder();
		recordsStr.append("List<GRecord> recordList = new ArrayList<>();").append("\n");
		recordsStr.append(recordStr);
		recordsStr.append("recordList.add(record);").append("\n");
		String executeListApiStr = "insert(\"jdbc_tool_test_pk_uk_mainmaster\")\n"+
				".values(recordList)";
		assertEquals(recordsStr + "\nint count = " + executeListApiStr + "\n.executeBatch();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, null).toString()));
		assertEquals(recordsStr + "\nint count = " + executeListApiStr + "\n.executeList();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, null).toString()));

	}

	@Test
	public void testExport_whereParts_SameColumn() {

		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_whereParts_sameColumn.xml");
		ApiRequest apiRequest = parseRequestFromFile(getCommonSetting(),"jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_whereParts_sameColumn.yaml");
		List<CodeResponse> result = convertVe2Code(apiRequest);

		assertEquals("SELECT", result.get(0).getType());
		assertEquals("仮想表ID: FBF32CF5550E402096711A5F1B4012B1", result.get(0).getDescription());
		assertEquals("SELECT\n"+
				"    jdcb_tool_test_allColumntype.objectID,\n"+
				"    jdcb_tool_test_allColumntype.StringColumn,\n"+
				"    jdcb_tool_test_allColumntype.IntColumn,\n"+
				"    jdcb_tool_test_allColumntype.NStringColumn,\n"+
				"    jdcb_tool_test_allColumntype.DateTimeColumn,\n"+
				"    jdcb_tool_test_allColumntype.YMColumn,\n"+
				"    jdcb_tool_test_allColumntype.CurrencyColumn,\n"+
				"    jdcb_tool_test_allColumntype.LongColumn,\n"+
				"    jdcb_tool_test_allColumntype.CompanyCD,\n"+
				"    jdcb_tool_test_allColumntype.ExclusiveFG,\n"+
				"    jdcb_tool_test_allColumntype.RegisteredPerson,\n"+
				"    jdcb_tool_test_allColumntype.RegisteredDT,\n"+
				"    jdcb_tool_test_allColumntype.UpdatedPerson,\n"+
				"    jdcb_tool_test_allColumntype.UpdatedDT\n"+
				"FROM\n"+
				"    " + dbSchema + ".jdcb_tool_test_allColumntype jdcb_tool_test_allColumntype\n"+
				"WHERE\n"+
				"    (\n"+
				"        jdcb_tool_test_allColumntype.objectID = '\"test1\"'\n"+
				"        OR jdcb_tool_test_allColumntype.objectID = '\"test2\"'\n"+
				"    )\n"+
				"    AND (\n"+
				"        jdcb_tool_test_allColumntype.objectID = ?\n"+
				"        OR jdcb_tool_test_allColumntype.objectID = ?\n"+
				"        AND jdcb_tool_test_allColumntype.CompanyCD = ?\n"+
				"    )", result.get(0).getSqlScript());

		String whereParams = "Map<String, Object> whereParams = new HashMap<>();\n"+
				"whereParams.put(\"jdcb_tool_test_allColumntype.CompanyCD\",);\n"+
				"whereParams.put(\"jdcb_tool_test_allColumntype.objectID\",);\n";
		String joinColumnFilterExpApiStr = "select(\"jdcb_tool_test_allColumntype\", \"jdcb_tool_test_allColumntype\")\n"+
				".fields(colsAll())\n"+
				".where(expMap($($(\"jdcb_tool_test_allColumntype.objectID = '\"test1\"'\").OR(\"jdcb_tool_test_allColumntype.objectID = '\"test2\"'\")).AND($(\"jdcb_tool_test_allColumntype.objectID = [:jdcb_tool_test_allColumntype.objectID]\").OR(\"jdcb_tool_test_allColumntype.objectID = $TRIM([:jdcb_tool_test_allColumntype.objectID])\").AND(\"jdcb_tool_test_allColumntype.CompanyCD = $TRIM(:jdcb_tool_test_allColumntype.CompanyCD)\")), whereParams))";

		assertEquals(whereParams + "List<GRecord> result = " + joinColumnFilterExpApiStr + "\n.findList();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE).toString()));
		assertEquals(whereParams + "GRecord result = " + joinColumnFilterExpApiStr + "\n.findRecord();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE).toString()));
		assertEquals(whereParams + "GRecordSet result = " + joinColumnFilterExpApiStr + "\n.findRecordSet();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE).toString()));
	}

//	@Test
//	public void testExport_categoryId() {
//
//		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_categoryId.xml");
//		CodeRequest codeRequest = parseRequestFromFile(getCommonSetting(),"jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_category.yaml");
//		List<CodeResponse> result = convertVe2Code(codeRequest);
//		assertEquals(8, result.size());
//		assertEquals("SELECT", result.get(0).getType());
//		assertEquals(new String("仮想表ID: F08F656C201141ADBD1B743F144A44A0 (カテゴリID: F56264D123E6450DB2AF788A7039CA41)".getBytes(StandardCharsets.UTF_8)), result.get(0).getDescription());
//		assertEquals("DELETE", result.get(1).getType());
//		assertEquals(new String("仮想表ID: F08F656C201141ADBD1B743F144A44A0 (カテゴリID: F56264D123E6450DB2AF788A7039CA41)".getBytes(StandardCharsets.UTF_8)), result.get(1).getDescription());
//		assertEquals("INSERT", result.get(2).getType());
//		assertEquals(new String("仮想表ID: BC37EC6B0EFF411980A0385FFB38D035 (カテゴリID: F56264D123E6450DB2AF788A7039CA41)".getBytes(StandardCharsets.UTF_8)), result.get(2).getDescription());
//		assertEquals("UPDATE", result.get(3).getType());
//		assertEquals(new String("仮想表ID: BC37EC6B0EFF411980A0385FFB38D035 (カテゴリID: F56264D123E6450DB2AF788A7039CA41)".getBytes(StandardCharsets.UTF_8)), result.get(3).getDescription());
//		assertEquals("SELECT", result.get(4).getType());
//		assertEquals(new String("仮想表ID: BC37EC6B0EFF411980A0385FFB38D035 (カテゴリID: F56264D123E6450DB2AF788A7039CA41)".getBytes(StandardCharsets.UTF_8)), result.get(4).getDescription());
//		assertEquals("DELETE", result.get(5).getType());
//		assertEquals(new String("仮想表ID: BC37EC6B0EFF411980A0385FFB38D035 (カテゴリID: F56264D123E6450DB2AF788A7039CA41)".getBytes(StandardCharsets.UTF_8)), result.get(5).getDescription());
//		assertEquals("SELECT", result.get(6).getType());
//		assertEquals(new String("仮想表ID: A4F67FC9EE6249AF9F5DE398C9231974 (カテゴリID: B27393A2779C472C88EEF38FABCFE118)".getBytes(StandardCharsets.UTF_8)), result.get(6).getDescription());
//		assertEquals("DELETE", result.get(7).getType());
//		assertEquals(new String("仮想表ID: A4F67FC9EE6249AF9F5DE398C9231974 (カテゴリID: B27393A2779C472C88EEF38FABCFE118)".getBytes(StandardCharsets.UTF_8)), result.get(7).getDescription());
//	}

//	@Test
//	public void testExportAll() {
//		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_exportall.xml");
//		GExporterResult<List<GXFormCodeResult>, byte[]> result = service.export(null, settingsFilePath);
//		assertEquals("DELETE", result.get(0).getType());
//		assertEquals(new String("仮想表ID: F08F656C201141ADBD1B743F144A44A0 (カテゴリID: F56264D123E6450DB2AF788A7039CA41)".getBytes(StandardCharsets.UTF_8)), result.get(0).getDescription());
//		assertEquals("SELECT", result.get(1).getType());
//		assertEquals(new String("仮想表ID: F08F656C201141ADBD1B743F144A44A0 (カテゴリID: F56264D123E6450DB2AF788A7039CA41)".getBytes(StandardCharsets.UTF_8)), result.get(1).getDescription());
//		assertEquals("DELETE", result.get(2).getType());
//		assertEquals(new String("仮想表ID: BC37EC6B0EFF411980A0385FFB38D035 (カテゴリID: F56264D123E6450DB2AF788A7039CA41)".getBytes(StandardCharsets.UTF_8)), result.get(2).getDescription());
//		assertEquals("UPDATE", result.get(3).getType());
//		assertEquals(new String("仮想表ID: BC37EC6B0EFF411980A0385FFB38D035 (カテゴリID: F56264D123E6450DB2AF788A7039CA41)".getBytes(StandardCharsets.UTF_8)), result.get(3).getDescription());
//		assertEquals("INSERT", result.get(4).getType());
//		assertEquals(new String("仮想表ID: BC37EC6B0EFF411980A0385FFB38D035 (カテゴリID: F56264D123E6450DB2AF788A7039CA41)".getBytes(StandardCharsets.UTF_8)), result.get(4).getDescription());
//		assertEquals("SELECT", result.get(5).getType());
//		assertEquals(new String("仮想表ID: BC37EC6B0EFF411980A0385FFB38D035 (カテゴリID: F56264D123E6450DB2AF788A7039CA41)".getBytes(StandardCharsets.UTF_8)), result.get(5).getDescription());
//		assertEquals("DELETE", result.get(6).getType());
//		assertEquals(new String("仮想表ID: A4F67FC9EE6249AF9F5DE398C9231974 (カテゴリID: B27393A2779C472C88EEF38FABCFE118)".getBytes(StandardCharsets.UTF_8)), result.get(6).getDescription());
//		assertEquals("SELECT", result.get(7).getType());
//		assertEquals(new String("仮想表ID: A4F67FC9EE6249AF9F5DE398C9231974 (カテゴリID: B27393A2779C472C88EEF38FABCFE118)".getBytes(StandardCharsets.UTF_8)), result.get(7).getDescription());
//		assertEquals("DELETE", result.get(8).getType());
//		assertEquals(new String("仮想表ID: 1FEFE156727646DC9122045339BD3999 (カテゴリID: F0D5F2FA82D04F29909340A50C4976D7)".getBytes(StandardCharsets.UTF_8)), result.get(8).getDescription());
//		assertEquals("UPDATE", result.get(9).getType());
//		assertEquals(new String("仮想表ID: 1FEFE156727646DC9122045339BD3999 (カテゴリID: F0D5F2FA82D04F29909340A50C4976D7)".getBytes(StandardCharsets.UTF_8)), result.get(9).getDescription());
//		assertEquals("INSERT", result.get(10).getType());
//		assertEquals(new String("仮想表ID: 1FEFE156727646DC9122045339BD3999 (カテゴリID: F0D5F2FA82D04F29909340A50C4976D7)".getBytes(StandardCharsets.UTF_8)), result.get(10).getDescription());
//		assertEquals("SELECT", result.get(11).getType());
//		assertEquals(new String("仮想表ID: 1FEFE156727646DC9122045339BD3999 (カテゴリID: F0D5F2FA82D04F29909340A50C4976D7)".getBytes(StandardCharsets.UTF_8)), result.get(11).getDescription());
//	}

	@Test
	public void testExport_veType_inq() {

		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_vetype_inq.xml");
		ApiRequest apiRequest = parseRequestFromFile(getCommonSetting(),"jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_vetype_inq.yaml");
		List<CodeResponse> result = convertVe2Code(apiRequest);
assertEquals(2, result.size());
		assertEquals("SELECT", result.get(0).getType());
		assertEquals("DELETE", result.get(1).getType());
		assertEquals("仮想表ID: 4E52A89202B145159B8256C53A433A24", result.get(1).getDescription());
		assertEquals(getDeleteSql_veType_inq(), result.get(1).getSqlScript());
		String whereParamsParts = "Map<String, Object> whereParams = new HashMap<>();\n"+
				"whereParams.put(\"jdbc_tool_test_mainmaster.UnitPrice\",);\n"+
				"whereParams.put(\"jdbc_tool_test_mainmaster.ItemCD\",);";
		String deleteApiStr = "delete(\"jdbc_tool_test_mainmaster\")\n"+
				".where(expMap($($(\"jdbc_tool_test_mainmaster.ItemCD = $TRIM([:jdbc_tool_test_mainmaster.ItemCD])\").AND(\"jdbc_tool_test_mainmaster.UnitPrice <> $TRIM([:jdbc_tool_test_mainmaster.UnitPrice])\")), whereParams))";

		assertNull(result.get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE).toString()));
		assertEquals(whereParamsParts + "\nint count = " + deleteApiStr + "\n.execute();", result.get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE).toString()));
		assertNull(result.get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE).toString()));
	}

	public String getDeleteSql_veType_inq() {
		return "DELETE FROM\n"+
				"    " + dbSchema + ".jdbc_tool_test_mainmaster\n"+
				"WHERE\n"+
				"    (\n"+
				"        " + dbSchema + ".jdbc_tool_test_mainmaster.ItemCD = ?\n"+
				"        AND " + dbSchema + ".jdbc_tool_test_mainmaster.UnitPrice <> ?\n"+
				"    )";
	}

	@Test
	public void testExport_veType_reg() {

		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_vetype_reg.xml");
		ApiRequest apiRequest = parseRequestFromFile(getCommonSetting(),"jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_vetype_reg.yaml");
		List<CodeResponse> result = convertVe2Code(apiRequest);
		assertEquals(4, result.size());
		assertEquals("INSERT", result.get(0).getType());
		assertEquals("UPDATE", result.get(1).getType());
		assertEquals("DELETE", result.get(3).getType());
		assertEquals("SELECT", result.get(2).getType());
		assertEquals("仮想表ID: CC71DD95CF7244DFB80A009930DB3F95", result.get(2).getDescription());
		assertEquals("SELECT\n"+
				"    jdbc_tool_test_mainmaster.objectID,\n"+
				"    jdbc_tool_test_mainmaster.ItemCD,\n"+
				"    jdbc_tool_test_mainmaster.UnitPrice\n"+
				"FROM\n"+
				"    " + dbSchema + ".jdbc_tool_test_mainmaster jdbc_tool_test_mainmaster\n"+
				"WHERE\n"+
				"    (\n"+
				"        jdbc_tool_test_mainmaster.ItemCD = ?\n"+
				"        AND jdbc_tool_test_mainmaster.UnitPrice <> ?\n"+
				"    )", result.get(2).getSqlScript());
		String whereParamsParts = "Map<String, Object> whereParams = new HashMap<>();\n"+
				"whereParams.put(\"jdbc_tool_test_mainmaster.UnitPrice\",);\n"+
				"whereParams.put(\"jdbc_tool_test_mainmaster.ItemCD\",);\n";
		String selectApiStr = "select(\"jdbc_tool_test_mainmaster\", \"jdbc_tool_test_mainmaster\")\n"+
				".fields(col(\"jdbc_tool_test_mainmaster.objectID\")," +
				"col(\"jdbc_tool_test_mainmaster.ItemCD\")," +
				"col(\"jdbc_tool_test_mainmaster.UnitPrice\"))\n"+
				".where(expMap($($(\"jdbc_tool_test_mainmaster.ItemCD = $TRIM([:jdbc_tool_test_mainmaster.ItemCD])\").AND(\"jdbc_tool_test_mainmaster.UnitPrice <> $TRIM([:jdbc_tool_test_mainmaster.UnitPrice])\")), whereParams))";

		assertEquals(whereParamsParts + "List<GRecord> result = " + selectApiStr + "\n.findList();", result.get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE).toString()));
		assertEquals(whereParamsParts + "GRecord result = " + selectApiStr + "\n.findRecord();", result.get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE).toString()));
		assertEquals(whereParamsParts + "GRecordSet result = " + selectApiStr + "\n.findRecordSet();", result.get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE).toString()));
	}


	@Test
	public void testExport_emptyColumn_inq() {

		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_emptyColumn_inq.xml");
		ApiRequest apiRequest = parseRequestFromFile(getCommonSetting(),"jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_emptyColumn_inq.yaml");
		List<CodeResponse> result = convertVe2Code(apiRequest);
assertEquals(2, result.size());
		assertEquals("SELECT", result.get(0).getType());
		assertEquals("仮想表ID: 4351175AD9054354B65FAC8422520F17", result.get(0).getDescription());
		assertEquals("SELECT\n"+
				"    jdbc_tool_test_emptyColumn.objectID,\n"+
				"    jdbc_tool_test_emptyColumn.StringColumn\n"+
				"FROM\n"+
				"    " + dbSchema + ".jdbc_tool_test_emptyColumn jdbc_tool_test_emptyColumn", result.get(0).getSqlScript());

		Map<String, Object> selectCodeResult = (result.get(0)).getVirtualEntity();
		assertEquals(4, ((List<Map<String,Object>>)selectCodeResult.get("columns")).size());
		assertEquals("Map<String, Object> absoluteVirtualColumn0 = new HashMap<>();\n"+
				"absoluteVirtualColumn0.put(\"name\", \"virtualColumn01\");\n"+
				"absoluteVirtualColumn0.put(\"displayName\", \"virtualColumn01\");\n"+
				"absoluteVirtualColumn0.put(\"fixedValue\", \"-\");\n"+
				"absoluteVirtualColumn0.put(\"aggregateFunction\", \"-\");\n"+
				"absoluteVirtualColumn0.put(\"format\", \"-\");\n"+
				"Map<String, Object> absoluteVirtualColumn1 = new HashMap<>();\n"+
				"absoluteVirtualColumn1.put(\"name\", \"virtualColumn02\");\n"+
				"absoluteVirtualColumn1.put(\"displayName\", \"virtualColumn02\");\n"+
				"absoluteVirtualColumn1.put(\"fixedValue\", \"9002\");\n"+
				"absoluteVirtualColumn1.put(\"aggregateFunction\", \"MAX\");\n"+
				"absoluteVirtualColumn1.put(\"format\", \"000.000\");\n", selectCodeResult.get("absoluteVirtualColumnCode"));

		String selectApiStr = "select(\"jdbc_tool_test_emptyColumn\", \"jdbc_tool_test_emptyColumn\")\n"+
				".fields(col(\"jdbc_tool_test_emptyColumn.objectID\"),col(\"jdbc_tool_test_emptyColumn.StringColumn\"))";
		assertEquals("List<GRecord> result = " + selectApiStr + "\n.findList();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, null).toString()));
		assertNull(result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE).toString()));
		assertNull(result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE_PK).toString()));
		assertNull(result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE_UK).toString()));
		assertEquals("GRecord result = " + selectApiStr + "\n.findRecord();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, null).toString()));
		assertNull(result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE).toString()));
		assertNull(result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE_PK).toString()));
		assertNull(result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE_UK).toString()));
		assertEquals("GRecordSet result = " + selectApiStr + "\n.findRecordSet();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, null).toString()));
		assertNull( result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE).toString()));
		assertNull( result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE_PK).toString()));
		assertNull( result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE_UK).toString()));

		assertEquals("DELETE", result.get(1).getType());
		assertEquals("仮想表ID: 4351175AD9054354B65FAC8422520F17", result.get(1).getDescription());
		assertEquals(getDeleteSql_emptyColumn(), result.get(1).getSqlScript());

		Map<String, Object> deleteCodeResult = (result.get(1)).getVirtualEntity();
		assertEquals(4, ((List<Map<String,Object>>)deleteCodeResult.get("columns")).size());
		assertEquals("Map<String, Object> absoluteVirtualColumn0 = new HashMap<>();\n"+
				"absoluteVirtualColumn0.put(\"name\", \"virtualColumn01\");\n"+
				"absoluteVirtualColumn0.put(\"displayName\", \"virtualColumn01\");\n"+
				"absoluteVirtualColumn0.put(\"fixedValue\", \"-\");\n"+
				"absoluteVirtualColumn0.put(\"aggregateFunction\", \"-\");\n"+
				"absoluteVirtualColumn0.put(\"format\", \"-\");\n"+
				"Map<String, Object> absoluteVirtualColumn1 = new HashMap<>();\n"+
				"absoluteVirtualColumn1.put(\"name\", \"virtualColumn02\");\n"+
				"absoluteVirtualColumn1.put(\"displayName\", \"virtualColumn02\");\n"+
				"absoluteVirtualColumn1.put(\"fixedValue\", \"9002\");\n"+
				"absoluteVirtualColumn1.put(\"aggregateFunction\", \"MAX\");\n"+
				"absoluteVirtualColumn1.put(\"format\", \"000.000\");\n", selectCodeResult.get("absoluteVirtualColumnCode"));

		String deleteApiStr = "delete(\"jdbc_tool_test_emptyColumn\")";
		assertEquals("int count = " + deleteApiStr + "\n.execute();", result.get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, null).toString()));
		assertNull(result.get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE).toString()));
		assertNull(result.get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_PK).toString()));
		assertNull(result.get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_UK).toString()));
		assertNull(result.get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE).toString()));
		assertNull(result.get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_PK).toString()));
		assertNull(result.get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_UK).toString()));
		assertNull(result.get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE).toString()));
		assertNull(result.get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_PK).toString()));
		assertNull(result.get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_UK).toString()));
	}

	public String getDeleteSql_emptyColumn() {
		return "DELETE FROM\n"+
				"    " + dbSchema + ".jdbc_tool_test_emptyColumn";
	}


	@Test
	public void testExport_emptyColumn_reg() {

		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_emptyColumn_reg.xml");
		ApiRequest apiRequest = parseRequestFromFile(getCommonSetting(),"jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_emptyColumn_reg.yaml");
		List<CodeResponse> result = convertVe2Code(apiRequest);
assertEquals(4, result.size());
		assertEquals("INSERT", result.get(0).getType());
		assertEquals("仮想表ID: A42FC45B7B66401EB1A7AE619876BDE7", result.get(0).getDescription());
		assertEquals("INSERT INTO\n"+
				"    " + dbSchema + ".jdbc_tool_test_emptyColumn(objectID, StringColumn)\n"+
				"VALUES\n"+
				"    " + "(?, ?)", result.get(0).getSqlScript());

		Map<String, Object> insertCodeResult = (result.get(0)).getVirtualEntity();
		assertEquals(5, ((List<Map<String,Object>>)insertCodeResult.get("columns")).size());
		assertEquals("Map<String, Object> absoluteVirtualColumn0 = new HashMap<>();\n"+
				"absoluteVirtualColumn0.put(\"name\", \"virtualColumn01\");\n"+
				"absoluteVirtualColumn0.put(\"displayName\", \"virtualColumn01\");\n"+
				"absoluteVirtualColumn0.put(\"fixedValue\", \"-\");\n"+
				"absoluteVirtualColumn0.put(\"aggregateFunction\", \"-\");\n"+
				"absoluteVirtualColumn0.put(\"format\", \"-\");\n"+
				"Map<String, Object> absoluteVirtualColumn1 = new HashMap<>();\n"+
				"absoluteVirtualColumn1.put(\"name\", \"virtualColumn02\");\n"+
				"absoluteVirtualColumn1.put(\"displayName\", \"virtualColumn02\");\n"+
				"absoluteVirtualColumn1.put(\"fixedValue\", \"9002\");\n"+
				"absoluteVirtualColumn1.put(\"aggregateFunction\", \"MAX\");\n"+
				"absoluteVirtualColumn1.put(\"format\", \"000.000\");\n", insertCodeResult.get("absoluteVirtualColumnCode"));

		String insertExecuteApiStr = "insert(\"jdbc_tool_test_emptyColumn\")\n"+ ".values(record)";
		String insertRecord = "GRecord record = createRecord();\n"+
				"record.setObject(\"objectID\",);\n"+
				"record.setObject(\"StringColumn\",);\n";

		assertEquals(insertRecord + "\nint count = " + insertExecuteApiStr + "\n.execute();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, null).toString()));
		assertNull(result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE).toString()));
		assertNull(result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_PK).toString()));
		assertNull(result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_UK).toString()));

		String insertExecuteBatchApiStr = "insert(\"jdbc_tool_test_emptyColumn\")\n"+ ".values(recordList)";
		StringBuilder insertRecordList = new StringBuilder();
		insertRecordList.append("List<GRecord> recordList = new ArrayList<>();").append("\n");
		insertRecordList.append(insertRecord);
		insertRecordList.append("recordList.add(record);").append("\n");

		assertEquals(insertRecordList + "\nint count = " + insertExecuteBatchApiStr + "\n.executeBatch();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, null).toString()));
		assertNull(result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE).toString()));
		assertNull(result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_PK).toString()));
		assertNull(result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_UK).toString()));
		assertEquals(insertRecordList + "\nint count = " + insertExecuteBatchApiStr + "\n.executeList();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, null).toString()));
		assertNull(result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE).toString()));
		assertNull(result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_PK).toString()));
		assertNull(result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_UK).toString()));


		assertEquals("UPDATE", result.get(1).getType());
		assertEquals("仮想表ID: A42FC45B7B66401EB1A7AE619876BDE7", result.get(1).getDescription());
		assertEquals("UPDATE\n"+
				"    " + dbSchema + ".jdbc_tool_test_emptyColumn jdbc_tool_test_emptyColumn\n"+
				"SET\n"+
				"    StringColumn = ?", result.get(1).getSqlScript());

		Map<String, Object> updateCodeResult = (result.get(1)).getVirtualEntity();
		assertEquals(5, ((List<Map<String,Object>>)updateCodeResult.get("columns")).size());
		assertEquals("Map<String, Object> absoluteVirtualColumn0 = new HashMap<>();\n"+
				"absoluteVirtualColumn0.put(\"name\", \"virtualColumn01\");\n"+
				"absoluteVirtualColumn0.put(\"displayName\", \"virtualColumn01\");\n"+
				"absoluteVirtualColumn0.put(\"fixedValue\", \"-\");\n"+
				"absoluteVirtualColumn0.put(\"aggregateFunction\", \"-\");\n"+
				"absoluteVirtualColumn0.put(\"format\", \"-\");\n"+
				"Map<String, Object> absoluteVirtualColumn1 = new HashMap<>();\n"+
				"absoluteVirtualColumn1.put(\"name\", \"virtualColumn02\");\n"+
				"absoluteVirtualColumn1.put(\"displayName\", \"virtualColumn02\");\n"+
				"absoluteVirtualColumn1.put(\"fixedValue\", \"9002\");\n"+
				"absoluteVirtualColumn1.put(\"aggregateFunction\", \"MAX\");\n"+
				"absoluteVirtualColumn1.put(\"format\", \"000.000\");\n", updateCodeResult.get("absoluteVirtualColumnCode"));

		String updateRecord = "GRecord record = createRecord();\n"+
				"record.setObject(\"StringColumn\",);\n";

		StringBuilder updateRecordList = new StringBuilder();
		updateRecordList.append("List<GRecord> recordList = new ArrayList<>();").append("\n");
		updateRecordList.append(updateRecord);
		updateRecordList.append("recordList.add(record);").append("\n");

		String updateExecuteApiStr = "update(\"jdbc_tool_test_emptyColumn\")\n"+ ".set(record)";
		assertEquals(updateRecord + "\nint count = " + updateExecuteApiStr + "\n.execute();", result.get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, null).toString()));
		assertNull(result.get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE).toString()));
		assertNull(result.get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_PK).toString()));
		assertNull(result.get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_UK).toString()));
		assertNull(result.get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE).toString()));
		assertNull(result.get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_PK).toString()));
		assertNull(result.get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_UK).toString()));
		assertNull(result.get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE).toString()));
		assertNull(result.get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_PK).toString()));
		assertNull(result.get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_UK).toString()));

		assertEquals("SELECT", result.get(2).getType());
		assertEquals("仮想表ID: A42FC45B7B66401EB1A7AE619876BDE7", result.get(2).getDescription());
		assertEquals("SELECT\n"+
				"    jdbc_tool_test_emptyColumn.objectID,\n"+
				"    jdbc_tool_test_emptyColumn.StringColumn,\n"+
				"    jdbc_tool_test_emptyColumn.IntColumn\n"+
				"FROM\n"+
				"    " + dbSchema + ".jdbc_tool_test_emptyColumn jdbc_tool_test_emptyColumn", result.get(2).getSqlScript());

		Map<String, Object> selectCodeResult = (result.get(2)).getVirtualEntity();
		assertEquals(5, ((List<Map<String,Object>>)selectCodeResult.get("columns")).size());
		assertEquals("Map<String, Object> absoluteVirtualColumn0 = new HashMap<>();\n"+
				"absoluteVirtualColumn0.put(\"name\", \"virtualColumn01\");\n"+
				"absoluteVirtualColumn0.put(\"displayName\", \"virtualColumn01\");\n"+
				"absoluteVirtualColumn0.put(\"fixedValue\", \"-\");\n"+
				"absoluteVirtualColumn0.put(\"aggregateFunction\", \"-\");\n"+
				"absoluteVirtualColumn0.put(\"format\", \"-\");\n"+
				"Map<String, Object> absoluteVirtualColumn1 = new HashMap<>();\n"+
				"absoluteVirtualColumn1.put(\"name\", \"virtualColumn02\");\n"+
				"absoluteVirtualColumn1.put(\"displayName\", \"virtualColumn02\");\n"+
				"absoluteVirtualColumn1.put(\"fixedValue\", \"9002\");\n"+
				"absoluteVirtualColumn1.put(\"aggregateFunction\", \"MAX\");\n"+
				"absoluteVirtualColumn1.put(\"format\", \"000.000\");\n", selectCodeResult.get("absoluteVirtualColumnCode"));

		String selectApiStr = "select(\"jdbc_tool_test_emptyColumn\", \"jdbc_tool_test_emptyColumn\")\n"+
				".fields(col(\"jdbc_tool_test_emptyColumn.objectID\"),col(\"jdbc_tool_test_emptyColumn.StringColumn\"),col(\"jdbc_tool_test_emptyColumn.IntColumn\"))";
		assertEquals("List<GRecord> result = " + selectApiStr + "\n.findList();", result.get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, null).toString()));
		assertNull(result.get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE).toString()));
		assertNull(result.get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE_PK).toString()));
		assertNull(result.get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE_UK).toString()));
		assertEquals("GRecord result = " + selectApiStr + "\n.findRecord();", result.get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, null).toString()));
		assertNull(result.get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE).toString()));
		assertNull(result.get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE_PK).toString()));
		assertNull(result.get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE_UK).toString()));
		assertEquals("GRecordSet result = " + selectApiStr + "\n.findRecordSet();", result.get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, null).toString()));
		assertNull(result.get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE).toString()));
		assertNull(result.get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE_PK).toString()));
		assertNull(result.get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE_UK).toString()));

		assertEquals("DELETE", result.get(3).getType());
		assertEquals("仮想表ID: A42FC45B7B66401EB1A7AE619876BDE7", result.get(3).getDescription());
		assertEquals(getDeleteSql_emptyColumn(), result.get(3).getSqlScript());

		Map<String, Object> deleteCodeResult = (result.get(3)).getVirtualEntity();
		assertEquals(5, ((List<Map<String,Object>>)deleteCodeResult.get("columns")).size());
		assertEquals("Map<String, Object> absoluteVirtualColumn0 = new HashMap<>();\n"+
				"absoluteVirtualColumn0.put(\"name\", \"virtualColumn01\");\n"+
				"absoluteVirtualColumn0.put(\"displayName\", \"virtualColumn01\");\n"+
				"absoluteVirtualColumn0.put(\"fixedValue\", \"-\");\n"+
				"absoluteVirtualColumn0.put(\"aggregateFunction\", \"-\");\n"+
				"absoluteVirtualColumn0.put(\"format\", \"-\");\n"+
				"Map<String, Object> absoluteVirtualColumn1 = new HashMap<>();\n"+
				"absoluteVirtualColumn1.put(\"name\", \"virtualColumn02\");\n"+
				"absoluteVirtualColumn1.put(\"displayName\", \"virtualColumn02\");\n"+
				"absoluteVirtualColumn1.put(\"fixedValue\", \"9002\");\n"+
				"absoluteVirtualColumn1.put(\"aggregateFunction\", \"MAX\");\n"+
				"absoluteVirtualColumn1.put(\"format\", \"000.000\");\n", selectCodeResult.get("absoluteVirtualColumnCode"));

		String deleteApiStr = "delete(\"jdbc_tool_test_emptyColumn\")";
		assertEquals("int count = " + deleteApiStr + "\n.execute();", result.get(3).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, null).toString()));
		assertNull(result.get(3).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE).toString()));
		assertNull(result.get(3).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_PK).toString()));
		assertNull(result.get(3).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_UK).toString()));
		assertNull(result.get(3).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE).toString()));
		assertNull(result.get(3).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_PK).toString()));
		assertNull(result.get(3).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_UK).toString()));
		assertNull(result.get(3).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE).toString()));
		assertNull(result.get(3).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_PK).toString()));
		assertNull(result.get(3).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_UK).toString()));
	}

	@Test
	public void testExport_fullCase() {

		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_fullcase.xml");
		ApiRequest apiRequest = parseRequestFromFile(getCommonSetting(),"jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_fullcase.yaml");
		List<CodeResponse> result = convertVe2Code(apiRequest);
assertEquals(4, result.size());
		assertEquals("INSERT", result.get(0).getType());
		assertEquals("仮想表ID: 35B6AB911C524EE69A30E29BF9A2904A", result.get(0).getDescription());
		assertEquals("INSERT INTO\n"+
				"    " + dbSchema + ".jdbc_tool_test_fullCaseMainEntity(objectID, StringColumn, IntColumn, NStringColumn, DateTimeColumn, YMColumn, CurrencyColumn, LongColumn, CompanyCD, ExclusiveFG, RegisteredPerson, UpdatedPerson, RegisteredDT, UpdatedDT)\n"+
				"VALUES\n"+
				"    " + "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " + getTimestampCommand() + ", " + getTimestampCommand() + ")", result.get(0).getSqlScript());

		Map<String, Object> insertCodeResult = (result.get(0)).getVirtualEntity();
		assertEquals(19, ((List<Map<String,Object>>)insertCodeResult.get("columns")).size());
		assertEquals("Map<String, Object> absoluteVirtualColumn0 = new HashMap<>();\n"+
				"absoluteVirtualColumn0.put(\"name\", \"virtualVolumn01\");\n"+
				"absoluteVirtualColumn0.put(\"displayName\", \"virtualVolumn01\");\n"+
				"absoluteVirtualColumn0.put(\"fixedValue\", \"virtualVolumn01V\");\n"+
				"absoluteVirtualColumn0.put(\"aggregateFunction\", \"-\");\n"+
				"absoluteVirtualColumn0.put(\"format\", \"virtualVolumn01F\");\n"+
				"Map<String, Object> absoluteVirtualColumn1 = new HashMap<>();\n"+
				"absoluteVirtualColumn1.put(\"name\", \"virtualVolumn02\");\n"+
				"absoluteVirtualColumn1.put(\"displayName\", \"virtualVolumn02\");\n"+
				"absoluteVirtualColumn1.put(\"fixedValue\", \"virtualVolumn02V\");\n"+
				"absoluteVirtualColumn1.put(\"aggregateFunction\", \"-\");\n"+
				"absoluteVirtualColumn1.put(\"format\", \"virtualVolumn02F\");\n", insertCodeResult.get("absoluteVirtualColumnCode"));

		String insertExecuteApiStr = "insert(\"jdbc_tool_test_fullCaseMainEntity\")\n"+ ".values(record)";
		String insertRecord = "GRecord record = createRecord();\n"+
				"record.setObject(\"objectID\",);\n"+
				"record.setObject(\"StringColumn\",);\n"+
				"record.setObject(\"IntColumn\",);\n"+
				"record.setObject(\"NStringColumn\",);\n"+
				"record.setObject(\"DateTimeColumn\",);\n"+
				"record.setObject(\"YMColumn\",);\n"+
				"record.setObject(\"CurrencyColumn\",);\n"+
				"record.setObject(\"LongColumn\",);\n"+
				"record.setObject(\"CompanyCD\",);\n"+
				"record.setObject(\"ExclusiveFG\",);\n"+
				"record.setObject(\"RegisteredPerson\",);\n"+
				"record.setObject(\"UpdatedPerson\",);\n"+
				"record.setObject(\"RegisteredDT\",);\n"+
				"record.setObject(\"UpdatedDT\",);\n";

		assertEquals(insertRecord + "\nint count = " + insertExecuteApiStr + "\n.execute();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, null).toString()));

		String insertExecuteBatchApiStr = "insert(\"jdbc_tool_test_fullCaseMainEntity\")\n"+ ".values(recordList)";
		StringBuilder insertRecordList = new StringBuilder();
		insertRecordList.append("List<GRecord> recordList = new ArrayList<>();").append("\n");
		insertRecordList.append(insertRecord);
		insertRecordList.append("recordList.add(record);").append("\n");

		assertEquals(insertRecordList + "\nint count = " + insertExecuteBatchApiStr + "\n.executeBatch();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, null).toString()));
		assertEquals(insertRecordList + "\nint count = " + insertExecuteBatchApiStr + "\n.executeList();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, null).toString()));


		assertEquals("UPDATE", result.get(1).getType());
		assertEquals("仮想表ID: 35B6AB911C524EE69A30E29BF9A2904A", result.get(1).getDescription());
		assertEquals("UPDATE\n"+
				"    " + dbSchema + ".jdbc_tool_test_fullCaseMainEntity jdbc_tool_test_fullCaseMainEntity\n"+
				"SET\n"+
				"    StringColumn = ?,\n"+
				"    IntColumn = ?,\n"+
				"    NStringColumn = ?,\n"+
				"    DateTimeColumn = ?,\n"+
				"    YMColumn = ?,\n"+
				"    CurrencyColumn = ?,\n"+
				"    LongColumn = ?,\n"+
				"    CompanyCD = ?,\n"+
				"    ExclusiveFG = ?,\n"+
				"    RegisteredPerson = ?,\n"+
				"    RegisteredDT = ?,\n"+
				"    UpdatedPerson = ?,\n"+
				"    UpdatedDT = ?\n"+
				"WHERE\n"+
				"    (\n"+
				"        jdbc_tool_test_fullCaseMainEntity.StringColumn = '9001'\n"+
				"        AND jdbc_tool_test_fullCaseMainEntity.IntColumn = 8001\n"+
				"    )\n"+
				"    AND (\n"+
				"        jdbc_tool_test_fullCaseMainEntity.NStringColumn = ?\n"+
				"        AND jdbc_tool_test_fullCaseMainEntity.DateTimeColumn = ?\n"+
				"        AND jdbc_tool_test_fullCaseMainEntity.CompanyCD = ?\n"+
				"    )", result.get(1).getSqlScript());

		Map<String, Object> updateCodeResult = (result.get(1)).getVirtualEntity();
		assertEquals(19, ((List<Map<String,Object>>)updateCodeResult.get("columns")).size());
		assertEquals("Map<String, Object> absoluteVirtualColumn0 = new HashMap<>();\n"+
				"absoluteVirtualColumn0.put(\"name\", \"virtualVolumn01\");\n"+
				"absoluteVirtualColumn0.put(\"displayName\", \"virtualVolumn01\");\n"+
				"absoluteVirtualColumn0.put(\"fixedValue\", \"virtualVolumn01V\");\n"+
				"absoluteVirtualColumn0.put(\"aggregateFunction\", \"-\");\n"+
				"absoluteVirtualColumn0.put(\"format\", \"virtualVolumn01F\");\n"+
				"Map<String, Object> absoluteVirtualColumn1 = new HashMap<>();\n"+
				"absoluteVirtualColumn1.put(\"name\", \"virtualVolumn02\");\n"+
				"absoluteVirtualColumn1.put(\"displayName\", \"virtualVolumn02\");\n"+
				"absoluteVirtualColumn1.put(\"fixedValue\", \"virtualVolumn02V\");\n"+
				"absoluteVirtualColumn1.put(\"aggregateFunction\", \"-\");\n"+
				"absoluteVirtualColumn1.put(\"format\", \"virtualVolumn02F\");\n", updateCodeResult.get("absoluteVirtualColumnCode"));

		String updateRecord = "GRecord record = createRecord();\n"+
				"record.setObject(\"StringColumn\",);\n"+
				"record.setObject(\"IntColumn\",);\n"+
				"record.setObject(\"NStringColumn\",);\n"+
				"record.setObject(\"DateTimeColumn\",);\n"+
				"record.setObject(\"YMColumn\",);\n"+
				"record.setObject(\"CurrencyColumn\",);\n"+
				"record.setObject(\"LongColumn\",);\n"+
				"record.setObject(\"CompanyCD\",);\n"+
				"record.setObject(\"ExclusiveFG\",);\n"+
				"record.setObject(\"RegisteredPerson\",);\n"+
				"record.setObject(\"RegisteredDT\",);\n"+
				"record.setObject(\"UpdatedPerson\",);\n"+
				"record.setObject(\"UpdatedDT\",);\n";

		String whereParamsParts = "Map<String, Object> whereParams = new HashMap<>();\n"+
				"whereParams.put(\"jdbc_tool_test_fullCaseMainEntity.DateTimeColumn\",);\n"+
				"whereParams.put(\"jdbc_tool_test_fullCaseMainEntity.NStringColumn\",);\n"+
				"whereParams.put(\"jdbc_tool_test_fullCaseMainEntity.CompanyCD\",);\n";
		String whereParamsPartsPKUK = "Object[] whereParams = new Object[]{,,};\n";
		StringBuilder updateRecordList = new StringBuilder();
		updateRecordList.append("List<GRecord> recordList = new ArrayList<>();").append("\n");
		updateRecordList.append(updateRecord);
		updateRecordList.append("recordList.add(record);").append("\n");

		String updateExecuteApiStr = "update(\"jdbc_tool_test_fullCaseMainEntity\")\n"+ ".set(record)";

		String updateWhereParts = ".where(expMap($($(\"jdbc_tool_test_fullCaseMainEntity.StringColumn = '9001'\").AND(\"jdbc_tool_test_fullCaseMainEntity.IntColumn = 8001\")).AND($(\"jdbc_tool_test_fullCaseMainEntity.NStringColumn = :jdbc_tool_test_fullCaseMainEntity.NStringColumn\").AND(\"jdbc_tool_test_fullCaseMainEntity.DateTimeColumn = :jdbc_tool_test_fullCaseMainEntity.DateTimeColumn\").AND(\"jdbc_tool_test_fullCaseMainEntity.CompanyCD = $TRIM(:jdbc_tool_test_fullCaseMainEntity.CompanyCD)\")), whereParams))";

		assertEquals(whereParamsParts + updateRecord + "\nint count = " + updateExecuteApiStr + "\n" + updateWhereParts + "\n.execute();",
				result.get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE).toString()));
		assertEquals(updateRecord + "\nint count = " + updateExecuteApiStr + "\n.wherePK()\n"+ ".execute();",
				result.get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_PK).toString()));
		assertEquals(updateRecord + "\nint count = " + updateExecuteApiStr + "\n.whereUK(\"MultipleColumnUK\")\n"+ "//.whereUK(\"OtherUKName\")\n"+ "//.whereUK(\"SingleColumnUK\")\n"+ ".execute();",
				result.get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_UK).toString()));

		String updateExecuteBatchApiStr = "update(\"jdbc_tool_test_fullCaseMainEntity\")\n"+
				".set(recordList)";

		assertEquals(updateRecordList + "\nint count = " + updateExecuteBatchApiStr + "\n.wherePK()\n"+ ".executeBatch();", result.get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_PK).toString()));
		assertEquals(updateRecordList + "\nint count = " + updateExecuteBatchApiStr + "\n.whereUK(\"MultipleColumnUK\")\n"+ "//.whereUK(\"OtherUKName\")\n"+ "//.whereUK(\"SingleColumnUK\")\n"+ ".executeBatch();", result.get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_UK).toString()));
		assertEquals(updateRecordList + "\nint count = " + updateExecuteBatchApiStr + "\n.wherePK()\n"+ ".executeList();", result.get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_PK).toString()));
		assertEquals(updateRecordList + "\nint count = " + updateExecuteBatchApiStr + "\n.whereUK(\"MultipleColumnUK\")\n"+ "//.whereUK(\"OtherUKName\")\n"+ "//.whereUK(\"SingleColumnUK\")\n"+ ".executeList();", result.get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_UK).toString()));

		assertEquals("SELECT", result.get(2).getType());
		assertEquals("仮想表ID: 35B6AB911C524EE69A30E29BF9A2904A", result.get(2).getDescription());
		assertEquals("SELECT\n"+
				"    MAX(jdbc_tool_test_fullCaseMainEntity.objectID),\n"+
				"    SUM(jdbc_tool_test_fullCaseMainEntity.StringColumn),\n"+
				"    AVG(jdbc_tool_test_fullCaseMainEntity.IntColumn),\n"+
				"    MIN(jdbc_tool_test_fullCaseMainEntity.NStringColumn),\n"+
				"    MAX(jdbc_tool_test_fullCaseMainEntity.DateTimeColumn),\n"+
				"    COUNT(jdbc_tool_test_fullCaseMainEntity.YMColumn),\n"+
				"    MAX(jdbc_tool_test_fullCaseMainEntity.CurrencyColumn),\n"+
				"    MAX(jdbc_tool_test_fullCaseMainEntity.LongColumn),\n"+
				"    MAX(jdbc_tool_test_fullCaseMainEntity.CompanyCD),\n"+
				"    MAX(jdbc_tool_test_fullCaseMainEntity.ExclusiveFG),\n"+
				"    MAX(jdbc_tool_test_fullCaseMainEntity.RegisteredPerson),\n"+
				"    MAX(jdbc_tool_test_fullCaseMainEntity.RegisteredDT),\n"+
				"    MAX(jdbc_tool_test_fullCaseMainEntity.UpdatedPerson),\n"+
				"    MAX(jdbc_tool_test_fullCaseMainEntity.UpdatedDT),\n"+
				"    MAX(jdbc_tool_test_fullCaseInnerJoinEntity.StringColumnInner),\n"+
				"    MAX(jdbc_tool_test_fullCaseRightJoinEntity.IntColumnRight),\n"+
				"    MAX(jdbc_tool_test_fullCaseLeftJoinEntity.NStringColumnLeft)\n"+
				"FROM\n"+
				"    " + dbSchema + ".jdbc_tool_test_fullCaseMainEntity jdbc_tool_test_fullCaseMainEntity\n"+
				"    INNER JOIN gef_jdbc_tool.jdbc_tool_test_fullCaseInnerJoinEntity jdbc_tool_test_fullCaseInnerJoinEntity ON jdbc_tool_test_fullCaseMainEntity.StringColumn = jdbc_tool_test_fullCaseInnerJoinEntity.StringColumnInner\n" +
				"    AND jdbc_tool_test_fullCaseInnerJoinEntity.NStringColumnInner = '9001'\n" +
				"    RIGHT OUTER JOIN gef_jdbc_tool.jdbc_tool_test_fullCaseRightJoinEntity jdbc_tool_test_fullCaseRightJoinEntity ON jdbc_tool_test_fullCaseMainEntity.NStringColumn = jdbc_tool_test_fullCaseRightJoinEntity.NStringColumnRight\n" +
				"    AND jdbc_tool_test_fullCaseRightJoinEntity.StringColumnRight = '7001'\n" +
				"    LEFT OUTER JOIN gef_jdbc_tool.jdbc_tool_test_fullCaseLeftJoinEntity jdbc_tool_test_fullCaseLeftJoinEntity ON jdbc_tool_test_fullCaseMainEntity.IntColumn = jdbc_tool_test_fullCaseLeftJoinEntity.IntColumnLeft\n" +
				"    AND jdbc_tool_test_fullCaseLeftJoinEntity.LongColumnLeft = 8001\n"+
				"WHERE\n"+
				"    (\n"+
				"        jdbc_tool_test_fullCaseMainEntity.StringColumn = '9001'\n"+
				"        AND jdbc_tool_test_fullCaseMainEntity.IntColumn = 8001\n"+
				"    )\n"+
				"    AND (\n"+
				"        jdbc_tool_test_fullCaseMainEntity.NStringColumn = ?\n"+
				"        AND jdbc_tool_test_fullCaseMainEntity.DateTimeColumn = ?\n"+
				"        AND jdbc_tool_test_fullCaseMainEntity.CompanyCD = ?\n"+
				"    )\n"+
				"GROUP BY\n"+
				"    jdbc_tool_test_fullCaseMainEntity.CurrencyColumn,\n"+
				"    jdbc_tool_test_fullCaseMainEntity.LongColumn\n"+
				"ORDER BY\n"+
				"    SUM(jdbc_tool_test_fullCaseMainEntity.StringColumn) ASC,\n"+
				"    MIN(jdbc_tool_test_fullCaseMainEntity.NStringColumn) DESC", result.get(2).getSqlScript());

		Map<String, Object> selectCodeResult = (result.get(2)).getVirtualEntity();
		assertEquals(19, ((List<Map<String,Object>>)selectCodeResult.get("columns")).size());
		assertEquals("Map<String, Object> absoluteVirtualColumn0 = new HashMap<>();\n"+
				"absoluteVirtualColumn0.put(\"name\", \"virtualVolumn01\");\n"+
				"absoluteVirtualColumn0.put(\"displayName\", \"virtualVolumn01\");\n"+
				"absoluteVirtualColumn0.put(\"fixedValue\", \"virtualVolumn01V\");\n"+
				"absoluteVirtualColumn0.put(\"aggregateFunction\", \"-\");\n"+
				"absoluteVirtualColumn0.put(\"format\", \"virtualVolumn01F\");\n"+
				"Map<String, Object> absoluteVirtualColumn1 = new HashMap<>();\n"+
				"absoluteVirtualColumn1.put(\"name\", \"virtualVolumn02\");\n"+
				"absoluteVirtualColumn1.put(\"displayName\", \"virtualVolumn02\");\n"+
				"absoluteVirtualColumn1.put(\"fixedValue\", \"virtualVolumn02V\");\n"+
				"absoluteVirtualColumn1.put(\"aggregateFunction\", \"-\");\n"+
				"absoluteVirtualColumn1.put(\"format\", \"virtualVolumn02F\");\n", selectCodeResult.get("absoluteVirtualColumnCode"));

		String selectApiStr = "select(\"jdbc_tool_test_fullCaseMainEntity\", \"jdbc_tool_test_fullCaseMainEntity\")\n"+
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
				"col(\"jdbc_tool_test_fullCaseLeftJoinEntity.NStringColumnLeft\", GColumn.FuncType.MAX))\n"+
				".groupBy(col(\"jdbc_tool_test_fullCaseMainEntity.CurrencyColumn\"),col(\"jdbc_tool_test_fullCaseMainEntity.LongColumn\"))\n"+
				".innerJoin(\"jdbc_tool_test_fullCaseInnerJoinEntity\", \"jdbc_tool_test_fullCaseInnerJoinEntity\", expMap($(\"jdbc_tool_test_fullCaseMainEntity.StringColumn = jdbc_tool_test_fullCaseInnerJoinEntity.StringColumnInner\").AND(\"jdbc_tool_test_fullCaseInnerJoinEntity.NStringColumnInner = '9001'\"), Collections.emptyMap()))\n" +
				".rightOuterJoin(\"jdbc_tool_test_fullCaseRightJoinEntity\", \"jdbc_tool_test_fullCaseRightJoinEntity\", expMap($(\"jdbc_tool_test_fullCaseMainEntity.NStringColumn = jdbc_tool_test_fullCaseRightJoinEntity.NStringColumnRight\").AND(\"jdbc_tool_test_fullCaseRightJoinEntity.StringColumnRight = '7001'\"), Collections.emptyMap()))\n" +
				".leftOuterJoin(\"jdbc_tool_test_fullCaseLeftJoinEntity\", \"jdbc_tool_test_fullCaseLeftJoinEntity\", expMap($(\"jdbc_tool_test_fullCaseMainEntity.IntColumn = jdbc_tool_test_fullCaseLeftJoinEntity.IntColumnLeft\").AND(\"jdbc_tool_test_fullCaseLeftJoinEntity.LongColumnLeft = 8001\"), Collections.emptyMap()))\n"+
				".orderBy(asc(\"jdbc_tool_test_fullCaseMainEntity.StringColumn\"),desc(\"jdbc_tool_test_fullCaseMainEntity.NStringColumn\"))";

		String selectWhereParts = ".where(expMap($($(\"jdbc_tool_test_fullCaseMainEntity.StringColumn = '9001'\").AND(\"jdbc_tool_test_fullCaseMainEntity.IntColumn = 8001\")).AND($(\"jdbc_tool_test_fullCaseMainEntity.NStringColumn = :jdbc_tool_test_fullCaseMainEntity.NStringColumn\").AND(\"jdbc_tool_test_fullCaseMainEntity.DateTimeColumn = :jdbc_tool_test_fullCaseMainEntity.DateTimeColumn\").AND(\"jdbc_tool_test_fullCaseMainEntity.CompanyCD = $TRIM(:jdbc_tool_test_fullCaseMainEntity.CompanyCD)\")), whereParams))";

		assertEquals(whereParamsParts + "List<GRecord> result = " + selectApiStr + "\n" + selectWhereParts + "\n.findList();", result.get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE).toString()));
		assertEquals(whereParamsPartsPKUK + "List<GRecord> result = " + selectApiStr + "\n.wherePK(whereParams)\n"+ ".findList();", result.get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE_PK).toString()));
		assertEquals(whereParamsPartsPKUK + "List<GRecord> result = " + selectApiStr + "\n.whereUK(\"MultipleColumnUK\", whereParams)\n"+ "//.whereUK(\"OtherUKName\", whereParams)\n"+ "//.whereUK(\"SingleColumnUK\", whereParams)\n"+ ".findList();", result.get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE_UK).toString()));
		assertEquals(whereParamsParts + "GRecord result = " + selectApiStr + "\n" + selectWhereParts + "\n.findRecord();", result.get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE).toString()));
		assertEquals(whereParamsPartsPKUK + "GRecord result = " + selectApiStr + "\n.wherePK(whereParams)\n"+ ".findRecord();", result.get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE_PK).toString()));
		assertEquals(whereParamsPartsPKUK + "GRecord result = " + selectApiStr + "\n.whereUK(\"MultipleColumnUK\", whereParams)\n"+ "//.whereUK(\"OtherUKName\", whereParams)\n"+ "//.whereUK(\"SingleColumnUK\", whereParams)\n"+ ".findRecord();", result.get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE_UK).toString()));
		assertEquals(whereParamsParts + "GRecordSet result = " + selectApiStr + "\n" + selectWhereParts + "\n.findRecordSet();", result.get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE).toString()));
		assertEquals(whereParamsPartsPKUK + "GRecordSet result = " + selectApiStr + "\n.wherePK(whereParams)\n"+ ".findRecordSet();", result.get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE_PK).toString()));
		assertEquals(whereParamsPartsPKUK + "GRecordSet result = " + selectApiStr + "\n.whereUK(\"MultipleColumnUK\", whereParams)\n"+ "//.whereUK(\"OtherUKName\", whereParams)\n"+ "//.whereUK(\"SingleColumnUK\", whereParams)\n"+ ".findRecordSet();", result.get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE_UK).toString()));

		assertEquals("DELETE", result.get(3).getType());
		assertEquals("仮想表ID: 35B6AB911C524EE69A30E29BF9A2904A", result.get(3).getDescription());
		assertEquals(getDeleteSql_fullCase(), result.get(3).getSqlScript());

		Map<String, Object> deleteCodeResult = (result.get(3)).getVirtualEntity();
		assertEquals(19, ((List<Map<String,Object>>)deleteCodeResult.get("columns")).size());
		assertEquals("Map<String, Object> absoluteVirtualColumn0 = new HashMap<>();\n"+
				"absoluteVirtualColumn0.put(\"name\", \"virtualVolumn01\");\n"+
				"absoluteVirtualColumn0.put(\"displayName\", \"virtualVolumn01\");\n"+
				"absoluteVirtualColumn0.put(\"fixedValue\", \"virtualVolumn01V\");\n"+
				"absoluteVirtualColumn0.put(\"aggregateFunction\", \"-\");\n"+
				"absoluteVirtualColumn0.put(\"format\", \"virtualVolumn01F\");\n"+
				"Map<String, Object> absoluteVirtualColumn1 = new HashMap<>();\n"+
				"absoluteVirtualColumn1.put(\"name\", \"virtualVolumn02\");\n"+
				"absoluteVirtualColumn1.put(\"displayName\", \"virtualVolumn02\");\n"+
				"absoluteVirtualColumn1.put(\"fixedValue\", \"virtualVolumn02V\");\n"+
				"absoluteVirtualColumn1.put(\"aggregateFunction\", \"-\");\n"+
				"absoluteVirtualColumn1.put(\"format\", \"virtualVolumn02F\");\n", selectCodeResult.get("absoluteVirtualColumnCode"));

		String deleteApiStr = "delete(\"jdbc_tool_test_fullCaseMainEntity\")";

		String deleteWhereParts = ".where(expMap($($(\"jdbc_tool_test_fullCaseMainEntity.StringColumn = '9001'\").AND(\"jdbc_tool_test_fullCaseMainEntity.IntColumn = 8001\")).AND($(\"jdbc_tool_test_fullCaseMainEntity.NStringColumn = :jdbc_tool_test_fullCaseMainEntity.NStringColumn\").AND(\"jdbc_tool_test_fullCaseMainEntity.DateTimeColumn = :jdbc_tool_test_fullCaseMainEntity.DateTimeColumn\").AND(\"jdbc_tool_test_fullCaseMainEntity.CompanyCD = $TRIM(:jdbc_tool_test_fullCaseMainEntity.CompanyCD)\")), whereParams))";

		assertEquals(whereParamsParts + "int count = " + deleteApiStr + "\n" + deleteWhereParts + "\n.execute();", result.get(3).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE).toString()));

		String deleteExecutePKWhereParts = "GRecord whereParams = createRecord();\n"+
				"whereParams.setObject(\"objectID\",);\n";


		String deleteExecuteListPKWhereParts = deleteExecutePKWhereParts +
				"List<GRecord> whereParamsList = new ArrayList<>();\n"+
				"whereParamsList.add(whereParams);\n";

		String deleteExecuteUKWhereParts = "GRecord whereParams = createRecord();\n"+
				"whereParams.setObject(\"StringColumn\",);\n"+
				"whereParams.setObject(\"IntColumn\",);"  + "\n" +
				"whereParams.setObject(\"NStringColumn\",);\n"+
				"//ユーニックキー「OtherUKName」用のカラム\n"+
				"/*whereParams.setObject(\"IntColumn\",);\n"+
				"whereParams.setObject(\"NStringColumn\",);\n"+
				"whereParams.setObject(\"DateTimeColumn\",);\n"+
				"whereParams.setObject(\"YMColumn\",);\n"+
				"*/\n"+
				"//ユーニックキー「SingleColumnUK」用のカラム\n"+
				"/*whereParams.setObject(\"StringColumn\",);\n"+
				"*/\n";
		String deleteExecuteListUKWhereParts = deleteExecuteUKWhereParts +
				"List<GRecord> whereParamsList = new ArrayList<>();\n"+
				"whereParamsList.add(whereParams);\n";

		assertEquals(deleteExecutePKWhereParts + "\nint count = " + deleteApiStr + "\n.wherePK(whereParams)\n"+ ".execute();", result.get(3).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_PK).toString()));
		assertEquals(deleteExecuteUKWhereParts + "\nint count = " + deleteApiStr + "\n.whereUK(\"MultipleColumnUK\", whereParams)\n"+ "//.whereUK(\"OtherUKName\", whereParams)\n"+ "//.whereUK(\"SingleColumnUK\", whereParams)\n"+ ".execute();", result.get(3).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_UK).toString()));

		assertEquals(deleteExecuteListPKWhereParts + "\nint count = " + deleteApiStr + "\n.wherePK(whereParamsList)\n"+ ".executeList();", result.get(3).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_PK).toString()));
		assertEquals(deleteExecuteListUKWhereParts + "\nint count = " + deleteApiStr + "\n.whereUK(\"MultipleColumnUK\", whereParamsList)\n"+ "//.whereUK(\"OtherUKName\", whereParamsList)\n"+ "//.whereUK(\"SingleColumnUK\", whereParamsList)\n"+ ".executeList();", result.get(3).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_UK).toString()));

		assertEquals(deleteExecuteListPKWhereParts + "\nint count = " + deleteApiStr + "\n.wherePK(whereParamsList)\n"+ ".executeBatch();", result.get(3).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_PK).toString()));
		assertEquals(deleteExecuteListUKWhereParts + "\nint count = " + deleteApiStr + "\n.whereUK(\"MultipleColumnUK\", whereParamsList)\n"+ "//.whereUK(\"OtherUKName\", whereParamsList)\n"+ "//.whereUK(\"SingleColumnUK\", whereParamsList)\n"+ ".executeBatch();", result.get(3).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_UK).toString()));
	}

	public String getDeleteSql_fullCase() {
		return "DELETE FROM\n"+
				"    " + dbSchema + ".jdbc_tool_test_fullCaseMainEntity\n"+
				"WHERE\n"+
				"    (\n"+
				"        " + dbSchema + ".jdbc_tool_test_fullCaseMainEntity.StringColumn = '9001'\n"+
				"        AND " + dbSchema + ".jdbc_tool_test_fullCaseMainEntity.IntColumn = 8001\n"+
				"    )\n"+
				"    AND (\n"+
				"        " + dbSchema + ".jdbc_tool_test_fullCaseMainEntity.NStringColumn = ?\n"+
				"        AND " + dbSchema + ".jdbc_tool_test_fullCaseMainEntity.DateTimeColumn = ?\n"+
				"        AND " + dbSchema + ".jdbc_tool_test_fullCaseMainEntity.CompanyCD = ?\n"+
				"    )";
	}

	@Test
	public void testExport_multipleUK_inq() {

		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_multipleUK_inq.xml");
		ApiRequest apiRequest = parseRequestFromFile(getCommonSetting(),"jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_multipleUK_inq.yaml");
		List<CodeResponse> result = convertVe2Code(apiRequest);
assertEquals(2, result.size());
		assertEquals("SELECT", result.get(0).getType());
		assertEquals("仮想表ID: 5936D594156641C5B5E5169879CE2888", result.get(0).getDescription());
		assertEquals("SELECT\n"+
				"    jdbc_tool_test_multipleUK.objectID,\n"+
				"    jdbc_tool_test_multipleUK.StringColumn,\n"+
				"    jdbc_tool_test_multipleUK.NStringColumn,\n"+
				"    jdbc_tool_test_multipleUK.IntColumn\n"+
				"FROM\n"+
				"    " + dbSchema + ".jdbc_tool_test_multipleUK jdbc_tool_test_multipleUK\n"+
				"WHERE\n"+
				"    (\n"+
				"        jdbc_tool_test_multipleUK.objectID = ?\n"+
				"        AND jdbc_tool_test_multipleUK.NStringColumn = ?\n"+
				"    )", result.get(0).getSqlScript());

		Map<String, Object> selectCodeResult = (result.get(0)).getVirtualEntity();
		assertEquals(4, ((List<Map<String,Object>>)selectCodeResult.get("columns")).size());
		assertEquals("-", selectCodeResult.get("absoluteVirtualColumnCode"));

		String whereParamsParts = "Map<String, Object> whereParams = new HashMap<>();\n"+
				"whereParams.put(\"jdbc_tool_test_multipleUK.objectID\",);\n"+
				"whereParams.put(\"jdbc_tool_test_multipleUK.NStringColumn\",);\n";
		String whereParamsPartsUKPK = "Object[] whereParams = new Object[]{,};\n";
		String selectApiStr = "select(\"jdbc_tool_test_multipleUK\", \"jdbc_tool_test_multipleUK\")\n"+
				".fields(colsAll())\n";
		String selectWhereParts = ".where(expMap($($(\"jdbc_tool_test_multipleUK.objectID = :jdbc_tool_test_multipleUK.objectID\").AND(\"jdbc_tool_test_multipleUK.NStringColumn = :jdbc_tool_test_multipleUK.NStringColumn\")), whereParams))\n";

		assertEquals(whereParamsParts + "List<GRecord> result = " + selectApiStr + selectWhereParts + ".findList();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE).toString()));
		assertEquals(whereParamsPartsUKPK + "List<GRecord> result = " + selectApiStr + ".wherePK(whereParams)\n"+ ".findList();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE_PK).toString()));
		assertEquals(whereParamsPartsUKPK + "List<GRecord> result = " + selectApiStr + ".whereUK(\"unionkey01\", whereParams)\n"+ "//.whereUK(\"unionkey02\", whereParams)\n"+ "//.whereUK(\"unionkey03\", whereParams)\n"+ ".findList();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE_UK).toString()));
		assertEquals(whereParamsParts + "GRecord result = " + selectApiStr + selectWhereParts + ".findRecord();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE).toString()));
		assertEquals(whereParamsPartsUKPK + "GRecord result = " + selectApiStr + ".wherePK(whereParams)\n"+ ".findRecord();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE_PK).toString()));
		assertEquals(whereParamsPartsUKPK + "GRecord result = " + selectApiStr + ".whereUK(\"unionkey01\", whereParams)\n"+ "//.whereUK(\"unionkey02\", whereParams)\n"+ "//.whereUK(\"unionkey03\", whereParams)\n"+ ".findRecord();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE_UK).toString()));
		assertEquals(whereParamsParts + "GRecordSet result = " + selectApiStr + selectWhereParts + ".findRecordSet();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE).toString()));
		assertEquals(whereParamsPartsUKPK + "GRecordSet result = " + selectApiStr + ".wherePK(whereParams)\n"+ ".findRecordSet();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE_PK).toString()));
		assertEquals(whereParamsPartsUKPK + "GRecordSet result = " + selectApiStr + ".whereUK(\"unionkey01\", whereParams)\n"+ "//.whereUK(\"unionkey02\", whereParams)\n"+ "//.whereUK(\"unionkey03\", whereParams)\n"+ ".findRecordSet();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE_UK).toString()));

		assertEquals("DELETE", result.get(1).getType());
		assertEquals("仮想表ID: 5936D594156641C5B5E5169879CE2888", result.get(1).getDescription());
		assertEquals(getDeleteSql_multipleUK(), result.get(1).getSqlScript());

		Map<String, Object> deleteCodeResult = (result.get(1)).getVirtualEntity();
		assertEquals(4, ((List<Map<String,Object>>)deleteCodeResult.get("columns")).size());
		assertEquals("-", selectCodeResult.get("absoluteVirtualColumnCode"));

		String deleteApiStr = "delete(\"jdbc_tool_test_multipleUK\")\n";
		String deleteWhereParts = ".where(expMap($($(\"jdbc_tool_test_multipleUK.objectID = :jdbc_tool_test_multipleUK.objectID\").AND(\"jdbc_tool_test_multipleUK.NStringColumn = :jdbc_tool_test_multipleUK.NStringColumn\")), whereParams))\n";

		assertEquals(whereParamsParts + "int count = " + deleteApiStr + deleteWhereParts + ".execute();", result.get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE).toString()));

		String deleteExecutePKWhereParts = "GRecord whereParams = createRecord();\n"+
				"whereParams.setObject(\"objectID\",);\n";


		String deleteExecuteListPKWhereParts = deleteExecutePKWhereParts +
				"List<GRecord> whereParamsList = new ArrayList<>();\n"+
				"whereParamsList.add(whereParams);\n";

		String deleteExecuteUKWhereParts = "GRecord whereParams = createRecord();\n"+
				"whereParams.setObject(\"objectID\",);\n"+
				"whereParams.setObject(\"StringColumn\",);\n"+
				"//ユーニックキー「unionkey02」用のカラム\n"+
				"/*whereParams.setObject(\"objectID\",);\n"+
				"whereParams.setObject(\"NStringColumn\",);\n"+
				"*/\n"+ "//ユーニックキー「unionkey03」用のカラム\n"+
				"/*whereParams.setObject(\"StringColumn\",);\n"+
				"whereParams.setObject(\"NStringColumn\",);\n"+
				"*/\n";
		String deleteExecuteListUKWhereParts = deleteExecuteUKWhereParts +
				"List<GRecord> whereParamsList = new ArrayList<>();\n"+
				"whereParamsList.add(whereParams);\n";

		assertEquals(deleteExecutePKWhereParts + "\nint count = " + deleteApiStr + ".wherePK(whereParams)\n"+ ".execute();", result.get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_PK).toString()));
		assertEquals(deleteExecuteUKWhereParts + "\nint count = " + deleteApiStr + ".whereUK(\"unionkey01\", whereParams)\n"+ "//.whereUK(\"unionkey02\", whereParams)\n"+ "//.whereUK(\"unionkey03\", whereParams)\n"+ ".execute();", result.get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_UK).toString()));

		assertEquals(deleteExecuteListPKWhereParts + "\nint count = " + deleteApiStr + ".wherePK(whereParamsList)\n"+ ".executeList();", result.get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_PK).toString()));
		assertEquals(deleteExecuteListUKWhereParts + "\nint count = " + deleteApiStr + ".whereUK(\"unionkey01\", whereParamsList)\n"+ "//.whereUK(\"unionkey02\", whereParamsList)\n"+ "//.whereUK(\"unionkey03\", whereParamsList)\n"+ ".executeList();", result.get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_UK).toString()));

		assertEquals(deleteExecuteListPKWhereParts + "\nint count = " + deleteApiStr + ".wherePK(whereParamsList)\n"+ ".executeBatch();", result.get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_PK).toString()));
		assertEquals(deleteExecuteListUKWhereParts + "\nint count = " + deleteApiStr + ".whereUK(\"unionkey01\", whereParamsList)\n"+ "//.whereUK(\"unionkey02\", whereParamsList)\n"+ "//.whereUK(\"unionkey03\", whereParamsList)\n"+ ".executeBatch();", result.get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_UK).toString()));
	}

	public String getDeleteSql_multipleUK() {
		return "DELETE FROM\n"+
				"    " + dbSchema + ".jdbc_tool_test_multipleUK\n"+
				"WHERE\n"+
				"    (\n"+
				"        " + dbSchema + ".jdbc_tool_test_multipleUK.objectID = ?\n"+
				"        AND " + dbSchema + ".jdbc_tool_test_multipleUK.NStringColumn = ?\n"+
				"    )";
	}

	@Test
	public void testExport_multipleUK_reg() {

		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_multipleUK_reg.xml");
		ApiRequest apiRequest = parseRequestFromFile(getCommonSetting(),"jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_multipleUK_reg.yaml");
		List<CodeResponse> result = convertVe2Code(apiRequest);
assertEquals(4, result.size());
		assertEquals("INSERT", result.get(0).getType());
		assertEquals("仮想表ID: 32F9873E7C744002A8DBD49D87007442", result.get(0).getDescription());
		assertEquals("INSERT INTO\n"+
				"    " + dbSchema + ".jdbc_tool_test_multipleUK(objectID, StringColumn, NStringColumn, IntColumn)\n"+
				"VALUES\n"+
				"    " + "(?, ?, ?, ?)", result.get(0).getSqlScript());

		Map<String, Object> insertCodeResult = (result.get(0)).getVirtualEntity();
		assertEquals(4, ((List<Map<String,Object>>)insertCodeResult.get("columns")).size());
		assertEquals("-", insertCodeResult.get("absoluteVirtualColumnCode"));

		String insertRecord = "GRecord record = createRecord();\n"+
				"record.setObject(\"objectID\",);\n"+
				"record.setObject(\"StringColumn\",);\n"+
				"record.setObject(\"NStringColumn\",);\n"+
				"record.setObject(\"IntColumn\",);\n";
		String insertExecuteApiStr = "insert(\"jdbc_tool_test_multipleUK\")\n"+ ".values(record)";

		assertEquals(insertRecord + "\nint count = " + insertExecuteApiStr + "\n.execute();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, null).toString()));
		assertNull(result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE).toString()));
		assertNull(result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_PK).toString()));
		assertNull(result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_UK).toString()));

		String insertExecuteBatchApiStr = "insert(\"jdbc_tool_test_multipleUK\")\n"+ ".values(recordList)";
		StringBuilder insertRecordList = new StringBuilder();
		insertRecordList.append("List<GRecord> recordList = new ArrayList<>();").append("\n");
		insertRecordList.append(insertRecord);
		insertRecordList.append("recordList.add(record);").append("\n");

		assertEquals(insertRecordList + "\nint count = " + insertExecuteBatchApiStr + "\n.executeBatch();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, null).toString()));
		assertNull(result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE).toString()));
		assertNull(result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_PK).toString()));
		assertNull(result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_UK).toString()));
		assertEquals(insertRecordList + "\nint count = " + insertExecuteBatchApiStr + "\n.executeList();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, null).toString()));
		assertNull(result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE).toString()));
		assertNull(result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_PK).toString()));
		assertNull(result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_UK).toString()));


		assertEquals("UPDATE", result.get(1).getType());
		assertEquals("仮想表ID: 32F9873E7C744002A8DBD49D87007442", result.get(1).getDescription());
		assertEquals("UPDATE\n"+
				"    " + dbSchema + ".jdbc_tool_test_multipleUK jdbc_tool_test_multipleUK\n"+
				"SET\n"+
				"    StringColumn = ?,\n"+
				"    NStringColumn = ?,\n"+
				"    IntColumn = ?\n"+
				"WHERE\n"+
				"    (\n"+
				"        jdbc_tool_test_multipleUK.objectID = ?\n"+
				"        AND jdbc_tool_test_multipleUK.NStringColumn = ?\n"+
				"    )", result.get(1).getSqlScript());

		Map<String, Object> updateCodeResult = (result.get(1)).getVirtualEntity();
		assertEquals(4, ((List<Map<String,Object>>)updateCodeResult.get("columns")).size());
		assertEquals("-", updateCodeResult.get("absoluteVirtualColumnCode"));

		String whereParamsParts = "Map<String, Object> whereParams = new HashMap<>();\n"+
				"whereParams.put(\"jdbc_tool_test_multipleUK.objectID\",);\n"+
				"whereParams.put(\"jdbc_tool_test_multipleUK.NStringColumn\",);\n";
		String whereParamsPartsUKPK = "Object[] whereParams = new Object[]{,};\n";
		String updateRecord = "GRecord record = createRecord();\n"+
				"record.setObject(\"StringColumn\",);\n"+
				"record.setObject(\"NStringColumn\",);\n"+
				"record.setObject(\"IntColumn\",);\n";
		String updateExecuteApiStr = "update(\"jdbc_tool_test_multipleUK\")\n"+ ".set(record)\n";
		String updateExecuteApiWhereStr = ".where(expMap($($(\"jdbc_tool_test_multipleUK.objectID = :jdbc_tool_test_multipleUK.objectID\").AND(\"jdbc_tool_test_multipleUK.NStringColumn = :jdbc_tool_test_multipleUK.NStringColumn\")), whereParams))\n";
		assertEquals(whereParamsParts + updateRecord + "\nint count = " + updateExecuteApiStr + updateExecuteApiWhereStr + ".execute();", result.get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE).toString()));
		assertEquals(updateRecord + "\nint count = " + updateExecuteApiStr + ".wherePK()\n"+ ".execute();", result.get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_PK).toString()));
		assertEquals(updateRecord + "\nint count = " + updateExecuteApiStr + ".whereUK(\"unionkey01\")\n"+ "//.whereUK(\"unionkey02\")\n"+ "//.whereUK(\"unionkey03\")\n"+ ".execute();", result.get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_UK).toString()));

		StringBuilder updateRecordList = new StringBuilder();
		updateRecordList.append("List<GRecord> recordList = new ArrayList<>();").append("\n");
		updateRecordList.append(updateRecord);
		updateRecordList.append("recordList.add(record);").append("\n");

		String updateExecuteListApiStr = "update(\"jdbc_tool_test_multipleUK\")\n"+ ".set(recordList)\n";

		assertEquals(updateRecordList + "\nint count = " + updateExecuteListApiStr + ".wherePK()\n"+ ".executeList();", result.get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_PK).toString()));
		assertEquals(updateRecordList + "\nint count = " + updateExecuteListApiStr + ".whereUK(\"unionkey01\")\n"+ "//.whereUK(\"unionkey02\")\n"+ "//.whereUK(\"unionkey03\")\n"+ ".executeList();", result.get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_UK).toString()));
		assertEquals(updateRecordList + "\nint count = " + updateExecuteListApiStr + ".wherePK()\n"+ ".executeBatch();", result.get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_PK).toString()));
		assertEquals(updateRecordList + "\nint count = " + updateExecuteListApiStr + ".whereUK(\"unionkey01\")\n"+ "//.whereUK(\"unionkey02\")\n"+ "//.whereUK(\"unionkey03\")\n"+ ".executeBatch();", result.get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_UK).toString()));

		assertEquals("SELECT", result.get(2).getType());
		assertEquals("仮想表ID: 32F9873E7C744002A8DBD49D87007442", result.get(2).getDescription());
		assertEquals("SELECT\n"+
				"    jdbc_tool_test_multipleUK.objectID,\n"+
				"    jdbc_tool_test_multipleUK.StringColumn,\n"+
				"    jdbc_tool_test_multipleUK.NStringColumn,\n"+
				"    jdbc_tool_test_multipleUK.IntColumn\n"+
				"FROM\n"+
				"    " + dbSchema + ".jdbc_tool_test_multipleUK jdbc_tool_test_multipleUK\n"+
				"WHERE\n"+
				"    (\n"+
				"        jdbc_tool_test_multipleUK.objectID = ?\n"+
				"        AND jdbc_tool_test_multipleUK.NStringColumn = ?\n"+
				"    )", result.get(2).getSqlScript());

		Map<String, Object> selectCodeResult = (result.get(2)).getVirtualEntity();
		assertEquals(4, ((List<Map<String,Object>>)selectCodeResult.get("columns")).size());
		assertEquals("-", selectCodeResult.get("absoluteVirtualColumnCode"));

		String selectApiStr = "select(\"jdbc_tool_test_multipleUK\", \"jdbc_tool_test_multipleUK\")\n"+
				".fields(colsAll())\n";
		String selectApiWhereStr = ".where(expMap($($(\"jdbc_tool_test_multipleUK.objectID = :jdbc_tool_test_multipleUK.objectID\").AND(\"jdbc_tool_test_multipleUK.NStringColumn = :jdbc_tool_test_multipleUK.NStringColumn\")), whereParams))\n";
		assertEquals(whereParamsParts + "List<GRecord> result = " + selectApiStr + selectApiWhereStr + ".findList();", result.get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE).toString()));
		assertEquals(whereParamsPartsUKPK + "List<GRecord> result = " + selectApiStr + ".wherePK(whereParams)\n"+ ".findList();", result.get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE_PK).toString()));
		assertEquals(whereParamsPartsUKPK + "List<GRecord> result = " + selectApiStr + ".whereUK(\"unionkey01\", whereParams)\n"+ "//.whereUK(\"unionkey02\", whereParams)\n"+ "//.whereUK(\"unionkey03\", whereParams)\n"+ ".findList();", result.get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE_UK).toString()));

		assertEquals(whereParamsParts + "GRecord result = " + selectApiStr + selectApiWhereStr + ".findRecord();", result.get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE).toString()));
		assertEquals(whereParamsPartsUKPK + "GRecord result = " + selectApiStr + ".wherePK(whereParams)\n"+ ".findRecord();", result.get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE_PK).toString()));
		assertEquals(whereParamsPartsUKPK + "GRecord result = " + selectApiStr + ".whereUK(\"unionkey01\", whereParams)\n"+ "//.whereUK(\"unionkey02\", whereParams)\n"+ "//.whereUK(\"unionkey03\", whereParams)\n"+ ".findRecord();", result.get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE_UK).toString()));

		assertEquals(whereParamsParts + "GRecordSet result = " + selectApiStr + selectApiWhereStr + ".findRecordSet();", result.get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE).toString()));
		assertEquals(whereParamsPartsUKPK + "GRecordSet result = " + selectApiStr + ".wherePK(whereParams)\n"+ ".findRecordSet();", result.get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE_PK).toString()));
		assertEquals(whereParamsPartsUKPK + "GRecordSet result = " + selectApiStr + ".whereUK(\"unionkey01\", whereParams)\n"+ "//.whereUK(\"unionkey02\", whereParams)\n"+ "//.whereUK(\"unionkey03\", whereParams)\n"+ ".findRecordSet();", result.get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE_UK).toString()));

		assertEquals("DELETE", result.get(3).getType());
		assertEquals("仮想表ID: 32F9873E7C744002A8DBD49D87007442", result.get(3).getDescription());
		assertEquals(getDeleteSql_multipleUK(), result.get(3).getSqlScript());

		Map<String, Object> deleteCodeResult = (result.get(3)).getVirtualEntity();
		assertEquals(4, ((List<Map<String,Object>>)deleteCodeResult.get("columns")).size());
		assertEquals("-", selectCodeResult.get("absoluteVirtualColumnCode"));

		String deleteRecordStr = "GRecord whereParams = createRecord();\n"+
				"whereParams.setObject(\"objectID\",);\n";
		String deleteApiStr = "delete(\"jdbc_tool_test_multipleUK\")\n";
		String deleteApiWhereStr = ".where(expMap($($(\"jdbc_tool_test_multipleUK.objectID = :jdbc_tool_test_multipleUK.objectID\").AND(\"jdbc_tool_test_multipleUK.NStringColumn = :jdbc_tool_test_multipleUK.NStringColumn\")), whereParams))\n";
		assertEquals(whereParamsParts + "int count = " + deleteApiStr + deleteApiWhereStr + ".execute();", result.get(3).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE).toString()));

		String deleteUKRecordStr = "GRecord whereParams = createRecord();\n"+
				"whereParams.setObject(\"objectID\",);\n"+
				"whereParams.setObject(\"StringColumn\",);\n"+
				"//ユーニックキー「unionkey02」用のカラム\n"+
				"/*whereParams.setObject(\"objectID\",);\n"+
				"whereParams.setObject(\"NStringColumn\",);\n"+
				"*/\n"+  "//ユーニックキー「unionkey03」用のカラム\n"+
				"/*whereParams.setObject(\"StringColumn\",);\n"+
				"whereParams.setObject(\"NStringColumn\",);\n"+
				"*/\n";
		assertEquals(deleteRecordStr + "\nint count = " + deleteApiStr + ".wherePK(whereParams)\n"+ ".execute();", result.get(3).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_PK).toString()));
		assertEquals(deleteUKRecordStr + "\nint count = " + deleteApiStr + ".whereUK(\"unionkey01\", whereParams)\n"+ "//.whereUK(\"unionkey02\", whereParams)\n"+ "//.whereUK(\"unionkey03\", whereParams)\n"+ ".execute();", result.get(3).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_UK).toString()));

		StringBuilder deleteListRecordStr = new StringBuilder();
		deleteListRecordStr.append(deleteRecordStr);
		deleteListRecordStr.append("List<GRecord> whereParamsList = new ArrayList<>();").append("\n");
		deleteListRecordStr.append("whereParamsList.add(whereParams);").append("\n");

		StringBuilder deleteListUKRecordStr = new StringBuilder();
		deleteListUKRecordStr.append(deleteUKRecordStr);
		deleteListUKRecordStr.append("List<GRecord> whereParamsList = new ArrayList<>();").append("\n");
		deleteListUKRecordStr.append("whereParamsList.add(whereParams);").append("\n");
		assertEquals(deleteListRecordStr + "\nint count = " + deleteApiStr + ".wherePK(whereParamsList)\n"+ ".executeList();", result.get(3).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_PK).toString()));
		assertEquals(deleteListUKRecordStr + "\nint count = " + deleteApiStr + ".whereUK(\"unionkey01\", whereParamsList)\n"+ "//.whereUK(\"unionkey02\", whereParamsList)\n"+ "//.whereUK(\"unionkey03\", whereParamsList)\n"+ ".executeList();", result.get(3).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_UK).toString()));
		assertEquals(deleteListRecordStr + "\nint count = " + deleteApiStr + ".wherePK(whereParamsList)\n"+ ".executeBatch();", result.get(3).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_PK).toString()));
		assertEquals(deleteListUKRecordStr + "\nint count = " + deleteApiStr + ".whereUK(\"unionkey01\", whereParamsList)\n"+ "//.whereUK(\"unionkey02\", whereParamsList)\n"+ "//.whereUK(\"unionkey03\", whereParamsList)\n"+ ".executeBatch();", result.get(3).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_UK).toString()));
	}

	@Test
	public void testExport_noPrimaryKey_inq() {

		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_noPrimaryKey_inq.xml");
		ApiRequest apiRequest = parseRequestFromFile(getCommonSetting(),"jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_noPrimaryKey_inq.yaml");
		List<CodeResponse> result = convertVe2Code(apiRequest);
assertEquals(2, result.size());
		assertEquals("SELECT", result.get(0).getType());
		assertEquals("仮想表ID: 4A0C9E4BF6234DFC86870D3136BA7136", result.get(0).getDescription());
		assertEquals("SELECT\n"+
				"    jdbc_tool_test_noPrimaryKey.StringColumn,\n"+
				"    jdbc_tool_test_noPrimaryKey.NStringColumn,\n"+
				"    jdbc_tool_test_noPrimaryKey.IntColumn\n"+
				"FROM\n"+
				"    " + dbSchema + ".jdbc_tool_test_noPrimaryKey jdbc_tool_test_noPrimaryKey\n"+
				"WHERE\n"+
				"    jdbc_tool_test_noPrimaryKey.StringColumn = ?", result.get(0).getSqlScript());

		Map<String, Object> selectCodeResult = (result.get(0)).getVirtualEntity();
		assertEquals(3, ((List<Map<String,Object>>)selectCodeResult.get("columns")).size());
		assertEquals("-", selectCodeResult.get("absoluteVirtualColumnCode"));

		String whereParamsParts = "Map<String, Object> whereParams = new HashMap<>();\n"+
				"whereParams.put(\"jdbc_tool_test_noPrimaryKey.StringColumn\",);\n";
		String whereParamsPartsUKPK = "Object[] whereParams = new Object[]{};\n";
		String selectApiStr = "select(\"jdbc_tool_test_noPrimaryKey\", \"jdbc_tool_test_noPrimaryKey\")\n"+ ".fields(colsAll())\n";
		String selectWhereParts = ".where(expMap($(\"jdbc_tool_test_noPrimaryKey.StringColumn = :jdbc_tool_test_noPrimaryKey.StringColumn\"), whereParams))\n";

		assertEquals(whereParamsParts + "List<GRecord> result = " + selectApiStr + selectWhereParts + ".findList();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE).toString()));
		assertEquals(whereParamsPartsUKPK + "List<GRecord> result = " + selectApiStr + ".whereUK(\"unionkey02\", whereParams)\n"+ ".findList();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE_UK).toString()));
		assertEquals(whereParamsParts + "GRecord result = " + selectApiStr + selectWhereParts + ".findRecord();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE).toString()));
		assertEquals(whereParamsPartsUKPK + "GRecord result = " + selectApiStr + ".whereUK(\"unionkey02\", whereParams)\n"+ ".findRecord();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE_UK).toString()));
		assertEquals(whereParamsParts + "GRecordSet result = " + selectApiStr + selectWhereParts + ".findRecordSet();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE).toString()));
		assertEquals(whereParamsPartsUKPK + "GRecordSet result = " + selectApiStr + ".whereUK(\"unionkey02\", whereParams)\n"+ ".findRecordSet();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE_UK).toString()));

		assertEquals("DELETE", result.get(1).getType());
		assertEquals("仮想表ID: 4A0C9E4BF6234DFC86870D3136BA7136", result.get(1).getDescription());
		assertEquals(getDeleteSql_noPrimaryKey(), result.get(1).getSqlScript());

		Map<String, Object> deleteCodeResult = (result.get(1)).getVirtualEntity();
		assertEquals(3, ((List<Map<String,Object>>)deleteCodeResult.get("columns")).size());
		assertEquals("-", selectCodeResult.get("absoluteVirtualColumnCode"));

		String deleteApiStr = "delete(\"jdbc_tool_test_noPrimaryKey\")\n";
		String deleteWhereParts = ".where(expMap($(\"jdbc_tool_test_noPrimaryKey.StringColumn = :jdbc_tool_test_noPrimaryKey.StringColumn\"), whereParams))\n";
		assertEquals(whereParamsParts + "int count = " + deleteApiStr + deleteWhereParts + ".execute();", result.get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE).toString()));

		String deleteExecuteUKWhereParts = "GRecord whereParams = createRecord();\n"+
				"whereParams.setObject(\"NStringColumn\",);\n"+
				"whereParams.setObject(\"IntColumn\",);\n";
		String deleteExecuteListUKWhereParts = deleteExecuteUKWhereParts +
				"List<GRecord> whereParamsList = new ArrayList<>();\n"+
				"whereParamsList.add(whereParams);\n";

		assertEquals(deleteExecuteUKWhereParts + "\nint count = " + deleteApiStr + ".whereUK(\"unionkey02\", whereParams)\n"+ ".execute();", result.get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_UK).toString()));
		assertEquals(deleteExecuteListUKWhereParts + "\nint count = " + deleteApiStr + ".whereUK(\"unionkey02\", whereParamsList)\n"+ ".executeList();", result.get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_UK).toString()));
		assertEquals(deleteExecuteListUKWhereParts + "\nint count = " + deleteApiStr + ".whereUK(\"unionkey02\", whereParamsList)\n"+ ".executeBatch();", result.get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_UK).toString()));
	}

	public String getDeleteSql_noPrimaryKey() {
		return "DELETE FROM\n"+
				"    " + dbSchema + ".jdbc_tool_test_noPrimaryKey\n"+
				"WHERE\n"+
				"    " + dbSchema + ".jdbc_tool_test_noPrimaryKey.StringColumn = ?";
	}

	@Test
	public void testExport_noPrimaryKey_reg() {

		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_noPrimaryKey_reg.xml");
		ApiRequest apiRequest = parseRequestFromFile(getCommonSetting(),"jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_noPrimaryKey_reg.yaml");
		List<CodeResponse> result = convertVe2Code(apiRequest);
assertEquals(4, result.size());
		assertEquals("INSERT", result.get(0).getType());
		assertEquals("仮想表ID: 1CEB5BB51B76406DAB4025137288D214", result.get(0).getDescription());
		assertEquals("INSERT INTO\n"+
				"    " + dbSchema + ".jdbc_tool_test_noPrimaryKey(StringColumn, NStringColumn, IntColumn)\n"+
				"VALUES\n"+
				"    " + "(?, ?, ?)", result.get(0).getSqlScript());

		Map<String, Object> insertCodeResult = (result.get(0)).getVirtualEntity();
		assertEquals(3, ((List<Map<String,Object>>)insertCodeResult.get("columns")).size());
		assertEquals("-", insertCodeResult.get("absoluteVirtualColumnCode"));

		String insertRecord = "GRecord record = createRecord();\n"+
				"record.setObject(\"StringColumn\",);\n"+
				"record.setObject(\"NStringColumn\",);\n"+
				"record.setObject(\"IntColumn\",);\n";
		String insertExecuteApiStr = "insert(\"jdbc_tool_test_noPrimaryKey\")\n"+ ".values(record)";

		assertEquals(insertRecord + "\nint count = " + insertExecuteApiStr + "\n.execute();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, null).toString()));
		assertNull(result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE).toString()));
		assertNull(result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_PK).toString()));
		assertNull(result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_UK).toString()));

		String insertExecuteBatchApiStr = "insert(\"jdbc_tool_test_noPrimaryKey\")\n"+ ".values(recordList)";
		StringBuilder insertRecordList = new StringBuilder();
		insertRecordList.append("List<GRecord> recordList = new ArrayList<>();").append("\n");
		insertRecordList.append(insertRecord);
		insertRecordList.append("recordList.add(record);").append("\n");

		assertEquals(insertRecordList + "\nint count = " + insertExecuteBatchApiStr + "\n.executeBatch();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, null).toString()));
		assertNull(result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE).toString()));
		assertNull(result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_PK).toString()));
		assertNull(result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_UK).toString()));
		assertEquals(insertRecordList + "\nint count = " + insertExecuteBatchApiStr + "\n.executeList();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, null).toString()));
		assertNull(result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE).toString()));
		assertNull(result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_PK).toString()));
		assertNull(result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_UK).toString()));


		assertEquals("UPDATE", result.get(1).getType());
		assertEquals("仮想表ID: 1CEB5BB51B76406DAB4025137288D214", result.get(1).getDescription());
		assertEquals("UPDATE\n"+
				"    " + dbSchema + ".jdbc_tool_test_noPrimaryKey jdbc_tool_test_noPrimaryKey\n"+
				"SET\n"+
				"    IntColumn = ?\n"+
				"WHERE\n"+
				"    jdbc_tool_test_noPrimaryKey.StringColumn = ?", result.get(1).getSqlScript());

		Map<String, Object> updateCodeResult = (result.get(1)).getVirtualEntity();
		assertEquals(3, ((List<Map<String,Object>>)updateCodeResult.get("columns")).size());
		assertEquals("-", updateCodeResult.get("absoluteVirtualColumnCode"));

		String whereParamsParts = "Map<String, Object> whereParams = new HashMap<>();\n"+
				"whereParams.put(\"jdbc_tool_test_noPrimaryKey.StringColumn\",);\n";
		String whereParamsPartsUKPK = "Object[] whereParams = new Object[]{};\n";
		String updateRecord = "GRecord record = createRecord();\n"+
				"record.setObject(\"IntColumn\",);\n";
		String updateExecuteApiStr = "update(\"jdbc_tool_test_noPrimaryKey\")\n"+ ".set(record)\n";
		String updateExecuteApiWhereStr = ".where(expMap($(\"jdbc_tool_test_noPrimaryKey.StringColumn = :jdbc_tool_test_noPrimaryKey.StringColumn\"), whereParams))\n";
		assertEquals(whereParamsParts + updateRecord + "\nint count = " + updateExecuteApiStr + updateExecuteApiWhereStr + ".execute();", result.get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE).toString()));
		assertEquals(updateRecord + "\nint count = " + updateExecuteApiStr + ".whereUK(\"unionkey02\")\n"+ ".execute();", result.get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_UK).toString()));

		StringBuilder updateRecordList = new StringBuilder();
		updateRecordList.append("List<GRecord> recordList = new ArrayList<>();").append("\n");
		updateRecordList.append(updateRecord);
		updateRecordList.append("recordList.add(record);").append("\n");

		String updateExecuteListApiStr = "update(\"jdbc_tool_test_noPrimaryKey\")\n"+ ".set(recordList)\n";

		assertEquals(updateRecordList + "\nint count = " + updateExecuteListApiStr + ".whereUK(\"unionkey02\")\n"+ ".executeList();", result.get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_UK).toString()));
		assertEquals(updateRecordList + "\nint count = " + updateExecuteListApiStr + ".whereUK(\"unionkey02\")\n"+ ".executeBatch();", result.get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_UK).toString()));

		assertEquals("SELECT", result.get(2).getType());
		assertEquals("仮想表ID: 1CEB5BB51B76406DAB4025137288D214", result.get(2).getDescription());
		assertEquals("SELECT\n"+
				"    jdbc_tool_test_noPrimaryKey.StringColumn,\n"+
				"    jdbc_tool_test_noPrimaryKey.NStringColumn,\n"+
				"    jdbc_tool_test_noPrimaryKey.IntColumn\n"+
				"FROM\n"+
				"    " + dbSchema + ".jdbc_tool_test_noPrimaryKey jdbc_tool_test_noPrimaryKey\n"+
				"WHERE\n"+
				"    jdbc_tool_test_noPrimaryKey.StringColumn = ?", result.get(2).getSqlScript());

		Map<String, Object> selectCodeResult = (result.get(2)).getVirtualEntity();
		assertEquals(3, ((List<Map<String,Object>>)selectCodeResult.get("columns")).size());
		assertEquals("-", selectCodeResult.get("absoluteVirtualColumnCode"));

		String selectApiStr = "select(\"jdbc_tool_test_noPrimaryKey\", \"jdbc_tool_test_noPrimaryKey\")\n"+
				".fields(colsAll())\n";
		String selectApiWhereStr = ".where(expMap($(\"jdbc_tool_test_noPrimaryKey.StringColumn = :jdbc_tool_test_noPrimaryKey.StringColumn\"), whereParams))\n";
		assertEquals(whereParamsParts + "List<GRecord> result = " + selectApiStr + selectApiWhereStr + ".findList();", result.get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE).toString()));
		assertEquals(whereParamsPartsUKPK + "List<GRecord> result = " + selectApiStr + ".whereUK(\"unionkey02\", whereParams)\n"+ ".findList();", result.get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE_UK).toString()));

		assertEquals(whereParamsParts + "GRecord result = " + selectApiStr + selectApiWhereStr + ".findRecord();", result.get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE).toString()));
		assertEquals(whereParamsPartsUKPK + "GRecord result = " + selectApiStr + ".whereUK(\"unionkey02\", whereParams)\n"+ ".findRecord();", result.get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE_UK).toString()));

		assertEquals(whereParamsParts + "GRecordSet result = " + selectApiStr + selectApiWhereStr + ".findRecordSet();", result.get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE).toString()));
		assertEquals(whereParamsPartsUKPK + "GRecordSet result = " + selectApiStr + ".whereUK(\"unionkey02\", whereParams)\n"+ ".findRecordSet();", result.get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE_UK).toString()));

		assertEquals("DELETE", result.get(3).getType());
		assertEquals("仮想表ID: 1CEB5BB51B76406DAB4025137288D214", result.get(3).getDescription());
		assertEquals(getDeleteSql_noPrimaryKey(), result.get(3).getSqlScript());

		Map<String, Object> deleteCodeResult = (result.get(3)).getVirtualEntity();
		assertEquals(3, ((List<Map<String,Object>>)deleteCodeResult.get("columns")).size());
		assertEquals("-", selectCodeResult.get("absoluteVirtualColumnCode"));

		String deleteApiStr = "delete(\"jdbc_tool_test_noPrimaryKey\")\n";
		String deleteApiWhereStr = ".where(expMap($(\"jdbc_tool_test_noPrimaryKey.StringColumn = :jdbc_tool_test_noPrimaryKey.StringColumn\"), whereParams))\n";
		assertEquals(whereParamsParts + "int count = " + deleteApiStr + deleteApiWhereStr + ".execute();", result.get(3).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE).toString()));


		String deleteRecordStr = "GRecord whereParams = createRecord();\n"+
				"whereParams.setObject(\"NStringColumn\",);\n"+
				"whereParams.setObject(\"IntColumn\",);\n";

		assertEquals(deleteRecordStr + "\nint count = " + deleteApiStr + ".whereUK(\"unionkey02\", whereParams)\n"+ ".execute();", result.get(3).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_UK).toString()));

		StringBuilder deleteListRecordStr = new StringBuilder();
		deleteListRecordStr.append(deleteRecordStr);
		deleteListRecordStr.append("List<GRecord> whereParamsList = new ArrayList<>();").append("\n");
		deleteListRecordStr.append("whereParamsList.add(whereParams);").append("\n");

		assertEquals(deleteListRecordStr + "\nint count = " + deleteApiStr + ".whereUK(\"unionkey02\", whereParamsList)\n"+ ".executeList();", result.get(3).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_UK).toString()));
		assertEquals(deleteListRecordStr + "\nint count = " + deleteApiStr + ".whereUK(\"unionkey02\", whereParamsList)\n"+ ".executeBatch();", result.get(3).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_UK).toString()));
	}

	@Test
	public void testExport_noUnionKey_inq() {

		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_noUnionKey_inq.xml");
		ApiRequest apiRequest = parseRequestFromFile(getCommonSetting(),"jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_noUnionKey_inq.yaml");
		List<CodeResponse> result = convertVe2Code(apiRequest);
assertEquals(2, result.size());
		assertEquals("SELECT", result.get(0).getType());
		assertEquals("仮想表ID: 4BF0513B4F0649B48379DF6F65E4074D", result.get(0).getDescription());
		assertEquals("SELECT\n"+
				"    jdbc_tool_test_noUnionKey.objectID,\n"+
				"    jdbc_tool_test_noUnionKey.StringColumn,\n"+
				"    jdbc_tool_test_noUnionKey.NStringColumn,\n"+
				"    jdbc_tool_test_noUnionKey.IntColumn\n"+
				"FROM\n"+
				"    " + dbSchema + ".jdbc_tool_test_noUnionKey jdbc_tool_test_noUnionKey\n"+
				"WHERE\n"+
				"    jdbc_tool_test_noUnionKey.StringColumn = ?", result.get(0).getSqlScript());

		Map<String, Object> selectCodeResult = (result.get(0)).getVirtualEntity();
		assertEquals(4, ((List<Map<String,Object>>)selectCodeResult.get("columns")).size());
		assertEquals("-", selectCodeResult.get("absoluteVirtualColumnCode"));

		String whereParamsParts = "Map<String, Object> whereParams = new HashMap<>();\n"+
				"whereParams.put(\"jdbc_tool_test_noUnionKey.StringColumn\",);\n";
		String whereParamsPartsUKPK = "Object[] whereParams = new Object[]{};\n";
		String selectApiStr = "select(\"jdbc_tool_test_noUnionKey\", \"jdbc_tool_test_noUnionKey\")\n"+ ".fields(colsAll())\n";
		String selectWhereParts = ".where(expMap($(\"jdbc_tool_test_noUnionKey.StringColumn = :jdbc_tool_test_noUnionKey.StringColumn\"), whereParams))\n";

		assertEquals(whereParamsParts + "List<GRecord> result = " + selectApiStr + selectWhereParts + ".findList();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE).toString()));
		assertEquals(whereParamsPartsUKPK + "List<GRecord> result = " + selectApiStr + ".wherePK(whereParams)\n"+ ".findList();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE_PK).toString()));
		assertEquals(whereParamsParts + "GRecord result = " + selectApiStr + selectWhereParts + ".findRecord();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE).toString()));
		assertEquals(whereParamsPartsUKPK + "GRecord result = " + selectApiStr + ".wherePK(whereParams)\n"+ ".findRecord();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE_PK).toString()));
		assertEquals(whereParamsParts + "GRecordSet result = " + selectApiStr + selectWhereParts + ".findRecordSet();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE).toString()));
		assertEquals(whereParamsPartsUKPK + "GRecordSet result = " + selectApiStr + ".wherePK(whereParams)\n"+ ".findRecordSet();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE_PK).toString()));

		assertEquals("DELETE", result.get(1).getType());
		assertEquals("仮想表ID: 4BF0513B4F0649B48379DF6F65E4074D", result.get(1).getDescription());
		assertEquals(getDeleteSql_noUnionKey(), result.get(1).getSqlScript());

		Map<String, Object> deleteCodeResult = (result.get(1)).getVirtualEntity();
		assertEquals(4, ((List<Map<String,Object>>)deleteCodeResult.get("columns")).size());
		assertEquals("-", selectCodeResult.get("absoluteVirtualColumnCode"));

		String deleteApiStr = "delete(\"jdbc_tool_test_noUnionKey\")\n";
		String deleteWhereParts = ".where(expMap($(\"jdbc_tool_test_noUnionKey.StringColumn = :jdbc_tool_test_noUnionKey.StringColumn\"), whereParams))\n";
		assertEquals(whereParamsParts + "int count = " + deleteApiStr + deleteWhereParts + ".execute();", result.get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE).toString()));

		String deleteExecutePKWhereParts = "GRecord whereParams = createRecord();\n"+
				"whereParams.setObject(\"objectID\",);\n";
		String deleteExecuteListUKWhereParts = deleteExecutePKWhereParts +
				"List<GRecord> whereParamsList = new ArrayList<>();\n"+
				"whereParamsList.add(whereParams);\n";

		assertEquals(deleteExecutePKWhereParts + "\nint count = " + deleteApiStr + ".wherePK(whereParams)\n"+ ".execute();", result.get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_PK).toString()));
		assertEquals(deleteExecuteListUKWhereParts + "\nint count = " + deleteApiStr + ".wherePK(whereParamsList)\n"+ ".executeList();", result.get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_PK).toString()));
		assertEquals(deleteExecuteListUKWhereParts + "\nint count = " + deleteApiStr + ".wherePK(whereParamsList)\n"+ ".executeBatch();", result.get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_PK).toString()));
	}

	public String getDeleteSql_noUnionKey() {
		return "DELETE FROM\n"+
				"    " + dbSchema + ".jdbc_tool_test_noUnionKey\n"+
				"WHERE\n"+
				"    " + dbSchema + ".jdbc_tool_test_noUnionKey.StringColumn = ?";
	}

	@Test
	public void testExport_noUnionKey_reg() {

		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_noUnionKey_reg.xml");
		ApiRequest apiRequest = parseRequestFromFile(getCommonSetting(),"jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_noUnionKey_reg.yaml");
		List<CodeResponse> result = convertVe2Code(apiRequest);
assertEquals(4, result.size());
		assertEquals("INSERT", result.get(0).getType());
		assertEquals("仮想表ID: 7E2B27F2FBA7416186A748E378426E1B", result.get(0).getDescription());
		assertEquals("INSERT INTO\n"+
				"    " + dbSchema + ".jdbc_tool_test_noUnionKey(objectID, StringColumn, NStringColumn, IntColumn)\n"+
				"VALUES\n"+
				"    " + "(?, ?, ?, ?)", result.get(0).getSqlScript());

		Map<String, Object> insertCodeResult = (result.get(0)).getVirtualEntity();
		assertEquals(4, ((List<Map<String,Object>>)insertCodeResult.get("columns")).size());
		assertEquals("-", insertCodeResult.get("absoluteVirtualColumnCode"));

		String insertRecord = "GRecord record = createRecord();\n"+
				"record.setObject(\"objectID\",);\n"+
				"record.setObject(\"StringColumn\",);\n"+
				"record.setObject(\"NStringColumn\",);\n"+
				"record.setObject(\"IntColumn\",);\n";
		String insertExecuteApiStr = "insert(\"jdbc_tool_test_noUnionKey\")\n"+  ".values(record)";

		assertEquals(insertRecord + "\nint count = " + insertExecuteApiStr + "\n.execute();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, null).toString()));
		assertNull(result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE).toString()));
		assertNull(result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_PK).toString()));
		assertNull(result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_UK).toString()));

		String insertExecuteBatchApiStr = "insert(\"jdbc_tool_test_noUnionKey\")\n"+ ".values(recordList)";
		StringBuilder insertRecordList = new StringBuilder();
		insertRecordList.append("List<GRecord> recordList = new ArrayList<>();").append("\n");
		insertRecordList.append(insertRecord);
		insertRecordList.append("recordList.add(record);").append("\n");

		assertEquals(insertRecordList + "\nint count = " + insertExecuteBatchApiStr + "\n.executeBatch();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, null).toString()));
		assertNull(result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE).toString()));
		assertNull(result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_PK).toString()));
		assertNull(result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_UK).toString()));
		assertEquals(insertRecordList + "\nint count = " + insertExecuteBatchApiStr + "\n.executeList();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, null).toString()));
		assertNull(result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE).toString()));
		assertNull(result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_PK).toString()));
		assertNull(result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_UK).toString()));


		assertEquals("UPDATE", result.get(1).getType());
		assertEquals("仮想表ID: 7E2B27F2FBA7416186A748E378426E1B", result.get(1).getDescription());
		assertEquals("UPDATE\n"+
				"    " + dbSchema + ".jdbc_tool_test_noUnionKey jdbc_tool_test_noUnionKey\n"+
				"SET\n"+
				"    StringColumn = ?,\n"+
				"    NStringColumn = ?,\n"+
				"    IntColumn = ?\n"+
				"WHERE\n"+
				"    jdbc_tool_test_noUnionKey.StringColumn = ?", result.get(1).getSqlScript());

		Map<String, Object> updateCodeResult = (result.get(1)).getVirtualEntity();
		assertEquals(4, ((List<Map<String,Object>>)updateCodeResult.get("columns")).size());
		assertEquals("-", updateCodeResult.get("absoluteVirtualColumnCode"));

		String whereParamsParts = "Map<String, Object> whereParams = new HashMap<>();\n"+
				"whereParams.put(\"jdbc_tool_test_noUnionKey.StringColumn\",);\n";
		String whereParamsPartsUKPK = "Object[] whereParams = new Object[]{};\n";
		String updateRecord = "GRecord record = createRecord();\n"+
				"record.setObject(\"StringColumn\",);\n"+
				"record.setObject(\"NStringColumn\",);\n"+
				"record.setObject(\"IntColumn\",);\n";
		String updateExecuteApiStr = "update(\"jdbc_tool_test_noUnionKey\")\n"+ ".set(record)\n";
		String updateExecuteApiWhereStr = ".where(expMap($(\"jdbc_tool_test_noUnionKey.StringColumn = :jdbc_tool_test_noUnionKey.StringColumn\"), whereParams))\n";
		assertEquals(whereParamsParts + updateRecord + "\nint count = " + updateExecuteApiStr + updateExecuteApiWhereStr + ".execute();", result.get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE).toString()));
		assertEquals(updateRecord + "\nint count = " + updateExecuteApiStr + ".wherePK()\n"+ ".execute();", result.get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_PK).toString()));
		assertNull(result.get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_UK).toString()));

		StringBuilder updateRecordList = new StringBuilder();
		updateRecordList.append("List<GRecord> recordList = new ArrayList<>();").append("\n");
		updateRecordList.append(updateRecord);
		updateRecordList.append("recordList.add(record);").append("\n");

		String updateExecuteListApiStr = "update(\"jdbc_tool_test_noUnionKey\")\n"+ ".set(recordList)\n";

		assertEquals(updateRecordList + "\nint count = " + updateExecuteListApiStr + ".wherePK()\n"+ ".executeList();", result.get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_PK).toString()));
		assertEquals(updateRecordList + "\nint count = " + updateExecuteListApiStr + ".wherePK()\n"+ ".executeBatch();", result.get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_PK).toString()));

		assertEquals("SELECT", result.get(2).getType());
		assertEquals("仮想表ID: 7E2B27F2FBA7416186A748E378426E1B", result.get(2).getDescription());
		assertEquals("SELECT\n"+
				"    jdbc_tool_test_noUnionKey.objectID,\n"+
				"    jdbc_tool_test_noUnionKey.StringColumn,\n"+
				"    jdbc_tool_test_noUnionKey.NStringColumn,\n"+
				"    jdbc_tool_test_noUnionKey.IntColumn\n"+
				"FROM\n"+
				"    " + dbSchema + ".jdbc_tool_test_noUnionKey jdbc_tool_test_noUnionKey\n"+
				"WHERE\n"+
				"    jdbc_tool_test_noUnionKey.StringColumn = ?", result.get(2).getSqlScript());

		Map<String, Object> selectCodeResult = (result.get(2)).getVirtualEntity();
		assertEquals(4, ((List<Map<String,Object>>)selectCodeResult.get("columns")).size());
		assertEquals("-", selectCodeResult.get("absoluteVirtualColumnCode"));

		String selectApiStr = "select(\"jdbc_tool_test_noUnionKey\", \"jdbc_tool_test_noUnionKey\")\n"+
				".fields(colsAll())\n";
		String selectApiWhereStr = ".where(expMap($(\"jdbc_tool_test_noUnionKey.StringColumn = :jdbc_tool_test_noUnionKey.StringColumn\"), whereParams))\n";
		assertEquals(whereParamsParts + "List<GRecord> result = " + selectApiStr + selectApiWhereStr + ".findList();", result.get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE).toString()));
		assertEquals(whereParamsPartsUKPK + "List<GRecord> result = " + selectApiStr + ".wherePK(whereParams)\n"+ ".findList();", result.get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE_PK).toString()));
		assertNull(result.get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE_UK).toString()));

		assertEquals(whereParamsParts + "GRecord result = " + selectApiStr + selectApiWhereStr + ".findRecord();", result.get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE).toString()));
		assertEquals(whereParamsPartsUKPK + "GRecord result = " + selectApiStr + ".wherePK(whereParams)\n"+ ".findRecord();", result.get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE_PK).toString()));
		assertNull(result.get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE_UK).toString()));

		assertEquals(whereParamsParts + "GRecordSet result = " + selectApiStr + selectApiWhereStr + ".findRecordSet();", result.get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE).toString()));
		assertEquals(whereParamsPartsUKPK + "GRecordSet result = " + selectApiStr + ".wherePK(whereParams)\n"+ ".findRecordSet();", result.get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE_PK).toString()));
		assertNull(result.get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE_UK).toString()));

		assertEquals("DELETE", result.get(3).getType());
		assertEquals("仮想表ID: 7E2B27F2FBA7416186A748E378426E1B", result.get(3).getDescription());
		assertEquals(getDeleteSql_noUnionKey(), result.get(3).getSqlScript());

		Map<String, Object> deleteCodeResult = (result.get(3)).getVirtualEntity();
		assertEquals(4, ((List<Map<String,Object>>)deleteCodeResult.get("columns")).size());
		assertEquals("-", selectCodeResult.get("absoluteVirtualColumnCode"));

		String deleteApiStr = "delete(\"jdbc_tool_test_noUnionKey\")\n";
		String deleteApiWhereStr = ".where(expMap($(\"jdbc_tool_test_noUnionKey.StringColumn = :jdbc_tool_test_noUnionKey.StringColumn\"), whereParams))\n";
		assertEquals(whereParamsParts + "int count = " + deleteApiStr + deleteApiWhereStr + ".execute();", result.get(3).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE).toString()));


		String deleteRecordStr = "GRecord whereParams = createRecord();\n"+
				"whereParams.setObject(\"objectID\",);\n";

		assertEquals(deleteRecordStr + "\nint count = " + deleteApiStr + ".wherePK(whereParams)\n"+ ".execute();", result.get(3).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_PK).toString()));
		assertNull(result.get(3).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_UK).toString()));

		StringBuilder deleteListRecordStr = new StringBuilder();
		deleteListRecordStr.append(deleteRecordStr);
		deleteListRecordStr.append("List<GRecord> whereParamsList = new ArrayList<>();").append("\n");
		deleteListRecordStr.append("whereParamsList.add(whereParams);").append("\n");

		assertNull(result.get(3).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE).toString()));
		assertEquals(deleteListRecordStr + "\nint count = " + deleteApiStr + ".wherePK(whereParamsList)\n"+ ".executeList();", result.get(3).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_PK).toString()));
		assertNull(result.get(3).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_UK).toString()));
		assertNull(result.get(3).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE).toString()));
		assertEquals(deleteListRecordStr + "\nint count = " + deleteApiStr + ".wherePK(whereParamsList)\n"+ ".executeBatch();", result.get(3).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_PK).toString()));
		assertNull(result.get(3).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_UK).toString()));
	}

	@Test
	public void testExport_noPrimaryKeyAndUnionKey_inq() {

		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_noPrimaryKeyAndUnionKey_inq.xml");
		ApiRequest apiRequest = parseRequestFromFile(getCommonSetting(),"jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_noPrimaryKeyAndUnionKey_inq.yaml");
		List<CodeResponse> result = convertVe2Code(apiRequest);
assertEquals(2, result.size());
		assertEquals("SELECT", result.get(0).getType());
		assertEquals("仮想表ID: A5F7CD6A13214BC5B5CB65CD9E81CABB", result.get(0).getDescription());
		assertEquals("SELECT\n"+
				"    jdbc_tool_test_noPrimaryKeyAndUnionKey.StringColumn,\n"+
				"    jdbc_tool_test_noPrimaryKeyAndUnionKey.NStringColumn,\n"+
				"    jdbc_tool_test_noPrimaryKeyAndUnionKey.IntColumn\n"+
				"FROM\n"+
				"    " + dbSchema + ".jdbc_tool_test_noPrimaryKeyAndUnionKey jdbc_tool_test_noPrimaryKeyAndUnionKey\n"+
				"WHERE\n"+
				"    jdbc_tool_test_noPrimaryKeyAndUnionKey.StringColumn = ?", result.get(0).getSqlScript());

		Map<String, Object> selectCodeResult = (result.get(0)).getVirtualEntity();
		assertEquals(3, ((List<Map<String,Object>>)selectCodeResult.get("columns")).size());
		assertEquals("-", selectCodeResult.get("absoluteVirtualColumnCode"));

		String whereParamsParts = "Map<String, Object> whereParams = new HashMap<>();\n"+
				"whereParams.put(\"jdbc_tool_test_noPrimaryKeyAndUnionKey.StringColumn\",);\n";
		String selectApiStr = "select(\"jdbc_tool_test_noPrimaryKeyAndUnionKey\", \"jdbc_tool_test_noPrimaryKeyAndUnionKey\")\n"+ ".fields(colsAll())\n";
		String selectWhereParts = ".where(expMap($(\"jdbc_tool_test_noPrimaryKeyAndUnionKey.StringColumn = :jdbc_tool_test_noPrimaryKeyAndUnionKey.StringColumn\"), whereParams))\n";

		assertEquals(whereParamsParts + "List<GRecord> result = " + selectApiStr + selectWhereParts + ".findList();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE).toString()));
		assertNull(result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE_PK).toString()));
		assertNull(result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE_UK).toString()));
		assertEquals(whereParamsParts + "GRecord result = " + selectApiStr + selectWhereParts + ".findRecord();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE).toString()));
		assertNull(result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE_PK).toString()));
		assertNull(result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE_UK).toString()));
		assertEquals(whereParamsParts + "GRecordSet result = " + selectApiStr + selectWhereParts + ".findRecordSet();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE).toString()));
		assertNull(result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE_PK).toString()));
		assertNull(result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE_UK).toString()));

		assertEquals("DELETE", result.get(1).getType());
		assertEquals("仮想表ID: A5F7CD6A13214BC5B5CB65CD9E81CABB", result.get(1).getDescription());
		assertEquals(getDeleteSql_noPrimaryKeyAndUnionKey(), result.get(1).getSqlScript());

		Map<String, Object> deleteCodeResult = (result.get(1)).getVirtualEntity();
		assertEquals(3, ((List<Map<String,Object>>)deleteCodeResult.get("columns")).size());
		assertEquals("-", selectCodeResult.get("absoluteVirtualColumnCode"));

		String deleteApiStr = "delete(\"jdbc_tool_test_noPrimaryKeyAndUnionKey\")\n";
		String deleteWhereParts = ".where(expMap($(\"jdbc_tool_test_noPrimaryKeyAndUnionKey.StringColumn = :jdbc_tool_test_noPrimaryKeyAndUnionKey.StringColumn\"), whereParams))\n";
		assertEquals(whereParamsParts + "int count = " + deleteApiStr + deleteWhereParts + ".execute();", result.get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE).toString()));
		assertNull(result.get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_PK).toString()));
		assertNull(result.get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_UK).toString()));
		assertNull(result.get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_PK).toString()));
		assertNull(result.get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE).toString()));
		assertNull(result.get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_UK).toString()));
		assertNull(result.get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE).toString()));
		assertNull(result.get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_PK).toString()));
		assertNull(result.get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_UK).toString()));
	}

	public String getDeleteSql_noPrimaryKeyAndUnionKey() {
		return "DELETE FROM\n"+
				"    " + dbSchema + ".jdbc_tool_test_noPrimaryKeyAndUnionKey\n"+
				"WHERE\n"+
				"    " + dbSchema + ".jdbc_tool_test_noPrimaryKeyAndUnionKey.StringColumn = ?";
	}

	@Test
	public void testExport_noPrimaryKeyAndUnionKey_reg() {

		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_noPrimaryKeyAndUnionKey_reg.xml");
		ApiRequest apiRequest = parseRequestFromFile(getCommonSetting(),"jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_noPrimaryKeyAndUnionKey_reg.yaml");
		List<CodeResponse> result = convertVe2Code(apiRequest);
		assertEquals(3, result.size());
		assertEquals("INSERT", result.get(0).getType());
		assertEquals("仮想表ID: 6B0D549134F341549F85B1603947BAE4", result.get(0).getDescription());
		assertEquals("INSERT INTO\n"+
				"    " + dbSchema + ".jdbc_tool_test_noPrimaryKeyAndUnionKey(StringColumn, NStringColumn, IntColumn)\n"+
				"VALUES\n"+
				"    " + "(?, ?, ?)", result.get(0).getSqlScript());

		Map<String, Object> insertCodeResult = (result.get(0)).getVirtualEntity();
		assertEquals(3, ((List<Map<String,Object>>)insertCodeResult.get("columns")).size());
		assertEquals("-", insertCodeResult.get("absoluteVirtualColumnCode"));

		String insertRecord = "GRecord record = createRecord();\n"+
				"record.setObject(\"StringColumn\",);\n"+
				"record.setObject(\"NStringColumn\",);\n"+
				"record.setObject(\"IntColumn\",);\n";
		String insertExecuteApiStr = "insert(\"jdbc_tool_test_noPrimaryKeyAndUnionKey\")\n"+ ".values(record)";

		assertEquals(insertRecord + "\nint count = " + insertExecuteApiStr + "\n.execute();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, null).toString()));
		assertNull(result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE).toString()));
		assertNull(result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_PK).toString()));
		assertNull(result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_UK).toString()));
		assertNull(result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE).toString()));
		assertNull(result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_PK).toString()));
		assertNull(result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_UK).toString()));
		assertNull(result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE).toString()));
		assertNull(result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_PK).toString()));
		assertNull(result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_UK).toString()));

		assertEquals("SELECT", result.get(1).getType());
		assertEquals("仮想表ID: 6B0D549134F341549F85B1603947BAE4", result.get(1).getDescription());
		assertEquals("SELECT\n"+
				"    jdbc_tool_test_noPrimaryKeyAndUnionKey.StringColumn,\n"+
				"    jdbc_tool_test_noPrimaryKeyAndUnionKey.NStringColumn,\n"+
				"    jdbc_tool_test_noPrimaryKeyAndUnionKey.IntColumn\n"+
				"FROM\n"+
				"    " + dbSchema + ".jdbc_tool_test_noPrimaryKeyAndUnionKey jdbc_tool_test_noPrimaryKeyAndUnionKey\n"+
				"WHERE\n"+
				"    jdbc_tool_test_noPrimaryKeyAndUnionKey.StringColumn = ?", result.get(1).getSqlScript());

		Map<String, Object> selectCodeResult = (result.get(1)).getVirtualEntity();
		assertEquals(3, ((List<Map<String,Object>>)selectCodeResult.get("columns")).size());
		assertEquals("-", selectCodeResult.get("absoluteVirtualColumnCode"));

		String whereParamsParts = "Map<String, Object> whereParams = new HashMap<>();\n"+
				"whereParams.put(\"jdbc_tool_test_noPrimaryKeyAndUnionKey.StringColumn\",);\n";
		String selectApiStr = "select(\"jdbc_tool_test_noPrimaryKeyAndUnionKey\", \"jdbc_tool_test_noPrimaryKeyAndUnionKey\")\n"+
				".fields(colsAll())\n";
		String selectApiWhereStr = ".where(expMap($(\"jdbc_tool_test_noPrimaryKeyAndUnionKey.StringColumn = :jdbc_tool_test_noPrimaryKeyAndUnionKey.StringColumn\"), whereParams))\n";

		assertEquals(whereParamsParts + "List<GRecord> result = " + selectApiStr + selectApiWhereStr + ".findList();", result.get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE).toString()));
		assertNull(result.get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE_PK).toString()));
		assertNull(result.get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE_UK).toString()));
		assertEquals(whereParamsParts + "GRecord result = " + selectApiStr + selectApiWhereStr + ".findRecord();", result.get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE).toString()));
		assertNull(result.get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE_PK).toString()));
		assertNull(result.get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE_UK).toString()));
		assertEquals(whereParamsParts + "GRecordSet result = " + selectApiStr + selectApiWhereStr + ".findRecordSet();", result.get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE).toString()));
		assertNull(result.get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE_PK).toString()));
		assertNull(result.get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE_UK).toString()));

		assertEquals("DELETE", result.get(2).getType());
		assertEquals("仮想表ID: 6B0D549134F341549F85B1603947BAE4", result.get(2).getDescription());
		assertEquals(getDeleteSql_noPrimaryKeyAndUnionKey(), result.get(2).getSqlScript());

		Map<String, Object> deleteCodeResult = (result.get(2)).getVirtualEntity();
		assertEquals(3, ((List<Map<String,Object>>)deleteCodeResult.get("columns")).size());
		assertEquals("-", selectCodeResult.get("absoluteVirtualColumnCode"));

		String deleteApiStr = "delete(\"jdbc_tool_test_noPrimaryKeyAndUnionKey\")\n";
		String deleteApiWhereStr = ".where(expMap($(\"jdbc_tool_test_noPrimaryKeyAndUnionKey.StringColumn = :jdbc_tool_test_noPrimaryKeyAndUnionKey.StringColumn\"), whereParams))\n";
		assertEquals(whereParamsParts + "int count = " + deleteApiStr + deleteApiWhereStr + ".execute();", result.get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE).toString()));
		assertNull(result.get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_PK).toString()));
		assertNull(result.get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_UK).toString()));


		assertNull(result.get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE).toString()));
		assertNull(result.get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_PK).toString()));
		assertNull(result.get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_UK).toString()));
		assertNull(result.get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE).toString()));
		assertNull(result.get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_PK).toString()));
		assertNull(result.get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_UK).toString()));
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
		assertEquals("SELECT\n"+
				"    jdbc_tool_test_pk_uk_mainmaster.objectID,\n"+
				"    jdbc_tool_test_pk_uk_mainmaster.ItemCD,\n"+
				"    jdbc_tool_test_pk_uk_mainmaster.ItemNA,\n"+
				"    jdbc_tool_test_pk_uk_mainmaster.UnitPrice,\n"+
				"    jdbc_tool_test_pk_uk_mainmaster.SellPrice,\n"+
				"    jdbc_tool_test_pk_uk_mainmaster.Flag,\n"+
				"    jdbc_tool_test_pk_uk_mainmaster.PriceFlag,\n"+
				"    jdbc_tool_test_pk_uk_mainmaster.ExclusiveFG,\n"+
				"    jdbc_tool_test_pk_uk_mainmaster.RegisteredDT,\n"+
				"    jdbc_tool_test_pk_uk_mainmaster.UpdatedDT\n"+
				"FROM\n"+
				"    " + dbSchema + ".jdbc_tool_test_pk_uk_mainmaster jdbc_tool_test_pk_uk_mainmaster\n"+
				"    LEFT OUTER JOIN gef_jdbc_tool.jdbc_tool_test_unitmaster jdbc_tool_test_unitmaster ON jdbc_tool_test_pk_uk_mainmaster.objectID = jdbc_tool_test_unitmaster.Code\n" +
				"    INNER JOIN gef_jdbc_tool.jdbc_tool_test_mainmaster jdbc_tool_test_mainmaster ON jdbc_tool_test_pk_uk_mainmaster.objectID = jdbc_tool_test_mainmaster.objectID\n" +
				"    RIGHT OUTER JOIN gef_jdbc_tool.jdbc_tool_test_ordermaster jdbc_tool_test_ordermaster ON jdbc_tool_test_pk_uk_mainmaster.ItemCD = jdbc_tool_test_ordermaster.objectID\n"+
				"WHERE\n"+
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
	public void testExport_join_mix_useForeignKeyDefault() {
//		doCustomConfig(getUseForeignKeyDefaultConfigPath());
		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_join_mix.xml");
		SettingRequest setting = getCommonSetting();
		setting.getTransform().put("useExpMap", false);
		ApiRequest apiRequest = parseRequestFromFile(setting,"jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_join_mix.yaml");

		List<CodeResponse> result = convertVe2Code(apiRequest);

		assertEquals("SELECT", result.get(0).getType());
		assertEquals("仮想表ID: E44ADEF7B30549449EDF193ECFB66631", result.get(0).getDescription());
		assertEquals("SELECT\n"+
				"    jdbc_tool_test_pk_uk_mainmaster.objectID,\n"+
				"    jdbc_tool_test_pk_uk_mainmaster.ItemCD,\n"+
				"    jdbc_tool_test_pk_uk_mainmaster.ItemNA,\n"+
				"    jdbc_tool_test_pk_uk_mainmaster.UnitPrice,\n"+
				"    jdbc_tool_test_pk_uk_mainmaster.SellPrice,\n"+
				"    jdbc_tool_test_pk_uk_mainmaster.Flag,\n"+
				"    jdbc_tool_test_pk_uk_mainmaster.PriceFlag,\n"+
				"    jdbc_tool_test_pk_uk_mainmaster.ExclusiveFG,\n"+
				"    jdbc_tool_test_pk_uk_mainmaster.RegisteredDT,\n"+
				"    jdbc_tool_test_pk_uk_mainmaster.UpdatedDT\n"+
				"FROM\n"+
				"    " + dbSchema + ".jdbc_tool_test_pk_uk_mainmaster jdbc_tool_test_pk_uk_mainmaster\n"+
				"    LEFT OUTER JOIN gef_jdbc_tool.jdbc_tool_test_unitmaster jdbc_tool_test_unitmaster ON jdbc_tool_test_pk_uk_mainmaster.objectID = jdbc_tool_test_unitmaster.Code\n" +
				"    INNER JOIN gef_jdbc_tool.jdbc_tool_test_mainmaster jdbc_tool_test_mainmaster ON jdbc_tool_test_pk_uk_mainmaster.objectID = jdbc_tool_test_mainmaster.objectID\n" +
				"    RIGHT OUTER JOIN gef_jdbc_tool.jdbc_tool_test_ordermaster jdbc_tool_test_ordermaster ON jdbc_tool_test_pk_uk_mainmaster.ItemCD = jdbc_tool_test_ordermaster.objectID\n"+
				"WHERE\n"+
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
	public void testExport_expMapCase1() {

		// パターン１：where (a = ? and b = ?) or c = ?
		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_expmapcase1.xml");
		ApiRequest apiRequest = parseRequestFromFile(getCommonSetting(),"jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_expmapcase1.yaml");
		List<CodeResponse> result = convertVe2Code(apiRequest);
assertEquals(4, result.size());
		assertEquals("INSERT", result.get(0).getType());
		assertEquals("仮想表ID: 8D0CDDE194344769B42F631F31BAFEFB", result.get(0).getDescription());
		assertEquals("INSERT INTO\n"+
						"    " + dbSchema + ".jdbc_tool_test_fullCaseMainEntity(objectID, StringColumn, IntColumn, NStringColumn, DateTimeColumn, YMColumn, CurrencyColumn, LongColumn, CompanyCD, ExclusiveFG, RegisteredPerson, UpdatedPerson, RegisteredDT, UpdatedDT)\n"+
						"VALUES\n"+
						"    " + "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " + getTimestampCommand() + ", " + getTimestampCommand() + ")"
				, result.get(0).getSqlScript());

		Map<String, Object> insertCodeResult = (result.get(0)).getVirtualEntity();
		assertEquals(14, ((List<Map<String,Object>>)insertCodeResult.get("columns")).size());
		assertEquals("-", insertCodeResult.get("absoluteVirtualColumnCode"));

		String insertRecord = "GRecord record = createRecord();\n"+
				"record.setObject(\"objectID\",);\n"+
				"record.setObject(\"StringColumn\",);\n"+
				"record.setObject(\"IntColumn\",);\n"+
				"record.setObject(\"NStringColumn\",);\n"+
				"record.setObject(\"DateTimeColumn\",);\n"+
				"record.setObject(\"YMColumn\",);\n"+
				"record.setObject(\"CurrencyColumn\",);\n"+
				"record.setObject(\"LongColumn\",);\n"+
				"record.setObject(\"CompanyCD\",);\n"+
				"record.setObject(\"ExclusiveFG\",);\n"+
				"record.setObject(\"RegisteredPerson\",);\n"+
				"record.setObject(\"UpdatedPerson\",);\n"+
				"record.setObject(\"RegisteredDT\",);\n"+
				"record.setObject(\"UpdatedDT\",);\n";
		String insertExecuteApiStr = "insert(\"jdbc_tool_test_fullCaseMainEntity\")\n"+  ".values(record)";

		assertEquals(insertRecord + "\nint count = " + insertExecuteApiStr + "\n.execute();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, null).toString()));
		assertNull(result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE).toString()));
		assertNull(result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_PK).toString()));
		assertNull(result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_UK).toString()));

		String insertExecuteBatchApiStr = "insert(\"jdbc_tool_test_fullCaseMainEntity\")\n"+ ".values(recordList)";
		StringBuilder insertRecordList = new StringBuilder();
		insertRecordList.append("List<GRecord> recordList = new ArrayList<>();").append("\n");
		insertRecordList.append(insertRecord);
		insertRecordList.append("recordList.add(record);").append("\n");

		assertEquals(insertRecordList + "\nint count = " + insertExecuteBatchApiStr + "\n.executeBatch();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, null).toString()));
		assertNull(result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE).toString()));
		assertNull(result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_PK).toString()));
		assertNull(result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_UK).toString()));
		assertEquals(insertRecordList + "\nint count = " + insertExecuteBatchApiStr + "\n.executeList();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, null).toString()));
		assertNull(result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE).toString()));
		assertNull(result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_PK).toString()));
		assertNull(result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_UK).toString()));


		assertEquals("UPDATE", result.get(1).getType());
		assertEquals("仮想表ID: 8D0CDDE194344769B42F631F31BAFEFB", result.get(1).getDescription());
		assertEquals("UPDATE\n"+
				"    " + dbSchema + ".jdbc_tool_test_fullCaseMainEntity jdbc_tool_test_fullCaseMainEntity\n"+
				"SET\n"+
				"    StringColumn = ?,\n"+
				"    IntColumn = ?,\n"+
				"    NStringColumn = ?,\n"+
				"    DateTimeColumn = ?,\n"+
				"    YMColumn = ?,\n"+
				"    CurrencyColumn = ?,\n"+
				"    LongColumn = ?,\n"+
				"    CompanyCD = ?,\n"+
				"    ExclusiveFG = ?,\n"+
				"    RegisteredPerson = ?,\n"+
				"    RegisteredDT = ?,\n"+
				"    UpdatedPerson = ?,\n"+
				"    UpdatedDT = ?\n"+
				"WHERE\n"+
				"    (\n"+
				"        (\n"+
				"            jdbc_tool_test_fullCaseMainEntity.objectID = '1001'\n"+
				"            AND jdbc_tool_test_fullCaseMainEntity.StringColumn = '2002'\n"+
				"        )\n"+
				"        OR jdbc_tool_test_fullCaseMainEntity.IntColumn = 3003\n"+
				"    )\n"+
				"    AND (\n"+
				"        (\n"+
				"            jdbc_tool_test_fullCaseMainEntity.NStringColumn = ?\n"+
				"            AND jdbc_tool_test_fullCaseMainEntity.DateTimeColumn = ?\n"+
				"        )\n"+
				"        OR jdbc_tool_test_fullCaseMainEntity.YMColumn = ?\n"+
				"        AND jdbc_tool_test_fullCaseMainEntity.CompanyCD = ?\n"+
				"    )", result.get(1).getSqlScript());

		Map<String, Object> updateCodeResult = (result.get(1)).getVirtualEntity();
		assertEquals(14, ((List<Map<String,Object>>)updateCodeResult.get("columns")).size());
		assertEquals("-", updateCodeResult.get("absoluteVirtualColumnCode"));

		String whereParamsParts = "Map<String, Object> whereParams = new HashMap<>();\n"+
				"whereParams.put(\"jdbc_tool_test_fullCaseMainEntity.YMColumn\",);\n"+
				"whereParams.put(\"jdbc_tool_test_fullCaseMainEntity.DateTimeColumn\",);\n"+
				"whereParams.put(\"jdbc_tool_test_fullCaseMainEntity.NStringColumn\",);\n"+
				"whereParams.put(\"jdbc_tool_test_fullCaseMainEntity.CompanyCD\",);\n";
		String whereParamsPartsUKPK = "Object[] whereParams = new Object[]{,,,};\n";
		String updateRecord = "GRecord record = createRecord();\n"+
				"record.setObject(\"StringColumn\",);\n"+
				"record.setObject(\"IntColumn\",);\n"+
				"record.setObject(\"NStringColumn\",);\n"+
				"record.setObject(\"DateTimeColumn\",);\n"+
				"record.setObject(\"YMColumn\",);\n"+
				"record.setObject(\"CurrencyColumn\",);\n"+
				"record.setObject(\"LongColumn\",);\n"+
				"record.setObject(\"CompanyCD\",);\n"+
				"record.setObject(\"ExclusiveFG\",);\n"+
				"record.setObject(\"RegisteredPerson\",);\n"+
				"record.setObject(\"RegisteredDT\",);\n"+
				"record.setObject(\"UpdatedPerson\",);\n"+
				"record.setObject(\"UpdatedDT\",);\n";
		String updateExecuteApiStr = "update(\"jdbc_tool_test_fullCaseMainEntity\")\n"+ ".set(record)\n";
		String executeApiWhereStr = ".where(expMap($($($(\"jdbc_tool_test_fullCaseMainEntity.objectID = '1001'\").AND(\"jdbc_tool_test_fullCaseMainEntity.StringColumn = '2002'\")).OR(\"jdbc_tool_test_fullCaseMainEntity.IntColumn = 3003\")).AND($($(\"jdbc_tool_test_fullCaseMainEntity.NStringColumn = :jdbc_tool_test_fullCaseMainEntity.NStringColumn\").AND(\"jdbc_tool_test_fullCaseMainEntity.DateTimeColumn = :jdbc_tool_test_fullCaseMainEntity.DateTimeColumn\")).OR(\"jdbc_tool_test_fullCaseMainEntity.YMColumn = :jdbc_tool_test_fullCaseMainEntity.YMColumn\").AND(\"jdbc_tool_test_fullCaseMainEntity.CompanyCD = $TRIM(:jdbc_tool_test_fullCaseMainEntity.CompanyCD)\")), whereParams))\n";
		assertEquals(whereParamsParts + updateRecord + "\nint count = " + updateExecuteApiStr + executeApiWhereStr + ".execute();", result.get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE).toString()));
		assertEquals(updateRecord + "\nint count = " + updateExecuteApiStr + ".wherePK()\n"+ ".execute();", result.get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_PK).toString()));
		String executeUKStr = ".whereUK(\"MultipleColumnUK\")\n"+ "//.whereUK(\"OtherUKName\")\n"+ "//.whereUK(\"SingleColumnUK\")";
		assertEquals(updateRecord + "\nint count = " + updateExecuteApiStr + executeUKStr + "\n.execute();", result.get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_UK).toString()));

		StringBuilder updateRecordList = new StringBuilder();
		updateRecordList.append("List<GRecord> recordList = new ArrayList<>();").append("\n");
		updateRecordList.append(updateRecord);
		updateRecordList.append("recordList.add(record);").append("\n");

		String updateExecuteListApiStr = "update(\"jdbc_tool_test_fullCaseMainEntity\")\n"+ ".set(recordList)\n";

		assertEquals(updateRecordList + "\nint count = " + updateExecuteListApiStr + ".wherePK()\n"+ ".executeList();", result.get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_PK).toString()));
		assertEquals(updateRecordList + "\nint count = " + updateExecuteListApiStr + executeUKStr + "\n.executeList();", result.get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_UK).toString()));
		assertEquals(updateRecordList + "\nint count = " + updateExecuteListApiStr + ".wherePK()\n"+ ".executeBatch();", result.get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_PK).toString()));
		assertEquals(updateRecordList + "\nint count = " + updateExecuteListApiStr + executeUKStr + "\n.executeBatch();", result.get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_UK).toString()));

		assertEquals("SELECT", result.get(2).getType());
		assertEquals("仮想表ID: 8D0CDDE194344769B42F631F31BAFEFB", result.get(2).getDescription());
		assertEquals("SELECT\n"+
				"    jdbc_tool_test_fullCaseMainEntity.objectID,\n"+
				"    jdbc_tool_test_fullCaseMainEntity.StringColumn,\n"+
				"    jdbc_tool_test_fullCaseMainEntity.IntColumn,\n"+
				"    jdbc_tool_test_fullCaseMainEntity.NStringColumn,\n"+
				"    jdbc_tool_test_fullCaseMainEntity.DateTimeColumn,\n"+
				"    jdbc_tool_test_fullCaseMainEntity.YMColumn,\n"+
				"    jdbc_tool_test_fullCaseMainEntity.CurrencyColumn,\n"+
				"    jdbc_tool_test_fullCaseMainEntity.LongColumn,\n"+
				"    jdbc_tool_test_fullCaseMainEntity.CompanyCD,\n"+
				"    jdbc_tool_test_fullCaseMainEntity.ExclusiveFG,\n"+
				"    jdbc_tool_test_fullCaseMainEntity.RegisteredPerson,\n"+
				"    jdbc_tool_test_fullCaseMainEntity.RegisteredDT,\n"+
				"    jdbc_tool_test_fullCaseMainEntity.UpdatedPerson,\n"+
				"    jdbc_tool_test_fullCaseMainEntity.UpdatedDT\n"+
				"FROM\n"+
				"    " + dbSchema + ".jdbc_tool_test_fullCaseMainEntity jdbc_tool_test_fullCaseMainEntity\n"+
				"WHERE\n"+
				"    (\n"+
				"        (\n"+
				"            jdbc_tool_test_fullCaseMainEntity.objectID = '1001'\n"+
				"            AND jdbc_tool_test_fullCaseMainEntity.StringColumn = '2002'\n"+
				"        )\n"+
				"        OR jdbc_tool_test_fullCaseMainEntity.IntColumn = 3003\n"+
				"    )\n"+
				"    AND (\n"+
				"        (\n"+
				"            jdbc_tool_test_fullCaseMainEntity.NStringColumn = ?\n"+
				"            AND jdbc_tool_test_fullCaseMainEntity.DateTimeColumn = ?\n"+
				"        )\n"+
				"        OR jdbc_tool_test_fullCaseMainEntity.YMColumn = ?\n"+
				"        AND jdbc_tool_test_fullCaseMainEntity.CompanyCD = ?\n"+
				"    )", result.get(2).getSqlScript());

		Map<String, Object> selectCodeResult = (result.get(2)).getVirtualEntity();
		assertEquals(14, ((List<Map<String,Object>>)selectCodeResult.get("columns")).size());
		assertEquals("-", selectCodeResult.get("absoluteVirtualColumnCode"));

		String selectApiStr = "select(\"jdbc_tool_test_fullCaseMainEntity\", \"jdbc_tool_test_fullCaseMainEntity\")\n"+
				".fields(colsAll())\n";
		String selectUKStr = ".whereUK(\"MultipleColumnUK\", whereParams)\n"+ "//.whereUK(\"OtherUKName\", whereParams)\n"+ "//.whereUK(\"SingleColumnUK\", whereParams)";
		assertEquals(whereParamsParts + "List<GRecord> result = " + selectApiStr + executeApiWhereStr + ".findList();", result.get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE).toString()));
		assertEquals(whereParamsPartsUKPK + "List<GRecord> result = " + selectApiStr + ".wherePK(whereParams)\n"+ ".findList();", result.get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE_PK).toString()));
		assertEquals(whereParamsPartsUKPK + "List<GRecord> result = " + selectApiStr + selectUKStr + "\n.findList();", result.get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE_UK).toString()));

		assertEquals(whereParamsParts + "GRecord result = " + selectApiStr + executeApiWhereStr + ".findRecord();", result.get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE).toString()));
		assertEquals(whereParamsPartsUKPK + "GRecord result = " + selectApiStr + ".wherePK(whereParams)\n"+ ".findRecord();", result.get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE_PK).toString()));
		assertEquals(whereParamsPartsUKPK + "GRecord result = " + selectApiStr + selectUKStr + "\n.findRecord();", result.get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE_UK).toString()));

		assertEquals(whereParamsParts + "GRecordSet result = " + selectApiStr + executeApiWhereStr + ".findRecordSet();", result.get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE).toString()));
		assertEquals(whereParamsPartsUKPK + "GRecordSet result = " + selectApiStr + ".wherePK(whereParams)\n"+ ".findRecordSet();", result.get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE_PK).toString()));
		assertEquals(whereParamsPartsUKPK + "GRecordSet result = " + selectApiStr + selectUKStr + "\n.findRecordSet();", result.get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE_UK).toString()));

		assertEquals("DELETE", result.get(3).getType());
		assertEquals("仮想表ID: 8D0CDDE194344769B42F631F31BAFEFB", result.get(3).getDescription());
		assertEquals(getDeleteSql_testExport_expMapCase1(), result.get(3).getSqlScript());

		Map<String, Object> deleteCodeResult = (result.get(3)).getVirtualEntity();
		assertEquals(14, ((List<Map<String,Object>>)deleteCodeResult.get("columns")).size());
		assertEquals("-", selectCodeResult.get("absoluteVirtualColumnCode"));

		String deleteApiStr = "delete(\"jdbc_tool_test_fullCaseMainEntity\")\n";
		assertEquals(whereParamsParts + "int count = " + deleteApiStr + executeApiWhereStr + ".execute();", result.get(3).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE).toString()));

		String deleteRecordStr = "GRecord whereParams = createRecord();\n"+
				"whereParams.setObject(\"objectID\",);\n";

		assertEquals(deleteRecordStr + "\nint count = " + deleteApiStr + ".wherePK(whereParams)\n"+ ".execute();", result.get(3).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_PK).toString()));
		String deleteRecordUKStr = "GRecord whereParams = createRecord();\n"+
				"whereParams.setObject(\"StringColumn\",);\n"+
				"whereParams.setObject(\"IntColumn\",);\n"+
				"whereParams.setObject(\"NStringColumn\",);\n"+
				"//ユーニックキー「OtherUKName」用のカラム\n"+
				"/*whereParams.setObject(\"IntColumn\",);\n"+
				"whereParams.setObject(\"NStringColumn\",);\n"+
				"whereParams.setObject(\"DateTimeColumn\",);\n"+
				"whereParams.setObject(\"YMColumn\",);\n"+
				"*/\n"+
				"//ユーニックキー「SingleColumnUK」用のカラム\n"+
				"/*whereParams.setObject(\"StringColumn\",);\n"+
				"*/\n";

		assertEquals(deleteRecordUKStr + "\nint count = " + deleteApiStr + selectUKStr + "\n.execute();", result.get(3).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_UK).toString()));

		StringBuilder deleteListRecordStr = new StringBuilder();
		deleteListRecordStr.append(deleteRecordStr);
		deleteListRecordStr.append("List<GRecord> whereParamsList = new ArrayList<>();").append("\n");
		deleteListRecordStr.append("whereParamsList.add(whereParams);").append("\n");

		StringBuilder deleteListRecordUKStr = new StringBuilder();
		deleteListRecordUKStr.append(deleteRecordUKStr);
		deleteListRecordUKStr.append("List<GRecord> whereParamsList = new ArrayList<>();").append("\n");
		deleteListRecordUKStr.append("whereParamsList.add(whereParams);").append("\n");
		String deleteListUKStr = ".whereUK(\"MultipleColumnUK\", whereParamsList)\n"+ "//.whereUK(\"OtherUKName\", whereParamsList)\n"+ "//.whereUK(\"SingleColumnUK\", whereParamsList)";

		assertNull(result.get(3).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE).toString()));
		assertEquals(deleteListRecordStr + "\nint count = " + deleteApiStr + ".wherePK(whereParamsList)\n"+ ".executeList();", result.get(3).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_PK).toString()));
		assertEquals(deleteListRecordUKStr + "\nint count = " + deleteApiStr + deleteListUKStr + "\n.executeList();", result.get(3).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_UK).toString()));
		assertNull(result.get(3).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE).toString()));
		assertEquals(deleteListRecordStr + "\nint count = " + deleteApiStr + ".wherePK(whereParamsList)\n"+ ".executeBatch();", result.get(3).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_PK).toString()));
		assertEquals(deleteListRecordUKStr + "\nint count = " + deleteApiStr + deleteListUKStr + "\n.executeBatch();", result.get(3).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_UK).toString()));
	}

	protected String getDeleteSql_testExport_expMapCase1() {
		return "DELETE FROM\n"+
				"    " + dbSchema + ".jdbc_tool_test_fullCaseMainEntity\n"+
				"WHERE\n"+
				"    (\n"+
				"        (\n"+
				"            " + dbSchema + ".jdbc_tool_test_fullCaseMainEntity.objectID = '1001'\n"+
				"            AND " + dbSchema + ".jdbc_tool_test_fullCaseMainEntity.StringColumn = '2002'\n"+
				"        )\n"+
				"        OR " + dbSchema + ".jdbc_tool_test_fullCaseMainEntity.IntColumn = 3003\n"+
				"    )\n"+
				"    AND (\n"+
				"        (\n"+
				"            " + dbSchema + ".jdbc_tool_test_fullCaseMainEntity.NStringColumn = ?\n"+
				"            AND " + dbSchema + ".jdbc_tool_test_fullCaseMainEntity.DateTimeColumn = ?\n"+
				"        )\n"+
				"        OR " + dbSchema + ".jdbc_tool_test_fullCaseMainEntity.YMColumn = ?\n"+
				"        AND " + dbSchema + ".jdbc_tool_test_fullCaseMainEntity.CompanyCD = ?\n"+
				"    )";
	}

	@Test
	public void testExport_expMapCase2() {

		// パターン２：where (a = ? and b = ?) or (c = ? and d = ?)
		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_expmapcase2.xml");
		ApiRequest apiRequest = parseRequestFromFile(getCommonSetting(),"jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_expmapcase2.yaml");
		List<CodeResponse> result = convertVe2Code(apiRequest);
assertEquals("INSERT", result.get(0).getType());
		assertEquals("仮想表ID: 055EB42884BA4FDBA154640B13B4D060", result.get(0).getDescription());
		assertEquals("INSERT INTO\n"+
						"    " + dbSchema + ".jdbc_tool_test_fullCaseMainEntity(objectID, StringColumn, IntColumn, NStringColumn, DateTimeColumn, YMColumn, CurrencyColumn, LongColumn, CompanyCD, ExclusiveFG, RegisteredPerson, UpdatedPerson, RegisteredDT, UpdatedDT)\n"+
						"VALUES\n"+
						"    " + "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " + getTimestampCommand() + ", " + getTimestampCommand() + ")"
				, result.get(0).getSqlScript());

		Map<String, Object> insertCodeResult = (result.get(0)).getVirtualEntity();
		assertEquals(14, ((List<Map<String,Object>>)insertCodeResult.get("columns")).size());
		assertEquals("-", insertCodeResult.get("absoluteVirtualColumnCode"));

		String insertRecord = "GRecord record = createRecord();\n"+
				"record.setObject(\"objectID\",);\n"+
				"record.setObject(\"StringColumn\",);\n"+
				"record.setObject(\"IntColumn\",);\n"+
				"record.setObject(\"NStringColumn\",);\n"+
				"record.setObject(\"DateTimeColumn\",);\n"+
				"record.setObject(\"YMColumn\",);\n"+
				"record.setObject(\"CurrencyColumn\",);\n"+
				"record.setObject(\"LongColumn\",);\n"+
				"record.setObject(\"CompanyCD\",);\n"+
				"record.setObject(\"ExclusiveFG\",);\n"+
				"record.setObject(\"RegisteredPerson\",);\n"+
				"record.setObject(\"UpdatedPerson\",);\n"+
				"record.setObject(\"RegisteredDT\",);\n"+
				"record.setObject(\"UpdatedDT\",);\n";
		String insertExecuteApiStr = "insert(\"jdbc_tool_test_fullCaseMainEntity\")\n"+  ".values(record)";

		assertEquals(insertRecord + "\nint count = " + insertExecuteApiStr + "\n.execute();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, null).toString()));
		assertNull(result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE).toString()));
		assertNull(result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_PK).toString()));
		assertNull(result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_UK).toString()));

		String insertExecuteBatchApiStr = "insert(\"jdbc_tool_test_fullCaseMainEntity\")\n"+ ".values(recordList)";
		StringBuilder insertRecordList = new StringBuilder();
		insertRecordList.append("List<GRecord> recordList = new ArrayList<>();").append("\n");
		insertRecordList.append(insertRecord);
		insertRecordList.append("recordList.add(record);").append("\n");

		assertEquals(insertRecordList + "\nint count = " + insertExecuteBatchApiStr + "\n.executeBatch();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, null).toString()));
		assertNull(result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE).toString()));
		assertNull(result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_PK).toString()));
		assertNull(result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_UK).toString()));
		assertEquals(insertRecordList + "\nint count = " + insertExecuteBatchApiStr + "\n.executeList();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, null).toString()));
		assertNull(result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE).toString()));
		assertNull(result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_PK).toString()));
		assertNull(result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_UK).toString()));


		assertEquals("UPDATE", result.get(1).getType());
		assertEquals("仮想表ID: 055EB42884BA4FDBA154640B13B4D060", result.get(1).getDescription());
		assertEquals("UPDATE\n"+
				"    " + dbSchema + ".jdbc_tool_test_fullCaseMainEntity jdbc_tool_test_fullCaseMainEntity\n"+
				"SET\n"+
				"    StringColumn = ?,\n"+
				"    IntColumn = ?,\n"+
				"    NStringColumn = ?,\n"+
				"    DateTimeColumn = ?,\n"+
				"    YMColumn = ?,\n"+
				"    CurrencyColumn = ?,\n"+
				"    LongColumn = ?,\n"+
				"    CompanyCD = ?,\n"+
				"    ExclusiveFG = ?,\n"+
				"    RegisteredPerson = ?,\n"+
				"    RegisteredDT = ?,\n"+
				"    UpdatedPerson = ?,\n"+
				"    UpdatedDT = ?\n"+
				"WHERE\n"+
				"    (\n"+
				"        (\n"+
				"            jdbc_tool_test_fullCaseMainEntity.objectID = '1001'\n"+
				"            AND jdbc_tool_test_fullCaseMainEntity.StringColumn = '2002'\n"+
				"        )\n"+
				"        OR (\n"+
				"            jdbc_tool_test_fullCaseMainEntity.IntColumn = 3003\n"+
				"            AND jdbc_tool_test_fullCaseMainEntity.NStringColumn = '4004'\n"+
				"        )\n"+
				"    )\n"+
				"    AND (\n"+
				"        (\n"+
				"            jdbc_tool_test_fullCaseMainEntity.DateTimeColumn = ?\n"+
				"            AND jdbc_tool_test_fullCaseMainEntity.YMColumn = ?\n"+
				"        )\n"+
				"        OR (\n"+
				"            jdbc_tool_test_fullCaseMainEntity.CurrencyColumn = ?\n"+
				"            AND jdbc_tool_test_fullCaseMainEntity.LongColumn = ?\n"+
				"        )\n"+
				"        AND jdbc_tool_test_fullCaseMainEntity.CompanyCD = ?\n"+
				"    )", result.get(1).getSqlScript());

		Map<String, Object> updateCodeResult = (result.get(1)).getVirtualEntity();
		assertEquals(14, ((List<Map<String,Object>>)updateCodeResult.get("columns")).size());
		assertEquals("-", updateCodeResult.get("absoluteVirtualColumnCode"));

		String whereParamsParts = "Map<String, Object> whereParams = new HashMap<>();\n"+
				"whereParams.put(\"jdbc_tool_test_fullCaseMainEntity.YMColumn\",);\n"+
				"whereParams.put(\"jdbc_tool_test_fullCaseMainEntity.DateTimeColumn\",);\n"+
				"whereParams.put(\"jdbc_tool_test_fullCaseMainEntity.LongColumn\",);\n"+
				"whereParams.put(\"jdbc_tool_test_fullCaseMainEntity.CompanyCD\",);\n"+
				"whereParams.put(\"jdbc_tool_test_fullCaseMainEntity.CurrencyColumn\",);\n";
		String updateRecord = "GRecord record = createRecord();\n"+
				"record.setObject(\"StringColumn\",);\n"+
				"record.setObject(\"IntColumn\",);\n"+
				"record.setObject(\"NStringColumn\",);\n"+
				"record.setObject(\"DateTimeColumn\",);\n"+
				"record.setObject(\"YMColumn\",);\n"+
				"record.setObject(\"CurrencyColumn\",);\n"+
				"record.setObject(\"LongColumn\",);\n"+
				"record.setObject(\"CompanyCD\",);\n"+
				"record.setObject(\"ExclusiveFG\",);\n"+
				"record.setObject(\"RegisteredPerson\",);\n"+
				"record.setObject(\"RegisteredDT\",);\n"+
				"record.setObject(\"UpdatedPerson\",);\n"+
				"record.setObject(\"UpdatedDT\",);\n";
		String updateExecuteApiStr = "update(\"jdbc_tool_test_fullCaseMainEntity\")\n"+ ".set(record)\n";
		String executeApiWhereStr = ".where(expMap($($($(\"jdbc_tool_test_fullCaseMainEntity.objectID = '1001'\").AND(\"jdbc_tool_test_fullCaseMainEntity.StringColumn = '2002'\")).OR($(\"jdbc_tool_test_fullCaseMainEntity.IntColumn = 3003\").AND(\"jdbc_tool_test_fullCaseMainEntity.NStringColumn = '4004'\"))).AND($($(\"jdbc_tool_test_fullCaseMainEntity.DateTimeColumn = :jdbc_tool_test_fullCaseMainEntity.DateTimeColumn\").AND(\"jdbc_tool_test_fullCaseMainEntity.YMColumn = :jdbc_tool_test_fullCaseMainEntity.YMColumn\")).OR($(\"jdbc_tool_test_fullCaseMainEntity.CurrencyColumn = :jdbc_tool_test_fullCaseMainEntity.CurrencyColumn\").AND(\"jdbc_tool_test_fullCaseMainEntity.LongColumn = :jdbc_tool_test_fullCaseMainEntity.LongColumn\")).AND(\"jdbc_tool_test_fullCaseMainEntity.CompanyCD = $TRIM(:jdbc_tool_test_fullCaseMainEntity.CompanyCD)\")), whereParams))\n";
		assertEquals(whereParamsParts + updateRecord + "\nint count = " + updateExecuteApiStr + executeApiWhereStr + ".execute();", result.get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE).toString()));
		assertEquals(updateRecord + "\nint count = " + updateExecuteApiStr + ".wherePK()\n"+ ".execute();", result.get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_PK).toString()));
		String executeUKStr = ".whereUK(\"MultipleColumnUK\")\n"+ "//.whereUK(\"OtherUKName\")\n"+ "//.whereUK(\"SingleColumnUK\")";
		assertEquals(updateRecord + "\nint count = " + updateExecuteApiStr + executeUKStr + "\n.execute();", result.get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_UK).toString()));

		StringBuilder updateRecordList = new StringBuilder();
		updateRecordList.append("List<GRecord> recordList = new ArrayList<>();").append("\n");
		updateRecordList.append(updateRecord);
		updateRecordList.append("recordList.add(record);").append("\n");

		String updateExecuteListApiStr = "update(\"jdbc_tool_test_fullCaseMainEntity\")\n"+ ".set(recordList)\n";

		assertEquals(updateRecordList + "\nint count = " + updateExecuteListApiStr + ".wherePK()\n"+ ".executeList();", result.get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_PK).toString()));
		assertEquals(updateRecordList + "\nint count = " + updateExecuteListApiStr + executeUKStr + "\n.executeList();", result.get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_UK).toString()));
		assertEquals(updateRecordList + "\nint count = " + updateExecuteListApiStr + ".wherePK()\n"+ ".executeBatch();", result.get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_PK).toString()));
		assertEquals(updateRecordList + "\nint count = " + updateExecuteListApiStr + executeUKStr + "\n.executeBatch();", result.get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_UK).toString()));

		assertEquals("SELECT", result.get(2).getType());
		assertEquals("仮想表ID: 055EB42884BA4FDBA154640B13B4D060", result.get(2).getDescription());
		assertEquals("SELECT\n"+
				"    jdbc_tool_test_fullCaseMainEntity.objectID,\n"+
				"    jdbc_tool_test_fullCaseMainEntity.StringColumn,\n"+
				"    jdbc_tool_test_fullCaseMainEntity.IntColumn,\n"+
				"    jdbc_tool_test_fullCaseMainEntity.NStringColumn,\n"+
				"    jdbc_tool_test_fullCaseMainEntity.DateTimeColumn,\n"+
				"    jdbc_tool_test_fullCaseMainEntity.YMColumn,\n"+
				"    jdbc_tool_test_fullCaseMainEntity.CurrencyColumn,\n"+
				"    jdbc_tool_test_fullCaseMainEntity.LongColumn,\n"+
				"    jdbc_tool_test_fullCaseMainEntity.CompanyCD,\n"+
				"    jdbc_tool_test_fullCaseMainEntity.ExclusiveFG,\n"+
				"    jdbc_tool_test_fullCaseMainEntity.RegisteredPerson,\n"+
				"    jdbc_tool_test_fullCaseMainEntity.RegisteredDT,\n"+
				"    jdbc_tool_test_fullCaseMainEntity.UpdatedPerson,\n"+
				"    jdbc_tool_test_fullCaseMainEntity.UpdatedDT\n"+
				"FROM\n"+
				"    " + dbSchema + ".jdbc_tool_test_fullCaseMainEntity jdbc_tool_test_fullCaseMainEntity\n"+
				"WHERE\n"+
				"    (\n"+
				"        (\n"+
				"            jdbc_tool_test_fullCaseMainEntity.objectID = '1001'\n"+
				"            AND jdbc_tool_test_fullCaseMainEntity.StringColumn = '2002'\n"+
				"        )\n"+
				"        OR (\n"+
				"            jdbc_tool_test_fullCaseMainEntity.IntColumn = 3003\n"+
				"            AND jdbc_tool_test_fullCaseMainEntity.NStringColumn = '4004'\n"+
				"        )\n"+
				"    )\n"+
				"    AND (\n"+
				"        (\n"+
				"            jdbc_tool_test_fullCaseMainEntity.DateTimeColumn = ?\n"+
				"            AND jdbc_tool_test_fullCaseMainEntity.YMColumn = ?\n"+
				"        )\n"+
				"        OR (\n"+
				"            jdbc_tool_test_fullCaseMainEntity.CurrencyColumn = ?\n"+
				"            AND jdbc_tool_test_fullCaseMainEntity.LongColumn = ?\n"+
				"        )\n"+
				"        AND jdbc_tool_test_fullCaseMainEntity.CompanyCD = ?\n"+
				"    )", result.get(2).getSqlScript());

		Map<String, Object> selectCodeResult = (result.get(2)).getVirtualEntity();
		assertEquals(14, ((List<Map<String,Object>>)selectCodeResult.get("columns")).size());
		assertEquals("-", selectCodeResult.get("absoluteVirtualColumnCode"));

		String selectApiStr = "select(\"jdbc_tool_test_fullCaseMainEntity\", \"jdbc_tool_test_fullCaseMainEntity\")\n"+
				".fields(colsAll())\n";
		String whereParamsPartsUKPK = "Object[] whereParams = new Object[]{,,,,};\n";
		String selectUKStr = ".whereUK(\"MultipleColumnUK\", whereParams)\n"+ "//.whereUK(\"OtherUKName\", whereParams)\n"+ "//.whereUK(\"SingleColumnUK\", whereParams)";
		assertEquals(whereParamsParts + "List<GRecord> result = " + selectApiStr + executeApiWhereStr + ".findList();", result.get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE).toString()));
		assertEquals(whereParamsPartsUKPK + "List<GRecord> result = " + selectApiStr + ".wherePK(whereParams)\n"+ ".findList();", result.get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE_PK).toString()));
		assertEquals(whereParamsPartsUKPK + "List<GRecord> result = " + selectApiStr + selectUKStr + "\n.findList();", result.get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE_UK).toString()));

		assertEquals(whereParamsParts + "GRecord result = " + selectApiStr + executeApiWhereStr + ".findRecord();", result.get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE).toString()));
		assertEquals(whereParamsPartsUKPK + "GRecord result = " + selectApiStr + ".wherePK(whereParams)\n"+ ".findRecord();", result.get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE_PK).toString()));
		assertEquals(whereParamsPartsUKPK + "GRecord result = " + selectApiStr + selectUKStr + "\n.findRecord();", result.get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE_UK).toString()));

		assertEquals(whereParamsParts + "GRecordSet result = " + selectApiStr + executeApiWhereStr + ".findRecordSet();", result.get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE).toString()));
		assertEquals(whereParamsPartsUKPK + "GRecordSet result = " + selectApiStr + ".wherePK(whereParams)\n"+ ".findRecordSet();", result.get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE_PK).toString()));
		assertEquals(whereParamsPartsUKPK + "GRecordSet result = " + selectApiStr + selectUKStr + "\n.findRecordSet();", result.get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE_UK).toString()));

		assertEquals("DELETE", result.get(3).getType());
		assertEquals("仮想表ID: 055EB42884BA4FDBA154640B13B4D060", result.get(3).getDescription());
		assertEquals(getDeleteSql_testExport_expMapCase2(), result.get(3).getSqlScript());

		Map<String, Object> deleteCodeResult = (result.get(3)).getVirtualEntity();
		assertEquals(14, ((List<Map<String,Object>>)deleteCodeResult.get("columns")).size());
		assertEquals("-", selectCodeResult.get("absoluteVirtualColumnCode"));

		String deleteApiStr = "delete(\"jdbc_tool_test_fullCaseMainEntity\")\n";
		assertEquals(whereParamsParts + "int count = " + deleteApiStr + executeApiWhereStr + ".execute();", result.get(3).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE).toString()));

		String deleteRecordStr = "GRecord whereParams = createRecord();\n"+
				"whereParams.setObject(\"objectID\",);\n";

		assertEquals(deleteRecordStr + "\nint count = " + deleteApiStr + ".wherePK(whereParams)\n"+ ".execute();", result.get(3).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_PK).toString()));
		String deleteRecordUKStr = "GRecord whereParams = createRecord();\n"+
				"whereParams.setObject(\"StringColumn\",);\n"+
				"whereParams.setObject(\"IntColumn\",);\n"+
				"whereParams.setObject(\"NStringColumn\",);\n"+
				"//ユーニックキー「OtherUKName」用のカラム\n"+
				"/*whereParams.setObject(\"IntColumn\",);\n"+
				"whereParams.setObject(\"NStringColumn\",);\n"+
				"whereParams.setObject(\"DateTimeColumn\",);\n"+
				"whereParams.setObject(\"YMColumn\",);\n"+
				"*/\n"+
				"//ユーニックキー「SingleColumnUK」用のカラム\n"+
				"/*whereParams.setObject(\"StringColumn\",);\n"+
				"*/\n";

		assertEquals(deleteRecordUKStr + "\nint count = " + deleteApiStr + selectUKStr + "\n.execute();", result.get(3).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_UK).toString()));

		StringBuilder deleteListRecordStr = new StringBuilder();
		deleteListRecordStr.append(deleteRecordStr);
		deleteListRecordStr.append("List<GRecord> whereParamsList = new ArrayList<>();").append("\n");
		deleteListRecordStr.append("whereParamsList.add(whereParams);").append("\n");

		StringBuilder deleteListRecordUKStr = new StringBuilder();
		deleteListRecordUKStr.append(deleteRecordUKStr);
		deleteListRecordUKStr.append("List<GRecord> whereParamsList = new ArrayList<>();").append("\n");
		deleteListRecordUKStr.append("whereParamsList.add(whereParams);").append("\n");
		String deleteListUKStr = ".whereUK(\"MultipleColumnUK\", whereParamsList)\n"+ "//.whereUK(\"OtherUKName\", whereParamsList)\n"+ "//.whereUK(\"SingleColumnUK\", whereParamsList)";

		assertNull(result.get(3).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE).toString()));
		assertEquals(deleteListRecordStr + "\nint count = " + deleteApiStr + ".wherePK(whereParamsList)\n"+ ".executeList();", result.get(3).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_PK).toString()));
		assertEquals(deleteListRecordUKStr + "\nint count = " + deleteApiStr + deleteListUKStr + "\n.executeList();", result.get(3).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_UK).toString()));
		assertNull(result.get(3).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE).toString()));
		assertEquals(deleteListRecordStr + "\nint count = " + deleteApiStr + ".wherePK(whereParamsList)\n"+ ".executeBatch();", result.get(3).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_PK).toString()));
		assertEquals(deleteListRecordUKStr + "\nint count = " + deleteApiStr + deleteListUKStr + "\n.executeBatch();", result.get(3).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_UK).toString()));
	}

	protected String getDeleteSql_testExport_expMapCase2() {
		return "DELETE FROM\n"+
				"    " + dbSchema + ".jdbc_tool_test_fullCaseMainEntity\n"+
				"WHERE\n"+
				"    (\n"+
				"        (\n"+
				"            " + dbSchema + ".jdbc_tool_test_fullCaseMainEntity.objectID = '1001'\n"+
				"            AND " + dbSchema + ".jdbc_tool_test_fullCaseMainEntity.StringColumn = '2002'\n"+
				"        )\n"+
				"        OR (\n"+
				"            " + dbSchema + ".jdbc_tool_test_fullCaseMainEntity.IntColumn = 3003\n"+
				"            AND " + dbSchema + ".jdbc_tool_test_fullCaseMainEntity.NStringColumn = '4004'\n"+
				"        )\n"+
				"    )\n"+
				"    AND (\n"+
				"        (\n"+
				"            " + dbSchema + ".jdbc_tool_test_fullCaseMainEntity.DateTimeColumn = ?\n"+
				"            AND " + dbSchema + ".jdbc_tool_test_fullCaseMainEntity.YMColumn = ?\n"+
				"        )\n"+
				"        OR (\n"+
				"            " + dbSchema + ".jdbc_tool_test_fullCaseMainEntity.CurrencyColumn = ?\n"+
				"            AND " + dbSchema + ".jdbc_tool_test_fullCaseMainEntity.LongColumn = ?\n"+
				"        )\n"+
				"        AND " + dbSchema + ".jdbc_tool_test_fullCaseMainEntity.CompanyCD = ?\n"+
				"    )";
	}

	@Test
	public void testExport_expMapCase3() {

		// パターン３：where (a = ? and b = ?) or (c = ? and d = ? and (e = ? and f = ?))
		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_expmapcase3.xml");
		ApiRequest apiRequest = parseRequestFromFile(getCommonSetting(),"jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_expmapcase3.yaml");
		List<CodeResponse> result = convertVe2Code(apiRequest);
assertEquals("INSERT", result.get(0).getType());
		assertEquals("仮想表ID: E706EFFF592B4C009DB35F8D381B4169", result.get(0).getDescription());
		assertEquals("INSERT INTO\n"+
						"    " + dbSchema + ".jdbc_tool_test_fullCaseMainEntity(objectID, StringColumn, IntColumn, NStringColumn, DateTimeColumn, YMColumn, CurrencyColumn, LongColumn, CompanyCD, ExclusiveFG, RegisteredPerson, UpdatedPerson, RegisteredDT, UpdatedDT)\n"+
						"VALUES\n"+
						"    " + "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " + getTimestampCommand() + ", " + getTimestampCommand() + ")"
				, result.get(0).getSqlScript());

		Map<String, Object> insertCodeResult = (result.get(0)).getVirtualEntity();
		assertEquals(14, ((List<Map<String,Object>>)insertCodeResult.get("columns")).size());
		assertEquals("-", insertCodeResult.get("absoluteVirtualColumnCode"));

		String insertRecord = "GRecord record = createRecord();\n"+
				"record.setObject(\"objectID\",);\n"+
				"record.setObject(\"StringColumn\",);\n"+
				"record.setObject(\"IntColumn\",);\n"+
				"record.setObject(\"NStringColumn\",);\n"+
				"record.setObject(\"DateTimeColumn\",);\n"+
				"record.setObject(\"YMColumn\",);\n"+
				"record.setObject(\"CurrencyColumn\",);\n"+
				"record.setObject(\"LongColumn\",);\n"+
				"record.setObject(\"CompanyCD\",);\n"+
				"record.setObject(\"ExclusiveFG\",);\n"+
				"record.setObject(\"RegisteredPerson\",);\n"+
				"record.setObject(\"UpdatedPerson\",);\n"+
				"record.setObject(\"RegisteredDT\",);\n"+
				"record.setObject(\"UpdatedDT\",);\n";
		String insertExecuteApiStr = "insert(\"jdbc_tool_test_fullCaseMainEntity\")\n"+  ".values(record)";

		assertEquals(insertRecord + "\nint count = " + insertExecuteApiStr + "\n.execute();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, null).toString()));
		assertNull(result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE).toString()));
		assertNull(result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_PK).toString()));
		assertNull(result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_UK).toString()));

		String insertExecuteBatchApiStr = "insert(\"jdbc_tool_test_fullCaseMainEntity\")\n"+ ".values(recordList)";
		StringBuilder insertRecordList = new StringBuilder();
		insertRecordList.append("List<GRecord> recordList = new ArrayList<>();").append("\n");
		insertRecordList.append(insertRecord);
		insertRecordList.append("recordList.add(record);").append("\n");

		assertEquals(insertRecordList + "\nint count = " + insertExecuteBatchApiStr + "\n.executeBatch();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, null).toString()));
		assertNull(result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE).toString()));
		assertNull(result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_PK).toString()));
		assertNull(result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_UK).toString()));
		assertEquals(insertRecordList + "\nint count = " + insertExecuteBatchApiStr + "\n.executeList();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, null).toString()));
		assertNull(result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE).toString()));
		assertNull(result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_PK).toString()));
		assertNull(result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_UK).toString()));


		assertEquals("UPDATE", result.get(1).getType());
		assertEquals("仮想表ID: E706EFFF592B4C009DB35F8D381B4169", result.get(1).getDescription());
		assertEquals("UPDATE\n"+
				"    " + dbSchema + ".jdbc_tool_test_fullCaseMainEntity jdbc_tool_test_fullCaseMainEntity\n"+
				"SET\n"+
				"    StringColumn = ?,\n"+
				"    IntColumn = ?,\n"+
				"    NStringColumn = ?,\n"+
				"    DateTimeColumn = ?,\n"+
				"    YMColumn = ?,\n"+
				"    CurrencyColumn = ?,\n"+
				"    LongColumn = ?,\n"+
				"    CompanyCD = ?,\n"+
				"    ExclusiveFG = ?,\n"+
				"    RegisteredPerson = ?,\n"+
				"    RegisteredDT = ?,\n"+
				"    UpdatedPerson = ?,\n"+
				"    UpdatedDT = ?\n"+
				"WHERE\n"+
				"    (\n"+
				"        (\n"+
				"            jdbc_tool_test_fullCaseMainEntity.objectID = '1001'\n"+
				"            AND jdbc_tool_test_fullCaseMainEntity.StringColumn = '2002'\n"+
				"        )\n"+
				"        OR (\n"+
				"            jdbc_tool_test_fullCaseMainEntity.IntColumn = 3003\n"+
				"            AND jdbc_tool_test_fullCaseMainEntity.NStringColumn = '4004'\n"+
				"            OR (\n"+
				"                jdbc_tool_test_fullCaseMainEntity.DateTimeColumn = '5005'\n"+
				"                AND jdbc_tool_test_fullCaseMainEntity.YMColumn = '6006'\n"+
				"            )\n"+
				"        )\n"+
				"    )\n"+
				"    AND (\n"+
				"        (\n"+
				"            jdbc_tool_test_fullCaseMainEntity.CurrencyColumn = ?\n"+
				"            AND jdbc_tool_test_fullCaseMainEntity.LongColumn = ?\n"+
				"        )\n"+
				"        OR (\n"+
				"            jdbc_tool_test_fullCaseMainEntity.CompanyCD = ?\n"+
				"            AND jdbc_tool_test_fullCaseMainEntity.ExclusiveFG = ?\n"+
				"            OR (\n"+
				"                jdbc_tool_test_fullCaseMainEntity.RegisteredPerson = ?\n"+
				"                AND jdbc_tool_test_fullCaseMainEntity.RegisteredDT = ?\n"+
				"            )\n"+
				"        )\n"+
				"    )", result.get(1).getSqlScript());

		Map<String, Object> updateCodeResult = (result.get(1)).getVirtualEntity();
		assertEquals(14, ((List<Map<String,Object>>)updateCodeResult.get("columns")).size());
		assertEquals("-", updateCodeResult.get("absoluteVirtualColumnCode"));

		String whereParamsParts = "Map<String, Object> whereParams = new HashMap<>();\n"+
				"whereParams.put(\"jdbc_tool_test_fullCaseMainEntity.RegisteredPerson\",);\n"+
				"whereParams.put(\"jdbc_tool_test_fullCaseMainEntity.LongColumn\",);\n"+
				"whereParams.put(\"jdbc_tool_test_fullCaseMainEntity.CompanyCD\",);\n"+
				"whereParams.put(\"jdbc_tool_test_fullCaseMainEntity.ExclusiveFG\",);\n"+
				"whereParams.put(\"jdbc_tool_test_fullCaseMainEntity.RegisteredDT\",);\n"+
				"whereParams.put(\"jdbc_tool_test_fullCaseMainEntity.CurrencyColumn\",);\n";
		String updateRecord = "GRecord record = createRecord();\n"+
				"record.setObject(\"StringColumn\",);\n"+
				"record.setObject(\"IntColumn\",);\n"+
				"record.setObject(\"NStringColumn\",);\n"+
				"record.setObject(\"DateTimeColumn\",);\n"+
				"record.setObject(\"YMColumn\",);\n"+
				"record.setObject(\"CurrencyColumn\",);\n"+
				"record.setObject(\"LongColumn\",);\n"+
				"record.setObject(\"CompanyCD\",);\n"+
				"record.setObject(\"ExclusiveFG\",);\n"+
				"record.setObject(\"RegisteredPerson\",);\n"+
				"record.setObject(\"RegisteredDT\",);\n"+
				"record.setObject(\"UpdatedPerson\",);\n"+
				"record.setObject(\"UpdatedDT\",);\n";
		String updateExecuteApiStr = "update(\"jdbc_tool_test_fullCaseMainEntity\")\n"+ ".set(record)\n";
		String executeApiWhereStr = ".where(expMap($($($(\"jdbc_tool_test_fullCaseMainEntity.objectID = '1001'\").AND(\"jdbc_tool_test_fullCaseMainEntity.StringColumn = '2002'\")).OR($(\"jdbc_tool_test_fullCaseMainEntity.IntColumn = 3003\").AND(\"jdbc_tool_test_fullCaseMainEntity.NStringColumn = '4004'\").OR($(\"jdbc_tool_test_fullCaseMainEntity.DateTimeColumn = '5005'\").AND(\"jdbc_tool_test_fullCaseMainEntity.YMColumn = '6006'\")))).AND($($(\"jdbc_tool_test_fullCaseMainEntity.CurrencyColumn = :jdbc_tool_test_fullCaseMainEntity.CurrencyColumn\").AND(\"jdbc_tool_test_fullCaseMainEntity.LongColumn = :jdbc_tool_test_fullCaseMainEntity.LongColumn\")).OR($(\"jdbc_tool_test_fullCaseMainEntity.CompanyCD = :jdbc_tool_test_fullCaseMainEntity.CompanyCD\").AND(\"jdbc_tool_test_fullCaseMainEntity.ExclusiveFG = :jdbc_tool_test_fullCaseMainEntity.ExclusiveFG\").OR($(\"jdbc_tool_test_fullCaseMainEntity.RegisteredPerson = :jdbc_tool_test_fullCaseMainEntity.RegisteredPerson\").AND(\"jdbc_tool_test_fullCaseMainEntity.RegisteredDT = :jdbc_tool_test_fullCaseMainEntity.RegisteredDT\")))), whereParams))\n";
		assertEquals(whereParamsParts + updateRecord + "\nint count = " + updateExecuteApiStr + executeApiWhereStr + ".execute();", result.get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE).toString()));
		assertEquals(updateRecord + "\nint count = " + updateExecuteApiStr + ".wherePK()\n"+ ".execute();", result.get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_PK).toString()));
		String executeUKStr = ".whereUK(\"MultipleColumnUK\")\n"+ "//.whereUK(\"OtherUKName\")\n"+ "//.whereUK(\"SingleColumnUK\")";
		assertEquals(updateRecord + "\nint count = " + updateExecuteApiStr + executeUKStr + "\n.execute();", result.get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_UK).toString()));

		StringBuilder updateRecordList = new StringBuilder();
		updateRecordList.append("List<GRecord> recordList = new ArrayList<>();").append("\n");
		updateRecordList.append(updateRecord);
		updateRecordList.append("recordList.add(record);").append("\n");

		String updateExecuteListApiStr = "update(\"jdbc_tool_test_fullCaseMainEntity\")\n"+ ".set(recordList)\n";

		assertEquals(updateRecordList + "\nint count = " + updateExecuteListApiStr + ".wherePK()\n"+ ".executeList();", result.get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_PK).toString()));
		assertEquals(updateRecordList + "\nint count = " + updateExecuteListApiStr + executeUKStr + "\n.executeList();", result.get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_UK).toString()));
		assertEquals(updateRecordList + "\nint count = " + updateExecuteListApiStr + ".wherePK()\n"+ ".executeBatch();", result.get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_PK).toString()));
		assertEquals(updateRecordList + "\nint count = " + updateExecuteListApiStr + executeUKStr + "\n.executeBatch();", result.get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_UK).toString()));

		assertEquals("SELECT", result.get(2).getType());
		assertEquals("仮想表ID: E706EFFF592B4C009DB35F8D381B4169", result.get(2).getDescription());
		assertEquals("SELECT\n"+
				"    jdbc_tool_test_fullCaseMainEntity.objectID,\n"+
				"    jdbc_tool_test_fullCaseMainEntity.StringColumn,\n"+
				"    jdbc_tool_test_fullCaseMainEntity.IntColumn,\n"+
				"    jdbc_tool_test_fullCaseMainEntity.NStringColumn,\n"+
				"    jdbc_tool_test_fullCaseMainEntity.DateTimeColumn,\n"+
				"    jdbc_tool_test_fullCaseMainEntity.YMColumn,\n"+
				"    jdbc_tool_test_fullCaseMainEntity.CurrencyColumn,\n"+
				"    jdbc_tool_test_fullCaseMainEntity.LongColumn,\n"+
				"    jdbc_tool_test_fullCaseMainEntity.CompanyCD,\n"+
				"    jdbc_tool_test_fullCaseMainEntity.ExclusiveFG,\n"+
				"    jdbc_tool_test_fullCaseMainEntity.RegisteredPerson,\n"+
				"    jdbc_tool_test_fullCaseMainEntity.RegisteredDT,\n"+
				"    jdbc_tool_test_fullCaseMainEntity.UpdatedPerson,\n"+
				"    jdbc_tool_test_fullCaseMainEntity.UpdatedDT\n"+
				"FROM\n"+
				"    " + dbSchema + ".jdbc_tool_test_fullCaseMainEntity jdbc_tool_test_fullCaseMainEntity\n"+
				"WHERE\n"+
				"    (\n"+
				"        (\n"+
				"            jdbc_tool_test_fullCaseMainEntity.objectID = '1001'\n"+
				"            AND jdbc_tool_test_fullCaseMainEntity.StringColumn = '2002'\n"+
				"        )\n"+
				"        OR (\n"+
				"            jdbc_tool_test_fullCaseMainEntity.IntColumn = 3003\n"+
				"            AND jdbc_tool_test_fullCaseMainEntity.NStringColumn = '4004'\n"+
				"            OR (\n"+
				"                jdbc_tool_test_fullCaseMainEntity.DateTimeColumn = '5005'\n"+
				"                AND jdbc_tool_test_fullCaseMainEntity.YMColumn = '6006'\n"+
				"            )\n"+
				"        )\n"+
				"    )\n"+
				"    AND (\n"+
				"        (\n"+
				"            jdbc_tool_test_fullCaseMainEntity.CurrencyColumn = ?\n"+
				"            AND jdbc_tool_test_fullCaseMainEntity.LongColumn = ?\n"+
				"        )\n"+
				"        OR (\n"+
				"            jdbc_tool_test_fullCaseMainEntity.CompanyCD = ?\n"+
				"            AND jdbc_tool_test_fullCaseMainEntity.ExclusiveFG = ?\n"+
				"            OR (\n"+
				"                jdbc_tool_test_fullCaseMainEntity.RegisteredPerson = ?\n"+
				"                AND jdbc_tool_test_fullCaseMainEntity.RegisteredDT = ?\n"+
				"            )\n"+
				"        )\n"+
				"    )", result.get(2).getSqlScript());

		Map<String, Object> selectCodeResult = (result.get(2)).getVirtualEntity();
		assertEquals(14, ((List<Map<String,Object>>)selectCodeResult.get("columns")).size());
		assertEquals("-", selectCodeResult.get("absoluteVirtualColumnCode"));

		String selectApiStr = "select(\"jdbc_tool_test_fullCaseMainEntity\", \"jdbc_tool_test_fullCaseMainEntity\")\n"+
				".fields(colsAll())\n";
		String whereParamsPartsUKPK = "Object[] whereParams = new Object[]{,,,,,};\n";
		String selectUKStr = ".whereUK(\"MultipleColumnUK\", whereParams)\n"+ "//.whereUK(\"OtherUKName\", whereParams)\n"+ "//.whereUK(\"SingleColumnUK\", whereParams)";
		assertEquals(whereParamsParts + "List<GRecord> result = " + selectApiStr + executeApiWhereStr + ".findList();", result.get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE).toString()));
		assertEquals(whereParamsPartsUKPK + "List<GRecord> result = " + selectApiStr + ".wherePK(whereParams)\n"+ ".findList();", result.get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE_PK).toString()));
		assertEquals(whereParamsPartsUKPK + "List<GRecord> result = " + selectApiStr + selectUKStr + "\n.findList();", result.get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE_UK).toString()));

		assertEquals(whereParamsParts + "GRecord result = " + selectApiStr + executeApiWhereStr + ".findRecord();", result.get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE).toString()));
		assertEquals(whereParamsPartsUKPK + "GRecord result = " + selectApiStr + ".wherePK(whereParams)\n"+ ".findRecord();", result.get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE_PK).toString()));
		assertEquals(whereParamsPartsUKPK + "GRecord result = " + selectApiStr + selectUKStr + "\n.findRecord();", result.get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE_UK).toString()));

		assertEquals(whereParamsParts + "GRecordSet result = " + selectApiStr + executeApiWhereStr + ".findRecordSet();", result.get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE).toString()));
		assertEquals(whereParamsPartsUKPK + "GRecordSet result = " + selectApiStr + ".wherePK(whereParams)\n"+ ".findRecordSet();", result.get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE_PK).toString()));
		assertEquals(whereParamsPartsUKPK + "GRecordSet result = " + selectApiStr + selectUKStr + "\n.findRecordSet();", result.get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE_UK).toString()));

		assertEquals("DELETE", result.get(3).getType());
		assertEquals("仮想表ID: E706EFFF592B4C009DB35F8D381B4169", result.get(3).getDescription());
		assertEquals(getDeleteSql_testExport_expMapCase3(), result.get(3).getSqlScript());

		Map<String, Object> deleteCodeResult = (result.get(3)).getVirtualEntity();
		assertEquals(14, ((List<Map<String,Object>>)deleteCodeResult.get("columns")).size());
		assertEquals("-", selectCodeResult.get("absoluteVirtualColumnCode"));

		String deleteApiStr = "delete(\"jdbc_tool_test_fullCaseMainEntity\")\n";
		assertEquals(whereParamsParts + "int count = " + deleteApiStr + executeApiWhereStr + ".execute();", result.get(3).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE).toString()));

		String deleteRecordStr = "GRecord whereParams = createRecord();\n"+
				"whereParams.setObject(\"objectID\",);\n";

		assertEquals(deleteRecordStr + "\nint count = " + deleteApiStr + ".wherePK(whereParams)\n"+ ".execute();", result.get(3).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_PK).toString()));
		String deleteRecordUKStr = "GRecord whereParams = createRecord();\n"+
				"whereParams.setObject(\"StringColumn\",);\n"+
				"whereParams.setObject(\"IntColumn\",);\n"+
				"whereParams.setObject(\"NStringColumn\",);\n"+
				"//ユーニックキー「OtherUKName」用のカラム\n"+
				"/*whereParams.setObject(\"IntColumn\",);\n"+
				"whereParams.setObject(\"NStringColumn\",);\n"+
				"whereParams.setObject(\"DateTimeColumn\",);\n"+
				"whereParams.setObject(\"YMColumn\",);\n"+
				"*/\n"+
				"//ユーニックキー「SingleColumnUK」用のカラム\n"+
				"/*whereParams.setObject(\"StringColumn\",);\n"+
				"*/\n";

		assertEquals(deleteRecordUKStr + "\nint count = " + deleteApiStr + selectUKStr + "\n.execute();", result.get(3).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_UK).toString()));

		StringBuilder deleteListRecordStr = new StringBuilder();
		deleteListRecordStr.append(deleteRecordStr);
		deleteListRecordStr.append("List<GRecord> whereParamsList = new ArrayList<>();").append("\n");
		deleteListRecordStr.append("whereParamsList.add(whereParams);").append("\n");

		StringBuilder deleteListRecordUKStr = new StringBuilder();
		deleteListRecordUKStr.append(deleteRecordUKStr);
		deleteListRecordUKStr.append("List<GRecord> whereParamsList = new ArrayList<>();").append("\n");
		deleteListRecordUKStr.append("whereParamsList.add(whereParams);").append("\n");
		String deleteListUKStr = ".whereUK(\"MultipleColumnUK\", whereParamsList)\n"+ "//.whereUK(\"OtherUKName\", whereParamsList)\n"+ "//.whereUK(\"SingleColumnUK\", whereParamsList)";

		assertNull(result.get(3).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE).toString()));
		assertEquals(deleteListRecordStr + "\nint count = " + deleteApiStr + ".wherePK(whereParamsList)\n"+ ".executeList();", result.get(3).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_PK).toString()));
		assertEquals(deleteListRecordUKStr + "\nint count = " + deleteApiStr + deleteListUKStr + "\n.executeList();", result.get(3).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_UK).toString()));
		assertNull(result.get(3).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE).toString()));
		assertEquals(deleteListRecordStr + "\nint count = " + deleteApiStr + ".wherePK(whereParamsList)\n"+ ".executeBatch();", result.get(3).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_PK).toString()));
		assertEquals(deleteListRecordUKStr + "\nint count = " + deleteApiStr + deleteListUKStr + "\n.executeBatch();", result.get(3).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_UK).toString()));
	}

	protected String getDeleteSql_testExport_expMapCase3() {
		return "DELETE FROM\n"+
				"    " + dbSchema + ".jdbc_tool_test_fullCaseMainEntity\n"+
				"WHERE\n"+
				"    (\n"+
				"        (\n"+
				"            " + dbSchema + ".jdbc_tool_test_fullCaseMainEntity.objectID = '1001'\n"+
				"            AND " + dbSchema + ".jdbc_tool_test_fullCaseMainEntity.StringColumn = '2002'\n"+
				"        )\n"+
				"        OR (\n"+
				"            " + dbSchema + ".jdbc_tool_test_fullCaseMainEntity.IntColumn = 3003\n"+
				"            AND " + dbSchema + ".jdbc_tool_test_fullCaseMainEntity.NStringColumn = '4004'\n"+
				"            OR (\n"+
				"                " + dbSchema + ".jdbc_tool_test_fullCaseMainEntity.DateTimeColumn = '5005'\n"+
				"                AND " + dbSchema + ".jdbc_tool_test_fullCaseMainEntity.YMColumn = '6006'\n"+
				"            )\n"+
				"        )\n"+
				"    )\n"+
				"    AND (\n"+
				"        (\n"+
				"            " + dbSchema + ".jdbc_tool_test_fullCaseMainEntity.CurrencyColumn = ?\n"+
				"            AND " + dbSchema + ".jdbc_tool_test_fullCaseMainEntity.LongColumn = ?\n"+
				"        )\n"+
				"        OR (\n"+
				"            " + dbSchema + ".jdbc_tool_test_fullCaseMainEntity.CompanyCD = ?\n"+
				"            AND " + dbSchema + ".jdbc_tool_test_fullCaseMainEntity.ExclusiveFG = ?\n"+
				"            OR (\n"+
				"                " + dbSchema + ".jdbc_tool_test_fullCaseMainEntity.RegisteredPerson = ?\n"+
				"                AND " + dbSchema + ".jdbc_tool_test_fullCaseMainEntity.RegisteredDT = ?\n"+
				"            )\n"+
				"        )\n"+
				"    )";
	}

	@Test
	public void testExport_expMapCase4() {

		// パターン４：where (a = ? and b = ?) or c = ? and d = ?
		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_expmapcase4.xml");
		ApiRequest apiRequest = parseRequestFromFile(getCommonSetting(),"jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_expmapcase4.yaml");
		List<CodeResponse> result = convertVe2Code(apiRequest);
assertEquals("INSERT", result.get(0).getType());
		assertEquals("仮想表ID: A2E4442626F04100A45E747DE5856120", result.get(0).getDescription());
		assertEquals("INSERT INTO\n"+
						"    " + dbSchema + ".jdbc_tool_test_fullCaseMainEntity(objectID, StringColumn, IntColumn, NStringColumn, DateTimeColumn, YMColumn, CurrencyColumn, LongColumn, ExclusiveFG, RegisteredPerson, UpdatedPerson, RegisteredDT, UpdatedDT)\n"+
						"VALUES\n"+
						"    " + "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " + getTimestampCommand() + ", " + getTimestampCommand() + ")"
				, result.get(0).getSqlScript());

		Map<String, Object> insertCodeResult = (result.get(0)).getVirtualEntity();
		assertEquals(13, ((List<Map<String,Object>>)insertCodeResult.get("columns")).size());
		assertEquals("-", insertCodeResult.get("absoluteVirtualColumnCode"));

		String insertRecord = "GRecord record = createRecord();\n"+
				"record.setObject(\"objectID\",);\n"+
				"record.setObject(\"StringColumn\",);\n"+
				"record.setObject(\"IntColumn\",);\n"+
				"record.setObject(\"NStringColumn\",);\n"+
				"record.setObject(\"DateTimeColumn\",);\n"+
				"record.setObject(\"YMColumn\",);\n"+
				"record.setObject(\"CurrencyColumn\",);\n"+
				"record.setObject(\"LongColumn\",);\n"+
				"record.setObject(\"ExclusiveFG\",);\n"+
				"record.setObject(\"RegisteredPerson\",);\n"+
				"record.setObject(\"UpdatedPerson\",);\n"+
				"record.setObject(\"RegisteredDT\",);\n"+
				"record.setObject(\"UpdatedDT\",);\n";
		String insertExecuteApiStr = "insert(\"jdbc_tool_test_fullCaseMainEntity\")\n"+  ".values(record)";

		assertEquals(insertRecord + "\nint count = " + insertExecuteApiStr + "\n.execute();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, null).toString()));
		assertNull(result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE).toString()));
		assertNull(result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_PK).toString()));
		assertNull(result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_UK).toString()));

		String insertExecuteBatchApiStr = "insert(\"jdbc_tool_test_fullCaseMainEntity\")\n"+ ".values(recordList)";
		StringBuilder insertRecordList = new StringBuilder();
		insertRecordList.append("List<GRecord> recordList = new ArrayList<>();").append("\n");
		insertRecordList.append(insertRecord);
		insertRecordList.append("recordList.add(record);").append("\n");

		assertEquals(insertRecordList + "\nint count = " + insertExecuteBatchApiStr + "\n.executeBatch();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, null).toString()));
		assertNull(result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE).toString()));
		assertNull(result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_PK).toString()));
		assertNull(result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_UK).toString()));
		assertEquals(insertRecordList + "\nint count = " + insertExecuteBatchApiStr + "\n.executeList();", result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, null).toString()));
		assertNull(result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE).toString()));
		assertNull(result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_PK).toString()));
		assertNull(result.get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_UK).toString()));


		assertEquals("UPDATE", result.get(1).getType());
		assertEquals("仮想表ID: A2E4442626F04100A45E747DE5856120", result.get(1).getDescription());
		assertEquals("UPDATE\n"+
				"    " + dbSchema + ".jdbc_tool_test_fullCaseMainEntity jdbc_tool_test_fullCaseMainEntity\n"+
				"SET\n"+
				"    StringColumn = ?,\n"+
				"    IntColumn = ?,\n"+
				"    NStringColumn = ?,\n"+
				"    DateTimeColumn = ?,\n"+
				"    YMColumn = ?,\n"+
				"    CurrencyColumn = ?,\n"+
				"    LongColumn = ?,\n"+
				"    ExclusiveFG = ?,\n"+
				"    RegisteredPerson = ?,\n"+
				"    RegisteredDT = ?,\n"+
				"    UpdatedPerson = ?,\n"+
				"    UpdatedDT = ?\n"+
				"WHERE\n"+
				"    (\n"+
				"        (\n"+
				"            jdbc_tool_test_fullCaseMainEntity.objectID = ?\n"+
				"            AND jdbc_tool_test_fullCaseMainEntity.StringColumn = ?\n"+
				"        )\n"+
				"        OR jdbc_tool_test_fullCaseMainEntity.IntColumn = ?\n"+
				"        AND jdbc_tool_test_fullCaseMainEntity.NStringColumn = ?\n"+
				"    )", result.get(1).getSqlScript());

		Map<String, Object> updateCodeResult = (result.get(1)).getVirtualEntity();
		assertEquals(13, ((List<Map<String,Object>>)updateCodeResult.get("columns")).size());
		assertEquals("-", updateCodeResult.get("absoluteVirtualColumnCode"));

		String whereParamsParts = "Map<String, Object> whereParams = new HashMap<>();\n"+
				"whereParams.put(\"jdbc_tool_test_fullCaseMainEntity.StringColumn\",);\n"+
				"whereParams.put(\"jdbc_tool_test_fullCaseMainEntity.objectID\",);\n"+
				"whereParams.put(\"jdbc_tool_test_fullCaseMainEntity.IntColumn\",);\n"+
				"whereParams.put(\"jdbc_tool_test_fullCaseMainEntity.NStringColumn\",);\n";
		String updateRecord = "GRecord record = createRecord();\n"+
				"record.setObject(\"StringColumn\",);\n"+
				"record.setObject(\"IntColumn\",);\n"+
				"record.setObject(\"NStringColumn\",);\n"+
				"record.setObject(\"DateTimeColumn\",);\n"+
				"record.setObject(\"YMColumn\",);\n"+
				"record.setObject(\"CurrencyColumn\",);\n"+
				"record.setObject(\"LongColumn\",);\n"+
				"record.setObject(\"ExclusiveFG\",);\n"+
				"record.setObject(\"RegisteredPerson\",);\n"+
				"record.setObject(\"RegisteredDT\",);\n"+
				"record.setObject(\"UpdatedPerson\",);\n"+
				"record.setObject(\"UpdatedDT\",);\n";
		String updateExecuteApiStr = "update(\"jdbc_tool_test_fullCaseMainEntity\")\n"+ ".set(record)\n";
		String executeApiWhereStr = ".where(expMap($($($(\"jdbc_tool_test_fullCaseMainEntity.objectID = :jdbc_tool_test_fullCaseMainEntity.objectID\").AND(\"jdbc_tool_test_fullCaseMainEntity.StringColumn = :jdbc_tool_test_fullCaseMainEntity.StringColumn\")).OR(\"jdbc_tool_test_fullCaseMainEntity.IntColumn = :jdbc_tool_test_fullCaseMainEntity.IntColumn\").AND(\"jdbc_tool_test_fullCaseMainEntity.NStringColumn = :jdbc_tool_test_fullCaseMainEntity.NStringColumn\")), whereParams))\n";
		assertEquals(whereParamsParts + updateRecord + "\nint count = " + updateExecuteApiStr + executeApiWhereStr + ".execute();", result.get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE).toString()));
		assertEquals(updateRecord + "\nint count = " + updateExecuteApiStr + ".wherePK()\n"+ ".execute();", result.get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_PK).toString()));
		String executeUKStr = ".whereUK(\"MultipleColumnUK\")\n"+ "//.whereUK(\"OtherUKName\")\n"+ "//.whereUK(\"SingleColumnUK\")";
		assertEquals(updateRecord + "\nint count = " + updateExecuteApiStr + executeUKStr + "\n.execute();", result.get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_UK).toString()));

		StringBuilder updateRecordList = new StringBuilder();
		updateRecordList.append("List<GRecord> recordList = new ArrayList<>();").append("\n");
		updateRecordList.append(updateRecord);
		updateRecordList.append("recordList.add(record);").append("\n");

		String updateExecuteListApiStr = "update(\"jdbc_tool_test_fullCaseMainEntity\")\n"+ ".set(recordList)\n";

		assertEquals(updateRecordList + "\nint count = " + updateExecuteListApiStr + ".wherePK()\n"+ ".executeList();", result.get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_PK).toString()));
		assertEquals(updateRecordList + "\nint count = " + updateExecuteListApiStr + executeUKStr + "\n.executeList();", result.get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_UK).toString()));
		assertEquals(updateRecordList + "\nint count = " + updateExecuteListApiStr + ".wherePK()\n"+ ".executeBatch();", result.get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_PK).toString()));
		assertEquals(updateRecordList + "\nint count = " + updateExecuteListApiStr + executeUKStr + "\n.executeBatch();", result.get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_UK).toString()));

		assertEquals("SELECT", result.get(2).getType());
		assertEquals("仮想表ID: A2E4442626F04100A45E747DE5856120", result.get(2).getDescription());
		assertEquals("SELECT\n"+
				"    jdbc_tool_test_fullCaseMainEntity.objectID,\n"+
				"    jdbc_tool_test_fullCaseMainEntity.StringColumn,\n"+
				"    jdbc_tool_test_fullCaseMainEntity.IntColumn,\n"+
				"    jdbc_tool_test_fullCaseMainEntity.NStringColumn,\n"+
				"    jdbc_tool_test_fullCaseMainEntity.DateTimeColumn,\n"+
				"    jdbc_tool_test_fullCaseMainEntity.YMColumn,\n"+
				"    jdbc_tool_test_fullCaseMainEntity.CurrencyColumn,\n"+
				"    jdbc_tool_test_fullCaseMainEntity.LongColumn,\n"+
				"    jdbc_tool_test_fullCaseMainEntity.ExclusiveFG,\n"+
				"    jdbc_tool_test_fullCaseMainEntity.RegisteredPerson,\n"+
				"    jdbc_tool_test_fullCaseMainEntity.RegisteredDT,\n"+
				"    jdbc_tool_test_fullCaseMainEntity.UpdatedPerson,\n"+
				"    jdbc_tool_test_fullCaseMainEntity.UpdatedDT\n"+
				"FROM\n"+
				"    " + dbSchema + ".jdbc_tool_test_fullCaseMainEntity jdbc_tool_test_fullCaseMainEntity\n"+
				"WHERE\n"+
				"    (\n"+
				"        (\n"+
				"            jdbc_tool_test_fullCaseMainEntity.objectID = ?\n"+
				"            AND jdbc_tool_test_fullCaseMainEntity.StringColumn = ?\n"+
				"        )\n"+
				"        OR jdbc_tool_test_fullCaseMainEntity.IntColumn = ?\n"+
				"        AND jdbc_tool_test_fullCaseMainEntity.NStringColumn = ?\n"+
				"    )", result.get(2).getSqlScript());

		Map<String, Object> selectCodeResult = (result.get(2)).getVirtualEntity();
		assertEquals(13, ((List<Map<String,Object>>)selectCodeResult.get("columns")).size());
		assertEquals("-", selectCodeResult.get("absoluteVirtualColumnCode"));

		String selectApiStr = "select(\"jdbc_tool_test_fullCaseMainEntity\", \"jdbc_tool_test_fullCaseMainEntity\")\n"+
				".fields(col(\"jdbc_tool_test_fullCaseMainEntity.objectID\"),col(\"jdbc_tool_test_fullCaseMainEntity.StringColumn\"),col(\"jdbc_tool_test_fullCaseMainEntity.IntColumn\"),col(\"jdbc_tool_test_fullCaseMainEntity.NStringColumn\"),col(\"jdbc_tool_test_fullCaseMainEntity.DateTimeColumn\"),col(\"jdbc_tool_test_fullCaseMainEntity.YMColumn\"),col(\"jdbc_tool_test_fullCaseMainEntity.CurrencyColumn\"),col(\"jdbc_tool_test_fullCaseMainEntity.LongColumn\"),col(\"jdbc_tool_test_fullCaseMainEntity.ExclusiveFG\"),col(\"jdbc_tool_test_fullCaseMainEntity.RegisteredPerson\"),col(\"jdbc_tool_test_fullCaseMainEntity.RegisteredDT\"),col(\"jdbc_tool_test_fullCaseMainEntity.UpdatedPerson\"),col(\"jdbc_tool_test_fullCaseMainEntity.UpdatedDT\"))\n";
		String whereParamsPartsUKPK = "Object[] whereParams = new Object[]{,,,};\n";
		String selectUKStr = ".whereUK(\"MultipleColumnUK\", whereParams)\n"+ "//.whereUK(\"OtherUKName\", whereParams)\n"+ "//.whereUK(\"SingleColumnUK\", whereParams)";
		assertEquals(whereParamsParts + "List<GRecord> result = " + selectApiStr + executeApiWhereStr + ".findList();", result.get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE).toString()));
		assertEquals(whereParamsPartsUKPK + "List<GRecord> result = " + selectApiStr + ".wherePK(whereParams)\n"+ ".findList();", result.get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE_PK).toString()));
		assertEquals(whereParamsPartsUKPK + "List<GRecord> result = " + selectApiStr + selectUKStr + "\n.findList();", result.get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE_UK).toString()));

		assertEquals(whereParamsParts + "GRecord result = " + selectApiStr + executeApiWhereStr + ".findRecord();", result.get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE).toString()));
		assertEquals(whereParamsPartsUKPK + "GRecord result = " + selectApiStr + ".wherePK(whereParams)\n"+ ".findRecord();", result.get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE_PK).toString()));
		assertEquals(whereParamsPartsUKPK + "GRecord result = " + selectApiStr + selectUKStr + "\n.findRecord();", result.get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE_UK).toString()));

		assertEquals(whereParamsParts + "GRecordSet result = " + selectApiStr + executeApiWhereStr + ".findRecordSet();", result.get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE).toString()));
		assertEquals(whereParamsPartsUKPK + "GRecordSet result = " + selectApiStr + ".wherePK(whereParams)\n"+ ".findRecordSet();", result.get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE_PK).toString()));
		assertEquals(whereParamsPartsUKPK + "GRecordSet result = " + selectApiStr + selectUKStr + "\n.findRecordSet();", result.get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE_UK).toString()));

		assertEquals("DELETE", result.get(3).getType());
		assertEquals("仮想表ID: A2E4442626F04100A45E747DE5856120", result.get(3).getDescription());
		assertEquals(getDeleteSql_testExport_expMapCase4(), result.get(3).getSqlScript());

		Map<String, Object> deleteCodeResult = (result.get(3)).getVirtualEntity();
		assertEquals(13, ((List<Map<String,Object>>)deleteCodeResult.get("columns")).size());
		assertEquals("-", selectCodeResult.get("absoluteVirtualColumnCode"));

		String deleteApiStr = "delete(\"jdbc_tool_test_fullCaseMainEntity\")\n";
		assertEquals(whereParamsParts + "int count = " + deleteApiStr + executeApiWhereStr + ".execute();", result.get(3).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE).toString()));

		String deleteRecordStr = "GRecord whereParams = createRecord();\n"+
				"whereParams.setObject(\"objectID\",);\n";

		assertEquals(deleteRecordStr + "\nint count = " + deleteApiStr + ".wherePK(whereParams)\n"+ ".execute();", result.get(3).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_PK).toString()));
		String deleteRecordUKStr = "GRecord whereParams = createRecord();\n"+
				"whereParams.setObject(\"StringColumn\",);\n"+
				"whereParams.setObject(\"IntColumn\",);\n"+
				"whereParams.setObject(\"NStringColumn\",);\n"+
				"//ユーニックキー「OtherUKName」用のカラム\n"+
				"/*whereParams.setObject(\"IntColumn\",);\n"+
				"whereParams.setObject(\"NStringColumn\",);\n"+
				"whereParams.setObject(\"DateTimeColumn\",);\n"+
				"whereParams.setObject(\"YMColumn\",);\n"+
				"*/\n"+
				"//ユーニックキー「SingleColumnUK」用のカラム\n"+
				"/*whereParams.setObject(\"StringColumn\",);\n"+
				"*/\n";

		assertEquals(deleteRecordUKStr + "\nint count = " + deleteApiStr + selectUKStr + "\n.execute();", result.get(3).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_UK).toString()));

		StringBuilder deleteListRecordStr = new StringBuilder();
		deleteListRecordStr.append(deleteRecordStr);
		deleteListRecordStr.append("List<GRecord> whereParamsList = new ArrayList<>();").append("\n");
		deleteListRecordStr.append("whereParamsList.add(whereParams);").append("\n");

		StringBuilder deleteListRecordUKStr = new StringBuilder();
		deleteListRecordUKStr.append(deleteRecordUKStr);
		deleteListRecordUKStr.append("List<GRecord> whereParamsList = new ArrayList<>();").append("\n");
		deleteListRecordUKStr.append("whereParamsList.add(whereParams);").append("\n");
		String deleteListUKStr = ".whereUK(\"MultipleColumnUK\", whereParamsList)\n"+ "//.whereUK(\"OtherUKName\", whereParamsList)\n"+ "//.whereUK(\"SingleColumnUK\", whereParamsList)";

		assertNull(result.get(3).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE).toString()));
		assertEquals(deleteListRecordStr + "\nint count = " + deleteApiStr + ".wherePK(whereParamsList)\n"+ ".executeList();", result.get(3).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_PK).toString()));
		assertEquals(deleteListRecordUKStr + "\nint count = " + deleteApiStr + deleteListUKStr + "\n.executeList();", result.get(3).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_UK).toString()));
		assertNull(result.get(3).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE).toString()));
		assertEquals(deleteListRecordStr + "\nint count = " + deleteApiStr + ".wherePK(whereParamsList)\n"+ ".executeBatch();", result.get(3).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_PK).toString()));
		assertEquals(deleteListRecordUKStr + "\nint count = " + deleteApiStr + deleteListUKStr + "\n.executeBatch();", result.get(3).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_UK).toString()));
	}
	protected String getDeleteSql_testExport_expMapCase4() {
		return "DELETE FROM\n"+
				"    " + dbSchema + ".jdbc_tool_test_fullCaseMainEntity\n"+
				"WHERE\n"+
				"    (\n"+
				"        (\n"+
				"            " + dbSchema + ".jdbc_tool_test_fullCaseMainEntity.objectID = ?\n"+
				"            AND " + dbSchema + ".jdbc_tool_test_fullCaseMainEntity.StringColumn = ?\n"+
				"        )\n"+
				"        OR " + dbSchema + ".jdbc_tool_test_fullCaseMainEntity.IntColumn = ?\n"+
				"        AND " + dbSchema + ".jdbc_tool_test_fullCaseMainEntity.NStringColumn = ?\n"+
				"    )";
	}
	public void cleanInsert(String filePath)  {
		GCommonSetting setting = getCommonSetting().toDaoCommonSetting();
		GDao2Utils.setCommonSetting((GDaoCommonSetting) setting);
		try {
			TestHelper.cleanInsert(setting.getDb(), filePath);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public ApiRequest parseRequestFromFile(SettingRequest commonSetting, String filePath) {
		ApiRequest apiRequest = new ApiRequest();
		try (InputStream input = GFileUtils.getResource(filePath).openStream()) {
			List<Map<String, Object>> map = yaml.load(input);
			apiRequest.setValue(map.get(0));
			apiRequest.setSetting(commonSetting);
			dbSchema = getCommonSetting().toDaoCommonSetting().getDb().getSchema();
			return apiRequest;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public List<CodeResponse> convertVe2Code(ApiRequest request) {
		ObjectMapper objectMapper = new ObjectMapper();
		List<CodeResponse> result = new ArrayList<>();
		ResponseEntity<ApiResponse> sqlExtractorResp = restTemplate.postForEntity(URL+":8081/codes/transformations/sql-extractors", request, ApiResponse.class);

		if (sqlExtractorResp.getBody().isSuccess()) {
			for (Map<String, Object> sqlExtractorMap : (List<Map<String, Object>>)sqlExtractorResp.getBody().getData()) {
				ApiRequest sql2CodeReq = new ApiRequest();
				sql2CodeReq.setSetting(request.getSetting());
				sql2CodeReq.setValue(sqlExtractorMap);
				ResponseEntity<ApiResponse> sql2CodeResp = restTemplate.postForEntity(URL+":8080/dao2jdbc-xform-sql2code/api/codes/transformations/sql2code", sql2CodeReq, ApiResponse.class);
				CodeResponse codeResponse = objectMapper.convertValue(sql2CodeResp.getBody().getData(), CodeResponse.class);
				result.add(codeResponse);
			}
		}

		return result;
	}
}
