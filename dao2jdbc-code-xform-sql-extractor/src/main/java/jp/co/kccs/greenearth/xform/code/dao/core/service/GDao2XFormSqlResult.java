package jp.co.kccs.greenearth.xform.code.dao.core.service;

import jp.co.kccs.greenearth.framework.dao.db.GDBVirtualEntity;
import jp.co.kccs.greenearth.xform.code.dao.core.GCrudType;

import java.io.Serializable;

public interface GDao2XFormSqlResult extends Serializable {
	String getSqlScript();
	GCrudType getType();
	GDBVirtualEntity getVirtualEntity();
	void setSqlScript(String sqlScript);
	void setType(GCrudType crudType);
	void setVirtualEntity(GDBVirtualEntity virtualEntity);
}
