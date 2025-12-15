/*
 * @(#)2013-12-26 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.integration.support.zipfile;

import com.wellsoft.context.util.date.DateUtils;
import com.wellsoft.pt.integration.security.ExchangeConfig;
import com.wellsoft.pt.integration.service.ExchangeDataFlowService;
import com.wellsoft.pt.integration.service.ExchangeDataTypeService;
import com.wellsoft.pt.integration.support.DataItem;
import com.wellsoft.pt.repository.service.MongoFileService;
import com.wellsoft.pt.unit.facade.service.UnitApiFacade;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-12-26.1	zhulh		2013-12-26		Create
 * </pre>
 * @date 2013-12-26
 */
@Component
public class ExcelZipFileHanlder implements ZipFileHandler {

    public static Map<String, Map<String, String>> typeIdFieldMap = new HashMap<String, Map<String, String>>();
    private static Map<String, String> sheetNameMap = new LinkedHashMap<String, String>();
    private static Map<String, String> ztdjSheetNameMap = new LinkedHashMap<String, String>();

    static {
        // 商事主体登记信息
        Map<String, String> ztMap = new LinkedHashMap<String, String>();
        ztMap.put(ExchangeConfig.DATA_ITEM_DATA_ID, ExchangeConfig.DATA_ITEM_DATA_ID);
        ztMap.put(ExchangeConfig.DATA_ITEM_REC_VER, ExchangeConfig.DATA_ITEM_REC_VER);
        ztMap.put(ExchangeConfig.SSXX_ZTDJ_ZCH, ExchangeConfig.SSXX_ZTDJ_ZCH);
        ztMap.put(ExchangeConfig.SSXX_ZTDJ_ZTMC, ExchangeConfig.SSXX_ZTDJ_ZTMC);
        ztMap.put(ExchangeConfig.SSXX_ZTDJ_LSZTMC, ExchangeConfig.SSXX_ZTDJ_LSZTMC);
        ztMap.put(ExchangeConfig.SSXX_ZTDJ_FDDBR, ExchangeConfig.SSXX_ZTDJ_FDDBR);
        ztMap.put(ExchangeConfig.SSXX_ZTDJ_FDDBRZWDM, ExchangeConfig.SSXX_ZTDJ_FDDBRZWDM);
        ztMap.put(ExchangeConfig.SSXX_ZTDJ_FDDBRZWMC, ExchangeConfig.SSXX_ZTDJ_FDDBRZWMC);
        ztMap.put(ExchangeConfig.SSXX_ZTDJ_FDDBRZJHM, ExchangeConfig.SSXX_ZTDJ_FDDBRZJHM);
        ztMap.put(ExchangeConfig.SSXX_ZTDJ_JYHW, ExchangeConfig.SSXX_ZTDJ_JYHW);
        ztMap.put(ExchangeConfig.SSXX_ZTDJ_XKJYHW, ExchangeConfig.SSXX_ZTDJ_XKJYHW);
        ztMap.put(ExchangeConfig.SSXX_ZTDJ_ZS, ExchangeConfig.SSXX_ZTDJ_ZS);
        ztMap.put(ExchangeConfig.SSXX_ZTDJ_LXDH, ExchangeConfig.SSXX_ZTDJ_LXDH);
        ztMap.put(ExchangeConfig.SSXX_ZTDJ_LLRXX, ExchangeConfig.SSXX_ZTDJ_LLRXX);
        ztMap.put(ExchangeConfig.SSXX_ZTDJ_RJCZE, ExchangeConfig.SSXX_ZTDJ_RJCZE);
        ztMap.put(ExchangeConfig.SSXX_ZTDJ_RJCZEDW, ExchangeConfig.SSXX_ZTDJ_RJCZEDW);
        ztMap.put(ExchangeConfig.SSXX_ZTDJ_SJCZE, ExchangeConfig.SSXX_ZTDJ_SJCZE);
        ztMap.put(ExchangeConfig.SSXX_ZTDJ_SJCZEDW, ExchangeConfig.SSXX_ZTDJ_SJCZEDW);
        ztMap.put(ExchangeConfig.SSXX_ZTDJ_ZTLX, ExchangeConfig.SSXX_ZTDJ_ZTLX);
        ztMap.put(ExchangeConfig.SSXX_ZTDJ_ZTLXDM, ExchangeConfig.SSXX_ZTDJ_ZTLXDM);
        ztMap.put(ExchangeConfig.SSXX_ZTDJ_CLRQ, ExchangeConfig.SSXX_ZTDJ_CLRQ);
        ztMap.put(ExchangeConfig.SSXX_ZTDJ_YYQX, ExchangeConfig.SSXX_ZTDJ_YYQX);
        ztMap.put(ExchangeConfig.SSXX_ZTDJ_HZRQ, ExchangeConfig.SSXX_ZTDJ_HZRQ);
        ztMap.put(ExchangeConfig.SSXX_ZTDJ_DJJG, ExchangeConfig.SSXX_ZTDJ_DJJG);
        ztMap.put(ExchangeConfig.SSXX_ZTDJ_DJJGDM, ExchangeConfig.SSXX_ZTDJ_DJJGDM);
        ztMap.put(ExchangeConfig.SSXX_ZTDJ_NBQK, ExchangeConfig.SSXX_ZTDJ_NBQK);
        ztMap.put(ExchangeConfig.SSXX_ZTDJ_ZTZT, ExchangeConfig.SSXX_ZTDJ_ZTZT);
        ztMap.put(ExchangeConfig.SSXX_ZTDJ_ZTZTDM, ExchangeConfig.SSXX_ZTDJ_ZTZTDM);
        ztMap.put(ExchangeConfig.SSXX_ZTDJ_ZC, ExchangeConfig.SSXX_ZTDJ_ZC);
        ztMap.put(ExchangeConfig.SSXX_ZTDJ_QTWJ, ExchangeConfig.SSXX_ZTDJ_QTWJ);
        typeIdFieldMap.put(ExchangeConfig.TYPE_ID_SSXX_ZTDJ, ztMap);
        sheetNameMap.put(ExchangeConfig.TYPE_ID_SSXX_ZTDJ, "主体登记信息");

        // 商事登记_主体的经营场所
        Map<String, String> jycsMap = new LinkedHashMap<String, String>();
        jycsMap.put(ExchangeConfig.SSXX_JYCS_JYCS, ExchangeConfig.SSXX_JYCS_JYCS);
        typeIdFieldMap.put(ExchangeConfig.USER_FORM_SSXX_JYCS, jycsMap);
        ztdjSheetNameMap.put(ExchangeConfig.USER_FORM_SSXX_JYCS, "经营场所");

        // 商事登记_主体的股东信息
        Map<String, String> gdxxMap = new LinkedHashMap<String, String>();
        gdxxMap.put(ExchangeConfig.SSXX_GDXX_GDMC, ExchangeConfig.SSXX_GDXX_GDMC);
        gdxxMap.put(ExchangeConfig.SSXX_GDXX_GDLX, ExchangeConfig.SSXX_GDXX_GDLX);
        gdxxMap.put(ExchangeConfig.SSXX_GDXX_GDLXDM, ExchangeConfig.SSXX_GDXX_GDLXDM);
        gdxxMap.put(ExchangeConfig.SSXX_GDXX_GB, ExchangeConfig.SSXX_GDXX_GB);
        gdxxMap.put(ExchangeConfig.SSXX_GDXX_GBDM, ExchangeConfig.SSXX_GDXX_GBDM);
        gdxxMap.put(ExchangeConfig.SSXX_GDXX_RJCZE, ExchangeConfig.SSXX_GDXX_RJCZE);
        gdxxMap.put(ExchangeConfig.SSXX_GDXX_RJCZEDW, ExchangeConfig.SSXX_GDXX_RJCZEDW);
        gdxxMap.put(ExchangeConfig.SSXX_GDXX_SJCZE, ExchangeConfig.SSXX_GDXX_SJCZE);
        gdxxMap.put(ExchangeConfig.SSXX_GDXX_SJCZEDW, ExchangeConfig.SSXX_GDXX_SJCZEDW);
        gdxxMap.put(ExchangeConfig.SSXX_GDXX_CZBL, ExchangeConfig.SSXX_GDXX_CZBL);
        typeIdFieldMap.put(ExchangeConfig.USER_FORM_SSXX_GDXX, gdxxMap);
        ztdjSheetNameMap.put(ExchangeConfig.USER_FORM_SSXX_GDXX, "股东信息");

        // 商事登记_主体组织机构信息
        Map<String, String> zzjgMap = new LinkedHashMap<String, String>();
        zzjgMap.put(ExchangeConfig.SSXX_ZZJG_XM, ExchangeConfig.SSXX_ZZJG_XM);
        zzjgMap.put(ExchangeConfig.SSXX_ZZJG_ZW, ExchangeConfig.SSXX_ZZJG_ZW);
        zzjgMap.put(ExchangeConfig.SSXX_ZZJG_ZWDM, ExchangeConfig.SSXX_ZZJG_ZWDM);
        typeIdFieldMap.put(ExchangeConfig.USER_FORM_SSXX_ZZJG, zzjgMap);
        ztdjSheetNameMap.put(ExchangeConfig.USER_FORM_SSXX_ZZJG, "组织机构信息");

        // 商事登记_主体分支机构信息
        Map<String, String> fzjgMap = new LinkedHashMap<String, String>();
        fzjgMap.put(ExchangeConfig.SSXX_FZJG_MC, ExchangeConfig.SSXX_FZJG_MC);
        fzjgMap.put(ExchangeConfig.SSXX_FZJG_JYCS, ExchangeConfig.SSXX_FZJG_JYCS);
        fzjgMap.put(ExchangeConfig.SSXX_FZJG_FZR, ExchangeConfig.SSXX_FZJG_FZR);
        fzjgMap.put(ExchangeConfig.SSXX_FZJG_BZ, ExchangeConfig.SSXX_FZJG_BZ);
        typeIdFieldMap.put(ExchangeConfig.USER_FORM_SSXX_FZJG, fzjgMap);
        ztdjSheetNameMap.put(ExchangeConfig.USER_FORM_SSXX_FZJG, "分支机构信息");

        // 商事登记_相关许可单位
        Map<String, String> xgxkMap = new LinkedHashMap<String, String>();
        xgxkMap.put(ExchangeConfig.SSXX_XGXK_XKJYXMMC, ExchangeConfig.SSXX_XGXK_XKJYXMMC);
        xgxkMap.put(ExchangeConfig.SSXX_XGXK_XKJYXMDM, ExchangeConfig.SSXX_XGXK_XKJYXMDM);
        xgxkMap.put(ExchangeConfig.SSXX_XGXK_DWMC, ExchangeConfig.SSXX_XGXK_DWMC);
        xgxkMap.put(ExchangeConfig.SSXX_XGXK_DWDM, ExchangeConfig.SSXX_XGXK_DWDM);
        typeIdFieldMap.put(ExchangeConfig.USER_FORM_SSXX_XGXK, xgxkMap);
        ztdjSheetNameMap.put(ExchangeConfig.USER_FORM_SSXX_XGXK, "相关许可单位");

        // 商事登记_主体清算信息
        Map<String, String> qsxxMap = new LinkedHashMap<String, String>();
        qsxxMap.put(ExchangeConfig.SSXX_QSXX_QSZFZR, ExchangeConfig.SSXX_QSXX_QSZFZR);
        qsxxMap.put(ExchangeConfig.SSXX_QSXX_QSZRY, ExchangeConfig.SSXX_QSXX_QSZRY);
        qsxxMap.put(ExchangeConfig.SSXX_QSXX_QSZDZ, ExchangeConfig.SSXX_QSXX_QSZDZ);
        qsxxMap.put(ExchangeConfig.SSXX_QSXX_QSZDH, ExchangeConfig.SSXX_QSXX_QSZDH);
        qsxxMap.put(ExchangeConfig.SSXX_QSXX_QSZBARQ, ExchangeConfig.SSXX_QSXX_QSZBARQ);
        typeIdFieldMap.put(ExchangeConfig.USER_FORM_SSXX_QSXX, qsxxMap);
        ztdjSheetNameMap.put(ExchangeConfig.USER_FORM_SSXX_QSXX, "清算信息");

        // 商事行政许可信息
        Map<String, String> xkMap = new LinkedHashMap<String, String>();
        xkMap.put(ExchangeConfig.DATA_ITEM_DATA_ID, ExchangeConfig.DATA_ITEM_DATA_ID);
        xkMap.put(ExchangeConfig.DATA_ITEM_REC_VER, ExchangeConfig.DATA_ITEM_REC_VER);
        xkMap.put(ExchangeConfig.SSXX_XZXK_ZCH, ExchangeConfig.SSXX_XZXK_ZCH);
        xkMap.put(ExchangeConfig.SSXX_XZXK_ZTMC, ExchangeConfig.SSXX_XZXK_ZTMC);
        xkMap.put(ExchangeConfig.SSXX_XZXK_FDDBR, ExchangeConfig.SSXX_XZXK_FDDBR);
        xkMap.put(ExchangeConfig.SSXX_XZXK_ZTLX, ExchangeConfig.SSXX_XZXK_ZTLX);
        xkMap.put(ExchangeConfig.SSXX_XZXK_JYCS, ExchangeConfig.SSXX_XZXK_JYCS);
        xkMap.put(ExchangeConfig.SSXX_XZXK_BZDWDM, ExchangeConfig.SSXX_XZXK_BZDWDM);
        xkMap.put(ExchangeConfig.SSXX_XZXK_BZDWMC, ExchangeConfig.SSXX_XZXK_BZDWMC);
        xkMap.put(ExchangeConfig.SSXX_XZXK_ZJH, ExchangeConfig.SSXX_XZXK_ZJH);
        xkMap.put(ExchangeConfig.SSXX_XZXK_ZZMC, ExchangeConfig.SSXX_XZXK_ZZMC);
        xkMap.put(ExchangeConfig.SSXX_XZXK_XKFW, ExchangeConfig.SSXX_XZXK_XKFW);
        xkMap.put(ExchangeConfig.SSXX_XZXK_GXRQ, ExchangeConfig.SSXX_XZXK_GXRQ);
        xkMap.put(ExchangeConfig.SSXX_XZXK_BZRQ, ExchangeConfig.SSXX_XZXK_BZRQ);
        xkMap.put(ExchangeConfig.SSXX_XZXK_XQGXRQ, ExchangeConfig.SSXX_XZXK_XQGXRQ);
        xkMap.put(ExchangeConfig.SSXX_XZXK_JGMC, ExchangeConfig.SSXX_XZXK_JGMC);
        xkMap.put(ExchangeConfig.SSXX_XZXK_DJZT, ExchangeConfig.SSXX_XZXK_DJZT);
        xkMap.put(ExchangeConfig.SSXX_XZXK_XGWJ, ExchangeConfig.SSXX_XZXK_XGWJ);
        typeIdFieldMap.put(ExchangeConfig.TYPE_ID_SSXX_XZXK, xkMap);
        sheetNameMap.put(ExchangeConfig.TYPE_ID_SSXX_XZXK, "行政许可信息");

        // 商事行政处罚信息
        Map<String, String> cfMap = new LinkedHashMap<String, String>();
        cfMap.put(ExchangeConfig.DATA_ITEM_DATA_ID, ExchangeConfig.DATA_ITEM_DATA_ID);
        cfMap.put(ExchangeConfig.DATA_ITEM_REC_VER, ExchangeConfig.DATA_ITEM_REC_VER);
        cfMap.put(ExchangeConfig.SSXX_XZCF_ZCH, ExchangeConfig.SSXX_XZCF_ZCH);
        cfMap.put(ExchangeConfig.SSXX_XZCF_ZTMC, ExchangeConfig.SSXX_XZCF_ZTMC);
        cfMap.put(ExchangeConfig.SSXX_XZCF_FDDBR, ExchangeConfig.SSXX_XZCF_FDDBR);
        cfMap.put(ExchangeConfig.SSXX_XZCF_ZTLX, ExchangeConfig.SSXX_XZCF_ZTLX);
        cfMap.put(ExchangeConfig.SSXX_XZCF_JYCS, ExchangeConfig.SSXX_XZCF_JYCS);
        cfMap.put(ExchangeConfig.SSXX_XZCF_DSR, ExchangeConfig.SSXX_XZCF_DSR);
        cfMap.put(ExchangeConfig.SSXX_XZCF_WHXWDX, ExchangeConfig.SSXX_XZCF_WHXWDX);
        cfMap.put(ExchangeConfig.SSXX_XZCF_CFYJ, ExchangeConfig.SSXX_XZCF_CFYJ);
        cfMap.put(ExchangeConfig.SSXX_XZCF_CFZL, ExchangeConfig.SSXX_XZCF_CFZL);
        cfMap.put(ExchangeConfig.SSXX_XZCF_CFDJJGDM, ExchangeConfig.SSXX_XZCF_CFDJJGDM);
        cfMap.put(ExchangeConfig.SSXX_XZCF_CFDJJGMC, ExchangeConfig.SSXX_XZCF_CFDJJGMC);
        cfMap.put(ExchangeConfig.SSXX_XZCF_CFRQ, ExchangeConfig.SSXX_XZCF_CFRQ);
        cfMap.put(ExchangeConfig.SSXX_XZCF_XGWJ, ExchangeConfig.SSXX_XZCF_XGWJ);
        typeIdFieldMap.put(ExchangeConfig.TYPE_ID_SSXX_XZCF, cfMap);
        sheetNameMap.put(ExchangeConfig.TYPE_ID_SSXX_XZCF, "行政处罚信息");

        // 商事主体荣誉信息
        Map<String, String> ryMap = new LinkedHashMap<String, String>();
        ryMap.put(ExchangeConfig.DATA_ITEM_DATA_ID, ExchangeConfig.DATA_ITEM_DATA_ID);
        ryMap.put(ExchangeConfig.DATA_ITEM_REC_VER, ExchangeConfig.DATA_ITEM_REC_VER);
        ryMap.put(ExchangeConfig.SSXX_QDRY_ZCH, ExchangeConfig.SSXX_QDRY_ZCH);
        ryMap.put(ExchangeConfig.SSXX_QDRY_ZTMC, ExchangeConfig.SSXX_QDRY_ZTMC);
        ryMap.put(ExchangeConfig.SSXX_QDRY_FDDBR, ExchangeConfig.SSXX_QDRY_FDDBR);
        ryMap.put(ExchangeConfig.SSXX_QDRY_ZTLX, ExchangeConfig.SSXX_QDRY_ZTLX);
        ryMap.put(ExchangeConfig.SSXX_QDRY_JYCS, ExchangeConfig.SSXX_QDRY_JYCS);
        ryMap.put(ExchangeConfig.SSXX_QDRY_MC, ExchangeConfig.SSXX_QDRY_MC);
        ryMap.put(ExchangeConfig.SSXX_QDRY_SYDWDM, ExchangeConfig.SSXX_QDRY_SYDWDM);
        ryMap.put(ExchangeConfig.SSXX_QDRY_SYDWMC, ExchangeConfig.SSXX_QDRY_SYDWMC);
        ryMap.put(ExchangeConfig.SSXX_QDRY_SYRQ, ExchangeConfig.SSXX_QDRY_SYRQ);
        ryMap.put(ExchangeConfig.SSXX_QDRY_YXRQ, ExchangeConfig.SSXX_QDRY_YXRQ);
        ryMap.put(ExchangeConfig.SSXX_QDRY_XGWJ, ExchangeConfig.SSXX_QDRY_XGWJ);
        typeIdFieldMap.put(ExchangeConfig.TYPE_ID_SSXX_QDRY, ryMap);
        sheetNameMap.put(ExchangeConfig.TYPE_ID_SSXX_QDRY, "主体荣誉信息");

        // 商事主体资质信息
        Map<String, String> zzMap = new LinkedHashMap<String, String>();
        zzMap.put(ExchangeConfig.DATA_ITEM_DATA_ID, ExchangeConfig.DATA_ITEM_DATA_ID);
        zzMap.put(ExchangeConfig.DATA_ITEM_REC_VER, ExchangeConfig.DATA_ITEM_REC_VER);
        zzMap.put(ExchangeConfig.SSXX_QDZZ_ZCH, ExchangeConfig.SSXX_QDZZ_ZCH);
        zzMap.put(ExchangeConfig.SSXX_QDZZ_ZTMC, ExchangeConfig.SSXX_QDZZ_ZTMC);
        zzMap.put(ExchangeConfig.SSXX_QDZZ_FDDBR, ExchangeConfig.SSXX_QDZZ_FDDBR);
        zzMap.put(ExchangeConfig.SSXX_QDZZ_ZTLX, ExchangeConfig.SSXX_QDZZ_ZTLX);
        zzMap.put(ExchangeConfig.SSXX_QDZZ_JYCS, ExchangeConfig.SSXX_QDZZ_JYCS);
        zzMap.put(ExchangeConfig.SSXX_QDZZ_MC, ExchangeConfig.SSXX_QDZZ_MC);
        zzMap.put(ExchangeConfig.SSXX_QDZZ_SYDWDM, ExchangeConfig.SSXX_QDZZ_SYDWDM);
        zzMap.put(ExchangeConfig.SSXX_QDZZ_SYDWMC, ExchangeConfig.SSXX_QDZZ_SYDWMC);
        zzMap.put(ExchangeConfig.SSXX_QDZZ_QDRQ, ExchangeConfig.SSXX_QDZZ_QDRQ);
        zzMap.put(ExchangeConfig.SSXX_QDZZ_YXRQ, ExchangeConfig.SSXX_QDZZ_YXRQ);
        zzMap.put(ExchangeConfig.SSXX_QDZZ_XGWJ, ExchangeConfig.SSXX_QDZZ_XGWJ);
        typeIdFieldMap.put(ExchangeConfig.TYPE_ID_SSXX_QDZZ, zzMap);
        sheetNameMap.put(ExchangeConfig.TYPE_ID_SSXX_QDZZ, "主体资质信息");

        // 商事主体年报备案信息
        Map<String, String> baMap = new LinkedHashMap<String, String>();
        baMap.put(ExchangeConfig.DATA_ITEM_DATA_ID, ExchangeConfig.DATA_ITEM_DATA_ID);
        baMap.put(ExchangeConfig.DATA_ITEM_REC_VER, ExchangeConfig.DATA_ITEM_REC_VER);
        baMap.put(ExchangeConfig.SSXX_NBBA_ZCH, ExchangeConfig.SSXX_NBBA_ZCH);
        baMap.put(ExchangeConfig.SSXX_NBBA_ZTMC, ExchangeConfig.SSXX_NBBA_ZTMC);
        baMap.put(ExchangeConfig.SSXX_NBBA_FDDBR, ExchangeConfig.SSXX_NBBA_FDDBR);
        baMap.put(ExchangeConfig.SSXX_NBBA_ZTLX, ExchangeConfig.SSXX_NBBA_ZTLX);
        baMap.put(ExchangeConfig.SSXX_NBBA_JYCS, ExchangeConfig.SSXX_NBBA_JYCS);
        baMap.put(ExchangeConfig.SSXX_NBBA_ND, ExchangeConfig.SSXX_NBBA_ND);
        baMap.put(ExchangeConfig.SSXX_NBBA_MC, ExchangeConfig.SSXX_NBBA_MC);
        baMap.put(ExchangeConfig.SSXX_NBBA_RJRQ, ExchangeConfig.SSXX_NBBA_RJRQ);
        baMap.put(ExchangeConfig.SSXX_NBBA_BGSM, ExchangeConfig.SSXX_NBBA_BGSM);
        baMap.put(ExchangeConfig.SSXX_NBBA_XGWJ, ExchangeConfig.SSXX_NBBA_XGWJ);
        typeIdFieldMap.put(ExchangeConfig.TYPE_ID_SSXX_NBBA, baMap);
        sheetNameMap.put(ExchangeConfig.TYPE_ID_SSXX_NBBA, "主体年报备案信息");

    }

