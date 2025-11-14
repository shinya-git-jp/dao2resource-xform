package jp.co.kccs.greenearth.xform.code.jdbc.resource;

import jakarta.ws.rs.core.Response;
import jp.co.kccs.greenearth.commons.GFrameworkException;
import jp.co.kccs.greenearth.framework.data.GSystemError;
import org.junit.Test;

import static org.junit.Assert.*;

public class GeneralExceptionMapperTest {

	/**
	 * 例外オブジェクトが渡される場合、{@link Response}が正しく作成されること。<br>
	 */
	@Test
	public void testReturnErrorResponse() {
		GeneralExceptionMapper generalExceptionMapper = new GeneralExceptionMapper();
		Response response = generalExceptionMapper.toResponse(new GFrameworkException("test"));

		assertEquals(ApiResponse.class, response.getEntity().getClass());
		ApiResponse<String> apiResponse = (ApiResponse<String>) response.getEntity();
		assertEquals(GSystemError.class, apiResponse.getError().getClass());
		assertNull(apiResponse.getData());
		assertFalse(apiResponse.isSuccess());
		assertEquals("test", apiResponse.getError().getMessage().get());
		assertEquals((Integer)500, apiResponse.getError().getStatusCode().get());
	}
}
