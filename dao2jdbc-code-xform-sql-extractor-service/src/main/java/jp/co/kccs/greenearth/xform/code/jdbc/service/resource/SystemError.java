package jp.co.kccs.greenearth.xform.code.jdbc.service.resource;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SystemError {
	private OffsetDateTime timeStamp;
	private String statusCode;
	private String message;

}
