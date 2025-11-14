package jp.co.kccs.greenearth.xform.dao.entity.cmd;

import jp.co.kccs.greenearth.commons.GFrameworkUtils;
import jp.co.kccs.greenearth.xform.entity.jdbc.core.service.GEntityFileDescriptor;

import java.util.List;

public interface GDao2XFormEntityCmdService {
	List<GEntityFileDescriptor> export(String filePath, String settingFilePath);
	static GDao2XFormEntityCmdService getInstance() {
		return GFrameworkUtils.getComponent(GDao2XFormEntityCmdService.class.getName());
	}
}
