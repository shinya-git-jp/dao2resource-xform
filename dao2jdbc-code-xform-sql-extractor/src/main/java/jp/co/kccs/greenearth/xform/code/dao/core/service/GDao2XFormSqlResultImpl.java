package jp.co.kccs.greenearth.xform.code.dao.core.service;

import com.github.vertical_blank.sqlformatter.SqlFormatter;
import com.github.vertical_blank.sqlformatter.core.FormatConfig;
import jp.co.kccs.greenearth.framework.dao.db.GDBVirtualEntity;
import jp.co.kccs.greenearth.xform.code.dao.core.GCrudType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
public class GDao2XFormSqlResultImpl implements GDao2XFormSqlResult {
	private String sqlScript;
	private GCrudType type;
	private GDBVirtualEntity virtualEntity;

	public GDao2XFormSqlResultImpl(String sqlScript, GCrudType type, GDBVirtualEntity virtualEntity) {
		this.sqlScript = reformatSql(sqlScript);
		this.type = type;
		this.virtualEntity = virtualEntity;
	}

	public void setSqlScript(String sqlScript) {
		this.sqlScript = reformatSql(sqlScript);
	}

	private String reformatSql(String sql) {
		if (Objects.nonNull(sql)) {
			return SqlFormatter.format(sql,
					FormatConfig.builder()
							.indent("    ")
							.maxColumnLength(1000)
							.build());
		}
		return null;
	}
}

