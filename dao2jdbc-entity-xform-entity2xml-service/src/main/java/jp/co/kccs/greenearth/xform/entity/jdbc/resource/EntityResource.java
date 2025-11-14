package jp.co.kccs.greenearth.xform.entity.jdbc.resource;

import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import jp.co.kccs.greenearth.framework.data.GValidateException;
import jp.co.kccs.greenearth.xform.configuration.GTransformSetting;
import jp.co.kccs.greenearth.xform.configuration.GXFormSetting;
import jp.co.kccs.greenearth.xform.configuration.GXFormSettingHolder;
import jp.co.kccs.greenearth.xform.entity.jdbc.service.ConvertEntity2XmlService;

import java.util.Map;
import java.util.Objects;

@Path("entities")
public class EntityResource {

	private ConvertEntity2XmlService entity2XmlFunction;

	public EntityResource() {
		this.entity2XmlFunction = new ConvertEntity2XmlService();
	}
	@POST
	@Path("transformations/entity2xml")
	public Response convertSql2Code(EntityRequest request) throws Exception {
		if (Objects.isNull(request.getSetting())) {
			throw new GValidateException("Setting is required.");
		}
		if (Objects.isNull(request.getValue())) {
			throw new GValidateException("Value is required.");
		}
		setSetting(request.getSetting());
		String result = entity2XmlFunction.handle(request.getValue());
		return Response.ok(new ApiResponse<>(result)).build();
	}
	private static void setSetting(SettingRequest setting) {
		Map<Class<?>, GXFormSetting> formSettingMap = setting.toDaoCommonSetting();
		GXFormSettingHolder.setCommonSetting(GTransformSetting.class, formSettingMap.get(GTransformSetting.class));
	}
}
