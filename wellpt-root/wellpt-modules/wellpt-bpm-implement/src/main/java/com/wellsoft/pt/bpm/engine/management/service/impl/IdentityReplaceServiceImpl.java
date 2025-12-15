package com.wellsoft.pt.bpm.engine.management.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.context.component.jqgrid.JqGridQueryInfo;
import com.wellsoft.context.component.jqgrid.JqTreeGridNode;
import com.wellsoft.context.enums.Encoding;
import com.wellsoft.context.enums.ModuleID;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.jdbc.support.QueryData;
import com.wellsoft.context.jdbc.support.QueryInfo;
import com.wellsoft.context.util.collection.ListUtils;
import com.wellsoft.context.util.date.DateUtils;
import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.pt.bpm.engine.core.FlowDefConstants;
import com.wellsoft.pt.bpm.engine.element.*;
import com.wellsoft.pt.bpm.engine.entity.FlowDefinition;
import com.wellsoft.pt.bpm.engine.entity.FlowSchema;
import com.wellsoft.pt.bpm.engine.enums.IdentityReplaceAction;
import com.wellsoft.pt.bpm.engine.exception.StarFlowParserException;
import com.wellsoft.pt.bpm.engine.management.dao.FlowSchemaLogDao;
import com.wellsoft.pt.bpm.engine.management.entity.FlowSchemaLog;
import com.wellsoft.pt.bpm.engine.management.service.IdentityReplaceService;
import com.wellsoft.pt.bpm.engine.management.support.IdentityReplaceRequest;
import com.wellsoft.pt.bpm.engine.parser.FlowDefinitionParser;
import com.wellsoft.pt.bpm.engine.service.FlowDefinitionService;
import com.wellsoft.pt.bpm.engine.service.FlowSchemaService;
import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import com.wellsoft.pt.log.entity.UserOperationLog;
import com.wellsoft.pt.log.enums.LogManageOperationEnum;
import com.wellsoft.pt.log.service.UserOperationLogService;
import com.wellsoft.pt.security.access.RequestSystemContextPathResolver;
import com.wellsoft.pt.security.core.userdetails.UserDetails;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import com.wellsoft.pt.workflow.service.FlowSchemeService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.custommonkey.xmlunit.DetailedDiff;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.Difference;
import org.custommonkey.xmlunit.NodeDetail;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.StringReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author linz
 * 流程参与人员批量修改
 */
@Service
public class IdentityReplaceServiceImpl extends BaseServiceImpl implements IdentityReplaceService {

    protected static final Map<String, String> ignoreFieldMap = new HashMap<String, String>();
    @Autowired
    FlowSchemaService flowSchemaService;
    @Autowired
    FlowDefinitionService flowDefinitionService;
    @Autowired
    FlowSchemaLogDao flowSchemaLogDao;
    @Autowired
    private FlowSchemeService flowSchemeService;
    @Autowired
    private UserOperationLogService userOperationLogService;

    /**
     * 创建代码流程定义的XML的文档对象
     *
     * @param xml
     * @return
     */
    public static Document createDocument(String xml) {
        SAXReader reader = new SAXReader();
        reader.setEncoding(Encoding.UTF8.getValue());
        Document document = null;
        try {
            document = reader.read(IOUtils.toInputStream(xml, Encoding.UTF8.getValue()));
            document.setXMLEncoding(Encoding.UTF8.getValue());
        } catch (Exception e) {
            throw new StarFlowParserException("流程定义信息不正确", e);
        }
        return document;
    }

    @Override
    public List<String> getXml() throws Exception {
        List<FlowSchema> flowSchemas = flowSchemaService.listAll();
        List<String> list = new ArrayList<String>();
        for (FlowSchema flowSchema : flowSchemas) {
            list.add(flowSchema.getContentAsString());
        }
        return list;
    }

