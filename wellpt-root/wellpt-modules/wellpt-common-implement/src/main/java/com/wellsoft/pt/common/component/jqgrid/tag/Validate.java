package com.wellsoft.pt.common.component.jqgrid.tag;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validate {

    final static int _INTEGER = 1;
    final static int _DOUBLE = 2;

    public static boolean is(Object value, int type) {
        switch (type) {
            case _INTEGER:
                return isInteger(value);
            case _DOUBLE:
                return isDouble(value);
            default:
                return true;
        }
    }

    public static boolean isNull(Object value) {
        if (value == null || "".equals(value)) {
            return true;
        }
        return false;
    }

    public static boolean isInteger(Object value) {
        if (isNull(value)) {
            return true;
        }
        Pattern integerPattern = Pattern.compile("[+-]?\\d+");
        Matcher integerMatcher = integerPattern.matcher(value.toString());
        if (integerMatcher.matches()) {
            return true;
        }
        return false;
    }

    private static boolean isDouble(Object value) {
        if (isNull(value)) {
            return true;
        }
        Pattern integerPattern = Pattern.compile("[+-]?\\d+[\\.]?\\d*");
        Matcher integerMatcher = integerPattern.matcher(value.toString());
        if (integerMatcher.matches()) {
            return true;
        }
        return false;
    }

}
