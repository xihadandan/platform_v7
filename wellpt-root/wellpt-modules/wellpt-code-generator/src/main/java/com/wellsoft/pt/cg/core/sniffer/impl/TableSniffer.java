package com.wellsoft.pt.cg.core.sniffer.impl;

import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.cg.core.Context;
import com.wellsoft.pt.cg.core.source.Source;
import com.wellsoft.pt.cg.core.source.TableSource;
import com.wellsoft.pt.cg.core.support.ColumnRemark;
import com.wellsoft.pt.jpa.hibernate.SessionFactoryUtils;
import com.wellsoft.pt.mt.entity.Tenant;
import com.wellsoft.pt.mt.facade.service.TenantFacadeService;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.engine.jdbc.spi.JdbcConnectionAccess;
import org.hibernate.engine.spi.SessionImplementor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据表嗅探器
 *
 * @author lmw
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015-7-8.1	lmw		2015-7-8		Create
 * </pre>
 * @date 2015-7-8
 */
@Service
@Transactional
public class TableSniffer extends AbstractSniffer {
    public static final String PARAM_TB = "tb";// 上下文的数据字段
    public static final String DECOLLATOR = ";";// 分隔符号

    /**
     * 嗅探
     *
     * @param context
     * @return
     */
    @Override
    public Source sniffer(Context context) {
        TableSource source = new TableSource();
        try {
            JdbcConnectionAccess jdbcConnectionAccess = ((SessionImplementor) SessionFactoryUtils
                    .getMultiTenantSessionFactory().getCurrentSession()).getJdbcConnectionAccess();
            Connection connection = jdbcConnectionAccess.obtainConnection();
            String tb = (String) context.getParam(PARAM_TB);
            if (!StringUtils.isNotBlank(tb)) {
                throw new RuntimeException("Do not specify table ");
            }
            String[] tbs = tb.split(DECOLLATOR);
            TenantFacadeService tenantService = ApplicationContextHolder.getBean(
                    TenantFacadeService.class);
            Tenant tenant = tenantService.getById(SpringSecurityUtils.getCurrentTenantId());
            for (String t : tbs) {
                // 列备注
                Map<String, Object> values = new HashMap<String, Object>();
                values.put("tableName", t.toUpperCase());
                values.put("owner", tenant.getJdbcUsername().toUpperCase());
                List<ColumnRemark> columnRemarks = this.nativeDao.namedQuery("tableColumnQuery",
                        values,
                        ColumnRemark.class);
                for (ColumnRemark col : columnRemarks) {
                    TableSource.Column column = new TableSource.Column();
                    column.setColumnName(col.getColumnName());
                    column.setType(col.getDataType());
                    if (column.getType().indexOf("(") != -1) {
                        column.setType(
                                column.getType().substring(0, column.getType().indexOf("(")));
                    }
                    column.setRemark(col.getRemark());
                    source.addColumn(t, column);
                }

            }
            connection.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return source;
    }

    /**
     * 填充
     *
     * @param context
     * @param source
     */
    @Override
    public void impact(Context context, Source source) {
        context.setSource(source);
    }
}
