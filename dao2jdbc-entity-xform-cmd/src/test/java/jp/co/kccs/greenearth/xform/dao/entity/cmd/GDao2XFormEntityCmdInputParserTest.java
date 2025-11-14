package jp.co.kccs.greenearth.xform.dao.entity.cmd;

import jp.co.kccs.greenearth.commons.GFrameworkUtils;
import jp.co.kccs.greenearth.commons.utils.GFileUtils;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static junit.framework.TestCase.assertEquals;

/**
 * @author KCCS XXXXX
 * @create GEF_NEXT_DATE
 * @since GEF_NEXT_VERSION
 */
public class GDao2XFormEntityCmdInputParserTest {

	@Test
	public void testParse_ConditionCode() {
		String filePath = GFileUtils.getResource("jp/co/kccs/greenearth/xform/dao/entity/cmd/gdao2xformentitycmdinputparsertest/condition.yaml").getPath();

		GDao2XFormEntityCmdInputParser parser = GFrameworkUtils.getComponent(GDao2XFormEntityCmdInputParser.class);
		List<String> parameters = parser.parse(filePath, StandardCharsets.UTF_8.name());

		assertEquals(2, parameters.size());
		assertEquals("54DDEA23D42047088F1005030BB95913", parameters.get(0));
		assertEquals("4CC7FD3DD7E040A29E14D5536D875CAA", parameters.get(1));
	}
}
