package jp.co.kccs.greenearth.xform.entity.jdbc.resource;

import jp.co.kccs.greenearth.commons.GFrameworkUtils;
import jp.co.kccs.greenearth.commons.utils.GFileUtils;
import jp.co.kccs.greenearth.framework.data.GValidateException;
import jp.co.kccs.greenearth.xform.dao.common.GDao2Utils;
import jp.co.kccs.greenearth.xform.dao.dto.GDao2VirtualEntity;
import jp.co.kccs.greenearth.xform.entity.jdbc.TestUtils;
import jp.co.kccs.greenearth.xform.entity.jdbc.function.EntityExtractorFunction;
import jp.co.kccs.greenearth.xform.entity.jdbc.function.EntityIdExistenceFunction;
import jp.co.kccs.greenearth.xform.entity.jdbc.function.FetchEntityIdByDatabaseIdFunction;
import jp.co.kccs.greenearth.xform.entity.jdbc.utility.Utils;
import org.junit.Test;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static jp.co.kccs.greenearth.xform.entity.jdbc.TestUtils.cleanInsert;
import static org.junit.Assert.*;

public class EntityControllerTest {
	private static EntityController entityController = new EntityController(
			new EntityExtractorFunction(),
			new FetchEntityIdByDatabaseIdFunction(),
			new EntityIdExistenceFunction()
	);

	@Test
	public void testExtractEntity() throws Exception {
		GFrameworkUtils.initDIContainer(null);
		cleanInsert("jp/co/kccs/greenearth/xform/entity/jdbc/import_entity.xml", TestUtils.getSuccessSetting());

		{
			EntityRequest entityRequest = new EntityRequest();
			entityRequest.setSetting(getSetting());
			entityRequest.setValue(getValue());
			ApiResponse<GDao2VirtualEntity.Entity> response = entityController.extractEntity(entityRequest);
			String entityString = Utils.convertObjectToString(response.getData());
			assertNotNull(response.getData());
			assertTrue(response.isSuccess());
			assertEquals("{\"name\":\"SLocalization\",\"phyName\":null,\"databaseName\":\"gef_jdbc_tool\",\"primaryKey\":{\"name\":\"objectID\",\"phyName\":null,\"size\":32,\"dataType\":\"STRING\",\"decimalSize\":\"-\",\"notNull\":true,\"primaryKey\":true},\"columns\":[{\"name\":\"objectID\",\"phyName\":null,\"size\":32,\"dataType\":\"STRING\",\"decimalSize\":\"-\",\"notNull\":true,\"primaryKey\":true},{\"name\":\"country1\",\"phyName\":null,\"size\":255,\"dataType\":\"STRING\",\"decimalSize\":\"-\",\"notNull\":false,\"primaryKey\":false},{\"name\":\"country2\",\"phyName\":null,\"size\":255,\"dataType\":\"STRING\",\"decimalSize\":\"-\",\"notNull\":false,\"primaryKey\":false},{\"name\":\"country3\",\"phyName\":null,\"size\":255,\"dataType\":\"STRING\",\"decimalSize\":\"-\",\"notNull\":false,\"primaryKey\":false},{\"name\":\"country4\",\"phyName\":null,\"size\":255,\"dataType\":\"STRING\",\"decimalSize\":\"-\",\"notNull\":false,\"primaryKey\":false},{\"name\":\"country5\",\"phyName\":null,\"size\":255,\"dataType\":\"STRING\",\"decimalSize\":\"-\",\"notNull\":false,\"primaryKey\":false}],\"uniqueKeys\":[],\"foreignKeys\":[]}", entityString);
		}
		{
			EntityRequest entityRequest = new EntityRequest();
			entityRequest.setValue(getValue());
			try {
				entityController.extractEntity(entityRequest);
				fail();
			} catch (Exception exception) {
				assertTrue(exception instanceof GValidateException);
				assertEquals("Setting is required.", exception.getMessage());
			}
		}
		{
			EntityRequest entityRequest = new EntityRequest();
			entityRequest.setSetting(getSetting());
			try {
				entityController.extractEntity(entityRequest);
				fail();
			} catch (Exception exception) {
				assertTrue(exception instanceof GValidateException);
				assertEquals("Value is required.", exception.getMessage());
			}
		}
	}

