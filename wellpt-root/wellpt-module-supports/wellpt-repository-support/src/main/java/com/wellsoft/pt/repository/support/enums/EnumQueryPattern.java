package com.wellsoft.pt.repository.support.enums;

/**
 * Description: 模糊匹配模式s
 *
 * @author Administrator
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-3-15.1	hunt		2014-3-15		Create
 * </pre>
 * @date 2014-3-15
 */
public enum EnumQueryPattern {
    /**
     * 精确查询
     **/
    PRECISE,
    /**
     * 右匹配
     */
    RIGTH_MATCH,
    /**
     * 左匹配
     */
    LEFT_MATCH,
    /**
     * 全模糊
     */
    FUZZY;


    public static String getMatchPatternStr(EnumQueryPattern qpattern, String fieldValue) {
        String patternStr = null;
        switch (qpattern) {
            case FUZZY:
                patternStr = "^.*" + fieldValue + ".*$";
                break;
            case RIGTH_MATCH:
                patternStr = "^.*" + fieldValue + "$";
                break;
            case LEFT_MATCH:
                patternStr = "^" + fieldValue + ".*$";
                break;
            case PRECISE:
                patternStr = fieldValue;
        }
        return patternStr;
    }
}
