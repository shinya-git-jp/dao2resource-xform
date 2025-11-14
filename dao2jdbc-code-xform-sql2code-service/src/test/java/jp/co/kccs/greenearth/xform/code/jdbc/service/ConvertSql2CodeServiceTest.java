package jp.co.kccs.greenearth.xform.code.jdbc.service;

import jp.co.kccs.greenearth.xform.code.dao.core.GDao2JdbcXFormCodeResult;
import jp.co.kccs.greenearth.xform.code.jdbc.configuration.GCodeTransformSetting;
import jp.co.kccs.greenearth.xform.code.jdbc.resource.CodeRequest;
import jp.co.kccs.greenearth.xform.code.jdbc.resource.SettingRequest;
import jp.co.kccs.greenearth.xform.configuration.GDbCommonSetting;
import jp.co.kccs.greenearth.xform.configuration.GTransformSetting;
import jp.co.kccs.greenearth.xform.configuration.GXFormSetting;
import jp.co.kccs.greenearth.xform.configuration.GXFormSettingHolder;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static jp.co.kccs.greenearth.xform.code.jdbc.Utils.getVirtualEntity;
import static jp.co.kccs.greenearth.xform.code.jdbc.utility.GUtils.convertObjectToString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class ConvertSql2CodeServiceTest {

	/**
	 * [1] 引数がnullの場合、変換された結果がnullになること。<br>
	 * [2] 引数があるの場合、変換された結果が正しいであること。<br>
	 */
	@Test
	public void testHandle() {
		ConvertSql2CodeService function = new ConvertSql2CodeService();
		{
			GDao2JdbcXFormCodeResult result = function.handle(null);
			assertNull(result);
		}
		{
			CodeRequest codeRequest = new CodeRequest();
			codeRequest.setSetting(getSetting());
			codeRequest.setValue(getValue());
			Map<Class<?>, GXFormSetting> setting = getSetting().toDaoCommonSetting();
			GXFormSettingHolder.setCommonSetting(GDbCommonSetting.class, setting.get(GDbCommonSetting.class));
			GXFormSettingHolder.setCommonSetting(GTransformSetting.class, setting.get(GTransformSetting.class));
			GXFormSettingHolder.setCommonSetting(GCodeTransformSetting.class, setting.get(GCodeTransformSetting.class));
			GDao2JdbcXFormCodeResult result = function.handle(codeRequest.getValue());
			String resultString = convertObjectToString(result);
			assertEquals("{\"description\":\"仮想表ID: aaaa-aaaa-aaaa-aaaa-aaaa\",\"sqlScript\":\"select\\r\\n    *\\r\\nfrom\\r\\n    test\\r\\nwhere\\r\\n    col1 = ?\",\"apiCodes\":{\"FIND_LIST-WHERE\":\"Map<String, Object> whereParams = new HashMap<>();\\r\\nwhereParams.put(\\\"col1\\\",);\\r\\nList<GRecord> result = select(\\\"test\\\", \\\"test\\\")\\r\\n.fields(colsAll())\\r\\n.where(expMap($(\\\"col1 = :col1\\\"), whereParams))\\r\\n.findList();\",\"FIND_RECORD-WHERE\":\"Map<String, Object> whereParams = new HashMap<>();\\r\\nwhereParams.put(\\\"col1\\\",);\\r\\nGRecord result = select(\\\"test\\\", \\\"test\\\")\\r\\n.fields(colsAll())\\r\\n.where(expMap($(\\\"col1 = :col1\\\"), whereParams))\\r\\n.findRecord();\",\"FIND_RECORD_SET-WHERE\":\"Map<String, Object> whereParams = new HashMap<>();\\r\\nwhereParams.put(\\\"col1\\\",);\\r\\nGRecordSet result = select(\\\"test\\\", \\\"test\\\")\\r\\n.fields(colsAll())\\r\\n.where(expMap($(\\\"col1 = :col1\\\"), whereParams))\\r\\n.findRecordSet();\"},\"type\":\"SELECT\",\"virtualEntity\":{\"displayName\":\"ve1\",\"veId\":\"aaaa-aaaa-aaaa-aaaa-aaaa\",\"categoryId\":\"bbbb-bbbb-bbbb-bbbb-bbbb\",\"categoryName\":\"categoryTest\",\"veType\":\"1\",\"refEntity\":{\"name\":\"testEntity\",\"phyName\":\"testPhy\",\"databaseName\":\"testDb\",\"primaryKey\":null,\"columns\":[{\"name\":\"testCol\",\"phyName\":\"testCol\",\"size\":10,\"dataType\":\"STRING\",\"decimalSize\":null,\"notNull\":true,\"primaryKey\":true},{\"name\":\"testCol2\",\"phyName\":\"testCol2\",\"size\":255,\"dataType\":\"STRING\",\"decimalSize\":null,\"notNull\":true,\"primaryKey\":false}],\"uniqueKeys\":[{\"name\":\"uk1\",\"uniqueKeyColumns\":[{\"name\":\"testCol\",\"phyName\":\"testCol\",\"size\":10,\"dataType\":\"STRING\",\"decimalSize\":null,\"notNull\":true,\"primaryKey\":true},{\"name\":\"testCol2\",\"phyName\":\"testCol2\",\"size\":255,\"dataType\":\"STRING\",\"decimalSize\":null,\"notNull\":true,\"primaryKey\":false}]}],\"foreignKeys\":[{\"name\":\"fk1\",\"referenceEntity\":{\"name\":\"refEntity\",\"phyName\":\"phyRefEntity\",\"databaseName\":\"testDb\",\"primaryKey\":null,\"columns\":[{\"name\":\"refCol\",\"phyName\":\"phyRefCol\",\"size\":10,\"dataType\":\"STRING\",\"decimalSize\":null,\"notNull\":true,\"primaryKey\":true}],\"uniqueKeys\":[],\"foreignKeys\":[]},\"foreignKeyColumns\":[{\"referenceColumn\":\"refCol\",\"sourceColumn\":\"testCol\",\"constValue\":\"test\"}],\"joinType\":\"INNER_JOIN\"}]},\"columns\":[{\"name\":\"testVColumn\",\"displayName\":\"testVColumn\",\"fixedValue\":null,\"aggregateFunction\":null,\"format\":null,\"refColumn\":\"testEntity.testCol\"},{\"name\":\"testVColumn2\",\"displayName\":\"testVColumn2\",\"fixedValue\":null,\"aggregateFunction\":null,\"format\":null,\"refColumn\":\"testEntity.testCol2\"}],\"absoluteVirtualColumnCode\":\"-\",\"foreignKeys\":[],\"filterConditions\":[],\"searchConditions\":[],\"groupConditions\":[],\"sortConditions\":[]}}", resultString);
		}
	}
	private static Map<String, Object> getValue() {
		Map<String, Object> value = new HashMap<>();
		value.put("sqlScript", "select * from test where col1=?");
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
