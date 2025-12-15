package com.wellsoft.pt.integration.dao.impl;

import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.pt.integration.dao.ExchangeDataRepestDao;
import com.wellsoft.pt.integration.entity.ExchangeDataRepest;
import com.wellsoft.pt.integration.security.ExchangeConfig;
import com.wellsoft.pt.jpa.dao.impl.AbstractJpaDaoImpl;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author Administrator
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-1-4.1	Administrator		2014-1-4		Create
 * </pre>
 * @date 2014-1-4
 */
@Repository
public class ExchangeDataRepestDaoImpl extends AbstractJpaDaoImpl<ExchangeDataRepest, String> implements
        ExchangeDataRepestDao {

    @Override
    public List<ExchangeDataRepest> getExchangeDataRepestIng() {
        String hql = "from ExchangeDataRepest e where e.status = 'ing' and e.sendNum<=10 order by e.modifyTime asc";
        Map<String, Object> values = new HashMap<String, Object>();
        PagingInfo paginginfo = new PagingInfo();
        paginginfo.setPageSize(ExchangeConfig.REPESTNUMBYONE);
        paginginfo.setCurrentPage(1);
        return listByHQLAndPage(hql, values, paginginfo);
    }

    // 商事登记
    @Override
    public List<ExchangeDataRepest> getExchangeDataRepestIngSSDJ() {
        String hql = "from ExchangeDataRepest e where e.systemUuid not in ('0c06d54f-c18b-4078-9be6-6d85627a9fa6','a2b949c4-8d2a-477d-9c87-167574754e83','61691d84-bfe8-4587-9f6a-3001c7a4acca','e053794f-a7dd-444f-a212-dec3186e6b89','19096467-f7a1-4175-9ca8-fbcf1b80935d') and  e.status = 'ing' and e.sendNum<=10 order by e.modifyTime asc";
        Map<String, Object> values = new HashMap<String, Object>();
        PagingInfo paginginfo = new PagingInfo();
        paginginfo.setPageSize(ExchangeConfig.REPESTNUMBYONE);
        paginginfo.setCurrentPage(1);
        return listByHQLAndPage(hql, values, paginginfo);
    }

    // 商事登记（工商数据量不较大,重发任务单独分开,以免影响其他单位）
    @Override
    public List<ExchangeDataRepest> getExchangeDataRepestIngGSDJ() {
        String hql = "from ExchangeDataRepest e where e.systemUuid = '0c06d54f-c18b-4078-9be6-6d85627a9fa6' and  e.status = 'ing' and e.sendNum<=10 order by e.modifyTime asc";
        Map<String, Object> values = new HashMap<String, Object>();
        PagingInfo paginginfo = new PagingInfo();
        paginginfo.setPageSize(ExchangeConfig.REPESTNUMBYONE);
        paginginfo.setCurrentPage(1);
        return listByHQLAndPage(hql, values, paginginfo);
    }

    // 企业设立和一照一码
    @Override
    public List<ExchangeDataRepest> getExchangeDataRepestIngYZYM() {
        String hql = "from ExchangeDataRepest e where e.systemUuid in ('a2b949c4-8d2a-477d-9c87-167574754e83','61691d84-bfe8-4587-9f6a-3001c7a4acca','e053794f-a7dd-444f-a212-dec3186e6b89','19096467-f7a1-4175-9ca8-fbcf1b80935d') and  e.status = 'ing' and e.sendNum<=10 order by e.modifyTime asc";
        Map<String, Object> values = new HashMap<String, Object>();
        PagingInfo paginginfo = new PagingInfo();
        paginginfo.setPageSize(ExchangeConfig.REPESTNUMBYONE);
        paginginfo.setCurrentPage(1);
        return listByHQLAndPage(hql, values, paginginfo);
    }
}