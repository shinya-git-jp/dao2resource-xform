package jp.co.kccs.greenearth.xform.dao.common;

import jp.co.kccs.greenearth.commons.GFrameworkProperties;
import jp.co.kccs.greenearth.framework.dao.db.GDBVirtualEntity;
import jp.co.kccs.greenearth.framework.dao.db.GDBVirtualEntityFactory;
import jp.co.kccs.greenearth.framework.dao.db.GDBVirtualEntityImpl;
import jp.co.kccs.greenearth.framework.dao.db.GSearchParameter;
import jp.co.kccs.greenearth.framework.dao.db.repository.GDBEntityFactory;
import jp.co.kccs.greenearth.framework.dao.db.repository.GForeignKey;
import jp.co.kccs.greenearth.framework.dao.enumtype.GDataType;
import jp.co.kccs.greenearth.framework.jdbc.GColumnType;
import jp.co.kccs.greenearth.testutils.dbunit.TestHelper;
import jp.co.kccs.greenearth.xform.configuration.GDbCommonSetting;
import jp.co.kccs.greenearth.xform.configuration.GTransformSetting;
import jp.co.kccs.greenearth.xform.configuration.GXFormSetting;
import jp.co.kccs.greenearth.xform.configuration.GXFormSettingHolder;
import jp.co.kccs.greenearth.xform.old.common.GXFormOldUtils;
import jp.co.kccs.greenearth.xform.old.configuration.GLocaleCommonSetting;
import jp.co.kccs.greenearth.xform.old.configuration.GReservedWordSetting;
import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static jp.co.kccs.greenearth.xform.old.common.GXFormOldUtils.getSchema;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * {@link jp.co.kccs.greenearth.xform.dao.common.GDao2Utils}のテストケース.<br>
 *
 * @create GEF_NEXT_DATE
 * @author KCSS yangfeng
 * @since GEF_NEXT_VERSION
 */
public abstract class GDao2UtilsTest {

	protected String dbScheme;
	protected GDbCommonSetting dbCommonSetting;
	protected abstract String getSetCommonSettingFile();
	protected abstract String getDbType();
	protected abstract void assertEqualBasicDataSource(BasicDataSource basicDataSource);

	protected abstract Map<Class<? extends GXFormSetting>, GXFormSetting> getSetting();