    @Override
    @Transactional
    public QueryData getForPageAsTree(JqGridQueryInfo jqGridQueryInfo, QueryInfo queryInfo,
                                      String name,
                                      String station, String flowGroup, String department,
                                      String flowName, String webContent) {
        // 查询父节点为null的部门
        List<FlowElement> results = null;
        results = queryFlow(null, name, station, flowGroup, department, flowName);
        // results = pageData.getResult();
        List<JqTreeGridNode> retResults = new ArrayList<JqTreeGridNode>();
        for (int index = 0; index < results.size(); index++) {
            FlowElement flow = results.get(index);
            JqTreeGridNode node = new JqTreeGridNode();
            node.setId(String.valueOf(flow.getUuid()));// id
            List<Object> cell = node.getCell();
            cell.add(flow.getUuid());// UUID
            cell.add(
                    "<img text='查看修改日记'  onclick=\"showLog('" + flow.getUuid() + "')\" src='" + webContent
                            + "/pt/workflow/images/search.png' /> ");
            cell.add(flow.getName());// name流程名
            BigDecimal bigDecimal = new BigDecimal(flow.getVersion()).add(BigDecimal.ZERO).setScale(
                    1,
                    RoundingMode.DOWN);
            if (bigDecimal.toString().length() == 1) {
                cell.add(bigDecimal + ".0");
            } else {
                cell.add(bigDecimal + "");
            }
            // 取出property节点数据
            PropertyElement propertyElement = flow.getProperty();
            if (propertyElement != null) {
                // 取出发起人集合
                List<UserUnitElement> creators = propertyElement.getCreators();
                if (creators != null && creators.size() > 0) {
                    StringBuilder creatorsBuff = new StringBuilder();
                    for (UnitElement unitElement : creators) {
                        creatorsBuff.append(
                                StringUtils.isEmpty(
                                        unitElement.getArgValue()) ? unitElement.getValue() : unitElement
                                        .getArgValue()).append(";");
                    }
                    if (StringUtils.isNotEmpty(creatorsBuff.toString())) {
                        cell.add((creatorsBuff.toString()).substring(0,
                                (creatorsBuff.toString()).lastIndexOf(";")));// 发起人
                    } else {
                        cell.add("");// 发起人
                    }
                } else {
                    cell.add("");// 发起人
                } // 发起人结束

                // 取出参与人集合
                List<UserUnitElement> propertyUsers = propertyElement.getUsers();
                if (propertyUsers != null && propertyUsers.size() > 0) {
                    StringBuilder propertyUsersBuff = new StringBuilder();
                    for (UnitElement unitElement : propertyUsers) {
                        propertyUsersBuff.append(
                                StringUtils.isEmpty(
                                        unitElement.getArgValue()) ? unitElement.getValue() : unitElement
                                        .getArgValue()).append(";");
                    }
                    if (StringUtils.isNotEmpty(propertyUsersBuff.toString())) {
                        cell.add((propertyUsersBuff.toString()).substring(0,
                                (propertyUsersBuff.toString()).lastIndexOf(";")));// 发起人
                    } else {
                        cell.add("");// 参与人
                    }
                } else {
                    cell.add("");// 参与人
                } // 参与人结束

                // 取出督办人集合
                List<UserUnitElement> monitors = propertyElement.getMonitors();
                if (monitors != null && monitors.size() > 0) {
                    StringBuilder monitorsBuff = new StringBuilder();
                    for (UnitElement unitElement : monitors) {
                        monitorsBuff.append(
                                StringUtils.isEmpty(
                                        unitElement.getArgValue()) ? unitElement.getValue() : unitElement
                                        .getArgValue()).append(";");
                    }
                    if (StringUtils.isNotEmpty(monitorsBuff.toString())) {
                        cell.add((monitorsBuff.toString()).substring(0,
                                (monitorsBuff.toString()).lastIndexOf(";")));// 发起人
                    } else {
                        cell.add("");// 督办人
                    }
                } else {
                    cell.add("");// 督办人
                } // 督办人结束

                // 取出监控者集合
                List<UserUnitElement> admins = propertyElement.getAdmins();
                if (admins != null && admins.size() > 0) {
                    StringBuilder adminsBuff = new StringBuilder();
                    for (UnitElement unitElement : admins) {
                        adminsBuff.append(
                                StringUtils.isEmpty(
                                        unitElement.getArgValue()) ? unitElement.getValue() : unitElement
                                        .getArgValue()).append(";");
                    }
                    if (StringUtils.isNotEmpty(adminsBuff.toString())) {
                        cell.add((adminsBuff.toString()).substring(0,
                                (adminsBuff.toString()).lastIndexOf(";")));// 发起人
                    } else {
                        cell.add("");// 监控者
                    }
                } else {
                    cell.add("");// 监控者
                } // 监控者结束

                // 取出阅读者集合
                List<UserUnitElement> viewers = propertyElement.getViewers();
                if (viewers != null && viewers.size() > 0) {
                    StringBuilder viewersBuff = new StringBuilder();
                    for (UnitElement unitElement : viewers) {
                        viewersBuff.append(
                                StringUtils.isEmpty(
                                        unitElement.getArgValue()) ? unitElement.getValue() : unitElement
                                        .getArgValue()).append(";");
                    }
                    if (StringUtils.isNotEmpty(viewersBuff.toString())) {
                        cell.add((viewersBuff.toString()).substring(0,
                                (viewersBuff.toString()).lastIndexOf(";")));// 发起人
                    } else {
                        cell.add("");// 阅读者
                    }
                } else {
                    cell.add("");// 阅读者
                } // 阅读者结束

            }
            retResults.add(node);

        }
        QueryData queryData = new QueryData();
        queryData.setDataList(retResults);

        queryData.setPagingInfo(queryInfo.getPagingInfo());
        return queryData;
    }

