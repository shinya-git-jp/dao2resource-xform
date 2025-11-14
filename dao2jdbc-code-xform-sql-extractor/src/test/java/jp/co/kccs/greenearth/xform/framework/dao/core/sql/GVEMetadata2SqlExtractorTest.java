package jp.co.kccs.greenearth.xform.framework.dao.core.sql;

import jp.co.kccs.greenearth.commons.GFrameworkUtils;
import jp.co.kccs.greenearth.framework.dao.db.GDBVirtualEntity;
import jp.co.kccs.greenearth.testutils.dbunit.TestHelper;
import jp.co.kccs.greenearth.xform.code.dao.core.GCrudType;
import jp.co.kccs.greenearth.xform.code.dao.core.creation.GVeIdMetadataLoaderImpl;
import jp.co.kccs.greenearth.xform.code.dao.core.creation.GVeMetadataLoader;
import jp.co.kccs.greenearth.xform.code.dao.core.sql.GVEMetadata2SqlExtractor;
import jp.co.kccs.greenearth.xform.configuration.GDbCommonSetting;
import jp.co.kccs.greenearth.xform.configuration.GTransformSetting;
import jp.co.kccs.greenearth.xform.configuration.GXFormSetting;
import jp.co.kccs.greenearth.xform.configuration.GXFormSettingHolder;
import jp.co.kccs.greenearth.xform.dao.common.GVEItem;
import jp.co.kccs.greenearth.xform.old.configuration.GLocaleCommonSetting;
import jp.co.kccs.greenearth.xform.old.configuration.GReservedWordSetting;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.util.HashMap;
import java.util.Map;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * @author KCCS XXXXX
 * @create GEF_NEXT_DATE
 * @since GEF_NEXT_VERSION
 */
public abstract class GVEMetadata2SqlExtractorTest {

	private static final String SELECT = "SELECT";
	private static final String INSERT = "INSERT";
	private static final String UPDATE = "UPDATE";
	private static final String DELETE = "DELETE";

	protected String dbSchema;
	protected GDbCommonSetting dbCommonSetting;
	protected GVeMetadataLoader veMetadataLoader = new GVeIdMetadataLoaderImpl();

	@Before
	public void setup() throws Exception {
		Map<Class<? extends GXFormSetting>, GXFormSetting> xformSetting = getSetting();
		GXFormSettingHolder.setCommonSetting(GDbCommonSetting.class, xformSetting.get(GDbCommonSetting.class));
		GXFormSettingHolder.setCommonSetting(GReservedWordSetting.class, xformSetting.get(GReservedWordSetting.class));
		GXFormSettingHolder.setCommonSetting(GLocaleCommonSetting.class, xformSetting.get(GLocaleCommonSetting.class));
		GXFormSettingHolder.setCommonSetting(GTransformSetting.class, xformSetting.get(GTransformSetting.class));
		dbCommonSetting = GXFormSettingHolder.getSetting(GDbCommonSetting.class);
		dbSchema = GXFormSettingHolder.getSetting(GDbCommonSetting.class).getSchema();
	}

	@After
	public void release() {
		GXFormSettingHolder.clearCommonSetting();
	}

