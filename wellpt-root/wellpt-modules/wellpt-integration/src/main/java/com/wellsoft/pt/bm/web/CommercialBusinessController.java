package com.wellsoft.pt.bm.web;

import com.wellsoft.context.util.file.FileDownloadUtils;
import com.wellsoft.pt.bm.bean.SelfPublicityBean;
import com.wellsoft.pt.bm.entity.AdvisoryComplaints;
import com.wellsoft.pt.bm.entity.RegisterApply;
import com.wellsoft.pt.bm.service.CommercialBusinessService;
import com.wellsoft.pt.repository.entity.FileEntity;
import com.wellsoft.pt.unit.entity.CommonUnit;
import com.wellsoft.pt.unit.facade.service.UnitApiFacade;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Collection;

/**
 * Description: 商事业务办理
 *
 * @author wangbx
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-12-5.1	wangbx		2013-12-5		Create
 * </pre>
 * @date 2013-12-5
 */
@Controller
@RequestMapping("/commercial/business")
public class CommercialBusinessController {
    protected Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private CommercialBusinessService commercialBusinessService;
    @Autowired
    private UnitApiFacade unitApiFacade;

    /**
     * 到咨询投诉详细页
     *
     * @param uuid
     * @param model
     * @return
     */
    @RequestMapping(value = "/toAdviceDetail")
    public String toDetailPage(@RequestParam(value = "uuid") String uuid, Model model) {
        AdvisoryComplaints advisoryComplaints = commercialBusinessService.getAdviceByUuid(uuid);
        CommonUnit unit = unitApiFacade.getCommonUnitById(advisoryComplaints.getReplyId());
        model.addAttribute("object", advisoryComplaints);
        if (unit != null) {
            model.addAttribute("replyUnitName", unit.getName());
        }
        return "pt/exchangedata/advice";
    }

    /**
     * 到咨询投诉详细页
     *
     * @param uuid
     * @param model
     * @return
     */
    @RequestMapping(value = "/download")
    public void download(@RequestParam(value = "uuids") Collection<String> uuids, HttpServletRequest request,
                         HttpServletResponse response) {
        try {
            File zipFile = commercialBusinessService.generateZipFile(uuids);

            FileDownloadUtils.download(request, response, new FileInputStream(zipFile), zipFile.getName());

            if (zipFile.exists()) {
                zipFile.delete();
            }
        } catch (Exception e) {
            logger.info(e.getMessage());
            FileDownloadUtils.download(request, response, new ByteArrayInputStream(ExceptionUtils.getStackTrace(e)
                    .getBytes()), "error.txt");
        }
    }

    /**
     * 到自主公示详细页
     *
     * @param uuid
     * @param model
     * @return
     */
    @RequestMapping(value = "/toSelfPublicDetail")
    public String toSelfPublic(@RequestParam(value = "verifyUuid") String uuid, Model model) {
        SelfPublicityBean selfPublicityBean = commercialBusinessService.getSelfPublicBeanByUuid(uuid);
        CommonUnit unit = unitApiFacade.getCommonUnitById(selfPublicityBean.getVerifyId());
        model.addAttribute("bean", selfPublicityBean);
        if (unit != null) {
            model.addAttribute("replyUnitName", unit.getName());
        }
        return "pt/exchangedata/selfpublic";
    }

    @RequestMapping(value = "/toRegisterApply")
    public String toRegisterApply(@RequestParam(value = "uuid") String uuid, Model model) {
        RegisterApply registerApply = commercialBusinessService.getRegisterByUuid(uuid);
        CommonUnit unit = unitApiFacade.getCommonUnitById(registerApply.getVerifyId());
        model.addAttribute("object", registerApply);
        if (unit != null) {
            model.addAttribute("replyUnitName", unit.getName());
        }
        return "pt/exchangedata/registerapply";
    }

    @RequestMapping(value = "/publicity/attach/{uuid}")
    public void publicityAttach(@PathVariable(value = "uuid") String uuid, HttpServletRequest request,
                                HttpServletResponse response) {
        try {
            FileEntity fileEntity = commercialBusinessService.getPublicityAttach(uuid);
            // 上传处理
            InputStream is = fileEntity.getFile();
            FileDownloadUtils.download(request, response, is, fileEntity.getFilename());
        } catch (Exception e) {
            FileDownloadUtils.download(request, response, new ByteArrayInputStream(e.getMessage().getBytes()), uuid);
        }
    }
}
