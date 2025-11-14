package jp.co.kccs.greenearth.xform.code.jdbc.service;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class NotFoundException extends RuntimeException {
	private String message;
}