    @Transactional
    public List<FlowElement> queryFlow(String selectRowIds, String nameSearch, String stationSearch,
                                       String flowGroupSearch, String departmentSearch,
                                       String flowNameSearch) {
        List<FlowSchema> flowSchemas = new ArrayList<FlowSchema>();
        // flowSchemas.add(flowSchemaService.get("66a75678-4eaf-4461-a7f7-31cebdac8882"));
        StringBuilder hqlBuff = new StringBuilder();
        hqlBuff.append("from FlowSchema f where 1=1 ");
        if (StringUtils.isNotEmpty(nameSearch)) {
            hqlBuff.append(" and content like '%");
            hqlBuff.append(nameSearch);
            hqlBuff.append("%'");
        }
        if (StringUtils.isNotEmpty(flowGroupSearch)) {
            hqlBuff.append(" and content like '%");
            hqlBuff.append(flowGroupSearch);
            hqlBuff.append("%'");
        }
        if (StringUtils.isNotEmpty(stationSearch)) {
            hqlBuff.append(" and content like '%");
            hqlBuff.append(stationSearch);
            hqlBuff.append("%'");
        }
        if (StringUtils.isNotEmpty(departmentSearch)) {
            hqlBuff.append(" and content like '%");
            hqlBuff.append(departmentSearch);
            hqlBuff.append("%'");
        }
        if (StringUtils.isNotEmpty(flowNameSearch)) {
            hqlBuff.append(" and content like '%");
            hqlBuff.append(flowNameSearch);
            hqlBuff.append("%'");
        }
        if (StringUtils.isNotEmpty(selectRowIds)) {
            hqlBuff.append(" and uuid in ('");
            hqlBuff.append(selectRowIds);
            hqlBuff.append("')");
        }

        flowSchemas = flowSchemaService.listByHQL(hqlBuff.toString(), null);
        List<String> list = new ArrayList<String>();
        List<FlowElement> flows = new ArrayList<FlowElement>();
        for (FlowSchema flowSchema : flowSchemas) {
            FlowElement flowEntity = FlowDefinitionParser.parseFlow(flowSchema);
            flowEntity.setUuid(flowSchema.getUuid());
            flows.add(flowEntity);
        }
        return flows;
    }

    @Override
    @Transactional
    public QueryData getForPageAsTreeDetail(JqGridQueryInfo jqGridQueryInfo, QueryInfo queryInfo,
                                            String uuid) {
        FlowSchema flowSchema = flowSchemaService.getOne(uuid);
        List<JqTreeGridNode> retResults = new ArrayList<JqTreeGridNode>();
        if (flowSchema != null) {
            FlowElement flowElement = FlowDefinitionParser.parseFlow(flowSchema);
            List<TaskElement> taskElements = flowElement.getTasks();
            if (taskElements != null && taskElements.size() > 0) {
                for (TaskElement taskElement : taskElements) {
                    JqTreeGridNode node = new JqTreeGridNode();
                    node.setId(uuid + "," + taskElement.getId());// id
                    List<Object> cell = node.getCell();
                    cell.add(uuid + "," + taskElement.getId());
                    cell.add(taskElement.getName());
                    List<UserUnitElement> taskUsers = taskElement.getUsers();
                    if (taskUsers != null && taskUsers.size() > 0) {
                        StringBuilder taskUsersBuff = new StringBuilder();
                        for (UnitElement unitElement : taskUsers) {
                            taskUsersBuff.append(
                                    StringUtils.isEmpty(
                                            unitElement.getArgValue()) ? unitElement.getValue()
                                            : unitElement.getArgValue()).append(";");
                        }
                        if (StringUtils.isNotEmpty(taskUsersBuff.toString())) {
                            cell.add((taskUsersBuff.toString()).substring(0,
                                    (taskUsersBuff.toString()).lastIndexOf(";")));// 当前操作人
                        } else {
                            cell.add("");// 当前操作人
                        }
                    } else {
                        cell.add("");// 当前操作人
                    } // 当前操作人结束

                    List<UserUnitElement> copyUsers = taskElement.getCopyUsers();
                    if (copyUsers != null && copyUsers.size() > 0) {
                        StringBuilder copyUsersBuff = new StringBuilder();
                        for (UnitElement unitElement : copyUsers) {
                            copyUsersBuff.append(
                                    StringUtils.isEmpty(
                                            unitElement.getArgValue()) ? unitElement.getValue()
                                            : unitElement.getArgValue()).append(";");
                        }
                        if (StringUtils.isNotEmpty(copyUsersBuff.toString())) {
                            cell.add((copyUsersBuff.toString()).substring(0,
                                    (copyUsersBuff.toString()).lastIndexOf(";")));// 当前操作人
                        } else {
                            cell.add("");// 抄送人
                        }
                    } else {
                        cell.add("");// 当前抄送人
                    } // 当前抄送人结束

                    List<UserUnitElement> monitors = taskElement.getMonitors();
                    if (monitors != null && monitors.size() > 0) {
                        StringBuilder monitorsBuff = new StringBuilder();
                        for (UnitElement unitElement : monitors) {
                            monitorsBuff.append(
                                    StringUtils.isEmpty(
                                            unitElement.getArgValue()) ? unitElement.getValue()
                                            : unitElement.getArgValue()).append(";");
                        }
                        if (StringUtils.isNotEmpty(monitorsBuff.toString())) {
                            cell.add((monitorsBuff.toString()).substring(0,
                                    (monitorsBuff.toString()).lastIndexOf(";")));// 当前操作人
                        } else {
                            cell.add("");// 监督者
                        }
                    } else {
                        cell.add("");// 监督者
                    } // 当前抄送人结束

                    retResults.add(node);
                }
            }
        }
        QueryData queryData = new QueryData();
        queryData.setDataList(retResults);

        queryData.setPagingInfo(queryInfo.getPagingInfo());
        return queryData;
    }

