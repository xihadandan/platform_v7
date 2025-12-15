package com.wellsoft.pt.message.sms.skmas;

import javax.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each
 * Java content interface and Java element interface
 * generated in the com.wellsoft.pt.message.support.smas package.
 * <p>An ObjectFactory allows you to programatically
 * construct new instances of the Java representation
 * for XML content. The Java representation of XML
 * content can consist of schema derived interfaces
 * and classes representing the binding of schema
 * type definitions, element declarations and model
 * groups.  Factory methods for each of these are
 * provided in this class.
 */
@XmlRegistry
public class ObjectFactory {


    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.wellsoft.pt.message.support.smas
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link RptResult }
     */
    public RptResult createRptResult() {
        return new RptResult();
    }

    /**
     * Create an instance of {@link MtResult }
     */
    public MtResult createMtResult() {
        return new MtResult();
    }

    /**
     * Create an instance of {@link Sms }
     */
    public Sms createSms() {
        return new Sms();
    }

    /**
     * Create an instance of {@link MoResult }
     */
    public MoResult createMoResult() {
        return new MoResult();
    }

    /**
     * Create an instance of {@link Rpt }
     */
    public Rpt createRpt() {
        return new Rpt();
    }

}
