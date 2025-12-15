package com.wellsoft.pt.api.adapter;

import com.wellsoft.pt.api.response.ApiResponse;
import org.springframework.stereotype.Component;

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
@Component
public class BaseJsonApiAdapter extends AbstractJsonApiAdapter<ApiResponse> {


    @Override
    public String name() {
        return "基础json报文适配器";
    }
}
