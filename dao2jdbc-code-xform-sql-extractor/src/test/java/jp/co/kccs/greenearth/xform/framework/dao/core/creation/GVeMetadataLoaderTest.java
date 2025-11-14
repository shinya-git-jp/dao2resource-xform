package jp.co.kccs.greenearth.xform.framework.dao.core.creation;

import jp.co.kccs.greenearth.framework.dao.db.GDBVirtualEntity;
import jp.co.kccs.greenearth.framework.dao.db.repository.GForeignKey;
import jp.co.kccs.greenearth.framework.dao.enumtype.GUsageType;
import jp.co.kccs.greenearth.testutils.dbunit.TestHelper;
import jp.co.kccs.greenearth.xform.code.dao.core.creation.GVeIdMetadataLoaderImpl;
import jp.co.kccs.greenearth.xform.code.dao.core.creation.GVeMetadataLoader;
import jp.co.kccs.greenearth.xform.configuration.GDbCommonSetting;
import jp.co.kccs.greenearth.xform.configuration.GTransformSetting;
import jp.co.kccs.greenearth.xform.configuration.GXFormSetting;
import jp.co.kccs.greenearth.xform.configuration.GXFormSettingHolder;
import jp.co.kccs.greenearth.xform.dao.common.GVEItem;
import jp.co.kccs.greenearth.xform.old.configuration.GLocaleCommonSetting;
import jp.co.kccs.greenearth.xform.old.configuration.GReservedWordSetting;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * @author KCCS XXXXX
 * @create GEF_NEXT_DATE
 * @since GEF_NEXT_VERSION
 */
public abstract class GVeMetadataLoaderTest {

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
	public void testCreateByVeId() {
		cleanInsert("jp/co/kccs/greenearth/xform/code/dao/core/creation/GVeMetadataLoaderTest/import_ceratebyveid.xml");
		
		String veId = "E44ADEF7B30549449EDF193ECFB66631";
		GVeMetadataLoader veMetadataLoader = new GVeIdMetadataLoaderImpl();
		GVEItem<GDBVirtualEntity> virtualEntity = (GVEItem<GDBVirtualEntity>) veMetadataLoader.create(veId);

		assertEquals("E44ADEF7B30549449EDF193ECFB66631", virtualEntity.getId());
		assertEquals(10, virtualEntity.getData().getVirtualColumns().size());
		assertEquals("jdbc_tool_test_pk_uk_mainmaster", virtualEntity.getData().getEntity().getEntityName());
		assertEquals("objectID", virtualEntity.getData().getVirtualColumns().get(0).getColumnName());
		assertEquals("ItemCD", virtualEntity.getData().getVirtualColumns().get(1).getColumnName());
		assertEquals("ItemNA", virtualEntity.getData().getVirtualColumns().get(2).getColumnName());
		assertEquals("UnitPrice", virtualEntity.getData().getVirtualColumns().get(3).getColumnName());
		assertEquals("SellPrice", virtualEntity.getData().getVirtualColumns().get(4).getColumnName());
		assertEquals("Flag", virtualEntity.getData().getVirtualColumns().get(5).getColumnName());
		assertEquals("PriceFlag", virtualEntity.getData().getVirtualColumns().get(6).getColumnName());
		assertEquals("ExclusiveFG", virtualEntity.getData().getVirtualColumns().get(7).getColumnName());
		assertEquals("RegisteredDT", virtualEntity.getData().getVirtualColumns().get(8).getColumnName());
		assertEquals("UpdatedDT", virtualEntity.getData().getVirtualColumns().get(9).getColumnName());
		
		assertEquals(3, virtualEntity.getData().getForeignKeys().size());
		GForeignKey innerForeignKey = virtualEntity.getData().getForeignKeys().get("99570675BC7D4920AA1ED5FC0CE6B0FD");
		assertEquals(GUsageType.INNER, innerForeignKey.getUsage());
		assertEquals("190B973EEB8B47FB97FECAC78BE43E79", innerForeignKey.getSourceEntityOID());
		assertEquals("54DDEA23D42047088F1005030BB95913", innerForeignKey.getReferenceEntityOID());
		assertEquals(1, innerForeignKey.getForeignKeyElements().size());
		assertEquals("87A41546CDE04D07B70DA11768058678", innerForeignKey.getForeignKeyElements().get(0).getSourceColumnOID());
		assertEquals("992E6B9331CC446C89CA13108960B4C9", innerForeignKey.getForeignKeyElements().get(0).getReferenceColumnOID());
		
		GForeignKey rightForeignKey = virtualEntity.getData().getForeignKeys().get("B0A57311DFD2486DBE7BC5FD48486ADD");
		assertEquals(GUsageType.RIGHT_OUTER, rightForeignKey.getUsage());
		assertEquals("190B973EEB8B47FB97FECAC78BE43E79", rightForeignKey.getSourceEntityOID());
		assertEquals("34DD344BA3F74F45934D5E8F05BE5689", rightForeignKey.getReferenceEntityOID());
		assertEquals(1, rightForeignKey.getForeignKeyElements().size());
		assertEquals("EA27F47B07DD41109077BD125EE2FC9A", rightForeignKey.getForeignKeyElements().get(0).getSourceColumnOID());
		assertEquals("E372117D7BC84463B2F961CE18AA5340", rightForeignKey.getForeignKeyElements().get(0).getReferenceColumnOID());
		
		GForeignKey leftForeignKey = virtualEntity.getData().getForeignKeys().get("347F2BA5B17F4E95A37C06B055F954F6");
		assertEquals(GUsageType.LEFT_OUTER, leftForeignKey.getUsage());
		assertEquals("190B973EEB8B47FB97FECAC78BE43E79", leftForeignKey.getSourceEntityOID());
		assertEquals("4CC7FD3DD7E040A29E14D5536D875CAA", leftForeignKey.getReferenceEntityOID());
		assertEquals(1, leftForeignKey.getForeignKeyElements().size());
		assertEquals("87A41546CDE04D07B70DA11768058678", leftForeignKey.getForeignKeyElements().get(0).getSourceColumnOID());
		assertEquals("AF3E03D171864D46A445BCE3B7CC1E55", leftForeignKey.getForeignKeyElements().get(0).getReferenceColumnOID());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateByVeIdNotExist() {
		cleanInsert("jp/co/kccs/greenearth/xform/code/dao/core/metadata/creation/GVeMetadataLoaderTest/import_ceratebyveid.xml");

		String veId = "E44ADEF7B30549449EDF193ECFB66632";
		GVeMetadataLoader veMetadataLoader = new GVeIdMetadataLoaderImpl();
		veMetadataLoader.create(veId);

		fail();
	}
}
