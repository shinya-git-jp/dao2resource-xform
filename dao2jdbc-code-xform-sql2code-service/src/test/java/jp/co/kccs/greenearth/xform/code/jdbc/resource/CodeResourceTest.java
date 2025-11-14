package jp.co.kccs.greenearth.xform.code.jdbc.resource;

import jakarta.ws.rs.core.Response;
import jp.co.kccs.greenearth.framework.data.GValidateException;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static jp.co.kccs.greenearth.xform.code.jdbc.Utils.getVirtualEntity;
import static org.junit.Assert.*;

public class CodeResourceTest {

	/**
	 * [1] 全てのリクエストフィルドがある場合、変換が成功すること。<br>
	 * [2] 設定が空の場合、{@link GValidateException}が発生すること。<br>
	 * [3] valueが空の場合、{@link GValidateException}が発生すること.
	 */
	@Test
	public void testConvertSql2Code() throws Exception {
		CodeResource codeResource = new CodeResource();
		{
			CodeRequest codeRequest = new CodeRequest();
			codeRequest.setSetting(getSetting());
			codeRequest.setValue(getValue());
			Response response = codeResource.convertSql2Code(codeRequest);
			assertNotNull(response.getEntity());
			assertEquals(
					"{\"success\":true,\"error\":null,\"data\":{\"description\":\"仮想表ID: aaaa-aaaa-aaaa-aaaa-aaaa\",\"sqlScript\":\"select\\r\\n    *\\r\\nfrom\\r\\n    testEntity a\\r\\n    join refEntity b on a.testCol = b.refCol\\r\\nwhere\\r\\n    testCol = ?;\",\"apiCodes\":{\"FIND_LIST-WHERE\":\"Map<String, Object> whereParams = new HashMap<>();\\r\\nwhereParams.put(\\\"testCol\\\",);\\r\\nList<GRecord> result = select(\\\"testEntity\\\", \\\"a\\\")\\r\\n.fields(colsAll())\\r\\n.innerJoin(\\\"refEntity\\\", \\\"b\\\", expMap($(\\\"a.testCol = b.refCol\\\"), Collections.emptyMap()))\\r\\n.where(expMap($(\\\"testCol = :testCol\\\"), whereParams))\\r\\n.findList();\",\"FIND_LIST-WHERE_UK\":\"Object[] whereParams = new Object[]{};\\r\\nList<GRecord> result = select(\\\"testEntity\\\", \\\"a\\\")\\r\\n.fields(colsAll())\\r\\n.innerJoin(\\\"refEntity\\\", \\\"b\\\", expMap($(\\\"a.testCol = b.refCol\\\"), Collections.emptyMap()))\\r\\n.whereUK(\\\"uk1\\\", whereParams)\\r\\n.findList();\",\"FIND_RECORD-WHERE\":\"Map<String, Object> whereParams = new HashMap<>();\\r\\nwhereParams.put(\\\"testCol\\\",);\\r\\nGRecord result = select(\\\"testEntity\\\", \\\"a\\\")\\r\\n.fields(colsAll())\\r\\n.innerJoin(\\\"refEntity\\\", \\\"b\\\", expMap($(\\\"a.testCol = b.refCol\\\"), Collections.emptyMap()))\\r\\n.where(expMap($(\\\"testCol = :testCol\\\"), whereParams))\\r\\n.findRecord();\",\"FIND_RECORD-WHERE_UK\":\"Object[] whereParams = new Object[]{};\\r\\nGRecord result = select(\\\"testEntity\\\", \\\"a\\\")\\r\\n.fields(colsAll())\\r\\n.innerJoin(\\\"refEntity\\\", \\\"b\\\", expMap($(\\\"a.testCol = b.refCol\\\"), Collections.emptyMap()))\\r\\n.whereUK(\\\"uk1\\\", whereParams)\\r\\n.findRecord();\",\"FIND_RECORD_SET-WHERE\":\"Map<String, Object> whereParams = new HashMap<>();\\r\\nwhereParams.put(\\\"testCol\\\",);\\r\\nGRecordSet result = select(\\\"testEntity\\\", \\\"a\\\")\\r\\n.fields(colsAll())\\r\\n.innerJoin(\\\"refEntity\\\", \\\"b\\\", expMap($(\\\"a.testCol = b.refCol\\\"), Collections.emptyMap()))\\r\\n.where(expMap($(\\\"testCol = :testCol\\\"), whereParams))\\r\\n.findRecordSet();\",\"FIND_RECORD_SET-WHERE_UK\":\"Object[] whereParams = new Object[]{};\\r\\nGRecordSet result = select(\\\"testEntity\\\", \\\"a\\\")\\r\\n.fields(colsAll())\\r\\n.innerJoin(\\\"refEntity\\\", \\\"b\\\", expMap($(\\\"a.testCol = b.refCol\\\"), Collections.emptyMap()))\\r\\n.whereUK(\\\"uk1\\\", whereParams)\\r\\n.findRecordSet();\"},\"type\":\"SELECT\",\"virtualEntity\":{\"displayName\":\"ve1\",\"veId\":\"aaaa-aaaa-aaaa-aaaa-aaaa\",\"categoryId\":\"bbbb-bbbb-bbbb-bbbb-bbbb\",\"categoryName\":\"categoryTest\",\"veType\":\"1\",\"refEntity\":{\"name\":\"testEntity\",\"phyName\":\"testPhy\",\"databaseName\":\"testDb\",\"primaryKey\":null,\"columns\":[{\"name\":\"testCol\",\"phyName\":\"testCol\",\"size\":10,\"dataType\":\"STRING\",\"decimalSize\":null,\"notNull\":true,\"primaryKey\":true},{\"name\":\"testCol2\",\"phyName\":\"testCol2\",\"size\":255,\"dataType\":\"STRING\",\"decimalSize\":null,\"notNull\":true,\"primaryKey\":false}],\"uniqueKeys\":[{\"name\":\"uk1\",\"uniqueKeyColumns\":[{\"name\":\"testCol\",\"phyName\":\"testCol\",\"size\":10,\"dataType\":\"STRING\",\"decimalSize\":null,\"notNull\":true,\"primaryKey\":true},{\"name\":\"testCol2\",\"phyName\":\"testCol2\",\"size\":255,\"dataType\":\"STRING\",\"decimalSize\":null,\"notNull\":true,\"primaryKey\":false}]}],\"foreignKeys\":[{\"name\":\"fk1\",\"referenceEntity\":{\"name\":\"refEntity\",\"phyName\":\"phyRefEntity\",\"databaseName\":\"testDb\",\"primaryKey\":null,\"columns\":[{\"name\":\"refCol\",\"phyName\":\"phyRefCol\",\"size\":10,\"dataType\":\"STRING\",\"decimalSize\":null,\"notNull\":true,\"primaryKey\":true}],\"uniqueKeys\":[],\"foreignKeys\":[]},\"foreignKeyColumns\":[{\"referenceColumn\":\"refCol\",\"sourceColumn\":\"testCol\",\"constValue\":\"test\"}],\"joinType\":\"INNER_JOIN\"}]},\"columns\":[{\"name\":\"testVColumn\",\"displayName\":\"testVColumn\",\"fixedValue\":null,\"aggregateFunction\":null,\"format\":null,\"refColumn\":\"testEntity.testCol\"},{\"name\":\"testVColumn2\",\"displayName\":\"testVColumn2\",\"fixedValue\":null,\"aggregateFunction\":null,\"format\":null,\"refColumn\":\"testEntity.testCol2\"}],\"absoluteVirtualColumnCode\":\"-\",\"foreignKeys\":[],\"filterConditions\":[],\"searchConditions\":[],\"groupConditions\":[],\"sortConditions\":[]}}}",
					response.getEntity()
			);
		}
		{
			CodeRequest codeRequest = new CodeRequest();
			codeRequest.setValue(getValue());
			try {
				codeResource.convertSql2Code(codeRequest);
				fail();
			} catch (Exception exception) {
				assertTrue(exception instanceof GValidateException);
				assertEquals("Setting is required.", exception.getMessage());
			}
		}
		{
			CodeRequest codeRequest = new CodeRequest();
			codeRequest.setSetting(getSetting());
			try {
				codeResource.convertSql2Code(codeRequest);
				fail();
			} catch (Exception exception) {
				assertTrue(exception instanceof GValidateException);
				assertEquals("Value is required.", exception.getMessage());
			}
		}
	}

	private static Map<String, Object> getValue() {
		Map<String, Object> value = new HashMap<>();
		value.put("sqlScript", "select * from testEntity a join refEntity b on a.testCol=b.refCol where testCol=?;");
		value.put("type", "SELECT");
		value.put("virtualEntity", getVirtualEntity());
		return value;
	}
	private static SettingRequest getSetting() {
		Map<String, Object> transform = new HashMap<>();
		transform.put("encoding", "UTF-16");
		Map<String, Object> codeTransform = new HashMap<>();
		codeTransform.put("useForeignKey", true);
		codeTransform.put("forceAliasColumn", false);
		codeTransform.put("entityQuery", false);
		codeTransform.put("useExpMap", true);
		SettingRequest settingRequest = new SettingRequest();
		settingRequest.setTransform(transform);
		settingRequest.setCodeTransform(codeTransform);

		return settingRequest;
	}
}