	@Test
	public void testFetchEntityIdByDatabaseId() throws Exception {
		GFrameworkUtils.initDIContainer(null);
		cleanInsert("jp/co/kccs/greenearth/xform/entity/jdbc/import_entity.xml", TestUtils.getSuccessSetting());
		{
			EntityRequest entityRequest = new EntityRequest();
			entityRequest.setSetting(getSetting());
			entityRequest.setValue(Map.of("databaseId", "377BB20685E5497FB5B5209DACC76432"));
			ApiResponse<List<String>> response = entityController.fetchEntityIdByDatabaseId(entityRequest);
			assertNotNull(response.getData());
			assertEquals(5, response.getData().size());
		}
		{
			EntityRequest entityRequest = new EntityRequest();
			entityRequest.setValue(Map.of("databaseId", "377BB20685E5497FB5B5209DACC76432"));
			try {
				entityController.fetchEntityIdByDatabaseId(entityRequest);
				fail();
			} catch (Exception exception) {
				assertTrue(exception instanceof GValidateException);
				assertEquals("Setting is required.", exception.getMessage());
			}
		}
		{
			EntityRequest entityRequest = new EntityRequest();
			entityRequest.setSetting(getSetting());
			try {
				entityController.fetchEntityIdByDatabaseId(entityRequest);
				fail();
			} catch (Exception exception) {
				assertTrue(exception instanceof GValidateException);
				assertEquals("Value is required.", exception.getMessage());
			}
		}
	}

	@Test
	public void testCheckIfIdIsExist() throws Exception {
		GFrameworkUtils.initDIContainer(null);
		cleanInsert("jp/co/kccs/greenearth/xform/entity/jdbc/import_entity.xml", TestUtils.getSuccessSetting());
		{
			EntityRequest entityRequest = new EntityRequest();
			entityRequest.setSetting(getSetting());
			entityRequest.setValue(Map.of("id", "377BB20685E5497FB5B5209DACC76432", "type", "databaseId"));
			ApiResponse<String> response = entityController.checkIfIdIsExist(entityRequest);
			assertNotNull(response.getData());
			assertEquals("ID is exist", response.getData());
		}
		{
			EntityRequest entityRequest = new EntityRequest();
			entityRequest.setValue(Map.of("id", "3083F38BA88F4279957254F992E7D5A5", "type", "databaseId"));
			try {
				entityController.checkIfIdIsExist(entityRequest);
				fail();
			} catch (Exception exception) {
				assertTrue(exception instanceof GValidateException);
				assertEquals("Setting is required.", exception.getMessage());
			}
		}
		{
			EntityRequest codeRequest = new EntityRequest();
			codeRequest.setSetting(getSetting());
			try {
				entityController.fetchEntityIdByDatabaseId(codeRequest);
				fail();
			} catch (Exception exception) {
				assertTrue(exception instanceof GValidateException);
				assertEquals("Value is required.", exception.getMessage());
			}
		}
	}

	private static Map<String, Object> getValue() {
		Map<String, Object> value = new HashMap<>();
		value.put("entityId", "2726BD6AA6254A1590612E092A91ACE4");
		return value;
	}
	private static SettingRequest getSetting() {
		Map<String, Object> transform = new HashMap<>();
		transform.put("encoding", "UTF-8");
		transform.put("useForeignKey", true);
		transform.put("forceAliasColumn", false);
		transform.put("entityQuery", false);
		transform.put("useExpMap", true);

		Map<String, Object> db = new HashMap<>();
		db.put("url", "jdbc:mysql://gef-unit-mysql8027.c3cxjzdf21mz.ap-northeast-1.rds.amazonaws.com:3306/gef_jdbc_tool?zeroDateTimeBehavior=convertToNull&useUnicode=true&useSSL=false&serverTimezone=GMT%2B9");
		db.put("schema", "gef_jdbc_tool");
		db.put("username", "geframe");
		db.put("password", "Kccs0000");
		db.put("dbType", "mysql");

		Map<String, Object> reservedWords = new HashMap<>();
		reservedWords.put("primaryKey", "objectID");
		reservedWords.put("exclusiveKey", "ExclusiveFG");
		reservedWords.put("companyCodeKey", "CompanyCD");
		reservedWords.put("insertedUserIDKey", "RegisteredPerson");
		reservedWords.put("insertedDateKey", "RegisteredDT");
		reservedWords.put("updatedUserIDKey", "UpdatedPerson");
		reservedWords.put("updatedDateKey", "UpdatedDT");

		Map<String, Object> locale = new HashMap<>();
		locale.put("current", "jp");
		locale.put("mapping", Map.of(
				"country1", "jp",
				"country2", "en",
				"country3", "cn"
		));

		SettingRequest settingRequest = new SettingRequest();
		settingRequest.setTransform(transform);
		settingRequest.setDb(db);
		settingRequest.setLocale(locale);
		settingRequest.setReservedWords(reservedWords);
		return settingRequest;
	}
}
