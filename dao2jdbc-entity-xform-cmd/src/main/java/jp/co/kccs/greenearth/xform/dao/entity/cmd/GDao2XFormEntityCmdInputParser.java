package jp.co.kccs.greenearth.xform.dao.entity.cmd;

import jp.co.kccs.greenearth.commons.GFrameworkUtils;

import java.util.List;

public interface GDao2XFormEntityCmdInputParser {
	List<String> parse(String filePath, String encoding);
	static GDao2XFormEntityCmdInputParser getInstance() {
		return GFrameworkUtils.getComponent(GDao2XFormEntityCmdInputParser.class.getName());
	}
}
