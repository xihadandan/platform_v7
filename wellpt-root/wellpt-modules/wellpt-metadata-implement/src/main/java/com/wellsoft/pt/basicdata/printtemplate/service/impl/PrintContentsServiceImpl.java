package com.wellsoft.pt.basicdata.printtemplate.service.impl;

import com.wellsoft.pt.basicdata.printtemplate.dao.PrintContentsDao;
import com.wellsoft.pt.basicdata.printtemplate.entity.PrintContents;
import com.wellsoft.pt.basicdata.printtemplate.service.PrintContentsService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author zhongzh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2019年1月17日.1	zhongzh		2019年1月17日		Create
 * </pre>
 * @date 2019年1月17日
 */
@Service
public class PrintContentsServiceImpl extends AbstractJpaServiceImpl<PrintContents, PrintContentsDao, String> implements
        PrintContentsService {

    private Logger logger = LoggerFactory.getLogger(PrintContentsServiceImpl.class);

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.printtemplate.service.PrintContentsService#listByTemplateUuid(java.lang.String)
     */
    @Override
    public List<PrintContents> listByTemplateUuid(String templateUuid) {
        return dao.listByTemplateUuid(templateUuid);
    }

}
