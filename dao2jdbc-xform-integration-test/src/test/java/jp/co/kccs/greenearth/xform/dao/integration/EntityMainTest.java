package jp.co.kccs.greenearth.xform.dao.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import jp.co.kccs.greenearth.commons.GFrameworkProperties;
import jp.co.kccs.greenearth.commons.GFrameworkUtils;
import jp.co.kccs.greenearth.commons.db.GConnectionManager;
import jp.co.kccs.greenearth.commons.utils.GFileUtils;
import jp.co.kccs.greenearth.testutils.dbunit.TestHelper;
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
import java.util.Base64;
import java.util.List;
import java.util.Map;

import static jp.co.kccs.greenearth.testutils.dbunit.TestHelper.cleanInsert;
import static jp.co.kccs.greenearth.xform.dao.integration.Utils.URL;
import static org.junit.Assert.assertEquals;

public abstract class EntityMainTest {
	protected static String dbScheme;

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
	public void testExport_singleEntityXml() throws Exception {

		cleanInsert("jp/co/kccs/greenearth/xform/dao/entity/cmd/gdao2xformentitycmdservicetest/import_fullcase.xml");
		ApiRequest request = parseRequestFromFile(getCommonSetting(), "jp/co/kccs/greenearth/xform/dao/entity/cmd/gdao2xformentitycmdservicetest/condition_singleEntity.yaml");

		String results = convertEntity2Xml(request);
		assertEquals(
				"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
						"<Entity database=\"" + dbScheme + "\" phyName=\"jdbc_tool_test_fullCaseMainEntity\">\n" +
						"  <Columns>\n" +
						"    <Column name=\"objectID\" phyName=\"objectID\" type=\"Varchar\" size=\"32\"/>\n" +
						"    <Column name=\"StringColumn\" phyName=\"StringColumn\" type=\"Varchar\" size=\"32\"/>\n" +
						"    <Column name=\"IntColumn\" phyName=\"IntColumn\" type=\"Integer\" size=\"8\"/>\n" +
						"    <Column name=\"NStringColumn\" phyName=\"NStringColumn\" type=\"Varchar\" size=\"32\"/>\n" +
						"    <Column name=\"DateTimeColumn\" phyName=\"DateTimeColumn\" type=\"Date\"/>\n" +
						"    <Column name=\"YMColumn\" phyName=\"YMColumn\" type=\"Varchar\" size=\"6\"/>\n" +
						"    <Column name=\"CurrencyColumn\" phyName=\"CurrencyColumn\" type=\"Double\" size=\"32\" scale=\"2\"/>\n" +
						"    <Column name=\"LongColumn\" phyName=\"LongColumn\" type=\"Long\" size=\"32\"/>\n" +
						"    <Column name=\"CompanyCD\" phyName=\"CompanyCD\" type=\"Varchar\" size=\"32\"/>\n" +
						"    <Column name=\"ExclusiveFG\" phyName=\"ExclusiveFG\" type=\"Varchar\" size=\"32\" exclusiveKey=\"true\"/>\n" +
						"    <Column name=\"RegisteredPerson\" phyName=\"RegisteredPerson\" type=\"Varchar\" size=\"32\"/>\n" +
						"    <Column name=\"RegisteredDT\" phyName=\"RegisteredDT\" type=\"Date\" generate=\"RegisteredDate\"/>\n" +
						"    <Column name=\"UpdatedPerson\" phyName=\"UpdatedPerson\" type=\"Varchar\" size=\"32\"/>\n" +
						"    <Column name=\"UpdatedDT\" phyName=\"UpdatedDT\" type=\"Date\" generate=\"UpdatedDate\"/>\n" +
						"  </Columns>\n" +
						"  <PrimaryKey>\n" +
						"    <ReferenceColumn refName=\"objectID\"/>\n" +
						"  </PrimaryKey>\n" +
						"  <UniqueKeys>\n" +
						"    <UniqueKey name=\"MultipleColumnUK\">\n" +
						"      <ReferenceColumn refName=\"StringColumn\"/>\n" +
						"      <ReferenceColumn refName=\"IntColumn\"/>\n" +
						"      <ReferenceColumn refName=\"NStringColumn\"/>\n" +
						"    </UniqueKey>\n" +
						"    <UniqueKey name=\"OtherUKName\">\n" +
						"      <ReferenceColumn refName=\"IntColumn\"/>\n" +
						"      <ReferenceColumn refName=\"NStringColumn\"/>\n" +
						"      <ReferenceColumn refName=\"DateTimeColumn\"/>\n" +
						"      <ReferenceColumn refName=\"YMColumn\"/>\n" +
						"    </UniqueKey>\n" +
						"    <UniqueKey name=\"SingleColumnUK\">\n" +
						"      <ReferenceColumn refName=\"StringColumn\"/>\n" +
						"    </UniqueKey>\n" +
						"  </UniqueKeys>\n" +
						"  <ForeignKeys>\n" +
						"    <ForeignKey name=\"RightJoinRefColumnAndConstValue\" refEntity=\"jdbc_tool_test_fullCaseRightJoinEntity\">\n" +
						"      <JoinColumn refName=\"NStringColumnRight\" srcName=\"NStringColumn\"/>\n" +
						"      <JoinColumn refName=\"StringColumnRight\" constValue=\"7001\"/>\n" +
						"    </ForeignKey>\n" +
						"    <ForeignKey name=\"InnerJoinRefCoulumnAndConstValue\" refEntity=\"jdbc_tool_test_fullCaseInnerJoinEntity\">\n" +
						"      <JoinColumn refName=\"StringColumnInner\" srcName=\"StringColumn\"/>\n" +
						"      <JoinColumn refName=\"NStringColumnInner\" constValue=\"9001\"/>\n" +
						"    </ForeignKey>\n" +
						"    <ForeignKey name=\"LeftJoinRefColumnAndConstValue\" refEntity=\"jdbc_tool_test_fullCaseLeftJoinEntity\">\n" +
						"      <JoinColumn refName=\"IntColumnLeft\" srcName=\"IntColumn\"/>\n" +
						"      <JoinColumn refName=\"LongColumnLeft\" constValue=\"8001\"/>\n" +
						"    </ForeignKey>\n" +
						"  </ForeignKeys>\n" +
						"</Entity>\n", results);
	}
//
//	@Test
//	public void testExport_allColumnTypeEntityXml() throws Exception {
//
//		cleanInsert("jp/co/kccs/greenearth/xform/dao/entity/cmd/gdao2xformentitycmdservicetest/import_entity.xml");
//		ApiRequest request = parseRequestFromFile(getCommonSetting(), "jp/co/kccs/greenearth/xform/dao/entity/cmd/gdao2xformentitycmdservicetest/condition_allColumnTypeEntity.yaml");
//		String results = convertEntity2Xml(request);
//
//		assertEquals(
//				"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
//						"<Entity database=\"" + dbScheme + "\" phyName=\"jdcb_tool_test_allColumntype\">\n" +
//						"  <Columns>\n" +
//						"    <Column name=\"objectID\" phyName=\"objectID\" type=\"Varchar\" size=\"32\"/>\n" +
//						"    <Column name=\"StringColumn\" phyName=\"StringColumn\" type=\"Varchar\" size=\"32\"/>\n" +
//						"    <Column name=\"IntColumn\" phyName=\"IntColumn\" type=\"Integer\" size=\"8\"/>\n" +
//						"    <Column name=\"NStringColumn\" phyName=\"NStringColumn\" type=\"Varchar\" size=\"32\"/>\n" +
//						"    <Column name=\"DateTimeColumn\" phyName=\"DateTimeColumn\" type=\"Date\"/>\n" +
//						"    <Column name=\"YMColumn\" phyName=\"YMColumn\" type=\"Varchar\" size=\"6\"/>\n" +
//						"    <Column name=\"CurrencyColumn\" phyName=\"CurrencyColumn\" type=\"Double\" size=\"32\" scale=\"2\"/>\n" +
//						"    <Column name=\"LongColumn\" phyName=\"LongColumn\" type=\"Long\" size=\"32\"/>\n" +
//						"    <Column name=\"CompanyCD\" phyName=\"CompanyCD\" type=\"Varchar\" size=\"32\"/>\n" +
//						"    <Column name=\"ExclusiveFG\" phyName=\"ExclusiveFG\" type=\"Varchar\" size=\"32\" exclusiveKey=\"true\"/>\n" +
//						"    <Column name=\"RegisteredPerson\" phyName=\"RegisteredPerson\" type=\"Varchar\" size=\"32\"/>\n" +
//						"    <Column name=\"RegisteredDT\" phyName=\"RegisteredDT\" type=\"Date\" generate=\"RegisteredDate\"/>\n" +
//						"    <Column name=\"UpdatedPerson\" phyName=\"UpdatedPerson\" type=\"Varchar\" size=\"32\"/>\n" +
//						"    <Column name=\"UpdatedDT\" phyName=\"UpdatedDT\" type=\"Date\" generate=\"UpdatedDate\"/>\n" +
//						"  </Columns>\n" +
//						"  <PrimaryKey>\n" +
//						"    <ReferenceColumn refName=\"objectID\"/>\n" +
//						"  </PrimaryKey>\n" +
//						"  <UniqueKeys>\n" +
//						"    <UniqueKey name=\"MulitipleColumnUKName\">\n" +
//						"      <ReferenceColumn refName=\"objectID\"/>\n" +
//						"      <ReferenceColumn refName=\"CompanyCD\"/>\n" +
//						"    </UniqueKey>\n" +
//						"    <UniqueKey name=\"OtherUKName\">\n" +
//						"      <ReferenceColumn refName=\"objectID\"/>\n" +
//						"      <ReferenceColumn refName=\"StringColumn\"/>\n" +
//						"      <ReferenceColumn refName=\"IntColumn\"/>\n" +
//						"      <ReferenceColumn refName=\"NStringColumn\"/>\n" +
//						"      <ReferenceColumn refName=\"DateTimeColumn\"/>\n" +
//						"      <ReferenceColumn refName=\"YMColumn\"/>\n" +
//						"      <ReferenceColumn refName=\"CurrencyColumn\"/>\n" +
//						"      <ReferenceColumn refName=\"LongColumn\"/>\n" +
//						"      <ReferenceColumn refName=\"CompanyCD\"/>\n" +
//						"      <ReferenceColumn refName=\"ExclusiveFG\"/>\n" +
//						"      <ReferenceColumn refName=\"RegisteredPerson\"/>\n" +
//						"      <ReferenceColumn refName=\"RegisteredDT\"/>\n" +
//						"      <ReferenceColumn refName=\"UpdatedPerson\"/>\n" +
//						"      <ReferenceColumn refName=\"UpdatedDT\"/>\n" +
//						"    </UniqueKey>\n" +
//						"    <UniqueKey name=\"SingleColumnUK\">\n" +
//						"      <ReferenceColumn refName=\"StringColumn\"/>\n" +
//						"    </UniqueKey>\n" +
//						"  </UniqueKeys>\n" +
//						"  <ForeignKeys>\n" +
//						"    <ForeignKey name=\"onlyConstValueFKName\" refEntity=\"jdbc_tool_test_pk_uk_mainmaster\">\n" +
//						"      <JoinColumn refName=\"objectID\" constValue=\"1001\"/>\n" +
//						"      <JoinColumn refName=\"ItemCD\" constValue=\"9001\"/>\n" +
//						"    </ForeignKey>\n" +
//						"    <ForeignKey name=\"columnAndConstValueMixFKName\" refEntity=\"jdbc_tool_test_pk_uk_mainmaster\">\n" +
//						"      <JoinColumn refName=\"objectID\" srcName=\"objectID\"/>\n" +
//						"      <JoinColumn refName=\"Flag\" constValue=\"1001\"/>\n" +
//						"    </ForeignKey>\n" +
//						"  </ForeignKeys>\n" +
//						"</Entity>\n", results);
//	}
//
//	@Test
//	public void testExport_dataBaseIDXml() throws Exception {
////		cleanInsert("jp/co/kccs/greenearth/xform/dao/entity/cmd/gdao2xformentitycmdservicetest/import_databaseId.xml");
//		ApiRequest request = parseRequestFromFile(getCommonSetting(), "jp/co/kccs/greenearth/xform/dao/entity/cmd/gdao2xformentitycmdservicetest/condition_databaseId.yaml").getPath();
//
//		String results = convertEntity2Xml(request);
//
//		assertEquals(
//				"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
//						"<Entity database=\"" + dbScheme + "\" phyName=\"SError\">\n" +
//						"  <Columns>\n" +
//						"    <Column name=\"objectID\" phyName=\"objectID\" type=\"Varchar\" size=\"32\"/>\n" +
//						"    <Column name=\"errorNo\" phyName=\"errorNo\" type=\"Varchar\" size=\"10\"/>\n" +
//						"    <Column name=\"errorMessageID\" phyName=\"errorMessageID\" type=\"Varchar\" size=\"32\"/>\n" +
//						"    <Column name=\"entryUserID\" phyName=\"entryUserID\" type=\"Varchar\" size=\"20\"/>\n" +
//						"    <Column name=\"entryDate\" phyName=\"entryDate\" type=\"Date\"/>\n" +
//						"    <Column name=\"updateUserID\" phyName=\"updateUserID\" type=\"Varchar\" size=\"20\"/>\n" +
//						"    <Column name=\"updateDate\" phyName=\"updateDate\" type=\"Date\"/>\n" +
//						"    <Column name=\"exclusiveFlag\" phyName=\"exclusiveFlag\" type=\"Varchar\" size=\"32\"/>\n" +
//						"    <Column name=\"errorType\" phyName=\"errorType\" type=\"Varchar\" size=\"1\"/>\n" +
//						"    <Column name=\"description\" phyName=\"description\" type=\"Varchar\" size=\"255\"/>\n" +
//						"    <Column name=\"url\" phyName=\"url\" type=\"Varchar\" size=\"255\"/>\n" +
//						"  </Columns>\n" +
//						"  <PrimaryKey>\n" +
//						"    <ReferenceColumn refName=\"objectID\"/>\n" +
//						"  </PrimaryKey>\n" +
//						"  <UniqueKeys>\n" +
//						"    <UniqueKey>\n" +
//						"      <ReferenceColumn refName=\"errorNo\"/>\n" +
//						"    </UniqueKey>\n" +
//						"  </UniqueKeys>\n" +
//						"  <ForeignKeys>\n" +
//						"    <ForeignKey refEntity=\"SLocalization\">\n" +
//						"      <JoinColumn refName=\"objectID\" srcName=\"errorMessageID\"/>\n" +
//						"    </ForeignKey>\n" +
//						"  </ForeignKeys>\n" +
//						"</Entity>\n", new String(results.getResult(), "UTF-8"));
//		assertEquals("SLocalization", results.get(1).getName());
//		assertEquals(
//				"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
//						"<Entity database=\"" + dbScheme + "\" phyName=\"SLocalization\">\n" +
//						"  <Columns>\n" +
//						"    <Column name=\"objectID\" phyName=\"objectID\" type=\"Varchar\" size=\"32\"/>\n" +
//						"    <Column name=\"country1\" phyName=\"country1\" type=\"Varchar\" size=\"255\"/>\n" +
//						"    <Column name=\"country2\" phyName=\"country2\" type=\"Varchar\" size=\"255\"/>\n" +
//						"    <Column name=\"country3\" phyName=\"country3\" type=\"Varchar\" size=\"255\"/>\n" +
//						"    <Column name=\"country4\" phyName=\"country4\" type=\"Varchar\" size=\"255\"/>\n" +
//						"    <Column name=\"country5\" phyName=\"country5\" type=\"Varchar\" size=\"255\"/>\n" +
//						"  </Columns>\n" +
//						"  <PrimaryKey>\n" +
//						"    <ReferenceColumn refName=\"objectID\"/>\n" +
//						"  </PrimaryKey>\n" +
//						"</Entity>\n", new String(results.get(1).getResult(), "UTF-8"));
//		assertEquals("jdbc_tool_test_mainmaster", results.get(2).getName());
//		assertEquals(
//				"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
//						"<Entity database=\"" + dbScheme + "\" phyName=\"jdbc_tool_test_mainmaster\">\n" +
//						"  <Columns>\n" +
//						"    <Column name=\"objectID\" phyName=\"objectID\" type=\"Varchar\" size=\"50\"/>\n" +
//						"    <Column name=\"ItemCD\" phyName=\"ItemCD\" type=\"Varchar\" size=\"50\"/>\n" +
//						"    <Column name=\"ItemNA\" phyName=\"ItemNA\" type=\"Varchar\" size=\"50\"/>\n" +
//						"    <Column name=\"UnitPrice\" phyName=\"UnitPrice\" type=\"Integer\"/>\n" +
//						"    <Column name=\"SellPrice\" phyName=\"SellPrice\" type=\"Integer\"/>\n" +
//						"    <Column name=\"Flag\" phyName=\"Flag\" type=\"Integer\"/>\n" +
//						"    <Column name=\"PriceFlag\" phyName=\"PriceFlag\" type=\"Integer\"/>\n" +
//						"    <Column name=\"ExclusiveFG\" phyName=\"ExclusiveFG\" type=\"Varchar\" size=\"32\" exclusiveKey=\"true\"/>\n" +
//						"    <Column name=\"RegisteredDT\" phyName=\"RegisteredDT\" type=\"Date\" generate=\"RegisteredDate\"/>\n" +
//						"    <Column name=\"UpdatedDT\" phyName=\"UpdatedDT\" type=\"Date\" generate=\"UpdatedDate\"/>\n" +
//						"  </Columns>\n" +
//						"  <PrimaryKey>\n" +
//						"    <ReferenceColumn refName=\"objectID\"/>\n" +
//						"  </PrimaryKey>\n" +
//						"  <UniqueKeys>\n" +
//						"    <UniqueKey name=\"MainMasterUniqueKey\">\n" +
//						"      <ReferenceColumn refName=\"objectID\"/>\n" +
//						"    </UniqueKey>\n" +
//						"  </UniqueKeys>\n" +
//						"  <ForeignKeys>\n" +
//						"    <ForeignKey name=\"FK\" refEntity=\"jdbc_tool_test_unitmaster\">\n" +
//						"      <JoinColumn refName=\"Code\" srcName=\"ItemCD\"/>\n" +
//						"    </ForeignKey>\n" +
//						"    <ForeignKey name=\"FKleft\" refEntity=\"jdbc_tool_test_ordermaster\">\n" +
//						"      <JoinColumn refName=\"CorporateNA\" srcName=\"ItemNA\"/>\n" +
//						"    </ForeignKey>\n" +
//						"  </ForeignKeys>\n" +
//						"</Entity>\n", new String(results.get(2).getResult(), "UTF-8"));
//
//		assertEquals("jdbc_tool_test_unitmaster", results.get(3).getName());
//		assertEquals(
//				"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
//						"<Entity database=\"" + dbScheme + "\" phyName=\"jdbc_tool_test_unitmaster\">\n" +
//						"  <Columns>\n" +
//						"    <Column name=\"objectID\" phyName=\"objectID\" type=\"Varchar\" size=\"50\"/>\n" +
//						"    <Column name=\"Code\" phyName=\"Code\" type=\"Varchar\" size=\"50\"/>\n" +
//						"    <Column name=\"Name\" phyName=\"Name\" type=\"Varchar\" size=\"50\"/>\n" +
//						"  </Columns>\n" +
//						"  <PrimaryKey>\n" +
//						"    <ReferenceColumn refName=\"objectID\"/>\n" +
//						"  </PrimaryKey>\n" +
//						"  <UniqueKeys>\n" +
//						"    <UniqueKey name=\"UnitMasterUK\">\n" +
//						"      <ReferenceColumn refName=\"Code\"/>\n" +
//						"      <ReferenceColumn refName=\"Name\"/>\n" +
//						"    </UniqueKey>\n" +
//						"  </UniqueKeys>\n" +
//						"  <ForeignKeys>\n" +
//						"    <ForeignKey name=\"FKRight\" refEntity=\"jdbc_tool_test_ordermaster\">\n" +
//						"      <JoinColumn refName=\"CorporateNA\" srcName=\"Name\"/>\n" +
//						"    </ForeignKey>\n" +
//						"  </ForeignKeys>\n" +
//						"</Entity>\n", new String(results.get(3).getResult(), "UTF-8"));
//		assertEquals("jdbc_tool_test_ordermaster", results.get(4).getName());
//		assertEquals(
//				"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
//						"<Entity database=\"" + dbScheme + "\" phyName=\"jdbc_tool_test_ordermaster\">\n" +
//						"  <Columns>\n" +
//						"    <Column name=\"objectID\" phyName=\"objectID\" type=\"Varchar\" size=\"50\"/>\n" +
//						"    <Column name=\"SlipNO\" phyName=\"SlipNO\" type=\"Varchar\" size=\"50\"/>\n" +
//						"    <Column name=\"CorporateNA_KANA\" phyName=\"CorporateNA_KANA\" type=\"Varchar\" size=\"50\"/>\n" +
//						"    <Column name=\"CorporateNA\" phyName=\"CorporateNA\" type=\"Varchar\" size=\"50\"/>\n" +
//						"  </Columns>\n" +
//						"  <PrimaryKey>\n" +
//						"    <ReferenceColumn refName=\"objectID\"/>\n" +
//						"  </PrimaryKey>\n" +
//						"  <UniqueKeys>\n" +
//						"    <UniqueKey name=\"UK_J\">\n" +
//						"      <ReferenceColumn refName=\"objectID\"/>\n" +
//						"    </UniqueKey>\n" +
//						"  </UniqueKeys>\n" +
//						"</Entity>\n", new String(results.get(4).getResult(), "UTF-8"));
//		assertEquals("jdbc_tool_test_pk_uk_mainmaster", results.get(5).getName());
//		assertEquals(
//				"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
//						"<Entity database=\"" + dbScheme + "\" phyName=\"jdbc_tool_test_pk_uk_mainmaster\">\n" +
//						"  <Columns>\n" +
//						"    <Column name=\"objectID\" phyName=\"objectID\" type=\"Varchar\" size=\"50\"/>\n" +
//						"    <Column name=\"ItemCD\" phyName=\"ItemCD\" type=\"Varchar\" size=\"50\"/>\n" +
//						"    <Column name=\"ItemNA\" phyName=\"ItemNA\" type=\"Varchar\" size=\"50\"/>\n" +
//						"    <Column name=\"UnitPrice\" phyName=\"UnitPrice\" type=\"Integer\"/>\n" +
//						"    <Column name=\"SellPrice\" phyName=\"SellPrice\" type=\"Integer\"/>\n" +
//						"    <Column name=\"Flag\" phyName=\"Flag\" type=\"Integer\"/>\n" +
//						"    <Column name=\"PriceFlag\" phyName=\"PriceFlag\" type=\"Integer\"/>\n" +
//						"    <Column name=\"ExclusiveFG\" phyName=\"ExclusiveFG\" type=\"Varchar\" size=\"32\" exclusiveKey=\"true\"/>\n" +
//						"    <Column name=\"RegisteredDT\" phyName=\"RegisteredDT\" type=\"Date\" generate=\"RegisteredDate\"/>\n" +
//						"    <Column name=\"UpdatedDT\" phyName=\"UpdatedDT\" type=\"Date\" generate=\"UpdatedDate\"/>\n" +
//						"  </Columns>\n" +
//						"  <PrimaryKey>\n" +
//						"    <ReferenceColumn refName=\"objectID\"/>\n" +
//						"  </PrimaryKey>\n" +
//						"  <UniqueKeys>\n" +
//						"    <UniqueKey name=\"jdbc_tool_test_uk\">\n" +
//						"      <ReferenceColumn refName=\"ItemCD\"/>\n" +
//						"      <ReferenceColumn refName=\"ItemNA\"/>\n" +
//						"    </UniqueKey>\n" +
//						"    <UniqueKey name=\"jdbc_tool_test_uk2\">\n" +
//						"      <ReferenceColumn refName=\"ItemCD\"/>\n" +
//						"      <ReferenceColumn refName=\"UnitPrice\"/>\n" +
//						"    </UniqueKey>\n" +
//						"  </UniqueKeys>\n" +
//						"  <ForeignKeys>\n" +
//						"    <ForeignKey name=\"FK_OrderMaster\" refEntity=\"jdbc_tool_test_ordermaster\">\n" +
//						"      <JoinColumn refName=\"objectID\" srcName=\"ItemCD\"/>\n" +
//						"    </ForeignKey>\n" +
//						"    <ForeignKey name=\"FK_MainMaster\" refEntity=\"jdbc_tool_test_mainmaster\">\n" +
//						"      <JoinColumn refName=\"objectID\" srcName=\"objectID\"/>\n" +
//						"    </ForeignKey>\n" +
//						"    <ForeignKey name=\"FK_UnitMaster\" refEntity=\"jdbc_tool_test_unitmaster\">\n" +
//						"      <JoinColumn refName=\"Code\" srcName=\"objectID\"/>\n" +
//						"    </ForeignKey>\n" +
//						"  </ForeignKeys>\n" +
//						"</Entity>\n", new String(results.get(5).getResult(), "UTF-8"));
//		assertEquals("jdcb_tool_test_allColumntype", results.get(6).getName());
//		assertEquals(
//				"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
//						"<Entity database=\"" + dbScheme + "\" phyName=\"jdcb_tool_test_allColumntype\">\n" +
//						"  <Columns>\n" +
//						"    <Column name=\"objectID\" phyName=\"objectID\" type=\"Varchar\" size=\"32\"/>\n" +
//						"    <Column name=\"StringColumn\" phyName=\"StringColumn\" type=\"Varchar\" size=\"32\"/>\n" +
//						"    <Column name=\"IntColumn\" phyName=\"IntColumn\" type=\"Integer\" size=\"8\"/>\n" +
//						"    <Column name=\"NStringColumn\" phyName=\"NStringColumn\" type=\"Varchar\" size=\"32\"/>\n" +
//						"    <Column name=\"DateTimeColumn\" phyName=\"DateTimeColumn\" type=\"Date\"/>\n" +
//						"    <Column name=\"YMColumn\" phyName=\"YMColumn\" type=\"Varchar\" size=\"6\"/>\n" +
//						"    <Column name=\"CurrencyColumn\" phyName=\"CurrencyColumn\" type=\"Double\" size=\"32\" scale=\"2\"/>\n" +
//						"    <Column name=\"LongColumn\" phyName=\"LongColumn\" type=\"Long\" size=\"32\"/>\n" +
//						"    <Column name=\"CompanyCD\" phyName=\"CompanyCD\" type=\"Varchar\" size=\"32\"/>\n" +
//						"    <Column name=\"ExclusiveFG\" phyName=\"ExclusiveFG\" type=\"Varchar\" size=\"32\" exclusiveKey=\"true\"/>\n" +
//						"    <Column name=\"RegisteredPerson\" phyName=\"RegisteredPerson\" type=\"Varchar\" size=\"32\"/>\n" +
//						"    <Column name=\"RegisteredDT\" phyName=\"RegisteredDT\" type=\"Date\" generate=\"RegisteredDate\"/>\n" +
//						"    <Column name=\"UpdatedPerson\" phyName=\"UpdatedPerson\" type=\"Varchar\" size=\"32\"/>\n" +
//						"    <Column name=\"UpdatedDT\" phyName=\"UpdatedDT\" type=\"Date\" generate=\"UpdatedDate\"/>\n" +
//						"  </Columns>\n" +
//						"  <PrimaryKey>\n" +
//						"    <ReferenceColumn refName=\"objectID\"/>\n" +
//						"  </PrimaryKey>\n" +
//						"  <UniqueKeys>\n" +
//						"    <UniqueKey name=\"MulitipleColumnUKName\">\n" +
//						"      <ReferenceColumn refName=\"objectID\"/>\n" +
//						"      <ReferenceColumn refName=\"CompanyCD\"/>\n" +
//						"    </UniqueKey>\n" +
//						"    <UniqueKey name=\"OtherUKName\">\n" +
//						"      <ReferenceColumn refName=\"objectID\"/>\n" +
//						"      <ReferenceColumn refName=\"StringColumn\"/>\n" +
//						"      <ReferenceColumn refName=\"IntColumn\"/>\n" +
//						"      <ReferenceColumn refName=\"NStringColumn\"/>\n" +
//						"      <ReferenceColumn refName=\"DateTimeColumn\"/>\n" +
//						"      <ReferenceColumn refName=\"YMColumn\"/>\n" +
//						"      <ReferenceColumn refName=\"CurrencyColumn\"/>\n" +
//						"      <ReferenceColumn refName=\"LongColumn\"/>\n" +
//						"      <ReferenceColumn refName=\"CompanyCD\"/>\n" +
//						"      <ReferenceColumn refName=\"ExclusiveFG\"/>\n" +
//						"      <ReferenceColumn refName=\"RegisteredPerson\"/>\n" +
//						"      <ReferenceColumn refName=\"RegisteredDT\"/>\n" +
//						"      <ReferenceColumn refName=\"UpdatedPerson\"/>\n" +
//						"      <ReferenceColumn refName=\"UpdatedDT\"/>\n" +
//						"    </UniqueKey>\n" +
//						"    <UniqueKey name=\"SingleColumnUK\">\n" +
//						"      <ReferenceColumn refName=\"StringColumn\"/>\n" +
//						"    </UniqueKey>\n" +
//						"  </UniqueKeys>\n" +
//						"  <ForeignKeys>\n" +
//						"    <ForeignKey name=\"onlyColumnFKName\" refEntity=\"jdbc_tool_test_pk_uk_mainmaster\">\n" +
//						"      <JoinColumn refName=\"objectID\" srcName=\"objectID\"/>\n" +
//						"      <JoinColumn refName=\"ItemCD\" srcName=\"StringColumn\"/>\n" +
//						"    </ForeignKey>\n" +
//						"    <ForeignKey name=\"onlyConstValueFKName\" refEntity=\"jdbc_tool_test_pk_uk_mainmaster\">\n" +
//						"      <JoinColumn refName=\"objectID\" constValue=\"1001\"/>\n" +
//						"      <JoinColumn refName=\"ItemCD\" constValue=\"9001\"/>\n" +
//						"    </ForeignKey>\n" +
//						"    <ForeignKey name=\"columnAndConstValueMixFKName\" refEntity=\"jdbc_tool_test_pk_uk_mainmaster\">\n" +
//						"      <JoinColumn refName=\"objectID\" srcName=\"objectID\"/>\n" +
//						"      <JoinColumn refName=\"Flag\" constValue=\"1001\"/>\n" +
//						"    </ForeignKey>\n" +
//						"  </ForeignKeys>\n" +
//						"</Entity>\n", new String(results.get(6).getResult(), "UTF-8"));
//		assertEquals("SLocalizationLabel", results.get(7).getName());
//		assertEquals(
//				"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
//						"<Entity database=\"" + dbScheme + "\" phyName=\"SLocalizationLabel\">\n" +
//						"  <Columns>\n" +
//						"    <Column name=\"objectID\" phyName=\"objectID\" type=\"Varchar\" size=\"32\"/>\n" +
//						"    <Column name=\"country1\" phyName=\"country1\" type=\"Varchar\" size=\"255\"/>\n" +
//						"    <Column name=\"country2\" phyName=\"country2\" type=\"Varchar\" size=\"255\"/>\n" +
//						"    <Column name=\"country3\" phyName=\"country3\" type=\"Varchar\" size=\"255\"/>\n" +
//						"    <Column name=\"country4\" phyName=\"country4\" type=\"Varchar\" size=\"255\"/>\n" +
//						"    <Column name=\"country5\" phyName=\"country5\" type=\"Varchar\" size=\"255\"/>\n" +
//						"  </Columns>\n" +
//						"  <PrimaryKey>\n" +
//						"    <ReferenceColumn refName=\"objectID\"/>\n" +
//						"  </PrimaryKey>\n" +
//						"</Entity>\n", new String(results.get(7).getResult(), "UTF-8"));
//	}

