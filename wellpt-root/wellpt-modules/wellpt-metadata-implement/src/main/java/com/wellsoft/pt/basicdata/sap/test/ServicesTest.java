package com.wellsoft.pt.basicdata.sap.test;

import com.wellsoft.pt.basicdata.sap.impl.SAPRfcImpl;
import com.wellsoft.pt.basicdata.sap.util.SAPRfcJson;

public class ServicesTest {

    public static void main(String[] args) {

        String paraJSON = new InputJsonCreate().createInputJson();
        SAPRfcJson util = new SAPRfcImpl().RFC(null, "ZFM_TEMPLATE", paraJSON, 1, -1, null);

        util.getStructure("PO_RETURN");

        System.out.println(util.getStructure("PO_RETURN"));
    }

}
