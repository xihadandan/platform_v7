package com.wellsoft.pt.bm.service.impl;

import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.bm.bean.SelfPublicityBean;
import com.wellsoft.pt.bm.entity.*;
import com.wellsoft.pt.bm.service.*;
import com.wellsoft.pt.integration.service.ExchangeDataClientService;
import com.wellsoft.pt.integration.service.ExchangeDataFlowService;
import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.repository.entity.FileEntity;
import com.wellsoft.pt.repository.service.MongoFileService;
import com.wellsoft.pt.unit.facade.service.UnitApiFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.*;

/**
 * Description: CommercialBusinessServiceImpl
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
@Service
@Transactional
public class CommercialBusinessServiceImpl extends BaseServiceImpl implements CommercialBusinessService {

    @Autowired
    private AdvisoryComplaintsService advisoryComplaintsService;
    @Autowired
    private RegisterApplyService registerApplyService;
    @Autowired
    private SubmitVerifyService submitVerifyService;
    @Autowired
    private PublicityAttachService publicityAttachService;
    @Autowired
    private MongoFileService fileService;
    @Autowired
    private ExchangeDataFlowService exchangeDataFlowService;
    @Autowired
    private UnitApiFacade unitApiFacade;

    @Autowired
    private ExchangeDataClientService exchangeDataClientService;

    /**
     * 咨询投诉回复
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bm.service.CommercialBusinessService#doReply(com.wellsoft.pt.bm.entity.AdvisoryComplaints)
     */
    @Override
    public String doReply(AdvisoryComplaints object) {
        if (object.getUuid() != null || !"".equals(object.getUuid())) {
            AdvisoryComplaints obj = advisoryComplaintsService.getOne(object.getUuid());
            obj.setIsReply(true);
            obj.setReplyTime(new Date());
            obj.setReplyMsg(object.getReplyMsg());
            advisoryComplaintsService.save(obj);
            return "success";
        } else {
            return "fail";
        }
    }

    @Override
    public AdvisoryComplaints getAdviceByUuid(String uuid) {
        return advisoryComplaintsService.getOne(uuid);
    }

    /**
     * 荣誉/资质审核
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bm.service.CommercialBusinessService#doVerify(com.wellsoft.pt.bm.bean.SelfPublicityBean)
     */
    @Override
    public String doSelfPublicVerify(SelfPublicityBean bean) {
        return null;
    }

    /**
     * 返回荣誉/资质公示bean
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bm.service.CommercialBusinessService#getSelfPublicBeanByUuid(java.lang.String)
     */
    @Override
    public SelfPublicityBean getSelfPublicBeanByUuid(String uuid) {
        SubmitVerify submitVerify = submitVerifyService.getOne(uuid);
        SelfPublicityApply selfPublicityApply = submitVerify.getSelfPublicityApply();
        RegisterApply registerApply = registerApplyService.queryByZch("zch", selfPublicityApply.getZch());

        SelfPublicityBean bean = new SelfPublicityBean();
        BeanUtils.copyProperties(selfPublicityApply, bean);

        bean.setIsVerify(submitVerify.getIsVerify() + "");
        bean.setVerifyId(submitVerify.getVerifyId());
        bean.setSubmitTime(submitVerify.getSubmitTime());
        bean.setVerifyTime(submitVerify.getVerifyTime());
        bean.setReplyMsg(submitVerify.getReplyMsg());

        if (registerApply != null) {
            bean.setReMc(registerApply.getMc());
            bean.setReFrxm(registerApply.getFrxm());
            bean.setReEmail(registerApply.getEmail());
            bean.setReMp(registerApply.getMp());
        }

        if (submitVerify.getSelfPublicityApply() != null) {
            List<PublicityAttach> attachs = this.publicityAttachService.listByPuuid(submitVerify
                    .getSelfPublicityApply().getUuid());
            List<PublicityAttach> publicityAttachs = new ArrayList<PublicityAttach>();
            for (PublicityAttach attach : attachs) {
                PublicityAttach publicityAttach = new PublicityAttach();
                BeanUtils.copyProperties(attach, publicityAttach);
                publicityAttachs.add(publicityAttach);
            }
            bean.setPublicityAttachs(publicityAttachs);
        }
        return bean;
    }

    /**
     * 返回主体注册信息
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bm.service.CommercialBusinessService#getRegisterByUuid(java.lang.String)
     */
    @Override
    public RegisterApply getRegisterByUuid(String uuid) {
        return registerApplyService.getOne(uuid);
    }

    /**
     * 主体注册审核
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bm.service.CommercialBusinessService#doRegisterReply(com.wellsoft.pt.bm.entity.RegisterApply)
     */
    @Override
    public String doRegisterReply(RegisterApply obj) {
        if (obj.getUuid() != null || !"".equals(obj.getUuid())) {
            RegisterApply ra = registerApplyService.getOne(obj.getUuid());
            ra.setVerifyTime(new Date());
            if (!obj.getIsVerify().equals("1")) {
                ra.setReplyMsg(obj.getReplyMsg());
            } else if (obj.getIsVerify().equals("1")) {
                ra.setReplyMsg("");
            }
            ra.setIsVerify(obj.getIsVerify());
            // 备用
            // CommonUnit unit = SpringSecurityUtils.getCurrentUserCommonUnit();
            // if(unit!=null) {
            // ra.setVerifyId(unit.getId());
            // }
            registerApplyService.save(ra);
            return "success";
        }
        return "fail";
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bm.service.CommercialBusinessService#getPublicityAttach(java.lang.String)
     */
    @Override
    public FileEntity getPublicityAttach(String uuid) {
        FileEntity fileEntity = new FileEntity();
        try {
            PublicityAttach publicityAttach = this.publicityAttachService.getOne(uuid);
            fileEntity.setFilename(publicityAttach.getFjmc());
            fileEntity.setFile(publicityAttach.getBody().getBinaryStream());
        } catch (Exception e) {
            logger.info(e.getMessage());
            throw new RuntimeException(e);
        }
        return fileEntity;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bm.service.CommercialBusinessService#getCommercialRegister()
     */
    @Override
    public Map<String, Object> getCommercialRegister() {
        Map<String, Object> rpMap = new HashMap<String, Object>();
        rpMap.put("branchNumber", 2);
        List<List<Map<String, String>>> processes = new ArrayList<List<Map<String, String>>>();
        rpMap.put("data", processes);

        // 1 企业登记
        List<Map<String, String>> proce1 = new ArrayList<Map<String, String>>();
        Map<String, String> itemMap1 = new HashMap<String, String>();
        itemMap1.put("task", "企业登记");
        itemMap1.put("code", "1");
        itemMap1.put("time", "3:43 PM");
        proce1.add(itemMap1);
        processes.add(proce1);

        // 2 工商上传
        List<Map<String, String>> proce2 = new ArrayList<Map<String, String>>();
        Map<String, String> itemMap2 = new HashMap<String, String>();
        itemMap2.put("task", "工商上传");
        itemMap2.put("code", "2");
        itemMap2.put("time", "3:43 PM");
        proce2.add(itemMap2);
        processes.add(proce2);

        // 3分发各个局
        List<Map<String, String>> proce3 = new ArrayList<Map<String, String>>();
        Map<String, String> itemMap31 = new HashMap<String, String>();
        itemMap31.put("task", "送达局1");
        itemMap31.put("code", "3");
        itemMap31.put("time", "3:43 PM");
        proce3.add(itemMap31);
        Map<String, String> itemMap32 = new HashMap<String, String>();
        itemMap32.put("task", "送达局2");
        itemMap32.put("code", "3");
        itemMap32.put("time", "3:43 PM");
        proce3.add(itemMap32);
        Map<String, String> itemMap33 = new HashMap<String, String>();
        itemMap33.put("task", "送达局3");
        itemMap33.put("code", "3");
        itemMap33.put("time", "3:43 PM");
        proce3.add(itemMap33);
        processes.add(proce3);

        // 4签收
        List<Map<String, String>> proce4 = new ArrayList<Map<String, String>>();
        Map<String, String> itemMap41 = new HashMap<String, String>();
        itemMap41.put("task", "签收1");
        itemMap41.put("code", "4");
        itemMap41.put("time", "3:43 PM");
        proce4.add(itemMap41);
        Map<String, String> itemMap42 = new HashMap<String, String>();
        itemMap42.put("task", "签收2");
        itemMap42.put("code", "4");
        itemMap42.put("time", "3:43 PM");
        proce4.add(itemMap42);
        Map<String, String> itemMap43 = new HashMap<String, String>();
        itemMap43.put("task", "签收3");
        itemMap43.put("code", "4");
        itemMap43.put("time", "3:43 PM");
        proce4.add(itemMap43);
        processes.add(proce4);

        // 5出签登记
        List<Map<String, String>> proce5 = new ArrayList<Map<String, String>>();
        Map<String, String> itemMap51 = new HashMap<String, String>();
        itemMap51.put("task", "出证1");
        itemMap51.put("code", "5");
        itemMap51.put("time", "3:43 PM");
        proce5.add(itemMap51);
        Map<String, String> itemMap52 = new HashMap<String, String>();
        // itemMap52.put("task", "出证2");
        // itemMap52.put("code", "5");
        // itemMap52.put("time", "3:43 PM");
        proce5.add(itemMap52);
        Map<String, String> itemMap53 = new HashMap<String, String>();
        itemMap53.put("task", "出证3");
        itemMap53.put("code", "5");
        itemMap53.put("time", "3:43 PM");
        proce5.add(itemMap53);
        processes.add(proce5);

        // 5出证
        List<Map<String, String>> proce6 = new ArrayList<Map<String, String>>();
        Map<String, String> itemMap61 = new HashMap<String, String>();
        itemMap61.put("task", "出证上传登记1");
        itemMap61.put("code", "6");
        itemMap61.put("time", "3:43 PM");
        proce6.add(itemMap61);
        Map<String, String> itemMap62 = new HashMap<String, String>();
        // itemMap62.put("task", "出证上传登记2");
        // itemMap62.put("code", "6");
        // itemMap62.put("time", "3:43 PM");
        proce6.add(itemMap62);
        Map<String, String> itemMap63 = new HashMap<String, String>();
        itemMap63.put("task", "出证上传登记3");
        itemMap63.put("code", "6");
        itemMap63.put("time", "3:43 PM");
        proce6.add(itemMap63);
        processes.add(proce6);

        return rpMap;
    }

    /**
     * (non-Javadoc)
     *
     * @throws Exception
     * @see com.wellsoft.pt.bm.service.CommercialBusinessService#generateZipFile(java.util.Collection)
     */
    @Override
    public File generateZipFile(Collection<String> uuids) throws Exception {
        File file = exchangeDataClientService.generateZipFile(uuids);
        return file;
    }

    @Override
    public List<QueryItem> queryQueryItem(String hql, Map<String, Object> values, PagingInfo pagingInfo) {
        return this.advisoryComplaintsService.listQueryItemByHQL(hql, values, pagingInfo);
    }

    @Override
    public Long findCountByHql(String string, Map<String, Object> values) {
        return this.advisoryComplaintsService.countAdvisoryCmpliants(string, values);
    }
}
