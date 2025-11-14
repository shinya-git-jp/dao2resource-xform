package jp.co.kccs.greenearth.framework.dao.db;

import jp.co.kccs.greenearth.commons.GFrameworkUtils;
import jp.co.kccs.greenearth.commons.jdbc.GJdbcConnectionManagerFactory;
import jp.co.kccs.greenearth.commons.utils.GLocaleUtils;
import jp.co.kccs.greenearth.framework.dao.GDAOException;
import jp.co.kccs.greenearth.framework.dao.db.repository.GDBColumn;
import jp.co.kccs.greenearth.framework.dao.db.repository.GDBEntity;
import jp.co.kccs.greenearth.framework.dao.db.repository.GUniqueKey;
import jp.co.kccs.greenearth.framework.dao.db.sql.GSQLExecuter;
import jp.co.kccs.greenearth.framework.dao.enumtype.GComparisonOperatorType;
import jp.co.kccs.greenearth.framework.dao.enumtype.GDataSourceType;
import jp.co.kccs.greenearth.framework.dao.enumtype.GSortMode;
import jp.co.kccs.greenearth.xform.configuration.GDbCommonSetting;
import jp.co.kccs.greenearth.xform.configuration.GXFormSettingHolder;
import jp.co.kccs.greenearth.xform.old.common.GDaoDbType;
import jp.co.kccs.greenearth.xform.old.configuration.GLocaleCommonSetting;
import jp.co.kccs.greenearth.xform.old.configuration.GReservedWordSetting;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;

public class GXFormVirtualEntityLoader extends GNamedDBVirtualEntityLoader {
	@Override
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

	@Override
	protected String getDBSchema() {
		GDbCommonSetting dbCommonSetting = GXFormSettingHolder.getSetting(GDbCommonSetting.class);
		return dbCommonSetting.getSchema();
	}


	@Override
	public GDBVirtualEntity loadRepository(String objectID) throws GDAOException {
		GDBVirtualEntity gve = (GDBVirtualEntity)GFrameworkUtils.getComponent("jp.co.kccs.greenearth.framework.dao.GVirtualEntity");
		gve.setObjectID(objectID);
		Connection conn = null;

		try {
			conn = this.catchConnection();
			this.createVirtualEntity(gve);
			this.createMainEntity(gve);
			this.createSubEntities(gve);
			this.createForeignKeys(gve);
			this.createVirtualColumns(gve);
			this.createConditionSet(gve);
			this.createOrdination(gve);
			this.createPrimaryKeyCondition(gve);
		} catch (Exception var8) {
			if (var8 instanceof IllegalArgumentException) {
				throw (IllegalArgumentException)var8;
			}

			throw new GDAOException(var8, "Loading of repository failed.");
		} finally {
			this.releaseConnection(conn);
		}

		return gve;
	}

	private void createOrdination(GDBVirtualEntity virtualEntity) throws GDAOException {
		GSQLExecuter gdbm = this.getSQLExecuter();
		GConditionSet conditionSet = virtualEntity.getConditionSet("DEFAULT");
		Object[] params = new Object[]{virtualEntity.getObjectID()};
		String sql = this.getCreateOrderByVirtualColumnSQL();
		Connection conn = null;

		try {
			conn = this.catchConnection();
			ResultSet rs = null;

			String virtualColumnOID;
			try {
				rs = gdbm.executeQuery(conn, sql, params);

				while(rs.next()) {
					virtualColumnOID = rs.getString(1);
					String tempMode = rs.getString(2);
					GDBVirtualColumn gvc = virtualEntity.getDBVirtualColumnByOID(virtualColumnOID);
					GOrderByColumn gsc = new GOrderByColumnImpl();
					gsc.initialize(gvc);
					GSortMode sortMode = GSortMode.convert(tempMode);
					gsc.setSortMode(sortMode);
					conditionSet.addOrderByColumn(gsc);
				}
			} finally {
				gdbm.closeResultSet(rs);
			}

			sql = this.getCreateGroupByVirtualColumnSQL();

			try {
				rs = gdbm.executeQuery(conn, sql, params);

				while(rs.next()) {
					virtualColumnOID = rs.getString(1);
					GDBVirtualColumn gvc = virtualEntity.getDBVirtualColumnByOID(virtualColumnOID);
					GGroupByColumn ggc = new GGroupByColumnImpl();
					ggc.initialize(gvc);
					conditionSet.addGroupByColumn(ggc);
				}

				if (conditionSet.getGroupByColumns().size() > 0) {
					conditionSet.setGroupByFlag(true);
				}
			} finally {
				gdbm.closeResultSet(rs);
			}
		} catch (SQLException var30) {
			throw new GDAOException(var30, "Loading of repository failed.");
		} finally {
			this.releaseConnection(conn);
		}

	}

