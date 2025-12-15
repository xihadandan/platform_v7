package com.wellsoft.pt.message.support;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

/**
 * Description: 消息发送参数
 *
 * @author chenq
 * @date 2018/9/27
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/9/27    chenq		2018/9/27		Create
 * </pre>
 */
public class MessageParams implements Serializable {


    private String templateId;//消息模板id

    private Set<String> sentWays = Sets.newLinkedHashSet();//发送方式:ON_LINE;EMAIL;SMS

    private Map<Object, Object> dataMap = Maps.newHashMap();//业务数据

    public Map<String, Object> extraData = Maps.newHashMap();// 额外数据

    private Set<String> recipientIds = Sets.newLinkedHashSet();//消息接收用户id集合

    private Set<String> recipientNames = Sets.newLinkedHashSet();//接收人名称集合

    private Set<String> mobilePns = Sets.newLinkedHashSet();//接收人手机号码集合

    private String subject;//消息主题

    private String content;//消息发送内容

    private String[] reservedTexts = new String[10];//保留文本

    private Boolean async = false;//是否异步发送

    public String getTemplateId() {
        return templateId;
    }

    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    public Set<String> getSentWays() {
        return sentWays;
    }

    public void setSentWays(Set<String> sentWays) {
        this.sentWays = sentWays;
    }

    public Map<Object, Object> getDataMap() {
        return dataMap;
    }

    public void setDataMap(Map<Object, Object> dataMap) {
        this.dataMap = dataMap;
    }

    public Set<String> getRecipientIds() {
        return recipientIds;
    }

    public void setRecipientIds(Set<String> recipientIds) {
        this.recipientIds = recipientIds;
    }

    public Set<String> getRecipientNames() {
        return recipientNames;
    }

    public void setRecipientNames(Set<String> recipientNames) {
        this.recipientNames = recipientNames;
    }

    public Set<String> getMobilePns() {
        return mobilePns;
    }

    public void setMobilePns(Set<String> mobilePns) {
        this.mobilePns = mobilePns;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String[] getReservedTexts() {
        return reservedTexts;
    }

    public void setReservedTexts(String[] reservedTexts) {
        this.reservedTexts = reservedTexts;
    }

    public Boolean getAsync() {
        return async;
    }

    public void setAsync(Boolean async) {
        this.async = async;
    }

    public Map<String, Object> getExtraData() {
        return extraData;
    }

    public void setExtraData(Map<String, Object> extraData) {
        this.extraData = extraData;
    }
}
