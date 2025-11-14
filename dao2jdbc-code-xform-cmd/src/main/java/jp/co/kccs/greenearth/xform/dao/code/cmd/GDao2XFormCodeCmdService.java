package jp.co.kccs.greenearth.xform.dao.code.cmd;

import jp.co.kccs.greenearth.commons.GFrameworkUtils;
import jp.co.kccs.greenearth.xform.code.jdbc.core.export.GExporterResult;

public interface GDao2XFormCodeCmdService<I, O> {
	GExporterResult<I, O> export(String filePath, String settingFile);
	static GDao2XFormCodeCmdService getInstance() {
		return GFrameworkUtils.getComponent(GDao2XFormCodeCmdService.class);
	}
}
