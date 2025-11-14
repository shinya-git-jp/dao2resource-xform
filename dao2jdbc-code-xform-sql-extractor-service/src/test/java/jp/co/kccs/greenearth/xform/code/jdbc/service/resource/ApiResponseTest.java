package jp.co.kccs.greenearth.xform.code.jdbc.service.resource;

import org.junit.Test;

import java.time.OffsetDateTime;
import java.time.ZoneId;

import static org.junit.Assert.*;

public class ApiResponseTest {

	/**
	 * [1] {@link ApiResponse#ApiResponse(Object)}で作成する場合、{@link ApiResponse#getData()}が設定され、{@link ApiResponse#isSuccess()} はtrueになること。<br>
	 * [2] {@link ApiResponse#ApiResponse(SystemError)}で作成する場合、{@link ApiResponse#getError()}データが設定され、{@link ApiResponse#isSuccess()} はfalseになること。<br>
	 */
	@Test
	public void testGet() {
		{
			ApiResponse<String> apiResponse = new ApiResponse<>("test");
			assertEquals("test", apiResponse.getData());
			assertNull(apiResponse.getError());
			assertTrue(apiResponse.isSuccess());
		}
		{
			SystemError systemError = new SystemError();
			systemError.setTimeStamp(OffsetDateTime.now(ZoneId.of("UTC")));
			systemError.setStatusCode("500");
			systemError.setMessage("error");
			ApiResponse<String> apiResponse = new ApiResponse<>(systemError);
			assertNull(apiResponse.getData());
			assertEquals(systemError, apiResponse.getError());
			assertFalse(apiResponse.isSuccess());
		}
	}
}
