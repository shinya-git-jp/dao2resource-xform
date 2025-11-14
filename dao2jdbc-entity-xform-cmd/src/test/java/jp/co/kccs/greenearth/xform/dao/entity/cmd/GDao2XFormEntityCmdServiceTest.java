package jp.co.kccs.greenearth.xform.dao.entity.cmd;

import jp.co.kccs.greenearth.commons.utils.GFileUtils;
import jp.co.kccs.greenearth.testutils.dbunit.TestHelper;
import jp.co.kccs.greenearth.xform.dao.common.GDao2Utils;
import jp.co.kccs.greenearth.xform.dao.common.GDaoCommonSetting;
import jp.co.kccs.greenearth.xform.dao.common.GDaoCommonSettingParserFilePath;
import jp.co.kccs.greenearth.xform.entity.jdbc.core.service.GEntityFileDescriptor;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * @author KCCS XXXXX
 * @create GEF_NEXT_DATE
 * @since GEF_NEXT_VERSION
 */
public abstract class GDao2XFormEntityCmdServiceTest {
	
	protected GDao2XFormEntityCmdService service = GDao2XFormEntityCmdService.getInstance();
	public static String settingsFilePath;
	public static String dbScheme;
	
	protected GDaoCommonSetting commonSetting;
	
	@Before
	public void setup() {
		settingsFilePath = GFileUtils.getResource(getSettingsFilePath()).getPath();
		GDaoCommonSettingParserFilePath commonSettingParser = new GDaoCommonSettingParserFilePath();
		commonSetting = commonSettingParser.parse(settingsFilePath);
		dbScheme = commonSetting.getDb().getSchema();
		GDao2Utils.setCommonSetting(commonSetting);
	}
	
	protected abstract String getSettingsFilePath();
	
	protected void cleanInsert(String importFilePath) throws Exception {
		TestHelper.cleanInsert(commonSetting.getDb(), importFilePath);
	}
	
	@Test
	public void testExport_singleEntityXml() throws Exception {
		
		cleanInsert("jp/co/kccs/greenearth/xform/dao/entity/cmd/gdao2xformentitycmdservicetest/import_entity.xml");
		String conditionFilePath = GFileUtils.getResource("jp/co/kccs/greenearth/xform/dao/entity/cmd/gdao2xformentitycmdservicetest/condition_singleEntity.yaml").getPath();

		List<GEntityFileDescriptor> results = service.export(conditionFilePath, settingsFilePath);

		assertEquals(1, results.size());
		assertEquals("jdbc_tool_test_mainmaster", results.get(0).getName());
		assertEquals(
				"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\r\n" +
					"<Entity database=\"" + dbScheme + "\" phyName=\"jdbc_tool_test_mainmaster\">\r\n" +
					"  <Columns>\r\n" +
					"    <Column name=\"objectID\" phyName=\"objectID\" type=\"Varchar\" size=\"50\"/>\r\n" +
					"    <Column name=\"ItemCD\" phyName=\"ItemCD\" type=\"Varchar\" size=\"50\"/>\r\n" +
					"    <Column name=\"ItemNA\" phyName=\"ItemNA\" type=\"Varchar\" size=\"50\"/>\r\n" +
					"    <Column name=\"UnitPrice\" phyName=\"UnitPrice\" type=\"Integer\"/>\r\n" +
					"    <Column name=\"SellPrice\" phyName=\"SellPrice\" type=\"Integer\"/>\r\n" +
					"    <Column name=\"Flag\" phyName=\"Flag\" type=\"Integer\"/>\r\n" +
					"    <Column name=\"PriceFlag\" phyName=\"PriceFlag\" type=\"Integer\"/>\r\n" +
					"    <Column name=\"ExclusiveFG\" phyName=\"ExclusiveFG\" type=\"Varchar\" size=\"32\" exclusiveKey=\"true\"/>\r\n" +
					"    <Column name=\"RegisteredDT\" phyName=\"RegisteredDT\" type=\"Date\" generate=\"RegisteredDate\"/>\r\n" +
					"    <Column name=\"UpdatedDT\" phyName=\"UpdatedDT\" type=\"Date\" generate=\"UpdatedDate\"/>\r\n" +
					"  </Columns>\r\n" +
					"  <PrimaryKey>\r\n" +
					"    <ReferenceColumn refName=\"objectID\"/>\r\n" +
					"  </PrimaryKey>\r\n" +
					"  <UniqueKeys>\r\n" +
					"    <UniqueKey name=\"MainMasterUniqueKey\">\r\n" +
					"      <ReferenceColumn refName=\"objectID\"/>\r\n" +
					"    </UniqueKey>\r\n" +
					"  </UniqueKeys>\r\n" +
					"  <ForeignKeys>\r\n" +
					"    <ForeignKey name=\"FK\" refEntity=\"jdbc_tool_test_unitmaster\">\r\n" +
					"      <JoinColumn refName=\"Code\" srcName=\"ItemCD\"/>\r\n" +
					"    </ForeignKey>\r\n" +
					"    <ForeignKey name=\"FKleft\" refEntity=\"jdbc_tool_test_ordermaster\">\r\n" +
					"      <JoinColumn refName=\"CorporateNA\" srcName=\"ItemNA\"/>\r\n" +
					"    </ForeignKey>\r\n" +
					"  </ForeignKeys>\r\n" +
					"</Entity>\r\n", new String(results.get(0).getResult(), "UTF-8"));
	}
	
