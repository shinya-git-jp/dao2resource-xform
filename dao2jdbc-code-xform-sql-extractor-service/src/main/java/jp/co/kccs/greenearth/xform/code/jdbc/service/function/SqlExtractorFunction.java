package jp.co.kccs.greenearth.xform.code.jdbc.service.function;

import jp.co.kccs.greenearth.commons.GFrameworkException;
import jp.co.kccs.greenearth.xform.code.dao.core.GCrudType;
import jp.co.kccs.greenearth.xform.code.dao.core.service.GDao2XFormCodeTransform;
import jp.co.kccs.greenearth.xform.code.dao.core.service.GDao2XFormSqlResult;
import jp.co.kccs.greenearth.xform.code.jdbc.service.resource.Dao2SqlResultResponse;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@Component
public class SqlExtractorFunction {
	public List<Dao2SqlResultResponse> handle(Map<String, Object> parameter) {
		if (!parameter.containsKey("veId")) {
			throw new GFrameworkException("veId parameters must be exist");
		}
		String veId = (String) parameter.get("veId");
		Object crudTypesParam = parameter.get("crudTypes");
		List<GCrudType> crudTypes = new ArrayList<>();
		if (Objects.nonNull(crudTypesParam) && crudTypesParam instanceof List) {
			((List<String>) crudTypesParam).forEach(crudType-> {
				crudTypes.add(GCrudType.valueOf(crudType));
			});
		} else {
			crudTypes.add(GCrudType.INSERT);
			crudTypes.add(GCrudType.SELECT);
			crudTypes.add(GCrudType.UPDATE);
			crudTypes.add(GCrudType.DELETE);
		}
		List<Dao2SqlResultResponse> results = new ArrayList<>();
		try {
			crudTypes.forEach(crudType -> {
				GDao2XFormSqlResult result = GDao2XFormCodeTransform.getInstance().transformByVeId(veId, crudType);
				if (Objects.nonNull(result.getSqlScript())) {
					results.add(new Dao2SqlResultResponse(result));
				}
			});
		} catch (Exception exception) {
			throw new GFrameworkException(exception);
		}
		return results;
	}
}
