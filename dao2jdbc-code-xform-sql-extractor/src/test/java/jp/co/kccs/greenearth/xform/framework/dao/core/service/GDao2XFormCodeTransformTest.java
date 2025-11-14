package jp.co.kccs.greenearth.xform.framework.dao.core.service;

import jp.co.kccs.greenearth.commons.GFrameworkUtils;
import jp.co.kccs.greenearth.commons.utils.GFileUtils;
import jp.co.kccs.greenearth.testutils.dbunit.TestHelper;
import jp.co.kccs.greenearth.xform.code.dao.core.GCrudType;
import jp.co.kccs.greenearth.xform.code.dao.core.service.GDao2XFormCodeTransform;
import jp.co.kccs.greenearth.xform.code.dao.core.service.GDao2XFormSqlResult;
import jp.co.kccs.greenearth.xform.configuration.GDbCommonSetting;
import jp.co.kccs.greenearth.xform.configuration.GTransformSetting;
import jp.co.kccs.greenearth.xform.configuration.GXFormSetting;
import jp.co.kccs.greenearth.xform.configuration.GXFormSettingHolder;
import jp.co.kccs.greenearth.xform.dao.common.*;
import jp.co.kccs.greenearth.xform.old.configuration.GLocaleCommonSetting;
import jp.co.kccs.greenearth.xform.old.configuration.GReservedWordSetting;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * @author KCCS XXXXX
 * @create GEF_NEXT_DATE
 * @since GEF_NEXT_VERSION
 */
public abstract class GDao2XFormCodeTransformTest {

	protected String dbSchema;


	protected GDbCommonSetting dbCommonSetting;

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