    @Override
    @Transactional
    public String saveBatchDateByGrid(String uuids, String taskIds, Boolean isUpdatecreator,
                                      Boolean isUpdatepropertyUser, Boolean isUpdatemonitor,
                                      Boolean isUpdateadmin, Boolean isUpdateviewer,
                                      Boolean isUpdatetaskUser, Boolean isUpdatecopyUser,
                                      Boolean isInsert, String oldUser, String newUser,
                                      String oldUserId, String newUserId) {
        String uuidList[] = uuids.split(";");
        // 遍历流程取出流程
        Map<FlowSchema, FlowElement> dateMap = new HashMap<FlowSchema, FlowElement>();
        // 获取登陆人信息
        UserDetails userDetails = SpringSecurityUtils.getCurrentUser();
        int type = 0;
        for (String uuid : uuidList) {
            StringBuilder logMsg = new StringBuilder();
            logMsg.append(userDetails.getUserName());
            // 保存日记信息
            FlowSchema flowSchema = flowSchemaService.getOne(uuid);
            FlowSchemaLog flowSchemaLog = new FlowSchemaLog();
            flowSchemaLog.setContent(flowSchema.getContentAsString());
            flowSchemaLog.setCreateTime(Calendar.getInstance().getTime());

            flowSchemaLog.setParentFlowSchemaUUID(flowSchema.getUuid());
            // 获取XML对象
            Document document = FlowDefinitionParser.createDocument(flowSchemaLog.getContent());

            List<Element> taskNodes = FlowDefinitionParser.selectElements(document,
                    FlowDefConstants.TASK_XPATH);

            // 更新创建人/发起人
            if (isUpdatecreator) {
                type = 1;
                logMsg.append("更新创建人/发起人：");
                List<Element> creatorNodes = FlowDefinitionParser.selectElements(document,
                        FlowDefConstants.FLOW_CREATORS_XPATH);
                List<Element> userNodeTemp = FlowDefinitionParser.selectElements(document,
                        "/flow/property/creators");
                if (userNodeTemp.size() == 0) {
                    List<Element> propertyNode = FlowDefinitionParser.selectElements(document,
                            "/flow/property");
                    Element element = propertyNode.get(0).addElement("creators");
                    List<Element> emElements = new ArrayList<Element>();
                    emElements.add(element);
                    updateUser(creatorNodes, emElements, oldUserId, newUserId, oldUser, newUser,
                            isInsert, type, logMsg);
                } else {
                    updateUser(creatorNodes, userNodeTemp, oldUserId, newUserId, oldUser, newUser,
                            isInsert, type,
                            logMsg);
                }
            }
            // 原参与人
            if (isUpdatepropertyUser) {
                type = 1;
                logMsg.append("更新参与人：");
                // 参与人
                List<Element> userNodes = FlowDefinitionParser.selectElements(document,
                        FlowDefConstants.FLOW_USERS_XPATH);
                List<Element> userNodeTemp = FlowDefinitionParser.selectElements(document,
                        "/flow/property/users");
                if (userNodeTemp.size() == 0) {
                    List<Element> propertyNode = FlowDefinitionParser.selectElements(document,
                            "/flow/property");
                    Element element = propertyNode.get(0).addElement("users");
                    List<Element> emElements = new ArrayList<Element>();
                    emElements.add(element);
                    updateUser(userNodes, emElements, oldUserId, newUserId, oldUser, newUser,
                            isInsert, type, logMsg);
                } else {
                    updateUser(userNodes, userNodeTemp, oldUserId, newUserId, oldUser, newUser,
                            isInsert, type, logMsg);
                }
            }
            // 督办人
            if (isUpdatemonitor) {
                type = 1;
                logMsg.append("更新督办人：");
                // 督办人
                List<Element> monitorNodes = FlowDefinitionParser.selectElements(document,
                        FlowDefConstants.FLOW_MONITORS_XPATH);
                List<Element> userNodeTemp = FlowDefinitionParser.selectElements(document,
                        "/flow/property/monitors");
                if (userNodeTemp.size() == 0) {
                    List<Element> propertyNode = FlowDefinitionParser.selectElements(document,
                            "/flow/property");
                    Element element = propertyNode.get(0).addElement("monitors");
                    List<Element> emElements = new ArrayList<Element>();
                    emElements.add(element);
                    updateUser(monitorNodes, emElements, oldUserId, newUserId, oldUser, newUser,
                            isInsert, type, logMsg);
                } else {
                    updateUser(monitorNodes, userNodeTemp, oldUserId, newUserId, oldUser, newUser,
                            isInsert, type,
                            logMsg);
                }
            }
            // 监控者
            if (isUpdateadmin) {
                type = 1;
                logMsg.append("更新监控者：");
                // 监控者
                List<Element> adminNodes = FlowDefinitionParser.selectElements(document,
                        FlowDefConstants.FLOW_ADMINS_XPATH);
                List<Element> userNodeTemp = FlowDefinitionParser.selectElements(document,
                        "/flow/property/admins");
                if (userNodeTemp.size() == 0) {
                    List<Element> propertyNode = FlowDefinitionParser.selectElements(document,
                            "/flow/property");
                    Element element = propertyNode.get(0).addElement("admins");
                    List<Element> emElements = new ArrayList<Element>();
                    emElements.add(element);
                    updateUser(adminNodes, emElements, oldUserId, newUserId, oldUser, newUser,
                            isInsert, type, logMsg);
                } else {
                    updateUser(adminNodes, userNodeTemp, oldUserId, newUserId, oldUser, newUser,
                            isInsert, type, logMsg);
                }
            }
            // 阅读者
            if (isUpdateviewer) {
                type = 1;
                logMsg.append("更新阅读者：");
                List<Element> viewerNodes = FlowDefinitionParser.selectElements(document,
                        FlowDefConstants.FLOW_VIEWERS_XPATH);
                List<Element> userNodeTemp = FlowDefinitionParser.selectElements(document,
                        "/flow/property/viewers");
                if (userNodeTemp.size() == 0) {
                    List<Element> propertyNode = FlowDefinitionParser.selectElements(document,
                            "/flow/property");
                    Element element = propertyNode.get(0).addElement("viewers");
                    List<Element> emElements = new ArrayList<Element>();
                    emElements.add(element);
                    updateUser(viewerNodes, emElements, oldUserId, newUserId, oldUser, newUser,
                            isInsert, type, logMsg);
                } else {
                    updateUser(viewerNodes, userNodeTemp, oldUserId, newUserId, oldUser, newUser,
                            isInsert, type,
                            logMsg);
                }
            }
            for (Element taskNode : taskNodes) {
                try {
                    // type为1的时候才进行子节点更新，
                    if (taskNode.attributeValue("type").equals("1")) {
                        if (isUpdatetaskUser) {
                            type = 1;
                            // user
                            logMsg.append("更新发起人：");
                            List<Element> userNodes = taskNode.selectNodes(
                                    FlowDefConstants.TASK_USERS_XPATH);
                            List<Element> userNodeTemp = taskNode.selectNodes("users");
                            if (userNodeTemp.size() == 0) {
                                Element element = taskNode.addElement("users");
                                List<Element> emElements = new ArrayList<Element>();
                                emElements.add(element);
                                updateUser(userNodes, emElements, oldUserId, newUserId, oldUser,
                                        newUser, isInsert,
                                        type, logMsg);
                            } else {
                                updateUser(userNodes, userNodeTemp, oldUserId, newUserId, oldUser,
                                        newUser, isInsert,
                                        type, logMsg);
                            }
                        }
                        if (isUpdatecopyUser) {
                            // copyUsers
                            type = 1;
                            logMsg.append("更新抄送人：");
                            List<Element> copyUserNodes = taskNode.selectNodes(
                                    FlowDefConstants.TASK_COPYUSERS_XPATH);
                            List<Element> userNodeTemp = taskNode.selectNodes("copyUsers");
                            if (userNodeTemp.size() == 0) {
                                Element element = taskNode.addElement("copyUsers");
                                List<Element> emElements = new ArrayList<Element>();
                                emElements.add(element);
                                updateUser(copyUserNodes, emElements, oldUserId, newUserId, oldUser,
                                        newUser, isInsert,
                                        type, logMsg);
                            } else {
                                updateUser(copyUserNodes, userNodeTemp, oldUserId, newUserId,
                                        oldUser, newUser,
                                        isInsert, type, logMsg);
                            }
                        }
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);

                }
            }
            flowSchemaLog.setLog(logMsg.toString());
            // System.out.println("------新报文-----------------");
            // System.out.println(document.asXML().toString());
            // System.out.println("------新报文日记-----------------");
            // System.out.println(flowSchemaLog.getLog());
            // System.out.println("------旧报文-----------------");
            // System.out.println(flowSchemaLog.getContentAsString());
            flowSchemaLog.setLog(logMsg.toString());
            flowSchemaLog.setFlowVersion(
                    flowSchemaLogDao.genVersionByParentUuid(flowSchema.getUuid(),
                            FlowDefinitionParser.parseFlow(flowSchema)));
//            Clob content;
//            try {
//                content = new javax.sql.rowset.serial.SerialClob(document.asXML().toCharArray());
//            } catch (Exception e) {
//                logger.error(ExceptionUtils.getStackTrace(e));
//                return "XML转换CLOB出错！";
//            }
            flowSchema.setContent(Hibernate.getLobCreator(flowSchemaLogDao.getSession()).createClob(document.asXML()));
            flowSchemaService.save(flowSchema);
            flowSchemaLogDao.save(flowSchemaLog);
        }
        // 替换完成执行更新操作
        return "替换完成";
    }

