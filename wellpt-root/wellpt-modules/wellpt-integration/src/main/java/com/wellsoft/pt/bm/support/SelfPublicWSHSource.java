package com.wellsoft.pt.bm.support;

import com.wellsoft.context.enums.ModuleID;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.basicdata.dyview.provider.AbstractViewDataSource;
import com.wellsoft.pt.basicdata.dyview.provider.ViewColumn;
import com.wellsoft.pt.basicdata.dyview.provider.ViewColumnType;
import com.wellsoft.pt.bm.service.CommercialBusinessService;
import com.wellsoft.pt.integration.security.ExchangeConfig;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import com.wellsoft.pt.unit.entity.CommonUnit;
import com.wellsoft.pt.unit.facade.service.UnitApiFacade;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Description: 投诉咨询未回复
 *
 * @author wangbx
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-12-5.1	wangbx		2013-12-5		Create
 * </pre>
 * @date 2013-12-5
 */
@Component
public class SelfPublicWSHSource extends AbstractViewDataSource {

    @Autowired
    private CommercialBusinessService commercialBusinessService;
    @Autowired
    private UnitApiFacade unitApiFacade;

    @Override
    public Collection<ViewColumn> getAllViewColumns() {
        Collection<ViewColumn> viewColumns = new ArrayList<ViewColumn>();

        ViewColumn uuid = new ViewColumn();
        uuid.setAttributeName("uuid");
        uuid.setColumnAlias("uuid");
        uuid.setColumnName("uuid");
        uuid.setColumnType(ViewColumnType.STRING);
        viewColumns.add(uuid);

        ViewColumn mc = new ViewColumn();
        mc.setAttributeName("selfPublicityApply.mc");
        mc.setColumnAlias("mc");
        mc.setColumnName("名称");
        mc.setColumnType(ViewColumnType.STRING);
        viewColumns.add(mc);

        ViewColumn sydw = new ViewColumn();
        sydw.setAttributeName("selfPublicityApply.sydw");
        sydw.setColumnAlias("sydw");
        sydw.setColumnName("授予单位");
        sydw.setColumnType(ViewColumnType.STRING);
        viewColumns.add(sydw);

        ViewColumn submitTime = new ViewColumn();
        submitTime.setAttributeName("submitTime");
        submitTime.setColumnAlias("submitTime");
        submitTime.setColumnName("提交时间");
        submitTime.setColumnType(ViewColumnType.STRING);
        viewColumns.add(submitTime);

        ViewColumn verifyTime = new ViewColumn();
        verifyTime.setAttributeName("verifyTime");
        verifyTime.setColumnAlias("verifyTime");
        verifyTime.setColumnName("审核时间");
        verifyTime.setColumnType(ViewColumnType.STRING);
        viewColumns.add(verifyTime);

        return viewColumns;
    }

    @Override
    public String getModuleId() {
        return ModuleID.DATA_EXCHANGE.getValue();
    }

    @Override
    public String getModuleName() {
        return "自主公示-审核状态";
    }

    @Override
    public List<QueryItem> query(Collection<ViewColumn> viewColumns, String whereHql, Map<String, Object> queryParams,
                                 String orderBy, PagingInfo pagingInfo) {

        Iterator<ViewColumn> it = null;
        if (viewColumns.isEmpty()) {
            it = getAllViewColumns().iterator();
        } else {
            it = viewColumns.iterator();
        }
        StringBuilder sb = new StringBuilder();
        while (it.hasNext()) {
            ViewColumn viewColumn = it.next();
            sb.append("o." + viewColumn.getAttributeName());
            sb.append(" as ");
            sb.append(viewColumn.getColumnAlias());
            if (it.hasNext()) {
                sb.append(Separator.COMMA.getValue());
            }
        }

        StringBuffer hql = new StringBuffer("select " + sb + " from SubmitVerify o where 1=1 ");
        Map<String, Object> values = new HashMap<String, Object>();

        if (whereHql == null || "".equals(whereHql)) {
            return new ArrayList<QueryItem>();
        }

        hql.append(" and " + whereHql);

        List<CommonUnit> units = unitApiFacade.getCommonUnitsByBusinessTypeIdAndUserId(
                ExchangeConfig.EXCHANGE_BUSINESS_TYPE, SpringSecurityUtils.getCurrentUserId());
        CommonUnit unit;
        if (units == null || units.size() == 0) {
            return new ArrayList<QueryItem>();

        } else {
            unit = units.get(0);
            hql.append(" and o.verifyId =:verifyId ");
            values.put("verifyId", unit.getId());
        }

        if (StringUtils.isNotBlank(orderBy)) {
            hql.append(" order by ");
            StringBuffer temp = new StringBuffer("");
            String[] orderBys = orderBy.split(Separator.COMMA.getValue());
            for (String string : orderBys) {
                temp.append(" ,o." + string);
            }
            hql.append(temp.toString().replaceFirst(",", ""));
        }

        pagingInfo.setCurrentPage(1);
        pagingInfo.setPageSize(10);
        pagingInfo.setFirst(0);
        pagingInfo.setAutoCount(true);
        List<QueryItem> queryItems = commercialBusinessService.queryQueryItem(hql.toString(), values, pagingInfo);
        return queryItems;
    }

    @Override
    public Long count(Collection<ViewColumn> viewColumns, String whereHql, Map<String, Object> queryParams) {

        StringBuffer hql = new StringBuffer("select count(*) from SubmitVerify o where 1=1 ");
        Map<String, Object> values = new HashMap<String, Object>();

        if (whereHql == null || "".equals(whereHql)) {
            return (long) 0;
        }

        hql.append(" and " + whereHql);

        List<CommonUnit> units = unitApiFacade.getCommonUnitsByBusinessTypeIdAndUserId(
                ExchangeConfig.EXCHANGE_BUSINESS_TYPE, SpringSecurityUtils.getCurrentUserId());
        CommonUnit unit;
        if (units != null && units.size() > 0) {
            unit = units.get(0);
            hql.append(" and o.verifyId =:verifyId ");
            values.put("verifyId", unit.getId());
        } else if (units == null || units.size() == 0) {
            return (long) 0;
        }

        return commercialBusinessService.findCountByHql(hql.toString(), values);
    }
}
