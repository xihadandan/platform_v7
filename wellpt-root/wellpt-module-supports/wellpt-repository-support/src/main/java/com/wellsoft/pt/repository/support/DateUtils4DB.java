package com.wellsoft.pt.repository.support;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils4DB {

    public static final String DB_DATETIME_PATTERN = "yyyyMMddHHmmss";

    public static String formate2DbPattern(long milliTime) {
        SimpleDateFormat sdf = new SimpleDateFormat(DB_DATETIME_PATTERN);
        return sdf.format(new Date(milliTime));
    }
}
