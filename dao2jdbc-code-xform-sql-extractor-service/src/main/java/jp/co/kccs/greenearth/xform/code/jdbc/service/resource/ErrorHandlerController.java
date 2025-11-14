package jp.co.kccs.greenearth.xform.code.jdbc.service.resource;

import jp.co.kccs.greenearth.commons.GFrameworkException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.time.OffsetDateTime;
import java.time.ZoneId;

@RestControllerAdvice
public class ErrorHandlerController {
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiResponse<String>> handleException(Exception exception) {
		SystemError error = new SystemError();
		error.setTimeStamp(OffsetDateTime.now(ZoneId.of("UTC")));
		error.setStatusCode("500");
		error.setMessage(exception.getMessage());
		return ResponseEntity.status(500).body(new ApiResponse<>(error));
	}
}