    @Autowired
    private UnitApiFacade unitApiFacade;
    @Autowired
    private ExchangeDataFlowService exchangeDataFlowService;
    @Autowired
    private ExchangeDataTypeService exchangeDataTypeService;
    @Autowired
    private MongoFileService mongoFileService;
    // 格式化下载的XLS文件名
    private SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmm");

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.integration.support.zipfile.ZipFileHandler#handler(java.util.zip.ZipFile)
     */
    @Override
    public Map<String, String> handler(ZipFile zipFile) throws Exception {
        Map<String, String> result = null;
        try {
            List<ZipEntry> excelEntries = getExcelEntries(zipFile);
            // 确保压缩包中只有一个Excel文件
            if (excelEntries.isEmpty()) {
                throw new RuntimeException("压缩包中找不到Excel文件!");
            } else if (excelEntries.size() > 1) {
                throw new RuntimeException("一个压缩包只能包含一个Excel文件!");
            }
            result = sendExcelRequest(zipFile, excelEntries.get(0));
        } catch (Exception e) {
            try {
                zipFile.close();
            } catch (Exception e2) {
                e2.printStackTrace();
            }

            throw e;
        }

        return result;
    }

    /**
     * (non-Javadoc)
     *
     * @throws Exception
     * @see com.wellsoft.pt.integration.support.zipfile.ZipFileHandler#generateZipFile(java.util.List)
     */
    @Override
    public File generateZipFile(List<ZipFileData> datas) throws Exception {
        Date date = Calendar.getInstance().getTime();
        String suffix = format.format(date);
        Map<String, List<ZipFileData>> typeBatchDataMap = new HashMap<String, List<ZipFileData>>();
        for (ZipFileData zipFileData : datas) {
            String key = zipFileData.getTypeId() + "_" + suffix;
            if (!typeBatchDataMap.containsKey(key)) {
                typeBatchDataMap.put(key, new ArrayList<ZipFileData>());
            }
            typeBatchDataMap.get(key).add(zipFileData);
        }
        return generateZipFileInternal(typeBatchDataMap);
    }

