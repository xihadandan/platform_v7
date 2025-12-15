package com.wellsoft.pt.api.adapter;

import com.google.common.base.Throwables;
import com.wellsoft.context.enums.ApiCodeEnum;
import com.wellsoft.pt.api.request.ApiAdapterRequest;
import com.wellsoft.pt.api.request.SoapApiRequest;
import com.wellsoft.pt.api.response.ApiResponse;
import com.wellsoft.pt.api.support.SoapCall;
import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axis2.AxisFault;
import org.apache.axis2.databinding.utils.BeanUtil;
import org.apache.axis2.util.StreamWrapper;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Description:
 *
 * @author chenq
 * @date 2018/11/8
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/11/8    chenq		2018/11/8		Create
 * </pre>
 */
public abstract class AbstractSoapApiAdapter<T extends ApiResponse> implements WellptApiAdapter {

    Class<T> responseClass;


    public AbstractSoapApiAdapter() {
        try {
            Type genType = this.getClass().getGenericSuperclass();
            if (genType instanceof ParameterizedType) {
                Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
                responseClass = (Class<T>) params[0];
            }
        } catch (Exception e) {
            throw Throwables.propagate(e);
        }
    }


    @Override
    public ApiResponse invoke(ApiAdapterRequest request) {
        SoapApiRequest soapApiRequest = (SoapApiRequest) request.getApiRequest();
        try {
            //请求对象直接转为请求报文
            javax.xml.stream.XMLStreamReader reader = BeanUtil.getPullParser(
                    request.getApiRequest());
            StreamWrapper parser = new StreamWrapper(reader);
            OMXMLParserWrapper parserWrapper = OMXMLBuilderFactory.createStAXOMBuilder(
                    OMAbstractFactory.getOMFactory(), parser);
            OMElement inputElement = parserWrapper.getDocumentElement();
            OMElement responseElement = SoapCall.buildAxis().endpoint(request.getEndpoint()).mtom(
                    true).operation(
                    soapApiRequest.operation().getOperation(),
                    soapApiRequest.operation().getNamespace()).input(inputElement).readTimeout(
                    request.getConnectionLiveSeconds() * 1000).post();
            return ApiResponse.build().data(responseElement);

        } catch (AxisFault fault) {
            logger.error("soap请求异常：", fault);
            return ApiResponse.build().code(ApiCodeEnum.API_SYSTEM_ERROR.getCode()).msg(
                    fault.getMessage());
        } catch (Exception e) {
            logger.error("soap请求异常：", e);
            return ApiResponse.build().code(ApiCodeEnum.API_SYSTEM_ERROR);
        }

    }
}
