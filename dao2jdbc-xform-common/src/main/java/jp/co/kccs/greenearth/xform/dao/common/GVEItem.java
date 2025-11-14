package jp.co.kccs.greenearth.xform.dao.common;

/**
 * @author KCCS XXXXX
 * @create GEF_NEXT_DATE
 * @since GEF_NEXT_VERSION
 */
public interface GVEItem<T> extends GVENode {

	T getData();
	void setData(T data);
}
