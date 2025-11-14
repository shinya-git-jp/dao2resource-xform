package jp.co.kccs.greenearth.xform.dao.code.cmd;


import jp.co.kccs.greenearth.xform.code.dao.core.GCrudType;

import java.util.List;

/**
 * @author KCCS XXXXX
 * @create GEF_NEXT_DATE
 * @since GEF_NEXT_VERSION
 */
public class GDao2XFormCodeParameterImpl implements GDao2XFormCodeParameter {

	private String id;

	private List<GCrudType> crudTypes;
	private boolean categoryFlag;

	@Override
	public String getId() {
		return this.id;
	}

	@Override
	public List<GCrudType> getCrudTypes() {
		return this.crudTypes;
	}

	@Override
	public boolean isCategory() {
		return categoryFlag;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public void setCategoryFlag(boolean categoryFlag) {
		this.categoryFlag = categoryFlag;
	}

	public void setCrudTypes(List<GCrudType> crudTypes) {
		this.crudTypes = crudTypes;
	}
}
