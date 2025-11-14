package jp.co.kccs.greenearth.xform.dao.common;

/**
 * @author KCCS XXXXX
 * @create GEF_NEXT_DATE
 * @since GEF_NEXT_VERSION
 */
public class GVEItemImpl<T> implements GVEItem<T> {

	private String id;

	private T data;

	@Override
	public T getData() {
		return this.data;
	}

	@Override
	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setData(T data) {
		this.data = data;
	}
}
