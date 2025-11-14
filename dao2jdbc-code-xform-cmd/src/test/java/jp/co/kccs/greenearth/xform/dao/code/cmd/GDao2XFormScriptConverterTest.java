package jp.co.kccs.greenearth.xform.dao.code.cmd;

import jp.co.kccs.greenearth.commons.GFrameworkUtils;
import jp.co.kccs.greenearth.commons.utils.GFileUtils;
import jp.co.kccs.greenearth.framework.dao.db.GDBVirtualEntity;
import jp.co.kccs.greenearth.testutils.dbunit.TestHelper;
import jp.co.kccs.greenearth.xform.code.dao.core.GCrudType;
import jp.co.kccs.greenearth.xform.code.dao.core.GDao2XFormScriptConverter;
import jp.co.kccs.greenearth.xform.code.dao.core.creation.GVeIdMetadataLoaderImpl;
import jp.co.kccs.greenearth.xform.code.dao.core.creation.GVeMetadataLoader;
import jp.co.kccs.greenearth.xform.code.dao.core.service.GDao2XFormSqlResult;
import jp.co.kccs.greenearth.xform.code.dao.core.service.GDao2XFormSqlResultImpl;
import jp.co.kccs.greenearth.xform.code.dao.core.sql.GVEMetadata2SqlExtractor;
import jp.co.kccs.greenearth.xform.code.jdbc.core.*;
import jp.co.kccs.greenearth.xform.code.jdbc.core.service.GXFormScript;
import jp.co.kccs.greenearth.xform.code.jdbc.core.service.GXFormScriptColumn;
import jp.co.kccs.greenearth.xform.dao.common.GDao2Utils;
import jp.co.kccs.greenearth.xform.dao.common.GDaoCommonSetting;
import jp.co.kccs.greenearth.xform.dao.common.GDaoCommonSettingParserFilePath;
import jp.co.kccs.greenearth.xform.dao.common.GVEItem;
import jp.co.kccs.greenearth.xform.jdbc.common.GXFormEntity;
import org.junit.Before;
import org.junit.Test;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author KCCS XXXXX
 * @create GEF_NEXT_DATE
 * @since GEF_NEXT_VERSION
 */
public abstract class GDao2XFormScriptConverterTest {

	GVeMetadataLoader veMetadataLoader = new GVeIdMetadataLoaderImpl();

	GVEItem<GDBVirtualEntity> virtualEntity;

	protected String dbSchema;

	protected GDaoCommonSetting commonSetting;

	@Before
	public void setup() throws Exception {
		String settingsFilePath = GFileUtils.getResource(getSettingsFilePath()).getPath();
		GDaoCommonSettingParserFilePath commonSettingParser = new GDaoCommonSettingParserFilePath();
		commonSetting = commonSettingParser.parse(settingsFilePath);
		dbSchema = commonSetting.getDb().getSchema();
		GDao2Utils.setCommonSetting(commonSetting);
	}