    /**
     * @param creatorNodes
     * @param fatherElement
     * @param oldUserId
     * @param newUserId
     * @param oldUser
     * @param newUser
     * @param isInsert
     * @param type
     * @param msg
     */
    @Transactional
    public void updateUser(List<Element> creatorNodes, List<Element> fatherElement,
                           String oldUserId, String newUserId,
                           String oldUser, String newUser, Boolean isInsert, int type,
                           StringBuilder logMsg) {
        // 原人员为空，插入形式直接新增
        String userList[] = newUser.split(";");
        String userId[] = newUserId.split(";");
        if (StringUtils.isEmpty(oldUserId) && isInsert) {
            if (creatorNodes.size() == 0) {
                logMsg.append("新增" + newUser + "人员信息！");
                for (int i = 0; i < userList.length; i++) {
                    Element element = fatherElement.get(0).addElement("unit");
                    element.addAttribute("type", String.valueOf(type));
                    element.addElement("value").setText(userId[i]);
                    element.addElement("argValue").setText(userList[i]);
                }
            } else {
                Boolean isErr = false;
                for (Element unitElement : creatorNodes) {
                    // 如果该人员已经存在就不新增
                    if (newUserId.equals(unitElement.elementText("value"))) {
                        isErr = true;
                    }
                }
                if (!isErr) {
                    logMsg.append("新增" + newUser + "人员信息！");
                    for (int i = 0; i < userList.length; i++) {
                        Element element = fatherElement.get(0).addElement("unit");
                        element.addAttribute("type", String.valueOf(type));
                        element.addElement("value").setText(userId[i]);
                        element.addElement("argValue").setText(userList[i]);
                    }
                }
            }
        }
        // List<UnitElement> newCreates = new ArrayList<UnitElement>();
        // 节点删除
        if (StringUtils.isNotEmpty(oldUserId) && StringUtils.isEmpty(newUserId)) {

            Boolean isErr = false;// 用于判断旧ID是否存在
            for (Element unitElement : creatorNodes) {
                // 找到对应的节点,直接移除
                if (oldUserId.equals(unitElement.elementText("value"))) {
                    isErr = true;
                    logMsg.append("删除" + oldUser + "人员信息！");
                    unitElement.getParent().remove(unitElement);
                }
            }
        }
        // 节点替换
        if (StringUtils.isNotEmpty(oldUserId) && StringUtils.isNotEmpty(newUserId)) {
            Boolean isErr = false;// 用于判断旧ID是否存在
            for (Element unitElement : creatorNodes) {
                // 找到对应的节点,直接修改
                String tempId = "";
                if (oldUserId.equals(unitElement.elementText("value"))) {
                    isErr = true;
                    unitElement.element("argValue").setText(userList[0]);
                    unitElement.element("value").setText(userId[0]);
                    logMsg.append("替换" + oldUser + "人员为" + newUser + "信息！");
                }
            }
            if (isErr) {
                for (int i = 1; i < userList.length; i++) {
                    Element element = fatherElement.get(0).addElement("unit");
                    element.addAttribute("type", String.valueOf(type));
                    element.addElement("value").setText(userId[i]);
                    element.addElement("argValue").setText(userList[i]);
                }
            }
            // 直接新增
            if (!isErr && isInsert) {
                for (Element unitElement : creatorNodes) {
                    // 如果该人员已经存在就不新增
                    if (newUserId.equals(unitElement.elementText("value"))) {
                        isErr = true;
                    }
                }
                if (!isErr) {
                    logMsg.append("新增" + newUser + "人员信息！");
                    for (int i = 0; i < userList.length; i++) {
                        Element element = fatherElement.get(0).addElement("unit");
                        element.addAttribute("type", String.valueOf(type));
                        element.addElement("value").setText(userId[i]);
                        element.addElement("argValue").setText(userList[i]);
                    }
                }
            }
        }
    }

