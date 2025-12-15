package com.wellsoft.pt.basicdata.directorydata.facade;

import com.wellsoft.context.service.AbstractApiFacade;
import com.wellsoft.pt.basicdata.directorydata.service.DirectoryDataService;

import javax.annotation.Resource;

/**
 * Description: 是否可以不用此类，直接调用service即可
 *
 * @author huanglinchuan
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015-1-15.1	huanglinchuan		2015-1-15		Create
 * </pre>
 * @date 2015-1-15
 */
public class DirectoryDataApiFacade extends AbstractApiFacade {

    @Resource
    DirectoryDataService service;

}
