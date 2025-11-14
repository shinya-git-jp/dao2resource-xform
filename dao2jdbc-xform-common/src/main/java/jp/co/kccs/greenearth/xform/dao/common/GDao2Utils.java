package jp.co.kccs.greenearth.xform.dao.common;

import jp.co.kccs.greenearth.commons.GFrameworkException;
import jp.co.kccs.greenearth.framework.dao.db.*;
import jp.co.kccs.greenearth.framework.dao.db.data.GBinary;
import jp.co.kccs.greenearth.framework.dao.db.repository.*;
import jp.co.kccs.greenearth.framework.dao.enumtype.GDataType;
import jp.co.kccs.greenearth.framework.dao.enumtype.GGroupFunctionType;
import jp.co.kccs.greenearth.framework.dao.enumtype.GUsageType;
import jp.co.kccs.greenearth.framework.jdbc.GColumnType;
import jp.co.kccs.greenearth.xform.configuration.GDbCommonSetting;
import jp.co.kccs.greenearth.xform.configuration.GDbType;
import jp.co.kccs.greenearth.xform.configuration.GXFormSettingHolder;
import jp.co.kccs.greenearth.xform.dao.dto.GDao2VirtualEntity;
import jp.co.kccs.greenearth.xform.old.common.GDaoDbType;
import jp.co.kccs.greenearth.xform.old.common.GSLocalizationResources;
import jp.co.kccs.greenearth.xform.old.configuration.GReservedWordSetting;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

import static jp.co.kccs.greenearth.xform.old.common.GXFormOldUtils.*;

public class GDao2Utils {

	private static GDao2VirtualEntity.VirtualColumn convertFrom(GDBVirtualColumn virtualColumn) {
		GDao2VirtualEntity.VirtualColumn vc = new GDao2VirtualEntity.VirtualColumn();
		String displayName = virtualColumn.getDisplayName(getLocale());
		String functionType = "-";
		if (Objects.nonNull(displayName) && displayName.equals("failed to get displayname.")) {
			displayName = "-";
		}
		if (Objects.isNull(displayName)) {
			displayName = "-";
		}

		Object fixedValue = virtualColumn.getDefaultValue();
		if (Objects.isNull(fixedValue)) {
			fixedValue = "-";
		}
		String format = virtualColumn.getFormatString();
		if (Objects.isNull(format)) {
			format = "-";
		}
		vc.setName(virtualColumn.getVirtualColumnName());
		if (Objects.nonNull(virtualColumn.getDBColumn())) {
			vc.setRefColumn(String.format("%s.%s", virtualColumn.getDBEntity().getEntityName(), virtualColumn.getColumnName()));
		}
		vc.setFormat(format);

		if (!virtualColumn.getGroupFunctionType().equals(GGroupFunctionType.NONE)) {
			functionType = virtualColumn.getGroupFunctionType().toString();
		}
		vc.setAggregateFunction(functionType);
		vc.setDisplayName(displayName);
		vc.setFixedValue(fixedValue);
		return vc;
	}


	public static GDao2VirtualEntity.Entity convertFrom(GDBEntity gdbEntity) {
		GDao2VirtualEntity.Entity entity = new GDao2VirtualEntity.Entity();
		entity.setName(gdbEntity.getEntityName());
		entity.setPrimaryKey(getPrimaryKey(gdbEntity));
		entity.setDatabaseName(gdbEntity.getDataBaseName());
		entity.setColumns(gdbEntity.getDBColumns().values().stream().map(GDao2Utils::convertFrom).collect(Collectors.toList()));
		entity.setUniqueKeys(gdbEntity.getUniqueKeys().stream().map(GDao2Utils::convertFrom).toList());
		entity.setForeignKeys(getDbEntityForeignKey(gdbEntity.getObjectID()).stream().map(GDao2Utils::convertFrom).collect(Collectors.toList()));
		return entity;
	}