    @Override
    @Transactional
    public List<FlowElement> loadFlowElementByIds(String uuids) {
        String uuidList[] = uuids.split(",");
        List<FlowElement> flowElements = new ArrayList<FlowElement>();
        for (String uuid : uuidList) {
            FlowSchema flowSchema = flowSchemaService.getOne(uuid);
            FlowElement flowElement = FlowDefinitionParser.parseFlow(flowSchema);
            flowElements.add(flowElement);
        }
        return flowElements;
    }

    @Override
    @Transactional
    public QueryData getUpdateContentLog(JqGridQueryInfo jqGridQueryInfo, QueryInfo queryInfo,
                                         String uuid,
                                         String webContent) {
        List<FlowSchemaLog> flowSchemaLogs = flowSchemaLogDao.getByParentUuid(uuid);
        if (flowSchemaLogs.size() == 0) {
            FlowDefinition flowDefinition = flowDefinitionService.getOne(uuid);
            flowSchemaLogs = flowSchemaLogDao.getByParentUuid(flowDefinition.getFlowSchemaUuid());
        }
        List<JqTreeGridNode> retResults = new ArrayList<JqTreeGridNode>();
        if (flowSchemaLogs.size() > 0) {
            for (FlowSchemaLog flowSchemaLog : flowSchemaLogs) {
                JqTreeGridNode node = new JqTreeGridNode();
                node.setId(flowSchemaLog.getUuid());// id
                List<Object> cell = node.getCell();
                cell.add(flowSchemaLog.getUuid());
                cell.add(
                        "<img text='比较XML'  onclick=\"showDiffDialog('" + flowSchemaLog.getUuid() + "')\" src='"
                                + webContent + "/pt/workflow/images/search.png' /> ");
                cell.add(DateUtils.formatDateTime(flowSchemaLog.getCreateTime()));
                cell.add(flowSchemaLog.getFlowVersion());
                cell.add(flowSchemaLog.getLog());
                retResults.add(node);
            }
        }
        QueryData queryData = new QueryData();
        queryData.setDataList(retResults);
        queryData.setPagingInfo(queryInfo.getPagingInfo());
        return queryData;
    }

