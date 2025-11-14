package jp.co.kccs.greenearth.xform.code.jdbc.resource;

import jp.co.kccs.greenearth.framework.data.GSystemError;
import lombok.Getter;
import lombok.Setter;

@Getter
public class ApiResponse<T> {
	private boolean success;
	private GSystemError error;
	private T data;

	public ApiResponse(T data) {
		this.success = true;
		this.data = data;
	}
	public ApiResponse(GSystemError error) {
		this.success = false;
		this.error = error;
	}
}
