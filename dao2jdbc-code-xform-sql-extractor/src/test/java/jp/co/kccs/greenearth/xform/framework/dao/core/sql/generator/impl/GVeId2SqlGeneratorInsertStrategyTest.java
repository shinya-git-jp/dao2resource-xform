package jp.co.kccs.greenearth.xform.framework.dao.core.sql.generator.impl;

import jp.co.kccs.greenearth.framework.dao.db.GDBVirtualEntity;
import jp.co.kccs.greenearth.framework.dao.db.GDBVirtualEntityFactory;
import jp.co.kccs.greenearth.testutils.dbunit.TestHelper;
import jp.co.kccs.greenearth.xform.code.dao.core.sql.generator.impl.GVEMetadata2SqlExtractorInsertStrategy;
import jp.co.kccs.greenearth.xform.configuration.GDbCommonSetting;
import jp.co.kccs.greenearth.xform.configuration.GTransformSetting;
import jp.co.kccs.greenearth.xform.configuration.GXFormSetting;
import jp.co.kccs.greenearth.xform.configuration.GXFormSettingHolder;
import jp.co.kccs.greenearth.xform.old.configuration.GLocaleCommonSetting;
import jp.co.kccs.greenearth.xform.old.configuration.GReservedWordSetting;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author KCCS XXXXX
 * @create GEF_NEXT_DATE
 * @since GEF_NEXT_VERSION
 */
public abstract class GVeId2SqlGeneratorInsertStrategyTest {

	protected String dbScheme;
	protected GDbCommonSetting dbCommonSetting;
	GVEMetadata2SqlExtractorInsertStrategy insertStrategy = new GVEMetadata2SqlExtractorInsertStrategy();

	@Before
	public void setup() throws Exception {
		Map<Class<? extends GXFormSetting>, GXFormSetting> xformSetting = getSetting();
		GXFormSettingHolder.setCommonSetting(GDbCommonSetting.class, xformSetting.get(GDbCommonSetting.class));
		GXFormSettingHolder.setCommonSetting(GReservedWordSetting.class, xformSetting.get(GReservedWordSetting.class));
		GXFormSettingHolder.setCommonSetting(GLocaleCommonSetting.class, xformSetting.get(GLocaleCommonSetting.class));
		GXFormSettingHolder.setCommonSetting(GTransformSetting.class, xformSetting.get(GTransformSetting.class));
		dbCommonSetting = GXFormSettingHolder.getSetting(GDbCommonSetting.class);
		dbScheme = GXFormSettingHolder.getSetting(GDbCommonSetting.class).getSchema();
	}

	@After
	public void release() {
		GXFormSettingHolder.clearCommonSetting();
	}
	
	public void cleanInsert(String filePath)  {
		try {
			TestHelper.cleanInsert(dbCommonSetting, filePath);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected abstract Map<Class<? extends GXFormSetting>, GXFormSetting> getSetting();
	
	public abstract String getTimestampCommand();

	@Test
	public void testCanGenerate() {
		cleanInsert("jp/co/kccs/greenearth/xform/code/dao/core/sql/generator/impl/GVeId2SqlGeneratorInsertStrategyTest/import_cangenerate.xml");
		String inqVeId = "57E66373CB914D9F88DB638EAF6B1699";
		GDBVirtualEntity inqVirtualEntity = GDBVirtualEntityFactory.getVirtualEntity(inqVeId);
		assertFalse(insertStrategy.canGenerate(inqVirtualEntity));
		
		String regVeId = "1FEFE156727646DC9122045339BD3999";
		GDBVirtualEntity regVirtualEntity = GDBVirtualEntityFactory.getVirtualEntity(regVeId);
		assertTrue(insertStrategy.canGenerate(regVirtualEntity));
	}
	
	@Test
	public void testGenerate(){
		cleanInsert("jp/co/kccs/greenearth/xform/code/dao/core/sql/generator/impl/GVeId2SqlGeneratorInsertStrategyTest/import_generate.xml");
		String veId = "1FEFE156727646DC9122045339BD3999";
		assertEquals("INSERT INTO " + dbScheme + ".jdbc_tool_test_pk_uk_mainmaster(objectID,ItemCD,ItemNA,UnitPrice,SellPrice,Flag,PriceFlag,ExclusiveFG,RegisteredDT,UpdatedDT) " +
				"VALUES(?,?,?,?,?,?,?,?," + getTimestampCommand() + "," + getTimestampCommand() + ")", insertStrategy.generate(veId));
	}
}
