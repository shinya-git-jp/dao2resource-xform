package jp.co.kccs.greenearth.xform.code.jdbc.service.resource;

import jp.co.kccs.greenearth.xform.code.dao.core.GCrudType;
import jp.co.kccs.greenearth.xform.dao.dto.GDao2VirtualEntity;
import org.junit.Test;
import static jp.co.kccs.greenearth.xform.code.jdbc.service.TestUtils.getVirtualEntity;
import static org.junit.Assert.*;

public class Dao2SqlResultResponseTest {

	/**
	 * 設定された値が正しく取得できること。<br>
	 */
	@Test
	public void testGetterSetter() {
		GDao2VirtualEntity virtual = getVirtualEntity();
		Dao2SqlResultResponse resultResponse = new Dao2SqlResultResponse();
		resultResponse.setType(GCrudType.SELECT);
		resultResponse.setSqlScript("select * from test;");
		resultResponse.setVirtualEntity(virtual);

		assertEquals("select * from test;", resultResponse.getSqlScript());
		assertEquals(GCrudType.SELECT, resultResponse.getType());
		assertEquals("aaaa-aaaa-aaaa-aaaa-aaaa", resultResponse.getVirtualEntity().getVeId());
		assertEquals("bbbb-bbbb-bbbb-bbbb-bbbb", resultResponse.getVirtualEntity().getCategoryId());
		assertEquals("categoryTest", resultResponse.getVirtualEntity().getCategoryName());
		assertEquals("1", resultResponse.getVirtualEntity().getVeType());
		assertEquals(2, resultResponse.getVirtualEntity().getColumns().size());

		assertEquals("testDb", resultResponse.getVirtualEntity().getRefEntity().getDatabaseName());
		assertEquals("testPhy", resultResponse.getVirtualEntity().getRefEntity().getPhyName());
		assertEquals("testEntity", resultResponse.getVirtualEntity().getRefEntity().getName());
		assertEquals(2, resultResponse.getVirtualEntity().getRefEntity().getColumns().size());
		assertEquals(1, resultResponse.getVirtualEntity().getRefEntity().getUniqueKeys().size());
		assertEquals(1, resultResponse.getVirtualEntity().getRefEntity().getForeignKeys().size());

		assertEquals("testVColumn", resultResponse.getVirtualEntity().getColumns().get(0).getName());
		assertEquals("testEntity.testCol", resultResponse.getVirtualEntity().getColumns().get(0).getRefColumn());
		assertEquals("testVColumn", resultResponse.getVirtualEntity().getColumns().get(0).getDisplayName());

		assertEquals("testVColumn2", resultResponse.getVirtualEntity().getColumns().get(1).getName());
		assertEquals("testEntity.testCol2", resultResponse.getVirtualEntity().getColumns().get(1).getRefColumn());
		assertEquals("testVColumn2", resultResponse.getVirtualEntity().getColumns().get(1).getDisplayName());

		assertEquals("testCol", resultResponse.getVirtualEntity().getRefEntity().getColumns().get(0).getName());
		assertEquals("testCol", resultResponse.getVirtualEntity().getRefEntity().getColumns().get(0).getPhyName());
		assertTrue(resultResponse.getVirtualEntity().getRefEntity().getColumns().get(0).isPrimaryKey());
		assertEquals(10, resultResponse.getVirtualEntity().getRefEntity().getColumns().get(0).getSize());
		assertTrue(resultResponse.getVirtualEntity().getRefEntity().getColumns().get(0).isNotNull());
		assertEquals("STRING", resultResponse.getVirtualEntity().getRefEntity().getColumns().get(0).getDataType());

		assertEquals("testCol2", resultResponse.getVirtualEntity().getRefEntity().getColumns().get(1).getName());
		assertEquals("testCol2", resultResponse.getVirtualEntity().getRefEntity().getColumns().get(1).getPhyName());
		assertFalse(resultResponse.getVirtualEntity().getRefEntity().getColumns().get(1).isPrimaryKey());
		assertEquals(255, resultResponse.getVirtualEntity().getRefEntity().getColumns().get(1).getSize());
		assertTrue(resultResponse.getVirtualEntity().getRefEntity().getColumns().get(1).isNotNull());
		assertEquals("STRING", resultResponse.getVirtualEntity().getRefEntity().getColumns().get(1).getDataType());

		assertEquals("uk1", resultResponse.getVirtualEntity().getRefEntity().getUniqueKeys().get(0).getName());
		assertEquals("testCol", resultResponse.getVirtualEntity().getRefEntity().getUniqueKeys().get(0).getUniqueKeyColumns().get(0).getName());
		assertEquals("testCol", resultResponse.getVirtualEntity().getRefEntity().getUniqueKeys().get(0).getUniqueKeyColumns().get(0).getPhyName());
		assertTrue(resultResponse.getVirtualEntity().getRefEntity().getUniqueKeys().get(0).getUniqueKeyColumns().get(0).isPrimaryKey());
		assertEquals(10, resultResponse.getVirtualEntity().getRefEntity().getUniqueKeys().get(0).getUniqueKeyColumns().get(0).getSize());
		assertTrue(resultResponse.getVirtualEntity().getRefEntity().getUniqueKeys().get(0).getUniqueKeyColumns().get(0).isNotNull());
		assertEquals("STRING", resultResponse.getVirtualEntity().getRefEntity().getUniqueKeys().get(0).getUniqueKeyColumns().get(0).getDataType());

		assertEquals("testCol2", resultResponse.getVirtualEntity().getRefEntity().getUniqueKeys().get(0).getUniqueKeyColumns().get(1).getName());
		assertEquals("testCol2", resultResponse.getVirtualEntity().getRefEntity().getUniqueKeys().get(0).getUniqueKeyColumns().get(1).getPhyName());
		assertFalse(resultResponse.getVirtualEntity().getRefEntity().getUniqueKeys().get(0).getUniqueKeyColumns().get(1).isPrimaryKey());
		assertEquals(255, resultResponse.getVirtualEntity().getRefEntity().getUniqueKeys().get(0).getUniqueKeyColumns().get(1).getSize());
		assertTrue(resultResponse.getVirtualEntity().getRefEntity().getUniqueKeys().get(0).getUniqueKeyColumns().get(1).isNotNull());
		assertEquals("STRING", resultResponse.getVirtualEntity().getRefEntity().getUniqueKeys().get(0).getUniqueKeyColumns().get(1).getDataType());

		assertEquals("refEntity", resultResponse.getVirtualEntity().getRefEntity().getForeignKeys().get(0).getReferenceEntity().getName());
		assertEquals("phyRefEntity", resultResponse.getVirtualEntity().getRefEntity().getForeignKeys().get(0).getReferenceEntity().getPhyName());
		assertEquals(1, resultResponse.getVirtualEntity().getRefEntity().getForeignKeys().get(0).getReferenceEntity().getColumns().size());
		assertEquals("refCol", resultResponse.getVirtualEntity().getRefEntity().getForeignKeys().get(0).getReferenceEntity().getColumns().get(0).getName());
		assertEquals("phyRefCol", resultResponse.getVirtualEntity().getRefEntity().getForeignKeys().get(0).getReferenceEntity().getColumns().get(0).getPhyName());
		assertEquals("STRING", resultResponse.getVirtualEntity().getRefEntity().getForeignKeys().get(0).getReferenceEntity().getColumns().get(0).getDataType());
		assertEquals(10, resultResponse.getVirtualEntity().getRefEntity().getForeignKeys().get(0).getReferenceEntity().getColumns().get(0).getSize());
		assertTrue(resultResponse.getVirtualEntity().getRefEntity().getForeignKeys().get(0).getReferenceEntity().getColumns().get(0).isNotNull());
		assertTrue(resultResponse.getVirtualEntity().getRefEntity().getForeignKeys().get(0).getReferenceEntity().getColumns().get(0).isPrimaryKey());

		assertEquals("testCol", resultResponse.getVirtualEntity().getRefEntity().getForeignKeys().get(0).getForeignKeyColumns().get(0).getSourceColumn());
		assertEquals("refCol", resultResponse.getVirtualEntity().getRefEntity().getForeignKeys().get(0).getForeignKeyColumns().get(0).getReferenceColumn());
		assertEquals("test", resultResponse.getVirtualEntity().getRefEntity().getForeignKeys().get(0).getForeignKeyColumns().get(0).getConstValue());

	}
}
