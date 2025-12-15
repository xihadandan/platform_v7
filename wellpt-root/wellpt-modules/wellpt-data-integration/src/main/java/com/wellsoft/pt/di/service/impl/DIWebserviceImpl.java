package com.wellsoft.pt.di.service.impl;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.context.util.xml.converter.DataHandlerBase64Converter;
import com.wellsoft.context.util.xml.converter.DateFormateConverter;
import com.wellsoft.context.util.xml.converter.MapFormateConverter;
import com.wellsoft.pt.di.request.DIRequest;
import com.wellsoft.pt.di.request.RequestWraper;
import com.wellsoft.pt.di.response.Response;
import com.wellsoft.pt.di.response.ResponseXmlConverter;
import com.wellsoft.pt.di.service.DIWebservice;
import com.wellsoft.pt.di.service.DiCallbackRequestService;
import com.wellsoft.pt.di.util.DIUtils;

import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

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
@SOAPBinding(style = SOAPBinding.Style.RPC)
@Path("/")
public class DIWebserviceImpl implements DIWebservice {

    static XStream XSTREAM = new XStream((new StaxDriver()));

    static {
        XSTREAM.registerConverter(new DateFormateConverter("yyyy-MM-dd HH:mm:ss"));
        XSTREAM.registerConverter(new MapFormateConverter(null, true, true));
        XSTREAM.registerConverter(new DataHandlerBase64Converter());
        XSTREAM.registerConverter(new ResponseXmlConverter(null));
        XSTREAM.alias("RESPONSE", Response.class);
    }


    @Override
    @POST
    public String execute(String id, String requestXml) {
        Response response = DIUtils.execute(new RequestWraper(id, new DIRequest(requestXml)));
        return XSTREAM.toXML(response);
    }

    @Override
    @POST
    public String callback(String requestId, String requestXml) {
        DiCallbackRequestService requestService = ApplicationContextHolder.getBean(
                DiCallbackRequestService.class);
        Response response = new Response();
        if (requestService.updateCallbackWaitDeal(requestId) > 0) {
            response = requestService.executeCallback(requestId, requestXml);
        }
        return XSTREAM.toXML(response);
    }

    @Override
    @POST
    public String query(String requestXml) {
        Response response = new Response();
        return XSTREAM.toXML(response);
    }


}