    /**
     * 如何描述该方法
     *
     * @param typeBatchDataMap
     * @return
     * @throws Exception
     */
    private File generateZipFileInternal(Map<String, List<ZipFileData>> typeBatchDataMap) throws Exception {
        return null;
    }

    /**
     * @param book
     * @param sheet
     * @param zipFileData
     */
    private void createExcelHeader(HSSFWorkbook book, HSSFSheet sheet, Map<String, String> fieldMap,
                                   List<ZipFileData> dataList) {
        HSSFCellStyle style = book.createCellStyle();
        style.setFillForegroundColor(HSSFColor.HSSFColorPredefined.PALE_BLUE.getIndex());
        style.setBorderLeft(BorderStyle.MEDIUM);
        style.setBorderRight(BorderStyle.MEDIUM);
        style.setBorderTop(BorderStyle.MEDIUM);
        style.setBorderBottom(BorderStyle.MEDIUM);
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        HSSFRow currentRow = sheet.createRow(0);
        int cellIndex = 0;
        for (cellIndex = 0; cellIndex < fieldMap.size(); cellIndex++) {
            HSSFCell currentCell = currentRow.createCell(cellIndex);
            currentCell.setCellStyle(style);
        }

        int index = 0;
        ZipFileData firtZipFileData = dataList.get(0);
        Map<String, String> headerMap = dataList.get(0).getHeaderMap();
        for (String key : fieldMap.keySet()) {
            currentRow.getCell(index).setCellValue(headerMap.get(key));
            index++;
        }

        // 行政许可，从表数据
        if (ExchangeConfig.TYPE_ID_SSXX_XZXK.equals(firtZipFileData.getTypeId())) {
            // 获取扩展字段标题列
            Map<String, Integer> extendHeadMap = new LinkedHashMap<String, Integer>();
            Map<String, String> extendFieldMap = getExtendHeadMap(dataList);
            int extendIndex = 0;
            for (String key : extendFieldMap.keySet()) {
                int column = cellIndex + extendIndex;
                HSSFCell currentCell = currentRow.createCell(cellIndex + extendIndex);
                currentCell.setCellStyle(style);
                currentCell.setCellValue(extendFieldMap.get(key));

                extendHeadMap.put(key, column);

                extendIndex++;
            }

            // 保存设置扩展字段头信息
            for (ZipFileData zipFileData : dataList) {
                zipFileData.setExtendHeadMap(extendHeadMap);
            }
        }

        // 商事主体信息
        if (ExchangeConfig.TYPE_ID_SSXX_ZTDJ.equals(firtZipFileData.getTypeId())) {

        }
    }