	public ApiRequest parseRequestFromFile(SettingRequest commonSetting, String filePath) {
		ApiRequest apiRequest = new ApiRequest();
		try (InputStream input = GFileUtils.getResource(filePath).openStream()) {
			List<Map<String, Object>> map = yaml.load(input);
			apiRequest.setValue(map.get(0));
			apiRequest.setSetting(commonSetting);
			dbScheme = getCommonSetting().toDaoCommonSetting().getDb().getSchema();
			return apiRequest;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
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
	public String convertEntity2Xml(ApiRequest request) {
		ResponseEntity<ApiResponse> entityExtractorResp = restTemplate.postForEntity(URL+":8082/entities/transformations/entity-extractors", request, ApiResponse.class);

		if (entityExtractorResp.getBody().isSuccess()) {
			Map<String, Object> entityExtractorRespData = (Map<String, Object>) entityExtractorResp.getBody().getData();
			ApiRequest entity2XmlReq = new ApiRequest();
			entity2XmlReq.setSetting(request.getSetting());
			entity2XmlReq.setValue(entityExtractorRespData);
			ResponseEntity<ApiResponse> entity2xmlResp = restTemplate.postForEntity(URL+":8080/dao2jdbc-xform-entity2xml/api/entities/transformations/entity2xml", entity2XmlReq, ApiResponse.class);
			byte[] entity2xmlRespDecodedByte = Base64.getDecoder().decode(String.valueOf(entity2xmlResp.getBody().getData()));
			return new String(entity2xmlRespDecodedByte);
		}
		return null;
	}
}