	public void cleanInsert(String filePath)  {
		try {
			TestHelper.cleanInsert(dbCommonSetting, filePath);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	protected abstract Map<Class<? extends GXFormSetting>, GXFormSetting> getSetting();

	protected abstract String getTimestampCommand();

	protected String getDeleteSql_Extract_inq() {
		return "DELETE FROM " + dbSchema + ".jdbc_tool_test_pk_uk_mainmaster WHERE " + dbSchema + ".jdbc_tool_test_pk_uk_mainmaster.objectID=?";
	}

	@Test
	public void testExtract_inq() {
		cleanInsert("jp/co/kccs/greenearth/xform/code/dao/core/sql/GVEMetadata2SqlExtractorTest/import_extract_select.xml");
		String veId = "57E66373CB914D9F88DB638EAF6B1699";
		Map<String, String> sqlDescriptorMap = getGSqlDescriptor(veId);

		assertEquals("SELECT jdbc_tool_test_pk_uk_mainmaster.objectID," +
				"jdbc_tool_test_pk_uk_mainmaster.ItemCD," +
				"jdbc_tool_test_pk_uk_mainmaster.ItemNA," +
				"jdbc_tool_test_pk_uk_mainmaster.UnitPrice," +
				"jdbc_tool_test_pk_uk_mainmaster.SellPrice," +
				"jdbc_tool_test_pk_uk_mainmaster.Flag," +
				"jdbc_tool_test_pk_uk_mainmaster.PriceFlag," +
				"jdbc_tool_test_pk_uk_mainmaster.ExclusiveFG," +
				"jdbc_tool_test_pk_uk_mainmaster.RegisteredDT," +
				"jdbc_tool_test_pk_uk_mainmaster.UpdatedDT " +
				"FROM " + dbSchema + ".jdbc_tool_test_pk_uk_mainmaster jdbc_tool_test_pk_uk_mainmaster " +
				"WHERE jdbc_tool_test_pk_uk_mainmaster.objectID=?", sqlDescriptorMap.get(SELECT));
		assertNull(sqlDescriptorMap.get(INSERT));
		assertNull(sqlDescriptorMap.get(UPDATE));
		assertEquals(getDeleteSql_Extract_inq(), sqlDescriptorMap.get(DELETE));
	}

	protected String getDeleteSql_Extract_reg() {
		return "DELETE FROM " + dbSchema + ".jdbc_tool_test_pk_uk_mainmaster " +
				"WHERE " + dbSchema + ".jdbc_tool_test_pk_uk_mainmaster.objectID=?";
	}

	@Test
	public void testExtract_reg() {
		cleanInsert("jp/co/kccs/greenearth/xform/code/dao/core/sql/GVEMetadata2SqlExtractorTest/import_extract_cud.xml");
		String veId = "1FEFE156727646DC9122045339BD3999";
		Map<String, String> sqlDescriptorMap = getGSqlDescriptor(veId);

		assertEquals("SELECT jdbc_tool_test_pk_uk_mainmaster.objectID," +
						"jdbc_tool_test_pk_uk_mainmaster.ItemCD," +
						"jdbc_tool_test_pk_uk_mainmaster.ItemNA," +
						"jdbc_tool_test_pk_uk_mainmaster.UnitPrice," +
						"jdbc_tool_test_pk_uk_mainmaster.SellPrice," +
						"jdbc_tool_test_pk_uk_mainmaster.Flag," +
						"jdbc_tool_test_pk_uk_mainmaster.PriceFlag," +
						"jdbc_tool_test_pk_uk_mainmaster.ExclusiveFG," +
						"jdbc_tool_test_pk_uk_mainmaster.RegisteredDT," +
						"jdbc_tool_test_pk_uk_mainmaster.UpdatedDT " +
						"FROM " + dbSchema + ".jdbc_tool_test_pk_uk_mainmaster jdbc_tool_test_pk_uk_mainmaster " +
						"WHERE jdbc_tool_test_pk_uk_mainmaster.objectID=?",
				sqlDescriptorMap.get(SELECT));
		assertEquals("INSERT INTO " + dbSchema + ".jdbc_tool_test_pk_uk_mainmaster(objectID,ItemCD,ItemNA,UnitPrice,SellPrice,Flag,PriceFlag,ExclusiveFG,RegisteredDT,UpdatedDT) " +
				"VALUES(?,?,?,?,?,?,?,?," + getTimestampCommand() + "," + getTimestampCommand() + ")", sqlDescriptorMap.get(INSERT));
		assertEquals("UPDATE " + dbSchema + ".jdbc_tool_test_pk_uk_mainmaster jdbc_tool_test_pk_uk_mainmaster " +
				"SET ItemCD=?,ItemNA=?,UnitPrice=?,SellPrice=?,Flag=?,PriceFlag=?,ExclusiveFG=?,RegisteredDT=?,UpdatedDT=? " +
				"WHERE jdbc_tool_test_pk_uk_mainmaster.objectID=?", sqlDescriptorMap.get(UPDATE));
		assertEquals(getDeleteSql_Extract_reg(), sqlDescriptorMap.get(DELETE));
	}

	private Map<String, String> getGSqlDescriptor(String veId){
		GVEItem<GDBVirtualEntity> virtualEntity = (GVEItem<GDBVirtualEntity>) veMetadataLoader.create(veId);

		GVEMetadata2SqlExtractor sqlExtractor = GFrameworkUtils.getComponent(GVEMetadata2SqlExtractor.class);
		String sqlDescriptor_Select = sqlExtractor.extract(virtualEntity.getData(), GCrudType.SELECT);
		String sqlDescriptor_Insert = sqlExtractor.extract(virtualEntity.getData(), GCrudType.INSERT);
		String sqlDescriptor_Update = sqlExtractor.extract(virtualEntity.getData(), GCrudType.UPDATE);
		String sqlDescriptor_Delete = sqlExtractor.extract(virtualEntity.getData(), GCrudType.DELETE);

		Map resultMap = new HashMap();
		resultMap.put(SELECT, sqlDescriptor_Select);
		resultMap.put(INSERT, sqlDescriptor_Insert);
		resultMap.put(UPDATE, sqlDescriptor_Update);
		resultMap.put(DELETE, sqlDescriptor_Delete);

		return resultMap;
	}
}
