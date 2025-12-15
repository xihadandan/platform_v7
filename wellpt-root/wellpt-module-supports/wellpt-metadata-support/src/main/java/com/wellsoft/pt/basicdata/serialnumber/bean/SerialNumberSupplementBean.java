package com.wellsoft.pt.basicdata.serialnumber.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @Auther: yt
 * @Date: 2022/5/9 10:47
 * @Description:
 */
public class SerialNumberSupplementBean implements Serializable {
    private static final long serialVersionUID = -2941781829343315381L;

    /**
     * uuid
     */
    private String uuid;
    /**
     * 关键部分
     */
    private String keyPart;
    /**
     * 头部
     */
    private String headPart;
    /**
     * 尾部
     */
    private String lastPart;

    /**
     * 最新指针
     */
    private Integer pointer;

    /**
     * 可补指针
     */
    private List<Integer> pointerList = new ArrayList<>();

    public String getKeyPart() {
        return keyPart;
    }

    public void setKeyPart(String keyPart) {
        this.keyPart = keyPart;
    }

    public String getHeadPart() {
        return headPart;
    }

    public void setHeadPart(String headPart) {
        this.headPart = headPart;
    }

    public String getLastPart() {
        return lastPart;
    }

    public void setLastPart(String lastPart) {
        this.lastPart = lastPart;
    }

    public Integer getPointer() {
        return pointer;
    }

    public void setPointer(Integer pointer) {
        this.pointer = pointer;
    }

    public List<Integer> getPointerList() {
        return pointerList;
    }

    public void setPointerList(List<Integer> pointerList) {
        this.pointerList = pointerList;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
