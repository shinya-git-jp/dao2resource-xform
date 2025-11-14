package jp.co.kccs.greenearth.xform.dao.code.cmd;

import jp.co.kccs.greenearth.commons.GFrameworkProperties;
import jp.co.kccs.greenearth.commons.GFrameworkUtils;
import jp.co.kccs.greenearth.commons.db.GConnectionManager;
import jp.co.kccs.greenearth.commons.di.GDIContainer;
import jp.co.kccs.greenearth.commons.utils.GFileUtils;
import jp.co.kccs.greenearth.framework.jdbc.GDatabaseImpl;
import jp.co.kccs.greenearth.testutils.dbunit.TestHelper;
import jp.co.kccs.greenearth.xform.code.dao.core.GDao2JdbcXFormCodeResult;
import jp.co.kccs.greenearth.xform.code.jdbc.core.GCompoundApiImpl;
import jp.co.kccs.greenearth.xform.code.jdbc.core.GTerminatorApi;
import jp.co.kccs.greenearth.xform.code.jdbc.core.GWhereApi;
import jp.co.kccs.greenearth.xform.code.jdbc.core.export.GExporterResult;
import jp.co.kccs.greenearth.xform.code.jdbc.core.service.GXFormCodeResult;
import jp.co.kccs.greenearth.xform.dao.common.*;
import jp.co.kccs.greenearth.xform.dao.dto.GDao2VirtualEntity;
import jp.co.kccs.greenearth.xform.jdbc.common.GCrudType;
import jp.co.kccs.greenearth.xform.jdbc.configuration.GDbCommonSetting;
import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * @author KCCS XXXXX
 * @create GEF_NEXT_DATE
 * @since GEF_NEXT_VERSION
 */
public abstract class GDao2XFormCodeCmdServiceTest {
	
	public static String settingsFilePath;
	public static String dbSchema;
	public static GDaoCommonSetting commonSetting;
	
	private static Map<GDaoDbType, String> driverMap = Map.of(
			GDaoDbType.mysql, "com.mysql.cj.jdbc.Driver",
			GDaoDbType.oracle, "oracle.jdbc.driver.OracleDriver",
			GDaoDbType.db2, "com.ibm.db2.jcc.DB2Driver"
	);
	
	protected GDao2XFormCodeCmdService<List<GXFormCodeResult>, byte[]> service = GFrameworkUtils.getComponent(GDao2XFormCodeCmdService.class);
	
	@Before
	public void setup() {
		
		GFrameworkProperties.refleshProperty();
		GFrameworkProperties.setProperty("geframe.commons.Db.MainDataSource", "jdbc/JavaKitDataSource" + getDBType());
		GFrameworkUtils.initDIContainer(null);
		GConnectionManager connManager = GConnectionManager.getInstance();
		connManager.removeConnections();
		
		settingsFilePath = GFileUtils.getResource(getSettingsFilePath()).getPath();
		GDaoCommonSettingParserFilePath commonSettingParser = new GDaoCommonSettingParserFilePath();
		commonSetting = commonSettingParser.parse(settingsFilePath);
		dbSchema = commonSetting.getDb().getSchema();
		GDao2Utils.setCommonSetting(commonSetting);
		setDbSettings(commonSetting.getDb());
	}
	
	protected abstract String getDBType();
	
	protected void setDbSettings(GDbCommonSetting commonSetting) {
		GDIContainer gdiContainer = GFrameworkUtils.getDIContainer();
		GDatabaseImpl database = (GDatabaseImpl)gdiContainer.getComponent("MainDataSource");
		database.setSchema(commonSetting.getSchema());
		database.setDbType(commonSetting.getDbType().getValue());
		BasicDataSource basicDataSource = (BasicDataSource)gdiContainer.getComponent("org.apache.commons.dbcp2.BasicDataSource" + getDBType());
		basicDataSource.setUrl(commonSetting.getUrl());
		basicDataSource.setUsername(commonSetting.getUsername());
		basicDataSource.setPassword(commonSetting.getPassword());
		basicDataSource.setDriverClassName((String)driverMap.get(commonSetting.getDbType()));
	}
	
	@After
	public void teardown() {
		GFrameworkProperties.refleshProperty();
		GFrameworkUtils.registerDIContainer(null);
	}
	
	protected abstract String getSettingsFilePath();
	
	public void cleanInsert(String filePath)  {
		try {
			TestHelper.cleanInsert(commonSetting.getDb(), filePath);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	protected void doCustomConfig(String resourcePath) {
		GConnectionManager connManager = GConnectionManager.getInstance();
		connManager.removeConnections();
		settingsFilePath = GFileUtils.getResource(resourcePath).getPath();
		GDaoCommonSettingParserFilePath commonSettingParser = new GDaoCommonSettingParserFilePath();
		commonSetting = commonSettingParser.parse(settingsFilePath);
		dbSchema = commonSetting.getDb().getSchema();
		GDao2Utils.setCommonSetting(commonSetting);
		setDbSettings(commonSetting.getDb());
	}
	
	protected abstract String getForceAliasColumnConfigPath();
	
	public String getConditionFilePath(String filePath) {
		return GFileUtils.getResource(filePath).getPath();
	}
	
	@Test
	public void testExport_groupFunction() {

		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_groupFunction.xml");
		String conditionFilePath = getConditionFilePath("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_groupFunction.yaml");
		GExporterResult<List<GXFormCodeResult>, byte[]> result = service.export(conditionFilePath, settingsFilePath);
		assertEquals("html", result.getMediaType());
		assertEquals(1, result.getData().size());
		assertEquals(GCrudType.SELECT, result.getData().get(0).getType());
		assertEquals("仮想表ID: E68EF7B747CA42B8ACF08908804DA0DB", result.getData().get(0).getDescription());
		assertEquals("SELECT" + System.lineSeparator() +
				"    MAX(jdbc_tool_test_mainmaster.objectID)," + System.lineSeparator() +
				"    COUNT(jdbc_tool_test_mainmaster.ItemCD)," + System.lineSeparator() +
				"    SUM(jdbc_tool_test_mainmaster.UnitPrice)," + System.lineSeparator() +
				"    AVG(jdbc_tool_test_mainmaster.SellPrice)," + System.lineSeparator() +
				"    MIN(jdbc_tool_test_mainmaster.Flag)," + System.lineSeparator() +
				"    MAX(jdbc_tool_test_mainmaster.PriceFlag)" + System.lineSeparator() +
				"FROM" + System.lineSeparator() +
				"    " + dbSchema + ".jdbc_tool_test_mainmaster jdbc_tool_test_mainmaster" + System.lineSeparator() +
				"GROUP BY" + System.lineSeparator() +
				"    jdbc_tool_test_mainmaster.UnitPrice", result.getData().get(0).getSqlScript());
		String apiStr = String.format("select(\"jdbc_tool_test_mainmaster\", \"jdbc_tool_test_mainmaster\")%n" +
				".fields(col(\"jdbc_tool_test_mainmaster.objectID\", GColumn.FuncType.MAX)," +
				"col(\"jdbc_tool_test_mainmaster.ItemCD\", GColumn.FuncType.COUNT)," +
				"col(\"jdbc_tool_test_mainmaster.UnitPrice\", GColumn.FuncType.SUM)," +
				"col(\"jdbc_tool_test_mainmaster.SellPrice\", GColumn.FuncType.AVG)," +
				"col(\"jdbc_tool_test_mainmaster.Flag\", GColumn.FuncType.MIN)," +
				"col(\"jdbc_tool_test_mainmaster.PriceFlag\", GColumn.FuncType.MAX))%n" +
				".groupBy(col(\"jdbc_tool_test_mainmaster.UnitPrice\"))");
		assertEquals("List<GRecord> result = " + apiStr + System.lineSeparator() + ".findList();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, null)));
		assertNull(result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE)));
		assertNull(result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE_PK)));
		assertNull(result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE_UK)));
		assertEquals("GRecord result = " + apiStr + System.lineSeparator() + ".findRecord();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, null)));
		assertNull(result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE)));
		assertNull(result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE_PK)));
		assertNull(result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE_UK)));
		assertEquals("GRecordSet result = " + apiStr + System.lineSeparator() + ".findRecordSet();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, null)));
		assertNull(result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE)));
		assertNull(result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE_PK)));
		assertNull(result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE_UK)));
	}
	
	
	@Test
	public void testExport_innerJoin() {

		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_innerJoin.xml");
		String conditionFilePath = getConditionFilePath("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_innerJoin.yaml");
		GExporterResult<List<GXFormCodeResult>, byte[]> result = service.export(conditionFilePath, settingsFilePath);
		
		assertEquals("html", result.getMediaType());
		assertEquals(1, result.getData().size());
		assertEquals(GCrudType.SELECT, result.getData().get(0).getType());
		assertEquals("仮想表ID: 7042D90E3612454C88F9FE3AC4FD3322", result.getData().get(0).getDescription());
		assertEquals("SELECT" + System.lineSeparator() +"    jdbc_tool_test_mainmaster.objectID," + System.lineSeparator() +
				"    jdbc_tool_test_mainmaster.ItemCD," + System.lineSeparator() +
				"    jdbc_tool_test_mainmaster.UnitPrice" + System.lineSeparator() +
				"FROM" + System.lineSeparator() +
				"    " + dbSchema + ".jdbc_tool_test_mainmaster jdbc_tool_test_mainmaster" + System.lineSeparator() +
				"    INNER JOIN " + dbSchema + ".jdbc_tool_test_unitmaster jdbc_tool_test_unitmaster ON jdbc_tool_test_mainmaster.ItemCD = jdbc_tool_test_unitmaster.Code"
				, result.getData().get(0).getSqlScript());
		String innerJoinApiStr = String.format("select(\"jdbc_tool_test_mainmaster\", \"jdbc_tool_test_mainmaster\")%n" +
				".fields(col(\"jdbc_tool_test_mainmaster.objectID\"),col(\"jdbc_tool_test_mainmaster.ItemCD\"),col(\"jdbc_tool_test_mainmaster.UnitPrice\"))%n" +
				".innerJoinFK(\"FK\", \"jdbc_tool_test_unitmaster\")");
		
		assertEquals("List<GRecord> result = " + innerJoinApiStr + System.lineSeparator() + ".findList();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, null)));
		assertNull(result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE)));
		assertNull(result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE_PK)));
		assertNull(result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE_UK)));
		assertEquals("GRecord result = " + innerJoinApiStr + System.lineSeparator() + ".findRecord();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, null)));
		assertNull(result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE)));
		assertNull(result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE_PK)));
		assertNull(result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE_UK)));
		assertEquals("GRecordSet result = " + innerJoinApiStr + System.lineSeparator() + ".findRecordSet();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, null)));
		assertNull(result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE)));
		assertNull(result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE_PK)));
		assertNull(result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE_UK)));
	}
	
	@Test
	public void testExport_leftOuterJoin() {

		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_leftOuterJoin.xml");
		String conditionFilePath = getConditionFilePath("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_leftOuterJoin.yaml");
		GExporterResult<List<GXFormCodeResult>, byte[]> result = service.export(conditionFilePath, settingsFilePath);
		
		assertEquals("html", result.getMediaType());
		assertEquals(1, result.getData().size());
		assertEquals(GCrudType.SELECT, result.getData().get(0).getType());
		assertEquals("仮想表ID: 5949D5C4FF624F538D10A7102A4DFB65", result.getData().get(0).getDescription());
		assertEquals("SELECT" + System.lineSeparator() +
				"    jdbc_tool_test_mainmaster.objectID," + System.lineSeparator() +
				"    jdbc_tool_test_mainmaster.ItemCD," + System.lineSeparator() +
				"    jdbc_tool_test_mainmaster.UnitPrice" + System.lineSeparator() +
				"FROM" + System.lineSeparator() +
				"    " + dbSchema + ".jdbc_tool_test_mainmaster jdbc_tool_test_mainmaster" + System.lineSeparator() +
				"    LEFT OUTER JOIN " + dbSchema + ".jdbc_tool_test_ordermaster jdbc_tool_test_ordermaster ON jdbc_tool_test_mainmaster.ItemNA = jdbc_tool_test_ordermaster.CorporateNA"
				, result.getData().get(0).getSqlScript());
		String leftOuterJoinApiStr = String.format("select(\"jdbc_tool_test_mainmaster\", \"jdbc_tool_test_mainmaster\")%n" +
				".fields(col(\"jdbc_tool_test_mainmaster.objectID\"),col(\"jdbc_tool_test_mainmaster.ItemCD\"),col(\"jdbc_tool_test_mainmaster.UnitPrice\"))%n" +
				".leftOuterJoinFK(\"FKleft\", \"jdbc_tool_test_ordermaster\")");
		
		assertEquals("List<GRecord> result = " + leftOuterJoinApiStr + System.lineSeparator() + ".findList();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, null)));
		assertNull(result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE)));
		assertNull(result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE_PK)));
		assertNull(result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE_UK)));
		assertEquals("GRecord result = " + leftOuterJoinApiStr + System.lineSeparator() + ".findRecord();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, null)));
		assertNull(result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE)));
		assertNull(result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE_PK)));
		assertNull(result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE_UK)));
		assertEquals("GRecordSet result = " + leftOuterJoinApiStr + System.lineSeparator() + ".findRecordSet();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, null)));
		assertNull(result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE)));
		assertNull(result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE_PK)));
		assertNull(result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE_UK)));
	}
	
	@Test
	public void testExport_rightOuterJoin() {

		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_rightOuterJoin.xml");
		String conditionFilePath = getConditionFilePath("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_rightOuterJoin.yaml");
		
		GExporterResult<List<GXFormCodeResult>, byte[]> result = service.export(conditionFilePath, settingsFilePath);
		assertEquals("html", result.getMediaType());
		assertEquals(1, result.getData().size());
		assertEquals(GCrudType.SELECT, result.getData().get(0).getType());
		assertEquals("仮想表ID: 9F26B91A99A140DB92ECE5D84178FAE5", result.getData().get(0).getDescription());
		assertEquals("SELECT" + System.lineSeparator() +
				"    jdbc_tool_test_unitmaster.objectID," + System.lineSeparator() +
				"    jdbc_tool_test_unitmaster.Code," + System.lineSeparator() +
				"    jdbc_tool_test_unitmaster.Name" + System.lineSeparator() +
				"FROM" + System.lineSeparator() +
				"    " + dbSchema + ".jdbc_tool_test_unitmaster jdbc_tool_test_unitmaster" + System.lineSeparator() +
				"    RIGHT OUTER JOIN " + dbSchema + ".jdbc_tool_test_ordermaster jdbc_tool_test_ordermaster ON jdbc_tool_test_unitmaster.Name = jdbc_tool_test_ordermaster.CorporateNA"
				, result.getData().get(0).getSqlScript());
		String rightOuterJoinApiStr = String.format("select(\"jdbc_tool_test_unitmaster\", \"jdbc_tool_test_unitmaster\")%n" +
				".fields(colsAll())%n" +
				".rightOuterJoinFK(\"FKRight\", \"jdbc_tool_test_ordermaster\")");
		
		assertEquals("List<GRecord> result = " + rightOuterJoinApiStr + System.lineSeparator() + ".findList();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, null)));
		assertNull(result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE)));
		assertNull(result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE_PK)));
		assertNull(result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE_UK)));
		assertEquals("GRecord result = " + rightOuterJoinApiStr + System.lineSeparator() + ".findRecord();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, null)));
		assertNull(result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE)));
		assertNull(result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE_PK)));
		assertNull(result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE_UK)));
		assertEquals("GRecordSet result = " + rightOuterJoinApiStr + System.lineSeparator() + ".findRecordSet();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, null)));
		assertNull(result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE)));
		assertNull(result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE_PK)));
		assertNull(result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE_UK)));
	}

	@Test
	public void testExport_groupBy() {

		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_groupBy.xml");
		String conditionFilePath = getConditionFilePath("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_groupBy.yaml");
		GExporterResult<List<GXFormCodeResult>, byte[]> result = service.export(conditionFilePath, settingsFilePath);

		assertEquals("html", result.getMediaType());
		assertEquals(1, result.getData().size());
		assertEquals(GCrudType.SELECT, result.getData().get(0).getType());
		assertEquals("仮想表ID: 0530ABBD2F0A4A0F8D7E6F58776897F7", result.getData().get(0).getDescription());
		assertEquals("SELECT" + System.lineSeparator() +
				"    MAX(jdbc_tool_test_mainmaster.objectID)," + System.lineSeparator() +
				"    MAX(jdbc_tool_test_mainmaster.ItemCD)," + System.lineSeparator() +
				"    MAX(jdbc_tool_test_mainmaster.UnitPrice)" + System.lineSeparator() +
				"FROM" + System.lineSeparator() +
				"    " + dbSchema + ".jdbc_tool_test_mainmaster jdbc_tool_test_mainmaster" + System.lineSeparator() +
				"GROUP BY" + System.lineSeparator() +
				"    jdbc_tool_test_mainmaster.UnitPrice", result.getData().get(0).getSqlScript());
		String groupByApiStr = String.format("select(\"jdbc_tool_test_mainmaster\", \"jdbc_tool_test_mainmaster\")%n" +
				".fields(col(\"jdbc_tool_test_mainmaster.objectID\", GColumn.FuncType.MAX)," +
				"col(\"jdbc_tool_test_mainmaster.ItemCD\", GColumn.FuncType.MAX)," +
				"col(\"jdbc_tool_test_mainmaster.UnitPrice\", GColumn.FuncType.MAX))%n" +
				".groupBy(col(\"jdbc_tool_test_mainmaster.UnitPrice\"))");

		assertEquals("List<GRecord> result = " + groupByApiStr + System.lineSeparator() + ".findList();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, null)));
		assertEquals("GRecord result = " + groupByApiStr + System.lineSeparator() + ".findRecord();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, null)));
		assertEquals("GRecordSet result = " + groupByApiStr + System.lineSeparator() + ".findRecordSet();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, null)));
	}

	@Test
	public void testExport_multipleGroupBy() {

		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_multipleGroupBy.xml");
		String conditionFilePath = getConditionFilePath("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_multipleGroupBy.yaml");
		GExporterResult<List<GXFormCodeResult>, byte[]> result = service.export(conditionFilePath, settingsFilePath);

		assertEquals("html", result.getMediaType());
		assertEquals(1, result.getData().size());

		assertEquals(GCrudType.SELECT, result.getData().get(0).getType());
		assertEquals("仮想表ID: F61014D80B8A41A8AD7016376CD055C0", result.getData().get(0).getDescription());
		assertEquals("SELECT" + System.lineSeparator() +
				"    MAX(jdbc_tool_test_mainmaster.objectID)," + System.lineSeparator() +
				"    MAX(jdbc_tool_test_mainmaster.ItemCD)," + System.lineSeparator() +
				"    MAX(jdbc_tool_test_mainmaster.UnitPrice)" + System.lineSeparator() +
				"FROM" + System.lineSeparator() +
				"    " + dbSchema + ".jdbc_tool_test_mainmaster jdbc_tool_test_mainmaster" + System.lineSeparator() +
				"GROUP BY" + System.lineSeparator() +
				"    jdbc_tool_test_mainmaster.UnitPrice," + System.lineSeparator() +
				"    jdbc_tool_test_mainmaster.ItemCD", result.getData().get(0).getSqlScript());
		String multipleGroupByApiStr = String.format("select(\"jdbc_tool_test_mainmaster\", \"jdbc_tool_test_mainmaster\")%n" +
				".fields(col(\"jdbc_tool_test_mainmaster.objectID\", GColumn.FuncType.MAX)," +
				"col(\"jdbc_tool_test_mainmaster.ItemCD\", GColumn.FuncType.MAX)," +
				"col(\"jdbc_tool_test_mainmaster.UnitPrice\", GColumn.FuncType.MAX))%n" +
				".groupBy(col(\"jdbc_tool_test_mainmaster.UnitPrice\"),col(\"jdbc_tool_test_mainmaster.ItemCD\"))");

		assertEquals("List<GRecord> result = " + multipleGroupByApiStr + System.lineSeparator() + ".findList();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, null)));
		assertEquals("GRecord result = " + multipleGroupByApiStr + System.lineSeparator() + ".findRecord();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, null)));
		assertEquals("GRecordSet result = " + multipleGroupByApiStr + System.lineSeparator() + ".findRecordSet();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, null)));
	}

	@Test
	public void testExport_orderByAsc() {

		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_orderByAsc.xml");
		String conditionFilePath = getConditionFilePath("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_orderByAsc.yaml");
		GExporterResult<List<GXFormCodeResult>, byte[]> result = service.export(conditionFilePath, settingsFilePath);

		assertEquals("html", result.getMediaType());
		assertEquals(1, result.getData().size());
		assertEquals(GCrudType.SELECT, result.getData().get(0).getType());
		assertEquals("仮想表ID: F8B25A24B8D44D8F9F31D75A4A6E434D", result.getData().get(0).getDescription());
		assertEquals("SELECT" + System.lineSeparator() +
				"    jdbc_tool_test_mainmaster.objectID," + System.lineSeparator() +
				"    jdbc_tool_test_mainmaster.ItemCD," + System.lineSeparator() +
				"    jdbc_tool_test_mainmaster.UnitPrice" + System.lineSeparator() +
				"FROM" + System.lineSeparator() +
				"    " + dbSchema + ".jdbc_tool_test_mainmaster jdbc_tool_test_mainmaster" + System.lineSeparator() +
				"ORDER BY" + System.lineSeparator() +
				"    jdbc_tool_test_mainmaster.UnitPrice ASC", result.getData().get(0).getSqlScript());
		String orderByApiStr = String.format("select(\"jdbc_tool_test_mainmaster\", \"jdbc_tool_test_mainmaster\")%n" +
				".fields(col(\"jdbc_tool_test_mainmaster.objectID\")," +
				"col(\"jdbc_tool_test_mainmaster.ItemCD\")," +
				"col(\"jdbc_tool_test_mainmaster.UnitPrice\"))%n" +
				".orderBy(asc(\"jdbc_tool_test_mainmaster.UnitPrice\"))");

		assertEquals("List<GRecord> result = " + orderByApiStr + System.lineSeparator() + ".findList();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, null)));
		assertEquals("GRecord result = " + orderByApiStr + System.lineSeparator() + ".findRecord();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, null)));
		assertEquals("GRecordSet result = " + orderByApiStr + System.lineSeparator() + ".findRecordSet();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, null)));
	}

	@Test
	public void testExport_multipleOrderByAsc() {

		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_multipleOrderByAsc.xml");
		String conditionFilePath = getConditionFilePath("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_multipleOrderByAsc.yaml");
		GExporterResult<List<GXFormCodeResult>, byte[]> result = service.export(conditionFilePath, settingsFilePath);

		assertEquals("html", result.getMediaType());
		assertEquals(1, result.getData().size());
		assertEquals(GCrudType.SELECT, result.getData().get(0).getType());
		assertEquals("仮想表ID: DD05A220FF144B60A3E7CE4E0C9CA2AA", result.getData().get(0).getDescription());
		assertEquals("SELECT" + System.lineSeparator() +
				"    jdbc_tool_test_mainmaster.objectID," + System.lineSeparator() +
				"    jdbc_tool_test_mainmaster.ItemCD," + System.lineSeparator() +
				"    jdbc_tool_test_mainmaster.UnitPrice" + System.lineSeparator() +
				"FROM" + System.lineSeparator() +
				"    " + dbSchema + ".jdbc_tool_test_mainmaster jdbc_tool_test_mainmaster" + System.lineSeparator() +
				"ORDER BY" + System.lineSeparator() +
				"    jdbc_tool_test_mainmaster.UnitPrice ASC," + System.lineSeparator() +
				"    jdbc_tool_test_mainmaster.ItemCD ASC", result.getData().get(0).getSqlScript());
		String multipleOrderByApiStr = String.format("select(\"jdbc_tool_test_mainmaster\", \"jdbc_tool_test_mainmaster\")%n" +
				".fields(col(\"jdbc_tool_test_mainmaster.objectID\")," +
				"col(\"jdbc_tool_test_mainmaster.ItemCD\")," +
				"col(\"jdbc_tool_test_mainmaster.UnitPrice\"))%n" +
				".orderBy(asc(\"jdbc_tool_test_mainmaster.UnitPrice\"),asc(\"jdbc_tool_test_mainmaster.ItemCD\"))");

		assertEquals("List<GRecord> result = " + multipleOrderByApiStr + System.lineSeparator() + ".findList();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, null)));
		assertEquals("GRecord result = " + multipleOrderByApiStr + System.lineSeparator() + ".findRecord();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, null)));
		assertEquals("GRecordSet result = " + multipleOrderByApiStr + System.lineSeparator() + ".findRecordSet();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, null)));
	}

	@Test
	public void testExport_orderByDesc() {

		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_orderByDesc.xml");
		String conditionFilePath = getConditionFilePath("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_orderByDesc.yaml");
		GExporterResult<List<GXFormCodeResult>, byte[]> result = service.export(conditionFilePath, settingsFilePath);

		assertEquals("html", result.getMediaType());
		assertEquals(1, result.getData().size());
		assertEquals(GCrudType.SELECT, result.getData().get(0).getType());
		assertEquals("仮想表ID: C1F0698184FB4E058A5704FA7B87855B", result.getData().get(0).getDescription());
		assertEquals("SELECT" + System.lineSeparator() +
				"    jdbc_tool_test_mainmaster.objectID," + System.lineSeparator() +
				"    jdbc_tool_test_mainmaster.ItemCD," + System.lineSeparator() +
				"    jdbc_tool_test_mainmaster.UnitPrice" + System.lineSeparator() +
				"FROM" + System.lineSeparator() +
				"    " + dbSchema + ".jdbc_tool_test_mainmaster jdbc_tool_test_mainmaster" + System.lineSeparator() +
				"ORDER BY" + System.lineSeparator() +
				"    jdbc_tool_test_mainmaster.UnitPrice DESC", result.getData().get(0).getSqlScript());
		String orderByDescApiStr = String.format("select(\"jdbc_tool_test_mainmaster\", \"jdbc_tool_test_mainmaster\")%n" +
				".fields(col(\"jdbc_tool_test_mainmaster.objectID\")," +
				"col(\"jdbc_tool_test_mainmaster.ItemCD\")," +
				"col(\"jdbc_tool_test_mainmaster.UnitPrice\"))%n" +
				".orderBy(desc(\"jdbc_tool_test_mainmaster.UnitPrice\"))");

		assertEquals("List<GRecord> result = " + orderByDescApiStr + System.lineSeparator() + ".findList();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, null)));
		assertEquals("GRecord result = " + orderByDescApiStr + System.lineSeparator() + ".findRecord();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, null)));
		assertEquals("GRecordSet result = " + orderByDescApiStr + System.lineSeparator() + ".findRecordSet();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, null)));
	}

	@Test
	public void testExport_multipleOrderByDesc() {

		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_multipleOrderByDesc.xml");
		String conditionFilePath = getConditionFilePath("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_multipleOrderByDesc.yaml");
		GExporterResult<List<GXFormCodeResult>, byte[]> result = service.export(conditionFilePath, settingsFilePath);

		assertEquals("html", result.getMediaType());
		assertEquals(1, result.getData().size());
		assertEquals(GCrudType.SELECT, result.getData().get(0).getType());
		assertEquals("仮想表ID: 1FB32673659841CAA624EB1C98C14BA4", result.getData().get(0).getDescription());
		assertEquals("SELECT" + System.lineSeparator() +
				"    jdbc_tool_test_mainmaster.objectID," + System.lineSeparator() +
				"    jdbc_tool_test_mainmaster.ItemCD," + System.lineSeparator() +
				"    jdbc_tool_test_mainmaster.UnitPrice" + System.lineSeparator() +
				"FROM" + System.lineSeparator() +
				"    " + dbSchema + ".jdbc_tool_test_mainmaster jdbc_tool_test_mainmaster" + System.lineSeparator() +
				"ORDER BY" + System.lineSeparator() +
				"    jdbc_tool_test_mainmaster.UnitPrice DESC," + System.lineSeparator() +
				"    jdbc_tool_test_mainmaster.ItemCD DESC", result.getData().get(0).getSqlScript());
		String multipleOrderByDescApiStr = String.format("select(\"jdbc_tool_test_mainmaster\", \"jdbc_tool_test_mainmaster\")%n" +
				".fields(col(\"jdbc_tool_test_mainmaster.objectID\")," +
				"col(\"jdbc_tool_test_mainmaster.ItemCD\")," +
				"col(\"jdbc_tool_test_mainmaster.UnitPrice\"))%n" +
				".orderBy(desc(\"jdbc_tool_test_mainmaster.UnitPrice\"),desc(\"jdbc_tool_test_mainmaster.ItemCD\"))");

		assertEquals("List<GRecord> result = " + multipleOrderByDescApiStr + System.lineSeparator() + ".findList();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, null)));
		assertEquals("GRecord result = " + multipleOrderByDescApiStr + System.lineSeparator() + ".findRecord();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, null)));
		assertEquals("GRecordSet result = " + multipleOrderByDescApiStr + System.lineSeparator() + ".findRecordSet();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, null)));
	}

	@Test
	public void testExport_orderByAscDescMix() {

		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_orderByAscDescMix.xml");
		String conditionFilePath = getConditionFilePath("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_orderByAscDescMix.yaml");
		GExporterResult<List<GXFormCodeResult>, byte[]> result = service.export(conditionFilePath, settingsFilePath);

		assertEquals("html", result.getMediaType());
		assertEquals(1, result.getData().size());
		assertEquals(GCrudType.SELECT, result.getData().get(0).getType());
		assertEquals("仮想表ID: EC68E86DD2894D8496765DCD8189C735", result.getData().get(0).getDescription());
		assertEquals("SELECT" + System.lineSeparator() +
				"    jdbc_tool_test_mainmaster.objectID," + System.lineSeparator() +
				"    jdbc_tool_test_mainmaster.ItemCD," + System.lineSeparator() +
				"    jdbc_tool_test_mainmaster.UnitPrice" + System.lineSeparator() +
				"FROM" + System.lineSeparator() +
				"    " + dbSchema + ".jdbc_tool_test_mainmaster jdbc_tool_test_mainmaster" + System.lineSeparator() +
				"ORDER BY" + System.lineSeparator() +
				"    jdbc_tool_test_mainmaster.UnitPrice DESC," + System.lineSeparator() +
				"    jdbc_tool_test_mainmaster.ItemCD ASC", result.getData().get(0).getSqlScript());
		String orderByAscDescMixApiStr = String.format("select(\"jdbc_tool_test_mainmaster\", \"jdbc_tool_test_mainmaster\")%n" +
				".fields(col(\"jdbc_tool_test_mainmaster.objectID\")," +
				"col(\"jdbc_tool_test_mainmaster.ItemCD\")," +
				"col(\"jdbc_tool_test_mainmaster.UnitPrice\"))%n" +
				".orderBy(desc(\"jdbc_tool_test_mainmaster.UnitPrice\"),asc(\"jdbc_tool_test_mainmaster.ItemCD\"))");

		assertEquals("List<GRecord> result = " + orderByAscDescMixApiStr + System.lineSeparator() + ".findList();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, null)));
		assertEquals("GRecord result = " + orderByAscDescMixApiStr + System.lineSeparator() + ".findRecord();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, null)));
		assertEquals("GRecordSet result = " + orderByAscDescMixApiStr + System.lineSeparator() + ".findRecordSet();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, null)));
	}

	@Test
	public void testExport_searchExpWherePartsLikeF() {

		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_searchExpWherePartsLikeF.xml");
		String conditionFilePath = getConditionFilePath("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_searchExpWherePartsLikeF.yaml");
		GExporterResult<List<GXFormCodeResult>, byte[]> result = service.export(conditionFilePath, settingsFilePath);

		assertEquals("html", result.getMediaType());
		assertEquals(1, result.getData().size());
		assertEquals(GCrudType.SELECT, result.getData().get(0).getType());
		assertEquals("仮想表ID: 27A84C6E3DCC43939E45785E4088039E", result.getData().get(0).getDescription());
		assertEquals("SELECT" + System.lineSeparator() +
				"    jdbc_tool_test_mainmaster.objectID," + System.lineSeparator() +
				"    jdbc_tool_test_mainmaster.ItemCD," + System.lineSeparator() +
				"    jdbc_tool_test_mainmaster.UnitPrice" + System.lineSeparator() +
				"FROM" + System.lineSeparator() +
				"    " + dbSchema + ".jdbc_tool_test_mainmaster jdbc_tool_test_mainmaster" + System.lineSeparator() +
				"WHERE" + System.lineSeparator() +
				"    jdbc_tool_test_mainmaster.ItemCD LIKE ? ESCAPE '/'", result.getData().get(0).getSqlScript());
		String whereParamsParts = "Map<String, Object> whereParams = new HashMap<>();" + System.lineSeparator() +
				"whereParams.put(\"jdbc_tool_test_mainmaster.ItemCD\",);" + System.lineSeparator();
		String searchExpWherePartsLikeFApiStr = "select(\"jdbc_tool_test_mainmaster\", \"jdbc_tool_test_mainmaster\")" + System.lineSeparator() +
				".fields(col(\"jdbc_tool_test_mainmaster.objectID\"),col(\"jdbc_tool_test_mainmaster.ItemCD\"),col(\"jdbc_tool_test_mainmaster.UnitPrice\"))" + System.lineSeparator() +
				".where(expMap($(\"jdbc_tool_test_mainmaster.ItemCD LIKE $CONCAT(:jdbc_tool_test_mainmaster.ItemCD, '%') ESCAPE '/'\"), whereParams))";

		assertEquals(whereParamsParts + "List<GRecord> result = " + searchExpWherePartsLikeFApiStr + System.lineSeparator() + ".findList();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE)));
		assertEquals(whereParamsParts + "GRecord result = " + searchExpWherePartsLikeFApiStr + System.lineSeparator() + ".findRecord();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE)));
		assertEquals(whereParamsParts + "GRecordSet result = " + searchExpWherePartsLikeFApiStr + System.lineSeparator() + ".findRecordSet();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE)));
	}

	@Test
	public void testExport_searchExpWherePartsLikeFB() {

		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_searchExpWherePartsLikeFB.xml");
		String conditionFilePath = getConditionFilePath("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_searchExpWherePartsLikeFB.yaml");
		GExporterResult<List<GXFormCodeResult>, byte[]> result = service.export(conditionFilePath, settingsFilePath);

		assertEquals("html", result.getMediaType());
		assertEquals(1, result.getData().size());
		assertEquals(GCrudType.SELECT, result.getData().get(0).getType());
		assertEquals("仮想表ID: F8DA3B5A055A4402945AE83B013884A4", result.getData().get(0).getDescription());
		assertEquals("SELECT" + System.lineSeparator() +
				"    jdbc_tool_test_unitmaster.objectID," + System.lineSeparator() +
				"    jdbc_tool_test_unitmaster.Code," + System.lineSeparator() +
				"    jdbc_tool_test_unitmaster.Name" + System.lineSeparator() +
				"FROM" + System.lineSeparator() +
				"    " + dbSchema + ".jdbc_tool_test_unitmaster jdbc_tool_test_unitmaster" + System.lineSeparator() +
				"WHERE" + System.lineSeparator() +
				"    jdbc_tool_test_unitmaster.Name LIKE ? ESCAPE '/'", result.getData().get(0).getSqlScript());
		String whereParamsParts = "Map<String, Object> whereParams = new HashMap<>();" + System.lineSeparator() +
				"whereParams.put(\"jdbc_tool_test_unitmaster.Name\",);" + System.lineSeparator();
		String groupByApiStr = "select(\"jdbc_tool_test_unitmaster\", \"jdbc_tool_test_unitmaster\")" + System.lineSeparator() +
				".fields(colsAll())" + System.lineSeparator() +
				".where(expMap($(\"jdbc_tool_test_unitmaster.Name LIKE $CONCAT('%', $CONCAT(:jdbc_tool_test_unitmaster.Name, '%')) ESCAPE '/'\"), whereParams))";

		assertEquals(whereParamsParts + "List<GRecord> result = " + groupByApiStr + System.lineSeparator() + ".findList();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE)));
		assertEquals(whereParamsParts + "GRecord result = " + groupByApiStr + System.lineSeparator() + ".findRecord();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE)));
		assertEquals(whereParamsParts + "GRecordSet result = " + groupByApiStr + System.lineSeparator() + ".findRecordSet();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE)));
	}

	@Test
	public void testExport_searchExpWherePartsLikeB() {

		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_searchExpWherePartsLikeB.xml");
		String conditionFilePath = getConditionFilePath("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_searchExpWherePartsLikeB.yaml");
		GExporterResult<List<GXFormCodeResult>, byte[]> result = service.export(conditionFilePath, settingsFilePath);

		assertEquals("html", result.getMediaType());
		assertEquals(1, result.getData().size());
		assertEquals(GCrudType.SELECT, result.getData().get(0).getType());
		assertEquals("仮想表ID: E463537F2A224BABBAB675A335772A7F", result.getData().get(0).getDescription());
		assertEquals("SELECT" + System.lineSeparator() +
				"    jdbc_tool_test_ordermaster.objectID," + System.lineSeparator() +
				"    jdbc_tool_test_ordermaster.SlipNO," + System.lineSeparator() +
				"    jdbc_tool_test_ordermaster.CorporateNA" + System.lineSeparator() +
				"FROM" + System.lineSeparator() +
				"    " + dbSchema + ".jdbc_tool_test_ordermaster jdbc_tool_test_ordermaster" + System.lineSeparator() +
				"WHERE" + System.lineSeparator() +
				"    jdbc_tool_test_ordermaster.CorporateNA LIKE ? ESCAPE '/'", result.getData().get(0).getSqlScript());
		String whereParamsParts = "Map<String, Object> whereParams = new HashMap<>();" + System.lineSeparator() +
				"whereParams.put(\"jdbc_tool_test_ordermaster.CorporateNA\",);" + System.lineSeparator();
		String groupByApiStr = "select(\"jdbc_tool_test_ordermaster\", \"jdbc_tool_test_ordermaster\")" + System.lineSeparator() +
				".fields(col(\"jdbc_tool_test_ordermaster.objectID\")," +
				"col(\"jdbc_tool_test_ordermaster.SlipNO\")," +
				"col(\"jdbc_tool_test_ordermaster.CorporateNA\"))" + System.lineSeparator() +
				".where(expMap($(\"jdbc_tool_test_ordermaster.CorporateNA LIKE $CONCAT('%', :jdbc_tool_test_ordermaster.CorporateNA) ESCAPE '/'\"), whereParams))";

		assertEquals(whereParamsParts + "List<GRecord> result = " + groupByApiStr + System.lineSeparator() + ".findList();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE)));
		assertEquals(whereParamsParts + "GRecord result = " + groupByApiStr + System.lineSeparator() + ".findRecord();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE)));
		assertEquals(whereParamsParts + "GRecordSet result = " + groupByApiStr + System.lineSeparator() + ".findRecordSet();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE)));
	}

	@Test
	public void testExport_searchExpWherePartsEquals() {

		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_searchExpWherePartsEquals.xml");
		String conditionFilePath = getConditionFilePath("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_searchExpWherePartsEquals.yaml");
		GExporterResult<List<GXFormCodeResult>, byte[]> result = service.export(conditionFilePath, settingsFilePath);

		assertEquals("html", result.getMediaType());
		assertEquals(1, result.getData().size());
		assertEquals(GCrudType.SELECT, result.getData().get(0).getType());
		assertEquals("仮想表ID: 4E52A89202B145159B8256C53A433A24", result.getData().get(0).getDescription());
		assertEquals("SELECT" + System.lineSeparator() +
				"    jdbc_tool_test_mainmaster.objectID," + System.lineSeparator() +
				"    jdbc_tool_test_mainmaster.ItemCD," + System.lineSeparator() +
				"    jdbc_tool_test_mainmaster.UnitPrice" + System.lineSeparator() +
				"FROM" + System.lineSeparator() +
				"    " + dbSchema + ".jdbc_tool_test_mainmaster jdbc_tool_test_mainmaster" + System.lineSeparator() +
				"WHERE" + System.lineSeparator() +
				"    (" + System.lineSeparator() +
				"        jdbc_tool_test_mainmaster.ItemCD = ?" + System.lineSeparator() +
				"        AND jdbc_tool_test_mainmaster.UnitPrice <> ?" + System.lineSeparator() +
				"    )", result.getData().get(0).getSqlScript());
		String whereParamsParts = "Map<String, Object> whereParams = new HashMap<>();" + System.lineSeparator() +
				"whereParams.put(\"jdbc_tool_test_mainmaster.UnitPrice\",);" + System.lineSeparator() +
				"whereParams.put(\"jdbc_tool_test_mainmaster.ItemCD\",);" + System.lineSeparator();
		String groupByApiStr = String.format("select(\"jdbc_tool_test_mainmaster\", \"jdbc_tool_test_mainmaster\")%n" +
				".fields(col(\"jdbc_tool_test_mainmaster.objectID\")," +
				"col(\"jdbc_tool_test_mainmaster.ItemCD\")," +
				"col(\"jdbc_tool_test_mainmaster.UnitPrice\"))%n" +
				".where(expMap($($(\"jdbc_tool_test_mainmaster.ItemCD = $TRIM([:jdbc_tool_test_mainmaster.ItemCD])\").AND(\"jdbc_tool_test_mainmaster.UnitPrice <> $TRIM([:jdbc_tool_test_mainmaster.UnitPrice])\")), whereParams))");

		assertEquals(whereParamsParts + "List<GRecord> result = " + groupByApiStr + System.lineSeparator() + ".findList();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE)));
		assertEquals(whereParamsParts + "GRecord result = " + groupByApiStr + System.lineSeparator() + ".findRecord();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE)));
		assertEquals(whereParamsParts + "GRecordSet result = " + groupByApiStr + System.lineSeparator() + ".findRecordSet();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE)));
	}

	@Test
	public void testExport_searchExpWherePartsCompare() {

		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_searchExpWherePartsCompare.xml");
		String conditionFilePath = getConditionFilePath("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_searchExpWherePartsCompare.yaml");
		GExporterResult<List<GXFormCodeResult>, byte[]> result = service.export(conditionFilePath, settingsFilePath);

		assertEquals("html", result.getMediaType());
		assertEquals(1, result.getData().size());
		assertEquals(GCrudType.SELECT, result.getData().get(0).getType());
		assertEquals("仮想表ID: 9F4455A3BB6C49438764521C9331F01C", result.getData().get(0).getDescription());
		assertEquals("SELECT" + System.lineSeparator() +
				"    jdbc_tool_test_mainmaster.objectID," + System.lineSeparator() +
				"    jdbc_tool_test_mainmaster.ItemCD," + System.lineSeparator() +
				"    jdbc_tool_test_mainmaster.UnitPrice," + System.lineSeparator() +
				"    jdbc_tool_test_mainmaster.SellPrice," + System.lineSeparator() +
				"    jdbc_tool_test_mainmaster.Flag," + System.lineSeparator() +
				"    jdbc_tool_test_mainmaster.PriceFlag" + System.lineSeparator() +
				"FROM" + System.lineSeparator() +
				"    " + dbSchema + ".jdbc_tool_test_mainmaster jdbc_tool_test_mainmaster" + System.lineSeparator() +
				"WHERE" + System.lineSeparator() +
				"    (" + System.lineSeparator() +
				"        jdbc_tool_test_mainmaster.UnitPrice > ?" + System.lineSeparator() +
				"        AND jdbc_tool_test_mainmaster.SellPrice < ?" + System.lineSeparator() +
				"        AND jdbc_tool_test_mainmaster.Flag <= ?" + System.lineSeparator() +
				"        AND jdbc_tool_test_mainmaster.PriceFlag >= ?" + System.lineSeparator() +
				"    )", result.getData().get(0).getSqlScript());
		String whereParamsParts = "Map<String, Object> whereParams = new HashMap<>();" + System.lineSeparator() +
				"whereParams.put(\"jdbc_tool_test_mainmaster.Flag\",);" + System.lineSeparator() +
				"whereParams.put(\"jdbc_tool_test_mainmaster.UnitPrice\",);" + System.lineSeparator() +
				"whereParams.put(\"jdbc_tool_test_mainmaster.PriceFlag\",);" + System.lineSeparator() +
				"whereParams.put(\"jdbc_tool_test_mainmaster.SellPrice\",);" + System.lineSeparator();
		String groupByApiStr = String.format("select(\"jdbc_tool_test_mainmaster\", \"jdbc_tool_test_mainmaster\")%n" +
				".fields(col(\"jdbc_tool_test_mainmaster.objectID\")," +
				"col(\"jdbc_tool_test_mainmaster.ItemCD\")," +
				"col(\"jdbc_tool_test_mainmaster.UnitPrice\")," +
				"col(\"jdbc_tool_test_mainmaster.SellPrice\")," +
				"col(\"jdbc_tool_test_mainmaster.Flag\")," +
				"col(\"jdbc_tool_test_mainmaster.PriceFlag\"))%n" +
				".where(expMap($($(\"jdbc_tool_test_mainmaster.UnitPrice > :jdbc_tool_test_mainmaster.UnitPrice\").AND(\"jdbc_tool_test_mainmaster.SellPrice < :jdbc_tool_test_mainmaster.SellPrice\").AND(\"jdbc_tool_test_mainmaster.Flag <= :jdbc_tool_test_mainmaster.Flag\").AND(\"jdbc_tool_test_mainmaster.PriceFlag >= :jdbc_tool_test_mainmaster.PriceFlag\")), whereParams))");

		assertEquals(whereParamsParts + "List<GRecord> result = " + groupByApiStr + System.lineSeparator() + ".findList();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE)));
		assertEquals(whereParamsParts + "GRecord result = " + groupByApiStr + System.lineSeparator() + ".findRecord();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE)));
		assertEquals(whereParamsParts + "GRecordSet result = " + groupByApiStr + System.lineSeparator() + ".findRecordSet();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE)));
	}

	@Test
	public void testExport_searchExpWherePartsOptionTrim() {

		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_searchExpWherePartsOptionTrim.xml");
		String conditionFilePath = getConditionFilePath("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_searchExpWherePartsOptionTrim.yaml");
		GExporterResult<List<GXFormCodeResult>, byte[]> result = service.export(conditionFilePath, settingsFilePath);

		assertEquals("html", result.getMediaType());
		assertEquals(1, result.getData().size());
		assertEquals(GCrudType.SELECT, result.getData().get(0).getType());
		assertEquals("仮想表ID: D6FA080F2A074718821223C549ABEFD4", result.getData().get(0).getDescription());
		assertEquals("SELECT" + System.lineSeparator() +
				"    jdbc_tool_test_mainmaster.objectID," + System.lineSeparator() +
				"    jdbc_tool_test_mainmaster.ItemCD," + System.lineSeparator() +
				"    jdbc_tool_test_mainmaster.UnitPrice," + System.lineSeparator() +
				"    jdbc_tool_test_mainmaster.RegisteredDT," + System.lineSeparator() +
				"    jdbc_tool_test_mainmaster.ExclusiveFG" + System.lineSeparator() +
				"FROM" + System.lineSeparator() +
				"    " + dbSchema + ".jdbc_tool_test_mainmaster jdbc_tool_test_mainmaster" + System.lineSeparator() +
				"    INNER JOIN " + dbSchema + ".jdbc_tool_test_unitmaster jdbc_tool_test_unitmaster ON jdbc_tool_test_mainmaster.ItemCD = jdbc_tool_test_unitmaster.Code" + System.lineSeparator() +
				"WHERE" + System.lineSeparator() +
				"    jdbc_tool_test_mainmaster.UnitPrice IN (100, 101, 102)" + System.lineSeparator() +
				"    AND (" + System.lineSeparator() +
				"        jdbc_tool_test_mainmaster.objectID LIKE ? ESCAPE '/'" + System.lineSeparator() +
				"        AND jdbc_tool_test_mainmaster.UnitPrice >= ?" + System.lineSeparator() +
				"    )" + System.lineSeparator() +
				"ORDER BY" + System.lineSeparator() +
				"    jdbc_tool_test_mainmaster.RegisteredDT ASC", result.getData().get(0).getSqlScript());
		String whereParamsParts = "Map<String, Object> whereParams = new HashMap<>();" + System.lineSeparator() +
				"whereParams.put(\"jdbc_tool_test_mainmaster.UnitPrice\",);" + System.lineSeparator() +
				"whereParams.put(\"jdbc_tool_test_mainmaster.objectID\",);" + System.lineSeparator();
		String groupByApiStr = "select(\"jdbc_tool_test_mainmaster\", \"jdbc_tool_test_mainmaster\")" + System.lineSeparator() +
				".fields(col(\"jdbc_tool_test_mainmaster.objectID\")," +
				"col(\"jdbc_tool_test_mainmaster.ItemCD\")," +
				"col(\"jdbc_tool_test_mainmaster.UnitPrice\")," +
				"col(\"jdbc_tool_test_mainmaster.RegisteredDT\")," +
				"col(\"jdbc_tool_test_mainmaster.ExclusiveFG\"))" + System.lineSeparator() +
				".innerJoinFK(\"FK\", \"jdbc_tool_test_unitmaster\")" + System.lineSeparator() +
				".orderBy(asc(\"jdbc_tool_test_mainmaster.RegisteredDT\"))" + System.lineSeparator() +
				".where(expMap($(\"jdbc_tool_test_mainmaster.UnitPrice IN (100, 101, 102)\").AND($(\"jdbc_tool_test_mainmaster.objectID LIKE $CONCAT('%', $CONCAT($TRIM([:jdbc_tool_test_mainmaster.objectID]), '%')) ESCAPE '/'\").AND(\"jdbc_tool_test_mainmaster.UnitPrice >= $TRIM([:jdbc_tool_test_mainmaster.UnitPrice])\")), whereParams))";

		assertEquals(whereParamsParts + "List<GRecord> result = " + groupByApiStr + System.lineSeparator() + ".findList();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE)));
		assertEquals(whereParamsParts + "GRecord result = " + groupByApiStr + System.lineSeparator() + ".findRecord();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE)));
		assertEquals(whereParamsParts + "GRecordSet result = " + groupByApiStr + System.lineSeparator() + ".findRecordSet();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE)));
	}

	@Test
	public void testExport_filterExpWherePartsFilterLikeF() {

		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_filterExpWherePartsFilterLikeF.xml");
		String conditionFilePath = getConditionFilePath("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_filterExpWherePartsLikeF.yaml");
		GExporterResult<List<GXFormCodeResult>, byte[]> result = service.export(conditionFilePath, settingsFilePath);

		assertEquals("html", result.getMediaType());
		assertEquals(1, result.getData().size());
		assertEquals(GCrudType.SELECT, result.getData().get(0).getType());
		assertEquals("仮想表ID: 5423003851B248BEA1621ACFB69B4D28", result.getData().get(0).getDescription());
		assertEquals("SELECT" + System.lineSeparator() +
				"    jdbc_tool_test_mainmaster.objectID," + System.lineSeparator() +
				"    jdbc_tool_test_mainmaster.ItemCD," + System.lineSeparator() +
				"    jdbc_tool_test_mainmaster.UnitPrice" + System.lineSeparator() +
				"FROM" + System.lineSeparator() +
				"    " + dbSchema + ".jdbc_tool_test_mainmaster jdbc_tool_test_mainmaster" + System.lineSeparator() +
				"WHERE" + System.lineSeparator() +
				"    jdbc_tool_test_mainmaster.ItemCD LIKE 'item%'", result.getData().get(0).getSqlScript());
		String groupByApiStr = "select(\"jdbc_tool_test_mainmaster\", \"jdbc_tool_test_mainmaster\")" + System.lineSeparator() +
				".fields(col(\"jdbc_tool_test_mainmaster.objectID\")," +
				"col(\"jdbc_tool_test_mainmaster.ItemCD\")," +
				"col(\"jdbc_tool_test_mainmaster.UnitPrice\"))" + System.lineSeparator() +
				".where(expMap($(\"jdbc_tool_test_mainmaster.ItemCD LIKE 'item%'\"), Collections.emptyMap()))";

		assertEquals("List<GRecord> result = " + groupByApiStr + System.lineSeparator() + ".findList();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE)));
		assertEquals("GRecord result = " + groupByApiStr + System.lineSeparator() + ".findRecord();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE)));
		assertEquals("GRecordSet result = " + groupByApiStr + System.lineSeparator() + ".findRecordSet();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE)));
	}

	@Test
	public void testExport_filterExpWherePartsFilterLikeFB() {

		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_filterExpWherePartsFilterLikeFB.xml");
		String conditionFilePath = getConditionFilePath("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_filterExpWherePartsLikeFB.yaml");
		GExporterResult<List<GXFormCodeResult>, byte[]> result = service.export(conditionFilePath, settingsFilePath);

		assertEquals("html", result.getMediaType());
		assertEquals(1, result.getData().size());
		assertEquals(GCrudType.SELECT, result.getData().get(0).getType());
		assertEquals("仮想表ID: 581587D1CA97431A92611EEC6587C09A", result.getData().get(0).getDescription());
		assertEquals("SELECT" + System.lineSeparator() +
				"    jdbc_tool_test_unitmaster.objectID," + System.lineSeparator() +
				"    jdbc_tool_test_unitmaster.Code," + System.lineSeparator() +
				"    jdbc_tool_test_unitmaster.Name" + System.lineSeparator() +
				"FROM" + System.lineSeparator() +
				"    " + dbSchema + ".jdbc_tool_test_unitmaster jdbc_tool_test_unitmaster" + System.lineSeparator() +
				"WHERE" + System.lineSeparator() +
				"    jdbc_tool_test_unitmaster.Name LIKE '%name%'", result.getData().get(0).getSqlScript());
		String groupByApiStr = "select(\"jdbc_tool_test_unitmaster\", \"jdbc_tool_test_unitmaster\")" + System.lineSeparator() +
				".fields(colsAll())" + System.lineSeparator() +
				".where(expMap($(\"jdbc_tool_test_unitmaster.Name LIKE '%name%'\"), Collections.emptyMap()))";

		assertEquals("List<GRecord> result = " + groupByApiStr + System.lineSeparator() + ".findList();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE)));
		assertEquals("GRecord result = " + groupByApiStr + System.lineSeparator() + ".findRecord();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE)));
		assertEquals("GRecordSet result = " + groupByApiStr + System.lineSeparator() + ".findRecordSet();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE)));
	}

	@Test
	public void testExport_filterExpWherePartsFilterLikeB() {

		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_filterExpWherePartsFilterLikeB.xml");

		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_filterExpWherePartsFilterLikeB.xml");
		String conditionFilePath = getConditionFilePath("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_filterExpWherePartsLikeB.yaml");
		GExporterResult<List<GXFormCodeResult>, byte[]> result = service.export(conditionFilePath, settingsFilePath);

		assertEquals("html", result.getMediaType());
		assertEquals(1, result.getData().size());
		assertEquals(GCrudType.SELECT, result.getData().get(0).getType());
		assertEquals("仮想表ID: 4DD746B1BD794D3B83C42272C9615E67", result.getData().get(0).getDescription());
		assertEquals("SELECT" + System.lineSeparator() +
				"    jdbc_tool_test_ordermaster.objectID," + System.lineSeparator() +
				"    jdbc_tool_test_ordermaster.SlipNO," + System.lineSeparator() +
				"    jdbc_tool_test_ordermaster.CorporateNA" + System.lineSeparator() +
				"FROM" + System.lineSeparator() +
				"    " + dbSchema + ".jdbc_tool_test_ordermaster jdbc_tool_test_ordermaster" + System.lineSeparator() +
				"WHERE" + System.lineSeparator() +
				"    jdbc_tool_test_ordermaster.CorporateNA LIKE '%na'", result.getData().get(0).getSqlScript());
		String groupByApiStr = "select(\"jdbc_tool_test_ordermaster\", \"jdbc_tool_test_ordermaster\")" + System.lineSeparator() +
				".fields(col(\"jdbc_tool_test_ordermaster.objectID\")," +
				"col(\"jdbc_tool_test_ordermaster.SlipNO\")," +
				"col(\"jdbc_tool_test_ordermaster.CorporateNA\"))" + System.lineSeparator() +
				".where(expMap($(\"jdbc_tool_test_ordermaster.CorporateNA LIKE '%na'\"), Collections.emptyMap()))";

		assertEquals("List<GRecord> result = " + groupByApiStr + System.lineSeparator() + ".findList();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE)));
		assertEquals("GRecord result = " + groupByApiStr + System.lineSeparator() + ".findRecord();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE)));
		assertEquals("GRecordSet result = " + groupByApiStr + System.lineSeparator() + ".findRecordSet();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE)));
	}

	@Test
	public void testExport_filterExpWherePartsEquals() {

		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_filterExpWherePartsEquals.xml");
		String conditionFilePath = getConditionFilePath("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_filterExpWherePartsEquals.yaml");
		GExporterResult<List<GXFormCodeResult>, byte[]> result = service.export(conditionFilePath, settingsFilePath);

		assertEquals("html", result.getMediaType());
		assertEquals(1, result.getData().size());
		assertEquals(GCrudType.SELECT, result.getData().get(0).getType());
		assertEquals("仮想表ID: 184DB91E2A434FCB9F5A94389E690333", result.getData().get(0).getDescription());
		assertEquals("SELECT" + System.lineSeparator() +
				"    jdbc_tool_test_mainmaster.objectID," + System.lineSeparator() +
				"    jdbc_tool_test_mainmaster.ItemCD," + System.lineSeparator() +
				"    jdbc_tool_test_mainmaster.UnitPrice" + System.lineSeparator() +
				"FROM" + System.lineSeparator() +
				"    " + dbSchema + ".jdbc_tool_test_mainmaster jdbc_tool_test_mainmaster" + System.lineSeparator() +
				"WHERE" + System.lineSeparator() +
				"    (" + System.lineSeparator() +
				"        jdbc_tool_test_mainmaster.objectID = '1001'" + System.lineSeparator() +
				"        AND jdbc_tool_test_mainmaster.ItemCD <> '9001'" + System.lineSeparator() +
				"    )", result.getData().get(0).getSqlScript());
		String groupByApiStr = "select(\"jdbc_tool_test_mainmaster\", \"jdbc_tool_test_mainmaster\")" + System.lineSeparator() +
				".fields(col(\"jdbc_tool_test_mainmaster.objectID\")," +
				"col(\"jdbc_tool_test_mainmaster.ItemCD\")," +
				"col(\"jdbc_tool_test_mainmaster.UnitPrice\"))" + System.lineSeparator() +
				".where(expMap($($(\"jdbc_tool_test_mainmaster.objectID = '1001'\").AND(\"jdbc_tool_test_mainmaster.ItemCD <> '9001'\")), Collections.emptyMap()))";

		assertEquals("List<GRecord> result = " + groupByApiStr + System.lineSeparator() + ".findList();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE)));
		assertEquals("GRecord result = " + groupByApiStr + System.lineSeparator() + ".findRecord();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE)));
		assertEquals("GRecordSet result = " + groupByApiStr + System.lineSeparator() + ".findRecordSet();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE)));
	}

	@Test
	public void testExport_filterExpWherePartsCompare() {

		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_filterExpWherePartsCompare.xml");
		String conditionFilePath = getConditionFilePath("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_filterExpWherePartsCompare.yaml");
		GExporterResult<List<GXFormCodeResult>, byte[]> result = service.export(conditionFilePath, settingsFilePath);

		assertEquals("html", result.getMediaType());
		assertEquals(1, result.getData().size());
		assertEquals(GCrudType.SELECT, result.getData().get(0).getType());
		assertEquals("仮想表ID: 6C660C8AFC13477589AD71FF629280A2", result.getData().get(0).getDescription());
		assertEquals("SELECT" + System.lineSeparator() +
				"    jdbc_tool_test_mainmaster.objectID," + System.lineSeparator() +
				"    jdbc_tool_test_mainmaster.ItemCD," + System.lineSeparator() +
				"    jdbc_tool_test_mainmaster.UnitPrice," + System.lineSeparator() +
				"    jdbc_tool_test_mainmaster.SellPrice," + System.lineSeparator() +
				"    jdbc_tool_test_mainmaster.Flag," + System.lineSeparator() +
				"    jdbc_tool_test_mainmaster.PriceFlag" + System.lineSeparator() +
				"FROM" + System.lineSeparator() +
				"    " + dbSchema + ".jdbc_tool_test_mainmaster jdbc_tool_test_mainmaster" + System.lineSeparator() +
				"WHERE" + System.lineSeparator() +
				"    (" + System.lineSeparator() +
				"        jdbc_tool_test_mainmaster.UnitPrice > 100" + System.lineSeparator() +
				"        AND jdbc_tool_test_mainmaster.UnitPrice < 500" + System.lineSeparator() +
				"        AND jdbc_tool_test_mainmaster.SellPrice <= 1000" + System.lineSeparator() +
				"        AND jdbc_tool_test_mainmaster.SellPrice >= 300" + System.lineSeparator() +
				"    )", result.getData().get(0).getSqlScript());
		String groupByApiStr = "select(\"jdbc_tool_test_mainmaster\", \"jdbc_tool_test_mainmaster\")" + System.lineSeparator() +
				".fields(col(\"jdbc_tool_test_mainmaster.objectID\")," +
				"col(\"jdbc_tool_test_mainmaster.ItemCD\")," +
				"col(\"jdbc_tool_test_mainmaster.UnitPrice\")," +
				"col(\"jdbc_tool_test_mainmaster.SellPrice\")," +
				"col(\"jdbc_tool_test_mainmaster.Flag\")," +
				"col(\"jdbc_tool_test_mainmaster.PriceFlag\"))" + System.lineSeparator() +
				".where(expMap($($(\"jdbc_tool_test_mainmaster.UnitPrice > 100\").AND(\"jdbc_tool_test_mainmaster.UnitPrice < 500\").AND(\"jdbc_tool_test_mainmaster.SellPrice <= 1000\").AND(\"jdbc_tool_test_mainmaster.SellPrice >= 300\")), Collections.emptyMap()))";

		assertEquals("List<GRecord> result = " + groupByApiStr + System.lineSeparator() + ".findList();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE)));
		assertEquals("GRecord result = " + groupByApiStr + System.lineSeparator() + ".findRecord();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE)));
		assertEquals("GRecordSet result = " + groupByApiStr + System.lineSeparator() + ".findRecordSet();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE)));
	}

	@Test
	public void testExport_filterExpWherePartsNull() {

		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_filterExpWherePartsNull.xml");
		String conditionFilePath = getConditionFilePath("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_filterExpWherePartsNull.yaml");
		GExporterResult<List<GXFormCodeResult>, byte[]> result = service.export(conditionFilePath, settingsFilePath);

		assertEquals("html", result.getMediaType());
		assertEquals(1, result.getData().size());
		assertEquals(GCrudType.SELECT, result.getData().get(0).getType());
		assertEquals("仮想表ID: AED40FC1076E4D388AC4DA1AB616F95A", result.getData().get(0).getDescription());
		assertEquals("SELECT" + System.lineSeparator() +
				"    jdbc_tool_test_mainmaster.objectID," + System.lineSeparator() +
				"    jdbc_tool_test_mainmaster.ItemCD," + System.lineSeparator() +
				"    jdbc_tool_test_mainmaster.UnitPrice" + System.lineSeparator() +
				"FROM" + System.lineSeparator() +
				"    " + dbSchema + ".jdbc_tool_test_mainmaster jdbc_tool_test_mainmaster" + System.lineSeparator() +
				"WHERE" + System.lineSeparator() +
				"    (" + System.lineSeparator() +
				"        jdbc_tool_test_mainmaster.ItemCD IS NULL" + System.lineSeparator() +
				"        AND jdbc_tool_test_mainmaster.UnitPrice IS NOT NULL" + System.lineSeparator() +
				"    )", result.getData().get(0).getSqlScript());
		String groupByApiStr = "select(\"jdbc_tool_test_mainmaster\", \"jdbc_tool_test_mainmaster\")" + System.lineSeparator() +
				".fields(col(\"jdbc_tool_test_mainmaster.objectID\")," +
				"col(\"jdbc_tool_test_mainmaster.ItemCD\")," +
				"col(\"jdbc_tool_test_mainmaster.UnitPrice\"))" + System.lineSeparator() +
				".where(expMap($($(\"jdbc_tool_test_mainmaster.ItemCD IS NULL\").AND(\"jdbc_tool_test_mainmaster.UnitPrice IS NOT NULL\")), Collections.emptyMap()))";

		assertEquals("List<GRecord> result = " + groupByApiStr + System.lineSeparator() + ".findList();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE)));
		assertEquals("GRecord result = " + groupByApiStr + System.lineSeparator() + ".findRecord();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE)));
		assertEquals("GRecordSet result = " + groupByApiStr + System.lineSeparator() + ".findRecordSet();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE)));
	}

	@Test
	public void testExport_filterExpWherePartsIn() {

		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_filterExpWherePartsIn.xml");
		String conditionFilePath = getConditionFilePath("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_filterExpWherePartsIn.yaml");
		GExporterResult<List<GXFormCodeResult>, byte[]> result = service.export(conditionFilePath, settingsFilePath);

		assertEquals("html", result.getMediaType());
		assertEquals(1, result.getData().size());
		assertEquals(GCrudType.SELECT, result.getData().get(0).getType());
		assertEquals("仮想表ID: 64761AEFA524454EBA2094036685F46E", result.getData().get(0).getDescription());
		assertEquals("SELECT" + System.lineSeparator() +
				"    jdbc_tool_test_mainmaster.objectID," + System.lineSeparator() +
				"    jdbc_tool_test_mainmaster.ItemCD," + System.lineSeparator() +
				"    jdbc_tool_test_mainmaster.UnitPrice" + System.lineSeparator() +
				"FROM" + System.lineSeparator() +
				"    " + dbSchema + ".jdbc_tool_test_mainmaster jdbc_tool_test_mainmaster" + System.lineSeparator() +
				"WHERE" + System.lineSeparator() +
				"    (" + System.lineSeparator() +
				"        jdbc_tool_test_mainmaster.ItemCD IN ('item010', '1001')" + System.lineSeparator() +
				"        AND jdbc_tool_test_mainmaster.UnitPrice NOT IN (10, 20, 30)" + System.lineSeparator() +
				"    )", result.getData().get(0).getSqlScript());
		String groupByApiStr = "select(\"jdbc_tool_test_mainmaster\", \"jdbc_tool_test_mainmaster\")" + System.lineSeparator() +
				".fields(col(\"jdbc_tool_test_mainmaster.objectID\")," +
				"col(\"jdbc_tool_test_mainmaster.ItemCD\")," +
				"col(\"jdbc_tool_test_mainmaster.UnitPrice\"))" + System.lineSeparator() +
				".where(expMap($($(\"jdbc_tool_test_mainmaster.ItemCD IN ('item010', '1001')\").AND(\"jdbc_tool_test_mainmaster.UnitPrice NOT IN (10, 20, 30)\")), Collections.emptyMap()))";

		assertEquals("List<GRecord> result = " + groupByApiStr + System.lineSeparator() + ".findList();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE)));
		assertEquals("GRecord result = " + groupByApiStr + System.lineSeparator() + ".findRecord();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE)));
		assertEquals("GRecordSet result = " + groupByApiStr + System.lineSeparator() + ".findRecordSet();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE)));
	}

	@Test
	public void testExport_wherePartsPK_inq() {

		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_wherePartsPK_inq.xml");
		String conditionFilePath = getConditionFilePath("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_wherePartsPK_inq.yaml");
		GExporterResult<List<GXFormCodeResult>, byte[]> result = service.export(conditionFilePath, settingsFilePath);

		assertEquals("html", result.getMediaType());
		assertEquals(1, result.getData().size());
		assertEquals(GCrudType.SELECT, result.getData().get(0).getType());
		assertEquals("仮想表ID: 57E66373CB914D9F88DB638EAF6B1699", result.getData().get(0).getDescription());
		assertEquals("SELECT" + System.lineSeparator() +
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
				"WHERE" + System.lineSeparator() +
				"    jdbc_tool_test_pk_uk_mainmaster.objectID = ?", result.getData().get(0).getSqlScript());
		String whereParamsParts = "Map<String, Object> whereParams = new HashMap<>();" + System.lineSeparator() +
				"whereParams.put(\"jdbc_tool_test_pk_uk_mainmaster.objectID\",);" + System.lineSeparator();
		String groupByApiStr = "select(\"jdbc_tool_test_pk_uk_mainmaster\", \"jdbc_tool_test_pk_uk_mainmaster\")" + System.lineSeparator() +
				".fields(colsAll())" + System.lineSeparator() +
				".where(expMap($(\"jdbc_tool_test_pk_uk_mainmaster.objectID = :jdbc_tool_test_pk_uk_mainmaster.objectID\"), whereParams))";

		assertEquals(whereParamsParts + "List<GRecord> result = " + groupByApiStr + System.lineSeparator() + ".findList();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE)));
		assertEquals(whereParamsParts + "GRecord result = " + groupByApiStr + System.lineSeparator() + ".findRecord();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE)));
		assertEquals(whereParamsParts + "GRecordSet result = " + groupByApiStr + System.lineSeparator() + ".findRecordSet();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE)));
	}

	@Test
	public void testExport_wherePartsUK_inq() {

		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_wherePartsUK_inq.xml");
		String conditionFilePath = getConditionFilePath("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_wherePartsUK_inq.yaml");
		GExporterResult<List<GXFormCodeResult>, byte[]> result = service.export(conditionFilePath, settingsFilePath);

		assertEquals("html", result.getMediaType());
		assertEquals(1, result.getData().size());
		assertEquals(GCrudType.SELECT, result.getData().get(0).getType());
		assertEquals("仮想表ID: 4EE394D81C334C2DA3C37670DE3A8277", result.getData().get(0).getDescription());
		assertEquals("SELECT" + System.lineSeparator() +
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
				"WHERE" + System.lineSeparator() +
				"    (" + System.lineSeparator() +
				"        jdbc_tool_test_pk_uk_mainmaster.ItemCD = ?" + System.lineSeparator() +
				"        AND jdbc_tool_test_pk_uk_mainmaster.ItemNA = ?" + System.lineSeparator() +
				"    )", result.getData().get(0).getSqlScript());
		String whereParamsParts = "Map<String, Object> whereParams = new HashMap<>();" + System.lineSeparator() +
				"whereParams.put(\"jdbc_tool_test_pk_uk_mainmaster.ItemCD\",);" + System.lineSeparator() +
				"whereParams.put(\"jdbc_tool_test_pk_uk_mainmaster.ItemNA\",);" + System.lineSeparator();
		String groupByApiStr = "select(\"jdbc_tool_test_pk_uk_mainmaster\", \"jdbc_tool_test_pk_uk_mainmaster\")" + System.lineSeparator() +
				".fields(colsAll())" + System.lineSeparator() +
				".where(expMap($($(\"jdbc_tool_test_pk_uk_mainmaster.ItemCD = :jdbc_tool_test_pk_uk_mainmaster.ItemCD\").AND(\"jdbc_tool_test_pk_uk_mainmaster.ItemNA = :jdbc_tool_test_pk_uk_mainmaster.ItemNA\")), whereParams))";

		assertEquals(whereParamsParts + "List<GRecord> result = " + groupByApiStr + System.lineSeparator() + ".findList();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE)));
		assertEquals(whereParamsParts + "GRecord result = " + groupByApiStr + System.lineSeparator() + ".findRecord();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE)));
		assertEquals(whereParamsParts + "GRecordSet result = " + groupByApiStr + System.lineSeparator() + ".findRecordSet();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE)));
	}

	@Test
	public void testExport_wherePartsPKAndUK_inq() {

		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_wherePartsPKAndUK_inq.xml");
		String conditionFilePath = getConditionFilePath("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_wherePartsPKAndUK_inq.yaml");
		GExporterResult<List<GXFormCodeResult>, byte[]> result = service.export(conditionFilePath, settingsFilePath);

		assertEquals("html", result.getMediaType());
		assertEquals(1, result.getData().size());
		assertEquals(GCrudType.SELECT, result.getData().get(0).getType());
		assertEquals("仮想表ID: C8BE7F91E3134D9984D2922A390EC098", result.getData().get(0).getDescription());
		assertEquals("SELECT" + System.lineSeparator() +
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
				"WHERE" + System.lineSeparator() +
				"    (" + System.lineSeparator() +
				"        jdbc_tool_test_pk_uk_mainmaster.ItemCD = ?" + System.lineSeparator() +
				"        AND jdbc_tool_test_pk_uk_mainmaster.ItemNA = ?" + System.lineSeparator() +
				"        AND jdbc_tool_test_pk_uk_mainmaster.objectID = ?" + System.lineSeparator() +
				"    )", result.getData().get(0).getSqlScript());
		String whereParamsParts = "Map<String, Object> whereParams = new HashMap<>();" + System.lineSeparator() +
				"whereParams.put(\"jdbc_tool_test_pk_uk_mainmaster.ItemCD\",);" + System.lineSeparator() +
				"whereParams.put(\"jdbc_tool_test_pk_uk_mainmaster.ItemNA\",);" + System.lineSeparator() +
				"whereParams.put(\"jdbc_tool_test_pk_uk_mainmaster.objectID\",);" + System.lineSeparator();
		String groupByApiStr = "select(\"jdbc_tool_test_pk_uk_mainmaster\", \"jdbc_tool_test_pk_uk_mainmaster\")" + System.lineSeparator() +
				".fields(colsAll())" + System.lineSeparator() +
				".where(expMap($($(\"jdbc_tool_test_pk_uk_mainmaster.ItemCD = :jdbc_tool_test_pk_uk_mainmaster.ItemCD\").AND(\"jdbc_tool_test_pk_uk_mainmaster.ItemNA = :jdbc_tool_test_pk_uk_mainmaster.ItemNA\").AND(\"jdbc_tool_test_pk_uk_mainmaster.objectID = :jdbc_tool_test_pk_uk_mainmaster.objectID\")), whereParams))";

		assertEquals(whereParamsParts + "List<GRecord> result = " + groupByApiStr + System.lineSeparator() + ".findList();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE)));
		assertEquals(whereParamsParts + "GRecord result = " + groupByApiStr + System.lineSeparator() + ".findRecord();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE)));
		assertEquals(whereParamsParts + "GRecordSet result = " + groupByApiStr + System.lineSeparator() + ".findRecordSet();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE)));
	}

	@Test
	public void testExport_wherePartsNotPKOrUK_inq() {

		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_wherePartsNotPKOrUK_inq.xml");
		String conditionFilePath = getConditionFilePath("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_wherePartsNotPKOrUK_inq.yaml");
		GExporterResult<List<GXFormCodeResult>, byte[]> result = service.export(conditionFilePath, settingsFilePath);

		assertEquals("html", result.getMediaType());
		assertEquals(1, result.getData().size());
		assertEquals(GCrudType.SELECT, result.getData().get(0).getType());
		assertEquals("仮想表ID: AAD0B7190546440EAB44DE85FB559161", result.getData().get(0).getDescription());
		assertEquals("SELECT" + System.lineSeparator() +
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
				"WHERE" + System.lineSeparator() +
				"    jdbc_tool_test_pk_uk_mainmaster.UnitPrice = ?", result.getData().get(0).getSqlScript());
		String whereParamsParts = "Map<String, Object> whereParams = new HashMap<>();" + System.lineSeparator() +
				"whereParams.put(\"jdbc_tool_test_pk_uk_mainmaster.UnitPrice\",);" + System.lineSeparator();
		String groupByApiStr = "select(\"jdbc_tool_test_pk_uk_mainmaster\", \"jdbc_tool_test_pk_uk_mainmaster\")" + System.lineSeparator() +
				".fields(colsAll())" + System.lineSeparator() +
				".where(expMap($(\"jdbc_tool_test_pk_uk_mainmaster.UnitPrice = :jdbc_tool_test_pk_uk_mainmaster.UnitPrice\"), whereParams))";

		assertEquals(whereParamsParts + "List<GRecord> result = " + groupByApiStr + System.lineSeparator() + ".findList();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE)));
		assertEquals(whereParamsParts + "GRecord result = " + groupByApiStr + System.lineSeparator() + ".findRecord();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE)));
		assertEquals(whereParamsParts + "GRecordSet result = " + groupByApiStr + System.lineSeparator() + ".findRecordSet();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE)));
	}

	@Test
	public void testExport_wherePartsNotCondition_inq() {

		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_wherePartsNotCondition_inq.xml");
		String conditionFilePath = getConditionFilePath("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_wherePartsNotCondition_inq.yaml");
		GExporterResult<List<GXFormCodeResult>, byte[]> result = service.export(conditionFilePath, settingsFilePath);

		assertEquals("html", result.getMediaType());
		assertEquals(1, result.getData().size());
		assertEquals(GCrudType.SELECT, result.getData().get(0).getType());
		assertEquals("仮想表ID: 4BE51C99F70E4FE7ADB3936CBF752599", result.getData().get(0).getDescription());
		assertEquals("SELECT" + System.lineSeparator() +
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
				"    " + dbSchema + ".jdbc_tool_test_pk_uk_mainmaster jdbc_tool_test_pk_uk_mainmaster", result.getData().get(0).getSqlScript());
		String groupByApiStr = "select(\"jdbc_tool_test_pk_uk_mainmaster\", \"jdbc_tool_test_pk_uk_mainmaster\")" + System.lineSeparator() +
				".fields(colsAll())";

		assertEquals("List<GRecord> result = " + groupByApiStr + System.lineSeparator() + ".findList();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, null)));
		assertEquals("GRecord result = " + groupByApiStr + System.lineSeparator() + ".findRecord();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, null)));
		assertEquals("GRecordSet result = " + groupByApiStr + System.lineSeparator() + ".findRecordSet();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, null)));
	}

	@Test
	public void testExport_wherePartsPK_reg_update() {

		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_wherePartsPK_reg_update.xml");
		String conditionFilePath = getConditionFilePath("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_wherePartsPK_reg_update.yaml");
		GExporterResult<List<GXFormCodeResult>, byte[]> result = service.export(conditionFilePath, settingsFilePath);

		assertEquals("html", result.getMediaType());
		assertEquals(1, result.getData().size());
		assertEquals(GCrudType.UPDATE, result.getData().get(0).getType());
		assertEquals("仮想表ID: 1FEFE156727646DC9122045339BD3999", result.getData().get(0).getDescription());
		assertEquals("UPDATE" + System.lineSeparator() +
				"    " + dbSchema + ".jdbc_tool_test_pk_uk_mainmaster jdbc_tool_test_pk_uk_mainmaster" + System.lineSeparator() +
				"SET" + System.lineSeparator() +
				"    ItemCD = ?," + System.lineSeparator() +
				"    ItemNA = ?," + System.lineSeparator() +
				"    UnitPrice = ?," + System.lineSeparator() +
				"    SellPrice = ?," + System.lineSeparator() +
				"    Flag = ?," + System.lineSeparator() +
				"    PriceFlag = ?," + System.lineSeparator() +
				"    ExclusiveFG = ?," + System.lineSeparator() +
				"    RegisteredDT = ?," + System.lineSeparator() +
				"    UpdatedDT = ?" + System.lineSeparator() +
				"WHERE" + System.lineSeparator() +
				"    jdbc_tool_test_pk_uk_mainmaster.objectID = ?", result.getData().get(0).getSqlScript());
		String whereParamsParts = "Map<String, Object> whereParams = new HashMap<>();" + System.lineSeparator() +
				"whereParams.put(\"jdbc_tool_test_pk_uk_mainmaster.objectID\",);" + System.lineSeparator();
		String recordStr = String.format("GRecord record = createRecord();%n" +
				"record.setObject(\"ItemCD\",);%n" +
				"record.setObject(\"ItemNA\",);%n" +
				"record.setObject(\"UnitPrice\",);%n" +
				"record.setObject(\"SellPrice\",);%n" +
				"record.setObject(\"Flag\",);%n" +
				"record.setObject(\"PriceFlag\",);%n" +
				"record.setObject(\"ExclusiveFG\",);%n" +
				"record.setObject(\"RegisteredDT\",);%n" +
				"record.setObject(\"UpdatedDT\",);%n");
		String executeApiStr = "update(\"jdbc_tool_test_pk_uk_mainmaster\")" + System.lineSeparator() +
				".set(record)" + System.lineSeparator();
		String executeApiWhereStr = ".where(expMap($(\"jdbc_tool_test_pk_uk_mainmaster.objectID = :jdbc_tool_test_pk_uk_mainmaster.objectID\"), whereParams))";
		assertEquals(whereParamsParts + recordStr + System.lineSeparator() + "int count = " + executeApiStr + executeApiWhereStr + System.lineSeparator() + ".execute();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE)));
		assertEquals(recordStr + System.lineSeparator() + "int count = " + executeApiStr + ".wherePK()" + System.lineSeparator() + ".execute();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_PK)));
		assertEquals(recordStr + System.lineSeparator() + "int count = " + executeApiStr + ".whereUK(\"jdbc_tool_test_uk\")" + System.lineSeparator() + "//.whereUK(\"jdbc_tool_test_uk2\")" + System.lineSeparator() + ".execute();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_UK)));

		StringBuilder recordsStr = new StringBuilder();
		recordsStr.append("List<GRecord> recordList = new ArrayList<>();").append(System.lineSeparator());
		recordsStr.append(recordStr);
		recordsStr.append("recordList.add(record);").append(System.lineSeparator());
		String executeListApiStr = "update(\"jdbc_tool_test_pk_uk_mainmaster\")" + System.lineSeparator() +
				".set(recordList)" + System.lineSeparator();
		assertEquals(recordsStr + System.lineSeparator() + "int count = " + executeListApiStr + ".wherePK()" + System.lineSeparator() + ".executeBatch();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_PK)));
		assertEquals(recordsStr + System.lineSeparator() + "int count = " + executeListApiStr + ".whereUK(\"jdbc_tool_test_uk\")" + System.lineSeparator() + "//.whereUK(\"jdbc_tool_test_uk2\")" + System.lineSeparator() + ".executeBatch();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_UK)));
		assertEquals(recordsStr + System.lineSeparator() + "int count = " + executeListApiStr + ".wherePK()" + System.lineSeparator() + ".executeList();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_PK)));
		assertEquals(recordsStr + System.lineSeparator() + "int count = " + executeListApiStr + ".whereUK(\"jdbc_tool_test_uk\")" + System.lineSeparator() + "//.whereUK(\"jdbc_tool_test_uk2\")" + System.lineSeparator() + ".executeList();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_UK)));
	}

	@Test
	public void testExport_wherePartsPK_reg_delete() {

		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_wherePartsPK_reg_delete.xml");
		String conditionFilePath = getConditionFilePath("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_wherePartsPK_reg_delete.yaml");
		GExporterResult<List<GXFormCodeResult>, byte[]> result = service.export(conditionFilePath, settingsFilePath);

		assertEquals("html", result.getMediaType());
		assertEquals(1, result.getData().size());
		assertEquals(GCrudType.DELETE, result.getData().get(0).getType());
		assertEquals("仮想表ID: 1FEFE156727646DC9122045339BD3999", result.getData().get(0).getDescription());
		assertEquals(getDeleteSql_WherePartsPK_reg_delete(), result.getData().get(0).getSqlScript());
		String recordStr = "Map<String, Object> whereParams = new HashMap<>();" + System.lineSeparator() +
				"whereParams.put(\"jdbc_tool_test_pk_uk_mainmaster.objectID\",);";
		String executeApiStr = "delete(\"jdbc_tool_test_pk_uk_mainmaster\")" + System.lineSeparator() +
				".where(expMap($(\"jdbc_tool_test_pk_uk_mainmaster.objectID = :jdbc_tool_test_pk_uk_mainmaster.objectID\"), whereParams))";
		assertEquals(recordStr + System.lineSeparator() + "int count = " + executeApiStr + System.lineSeparator() + ".execute();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE)));

		StringBuilder recordsStr = new StringBuilder();
		recordsStr.append("GRecord whereParams = createRecord();" + System.lineSeparator() +
				"whereParams.setObject(\"objectID\",);" + System.lineSeparator());
		recordsStr.append("List<GRecord> whereParamsList = new ArrayList<>();").append(System.lineSeparator());
		recordsStr.append("whereParamsList.add(whereParams);").append(System.lineSeparator());
		String executeListApiStr = "delete(\"jdbc_tool_test_pk_uk_mainmaster\")" + System.lineSeparator() +
				".wherePK(whereParamsList)";
		assertEquals(recordsStr + System.lineSeparator() + "int count = " + executeListApiStr + System.lineSeparator() + ".executeBatch();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_PK)));
		assertEquals(recordsStr + System.lineSeparator() + "int count = " + executeListApiStr + System.lineSeparator() + ".executeList();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_PK)));
	}
	
	public String getDeleteSql_WherePartsPK_reg_delete() {
		return "DELETE FROM" + System.lineSeparator() +
				"    " + dbSchema + ".jdbc_tool_test_pk_uk_mainmaster" + System.lineSeparator() +
				"WHERE" + System.lineSeparator() +
				"    " + dbSchema + ".jdbc_tool_test_pk_uk_mainmaster.objectID = ?";
	}

	@Test
	public void testExport_wherePartsUK_reg_update() {

		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_wherePartsUK_reg_update.xml");
		String conditionFilePath = getConditionFilePath("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_wherePartsUK_reg_update.yaml");
		GExporterResult<List<GXFormCodeResult>, byte[]> result = service.export(conditionFilePath, settingsFilePath);

		assertEquals("html", result.getMediaType());
		assertEquals(1, result.getData().size());
		assertEquals(GCrudType.UPDATE, result.getData().get(0).getType());
		assertEquals("仮想表ID: 2E5140562F7C48EE966F61BECAD75343", result.getData().get(0).getDescription());
		assertEquals("UPDATE" + System.lineSeparator() +
				"    " + dbSchema + ".jdbc_tool_test_pk_uk_mainmaster jdbc_tool_test_pk_uk_mainmaster" + System.lineSeparator() +
				"SET" + System.lineSeparator() +
				"    ItemCD = ?," + System.lineSeparator() +
				"    ItemNA = ?," + System.lineSeparator() +
				"    UnitPrice = ?," + System.lineSeparator() +
				"    SellPrice = ?," + System.lineSeparator() +
				"    Flag = ?," + System.lineSeparator() +
				"    PriceFlag = ?," + System.lineSeparator() +
				"    ExclusiveFG = ?," + System.lineSeparator() +
				"    RegisteredDT = ?," + System.lineSeparator() +
				"    UpdatedDT = ?" + System.lineSeparator() +
				"WHERE" + System.lineSeparator() +
				"    (" + System.lineSeparator() +
				"        jdbc_tool_test_pk_uk_mainmaster.ItemCD = ?" + System.lineSeparator() +
				"        AND jdbc_tool_test_pk_uk_mainmaster.ItemNA = ?" + System.lineSeparator() +
				"    )", result.getData().get(0).getSqlScript());
		String whereParamsParts = "Map<String, Object> whereParams = new HashMap<>();" + System.lineSeparator() +
				"whereParams.put(\"jdbc_tool_test_pk_uk_mainmaster.ItemCD\",);" + System.lineSeparator() +
				"whereParams.put(\"jdbc_tool_test_pk_uk_mainmaster.ItemNA\",);" + System.lineSeparator();
		String recordStr = "GRecord record = createRecord();" + System.lineSeparator() +
				"record.setObject(\"ItemCD\",);" + System.lineSeparator() +
				"record.setObject(\"ItemNA\",);" + System.lineSeparator() +
				"record.setObject(\"UnitPrice\",);" + System.lineSeparator() +
				"record.setObject(\"SellPrice\",);" + System.lineSeparator() +
				"record.setObject(\"Flag\",);" + System.lineSeparator() +
				"record.setObject(\"PriceFlag\",);" + System.lineSeparator() +
				"record.setObject(\"ExclusiveFG\",);" + System.lineSeparator() +
				"record.setObject(\"RegisteredDT\",);" + System.lineSeparator() +
				"record.setObject(\"UpdatedDT\",);" + System.lineSeparator();
		String executeApiStr = "update(\"jdbc_tool_test_pk_uk_mainmaster\")" + System.lineSeparator() +
				".set(record)" + System.lineSeparator();
		String executeApiWhereStr = ".where(expMap($($(\"jdbc_tool_test_pk_uk_mainmaster.ItemCD = :jdbc_tool_test_pk_uk_mainmaster.ItemCD\").AND(\"jdbc_tool_test_pk_uk_mainmaster.ItemNA = :jdbc_tool_test_pk_uk_mainmaster.ItemNA\")), whereParams))";
		assertEquals(whereParamsParts + recordStr + System.lineSeparator() + "int count = " + executeApiStr + executeApiWhereStr + System.lineSeparator() + ".execute();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE)));
		assertEquals(recordStr + System.lineSeparator() + "int count = " + executeApiStr + ".wherePK()" + System.lineSeparator() + ".execute();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_PK)));
		assertEquals(recordStr + System.lineSeparator() + "int count = " + executeApiStr + ".whereUK(\"jdbc_tool_test_uk\")" + System.lineSeparator() + "//.whereUK(\"jdbc_tool_test_uk2\")" + System.lineSeparator() + ".execute();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_UK)));

		StringBuilder recordsStr = new StringBuilder();
		recordsStr.append("List<GRecord> recordList = new ArrayList<>();").append(System.lineSeparator());
		recordsStr.append(recordStr);
		recordsStr.append("recordList.add(record);").append(System.lineSeparator());
		String executeListApiStr = "update(\"jdbc_tool_test_pk_uk_mainmaster\")" + System.lineSeparator() +
				".set(recordList)" + System.lineSeparator();
		assertEquals(recordsStr + System.lineSeparator() + "int count = " + executeListApiStr + ".wherePK()" + System.lineSeparator() + ".executeBatch();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_PK)));
		assertEquals(recordsStr + System.lineSeparator() + "int count = " + executeListApiStr + ".whereUK(\"jdbc_tool_test_uk\")" + System.lineSeparator() + "//.whereUK(\"jdbc_tool_test_uk2\")" + System.lineSeparator() + ".executeBatch();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_UK)));
		assertEquals(recordsStr + System.lineSeparator() + "int count = " + executeListApiStr + ".wherePK()" + System.lineSeparator() + ".executeList();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_PK)));
		assertEquals(recordsStr + System.lineSeparator() + "int count = " + executeListApiStr + ".whereUK(\"jdbc_tool_test_uk\")" + System.lineSeparator() + "//.whereUK(\"jdbc_tool_test_uk2\")" + System.lineSeparator() + ".executeList();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_UK)));
	}

	@Test
	public void testExport_wherePartsUK_reg_delete() {

		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_wherePartsUK_reg_delete.xml");
		String conditionFilePath = getConditionFilePath("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_wherePartsUK_reg_delete.yaml");
		GExporterResult<List<GXFormCodeResult>, byte[]> result = service.export(conditionFilePath, settingsFilePath);

		assertEquals("html", result.getMediaType());
		assertEquals(1, result.getData().size());
		assertEquals(GCrudType.DELETE, result.getData().get(0).getType());
		assertEquals("仮想表ID: 2E5140562F7C48EE966F61BECAD75343", result.getData().get(0).getDescription());
		assertEquals(getDeleteSql_wherePartsUK_reg_delete(), result.getData().get(0).getSqlScript());
		String recordStr = String.format("GRecord whereParams = createRecord();%n" +
				"whereParams.setObject(\"ItemCD\",);%n" +
				"whereParams.setObject(\"ItemNA\",);%n" +
				"//ユーニックキー「jdbc_tool_test_uk2」用のカラム%n" +
				"/*whereParams.setObject(\"ItemCD\",);%n" +
				"whereParams.setObject(\"UnitPrice\",);%n" +
				"*/%n");
		String executeApiStr = String.format("delete(\"jdbc_tool_test_pk_uk_mainmaster\")%n" +
				".whereUK(\"jdbc_tool_test_uk\", whereParams)%n" +
				"//.whereUK(\"jdbc_tool_test_uk2\", whereParams)");
		assertEquals(recordStr + System.lineSeparator() + "int count = " + executeApiStr + System.lineSeparator() + ".execute();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_UK)));

		StringBuilder recordsStr = new StringBuilder();
		recordsStr.append(recordStr);
		recordsStr.append("List<GRecord> whereParamsList = new ArrayList<>();").append(System.lineSeparator());
		recordsStr.append("whereParamsList.add(whereParams);").append(System.lineSeparator());
		String executeListApiStr = String.format("delete(\"jdbc_tool_test_pk_uk_mainmaster\")%n" +
				".whereUK(\"jdbc_tool_test_uk\", whereParamsList)%n" +
				"//.whereUK(\"jdbc_tool_test_uk2\", whereParamsList)");
		assertEquals(recordsStr + System.lineSeparator() + "int count = " + executeListApiStr + System.lineSeparator() + ".executeBatch();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_UK)));
		assertEquals(recordsStr + System.lineSeparator() + "int count = " + executeListApiStr + System.lineSeparator() + ".executeList();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_UK)));
	}
	
	public String getDeleteSql_wherePartsUK_reg_delete() {
		return "DELETE FROM" + System.lineSeparator() +
				"    " + dbSchema + ".jdbc_tool_test_pk_uk_mainmaster" + System.lineSeparator() +
				"WHERE" + System.lineSeparator() +
				"    (" + System.lineSeparator() +
				"        " + dbSchema + ".jdbc_tool_test_pk_uk_mainmaster.ItemCD = ?" + System.lineSeparator() +
				"        AND " + dbSchema + ".jdbc_tool_test_pk_uk_mainmaster.ItemNA = ?" + System.lineSeparator() +
				"    )";
	}

	@Test
	public void testExport_wherePartsPKAndUK_reg_update() {

		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_wherePartsPKAndUK_reg_update.xml");
		String conditionFilePath = getConditionFilePath("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_wherePartsPKAndUK_reg_update.yaml");
		GExporterResult<List<GXFormCodeResult>, byte[]> result = service.export(conditionFilePath, settingsFilePath);

		assertEquals("html", result.getMediaType());
		assertEquals(1, result.getData().size());
		assertEquals(GCrudType.UPDATE, result.getData().get(0).getType());
		assertEquals("仮想表ID: 193814253CB6440E83B1088325C8EFCD", result.getData().get(0).getDescription());
		assertEquals("UPDATE" + System.lineSeparator() +
				"    " + dbSchema + ".jdbc_tool_test_pk_uk_mainmaster jdbc_tool_test_pk_uk_mainmaster" + System.lineSeparator() +
				"SET" + System.lineSeparator() +
				"    ItemCD = ?," + System.lineSeparator() +
				"    ItemNA = ?," + System.lineSeparator() +
				"    UnitPrice = ?," + System.lineSeparator() +
				"    SellPrice = ?," + System.lineSeparator() +
				"    Flag = ?," + System.lineSeparator() +
				"    PriceFlag = ?," + System.lineSeparator() +
				"    ExclusiveFG = ?," + System.lineSeparator() +
				"    RegisteredDT = ?," + System.lineSeparator() +
				"    UpdatedDT = ?" + System.lineSeparator() +
				"WHERE" + System.lineSeparator() +
				"    (" + System.lineSeparator() +
				"        jdbc_tool_test_pk_uk_mainmaster.ItemCD = ?" + System.lineSeparator() +
				"        AND jdbc_tool_test_pk_uk_mainmaster.ItemNA = ?" + System.lineSeparator() +
				"        AND jdbc_tool_test_pk_uk_mainmaster.objectID = ?" + System.lineSeparator() +
				"    )", result.getData().get(0).getSqlScript());
		String whereParamsParts = "Map<String, Object> whereParams = new HashMap<>();" + System.lineSeparator() +
				"whereParams.put(\"jdbc_tool_test_pk_uk_mainmaster.ItemCD\",);" + System.lineSeparator() +
				"whereParams.put(\"jdbc_tool_test_pk_uk_mainmaster.ItemNA\",);" + System.lineSeparator() +
				"whereParams.put(\"jdbc_tool_test_pk_uk_mainmaster.objectID\",);" + System.lineSeparator();
		String recordStr = "GRecord record = createRecord();" + System.lineSeparator() +
				"record.setObject(\"ItemCD\",);" + System.lineSeparator() +
				"record.setObject(\"ItemNA\",);" + System.lineSeparator() +
				"record.setObject(\"UnitPrice\",);" + System.lineSeparator() +
				"record.setObject(\"SellPrice\",);" + System.lineSeparator() +
				"record.setObject(\"Flag\",);" + System.lineSeparator() +
				"record.setObject(\"PriceFlag\",);" + System.lineSeparator() +
				"record.setObject(\"ExclusiveFG\",);" + System.lineSeparator() +
				"record.setObject(\"RegisteredDT\",);" + System.lineSeparator() +
				"record.setObject(\"UpdatedDT\",);" + System.lineSeparator();
		String executeApiStr = "update(\"jdbc_tool_test_pk_uk_mainmaster\")" + System.lineSeparator() +
				".set(record)" + System.lineSeparator();
		String executeApiWhereStr = ".where(expMap($($(\"jdbc_tool_test_pk_uk_mainmaster.ItemCD = :jdbc_tool_test_pk_uk_mainmaster.ItemCD\").AND(\"jdbc_tool_test_pk_uk_mainmaster.ItemNA = :jdbc_tool_test_pk_uk_mainmaster.ItemNA\").AND(\"jdbc_tool_test_pk_uk_mainmaster.objectID = :jdbc_tool_test_pk_uk_mainmaster.objectID\")), whereParams))";
		assertEquals(whereParamsParts + recordStr + System.lineSeparator() + "int count = " + executeApiStr + executeApiWhereStr + System.lineSeparator() + ".execute();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE)));
		assertEquals(recordStr + System.lineSeparator() + "int count = " + executeApiStr + ".wherePK()" + System.lineSeparator() + ".execute();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_PK)));
		assertEquals(recordStr + System.lineSeparator() + "int count = " + executeApiStr + ".whereUK(\"jdbc_tool_test_uk\")" + System.lineSeparator() + "//.whereUK(\"jdbc_tool_test_uk2\")" + System.lineSeparator() + ".execute();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_UK)));

		StringBuilder recordsStr = new StringBuilder();
		recordsStr.append("List<GRecord> recordList = new ArrayList<>();").append(System.lineSeparator());
		recordsStr.append(recordStr);
		recordsStr.append("recordList.add(record);").append(System.lineSeparator());
		String executeListApiStr = "update(\"jdbc_tool_test_pk_uk_mainmaster\")" + System.lineSeparator() +
				".set(recordList)" + System.lineSeparator();
		assertEquals(recordsStr + System.lineSeparator() + "int count = " + executeListApiStr + ".wherePK()"  + System.lineSeparator() + ".executeBatch();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_PK)));
		assertEquals(recordsStr + System.lineSeparator() + "int count = " + executeListApiStr + ".whereUK(\"jdbc_tool_test_uk\")" + System.lineSeparator() + "//.whereUK(\"jdbc_tool_test_uk2\")"  + System.lineSeparator() + ".executeBatch();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_UK)));
		assertEquals(recordsStr + System.lineSeparator() + "int count = " + executeListApiStr + ".wherePK()"  + System.lineSeparator() + ".executeList();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_PK)));
		assertEquals(recordsStr + System.lineSeparator() + "int count = " + executeListApiStr + ".whereUK(\"jdbc_tool_test_uk\")" + System.lineSeparator() + "//.whereUK(\"jdbc_tool_test_uk2\")"  + System.lineSeparator() + ".executeList();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_UK)));
	}

	@Test
	public void testExport_wherePartsPKAndUK_reg_delete() {

		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_wherePartsPKAndUK_reg_delete.xml");
		String conditionFilePath = getConditionFilePath("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_wherePartsPKAndUK_reg_delete.yaml");
		GExporterResult<List<GXFormCodeResult>, byte[]> result = service.export(conditionFilePath, settingsFilePath);

		assertEquals("html", result.getMediaType());
		assertEquals(1, result.getData().size());
		assertEquals(GCrudType.DELETE, result.getData().get(0).getType());
		assertEquals("仮想表ID: 193814253CB6440E83B1088325C8EFCD", result.getData().get(0).getDescription());
		assertEquals(getDeleteSql_wherePartsPKAndUK_reg_delete(), result.getData().get(0).getSqlScript());
		String whereParamsParts = "Map<String, Object> whereParams = new HashMap<>();" + System.lineSeparator() +
				"whereParams.put(\"jdbc_tool_test_pk_uk_mainmaster.ItemCD\",);" + System.lineSeparator() +
 				"whereParams.put(\"jdbc_tool_test_pk_uk_mainmaster.ItemNA\",);" + System.lineSeparator() +
				"whereParams.put(\"jdbc_tool_test_pk_uk_mainmaster.objectID\",);";

		String executeApiStr = "delete(\"jdbc_tool_test_pk_uk_mainmaster\")" + System.lineSeparator() +
				".where(expMap($($(\"jdbc_tool_test_pk_uk_mainmaster.ItemCD = :jdbc_tool_test_pk_uk_mainmaster.ItemCD\").AND(\"jdbc_tool_test_pk_uk_mainmaster.ItemNA = :jdbc_tool_test_pk_uk_mainmaster.ItemNA\").AND(\"jdbc_tool_test_pk_uk_mainmaster.objectID = :jdbc_tool_test_pk_uk_mainmaster.objectID\")), whereParams))";
		assertEquals(whereParamsParts + System.lineSeparator() + "int count = " + executeApiStr + System.lineSeparator() + ".execute();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE)));
	}
	
	public String getDeleteSql_wherePartsPKAndUK_reg_delete() {
		return "DELETE FROM" + System.lineSeparator() +
				"    " + dbSchema + ".jdbc_tool_test_pk_uk_mainmaster" + System.lineSeparator() +
				"WHERE" + System.lineSeparator() +
				"    (" + System.lineSeparator() +
				"        " + dbSchema + ".jdbc_tool_test_pk_uk_mainmaster.ItemCD = ?" + System.lineSeparator() +
				"        AND " + dbSchema + ".jdbc_tool_test_pk_uk_mainmaster.ItemNA = ?" + System.lineSeparator() +
				"        AND " + dbSchema + ".jdbc_tool_test_pk_uk_mainmaster.objectID = ?" + System.lineSeparator() +
				"    )";
	}

	@Test
	public void testExport_wherePartsNotPKOrUK_reg_update() {

		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_wherePartsNotPKOrUK_reg_update.xml");
		String conditionFilePath = getConditionFilePath("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_wherePartsNotPKOrUK_reg_update.yaml");
		GExporterResult<List<GXFormCodeResult>, byte[]> result = service.export(conditionFilePath, settingsFilePath);

		assertEquals("html", result.getMediaType());
		assertEquals(1, result.getData().size());
		assertEquals(GCrudType.UPDATE, result.getData().get(0).getType());
		assertEquals("仮想表ID: 4E800A8CAD8A4071A2A60C0D7E555ECB", result.getData().get(0).getDescription());
		assertEquals("UPDATE" + System.lineSeparator() +
				"    " + dbSchema + ".jdbc_tool_test_pk_uk_mainmaster jdbc_tool_test_pk_uk_mainmaster" + System.lineSeparator() +
				"SET" + System.lineSeparator() +
				"    ItemCD = ?," + System.lineSeparator() +
				"    ItemNA = ?," + System.lineSeparator() +
				"    UnitPrice = ?," + System.lineSeparator() +
				"    SellPrice = ?," + System.lineSeparator() +
				"    Flag = ?," + System.lineSeparator() +
				"    PriceFlag = ?," + System.lineSeparator() +
				"    ExclusiveFG = ?," + System.lineSeparator() +
				"    RegisteredDT = ?," + System.lineSeparator() +
				"    UpdatedDT = ?" + System.lineSeparator() +
				"WHERE" + System.lineSeparator() +
				"    jdbc_tool_test_pk_uk_mainmaster.UnitPrice = ?", result.getData().get(0).getSqlScript());
		String whereParamsParts = "Map<String, Object> whereParams = new HashMap<>();" + System.lineSeparator() +
				"whereParams.put(\"jdbc_tool_test_pk_uk_mainmaster.UnitPrice\",);" + System.lineSeparator();
		String recordStr = "GRecord record = createRecord();" + System.lineSeparator() +
				"record.setObject(\"ItemCD\",);" + System.lineSeparator() +
				"record.setObject(\"ItemNA\",);" + System.lineSeparator() +
				"record.setObject(\"UnitPrice\",);" + System.lineSeparator() +
				"record.setObject(\"SellPrice\",);" + System.lineSeparator() +
				"record.setObject(\"Flag\",);" + System.lineSeparator() +
				"record.setObject(\"PriceFlag\",);" + System.lineSeparator() +
				"record.setObject(\"ExclusiveFG\",);" + System.lineSeparator() +
				"record.setObject(\"RegisteredDT\",);" + System.lineSeparator() +
				"record.setObject(\"UpdatedDT\",);" + System.lineSeparator();
		String executeApiStr = "update(\"jdbc_tool_test_pk_uk_mainmaster\")" + System.lineSeparator() +
				".set(record)" + System.lineSeparator();
		String executeApiWhereStr = ".where(expMap($(\"jdbc_tool_test_pk_uk_mainmaster.UnitPrice = :jdbc_tool_test_pk_uk_mainmaster.UnitPrice\"), whereParams))";
		assertEquals(whereParamsParts + recordStr + System.lineSeparator() + "int count = " + executeApiStr + executeApiWhereStr + System.lineSeparator() + ".execute();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE)));
		assertEquals(recordStr + System.lineSeparator() + "int count = " + executeApiStr + ".wherePK()" + System.lineSeparator() + ".execute();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_PK)));
		assertEquals(recordStr + System.lineSeparator() + "int count = " + executeApiStr + ".whereUK(\"jdbc_tool_test_uk\")" + System.lineSeparator() + "//.whereUK(\"jdbc_tool_test_uk2\")" + System.lineSeparator() + ".execute();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_UK)));

		StringBuilder recordsStr = new StringBuilder();
		recordsStr.append("List<GRecord> recordList = new ArrayList<>();").append(System.lineSeparator());
		recordsStr.append(recordStr);
		recordsStr.append("recordList.add(record);").append(System.lineSeparator());
		String executeListApiStr = "update(\"jdbc_tool_test_pk_uk_mainmaster\")" + System.lineSeparator() +
				".set(recordList)" + System.lineSeparator();
		assertEquals(recordsStr + System.lineSeparator() + "int count = " + executeListApiStr + ".wherePK()" + System.lineSeparator() + ".executeBatch();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_PK)));
		assertEquals(recordsStr + System.lineSeparator() + "int count = " + executeListApiStr + ".whereUK(\"jdbc_tool_test_uk\")" + System.lineSeparator() + "//.whereUK(\"jdbc_tool_test_uk2\")" + System.lineSeparator() + ".executeBatch();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_UK)));
		assertEquals(recordsStr + System.lineSeparator() + "int count = " + executeListApiStr + ".wherePK()" + System.lineSeparator() + ".executeList();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_PK)));
		assertEquals(recordsStr + System.lineSeparator() + "int count = " + executeListApiStr + ".whereUK(\"jdbc_tool_test_uk\")" + System.lineSeparator() + "//.whereUK(\"jdbc_tool_test_uk2\")" + System.lineSeparator() + ".executeList();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_UK)));
	}

	@Test
	public void testExport_wherePartsNotPKOrUK_reg_delete() {

		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_wherePartsNotPKOrUK_reg_delete.xml");
		String conditionFilePath = getConditionFilePath("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_wherePartsNotPKOrUK_reg_delete.yaml");
		GExporterResult<List<GXFormCodeResult>, byte[]> result = service.export(conditionFilePath, settingsFilePath);

		assertEquals("html", result.getMediaType());
		assertEquals(1, result.getData().size());
		assertEquals(GCrudType.DELETE, result.getData().get(0).getType());
		assertEquals("仮想表ID: 4E800A8CAD8A4071A2A60C0D7E555ECB", result.getData().get(0).getDescription());
		assertEquals(getDeleteSql_wherePartsNotPKOrUK_reg_delete(), result.getData().get(0).getSqlScript());
		String whereParamsParts = "Map<String, Object> whereParams = new HashMap<>();" + System.lineSeparator() +
				"whereParams.put(\"jdbc_tool_test_pk_uk_mainmaster.UnitPrice\",);";
		String executeApiStr = "delete(\"jdbc_tool_test_pk_uk_mainmaster\")" + System.lineSeparator() +
				".where(expMap($(\"jdbc_tool_test_pk_uk_mainmaster.UnitPrice = :jdbc_tool_test_pk_uk_mainmaster.UnitPrice\"), whereParams))";
		assertEquals(whereParamsParts + System.lineSeparator() + "int count = " + executeApiStr + System.lineSeparator() + ".execute();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE)));
	}
	
	public String getDeleteSql_wherePartsNotPKOrUK_reg_delete() {
		return "DELETE FROM" + System.lineSeparator() +
				"    " + dbSchema + ".jdbc_tool_test_pk_uk_mainmaster" + System.lineSeparator() +
				"WHERE" + System.lineSeparator() +
				"    " + dbSchema + ".jdbc_tool_test_pk_uk_mainmaster.UnitPrice = ?";
	}

	@Test
	public void testExport_wherePartsNotCondition_reg_update() {

		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_wherePartsNotCondition_reg_update.xml");
		String conditionFilePath = getConditionFilePath("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_wherePartsNotCondition_reg_update.yaml");
		GExporterResult<List<GXFormCodeResult>, byte[]> result = service.export(conditionFilePath, settingsFilePath);

		assertEquals("html", result.getMediaType());
		assertEquals(1, result.getData().size());
		assertEquals(GCrudType.UPDATE, result.getData().get(0).getType());
		assertEquals("仮想表ID: 8C75277254C14BC0B310CF2182BFF928", result.getData().get(0).getDescription());
		assertEquals("UPDATE" + System.lineSeparator() +
				"    " + dbSchema + ".jdbc_tool_test_pk_uk_mainmaster jdbc_tool_test_pk_uk_mainmaster" + System.lineSeparator() +
				"SET" + System.lineSeparator() +
				"    ItemCD = ?," + System.lineSeparator() +
				"    ItemNA = ?," + System.lineSeparator() +
				"    UnitPrice = ?," + System.lineSeparator() +
				"    SellPrice = ?," + System.lineSeparator() +
				"    Flag = ?," + System.lineSeparator() +
				"    PriceFlag = ?," + System.lineSeparator() +
				"    ExclusiveFG = ?," + System.lineSeparator() +
				"    RegisteredDT = ?," + System.lineSeparator() +
				"    UpdatedDT = ?", result.getData().get(0).getSqlScript());
		String recordStr = "GRecord record = createRecord();" + System.lineSeparator() +
				"record.setObject(\"ItemCD\",);" + System.lineSeparator() +
				"record.setObject(\"ItemNA\",);" + System.lineSeparator() +
				"record.setObject(\"UnitPrice\",);" + System.lineSeparator() +
				"record.setObject(\"SellPrice\",);" + System.lineSeparator() +
				"record.setObject(\"Flag\",);" + System.lineSeparator() +
				"record.setObject(\"PriceFlag\",);" + System.lineSeparator() +
				"record.setObject(\"ExclusiveFG\",);" + System.lineSeparator() +
				"record.setObject(\"RegisteredDT\",);" + System.lineSeparator() +
				"record.setObject(\"UpdatedDT\",);" + System.lineSeparator();
		String executeApiStr = "update(\"jdbc_tool_test_pk_uk_mainmaster\")" + System.lineSeparator() +
				".set(record)";
		assertEquals(recordStr + System.lineSeparator() + "int count = " + executeApiStr + System.lineSeparator() + ".execute();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, null)));
		assertNull(result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE)));
		assertNull(result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_PK)));
		assertNull(result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_UK)));
		assertNull(result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, null)));
		assertNull(result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE)));
		assertNull(result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_PK)));
		assertNull(result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_UK)));
		assertNull(result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, null)));
		assertNull(result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE)));
		assertNull(result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_PK)));
		assertNull(result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_UK)));
	}

	@Test
	public void testExport_wherePartsNotCondition_reg_delete() {

		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_wherePartsNotCondition_reg_delete.xml");
		String conditionFilePath = getConditionFilePath("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_wherePartsNotCondition_reg_delete.yaml");
		GExporterResult<List<GXFormCodeResult>, byte[]> result = service.export(conditionFilePath, settingsFilePath);

		assertEquals("html", result.getMediaType());
		assertEquals(1, result.getData().size());
		assertEquals(GCrudType.DELETE, result.getData().get(0).getType());
		assertEquals("仮想表ID: 8C75277254C14BC0B310CF2182BFF928", result.getData().get(0).getDescription());
		assertEquals(getDeleteSql_wherePartsNotCondition_reg_delete(), result.getData().get(0).getSqlScript());
		String executeApiStr = "delete(\"jdbc_tool_test_pk_uk_mainmaster\")";
		assertEquals("int count = " + executeApiStr + System.lineSeparator() + ".execute();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, null)));
	}
	
	public String getDeleteSql_wherePartsNotCondition_reg_delete() {
		return "DELETE FROM" + System.lineSeparator() +
				"    " + dbSchema + ".jdbc_tool_test_pk_uk_mainmaster";
	}

	@Test
	public void testExport_searchExpWherePartsLikeF_reg_update() {

		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_searchExpWherePartsLikeF_reg_update.xml");
		String conditionFilePath = getConditionFilePath("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_searchExpWherePartsLikeF_reg_update.yaml");
		GExporterResult<List<GXFormCodeResult>, byte[]> result = service.export(conditionFilePath, settingsFilePath);

		assertEquals("html", result.getMediaType());
		assertEquals(1, result.getData().size());
		assertEquals(GCrudType.UPDATE, result.getData().get(0).getType());
		assertEquals("仮想表ID: FBD28623C6794B44ACED6A253DA526E5", result.getData().get(0).getDescription());
		assertEquals("UPDATE" + System.lineSeparator() +
				"    " + dbSchema + ".jdbc_tool_test_mainmaster jdbc_tool_test_mainmaster" + System.lineSeparator() +
				"SET" + System.lineSeparator() +
				"    ItemCD = ?," + System.lineSeparator() +
				"    UnitPrice = ?" + System.lineSeparator() +
				"WHERE" + System.lineSeparator() +
				"    jdbc_tool_test_mainmaster.ItemCD LIKE ? ESCAPE '/'", result.getData().get(0).getSqlScript());
		String whereParamsParts = "Map<String, Object> whereParams = new HashMap<>();" + System.lineSeparator() +
				"whereParams.put(\"jdbc_tool_test_mainmaster.ItemCD\",);" + System.lineSeparator();
		String recordStr = "GRecord record = createRecord();" + System.lineSeparator() +
				"record.setObject(\"ItemCD\",);" + System.lineSeparator() +
				"record.setObject(\"UnitPrice\",);" + System.lineSeparator();
		String executeApiStr = "update(\"jdbc_tool_test_mainmaster\")" + System.lineSeparator() +
				".set(record)" + System.lineSeparator();
		String executeApiWhereStr = ".where(expMap($(\"jdbc_tool_test_mainmaster.ItemCD LIKE $CONCAT(:jdbc_tool_test_mainmaster.ItemCD, '%') ESCAPE '/'\"), whereParams))";
		assertEquals(whereParamsParts + recordStr + System.lineSeparator() + "int count = " + executeApiStr + executeApiWhereStr + System.lineSeparator() + ".execute();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE)));
		assertEquals(recordStr + System.lineSeparator() + "int count = " + executeApiStr + ".wherePK()" + System.lineSeparator() + ".execute();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_PK)));
		assertEquals(recordStr + System.lineSeparator() + "int count = " + executeApiStr + ".whereUK(\"MainMasterUniqueKey\")" + System.lineSeparator() + ".execute();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_UK)));

		StringBuilder recordsStr = new StringBuilder();
		recordsStr.append("List<GRecord> recordList = new ArrayList<>();").append(System.lineSeparator());
		recordsStr.append(recordStr);
		recordsStr.append("recordList.add(record);").append(System.lineSeparator());
		String executeListApiStr = "update(\"jdbc_tool_test_mainmaster\")" + System.lineSeparator() +
				".set(recordList)" + System.lineSeparator();
		assertEquals(recordsStr + System.lineSeparator() + "int count = " + executeListApiStr + ".wherePK()" + System.lineSeparator() + ".executeBatch();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_PK)));
		assertEquals(recordsStr + System.lineSeparator() + "int count = " + executeListApiStr + ".whereUK(\"MainMasterUniqueKey\")" + System.lineSeparator() + ".executeBatch();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_UK)));
		assertEquals(recordsStr + System.lineSeparator() + "int count = " + executeListApiStr + ".wherePK()" + System.lineSeparator() + ".executeList();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_PK)));
		assertEquals(recordsStr + System.lineSeparator() + "int count = " + executeListApiStr + ".whereUK(\"MainMasterUniqueKey\")" + System.lineSeparator() + ".executeList();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_UK)));
	}

	@Test
	public void testExport_searchExpWherePartsLikeF_reg_delete() {

		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_searchExpWherePartsLikeF_reg_delete.xml");
		String conditionFilePath = getConditionFilePath("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_searchExpWherePartsLikeF_reg_delete.yaml");
		GExporterResult<List<GXFormCodeResult>, byte[]> result = service.export(conditionFilePath, settingsFilePath);

		assertEquals("html", result.getMediaType());
		assertEquals(1, result.getData().size());
		assertEquals(GCrudType.DELETE, result.getData().get(0).getType());
		assertEquals("仮想表ID: FBD28623C6794B44ACED6A253DA526E5", result.getData().get(0).getDescription());
		assertEquals(getDeleteSql_searchExpWherePartsLikeF_reg_delete(), result.getData().get(0).getSqlScript());
		String whereParamsParts = "Map<String, Object> whereParams = new HashMap<>();" + System.lineSeparator() +
				"whereParams.put(\"jdbc_tool_test_mainmaster.ItemCD\",);";
		String executeApiStr = "delete(\"jdbc_tool_test_mainmaster\")" + System.lineSeparator() +
				".where(expMap($(\"jdbc_tool_test_mainmaster.ItemCD LIKE $CONCAT(:jdbc_tool_test_mainmaster.ItemCD, '%') ESCAPE '/'\"), whereParams))";
		assertEquals(whereParamsParts + System.lineSeparator() + "int count = " + executeApiStr + System.lineSeparator() + ".execute();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE)));
	}
	
	public String getDeleteSql_searchExpWherePartsLikeF_reg_delete() {
		return "DELETE FROM" + System.lineSeparator() +
				"    " + dbSchema + ".jdbc_tool_test_mainmaster" + System.lineSeparator() +
				"WHERE" + System.lineSeparator() +
				"    " + dbSchema + ".jdbc_tool_test_mainmaster.ItemCD LIKE ? ESCAPE '/'";
	}

	@Test
	public void testExport_searchExpWherePartsLikeFB_reg_update() {

		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_searchExpWherePartsLikeFB_reg_update.xml");
		String conditionFilePath = getConditionFilePath("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_searchExpWherePartsLikeFB_reg_update.yaml");
		GExporterResult<List<GXFormCodeResult>, byte[]> result = service.export(conditionFilePath, settingsFilePath);

		assertEquals("html", result.getMediaType());
		assertEquals(1, result.getData().size());
		assertEquals(GCrudType.UPDATE, result.getData().get(0).getType());
		assertEquals("仮想表ID: D65A07E6C02546B58A1BD511ED22B508", result.getData().get(0).getDescription());
		assertEquals("UPDATE" + System.lineSeparator() +
				"    " + dbSchema + ".jdbc_tool_test_unitmaster jdbc_tool_test_unitmaster" + System.lineSeparator() +
				"SET" + System.lineSeparator() +
				"    Code = ?," + System.lineSeparator() +
				"    Name = ?" + System.lineSeparator() +
				"WHERE" + System.lineSeparator() +
				"    jdbc_tool_test_unitmaster.Name LIKE ? ESCAPE '/'", result.getData().get(0).getSqlScript());
		String whereParamsParts = "Map<String, Object> whereParams = new HashMap<>();" + System.lineSeparator() +
				"whereParams.put(\"jdbc_tool_test_unitmaster.Name\",);" + System.lineSeparator();
		String recordStr = "GRecord record = createRecord();" + System.lineSeparator() +
				"record.setObject(\"Code\",);" + System.lineSeparator() +
				"record.setObject(\"Name\",);" + System.lineSeparator();
		String executeApiStr = "update(\"jdbc_tool_test_unitmaster\")" + System.lineSeparator() +
				".set(record)" + System.lineSeparator();
		String executeApiWhereStr = ".where(expMap($(\"jdbc_tool_test_unitmaster.Name LIKE $CONCAT('%', $CONCAT(:jdbc_tool_test_unitmaster.Name, '%')) ESCAPE '/'\"), whereParams))";
		assertEquals(whereParamsParts + recordStr + System.lineSeparator() + "int count = " + executeApiStr + executeApiWhereStr + System.lineSeparator() + ".execute();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE)));
		assertEquals(recordStr + System.lineSeparator() + "int count = " + executeApiStr + ".wherePK()" + System.lineSeparator() + ".execute();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_PK)));
		assertEquals(recordStr + System.lineSeparator() + "int count = " + executeApiStr + ".whereUK(\"UnitMasterUK\")" + System.lineSeparator() + ".execute();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_UK)));

		StringBuilder recordsStr = new StringBuilder();
		recordsStr.append("List<GRecord> recordList = new ArrayList<>();").append(System.lineSeparator());
		recordsStr.append(recordStr);
		recordsStr.append("recordList.add(record);").append(System.lineSeparator());
		String executeListApiStr = "update(\"jdbc_tool_test_unitmaster\")" + System.lineSeparator() +
				".set(recordList)" + System.lineSeparator();
		assertEquals(recordsStr + System.lineSeparator() + "int count = " + executeListApiStr + ".wherePK()" + System.lineSeparator() + ".executeBatch();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_PK)));
		assertEquals(recordsStr + System.lineSeparator() + "int count = " + executeListApiStr + ".whereUK(\"UnitMasterUK\")" + System.lineSeparator() + ".executeBatch();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_UK)));
		assertEquals(recordsStr + System.lineSeparator() + "int count = " + executeListApiStr + ".wherePK()" + System.lineSeparator() + ".executeList();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_PK)));
		assertEquals(recordsStr + System.lineSeparator() + "int count = " + executeListApiStr + ".whereUK(\"UnitMasterUK\")" + System.lineSeparator() + ".executeList();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_UK)));
	}

	@Test
	public void testExport_searchExpWherePartsLikeFB_reg_delete() {

		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_searchExpWherePartsLikeFB_reg_delete.xml");
		String conditionFilePath = getConditionFilePath("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_searchExpWherePartsLikeFB_reg_delete.yaml");
		GExporterResult<List<GXFormCodeResult>, byte[]> result = service.export(conditionFilePath, settingsFilePath);

		assertEquals("html", result.getMediaType());
		assertEquals(1, result.getData().size());
		assertEquals(GCrudType.DELETE, result.getData().get(0).getType());
		assertEquals("仮想表ID: D65A07E6C02546B58A1BD511ED22B508", result.getData().get(0).getDescription());
		assertEquals(getDeleteSql_searchExpWherePartsLikeFB_reg_delete(), result.getData().get(0).getSqlScript());
		String whereParamsParts = "Map<String, Object> whereParams = new HashMap<>();" + System.lineSeparator() +
				"whereParams.put(\"jdbc_tool_test_unitmaster.Name\",);";
		String executeApiStr = "delete(\"jdbc_tool_test_unitmaster\")" + System.lineSeparator() +
				".where(expMap($(\"jdbc_tool_test_unitmaster.Name LIKE $CONCAT('%', $CONCAT(:jdbc_tool_test_unitmaster.Name, '%')) ESCAPE '/'\"), whereParams))";
		assertEquals(whereParamsParts + System.lineSeparator() + "int count = " + executeApiStr + System.lineSeparator() + ".execute();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE)));
	}
	
	public String getDeleteSql_searchExpWherePartsLikeFB_reg_delete() {
		return "DELETE FROM" + System.lineSeparator() +
				"    " + dbSchema + ".jdbc_tool_test_unitmaster" + System.lineSeparator() +
				"WHERE" + System.lineSeparator() +
				"    " + dbSchema + ".jdbc_tool_test_unitmaster.Name LIKE ? ESCAPE '/'";
	}

	@Test
	public void testExport_searchExpWherePartsLikeB_reg_update() {

		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_searchExpWherePartsLikeB_reg_update.xml");
		String conditionFilePath = getConditionFilePath("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_searchExpWherePartsLikeB_reg_update.yaml");
		GExporterResult<List<GXFormCodeResult>, byte[]> result = service.export(conditionFilePath, settingsFilePath);

		assertEquals("html", result.getMediaType());
		assertEquals(1, result.getData().size());
		assertEquals(GCrudType.UPDATE, result.getData().get(0).getType());
		assertEquals("仮想表ID: 730835BE19ED400F9FAFEF9DCF864057", result.getData().get(0).getDescription());
		assertEquals("UPDATE" + System.lineSeparator() +
				"    " + dbSchema + ".jdbc_tool_test_ordermaster jdbc_tool_test_ordermaster" + System.lineSeparator() +
				"SET" + System.lineSeparator() +
				"    SlipNO = ?," + System.lineSeparator() +
				"    CorporateNA = ?" + System.lineSeparator() +
				"WHERE" + System.lineSeparator() +
				"    jdbc_tool_test_ordermaster.CorporateNA LIKE ? ESCAPE '/'", result.getData().get(0).getSqlScript());
		String whereParamsParts = "Map<String, Object> whereParams = new HashMap<>();" + System.lineSeparator() +
				"whereParams.put(\"jdbc_tool_test_ordermaster.CorporateNA\",);" + System.lineSeparator();
		String recordStr = "GRecord record = createRecord();" + System.lineSeparator() +
				"record.setObject(\"SlipNO\",);" + System.lineSeparator() +
				"record.setObject(\"CorporateNA\",);" + System.lineSeparator();
		String executeApiStr = "update(\"jdbc_tool_test_ordermaster\")" + System.lineSeparator() +
				".set(record)" + System.lineSeparator();
		String executeApiWhereStr = ".where(expMap($(\"jdbc_tool_test_ordermaster.CorporateNA LIKE $CONCAT('%', :jdbc_tool_test_ordermaster.CorporateNA) ESCAPE '/'\"), whereParams))";
		assertEquals(whereParamsParts + recordStr + System.lineSeparator() + "int count = " + executeApiStr + executeApiWhereStr + System.lineSeparator() + ".execute();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE)));
		assertEquals(recordStr + System.lineSeparator() + "int count = " + executeApiStr + ".wherePK()" + System.lineSeparator() + ".execute();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_PK)));
		assertEquals(recordStr + System.lineSeparator() + "int count = " + executeApiStr + ".whereUK(\"OrderMasterUK\")" + System.lineSeparator() + ".execute();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_UK)));

		StringBuilder recordsStr = new StringBuilder();
		recordsStr.append("List<GRecord> recordList = new ArrayList<>();").append(System.lineSeparator());
		recordsStr.append(recordStr);
		recordsStr.append("recordList.add(record);").append(System.lineSeparator());
		String executeListApiStr = "update(\"jdbc_tool_test_ordermaster\")" + System.lineSeparator() +
				".set(recordList)" + System.lineSeparator();
		assertEquals(recordsStr + System.lineSeparator() + "int count = " + executeListApiStr + ".wherePK()" + System.lineSeparator() + ".executeBatch();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_PK)));
		assertEquals(recordsStr + System.lineSeparator() + "int count = " + executeListApiStr + ".whereUK(\"OrderMasterUK\")" + System.lineSeparator() + ".executeBatch();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_UK)));
		assertEquals(recordsStr + System.lineSeparator() + "int count = " + executeListApiStr + ".wherePK()" + System.lineSeparator() + ".executeList();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_PK)));
		assertEquals(recordsStr + System.lineSeparator() + "int count = " + executeListApiStr + ".whereUK(\"OrderMasterUK\")" + System.lineSeparator() + ".executeList();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_UK)));
	}

	@Test
	public void testExport_searchExpWherePartsLikeB_reg_delete() {

		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_searchExpWherePartsLikeB_reg_delete.xml");
		String conditionFilePath = getConditionFilePath("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_searchExpWherePartsLikeB_reg_delete.yaml");
		GExporterResult<List<GXFormCodeResult>, byte[]> result = service.export(conditionFilePath, settingsFilePath);

		assertEquals("html", result.getMediaType());
		assertEquals(1, result.getData().size());
		assertEquals(GCrudType.DELETE, result.getData().get(0).getType());
		assertEquals("仮想表ID: 730835BE19ED400F9FAFEF9DCF864057", result.getData().get(0).getDescription());
		assertEquals(getDeleteSql_searchExpWherePartsLikeB_reg_delete(), result.getData().get(0).getSqlScript());
		String whereParamsParts = "Map<String, Object> whereParams = new HashMap<>();" + System.lineSeparator() +
				"whereParams.put(\"jdbc_tool_test_ordermaster.CorporateNA\",);";
		String executeApiStr = "delete(\"jdbc_tool_test_ordermaster\")" + System.lineSeparator() +
				".where(expMap($(\"jdbc_tool_test_ordermaster.CorporateNA LIKE $CONCAT('%', :jdbc_tool_test_ordermaster.CorporateNA) ESCAPE '/'\"), whereParams))";
		assertEquals(whereParamsParts + System.lineSeparator() + "int count = " + executeApiStr + System.lineSeparator() + ".execute();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE)));
	}
	
	public String getDeleteSql_searchExpWherePartsLikeB_reg_delete() {
		return "DELETE FROM" + System.lineSeparator() +
				"    " + dbSchema + ".jdbc_tool_test_ordermaster" + System.lineSeparator() +
				"WHERE" + System.lineSeparator() +
				"    " + dbSchema + ".jdbc_tool_test_ordermaster.CorporateNA LIKE ? ESCAPE '/'";
	}

	@Test
	public void testExport_searchExpWherePartsEquals_reg_update() {

		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_searchExpWherePartsEquals_reg_update.xml");
		String conditionFilePath = getConditionFilePath("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_searchExpWherePartsEquals_reg_update.yaml");
		GExporterResult<List<GXFormCodeResult>, byte[]> result = service.export(conditionFilePath, settingsFilePath);

		assertEquals("html", result.getMediaType());
		assertEquals(1, result.getData().size());
		assertEquals(GCrudType.UPDATE, result.getData().get(0).getType());
		assertEquals("仮想表ID: CC71DD95CF7244DFB80A009930DB3F95", result.getData().get(0).getDescription());
		assertEquals("UPDATE" + System.lineSeparator() +
				"    " + dbSchema + ".jdbc_tool_test_mainmaster jdbc_tool_test_mainmaster" + System.lineSeparator() +
				"SET" + System.lineSeparator() +
				"    ItemCD = ?," + System.lineSeparator() +
				"    UnitPrice = ?" + System.lineSeparator() +
				"WHERE" + System.lineSeparator() +
				"    (" + System.lineSeparator() +
				"        jdbc_tool_test_mainmaster.ItemCD = ?" + System.lineSeparator() +
				"        AND jdbc_tool_test_mainmaster.UnitPrice <> ?" + System.lineSeparator() +
				"    )", result.getData().get(0).getSqlScript());
		String whereParamsParts = "Map<String, Object> whereParams = new HashMap<>();" + System.lineSeparator() +
				"whereParams.put(\"jdbc_tool_test_mainmaster.UnitPrice\",);" + System.lineSeparator() +
				"whereParams.put(\"jdbc_tool_test_mainmaster.ItemCD\",);" + System.lineSeparator();
		String recordStr = "GRecord record = createRecord();" + System.lineSeparator() +
				"record.setObject(\"ItemCD\",);" + System.lineSeparator() +
				"record.setObject(\"UnitPrice\",);" + System.lineSeparator();
		String executeApiStr = "update(\"jdbc_tool_test_mainmaster\")" + System.lineSeparator() +
				".set(record)" + System.lineSeparator();
		String executeApiWhereStr = ".where(expMap($($(\"jdbc_tool_test_mainmaster.ItemCD = $TRIM([:jdbc_tool_test_mainmaster.ItemCD])\").AND(\"jdbc_tool_test_mainmaster.UnitPrice <> $TRIM([:jdbc_tool_test_mainmaster.UnitPrice])\")), whereParams))";
		assertEquals(whereParamsParts + recordStr + System.lineSeparator() + "int count = " + executeApiStr + executeApiWhereStr + System.lineSeparator() + ".execute();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE)));
		assertEquals(recordStr + System.lineSeparator() + "int count = " + executeApiStr + ".wherePK()" + System.lineSeparator() + ".execute();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_PK)));
		assertEquals(recordStr + System.lineSeparator() + "int count = " + executeApiStr + ".whereUK(\"MainMasterUniqueKey\")" + System.lineSeparator() + ".execute();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_UK)));

		StringBuilder recordsStr = new StringBuilder();
		recordsStr.append("List<GRecord> recordList = new ArrayList<>();").append(System.lineSeparator());
		recordsStr.append(recordStr);
		recordsStr.append("recordList.add(record);").append(System.lineSeparator());
		String executeListApiStr = "update(\"jdbc_tool_test_mainmaster\")" + System.lineSeparator() +
				".set(recordList)" + System.lineSeparator();
		assertEquals(recordsStr + System.lineSeparator() + "int count = " + executeListApiStr + ".wherePK()" + System.lineSeparator() + ".executeBatch();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_PK)));
		assertEquals(recordsStr + System.lineSeparator() + "int count = " + executeListApiStr + ".whereUK(\"MainMasterUniqueKey\")" + System.lineSeparator() + ".executeBatch();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_UK)));
		assertEquals(recordsStr + System.lineSeparator() + "int count = " + executeListApiStr + ".wherePK()" + System.lineSeparator() + ".executeList();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_PK)));
		assertEquals(recordsStr + System.lineSeparator() + "int count = " + executeListApiStr + ".whereUK(\"MainMasterUniqueKey\")" + System.lineSeparator() + ".executeList();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_UK)));
	}

	@Test
	public void testExport_searchExpWherePartsEquals_reg_delete() {

		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_searchExpWherePartsEquals_reg_delete.xml");
		String conditionFilePath = getConditionFilePath("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_searchExpWherePartsEquals_reg_delete.yaml");
		GExporterResult<List<GXFormCodeResult>, byte[]> result = service.export(conditionFilePath, settingsFilePath);

		assertEquals("html", result.getMediaType());
		assertEquals(1, result.getData().size());
		assertEquals(GCrudType.DELETE, result.getData().get(0).getType());
		assertEquals("仮想表ID: CC71DD95CF7244DFB80A009930DB3F95", result.getData().get(0).getDescription());
		assertEquals(getDeleteSql_searchExpWherePartsEquals_reg_delete(), result.getData().get(0).getSqlScript());
		String whereParamsParts = "Map<String, Object> whereParams = new HashMap<>();" + System.lineSeparator() +
				"whereParams.put(\"jdbc_tool_test_mainmaster.UnitPrice\",);" + System.lineSeparator() +
				"whereParams.put(\"jdbc_tool_test_mainmaster.ItemCD\",);";
		String executeApiStr = "delete(\"jdbc_tool_test_mainmaster\")" + System.lineSeparator() +
				".where(expMap($($(\"jdbc_tool_test_mainmaster.ItemCD = $TRIM([:jdbc_tool_test_mainmaster.ItemCD])\").AND(\"jdbc_tool_test_mainmaster.UnitPrice <> $TRIM([:jdbc_tool_test_mainmaster.UnitPrice])\")), whereParams))";
		assertEquals(whereParamsParts + System.lineSeparator() + "int count = " + executeApiStr + System.lineSeparator() + ".execute();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE)));
	}
	
	public String getDeleteSql_searchExpWherePartsEquals_reg_delete() {
		return "DELETE FROM" + System.lineSeparator() +
				"    " + dbSchema + ".jdbc_tool_test_mainmaster" + System.lineSeparator() +
				"WHERE" + System.lineSeparator() +
				"    (" + System.lineSeparator() +
				"        " + dbSchema + ".jdbc_tool_test_mainmaster.ItemCD = ?" + System.lineSeparator() +
				"        AND " + dbSchema + ".jdbc_tool_test_mainmaster.UnitPrice <> ?" + System.lineSeparator() +
				"    )";
	}

	@Test
	public void testExport_searchExpWherePartsCompare_reg_update() {

		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_searchExpWherePartsCompare_reg_update.xml");
		String conditionFilePath = getConditionFilePath("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_searchExpWherePartsCompare_reg_update.yaml");
		GExporterResult<List<GXFormCodeResult>, byte[]> result = service.export(conditionFilePath, settingsFilePath);

		assertEquals("html", result.getMediaType());
		assertEquals(1, result.getData().size());
		assertEquals(GCrudType.UPDATE, result.getData().get(0).getType());
		assertEquals("仮想表ID: 59EF9C5DD58045A9A952B43DDC13F762", result.getData().get(0).getDescription());
		assertEquals("UPDATE" + System.lineSeparator() +
				"    " + dbSchema + ".jdbc_tool_test_mainmaster jdbc_tool_test_mainmaster" + System.lineSeparator() +
				"SET" + System.lineSeparator() +
				"    ItemCD = ?," + System.lineSeparator() +
				"    UnitPrice = ?," + System.lineSeparator() +
				"    SellPrice = ?," + System.lineSeparator() +
				"    Flag = ?," + System.lineSeparator() +
				"    PriceFlag = ?" + System.lineSeparator() +
				"WHERE" + System.lineSeparator() +
				"    (" + System.lineSeparator() +
				"        jdbc_tool_test_mainmaster.UnitPrice > ?" + System.lineSeparator() +
				"        AND jdbc_tool_test_mainmaster.SellPrice < ?" + System.lineSeparator() +
				"        AND jdbc_tool_test_mainmaster.Flag <= ?" + System.lineSeparator() +
				"        AND jdbc_tool_test_mainmaster.PriceFlag >= ?" + System.lineSeparator() +
				"    )", result.getData().get(0).getSqlScript());
		String whereParamsParts = "Map<String, Object> whereParams = new HashMap<>();" + System.lineSeparator() +
				"whereParams.put(\"jdbc_tool_test_mainmaster.Flag\",);" + System.lineSeparator() +
				"whereParams.put(\"jdbc_tool_test_mainmaster.UnitPrice\",);" + System.lineSeparator() +
				"whereParams.put(\"jdbc_tool_test_mainmaster.PriceFlag\",);" + System.lineSeparator() +
				"whereParams.put(\"jdbc_tool_test_mainmaster.SellPrice\",);" + System.lineSeparator();
		String recordStr = "GRecord record = createRecord();" + System.lineSeparator() +
				"record.setObject(\"ItemCD\",);" + System.lineSeparator() +
				"record.setObject(\"UnitPrice\",);" + System.lineSeparator() +
				"record.setObject(\"SellPrice\",);" + System.lineSeparator() +
				"record.setObject(\"Flag\",);" + System.lineSeparator() +
				"record.setObject(\"PriceFlag\",);" + System.lineSeparator();
		String executeApiStr = "update(\"jdbc_tool_test_mainmaster\")" + System.lineSeparator() +
				".set(record)" + System.lineSeparator();
		String executeApiWhereStr = ".where(expMap($($(\"jdbc_tool_test_mainmaster.UnitPrice > :jdbc_tool_test_mainmaster.UnitPrice\").AND(\"jdbc_tool_test_mainmaster.SellPrice < :jdbc_tool_test_mainmaster.SellPrice\").AND(\"jdbc_tool_test_mainmaster.Flag <= :jdbc_tool_test_mainmaster.Flag\").AND(\"jdbc_tool_test_mainmaster.PriceFlag >= :jdbc_tool_test_mainmaster.PriceFlag\")), whereParams))";
		assertEquals(whereParamsParts + recordStr + System.lineSeparator() + "int count = " + executeApiStr + executeApiWhereStr + System.lineSeparator() + ".execute();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE)));
		assertEquals(recordStr + System.lineSeparator() + "int count = " + executeApiStr + ".wherePK()" + System.lineSeparator() + ".execute();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_PK)));
		assertEquals(recordStr + System.lineSeparator() + "int count = " + executeApiStr + ".whereUK(\"MainMasterUniqueKey\")" + System.lineSeparator() + ".execute();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_UK)));

		StringBuilder recordsStr = new StringBuilder();
		recordsStr.append("List<GRecord> recordList = new ArrayList<>();").append(System.lineSeparator());
		recordsStr.append(recordStr);
		recordsStr.append("recordList.add(record);").append(System.lineSeparator());
		String executeListApiStr = "update(\"jdbc_tool_test_mainmaster\")" + System.lineSeparator() +
				".set(recordList)" + System.lineSeparator();
		assertEquals(recordsStr + System.lineSeparator() + "int count = " + executeListApiStr + ".wherePK()" + System.lineSeparator() + ".executeBatch();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_PK)));
		assertEquals(recordsStr + System.lineSeparator() + "int count = " + executeListApiStr + ".whereUK(\"MainMasterUniqueKey\")" + System.lineSeparator() + ".executeBatch();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_UK)));
		assertEquals(recordsStr + System.lineSeparator() + "int count = " + executeListApiStr + ".wherePK()" + System.lineSeparator() + ".executeList();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_PK)));
		assertEquals(recordsStr + System.lineSeparator() + "int count = " + executeListApiStr + ".whereUK(\"MainMasterUniqueKey\")" + System.lineSeparator() + ".executeList();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_UK)));
	}

	@Test
	public void testExport_searchExpWherePartsCompare_reg_delete() {

		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_searchExpWherePartsCompare_reg_delete.xml");
		String conditionFilePath = getConditionFilePath("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_searchExpWherePartsCompare_reg_delete.yaml");
		GExporterResult<List<GXFormCodeResult>, byte[]> result = service.export(conditionFilePath, settingsFilePath);

		assertEquals("html", result.getMediaType());
		assertEquals(1, result.getData().size());
		assertEquals(GCrudType.DELETE, result.getData().get(0).getType());
		assertEquals("仮想表ID: 59EF9C5DD58045A9A952B43DDC13F762", result.getData().get(0).getDescription());
		assertEquals(getDeleteSql_searchExpWherePartsCompare_reg_delete(), result.getData().get(0).getSqlScript());
		String whereParamsParts = "Map<String, Object> whereParams = new HashMap<>();" + System.lineSeparator() +
				"whereParams.put(\"jdbc_tool_test_mainmaster.Flag\",);" + System.lineSeparator() +
				"whereParams.put(\"jdbc_tool_test_mainmaster.UnitPrice\",);" + System.lineSeparator() +
				"whereParams.put(\"jdbc_tool_test_mainmaster.PriceFlag\",);" + System.lineSeparator() +
				"whereParams.put(\"jdbc_tool_test_mainmaster.SellPrice\",);";
		String executeApiStr = "delete(\"jdbc_tool_test_mainmaster\")" + System.lineSeparator() +
				".where(expMap($($(\"jdbc_tool_test_mainmaster.UnitPrice > :jdbc_tool_test_mainmaster.UnitPrice\").AND(\"jdbc_tool_test_mainmaster.SellPrice < :jdbc_tool_test_mainmaster.SellPrice\").AND(\"jdbc_tool_test_mainmaster.Flag <= :jdbc_tool_test_mainmaster.Flag\").AND(\"jdbc_tool_test_mainmaster.PriceFlag >= :jdbc_tool_test_mainmaster.PriceFlag\")), whereParams))";
		assertEquals(whereParamsParts + System.lineSeparator() + "int count = " + executeApiStr + System.lineSeparator() + ".execute();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE)));
	}
	
	public String getDeleteSql_searchExpWherePartsCompare_reg_delete() {
		return "DELETE FROM" + System.lineSeparator() +
				"    " + dbSchema + ".jdbc_tool_test_mainmaster" + System.lineSeparator() +
				"WHERE" + System.lineSeparator() +
				"    (" + System.lineSeparator() +
				"        " + dbSchema + ".jdbc_tool_test_mainmaster.UnitPrice > ?" + System.lineSeparator() +
				"        AND " + dbSchema + ".jdbc_tool_test_mainmaster.SellPrice < ?" + System.lineSeparator() +
				"        AND " + dbSchema + ".jdbc_tool_test_mainmaster.Flag <= ?" + System.lineSeparator() +
				"        AND " + dbSchema + ".jdbc_tool_test_mainmaster.PriceFlag >= ?" + System.lineSeparator() +
				"    )";
	}

	@Test
	public void testExport_searchExpWherePartsOptionTrim_reg_update() {

		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_searchExpWherePartsOptionTrim_reg_update.xml");
		String conditionFilePath = getConditionFilePath("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_searchExpWherePartsOptionTrim_reg_update.yaml");
		GExporterResult<List<GXFormCodeResult>, byte[]> result = service.export(conditionFilePath, settingsFilePath);

		assertEquals("html", result.getMediaType());
		assertEquals(1, result.getData().size());
		assertEquals(GCrudType.UPDATE, result.getData().get(0).getType());
		assertEquals("仮想表ID: F80DEB51F0004B1ABEB2D8EA44D4644F", result.getData().get(0).getDescription());
		assertEquals("UPDATE" + System.lineSeparator() +
				"    " + dbSchema + ".jdbc_tool_test_mainmaster jdbc_tool_test_mainmaster" + System.lineSeparator() +
				"SET" + System.lineSeparator() +
				"    ItemCD = ?," + System.lineSeparator() +
				"    UnitPrice = ?," + System.lineSeparator() +
				"    RegisteredDT = ?," + System.lineSeparator() +
				"    ExclusiveFG = ?" + System.lineSeparator() +
				"WHERE" + System.lineSeparator() +
				"    jdbc_tool_test_mainmaster.UnitPrice IN (100, 101, 102)" + System.lineSeparator() +
				"    AND (" + System.lineSeparator() +
				"        jdbc_tool_test_mainmaster.objectID LIKE ? ESCAPE '/'" + System.lineSeparator() +
				"        AND jdbc_tool_test_mainmaster.UnitPrice >= ?" + System.lineSeparator() +
				"    )", result.getData().get(0).getSqlScript());
		String whereParamsParts = "Map<String, Object> whereParams = new HashMap<>();" + System.lineSeparator() +
				"whereParams.put(\"jdbc_tool_test_mainmaster.UnitPrice\",);" + System.lineSeparator() +
				"whereParams.put(\"jdbc_tool_test_mainmaster.objectID\",);" + System.lineSeparator();
		String recordStr = "GRecord record = createRecord();" + System.lineSeparator() +
				"record.setObject(\"ItemCD\",);" + System.lineSeparator() +
				"record.setObject(\"UnitPrice\",);" + System.lineSeparator() +
				"record.setObject(\"RegisteredDT\",);" + System.lineSeparator() +
				"record.setObject(\"ExclusiveFG\",);" + System.lineSeparator();
		String executeApiStr = "update(\"jdbc_tool_test_mainmaster\")" + System.lineSeparator() +
				".set(record)" + System.lineSeparator();
		String executeApiWhereStr = ".where(expMap($(\"jdbc_tool_test_mainmaster.UnitPrice IN (100, 101, 102)\").AND($(\"jdbc_tool_test_mainmaster.objectID LIKE $CONCAT('%', $CONCAT($TRIM([:jdbc_tool_test_mainmaster.objectID]), '%')) ESCAPE '/'\").AND(\"jdbc_tool_test_mainmaster.UnitPrice >= $TRIM([:jdbc_tool_test_mainmaster.UnitPrice])\")), whereParams))";
		assertEquals(whereParamsParts + recordStr + System.lineSeparator() + "int count = " + executeApiStr + executeApiWhereStr + System.lineSeparator() + ".execute();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE)));
		assertEquals(recordStr + System.lineSeparator() + "int count = " + executeApiStr + ".wherePK()" + System.lineSeparator() + ".execute();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_PK)));
		assertEquals(recordStr + System.lineSeparator() + "int count = " + executeApiStr + ".whereUK(\"MainMasterUniqueKey\")" + System.lineSeparator() + ".execute();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_UK)));

		StringBuilder recordsStr = new StringBuilder();
		recordsStr.append("List<GRecord> recordList = new ArrayList<>();").append(System.lineSeparator());
		recordsStr.append(recordStr);
		recordsStr.append("recordList.add(record);").append(System.lineSeparator());
		String executeListApiStr = "update(\"jdbc_tool_test_mainmaster\")" + System.lineSeparator() +
				".set(recordList)" + System.lineSeparator();
		assertEquals(recordsStr + System.lineSeparator() + "int count = " + executeListApiStr + ".wherePK()" + System.lineSeparator() + ".executeBatch();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_PK)));
		assertEquals(recordsStr + System.lineSeparator() + "int count = " + executeListApiStr + ".whereUK(\"MainMasterUniqueKey\")" + System.lineSeparator() + ".executeBatch();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_UK)));
		assertEquals(recordsStr + System.lineSeparator() + "int count = " + executeListApiStr + ".wherePK()" + System.lineSeparator() + ".executeList();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_PK)));
		assertEquals(recordsStr + System.lineSeparator() + "int count = " + executeListApiStr + ".whereUK(\"MainMasterUniqueKey\")" + System.lineSeparator() + ".executeList();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_UK)));
	}

	@Test
	public void testExport_searchExpWherePartsOptionTrim_reg_delete() {

		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_searchExpWherePartsOptionTrim_reg_delete.xml");
		String conditionFilePath = getConditionFilePath("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_searchExpWherePartsOptionTrim_reg_delete.yaml");
		GExporterResult<List<GXFormCodeResult>, byte[]> result = service.export(conditionFilePath, settingsFilePath);

		assertEquals("html", result.getMediaType());
		assertEquals(1, result.getData().size());
		assertEquals(GCrudType.DELETE, result.getData().get(0).getType());
		assertEquals("仮想表ID: F80DEB51F0004B1ABEB2D8EA44D4644F", result.getData().get(0).getDescription());
		assertEquals(getDeleteSql_searchExpWherePartsOptionTrim_reg_delete(), result.getData().get(0).getSqlScript());
		String whereParamsParts = "Map<String, Object> whereParams = new HashMap<>();" + System.lineSeparator() +
				"whereParams.put(\"jdbc_tool_test_mainmaster.UnitPrice\",);" + System.lineSeparator() +
				"whereParams.put(\"jdbc_tool_test_mainmaster.objectID\",);";
		String executeApiStr = "delete(\"jdbc_tool_test_mainmaster\")" + System.lineSeparator() +
				".where(expMap($(\"jdbc_tool_test_mainmaster.UnitPrice IN (100, 101, 102)\").AND($(\"jdbc_tool_test_mainmaster.objectID LIKE $CONCAT('%', $CONCAT($TRIM([:jdbc_tool_test_mainmaster.objectID]), '%')) ESCAPE '/'\").AND(\"jdbc_tool_test_mainmaster.UnitPrice >= $TRIM([:jdbc_tool_test_mainmaster.UnitPrice])\")), whereParams))";
		assertEquals(whereParamsParts + System.lineSeparator() + "int count = " + executeApiStr + System.lineSeparator() + ".execute();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE)));
	}
	
	public String getDeleteSql_searchExpWherePartsOptionTrim_reg_delete() {
		return "DELETE FROM" + System.lineSeparator() +
				"    " + dbSchema + ".jdbc_tool_test_mainmaster" + System.lineSeparator() +
				"WHERE" + System.lineSeparator() +
				"    " + dbSchema + ".jdbc_tool_test_mainmaster.UnitPrice IN (100, 101, 102)" + System.lineSeparator() +
				"    AND (" + System.lineSeparator() +
				"        " + dbSchema + ".jdbc_tool_test_mainmaster.objectID LIKE ? ESCAPE '/'" + System.lineSeparator() +
				"        AND " + dbSchema + ".jdbc_tool_test_mainmaster.UnitPrice >= ?" + System.lineSeparator() +
				"    )";
	}

	@Test
	public void testExport_filterExpWherePartsLikeF_reg_update() {

		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_filterExpWherePartsLikeF_reg_update.xml");
		String conditionFilePath = getConditionFilePath("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_filterExpWherePartsLikeF_reg_update.yaml");
		GExporterResult<List<GXFormCodeResult>, byte[]> result = service.export(conditionFilePath, settingsFilePath);

		assertEquals("html", result.getMediaType());
		assertEquals(1, result.getData().size());
		assertEquals(GCrudType.UPDATE, result.getData().get(0).getType());
		assertEquals("仮想表ID: 718905E41FF64047B7AF5DE8E90B51C8", result.getData().get(0).getDescription());
		assertEquals("UPDATE" + System.lineSeparator() +
				"    " + dbSchema + ".jdbc_tool_test_mainmaster jdbc_tool_test_mainmaster" + System.lineSeparator() +
				"SET" + System.lineSeparator() +
				"    ItemCD = ?," + System.lineSeparator() +
				"    UnitPrice = ?" + System.lineSeparator() +
				"WHERE" + System.lineSeparator() +
				"    jdbc_tool_test_mainmaster.ItemCD LIKE 'item%'", result.getData().get(0).getSqlScript());
		String recordStr = "GRecord record = createRecord();" + System.lineSeparator() +
				"record.setObject(\"ItemCD\",);" + System.lineSeparator() +
				"record.setObject(\"UnitPrice\",);" + System.lineSeparator();
		String executeApiStr = "update(\"jdbc_tool_test_mainmaster\")" + System.lineSeparator() +
				".set(record)" + System.lineSeparator();
		String executeApiWhereStr = ".where(expMap($(\"jdbc_tool_test_mainmaster.ItemCD LIKE 'item%'\"), Collections.emptyMap()))";
		assertEquals(recordStr + System.lineSeparator() + "int count = " + executeApiStr + executeApiWhereStr + System.lineSeparator() + ".execute();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE)));
		assertEquals(recordStr + System.lineSeparator() + "int count = " + executeApiStr + ".wherePK()" + System.lineSeparator() + ".execute();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_PK)));
		assertEquals(recordStr + System.lineSeparator() + "int count = " + executeApiStr + ".whereUK(\"MainMasterUniqueKey\")" + System.lineSeparator() + ".execute();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_UK)));
		
		StringBuilder recordsStr = new StringBuilder();
		recordsStr.append("List<GRecord> recordList = new ArrayList<>();").append(System.lineSeparator());
		recordsStr.append(recordStr);
		recordsStr.append("recordList.add(record);").append(System.lineSeparator());
		String executeListApiStr = "update(\"jdbc_tool_test_mainmaster\")" + System.lineSeparator() +
				".set(recordList)" + System.lineSeparator();
		assertEquals(recordsStr + System.lineSeparator() + "int count = " + executeListApiStr + ".wherePK()" + System.lineSeparator() + ".executeBatch();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_PK)));
		assertEquals(recordsStr + System.lineSeparator() + "int count = " + executeListApiStr + ".whereUK(\"MainMasterUniqueKey\")" + System.lineSeparator() + ".executeBatch();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_UK)));
		assertEquals(recordsStr + System.lineSeparator() + "int count = " + executeListApiStr + ".wherePK()" + System.lineSeparator() + ".executeList();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_PK)));
		assertEquals(recordsStr + System.lineSeparator() + "int count = " + executeListApiStr + ".whereUK(\"MainMasterUniqueKey\")" + System.lineSeparator() + ".executeList();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_UK)));
	}

	@Test
	public void testExport_filterExpWherePartsLikeF_reg_delete() {

		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_filterExpWherePartsLikeF_reg_delete.xml");
		String conditionFilePath = getConditionFilePath("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_filterExpWherePartsLikeF_reg_delete.yaml");
		GExporterResult<List<GXFormCodeResult>, byte[]> result = service.export(conditionFilePath, settingsFilePath);

		assertEquals("html", result.getMediaType());
		assertEquals(1, result.getData().size());
		assertEquals(GCrudType.DELETE, result.getData().get(0).getType());
		assertEquals("仮想表ID: 718905E41FF64047B7AF5DE8E90B51C8", result.getData().get(0).getDescription());
		assertEquals(getDeleteSql_filterExpWherePartsLikeF_reg_delete(), result.getData().get(0).getSqlScript());
		String executeApiStr = "delete(\"jdbc_tool_test_mainmaster\")" + System.lineSeparator() +
				".where(expMap($(\"jdbc_tool_test_mainmaster.ItemCD LIKE 'item%'\"), Collections.emptyMap()))";
		assertEquals("int count = " + executeApiStr + System.lineSeparator() + ".execute();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE)));
	}
	
	public String getDeleteSql_filterExpWherePartsLikeF_reg_delete() {
		return "DELETE FROM" + System.lineSeparator() +
				"    " + dbSchema + ".jdbc_tool_test_mainmaster" + System.lineSeparator() +
				"WHERE" + System.lineSeparator() +
				"    " + dbSchema + ".jdbc_tool_test_mainmaster.ItemCD LIKE 'item%'";
	}

	@Test
	public void testExport_filterExpWherePartsLikeFB_reg_update() {

		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_filterExpWherePartsLikeFB_reg_update.xml");
		String conditionFilePath = getConditionFilePath("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_filterExpWherePartsLikeFB_reg_update.yaml");
		GExporterResult<List<GXFormCodeResult>, byte[]> result = service.export(conditionFilePath, settingsFilePath);

		assertEquals("html", result.getMediaType());
		assertEquals(1, result.getData().size());
		assertEquals(GCrudType.UPDATE, result.getData().get(0).getType());
		assertEquals("仮想表ID: 80A621FD138E4F28B53494E4DC8CBAAD", result.getData().get(0).getDescription());
		assertEquals("UPDATE" + System.lineSeparator() +
				"    " + dbSchema + ".jdbc_tool_test_unitmaster jdbc_tool_test_unitmaster" + System.lineSeparator() +
				"SET" + System.lineSeparator() +
				"    Code = ?," + System.lineSeparator() +
				"    Name = ?" + System.lineSeparator() +
				"WHERE" + System.lineSeparator() +
				"    jdbc_tool_test_unitmaster.Name LIKE '%name%'", result.getData().get(0).getSqlScript());
		String recordStr = "GRecord record = createRecord();" + System.lineSeparator() +
				"record.setObject(\"Code\",);" + System.lineSeparator() +
				"record.setObject(\"Name\",);" + System.lineSeparator();
		String executeApiStr = "update(\"jdbc_tool_test_unitmaster\")" + System.lineSeparator() +
				".set(record)" + System.lineSeparator();
		String executeApiWhereStr = ".where(expMap($(\"jdbc_tool_test_unitmaster.Name LIKE '%name%'\"), Collections.emptyMap()))";
		assertEquals(recordStr + System.lineSeparator() + "int count = " + executeApiStr + executeApiWhereStr + System.lineSeparator() + ".execute();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE)));
		assertEquals(recordStr + System.lineSeparator() + "int count = " + executeApiStr + ".wherePK()" + System.lineSeparator() + ".execute();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_PK)));
		assertEquals(recordStr + System.lineSeparator() + "int count = " + executeApiStr + ".whereUK(\"UnitMasterUK\")" + System.lineSeparator() + ".execute();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_UK)));

		StringBuilder recordsStr = new StringBuilder();
		recordsStr.append("List<GRecord> recordList = new ArrayList<>();").append(System.lineSeparator());
		recordsStr.append(recordStr);
		recordsStr.append("recordList.add(record);").append(System.lineSeparator());
		String executeListApiStr = "update(\"jdbc_tool_test_unitmaster\")" + System.lineSeparator() +
				".set(recordList)" + System.lineSeparator();
		assertEquals(recordsStr + System.lineSeparator() + "int count = " + executeListApiStr + ".wherePK()" + System.lineSeparator() + ".executeBatch();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_PK)));
		assertEquals(recordsStr + System.lineSeparator() + "int count = " + executeListApiStr + ".whereUK(\"UnitMasterUK\")" +  System.lineSeparator() + ".executeBatch();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_UK)));
		assertEquals(recordsStr + System.lineSeparator() + "int count = " + executeListApiStr + ".wherePK()" + System.lineSeparator() + ".executeList();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_PK)));
		assertEquals(recordsStr + System.lineSeparator() + "int count = " + executeListApiStr + ".whereUK(\"UnitMasterUK\")" +  System.lineSeparator() + ".executeList();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_UK)));
	}

	@Test
	public void testExport_filterExpWherePartsLikeFB_reg_delete() {

		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_filterExpWherePartsLikeFB_reg_delete.xml");
		String conditionFilePath = getConditionFilePath("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_filterExpWherePartsLikeFB_reg_delete.yaml");
		GExporterResult<List<GXFormCodeResult>, byte[]> result = service.export(conditionFilePath, settingsFilePath);

		assertEquals("html", result.getMediaType());
		assertEquals(1, result.getData().size());
		assertEquals(GCrudType.DELETE, result.getData().get(0).getType());
		assertEquals("仮想表ID: 80A621FD138E4F28B53494E4DC8CBAAD", result.getData().get(0).getDescription());
		assertEquals(getDeleteSql_filterExpWherePartsLikeFB_reg_delete(), result.getData().get(0).getSqlScript());
		String executeApiStr = "delete(\"jdbc_tool_test_unitmaster\")" + System.lineSeparator() +
				".where(expMap($(\"jdbc_tool_test_unitmaster.Name LIKE '%name%'\"), Collections.emptyMap()))";
		assertEquals("int count = " + executeApiStr + System.lineSeparator() + ".execute();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE)));
	}
	
	public String getDeleteSql_filterExpWherePartsLikeFB_reg_delete() {
		return "DELETE FROM" + System.lineSeparator() +
				"    " + dbSchema + ".jdbc_tool_test_unitmaster" + System.lineSeparator() +
				"WHERE" + System.lineSeparator() +
				"    " + dbSchema + ".jdbc_tool_test_unitmaster.Name LIKE '%name%'";
	}

	@Test
	public void testExport_filterExpWherePartsLikeB_reg_update() {

		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_filterExpWherePartsLikeB_reg_update.xml");
		String conditionFilePath = getConditionFilePath("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_filterExpWherePartsLikeB_reg_update.yaml");
		GExporterResult<List<GXFormCodeResult>, byte[]> result = service.export(conditionFilePath, settingsFilePath);

		assertEquals("html", result.getMediaType());
		assertEquals(1, result.getData().size());
		assertEquals(GCrudType.UPDATE, result.getData().get(0).getType());
		assertEquals("仮想表ID: 59CCE10D855740D89844D3C62AA18461", result.getData().get(0).getDescription());
		assertEquals("UPDATE" + System.lineSeparator() +
				"    " + dbSchema + ".jdbc_tool_test_ordermaster jdbc_tool_test_ordermaster" + System.lineSeparator() +
				"SET" + System.lineSeparator() +
				"    SlipNO = ?," + System.lineSeparator() +
				"    CorporateNA = ?" + System.lineSeparator() +
				"WHERE" + System.lineSeparator() +
				"    jdbc_tool_test_ordermaster.CorporateNA LIKE '%na'", result.getData().get(0).getSqlScript());
		String recordStr = "GRecord record = createRecord();" + System.lineSeparator() +
				"record.setObject(\"SlipNO\",);" + System.lineSeparator() +
				"record.setObject(\"CorporateNA\",);" + System.lineSeparator();
		String executeApiStr = "update(\"jdbc_tool_test_ordermaster\")" + System.lineSeparator() +
				".set(record)" + System.lineSeparator();
		String executeApiWhereStr = ".where(expMap($(\"jdbc_tool_test_ordermaster.CorporateNA LIKE '%na'\"), Collections.emptyMap()))";
		assertEquals(recordStr + System.lineSeparator() + "int count = " + executeApiStr + executeApiWhereStr + System.lineSeparator() + ".execute();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE)));
		assertEquals(recordStr + System.lineSeparator() + "int count = " + executeApiStr + ".wherePK()" + System.lineSeparator() + ".execute();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_PK)));
		assertEquals(recordStr + System.lineSeparator() + "int count = " + executeApiStr + ".whereUK(\"OrderMasterUK\")" + System.lineSeparator() + ".execute();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_UK)));

		StringBuilder recordsStr = new StringBuilder();
		recordsStr.append("List<GRecord> recordList = new ArrayList<>();").append(System.lineSeparator());
		recordsStr.append(recordStr);
		recordsStr.append("recordList.add(record);").append(System.lineSeparator());
		String executeListApiStr = "update(\"jdbc_tool_test_ordermaster\")" + System.lineSeparator() +
				".set(recordList)" + System.lineSeparator();
		assertEquals(recordsStr + System.lineSeparator() + "int count = " + executeListApiStr + ".wherePK()" + System.lineSeparator() + ".executeBatch();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_PK)));
		assertEquals(recordsStr + System.lineSeparator() + "int count = " + executeListApiStr + ".whereUK(\"OrderMasterUK\")" + System.lineSeparator() + ".executeBatch();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_UK)));
		assertEquals(recordsStr + System.lineSeparator() + "int count = " + executeListApiStr + ".wherePK()" + System.lineSeparator() + ".executeList();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_PK)));
		assertEquals(recordsStr + System.lineSeparator() + "int count = " + executeListApiStr + ".whereUK(\"OrderMasterUK\")" + System.lineSeparator() + ".executeList();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_UK)));
	}

	@Test
	public void testExport_filterExpWherePartsLikeB_reg_delete() {

		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_filterExpWherePartsLikeB_reg_delete.xml");
		String conditionFilePath = getConditionFilePath("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_filterExpWherePartsLikeB_reg_delete.yaml");
		GExporterResult<List<GXFormCodeResult>, byte[]> result = service.export(conditionFilePath, settingsFilePath);

		assertEquals("html", result.getMediaType());
		assertEquals(1, result.getData().size());
		assertEquals(GCrudType.DELETE, result.getData().get(0).getType());
		assertEquals("仮想表ID: 59CCE10D855740D89844D3C62AA18461", result.getData().get(0).getDescription());
		assertEquals(getDeleteSql_filterExpWherePartsLikeB_reg_delete(), result.getData().get(0).getSqlScript());
		String executeApiStr = "delete(\"jdbc_tool_test_ordermaster\")" + System.lineSeparator() +
				".where(expMap($(\"jdbc_tool_test_ordermaster.CorporateNA LIKE '%na'\"), Collections.emptyMap()))";
		assertEquals("int count = " + executeApiStr + System.lineSeparator() + ".execute();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE)));
	}
	
	public String getDeleteSql_filterExpWherePartsLikeB_reg_delete() {
		return "DELETE FROM" + System.lineSeparator() +
				"    " + dbSchema + ".jdbc_tool_test_ordermaster" + System.lineSeparator() +
				"WHERE" + System.lineSeparator() +
				"    " + dbSchema + ".jdbc_tool_test_ordermaster.CorporateNA LIKE '%na'";
	}

	@Test
	public void testExport_filterExpWherePartsEquals_reg_update() {

		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_filterExpWherePartsEquals_reg_update.xml");
		String conditionFilePath = getConditionFilePath("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_filterExpWherePartsEquals_reg_update.yaml");
		GExporterResult<List<GXFormCodeResult>, byte[]> result = service.export(conditionFilePath, settingsFilePath);

		assertEquals("html", result.getMediaType());
		assertEquals(1, result.getData().size());
		assertEquals(GCrudType.UPDATE, result.getData().get(0).getType());
		assertEquals("仮想表ID: 0F791999DDC84198A27A7309D24F6334", result.getData().get(0).getDescription());
		assertEquals("UPDATE" + System.lineSeparator() +
				"    " + dbSchema + ".jdbc_tool_test_mainmaster jdbc_tool_test_mainmaster" + System.lineSeparator() +
				"SET" + System.lineSeparator() +
				"    ItemCD = ?," + System.lineSeparator() +
				"    UnitPrice = ?" + System.lineSeparator() +
				"WHERE" + System.lineSeparator() +
				"    (" + System.lineSeparator() +
				"        jdbc_tool_test_mainmaster.objectID = '1001'" + System.lineSeparator() +
				"        AND jdbc_tool_test_mainmaster.ItemCD <> '9001'" + System.lineSeparator() +
				"    )", result.getData().get(0).getSqlScript());
		String recordStr = "GRecord record = createRecord();" + System.lineSeparator() +
				"record.setObject(\"ItemCD\",);" + System.lineSeparator() +
				"record.setObject(\"UnitPrice\",);" + System.lineSeparator();
		String executeApiStr = "update(\"jdbc_tool_test_mainmaster\")" + System.lineSeparator() +
				".set(record)" + System.lineSeparator();
		String executeApiWhereStr = ".where(expMap($($(\"jdbc_tool_test_mainmaster.objectID = '1001'\").AND(\"jdbc_tool_test_mainmaster.ItemCD <> '9001'\")), Collections.emptyMap()))";
		assertEquals(recordStr + System.lineSeparator() + "int count = " + executeApiStr + executeApiWhereStr + System.lineSeparator() + ".execute();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE)));
		assertEquals(recordStr + System.lineSeparator() + "int count = " + executeApiStr + ".wherePK()" + System.lineSeparator() + ".execute();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_PK)));
		assertEquals(recordStr + System.lineSeparator() + "int count = " + executeApiStr + ".whereUK(\"MainMasterUniqueKey\")" + System.lineSeparator() + ".execute();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_UK)));

		StringBuilder recordsStr = new StringBuilder();
		recordsStr.append("List<GRecord> recordList = new ArrayList<>();").append(System.lineSeparator());
		recordsStr.append(recordStr);
		recordsStr.append("recordList.add(record);").append(System.lineSeparator());
		String executeListApiStr = "update(\"jdbc_tool_test_mainmaster\")" + System.lineSeparator() +
				".set(recordList)" + System.lineSeparator();
		assertEquals(recordsStr + System.lineSeparator() + "int count = " + executeListApiStr + ".wherePK()"  + System.lineSeparator() + ".executeBatch();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_PK)));
		assertEquals(recordsStr + System.lineSeparator() + "int count = " + executeListApiStr + ".whereUK(\"MainMasterUniqueKey\")" + System.lineSeparator() + ".executeBatch();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_UK)));
		assertEquals(recordsStr + System.lineSeparator() + "int count = " + executeListApiStr + ".wherePK()"  + System.lineSeparator() + ".executeList();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_PK)));
		assertEquals(recordsStr + System.lineSeparator() + "int count = " + executeListApiStr + ".whereUK(\"MainMasterUniqueKey\")" + System.lineSeparator() + ".executeList();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_UK)));
	}

	@Test
	public void testExport_filterExpWherePartsEquals_reg_delete() {

		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_filterExpWherePartsEquals_reg_delete.xml");
		String conditionFilePath = getConditionFilePath("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_filterExpWherePartsEquals_reg_delete.yaml");
		GExporterResult<List<GXFormCodeResult>, byte[]> result = service.export(conditionFilePath, settingsFilePath);

		assertEquals("html", result.getMediaType());
		assertEquals(1, result.getData().size());
		assertEquals(GCrudType.DELETE, result.getData().get(0).getType());
		assertEquals("仮想表ID: 0F791999DDC84198A27A7309D24F6334", result.getData().get(0).getDescription());
		assertEquals(getDeleteSql_filterExpWherePartsEquals_reg_delete(), result.getData().get(0).getSqlScript());
		String executeApiStr = "delete(\"jdbc_tool_test_mainmaster\")" + System.lineSeparator() +
				".where(expMap($($(\"jdbc_tool_test_mainmaster.objectID = '1001'\").AND(\"jdbc_tool_test_mainmaster.ItemCD <> '9001'\")), Collections.emptyMap()))";
		assertEquals("int count = " + executeApiStr + System.lineSeparator() + ".execute();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE)));
	}
	
	public String getDeleteSql_filterExpWherePartsEquals_reg_delete() {
		return "DELETE FROM" + System.lineSeparator() +
				"    " + dbSchema + ".jdbc_tool_test_mainmaster" + System.lineSeparator() +
				"WHERE" + System.lineSeparator() +
				"    (" + System.lineSeparator() +
				"        " + dbSchema + ".jdbc_tool_test_mainmaster.objectID = '1001'" + System.lineSeparator() +
				"        AND " + dbSchema + ".jdbc_tool_test_mainmaster.ItemCD <> '9001'" + System.lineSeparator() +
				"    )";
	}

	@Test
	public void testExport_filterExpWherePartsCompare_reg_update() {

		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_filterExpWherePartsCompare_reg_update.xml");
		String conditionFilePath = getConditionFilePath("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_filterExpWherePartsCompare_reg_update.yaml");
		GExporterResult<List<GXFormCodeResult>, byte[]> result = service.export(conditionFilePath, settingsFilePath);

		assertEquals("html", result.getMediaType());
		assertEquals(1, result.getData().size());
		assertEquals(GCrudType.UPDATE, result.getData().get(0).getType());
		assertEquals("仮想表ID: 9F42C74C3ACA4DA9815963583F3F6D1D", result.getData().get(0).getDescription());
		assertEquals("UPDATE" + System.lineSeparator() +
				"    " + dbSchema + ".jdbc_tool_test_mainmaster jdbc_tool_test_mainmaster" + System.lineSeparator() +
				"SET" + System.lineSeparator() +
				"    ItemCD = ?," + System.lineSeparator() +
				"    UnitPrice = ?," + System.lineSeparator() +
				"    SellPrice = ?," + System.lineSeparator() +
				"    Flag = ?," + System.lineSeparator() +
				"    PriceFlag = ?" + System.lineSeparator() +
				"WHERE" + System.lineSeparator() +
				"    (" + System.lineSeparator() +
				"        jdbc_tool_test_mainmaster.UnitPrice > 100" + System.lineSeparator() +
				"        AND jdbc_tool_test_mainmaster.UnitPrice < 500" + System.lineSeparator() +
				"        AND jdbc_tool_test_mainmaster.SellPrice <= 1000" + System.lineSeparator() +
				"        AND jdbc_tool_test_mainmaster.SellPrice >= 300" + System.lineSeparator() +
				"    )", result.getData().get(0).getSqlScript());
		String recordStr = "GRecord record = createRecord();" + System.lineSeparator() +
				"record.setObject(\"ItemCD\",);" + System.lineSeparator() +
				"record.setObject(\"UnitPrice\",);" + System.lineSeparator() +
				"record.setObject(\"SellPrice\",);" + System.lineSeparator() +
				"record.setObject(\"Flag\",);" + System.lineSeparator() +
				"record.setObject(\"PriceFlag\",);" + System.lineSeparator();
		String executeApiStr = "update(\"jdbc_tool_test_mainmaster\")" + System.lineSeparator() +
				".set(record)" + System.lineSeparator();
		String executeWhereApiStr = ".where(expMap($($(\"jdbc_tool_test_mainmaster.UnitPrice > 100\").AND(\"jdbc_tool_test_mainmaster.UnitPrice < 500\").AND(\"jdbc_tool_test_mainmaster.SellPrice <= 1000\").AND(\"jdbc_tool_test_mainmaster.SellPrice >= 300\")), Collections.emptyMap()))";
		assertEquals(recordStr + System.lineSeparator() + "int count = " + executeApiStr + executeWhereApiStr + System.lineSeparator() + ".execute();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE)));
		assertEquals(recordStr + System.lineSeparator() + "int count = " + executeApiStr + ".wherePK()" + System.lineSeparator() + ".execute();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_PK)));
		assertEquals(recordStr + System.lineSeparator() + "int count = " + executeApiStr + ".whereUK(\"MainMasterUniqueKey\")" + System.lineSeparator() + ".execute();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_UK)));

		StringBuilder recordsStr = new StringBuilder();
		recordsStr.append("List<GRecord> recordList = new ArrayList<>();").append(System.lineSeparator());
		recordsStr.append(recordStr);
		recordsStr.append("recordList.add(record);").append(System.lineSeparator());
		String executeListApiStr = "update(\"jdbc_tool_test_mainmaster\")" + System.lineSeparator() +
				".set(recordList)" + System.lineSeparator();
		assertEquals(recordsStr + System.lineSeparator() + "int count = " + executeListApiStr + ".wherePK()"  + System.lineSeparator() + ".executeBatch();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_PK)));
		assertEquals(recordsStr + System.lineSeparator() + "int count = " + executeListApiStr + ".whereUK(\"MainMasterUniqueKey\")"  + System.lineSeparator() + ".executeBatch();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_UK)));
		assertEquals(recordsStr + System.lineSeparator() + "int count = " + executeListApiStr + ".wherePK()"  + System.lineSeparator() + ".executeList();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_PK)));
		assertEquals(recordsStr + System.lineSeparator() + "int count = " + executeListApiStr + ".whereUK(\"MainMasterUniqueKey\")"  + System.lineSeparator() + ".executeList();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_UK)));
	}

	@Test
	public void testExport_filterExpWherePartsCompare_reg_delete() {

		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_filterExpWherePartsCompare_reg_delete.xml");
		String conditionFilePath = getConditionFilePath("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_filterExpWherePartsCompare_reg_delete.yaml");
		GExporterResult<List<GXFormCodeResult>, byte[]> result = service.export(conditionFilePath, settingsFilePath);

		assertEquals("html", result.getMediaType());
		assertEquals(1, result.getData().size());
		assertEquals(GCrudType.DELETE, result.getData().get(0).getType());
		assertEquals("仮想表ID: 9F42C74C3ACA4DA9815963583F3F6D1D", result.getData().get(0).getDescription());
		assertEquals(getDeleteSql_filterExpWherePartsCompare_reg_delete(), result.getData().get(0).getSqlScript());
		String executeApiStr = "delete(\"jdbc_tool_test_mainmaster\")" + System.lineSeparator() +
				".where(expMap($($(\"jdbc_tool_test_mainmaster.UnitPrice > 100\").AND(\"jdbc_tool_test_mainmaster.UnitPrice < 500\").AND(\"jdbc_tool_test_mainmaster.SellPrice <= 1000\").AND(\"jdbc_tool_test_mainmaster.SellPrice >= 300\")), Collections.emptyMap()))";
		assertEquals("int count = " + executeApiStr + System.lineSeparator() + ".execute();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE)));
	}
	
	public String getDeleteSql_filterExpWherePartsCompare_reg_delete() {
		return "DELETE FROM" + System.lineSeparator() +
				"    " + dbSchema + ".jdbc_tool_test_mainmaster" + System.lineSeparator() +
				"WHERE" + System.lineSeparator() +
				"    (" + System.lineSeparator() +
				"        " + dbSchema + ".jdbc_tool_test_mainmaster.UnitPrice > 100" + System.lineSeparator() +
				"        AND " + dbSchema + ".jdbc_tool_test_mainmaster.UnitPrice < 500" + System.lineSeparator() +
				"        AND " + dbSchema + ".jdbc_tool_test_mainmaster.SellPrice <= 1000" + System.lineSeparator() +
				"        AND " + dbSchema + ".jdbc_tool_test_mainmaster.SellPrice >= 300" + System.lineSeparator() +
				"    )";
	}

	@Test
	public void testExport_filterExpWherePartsNullreg_update() {

		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_filterExpWherePartsNull_reg_update.xml");
		String conditionFilePath = getConditionFilePath("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_filterExpWherePartsNull_reg_update.yaml");
		GExporterResult<List<GXFormCodeResult>, byte[]> result = service.export(conditionFilePath, settingsFilePath);

		assertEquals("html", result.getMediaType());
		assertEquals(1, result.getData().size());
		assertEquals(GCrudType.UPDATE, result.getData().get(0).getType());
		assertEquals("仮想表ID: AD3F414042CA40FFA5F91F6DE9997A45", result.getData().get(0).getDescription());
			assertEquals("UPDATE" + System.lineSeparator() +
				"    " + dbSchema + ".jdbc_tool_test_mainmaster jdbc_tool_test_mainmaster" + System.lineSeparator() +
				"SET" + System.lineSeparator() +
				"    ItemCD = ?," + System.lineSeparator() +
				"    UnitPrice = ?" + System.lineSeparator() +
				"WHERE" + System.lineSeparator() +
				"    (" + System.lineSeparator() +
				"        jdbc_tool_test_mainmaster.ItemCD IS NULL" + System.lineSeparator() +
				"        AND jdbc_tool_test_mainmaster.UnitPrice IS NOT NULL" + System.lineSeparator() +
				"    )", result.getData().get(0).getSqlScript());
		String recordStr = "GRecord record = createRecord();" + System.lineSeparator() +
				"record.setObject(\"ItemCD\",);" + System.lineSeparator() +
				"record.setObject(\"UnitPrice\",);" + System.lineSeparator();
		String executeApiStr = "update(\"jdbc_tool_test_mainmaster\")" + System.lineSeparator() +
				".set(record)" + System.lineSeparator();
		String executeApiWhereStr = ".where(expMap($($(\"jdbc_tool_test_mainmaster.ItemCD IS NULL\").AND(\"jdbc_tool_test_mainmaster.UnitPrice IS NOT NULL\")), Collections.emptyMap()))";
		assertEquals(recordStr + System.lineSeparator() + "int count = " + executeApiStr + executeApiWhereStr + System.lineSeparator() + ".execute();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE)));
		assertEquals(recordStr + System.lineSeparator() + "int count = " + executeApiStr + ".wherePK()" + System.lineSeparator() + ".execute();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_PK)));
		assertEquals(recordStr + System.lineSeparator() + "int count = " + executeApiStr + ".whereUK(\"MainMasterUniqueKey\")" + System.lineSeparator() + ".execute();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_UK)));

		StringBuilder recordsStr = new StringBuilder();
		recordsStr.append("List<GRecord> recordList = new ArrayList<>();").append(System.lineSeparator());
		recordsStr.append(recordStr);
		recordsStr.append("recordList.add(record);").append(System.lineSeparator());
		String executeListApiStr = "update(\"jdbc_tool_test_mainmaster\")" + System.lineSeparator() +
				".set(recordList)" + System.lineSeparator();
		assertEquals(recordsStr + System.lineSeparator() + "int count = " + executeListApiStr + ".wherePK()" + System.lineSeparator() + ".executeBatch();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_PK)));
		assertEquals(recordsStr + System.lineSeparator() + "int count = " + executeListApiStr + ".whereUK(\"MainMasterUniqueKey\")" + System.lineSeparator() + ".executeBatch();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_UK)));
		assertEquals(recordsStr + System.lineSeparator() + "int count = " + executeListApiStr + ".wherePK()" + System.lineSeparator() + ".executeList();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_PK)));
		assertEquals(recordsStr + System.lineSeparator() + "int count = " + executeListApiStr + ".whereUK(\"MainMasterUniqueKey\")" + System.lineSeparator() + ".executeList();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_UK)));
	}

	@Test
	public void testExport_filterExpWherePartsNull_reg_delete() {

		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_filterExpWherePartsNull_reg_delete.xml");
		String conditionFilePath = getConditionFilePath("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_filterExpWherePartsNull_reg_delete.yaml");
		GExporterResult<List<GXFormCodeResult>, byte[]> result = service.export(conditionFilePath, settingsFilePath);

		assertEquals("html", result.getMediaType());
		assertEquals(1, result.getData().size());
		assertEquals(GCrudType.DELETE, result.getData().get(0).getType());
		assertEquals("仮想表ID: AD3F414042CA40FFA5F91F6DE9997A45", result.getData().get(0).getDescription());
		assertEquals(getDeleteSql_filterExpWherePartsNull_reg_delete(), result.getData().get(0).getSqlScript());
		String executeApiStr = "delete(\"jdbc_tool_test_mainmaster\")" + System.lineSeparator() +
				".where(expMap($($(\"jdbc_tool_test_mainmaster.ItemCD IS NULL\").AND(\"jdbc_tool_test_mainmaster.UnitPrice IS NOT NULL\")), Collections.emptyMap()))";
		assertEquals("int count = " + executeApiStr + System.lineSeparator() + ".execute();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE)));
	}
	
	public String getDeleteSql_filterExpWherePartsNull_reg_delete() {
		return "DELETE FROM" + System.lineSeparator() +
				"    " + dbSchema + ".jdbc_tool_test_mainmaster" + System.lineSeparator() +
				"WHERE" + System.lineSeparator() +
				"    (" + System.lineSeparator() +
				"        " + dbSchema + ".jdbc_tool_test_mainmaster.ItemCD IS NULL" + System.lineSeparator() +
				"        AND " + dbSchema + ".jdbc_tool_test_mainmaster.UnitPrice IS NOT NULL" + System.lineSeparator() +
				"    )";
	}

	@Test
	public void testExport_filterExpWherePartsIn_reg_update() {

		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_filterExpWherePartsIn_reg_update.xml");
		String conditionFilePath = getConditionFilePath("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_filterExpWherePartsIn_reg_update.yaml");
		GExporterResult<List<GXFormCodeResult>, byte[]> result = service.export(conditionFilePath, settingsFilePath);

		assertEquals("html", result.getMediaType());
		assertEquals(1, result.getData().size());
		assertEquals(GCrudType.UPDATE, result.getData().get(0).getType());
		assertEquals("仮想表ID: E1F30CCA20B54049B9ADFBD8488168DD", result.getData().get(0).getDescription());
		assertEquals("UPDATE" + System.lineSeparator() +
				"    " + dbSchema + ".jdbc_tool_test_mainmaster jdbc_tool_test_mainmaster" + System.lineSeparator() +
				"SET" + System.lineSeparator() +
				"    ItemCD = ?," + System.lineSeparator() +
				"    UnitPrice = ?" + System.lineSeparator() +
				"WHERE" + System.lineSeparator() +
				"    (" + System.lineSeparator() +
				"        jdbc_tool_test_mainmaster.ItemCD IN ('item010', '1001')" + System.lineSeparator() +
				"        AND jdbc_tool_test_mainmaster.UnitPrice NOT IN (10, 20, 30)" + System.lineSeparator() +
				"    )", result.getData().get(0).getSqlScript());
		String recordStr = "GRecord record = createRecord();" + System.lineSeparator() +
				"record.setObject(\"ItemCD\",);" + System.lineSeparator() +
				"record.setObject(\"UnitPrice\",);" + System.lineSeparator();
		String executeApiStr = "update(\"jdbc_tool_test_mainmaster\")" + System.lineSeparator() +
				".set(record)" + System.lineSeparator();
		String executeApiWhereStr = ".where(expMap($($(\"jdbc_tool_test_mainmaster.ItemCD IN ('item010', '1001')\").AND(\"jdbc_tool_test_mainmaster.UnitPrice NOT IN (10, 20, 30)\")), Collections.emptyMap()))";
		assertEquals(recordStr + System.lineSeparator() + "int count = " + executeApiStr + executeApiWhereStr + System.lineSeparator() + ".execute();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE)));
		assertEquals(recordStr + System.lineSeparator() + "int count = " + executeApiStr + ".wherePK()" + System.lineSeparator() + ".execute();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_PK)));
		assertEquals(recordStr + System.lineSeparator() + "int count = " + executeApiStr + ".whereUK(\"MainMasterUniqueKey\")" + System.lineSeparator() + ".execute();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_UK)));

		StringBuilder recordsStr = new StringBuilder();
		recordsStr.append("List<GRecord> recordList = new ArrayList<>();").append(System.lineSeparator());
		recordsStr.append(recordStr);
		recordsStr.append("recordList.add(record);").append(System.lineSeparator());
		String executeListApiStr = "update(\"jdbc_tool_test_mainmaster\")" + System.lineSeparator() +
				".set(recordList)" + System.lineSeparator();
		assertEquals(recordsStr + System.lineSeparator() + "int count = " + executeListApiStr+ ".wherePK()"  + System.lineSeparator() + ".executeBatch();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_PK)));
		assertEquals(recordsStr + System.lineSeparator() + "int count = " + executeListApiStr + ".whereUK(\"MainMasterUniqueKey\")" + System.lineSeparator() + ".executeBatch();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_UK)));
		assertEquals(recordsStr + System.lineSeparator() + "int count = " + executeListApiStr+ ".wherePK()"  + System.lineSeparator() + ".executeList();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_PK)));
		assertEquals(recordsStr + System.lineSeparator() + "int count = " + executeListApiStr + ".whereUK(\"MainMasterUniqueKey\")" + System.lineSeparator() + ".executeList();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_UK)));
	}

	@Test
	public void testExport_filterExpWherePartsIn_reg_delete() {

		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_filterExpWherePartsIn_reg_delete.xml");
		String conditionFilePath = getConditionFilePath("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_filterExpWherePartsIn_reg_delete.yaml");
		GExporterResult<List<GXFormCodeResult>, byte[]> result = service.export(conditionFilePath, settingsFilePath);

		assertEquals("html", result.getMediaType());
		assertEquals(1, result.getData().size());
		assertEquals(GCrudType.DELETE, result.getData().get(0).getType());
		assertEquals("仮想表ID: E1F30CCA20B54049B9ADFBD8488168DD", result.getData().get(0).getDescription());
		assertEquals(getDeleteSql_filterExpWherePartsIn_reg_delete(), result.getData().get(0).getSqlScript());
		String executeApiStr = "delete(\"jdbc_tool_test_mainmaster\")" + System.lineSeparator() +
				".where(expMap($($(\"jdbc_tool_test_mainmaster.ItemCD IN ('item010', '1001')\").AND(\"jdbc_tool_test_mainmaster.UnitPrice NOT IN (10, 20, 30)\")), Collections.emptyMap()))";
		assertEquals("int count = " + executeApiStr + System.lineSeparator() + ".execute();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE)));
	}
	
	public String getDeleteSql_filterExpWherePartsIn_reg_delete() {
		return "DELETE FROM" + System.lineSeparator() +
				"    " + dbSchema + ".jdbc_tool_test_mainmaster" + System.lineSeparator() +
				"WHERE" + System.lineSeparator() +
				"    (" + System.lineSeparator() +
				"        " + dbSchema + ".jdbc_tool_test_mainmaster.ItemCD IN ('item010', '1001')" + System.lineSeparator() +
				"        AND " + dbSchema + ".jdbc_tool_test_mainmaster.UnitPrice NOT IN (10, 20, 30)" + System.lineSeparator() +
				"    )";
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
		assertEquals("SELECT" + System.lineSeparator() +
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
				"    LEFT OUTER JOIN " + dbSchema + ".jdbc_tool_test_unitmaster jdbc_tool_test_unitmaster ON jdbc_tool_test_pk_uk_mainmaster.objectID = jdbc_tool_test_unitmaster.Code" + System.lineSeparator() +
				"    INNER JOIN " + dbSchema + ".jdbc_tool_test_mainmaster jdbc_tool_test_mainmaster ON jdbc_tool_test_pk_uk_mainmaster.objectID = jdbc_tool_test_mainmaster.objectID" + System.lineSeparator() +
				"    RIGHT OUTER JOIN " + dbSchema + ".jdbc_tool_test_ordermaster jdbc_tool_test_ordermaster ON jdbc_tool_test_pk_uk_mainmaster.ItemCD = jdbc_tool_test_ordermaster.objectID" + System.lineSeparator() +
				"WHERE" + System.lineSeparator() +
				"    jdbc_tool_test_pk_uk_mainmaster.objectID = ?", result.getData().get(0).getSqlScript());

		String whereParamsParts = "Map<String, Object> whereParams = new HashMap<>();" + System.lineSeparator() +
				"whereParams.put(\"jdbc_tool_test_pk_uk_mainmaster.objectID\",);" + System.lineSeparator();
		String groupByApiStr = "select(\"jdbc_tool_test_pk_uk_mainmaster\", \"jdbc_tool_test_pk_uk_mainmaster\")" + System.lineSeparator() +
				".fields(colsAll())" + System.lineSeparator() +
				".leftOuterJoinFK(\"FK_UnitMaster\", \"jdbc_tool_test_unitmaster\")" + System.lineSeparator() +
				".innerJoinFK(\"FK_MainMaster\", \"jdbc_tool_test_mainmaster\")" + System.lineSeparator() +
				".rightOuterJoinFK(\"FK_OrderMaster\", \"jdbc_tool_test_ordermaster\")" + System.lineSeparator() +
				".where(expMap($(\"jdbc_tool_test_pk_uk_mainmaster.objectID = :jdbc_tool_test_pk_uk_mainmaster.objectID\"), whereParams))";

		assertEquals(whereParamsParts + "List<GRecord> result = " + groupByApiStr + System.lineSeparator() + ".findList();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE)));
		assertEquals(whereParamsParts + "GRecord result = " + groupByApiStr + System.lineSeparator() + ".findRecord();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE)));
		assertEquals(whereParamsParts + "GRecordSet result = " + groupByApiStr + System.lineSeparator() + ".findRecordSet();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE)));
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
		assertEquals("SELECT" + System.lineSeparator() +
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
				"    LEFT OUTER JOIN " + dbSchema + ".jdbc_tool_test_unitmaster jdbc_tool_test_unitmaster ON jdbc_tool_test_pk_uk_mainmaster.objectID = jdbc_tool_test_unitmaster.Code" + System.lineSeparator() +
				"    INNER JOIN " + dbSchema + ".jdbc_tool_test_mainmaster jdbc_tool_test_mainmaster ON jdbc_tool_test_pk_uk_mainmaster.objectID = jdbc_tool_test_mainmaster.objectID" + System.lineSeparator() +
				"    RIGHT OUTER JOIN " + dbSchema + ".jdbc_tool_test_ordermaster jdbc_tool_test_ordermaster ON jdbc_tool_test_pk_uk_mainmaster.ItemCD = jdbc_tool_test_ordermaster.objectID" + System.lineSeparator() +
				"WHERE" + System.lineSeparator() +
				"    (" + System.lineSeparator() +
				"        jdbc_tool_test_pk_uk_mainmaster.objectID = ?" + System.lineSeparator() +
				"        AND jdbc_tool_test_unitmaster.Code = ?" + System.lineSeparator() +
				"        AND jdbc_tool_test_mainmaster.objectID = ?" + System.lineSeparator() +
				"        AND jdbc_tool_test_ordermaster.SlipNO LIKE ? ESCAPE '/'" + System.lineSeparator() +
				"        AND jdbc_tool_test_pk_uk_mainmaster.ItemCD LIKE ? ESCAPE '/'" + System.lineSeparator() +
				"        AND jdbc_tool_test_pk_uk_mainmaster.ItemNA LIKE ? ESCAPE '/'" + System.lineSeparator() +
				"    )", result.getData().get(0).getSqlScript());

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
				".leftOuterJoinFK(\"FK_UnitMaster\", \"jdbc_tool_test_unitmaster\")" + System.lineSeparator() +
				".innerJoinFK(\"FK_MainMaster\", \"jdbc_tool_test_mainmaster\")" + System.lineSeparator() +
				".rightOuterJoinFK(\"FK_OrderMaster\", \"jdbc_tool_test_ordermaster\")" + System.lineSeparator() +
				".where(expMap($($(\"jdbc_tool_test_pk_uk_mainmaster.objectID = :jdbc_tool_test_pk_uk_mainmaster.objectID\").AND(\"jdbc_tool_test_unitmaster.Code = :jdbc_tool_test_unitmaster.Code\").AND(\"jdbc_tool_test_mainmaster.objectID = :jdbc_tool_test_mainmaster.objectID\").AND(\"jdbc_tool_test_ordermaster.SlipNO LIKE $CONCAT(:jdbc_tool_test_ordermaster.SlipNO, '%') ESCAPE '/'\").AND(\"jdbc_tool_test_pk_uk_mainmaster.ItemCD LIKE $CONCAT('%', $CONCAT(:jdbc_tool_test_pk_uk_mainmaster.ItemCD, '%')) ESCAPE '/'\").AND(\"jdbc_tool_test_pk_uk_mainmaster.ItemNA LIKE $CONCAT('%', :jdbc_tool_test_pk_uk_mainmaster.ItemNA) ESCAPE '/'\")), whereParams))";

		assertEquals(whereParamsParts + "List<GRecord> result = " + groupByApiStr + System.lineSeparator() + ".findList();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE)));
		assertEquals(whereParamsParts + "GRecord result = " + groupByApiStr + System.lineSeparator() + ".findRecord();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE)));
		assertEquals(whereParamsParts + "GRecordSet result = " + groupByApiStr + System.lineSeparator() + ".findRecordSet();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE)));
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
		assertEquals("SELECT" + System.lineSeparator() +
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
				"    LEFT OUTER JOIN " + dbSchema + ".jdbc_tool_test_unitmaster jdbc_tool_test_unitmaster ON jdbc_tool_test_pk_uk_mainmaster.objectID = jdbc_tool_test_unitmaster.Code" + System.lineSeparator() +
				"    INNER JOIN " + dbSchema + ".jdbc_tool_test_mainmaster jdbc_tool_test_mainmaster ON jdbc_tool_test_pk_uk_mainmaster.objectID = jdbc_tool_test_mainmaster.objectID" + System.lineSeparator() +
				"    RIGHT OUTER JOIN " + dbSchema + ".jdbc_tool_test_ordermaster jdbc_tool_test_ordermaster ON jdbc_tool_test_pk_uk_mainmaster.ItemCD = jdbc_tool_test_ordermaster.objectID" + System.lineSeparator() +
				"WHERE" + System.lineSeparator() +
				"    (" + System.lineSeparator() +
				"        jdbc_tool_test_pk_uk_mainmaster.objectID = ?" + System.lineSeparator() +
				"        AND jdbc_tool_test_unitmaster.Code = ?" + System.lineSeparator() +
				"        AND jdbc_tool_test_mainmaster.objectID = ?" + System.lineSeparator() +
				"        AND jdbc_tool_test_ordermaster.SlipNO LIKE ? ESCAPE '/'" + System.lineSeparator() +
				"        AND jdbc_tool_test_pk_uk_mainmaster.ItemCD LIKE ? ESCAPE '/'" + System.lineSeparator() +
				"        AND jdbc_tool_test_pk_uk_mainmaster.ItemNA LIKE ? ESCAPE '/'" + System.lineSeparator() +
				"    )", result.getData().get(0).getSqlScript());

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
				".leftOuterJoinFK(\"FK_UnitMaster\", \"jdbc_tool_test_unitmaster\")" + System.lineSeparator() +
				".innerJoinFK(\"FK_MainMaster\", \"jdbc_tool_test_mainmaster\")" + System.lineSeparator() +
				".rightOuterJoinFK(\"FK_OrderMaster\", \"jdbc_tool_test_ordermaster\")" + System.lineSeparator() +
				".where(expMap($($(\"jdbc_tool_test_pk_uk_mainmaster.objectID = [:jdbc_tool_test_pk_uk_mainmaster.objectID]\").AND(\"jdbc_tool_test_unitmaster.Code = $TRIM(:jdbc_tool_test_unitmaster.Code)\").AND(\"jdbc_tool_test_mainmaster.objectID = $TRIM([:jdbc_tool_test_mainmaster.objectID])\").AND(\"jdbc_tool_test_ordermaster.SlipNO LIKE $CONCAT(:jdbc_tool_test_ordermaster.SlipNO, '%') ESCAPE '/'\").AND(\"jdbc_tool_test_pk_uk_mainmaster.ItemCD LIKE $CONCAT('%', $CONCAT(:jdbc_tool_test_pk_uk_mainmaster.ItemCD, '%')) ESCAPE '/'\").AND(\"jdbc_tool_test_pk_uk_mainmaster.ItemNA LIKE $CONCAT('%', :jdbc_tool_test_pk_uk_mainmaster.ItemNA) ESCAPE '/'\")), whereParams))";

		assertEquals(whereParamsParts + "List<GRecord> result = " + groupByApiStr + System.lineSeparator() + ".findList();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE)));
		assertEquals(whereParamsParts + "GRecord result = " + groupByApiStr + System.lineSeparator() + ".findRecord();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE)));
		assertEquals(whereParamsParts + "GRecordSet result = " + groupByApiStr + System.lineSeparator() + ".findRecordSet();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE)));
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
		assertEquals("SELECT" + System.lineSeparator() +
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
				"    LEFT OUTER JOIN " + dbSchema + ".jdbc_tool_test_unitmaster jdbc_tool_test_unitmaster ON jdbc_tool_test_pk_uk_mainmaster.objectID = jdbc_tool_test_unitmaster.Code" + System.lineSeparator() +
				"    INNER JOIN " + dbSchema + ".jdbc_tool_test_mainmaster jdbc_tool_test_mainmaster ON jdbc_tool_test_pk_uk_mainmaster.objectID = jdbc_tool_test_mainmaster.objectID" + System.lineSeparator() +
				"    RIGHT OUTER JOIN " + dbSchema + ".jdbc_tool_test_ordermaster jdbc_tool_test_ordermaster ON jdbc_tool_test_pk_uk_mainmaster.ItemCD = jdbc_tool_test_ordermaster.objectID" + System.lineSeparator() +
				"WHERE" + System.lineSeparator() +
				"    (" + System.lineSeparator() +
				"        jdbc_tool_test_pk_uk_mainmaster.objectID = '1001'" + System.lineSeparator() +
				"        AND jdbc_tool_test_unitmaster.Code = '1001'" + System.lineSeparator() +
				"        AND jdbc_tool_test_mainmaster.objectID = '1001'" + System.lineSeparator() +
				"        AND jdbc_tool_test_ordermaster.SlipNO LIKE '100%'" + System.lineSeparator() +
				"        AND jdbc_tool_test_pk_uk_mainmaster.ItemCD LIKE '%100%'" + System.lineSeparator() +
				"        AND jdbc_tool_test_pk_uk_mainmaster.ItemNA LIKE '%gef%'" + System.lineSeparator() +
				"    )", result.getData().get(0).getSqlScript());

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
				".leftOuterJoinFK(\"FK_UnitMaster\", \"jdbc_tool_test_unitmaster\")" + System.lineSeparator() +
				".innerJoinFK(\"FK_MainMaster\", \"jdbc_tool_test_mainmaster\")" + System.lineSeparator() +
				".rightOuterJoinFK(\"FK_OrderMaster\", \"jdbc_tool_test_ordermaster\")" + System.lineSeparator() +
				".where(exp($($(\"jdbc_tool_test_pk_uk_mainmaster.objectID = '1001'\").AND(\"jdbc_tool_test_unitmaster.Code = '1001'\").AND(\"jdbc_tool_test_mainmaster.objectID = '1001'\").AND(\"jdbc_tool_test_ordermaster.SlipNO LIKE '100%'\").AND(\"jdbc_tool_test_pk_uk_mainmaster.ItemCD LIKE '%100%'\").AND(\"jdbc_tool_test_pk_uk_mainmaster.ItemNA LIKE '%gef%'\"))))";

		assertEquals("List<GRecord> result = " + joinColumnFilterExpApiStr + System.lineSeparator() + ".findList();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE)));
		assertEquals("GRecord result = " + joinColumnFilterExpApiStr + System.lineSeparator() + ".findRecord();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE)));
		assertEquals("GRecordSet result = " + joinColumnFilterExpApiStr + System.lineSeparator() + ".findRecordSet();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE)));
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
		assertEquals("SELECT" + System.lineSeparator() +
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
				"    LEFT OUTER JOIN " + dbSchema + ".jdbc_tool_test_unitmaster jdbc_tool_test_unitmaster ON jdbc_tool_test_pk_uk_mainmaster.objectID = jdbc_tool_test_unitmaster.Code" + System.lineSeparator() +
				"    INNER JOIN " + dbSchema + ".jdbc_tool_test_mainmaster jdbc_tool_test_mainmaster ON jdbc_tool_test_pk_uk_mainmaster.objectID = jdbc_tool_test_mainmaster.objectID" + System.lineSeparator() +
				"    RIGHT OUTER JOIN " + dbSchema + ".jdbc_tool_test_ordermaster jdbc_tool_test_ordermaster ON jdbc_tool_test_pk_uk_mainmaster.ItemCD = jdbc_tool_test_ordermaster.objectID" + System.lineSeparator() +
				"WHERE" + System.lineSeparator() +
				"    (" + System.lineSeparator() +
				"        jdbc_tool_test_pk_uk_mainmaster.objectID = '1001'" + System.lineSeparator() +
				"        AND jdbc_tool_test_unitmaster.Code = '1001'" + System.lineSeparator() +
				"        AND jdbc_tool_test_mainmaster.objectID = '1001'" + System.lineSeparator() +
				"        AND jdbc_tool_test_ordermaster.SlipNO LIKE '100%'" + System.lineSeparator() +
				"        AND jdbc_tool_test_pk_uk_mainmaster.ItemCD LIKE '%100%'" + System.lineSeparator() +
				"        AND jdbc_tool_test_pk_uk_mainmaster.ItemNA LIKE '%gef%'" + System.lineSeparator() +
				"    )", result.getData().get(0).getSqlScript());

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
				".leftOuterJoinFK(\"FK_UnitMaster\", \"jdbc_tool_test_unitmaster\")" + System.lineSeparator() +
				".innerJoinFK(\"FK_MainMaster\", \"jdbc_tool_test_mainmaster\")" + System.lineSeparator() +
				".rightOuterJoinFK(\"FK_OrderMaster\", \"jdbc_tool_test_ordermaster\")" + System.lineSeparator() +
				".where(expMap($($(\"jdbc_tool_test_pk_uk_mainmaster.objectID = '1001'\").AND(\"jdbc_tool_test_unitmaster.Code = '1001'\").AND(\"jdbc_tool_test_mainmaster.objectID = '1001'\").AND(\"jdbc_tool_test_ordermaster.SlipNO LIKE '100%'\").AND(\"jdbc_tool_test_pk_uk_mainmaster.ItemCD LIKE '%100%'\").AND(\"jdbc_tool_test_pk_uk_mainmaster.ItemNA LIKE '%gef%'\")), Collections.emptyMap()))";

		assertEquals("List<GRecord> result = " + joinColumnFilterExpApiStr + System.lineSeparator() + ".findList();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE)));
		assertEquals("GRecord result = " + joinColumnFilterExpApiStr + System.lineSeparator() + ".findRecord();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE)));
		assertEquals("GRecordSet result = " + joinColumnFilterExpApiStr + System.lineSeparator() + ".findRecordSet();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE)));
	}

	@Test
	public void testExport_columnAndConstValueMixFK() {

		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_columnAndConstValueMixFK.xml");
		String conditionFilePath = getConditionFilePath("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_columnAndConstValueMixFK_inq.yaml");
		GExporterResult<List<GXFormCodeResult>, byte[]> result = service.export(conditionFilePath, settingsFilePath);

		assertEquals("html", result.getMediaType());
		assertEquals(1, result.getData().size());
		assertEquals(GCrudType.SELECT, result.getData().get(0).getType());
		assertEquals("仮想表ID: AF3EB4CFAFD649868AB42E624707EBD8", result.getData().get(0).getDescription());
		assertEquals("SELECT" + System.lineSeparator() +
				"    jdcb_tool_test_allColumntype.objectID," + System.lineSeparator() +
				"    jdcb_tool_test_allColumntype.StringColumn," + System.lineSeparator() +
				"    jdcb_tool_test_allColumntype.IntColumn," + System.lineSeparator() +
				"    jdcb_tool_test_allColumntype.NStringColumn," + System.lineSeparator() +
				"    jdcb_tool_test_allColumntype.DateTimeColumn," + System.lineSeparator() +
				"    jdcb_tool_test_allColumntype.YMColumn," + System.lineSeparator() +
				"    jdcb_tool_test_allColumntype.CurrencyColumn," + System.lineSeparator() +
				"    jdcb_tool_test_allColumntype.LongColumn," + System.lineSeparator() +
				"    jdcb_tool_test_allColumntype.CompanyCD," + System.lineSeparator() +
				"    jdcb_tool_test_allColumntype.ExclusiveFG," + System.lineSeparator() +
				"    jdcb_tool_test_allColumntype.RegisteredPerson," + System.lineSeparator() +
				"    jdcb_tool_test_allColumntype.RegisteredDT," + System.lineSeparator() +
				"    jdcb_tool_test_allColumntype.UpdatedPerson," + System.lineSeparator() +
				"    jdcb_tool_test_allColumntype.UpdatedDT" + System.lineSeparator() +
				"FROM" + System.lineSeparator() +
				"    " + dbSchema + ".jdcb_tool_test_allColumntype jdcb_tool_test_allColumntype" + System.lineSeparator() +
				"    INNER JOIN " + dbSchema + ".jdbc_tool_test_pk_uk_mainmaster jdbc_tool_test_pk_uk_mainmaster ON jdcb_tool_test_allColumntype.objectID = jdbc_tool_test_pk_uk_mainmaster.objectID" + System.lineSeparator() +
				"    AND jdbc_tool_test_pk_uk_mainmaster.Flag = 1001" + System.lineSeparator() +
				"WHERE" + System.lineSeparator() +
				"    jdcb_tool_test_allColumntype.CompanyCD = ?", result.getData().get(0).getSqlScript());

		String whereParams = "Map<String, Object> whereParams = new HashMap<>();" + System.lineSeparator() +
				"whereParams.put(\"jdcb_tool_test_allColumntype.CompanyCD\",);" + System.lineSeparator();
		String joinColumnFilterExpApiStr = "select(\"jdcb_tool_test_allColumntype\", \"jdcb_tool_test_allColumntype\")" + System.lineSeparator() +
				".fields(colsAll())" + System.lineSeparator() +
				".innerJoin(\"jdbc_tool_test_pk_uk_mainmaster\", \"jdbc_tool_test_pk_uk_mainmaster\", expMap($(\"jdcb_tool_test_allColumntype.objectID = jdbc_tool_test_pk_uk_mainmaster.objectID\").AND(\"jdbc_tool_test_pk_uk_mainmaster.Flag = 1001\"), Collections.emptyMap()))" + System.lineSeparator() +
				".where(expMap($(\"jdcb_tool_test_allColumntype.CompanyCD = $TRIM(:jdcb_tool_test_allColumntype.CompanyCD)\"), whereParams))";

		assertEquals(whereParams + "List<GRecord> result = " + joinColumnFilterExpApiStr + System.lineSeparator() + ".findList();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE)));
		assertEquals(whereParams + "GRecord result = " + joinColumnFilterExpApiStr + System.lineSeparator() + ".findRecord();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE)));
		assertEquals(whereParams + "GRecordSet result = " + joinColumnFilterExpApiStr + System.lineSeparator() + ".findRecordSet();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE)));
	}

	@Test
	public void testExport_onlyConstValueFK() {

		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_onlyConstValueFK.xml");
		String conditionFilePath = getConditionFilePath("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_onlyConstValueFK_inq.yaml");
		GExporterResult<List<GXFormCodeResult>, byte[]> result = service.export(conditionFilePath, settingsFilePath);

		assertEquals("html", result.getMediaType());
		assertEquals(1, result.getData().size());
		assertEquals(GCrudType.SELECT, result.getData().get(0).getType());
		assertEquals("仮想表ID: FABF321D11FD48129DBD9D148F66CEAA", result.getData().get(0).getDescription());
		assertEquals("SELECT" + System.lineSeparator() +
				"    jdcb_tool_test_allColumntype.objectID," + System.lineSeparator() +
				"    jdcb_tool_test_allColumntype.StringColumn," + System.lineSeparator() +
				"    jdcb_tool_test_allColumntype.IntColumn," + System.lineSeparator() +
				"    jdcb_tool_test_allColumntype.NStringColumn," + System.lineSeparator() +
				"    jdcb_tool_test_allColumntype.DateTimeColumn," + System.lineSeparator() +
				"    jdcb_tool_test_allColumntype.YMColumn," + System.lineSeparator() +
				"    jdcb_tool_test_allColumntype.CurrencyColumn," + System.lineSeparator() +
				"    jdcb_tool_test_allColumntype.LongColumn," + System.lineSeparator() +
				"    jdcb_tool_test_allColumntype.CompanyCD," + System.lineSeparator() +
				"    jdcb_tool_test_allColumntype.ExclusiveFG," + System.lineSeparator() +
				"    jdcb_tool_test_allColumntype.RegisteredPerson," + System.lineSeparator() +
				"    jdcb_tool_test_allColumntype.RegisteredDT," + System.lineSeparator() +
				"    jdcb_tool_test_allColumntype.UpdatedPerson," + System.lineSeparator() +
				"    jdcb_tool_test_allColumntype.UpdatedDT" + System.lineSeparator() +
				"FROM" + System.lineSeparator() +
				"    " + dbSchema + ".jdcb_tool_test_allColumntype jdcb_tool_test_allColumntype" + System.lineSeparator() +
				"    INNER JOIN " + dbSchema + ".jdbc_tool_test_pk_uk_mainmaster jdbc_tool_test_pk_uk_mainmaster ON jdbc_tool_test_pk_uk_mainmaster.objectID = '1001'" + System.lineSeparator() +
				"    AND jdbc_tool_test_pk_uk_mainmaster.ItemCD = '9001'" + System.lineSeparator() +
				"WHERE" + System.lineSeparator() +
				"    jdcb_tool_test_allColumntype.CompanyCD = ?", result.getData().get(0).getSqlScript());

		String whereParams = "Map<String, Object> whereParams = new HashMap<>();" + System.lineSeparator() +
				"whereParams.put(\"jdcb_tool_test_allColumntype.CompanyCD\",);" + System.lineSeparator();
		String joinColumnFilterExpApiStr = "select(\"jdcb_tool_test_allColumntype\", \"jdcb_tool_test_allColumntype\")" + System.lineSeparator() +
				".fields(colsAll())" + System.lineSeparator() +  ".innerJoin(\"jdbc_tool_test_pk_uk_mainmaster\", \"jdbc_tool_test_pk_uk_mainmaster\", expMap($(\"jdbc_tool_test_pk_uk_mainmaster.objectID = '1001'\").AND(\"jdbc_tool_test_pk_uk_mainmaster.ItemCD = '9001'\"), Collections.emptyMap()))" + System.lineSeparator() +
				".where(expMap($(\"jdcb_tool_test_allColumntype.CompanyCD = $TRIM(:jdcb_tool_test_allColumntype.CompanyCD)\"), whereParams))";

		assertEquals(whereParams + "List<GRecord> result = " + joinColumnFilterExpApiStr + System.lineSeparator() + ".findList();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE)));
		assertEquals(whereParams + "GRecord result = " + joinColumnFilterExpApiStr + System.lineSeparator() + ".findRecord();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE)));
		assertEquals(whereParams + "GRecordSet result = " + joinColumnFilterExpApiStr + System.lineSeparator() + ".findRecordSet();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE)));
	}
	
	protected abstract String getTimestampCommand();

	@Test
	public void testExport_insert() {
		
		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_insert.xml");
		String conditionFilePath = getConditionFilePath("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_insert.yaml");
		GExporterResult<List<GXFormCodeResult>, byte[]> result = service.export(conditionFilePath, settingsFilePath);

		assertEquals("html", result.getMediaType());
		assertEquals(1, result.getData().size());
		assertEquals(GCrudType.INSERT, result.getData().get(0).getType());
		assertEquals("仮想表ID: 1FEFE156727646DC9122045339BD3999", result.getData().get(0).getDescription());
		assertEquals("INSERT INTO" + System.lineSeparator() +
				"    " + dbSchema + ".jdbc_tool_test_pk_uk_mainmaster(objectID, ItemCD, ItemNA, UnitPrice, SellPrice, Flag, PriceFlag, ExclusiveFG, RegisteredDT, UpdatedDT)" + System.lineSeparator() +
				"VALUES" + System.lineSeparator() +
				"(?, ?, ?, ?, ?, ?, ?, ?, " + getTimestampCommand() + ", " + getTimestampCommand() + ")", result.getData().get(0).getSqlScript());
		String recordStr = "GRecord record = createRecord();" + System.lineSeparator() +
				"record.setObject(\"objectID\",);" + System.lineSeparator() +
				"record.setObject(\"ItemCD\",);" + System.lineSeparator() +
				"record.setObject(\"ItemNA\",);" + System.lineSeparator() +
				"record.setObject(\"UnitPrice\",);" + System.lineSeparator() +
				"record.setObject(\"SellPrice\",);" + System.lineSeparator() +
				"record.setObject(\"Flag\",);" + System.lineSeparator() +
				"record.setObject(\"PriceFlag\",);" + System.lineSeparator() +
				"record.setObject(\"ExclusiveFG\",);" + System.lineSeparator() +
				"record.setObject(\"RegisteredDT\",);" + System.lineSeparator() +
				"record.setObject(\"UpdatedDT\",);" + System.lineSeparator();
		String executeApiStr = "insert(\"jdbc_tool_test_pk_uk_mainmaster\")" + System.lineSeparator() +
				".values(record)";
		assertEquals(recordStr + System.lineSeparator() + "int count = " + executeApiStr + System.lineSeparator() + ".execute();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, null)));

		StringBuilder recordsStr = new StringBuilder();
		recordsStr.append("List<GRecord> recordList = new ArrayList<>();").append(System.lineSeparator());
		recordsStr.append(recordStr);
		recordsStr.append("recordList.add(record);").append(System.lineSeparator());
		String executeListApiStr = "insert(\"jdbc_tool_test_pk_uk_mainmaster\")" + System.lineSeparator() +
				".values(recordList)";
		assertEquals(recordsStr + System.lineSeparator() + "int count = " + executeListApiStr + System.lineSeparator() + ".executeBatch();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, null)));
		assertEquals(recordsStr + System.lineSeparator() + "int count = " + executeListApiStr + System.lineSeparator() + ".executeList();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, null)));
	
	}
	
	@Test
	public void testExport_whereParts_SameColumn() {
		
		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_whereParts_sameColumn.xml");
		String conditionFilePath = getConditionFilePath("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_whereParts_sameColumn.yaml");
		GExporterResult<List<GXFormCodeResult>, byte[]> result = service.export(conditionFilePath, settingsFilePath);
		
		assertEquals("html", result.getMediaType());
		assertEquals(1, result.getData().size());
		assertEquals(GCrudType.SELECT, result.getData().get(0).getType());
		assertEquals("仮想表ID: FBF32CF5550E402096711A5F1B4012B1", result.getData().get(0).getDescription());
		assertEquals("SELECT" + System.lineSeparator() +
				"    jdcb_tool_test_allColumntype.objectID," + System.lineSeparator() +
				"    jdcb_tool_test_allColumntype.StringColumn," + System.lineSeparator() +
				"    jdcb_tool_test_allColumntype.IntColumn," + System.lineSeparator() +
				"    jdcb_tool_test_allColumntype.NStringColumn," + System.lineSeparator() +
				"    jdcb_tool_test_allColumntype.DateTimeColumn," + System.lineSeparator() +
				"    jdcb_tool_test_allColumntype.YMColumn," + System.lineSeparator() +
				"    jdcb_tool_test_allColumntype.CurrencyColumn," + System.lineSeparator() +
				"    jdcb_tool_test_allColumntype.LongColumn," + System.lineSeparator() +
				"    jdcb_tool_test_allColumntype.CompanyCD," + System.lineSeparator() +
				"    jdcb_tool_test_allColumntype.ExclusiveFG," + System.lineSeparator() +
				"    jdcb_tool_test_allColumntype.RegisteredPerson," + System.lineSeparator() +
				"    jdcb_tool_test_allColumntype.RegisteredDT," + System.lineSeparator() +
				"    jdcb_tool_test_allColumntype.UpdatedPerson," + System.lineSeparator() +
				"    jdcb_tool_test_allColumntype.UpdatedDT" + System.lineSeparator() +
				"FROM" + System.lineSeparator() +
				"    " + dbSchema + ".jdcb_tool_test_allColumntype jdcb_tool_test_allColumntype" + System.lineSeparator() +
				"WHERE" + System.lineSeparator() +
				"    (" + System.lineSeparator() +
				"        jdcb_tool_test_allColumntype.objectID = '\"test1\"'" + System.lineSeparator() +
				"        OR jdcb_tool_test_allColumntype.objectID = '\"test2\"'" + System.lineSeparator() +
				"    )" + System.lineSeparator() +
				"    AND (" + System.lineSeparator() +
				"        jdcb_tool_test_allColumntype.objectID = ?" + System.lineSeparator() +
				"        OR jdcb_tool_test_allColumntype.objectID = ?" + System.lineSeparator() +
				"        AND jdcb_tool_test_allColumntype.CompanyCD = ?" + System.lineSeparator() +
				"    )", result.getData().get(0).getSqlScript());

		String whereParams = "Map<String, Object> whereParams = new HashMap<>();" + System.lineSeparator() +
				"whereParams.put(\"jdcb_tool_test_allColumntype.CompanyCD\",);" + System.lineSeparator() +
				"whereParams.put(\"jdcb_tool_test_allColumntype.objectID\",);" + System.lineSeparator();
		String joinColumnFilterExpApiStr = "select(\"jdcb_tool_test_allColumntype\", \"jdcb_tool_test_allColumntype\")" + System.lineSeparator() +
				".fields(colsAll())" + System.lineSeparator() +
				".where(expMap($($(\"jdcb_tool_test_allColumntype.objectID = '\"test1\"'\").OR(\"jdcb_tool_test_allColumntype.objectID = '\"test2\"'\")).AND($(\"jdcb_tool_test_allColumntype.objectID = [:jdcb_tool_test_allColumntype.objectID]\").OR(\"jdcb_tool_test_allColumntype.objectID = $TRIM([:jdcb_tool_test_allColumntype.objectID])\").AND(\"jdcb_tool_test_allColumntype.CompanyCD = $TRIM(:jdcb_tool_test_allColumntype.CompanyCD)\")), whereParams))";

		assertEquals(whereParams + "List<GRecord> result = " + joinColumnFilterExpApiStr + System.lineSeparator() + ".findList();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE)));
		assertEquals(whereParams + "GRecord result = " + joinColumnFilterExpApiStr + System.lineSeparator() + ".findRecord();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE)));
		assertEquals(whereParams + "GRecordSet result = " + joinColumnFilterExpApiStr + System.lineSeparator() + ".findRecordSet();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE)));
	}

	@Test
	public void testExport_categoryId() {

		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_categoryId.xml");
		String conditionFilePath = getConditionFilePath("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_category.yaml");
		GExporterResult<List<GXFormCodeResult>, byte[]> result = service.export(conditionFilePath, settingsFilePath);
		assertEquals(8, result.getData().size());
		assertEquals(GCrudType.SELECT, result.getData().get(0).getType());
		assertEquals(new String("仮想表ID: F08F656C201141ADBD1B743F144A44A0 (カテゴリID: F56264D123E6450DB2AF788A7039CA41)".getBytes(StandardCharsets.UTF_8)), result.getData().get(0).getDescription());
		assertEquals(GCrudType.DELETE, result.getData().get(1).getType());
		assertEquals(new String("仮想表ID: F08F656C201141ADBD1B743F144A44A0 (カテゴリID: F56264D123E6450DB2AF788A7039CA41)".getBytes(StandardCharsets.UTF_8)), result.getData().get(1).getDescription());
		assertEquals(GCrudType.INSERT, result.getData().get(2).getType());
		assertEquals(new String("仮想表ID: BC37EC6B0EFF411980A0385FFB38D035 (カテゴリID: F56264D123E6450DB2AF788A7039CA41)".getBytes(StandardCharsets.UTF_8)), result.getData().get(2).getDescription());
		assertEquals(GCrudType.UPDATE, result.getData().get(3).getType());
		assertEquals(new String("仮想表ID: BC37EC6B0EFF411980A0385FFB38D035 (カテゴリID: F56264D123E6450DB2AF788A7039CA41)".getBytes(StandardCharsets.UTF_8)), result.getData().get(3).getDescription());
		assertEquals(GCrudType.SELECT, result.getData().get(4).getType());
		assertEquals(new String("仮想表ID: BC37EC6B0EFF411980A0385FFB38D035 (カテゴリID: F56264D123E6450DB2AF788A7039CA41)".getBytes(StandardCharsets.UTF_8)), result.getData().get(4).getDescription());
		assertEquals(GCrudType.DELETE, result.getData().get(5).getType());
		assertEquals(new String("仮想表ID: BC37EC6B0EFF411980A0385FFB38D035 (カテゴリID: F56264D123E6450DB2AF788A7039CA41)".getBytes(StandardCharsets.UTF_8)), result.getData().get(5).getDescription());
		assertEquals(GCrudType.SELECT, result.getData().get(6).getType());
		assertEquals(new String("仮想表ID: A4F67FC9EE6249AF9F5DE398C9231974 (カテゴリID: B27393A2779C472C88EEF38FABCFE118)".getBytes(StandardCharsets.UTF_8)), result.getData().get(6).getDescription());
		assertEquals(GCrudType.DELETE, result.getData().get(7).getType());
		assertEquals(new String("仮想表ID: A4F67FC9EE6249AF9F5DE398C9231974 (カテゴリID: B27393A2779C472C88EEF38FABCFE118)".getBytes(StandardCharsets.UTF_8)), result.getData().get(7).getDescription());
	}

	@Test
	public void testExportAll() {
		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_exportall.xml");
		GExporterResult<List<GXFormCodeResult>, byte[]> result = service.export(null, settingsFilePath);
		assertEquals(12, result.getData().size());
		assertEquals(GCrudType.DELETE, result.getData().get(0).getType());
		assertEquals(new String("仮想表ID: F08F656C201141ADBD1B743F144A44A0 (カテゴリID: F56264D123E6450DB2AF788A7039CA41)".getBytes(StandardCharsets.UTF_8)), result.getData().get(0).getDescription());
		assertEquals(GCrudType.SELECT, result.getData().get(1).getType());
		assertEquals(new String("仮想表ID: F08F656C201141ADBD1B743F144A44A0 (カテゴリID: F56264D123E6450DB2AF788A7039CA41)".getBytes(StandardCharsets.UTF_8)), result.getData().get(1).getDescription());
		assertEquals(GCrudType.DELETE, result.getData().get(2).getType());
		assertEquals(new String("仮想表ID: BC37EC6B0EFF411980A0385FFB38D035 (カテゴリID: F56264D123E6450DB2AF788A7039CA41)".getBytes(StandardCharsets.UTF_8)), result.getData().get(2).getDescription());
		assertEquals(GCrudType.UPDATE, result.getData().get(3).getType());
		assertEquals(new String("仮想表ID: BC37EC6B0EFF411980A0385FFB38D035 (カテゴリID: F56264D123E6450DB2AF788A7039CA41)".getBytes(StandardCharsets.UTF_8)), result.getData().get(3).getDescription());
		assertEquals(GCrudType.INSERT, result.getData().get(4).getType());
		assertEquals(new String("仮想表ID: BC37EC6B0EFF411980A0385FFB38D035 (カテゴリID: F56264D123E6450DB2AF788A7039CA41)".getBytes(StandardCharsets.UTF_8)), result.getData().get(4).getDescription());
		assertEquals(GCrudType.SELECT, result.getData().get(5).getType());
		assertEquals(new String("仮想表ID: BC37EC6B0EFF411980A0385FFB38D035 (カテゴリID: F56264D123E6450DB2AF788A7039CA41)".getBytes(StandardCharsets.UTF_8)), result.getData().get(5).getDescription());
		assertEquals(GCrudType.DELETE, result.getData().get(6).getType());
		assertEquals(new String("仮想表ID: A4F67FC9EE6249AF9F5DE398C9231974 (カテゴリID: B27393A2779C472C88EEF38FABCFE118)".getBytes(StandardCharsets.UTF_8)), result.getData().get(6).getDescription());
		assertEquals(GCrudType.SELECT, result.getData().get(7).getType());
		assertEquals(new String("仮想表ID: A4F67FC9EE6249AF9F5DE398C9231974 (カテゴリID: B27393A2779C472C88EEF38FABCFE118)".getBytes(StandardCharsets.UTF_8)), result.getData().get(7).getDescription());
		assertEquals(GCrudType.DELETE, result.getData().get(8).getType());
		assertEquals(new String("仮想表ID: 1FEFE156727646DC9122045339BD3999 (カテゴリID: F0D5F2FA82D04F29909340A50C4976D7)".getBytes(StandardCharsets.UTF_8)), result.getData().get(8).getDescription());
		assertEquals(GCrudType.UPDATE, result.getData().get(9).getType());
		assertEquals(new String("仮想表ID: 1FEFE156727646DC9122045339BD3999 (カテゴリID: F0D5F2FA82D04F29909340A50C4976D7)".getBytes(StandardCharsets.UTF_8)), result.getData().get(9).getDescription());
		assertEquals(GCrudType.INSERT, result.getData().get(10).getType());
		assertEquals(new String("仮想表ID: 1FEFE156727646DC9122045339BD3999 (カテゴリID: F0D5F2FA82D04F29909340A50C4976D7)".getBytes(StandardCharsets.UTF_8)), result.getData().get(10).getDescription());
		assertEquals(GCrudType.SELECT, result.getData().get(11).getType());
		assertEquals(new String("仮想表ID: 1FEFE156727646DC9122045339BD3999 (カテゴリID: F0D5F2FA82D04F29909340A50C4976D7)".getBytes(StandardCharsets.UTF_8)), result.getData().get(11).getDescription());
	}
	
	@Test
	public void testExport_veType_inq() {
		
		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_vetype_inq.xml");
		String conditionFilePath = getConditionFilePath("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_vetype_inq.yaml");
		GExporterResult<List<GXFormCodeResult>, byte[]> result = service.export(conditionFilePath, settingsFilePath);

		assertEquals("html", result.getMediaType());
		assertEquals(2, result.getData().size());
		assertEquals(GCrudType.SELECT, result.getData().get(0).getType());
		assertEquals(GCrudType.DELETE, result.getData().get(1).getType());
		assertEquals("仮想表ID: 4E52A89202B145159B8256C53A433A24", result.getData().get(1).getDescription());
		assertEquals(getDeleteSql_veType_inq(), result.getData().get(1).getSqlScript());
		String whereParamsParts = "Map<String, Object> whereParams = new HashMap<>();" + System.lineSeparator() +
				"whereParams.put(\"jdbc_tool_test_mainmaster.UnitPrice\",);" + System.lineSeparator() +
				"whereParams.put(\"jdbc_tool_test_mainmaster.ItemCD\",);";
		String deleteApiStr = "delete(\"jdbc_tool_test_mainmaster\")" + System.lineSeparator() +
				".where(expMap($($(\"jdbc_tool_test_mainmaster.ItemCD = $TRIM([:jdbc_tool_test_mainmaster.ItemCD])\").AND(\"jdbc_tool_test_mainmaster.UnitPrice <> $TRIM([:jdbc_tool_test_mainmaster.UnitPrice])\")), whereParams))";

		assertNull(result.getData().get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE)));
		assertEquals(whereParamsParts + System.lineSeparator() + "int count = " + deleteApiStr + System.lineSeparator() + ".execute();", result.getData().get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE)));
		assertNull(result.getData().get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE)));
	}
	
	public String getDeleteSql_veType_inq() {
		return "DELETE FROM" + System.lineSeparator() +
				"    " + dbSchema + ".jdbc_tool_test_mainmaster" + System.lineSeparator() +
				"WHERE" + System.lineSeparator() +
				"    (" + System.lineSeparator() +
				"        " + dbSchema + ".jdbc_tool_test_mainmaster.ItemCD = ?" + System.lineSeparator() +
				"        AND " + dbSchema + ".jdbc_tool_test_mainmaster.UnitPrice <> ?" + System.lineSeparator() +
				"    )";
	}
	
	@Test
	public void testExport_veType_reg() {

		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_vetype_reg.xml");
		String conditionFilePath = getConditionFilePath("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_vetype_reg.yaml");
		GExporterResult<List<GXFormCodeResult>, byte[]> result = service.export(conditionFilePath, settingsFilePath);

		assertEquals("html", result.getMediaType());
		assertEquals(4, result.getData().size());
		assertEquals(GCrudType.INSERT, result.getData().get(0).getType());
		assertEquals(GCrudType.UPDATE, result.getData().get(1).getType());
		assertEquals(GCrudType.DELETE, result.getData().get(3).getType());
		assertEquals(GCrudType.SELECT, result.getData().get(2).getType());
		assertEquals("仮想表ID: CC71DD95CF7244DFB80A009930DB3F95", result.getData().get(2).getDescription());
		assertEquals("SELECT" + System.lineSeparator() +
				"    jdbc_tool_test_mainmaster.objectID," + System.lineSeparator() +
				"    jdbc_tool_test_mainmaster.ItemCD," + System.lineSeparator() +
				"    jdbc_tool_test_mainmaster.UnitPrice" + System.lineSeparator() +
				"FROM" + System.lineSeparator() +
				"    " + dbSchema + ".jdbc_tool_test_mainmaster jdbc_tool_test_mainmaster" + System.lineSeparator() +
				"WHERE" + System.lineSeparator() +
				"    (" + System.lineSeparator() +
				"        jdbc_tool_test_mainmaster.ItemCD = ?" + System.lineSeparator() +
				"        AND jdbc_tool_test_mainmaster.UnitPrice <> ?" + System.lineSeparator() +
				"    )", result.getData().get(2).getSqlScript());
		String whereParamsParts = "Map<String, Object> whereParams = new HashMap<>();" + System.lineSeparator() +
				"whereParams.put(\"jdbc_tool_test_mainmaster.UnitPrice\",);" + System.lineSeparator() +
				"whereParams.put(\"jdbc_tool_test_mainmaster.ItemCD\",);" + System.lineSeparator();
		String selectApiStr = "select(\"jdbc_tool_test_mainmaster\", \"jdbc_tool_test_mainmaster\")" + System.lineSeparator() +
				".fields(col(\"jdbc_tool_test_mainmaster.objectID\")," +
				"col(\"jdbc_tool_test_mainmaster.ItemCD\")," +
				"col(\"jdbc_tool_test_mainmaster.UnitPrice\"))" + System.lineSeparator() +
				".where(expMap($($(\"jdbc_tool_test_mainmaster.ItemCD = $TRIM([:jdbc_tool_test_mainmaster.ItemCD])\").AND(\"jdbc_tool_test_mainmaster.UnitPrice <> $TRIM([:jdbc_tool_test_mainmaster.UnitPrice])\")), whereParams))";

		assertEquals(whereParamsParts + "List<GRecord> result = " + selectApiStr + System.lineSeparator() + ".findList();", result.getData().get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE)));
		assertEquals(whereParamsParts + "GRecord result = " + selectApiStr + System.lineSeparator() + ".findRecord();", result.getData().get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE)));
		assertEquals(whereParamsParts + "GRecordSet result = " + selectApiStr + System.lineSeparator() + ".findRecordSet();", result.getData().get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE)));
	}
	
	
	@Test
	public void testExport_emptyColumn_inq() {

		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_emptyColumn_inq.xml");
		String conditionFilePath = getConditionFilePath("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_emptyColumn_inq.yaml");
		GExporterResult<List<GXFormCodeResult>, byte[]> result = service.export(conditionFilePath, settingsFilePath);

		assertEquals("html", result.getMediaType());
		assertEquals(2, result.getData().size());
		assertEquals(GCrudType.SELECT, result.getData().get(0).getType());
		assertEquals("仮想表ID: 4351175AD9054354B65FAC8422520F17", result.getData().get(0).getDescription());
		assertEquals("SELECT" + System.lineSeparator() +
				"    jdbc_tool_test_emptyColumn.objectID," + System.lineSeparator() +
				"    jdbc_tool_test_emptyColumn.StringColumn" + System.lineSeparator() +
				"FROM" + System.lineSeparator() +
				"    " + dbSchema + ".jdbc_tool_test_emptyColumn jdbc_tool_test_emptyColumn", result.getData().get(0).getSqlScript());
		
		GDao2VirtualEntity selectCodeResult = ((GDao2JdbcXFormCodeResult)result.getData().get(0)).getVirtualEntity();
		assertEquals(4, selectCodeResult.getColumns().size());
		assertEquals("Map<String, Object> absoluteVirtualColumn0 = new HashMap<>();" + System.lineSeparator() +
				"absoluteVirtualColumn0.put(\"name\", \"virtualColumn01\");" + System.lineSeparator() +
				"absoluteVirtualColumn0.put(\"displayName\", \"virtualColumn01\");" + System.lineSeparator() +
				"absoluteVirtualColumn0.put(\"fixedValue\", \"-\");" + System.lineSeparator() +
				"absoluteVirtualColumn0.put(\"aggregateFunction\", \"-\");" + System.lineSeparator() +
				"absoluteVirtualColumn0.put(\"format\", \"-\");" + System.lineSeparator() +
				"Map<String, Object> absoluteVirtualColumn1 = new HashMap<>();" + System.lineSeparator() +
				"absoluteVirtualColumn1.put(\"name\", \"virtualColumn02\");" + System.lineSeparator() +
				"absoluteVirtualColumn1.put(\"displayName\", \"virtualColumn02\");" + System.lineSeparator() +
				"absoluteVirtualColumn1.put(\"fixedValue\", \"9002\");" + System.lineSeparator() +
				"absoluteVirtualColumn1.put(\"aggregateFunction\", \"MAX\");" + System.lineSeparator() +
				"absoluteVirtualColumn1.put(\"format\", \"000.000\");" + System.lineSeparator(), selectCodeResult.getAbsoluteVirtualColumnCode());

		String selectApiStr = "select(\"jdbc_tool_test_emptyColumn\", \"jdbc_tool_test_emptyColumn\")" + System.lineSeparator() +
				".fields(col(\"jdbc_tool_test_emptyColumn.objectID\"),col(\"jdbc_tool_test_emptyColumn.StringColumn\"))";
		assertEquals("List<GRecord> result = " + selectApiStr + System.lineSeparator() + ".findList();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, null)));
		assertNull(result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE)));
		assertNull(result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE_PK)));
		assertNull(result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE_UK)));
		assertEquals("GRecord result = " + selectApiStr + System.lineSeparator() + ".findRecord();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, null)));
		assertNull(result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE)));
		assertNull(result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE_PK)));
		assertNull(result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE_UK)));
		assertEquals("GRecordSet result = " + selectApiStr + System.lineSeparator() + ".findRecordSet();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, null)));
		assertNull( result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE)));
		assertNull( result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE_PK)));
		assertNull( result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE_UK)));
		
		assertEquals(GCrudType.DELETE, result.getData().get(1).getType());
		assertEquals("仮想表ID: 4351175AD9054354B65FAC8422520F17", result.getData().get(1).getDescription());
		assertEquals(getDeleteSql_emptyColumn(), result.getData().get(1).getSqlScript());
		
		GDao2VirtualEntity deleteCodeResult = ((GDao2JdbcXFormCodeResult)result.getData().get(1)).getVirtualEntity();
		assertEquals(4, deleteCodeResult.getColumns().size());
		assertEquals("Map<String, Object> absoluteVirtualColumn0 = new HashMap<>();" + System.lineSeparator() +
				"absoluteVirtualColumn0.put(\"name\", \"virtualColumn01\");" + System.lineSeparator() +
				"absoluteVirtualColumn0.put(\"displayName\", \"virtualColumn01\");" + System.lineSeparator() +
				"absoluteVirtualColumn0.put(\"fixedValue\", \"-\");" + System.lineSeparator() +
				"absoluteVirtualColumn0.put(\"aggregateFunction\", \"-\");" + System.lineSeparator() +
				"absoluteVirtualColumn0.put(\"format\", \"-\");" + System.lineSeparator() +
				"Map<String, Object> absoluteVirtualColumn1 = new HashMap<>();" + System.lineSeparator() +
				"absoluteVirtualColumn1.put(\"name\", \"virtualColumn02\");" + System.lineSeparator() +
				"absoluteVirtualColumn1.put(\"displayName\", \"virtualColumn02\");" + System.lineSeparator() +
				"absoluteVirtualColumn1.put(\"fixedValue\", \"9002\");" + System.lineSeparator() +
				"absoluteVirtualColumn1.put(\"aggregateFunction\", \"MAX\");" + System.lineSeparator() +
				"absoluteVirtualColumn1.put(\"format\", \"000.000\");" + System.lineSeparator(), selectCodeResult.getAbsoluteVirtualColumnCode());
		
		String deleteApiStr = "delete(\"jdbc_tool_test_emptyColumn\")";
		assertEquals("int count = " + deleteApiStr + System.lineSeparator() + ".execute();", result.getData().get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, null)));
		assertNull(result.getData().get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE)));
		assertNull(result.getData().get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_PK)));
		assertNull(result.getData().get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_UK)));
		assertNull(result.getData().get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE)));
		assertNull(result.getData().get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_PK)));
		assertNull(result.getData().get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_UK)));
		assertNull(result.getData().get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE)));
		assertNull(result.getData().get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_PK)));
		assertNull(result.getData().get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_UK)));
	}
	
	public String getDeleteSql_emptyColumn() {
		return "DELETE FROM" + System.lineSeparator() +
				"    " + dbSchema + ".jdbc_tool_test_emptyColumn";
	}
	
	
	@Test
	public void testExport_emptyColumn_reg() {

		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_emptyColumn_reg.xml");
		String conditionFilePath = getConditionFilePath("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_emptyColumn_reg.yaml");
		GExporterResult<List<GXFormCodeResult>, byte[]> result = service.export(conditionFilePath, settingsFilePath);

		assertEquals("html", result.getMediaType());
		assertEquals(4, result.getData().size());
		assertEquals(GCrudType.INSERT, result.getData().get(0).getType());
		assertEquals("仮想表ID: A42FC45B7B66401EB1A7AE619876BDE7", result.getData().get(0).getDescription());
		assertEquals("INSERT INTO" + System.lineSeparator() +
				"    " + dbSchema + ".jdbc_tool_test_emptyColumn(objectID, StringColumn)" + System.lineSeparator() +
				"VALUES" + System.lineSeparator() +
				"(?, ?)", result.getData().get(0).getSqlScript());
		
		GDao2VirtualEntity insertCodeResult = ((GDao2JdbcXFormCodeResult)result.getData().get(0)).getVirtualEntity();
		assertEquals(5, insertCodeResult.getColumns().size());
		assertEquals("Map<String, Object> absoluteVirtualColumn0 = new HashMap<>();" + System.lineSeparator() +
				"absoluteVirtualColumn0.put(\"name\", \"virtualColumn01\");" + System.lineSeparator() +
				"absoluteVirtualColumn0.put(\"displayName\", \"virtualColumn01\");" + System.lineSeparator() +
				"absoluteVirtualColumn0.put(\"fixedValue\", \"-\");" + System.lineSeparator() +
				"absoluteVirtualColumn0.put(\"aggregateFunction\", \"-\");" + System.lineSeparator() +
				"absoluteVirtualColumn0.put(\"format\", \"-\");" + System.lineSeparator() +
				"Map<String, Object> absoluteVirtualColumn1 = new HashMap<>();" + System.lineSeparator() +
				"absoluteVirtualColumn1.put(\"name\", \"virtualColumn02\");" + System.lineSeparator() +
				"absoluteVirtualColumn1.put(\"displayName\", \"virtualColumn02\");" + System.lineSeparator() +
				"absoluteVirtualColumn1.put(\"fixedValue\", \"9002\");" + System.lineSeparator() +
				"absoluteVirtualColumn1.put(\"aggregateFunction\", \"MAX\");" + System.lineSeparator() +
				"absoluteVirtualColumn1.put(\"format\", \"000.000\");" + System.lineSeparator(), insertCodeResult.getAbsoluteVirtualColumnCode());

		String insertExecuteApiStr = "insert(\"jdbc_tool_test_emptyColumn\")" + System.lineSeparator() + ".values(record)";
		String insertRecord = "GRecord record = createRecord();" + System.lineSeparator() +
				"record.setObject(\"objectID\",);" + System.lineSeparator() +
				"record.setObject(\"StringColumn\",);" + System.lineSeparator();
		
		assertEquals(insertRecord + System.lineSeparator() + "int count = " + insertExecuteApiStr + System.lineSeparator() + ".execute();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, null)));
		assertNull(result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE)));
		assertNull(result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_PK)));
		assertNull(result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_UK)));
		
		String insertExecuteBatchApiStr = "insert(\"jdbc_tool_test_emptyColumn\")" + System.lineSeparator() + ".values(recordList)";
		StringBuilder insertRecordList = new StringBuilder();
		insertRecordList.append("List<GRecord> recordList = new ArrayList<>();").append(System.lineSeparator());
		insertRecordList.append(insertRecord);
		insertRecordList.append("recordList.add(record);").append(System.lineSeparator());
		
		assertEquals(insertRecordList + System.lineSeparator() + "int count = " + insertExecuteBatchApiStr + System.lineSeparator() + ".executeBatch();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, null)));
		assertNull(result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE)));
		assertNull(result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_PK)));
		assertNull(result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_UK)));
		assertEquals(insertRecordList + System.lineSeparator() + "int count = " + insertExecuteBatchApiStr + System.lineSeparator() + ".executeList();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, null)));
		assertNull(result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE)));
		assertNull(result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_PK)));
		assertNull(result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_UK)));
		
		
		assertEquals(GCrudType.UPDATE, result.getData().get(1).getType());
		assertEquals("仮想表ID: A42FC45B7B66401EB1A7AE619876BDE7", result.getData().get(1).getDescription());
		assertEquals("UPDATE" + System.lineSeparator() +
				"    " + dbSchema + ".jdbc_tool_test_emptyColumn jdbc_tool_test_emptyColumn" + System.lineSeparator() +
				"SET" + System.lineSeparator() +
				"    StringColumn = ?", result.getData().get(1).getSqlScript());
		
		GDao2VirtualEntity updateCodeResult = ((GDao2JdbcXFormCodeResult)result.getData().get(1)).getVirtualEntity();
		assertEquals(5, updateCodeResult.getColumns().size());
		assertEquals("Map<String, Object> absoluteVirtualColumn0 = new HashMap<>();" + System.lineSeparator() +
				"absoluteVirtualColumn0.put(\"name\", \"virtualColumn01\");" + System.lineSeparator() +
				"absoluteVirtualColumn0.put(\"displayName\", \"virtualColumn01\");" + System.lineSeparator() +
				"absoluteVirtualColumn0.put(\"fixedValue\", \"-\");" + System.lineSeparator() +
				"absoluteVirtualColumn0.put(\"aggregateFunction\", \"-\");" + System.lineSeparator() +
				"absoluteVirtualColumn0.put(\"format\", \"-\");" + System.lineSeparator() +
				"Map<String, Object> absoluteVirtualColumn1 = new HashMap<>();" + System.lineSeparator() +
				"absoluteVirtualColumn1.put(\"name\", \"virtualColumn02\");" + System.lineSeparator() +
				"absoluteVirtualColumn1.put(\"displayName\", \"virtualColumn02\");" + System.lineSeparator() +
				"absoluteVirtualColumn1.put(\"fixedValue\", \"9002\");" + System.lineSeparator() +
				"absoluteVirtualColumn1.put(\"aggregateFunction\", \"MAX\");" + System.lineSeparator() +
				"absoluteVirtualColumn1.put(\"format\", \"000.000\");" + System.lineSeparator(), updateCodeResult.getAbsoluteVirtualColumnCode());

		String updateRecord = "GRecord record = createRecord();" + System.lineSeparator() +
				"record.setObject(\"StringColumn\",);" + System.lineSeparator();
		
		StringBuilder updateRecordList = new StringBuilder();
		updateRecordList.append("List<GRecord> recordList = new ArrayList<>();").append(System.lineSeparator());
		updateRecordList.append(updateRecord);
		updateRecordList.append("recordList.add(record);").append(System.lineSeparator());
		
		String updateExecuteApiStr = "update(\"jdbc_tool_test_emptyColumn\")" + System.lineSeparator() + ".set(record)";
		assertEquals(updateRecord + System.lineSeparator() + "int count = " + updateExecuteApiStr + System.lineSeparator() + ".execute();", result.getData().get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, null)));
		assertNull(result.getData().get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE)));
		assertNull(result.getData().get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_PK)));
		assertNull(result.getData().get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_UK)));
		assertNull(result.getData().get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE)));
		assertNull(result.getData().get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_PK)));
		assertNull(result.getData().get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_UK)));
		assertNull(result.getData().get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE)));
		assertNull(result.getData().get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_PK)));
		assertNull(result.getData().get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_UK)));
		
		assertEquals(GCrudType.SELECT, result.getData().get(2).getType());
		assertEquals("仮想表ID: A42FC45B7B66401EB1A7AE619876BDE7", result.getData().get(2).getDescription());
		assertEquals("SELECT" + System.lineSeparator() +
				"    jdbc_tool_test_emptyColumn.objectID," + System.lineSeparator() +
				"    jdbc_tool_test_emptyColumn.StringColumn," + System.lineSeparator() +
				"    jdbc_tool_test_emptyColumn.IntColumn" + System.lineSeparator() +
				"FROM" + System.lineSeparator() +
				"    " + dbSchema + ".jdbc_tool_test_emptyColumn jdbc_tool_test_emptyColumn", result.getData().get(2).getSqlScript());

		GDao2VirtualEntity selectCodeResult = ((GDao2JdbcXFormCodeResult)result.getData().get(2)).getVirtualEntity();
		assertEquals(5, selectCodeResult.getColumns().size());
		assertEquals("Map<String, Object> absoluteVirtualColumn0 = new HashMap<>();" + System.lineSeparator() +
				"absoluteVirtualColumn0.put(\"name\", \"virtualColumn01\");" + System.lineSeparator() +
				"absoluteVirtualColumn0.put(\"displayName\", \"virtualColumn01\");" + System.lineSeparator() +
				"absoluteVirtualColumn0.put(\"fixedValue\", \"-\");" + System.lineSeparator() +
				"absoluteVirtualColumn0.put(\"aggregateFunction\", \"-\");" + System.lineSeparator() +
				"absoluteVirtualColumn0.put(\"format\", \"-\");" + System.lineSeparator() +
				"Map<String, Object> absoluteVirtualColumn1 = new HashMap<>();" + System.lineSeparator() +
				"absoluteVirtualColumn1.put(\"name\", \"virtualColumn02\");" + System.lineSeparator() +
				"absoluteVirtualColumn1.put(\"displayName\", \"virtualColumn02\");" + System.lineSeparator() +
				"absoluteVirtualColumn1.put(\"fixedValue\", \"9002\");" + System.lineSeparator() +
				"absoluteVirtualColumn1.put(\"aggregateFunction\", \"MAX\");" + System.lineSeparator() +
				"absoluteVirtualColumn1.put(\"format\", \"000.000\");" + System.lineSeparator(), selectCodeResult.getAbsoluteVirtualColumnCode());

		String selectApiStr = "select(\"jdbc_tool_test_emptyColumn\", \"jdbc_tool_test_emptyColumn\")" + System.lineSeparator() +
				".fields(col(\"jdbc_tool_test_emptyColumn.objectID\"),col(\"jdbc_tool_test_emptyColumn.StringColumn\"),col(\"jdbc_tool_test_emptyColumn.IntColumn\"))";
		assertEquals("List<GRecord> result = " + selectApiStr + System.lineSeparator() + ".findList();", result.getData().get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, null)));
		assertNull(result.getData().get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE)));
		assertNull(result.getData().get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE_PK)));
		assertNull(result.getData().get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE_UK)));
		assertEquals("GRecord result = " + selectApiStr + System.lineSeparator() + ".findRecord();", result.getData().get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, null)));
		assertNull(result.getData().get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE)));
		assertNull(result.getData().get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE_PK)));
		assertNull(result.getData().get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE_UK)));
		assertEquals("GRecordSet result = " + selectApiStr + System.lineSeparator() + ".findRecordSet();", result.getData().get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, null)));
		assertNull(result.getData().get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE)));
		assertNull(result.getData().get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE_PK)));
		assertNull(result.getData().get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE_UK)));
		
		assertEquals(GCrudType.DELETE, result.getData().get(3).getType());
		assertEquals("仮想表ID: A42FC45B7B66401EB1A7AE619876BDE7", result.getData().get(3).getDescription());
		assertEquals(getDeleteSql_emptyColumn(), result.getData().get(3).getSqlScript());
		
		GDao2VirtualEntity deleteCodeResult = ((GDao2JdbcXFormCodeResult)result.getData().get(3)).getVirtualEntity();
		assertEquals(5, deleteCodeResult.getColumns().size());
		assertEquals("Map<String, Object> absoluteVirtualColumn0 = new HashMap<>();" + System.lineSeparator() +
				"absoluteVirtualColumn0.put(\"name\", \"virtualColumn01\");" + System.lineSeparator() +
				"absoluteVirtualColumn0.put(\"displayName\", \"virtualColumn01\");" + System.lineSeparator() +
				"absoluteVirtualColumn0.put(\"fixedValue\", \"-\");" + System.lineSeparator() +
				"absoluteVirtualColumn0.put(\"aggregateFunction\", \"-\");" + System.lineSeparator() +
				"absoluteVirtualColumn0.put(\"format\", \"-\");" + System.lineSeparator() +
				"Map<String, Object> absoluteVirtualColumn1 = new HashMap<>();" + System.lineSeparator() +
				"absoluteVirtualColumn1.put(\"name\", \"virtualColumn02\");" + System.lineSeparator() +
				"absoluteVirtualColumn1.put(\"displayName\", \"virtualColumn02\");" + System.lineSeparator() +
				"absoluteVirtualColumn1.put(\"fixedValue\", \"9002\");" + System.lineSeparator() +
				"absoluteVirtualColumn1.put(\"aggregateFunction\", \"MAX\");" + System.lineSeparator() +
				"absoluteVirtualColumn1.put(\"format\", \"000.000\");" + System.lineSeparator(), selectCodeResult.getAbsoluteVirtualColumnCode());
		
		String deleteApiStr = "delete(\"jdbc_tool_test_emptyColumn\")";
		assertEquals("int count = " + deleteApiStr + System.lineSeparator() + ".execute();", result.getData().get(3).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, null)));
		assertNull(result.getData().get(3).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE)));
		assertNull(result.getData().get(3).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_PK)));
		assertNull(result.getData().get(3).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_UK)));
		assertNull(result.getData().get(3).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE)));
		assertNull(result.getData().get(3).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_PK)));
		assertNull(result.getData().get(3).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_UK)));
		assertNull(result.getData().get(3).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE)));
		assertNull(result.getData().get(3).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_PK)));
		assertNull(result.getData().get(3).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_UK)));
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
		updateRecordList.append("List<GRecord> recordList = new ArrayList<>();").append(System.lineSeparator());
		updateRecordList.append(updateRecord);
		updateRecordList.append("recordList.add(record);").append(System.lineSeparator());
		
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
				
		assertEquals(updateRecordList + System.lineSeparator() + "int count = " + updateExecuteBatchApiStr + System.lineSeparator() + ".wherePK()" + System.lineSeparator() + ".executeBatch();", result.getData().get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_PK)));
		assertEquals(updateRecordList + System.lineSeparator() + "int count = " + updateExecuteBatchApiStr + System.lineSeparator() + ".whereUK(\"MultipleColumnUK\")" + System.lineSeparator() + "//.whereUK(\"OtherUKName\")" + System.lineSeparator() + "//.whereUK(\"SingleColumnUK\")" + System.lineSeparator() + ".executeBatch();", result.getData().get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_UK)));
		assertEquals(updateRecordList + System.lineSeparator() + "int count = " + updateExecuteBatchApiStr + System.lineSeparator() + ".wherePK()" + System.lineSeparator() + ".executeList();", result.getData().get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_PK)));
		assertEquals(updateRecordList + System.lineSeparator() + "int count = " + updateExecuteBatchApiStr + System.lineSeparator() + ".whereUK(\"MultipleColumnUK\")" + System.lineSeparator() + "//.whereUK(\"OtherUKName\")" + System.lineSeparator() + "//.whereUK(\"SingleColumnUK\")" + System.lineSeparator() + ".executeList();", result.getData().get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_UK)));
		
		assertEquals(GCrudType.SELECT, result.getData().get(2).getType());
		assertEquals("仮想表ID: 35B6AB911C524EE69A30E29BF9A2904A", result.getData().get(2).getDescription());
		assertEquals("SELECT" + System.lineSeparator() +
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
				"    INNER JOIN " + dbSchema + ".jdbc_tool_test_fullCaseInnerJoinEntity jdbc_tool_test_fullCaseInnerJoinEntity ON jdbc_tool_test_fullCaseMainEntity.StringColumn = jdbc_tool_test_fullCaseInnerJoinEntity.StringColumnInner" + System.lineSeparator() +
				"    AND jdbc_tool_test_fullCaseInnerJoinEntity.NStringColumnInner = '9001'" + System.lineSeparator() +
				"    RIGHT OUTER JOIN " + dbSchema + ".jdbc_tool_test_fullCaseRightJoinEntity jdbc_tool_test_fullCaseRightJoinEntity ON jdbc_tool_test_fullCaseMainEntity.NStringColumn = jdbc_tool_test_fullCaseRightJoinEntity.NStringColumnRight" + System.lineSeparator() +
				"    AND jdbc_tool_test_fullCaseRightJoinEntity.StringColumnRight = '7001'" + System.lineSeparator() +
				"    LEFT OUTER JOIN " + dbSchema + ".jdbc_tool_test_fullCaseLeftJoinEntity jdbc_tool_test_fullCaseLeftJoinEntity ON jdbc_tool_test_fullCaseMainEntity.IntColumn = jdbc_tool_test_fullCaseLeftJoinEntity.IntColumnLeft" + System.lineSeparator() +
				"    AND jdbc_tool_test_fullCaseLeftJoinEntity.LongColumnLeft = 8001" + System.lineSeparator() +
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
				"    MIN(jdbc_tool_test_fullCaseMainEntity.NStringColumn) DESC", result.getData().get(2).getSqlScript());
		
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
				".innerJoin(\"jdbc_tool_test_fullCaseInnerJoinEntity\", \"jdbc_tool_test_fullCaseInnerJoinEntity\", expMap($(\"jdbc_tool_test_fullCaseMainEntity.StringColumn = jdbc_tool_test_fullCaseInnerJoinEntity.StringColumnInner\").AND(\"jdbc_tool_test_fullCaseInnerJoinEntity.NStringColumnInner = '9001'\"), Collections.emptyMap()))" + System.lineSeparator() +
				".rightOuterJoin(\"jdbc_tool_test_fullCaseRightJoinEntity\", \"jdbc_tool_test_fullCaseRightJoinEntity\", expMap($(\"jdbc_tool_test_fullCaseMainEntity.NStringColumn = jdbc_tool_test_fullCaseRightJoinEntity.NStringColumnRight\").AND(\"jdbc_tool_test_fullCaseRightJoinEntity.StringColumnRight = '7001'\"), Collections.emptyMap()))" + System.lineSeparator() +
				".leftOuterJoin(\"jdbc_tool_test_fullCaseLeftJoinEntity\", \"jdbc_tool_test_fullCaseLeftJoinEntity\", expMap($(\"jdbc_tool_test_fullCaseMainEntity.IntColumn = jdbc_tool_test_fullCaseLeftJoinEntity.IntColumnLeft\").AND(\"jdbc_tool_test_fullCaseLeftJoinEntity.LongColumnLeft = 8001\"), Collections.emptyMap()))" + System.lineSeparator() +
				".orderBy(asc(\"jdbc_tool_test_fullCaseMainEntity.StringColumn\"),desc(\"jdbc_tool_test_fullCaseMainEntity.NStringColumn\"))";
		
		String selectWhereParts = ".where(expMap($($(\"jdbc_tool_test_fullCaseMainEntity.StringColumn = '9001'\").AND(\"jdbc_tool_test_fullCaseMainEntity.IntColumn = 8001\")).AND($(\"jdbc_tool_test_fullCaseMainEntity.NStringColumn = :jdbc_tool_test_fullCaseMainEntity.NStringColumn\").AND(\"jdbc_tool_test_fullCaseMainEntity.DateTimeColumn = :jdbc_tool_test_fullCaseMainEntity.DateTimeColumn\").AND(\"jdbc_tool_test_fullCaseMainEntity.CompanyCD = $TRIM(:jdbc_tool_test_fullCaseMainEntity.CompanyCD)\")), whereParams))";
		
		assertEquals(whereParamsParts + "List<GRecord> result = " + selectApiStr + System.lineSeparator() + selectWhereParts + System.lineSeparator() + ".findList();", result.getData().get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE)));
		assertEquals(whereParamsPartsPKUK + "List<GRecord> result = " + selectApiStr + System.lineSeparator() + ".wherePK(whereParams)" + System.lineSeparator() + ".findList();", result.getData().get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE_PK)));
		assertEquals(whereParamsPartsPKUK + "List<GRecord> result = " + selectApiStr + System.lineSeparator() + ".whereUK(\"MultipleColumnUK\", whereParams)" + System.lineSeparator() + "//.whereUK(\"OtherUKName\", whereParams)" + System.lineSeparator() + "//.whereUK(\"SingleColumnUK\", whereParams)" + System.lineSeparator() + ".findList();", result.getData().get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE_UK)));
		assertEquals(whereParamsParts + "GRecord result = " + selectApiStr + System.lineSeparator() + selectWhereParts + System.lineSeparator() + ".findRecord();", result.getData().get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE)));
		assertEquals(whereParamsPartsPKUK + "GRecord result = " + selectApiStr + System.lineSeparator() + ".wherePK(whereParams)" + System.lineSeparator() + ".findRecord();", result.getData().get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE_PK)));
		assertEquals(whereParamsPartsPKUK + "GRecord result = " + selectApiStr + System.lineSeparator() + ".whereUK(\"MultipleColumnUK\", whereParams)" + System.lineSeparator() + "//.whereUK(\"OtherUKName\", whereParams)" + System.lineSeparator() + "//.whereUK(\"SingleColumnUK\", whereParams)" + System.lineSeparator() + ".findRecord();", result.getData().get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE_UK)));
		assertEquals(whereParamsParts + "GRecordSet result = " + selectApiStr + System.lineSeparator() + selectWhereParts + System.lineSeparator() + ".findRecordSet();", result.getData().get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE)));
		assertEquals(whereParamsPartsPKUK + "GRecordSet result = " + selectApiStr + System.lineSeparator() + ".wherePK(whereParams)" + System.lineSeparator() + ".findRecordSet();", result.getData().get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE_PK)));
		assertEquals(whereParamsPartsPKUK + "GRecordSet result = " + selectApiStr + System.lineSeparator() + ".whereUK(\"MultipleColumnUK\", whereParams)" + System.lineSeparator() + "//.whereUK(\"OtherUKName\", whereParams)" + System.lineSeparator() + "//.whereUK(\"SingleColumnUK\", whereParams)" + System.lineSeparator() + ".findRecordSet();", result.getData().get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE_UK)));
		
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
		
		assertEquals(whereParamsParts + "int count = " + deleteApiStr + System.lineSeparator() + deleteWhereParts + System.lineSeparator() + ".execute();", result.getData().get(3).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE)));
		
		String deleteExecutePKWhereParts = "GRecord whereParams = createRecord();" + System.lineSeparator() +
				"whereParams.setObject(\"objectID\",);" + System.lineSeparator();
		
		
		String deleteExecuteListPKWhereParts = deleteExecutePKWhereParts +
				"List<GRecord> whereParamsList = new ArrayList<>();" + System.lineSeparator() +
				"whereParamsList.add(whereParams);" + System.lineSeparator();
		
		String deleteExecuteUKWhereParts = "GRecord whereParams = createRecord();" + System.lineSeparator() +
				"whereParams.setObject(\"StringColumn\",);" + System.lineSeparator() +
				"whereParams.setObject(\"IntColumn\",);"  + System.lineSeparator() +
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
		
		assertEquals(deleteExecutePKWhereParts + System.lineSeparator() + "int count = " + deleteApiStr + System.lineSeparator() + ".wherePK(whereParams)" + System.lineSeparator() + ".execute();", result.getData().get(3).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_PK)));
		assertEquals(deleteExecuteUKWhereParts + System.lineSeparator() + "int count = " + deleteApiStr + System.lineSeparator() + ".whereUK(\"MultipleColumnUK\", whereParams)" + System.lineSeparator() + "//.whereUK(\"OtherUKName\", whereParams)" + System.lineSeparator() + "//.whereUK(\"SingleColumnUK\", whereParams)" + System.lineSeparator() + ".execute();", result.getData().get(3).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_UK)));
		
		assertEquals(deleteExecuteListPKWhereParts + System.lineSeparator() + "int count = " + deleteApiStr + System.lineSeparator() + ".wherePK(whereParamsList)" + System.lineSeparator() + ".executeList();", result.getData().get(3).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_PK)));
		assertEquals(deleteExecuteListUKWhereParts + System.lineSeparator() + "int count = " + deleteApiStr + System.lineSeparator() + ".whereUK(\"MultipleColumnUK\", whereParamsList)" + System.lineSeparator() + "//.whereUK(\"OtherUKName\", whereParamsList)" + System.lineSeparator() + "//.whereUK(\"SingleColumnUK\", whereParamsList)" + System.lineSeparator() + ".executeList();", result.getData().get(3).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_UK)));
		
		assertEquals(deleteExecuteListPKWhereParts + System.lineSeparator() + "int count = " + deleteApiStr + System.lineSeparator() + ".wherePK(whereParamsList)" + System.lineSeparator() + ".executeBatch();", result.getData().get(3).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_PK)));
		assertEquals(deleteExecuteListUKWhereParts + System.lineSeparator() + "int count = " + deleteApiStr + System.lineSeparator() + ".whereUK(\"MultipleColumnUK\", whereParamsList)" + System.lineSeparator() + "//.whereUK(\"OtherUKName\", whereParamsList)" + System.lineSeparator() + "//.whereUK(\"SingleColumnUK\", whereParamsList)" + System.lineSeparator() + ".executeBatch();", result.getData().get(3).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_UK)));
	}
	
	public String getDeleteSql_fullCase() {
		return "DELETE FROM" + System.lineSeparator() +
				"    " + dbSchema + ".jdbc_tool_test_fullCaseMainEntity" + System.lineSeparator() +
				"WHERE" + System.lineSeparator() +
				"    (" + System.lineSeparator() +
				"        " + dbSchema + ".jdbc_tool_test_fullCaseMainEntity.StringColumn = '9001'" + System.lineSeparator() +
				"        AND " + dbSchema + ".jdbc_tool_test_fullCaseMainEntity.IntColumn = 8001" + System.lineSeparator() +
				"    )" + System.lineSeparator() +
				"    AND (" + System.lineSeparator() +
				"        " + dbSchema + ".jdbc_tool_test_fullCaseMainEntity.NStringColumn = ?" + System.lineSeparator() +
				"        AND " + dbSchema + ".jdbc_tool_test_fullCaseMainEntity.DateTimeColumn = ?" + System.lineSeparator() +
				"        AND " + dbSchema + ".jdbc_tool_test_fullCaseMainEntity.CompanyCD = ?" + System.lineSeparator() +
				"    )";
	}
	
	@Test
	public void testExport_multipleUK_inq() {

		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_multipleUK_inq.xml");
		String conditionFilePath = getConditionFilePath("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_multipleUK_inq.yaml");
		GExporterResult<List<GXFormCodeResult>, byte[]> result = service.export(conditionFilePath, settingsFilePath);

		assertEquals("html", result.getMediaType());
		assertEquals(2, result.getData().size());
		assertEquals(GCrudType.SELECT, result.getData().get(0).getType());
		assertEquals("仮想表ID: 5936D594156641C5B5E5169879CE2888", result.getData().get(0).getDescription());
		assertEquals("SELECT" + System.lineSeparator() +
				"    jdbc_tool_test_multipleUK.objectID," + System.lineSeparator() +
				"    jdbc_tool_test_multipleUK.StringColumn," + System.lineSeparator() +
				"    jdbc_tool_test_multipleUK.NStringColumn," + System.lineSeparator() +
				"    jdbc_tool_test_multipleUK.IntColumn" + System.lineSeparator() +
				"FROM" + System.lineSeparator() +
				"    " + dbSchema + ".jdbc_tool_test_multipleUK jdbc_tool_test_multipleUK" + System.lineSeparator() +
				"WHERE" + System.lineSeparator() +
				"    (" + System.lineSeparator() +
				"        jdbc_tool_test_multipleUK.objectID = ?" + System.lineSeparator() +
				"        AND jdbc_tool_test_multipleUK.NStringColumn = ?" + System.lineSeparator() +
				"    )", result.getData().get(0).getSqlScript());
		
		GDao2VirtualEntity selectCodeResult = ((GDao2JdbcXFormCodeResult)result.getData().get(0)).getVirtualEntity();
		assertEquals(4, selectCodeResult.getColumns().size());
		assertEquals("-", selectCodeResult.getAbsoluteVirtualColumnCode());

		String whereParamsParts = "Map<String, Object> whereParams = new HashMap<>();" + System.lineSeparator() +
				"whereParams.put(\"jdbc_tool_test_multipleUK.objectID\",);" + System.lineSeparator() +
				"whereParams.put(\"jdbc_tool_test_multipleUK.NStringColumn\",);" + System.lineSeparator();
		String whereParamsPartsUKPK = "Object[] whereParams = new Object[]{,};" + System.lineSeparator();
		String selectApiStr = "select(\"jdbc_tool_test_multipleUK\", \"jdbc_tool_test_multipleUK\")" + System.lineSeparator() +
				".fields(colsAll())" + System.lineSeparator();
		String selectWhereParts = ".where(expMap($($(\"jdbc_tool_test_multipleUK.objectID = :jdbc_tool_test_multipleUK.objectID\").AND(\"jdbc_tool_test_multipleUK.NStringColumn = :jdbc_tool_test_multipleUK.NStringColumn\")), whereParams))" + System.lineSeparator();
		
		assertEquals(whereParamsParts + "List<GRecord> result = " + selectApiStr + selectWhereParts + ".findList();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE)));
		assertEquals(whereParamsPartsUKPK + "List<GRecord> result = " + selectApiStr + ".wherePK(whereParams)" + System.lineSeparator() + ".findList();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE_PK)));
		assertEquals(whereParamsPartsUKPK + "List<GRecord> result = " + selectApiStr + ".whereUK(\"unionkey01\", whereParams)" + System.lineSeparator() + "//.whereUK(\"unionkey02\", whereParams)" + System.lineSeparator() + "//.whereUK(\"unionkey03\", whereParams)" + System.lineSeparator() + ".findList();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE_UK)));
		assertEquals(whereParamsParts + "GRecord result = " + selectApiStr + selectWhereParts + ".findRecord();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE)));
		assertEquals(whereParamsPartsUKPK + "GRecord result = " + selectApiStr + ".wherePK(whereParams)" + System.lineSeparator() + ".findRecord();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE_PK)));
		assertEquals(whereParamsPartsUKPK + "GRecord result = " + selectApiStr + ".whereUK(\"unionkey01\", whereParams)" + System.lineSeparator() + "//.whereUK(\"unionkey02\", whereParams)" + System.lineSeparator() + "//.whereUK(\"unionkey03\", whereParams)" + System.lineSeparator() + ".findRecord();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE_UK)));
		assertEquals(whereParamsParts + "GRecordSet result = " + selectApiStr + selectWhereParts + ".findRecordSet();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE)));
		assertEquals(whereParamsPartsUKPK + "GRecordSet result = " + selectApiStr + ".wherePK(whereParams)" + System.lineSeparator() + ".findRecordSet();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE_PK)));
		assertEquals(whereParamsPartsUKPK + "GRecordSet result = " + selectApiStr + ".whereUK(\"unionkey01\", whereParams)" + System.lineSeparator() + "//.whereUK(\"unionkey02\", whereParams)" + System.lineSeparator() + "//.whereUK(\"unionkey03\", whereParams)" + System.lineSeparator() + ".findRecordSet();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE_UK)));
		
		assertEquals(GCrudType.DELETE, result.getData().get(1).getType());
		assertEquals("仮想表ID: 5936D594156641C5B5E5169879CE2888", result.getData().get(1).getDescription());
		assertEquals(getDeleteSql_multipleUK(), result.getData().get(1).getSqlScript());
		
		GDao2VirtualEntity deleteCodeResult = ((GDao2JdbcXFormCodeResult)result.getData().get(1)).getVirtualEntity();
		assertEquals(4, deleteCodeResult.getColumns().size());
		assertEquals("-", selectCodeResult.getAbsoluteVirtualColumnCode());
		
		String deleteApiStr = "delete(\"jdbc_tool_test_multipleUK\")" + System.lineSeparator();
		String deleteWhereParts = ".where(expMap($($(\"jdbc_tool_test_multipleUK.objectID = :jdbc_tool_test_multipleUK.objectID\").AND(\"jdbc_tool_test_multipleUK.NStringColumn = :jdbc_tool_test_multipleUK.NStringColumn\")), whereParams))" + System.lineSeparator();
		
		assertEquals(whereParamsParts + "int count = " + deleteApiStr + deleteWhereParts + ".execute();", result.getData().get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE)));
		
		String deleteExecutePKWhereParts = "GRecord whereParams = createRecord();" + System.lineSeparator() +
				"whereParams.setObject(\"objectID\",);" + System.lineSeparator();
		
		
		String deleteExecuteListPKWhereParts = deleteExecutePKWhereParts +
				"List<GRecord> whereParamsList = new ArrayList<>();" + System.lineSeparator() +
				"whereParamsList.add(whereParams);" + System.lineSeparator();
		
		String deleteExecuteUKWhereParts = "GRecord whereParams = createRecord();" + System.lineSeparator() +
				"whereParams.setObject(\"objectID\",);" + System.lineSeparator() +
				"whereParams.setObject(\"StringColumn\",);" + System.lineSeparator() +
				"//ユーニックキー「unionkey02」用のカラム" + System.lineSeparator() +
				"/*whereParams.setObject(\"objectID\",);" + System.lineSeparator() +
				"whereParams.setObject(\"NStringColumn\",);" + System.lineSeparator() +
				"*/" + System.lineSeparator()+ "//ユーニックキー「unionkey03」用のカラム" + System.lineSeparator() +
				"/*whereParams.setObject(\"StringColumn\",);" + System.lineSeparator() +
				"whereParams.setObject(\"NStringColumn\",);" + System.lineSeparator() +
				"*/" + System.lineSeparator();
		String deleteExecuteListUKWhereParts = deleteExecuteUKWhereParts +
				"List<GRecord> whereParamsList = new ArrayList<>();" + System.lineSeparator() +
				"whereParamsList.add(whereParams);" + System.lineSeparator();
		
		assertEquals(deleteExecutePKWhereParts + System.lineSeparator() + "int count = " + deleteApiStr + ".wherePK(whereParams)" + System.lineSeparator() + ".execute();", result.getData().get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_PK)));
		assertEquals(deleteExecuteUKWhereParts + System.lineSeparator() + "int count = " + deleteApiStr + ".whereUK(\"unionkey01\", whereParams)" + System.lineSeparator() + "//.whereUK(\"unionkey02\", whereParams)" + System.lineSeparator() + "//.whereUK(\"unionkey03\", whereParams)" + System.lineSeparator() + ".execute();", result.getData().get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_UK)));
		
		assertEquals(deleteExecuteListPKWhereParts + System.lineSeparator() + "int count = " + deleteApiStr + ".wherePK(whereParamsList)" + System.lineSeparator() + ".executeList();", result.getData().get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_PK)));
		assertEquals(deleteExecuteListUKWhereParts + System.lineSeparator() + "int count = " + deleteApiStr + ".whereUK(\"unionkey01\", whereParamsList)" + System.lineSeparator() + "//.whereUK(\"unionkey02\", whereParamsList)" + System.lineSeparator() + "//.whereUK(\"unionkey03\", whereParamsList)" + System.lineSeparator() + ".executeList();", result.getData().get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_UK)));
		
		assertEquals(deleteExecuteListPKWhereParts + System.lineSeparator() + "int count = " + deleteApiStr + ".wherePK(whereParamsList)" + System.lineSeparator() + ".executeBatch();", result.getData().get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_PK)));
		assertEquals(deleteExecuteListUKWhereParts + System.lineSeparator() + "int count = " + deleteApiStr + ".whereUK(\"unionkey01\", whereParamsList)" + System.lineSeparator() + "//.whereUK(\"unionkey02\", whereParamsList)" + System.lineSeparator() + "//.whereUK(\"unionkey03\", whereParamsList)" + System.lineSeparator() + ".executeBatch();", result.getData().get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_UK)));
	}
	
	public String getDeleteSql_multipleUK() {
		return "DELETE FROM" + System.lineSeparator() +
				"    " + dbSchema + ".jdbc_tool_test_multipleUK" + System.lineSeparator() +
				"WHERE" + System.lineSeparator() +
				"    (" + System.lineSeparator() +
				"        " + dbSchema + ".jdbc_tool_test_multipleUK.objectID = ?" + System.lineSeparator() +
				"        AND " + dbSchema + ".jdbc_tool_test_multipleUK.NStringColumn = ?" + System.lineSeparator() +
				"    )";
	}
	
	@Test
	public void testExport_multipleUK_reg() {

		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_multipleUK_reg.xml");
		String conditionFilePath = getConditionFilePath("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_multipleUK_reg.yaml");
		GExporterResult<List<GXFormCodeResult>, byte[]> result = service.export(conditionFilePath, settingsFilePath);

		assertEquals("html", result.getMediaType());
		assertEquals(4, result.getData().size());
		assertEquals(GCrudType.INSERT, result.getData().get(0).getType());
		assertEquals("仮想表ID: 32F9873E7C744002A8DBD49D87007442", result.getData().get(0).getDescription());
		assertEquals("INSERT INTO" + System.lineSeparator() +
				"    " + dbSchema + ".jdbc_tool_test_multipleUK(objectID, StringColumn, NStringColumn, IntColumn)" + System.lineSeparator() +
				"VALUES" + System.lineSeparator() +
				"(?, ?, ?, ?)", result.getData().get(0).getSqlScript());
		
		GDao2VirtualEntity insertCodeResult = ((GDao2JdbcXFormCodeResult)result.getData().get(0)).getVirtualEntity();
		assertEquals(4, insertCodeResult.getColumns().size());
		assertEquals("-", insertCodeResult.getAbsoluteVirtualColumnCode());

		String insertRecord = "GRecord record = createRecord();" + System.lineSeparator() +
				"record.setObject(\"objectID\",);" + System.lineSeparator() +
				"record.setObject(\"StringColumn\",);" + System.lineSeparator() +
				"record.setObject(\"NStringColumn\",);" + System.lineSeparator() +
				"record.setObject(\"IntColumn\",);" + System.lineSeparator();
		String insertExecuteApiStr = "insert(\"jdbc_tool_test_multipleUK\")" + System.lineSeparator() + ".values(record)";
		
		assertEquals(insertRecord + System.lineSeparator() + "int count = " + insertExecuteApiStr + System.lineSeparator() + ".execute();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, null)));
		assertNull(result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE)));
		assertNull(result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_PK)));
		assertNull(result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_UK)));
		
		String insertExecuteBatchApiStr = "insert(\"jdbc_tool_test_multipleUK\")" + System.lineSeparator() + ".values(recordList)";
		StringBuilder insertRecordList = new StringBuilder();
		insertRecordList.append("List<GRecord> recordList = new ArrayList<>();").append(System.lineSeparator());
		insertRecordList.append(insertRecord);
		insertRecordList.append("recordList.add(record);").append(System.lineSeparator());
		
		assertEquals(insertRecordList + System.lineSeparator() + "int count = " + insertExecuteBatchApiStr + System.lineSeparator() + ".executeBatch();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, null)));
		assertNull(result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE)));
		assertNull(result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_PK)));
		assertNull(result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_UK)));
		assertEquals(insertRecordList + System.lineSeparator() + "int count = " + insertExecuteBatchApiStr + System.lineSeparator() + ".executeList();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, null)));
		assertNull(result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE)));
		assertNull(result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_PK)));
		assertNull(result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_UK)));
		
		
		assertEquals(GCrudType.UPDATE, result.getData().get(1).getType());
		assertEquals("仮想表ID: 32F9873E7C744002A8DBD49D87007442", result.getData().get(1).getDescription());
		assertEquals("UPDATE" + System.lineSeparator() +
				"    " + dbSchema + ".jdbc_tool_test_multipleUK jdbc_tool_test_multipleUK" + System.lineSeparator() +
				"SET" + System.lineSeparator() +
				"    StringColumn = ?," + System.lineSeparator() +
				"    NStringColumn = ?," + System.lineSeparator() +
				"    IntColumn = ?" + System.lineSeparator() +
				"WHERE" + System.lineSeparator() +
				"    (" + System.lineSeparator() +
				"        jdbc_tool_test_multipleUK.objectID = ?" + System.lineSeparator() +
				"        AND jdbc_tool_test_multipleUK.NStringColumn = ?" + System.lineSeparator() +
				"    )", result.getData().get(1).getSqlScript());
		
		GDao2VirtualEntity updateCodeResult = ((GDao2JdbcXFormCodeResult)result.getData().get(1)).getVirtualEntity();
		assertEquals(4, updateCodeResult.getColumns().size());
		assertEquals("-", updateCodeResult.getAbsoluteVirtualColumnCode());

		String whereParamsParts = "Map<String, Object> whereParams = new HashMap<>();" + System.lineSeparator() +
				"whereParams.put(\"jdbc_tool_test_multipleUK.objectID\",);" + System.lineSeparator() +
				"whereParams.put(\"jdbc_tool_test_multipleUK.NStringColumn\",);" + System.lineSeparator();
		String whereParamsPartsUKPK = "Object[] whereParams = new Object[]{,};" + System.lineSeparator();
		String updateRecord = "GRecord record = createRecord();" + System.lineSeparator() +
				"record.setObject(\"StringColumn\",);" + System.lineSeparator() +
				"record.setObject(\"NStringColumn\",);" + System.lineSeparator() +
				"record.setObject(\"IntColumn\",);" + System.lineSeparator();
		String updateExecuteApiStr = "update(\"jdbc_tool_test_multipleUK\")" + System.lineSeparator() + ".set(record)" + System.lineSeparator();
		String updateExecuteApiWhereStr = ".where(expMap($($(\"jdbc_tool_test_multipleUK.objectID = :jdbc_tool_test_multipleUK.objectID\").AND(\"jdbc_tool_test_multipleUK.NStringColumn = :jdbc_tool_test_multipleUK.NStringColumn\")), whereParams))" + System.lineSeparator();
		assertEquals(whereParamsParts + updateRecord + System.lineSeparator() + "int count = " + updateExecuteApiStr + updateExecuteApiWhereStr + ".execute();", result.getData().get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE)));
		assertEquals(updateRecord + System.lineSeparator() + "int count = " + updateExecuteApiStr + ".wherePK()" + System.lineSeparator() + ".execute();", result.getData().get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_PK)));
		assertEquals(updateRecord + System.lineSeparator() + "int count = " + updateExecuteApiStr + ".whereUK(\"unionkey01\")" + System.lineSeparator() + "//.whereUK(\"unionkey02\")" + System.lineSeparator() + "//.whereUK(\"unionkey03\")" + System.lineSeparator() + ".execute();", result.getData().get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_UK)));
		
		StringBuilder updateRecordList = new StringBuilder();
		updateRecordList.append("List<GRecord> recordList = new ArrayList<>();").append(System.lineSeparator());
		updateRecordList.append(updateRecord);
		updateRecordList.append("recordList.add(record);").append(System.lineSeparator());
		
		String updateExecuteListApiStr = "update(\"jdbc_tool_test_multipleUK\")" + System.lineSeparator() + ".set(recordList)" + System.lineSeparator();
		
		assertEquals(updateRecordList + System.lineSeparator() + "int count = " + updateExecuteListApiStr + ".wherePK()" + System.lineSeparator() + ".executeList();", result.getData().get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_PK)));
		assertEquals(updateRecordList + System.lineSeparator() + "int count = " + updateExecuteListApiStr + ".whereUK(\"unionkey01\")" + System.lineSeparator() + "//.whereUK(\"unionkey02\")" + System.lineSeparator() + "//.whereUK(\"unionkey03\")" + System.lineSeparator() + ".executeList();", result.getData().get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_UK)));
		assertEquals(updateRecordList + System.lineSeparator() + "int count = " + updateExecuteListApiStr + ".wherePK()" + System.lineSeparator() + ".executeBatch();", result.getData().get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_PK)));
		assertEquals(updateRecordList + System.lineSeparator() + "int count = " + updateExecuteListApiStr + ".whereUK(\"unionkey01\")" + System.lineSeparator() + "//.whereUK(\"unionkey02\")" + System.lineSeparator() + "//.whereUK(\"unionkey03\")" + System.lineSeparator() + ".executeBatch();", result.getData().get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_UK)));
		
		assertEquals(GCrudType.SELECT, result.getData().get(2).getType());
		assertEquals("仮想表ID: 32F9873E7C744002A8DBD49D87007442", result.getData().get(2).getDescription());
		assertEquals("SELECT" + System.lineSeparator() +
				"    jdbc_tool_test_multipleUK.objectID," + System.lineSeparator() +
				"    jdbc_tool_test_multipleUK.StringColumn," + System.lineSeparator() +
				"    jdbc_tool_test_multipleUK.NStringColumn," + System.lineSeparator() +
				"    jdbc_tool_test_multipleUK.IntColumn" + System.lineSeparator() +
				"FROM" + System.lineSeparator() +
				"    " + dbSchema + ".jdbc_tool_test_multipleUK jdbc_tool_test_multipleUK" + System.lineSeparator() +
				"WHERE" + System.lineSeparator() +
				"    (" + System.lineSeparator() +
				"        jdbc_tool_test_multipleUK.objectID = ?" + System.lineSeparator() +
				"        AND jdbc_tool_test_multipleUK.NStringColumn = ?" + System.lineSeparator() +
				"    )", result.getData().get(2).getSqlScript());
		
		GDao2VirtualEntity selectCodeResult = ((GDao2JdbcXFormCodeResult)result.getData().get(2)).getVirtualEntity();
		assertEquals(4, selectCodeResult.getColumns().size());
		assertEquals("-", selectCodeResult.getAbsoluteVirtualColumnCode());

		String selectApiStr = "select(\"jdbc_tool_test_multipleUK\", \"jdbc_tool_test_multipleUK\")" + System.lineSeparator() +
				".fields(colsAll())" + System.lineSeparator();
		String selectApiWhereStr = ".where(expMap($($(\"jdbc_tool_test_multipleUK.objectID = :jdbc_tool_test_multipleUK.objectID\").AND(\"jdbc_tool_test_multipleUK.NStringColumn = :jdbc_tool_test_multipleUK.NStringColumn\")), whereParams))" + System.lineSeparator();
		assertEquals(whereParamsParts + "List<GRecord> result = " + selectApiStr + selectApiWhereStr + ".findList();", result.getData().get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE)));
		assertEquals(whereParamsPartsUKPK + "List<GRecord> result = " + selectApiStr + ".wherePK(whereParams)" + System.lineSeparator() + ".findList();", result.getData().get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE_PK)));
		assertEquals(whereParamsPartsUKPK + "List<GRecord> result = " + selectApiStr + ".whereUK(\"unionkey01\", whereParams)" + System.lineSeparator() + "//.whereUK(\"unionkey02\", whereParams)" + System.lineSeparator() + "//.whereUK(\"unionkey03\", whereParams)" + System.lineSeparator() + ".findList();", result.getData().get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE_UK)));
		
		assertEquals(whereParamsParts + "GRecord result = " + selectApiStr + selectApiWhereStr + ".findRecord();", result.getData().get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE)));
		assertEquals(whereParamsPartsUKPK + "GRecord result = " + selectApiStr + ".wherePK(whereParams)" + System.lineSeparator() + ".findRecord();", result.getData().get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE_PK)));
		assertEquals(whereParamsPartsUKPK + "GRecord result = " + selectApiStr + ".whereUK(\"unionkey01\", whereParams)" + System.lineSeparator() + "//.whereUK(\"unionkey02\", whereParams)" + System.lineSeparator() + "//.whereUK(\"unionkey03\", whereParams)" + System.lineSeparator() + ".findRecord();", result.getData().get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE_UK)));
		
		assertEquals(whereParamsParts + "GRecordSet result = " + selectApiStr + selectApiWhereStr + ".findRecordSet();", result.getData().get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE)));
		assertEquals(whereParamsPartsUKPK + "GRecordSet result = " + selectApiStr + ".wherePK(whereParams)" + System.lineSeparator() + ".findRecordSet();", result.getData().get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE_PK)));
		assertEquals(whereParamsPartsUKPK + "GRecordSet result = " + selectApiStr + ".whereUK(\"unionkey01\", whereParams)" + System.lineSeparator() + "//.whereUK(\"unionkey02\", whereParams)" + System.lineSeparator() + "//.whereUK(\"unionkey03\", whereParams)" + System.lineSeparator() + ".findRecordSet();", result.getData().get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE_UK)));
		
		assertEquals(GCrudType.DELETE, result.getData().get(3).getType());
		assertEquals("仮想表ID: 32F9873E7C744002A8DBD49D87007442", result.getData().get(3).getDescription());
		assertEquals(getDeleteSql_multipleUK(), result.getData().get(3).getSqlScript());
		
		GDao2VirtualEntity deleteCodeResult = ((GDao2JdbcXFormCodeResult)result.getData().get(3)).getVirtualEntity();
		assertEquals(4, deleteCodeResult.getColumns().size());
		assertEquals("-", selectCodeResult.getAbsoluteVirtualColumnCode());
		
		String deleteRecordStr = "GRecord whereParams = createRecord();" + System.lineSeparator() +
				"whereParams.setObject(\"objectID\",);" + System.lineSeparator();
		String deleteApiStr = "delete(\"jdbc_tool_test_multipleUK\")" + System.lineSeparator();
		String deleteApiWhereStr = ".where(expMap($($(\"jdbc_tool_test_multipleUK.objectID = :jdbc_tool_test_multipleUK.objectID\").AND(\"jdbc_tool_test_multipleUK.NStringColumn = :jdbc_tool_test_multipleUK.NStringColumn\")), whereParams))" + System.lineSeparator();
		assertEquals(whereParamsParts + "int count = " + deleteApiStr + deleteApiWhereStr + ".execute();", result.getData().get(3).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE)));
		
		String deleteUKRecordStr = "GRecord whereParams = createRecord();" + System.lineSeparator() +
				"whereParams.setObject(\"objectID\",);" + System.lineSeparator() +
				"whereParams.setObject(\"StringColumn\",);" + System.lineSeparator() +
				"//ユーニックキー「unionkey02」用のカラム" + System.lineSeparator() +
				"/*whereParams.setObject(\"objectID\",);" + System.lineSeparator() +
				"whereParams.setObject(\"NStringColumn\",);" + System.lineSeparator() +
				"*/" + System.lineSeparator() +  "//ユーニックキー「unionkey03」用のカラム" + System.lineSeparator() +
				"/*whereParams.setObject(\"StringColumn\",);" + System.lineSeparator() +
				"whereParams.setObject(\"NStringColumn\",);" + System.lineSeparator() +
				"*/" + System.lineSeparator();
		assertEquals(deleteRecordStr + System.lineSeparator() + "int count = " + deleteApiStr + ".wherePK(whereParams)" + System.lineSeparator() + ".execute();", result.getData().get(3).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_PK)));
		assertEquals(deleteUKRecordStr + System.lineSeparator() + "int count = " + deleteApiStr + ".whereUK(\"unionkey01\", whereParams)" + System.lineSeparator() + "//.whereUK(\"unionkey02\", whereParams)" + System.lineSeparator() + "//.whereUK(\"unionkey03\", whereParams)" + System.lineSeparator() + ".execute();", result.getData().get(3).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_UK)));
		
		StringBuilder deleteListRecordStr = new StringBuilder();
		deleteListRecordStr.append(deleteRecordStr);
		deleteListRecordStr.append("List<GRecord> whereParamsList = new ArrayList<>();").append(System.lineSeparator());
		deleteListRecordStr.append("whereParamsList.add(whereParams);").append(System.lineSeparator());
		
		StringBuilder deleteListUKRecordStr = new StringBuilder();
		deleteListUKRecordStr.append(deleteUKRecordStr);
		deleteListUKRecordStr.append("List<GRecord> whereParamsList = new ArrayList<>();").append(System.lineSeparator());
		deleteListUKRecordStr.append("whereParamsList.add(whereParams);").append(System.lineSeparator());
		assertEquals(deleteListRecordStr + System.lineSeparator() + "int count = " + deleteApiStr + ".wherePK(whereParamsList)" + System.lineSeparator() + ".executeList();", result.getData().get(3).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_PK)));
		assertEquals(deleteListUKRecordStr + System.lineSeparator() + "int count = " + deleteApiStr + ".whereUK(\"unionkey01\", whereParamsList)" + System.lineSeparator() + "//.whereUK(\"unionkey02\", whereParamsList)" + System.lineSeparator() + "//.whereUK(\"unionkey03\", whereParamsList)" + System.lineSeparator() + ".executeList();", result.getData().get(3).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_UK)));
		assertEquals(deleteListRecordStr + System.lineSeparator() + "int count = " + deleteApiStr + ".wherePK(whereParamsList)" + System.lineSeparator() + ".executeBatch();", result.getData().get(3).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_PK)));
		assertEquals(deleteListUKRecordStr + System.lineSeparator() + "int count = " + deleteApiStr + ".whereUK(\"unionkey01\", whereParamsList)" + System.lineSeparator() + "//.whereUK(\"unionkey02\", whereParamsList)" + System.lineSeparator() + "//.whereUK(\"unionkey03\", whereParamsList)" + System.lineSeparator() + ".executeBatch();", result.getData().get(3).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_UK)));
	}
	
	@Test
	public void testExport_noPrimaryKey_inq() {

		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_noPrimaryKey_inq.xml");
		String conditionFilePath = getConditionFilePath("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_noPrimaryKey_inq.yaml");
		GExporterResult<List<GXFormCodeResult>, byte[]> result = service.export(conditionFilePath, settingsFilePath);

		assertEquals("html", result.getMediaType());
		assertEquals(2, result.getData().size());
		assertEquals(GCrudType.SELECT, result.getData().get(0).getType());
		assertEquals("仮想表ID: 4A0C9E4BF6234DFC86870D3136BA7136", result.getData().get(0).getDescription());
		assertEquals("SELECT" + System.lineSeparator() +
				"    jdbc_tool_test_noPrimaryKey.StringColumn," + System.lineSeparator() +
				"    jdbc_tool_test_noPrimaryKey.NStringColumn," + System.lineSeparator() +
				"    jdbc_tool_test_noPrimaryKey.IntColumn" + System.lineSeparator() +
				"FROM" + System.lineSeparator() +
				"    " + dbSchema + ".jdbc_tool_test_noPrimaryKey jdbc_tool_test_noPrimaryKey" + System.lineSeparator() +
				"WHERE" + System.lineSeparator() +
				"    jdbc_tool_test_noPrimaryKey.StringColumn = ?", result.getData().get(0).getSqlScript());
		
		GDao2VirtualEntity selectCodeResult = ((GDao2JdbcXFormCodeResult)result.getData().get(0)).getVirtualEntity();
		assertEquals(3, selectCodeResult.getColumns().size());
		assertEquals("-", selectCodeResult.getAbsoluteVirtualColumnCode());

		String whereParamsParts = "Map<String, Object> whereParams = new HashMap<>();" + System.lineSeparator() +
				"whereParams.put(\"jdbc_tool_test_noPrimaryKey.StringColumn\",);" + System.lineSeparator();
		String whereParamsPartsUKPK = "Object[] whereParams = new Object[]{};" + System.lineSeparator();
		String selectApiStr = "select(\"jdbc_tool_test_noPrimaryKey\", \"jdbc_tool_test_noPrimaryKey\")" + System.lineSeparator() + ".fields(colsAll())" + System.lineSeparator();
		String selectWhereParts = ".where(expMap($(\"jdbc_tool_test_noPrimaryKey.StringColumn = :jdbc_tool_test_noPrimaryKey.StringColumn\"), whereParams))" + System.lineSeparator();
		
		assertEquals(whereParamsParts + "List<GRecord> result = " + selectApiStr + selectWhereParts + ".findList();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE)));
		assertEquals(whereParamsPartsUKPK + "List<GRecord> result = " + selectApiStr + ".whereUK(\"unionkey02\", whereParams)" + System.lineSeparator() + ".findList();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE_UK)));
		assertEquals(whereParamsParts + "GRecord result = " + selectApiStr + selectWhereParts + ".findRecord();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE)));
		assertEquals(whereParamsPartsUKPK + "GRecord result = " + selectApiStr + ".whereUK(\"unionkey02\", whereParams)" + System.lineSeparator() + ".findRecord();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE_UK)));
		assertEquals(whereParamsParts + "GRecordSet result = " + selectApiStr + selectWhereParts + ".findRecordSet();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE)));
		assertEquals(whereParamsPartsUKPK + "GRecordSet result = " + selectApiStr + ".whereUK(\"unionkey02\", whereParams)" + System.lineSeparator() + ".findRecordSet();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE_UK)));
		
		assertEquals(GCrudType.DELETE, result.getData().get(1).getType());
		assertEquals("仮想表ID: 4A0C9E4BF6234DFC86870D3136BA7136", result.getData().get(1).getDescription());
		assertEquals(getDeleteSql_noPrimaryKey(), result.getData().get(1).getSqlScript());
		
		GDao2VirtualEntity deleteCodeResult = ((GDao2JdbcXFormCodeResult)result.getData().get(1)).getVirtualEntity();
		assertEquals(3, deleteCodeResult.getColumns().size());
		assertEquals("-", selectCodeResult.getAbsoluteVirtualColumnCode());
		
		String deleteApiStr = "delete(\"jdbc_tool_test_noPrimaryKey\")" + System.lineSeparator();
		String deleteWhereParts = ".where(expMap($(\"jdbc_tool_test_noPrimaryKey.StringColumn = :jdbc_tool_test_noPrimaryKey.StringColumn\"), whereParams))" + System.lineSeparator();
		assertEquals(whereParamsParts + "int count = " + deleteApiStr + deleteWhereParts + ".execute();", result.getData().get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE)));
		
		String deleteExecuteUKWhereParts = "GRecord whereParams = createRecord();" + System.lineSeparator() +
				"whereParams.setObject(\"NStringColumn\",);" + System.lineSeparator() +
				"whereParams.setObject(\"IntColumn\",);" + System.lineSeparator();
		String deleteExecuteListUKWhereParts = deleteExecuteUKWhereParts +
				"List<GRecord> whereParamsList = new ArrayList<>();" + System.lineSeparator() +
				"whereParamsList.add(whereParams);" + System.lineSeparator();
		
		assertEquals(deleteExecuteUKWhereParts + System.lineSeparator() + "int count = " + deleteApiStr + ".whereUK(\"unionkey02\", whereParams)" + System.lineSeparator() + ".execute();", result.getData().get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_UK)));
		assertEquals(deleteExecuteListUKWhereParts + System.lineSeparator() + "int count = " + deleteApiStr + ".whereUK(\"unionkey02\", whereParamsList)" + System.lineSeparator() + ".executeList();", result.getData().get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_UK)));
		assertEquals(deleteExecuteListUKWhereParts + System.lineSeparator() + "int count = " + deleteApiStr + ".whereUK(\"unionkey02\", whereParamsList)" + System.lineSeparator() + ".executeBatch();", result.getData().get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_UK)));
	}
	
	public String getDeleteSql_noPrimaryKey() {
		return "DELETE FROM" + System.lineSeparator() +
				"    " + dbSchema + ".jdbc_tool_test_noPrimaryKey" + System.lineSeparator() +
				"WHERE" + System.lineSeparator() +
				"    " + dbSchema + ".jdbc_tool_test_noPrimaryKey.StringColumn = ?";
	}
	
	@Test
	public void testExport_noPrimaryKey_reg() {

		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_noPrimaryKey_reg.xml");
		String conditionFilePath = getConditionFilePath("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_noPrimaryKey_reg.yaml");
		GExporterResult<List<GXFormCodeResult>, byte[]> result = service.export(conditionFilePath, settingsFilePath);

		assertEquals("html", result.getMediaType());
		assertEquals(4, result.getData().size());
		assertEquals(GCrudType.INSERT, result.getData().get(0).getType());
		assertEquals("仮想表ID: 1CEB5BB51B76406DAB4025137288D214", result.getData().get(0).getDescription());
		assertEquals("INSERT INTO" + System.lineSeparator() +
				"    " + dbSchema + ".jdbc_tool_test_noPrimaryKey(StringColumn, NStringColumn, IntColumn)" + System.lineSeparator() +
				"VALUES" + System.lineSeparator() +
				"(?, ?, ?)", result.getData().get(0).getSqlScript());
		
		GDao2VirtualEntity insertCodeResult = ((GDao2JdbcXFormCodeResult)result.getData().get(0)).getVirtualEntity();
		assertEquals(3, insertCodeResult.getColumns().size());
		assertEquals("-", insertCodeResult.getAbsoluteVirtualColumnCode());

		String insertRecord = "GRecord record = createRecord();" + System.lineSeparator() +
				"record.setObject(\"StringColumn\",);" + System.lineSeparator() +
				"record.setObject(\"NStringColumn\",);" + System.lineSeparator() +
				"record.setObject(\"IntColumn\",);" + System.lineSeparator();
		String insertExecuteApiStr = "insert(\"jdbc_tool_test_noPrimaryKey\")" + System.lineSeparator() + ".values(record)";
		
		assertEquals(insertRecord + System.lineSeparator() + "int count = " + insertExecuteApiStr + System.lineSeparator() + ".execute();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, null)));
		assertNull(result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE)));
		assertNull(result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_PK)));
		assertNull(result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_UK)));
		
		String insertExecuteBatchApiStr = "insert(\"jdbc_tool_test_noPrimaryKey\")" + System.lineSeparator() + ".values(recordList)";
		StringBuilder insertRecordList = new StringBuilder();
		insertRecordList.append("List<GRecord> recordList = new ArrayList<>();").append(System.lineSeparator());
		insertRecordList.append(insertRecord);
		insertRecordList.append("recordList.add(record);").append(System.lineSeparator());
		
		assertEquals(insertRecordList + System.lineSeparator() + "int count = " + insertExecuteBatchApiStr + System.lineSeparator() + ".executeBatch();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, null)));
		assertNull(result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE)));
		assertNull(result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_PK)));
		assertNull(result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_UK)));
		assertEquals(insertRecordList + System.lineSeparator() + "int count = " + insertExecuteBatchApiStr + System.lineSeparator() + ".executeList();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, null)));
		assertNull(result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE)));
		assertNull(result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_PK)));
		assertNull(result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_UK)));
		
		
		assertEquals(GCrudType.UPDATE, result.getData().get(1).getType());
		assertEquals("仮想表ID: 1CEB5BB51B76406DAB4025137288D214", result.getData().get(1).getDescription());
		assertEquals("UPDATE" + System.lineSeparator() +
				"    " + dbSchema + ".jdbc_tool_test_noPrimaryKey jdbc_tool_test_noPrimaryKey" + System.lineSeparator() +
				"SET" + System.lineSeparator() +
				"    IntColumn = ?" + System.lineSeparator() +
				"WHERE" + System.lineSeparator() +
				"    jdbc_tool_test_noPrimaryKey.StringColumn = ?", result.getData().get(1).getSqlScript());
		
		GDao2VirtualEntity updateCodeResult = ((GDao2JdbcXFormCodeResult)result.getData().get(1)).getVirtualEntity();
		assertEquals(3, updateCodeResult.getColumns().size());
		assertEquals("-", updateCodeResult.getAbsoluteVirtualColumnCode());

		String whereParamsParts = "Map<String, Object> whereParams = new HashMap<>();" + System.lineSeparator() +
				"whereParams.put(\"jdbc_tool_test_noPrimaryKey.StringColumn\",);" + System.lineSeparator();
		String whereParamsPartsUKPK = "Object[] whereParams = new Object[]{};" + System.lineSeparator();
		String updateRecord = "GRecord record = createRecord();" + System.lineSeparator() +
				"record.setObject(\"IntColumn\",);" + System.lineSeparator();
		String updateExecuteApiStr = "update(\"jdbc_tool_test_noPrimaryKey\")" + System.lineSeparator() + ".set(record)" + System.lineSeparator();
		String updateExecuteApiWhereStr = ".where(expMap($(\"jdbc_tool_test_noPrimaryKey.StringColumn = :jdbc_tool_test_noPrimaryKey.StringColumn\"), whereParams))" + System.lineSeparator();
		assertEquals(whereParamsParts + updateRecord + System.lineSeparator() + "int count = " + updateExecuteApiStr + updateExecuteApiWhereStr + ".execute();", result.getData().get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE)));
		assertEquals(updateRecord + System.lineSeparator() + "int count = " + updateExecuteApiStr + ".whereUK(\"unionkey02\")" + System.lineSeparator() + ".execute();", result.getData().get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_UK)));
		
		StringBuilder updateRecordList = new StringBuilder();
		updateRecordList.append("List<GRecord> recordList = new ArrayList<>();").append(System.lineSeparator());
		updateRecordList.append(updateRecord);
		updateRecordList.append("recordList.add(record);").append(System.lineSeparator());
		
		String updateExecuteListApiStr = "update(\"jdbc_tool_test_noPrimaryKey\")" + System.lineSeparator() + ".set(recordList)" + System.lineSeparator();
		
		assertEquals(updateRecordList + System.lineSeparator() + "int count = " + updateExecuteListApiStr + ".whereUK(\"unionkey02\")" + System.lineSeparator() + ".executeList();", result.getData().get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_UK)));
		assertEquals(updateRecordList + System.lineSeparator() + "int count = " + updateExecuteListApiStr + ".whereUK(\"unionkey02\")" + System.lineSeparator() + ".executeBatch();", result.getData().get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_UK)));
		
		assertEquals(GCrudType.SELECT, result.getData().get(2).getType());
		assertEquals("仮想表ID: 1CEB5BB51B76406DAB4025137288D214", result.getData().get(2).getDescription());
		assertEquals("SELECT" + System.lineSeparator() +
				"    jdbc_tool_test_noPrimaryKey.StringColumn," + System.lineSeparator() +
				"    jdbc_tool_test_noPrimaryKey.NStringColumn," + System.lineSeparator() +
				"    jdbc_tool_test_noPrimaryKey.IntColumn" + System.lineSeparator() +
				"FROM" + System.lineSeparator() +
				"    " + dbSchema + ".jdbc_tool_test_noPrimaryKey jdbc_tool_test_noPrimaryKey" + System.lineSeparator() +
				"WHERE" + System.lineSeparator() +
				"    jdbc_tool_test_noPrimaryKey.StringColumn = ?", result.getData().get(2).getSqlScript());
		
		GDao2VirtualEntity selectCodeResult = ((GDao2JdbcXFormCodeResult)result.getData().get(2)).getVirtualEntity();
		assertEquals(3, selectCodeResult.getColumns().size());
		assertEquals("-", selectCodeResult.getAbsoluteVirtualColumnCode());

		String selectApiStr = "select(\"jdbc_tool_test_noPrimaryKey\", \"jdbc_tool_test_noPrimaryKey\")" + System.lineSeparator() +
				".fields(colsAll())" + System.lineSeparator();
		String selectApiWhereStr = ".where(expMap($(\"jdbc_tool_test_noPrimaryKey.StringColumn = :jdbc_tool_test_noPrimaryKey.StringColumn\"), whereParams))" + System.lineSeparator();
		assertEquals(whereParamsParts + "List<GRecord> result = " + selectApiStr + selectApiWhereStr + ".findList();", result.getData().get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE)));
		assertEquals(whereParamsPartsUKPK + "List<GRecord> result = " + selectApiStr + ".whereUK(\"unionkey02\", whereParams)" + System.lineSeparator() + ".findList();", result.getData().get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE_UK)));
		
		assertEquals(whereParamsParts + "GRecord result = " + selectApiStr + selectApiWhereStr + ".findRecord();", result.getData().get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE)));
		assertEquals(whereParamsPartsUKPK + "GRecord result = " + selectApiStr + ".whereUK(\"unionkey02\", whereParams)" + System.lineSeparator() + ".findRecord();", result.getData().get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE_UK)));
		
		assertEquals(whereParamsParts + "GRecordSet result = " + selectApiStr + selectApiWhereStr + ".findRecordSet();", result.getData().get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE)));
		assertEquals(whereParamsPartsUKPK + "GRecordSet result = " + selectApiStr + ".whereUK(\"unionkey02\", whereParams)" + System.lineSeparator() + ".findRecordSet();", result.getData().get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE_UK)));
		
		assertEquals(GCrudType.DELETE, result.getData().get(3).getType());
		assertEquals("仮想表ID: 1CEB5BB51B76406DAB4025137288D214", result.getData().get(3).getDescription());
		assertEquals(getDeleteSql_noPrimaryKey(), result.getData().get(3).getSqlScript());
		
		GDao2VirtualEntity deleteCodeResult = ((GDao2JdbcXFormCodeResult)result.getData().get(3)).getVirtualEntity();
		assertEquals(3, deleteCodeResult.getColumns().size());
		assertEquals("-", selectCodeResult.getAbsoluteVirtualColumnCode());
		
		String deleteApiStr = "delete(\"jdbc_tool_test_noPrimaryKey\")" + System.lineSeparator();
		String deleteApiWhereStr = ".where(expMap($(\"jdbc_tool_test_noPrimaryKey.StringColumn = :jdbc_tool_test_noPrimaryKey.StringColumn\"), whereParams))" + System.lineSeparator();
		assertEquals(whereParamsParts + "int count = " + deleteApiStr + deleteApiWhereStr + ".execute();", result.getData().get(3).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE)));
		

		String deleteRecordStr = "GRecord whereParams = createRecord();" + System.lineSeparator() +
				"whereParams.setObject(\"NStringColumn\",);" + System.lineSeparator() +
				"whereParams.setObject(\"IntColumn\",);" + System.lineSeparator();
		
		assertEquals(deleteRecordStr + System.lineSeparator() + "int count = " + deleteApiStr + ".whereUK(\"unionkey02\", whereParams)" + System.lineSeparator() + ".execute();", result.getData().get(3).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_UK)));
		
		StringBuilder deleteListRecordStr = new StringBuilder();
		deleteListRecordStr.append(deleteRecordStr);
		deleteListRecordStr.append("List<GRecord> whereParamsList = new ArrayList<>();").append(System.lineSeparator());
		deleteListRecordStr.append("whereParamsList.add(whereParams);").append(System.lineSeparator());
		
		assertEquals(deleteListRecordStr + System.lineSeparator() + "int count = " + deleteApiStr + ".whereUK(\"unionkey02\", whereParamsList)" + System.lineSeparator() + ".executeList();", result.getData().get(3).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_UK)));
		assertEquals(deleteListRecordStr + System.lineSeparator() + "int count = " + deleteApiStr + ".whereUK(\"unionkey02\", whereParamsList)" + System.lineSeparator() + ".executeBatch();", result.getData().get(3).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_UK)));
	}
	
	@Test
	public void testExport_noUnionKey_inq() {

		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_noUnionKey_inq.xml");
		String conditionFilePath = getConditionFilePath("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_noUnionKey_inq.yaml");
		GExporterResult<List<GXFormCodeResult>, byte[]> result = service.export(conditionFilePath, settingsFilePath);

		assertEquals("html", result.getMediaType());
		assertEquals(2, result.getData().size());
		assertEquals(GCrudType.SELECT, result.getData().get(0).getType());
		assertEquals("仮想表ID: 4BF0513B4F0649B48379DF6F65E4074D", result.getData().get(0).getDescription());
		assertEquals("SELECT" + System.lineSeparator() +
				"    jdbc_tool_test_noUnionKey.objectID," + System.lineSeparator() +
				"    jdbc_tool_test_noUnionKey.StringColumn," + System.lineSeparator() +
				"    jdbc_tool_test_noUnionKey.NStringColumn," + System.lineSeparator() +
				"    jdbc_tool_test_noUnionKey.IntColumn" + System.lineSeparator() +
				"FROM" + System.lineSeparator() +
				"    " + dbSchema + ".jdbc_tool_test_noUnionKey jdbc_tool_test_noUnionKey" + System.lineSeparator() +
				"WHERE" + System.lineSeparator() +
				"    jdbc_tool_test_noUnionKey.StringColumn = ?", result.getData().get(0).getSqlScript());
		
		GDao2VirtualEntity selectCodeResult = ((GDao2JdbcXFormCodeResult)result.getData().get(0)).getVirtualEntity();
		assertEquals(4, selectCodeResult.getColumns().size());
		assertEquals("-", selectCodeResult.getAbsoluteVirtualColumnCode());

		String whereParamsParts = "Map<String, Object> whereParams = new HashMap<>();" + System.lineSeparator() +
				"whereParams.put(\"jdbc_tool_test_noUnionKey.StringColumn\",);" + System.lineSeparator();
		String whereParamsPartsUKPK = "Object[] whereParams = new Object[]{};" + System.lineSeparator();
		String selectApiStr = "select(\"jdbc_tool_test_noUnionKey\", \"jdbc_tool_test_noUnionKey\")" + System.lineSeparator() + ".fields(colsAll())" + System.lineSeparator();
		String selectWhereParts = ".where(expMap($(\"jdbc_tool_test_noUnionKey.StringColumn = :jdbc_tool_test_noUnionKey.StringColumn\"), whereParams))" + System.lineSeparator();
		
		assertEquals(whereParamsParts + "List<GRecord> result = " + selectApiStr + selectWhereParts + ".findList();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE)));
		assertEquals(whereParamsPartsUKPK + "List<GRecord> result = " + selectApiStr + ".wherePK(whereParams)" + System.lineSeparator() + ".findList();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE_PK)));
		assertEquals(whereParamsParts + "GRecord result = " + selectApiStr + selectWhereParts + ".findRecord();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE)));
		assertEquals(whereParamsPartsUKPK + "GRecord result = " + selectApiStr + ".wherePK(whereParams)" + System.lineSeparator() + ".findRecord();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE_PK)));
		assertEquals(whereParamsParts + "GRecordSet result = " + selectApiStr + selectWhereParts + ".findRecordSet();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE)));
		assertEquals(whereParamsPartsUKPK + "GRecordSet result = " + selectApiStr + ".wherePK(whereParams)" + System.lineSeparator() + ".findRecordSet();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE_PK)));
		
		assertEquals(GCrudType.DELETE, result.getData().get(1).getType());
		assertEquals("仮想表ID: 4BF0513B4F0649B48379DF6F65E4074D", result.getData().get(1).getDescription());
		assertEquals(getDeleteSql_noUnionKey(), result.getData().get(1).getSqlScript());
		
		GDao2VirtualEntity deleteCodeResult = ((GDao2JdbcXFormCodeResult)result.getData().get(1)).getVirtualEntity();
		assertEquals(4, deleteCodeResult.getColumns().size());
		assertEquals("-", selectCodeResult.getAbsoluteVirtualColumnCode());
		
		String deleteApiStr = "delete(\"jdbc_tool_test_noUnionKey\")" + System.lineSeparator();
		String deleteWhereParts = ".where(expMap($(\"jdbc_tool_test_noUnionKey.StringColumn = :jdbc_tool_test_noUnionKey.StringColumn\"), whereParams))" + System.lineSeparator();
		assertEquals(whereParamsParts + "int count = " + deleteApiStr + deleteWhereParts + ".execute();", result.getData().get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE)));
		
		String deleteExecutePKWhereParts = "GRecord whereParams = createRecord();" + System.lineSeparator() +
				"whereParams.setObject(\"objectID\",);" + System.lineSeparator();
		String deleteExecuteListUKWhereParts = deleteExecutePKWhereParts +
				"List<GRecord> whereParamsList = new ArrayList<>();" + System.lineSeparator() +
				"whereParamsList.add(whereParams);" + System.lineSeparator();
		
		assertEquals(deleteExecutePKWhereParts + System.lineSeparator() + "int count = " + deleteApiStr + ".wherePK(whereParams)" + System.lineSeparator() + ".execute();", result.getData().get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_PK)));
		assertEquals(deleteExecuteListUKWhereParts + System.lineSeparator() + "int count = " + deleteApiStr + ".wherePK(whereParamsList)" + System.lineSeparator() + ".executeList();", result.getData().get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_PK)));
		assertEquals(deleteExecuteListUKWhereParts + System.lineSeparator() + "int count = " + deleteApiStr + ".wherePK(whereParamsList)" + System.lineSeparator() + ".executeBatch();", result.getData().get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_PK)));
	}
	
	public String getDeleteSql_noUnionKey() {
		return "DELETE FROM" + System.lineSeparator() +
				"    " + dbSchema + ".jdbc_tool_test_noUnionKey" + System.lineSeparator() +
				"WHERE" + System.lineSeparator() +
				"    " + dbSchema + ".jdbc_tool_test_noUnionKey.StringColumn = ?";
	}
	
	@Test
	public void testExport_noUnionKey_reg() {

		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_noUnionKey_reg.xml");
		String conditionFilePath = getConditionFilePath("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_noUnionKey_reg.yaml");
		GExporterResult<List<GXFormCodeResult>, byte[]> result = service.export(conditionFilePath, settingsFilePath);

		assertEquals("html", result.getMediaType());
		assertEquals(4, result.getData().size());
		assertEquals(GCrudType.INSERT, result.getData().get(0).getType());
		assertEquals("仮想表ID: 7E2B27F2FBA7416186A748E378426E1B", result.getData().get(0).getDescription());
		assertEquals("INSERT INTO" + System.lineSeparator() +
				"    " + dbSchema + ".jdbc_tool_test_noUnionKey(objectID, StringColumn, NStringColumn, IntColumn)" + System.lineSeparator() +
				"VALUES" + System.lineSeparator() +
				"(?, ?, ?, ?)", result.getData().get(0).getSqlScript());
		
		GDao2VirtualEntity insertCodeResult = ((GDao2JdbcXFormCodeResult)result.getData().get(0)).getVirtualEntity();
		assertEquals(4, insertCodeResult.getColumns().size());
		assertEquals("-", insertCodeResult.getAbsoluteVirtualColumnCode());

		String insertRecord = "GRecord record = createRecord();" + System.lineSeparator() +
				"record.setObject(\"objectID\",);" + System.lineSeparator() +
				"record.setObject(\"StringColumn\",);" + System.lineSeparator() +
				"record.setObject(\"NStringColumn\",);" + System.lineSeparator() +
				"record.setObject(\"IntColumn\",);" + System.lineSeparator();
		String insertExecuteApiStr = "insert(\"jdbc_tool_test_noUnionKey\")" + System.lineSeparator() +  ".values(record)";
		
		assertEquals(insertRecord + System.lineSeparator() + "int count = " + insertExecuteApiStr + System.lineSeparator() + ".execute();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, null)));
		assertNull(result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE)));
		assertNull(result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_PK)));
		assertNull(result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_UK)));
		
		String insertExecuteBatchApiStr = "insert(\"jdbc_tool_test_noUnionKey\")" + System.lineSeparator() + ".values(recordList)";
		StringBuilder insertRecordList = new StringBuilder();
		insertRecordList.append("List<GRecord> recordList = new ArrayList<>();").append(System.lineSeparator());
		insertRecordList.append(insertRecord);
		insertRecordList.append("recordList.add(record);").append(System.lineSeparator());
		
		assertEquals(insertRecordList + System.lineSeparator() + "int count = " + insertExecuteBatchApiStr + System.lineSeparator() + ".executeBatch();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, null)));
		assertNull(result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE)));
		assertNull(result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_PK)));
		assertNull(result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_UK)));
		assertEquals(insertRecordList + System.lineSeparator() + "int count = " + insertExecuteBatchApiStr + System.lineSeparator() + ".executeList();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, null)));
		assertNull(result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE)));
		assertNull(result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_PK)));
		assertNull(result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_UK)));
		
		
		assertEquals(GCrudType.UPDATE, result.getData().get(1).getType());
		assertEquals("仮想表ID: 7E2B27F2FBA7416186A748E378426E1B", result.getData().get(1).getDescription());
		assertEquals("UPDATE" + System.lineSeparator() +
				"    " + dbSchema + ".jdbc_tool_test_noUnionKey jdbc_tool_test_noUnionKey" + System.lineSeparator() +
				"SET" + System.lineSeparator() +
				"    StringColumn = ?," + System.lineSeparator() +
				"    NStringColumn = ?," + System.lineSeparator() +
				"    IntColumn = ?" + System.lineSeparator() +
				"WHERE" + System.lineSeparator() +
				"    jdbc_tool_test_noUnionKey.StringColumn = ?", result.getData().get(1).getSqlScript());
		
		GDao2VirtualEntity updateCodeResult = ((GDao2JdbcXFormCodeResult)result.getData().get(1)).getVirtualEntity();
		assertEquals(4, updateCodeResult.getColumns().size());
		assertEquals("-", updateCodeResult.getAbsoluteVirtualColumnCode());

		String whereParamsParts = "Map<String, Object> whereParams = new HashMap<>();" + System.lineSeparator() +
				"whereParams.put(\"jdbc_tool_test_noUnionKey.StringColumn\",);" + System.lineSeparator();
		String whereParamsPartsUKPK = "Object[] whereParams = new Object[]{};" + System.lineSeparator();
		String updateRecord = "GRecord record = createRecord();" + System.lineSeparator() +
				"record.setObject(\"StringColumn\",);" + System.lineSeparator() +
				"record.setObject(\"NStringColumn\",);" + System.lineSeparator() +
				"record.setObject(\"IntColumn\",);" + System.lineSeparator();
		String updateExecuteApiStr = "update(\"jdbc_tool_test_noUnionKey\")" + System.lineSeparator() + ".set(record)" + System.lineSeparator();
		String updateExecuteApiWhereStr = ".where(expMap($(\"jdbc_tool_test_noUnionKey.StringColumn = :jdbc_tool_test_noUnionKey.StringColumn\"), whereParams))" + System.lineSeparator();
		assertEquals(whereParamsParts + updateRecord + System.lineSeparator() + "int count = " + updateExecuteApiStr + updateExecuteApiWhereStr + ".execute();", result.getData().get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE)));
		assertEquals(updateRecord + System.lineSeparator() + "int count = " + updateExecuteApiStr + ".wherePK()" + System.lineSeparator() + ".execute();", result.getData().get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_PK)));
		assertNull(result.getData().get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_UK)));
		
		StringBuilder updateRecordList = new StringBuilder();
		updateRecordList.append("List<GRecord> recordList = new ArrayList<>();").append(System.lineSeparator());
		updateRecordList.append(updateRecord);
		updateRecordList.append("recordList.add(record);").append(System.lineSeparator());
		
		String updateExecuteListApiStr = "update(\"jdbc_tool_test_noUnionKey\")" + System.lineSeparator() + ".set(recordList)" + System.lineSeparator();
		
		assertEquals(updateRecordList + System.lineSeparator() + "int count = " + updateExecuteListApiStr + ".wherePK()" + System.lineSeparator() + ".executeList();", result.getData().get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_PK)));
		assertEquals(updateRecordList + System.lineSeparator() + "int count = " + updateExecuteListApiStr + ".wherePK()" + System.lineSeparator() + ".executeBatch();", result.getData().get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_PK)));
		
		assertEquals(GCrudType.SELECT, result.getData().get(2).getType());
		assertEquals("仮想表ID: 7E2B27F2FBA7416186A748E378426E1B", result.getData().get(2).getDescription());
		assertEquals("SELECT" + System.lineSeparator() +
				"    jdbc_tool_test_noUnionKey.objectID," + System.lineSeparator() +
				"    jdbc_tool_test_noUnionKey.StringColumn," + System.lineSeparator() +
				"    jdbc_tool_test_noUnionKey.NStringColumn," + System.lineSeparator() +
				"    jdbc_tool_test_noUnionKey.IntColumn" + System.lineSeparator() +
				"FROM" + System.lineSeparator() +
				"    " + dbSchema + ".jdbc_tool_test_noUnionKey jdbc_tool_test_noUnionKey" + System.lineSeparator() +
				"WHERE" + System.lineSeparator() +
				"    jdbc_tool_test_noUnionKey.StringColumn = ?", result.getData().get(2).getSqlScript());
		
		GDao2VirtualEntity selectCodeResult = ((GDao2JdbcXFormCodeResult)result.getData().get(2)).getVirtualEntity();
		assertEquals(4, selectCodeResult.getColumns().size());
		assertEquals("-", selectCodeResult.getAbsoluteVirtualColumnCode());

		String selectApiStr = "select(\"jdbc_tool_test_noUnionKey\", \"jdbc_tool_test_noUnionKey\")" + System.lineSeparator() +
				".fields(colsAll())" + System.lineSeparator();
		String selectApiWhereStr = ".where(expMap($(\"jdbc_tool_test_noUnionKey.StringColumn = :jdbc_tool_test_noUnionKey.StringColumn\"), whereParams))" + System.lineSeparator();
		assertEquals(whereParamsParts + "List<GRecord> result = " + selectApiStr + selectApiWhereStr + ".findList();", result.getData().get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE)));
		assertEquals(whereParamsPartsUKPK + "List<GRecord> result = " + selectApiStr + ".wherePK(whereParams)" + System.lineSeparator() + ".findList();", result.getData().get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE_PK)));
		assertNull(result.getData().get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE_UK)));
		
		assertEquals(whereParamsParts + "GRecord result = " + selectApiStr + selectApiWhereStr + ".findRecord();", result.getData().get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE)));
		assertEquals(whereParamsPartsUKPK + "GRecord result = " + selectApiStr + ".wherePK(whereParams)" + System.lineSeparator() + ".findRecord();", result.getData().get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE_PK)));
		assertNull(result.getData().get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE_UK)));
		
		assertEquals(whereParamsParts + "GRecordSet result = " + selectApiStr + selectApiWhereStr + ".findRecordSet();", result.getData().get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE)));
		assertEquals(whereParamsPartsUKPK + "GRecordSet result = " + selectApiStr + ".wherePK(whereParams)" + System.lineSeparator() + ".findRecordSet();", result.getData().get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE_PK)));
		assertNull(result.getData().get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE_UK)));
		
		assertEquals(GCrudType.DELETE, result.getData().get(3).getType());
		assertEquals("仮想表ID: 7E2B27F2FBA7416186A748E378426E1B", result.getData().get(3).getDescription());
		assertEquals(getDeleteSql_noUnionKey(), result.getData().get(3).getSqlScript());
		
		GDao2VirtualEntity deleteCodeResult = ((GDao2JdbcXFormCodeResult)result.getData().get(3)).getVirtualEntity();
		assertEquals(4, deleteCodeResult.getColumns().size());
		assertEquals("-", selectCodeResult.getAbsoluteVirtualColumnCode());
		
		String deleteApiStr = "delete(\"jdbc_tool_test_noUnionKey\")" + System.lineSeparator();
		String deleteApiWhereStr = ".where(expMap($(\"jdbc_tool_test_noUnionKey.StringColumn = :jdbc_tool_test_noUnionKey.StringColumn\"), whereParams))" + System.lineSeparator();
		assertEquals(whereParamsParts + "int count = " + deleteApiStr + deleteApiWhereStr + ".execute();", result.getData().get(3).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE)));
		

		String deleteRecordStr = "GRecord whereParams = createRecord();" + System.lineSeparator() +
				"whereParams.setObject(\"objectID\",);" + System.lineSeparator();
		
		assertEquals(deleteRecordStr + System.lineSeparator() + "int count = " + deleteApiStr + ".wherePK(whereParams)" + System.lineSeparator() + ".execute();", result.getData().get(3).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_PK)));
		assertNull(result.getData().get(3).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_UK)));
		
		StringBuilder deleteListRecordStr = new StringBuilder();
		deleteListRecordStr.append(deleteRecordStr);
		deleteListRecordStr.append("List<GRecord> whereParamsList = new ArrayList<>();").append(System.lineSeparator());
		deleteListRecordStr.append("whereParamsList.add(whereParams);").append(System.lineSeparator());
		
		assertNull(result.getData().get(3).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE)));
		assertEquals(deleteListRecordStr + System.lineSeparator() + "int count = " + deleteApiStr + ".wherePK(whereParamsList)" + System.lineSeparator() + ".executeList();", result.getData().get(3).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_PK)));
		assertNull(result.getData().get(3).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_UK)));
		assertNull(result.getData().get(3).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE)));
		assertEquals(deleteListRecordStr + System.lineSeparator() + "int count = " + deleteApiStr + ".wherePK(whereParamsList)" + System.lineSeparator() + ".executeBatch();", result.getData().get(3).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_PK)));
		assertNull(result.getData().get(3).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_UK)));
	}
	
	@Test
	public void testExport_noPrimaryKeyAndUnionKey_inq() {

		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_noPrimaryKeyAndUnionKey_inq.xml");
		String conditionFilePath = getConditionFilePath("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_noPrimaryKeyAndUnionKey_inq.yaml");
		GExporterResult<List<GXFormCodeResult>, byte[]> result = service.export(conditionFilePath, settingsFilePath);

		assertEquals("html", result.getMediaType());
		assertEquals(2, result.getData().size());
		assertEquals(GCrudType.SELECT, result.getData().get(0).getType());
		assertEquals("仮想表ID: A5F7CD6A13214BC5B5CB65CD9E81CABB", result.getData().get(0).getDescription());
		assertEquals("SELECT" + System.lineSeparator() +
				"    jdbc_tool_test_noPrimaryKeyAndUnionKey.StringColumn," + System.lineSeparator() +
				"    jdbc_tool_test_noPrimaryKeyAndUnionKey.NStringColumn," + System.lineSeparator() +
				"    jdbc_tool_test_noPrimaryKeyAndUnionKey.IntColumn" + System.lineSeparator() +
				"FROM" + System.lineSeparator() +
				"    " + dbSchema + ".jdbc_tool_test_noPrimaryKeyAndUnionKey jdbc_tool_test_noPrimaryKeyAndUnionKey" + System.lineSeparator() +
				"WHERE" + System.lineSeparator() +
				"    jdbc_tool_test_noPrimaryKeyAndUnionKey.StringColumn = ?", result.getData().get(0).getSqlScript());
		
		GDao2VirtualEntity selectCodeResult = ((GDao2JdbcXFormCodeResult)result.getData().get(0)).getVirtualEntity();
		assertEquals(3, selectCodeResult.getColumns().size());
		assertEquals("-", selectCodeResult.getAbsoluteVirtualColumnCode());

		String whereParamsParts = "Map<String, Object> whereParams = new HashMap<>();" + System.lineSeparator() +
				"whereParams.put(\"jdbc_tool_test_noPrimaryKeyAndUnionKey.StringColumn\",);" + System.lineSeparator();
		String selectApiStr = "select(\"jdbc_tool_test_noPrimaryKeyAndUnionKey\", \"jdbc_tool_test_noPrimaryKeyAndUnionKey\")" + System.lineSeparator() + ".fields(colsAll())" + System.lineSeparator();
		String selectWhereParts = ".where(expMap($(\"jdbc_tool_test_noPrimaryKeyAndUnionKey.StringColumn = :jdbc_tool_test_noPrimaryKeyAndUnionKey.StringColumn\"), whereParams))" + System.lineSeparator();
		
		assertEquals(whereParamsParts + "List<GRecord> result = " + selectApiStr + selectWhereParts + ".findList();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE)));
		assertNull(result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE_PK)));
		assertNull(result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE_UK)));
		assertEquals(whereParamsParts + "GRecord result = " + selectApiStr + selectWhereParts + ".findRecord();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE)));
		assertNull(result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE_PK)));
		assertNull(result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE_UK)));
		assertEquals(whereParamsParts + "GRecordSet result = " + selectApiStr + selectWhereParts + ".findRecordSet();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE)));
		assertNull(result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE_PK)));
		assertNull(result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE_UK)));
		
		assertEquals(GCrudType.DELETE, result.getData().get(1).getType());
		assertEquals("仮想表ID: A5F7CD6A13214BC5B5CB65CD9E81CABB", result.getData().get(1).getDescription());
		assertEquals(getDeleteSql_noPrimaryKeyAndUnionKey(), result.getData().get(1).getSqlScript());
		
		GDao2VirtualEntity deleteCodeResult = ((GDao2JdbcXFormCodeResult)result.getData().get(1)).getVirtualEntity();
		assertEquals(3, deleteCodeResult.getColumns().size());
		assertEquals("-", selectCodeResult.getAbsoluteVirtualColumnCode());
		
		String deleteApiStr = "delete(\"jdbc_tool_test_noPrimaryKeyAndUnionKey\")" + System.lineSeparator();
		String deleteWhereParts = ".where(expMap($(\"jdbc_tool_test_noPrimaryKeyAndUnionKey.StringColumn = :jdbc_tool_test_noPrimaryKeyAndUnionKey.StringColumn\"), whereParams))" + System.lineSeparator();
		assertEquals(whereParamsParts + "int count = " + deleteApiStr + deleteWhereParts + ".execute();", result.getData().get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE)));
		assertNull(result.getData().get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_PK)));
		assertNull(result.getData().get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_UK)));
		assertNull(result.getData().get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_PK)));
		assertNull(result.getData().get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE)));
		assertNull(result.getData().get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_UK)));
		assertNull(result.getData().get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE)));
		assertNull(result.getData().get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_PK)));
		assertNull(result.getData().get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_UK)));
	}
	
	public String getDeleteSql_noPrimaryKeyAndUnionKey() {
		return "DELETE FROM" + System.lineSeparator() +
				"    " + dbSchema + ".jdbc_tool_test_noPrimaryKeyAndUnionKey" + System.lineSeparator() +
				"WHERE" + System.lineSeparator() +
				"    " + dbSchema + ".jdbc_tool_test_noPrimaryKeyAndUnionKey.StringColumn = ?";
	}
	
	@Test
	public void testExport_noPrimaryKeyAndUnionKey_reg() {

		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_noPrimaryKeyAndUnionKey_reg.xml");
		String conditionFilePath = getConditionFilePath("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_noPrimaryKeyAndUnionKey_reg.yaml");
		GExporterResult<List<GXFormCodeResult>, byte[]> result = service.export(conditionFilePath, settingsFilePath);

		assertEquals("html", result.getMediaType());
		assertEquals(3, result.getData().size());
		assertEquals(GCrudType.INSERT, result.getData().get(0).getType());
		assertEquals("仮想表ID: 6B0D549134F341549F85B1603947BAE4", result.getData().get(0).getDescription());
		assertEquals("INSERT INTO" + System.lineSeparator() +
				"    " + dbSchema + ".jdbc_tool_test_noPrimaryKeyAndUnionKey(StringColumn, NStringColumn, IntColumn)" + System.lineSeparator() +
				"VALUES" + System.lineSeparator() +
				"(?, ?, ?)", result.getData().get(0).getSqlScript());
		
		GDao2VirtualEntity insertCodeResult = ((GDao2JdbcXFormCodeResult)result.getData().get(0)).getVirtualEntity();
		assertEquals(3, insertCodeResult.getColumns().size());
		assertEquals("-", insertCodeResult.getAbsoluteVirtualColumnCode());

		String insertRecord = "GRecord record = createRecord();" + System.lineSeparator() +
				"record.setObject(\"StringColumn\",);" + System.lineSeparator() +
				"record.setObject(\"NStringColumn\",);" + System.lineSeparator() +
				"record.setObject(\"IntColumn\",);" + System.lineSeparator();
		String insertExecuteApiStr = "insert(\"jdbc_tool_test_noPrimaryKeyAndUnionKey\")" + System.lineSeparator() + ".values(record)";
		
		assertEquals(insertRecord + System.lineSeparator() + "int count = " + insertExecuteApiStr + System.lineSeparator() + ".execute();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, null)));
		assertNull(result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE)));
		assertNull(result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_PK)));
		assertNull(result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_UK)));
		assertNull(result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE)));
		assertNull(result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_PK)));
		assertNull(result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_UK)));
		assertNull(result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE)));
		assertNull(result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_PK)));
		assertNull(result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_UK)));
		
		assertEquals(GCrudType.SELECT, result.getData().get(1).getType());
		assertEquals("仮想表ID: 6B0D549134F341549F85B1603947BAE4", result.getData().get(1).getDescription());
		assertEquals("SELECT" + System.lineSeparator() +
				"    jdbc_tool_test_noPrimaryKeyAndUnionKey.StringColumn," + System.lineSeparator() +
				"    jdbc_tool_test_noPrimaryKeyAndUnionKey.NStringColumn," + System.lineSeparator() +
				"    jdbc_tool_test_noPrimaryKeyAndUnionKey.IntColumn" + System.lineSeparator() +
				"FROM" + System.lineSeparator() +
				"    " + dbSchema + ".jdbc_tool_test_noPrimaryKeyAndUnionKey jdbc_tool_test_noPrimaryKeyAndUnionKey" + System.lineSeparator() +
				"WHERE" + System.lineSeparator() +
				"    jdbc_tool_test_noPrimaryKeyAndUnionKey.StringColumn = ?", result.getData().get(1).getSqlScript());
		
		GDao2VirtualEntity selectCodeResult = ((GDao2JdbcXFormCodeResult)result.getData().get(1)).getVirtualEntity();
		assertEquals(3, selectCodeResult.getColumns().size());
		assertEquals("-", selectCodeResult.getAbsoluteVirtualColumnCode());

		String whereParamsParts = "Map<String, Object> whereParams = new HashMap<>();" + System.lineSeparator() +
				"whereParams.put(\"jdbc_tool_test_noPrimaryKeyAndUnionKey.StringColumn\",);" + System.lineSeparator();
		String selectApiStr = "select(\"jdbc_tool_test_noPrimaryKeyAndUnionKey\", \"jdbc_tool_test_noPrimaryKeyAndUnionKey\")" + System.lineSeparator() +
				".fields(colsAll())" + System.lineSeparator();
		String selectApiWhereStr = ".where(expMap($(\"jdbc_tool_test_noPrimaryKeyAndUnionKey.StringColumn = :jdbc_tool_test_noPrimaryKeyAndUnionKey.StringColumn\"), whereParams))" + System.lineSeparator();
		
		assertEquals(whereParamsParts + "List<GRecord> result = " + selectApiStr + selectApiWhereStr + ".findList();", result.getData().get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE)));
		assertNull(result.getData().get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE_PK)));
		assertNull(result.getData().get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE_UK)));
		assertEquals(whereParamsParts + "GRecord result = " + selectApiStr + selectApiWhereStr + ".findRecord();", result.getData().get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE)));
		assertNull(result.getData().get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE_PK)));
		assertNull(result.getData().get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE_UK)));
		assertEquals(whereParamsParts + "GRecordSet result = " + selectApiStr + selectApiWhereStr + ".findRecordSet();", result.getData().get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE)));
		assertNull(result.getData().get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE_PK)));
		assertNull(result.getData().get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE_UK)));
		
		assertEquals(GCrudType.DELETE, result.getData().get(2).getType());
		assertEquals("仮想表ID: 6B0D549134F341549F85B1603947BAE4", result.getData().get(2).getDescription());
		assertEquals(getDeleteSql_noPrimaryKeyAndUnionKey(), result.getData().get(2).getSqlScript());
		
		GDao2VirtualEntity deleteCodeResult = ((GDao2JdbcXFormCodeResult)result.getData().get(2)).getVirtualEntity();
		assertEquals(3, deleteCodeResult.getColumns().size());
		assertEquals("-", selectCodeResult.getAbsoluteVirtualColumnCode());
		
		String deleteApiStr = "delete(\"jdbc_tool_test_noPrimaryKeyAndUnionKey\")" + System.lineSeparator();
		String deleteApiWhereStr = ".where(expMap($(\"jdbc_tool_test_noPrimaryKeyAndUnionKey.StringColumn = :jdbc_tool_test_noPrimaryKeyAndUnionKey.StringColumn\"), whereParams))" + System.lineSeparator();
		assertEquals(whereParamsParts + "int count = " + deleteApiStr + deleteApiWhereStr + ".execute();", result.getData().get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE)));
		assertNull(result.getData().get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_PK)));
		assertNull(result.getData().get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_UK)));
		
		
		assertNull(result.getData().get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE)));
		assertNull(result.getData().get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_PK)));
		assertNull(result.getData().get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_UK)));
		assertNull(result.getData().get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE)));
		assertNull(result.getData().get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_PK)));
		assertNull(result.getData().get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_UK)));
	}
	
	protected abstract String getUseForeignKeyFalseConfigPath();

	@Test
	public void testExport_join_mix_useForeignKeyFalse() {
		doCustomConfig(getUseForeignKeyFalseConfigPath());
		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_join_mix.xml");
		String conditionFilePath = getConditionFilePath("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_join_mix.yaml");
		GExporterResult<List<GXFormCodeResult>, byte[]> result = service.export(conditionFilePath, settingsFilePath);

		assertEquals("html", result.getMediaType());
		assertEquals(1, result.getData().size());
		assertEquals(GCrudType.SELECT, result.getData().get(0).getType());
		assertEquals("仮想表ID: E44ADEF7B30549449EDF193ECFB66631", result.getData().get(0).getDescription());
		assertEquals("SELECT" + System.lineSeparator() +
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
				"    LEFT OUTER JOIN " + dbSchema + ".jdbc_tool_test_unitmaster jdbc_tool_test_unitmaster ON jdbc_tool_test_pk_uk_mainmaster.objectID = jdbc_tool_test_unitmaster.Code" + System.lineSeparator() +
				"    INNER JOIN " + dbSchema + ".jdbc_tool_test_mainmaster jdbc_tool_test_mainmaster ON jdbc_tool_test_pk_uk_mainmaster.objectID = jdbc_tool_test_mainmaster.objectID" + System.lineSeparator() +
				"    RIGHT OUTER JOIN " + dbSchema + ".jdbc_tool_test_ordermaster jdbc_tool_test_ordermaster ON jdbc_tool_test_pk_uk_mainmaster.ItemCD = jdbc_tool_test_ordermaster.objectID" + System.lineSeparator() +
				"WHERE" + System.lineSeparator() +
				"    jdbc_tool_test_pk_uk_mainmaster.objectID = ?", result.getData().get(0).getSqlScript());

		String whereParamsParts = "Object[] whereParams = new Object[]{};" + System.lineSeparator();
		String groupByApiStr = "select(\"jdbc_tool_test_pk_uk_mainmaster\", \"jdbc_tool_test_pk_uk_mainmaster\")" + System.lineSeparator() +
				".fields(colsAll())" + System.lineSeparator() +
				".leftOuterJoin(\"jdbc_tool_test_unitmaster\", \"jdbc_tool_test_unitmaster\", exp($(\"jdbc_tool_test_pk_uk_mainmaster.objectID = jdbc_tool_test_unitmaster.Code\")))" + System.lineSeparator() +
				".innerJoin(\"jdbc_tool_test_mainmaster\", \"jdbc_tool_test_mainmaster\", exp($(\"jdbc_tool_test_pk_uk_mainmaster.objectID = jdbc_tool_test_mainmaster.objectID\")))" + System.lineSeparator() +
				".rightOuterJoin(\"jdbc_tool_test_ordermaster\", \"jdbc_tool_test_ordermaster\", exp($(\"jdbc_tool_test_pk_uk_mainmaster.ItemCD = jdbc_tool_test_ordermaster.objectID\")))" + System.lineSeparator() +
				".where(exp($(\"jdbc_tool_test_pk_uk_mainmaster.objectID = ?\"), whereParams))";

		assertEquals(whereParamsParts + "List<GRecord> result = " + groupByApiStr + System.lineSeparator() + ".findList();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE)));
		assertEquals(whereParamsParts + "GRecord result = " + groupByApiStr + System.lineSeparator() + ".findRecord();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE)));
		assertEquals(whereParamsParts + "GRecordSet result = " + groupByApiStr + System.lineSeparator() + ".findRecordSet();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE)));
	}
	
	protected abstract String getUseForeignKeyDefaultConfigPath();
	
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
		assertEquals("SELECT" + System.lineSeparator() +
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
				"    LEFT OUTER JOIN " + dbSchema + ".jdbc_tool_test_unitmaster jdbc_tool_test_unitmaster ON jdbc_tool_test_pk_uk_mainmaster.objectID = jdbc_tool_test_unitmaster.Code" + System.lineSeparator() +
				"    INNER JOIN " + dbSchema + ".jdbc_tool_test_mainmaster jdbc_tool_test_mainmaster ON jdbc_tool_test_pk_uk_mainmaster.objectID = jdbc_tool_test_mainmaster.objectID" + System.lineSeparator() +
				"    RIGHT OUTER JOIN " + dbSchema + ".jdbc_tool_test_ordermaster jdbc_tool_test_ordermaster ON jdbc_tool_test_pk_uk_mainmaster.ItemCD = jdbc_tool_test_ordermaster.objectID" + System.lineSeparator() +
				"WHERE" + System.lineSeparator() +
				"    jdbc_tool_test_pk_uk_mainmaster.objectID = ?", result.getData().get(0).getSqlScript());

		String whereParamsParts = "Object[] whereParams = new Object[]{};" + System.lineSeparator();
		String groupByApiStr = "select(\"jdbc_tool_test_pk_uk_mainmaster\", \"jdbc_tool_test_pk_uk_mainmaster\")" + System.lineSeparator() +
				".fields(colsAll())" + System.lineSeparator() +
				".leftOuterJoin(\"jdbc_tool_test_unitmaster\", \"jdbc_tool_test_unitmaster\", exp($(\"jdbc_tool_test_pk_uk_mainmaster.objectID = jdbc_tool_test_unitmaster.Code\")))" + System.lineSeparator() +
				".innerJoin(\"jdbc_tool_test_mainmaster\", \"jdbc_tool_test_mainmaster\", exp($(\"jdbc_tool_test_pk_uk_mainmaster.objectID = jdbc_tool_test_mainmaster.objectID\")))" + System.lineSeparator() +
				".rightOuterJoin(\"jdbc_tool_test_ordermaster\", \"jdbc_tool_test_ordermaster\", exp($(\"jdbc_tool_test_pk_uk_mainmaster.ItemCD = jdbc_tool_test_ordermaster.objectID\")))" + System.lineSeparator() +
				".where(exp($(\"jdbc_tool_test_pk_uk_mainmaster.objectID = ?\"), whereParams))";

		assertEquals(whereParamsParts + "List<GRecord> result = " + groupByApiStr + System.lineSeparator() + ".findList();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE)));
		assertEquals(whereParamsParts + "GRecord result = " + groupByApiStr + System.lineSeparator() + ".findRecord();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE)));
		assertEquals(whereParamsParts + "GRecordSet result = " + groupByApiStr + System.lineSeparator() + ".findRecordSet();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE)));
	}
	
	@Test
	public void testExport_expMapCase1() {
		
		// パターン１：where (a = ? and b = ?) or c = ?
		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_expmapcase1.xml");
		String conditionFilePath = getConditionFilePath("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_expmapcase1.yaml");
		GExporterResult<List<GXFormCodeResult>, byte[]> result = service.export(conditionFilePath, settingsFilePath);
		
		assertEquals("html", result.getMediaType());
		assertEquals(4, result.getData().size());
		assertEquals(GCrudType.INSERT, result.getData().get(0).getType());
		assertEquals("仮想表ID: 8D0CDDE194344769B42F631F31BAFEFB", result.getData().get(0).getDescription());
		assertEquals("INSERT INTO" + System.lineSeparator() +
				"    " + dbSchema + ".jdbc_tool_test_fullCaseMainEntity(objectID, StringColumn, IntColumn, NStringColumn, DateTimeColumn, YMColumn, CurrencyColumn, LongColumn, CompanyCD, ExclusiveFG, RegisteredPerson, UpdatedPerson, RegisteredDT, UpdatedDT)" + System.lineSeparator() +
				"VALUES" + System.lineSeparator() +
				"(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " + getTimestampCommand() + ", " + getTimestampCommand() + ")"
				, result.getData().get(0).getSqlScript());
		
		GDao2VirtualEntity insertCodeResult = ((GDao2JdbcXFormCodeResult)result.getData().get(0)).getVirtualEntity();
		assertEquals(14, insertCodeResult.getColumns().size());
		assertEquals("-", insertCodeResult.getAbsoluteVirtualColumnCode());

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
		String insertExecuteApiStr = "insert(\"jdbc_tool_test_fullCaseMainEntity\")" + System.lineSeparator() +  ".values(record)";
		
		assertEquals(insertRecord + System.lineSeparator() + "int count = " + insertExecuteApiStr + System.lineSeparator() + ".execute();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, null)));
		assertNull(result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE)));
		assertNull(result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_PK)));
		assertNull(result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_UK)));
		
		String insertExecuteBatchApiStr = "insert(\"jdbc_tool_test_fullCaseMainEntity\")" + System.lineSeparator() + ".values(recordList)";
		StringBuilder insertRecordList = new StringBuilder();
		insertRecordList.append("List<GRecord> recordList = new ArrayList<>();").append(System.lineSeparator());
		insertRecordList.append(insertRecord);
		insertRecordList.append("recordList.add(record);").append(System.lineSeparator());
		
		assertEquals(insertRecordList + System.lineSeparator() + "int count = " + insertExecuteBatchApiStr + System.lineSeparator() + ".executeBatch();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, null)));
		assertNull(result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE)));
		assertNull(result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_PK)));
		assertNull(result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_UK)));
		assertEquals(insertRecordList + System.lineSeparator() + "int count = " + insertExecuteBatchApiStr + System.lineSeparator() + ".executeList();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, null)));
		assertNull(result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE)));
		assertNull(result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_PK)));
		assertNull(result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_UK)));
		
		
		assertEquals(GCrudType.UPDATE, result.getData().get(1).getType());
		assertEquals("仮想表ID: 8D0CDDE194344769B42F631F31BAFEFB", result.getData().get(1).getDescription());
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
				"    )", result.getData().get(1).getSqlScript());
		
		GDao2VirtualEntity updateCodeResult = ((GDao2JdbcXFormCodeResult)result.getData().get(1)).getVirtualEntity();
		assertEquals(14, updateCodeResult.getColumns().size());
		assertEquals("-", updateCodeResult.getAbsoluteVirtualColumnCode());

		String whereParamsParts = "Map<String, Object> whereParams = new HashMap<>();" + System.lineSeparator() +
				"whereParams.put(\"jdbc_tool_test_fullCaseMainEntity.YMColumn\",);" + System.lineSeparator() +
				"whereParams.put(\"jdbc_tool_test_fullCaseMainEntity.DateTimeColumn\",);" + System.lineSeparator() +
				"whereParams.put(\"jdbc_tool_test_fullCaseMainEntity.NStringColumn\",);" + System.lineSeparator() +
				"whereParams.put(\"jdbc_tool_test_fullCaseMainEntity.CompanyCD\",);" + System.lineSeparator();
		String whereParamsPartsUKPK = "Object[] whereParams = new Object[]{,,,};" + System.lineSeparator();
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
		String updateExecuteApiStr = "update(\"jdbc_tool_test_fullCaseMainEntity\")" + System.lineSeparator() + ".set(record)" + System.lineSeparator();
		String executeApiWhereStr = ".where(expMap($($($(\"jdbc_tool_test_fullCaseMainEntity.objectID = '1001'\").AND(\"jdbc_tool_test_fullCaseMainEntity.StringColumn = '2002'\")).OR(\"jdbc_tool_test_fullCaseMainEntity.IntColumn = 3003\")).AND($($(\"jdbc_tool_test_fullCaseMainEntity.NStringColumn = :jdbc_tool_test_fullCaseMainEntity.NStringColumn\").AND(\"jdbc_tool_test_fullCaseMainEntity.DateTimeColumn = :jdbc_tool_test_fullCaseMainEntity.DateTimeColumn\")).OR(\"jdbc_tool_test_fullCaseMainEntity.YMColumn = :jdbc_tool_test_fullCaseMainEntity.YMColumn\").AND(\"jdbc_tool_test_fullCaseMainEntity.CompanyCD = $TRIM(:jdbc_tool_test_fullCaseMainEntity.CompanyCD)\")), whereParams))" + System.lineSeparator();
		assertEquals(whereParamsParts + updateRecord + System.lineSeparator() + "int count = " + updateExecuteApiStr + executeApiWhereStr + ".execute();", result.getData().get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE)));
		assertEquals(updateRecord + System.lineSeparator() + "int count = " + updateExecuteApiStr + ".wherePK()" + System.lineSeparator() + ".execute();", result.getData().get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_PK)));
		String executeUKStr = ".whereUK(\"MultipleColumnUK\")" + System.lineSeparator() + "//.whereUK(\"OtherUKName\")" + System.lineSeparator() + "//.whereUK(\"SingleColumnUK\")";
		assertEquals(updateRecord + System.lineSeparator() + "int count = " + updateExecuteApiStr + executeUKStr + System.lineSeparator() + ".execute();", result.getData().get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_UK)));
		
		StringBuilder updateRecordList = new StringBuilder();
		updateRecordList.append("List<GRecord> recordList = new ArrayList<>();").append(System.lineSeparator());
		updateRecordList.append(updateRecord);
		updateRecordList.append("recordList.add(record);").append(System.lineSeparator());
		
		String updateExecuteListApiStr = "update(\"jdbc_tool_test_fullCaseMainEntity\")" + System.lineSeparator() + ".set(recordList)" + System.lineSeparator();
		
		assertEquals(updateRecordList + System.lineSeparator() + "int count = " + updateExecuteListApiStr + ".wherePK()" + System.lineSeparator() + ".executeList();", result.getData().get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_PK)));
		assertEquals(updateRecordList + System.lineSeparator() + "int count = " + updateExecuteListApiStr + executeUKStr + System.lineSeparator() + ".executeList();", result.getData().get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_UK)));
		assertEquals(updateRecordList + System.lineSeparator() + "int count = " + updateExecuteListApiStr + ".wherePK()" + System.lineSeparator() + ".executeBatch();", result.getData().get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_PK)));
		assertEquals(updateRecordList + System.lineSeparator() + "int count = " + updateExecuteListApiStr + executeUKStr + System.lineSeparator() + ".executeBatch();", result.getData().get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_UK)));
		
		assertEquals(GCrudType.SELECT, result.getData().get(2).getType());
		assertEquals("仮想表ID: 8D0CDDE194344769B42F631F31BAFEFB", result.getData().get(2).getDescription());
		assertEquals("SELECT" + System.lineSeparator() +
				"    jdbc_tool_test_fullCaseMainEntity.objectID," + System.lineSeparator() +
				"    jdbc_tool_test_fullCaseMainEntity.StringColumn," + System.lineSeparator() +
				"    jdbc_tool_test_fullCaseMainEntity.IntColumn," + System.lineSeparator() +
				"    jdbc_tool_test_fullCaseMainEntity.NStringColumn," + System.lineSeparator() +
				"    jdbc_tool_test_fullCaseMainEntity.DateTimeColumn," + System.lineSeparator() +
				"    jdbc_tool_test_fullCaseMainEntity.YMColumn," + System.lineSeparator() +
				"    jdbc_tool_test_fullCaseMainEntity.CurrencyColumn," + System.lineSeparator() +
				"    jdbc_tool_test_fullCaseMainEntity.LongColumn," + System.lineSeparator() +
				"    jdbc_tool_test_fullCaseMainEntity.CompanyCD," + System.lineSeparator() +
				"    jdbc_tool_test_fullCaseMainEntity.ExclusiveFG," + System.lineSeparator() +
				"    jdbc_tool_test_fullCaseMainEntity.RegisteredPerson," + System.lineSeparator() +
				"    jdbc_tool_test_fullCaseMainEntity.RegisteredDT," + System.lineSeparator() +
				"    jdbc_tool_test_fullCaseMainEntity.UpdatedPerson," + System.lineSeparator() +
				"    jdbc_tool_test_fullCaseMainEntity.UpdatedDT" + System.lineSeparator() +
				"FROM" + System.lineSeparator() +
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
				"    )", result.getData().get(2).getSqlScript());
		
		GDao2VirtualEntity selectCodeResult = ((GDao2JdbcXFormCodeResult)result.getData().get(2)).getVirtualEntity();
		assertEquals(14, selectCodeResult.getColumns().size());
		assertEquals("-", selectCodeResult.getAbsoluteVirtualColumnCode());

		String selectApiStr = "select(\"jdbc_tool_test_fullCaseMainEntity\", \"jdbc_tool_test_fullCaseMainEntity\")" + System.lineSeparator() +
				".fields(colsAll())" + System.lineSeparator();
		String selectUKStr = ".whereUK(\"MultipleColumnUK\", whereParams)" + System.lineSeparator() + "//.whereUK(\"OtherUKName\", whereParams)" + System.lineSeparator() + "//.whereUK(\"SingleColumnUK\", whereParams)";
		assertEquals(whereParamsParts + "List<GRecord> result = " + selectApiStr + executeApiWhereStr + ".findList();", result.getData().get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE)));
		assertEquals(whereParamsPartsUKPK + "List<GRecord> result = " + selectApiStr + ".wherePK(whereParams)" + System.lineSeparator() + ".findList();", result.getData().get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE_PK)));
		assertEquals(whereParamsPartsUKPK + "List<GRecord> result = " + selectApiStr + selectUKStr + System.lineSeparator() + ".findList();", result.getData().get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE_UK)));
		
		assertEquals(whereParamsParts + "GRecord result = " + selectApiStr + executeApiWhereStr + ".findRecord();", result.getData().get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE)));
		assertEquals(whereParamsPartsUKPK + "GRecord result = " + selectApiStr + ".wherePK(whereParams)" + System.lineSeparator() + ".findRecord();", result.getData().get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE_PK)));
		assertEquals(whereParamsPartsUKPK + "GRecord result = " + selectApiStr + selectUKStr + System.lineSeparator() + ".findRecord();", result.getData().get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE_UK)));
		
		assertEquals(whereParamsParts + "GRecordSet result = " + selectApiStr + executeApiWhereStr + ".findRecordSet();", result.getData().get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE)));
		assertEquals(whereParamsPartsUKPK + "GRecordSet result = " + selectApiStr + ".wherePK(whereParams)" + System.lineSeparator() + ".findRecordSet();", result.getData().get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE_PK)));
		assertEquals(whereParamsPartsUKPK + "GRecordSet result = " + selectApiStr + selectUKStr + System.lineSeparator() + ".findRecordSet();", result.getData().get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE_UK)));
		
		assertEquals(GCrudType.DELETE, result.getData().get(3).getType());
		assertEquals("仮想表ID: 8D0CDDE194344769B42F631F31BAFEFB", result.getData().get(3).getDescription());
		assertEquals(getDeleteSql_testExport_expMapCase1(), result.getData().get(3).getSqlScript());
		
		GDao2VirtualEntity deleteCodeResult = ((GDao2JdbcXFormCodeResult)result.getData().get(3)).getVirtualEntity();
		assertEquals(14, deleteCodeResult.getColumns().size());
		assertEquals("-", selectCodeResult.getAbsoluteVirtualColumnCode());
		
		String deleteApiStr = "delete(\"jdbc_tool_test_fullCaseMainEntity\")" + System.lineSeparator();
		assertEquals(whereParamsParts + "int count = " + deleteApiStr + executeApiWhereStr + ".execute();", result.getData().get(3).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE)));
		
		String deleteRecordStr = "GRecord whereParams = createRecord();" + System.lineSeparator() +
				"whereParams.setObject(\"objectID\",);" + System.lineSeparator();
		
		assertEquals(deleteRecordStr + System.lineSeparator() + "int count = " + deleteApiStr + ".wherePK(whereParams)" + System.lineSeparator() + ".execute();", result.getData().get(3).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_PK)));
		String deleteRecordUKStr = "GRecord whereParams = createRecord();" + System.lineSeparator() +
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
		
		assertEquals(deleteRecordUKStr + System.lineSeparator() + "int count = " + deleteApiStr + selectUKStr + System.lineSeparator() + ".execute();", result.getData().get(3).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_UK)));
		
		StringBuilder deleteListRecordStr = new StringBuilder();
		deleteListRecordStr.append(deleteRecordStr);
		deleteListRecordStr.append("List<GRecord> whereParamsList = new ArrayList<>();").append(System.lineSeparator());
		deleteListRecordStr.append("whereParamsList.add(whereParams);").append(System.lineSeparator());
		
		StringBuilder deleteListRecordUKStr = new StringBuilder();
		deleteListRecordUKStr.append(deleteRecordUKStr);
		deleteListRecordUKStr.append("List<GRecord> whereParamsList = new ArrayList<>();").append(System.lineSeparator());
		deleteListRecordUKStr.append("whereParamsList.add(whereParams);").append(System.lineSeparator());
		String deleteListUKStr = ".whereUK(\"MultipleColumnUK\", whereParamsList)" + System.lineSeparator() + "//.whereUK(\"OtherUKName\", whereParamsList)" + System.lineSeparator() + "//.whereUK(\"SingleColumnUK\", whereParamsList)";
		
		assertNull(result.getData().get(3).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE)));
		assertEquals(deleteListRecordStr + System.lineSeparator() + "int count = " + deleteApiStr + ".wherePK(whereParamsList)" + System.lineSeparator() + ".executeList();", result.getData().get(3).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_PK)));
		assertEquals(deleteListRecordUKStr + System.lineSeparator() + "int count = " + deleteApiStr + deleteListUKStr + System.lineSeparator() + ".executeList();", result.getData().get(3).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_UK)));
		assertNull(result.getData().get(3).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE)));
		assertEquals(deleteListRecordStr + System.lineSeparator() + "int count = " + deleteApiStr + ".wherePK(whereParamsList)" + System.lineSeparator() + ".executeBatch();", result.getData().get(3).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_PK)));
		assertEquals(deleteListRecordUKStr + System.lineSeparator() + "int count = " + deleteApiStr + deleteListUKStr + System.lineSeparator() + ".executeBatch();", result.getData().get(3).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_UK)));
	}
	
	protected String getDeleteSql_testExport_expMapCase1() {
		return "DELETE FROM" + System.lineSeparator() +
				"    " + dbSchema + ".jdbc_tool_test_fullCaseMainEntity" + System.lineSeparator() +
				"WHERE" + System.lineSeparator() +
				"    (" + System.lineSeparator() +
				"        (" + System.lineSeparator() +
				"            " + dbSchema + ".jdbc_tool_test_fullCaseMainEntity.objectID = '1001'" + System.lineSeparator() +
				"            AND " + dbSchema + ".jdbc_tool_test_fullCaseMainEntity.StringColumn = '2002'" + System.lineSeparator() +
				"        )" + System.lineSeparator() +
				"        OR " + dbSchema + ".jdbc_tool_test_fullCaseMainEntity.IntColumn = 3003" + System.lineSeparator() +
				"    )" + System.lineSeparator() +
				"    AND (" + System.lineSeparator() +
				"        (" + System.lineSeparator() +
				"            " + dbSchema + ".jdbc_tool_test_fullCaseMainEntity.NStringColumn = ?" + System.lineSeparator() +
				"            AND " + dbSchema + ".jdbc_tool_test_fullCaseMainEntity.DateTimeColumn = ?" + System.lineSeparator() +
				"        )" + System.lineSeparator() +
				"        OR " + dbSchema + ".jdbc_tool_test_fullCaseMainEntity.YMColumn = ?" + System.lineSeparator() +
				"        AND " + dbSchema + ".jdbc_tool_test_fullCaseMainEntity.CompanyCD = ?" + System.lineSeparator() +
				"    )";
	}
	
	@Test
	public void testExport_expMapCase2() {
		
		// パターン２：where (a = ? and b = ?) or (c = ? and d = ?)
		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_expmapcase2.xml");
		String conditionFilePath = getConditionFilePath("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_expmapcase2.yaml");
		GExporterResult<List<GXFormCodeResult>, byte[]> result = service.export(conditionFilePath, settingsFilePath);
		
		assertEquals("html", result.getMediaType());
		assertEquals(GCrudType.INSERT, result.getData().get(0).getType());
		assertEquals("仮想表ID: 055EB42884BA4FDBA154640B13B4D060", result.getData().get(0).getDescription());
		assertEquals("INSERT INTO" + System.lineSeparator() +
				"    " + dbSchema + ".jdbc_tool_test_fullCaseMainEntity(objectID, StringColumn, IntColumn, NStringColumn, DateTimeColumn, YMColumn, CurrencyColumn, LongColumn, CompanyCD, ExclusiveFG, RegisteredPerson, UpdatedPerson, RegisteredDT, UpdatedDT)" + System.lineSeparator() +
				"VALUES" + System.lineSeparator() +
				"(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " + getTimestampCommand() + ", " + getTimestampCommand() + ")"
				, result.getData().get(0).getSqlScript());
		
		GDao2VirtualEntity insertCodeResult = ((GDao2JdbcXFormCodeResult)result.getData().get(0)).getVirtualEntity();
		assertEquals(14, insertCodeResult.getColumns().size());
		assertEquals("-", insertCodeResult.getAbsoluteVirtualColumnCode());

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
		String insertExecuteApiStr = "insert(\"jdbc_tool_test_fullCaseMainEntity\")" + System.lineSeparator() +  ".values(record)";
		
		assertEquals(insertRecord + System.lineSeparator() + "int count = " + insertExecuteApiStr + System.lineSeparator() + ".execute();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, null)));
		assertNull(result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE)));
		assertNull(result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_PK)));
		assertNull(result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_UK)));
		
		String insertExecuteBatchApiStr = "insert(\"jdbc_tool_test_fullCaseMainEntity\")" + System.lineSeparator() + ".values(recordList)";
		StringBuilder insertRecordList = new StringBuilder();
		insertRecordList.append("List<GRecord> recordList = new ArrayList<>();").append(System.lineSeparator());
		insertRecordList.append(insertRecord);
		insertRecordList.append("recordList.add(record);").append(System.lineSeparator());
		
		assertEquals(insertRecordList + System.lineSeparator() + "int count = " + insertExecuteBatchApiStr + System.lineSeparator() + ".executeBatch();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, null)));
		assertNull(result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE)));
		assertNull(result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_PK)));
		assertNull(result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_UK)));
		assertEquals(insertRecordList + System.lineSeparator() + "int count = " + insertExecuteBatchApiStr + System.lineSeparator() + ".executeList();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, null)));
		assertNull(result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE)));
		assertNull(result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_PK)));
		assertNull(result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_UK)));
		
		
		assertEquals(GCrudType.UPDATE, result.getData().get(1).getType());
		assertEquals("仮想表ID: 055EB42884BA4FDBA154640B13B4D060", result.getData().get(1).getDescription());
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
				"    )", result.getData().get(1).getSqlScript());
		
		GDao2VirtualEntity updateCodeResult = ((GDao2JdbcXFormCodeResult)result.getData().get(1)).getVirtualEntity();
		assertEquals(14, updateCodeResult.getColumns().size());
		assertEquals("-", updateCodeResult.getAbsoluteVirtualColumnCode());

		String whereParamsParts = "Map<String, Object> whereParams = new HashMap<>();" + System.lineSeparator() +
				"whereParams.put(\"jdbc_tool_test_fullCaseMainEntity.YMColumn\",);" + System.lineSeparator() +
				"whereParams.put(\"jdbc_tool_test_fullCaseMainEntity.DateTimeColumn\",);" + System.lineSeparator() +
				"whereParams.put(\"jdbc_tool_test_fullCaseMainEntity.LongColumn\",);" + System.lineSeparator() +
				"whereParams.put(\"jdbc_tool_test_fullCaseMainEntity.CompanyCD\",);" + System.lineSeparator() +
				"whereParams.put(\"jdbc_tool_test_fullCaseMainEntity.CurrencyColumn\",);" + System.lineSeparator();
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
		String updateExecuteApiStr = "update(\"jdbc_tool_test_fullCaseMainEntity\")" + System.lineSeparator() + ".set(record)" + System.lineSeparator();
		String executeApiWhereStr = ".where(expMap($($($(\"jdbc_tool_test_fullCaseMainEntity.objectID = '1001'\").AND(\"jdbc_tool_test_fullCaseMainEntity.StringColumn = '2002'\")).OR($(\"jdbc_tool_test_fullCaseMainEntity.IntColumn = 3003\").AND(\"jdbc_tool_test_fullCaseMainEntity.NStringColumn = '4004'\"))).AND($($(\"jdbc_tool_test_fullCaseMainEntity.DateTimeColumn = :jdbc_tool_test_fullCaseMainEntity.DateTimeColumn\").AND(\"jdbc_tool_test_fullCaseMainEntity.YMColumn = :jdbc_tool_test_fullCaseMainEntity.YMColumn\")).OR($(\"jdbc_tool_test_fullCaseMainEntity.CurrencyColumn = :jdbc_tool_test_fullCaseMainEntity.CurrencyColumn\").AND(\"jdbc_tool_test_fullCaseMainEntity.LongColumn = :jdbc_tool_test_fullCaseMainEntity.LongColumn\")).AND(\"jdbc_tool_test_fullCaseMainEntity.CompanyCD = $TRIM(:jdbc_tool_test_fullCaseMainEntity.CompanyCD)\")), whereParams))" + System.lineSeparator();
		assertEquals(whereParamsParts + updateRecord + System.lineSeparator() + "int count = " + updateExecuteApiStr + executeApiWhereStr + ".execute();", result.getData().get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE)));
		assertEquals(updateRecord + System.lineSeparator() + "int count = " + updateExecuteApiStr + ".wherePK()" + System.lineSeparator() + ".execute();", result.getData().get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_PK)));
		String executeUKStr = ".whereUK(\"MultipleColumnUK\")" + System.lineSeparator() + "//.whereUK(\"OtherUKName\")" + System.lineSeparator() + "//.whereUK(\"SingleColumnUK\")";
		assertEquals(updateRecord + System.lineSeparator() + "int count = " + updateExecuteApiStr + executeUKStr + System.lineSeparator() + ".execute();", result.getData().get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_UK)));
		
		StringBuilder updateRecordList = new StringBuilder();
		updateRecordList.append("List<GRecord> recordList = new ArrayList<>();").append(System.lineSeparator());
		updateRecordList.append(updateRecord);
		updateRecordList.append("recordList.add(record);").append(System.lineSeparator());
		
		String updateExecuteListApiStr = "update(\"jdbc_tool_test_fullCaseMainEntity\")" + System.lineSeparator() + ".set(recordList)" + System.lineSeparator();
		
		assertEquals(updateRecordList + System.lineSeparator() + "int count = " + updateExecuteListApiStr + ".wherePK()" + System.lineSeparator() + ".executeList();", result.getData().get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_PK)));
		assertEquals(updateRecordList + System.lineSeparator() + "int count = " + updateExecuteListApiStr + executeUKStr + System.lineSeparator() + ".executeList();", result.getData().get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_UK)));
		assertEquals(updateRecordList + System.lineSeparator() + "int count = " + updateExecuteListApiStr + ".wherePK()" + System.lineSeparator() + ".executeBatch();", result.getData().get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_PK)));
		assertEquals(updateRecordList + System.lineSeparator() + "int count = " + updateExecuteListApiStr + executeUKStr + System.lineSeparator() + ".executeBatch();", result.getData().get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_UK)));
		
		assertEquals(GCrudType.SELECT, result.getData().get(2).getType());
		assertEquals("仮想表ID: 055EB42884BA4FDBA154640B13B4D060", result.getData().get(2).getDescription());
		assertEquals("SELECT" + System.lineSeparator() +
				"    jdbc_tool_test_fullCaseMainEntity.objectID," + System.lineSeparator() +
				"    jdbc_tool_test_fullCaseMainEntity.StringColumn," + System.lineSeparator() +
				"    jdbc_tool_test_fullCaseMainEntity.IntColumn," + System.lineSeparator() +
				"    jdbc_tool_test_fullCaseMainEntity.NStringColumn," + System.lineSeparator() +
				"    jdbc_tool_test_fullCaseMainEntity.DateTimeColumn," + System.lineSeparator() +
				"    jdbc_tool_test_fullCaseMainEntity.YMColumn," + System.lineSeparator() +
				"    jdbc_tool_test_fullCaseMainEntity.CurrencyColumn," + System.lineSeparator() +
				"    jdbc_tool_test_fullCaseMainEntity.LongColumn," + System.lineSeparator() +
				"    jdbc_tool_test_fullCaseMainEntity.CompanyCD," + System.lineSeparator() +
				"    jdbc_tool_test_fullCaseMainEntity.ExclusiveFG," + System.lineSeparator() +
				"    jdbc_tool_test_fullCaseMainEntity.RegisteredPerson," + System.lineSeparator() +
				"    jdbc_tool_test_fullCaseMainEntity.RegisteredDT," + System.lineSeparator() +
				"    jdbc_tool_test_fullCaseMainEntity.UpdatedPerson," + System.lineSeparator() +
				"    jdbc_tool_test_fullCaseMainEntity.UpdatedDT" + System.lineSeparator() +
				"FROM" + System.lineSeparator() +
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
				"    )", result.getData().get(2).getSqlScript());
		
		GDao2VirtualEntity selectCodeResult = ((GDao2JdbcXFormCodeResult)result.getData().get(2)).getVirtualEntity();
		assertEquals(14, selectCodeResult.getColumns().size());
		assertEquals("-", selectCodeResult.getAbsoluteVirtualColumnCode());

		String selectApiStr = "select(\"jdbc_tool_test_fullCaseMainEntity\", \"jdbc_tool_test_fullCaseMainEntity\")" + System.lineSeparator() +
				".fields(colsAll())" + System.lineSeparator();
		String whereParamsPartsUKPK = "Object[] whereParams = new Object[]{,,,,};" + System.lineSeparator();
		String selectUKStr = ".whereUK(\"MultipleColumnUK\", whereParams)" + System.lineSeparator() + "//.whereUK(\"OtherUKName\", whereParams)" + System.lineSeparator() + "//.whereUK(\"SingleColumnUK\", whereParams)";
		assertEquals(whereParamsParts + "List<GRecord> result = " + selectApiStr + executeApiWhereStr + ".findList();", result.getData().get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE)));
		assertEquals(whereParamsPartsUKPK + "List<GRecord> result = " + selectApiStr + ".wherePK(whereParams)" + System.lineSeparator() + ".findList();", result.getData().get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE_PK)));
		assertEquals(whereParamsPartsUKPK + "List<GRecord> result = " + selectApiStr + selectUKStr + System.lineSeparator() + ".findList();", result.getData().get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE_UK)));
		
		assertEquals(whereParamsParts + "GRecord result = " + selectApiStr + executeApiWhereStr + ".findRecord();", result.getData().get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE)));
		assertEquals(whereParamsPartsUKPK + "GRecord result = " + selectApiStr + ".wherePK(whereParams)" + System.lineSeparator() + ".findRecord();", result.getData().get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE_PK)));
		assertEquals(whereParamsPartsUKPK + "GRecord result = " + selectApiStr + selectUKStr + System.lineSeparator() + ".findRecord();", result.getData().get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE_UK)));
		
		assertEquals(whereParamsParts + "GRecordSet result = " + selectApiStr + executeApiWhereStr + ".findRecordSet();", result.getData().get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE)));
		assertEquals(whereParamsPartsUKPK + "GRecordSet result = " + selectApiStr + ".wherePK(whereParams)" + System.lineSeparator() + ".findRecordSet();", result.getData().get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE_PK)));
		assertEquals(whereParamsPartsUKPK + "GRecordSet result = " + selectApiStr + selectUKStr + System.lineSeparator() + ".findRecordSet();", result.getData().get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE_UK)));
		
		assertEquals(GCrudType.DELETE, result.getData().get(3).getType());
		assertEquals("仮想表ID: 055EB42884BA4FDBA154640B13B4D060", result.getData().get(3).getDescription());
		assertEquals(getDeleteSql_testExport_expMapCase2(), result.getData().get(3).getSqlScript());
		
		GDao2VirtualEntity deleteCodeResult = ((GDao2JdbcXFormCodeResult)result.getData().get(3)).getVirtualEntity();
		assertEquals(14, deleteCodeResult.getColumns().size());
		assertEquals("-", selectCodeResult.getAbsoluteVirtualColumnCode());
		
		String deleteApiStr = "delete(\"jdbc_tool_test_fullCaseMainEntity\")" + System.lineSeparator();
		assertEquals(whereParamsParts + "int count = " + deleteApiStr + executeApiWhereStr + ".execute();", result.getData().get(3).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE)));
		
		String deleteRecordStr = "GRecord whereParams = createRecord();" + System.lineSeparator() +
				"whereParams.setObject(\"objectID\",);" + System.lineSeparator();
		
		assertEquals(deleteRecordStr + System.lineSeparator() + "int count = " + deleteApiStr + ".wherePK(whereParams)" + System.lineSeparator() + ".execute();", result.getData().get(3).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_PK)));
		String deleteRecordUKStr = "GRecord whereParams = createRecord();" + System.lineSeparator() +
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
		
		assertEquals(deleteRecordUKStr + System.lineSeparator() + "int count = " + deleteApiStr + selectUKStr + System.lineSeparator() + ".execute();", result.getData().get(3).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_UK)));
		
		StringBuilder deleteListRecordStr = new StringBuilder();
		deleteListRecordStr.append(deleteRecordStr);
		deleteListRecordStr.append("List<GRecord> whereParamsList = new ArrayList<>();").append(System.lineSeparator());
		deleteListRecordStr.append("whereParamsList.add(whereParams);").append(System.lineSeparator());
		
		StringBuilder deleteListRecordUKStr = new StringBuilder();
		deleteListRecordUKStr.append(deleteRecordUKStr);
		deleteListRecordUKStr.append("List<GRecord> whereParamsList = new ArrayList<>();").append(System.lineSeparator());
		deleteListRecordUKStr.append("whereParamsList.add(whereParams);").append(System.lineSeparator());
		String deleteListUKStr = ".whereUK(\"MultipleColumnUK\", whereParamsList)" + System.lineSeparator() + "//.whereUK(\"OtherUKName\", whereParamsList)" + System.lineSeparator() + "//.whereUK(\"SingleColumnUK\", whereParamsList)";
		
		assertNull(result.getData().get(3).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE)));
		assertEquals(deleteListRecordStr + System.lineSeparator() + "int count = " + deleteApiStr + ".wherePK(whereParamsList)" + System.lineSeparator() + ".executeList();", result.getData().get(3).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_PK)));
		assertEquals(deleteListRecordUKStr + System.lineSeparator() + "int count = " + deleteApiStr + deleteListUKStr + System.lineSeparator() + ".executeList();", result.getData().get(3).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_UK)));
		assertNull(result.getData().get(3).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE)));
		assertEquals(deleteListRecordStr + System.lineSeparator() + "int count = " + deleteApiStr + ".wherePK(whereParamsList)" + System.lineSeparator() + ".executeBatch();", result.getData().get(3).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_PK)));
		assertEquals(deleteListRecordUKStr + System.lineSeparator() + "int count = " + deleteApiStr + deleteListUKStr + System.lineSeparator() + ".executeBatch();", result.getData().get(3).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_UK)));
	}
	
	protected String getDeleteSql_testExport_expMapCase2() {
		return "DELETE FROM" + System.lineSeparator() +
				"    " + dbSchema + ".jdbc_tool_test_fullCaseMainEntity" + System.lineSeparator() +
				"WHERE" + System.lineSeparator() +
				"    (" + System.lineSeparator() +
				"        (" + System.lineSeparator() +
				"            " + dbSchema + ".jdbc_tool_test_fullCaseMainEntity.objectID = '1001'" + System.lineSeparator() +
				"            AND " + dbSchema + ".jdbc_tool_test_fullCaseMainEntity.StringColumn = '2002'" + System.lineSeparator() +
				"        )" + System.lineSeparator() +
				"        OR (" + System.lineSeparator() +
				"            " + dbSchema + ".jdbc_tool_test_fullCaseMainEntity.IntColumn = 3003" + System.lineSeparator() +
				"            AND " + dbSchema + ".jdbc_tool_test_fullCaseMainEntity.NStringColumn = '4004'" + System.lineSeparator() +
				"        )" + System.lineSeparator() +
				"    )" + System.lineSeparator() +
				"    AND (" + System.lineSeparator() +
				"        (" + System.lineSeparator() +
				"            " + dbSchema + ".jdbc_tool_test_fullCaseMainEntity.DateTimeColumn = ?" + System.lineSeparator() +
				"            AND " + dbSchema + ".jdbc_tool_test_fullCaseMainEntity.YMColumn = ?" + System.lineSeparator() +
				"        )" + System.lineSeparator() +
				"        OR (" + System.lineSeparator() +
				"            " + dbSchema + ".jdbc_tool_test_fullCaseMainEntity.CurrencyColumn = ?" + System.lineSeparator() +
				"            AND " + dbSchema + ".jdbc_tool_test_fullCaseMainEntity.LongColumn = ?" + System.lineSeparator() +
				"        )" + System.lineSeparator() +
				"        AND " + dbSchema + ".jdbc_tool_test_fullCaseMainEntity.CompanyCD = ?" + System.lineSeparator() +
				"    )";
	}
	
	@Test
	public void testExport_expMapCase3() {
		
		// パターン３：where (a = ? and b = ?) or (c = ? and d = ? and (e = ? and f = ?))
		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_expmapcase3.xml");
		String conditionFilePath = getConditionFilePath("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_expmapcase3.yaml");
		GExporterResult<List<GXFormCodeResult>, byte[]> result = service.export(conditionFilePath, settingsFilePath);
		
		assertEquals("html", result.getMediaType());
		assertEquals(GCrudType.INSERT, result.getData().get(0).getType());
		assertEquals("仮想表ID: E706EFFF592B4C009DB35F8D381B4169", result.getData().get(0).getDescription());
		assertEquals("INSERT INTO" + System.lineSeparator() +
				"    " + dbSchema + ".jdbc_tool_test_fullCaseMainEntity(objectID, StringColumn, IntColumn, NStringColumn, DateTimeColumn, YMColumn, CurrencyColumn, LongColumn, CompanyCD, ExclusiveFG, RegisteredPerson, UpdatedPerson, RegisteredDT, UpdatedDT)" + System.lineSeparator() +
				"VALUES" + System.lineSeparator() +
				"(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " + getTimestampCommand() + ", " + getTimestampCommand() + ")"
				, result.getData().get(0).getSqlScript());
		
		GDao2VirtualEntity insertCodeResult = ((GDao2JdbcXFormCodeResult)result.getData().get(0)).getVirtualEntity();
		assertEquals(14, insertCodeResult.getColumns().size());
		assertEquals("-", insertCodeResult.getAbsoluteVirtualColumnCode());

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
		String insertExecuteApiStr = "insert(\"jdbc_tool_test_fullCaseMainEntity\")" + System.lineSeparator() +  ".values(record)";
		
		assertEquals(insertRecord + System.lineSeparator() + "int count = " + insertExecuteApiStr + System.lineSeparator() + ".execute();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, null)));
		assertNull(result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE)));
		assertNull(result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_PK)));
		assertNull(result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_UK)));
		
		String insertExecuteBatchApiStr = "insert(\"jdbc_tool_test_fullCaseMainEntity\")" + System.lineSeparator() + ".values(recordList)";
		StringBuilder insertRecordList = new StringBuilder();
		insertRecordList.append("List<GRecord> recordList = new ArrayList<>();").append(System.lineSeparator());
		insertRecordList.append(insertRecord);
		insertRecordList.append("recordList.add(record);").append(System.lineSeparator());
		
		assertEquals(insertRecordList + System.lineSeparator() + "int count = " + insertExecuteBatchApiStr + System.lineSeparator() + ".executeBatch();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, null)));
		assertNull(result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE)));
		assertNull(result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_PK)));
		assertNull(result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_UK)));
		assertEquals(insertRecordList + System.lineSeparator() + "int count = " + insertExecuteBatchApiStr + System.lineSeparator() + ".executeList();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, null)));
		assertNull(result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE)));
		assertNull(result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_PK)));
		assertNull(result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_UK)));
		
		
		assertEquals(GCrudType.UPDATE, result.getData().get(1).getType());
		assertEquals("仮想表ID: E706EFFF592B4C009DB35F8D381B4169", result.getData().get(1).getDescription());
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
				"    )", result.getData().get(1).getSqlScript());
		
		GDao2VirtualEntity updateCodeResult = ((GDao2JdbcXFormCodeResult)result.getData().get(1)).getVirtualEntity();
		assertEquals(14, updateCodeResult.getColumns().size());
		assertEquals("-", updateCodeResult.getAbsoluteVirtualColumnCode());

		String whereParamsParts = "Map<String, Object> whereParams = new HashMap<>();" + System.lineSeparator() +
				"whereParams.put(\"jdbc_tool_test_fullCaseMainEntity.RegisteredPerson\",);" + System.lineSeparator() +
				"whereParams.put(\"jdbc_tool_test_fullCaseMainEntity.LongColumn\",);" + System.lineSeparator() +
				"whereParams.put(\"jdbc_tool_test_fullCaseMainEntity.CompanyCD\",);" + System.lineSeparator() +
				"whereParams.put(\"jdbc_tool_test_fullCaseMainEntity.ExclusiveFG\",);" + System.lineSeparator() +
				"whereParams.put(\"jdbc_tool_test_fullCaseMainEntity.RegisteredDT\",);" + System.lineSeparator() +
				"whereParams.put(\"jdbc_tool_test_fullCaseMainEntity.CurrencyColumn\",);" + System.lineSeparator();
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
		String updateExecuteApiStr = "update(\"jdbc_tool_test_fullCaseMainEntity\")" + System.lineSeparator() + ".set(record)" + System.lineSeparator();
		String executeApiWhereStr = ".where(expMap($($($(\"jdbc_tool_test_fullCaseMainEntity.objectID = '1001'\").AND(\"jdbc_tool_test_fullCaseMainEntity.StringColumn = '2002'\")).OR($(\"jdbc_tool_test_fullCaseMainEntity.IntColumn = 3003\").AND(\"jdbc_tool_test_fullCaseMainEntity.NStringColumn = '4004'\").OR($(\"jdbc_tool_test_fullCaseMainEntity.DateTimeColumn = '5005'\").AND(\"jdbc_tool_test_fullCaseMainEntity.YMColumn = '6006'\")))).AND($($(\"jdbc_tool_test_fullCaseMainEntity.CurrencyColumn = :jdbc_tool_test_fullCaseMainEntity.CurrencyColumn\").AND(\"jdbc_tool_test_fullCaseMainEntity.LongColumn = :jdbc_tool_test_fullCaseMainEntity.LongColumn\")).OR($(\"jdbc_tool_test_fullCaseMainEntity.CompanyCD = :jdbc_tool_test_fullCaseMainEntity.CompanyCD\").AND(\"jdbc_tool_test_fullCaseMainEntity.ExclusiveFG = :jdbc_tool_test_fullCaseMainEntity.ExclusiveFG\").OR($(\"jdbc_tool_test_fullCaseMainEntity.RegisteredPerson = :jdbc_tool_test_fullCaseMainEntity.RegisteredPerson\").AND(\"jdbc_tool_test_fullCaseMainEntity.RegisteredDT = :jdbc_tool_test_fullCaseMainEntity.RegisteredDT\")))), whereParams))" + System.lineSeparator();
		assertEquals(whereParamsParts + updateRecord + System.lineSeparator() + "int count = " + updateExecuteApiStr + executeApiWhereStr + ".execute();", result.getData().get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE)));
		assertEquals(updateRecord + System.lineSeparator() + "int count = " + updateExecuteApiStr + ".wherePK()" + System.lineSeparator() + ".execute();", result.getData().get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_PK)));
		String executeUKStr = ".whereUK(\"MultipleColumnUK\")" + System.lineSeparator() + "//.whereUK(\"OtherUKName\")" + System.lineSeparator() + "//.whereUK(\"SingleColumnUK\")";
		assertEquals(updateRecord + System.lineSeparator() + "int count = " + updateExecuteApiStr + executeUKStr + System.lineSeparator() + ".execute();", result.getData().get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_UK)));
		
		StringBuilder updateRecordList = new StringBuilder();
		updateRecordList.append("List<GRecord> recordList = new ArrayList<>();").append(System.lineSeparator());
		updateRecordList.append(updateRecord);
		updateRecordList.append("recordList.add(record);").append(System.lineSeparator());
		
		String updateExecuteListApiStr = "update(\"jdbc_tool_test_fullCaseMainEntity\")" + System.lineSeparator() + ".set(recordList)" + System.lineSeparator();
		
		assertEquals(updateRecordList + System.lineSeparator() + "int count = " + updateExecuteListApiStr + ".wherePK()" + System.lineSeparator() + ".executeList();", result.getData().get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_PK)));
		assertEquals(updateRecordList + System.lineSeparator() + "int count = " + updateExecuteListApiStr + executeUKStr + System.lineSeparator() + ".executeList();", result.getData().get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_UK)));
		assertEquals(updateRecordList + System.lineSeparator() + "int count = " + updateExecuteListApiStr + ".wherePK()" + System.lineSeparator() + ".executeBatch();", result.getData().get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_PK)));
		assertEquals(updateRecordList + System.lineSeparator() + "int count = " + updateExecuteListApiStr + executeUKStr + System.lineSeparator() + ".executeBatch();", result.getData().get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_UK)));
		
		assertEquals(GCrudType.SELECT, result.getData().get(2).getType());
		assertEquals("仮想表ID: E706EFFF592B4C009DB35F8D381B4169", result.getData().get(2).getDescription());
		assertEquals("SELECT" + System.lineSeparator() +
				"    jdbc_tool_test_fullCaseMainEntity.objectID," + System.lineSeparator() +
				"    jdbc_tool_test_fullCaseMainEntity.StringColumn," + System.lineSeparator() +
				"    jdbc_tool_test_fullCaseMainEntity.IntColumn," + System.lineSeparator() +
				"    jdbc_tool_test_fullCaseMainEntity.NStringColumn," + System.lineSeparator() +
				"    jdbc_tool_test_fullCaseMainEntity.DateTimeColumn," + System.lineSeparator() +
				"    jdbc_tool_test_fullCaseMainEntity.YMColumn," + System.lineSeparator() +
				"    jdbc_tool_test_fullCaseMainEntity.CurrencyColumn," + System.lineSeparator() +
				"    jdbc_tool_test_fullCaseMainEntity.LongColumn," + System.lineSeparator() +
				"    jdbc_tool_test_fullCaseMainEntity.CompanyCD," + System.lineSeparator() +
				"    jdbc_tool_test_fullCaseMainEntity.ExclusiveFG," + System.lineSeparator() +
				"    jdbc_tool_test_fullCaseMainEntity.RegisteredPerson," + System.lineSeparator() +
				"    jdbc_tool_test_fullCaseMainEntity.RegisteredDT," + System.lineSeparator() +
				"    jdbc_tool_test_fullCaseMainEntity.UpdatedPerson," + System.lineSeparator() +
				"    jdbc_tool_test_fullCaseMainEntity.UpdatedDT" + System.lineSeparator() +
				"FROM" + System.lineSeparator() +
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
				"    )", result.getData().get(2).getSqlScript());
		
		GDao2VirtualEntity selectCodeResult = ((GDao2JdbcXFormCodeResult)result.getData().get(2)).getVirtualEntity();
		assertEquals(14, selectCodeResult.getColumns().size());
		assertEquals("-", selectCodeResult.getAbsoluteVirtualColumnCode());

		String selectApiStr = "select(\"jdbc_tool_test_fullCaseMainEntity\", \"jdbc_tool_test_fullCaseMainEntity\")" + System.lineSeparator() +
				".fields(colsAll())" + System.lineSeparator();
		String whereParamsPartsUKPK = "Object[] whereParams = new Object[]{,,,,,};" + System.lineSeparator();
		String selectUKStr = ".whereUK(\"MultipleColumnUK\", whereParams)" + System.lineSeparator() + "//.whereUK(\"OtherUKName\", whereParams)" + System.lineSeparator() + "//.whereUK(\"SingleColumnUK\", whereParams)";
		assertEquals(whereParamsParts + "List<GRecord> result = " + selectApiStr + executeApiWhereStr + ".findList();", result.getData().get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE)));
		assertEquals(whereParamsPartsUKPK + "List<GRecord> result = " + selectApiStr + ".wherePK(whereParams)" + System.lineSeparator() + ".findList();", result.getData().get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE_PK)));
		assertEquals(whereParamsPartsUKPK + "List<GRecord> result = " + selectApiStr + selectUKStr + System.lineSeparator() + ".findList();", result.getData().get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE_UK)));
		
		assertEquals(whereParamsParts + "GRecord result = " + selectApiStr + executeApiWhereStr + ".findRecord();", result.getData().get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE)));
		assertEquals(whereParamsPartsUKPK + "GRecord result = " + selectApiStr + ".wherePK(whereParams)" + System.lineSeparator() + ".findRecord();", result.getData().get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE_PK)));
		assertEquals(whereParamsPartsUKPK + "GRecord result = " + selectApiStr + selectUKStr + System.lineSeparator() + ".findRecord();", result.getData().get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE_UK)));
		
		assertEquals(whereParamsParts + "GRecordSet result = " + selectApiStr + executeApiWhereStr + ".findRecordSet();", result.getData().get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE)));
		assertEquals(whereParamsPartsUKPK + "GRecordSet result = " + selectApiStr + ".wherePK(whereParams)" + System.lineSeparator() + ".findRecordSet();", result.getData().get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE_PK)));
		assertEquals(whereParamsPartsUKPK + "GRecordSet result = " + selectApiStr + selectUKStr + System.lineSeparator() + ".findRecordSet();", result.getData().get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE_UK)));
		
		assertEquals(GCrudType.DELETE, result.getData().get(3).getType());
		assertEquals("仮想表ID: E706EFFF592B4C009DB35F8D381B4169", result.getData().get(3).getDescription());
		assertEquals(getDeleteSql_testExport_expMapCase3(), result.getData().get(3).getSqlScript());
		
		GDao2VirtualEntity deleteCodeResult = ((GDao2JdbcXFormCodeResult)result.getData().get(3)).getVirtualEntity();
		assertEquals(14, deleteCodeResult.getColumns().size());
		assertEquals("-", selectCodeResult.getAbsoluteVirtualColumnCode());
		
		String deleteApiStr = "delete(\"jdbc_tool_test_fullCaseMainEntity\")" + System.lineSeparator();
		assertEquals(whereParamsParts + "int count = " + deleteApiStr + executeApiWhereStr + ".execute();", result.getData().get(3).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE)));
		
		String deleteRecordStr = "GRecord whereParams = createRecord();" + System.lineSeparator() +
				"whereParams.setObject(\"objectID\",);" + System.lineSeparator();
		
		assertEquals(deleteRecordStr + System.lineSeparator() + "int count = " + deleteApiStr + ".wherePK(whereParams)" + System.lineSeparator() + ".execute();", result.getData().get(3).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_PK)));
		String deleteRecordUKStr = "GRecord whereParams = createRecord();" + System.lineSeparator() +
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
		
		assertEquals(deleteRecordUKStr + System.lineSeparator() + "int count = " + deleteApiStr + selectUKStr + System.lineSeparator() + ".execute();", result.getData().get(3).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_UK)));
		
		StringBuilder deleteListRecordStr = new StringBuilder();
		deleteListRecordStr.append(deleteRecordStr);
		deleteListRecordStr.append("List<GRecord> whereParamsList = new ArrayList<>();").append(System.lineSeparator());
		deleteListRecordStr.append("whereParamsList.add(whereParams);").append(System.lineSeparator());
		
		StringBuilder deleteListRecordUKStr = new StringBuilder();
		deleteListRecordUKStr.append(deleteRecordUKStr);
		deleteListRecordUKStr.append("List<GRecord> whereParamsList = new ArrayList<>();").append(System.lineSeparator());
		deleteListRecordUKStr.append("whereParamsList.add(whereParams);").append(System.lineSeparator());
		String deleteListUKStr = ".whereUK(\"MultipleColumnUK\", whereParamsList)" + System.lineSeparator() + "//.whereUK(\"OtherUKName\", whereParamsList)" + System.lineSeparator() + "//.whereUK(\"SingleColumnUK\", whereParamsList)";
		
		assertNull(result.getData().get(3).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE)));
		assertEquals(deleteListRecordStr + System.lineSeparator() + "int count = " + deleteApiStr + ".wherePK(whereParamsList)" + System.lineSeparator() + ".executeList();", result.getData().get(3).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_PK)));
		assertEquals(deleteListRecordUKStr + System.lineSeparator() + "int count = " + deleteApiStr + deleteListUKStr + System.lineSeparator() + ".executeList();", result.getData().get(3).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_UK)));
		assertNull(result.getData().get(3).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE)));
		assertEquals(deleteListRecordStr + System.lineSeparator() + "int count = " + deleteApiStr + ".wherePK(whereParamsList)" + System.lineSeparator() + ".executeBatch();", result.getData().get(3).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_PK)));
		assertEquals(deleteListRecordUKStr + System.lineSeparator() + "int count = " + deleteApiStr + deleteListUKStr + System.lineSeparator() + ".executeBatch();", result.getData().get(3).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_UK)));
	}
	
	protected String getDeleteSql_testExport_expMapCase3() {
		return "DELETE FROM" + System.lineSeparator() +
				"    " + dbSchema + ".jdbc_tool_test_fullCaseMainEntity" + System.lineSeparator() +
				"WHERE" + System.lineSeparator() +
				"    (" + System.lineSeparator() +
				"        (" + System.lineSeparator() +
				"            " + dbSchema + ".jdbc_tool_test_fullCaseMainEntity.objectID = '1001'" + System.lineSeparator() +
				"            AND " + dbSchema + ".jdbc_tool_test_fullCaseMainEntity.StringColumn = '2002'" + System.lineSeparator() +
				"        )" + System.lineSeparator() +
				"        OR (" + System.lineSeparator() +
				"            " + dbSchema + ".jdbc_tool_test_fullCaseMainEntity.IntColumn = 3003" + System.lineSeparator() +
				"            AND " + dbSchema + ".jdbc_tool_test_fullCaseMainEntity.NStringColumn = '4004'" + System.lineSeparator() +
				"            OR (" + System.lineSeparator() +
				"                " + dbSchema + ".jdbc_tool_test_fullCaseMainEntity.DateTimeColumn = '5005'" + System.lineSeparator() +
				"                AND " + dbSchema + ".jdbc_tool_test_fullCaseMainEntity.YMColumn = '6006'" + System.lineSeparator() +
				"            )" + System.lineSeparator() +
				"        )" + System.lineSeparator() +
				"    )" + System.lineSeparator() +
				"    AND (" + System.lineSeparator() +
				"        (" + System.lineSeparator() +
				"            " + dbSchema + ".jdbc_tool_test_fullCaseMainEntity.CurrencyColumn = ?" + System.lineSeparator() +
				"            AND " + dbSchema + ".jdbc_tool_test_fullCaseMainEntity.LongColumn = ?" + System.lineSeparator() +
				"        )" + System.lineSeparator() +
				"        OR (" + System.lineSeparator() +
				"            " + dbSchema + ".jdbc_tool_test_fullCaseMainEntity.CompanyCD = ?" + System.lineSeparator() +
				"            AND " + dbSchema + ".jdbc_tool_test_fullCaseMainEntity.ExclusiveFG = ?" + System.lineSeparator() +
				"            OR (" + System.lineSeparator() +
				"                " + dbSchema + ".jdbc_tool_test_fullCaseMainEntity.RegisteredPerson = ?" + System.lineSeparator() +
				"                AND " + dbSchema + ".jdbc_tool_test_fullCaseMainEntity.RegisteredDT = ?" + System.lineSeparator() +
				"            )" + System.lineSeparator() +
				"        )" + System.lineSeparator() +
				"    )";
	}
	
	@Test
	public void testExport_expMapCase4() {
		
		// パターン４：where (a = ? and b = ?) or c = ? and d = ?
		cleanInsert("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/import_expmapcase4.xml");
		String conditionFilePath = getConditionFilePath("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdservicetest/condition_expmapcase4.yaml");
		GExporterResult<List<GXFormCodeResult>, byte[]> result = service.export(conditionFilePath, settingsFilePath);
		
		assertEquals("html", result.getMediaType());
		assertEquals(GCrudType.INSERT, result.getData().get(0).getType());
		assertEquals("仮想表ID: A2E4442626F04100A45E747DE5856120", result.getData().get(0).getDescription());
		assertEquals("INSERT INTO" + System.lineSeparator() +
				"    " + dbSchema + ".jdbc_tool_test_fullCaseMainEntity(objectID, StringColumn, IntColumn, NStringColumn, DateTimeColumn, YMColumn, CurrencyColumn, LongColumn, ExclusiveFG, RegisteredPerson, UpdatedPerson, RegisteredDT, UpdatedDT)" + System.lineSeparator() +
				"VALUES" + System.lineSeparator() +
				"(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " + getTimestampCommand() + ", " + getTimestampCommand() + ")"
				, result.getData().get(0).getSqlScript());
		
		GDao2VirtualEntity insertCodeResult = ((GDao2JdbcXFormCodeResult)result.getData().get(0)).getVirtualEntity();
		assertEquals(13, insertCodeResult.getColumns().size());
		assertEquals("-", insertCodeResult.getAbsoluteVirtualColumnCode());

		String insertRecord = "GRecord record = createRecord();" + System.lineSeparator() +
				"record.setObject(\"objectID\",);" + System.lineSeparator() +
				"record.setObject(\"StringColumn\",);" + System.lineSeparator() +
				"record.setObject(\"IntColumn\",);" + System.lineSeparator() +
				"record.setObject(\"NStringColumn\",);" + System.lineSeparator() +
				"record.setObject(\"DateTimeColumn\",);" + System.lineSeparator() +
				"record.setObject(\"YMColumn\",);" + System.lineSeparator() +
				"record.setObject(\"CurrencyColumn\",);" + System.lineSeparator() +
				"record.setObject(\"LongColumn\",);" + System.lineSeparator() +
				"record.setObject(\"ExclusiveFG\",);" + System.lineSeparator() +
				"record.setObject(\"RegisteredPerson\",);" + System.lineSeparator() +
				"record.setObject(\"UpdatedPerson\",);" + System.lineSeparator() +
				"record.setObject(\"RegisteredDT\",);" + System.lineSeparator() +
				"record.setObject(\"UpdatedDT\",);" + System.lineSeparator();
		String insertExecuteApiStr = "insert(\"jdbc_tool_test_fullCaseMainEntity\")" + System.lineSeparator() +  ".values(record)";
		
		assertEquals(insertRecord + System.lineSeparator() + "int count = " + insertExecuteApiStr + System.lineSeparator() + ".execute();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, null)));
		assertNull(result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE)));
		assertNull(result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_PK)));
		assertNull(result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_UK)));
		
		String insertExecuteBatchApiStr = "insert(\"jdbc_tool_test_fullCaseMainEntity\")" + System.lineSeparator() + ".values(recordList)";
		StringBuilder insertRecordList = new StringBuilder();
		insertRecordList.append("List<GRecord> recordList = new ArrayList<>();").append(System.lineSeparator());
		insertRecordList.append(insertRecord);
		insertRecordList.append("recordList.add(record);").append(System.lineSeparator());
		
		assertEquals(insertRecordList + System.lineSeparator() + "int count = " + insertExecuteBatchApiStr + System.lineSeparator() + ".executeBatch();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, null)));
		assertNull(result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE)));
		assertNull(result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_PK)));
		assertNull(result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_UK)));
		assertEquals(insertRecordList + System.lineSeparator() + "int count = " + insertExecuteBatchApiStr + System.lineSeparator() + ".executeList();", result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, null)));
		assertNull(result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE)));
		assertNull(result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_PK)));
		assertNull(result.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_UK)));
		
		
		assertEquals(GCrudType.UPDATE, result.getData().get(1).getType());
		assertEquals("仮想表ID: A2E4442626F04100A45E747DE5856120", result.getData().get(1).getDescription());
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
				"    ExclusiveFG = ?," + System.lineSeparator() +
				"    RegisteredPerson = ?," + System.lineSeparator() +
				"    RegisteredDT = ?," + System.lineSeparator() +
				"    UpdatedPerson = ?," + System.lineSeparator() +
				"    UpdatedDT = ?" + System.lineSeparator() +
				"WHERE" + System.lineSeparator() +
				"    (" + System.lineSeparator() +
				"        (" + System.lineSeparator() +
				"            jdbc_tool_test_fullCaseMainEntity.objectID = ?" + System.lineSeparator() +
				"            AND jdbc_tool_test_fullCaseMainEntity.StringColumn = ?" + System.lineSeparator() +
				"        )" + System.lineSeparator() +
				"        OR jdbc_tool_test_fullCaseMainEntity.IntColumn = ?" + System.lineSeparator() +
				"        AND jdbc_tool_test_fullCaseMainEntity.NStringColumn = ?" + System.lineSeparator() +
				"    )", result.getData().get(1).getSqlScript());
		
		GDao2VirtualEntity updateCodeResult = ((GDao2JdbcXFormCodeResult)result.getData().get(1)).getVirtualEntity();
		assertEquals(13, updateCodeResult.getColumns().size());
		assertEquals("-", updateCodeResult.getAbsoluteVirtualColumnCode());

		String whereParamsParts = "Map<String, Object> whereParams = new HashMap<>();" + System.lineSeparator() +
				"whereParams.put(\"jdbc_tool_test_fullCaseMainEntity.StringColumn\",);" + System.lineSeparator() +
				"whereParams.put(\"jdbc_tool_test_fullCaseMainEntity.objectID\",);" + System.lineSeparator() +
				"whereParams.put(\"jdbc_tool_test_fullCaseMainEntity.IntColumn\",);" + System.lineSeparator() +
				"whereParams.put(\"jdbc_tool_test_fullCaseMainEntity.NStringColumn\",);" + System.lineSeparator();
		String updateRecord = "GRecord record = createRecord();" + System.lineSeparator() +
				"record.setObject(\"StringColumn\",);" + System.lineSeparator() +
				"record.setObject(\"IntColumn\",);" + System.lineSeparator() +
				"record.setObject(\"NStringColumn\",);" + System.lineSeparator() +
				"record.setObject(\"DateTimeColumn\",);" + System.lineSeparator() +
				"record.setObject(\"YMColumn\",);" + System.lineSeparator() +
				"record.setObject(\"CurrencyColumn\",);" + System.lineSeparator() +
				"record.setObject(\"LongColumn\",);" + System.lineSeparator() +
				"record.setObject(\"ExclusiveFG\",);" + System.lineSeparator() +
				"record.setObject(\"RegisteredPerson\",);" + System.lineSeparator() +
				"record.setObject(\"RegisteredDT\",);" + System.lineSeparator() +
				"record.setObject(\"UpdatedPerson\",);" + System.lineSeparator() +
				"record.setObject(\"UpdatedDT\",);" + System.lineSeparator();
		String updateExecuteApiStr = "update(\"jdbc_tool_test_fullCaseMainEntity\")" + System.lineSeparator() + ".set(record)" + System.lineSeparator();
		String executeApiWhereStr = ".where(expMap($($($(\"jdbc_tool_test_fullCaseMainEntity.objectID = :jdbc_tool_test_fullCaseMainEntity.objectID\").AND(\"jdbc_tool_test_fullCaseMainEntity.StringColumn = :jdbc_tool_test_fullCaseMainEntity.StringColumn\")).OR(\"jdbc_tool_test_fullCaseMainEntity.IntColumn = :jdbc_tool_test_fullCaseMainEntity.IntColumn\").AND(\"jdbc_tool_test_fullCaseMainEntity.NStringColumn = :jdbc_tool_test_fullCaseMainEntity.NStringColumn\")), whereParams))" + System.lineSeparator();
		assertEquals(whereParamsParts + updateRecord + System.lineSeparator() + "int count = " + updateExecuteApiStr + executeApiWhereStr + ".execute();", result.getData().get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE)));
		assertEquals(updateRecord + System.lineSeparator() + "int count = " + updateExecuteApiStr + ".wherePK()" + System.lineSeparator() + ".execute();", result.getData().get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_PK)));
		String executeUKStr = ".whereUK(\"MultipleColumnUK\")" + System.lineSeparator() + "//.whereUK(\"OtherUKName\")" + System.lineSeparator() + "//.whereUK(\"SingleColumnUK\")";
		assertEquals(updateRecord + System.lineSeparator() + "int count = " + updateExecuteApiStr + executeUKStr + System.lineSeparator() + ".execute();", result.getData().get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_UK)));
		
		StringBuilder updateRecordList = new StringBuilder();
		updateRecordList.append("List<GRecord> recordList = new ArrayList<>();").append(System.lineSeparator());
		updateRecordList.append(updateRecord);
		updateRecordList.append("recordList.add(record);").append(System.lineSeparator());
		
		String updateExecuteListApiStr = "update(\"jdbc_tool_test_fullCaseMainEntity\")" + System.lineSeparator() + ".set(recordList)" + System.lineSeparator();
		
		assertEquals(updateRecordList + System.lineSeparator() + "int count = " + updateExecuteListApiStr + ".wherePK()" + System.lineSeparator() + ".executeList();", result.getData().get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_PK)));
		assertEquals(updateRecordList + System.lineSeparator() + "int count = " + updateExecuteListApiStr + executeUKStr + System.lineSeparator() + ".executeList();", result.getData().get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_UK)));
		assertEquals(updateRecordList + System.lineSeparator() + "int count = " + updateExecuteListApiStr + ".wherePK()" + System.lineSeparator() + ".executeBatch();", result.getData().get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_PK)));
		assertEquals(updateRecordList + System.lineSeparator() + "int count = " + updateExecuteListApiStr + executeUKStr + System.lineSeparator() + ".executeBatch();", result.getData().get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_UK)));
		
		assertEquals(GCrudType.SELECT, result.getData().get(2).getType());
		assertEquals("仮想表ID: A2E4442626F04100A45E747DE5856120", result.getData().get(2).getDescription());
		assertEquals("SELECT" + System.lineSeparator() +
				"    jdbc_tool_test_fullCaseMainEntity.objectID," + System.lineSeparator() +
				"    jdbc_tool_test_fullCaseMainEntity.StringColumn," + System.lineSeparator() +
				"    jdbc_tool_test_fullCaseMainEntity.IntColumn," + System.lineSeparator() +
				"    jdbc_tool_test_fullCaseMainEntity.NStringColumn," + System.lineSeparator() +
				"    jdbc_tool_test_fullCaseMainEntity.DateTimeColumn," + System.lineSeparator() +
				"    jdbc_tool_test_fullCaseMainEntity.YMColumn," + System.lineSeparator() +
				"    jdbc_tool_test_fullCaseMainEntity.CurrencyColumn," + System.lineSeparator() +
				"    jdbc_tool_test_fullCaseMainEntity.LongColumn," + System.lineSeparator() +
				"    jdbc_tool_test_fullCaseMainEntity.ExclusiveFG," + System.lineSeparator() +
				"    jdbc_tool_test_fullCaseMainEntity.RegisteredPerson," + System.lineSeparator() +
				"    jdbc_tool_test_fullCaseMainEntity.RegisteredDT," + System.lineSeparator() +
				"    jdbc_tool_test_fullCaseMainEntity.UpdatedPerson," + System.lineSeparator() +
				"    jdbc_tool_test_fullCaseMainEntity.UpdatedDT" + System.lineSeparator() +
				"FROM" + System.lineSeparator() +
				"    " + dbSchema + ".jdbc_tool_test_fullCaseMainEntity jdbc_tool_test_fullCaseMainEntity" + System.lineSeparator() +
				"WHERE" + System.lineSeparator() +
				"    (" + System.lineSeparator() +
				"        (" + System.lineSeparator() +
				"            jdbc_tool_test_fullCaseMainEntity.objectID = ?" + System.lineSeparator() +
				"            AND jdbc_tool_test_fullCaseMainEntity.StringColumn = ?" + System.lineSeparator() +
				"        )" + System.lineSeparator() +
				"        OR jdbc_tool_test_fullCaseMainEntity.IntColumn = ?" + System.lineSeparator() +
				"        AND jdbc_tool_test_fullCaseMainEntity.NStringColumn = ?" + System.lineSeparator() +
				"    )", result.getData().get(2).getSqlScript());
		
		GDao2VirtualEntity selectCodeResult = ((GDao2JdbcXFormCodeResult)result.getData().get(2)).getVirtualEntity();
		assertEquals(13, selectCodeResult.getColumns().size());
		assertEquals("-", selectCodeResult.getAbsoluteVirtualColumnCode());

		String selectApiStr = "select(\"jdbc_tool_test_fullCaseMainEntity\", \"jdbc_tool_test_fullCaseMainEntity\")" + System.lineSeparator() +
				".fields(col(\"jdbc_tool_test_fullCaseMainEntity.objectID\"),col(\"jdbc_tool_test_fullCaseMainEntity.StringColumn\"),col(\"jdbc_tool_test_fullCaseMainEntity.IntColumn\"),col(\"jdbc_tool_test_fullCaseMainEntity.NStringColumn\"),col(\"jdbc_tool_test_fullCaseMainEntity.DateTimeColumn\"),col(\"jdbc_tool_test_fullCaseMainEntity.YMColumn\"),col(\"jdbc_tool_test_fullCaseMainEntity.CurrencyColumn\"),col(\"jdbc_tool_test_fullCaseMainEntity.LongColumn\"),col(\"jdbc_tool_test_fullCaseMainEntity.ExclusiveFG\"),col(\"jdbc_tool_test_fullCaseMainEntity.RegisteredPerson\"),col(\"jdbc_tool_test_fullCaseMainEntity.RegisteredDT\"),col(\"jdbc_tool_test_fullCaseMainEntity.UpdatedPerson\"),col(\"jdbc_tool_test_fullCaseMainEntity.UpdatedDT\"))" + System.lineSeparator();
		String whereParamsPartsUKPK = "Object[] whereParams = new Object[]{,,,};" + System.lineSeparator();
		String selectUKStr = ".whereUK(\"MultipleColumnUK\", whereParams)" + System.lineSeparator() + "//.whereUK(\"OtherUKName\", whereParams)" + System.lineSeparator() + "//.whereUK(\"SingleColumnUK\", whereParams)";
		assertEquals(whereParamsParts + "List<GRecord> result = " + selectApiStr + executeApiWhereStr + ".findList();", result.getData().get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE)));
		assertEquals(whereParamsPartsUKPK + "List<GRecord> result = " + selectApiStr + ".wherePK(whereParams)" + System.lineSeparator() + ".findList();", result.getData().get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE_PK)));
		assertEquals(whereParamsPartsUKPK + "List<GRecord> result = " + selectApiStr + selectUKStr + System.lineSeparator() + ".findList();", result.getData().get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE_UK)));
		
		assertEquals(whereParamsParts + "GRecord result = " + selectApiStr + executeApiWhereStr + ".findRecord();", result.getData().get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE)));
		assertEquals(whereParamsPartsUKPK + "GRecord result = " + selectApiStr + ".wherePK(whereParams)" + System.lineSeparator() + ".findRecord();", result.getData().get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE_PK)));
		assertEquals(whereParamsPartsUKPK + "GRecord result = " + selectApiStr + selectUKStr + System.lineSeparator() + ".findRecord();", result.getData().get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE_UK)));
		
		assertEquals(whereParamsParts + "GRecordSet result = " + selectApiStr + executeApiWhereStr + ".findRecordSet();", result.getData().get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE)));
		assertEquals(whereParamsPartsUKPK + "GRecordSet result = " + selectApiStr + ".wherePK(whereParams)" + System.lineSeparator() + ".findRecordSet();", result.getData().get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE_PK)));
		assertEquals(whereParamsPartsUKPK + "GRecordSet result = " + selectApiStr + selectUKStr + System.lineSeparator() + ".findRecordSet();", result.getData().get(2).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE_UK)));
		
		assertEquals(GCrudType.DELETE, result.getData().get(3).getType());
		assertEquals("仮想表ID: A2E4442626F04100A45E747DE5856120", result.getData().get(3).getDescription());
		assertEquals(getDeleteSql_testExport_expMapCase4(), result.getData().get(3).getSqlScript());
		
		GDao2VirtualEntity deleteCodeResult = ((GDao2JdbcXFormCodeResult)result.getData().get(3)).getVirtualEntity();
		assertEquals(13, deleteCodeResult.getColumns().size());
		assertEquals("-", selectCodeResult.getAbsoluteVirtualColumnCode());
		
		String deleteApiStr = "delete(\"jdbc_tool_test_fullCaseMainEntity\")" + System.lineSeparator();
		assertEquals(whereParamsParts + "int count = " + deleteApiStr + executeApiWhereStr + ".execute();", result.getData().get(3).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE)));
		
		String deleteRecordStr = "GRecord whereParams = createRecord();" + System.lineSeparator() +
				"whereParams.setObject(\"objectID\",);" + System.lineSeparator();
		
		assertEquals(deleteRecordStr + System.lineSeparator() + "int count = " + deleteApiStr + ".wherePK(whereParams)" + System.lineSeparator() + ".execute();", result.getData().get(3).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_PK)));
		String deleteRecordUKStr = "GRecord whereParams = createRecord();" + System.lineSeparator() +
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
		
		assertEquals(deleteRecordUKStr + System.lineSeparator() + "int count = " + deleteApiStr + selectUKStr + System.lineSeparator() + ".execute();", result.getData().get(3).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_UK)));
		
		StringBuilder deleteListRecordStr = new StringBuilder();
		deleteListRecordStr.append(deleteRecordStr);
		deleteListRecordStr.append("List<GRecord> whereParamsList = new ArrayList<>();").append(System.lineSeparator());
		deleteListRecordStr.append("whereParamsList.add(whereParams);").append(System.lineSeparator());
		
		StringBuilder deleteListRecordUKStr = new StringBuilder();
		deleteListRecordUKStr.append(deleteRecordUKStr);
		deleteListRecordUKStr.append("List<GRecord> whereParamsList = new ArrayList<>();").append(System.lineSeparator());
		deleteListRecordUKStr.append("whereParamsList.add(whereParams);").append(System.lineSeparator());
		String deleteListUKStr = ".whereUK(\"MultipleColumnUK\", whereParamsList)" + System.lineSeparator() + "//.whereUK(\"OtherUKName\", whereParamsList)" + System.lineSeparator() + "//.whereUK(\"SingleColumnUK\", whereParamsList)";
		
		assertNull(result.getData().get(3).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE)));
		assertEquals(deleteListRecordStr + System.lineSeparator() + "int count = " + deleteApiStr + ".wherePK(whereParamsList)" + System.lineSeparator() + ".executeList();", result.getData().get(3).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_PK)));
		assertEquals(deleteListRecordUKStr + System.lineSeparator() + "int count = " + deleteApiStr + deleteListUKStr + System.lineSeparator() + ".executeList();", result.getData().get(3).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_UK)));
		assertNull(result.getData().get(3).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE)));
		assertEquals(deleteListRecordStr + System.lineSeparator() + "int count = " + deleteApiStr + ".wherePK(whereParamsList)" + System.lineSeparator() + ".executeBatch();", result.getData().get(3).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_PK)));
		assertEquals(deleteListRecordUKStr + System.lineSeparator() + "int count = " + deleteApiStr + deleteListUKStr + System.lineSeparator() + ".executeBatch();", result.getData().get(3).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_UK)));
	}
	protected String getDeleteSql_testExport_expMapCase4() {
		return "DELETE FROM" + System.lineSeparator() +
				"    " + dbSchema + ".jdbc_tool_test_fullCaseMainEntity" + System.lineSeparator() +
				"WHERE" + System.lineSeparator() +
				"    (" + System.lineSeparator() +
				"        (" + System.lineSeparator() +
				"            " + dbSchema + ".jdbc_tool_test_fullCaseMainEntity.objectID = ?" + System.lineSeparator() +
				"            AND " + dbSchema + ".jdbc_tool_test_fullCaseMainEntity.StringColumn = ?" + System.lineSeparator() +
				"        )" + System.lineSeparator() +
				"        OR " + dbSchema + ".jdbc_tool_test_fullCaseMainEntity.IntColumn = ?" + System.lineSeparator() +
				"        AND " + dbSchema + ".jdbc_tool_test_fullCaseMainEntity.NStringColumn = ?" + System.lineSeparator() +
				"    )";
	}
}