	private static List<GForeignKey> getDbEntityForeignKey(String entityId) {
		List<GForeignKey> foreignKeyList = new ArrayList<>();
		try {
			List<Map<String, Object>> result = executeSQL(String.format("Select objectID AS \"objectID\" from %s.SForeignKey where sourceEntityObjectID =  '%s'", getSchema(), entityId));
			List<String> resultList = new ArrayList<>();
			for (Map<String, Object> stringObjectMap : result) {
				resultList.add((String) stringObjectMap.get("objectID"));
			}
			for (String s : resultList) {
				List<Map<String, Object>> resultSetFK = null;
				GDbType dbType = GXFormSettingHolder.getSetting(GDbCommonSetting.class).getDbType();
				if (dbType == GDaoDbType.mysql) {
					resultSetFK = executeSQL(String.format("Select `usage` AS \"usage\" from %s.sentitymap where foreignKeyObjectID =  '%s'", getSchema(), s));
				} else if (dbType == GDaoDbType.db2 || dbType == GDaoDbType.oracle) {
					resultSetFK = executeSQL(String.format("Select usage AS \"usage\" from %s.sentitymap where foreignKeyObjectID =  '%s'", getSchema(), s));
				}
				String usageType = null;
				if (Objects.nonNull(resultSetFK)) {
					for (Map<String, Object> stringObjectMap : resultSetFK) {
						usageType = (String) stringObjectMap.get("usage");
					}
				}
				GForeignKey foreignKey = GDBEntityFactory.getForeignKey(s);
				if (Objects.nonNull(usageType)) {
					foreignKey.setUsage(GUsageType.convert(usageType));
				}
				foreignKeyList.add(foreignKey);
			}
		} catch (SQLException e) {
			throw new GFrameworkException(e);
		}
		return foreignKeyList;
	}
	private static GDao2VirtualEntity.Column getPrimaryKey(GDBEntity entity) {
		GReservedWordSetting reservedWordSetting = GXFormSettingHolder.getSetting(GReservedWordSetting.class);
		for (GDBColumn value : entity.getDBColumns().values()) {
			if (value.getColumnName().equals(reservedWordSetting.getPrimaryKeyColumn())) {
				return convertFrom(value);
			}
		}
		return null;
	}

	public static GDao2VirtualEntity convertFrom(GDBVirtualEntity virtualEntity) {
		GDao2VirtualEntity ve = new GDao2VirtualEntity();
		String displayName = virtualEntity.getDisplayName(getLocale());
		if (Objects.nonNull(displayName) && displayName.equals("failed to get displayname.")) {
			displayName = "-";
		}
		if (Objects.isNull(displayName) || displayName.isEmpty()) {
			displayName = "-";
		}
		ve.setVeId(virtualEntity.getObjectID());
		ve.setVeType(virtualEntity.getVeType().equals("0") ? "Inq" : "Reg");
		ve.setRefEntity(convertFrom(virtualEntity.getMainDBEntity()));
		ve.setFilterConditions(virtualEntity.getConditionSet().getFilterCondition().getConditionColumns().stream().map(GDao2Utils::convertFromFilterCondition).toList());
		ve.setSearchConditions(virtualEntity.getConditionSet().getSearchCondition().getConditionColumns().stream().map(GDao2Utils::convertFromSearchCondition).toList());
		ve.setSortConditions(virtualEntity.getConditionSet().getOrderByColumns().stream().map(GDao2Utils::convertFrom).toList());
		ve.setGroupConditions(virtualEntity.getConditionSet().getGroupByColumns().stream().map(GDao2Utils::convertFrom).toList());
		ve.setForeignKeys(virtualEntity.getForeignKeys().values().stream().map(GDao2Utils::convertFrom).toList());
		ve.setDisplayName(displayName);
		ve.setCategoryId("-");
		ve.setCategoryName("-");
		ve.setColumns(virtualEntity.getDBVirtualColumns().values().stream().map(GDao2Utils::convertFrom).collect(Collectors.toList()));
		return ve;
	}


