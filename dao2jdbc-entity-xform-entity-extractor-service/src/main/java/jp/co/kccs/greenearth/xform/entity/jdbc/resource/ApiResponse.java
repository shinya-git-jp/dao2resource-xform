package jp.co.kccs.greenearth.xform.entity.jdbc.resource;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiResponse<T> {
	private boolean success;
	private SystemError error;
	private T data;

	public ApiResponse(T data) {
		this.success = true;
		this.data = data;
	}
	public ApiResponse(SystemError error) {
		this.success = false;
		this.error = error;
	}
}
