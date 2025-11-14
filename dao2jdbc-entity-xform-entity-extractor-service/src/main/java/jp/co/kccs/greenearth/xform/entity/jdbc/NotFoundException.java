package jp.co.kccs.greenearth.xform.entity.jdbc;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class NotFoundException extends RuntimeException {
	private String message;
}