	private void createPrimaryKeyCondition(GDBVirtualEntity virtualEntity) {
		String primaryKeyColumnName = (GXFormSettingHolder.getSetting(GReservedWordSetting.class)).getPrimaryKeyColumn();
		GDBVirtualColumn primaryVirtualColumn = virtualEntity.getDBVirtualColumn(primaryKeyColumnName);
		GCondition primaryKey = (GCondition)GFrameworkUtils.getComponent("jp.co.kccs.greenearth.framework.dao.db.GCondition");
		if (primaryVirtualColumn != null) {
			primaryKey.setExpression("%%");
			primaryKey.setConditionType("PrimaryKey");
			GConditionColumn gcc = (GConditionColumn)GFrameworkUtils.getComponent("jp.co.kccs.greenearth.framework.dao.db.GConditionColumn");
			gcc.setComparisonOperator(GComparisonOperatorType.EQUAL);
			gcc.setVirtualColumn(primaryVirtualColumn);
			gcc.setFlgOption(true);
			gcc.setFlgTrim(false);
			gcc.setIndex(0);
			primaryKey.addConditionColumn(gcc);
		} else {
			GDBEntity entity = virtualEntity.getMainDBEntity();
			if (entity == null) {
				return;
			}

			List<GUniqueKey> uniqueKeys = entity.getUniqueKeys();
			if (uniqueKeys == null || uniqueKeys.size() == 0) {
				return;
			}

			GUniqueKey uniqueKey = (GUniqueKey)uniqueKeys.get(0);
			if (uniqueKey != null) {
				List<GDBColumn> columns = uniqueKey.getKeyElements();
				String expression = "";

				for(int i = 0; i < columns.size(); ++i) {
					GDBColumn column = (GDBColumn)columns.get(i);
					if (i > 0) {
						expression = expression + " AND ";
					}

					expression = expression + "%%";
					GDBVirtualColumn gvc = virtualEntity.getDBVirtualColumnAtMainEntity(column);
					GConditionColumn gcc = (GConditionColumn)GFrameworkUtils.getComponent("jp.co.kccs.greenearth.framework.dao.db.GConditionColumn");
					gcc.setComparisonOperator(GComparisonOperatorType.EQUAL);
					gcc.setVirtualColumn(gvc);
					gcc.setFlgOption(true);
					gcc.setFlgTrim(false);
					gcc.setIndex(i);
					primaryKey.addConditionColumn(gcc);
				}

				primaryKey.setConditionType("PrimaryKey");
				primaryKey.setExpression(expression);
				uniqueKeys.remove(0);
			}
		}

		virtualEntity.setPrimaryKey(primaryKey);
	}

	private void releaseConnection(Connection conn) {
		try {
			GJdbcConnectionManagerFactory.getInstance().close(conn);
		} catch (SQLException var3) {
			throw new GDAOException(var3, "To close connection is failed.");
		}
	}

	@Override
	protected Map<Locale, String> getLocalizeName4VE(Connection conn, GSQLExecuter gdbm, String id) {
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
