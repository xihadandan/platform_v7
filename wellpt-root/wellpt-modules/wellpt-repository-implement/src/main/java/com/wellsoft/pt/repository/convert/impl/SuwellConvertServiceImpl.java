/*
 * @(#)2020年1月19日 V1.0
 *
 * Copyright 2020 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.repository.convert.impl;

import com.suwell.ofd.custom.agent.ConvertException;
import com.suwell.ofd.custom.agent.HTTPAgent;
import com.suwell.ofd.custom.wrapper.Const.PackType;
import com.suwell.ofd.custom.wrapper.Const.Target;
import com.suwell.ofd.custom.wrapper.PackException;
import com.suwell.ofd.custom.wrapper.Packet;
import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import com.wellsoft.pt.repository.convert.FileConvertService;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author zhongzh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2020年1月19日.1	zhongzh		2020年1月19日		Create
 * </pre>
 * @date 2020年1月19日
 */
@Component("suwellConvertService")
public class SuwellConvertServiceImpl extends BaseServiceImpl implements FileConvertService {

    @Value("${ofd.convert.suwell.http-agent}")
    private String httpAgentUrl;//  = Config.getValue("ofd.convert.suwell.http-agent");

    @Override
    public void officeToOFD(File srcFile, OutputStream out, Map<String, String> metas) {
        HTTPAgent ha = null;
        try {
            ha = new HTTPAgent(httpAgentUrl);
            ha.officeToOFD(srcFile, out, metas);
        } catch (Exception ex) {
            logger.warn("officeToOFD agentUrl[" + httpAgentUrl + "]srcFile[" + srcFile.getAbsolutePath() + "]:" + ex.getMessage(),
                    ex);
            throw new RuntimeException(ex.getMessage(), ex);
        } finally {
            IOUtils.closeQuietly(ha);
        }
    }

    @Override
    public void officeToPDF(File srcFile, OutputStream out, Map<String, String> metas) {
        MyHTTPAgent ha = null;
        try {
            ha = new MyHTTPAgent(httpAgentUrl);
            ha.officeToPDF(srcFile, out, metas);
        } catch (Exception ex) {
            logger.warn("officeToPDF agentUrl[" + httpAgentUrl + "]srcFile[" + srcFile.getAbsolutePath() + "]:" + ex.getMessage(),
                    ex);
            throw new RuntimeException(ex.getMessage(), ex);
        } finally {
            IOUtils.closeQuietly(ha);
        }
    }

    public static class MyHTTPAgent extends HTTPAgent {

        /**
         * @param baseURL
         */
        public MyHTTPAgent(String baseURL) {
            super(baseURL);
        }

        public void officeToPDF(File srcFile, OutputStream out, Map<String, String> metas) throws IOException,
                ConvertException, PackException {
            Packet packet = new Packet(PackType.COMMON, Target.PDF);
            try {
                append(packet, srcFile, 0.0F, 0);
                metadata(packet, metas);
                convert(packet, out);
            } finally {
                IOUtils.closeQuietly(packet);
            }
        }
    }
}
