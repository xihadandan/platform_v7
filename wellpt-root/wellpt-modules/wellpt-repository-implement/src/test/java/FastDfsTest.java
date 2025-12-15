import com.wellsoft.pt.repository.support.FastDFSUtils;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

/*
 * @(#)2020年2月11日 V1.0
 *
 * Copyright 2020 WELL-SOFT, Inc. All rights reserved.
 */

/**
 * Description: 如何描述该类
 *
 * @author wangrf
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2020年2月11日.1	wangrf		2020年2月11日		Create
 * </pre>
 * @date 2020年2月11日
 */
public class FastDfsTest {

    @Test
    public void testFileUpload() throws Exception {
        File file = new File("F:\\test\\123.txt");
        InputStream is = new FileInputStream(file);
        String id = FastDFSUtils.uploadFile(is, null, null);
        System.out.println(id);
    }

}
