package com.wellsoft.pt.di.transform;

import com.wellsoft.pt.integration.request.ShareRequest;
import org.junit.Assert;
import org.junit.Test;

/**
 * Description:
 *
 * @author chenq
 * @date 2019/8/20
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/8/20    chenq		2019/8/20		Create
 * </pre>
 */
public class XmlConvert2DXShareRequestTransformTest {

    @Test
    public void transform() throws Exception {

        ShareRequest request = new XmlConvert2DXShareRequestTransform().transform(
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "\n" +
                        "<item>\n" +
                        "  <TYPEID>xxx</TYPEID>\n" +
                        "  <UNITID>U11111</UNITID>\n" +
                        "  <CONDITION>\n" +
                        "    <item>\n" +
                        "      <KEY>con_key</KEY>\n" +
                        "      <VALUE>11111</VALUE>\n" +
                        "      <OPERATOR>=</OPERATOR>\n" +
                        "    </item>\n" +
                        "  </CONDITION>\n" +
                        "  <PAGESIZE>100</PAGESIZE>\n" +
                        "  <CURRENTPAGE>1</CURRENTPAGE>\n" +
                        "  <PARAMS>\n" +
                        "    <key1>value1</key1>\n" +
                        "  </PARAMS>\n" +
                        "</item>");
        Assert.assertEquals("xxx", request.getTypeId());
    }
}