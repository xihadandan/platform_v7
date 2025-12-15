/*
 * @(#)2013-3-13 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.view.service.impl;

import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.enums.IdPrefix;
import com.wellsoft.context.enums.ModuleID;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.context.util.date.DateUtils;
import com.wellsoft.context.util.reflection.ConvertUtils;
import com.wellsoft.pt.basicdata.datadict.dto.CdDataDictionaryItemDto;
import com.wellsoft.pt.basicdata.datadict.entity.DataDictionary;
import com.wellsoft.pt.basicdata.datadict.service.DataDictionaryService;
import com.wellsoft.pt.basicdata.datasource.bean.DataSourceDefinitionBean;
import com.wellsoft.pt.basicdata.datasource.facade.service.impl.DataSourceApiFacadeImpl;
import com.wellsoft.pt.basicdata.datasource.provider.DataSourceProvider;
import com.wellsoft.pt.basicdata.datasource.service.DataSourceDataService;
import com.wellsoft.pt.basicdata.datasource.service.DataSourceDefinitionService;
import com.wellsoft.pt.basicdata.datasource.support.DataSourceConfig;
import com.wellsoft.pt.basicdata.facade.service.BasicDataApiFacade;
import com.wellsoft.pt.basicdata.systemtable.entity.SystemTable;
import com.wellsoft.pt.basicdata.systemtable.entity.SystemTableAttribute;
import com.wellsoft.pt.basicdata.systemtable.entity.SystemTableRelationship;
import com.wellsoft.pt.basicdata.view.bean.*;
import com.wellsoft.pt.basicdata.view.dao.ViewDefinitionNewDao;
import com.wellsoft.pt.basicdata.view.entity.*;
import com.wellsoft.pt.basicdata.view.provider.ViewColumnNew;
import com.wellsoft.pt.basicdata.view.provider.ViewDataSourceNew;
import com.wellsoft.pt.basicdata.view.service.GetViewDataNewService;
import com.wellsoft.pt.basicdata.view.service.ViewDefinitionNewService;
import com.wellsoft.pt.basicdata.view.support.CondSelectAskInfoNew;
import com.wellsoft.pt.basicdata.view.support.DyViewQueryInfoNew;
import com.wellsoft.pt.basicdata.view.support.DyviewConfigNew;
import com.wellsoft.pt.basicdata.view.support.ToJavaName;
import com.wellsoft.pt.cache.CacheManager;
import com.wellsoft.pt.common.marker.service.ReadMarkerService;
import com.wellsoft.pt.dyform.facade.dto.DyFormFormDefinition;
import com.wellsoft.pt.dyform.facade.dto.DyformFieldDefinition;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.multi.org.entity.MultiOrgElement;
import com.wellsoft.pt.multi.org.entity.MultiOrgUserAccount;
import com.wellsoft.pt.multi.org.facade.service.OrgApiFacade;
import com.wellsoft.pt.security.acl.entity.AclSid;
import com.wellsoft.pt.security.acl.service.AclService;
import com.wellsoft.pt.security.acl.support.AclPermission;
import com.wellsoft.pt.security.acl.support.QueryInfo;
import com.wellsoft.pt.security.audit.entity.Resource;
import com.wellsoft.pt.security.facade.service.SecurityApiFacade;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import com.wellsoft.pt.unit.entity.CommonUnit;
import com.wellsoft.pt.unit.facade.service.UnitApiFacade;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.model.Permission;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Description: 如何描述该类
 *
 * @author Administrator
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-3-13.1	Administrator		2013-3-13		Create
 * </pre>
 * @date 2013-3-13
 */
@Service
@Transactional
public class ViewDefinitionNewServiceImpl extends BaseServiceImpl implements ViewDefinitionNewService {

    private Logger logger = LoggerFactory.getLogger(ViewDefinitionNewServiceImpl.class);

    @Autowired
    private ViewDefinitionNewDao viewDefinitionNewDao;

    @Autowired
    private DyFormFacade dyFormApiFacade;
    @Autowired
    private DataSourceApiFacadeImpl dataSourceApiFacade;
    @Autowired
    private DataSourceDataService dataSourceDataService;
    @Autowired
    private SecurityApiFacade securityApiFacade;
    @Autowired
    private BasicDataApiFacade basicDataApiFacade;
    @Autowired(required = false)
    private Map<String, ViewDataSourceNew> viewDataSourceNewMap;
    @Autowired
    private GetViewDataNewService getViewDataNewService;
    @Autowired
    private AclService aclService;
    @Autowired
    private OrgApiFacade orgApiFacade;

    @Autowired
    private DataDictionaryService dataDictionaryService;
    @Autowired
    private ReadMarkerService readMarkerService;
    @Autowired
    private UnitApiFacade unitApiFacade;
    @Autowired
    private DataSourceDefinitionService dataSourceDefinitionService;
    @Autowired(required = false)
    private Map<String, DataSourceProvider> dataSourceProviderMap;

