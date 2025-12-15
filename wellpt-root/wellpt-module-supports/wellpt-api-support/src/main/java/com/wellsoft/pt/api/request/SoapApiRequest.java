package com.wellsoft.pt.api.request;


import java.io.Serializable;

/**
 * Description: soap 服务的请求参数封装
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
public class SoapApiRequest extends ApiRequest {
    private static final long serialVersionUID = 232321320545699772L;

    protected Operation operation;

    public SoapApiRequest(Operation operation) {
        this.operation = operation;
    }

    public Operation operation() {
        return operation;
    }


    public static class Operation implements Serializable {
        private String namespace;
        private String operation;

        public Operation() {
        }

        public Operation(String namespace, String operation) {
            this.namespace = namespace;
            this.operation = operation;
        }

        public String getNamespace() {
            return namespace;
        }

        public void setNamespace(String namespace) {
            this.namespace = namespace;
        }

        public String getOperation() {
            return operation;
        }

        public void setOperation(String operation) {
            this.operation = operation;
        }
    }


}
