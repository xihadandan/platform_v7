package com.wellsoft.pt.basicdata.printtemplate.dao;

import com.wellsoft.pt.basicdata.printtemplate.entity.PrintTemplate;
import com.wellsoft.pt.jpa.dao.JpaDao;

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
public interface PrintTemplateDao extends JpaDao<PrintTemplate, String> {

    Long countByCategory(String category);

    public abstract Double getLatestVersionById(String id);

    public abstract PrintTemplate getById(String id);

    public abstract Long countById(String id);

}
