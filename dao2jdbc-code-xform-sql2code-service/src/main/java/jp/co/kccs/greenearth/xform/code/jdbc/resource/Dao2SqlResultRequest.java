package jp.co.kccs.greenearth.xform.code.jdbc.resource;

import jp.co.kccs.greenearth.xform.code.jdbc.core.GLikeType;
import jp.co.kccs.greenearth.xform.dao.dto.GDao2VirtualEntity;
import jp.co.kccs.greenearth.xform.jdbc.common.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.*;

@Getter
@Setter
@NoArgsConstructor
public class Dao2SqlResultRequest {
	private String sqlScript;
	private GCrudType type;
	private GDao2VirtualEntity virtualEntity;

	private static final Map<String, GLikeType> LIKE_TYPE_MAP = Map.of(
			"LIKE_PART", GLikeType.LIKE_PART,
			"LIKE_SUFFIX", GLikeType.LIKE_SUFFIX,
			"LIKE_PREFIX", GLikeType.LIKE_PREFIX
	);

}
