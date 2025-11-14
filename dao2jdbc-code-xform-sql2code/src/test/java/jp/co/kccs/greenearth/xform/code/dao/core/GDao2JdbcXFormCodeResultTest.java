package jp.co.kccs.greenearth.xform.code.dao.core;

import jp.co.kccs.greenearth.commons.GFrameworkException;
import jp.co.kccs.greenearth.xform.code.jdbc.core.GCompoundApiImpl;
import jp.co.kccs.greenearth.xform.code.jdbc.core.GTerminatorApi;
import jp.co.kccs.greenearth.xform.code.jdbc.core.service.GXFormCodeResult;
import jp.co.kccs.greenearth.xform.dao.dto.GDao2VirtualEntity;
import jp.co.kccs.greenearth.xform.jdbc.common.GCrudType;
import org.junit.Test;
import java.util.List;
import static jp.co.kccs.greenearth.xform.code.dao.core.GUtilTest.getXFormCodeResult;
import static org.junit.Assert.*;

public class GDao2JdbcXFormCodeResultTest {

	/**
	 * [1] {@link GDao2JdbcXFormCodeResult#from(GXFormCodeResult, GDao2VirtualEntity)}で正常な{@link GXFormCodeResult}と{@link GDao2VirtualEntity}が渡される場合、{@link GDao2JdbcXFormCodeResult}結果が生成されること。<br>
	 * [2] {@link GDao2JdbcXFormCodeResult#from(GXFormCodeResult, GDao2VirtualEntity)}で、{@link GXFormCodeResult}がnullの場合、かつ{@link GDao2VirtualEntity}が渡される場合、エラーが発生されること。<br>
	 * [3] {@link GDao2JdbcXFormCodeResult#from(GXFormCodeResult, GDao2VirtualEntity)}で、{@link GDao2VirtualEntity}がnullの場合、かつ{@link GXFormCodeResult}が渡される場合、エラーが発生されること。<br>
	 */
	@Test
	public void testFrom() {
		{
			List<GXFormCodeResult> codeResults = getXFormCodeResult();
			GDao2VirtualEntity virtualEntity = GUtilTest.getVirtualEntity();
			GDao2JdbcXFormCodeResult dao2JdbcXFormCodeResults = GDao2JdbcXFormCodeResult.from(codeResults.get(0), virtualEntity);
			assertEquals(virtualEntity, dao2JdbcXFormCodeResults.getVirtualEntity());
			assertEquals("test", dao2JdbcXFormCodeResults.getDescription());
			assertEquals(GCrudType.SELECT, dao2JdbcXFormCodeResults.getType());
			assertEquals("select" + System.lineSeparator() +
					"    *" + System.lineSeparator() +
					"from" + System.lineSeparator() +
					"    test;", dao2JdbcXFormCodeResults.getSqlScript());
			assertTrue(dao2JdbcXFormCodeResults.getApiCodes().containsKey(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, null)));
			assertTrue(dao2JdbcXFormCodeResults.getApiCodes().containsKey(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, null)));
			assertTrue(dao2JdbcXFormCodeResults.getApiCodes().containsKey(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, null)));
			assertEquals("GRecordSet result = select(\"test\", \"test\")" + System.lineSeparator() +
					".fields(colsAll())" + System.lineSeparator() +
					".findRecordSet();", dao2JdbcXFormCodeResults.getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD_SET, null)));
			assertEquals("List<GRecord> result = select(\"test\", \"test\")" + System.lineSeparator() +
					".fields(colsAll())" + System.lineSeparator() +
					".findList();", dao2JdbcXFormCodeResults.getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_LIST, null)));
			assertEquals("GRecord result = select(\"test\", \"test\")" + System.lineSeparator() +
					".fields(colsAll())" + System.lineSeparator() +
					".findRecord();", dao2JdbcXFormCodeResults.getApiCodes().get(new GCompoundApiImpl(GTerminatorApi.FIND_RECORD, null)));
		}
		{
			try {
				GDao2JdbcXFormCodeResult.from(null, GUtilTest.getVirtualEntity());
				fail();
			} catch (Exception exception) {
				assertEquals(GFrameworkException.class, exception.getClass());
				assertEquals("GXFormCodeResult cannot be null.", exception.getMessage());
			}
		}
		{
			try {
				GDao2JdbcXFormCodeResult.from(GUtilTest.getXFormCodeResult().get(0), null);
				fail();
			} catch (Exception exception) {
				assertEquals(GFrameworkException.class, exception.getClass());
				assertEquals("Virtual Entity cannot be null.", exception.getMessage());
			}
		}
	}


}
