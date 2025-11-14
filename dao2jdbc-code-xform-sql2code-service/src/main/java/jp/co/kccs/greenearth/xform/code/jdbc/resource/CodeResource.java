package jp.co.kccs.greenearth.xform.code.jdbc.resource;

import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jp.co.kccs.greenearth.framework.data.GValidateException;
import jp.co.kccs.greenearth.xform.code.dao.core.GDao2JdbcXFormCodeResult;
import jp.co.kccs.greenearth.xform.code.jdbc.configuration.GCodeTransformSetting;
import jp.co.kccs.greenearth.xform.code.jdbc.service.ConvertSql2CodeService;
import jp.co.kccs.greenearth.xform.code.jdbc.utility.GUtils;
import jp.co.kccs.greenearth.xform.configuration.GTransformSetting;
import jp.co.kccs.greenearth.xform.configuration.GXFormSetting;
import jp.co.kccs.greenearth.xform.configuration.GXFormSettingHolder;

import java.util.Map;
import java.util.Objects;

@Path("codes")
public class CodeResource {

	private ConvertSql2CodeService convertSql2CodeFunction;

	public CodeResource() {
		this.convertSql2CodeFunction = new ConvertSql2CodeService();
	}
	@POST
	@Path("transformations/sql2code")
	@Produces(MediaType.APPLICATION_JSON)
	public Response convertSql2Code(CodeRequest request) throws Exception {
		if (Objects.isNull(request.getSetting())) {
			throw new GValidateException("Setting is required.");
		}
		if (Objects.isNull(request.getValue())) {
			throw new GValidateException("Value is required.");
		}
		setSetting(request.getSetting());
		GDao2JdbcXFormCodeResult result = convertSql2CodeFunction.handle(request.getValue());
		return Response.ok(GUtils.convertObjectToString(new ApiResponse<>(result))).build();
	}
	private static void setSetting(SettingRequest setting) {
		Map<Class<?>, GXFormSetting> formSettingMap = setting.toDaoCommonSetting();
		GXFormSettingHolder.setCommonSetting(GTransformSetting.class, formSettingMap.get(GTransformSetting.class));
		GXFormSettingHolder.setCommonSetting(GCodeTransformSetting.class, formSettingMap.get(GCodeTransformSetting.class));
	}
}