	public void cleanInsert(String filePath)  {
		try {
			TestHelper.cleanInsert(dbCommonSetting, filePath);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Before
	public void setup() throws Exception {
		Map<Class<? extends GXFormSetting>, GXFormSetting> xformSetting = getSetting();
		GXFormSettingHolder.setCommonSetting(GDbCommonSetting.class, xformSetting.get(GDbCommonSetting.class));
		GXFormSettingHolder.setCommonSetting(GReservedWordSetting.class, xformSetting.get(GReservedWordSetting.class));
		GXFormSettingHolder.setCommonSetting(GLocaleCommonSetting.class, xformSetting.get(GLocaleCommonSetting.class));
		GXFormSettingHolder.setCommonSetting(GTransformSetting.class, xformSetting.get(GTransformSetting.class));
		dbCommonSetting = GXFormSettingHolder.getSetting(GDbCommonSetting.class);
	}
	@After
	public void teardown() {
		GFrameworkProperties.refleshProperty();
	}

	private void setSetting() {
		Map<Class<? extends GXFormSetting>, GXFormSetting> xformSetting = getSetting();
		GXFormSettingHolder.setCommonSetting(GDbCommonSetting.class, xformSetting.get(GDbCommonSetting.class));
		GXFormSettingHolder.setCommonSetting(GReservedWordSetting.class, xformSetting.get(GReservedWordSetting.class));
		GXFormSettingHolder.setCommonSetting(GLocaleCommonSetting.class, xformSetting.get(GLocaleCommonSetting.class));
		GXFormSettingHolder.setCommonSetting(GTransformSetting.class, xformSetting.get(GTransformSetting.class));
		dbCommonSetting = GXFormSettingHolder.getSetting(GDbCommonSetting.class);
	}
//	/**
//	 * {@link GDao2Utils#setCommonSetting(GDaoCommonSetting)}のテスト.<br>
//	 * <br>
//	 * localeMapping:未設定の場合、正常に読み取る。<br>
//	 *
//	 * @create GEF_NEXT_DATE
//	 * @author KCSS yangfeng
//	 * @since GEF_NEXT_VERSION
//	 */
//	@Test
//	public void testSetCommonSettingNullLocaleMapping(){
//
//		String settingsFilePath = GFileUtils.getResource("jp/co/kccs/greenearth/xform/dao/common/GDao2UtilsTest/settings_setCommonSettingNullLocaleMapping.yaml").getPath();
//		GDaoCommonSettingParserFilePath commonSettingParser = new GDaoCommonSettingParserFilePath();
//		GDaoCommonSetting commonSetting = commonSettingParser.parse(settingsFilePath);
//
//		GDao2Utils.setCommonSetting(commonSetting);
//
//		assertEquals(GFrameworkProperties.getProperty("geframe.jk2.Locale.Language1"), "en");
//		assertEquals(GFrameworkProperties.getProperty("geframe.jk2.Locale.Language2"), "ja");
//		assertEquals(GFrameworkProperties.getProperty("geframe.jk2.Locale.Language3"), "zh");
//		assertEquals(GFrameworkProperties.getProperty("geframe.jk2.Locale.Language4"), "");
//		assertEquals(GFrameworkProperties.getProperty("geframe.jk2.Locale.Language5"), "");
//	}
//
//	/**
//	 * {@link GDao2Utils#setReservedWord(GReservedWordSetting)}のテスト.<br>
//	 * <br>
//	 * GReservedWordSettingに設定されている値をGFrameworkPropertiesに設定する。<br>
//	 *
//	 * @create GEF_NEXT_DATE
//	 * @author KCSS yangfeng
//	 * @since GEF_NEXT_VERSION
//	 */
//	@Test
//	public void testSetReservedWord() {
//
//		assertNotEquals(GFrameworkProperties.getProperty("geframe.dao.PrimaryKey"), "junitPrimaryKey");
//		assertNotEquals(GFrameworkProperties.getProperty("geframe.dao.CompanyCodeKey"), "junitCompanyCode");
//		assertNotEquals(GFrameworkProperties.getProperty("geframe.dao.ExclusiveKey"), "junitExclusiveKey");
//		assertNotEquals(GFrameworkProperties.getProperty("geframe.dao.InsertedDateKey"), "junitInsertedDate");
//		assertNotEquals(GFrameworkProperties.getProperty("geframe.dao.InsertedUserIDKey"), "junitInsertedUser");
//		assertNotEquals(GFrameworkProperties.getProperty("geframe.dao.UpdatedDateKey"), "junitUpdatedDate");
//		assertNotEquals(GFrameworkProperties.getProperty("geframe.dao.UpdatedUserIDKey"), "junitUpdatedUserId");
//
//		GReservedWordSetting reservedWordSetting = new GReservedWordSettingImpl();
//		reservedWordSetting.setPrimaryKeyColumn("junitPrimaryKey");
//		reservedWordSetting.setCompanyCodeColumn("junitCompanyCode");
//		reservedWordSetting.setExclusiveKeyColumn("junitExclusiveKey");
//		reservedWordSetting.setInsertedDateColumn("junitInsertedDate");
//		reservedWordSetting.setInsertedUserIdColumn("junitInsertedUser");
//		reservedWordSetting.setUpdatedDateColumn("junitUpdatedDate");
//		reservedWordSetting.setUpdatedUserIdColumn("junitUpdatedUserId");
//
//		GDao2Utils.setReservedWord(reservedWordSetting);
//
//		assertEquals(GFrameworkProperties.getProperty("geframe.dao.PrimaryKey"), "junitPrimaryKey");
//		assertEquals(GFrameworkProperties.getProperty("geframe.dao.CompanyCodeKey"), "junitCompanyCode");
//		assertEquals(GFrameworkProperties.getProperty("geframe.dao.ExclusiveKey"), "junitExclusiveKey");
//		assertEquals(GFrameworkProperties.getProperty("geframe.dao.InsertedDateKey"), "junitInsertedDate");
//		assertEquals(GFrameworkProperties.getProperty("geframe.dao.InsertedUserIDKey"), "junitInsertedUser");
//		assertEquals(GFrameworkProperties.getProperty("geframe.dao.UpdatedDateKey"), "junitUpdatedDate");
//		assertEquals(GFrameworkProperties.getProperty("geframe.dao.UpdatedUserIDKey"), "junitUpdatedUserId");
//	}
//
//	/**
//	 * {@link GDao2Utils#convertFromDBEntity(GDBEntity)}のテスト.<br>
//	 * <br>
//	 * GDBEntityが空白の場合、GXFormEntityへ正常に変換する。<br>
//	 *
//	 * @create GEF_NEXT_DATE
//	 * @author KCSS yangfeng
//	 * @since GEF_NEXT_VERSION
//	 */
//	@Test
//	public void testConvertFromDBEntityEmpty() {
//		GDBEntity dbEntity = new GDBEntityImpl();
//
//		GXFormEntity gxFormEntity = GDao2Utils.convertFromDBEntity(dbEntity);
//		assertNull(gxFormEntity.getName());
//		assertNull(gxFormEntity.getPhyName());
//		assertNull(gxFormEntity.getDatabaseName());
//		assertEquals(gxFormEntity.getColumns().size(), 0);
//		assertEquals(gxFormEntity.getPrimaryKey().size(), 0);
//		assertEquals(gxFormEntity.getUniqueKeys().size(), 0);
//		assertEquals(gxFormEntity.getForeignKeys().size(), 0);
//
//		GEntity entity = gxFormEntity.toEntity();
//		assertNull(entity.getName());
//		assertNull(entity.getPhyName());
//		assertNull(entity.getDatabase().getName());
//		assertEquals(entity.getColumns().size(), 0);
//		assertEquals(entity.getPrimaryKey().getReferenceColumns().size(), 0);
//		assertEquals(entity.getUniqueKeys().size(), 0);
//		assertEquals(entity.getForeignKeys().size(), 0);
//	}
//
//	/**
//	 * {@link GDao2Utils#convertFromDBEntity(GDBEntity)}のテスト.<br>
//	 * <br>
//	 * GDBEntityが空白ではない場合、GXFormEntityへ正常に変換する。<br>
//	 *
//	 * @create GEF_NEXT_DATE
//	 * @author KCSS yangfeng
//	 * @since GEF_NEXT_VERSION
//	 */
//	@Test
//	public void testConvertFromDBEntityFull() {
//		cleanInsert("jp/co/kccs/greenearth/xform/dao/common/GDao2UtilsTest/import_entity.xml");
//		String entityId = "CB30B5B47816426A8A14AFB44D6CA251";
//		GDBEntity gdbEntity = GDBEntityFactory.getDBEntity(entityId);
//		gdbEntity.setVirtualEntity(getDbEntityForeignKey(entityId));
//
//		GXFormEntity gxFormEntity = GDao2Utils.convertFromDBEntity(gdbEntity);
//		assertEquals(gxFormEntity.getName(), "jdcb_tool_test_allColumntype");
//		assertEquals(gxFormEntity.getPhyName(), "jdcb_tool_test_allColumntype");
//		assertEquals(gxFormEntity.getDatabaseName(), dbScheme);
//		assertEquals(gxFormEntity.getColumns().size(), 14);
//		assertEquals(gxFormEntity.getPrimaryKey().size(), 1);
//		assertEquals(gxFormEntity.getUniqueKeys().size(), 3);
//		assertEquals(gxFormEntity.getForeignKeys().size(), 3);
//
//		GEntity entity = gxFormEntity.toEntity();
//		assertEquals(entity.getName(), "jdcb_tool_test_allColumntype");
//		assertEquals(entity.getPhyName(), "jdcb_tool_test_allColumntype");
//		assertEquals(entity.getDatabase().getName(), dbScheme);
//		assertEquals(entity.getColumns().size(), 14);
//		assertEquals(entity.getPrimaryKey().getReferenceColumns().size(), 1);
//		assertEquals(entity.getUniqueKeys().size(), 3);
//		assertEquals(entity.getForeignKeys().size(), 3);
//	}

	/**
	 * {@link GDao2Utils#convertStringToGColumnTypeVE(GDataType)}のテスト.<br>
	 * <br>
	 * GDataTypeをGColumnTypeへ正常に変換する。<br>
	 *
	 * @create GEF_NEXT_DATE
	 * @author KCSS yangfeng
	 * @since GEF_NEXT_VERSION
	 */
	@Test
	public void testConvertStringToGColumnTypeVE() {
		assertEquals(GColumnType.INTEGER, GDao2Utils.convertStringToGColumnTypeVE(GDataType.INTEGER));
		assertEquals(GColumnType.VARCHAR, GDao2Utils.convertStringToGColumnTypeVE(GDataType.STRING));
		assertEquals(GColumnType.DOUBLE, GDao2Utils.convertStringToGColumnTypeVE(GDataType.DECIMAL));
		assertEquals(GColumnType.DATE, GDao2Utils.convertStringToGColumnTypeVE(GDataType.DATE));
		assertEquals(GColumnType.LONG, GDao2Utils.convertStringToGColumnTypeVE(GDataType.LONG));
		assertNull(GDao2Utils.convertStringToGColumnTypeVE(GDataType.FILE));
		assertNull(GDao2Utils.convertStringToGColumnTypeVE(GDataType.BINARY));
	}

	/**
	 * {@link GDao2Utils#setParameterToGSearchParameter(GSearchParameter)}のテスト.<br>
	 * <br>
	 * GSearchParameterにパラメータ値を正しく設定すること。<br>
	 *
	 * @create GEF_NEXT_DATE
	 * @author KCSS yangfeng
	 * @since GEF_NEXT_VERSION
	 */
	@Test
	public void testSetParameterToGSearchParameter() {
		cleanInsert("jp/co/kccs/greenearth/xform/dao/common/GDao2UtilsTest/import_searchParameter.xml");
		GDBVirtualEntity virtualEntity = GDBVirtualEntityFactory.getVirtualEntity("A231AF5843414E50BDBB6675E277E24E");
		GSearchParameter parameter = virtualEntity.createSearchParameter();
		GSearchParameter searchParameter = GDao2Utils.setParameterToGSearchParameter(parameter);

		assertEquals(searchParameter.getValues().length, 9);
		assertEquals(searchParameter.getValueByIndex(0), "");
		assertEquals(searchParameter.getValueByIndex(1), "");
		assertEquals(searchParameter.getValueByIndex(2), 0);
		assertEquals(searchParameter.getValueByIndex(3), "");
		assertEquals(searchParameter.getValueByIndex(4).getClass(), Date.class);
		assertEquals(searchParameter.getValueByIndex(5), "");
		assertEquals(searchParameter.getValueByIndex(6), new BigDecimal(0));
//		assertEquals(searchParameter.getValueByIndex(7), 0L);
		assertEquals(searchParameter.getValueByIndex(8), "");

	}

	private GDBVirtualEntity getDbEntityForeignKey(String entityId) {
		GDBVirtualEntity ve = new GDBVirtualEntityImpl();
		try {
			List<Map<String, Object>> resultSet = GXFormOldUtils.executeSQL(String.format("Select objectID AS \"objectID\" from %s.SForeignKey where sourceEntityObjectID =  '%s'", getSchema(), entityId));

			List<String> resultList = new ArrayList<>();
			for (Map<String, Object> stringObjectMap : resultSet) {
				resultList.add((String) stringObjectMap.get("objectID"));
			}
			resultList.forEach(result-> {
				GForeignKey foreignKey = GDBEntityFactory.getForeignKey(result);
				ve.addForeignKey(foreignKey);
			});
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return ve;
	}
}
