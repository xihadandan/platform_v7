package com.wellsoft.pt.basicdata.printtemplate.service.impl;

import com.deepoove.poi.data.DocxRenderData;
import com.deepoove.poi.data.PictureRenderData;
import com.deepoove.poi.data.PictureType;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.ComThread;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;
import com.spire.doc.Document;
import com.spire.doc.FileFormat;
import com.spire.doc.collections.CommentsCollection;
import com.wellsoft.context.component.jqgrid.JqGridQueryData;
import com.wellsoft.context.component.jqgrid.JqGridQueryInfo;
import com.wellsoft.context.component.jqgrid.JqTreeGridNode;
import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.config.Config;
import com.wellsoft.context.enums.IdPrefix;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.exception.BusinessException;
import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.context.jdbc.support.*;
import com.wellsoft.context.util.date.DateUtil;
import com.wellsoft.context.util.tree.TreeUtils;
import com.wellsoft.pt.app.entity.AppDefElementI18nEntity;
import com.wellsoft.pt.app.service.AppDefElementI18nService;
import com.wellsoft.pt.basicdata.iexport.suport.IexportType;
import com.wellsoft.pt.basicdata.printtemplate.bean.PrintContentsBean;
import com.wellsoft.pt.basicdata.printtemplate.bean.PrintTemplateBean;
import com.wellsoft.pt.basicdata.printtemplate.bean.PrintTemplateTreeBean;
import com.wellsoft.pt.basicdata.printtemplate.dao.PrintTemplateDao;
import com.wellsoft.pt.basicdata.printtemplate.entity.PrintContents;
import com.wellsoft.pt.basicdata.printtemplate.entity.PrintRecord;
import com.wellsoft.pt.basicdata.printtemplate.entity.PrintTemplate;
import com.wellsoft.pt.basicdata.printtemplate.entity.PrintTemplateCategory;
import com.wellsoft.pt.basicdata.printtemplate.service.PrintContentsService;
import com.wellsoft.pt.basicdata.printtemplate.service.PrintRecordService;
import com.wellsoft.pt.basicdata.printtemplate.service.PrintTemplateCategoryService;
import com.wellsoft.pt.basicdata.printtemplate.service.PrintTemplateService;
import com.wellsoft.pt.basicdata.printtemplate.support.PrintTemplateNotAssignedLangException;
import com.wellsoft.pt.basicdata.printtemplate.support.PrintUtils;
import com.wellsoft.pt.basicdata.printtemplate.support.PrinttemplateUtil;
import com.wellsoft.pt.basicdata.printtemplate.support.utils.PoiTlUtils;
import com.wellsoft.pt.basicdata.printtemplate.support.utils.PrinttemplateFreemarkerUtils;
import com.wellsoft.pt.basicdata.printtemplate.support.utils.WordXmlFreeMarkerPreprocessor;
import com.wellsoft.pt.bpm.engine.exception.PrintTemplateNotFoundException;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.multi.org.entity.MultiOrgElement;
import com.wellsoft.pt.multi.org.entity.MultiOrgUserAccount;
import com.wellsoft.pt.multi.org.facade.service.OrgApiFacade;
import com.wellsoft.pt.repository.entity.LogicFileInfo;
import com.wellsoft.pt.repository.entity.mongo.file.MongoFileEntity;
import com.wellsoft.pt.repository.service.MongoFileService;
import com.wellsoft.pt.security.acl.entity.AclSid;
import com.wellsoft.pt.security.acl.service.AclService;
import com.wellsoft.pt.security.core.userdetails.UserDetails;
import com.wellsoft.pt.security.facade.service.SecurityApiFacade;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import freemarker.template.Configuration;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.ReaderInputStream;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.logging.log4j.util.Strings;
import org.hibernate.Hibernate;
import org.hibernate.engine.jdbc.LobCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.persistence.Table;
import java.awt.image.BufferedImage;
import java.beans.PropertyDescriptor;
import java.io.*;
import java.lang.annotation.Annotation;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.sql.Clob;
import java.sql.SQLException;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Description: 打印模板实现类
 *
 * @author zhouyq
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-3-21.1	zhouyq		2013-3-21		Create
 * </pre>
 * @date 2013-3-21
 */
