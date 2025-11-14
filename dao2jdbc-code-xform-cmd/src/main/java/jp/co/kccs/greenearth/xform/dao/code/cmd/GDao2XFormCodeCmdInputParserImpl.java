package jp.co.kccs.greenearth.xform.dao.code.cmd;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import jp.co.kccs.greenearth.commons.GFrameworkException;
import jp.co.kccs.greenearth.xform.code.dao.core.GCrudType;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class GDao2XFormCodeCmdInputParserImpl implements GDao2XFormCodeCmdInputParser {
	@Override
	public List<GDao2XFormCodeParameter> parse(String filePath, String encoding) throws IOException {
		if (Objects.isNull(encoding) || encoding.isEmpty()) {
			encoding = StandardCharsets.UTF_8.name();
		}
		ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
		File file = new File(filePath);
		InputStreamReader reader = new InputStreamReader(new FileInputStream(file), encoding);
		List<Map<String, Object>> yamlObj = mapper.readValue(reader, List.class);
		return convert(yamlObj);
	}
	private GDao2XFormCodeParameter convertAsVeId(Map<String, Object> object) {
		GDao2XFormCodeParameter parameter = new GDao2XFormCodeParameterImpl();
		parameter.setId((String) object.get("veId"));
		parameter.setCrudTypes(getCrudTypes(object));
		parameter.setCategoryFlag(false);
		return parameter;
	}
	private GDao2XFormCodeParameter convertAsCategory(Map<String, Object> object) {
		GDao2XFormCodeParameter parameter = new GDao2XFormCodeParameterImpl();
		parameter.setId((String) object.get("categoryId"));
		parameter.setCrudTypes(getCrudTypes(object));
		parameter.setCategoryFlag(true);
		return parameter;
	}
	private List<GCrudType> getCrudTypes(Map<String, Object> object) {
		if (!object.containsKey("type")) {
			return List.of(GCrudType.SELECT, GCrudType.UPDATE, GCrudType.INSERT, GCrudType.DELETE);
		}
		return ((List<String>) object.get("type")).stream().map(GCrudType::valueOf).toList();
	}
	private GDao2XFormCodeParameter convert(Map<String, Object> object) {
		if (object.containsKey("veId")) {
			return convertAsVeId(object);
		} else if (object.containsKey("categoryId")) {
			return convertAsCategory(object);
		}
		throw new GFrameworkException("The item should contains at least 1 key (veId or categoryId)");
	}
	private List<GDao2XFormCodeParameter> convert(List<Map<String, Object>> objects) {
		List<GDao2XFormCodeParameter> results = new ArrayList<>();
		objects.forEach(object-> {
			results.add(convert(object));
		});
		return results;
	}
}
