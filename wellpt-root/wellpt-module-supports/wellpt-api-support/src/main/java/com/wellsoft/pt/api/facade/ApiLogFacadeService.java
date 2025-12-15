package com.wellsoft.pt.api.facade;

import com.wellsoft.pt.api.entity.ApiInvokeLogEntity;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2025年09月23日   chenq	 Create
 * </pre>
 */
public interface ApiLogFacadeService {

    void saveApiInvokeLog(ApiInvokeLogEntity log);
}