    /**
     * @param dataList
     * @return
     */
    @SuppressWarnings("unchecked")
    private Map<String, String> getExtendHeadMap(List<ZipFileData> dataList) {
        return null;
    }

    /**
     * @param subDataMap
     * @return
     */
    private Map<String, Object> converToColumnMap(Map<String, Object> subDataMap) {
        return null;
    }

    /**
     * @param book
     * @param sheet
     * @param fieldMap
     * @param zipFileData
     * @param zipFileData
     */
    @SuppressWarnings("unchecked")
    private void createData(HSSFWorkbook book, HSSFSheet sheet, Map<String, String> fieldMap, List<ZipFileData> dataList) {
        return;
    }

    /**
     * @param ssztSheetRowMap
     * @param sheet1
     * @return
     */
    private int getCurrentRow(Map<HSSFSheet, Integer> ssztSheetRowMap, HSSFSheet sheet1) {
        Integer currentRow = ssztSheetRowMap.get(sheet1);
        if (currentRow == null) {
            ssztSheetRowMap.put(sheet1, Integer.valueOf(0) + 1);
        }
        return ssztSheetRowMap.get(sheet1);
    }

    /**
     * @param ssztSheetRowMap
     * @param sheet1
     * @return
     */
    private int getNextRow(Map<HSSFSheet, Integer> ssztSheetRowMap, HSSFSheet sheet1) {
        Integer currentRow = ssztSheetRowMap.get(sheet1);
        if (currentRow == null) {
            ssztSheetRowMap.put(sheet1, Integer.valueOf(0));
        }
        currentRow = ssztSheetRowMap.get(sheet1);
        int nextRow = currentRow + 1;
        ssztSheetRowMap.put(sheet1, nextRow);
        return nextRow;
    }

