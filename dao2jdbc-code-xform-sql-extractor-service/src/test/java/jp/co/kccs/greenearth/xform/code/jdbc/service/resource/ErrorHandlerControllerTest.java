package jp.co.kccs.greenearth.xform.code.jdbc.service.resource;

import jp.co.kccs.greenearth.commons.GFrameworkException;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

public class ErrorHandlerControllerTest {

	/**
	 * 例外オブジェクトが渡される場合、{@link ApiResponse}が正しく作成されること。<br>
	 */
	@Test
	public void testReturnErrorResponse() {
		ErrorHandlerController errorHandlerController = new ErrorHandlerController();
		ResponseEntity<ApiResponse<String>> responseEntity = errorHandlerController.handleException(new GFrameworkException("test"));

		ApiResponse<String> response = responseEntity.getBody();
		assertEquals(SystemError.class, response.getError().getClass());
		assertNull(response.getData());
		assertFalse(response.isSuccess());
		assertEquals("test", response.getError().getMessage());
		assertEquals("500", response.getError().getStatusCode());
	}
}
