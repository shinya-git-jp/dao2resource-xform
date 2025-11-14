package jp.co.kccs.greenearth.xform.code.dao.core;

import jp.co.kccs.greenearth.commons.GFrameworkException;
import jp.co.kccs.greenearth.xform.code.jdbc.core.service.GXFormCodeResult;
import jp.co.kccs.greenearth.xform.code.jdbc.core.service.GXFormCodeResultImpl;
import jp.co.kccs.greenearth.xform.dao.dto.GDao2VirtualEntity;
import lombok.Getter;
import lombok.Setter;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
public class GDao2JdbcXFormCodeResult extends GXFormCodeResultImpl {
	private GDao2VirtualEntity virtualEntity;

	public static GDao2JdbcXFormCodeResult from(GXFormCodeResult formCodeResult, GDao2VirtualEntity virtualEntity) {
		if (Objects.isNull(virtualEntity)) {
			throw new GFrameworkException("Virtual Entity cannot be null.");
		}
		if (Objects.isNull(formCodeResult)) {
			throw new GFrameworkException("GXFormCodeResult cannot be null.");
		}
		GDao2JdbcXFormCodeResult dao2JdbcXFormCodeResult = new GDao2JdbcXFormCodeResult();
		dao2JdbcXFormCodeResult.setSqlScript(formCodeResult.getSqlScript());
		dao2JdbcXFormCodeResult.setApiCodes(formCodeResult.getApiCodes());
		dao2JdbcXFormCodeResult.setDescription(formCodeResult.getDescription());
		dao2JdbcXFormCodeResult.setType(formCodeResult.getType());
		dao2JdbcXFormCodeResult.setVirtualEntity(virtualEntity);
		return dao2JdbcXFormCodeResult;
	}

}