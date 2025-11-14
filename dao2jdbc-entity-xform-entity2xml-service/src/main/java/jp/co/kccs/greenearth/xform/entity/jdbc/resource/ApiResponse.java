package jp.co.kccs.greenearth.xform.entity.jdbc.resource;

import jp.co.kccs.greenearth.framework.data.GSystemError;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
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