	@Test
	public void testExport_multipleEntityXml() throws Exception {
		cleanInsert("jp/co/kccs/greenearth/xform/dao/entity/cmd/gdao2xformentitycmdservicetest/import_entity.xml");
		String conditionFilePath = GFileUtils.getResource("jp/co/kccs/greenearth/xform/dao/entity/cmd/gdao2xformentitycmdservicetest/condition_multipleEntity.yaml").getPath();
		List<GEntityFileDescriptor> results = service.export(conditionFilePath, settingsFilePath);

		assertEquals(2, results.size());
		assertEquals("jdbc_tool_test_mainmaster", results.get(0).getName());
		assertEquals(
				"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\r\n" +
					"<Entity database=\"" + dbScheme + "\" phyName=\"jdbc_tool_test_mainmaster\">\r\n" +
					"  <Columns>\r\n" +
					"    <Column name=\"objectID\" phyName=\"objectID\" type=\"Varchar\" size=\"50\"/>\r\n" +
					"    <Column name=\"ItemCD\" phyName=\"ItemCD\" type=\"Varchar\" size=\"50\"/>\r\n" +
					"    <Column name=\"ItemNA\" phyName=\"ItemNA\" type=\"Varchar\" size=\"50\"/>\r\n" +
					"    <Column name=\"UnitPrice\" phyName=\"UnitPrice\" type=\"Integer\"/>\r\n" +
					"    <Column name=\"SellPrice\" phyName=\"SellPrice\" type=\"Integer\"/>\r\n" +
					"    <Column name=\"Flag\" phyName=\"Flag\" type=\"Integer\"/>\r\n" +
					"    <Column name=\"PriceFlag\" phyName=\"PriceFlag\" type=\"Integer\"/>\r\n" +
					"    <Column name=\"ExclusiveFG\" phyName=\"ExclusiveFG\" type=\"Varchar\" size=\"32\" exclusiveKey=\"true\"/>\r\n" +
					"    <Column name=\"RegisteredDT\" phyName=\"RegisteredDT\" type=\"Date\" generate=\"RegisteredDate\"/>\r\n" +
					"    <Column name=\"UpdatedDT\" phyName=\"UpdatedDT\" type=\"Date\" generate=\"UpdatedDate\"/>\r\n" +
					"  </Columns>\r\n" +
					"  <PrimaryKey>\r\n" +
					"    <ReferenceColumn refName=\"objectID\"/>\r\n" +
					"  </PrimaryKey>\r\n" +
					"  <UniqueKeys>\r\n" +
					"    <UniqueKey name=\"MainMasterUniqueKey\">\r\n" +
					"      <ReferenceColumn refName=\"objectID\"/>\r\n" +
					"    </UniqueKey>\r\n" +
					"  </UniqueKeys>\r\n" +
					"  <ForeignKeys>\r\n" +
					"    <ForeignKey name=\"FK\" refEntity=\"jdbc_tool_test_unitmaster\">\r\n" +
					"      <JoinColumn refName=\"Code\" srcName=\"ItemCD\"/>\r\n" +
					"    </ForeignKey>\r\n" +
					"    <ForeignKey name=\"FKleft\" refEntity=\"jdbc_tool_test_ordermaster\">\r\n" +
					"      <JoinColumn refName=\"CorporateNA\" srcName=\"ItemNA\"/>\r\n" +
					"    </ForeignKey>\r\n" +
					"  </ForeignKeys>\r\n" +
					"</Entity>\r\n", new String(results.get(0).getResult(), "UTF-8"));
		assertEquals("jdbc_tool_test_unitmaster", results.get(1).getName());
		assertEquals(
				"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\r\n" +
					"<Entity database=\"" + dbScheme + "\" phyName=\"jdbc_tool_test_unitmaster\">\r\n" +
					"  <Columns>\r\n" +
					"    <Column name=\"objectID\" phyName=\"objectID\" type=\"Varchar\" size=\"50\"/>\r\n" +
					"    <Column name=\"Code\" phyName=\"Code\" type=\"Varchar\" size=\"50\"/>\r\n" +
					"    <Column name=\"Name\" phyName=\"Name\" type=\"Varchar\" size=\"50\"/>\r\n" +
					"  </Columns>\r\n" +
					"  <PrimaryKey>\r\n" +
					"    <ReferenceColumn refName=\"objectID\"/>\r\n" +
					"  </PrimaryKey>\r\n" +
					"  <UniqueKeys>\r\n" +
					"    <UniqueKey name=\"UnitMasterUK\">\r\n" +
					"      <ReferenceColumn refName=\"Code\"/>\r\n" +
					"      <ReferenceColumn refName=\"Name\"/>\r\n" +
					"    </UniqueKey>\r\n" +
					"  </UniqueKeys>\r\n" +
					"  <ForeignKeys>\r\n" +
					"    <ForeignKey name=\"FKRight\" refEntity=\"jdbc_tool_test_ordermaster\">\r\n" +
					"      <JoinColumn refName=\"CorporateNA\" srcName=\"Name\"/>\r\n" +
					"    </ForeignKey>\r\n" +
					"  </ForeignKeys>\r\n" +
					"</Entity>\r\n", new String(results.get(1).getResult(), "UTF-8"));
	}
	
