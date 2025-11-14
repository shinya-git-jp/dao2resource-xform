package jp.co.kccs.greenearth.xform.dao.entity.mapper;

import jp.co.kccs.greenearth.commons.GFrameworkUtils;
import jp.co.kccs.greenearth.xform.dao.dto.GDao2VirtualEntity;
import jp.co.kccs.greenearth.xform.jdbc.common.*;

import java.util.Objects;

import static jp.co.kccs.greenearth.xform.jdbc.common.GCommonUtils.getGenerateType;
import static jp.co.kccs.greenearth.xform.jdbc.common.GCommonUtils.isExclusion;

public class GXFormEntityMapper implements GEntityMetadataMapper<GDao2VirtualEntity.Entity> {
	@Override
	public GXFormEntity map(GDao2VirtualEntity.Entity entity) {
		return convertFromDBEntity(entity);
	}

	public static GXFormEntity convertFromDBEntity(GDao2VirtualEntity.Entity parameter) {
		GXFormEntityBuilder builder = GXFormEntity.builder()
				.name(parameter.getName())
				.databaseName(parameter.getDatabaseName())
				.phyName(parameter.getName());
		if (Objects.nonNull(parameter.getColumns())) {
			parameter.getColumns().forEach(col-> {
				addColumn(builder, col);
			});
		}
		if (Objects.nonNull(parameter.getPrimaryKey())) {
			addPrimaryKey(builder, parameter.getPrimaryKey());
		}

		if (Objects.nonNull(parameter.getUniqueKeys())) {
			parameter.getUniqueKeys().forEach(uniqueKey-> {
				addUniqueKey(builder, uniqueKey);
			});
		}

		if (Objects.nonNull(parameter.getForeignKeys()) && !parameter.getForeignKeys().isEmpty()) {
			parameter.getForeignKeys().forEach(value -> {
				addForeignKey(builder, value);
			});
		}

		return builder.build();
	}

	private static GXFormEntityBuilder addColumn(GXFormEntityBuilder builder, GDao2VirtualEntity.Column col) {
		String decimalSize = Objects.isNull(col.getDecimalSize()) ? "0" : col.getDecimalSize().equals("-") ? "0" : col.getDecimalSize();
		return builder.addColumn()
				.name(col.getName())
				.type(convertStringToGColumnTypeVE(col.getDataType()))
				.generateType(getGenerateType(col.getName()))
				.phyName(col.getName())
				.exclusiveKey(isExclusion(col.getName()))
				.scale(Integer.parseInt(decimalSize))
				.size(col.getSize())
				.finish();
	}
	private static GXFormEntityBuilder addPrimaryKey(GXFormEntityBuilder builder, GDao2VirtualEntity.Column col) {
		String decimalSize = Objects.isNull(col.getDecimalSize()) ? "0" : col.getDecimalSize().equals("-") ? "0" : col.getDecimalSize();
		return builder.addPrimaryKeyColumn()
				.name(col.getName())
				.type(convertStringToGColumnTypeVE(col.getDataType()))
				.generateType(getGenerateType(col.getName()))
				.phyName(col.getName())
				.exclusiveKey(isExclusion(col.getName()))
				.scale(Integer.parseInt(decimalSize))
				.size(col.getSize())
				.finish();
	}
	private static GXFormEntityBuilder addUniqueKey(GXFormEntityBuilder builder, GDao2VirtualEntity.UniqueKey uniqueKey) {
		GXFormEntityUniqueKeyBuilder uniqueKeyBuilder =	builder.addUniqueKey()
				.name(uniqueKey.getName());
		uniqueKey.getUniqueKeyColumns().forEach(col->{
			uniqueKeyBuilder
					.addColumn()
					.name(col.getName())
					.finish();
		});
		return uniqueKeyBuilder.finish();
	}
	private static GXFormEntityBuilder addForeignKey(GXFormEntityBuilder builder, GDao2VirtualEntity.ForeignKey foreignKey) {
		GXFormEntityForeignKeyBuilder fkBuilder = builder.addForeignKey()
				.name(foreignKey.getName())
				.refEntity(foreignKey.getReferenceEntity().getName());
		for (GDao2VirtualEntity.ForeignKeyColumn fkCol : foreignKey.getForeignKeyColumns()) {
			fkBuilder = buildForeignKey(fkBuilder, fkCol);
		}
		return fkBuilder.finish();
	}
	private static GXFormEntityForeignKeyBuilder buildForeignKey(GXFormEntityForeignKeyBuilder fkBuilder, GDao2VirtualEntity.ForeignKeyColumn foreignKeyColumn) {
		GXFormEntityForeignKeyColumnBuilder fkColBuilder = fkBuilder
				.addColumn();
		if (Objects.nonNull(foreignKeyColumn.getSourceColumn())) {
			fkColBuilder = fkColBuilder.srcColumn(foreignKeyColumn.getSourceColumn());
		}
		String constValue = foreignKeyColumn.getConstValue().equals("-") ? null : foreignKeyColumn.getConstValue();
		return fkColBuilder.refColumn(foreignKeyColumn.getReferenceColumn())
				.constValue(constValue)
				.isNull(false)
				.bindParameter(false)
				.finish();
	}

	public static GColumnType convertStringToGColumnTypeVE(String dataType) {
		if (dataType.equals("INTEGER")) {
			return GColumnType.INTEGER;
		} else if (dataType.equals("STRING")) {
			return GColumnType.VARCHAR;
		} else if (dataType.equals("DECIMAL")) {
			return GColumnType.DOUBLE;
		} else if (dataType.equals("DATE")) {
			return GColumnType.DATE;
		} else if (dataType.equals("LONG")) {
			return GColumnType.LONG;
		}
		return null;
	}

	private static GGenerateType getGenerateType(String phyName) {
		if (Objects.isNull(phyName) || phyName.equals("-")) {
			return null;
		} else {
			String registeredValue = GFrameworkUtils.getProperty("geframe.dao.InsertedDateKey");
			if (phyName.equals(registeredValue)) {
				return GGenerateType.REGISTERED_DATE;
			} else {
				String updatedValue = GFrameworkUtils.getProperty("geframe.dao.UpdatedDateKey");
				if (phyName.equals(updatedValue)) {
					return GGenerateType.UPDATED_DATE;
				}
			}
		}
		return null;
	}

}