    /**
     * 如何描述该方法
     *
     * @param viewDefUuid
     * @return
     */
    public static ViewDefinitionNewBean getViewDefinitionNewBean(String viewDefUuid) {
        String cacheKey = "getViewDefinitionNewBean_" + viewDefUuid;
        CacheManager cacheManager = ApplicationContextHolder.getBean(CacheManager.class);
        return (ViewDefinitionNewBean) cacheManager.getCache(ModuleID.CMS).getValue(cacheKey);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.dyview.service.ViewDefinitionServiceBak#getById(java.lang.String)
     */
    @Override
    public ViewDefinitionNew getByViewId(String id) {
        return this.viewDefinitionNewDao.findUniqueBy("viewId", id);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.dyview.service.ViewDefinitionServiceBak#searchViewDefinition()
     */
    @Override
    public List<ViewDefinitionNew> searchViewDefinition() {
        String hql = "from ViewDefinitionNew h";
        List<ViewDefinitionNew> viewDefinitionNews = viewDefinitionNewDao.find(hql);
        return viewDefinitionNews;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.dyview.service.ViewDefinitionService#searchViewDefinitionByPermission()
     */
    @Override
    public List<ViewDefinitionNew> searchViewDefinitionByPermission() {
        // String hql =
        // " select distinct h from ViewDefinitionNew h inner join h.columnDefinitions r where r.fieldPermission is true";
        String hql = " select distinct r.viewDefinition from ColumnDefinitionNew r where r.fieldPermission is true";

        List<ViewDefinitionNew> viewDefinitionNews = viewDefinitionNewDao.find(hql);
        return viewDefinitionNews;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.dyview.service.ViewDefinitionServiceBak#saveViewDefinition(com.wellsoft.pt.basicdata.dyview.entity.ViewDefinition)
     */
    @Override
    public void saveViewDefinition(ViewDefinitionNew entity) {
        // TODO Auto-generated method stub

    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.dyview.service.ViewDefinitionServiceBak#deleteViewDefinition(java.lang.String)
     */
    @Override
    public void deleteViewDefinition(String viewUuid) {
        ViewDefinitionNew viewDefinitionNew = viewDefinitionNewDao.get(viewUuid);
        if (viewDefinitionNew != null) {
            viewDefinitionNewDao.delete(viewDefinitionNew);
        }
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.dyview.service.ViewDefinitionServiceBak#getBean(java.lang.String)
     */
    @Override
    public ColumnDefinitionNewBean getBean(String s) {

        return null;
    }

    /**
     * 保存视图
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.dyview.service.ViewDefinitionServiceBak#saveBean(com.wellsoft.pt.basicdata.dyview.bean.ViewDefinitionBean)
     */
    @Override
    public String saveBean(ViewDefinitionNewBean bean) {
        ViewDefinitionNew viewDefinitionNew = new ViewDefinitionNew();
        if (StringUtils.isNotBlank(bean.getUuid())) {
            viewDefinitionNew = this.viewDefinitionNewDao.get(bean.getUuid());
        } else {
            bean.setUuid(null);
            bean.setId((new StringBuilder("V")).append(UUID.randomUUID()).toString());
        }
        BeanUtils.copyProperties(bean, viewDefinitionNew);
        this.viewDefinitionNewDao.save(viewDefinitionNew);

        // 保存列属性
        Set<ColumnDefinitionNewBean> beans = new LinkedHashSet<ColumnDefinitionNewBean>();
        beans = bean.getColumnFields();
        if (StringUtils.isNotBlank(bean.getUuid())) {
            // 删除已存在的列
            for (ColumnDefinitionNew cdf : bean.getColumnDefinitionNews()) {
                ColumnDefinitionNew columnDefinitionNew = this.dao.get(ColumnDefinitionNew.class, cdf.getUuid());
                this.dao.delete(columnDefinitionNew);
            }
        }
        for (ColumnDefinitionNew cdf : beans) {
            // cdf.setSortOrder(i);
            if (StringUtils.isNotBlank(cdf.getUuid())) {
                ColumnDefinitionNew columnDefinitionNew = this.dao.get(ColumnDefinitionNew.class, cdf.getUuid());
                BeanUtils.copyProperties(cdf, columnDefinitionNew);
                columnDefinitionNew.setViewDefinitionNew(viewDefinitionNew);
                this.dao.save(columnDefinitionNew);
            } else {
                ColumnDefinitionNew columnDefinitionNew = new ColumnDefinitionNew();
                BeanUtils.copyProperties(cdf, columnDefinitionNew);
                columnDefinitionNew.setViewDefinitionNew(viewDefinitionNew);
                this.dao.save(columnDefinitionNew);
            }
        }
        // 样式定义
        Set<ColumnCssDefinitionNewBean> columnCssDefinitionNewBeans = new LinkedHashSet<ColumnCssDefinitionNewBean>();
        columnCssDefinitionNewBeans = bean.getColumnCssDefinitionFields();
        if (StringUtils.isNotBlank(bean.getUuid())) {
            // 删除已存在的行样式
            for (ColumnCssDefinitionNew ccdf : bean.getColumnCssDefinitionNew()) {
                ColumnCssDefinitionNew columnCssDefinitionNew = this.dao.get(ColumnCssDefinitionNew.class,
                        ccdf.getUuid());
                this.dao.delete(columnCssDefinitionNew);
            }
        }
        for (ColumnCssDefinitionNew ccdf : columnCssDefinitionNewBeans) {
            if (StringUtils.isNotBlank(ccdf.getUuid())) {
                ColumnCssDefinitionNew columnCssDefinitionNew = this.dao.get(ColumnCssDefinitionNew.class,
                        ccdf.getUuid());
                BeanUtils.copyProperties(ccdf, columnCssDefinitionNew);
                columnCssDefinitionNew.setViewDefinitionNew(viewDefinitionNew);
                this.dao.save(columnCssDefinitionNew);
            } else {
                ColumnCssDefinitionNew columnCssDefinitionNew = new ColumnCssDefinitionNew();
                BeanUtils.copyProperties(ccdf, columnCssDefinitionNew);
                columnCssDefinitionNew.setViewDefinitionNew(viewDefinitionNew);
                this.dao.save(columnCssDefinitionNew);
            }
        }

        Set<CustomButtonNewBean> customBeans = new LinkedHashSet<CustomButtonNewBean>();
        customBeans = bean.getCustomButtonFields();
        if (StringUtils.isNotBlank(bean.getUuid())) {
            // 删除已存在的按钮
            for (CustomButtonNew cdf : bean.getCustomButtonNews()) {
                CustomButtonNew customButtonNew = this.dao.get(CustomButtonNew.class, cdf.getUuid());
                this.dao.delete(customButtonNew);
            }
        }
        for (CustomButtonNew cdf : customBeans) {
            if (StringUtils.isNotBlank(cdf.getUuid())) {
                CustomButtonNew customButtonNew = this.dao.get(CustomButtonNew.class, cdf.getUuid());
                BeanUtils.copyProperties(cdf, customButtonNew);
                customButtonNew.setViewDefinitionNew(viewDefinitionNew);
                this.dao.save(customButtonNew);
            } else {
                CustomButtonNew customButtonNew = new CustomButtonNew();
                BeanUtils.copyProperties(cdf, customButtonNew);
                customButtonNew.setViewDefinitionNew(viewDefinitionNew);
                this.dao.save(customButtonNew);
            }
        }

        // 保存分页属性
        PageDefinitionNewBean pageBean = bean.getPageFields();

        PageDefinitionNew pageDefinitionNew = viewDefinitionNew.getPageDefinitionNews();
        if (pageDefinitionNew != null) {
            BeanUtils.copyProperties(pageBean, pageDefinitionNew, new String[]{IdEntity.UUID});
            this.dao.save(pageDefinitionNew);
        } else {
            // 新建一个分页定义
            PageDefinitionNew pageDefinition2 = new PageDefinitionNew();
            BeanUtils.copyProperties(pageBean, pageDefinition2);
            pageDefinition2.setViewDefinitionNew(viewDefinitionNew);
            this.dao.save(pageDefinition2);
        }

        // 保存查询属性
        SelectDefinitionNewBean selectBean = bean.getSelectFields();
        SelectDefinitionNewBean selectDeleteBean = bean.getSelectDeleteFields();
        SelectDefinitionNew selectDefinitionNew = viewDefinitionNew.getSelectDefinitionNews();
        if (selectDefinitionNew != null) {
            BeanUtils.copyProperties(selectBean, selectDefinitionNew, new String[]{IdEntity.UUID});
            this.dao.save(selectDefinitionNew);
            if (selectDeleteBean != null) {
                Set<ConditionTypeNewBean> conditionTypeDeBeans = selectDeleteBean.getConditionTypeFields();
                for (ConditionTypeNew cdt : conditionTypeDeBeans) {
                    if (StringUtils.isNotBlank(cdt.getUuid())) {
                        // 备选项
                        ConditionTypeNew conditionTypeNew = this.dao.get(ConditionTypeNew.class, cdt.getUuid());
                        this.dao.delete(conditionTypeNew);
                    }
                }
                Set<FieldSelectBean> fieldSelectDeBeans = selectDeleteBean.getFieldSelectBeans();
                for (FieldSelect fs : fieldSelectDeBeans) {
                    if (StringUtils.isNotBlank(fs.getUuid())) {
                        // 备选项
                        FieldSelect fieldSelectNew = this.dao.get(FieldSelect.class, fs.getUuid());
                        this.dao.delete(fieldSelectNew);
                    }
                }
            }
            Set<FieldSelectBean> fieldSelectBeans = selectBean.getFieldSelectBeans();
            for (FieldSelect fs : fieldSelectBeans) {
                if (StringUtils.isNotBlank(fs.getUuid())) {
                    FieldSelect fieldSelect = this.dao.get(FieldSelect.class, fs.getUuid());
                    BeanUtils.copyProperties(fs, fieldSelect);
                    fieldSelect.setSelectDefinitionNew(selectDefinitionNew);
                    this.dao.save(fieldSelect);
                } else {
                    FieldSelect fieldSelect = new FieldSelect();
                    BeanUtils.copyProperties(fs, fieldSelect);
                    fieldSelect.setSelectDefinitionNew(selectDefinitionNew);
                    this.dao.save(fieldSelect);
                }
            }

            Set<ConditionTypeNewBean> conditionTypeNewBeans = selectBean.getConditionTypeFields();
            for (ConditionTypeNew cdt : conditionTypeNewBeans) {
                if (StringUtils.isNotBlank(cdt.getUuid())) {
                    ConditionTypeNew conditionTypeNew = this.dao.get(ConditionTypeNew.class, cdt.getUuid());
                    BeanUtils.copyProperties(cdt, conditionTypeNew);
                    conditionTypeNew.setSelectDefinitionNew(selectDefinitionNew);
                    this.dao.save(conditionTypeNew);
                } else {
                    ConditionTypeNew conditionTypeNew = new ConditionTypeNew();
                    BeanUtils.copyProperties(cdt, conditionTypeNew);
                    conditionTypeNew.setSelectDefinitionNew(selectDefinitionNew);
                    this.dao.save(conditionTypeNew);
                }
            }
        } else {
            // 新建一个查询定义
            SelectDefinitionNew selectDefinition2 = new SelectDefinitionNew();
            BeanUtils.copyProperties(selectBean, selectDefinition2);
            selectDefinition2.setViewDefinitionNew(viewDefinitionNew);
            this.dao.save(selectDefinition2);

            Set<FieldSelectBean> fieldSelectBeans = selectBean.getFieldSelectBeans();
            for (FieldSelect fs : fieldSelectBeans) {
                if (StringUtils.isNotBlank(fs.getUuid())) {
                    FieldSelect fieldSelect = this.dao.get(FieldSelect.class, fs.getUuid());
                    BeanUtils.copyProperties(fs, fieldSelect);
                    fieldSelect.setSelectDefinitionNew(selectDefinitionNew);
                    this.dao.save(fieldSelect);
                } else {
                    FieldSelect fieldSelect = new FieldSelect();
                    BeanUtils.copyProperties(fs, fieldSelect);
                    fieldSelect.setSelectDefinitionNew(selectDefinitionNew);
                    this.dao.save(fieldSelect);
                }
            }

            Set<ConditionTypeNewBean> conditionTypeNewBeans = selectBean.getConditionTypeFields();
            for (ConditionTypeNew cdt : conditionTypeNewBeans) {
                if (StringUtils.isNotBlank(cdt.getUuid())) {
                    ConditionTypeNew conditionTypeNew = this.dao.get(ConditionTypeNew.class, cdt.getUuid());
                    BeanUtils.copyProperties(cdt, conditionTypeNew);
                    conditionTypeNew.setSelectDefinitionNew(selectDefinitionNew);
                    this.dao.save(conditionTypeNew);
                } else {
                    ConditionTypeNew conditionTypeNew = new ConditionTypeNew();
                    BeanUtils.copyProperties(cdt, conditionTypeNew);
                    conditionTypeNew.setSelectDefinitionNew(selectDefinitionNew);
                    this.dao.save(conditionTypeNew);
                }
            }
        }

        // 清理缓存
        // CmsCacheUtils.clear();
        return viewDefinitionNew.getUuid();
    }

    /**
     * 根据uuid获取视图的详细信息
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.dyview.service.ViewDefinitionServiceBak#getBeanByUuid(java.lang.String)
     */
    @Override
    public ViewDefinitionNewBean getBeanByUuid(String uuid) {
        ViewDefinitionNewBean viewDefinitionNewBean = getViewDefinitionNewBean(uuid);
        if (viewDefinitionNewBean == null) {
            ViewDefinitionNew viewDefinitionNew = viewDefinitionNewDao.getByUuid(uuid);
            if (viewDefinitionNew != null) {
                viewDefinitionNewBean = new ViewDefinitionNewBean();
                BeanUtils.copyProperties(viewDefinitionNew, viewDefinitionNewBean);
                // 设置列定义
                Set<ColumnDefinitionNew> columnDefinitionNews = viewDefinitionNew.getColumnDefinitionNews();
                viewDefinitionNewBean.setColumnDefinitionNews(BeanUtils.convertCollection(columnDefinitionNews,
                        ColumnDefinitionNew.class));
                // 设置样式定义
                Set<ColumnCssDefinitionNew> columnCssDefinitionNews = viewDefinitionNew.getColumnCssDefinitionNew();
                viewDefinitionNewBean.setColumnCssDefinitionNew(BeanUtils.convertCollection(columnCssDefinitionNews,
                        ColumnCssDefinitionNew.class));
                // 设置按钮定义
                Set<CustomButtonNew> customButtonNews = viewDefinitionNew.getCustomButtonNews();
                viewDefinitionNewBean.setCustomButtonNews(BeanUtils.convertCollection(customButtonNews,
                        CustomButtonNew.class));

                // 设置分页定义
                PageDefinitionNew pageDefinitionNew = viewDefinitionNew.getPageDefinitionNews();
                PageDefinitionNew pageDefinition2 = new PageDefinitionNew();
                if (pageDefinitionNew != null) {
                    BeanUtils.copyProperties(pageDefinitionNew, pageDefinition2);
                    viewDefinitionNewBean.setPageDefinitionNews(pageDefinition2);
                }

                // 设置查询定义
                SelectDefinitionNew selectDefinitionNew = viewDefinitionNew.getSelectDefinitionNews();
                Set<ConditionTypeNew> conditionTypeNews = selectDefinitionNew.getConditionTypeNew();
                Set<FieldSelect> fieldSelect = selectDefinitionNew.getFieldSelects();
                SelectDefinitionNew selectDefinition2 = new SelectDefinitionNew();
                if (selectDefinitionNew != null) {
                    BeanUtils.copyProperties(selectDefinitionNew, selectDefinition2);
                    selectDefinition2.setFieldSelects(BeanUtils.convertCollection(fieldSelect, FieldSelect.class));
                    selectDefinition2.setConditionTypeNew(BeanUtils.convertCollection(conditionTypeNews,
                            ConditionTypeNew.class));
                    viewDefinitionNewBean.setSelectDefinitionNews(selectDefinition2);
                }
                // CmsCacheUtils.setViewDefinitionNewBean(uuid,
                // viewDefinitionNewBean);

            }
        }
        return viewDefinitionNewBean;
    }

    /**
     * 根据jqgrid的行id来获取视图的详细信息
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.dyview.service.ViewDefinitionServiceBak#getBeanById(java.lang.String)
     */
    @Override
    public ViewDefinitionNewBean getBeanById(String id) {
        ViewDefinitionNew viewDefinitionNew = viewDefinitionNewDao.getById(id);
        ViewDefinitionNewBean bean = new ViewDefinitionNewBean();
        BeanUtils.copyProperties(viewDefinitionNew, bean);
        // 设置列定义
        Set<ColumnDefinitionNew> columnDefinitionNews = viewDefinitionNew.getColumnDefinitionNews();
        bean.setColumnDefinitionNews(BeanUtils.convertCollection(columnDefinitionNews, ColumnDefinitionNew.class));

        // 设置样式定义
        Set<ColumnCssDefinitionNew> columnCssDefinitionNews = viewDefinitionNew.getColumnCssDefinitionNew();
        if (columnCssDefinitionNews.isEmpty() == false) {
            bean.setColumnCssDefinitionNew(BeanUtils.convertCollection(columnCssDefinitionNews,
                    ColumnCssDefinitionNew.class));
        }

        // 设置按钮定义
        Set<CustomButtonNew> customButtonNews = viewDefinitionNew.getCustomButtonNews();
        bean.setCustomButtonNews(BeanUtils.convertCollection(customButtonNews, CustomButtonNew.class));

        // 设置分页定义
        PageDefinitionNew pageDefinitionNew = viewDefinitionNew.getPageDefinitionNews();
        PageDefinitionNew pageDefinition2 = new PageDefinitionNew();
        if (pageDefinitionNew != null) {
            BeanUtils.copyProperties(pageDefinitionNew, pageDefinition2);
            bean.setPageDefinitionNews(pageDefinition2);
        }
        // 设置查询定义
        SelectDefinitionNew selectDefinitionNew = viewDefinitionNew.getSelectDefinitionNews();
        Set<ConditionTypeNew> conditionTypeNews = selectDefinitionNew.getConditionTypeNew();
        Set<FieldSelect> fieldSelect = selectDefinitionNew.getFieldSelects();
        SelectDefinitionNew selectDefinition2 = new SelectDefinitionNew();
        if (selectDefinitionNew != null) {
            BeanUtils.copyProperties(selectDefinitionNew, selectDefinition2);
            selectDefinition2.setFieldSelects(BeanUtils.convertCollection(fieldSelect, FieldSelect.class));
            selectDefinition2.setConditionTypeNew(BeanUtils
                    .convertCollection(conditionTypeNews, ConditionTypeNew.class));
            bean.setSelectDefinitionNews(selectDefinition2);
        }
        return bean;
    }

    /**
     * 根据jqgrid的行id来删除视图的详细信息
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.dyview.service.ViewDefinitionServiceBak#deleteById(java.lang.String)
     */
    @Override
    public void deleteById(String id) {
        ViewDefinitionNew viewDefinitionNew = viewDefinitionNewDao.getById(id);
        viewDefinitionNewDao.delete(viewDefinitionNew);
        // 清理缓存
        // CmsCacheUtils.clear();
    }

    /**
     * 批量删除视图
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.dyview.service.ViewDefinitionServiceBak#deleteAllById(java.lang.String[])
     */
    public void deleteAllById(String[] ids) {
        for (int i = 0; i < ids.length; i++) {
            ViewDefinitionNew viewDefinitionNew = viewDefinitionNewDao.getById(ids[i]);
            viewDefinitionNewDao.delete(viewDefinitionNew);
        }
        // 清理缓存
        // CmsCacheUtils.clear();
    }

    /**
     * 模块数据查询
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.dyview.service.ViewDefinitionServiceBak#getColumnData3(java.lang.String, java.util.Set, com.wellsoft.pt.basicdata.dyview.entity.PageDefinition, com.wellsoft.context.jdbc.support.PagingInfo)
     */
    @Override
    public List<QueryItem> getColumnData3(String defaultCondition, String tableName,
                                          Set<ColumnDefinitionNew> columnDefinitionNews, PageDefinitionNew pageDefinitionNew,
                                          DyViewQueryInfoNew dyViewQueryInfoNew, String count) {
        return null;
    }

    @Override
    public long getView3Count(String tableName, String whereHql) {
        return viewDataSourceNewMap.get(tableName).count(null, whereHql, new HashMap<String, Object>());
    }

    /**
     * 模块数据查询(提供给树形下拉框使用)
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.dyview.service.ViewDefinitionServiceBak#getColumnData3(java.lang.String, java.util.Set, com.wellsoft.pt.basicdata.dyview.entity.PageDefinition, com.wellsoft.context.jdbc.support.PagingInfo)
     */
    @Override
    public List<TreeNode> getColumnDataForTree(String s, String viewUuid) {
        return null;
    }

    /**
     * 分类获得所有的视图
     *
     * @return
     */
    public List<TreeNode> getViewAsTreeAsync(String id) {
        List<CdDataDictionaryItemDto> ddList = basicDataApiFacade.getDataDictionariesByType("MODULE_CATEGORY");
        List<ViewDefinitionNew> viewDefinitionNews = this.searchViewDefinition();
        List<TreeNode> treeNodes = new ArrayList<TreeNode>();
        for (CdDataDictionaryItemDto d : ddList) {
            TreeNode node = new TreeNode();
            node.setId(d.getValue());
            node.setData(d.getValue());
            node.setNocheck(true);
            node.setName(d.getLabel());
            List<TreeNode> childTreeNodes = new ArrayList<TreeNode>();
            for (ViewDefinitionNew v : viewDefinitionNews) {
                if (v.getCateUuid().equals(d.getValue())) {
                    TreeNode node2 = new TreeNode();
                    node2.setId(v.getUuid());
                    node2.setData(v.getUuid());
                    node2.setName(v.getViewName());
                    childTreeNodes.add(node2);
                    node.setChildren(childTreeNodes);
                    node.setIsParent(true);
                }
            }
            treeNodes.add(node);
        }
        return treeNodes;
    }

    /**
     * 获取视图对应的列的数据 (调用动态表单接口)
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.dyview.service.ViewDefinitionServiceBak#getColumnData(java.lang.String)
     */
    @Override
    public List<QueryItem> getColumnData(String defaultCondition, String dataSourceDefId,
                                         Set<ColumnDefinitionNew> columnDefinitionNews, PageDefinitionNew pageDefinitionNew,
                                         DyViewQueryInfoNew dyViewQueryInfoNew) {
        return null;
    }

    /**
     * 根据uuid获取视图对应的排序的列的数据
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.dyview.service.ViewDefinitionServiceBak#getSortColumnData(java.lang.String, java.lang.String, java.lang.String, java.util.Set)
     */
    @Override
    public List<QueryItem> getSortColumnData(String defaultCondition, String tableName,
                                             Set<ColumnDefinitionNew> columnDefinitionNews, String title, PageDefinitionNew pageDefinitionNew,
                                             PagingInfo page, String orderbyArr) {
        StringBuilder selection = new StringBuilder();
        Set<String> choorseNames = new HashSet<String>();
        Set<String> unitNames = new HashSet<String>();
        if (defaultCondition != null && !defaultCondition.equals("")) {
            selection.append(defaultCondition + " and");
        }
        StringBuilder orderBy = new StringBuilder();
        int firstResult = 0;
        int maxResults = 0;
        List<String> fieldName = new ArrayList<String>();
        if (pageDefinitionNew.getIsPaging() == true) {
            firstResult = pageDefinitionNew.getPageNum() * (page.getCurrentPage() - 1);
            maxResults = pageDefinitionNew.getPageNum();
        }
        // 计数器
        int i = 0;
        for (ColumnDefinitionNew columnDefinitionNew : columnDefinitionNews) {
            String field = columnDefinitionNew.getFieldName();
            String titleName = columnDefinitionNew.getTitleName();
            fieldName.add(field + " as " + field);
            String defaultSort = columnDefinitionNew.getDefaultSort();
            if (title.equals(titleName)) {
                if (i < 1) {
                    if (orderBy.toString().equals("")) {
                        orderBy.append(field).append(" " + orderbyArr);
                    } else {
                        orderBy.append("," + field).append(" " + orderbyArr);
                    }
                } else {
                    if (orderBy.toString().equals("")) {
                        orderBy.append(field).append(" " + orderbyArr);
                    } else {
                        orderBy.append("," + field).append(" " + orderbyArr);
                    }
                }
            }
            if (columnDefinitionNew.getColumnType().equals("用户")) {
                choorseNames.add(columnDefinitionNew.getFieldName());
            }
            if (columnDefinitionNew.getColumnType().equals("单位")) {
                unitNames.add(columnDefinitionNew.getFieldName());
            }
            i++;
        }
        String[] fieldNames = fieldName.toArray(new String[fieldName.size()]);
        // 是否去掉重复的数据
        boolean distinct = true;

        List<QueryItem> data = dyFormApiFacade.query(tableName, distinct, fieldNames, selection.toString(), null, null,
                null, orderBy.toString(), firstResult, maxResults);
        // 将列类型是用户的值通过调用组织机构的接口获取到用户的显示值显示到页面上
        getDataByUser(data, choorseNames);
        getDataByUnit(data, unitNames);
        return data;
    }

    /**
     * 根据uuid获取视图对应的排序的列的数据
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.dyview.service.ViewDefinitionServiceBak#getSortColumnData(java.lang.String, java.lang.String, java.lang.String, java.util.Set)
     */
    @Override
    public List<QueryItem> getSortColumnData3(String defaultCondition, String tableName,
                                              Set<ColumnDefinitionNew> columnDefinitionNews, String title, PageDefinitionNew pageDefinitionNew,
                                              PagingInfo page, String orderbyArr) {
        List<QueryItem> data = new ArrayList<QueryItem>();
        StringBuilder selection = new StringBuilder();
        Set<String> choorseNames = new HashSet<String>();
        Set<String> unitNames = new HashSet<String>();
        if (defaultCondition != null && !defaultCondition.equals("")) {
            selection.append(defaultCondition + " and ");
        }
        StringBuilder orderBy = new StringBuilder();
        // 计数器
        int i = 0;
        List<ViewColumnNew> viewColumnNews = new ArrayList<ViewColumnNew>();
        Map<String, ViewColumnNew> map = ConvertUtils.convertElementToMap(viewDataSourceNewMap.get(tableName)
                .getAllViewColumns(), "columnAlias");
        for (ColumnDefinitionNew columnDefinitionNew : columnDefinitionNews) {
            viewColumnNews.add(map.get(columnDefinitionNew.getFieldName()));
            String field = columnDefinitionNew.getFieldName();
            String titleName = columnDefinitionNew.getTitleName();
            String otherName = columnDefinitionNew.getOtherName();
            if (otherName != null && otherName != "") {
                if (title.equals(otherName)) {
                    if (i < 1) {
                        if (orderBy.toString().equals("")) {
                            orderBy.append(field).append(" " + orderbyArr);
                        } else {
                            orderBy.append("," + field).append(" " + orderbyArr);
                        }
                    } else {
                        if (orderBy.toString().equals("")) {
                            orderBy.append(field).append(" " + orderbyArr);
                        } else {
                            orderBy.append("," + field).append(" " + orderbyArr);
                        }
                    }
                }
            } else {
                if (title.equals(titleName)) {
                    if (i < 1) {
                        if (orderBy.toString().equals("")) {
                            orderBy.append(field).append(" " + orderbyArr);
                        } else {
                            orderBy.append("," + field).append(" " + orderbyArr);
                        }
                    } else {
                        if (orderBy.toString().equals("")) {
                            orderBy.append(field).append(" " + orderbyArr);
                        } else {
                            orderBy.append("," + field).append(" " + orderbyArr);
                        }
                    }
                }
            }

            if (columnDefinitionNew.getColumnType().equals("用户")) {
                choorseNames.add(columnDefinitionNew.getFieldName());
            }
            if (columnDefinitionNew.getColumnType().equals("单位")) {
                unitNames.add(columnDefinitionNew.getFieldName());
            }
            i++;
        }
        Long count_ = viewDataSourceNewMap.get(tableName).count(viewColumnNews, defaultCondition,
                new HashMap<String, Object>());
        page.setTotalCount(count_);
        data = viewDataSourceNewMap.get(tableName).query(viewColumnNews, defaultCondition,
                new HashMap<String, Object>(), orderBy == null ? "" : orderBy.toString(), page);
        // 将列类型是用户的值通过调用组织机构的接口获取到用户的显示值显示到页面上
        getDataByUser(data, choorseNames);
        getDataByUnit(data, unitNames);
        return data;
    }

    /**
     * 根据关键字获取表数据
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.dyview.service.ViewDefinitionServiceBak#getSelectColumnData(java.lang.String, java.lang.String, java.lang.String, java.util.Set, com.wellsoft.pt.basicdata.dyview.entity.PageDefinition, com.wellsoft.context.jdbc.support.PagingInfo, java.util.List)
     */
    @Override
    public List<QueryItem> getSelectColumnData(String defaultCondition, String tableName,
                                               Set<ColumnDefinitionNew> columnDefinitionNews, PageDefinitionNew pageDefinitionNew, PagingInfo page,
                                               List keyWords) {
        StringBuilder selection = new StringBuilder();
        Set<String> choorseNames = new HashSet<String>();
        Set<String> unitNames = new HashSet<String>();
        if (defaultCondition != null && !defaultCondition.equals("")) {
            selection.append(defaultCondition + " and");
        }
        StringBuilder orderBy = new StringBuilder();
        int firstResult = 0;
        int maxResults = 0;
        List<String> fieldName = new ArrayList<String>();
        if (pageDefinitionNew.getIsPaging() == true) {
            firstResult = pageDefinitionNew.getPageNum() * (page.getCurrentPage() - 1);
            maxResults = pageDefinitionNew.getPageNum();
        }

        // 计数器
        int i = 0;
        for (ColumnDefinitionNew columnDefinitionNew : columnDefinitionNews) {
            String field = columnDefinitionNew.getFieldName();
            fieldName.add(field + " as " + field);
            String defaultSort = columnDefinitionNew.getDefaultSort();
            String columnDataType = columnDefinitionNew.getColumnDataType();
            if (StringUtils.isNotBlank(field) && columnDataType != "2") {
                if (i < 1) {
                    for (int j = 0; j < keyWords.size(); j++) {
                        if (j < 1) {
                            if ("20".equals(columnDataType)) {
                                List<String> users = orgApiFacade.getOrgIdsLikeName(keyWords.get(j).toString());
                                if (!users.isEmpty()) {
                                    selection.append(field + " in (");
                                    StringBuilder sb = new StringBuilder();
                                    for (int m = 0; m < users.size(); m++) {
                                        sb.append(",'" + users.get(m) + "'");
                                    }
                                    selection.append(sb.toString().replaceFirst(",", "") + ")");
                                }
                            } else {
                                selection.append(field + " like " + "'%" + keyWords.get(j) + "%'");
                            }
                        } else {
                            if ("20".equals(columnDataType)) {
                                List<String> users = orgApiFacade.getOrgIdsLikeName(keyWords.get(j).toString());
                                if (!users.isEmpty()) {
                                    selection.append(" or " + field + " in (");
                                    StringBuilder sb = new StringBuilder();
                                    for (int m = 0; m < users.size(); m++) {
                                        sb.append(",'" + users.get(m) + "'");
                                    }
                                    selection.append(sb.toString().replaceFirst(",", "") + ")");
                                }
                            } else {
                                selection.append(" or " + field + " like " + "'%" + keyWords.get(j) + "%'");
                            }
                        }
                    }
                } else {
                    for (int j = 0; j < keyWords.size(); j++) {
                        if (j < 1) {
                            if ("20".equals(columnDataType)) {
                                List<String> users = orgApiFacade.getOrgIdsLikeName(keyWords.get(j).toString());
                                if (!users.isEmpty()) {
                                    selection.append(" or " + field + " in (");
                                    StringBuilder sb = new StringBuilder();
                                    for (int m = 0; m < users.size(); m++) {
                                        sb.append(",'" + users.get(m) + "'");
                                    }
                                    selection.append(sb.toString().replaceFirst(",", "") + ")");
                                }
                            } else {
                                selection.append(" or " + field + " like " + "'%" + keyWords.get(j) + "%'");
                            }
                        } else {
                            if ("20".equals(columnDataType)) {
                                List<String> users = orgApiFacade.getOrgIdsLikeName(keyWords.get(j).toString());
                                if (!users.isEmpty()) {
                                    selection.append(" or " + field + " in (");
                                    StringBuilder sb = new StringBuilder();
                                    for (int m = 0; m < users.size(); m++) {
                                        sb.append(",'" + users.get(m) + "'");
                                    }
                                    selection.append(sb.toString().replaceFirst(",", "") + ")");
                                }
                            } else {
                                selection.append(" or " + field + " like " + "'%" + keyWords.get(j) + "%'");
                            }
                        }
                    }
                }
            }
            if (defaultSort != null && defaultSort.equals("升序")) {
                if (i < 1) {
                    orderBy.append(field);
                } else if (orderBy.toString().equals("")) {
                    orderBy.append(field);
                } else {
                    orderBy.append("," + field);
                }
            } else if (defaultSort != null && defaultSort.equals("降序")) {
                if (i < 1) {
                    orderBy.append(field);
                    orderBy.append(" desc");
                } else if (orderBy.toString().equals("")) {
                    orderBy.append(field);
                    orderBy.append(" desc");
                } else {
                    orderBy.append("," + field);
                    orderBy.append(" desc");
                }
            }
            if (columnDefinitionNew.getColumnType().equals("用户")) {
                choorseNames.add(columnDefinitionNew.getFieldName());
            }
            if (columnDefinitionNew.getColumnType().equals("单位")) {
                unitNames.add(columnDefinitionNew.getFieldName());
            }
            i++;
        }
        String[] fieldNames = fieldName.toArray(new String[fieldName.size()]);
        // 是否去掉重复的数据
        boolean distinct = true;
        List<QueryItem> totalData = dyFormApiFacade.query(tableName, distinct, fieldNames, selection.toString(), null,
                null, null, orderBy.toString(), 0, 0);
        List<QueryItem> data = dyFormApiFacade.query(tableName, distinct, fieldNames, selection.toString(), null, null,
                null, orderBy.toString(), firstResult, maxResults);
        page.setTotalCount(totalData.size());

        // 将列类型是用户的值通过调用组织机构的接口获取到用户的显示值显示到页面上
        getDataByUser(data, choorseNames);
        getDataByUnit(data, unitNames);
        return data;
    }

    /**
     * 根据关键字获取表数据
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.dyview.service.ViewDefinitionServiceBak#getSelectColumnData(java.lang.String, java.lang.String, java.lang.String, java.util.Set, com.wellsoft.pt.basicdata.dyview.entity.PageDefinition, com.wellsoft.context.jdbc.support.PagingInfo, java.util.List)
     */
    @Override
    public List<QueryItem> getSelectColumnData3(String defaultCondition, String tableName,
                                                Set<ColumnDefinitionNew> columnDefinitionNews, PageDefinitionNew pageDefinitionNew, PagingInfo page,
                                                List keyWords, String beginTime, String endTime, String searchField) {

        List<QueryItem> data = new ArrayList<QueryItem>();
        Set<String> choorseNames = new HashSet<String>();
        Set<String> unitNames = new HashSet<String>();
        StringBuilder orderBy = new StringBuilder();
        // 计数器
        int i = 0;
        List<ViewColumnNew> viewColumnNews = new ArrayList<ViewColumnNew>();
        Map<String, ViewColumnNew> map = ConvertUtils.convertElementToMap(viewDataSourceNewMap.get(tableName)
                .getAllViewColumns(), "columnAlias");
        String str = "";
        String timeSql = "";
        Map paraMap = new HashMap();
        for (ColumnDefinitionNew columnDefinitionNew : columnDefinitionNews) {
            viewColumnNews.add(map.get(columnDefinitionNew.getFieldName()));
            String field = columnDefinitionNew.getFieldName();
            ViewColumnNew viewColumnNew = map.get(field);
            field = viewColumnNew.getAttributeName();
            String defaultSort = columnDefinitionNew.getDefaultSort();
            String ColumnType = columnDefinitionNew.getColumnType();
            String columnDataType = columnDefinitionNew.getColumnDataType();
            if (field != null && !"".equals(field) && columnDataType != null && columnDataType.equals("STRING")) {
                if (field.indexOf(".") < 0 || (field.indexOf(".") > -1 && field.split("\\.")[0].length() > 1)) {
                    field = " o." + field;
                }
                for (int j = 0; j < keyWords.size(); j++) {
                    if (!StringUtils.isBlank(keyWords.get(j).toString())) {
                        if ("用户".equals(ColumnType)) {
                            List<String> users = orgApiFacade.getOrgIdsLikeName(keyWords.get(j).toString());
                            if (!users.isEmpty()) {
                                str += " or " + field + " in (";
                                StringBuilder sb = new StringBuilder();
                                for (int m = 0; m < users.size(); m++) {
                                    sb.append(",'" + users.get(m) + "'");
                                }
                                str += sb.toString().replaceFirst(",", "") + ")";
                            }
                        } else if ("单位".equals(ColumnType)) {
                            List<CommonUnit> commonUnits = unitApiFacade.getCommonUnitsByBlurUnitName(keyWords.get(j)
                                    .toString());
                            if (!commonUnits.isEmpty()) {
                                str += " or " + field + " in (";
                                StringBuilder sb = new StringBuilder();
                                for (int m = 0; m < commonUnits.size(); m++) {
                                    sb.append(",'" + commonUnits.get(m).getUnitId() + "'");
                                }
                                str += sb.toString().replaceFirst(",", "") + ")";
                            }
                        } else {
                            str += " or " + field + " like " + "'%" + keyWords.get(j) + "%'";
                        }
                    }
                }
            } else if (field != null && !"".equals(field) && columnDataType != null && columnDataType.equals("DATE")) {
                if (field.indexOf(".") < 0 || (field.indexOf(".") > -1 && field.split("\\.")[0].length() > 1)) {
                    field = " o." + field;
                }
                if (!StringUtils.isBlank(searchField) && field.indexOf(searchField) > -1) {
                    DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                    if (!StringUtils.isBlank(beginTime)) {
                        try {
                            Date startDate = format.parse(beginTime);
                            timeSql += field + " >= :startDate_" + field;
                            paraMap.put("startDate_" + field, startDate);
                        } catch (ParseException e) {
                            // TODO Auto-generated catch block
                            logger.error(e.getMessage(), e);
                        }
                    }
                    if (!StringUtils.isBlank(endTime)) {
                        try {
                            Date endDate = format.parse(endTime);
                            // if (StringUtils.isNotBlank(beginTime) &&
                            // beginTime.equals(endTime)) {
                            /**update by linz 时间相同则从00到23:59:59**/
                            SimpleDateFormat formatTime = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
                            String endDateTemp = format.format(endDate);
                            endDateTemp = endDateTemp + " 23:59:59";
                            endDate = completeDateParse(formatTime, endDateTemp);
                            // }
                            if (!StringUtils.isBlank(timeSql)) {
                                timeSql += " and " + field + " < :endDate_" + field;
                            } else {
                                timeSql += field + "< :endDate_" + field;
                            }
                            paraMap.put("endDate_" + field, endDate);
                        } catch (ParseException e) {
                            // TODO Auto-generated catch block
                            logger.error(e.getMessage(), e);
                        }
                    }
                }
            }
            if (defaultSort != null && defaultSort.equals("升序")) {
                if (i < 1) {
                    orderBy.append(field);
                } else if (orderBy.toString().equals("")) {
                    orderBy.append(field);
                } else {
                    orderBy.append("," + field);
                }
            } else if (defaultSort != null && defaultSort.equals("降序")) {
                if (i < 1) {
                    orderBy.append(field);
                    orderBy.append(" desc");
                } else if (orderBy.toString().equals("")) {
                    orderBy.append(field);
                    orderBy.append(" desc");
                } else {
                    orderBy.append("," + field);
                    orderBy.append(" desc");
                }
            }
            if (columnDefinitionNew.getColumnType().equals("用户")) {
                choorseNames.add(columnDefinitionNew.getFieldName());
            }
            if (columnDefinitionNew.getColumnType().equals("单位")) {
                unitNames.add(columnDefinitionNew.getFieldName());
            }
            i++;
        }
        if (str != null) {
            str = str.replaceFirst("or", "");
        }
        if (StringUtils.isBlank(str)) {
            str = "1=1";
        }
        if (defaultCondition != null && !defaultCondition.equals("") && columnDefinitionNews.size() > 0) {
            defaultCondition = "(" + defaultCondition + ")" + " and (" + str + ")";
            if (!StringUtils.isBlank(timeSql)) {
                defaultCondition = "(" + defaultCondition + ")" + " and " + timeSql;
            }
        } else if (defaultCondition == null && columnDefinitionNews.size() > 0) {
            defaultCondition = str;
            if (!StringUtils.isBlank(timeSql)) {
                defaultCondition = "(" + defaultCondition + ")" + " and " + timeSql;
            }
        }
        Long count_ = viewDataSourceNewMap.get(tableName).count(viewColumnNews, defaultCondition, paraMap);
        page.setTotalCount(count_);
        data = viewDataSourceNewMap.get(tableName).query(viewColumnNews, defaultCondition, paraMap,
                orderBy == null ? "" : orderBy.toString(), page);
        // 将列类型是用户的值通过调用组织机构的接口获取到用户的显示值显示到页面上
        getDataByUser(data, choorseNames);
        getDataByUnit(data, unitNames);
        return data;
    }

    /**
     * 根据传入的查询字段名查询相应的字段数据
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.dyview.service.ViewDefinitionServiceBak#getSelectData(java.lang.String, java.lang.String, java.util.Set, java.lang.String, com.wellsoft.context.jdbc.support.PagingInfo)
     */
    @Override
    public List<QueryItem> getSelectData(String formUuid, String fieldName, PagingInfo page) {
        StringBuilder selection = new StringBuilder();
        Set<String> choorseNames = new HashSet<String>();
        int firstResult = 0;
        int maxResults = 0;
        if (page != null) {
            firstResult = page.getPageSize() * (page.getCurrentPage() - 1);
            maxResults = firstResult + page.getPageSize();
        }

        List<String> fieldNames = new ArrayList<String>();

        DyFormFormDefinition formDefinition = dyFormApiFacade.getFormDefinition(formUuid);
        String tableName = formDefinition.getName();

        List<DyformFieldDefinition> fieldDefinitions = dyFormApiFacade.getFormDefinition(formUuid)
                .doGetFieldDefintions();

        for (DyformFieldDefinition fieldDefinition : fieldDefinitions) {/*
         * String
         * field
         * =
         * fieldDefinition
         * .
         * getDisplayName
         * ();
         * if
         * (field
         * !=
         * null
         * &&
         * fieldName
         * .
         * equals
         * (
         * field
         * )) {
         * fieldNames
         * .add(
         * fieldDefinition
         * .
         * getName
         * () +
         * " as value"
         * );
         * selection
         * .
         * append
         * (
         * fieldDefinition
         * .
         * getName
         * () +
         * " is not null"
         * ); //
         * System
         * .out.
         * println
         * (
         * fieldDefinition
         * .
         * getType
         * ());
         * if (
         * fieldDefinition
         * .
         * getDbDataType
         * (
         * ).equals
         * (
         * "20")
         * ) {
         * choorseNames
         * .add(
         * "value"
         * ); }
         * }
         */
        }
        String[] fieldData = fieldNames.toArray(new String[fieldNames.size()]);
        // 是否去掉重复的数据
        boolean distinct = true;
        List<QueryItem> data = dyFormApiFacade.query(tableName, distinct, fieldData, selection.toString(), null, null,
                null, null, firstResult, maxResults);
        // 将列类型是用户的值通过调用组织机构的接口获取到用户的显示值显示到页面上
        for (QueryItem map : data) {
            Iterator ite = map.keySet().iterator();
            QueryItem obj = new QueryItem();
            Map<String, MultiOrgUserAccount> userMap = new HashMap<String, MultiOrgUserAccount>();
            Set<String> users = new HashSet<String>();
            Set<String> depts = new HashSet<String>();
            while (ite.hasNext()) {
                String key = ite.next().toString();
                // obj.put(key, map.get(key));
                for (String chooseName : choorseNames) {
                    users.clear();
                    depts.clear();
                    if (key.equals(chooseName)) {
                        String chooseValue = (String) map.get(key);
                        if (chooseValue != null) {
                            String[] chooseValueSplit = chooseValue.split(";");
                            for (int j = 0; j < chooseValueSplit.length; j++) {
                                if (chooseValueSplit[j].startsWith(IdPrefix.USER.getValue())) {
                                    users.add(chooseValueSplit[j]);
                                } else if (chooseValueSplit[j].startsWith(IdPrefix.DEPARTMENT.getValue())) {
                                    depts.add(chooseValueSplit[j]);
                                }
                            }
                            StringBuilder descNames = new StringBuilder();
                            StringBuilder descNamesUser = new StringBuilder();
                            if (users.size() > 0) {
                                List<MultiOrgUserAccount> user = orgApiFacade.queryUserAccountListByIds(users);
                                userMap = ConvertUtils.convertElementToMap(user, "id");

                                Iterator<MultiOrgUserAccount> it = user.iterator();
                                while (it.hasNext()) {
                                    String descName = it.next().getUserName();
                                    descNamesUser.append(descName);
                                    if (it.hasNext()) {
                                        descNamesUser.append(";");
                                    }
                                }
                            }
                            StringBuilder descNameDept = new StringBuilder();
                            if (depts.size() > 0) {
                                List<MultiOrgElement> dept = orgApiFacade.queryOrgElementListByIds(depts);
                                Iterator<MultiOrgElement> it2 = dept.iterator();
                                while (it2.hasNext()) {

                                    String descName = it2.next().getName();
                                    descNameDept.append(descName);
                                    if (it2.hasNext()) {
                                        descNameDept.append(";");
                                    }
                                }
                            }
                            // System.out.println(descNamesUser.toString());
                            if (descNamesUser.toString().equals("")) {
                                descNames.append(descNameDept.toString());
                            } else {
                                descNames.append(descNamesUser.toString() + ";" + descNameDept.toString());
                            }

                            map.put("value", descNames.toString());
                        }
                    }
                }
            }

        }
        return data;
    }

    /**
     * 根据条件备选项来获取对应的表数据
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.dyview.service.ViewDefinitionServiceBak#getCondColumnData(java.lang.String, java.util.Set, com.wellsoft.pt.basicdata.dyview.entity.PageDefinition, com.wellsoft.context.jdbc.support.PagingInfo, java.lang.String)
     */
    @Override
    public List<QueryItem> getCondColumnData(String defaultCondition, String tableName,
                                             Set<ColumnDefinitionNew> columnDefinitionNews, PageDefinitionNew pageDefinitionNew, PagingInfo page,
                                             String condValue, String appointColumn) {
        StringBuilder selection = new StringBuilder();
        Set<String> choorseNames = new HashSet<String>();
        Set<String> unitNames = new HashSet<String>();
        if (defaultCondition != null && !defaultCondition.equals("")) {
            selection.append(defaultCondition + " and");
        }
        StringBuilder orderBy = new StringBuilder();
        int firstResult = 0;
        int maxResults = 0;
        List<String> fieldName = new ArrayList<String>();
        if (pageDefinitionNew.getIsPaging() == true) {
            firstResult = pageDefinitionNew.getPageNum() * (page.getCurrentPage() - 1);
            maxResults = pageDefinitionNew.getPageNum();
        }

        // 计数器
        int i = 0;
        for (ColumnDefinitionNew columnDefinitionNew : columnDefinitionNews) {

            String title = columnDefinitionNew.getTitleName();
            String fieldValue = columnDefinitionNew.getFieldName();
            fieldName.add(fieldValue + " as " + fieldValue);
            if (title.equals(appointColumn)) {
                String field = columnDefinitionNew.getFieldName();

                String defaultSort = columnDefinitionNew.getDefaultSort();
                if (field != null && !"".equals(field)) {
                    selection.append(field + " like " + "'%" + condValue + "%'");
                }
                if (defaultSort != null && defaultSort.equals("升序")) {
                    if (i < 1) {
                        orderBy.append(field);
                    } else if (orderBy.toString().equals("")) {
                        orderBy.append(field);
                    } else {
                        orderBy.append(field);
                    }
                } else if (defaultSort != null && defaultSort.equals("降序")) {
                    if (i < 1) {
                        orderBy.append(field);
                        orderBy.append(" desc");
                    } else if (orderBy.toString().equals("")) {
                        orderBy.append(field);
                        orderBy.append(" desc");
                    } else {
                        orderBy.append(field);
                        orderBy.append(" desc");
                    }
                }
            }
            if (columnDefinitionNew.getColumnType().equals("用户")) {
                choorseNames.add(columnDefinitionNew.getFieldName());
            }
            if (columnDefinitionNew.getColumnType().equals("单位")) {
                unitNames.add(columnDefinitionNew.getFieldName());
            }
            i++;
        }
        // System.out.println(selection);
        String[] fieldNames = fieldName.toArray(new String[fieldName.size()]);
        // 是否去掉重复的数据
        boolean distinct = true;

        List<QueryItem> data = dyFormApiFacade.query(tableName, distinct, fieldNames, selection.toString(), null, null,
                null, orderBy.toString(), firstResult, maxResults);
        // 将列类型是用户的值通过调用组织机构的接口获取到用户的显示值显示到页面上
        getDataByUser(data, choorseNames);
        getDataByUnit(data, unitNames);
        return data;
    }

    /**
     * 根据条件备选项来获取对应的表数据
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.dyview.service.ViewDefinitionServiceBak#getCondColumnData(java.lang.String, java.util.Set, com.wellsoft.pt.basicdata.dyview.entity.PageDefinition, com.wellsoft.context.jdbc.support.PagingInfo, java.lang.String)
     */
    @Override
    public List<QueryItem> getCondColumnData3(String defaultCondition, String tableName,
                                              Set<ColumnDefinitionNew> columnDefinitionNews, PageDefinitionNew pageDefinitionNew, PagingInfo page,
                                              String condValue, String appointColumn) {
        List<QueryItem> data = new ArrayList<QueryItem>();
        Set<String> choorseNames = new HashSet<String>();
        StringBuilder orderBy = new StringBuilder();

        List<ViewColumnNew> viewColumnNews = new ArrayList<ViewColumnNew>();
        Map<String, ViewColumnNew> map = ConvertUtils.convertElementToMap(viewDataSourceNewMap.get(tableName)
                .getAllViewColumns(), "columnAlias");
        for (ColumnDefinitionNew columnDefinitionNew : columnDefinitionNews) {
            viewColumnNews.add(map.get(columnDefinitionNew.getFieldName()));
            String field = columnDefinitionNew.getFieldName();
            ViewColumnNew viewColumnNew = map.get(field);
            field = viewColumnNew.getAttributeName();
            String title = columnDefinitionNew.getTitleName();
            if (title != null && !"".equals(title) && title.equals(appointColumn)) {
                if (defaultCondition.equals("")) {
                    defaultCondition += field + " like " + "'%" + condValue + "%'";
                } else {
                    defaultCondition += " or " + field + " like " + "'%" + condValue + "%'";
                }
            }
        }
        data = viewDataSourceNewMap.get(tableName).query(viewColumnNews, defaultCondition,
                new HashMap<String, Object>(), orderBy == null ? "" : orderBy.toString(), page);
        // 将列类型是用户的值通过调用组织机构的接口获取到用户的显示值显示到页面上
        getDataByUser(data, choorseNames);
        return data;
    }

    /**
     * 根据日期备选项来获取对应的表数据<无用>
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.dyview.service.ViewDefinitionServiceBak#getDateColumnData(java.lang.String, java.util.Set, com.wellsoft.pt.basicdata.dyview.entity.PageDefinition, com.wellsoft.context.jdbc.support.PagingInfo, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public List<QueryItem> getDateColumnData(String defaultCondition, String tableName,
                                             Set<ColumnDefinitionNew> columnDefinitionNews, PageDefinitionNew pageDefinitionNew, PagingInfo page,
                                             String beginTime, String endTime, String appointColumn) {
        StringBuilder selection = new StringBuilder();
        Set<String> choorseNames = new HashSet<String>();
        Set<String> unitNames = new HashSet<String>();
        if (defaultCondition != null && !defaultCondition.equals("")) {
            selection.append(defaultCondition + " and");
        }
        StringBuilder orderBy = new StringBuilder();
        Map<String, Object> selectionArgs = new HashMap<String, Object>();
        int firstResult = 0;
        int maxResults = 0;
        List<String> fieldName = new ArrayList<String>();
        if (pageDefinitionNew.getIsPaging() == true) {
            firstResult = pageDefinitionNew.getPageNum() * (page.getCurrentPage() - 1);
            maxResults = pageDefinitionNew.getPageNum();
        }

        // 计数器
        int i = 0;
        for (ColumnDefinitionNew columnDefinitionNew : columnDefinitionNews) {

            String title = columnDefinitionNew.getTitleName();
            String fieldValue = columnDefinitionNew.getFieldName();
            fieldName.add(fieldValue + " as " + fieldValue);
            if (title.equals(appointColumn)) {
                String field = columnDefinitionNew.getFieldName();

                String defaultSort = columnDefinitionNew.getDefaultSort();
                if (field != null && !"".equals(field) && beginTime != "" && endTime != "") {
                    selection.append(field + " between :beginTime and :endTime");
                    selectionArgs.put("beginTime", beginTime);
                    selectionArgs.put("endTime", endTime);
                }

                if (defaultSort != null && defaultSort.equals("升序")) {
                    if (i < 1) {
                        orderBy.append(field);
                    } else if (orderBy.toString().equals("")) {
                        orderBy.append(field);
                    } else {
                        orderBy.append(field);
                    }
                } else if (defaultSort != null && defaultSort.equals("降序")) {
                    if (i < 1) {
                        orderBy.append(field);
                        orderBy.append(" desc");
                    } else if (orderBy.toString().equals("")) {
                        orderBy.append(field);
                        orderBy.append(" desc");
                    } else {
                        orderBy.append(field);
                        orderBy.append(" desc");
                    }
                }
            }
            if (columnDefinitionNew.getColumnType().equals("用户")) {
                choorseNames.add(columnDefinitionNew.getFieldName());
            }
            if (columnDefinitionNew.getColumnType().equals("单位")) {
                unitNames.add(columnDefinitionNew.getFieldName());
            }
            i++;
        }
        // System.out.println(selection);
        String[] fieldNames = fieldName.toArray(new String[fieldName.size()]);
        // 是否去掉重复的数据
        boolean distinct = true;

        List<QueryItem> data = dyFormApiFacade.query(tableName, distinct, fieldNames, selection.toString(),
                selectionArgs, null, null, orderBy.toString(), firstResult, maxResults);
        // 将列类型是用户的值通过调用组织机构的接口获取到用户的显示值显示到页面上
        getDataByUser(data, choorseNames);
        getDataByUnit(data, unitNames);
        return data;
    }

    /**
     * 获取系统表的日期数据<弃用>
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.dyview.service.ViewDefinitionServiceBak#getDateColumnData2(java.lang.String, java.lang.String, java.util.Set, com.wellsoft.pt.basicdata.dyview.entity.PageDefinition, com.wellsoft.context.jdbc.support.PagingInfo, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public List<QueryItem> getDateColumnData2(String tableUuid, String defaultCondition,
                                              Set<ColumnDefinitionNew> columnDefinitionNews, PageDefinitionNew pageDefinitionNew, PagingInfo page,
                                              String beginTime, String endTime, String appointColumn) {
        StringBuilder selection = new StringBuilder();
        Set<String> choorseNames = new HashSet<String>();
        Set<String> unitNames = new HashSet<String>();
        if (defaultCondition != null && !defaultCondition.equals("")) {
            selection.append(defaultCondition + " and");
        }
        StringBuilder orderBy = new StringBuilder();
        Map<String, Object> selectionArgs = new HashMap<String, Object>();
        int firstResult = 0;
        int maxResults = 0;
        List<String> fieldName = new ArrayList<String>();
        if (pageDefinitionNew.getIsPaging() == true) {
            firstResult = pageDefinitionNew.getPageNum() * (page.getCurrentPage() - 1);
            maxResults = pageDefinitionNew.getPageNum();
        }

        // 计数器
        int i = 0;
        for (ColumnDefinitionNew columnDefinitionNew : columnDefinitionNews) {

            String title = columnDefinitionNew.getTitleName();
            String fieldValue = columnDefinitionNew.getFieldName();
            fieldName.add(fieldValue);
            if (title.equals(appointColumn)) {
                String field = columnDefinitionNew.getFieldName();

                String defaultSort = columnDefinitionNew.getDefaultSort();
                if (field != null && !"".equals(field) && beginTime != "" && endTime != "") {
                    selection.append(field + " between :beginTime and :endTime");
                    selectionArgs.put("beginTime", beginTime);
                    selectionArgs.put("endTime", endTime);
                }

                if (defaultSort != null && defaultSort.equals("升序")) {
                    if (i < 1) {
                        orderBy.append(field);
                    } else if (orderBy.equals("")) {
                        orderBy.append(field);
                    } else {
                        orderBy.append(field);
                    }
                } else if (defaultSort != null && defaultSort.equals("降序")) {
                    if (i < 1) {
                        orderBy.append(field);
                        orderBy.append(" desc");
                    } else if (orderBy.toString().equals("")) {
                        orderBy.append(field);
                        orderBy.append(" desc");
                    } else {
                        orderBy.append(field);
                        orderBy.append(" desc");
                    }
                }
            }
            if (columnDefinitionNew.getColumnType().equals("用户")) {
                choorseNames.add(columnDefinitionNew.getFieldName());
            }
            if (columnDefinitionNew.getColumnType().equals("单位")) {
                unitNames.add(columnDefinitionNew.getFieldName());
            }
            i++;
        }
        // System.out.println(selection);
        String[] fieldNames = fieldName.toArray(new String[fieldName.size()]);
        // 是否去掉重复的数据
        boolean distinct = true;
        List<QueryItem> data = null;
        // 将列类型是用户的值通过调用组织机构的接口获取到用户的显示值显示到页面上
        // getDataByUser2(data, choorseNames);
        getDataByUnit(data, unitNames);
        return data;
    }

    /**
     * 根据uuid获取视图对应的列的数据 (调用系统表接口)
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.dyview.service.ViewDefinitionServiceBak#getColumnData2(java.lang.String, java.lang.String, java.util.Set, com.wellsoft.pt.basicdata.dyview.entity.PageDefinition, com.wellsoft.context.jdbc.support.PagingInfo)
     */
    @Override
    public List<QueryItem> getColumnData2(String tableUuid, String defaultCondition,
                                          Set<ColumnDefinitionNew> columnDefinitionNews, String rowIdKey, String roleType, String roleValue,
                                          PageDefinitionNew pageDefinitionNew, DyViewQueryInfoNew dyViewQueryInfoNew, String count) {
        return null;
    }

    /**
     * 系统表方法获取点击排序的数据
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.dyview.service.ViewDefinitionServiceBak#getSortColumnData2(java.lang.String, java.lang.String, java.util.Set, com.wellsoft.pt.basicdata.dyview.entity.PageDefinition, com.wellsoft.context.jdbc.support.PagingInfo)
     */
    @Override
    public List<QueryItem> getSortColumnData2(String tableUuid, String defaultCondition,
                                              Set<ColumnDefinitionNew> columnDefinitionNews, String rowIdKey, String title,
                                              PageDefinitionNew pageDefinitionNew, PagingInfo page, String roleType, String roleValue, String orderbyArr) {
        List<QueryItem> queryItems = new ArrayList<QueryItem>();
        StringBuilder selection = new StringBuilder();
        Set<String> choorseNames = new HashSet<String>();
        Set<String> unitNames = new HashSet<String>();
        Map<String, String> aclChoorseNames = new HashMap<String, String>();
        com.wellsoft.pt.security.acl.support.QueryInfo aclQueryInfo = new com.wellsoft.pt.security.acl.support.QueryInfo();
        if (defaultCondition != null && !defaultCondition.equals("")) {
            selection.append(defaultCondition);
            aclQueryInfo.setWhereHql(selection.toString());
        }
        if (pageDefinitionNew.getIsPaging() == true) {
            // 设置当前页
            aclQueryInfo.getPage().setPageNo(page.getCurrentPage());
            // 设置每页的页数
            aclQueryInfo.getPage().setPageSize(pageDefinitionNew.getPageNum());
        }
        StringBuilder selectionHql = new StringBuilder();
        StringBuilder orderBy = new StringBuilder();
        Class<IdEntity> Entityname = null;
        List<SystemTableRelationship> relationShip = getViewDataNewService.getAttributesByrelationship(tableUuid);

        SystemTable st = basicDataApiFacade.getTable(tableUuid);
        String moduleId = st.getModuleName();// 模块id
        // 多表时
        if (relationShip.size() != 0) {
            for (int index = 0; index < relationShip.size(); index++) {
                String mainTableName = relationShip.get(index).getMainTableName();// 主表名
                try {
                    Entityname = (Class<IdEntity>) Class.forName(mainTableName);
                } catch (ClassNotFoundException e) {
                    logger.error(e.getMessage(), e);
                }
                String SecondaryTableName = relationShip.get(index).getSecondaryTableName();// 从表名
                String associateAttribute = relationShip.get(index).getAssociatedAttributes();// 关联属性
                String tableRelationShip = relationShip.get(index).getTableRelationship();

                String[] mainTNs = mainTableName.split("entity.");
                String[] secondaryTNs = SecondaryTableName.split("entity.");
                // 计数器
                int i = 0;
                for (ColumnDefinitionNew columnDefinitionNew : columnDefinitionNews) {
                    String field = columnDefinitionNew.getFieldName();
                    String entityName = columnDefinitionNew.getEntityName();
                    String columnAlias = columnDefinitionNew.getColumnAliase();
                    String defaultSort = columnDefinitionNew.getDefaultSort();

                    // if (tableRelationShip.equals("一对一")) {
                    if (i < 1) {
                        for (int j = 1; j < mainTNs.length; j++) {
                            String mainTN = mainTNs[1].toUpperCase();
                            String entityN = entityName.toUpperCase();
                            String secondaryTN = secondaryTNs[1];
                            if (mainTN.equals(entityN)) {
                                selectionHql.append("o." + field + " as " + columnAlias);
                                if (title.equals(columnDefinitionNew.getOtherName())) {
                                    aclQueryInfo.addOrderby(field + " " + orderbyArr);
                                }
                            } else {
                                selectionHql.append("o." + associateAttribute + "." + field + " as " + columnAlias);
                                if (title.equals(columnDefinitionNew.getOtherName())) {
                                    aclQueryInfo.addOrderby(associateAttribute + "." + field + " " + orderbyArr);
                                }
                            }
                        }
                    } else {
                        for (int j = 1; j < mainTNs.length; j++) {
                            String mainTN = mainTNs[1].toUpperCase();
                            String entityN = entityName.toUpperCase();
                            String secondaryTN = secondaryTNs[1];
                            if (mainTN.equals(entityN)) {
                                selectionHql.append(",o." + field + " as " + columnAlias);
                                if (title.equals(columnDefinitionNew.getOtherName())) {
                                    aclQueryInfo.addOrderby(field + " " + orderbyArr);
                                }

                            } else {
                                selectionHql.append(",o." + associateAttribute + "." + field + " as " + columnAlias);
                                if (title.equals(columnDefinitionNew.getOtherName())) {
                                    aclQueryInfo.addOrderby(associateAttribute + "." + field + " " + orderbyArr);
                                }
                            }
                        }
                    }
                    // }

                    if (columnDefinitionNew.getColumnType().equals("用户")) {
                        choorseNames.add(columnDefinitionNew.getColumnAliase());
                    }
                    if (columnDefinitionNew.getColumnType().equals("单位")) {
                        unitNames.add(columnDefinitionNew.getColumnAliase());
                    }
                    if (columnDefinitionNew.getColumnType().equals("acl用户")) {
                        String value = columnDefinitionNew.getValue();
                        if (StringUtils.isNotBlank(value) && value.indexOf("|") != -1) {
                            String[] values = StringUtils.split(value, "|");
                            if (values.length == 2 && StringUtils.isNotBlank(values[1])) {
                                aclChoorseNames.put(columnDefinitionNew.getColumnAliase() + "entityClass", values[0]);
                                aclChoorseNames.put(columnDefinitionNew.getColumnAliase(), values[1]);
                            }
                        }
                    }
                    i++;
                }
            }
        }
        // 单表时
        else {
            String mainTableName = st.getFullEntityName();// 主表名
            try {
                Entityname = (Class<IdEntity>) Class.forName(mainTableName);
            } catch (ClassNotFoundException e) {
                logger.error(e.getMessage(), e);
            }

            // 计数器
            int i = 0;
            for (ColumnDefinitionNew columnDefinitionNew : columnDefinitionNews) {
                String field = columnDefinitionNew.getFieldName();
                String columnAlias = columnDefinitionNew.getColumnAliase();

                // if (tableRelationShip.equals("一对一")) {
                if (i < 1) {
                    selectionHql.append("o." + field + " as " + columnAlias);
                    if (title.equals(columnDefinitionNew.getOtherName())) {
                        aclQueryInfo.addOrderby(field + " " + orderbyArr);
                    }
                } else {
                    selectionHql.append(",o." + field + " as " + columnAlias);
                    if (title.equals(columnDefinitionNew.getOtherName())) {
                        aclQueryInfo.addOrderby(field + " " + orderbyArr);
                    }

                }
                // }

                if (columnDefinitionNew.getColumnType().equals("用户")) {
                    choorseNames.add(columnDefinitionNew.getColumnAliase());
                }
                if (columnDefinitionNew.getColumnType().equals("单位")) {
                    unitNames.add(columnDefinitionNew.getColumnAliase());
                }
                if (columnDefinitionNew.getColumnType().equals("acl用户")) {
                    String value = columnDefinitionNew.getValue();
                    if (StringUtils.isNotBlank(value) && value.indexOf("|") != -1) {
                        String[] values = StringUtils.split(value, "|");
                        if (values.length == 2 && StringUtils.isNotBlank(values[1])) {
                            aclChoorseNames.put(columnDefinitionNew.getColumnAliase() + "entityClass", values[0]);
                            aclChoorseNames.put(columnDefinitionNew.getColumnAliase(), values[1]);
                        }
                    }
                }
                i++;
            }

        }

        aclQueryInfo.setWhereHql(selection.toString());
        aclQueryInfo.setSelectionHql(selectionHql.toString());
        // 处理权限角色值
        List<Permission> roTypeList = new ArrayList<Permission>();
        String[] roTypeArray = roleType.split(";");
        for (int i = 0; i < roTypeArray.length; i++) {
            roTypeList.add(new AclPermission(Integer.valueOf(roTypeArray[i])));
        }
        List<String> sids = new ArrayList<String>();
        sids.add(SpringSecurityUtils.getCurrentUserId());

        if (roleValue.equals("person")) {
            queryItems = aclService.queryForItem(Entityname, aclQueryInfo, roTypeList, sids);
            page.setTotalCount(aclQueryInfo.getPage().getTotalCount());
            getDataByUser(queryItems, choorseNames);
            getDataByUnit(queryItems, unitNames);
            getDataByAclUser(rowIdKey, queryItems, aclChoorseNames);
        } else if (roleValue.equals("group")) {
            queryItems = aclService.queryForItem(Entityname, aclQueryInfo, roTypeList,
                    SpringSecurityUtils.getCurrentUserId(), moduleId);
            page.setTotalCount(aclQueryInfo.getPage().getTotalCount());
            getDataByUser(queryItems, choorseNames);
            getDataByUnit(queryItems, unitNames);
            getDataByAclUser(rowIdKey, queryItems, aclChoorseNames);
        } else if (roleValue.equals("all")) {
            queryItems = aclService.queryAllForItem(Entityname, aclQueryInfo, roTypeList,
                    SpringSecurityUtils.getCurrentUserId(), moduleId);
            page.setTotalCount(aclQueryInfo.getPage().getTotalCount());
            getDataByUser(queryItems, choorseNames);
            getDataByUnit(queryItems, unitNames);
            getDataByAclUser(rowIdKey, queryItems, aclChoorseNames);
        }
        return queryItems;
    }

    /**
     * 根据查询关键字条件获取对应的表数据
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.dyview.service.ViewDefinitionServiceBak#getSelectColumnData2(java.lang.String, java.lang.String, java.util.Set, com.wellsoft.pt.basicdata.dyview.entity.PageDefinition, com.wellsoft.context.jdbc.support.PagingInfo, java.util.List)
     */
    @Override
    public List<QueryItem> getSelectColumnData2(String tableUuid, String defaultCondition,
                                                Set<ColumnDefinitionNew> columnDefinitionNews, String rowIdKey, PageDefinitionNew pageDefinitionNew,
                                                PagingInfo page, List keyWords, String roleType, String roleValue, String beginTime, String endTime,
                                                String searchField) {
        List<QueryItem> queryItems = new ArrayList<QueryItem>();

        Set<String> choorseNames = new HashSet<String>();
        Set<String> unitNames = new HashSet<String>();
        Map<String, String> aclChoorseNames = new HashMap<String, String>();
        com.wellsoft.pt.security.acl.support.QueryInfo aclQueryInfo = new com.wellsoft.pt.security.acl.support.QueryInfo();
        // 设置当前页
        aclQueryInfo.getPage().setPageNo(page.getCurrentPage());
        // 设置每页的页数
        aclQueryInfo.getPage().setPageSize(pageDefinitionNew.getPageNum());
        StringBuilder selection = new StringBuilder();
        StringBuilder selectionHql = new StringBuilder();
        String timeSql = "";
        StringBuilder orderBy = new StringBuilder();
        Class<IdEntity> Entityname = null;
        List<SystemTableRelationship> relationShip = getViewDataNewService.getAttributesByrelationship(tableUuid);

        SystemTable st = basicDataApiFacade.getTable(tableUuid);
        String moduleId = st.getModuleName();// 模块id
        if (relationShip.size() != 0) {
            for (int index = 0; index < relationShip.size(); index++) {
                String mainTableName = relationShip.get(index).getMainTableName();// 主表名
                try {
                    Entityname = (Class<IdEntity>) Class.forName(mainTableName);
                } catch (ClassNotFoundException e) {
                    logger.error(e.getMessage(), e);
                }
                String SecondaryTableName = relationShip.get(index).getSecondaryTableName();// 从表名
                String associateAttribute = relationShip.get(index).getAssociatedAttributes();// 关联属性
                String tableRelationShip = relationShip.get(index).getTableRelationship();// 表关系

                String[] mainTNs = mainTableName.split("entity.");
                String[] secondaryTNs = SecondaryTableName.split("entity.");
                for (ColumnDefinitionNew columnDefinitionNew : columnDefinitionNews) {
                    String field = columnDefinitionNew.getFieldName();
                    String entityName = columnDefinitionNew.getEntityName();
                    String columnAlias = columnDefinitionNew.getColumnAliase();
                    String defaultSort = columnDefinitionNew.getDefaultSort();
                    String columnName = columnDefinitionNew.getTitleName();
                    // for (int j = 1; j < mainTNs.length; j++) {
                    String mainTN = mainTNs[1].toUpperCase();
                    String entityN = entityName.toUpperCase();
                    String secondaryTN = secondaryTNs[1].toUpperCase();
                    if (mainTN.equals(entityN)) {
                        selectionHql.append(",o." + field + " as " + columnAlias);
                        // 默认排序
                        if (defaultSort != null) {
                            if (defaultSort.equals(DyviewConfigNew.DYVIEW_SORT_ASC)) {
                                orderBy.append("," + field).append(" asc");
                            } else if (defaultSort.equals(DyviewConfigNew.DYVIEW_SORT_DESC)) {
                                orderBy.append("," + field).append(" desc");
                            }
                        }

                        for (int l = 0; l < keyWords.size(); l++) {
                            if (columnDefinitionNew.getColumnDataType().equals("STRING")
                                    && !StringUtils.isBlank(keyWords.get(l).toString())) {
                                if ("用户".equals(columnDefinitionNew.getColumnType())) {
                                    List<String> users = orgApiFacade.getOrgIdsLikeName(keyWords.get(l).toString());
                                    if (!users.isEmpty()) {
                                        selection.append(" or o." + field + " in " + "(:" + columnAlias + ")");
                                        StringBuilder sb = new StringBuilder();
                                        for (int m = 0; m < users.size(); m++) {
                                            sb.append("," + users.get(m));
                                        }
                                        aclQueryInfo.addQueryParams(columnAlias, sb.toString().replaceFirst(",", ""));
                                    }
                                } else {
                                    selection.append(" or " + "o." + field + " like " + ":" + columnAlias);
                                    aclQueryInfo
                                            .addQueryParams(
                                                    columnAlias,
                                                    "%"
                                                            + columnDefinitionNew.getColumnDataType((String) keyWords
                                                            .get(l)) + "%");
                                }
                            } else if (columnDefinitionNew.getColumnDataType().equals("DATE")
                                    && !StringUtils.isBlank(searchField) && searchField.equals(field)) {
                                DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                                if (!StringUtils.isBlank(beginTime) && StringUtils.isBlank(endTime)) {
                                    try {
                                        Date startDate = format.parse(beginTime);
                                        timeSql += " o." + field + " >= :beginTime_" + field;
                                        aclQueryInfo.addQueryParams("beginTime_" + field, startDate);
                                    } catch (ParseException e) {
                                        // TODO Auto-generated catch block
                                        logger.error(e.getMessage(), e);
                                    }
                                } else if (StringUtils.isBlank(beginTime) && !StringUtils.isBlank(endTime)) {
                                    try {
                                        Date endDate = format.parse(endTime);
                                        timeSql += " o." + field + " <= :endTime_" + field;
                                        aclQueryInfo.addQueryParams("endTime_" + field, endDate);
                                    } catch (ParseException e) {
                                        // TODO Auto-generated catch block
                                        logger.error(e.getMessage(), e);
                                    }
                                } else if (!StringUtils.isBlank(beginTime) && !StringUtils.isBlank(endTime)) {
                                    try {
                                        Date startDate = format.parse(beginTime);
                                        Date endDate = format.parse(endTime);
                                        // if (StringUtils.isNotBlank(beginTime)
                                        // && beginTime.equals(endTime)) {
                                        /**update by linz 时间相同则从00到23:59:59**/
                                        DateFormat formatTime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                                        String endDateTemp = format.format(endDate);
                                        endDateTemp = endDateTemp + " 23:59:59";
                                        endDate = completeDateParse(formatTime, endDateTemp);
                                        // }
                                        timeSql += " o." + field + " >= :beginTime_" + field;
                                        timeSql += " and o." + field + " <= :endTime_" + field;
                                        aclQueryInfo.addQueryParams("beginTime_" + field, startDate);
                                        aclQueryInfo.addQueryParams("endTime_" + field, endDate);
                                    } catch (ParseException e) {
                                        // TODO Auto-generated catch block
                                        logger.error(e.getMessage(), e);
                                    }
                                }
                            }
                        }
                    } else if (secondaryTN.equals(entityN)) {
                        selectionHql.append(",o." + associateAttribute + "." + field + " as " + columnAlias);
                        if (defaultSort != null && defaultSort.equals("升序")) {
                            if (orderBy.toString().equals("")) {
                                aclQueryInfo.addOrderby(associateAttribute + "." + field);
                            } else {
                                aclQueryInfo.addOrderby("," + associateAttribute + "." + field);
                            }
                        }
                        if (defaultSort != null && defaultSort.equals("降序")) {
                            if (orderBy.toString().equals("")) {
                                aclQueryInfo.addOrderby(associateAttribute + "." + field, "desc");
                            } else {
                                aclQueryInfo.addOrderby("," + associateAttribute + "." + field, "desc");
                            }
                        }
                        for (int l = 0; l < keyWords.size(); l++) {
                            if (columnDefinitionNew.getColumnDataType().equals("STRING")
                                    && !StringUtils.isBlank(keyWords.get(l).toString())) {
                                if ("用户".equals(columnDefinitionNew.getColumnType())) {
                                    List<String> users = orgApiFacade.getOrgIdsLikeName(keyWords.get(l).toString());
                                    if (!users.isEmpty()) {
                                        selection.append(" or o." + associateAttribute + "." + field + " in " + "(:"
                                                + columnAlias + ")");
                                        StringBuilder sb = new StringBuilder();
                                        for (int m = 0; m < users.size(); m++) {
                                            sb.append("," + users.get(m));
                                        }
                                        aclQueryInfo.addQueryParams(columnAlias, sb.toString().replaceFirst(",", ""));
                                    }
                                } else {
                                    selection.append(" or " + "o." + associateAttribute + "." + field + " like " + ":"
                                            + columnAlias);
                                    aclQueryInfo
                                            .addQueryParams(
                                                    columnAlias,
                                                    "%"
                                                            + columnDefinitionNew.getColumnDataType((String) keyWords
                                                            .get(l)) + "%");
                                }
                            } else if (columnDefinitionNew.getColumnDataType().equals("DATE")
                                    && !StringUtils.isBlank(searchField) && searchField.equals(field)) {
                                DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                                if (!StringUtils.isBlank(beginTime) && StringUtils.isBlank(endTime)) {
                                    try {
                                        Date startDate = format.parse(beginTime);
                                        timeSql += " o." + associateAttribute + "." + field + " >= :beginTime_" + field;
                                        aclQueryInfo.addQueryParams("beginTime_" + field, startDate);
                                    } catch (ParseException e) {
                                        // TODO Auto-generated catch block
                                        logger.error(e.getMessage(), e);
                                    }
                                } else if (StringUtils.isBlank(beginTime) && !StringUtils.isBlank(endTime)) {
                                    try {
                                        Date endDate = format.parse(endTime);
                                        timeSql += " o." + associateAttribute + "." + field + " <= :endTime_" + field;
                                        aclQueryInfo.addQueryParams("endTime_" + field, endDate);
                                    } catch (ParseException e) {
                                        // TODO Auto-generated catch block
                                        logger.error(e.getMessage(), e);
                                    }
                                } else if (!StringUtils.isBlank(beginTime) && !StringUtils.isBlank(endTime)) {
                                    try {
                                        Date startDate = format.parse(beginTime);
                                        Date endDate = format.parse(endTime);
                                        // if (StringUtils.isNotBlank(beginTime)
                                        // && beginTime.equals(endTime)) {
                                        /**update by linz 时间相同则从00到23:59:59**/
                                        DateFormat formatTime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                                        String endDateTemp = format.format(endDate);
                                        endDateTemp = endDateTemp + " 23:59:59";
                                        endDate = completeDateParse(formatTime, endDateTemp);
                                        // }
                                        timeSql += " o." + associateAttribute + "." + field + " >= :beginTime_" + field;
                                        timeSql += " and o." + associateAttribute + "." + field + " <= :endTime_"
                                                + field;
                                        aclQueryInfo.addQueryParams("beginTime_" + field, startDate);
                                        aclQueryInfo.addQueryParams("endTime_" + field, endDate);
                                    } catch (ParseException e) {
                                        // TODO Auto-generated catch block
                                        logger.error(e.getMessage(), e);
                                    }
                                }
                            }
                        }
                    }
                    // }
                    if (columnDefinitionNew.getColumnType().equals("用户")) {
                        choorseNames.add(columnDefinitionNew.getColumnAliase());
                    }
                    if (columnDefinitionNew.getColumnType().equals("单位")) {
                        unitNames.add(columnDefinitionNew.getColumnAliase());
                    }
                    if (columnDefinitionNew.getColumnType().equals("acl用户")) {
                        String value = columnDefinitionNew.getValue();
                        if (StringUtils.isNotBlank(value) && value.indexOf("|") != -1) {
                            String[] values = StringUtils.split(value, "|");
                            if (values.length == 2 && StringUtils.isNotBlank(values[1])) {
                                aclChoorseNames.put(columnDefinitionNew.getColumnAliase() + "entityClass", values[0]);
                                aclChoorseNames.put(columnDefinitionNew.getColumnAliase(), values[1]);
                            }
                        }
                    }
                }
            }
        }
        // 单表时
        else {
            String mainTableName = st.getFullEntityName();// 主表名
            try {
                Entityname = (Class<IdEntity>) Class.forName(mainTableName);
            } catch (ClassNotFoundException e) {
                logger.error(e.getMessage(), e);
            }
            for (ColumnDefinitionNew columnDefinitionNew : columnDefinitionNews) {
                String field = columnDefinitionNew.getFieldName();
                String columnAlias = columnDefinitionNew.getColumnAliase();
                String defaultSort = columnDefinitionNew.getDefaultSort();
                selectionHql.append(",o." + field + " as " + columnAlias);
                if (defaultSort != null && defaultSort.equals("升序")) {
                    if (orderBy.toString().equals("")) {
                        aclQueryInfo.addOrderby(field);
                    } else {
                        aclQueryInfo.addOrderby("," + field);
                    }
                }
                if (defaultSort != null && defaultSort.equals("降序")) {
                    if (orderBy.toString().equals("")) {
                        aclQueryInfo.addOrderby(field, "desc");
                    } else {
                        aclQueryInfo.addOrderby("," + field, "desc");
                    }
                }
                for (int l = 0; l < keyWords.size(); l++) {
                    if (columnDefinitionNew.getColumnDataType().equals("STRING")
                            && !StringUtils.isBlank(keyWords.get(l).toString())) {
                        if ("用户".equals(columnDefinitionNew.getColumnType())) {
                            List<String> users = orgApiFacade.getOrgIdsLikeName(keyWords.get(l).toString());
                            if (!users.isEmpty()) {
                                selection.append(" or o." + field + " in " + "(:" + columnAlias + ")");
                                StringBuilder sb = new StringBuilder();
                                for (int m = 0; m < users.size(); m++) {
                                    sb.append("," + users.get(m));
                                }
                                aclQueryInfo.addQueryParams(columnAlias, sb.toString().replaceFirst(",", ""));
                            }
                        } else {
                            selection.append(" or " + "o." + field + " like " + ":" + columnAlias);
                            aclQueryInfo.addQueryParams(columnAlias,
                                    "%" + columnDefinitionNew.getColumnDataType((String) keyWords.get(l)) + "%");
                        }
                    } else if (columnDefinitionNew.getColumnDataType().equals("DATE")
                            && !StringUtils.isBlank(searchField) && searchField.equals(field)) {
                        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                        if (!StringUtils.isBlank(beginTime) && StringUtils.isBlank(endTime)) {
                            try {
                                Date startDate = format.parse(beginTime);
                                timeSql += " o." + field + " >= :beginTime_" + field;
                                aclQueryInfo.addQueryParams("beginTime_" + field, startDate);
                            } catch (ParseException e) {
                                // TODO Auto-generated catch block
                                logger.error(e.getMessage(), e);
                            }
                        } else if (StringUtils.isBlank(beginTime) && !StringUtils.isBlank(endTime)) {
                            try {
                                Date endDate = format.parse(endTime);
                                timeSql += " o." + field + " <= :endTime_" + field;
                                aclQueryInfo.addQueryParams("endTime_" + field, endDate);
                            } catch (ParseException e) {
                                // TODO Auto-generated catch block
                                logger.error(e.getMessage(), e);
                            }
                        } else if (!StringUtils.isBlank(beginTime) && !StringUtils.isBlank(endTime)) {
                            try {
                                Date startDate = format.parse(beginTime);
                                Date endDate = format.parse(endTime);
                                // if (StringUtils.isNotBlank(beginTime) &&
                                // beginTime.equals(endTime)) {
                                /**update by linz 时间相同则从00到23:59:59**/
                                DateFormat formatTime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                                String endDateTemp = format.format(endDate);
                                endDateTemp = endDateTemp + " 23:59:59";
                                endDate = completeDateParse(formatTime, endDateTemp);
                                // }
                                timeSql += " o." + field + " >= :beginTime_" + field;
                                timeSql += " and o." + field + " <= :endTime_" + field;
                                aclQueryInfo.addQueryParams("beginTime_" + field, startDate);
                                aclQueryInfo.addQueryParams("endTime_" + field, endDate);
                            } catch (ParseException e) {
                                // TODO Auto-generated catch block
                                logger.error(e.getMessage(), e);
                            }
                        }
                    }

                }
                if (columnDefinitionNew.getColumnType().equals("用户")) {
                    choorseNames.add(columnDefinitionNew.getColumnAliase());
                }
                if (columnDefinitionNew.getColumnType().equals("单位")) {
                    unitNames.add(columnDefinitionNew.getColumnAliase());
                }
                if (columnDefinitionNew.getColumnType().equals("acl用户")) {
                    String value = columnDefinitionNew.getValue();
                    if (StringUtils.isNotBlank(value) && value.indexOf("|") != -1) {
                        String[] values = StringUtils.split(value, "|");
                        if (values.length == 2 && StringUtils.isNotBlank(values[1])) {
                            aclChoorseNames.put(columnDefinitionNew.getColumnAliase() + "entityClass", values[0]);
                            aclChoorseNames.put(columnDefinitionNew.getColumnAliase(), values[1]);
                        }
                    }
                }
            }
        }

        if (defaultCondition != null && !defaultCondition.equals("")) {
            if (!StringUtils.isBlank(selection.toString())) {
                aclQueryInfo.setWhereHql("(" + defaultCondition + ")" + " and ("
                        + selection.toString().replaceFirst("or", "") + ")");
                Date date = new Date();
                aclQueryInfo.addQueryParams("nowDate", date);
            } else {
                aclQueryInfo.setWhereHql("(" + defaultCondition + ")");
            }
            if (!StringUtils.isBlank(timeSql)) {
                aclQueryInfo.setWhereHql(aclQueryInfo.getWhereHql() + " and (" + timeSql + ")");
            }
        } else {
            aclQueryInfo.setWhereHql(selection.toString().replaceFirst("or", ""));
            if (!StringUtils.isBlank(aclQueryInfo.getWhereHql()) && !StringUtils.isBlank(timeSql)) {
                aclQueryInfo.setWhereHql("(" + aclQueryInfo.getWhereHql() + ")" + " and (" + timeSql + ")");
            } else if (StringUtils.isBlank(aclQueryInfo.getWhereHql()) && !StringUtils.isBlank(timeSql)) {
                aclQueryInfo.setWhereHql(timeSql);
            }
        }

        aclQueryInfo.setSelectionHql(selectionHql.toString().replaceFirst(",", ""));

        // 处理权限角色值
        List<Permission> roTypeList = new ArrayList<Permission>();
        String[] roTypeArray = roleType.split(";");
        for (int i = 0; i < roTypeArray.length; i++) {
            roTypeList.add(new AclPermission(Integer.valueOf(roTypeArray[i])));
        }
        List<String> sids = new ArrayList<String>();
        sids.add(SpringSecurityUtils.getCurrentUserId());

        if (roleValue.equals("person")) {
            queryItems = aclService.queryForItem(Entityname, aclQueryInfo, roTypeList, sids);
            page.setTotalCount(aclQueryInfo.getPage().getTotalCount());
            getDataByUser(queryItems, choorseNames);
            getDataByUnit(queryItems, unitNames);
            getDataByAclUser(rowIdKey, queryItems, aclChoorseNames);
        } else if (roleValue.equals("group")) {
            queryItems = aclService.queryForItem(Entityname, aclQueryInfo, roTypeList,
                    SpringSecurityUtils.getCurrentUserId(), moduleId);
            page.setTotalCount(aclQueryInfo.getPage().getTotalCount());
            getDataByUser(queryItems, choorseNames);
            getDataByUnit(queryItems, unitNames);
            getDataByAclUser(rowIdKey, queryItems, aclChoorseNames);
        } else if (roleValue.equals("all")) {
            queryItems = aclService.queryAllForItem(Entityname, aclQueryInfo, roTypeList,
                    SpringSecurityUtils.getCurrentUserId(), moduleId);
            page.setTotalCount(aclQueryInfo.getPage().getTotalCount());
            getDataByUser(queryItems, choorseNames);
            getDataByUnit(queryItems, unitNames);
            getDataByAclUser(rowIdKey, queryItems, aclChoorseNames);
        }
        return queryItems;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.dyview.service.ViewDefinitionServiceBak#getCondColumnData2(java.lang.String, java.lang.String, java.util.Set, com.wellsoft.pt.basicdata.dyview.entity.PageDefinition, com.wellsoft.context.jdbc.support.PagingInfo, java.lang.String, java.lang.String)
     */
    @Override
    public List<QueryItem> getCondColumnData2(String tableUuid, String defaultCondition,
                                              Set<ColumnDefinitionNew> columnDefinitionNews, String rowIdKey, PageDefinitionNew pageDefinitionNew,
                                              PagingInfo page, String condValue, String appointColumn, String roleType, String roleValue) {
        List<QueryItem> queryItems = new ArrayList<QueryItem>();
        StringBuilder selection = new StringBuilder();
        Set<String> choorseNames = new HashSet<String>();
        Set<String> unitNames = new HashSet<String>();
        Map<String, String> aclChoorseNames = new HashMap<String, String>();
        com.wellsoft.pt.security.acl.support.QueryInfo aclQueryInfo = new com.wellsoft.pt.security.acl.support.QueryInfo();
        if (defaultCondition != null && !defaultCondition.equals("")) {
            selection.append(defaultCondition);
            aclQueryInfo.setWhereHql(selection.toString());
        }
        // 设置当前页
        aclQueryInfo.getPage().setPageNo(page.getCurrentPage());
        // 设置每页的页数
        aclQueryInfo.getPage().setPageSize(page.getPageSize());

        StringBuilder selectionHql = new StringBuilder();
        StringBuilder orderBy = new StringBuilder();
        Class<IdEntity> Entityname = null;
        List<SystemTableRelationship> relationShip = getViewDataNewService.getAttributesByrelationship(tableUuid);
        SystemTable table = getViewDataNewService.getSystemTable(tableUuid);
        SystemTable st = basicDataApiFacade.getTable(tableUuid);
        String moduleId = st.getModuleName();// 模块id

        if (pageDefinitionNew.getIsPaging() == true) {
        }
        if (relationShip.size() > 0) {
            for (int index = 0; index < relationShip.size(); index++) {
                String mainTableName = relationShip.get(index).getMainTableName();// 主表名
                try {
                    Entityname = (Class<IdEntity>) Class.forName(mainTableName);
                } catch (ClassNotFoundException e) {
                    logger.error(e.getMessage(), e);
                }
                String SecondaryTableName = relationShip.get(index).getSecondaryTableName();// 从表名
                String associateAttribute = relationShip.get(index).getAssociatedAttributes();// 关联属性
                String tableRelationShip = relationShip.get(index).getTableRelationship();

                String[] mainTNs = mainTableName.split("entity.");
                String[] secondaryTNs = SecondaryTableName.split("entity.");
                // 计数器
                int i = 0;
                for (ColumnDefinitionNew columnDefinitionNew : columnDefinitionNews) {
                    String field = columnDefinitionNew.getFieldName();
                    String entityName = columnDefinitionNew.getEntityName();
                    String columnAlias = columnDefinitionNew.getColumnAliase();
                    String defaultSort = columnDefinitionNew.getDefaultSort();
                    String columnName = columnDefinitionNew.getTitleName();

                    if ("一对一".equals(tableRelationShip) || "多对一".equals(tableRelationShip)) {
                        if (i < 1) {
                            for (int j = 1; j < mainTNs.length; j++) {
                                String mainTN = mainTNs[1].toUpperCase();
                                String entityN = entityName.toUpperCase();
                                String secondaryTN = secondaryTNs[1];
                                if (mainTN.equals(entityN)) {
                                    selectionHql.append("o." + field + " as " + columnAlias);
                                    if (defaultSort != null && defaultSort.equals("升序")) {
                                        aclQueryInfo.addOrderby(field);
                                    } else if (orderBy.toString().equals("")) {
                                        aclQueryInfo.addOrderby(field);
                                    } else {
                                        aclQueryInfo.addOrderby("," + field);
                                    }
                                    if (defaultSort != null && defaultSort.equals("降序")) {
                                        aclQueryInfo.addOrderby(field, "desc");
                                    }
                                } else {
                                    selectionHql.append("o." + associateAttribute + "." + field + " as " + columnAlias);
                                    if (defaultSort != null && defaultSort.equals("升序")) {
                                        aclQueryInfo.addOrderby(associateAttribute + "." + field);
                                    } else if (orderBy.toString().equals("")) {
                                        aclQueryInfo.addOrderby(associateAttribute + "." + field);
                                    }
                                    if (defaultSort != null && defaultSort.equals("降序")) {
                                        aclQueryInfo.addOrderby(associateAttribute + "." + field, "desc");
                                    } else if (orderBy.toString().equals("")) {
                                        aclQueryInfo.addOrderby(associateAttribute + "." + field, "desc");
                                    } else {
                                        aclQueryInfo.addOrderby("," + associateAttribute + "." + field);
                                    }
                                }
                            }
                        } else {
                            for (int j = 1; j < mainTNs.length; j++) {
                                String mainTN = mainTNs[1].toUpperCase();
                                String entityN = entityName.toUpperCase();
                                String secondaryTN = secondaryTNs[1];
                                if (mainTN.equals(entityN)) {
                                    selectionHql.append(",o." + field + " as " + columnAlias);
                                    if (defaultSort != null && defaultSort.equals("升序")) {
                                        if (orderBy.toString().equals("")) {
                                            aclQueryInfo.addOrderby(field);
                                        } else {
                                            aclQueryInfo.addOrderby("," + field);
                                        }
                                    }
                                    if (defaultSort != null && defaultSort.equals("降序")) {
                                        if (orderBy.toString().equals("")) {
                                            aclQueryInfo.addOrderby(field, "desc");
                                        } else {
                                            aclQueryInfo.addOrderby("," + field, "desc");
                                        }
                                    }

                                } else {
                                    selectionHql
                                            .append(",o." + associateAttribute + "." + field + " as " + columnAlias);
                                    if (defaultSort != null && defaultSort.equals("升序")) {
                                        if (orderBy.toString().equals("")) {
                                            aclQueryInfo.addOrderby(associateAttribute + "." + field);
                                        } else {
                                            aclQueryInfo.addOrderby("," + associateAttribute + "." + field);
                                        }
                                    }
                                    if (defaultSort != null && defaultSort.equals("降序")) {
                                        if (orderBy.toString().equals("")) {
                                            aclQueryInfo.addOrderby(associateAttribute + "." + field, "desc");
                                        } else {
                                            aclQueryInfo.addOrderby("," + associateAttribute + "." + field, "desc");
                                        }
                                    }
                                }
                            }
                        }
                    }

                    if (columnName.equals(appointColumn) && StringUtils.isNotBlank(field)) {
                        if (columnDefinitionNew.getColumnDataType().equals("STRING")) {
                            if (selection.length() > 0) {
                                selection.append(" and o." + field + " like " + ":" + columnAlias);
                            } else {
                                selection.append("o." + field + " like " + ":" + columnAlias);
                            }
                            aclQueryInfo.addQueryParams(columnAlias, "%" + condValue + "%");
                        } else if (columnDefinitionNew.getColumnDataType().equals("BOOLEAN")) {
                            if (selection.length() > 0) {
                                selection.append(" and o." + field + " = " + ":" + columnAlias);
                            } else {
                                selection.append("o." + field + " = " + ":" + columnAlias);
                            }
                            if (condValue.equals("false")) {
                                aclQueryInfo.addQueryParams(columnAlias, false);
                            } else {
                                aclQueryInfo.addQueryParams(columnAlias, true);
                            }
                        }
                    }

                    if (columnDefinitionNew.getColumnType().equals("用户")) {
                        choorseNames.add(columnDefinitionNew.getFieldName());
                    }
                    if (columnDefinitionNew.getColumnType().equals("单位")) {
                        unitNames.add(columnDefinitionNew.getColumnAliase());
                    }
                    if (columnDefinitionNew.getColumnType().equals("acl用户")) {
                        String value = columnDefinitionNew.getValue();
                        if (StringUtils.isNotBlank(value) && value.indexOf("|") != -1) {
                            String[] values = StringUtils.split(value, "|");
                            if (values.length == 2 && StringUtils.isNotBlank(values[1])) {
                                aclChoorseNames.put(columnDefinitionNew.getColumnAliase() + "entityClass", values[0]);
                                aclChoorseNames.put(columnDefinitionNew.getColumnAliase(), values[1]);
                            }
                        }
                    }
                    i++;
                }
            }
        } else {
            int i = 0;
            for (ColumnDefinitionNew columnDefinitionNew : columnDefinitionNews) {
                String tableName = table.getFullEntityName();
                try {
                    Entityname = (Class<IdEntity>) Class.forName(tableName);
                } catch (ClassNotFoundException e) {
                    logger.error(e.getMessage(), e);
                }
                String field = columnDefinitionNew.getFieldName();
                String columnAlias = columnDefinitionNew.getColumnAliase();
                String defaultSort = columnDefinitionNew.getDefaultSort();
                String columnName = columnDefinitionNew.getTitleName();
                if (i < 1) {
                    selectionHql.append("o." + field + " as " + columnAlias);
                    if (defaultSort != null && defaultSort.equals("升序")) {
                        aclQueryInfo.addOrderby(field);
                    } else if (orderBy.toString().equals("")) {
                        aclQueryInfo.addOrderby(field);
                    } else {
                        aclQueryInfo.addOrderby("," + field);
                    }
                    if (defaultSort != null && defaultSort.equals("降序")) {
                        aclQueryInfo.addOrderby(field, "desc");
                    }
                } else {
                    selectionHql.append(",o." + field + " as " + columnAlias);
                    if (defaultSort != null && defaultSort.equals("升序")) {
                        if (orderBy.toString().equals("")) {
                            aclQueryInfo.addOrderby(field);
                        } else {
                            aclQueryInfo.addOrderby("," + field);
                        }
                    }
                    if (defaultSort != null && defaultSort.equals("降序")) {
                        if (orderBy.toString().equals("")) {
                            aclQueryInfo.addOrderby(field, "desc");
                        } else {
                            aclQueryInfo.addOrderby("," + field, "desc");
                        }
                    }
                }

                if (columnName.equals(appointColumn) && StringUtils.isNotBlank(field)) {
                    if (columnDefinitionNew.getColumnDataType().equals("STRING")) {
                        if (selection.length() > 0) {
                            selection.append(" and o." + field + " like " + ":" + columnAlias);
                        } else {
                            selection.append("o." + field + " like " + ":" + columnAlias);
                        }
                        aclQueryInfo.addQueryParams(columnAlias, "%" + condValue + "%");
                    } else if (columnDefinitionNew.getColumnDataType().equals("BOOLEAN")) {
                        if (selection.length() > 0) {
                            selection.append(" and o." + field + " = " + ":" + columnAlias);
                        } else {
                            selection.append("o." + field + " = " + ":" + columnAlias);
                        }
                        if (condValue.equals("false")) {
                            aclQueryInfo.addQueryParams(columnAlias, false);
                        } else {
                            aclQueryInfo.addQueryParams(columnAlias, true);
                        }
                    }
                }

                if (columnDefinitionNew.getColumnType().equals("用户")) {
                    choorseNames.add(columnDefinitionNew.getFieldName());
                }
                if (columnDefinitionNew.getColumnType().equals("单位")) {
                    unitNames.add(columnDefinitionNew.getColumnAliase());
                }
                if (columnDefinitionNew.getColumnType().equals("acl用户")) {
                    String value = columnDefinitionNew.getValue();
                    if (StringUtils.isNotBlank(value) && value.indexOf("|") != -1) {
                        String[] values = StringUtils.split(value, "|");
                        if (values.length == 2 && StringUtils.isNotBlank(values[1])) {
                            aclChoorseNames.put(columnDefinitionNew.getColumnAliase() + "entityClass", values[0]);
                            aclChoorseNames.put(columnDefinitionNew.getColumnAliase(), values[1]);
                        }
                    }
                }
                i++;
            }
        }

        aclQueryInfo.setWhereHql(selection.toString());
        aclQueryInfo.setSelectionHql(selectionHql.toString());
        // 处理权限角色值
        List<Permission> roTypeList = new ArrayList<Permission>();
        String[] roTypeArray = roleType.split(";");
        for (int i = 0; i < roTypeArray.length; i++) {
            roTypeList.add(new AclPermission(Integer.valueOf(roTypeArray[i])));
        }
        List<String> sids = new ArrayList<String>();
        sids.add(SpringSecurityUtils.getCurrentUserId());
        if (roleValue.equals("person")) {
            queryItems = aclService.queryForItem(Entityname, aclQueryInfo, roTypeList, sids);
            page.setTotalCount(aclQueryInfo.getPage().getTotalCount());
            getDataByUser(queryItems, choorseNames);
            getDataByUnit(queryItems, unitNames);
            getDataByAclUser(rowIdKey, queryItems, aclChoorseNames);
        }
        // 群组权限
        else if (roleValue.equals("group")) {
            queryItems = aclService.queryForItem(Entityname, aclQueryInfo, roTypeList,
                    SpringSecurityUtils.getCurrentUserId(), moduleId);
            page.setTotalCount(aclQueryInfo.getPage().getTotalCount());
            getDataByUser(queryItems, choorseNames);
            getDataByUnit(queryItems, unitNames);
            getDataByAclUser(rowIdKey, queryItems, aclChoorseNames);
        } else if (roleValue.equals("all")) {
            queryItems = aclService.queryAllForItem(Entityname, aclQueryInfo, roTypeList,
                    SpringSecurityUtils.getCurrentUserId(), moduleId);
            page.setTotalCount(aclQueryInfo.getPage().getTotalCount());
            getDataByUser(queryItems, choorseNames);
            getDataByUnit(queryItems, unitNames);
            getDataByAclUser(rowIdKey, queryItems, aclChoorseNames);
        }
        return queryItems;
    }

    /**
     * 根据传入的查询字段名查询相应的字段数据(调用系统表单接口)
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.dyview.service.ViewDefinitionServiceBak#getSelectData2(java.lang.String, java.lang.String, com.wellsoft.context.jdbc.support.PagingInfo)
     */
    @Override
    public List<Map<String, Object>> getSelectData2(String formUuid, String fieldName, PagingInfo page) {
        String systemTableUuid = formUuid;
        StringBuilder selection = new StringBuilder();
        Set<String> choorseNames = new HashSet<String>();
        int firstResult = 0;
        int maxResults = 0;
        if (page != null) {
            firstResult = page.getPageSize() * (page.getCurrentPage() - 1);
            maxResults = firstResult + page.getPageSize();
        }

        List<String> fieldNames = new ArrayList<String>();

        List<SystemTableAttribute> systemTableAttributes = getViewDataNewService.getSystemTableColumns(systemTableUuid);

        for (SystemTableAttribute systemTableAttribute : systemTableAttributes) {
            String field = systemTableAttribute.getChineseName();
            if (field != null && fieldName.equals(field)) {

                SystemTable st_ = systemTableAttribute.getSystemTable();// basicDataApiFacade.getTable(formUuid);
                systemTableUuid = st_.getUuid();
                String dataParam = "";
                try {
                    dataParam = basicDataApiFacade.getColumnName(Class.forName(st_.getFullEntityName()),
                            systemTableAttribute.getAttributeName());
                } catch (ClassNotFoundException e) {
                    // TODO Auto-generated catch block
                    logger.error(e.getMessage(), e);
                }

                fieldNames.add(dataParam + " as value_");
                selection.append(dataParam + " is not null");
            }
        }

        String[] fieldData = fieldNames.toArray(new String[fieldNames.size()]);
        // 是否去掉重复的数据
        boolean distinct = true;
        List<Map<String, Object>> data = basicDataApiFacade.query(systemTableUuid, distinct, fieldData,
                selection.toString(), null, null, null, null, 0, 0);
        // 将列类型是用户的值通过调用组织机构的接口获取到用户的显示值显示到页面上
        getDataByUser2(data, choorseNames);
        return data;
    }

    /**
     * 根据传入的按钮编码来获取按钮的信息
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.dyview.service.ViewDefinitionServiceBak#getCustomButton(java.lang.String)
     */
    @Override
    public List<Resource> getCustomButton(String code) {
        return securityApiFacade.getDynamicButtonResourcesByCode(code);
    }

    /**
     * 封装获取列类型是用户的列字段数据(动态表单)
     *
     * @param data
     * @param choorseNames
     */
    public void getDataByUser(List<QueryItem> data, Set<String> choorseNames) {
        if (choorseNames.isEmpty()) {
            return;
        }
        if (data != null) {
            // 将列类型是用户的值通过调用组织机构的接口获取到用户的显示值显示到页面上
            for (QueryItem map : data) {
                Iterator ite = map.keySet().iterator();
                QueryItem obj = new QueryItem();
                Map<String, MultiOrgUserAccount> userMap = new HashMap<String, MultiOrgUserAccount>();
                Set<String> users = new HashSet<String>();
                Set<String> depts = new HashSet<String>();
                while (ite.hasNext()) {
                    String key = ite.next().toString();
                    // obj.put(key, map.get(key));
                    for (String chooseName : choorseNames) {
                        chooseName = QueryItem.getKey(chooseName);
                        users.clear();
                        depts.clear();
                        if (key.equals(chooseName)) {
                            String chooseValue = (String) map.get(key);
                            if (chooseValue != null && !chooseValue.equals("system")) {
                                String[] chooseValueSplit = chooseValue.split(";");
                                for (int j = 0; j < chooseValueSplit.length; j++) {
                                    if (chooseValueSplit[j].startsWith(IdPrefix.USER.getValue())) {
                                        users.add(chooseValueSplit[j]);
                                    } else if (chooseValueSplit[j].startsWith(IdPrefix.DEPARTMENT.getValue())) {
                                        depts.add(chooseValueSplit[j]);
                                    } else {
                                        users.add(chooseValueSplit[j]);
                                    }
                                }
                                StringBuilder descNames = new StringBuilder();
                                StringBuilder descNamesUser = new StringBuilder();
                                if (users.size() > 0) {
                                    List<MultiOrgUserAccount> user = orgApiFacade.queryUserAccountListByIds(users);
                                    user.remove(null);
                                    userMap = ConvertUtils.convertElementToMap(user, "id");

                                    Iterator<MultiOrgUserAccount> it = user.iterator();
                                    while (it.hasNext()) {
                                        String descName = it.next().getUserName();
                                        descNamesUser.append(descName);
                                        if (it.hasNext()) {
                                            descNamesUser.append(";");
                                        }
                                    }

                                    Iterator<String> userIt = users.iterator();
                                    while (userIt.hasNext()) {
                                        String userStr = userIt.next();
                                        if (!userStr.startsWith(IdPrefix.USER.getValue())
                                                && !descNamesUser.toString().contains(userStr)) {
                                            descNamesUser.append(userStr);
                                            if (userIt.hasNext()) {
                                                descNamesUser.append(";");
                                            }
                                        }
                                    }
                                }
                                StringBuilder descNameDept = new StringBuilder();
                                if (depts.size() > 0) {
                                    List<MultiOrgElement> dept = orgApiFacade.queryOrgElementListByIds(depts);
                                    Iterator<MultiOrgElement> it2 = dept.iterator();
                                    while (it2.hasNext()) {

                                        String descName = it2.next().getName();
                                        descNameDept.append(descName);
                                        if (it2.hasNext()) {
                                            descNameDept.append(";");
                                        }
                                    }
                                }
                                // System.out.println(descNamesUser.toString());
                                if (descNamesUser.toString().equals("")) {
                                    descNames.append(descNameDept.toString());
                                } else {
                                    if (descNameDept.toString().equals("")) {
                                        descNames.append(descNamesUser.toString());
                                    } else {
                                        descNames.append(descNamesUser.toString() + ";" + descNameDept.toString());
                                    }
                                }
                                map.put(key, descNames.toString());
                            } else if (chooseValue != null && chooseValue.equals("system")) {
                                map.put(key, "system");
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 封装获取列类型是用户的列字段数据(动态表单)
     *
     * @param data
     * @param choorseNames
     */
    public void getDataByUnit(List<QueryItem> data, Set<String> choorseNames) {
        if (choorseNames.isEmpty()) {
            return;
        }
        // 将列类型是用户的值通过调用组织机构的接口获取到用户的显示值显示到页面上
        for (QueryItem map : data) {
            Iterator ite = map.keySet().iterator();
            while (ite.hasNext()) {
                String key = ite.next().toString();
                // obj.put(key, map.get(key));
                for (String chooseName : choorseNames) {
                    chooseName = QueryItem.getKey(chooseName);
                    if (key.equals(chooseName)) {
                        String unitValue = (String) map.get(key);
                        if (unitValue != null) {
                            StringBuilder descNames = new StringBuilder();
                            StringBuilder descNameUnit = new StringBuilder();
                            String[] chooseValueSplit = unitValue.replace("null", "").split(";");
                            for (int j = 0; j < chooseValueSplit.length; j++) {
                                if (!chooseValueSplit[j].equals("")) {
                                    CommonUnit commonUnit = unitApiFacade.getCommonUnitById(chooseValueSplit[j]);
                                    descNameUnit.append(";");
                                    if (commonUnit != null) {
                                        descNameUnit.append(commonUnit.getName());
                                    } else {
                                        descNameUnit.append(chooseValueSplit[j]);
                                    }
                                }
                            }
                            if (!descNameUnit.toString().equals("")) {
                                descNames.append(descNameUnit.toString().replaceFirst(";", ""));
                            }
                            map.put(key, descNames.toString());
                        }
                    }
                }
            }
        }
    }

    /**
     * 封装获取列类型是数据字典的列字段数据
     *
     * @param data
     * @param fields
     */
    public void getDataByDataDict(List<QueryItem> data, List<Map<String, String>> fields) {
        if (fields.isEmpty()) {
            return;
        }
        for (QueryItem map : data) {
            Iterator ite = map.keySet().iterator();
            while (ite.hasNext()) {
                String key = ite.next().toString();
                for (int i = 0; i < fields.size(); i++) {
                    String fieldValue = "";
                    Map<String, String> fieldMap = fields.get(i);
                    for (String mapKey : fieldMap.keySet()) {
                        if (key.equals(mapKey)) {
                            if (map.get(mapKey) instanceof Number) {
                                DecimalFormat decimalFormat = new DecimalFormat();
                                fieldValue = decimalFormat.format(map.get(mapKey));
                                DataDictionary dataDictionary = dataDictionaryService.getByType(fieldMap.get(mapKey));
                                List<DataDictionary> children = dataDictionary.getChildren();
                                for (DataDictionary child : children) {
                                    if (child.getCode().equals(fieldValue)) {
                                        map.put(key, child.getName());
                                    }
                                }
                            } else {
                                fieldValue = (String) map.get(mapKey);
                                DataDictionary dataDictionary = dataDictionaryService.getByType(fieldMap.get(mapKey));
                                List<DataDictionary> children = dataDictionary.getChildren();
                                for (DataDictionary child : children) {
                                    if (child.getCode().equals(fieldValue)) {
                                        map.put(key, child.getName());
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 解析Acl用户
     *
     * @param queryItems
     * @param aclChoorseNames
     */
    @SuppressWarnings("unchecked")
    private void getDataByAclUser(String rowIdKey, List<QueryItem> queryItems, Map<String, String> aclChoorseNames) {
        if (aclChoorseNames.isEmpty()) {
            return;
        }
        try {
            for (QueryItem item : queryItems) {
                String entityUuid = item.get(rowIdKey) == null ? "" : item.get(rowIdKey).toString();
                for (String rawKey : aclChoorseNames.keySet()) {
                    if (item.containsKey(QueryItem.getKey(rawKey))) {
                        String className = aclChoorseNames.get(rawKey + "entityClass");
                        String value = aclChoorseNames.get(rawKey);
                        Class<IdEntity> entityClass = (Class<IdEntity>) Class.forName(className);
                        AclPermission permission = new AclPermission(Integer.valueOf(value));
                        StringBuilder sb = new StringBuilder();
                        List<AclSid> aclSids = aclService.getSid(entityClass, entityUuid, permission);
                        Iterator<AclSid> it = aclSids.iterator();
                        while (it.hasNext()) {
                            MultiOrgUserAccount user = orgApiFacade.getAccountByUserId(it.next().getSid());
                            if (user != null) {
                                sb.append(user.getUserName());
                                if (it.hasNext()) {
                                    sb.append(Separator.SEMICOLON.getValue());
                                }
                            }
                        }
                        item.put(QueryItem.getKey(rawKey), sb.toString());
                    }
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * 封装获取列类型是用户的列字段数据(系统表)
     *
     * @param data
     * @param choorseNames
     */
    public void getDataByUser2(List<Map<String, Object>> data, Set<String> choorseNames) {
        // 将列类型是用户的值通过调用组织机构的接口获取到用户的显示值显示到页面上
        for (Map<String, Object> map : data) {
            Iterator<String> ite = map.keySet().iterator();
            Set<String> users = new HashSet<String>();
            Set<String> depts = new HashSet<String>();
            while (ite.hasNext()) {
                String key = ite.next().toString();
                // obj.put(key, map.get(key));
                for (String chooseName : choorseNames) {
                    users.clear();
                    depts.clear();
                    if (key.equals(chooseName)) {
                        String chooseValue = (String) map.get(key);
                        if (chooseValue != null) {
                            String[] chooseValueSplit = chooseValue.split(";");
                            for (int j = 0; j < chooseValueSplit.length; j++) {
                                if (chooseValueSplit[j].startsWith(IdPrefix.USER.getValue())) {
                                    users.add(chooseValueSplit[j]);
                                } else if (chooseValueSplit[j].startsWith(IdPrefix.DEPARTMENT.getValue())) {
                                    depts.add(chooseValueSplit[j]);
                                }
                            }
                            StringBuilder descNames = new StringBuilder();
                            StringBuilder descNamesUser = new StringBuilder();
                            if (users.size() > 0) {
                                List<MultiOrgUserAccount> user = orgApiFacade.queryUserAccountListByIds(users);

                                Iterator<MultiOrgUserAccount> it = user.iterator();
                                while (it.hasNext()) {
                                    String descName = it.next().getUserName();
                                    descNamesUser.append(descName);
                                    if (it.hasNext()) {
                                        descNamesUser.append(";");
                                    }
                                }
                            }
                            StringBuilder descNameDept = new StringBuilder();
                            if (depts.size() > 0) {
                                List<MultiOrgElement> dept = orgApiFacade.queryOrgElementListByIds(depts);
                                Iterator<MultiOrgElement> it2 = dept.iterator();
                                while (it2.hasNext()) {

                                    String descName = it2.next().getName();
                                    descNameDept.append(descName);
                                    if (it2.hasNext()) {
                                        descNameDept.append(";");
                                    }
                                }
                            }
                            // System.out.println(descNamesUser.toString());
                            if (descNamesUser.toString().equals("")) {
                                descNames.append(descNameDept.toString());
                            } else {
                                descNames.append(descNamesUser.toString() + ";" + descNameDept.toString());
                            }
                            map.put(key, descNames.toString());
                        }
                    }
                }
            }

        }
    }

    /**
     * 根据字段key值跟value值获取视图的数据
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.dyview.service.ViewDefinitionService#getViewDataByKey(java.lang.String, java.lang.String)
     */
    public List<QueryItem> getViewDataByKey(String val, String key) {
        DyViewQueryInfoNew dyViewQueryInfoNew = new DyViewQueryInfoNew();
        PagingInfo pageInfo = new PagingInfo();
        dyViewQueryInfoNew.setPageInfo(pageInfo);
        ViewDefinitionNewBean viewDefinitionNewBean = new ViewDefinitionNewBean();
        if (key != null) {
            ViewDefinitionNew viewDefinitionNew = viewDefinitionNewDao.findUniqueBy(key, val);
            viewDefinitionNewBean = this.getBeanByUuid(viewDefinitionNew.getUuid());
        } else {
            viewDefinitionNewBean = this.getBeanByUuid(val);
        }
        // 数据源的ID
        String tableDefinitionId = viewDefinitionNewBean.getTableDefinitionId();
        // 复选框的key
        String rowIdKey = StringUtils.isBlank(viewDefinitionNewBean.getCheckKey()) ? "uuid" : viewDefinitionNewBean
                .getCheckKey();
        String readKey = viewDefinitionNewBean.getReadKey();
        if (readKey == null || (readKey != null && readKey.equals(""))) {
            readKey = "uuid";
        }
        // 获取数据源的ID
        String dataSourceDefId = viewDefinitionNewBean.getTableDefinitionId();

        // 获取视图的默认搜索条件
        String defaultCondition = viewDefinitionNewBean.getDefaultCondition();
        defaultCondition = StringUtils.replace(defaultCondition, "{当前登录人}", SpringSecurityUtils.getCurrentUserName());
        // 获取视图的分页信息
        PagingInfo page = new PagingInfo();
        PageDefinitionNew pageDefinitionNew = new PageDefinitionNew();
        if (0 == (page.getCurrentPage())) {
            page.setCurrentPage(1);
        }
        pageDefinitionNew = viewDefinitionNewBean.getPageDefinitionNews();
        viewDefinitionNewBean.setPageAble(viewDefinitionNewBean.getPageDefinitionNews().getIsPaging());

        // 获取视图下所有的列字段的数据
        Set<ColumnDefinitionNew> columnDefinitionNews = viewDefinitionNewBean.getColumnDefinitionNews();

        // 获取表的总记录数
        Long totalCount = null;
        totalCount = dataSourceApiFacade.count(dataSourceDefId, null, null);
        if (pageDefinitionNew.getIsPaging() == true) {
            page.setTotalCount(totalCount);
            page.setPageSize(pageDefinitionNew.getPageNum());
        }
        List<QueryItem> queryItems = new ArrayList<QueryItem>();
        String whereSql = "";
        queryItems = this.getViewData(defaultCondition, whereSql, tableDefinitionId, columnDefinitionNews,
                pageDefinitionNew, dyViewQueryInfoNew, rowIdKey);
        return queryItems;
    }

    /**
     * 根据视图uuid获得所有的列定义
     *
     * @param viewUuid
     * @return
     */
    @Override
    public Set<ColumnDefinitionNew> getColumnDefinitions(String viewUuid) {
        ViewDefinitionNewBean viewDefinitionNewBean = new ViewDefinitionNewBean();
        viewDefinitionNewBean = this.getBeanByUuid(viewUuid);
        Set<ColumnDefinitionNew> columnDefinitionNews = viewDefinitionNewBean.getColumnDefinitionNews();
        return columnDefinitionNews;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.dyview.service.ViewDefinitionServiceBak#getViewSingleData()
     */
    @Override
    public List<QueryItem> getViewSingleData(String viewUuid, String relationDataDefiantion, String TempId) {
        DyViewQueryInfoNew dyViewQueryInfoNew = new DyViewQueryInfoNew();
        ViewDefinitionNewBean viewDefinitionNewBean = new ViewDefinitionNewBean();
        viewDefinitionNewBean = getBeanByUuid(viewUuid);
        // 获取数据源范围下的表名
        String tableName = "";
        int datascope = 0;
        // 获取视图的默认搜索条件
        String defaultCondition = "o.uuid = '" + TempId + "' and o.status != '0'";
        // 获取视图的分页信息
        PageDefinitionNew pageDefinitionNew = viewDefinitionNewBean.getPageDefinitionNews();
        // 获取视图下所有的列字段的数据
        Set<ColumnDefinitionNew> columnDefinitionsTemp = viewDefinitionNewBean.getColumnDefinitionNews();
        Set<ColumnDefinitionNew> columnDefinitionNews = new HashSet<ColumnDefinitionNew>();
        for (ColumnDefinitionNew columnDefinitionNew : columnDefinitionsTemp) {
            if (relationDataDefiantion != null && !relationDataDefiantion.equals("")) {
                if (columnDefinitionNew.getColumnAliase() != null
                        && relationDataDefiantion.indexOf(columnDefinitionNew.getColumnAliase()) > -1) {
                    columnDefinitionNews.add(columnDefinitionNew);
                } else if (columnDefinitionNew.getFieldName() != null
                        && relationDataDefiantion.indexOf(columnDefinitionNew.getFieldName()) > -1) {
                    columnDefinitionNews.add(columnDefinitionNew);
                }

            }
        }

        PagingInfo page = new PagingInfo();
        List<QueryItem> queryItems = getColumnData3(defaultCondition, tableName, columnDefinitionNews,
                pageDefinitionNew, dyViewQueryInfoNew, null);
        return queryItems;
    }

    /**
     * 获取所有视图带权限的列
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.dyview.service.ViewDefinitionService#getViewColumnByPermission()
     */
    @Override
    public List<ColumnDefinitionNew> getViewColumnByPermission() {
        List<ColumnDefinitionNew> columnDefinitionNew = new ArrayList<ColumnDefinitionNew>();
        List<ViewDefinitionNew> viewDefinitionNews = this.searchViewDefinition();
        for (int i = 0; i < viewDefinitionNews.size(); i++) {
            Set<ColumnDefinitionNew> columnDefinitionNews = viewDefinitionNews.get(i).getColumnDefinitionNews();
            for (ColumnDefinitionNew columnDefinition2 : columnDefinitionNews) {
                if (columnDefinition2.getFieldPermission() != null && columnDefinition2.getFieldPermission() == true) {
                    columnDefinitionNew.add(columnDefinition2);
                }
            }
        }
        return columnDefinitionNew;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.view.service.ViewDefinitionNewService#getViewData(java.lang.String, java.lang.String, java.util.Set, com.wellsoft.pt.basicdata.view.entity.PageDefinitionNew, com.wellsoft.pt.basicdata.view.support.DyViewQueryInfoNew)
     */
    @Override
    public List<QueryItem> getViewData(String defaultCondition, String whereSql, String dataSourceDefId,
                                       Set<ColumnDefinitionNew> columnDefinitionNews, PageDefinitionNew pageDefinitionNew,
                                       DyViewQueryInfoNew dyViewQueryInfoNew, String rowIdKey) {
        DataSourceDefinitionBean dataSourceBean = dataSourceDefinitionService.getBeanById(dataSourceDefId);
        String inDataScope = dataSourceBean.getInDataScope();
        QueryInfo<?> aclQueryInfo = new QueryInfo();
        Map<String, Object> paraMap = new HashMap<String, Object>();
        if (!org.springframework.util.CollectionUtils.isEmpty(dyViewQueryInfoNew.getExpandParams())) {
            for (String key : dyViewQueryInfoNew.getExpandParams().keySet()) {
                if (StringUtils.isNotBlank(key) && key.startsWith("expandParams_")) {
                    paraMap.put(key.replaceFirst("expandParams_", ""), dyViewQueryInfoNew.getExpandParams().get(key));
                }
            }
        }
        StringBuilder selection = new StringBuilder();
        selection.append(" (1=1)");

        StringBuilder selectionTemp = new StringBuilder();
        Set<String> choorseNames = new HashSet<String>();
        Set<String> unitNames = new HashSet<String>();
        List<Map<String, String>> dataDictFields = new ArrayList<Map<String, String>>();
        Map<String, String> aclChoorseNames = new HashMap<String, String>();
        StringBuilder orderBy = new StringBuilder();

        // 前端所有的请求参数类
        List<CondSelectAskInfoNew> askInfo = dyViewQueryInfoNew.getCondSelectList();
        if (askInfo == null) {
            askInfo = new ArrayList<CondSelectAskInfoNew>();
        }
        StringBuilder selectionFields = new StringBuilder();
        if (askInfo != null && askInfo.size() > 0) {
            for (int i = 0; i < askInfo.size(); i++) {
                // 备选项的真实值
                String optionValue = StringUtils.trimToEmpty(askInfo.get(i).getOptionValue());
                // 备选项对应的列名称
                String appointColumn = StringUtils.trimToEmpty(askInfo.get(i).getAppointColumn());
                // 查询的字段
                String searchField = StringUtils.trimToEmpty(askInfo.get(i).getSearchField());

                String keyWordValue = getKeyWork(askInfo.get(i));

                if (StringUtils.isNotBlank(inDataScope)
                        && (inDataScope.equals(DataSourceConfig.IN_DATA_SCOPE_ENTITYBYACL) || inDataScope
                        .equals(DataSourceConfig.IN_DATA_SCOPE_ENTITY))) {
                    // 获取选取的实体类表
                    List<SystemTableRelationship> relationShip = getViewDataNewService
                            .getAttributesByrelationship(dataSourceBean.getChooseDataId());
                    // 多表时
                    if (relationShip.size() != 0) {
                        for (int index = 0; index < relationShip.size(); index++) {
                            String mainTableName = relationShip.get(index).getMainTableName();// 主表名
                            String SecondaryTableName = relationShip.get(index).getSecondaryTableName();// 从表名
                            String associateAttribute = relationShip.get(index).getAssociatedAttributes();// 关联属性
                            String[] mainTNs = mainTableName.split("entity.");
                            String[] secondaryTNs = SecondaryTableName.split("entity.");
                            String secondaryTN = secondaryTNs[1].toUpperCase();
                            String mainTN = mainTNs[1].toUpperCase();
                            for (ColumnDefinitionNew columnDefinitionNew : columnDefinitionNews) {
                                String entityN = columnDefinitionNew.getEntityName().toUpperCase();
                                if (mainTN.equals(entityN) || secondaryTN.equals(entityN)) {
                                    String fieldName = columnDefinitionNew.getFieldName();
                                    if (secondaryTN.equals(entityN)) {
                                        fieldName = "o." + associateAttribute + "."
                                                + columnDefinitionNew.getFieldName();
                                    }
                                    if (searchField.equals(columnDefinitionNew.getColumnAliase())) {
                                        selectionFields
                                                .append(assemblyWhereSqlUtil(fieldName, askInfo.get(i), paraMap));
                                    }
                                    selectionTemp.append(assemblyKeyWorkWhereSql(keyWordValue, columnDefinitionNew,
                                            aclQueryInfo, fieldName, columnDefinitionNew.getColumnAliase()));
                                }
                            }
                        }
                    }
                    // 单表时
                    else {
                        for (ColumnDefinitionNew columnDefinitionNew : columnDefinitionNews) {
                            selectionFields.append(assemblyWhereSqlUtil(columnDefinitionNew.getFieldName(),
                                    askInfo.get(i), paraMap));
                            selectionTemp.append(assemblyKeyWorkWhereSql(keyWordValue, columnDefinitionNew,
                                    aclQueryInfo, columnDefinitionNew.getFieldName(),
                                    columnDefinitionNew.getColumnAliase()));
                        }
                    }
                } else {
                    selectionFields.append(assemblyWhereSqlUtil(askInfo.get(i), paraMap));
                    // 计数器
                    for (ColumnDefinitionNew columnDefinitionNew : columnDefinitionNews) {
                        String field = columnDefinitionNew.getFieldName();
                        String titleName = columnDefinitionNew.getTitleName();
                        if (field.equals(titleName)) {
                            field = columnDefinitionNew.getColumnAliase();
                        }
                        /************** 排序 *************/
                        String orderTitle = StringUtils.trimToEmpty(askInfo.get(i).getOrderTitle());
                        if (orderBy.indexOf(field) == -1) {
                            if (StringUtils.isNotBlank(orderTitle)
                                    && (orderTitle.equals(titleName) || orderTitle.equals(columnDefinitionNew
                                    .getOtherName()))) {
                                String asc = "";
                                if (StringUtils.isNotBlank(orderTitle) && orderTitle.equals(titleName)) {
                                    asc = StringUtils.trimToEmpty(askInfo.get(i).getOrderbyArr());
                                }
                                String orderStr = field + " " + asc;
                                if (orderBy.length() > 0) {
                                    orderBy = new StringBuilder(orderStr).append(",").append(orderBy);
                                } else {
                                    orderBy.append(orderStr);
                                }
                            } else if (DyviewConfigNew.DYVIEW_SORT_DESC.equals(columnDefinitionNew.getDefaultSort())
                                    || DyviewConfigNew.DYVIEW_SORT_ASC.equals(columnDefinitionNew.getDefaultSort())) {
                                String asc = "";
                                if (columnDefinitionNew.getDefaultSort().equals(DyviewConfigNew.DYVIEW_SORT_ASC)) {
                                    asc = "asc";
                                }
                                if (orderBy.length() > 0) {
                                    orderBy.append(",");
                                }
                                orderBy.append(field).append(" ").append(asc);
                            }
                        }

                        /********************* 搜索条件 *************************/
                        selectionTemp.append(assemblyKeyWorkWhereSql(keyWordValue, columnDefinitionNew, aclQueryInfo,
                                columnDefinitionNew.getFieldName(), columnDefinitionNew.getColumnAliase()));

                        if (StringUtils.isNotBlank(appointColumn) && titleName.equals(appointColumn)
                                && StringUtils.isNotBlank(field)) {
                            selectionTemp.append(field + " like " + "'%" + optionValue + "%'");
                        }
                    }
                }
            }
        }

        for (ColumnDefinitionNew columnDefinitionNew : columnDefinitionNews) {
            String field = columnDefinitionNew.getFieldName();
            String columnAliase = columnDefinitionNew.getColumnAliase();
            /************** 排序 *************/
            String defaultSort = columnDefinitionNew.getDefaultSort();
            // 默认排序
            if (DyviewConfigNew.DYVIEW_SORT_DESC.equals(defaultSort)
                    || DyviewConfigNew.DYVIEW_SORT_ASC.equals(defaultSort)) {

                if (orderBy.length() > 0) {
                    orderBy.append(",");
                }
                orderBy.append(StringUtils.isNotBlank(field) ? field : columnAliase).append(" ")
                        .append(defaultSort.equals(DyviewConfigNew.DYVIEW_SORT_DESC) ? "desc" : "asc");
            }
            if (columnDefinitionNew.getColumnType().equals("用户") || columnDefinitionNew.getColumnType().equals("单位")) {
                choorseNames.add(StringUtils.isNotBlank(columnAliase) ? columnAliase : field);
            }

            if (columnDefinitionNew.getColumnType().equals("数据字典")) {
                Map<String, String> map = new HashMap<String, String>();
                map.put(ToJavaName.dbNameToJavaName(columnDefinitionNew.getColumnAliase(), false),
                        columnDefinitionNew.getDataDictValue());
                dataDictFields.add(map);
            }
            if (columnDefinitionNew.getColumnType().equals("acl用户")) {
                String value = columnDefinitionNew.getValue();
                if (StringUtils.isNotBlank(value) && value.indexOf("|") != -1) {
                    String[] values = StringUtils.split(value, "|");
                    if (values.length == 2 && StringUtils.isNotBlank(values[1])) {
                        aclChoorseNames.put(columnDefinitionNew.getColumnAliase() + "entityClass", values[0]);
                        aclChoorseNames.put(columnDefinitionNew.getColumnAliase(), values[1]);
                    }
                }
            }
        }
        String whereSqlByAll = null;
        defaultCondition = StringUtils.trimToEmpty(defaultCondition);
        whereSql = StringUtils.trimToEmpty(whereSql);
        if (dataSourceBean.getDataSourceTypeId().equals("3")) {
            String where = defaultCondition;
            if (StringUtils.isNotBlank(whereSql)) {
                if (StringUtils.isNotBlank(where)) {
                    where += " and ";
                }
                where += whereSql;

            }
            whereSqlByAll = dataSourceProviderMap.get(dataSourceBean.getChooseDataId()).getWhereSqlForAll(
                    dyViewQueryInfoNew, where);
        }
        if (whereSqlByAll != null) {
            selection.append(" and " + whereSqlByAll);
        } else {
            if (StringUtils.isNotEmpty(defaultCondition)) {
                selection.append(" and (" + defaultCondition + ")");
            }
            if (StringUtils.isNotEmpty(whereSql)) {
                selection.append(" and (" + whereSql + ")");
            }

            if (StringUtils.isNotEmpty(selectionFields.toString())) {
                if (dataSourceBean.getDataSourceTypeId().equals("3")) {
                    String selectWhereSql = dataSourceProviderMap.get(dataSourceBean.getChooseDataId()).getWhereSql(
                            dyViewQueryInfoNew);
                    if (StringUtils.isNotBlank(selectWhereSql)) {
                        selection.append(" and (").append(selectWhereSql + ")");
                    } else {
                        selection.append(" and (").append(selectionFields.toString().replaceFirst("and", "") + ")");
                    }
                } else {
                    selection.append(" and (").append(selectionFields.toString().replaceFirst("and", "") + ")");
                }

            }
        }

        if (StringUtils.isNotEmpty(selectionTemp.toString())) {
            selection.append(" and (").append(selectionTemp.toString().replaceFirst(" or", "") + ")");
        }

        // 是否去掉重复的数据(后续：视图要加入去重定义)
        List<QueryItem> queryItem = new ArrayList<QueryItem>();
        if (inDataScope.equals(DataSourceConfig.IN_DATA_SCOPE_ENTITYBYACL)) {
            aclQueryInfo.addOrderby(orderBy.toString());
            aclQueryInfo.setWhereHql(selection.toString());
            queryItem = dataSourceDataService.queryForAcl(dataSourceDefId, aclQueryInfo,
                    dyViewQueryInfoNew.getPageInfo());
        } else {
            queryItem = dataSourceApiFacade.query(dataSourceDefId, selection.toString(), paraMap, orderBy.toString(),
                    dyViewQueryInfoNew.getPageInfo());
        }

        getDataByUser(queryItem, choorseNames);
        getDataByUnit(queryItem, unitNames);
        getDataByDataDict(queryItem, dataDictFields);
        getDataByAclUser(rowIdKey, queryItem, aclChoorseNames);
        return queryItem;
    }

    private Date completeDateParse(DateFormat format, String time) throws ParseException {
        SimpleDateFormat sdf = null;
        Date date = null;
        String defaultDatePattern2 = "yyyy-MM-dd HH:mm:ss";
        if (defaultDatePattern2.length() == time.length()) {
            sdf = new SimpleDateFormat(defaultDatePattern2);
            date = sdf.parse(time);
        } else {
            date = format.parse(time);
        }
        return date;
    }

    public String getKeyWork(CondSelectAskInfoNew condSelectAskInfoNew) {
        if (CollectionUtils.isNotEmpty(condSelectAskInfoNew.getKeyWords())) {
            for (Map<String, String> keyWord : condSelectAskInfoNew.getKeyWords()) {
                String keyWordValue = keyWord.get("all");
                if (StringUtils.isNotBlank(keyWordValue)) {
                    return keyWordValue;
                }
            }
        }
        return "";
    }

    public String assemblyKeyWorkWhereSql(String keyWordValue, ColumnDefinitionNew columnDefinitionNew,
                                          QueryInfo<?> aclQueryInfo, String field, String columnAlias) {
        StringBuilder sBuilder = new StringBuilder();
        if (field.equals("uuid") || !isTrue(columnDefinitionNew.getJoinQuery())) {
            return sBuilder.toString();
        }

        if (StringUtils.isNotBlank(keyWordValue)) {
            String[] keyValues = keyWordValue.split(" ");
            for (String keyValue : keyValues) {
                if ("用户".equals(columnDefinitionNew.getColumnType())) {
                    List<String> users = new ArrayList<String>();
                    if (keyValue.length() != 0) {
                        users = orgApiFacade.getOrgIdsLikeName(keyValue);
                    }
                    if (!users.isEmpty()) {
                        for (int i = 0; i < users.size() / 1000 + 1; i++) {
                            int from = i * 1000;
                            int to = (i + 1) * 1000 > users.size() ? users.size() : (i + 1) * 1000;
                            sBuilder.append(" or ").append(field).append(" in (")
                                    .append(arrayToString(users.subList(from, to))).append(")");
                        }
                        // aclQueryInfo.addQueryParams(columnAlias,
                        // StringUtils.join(users, ","));
                    }
                } else {
                    sBuilder.append(" or ").append(field).append(" like '%").append(keyValue).append("%'");
                    // aclQueryInfo.addQueryParams(columnAlias,
                    // columnDefinitionNew.getColumnDataType(keyValue));
                }
            }
        }
        return sBuilder.toString();
    }

    private String arrayToString(List<String> arrays) {
        return arrayToString(arrays.toArray(new String[]{}));
    }

    private String arrayToString(String[] arrays) {
        StringBuilder sBuilder = new StringBuilder("'");
        sBuilder.append(StringUtils.join(arrays, "','"));
        sBuilder.append("'");
        return sBuilder.toString();
    }

    public String assemblyWhereSqlUtil(String searchField, CondSelectAskInfoNew condSelectAskInfoNew,
                                       Map<String, Object> params) {
        String searchFieldTypeId = StringUtils.trimToEmpty(condSelectAskInfoNew.getSearchFieldTypeId());
        // 查询的字段
        String searchValue = StringUtils.trimToEmpty(condSelectAskInfoNew.getSearchValue());
        // 日期查询开始时间
        String beginDate = StringUtils.trimToEmpty(condSelectAskInfoNew.getBeginTime());
        // 日期查询结束时间
        String endDate = StringUtils.trimToEmpty(condSelectAskInfoNew.getEndTime());
        if (StringUtils.isBlank(searchValue) && StringUtils.isBlank(beginDate) && StringUtils.isBlank(endDate)) {
            return "";
        }
        if (searchFieldTypeId.equals("SELECT") || searchFieldTypeId.equals("RADIO")
                || searchFieldTypeId.equals("CHECKBOX")) {
            return assemblySelect(searchField, searchValue, params);
        } else if (searchFieldTypeId.equals("ORG")) {
            return assemblyOrg(searchField, searchValue, params);
        } else if (searchFieldTypeId.equals("DATE")) {
            return assemblyDate(searchField, beginDate, endDate, params);
        } else if (searchFieldTypeId.equals("TEXT") && isTrue(condSelectAskInfoNew.getIsArea())) {
            return assemblyTextArea(searchField, searchValue, params);
        } else if (searchFieldTypeId.equals("TEXT") && isTrue(condSelectAskInfoNew.getIsLike())) {
            return assemblyTextList(searchField, searchValue, params);
        } else if (searchFieldTypeId.equals("TEXT") && isTrue(condSelectAskInfoNew.getIsExact())) {
            return assemblyTextExact(condSelectAskInfoNew.getExactValue(), searchField, searchValue, params);
        }
        return "";
    }

    public String assemblyWhereSqlUtil(CondSelectAskInfoNew condSelectAskInfoNew, Map<String, Object> params) {
        String searchField = StringUtils.trimToEmpty(condSelectAskInfoNew.getSearchField());
        return assemblyWhereSqlUtil(searchField, condSelectAskInfoNew, params);
    }

    private boolean isTrue(Boolean bool) {
        return bool != null && bool;
    }

    private String assemblyTextExact(String exactValue, String searchField, String searchValue,
                                     Map<String, Object> params) {
        StringBuilder sBuilder = new StringBuilder(" and ");
        if ("1".equals(exactValue)) {
            sBuilder.append(searchField).append(" = ").append(toParams(searchValue));
            // params.put(searchField, searchValue);
        } else if ("2".equals(exactValue)) {
            sBuilder.append(searchField).append(" in ( ").append(arrayToString(searchValue.split(","))).append("");
            // params.put(searchField, searchValue.split(","));
        } else if ("3".equals(exactValue)) {
            sBuilder.append(searchField).append(" <> ").append(toParams(searchValue));
            // params.put(searchField, searchValue);
        } else if ("4".equals(exactValue)) {
            sBuilder.append(searchField).append(" not in ( ").append(arrayToString(searchValue.split(","))).append("");
            // params.put(searchField, searchValue.split(","));
        } else {
            return "";
        }
        return sBuilder.toString();
    }

    private String assemblyTextList(String searchField, String searchValue, Map<String, Object> params) {
        StringBuilder sBuilder = new StringBuilder();
        String[] searchValues = searchValue.split(" ");
        for (int i = 0; i < searchValues.length; i++) {
            // params.put(searchField + i, "%" + searchValues[i] + "%");
            sBuilder.append(" and ").append(searchField).append(" like ").append(toParams("%" + searchValues[i] + "%"));
        }
        return sBuilder.toString();
    }

    private String assemblyTextArea(String searchField, String searchValue, Map<String, Object> params) {
        StringBuilder sBuilder = new StringBuilder();
        String[] searchValues = searchValue.split("\\|");
        if (searchValues.length > 0) {
            if (StringUtils.isNotBlank(searchValues[0])) {
                params.put(searchField + "Begin", searchValues[0]);
                sBuilder.append(" and ").append(searchField).append(" >= ").append(toParams(searchValues[0]));
            }
            if (searchValues.length == 2 && StringUtils.isNotBlank(searchValues[1])) {
                params.put(searchField + "End", searchValues[1]);
                sBuilder.append(" and ").append(searchField).append(" <= ").append(toParams(searchValues[1]));
            }
        }
        return sBuilder.toString();
    }

    private String assemblyDate(String searchField, String beginDate, String endDate, Map<String, Object> params) {
        StringBuilder sBuilder = new StringBuilder();
        try {
            if (StringUtils.isNotBlank(beginDate)) {
                Date begin = DateUtils.parseDateTime(beginDate + " 00:00:00");
                params.put(searchField + "Begin", begin);
                sBuilder.append(" and ").append(searchField).append(" >= ").append(":").append(searchField)
                        .append("Begin");
            }
            if (StringUtils.isNotBlank(endDate)) {
                Date end = DateUtils.parseDateTime(endDate + " 23:59:59");
                params.put(searchField + "End", end);
                sBuilder.append(" and ").append(searchField).append(" <= ").append(":").append(searchField)
                        .append("End");
            }
        } catch (ParseException e) {
            // 日期解析错误时,按字符串比较（文件库的日期比较）modify@zhongzh
            if (StringUtils.isNotBlank(beginDate)) {
                params.put(searchField + "Begin", beginDate);
                sBuilder.append(" and ").append(searchField).append(" >= ").append(":" + searchField + "Begin");
            }
            if (StringUtils.isNotBlank(endDate)) {
                params.put(searchField + "End", endDate);
                sBuilder.append(" and ").append(searchField).append(" <= ").append(":" + searchField + "End");
            }
        }
        return sBuilder.toString();
    }

    private String assemblyOrg(String searchField, String searchValue, Map<String, Object> params) {
        StringBuilder sBuilder = new StringBuilder(" and ");
        sBuilder.append(searchField).append("  in ( ").append(arrayToString(searchValue.split(";"))).append(" )");
        // String[] searchValues = searchValue.split(";");
        // params.put(searchField, Arrays.asList(searchValues));
        return sBuilder.toString();
    }

    private String assemblySelect(String searchField, String searchValue, Map<String, Object> params) {
        StringBuilder sBuilder = new StringBuilder(" and ").append(searchField).append(" = ")
                .append(toParams(searchValue));
        // params.put(searchField, searchValue);
        return sBuilder.toString();
    }

    private String toParams(String searchField) {
        return "'" + searchField + "'";
    }
}
