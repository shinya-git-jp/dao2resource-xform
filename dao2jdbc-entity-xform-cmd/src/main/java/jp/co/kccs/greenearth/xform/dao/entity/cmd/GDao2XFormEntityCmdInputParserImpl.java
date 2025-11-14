package jp.co.kccs.greenearth.xform.dao.entity.cmd;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import jp.co.kccs.greenearth.commons.GFrameworkException;
import jp.co.kccs.greenearth.xform.entity.dao.core.loader.GDatabaseEntityMetadataLoader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import static jp.co.kccs.greenearth.xform.dao.common.GDao2Utils.executeSQL;
import static jp.co.kccs.greenearth.xform.dao.common.GDao2Utils.getSchema;

public class GDao2XFormEntityCmdInputParserImpl implements GDao2XFormEntityCmdInputParser {
	@Override
	public List<String> parse(String filePath, String encoding) {
		if (Objects.isNull(encoding) || encoding.isEmpty()) {
			encoding = StandardCharsets.UTF_8.name();
		}
		ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
		List<Map<String, Object>> yamlObj = null;
		try {
			File file = new File(filePath);
			InputStreamReader reader = new InputStreamReader(new FileInputStream(file), encoding);
			yamlObj = mapper.readValue(reader, List.class);
		} catch (IOException e) {
			throw new GFrameworkException(e);
		}
		return convert(yamlObj);
	}

	private List<String> convert(List<Map<String, Object>> objects) {
		List<String> convertResults = new ArrayList<>();
		objects.forEach(object-> {
			convertResults.addAll(convert(object));
		});
		return convertResults;
	}
	private List<String> convert(Map<String, Object> object) {
		List<String> result = new ArrayList<>();
		if (object.containsKey("entityId")) {
			result.add((String) object.get("entityId"));
		} else {
			GDatabaseEntityMetadataLoader databaseEntityMetadataLoader = new GDatabaseEntityMetadataLoader();
			result.addAll(databaseEntityMetadataLoader.load((String) object.get("databaseId")));
		}
		return result;
	}
}