	private static GDao2VirtualEntity.Column convertFrom(GDBColumn column) {
		GDao2VirtualEntity.Column col = new GDao2VirtualEntity.Column();
		GReservedWordSetting reservedWordSetting = GXFormSettingHolder.getSetting(GReservedWordSetting.class);
		col.setName(column.getColumnName());
		col.setSize(column.getIntegerLength());
		col.setDecimalSize(column.getDecimalLength() == 0 ? "-" : String.format("%d", column.getDecimalLength()));
		col.setNotNull(column.isNotNull());
		col.setPrimaryKey(column.getColumnName().equals(reservedWordSetting.getPrimaryKeyColumn()));
		col.setDataType(column.getDataType().name());
		return col;
	}

	private static GDao2VirtualEntity.UniqueKey convertFrom(GUniqueKey uniqueKey) {
		GDao2VirtualEntity.UniqueKey uk = new GDao2VirtualEntity.UniqueKey();
		uk.setName(getUniqueKeyName(uniqueKey.getObjectID()));
		uk.setUniqueKeyColumns(uniqueKey.getKeyElements().stream().map(GDao2Utils::convertFrom).toList());
		return uk;
	}

	private static GDao2VirtualEntity.ForeignKey convertFrom(GForeignKey foreignKey) {
		GDao2VirtualEntity.ForeignKey fk = new GDao2VirtualEntity.ForeignKey();
		fk.setName(getForeignKeyName(foreignKey.getObjectID()));
		fk.setReferenceEntity(convertFrom(GDBEntityFactory.getDBEntity(foreignKey.getReferenceEntityOID())));
		fk.setForeignKeyColumns(foreignKey.getForeignKeyElements().stream().map(GDao2Utils::convertFrom).toList());
		fk.setJoinType(foreignKey.getUsage().toString());
		return fk;
	}

	public static GColumnType convertStringToGColumnTypeVE(GDataType dataType) {
		if (dataType.equals(GDataType.INTEGER)) {
			return GColumnType.INTEGER;
		} else if (dataType.equals(GDataType.STRING)) {
			return GColumnType.VARCHAR;
		} else if (dataType.equals(GDataType.DECIMAL)) {
			return GColumnType.DOUBLE;
		} else if (dataType.equals(GDataType.DATE)) {
			return GColumnType.DATE;
		} else if (dataType.equals(GDataType.LONG)) {
			return GColumnType.LONG;
		}
		return null;
	}
	public static String getForeignKeyName(String objectId) {
		try {
			List<Map<String, Object>> resultSet = executeSQL(String.format("Select nameResourceID AS \"nameResourceID\" from %s.SFOREIGNKEY where objectID =  '%s'", getSchema(), objectId));
			List<String> resultList = new ArrayList<>();
			for (Map<String, Object> stringObjectMap : resultSet) {
				resultList.add((String) stringObjectMap.get("nameResourceID"));
			}
			if (!resultList.isEmpty()) {
				String id =	resultList.get(0);
				return GSLocalizationResources.getMessage(id, getLocale());
			}
		} catch (SQLException e) {
			throw new GFrameworkException(e);
		}
		return objectId;
	}
	public static String getUniqueKeyName(String objectId) {
		try {
			List<Map<String, Object>> result = executeSQL(String.format("Select nameResourceID AS \"nameResourceID\" from %s.SUNIQUEKEY where objectID =  '%s'", getSchema(), objectId));
			List<String> resultList = new ArrayList<>();
			for (Map<String, Object> stringObjectMap : result) {
				resultList.add((String) stringObjectMap.get("nameResourceID"));
			}
			if (!resultList.isEmpty()) {
				String id =	resultList.get(0);
				return GSLocalizationResources.getMessage(id, getLocale());
			}
		} catch (SQLException e) {
			throw new GFrameworkException(e);
		}
		return objectId;
	}
	private static GDao2VirtualEntity.ForeignKeyColumn convertFrom(GForeignKeyElement foreignKeyElement) {
		GDao2VirtualEntity.ForeignKeyColumn foreignKeyColumn = new GDao2VirtualEntity.ForeignKeyColumn();
		if (Objects.nonNull(foreignKeyElement.getReferenceColumn())) {
			foreignKeyColumn.setReferenceColumn(foreignKeyElement.getReferenceColumn().getColumnName());
		}
		if (Objects.nonNull(foreignKeyElement.getSourceColumn())) {
			foreignKeyColumn.setSourceColumn(foreignKeyElement.getSourceColumn().getColumnName());
		}
		if (Objects.nonNull(foreignKeyElement.getConstValue())) {
			foreignKeyColumn.setConstValue(foreignKeyElement.getConstValue());
		}
		return foreignKeyColumn;
	}

