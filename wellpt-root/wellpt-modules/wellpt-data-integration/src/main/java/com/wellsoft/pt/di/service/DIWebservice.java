package com.wellsoft.pt.di.service;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

/**
 * Description:
 *
 * @author chenq
 * @date 2019/8/15
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/8/15    chenq		2019/8/15		Create
 * </pre>
 */
@WebService
public interface DIWebservice {

    @WebMethod
    String execute(@WebParam(name = "id") String id,
                   @WebParam(name = "requestXml") String requestXml);

    @WebMethod
    String callback(@WebParam(name = "requestId") String requestId,
                    @WebParam(name = "requestXml") String requestXml);

    @WebMethod
    String query(@WebParam(name = "requestXml") String requestXml);

}