	public void cleanInsert(String filePath)  {
		try {
			TestHelper.cleanInsert(commonSetting.getDb(), filePath);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected abstract String getSettingsFilePath();

	protected abstract String getTimestampCommand();

	// [1]
	@Test
	public void testConvert_WherePartsTrimOptionLike() {

		cleanInsert("jp/co/kccs/greenearth/xform/code/dao/core/metadata/GDao2XFormScriptConverterTest/import_trim_option_like.xml");
		String veId = "940E79601B9143D687A8500F9915F7D8";
		virtualEntity = (GVEItem<GDBVirtualEntity>) veMetadataLoader.create(veId);
		GVEMetadata2SqlExtractor sqlExtractor = GFrameworkUtils.getComponent(GVEMetadata2SqlExtractor.class);
		String sqlDescriptor_Select = sqlExtractor.extract(virtualEntity.getData(), GCrudType.SELECT);
		GDao2XFormScriptConverter converter = GFrameworkUtils.getComponent(GDao2XFormScriptConverter.class);
		GDao2XFormSqlResult sqlResult = new GDao2XFormSqlResultImpl();
		sqlResult.setSqlScript(sqlDescriptor_Select);
		sqlResult.setType(GCrudType.SELECT);
		sqlResult.setVirtualEntity(virtualEntity.getData());
		GXFormScript formScript = converter.convert(sqlResult.getSqlScript(), jp.co.kccs.greenearth.xform.jdbc.common.GCrudType.valueOf(sqlResult.getType().toString()), GDao2Utils.convertFrom(sqlResult.getVirtualEntity()));

		assertEquals("仮想表ID: 940E79601B9143D687A8500F9915F7D8", formScript.getDescription());
		assertEquals(GCrudType.SELECT, formScript.getCrudType());
		assertEquals(9, formScript.getApiTypes().size());
		assertTrue(formScript.getApiTypes().contains(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE)));
		assertTrue(formScript.getApiTypes().contains(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE_PK)));
		assertTrue(formScript.getApiTypes().contains(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE_UK)));
		assertTrue(formScript.getApiTypes().contains(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE)));
		assertTrue(formScript.getApiTypes().contains(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE_PK)));
		assertTrue(formScript.getApiTypes().contains(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE_UK)));
		assertTrue(formScript.getApiTypes().contains(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE)));
		assertTrue(formScript.getApiTypes().contains(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE_PK)));
		assertTrue(formScript.getApiTypes().contains(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE_UK)));
		assertEquals("SELECT jdbc_tool_test_pk_uk_mainmaster.objectID," +
				"jdbc_tool_test_pk_uk_mainmaster.ItemCD," +
				"jdbc_tool_test_pk_uk_mainmaster.ItemNA," +
				"jdbc_tool_test_pk_uk_mainmaster.UnitPrice," +
				"jdbc_tool_test_pk_uk_mainmaster.SellPrice," +
				"jdbc_tool_test_pk_uk_mainmaster.Flag," +
				"jdbc_tool_test_pk_uk_mainmaster.PriceFlag," +
				"jdbc_tool_test_pk_uk_mainmaster.ExclusiveFG," +
				"jdbc_tool_test_pk_uk_mainmaster.RegisteredDT," +
				"jdbc_tool_test_pk_uk_mainmaster.UpdatedDT," +
				"jdbc_tool_test_mainmaster.objectID," +
				"jdbc_tool_test_unitmaster.Code," +
				"jdbc_tool_test_ordermaster.SlipNO," +
				"jdbc_tool_test_mainmaster.SellPrice " +
				"FROM " + dbSchema + ".jdbc_tool_test_pk_uk_mainmaster jdbc_tool_test_pk_uk_mainmaster " +
				"LEFT OUTER JOIN " + dbSchema + ".jdbc_tool_test_unitmaster jdbc_tool_test_unitmaster " +
				"ON jdbc_tool_test_pk_uk_mainmaster.objectID=jdbc_tool_test_unitmaster.Code " +
				"INNER JOIN " + dbSchema + ".jdbc_tool_test_mainmaster jdbc_tool_test_mainmaster " +
				"ON jdbc_tool_test_pk_uk_mainmaster.objectID=jdbc_tool_test_mainmaster.objectID " +
				"RIGHT OUTER JOIN " + dbSchema + ".jdbc_tool_test_ordermaster jdbc_tool_test_ordermaster " +
				"ON jdbc_tool_test_pk_uk_mainmaster.ItemCD=jdbc_tool_test_ordermaster.objectID " +
				"WHERE ( jdbc_tool_test_pk_uk_mainmaster.objectID=? " +
				"AND jdbc_tool_test_unitmaster.Code=? " +
				"AND jdbc_tool_test_mainmaster.objectID=? " +
				"AND jdbc_tool_test_ordermaster.SlipNO LIKE ? ESCAPE '/' " +
				"AND jdbc_tool_test_pk_uk_mainmaster.ItemCD LIKE ? ESCAPE '/' " +
				"AND jdbc_tool_test_pk_uk_mainmaster.ItemNA LIKE ? ESCAPE '/' )", formScript.getScript());

		List<GXFormScriptColumn> trimWhereColumns = formScript.getWhereColumns()
				.stream().filter(column->column.getTrimValue() != null).collect(Collectors.toList())
				.stream().sorted(Comparator.comparing(GXFormScriptColumn::getName)).collect(Collectors.toList());
		assertEquals(2, trimWhereColumns.size());
		assertEquals("Code", trimWhereColumns.get(0).getName());
		assertEquals(GTrimType.BOTH, trimWhereColumns.get(0).getTrimValue());
		assertEquals("objectID", trimWhereColumns.get(1).getName());
		assertEquals(GTrimType.BOTH, trimWhereColumns.get(1).getTrimValue());

		// TODO
		List<GXFormScriptColumn> optionWhereColumns = formScript.getWhereColumns()
				.stream().filter(column->column.isOptional()).collect(Collectors.toList())
				.stream().sorted(Comparator.comparing(GXFormScriptColumn::getName)).collect(Collectors.toList());
		assertEquals(2, optionWhereColumns.size());
		assertEquals("objectID", optionWhereColumns.get(0).getName());
		assertEquals(true, optionWhereColumns.get(0).isOptional());
		assertEquals("objectID", optionWhereColumns.get(1).getName());
		assertEquals(true, optionWhereColumns.get(1).isOptional());

		List<GXFormScriptColumn> likeWhereColumns = formScript.getWhereColumns()
				.stream().filter(column->column.getLikeType() != null).collect(Collectors.toList())
				.stream().sorted(Comparator.comparing(GXFormScriptColumn::getName)).collect(Collectors.toList());
		assertEquals(3, likeWhereColumns.size());
		assertEquals("ItemCD", likeWhereColumns.get(0).getName());
		assertEquals(GLikeType.LIKE_PART, likeWhereColumns.get(0).getLikeType());
		assertEquals("ItemNA", likeWhereColumns.get(1).getName());
		assertEquals(GLikeType.LIKE_SUFFIX, likeWhereColumns.get(1).getLikeType());
		assertEquals("SlipNO", likeWhereColumns.get(2).getName());
		assertEquals(GLikeType.LIKE_PREFIX, likeWhereColumns.get(2).getLikeType());

	}

	@Test
	public void testConvert_ForeignKey() {

		cleanInsert("jp/co/kccs/greenearth/xform/code/dao/core/metadata/GDao2XFormScriptConverterTest/import_foreignkey.xml");
		String veId = "E44ADEF7B30549449EDF193ECFB66631";
		virtualEntity = (GVEItem<GDBVirtualEntity>) veMetadataLoader.create(veId);
		GVEMetadata2SqlExtractor sqlExtractor = GFrameworkUtils.getComponent(GVEMetadata2SqlExtractor.class);
		String sqlDescriptor_Select = sqlExtractor.extract(virtualEntity.getData(), GCrudType.SELECT);
		GDao2XFormScriptConverter converter = GFrameworkUtils.getComponent(GDao2XFormScriptConverter.class);
		GDao2XFormSqlResult sqlResult = new GDao2XFormSqlResultImpl();
		sqlResult.setVirtualEntity(virtualEntity.getData());
		sqlResult.setSqlScript(sqlDescriptor_Select);
		sqlResult.setType(GCrudType.SELECT);
		GXFormScript formScript = converter.convert(sqlResult.getSqlScript(), jp.co.kccs.greenearth.xform.jdbc.common.GCrudType.DELETE, GDao2Utils.convertFrom(sqlResult.getVirtualEntity()));
		assertEquals(GCrudType.SELECT, formScript.getCrudType());
		assertEquals(9, formScript.getApiTypes().size());
		assertTrue(formScript.getApiTypes().contains(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE)));
		assertTrue(formScript.getApiTypes().contains(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE_PK)));
		assertTrue(formScript.getApiTypes().contains(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE_UK)));
		assertTrue(formScript.getApiTypes().contains(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE)));
		assertTrue(formScript.getApiTypes().contains(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE_PK)));
		assertTrue(formScript.getApiTypes().contains(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE_UK)));
		assertTrue(formScript.getApiTypes().contains(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE)));
		assertTrue(formScript.getApiTypes().contains(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE_PK)));
		assertTrue(formScript.getApiTypes().contains(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE_UK)));
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
				"LEFT OUTER JOIN " + dbSchema + ".jdbc_tool_test_unitmaster jdbc_tool_test_unitmaster " +
				"ON jdbc_tool_test_pk_uk_mainmaster.objectID=jdbc_tool_test_unitmaster.Code " +
				"INNER JOIN " + dbSchema + ".jdbc_tool_test_mainmaster jdbc_tool_test_mainmaster " +
				"ON jdbc_tool_test_pk_uk_mainmaster.objectID=jdbc_tool_test_mainmaster.objectID " +
				"RIGHT OUTER JOIN " + dbSchema + ".jdbc_tool_test_ordermaster jdbc_tool_test_ordermaster " +
				"ON jdbc_tool_test_pk_uk_mainmaster.ItemCD=jdbc_tool_test_ordermaster.objectID " +
				"WHERE jdbc_tool_test_pk_uk_mainmaster.objectID=?", formScript.getScript());
		assertEquals(1, formScript.getEntities().size());
		GXFormEntity gxFormEntity = formScript.getEntities().get("jdbc_tool_test_pk_uk_mainmaster");
		assertEquals(3, gxFormEntity.getForeignKeys().size());
		assertEquals("ItemCD", gxFormEntity.getForeignKeys().get("FK_OrderMaster").getColumns().get(0).getSrcColumn());
		assertEquals("objectID", gxFormEntity.getForeignKeys().get("FK_OrderMaster").getColumns().get(0).getRefColumn());
		assertEquals("objectID", gxFormEntity.getForeignKeys().get("FK_MainMaster").getColumns().get(0).getSrcColumn());
		assertEquals("objectID", gxFormEntity.getForeignKeys().get("FK_MainMaster").getColumns().get(0).getRefColumn());
		assertEquals("objectID", gxFormEntity.getForeignKeys().get("FK_UnitMaster").getColumns().get(0).getSrcColumn());
		assertEquals("Code", gxFormEntity.getForeignKeys().get("FK_UnitMaster").getColumns().get(0).getRefColumn());
	}

	// [3]
	@Test
	public void testConvert_CUD() {
		cleanInsert("jp/co/kccs/greenearth/xform/code/dao/core/metadata/GDao2XFormScriptConverterTest/import_cud.xml");
		String veId = "193814253CB6440E83B1088325C8EFCD";
		virtualEntity = (GVEItem<GDBVirtualEntity>) veMetadataLoader.create(veId);
		GVEMetadata2SqlExtractor sqlExtractor = GFrameworkUtils.getComponent(GVEMetadata2SqlExtractor.class);

		// Insert
		String sqlDescriptor_Insert = sqlExtractor.extract(virtualEntity.getData(), GCrudType.INSERT);
		GDao2XFormSqlResult sqlResult = new GDao2XFormSqlResultImpl();
		sqlResult.setType(GCrudType.INSERT);
		sqlResult.setSqlScript(sqlDescriptor_Insert);
		sqlResult.setVirtualEntity(virtualEntity.getData());
		GXFormScript formScriptInsert = GFrameworkUtils.getComponent(GDao2XFormScriptConverter.class)
				.convert(sqlResult.getSqlScript(), jp.co.kccs.greenearth.xform.jdbc.common.GCrudType.INSERT, GDao2Utils.convertFrom(sqlResult.getVirtualEntity()));

		assertEquals("INSERT INTO " + dbSchema + ".jdbc_tool_test_pk_uk_mainmaster" +
				"(objectID,ItemCD,ItemNA,UnitPrice,SellPrice,Flag,PriceFlag,ExclusiveFG,RegisteredDT,UpdatedDT) " +
				"VALUES(?,?,?,?,?,?,?,?," + getTimestampCommand() + "," + getTimestampCommand() + ")", formScriptInsert.getScript());
		assertEquals(GCrudType.INSERT, formScriptInsert.getCrudType());
		assertEquals(3, formScriptInsert.getApiTypes().size());
		assertTrue(formScriptInsert.getApiTypes().contains(new GCompoundApiImpl(GTerminatorApi.EXECUTE, null)));
		assertTrue(formScriptInsert.getApiTypes().contains(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, null)));
		assertTrue(formScriptInsert.getApiTypes().contains(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, null)));
		assertEquals("仮想表ID: 193814253CB6440E83B1088325C8EFCD", formScriptInsert.getDescription());

		// Update
		String sqlDescriptor_Update = sqlExtractor.extract(virtualEntity.getData(), GCrudType.UPDATE);
		GDao2XFormSqlResult sqlResultUpdate = new GDao2XFormSqlResultImpl();
		sqlResultUpdate.setVirtualEntity(virtualEntity.getData());
		sqlResultUpdate.setType(GCrudType.UPDATE);
		sqlResultUpdate.setSqlScript(sqlDescriptor_Update);
		GXFormScript formScriptUpdate = GFrameworkUtils.getComponent(GDao2XFormScriptConverter.class)
				.convert(sqlResultUpdate.getSqlScript(), jp.co.kccs.greenearth.xform.jdbc.common.GCrudType.UPDATE, GDao2Utils.convertFrom(sqlResult.getVirtualEntity()));

		assertEquals("UPDATE " + dbSchema + ".jdbc_tool_test_pk_uk_mainmaster jdbc_tool_test_pk_uk_mainmaster " +
				"SET ItemCD=?,ItemNA=?,UnitPrice=?,SellPrice=?,Flag=?,PriceFlag=?,ExclusiveFG=?,RegisteredDT=?,UpdatedDT=? " +
				"WHERE ( jdbc_tool_test_pk_uk_mainmaster.ItemCD=? " +
				"AND jdbc_tool_test_pk_uk_mainmaster.ItemNA=? " +
				"AND jdbc_tool_test_pk_uk_mainmaster.objectID=? )" , formScriptUpdate.getScript());
		assertEquals(GCrudType.UPDATE, formScriptUpdate.getCrudType());
		assertEquals(9, formScriptUpdate.getApiTypes().size());
		assertTrue(formScriptUpdate.getApiTypes().contains(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE)));
		assertTrue(formScriptUpdate.getApiTypes().contains(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_PK)));
		assertTrue(formScriptUpdate.getApiTypes().contains(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_UK)));
		assertTrue(formScriptUpdate.getApiTypes().contains(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE)));
		assertTrue(formScriptUpdate.getApiTypes().contains(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_PK)));
		assertTrue(formScriptUpdate.getApiTypes().contains(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_UK)));
		assertTrue(formScriptUpdate.getApiTypes().contains(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE)));
		assertTrue(formScriptUpdate.getApiTypes().contains(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_PK)));
		assertTrue(formScriptUpdate.getApiTypes().contains(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_UK)));
		assertEquals("仮想表ID: 193814253CB6440E83B1088325C8EFCD", formScriptUpdate.getDescription());

		// Delete
		String sqlDescriptor_Delete = sqlExtractor.extract(virtualEntity.getData(), GCrudType.DELETE);
		GDao2XFormSqlResult sqlResultDelete = new GDao2XFormSqlResultImpl();
		sqlResultDelete.setSqlScript(sqlDescriptor_Delete);
		sqlResultDelete.setType(GCrudType.DELETE);
		sqlResultDelete.setVirtualEntity(virtualEntity.getData());
		GXFormScript formScriptDelete = GFrameworkUtils.getComponent(GDao2XFormScriptConverter.class)
				.convert(sqlResultDelete.getSqlScript(), jp.co.kccs.greenearth.xform.jdbc.common.GCrudType.DELETE, GDao2Utils.convertFrom(sqlResult.getVirtualEntity()));

		assertEquals(getDeleteSql_Convert_CUD(), formScriptDelete.getScript());
		assertEquals(GCrudType.DELETE, formScriptDelete.getCrudType());
		assertEquals(9, formScriptDelete.getApiTypes().size());
		assertTrue(formScriptUpdate.getApiTypes().contains(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE)));
		assertTrue(formScriptUpdate.getApiTypes().contains(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_PK)));
		assertTrue(formScriptUpdate.getApiTypes().contains(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_UK)));
		assertTrue(formScriptUpdate.getApiTypes().contains(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE)));
		assertTrue(formScriptUpdate.getApiTypes().contains(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_PK)));
		assertTrue(formScriptUpdate.getApiTypes().contains(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_UK)));
		assertTrue(formScriptUpdate.getApiTypes().contains(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE)));
		assertTrue(formScriptUpdate.getApiTypes().contains(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_PK)));
		assertTrue(formScriptUpdate.getApiTypes().contains(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_UK)));
		assertEquals("仮想表ID: 193814253CB6440E83B1088325C8EFCD", formScriptDelete.getDescription());
	}

	protected String getDeleteSql_Convert_CUD(){
		return "DELETE FROM " + dbSchema + ".jdbc_tool_test_pk_uk_mainmaster " +
				"WHERE ( " + dbSchema + ".jdbc_tool_test_pk_uk_mainmaster.ItemCD=? " +
				"AND " + dbSchema + ".jdbc_tool_test_pk_uk_mainmaster.ItemNA=? " +
				"AND " + dbSchema + ".jdbc_tool_test_pk_uk_mainmaster.objectID=? )";
	}
}
