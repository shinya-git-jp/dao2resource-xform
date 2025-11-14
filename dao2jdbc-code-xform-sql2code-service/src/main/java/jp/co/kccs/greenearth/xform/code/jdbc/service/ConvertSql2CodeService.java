package jp.co.kccs.greenearth.xform.code.jdbc.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jp.co.kccs.greenearth.xform.code.dao.core.GDao2JdbcXFormCodeResult;
import jp.co.kccs.greenearth.xform.code.dao.core.GDao2XFormScriptConverter;
import jp.co.kccs.greenearth.xform.code.jdbc.resource.Dao2SqlResultRequest;
import jp.co.kccs.greenearth.xform.code.jdbc.core.service.GXFormCodeTransformService;
import jp.co.kccs.greenearth.xform.code.jdbc.core.service.GXFormScript;
import java.util.*;

public class ConvertSql2CodeService {
	public GDao2JdbcXFormCodeResult handle(Map<String, Object> parameter) {
		if (Objects.isNull(parameter)) {
			return null;
		}
		GXFormCodeTransformService transformService = GXFormCodeTransformService.getInstance();
		GDao2XFormScriptConverter xFormScriptConverter = GDao2XFormScriptConverter.getInstance();
		ObjectMapper objectMapper = new ObjectMapper();
		Dao2SqlResultRequest request = objectMapper.convertValue(parameter, Dao2SqlResultRequest.class);
		GXFormScript script = xFormScriptConverter.convert(request.getSqlScript(), request.getType(), request.getVirtualEntity());
		return GDao2JdbcXFormCodeResult.from(transformService.transform(script), request.getVirtualEntity());
	}
}
