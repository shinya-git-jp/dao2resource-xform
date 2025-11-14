//package jp.co.kccs.greenearth.xform.entity.dao.core.service;
//
//import jp.co.kccs.greenearth.commons.GFrameworkUtils;
//import jp.co.kccs.greenearth.commons.utils.GFileUtils;
//import jp.co.kccs.greenearth.testutils.dbunit.TestHelper;
//import jp.co.kccs.greenearth.xform.dao.common.GDao2Utils;
//import jp.co.kccs.greenearth.xform.dao.common.GDaoCommonSetting;
//import jp.co.kccs.greenearth.xform.dao.common.GDaoCommonSettingParserFilePath;
//import jp.co.kccs.greenearth.xform.entity.jdbc.core.service.GEntityFileDescriptor;
//import org.junit.Before;
//import org.junit.Test;
//
//import java.io.UnsupportedEncodingException;
//
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertNotNull;
//
///**
// * @author KCCS XXXXX
// * @create GEF_NEXT_DATE
// * @since GEF_NEXT_VERSION
// */
//public abstract class GVeEntityFileTransformServiceTest {
//
//	protected String dbScheme;
//
//	@Before
//	public void setup() throws Exception {
//		String settingsFilePath = GFileUtils.getResource(getSettingFilePath()).getPath();
//		GDaoCommonSettingParserFilePath commonSettingParser = new GDaoCommonSettingParserFilePath();
//		GDaoCommonSetting commonSetting = commonSettingParser.parse(settingsFilePath);
//		dbScheme = commonSetting.getDbSetting().getSchema();
//		GDao2Utils.setCommonSetting(commonSetting);
//		TestHelper.cleanInsert(commonSetting.getDbSetting(), "jp/co/kccs/greenearth/xform/entity/dao/core/service/GVeEntityFileTransformServiceTest/import_entity.xml");
//	}
//
//	protected abstract String getSettingFilePath();
//
//	@Test
//	public void testTransformByEntityId() throws UnsupportedEncodingException {
//
//		GVeEntityFileTransformService veEntityFileTransformService =
//				GFrameworkUtils.getComponent(GVeEntityFileTransformService.class.getName());
//
//		GEntityFileDescriptor entityFileDescriptor = veEntityFileTransformService.transformByEntityId("CB30B5B47816426A8A14AFB44D6CA251");
//
//		assertNotNull(entityFileDescriptor);
//		assertEquals("jdcb_tool_test_allColumntype", entityFileDescriptor.getName());
//		assertEquals(
//				"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\r\n" +
//						"<Entity database=\"" + dbScheme + "\" phyName=\"jdcb_tool_test_allColumntype\">\r\n" +
//						"  <Columns>\r\n" +
//						"    <Column name=\"objectID\" phyName=\"objectID\" type=\"Varchar\" size=\"32\"/>\r\n" +
//						"    <Column name=\"StringColumn\" phyName=\"StringColumn\" type=\"Varchar\" size=\"32\"/>\r\n" +
//						"    <Column name=\"IntColumn\" phyName=\"IntColumn\" type=\"Integer\" size=\"8\"/>\r\n" +
//						"    <Column name=\"NStringColumn\" phyName=\"NStringColumn\" type=\"Varchar\" size=\"32\"/>\r\n" +
//						"    <Column name=\"DateTimeColumn\" phyName=\"DateTimeColumn\" type=\"Date\"/>\r\n" +
//						"    <Column name=\"YMColumn\" phyName=\"YMColumn\" type=\"Varchar\" size=\"6\"/>\r\n" +
//						"    <Column name=\"CurrencyColumn\" phyName=\"CurrencyColumn\" type=\"Double\" size=\"32\" scale=\"2\"/>\r\n" +
//						"    <Column name=\"LongColumn\" phyName=\"LongColumn\" type=\"Long\" size=\"32\"/>\r\n" +
//						"    <Column name=\"CompanyCD\" phyName=\"CompanyCD\" type=\"Varchar\" size=\"32\"/>\r\n" +
//						"    <Column name=\"ExclusiveFG\" phyName=\"ExclusiveFG\" type=\"Varchar\" size=\"32\" exclusiveKey=\"true\"/>\r\n" +
//						"    <Column name=\"RegisteredPerson\" phyName=\"RegisteredPerson\" type=\"Varchar\" size=\"32\"/>\r\n" +
//						"    <Column name=\"RegisteredDT\" phyName=\"RegisteredDT\" type=\"Date\" generate=\"RegisteredDate\"/>\r\n" +
//						"    <Column name=\"UpdatedPerson\" phyName=\"UpdatedPerson\" type=\"Varchar\" size=\"32\"/>\r\n" +
//						"    <Column name=\"UpdatedDT\" phyName=\"UpdatedDT\" type=\"Date\" generate=\"UpdatedDate\"/>\r\n" +
//						"  </Columns>\r\n" +
//						"  <PrimaryKey>\r\n" +
//						"    <ReferenceColumn refName=\"objectID\"/>\r\n" +
//						"  </PrimaryKey>\r\n" +
//						"  <UniqueKeys>\r\n" +
//						"    <UniqueKey name=\"MulitipleColumnUKName\">\r\n" +
//						"      <ReferenceColumn refName=\"objectID\"/>\r\n" +
//						"      <ReferenceColumn refName=\"CompanyCD\"/>\r\n" +
//						"    </UniqueKey>\r\n" +
//						"    <UniqueKey name=\"OtherUKName\">\r\n" +
//						"      <ReferenceColumn refName=\"objectID\"/>\r\n" +
//						"      <ReferenceColumn refName=\"StringColumn\"/>\r\n" +
//						"      <ReferenceColumn refName=\"IntColumn\"/>\r\n" +
//						"      <ReferenceColumn refName=\"NStringColumn\"/>\r\n" +
//						"      <ReferenceColumn refName=\"DateTimeColumn\"/>\r\n" +
//						"      <ReferenceColumn refName=\"YMColumn\"/>\r\n" +
//						"      <ReferenceColumn refName=\"CurrencyColumn\"/>\r\n" +
//						"      <ReferenceColumn refName=\"LongColumn\"/>\r\n" +
//						"      <ReferenceColumn refName=\"CompanyCD\"/>\r\n" +
//						"      <ReferenceColumn refName=\"ExclusiveFG\"/>\r\n" +
//						"      <ReferenceColumn refName=\"RegisteredPerson\"/>\r\n" +
//						"      <ReferenceColumn refName=\"RegisteredDT\"/>\r\n" +
//						"      <ReferenceColumn refName=\"UpdatedPerson\"/>\r\n" +
//						"      <ReferenceColumn refName=\"UpdatedDT\"/>\r\n" +
//						"    </UniqueKey>\r\n" +
//						"    <UniqueKey name=\"SingleColumnUK\">\r\n" +
//						"      <ReferenceColumn refName=\"StringColumn\"/>\r\n" +
//						"    </UniqueKey>\r\n" +
//						"  </UniqueKeys>\r\n" +
//						"  <ForeignKeys>\r\n" +
//						"    <ForeignKey name=\"onlyColumnFKName\" refEntity=\"jdbc_tool_test_pk_uk_mainmaster\">\r\n" +
//						"      <JoinColumn refName=\"objectID\" srcName=\"objectID\"/>\r\n" +
//						"      <JoinColumn refName=\"ItemCD\" srcName=\"StringColumn\"/>\r\n" +
//						"    </ForeignKey>\r\n" +
//						"    <ForeignKey name=\"onlyConstValueFKName\" refEntity=\"jdbc_tool_test_pk_uk_mainmaster\">\r\n" +
//						"      <JoinColumn refName=\"objectID\" constValue=\"1001\"/>\r\n" +
//						"      <JoinColumn refName=\"ItemCD\" constValue=\"9001\"/>\r\n" +
//						"    </ForeignKey>\r\n" +
//						"    <ForeignKey name=\"columnAndConstValueMixFKName\" refEntity=\"jdbc_tool_test_pk_uk_mainmaster\">\r\n" +
//						"      <JoinColumn refName=\"objectID\" srcName=\"objectID\"/>\r\n" +
//						"      <JoinColumn refName=\"Flag\" constValue=\"1001\"/>\r\n" +
//						"    </ForeignKey>\r\n" +
//						"  </ForeignKeys>\r\n" +
//					"</Entity>\r\n", new String(entityFileDescriptor.getResult(), "UTF-8"));
//	}
//}
