package com.wellsoft.pt.di.transform;

import com.wellsoft.pt.integration.request.DXRequest;
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
public class XmlConvert2DXRequestTransformTest {

    @Test
    public void transform() throws Exception {

        DXRequest request = new XmlConvert2DXRequestTransform().transform(
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "\n" +
                        "<item>\n" +
                        "  <FROM>厦门行政服务中心</FROM>\n" +
                        "  <TO>中央行政服务中心</TO>\n" +
                        "  <CC>国土OA中心</CC>\n" +
                        "  <BCC>国家保密局</BCC>\n" +
                        "  <TYPEID>1</TYPEID>\n" +
                        "  <BATCHID>8085e72e-46bb-4953-8412-aa2e99dd5a17</BATCHID>\n" +
                        "  <DATALIST>\n" +
                        "    <item>\n" +
                        "      <DATAID>8085e72e-46bb-4953-8412-aa2e99dd5a17</DATAID>\n" +
                        "      <RECVER>0</RECVER>\n" +
                        "      <PARAMS>\n" +
                        "        <key1>value1</key1>\n" +
                        "      </PARAMS>\n" +
                        "      <TEXT>\n" +
                        "        <item>\n" +
                        "          <BLHJ>1111</BLHJ>\n" +
                        "        </item>\n" +
                        "        <item>\n" +
                        "          <BLHJ>2222</BLHJ>\n" +
                        "        </item>&lt;item&gt;&lt;BLHJ&gt;1111&lt;/BLHJ&gt;&lt;/item&gt;&lt;item&gt;&lt;BLHJ&gt;2222&lt;/BLHJ&gt;&lt;/item&gt;\n" +
                        "      </TEXT>\n" +
                        "      <STREAMINGDATAS>\n" +
                        "        <item>\n" +
                        "          <DATAHANDLER>IyBDb3B5cmlnaHQgKGMpIDE5OTMtMjAwOSBNaWNyb3NvZnQgQ29ycC4NCiMNCiMgVGhpcyBpcyBh\n" +
                        "IHNhbXBsZSBIT1NUUyBmaWxlIHVzZWQgYnkgTWljcm9zb2Z0IFRDUC9JUCBmb3IgV2luZG93cy4N\n" +
                        "CiMNCiMgVGhpcyBmaWxlIGNvbnRhaW5zIHRoZSBtYXBwaW5ncyBvZiBJUCBhZGRyZXNzZXMgdG8g\n" +
                        "aG9zdCBuYW1lcy4gRWFjaA0KIyBlbnRyeSBzaG91bGQgYmUga2VwdCBvbiBhbiBpbmRpdmlkdWFs\n" +
                        "IGxpbmUuIFRoZSBJUCBhZGRyZXNzIHNob3VsZA0KIyBiZSBwbGFjZWQgaW4gdGhlIGZpcnN0IGNv\n" +
                        "bHVtbiBmb2xsb3dlZCBieSB0aGUgY29ycmVzcG9uZGluZyBob3N0IG5hbWUuDQojIFRoZSBJUCBh\n" +
                        "ZGRyZXNzIGFuZCB0aGUgaG9zdCBuYW1lIHNob3VsZCBiZSBzZXBhcmF0ZWQgYnkgYXQgbGVhc3Qg\n" +
                        "b25lDQojIHNwYWNlLg0KIw0KIyBBZGRpdGlvbmFsbHksIGNvbW1lbnRzIChzdWNoIGFzIHRoZXNl\n" +
                        "KSBtYXkgYmUgaW5zZXJ0ZWQgb24gaW5kaXZpZHVhbA0KIyBsaW5lcyBvciBmb2xsb3dpbmcgdGhl\n" +
                        "IG1hY2hpbmUgbmFtZSBkZW5vdGVkIGJ5IGEgJyMnIHN5bWJvbC4NCiMNCiMgRm9yIGV4YW1wbGU6\n" +
                        "DQojDQojICAgICAgMTAyLjU0Ljk0Ljk3ICAgICByaGluby5hY21lLmNvbSAgICAgICAgICAjIHNv\n" +
                        "dXJjZSBzZXJ2ZXINCiMgICAgICAgMzguMjUuNjMuMTAgICAgIHguYWNtZS5jb20gICAgICAgICAg\n" +
                        "ICAgICMgeCBjbGllbnQgaG9zdA0KDQojIGxvY2FsaG9zdCBuYW1lIHJlc29sdXRpb24gaXMgaGFu\n" +
                        "ZGxlZCB3aXRoaW4gRE5TIGl0c2VsZi4NCgkxMjcuMC4wLjEgICAgICAgbG9jYWxob3N0DQojCTo6\n" +
                        "MSAgICAgICAgICAgICBsb2NhbGhvc3QNCg==</DATAHANDLER>\n" +
                        "          <FILENAME>hosts</FILENAME>\n" +
                        "        </item>\n" +
                        "      </STREAMINGDATAS>\n" +
                        "    </item>\n" +
                        "  </DATALIST>\n" +
                        "</item>");

        Assert.assertEquals("厦门行政服务中心", request.getFrom());
    }
}