package jp.co.kccs.greenearth.xform.code.jdbc.service.resource;

import jp.co.kccs.greenearth.commons.GFrameworkException;
import jp.co.kccs.greenearth.framework.data.GValidateException;
import jp.co.kccs.greenearth.xform.code.jdbc.service.NotFoundException;
import jp.co.kccs.greenearth.xform.code.jdbc.service.function.CheckConnectionFunction;
import jp.co.kccs.greenearth.xform.code.jdbc.service.function.FetchVeIdByCategoryIdFunction;
import jp.co.kccs.greenearth.xform.code.jdbc.service.function.SqlExtractorFunction;
import jp.co.kccs.greenearth.xform.code.jdbc.service.function.VeIdExistenceFunction;
import jp.co.kccs.greenearth.xform.configuration.GDbCommonSetting;
import jp.co.kccs.greenearth.xform.configuration.GTransformSetting;
import jp.co.kccs.greenearth.xform.configuration.GXFormSetting;
import jp.co.kccs.greenearth.xform.configuration.GXFormSettingHolder;
import jp.co.kccs.greenearth.xform.dao.common.GDao2Utils;
import jp.co.kccs.greenearth.xform.old.configuration.GLocaleCommonSetting;
import jp.co.kccs.greenearth.xform.old.configuration.GReservedWordSetting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;

@RestController
public class CodeController {
	private final Map<String, Semaphore> semaphoreMap = new ConcurrentHashMap<>();

	@Autowired
	private SqlExtractorFunction sqlExtractorFunction;
	@Autowired
	private FetchVeIdByCategoryIdFunction fetchVeIdByCategoryIdFunction;
	@Autowired
	private VeIdExistenceFunction veIdExistenceFunction;
	@Autowired
	private CheckConnectionFunction checkConnectionFunction;

	public CodeController(){}
	CodeController(
		SqlExtractorFunction sqlExtractorFunction,
	   	FetchVeIdByCategoryIdFunction fetchVeIdByCategoryIdFunction,
		VeIdExistenceFunction veIdExistenceFunction,
		CheckConnectionFunction checkConnectionFunction
	) {
		this.sqlExtractorFunction = sqlExtractorFunction;
		this.fetchVeIdByCategoryIdFunction = fetchVeIdByCategoryIdFunction;
		this.veIdExistenceFunction = veIdExistenceFunction;
		this.checkConnectionFunction = checkConnectionFunction;
	}

	@PostMapping("/codes/transformations/sql-extractors")
	public ApiResponse<List<Dao2SqlResultResponse>> extractSql(@RequestBody CodeRequest request) throws Exception {
//		String key = GDao2Utils.getDbCommonSettingEncoded(request.getSetting().toDaoCommonSetting().getDb());
//		Semaphore semaphore = semaphoreMap.computeIfAbsent(key, k -> new Semaphore(5)); // limit to 3
//		try {
//			semaphore.acquire();
//			validateRequest(request);
//			setSetting(request.getSetting());
//			return new ApiResponse<>(sqlExtractorFunction.handle(request.getValue()));
//		} catch (InterruptedException e) {
//			Thread.currentThread().interrupt();
//			SystemError systemError = new SystemError();
//			systemError.setStatusCode("500");
//			systemError.setMessage("Interrupted");
//			return new ApiResponse<>(systemError);
//		} finally {
//			semaphore.release();
//		}
		validateRequest(request);
		setSetting(request.getSetting());
		return new ApiResponse<>(sqlExtractorFunction.handle(request.getValue()));
	}

	@PostMapping("/codes/categories/children")
	public ApiResponse<List<String>> fetchVeIdByCategoryId(@RequestBody CodeRequest request) throws Exception {
		validateRequest(request);
		setSetting(request.getSetting());
			List<String> output = fetchVeIdByCategoryIdFunction.handle((String) request.getValue().get("categoryId"));
			return new ApiResponse<>(output);
	}

	@PostMapping("/codes/ids")
	public ApiResponse<String> checkIfIdIsExist(@RequestBody CodeRequest request) throws Exception {
		validateRequest(request);
		setSetting(request.getSetting());
		try {
			String output = veIdExistenceFunction.handle(request.getValue());
			return new ApiResponse<>(output);
		} catch (NotFoundException notFoundException) {
			return new ApiResponse<>(notFoundException.getMessage());
		}
	}

	@PostMapping("/check-connections")
	public ApiResponse<String> checkConnections(@RequestBody CodeRequest request) throws Exception {
		if (Objects.isNull(request.getSetting())) {
			throw new GValidateException("Setting is required.");
		}
		setSetting(request.getSetting());
		try {
			String output = checkConnectionFunction.handle(request.getValue());
			return new ApiResponse<>(output);
		} catch (GFrameworkException exception) {
			return new ApiResponse<>(exception.getMessage());
		}
	}
	private void validateRequest(CodeRequest request) throws GValidateException {
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
