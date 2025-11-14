package jp.co.kccs.greenearth.xform.entity.jdbc.function;

import jp.co.kccs.greenearth.commons.GFrameworkException;
import jp.co.kccs.greenearth.xform.entity.dao.core.loader.GDatabaseEntityMetadataLoader;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FetchEntityIdByDatabaseIdFunction {
	public List<String> handle(String input) {
		return fetchCategoryByVeId(input);
	}

	private List<String> fetchCategoryByVeId(String databaseId) {
		GDatabaseEntityMetadataLoader databaseEntityMetadataLoader = new GDatabaseEntityMetadataLoader();
		return databaseEntityMetadataLoader.load(databaseId);
	}
}
