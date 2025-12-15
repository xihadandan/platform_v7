package com.wellsoft.pt.integration.web;

import com.wellsoft.context.util.file.FileDownloadUtils;
import com.wellsoft.pt.integration.entity.ExchangeDataTransform;
import com.wellsoft.pt.integration.entity.ExchangeDataType;
import com.wellsoft.pt.integration.entity.ExchangeRoute;
import com.wellsoft.pt.integration.entity.ExchangeSystem;
import com.wellsoft.pt.integration.service.ExchangeDataConfigService;
import com.wellsoft.pt.integration.service.ExchangeDataTransformService;
import com.wellsoft.pt.integration.service.ExchangeDataTypeService;
import com.wellsoft.pt.integration.service.ExchangeRouteService;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author Administrator
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-11-17.1	wbx		2013-11-17		Create
 * </pre>
 * @date 2013-11-17
 */
@Controller
@RequestMapping("/exchangedata/dataconfig")
public class ExchangeDataConfigController {
    protected Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private ExchangeDataConfigService exchangeDataConfigService;
    @Autowired
    private ExchangeDataTransformService exchangeDataTransformService;
    @Autowired
    private ExchangeRouteService exchangeRouteService;
    @Autowired
    private ExchangeDataTypeService exchangeDataTypeService;

    @RequestMapping(value = "/exchange_systemlist")
    public String exchangeSystemList(ModelMap modelMap) {
        List<ExchangeSystem> dataList = exchangeDataConfigService.getExSystemList();
        modelMap.put("dataList", dataList);
        return "pt/exchangedata/datasyspage";
    }

    @RequestMapping(value = "/exchange_data_transformlist")
    public String exchangeDataTransform(ModelMap modelMap) {
        List<ExchangeDataTransform> dataList = exchangeDataTransformService.listAll();
        modelMap.put("dataList", dataList);
        return "pt/exchangedata/transformpage";
    }

    @RequestMapping(value = "/exchange_routelist")
    public String exchangeRoute(ModelMap modelMap) {
        List<ExchangeRoute> dataList = exchangeRouteService.getExRouteList();
        modelMap.put("dataList", dataList);
        return "pt/exchangedata/routepage";
    }

    @RequestMapping(value = "/exchange_data_typelist")
    public String exchangeDataType(ModelMap modelMap) {
        List<ExchangeDataType> dataList = exchangeDataTypeService.getExDataTypeList();
        modelMap.put("dataList", dataList);
        return "pt/exchangedata/datatype";
    }

    @RequestMapping(value = "/exchange_loglist")
    public String exchangeLog(ModelMap modelMap) {
        // List<ExchangeDataLog> dataList =
        // exchangeDataConfigService.getExchangeLogList();
        // modelMap.put("dataList", dataList);
        return "pt/exchangedata/exchangelog";
    }

    // 读取处理
    @RequestMapping(value = "/downLoadXml")
    @ResponseBody
    public void downLoadXml(@RequestParam("uuid") String uuid, HttpServletRequest request, HttpServletResponse response) {
        ExchangeDataType edt = exchangeDataTypeService.getExchangeDataTypeByUuid(uuid);
        if (edt.getFormId() != null && !edt.getFormId().equals("")) {
            try {
                String text_ = IOUtils.toString(edt.getText().getCharacterStream()) == null ? "" : IOUtils.toString(
                        edt.getText().getCharacterStream()).toString();
                InputStream inputStream = new ByteArrayInputStream(text_.getBytes("UTF-8"));
                String fileName = edt.getName();
                FileDownloadUtils.download(request, response, inputStream, fileName + ".xml");
            } catch (IOException e) {
                // TODO Auto-generated catch block
                logger.info(e.getMessage());
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                logger.info(e.getMessage());
            }
        } else {
            ServletOutputStream os;
            try {
                os = response.getOutputStream();
                os.print("<script type='text/javascript'>alert('数据类型未指定对应表单');</script>");
            } catch (IOException e) {
                logger.info(e.getMessage());
            }
        }
    }

    @RequestMapping(value = "/exchange_sg_set")
    public String exchangeDataSGSet(ModelMap modelMap) {
        Map<String, String> map = exchangeDataConfigService.getSGSet();
        modelMap.put("result", map);
        return "pt/exchangedata/exchangesgset";
    }

    @RequestMapping(value = "/dx_exchange_loglist")
    public String dXExchangeLog(ModelMap modelMap) {
        return "pt/exchangedata/dXExchangelog";
    }

    @RequestMapping(value = "/synchronous_source")
    public String synchronousSource() {

        return "pt/exchangedata/synchronousSource";
    }

    @RequestMapping(value = "/synchronous_rules")
    public String synchronousRules() {

        return "pt/exchangedata/synchronousRules";
    }

    @RequestMapping(value = "/sys_properties")
    public String sysProperties() {

        return "pt/exchangedata/sysProperties";
    }
}
