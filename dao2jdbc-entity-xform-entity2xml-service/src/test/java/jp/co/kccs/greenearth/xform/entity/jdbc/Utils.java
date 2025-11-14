package jp.co.kccs.greenearth.xform.entity.jdbc;

import jp.co.kccs.greenearth.xform.dao.dto.GDao2VirtualEntity;

import java.util.List;

public class Utils {

	public static String getParameterValue() {
		return "{\n" +
				"    \"name\": \"jdbc_tool_test_fullCaseMainEntity\",\n" +
				"    \"phyName\": null,\n" +
				"    \"databaseName\": \"gef_jdbc_tool\",\n" +
				"    \"primaryKey\": {\n" +
				"        \"name\": \"objectID\",\n" +
				"        \"phyName\": null,\n" +
				"        \"size\": 32,\n" +
				"        \"dataType\": \"STRING\",\n" +
				"        \"decimalSize\": \"-\",\n" +
				"        \"notNull\": false,\n" +
				"        \"primaryKey\": true\n" +
				"    },\n" +
				"    \"columns\": [\n" +
				"        {\n" +
				"            \"name\": \"objectID\",\n" +
				"            \"phyName\": null,\n" +
				"            \"size\": 32,\n" +
				"            \"dataType\": \"STRING\",\n" +
				"            \"decimalSize\": \"-\",\n" +
				"            \"notNull\": false,\n" +
				"            \"primaryKey\": true\n" +
				"        },\n" +
				"        {\n" +
				"            \"name\": \"StringColumn\",\n" +
				"            \"phyName\": null,\n" +
				"            \"size\": 32,\n" +
				"            \"dataType\": \"STRING\",\n" +
				"            \"decimalSize\": \"-\",\n" +
				"            \"notNull\": false,\n" +
				"            \"primaryKey\": false\n" +
				"        },\n" +
				"        {\n" +
				"            \"name\": \"IntColumn\",\n" +
				"            \"phyName\": null,\n" +
				"            \"size\": 8,\n" +
				"            \"dataType\": \"INTEGER\",\n" +
				"            \"decimalSize\": \"-\",\n" +
				"            \"notNull\": false,\n" +
				"            \"primaryKey\": false\n" +
				"        },\n" +
				"        {\n" +
				"            \"name\": \"NStringColumn\",\n" +
				"            \"phyName\": null,\n" +
				"            \"size\": 32,\n" +
				"            \"dataType\": \"STRING\",\n" +
				"            \"decimalSize\": \"-\",\n" +
				"            \"notNull\": false,\n" +
				"            \"primaryKey\": false\n" +
				"        },\n" +
				"        {\n" +
				"            \"name\": \"DateTimeColumn\",\n" +
				"            \"phyName\": null,\n" +
				"            \"size\": 0,\n" +
				"            \"dataType\": \"DATE\",\n" +
				"            \"decimalSize\": \"-\",\n" +
				"            \"notNull\": false,\n" +
				"            \"primaryKey\": false\n" +
				"        },\n" +
				"        {\n" +
				"            \"name\": \"YMColumn\",\n" +
				"            \"phyName\": null,\n" +
				"            \"size\": 6,\n" +
				"            \"dataType\": \"STRING\",\n" +
				"            \"decimalSize\": \"-\",\n" +
				"            \"notNull\": false,\n" +
				"            \"primaryKey\": false\n" +
				"        },\n" +
				"        {\n" +
				"            \"name\": \"CurrencyColumn\",\n" +
				"            \"phyName\": null,\n" +
				"            \"size\": 32,\n" +
				"            \"dataType\": \"DECIMAL\",\n" +
				"            \"decimalSize\": \"2\",\n" +
				"            \"notNull\": false,\n" +
				"            \"primaryKey\": false\n" +
				"        },\n" +
				"        {\n" +
				"            \"name\": \"LongColumn\",\n" +
				"            \"phyName\": null,\n" +
				"            \"size\": 32,\n" +
				"            \"dataType\": \"LONG\",\n" +
				"            \"decimalSize\": \"-\",\n" +
				"            \"notNull\": false,\n" +
				"            \"primaryKey\": false\n" +
				"        },\n" +
				"        {\n" +
				"            \"name\": \"CompanyCD\",\n" +
				"            \"phyName\": null,\n" +
				"            \"size\": 32,\n" +
				"            \"dataType\": \"STRING\",\n" +
				"            \"decimalSize\": \"-\",\n" +
				"            \"notNull\": false,\n" +
				"            \"primaryKey\": false\n" +
				"        },\n" +
				"        {\n" +
				"            \"name\": \"ExclusiveFG\",\n" +
				"            \"phyName\": null,\n" +
				"            \"size\": 32,\n" +
				"            \"dataType\": \"STRING\",\n" +
				"            \"decimalSize\": \"-\",\n" +
				"            \"notNull\": false,\n" +
				"            \"primaryKey\": false\n" +
				"        },\n" +
				"        {\n" +
				"            \"name\": \"RegisteredPerson\",\n" +
				"            \"phyName\": null,\n" +
				"            \"size\": 32,\n" +
				"            \"dataType\": \"STRING\",\n" +
				"            \"decimalSize\": \"-\",\n" +
				"            \"notNull\": false,\n" +
				"            \"primaryKey\": false\n" +
				"        },\n" +
				"        {\n" +
				"            \"name\": \"RegisteredDT\",\n" +
				"            \"phyName\": null,\n" +
				"            \"size\": 0,\n" +
				"            \"dataType\": \"DATE\",\n" +
				"            \"decimalSize\": \"-\",\n" +
				"            \"notNull\": false,\n" +
				"            \"primaryKey\": false\n" +
				"        },\n" +
				"        {\n" +
				"            \"name\": \"UpdatedPerson\",\n" +
				"            \"phyName\": null,\n" +
				"            \"size\": 32,\n" +
				"            \"dataType\": \"STRING\",\n" +
				"            \"decimalSize\": \"-\",\n" +
				"            \"notNull\": false,\n" +
				"            \"primaryKey\": false\n" +
				"        },\n" +
				"        {\n" +
				"            \"name\": \"UpdatedDT\",\n" +
				"            \"phyName\": null,\n" +
				"            \"size\": 0,\n" +
				"            \"dataType\": \"DATE\",\n" +
				"            \"decimalSize\": \"-\",\n" +
				"            \"notNull\": false,\n" +
				"            \"primaryKey\": false\n" +
				"        }\n" +
				"    ],\n" +
				"    \"uniqueKeys\": [\n" +
				"        {\n" +
				"            \"name\": null,\n" +
				"            \"uniqueKeyColumns\": [\n" +
				"                {\n" +
				"                    \"name\": \"IntColumn\",\n" +
				"                    \"phyName\": null,\n" +
				"                    \"size\": 8,\n" +
				"                    \"dataType\": \"INTEGER\",\n" +
				"                    \"decimalSize\": \"-\",\n" +
				"                    \"notNull\": false,\n" +
				"                    \"primaryKey\": false\n" +
				"                },\n" +
				"                {\n" +
				"                    \"name\": \"NStringColumn\",\n" +
				"                    \"phyName\": null,\n" +
				"                    \"size\": 32,\n" +
				"                    \"dataType\": \"STRING\",\n" +
				"                    \"decimalSize\": \"-\",\n" +
				"                    \"notNull\": false,\n" +
				"                    \"primaryKey\": false\n" +
				"                },\n" +
				"                {\n" +
				"                    \"name\": \"DateTimeColumn\",\n" +
				"                    \"phyName\": null,\n" +
				"                    \"size\": 0,\n" +
				"                    \"dataType\": \"DATE\",\n" +
				"                    \"decimalSize\": \"-\",\n" +
				"                    \"notNull\": false,\n" +
				"                    \"primaryKey\": false\n" +
				"                },\n" +
				"                {\n" +
				"                    \"name\": \"YMColumn\",\n" +
				"                    \"phyName\": null,\n" +
				"                    \"size\": 6,\n" +
				"                    \"dataType\": \"STRING\",\n" +
				"                    \"decimalSize\": \"-\",\n" +
				"                    \"notNull\": false,\n" +
				"                    \"primaryKey\": false\n" +
				"                }\n" +
				"            ]\n" +
				"        },\n" +
				"        {\n" +
				"            \"name\": null,\n" +
				"            \"uniqueKeyColumns\": [\n" +
				"                {\n" +
				"                    \"name\": \"StringColumn\",\n" +
				"                    \"phyName\": null,\n" +
				"                    \"size\": 32,\n" +
				"                    \"dataType\": \"STRING\",\n" +
				"                    \"decimalSize\": \"-\",\n" +
				"                    \"notNull\": false,\n" +
				"                    \"primaryKey\": false\n" +
				"                }\n" +
				"            ]\n" +
				"        },\n" +
				"        {\n" +
				"            \"name\": null,\n" +
				"            \"uniqueKeyColumns\": [\n" +
				"                {\n" +
				"                    \"name\": \"StringColumn\",\n" +
				"                    \"phyName\": null,\n" +
				"                    \"size\": 32,\n" +
				"                    \"dataType\": \"STRING\",\n" +
				"                    \"decimalSize\": \"-\",\n" +
				"                    \"notNull\": false,\n" +
				"                    \"primaryKey\": false\n" +
				"                },\n" +
				"                {\n" +
				"                    \"name\": \"IntColumn\",\n" +
				"                    \"phyName\": null,\n" +
				"                    \"size\": 8,\n" +
				"                    \"dataType\": \"INTEGER\",\n" +
				"                    \"decimalSize\": \"-\",\n" +
				"                    \"notNull\": false,\n" +
				"                    \"primaryKey\": false\n" +
				"                },\n" +
				"                {\n" +
				"                    \"name\": \"NStringColumn\",\n" +
				"                    \"phyName\": null,\n" +
				"                    \"size\": 32,\n" +
				"                    \"dataType\": \"STRING\",\n" +
				"                    \"decimalSize\": \"-\",\n" +
				"                    \"notNull\": false,\n" +
				"                    \"primaryKey\": false\n" +
				"                }\n" +
				"            ]\n" +
				"        }\n" +
				"    ],\n" +
				"    \"foreignKeys\": [\n" +
				"        {\n" +
				"            \"name\": null,\n" +
				"            \"referenceEntity\": {\n" +
				"                \"name\": \"jdbc_tool_test_fullCaseInnerJoinEntity\",\n" +
				"                \"phyName\": null,\n" +
				"                \"databaseName\": \"gef_jdbc_tool\",\n" +
				"                \"primaryKey\": {\n" +
				"                    \"name\": \"objectID\",\n" +
				"                    \"phyName\": null,\n" +
				"                    \"size\": 32,\n" +
				"                    \"dataType\": \"STRING\",\n" +
				"                    \"decimalSize\": \"-\",\n" +
				"                    \"notNull\": false,\n" +
				"                    \"primaryKey\": true\n" +
				"                },\n" +
				"                \"columns\": [\n" +
				"                    {\n" +
				"                        \"name\": \"objectID\",\n" +
				"                        \"phyName\": null,\n" +
				"                        \"size\": 32,\n" +
				"                        \"dataType\": \"STRING\",\n" +
				"                        \"decimalSize\": \"-\",\n" +
				"                        \"notNull\": false,\n" +
				"                        \"primaryKey\": true\n" +
				"                    },\n" +
				"                    {\n" +
				"                        \"name\": \"StringColumnInner\",\n" +
				"                        \"phyName\": null,\n" +
				"                        \"size\": 32,\n" +
				"                        \"dataType\": \"STRING\",\n" +
				"                        \"decimalSize\": \"-\",\n" +
				"                        \"notNull\": false,\n" +
				"                        \"primaryKey\": false\n" +
				"                    },\n" +
				"                    {\n" +
				"                        \"name\": \"IntColumnInner\",\n" +
				"                        \"phyName\": null,\n" +
				"                        \"size\": 8,\n" +
				"                        \"dataType\": \"INTEGER\",\n" +
				"                        \"decimalSize\": \"-\",\n" +
				"                        \"notNull\": false,\n" +
				"                        \"primaryKey\": false\n" +
				"                    },\n" +
				"                    {\n" +
				"                        \"name\": \"NStringColumnInner\",\n" +
				"                        \"phyName\": null,\n" +
				"                        \"size\": 32,\n" +
				"                        \"dataType\": \"STRING\",\n" +
				"                        \"decimalSize\": \"-\",\n" +
				"                        \"notNull\": false,\n" +
				"                        \"primaryKey\": false\n" +
				"                    },\n" +
				"                    {\n" +
				"                        \"name\": \"DateTimeColumnInner\",\n" +
				"                        \"phyName\": null,\n" +
				"                        \"size\": 0,\n" +
				"                        \"dataType\": \"DATE\",\n" +
				"                        \"decimalSize\": \"-\",\n" +
				"                        \"notNull\": false,\n" +
				"                        \"primaryKey\": false\n" +
				"                    },\n" +
				"                    {\n" +
				"                        \"name\": \"YMColumnInner\",\n" +
				"                        \"phyName\": null,\n" +
				"                        \"size\": 6,\n" +
				"                        \"dataType\": \"STRING\",\n" +
				"                        \"decimalSize\": \"-\",\n" +
				"                        \"notNull\": false,\n" +
				"                        \"primaryKey\": false\n" +
				"                    },\n" +
				"                    {\n" +
				"                        \"name\": \"CurrencyColumnInner\",\n" +
				"                        \"phyName\": null,\n" +
				"                        \"size\": 32,\n" +
				"                        \"dataType\": \"DECIMAL\",\n" +
				"                        \"decimalSize\": \"2\",\n" +
				"                        \"notNull\": false,\n" +
				"                        \"primaryKey\": false\n" +
				"                    },\n" +
				"                    {\n" +
				"                        \"name\": \"LongColumnInner\",\n" +
				"                        \"phyName\": null,\n" +
				"                        \"size\": 32,\n" +
				"                        \"dataType\": \"LONG\",\n" +
				"                        \"decimalSize\": \"-\",\n" +
				"                        \"notNull\": false,\n" +
				"                        \"primaryKey\": false\n" +
				"                    }\n" +
				"                ],\n" +
				"                \"uniqueKeys\": [],\n" +
				"                \"foreignKeys\": []\n" +
				"            },\n" +
				"            \"foreignKeyColumns\": [\n" +
				"                {\n" +
				"                    \"referenceColumn\": \"StringColumnInner\",\n" +
				"                    \"sourceColumn\": \"StringColumn\",\n" +
				"                    \"constValue\": \"-\"\n" +
				"                },\n" +
				"                {\n" +
				"                    \"referenceColumn\": \"NStringColumnInner\",\n" +
				"                    \"sourceColumn\": \"-\",\n" +
				"                    \"constValue\": \"9001\"\n" +
				"                }\n" +
				"            ],\n" +
				"            \"joinType\": \"INNER\"\n" +
				"        },\n" +
				"        {\n" +
				"            \"name\": null,\n" +
				"            \"referenceEntity\": {\n" +
				"                \"name\": \"jdbc_tool_test_fullCaseRightJoinEntity\",\n" +
				"                \"phyName\": null,\n" +
				"                \"databaseName\": \"gef_jdbc_tool\",\n" +
				"                \"primaryKey\": {\n" +
				"                    \"name\": \"objectID\",\n" +
				"                    \"phyName\": null,\n" +
				"                    \"size\": 32,\n" +
				"                    \"dataType\": \"STRING\",\n" +
				"                    \"decimalSize\": \"-\",\n" +
				"                    \"notNull\": false,\n" +
				"                    \"primaryKey\": true\n" +
				"                },\n" +
				"                \"columns\": [\n" +
				"                    {\n" +
				"                        \"name\": \"objectID\",\n" +
				"                        \"phyName\": null,\n" +
				"                        \"size\": 32,\n" +
				"                        \"dataType\": \"STRING\",\n" +
				"                        \"decimalSize\": \"-\",\n" +
				"                        \"notNull\": false,\n" +
				"                        \"primaryKey\": true\n" +
				"                    },\n" +
				"                    {\n" +
				"                        \"name\": \"StringColumnRight\",\n" +
				"                        \"phyName\": null,\n" +
				"                        \"size\": 32,\n" +
				"                        \"dataType\": \"STRING\",\n" +
				"                        \"decimalSize\": \"-\",\n" +
				"                        \"notNull\": false,\n" +
				"                        \"primaryKey\": false\n" +
				"                    },\n" +
				"                    {\n" +
				"                        \"name\": \"IntColumnRight\",\n" +
				"                        \"phyName\": null,\n" +
				"                        \"size\": 8,\n" +
				"                        \"dataType\": \"INTEGER\",\n" +
				"                        \"decimalSize\": \"-\",\n" +
				"                        \"notNull\": false,\n" +
				"                        \"primaryKey\": false\n" +
				"                    },\n" +
				"                    {\n" +
				"                        \"name\": \"NStringColumnRight\",\n" +
				"                        \"phyName\": null,\n" +
				"                        \"size\": 32,\n" +
				"                        \"dataType\": \"STRING\",\n" +
				"                        \"decimalSize\": \"-\",\n" +
				"                        \"notNull\": false,\n" +
				"                        \"primaryKey\": false\n" +
				"                    },\n" +
				"                    {\n" +
				"                        \"name\": \"DateTimeColumnRight\",\n" +
				"                        \"phyName\": null,\n" +
				"                        \"size\": 0,\n" +
				"                        \"dataType\": \"DATE\",\n" +
				"                        \"decimalSize\": \"-\",\n" +
				"                        \"notNull\": false,\n" +
				"                        \"primaryKey\": false\n" +
				"                    },\n" +
				"                    {\n" +
				"                        \"name\": \"YMColumnRight\",\n" +
				"                        \"phyName\": null,\n" +
				"                        \"size\": 6,\n" +
				"                        \"dataType\": \"STRING\",\n" +
				"                        \"decimalSize\": \"-\",\n" +
				"                        \"notNull\": false,\n" +
				"                        \"primaryKey\": false\n" +
				"                    },\n" +
				"                    {\n" +
				"                        \"name\": \"CurrencyColumnRight\",\n" +
				"                        \"phyName\": null,\n" +
				"                        \"size\": 32,\n" +
				"                        \"dataType\": \"DECIMAL\",\n" +
				"                        \"decimalSize\": \"2\",\n" +
				"                        \"notNull\": false,\n" +
				"                        \"primaryKey\": false\n" +
				"                    },\n" +
				"                    {\n" +
				"                        \"name\": \"LongColumnRight\",\n" +
				"                        \"phyName\": null,\n" +
				"                        \"size\": 32,\n" +
				"                        \"dataType\": \"LONG\",\n" +
				"                        \"decimalSize\": \"-\",\n" +
				"                        \"notNull\": false,\n" +
				"                        \"primaryKey\": false\n" +
				"                    }\n" +
				"                ],\n" +
				"                \"uniqueKeys\": [],\n" +
				"                \"foreignKeys\": []\n" +
				"            },\n" +
				"            \"foreignKeyColumns\": [\n" +
				"                {\n" +
				"                    \"referenceColumn\": \"NStringColumnRight\",\n" +
				"                    \"sourceColumn\": \"NStringColumn\",\n" +
				"                    \"constValue\": \"-\"\n" +
				"                },\n" +
				"                {\n" +
				"                    \"referenceColumn\": \"StringColumnRight\",\n" +
				"                    \"sourceColumn\": \"-\",\n" +
				"                    \"constValue\": \"7001\"\n" +
				"                }\n" +
				"            ],\n" +
				"            \"joinType\": \"RIGHT_OUTER\"\n" +
				"        },\n" +
				"        {\n" +
				"            \"name\": null,\n" +
				"            \"referenceEntity\": {\n" +
				"                \"name\": \"jdbc_tool_test_fullCaseLeftJoinEntity\",\n" +
				"                \"phyName\": null,\n" +
				"                \"databaseName\": \"gef_jdbc_tool\",\n" +
				"                \"primaryKey\": {\n" +
				"                    \"name\": \"objectID\",\n" +
				"                    \"phyName\": null,\n" +
				"                    \"size\": 32,\n" +
				"                    \"dataType\": \"STRING\",\n" +
				"                    \"decimalSize\": \"-\",\n" +
				"                    \"notNull\": false,\n" +
				"                    \"primaryKey\": true\n" +
				"                },\n" +
				"                \"columns\": [\n" +
				"                    {\n" +
				"                        \"name\": \"objectID\",\n" +
				"                        \"phyName\": null,\n" +
				"                        \"size\": 32,\n" +
				"                        \"dataType\": \"STRING\",\n" +
				"                        \"decimalSize\": \"-\",\n" +
				"                        \"notNull\": false,\n" +
				"                        \"primaryKey\": true\n" +
				"                    },\n" +
				"                    {\n" +
				"                        \"name\": \"StringColumnLeft\",\n" +
				"                        \"phyName\": null,\n" +
				"                        \"size\": 32,\n" +
				"                        \"dataType\": \"STRING\",\n" +
				"                        \"decimalSize\": \"-\",\n" +
				"                        \"notNull\": false,\n" +
				"                        \"primaryKey\": false\n" +
				"                    },\n" +
				"                    {\n" +
				"                        \"name\": \"IntColumnLeft\",\n" +
				"                        \"phyName\": null,\n" +
				"                        \"size\": 8,\n" +
				"                        \"dataType\": \"INTEGER\",\n" +
				"                        \"decimalSize\": \"-\",\n" +
				"                        \"notNull\": false,\n" +
				"                        \"primaryKey\": false\n" +
				"                    },\n" +
				"                    {\n" +
				"                        \"name\": \"NStringColumnLeft\",\n" +
				"                        \"phyName\": null,\n" +
				"                        \"size\": 32,\n" +
				"                        \"dataType\": \"STRING\",\n" +
				"                        \"decimalSize\": \"-\",\n" +
				"                        \"notNull\": false,\n" +
				"                        \"primaryKey\": false\n" +
				"                    },\n" +
				"                    {\n" +
				"                        \"name\": \"DateTimeColumnLeft\",\n" +
				"                        \"phyName\": null,\n" +
				"                        \"size\": 0,\n" +
				"                        \"dataType\": \"DATE\",\n" +
				"                        \"decimalSize\": \"-\",\n" +
				"                        \"notNull\": false,\n" +
				"                        \"primaryKey\": false\n" +
				"                    },\n" +
				"                    {\n" +
				"                        \"name\": \"YMColumnLeft\",\n" +
				"                        \"phyName\": null,\n" +
				"                        \"size\": 6,\n" +
				"                        \"dataType\": \"STRING\",\n" +
				"                        \"decimalSize\": \"-\",\n" +
				"                        \"notNull\": false,\n" +
				"                        \"primaryKey\": false\n" +
				"                    },\n" +
				"                    {\n" +
				"                        \"name\": \"CurrencyColumnLeft\",\n" +
				"                        \"phyName\": null,\n" +
				"                        \"size\": 32,\n" +
				"                        \"dataType\": \"DECIMAL\",\n" +
				"                        \"decimalSize\": \"2\",\n" +
				"                        \"notNull\": false,\n" +
				"                        \"primaryKey\": false\n" +
				"                    },\n" +
				"                    {\n" +
				"                        \"name\": \"LongColumnLeft\",\n" +
				"                        \"phyName\": null,\n" +
				"                        \"size\": 32,\n" +
				"                        \"dataType\": \"LONG\",\n" +
				"                        \"decimalSize\": \"-\",\n" +
				"                        \"notNull\": false,\n" +
				"                        \"primaryKey\": false\n" +
				"                    }\n" +
				"                ],\n" +
				"                \"uniqueKeys\": [],\n" +
				"                \"foreignKeys\": []\n" +
				"            },\n" +
				"            \"foreignKeyColumns\": [\n" +
				"                {\n" +
				"                    \"referenceColumn\": \"IntColumnLeft\",\n" +
				"                    \"sourceColumn\": \"IntColumn\",\n" +
				"                    \"constValue\": \"-\"\n" +
				"                },\n" +
				"                {\n" +
				"                    \"referenceColumn\": \"LongColumnLeft\",\n" +
				"                    \"sourceColumn\": \"-\",\n" +
				"                    \"constValue\": \"8001\"\n" +
				"                }\n" +
				"            ],\n" +
				"            \"joinType\": \"LEFT_OUTER\"\n" +
				"        }\n" +
				"    ]\n" +
				"}";
	}
	public static GDao2VirtualEntity getVirtualEntity() {
		GDao2VirtualEntity.ForeignKeyColumn foreignKeyColumn = new GDao2VirtualEntity.ForeignKeyColumn();
		foreignKeyColumn.setSourceColumn("testCol");
		foreignKeyColumn.setReferenceColumn("refCol");
		foreignKeyColumn.setConstValue("test");

		GDao2VirtualEntity.Column refCol = new GDao2VirtualEntity.Column();
		refCol.setName("refCol");
		refCol.setPhyName("phyRefCol");
		refCol.setDataType("STRING");
		refCol.setSize(10);
		refCol.setNotNull(true);
		refCol.setPrimaryKey(true);

		GDao2VirtualEntity.Entity referenceEntity = new GDao2VirtualEntity.Entity();
		referenceEntity.setName("refEntity");
		referenceEntity.setPhyName("phyRefEntity");
		referenceEntity.setColumns(List.of(refCol));
		referenceEntity.setDatabaseName("testDb");

		GDao2VirtualEntity.Column column = new GDao2VirtualEntity.Column();
		column.setName("testCol");
		column.setPhyName("testCol");
		column.setPrimaryKey(true);
		column.setSize(10);
		column.setNotNull(true);
		column.setDataType("STRING");

		GDao2VirtualEntity.Column column2 = new GDao2VirtualEntity.Column();
		column2.setName("testCol2");
		column2.setPhyName("testCol2");
		column2.setPrimaryKey(false);
		column2.setSize(255);
		column2.setNotNull(true);
		column2.setDataType("STRING");

		GDao2VirtualEntity.UniqueKey uniqueKey = new GDao2VirtualEntity.UniqueKey();
		uniqueKey.setName("uk1");
		uniqueKey.setUniqueKeyColumns(List.of(column, column2));

		GDao2VirtualEntity.ForeignKey foreignKey = new GDao2VirtualEntity.ForeignKey();
		foreignKey.setName("fk1");
		foreignKey.setJoinType("INNER_JOIN");
		foreignKey.setReferenceEntity(referenceEntity);
		foreignKey.setForeignKeyColumns(List.of(foreignKeyColumn));

		GDao2VirtualEntity.VirtualColumn virtualColumn1 = new GDao2VirtualEntity.VirtualColumn();
		virtualColumn1.setName("testVColumn");
		virtualColumn1.setRefColumn("testEntity.testCol");
		virtualColumn1.setDisplayName("testVColumn");

		GDao2VirtualEntity.VirtualColumn virtualColumn2 = new GDao2VirtualEntity.VirtualColumn();
		virtualColumn2.setName("testVColumn2");
		virtualColumn2.setRefColumn("testEntity.testCol2");
		virtualColumn2.setDisplayName("testVColumn2");

		GDao2VirtualEntity.Entity entity = new GDao2VirtualEntity.Entity();
		entity.setName("testEntity");
		entity.setDatabaseName("testDb");
		entity.setPhyName("testPhy");
		entity.setColumns(List.of(column, column2));
		entity.setUniqueKeys(List.of(uniqueKey));
		entity.setForeignKeys(List.of(foreignKey));

		GDao2VirtualEntity virtualEntity = new GDao2VirtualEntity();
		virtualEntity.setDisplayName("ve1");
		virtualEntity.setVeId("aaaa-aaaa-aaaa-aaaa-aaaa");
		virtualEntity.setCategoryId("bbbb-bbbb-bbbb-bbbb-bbbb");
		virtualEntity.setCategoryName("categoryTest");
		virtualEntity.setVeType("1");
		virtualEntity.setRefEntity(entity);
		virtualEntity.setColumns(List.of(virtualColumn1, virtualColumn2));
		return virtualEntity;
	}
}
