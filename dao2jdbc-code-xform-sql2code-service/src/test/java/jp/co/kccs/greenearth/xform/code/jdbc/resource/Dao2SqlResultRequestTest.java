package jp.co.kccs.greenearth.xform.code.jdbc.resource;

import jp.co.kccs.greenearth.xform.code.jdbc.resource.Dao2SqlResultRequest;
import jp.co.kccs.greenearth.xform.dao.dto.GDao2VirtualEntity;
import jp.co.kccs.greenearth.xform.jdbc.common.GCrudType;
import org.junit.Test;

import static jp.co.kccs.greenearth.xform.code.jdbc.Utils.getVirtualEntity;
import static org.junit.Assert.*;

public class Dao2SqlResultRequestTest {

	/**
	 * 設定された値が正しく取得できること。<br>
	 */
	@Test
	public void testGetterSetter() {
		GDao2VirtualEntity virtual = getVirtualEntity();
		Dao2SqlResultRequest resultRequest = new Dao2SqlResultRequest();
		resultRequest.setType(GCrudType.SELECT);
		resultRequest.setSqlScript("select * from test;");
		resultRequest.setVirtualEntity(virtual);


		assertEquals("select * from test;", resultRequest.getSqlScript());
		assertEquals(GCrudType.SELECT, resultRequest.getType());
		assertEquals("aaaa-aaaa-aaaa-aaaa-aaaa", resultRequest.getVirtualEntity().getVeId());
		assertEquals("bbbb-bbbb-bbbb-bbbb-bbbb", resultRequest.getVirtualEntity().getCategoryId());
		assertEquals("categoryTest", resultRequest.getVirtualEntity().getCategoryName());
		assertEquals("1", resultRequest.getVirtualEntity().getVeType());
		assertEquals(2, resultRequest.getVirtualEntity().getColumns().size());

		assertEquals("testDb", resultRequest.getVirtualEntity().getRefEntity().getDatabaseName());
		assertEquals("testPhy", resultRequest.getVirtualEntity().getRefEntity().getPhyName());
		assertEquals("testEntity", resultRequest.getVirtualEntity().getRefEntity().getName());
		assertEquals(2, resultRequest.getVirtualEntity().getRefEntity().getColumns().size());
		assertEquals(1, resultRequest.getVirtualEntity().getRefEntity().getUniqueKeys().size());
		assertEquals(1, resultRequest.getVirtualEntity().getRefEntity().getForeignKeys().size());

		assertEquals("testVColumn", resultRequest.getVirtualEntity().getColumns().get(0).getName());
		assertEquals("testEntity.testCol", resultRequest.getVirtualEntity().getColumns().get(0).getRefColumn());
		assertEquals("testVColumn", resultRequest.getVirtualEntity().getColumns().get(0).getDisplayName());

		assertEquals("testVColumn2", resultRequest.getVirtualEntity().getColumns().get(1).getName());
		assertEquals("testEntity.testCol2", resultRequest.getVirtualEntity().getColumns().get(1).getRefColumn());
		assertEquals("testVColumn2", resultRequest.getVirtualEntity().getColumns().get(1).getDisplayName());

		assertEquals("testCol", resultRequest.getVirtualEntity().getRefEntity().getColumns().get(0).getName());
		assertEquals("testCol", resultRequest.getVirtualEntity().getRefEntity().getColumns().get(0).getPhyName());
		assertTrue(resultRequest.getVirtualEntity().getRefEntity().getColumns().get(0).isPrimaryKey());
		assertEquals(10, resultRequest.getVirtualEntity().getRefEntity().getColumns().get(0).getSize());
		assertTrue(resultRequest.getVirtualEntity().getRefEntity().getColumns().get(0).isNotNull());
		assertEquals("STRING", resultRequest.getVirtualEntity().getRefEntity().getColumns().get(0).getDataType());

		assertEquals("testCol2", resultRequest.getVirtualEntity().getRefEntity().getColumns().get(1).getName());
		assertEquals("testCol2", resultRequest.getVirtualEntity().getRefEntity().getColumns().get(1).getPhyName());
		assertFalse(resultRequest.getVirtualEntity().getRefEntity().getColumns().get(1).isPrimaryKey());
		assertEquals(255, resultRequest.getVirtualEntity().getRefEntity().getColumns().get(1).getSize());
		assertTrue(resultRequest.getVirtualEntity().getRefEntity().getColumns().get(1).isNotNull());
		assertEquals("STRING", resultRequest.getVirtualEntity().getRefEntity().getColumns().get(1).getDataType());

		assertEquals("uk1", resultRequest.getVirtualEntity().getRefEntity().getUniqueKeys().get(0).getName());
		assertEquals("testCol", resultRequest.getVirtualEntity().getRefEntity().getUniqueKeys().get(0).getUniqueKeyColumns().get(0).getName());
		assertEquals("testCol", resultRequest.getVirtualEntity().getRefEntity().getUniqueKeys().get(0).getUniqueKeyColumns().get(0).getPhyName());
		assertTrue(resultRequest.getVirtualEntity().getRefEntity().getUniqueKeys().get(0).getUniqueKeyColumns().get(0).isPrimaryKey());
		assertEquals(10, resultRequest.getVirtualEntity().getRefEntity().getUniqueKeys().get(0).getUniqueKeyColumns().get(0).getSize());
		assertTrue(resultRequest.getVirtualEntity().getRefEntity().getUniqueKeys().get(0).getUniqueKeyColumns().get(0).isNotNull());
		assertEquals("STRING", resultRequest.getVirtualEntity().getRefEntity().getUniqueKeys().get(0).getUniqueKeyColumns().get(0).getDataType());

		assertEquals("testCol2", resultRequest.getVirtualEntity().getRefEntity().getUniqueKeys().get(0).getUniqueKeyColumns().get(1).getName());
		assertEquals("testCol2", resultRequest.getVirtualEntity().getRefEntity().getUniqueKeys().get(0).getUniqueKeyColumns().get(1).getPhyName());
		assertFalse(resultRequest.getVirtualEntity().getRefEntity().getUniqueKeys().get(0).getUniqueKeyColumns().get(1).isPrimaryKey());
		assertEquals(255, resultRequest.getVirtualEntity().getRefEntity().getUniqueKeys().get(0).getUniqueKeyColumns().get(1).getSize());
		assertTrue(resultRequest.getVirtualEntity().getRefEntity().getUniqueKeys().get(0).getUniqueKeyColumns().get(1).isNotNull());
		assertEquals("STRING", resultRequest.getVirtualEntity().getRefEntity().getUniqueKeys().get(0).getUniqueKeyColumns().get(1).getDataType());

		assertEquals("refEntity", resultRequest.getVirtualEntity().getRefEntity().getForeignKeys().get(0).getReferenceEntity().getName());
		assertEquals("phyRefEntity", resultRequest.getVirtualEntity().getRefEntity().getForeignKeys().get(0).getReferenceEntity().getPhyName());
		assertEquals(1, resultRequest.getVirtualEntity().getRefEntity().getForeignKeys().get(0).getReferenceEntity().getColumns().size());
		assertEquals("refCol", resultRequest.getVirtualEntity().getRefEntity().getForeignKeys().get(0).getReferenceEntity().getColumns().get(0).getName());
		assertEquals("phyRefCol", resultRequest.getVirtualEntity().getRefEntity().getForeignKeys().get(0).getReferenceEntity().getColumns().get(0).getPhyName());
		assertEquals("STRING", resultRequest.getVirtualEntity().getRefEntity().getForeignKeys().get(0).getReferenceEntity().getColumns().get(0).getDataType());
		assertEquals(10, resultRequest.getVirtualEntity().getRefEntity().getForeignKeys().get(0).getReferenceEntity().getColumns().get(0).getSize());
		assertTrue(resultRequest.getVirtualEntity().getRefEntity().getForeignKeys().get(0).getReferenceEntity().getColumns().get(0).isNotNull());
		assertTrue(resultRequest.getVirtualEntity().getRefEntity().getForeignKeys().get(0).getReferenceEntity().getColumns().get(0).isPrimaryKey());

		assertEquals("testCol", resultRequest.getVirtualEntity().getRefEntity().getForeignKeys().get(0).getForeignKeyColumns().get(0).getSourceColumn());
		assertEquals("refCol", resultRequest.getVirtualEntity().getRefEntity().getForeignKeys().get(0).getForeignKeyColumns().get(0).getReferenceColumn());
		assertEquals("test", resultRequest.getVirtualEntity().getRefEntity().getForeignKeys().get(0).getForeignKeyColumns().get(0).getConstValue());

	}
}
