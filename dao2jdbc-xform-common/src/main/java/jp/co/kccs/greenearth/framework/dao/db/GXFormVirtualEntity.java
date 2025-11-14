package jp.co.kccs.greenearth.framework.dao.db;

import jp.co.kccs.greenearth.xform.configuration.GXFormSettingHolder;
import jp.co.kccs.greenearth.xform.old.configuration.GReservedWordSetting;

import java.util.MissingResourceException;

public class GXFormVirtualEntity extends GDBVirtualEntityImpl {
	public GXFormVirtualEntity() {
		this.initialize();
	}
	@Override
	protected void initialize() {
		GReservedWordSetting reservedWordSetting = GXFormSettingHolder.getSetting(GReservedWordSetting.class);

		String key;
		try {
			key = reservedWordSetting.getCompanyCodeColumn();
			this.setCompanyCDKey(key);
		} catch (MissingResourceException var7) {
			this.setCompanyCDKey("CompanyCD");
		}

		try {
			key = reservedWordSetting.getExclusiveKeyColumn();
			this.setExclusiveKey(key);
		} catch (MissingResourceException var6) {
			this.setExclusiveKey("ExclusiveFG");
		}

		try {
			key = reservedWordSetting.getInsertedUserIdColumn();
			this.setInsertedUserIDKey(key);
		} catch (MissingResourceException var5) {
			this.setInsertedUserIDKey("RegisteredPerson");
		}

		try {
			key = reservedWordSetting.getInsertedDateColumn();
			this.setInsertedDateKey(key);
		} catch (MissingResourceException var4) {
			this.setInsertedDateKey("RegisteredDT");
		}

		try {
			key = reservedWordSetting.getUpdatedUserIdColumn();
			this.setUpdatedUserIDKey(key);
		} catch (MissingResourceException var3) {
			this.setUpdatedUserIDKey("UpdatedPerson");
		}

		try {
			key = reservedWordSetting.getUpdatedDateColumn();
			this.setUpdatedDateKey(key);
		} catch (MissingResourceException var2) {
			this.setUpdatedDateKey("UpdatedDT");
		}

	}
}
