//package jp.co.kccs.greenearth.xform.dao.code.cmd;
//
//import jp.co.kccs.greenearth.commons.GFrameworkUtils;
//import jp.co.kccs.greenearth.framework.dao.db.GDBVirtualEntity;
//import jp.co.kccs.greenearth.xform.code.dao.core.sql.GVEMetadata2SqlExtractor;
//import jp.co.kccs.greenearth.xform.code.jdbc.core.GCompoundApiImpl;
//import jp.co.kccs.greenearth.xform.code.jdbc.core.GLikeType;
//import jp.co.kccs.greenearth.xform.code.jdbc.core.GTerminatorApi;
//import jp.co.kccs.greenearth.xform.code.jdbc.core.GTrimType;
//import jp.co.kccs.greenearth.xform.code.jdbc.core.GWhereApi;
//import jp.co.kccs.greenearth.xform.code.jdbc.core.service.GSqlDescriptor;
//import jp.co.kccs.greenearth.xform.code.jdbc.core.service.GXFormScript;
//import jp.co.kccs.greenearth.xform.code.jdbc.core.service.GXFormScriptColumn;
//import jp.co.kccs.greenearth.xform.dao.common.GVEItem;
//import jp.co.kccs.greenearth.xform.jdbc.common.GCrudType;
//import jp.co.kccs.greenearth.xform.jdbc.common.GXFormEntity;
//import org.junit.Test;
//
//import java.util.ArrayList;
//import java.util.Comparator;
//import java.util.List;
//import java.util.stream.Collectors;
//
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertTrue;
//
///**
// * @author KCCS XXXXX
// * @create GEF_NEXT_DATE
// * @since GEF_NEXT_VERSION
// */
//public class GDao2XFormScriptConverterDB2Test extends GDao2XFormScriptConverterTest {
//
//	@Override
//	protected String getSettingsFilePath() {
//		return "inputFile/settings_db2.yaml";
//	}
//
//	@Override
//	protected String getTimestampCommand() {
//		return "CURRENT TIMESTAMP";
//	}
//
//	// [1]
//	@Test
//	public void testConvert_WherePartsTrimOptionLike() {
//
//		cleanInsert("jp/co/kccs/greenearth/xform/code/dao/core/metadata/GDao2XFormScriptConverterTest/import_trim_option_like.xml");
//		String veId = "940E79601B9143D687A8500F9915F7D8";
//		virtualEntity = (GVEItem<GDBVirtualEntity>) veMetadataLoader.create(veId);
//		GVEMetadata2SqlExtractor sqlExtractor = GFrameworkUtils.getComponent(GVEMetadata2SqlExtractor.class);
//		GSqlDescriptor sqlDescriptor_Select = sqlExtractor.extract(virtualEntity.getData(), GCrudType.SELECT);
//		GDao2XFormScriptConverter converter = GFrameworkUtils.getComponent(GDao2XFormScriptConverter.class);
//
//		GXFormScript formScript = converter.convert(sqlDescriptor_Select, virtualEntity.getData());
//
//		assertEquals("仮想表ID: 940E79601B9143D687A8500F9915F7D8", formScript.getDescription());
//		assertEquals(GCrudType.SELECT, formScript.getCrudType());
//		assertEquals(9, formScript.getApiTypes().size());
//		assertTrue(formScript.getApiTypes().contains(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE)));
//		assertTrue(formScript.getApiTypes().contains(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE_PK)));
//		assertTrue(formScript.getApiTypes().contains(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE_UK)));
//		assertTrue(formScript.getApiTypes().contains(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE)));
//		assertTrue(formScript.getApiTypes().contains(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE_PK)));
//		assertTrue(formScript.getApiTypes().contains(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE_UK)));
//		assertTrue(formScript.getApiTypes().contains(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE)));
//		assertTrue(formScript.getApiTypes().contains(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE_PK)));
//		assertTrue(formScript.getApiTypes().contains(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE_UK)));
//
//		assertTrue(assertSqlWithJoin("SELECT jdbc_tool_test_pk_uk_mainmaster.objectID," +
//				"jdbc_tool_test_pk_uk_mainmaster.ItemCD," +
//				"jdbc_tool_test_pk_uk_mainmaster.ItemNA," +
//				"jdbc_tool_test_pk_uk_mainmaster.UnitPrice," +
//				"jdbc_tool_test_pk_uk_mainmaster.SellPrice," +
//				"jdbc_tool_test_pk_uk_mainmaster.Flag," +
//				"jdbc_tool_test_pk_uk_mainmaster.PriceFlag," +
//				"jdbc_tool_test_pk_uk_mainmaster.ExclusiveFG," +
//				"jdbc_tool_test_pk_uk_mainmaster.RegisteredDT," +
//				"jdbc_tool_test_pk_uk_mainmaster.UpdatedDT," +
//				"jdbc_tool_test_mainmaster.objectID," +
//				"jdbc_tool_test_unitmaster.Code," +
//				"jdbc_tool_test_ordermaster.SlipNO," +
//				"jdbc_tool_test_mainmaster.SellPrice " +
//				"FROM " + dbSchema + ".jdbc_tool_test_pk_uk_mainmaster jdbc_tool_test_pk_uk_mainmaster " +
//				"${join}" +
//				"WHERE ( jdbc_tool_test_pk_uk_mainmaster.objectID=? " +
//				"AND jdbc_tool_test_unitmaster.Code=? " +
//				"AND jdbc_tool_test_mainmaster.objectID=? " +
//				"AND jdbc_tool_test_ordermaster.SlipNO LIKE ? ESCAPE '/' " +
//				"AND jdbc_tool_test_pk_uk_mainmaster.ItemCD LIKE ? ESCAPE '/' " +
//				"AND jdbc_tool_test_pk_uk_mainmaster.ItemNA LIKE ? ESCAPE '/' )",
//				List.of("LEFT OUTER JOIN " + dbSchema + ".jdbc_tool_test_unitmaster jdbc_tool_test_unitmaster ON jdbc_tool_test_pk_uk_mainmaster.objectID=jdbc_tool_test_unitmaster.Code ",
//						"INNER JOIN " + dbSchema + ".jdbc_tool_test_mainmaster jdbc_tool_test_mainmaster ON jdbc_tool_test_pk_uk_mainmaster.objectID=jdbc_tool_test_mainmaster.objectID ",
//						"RIGHT OUTER JOIN " + dbSchema + ".jdbc_tool_test_ordermaster jdbc_tool_test_ordermaster ON jdbc_tool_test_pk_uk_mainmaster.ItemCD=jdbc_tool_test_ordermaster.objectID "
//				), formScript.getScript()));
//
//		List<GXFormScriptColumn> trimWhereColumns = formScript.getWhereColumns()
//				.stream().filter(column->column.getTrimValue() != null).collect(Collectors.toList())
//				.stream().sorted(Comparator.comparing(GXFormScriptColumn::getName)).collect(Collectors.toList());
//		assertEquals(2, trimWhereColumns.size());
//		assertEquals("Code", trimWhereColumns.get(0).getName());
//		assertEquals(GTrimType.BOTH, trimWhereColumns.get(0).getTrimValue());
//		assertEquals("objectID", trimWhereColumns.get(1).getName());
//		assertEquals(GTrimType.BOTH, trimWhereColumns.get(1).getTrimValue());
//
//		// TODO
//		List<GXFormScriptColumn> optionWhereColumns = formScript.getWhereColumns()
//				.stream().filter(column->column.isOptional()).collect(Collectors.toList())
//				.stream().sorted(Comparator.comparing(GXFormScriptColumn::getName)).collect(Collectors.toList());
//		assertEquals(2, optionWhereColumns.size());
//		assertEquals("objectID", optionWhereColumns.get(0).getName());
//		assertEquals(true, optionWhereColumns.get(0).isOptional());
//		assertEquals("objectID", optionWhereColumns.get(1).getName());
//		assertEquals(true, optionWhereColumns.get(1).isOptional());
//
//		List<GXFormScriptColumn> likeWhereColumns = formScript.getWhereColumns()
//				.stream().filter(column->column.getLikeType() != null).collect(Collectors.toList())
//				.stream().sorted(Comparator.comparing(GXFormScriptColumn::getName)).collect(Collectors.toList());
//		assertEquals(3, likeWhereColumns.size());
//		assertEquals("ItemCD", likeWhereColumns.get(0).getName());
//		assertEquals(GLikeType.LIKE_PART, likeWhereColumns.get(0).getLikeType());
//		assertEquals("ItemNA", likeWhereColumns.get(1).getName());
//		assertEquals(GLikeType.LIKE_SUFFIX, likeWhereColumns.get(1).getLikeType());
//		assertEquals("SlipNO", likeWhereColumns.get(2).getName());
//		assertEquals(GLikeType.LIKE_PREFIX, likeWhereColumns.get(2).getLikeType());
//
//	}
//
//	@Test
//	public void testConvert_ForeignKey() {
//
//		cleanInsert("jp/co/kccs/greenearth/xform/code/dao/core/metadata/GDao2XFormScriptConverterTest/import_foreignkey.xml");
//		String veId = "E44ADEF7B30549449EDF193ECFB66631";
//		virtualEntity = (GVEItem<GDBVirtualEntity>) veMetadataLoader.create(veId);
//		GVEMetadata2SqlExtractor sqlExtractor = GFrameworkUtils.getComponent(GVEMetadata2SqlExtractor.class);
//		GSqlDescriptor sqlDescriptor_Select = sqlExtractor.extract(virtualEntity.getData(), GCrudType.SELECT);
//		GDao2XFormScriptConverter converter = GFrameworkUtils.getComponent(GDao2XFormScriptConverter.class);
//
//		GXFormScript formScript = converter.convert(sqlDescriptor_Select, virtualEntity.getData());
//		assertEquals(GCrudType.SELECT, formScript.getCrudType());
//		assertEquals(9, formScript.getApiTypes().size());
//		assertTrue(formScript.getApiTypes().contains(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE)));
//		assertTrue(formScript.getApiTypes().contains(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE_PK)));
//		assertTrue(formScript.getApiTypes().contains(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE_UK)));
//		assertTrue(formScript.getApiTypes().contains(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE)));
//		assertTrue(formScript.getApiTypes().contains(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE_PK)));
//		assertTrue(formScript.getApiTypes().contains(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE_UK)));
//		assertTrue(formScript.getApiTypes().contains(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE)));
//		assertTrue(formScript.getApiTypes().contains(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE_PK)));
//		assertTrue(formScript.getApiTypes().contains(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE_UK)));
//		assertTrue(assertSqlWithJoin("SELECT jdbc_tool_test_pk_uk_mainmaster.objectID," +
//				"jdbc_tool_test_pk_uk_mainmaster.ItemCD," +
//				"jdbc_tool_test_pk_uk_mainmaster.ItemNA," +
//				"jdbc_tool_test_pk_uk_mainmaster.UnitPrice," +
//				"jdbc_tool_test_pk_uk_mainmaster.SellPrice," +
//				"jdbc_tool_test_pk_uk_mainmaster.Flag," +
//				"jdbc_tool_test_pk_uk_mainmaster.PriceFlag," +
//				"jdbc_tool_test_pk_uk_mainmaster.ExclusiveFG," +
//				"jdbc_tool_test_pk_uk_mainmaster.RegisteredDT," +
//				"jdbc_tool_test_pk_uk_mainmaster.UpdatedDT " +
//				"FROM " + dbSchema + ".jdbc_tool_test_pk_uk_mainmaster jdbc_tool_test_pk_uk_mainmaster " +
//				"${join}" +
//				"WHERE jdbc_tool_test_pk_uk_mainmaster.objectID=?",
//				List.of("LEFT OUTER JOIN " + dbSchema + ".jdbc_tool_test_unitmaster jdbc_tool_test_unitmaster ON jdbc_tool_test_pk_uk_mainmaster.objectID=jdbc_tool_test_unitmaster.Code ",
//						"INNER JOIN " + dbSchema + ".jdbc_tool_test_mainmaster jdbc_tool_test_mainmaster ON jdbc_tool_test_pk_uk_mainmaster.objectID=jdbc_tool_test_mainmaster.objectID ",
//						"RIGHT OUTER JOIN " + dbSchema + ".jdbc_tool_test_ordermaster jdbc_tool_test_ordermaster ON jdbc_tool_test_pk_uk_mainmaster.ItemCD=jdbc_tool_test_ordermaster.objectID "
//				), formScript.getScript()));
//		assertEquals(1, formScript.getEntities().size());
//		GXFormEntity gxFormEntity = formScript.getEntities().get("jdbc_tool_test_pk_uk_mainmaster");
//		assertEquals(3, gxFormEntity.getForeignKeys().size());
//		assertEquals("ItemCD", gxFormEntity.getForeignKeys().get("FK_OrderMaster").getColumns().get(0).getSrcColumn());
//		assertEquals("objectID", gxFormEntity.getForeignKeys().get("FK_OrderMaster").getColumns().get(0).getRefColumn());
//		assertEquals("objectID", gxFormEntity.getForeignKeys().get("FK_MainMaster").getColumns().get(0).getSrcColumn());
//		assertEquals("objectID", gxFormEntity.getForeignKeys().get("FK_MainMaster").getColumns().get(0).getRefColumn());
//		assertEquals("objectID", gxFormEntity.getForeignKeys().get("FK_UnitMaster").getColumns().get(0).getSrcColumn());
//		assertEquals("Code", gxFormEntity.getForeignKeys().get("FK_UnitMaster").getColumns().get(0).getRefColumn());
//	}
//
//	protected String getDeleteSql_Convert_CUD(){
//		return "DELETE FROM " + dbSchema + ".jdbc_tool_test_pk_uk_mainmaster jdbc_tool_test_pk_uk_mainmaster " +
//				"WHERE ( jdbc_tool_test_pk_uk_mainmaster.ItemCD=? " +
//				"AND jdbc_tool_test_pk_uk_mainmaster.ItemNA=? " +
//				"AND jdbc_tool_test_pk_uk_mainmaster.objectID=? )";
//	}
//
//	private boolean assertSqlWithJoin(String sql, List<String> joins, String target) {
//		List<String> sqlPatterns = getSqlPatterns(sql, joins);
//		for (String sqlPattern : sqlPatterns) {
//			if (sqlPattern.equals(target)) {
//				return true;
//			}
//		}
//		return false;
//	}
//
//	private List<String> getSqlPatterns(String sql, List<String> joins) {
//		List<String> joinPermutations = getJoinPermutations(joins);
//		List<String> sqlPatterns = new ArrayList<>();
//		for (String join : joinPermutations) {
//			sqlPatterns.add(sql.replace("${join}", join));
//		}
//		return sqlPatterns;
//	}
//
//	private List<String> getJoinPermutations(List<String> joinList) {
//		List<List<String>> joinPermutationsResult = getPermutations(joinList);
//		List<String> joinPermutations = new ArrayList<>();
//		for (List<String> joins : joinPermutationsResult) {
//			String result = "";
//			for (String join : joins) {
//				result += join;
//			}
//			joinPermutations.add(result);
//		}
//		return joinPermutations;
//	}
//
//	public <T> List<List<T>> getPermutations(List<T> list) {
//		List<List<T>> result = new ArrayList<>();
//		if (list.size() == 0) {
//			result.add(new ArrayList<>());
//			return result;
//		}
//		T firstElement = list.get(0);
//		List<T> remainingElements = list.subList(1, list.size());
//		List<List<T>> permutationsOfRest = getPermutations(remainingElements);
//
//		for (List<T> perm : permutationsOfRest) {
//			for (int i = 0; i <= perm.size(); i++) {
//				List<T> permCopy = new ArrayList<>(perm);
//				permCopy.add(i, firstElement);
//				result.add(permCopy);
//			}
//		}
//		return result;
//	}
//}
