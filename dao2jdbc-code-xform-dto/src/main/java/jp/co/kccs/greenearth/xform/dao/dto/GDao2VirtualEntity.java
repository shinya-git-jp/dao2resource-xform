package jp.co.kccs.greenearth.xform.dao.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
public class GDao2VirtualEntity {
	private String displayName;
	private String veId;
	private String categoryId;
	private String categoryName;
	private String veType;
	private Entity refEntity = new Entity();
	private List<VirtualColumn> columns = new ArrayList<>();
	private String absoluteVirtualColumnCode;
	private List<ForeignKey> foreignKeys = new ArrayList<>();
	private List<FilterCondition> filterConditions = new ArrayList<>();
	private List<SearchCondition> searchConditions = new ArrayList<>();
	private List<GroupCondition> groupConditions = new ArrayList<>();
	private List<SortCondition> sortConditions = new ArrayList<>();

	public void setColumns(List<VirtualColumn> columns) {
		this.columns = columns;
		this.absoluteVirtualColumnCode = getAbsoluteVirtualColumnsCode();
	}
	private String getAbsoluteVirtualColumnsCode() {
		StringBuilder code = new StringBuilder();
		int index = 0;
		for (VirtualColumn column : columns) {
			if (Objects.isNull(column.getRefColumn())) {
				String varName = "absoluteVirtualColumn" + index;
				code.append(String.format("Map<String, Object> %s = new HashMap<>();%n", varName));
				if (Objects.nonNull(column.getName())) {
					code.append(String.format("%s.put(\"name\", \"%s\");%n", varName, column.getName()));
				}
				if (Objects.nonNull(column.getDisplayName())) {
					code.append(String.format("%s.put(\"displayName\", \"%s\");%n", varName, column.getDisplayName()));
				}
				if (Objects.nonNull(column.getFixedValue())) {
					code.append(String.format("%s.put(\"fixedValue\", \"%s\");%n", varName, column.getFixedValue()));
				}
				if (Objects.nonNull(column.getAggregateFunction())) {
					code.append(String.format("%s.put(\"aggregateFunction\", \"%s\");%n", varName, column.getAggregateFunction()));
				}
				if (Objects.nonNull(column.getFormat())) {
					code.append(String.format("%s.put(\"format\", \"%s\");", varName, column.getFormat()));
				}
				index += 1;
				code.append(System.lineSeparator());
			}
		}
		return code.toString().isEmpty() ? "-" : code.toString();
	}

	@Getter
	@Setter
	public static final class VirtualColumn {
		private String name;
		private String displayName;
		private Object fixedValue;
		private String aggregateFunction;
		private String format;
		private String refColumn;
	}

	@Getter
	@Setter
	public static final class Entity {
		private String name;
		private String phyName;
		private String databaseName;
		private Column primaryKey;
		private List<Column> columns = new ArrayList<>();
		private List<UniqueKey> uniqueKeys = new ArrayList<>();
		private List<ForeignKey> foreignKeys = new ArrayList<>();


	}

	@Getter
	@Setter
	public static final class Column {
		private String name;
		private String phyName;
		private int size = 0;
		private String dataType;
		private String decimalSize;
		private boolean notNull;
		private boolean isPrimaryKey;

	}

	@Getter
	@Setter
	public static final class UniqueKey {
		private String name;
		private List<Column> uniqueKeyColumns = new ArrayList<>();

	}

	@Getter
	@Setter
	public static final class ForeignKey {
		private String name;
		private Entity referenceEntity = new Entity();
		private List<ForeignKeyColumn> foreignKeyColumns = new ArrayList<>();
		private String joinType;


	}

	@Getter
	@Setter
	public static final class ForeignKeyColumn {
		private String referenceColumn;;
		private String sourceColumn;
		private String constValue = "-";

	}
	@Getter
	@Setter
	public static final class FilterCondition {
		private String column;
		private String entity;
		private String comparisonOperator;
		private String filterValue;

	}

	@Getter
	@Setter
	public static final class SearchCondition {
		private String column;
		private String comparisonOperator;
		private boolean isOptional;
		private String entity;
		private boolean isTrim;

	}

	@Getter
	@Setter
	public static final class SortCondition {
		private String sortColumn;
		private String sortMode;
	}

	@Getter
	@Setter
	public static final class GroupCondition {
		private String groupColumn;
	}

}
