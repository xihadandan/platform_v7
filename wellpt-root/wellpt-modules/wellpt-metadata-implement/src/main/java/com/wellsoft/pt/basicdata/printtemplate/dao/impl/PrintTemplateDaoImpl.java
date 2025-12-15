package com.wellsoft.pt.basicdata.printtemplate.dao.impl;

import com.wellsoft.pt.basicdata.printtemplate.dao.PrintTemplateDao;
import com.wellsoft.pt.basicdata.printtemplate.entity.PrintTemplate;
import com.wellsoft.pt.jpa.dao.impl.AbstractJpaDaoImpl;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

/**
 * Description: 打印模板数据层访问类
 *
 * @author zhouyq
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-3-21.1	zhouyq		2013-3-21		Create
 * </pre>
 * @date 2013-3-21
 */
@Repository
public class PrintTemplateDaoImpl extends AbstractJpaDaoImpl<PrintTemplate, String> implements PrintTemplateDao {

    /**
     * 根据ID计算已经存在的模板定义数
     */
    public static final String COUNT_BY_ID = "select count(*) from PrintTemplate def where def.id = :id";
    // 根据模板定义ID获取最新的版本
    public static final String GET_LATEST_VERSION_BY_ID = "select max(version) as version from PrintTemplate def where def.id = :id group by def.id";
    public static final String COUNT_BY_CATEGORY = "select count(*) from PrintTemplate pt where pt.category = :category";
    private static final String QUERY_BY_ID = "from PrintTemplate o where o.id = :id "
            + "and exists(select id, max(version) from PrintTemplate definition "
            + "group by id having definition.id = o.id and max(version) = o.version)";

    @Override
    public PrintTemplate getById(String id) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("id", id);
        return getOneByHQL(QUERY_BY_ID, values);
    }


    /**
     * 根据流程分类计算已经存在的流程定义数
     */
    @Override
    public Long countByCategory(String category) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("category", category);
        return this.getNumberByHQL(COUNT_BY_CATEGORY, values);
    }

    /**
     * 根据模板定义ID获取最新的版本
     *
     * @param id
     * @return
     */
    @Override
    public Double getLatestVersionById(String id) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("id", id);
        return getNumberByHQL(GET_LATEST_VERSION_BY_ID, values);
    }

    /**
     * 根据ID计算已经存在的模板定义数
     *
     * @param id
     * @return
     */
    @Override
    public Long countById(String id) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("id", id);
        return getNumberByHQL(COUNT_BY_ID, values);
    }

}
