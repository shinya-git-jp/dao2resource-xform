package jp.co.kccs.greenearth.framework.dao.db.repository;

import jp.co.kccs.greenearth.commons.utils.GLocaleUtils;
import jp.co.kccs.greenearth.framework.dao.GDAOException;
import jp.co.kccs.greenearth.framework.dao.db.sql.GSQLExecuter;
import jp.co.kccs.greenearth.framework.dao.enumtype.GDataSourceType;
import jp.co.kccs.greenearth.xform.configuration.GDbCommonSetting;
import jp.co.kccs.greenearth.xform.configuration.GXFormSettingHolder;
import jp.co.kccs.greenearth.xform.old.common.GDaoDbType;
import jp.co.kccs.greenearth.xform.old.configuration.GLocaleCommonSetting;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class GXFormDBEntityLoaderImpl extends GNamedDBEntityLoader {
	protected GDataSourceType getDataSourceType() {
		GDbCommonSetting dbCommonSetting = GXFormSettingHolder.getSetting(GDbCommonSetting.class);
		String dbType = "";
		if (dbCommonSetting.getDbType() == GDaoDbType.mysql) {
			dbType = "5";
		}
		else if (dbCommonSetting.getDbType() == GDaoDbType.oracle) {
			dbType = "0";
		}
		else if (dbCommonSetting.getDbType() == GDaoDbType.db2) {
			dbType = "2";
		}
		return GDataSourceType.convert(dbType);
	}

	protected String getDBSchema() {
		GDbCommonSetting daoCommonSetting = GXFormSettingHolder.getSetting(GDbCommonSetting.class);
		return daoCommonSetting.getSchema();
	}

	@Override
	protected Map<Locale, String> getLocalizeName4Entity(Connection conn, GSQLExecuter gdbm, String id) {
		Map<Locale, String> nameMap = new HashMap();
		String sql = this.getLocalizeNameSQL();
		Object[] params = new Object[]{id};
		ResultSet rs = null;
		GLocaleCommonSetting localeCommonSetting = (GXFormSettingHolder.getSetting(GLocaleCommonSetting.class));
		Locale[] languages = new Locale[]{
				Objects.isNull(localeCommonSetting.getMapping().getCountry1()) ? new Locale("") : GLocaleUtils.getLocale(localeCommonSetting.getMapping().getCountry1()),
				Objects.isNull(localeCommonSetting.getMapping().getCountry2()) ? new Locale("")  : GLocaleUtils.getLocale(localeCommonSetting.getMapping().getCountry2()),
				Objects.isNull(localeCommonSetting.getMapping().getCountry3()) ? new Locale("")  : GLocaleUtils.getLocale(localeCommonSetting.getMapping().getCountry3()),
				Objects.isNull(localeCommonSetting.getMapping().getCountry4()) ? new Locale("")  : GLocaleUtils.getLocale(localeCommonSetting.getMapping().getCountry4()),
				Objects.isNull(localeCommonSetting.getMapping().getCountry5()) ? new Locale("")  : GLocaleUtils.getLocale(localeCommonSetting.getMapping().getCountry5())
		};

		try {
			rs = gdbm.executeQuery(conn, sql, params);
			ResultSetMetaData rsm = rs.getMetaData();
			int count = rsm.getColumnCount();
			int i;
			if (rs.next()) {
				for(i = 0; i < count; ++i) {
					nameMap.put(languages[i], rs.getString(i+1));
				}
			} else {
				for(i = 0; i < count; ++i) {
					nameMap.put(languages[i], "failed to get displayname.");
				}
			}
		} catch (SQLException var18) {
			throw new GDAOException(var18, "Loading of repository failed.");
		} finally {
			try {
				gdbm.closeResultSet(rs);
			} catch (SQLException var17) {
				throw new GDAOException(var17, "To close connection is failed.");
			}
		}

		return nameMap;
	}
}
