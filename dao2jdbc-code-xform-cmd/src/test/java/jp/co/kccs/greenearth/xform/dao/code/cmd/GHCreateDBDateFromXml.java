package jp.co.kccs.greenearth.xform.dao.code.cmd;

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
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GHCreateDBDateFromXml {

	public static Connection connection = null;

	@Test
	public void createTestData() throws Exception {

		String filePath = "D:\\github\\dao2\\gef-dao2jdbc-xform\\dao2jdbc-code-xform-cmd";
		String filePath2 = "D:\\github\\dao2\\gef-dao2jdbc-xform\\dao2jdbc-xform-test-utils\\src\\main\\resources\\dbunit\\import_common.xml";
		String filePath3 = "D:\\github\\dao2\\gef-dao2jdbc-xform\\dao2jdbc-xform-test-utils\\src\\main\\resources\\dbunit\\import_database_mysql_common.xml";

		File file = new File(filePath);
		File file2 = new File(filePath2);
		File file3 = new File(filePath3);
		List<String> entityMapFiles = new ArrayList<>();
		getEntityMapXml(entityMapFiles, file);
		getEntityMapXml(entityMapFiles, file2);
		getEntityMapXml(entityMapFiles, file3);

		List<String> str = new ArrayList<>();
		entityMapFiles.stream().forEach(entityMapFile-> {
			try (FileInputStream fis = new FileInputStream(entityMapFile);
				 InputStreamReader isr = new InputStreamReader(fis);
				 BufferedReader br = new BufferedReader(isr)) {

				String line="";
				while ((line = br.readLine()) != null) {
					if (!str.contains(line.trim()) && !"</dataset>".equals(line.trim())) {
						str.add(line.trim());
						str.add("\n");
					}
				}
			} catch (FileNotFoundException e) {
				throw new RuntimeException(e);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		});
		str.add("</dataset>");

		FileWriter writer = new FileWriter("dbTestData.xml");
		str.stream().forEach(ss->{
			try {
				writer.write(ss);
			} catch (IOException e) {
			}
		});
		writer.close();

		catchMySqlConnection();
		cleanInsert("mysql", "dbTestData.xml", "gef_jdbc_tool");
	}

	public void cleanInsert(String dbType, String filePath, String schema) throws Exception {

		FlatXmlDataSetBuilder builder = new FlatXmlDataSetBuilder();
		builder.setColumnSensing(true);
		IDataSet dataset = builder.build(new File(filePath));

		IDatabaseConnection conn;
		if(dbType.equals("mysql")) {
			conn = new MySqlConnection(connection, schema);
			conn.getConfig().setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new MySqlDataTypeFactory());
			conn.getConfig().setProperty(DatabaseConfig.PROPERTY_ESCAPE_PATTERN , "`?`");
		} else if (dbType.equals("oracle")) {
			conn = new OracleConnection(connection, schema);
		} else if (dbType.equals("db2")) {
			conn = new Db2Connection(connection, schema);
		} else {
			throw new DatabaseUnitException("unknown type");
		}
		DatabaseOperation.CLEAN_INSERT.execute(conn, dataset);
	}

	public static void catchMySqlConnection() throws Exception {
		Class.forName("com.mysql.cj.jdbc.Driver");
		connection = DriverManager.getConnection("jdbc:mysql://gef-unit-mysql8027.c3cxjzdf21mz.ap-northeast-1.rds.amazonaws.com:3306/gef_jdbc_tool?zeroDateTimeBehavior=convertToNull&useUnicode=true&useSSL=false&serverTimezone=GMT%2B9","geframe","Kccs0000");
	}

	public static void catchOracleConnection() throws Exception {
		Class.forName("oracle.jdbc.driver.OracleDriver");
		connection = DriverManager.getConnection("jdbc:oracle:thin:@//gef-integration-ora19150.c3cxjzdf21mz.ap-northeast-1.rds.amazonaws.com:1521/ORCL","gef_jdbc_tool","Kccs0000");
	}

	public static void catchDB2Connection() throws Exception {
		Class.forName("com.ibm.db2.jcc.DB2Driver");
		connection = DriverManager.getConnection("jdbc:db2://10.149.101.78:50000/testdb","db2inst1","jissou0000");
	}

	public static void getEntityMapXml(List<String> files, File file) {
		if (file.isFile()) {
			if (file.getName().contains("import_")) {
				files.add(file.getAbsolutePath());
			}
		} else {
			Arrays.stream(file.listFiles()).forEach(f->{
				if (f.isDirectory()) {
					getEntityMapXml(files, f);
				} else {
					if (f.getName().contains("import_")) {
						files.add(f.getAbsolutePath());
					}
				}
			});
		}
	}
}
