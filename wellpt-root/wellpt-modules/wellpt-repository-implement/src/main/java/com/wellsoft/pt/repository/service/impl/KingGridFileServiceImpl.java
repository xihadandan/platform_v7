package com.wellsoft.pt.repository.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.util.IOUtils;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.repository.dao.KingGridFileDao;
import com.wellsoft.pt.repository.entity.ConvertStatus;
import com.wellsoft.pt.repository.entity.ConvertType;
import com.wellsoft.pt.repository.entity.KingGridFileEntity;
import com.wellsoft.pt.repository.service.KingGridFileService;
import com.wellsoft.pt.repository.utils.KingGridConfig;
import com.wellsoft.pt.repository.utils.KingGridOfdUtil;
import com.wellsoft.pt.repository.vo.KingGridParamVo;
import com.wellsoft.pt.repository.vo.OfdParam;
import com.wellsoft.pt.repository.vo.PreviewParam;
import com.wellsoft.pt.repository.vo.PreviewResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.net.URLEncoder;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Description:
 *
 * <pre>
 * 修改记录:
 * 修改后版本    修改人     修改日期    修改内容
 * 1.0         baozh    2021/11/9   Create
 * </pre>
 */
@Service
public class KingGridFileServiceImpl extends AbstractJpaServiceImpl<KingGridFileEntity, KingGridFileDao, String> implements KingGridFileService {

    @Autowired
    ScheduledExecutorService scheduledExecutorService;

    @Autowired
    ThreadPoolTaskExecutor apiExecutor;

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


    @Transactional
    @Override
    public KingGridFileEntity saveFile(String fileName, File srcFile, String convertType, File targetDirectory, KingGridParamVo paramVo) {

        KingGridFileEntity entity = new KingGridFileEntity();
        entity.setFileName(fileName.substring(0, fileName.lastIndexOf(".")));
        entity.setConvertType(ConvertType.getConvertType(convertType));
        entity.setSourceFilePath(srcFile.getAbsolutePath());
        entity.setFileSize(srcFile.length());
        entity.setStatus(ConvertStatus.NOT);
        save(entity);
        flushSession();
        String uuid = entity.getUuid();
        //异步调用转换
        scheduledExecutorService.submit(() -> {
            InputStream is = null;
            OutputStream out = null;
            KingGridFileService service = ApplicationContextHolder.getBean(KingGridFileServiceImpl.class);
            KingGridFileEntity tempEntity = service.getOne(uuid);
            FileInputStream fileInputStream = null;
            try {
                fileInputStream = new FileInputStream(srcFile);
                tempEntity.setStatus(ConvertStatus.ING);
                service.update(tempEntity);
                if (ConvertType.PREVIEW == entity.getConvertType()) {
                    PreviewParam previewParam = new PreviewParam(fileInputStream, srcFile.getName(), kingGridConfig.getAdminUrl());
                    BeanUtils.copyProperties(paramVo, previewParam);
                    PreviewResult result = KingGridOfdUtil.preview(previewParam);
                    tempEntity.setStatus(ConvertStatus.SUCCESS);
                    tempEntity.setTargetFilePath(JSONObject.toJSONString(result));
                } else {
                    OfdParam param = newParam(fileInputStream, srcFile.getName(), uuid, fileName);
                    String prefix = entity.getConvertType().getConvertSuffix();
                    if (ConvertType.OFFICE2PDF == entity.getConvertType()) {
                        is = KingGridOfdUtil.office2pdf(param);
                    } else if (ConvertType.OFFICE2OFD == entity.getConvertType()) {
                        is = KingGridOfdUtil.office2ofd(param);
                    } else if (ConvertType.PDF2OFD == entity.getConvertType()) {
                        is = KingGridOfdUtil.pdf2ofd(param);
                    } else if (ConvertType.OFD2PDF == entity.getConvertType()) {
                        is = KingGridOfdUtil.ofd2pdf(param);
                    }
                    File targetFile = File.createTempFile(uuid, "." + prefix, targetDirectory);
                    out = new FileOutputStream(targetFile);
                    KingGridOfdUtil.output(is, out);
                    tempEntity.setStatus(ConvertStatus.SUCCESS);
                    tempEntity.setTargetFilePath(targetFile.getAbsolutePath());
                }
                service.update(tempEntity);
            } catch (Exception e) {
                e.printStackTrace();
                tempEntity.setStatus(ConvertStatus.FAIL);
                tempEntity.setErrorMsg(e.getMessage());
                service.update(tempEntity);
            } finally {
                IOUtils.close(out);
                IOUtils.close(is);
                IOUtils.close(fileInputStream);
            }
        });

        return entity;
    }

    @Override
    public InputStream convertFile(File srcFile, InputStream out, String convertType, String businessId, String businessName, KingGridParamVo paramVo) {
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(srcFile);
            OfdParam param = newParam(fileInputStream, srcFile.getName(), businessId, businessName);
            if (ConvertType.OFFICE2PDF == ConvertType.getConvertType(convertType)) {
                out = KingGridOfdUtil.office2pdf(param);
            } else if (ConvertType.OFFICE2OFD == ConvertType.getConvertType(convertType)) {
                out = KingGridOfdUtil.office2ofd(param);
            } else if (ConvertType.PDF2OFD == ConvertType.getConvertType(convertType)) {
                out = KingGridOfdUtil.pdf2ofd(param);
            } else if (ConvertType.OFD2PDF == ConvertType.getConvertType(convertType)) {
                out = KingGridOfdUtil.ofd2pdf(param);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.close(fileInputStream);
        }
        return out;
    }

    @Override
    public String previewFile(File srcFile, KingGridParamVo paramVo) {

        PreviewResult result = null;
        InputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(srcFile);
            PreviewParam previewParam = new PreviewParam(fileInputStream, srcFile.getName(), kingGridConfig.getAdminUrl());
            BeanUtils.copyProperties(paramVo, previewParam);
            result = KingGridOfdUtil.preview(previewParam);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.close(fileInputStream);
        }
        return result.getUrl();
    }
}
