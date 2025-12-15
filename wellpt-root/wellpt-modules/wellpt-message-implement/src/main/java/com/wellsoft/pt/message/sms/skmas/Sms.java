package com.wellsoft.pt.message.sms.skmas;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Sms complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="Sms">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="sn" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="smsId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="spNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="phoneNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="content" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="createTime" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Sms", propOrder = {
        "sn",
        "smsId",
        "spNumber",
        "phoneNumber",
        "content",
        "createTime"
})
public class Sms {

    protected String sn;
    protected String smsId;
    protected String spNumber;
    protected String phoneNumber;
    protected String content;
    protected String createTime;

    /**
     * Gets the value of the sn property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getSn() {
        return sn;
    }

    /**
     * Sets the value of the sn property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setSn(String value) {
        this.sn = value;
    }

    /**
     * Gets the value of the smsId property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getSmsId() {
        return smsId;
    }

    /**
     * Sets the value of the smsId property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setSmsId(String value) {
        this.smsId = value;
    }

    /**
     * Gets the value of the spNumber property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getSpNumber() {
        return spNumber;
    }

    /**
     * Sets the value of the spNumber property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setSpNumber(String value) {
        this.spNumber = value;
    }

    /**
     * Gets the value of the phoneNumber property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Sets the value of the phoneNumber property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setPhoneNumber(String value) {
        this.phoneNumber = value;
    }

    /**
     * Gets the value of the content property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getContent() {
        return content;
    }

    /**
     * Sets the value of the content property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setContent(String value) {
        this.content = value;
    }

    /**
     * Gets the value of the createTime property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getCreateTime() {
        return createTime;
    }

    /**
     * Sets the value of the createTime property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setCreateTime(String value) {
        this.createTime = value;
    }

    @Override
    public String toString() {
        return "Sms [sn=" + sn + ", smsId=" + smsId + ", spNumber=" + spNumber + ", phoneNumber=" + phoneNumber
                + ", content=" + content + ", createTime=" + createTime + "]";
    }
}
