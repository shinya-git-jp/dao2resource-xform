package jp.co.kccs.greenearth.xform.code.dao.core;

import jp.co.kccs.greenearth.xform.code.jdbc.core.export.GExporterForm;
import jp.co.kccs.greenearth.xform.code.jdbc.core.export.GExporterFormImpl;
import jp.co.kccs.greenearth.xform.code.jdbc.core.service.GXFormCodeResult;
import jp.co.kccs.greenearth.xform.code.jdbc.core.service.GXFormCodeTransformService;
import jp.co.kccs.greenearth.xform.code.jdbc.core.service.GXFormCodeTransformServiceImpl;
import jp.co.kccs.greenearth.xform.code.jdbc.core.service.GXFormScript;
import jp.co.kccs.greenearth.xform.dao.dto.GDao2VirtualEntity;
import jp.co.kccs.greenearth.xform.jdbc.common.GCrudType;

import java.util.List;

public class GUtilTest {

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
	public static List<GXFormCodeResult> getXFormCodeResult() {
		GXFormScript script = GXFormScript.getBuilder()
				.script("select * from test;")
				.description("test")
				.crudType(GCrudType.SELECT)
				.build();
		GXFormCodeTransformService transformer = new GXFormCodeTransformServiceImpl();
		GDao2JdbcXFormCodeResult result = GDao2JdbcXFormCodeResult.from(transformer.transform(script), getVirtualEntity());

		GXFormScript script2 = GXFormScript.getBuilder()
				.script("select * from test2 where col1 = ?")
				.description("test2")
				.crudType(GCrudType.SELECT)
				.build();
		GDao2JdbcXFormCodeResult result2 = GDao2JdbcXFormCodeResult.from(transformer.transform(script2), getVirtualEntity());

		GXFormScript script3 = GXFormScript.getBuilder()
				.script("delete * from test2 where col1 = ?")
				.description("test2")
				.crudType(GCrudType.DELETE)
				.build();
		GDao2JdbcXFormCodeResult result3 = GDao2JdbcXFormCodeResult.from(transformer.transform(script3), getVirtualEntity());

		GXFormScript script4 = GXFormScript.getBuilder()
				.script("update from test2 set col1=? where col2=?")
				.description("test2")
				.crudType(GCrudType.UPDATE)
				.build();
		GDao2JdbcXFormCodeResult result4 = GDao2JdbcXFormCodeResult.from(transformer.transform(script4), getVirtualEntity());

		GXFormScript script5 = GXFormScript.getBuilder()
				.script("insert into test2 values(?,?)")
				.description("test2")
				.crudType(GCrudType.INSERT)
				.build();
		GDao2JdbcXFormCodeResult result5 = GDao2JdbcXFormCodeResult.from(transformer.transform(script5), getVirtualEntity());


		return List.of(result, result2, result3, result4, result5);
	}

	public static GExporterForm<List<GXFormCodeResult>> getResult(List<GXFormCodeResult> result) {
		GExporterForm<List<GXFormCodeResult>> exporterForm = new GExporterFormImpl();
		exporterForm.setData(result);
		exporterForm.setMediaType("html");
		return exporterForm;
	}
}
