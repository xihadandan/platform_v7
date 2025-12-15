package com.wellsoft.pt.app.facade.service;

import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.service.Facade;
import com.wellsoft.pt.app.bean.AppDefCodeHisDto;

import java.util.List;

/**
 * Description:
 *
 * @author chenq
 * @date 2018/12/7
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/12/7    chenq		2018/12/7		Create
 * </pre>
 */
public interface AppDefCodeHisFacadeService extends Facade {


    /**
     * 新增代码历史数据
     *
     * @param codeDto
     */
    void addCodeHis(AppDefCodeHisDto codeDto);

    /**
     * 获取脚本内容
     *
     * @param uuid
     * @return
     */
    String getScriptContent(String uuid);

    /**
     * @param relaBusizUuid
     * @param scriptType
     * @return
     */
    List<AppDefCodeHisDto> listByPage(String relaBusizUuid, String scriptType, PagingInfo page);


}
