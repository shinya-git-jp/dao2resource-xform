package jp.co.kccs.greenearth.xform.dao.entity.mapper;

import jp.co.kccs.greenearth.framework.jdbc.entity.repository.GEntity;
import jp.co.kccs.greenearth.xform.dao.dto.GDao2VirtualEntity;
import jp.co.kccs.greenearth.xform.jdbc.common.GColumnType;
import jp.co.kccs.greenearth.xform.jdbc.common.GXFormEntity;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * @author KCCS XXXXX
 * @create GEF_NEXT_DATE
 * @since GEF_NEXT_VERSION
 */
public  class GXFormEntityMapperTest {

	/**
	 * [1] {@link GDao2VirtualEntity.Entity}のフィールドが初期の状態の場合、{@link GXFormEntityMapper}で変換されたら、{@link GXFormEntity}のフィールドが初期の状態になること。<br>
	 * [2] {@link GDao2VirtualEntity.Entity}のフィールドが空の状態じゃない場合、{@link GXFormEntityMapper}で変換されたら、{@link GXFormEntity}のフィールドが変換前の値と一致すること。<br>
	 */
	@Test
	public void testConvert() {
		{

			GDao2VirtualEntity.Entity entity = new GDao2VirtualEntity.Entity();
			GXFormEntityMapper mapper = new GXFormEntityMapper();

			GXFormEntity gxFormEntity = mapper.map(entity);
			Assert.assertNull(gxFormEntity.getName());
			Assert.assertNull(gxFormEntity.getPhyName());
			Assert.assertNull(gxFormEntity.getDatabaseName());
			Assert.assertEquals(0, gxFormEntity.getColumns().size());
			Assert.assertEquals(0, gxFormEntity.getPrimaryKey().size());
			Assert.assertEquals(0, gxFormEntity.getUniqueKeys().size());
			Assert.assertEquals(0, gxFormEntity.getForeignKeys().size());

			GEntity convertedEntity = gxFormEntity.toEntity();
			Assert.assertNull(convertedEntity.getName());
			Assert.assertNull(convertedEntity.getPhyName());
			Assert.assertNull(convertedEntity.getDatabase().getName());
			Assert.assertEquals(0, convertedEntity.getColumns().size());
			Assert.assertEquals(0, convertedEntity.getPrimaryKey().getReferenceColumns().size());
			Assert.assertEquals(0, convertedEntity.getUniqueKeys().size());
			Assert.assertEquals(0, convertedEntity.getForeignKeys().size());
		}
		{

			GXFormEntityMapper mapper = new GXFormEntityMapper();
			GXFormEntity gxFormEntity = mapper.map(getEntity());
			assertEquals("testDb", gxFormEntity.getDatabaseName());
			assertEquals("testEntity", gxFormEntity.getPhyName());
			assertEquals("testEntity", gxFormEntity.getName());
			assertEquals(2, gxFormEntity.getColumns().size());
			assertEquals(1, gxFormEntity.getUniqueKeys().size());
			assertEquals(1, gxFormEntity.getForeignKeys().size());

			assertEquals("testCol", gxFormEntity.getColumns().get(0).getName());
			assertEquals("testCol", gxFormEntity.getColumns().get(0).getPhyName());
			assertEquals(10, gxFormEntity.getColumns().get(0).getSize());
			assertEquals(GColumnType.VARCHAR, gxFormEntity.getColumns().get(0).getType());

			assertEquals("testCol2", gxFormEntity.getColumns().get(1).getName());
			assertEquals("testCol2", gxFormEntity.getColumns().get(1).getPhyName());
			assertEquals(255, gxFormEntity.getColumns().get(1).getSize());
			assertEquals(GColumnType.VARCHAR, gxFormEntity.getColumns().get(1).getType());

			assertTrue(gxFormEntity.getUniqueKeys().containsKey("uk1"));
			assertEquals("testCol", gxFormEntity.getUniqueKeys().get("uk1").get(0).getName());

			assertEquals("testCol2", gxFormEntity.getUniqueKeys().get("uk1").get(1).getName());

			assertEquals("fk1", gxFormEntity.getForeignKeys().get("fk1").getName());
			assertEquals("refEntity", gxFormEntity.getForeignKeys().get("fk1").getRefEntity());
			assertEquals("testEntity", gxFormEntity.getForeignKeys().get("fk1").getEntity().getName());
			assertEquals(1, gxFormEntity.getForeignKeys().get("fk1").getColumns().size());
			assertEquals("testCol", gxFormEntity.getForeignKeys().get("fk1").getColumns().get(0).getSrcColumn());
			assertEquals("test", gxFormEntity.getForeignKeys().get("fk1").getColumns().get(0).getConstValue());

		}
	}

	public static GDao2VirtualEntity.Entity getEntity() {
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

		GDao2VirtualEntity.Entity entity = new GDao2VirtualEntity.Entity();
		entity.setName("testEntity");
		entity.setDatabaseName("testDb");
		entity.setPhyName("testPhy");
		entity.setColumns(List.of(column, column2));
		entity.setUniqueKeys(List.of(uniqueKey));
		entity.setForeignKeys(List.of(foreignKey));

		return entity;
	}
}
