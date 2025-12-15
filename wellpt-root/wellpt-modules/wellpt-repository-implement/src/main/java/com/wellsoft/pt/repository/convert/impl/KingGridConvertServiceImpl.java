package com.wellsoft.pt.repository.convert.impl;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.util.IOUtils;
import com.wellsoft.context.web.controller.ApiResult;
import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.repository.convert.FileConvertService;
import com.wellsoft.pt.repository.convert.KingGridConvertService;
import com.wellsoft.pt.repository.entity.ConvertType;
import com.wellsoft.pt.repository.utils.KingGridConfig;
import com.wellsoft.pt.repository.utils.KingGridOfdUtil;
import com.wellsoft.pt.repository.vo.OfdParam;
import com.wellsoft.pt.repository.vo.PreviewParam;
import com.wellsoft.pt.repository.vo.PreviewResult;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.URLEncoder;
import java.util.Map;

/**
 * Description:
 * 金格超越文档转换
 * <pre>
 * 修改记录:
 * 修改后版本    修改人     修改日期    修改内容
 * 1.0         baozh    2021/11/8   Create
 * </pre>
 */
@Service("kingGridConvertService")
public class KingGridConvertServiceImpl extends BaseServiceImpl implements KingGridConvertService, FileConvertService {

    @Autowired
    KingGridConfig kingGridConfig;

    private OfdParam newParam(FileInputStream fileInputStream, String fileName, String businessId, String businessName) throws Exception {
        OfdParam param = new OfdParam();
        param.setAppKey(kingGridConfig.getAppKey());
        param.setAppSecret(kingGridConfig.getAppSecret());
        param.setUrl(kingGridConfig.getUrl());
        param.setFileName(URLEncoder.encode(fileName, "UTF-8"));
        param.setFileInputStream(fileInputStream);
        param.setBusinessId(businessId);
        param.setBusinessName("utf-8", businessName);
        return param;
    }

    @Override
    public void officeToOFD(File srcFile, OutputStream out, Map<String, String> metas) {
        FileInputStream fileInputStream = null;
        InputStream is = null;
        try {
            fileInputStream = new FileInputStream(srcFile);
            OfdParam param = newParam(fileInputStream, srcFile.getName(), metas.get("businessId"), metas.get("businessName"));
            is = KingGridOfdUtil.office2ofd(param);
            KingGridOfdUtil.output(is, out);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.close(is);
            IOUtils.close(fileInputStream);
        }
    }

    @Override
    public void officeToPDF(File srcFile, OutputStream out, Map<String, String> metas) {
        FileInputStream fileInputStream = null;
        InputStream is = null;
        try {
            fileInputStream = new FileInputStream(srcFile);
            OfdParam param = newParam(fileInputStream, srcFile.getName(), metas.get("businessId"), metas.get("businessName"));
            is = KingGridOfdUtil.office2pdf(param);
            KingGridOfdUtil.output(is, out);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.close(is);
            IOUtils.close(fileInputStream);
        }
    }

    @Override
    public String kingGridConvert(File srcFile, OutputStream out, Map<String, String> metas) {
        InputStream is = null;
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(srcFile);
            ConvertType convertType = ConvertType.getConvertType(metas.get("convertType"));
            if (ConvertType.PREVIEW == convertType) {
                PreviewParam previewParam = new PreviewParam(fileInputStream, srcFile.getName(), kingGridConfig.getAdminUrl());
                BeanUtils.copyProperties(metas, previewParam);
                PreviewResult result = KingGridOfdUtil.preview(previewParam);
                return JSONObject.toJSONString(ApiResult.success(result));
            } else {
                OfdParam param = newParam(fileInputStream, srcFile.getName(), metas.get("businessId"), metas.get("businessName"));
                String prefix = convertType.getConvertSuffix();
                if (ConvertType.OFFICE2PDF == convertType) {
                    is = KingGridOfdUtil.office2pdf(param);
                } else if (ConvertType.OFFICE2OFD == convertType) {
                    is = KingGridOfdUtil.office2ofd(param);
                } else if (ConvertType.PDF2OFD == convertType) {
                    is = KingGridOfdUtil.pdf2ofd(param);
                } else if (ConvertType.OFD2PDF == convertType) {
                    is = KingGridOfdUtil.ofd2pdf(param);
                }
                String targetFilePath = kingGridConfig.getTargetFilePath();
                if (StringUtils.isBlank(targetFilePath)) {
                    targetFilePath = System.getProperty("user.dir") + File.separator + "target";
                }
                File targetFile = File.createTempFile(metas.get("businessId"), "." + prefix, new File(targetFilePath));
                out = new FileOutputStream(targetFile);
                KingGridOfdUtil.output(is, out);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.close(out);
            IOUtils.close(is);
            IOUtils.close(fileInputStream);
        }
        return JSONObject.toJSONString(ApiResult.success());
    }
}
