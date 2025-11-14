package jp.co.kccs.greenearth.xform.dao.common;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.util.ArrayList;
import java.util.List;

/**
 * @author KCCS XXXXX
 * @create GEF_NEXT_DATE
 * @since GEF_NEXT_VERSION
 */
public class GVECategoryImpl<T> implements GVECategory {

	private String id;

	private List<GVENode<T>> children = new ArrayList<>();

	@SuppressFBWarnings(value = {"EI_EXPOSE_REP"})
	@Override
	public List<GVENode<T>> getChildren() {
		return this.children;
	}

	@Override
	public void addChildren(GVENode node) {
		this.children.add(node);
	}

	@Override
	public void deleteChildren(GVENode node) {
		this.children.remove(node);
	}

	@Override
	public int getSize() {
		return this.children.size();
	}

	@Override
	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}
	@SuppressFBWarnings(value = {"EI_EXPOSE_REP2"})
	public void setChildren(List<GVENode<T>> children) {
		this.children = children;
	}
}