	private static GDao2VirtualEntity.FilterCondition convertFromFilterCondition(GConditionColumn conditionColumn) {
		GDao2VirtualEntity.FilterCondition filterCondition = new GDao2VirtualEntity.FilterCondition();
		filterCondition.setColumn(conditionColumn.getDBVirtualColumn().getDBColumn().getColumnName());
		filterCondition.setEntity(conditionColumn.getDBVirtualColumn().getDBEntity().getEntityName());
		filterCondition.setComparisonOperator(conditionColumn.getComparisonOperator().name());
		if (Objects.nonNull(conditionColumn.getFilteringValue())) {
			filterCondition.setFilterValue(conditionColumn.getFilteringValue());
		}
		return filterCondition;
	}

	private static GDao2VirtualEntity.SearchCondition convertFromSearchCondition(GConditionColumn conditionColumn) {
		GDao2VirtualEntity.SearchCondition searchCondition = new GDao2VirtualEntity.SearchCondition();
		searchCondition.setColumn(conditionColumn.getDBVirtualColumn().getDBColumn().getColumnName());
		searchCondition.setEntity(conditionColumn.getDBVirtualColumn().getDBEntity().getEntityName());
		searchCondition.setComparisonOperator(conditionColumn.getComparisonOperator().name());
		searchCondition.setOptional(conditionColumn.isOptional());
		searchCondition.setTrim(conditionColumn.isTrim());
		return searchCondition;
	}

	private static GDao2VirtualEntity.SortCondition convertFrom(GOrderByColumn orderByColumn) {
		GDao2VirtualEntity.SortCondition sortColumn = new GDao2VirtualEntity.SortCondition();
		sortColumn.setSortColumn(orderByColumn.getVirtualColumnName());
		sortColumn.setSortMode(orderByColumn.getSortMode().name());
		return sortColumn;
	}

	private static GDao2VirtualEntity.GroupCondition convertFrom(GGroupByColumn groupByColumn) {
		GDao2VirtualEntity.GroupCondition groupColumn = new GDao2VirtualEntity.GroupCondition();
		groupColumn.setGroupColumn(groupByColumn.getVirtualColumnName());
		return groupColumn;
	}


	public static GSearchParameter setParameterToGSearchParameter(GSearchParameter parameter) {
		for (int i = 0; i < parameter.getConditionColumnSize(); i++) {
			GDBVirtualColumn dbVirtualColumn = parameter.getSearchCondition().getConditionColumn(i).getDBVirtualColumn();
			if (dbVirtualColumn.isTypeString()) {
				parameter.set(i, "");
			}
			else if (dbVirtualColumn.isTypeInteger()) {
				parameter.set(i, 0);
			}
			else if (dbVirtualColumn.isTypeDate()) {
				parameter.set(i, new Date());
			}
			else if (dbVirtualColumn.isTypeDecimal()) {
				parameter.set(i, new BigDecimal(0));
			}
			else if (dbVirtualColumn.isTypeNumber()) {
				parameter.set(i, 0f);
			}
			else if (dbVirtualColumn.isTypeLong()) {
				parameter.set(i, 0L);
			}
			else if (dbVirtualColumn.isTypeBinary()) {
				parameter.set(i, new GBinary());
			}
		}

		return parameter;
	}
}
