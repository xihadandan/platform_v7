(function (factory) {
    "use strict";
    if (typeof define === 'function' && define.amd) {
        // AMD. Register as an anonymous module.
        define([], factory);
    } else {
        // Browser globals
        factory();
    }
}(function () {
    var preservedFields = [
        {
            "elementType": "id",
            "name": "uuid",
            "dataType": "string",
            "column": "uuid",
            "length": 255
        },
        {
            "elementType": "property",
            "name": "creator",
            "dataType": "string",
            "column": "creator",
            "length": 255
        },
        {
            "elementType": "property",
            "name": "create_time",
            "dataType": "java.sql.Timestamp",
            "column": "create_time",
            "length": null
        },
        {
            "elementType": "property",
            "name": "modifier",
            "dataType": "string",
            "column": "modifier",
            "length": 255
        },
        {
            "elementType": "property",
            "name": "modify_time",
            "dataType": "java.sql.Timestamp",
            "column": "modify_time",
            "length": null
        },
        {
            "elementType": "property",
            "name": "rec_ver",
            "dataType": "integer",
            "column": "rec_ver",
            "length": null
        },
        {
            "elementType": "property",
            "name": "form_uuid",
            "dataType": "string",
            "column": "form_uuid",
            "length": 255
        },
        {
            "elementType": "property",
            "name": "status",
            "dataType": "string",
            "column": "status",
            "length": 10
        },
        {
            "elementType": "property",
            "name": "signature_",
            "dataType": "string",
            "column": "signature_",
            "length": 50
        },
        {
            "elementType": "property",
            "name": "version",
            "dataType": "string",
            "column": "version",
            "length": 255
        },
        {
            "elementType": "property",
            "name": "system_unit_id",
            "dataType": "string",
            "column": "system_unit_id",
            "length": 32
        },
        {
            "elementType": "id",
            "name": "uuid",
            "dataType": "string",
            "column": "uuid",
            "length": 255
        },
        {
            "elementType": "property",
            "name": "creator",
            "dataType": "string",
            "column": "creator",
            "length": 255
        },
        {
            "elementType": "property",
            "name": "modify_time",
            "dataType": "java.sql.Timestamp",
            "column": "create_time",
            "length": null
        },
        {
            "elementType": "property",
            "name": "modifier",
            "dataType": "string",
            "column": "modifier",
            "length": 255
        },
        {
            "elementType": "property",
            "name": "modify_time",
            "dataType": "java.sql.Timestamp",
            "column": "modify_time",
            "length": null
        },
        {
            "elementType": "property",
            "name": "rec_ver",
            "dataType": "integer",
            "column": "rec_ver",
            "length": null
        },
        {
            "elementType": "property",
            "name": "data_uuid",
            "dataType": "string",
            "column": "data_uuid",
            "length": 255
        },
        {
            "elementType": "property",
            "name": "mainform_data_uuid",
            "dataType": "string",
            "column": "mainform_data_uuid",
            "length": 255
        },
        {
            "elementType": "property",
            "name": "mainform_form_uuid",
            "dataType": "string",
            "column": "mainform_form_uuid",
            "length": 255
        },
        {
            "elementType": "property",
            "name": "sort_order",
            "dataType": "string",
            "column": "sort_order",
            "length": 255
        },
        {
            "elementType": "property",
            "name": "parent_uuid",
            "dataType": "string",
            "column": "parent_uuid",
            "length": 255
        },
        {
            "name": "ADD"
        },
        {
            "name": "ANALYZE"
        },
        {
            "name": "ASC"
        },
        {
            "name": "BETWEEN"
        },
        {
            "name": "BLOB"
        },
        {
            "name": "CALL"
        },
        {
            "name": "CHANGE"
        },
        {
            "name": "CHECK"
        },
        {
            "name": "CONDITION"
        },
        {
            "name": "CONTINUE"
        },
        {
            "name": "CROSS"
        },
        {
            "name": "CURRENT_TIMESTAMP"
        },
        {
            "name": "DATABASE"
        },
        {
            "name": "DAY_MICROSECOND"
        },
        {
            "name": "DEC"
        },
        {
            "name": "DEFAULT"
        },
        {
            "name": "DESC"
        },
        {
            "name": "DISTINCT"
        },
        {
            "name": "DOUBLE"
        },
        {
            "name": "EACH"
        },
        {
            "name": "ENCLOSED"
        },
        {
            "name": "EXIT"
        },
        {
            "name": "FETCH"
        },
        {
            "name": "FLOAT8"
        },
        {
            "name": "FOREIGN"
        },
        {
            "name": "GOTO"
        },
        {
            "name": "HAVING"
        },
        {
            "name": "HOUR_MINUTE"
        },
        {
            "name": "IGNORE"
        },
        {
            "name": "INFILE"
        },
        {
            "name": "INSENSITIVE"
        },
        {
            "name": "INT1"
        },
        {
            "name": "INT4"
        },
        {
            "name": "INTERVAL"
        },
        {
            "name": "ITERATE"
        },
        {
            "name": "KEYS"
        },
        {
            "name": "LEADING"
        },
        {
            "name": "LIKE"
        },
        {
            "name": "LINES"
        },
        {
            "name": "LOCALTIMESTAMP"
        },
        {
            "name": "LONGBLOB"
        },
        {
            "name": "LOW_PRIORITY"
        },
        {
            "name": "MEDIUMINT"
        },
        {
            "name": "MINUTE_MICROSECOND"
        },
        {
            "name": "MODIFIES"
        },
        {
            "name": "NO_WRITE_TO_BINLOG"
        },
        {
            "name": "ON"
        },
        {
            "name": "OPTIONALLY"
        },
        {
            "name": "OUT"
        },
        {
            "name": "PRECISION"
        },
        {
            "name": "PURGE"
        },
        {
            "name": "READ"
        },
        {
            "name": "REFERENCES"
        },
        {
            "name": "RENAME"
        },
        {
            "name": "REQUIRE"
        },
        {
            "name": "REVOKE"
        },
        {
            "name": "SCHEMA"
        },
        {
            "name": "SELECT"
        },
        {
            "name": "SET"
        },
        {
            "name": "SPATIAL"
        },
        {
            "name": "SQLEXCEPTION"
        },
        {
            "name": "SQL_BIG_RESULT"
        },
        {
            "name": "SSL"
        },
        {
            "name": "TABLE"
        },
        {
            "name": "TINYBLOB"
        },
        {
            "name": "TO"
        },
        {
            "name": "TRUE"
        },
        {
            "name": "UNIQUE"
        },
        {
            "name": "UPDATE"
        },
        {
            "name": "USING"
        },
        {
            "name": "UTC_TIMESTAMP"
        },
        {
            "name": "VARCHAR"
        },
        {
            "name": "WHEN"
        },
        {
            "name": "WITH"
        },
        {
            "name": "XOR"
        },
        {
            "name": "ALL"
        },
        {
            "name": "AND"
        },
        {
            "name": "ASENSITIVE"
        },
        {
            "name": "BIGINT"
        },
        {
            "name": "BOTH"
        },
        {
            "name": "CASCADE"
        },
        {
            "name": "CHAR"
        },
        {
            "name": "COLLATE"
        },
        {
            "name": "CONNECTION"
        },
        {
            "name": "CONVERT"
        },
        {
            "name": "CURRENT_DATE"
        },
        {
            "name": "CURRENT_USER"
        },
        {
            "name": "DATABASES"
        },
        {
            "name": "DAY_MINUTE"
        },
        {
            "name": "DECIMAL"
        },
        {
            "name": "DELAYED"
        },
        {
            "name": "DESCRIBE"
        },
        {
            "name": "DISTINCTROW"
        },
        {
            "name": "DROP"
        },
        {
            "name": "ELSE"
        },
        {
            "name": "ESCAPED"
        },
        {
            "name": "EXPLAIN"
        },
        {
            "name": "FLOAT"
        },
        {
            "name": "FOR"
        },
        {
            "name": "FROM"
        },
        {
            "name": "GRANT"
        },
        {
            "name": "HIGH_PRIORITY"
        },
        {
            "name": "HOUR_SECOND"
        },
        {
            "name": "IN"
        },
        {
            "name": "INNER"
        },
        {
            "name": "INSERT"
        },
        {
            "name": "INT2"
        },
        {
            "name": "INT8"
        },
        {
            "name": "INTO"
        },
        {
            "name": "JOIN"
        },
        {
            "name": "KILL"
        },
        {
            "name": "LEAVE"
        },
        {
            "name": "LIMIT"
        },
        {
            "name": "LOAD"
        },
        {
            "name": "LOCK"
        },
        {
            "name": "LONGTEXT"
        },
        {
            "name": "MATCH"
        },
        {
            "name": "MEDIUMTEXT"
        },
        {
            "name": "MINUTE_SECOND"
        },
        {
            "name": "NATURAL"
        },
        {
            "name": "NULL"
        },
        {
            "name": "OPTIMIZE"
        },
        {
            "name": "OR"
        },
        {
            "name": "OUTER"
        },
        {
            "name": "PRIMARY"
        },
        {
            "name": "RAID0"
        },
        {
            "name": "READS"
        },
        {
            "name": "REGEXP"
        },
        {
            "name": "REPEAT"
        },
        {
            "name": "RESTRICT"
        },
        {
            "name": "RIGHT"
        },
        {
            "name": "SCHEMAS"
        },
        {
            "name": "SENSITIVE"
        },
        {
            "name": "SHOW"
        },
        {
            "name": "SPECIFIC"
        },
        {
            "name": "SQLSTATE"
        },
        {
            "name": "SQL_CALC_FOUND_ROWS"
        },
        {
            "name": "STARTING"
        },
        {
            "name": "TERMINATED"
        },
        {
            "name": "TINYINT"
        },
        {
            "name": "TRAILING"
        },
        {
            "name": "UNDO"
        },
        {
            "name": "UNLOCK"
        },
        {
            "name": "USAGE"
        },
        {
            "name": "UTC_DATE"
        },
        {
            "name": "VALUES"
        },
        {
            "name": "VARCHARACTER"
        },
        {
            "name": "WHERE"
        },
        {
            "name": "WRITE"
        },
        {
            "name": "YEAR_MONTH"
        },
        {
            "name": "ALTER"
        },
        {
            "name": "AS"
        },
        {
            "name": "BEFORE"
        },
        {
            "name": "BINARY"
        },
        {
            "name": "BY"
        },
        {
            "name": "CASE"
        },
        {
            "name": "CHARACTER"
        },
        {
            "name": "COLUMN"
        },
        {
            "name": "CONSTRAINT"
        },
        {
            "name": "CREATE"
        },
        {
            "name": "CURRENT_TIME"
        },
        {
            "name": "CURSOR"
        },
        {
            "name": "DAY_HOUR"
        },
        {
            "name": "DAY_SECOND"
        },
        {
            "name": "DECLARE"
        },
        {
            "name": "DELETE"
        },
        {
            "name": "DETERMINISTIC"
        },
        {
            "name": "DIV"
        },
        {
            "name": "DUAL"
        },
        {
            "name": "ELSEIF"
        },
        {
            "name": "EXISTS"
        },
        {
            "name": "FALSE"
        },
        {
            "name": "FLOAT4"
        },
        {
            "name": "FORCE"
        },
        {
            "name": "FULLTEXT"
        },
        {
            "name": "GROUP"
        },
        {
            "name": "HOUR_MICROSECOND"
        },
        {
            "name": "IF"
        },
        {
            "name": "INDEX"
        },
        {
            "name": "INOUT"
        },
        {
            "name": "INT"
        },
        {
            "name": "INT3"
        },
        {
            "name": "INTEGER"
        },
        {
            "name": "IS"
        },
        {
            "name": "KEY"
        },
        {
            "name": "LABEL"
        },
        {
            "name": "LEFT"
        },
        {
            "name": "LINEAR"
        },
        {
            "name": "LOCALTIME"
        },
        {
            "name": "LONG"
        },
        {
            "name": "LOOP"
        },
        {
            "name": "MEDIUMBLOB"
        },
        {
            "name": "MIDDLEINT"
        },
        {
            "name": "MOD"
        },
        {
            "name": "NOT"
        },
        {
            "name": "NUMERIC"
        },
        {
            "name": "OPTION"
        },
        {
            "name": "ORDER"
        },
        {
            "name": "OUTFILE"
        },
        {
            "name": "PROCEDURE"
        },
        {
            "name": "RANGE"
        },
        {
            "name": "REAL"
        },
        {
            "name": "RELEASE"
        },
        {
            "name": "REPLACE"
        },
        {
            "name": "RETURN"
        },
        {
            "name": "RLIKE"
        },
        {
            "name": "SECOND_MICROSECOND"
        },
        {
            "name": "SEPARATOR"
        },
        {
            "name": "SMALLINT"
        },
        {
            "name": "SQL"
        },
        {
            "name": "SQLWARNING"
        },
        {
            "name": "SQL_SMALL_RESULT"
        },
        {
            "name": "STRAIGHT_JOIN"
        },
        {
            "name": "THEN"
        },
        {
            "name": "TINYTEXT"
        },
        {
            "name": "TRIGGER"
        },
        {
            "name": "UNION"
        },
        {
            "name": "UNSIGNED"
        },
        {
            "name": "USE"
        },
        {
            "name": "UTC_TIME"
        },
        {
            "name": "VARBINARY"
        },
        {
            "name": "VARYING"
        },
        {
            "name": "WHILE"
        },
        {
            "name": "X509"
        },
        {
            "name": "ZEROFILL"
        },
        {
            "name": "!"
        },
        {
            "name": "&"
        },
        {
            "name": "("
        },
        {
            "name": ")"
        },
        {
            "name": "*"
        },
        {
            "name": "+"
        },
        {
            "name": ","
        },
        {
            "name": "-"
        },
        {
            "name": "."
        },
        {
            "name": "/"
        },
        {
            "name": ":"
        },
        {
            "name": "<"
        },
        {
            "name": "<<"
        },
        {
            "name": "="
        },
        {
            "name": ">"
        },
        {
            "name": "@"
        },
        {
            "name": "A"
        },
        {
            "name": "ABORT"
        },
        {
            "name": "ACCESS"
        },
        {
            "name": "ACCESSED"
        },
        {
            "name": "ACCOUNT"
        },
        {
            "name": "ACTIVATE"
        },
        {
            "name": "ADD"
        },
        {
            "name": "ADMIN"
        },
        {
            "name": "ADMINISTER"
        },
        {
            "name": "ADMINISTRATOR"
        },
        {
            "name": "ADVISE"
        },
        {
            "name": "ADVISOR"
        },
        {
            "name": "AFTER"
        },
        {
            "name": "ALGORITHM"
        },
        {
            "name": "ALIAS"
        },
        {
            "name": "ALL"
        },
        {
            "name": "ALLOCATE"
        },
        {
            "name": "ALLOW"
        },
        {
            "name": "ALL_ROWS"
        },
        {
            "name": "ALTER"
        },
        {
            "name": "ALWAYS"
        },
        {
            "name": "ANALYZE"
        },
        {
            "name": "ANCILLARY"
        },
        {
            "name": "AND"
        },
        {
            "name": "AND_EQUAL"
        },
        {
            "name": "ANTIJOIN"
        },
        {
            "name": "ANY"
        },
        {
            "name": "APPEND"
        },
        {
            "name": "APPLY"
        },
        {
            "name": "ARCHIVE"
        },
        {
            "name": "ARCHIVELOG"
        },
        {
            "name": "ARRAY"
        },
        {
            "name": "AS"
        },
        {
            "name": "ASC"
        },
        {
            "name": "ASSOCIATE"
        },
        {
            "name": "AT"
        },
        {
            "name": "ATTRIBUTE"
        },
        {
            "name": "ATTRIBUTES"
        },
        {
            "name": "AUDIT"
        },
        {
            "name": "AUTHENTICATED"
        },
        {
            "name": "AUTHENTICATION"
        },
        {
            "name": "AUTHID"
        },
        {
            "name": "AUTHORIZATION"
        },
        {
            "name": "AUTO"
        },
        {
            "name": "AUTOALLOCATE"
        },
        {
            "name": "AUTOEXTEND"
        },
        {
            "name": "AUTOMATIC"
        },
        {
            "name": "AVAILABILITY"
        },
        {
            "name": "BACKUP"
        },
        {
            "name": "BECOME"
        },
        {
            "name": "BEFORE"
        },
        {
            "name": "BEGIN"
        },
        {
            "name": "BEHALF"
        },
        {
            "name": "BETWEEN"
        },
        {
            "name": "BFILE"
        },
        {
            "name": "BIGFILE"
        },
        {
            "name": "BINARY_DOUBLE"
        },
        {
            "name": "BINARY_DOUBLE_INFINITY"
        },
        {
            "name": "BINARY_DOUBLE_NAN"
        },
        {
            "name": "BINARY_FLOAT"
        },
        {
            "name": "BINARY_FLOAT_INFINITY"
        },
        {
            "name": "BINARY_FLOAT_NAN"
        },
        {
            "name": "BINDING"
        },
        {
            "name": "BITMAP"
        },
        {
            "name": "BITS"
        },
        {
            "name": "BLOB"
        },
        {
            "name": "BLOCK"
        },
        {
            "name": "BLOCKS"
        },
        {
            "name": "BLOCKSIZE"
        },
        {
            "name": "BLOCK_RANGE"
        },
        {
            "name": "BODY"
        },
        {
            "name": "BOTH"
        },
        {
            "name": "BOUND"
        },
        {
            "name": "BROADCAST"
        },
        {
            "name": "BUFFER"
        },
        {
            "name": "BUFFER_CACHE"
        },
        {
            "name": "BUFFER_POOL"
        },
        {
            "name": "BUILD"
        },
        {
            "name": "BULK"
        },
        {
            "name": "BY"
        },
        {
            "name": "BYPASS_RECURSIVE_CHECK"
        },
        {
            "name": "BYPASS_UJVC"
        },
        {
            "name": "BYTE"
        },
        {
            "name": "CACHE"
        },
        {
            "name": "CACHE_CB"
        },
        {
            "name": "CACHE_INSTANCES"
        },
        {
            "name": "CACHE_TEMP_TABLE"
        },
        {
            "name": "CALL"
        },
        {
            "name": "CANCEL"
        },
        {
            "name": "CARDINALITY"
        },
        {
            "name": "CASCADE"
        },
        {
            "name": "CASE"
        },
        {
            "name": "CAST"
        },
        {
            "name": "CATEGORY"
        },
        {
            "name": "CERTIFICATE"
        },
        {
            "name": "CFILE"
        },
        {
            "name": "CHAINED"
        },
        {
            "name": "CHANGE"
        },
        {
            "name": "CHAR"
        },
        {
            "name": "CHARACTER"
        },
        {
            "name": "CHAR_CS"
        },
        {
            "name": "CHECK"
        },
        {
            "name": "CHECKPOINT"
        },
        {
            "name": "CHILD"
        },
        {
            "name": "CHOOSE"
        },
        {
            "name": "CHUNK"
        },
        {
            "name": "CIV_GB"
        },
        {
            "name": "CLASS"
        },
        {
            "name": "CLEAR"
        },
        {
            "name": "CLOB"
        },
        {
            "name": "CLONE"
        },
        {
            "name": "CLOSE"
        },
        {
            "name": "CLOSE_CACHED_OPEN_CURSORS"
        },
        {
            "name": "CLUSTER"
        },
        {
            "name": "CLUSTERING_FACTOR"
        },
        {
            "name": "COALESCE"
        },
        {
            "name": "COARSE"
        },
        {
            "name": "COLLECT"
        },
        {
            "name": "COLLECTIONS_GET_REFS"
        },
        {
            "name": "COLUMN"
        },
        {
            "name": "COLUMNS"
        },
        {
            "name": "COLUMN_STATS"
        },
        {
            "name": "COLUMN_VALUE"
        },
        {
            "name": "COMMENT"
        },
        {
            "name": "COMMIT"
        },
        {
            "name": "COMMITTED"
        },
        {
            "name": "COMPACT"
        },
        {
            "name": "COMPATIBILITY"
        },
        {
            "name": "COMPILE"
        },
        {
            "name": "COMPLETE"
        },
        {
            "name": "COMPOSITE_LIMIT"
        },
        {
            "name": "COMPRESS"
        },
        {
            "name": "COMPUTE"
        },
        {
            "name": "CONFORMING"
        },
        {
            "name": "CONNECT"
        },
        {
            "name": "CONNECT_BY_ISCYCLE"
        },
        {
            "name": "CONNECT_BY_ISLEAF"
        },
        {
            "name": "CONNECT_BY_ROOT"
        },
        {
            "name": "CONNECT_TIME"
        },
        {
            "name": "CONSIDER"
        },
        {
            "name": "CONSISTENT"
        },
        {
            "name": "CONSTRAINT"
        },
        {
            "name": "CONSTRAINTS"
        },
        {
            "name": "CONTAINER"
        },
        {
            "name": "CONTENT"
        },
        {
            "name": "CONTENTS"
        },
        {
            "name": "CONTEXT"
        },
        {
            "name": "CONTINUE"
        },
        {
            "name": "CONTROLFILE"
        },
        {
            "name": "CONVERT"
        },
        {
            "name": "CORRUPTION"
        },
        {
            "name": "COST"
        },
        {
            "name": "CPU_COSTING"
        },
        {
            "name": "CPU_PER_CALL"
        },
        {
            "name": "CPU_PER_SESSION"
        },
        {
            "name": "CREATE"
        },
        {
            "name": "CREATE_STORED_OUTLINES"
        },
        {
            "name": "CROSS"
        },
        {
            "name": "CUBE"
        },
        {
            "name": "CUBE_GB"
        },
        {
            "name": "CURRENT"
        },
        {
            "name": "CURRENT_DATE"
        },
        {
            "name": "CURRENT_SCHEMA"
        },
        {
            "name": "CURRENT_TIME"
        },
        {
            "name": "CURRENT_TIMESTAMP"
        },
        {
            "name": "CURRENT_USER"
        },
        {
            "name": "CURSOR"
        },
        {
            "name": "CURSOR_SHARING_EXACT"
        },
        {
            "name": "CURSOR_SPECIFIC_SEGMENT"
        },
        {
            "name": "CYCLE"
        },
        {
            "name": "DANGLING"
        },
        {
            "name": "DATA"
        },
        {
            "name": "DATABASE"
        },
        {
            "name": "DATAFILE"
        },
        {
            "name": "DATAFILES"
        },
        {
            "name": "DATAOBJNO"
        },
        {
            "name": "DATE"
        },
        {
            "name": "DATE_MODE"
        },
        {
            "name": "DAY"
        },
        {
            "name": "DBA"
        },
        {
            "name": "DBA_RECYCLEBIN"
        },
        {
            "name": "DBTIMEZONE"
        },
        {
            "name": "DDL"
        },
        {
            "name": "DEALLOCATE"
        },
        {
            "name": "DEBUG"
        },
        {
            "name": "DEC"
        },
        {
            "name": "DECIMAL"
        },
        {
            "name": "DECLARE"
        },
        {
            "name": "DECREMENT"
        },
        {
            "name": "DEFAULT"
        },
        {
            "name": "DEFERRABLE"
        },
        {
            "name": "DEFERRED"
        },
        {
            "name": "DEFINED"
        },
        {
            "name": "DEFINER"
        },
        {
            "name": "DEGREE"
        },
        {
            "name": "DELAY"
        },
        {
            "name": "DELETE"
        },
        {
            "name": "DEMAND"
        },
        {
            "name": "DENSE_RANK"
        },
        {
            "name": "DEREF"
        },
        {
            "name": "DEREF_NO_REWRITE"
        },
        {
            "name": "DESC"
        },
        {
            "name": "DETACHED"
        },
        {
            "name": "DETERMINES"
        },
        {
            "name": "DICTIONARY"
        },
        {
            "name": "DIMENSION"
        },
        {
            "name": "DIRECTORY"
        },
        {
            "name": "DISABLE"
        },
        {
            "name": "DISASSOCIATE"
        },
        {
            "name": "DISCONNECT"
        },
        {
            "name": "DISK"
        },
        {
            "name": "DISKGROUP"
        },
        {
            "name": "DISKS"
        },
        {
            "name": "DISMOUNT"
        },
        {
            "name": "DISTINCT"
        },
        {
            "name": "DISTINGUISHED"
        },
        {
            "name": "DISTRIBUTED"
        },
        {
            "name": "DML"
        },
        {
            "name": "DML_UPDATE"
        },
        {
            "name": "DOCUMENT"
        },
        {
            "name": "DOMAIN_INDEX_NO_SORT"
        },
        {
            "name": "DOMAIN_INDEX_SORT"
        },
        {
            "name": "DOUBLE"
        },
        {
            "name": "DOWNGRADE"
        },
        {
            "name": "DRIVING_SITE"
        },
        {
            "name": "DROP"
        },
        {
            "name": "DUMP"
        },
        {
            "name": "DYNAMIC"
        },
        {
            "name": "DYNAMIC_SAMPLING"
        },
        {
            "name": "DYNAMIC_SAMPLING_EST_CDN"
        },
        {
            "name": "EACH"
        },
        {
            "name": "ELEMENT"
        },
        {
            "name": "ELSE"
        },
        {
            "name": "EMPTY"
        },
        {
            "name": "ENABLE"
        },
        {
            "name": "ENCRYPTED"
        },
        {
            "name": "ENCRYPTION"
        },
        {
            "name": "END"
        },
        {
            "name": "ENFORCE"
        },
        {
            "name": "ENFORCED"
        },
        {
            "name": "ENTRY"
        },
        {
            "name": "ERROR"
        },
        {
            "name": "ERROR_ON_OVERLAP_TIME"
        },
        {
            "name": "ESCAPE"
        },
        {
            "name": "ESTIMATE"
        },
        {
            "name": "EVENTS"
        },
        {
            "name": "EXCEPT"
        },
        {
            "name": "EXCEPTIONS"
        },
        {
            "name": "EXCHANGE"
        },
        {
            "name": "EXCLUDING"
        },
        {
            "name": "EXCLUSIVE"
        },
        {
            "name": "EXECUTE"
        },
        {
            "name": "EXEMPT"
        },
        {
            "name": "EXISTS"
        },
        {
            "name": "EXPAND_GSET_TO_UNION"
        },
        {
            "name": "EXPIRE"
        },
        {
            "name": "EXPLAIN"
        },
        {
            "name": "EXPLOSION"
        },
        {
            "name": "EXPORT"
        },
        {
            "name": "EXPR_CORR_CHECK"
        },
        {
            "name": "EXTEND"
        },
        {
            "name": "EXTENDS"
        },
        {
            "name": "EXTENT"
        },
        {
            "name": "EXTENTS"
        },
        {
            "name": "EXTERNAL"
        },
        {
            "name": "EXTERNALLY"
        },
        {
            "name": "EXTRACT"
        },
        {
            "name": "FACT"
        },
        {
            "name": "FAILED"
        },
        {
            "name": "FAILED_LOGIN_ATTEMPTS"
        },
        {
            "name": "FAILGROUP"
        },
        {
            "name": "FALSE"
        },
        {
            "name": "FAST"
        },
        {
            "name": "FBTSCAN"
        },
        {
            "name": "FIC_CIV"
        },
        {
            "name": "FIC_PIV"
        },
        {
            "name": "FILE"
        },
        {
            "name": "FILTER"
        },
        {
            "name": "FINAL"
        },
        {
            "name": "FINE"
        },
        {
            "name": "FINISH"
        },
        {
            "name": "FIRST"
        },
        {
            "name": "FIRST_ROWS"
        },
        {
            "name": "FLAGGER"
        },
        {
            "name": "FLASHBACK"
        },
        {
            "name": "FLOAT"
        },
        {
            "name": "FLOB"
        },
        {
            "name": "FLUSH"
        },
        {
            "name": "FOLLOWING"
        },
        {
            "name": "FOR"
        },
        {
            "name": "FORCE"
        },
        {
            "name": "FORCE_XML_QUERY_REWRITE"
        },
        {
            "name": "FOREIGN"
        },
        {
            "name": "FREELIST"
        },
        {
            "name": "FREELISTS"
        },
        {
            "name": "FREEPOOLS"
        },
        {
            "name": "FRESH"
        },
        {
            "name": "FROM"
        },
        {
            "name": "FULL"
        },
        {
            "name": "FUNCTION"
        },
        {
            "name": "FUNCTIONS"
        },
        {
            "name": "GATHER_PLAN_STATISTICS"
        },
        {
            "name": "GBY_CONC_ROLLUP"
        },
        {
            "name": "GENERATED"
        },
        {
            "name": "GLOBAL"
        },
        {
            "name": "GLOBALLY"
        },
        {
            "name": "GLOBAL_NAME"
        },
        {
            "name": "GLOBAL_TOPIC_ENABLED"
        },
        {
            "name": "GRANT"
        },
        {
            "name": "GROUP"
        },
        {
            "name": "GROUPING"
        },
        {
            "name": "GROUPS"
        },
        {
            "name": "GROUP_BY"
        },
        {
            "name": "GUARANTEE"
        },
        {
            "name": "GUARANTEED"
        },
        {
            "name": "GUARD"
        },
        {
            "name": "HASH"
        },
        {
            "name": "HASHKEYS"
        },
        {
            "name": "HASH_AJ"
        },
        {
            "name": "HASH_SJ"
        },
        {
            "name": "HAVING"
        },
        {
            "name": "HEADER"
        },
        {
            "name": "HEAP"
        },
        {
            "name": "HIERARCHY"
        },
        {
            "name": "HIGH"
        },
        {
            "name": "HINTSET_BEGIN"
        },
        {
            "name": "HINTSET_END"
        },
        {
            "name": "HOUR"
        },
        {
            "name": "HWM_BROKERED"
        },
        {
            "name": "ID"
        },
        {
            "name": "IDENTIFIED"
        },
        {
            "name": "IDENTIFIER"
        },
        {
            "name": "IDENTITY"
        },
        {
            "name": "IDGENERATORS"
        },
        {
            "name": "IDLE_TIME"
        },
        {
            "name": "IF"
        },
        {
            "name": "IGNORE"
        },
        {
            "name": "IGNORE_ON_CLAUSE"
        },
        {
            "name": "IGNORE_OPTIM_EMBEDDED_HINTS"
        },
        {
            "name": "IGNORE_WHERE_CLAUSE"
        },
        {
            "name": "IMMEDIATE"
        },
        {
            "name": "IMPORT"
        },
        {
            "name": "IN"
        },
        {
            "name": "INCLUDE_VERSION"
        },
        {
            "name": "INCLUDING"
        },
        {
            "name": "INCREMENT"
        },
        {
            "name": "INCREMENTAL"
        },
        {
            "name": "INDEX"
        },
        {
            "name": "INDEXED"
        },
        {
            "name": "INDEXES"
        },
        {
            "name": "INDEXTYPE"
        },
        {
            "name": "INDEXTYPES"
        },
        {
            "name": "INDEX_ASC"
        },
        {
            "name": "INDEX_COMBINE"
        },
        {
            "name": "INDEX_DESC"
        },
        {
            "name": "INDEX_FFS"
        },
        {
            "name": "INDEX_FILTER"
        },
        {
            "name": "INDEX_JOIN"
        },
        {
            "name": "INDEX_ROWS"
        },
        {
            "name": "INDEX_RRS"
        },
        {
            "name": "INDEX_SCAN"
        },
        {
            "name": "INDEX_SKIP_SCAN"
        },
        {
            "name": "INDEX_SS"
        },
        {
            "name": "INDEX_SS_ASC"
        },
        {
            "name": "INDEX_SS_DESC"
        },
        {
            "name": "INDEX_STATS"
        },
        {
            "name": "INDICATOR"
        },
        {
            "name": "INFINITE"
        },
        {
            "name": "INFORMATIONAL"
        },
        {
            "name": "INITIAL"
        },
        {
            "name": "INITIALIZED"
        },
        {
            "name": "INITIALLY"
        },
        {
            "name": "INITRANS"
        },
        {
            "name": "INLINE"
        },
        {
            "name": "INNER"
        },
        {
            "name": "INSERT"
        },
        {
            "name": "INSTANCE"
        },
        {
            "name": "INSTANCES"
        },
        {
            "name": "INSTANTIABLE"
        },
        {
            "name": "INSTANTLY"
        },
        {
            "name": "INSTEAD"
        },
        {
            "name": "INT"
        },
        {
            "name": "INTEGER"
        },
        {
            "name": "INTEGRITY"
        },
        {
            "name": "INTERMEDIATE"
        },
        {
            "name": "INTERNAL_CONVERT"
        },
        {
            "name": "INTERNAL_USE"
        },
        {
            "name": "INTERPRETED"
        },
        {
            "name": "INTERSECT"
        },
        {
            "name": "INTERVAL"
        },
        {
            "name": "INTO"
        },
        {
            "name": "INVALIDATE"
        },
        {
            "name": "IN_MEMORY_METADATA"
        },
        {
            "name": "IS"
        },
        {
            "name": "ISOLATION"
        },
        {
            "name": "ISOLATION_LEVEL"
        },
        {
            "name": "ITERATE"
        },
        {
            "name": "ITERATION_NUMBER"
        },
        {
            "name": "JAVA"
        },
        {
            "name": "JOB"
        },
        {
            "name": "JOIN"
        },
        {
            "name": "KEEP"
        },
        {
            "name": "KERBEROS"
        },
        {
            "name": "KEY"
        },
        {
            "name": "KEYFILE"
        },
        {
            "name": "KEYS"
        },
        {
            "name": "KEYSIZE"
        },
        {
            "name": "KEY_LENGTH"
        },
        {
            "name": "KILL"
        },
        {
            "name": "LAST"
        },
        {
            "name": "LATERAL"
        },
        {
            "name": "LAYER"
        },
        {
            "name": "LDAP_REGISTRATION"
        },
        {
            "name": "LDAP_REGISTRATION_ENABLED"
        },
        {
            "name": "LDAP_REG_SYNC_INTERVAL"
        },
        {
            "name": "LEADING"
        },
        {
            "name": "LEFT"
        },
        {
            "name": "LENGTH"
        },
        {
            "name": "LESS"
        },
        {
            "name": "LEVEL"
        },
        {
            "name": "LEVELS"
        },
        {
            "name": "LIBRARY"
        },
        {
            "name": "LIKE"
        },
        {
            "name": "LIKE2"
        },
        {
            "name": "LIKE4"
        },
        {
            "name": "LIKEC"
        },
        {
            "name": "LIKE_EXPAND"
        },
        {
            "name": "LIMIT"
        },
        {
            "name": "LINK"
        },
        {
            "name": "LIST"
        },
        {
            "name": "LOB"
        },
        {
            "name": "LOCAL"
        },
        {
            "name": "LOCALTIME"
        },
        {
            "name": "LOCALTIMESTAMP"
        },
        {
            "name": "LOCAL_INDEXES"
        },
        {
            "name": "LOCATION"
        },
        {
            "name": "LOCATOR"
        },
        {
            "name": "LOCK"
        },
        {
            "name": "LOCKED"
        },
        {
            "name": "LOG"
        },
        {
            "name": "LOGFILE"
        },
        {
            "name": "LOGGING"
        },
        {
            "name": "LOGICAL"
        },
        {
            "name": "LOGICAL_READS_PER_CALL"
        },
        {
            "name": "LOGICAL_READS_PER_SESSION"
        },
        {
            "name": "LOGOFF"
        },
        {
            "name": "LOGON"
        },
        {
            "name": "LONG"
        },
        {
            "name": "MAIN"
        },
        {
            "name": "MANAGE"
        },
        {
            "name": "MANAGED"
        },
        {
            "name": "MANAGEMENT"
        },
        {
            "name": "MANUAL"
        },
        {
            "name": "MAPPING"
        },
        {
            "name": "MASTER"
        },
        {
            "name": "MATCHED"
        },
        {
            "name": "MATERIALIZE"
        },
        {
            "name": "MATERIALIZED"
        },
        {
            "name": "MAX"
        },
        {
            "name": "MAXARCHLOGS"
        },
        {
            "name": "MAXDATAFILES"
        },
        {
            "name": "MAXEXTENTS"
        },
        {
            "name": "MAXIMIZE"
        },
        {
            "name": "MAXINSTANCES"
        },
        {
            "name": "MAXLOGFILES"
        },
        {
            "name": "MAXLOGHISTORY"
        },
        {
            "name": "MAXLOGMEMBERS"
        },
        {
            "name": "MAXSIZE"
        },
        {
            "name": "MAXTRANS"
        },
        {
            "name": "MAXVALUE"
        },
        {
            "name": "MEASURES"
        },
        {
            "name": "MEMBER"
        },
        {
            "name": "MEMORY"
        },
        {
            "name": "MERGE"
        },
        {
            "name": "MERGE_AJ"
        },
        {
            "name": "MERGE_CONST_ON"
        },
        {
            "name": "MERGE_SJ"
        },
        {
            "name": "METHOD"
        },
        {
            "name": "MIGRATE"
        },
        {
            "name": "MIN"
        },
        {
            "name": "MINEXTENTS"
        },
        {
            "name": "MINIMIZE"
        },
        {
            "name": "MINIMUM"
        },
        {
            "name": "MINUS"
        },
        {
            "name": "MINUTE"
        },
        {
            "name": "MINVALUE"
        },
        {
            "name": "MIRROR"
        },
        {
            "name": "MLSLABEL"
        },
        {
            "name": "MODE"
        },
        {
            "name": "MODEL"
        },
        {
            "name": "MODEL_DONTVERIFY_UNIQUENESS"
        },
        {
            "name": "MODEL_MIN_ANALYSIS"
        },
        {
            "name": "MODEL_NO_ANALYSIS"
        },
        {
            "name": "MODEL_PBY"
        },
        {
            "name": "MODEL_PUSH_REF"
        },
        {
            "name": "MODIFY"
        },
        {
            "name": "MONITORING"
        },
        {
            "name": "MONTH"
        },
        {
            "name": "MOUNT"
        },
        {
            "name": "MOVE"
        },
        {
            "name": "MOVEMENT"
        },
        {
            "name": "MULTISET"
        },
        {
            "name": "MV_MERGE"
        },
        {
            "name": "NAME"
        },
        {
            "name": "NAMED"
        },
        {
            "name": "NAN"
        },
        {
            "name": "NATIONAL"
        },
        {
            "name": "NATIVE"
        },
        {
            "name": "NATURAL"
        },
        {
            "name": "NAV"
        },
        {
            "name": "NCHAR"
        },
        {
            "name": "NCHAR_CS"
        },
        {
            "name": "NCLOB"
        },
        {
            "name": "NEEDED"
        },
        {
            "name": "NESTED"
        },
        {
            "name": "NESTED_TABLE_FAST_INSERT"
        },
        {
            "name": "NESTED_TABLE_GET_REFS"
        },
        {
            "name": "NESTED_TABLE_ID"
        },
        {
            "name": "NESTED_TABLE_SET_REFS"
        },
        {
            "name": "NESTED_TABLE_SET_SETID"
        },
        {
            "name": "NETWORK"
        },
        {
            "name": "NEVER"
        },
        {
            "name": "NEW"
        },
        {
            "name": "NEXT"
        },
        {
            "name": "NLS_CALENDAR"
        },
        {
            "name": "NLS_CHARACTERSET"
        },
        {
            "name": "NLS_COMP"
        },
        {
            "name": "NLS_CURRENCY"
        },
        {
            "name": "NLS_DATE_FORMAT"
        },
        {
            "name": "NLS_DATE_LANGUAGE"
        },
        {
            "name": "NLS_ISO_CURRENCY"
        },
        {
            "name": "NLS_LANG"
        },
        {
            "name": "NLS_LANGUAGE"
        },
        {
            "name": "NLS_LENGTH_SEMANTICS"
        },
        {
            "name": "NLS_NCHAR_CONV_EXCP"
        },
        {
            "name": "NLS_NUMERIC_CHARACTERS"
        },
        {
            "name": "NLS_SORT"
        },
        {
            "name": "NLS_SPECIAL_CHARS"
        },
        {
            "name": "NLS_TERRITORY"
        },
        {
            "name": "NL_AJ"
        },
        {
            "name": "NL_SJ"
        },
        {
            "name": "NO"
        },
        {
            "name": "NOAPPEND"
        },
        {
            "name": "NOARCHIVELOG"
        },
        {
            "name": "NOAUDIT"
        },
        {
            "name": "NOCACHE"
        },
        {
            "name": "NOCOMPRESS"
        },
        {
            "name": "NOCPU_COSTING"
        },
        {
            "name": "NOCYCLE"
        },
        {
            "name": "NODELAY"
        },
        {
            "name": "NOFORCE"
        },
        {
            "name": "NOGUARANTEE"
        },
        {
            "name": "NOLOGGING"
        },
        {
            "name": "NOMAPPING"
        },
        {
            "name": "NOMAXVALUE"
        },
        {
            "name": "NOMINIMIZE"
        },
        {
            "name": "NOMINVALUE"
        },
        {
            "name": "NOMONITORING"
        },
        {
            "name": "NONE"
        },
        {
            "name": "NOORDER"
        },
        {
            "name": "NOOVERRIDE"
        },
        {
            "name": "NOPARALLEL"
        },
        {
            "name": "NOPARALLEL_INDEX"
        },
        {
            "name": "NORELY"
        },
        {
            "name": "NOREPAIR"
        },
        {
            "name": "NORESETLOGS"
        },
        {
            "name": "NOREVERSE"
        },
        {
            "name": "NOREWRITE"
        },
        {
            "name": "NORMAL"
        },
        {
            "name": "NOROWDEPENDENCIES"
        },
        {
            "name": "NOSEGMENT"
        },
        {
            "name": "NOSORT"
        },
        {
            "name": "NOSTRICT"
        },
        {
            "name": "NOSWITCH"
        },
        {
            "name": "NOT"
        },
        {
            "name": "NOTHING"
        },
        {
            "name": "NOVALIDATE"
        },
        {
            "name": "NOWAIT"
        },
        {
            "name": "NO_ACCESS"
        },
        {
            "name": "NO_BASETABLE_MULTIMV_REWRITE"
        },
        {
            "name": "NO_BUFFER"
        },
        {
            "name": "NO_CPU_COSTING"
        },
        {
            "name": "NO_EXPAND"
        },
        {
            "name": "NO_EXPAND_GSET_TO_UNION"
        },
        {
            "name": "NO_FACT"
        },
        {
            "name": "NO_FILTERING"
        },
        {
            "name": "NO_INDEX"
        },
        {
            "name": "NO_INDEX_FFS"
        },
        {
            "name": "NO_INDEX_SS"
        },
        {
            "name": "NO_MERGE"
        },
        {
            "name": "NO_MODEL_PUSH_REF"
        },
        {
            "name": "NO_MONITORING"
        },
        {
            "name": "NO_MULTIMV_REWRITE"
        },
        {
            "name": "NO_ORDER_ROLLUPS"
        },
        {
            "name": "NO_PARALLEL"
        },
        {
            "name": "NO_PARALLEL_INDEX"
        },
        {
            "name": "NO_PARTIAL_COMMIT"
        },
        {
            "name": "NO_PRUNE_GSETS"
        },
        {
            "name": "NO_PUSH_PRED"
        },
        {
            "name": "NO_PUSH_SUBQ"
        },
        {
            "name": "NO_QKN_BUFF"
        },
        {
            "name": "NO_QUERY_TRANSFORMATION"
        },
        {
            "name": "NO_REF_CASCADE"
        },
        {
            "name": "NO_REWRITE"
        },
        {
            "name": "NO_SEMIJOIN"
        },
        {
            "name": "NO_SET_TO_JOIN"
        },
        {
            "name": "NO_STAR_TRANSFORMATION"
        },
        {
            "name": "NO_STATS_GSETS"
        },
        {
            "name": "NO_SWAP_JOIN_INPUTS"
        },
        {
            "name": "NO_TRIGGER"
        },
        {
            "name": "NO_UNNEST"
        },
        {
            "name": "NO_USE_HASH"
        },
        {
            "name": "NO_USE_MERGE"
        },
        {
            "name": "NO_USE_NL"
        },
        {
            "name": "NO_XML_QUERY_REWRITE"
        },
        {
            "name": "NULL"
        },
        {
            "name": "NULLS"
        },
        {
            "name": "NUMBER"
        },
        {
            "name": "NUMERIC"
        },
        {
            "name": "NVARCHAR2"
        },
        {
            "name": "OBJECT"
        },
        {
            "name": "OBJNO"
        },
        {
            "name": "OBJNO_REUSE"
        },
        {
            "name": "OF"
        },
        {
            "name": "OFF"
        },
        {
            "name": "OFFLINE"
        },
        {
            "name": "OID"
        },
        {
            "name": "OIDINDEX"
        },
        {
            "name": "OLD"
        },
        {
            "name": "ON"
        },
        {
            "name": "ONLINE"
        },
        {
            "name": "ONLY"
        },
        {
            "name": "OPAQUE"
        },
        {
            "name": "OPAQUE_TRANSFORM"
        },
        {
            "name": "OPAQUE_XCANONICAL"
        },
        {
            "name": "OPCODE"
        },
        {
            "name": "OPEN"
        },
        {
            "name": "OPERATOR"
        },
        {
            "name": "OPTIMAL"
        },
        {
            "name": "OPTIMIZER_FEATURES_ENABLE"
        },
        {
            "name": "OPTIMIZER_GOAL"
        },
        {
            "name": "OPTION"
        },
        {
            "name": "OPT_ESTIMATE"
        },
        {
            "name": "OR"
        },
        {
            "name": "ORA_ROWSCN"
        },
        {
            "name": "ORDER"
        },
        {
            "name": "ORDERED"
        },
        {
            "name": "ORDERED_PREDICATES"
        },
        {
            "name": "ORGANIZATION"
        },
        {
            "name": "OR_EXPAND"
        },
        {
            "name": "OUTER"
        },
        {
            "name": "OUTLINE"
        },
        {
            "name": "OUT_OF_LINE"
        },
        {
            "name": "OVER"
        },
        {
            "name": "OVERFLOW"
        },
        {
            "name": "OVERFLOW_NOMOVE"
        },
        {
            "name": "OVERLAPS"
        },
        {
            "name": "OWN"
        },
        {
            "name": "PACKAGE"
        },
        {
            "name": "PACKAGES"
        },
        {
            "name": "PARALLEL"
        },
        {
            "name": "PARALLEL_INDEX"
        },
        {
            "name": "PARAMETERS"
        },
        {
            "name": "PARENT"
        },
        {
            "name": "PARITY"
        },
        {
            "name": "PARTIALLY"
        },
        {
            "name": "PARTITION"
        },
        {
            "name": "PARTITIONS"
        },
        {
            "name": "PARTITION_HASH"
        },
        {
            "name": "PARTITION_LIST"
        },
        {
            "name": "PARTITION_RANGE"
        },
        {
            "name": "PASSWORD"
        },
        {
            "name": "PASSWORD_GRACE_TIME"
        },
        {
            "name": "PASSWORD_LIFE_TIME"
        },
        {
            "name": "PASSWORD_LOCK_TIME"
        },
        {
            "name": "PASSWORD_REUSE_MAX"
        },
        {
            "name": "PASSWORD_REUSE_TIME"
        },
        {
            "name": "PASSWORD_VERIFY_FUNCTION"
        },
        {
            "name": "PCTFREE"
        },
        {
            "name": "PCTINCREASE"
        },
        {
            "name": "PCTTHRESHOLD"
        },
        {
            "name": "PCTUSED"
        },
        {
            "name": "PCTVERSION"
        },
        {
            "name": "PERCENT"
        },
        {
            "name": "PERFORMANCE"
        },
        {
            "name": "PERMANENT"
        },
        {
            "name": "PFILE"
        },
        {
            "name": "PHYSICAL"
        },
        {
            "name": "PIV_GB"
        },
        {
            "name": "PIV_SSF"
        },
        {
            "name": "PLAN"
        },
        {
            "name": "PLSQL_CODE_TYPE"
        },
        {
            "name": "PLSQL_DEBUG"
        },
        {
            "name": "PLSQL_OPTIMIZE_LEVEL"
        },
        {
            "name": "PLSQL_WARNINGS"
        },
        {
            "name": "POLICY"
        },
        {
            "name": "POST_TRANSACTION"
        },
        {
            "name": "POWER"
        },
        {
            "name": "PQ_DISTRIBUTE"
        },
        {
            "name": "PQ_MAP"
        },
        {
            "name": "PQ_NOMAP"
        },
        {
            "name": "PREBUILT"
        },
        {
            "name": "PRECEDING"
        },
        {
            "name": "PRECISION"
        },
        {
            "name": "PREPARE"
        },
        {
            "name": "PRESENT"
        },
        {
            "name": "PRESERVE"
        },
        {
            "name": "PRIMARY"
        },
        {
            "name": "PRIOR"
        },
        {
            "name": "PRIVATE"
        },
        {
            "name": "PRIVATE_SGA"
        },
        {
            "name": "PRIVILEGE"
        },
        {
            "name": "PRIVILEGES"
        },
        {
            "name": "PROCEDURE"
        },
        {
            "name": "PROFILE"
        },
        {
            "name": "PROGRAM"
        },
        {
            "name": "PROJECT"
        },
        {
            "name": "PROTECTED"
        },
        {
            "name": "PROTECTION"
        },
        {
            "name": "PUBLIC"
        },
        {
            "name": "PURGE"
        },
        {
            "name": "PUSH_PRED"
        },
        {
            "name": "PUSH_SUBQ"
        },
        {
            "name": "PX_GRANULE"
        },
        {
            "name": "QB_NAME"
        },
        {
            "name": "QUERY"
        },
        {
            "name": "QUERY_BLOCK"
        },
        {
            "name": "QUEUE"
        },
        {
            "name": "QUEUE_CURR"
        },
        {
            "name": "QUEUE_ROWP"
        },
        {
            "name": "QUIESCE"
        },
        {
            "name": "QUOTA"
        },
        {
            "name": "RANDOM"
        },
        {
            "name": "RANGE"
        },
        {
            "name": "RAPIDLY"
        },
        {
            "name": "RAW"
        },
        {
            "name": "RBA"
        },
        {
            "name": "READ"
        },
        {
            "name": "READS"
        },
        {
            "name": "REAL"
        },
        {
            "name": "REBALANCE"
        },
        {
            "name": "REBUILD"
        },
        {
            "name": "RECORDS_PER_BLOCK"
        },
        {
            "name": "RECOVER"
        },
        {
            "name": "RECOVERABLE"
        },
        {
            "name": "RECOVERY"
        },
        {
            "name": "RECYCLE"
        },
        {
            "name": "RECYCLEBIN"
        },
        {
            "name": "REDUCED"
        },
        {
            "name": "REDUNDANCY"
        },
        {
            "name": "REF"
        },
        {
            "name": "REFERENCE"
        },
        {
            "name": "REFERENCED"
        },
        {
            "name": "REFERENCES"
        },
        {
            "name": "REFERENCING"
        },
        {
            "name": "REFRESH"
        },
        {
            "name": "REF_CASCADE_CURSOR"
        },
        {
            "name": "REGEXP_LIKE"
        },
        {
            "name": "REGISTER"
        },
        {
            "name": "REJECT"
        },
        {
            "name": "REKEY"
        },
        {
            "name": "RELATIONAL"
        },
        {
            "name": "RELY"
        },
        {
            "name": "REMOTE_MAPPED"
        },
        {
            "name": "RENAME"
        },
        {
            "name": "REPAIR"
        },
        {
            "name": "REPLACE"
        },
        {
            "name": "REQUIRED"
        },
        {
            "name": "RESET"
        },
        {
            "name": "RESETLOGS"
        },
        {
            "name": "RESIZE"
        },
        {
            "name": "RESOLVE"
        },
        {
            "name": "RESOLVER"
        },
        {
            "name": "RESOURCE"
        },
        {
            "name": "RESTORE_AS_INTERVALS"
        },
        {
            "name": "RESTRICT"
        },
        {
            "name": "RESTRICTED"
        },
        {
            "name": "RESTRICT_ALL_REF_CONS"
        },
        {
            "name": "RESUMABLE"
        },
        {
            "name": "RESUME"
        },
        {
            "name": "RETENTION"
        },
        {
            "name": "RETURN"
        },
        {
            "name": "RETURNING"
        },
        {
            "name": "REUSE"
        },
        {
            "name": "REVERSE"
        },
        {
            "name": "REVOKE"
        },
        {
            "name": "REWRITE"
        },
        {
            "name": "REWRITE_OR_ERROR"
        },
        {
            "name": "RIGHT"
        },
        {
            "name": "ROLE"
        },
        {
            "name": "ROLES"
        },
        {
            "name": "ROLLBACK"
        },
        {
            "name": "ROLLUP"
        },
        {
            "name": "ROW"
        },
        {
            "name": "ROWDEPENDENCIES"
        },
        {
            "name": "ROWID"
        },
        {
            "name": "ROWNUM"
        },
        {
            "name": "ROWS"
        },
        {
            "name": "ROW_LENGTH"
        },
        {
            "name": "RULE"
        },
        {
            "name": "RULES"
        },
        {
            "name": "SAMPLE"
        },
        {
            "name": "SAVEPOINT"
        },
        {
            "name": "SAVE_AS_INTERVALS"
        },
        {
            "name": "SB4"
        },
        {
            "name": "SCALE"
        },
        {
            "name": "SCALE_ROWS"
        },
        {
            "name": "SCAN"
        },
        {
            "name": "SCAN_INSTANCES"
        },
        {
            "name": "SCHEDULER"
        },
        {
            "name": "SCHEMA"
        },
        {
            "name": "SCN"
        },
        {
            "name": "SCN_ASCENDING"
        },
        {
            "name": "SCOPE"
        },
        {
            "name": "SD_ALL"
        },
        {
            "name": "SD_INHIBIT"
        },
        {
            "name": "SD_SHOW"
        },
        {
            "name": "SECOND"
        },
        {
            "name": "SECURITY"
        },
        {
            "name": "SEED"
        },
        {
            "name": "SEGMENT"
        },
        {
            "name": "SEG_BLOCK"
        },
        {
            "name": "SEG_FILE"
        },
        {
            "name": "SELECT"
        },
        {
            "name": "SELECTIVITY"
        },
        {
            "name": "SEMIJOIN"
        },
        {
            "name": "SEMIJOIN_DRIVER"
        },
        {
            "name": "SEQUENCE"
        },
        {
            "name": "SEQUENCED"
        },
        {
            "name": "SEQUENTIAL"
        },
        {
            "name": "SERIALIZABLE"
        },
        {
            "name": "SERVERERROR"
        },
        {
            "name": "SESSION"
        },
        {
            "name": "SESSIONS_PER_USER"
        },
        {
            "name": "SESSIONTIMEZONE"
        },
        {
            "name": "SESSIONTZNAME"
        },
        {
            "name": "SESSION_CACHED_CURSORS"
        },
        {
            "name": "SET"
        },
        {
            "name": "SETS"
        },
        {
            "name": "SETTINGS"
        },
        {
            "name": "SET_TO_JOIN"
        },
        {
            "name": "SEVERE"
        },
        {
            "name": "SHARE"
        },
        {
            "name": "SHARED"
        },
        {
            "name": "SHARED_POOL"
        },
        {
            "name": "SHRINK"
        },
        {
            "name": "SHUTDOWN"
        },
        {
            "name": "SIBLINGS"
        },
        {
            "name": "SID"
        },
        {
            "name": "SIMPLE"
        },
        {
            "name": "SINGLE"
        },
        {
            "name": "SINGLETASK"
        },
        {
            "name": "SIZE"
        },
        {
            "name": "SKIP"
        },
        {
            "name": "SKIP_EXT_OPTIMIZER"
        },
        {
            "name": "SKIP_UNQ_UNUSABLE_IDX"
        },
        {
            "name": "SKIP_UNUSABLE_INDEXES"
        },
        {
            "name": "SMALLFILE"
        },
        {
            "name": "SMALLINT"
        },
        {
            "name": "SNAPSHOT"
        },
        {
            "name": "SOME"
        },
        {
            "name": "SORT"
        },
        {
            "name": "SOURCE"
        },
        {
            "name": "SPACE"
        },
        {
            "name": "SPECIFICATION"
        },
        {
            "name": "SPFILE"
        },
        {
            "name": "SPLIT"
        },
        {
            "name": "SPREADSHEET"
        },
        {
            "name": "SQL"
        },
        {
            "name": "SQLLDR"
        },
        {
            "name": "SQL_TRACE"
        },
        {
            "name": "STANDBY"
        },
        {
            "name": "STAR"
        },
        {
            "name": "START"
        },
        {
            "name": "STARTUP"
        },
        {
            "name": "STAR_TRANSFORMATION"
        },
        {
            "name": "STATEMENT_ID"
        },
        {
            "name": "STATIC"
        },
        {
            "name": "STATISTICS"
        },
        {
            "name": "STOP"
        },
        {
            "name": "STORAGE"
        },
        {
            "name": "STORE"
        },
        {
            "name": "STREAMS"
        },
        {
            "name": "STRICT"
        },
        {
            "name": "STRIP"
        },
        {
            "name": "STRUCTURE"
        },
        {
            "name": "SUBMULTISET"
        },
        {
            "name": "SUBPARTITION"
        },
        {
            "name": "SUBPARTITIONS"
        },
        {
            "name": "SUBPARTITION_REL"
        },
        {
            "name": "SUBSTITUTABLE"
        },
        {
            "name": "SUCCESSFUL"
        },
        {
            "name": "SUMMARY"
        },
        {
            "name": "SUPPLEMENTAL"
        },
        {
            "name": "SUSPEND"
        },
        {
            "name": "SWAP_JOIN_INPUTS"
        },
        {
            "name": "SWITCH"
        },
        {
            "name": "SWITCHOVER"
        },
        {
            "name": "SYNONYM"
        },
        {
            "name": "SYSAUX"
        },
        {
            "name": "SYSDATE"
        },
        {
            "name": "SYSDBA"
        },
        {
            "name": "SYSOPER"
        },
        {
            "name": "SYSTEM"
        },
        {
            "name": "SYSTIMESTAMP"
        },
        {
            "name": "SYS_DL_CURSOR"
        },
        {
            "name": "SYS_FBT_INSDEL"
        },
        {
            "name": "SYS_OP_BITVEC"
        },
        {
            "name": "SYS_OP_CAST"
        },
        {
            "name": "SYS_OP_COL_PRESENT"
        },
        {
            "name": "SYS_OP_ENFORCE_NOT_NULL$"
        },
        {
            "name": "SYS_OP_MINE_VALUE"
        },
        {
            "name": "SYS_OP_NOEXPAND"
        },
        {
            "name": "SYS_OP_NTCIMG$"
        },
        {
            "name": "SYS_PARALLEL_TXN"
        },
        {
            "name": "SYS_RID_ORDER"
        },
        {
            "name": "TABLE"
        },
        {
            "name": "TABLES"
        },
        {
            "name": "TABLESPACE"
        },
        {
            "name": "TABLESPACE_NO"
        },
        {
            "name": "TABLE_STATS"
        },
        {
            "name": "TABNO"
        },
        {
            "name": "TEMPFILE"
        },
        {
            "name": "TEMPLATE"
        },
        {
            "name": "TEMPORARY"
        },
        {
            "name": "TEST"
        },
        {
            "name": "THAN"
        },
        {
            "name": "THE"
        },
        {
            "name": "THEN"
        },
        {
            "name": "THREAD"
        },
        {
            "name": "THROUGH"
        },
        {
            "name": "TIME"
        },
        {
            "name": "TIMEOUT"
        },
        {
            "name": "TIMESTAMP"
        },
        {
            "name": "TIMEZONE_ABBR"
        },
        {
            "name": "TIMEZONE_HOUR"
        },
        {
            "name": "TIMEZONE_MINUTE"
        },
        {
            "name": "TIMEZONE_REGION"
        },
        {
            "name": "TIME_ZONE"
        },
        {
            "name": "TIV_GB"
        },
        {
            "name": "TIV_SSF"
        },
        {
            "name": "TO"
        },
        {
            "name": "TOPLEVEL"
        },
        {
            "name": "TRACE"
        },
        {
            "name": "TRACING"
        },
        {
            "name": "TRACKING"
        },
        {
            "name": "TRAILING"
        },
        {
            "name": "TRANSACTION"
        },
        {
            "name": "TRANSITIONAL"
        },
        {
            "name": "TREAT"
        },
        {
            "name": "TRIGGER"
        },
        {
            "name": "TRIGGERS"
        },
        {
            "name": "TRUE"
        },
        {
            "name": "TRUNCATE"
        },
        {
            "name": "TRUSTED"
        },
        {
            "name": "TUNING"
        },
        {
            "name": "TX"
        },
        {
            "name": "TYPE"
        },
        {
            "name": "TYPES"
        },
        {
            "name": "TZ_OFFSET"
        },
        {
            "name": "UB2"
        },
        {
            "name": "UBA"
        },
        {
            "name": "UID"
        },
        {
            "name": "UNARCHIVED"
        },
        {
            "name": "UNBOUND"
        },
        {
            "name": "UNBOUNDED"
        },
        {
            "name": "UNDER"
        },
        {
            "name": "UNDO"
        },
        {
            "name": "UNDROP"
        },
        {
            "name": "UNIFORM"
        },
        {
            "name": "UNION"
        },
        {
            "name": "UNIQUE"
        },
        {
            "name": "UNLIMITED"
        },
        {
            "name": "UNLOCK"
        },
        {
            "name": "UNNEST"
        },
        {
            "name": "UNPACKED"
        },
        {
            "name": "UNPROTECTED"
        },
        {
            "name": "UNQUIESCE"
        },
        {
            "name": "UNRECOVERABLE"
        },
        {
            "name": "UNTIL"
        },
        {
            "name": "UNUSABLE"
        },
        {
            "name": "UNUSED"
        },
        {
            "name": "UPDATABLE"
        },
        {
            "name": "UPDATE"
        },
        {
            "name": "UPDATED"
        },
        {
            "name": "UPD_INDEXES"
        },
        {
            "name": "UPD_JOININDEX"
        },
        {
            "name": "UPGRADE"
        },
        {
            "name": "UPSERT"
        },
        {
            "name": "UROWID"
        },
        {
            "name": "USAGE"
        },
        {
            "name": "USE"
        },
        {
            "name": "USER"
        },
        {
            "name": "USER_DEFINED"
        },
        {
            "name": "USER_RECYCLEBIN"
        },
        {
            "name": "USE_ANTI"
        },
        {
            "name": "USE_CONCAT"
        },
        {
            "name": "USE_HASH"
        },
        {
            "name": "USE_MERGE"
        },
        {
            "name": "USE_NL"
        },
        {
            "name": "USE_NL_WITH_INDEX"
        },
        {
            "name": "USE_PRIVATE_OUTLINES"
        },
        {
            "name": "USE_SEMI"
        },
        {
            "name": "USE_STORED_OUTLINES"
        },
        {
            "name": "USE_TTT_FOR_GSETS"
        },
        {
            "name": "USE_WEAK_NAME_RESL"
        },
        {
            "name": "USING"
        },
        {
            "name": "VALIDATE"
        },
        {
            "name": "VALIDATION"
        },
        {
            "name": "VALUE"
        },
        {
            "name": "VALUES"
        },
        {
            "name": "VARCHAR"
        },
        {
            "name": "VARCHAR2"
        },
        {
            "name": "VARRAY"
        },
        {
            "name": "VARYING"
        },
        {
            "name": "VECTOR_READ"
        },
        {
            "name": "VECTOR_READ_TRACE"
        },
        {
            "name": "VERSION"
        },
        {
            "name": "VERSIONS"
        },
        {
            "name": "VIEW"
        },
        {
            "name": "WAIT"
        },
        {
            "name": "WELLFORMED"
        },
        {
            "name": "WHEN"
        },
        {
            "name": "WHENEVER"
        },
        {
            "name": "WHERE"
        },
        {
            "name": "WHITESPACE"
        },
        {
            "name": "WITH"
        },
        {
            "name": "WITHIN"
        },
        {
            "name": "WITHOUT"
        },
        {
            "name": "WORK"
        },
        {
            "name": "WRITE"
        },
        {
            "name": "XID"
        },
        {
            "name": "XMLATTRIBUTES"
        },
        {
            "name": "XMLCOLATTVAL"
        },
        {
            "name": "XMLELEMENT"
        },
        {
            "name": "XMLFOREST"
        },
        {
            "name": "XMLPARSE"
        },
        {
            "name": "XMLSCHEMA"
        },
        {
            "name": "XMLTYPE"
        },
        {
            "name": "X_DYN_PRUNE"
        },
        {
            "name": "YEAR"
        },
        {
            "name": "ZONE"
        },
        {
            "name": "["
        },
        {
            "name": "]"
        },
        {
            "name": "^"
        },
        {
            "name": "|"
        },
        {
            "name": "ADD"
        },
        {
            "name": "ALL"
        },
        {
            "name": "ALTER"
        },
        {
            "name": "AND"
        },
        {
            "name": "ANY"
        },
        {
            "name": "AS"
        },
        {
            "name": "ASC"
        },
        {
            "name": "AUTHORIZATION"
        },
        {
            "name": "BACKUP"
        },
        {
            "name": "BEGIN"
        },
        {
            "name": "BETWEEN"
        },
        {
            "name": "BREAK"
        },
        {
            "name": "BROWSE"
        },
        {
            "name": "BULK"
        },
        {
            "name": "BY"
        },
        {
            "name": "CASCADE"
        },
        {
            "name": "CASE"
        },
        {
            "name": "CHECK"
        },
        {
            "name": "CHECKPOINT"
        },
        {
            "name": "CLOSE"
        },
        {
            "name": "CLUSTERED"
        },
        {
            "name": "COALESCE"
        },
        {
            "name": "COLLATE"
        },
        {
            "name": "COLUMN"
        },
        {
            "name": "COMMIT"
        },
        {
            "name": "COMPUTE"
        },
        {
            "name": "CONSTRAINT"
        },
        {
            "name": "CONTAINS"
        },
        {
            "name": "CONTAINSTABLE"
        },
        {
            "name": "CONTINUE"
        },
        {
            "name": "CONVERT"
        },
        {
            "name": "CREATE"
        },
        {
            "name": "CROSS"
        },
        {
            "name": "CURRENT"
        },
        {
            "name": "CURRENT_DATE"
        },
        {
            "name": "CURRENT_TIME"
        },
        {
            "name": "CURRENT_TIMESTAMP"
        },
        {
            "name": "CURRENT_USER"
        },
        {
            "name": "CURSOR"
        },
        {
            "name": "DATABASE"
        },
        {
            "name": "DBCC"
        },
        {
            "name": "DEALLOCATE"
        },
        {
            "name": "DECLARE"
        },
        {
            "name": "DEFAULT"
        },
        {
            "name": "DELETE"
        },
        {
            "name": "DENY"
        },
        {
            "name": "DESC"
        },
        {
            "name": "DISK"
        },
        {
            "name": "DISTINCT"
        },
        {
            "name": "DISTRIBUTED"
        },
        {
            "name": "DOUBLE"
        },
        {
            "name": "DROP"
        },
        {
            "name": "DUMMY"
        },
        {
            "name": "DUMP"
        },
        {
            "name": "ELSE"
        },
        {
            "name": "END"
        },
        {
            "name": "ERRLVL"
        },
        {
            "name": "ESCAPE"
        },
        {
            "name": "EXCEPT"
        },
        {
            "name": "EXEC"
        },
        {
            "name": "EXECUTE"
        },
        {
            "name": "EXISTS"
        },
        {
            "name": "EXIT"
        },
        {
            "name": "FETCH"
        },
        {
            "name": "FILE"
        },
        {
            "name": "FILLFACTOR"
        },
        {
            "name": "FOR"
        },
        {
            "name": "FOREIGN"
        },
        {
            "name": "FREETEXT"
        },
        {
            "name": "FREETEXTTABLE"
        },
        {
            "name": "FROM"
        },
        {
            "name": "FULL"
        },
        {
            "name": "FUNCTION"
        },
        {
            "name": "GOTO"
        },
        {
            "name": "GRANT"
        },
        {
            "name": "GROUP"
        },
        {
            "name": "HAVING"
        },
        {
            "name": "HOLDLOCK"
        },
        {
            "name": "IDENTITY"
        },
        {
            "name": "IDENTITY_INSERT"
        },
        {
            "name": "IDENTITYCOL"
        },
        {
            "name": "IF"
        },
        {
            "name": "IN"
        },
        {
            "name": "INDEX"
        },
        {
            "name": "INNER"
        },
        {
            "name": "INSERT"
        },
        {
            "name": "INTERSECT"
        },
        {
            "name": "INTO"
        },
        {
            "name": "IS"
        },
        {
            "name": "JOIN"
        },
        {
            "name": "KEY"
        },
        {
            "name": "KILL"
        },
        {
            "name": "LEFT"
        },
        {
            "name": "LIKE"
        },
        {
            "name": "LINENO"
        },
        {
            "name": "LOAD"
        },
        {
            "name": "NATIONAL"
        },
        {
            "name": "NOCHECK"
        },
        {
            "name": "NONCLUSTERED"
        },
        {
            "name": "NOT"
        },
        {
            "name": "NULL"
        },
        {
            "name": "NULLIF"
        },
        {
            "name": "OF"
        },
        {
            "name": "OFF"
        },
        {
            "name": "OFFSETS"
        },
        {
            "name": "ON"
        },
        {
            "name": "OPEN"
        },
        {
            "name": "OPENDATASOURCE"
        },
        {
            "name": "OPENQUERY"
        },
        {
            "name": "OPENROWSET"
        },
        {
            "name": "OPENXML"
        },
        {
            "name": "OPTION"
        },
        {
            "name": "OR"
        },
        {
            "name": "ORDER"
        },
        {
            "name": "OUTER"
        },
        {
            "name": "OVER"
        },
        {
            "name": "PERCENT"
        },
        {
            "name": "PLAN"
        },
        {
            "name": "PRECISION"
        },
        {
            "name": "PRIMARY"
        },
        {
            "name": "PRINT"
        },
        {
            "name": "PROC"
        },
        {
            "name": "PROCEDURE"
        },
        {
            "name": "PUBLIC"
        },
        {
            "name": "RAISERROR"
        },
        {
            "name": "READ"
        },
        {
            "name": "READTEXT"
        },
        {
            "name": "RECONFIGURE"
        },
        {
            "name": "REFERENCES"
        },
        {
            "name": "REPLICATION"
        },
        {
            "name": "RESTORE"
        },
        {
            "name": "RESTRICT"
        },
        {
            "name": "RETURN"
        },
        {
            "name": "REVOKE"
        },
        {
            "name": "RIGHT"
        },
        {
            "name": "ROLLBACK"
        },
        {
            "name": "ROWCOUNT"
        },
        {
            "name": "ROWGUIDCOL"
        },
        {
            "name": "RULE"
        },
        {
            "name": "SAVE"
        },
        {
            "name": "SCHEMA"
        },
        {
            "name": "SELECT"
        },
        {
            "name": "SESSION_USER"
        },
        {
            "name": "SET"
        },
        {
            "name": "SETUSER"
        },
        {
            "name": "SHUTDOWN"
        },
        {
            "name": "SOME"
        },
        {
            "name": "STATISTICS"
        },
        {
            "name": "SYSTEM_USER"
        },
        {
            "name": "TABLE"
        },
        {
            "name": "TEXTSIZE"
        },
        {
            "name": "THEN"
        },
        {
            "name": "TO"
        },
        {
            "name": "TOP"
        },
        {
            "name": "TRAN"
        },
        {
            "name": "TRANSACTION"
        },
        {
            "name": "TRIGGER"
        },
        {
            "name": "TRUNCATE"
        },
        {
            "name": "TSEQUAL"
        },
        {
            "name": "UNION"
        },
        {
            "name": "UNIQUE"
        },
        {
            "name": "UPDATE"
        },
        {
            "name": "UPDATETEXT"
        },
        {
            "name": "USE"
        },
        {
            "name": "USER"
        },
        {
            "name": "VALUES"
        },
        {
            "name": "VARYING"
        },
        {
            "name": "VIEW"
        },
        {
            "name": "WAITFOR"
        },
        {
            "name": "WHEN"
        },
        {
            "name": "WHERE"
        },
        {
            "name": "WHILE"
        },
        {
            "name": "WITH"
        },
        {
            "name": "WRITETEXT"
        }
    ]

    preservedFields.is = function (fieldName) {
        for (var index = 0; index < this.length; index++) {
            if (fieldName.toLowerCase() == this[index].name.toLowerCase()) {
                return true;
            }
        }
        return false;
    };

    window.preservedFields = preservedFields;
    return preservedFields;
}))