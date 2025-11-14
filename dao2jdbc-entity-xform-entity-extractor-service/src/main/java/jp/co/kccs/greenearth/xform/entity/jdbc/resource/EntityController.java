package jp.co.kccs.greenearth.xform.entity.jdbc.resource;


import jp.co.kccs.greenearth.framework.data.GValidateException;
import jp.co.kccs.greenearth.xform.configuration.GDbCommonSetting;
import jp.co.kccs.greenearth.xform.configuration.GTransformSetting;
import jp.co.kccs.greenearth.xform.configuration.GXFormSetting;
import jp.co.kccs.greenearth.xform.configuration.GXFormSettingHolder;
import jp.co.kccs.greenearth.xform.dao.common.GDao2Utils;
import jp.co.kccs.greenearth.xform.dao.dto.GDao2VirtualEntity;
import jp.co.kccs.greenearth.xform.entity.jdbc.function.EntityExtractorFunction;
import jp.co.kccs.greenearth.xform.entity.jdbc.function.EntityIdExistenceFunction;
import jp.co.kccs.greenearth.xform.entity.jdbc.function.FetchEntityIdByDatabaseIdFunction;
import jp.co.kccs.greenearth.xform.entity.jdbc.NotFoundException;
import jp.co.kccs.greenearth.xform.old.configuration.GLocaleCommonSetting;
import jp.co.kccs.greenearth.xform.old.configuration.GReservedWordSetting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
public class EntityController {
	@Autowired
	private EntityExtractorFunction entityExtractorFunction;
	@Autowired
	private FetchEntityIdByDatabaseIdFunction fetchEntityIdByDatabaseIdFunction;
	@Autowired
	private EntityIdExistenceFunction entityIdExistenceFunction;

	public EntityController() {}

	EntityController(EntityExtractorFunction entityExtractorFunction,
					 FetchEntityIdByDatabaseIdFunction fetchEntityIdByDatabaseIdFunction,
					 EntityIdExistenceFunction entityIdExistenceFunction) {
		this.entityExtractorFunction = entityExtractorFunction;
		this.fetchEntityIdByDatabaseIdFunction = fetchEntityIdByDatabaseIdFunction;
		this.entityIdExistenceFunction = entityIdExistenceFunction;
	}

	@PostMapping("/entities/transformations/entity-extractors")
	public ApiResponse<GDao2VirtualEntity.Entity> extractEntity(@RequestBody EntityRequest request) throws Exception {
		validateRequest(request);
		setSetting(request.getSetting());
		return new ApiResponse<>(entityExtractorFunction.handle(request.getValue()));
	}

	@PostMapping("/entities/databases/children")
	public ApiResponse<List<String>> fetchEntityIdByDatabaseId(@RequestBody EntityRequest request) throws Exception {
		validateRequest(request);
		setSetting(request.getSetting());
			List<String> output = fetchEntityIdByDatabaseIdFunction.handle((String) request.getValue().get("databaseId"));
			return new ApiResponse<>(output);
	}

	@PostMapping("/entities/ids")
	public ApiResponse<String> checkIfIdIsExist(@RequestBody EntityRequest request) throws Exception {
		validateRequest(request);
		setSetting(request.getSetting());
		try {
			String output = entityIdExistenceFunction.handle(request.getValue());
			return new ApiResponse<>(output);
		} catch (NotFoundException notFoundException) {
			return new ApiResponse<>(notFoundException.getMessage());
		}
	}

	private void validateRequest(EntityRequest request) throws GValidateException {
		if (Objects.isNull(request.getSetting())) {
			throw new GValidateException("Setting is required.");
		}
		if (Objects.isNull(request.getValue())) {
			throw new GValidateException("Value is required.");
		}
	}
	private static void setSetting(SettingRequest setting) {
		Map<Class<?>, GXFormSetting> xformSetting = setting.toDaoCommonSetting();
		GXFormSettingHolder.setCommonSetting(GTransformSetting.class, xformSetting.get(GTransformSetting.class));
		GXFormSettingHolder.setCommonSetting(GDbCommonSetting.class, xformSetting.get(GDbCommonSetting.class));
		GXFormSettingHolder.setCommonSetting(GLocaleCommonSetting.class, xformSetting.get(GLocaleCommonSetting.class));
		GXFormSettingHolder.setCommonSetting(GReservedWordSetting.class, xformSetting.get(GReservedWordSetting.class));

	}
}
