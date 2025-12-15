package com.wellsoft.pt.basicdata.printtemplate.service;

import com.wellsoft.context.component.jqgrid.JqGridQueryData;
import com.wellsoft.context.component.jqgrid.JqGridQueryInfo;
import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.context.jdbc.support.QueryData;
import com.wellsoft.context.jdbc.support.QueryInfo;
import com.wellsoft.pt.basicdata.printtemplate.bean.PrintTemplateBean;
import com.wellsoft.pt.basicdata.printtemplate.dao.PrintTemplateDao;
import com.wellsoft.pt.basicdata.printtemplate.entity.PrintContents;
import com.wellsoft.pt.basicdata.printtemplate.entity.PrintTemplate;
import com.wellsoft.pt.basicdata.printtemplate.entity.PrintTemplateCategory;
import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.repository.entity.mongo.file.MongoFileEntity;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Description: 打印模板服务层接口
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

public interface PrintTemplateService extends JpaService<PrintTemplate, PrintTemplateDao, String> {

    public static final String BTN_RESOURCE_SCHEMA = "B://";

    public static final String ACL_SID = "ROLE_PRINT_TEMPLATE";

    void save(PrintTemplate printTemplate);

    /**
     *
     * 下载打印模板定义
     *
     * @param uuid
     */
    /*
     * public void download(String uuid);
     */

    /**
     * 使用acl接口存放打印模板使用人
     *
     * @param printTemplate
     * @return
     */
    PrintTemplate saveAcl(PrintTemplate printTemplate);

    /**
     * 通过UUID获取打印模板
     *
     * @param uuid
     * @return
     */
    public PrintTemplate getByUuid(String uuid);

    /**
     * 通过UUID获取打印模板VO对象
     *
     * @param uuid
     * @return
     */
    public PrintTemplateBean getBeanByUuid(String uuid);

    /**
     * 保存打印模板
     *
     * @param uuid
     * @return
     */
    public PrintTemplate saveBean(PrintTemplateBean bean, Boolean saveNew) throws IOException;

    /**
     * 通过UUID删除打印模板
     *
     * @param uuid
     * @return
     */
    public void remove(String uuid);

    /**
     * 批量删除
     *
     * @param uuids
     */
    public void deleteAllById(String[] ids);

    void deleteByUuids(List<String> uuids);

    /**
     * 打印模板列表查询
     *
     * @param queryInfo
     * @return
     */
    public JqGridQueryData query(JqGridQueryInfo queryInfo);

    /**
     * 打印模板调用接口,返回文件（模板ID,单份工作,动态表单集合,输入文件(正文)）
     *
     * @param templateId
     * @param entities
     */
    public abstract <ENTITY extends IdEntity> File getPrintTemplateFile(String templateId, Collection<ENTITY> entities,
                                                                        Map<String, Object> dytableMap, File textFile) throws Exception;

    /**
     * 打印模板调用接口,返回文件流（模板ID,单份工作,动态表单集合,输入文件(正文)）
     *
     * @param templateId
     * @param entities
     */
    public abstract <ENTITY extends IdEntity> InputStream getPrintTemplateInputStream(String templateId,
                                                                                      Collection<ENTITY> entities, Map<String, Object> dytableMap, File textFile) throws Exception;

    public abstract <ENTITY extends IdEntity> InputStream getPrintTemplateInputStream(String templateId,
                                                                                      String printTemplateUuid, String lang, Collection<ENTITY> entities, Map<String, Object> dytableMap,
                                                                                      File textFile) throws Exception;

    /**
     * 打印模板调用接口,返回文件流（模板ID,单份工作,动态表单集合,输入所有文件(正文)）
     *
     * @param templateId
     * @param entities
     */
    public abstract <ENTITY extends IdEntity> InputStream getPrintTemplateInputStream(String templateId,
                                                                                      Collection<ENTITY> entities, Map<String, Object> dytableMap, Map<String, List<MongoFileEntity>> bodyFiles)
            throws Exception;

    public abstract <ENTITY extends IdEntity> InputStream getPrintTemplateInputStream(String templateId,
                                                                                      String printTemplateUuid, String lang, Collection<ENTITY> entities, Map<String, Object> dytableMap,
                                                                                      Map<String, List<MongoFileEntity>> bodyFiles) throws Exception;

