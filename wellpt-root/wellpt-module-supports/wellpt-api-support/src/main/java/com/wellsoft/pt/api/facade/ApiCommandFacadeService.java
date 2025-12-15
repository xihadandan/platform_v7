package com.wellsoft.pt.api.facade;

import com.wellsoft.context.annotation.Description;
import com.wellsoft.context.service.Facade;
import com.wellsoft.pt.api.dto.ApiCommandDetailDto;
import com.wellsoft.pt.api.request.ApiAdapterRequest;

import java.util.List;

/**
 * Description:
 *
 * @author chenq
 * @date 2018/8/13
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/8/13    chenq		2018/8/13		Create
 * </pre>
 */
public interface ApiCommandFacadeService extends Facade {


    /**
     * 获取指令报文明细
     *
     * @param commandUuid
     * @return
     */
    ApiCommandDetailDto getCommandDetailByCommandUuid(String commandUuid);


    /**
     * 重发
     *
     * @param uuids
     * @param requestBody
     */
    void resend(List<String> uuids, String requestBody);

    @Description("保存适配器指令")
    String saveAdapterRequestCommand(ApiAdapterRequest request);


}
