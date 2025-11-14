package jp.co.kccs.greenearth.xform.entity.jdbc.resource;

import jakarta.ws.rs.core.Response;
import jp.co.kccs.greenearth.framework.data.GValidateException;
import org.junit.Test;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import static jp.co.kccs.greenearth.xform.entity.jdbc.Utils.getParameterValue;
import static jp.co.kccs.greenearth.xform.entity.jdbc.utility.GUtils.parseStringToMap;
import static org.junit.Assert.*;

public class EntityResourceTest {

	/**
	 * [1] 全てのリクエストフィルドがある場合、変換が成功すること。<br>
	 * [2] 設定が空の場合、{@link GValidateException}が発生すること。<br>
	 * [3] valueが空の場合、{@link GValidateException}が発生すること.
	 */
	@Test
	public void testConvertEntity2Xml() throws Exception {
		EntityResource codeResource = new EntityResource();
		{
			EntityRequest codeRequest = new EntityRequest();
			codeRequest.setSetting(getSetting());
			codeRequest.setValue(getValue());
			Response response = codeResource.convertSql2Code(codeRequest);
			assertNotNull(response.getEntity());
			assertTrue(response.getEntity() instanceof ApiResponse<?>);
			ApiResponse<String> apiResponse = (ApiResponse<String>) response.getEntity();
			assertTrue(apiResponse.isSuccess());
			assertEquals(200, response.getStatus());
			assertNull(apiResponse.getError());
			assertNotNull(apiResponse.getData());
			String decodedOutput = new String(Base64.getDecoder().decode(apiResponse.getData()));
			assertEquals("PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVRGLTgiIHN0YW5kYWxvbmU9InllcyI/Pg0KPEVudGl0eSBkYXRhYmFzZT0iZ2VmX2pkYmNfdG9vbCIgcGh5TmFtZT0iamRiY190b29sX3Rlc3RfZnVsbENhc2VNYWluRW50aXR5Ij4NCiAgPENvbHVtbnM+DQogICAgPENvbHVtbiBuYW1lPSJvYmplY3RJRCIgcGh5TmFtZT0ib2JqZWN0SUQiIHR5cGU9IlZhcmNoYXIiIHNpemU9IjMyIi8+DQogICAgPENvbHVtbiBuYW1lPSJTdHJpbmdDb2x1bW4iIHBoeU5hbWU9IlN0cmluZ0NvbHVtbiIgdHlwZT0iVmFyY2hhciIgc2l6ZT0iMzIiLz4NCiAgICA8Q29sdW1uIG5hbWU9IkludENvbHVtbiIgcGh5TmFtZT0iSW50Q29sdW1uIiB0eXBlPSJJbnRlZ2VyIiBzaXplPSI4Ii8+DQogICAgPENvbHVtbiBuYW1lPSJOU3RyaW5nQ29sdW1uIiBwaHlOYW1lPSJOU3RyaW5nQ29sdW1uIiB0eXBlPSJWYXJjaGFyIiBzaXplPSIzMiIvPg0KICAgIDxDb2x1bW4gbmFtZT0iRGF0ZVRpbWVDb2x1bW4iIHBoeU5hbWU9IkRhdGVUaW1lQ29sdW1uIiB0eXBlPSJEYXRlIi8+DQogICAgPENvbHVtbiBuYW1lPSJZTUNvbHVtbiIgcGh5TmFtZT0iWU1Db2x1bW4iIHR5cGU9IlZhcmNoYXIiIHNpemU9IjYiLz4NCiAgICA8Q29sdW1uIG5hbWU9IkN1cnJlbmN5Q29sdW1uIiBwaHlOYW1lPSJDdXJyZW5jeUNvbHVtbiIgdHlwZT0iRG91YmxlIiBzaXplPSIzMiIgc2NhbGU9IjIiLz4NCiAgICA8Q29sdW1uIG5hbWU9IkxvbmdDb2x1bW4iIHBoeU5hbWU9IkxvbmdDb2x1bW4iIHR5cGU9IkxvbmciIHNpemU9IjMyIi8+DQogICAgPENvbHVtbiBuYW1lPSJDb21wYW55Q0QiIHBoeU5hbWU9IkNvbXBhbnlDRCIgdHlwZT0iVmFyY2hhciIgc2l6ZT0iMzIiLz4NCiAgICA8Q29sdW1uIG5hbWU9IkV4Y2x1c2l2ZUZHIiBwaHlOYW1lPSJFeGNsdXNpdmVGRyIgdHlwZT0iVmFyY2hhciIgc2l6ZT0iMzIiIGV4Y2x1c2l2ZUtleT0idHJ1ZSIvPg0KICAgIDxDb2x1bW4gbmFtZT0iUmVnaXN0ZXJlZFBlcnNvbiIgcGh5TmFtZT0iUmVnaXN0ZXJlZFBlcnNvbiIgdHlwZT0iVmFyY2hhciIgc2l6ZT0iMzIiLz4NCiAgICA8Q29sdW1uIG5hbWU9IlJlZ2lzdGVyZWREVCIgcGh5TmFtZT0iUmVnaXN0ZXJlZERUIiB0eXBlPSJEYXRlIiBnZW5lcmF0ZT0iUmVnaXN0ZXJlZERhdGUiLz4NCiAgICA8Q29sdW1uIG5hbWU9IlVwZGF0ZWRQZXJzb24iIHBoeU5hbWU9IlVwZGF0ZWRQZXJzb24iIHR5cGU9IlZhcmNoYXIiIHNpemU9IjMyIi8+DQogICAgPENvbHVtbiBuYW1lPSJVcGRhdGVkRFQiIHBoeU5hbWU9IlVwZGF0ZWREVCIgdHlwZT0iRGF0ZSIgZ2VuZXJhdGU9IlVwZGF0ZWREYXRlIi8+DQogIDwvQ29sdW1ucz4NCiAgPFByaW1hcnlLZXk+DQogICAgPFJlZmVyZW5jZUNvbHVtbiByZWZOYW1lPSJvYmplY3RJRCIvPg0KICA8L1ByaW1hcnlLZXk+DQogIDxVbmlxdWVLZXlzPg0KICAgIDxVbmlxdWVLZXk+DQogICAgICA8UmVmZXJlbmNlQ29sdW1uIHJlZk5hbWU9IlN0cmluZ0NvbHVtbiIvPg0KICAgICAgPFJlZmVyZW5jZUNvbHVtbiByZWZOYW1lPSJJbnRDb2x1bW4iLz4NCiAgICAgIDxSZWZlcmVuY2VDb2x1bW4gcmVmTmFtZT0iTlN0cmluZ0NvbHVtbiIvPg0KICAgIDwvVW5pcXVlS2V5Pg0KICA8L1VuaXF1ZUtleXM+DQogIDxGb3JlaWduS2V5cz4NCiAgICA8Rm9yZWlnbktleSByZWZFbnRpdHk9ImpkYmNfdG9vbF90ZXN0X2Z1bGxDYXNlTGVmdEpvaW5FbnRpdHkiPg0KICAgICAgPEpvaW5Db2x1bW4gcmVmTmFtZT0iSW50Q29sdW1uTGVmdCIgc3JjTmFtZT0iSW50Q29sdW1uIi8+DQogICAgICA8Sm9pbkNvbHVtbiByZWZOYW1lPSJMb25nQ29sdW1uTGVmdCIgc3JjTmFtZT0iLSIgY29uc3RWYWx1ZT0iODAwMSIvPg0KICAgIDwvRm9yZWlnbktleT4NCiAgPC9Gb3JlaWduS2V5cz4NCjwvRW50aXR5Pg0K",
					apiResponse.getData());
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
		{
			EntityRequest codeRequest = new EntityRequest();
			codeRequest.setValue(getValue());
			try {
				codeResource.convertSql2Code(codeRequest);
				fail();
			} catch (Exception exception) {
				assertTrue(exception instanceof GValidateException);
				assertEquals("Setting is required.", exception.getMessage());
			}
		}
		{
			EntityRequest codeRequest = new EntityRequest();
			codeRequest.setSetting(getSetting());
			try {
				codeResource.convertSql2Code(codeRequest);
				fail();
			} catch (Exception exception) {
				assertTrue(exception instanceof GValidateException);
				assertEquals("Value is required.", exception.getMessage());
			}
		}
	}

	private static Map<String, Object> getValue() {
		return parseStringToMap(getParameterValue());
	}
	private static SettingRequest getSetting() {
		Map<String, Object> transform = new HashMap<>();
		transform.put("encoding", "UTF-8");
		transform.put("useForeignKey", true);
		transform.put("forceAliasColumn", false);
		transform.put("entityQuery", false);
		transform.put("useExpMap", true);
		SettingRequest settingRequest = new SettingRequest();
		settingRequest.setTransform(transform);

		return settingRequest;
	}
}
