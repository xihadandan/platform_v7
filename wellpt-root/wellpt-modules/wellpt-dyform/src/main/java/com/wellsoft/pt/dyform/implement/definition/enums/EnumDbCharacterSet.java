package com.wellsoft.pt.dyform.implement.definition.enums;

public enum EnumDbCharacterSet {
    ORACLE_CHARACTERSET_ZHS16GBK("Oracle11g", "ZHS16GBK", 4000),
    ORACLE_CHARACTERSET_AL32UTF8("Oracle11g", "AL32UTF8", 2666);
//	ORACLE_CHARACTERSET_ZHS16CGB231280("ORACLE", "ZHS16CGB231280", ""),
//	ORACLE_CHARACTERSET_WE8ISO8859P1("ORACLE", "WE8ISO8859P1", ""),
//	ORACLE_CHARACTERSET_US7ASCII("ORACLE", "US7ASCII", ""),
//	ORACLE_CHARACTERSET_UTF8("ORACLE", "UTF8", "");

    private String dbType;
    private String parameter;
    private Integer maxVarchar2Length;
    private EnumDbCharacterSet(String dbType, String parameter, Integer maxVarchar2Length) {
        this.dbType = dbType;
        this.parameter = parameter;
        this.maxVarchar2Length = maxVarchar2Length;
    }

    public static Integer maxLengthByTypeAndCodeAndName(String dbType, String parameter) {
        for (EnumDbCharacterSet enumDbParameters : EnumDbCharacterSet.values()) {
            if (enumDbParameters.getDbType().equals(dbType)
                    && enumDbParameters.getParameter().equals(parameter)) {
                return enumDbParameters.getMaxVarchar2Length();
            }
        }
        return null;
    }

    public String getDbType() {
        return dbType;
    }

    public String getParameter() {
        return parameter;
    }

    public int getMaxVarchar2Length() {
        return maxVarchar2Length;
    }
}
