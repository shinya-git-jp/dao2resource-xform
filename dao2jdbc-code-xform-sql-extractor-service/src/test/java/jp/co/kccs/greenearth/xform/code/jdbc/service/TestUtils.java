package jp.co.kccs.greenearth.xform.code.jdbc.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import jp.co.kccs.greenearth.commons.utils.GFileUtils;
import jp.co.kccs.greenearth.testutils.dbunit.TestHelper;
import jp.co.kccs.greenearth.xform.code.jdbc.service.resource.Dao2SqlResultResponse;
import jp.co.kccs.greenearth.xform.configuration.GDbCommonSetting;
import jp.co.kccs.greenearth.xform.configuration.GDbCommonSettingImpl;
import jp.co.kccs.greenearth.xform.dao.dto.GDao2VirtualEntity;
import jp.co.kccs.greenearth.xform.old.common.GDaoDbType;
import jp.co.kccs.greenearth.xform.old.configuration.GLocaleCommonSetting;
import jp.co.kccs.greenearth.xform.old.configuration.GReservedWordSetting;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class TestUtils {

	public static void cleanInsert(String filePath, GDbCommonSetting dbCommonSetting)  {
		try {
			TestHelper.cleanInsert(dbCommonSetting, filePath);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static GDbCommonSetting getSuccessSetting() {
		return (GDbCommonSetting) TestHelper.getMySqlSetting().get(GDbCommonSetting.class);
	}
	public static GLocaleCommonSetting getLocaleCommonSetting() {
		return (GLocaleCommonSetting) TestHelper.getMySqlSetting().get(GLocaleCommonSetting.class);
	}
	public static GReservedWordSetting getReservedWordSetting() {
		return (GReservedWordSetting) TestHelper.getMySqlSetting().get(GReservedWordSetting.class);
	}
	public static GDbCommonSetting getFailedSetting() {
		GDbCommonSetting commonSetting = new GDbCommonSettingImpl();
		commonSetting.setDbType(GDaoDbType.mysql);
		commonSetting.setUrl("jdbc:mysql://gef-unit-mysql8027.c3cxjzdf21mz.ap-northeast-1.rds.amazonaws.com:3306/gef_jdbc_tool?zeroDateTimeBehavior=convertToNull&useUnicode=true&useSSL=false&serverTimezone=GMT%2B9");
		commonSetting.setSchema("gef_jdbc_tool");
		commonSetting.setUsername("geframe");
		commonSetting.setPassword("Kccs0001");
		return commonSetting;
	}
	public static GDao2VirtualEntity getVirtualEntity() {
		GDao2VirtualEntity.ForeignKeyColumn foreignKeyColumn = new GDao2VirtualEntity.ForeignKeyColumn();
		foreignKeyColumn.setSourceColumn("testCol");
		foreignKeyColumn.setReferenceColumn("refCol");
		foreignKeyColumn.setConstValue("test");

		GDao2VirtualEntity.Column refCol = new GDao2VirtualEntity.Column();
		refCol.setName("refCol");
		refCol.setPhyName("phyRefCol");
		refCol.setDataType("STRING");
		refCol.setSize(10);
		refCol.setNotNull(true);
		refCol.setPrimaryKey(true);

		GDao2VirtualEntity.Entity referenceEntity = new GDao2VirtualEntity.Entity();
		referenceEntity.setName("refEntity");
		referenceEntity.setPhyName("phyRefEntity");
		referenceEntity.setColumns(List.of(refCol));
		referenceEntity.setDatabaseName("testDb");

		GDao2VirtualEntity.Column column = new GDao2VirtualEntity.Column();
		column.setName("testCol");
		column.setPhyName("testCol");
		column.setPrimaryKey(true);
		column.setSize(10);
		column.setNotNull(true);
		column.setDataType("STRING");

		GDao2VirtualEntity.Column column2 = new GDao2VirtualEntity.Column();
		column2.setName("testCol2");
		column2.setPhyName("testCol2");
		column2.setPrimaryKey(false);
		column2.setSize(255);
		column2.setNotNull(true);
		column2.setDataType("STRING");

		GDao2VirtualEntity.UniqueKey uniqueKey = new GDao2VirtualEntity.UniqueKey();
		uniqueKey.setName("uk1");
		uniqueKey.setUniqueKeyColumns(List.of(column, column2));

		GDao2VirtualEntity.ForeignKey foreignKey = new GDao2VirtualEntity.ForeignKey();
		foreignKey.setName("fk1");
		foreignKey.setJoinType("INNER_JOIN");
		foreignKey.setReferenceEntity(referenceEntity);
		foreignKey.setForeignKeyColumns(List.of(foreignKeyColumn));

		GDao2VirtualEntity.VirtualColumn virtualColumn1 = new GDao2VirtualEntity.VirtualColumn();
		virtualColumn1.setName("testVColumn");
		virtualColumn1.setRefColumn("testEntity.testCol");
		virtualColumn1.setDisplayName("testVColumn");

		GDao2VirtualEntity.VirtualColumn virtualColumn2 = new GDao2VirtualEntity.VirtualColumn();
		virtualColumn2.setName("testVColumn2");
		virtualColumn2.setRefColumn("testEntity.testCol2");
		virtualColumn2.setDisplayName("testVColumn2");

		GDao2VirtualEntity.Entity entity = new GDao2VirtualEntity.Entity();
		entity.setName("testEntity");
		entity.setDatabaseName("testDb");
		entity.setPhyName("testPhy");
		entity.setColumns(List.of(column, column2));
		entity.setUniqueKeys(List.of(uniqueKey));
		entity.setForeignKeys(List.of(foreignKey));

		GDao2VirtualEntity virtualEntity = new GDao2VirtualEntity();
		virtualEntity.setDisplayName("ve1");
		virtualEntity.setVeId("aaaa-aaaa-aaaa-aaaa-aaaa");
		virtualEntity.setCategoryId("bbbb-bbbb-bbbb-bbbb-bbbb");
		virtualEntity.setCategoryName("categoryTest");
		virtualEntity.setVeType("1");
		virtualEntity.setRefEntity(entity);
		virtualEntity.setColumns(List.of(virtualColumn1, virtualColumn2));
		return virtualEntity;
	}

	public static String readFile(String filePath) {
		URL expectedFile = GFileUtils.getResource(filePath);
		StringBuilder content = new StringBuilder();
		try (BufferedReader br = new BufferedReader(new FileReader(expectedFile.getFile()))) {
			String line;
			while ((line = br.readLine()) != null) {
				content.append(line).append(System.lineSeparator());
			}
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		int separatorLength = System.lineSeparator().length();
		if (content.length() >= separatorLength) {
			content.setLength(content.length() - separatorLength);
		}
		return content.toString();
	}
	public static boolean compare(String expected, List<Dao2SqlResultResponse> actual) {
		ObjectMapper objectMapper = new ObjectMapper();
		String actualString = null;
		try {
			actualString = objectMapper.writeValueAsString(actual);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
		try {
			objectMapper.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);
			List<Map<String, Object>> expectedResult = objectMapper.readValue(expected, List.class);
			List<Map<String, Object>> actualResult = objectMapper.readValue(actualString, List.class);
			return compareList(expectedResult, actualResult);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}
	public static boolean compareList(List<Map<String, Object>> list1, List<Map<String, Object>> list2) {
		if (list1.size() != list2.size()) return false;

		List<Map<String, Object>> copy2 = new ArrayList<>(list2);
		for (Map<String, Object> m1 : list1) {
			boolean matched = false;
			Iterator<Map<String, Object>> it = copy2.iterator();
			while (it.hasNext()) {
				if (m1.equals(it.next())) {
					it.remove();
					matched = true;
					break;
				}
			}
			if (!matched) return false;
		}
		return true;
	}
}
