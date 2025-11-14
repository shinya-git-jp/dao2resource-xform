package jp.co.kccs.greenearth.xform.code.jdbc.service.resource;

import jp.co.kccs.greenearth.xform.code.dao.core.GCrudType;
import jp.co.kccs.greenearth.xform.code.dao.core.service.GDao2XFormSqlResult;
import jp.co.kccs.greenearth.xform.dao.common.GDao2Utils;
import jp.co.kccs.greenearth.xform.dao.dto.GDao2VirtualEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Dao2SqlResultResponse {
	private String sqlScript;
	private GCrudType type;
	private GDao2VirtualEntity virtualEntity;
	
	public Dao2SqlResultResponse(GDao2XFormSqlResult sqlResult) {
		this.sqlScript = sqlResult.getSqlScript();
		this.type = sqlResult.getType();
		this.virtualEntity = GDao2Utils.convertFrom(sqlResult.getVirtualEntity());
	}

}
