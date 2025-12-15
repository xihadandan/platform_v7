/*
 * @(#)2018年5月2日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.core.web.interceptor;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

/**
 * Description: 如何描述该类
 *
 * @author zyguo
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年5月2日.1	{Svn璐﹀彿}		2018年5月2日		Create
 * </pre>
 * @date 2018年5月2日
 */
public class RepeatedlyReadRequestWrapper extends HttpServletRequestWrapper {
    private final byte[] body;

    /**
     * 因为DmsDataServicesController ,里面有需要二开的 preHandler，但是由于request.getReader()
     * 或 request.getInputStream()，数据只能读取一次，无法重复读取造成异常，所以需要给 DmsDataServicesController
     * 接口套上一个过滤器，将 request.getInputStream 变成可以重复读取的request
     *
     * @param request
     * @throws IOException
     */
    public RepeatedlyReadRequestWrapper(HttpServletRequest request) throws IOException {
        super(request);
        body = IOUtils.toByteArray(request.getInputStream());
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(getInputStream()));
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        final ByteArrayInputStream bais = new ByteArrayInputStream(body);
        return new ServletInputStream() {

            @Override
            public int read() throws IOException {
                return bais.read();
            }

            @Override
            public boolean isFinished() {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public boolean isReady() {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public void setReadListener(ReadListener readListener) {
                // TODO Auto-generated method stub

            }
        };
    }

    /**
     * 通过BufferedReader和字符编码集转换成byte数组
     *
     * @param br
     * @param encoding
     * @return
     * @throws IOException
     */
    private byte[] readBytes(BufferedReader br, String encoding) throws IOException {
        String str = null, retStr = "";
        while ((str = br.readLine()) != null) {
            retStr += str;
        }
        if (StringUtils.isNotBlank(retStr)) {
            return retStr.getBytes(Charset.forName(encoding));
        }
        return null;
    }

}
