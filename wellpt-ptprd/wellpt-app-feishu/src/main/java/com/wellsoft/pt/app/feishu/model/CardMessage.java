/*
 * @(#)4/7/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.feishu.model;

import com.google.common.base.Charsets;
import com.wellsoft.context.base.BaseObject;
import com.wellsoft.pt.app.feishu.vo.FeishuConfigVo;
import org.apache.commons.compress.utils.Lists;
import org.codehaus.jackson.annotate.JsonIgnore;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

/**
 * Description:
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 4/7/25.1	    zhulh		4/7/25		    Create
 * </pre>
 * @date 4/7/25
 */
public class CardMessage extends BaseObject {

    private Header header;

    private List<Object> elements = Lists.newArrayList();

    /**
     * @return the header
     */
    public Header getHeader() {
        return header;
    }

    /**
     * @param header 要设置的header
     */
    public void setHeader(Header header) {
        this.header = header;
    }

    /**
     * @return the elements
     */
    public List<Object> getElements() {
        return elements;
    }

    /**
     * @param elements 要设置的elements
     */
    public void setElements(List<Object> elements) {
        this.elements = elements;
    }

    @JsonIgnore
    public void setTextHeader(String title) {
        Header header = new Header();
        header.setTemplate("blue");

        Text titleObj = new Text();
        titleObj.setContent(title);
        titleObj.setTag("plain_text");
        header.setTitle(titleObj);

        this.header = header;
    }

    @JsonIgnore
    public CardMessage addDivElement(String content) {
        Element element = new Element();
        element.setTag("div");
        Text text = new Text();
        text.setContent(content);
        text.setTag("lark_md");
        element.setText(text);
        this.elements.add(element);
        return this;
    }

    @JsonIgnore
    public CardMessage addLinkElement(String title, String pcUrl, String mobileUrl, FeishuConfigVo feishuConfigVo) {
        String appLink = "https://applink.feishu.cn/client/web_url/open?url=%s&mode=appCenter";
        String url = String.format(appLink, encodeURIComponent(pcUrl));

        String link = "<link url='%s' pc_url='%s' ios_url='%s' android_url='%s'>%s</link>";
        String linkContent = String.format(link, url, url, mobileUrl, mobileUrl, title);

        LinkElement element = new LinkElement();
        element.setTag("markdown");
        element.setContent(linkContent);
        this.elements.add(element);
        return this;
    }

    private String encodeURIComponent(String pcUrl) {
        try {
            return URLEncoder.encode(pcUrl, Charsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
        }
        return pcUrl;
    }

    private static final class LinkElement extends BaseObject {
        private String tag = "markdown";
        private String content;

        /**
         * @return the tag
         */
        public String getTag() {
            return tag;
        }

        /**
         * @param tag 要设置的tag
         */
        public void setTag(String tag) {
            this.tag = tag;
        }

        /**
         * @return the content
         */
        public String getContent() {
            return content;
        }

        /**
         * @param content 要设置的content
         */
        public void setContent(String content) {
            this.content = content;
        }
    }

    private static final class Element extends BaseObject {
        private String tag;
        private Text text;

        /**
         * @return the tag
         */
        public String getTag() {
            return tag;
        }

        /**
         * @param tag 要设置的tag
         */
        public void setTag(String tag) {
            this.tag = tag;
        }

        /**
         * @return the text
         */
        public Text getText() {
            return text;
        }

        /**
         * @param text 要设置的text
         */
        public void setText(Text text) {
            this.text = text;
        }
    }

    private static final class Header extends BaseObject {

        private String template;

        private Text title;

        /**
         * @return the template
         */
        public String getTemplate() {
            return template;
        }

        /**
         * @param template 要设置的template
         */
        public void setTemplate(String template) {
            this.template = template;
        }

        /**
         * @return the title
         */
        public Text getTitle() {
            return title;
        }

        /**
         * @param title 要设置的title
         */
        public void setTitle(Text title) {
            this.title = title;
        }
    }

    private static final class Text extends BaseObject {

        private String content;

        private String tag;


        /**
         * @return the content
         */
        public String getContent() {
            return content;
        }

        /**
         * @param content 要设置的content
         */
        public void setContent(String content) {
            this.content = content;
        }

        /**
         * @return the tag
         */
        public String getTag() {
            return tag;
        }

        /**
         * @param tag 要设置的tag
         */
        public void setTag(String tag) {
            this.tag = tag;
        }
    }
}
