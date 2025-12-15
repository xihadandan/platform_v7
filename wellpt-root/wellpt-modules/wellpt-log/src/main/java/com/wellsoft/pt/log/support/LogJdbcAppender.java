package com.wellsoft.pt.log.support;

import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.jdbc.JDBCAppender;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * Description: 如何描述该类
 *
 * @author lmw
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015-6-30	lmw		2015-6-30		Create
 * </pre>
 * @date 2015-6-30
 */
public class LogJdbcAppender extends JDBCAppender {
    @Override
    protected void execute(String sql) throws SQLException {
        String uuid = UUID.randomUUID().toString();
        System.out.println(uuid);
        sql = StringUtils.replace(sql, "{uuid}", uuid);
        sql = StringUtils.replace(sql, "{userId}", SpringSecurityUtils.getCurrentUserId());
        sql = StringUtils.replace(sql, "{logCode}", generateCode());
        System.out.println(sql);
        super.execute(sql);
    }

    public String generateCode() {
        String str = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        return str;
    }
}
