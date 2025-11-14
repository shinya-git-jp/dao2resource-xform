package jp.co.kccs.greenearth.xform.dao.code.cmd;

import jp.co.kccs.greenearth.commons.GFrameworkUtils;
import java.io.IOException;
import java.util.List;

public interface GDao2XFormCodeCmdInputParser {
	List<GDao2XFormCodeParameter> parse(String filePath, String encoding) throws IOException;
	static GDao2XFormCodeCmdInputParser getInstance() {
		return GFrameworkUtils.getComponent(GDao2XFormCodeCmdInputParser.class);
	}
}
