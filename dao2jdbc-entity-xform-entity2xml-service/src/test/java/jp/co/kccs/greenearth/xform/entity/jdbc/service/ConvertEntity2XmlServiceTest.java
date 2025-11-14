package jp.co.kccs.greenearth.xform.entity.jdbc.service;

import org.junit.Test;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import static jp.co.kccs.greenearth.xform.entity.jdbc.Utils.getParameterValue;
import static jp.co.kccs.greenearth.xform.entity.jdbc.utility.GUtils.parseStringToMap;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class ConvertEntity2XmlServiceTest {

	@Test
	public void testHandle() {
		ConvertEntity2XmlService function = new ConvertEntity2XmlService();
		{
			String output = function.handle(null);
			assertNull(output);
		}
		{
			String output = function.handle(new HashMap<>());
			String decodedOutput = new String(Base64.getDecoder().decode(output));
			assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" + System.lineSeparator() +
					"<Entity>" + System.lineSeparator() +
					"  <Columns/>" + System.lineSeparator() +
					"</Entity>" + System.lineSeparator(), decodedOutput);
		}
		{
			Map<String, Object> inputParameter = parseStringToMap(getParameterValue());
			String output = function.handle(inputParameter);
			String decodedOutput = new String(Base64.getDecoder().decode(output));
			assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" + System.lineSeparator() +
					"<Entity database=\"gef_jdbc_tool\" phyName=\"jdbc_tool_test_fullCaseMainEntity\">" + System.lineSeparator() + 
					"  <Columns>" + System.lineSeparator() + 
					"    <Column name=\"objectID\" phyName=\"objectID\" type=\"Varchar\" size=\"32\"/>" + System.lineSeparator() + 
					"    <Column name=\"StringColumn\" phyName=\"StringColumn\" type=\"Varchar\" size=\"32\"/>" + System.lineSeparator() + 
					"    <Column name=\"IntColumn\" phyName=\"IntColumn\" type=\"Integer\" size=\"8\"/>" + System.lineSeparator() + 
					"    <Column name=\"NStringColumn\" phyName=\"NStringColumn\" type=\"Varchar\" size=\"32\"/>" + System.lineSeparator() + 
					"    <Column name=\"DateTimeColumn\" phyName=\"DateTimeColumn\" type=\"Date\"/>" + System.lineSeparator() + 
					"    <Column name=\"YMColumn\" phyName=\"YMColumn\" type=\"Varchar\" size=\"6\"/>" + System.lineSeparator() + 
					"    <Column name=\"CurrencyColumn\" phyName=\"CurrencyColumn\" type=\"Double\" size=\"32\" scale=\"2\"/>" + System.lineSeparator() + 
					"    <Column name=\"LongColumn\" phyName=\"LongColumn\" type=\"Long\" size=\"32\"/>" + System.lineSeparator() + 
					"    <Column name=\"CompanyCD\" phyName=\"CompanyCD\" type=\"Varchar\" size=\"32\"/>" + System.lineSeparator() + 
					"    <Column name=\"ExclusiveFG\" phyName=\"ExclusiveFG\" type=\"Varchar\" size=\"32\" exclusiveKey=\"true\"/>" + System.lineSeparator() + 
					"    <Column name=\"RegisteredPerson\" phyName=\"RegisteredPerson\" type=\"Varchar\" size=\"32\"/>" + System.lineSeparator() + 
					"    <Column name=\"RegisteredDT\" phyName=\"RegisteredDT\" type=\"Date\" generate=\"RegisteredDate\"/>" + System.lineSeparator() + 
					"    <Column name=\"UpdatedPerson\" phyName=\"UpdatedPerson\" type=\"Varchar\" size=\"32\"/>" + System.lineSeparator() + 
					"    <Column name=\"UpdatedDT\" phyName=\"UpdatedDT\" type=\"Date\" generate=\"UpdatedDate\"/>" + System.lineSeparator() + 
					"  </Columns>" + System.lineSeparator() + 
					"  <PrimaryKey>" + System.lineSeparator() + 
					"    <ReferenceColumn refName=\"objectID\"/>" + System.lineSeparator() + 
					"  </PrimaryKey>" + System.lineSeparator() + 
					"  <UniqueKeys>" + System.lineSeparator() + 
					"    <UniqueKey>" + System.lineSeparator() + 
					"      <ReferenceColumn refName=\"StringColumn\"/>" + System.lineSeparator() + 
					"      <ReferenceColumn refName=\"IntColumn\"/>" + System.lineSeparator() + 
					"      <ReferenceColumn refName=\"NStringColumn\"/>" + System.lineSeparator() + 
					"    </UniqueKey>" + System.lineSeparator() + 
					"  </UniqueKeys>" + System.lineSeparator() + 
					"  <ForeignKeys>" + System.lineSeparator() + 
					"    <ForeignKey refEntity=\"jdbc_tool_test_fullCaseLeftJoinEntity\">" + System.lineSeparator() + 
					"      <JoinColumn refName=\"IntColumnLeft\" srcName=\"IntColumn\"/>" + System.lineSeparator() + 
					"      <JoinColumn refName=\"LongColumnLeft\" srcName=\"-\" constValue=\"8001\"/>" + System.lineSeparator() + 
					"    </ForeignKey>" + System.lineSeparator() + 
					"  </ForeignKeys>" + System.lineSeparator() + 
					"</Entity>" + System.lineSeparator(), decodedOutput);
		}
	}

}
