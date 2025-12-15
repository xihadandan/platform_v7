package com.wellsoft.pt.di.component.webservice;

import com.wellsoft.pt.di.anotation.EndpointParameter;
import com.wellsoft.pt.di.component.AbstractEndpoint;
import com.wellsoft.pt.di.component.WithoutConsumer;
import com.wellsoft.pt.di.enums.DIParameterDomType;
import com.wellsoft.pt.di.enums.EdpParameterType;

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
public class DynamicCxfWebserviceEndpoint extends
        AbstractEndpoint<DynamicCxfWebserviceDIComponent, DynamicCxfWebserviceProducer, WithoutConsumer> {


    @EndpointParameter(name = "webservice地址", type = EdpParameterType.PRODUCER)
    private String address;

    @EndpointParameter(name = "调用方法名", type = EdpParameterType.PRODUCER)
    private String method;

    @EndpointParameter(name = "是否开启CA认证", type = EdpParameterType.PRODUCER, domType = DIParameterDomType.CHECKBOX_BOOLEAN)
    private Boolean isEnableCA;

    @EndpointParameter(name = "证书主体", type = EdpParameterType.PRODUCER)
    private String subjectDN;


    @Override
    public String endpointPrefix() {
        return "pt-cxf-ws";
    }

    @Override
    public String endpointName() {
        return "平台动态CXF-Webservice服务调用";
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public boolean getIsEnableCA() {
        return isEnableCA == null ? false : isEnableCA;
    }

    public void setIsEnableCA(boolean enableCA) {
        isEnableCA = enableCA;
    }

    public String getSubjectDN() {
        return subjectDN;
    }

    public void setSubjectDN(String subjectDN) {
        this.subjectDN = subjectDN;
    }
}
