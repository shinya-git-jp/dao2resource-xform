package jp.co.kccs.greenearth.xform.dao.dto;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class GDao2VirtualEntityTest {

	/**
	 * 設定された値が正しく取得できること。<br>
	 */
	@Test
	public void testGetterSetter() {
		{
			GDao2VirtualEntity virtualEntity = getVirtualEntity();
			assertEquals("aaaa-aaaa-aaaa-aaaa-aaaa", virtualEntity.getVeId());
			assertEquals("bbbb-bbbb-bbbb-bbbb-bbbb", virtualEntity.getCategoryId());
			assertEquals("categoryTest", virtualEntity.getCategoryName());
			assertEquals("1", virtualEntity.getVeType());
			assertEquals(2, virtualEntity.getColumns().size());

			assertEquals("testDb", virtualEntity.getRefEntity().getDatabaseName());
			assertEquals("testPhy", virtualEntity.getRefEntity().getPhyName());
			assertEquals("testEntity", virtualEntity.getRefEntity().getName());
			assertEquals(2, virtualEntity.getRefEntity().getColumns().size());
			assertEquals(1, virtualEntity.getRefEntity().getUniqueKeys().size());
			assertEquals(1, virtualEntity.getRefEntity().getForeignKeys().size());

			assertEquals("testVColumn", virtualEntity.getColumns().get(0).getName());
			assertEquals("testEntity.testCol", virtualEntity.getColumns().get(0).getRefColumn());
			assertEquals("testVColumn", virtualEntity.getColumns().get(0).getDisplayName());

			assertEquals("testVColumn2", virtualEntity.getColumns().get(1).getName());
			assertEquals("testEntity.testCol2", virtualEntity.getColumns().get(1).getRefColumn());
			assertEquals("testVColumn2", virtualEntity.getColumns().get(1).getDisplayName());

			assertEquals("testCol", virtualEntity.getRefEntity().getColumns().get(0).getName());
			assertEquals("testCol", virtualEntity.getRefEntity().getColumns().get(0).getPhyName());
			assertTrue(virtualEntity.getRefEntity().getColumns().get(0).isPrimaryKey());
			assertEquals(10, virtualEntity.getRefEntity().getColumns().get(0).getSize());
			assertTrue(virtualEntity.getRefEntity().getColumns().get(0).isNotNull());
			assertEquals("STRING", virtualEntity.getRefEntity().getColumns().get(0).getDataType());

			assertEquals("testCol2", virtualEntity.getRefEntity().getColumns().get(1).getName());
			assertEquals("testCol2", virtualEntity.getRefEntity().getColumns().get(1).getPhyName());
			assertFalse(virtualEntity.getRefEntity().getColumns().get(1).isPrimaryKey());
			assertEquals(255, virtualEntity.getRefEntity().getColumns().get(1).getSize());
			assertTrue(virtualEntity.getRefEntity().getColumns().get(1).isNotNull());
			assertEquals("STRING", virtualEntity.getRefEntity().getColumns().get(1).getDataType());

			assertEquals("uk1", virtualEntity.getRefEntity().getUniqueKeys().get(0).getName());
			assertEquals("testCol", virtualEntity.getRefEntity().getUniqueKeys().get(0).getUniqueKeyColumns().get(0).getName());
			assertEquals("testCol", virtualEntity.getRefEntity().getUniqueKeys().get(0).getUniqueKeyColumns().get(0).getPhyName());
			assertTrue(virtualEntity.getRefEntity().getUniqueKeys().get(0).getUniqueKeyColumns().get(0).isPrimaryKey());
			assertEquals(10, virtualEntity.getRefEntity().getUniqueKeys().get(0).getUniqueKeyColumns().get(0).getSize());
			assertTrue(virtualEntity.getRefEntity().getUniqueKeys().get(0).getUniqueKeyColumns().get(0).isNotNull());
			assertEquals("STRING", virtualEntity.getRefEntity().getUniqueKeys().get(0).getUniqueKeyColumns().get(0).getDataType());

			assertEquals("testCol2", virtualEntity.getRefEntity().getUniqueKeys().get(0).getUniqueKeyColumns().get(1).getName());
			assertEquals("testCol2", virtualEntity.getRefEntity().getUniqueKeys().get(0).getUniqueKeyColumns().get(1).getPhyName());
			assertFalse(virtualEntity.getRefEntity().getUniqueKeys().get(0).getUniqueKeyColumns().get(1).isPrimaryKey());
			assertEquals(255, virtualEntity.getRefEntity().getUniqueKeys().get(0).getUniqueKeyColumns().get(1).getSize());
			assertTrue(virtualEntity.getRefEntity().getUniqueKeys().get(0).getUniqueKeyColumns().get(1).isNotNull());
			assertEquals("STRING", virtualEntity.getRefEntity().getUniqueKeys().get(0).getUniqueKeyColumns().get(1).getDataType());

			assertEquals("refEntity", virtualEntity.getRefEntity().getForeignKeys().get(0).getReferenceEntity().getName());
			assertEquals("phyRefEntity", virtualEntity.getRefEntity().getForeignKeys().get(0).getReferenceEntity().getPhyName());
			assertEquals(1, virtualEntity.getRefEntity().getForeignKeys().get(0).getReferenceEntity().getColumns().size());
			assertEquals("refCol", virtualEntity.getRefEntity().getForeignKeys().get(0).getReferenceEntity().getColumns().get(0).getName());
			assertEquals("phyRefCol", virtualEntity.getRefEntity().getForeignKeys().get(0).getReferenceEntity().getColumns().get(0).getPhyName());
			assertEquals("STRING", virtualEntity.getRefEntity().getForeignKeys().get(0).getReferenceEntity().getColumns().get(0).getDataType());
			assertEquals(10, virtualEntity.getRefEntity().getForeignKeys().get(0).getReferenceEntity().getColumns().get(0).getSize());
			assertTrue(virtualEntity.getRefEntity().getForeignKeys().get(0).getReferenceEntity().getColumns().get(0).isNotNull());
			assertTrue(virtualEntity.getRefEntity().getForeignKeys().get(0).getReferenceEntity().getColumns().get(0).isPrimaryKey());

			assertEquals("testCol", virtualEntity.getRefEntity().getForeignKeys().get(0).getForeignKeyColumns().get(0).getSourceColumn());
			assertEquals("refCol", virtualEntity.getRefEntity().getForeignKeys().get(0).getForeignKeyColumns().get(0).getReferenceColumn());
			assertEquals("test", virtualEntity.getRefEntity().getForeignKeys().get(0).getForeignKeyColumns().get(0).getConstValue());
		}
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
}