	@Test
	public void testExport_allColumnTypeEntityXml() throws Exception {
		
		cleanInsert("jp/co/kccs/greenearth/xform/dao/entity/cmd/gdao2xformentitycmdservicetest/import_entity.xml");
		String conditionFilePath = GFileUtils.getResource("jp/co/kccs/greenearth/xform/dao/entity/cmd/gdao2xformentitycmdservicetest/condition_allColumnTypeEntity.yaml").getPath();

		List<GEntityFileDescriptor> results = service.export(conditionFilePath, settingsFilePath);

		assertEquals(1, results.size());
		assertEquals("jdcb_tool_test_allColumntype", results.get(0).getName());
		assertEquals(
				"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\r\n" +
					"<Entity database=\"" + dbScheme + "\" phyName=\"jdcb_tool_test_allColumntype\">\r\n" +
					"  <Columns>\r\n" +
					"    <Column name=\"objectID\" phyName=\"objectID\" type=\"Varchar\" size=\"32\"/>\r\n" +
					"    <Column name=\"StringColumn\" phyName=\"StringColumn\" type=\"Varchar\" size=\"32\"/>\r\n" +
					"    <Column name=\"IntColumn\" phyName=\"IntColumn\" type=\"Integer\" size=\"8\"/>\r\n" +
					"    <Column name=\"NStringColumn\" phyName=\"NStringColumn\" type=\"Varchar\" size=\"32\"/>\r\n" +
					"    <Column name=\"DateTimeColumn\" phyName=\"DateTimeColumn\" type=\"Date\"/>\r\n" +
					"    <Column name=\"YMColumn\" phyName=\"YMColumn\" type=\"Varchar\" size=\"6\"/>\r\n" +
					"    <Column name=\"CurrencyColumn\" phyName=\"CurrencyColumn\" type=\"Double\" size=\"32\" scale=\"2\"/>\r\n" +
					"    <Column name=\"LongColumn\" phyName=\"LongColumn\" type=\"Long\" size=\"32\"/>\r\n" +
					"    <Column name=\"CompanyCD\" phyName=\"CompanyCD\" type=\"Varchar\" size=\"32\"/>\r\n" +
					"    <Column name=\"ExclusiveFG\" phyName=\"ExclusiveFG\" type=\"Varchar\" size=\"32\" exclusiveKey=\"true\"/>\r\n" +
					"    <Column name=\"RegisteredPerson\" phyName=\"RegisteredPerson\" type=\"Varchar\" size=\"32\"/>\r\n" +
					"    <Column name=\"RegisteredDT\" phyName=\"RegisteredDT\" type=\"Date\" generate=\"RegisteredDate\"/>\r\n" +
					"    <Column name=\"UpdatedPerson\" phyName=\"UpdatedPerson\" type=\"Varchar\" size=\"32\"/>\r\n" +
					"    <Column name=\"UpdatedDT\" phyName=\"UpdatedDT\" type=\"Date\" generate=\"UpdatedDate\"/>\r\n" +
					"  </Columns>\r\n" +
					"  <PrimaryKey>\r\n" +
					"    <ReferenceColumn refName=\"objectID\"/>\r\n" +
					"  </PrimaryKey>\r\n" +
					"  <UniqueKeys>\r\n" +
					"    <UniqueKey name=\"MulitipleColumnUKName\">\r\n" +
					"      <ReferenceColumn refName=\"objectID\"/>\r\n" +
					"      <ReferenceColumn refName=\"CompanyCD\"/>\r\n" +
					"    </UniqueKey>\r\n" +
					"    <UniqueKey name=\"OtherUKName\">\r\n" +
					"      <ReferenceColumn refName=\"objectID\"/>\r\n" +
					"      <ReferenceColumn refName=\"StringColumn\"/>\r\n" +
					"      <ReferenceColumn refName=\"IntColumn\"/>\r\n" +
					"      <ReferenceColumn refName=\"NStringColumn\"/>\r\n" +
					"      <ReferenceColumn refName=\"DateTimeColumn\"/>\r\n" +
					"      <ReferenceColumn refName=\"YMColumn\"/>\r\n" +
					"      <ReferenceColumn refName=\"CurrencyColumn\"/>\r\n" +
					"      <ReferenceColumn refName=\"LongColumn\"/>\r\n" +
					"      <ReferenceColumn refName=\"CompanyCD\"/>\r\n" +
					"      <ReferenceColumn refName=\"ExclusiveFG\"/>\r\n" +
					"      <ReferenceColumn refName=\"RegisteredPerson\"/>\r\n" +
					"      <ReferenceColumn refName=\"RegisteredDT\"/>\r\n" +
					"      <ReferenceColumn refName=\"UpdatedPerson\"/>\r\n" +
					"      <ReferenceColumn refName=\"UpdatedDT\"/>\r\n" +
					"    </UniqueKey>\r\n" +
					"    <UniqueKey name=\"SingleColumnUK\">\r\n" +
					"      <ReferenceColumn refName=\"StringColumn\"/>\r\n" +
					"    </UniqueKey>\r\n" +
					"  </UniqueKeys>\r\n" +
					"  <ForeignKeys>\r\n" +
					"    <ForeignKey name=\"MultipleConstValueFKName\" refEntity=\"jdbc_tool_test_pk_uk_mainmaster\">\r\n" +
					"      <JoinColumn refName=\"objectID\" constValue=\"1001\"/>\r\n" +
					"      <JoinColumn refName=\"ItemCD\" constValue=\"9001\"/>\r\n" +
					"    </ForeignKey>\r\n" +
					"    <ForeignKey name=\"MultipleColumnFKName\" refEntity=\"jdbc_tool_test_pk_uk_mainmaster\">\r\n" +
					"      <JoinColumn refName=\"objectID\" srcName=\"objectID\"/>\r\n" +
					"      <JoinColumn refName=\"ItemCD\" srcName=\"StringColumn\"/>\r\n" +
					"    </ForeignKey>\r\n" +
					"    <ForeignKey name=\"ConstValueFKName\" refEntity=\"jdbc_tool_test_pk_uk_mainmaster\">\r\n" +
					"      <JoinColumn refName=\"objectID\" srcName=\"objectID\"/>\r\n" +
					"      <JoinColumn refName=\"Flag\" constValue=\"1001\"/>\r\n" +
					"    </ForeignKey>\r\n" +
					"    <ForeignKey name=\"SingleColumnFKName\" refEntity=\"jdbc_tool_test_pk_uk_mainmaster\">\r\n" +
					"      <JoinColumn refName=\"objectID\" srcName=\"objectID\"/>\r\n" +
					"      <JoinColumn refName=\"ItemCD\" srcName=\"StringColumn\"/>\r\n" +
					"    </ForeignKey>\r\n" +
					"  </ForeignKeys>\r\n" +
					"</Entity>\r\n", new String(results.get(0).getResult(), "UTF-8"));
	}
	
