package jp.co.kccs.greenearth.xform.code.jdbc.resource;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import jp.co.kccs.greenearth.framework.data.GSystemError;
import jp.co.kccs.greenearth.xform.code.jdbc.resource.ApiResponse;

import java.time.OffsetDateTime;
import java.time.ZoneId;

@Provider
public class GeneralExceptionMapper implements jakarta.ws.rs.ext.ExceptionMapper<Exception> {
	@Override
	public Response toResponse(Exception exception) {
		GSystemError error = GSystemError.create();
		error.setTimeStamp(OffsetDateTime.now(ZoneId.of("UTC")));
		error.setStatusCode(500);
		error.setMessage(exception.getMessage());
		return Response.status(500).entity(
				new ApiResponse<>(error)
		).build();
	}
}
