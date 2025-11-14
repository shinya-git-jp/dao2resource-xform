package jp.co.kccs.greenearth.xform.code.dao.core;

import jp.co.kccs.greenearth.commons.GFrameworkException;
import jp.co.kccs.greenearth.xform.code.jdbc.core.GCompoundApiImpl;
import jp.co.kccs.greenearth.xform.code.jdbc.core.GTerminatorApi;
import jp.co.kccs.greenearth.xform.code.jdbc.core.GWhereApi;
import jp.co.kccs.greenearth.xform.code.jdbc.core.service.GXFormScript;
import jp.co.kccs.greenearth.xform.dao.dto.GDao2VirtualEntity;
import jp.co.kccs.greenearth.xform.jdbc.common.GColumnType;
import jp.co.kccs.greenearth.xform.jdbc.common.GCrudType;
import jp.co.kccs.greenearth.xform.jdbc.common.GXFormEntity;
import org.junit.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;

public class GDao2XFormScriptConverterImplTest {

	@Test
	public void testConvert() {
		GDao2VirtualEntity virtualEntity = GUtilTest.getVirtualEntity();
		GDao2XFormScriptConverter converter = new GDao2XFormScriptConverterImpl();
		{
			GXFormScript script = converter.convert("select * from test;", GCrudType.SELECT, virtualEntity);

			assertEquals("仮想表ID: " + virtualEntity.getVeId(), script.getDescription());
			assertEquals("select * from test;", script.getScript());
			assertTrue(script.getSelectColumns().isEmpty());
			assertTrue(script.getWhereColumns().isEmpty());
			assertEquals(GCrudType.SELECT, script.getCrudType());
			assertEquals(1, script.getEntities().size());
			GXFormEntity gxFormEntity = script.getEntities().get("testEntity");
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
			assertTrue(script.getApiTypes().contains(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE)));
			assertTrue(script.getApiTypes().contains(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE)));
			assertTrue(script.getApiTypes().contains(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE_PK)));
			assertTrue(script.getApiTypes().contains(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE_PK)));
			assertTrue(script.getApiTypes().contains(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE_PK)));
			assertTrue(script.getApiTypes().contains(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE_UK)));
			assertTrue(script.getApiTypes().contains(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE_UK)));
			assertTrue(script.getApiTypes().contains(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE_UK)));
			assertNull(script.getExclusiveColumn());
		}
		{
			try {
				converter.convert(null, null, null);
				fail();
			} catch (Exception exception) {
				assertEquals(GFrameworkException.class, exception.getClass());
				assertEquals("Virtual Entity cannot be null.", exception.getMessage());
			}
		}
		{
			try {
				converter.convert(null, null, virtualEntity);
				fail();
			} catch (Exception exception) {
				assertEquals(GFrameworkException.class, exception.getClass());
				assertEquals("Crud type cannot be null.", exception.getMessage());
			}
		}
		{
			GXFormScript script = converter.convert(null, GCrudType.SELECT, virtualEntity);
			assertNull(script.getScript());
			assertEquals("仮想表ID: " + virtualEntity.getVeId(), script.getDescription());
			assertTrue(script.getSelectColumns().isEmpty());
			assertTrue(script.getWhereColumns().isEmpty());
			assertEquals(GCrudType.SELECT, script.getCrudType());
			assertEquals(1, script.getEntities().size());
			assertTrue(script.getApiTypes().contains(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE)));
			assertTrue(script.getApiTypes().contains(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE)));
			assertTrue(script.getApiTypes().contains(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE_PK)));
			assertTrue(script.getApiTypes().contains(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE_PK)));
			assertTrue(script.getApiTypes().contains(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE_PK)));
			assertTrue(script.getApiTypes().contains(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, GWhereApi.WHERE_UK)));
			assertTrue(script.getApiTypes().contains(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, GWhereApi.WHERE_UK)));
			assertTrue(script.getApiTypes().contains(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, GWhereApi.WHERE_UK)));
			assertNull(script.getExclusiveColumn());
		}
	}
}
