package com.wellsoft.pt.ei.dto;

/**
 * @Auther: yt
 * @Date: 2021/9/30 15:06
 * @Description:
 */
public class ImportEntity<T, D> {

    private T obj;

    private D sorce;

    private String sorceJson;


    private boolean postProcess = false;

    public T getObj() {
        return obj;
    }

    public void setObj(T obj) {
        this.obj = obj;
    }

    public boolean isPostProcess() {
        return postProcess;
    }

    public void setPostProcess(boolean postProcess) {
        this.postProcess = postProcess;
    }

    public D getSorce() {
        return sorce;
    }

    public void setSorce(D sorce) {
        this.sorce = sorce;
    }

    public String getSorceJson() {
        return sorceJson;
    }

    public void setSorceJson(String sorceJson) {
        this.sorceJson = sorceJson;
    }
}
