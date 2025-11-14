enum CrudType {
    SELECT = "SELECT",
    INSERT = "INSERT",
    UPDATE = "UPDATE",
    DELETE = "DELETE"
}
enum TrimType {
    LEFT = "LEFT",
    RIGHT = "RIGHT",
    BOTH = "BOTH"
}
enum LikeType {
    BOTH = "BOTN",
    FRONT = "FRONT",
    BACK = "BACK"
}
enum ScriptType {
    VEID = "veId",
    CATEGORYID = "categoryId"
}
enum DbType {
    ORACLE = "oracle",
    DB2 = "db2",
    MYSQL = "mysql"
}
enum EntityConditionType {
    ENTITYID = "entityId",
    DATABASEID = "databaseId"
}
export { CrudType, TrimType, LikeType, ScriptType, DbType, EntityConditionType };