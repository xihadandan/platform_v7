package com.wellsoft.pt.api.adapter;

import com.wellsoft.pt.api.request.ApiAdapterRequest;
import com.wellsoft.pt.api.response.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Description:
 *
 * @author chenq
 * @date 2018/10/22
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/10/22    chenq		2018/10/22		Create
 * </pre>
 */
public interface WellptApiAdapter<T extends ApiResponse> {

    Logger logger = LoggerFactory.getLogger(WellptApiAdapter.class.getClass());


    T invoke(ApiAdapterRequest request);

    String name();
}