    @Override
    @Transactional
    public FlowSchema getFlowSchemaByUuid(String uuid) {
        // TODO Auto-generated method stub
        return flowSchemaService.getOne(uuid);
    }

    @Override
    @Transactional
    public FlowSchemaLog getFlowSchemaLogByUuid(String uuid) {
        // TODO Auto-generated method stub
        return flowSchemaLogDao.getOne(uuid);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, String> compareXml(String flowSchemaLogUuid) {
        Map<String, String> result = Maps.newHashMap();
        FlowSchemaLog flowSchemaLog = this.getFlowSchemaLogByUuid(
                flowSchemaLogUuid);
        FlowSchema flowSchema = this.getFlowSchemaByUuid(
                flowSchemaLog.getParentFlowSchemaUUID());
        String logXml = flowSchemaLog.getContentAsString();
        String flowXml = flowSchema.getContentAsString();
        StringReader logStr = new StringReader(logXml);
        StringReader xmlStr = new StringReader(flowXml);
        try {
            Diff diff = new Diff(logStr, xmlStr);
            DetailedDiff myDiff = new DetailedDiff(diff);
            List<Difference> allDifferences = myDiff.getAllDifferences();
            StringBuilder sb = new StringBuilder();
            int index = 0;
            for (Difference difference : allDifferences) {
                index++;
                sb.append("第" + index + "个不同点:");
                sb.append("<br/>");
                NodeDetail controlNode = difference.getControlNodeDetail();
                NodeDetail testNode = difference.getTestNodeDetail();
                String controlPath = controlNode.getXpathLocation();
                String testPath = testNode.getXpathLocation();
                if (StringUtils.isBlank(controlPath)) {
                    sb.append("&nbsp;&nbsp;&nbsp;&nbsp;日记中: 不存在结点信息");
                } else {
                    sb.append(
                            "&nbsp;&nbsp;&nbsp;&nbsp;日记中: 路径" + controlPath + "的值为[" + controlNode.getValue() + "]");
                }
                sb.append("<br/>");
                if (StringUtils.isBlank(testPath)) {
                    sb.append("&nbsp;&nbsp;&nbsp;&nbsp;当前流程中: 不存在结点信息");
                } else {
                    sb.append(
                            "&nbsp;&nbsp;&nbsp;&nbsp;当前流程中: 路径" + testPath + "的值为[" + testNode.getValue() + "]");
                }
                sb.append("<br/>");
            }
            // return "流程XML:" + flowXml + "小版本XML:" + logXml + "差异XML：" + sb.toString();
            result.put("flowXml", flowXml);
            result.put("logXml", logXml);
            result.put("diff", sb.toString());
        } catch (Exception e) {
        }
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, String> getLogDiff(String flowSchemaLogUuid) {
        Map<String, String> result = null;
        FlowSchemaLog flowSchemaLog = this.getFlowSchemaLogByUuid(flowSchemaLogUuid);
        // XML定义
        if (StringUtils.isNotBlank(flowSchemaLog.getContent())) {
            result = compareXml(flowSchemaLogUuid);
            result.put("xmlDefinition", "true");
            return result;
        }

        result = Maps.newHashMap();
        FlowSchema flowSchema = this.getFlowSchemaByUuid(flowSchemaLog.getParentFlowSchemaUUID());
        result.put("flowJson", flowSchema.getDefinitionJsonAsString());
        result.put("logJson", flowSchemaLog.getDefinitionJson());
        result.put("xmlDefinition", "false");
        return result;
    }

    @Override
    public void modify(IdentityReplaceRequest replaceRequest) {
        IdentityReplaceRequest.IdentityReplaceParams params = replaceRequest.getParams();
        List<IdentityReplaceRequest.IdentityReplaceFlowDefinitionUserItem> records = replaceRequest.getRecords();
        if (CollectionUtils.isEmpty(records)) {
            return;
        }

        Map<String, List<IdentityReplaceRequest.IdentityReplaceFlowDefinitionUserItem>> recordsMap = ListUtils.list2group(records, "uuid");
        boolean saveAsNewVersion = StringUtils.equals("newVersion", params.getSaveAs());
        for (Map.Entry<String, List<IdentityReplaceRequest.IdentityReplaceFlowDefinitionUserItem>> entry : recordsMap.entrySet()) {
            String flowDefUuid = entry.getKey();
            List<IdentityReplaceRequest.IdentityReplaceFlowDefinitionUserItem> items = entry.getValue();
            List<String> definitionUserUuids = items.stream().filter(item -> StringUtils.isNotBlank(item.getNodeUuids()))
                    .flatMap(item -> Arrays.stream(StringUtils.split(item.getNodeUuids(), Separator.SEMICOLON.getValue())))
                    .collect(Collectors.toList());
            try {
                List<String> successDefinitionUserUuids = Lists.newArrayList();
                if (CollectionUtils.isNotEmpty(definitionUserUuids)) {
                    successDefinitionUserUuids.addAll(flowSchemeService.modifyUserWithFlowDefinitionUserUuid(flowDefUuid, definitionUserUuids, params, saveAsNewVersion));
                }
                if (CollectionUtils.isNotEmpty(successDefinitionUserUuids)) {
                    items.forEach(item -> {
                        List<String> nodeUuids = Arrays.asList(StringUtils.split(item.getNodeUuids(), Separator.SEMICOLON.getValue()));
                        if (CollectionUtils.containsAny(successDefinitionUserUuids, nodeUuids)) {
                            item.setStatus(IdentityReplaceRequest.IdentityReplaceFlowDefinitionUserItem.SUCCESS);
                        } else {
                            item.setStatus(IdentityReplaceRequest.IdentityReplaceFlowDefinitionUserItem.ERROR);
                            item.setResultMsg("查找内容和修改内容不匹配");
                        }
                    });
                } else {
                    items.forEach(item -> {
                        item.setStatus(IdentityReplaceRequest.IdentityReplaceFlowDefinitionUserItem.ERROR);
                        item.setResultMsg("查找内容和修改内容不匹配");
                    });
                }
            } catch (Exception e) {
                items.forEach(item -> {
                    item.setStatus(IdentityReplaceRequest.IdentityReplaceFlowDefinitionUserItem.ERROR);
                    item.setResultMsg(e.getMessage());
                });
            }
        }

        // 用户操作日志
        log(replaceRequest);
    }

    /**
     * 记录操作日志
     *
     * @param replaceRequest
     */
    private void log(IdentityReplaceRequest replaceRequest) {
        IdentityReplaceRequest.IdentityReplaceParams params = replaceRequest.getParams();
        // 用户操作日志
        UserOperationLog log = new UserOperationLog();
        log.setModuleId(ModuleID.WORKFLOW.getValue());
        log.setModuleName("工作流程");
        if (IdentityReplaceAction.Delete.getValue().equals(params.getModifyMode())) {
            log.setContent("查找办理人\"" + params.getOldUserName() + "\"，" +
                    IdentityReplaceAction.getNameByValue(params.getModifyMode()));
        } else if (IdentityReplaceAction.Add.getValue().equals(params.getModifyMode())) {
            log.setContent("查找办理人\"" + params.getOldUserName() + "\"，" +
                    IdentityReplaceAction.getNameByValue(params.getModifyMode()) + "为\"" + params.getNewUserName() + "\"");
        } else {
            log.setContent("查找办理人\"" + params.getOldUserName() + "\"，" +
                    IdentityReplaceAction.getNameByValue(params.getModifyMode()) + "\"" + params.getNewUserName() + "\"");
        }
        log.setOperation(LogManageOperationEnum.identityReplace.getValue());
        log.setUserName(SpringSecurityUtils.getCurrentUserName());
        log.setDetails(JsonUtils.object2Json(replaceRequest));
        log.setSystemUnitId(SpringSecurityUtils.getCurrentUserUnitId());
        log.setSystem(RequestSystemContextPathResolver.system());
        log.setTenant(SpringSecurityUtils.getCurrentTenantId());
        userOperationLogService.save(log);
    }

    /**
     * @param keyword
     * @param pagingInfo
     * @param orderBy
     * @return
     */
    @Override
    public QueryData listLogs(String keyword, PagingInfo pagingInfo, String orderBy) {
        String system = RequestSystemContextPathResolver.system();
        String hql = "select t.uuid as uuid, t.createTime as  createTime, t.moduleId as moduleId, t.moduleName as moduleName," +
                "t.content as content, t.operation as operation, t.userName as userName from UserOperationLog t " +
                "where t.operation in(:operations) ";
        if (StringUtils.isNotBlank(keyword)) {
            hql += " and t.content like '%' || :keyword || '%' ";
        }
        if (StringUtils.isNotBlank(system)) {
            hql += " and t.system = :system ";
        }
        if (StringUtils.isNotBlank(orderBy)) {
            hql += " order by " + orderBy;
        }
        Map<String, Object> params = Maps.newHashMap();
        params.put("operations", Lists.newArrayList(LogManageOperationEnum.identityReplace.getValue()));
        params.put("keyword", keyword);
        params.put("system", system);
        List<UserOperationLog> logs = userOperationLogService.listByHQLAndPage(hql, params, pagingInfo);

        QueryData queryData = new QueryData();
        queryData.setDataList(logs);
        queryData.setPagingInfo(pagingInfo);
        return queryData;
    }

    /**
     * @param logUuid
     * @return
     */
    @Override
    public UserOperationLog getLogByUuid(String logUuid) {
        return userOperationLogService.getOne(logUuid);
    }
}