	@Test
	public void testExport_dataBaseIDXml() throws Exception {
		
		cleanInsert("jp/co/kccs/greenearth/xform/dao/entity/cmd/gdao2xformentitycmdservicetest/import_databaseId.xml");
		String conditionFilePath = GFileUtils.getResource("jp/co/kccs/greenearth/xform/dao/entity/cmd/gdao2xformentitycmdservicetest/condition_databaseId.yaml").getPath();

		List<GEntityFileDescriptor> results = service.export(conditionFilePath, settingsFilePath);

		assertEquals(8, results.size());
		assertEquals("SError", results.get(0).getName());
		assertEquals(
				"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\r\n" +
					"<Entity database=\"" + dbScheme + "\" phyName=\"SError\">\r\n" +
					"  <Columns>\r\n" +
					"    <Column name=\"objectID\" phyName=\"objectID\" type=\"Varchar\" size=\"32\"/>\r\n" +
					"    <Column name=\"errorNo\" phyName=\"errorNo\" type=\"Varchar\" size=\"10\"/>\r\n" +
					"    <Column name=\"errorMessageID\" phyName=\"errorMessageID\" type=\"Varchar\" size=\"32\"/>\r\n" +
					"    <Column name=\"entryUserID\" phyName=\"entryUserID\" type=\"Varchar\" size=\"20\"/>\r\n" +
					"    <Column name=\"entryDate\" phyName=\"entryDate\" type=\"Date\"/>\r\n" +
					"    <Column name=\"updateUserID\" phyName=\"updateUserID\" type=\"Varchar\" size=\"20\"/>\r\n" +
					"    <Column name=\"updateDate\" phyName=\"updateDate\" type=\"Date\"/>\r\n" +
					"    <Column name=\"exclusiveFlag\" phyName=\"exclusiveFlag\" type=\"Varchar\" size=\"32\"/>\r\n" +
					"    <Column name=\"errorType\" phyName=\"errorType\" type=\"Varchar\" size=\"1\"/>\r\n" +
					"    <Column name=\"description\" phyName=\"description\" type=\"Varchar\" size=\"255\"/>\r\n" +
					"    <Column name=\"url\" phyName=\"url\" type=\"Varchar\" size=\"255\"/>\r\n" +
					"  </Columns>\r\n" +
					"  <PrimaryKey>\r\n" +
					"    <ReferenceColumn refName=\"objectID\"/>\r\n" +
					"  </PrimaryKey>\r\n" +
					"  <UniqueKeys>\r\n" +
					"    <UniqueKey>\r\n" +
					"      <ReferenceColumn refName=\"errorNo\"/>\r\n" +
					"    </UniqueKey>\r\n" +
					"  </UniqueKeys>\r\n" +
					"  <ForeignKeys>\r\n" +
					"    <ForeignKey refEntity=\"SLocalization\">\r\n" +
					"      <JoinColumn refName=\"objectID\" srcName=\"errorMessageID\"/>\r\n" +
					"    </ForeignKey>\r\n" +
					"  </ForeignKeys>\r\n" +
					"</Entity>\r\n", new String(results.get(0).getResult(), "UTF-8"));
		assertEquals("SLocalization", results.get(1).getName());
		assertEquals(
				"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\r\n" +
					"<Entity database=\"" + dbScheme + "\" phyName=\"SLocalization\">\r\n" +
					"  <Columns>\r\n" +
					"    <Column name=\"objectID\" phyName=\"objectID\" type=\"Varchar\" size=\"32\"/>\r\n" +
					"    <Column name=\"country1\" phyName=\"country1\" type=\"Varchar\" size=\"255\"/>\r\n" +
					"    <Column name=\"country2\" phyName=\"country2\" type=\"Varchar\" size=\"255\"/>\r\n" +
					"    <Column name=\"country3\" phyName=\"country3\" type=\"Varchar\" size=\"255\"/>\r\n" +
					"    <Column name=\"country4\" phyName=\"country4\" type=\"Varchar\" size=\"255\"/>\r\n" +
					"    <Column name=\"country5\" phyName=\"country5\" type=\"Varchar\" size=\"255\"/>\r\n" +
					"  </Columns>\r\n" +
					"  <PrimaryKey>\r\n" +
					"    <ReferenceColumn refName=\"objectID\"/>\r\n" +
					"  </PrimaryKey>\r\n" +
					"</Entity>\r\n", new String(results.get(1).getResult(), "UTF-8"));
		assertEquals("jdbc_tool_test_mainmaster", results.get(2).getName());
		assertEquals(
				"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\r\n" +
					"<Entity database=\"" + dbScheme + "\" phyName=\"jdbc_tool_test_mainmaster\">\r\n" +
					"  <Columns>\r\n" +
					"    <Column name=\"objectID\" phyName=\"objectID\" type=\"Varchar\" size=\"50\"/>\r\n" +
					"    <Column name=\"ItemCD\" phyName=\"ItemCD\" type=\"Varchar\" size=\"50\"/>\r\n" +
					"    <Column name=\"ItemNA\" phyName=\"ItemNA\" type=\"Varchar\" size=\"50\"/>\r\n" +
					"    <Column name=\"UnitPrice\" phyName=\"UnitPrice\" type=\"Integer\"/>\r\n" +
					"    <Column name=\"SellPrice\" phyName=\"SellPrice\" type=\"Integer\"/>\r\n" +
					"    <Column name=\"Flag\" phyName=\"Flag\" type=\"Integer\"/>\r\n" +
					"    <Column name=\"PriceFlag\" phyName=\"PriceFlag\" type=\"Integer\"/>\r\n" +
					"    <Column name=\"ExclusiveFG\" phyName=\"ExclusiveFG\" type=\"Varchar\" size=\"32\" exclusiveKey=\"true\"/>\r\n" +
					"    <Column name=\"RegisteredDT\" phyName=\"RegisteredDT\" type=\"Date\" generate=\"RegisteredDate\"/>\r\n" +
					"    <Column name=\"UpdatedDT\" phyName=\"UpdatedDT\" type=\"Date\" generate=\"UpdatedDate\"/>\r\n" +
					"  </Columns>\r\n" +
					"  <PrimaryKey>\r\n" +
					"    <ReferenceColumn refName=\"objectID\"/>\r\n" +
					"  </PrimaryKey>\r\n" +
					"  <UniqueKeys>\r\n" +
					"    <UniqueKey name=\"MainMasterUniqueKey\">\r\n" +
					"      <ReferenceColumn refName=\"objectID\"/>\r\n" +
					"    </UniqueKey>\r\n" +
					"  </UniqueKeys>\r\n" +
					"  <ForeignKeys>\r\n" +
					"    <ForeignKey name=\"FK\" refEntity=\"jdbc_tool_test_unitmaster\">\r\n" +
					"      <JoinColumn refName=\"Code\" srcName=\"ItemCD\"/>\r\n" +
					"    </ForeignKey>\r\n" +
					"    <ForeignKey name=\"FKleft\" refEntity=\"jdbc_tool_test_ordermaster\">\r\n" +
					"      <JoinColumn refName=\"CorporateNA\" srcName=\"ItemNA\"/>\r\n" +
					"    </ForeignKey>\r\n" +
					"  </ForeignKeys>\r\n" +
					"</Entity>\r\n", new String(results.get(2).getResult(), "UTF-8"));
		
		assertEquals("jdbc_tool_test_unitmaster", results.get(3).getName());
		assertEquals(
				"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\r\n" +
					"<Entity database=\"" + dbScheme + "\" phyName=\"jdbc_tool_test_unitmaster\">\r\n" +
					"  <Columns>\r\n" +
					"    <Column name=\"objectID\" phyName=\"objectID\" type=\"Varchar\" size=\"50\"/>\r\n" +
					"    <Column name=\"Code\" phyName=\"Code\" type=\"Varchar\" size=\"50\"/>\r\n" +
					"    <Column name=\"Name\" phyName=\"Name\" type=\"Varchar\" size=\"50\"/>\r\n" +
					"  </Columns>\r\n" +
					"  <PrimaryKey>\r\n" +
					"    <ReferenceColumn refName=\"objectID\"/>\r\n" +
					"  </PrimaryKey>\r\n" +
					"  <UniqueKeys>\r\n" +
					"    <UniqueKey name=\"UnitMasterUK\">\r\n" +
					"      <ReferenceColumn refName=\"Code\"/>\r\n" +
					"      <ReferenceColumn refName=\"Name\"/>\r\n" +
					"    </UniqueKey>\r\n" +
					"  </UniqueKeys>\r\n" +
					"  <ForeignKeys>\r\n" +
					"    <ForeignKey name=\"FKRight\" refEntity=\"jdbc_tool_test_ordermaster\">\r\n" +
					"      <JoinColumn refName=\"CorporateNA\" srcName=\"Name\"/>\r\n" +
					"    </ForeignKey>\r\n" +
					"  </ForeignKeys>\r\n" +
					"</Entity>\r\n", new String(results.get(3).getResult(), "UTF-8"));
		assertEquals("jdbc_tool_test_ordermaster", results.get(4).getName());
		assertEquals(
				"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\r\n" +
					"<Entity database=\"" + dbScheme + "\" phyName=\"jdbc_tool_test_ordermaster\">\r\n" +
					"  <Columns>\r\n" +
					"    <Column name=\"objectID\" phyName=\"objectID\" type=\"Varchar\" size=\"50\"/>\r\n" +
					"    <Column name=\"SlipNO\" phyName=\"SlipNO\" type=\"Varchar\" size=\"50\"/>\r\n" +
					"    <Column name=\"CorporateNA_KANA\" phyName=\"CorporateNA_KANA\" type=\"Varchar\" size=\"50\"/>\r\n" +
					"    <Column name=\"CorporateNA\" phyName=\"CorporateNA\" type=\"Varchar\" size=\"50\"/>\r\n" +
					"  </Columns>\r\n" +
					"  <PrimaryKey>\r\n" +
					"    <ReferenceColumn refName=\"objectID\"/>\r\n" +
					"  </PrimaryKey>\r\n" +
					"  <UniqueKeys>\r\n" +
					"    <UniqueKey name=\"UK_J\">\r\n" +
					"      <ReferenceColumn refName=\"objectID\"/>\r\n" +
					"    </UniqueKey>\r\n" +
					"  </UniqueKeys>\r\n" +
					"</Entity>\r\n", new String(results.get(4).getResult(), "UTF-8"));
		assertEquals("jdbc_tool_test_pk_uk_mainmaster", results.get(5).getName());
		assertEquals(
				"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\r\n" +
					"<Entity database=\"" + dbScheme + "\" phyName=\"jdbc_tool_test_pk_uk_mainmaster\">\r\n" +
					"  <Columns>\r\n" +
					"    <Column name=\"objectID\" phyName=\"objectID\" type=\"Varchar\" size=\"50\"/>\r\n" +
					"    <Column name=\"ItemCD\" phyName=\"ItemCD\" type=\"Varchar\" size=\"50\"/>\r\n" +
					"    <Column name=\"ItemNA\" phyName=\"ItemNA\" type=\"Varchar\" size=\"50\"/>\r\n" +
					"    <Column name=\"UnitPrice\" phyName=\"UnitPrice\" type=\"Integer\"/>\r\n" +
					"    <Column name=\"SellPrice\" phyName=\"SellPrice\" type=\"Integer\"/>\r\n" +
					"    <Column name=\"Flag\" phyName=\"Flag\" type=\"Integer\"/>\r\n" +
					"    <Column name=\"PriceFlag\" phyName=\"PriceFlag\" type=\"Integer\"/>\r\n" +
					"    <Column name=\"ExclusiveFG\" phyName=\"ExclusiveFG\" type=\"Varchar\" size=\"32\" exclusiveKey=\"true\"/>\r\n" +
					"    <Column name=\"RegisteredDT\" phyName=\"RegisteredDT\" type=\"Date\" generate=\"RegisteredDate\"/>\r\n" +
					"    <Column name=\"UpdatedDT\" phyName=\"UpdatedDT\" type=\"Date\" generate=\"UpdatedDate\"/>\r\n" +
					"  </Columns>\r\n" +
					"  <PrimaryKey>\r\n" +
					"    <ReferenceColumn refName=\"objectID\"/>\r\n" +
					"  </PrimaryKey>\r\n" +
					"  <UniqueKeys>\r\n" +
					"    <UniqueKey name=\"jdbc_tool_test_uk\">\r\n" +
					"      <ReferenceColumn refName=\"ItemCD\"/>\r\n" +
					"      <ReferenceColumn refName=\"ItemNA\"/>\r\n" +
					"    </UniqueKey>\r\n" +
					"    <UniqueKey name=\"jdbc_tool_test_uk2\">\r\n" +
					"      <ReferenceColumn refName=\"ItemCD\"/>\r\n" +
					"      <ReferenceColumn refName=\"UnitPrice\"/>\r\n" +
					"    </UniqueKey>\r\n" +
					"  </UniqueKeys>\r\n" +
					"  <ForeignKeys>\r\n" +
					"    <ForeignKey name=\"FK_OrderMaster\" refEntity=\"jdbc_tool_test_ordermaster\">\r\n" +
					"      <JoinColumn refName=\"objectID\" srcName=\"ItemCD\"/>\r\n" +
					"    </ForeignKey>\r\n" +
					"    <ForeignKey name=\"FK_MainMaster\" refEntity=\"jdbc_tool_test_mainmaster\">\r\n" +
					"      <JoinColumn refName=\"objectID\" srcName=\"objectID\"/>\r\n" +
					"    </ForeignKey>\r\n" +
					"    <ForeignKey name=\"FK_UnitMaster\" refEntity=\"jdbc_tool_test_unitmaster\">\r\n" +
					"      <JoinColumn refName=\"Code\" srcName=\"objectID\"/>\r\n" +
					"    </ForeignKey>\r\n" +
					"  </ForeignKeys>\r\n" +
					"</Entity>\r\n", new String(results.get(5).getResult(), "UTF-8"));
		assertEquals("jdcb_tool_test_allColumntype", results.get(6).getName());
		assertEquals(
				"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\r\n" +
					"<Entity database=\"" + dbScheme + "\" phyName=\"jdcb_tool_test_allColumntype\">\r\n" +
					"  <Columns>\r\n" +
					"    <Column name=\"objectID\" phyName=\"objectID\" type=\"Varchar\" size=\"32\"/>\r\n" +
					"    <Column name=\"StringColumn\" phyName=\"StringColumn\" type=\"Varchar\" size=\"32\"/>\r\n" +
					"    <Column name=\"IntColumn\" phyName=\"IntColumn\" type=\"Integer\" size=\"8\"/>\r\n" +
					"    <Column name=\"NStringColumn\" phyName=\"NStringColumn\" type=\"Varchar\" size=\"32\"/>\r\n" +
					"    <Column name=\"DateTimeColumn\" phyName=\"DateTimeColumn\" type=\"Date\"/>\r\n" +
					"    <Column name=\"YMColumn\" phyName=\"YMColumn\" type=\"Varchar\" size=\"6\"/>\r\n" +
					"    <Column name=\"CurrencyColumn\" phyName=\"CurrencyColumn\" type=\"Double\" size=\"32\" scale=\"2\"/>\r\n" +
					"    <Column name=\"LongColumn\" phyName=\"LongColumn\" type=\"Long\" size=\"32\"/>\r\n" +
					"    <Column name=\"CompanyCD\" phyName=\"CompanyCD\" type=\"Varchar\" size=\"32\"/>\r\n" +
					"    <Column name=\"ExclusiveFG\" phyName=\"ExclusiveFG\" type=\"Varchar\" size=\"32\" exclusiveKey=\"true\"/>\r\n" +
					"    <Column name=\"RegisteredPerson\" phyName=\"RegisteredPerson\" type=\"Varchar\" size=\"32\"/>\r\n" +
					"    <Column name=\"RegisteredDT\" phyName=\"RegisteredDT\" type=\"Date\" generate=\"RegisteredDate\"/>\r\n" +
					"    <Column name=\"UpdatedPerson\" phyName=\"UpdatedPerson\" type=\"Varchar\" size=\"32\"/>\r\n" +
					"    <Column name=\"UpdatedDT\" phyName=\"UpdatedDT\" type=\"Date\" generate=\"UpdatedDate\"/>\r\n" +
					"  </Columns>\r\n" +
					"  <PrimaryKey>\r\n" +
					"    <ReferenceColumn refName=\"objectID\"/>\r\n" +
					"  </PrimaryKey>\r\n" +
					"  <UniqueKeys>\r\n" +
					"    <UniqueKey name=\"MulitipleColumnUKName\">\r\n" +
					"      <ReferenceColumn refName=\"objectID\"/>\r\n" +
					"      <ReferenceColumn refName=\"CompanyCD\"/>\r\n" +
					"    </UniqueKey>\r\n" +
					"    <UniqueKey name=\"OtherUKName\">\r\n" +
					"      <ReferenceColumn refName=\"objectID\"/>\r\n" +
					"      <ReferenceColumn refName=\"StringColumn\"/>\r\n" +
					"      <ReferenceColumn refName=\"IntColumn\"/>\r\n" +
					"      <ReferenceColumn refName=\"NStringColumn\"/>\r\n" +
					"      <ReferenceColumn refName=\"DateTimeColumn\"/>\r\n" +
					"      <ReferenceColumn refName=\"YMColumn\"/>\r\n" +
					"      <ReferenceColumn refName=\"CurrencyColumn\"/>\r\n" +
					"      <ReferenceColumn refName=\"LongColumn\"/>\r\n" +
					"      <ReferenceColumn refName=\"CompanyCD\"/>\r\n" +
					"      <ReferenceColumn refName=\"ExclusiveFG\"/>\r\n" +
					"      <ReferenceColumn refName=\"RegisteredPerson\"/>\r\n" +
					"      <ReferenceColumn refName=\"RegisteredDT\"/>\r\n" +
					"      <ReferenceColumn refName=\"UpdatedPerson\"/>\r\n" +
					"      <ReferenceColumn refName=\"UpdatedDT\"/>\r\n" +
					"    </UniqueKey>\r\n" +
					"    <UniqueKey name=\"SingleColumnUK\">\r\n" +
					"      <ReferenceColumn refName=\"StringColumn\"/>\r\n" +
					"    </UniqueKey>\r\n" +
					"  </UniqueKeys>\r\n" +
					"  <ForeignKeys>\r\n" +
					"    <ForeignKey name=\"onlyColumnFKName\" refEntity=\"jdbc_tool_test_pk_uk_mainmaster\">\r\n" +
					"      <JoinColumn refName=\"objectID\" srcName=\"objectID\"/>\r\n" +
					"      <JoinColumn refName=\"ItemCD\" srcName=\"StringColumn\"/>\r\n" +
					"    </ForeignKey>\r\n" +
					"    <ForeignKey name=\"onlyConstValueFKName\" refEntity=\"jdbc_tool_test_pk_uk_mainmaster\">\r\n" +
					"      <JoinColumn refName=\"objectID\" constValue=\"1001\"/>\r\n" +
					"      <JoinColumn refName=\"ItemCD\" constValue=\"9001\"/>\r\n" +
					"    </ForeignKey>\r\n" +
					"    <ForeignKey name=\"columnAndConstValueMixFKName\" refEntity=\"jdbc_tool_test_pk_uk_mainmaster\">\r\n" +
					"      <JoinColumn refName=\"objectID\" srcName=\"objectID\"/>\r\n" +
					"      <JoinColumn refName=\"Flag\" constValue=\"1001\"/>\r\n" +
					"    </ForeignKey>\r\n" +
					"  </ForeignKeys>\r\n" +
					"</Entity>\r\n", new String(results.get(6).getResult(), "UTF-8"));
		assertEquals("SLocalizationLabel", results.get(7).getName());
		assertEquals(
				"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\r\n" +
					"<Entity database=\"" + dbScheme + "\" phyName=\"SLocalizationLabel\">\r\n" +
					"  <Columns>\r\n" +
					"    <Column name=\"objectID\" phyName=\"objectID\" type=\"Varchar\" size=\"32\"/>\r\n" +
					"    <Column name=\"country1\" phyName=\"country1\" type=\"Varchar\" size=\"255\"/>\r\n" +
					"    <Column name=\"country2\" phyName=\"country2\" type=\"Varchar\" size=\"255\"/>\r\n" +
					"    <Column name=\"country3\" phyName=\"country3\" type=\"Varchar\" size=\"255\"/>\r\n" +
					"    <Column name=\"country4\" phyName=\"country4\" type=\"Varchar\" size=\"255\"/>\r\n" +
					"    <Column name=\"country5\" phyName=\"country5\" type=\"Varchar\" size=\"255\"/>\r\n" +
					"  </Columns>\r\n" +
					"  <PrimaryKey>\r\n" +
					"    <ReferenceColumn refName=\"objectID\"/>\r\n" +
					"  </PrimaryKey>\r\n" +
					"</Entity>\r\n", new String(results.get(7).getResult(), "UTF-8"));
	}
}
