package jp.co.kccs.greenearth.xform.framework.dao.core.creation;

import jp.co.kccs.greenearth.commons.GFrameworkUtils;
import jp.co.kccs.greenearth.commons.utils.GFileUtils;
import jp.co.kccs.greenearth.framework.dao.db.GDBVirtualEntity;
import jp.co.kccs.greenearth.testutils.dbunit.TestHelper;
import jp.co.kccs.greenearth.xform.code.dao.core.creation.GVeCategoryMetadataLoader;
import jp.co.kccs.greenearth.xform.configuration.GDbCommonSetting;
import jp.co.kccs.greenearth.xform.configuration.GTransformSetting;
import jp.co.kccs.greenearth.xform.configuration.GXFormSetting;
import jp.co.kccs.greenearth.xform.configuration.GXFormSettingHolder;
import jp.co.kccs.greenearth.xform.dao.common.GDao2Utils;
import jp.co.kccs.greenearth.xform.dao.common.GVECategoryImpl;
import jp.co.kccs.greenearth.xform.dao.common.GVEItemImpl;
import jp.co.kccs.greenearth.xform.old.configuration.GLocaleCommonSetting;
import jp.co.kccs.greenearth.xform.old.configuration.GReservedWordSetting;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;


/**
 * @author KCCS XXXXX
 * @create GEF_NEXT_DATE
 * @since GEF_NEXT_VERSION
 */
public abstract class GVeCategoryMetadataLoaderTest {

	protected String dbScheme;

	protected GDbCommonSetting dbCommonSetting;

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
	
	@Test
	public void testCreateByCategoryId() {
		
		cleanInsert("jp/co/kccs/greenearth/xform/code/dao/core/creation/GVeMetadataLoaderTest/import_createbycategoryid.xml");
		
		String categoryId = "F56264D123E6450DB2AF788A7039CA41";
		GVeCategoryMetadataLoader veCategoryMetadataLoader = new GVeCategoryMetadataLoader();
		GVECategoryImpl<GDBVirtualEntity> virtualEntityGVENode = (GVECategoryImpl<GDBVirtualEntity>) veCategoryMetadataLoader.create(categoryId);

		assertEquals("F56264D123E6450DB2AF788A7039CA41", virtualEntityGVENode.getId());
		assertEquals(3, virtualEntityGVENode.getChildren().size());
		assertEquals("F08F656C201141ADBD1B743F144A44A0", virtualEntityGVENode.getChildren().get(0).getId());
		assertEquals("BC37EC6B0EFF411980A0385FFB38D035", virtualEntityGVENode.getChildren().get(1).getId());
		GVECategoryImpl<GDBVirtualEntity> dbVirtualEntityGVENode = (GVECategoryImpl<GDBVirtualEntity>) virtualEntityGVENode.getChildren().get(2);
		assertEquals(1, dbVirtualEntityGVENode.getChildren().size());
		GVECategoryImpl<GDBVirtualEntity> gdbVirtualEntityGVENode = (GVECategoryImpl<GDBVirtualEntity>) dbVirtualEntityGVENode.getChildren().get(0);
		GVEItemImpl<GDBVirtualEntity> dbVirtualEntityGVEItem = (GVEItemImpl<GDBVirtualEntity>) gdbVirtualEntityGVENode.getChildren().get(0);
		assertEquals("A4F67FC9EE6249AF9F5DE398C9231974", dbVirtualEntityGVEItem.getId());
	}
	
	@Test
	public void testCreateByCategoryIdNotExist() {

		cleanInsert("jp/co/kccs/greenearth/xform/code/dao/core/creation/GVeMetadataLoaderTest/import_createbycategoryid.xml");

		String categoryId = "F56264D123E6450DB2AF788A7039CA42";
		GVeCategoryMetadataLoader veCategoryMetadataLoader = new GVeCategoryMetadataLoader();
		GVECategoryImpl<GDBVirtualEntity> virtualEntityGVENode = (GVECategoryImpl<GDBVirtualEntity>) veCategoryMetadataLoader.create(categoryId);

		assertEquals("F56264D123E6450DB2AF788A7039CA42", virtualEntityGVENode.getId());
		assertEquals(0, virtualEntityGVENode.getChildren().size());

	}
}
