package jp.co.kccs.greenearth.xform.entity.dao.core.loader;

public interface GEntityMetadataLoader<I, O> {
	O load(I parameter);
}
