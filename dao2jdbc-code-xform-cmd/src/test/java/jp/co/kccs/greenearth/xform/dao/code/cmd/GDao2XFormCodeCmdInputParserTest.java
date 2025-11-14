package jp.co.kccs.greenearth.xform.dao.code.cmd;

import jp.co.kccs.greenearth.commons.GFrameworkUtils;
import jp.co.kccs.greenearth.commons.utils.GFileUtils;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static junit.framework.TestCase.assertEquals;

/**
 * @author KCCS XXXXX
 * @create GEF_NEXT_DATE
 * @since GEF_NEXT_VERSION
 */
public class GDao2XFormCodeCmdInputParserTest {

	@Test
	public void testParse_ConditionCode() throws IOException {
		String filePath = GFileUtils.getResource("jp/co/kccs/greenearth/xform/dao/code/cmd/gdao2xformcodecmdinputparsertest/condition.yaml").getPath();

		GDao2XFormCodeCmdInputParser parser = GFrameworkUtils.getComponent(GDao2XFormCodeCmdInputParser.class);
		List<GDao2XFormCodeParameter> parameters = parser.parse(filePath, StandardCharsets.UTF_8.name());

		assertEquals(3, parameters.size());
		assertEquals("1AE92EDAF203443F820D28B2015219AD", parameters.get(0).getId());
		assertEquals(3, parameters.get(0).getCrudTypes().size());
		assertEquals(false, parameters.get(0).isCategory());
		assertEquals("7042D90E3612454C88F9FE3AC4FD3322", parameters.get(1).getId());
		assertEquals(1, parameters.get(1).getCrudTypes().size());
		assertEquals(false, parameters.get(1).isCategory());
		assertEquals("1CD54C7D6D7A4E1FAF0B669BE61E248C", parameters.get(2).getId());
		assertEquals(2, parameters.get(2).getCrudTypes().size());
		assertEquals(true, parameters.get(2).isCategory());
	}

}
