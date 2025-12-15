package com.wellsoft.pt.message.sms.skmas;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for RptResult complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="RptResult">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="returnRptCount" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="returnRptList" type="{http://localhost:8080/s-mas}Rpt" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="returnStateCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="returnStateIntro" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RptResult", propOrder = {
        "returnRptCount",
        "returnRptList",
        "returnStateCode",
        "returnStateIntro"
})
public class RptResult {

    protected String returnRptCount;
    protected List<Rpt> returnRptList;
    protected String returnStateCode;
    protected String returnStateIntro;

    /**
     * Gets the value of the returnRptCount property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getReturnRptCount() {
        return returnRptCount;
    }

    /**
     * Sets the value of the returnRptCount property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setReturnRptCount(String value) {
        this.returnRptCount = value;
    }

    /**
     * Gets the value of the returnRptList property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the returnRptList property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getReturnRptList().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Rpt }
     */
    public List<Rpt> getReturnRptList() {
        if (returnRptList == null) {
            returnRptList = new ArrayList<Rpt>();
        }
        return this.returnRptList;
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
