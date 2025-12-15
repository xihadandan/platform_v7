package com.wellsoft.pt.message.sms.skmas;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Rpt complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="Rpt">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="sn" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="smsId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="phoneNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="stateCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="stateIntro" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="createTime" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Rpt", propOrder = {
        "sn",
        "smsId",
        "phoneNumber",
        "stateCode",
        "stateIntro",
        "createTime"
})
public class Rpt {

    protected String sn;
    protected String smsId;
    protected String phoneNumber;
    protected String stateCode;
    protected String stateIntro;
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
     * Gets the value of the stateCode property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getStateCode() {
        return stateCode;
    }

    /**
     * Sets the value of the stateCode property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setStateCode(String value) {
        this.stateCode = value;
    }

    /**
     * Gets the value of the stateIntro property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getStateIntro() {
        return stateIntro;
    }

    /**
     * Sets the value of the stateIntro property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setStateIntro(String value) {
        this.stateIntro = value;
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

}