    /**
     * @param zipFile
     * @param zipEntry
     * @throws Exception
     */
    private Map<String, String> sendExcelRequest(ZipFile zipFile, ZipEntry zipEntry) throws Exception {
        return null;
    }

    /**
     * @param datas
     * @return
     */
    private Map<Integer, List<Map<String, String>>> validateData(String formId, List<Map<String, Object>> datas) {
        return null;
    }

    /**
     * @param value
     * @return
     */
    private String getValueAsString(Object value) {
        if (value == null) {
            return "";
        }

        if (value instanceof Date) {
            return DateUtils.formatDateTime((Date) value);
        }

        return value.toString();
    }

    /**
     * 获取附件的文件名
     *
     * @param dataItem
     * @return
     */
    private String getAttachFolderName(DataItem dataItem) {
        return dataItem.getDataId() + "_" + dataItem.getRecVer();
    }

    /**
     * @param zipFile
     * @return
     */
    private List<ZipEntry> getExcelEntries(ZipFile zipFile) {
        List<ZipEntry> zipEntries = new ArrayList<ZipEntry>();
        Enumeration<ZipEntry> e = zipFile.getEntries();
        while (e.hasMoreElements()) {
            ZipEntry entry = e.nextElement();
            if (entry.isDirectory()) {
                continue;
            }

            String name = entry.getName();
            String extension = StringUtils.substring(name, name.lastIndexOf(".") + 1);
            if ("xls".equalsIgnoreCase(extension) || "xlsx".equalsIgnoreCase(extension)) {
                zipEntries.add(entry);
            }
        }
        return zipEntries;
    }

    /**
     * 根据ZIP包中的夹实体，获取该夹实体下的所有文件实体
     *
     * @param zipFile
     * @param folderEntry
     * @return
     */
    private List<ZipEntry> getFileZipEntries(ZipFile zipFile, ZipEntry folderEntry) {
        List<ZipEntry> zipEntries = new ArrayList<ZipEntry>();
        if (folderEntry == null) {
            return zipEntries;
        }
        String folderName = folderEntry.getName();
        Enumeration<ZipEntry> e = zipFile.getEntries();
        while (e.hasMoreElements()) {
            ZipEntry entry = e.nextElement();
            if (!entry.isDirectory() && entry.getName().startsWith(folderName)) {
                zipEntries.add(entry);
            }
        }
        return zipEntries;
    }

}