	public void cleanInsert(String filePath)  {
		try {
			TestHelper.cleanInsert(dbCommonSetting, filePath);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected abstract Map<Class<? extends GXFormSetting>, GXFormSetting> getSetting();

	protected abstract String getTimestampCommand();

	@Test
	public void testTransformByVeId(){
		cleanInsert("jp/co/kccs/greenearth/xform/code/dao/core/service/GDao2XFormCodeTransformTest/import_TransformByVeId.xml");
		String veId = "E68EF7B747CA42B8ACF08908804DA0DB";
		GDao2XFormCodeTransform transformer = GFrameworkUtils.getComponent(GDao2XFormCodeTransform.class.getName());
		GDao2XFormSqlResult dao2XFormSqlResult = transformer.transformByVeId(veId, jp.co.kccs.greenearth.xform.code.dao.core.GCrudType.SELECT);

		assertEquals(jp.co.kccs.greenearth.xform.code.dao.core.GCrudType.SELECT, dao2XFormSqlResult.getType());
		assertEquals("E68EF7B747CA42B8ACF08908804DA0DB", dao2XFormSqlResult.getVirtualEntity().getObjectID());
		assertEquals("SELECT" + System.lineSeparator() +
				"    MAX(jdbc_tool_test_mainmaster.objectID)," + System.lineSeparator() +
				"    COUNT(jdbc_tool_test_mainmaster.ItemCD)," + System.lineSeparator() +
				"    SUM(jdbc_tool_test_mainmaster.UnitPrice)," + System.lineSeparator() +
				"    AVG(jdbc_tool_test_mainmaster.SellPrice)," + System.lineSeparator() +
				"    MIN(jdbc_tool_test_mainmaster.Flag)," + System.lineSeparator() +
				"    MAX(jdbc_tool_test_mainmaster.PriceFlag)" + System.lineSeparator() +
				"FROM" + System.lineSeparator() +
				"    " + dbSchema + ".jdbc_tool_test_mainmaster jdbc_tool_test_mainmaster" + System.lineSeparator() +
				"GROUP BY" + System.lineSeparator() +
				"    jdbc_tool_test_mainmaster.UnitPrice", dao2XFormSqlResult.getSqlScript());


//
//		String apiCode = "select(\"jdbc_tool_test_mainmaster\", \"jdbc_tool_test_mainmaster\")" + System.lineSeparator() +
//				".fields(col(\"jdbc_tool_test_mainmaster.objectID\", GColumn.FuncType.MAX)," +
//				"col(\"jdbc_tool_test_mainmaster.ItemCD\", GColumn.FuncType.COUNT)," +
//				"col(\"jdbc_tool_test_mainmaster.UnitPrice\", GColumn.FuncType.SUM)," +
//				"col(\"jdbc_tool_test_mainmaster.SellPrice\", GColumn.FuncType.AVG)," +
//				"col(\"jdbc_tool_test_mainmaster.Flag\", GColumn.FuncType.MIN)," +
//				"col(\"jdbc_tool_test_mainmaster.PriceFlag\", GColumn.FuncType.MAX))" + System.lineSeparator() +
//				".groupBy(col(\"jdbc_tool_test_mainmaster.UnitPrice\"))" + System.lineSeparator();
//		assertEquals("List<GRecord> result = " + apiCode + ".findList();", listGVEItem.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, null)));
//		assertEquals("GRecord result = " + apiCode + ".findRecord();", listGVEItem.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, null)));
//		assertEquals("GRecordSet result = " + apiCode + ".findRecordSet();", listGVEItem.getData().get(0).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, null)));

		dao2XFormSqlResult = transformer.transformByVeId(veId, jp.co.kccs.greenearth.xform.code.dao.core.GCrudType.DELETE);

		assertEquals(GCrudType.DELETE, dao2XFormSqlResult.getType());
		assertEquals("E68EF7B747CA42B8ACF08908804DA0DB", dao2XFormSqlResult.getVirtualEntity().getObjectID());
		assertEquals(getDeleteSql_TransformByVeId(), dao2XFormSqlResult.getSqlScript());

//		assertEquals(1, listGVEItem.getData().get(1).getApiCodes().size());
//		String deleteApiCode = "delete(\"jdbc_tool_test_mainmaster\")" + System.lineSeparator();
//		assertEquals("int count = " + deleteApiCode + ".execute();", listGVEItem.getData().get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, null)));
//		assertNull(listGVEItem.getData().get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE)));
//		assertNull(listGVEItem.getData().get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_PK)));
//		assertNull(listGVEItem.getData().get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_UK)));
//		assertNull(listGVEItem.getData().get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE)));
//		assertNull(listGVEItem.getData().get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_PK)));
//		assertNull(listGVEItem.getData().get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_UK)));
//		assertNull(listGVEItem.getData().get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE)));
//		assertNull(listGVEItem.getData().get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_PK)));
//		assertNull(listGVEItem.getData().get(1).getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_UK)));
	}

	protected String getDeleteSql_TransformByVeId() {
		return "DELETE FROM" + System.lineSeparator() + "    " + dbSchema + ".jdbc_tool_test_mainmaster";
	}


	protected String getDeleteSql_TransformByVeCategoryId() {
		return "DELETE FROM" + System.lineSeparator() +
						"    " + dbSchema + ".jdbc_tool_test_mainmaster" + System.lineSeparator() +
						"WHERE" + System.lineSeparator() +
						"    " + dbSchema + ".jdbc_tool_test_mainmaster.UnitPrice IN (100, 101, 102)" + System.lineSeparator() +
						"    AND (" + System.lineSeparator() +
						"        " + dbSchema + ".jdbc_tool_test_mainmaster.objectID LIKE ? ESCAPE '/'" + System.lineSeparator() +
						"        AND " + dbSchema + ".jdbc_tool_test_mainmaster.UnitPrice >= ?" + System.lineSeparator() +
						"    )";
	}
//
//	@Test
//	public void testTransformByVeCategoryId(){
//		cleanInsert("jp/co/kccs/greenearth/xform/code/dao/core/service/GDao2XFormCodeTransformTest/import_TransformByVeCategoryId.xml");
//		String categoryId = "F56264D123E6450DB2AF788A7039CA41";
//		GDao2XFormCodeTransform transformer = GFrameworkUtils.getComponent(GDao2XFormCodeTransform.class.getName());
//
//		GVENode<> categoryIdLoader =
//
//		GVECategory<List<GXFormCodeResult>> listGVECategory = transformer.transformByCategoryId(parameter);
//		List<GVENode<List<GXFormCodeResult>>> children = listGVECategory.getChildren();
//
//		Map<String, GVENode> gveItemMap = children.stream().filter(child->child instanceof GVEItem)
//				.collect(Collectors.toMap(child-> child.getId(), child-> child));
//		// [1]
//		{
//			GVEItemImpl veItem = (GVEItemImpl) gveItemMap.get("F08F656C201141ADBD1B743F144A44A0");
//			ArrayList data = (ArrayList) veItem.getData();
//			assertEquals(2, data.size());
//			GXFormCodeResultImpl xFormCodeResult = (GXFormCodeResultImpl) data.get(0);
//
//			assertEquals(GCrudType.SELECT, xFormCodeResult.getType());
//			assertEquals("仮想表ID: F08F656C201141ADBD1B743F144A44A0 (カテゴリID: F56264D123E6450DB2AF788A7039CA41)", xFormCodeResult.getDescription());
//			assertEquals("SELECT" + System.lineSeparator() +
//				"    jdbc_tool_test_mainmaster.objectID," + System.lineSeparator() +
//				"    jdbc_tool_test_mainmaster.ItemCD," + System.lineSeparator() +
//				"    jdbc_tool_test_mainmaster.UnitPrice," + System.lineSeparator() +
//				"    jdbc_tool_test_mainmaster.RegisteredDT," + System.lineSeparator() +
//				"    jdbc_tool_test_mainmaster.ExclusiveFG" + System.lineSeparator() +
//				"FROM" + System.lineSeparator() +
//				"    " + dbSchema + ".jdbc_tool_test_mainmaster jdbc_tool_test_mainmaster" + System.lineSeparator() +
//				"    INNER JOIN " + dbSchema + ".jdbc_tool_test_unitmaster jdbc_tool_test_unitmaster ON jdbc_tool_test_mainmaster.ItemCD = jdbc_tool_test_unitmaster.Code" + System.lineSeparator() +
//				"WHERE" + System.lineSeparator() +
//				"    jdbc_tool_test_mainmaster.UnitPrice IN (100, 101, 102)" + System.lineSeparator() +
//				"    AND (" + System.lineSeparator() +
//				"        jdbc_tool_test_mainmaster.objectID LIKE ? ESCAPE '/'" + System.lineSeparator() +
//				"        AND jdbc_tool_test_mainmaster.UnitPrice >= ?" + System.lineSeparator() +
//				"    )" + System.lineSeparator() +
//				"ORDER BY" + System.lineSeparator() +
//				"    jdbc_tool_test_mainmaster.RegisteredDT ASC", xFormCodeResult.getSqlScript());
//			String whereParams = "Object[] whereParams = new Object[]{,};" + System.lineSeparator();
//			String apiCode = "select(\"jdbc_tool_test_mainmaster\", \"jdbc_tool_test_mainmaster\")" + System.lineSeparator() +
//					".fields(col(\"jdbc_tool_test_mainmaster.objectID\"),col(\"jdbc_tool_test_mainmaster.ItemCD\")," +
//					"col(\"jdbc_tool_test_mainmaster.UnitPrice\"),col(\"jdbc_tool_test_mainmaster.RegisteredDT\")," +
//					"col(\"jdbc_tool_test_mainmaster.ExclusiveFG\"))" + System.lineSeparator() +
//					".innerJoinFK(\"FK\", \"jdbc_tool_test_unitmaster\")" + System.lineSeparator() +
//					".orderBy(asc(\"jdbc_tool_test_mainmaster.RegisteredDT\"))" + System.lineSeparator() +
//					".where(exp($(\"jdbc_tool_test_mainmaster.UnitPrice IN (100, 101, 102)\")" +
//					".AND($(\"jdbc_tool_test_mainmaster.objectID LIKE $CONCAT('%', $CONCAT(?, '%')) ESCAPE '/'\")" +
//					".AND(\"jdbc_tool_test_mainmaster.UnitPrice >= ?\")), whereParams))" ;
//			assertEquals(whereParams + "List<GRecord> result = " + apiCode + System.lineSeparator() + ".findList();", xFormCodeResult.getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE)));
//			assertEquals(whereParams + "GRecord result = "  + apiCode + System.lineSeparator() + ".findRecord();", xFormCodeResult.getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE)));
//			assertEquals(whereParams + "GRecordSet result = "  + apiCode + System.lineSeparator() + ".findRecordSet();", xFormCodeResult.getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE)));
//		}
//
//		// [2]
//		{
//
//			GVEItemImpl veItem = (GVEItemImpl)  gveItemMap.get("BC37EC6B0EFF411980A0385FFB38D035");
//			ArrayList data = (ArrayList) veItem.getData();
//			assertEquals(4, data.size());
//			{
//				GXFormCodeResultImpl xFormCodeInsertResult = (GXFormCodeResultImpl) data.get(0);
//				assertEquals(GCrudType.INSERT, xFormCodeInsertResult.getType());
//				assertEquals("仮想表ID: BC37EC6B0EFF411980A0385FFB38D035 (カテゴリID: F56264D123E6450DB2AF788A7039CA41)", xFormCodeInsertResult.getDescription());
//				assertEquals("INSERT INTO" + System.lineSeparator() +
//						"    " + dbSchema + ".jdbc_tool_test_mainmaster(objectID, ItemCD, UnitPrice, ExclusiveFG, RegisteredDT)" + System.lineSeparator() +
//						"VALUES" + System.lineSeparator() +
//						"(?, ?, ?, ?, " + getTimestampCommand() + ")", xFormCodeInsertResult.getSqlScript());
//
//				String recordStr = "GRecord record = createRecord();" + System.lineSeparator() +
//					"record.setObject(\"objectID\",);" + System.lineSeparator() +
//					"record.setObject(\"ItemCD\",);" + System.lineSeparator() +
//					"record.setObject(\"UnitPrice\",);" + System.lineSeparator() +
//					"record.setObject(\"ExclusiveFG\",);" + System.lineSeparator() +
//					"record.setObject(\"RegisteredDT\",);" + System.lineSeparator();
//
//				String executeApiStr = "insert(\"jdbc_tool_test_mainmaster\")" + System.lineSeparator() +
//						".values(record)";
//				assertEquals(recordStr + "" + System.lineSeparator() + "int count = " + executeApiStr + System.lineSeparator() + ".execute();", xFormCodeInsertResult.getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, null)));
//
//				StringBuilder recordsStr = new StringBuilder();
//				recordsStr.append("List<GRecord> recordList = new ArrayList<>();" + System.lineSeparator());
//				recordsStr.append(recordStr);
//				recordsStr.append("recordList.add(record);" + System.lineSeparator());
//				String executeListApiStr = "insert(\"jdbc_tool_test_mainmaster\")" + System.lineSeparator() +
//						".values(recordList)";
//				assertEquals(recordsStr + "" + System.lineSeparator() + "int count = "  + executeListApiStr + System.lineSeparator() + ".executeBatch();", xFormCodeInsertResult.getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, null)));
//				assertEquals(recordsStr + "" + System.lineSeparator() + "int count = "  + executeListApiStr + System.lineSeparator() + ".executeList();", xFormCodeInsertResult.getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, null)));
//			}
//			{
//				GXFormCodeResultImpl xFormCodeUpdateResult = (GXFormCodeResultImpl) data.get(1);
//				assertEquals(GCrudType.UPDATE, xFormCodeUpdateResult.getType());
//				assertEquals("仮想表ID: BC37EC6B0EFF411980A0385FFB38D035 (カテゴリID: F56264D123E6450DB2AF788A7039CA41)", xFormCodeUpdateResult.getDescription());
//				assertEquals("UPDATE" + System.lineSeparator() +
//						"    " + dbSchema + ".jdbc_tool_test_mainmaster jdbc_tool_test_mainmaster" + System.lineSeparator() +
//						"SET" + System.lineSeparator() +
//						"    ItemCD = ?," + System.lineSeparator() +
//						"    UnitPrice = ?," + System.lineSeparator() +
//						"    RegisteredDT = ?," + System.lineSeparator() +
//						"    ExclusiveFG = ?" + System.lineSeparator() +
//						"WHERE" + System.lineSeparator() +
//						"    jdbc_tool_test_mainmaster.UnitPrice IN (100, 101, 102)" + System.lineSeparator() +
//						"    AND (" + System.lineSeparator() +
//						"        jdbc_tool_test_mainmaster.objectID LIKE ? ESCAPE '/'" + System.lineSeparator() +
//						"        AND jdbc_tool_test_mainmaster.UnitPrice >= ?" + System.lineSeparator() +
//						"    )", xFormCodeUpdateResult.getSqlScript());
//				String whereParams = "Object[] whereParams = new Object[]{,};";
//				String recordStr = "GRecord record = createRecord();" + System.lineSeparator() +
//					"record.setObject(\"ItemCD\",);" + System.lineSeparator() +
//					"record.setObject(\"UnitPrice\",);" + System.lineSeparator() +
//					"record.setObject(\"RegisteredDT\",);" + System.lineSeparator() +
//					"record.setObject(\"ExclusiveFG\",);" + System.lineSeparator();
//
//				String executeApiStr = "update(\"jdbc_tool_test_mainmaster\")" + System.lineSeparator() +
//						".set(record)" + System.lineSeparator();
//				String executeApiWhereStr = ".where(exp($(\"jdbc_tool_test_mainmaster.UnitPrice IN (100, 101, 102)\").AND($(\"jdbc_tool_test_mainmaster.objectID LIKE $CONCAT('%', $CONCAT(?, '%')) ESCAPE '/'\").AND(\"jdbc_tool_test_mainmaster.UnitPrice >= ?\")), whereParams))";
//				assertEquals(whereParams + "" + System.lineSeparator() + recordStr + "" + System.lineSeparator() + "int count = " + executeApiStr + executeApiWhereStr + System.lineSeparator() + ".execute();", xFormCodeUpdateResult.getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE)));
//				assertEquals(recordStr + "" + System.lineSeparator() + "int count = " + executeApiStr + ".wherePK()" + System.lineSeparator() + ".execute();", xFormCodeUpdateResult.getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_PK)));
//				assertEquals(recordStr + "" + System.lineSeparator() + "int count = " + executeApiStr + ".whereUK(\"MainMasterUniqueKey\")" + System.lineSeparator() + ".execute();", xFormCodeUpdateResult.getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_UK)));
//
//				StringBuilder recordsStr = new StringBuilder();
//				recordsStr.append("List<GRecord> recordList = new ArrayList<>();" + System.lineSeparator());
//				recordsStr.append(recordStr);
//				recordsStr.append("recordList.add(record);" + System.lineSeparator());
//				String executeListApiStr = "update(\"jdbc_tool_test_mainmaster\")" + System.lineSeparator() +
//						".set(recordList)" + System.lineSeparator();
//
//				assertEquals(recordsStr + "" + System.lineSeparator() + "int count = "  + executeListApiStr + ".wherePK()" + System.lineSeparator() + ".executeBatch();", xFormCodeUpdateResult.getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_PK)));
//				assertEquals(recordsStr + "" + System.lineSeparator() + "int count = "  + executeListApiStr + ".whereUK(\"MainMasterUniqueKey\")" + System.lineSeparator() + ".executeBatch();", xFormCodeUpdateResult.getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_UK)));
//				assertEquals(recordsStr + "" + System.lineSeparator() + "int count = "  + executeListApiStr + ".wherePK()" +  System.lineSeparator() + ".executeList();", xFormCodeUpdateResult.getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_PK)));
//				assertEquals(recordsStr + "" + System.lineSeparator() + "int count = "  + executeListApiStr + ".whereUK(\"MainMasterUniqueKey\")" +  System.lineSeparator() + ".executeList();", xFormCodeUpdateResult.getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_UK)));
//			}
//			{
//				GXFormCodeResultImpl xFormCodeSelectResult = (GXFormCodeResultImpl) data.get(2);
//				assertEquals(GCrudType.SELECT, xFormCodeSelectResult.getType());
//				assertEquals("仮想表ID: BC37EC6B0EFF411980A0385FFB38D035 (カテゴリID: F56264D123E6450DB2AF788A7039CA41)", xFormCodeSelectResult.getDescription());
//				assertEquals("SELECT" + System.lineSeparator() +
//						"    jdbc_tool_test_mainmaster.objectID," + System.lineSeparator() +
//						"    jdbc_tool_test_mainmaster.ItemCD," + System.lineSeparator() +
//						"    jdbc_tool_test_mainmaster.UnitPrice," + System.lineSeparator() +
//						"    jdbc_tool_test_mainmaster.RegisteredDT," + System.lineSeparator() +
//						"    jdbc_tool_test_mainmaster.ExclusiveFG" + System.lineSeparator() +
//						"FROM" + System.lineSeparator() +
//						"    " + dbSchema + ".jdbc_tool_test_mainmaster jdbc_tool_test_mainmaster" + System.lineSeparator() +
//						"    INNER JOIN " + dbSchema + ".jdbc_tool_test_unitmaster jdbc_tool_test_unitmaster ON jdbc_tool_test_mainmaster.ItemCD = jdbc_tool_test_unitmaster.Code" + System.lineSeparator() +
//						"WHERE" + System.lineSeparator() +
//						"    jdbc_tool_test_mainmaster.UnitPrice IN (100, 101, 102)" + System.lineSeparator() +
//						"    AND (" + System.lineSeparator() +
//						"        jdbc_tool_test_mainmaster.objectID LIKE ? ESCAPE '/'" + System.lineSeparator() +
//						"        AND jdbc_tool_test_mainmaster.UnitPrice >= ?" + System.lineSeparator() +
//						"    )" + System.lineSeparator() +
//						"ORDER BY" + System.lineSeparator() +
//						"    jdbc_tool_test_mainmaster.RegisteredDT ASC", xFormCodeSelectResult.getSqlScript());
//
//				String whereParams = "Object[] whereParams = new Object[]{,};";
//				String apiCode = "select(\"jdbc_tool_test_mainmaster\", \"jdbc_tool_test_mainmaster\")" + System.lineSeparator() +
//						".fields(col(\"jdbc_tool_test_mainmaster.objectID\")," +
//						"col(\"jdbc_tool_test_mainmaster.ItemCD\")," +
//						"col(\"jdbc_tool_test_mainmaster.UnitPrice\")," +
//						"col(\"jdbc_tool_test_mainmaster.RegisteredDT\")," +
//						"col(\"jdbc_tool_test_mainmaster.ExclusiveFG\"))" + System.lineSeparator() +
//						".innerJoinFK(\"FK\", \"jdbc_tool_test_unitmaster\")" + System.lineSeparator() +
//						".orderBy(asc(\"jdbc_tool_test_mainmaster.RegisteredDT\"))" + System.lineSeparator() ;
//				String apiWhereApi = ".where(exp($(\"jdbc_tool_test_mainmaster.UnitPrice IN (100, 101, 102)\").AND($(\"jdbc_tool_test_mainmaster.objectID LIKE $CONCAT('%', $CONCAT(?, '%')) ESCAPE '/'\").AND(\"jdbc_tool_test_mainmaster.UnitPrice >= ?\")), whereParams))";
//				assertEquals(whereParams + "" + System.lineSeparator() + "List<GRecord> result = " + apiCode + apiWhereApi + System.lineSeparator() + ".findList();", xFormCodeSelectResult.getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE)));
//				assertEquals(whereParams + "" + System.lineSeparator() + "List<GRecord> result = " + apiCode + ".wherePK(whereParams)" + System.lineSeparator() + ".findList();", xFormCodeSelectResult.getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE_PK)));
//				assertEquals(whereParams + "" + System.lineSeparator() + "List<GRecord> result = " + apiCode + ".whereUK(\"MainMasterUniqueKey\", whereParams)" + System.lineSeparator() + ".findList();", xFormCodeSelectResult.getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE_UK)));
//				assertEquals(whereParams + "" + System.lineSeparator() + "GRecord result = "  + apiCode + apiWhereApi + System.lineSeparator() + ".findRecord();", xFormCodeSelectResult.getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE)));
//				assertEquals(whereParams + "" + System.lineSeparator() + "GRecord result = "  + apiCode + ".wherePK(whereParams)"  + System.lineSeparator() + ".findRecord();", xFormCodeSelectResult.getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE_PK)));
//				assertEquals(whereParams + "" + System.lineSeparator() + "GRecord result = "  + apiCode + ".whereUK(\"MainMasterUniqueKey\", whereParams)" + System.lineSeparator() + ".findRecord();", xFormCodeSelectResult.getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE_UK)));
//				assertEquals(whereParams + "" + System.lineSeparator() + "GRecordSet result = "  + apiCode + apiWhereApi + System.lineSeparator() + ".findRecordSet();", xFormCodeSelectResult.getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE)));
//				assertEquals(whereParams + "" + System.lineSeparator() + "GRecordSet result = "  + apiCode + ".wherePK(whereParams)"  + System.lineSeparator() + ".findRecordSet();", xFormCodeSelectResult.getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE_PK)));
//				assertEquals(whereParams + "" + System.lineSeparator() + "GRecordSet result = "  + apiCode + ".whereUK(\"MainMasterUniqueKey\", whereParams)" + System.lineSeparator() + ".findRecordSet();", xFormCodeSelectResult.getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE_UK)));
//			}
//			{
//				GXFormCodeResultImpl xFormCodeDeleteResult = (GXFormCodeResultImpl) data.get(3);
//				assertEquals(GCrudType.DELETE, xFormCodeDeleteResult.getType());
//				assertEquals("仮想表ID: BC37EC6B0EFF411980A0385FFB38D035 (カテゴリID: F56264D123E6450DB2AF788A7039CA41)", xFormCodeDeleteResult.getDescription());
//				assertEquals(getDeleteSql_TransformByVeCategoryId(), xFormCodeDeleteResult.getSqlScript());
//
//				String whereParams = "Object[] whereParams = new Object[]{,};" + System.lineSeparator();
//				String deleteApiStr = "delete(\"jdbc_tool_test_mainmaster\")" + System.lineSeparator();
//				String deleteApiWhereStr = ".where(exp($(\"jdbc_tool_test_mainmaster.UnitPrice IN (100, 101, 102)\").AND($(\"jdbc_tool_test_mainmaster.objectID LIKE $CONCAT('%', $CONCAT(?, '%')) ESCAPE '/'\").AND(\"jdbc_tool_test_mainmaster.UnitPrice >= ?\")), whereParams))" + System.lineSeparator();
//
//				String whereRecord = "GRecord whereParams = createRecord();" + System.lineSeparator() + "whereParams.setObject(\"objectID\",);" + System.lineSeparator();
//				StringBuilder whereRecords = new StringBuilder();
//				whereRecords.append(whereRecord);
//				whereRecords.append("List<GRecord> whereParamsList = new ArrayList<>();" + System.lineSeparator());
//				whereRecords.append("whereParamsList.add(whereParams);" + System.lineSeparator());
//
//				assertEquals(whereParams + "" + System.lineSeparator() + "int count = " + deleteApiStr + deleteApiWhereStr + ".execute();", xFormCodeDeleteResult.getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE)));
//				assertEquals(whereRecord + "" + System.lineSeparator() + "int count = " + deleteApiStr + ".wherePK(whereParams)" + System.lineSeparator() + ".execute();", xFormCodeDeleteResult.getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_PK)));
//				assertEquals(whereRecord + "" + System.lineSeparator() + "int count = " + deleteApiStr + ".whereUK(\"MainMasterUniqueKey\", whereParams)" + System.lineSeparator() + ".execute();", xFormCodeDeleteResult.getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_UK)));
//				assertNull(xFormCodeDeleteResult.getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE)));
//				assertEquals(whereRecords + "" + System.lineSeparator() + "int count = " + deleteApiStr + ".wherePK(whereParamsList)" + System.lineSeparator() + ".executeBatch();", xFormCodeDeleteResult.getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_PK)));
//				assertEquals(whereRecords + "" + System.lineSeparator() + "int count = " + deleteApiStr + ".whereUK(\"MainMasterUniqueKey\", whereParamsList)" + System.lineSeparator() + ".executeBatch();", xFormCodeDeleteResult.getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_UK)));
//				assertNull(xFormCodeDeleteResult.getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE)));
//				assertEquals(whereRecords + "" + System.lineSeparator() + "int count = " + deleteApiStr + ".wherePK(whereParamsList)" + System.lineSeparator() + ".executeList();", xFormCodeDeleteResult.getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_PK)));
//				assertEquals(whereRecords + "" + System.lineSeparator() + "int count = " + deleteApiStr + ".whereUK(\"MainMasterUniqueKey\", whereParamsList)" + System.lineSeparator() + ".executeList();", xFormCodeDeleteResult.getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_UK)));
//			}
//		}
//
//		// [3]
//		{
//			GVECategoryImpl veCategory = (GVECategoryImpl) children.stream().filter(child->child instanceof GVECategory).findFirst().get();
//			List subChildren = veCategory.getChildren();
//			GVEItemImpl veItem = (GVEItemImpl) subChildren.get(0);
//			{
//				GXFormCodeResultImpl result = (GXFormCodeResultImpl) ((ArrayList) veItem.getData()).get(0);
//
//				assertEquals(3, listGVECategory.getChildren().size());
//				assertEquals(GCrudType.SELECT, result.getType());
//				assertEquals("仮想表ID: A4F67FC9EE6249AF9F5DE398C9231974 (カテゴリID: B27393A2779C472C88EEF38FABCFE118)", result.getDescription());
//				assertEquals("SELECT" + System.lineSeparator() +
//						"    jdbc_tool_test_mainmaster.objectID," + System.lineSeparator() +
//						"    jdbc_tool_test_mainmaster.ItemCD," + System.lineSeparator() +
//						"    jdbc_tool_test_mainmaster.UnitPrice," + System.lineSeparator() +
//						"    jdbc_tool_test_mainmaster.RegisteredDT," + System.lineSeparator() +
//						"    jdbc_tool_test_mainmaster.ExclusiveFG" + System.lineSeparator() +
//						"FROM" + System.lineSeparator() +
//						"    " + dbSchema + ".jdbc_tool_test_mainmaster jdbc_tool_test_mainmaster" + System.lineSeparator() +
//						"    INNER JOIN " + dbSchema + ".jdbc_tool_test_unitmaster jdbc_tool_test_unitmaster ON jdbc_tool_test_mainmaster.ItemCD = jdbc_tool_test_unitmaster.Code" + System.lineSeparator() +
//						"WHERE" + System.lineSeparator() +
//						"    jdbc_tool_test_mainmaster.UnitPrice IN (100, 101, 102)" + System.lineSeparator() +
//						"    AND (" + System.lineSeparator() +
//						"        jdbc_tool_test_mainmaster.objectID LIKE ? ESCAPE '/'" + System.lineSeparator() +
//						"        AND jdbc_tool_test_mainmaster.UnitPrice >= ?" + System.lineSeparator() +
//						"    )" + System.lineSeparator() +
//						"ORDER BY" + System.lineSeparator() +
//						"    jdbc_tool_test_mainmaster.RegisteredDT ASC", result.getSqlScript());
//				String whereParams = "Object[] whereParams = new Object[]{,};" + System.lineSeparator();
//				String apiCode = "select(\"jdbc_tool_test_mainmaster\", \"jdbc_tool_test_mainmaster\")" + System.lineSeparator() +
//						".fields(col(\"jdbc_tool_test_mainmaster.objectID\")," +
//						"col(\"jdbc_tool_test_mainmaster.ItemCD\")," +
//						"col(\"jdbc_tool_test_mainmaster.UnitPrice\")," +
//						"col(\"jdbc_tool_test_mainmaster.RegisteredDT\")," +
//						"col(\"jdbc_tool_test_mainmaster.ExclusiveFG\"))" + System.lineSeparator() +
//						".innerJoinFK(\"FK\", \"jdbc_tool_test_unitmaster\")" + System.lineSeparator() +
//						".orderBy(asc(\"jdbc_tool_test_mainmaster.RegisteredDT\"))" + System.lineSeparator();
//				String apiWhereCode = ".where(exp($(\"jdbc_tool_test_mainmaster.UnitPrice IN (100, 101, 102)\").AND($(\"jdbc_tool_test_mainmaster.objectID LIKE $CONCAT('%', $CONCAT(?, '%')) ESCAPE '/'\").AND(\"jdbc_tool_test_mainmaster.UnitPrice >= ?\")), whereParams))";
//
//				assertEquals(whereParams + "List<GRecord> result = " + apiCode + apiWhereCode + System.lineSeparator() + ".findList();", result.getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE)));
//				assertEquals(whereParams + "List<GRecord> result = " + apiCode + ".wherePK(whereParams)" + System.lineSeparator() + ".findList();", result.getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE_PK)));
//				assertEquals(whereParams + "List<GRecord> result = " + apiCode + ".whereUK(\"MainMasterUniqueKey\", whereParams)" + System.lineSeparator() + ".findList();", result.getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE_UK)));
//				assertEquals(whereParams + "GRecord result = " + apiCode + apiWhereCode + System.lineSeparator() + ".findRecord();", result.getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE)));
//				assertEquals(whereParams + "GRecord result = " + apiCode + ".wherePK(whereParams)" + System.lineSeparator() + ".findRecord();", result.getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE_PK)));
//				assertEquals(whereParams + "GRecord result = " + apiCode + ".whereUK(\"MainMasterUniqueKey\", whereParams)" + System.lineSeparator() + ".findRecord();", result.getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE_UK)));
//				assertEquals(whereParams + "GRecordSet result = " + apiCode + apiWhereCode + System.lineSeparator() + ".findRecordSet();", result.getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE)));
//				assertEquals(whereParams + "GRecordSet result = " + apiCode + ".wherePK(whereParams)" + System.lineSeparator() + ".findRecordSet();", result.getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE_PK)));
//				assertEquals(whereParams + "GRecordSet result = " + apiCode + ".whereUK(\"MainMasterUniqueKey\", whereParams)" + System.lineSeparator() + ".findRecordSet();", result.getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE_UK)));
//			}
//			{
//				GXFormCodeResultImpl result = (GXFormCodeResultImpl) ((ArrayList) veItem.getData()).get(1);
//				assertEquals(GCrudType.DELETE, result.getType());
//				assertEquals("仮想表ID: A4F67FC9EE6249AF9F5DE398C9231974 (カテゴリID: B27393A2779C472C88EEF38FABCFE118)", result.getDescription());
//				assertEquals(getDeleteSql_TransformByVeCategoryId_2(),
//						result.getSqlScript());
//
//				assertEquals(7, result.getApiCodes().size());
//				String whereParams = "Object[] whereParams = new Object[]{,};" + System.lineSeparator();
//				String deleteApiStr = "delete(\"jdbc_tool_test_mainmaster\")" + System.lineSeparator();
//				String deleteApiWhereStr = ".where(exp($(\"jdbc_tool_test_mainmaster.UnitPrice IN (100, 101, 102)\").AND($(\"jdbc_tool_test_mainmaster.objectID LIKE $CONCAT('%', $CONCAT(?, '%')) ESCAPE '/'\").AND(\"jdbc_tool_test_mainmaster.UnitPrice >= ?\")), whereParams))" + System.lineSeparator();
//
//				String whereRecord = "GRecord whereParams = createRecord();" + System.lineSeparator() + "whereParams.setObject(\"objectID\",);" + System.lineSeparator();
//				StringBuilder whereRecords = new StringBuilder();
//				whereRecords.append(whereRecord);
//				whereRecords.append("List<GRecord> whereParamsList = new ArrayList<>();" + System.lineSeparator());
//				whereRecords.append("whereParamsList.add(whereParams);" + System.lineSeparator());
//
//				assertEquals(whereParams + "" + System.lineSeparator() + "int count = " + deleteApiStr + deleteApiWhereStr + ".execute();", result.getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE)));
//				assertEquals(whereRecord + "" + System.lineSeparator() + "int count = " + deleteApiStr + ".wherePK(whereParams)" + System.lineSeparator() + ".execute();", result.getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_PK)));
//				assertEquals(whereRecord + "" + System.lineSeparator() + "int count = " + deleteApiStr + ".whereUK(\"MainMasterUniqueKey\", whereParams)" + System.lineSeparator() + ".execute();", result.getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE, GWhereApi.WHERE_UK)));
//				assertNull(result.getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE)));
//				assertEquals(whereRecords + "" + System.lineSeparator() + "int count = " + deleteApiStr + ".wherePK(whereParamsList)" + System.lineSeparator() + ".executeBatch();", result.getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_PK)));
//				assertEquals(whereRecords + "" + System.lineSeparator() + "int count = " + deleteApiStr + ".whereUK(\"MainMasterUniqueKey\", whereParamsList)" + System.lineSeparator() + ".executeBatch();", result.getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_BATCH, GWhereApi.WHERE_UK)));
//				assertNull(result.getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE)));
//				assertEquals(whereRecords + "" + System.lineSeparator() + "int count = " + deleteApiStr + ".wherePK(whereParamsList)" + System.lineSeparator() + ".executeList();", result.getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_PK)));
//				assertEquals(whereRecords + "" + System.lineSeparator() + "int count = " + deleteApiStr + ".whereUK(\"MainMasterUniqueKey\", whereParamsList)" + System.lineSeparator() + ".executeList();", result.getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.EXECUTE_LIST, GWhereApi.WHERE_UK)));
//
//			}
//		}
//	}
	protected String getDeleteSql_TransformByVeCategoryId_2() {
		return "DELETE FROM" + System.lineSeparator() +
						"    " + dbSchema + ".jdbc_tool_test_mainmaster" + System.lineSeparator() +
						"WHERE" + System.lineSeparator() +
						"    " + dbSchema + ".jdbc_tool_test_mainmaster.UnitPrice IN (100, 101, 102)" + System.lineSeparator() +
						"    AND (" + System.lineSeparator() +
						"        " + dbSchema + ".jdbc_tool_test_mainmaster.objectID LIKE ? ESCAPE '/'" + System.lineSeparator() +
						"        AND " + dbSchema + ".jdbc_tool_test_mainmaster.UnitPrice >= ?" + System.lineSeparator() +
						"    )";
	}
}
