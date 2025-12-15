package com.wellsoft.pt.log.service;

import com.wellsoft.context.jdbc.support.QueryData;
import com.wellsoft.pt.log.support.ElasticSearchSysLogParams;

/**
 * Description:
 *
 * @author chenq
 * @date 2019/1/14
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/1/14    chenq		2019/1/14		Create
 * </pre>
 */
public interface ElasticSearchLogService {


    QueryData querySysLogs(ElasticSearchSysLogParams params);

    long countSysLogs(ElasticSearchSysLogParams params);

}
