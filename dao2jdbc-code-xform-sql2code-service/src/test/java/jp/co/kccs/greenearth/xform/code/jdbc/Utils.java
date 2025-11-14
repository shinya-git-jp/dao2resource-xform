package jp.co.kccs.greenearth.xform.code.jdbc;

import jp.co.kccs.greenearth.xform.dao.dto.GDao2VirtualEntity;

import java.util.List;

public class Utils {

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
