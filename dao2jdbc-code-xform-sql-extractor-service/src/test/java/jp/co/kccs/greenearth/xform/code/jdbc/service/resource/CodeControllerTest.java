//package jp.co.kccs.greenearth.xform.code.jdbc.service.resource;
//
//import jp.co.kccs.greenearth.commons.utils.GFileUtils;
//import jp.co.kccs.greenearth.framework.data.GValidateException;
//import jp.co.kccs.greenearth.xform.code.dao.core.GCrudType;
//import jp.co.kccs.greenearth.xform.code.jdbc.service.function.CheckConnectionFunction;
//import jp.co.kccs.greenearth.xform.code.jdbc.service.function.FetchVeIdByCategoryIdFunction;
//import jp.co.kccs.greenearth.xform.code.jdbc.service.function.SqlExtractorFunction;
//import jp.co.kccs.greenearth.xform.code.jdbc.service.function.VeIdExistenceFunction;
//import jp.co.kccs.greenearth.xform.dao.common.GDao2Utils;
//import jp.co.kccs.greenearth.xform.dao.common.GDaoCommonSetting;
//import jp.co.kccs.greenearth.xform.dao.common.GDaoCommonSettingParserFilePath;
//import jp.co.kccs.greenearth.xform.jdbc.configuration.GCommonSettingParser;
//import jp.co.kccs.greenearth.xform.jdbc.configuration.GConfigurationUtils;
//import org.junit.Test;
//
//import java.net.URL;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import static jp.co.kccs.greenearth.xform.code.jdbc.service.Utils.cleanInsert;
//import static org.junit.Assert.*;
//import static org.junit.Assert.assertEquals;
//
//public class CodeControllerTest {
//	private static CodeController codeController = new CodeController(
//			new SqlExtractorFunction(),
//			new FetchVeIdByCategoryIdFunction(),
//			new VeIdExistenceFunction(),
//			new CheckConnectionFunction()
//	);
//
//	@Test
//	public void testExtractSql() throws Exception {
//		GCommonSettingParser<String> commonSettingParser = new GDaoCommonSettingParserFilePath();
//		URL url = GFileUtils.getResource("setting-success.yml");
//		GDaoCommonSetting daoCommonSetting = (GDaoCommonSetting) commonSettingParser.parse(url.getPath());
//		GDaoCommonSetting commonSetting = daoCommonSetting;
//		GDao2Utils.setCommonSetting(daoCommonSetting);
//		cleanInsert("jp/co/kccs/greenearth/xform/code/jdbc/service/function/import_extract_cud.xml", commonSetting);
//
//		{
//
//			CodeRequest codeRequest = new CodeRequest();
//			codeRequest.setSetting(getSetting());
//			codeRequest.setValue(getValue());
//			ApiResponse<List<Dao2SqlResultResponse>> response = codeController.extractSql(codeRequest);
//			assertNotNull(response.getData());
//			assertEquals(1, response.getData().size());
//			assertEquals(GCrudType.SELECT, response.getData().get(0).getType());
//			assertEquals("SELECT" + System.lineSeparator() +
//					"    jdbc_tool_test_pk_uk_mainmaster.objectID," + System.lineSeparator() +
//					"    jdbc_tool_test_pk_uk_mainmaster.ItemCD," + System.lineSeparator() +
//					"    jdbc_tool_test_pk_uk_mainmaster.ItemNA," + System.lineSeparator() +
//					"    jdbc_tool_test_pk_uk_mainmaster.UnitPrice," + System.lineSeparator() +
//					"    jdbc_tool_test_pk_uk_mainmaster.SellPrice," + System.lineSeparator() +
//					"    jdbc_tool_test_pk_uk_mainmaster.Flag," + System.lineSeparator() +
//					"    jdbc_tool_test_pk_uk_mainmaster.PriceFlag," + System.lineSeparator() +
//					"    jdbc_tool_test_pk_uk_mainmaster.ExclusiveFG," + System.lineSeparator() +
//					"    jdbc_tool_test_pk_uk_mainmaster.RegisteredDT," + System.lineSeparator() +
//					"    jdbc_tool_test_pk_uk_mainmaster.UpdatedDT" + System.lineSeparator() +
//					"FROM" + System.lineSeparator() +
//					"    gef_jdbc_tool.jdbc_tool_test_pk_uk_mainmaster jdbc_tool_test_pk_uk_mainmaster" + System.lineSeparator() +
//					"WHERE" + System.lineSeparator() +
//					"    jdbc_tool_test_pk_uk_mainmaster.objectID = ?", response.getData().get(0).getSqlScript());
//				assertNotNull(response.getData().get(0).getVirtualEntity());
//		}
//		{
//			CodeRequest codeRequest = new CodeRequest();
//			codeRequest.setValue(getValue());
//			try {
//				codeController.extractSql(codeRequest);
//				fail();
//			} catch (Exception exception) {
//				assertTrue(exception instanceof GValidateException);
//				assertEquals("Setting is required.", exception.getMessage());
//			}
//		}
//		{
//			CodeRequest codeRequest = new CodeRequest();
//			codeRequest.setSetting(getSetting());
//			try {
//				codeController.extractSql(codeRequest);
//				fail();
//			} catch (Exception exception) {
//				assertTrue(exception instanceof GValidateException);
//				assertEquals("Value is required.", exception.getMessage());
//			}
//		}
//	}
//
//	@Test
//	public void testFetchVeIdByCategoryId() throws Exception {
//		GCommonSettingParser<String> commonSettingParser = new GDaoCommonSettingParserFilePath();
//		URL url = GFileUtils.getResource("setting-success.yml");
//		GDaoCommonSetting daoCommonSetting = (GDaoCommonSetting) commonSettingParser.parse(url.getPath());
//		GDaoCommonSetting commonSetting = daoCommonSetting;
//		GDao2Utils.setCommonSetting(daoCommonSetting);
//		cleanInsert("jp/co/kccs/greenearth/xform/code/jdbc/service/function/import.xml", commonSetting);
//		{
//			CodeRequest codeRequest = new CodeRequest();
//			codeRequest.setSetting(getSetting());
//			codeRequest.setValue(Map.of("categoryId", "B27393A2779C472C88EEF38FABCFE118"));
//			ApiResponse<List<String>> response = codeController.fetchVeIdByCategoryId(codeRequest);
//			assertNotNull(response.getData());
//			assertEquals(1, response.getData().size());
//		}
//		{
//			CodeRequest codeRequest = new CodeRequest();
//			codeRequest.setValue(Map.of("categoryId", "3083F38BA88F4279957254F992E7D5A5"));
//			try {
//				codeController.fetchVeIdByCategoryId(codeRequest);
//				fail();
//			} catch (Exception exception) {
//				assertTrue(exception instanceof GValidateException);
//				assertEquals("Setting is required.", exception.getMessage());
//			}
//		}
//		{
//			CodeRequest codeRequest = new CodeRequest();
//			codeRequest.setSetting(getSetting());
//			try {
//				codeController.fetchVeIdByCategoryId(codeRequest);
//				fail();
//			} catch (Exception exception) {
//				assertTrue(exception instanceof GValidateException);
//				assertEquals("Value is required.", exception.getMessage());
//			}
//		}
//	}
//
//	@Test
//	public void testCheckIfIdIsExist() throws Exception {
//		GCommonSettingParser<String> commonSettingParser = new GDaoCommonSettingParserFilePath();
//		URL url = GFileUtils.getResource("setting-success.yml");
//		GDaoCommonSetting daoCommonSetting = (GDaoCommonSetting) commonSettingParser.parse(url.getPath());
//		GDaoCommonSetting commonSetting = daoCommonSetting;
//		GDao2Utils.setCommonSetting(daoCommonSetting);
//		cleanInsert("jp/co/kccs/greenearth/xform/code/jdbc/service/function/import.xml", commonSetting);
//		{
//			CodeRequest codeRequest = new CodeRequest();
//			codeRequest.setSetting(getSetting());
//			codeRequest.setValue(Map.of("id", "B27393A2779C472C88EEF38FABCFE118", "type", "categoryId"));
//			ApiResponse<String> response = codeController.checkIfIdIsExist(codeRequest);
//			assertNotNull(response.getData());
//			assertEquals("ID is exist.", response.getData());
//		}
//		{
//			CodeRequest codeRequest = new CodeRequest();
//			codeRequest.setValue(Map.of("id", "3083F38BA88F4279957254F992E7D5A5", "type", "categoryId"));
//			try {
//				codeController.checkIfIdIsExist(codeRequest);
//				fail();
//			} catch (Exception exception) {
//				assertTrue(exception instanceof GValidateException);
//				assertEquals("Setting is required.", exception.getMessage());
//			}
//		}
//		{
//			CodeRequest codeRequest = new CodeRequest();
//			codeRequest.setSetting(getSetting());
//			try {
//				codeController.checkIfIdIsExist(codeRequest);
//				fail();
//			} catch (Exception exception) {
//				assertTrue(exception instanceof GValidateException);
//				assertEquals("Value is required.", exception.getMessage());
//			}
//		}
//	}
//
//	@Test
//	public void testCheckConnections() throws Exception {
//		{
//			CodeRequest codeRequest = new CodeRequest();
//			codeRequest.setSetting(getSetting());
//			ApiResponse<String> response = codeController.checkConnections(codeRequest);
//			assertNotNull(response.getData());
//			assertEquals("Success!", response.getData());
//		}
//		{
//			CodeRequest codeRequest = new CodeRequest();
//			try {
//				codeController.checkConnections(codeRequest);
//				fail();
//			} catch (Exception exception) {
//				assertTrue(exception instanceof GValidateException);
//				assertEquals("Setting is required.", exception.getMessage());
//			}
//		}
//	}
//
//	private static Map<String, Object> getValue() {
//		Map<String, Object> value = new HashMap<>();
//		value.put("veId", "1FEFE156727646DC9122045339BD3999");
//		value.put("crudTypes", List.of("SELECT"));
//		return value;
//	}
//	private static SettingRequest getSetting() throws Exception {
//		Map<String, Object> transform = new HashMap<>();
//		transform.put("encoding", "UTF-8");
//		transform.put("useForeignKey", true);
//		transform.put("forceAliasColumn", false);
//		transform.put("entityQuery", false);
//		transform.put("useExpMap", true);
//
//		Map<String, Object> db = new HashMap<>();
//		String encryptedPassword = GConfigurationUtils.encryptPassword("Kccs0000");
//		db.put("url", "jdbc:mysql://gef-unit-mysql8027.c3cxjzdf21mz.ap-northeast-1.rds.amazonaws.com:3306/gef_jdbc_tool?zeroDateTimeBehavior=convertToNull&useUnicode=true&useSSL=false&serverTimezone=GMT%2B9");
//		db.put("schema", "gef_jdbc_tool");
//		db.put("username", "geframe");
//		db.put("password", encryptedPassword);
//		db.put("dbType", "mysql");
//
//		Map<String, Object> reservedWords = new HashMap<>();
//		reservedWords.put("primaryKey", "primaryKey");
//		reservedWords.put("exclusiveKey", "exclusiveKey");
//		reservedWords.put("companyCodeKey", "companyCodeKey");
//		reservedWords.put("insertedUserIDKey", "RegisteredPerson");
//		reservedWords.put("insertedDateKey", "RegisteredDT");
//		reservedWords.put("updatedUserIDKey", "UpdatedPerson");
//		reservedWords.put("updatedDateKey", "UpdatedDT");
//
//		Map<String, Object> locale = new HashMap<>();
//		locale.put("current", "jp");
//		locale.put("mapping", Map.of(
//				"country1", "jp",
//				"country2", "en",
//				"country3", "cn"
//		));
//
//		SettingRequest settingRequest = new SettingRequest();
//		settingRequest.setTransform(transform);
//		settingRequest.setDb(db);
//		settingRequest.setLocale(locale);
//		settingRequest.setReservedWords(reservedWords);
//		return settingRequest;
//	}
//}
