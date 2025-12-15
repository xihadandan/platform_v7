/*
 * @(#)2013-1-15 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.org.unit.service.impl;

import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.pt.org.dao.DutyDao;
import com.wellsoft.pt.org.entity.Duty;
import com.wellsoft.pt.org.entity.Job;
import com.wellsoft.pt.org.service.DutyService;
import com.wellsoft.pt.org.service.JobService;
import com.wellsoft.pt.org.unit.service.UnitTreeTypeDelegateService;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.wellsoft.pt.org.unit.service.impl.UnitTreeServiceImpl.*;

/**
 * Description: 岗位选择service
 *
 * @author zhengky
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	       修改人		修改日期	      修改内容
 * 2014-8-17.1  zhengky	2014-8-17	  Create
 * </pre>
 * @date 2014-8-17
 */
@Service
@Transactional
public class UnitTreeJobDutyTypeDelegateServiceImpl extends UnitTreeTypeDelegateService {
    @Autowired
    private JobService jobService;
    @Autowired
    private DutyService dutyService;
    @Autowired
    private DutyDao dutyDao;

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.unit.service.UnitTreeTypeDelegateService#parserUnit(java.lang.String,
     * java.lang.String, java.lang.String)
     */
    @Override
    public Document parserUnit(String type, String all, String login, String filterCondition, HashMap<String, String> filterDisplayMap) {
        Document document = DocumentHelper.createDocument();
        Element root = document.addElement(NODE_UNITS);
        List<Duty> dutys = dutyService.getAllDutys();
        buildFullDutyXML(root, dutys, "", filterDisplayMap);
        return document;
    }

    /**
     * 如何描述该方法
     *
     * @param element
     * @param groups
     * @param prefix
     */
    private void buildFullDutyXML(Element element, List<Duty> dutys, String prefix, HashMap filterDisplayMap) {
        for (Duty duty : dutys) {
            Element root = element.addElement(NODE_UNIT);
            String path = prefix + duty.getName();
            String isLeaf = FALSE;
            if (duty.getJobs().size() < 1) {
                isLeaf = TRUE;
            }
            setDutyElement(root, duty, isLeaf, path, filterDisplayMap);

        }
    }

    protected void setDutyElement(Element element, Duty duty, String isLeaf, String path, HashMap filterDisplayMap) {
        if (filterDisplayMap.get(duty.getId()) != null) return;
        element.addAttribute(ATTRIBUTE_TYPE, ATTRIBUTE_TYPE_DUTY);
        element.addAttribute(ATTRIBUTE_ISLEAF, isLeaf);
        element.addAttribute(ATTRIBUTE_ID, duty.getId());
        element.addAttribute(ATTRIBUTE_NAME, duty.getName() + "【" + duty.getDutyLevel() + "】");
        element.addAttribute(ATTRIBUTE_PATH, path);
    }

    /**
     * 如何描述该方法
     *
     * @param element
     * @param user
     * @param path
     */
    protected void setDutyJobElement(Element element, Job job, String path, HashMap filterDisplayMap) {
        setDutyJobElement(element, ATTRIBUTE_TYPE_DUTY, "1", job.getId(), job.getDepartmentName() + job.getName(),
                path, "", filterDisplayMap);
    }

    protected void setDepartmentJobElement(Element element, Job job, String type, String path, String title, HashMap filterDisplayMap) {
        setDutyJobElement(element, ATTRIBUTE_TYPE_DUTY, type, job.getId(), job.getDepartmentName() + job.getName(),
                path, title, filterDisplayMap);
    }

    protected void setDutyJobElement(Element element, String type, String isLeaf, String id, String name, String path,
                                     String title, HashMap filterDisplayMap) {
        if (filterDisplayMap.get(id) != null) return;
        element.addAttribute(ATTRIBUTE_TYPE, type);
        element.addAttribute(ATTRIBUTE_ISLEAF, isLeaf);
        element.addAttribute(ATTRIBUTE_ID, id);
        element.addAttribute(ATTRIBUTE_NAME, name);
        element.addAttribute(ATTRIBUTE_PATH, path);
        if (StringUtils.isNotBlank(title)) {
            element.addAttribute(ATTRIBUTE_TITLE, title);
        }
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.unit.service.UnitTreeTypeDelegateService#toggleUnit(java.lang.String,
     * java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public Document toggleUnit(String type, String id, String all, String login, HashMap<String, String> filterDisplayMap) {

        Document document = DocumentHelper.createDocument();
        Duty duty = this.dutyService.getById(id);
        Element toproot = document.addElement(NODE_UNITS);

        Element root = toproot.addElement(NODE_UNIT);
        String isLeaf = FALSE;
        String path = duty.getName();
        setDutyElement(root, duty, isLeaf, path, filterDisplayMap);

        List<Job> jobs = jobService.getJobByDutyUuid(duty.getUuid());
        // 获取部门下的岗位
        for (Job job : jobs) {
            Element child = root.addElement(NODE_UNIT);
            String jobPath = job.getDepartmentName() + "/" + job.getName();
            setDutyJobElement(child, job, jobPath, filterDisplayMap);
        }

        return document;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.unit.service.UnitTreeTypeDelegateService#leafUnit(java.lang.String,
     * java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public Document leafUnit(String type, String id, String leafType, String login, HashMap<String, String> filterDisplayMap) {
        Document document = DocumentHelper.createDocument();
        if (id.startsWith("W")) {
            Duty duty = this.dutyService.getById(id);
            Element root = document.addElement(NODE_UNITS);
            Set<Job> jobs = duty.getJobs();
            for (Job job : jobs) {
                Element child = root.addElement(NODE_UNIT);
                String jobPath = job.getDepartmentName() + "/" + job.getName();
                setDutyJobElement(child, job, jobPath, filterDisplayMap);
            }
        }
        return document;
    }

    /**
     * 如何描述该方法
     *
     * @param type
     * @param all
     * @param login
     * @param searchValue
     */
    public Document searchXml(String type, String all, String login, String searchValue, HashMap filterDisplayMap) {

        Map<String, Object> values = new HashMap<String, Object>();
        values.put("code", searchValue);
        values.put("name", searchValue);
        values.put("dutyLevel", searchValue);
        values.put("remark", searchValue);
        List<Duty> dataList = this.dutyService.namedQuery("dutyQuery", values, Duty.class, new PagingInfo(1,
                Integer.MAX_VALUE));
        Document document = DocumentHelper.createDocument();
        Element root = document.addElement(NODE_UNITS);

        buildFullDutyXML(root, dataList, "", filterDisplayMap);

        return document;
    }

}