@Service
public class PrintTemplateServiceImpl extends AbstractJpaServiceImpl<PrintTemplate, PrintTemplateDao, String>
        implements PrintTemplateService {

    /**
     * 日期转为中文大小写
     */
    private static final String[] NUMBERS = {"零", "壹", "贰", "叁", "肆", "伍", "陆", "柒", "捌", "玖", "拾"};
    private static final String[] SMALL_NUMBERS = {"〇", "一", "二", "三", "四", "五", "六", "七", "八", "九", "十"};
    private Logger logger = LoggerFactory.getLogger(PrintTemplateServiceImpl.class);
    @Autowired
    private AclService aclService;
    @Autowired
    private OrgApiFacade orgApiFacade;
    @Autowired
    private MongoFileService mongoFileService;
    @Autowired
    private SecurityApiFacade securityApiFacade;
    @Autowired
    private PrintRecordService printRecordService;
    @Autowired
    private PrintContentsService printContentsService;
    @Autowired
    private AppDefElementI18nService appDefElementI18nService;

    // private Dispatch wordObject;
    @Resource
    private PrintTemplateCategoryService printTemplateCategoryService;
    private Configuration configuration = null;

    public static synchronized String toChinese(String str) {
        return toChinese(str, 0);
    }

    public static synchronized String toSmallChinese(String str) {
        return toSmallChinese(str, 0);
    }

    public static synchronized String toChinese(String str, int unit) {
        StringBuilder sb = new StringBuilder();
        sb.append(getSplitDateStr(str, unit, NUMBERS));
        return sb.toString();
    }

    public static synchronized String toSmallChinese(String str, int unit) {
        StringBuilder sb = new StringBuilder();
        sb.append(getSplitDateStr(str, unit, SMALL_NUMBERS));
        return sb.toString();
    }

    public static String getSplitDateStr(String str, int unit, String[] numbers) {
        // unit是单位 0=年 1=月 2日
        String[] DateStr = str.split("-");
        if (unit > DateStr.length)
            unit = 0;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < DateStr[unit].length(); i++) {

            if ((unit == 1 || unit == 2) && Integer.valueOf(DateStr[unit]) > 9) {
                sb.append(convertNum(DateStr[unit].substring(0, 1), numbers)).append(convertNum("10", numbers))
                        .append(convertNum(DateStr[unit].substring(1, 2), numbers));
                break;
            } else {
                sb.append(convertNum(DateStr[unit].substring(i, i + 1), numbers));
            }
        }
        if (unit == 1 || unit == 2) {
            return sb.toString().replaceAll("^" + convertNum("1", numbers), "").replace(convertNum("0", numbers), "");
        }
        return sb.toString();
    }

    private static String convertNum(String str, String[] numbers) {
        return numbers[Integer.valueOf(str)];
    }

    public static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }

    /**
     * 通过uuid查找打印模板
     *
     * @see com.wellsoft.pt.message.service.PrintTemplateService#getById(java.lang.String)
     */

    @Override
    public PrintTemplate getByUuid(String uuid) {
        return dao.getOne(uuid);
    }

    /**
     * 根据uuid查找对应的打印模板bean
     *
     * @see com.wellsoft.pt.message.service.PrintTemplateService#getBeanById(java.lang.String)
     */

    @Override
    public PrintTemplateBean getBeanByUuid(String uuid) {
        PrintTemplate printTemplate = this.dao.getOne(uuid);
        PrintTemplateBean bean = new PrintTemplateBean();
        BeanUtils.copyProperties(printTemplate, bean);
        // category
        String category = printTemplate.getCategory();
        if (StringUtils.isNotBlank(category)) {
            PrintTemplateCategory printTemplateCategory = printTemplateCategoryService.getOne(category);
            if (printTemplateCategory != null) {
                bean.setCategoryName(printTemplateCategory.getName());
            }
        }

        // 取模板
        List<PrintContents> printContents = printContentsService.listByTemplateUuid(printTemplate.getUuid());
        if (printContents != null && printContents.isEmpty() == false) {
            for (PrintContents contents : printContents) {
                PrintContentsBean contentBeans = new PrintContentsBean();
                BeanUtils.copyProperties(contents, contentBeans);
                Clob content = contents.getContent();
                if (content != null) {
                    try {
                        String printContent = IOUtils.toString(content.getCharacterStream());
                        // printContent = URLEncoder.encode(printContent, "UTF-8");
                        contentBeans.setPrintContent(printContent);
                    } catch (IOException ex) {
                        logger.warn(ex.getMessage(), ex);
                    } catch (SQLException ex) {
                        logger.warn(ex.getMessage(), ex);
                    }
                }
                bean.getPrintContents().add(contentBeans);
            }
        }

        List<MongoFileEntity> files = mongoFileService.getFilesFromFolder(uuid, "attach");
        if (files != null && files.size() > 0) {
            bean.setFileName(files.get(0).getFileName());
        }

        // 设置打印模板使用人
        List<AclSid> aclSids = aclService.getSid(printTemplate);
        List<String> sids = new ArrayList<String>();
        for (AclSid sid : aclSids) {
            if (ACL_SID.equals(sid.getSid())) {
                continue;
            }
            sids.add(sid.getSid());
        }
        StringBuilder ownerIds = new StringBuilder();
        StringBuilder ownerNames = new StringBuilder();
        Iterator<String> it = sids.iterator();
        while (it.hasNext()) {
            String sid = it.next();
            if (sid.startsWith(IdPrefix.USER.getValue())) {
                MultiOrgUserAccount user = orgApiFacade.getAccountByUserId(sid);
                ownerIds.append(user.getId());
                ownerNames.append(user.getUserName());
            } else if (sid.startsWith(IdPrefix.ROLE.getValue())) {
                sid = sid.substring(IdPrefix.ROLE.getName().length() + 1);
                MultiOrgElement department = orgApiFacade.getOrgElementById(sid);
                ownerIds.append(department.getId());
                ownerNames.append(department.getName());
            }
            if (it.hasNext()) {
                ownerIds.append(Separator.SEMICOLON.getValue());
                ownerNames.append(Separator.SEMICOLON.getValue());
            }
        }
        bean.setOwnerIds(ownerIds.toString());
        bean.setOwnerNames(ownerNames.toString());
        return bean;
    }

    @Override
    @Transactional
    public PrintTemplate saveBean(PrintTemplateBean bean, Boolean saveNew) throws IOException {
        PrintTemplate printTemplate = new PrintTemplate();
        saveNew = Boolean.TRUE.equals(saveNew);
        // 保存新printTemplate 设置id值
        if (StringUtils.isBlank(bean.getUuid()) || saveNew) {
            if (false == saveNew && idIsExists(bean.getId(), bean.getUuid())) {
                throw new RuntimeException("ID已经存在！");
            }
            bean.setUuid(null);
            // 设置版本
            Double latestVersion = dao.getLatestVersionById(bean.getId());
            if (latestVersion != null) {
                latestVersion += 0.1;
            } else {
                latestVersion = 1d;
            }
            bean.setVersion(latestVersion);
            bean.setSystemUnitId(SpringSecurityUtils.getCurrentUserUnitId());
        } else {
            printTemplate = dao.getOne(bean.getUuid());
        }
        BeanUtils.copyProperties(bean, printTemplate);

        // 设置所有者
        if (StringUtils.isNotBlank(bean.getOwnerIds())) {
            String[] ownerIds = StringUtils.split(bean.getOwnerIds(), Separator.SEMICOLON.getValue());
            printTemplate.setOwners(Arrays.asList(ownerIds));
        }

        this.dao.save(printTemplate);
        if (CollectionUtils.isNotEmpty(bean.getI18ns())) {
            appDefElementI18nService.deleteAllI18n(printTemplate.getId(), printTemplate.getUuid(), null, IexportType.PrintTemplate);
            for (AppDefElementI18nEntity i : bean.getI18ns()) {
                i.setElementId(printTemplate.getId());
                i.setDefId(printTemplate.getUuid());
                i.setApplyTo(IexportType.PrintTemplate);
                i.setVersion(new BigDecimal(1.0));
            }
            appDefElementI18nService.saveAll(bean.getI18ns());
        }
        // 保存模板
        List<PrintContentsBean> printContents = bean.getPrintContents();
        if (printContents != null && printContents.isEmpty() == false) {
            LobCreator lobCreator = Hibernate.getLobCreator(dao.getSession());
            for (PrintContentsBean contentsBean : printContents) {
                PrintContents contentEntity = null;
                if (StringUtils.isBlank(contentsBean.getUuid()) || saveNew) {
                    contentEntity = new PrintContents();
                } else {
                    contentEntity = printContentsService.getOne(contentsBean.getUuid());
                }
                BeanUtils.copyProperties(contentsBean, contentEntity);
                String printContent = contentsBean.getPrintContent();
                if (StringUtils.isBlank(printContent)) {
                    contentEntity.setContent(null);
                } else {
                    // printContent = URLDecoder.decode(printContent, "UTF-8");
                    Clob content = lobCreator.createClob(printContent);
                    contentEntity.setContent(content);
                }
                contentEntity.setPrintTemplate(printTemplate);
                printContentsService.save(contentEntity);
                // 保存附件
                if (StringUtils.isNotBlank(contentEntity.getFileUuid())) {
                    mongoFileService.popAllFilesFromFolder(contentEntity.getUuid());
                    mongoFileService.pushFileToFolder(contentEntity.getUuid(), contentEntity.getFileUuid(), "attach");
                }
            }
        }

        if (StringUtils.isNotBlank(printTemplate.getFileUuid())) {
            mongoFileService.popAllFilesFromFolder(printTemplate.getUuid());
            mongoFileService.pushFileToFolder(printTemplate.getUuid(), printTemplate.getFileUuid(), "attach");
        }

        List<String> printContentsDel = bean.getPrintContentsDel();
        if (saveNew == false && printContentsDel != null && printContentsDel.isEmpty() == false) {
            for (String contentsDelUuid : printContentsDel) {
                if (StringUtils.isBlank(contentsDelUuid)) {
                    continue;
                }
                printContentsService.delete(contentsDelUuid);
            }
        }
        this.saveAcl(printTemplate);
        return printTemplate;

    }

    /**
     * 删除打印模板
     *
     * @see com.wellsoft.pt.message.service.PrintTemplateService#remove(java.lang.String)
     */

    @Override
    @Transactional
    public void remove(String uuid) {
        // 删除内容
        List<PrintContents> printContents = printContentsService.listByTemplateUuid(uuid);
        if (printContents != null && printContents.isEmpty() == false) {
            for (PrintContents entity : printContents) {
                printContentsService.delete(entity);
            }
        }
        this.dao.delete(uuid);
    }

    @Override
    public JqGridQueryData query(JqGridQueryInfo queryInfo) {
        PagingInfo pageData = new PagingInfo(queryInfo.getPage(), queryInfo.getRows(), true);
        List<PrintTemplate> printTemplates = this.dao.listAllByOrderPage(pageData, null);
        List<PrintTemplate> jqUsers = new ArrayList<PrintTemplate>();
        for (PrintTemplate printTemplate : printTemplates) {
            PrintTemplate jqPrintTemplate = new PrintTemplate();
            BeanUtils.copyProperties(printTemplate, jqPrintTemplate);
            jqUsers.add(jqPrintTemplate);
        }
        JqGridQueryData queryData = new JqGridQueryData();
        queryData.setCurrentPage(queryInfo.getPage());
        queryData.setDataList(jqUsers);
        queryData.setRepeatitems(false);
        queryData.setTotalPages(pageData.getTotalPages());
        queryData.setTotalRows(pageData.getTotalCount());
        return queryData;
    }

    @Override
    public QueryData getForPageAsTree(JqGridQueryInfo jqGridQueryInfo, QueryInfo queryInfo) {
        // 设置查询字段条件
        Map<String, Object> values = PropertyFilter.convertToMap(queryInfo.getPropertyFilters());
        // 查询父节点为null的部门
        List<QueryItem> results = null;
        if (StringUtils.isBlank(jqGridQueryInfo.getNodeid())) {
            results = dao.listQueryItemByNameHQLQuery("topPrintTemplateTreeQuery", values, queryInfo.getPagingInfo());
        } else {
            String uuid = jqGridQueryInfo.getNodeid();
            PrintTemplate parent = dao.getOne(uuid);
            values.clear();
            values.put("parentUuid", uuid);
            values.put("id", parent.getId());
            results = dao.listQueryItemByNameHQLQuery("printTemplateTreeQuery", values, queryInfo.getPagingInfo());
        }
        List<JqTreeGridNode> retResults = new ArrayList<JqTreeGridNode>();

        int level = jqGridQueryInfo.getN_level() == null ? 0 : jqGridQueryInfo.getN_level() + 1;
        String parentId = jqGridQueryInfo.getNodeid() == null ? "null" : jqGridQueryInfo.getNodeid();
        for (int index = 0; index < results.size(); index++) {
            QueryItem item = results.get(index);
            JqTreeGridNode node = new JqTreeGridNode();
            node.setId(item.get("id").toString());// id
            List<Object> cell = node.getCell();
            cell.add(item.get("uuid"));// UUID
            cell.add(item.get("name") + " (" + PrintUtils.versionFormat.format(item.get("version")) + ")");// name
            cell.add(item.get("id"));// id
            cell.add(item.get("version"));// version
            cell.add(item.get("code"));// code
            cell.add(item.get("type"));// type
            // level field
            cell.add(level);
            // parent id field
            cell.add(parentId);
            // leaf field
            if (StringUtils.isBlank(jqGridQueryInfo.getNodeid())) {
                if (Double.valueOf(1).equals(item.get("version"))) {
                    cell.add(true);
                } else {
                    cell.add(dao.countById(item.get("id").toString()) <= 1);
                }
            } else {
                cell.add(true);
            }
            // expanded field 第一个节点展开
            if ("null".equals(parentId)) {
                cell.add(true);
            } else {
                cell.add(false);
            }
            retResults.add(node);
        }
        QueryData queryData = new QueryData();
        queryData.setDataList(retResults);
        queryData.setPagingInfo(queryInfo.getPagingInfo());
        return queryData;
    }

    @Override
    @Transactional
    public PrintTemplate saveAcl(PrintTemplate printTemplate) {
        List<AclSid> aclSids = aclService.getSid(printTemplate);
        List<String> existSids = new ArrayList<String>();
        for (AclSid aclSid : aclSids) {
            existSids.add(aclSid.getSid());
        }
        List<String> sids = getAclSid(printTemplate);
        // 新的SID
        List<String> newSids = new ArrayList<String>();
        for (String newSid : sids) {
            if (!existSids.contains(newSid)) {
                newSids.add(newSid);
            }
        }
        // 要删除的SID
        List<String> delSids = new ArrayList<String>();
        for (String newSid : existSids) {
            if (!sids.contains(newSid)) {
                delSids.add(newSid);
            }
        }

        // 删除
        for (String sid : delSids) {
            aclService.removePermission(printTemplate, BasePermission.ADMINISTRATION, sid);
        }
        /*
         * // 新增 if (printTemplate.getParent() != null) { aclService.save(printTemplate,
         * printTemplate.getParent(), sids.get(0), BasePermission.ADMINISTRATION); }
         */
        for (String sid : sids) {
            aclService.addPermission(printTemplate, BasePermission.ADMINISTRATION, sid);
        }
        return aclService.get(PrintTemplate.class, printTemplate.getUuid(), BasePermission.ADMINISTRATION);
    }

    /**
     * 返回打印模板使用者在ACL中的SID
     *
     * @param printTemplate
     * @return
     */

    private List<String> getAclSid(PrintTemplate printTemplate) {
        List<String> newOwners = new ArrayList<String>();
        if (printTemplate.getOwners().isEmpty()) {
            printTemplate.getOwners().add(ACL_SID);
            return printTemplate.getOwners();
        } else {
            List<String> owners = printTemplate.getOwners();
            for (String owner : owners) {
                if (owner.startsWith(IdPrefix.DEPARTMENT.getValue())) {
                    owner = "ROLE_" + owner;
                }
                newOwners.add(owner);
            }
        }
        // 返回组织部门中选择的角色作为SID
        return newOwners;
    }

    /**
     * 判断当前登录用户是否在指定的组织部门中
     *
     * @param printTemplate
     * @param sid
     */

    private Boolean hasPermission(PrintTemplate printTemplate) {
        Boolean hasPermission = false;
        // 获取该打印模板的所有SID，判断是否有访问权限
        List<AclSid> aclSids = aclService.getSid(printTemplate);
        for (AclSid aclSid : aclSids) {
            String sid = aclSid.getSid();
            // 如果所有者是默认的则有权限
            if (sid.equals(ACL_SID)) {
                hasPermission = true;
                break;
            } else {// 由组织部门提供接口，判断当前登录用户是否在指定的SID(组织部门)中
                if (sid.startsWith(IdPrefix.USER.getValue())) {
                    if (StringUtils.equals(((UserDetails) SpringSecurityUtils.getCurrentUser()).getUserId(), sid)) {
                        hasPermission = true;
                        break;
                    }
                } else {
                    hasPermission = false;
                }
            }
        }
        return hasPermission;
    }

    /**
     * 打印模板调用接口,返回文件流（模板ID,单份工作,动态表单集合,输入文件(正文)）
     *
     * @param templateId
     * @param entities
     */
    @Override
    public <ENTITY extends IdEntity> InputStream getPrintTemplateInputStream(String templateId,
                                                                             Collection<ENTITY> entities, Map<String, Object> dytableMap, File textFile) throws Exception {
        return getPrintTemplateInputStream(templateId, null, null, entities, dytableMap, textFile);
    }

    public <ENTITY extends IdEntity> InputStream getPrintTemplateInputStream(String templateId,
                                                                             String printTemplateUuid, String lang, Collection<ENTITY> entities, Map<String, Object> dytableMap,
                                                                             File textFile) throws Exception {
        List<Collection<ENTITY>> allEntities = new ArrayList<Collection<ENTITY>>();
        allEntities.add(entities);
        List<Map<String, Object>> dytableMaps = new ArrayList<Map<String, Object>>();
        dytableMaps.add(dytableMap);
        File finalFile = getPrintTemplateFile(templateId, printTemplateUuid, lang, allEntities, dytableMaps, textFile);
        InputStream fileInStream = new FileInputStream(finalFile);
        return fileInStream;
    }

    /**
     * 打印模板调用接口,返回文件流（模板ID,单份工作,动态表单集合,输入所有文件(正文)）
     *
     * @param templateId
     * @param entities
     */
    @Override
    public <ENTITY extends IdEntity> InputStream getPrintTemplateInputStream(String templateId,
                                                                             Collection<ENTITY> entities, Map<String, Object> dytableMap, Map<String, List<MongoFileEntity>> bodyFiles)
            throws Exception {
        return getPrintTemplateInputStream(templateId, null, null, entities, dytableMap, bodyFiles);
    }

    @Override
    public <ENTITY extends IdEntity> InputStream getPrintTemplateInputStream(String templateId,
                                                                             String printTemplateUuid, String lang, Collection<ENTITY> entities, Map<String, Object> dytableMap,
                                                                             Map<String, List<MongoFileEntity>> bodyFiles) throws Exception {
        List<Collection<ENTITY>> allEntities = new ArrayList<Collection<ENTITY>>();
        allEntities.add(entities);
        List<Map<String, Object>> dytableMaps = new ArrayList<Map<String, Object>>();
        dytableMaps.add(dytableMap);
        File finalFile = getPrintTemplateFile(templateId, printTemplateUuid, lang, allEntities, dytableMaps, bodyFiles);
        InputStream fileInStream = new FileInputStream(finalFile);
        return fileInStream;
    }

    /**
     * 打印模板调用接口,返回文件（模板ID,单份工作，动态表单集合，输入文件(正文)）
     * <p>
     * (non-Javadoc)
     *
     * @throws Exception
     * @see com.wellsoft.pt.basicdata.printtemplate.service.PrintTemplateService#getPrintTemplate(java.lang.String, java.util.Collection, java.io.InputStream)
     */
    @Override
    public <ENTITY extends IdEntity> File getPrintTemplateFile(String templateId, Collection<ENTITY> entities,
                                                               Map<String, Object> dytableMap, File textFile) throws Exception {
        List<Collection<ENTITY>> allEntities = new ArrayList<Collection<ENTITY>>();
        allEntities.add(entities);
        List<Map<String, Object>> dytableMaps = new ArrayList<Map<String, Object>>();
        dytableMaps.add(dytableMap);
        return getPrintTemplateFile(templateId, allEntities, dytableMaps, textFile);
    }

    /**
     * 打印模板调用接口,返回文件流（模板ID,多份工作,动态表单集合,输入文件(正文)）
     *
     * @param templateId
     * @param entities
     */
    @Override
    public <ENTITY extends IdEntity> InputStream getPrintTemplateInputStream(String templateId,
                                                                             Collection<Collection<ENTITY>> allEntities, List<Map<String, Object>> dytableMaps, File textFile)
            throws Exception {
        File finalFile = getPrintTemplateFile(templateId, allEntities, dytableMaps, textFile);
        InputStream fileInStream = new FileInputStream(finalFile);
        return fileInStream;
    }

    @Override
    @Transactional
    public <ENTITY extends IdEntity> File getPrintTemplateFile(String templateId,
                                                               Collection<Collection<ENTITY>> allEntities, List<Map<String, Object>> dytableMaps, File textFile)
            throws Exception {
        return getPrintTemplateFile(templateId, null, null, allEntities, dytableMaps, textFile);
    }

    /**
     * 打印模板调用接口,返回文件（模板ID,多份工作，动态表单集合，输入文件(正文)）
     * <p>
     * (non-Javadoc)
     *
     * @throws Exception
     * @see com.wellsoft.pt.basicdata.printtemplate.service.PrintTemplateService#getPrintTemplate(java.lang.String, java.util.Collection, java.io.InputStream)
     */
    // @Override
    @Transactional
    public <ENTITY extends IdEntity> File getPrintTemplateFile(String templateId, String printTemplateUuid, String lang,
                                                               Collection<Collection<ENTITY>> allEntities, List<Map<String, Object>> dytableMaps, File textFile)
            throws Exception {
        PrintTemplate printTemplate = getPrintTemplate(templateId, printTemplateUuid);// getById(templateId);
        File finalFile = null;
        String finalFileName = PrinttemplateUtil.getFileName();
        // word模板类型
        if (printTemplate.doIsTemplateFileTypeAsWord()) {
            // String uuid = printTemplate.getUuid();
            // 下载该打印模板对应的模板文件
            // List<MongoFileEntity> files = mongoFileService.getFilesFromFolder(uuid,
            // "attach");
            // MongoFileEntity mongoFileEntity = files.get(0);
            InputStream inputStreamOut = getInputStreamByPrintTemplate(printTemplate, lang);// mongoFileEntity.getInputstream();
            // 将文件输出流转为临时文件,使用"./"表示放在eclipseWorkspace\basic下
            String tempFileName = handleFileNameByOs(finalFileName, "-temp", ".doc");
            File tempFile = new File(Config.HOME_DIR + File.separator + tempFileName);
            try {
                FileUtils.copyInputStreamToFile(inputStreamOut, tempFile);
                String origWordText = PrinttemplateUtil.getWordText(tempFile);// 获取临时word文件文本内容
                logger.info("临时文件输出成功！");
                // 判断是否需要多次套打
                if (allEntities.size() == 1) {
                    // 判断是否保存到源文档
                    if (printTemplate.getIsSaveSource()) {
                        for (Collection<ENTITY> entities : allEntities) {
                            // 文件名定义格式
                            String fileNameFormat = printTemplate.getFileNameFormat();
                            fileNameFormat = fileNameFormat.replaceAll("}", "!}");// 防止freemarker模板处理null出错
                            Map<String, Object> templateMap = getMapValue(entities, dytableMaps.get(0));// 获得模板集合

                            String fileName = PrinttemplateFreemarkerUtils.process(printTemplate, fileNameFormat,
                                    templateMap);
                            String outputPath = Config.HOME_DIR + File.separator + fileName + ".doc";// 文件输出路径
                            Map<String, String> replaceMap = new HashMap<String, String>();// 用来存放解析后的键值对
                            String reg = "\\$\\{.*?(?=\\}.*)";// 文本内容中的关键字定义部分正则
                            Pattern pattern = Pattern.compile(reg);
                            Matcher matcher = pattern.matcher(origWordText);
                            String keyWords = "";
                            while (matcher.find()) {
                                // 取得关键字
                                String keyWord = matcher.group() + "!}";
                                keyWords = keyWords + keyWord + ",";
                                logger.info("关键字：" + keyWord);
                                // 模板解析关键字
                                String result = PrinttemplateFreemarkerUtils.process(printTemplate, keyWord,
                                        templateMap);
                                logger.info("关键字替换后：" + result);
                                String newKeyWord = keyWord.replaceAll("!", "");
                                replaceMap.put(newKeyWord, result);
                            }
                            // 读取关键字后保存在数据库中的关键字列
                            String newKeyWords = keyWords.replaceAll("!", "");
                            printTemplate.setKeyWords(newKeyWords);
                            dao.save(printTemplate);

                            // 完成关键字解析后对word进行操作
                            finalFile = exportDocByMutiLine(tempFile.getCanonicalPath(), outputPath, replaceMap,
                                    textFile, printTemplate, origWordText);
                        }
                    } else {
                        // 多个工作数量
                        Integer docNumber = allEntities.size();
                        Integer subScript = 0;
                        // 对多个工作进行遍历
                        for (Collection<ENTITY> entities : allEntities) {
                            Map<String, String> replaceMap = new HashMap<String, String>();// 用来存放解析后的键值对
                            Map<String, Object> templateMap = getMapValue(entities, dytableMaps.get(subScript));// 获得模板集合
                            subScript = subScript + 1;

                            // String text =
                            // PrinttemplateUtil.getWordText(tempFile);//
                            // 获取临时word文件文本内容
                            String reg = "\\$\\{.*?(?=\\}.*)";// 文本内容中的关键字定义部分正则
                            Pattern pattern = Pattern.compile(reg);
                            Matcher matcher = pattern.matcher(origWordText);
                            String keyWords = "";
                            while (matcher.find()) {
                                // 取得关键字
                                String keyWord = matcher.group() + "!}";
                                keyWords = keyWords + keyWord + ",";
                                logger.info("关键字：" + keyWord);
                                // 模板解析关键字
                                String result = PrinttemplateFreemarkerUtils.process(printTemplate, keyWord,
                                        templateMap);
                                logger.info("关键字替换后：" + result);
                                String newKeyWord = keyWord.replaceAll("!", "");
                                replaceMap.put(newKeyWord, result);
                            }
                            // 读取关键字后保存在数据库中的关键字列
                            String newKeyWords = keyWords.replaceAll("!", "");
                            printTemplate.setKeyWords(newKeyWords);
                            dao.save(printTemplate);

                            // 完成关键字解析后对word进行操作
                            exportDoc(tempFile.getCanonicalPath(),
                                    Config.HOME_DIR + File.separator + handleFileNameByOs(finalFileName, "-" + docNumber, ".doc"),
                                    replaceMap, textFile, printTemplate, origWordText);
                            docNumber = docNumber - 1;
                        }

                        // 判断多次套打是分页还是多行
                        if ("paging".equals(printTemplate.getPrintInterval())) {
                            // 合并word文档(分页)
                            finalFile = uniteDocByPage(allEntities.size(), printTemplate, finalFileName);
                        } else /*
                         * if ("multi_line".equals(printTemplate .getPrintInterval()))
                         */ {
                            // 合并word文档(多行)
                            finalFile = uniteDocByMultiLine(allEntities.size(), printTemplate, finalFileName,
                                    origWordText);
                        }
                    }
                } else {
                    // 多个工作数量
                    Integer docNumber = allEntities.size();
                    Integer subScript = 0;
                    // 对多个工作进行遍历
                    for (Collection<ENTITY> entities : allEntities) {
                        Map<String, String> replaceMap = new HashMap<String, String>();// 用来存放解析后的键值对
                        Map<String, Object> templateMap = getMapValue(entities, dytableMaps.get(subScript));// 获得模板集合
                        subScript = subScript + 1;

                        // File tempFile = getTempFile();// 获取下载的临时文件
                        // String text =
                        // PrinttemplateUtil.getWordText(tempFile);//
                        // 获取临时word文件文本内容
                        String reg = "\\$\\{.*?(?=\\}.*)";// 文本内容中的关键字定义部分正则
                        Pattern pattern = Pattern.compile(reg);
                        Matcher matcher = pattern.matcher(origWordText);
                        String keyWords = "";
                        while (matcher.find()) {
                            // 取得关键字
                            String keyWord = matcher.group() + "!}";
                            keyWords = keyWords + keyWord + ",";
                            logger.info("关键字：" + keyWord);
                            // 模板解析关键字
                            String result = PrinttemplateFreemarkerUtils.process(printTemplate, keyWord, templateMap);
                            logger.info("关键字替换后：" + result);
                            String newKeyWord = keyWord.replaceAll("!", "");
                            replaceMap.put(newKeyWord, result);
                        }
                        // 读取关键字后保存在数据库中的关键字列
                        String newKeyWords = keyWords.replaceAll("!", "");
                        printTemplate.setKeyWords(newKeyWords);
                        dao.save(printTemplate);

                        // 完成关键字解析后对word进行操作
                        exportDoc(tempFile.getCanonicalPath(),
                                Config.HOME_DIR + File.separator + handleFileNameByOs(finalFileName, "-" + docNumber, ".doc"), replaceMap,
                                textFile, printTemplate, origWordText);
                        docNumber = docNumber - 1;
                    }

                    // 判断多次套打是分页还是多行
                    if ("paging".equals(printTemplate.getPrintInterval())) {
                        // 合并word文档(分页)
                        finalFile = uniteDocByPage(allEntities.size(), printTemplate, finalFileName);
                    } else /*
                     * if ("multi_line".equals(printTemplate .getPrintInterval()))
                     */ {
                        // 合并word文档(多行)
                        finalFile = uniteDocByMultiLine(allEntities.size(), printTemplate, finalFileName, origWordText);
                    }
                }
            } finally {
                if (tempFile.exists() && tempFile.delete()) {
                    logger.info("临时文件删除成功！");
                }
            }

            // 判断是否保存打印记录
            if (BooleanUtils.isTrue(printTemplate.getIsSavePrintRecord())) {
                for (Collection<ENTITY> entities : allEntities) {
                    PrintRecord printRecord = new PrintRecord();
                    String printObjectUuid = "";
                    String printObjectType = "";
                    for (ENTITY entity : entities) {
                        printObjectUuid = printObjectUuid + entity.getUuid() + ",";
                        printObjectType = printObjectType + entity.getClass().getName() + ",";
                    }
                    // 打印记录历史次数集合
                    List<Integer> oldPrintTimes = new ArrayList<Integer>();
                    List<PrintRecord> oldPrintRecords = printRecordService.queryByPrintObject(printObjectUuid);
                    for (PrintRecord oldPrintRecord : oldPrintRecords) {
                        Integer oldPrintTimes1 = oldPrintRecord.getPrintTimes();
                        oldPrintTimes.add(oldPrintTimes1);
                    }
                    // 取得打印次数的最大值
                    int maxOldPrintTimes = 0;
                    for (int i = 0; i < oldPrintTimes.size(); i++) {
                        if (oldPrintTimes.get(i) > maxOldPrintTimes) {
                            maxOldPrintTimes = oldPrintTimes.get(i);
                        }
                    }
                    // 如果打印记录不为null的话
                    if (oldPrintRecords != null) {
                        printRecord.setPrintTimes(maxOldPrintTimes + 1);
                    } else {
                        printRecord.setPrintTimes(1);
                    }
                    printRecord.setPrintObject(printObjectUuid);
                    printRecord.setPrintObjectType(printObjectType);
                    // 获取调用人
                    String userName = SpringSecurityUtils.getCurrentUserName();
                    logger.info("当前用户名：" + userName);
                    printRecord.setUserName(userName);
                    printRecord.setCode(templateId);
                    printRecordService.save(printRecord);
                }
            }
        } else if (printTemplate.doIsTemplateFileTypeAsHtml() || printTemplate.doIsTemplateFileTypeAsWordXml()
                || printTemplate.doIsTemplateFileTypeAsWordXmlByComment()) {
            // 判断是否需要多次套打
            if (allEntities.size() == 1) {
                // 判断是否保存到源文档
                if (printTemplate.getIsSaveSource()) {
                    for (Collection<ENTITY> entities : allEntities) {
                        // 文件流每次被读取的话都要新下载该文件流
                        // String uuid = printTemplate.getUuid();
                        // 下载该打印模板对应的html模板文件
                        // List<MongoFileEntity> files = mongoFileService.getFilesFromFolder(uuid,
                        // "attach");
                        // MongoFileEntity mongoFileEntity = files.get(0);
                        InputStream inputStreamOut = getInputStreamByPrintTemplate(printTemplate, lang);// mongoFileEntity.getInputstream();
                        // 文件名定义格式
                        String fileNameFormat = printTemplate.getFileNameFormat();
                        fileNameFormat = fileNameFormat.replaceAll("}", "!}");// 防止freemarker模板处理null出错
                        Map<String, Object> templateMap = getMapValue(entities, dytableMaps.get(0));// 获得模板集合

                        String fileName = PrinttemplateFreemarkerUtils.process(printTemplate, fileNameFormat,
                                templateMap);
                        String outputPath = Config.HOME_DIR + File.separator + fileName;// 文件输出路径
                        if (printTemplate.doIsTemplateFileTypeAsWordXml()
                                || printTemplate.doIsTemplateFileTypeAsWordXmlByComment()) {
                            outputPath = outputPath + ".doc";
                        } else if (printTemplate.doIsTemplateFileTypeAsHtml()) {
                            outputPath = outputPath + ".html";
                        }

                        // 读取html模板流并替换为freemarker模板数据内容
                        String beforeReplaceXml = IOUtils.toString(inputStreamOut);
                        if (printTemplate.doIsTemplateFileTypeAsWordXml()
                                || printTemplate.doIsTemplateFileTypeAsWordXmlByComment()) {
                            if (printTemplate.doIsTemplateFileTypeAsWordXmlByComment()) {
                                try {
                                    beforeReplaceXml = WordXmlFreeMarkerPreprocessor.process(beforeReplaceXml);
                                } catch (Exception e) {
                                    logger.error("word xml 预处理异常: " + e.getMessage(), e);
                                    throw new RuntimeException("word xml 预处理异常: " + e.getMessage(), e);
                                }

                            }
                            beforeReplaceXml = beforeReplaceXml.replaceAll("<\\!\\-\\-//", "");
                            beforeReplaceXml = beforeReplaceXml.replaceAll("//\\-\\->", "");
                        }
                        String afterReplaceHtml = PrinttemplateFreemarkerUtils.process(printTemplate, beforeReplaceXml,
                                templateMap);

                        InputStream afterReplaceInputStream = new ByteArrayInputStream(
                                afterReplaceHtml.getBytes("UTF-8"));
                        FileUtils.copyInputStreamToFile(afterReplaceInputStream, new File(outputPath));
                        finalFile = new File(outputPath);
                    }
                } else {
                    // 多个工作数量
                    Integer docNumber = allEntities.size();
                    Integer subScript = 0;
                    // 对多个工作进行遍历
                    for (Collection<ENTITY> entities : allEntities) {
                        // 文件流每次被读取的话都要新下载该文件流
                        // String uuid = printTemplate.getUuid();
                        // 下载该打印模板对应的html模板文件
                        // List<MongoFileEntity> files = mongoFileService.getFilesFromFolder(uuid,
                        // "attach");
                        // MongoFileEntity mongoFileEntity = files.get(0);
                        InputStream inputStreamOut = getInputStreamByPrintTemplate(printTemplate, lang);// mongoFileEntity.getInputstream();
                        Map<String, Object> templateMap = getMapValue(entities, dytableMaps.get(subScript));// 获得模板集合
                        subScript = subScript + 1;

                        // 读取html模板流并替换为freemarker模板数据内容
                        String beforeReplaceXml = IOUtils.toString(inputStreamOut);
                        if (printTemplate.doIsTemplateFileTypeAsWordXml()
                                || printTemplate.doIsTemplateFileTypeAsWordXmlByComment()) {
                            if (printTemplate.doIsTemplateFileTypeAsWordXmlByComment()) {
                                try {
                                    beforeReplaceXml = WordXmlFreeMarkerPreprocessor.process(beforeReplaceXml);
                                } catch (Exception e) {
                                    logger.error("word xml 预处理异常: " + e.getMessage(), e);
                                    throw new RuntimeException("word xml 预处理异常: " + e.getMessage(), e);
                                }

                            }

                            beforeReplaceXml = beforeReplaceXml.replaceAll("<\\!\\-\\-//", "");
                            beforeReplaceXml = beforeReplaceXml.replaceAll("//\\-\\->", "");
                        }
                        String afterReplaceHtml = PrinttemplateFreemarkerUtils.process(printTemplate, beforeReplaceXml,
                                templateMap);

                        InputStream afterReplaceInputStream = new ByteArrayInputStream(
                                afterReplaceHtml.getBytes("UTF-8"));//
                        String outputPath = Strings.EMPTY;// 文件输出路径
                        if (printTemplate.doIsTemplateFileTypeAsWordXml()
                                || printTemplate.doIsTemplateFileTypeAsWordXmlByComment()) {
                            outputPath = Config.HOME_DIR + File.separator + handleFileNameByOs(finalFileName, Strings.EMPTY, ".doc");
                        } else if (printTemplate.doIsTemplateFileTypeAsHtml()) {
                            outputPath = Config.HOME_DIR + File.separator + handleFileNameByOs(finalFileName, Strings.EMPTY, ".html");
                        }
                        FileUtils.copyInputStreamToFile(afterReplaceInputStream, new File(outputPath));
                        finalFile = new File(outputPath);
                        docNumber = docNumber - 1;
                    }
                }
            } else {
                // 多个工作数量
                Integer docNumber = allEntities.size();
                Integer subScript = 0;
                // 对多个工作进行遍历
                for (Collection<ENTITY> entities : allEntities) {
                    // 文件流每次被读取的话都要新下载该文件流
                    // String uuid = printTemplate.getUuid();
                    // 下载该打印模板对应的html模板文件
                    // List<MongoFileEntity> files = mongoFileService.getFilesFromFolder(uuid,
                    // "attach");
                    // MongoFileEntity mongoFileEntity = files.get(0);
                    InputStream inputStreamOut = getInputStreamByPrintTemplate(printTemplate, lang);// mongoFileEntity.getInputstream();
                    Map<String, Object> templateMap = getMapValue(entities, dytableMaps.get(subScript));// 获得模板集合
                    subScript = subScript + 1;

                    // 读取html模板流并替换为freemarker模板数据内容
                    String beforeReplaceXml = IOUtils.toString(inputStreamOut);
                    if (printTemplate.doIsTemplateFileTypeAsWordXml()
                            || printTemplate.doIsTemplateFileTypeAsWordXmlByComment()) {
                        if (printTemplate.doIsTemplateFileTypeAsWordXmlByComment()) {
                            try {
                                beforeReplaceXml = WordXmlFreeMarkerPreprocessor.process(beforeReplaceXml);
                            } catch (Exception e) {
                                logger.error("word xml 预处理异常: " + e.getMessage(), e);
                                throw new RuntimeException("word xml 预处理异常: " + e.getMessage(), e);
                            }

                        }
                        beforeReplaceXml = beforeReplaceXml.replaceAll("<\\!\\-\\-//", "");
                        beforeReplaceXml = beforeReplaceXml.replaceAll("//\\-\\->", "");
                    }
                    String afterReplaceHtml = PrinttemplateFreemarkerUtils.process(printTemplate, beforeReplaceXml,
                            templateMap);

                    InputStream afterReplaceInputStream = new ByteArrayInputStream(afterReplaceHtml.getBytes("UTF-8"));// html模板替换后的文件流
                    String outputPath = Strings.EMPTY;// 文件输出路径
                    if (printTemplate.doIsTemplateFileTypeAsWordXml()
                            || printTemplate.doIsTemplateFileTypeAsWordXmlByComment()) {
                        outputPath = Config.HOME_DIR + File.separator + handleFileNameByOs(finalFileName, Strings.EMPTY, ".doc");
                    } else if (printTemplate.doIsTemplateFileTypeAsHtml()) {
                        outputPath = Config.HOME_DIR + File.separator + handleFileNameByOs(finalFileName, Strings.EMPTY, ".html");
                    }
                    FileUtils.copyInputStreamToFile(afterReplaceInputStream, new File(outputPath));
                    finalFile = new File(outputPath);

                    docNumber = docNumber - 1;
                }
            }

            // 判断是否保存打印记录
            if (BooleanUtils.isTrue(printTemplate.getIsSavePrintRecord())) {
                for (Collection<ENTITY> entities : allEntities) {
                    PrintRecord printRecord = new PrintRecord();
                    String printObjectUuid = "";
                    String printObjectType = "";
                    for (ENTITY entity : entities) {
                        printObjectUuid = printObjectUuid + entity.getUuid() + ",";
                        printObjectType = printObjectType + entity.getClass().getName() + ",";
                    }
                    // 打印记录历史次数集合
                    List<Integer> oldPrintTimes = new ArrayList<Integer>();
                    List<PrintRecord> oldPrintRecords = printRecordService.queryByPrintObject(printObjectUuid);
                    for (PrintRecord oldPrintRecord : oldPrintRecords) {
                        Integer oldPrintTimes1 = oldPrintRecord.getPrintTimes();
                        oldPrintTimes.add(oldPrintTimes1);
                    }
                    // 取得打印次数的最大值
                    int maxOldPrintTimes = 0;
                    for (int i = 0; i < oldPrintTimes.size(); i++) {
                        if (oldPrintTimes.get(i) > maxOldPrintTimes) {
                            maxOldPrintTimes = oldPrintTimes.get(i);
                        }
                    }
                    // 如果打印记录不为null的话
                    if (oldPrintRecords != null) {
                        printRecord.setPrintTimes(maxOldPrintTimes + 1);
                    } else {
                        printRecord.setPrintTimes(1);
                    }
                    printRecord.setPrintObject(printObjectUuid);
                    printRecord.setPrintObjectType(printObjectType);
                    // 获取调用人
                    String userName = SpringSecurityUtils.getCurrentUserName();
                    logger.info("当前用户名：" + userName);
                    printRecord.setUserName(userName);
                    printRecord.setCode(templateId);
                    printRecordService.save(printRecord);
                }
            }
        } else if (printTemplate.doIsTemplateFileTypeAsWordPoi()) {
            finalFile = getPrintTemplateFileByPoi(lang, allEntities, dytableMaps, printTemplate, finalFileName, null,
                    textFile);
        }
        return finalFile;
    }

    private <ENTITY extends IdEntity> File getPrintTemplateFileByPoi(String lang,
                                                                     Collection<Collection<ENTITY>> allEntities, List<Map<String, Object>> dytableMaps,
                                                                     PrintTemplate printTemplate, String finalFileName, Map<String, List<MongoFileEntity>> bodyFiles,
                                                                     File textFile) throws Exception {
        File finalFile = null;
        List<File> tempFiles = new ArrayList<>();

        // poi解析
        try {
            if (allEntities.size() == 1) {
                // 判断是否保存到源文档
                // if (printTemplate.getIsSaveSource()) {
                for (Collection<ENTITY> entities : allEntities) {
                    String fileNameFormat = printTemplate.getFileNameFormat();
                    if (fileNameFormat == null) {
                        fileNameFormat = printTemplate.getName();
                    }
                    fileNameFormat = fileNameFormat.replaceAll("}", "!}");// 防止freemarker模板处理null出错
                    Map<String, Object> templateMap = getMapValue(entities, dytableMaps.get(0));// 获得模板集合
                    String fileName = PrinttemplateFreemarkerUtils.process(printTemplate, fileNameFormat, templateMap);
                    if (textFile != null) {
                        if (StringUtils.endsWith(textFile.getName(), ".docx")
                                || StringUtils.endsWith(textFile.getName(), ".doc")) {
                            String tempOfficePath = Config.HOME_DIR + File.separator + new Date().getTime() + textFile.getName();
                            Document doc = new Document(new FileInputStream(textFile));
                            doc.acceptChanges();
                            CommentsCollection comments = doc.getComments();
                            for (int commentInt = 0; commentInt < comments.getCount(); commentInt++) {
                                comments.removeAt(commentInt);
                            }
                            doc.saveToFile(tempOfficePath, FileFormat.Docx_2010);
                            File tempOfficeFile = new File(tempOfficePath);
                            tempFiles.add(tempOfficeFile);
                            templateMap.put("bodyFile", new DocxRenderData(tempOfficeFile));
                        } else {
                            templateMap.put("bodyFile", new DocxRenderData(textFile));
                        }

                    }
                    if (bodyFiles != null) {
                        for (String key : bodyFiles.keySet()) {
                            List<MongoFileEntity> mongoFileEntities = bodyFiles.get(key);
                            if (CollectionUtils.isNotEmpty(mongoFileEntities)) {

                                Integer isImage = null; // null 初始值；-1 无图片文件；0 存在图片和非图片；1 都为图片文件
                                List<PictureRenderData> pictureRenderDataList = new ArrayList<>();
                                for (int i = 0; i < mongoFileEntities.size(); i++) {
                                    MongoFileEntity mongoFileEntity = mongoFileEntities.get(i);
                                    String fName = mongoFileEntity.getFileName();
                                    File tempImageFile = null;
                                    try {
                                        // 是否是支持套打图片的类型（是否是图片）
                                        PictureType.suggestFileType(fName);
                                        String imagePath = Config.APP_DATA_DIR + File.separator + new Date().getTime() + fName;
                                        tempImageFile = new File(imagePath);
                                        FileUtils.copyInputStreamToFile(mongoFileEntity.getInputstream(), tempImageFile);
                                        tempFiles.add(tempImageFile);
                                        BufferedImage image = ImageIO.read(tempImageFile);
//										if(image == null){
//											throw new RenderException("Unsupported picture: " + fName + ". Expected emf|wmf|pict|jpeg|png|dib|gif|tiff|eps|bmp|wpg");
//										}
                                        if (image != null) {
                                            pictureRenderDataList.add(new PictureRenderData(image.getWidth(), image.getHeight(), tempImageFile));
                                        }

                                        if (isImage == null) {
                                            isImage = 1;
                                        } else if (isImage == -1) {
                                            isImage = 0;
                                        }

                                    } catch (IllegalArgumentException e) {
                                        // 非图片文件
                                        if (isImage == null) {
                                            isImage = -1;
                                        } else if (isImage == 1) {
                                            isImage = 0;
                                        }
                                    }

                                    if (i == mongoFileEntities.size() - 1) {
                                        if (isImage == null || isImage == -1) {
                                            if (StringUtils.endsWith(mongoFileEntity.getFileName(), ".docx")
                                                    || StringUtils.endsWith(mongoFileEntity.getFileName(), ".doc")) {
                                                String tempOfficePath = Config.HOME_DIR + File.separator + new Date().getTime() + mongoFileEntity.getFileName();
                                                Document doc = new Document(mongoFileEntity.getInputstream());
                                                doc.acceptChanges();
                                                CommentsCollection comments = doc.getComments();
                                                for (int commentInt = 0; commentInt < comments.getCount(); commentInt++) {
                                                    comments.removeAt(commentInt);
                                                }
                                                doc.saveToFile(tempOfficePath, FileFormat.Docx_2010);
                                                File tempOfficeFile = new File(tempOfficePath);
                                                tempFiles.add(tempOfficeFile);
                                                templateMap.put("bodyFile_" + key, new DocxRenderData(tempOfficeFile));
                                            } else {
                                                templateMap.put("bodyFile_" + key, new DocxRenderData(mongoFileEntity.getInputstream()));
                                            }
                                        } else if (isImage == 1) {
                                            templateMap.put("bodyFile_" + key, pictureRenderDataList);
                                        }/*  else if(isImage == 0) {
                                            templateMap.put("bodyFile_" + key, new DocxRenderData(tempOfficeFile));
                                        }*/
                                    }
                                }

                            }
                        }
                    }

                    String outputPath = Config.HOME_DIR + File.separator + handleFileNameByOs(fileName, Strings.EMPTY, ".docx");// 文件输出路径
                    InputStream printTemplateInputStream = getInputStreamByPrintTemplate(printTemplate, lang);
                    PoiTlUtils.renderDocxFile(printTemplateInputStream, templateMap, new File(outputPath));
                    finalFile = new File(outputPath);
                }

            } else {
                int docNumber = allEntities.size();
                int subScript = 0;

                List<File> mergeFiles = new ArrayList<>();

                // 多个工作数量
                for (Collection<ENTITY> entities : allEntities) {
                    String fileNameFormat = printTemplate.getFileNameFormat();
                    // fileNameFormat = fileNameFormat.replaceAll("}", "!}");//
                    // 防止freemarker模板处理null出错
                    Map<String, Object> templateMap = getMapValue(entities, dytableMaps.get(subScript));// 获得模板集合
                    // String fileName = PrinttemplateFreemarkerUtils.process(printTemplate,
                    // fileNameFormat, templateMap);
                    String outputPath = Config.HOME_DIR + File.separator + handleFileNameByOs(finalFileName, docNumber + Strings.EMPTY, ".docx");// 文件输出路径
                    InputStream printTemplateInputStream = getInputStreamByPrintTemplate(printTemplate, lang);
                    PoiTlUtils.renderDocxFile(printTemplateInputStream, templateMap, new File(outputPath));
                    mergeFiles.add(new File(outputPath));
                    docNumber--;
                    subScript++;
                }
                finalFile = PoiTlUtils.mergeDocx(mergeFiles, "paging".equals(printTemplate.getPrintInterval()));
                PoiTlUtils.deleteFiles(mergeFiles);
            }
        } catch (Exception e) {
            if (e instanceof BusinessException) {
                throw (BusinessException) e;
            }
        } finally {
            PoiTlUtils.deleteFiles(tempFiles);
            // if (tempFile.exists() && tempFile.delete()) {
            // logger.info("临时文件删除成功！");
            // }
        }

        // 判断是否保存打印记录
        if (BooleanUtils.isTrue(printTemplate.getIsSavePrintRecord())) {
            for (Collection<ENTITY> entities : allEntities) {
                PrintRecord printRecord = new PrintRecord();
                String printObjectUuid = "";
                String printObjectType = "";
                for (ENTITY entity : entities) {
                    printObjectUuid = printObjectUuid + entity.getUuid() + ",";
                    printObjectType = printObjectType + entity.getClass().getName() + ",";
                }
                // 打印记录历史次数集合
                List<Integer> oldPrintTimes = new ArrayList<Integer>();
                List<PrintRecord> oldPrintRecords = printRecordService.queryByPrintObject(printObjectUuid);
                for (PrintRecord oldPrintRecord : oldPrintRecords) {
                    Integer oldPrintTimes1 = oldPrintRecord.getPrintTimes();
                    oldPrintTimes.add(oldPrintTimes1);
                }
                // 取得打印次数的最大值
                int maxOldPrintTimes = 0;
                for (int i = 0; i < oldPrintTimes.size(); i++) {
                    if (oldPrintTimes.get(i) > maxOldPrintTimes) {
                        maxOldPrintTimes = oldPrintTimes.get(i);
                    }
                }
                // 如果打印记录不为null的话
                if (oldPrintRecords != null) {
                    printRecord.setPrintTimes(maxOldPrintTimes + 1);
                } else {
                    printRecord.setPrintTimes(1);
                }
                printRecord.setPrintObject(printObjectUuid);
                printRecord.setPrintObjectType(printObjectType);
                // 获取调用人
                String userName = SpringSecurityUtils.getCurrentUserName();
                logger.info("当前用户名：" + userName);
                printRecord.setUserName(userName);
                printRecord.setCode(printTemplate.getId());
                printRecordService.save(printRecord);
            }
        }
        return finalFile;
    }

    /**
     * 打印模板调用接口,返回文件（模板ID,多份工作，动态表单集合，输入所有文件(正文)）
     * <p>
     * (non-Javadoc)
     *
     * @throws Exception
     * @see com.wellsoft.pt.basicdata.printtemplate.service.PrintTemplateService#getPrintTemplate(java.lang.String, java.util.Collection, java.io.InputStream)
     */
    @Override
    @Transactional
    public <ENTITY extends IdEntity> File getPrintTemplateFile(String templateId,
                                                               Collection<Collection<ENTITY>> allEntities, List<Map<String, Object>> dytableMaps,
                                                               Map<String, List<MongoFileEntity>> bodyFiles) throws Exception {
        return getPrintTemplateFile(templateId, null, null, allEntities, dytableMaps, bodyFiles);
    }

    @Transactional
    public <ENTITY extends IdEntity> File getPrintTemplateFile(String templateId, String printTemplateUuid, String lang,
                                                               Collection<Collection<ENTITY>> allEntities, List<Map<String, Object>> dytableMaps,
                                                               Map<String, List<MongoFileEntity>> bodyFiles) throws Exception {
        // 先下载打印模板
        PrintTemplate printTemplate = getPrintTemplate(templateId, printTemplateUuid);// getById(templateId);
        File finalFile = null;// 要返回的file
        String finalFileName = PrinttemplateUtil.getFileName();
        // word模板类型
        if (printTemplate.doIsTemplateFileTypeAsWord()) {
            // String uuid = printTemplate.getUuid();
            // 下载该打印模板对应的模板文件
            // List<MongoFileEntity> files = mongoFileService.getFilesFromFolder(uuid,
            // "attach");
            // MongoFileEntity mongoFileEntity = files.get(0);
            InputStream inputStreamOut = getInputStreamByPrintTemplate(printTemplate, lang);// mongoFileEntity.getInputstream();
            // 将文件输出流转为临时文件,使用"./"表示放在eclipseWorkspace\basic下
            String tempFileName = handleFileNameByOs(finalFileName, "-temp", ".doc");
            File tempFile = new File(Config.HOME_DIR + File.separator + tempFileName);
            try {
                FileUtils.copyInputStreamToFile(inputStreamOut, tempFile);
                logger.info("临时文件输出成功！");
                String origWordText = PrinttemplateUtil.getWordText(tempFile);// 获取临时word文件文本内容

                // 判断是否需要多次套打
                if (allEntities.size() == 1) {
                    // 判断是否保存到源文档
                    if (printTemplate.getIsSaveSource()) {
                        for (Collection<ENTITY> entities : allEntities) {
                            // 文件名定义格式
                            String fileNameFormat = printTemplate.getFileNameFormat();
                            fileNameFormat = fileNameFormat.replaceAll("}", "!}");// 防止freemarker模板处理null出错
                            Map<String, Object> templateMap = getMapValue(entities, dytableMaps.get(0));// 获得模板集合

                            String fileName = PrinttemplateFreemarkerUtils.process(printTemplate, fileNameFormat,
                                    templateMap);
                            String outputPath = Config.HOME_DIR + File.separator + fileName + ".doc";// 文件输出路径
                            Map<String, String> replaceMap = new HashMap<String, String>();// 用来存放解析后的键值对
                            String reg = "\\$\\{.*?(?=\\}.*)";// 文本内容中的关键字定义部分正则
                            Pattern pattern = Pattern.compile(reg);
                            Matcher matcher = pattern.matcher(origWordText);
                            String keyWords = "";
                            while (matcher.find()) {
                                // 取得关键字
                                String keyWord = matcher.group() + "!}";
                                keyWords = keyWords + keyWord + ",";
                                logger.info("关键字：" + keyWord);
                                // 模板解析关键字
                                String result = PrinttemplateFreemarkerUtils.process(printTemplate, keyWord,
                                        templateMap);
                                logger.info("关键字替换后：" + result);
                                String newKeyWord = keyWord.replaceAll("!", "");
                                replaceMap.put(newKeyWord, result);
                            }
                            // 读取关键字后保存在数据库中的关键字列
                            String newKeyWords = keyWords.replaceAll("!", "");
                            printTemplate.setKeyWords(newKeyWords);
                            dao.save(printTemplate);

                            // 完成关键字解析后对word进行操作
                            finalFile = exportDocByMutiLine(tempFile.getCanonicalPath(), outputPath, replaceMap,
                                    bodyFiles, printTemplate, origWordText);
                        }
                    } else {
                        // 多个工作数量
                        Integer docNumber = allEntities.size();
                        Integer subScript = 0;
                        // 对多个工作进行遍历
                        for (Collection<ENTITY> entities : allEntities) {
                            Map<String, String> replaceMap = new HashMap<String, String>();// 用来存放解析后的键值对
                            Map<String, Object> templateMap = getMapValue(entities, dytableMaps.get(subScript));// 获得模板集合
                            subScript = subScript + 1;

                            // File tempFile = getTempFile();// 获取下载的临时文件
                            // String text =
                            // PrinttemplateUtil.getWordText(tempFile);//
                            // 获取临时word文件文本内容
                            String reg = "\\$\\{.*?(?=\\}.*)";// 文本内容中的关键字定义部分正则
                            Pattern pattern = Pattern.compile(reg);
                            Matcher matcher = pattern.matcher(origWordText);
                            String keyWords = "";
                            while (matcher.find()) {
                                // 取得关键字
                                String keyWord = matcher.group() + "!}";
                                keyWords = keyWords + keyWord + ",";
                                logger.info("关键字：" + keyWord);
                                // 模板解析关键字
                                String result = PrinttemplateFreemarkerUtils.process(printTemplate, keyWord,
                                        templateMap);
                                logger.info("关键字替换后：" + result);
                                String newKeyWord = keyWord.replaceAll("!", "");
                                replaceMap.put(newKeyWord, result);
                            }
                            // 读取关键字后保存在数据库中的关键字列
                            String newKeyWords = keyWords.replaceAll("!", "");
                            printTemplate.setKeyWords(newKeyWords);
                            dao.save(printTemplate);

                            // 完成关键字解析后对word进行操作
                            exportDoc(tempFile.getCanonicalPath(),
                                    Config.HOME_DIR + File.separator + handleFileNameByOs(finalFileName, "-" + docNumber, ".doc"),
                                    replaceMap, bodyFiles, printTemplate, origWordText);
                            docNumber = docNumber - 1;
                        }

                        // 判断多次套打是分页还是多行
                        if ("paging".equals(printTemplate.getPrintInterval())) {
                            // 合并word文档(分页)
                            finalFile = uniteDocByPage(allEntities.size(), printTemplate, finalFileName);
                        } else /*
                         * if ("multi_line".equals(printTemplate .getPrintInterval()))
                         */ {
                            // 合并word文档(多行)
                            finalFile = uniteDocByMultiLine(allEntities.size(), printTemplate, finalFileName,
                                    origWordText);
                        }
                    }
                } else {
                    // 多个工作数量
                    Integer docNumber = allEntities.size();
                    Integer subScript = 0;
                    // 对多个工作进行遍历
                    for (Collection<ENTITY> entities : allEntities) {
                        Map<String, String> replaceMap = new HashMap<String, String>();// 用来存放解析后的键值对
                        Map<String, Object> templateMap = getMapValue(entities, dytableMaps.get(subScript));// 获得模板集合
                        subScript = subScript + 1;

                        // File tempFile = getTempFile();// 获取下载的临时文件
                        // String text =
                        // PrinttemplateUtil.getWordText(tempFile);//
                        // 获取临时word文件文本内容
                        String reg = "\\$\\{.*?(?=\\}.*)";// 文本内容中的关键字定义部分正则
                        Pattern pattern = Pattern.compile(reg);
                        Matcher matcher = pattern.matcher(origWordText);
                        String keyWords = "";
                        while (matcher.find()) {
                            // 取得关键字
                            String keyWord = matcher.group() + "!}";
                            keyWords = keyWords + keyWord + ",";
                            logger.info("关键字：" + keyWord);
                            // 模板解析关键字
                            String result = PrinttemplateFreemarkerUtils.process(printTemplate, keyWord, templateMap);
                            logger.info("关键字替换后：" + result);
                            String newKeyWord = keyWord.replaceAll("!", "");
                            replaceMap.put(newKeyWord, result);
                        }
                        // 读取关键字后保存在数据库中的关键字列
                        String newKeyWords = keyWords.replaceAll("!", "");
                        printTemplate.setKeyWords(newKeyWords);
                        dao.save(printTemplate);

                        // 完成关键字解析后对word进行操作
                        exportDoc(tempFile.getCanonicalPath(),
                                Config.HOME_DIR + File.separator + handleFileNameByOs(finalFileName, "-" + docNumber, ".doc"), replaceMap,
                                bodyFiles, printTemplate, origWordText);
                        docNumber = docNumber - 1;
                    }

                    // 判断多次套打是分页还是多行
                    if ("paging".equals(printTemplate.getPrintInterval())) {
                        // 合并word文档(分页)
                        finalFile = uniteDocByPage(allEntities.size(), printTemplate, finalFileName);
                    } else /*
                     * if ("multi_line".equals(printTemplate .getPrintInterval()))
                     */ {
                        // 合并word文档(多行)
                        finalFile = uniteDocByMultiLine(allEntities.size(), printTemplate, finalFileName, origWordText);
                    }
                }
            } finally {
                if (tempFile.exists() && tempFile.delete()) {
                    logger.info("临时文件删除成功！");
                }
            }

            // 判断是否保存打印记录
            if (BooleanUtils.isTrue(printTemplate.getIsSavePrintRecord())) {
                for (Collection<ENTITY> entities : allEntities) {
                    PrintRecord printRecord = new PrintRecord();
                    String printObjectUuid = "";
                    String printObjectType = "";
                    for (ENTITY entity : entities) {
                        printObjectUuid = printObjectUuid + entity.getUuid() + ",";
                        printObjectType = printObjectType + entity.getClass().getName() + ",";
                    }
                    // 打印记录历史次数集合
                    List<Integer> oldPrintTimes = new ArrayList<Integer>();
                    List<PrintRecord> oldPrintRecords = printRecordService.queryByPrintObject(printObjectUuid);
                    for (PrintRecord oldPrintRecord : oldPrintRecords) {
                        Integer oldPrintTimes1 = oldPrintRecord.getPrintTimes();
                        oldPrintTimes.add(oldPrintTimes1);
                    }
                    // 取得打印次数的最大值
                    int maxOldPrintTimes = 0;
                    for (int i = 0; i < oldPrintTimes.size(); i++) {
                        if (oldPrintTimes.get(i) > maxOldPrintTimes) {
                            maxOldPrintTimes = oldPrintTimes.get(i);
                        }
                    }
                    // 如果打印记录不为null的话
                    if (oldPrintRecords != null) {
                        printRecord.setPrintTimes(maxOldPrintTimes + 1);
                    } else {
                        printRecord.setPrintTimes(1);
                    }
                    printRecord.setPrintObject(printObjectUuid);
                    printRecord.setPrintObjectType(printObjectType);
                    // 获取调用人
                    String userName = SpringSecurityUtils.getCurrentUserName();
                    logger.info("当前用户名：" + userName);
                    printRecord.setUserName(userName);
                    printRecord.setCode(templateId);
                    printRecordService.save(printRecord);
                }
            }
        } else if (printTemplate.doIsTemplateFileTypeAsHtml() || printTemplate.doIsTemplateFileTypeAsWordXml()
                || printTemplate.doIsTemplateFileTypeAsWordXmlByComment()) {
            // 判断是否需要多次套打
            if (allEntities.size() == 1) {
                // 判断是否保存到源文档
                if (BooleanUtils.isTrue(printTemplate.getIsSaveSource())) {
                    for (Collection<ENTITY> entities : allEntities) {
                        // 文件流每次被读取的话都要新下载该文件流
                        // String uuid = printTemplate.getUuid();
                        // 下载该打印模板对应的html模板文件
                        // List<MongoFileEntity> files = mongoFileService.getFilesFromFolder(uuid,
                        // "attach");
                        // MongoFileEntity mongoFileEntity = files.get(0);
                        InputStream inputStreamOut = getInputStreamByPrintTemplate(printTemplate, lang);// mongoFileEntity.getInputstream();
                        // 文件名定义格式
                        String fileNameFormat = printTemplate.getFileNameFormat();
                        fileNameFormat = fileNameFormat.replaceAll("}", "!}");// 防止freemarker模板处理null出错
                        Map<String, Object> templateMap = getMapValue(entities, dytableMaps.get(0));// 获得模板集合

                        String fileName = PrinttemplateFreemarkerUtils.process(printTemplate, fileNameFormat,
                                templateMap);
                        String outputPath = Config.HOME_DIR + File.separator + fileName + ".doc";// 文件输出路径
                        Map<String, String> replaceMap = new HashMap<String, String>();// 用来存放解析后的键值对

                        // 读取html模板流并替换为freemarker模板数据内容
                        String beforeReplaceHtml = IOUtils.toString(inputStreamOut);
                        String afterReplaceHtml = PrinttemplateFreemarkerUtils.process(printTemplate, beforeReplaceHtml,
                                templateMap);

                        if (printTemplate.doIsTemplateFileTypeAsWordXml()) {
                            afterReplaceHtml = afterReplaceHtml.replaceAll(" \n", "<w:br/>");
                        }
                        InputStream afterReplaceInputStream = new ByteArrayInputStream(
                                afterReplaceHtml.getBytes("UTF-8"));// html模板替换后的文件流
                        // File tempHtmlFile = new File("./", "tempHtml.doc");
                        FileUtils.copyInputStreamToFile(afterReplaceInputStream, new File(outputPath));
                        finalFile = new File(outputPath);
                    }
                } else {
                    // 多个工作数量
                    Integer docNumber = allEntities.size();
                    Integer subScript = 0;
                    // 对多个工作进行遍历
                    for (Collection<ENTITY> entities : allEntities) {
                        // 文件流每次被读取的话都要新下载该文件流
                        // String uuid = printTemplate.getUuid();
                        // 下载该打印模板对应的html模板文件
                        // List<MongoFileEntity> files = mongoFileService.getFilesFromFolder(uuid,
                        // "attach");
                        // MongoFileEntity mongoFileEntity = files.get(0);
                        InputStream inputStreamOut = getInputStreamByPrintTemplate(printTemplate, lang);// mongoFileEntity.getInputstream();
                        Map<String, String> replaceMap = new HashMap<String, String>();// 用来存放解析后的键值对
                        Map<String, Object> templateMap = getMapValue(entities, dytableMaps.get(subScript));// 获得模板集合
                        subScript = subScript + 1;

                        // 读取html模板流并替换为freemarker模板数据内容
                        String beforeReplaceHtml = IOUtils.toString(inputStreamOut);
                        String afterReplaceHtml = PrinttemplateFreemarkerUtils.process(printTemplate, beforeReplaceHtml,
                                templateMap);

                        if (printTemplate.doIsTemplateFileTypeAsWordXml()) {
                            afterReplaceHtml = afterReplaceHtml.replaceAll(" \n", "<w:br/>");
                        }
                        InputStream afterReplaceInputStream = new ByteArrayInputStream(
                                afterReplaceHtml.getBytes("UTF-8"));// html模板替换后的文件流
                        // File tempHtmlFile = new File(Config.HOME_DIR +
                        // File.separator +
                        // docNumber + ".doc");//多份工作临时存放位置，存放格式为1.doc,2.doc……
                        String outputPath = Config.HOME_DIR + File.separator + handleFileNameByOs(finalFileName, Strings.EMPTY, ".doc");// 文件输出路径
                        FileUtils.copyInputStreamToFile(afterReplaceInputStream, new File(outputPath));
                        finalFile = new File(outputPath);
                        docNumber = docNumber - 1;
                    }

                }
            } else {
                // 多个工作数量
                Integer docNumber = allEntities.size();
                Integer subScript = 0;
                // 对多个工作进行遍历
                for (Collection<ENTITY> entities : allEntities) {
                    // 文件流每次被读取的话都要新下载该文件流
                    // String uuid = printTemplate.getUuid();
                    // 下载该打印模板对应的html模板文件
                    // List<MongoFileEntity> files = mongoFileService.getFilesFromFolder(uuid,
                    // "attach");
                    // MongoFileEntity mongoFileEntity = files.get(0);
                    InputStream inputStreamOut = getInputStreamByPrintTemplate(printTemplate, lang);// mongoFileEntity.getInputstream();
                    Map<String, String> replaceMap = new HashMap<String, String>();// 用来存放解析后的键值对
                    Map<String, Object> templateMap = getMapValue(entities, dytableMaps.get(subScript));// 获得模板集合
                    subScript = subScript + 1;

                    // 读取html模板流并替换为freemarker模板数据内容
                    String beforeReplaceHtml = IOUtils.toString(inputStreamOut);
                    String afterReplaceHtml = PrinttemplateFreemarkerUtils.process(printTemplate, beforeReplaceHtml,
                            templateMap);

                    InputStream afterReplaceInputStream = new ByteArrayInputStream(afterReplaceHtml.getBytes("UTF-8"));// html模板替换后的文件流
                    // File tempHtmlFile = new File(Config.HOME_DIR +
                    // File.separator +
                    // docNumber + ".doc");//多份工作临时存放位置，存放格式为1.doc,2.doc……
                    String outputPath = Config.HOME_DIR + File.separator + handleFileNameByOs(finalFileName, Strings.EMPTY, ".doc");// 文件输出路径
                    FileUtils.copyInputStreamToFile(afterReplaceInputStream, new File(outputPath));
                    finalFile = new File(outputPath);

                    docNumber = docNumber - 1;
                }
            }

            // 判断是否保存打印记录
            if (BooleanUtils.isTrue(printTemplate.getIsSavePrintRecord())) {
                for (Collection<ENTITY> entities : allEntities) {
                    PrintRecord printRecord = new PrintRecord();
                    String printObjectUuid = "";
                    String printObjectType = "";
                    for (ENTITY entity : entities) {
                        printObjectUuid = printObjectUuid + entity.getUuid() + ",";
                        printObjectType = printObjectType + entity.getClass().getName() + ",";
                    }
                    // 打印记录历史次数集合
                    List<Integer> oldPrintTimes = new ArrayList<Integer>();
                    List<PrintRecord> oldPrintRecords = printRecordService.queryByPrintObject(printObjectUuid);
                    for (PrintRecord oldPrintRecord : oldPrintRecords) {
                        Integer oldPrintTimes1 = oldPrintRecord.getPrintTimes();
                        oldPrintTimes.add(oldPrintTimes1);
                    }
                    // 取得打印次数的最大值
                    int maxOldPrintTimes = 0;
                    for (int i = 0; i < oldPrintTimes.size(); i++) {
                        if (oldPrintTimes.get(i) > maxOldPrintTimes) {
                            maxOldPrintTimes = oldPrintTimes.get(i);
                        }
                    }
                    // 如果打印记录不为null的话
                    if (oldPrintRecords != null) {
                        printRecord.setPrintTimes(maxOldPrintTimes + 1);
                    } else {
                        printRecord.setPrintTimes(1);
                    }
                    printRecord.setPrintObject(printObjectUuid);
                    printRecord.setPrintObjectType(printObjectType);
                    // 获取调用人
                    String userName = SpringSecurityUtils.getCurrentUserName();
                    logger.info("当前用户名：" + userName);
                    printRecord.setUserName(userName);
                    printRecord.setCode(templateId);
                    printRecordService.save(printRecord);
                }
            }
        } else if (printTemplate.doIsTemplateFileTypeAsWordPoi()) {
            finalFile = getPrintTemplateFileByPoi(lang, allEntities, dytableMaps, printTemplate, finalFileName,
                    bodyFiles, null);
        } else {
            throw new BusinessException(printTemplate.getName() + ":打印模板类型未配置，请检查");
        }
        return finalFile;
    }

    /**
     * 合并文件（多行）
     */
    private <ENTITY extends IdEntity> File uniteDocByMultiLine(Integer docNumber, PrintTemplate printTemplate,
                                                               String finalFileName, String orgiWordText) {
        Integer rows = printTemplate.getRowNumber();// 获取行数
        String outputPath = Config.HOME_DIR + File.separator + handleFileNameByOs(finalFileName, Strings.EMPTY, ".doc");// 合并文件输出路径
        logger.info("合并文件输出路径:" + outputPath);
        List<String> fileList = new ArrayList<String>();
        for (int i = 1; i <= docNumber; i++) {
            String file = Config.HOME_DIR + File.separator + handleFileNameByOs(finalFileName, "-" + i, ".doc");
            fileList.add(file);
            logger.info("当前文件名为：" + file);
        }

        if (fileList.size() == 0 || fileList == null) {
            return null;
        }
        // 打开word
        ActiveXComponent active = new ActiveXComponent("Word.Application");// 启动word
        try {
            // 设置word不可见
            active.setProperty("Visible", new Variant(false));
            active.setProperty("DisplayAlerts", new Variant(0));

            // 获得documents对象
            Object docs = active.getProperty("Documents").toDispatch();
            // 打开第一个文件
            Dispatch doc = Dispatch.invoke((Dispatch) docs, "Open", Dispatch.Method,
                    new Object[]{(String) fileList.get(0), new Variant(false), new Variant(true)}, new int[3])
                    .toDispatch();
            // 选择区域
            Dispatch selection = PrinttemplateUtil.select(active);
            // 判断文本内容中是否包含{BOTTOM}
            if (orgiWordText.contains("{BOTTOM}")) {
                // 置底部分不用显示痕迹
                Dispatch.put(doc, "TrackRevisions", new Variant(false));
                Dispatch.put(doc, "PrintRevisions", new Variant(false));
                Dispatch.put(doc, "ShowRevisions", new Variant(false));
                logger.info("文本中包含{BOTTOM},需要置底");
                PrinttemplateUtil.moveStart(selection);
                Boolean result = PrinttemplateUtil.find("{BOTTOM}", selection, active);// 查找{BOTTOM}关键字并选中
                if (result) {
                    // 替换{BOTTOM}为空
                    PrinttemplateUtil.replaceAll(selection, "{BOTTOM}", "");
                    logger.info("{BOTTOM}已经替换为空");
                    // // 插入分页符
                    // Dispatch.call(selection, "InsertBreak", new Variant(7));
                    // moveUp(2, selection);//将光标往上移动两行
                    int pages1 = Integer.parseInt(Dispatch.call(selection, "information", 4).toString());// 总页数
                    // //显示修订内容的最终状态
                    logger.info("移动前总页数：" + pages1);
                    boolean flag = false;
                    while (pages1 == Integer.parseInt(Dispatch.call(selection, "information", 4).toString()) && !flag) {
                        PrinttemplateUtil.insertBlank(selection);// 插入空白行
                        while (Integer.parseInt(Dispatch.call(selection, "information", 4).toString()) > pages1) {
                            PrinttemplateUtil.moveUp(1, selection, active);// 超过当前页光标上移
                            // goToEnd(selection);//光标移动至末尾
                            Dispatch.call(selection, "Delete");// 删除当前空行
                            flag = true;
                            break;
                        }
                    }
                }
            }
            // 追加文件
            for (int i = 1; i < fileList.size(); i++) {
                PrinttemplateUtil.goToEnd(selection);
                if (i == 1) {
                    for (int j = 0; j <= rows; j++) {
                        PrinttemplateUtil.insertBlank(selection);
                    }
                } else {
                    // 第二份文件开始追加时少一个空行
                    for (int j = 0; j <= rows - 1; j++) {
                        PrinttemplateUtil.insertBlank(selection);
                    }
                }

                Dispatch.invoke(active.getProperty("Selection").toDispatch(), "insertFile", Dispatch.Method,
                        new Object[]{(String) fileList.get(i), "", new Variant(false), new Variant(false),
                                new Variant(false)},
                        new int[3]);
                // 判断文本内容中是否包含{BOTTOM}
                if (orgiWordText.contains("{BOTTOM}")) {
                    // 置底部分不用显示痕迹
                    Dispatch.put(doc, "TrackRevisions", new Variant(false));
                    Dispatch.put(doc, "PrintRevisions", new Variant(false));
                    Dispatch.put(doc, "ShowRevisions", new Variant(false));
                    logger.info("文本中包含{BOTTOM},需要置底");
                    PrinttemplateUtil.moveStart(selection);
                    Boolean result = PrinttemplateUtil.find("{BOTTOM}", selection, active);// 查找{BOTTOM}关键字并选中
                    if (result) {
                        // 替换{BOTTOM}为空
                        PrinttemplateUtil.replaceAll(selection, "{BOTTOM}", "");
                        logger.info("{BOTTOM}已经替换为空");
                        // // 插入分页符
                        // Dispatch.call(selection, "InsertBreak", new
                        // Variant(7));
                        // moveUp(2, selection);//将光标往上移动两行
                        int pages1 = Integer.parseInt(Dispatch.call(selection, "information", 4).toString());// 总页数
                        // //显示修订内容的最终状态
                        logger.info("移动前总页数：" + pages1);
                        boolean flag = false;
                        while (pages1 == Integer.parseInt(Dispatch.call(selection, "information", 4).toString())
                                && !flag) {
                            PrinttemplateUtil.insertBlank(selection);// 插入空白行
                            while (Integer.parseInt(Dispatch.call(selection, "information", 4).toString()) > pages1) {
                                flag = true;
                                break;
                            }
                        }
                    }
                }
            }
            // 删除最后一页空白页
            PrinttemplateUtil.goToEnd(selection);// 光标移动至末尾
            Dispatch.call(selection, "Delete");// 删除当前空行
            // 保存新的word文件
            Dispatch.invoke((Dispatch) doc, "SaveAs", Dispatch.Method, new Object[]{outputPath, new Variant(1)},
                    new int[3]);
            if (printTemplate.getIsReadOnly()) {
                new File(outputPath).setWritable(false);// 将输出文档设为只读
            }
            Variant f = new Variant(false);
            Dispatch.call((Dispatch) doc, "Close", f);
            // 合并完毕后删除原来文件
            for (String file : fileList) {
                // String file = Config.HOME_DIR + File.separator + i + ".doc";
                File delFile = new File(file);
                delFile.delete();
                logger.info("原来文件删除成功！");
            }
            // 删除临时文件temp.doc
            // new File("./", "temp.doc").delete();
            // logger.info("临时文件删除成功！");
        } catch (Exception e) {
            throw new RuntimeException("合并word文件出错.原因:" + e);
        } finally {
            active.invoke("Quit", new Variant[]{});
        }
        logger.info("多行合并成功！");
        return new File(outputPath);
    }

    /**
     * 合并文件（分页）
     */
    @Override
    public <ENTITY extends IdEntity> File uniteDocByPage(Integer docNumber, PrintTemplate printTemplate,
                                                         String finalFileName) {
        String outputPath = Config.HOME_DIR + File.separator + handleFileNameByOs(finalFileName, Strings.EMPTY, ".doc");// 合并文件输出路径
        logger.info("合并文件输出路径:" + outputPath);
        List<String> fileList = new ArrayList<String>();
        for (int i = 1; i <= docNumber; i++) {
            String file = Config.HOME_DIR + File.separator + handleFileNameByOs(finalFileName, "-" + i, ".doc");
            fileList.add(file);
            logger.info("当前文件名为：" + file);
        }
        if (fileList.size() == 0 || fileList == null) {
            return null;
        }
        // 打开word
        ActiveXComponent active = new ActiveXComponent("Word.Application");// 启动word
        try {
            // 设置word不可见
            active.setProperty("Visible", new Variant(false));
            // 获得documents对象
            Object docs = active.getProperty("Documents").toDispatch();
            // 打开第一个文件
            Object doc = Dispatch.invoke((Dispatch) docs, "Open", Dispatch.Method,
                    new Object[]{(String) fileList.get(0), new Variant(false), new Variant(true)}, new int[3])
                    .toDispatch();
            // 选择区域
            Dispatch selection = PrinttemplateUtil.select(active);
            // 追加文件
            for (int i = 1; i < fileList.size(); i++) {
                PrinttemplateUtil.goToEnd(selection);// 光标移动至末尾
                PrinttemplateUtil.PageBreak(selection);// 插入分页符
                Dispatch.invoke(active.getProperty("Selection").toDispatch(), "insertFile", Dispatch.Method,
                        new Object[]{(String) fileList.get(i), "", new Variant(false), new Variant(false),
                                new Variant(false)},
                        new int[3]);
            }
            // 删除最后一页空白页
            PrinttemplateUtil.goToEnd(selection);// 光标移动至末尾
            Dispatch.call(selection, "Delete");// 删除当前空行
            // 保存新的word文件
            Dispatch.invoke((Dispatch) doc, "SaveAs", Dispatch.Method, new Object[]{outputPath, new Variant(1)},
                    new int[3]);
            if (printTemplate.getIsReadOnly()) {
                new File(outputPath).setWritable(false);// 将输出文档设为只读
            }
            Variant f = new Variant(false);
            Dispatch.call((Dispatch) doc, "Close", f);
            // 合并完毕后删除原来文件
            for (String file : fileList) {
                // String file = Config.HOME_DIR + File.separator + i + ".doc";
                File delFile = new File(file);
                delFile.delete();
                logger.info("原来文件删除成功！");
            }
            // 删除临时文件temp.doc
            // new File("./", "temp.doc").delete();
            // logger.info("临时文件删除成功！");
        } catch (Exception e) {
            throw new RuntimeException("合并word文件出错.原因:" + e);
        } finally {
            active.invoke("Quit", new Variant[]{});
        }
        logger.info("分页合并成功！");
        return new File(outputPath);
    }

    /**
     * 解析关键字后操作word(单工作多行)
     *
     * @param inputDocPath
     * @param outPutDocPath
     * @param map
     * @param isPrint
     * @throws IOException
     */
    public File exportDocByMutiLine(String inputDocPath, String outPutDocPath, Map<String, String> map, File textFile,
                                    PrintTemplate printTemplate, String orgiWordText) {
        // 初始化com的线程
        PrinttemplateUtil.initSTA();
        // word运行程序对象
        ActiveXComponent word = null;
        try {
            word = new ActiveXComponent("Word.Application");
            // 文档对象
            Dispatch wordObject = (Dispatch) word.getObject();
            // 设置属性 Variant(true)表示word应用程序可见
            Dispatch.put((Dispatch) wordObject, "Visible", new Variant(false));
            Dispatch.put((Dispatch) wordObject, "DisplayAlerts", new Variant(0));
            // word所有文档
            Dispatch documents = word.getProperty("Documents").toDispatch();
            // 打开模板文档
            Dispatch document = PrinttemplateUtil.open(documents, inputDocPath);
            // 选择区域
            Dispatch selection = PrinttemplateUtil.select(word);

            Iterator keys = map.keySet().iterator();
            String oldText;
            Object newValue;
            while (keys.hasNext()) {
                oldText = (String) keys.next();// 替换前关键字
                newValue = map.get(oldText);// 替换后的关键字
                PrinttemplateUtil.replaceAll(selection, oldText, newValue);
            }
            // 判断是否包含{正文}及正文内容的文件
            if (orgiWordText.contains("{正文}") && textFile.exists()) {
                // 判断是否需要保留正文修改痕迹
                if (printTemplate.getIsSaveTrace()) {
                    Dispatch.put(document, "TrackRevisions", new Variant(false));
                    Dispatch.put(document, "PrintRevisions", new Variant(false));
                    Dispatch.put(document, "ShowRevisions", new Variant(false));
                    try {
                        PrinttemplateUtil.replaceFile(selection, "{正文}", textFile.getCanonicalPath());
                    } catch (IOException e) {
                        logger.error(e.getMessage(), e);
                        PrinttemplateUtil.close(document);// 出异常则关闭当前word
                        word.invoke("Quit", new Variant[0]);
                    }
                } else {
                    // 打开含正文内容文档
                    Dispatch textDocument;
                    try {
                        textDocument = PrinttemplateUtil.open(documents, textFile.getCanonicalPath());
                        Dispatch textSelection = PrinttemplateUtil.select(word);
                        Dispatch wordContent = Dispatch.get(textDocument, "Content").toDispatch(); // 取得正文word文件的内容
                        Dispatch.call(wordContent, "Select");// 全选
                        Dispatch.call(textSelection, "Copy");// 复制
                        PrinttemplateUtil.close(textDocument);// 关闭正文word
                        // 替换{正文}为空
                        PrinttemplateUtil.replaceAll(selection, "{正文}", "");
                        logger.info("{正文}已经替换为空");
                        PrinttemplateUtil.paste(selection);// 粘贴至源文档
                    } catch (IOException e) {
                        logger.error(e.getMessage(), e);
                        PrinttemplateUtil.close(document);// 出异常则关闭当前word
                        word.invoke("Quit", new Variant[0]);
                    }
                }
            }
            // 判断文本内容中是否包含{BOTTOM}
            if (orgiWordText.contains("{BOTTOM}")) {
                // 置底部分不用显示痕迹
                Dispatch.put(document, "TrackRevisions", new Variant(false));
                Dispatch.put(document, "PrintRevisions", new Variant(false));
                Dispatch.put(document, "ShowRevisions", new Variant(false));
                logger.info("文本中包含{BOTTOM},需要置底");
                PrinttemplateUtil.moveStart(selection);
                Boolean result = PrinttemplateUtil.find("{BOTTOM}", selection, word);// 查找{BOTTOM}关键字并选中
                if (result) {
                    // 替换{BOTTOM}为空
                    PrinttemplateUtil.replaceAll(selection, "{BOTTOM}", "");
                    logger.info("{BOTTOM}已经替换为空");
                    // // 插入分页符
                    // Dispatch.call(selection, "InsertBreak", new Variant(7));
                    // moveUp(2, selection);//将光标往上移动两行
                    int pages1 = Integer.parseInt(Dispatch.call(selection, "information", 4).toString());// 总页数
                    // //显示修订内容的最终状态
                    logger.info("移动前总页数：" + pages1);
                    boolean flag = false;
                    while (pages1 == Integer.parseInt(Dispatch.call(selection, "information", 4).toString()) && !flag) {
                        PrinttemplateUtil.insertBlank(selection);// 插入空白行
                        while (Integer.parseInt(Dispatch.call(selection, "information", 4).toString()) > pages1) {
                            PrinttemplateUtil.moveUp(1, selection, word);// 超过当前页光标上移
                            // goToEnd(selection);//光标移动至末尾
                            Dispatch.call(selection, "Delete");// 删除当前空行
                            flag = true;
                            break;
                        }
                    }
                }
            }

            PrinttemplateUtil.save(word, outPutDocPath, printTemplate, document);
            PrinttemplateUtil.close(document);
        } finally {
            if (word != null) {
                word.invoke("Quit", new Variant[0]);
            }
            // 关闭com的线程
            ComThread.Release();
        }
        return new File(outPutDocPath);
    }

    public File exportDocByMutiLine(String inputDocPath, String outPutDocPath, Map<String, String> map,
                                    Map<String, List<MongoFileEntity>> bodyFiles, PrintTemplate printTemplate, String origWordText) {
        // 初始化com的线程
        PrinttemplateUtil.initSTA();
        // word运行程序对象
        ActiveXComponent word = null;
        try {
            word = new ActiveXComponent("Word.Application");
            // 文档对象
            Dispatch wordObject = (Dispatch) word.getObject();
            // 设置属性 Variant(true)表示word应用程序可见
            Dispatch.put((Dispatch) wordObject, "Visible", new Variant(false));
            Dispatch.put((Dispatch) wordObject, "DisplayAlerts", new Variant(0));
            // word所有文档
            Dispatch documents = word.getProperty("Documents").toDispatch();
            // 打开模板文档
            Dispatch document = PrinttemplateUtil.open(documents, inputDocPath);
            try {
                // 选择区域
                Dispatch selection = PrinttemplateUtil.select(word);

                Iterator keys = map.keySet().iterator();
                String oldText;
                Object newValue;
                while (keys.hasNext()) {
                    oldText = (String) keys.next();// 替换前关键字
                    newValue = map.get(oldText);// 替换后的关键字
                    PrinttemplateUtil.replaceAll(selection, oldText, newValue);
                }
                for (Entry<String, List<MongoFileEntity>> entry : bodyFiles.entrySet()) {
                    if (origWordText.contains("{正文_" + entry.getKey() + "}")) {
                        List<MongoFileEntity> fileEntities = entry.getValue();
                        MongoFileEntity bodyFileEntity = null;
                        if (fileEntities != null && !fileEntities.isEmpty()) {
                            for (MongoFileEntity mongoFileEntity : fileEntities) {
                                // if
                                // (mongoFileEntity.getId().startsWith("BODY_FILEID_"))
                                // {
                                bodyFileEntity = mongoFileEntity;
                                // break;
                                // }
                            }
                        }
                        File bodyFile = null;
                        if (bodyFileEntity != null) {
                            OutputStream output = null;
                            try {
                                bodyFile = new File(Config.APP_DATA_DIR, UUID.randomUUID().toString());
                                InputStream is = bodyFileEntity.getInputstream();
                                output = new FileOutputStream(bodyFile);
                                IOUtils.copy(is, output);
                            } catch (Exception e) {
                                logger.error(ExceptionUtils.getStackTrace(e));
                            } finally {
                                if (output != null) {
                                    IOUtils.closeQuietly(output);
                                }
                            }
                        }
                        if (bodyFile != null && bodyFile.exists()) {
                            // 判断是否需要保留正文修改痕迹
                            if (printTemplate.getIsSaveTrace()) {
                                Dispatch.put(document, "TrackRevisions", new Variant(false));
                                Dispatch.put(document, "PrintRevisions", new Variant(false));
                                Dispatch.put(document, "ShowRevisions", new Variant(false));
                                try {
                                    PrinttemplateUtil.replaceFile(selection, "{正文_" + entry.getKey() + "}",
                                            bodyFile.getCanonicalPath());
                                } catch (IOException e) {
                                    logger.error(e.getMessage(), e);
                                    PrinttemplateUtil.close(document);// 出异常则关闭当前word
                                    word.invoke("Quit", new Variant[0]);
                                }
                            } else {
                                // 打开含正文内容文档
                                Dispatch textDocument;
                                try {
                                    textDocument = PrinttemplateUtil.open(documents, bodyFile.getCanonicalPath());
                                    Dispatch textSelection = PrinttemplateUtil.select(word);
                                    Dispatch wordContent = Dispatch.get(textDocument, "Content").toDispatch(); // 取得正文word文件的内容
                                    Dispatch.call(wordContent, "Select");// 全选
                                    Dispatch.call(textSelection, "Copy");// 复制
                                    PrinttemplateUtil.close(textDocument);// 关闭正文word
                                    // 替换{正文}为空
                                    PrinttemplateUtil.replaceAll(selection, "{正文_" + entry.getKey() + "}", "");
                                    logger.info("{正文}已经替换为空");
                                    PrinttemplateUtil.paste(selection);// 粘贴至源文档
                                } catch (IOException e) {
                                    logger.error(e.getMessage(), e);
                                    PrinttemplateUtil.close(document);// 出异常则关闭当前word
                                    word.invoke("Quit", new Variant[0]);
                                }
                            }

                        }
                    }

                    // 判断文本内容中是否包含{BOTTOM}
                    if (origWordText.contains("{BOTTOM}")) {
                        // 置底部分不用显示痕迹
                        Dispatch.put(document, "TrackRevisions", new Variant(false));
                        Dispatch.put(document, "PrintRevisions", new Variant(false));
                        Dispatch.put(document, "ShowRevisions", new Variant(false));
                        logger.info("文本中包含{BOTTOM},需要置底");
                        PrinttemplateUtil.moveStart(selection);
                        Boolean result = PrinttemplateUtil.find("{BOTTOM}", selection, word);// 查找{BOTTOM}关键字并选中
                        if (result) {
                            // 替换{BOTTOM}为空
                            PrinttemplateUtil.replaceAll(selection, "{BOTTOM}", "");
                            logger.info("{BOTTOM}已经替换为空");
                            // // 插入分页符
                            // Dispatch.call(selection, "InsertBreak", new
                            // Variant(7));
                            // moveUp(2, selection);//将光标往上移动两行
                            int pages1 = Integer.parseInt(Dispatch.call(selection, "information", 4).toString());// 总页数
                            // //显示修订内容的最终状态
                            logger.info("移动前总页数：" + pages1);
                            boolean flag = false;
                            while (pages1 == Integer.parseInt(Dispatch.call(selection, "information", 4).toString())
                                    && !flag) {
                                PrinttemplateUtil.insertBlank(selection);// 插入空白行
                                while (Integer
                                        .parseInt(Dispatch.call(selection, "information", 4).toString()) > pages1) {
                                    PrinttemplateUtil.moveUp(1, selection, word);// 超过当前页光标上移
                                    // goToEnd(selection);//光标移动至末尾
                                    Dispatch.call(selection, "Delete");// 删除当前空行
                                    flag = true;
                                    break;
                                }
                            }
                        }
                    }
                }
            } finally {
                PrinttemplateUtil.save(word, outPutDocPath, printTemplate, document);
                PrinttemplateUtil.close(document);
            }
        } finally {
            if (word != null) {
                word.invoke("Quit", new Variant[0]);
            }
            // 关闭com的线程
            ComThread.Release();
        }
        return new File(outPutDocPath);
    }

    /**
     * 解析关键字后操作word
     *
     * @param inputDocPath
     * @param outPutDocPath
     * @param map
     * @param isPrint
     * @throws IOException
     */
    public File exportDoc(String inputDocPath, String outPutDocPath, Map<String, String> map, File textFile,
                          PrintTemplate printTemplate, String orgiWordText) {
        PrinttemplateUtil.initSTA();
        // word运行程序对象
        ActiveXComponent word = null;
        try {
            word = new ActiveXComponent("Word.Application");
            // 文档对象
            Dispatch wordObject = (Dispatch) word.getObject();
            // 设置属性 Variant(true)表示word应用程序可见
            Dispatch.put((Dispatch) wordObject, "Visible", new Variant(false));
            // WdAlertLevel
            // https://docs.microsoft.com/en-us/dotnet/api/microsoft.office.interop.word.wdalertlevel?view=word-pia
            Dispatch.put((Dispatch) wordObject, "DisplayAlerts", new Variant(0));
            // word所有文档
            Dispatch documents = word.getProperty("Documents").toDispatch();
            // 打开模板文档
            Dispatch document = PrinttemplateUtil.open(documents, inputDocPath);
            // 选择区域
            Dispatch selection = PrinttemplateUtil.select(word);

            Iterator keys = map.keySet().iterator();
            String oldText;
            Object newValue;
            while (keys.hasNext()) {
                oldText = (String) keys.next();// 替换前关键字
                newValue = map.get(oldText);// 替换后的关键字
                PrinttemplateUtil.replaceAll(selection, oldText, newValue);
            }
            // 判断是否包含{正文}及正文内容的文件
            if (orgiWordText.contains("{正文}") && textFile.exists()) {
                // 判断是否需要保留正文修改痕迹
                if (printTemplate.getIsSaveTrace()) {
                    Dispatch.put(document, "TrackRevisions", new Variant(false));
                    Dispatch.put(document, "PrintRevisions", new Variant(false));
                    Dispatch.put(document, "ShowRevisions", new Variant(false));
                    try {
                        PrinttemplateUtil.replaceFile(selection, "{正文}", textFile.getCanonicalPath());
                    } catch (IOException e) {
                        logger.error(e.getMessage(), e);
                        PrinttemplateUtil.close(document);// 出异常则关闭当前word
                        word.invoke("Quit", new Variant[0]);
                    }
                } else {
                    // 打开含正文内容文档
                    Dispatch textDocument;
                    try {
                        textDocument = PrinttemplateUtil.open(documents, textFile.getCanonicalPath());
                        Dispatch textSelection = PrinttemplateUtil.select(word);
                        Dispatch wordContent = Dispatch.get(textDocument, "Content").toDispatch(); // 取得正文word文件的内容
                        Dispatch.call(wordContent, "Select");// 全选
                        Dispatch.call(textSelection, "Copy");// 复制
                        PrinttemplateUtil.close(textDocument);// 关闭正文word
                        // 替换{正文}为空
                        PrinttemplateUtil.replaceAll(selection, "{正文}", "");
                        logger.info("{正文}已经替换为空");
                        PrinttemplateUtil.paste(selection);// 粘贴至源文档
                    } catch (IOException e) {
                        logger.error(e.getMessage(), e);
                        PrinttemplateUtil.close(document);// 出异常则关闭当前word
                        word.invoke("Quit", new Variant[0]);
                    }
                }

            }
            // 如果是分页先执行置底然后再合并 &&判断文本内容中是否包含{BOTTOM}
            if ("paging".equals(printTemplate.getPrintInterval()) && orgiWordText.contains("{BOTTOM}")) {

                // 置底部分不用显示痕迹
                Dispatch.put(document, "TrackRevisions", new Variant(false));
                Dispatch.put(document, "PrintRevisions", new Variant(false));
                Dispatch.put(document, "ShowRevisions", new Variant(false));
                logger.info("文本中包含{BOTTOM},需要置底");
                PrinttemplateUtil.moveStart(selection);
                Boolean result = PrinttemplateUtil.find("{BOTTOM}", selection, word);// 查找{BOTTOM}关键字并选中
                if (result) {
                    // 替换{BOTTOM}为空
                    PrinttemplateUtil.replaceAll(selection, "{BOTTOM}", "");
                    logger.info("{BOTTOM}已经替换为空");
                    // // 插入分页符
                    // Dispatch.call(selection, "InsertBreak", new Variant(7));
                    // moveUp(2, selection);//将光标往上移动两行
                    int pages1 = Integer.parseInt(Dispatch.call(selection, "information", 4).toString());// 总页数
                    // //显示修订内容的最终状态
                    logger.info("移动前总页数：" + pages1);
                    boolean flag = false;
                    while (pages1 == Integer.parseInt(Dispatch.call(selection, "information", 4).toString()) && !flag) {
                        PrinttemplateUtil.insertBlank(selection);// 插入空白行
                        while (Integer.parseInt(Dispatch.call(selection, "information", 4).toString()) > pages1) {
                            PrinttemplateUtil.moveUp(1, selection, word);// 超过当前页光标上移
                            // goToEnd(selection);//光标移动至末尾
                            Dispatch.call(selection, "Delete");// 删除当前空行
                            flag = true;
                            break;
                        }
                    }
                }
            }

            PrinttemplateUtil.save(word, outPutDocPath, printTemplate, document);
            PrinttemplateUtil.close(document);
        } finally {
            if (word != null) {
                word.invoke("Quit", new Variant[0]);
            }
            // 关闭com的线程
            ComThread.Release();
        }
        return new File(outPutDocPath);
    }

    public File exportDoc(String inputDocPath, String outPutDocPath, Map<String, String> map,
                          Map<String, List<MongoFileEntity>> bodyFiles, PrintTemplate printTemplate, String orgiWordText) {
        PrinttemplateUtil.initSTA();
        // word运行程序对象
        ActiveXComponent word = null;
        try {
            word = new ActiveXComponent("Word.Application");
            // 文档对象
            Dispatch wordObject = (Dispatch) word.getObject();
            // 设置属性 Variant(true)表示word应用程序可见
            Dispatch.put((Dispatch) wordObject, "Visible", new Variant(false));
            Dispatch.put((Dispatch) wordObject, "DisplayAlerts", new Variant(0));
            // word所有文档
            Dispatch documents = word.getProperty("Documents").toDispatch();
            // 打开模板文档
            Dispatch document = PrinttemplateUtil.open(documents, inputDocPath);
            try {
                // 选择区域
                Dispatch selection = PrinttemplateUtil.select(word);

                Iterator keys = map.keySet().iterator();
                String oldText;
                Object newValue;
                while (keys.hasNext()) {
                    oldText = (String) keys.next();// 替换前关键字
                    newValue = map.get(oldText);// 替换后的关键字
                    if (StringUtils.contains(oldText, "{正文_")) {// 正文控件套打问题，无法在正确位置套打
                        continue;
                    }
                    PrinttemplateUtil.replaceTextAll(selection, oldText, newValue);
                }
                for (Entry<String, List<MongoFileEntity>> entry : bodyFiles.entrySet()) {
                    if (orgiWordText.contains("{正文_" + entry.getKey() + "}")) {
                        List<MongoFileEntity> fileEntities = entry.getValue();
                        MongoFileEntity bodyFileEntity = null;
                        if (fileEntities != null && !fileEntities.isEmpty()) {
                            for (MongoFileEntity mongoFileEntity : fileEntities) {
                                // if
                                // (mongoFileEntity.getId().startsWith("BODY_FILEID_"))
                                // {
                                bodyFileEntity = mongoFileEntity;
                                // break;
                                // }
                            }

                        }
                        File bodyFile = null;
                        if (bodyFileEntity != null) {
                            OutputStream output = null;
                            try {
                                bodyFile = new File(Config.APP_DATA_DIR, UUID.randomUUID().toString());
                                InputStream is = bodyFileEntity.getInputstream();
                                output = new FileOutputStream(bodyFile);
                                IOUtils.copy(is, output);
                            } catch (Exception e) {
                                logger.error(ExceptionUtils.getStackTrace(e));
                            } finally {
                                if (output != null) {
                                    IOUtils.closeQuietly(output);
                                }
                            }
                        }
                        if (bodyFile != null && bodyFile.exists()) {
                            // 判断是否需要保留正文修改痕迹
                            if (printTemplate.getIsSaveTrace()) {
                                Dispatch.put(document, "TrackRevisions", new Variant(false));
                                Dispatch.put(document, "PrintRevisions", new Variant(false));
                                Dispatch.put(document, "ShowRevisions", new Variant(false));
                                try {
                                    PrinttemplateUtil.replaceFile(selection, "{正文_" + entry.getKey() + "}",
                                            bodyFile.getCanonicalPath());
                                } catch (IOException e) {
                                    logger.error(e.getMessage(), e);
                                    PrinttemplateUtil.close(document);// 出异常则关闭当前word
                                    word.invoke("Quit", new Variant[0]);
                                }
                            } else {
                                // 打开含正文内容文档
                                Dispatch textDocument;
                                try {
                                    textDocument = PrinttemplateUtil.open(documents, bodyFile.getCanonicalPath());
                                    Dispatch textSelection = PrinttemplateUtil.select(word);
                                    Dispatch wordContent = Dispatch.get(textDocument, "Content").toDispatch(); // 取得正文word文件的内容
                                    Dispatch.call(wordContent, "Select");// 全选
                                    Dispatch.call(textSelection, "Copy");// 复制
                                    PrinttemplateUtil.close(textDocument);// 关闭正文word
                                    // 替换{正文}为空
                                    PrinttemplateUtil.replaceTextAll(selection, "{正文_" + entry.getKey() + "}", "");
                                    logger.info("{正文}已经替换为空");
                                    PrinttemplateUtil.paste(selection);// 粘贴至源文档
                                } catch (IOException e) {
                                    logger.error(e.getMessage(), e);
                                    PrinttemplateUtil.close(document);// 出异常则关闭当前word
                                    word.invoke("Quit", new Variant[0]);
                                }
                            }

                        }
                    }
                    // TODO
                    // 如果是分页先执行置底然后再合并 &&判断文本内容中是否包含{BOTTOM}
                    if ("paging".equals(printTemplate.getPrintInterval()) && orgiWordText.contains("{BOTTOM}")) {

                        // 置底部分不用显示痕迹
                        Dispatch.put(document, "TrackRevisions", new Variant(false));
                        Dispatch.put(document, "PrintRevisions", new Variant(false));
                        Dispatch.put(document, "ShowRevisions", new Variant(false));
                        logger.info("文本中包含{BOTTOM},需要置底");
                        PrinttemplateUtil.moveStart(selection);
                        Boolean result = PrinttemplateUtil.find("{BOTTOM}", selection, word);// 查找{BOTTOM}关键字并选中
                        if (result) {
                            // 替换{BOTTOM}为空
                            PrinttemplateUtil.replaceAll(selection, "{BOTTOM}", "");
                            logger.info("{BOTTOM}已经替换为空");
                            // // 插入分页符
                            // Dispatch.call(selection, "InsertBreak", new
                            // Variant(7));
                            // moveUp(2, selection);//将光标往上移动两行
                            int pages1 = Integer.parseInt(Dispatch.call(selection, "information", 4).toString());// 总页数
                            // //显示修订内容的最终状态
                            logger.info("移动前总页数：" + pages1);
                            boolean flag = false;
                            while (pages1 == Integer.parseInt(Dispatch.call(selection, "information", 4).toString())
                                    && !flag) {
                                PrinttemplateUtil.insertBlank(selection);// 插入空白行
                                while (Integer
                                        .parseInt(Dispatch.call(selection, "information", 4).toString()) > pages1) {
                                    PrinttemplateUtil.moveUp(1, selection, word);// 超过当前页光标上移
                                    // goToEnd(selection);//光标移动至末尾
                                    Dispatch.call(selection, "Delete");// 删除当前空行
                                    flag = true;
                                    break;
                                }
                            }
                        }
                    }
                }
            } finally {
                PrinttemplateUtil.save(word, outPutDocPath, printTemplate, document);
                PrinttemplateUtil.close(document);
            }
        } finally {
            if (word != null) {
                word.invoke("Quit", new Variant[0]);
            }
            // 关闭com的线程
            ComThread.Release();
        }
        return new File(outPutDocPath);
    }

    /**
     * 关键字定义（实体集合）
     *
     * @return
     * @throws Exception
     */

    public <ENTITY extends IdEntity> Map<String, Object> getMapValue(Collection<ENTITY> entities,
                                                                     Map<String, Object> dytableMap) throws Exception {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);// 获取年份
        int month = cal.get(Calendar.MONTH) + 1;// 获取月份
        int day = cal.get(Calendar.DATE);// 获取日
        int hour = cal.get(Calendar.HOUR_OF_DAY);// 小时
        int minute = cal.get(Calendar.MINUTE);// 分
        int second = cal.get(Calendar.SECOND);// 秒

        Map<String, Object> keyWordMap = new HashMap<String, Object>();
        keyWordMap.put("年", String.valueOf(year));
        keyWordMap.put("月", DateUtil.getFormatDate(month));
        keyWordMap.put("日", DateUtil.getFormatDate(day));
        keyWordMap.put("时", DateUtil.getFormatDate(hour));
        keyWordMap.put("分", DateUtil.getFormatDate(minute));
        keyWordMap.put("秒", DateUtil.getFormatDate(second));
        keyWordMap.put("简年", String.valueOf(year).substring(2));

        keyWordMap.put("YEAR", String.valueOf(year));
        keyWordMap.put("MONTH", DateUtil.getFormatDate(month));
        keyWordMap.put("DAY", DateUtil.getFormatDate(day));
        keyWordMap.put("HOUR", DateUtil.getFormatDate(hour));
        keyWordMap.put("MINUTE", DateUtil.getFormatDate(minute));
        keyWordMap.put("SECOND", DateUtil.getFormatDate(second));
        keyWordMap.put("SHORTYEAR", String.valueOf(year).substring(2));

        String dateStr = keyWordMap.get("年") + "-" + keyWordMap.get("月") + "-" + keyWordMap.get("日");
        keyWordMap.put("大写年", toChinese(dateStr, 0));
        keyWordMap.put("大写月", toChinese(dateStr, 1));
        keyWordMap.put("大写日", toChinese(dateStr, 2));
        keyWordMap.put("小写年", toSmallChinese(dateStr, 0));
        keyWordMap.put("小写月", toSmallChinese(dateStr, 1));
        keyWordMap.put("小写日", toSmallChinese(dateStr, 2));

        keyWordMap.put("UPPER_YEAR", toChinese(dateStr, 0));
        keyWordMap.put("UPPER_MONTH", toChinese(dateStr, 1));
        keyWordMap.put("UPPER_DAY", toChinese(dateStr, 2));
        keyWordMap.put("LOWER_YEAR", toSmallChinese(dateStr, 0));
        keyWordMap.put("LOWER_MONTH", toSmallChinese(dateStr, 1));
        keyWordMap.put("LOWER_DAY", toSmallChinese(dateStr, 2));

        keyWordMap.put("当前登录人", SpringSecurityUtils.getCurrentUserName());
        keyWordMap.put("currentUserName", SpringSecurityUtils.getCurrentUserName());

        /*
         * Map<String, SysProperties> sysPropertiess =
         * exchangedataApiFacade.getAllSysProperties("SYSPROPERTIES"); if
         * (sysPropertiess.get("sys_context_path") != null) { String contextPath =
         * sysPropertiess.get("sys_context_path").getProValue();// 系统访问全路径 if
         * (StringUtils.isNotBlank(contextPath)) { keyWordMap.put("ctx", contextPath); }
         * }
         */

        // 动态表单解析
        keyWordMap.put("dytable", dytableMap);
        keyWordMap.put("dyform", dytableMap);
        for (String key : dytableMap.keySet()) {
            if (dytableMap.get(key) instanceof List) {
                List lists = (List) dytableMap.get(key);
                if (lists.size() == 1) {
                    for (Object object : lists) {
                        if (object instanceof LogicFileInfo) {
                            LogicFileInfo logicFileInfo = (LogicFileInfo) object;
                            String fileId = logicFileInfo.getFileID();
                            String fileName = logicFileInfo.getFileName();
                            Long fileSize = logicFileInfo.getFileSize();
                            keyWordMap.put(key + "_fileId", fileId);
                            keyWordMap.put(key + "_fileName", fileName);
                            keyWordMap.put(key + "_fileSize", fileSize);
                        }
                    }
                } else if (lists.size() > 1) {
                    List<Map<String, Object>> fileIdsMapList = new ArrayList<Map<String, Object>>();
                    List<Map<String, Object>> fileNamesMapList = new ArrayList<Map<String, Object>>();
                    List<Map<String, Object>> fileSizesMapList = new ArrayList<Map<String, Object>>();
                    for (Object object : lists) {
                        if (object instanceof LogicFileInfo) {
                            Map<String, Object> logicFileIdsMap = new HashMap<String, Object>();
                            Map<String, Object> logicFileNamesMap = new HashMap<String, Object>();
                            Map<String, Object> logicFileSizesMap = new HashMap<String, Object>();
                            LogicFileInfo logicFileInfo = (LogicFileInfo) object;

                            String fileId = logicFileInfo.getFileID();
                            logicFileIdsMap.put(key + "_fileId", fileId);
                            fileIdsMapList.add(logicFileIdsMap);

                            String fileName = logicFileInfo.getFileName();
                            logicFileNamesMap.put(key + "_fileName", fileName);
                            fileNamesMapList.add(logicFileNamesMap);

                            Long fileSize = logicFileInfo.getFileSize();
                            logicFileSizesMap.put(key + "_fileSize", fileSize);
                            fileSizesMapList.add(logicFileSizesMap);

                        }
                    }
                    keyWordMap.put(key + "_fileIds", fileIdsMapList);
                    keyWordMap.put(key + "_fileNames", fileNamesMapList);
                    keyWordMap.put(key + "_fileSizes", fileSizesMapList);
                }
            }
        }

        // 只传入${属性}时解析
        for (Object obj : entities) {
            // 反射取得实体类的属性及值存入keyWordMap集合中
            BeanWrapperImpl wrapper = new BeanWrapperImpl(obj);
            PropertyDescriptor[] descriptors = wrapper.getPropertyDescriptors();
            for (PropertyDescriptor descriptor : descriptors) {
                String propertyName = descriptor.getName();
                logger.info(propertyName);
                keyWordMap.put(propertyName, wrapper.getPropertyValue(propertyName));
            }
        }

        // 传入${类.属性}时解析
        for (Object obj : entities) {
            String className = obj.getClass().getSimpleName();
            String lowerCaseName = className.substring(0, 1).toLowerCase() + className.substring(1);
            logger.info("首字母小写类名：" + lowerCaseName);
            keyWordMap.put(lowerCaseName, obj);
        }

        return keyWordMap;
    }

    /**
     * 根据类的完全限定名获取表名
     *
     * @param classtype
     * @return
     */

    public String getTableName(Class classtype) {
        Annotation[] anno = classtype.getAnnotations();
        String tableName = "";
        for (int i = 0; i < anno.length; i++) {
            if (anno[i] instanceof Table) {
                Table table = (Table) anno[i];
                logger.info(table.name());
                tableName = table.name();
            }
        }
        return tableName;
    }

    /**
     * 是否保存到源文档
     */
    @Override
    public Boolean isSaveToSource(String templateId) {
        PrintTemplate printTemplate = getById(templateId);
        Boolean isSaveSource = printTemplate.getIsSaveSource();
        return isSaveSource;
    }

    /**
     * 获取所有可用的打印模板定义
     *
     * @return
     */
    @Override
    public List<PrintTemplate> findAll() {
        return listAll();
    }

    @Override
    @Transactional
    public void save(PrintTemplate printTemplate) {
        dao.save(printTemplate);
    }

    /**
     * 批量删除
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.printtemplate.service.PrintTemplateService#deleteAllById(java.lang.String[])
     */
    @Override
    public void deleteAllById(String[] ids) {
        for (int i = 0; i < ids.length; i++) {
            PrintTemplate printTemplate = getById(ids[i]);
            if (printTemplate == null) {
                printTemplate = dao.getOne(ids[i]);
            }

            dao.delete(printTemplate);
        }
    }

    /**
     * 根据打印模板ID获取对应的模板实体对象
     *
     * @param printTemplateId
     * @return
     */
    public PrintTemplate getPrintTemplateById(String printTemplateId) {
        return getById(printTemplateId);
    }

    /**
     * 根据打印模板名称获取对应的模板实体对象集合
     *
     * @param printTemplateId
     * @return
     */
    public List<PrintTemplate> getPrintTemplatesByName(String printTemplateName) {
        return this.dao.listByFieldEqValue("name", printTemplateName);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.printtemplate.service.PrintTemplateService#getPrintTemplatesByCategory(java.lang.String)
     */
    @Override
    public List<PrintTemplate> getPrintTemplatesByCategory(String category) {
        return this.dao.listByFieldEqValue("type", category);
    }

    /**
     * 如何描述该方法
     *
     * @param example
     * @return
     */
    public List<PrintTemplate> findByExample(PrintTemplate printTemplate) {
        return dao.listByEntity(printTemplate);
    }

    /**
     * 获取打印模板类型
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.printtemplate.service.PrintTemplateService#getPrintTemplateType(java.lang.String)
     */
    @Override
    public String getPrintTemplateType(String printTemplateId) {
        PrintTemplate printTemplate = this.getPrintTemplateById(printTemplateId);
        return printTemplate.getTemplateType();
    }

    @Override
    public List<PrintTemplate> getPrintTemplateByIds(String printTemplateIdsStr) {
        List<PrintTemplate> printTemplateList = new ArrayList<>();
        String[] printTemplateIds = printTemplateIdsStr.split(Separator.SEMICOLON.getValue());
        for (String printTemplateId : printTemplateIds) {
            PrintTemplate printTemplate = this.getPrintTemplateById(printTemplateId);
            printTemplateList.add(printTemplate);
        }
        return printTemplateList;
    }

    public PrintTemplate getById(String id) {
        return dao.getById(id);
        // List<PrintTemplate> printTemplates = this.dao.listByFieldEqValue("id", id);
        // return CollectionUtils.isNotEmpty(printTemplates) ? printTemplates.get(0) :
        // null;
    }

    public PrintTemplate getPrintTemplate(String templateId, String templateUuid) {
        PrintTemplate printTemplate = null;
        if (StringUtils.isNotBlank(templateUuid)) {
            printTemplate = dao.getOne(templateUuid);
        }
        if (printTemplate != null && StringUtils.isNotBlank(printTemplate.getUuid())) {
            return printTemplate;
        }
        return this.getById(templateId);// .findUniqueBy("id", templateId);
    }

    /**
     *
     */
    @Override
    public void checkPrintTemplateOk(String templateId, String templateUuid, String lang) {
        PrintTemplate printTemplate = getPrintTemplate(templateId, templateUuid);
        if (printTemplate == null) {
            throw new PrintTemplateNotFoundException();
        }
        IOUtils.closeQuietly(getInputStreamByPrintTemplate(printTemplate, lang));
    }

    public InputStream getInputStreamByPrintTemplate(PrintTemplate printTemplate, String lang) {
        List<PrintContents> lists = printContentsService.listByTemplateUuid(printTemplate.getUuid());
        if (lists == null || lists.isEmpty()) {
            List<MongoFileEntity> files = mongoFileService.getFilesFromFolder(printTemplate.getUuid(), "attach");
            if (CollectionUtils.isEmpty(files)) {
                throw new BusinessException("模板文件不能为空，请检查模板配置！");
            }
            MongoFileEntity mongoFileEntity = files.get(0);
            return mongoFileEntity.getInputstream();
        } else if (lists.size() == 1) {
            PrintContents printContent = lists.get(0);
            if (printContent.getContent() != null) {
                try {
                    Reader render = printContent.getContent().getCharacterStream();
                    return new ReaderInputStream(render, Charset.defaultCharset());
                } catch (SQLException ex) {
                    logger.error(ex.getMessage(), ex);
                }
            } else if (StringUtils.isNotBlank(printContent.getFileUuid())) {
                MongoFileEntity mongoFileEntity = mongoFileService.getFile(printContent.getFileUuid());
                return mongoFileEntity.getInputstream();
            }
        }
        for (PrintContents printContent : lists) {
            if (StringUtils.isNotBlank(lang) && (StringUtils.equals(printContent.getLang(), lang)
                    || StringUtils.equals(printContent.getUuid(), lang)) == false) {
                continue;
            }
            if (printContent.getContent() != null) {
                try {
                    Reader render = printContent.getContent().getCharacterStream();
                    return new ReaderInputStream(render, Charset.defaultCharset());
                } catch (SQLException ex) {
                    logger.error(ex.getMessage(), ex);
                }
            } else if (StringUtils.isNotBlank(printContent.getFileUuid())) {
                MongoFileEntity mongoFileEntity = mongoFileService.getFile(printContent.getFileUuid());
                return mongoFileEntity.getInputstream();
            }
        }
        throw new PrintTemplateNotAssignedLangException("未知打印语言", lists);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.printtemplate.service.PrintTemplateService#getPrintTempalteLangs(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public List<PrintContents> getPrintTempalteLangs(String printtemplateId, String printtemplateUuid,
                                                     String printtemplateLang) {
        List<PrintContents> printContents = new ArrayList<PrintContents>();
        PrintTemplate printTemplate = getPrintTemplate(printtemplateId, printtemplateUuid);
        if (printTemplate != null && StringUtils.isNotBlank(printTemplate.getUuid())) {
            List<PrintContents> lists = printContentsService.listByTemplateUuid(printTemplate.getUuid());
            if (lists != null && lists.isEmpty() == false) {
                for (PrintContents printContent : lists) {
                    String resourceCode = printContent.getResourceCode();
                    if (StringUtils.isNotBlank(resourceCode) && securityApiFacade.isGranted(resourceCode) == false) {
                        continue;
                    } /*
                     * else if (securityApiFacade.isGranted(BTN_RESOURCE_SCHEMA +
                     * printContent.getLang()) == false) { continue;// TODO 添加权限控制 }
                     */
                    printContents.add(printContent);
                }
            }
        }
        return printContents;
    }

    @Override
    public List<TreeNode> getPrintTemplateTree() {
        List<PrintTemplateCategory> printTemplateCategoryList = printTemplateCategoryService.getAllBySystemUnitIds();
        List<PrintTemplate> printTemplateList = this.findAll();

        return getPrintTemplateTree(printTemplateCategoryList, printTemplateList);
    }

    @Override
    public List<TreeNode> getPrintTemplateTree(List<PrintTemplateCategory> printTemplateCategoryList,
                                               List<PrintTemplate> printTemplateList) {
        List<PrintTemplateTreeBean> printTemplateTreeBeanList = new ArrayList<>();
        for (PrintTemplateCategory printTemplateCategory : printTemplateCategoryList) {
            PrintTemplateTreeBean printTemplateTreeBean = new PrintTemplateTreeBean();
            printTemplateTreeBean.setUuid(printTemplateCategory.getUuid());
            printTemplateTreeBean.setParentUuid(printTemplateCategory.getParentUuid());
            printTemplateTreeBean.setPrintTemplateCategory(printTemplateCategory);
            printTemplateTreeBeanList.add(printTemplateTreeBean);
        }

        for (PrintTemplate printTemplate : printTemplateList) {
            PrintTemplateTreeBean printTemplateTreeBean = new PrintTemplateTreeBean();
            printTemplateTreeBean.setUuid(printTemplate.getUuid());
            printTemplateTreeBean.setParentUuid(printTemplate.getCategory());
            printTemplateTreeBean.setPrintTemplate(printTemplate);
            printTemplateTreeBeanList.add(printTemplateTreeBean);
        }

        TreeNode treeNode = TreeUtils.buildTree(null, printTemplateTreeBeanList, "uuid", "parentUuid",
                new Function<PrintTemplateTreeBean, TreeNode>() {
                    @Override
                    public TreeNode apply(PrintTemplateTreeBean treeNodeDto) {
                        TreeNode treeNode1 = new TreeNode();
                        treeNode1.setId(treeNodeDto.getUuid());
                        PrintTemplate printTemplate = treeNodeDto.getPrintTemplate();
                        if (printTemplate != null) {
                            treeNode1.setName(printTemplate.getName() + " ("
                                    + PrintUtils.versionFormat.format(printTemplate.getVersion()) + ")");
                            treeNode1.setData(printTemplate);
                            treeNode1.setType(PrintTemplate.class.getSimpleName());
                        }
                        PrintTemplateCategory printTemplateCategory = treeNodeDto.getPrintTemplateCategory();
                        if (printTemplateCategory != null) {
                            treeNode1.setName(printTemplateCategory.getName());
                            treeNode1.setData(printTemplateCategory);
                            treeNode1.setType(PrintTemplateCategory.class.getSimpleName());
                        }

                        return treeNode1;
                    }
                });

        List<TreeNode> children = treeNode.getChildren();
        // 过滤掉没有模版的分类
        filterHasPrintTemplateCategory(treeNode);
        if (children == null || children.size() == 0) {
            children = Lists.newArrayList();
            children.add(treeNode);
        }

        // TreeNode unClassifyTreeNode = new TreeNode("un_classify", "未分类", "");
        // unClassifyTreeNode.setData(new PrintTemplateCategory());
        // unClassifyTreeNode.setNocheck(false);
        // unClassifyTreeNode.setIsParent(true);
        // unClassifyTreeNode.setType(PrintTemplateCategory.class.getSimpleName());
        // List<TreeNode> unClassifyTreeNodeChildren = new ArrayList<>();
        // if (CollectionUtils.isNotEmpty(children)) {
        // for (Iterator<TreeNode> iterator = children.iterator(); iterator.hasNext(); )
        // {
        // TreeNode next = iterator.next();
        // if (PrintTemplate.class.getSimpleName().equals(next.getType())) {
        // // 打印模版
        // unClassifyTreeNodeChildren.add(next);
        // iterator.remove();
        // }
        // }
        // if (CollectionUtils.isNotEmpty(unClassifyTreeNodeChildren)) {
        // unClassifyTreeNode.setChildren(unClassifyTreeNodeChildren);
        // treeNode.getChildren().add(0, unClassifyTreeNode);
        // }
        // }

        return children;// Collections.singletonList(treeNode);
    }

    private boolean filterHasPrintTemplateCategory(TreeNode treeNode) {
        boolean hasTemplate = false;
        if (PrintTemplate.class.getSimpleName().equals(treeNode.getType())) {
            hasTemplate = true;
        }

        if (CollectionUtils.isNotEmpty(treeNode.getChildren())) {

            for (Iterator<TreeNode> iterator = treeNode.getChildren().iterator(); iterator.hasNext(); ) {
                TreeNode child = iterator.next();
                if (filterHasPrintTemplateCategory(child)) {
                    hasTemplate = true;
                } else {
                    iterator.remove();
                }
            }
        }

        return hasTemplate;
    }

    @Override
    public void updateCategory(String oldCategoryCode, String newCategoryCode) {
        String hql = "update PrintTemplate t set t.category = :newCategoryCode where t.category = :oldCategoryCode";
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("oldCategoryCode", oldCategoryCode);
        values.put("newCategoryCode", newCategoryCode);
        this.dao.updateByHQL(hql, values);
    }

    @Override
    public List<PrintTemplate> getListByUuids(Set<String> printTemplateUuidList) {
        if (CollectionUtils.isEmpty(printTemplateUuidList)) {
            return Lists.newArrayList();
        }
        Map<String, Object> values = Maps.newHashMap();
        values.put("printTemplateUuidList", printTemplateUuidList);
        return this.dao.listByNameSQLQuery("getListByUuids", values);
    }

    @Override
    public List<PrintTemplate> getTemplateListByTypes(Set<String> categoryUuids) {
        if (CollectionUtils.isEmpty(categoryUuids)) {
            return Lists.newArrayList();
        }
        Map<String, Object> values = Maps.newHashMap();
        values.put("categoryUuids", categoryUuids);
        return this.dao.listByNameSQLQuery("getTemplateListByTypes", values);
    }

    public boolean idIsExists(String id, String uuid) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("id", id);
        StringBuilder hql = new StringBuilder("select count(uuid) from PrintTemplate o where o.id = :id ");
        if (StringUtils.isNotBlank(uuid)) {
            hql.append(" and o.uuid <> :uuid ");
            params.put("uuid", uuid);
        }
        return (Long) this.dao.getNumberByHQL(hql.toString(), params) > 0;
    }

    /**
     * 根据操作系统处理文件名
     *
     * @param fileName 文件名
     * @param fillPart 填充部分（例如-temp，-1）
     * @param suffix   后缀（例如.doc）
     * @return 处理后的文件名
     */
    private String handleFileNameByOs(String fileName, String fillPart, String suffix) {
        boolean isWindows = System.getProperty("os.name").toLowerCase().contains("win");

        if (!isWindows) {
            int fileNameLength = fileName.length();
            int fillPartLength = fillPart.length();
            // 基于Linux内核的国产操作系统，文件名超过83个字符的时候，无法创建，需要截断
            if (fileNameLength + fillPartLength > 83) {
                return fileName.substring(0, 83 - fillPartLength) + fillPart + suffix;
            }
        }

        return fileName + fillPart + suffix;
    }

}
