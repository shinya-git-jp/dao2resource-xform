package jp.co.kccs.greenearth.xform.entity.jdbc.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jp.co.kccs.greenearth.xform.dao.dto.GDao2VirtualEntity;
import jp.co.kccs.greenearth.xform.dao.entity.mapper.GXFormEntityMapper;
import jp.co.kccs.greenearth.xform.entity.jdbc.core.service.GEntityFileDescriptor;
import jp.co.kccs.greenearth.xform.entity.jdbc.core.service.GEntityFileTransformService;
import jp.co.kccs.greenearth.xform.jdbc.common.GXFormEntity;

import java.util.Base64;
import java.util.Map;
import java.util.Objects;

public class ConvertEntity2XmlService {
	public String handle(Map<String, Object> input) {
		if (Objects.isNull(input)) {
			return null;
		}
		ObjectMapper objectMapper = new ObjectMapper();
		GDao2VirtualEntity.Entity entity = objectMapper.convertValue(input, GDao2VirtualEntity.Entity.class);
		GXFormEntity gxFormEntity = (new GXFormEntityMapper()).map(entity);
		GEntityFileDescriptor descriptor = GEntityFileTransformService.getInstance().transform(gxFormEntity);
		if (Objects.nonNull(descriptor)) {
			return Base64.getEncoder().encodeToString(descriptor.getResult());
		}
		return null;
	}
}
