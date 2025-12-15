package com.wellsoft.pt.basicdata.printtemplate.dao.impl;

import com.wellsoft.pt.basicdata.printtemplate.dao.PrintContentsDao;
import com.wellsoft.pt.basicdata.printtemplate.entity.PrintContents;
import com.wellsoft.pt.jpa.dao.impl.AbstractJpaDaoImpl;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author zhongzh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年12月3日.1	zhongzh		2018年12月3日		Create
 * </pre>
 * @date 2018年12月3日
 */
@Repository
public class PrintContentsDaoImpl extends AbstractJpaDaoImpl<PrintContents, String> implements PrintContentsDao {

    @Override
    public List<PrintContents> listByTemplateUuid(String templateUuid) {
        String hql = "from PrintContents t where t.printTemplate.uuid = :templateUuid order by t.sortOrder asc";
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("templateUuid", templateUuid);
        return listByHQL(hql, values);
    }
}