    /**
     * 打印模板调用接口,返回文件（模板ID,多份工作,动态表单集合,输入文件(正文)）
     *
     * @param templateId
     * @param entities
     */
    public abstract <ENTITY extends IdEntity> File getPrintTemplateFile(String templateId,
                                                                        Collection<Collection<ENTITY>> allEntities, List<Map<String, Object>> dytableMaps, File textFile)
            throws Exception;

    /**
     * 打印模板调用接口,返回文件（模板ID,多份工作,动态表单集合,输入所有文件(正文)）
     *
     * @param templateId
     * @param entities
     */
    public abstract <ENTITY extends IdEntity> File getPrintTemplateFile(String templateId,
                                                                        Collection<Collection<ENTITY>> allEntities, List<Map<String, Object>> dytableMaps,
                                                                        Map<String, List<MongoFileEntity>> bodyFiles) throws Exception;

    /**
     * 打印模板调用接口,返回文件流（模板ID,多份工作,动态表单集合,输入文件(正文)）
     *
     * @param templateId
     * @param entities
     */
    public abstract <ENTITY extends IdEntity> InputStream getPrintTemplateInputStream(String templateId,
                                                                                      Collection<Collection<ENTITY>> allEntities, List<Map<String, Object>> dytableMaps, File textFile)
            throws Exception;

    /**
     * 合并文件（分页）
     */
    <ENTITY extends IdEntity> File uniteDocByPage(Integer docNumber, PrintTemplate printTemplate, String finalFileName);

    /**
     * 打印结果是否保存到源文档
     */
    public Boolean isSaveToSource(String templateId);

    /**
     * 获取所有可用的打印模板定义
     *
     * @return
     */
    List<PrintTemplate> findAll();

    /**
     * 根据打印模板ID获取对应的模板实体对象
     *
     * @param printTemplateId
     * @return
     */
    PrintTemplate getPrintTemplateById(String printTemplateId);

    /**
     * 根据打印模板名称获取对应的模板实体对象集合
     *
     * @param printTemplateId
     * @return
     */
    List<PrintTemplate> getPrintTemplatesByName(String printTemplateName);

    /**
     * 根据打印模板分类获取对应的模板实体对象集合
     *
     * @param category
     * @return
     */
    List<PrintTemplate> getPrintTemplatesByCategory(String category);

    /**
     * 如何描述该方法
     *
     * @param example
     * @return
     */
    List<PrintTemplate> findByExample(PrintTemplate printTemplate);

    /**
     * 获取打印模板类型
     *
     * @param example
     * @return
     */
    String getPrintTemplateType(String printTemplateId);

    public abstract List<PrintContents> getPrintTempalteLangs(String printtemplateId, String printtemplateUuid,
                                                              String printtemplateLang);

    List<PrintTemplate> getPrintTemplateByIds(String printTemplateIdsStr);

    public abstract void checkPrintTemplateOk(String templateId, String templateUuid, String lang);

    /**
     * 如何描述该方法
     *
     * @param jqGridQueryInfo
     * @param queryInfo
     * @return
     */
    QueryData getForPageAsTree(JqGridQueryInfo jqGridQueryInfo, QueryInfo queryInfo);

    List<TreeNode> getPrintTemplateTree();

    List<TreeNode> getPrintTemplateTree(List<PrintTemplateCategory> printTemplateCategoryList,
                                        List<PrintTemplate> printTemplateList);

    void updateCategory(String oldCategoryCode, String newCategoryCode);

    /**
     * 通过Uuid集合获取打印模板对象列表
     *
     * @param printTemplateUuidList
     * @return
     **/
    public List<PrintTemplate> getListByUuids(Set<String> printTemplateUuidList);

    /**
     * 通过打印模板分类Uuid集合获取打印模板对象列表
     *
     * @param categoryUuids
     * @return java.util.List<com.wellsoft.pt.basicdata.printtemplate.entity.PrintTemplate>
     **/
    public List<PrintTemplate> getTemplateListByTypes(Set<String> categoryUuids);

}
