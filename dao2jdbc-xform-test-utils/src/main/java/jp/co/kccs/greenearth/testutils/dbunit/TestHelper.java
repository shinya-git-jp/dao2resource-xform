package jp.co.kccs.greenearth.testutils.dbunit;

import jp.co.kccs.greenearth.commons.db.GConnectionManager;
import jp.co.kccs.greenearth.commons.utils.GFileUtils;
import jp.co.kccs.greenearth.framework.dao.db.GDBVirtualEntityFactory;
import jp.co.kccs.greenearth.framework.dao.db.repository.GDBEntityFactory;
import jp.co.kccs.greenearth.xform.configuration.*;
import jp.co.kccs.greenearth.xform.old.common.GDaoDbType;
import jp.co.kccs.greenearth.xform.old.configuration.*;
import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.ext.db2.Db2Connection;
import org.dbunit.ext.mysql.MySqlConnection;
import org.dbunit.ext.mysql.MySqlDataTypeFactory;
import org.dbunit.ext.oracle.OracleConnection;
import org.dbunit.operation.DatabaseOperation;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class TestHelper {
	
	protected static final String IMPORT_COMMON_FILE = "dbunit/import_common.xml";
	
	private static Map<String, String> dbTypeMap = Map.of(
			"oracle19c", "dbunit/import_database_oracle_common.xml",
			"db2v9", "dbunit/import_database_db2_common.xml",
			"mysql8_0", "dbunit/import_database_mysql_common.xml");
	
	public static void cleanInsert(GDbCommonSetting setting, String filePath) throws Exception {
		
		GDBVirtualEntityFactory.getInstance().clear();
		GDBEntityFactory.clearPool();
		
		FlatXmlDataSetBuilder builder = new FlatXmlDataSetBuilder();
		builder.setColumnSensing(true);
		IDataSet datasetDataBase = builder.build(GFileUtils.getResource(dbTypeMap.get(setting.getDbType().getValue())));
		IDataSet datasetCommon = builder.build(GFileUtils.getResource(IMPORT_COMMON_FILE));
		IDataSet dataset = builder.build(GFileUtils.getResource(filePath));
		
		GConnectionManager connManager = GConnectionManager.getInstance();
		connManager.removeConnections();
		Connection connection = null;
		try {
			connection = connManager.catchConnection();
			IDatabaseConnection conn;
			if (setting.getDbType().getValue().equals("mysql8_0")) {
				conn = new MySqlConnection(connection, setting.getSchema());
				conn.getConfig().setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new MySqlDataTypeFactory());
				conn.getConfig().setProperty(DatabaseConfig.PROPERTY_ESCAPE_PATTERN, "`?`");
			} else if (setting.getDbType().getValue().equals("oracle19c")) {
				conn = new OracleConnection(connection, setting.getSchema());
			} else if (setting.getDbType().getValue().equals("db2v9")) {
				conn = new Db2Connection(connection, setting.getSchema());
			} else {
				throw new DatabaseUnitException("unknown type");
			}
			DatabaseOperation.CLEAN_INSERT.execute(conn, datasetCommon);
			DatabaseOperation.INSERT.execute(conn, datasetDataBase);
			DatabaseOperation.INSERT.execute(conn, dataset);
		} finally {
			try {
				connManager.releaseConnection(connection);
			} catch (SQLException sqle) {
				sqle.printStackTrace();
			}
		}
	}
	public static Map<Class<? extends GXFormSetting>, GXFormSetting> getMySqlSetting() {
		GDbCommonSetting dbCommonSetting = new GDbCommonSettingImpl();
		dbCommonSetting.setDbType(GDaoDbType.mysql);
		dbCommonSetting.setUrl("jdbc:mysql://gef-unit-mysql8027.c3cxjzdf21mz.ap-northeast-1.rds.amazonaws.com:3306/gef_jdbc_tool?zeroDateTimeBehavior=convertToNull&useUnicode=true&useSSL=false&serverTimezone=GMT%2B9");
		dbCommonSetting.setSchema("gef_jdbc_tool");
		dbCommonSetting.setUsername("geframe");
		dbCommonSetting.setPassword("Kccs0000");

		GLocaleCommonSetting localeCommonSetting = new GLocaleCommonSettingImpl();
		GLocaleMappingCommonSetting localeMappingCommonSetting = new GLocaleMappingCommonSettingImpl();
		localeMappingCommonSetting.setCountry1("en");
		localeMappingCommonSetting.setCountry2("ja");
		localeMappingCommonSetting.setCountry3("zh");
		localeCommonSetting.setCurrent("en");
		localeCommonSetting.setMapping(localeMappingCommonSetting);

		GReservedWordSetting reservedWordSetting = new GReservedWordSettingImpl();
		reservedWordSetting.setPrimaryKeyColumn("objectID");
		reservedWordSetting.setExclusiveKeyColumn("ExclusiveFG");
		reservedWordSetting.setCompanyCodeColumn("CompanyCD");
		reservedWordSetting.setInsertedUserIdColumn("RegisteredPerson");
		reservedWordSetting.setInsertedDateColumn("RegisteredDT");
		reservedWordSetting.setUpdatedUserIdColumn("UpdatedPerson");
		reservedWordSetting.setUpdatedDateColumn("UpdatedDT");

		GTransformSetting transformSetting = new GTransformSettingImpl();
		transformSetting.setEncoding("UTF-8");

		Map<Class<? extends GXFormSetting>, GXFormSetting> mapSetting = new HashMap<>();
		mapSetting.put(GDbCommonSetting.class, dbCommonSetting);
		mapSetting.put(GLocaleCommonSetting.class, localeCommonSetting);
		mapSetting.put(GReservedWordSetting.class, reservedWordSetting);
		mapSetting.put(GTransformSetting.class, transformSetting);

		return mapSetting;
	}

	public static Map<Class<? extends GXFormSetting>, GXFormSetting> getDb2SqlSetting() {
		GDbCommonSetting dbCommonSetting = new GDbCommonSettingImpl();
		dbCommonSetting.setDbType(GDaoDbType.db2);
		dbCommonSetting.setUrl("jdbc:db2://10.149.101.78:50000/testdb");
		dbCommonSetting.setSchema("geframe");
		dbCommonSetting.setUsername("db2inst1");
		dbCommonSetting.setPassword("kccs0000");

		GLocaleCommonSetting localeCommonSetting = new GLocaleCommonSettingImpl();
		GLocaleMappingCommonSetting localeMappingCommonSetting = new GLocaleMappingCommonSettingImpl();
		localeMappingCommonSetting.setCountry1("en");
		localeMappingCommonSetting.setCountry2("ja");
		localeMappingCommonSetting.setCountry3("zh");
		localeCommonSetting.setCurrent("en");
		localeCommonSetting.setMapping(localeMappingCommonSetting);

		GReservedWordSetting reservedWordSetting = new GReservedWordSettingImpl();
		reservedWordSetting.setPrimaryKeyColumn("objectID");
		reservedWordSetting.setExclusiveKeyColumn("ExclusiveFG");
		reservedWordSetting.setCompanyCodeColumn("CompanyCD");
		reservedWordSetting.setInsertedUserIdColumn("RegisteredPerson");
		reservedWordSetting.setInsertedDateColumn("RegisteredDT");
		reservedWordSetting.setUpdatedUserIdColumn("UpdatedPerson");
		reservedWordSetting.setUpdatedDateColumn("UpdatedDT");

		GTransformSetting transformSetting = new GTransformSettingImpl();
		transformSetting.setEncoding("UTF-8");

		Map<Class<? extends GXFormSetting>, GXFormSetting> mapSetting = new HashMap<>();
		mapSetting.put(GDbCommonSetting.class, dbCommonSetting);
		mapSetting.put(GLocaleCommonSetting.class, localeCommonSetting);
		mapSetting.put(GReservedWordSetting.class, reservedWordSetting);
		mapSetting.put(GTransformSetting.class, transformSetting);

		return mapSetting;
	}


	public static Map<Class<? extends GXFormSetting>, GXFormSetting> getOracleSqlSetting() {
		GDbCommonSetting dbCommonSetting = new GDbCommonSettingImpl();
		dbCommonSetting.setDbType(GDaoDbType.oracle);
		dbCommonSetting.setUrl("jdbc:oracle:thin:@gef-integration-ora19150.c3cxjzdf21mz.ap-northeast-1.rds.amazonaws.com:1521:ORCL");
		dbCommonSetting.setSchema("gef_jdbc_tool");
		dbCommonSetting.setUsername("gef_jdbc_tool");
		dbCommonSetting.setPassword("Kccs0000");

		GLocaleCommonSetting localeCommonSetting = new GLocaleCommonSettingImpl();
		GLocaleMappingCommonSetting localeMappingCommonSetting = new GLocaleMappingCommonSettingImpl();
		localeMappingCommonSetting.setCountry1("en");
		localeMappingCommonSetting.setCountry2("ja");
		localeMappingCommonSetting.setCountry3("zh");
		localeCommonSetting.setCurrent("en");
		localeCommonSetting.setMapping(localeMappingCommonSetting);

		GReservedWordSetting reservedWordSetting = new GReservedWordSettingImpl();
		reservedWordSetting.setPrimaryKeyColumn("objectID");
		reservedWordSetting.setExclusiveKeyColumn("ExclusiveFG");
		reservedWordSetting.setCompanyCodeColumn("CompanyCD");
		reservedWordSetting.setInsertedUserIdColumn("RegisteredPerson");
		reservedWordSetting.setInsertedDateColumn("RegisteredDT");
		reservedWordSetting.setUpdatedUserIdColumn("UpdatedPerson");
		reservedWordSetting.setUpdatedDateColumn("UpdatedDT");

		GTransformSetting transformSetting = new GTransformSettingImpl();
		transformSetting.setEncoding("UTF-8");

		Map<Class<? extends GXFormSetting>, GXFormSetting> mapSetting = new HashMap<>();
		mapSetting.put(GDbCommonSetting.class, dbCommonSetting);
		mapSetting.put(GLocaleCommonSetting.class, localeCommonSetting);
		mapSetting.put(GReservedWordSetting.class, reservedWordSetting);
		mapSetting.put(GTransformSetting.class, transformSetting);

		return mapSetting;
	}
}
