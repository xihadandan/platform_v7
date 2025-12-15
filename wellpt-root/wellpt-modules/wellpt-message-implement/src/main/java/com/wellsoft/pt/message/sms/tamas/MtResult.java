package com.wellsoft.pt.message.sms.tamas;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>Java class for MtResult complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="MtResult">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="returnMtCount" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="returnStateCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="returnStateIntro" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MtResult", propOrder = {"returnMtCount", "returnStateCode", "returnStateIntro"})
public class MtResult {

    protected String returnMtCount;
    protected String returnStateCode;
    protected String returnStateIntro;

    /**
     * Gets the value of the returnMtCount property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getReturnMtCount() {
        return returnMtCount;
    }

    /**
     * Sets the value of the returnMtCount property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setReturnMtCount(String value) {
        this.returnMtCount = value;
    }

    /**
     * Gets the value of the returnStateCode property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getReturnStateCode() {
        return returnStateCode;
    }

    /**
     * Sets the value of the returnStateCode property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setReturnStateCode(String value) {
        this.returnStateCode = value;
    }

    /**
     * Gets the value of the returnStateIntro property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getReturnStateIntro() {
        return returnStateIntro;
    }

    /**
     * Sets the value of the returnStateIntro property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setReturnStateIntro(String value) {
        this.returnStateIntro = value;
    }

}
