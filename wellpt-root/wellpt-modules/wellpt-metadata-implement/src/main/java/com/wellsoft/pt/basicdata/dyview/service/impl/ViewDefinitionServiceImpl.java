/*
 * @(#)2013-3-13 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.dyview.service.impl;

import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.enums.IdPrefix;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.context.util.reflection.ConvertUtils;
import com.wellsoft.pt.basicdata.datadict.dto.CdDataDictionaryItemDto;
import com.wellsoft.pt.basicdata.datadict.service.DataDictionaryService;
import com.wellsoft.pt.basicdata.dyview.bean.*;
import com.wellsoft.pt.basicdata.dyview.dao.*;
import com.wellsoft.pt.basicdata.dyview.entity.*;
import com.wellsoft.pt.basicdata.dyview.provider.ViewColumn;
import com.wellsoft.pt.basicdata.dyview.provider.ViewDataSource;
import com.wellsoft.pt.basicdata.dyview.service.GetViewDataService;
import com.wellsoft.pt.basicdata.dyview.service.ViewDefinitionService;
import com.wellsoft.pt.basicdata.dyview.support.CondSelectAskInfo;
import com.wellsoft.pt.basicdata.dyview.support.DateUtil;
import com.wellsoft.pt.basicdata.dyview.support.DyViewQueryInfo;
import com.wellsoft.pt.basicdata.dyview.support.DyviewConfig;
import com.wellsoft.pt.basicdata.facade.service.BasicDataApiFacade;
import com.wellsoft.pt.basicdata.systemtable.entity.SystemTable;
import com.wellsoft.pt.basicdata.systemtable.entity.SystemTableAttribute;
import com.wellsoft.pt.basicdata.systemtable.entity.SystemTableRelationship;
import com.wellsoft.pt.basicdata.view.dao.ViewDefinitionNewDao;
import com.wellsoft.pt.basicdata.view.entity.ViewDefinitionNew;
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
import com.wellsoft.pt.security.audit.entity.Resource;
import com.wellsoft.pt.security.facade.service.SecurityApiFacade;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import com.wellsoft.pt.unit.entity.CommonUnit;
import com.wellsoft.pt.unit.facade.service.UnitApiFacade;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.model.Permission;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DateFormat;
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
public class ViewDefinitionServiceImpl extends BaseServiceImpl implements ViewDefinitionService {

    @Autowired
    private ViewAttributeDao viewAttributeDao;
    @Autowired
    private ViewDefinitionNewDao viewDefinitionNewDao;

    @Autowired
    private ColumnAttributeDao columnAttributeDao;

    @Autowired
    private CustomButtonDao customButtonDao;

    @Autowired
    private ColumnCssDefinitionDao columnCssDefinitionDao;

    @Autowired
    private PageAttributeDao pageAttributeDao;

    @Autowired
    private SelectAttributeDao selectAttributeDao;

    @Autowired
    private DyFormFacade dyFormApiFacade;

    @Autowired
    private SecurityApiFacade securityApiFacade;

    @Autowired
    private BasicDataApiFacade basicDataApiFacade;

    @Autowired
    private ConditionTypeDao conditionTypeDao;

    @Autowired
    private ExactKeySelectColDao exactKeySelectColDao;

    @Autowired
    private Map<String, ViewDataSource> viewDataSourceMap;

    @Autowired
    private GetViewDataService getViewDataService;

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

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.dyview.service.ViewDefinitionServiceBak#getById(java.lang.String)
     */
    @Override
    public ViewDefinition getByViewId(String id) {
        return this.viewAttributeDao.findUniqueBy("viewId", id);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.dyview.service.ViewDefinitionServiceBak#searchViewDefinition()
     */
    @Override
    public List<ViewDefinition> searchViewDefinition() {
        String hql = "from ViewDefinition h";
        List<ViewDefinition> viewDefinitions = viewAttributeDao.find(hql);
        return viewDefinitions;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.dyview.service.ViewDefinitionService#searchViewDefinitionNew()
     */
    @Override
    public List<ViewDefinitionNew> searchViewDefinitionNew() {
        String hql = "from ViewDefinitionNew h";
        List<ViewDefinitionNew> viewDefinitions = viewDefinitionNewDao.find(hql);
        return viewDefinitions;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.dyview.service.ViewDefinitionService#searchViewDefinitionByPermission()
     */
    @Override
    public List<ViewDefinition> searchViewDefinitionByPermission() {
        // String hql =
        // " select distinct h from ViewDefinitionNew h inner join h.columnDefinitions r where r.fieldPermission is true";
        String hql = " select distinct r.viewDefinition from ColumnDefinitionNew r where r.fieldPermission is true";

        List<ViewDefinition> viewDefinitions = viewAttributeDao.find(hql);
        return viewDefinitions;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.dyview.service.ViewDefinitionServiceBak#saveViewDefinition(com.wellsoft.pt.basicdata.dyview.entity.ViewDefinition)
     */
    @Override
    public void saveViewDefinition(ViewDefinition entity) {
        // TODO Auto-generated method stub

    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.dyview.service.ViewDefinitionServiceBak#deleteViewDefinition(java.lang.String)
     */
    @Override
    public void deleteViewDefinition(String viewUuid) {
        // TODO Auto-generated method stub

    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.dyview.service.ViewDefinitionServiceBak#getBean(java.lang.String)
     */
    @Override
    public ColumnDefinitionBean getBean(String s) {

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
    public String saveBean(ViewDefinitionBean bean) {
        ViewDefinition viewDefinition = new ViewDefinition();
        if (StringUtils.isNotBlank(bean.getUuid())) {
            viewDefinition = this.viewAttributeDao.get(bean.getUuid());
        } else {
            bean.setUuid(null);
            bean.setId((new StringBuilder("V")).append(UUID.randomUUID()).toString());
        }
        BeanUtils.copyProperties(bean, viewDefinition);
        this.viewAttributeDao.save(viewDefinition);

        // 保存列属性
        Set<ColumnDefinitionBean> beans = new LinkedHashSet<ColumnDefinitionBean>();
        beans = bean.getColumnFields();
        if (StringUtils.isNotBlank(bean.getUuid())) {
            // 删除已存在的列
            for (ColumnDefinition cdf : bean.getColumnDefinitions()) {
                ColumnDefinition columnDefinition = this.columnAttributeDao.get(cdf.getUuid());
                this.columnAttributeDao.delete(columnDefinition);
            }
        }
        for (ColumnDefinition cdf : beans) {
            // cdf.setSortOrder(i);
            if (StringUtils.isNotBlank(cdf.getUuid())) {
                ColumnDefinition columnDefinition = this.columnAttributeDao.get(cdf.getUuid());
                BeanUtils.copyProperties(cdf, columnDefinition);
                columnDefinition.setViewDefinition(viewDefinition);
                this.columnAttributeDao.save(columnDefinition);
            } else {
                ColumnDefinition columnDefinition = new ColumnDefinition();
                BeanUtils.copyProperties(cdf, columnDefinition);
                columnDefinition.setViewDefinition(viewDefinition);
                this.columnAttributeDao.save(columnDefinition);
            }
        }
        // 样式定义
        Set<ColumnCssDefinitionBean> columnCssDefinitionBeans = new LinkedHashSet<ColumnCssDefinitionBean>();
        columnCssDefinitionBeans = bean.getColumnCssDefinitionFields();
        if (StringUtils.isNotBlank(bean.getUuid())) {
            // 删除已存在的行样式
            for (ColumnCssDefinition ccdf : bean.getColumnCssDefinition()) {
                ColumnCssDefinition columnCssDefinition = this.columnCssDefinitionDao.get(ccdf.getUuid());
                this.columnCssDefinitionDao.delete(columnCssDefinition);
            }
        }
        for (ColumnCssDefinition ccdf : columnCssDefinitionBeans) {
            if (StringUtils.isNotBlank(ccdf.getUuid())) {
                ColumnCssDefinition columnCssDefinition = this.columnCssDefinitionDao.get(ccdf.getUuid());
                BeanUtils.copyProperties(ccdf, columnCssDefinition);
                columnCssDefinition.setViewDefinition(viewDefinition);
                this.columnCssDefinitionDao.save(columnCssDefinition);
            } else {
                ColumnCssDefinition columnCssDefinition = new ColumnCssDefinition();
                BeanUtils.copyProperties(ccdf, columnCssDefinition);
                columnCssDefinition.setViewDefinition(viewDefinition);
                this.columnCssDefinitionDao.save(columnCssDefinition);
            }
        }

        Set<CustomButtonBean> customBeans = new LinkedHashSet<CustomButtonBean>();
        customBeans = bean.getCustomButtonFields();
        if (StringUtils.isNotBlank(bean.getUuid())) {
            // 删除已存在的按钮
            for (CustomButton cdf : bean.getCustomButtons()) {
                CustomButton customButton = this.customButtonDao.get(cdf.getUuid());
                this.customButtonDao.delete(customButton);
            }
        }
        for (CustomButton cdf : customBeans) {
            if (StringUtils.isNotBlank(cdf.getUuid())) {
                CustomButton customButton = this.customButtonDao.get(cdf.getUuid());
                BeanUtils.copyProperties(cdf, customButton);
                customButton.setViewDefinition(viewDefinition);
                this.customButtonDao.save(customButton);
            } else {
                CustomButton customButton = new CustomButton();
                BeanUtils.copyProperties(cdf, customButton);
                customButton.setViewDefinition(viewDefinition);
                this.customButtonDao.save(customButton);
            }
        }

        // 保存分页属性
        PageDefinitionBean pageBean = bean.getPageFields();

        PageDefinition pageDefinition = viewDefinition.getPageDefinitions();
        if (pageDefinition != null) {
            BeanUtils.copyProperties(pageBean, pageDefinition, new String[]{IdEntity.UUID});
            this.pageAttributeDao.save(pageDefinition);
        } else {
            // 新建一个分页定义
            PageDefinition pageDefinitionNew = new PageDefinition();
            BeanUtils.copyProperties(pageBean, pageDefinitionNew);
            pageDefinitionNew.setViewDefinition(viewDefinition);
            this.pageAttributeDao.save(pageDefinitionNew);
        }

        // 保存查询属性
        SelectDefinitionBean selectBean = bean.getSelectFields();
        SelectDefinitionBean selectDeleteBean = bean.getSelectDeleteFields();
        SelectDefinition selectDefinition = viewDefinition.getSelectDefinitions();
        if (selectDefinition != null) {
            BeanUtils.copyProperties(selectBean, selectDefinition, new String[]{IdEntity.UUID});
            this.selectAttributeDao.save(selectDefinition);
            if (selectDeleteBean != null) {
                Set<ConditionTypeBean> conditionTypeDeBeans = selectDeleteBean.getConditionTypeFields();
                for (ConditionType cdt : conditionTypeDeBeans) {
                    if (StringUtils.isNotBlank(cdt.getUuid())) {
                        // 备选项
                        ConditionType conditionType = this.conditionTypeDao.get(cdt.getUuid());
                        this.conditionTypeDao.delete(conditionType);
                    }
                }

                Set<ExactKeySelectCol> exactKeySelectCols = selectDeleteBean.getExactKeySelectCols();
                for (ExactKeySelectCol ekc : exactKeySelectCols) {
                    if (StringUtils.isNotBlank(ekc.getUuid())) {
                        // 备选项
                        ExactKeySelectCol exactKeySelectCol = this.exactKeySelectColDao.get(ekc.getUuid());
                        this.exactKeySelectColDao.delete(exactKeySelectCol);
                    }
                }
            }

            // 如果用户定义了关键字查询并且定义了精确关键字查询
            if (selectBean.getForKeySelect() == true && selectBean.getExactKeySelect() == true) {
                Set<ExactKeySelectColBean> exactKeySelectColBeans = selectBean.getExactKeySelectColBeans();
                for (ExactKeySelectCol esc : exactKeySelectColBeans) {
                    if (StringUtils.isNotBlank(esc.getUuid())) {
                        ExactKeySelectCol exactKeySelectCol = this.exactKeySelectColDao.get(esc.getUuid());
                        BeanUtils.copyProperties(esc, exactKeySelectCol);
                        exactKeySelectCol.setSelectDefinition(selectDefinition);
                        this.exactKeySelectColDao.save(exactKeySelectCol);
                    } else {
                        ExactKeySelectCol exactKeySelectCol = new ExactKeySelectCol();
                        BeanUtils.copyProperties(esc, exactKeySelectCol);
                        exactKeySelectCol.setSelectDefinition(selectDefinition);
                        this.exactKeySelectColDao.save(exactKeySelectCol);
                    }
                }
            }

            Set<ConditionTypeBean> conditionTypeBeans = selectBean.getConditionTypeFields();
            for (ConditionType cdt : conditionTypeBeans) {
                if (StringUtils.isNotBlank(cdt.getUuid())) {
                    ConditionType conditionType = this.conditionTypeDao.get(cdt.getUuid());
                    BeanUtils.copyProperties(cdt, conditionType);
                    conditionType.setSelectDefinition(selectDefinition);
                    this.conditionTypeDao.save(conditionType);
                } else {
                    ConditionType conditionType = new ConditionType();
                    BeanUtils.copyProperties(cdt, conditionType);
                    conditionType.setSelectDefinition(selectDefinition);
                    this.conditionTypeDao.save(conditionType);
                }
            }
        } else {
            // 新建一个查询定义
            SelectDefinition selectDefinitionNew = new SelectDefinition();
            BeanUtils.copyProperties(selectBean, selectDefinitionNew);
            selectDefinitionNew.setViewDefinition(viewDefinition);
            this.selectAttributeDao.save(selectDefinitionNew);

            // 如果用户定义了关键字查询并且定义了精确关键字查询
            if (selectBean.getForKeySelect() == true && selectBean.getExactKeySelect() == true) {
                Set<ExactKeySelectColBean> exactKeySelectColBeans = selectBean.getExactKeySelectColBeans();
                for (ExactKeySelectCol esc : exactKeySelectColBeans) {
                    if (StringUtils.isNotBlank(esc.getUuid())) {
                        ExactKeySelectCol exactKeySelectCol = this.exactKeySelectColDao.get(esc.getUuid());
                        BeanUtils.copyProperties(esc, exactKeySelectCol);
                        exactKeySelectCol.setSelectDefinition(selectDefinition);
                        this.exactKeySelectColDao.save(exactKeySelectCol);
                    } else {
                        ExactKeySelectCol exactKeySelectCol = new ExactKeySelectCol();
                        BeanUtils.copyProperties(esc, exactKeySelectCol);
                        exactKeySelectCol.setSelectDefinition(selectDefinition);
                        this.exactKeySelectColDao.save(exactKeySelectCol);
                    }
                }
            }

            Set<ConditionTypeBean> conditionTypeBeans = selectBean.getConditionTypeFields();
            for (ConditionType cdt : conditionTypeBeans) {
                if (StringUtils.isNotBlank(cdt.getUuid())) {
                    ConditionType conditionType = this.conditionTypeDao.get(cdt.getUuid());
                    BeanUtils.copyProperties(cdt, conditionType);
                    conditionType.setSelectDefinition(selectDefinitionNew);
                    this.conditionTypeDao.save(conditionType);
                } else {
                    ConditionType conditionType = new ConditionType();
                    BeanUtils.copyProperties(cdt, conditionType);
                    conditionType.setSelectDefinition(selectDefinitionNew);
                    this.conditionTypeDao.save(conditionType);
                }
            }
        }

        return viewDefinition.getUuid();
    }

    /**
     * 根据uuid获取视图的详细信息
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.dyview.service.ViewDefinitionServiceBak#getBeanByUuid(java.lang.String)
     */
    @Override
    public ViewDefinitionBean getBeanByUuid(String uuid) {
        ViewDefinition viewDefinition = viewAttributeDao.getByUuid(uuid);
        ViewDefinitionBean viewDefinitionBean = new ViewDefinitionBean();
        BeanUtils.copyProperties(viewDefinition, viewDefinitionBean);
        // 设置列定义
        Set<ColumnDefinition> columnDefinitions = viewDefinition.getColumnDefinitions();
        viewDefinitionBean.setColumnDefinitions(BeanUtils.convertCollection(columnDefinitions, ColumnDefinition.class));
        // 设置样式定义
        Set<ColumnCssDefinition> columnCssDefinitions = viewDefinition.getColumnCssDefinition();
        viewDefinitionBean.setColumnCssDefinition(BeanUtils.convertCollection(columnCssDefinitions,
                ColumnCssDefinition.class));
        // 设置按钮定义
        Set<CustomButton> customButtons = viewDefinition.getCustomButtons();
        viewDefinitionBean.setCustomButtons(BeanUtils.convertCollection(customButtons, CustomButton.class));

        // 设置分页定义
        PageDefinition pageDefinition = viewDefinition.getPageDefinitions();
        PageDefinition pageDefinition2 = new PageDefinition();
        if (pageDefinition != null) {
            BeanUtils.copyProperties(pageDefinition, pageDefinition2);
            viewDefinitionBean.setPageDefinitions(pageDefinition2);
        }

        // 设置查询定义
        SelectDefinition selectDefinition = viewDefinition.getSelectDefinitions();
        SelectDefinition selectDefinition2 = new SelectDefinition();
        if (selectDefinition != null) {
            Set<ConditionType> conditionTypes = selectDefinition.getConditionType();
            Set<ExactKeySelectCol> exactKeySelectCols = selectDefinition.getExactKeySelectCols();
            BeanUtils.copyProperties(selectDefinition, selectDefinition2);
            selectDefinition2.setConditionType(BeanUtils.convertCollection(conditionTypes, ConditionType.class));
            selectDefinition2.setExactKeySelectCols(BeanUtils.convertCollection(exactKeySelectCols,
                    ExactKeySelectCol.class));
            viewDefinitionBean.setSelectDefinitions(selectDefinition2);
        } else {
            Set<ConditionType> conditionTypeNew = new HashSet<ConditionType>();
            selectDefinition2.setConditionType(conditionTypeNew);
            viewDefinitionBean.setSelectDefinitions(selectDefinition2);
        }
        return viewDefinitionBean;
    }

    /**
     * 根据jqgrid的行id来获取视图的详细信息
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.dyview.service.ViewDefinitionServiceBak#getBeanById(java.lang.String)
     */
    @Override
    public ViewDefinitionBean getBeanById(String id) {
        ViewDefinition viewDefinition = viewAttributeDao.getById(id);
        ViewDefinitionBean bean = new ViewDefinitionBean();
        BeanUtils.copyProperties(viewDefinition, bean);
        // 设置列定义
        Set<ColumnDefinition> columnDefinitions = viewDefinition.getColumnDefinitions();
        bean.setColumnDefinitions(BeanUtils.convertCollection(columnDefinitions, ColumnDefinition.class));
        // 设置样式定义
        Set<ColumnCssDefinition> columnCssDefinitions = viewDefinition.getColumnCssDefinition();
        if (columnCssDefinitions.isEmpty() == false) {
            bean.setColumnCssDefinition(BeanUtils.convertCollection(columnCssDefinitions, ColumnCssDefinition.class));
        }

        // 设置按钮定义
        Set<CustomButton> customButtons = viewDefinition.getCustomButtons();
        bean.setCustomButtons(BeanUtils.convertCollection(customButtons, CustomButton.class));

        // 设置分页定义
        PageDefinition pageDefinition = viewDefinition.getPageDefinitions();
        PageDefinition pageDefinition2 = new PageDefinition();
        if (pageDefinition != null) {
            BeanUtils.copyProperties(pageDefinition, pageDefinition2);
            bean.setPageDefinitions(pageDefinition2);
        }
        // 设置选择角色数据
        String roleType = viewDefinition.getRoleType();
        if (roleType != null) {
            QueryItem role = dataDictionaryService.getKeyValuePair("DATA_PERMISSION", roleType);
            bean.setRoleName((String) role.get("label"));
        } else {
            bean.setRoleName("");
        }
        // 设置查询定义
        SelectDefinition selectDefinition = viewDefinition.getSelectDefinitions();
        SelectDefinition selectDefinition2 = new SelectDefinition();
        if (selectDefinition != null) {
            Set<ConditionType> conditionTypes = selectDefinition.getConditionType();
            Set<ExactKeySelectCol> exactKeySelectCols = selectDefinition.getExactKeySelectCols();
            BeanUtils.copyProperties(selectDefinition, selectDefinition2);
            selectDefinition2.setConditionType(BeanUtils.convertCollection(conditionTypes, ConditionType.class));
            selectDefinition2.setExactKeySelectCols(BeanUtils.convertCollection(exactKeySelectCols,
                    ExactKeySelectCol.class));
            bean.setSelectDefinitions(selectDefinition2);
        } else {
            Set<ConditionType> conditionTypeNew = new HashSet<ConditionType>();
            selectDefinition2.setConditionType(conditionTypeNew);
            bean.setSelectDefinitions(selectDefinition2);
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
        ViewDefinition viewDefinition = viewAttributeDao.getById(id);
        viewAttributeDao.delete(viewDefinition);
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
            ViewDefinition viewDefinition = viewAttributeDao.getById(ids[i]);
            viewAttributeDao.delete(viewDefinition);
        }
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
                                          Set<ColumnDefinition> columnDefinitions, PageDefinition pageDefinition, DyViewQueryInfo dyViewQueryInfo,
                                          String count) {

        List<QueryItem> data = new ArrayList<QueryItem>();
        Set<String> choorseNames = new HashSet<String>();
        Set<String> unitNames = new HashSet<String>();
        StringBuilder orderBy = new StringBuilder();
        // 分页的请求参数信息
        PagingInfo page = new PagingInfo();
        if (dyViewQueryInfo.getPageInfo() != null) {
            page = dyViewQueryInfo.getPageInfo();
        }
        // 前端所有的请求参数类
        CondSelectAskInfo askInfo = dyViewQueryInfo.getCondSelect();
        String orderTitle = "";
        String orderbyArr = "";
        List<Map<String, String>> keyWords = new ArrayList<Map<String, String>>();
        String appointColumn = "";
        String optionValue = "";
        String beginTime = "";
        String endTime = "";
        String searchField = "";
        if (askInfo != null) {
            // 点击排序
            if (StringUtils.isNotEmpty(askInfo.getOrderTitle())) {
                orderTitle = askInfo.getOrderTitle();
            }
            if (StringUtils.isNotEmpty(askInfo.getOrderbyArr())) {
                orderbyArr = askInfo.getOrderbyArr();
            }
            // 关键字
            if (askInfo.getKeyWords() != null && askInfo.getKeyWords().size() != 0) {
                keyWords = askInfo.getKeyWords();
            }
            // 备选项对应的列名称
            if (StringUtils.isNotEmpty(askInfo.getAppointColumn())) {
                appointColumn = askInfo.getAppointColumn();
            }
            // 备选项的真实值
            if (StringUtils.isNotEmpty(askInfo.getOptionValue())) {
                optionValue = askInfo.getOptionValue();
            }
            // 时间查询的请求参数
            if (StringUtils.isNotEmpty(askInfo.getBeginTime())) {
                beginTime = askInfo.getBeginTime();
            }
            if (StringUtils.isNotEmpty(askInfo.getEndTime())) {
                endTime = askInfo.getEndTime();
            }
            if (StringUtils.isNotEmpty(askInfo.getSearchField())) {
                searchField = askInfo.getSearchField();
            }
        }

        StringBuilder selection = new StringBuilder();

        List<ViewColumn> viewColumns = new ArrayList<ViewColumn>();
        Map<String, ViewColumn> map = ConvertUtils.convertElementToMap(viewDataSourceMap.get(tableName)
                .getAllViewColumns(), "columnAlias");
        String str = "";
        String timeSql = "";
        Map paraMap = new HashMap();
        /*********************搜索条件*************************/
        String keyWordType = "field";
        String keyWordValue = "";
        if (keyWords != null && keyWords.size() != 0) {
            for (int j = 0; j < keyWords.size(); j++) {
                Map<String, String> keyWord = keyWords.get(j);
                for (String key : keyWord.keySet()) {
                    if (key.equals("all")) {
                        keyWordType = "all";
                        keyWordValue = keyWord.get("all").toString();
                        break;
                    }
                }

            }
        }

        for (ColumnDefinition columnDefinition : columnDefinitions) {
            ViewColumn viewColumn = map.get(columnDefinition.getFieldName());
            if (viewColumn.isQueryField() == true) {

                if (viewColumn == null) {
                    viewColumn = map.get(columnDefinition.getColumnAliase());
                }
                viewColumns.add(viewColumn);
                String field = viewColumn.getAttributeName();
                String titleName = columnDefinition.getTitleName();
                /**************排序*************/
                String defaultSort = columnDefinition.getDefaultSort();
                String ColumnType = columnDefinition.getColumnType();
                String columnDataType = viewColumn.getColumnType().toString();

                if (field != null && !"".equals(field) && columnDataType != null && columnDataType.equals("STRING")
                        && keyWords.size() != 0) {
                    if (field.indexOf(".") < 0 || (field.indexOf(".") > -1 && field.split("\\.")[0].length() > 1)) {
                        field = " o." + field;
                    }

                    if (keyWordType.equals("all") && !field.equals(" o.uuid")) {// 全部列模糊查询
                        String[] keyValues = keyWordValue.split(",");
                        for (int k = 0; k < keyValues.length; k++) {
                            if ("用户".equals(ColumnType)) {
                                List<String> users = orgApiFacade.getOrgIdsLikeName(keyValues[k]);
                                if (!users.isEmpty()) {
                                    str += " or " + field + " in (";
                                    StringBuilder sb = new StringBuilder();
                                    for (int m = 0; m < users.size(); m++) {
                                        sb.append(",'" + users.get(m) + "'");
                                    }
                                    str += sb.toString().replaceFirst(",", "") + ")";
                                }
                            } else if ("单位".equals(ColumnType)) {
                                List<CommonUnit> commonUnits = unitApiFacade.getCommonUnitsByBlurUnitName(keyValues[k]);
                                if (!commonUnits.isEmpty()) {
                                    str += " or " + field + " in (";
                                    StringBuilder sb = new StringBuilder();
                                    for (int m = 0; m < commonUnits.size(); m++) {
                                        sb.append(",'" + commonUnits.get(m).getUnitId() + "'");
                                    }
                                    str += sb.toString().replaceFirst(",", "") + ")";
                                }
                            } else {
                                str += " or " + "lower(" + field + ") like '%'||lower('" + keyValues[k] + "')||'%'";
                                // str += " or " + field + " like " + "'%" +
                                // keyValues[k] + "%'";
                            }
                        }

                    } else {
                        for (int j = 0; j < keyWords.size(); j++) {
                            Map<String, String> keyWord = keyWords.get(j);
                            // 遍历关键字的map
                            for (String key : keyWord.keySet()) {
                                if (key.equals(titleName)) {
                                    if ("用户".equals(ColumnType)) {
                                        List<String> users = orgApiFacade.getOrgIdsLikeName(keyWord.get(key));
                                        if (!users.isEmpty()) {
                                            str += " and " + field + " in (";
                                            StringBuilder sb = new StringBuilder();
                                            for (int m = 0; m < users.size(); m++) {
                                                sb.append(",'" + users.get(m) + "'");
                                            }
                                            str += sb.toString().replaceFirst(",", "") + ")";
                                        }
                                    } else if ("单位".equals(ColumnType)) {
                                        List<CommonUnit> commonUnits = unitApiFacade
                                                .getCommonUnitsByBlurUnitName(keyWords.get(j).toString());
                                        if (!commonUnits.isEmpty()) {
                                            str += " and " + field + " in (";
                                            StringBuilder sb = new StringBuilder();
                                            for (int m = 0; m < commonUnits.size(); m++) {
                                                sb.append(",'" + commonUnits.get(m).getUnitId() + "'");
                                            }
                                            str += sb.toString().replaceFirst(",", "") + ")";
                                        }
                                    } else {
                                        str += " and " + "lower(" + field + ") like '%'||lower('" + keyWord.get(key)
                                                + "')||'%'";
                                        // str += " and " + field + " like " +
                                        // "'%" + keyWord.get(key) + "%'";
                                    }
                                }
                            }
                        }
                    }

                } else if (StringUtils.isNotBlank(field) && columnDataType != null && columnDataType.equals("DATE")
                        && keyWords.size() != 0 && !StringUtils.isBlank(searchField) && field.indexOf(searchField) > -1) {
                    DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                    if (!StringUtils.isBlank(beginTime)) {
                        try {
                            Date startDate = format.parse(beginTime);
                            timeSql += field + ">:startDate";
                            paraMap.put("startDate", startDate);
                        } catch (ParseException e) {
                            logger.error(ExceptionUtils.getStackTrace(e));
                        }
                    }
                    if (!StringUtils.isBlank(endTime)) {
                        try {
                            Date endDate = format.parse(endTime);
                            if (!StringUtils.isBlank(timeSql)) {
                                timeSql += " and " + field + "<:endDate";
                            } else {
                                timeSql += field + "< :endDate";
                            }
                            paraMap.put("endDate", endDate);
                        } catch (ParseException e) {
                            logger.error(ExceptionUtils.getStackTrace(e));
                        }
                    }
                }
                // 点击排序
                if (StringUtils.isNotBlank(orderTitle) && orderTitle.equals(titleName)) {
                    orderBy.append("," + field).append(" " + orderbyArr);
                }
                // 默认排序
                if (defaultSort != null) {
                    if (defaultSort.equals(DyviewConfig.DYVIEW_SORT_ASC)) {
                        orderBy.append("," + field).append(" asc");
                    } else if (defaultSort.equals(DyviewConfig.DYVIEW_SORT_DESC)) {
                        orderBy.append("," + field).append(" desc");
                    }
                }

                // 按条件排序的处理
                if (titleName != null && !"".equals(titleName) && titleName.equals(appointColumn)) {
                    if (field.indexOf(".") < 0 || (field.indexOf(".") > -1 && field.split("\\.")[0].length() > 1)) {
                        field = " o." + field;
                    }
                    if (defaultCondition.equals("")) {
                        defaultCondition += field + " like " + "'%" + optionValue + "%'";
                    } else {
                        defaultCondition += " and " + field + " like " + "'%" + optionValue + "%'";
                    }
                }

                if (columnDefinition.getColumnType().equals("用户")) {
                    choorseNames.add(columnDefinition.getFieldName());
                }
                if (columnDefinition.getColumnType().equals("单位")) {
                    unitNames.add(columnDefinition.getFieldName());
                }
            }
        }
        if (count != null && !"".equals(count)) {
            page.setPageSize(Integer.parseInt(count));
        }

        if (str != null) {
            if (keyWordType.equals("all")) {
                str = str.replaceFirst("or", "");
            } else {
                str = str.replaceFirst("and", "");
            }
        }
        if (StringUtils.isBlank(str)) {
            str = "1=1";
        }
        if (StringUtils.isNotBlank(defaultCondition) && !defaultCondition.equals("") && columnDefinitions.size() > 0) {
            defaultCondition = "(" + defaultCondition + ")" + " and (" + str + ")";
            if (!StringUtils.isBlank(timeSql)) {
                defaultCondition = "(" + defaultCondition + ")" + " and " + timeSql;
            }
        } else if (StringUtils.isBlank(defaultCondition) && columnDefinitions.size() > 0) {
            defaultCondition = str;
            if (!StringUtils.isBlank(timeSql)) {
                defaultCondition = "(" + defaultCondition + ")" + " and " + timeSql;
            }
        }
        Long count_ = viewDataSourceMap.get(tableName).count(viewColumns, "(" + defaultCondition + ")",
                new HashMap<String, Object>());
        page.setTotalCount(count_);
        data = viewDataSourceMap.get(tableName).query(viewColumns, "(" + defaultCondition + ")",
                new HashMap<String, Object>(), orderBy == null ? "" : orderBy.toString().replaceFirst(",", ""), page);
        // 将列类型是用户的值通过调用组织机构的接口获取到用户的显示值显示到页面上
        getDataByUser(data, choorseNames);
        getDataByUnit(data, unitNames);
        return data;
    }

    @Override
    public long getView3Count(String tableName, String whereHql) {
        return viewDataSourceMap.get(tableName).count(null, whereHql, new HashMap<String, Object>());
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
        String[] Ids = viewUuid.split(",");
        String viewuuid = Ids[0];
        String moduldid = Ids[1];

        PagingInfo page = new PagingInfo();
        // 获取视图下所有的列字段的数据
        ViewDefinitionBean viewDefinitionBean = new ViewDefinitionBean();
        viewDefinitionBean = getBeanByUuid(viewuuid);
        Set<ColumnDefinition> columnDefinitions = viewDefinitionBean.getColumnDefinitions();
        String tableName = viewDefinitionBean.getTableDefinitionName();
        TreeNode treenode = new TreeNode();
        List<TreeNode> list1 = treenode.getChildren();
        List<QueryItem> data = new ArrayList<QueryItem>();
        // 计数器
        int i = 0;
        List<ViewColumn> viewColumns = new ArrayList<ViewColumn>();
        Map<String, ViewColumn> map = ConvertUtils.convertElementToMap(viewDataSourceMap.get(tableName)
                .getAllViewColumns(), "columnAlias");
        for (ColumnDefinition columnDefinition : columnDefinitions) {
            viewColumns.add(map.get(columnDefinition.getFieldName()));
            i++;
        }
        data = viewDataSourceMap.get(tableName).query(viewColumns, "", new HashMap<String, Object>(), "", page);
        if (data.size() != 0) {
            for (int index = 0; index < data.size(); index++) {
                if (data.get(index).get("parentUuid").equals(moduldid)) {
                    String title = (String) data.get(index).get("title");
                    String fileUuid = (String) data.get(index).get("uuid");
                    TreeNode treenode1 = new TreeNode();
                    treenode1.setName(title);
                    treenode1.setId(fileUuid);
                    list1.add(treenode1);
                }
            }
        }

        return list1;
    }

    /**
     * 分类获得所有的视图
     *
     * @return
     */
    public List<TreeNode> getViewAsTreeAsync(String id) {
        List<CdDataDictionaryItemDto> ddList = basicDataApiFacade.getDataDictionariesByType("MODULE_CATEGORY");
        List<ViewDefinition> viewDefinitions = this.searchViewDefinition();
        List<TreeNode> treeNodes = new ArrayList<TreeNode>();
        for (CdDataDictionaryItemDto d : ddList) {
            TreeNode node = new TreeNode();
            node.setId(d.getValue());
            node.setData(d.getValue());
            node.setNocheck(true);
            node.setName(d.getLabel());
            List<TreeNode> childTreeNodes = new ArrayList<TreeNode>();
            for (ViewDefinition v : viewDefinitions) {
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
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.dyview.service.ViewDefinitionService#getViewNewAsTreeAsync(java.lang.String)
     */
    @Override
    public List<TreeNode> getViewNewAsTreeAsync(String id) {
        List<CdDataDictionaryItemDto> ddList = basicDataApiFacade.getDataDictionariesByType("MODULE_CATEGORY");
        List<ViewDefinitionNew> viewDefinitions = this.searchViewDefinitionNew();
        List<ViewDefinition> viewDefinitionOlds = this.searchViewDefinition();
        List<TreeNode> treeNodes = new ArrayList<TreeNode>();
        for (CdDataDictionaryItemDto d : ddList) {
            TreeNode node = new TreeNode();
            node.setId(Objects.toString(d.getUuid(), StringUtils.EMPTY));
            node.setData(d.getUuid());
            node.setNocheck(true);
            node.setName(d.getLabel());
            List<TreeNode> childTreeNodes = new ArrayList<TreeNode>();
            for (ViewDefinitionNew v : viewDefinitions) {
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
            for (ViewDefinition v : viewDefinitionOlds) {
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
    public List<QueryItem> getColumnData(String defaultCondition, String tableName,
                                         Set<ColumnDefinition> columnDefinitions, PageDefinition pageDefinition, DyViewQueryInfo dyViewQueryInfo) {

        // 前端所有的请求参数类
        CondSelectAskInfo askInfo = dyViewQueryInfo.getCondSelect();
        String orderTitle = "";
        String orderbyArr = "";
        List<Map<String, String>> keyWords = new ArrayList<Map<String, String>>();
        String appointColumn = "";
        String optionValue = "";
        String beginTime = "";
        String endTime = "";
        String searchField = "";
        if (askInfo != null) {
            // 点击排序
            if (StringUtils.isNotEmpty(askInfo.getOrderTitle())) {
                orderTitle = askInfo.getOrderTitle();
            }
            if (StringUtils.isNotEmpty(askInfo.getOrderbyArr())) {
                orderbyArr = askInfo.getOrderbyArr();
            }
            // 关键字
            if (askInfo.getKeyWords() != null && askInfo.getKeyWords().size() != 0) {
                keyWords = askInfo.getKeyWords();
            }
            // 备选项对应的列名称
            if (StringUtils.isNotEmpty(askInfo.getAppointColumn())) {
                appointColumn = askInfo.getAppointColumn();
            }
            // 备选项的真实值
            if (StringUtils.isNotEmpty(askInfo.getOptionValue())) {
                optionValue = askInfo.getOptionValue();
            }
            // 时间查询的请求参数
            if (StringUtils.isNotEmpty(askInfo.getBeginTime())) {
                beginTime = askInfo.getBeginTime();
            }
            if (StringUtils.isNotEmpty(askInfo.getEndTime())) {
                endTime = askInfo.getEndTime();
            }
            if (StringUtils.isNotEmpty(askInfo.getSearchField())) {
                searchField = askInfo.getSearchField();
            }
        }

        List<QueryItem> data = new ArrayList<QueryItem>();
        StringBuilder selection = new StringBuilder();
        selection.append(" (");
        StringBuilder selectionTemp = new StringBuilder();
        Set<String> choorseNames = new HashSet<String>();
        Set<String> unitNames = new HashSet<String>();
        StringBuilder orderBy = new StringBuilder();
        int firstResult = 0;
        int maxResults = 0;
        List<String> fieldName = new ArrayList<String>();
        PagingInfo page = new PagingInfo();
        if (dyViewQueryInfo.getPageInfo() != null) {
            page = dyViewQueryInfo.getPageInfo();
            firstResult = page.getPageSize() * (page.getCurrentPage() - 1);
            maxResults = page.getPageSize();
        } else {
            if (pageDefinition.getIsPaging() == true) {
                firstResult = pageDefinition.getPageNum() * (page.getCurrentPage() - 1);
                maxResults = pageDefinition.getPageNum();
            }
        }

        // 计数器
        // int i = 0;
        for (ColumnDefinition columnDefinition : columnDefinitions) {
            String field = columnDefinition.getFieldName();
            String titleName = columnDefinition.getTitleName();
            fieldName.add(field + " as " + field);
            /**************排序*************/
            String defaultSort = columnDefinition.getDefaultSort();
            // 点击排序
            if (StringUtils.isNotBlank(orderTitle) && orderTitle.equals(titleName)) {
                orderBy.append("," + field).append(" " + orderbyArr);
            }
            // 默认排序
            if (defaultSort != null) {
                if (defaultSort.equals(DyviewConfig.DYVIEW_SORT_ASC)) {
                    orderBy.append("," + field).append(" asc");
                } else if (defaultSort.equals(DyviewConfig.DYVIEW_SORT_DESC)) {
                    orderBy.append("," + field).append(" desc");
                }
            }
            /*********************搜索条件*************************/
            String keyWordType = "field";
            String keyWordValue = "";
            for (int j = 0; j < keyWords.size(); j++) {
                Map<String, String> keyWord = keyWords.get(j);
                for (String key : keyWord.keySet()) {
                    if (key.equals("all")) {
                        keyWordType = "all";
                        keyWordValue = keyWord.get("all").toString();
                        break;
                    }
                }

            }
            String columnDataType = columnDefinition.getColumnDataType();
            if (keyWords.size() != 0 && StringUtils.isNotBlank(field) && columnDataType != "2") {
                if (keyWordType.equals("all") && !field.equals("uuid")) {// 全部列模糊查询
                    String[] keyValues = keyWordValue.split(",");
                    for (int k = 0; k < keyValues.length; k++) {
                        if ("20".equals(columnDataType)) {
                            List<String> users = orgApiFacade.getOrgIdsLikeName(keyValues[k]);
                            if (!users.isEmpty()) {
                                StringBuilder sb = new StringBuilder();
                                for (int m = 0; m < users.size(); m++) {
                                    sb.append(",'" + users.get(m) + "'");
                                }
                                selectionTemp.append(" or " + field + " in (" + sb.toString().replaceFirst(",", "")
                                        + ")");
                            }
                        } else {
                            selectionTemp.append(" or " + field + " like " + "'%" + keyValues[k] + "%'");
                        }
                    }
                } else {// 精确列
                    for (int j = 0; j < keyWords.size(); j++) {
                        Map<String, String> keyWord = keyWords.get(j);
                        // 遍历关键字的map
                        for (String key : keyWord.keySet()) {
                            if (key.equals(titleName)) {
                                if ("20".equals(columnDataType)) {
                                    List<String> users = orgApiFacade.getOrgIdsLikeName(keyWord.get(key));
                                    if (!users.isEmpty()) {
                                        StringBuilder sb = new StringBuilder();
                                        for (int m = 0; m < users.size(); m++) {
                                            sb.append(",'" + users.get(m) + "'");
                                        }
                                        selectionTemp.append(" or " + field + " in ("
                                                + sb.toString().replaceFirst(",", "") + ")");
                                    }
                                } else {
                                    selectionTemp.append(" or " + field + " like " + "'%" + keyWord.get(key) + "%'");
                                }
                            }
                        }
                    }
                }
            }

            if (StringUtils.isNotBlank(appointColumn) && StringUtils.isNotBlank(field)
                    && titleName.equals(appointColumn)) {
                selectionTemp.append(field + " like " + "'%" + optionValue + "%'");
            }

            if (columnDefinition.getColumnType().equals("用户")) {
                choorseNames.add(columnDefinition.getFieldName());
            }
            if (columnDefinition.getColumnType().equals("单位")) {
                unitNames.add(columnDefinition.getFieldName());
            }

        }
        if (StringUtils.isNotEmpty(selectionTemp.toString())) {
            selection.append(selectionTemp.toString()).append(" )");
        } else {
            selection.append("1=1)");
        }

        if (defaultCondition != null && !defaultCondition.equals("")) {
            selection.append(" and " + defaultCondition);
        }
        String[] fieldNames = fieldName.toArray(new String[fieldName.size()]);
        // 是否去掉重复的数据(后续：视图要加入去重定义)
        boolean distinct = false;
        if (keyWords.size() != 0) {
            List<QueryItem> totalData = dyFormApiFacade.query(tableName, distinct, fieldNames, selection.toString(),
                    null, null, null, orderBy.toString().replaceFirst(",", " "), 0, 0);
            page.setTotalCount(totalData.size());
        }

        data = dyFormApiFacade.query(tableName, distinct, fieldNames, selection.toString(), null, null, null, orderBy
                .toString().replaceFirst(",", " "), firstResult, maxResults);

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
    public List<QueryItem> getSortColumnData(String defaultCondition, String tableName,
                                             Set<ColumnDefinition> columnDefinitions, String title, PageDefinition pageDefinition, PagingInfo page,
                                             String orderbyArr) {
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
        if (pageDefinition.getIsPaging() == true) {
            firstResult = pageDefinition.getPageNum() * (page.getCurrentPage() - 1);
            maxResults = pageDefinition.getPageNum();
        }
        // 计数器
        int i = 0;
        for (ColumnDefinition columnDefinition : columnDefinitions) {
            String field = columnDefinition.getFieldName();
            String titleName = columnDefinition.getTitleName();
            fieldName.add(field + " as " + field);
            String defaultSort = columnDefinition.getDefaultSort();
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
            if (columnDefinition.getColumnType().equals("用户")) {
                choorseNames.add(columnDefinition.getFieldName());
            }
            if (columnDefinition.getColumnType().equals("单位")) {
                unitNames.add(columnDefinition.getFieldName());
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
                                              Set<ColumnDefinition> columnDefinitions, String title, PageDefinition pageDefinition, PagingInfo page,
                                              String orderbyArr) {
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
        List<ViewColumn> viewColumns = new ArrayList<ViewColumn>();
        Map<String, ViewColumn> map = ConvertUtils.convertElementToMap(viewDataSourceMap.get(tableName)
                .getAllViewColumns(), "columnAlias");
        for (ColumnDefinition columnDefinition : columnDefinitions) {
            viewColumns.add(map.get(columnDefinition.getFieldName()));
            String field = columnDefinition.getFieldName();
            String titleName = columnDefinition.getTitleName();
            String otherName = columnDefinition.getOtherName();
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

            if (columnDefinition.getColumnType().equals("用户")) {
                choorseNames.add(columnDefinition.getFieldName());
            }
            if (columnDefinition.getColumnType().equals("单位")) {
                unitNames.add(columnDefinition.getFieldName());
            }
            i++;
        }
        Long count_ = viewDataSourceMap.get(tableName).count(viewColumns, defaultCondition,
                new HashMap<String, Object>());
        page.setTotalCount(count_);
        data = viewDataSourceMap.get(tableName).query(viewColumns, defaultCondition, new HashMap<String, Object>(),
                orderBy == null ? "" : orderBy.toString(), page);
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
                                               Set<ColumnDefinition> columnDefinitions, PageDefinition pageDefinition, PagingInfo page, List keyWords) {
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
        if (pageDefinition.getIsPaging() == true) {
            firstResult = pageDefinition.getPageNum() * (page.getCurrentPage() - 1);
            maxResults = pageDefinition.getPageNum();
        }

        // 计数器
        int i = 0;
        for (ColumnDefinition columnDefinition : columnDefinitions) {
            String field = columnDefinition.getFieldName();
            fieldName.add(field + " as " + field);
            String defaultSort = columnDefinition.getDefaultSort();
            String columnDataType = columnDefinition.getColumnDataType();
            if (field != null && !"".equals(field) && columnDataType != "2") {
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
            if (columnDefinition.getColumnType().equals("用户")) {
                choorseNames.add(columnDefinition.getFieldName());
            }
            if (columnDefinition.getColumnType().equals("单位")) {
                unitNames.add(columnDefinition.getFieldName());
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
                                                Set<ColumnDefinition> columnDefinitions, PageDefinition pageDefinition, PagingInfo page, List keyWords,
                                                String beginTime, String endTime, String searchField) {

        List<QueryItem> data = new ArrayList<QueryItem>();
        Set<String> choorseNames = new HashSet<String>();
        Set<String> unitNames = new HashSet<String>();
        StringBuilder orderBy = new StringBuilder();
        // 计数器
        int i = 0;
        List<ViewColumn> viewColumns = new ArrayList<ViewColumn>();
        Map<String, ViewColumn> map = ConvertUtils.convertElementToMap(viewDataSourceMap.get(tableName)
                .getAllViewColumns(), "columnAlias");
        String str = "";
        String timeSql = "";
        Map paraMap = new HashMap();
        for (ColumnDefinition columnDefinition : columnDefinitions) {
            viewColumns.add(map.get(columnDefinition.getFieldName()));
            String field = columnDefinition.getFieldName();
            ViewColumn viewColumn = map.get(field);
            field = viewColumn.getAttributeName();
            String defaultSort = columnDefinition.getDefaultSort();
            String ColumnType = columnDefinition.getColumnType();
            String columnDataType = columnDefinition.getColumnDataType();
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
                            timeSql += field + ">:startDate";
                            paraMap.put("startDate", startDate);
                        } catch (ParseException e) {
                            logger.error(ExceptionUtils.getStackTrace(e));
                        }
                    }
                    if (!StringUtils.isBlank(endTime)) {
                        try {
                            Date endDate = format.parse(endTime);
                            if (!StringUtils.isBlank(timeSql)) {
                                timeSql += " and " + field + "<:endDate";
                            } else {
                                timeSql += field + "< :endDate";
                            }
                            paraMap.put("endDate", endDate);
                        } catch (ParseException e) {
                            logger.error(ExceptionUtils.getStackTrace(e));
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
            if (columnDefinition.getColumnType().equals("用户")) {
                choorseNames.add(columnDefinition.getFieldName());
            }
            if (columnDefinition.getColumnType().equals("单位")) {
                unitNames.add(columnDefinition.getFieldName());
            }
            i++;
        }
        if (str != null) {
            str = str.replaceFirst("or", "");
        }
        if (StringUtils.isBlank(str)) {
            str = "1=1";
        }
        if (defaultCondition != null && !defaultCondition.equals("") && columnDefinitions.size() > 0) {
            defaultCondition = "(" + defaultCondition + ")" + " and (" + str + ")";
            if (!StringUtils.isBlank(timeSql)) {
                defaultCondition = "(" + defaultCondition + ")" + " and " + timeSql;
            }
        } else if (defaultCondition == null && columnDefinitions.size() > 0) {
            defaultCondition = str;
            if (!StringUtils.isBlank(timeSql)) {
                defaultCondition = "(" + defaultCondition + ")" + " and " + timeSql;
            }
        }
        Long count_ = viewDataSourceMap.get(tableName).count(viewColumns, defaultCondition, paraMap);
        page.setTotalCount(count_);
        data = viewDataSourceMap.get(tableName).query(viewColumns, defaultCondition, paraMap,
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

        for (DyformFieldDefinition fieldDefinition : fieldDefinitions) {
            String field = fieldDefinition.getDisplayName();
            if (field != null && fieldName.equals(field)) {/*
             * fieldNames.add(
             * fieldDefinition
             * .getName() +
             * " as value");
             * selection.append(
             * fieldDefinition
             * .getName() +
             * " is not null");
             * //
             * System.out.println
             * (fieldDefinition.
             * getType()); if
             * (fieldDefinition
             * .getDbDataType
             * ().equals("20"))
             * {
             * choorseNames.add
             * ("value"); }
             */
            }
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
                                             Set<ColumnDefinition> columnDefinitions, PageDefinition pageDefinition, PagingInfo page, String condValue,
                                             String appointColumn) {
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
        if (pageDefinition.getIsPaging() == true) {
            firstResult = pageDefinition.getPageNum() * (page.getCurrentPage() - 1);
            maxResults = pageDefinition.getPageNum();
        }

        // 计数器
        int i = 0;
        for (ColumnDefinition columnDefinition : columnDefinitions) {

            String title = columnDefinition.getTitleName();
            String fieldValue = columnDefinition.getFieldName();
            fieldName.add(fieldValue + " as " + fieldValue);
            if (title.equals(appointColumn)) {
                String field = columnDefinition.getFieldName();

                String defaultSort = columnDefinition.getDefaultSort();
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
            if (columnDefinition.getColumnType().equals("用户")) {
                choorseNames.add(columnDefinition.getFieldName());
            }
            if (columnDefinition.getColumnType().equals("单位")) {
                unitNames.add(columnDefinition.getFieldName());
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
                                              Set<ColumnDefinition> columnDefinitions, PageDefinition pageDefinition, PagingInfo page, String condValue,
                                              String appointColumn) {
        List<QueryItem> data = new ArrayList<QueryItem>();
        Set<String> choorseNames = new HashSet<String>();
        StringBuilder orderBy = new StringBuilder();

        List<ViewColumn> viewColumns = new ArrayList<ViewColumn>();
        Map<String, ViewColumn> map = ConvertUtils.convertElementToMap(viewDataSourceMap.get(tableName)
                .getAllViewColumns(), "columnAlias");
        for (ColumnDefinition columnDefinition : columnDefinitions) {
            viewColumns.add(map.get(columnDefinition.getFieldName()));
            String field = columnDefinition.getFieldName();
            ViewColumn viewColumn = map.get(field);
            field = viewColumn.getAttributeName();
            String title = columnDefinition.getTitleName();
            if (title != null && !"".equals(title) && title.equals(appointColumn)) {
                if (defaultCondition.equals("")) {
                    defaultCondition += field + " like " + "'%" + condValue + "%'";
                } else {
                    defaultCondition += " or " + field + " like " + "'%" + condValue + "%'";
                }
            }
        }
        data = viewDataSourceMap.get(tableName).query(viewColumns, defaultCondition, new HashMap<String, Object>(),
                orderBy == null ? "" : orderBy.toString(), page);
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
                                             Set<ColumnDefinition> columnDefinitions, PageDefinition pageDefinition, PagingInfo page, String beginTime,
                                             String endTime, String appointColumn) {
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
        if (pageDefinition.getIsPaging() == true) {
            firstResult = pageDefinition.getPageNum() * (page.getCurrentPage() - 1);
            maxResults = pageDefinition.getPageNum();
        }

        // 计数器
        int i = 0;
        for (ColumnDefinition columnDefinition : columnDefinitions) {

            String title = columnDefinition.getTitleName();
            String fieldValue = columnDefinition.getFieldName();
            fieldName.add(fieldValue + " as " + fieldValue);
            if (title.equals(appointColumn)) {
                String field = columnDefinition.getFieldName();

                String defaultSort = columnDefinition.getDefaultSort();
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
            if (columnDefinition.getColumnType().equals("用户")) {
                choorseNames.add(columnDefinition.getFieldName());
            }
            if (columnDefinition.getColumnType().equals("单位")) {
                unitNames.add(columnDefinition.getFieldName());
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
                                              Set<ColumnDefinition> columnDefinitions, PageDefinition pageDefinition, PagingInfo page, String beginTime,
                                              String endTime, String appointColumn) {
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
        if (pageDefinition.getIsPaging() == true) {
            firstResult = pageDefinition.getPageNum() * (page.getCurrentPage() - 1);
            maxResults = pageDefinition.getPageNum();
        }

        // 计数器
        int i = 0;
        for (ColumnDefinition columnDefinition : columnDefinitions) {

            String title = columnDefinition.getTitleName();
            String fieldValue = columnDefinition.getFieldName();
            fieldName.add(fieldValue);
            if (title.equals(appointColumn)) {
                String field = columnDefinition.getFieldName();

                String defaultSort = columnDefinition.getDefaultSort();
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
            if (columnDefinition.getColumnType().equals("用户")) {
                choorseNames.add(columnDefinition.getFieldName());
            }
            if (columnDefinition.getColumnType().equals("单位")) {
                unitNames.add(columnDefinition.getFieldName());
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
                                          Set<ColumnDefinition> columnDefinitions, String rowIdKey, String roleType, String roleValue,
                                          PageDefinition pageDefinition, DyViewQueryInfo dyViewQueryInfo, String count) {
        // 前端所有的请求参数类
        CondSelectAskInfo askInfo = dyViewQueryInfo.getCondSelect();
        String orderTitle = "";
        String orderbyArr = "";
        List<Map<String, String>> keyWords = new ArrayList<Map<String, String>>();
        String appointColumn = "";
        String optionValue = "";
        String beginTime = "";
        String endTime = "";
        String searchField = "";
        if (askInfo != null) {
            // 点击排序
            if (StringUtils.isNotEmpty(askInfo.getOrderTitle())) {
                orderTitle = askInfo.getOrderTitle();
            }
            if (StringUtils.isNotEmpty(askInfo.getOrderbyArr())) {
                orderbyArr = askInfo.getOrderbyArr();
            }
            if (askInfo.getKeyWords() != null && askInfo.getKeyWords().size() != 0) {
                // 关键字
                keyWords = askInfo.getKeyWords();
            }
            if (StringUtils.isNotEmpty(askInfo.getAppointColumn())) {
                // 备选项对应的列名称
                appointColumn = askInfo.getAppointColumn();
            }
            if (StringUtils.isNotEmpty(askInfo.getOptionValue())) {
                // 备选项的真实值
                optionValue = askInfo.getOptionValue();
            }
            // 时间查询的请求参数
            if (StringUtils.isNotEmpty(askInfo.getBeginTime())) {
                beginTime = askInfo.getBeginTime();
            }
            if (StringUtils.isNotEmpty(askInfo.getEndTime())) {
                endTime = askInfo.getEndTime();
            }
            if (StringUtils.isNotEmpty(askInfo.getSearchField())) {
                searchField = askInfo.getSearchField();
            }
        }

        // 时间查询的sql
        String timeSql = "";

        List<QueryItem> queryItems = new ArrayList<QueryItem>();
        StringBuilder selection = new StringBuilder();
        Set<String> choorseNames = new HashSet<String>();
        Set<String> unitNames = new HashSet<String>();
        Map<String, String> aclChoorseNames = new HashMap<String, String>();
        com.wellsoft.pt.security.acl.support.QueryInfo aclQueryInfo = new com.wellsoft.pt.security.acl.support.QueryInfo();
        // if (defaultCondition != null && !defaultCondition.equals("")) {
        // selection.append(defaultCondition);
        // aclQueryInfo.setWhereHql(selection.toString());
        // }
        PagingInfo page = new PagingInfo();
        if (count == null) {
            // 分页请求参数
            if (dyViewQueryInfo.getPageInfo() != null) {
                page = dyViewQueryInfo.getPageInfo();
                aclQueryInfo.getPage().setPageNo(page.getCurrentPage());
                aclQueryInfo.getPage().setPageSize(page.getPageSize());
            } else {
                // 设置当前页
                aclQueryInfo.getPage().setPageNo(page.getCurrentPage());
                // 设置每页的页数
                if (pageDefinition.getIsPaging() == true) {
                    aclQueryInfo.getPage().setPageSize(pageDefinition.getPageNum());
                }
            }
        } else {
            aclQueryInfo.getPage().setPageNo(1);
            aclQueryInfo.getPage().setPageSize(Integer.valueOf(count));
        }

        String moudle = "";
        StringBuilder selectionHql = new StringBuilder();
        StringBuilder orderBy = new StringBuilder();
        Class<IdEntity> Entityname = null;
        List<SystemTableRelationship> relationShip = getViewDataService.getAttributesByrelationship(tableUuid);
        SystemTable st = basicDataApiFacade.getTable(tableUuid);
        String moduleId = st.getModuleName();// 模块id
        // 多表时
        if (relationShip.size() != 0) {
            for (int index = 0; index < relationShip.size(); index++) {
                String mainTableName = relationShip.get(index).getMainTableName();// 主表名
                try {
                    Entityname = (Class<IdEntity>) Class.forName(mainTableName);
                } catch (ClassNotFoundException e) {
                    logger.error(ExceptionUtils.getStackTrace(e));
                }
                String SecondaryTableName = relationShip.get(index).getSecondaryTableName();// 从表名
                String associateAttribute = relationShip.get(index).getAssociatedAttributes();// 关联属性
                String tableRelationShip = relationShip.get(index).getTableRelationship();

                String[] mainTNs = mainTableName.split("entity.");
                String[] secondaryTNs = SecondaryTableName.split("entity.");

                for (ColumnDefinition columnDefinition : columnDefinitions) {
                    String field = columnDefinition.getFieldName();
                    String entityName = columnDefinition.getEntityName();
                    String columnAlias = columnDefinition.getColumnAliase();
                    String titleName = columnDefinition.getTitleName();
                    /**************排序*************/
                    String defaultSort = columnDefinition.getDefaultSort();
                    if (defaultSort == null) {
                        defaultSort = "";
                    }

                    String mainTN = mainTNs[1].toUpperCase();
                    String entityN = entityName.toUpperCase();
                    String secondaryTN = secondaryTNs[1].toUpperCase();

                    if (mainTN.equals(entityN)) {
                        selectionHql.append(",o." + field + " as " + columnAlias);
                        // 默认排序
                        if (defaultSort.equals(DyviewConfig.DYVIEW_SORT_ASC)) {
                            if (orderBy.toString().equals("")) {
                                aclQueryInfo.addOrderby(field, "asc");
                            } else {
                                aclQueryInfo.addOrderby("," + field, "asc");
                            }
                        } else if (defaultSort.equals(DyviewConfig.DYVIEW_SORT_DESC)) {
                            if (orderBy.toString() != null && orderBy.toString().equals("")) {
                                aclQueryInfo.addOrderby(field, "desc");
                            } else {
                                aclQueryInfo.addOrderby("," + field, "desc");
                            }
                        }
                        if (orderTitle.equals(columnDefinition.getOtherName())) {
                            aclQueryInfo.addOrderby(field + " " + orderbyArr);
                        }
                        /*********************搜索条件*************************/
                        String keyWordType = "field";
                        String keyWordValue = "";
                        for (int j = 0; j < keyWords.size(); j++) {
                            Map<String, String> keyWord = keyWords.get(j);
                            for (String key : keyWord.keySet()) {
                                if (key.equals("all")) {
                                    keyWordType = "all";
                                    keyWordValue = keyWord.get("all").toString();
                                    break;
                                }
                            }

                        }
                        // for (int l = 0; l < keyWords.size(); l++) {

                        if (columnDefinition.getColumnDataType() != null
                                && columnDefinition.getColumnDataType().equals("STRING") && keyWords.size() != 0) {
                            if (keyWordType.equals("all")) {// 全部列模糊查询
                                String[] keyValues = keyWordValue.split(",");
                                for (int k = 0; k < keyValues.length; k++) {
                                    if ("用户".equals(columnDefinition.getColumnType())) {
                                        List<String> users = orgApiFacade.getOrgIdsLikeName(keyValues[k]);
                                        if (!users.isEmpty()) {
                                            selection.append(" or o." + field + " in " + "(:" + columnAlias + ")");
                                            StringBuilder sb = new StringBuilder();
                                            for (int m = 0; m < users.size(); m++) {
                                                sb.append("," + users.get(m));
                                            }
                                            aclQueryInfo.addQueryParams(columnAlias, sb.toString()
                                                    .replaceFirst(",", ""));
                                        }
                                    } else {
                                        selection.append(" or " + "lower(o." + field + ") like '%'||lower(:"
                                                + columnAlias + ")||'%'");
                                        aclQueryInfo.addQueryParams(columnAlias,
                                                columnDefinition.getColumnDataType(keyValues[k]));
                                    }
                                }
                            } else {// 精确列
                                for (int j = 0; j < keyWords.size(); j++) {
                                    Map<String, String> keyWord = keyWords.get(j);
                                    // 遍历关键字的map
                                    for (String key : keyWord.keySet()) {
                                        if (key.equals(field)) {
                                            if ("用户".equals(columnDefinition.getColumnType())) {
                                                List<String> users = orgApiFacade.getOrgIdsLikeName(keyWord.get(key));
                                                if (!users.isEmpty()) {
                                                    selection.append(" or o." + field + " in " + "(:" + columnAlias
                                                            + ")");
                                                    StringBuilder sb = new StringBuilder();
                                                    for (int m = 0; m < users.size(); m++) {
                                                        sb.append(",'" + users.get(m) + "'");
                                                    }
                                                    aclQueryInfo.addQueryParams(columnAlias, sb.toString()
                                                            .replaceFirst(",", ""));
                                                }
                                            } else {
                                                selection.append(" or " + "lower(o." + field + ") like '%'||lower(:"
                                                        + columnAlias + ")||'%'");
                                                aclQueryInfo.addQueryParams(columnAlias,
                                                        columnDefinition.getColumnDataType(keyWord.get(key)));
                                            }
                                        }
                                    }
                                }
                            }
                        } else if (columnDefinition.getColumnDataType() != null
                                && columnDefinition.getColumnDataType().equals("DATE") && keyWords.size() != 0
                                && !StringUtils.isBlank(searchField) && searchField.equals(field)) {
                            DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                            if (!StringUtils.isBlank(beginTime) && StringUtils.isBlank(endTime)) {
                                try {
                                    Date startDate = format.parse(beginTime);
                                    timeSql += " o." + field + " > :beginTime";
                                    aclQueryInfo.addQueryParams("beginTime", startDate);
                                } catch (ParseException e) {
                                    logger.error(ExceptionUtils.getStackTrace(e));
                                }
                            } else if (StringUtils.isBlank(beginTime) && !StringUtils.isBlank(endTime)) {
                                try {
                                    Date endDate = format.parse(endTime);
                                    timeSql += " o." + field + " < :endTime";
                                    aclQueryInfo.addQueryParams("endTime", endDate);
                                } catch (ParseException e) {
                                    logger.error(ExceptionUtils.getStackTrace(e));
                                }
                            } else if (!StringUtils.isBlank(beginTime) && !StringUtils.isBlank(endTime)) {
                                try {
                                    Date startDate = format.parse(beginTime);
                                    Date endDate = format.parse(endTime);
                                    timeSql += " o." + field + " > :beginTime";
                                    timeSql += " and o." + field + " < :endTime";
                                    aclQueryInfo.addQueryParams("beginTime", startDate);
                                    aclQueryInfo.addQueryParams("endTime", endDate);
                                } catch (ParseException e) {
                                    logger.error(ExceptionUtils.getStackTrace(e));
                                }
                            }
                        }
                        // }

                    } else if (secondaryTN.equals(entityN)) {
                        selectionHql.append(",o." + associateAttribute + "." + field + " as " + columnAlias);
                        // 默认排序
                        if (defaultSort != null) {
                            if (defaultSort.equals(DyviewConfig.DYVIEW_SORT_ASC)) {
                                if (orderBy.toString().equals("")) {
                                    aclQueryInfo.addOrderby(associateAttribute + "." + field, "asc");
                                } else {
                                    aclQueryInfo.addOrderby("," + associateAttribute + "." + field, "asc");
                                }
                            } else if (defaultSort.equals(DyviewConfig.DYVIEW_SORT_DESC)) {
                                if (orderBy.toString() != null && orderBy.toString().equals("")) {
                                    aclQueryInfo.addOrderby(associateAttribute + "." + field, "desc");
                                } else {
                                    aclQueryInfo.addOrderby("," + associateAttribute + "." + field, "desc");
                                }
                            }
                        }
                        if (orderTitle.equals(columnDefinition.getOtherName())) {
                            aclQueryInfo.addOrderby(associateAttribute + "." + field + " " + orderbyArr);
                        }

                        /*********************搜索条件*************************/
                        String keyWordType = "field";
                        String keyWordValue = "";
                        if (keyWords != null) {
                            for (int j = 0; j < keyWords.size(); j++) {
                                Map<String, String> keyWord = keyWords.get(j);
                                for (String key : keyWord.keySet()) {
                                    if (key.equals("all")) {
                                        keyWordType = "all";
                                        keyWordValue = keyWord.get("all").toString();
                                        break;
                                    }
                                }
                            }

                            // for (int l = 0; l < keyWords.size(); l++) {

                            if (columnDefinition.getColumnDataType() != null
                                    && columnDefinition.getColumnDataType().equals("STRING") && keyWords.size() != 0) {
                                if (keyWordType.equals("all")) {// 全部列模糊查询
                                    String[] keyValues = keyWordValue.split(",");
                                    for (int k = 0; k < keyValues.length; k++) {
                                        if ("用户".equals(columnDefinition.getColumnType())) {
                                            List<String> users = orgApiFacade.getOrgIdsLikeName(keyValues[k]);
                                            if (!users.isEmpty()) {
                                                selection.append(" or o." + associateAttribute + "." + field + " in "
                                                        + "(:" + columnAlias + ")");
                                                StringBuilder sb = new StringBuilder();
                                                for (int m = 0; m < users.size(); m++) {
                                                    sb.append("," + users.get(m));
                                                }
                                                aclQueryInfo.addQueryParams(columnAlias,
                                                        sb.toString().replaceFirst(",", ""));
                                            }
                                        } else {
                                            selection.append(" or " + "lower(o." + associateAttribute + "." + field
                                                    + ") like '%'||lower(:" + columnAlias + ")||'%'");
                                            aclQueryInfo.addQueryParams(columnAlias,
                                                    columnDefinition.getColumnDataType(keyValues[k]));
                                        }
                                    }
                                } else {// 精确列
                                    for (int j = 0; j < keyWords.size(); j++) {
                                        Map<String, String> keyWord = keyWords.get(j);
                                        // 遍历关键字的map
                                        for (String key : keyWord.keySet()) {
                                            if (key.equals(titleName)) {
                                                if ("用户".equals(columnDefinition.getColumnType())) {
                                                    List<String> users = orgApiFacade.getOrgIdsLikeName(keyWord
                                                            .get(key));
                                                    if (!users.isEmpty()) {
                                                        selection.append(" or o." + associateAttribute + "." + field
                                                                + " in " + "(:" + columnAlias + ")");
                                                        StringBuilder sb = new StringBuilder();
                                                        for (int m = 0; m < users.size(); m++) {
                                                            sb.append("," + users.get(m));
                                                        }
                                                        aclQueryInfo.addQueryParams(columnAlias, sb.toString()
                                                                .replaceFirst(",", ""));
                                                    }
                                                } else {
                                                    selection.append(" or " + "lower(o." + associateAttribute + "."
                                                            + field + ") like '%'||lower(:" + columnAlias + ")||'%'");
                                                    aclQueryInfo.addQueryParams(columnAlias,
                                                            columnDefinition.getColumnDataType(keyWord.get(key)));
                                                }
                                            }
                                        }
                                    }
                                }
                            } else if (columnDefinition.getColumnDataType() != null
                                    && columnDefinition.getColumnDataType().equals("DATE") && keyWords.size() != 0
                                    && !StringUtils.isBlank(searchField) && searchField.equals(field)) {
                                DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                                if (!StringUtils.isBlank(beginTime) && StringUtils.isBlank(endTime)) {
                                    try {
                                        Date startDate = format.parse(beginTime);
                                        timeSql += " o." + associateAttribute + "." + field + " > :beginTime";
                                        aclQueryInfo.addQueryParams("beginTime", startDate);
                                    } catch (ParseException e) {
                                        logger.error(ExceptionUtils.getStackTrace(e));
                                    }
                                } else if (StringUtils.isBlank(beginTime) && !StringUtils.isBlank(endTime)) {
                                    try {
                                        Date endDate = format.parse(endTime);
                                        timeSql += " o." + associateAttribute + "." + field + " < :endTime";
                                        aclQueryInfo.addQueryParams("endTime", endDate);
                                    } catch (ParseException e) {
                                        logger.error(ExceptionUtils.getStackTrace(e));
                                    }
                                } else if (!StringUtils.isBlank(beginTime) && !StringUtils.isBlank(endTime)) {
                                    try {
                                        Date startDate = format.parse(beginTime);
                                        Date endDate = format.parse(endTime);
                                        timeSql += " o." + associateAttribute + "." + field + " > :beginTime";
                                        timeSql += " and o." + associateAttribute + "." + field + " < :endTime";
                                        aclQueryInfo.addQueryParams("beginTime", startDate);
                                        aclQueryInfo.addQueryParams("endTime", endDate);
                                    } catch (ParseException e) {
                                        logger.error(ExceptionUtils.getStackTrace(e));
                                    }
                                }
                            }
                            // }
                        }

                    }
                    // 按条件排序的处理
                    if (titleName.equals(appointColumn) && StringUtils.isNotBlank(field)) {
                        if (columnDefinition.getColumnDataType().equals("STRING")) {
                            if (selection.length() > 0) {
                                selection.append(" and " + "lower(o." + field + ") like '%'||lower(:" + columnAlias
                                        + ")||'%'");
                            } else {
                                selection.append("lower(o." + field + ") like '%'||lower(:" + columnAlias + ")||'%'");
                            }
                            aclQueryInfo.addQueryParams(columnAlias, optionValue);
                        } else if (columnDefinition.getColumnDataType().equals("BOOLEAN")) {
                            if (selection.length() > 0) {
                                selection.append(" and o." + field + " = " + ":" + columnAlias);
                            } else {
                                selection.append("o." + field + " = " + ":" + columnAlias);
                            }
                            if (optionValue.equals("false")) {
                                aclQueryInfo.addQueryParams(columnAlias, false);
                            } else {
                                aclQueryInfo.addQueryParams(columnAlias, true);
                            }
                        }
                    }

                    if (columnDefinition.getColumnType().equals("用户")) {
                        choorseNames.add(columnDefinition.getColumnAliase());
                    }
                    if (columnDefinition.getColumnType().equals("单位")) {
                        unitNames.add(columnDefinition.getColumnAliase());
                    }
                    if (columnDefinition.getColumnType().equals("acl用户")) {
                        String value = columnDefinition.getValue();
                        if (StringUtils.isNotBlank(value) && value.indexOf("|") != -1) {
                            String[] values = StringUtils.split(value, "|");
                            if (values.length == 2 && StringUtils.isNotBlank(values[1])) {
                                aclChoorseNames.put(columnDefinition.getColumnAliase() + "entityClass", values[0]);
                                aclChoorseNames.put(columnDefinition.getColumnAliase(), values[1]);
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
                logger.error(ExceptionUtils.getStackTrace(e));
            }
            for (ColumnDefinition columnDefinition : columnDefinitions) {
                String field = columnDefinition.getFieldName();
                String columnAlias = columnDefinition.getColumnAliase();
                String defaultSort = columnDefinition.getDefaultSort();
                String titleName = columnDefinition.getTitleName();
                // if (!field.equals("uuid")) {
                selectionHql.append(",o." + field + " as " + columnAlias);

                if (orderTitle.equals(columnDefinition.getOtherName())) {
                    aclQueryInfo.addOrderby(field + " " + orderbyArr);
                }

                // 默认排序
                if (defaultSort != null) {
                    if (defaultSort.equals(DyviewConfig.DYVIEW_SORT_ASC)) {
                        if (orderBy.toString().equals("")) {
                            aclQueryInfo.addOrderby(field, "asc");
                        } else {
                            aclQueryInfo.addOrderby("," + field, "asc");
                        }
                    } else if (defaultSort.equals(DyviewConfig.DYVIEW_SORT_DESC)) {
                        if (orderBy.toString() != null && orderBy.toString().equals("")) {
                            aclQueryInfo.addOrderby(field, "desc");
                        } else {
                            aclQueryInfo.addOrderby("," + field, "desc");
                        }
                    }
                }

                /*********************搜索条件*************************/
                String keyWordType = "field";
                String keyWordValue = "";
                if (keyWords != null) {
                    for (int j = 0; j < keyWords.size(); j++) {
                        Map<String, String> keyWord = keyWords.get(j);
                        for (String key : keyWord.keySet()) {
                            if (key.equals("all")) {
                                keyWordType = "all";
                                keyWordValue = keyWord.get("all").toString();
                                break;
                            }
                        }
                    }

                    // for (int l = 0; l < keyWords.size(); l++) {
                    if (columnDefinition.getColumnDataType() != null
                            && (columnDefinition.getColumnDataType().equals("STRING") || columnDefinition
                            .getColumnDataType().equals("CLOB")) && keyWords.size() != 0) {
                        if (keyWordType.equals("all") && !field.equals("uuid")) {// 全部列模糊查询
                            String[] keyValues = keyWordValue.split(",");
                            for (int k = 0; k < keyValues.length; k++) {
                                if ("用户".equals(columnDefinition.getColumnType())) {
                                    List<String> users = orgApiFacade.getOrgIdsLikeName(keyValues[k]);
                                    if (!users.isEmpty()) {
                                        selection.append(" or o." + field + " in " + "(:" + columnAlias + ")");
                                        StringBuilder sb = new StringBuilder();
                                        for (int m = 0; m < users.size(); m++) {
                                            sb.append("," + users.get(m));
                                        }
                                        aclQueryInfo.addQueryParams(columnAlias, sb.toString().replaceFirst(",", ""));
                                    }
                                } else {
                                    selection.append(" or " + "lower(o." + field + ") like '%'||lower(:" + columnAlias
                                            + ")||'%'");
                                    aclQueryInfo.addQueryParams(columnAlias,
                                            columnDefinition.getColumnDataType(keyValues[k]));
                                }
                            }
                        } else {// 精确列
                            for (int j = 0; j < keyWords.size(); j++) {
                                Map<String, String> keyWord = keyWords.get(j);
                                // 遍历关键字的map
                                for (String key : keyWord.keySet()) {
                                    if (keyWord.get(key) != "" && keyWord.get(key) != null && key.equals(titleName)) {
                                        if ("用户".equals(columnDefinition.getColumnType())) {
                                            List<String> users = orgApiFacade.getOrgIdsLikeName(keyWord.get(key));
                                            if (!users.isEmpty()) {
                                                selection.append(" or o." + field + " in " + "(:" + columnAlias + ")");
                                                StringBuilder sb = new StringBuilder();
                                                for (int m = 0; m < users.size(); m++) {
                                                    sb.append(",'" + users.get(m) + "'");
                                                }
                                                aclQueryInfo.addQueryParams(columnAlias,
                                                        sb.toString().replaceFirst(",", ""));
                                            }
                                        } else {
                                            selection.append(" or " + "lower(o." + field + ") like '%'||lower(:"
                                                    + columnAlias + ")||'%'");
                                            aclQueryInfo.addQueryParams(columnAlias,
                                                    columnDefinition.getColumnDataType(keyWord.get(key)));
                                        }
                                    }
                                }
                            }
                        }
                    } else if (columnDefinition.getColumnDataType() != null
                            && columnDefinition.getColumnDataType().equals("DATE") && keyWords.size() != 0
                            && !StringUtils.isBlank(searchField) && searchField.equals(field)) {
                        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                        if (!StringUtils.isBlank(beginTime) && StringUtils.isBlank(endTime)) {
                            try {
                                Date startDate = format.parse(beginTime);
                                timeSql += " o." + field + " > :beginTime";
                                aclQueryInfo.addQueryParams("beginTime", startDate);
                            } catch (ParseException e) {
                                logger.error(ExceptionUtils.getStackTrace(e));
                            }
                        } else if (StringUtils.isBlank(beginTime) && !StringUtils.isBlank(endTime)) {
                            try {
                                Date endDate = format.parse(endTime);
                                timeSql += " o." + field + " < :endTime";
                                aclQueryInfo.addQueryParams("endTime", endDate);
                            } catch (ParseException e) {
                                logger.error(ExceptionUtils.getStackTrace(e));
                            }
                        } else if (!StringUtils.isBlank(beginTime) && !StringUtils.isBlank(endTime)) {
                            try {
                                Date startDate = format.parse(beginTime);
                                Date endDate = format.parse(endTime);
                                timeSql += " o." + field + " > :beginTime";
                                timeSql += " and o." + field + " < :endTime";
                                aclQueryInfo.addQueryParams("beginTime", startDate);
                                aclQueryInfo.addQueryParams("endTime", endDate);
                            } catch (ParseException e) {
                                logger.error(ExceptionUtils.getStackTrace(e));
                            }
                        }
                    }
                    // }
                }

                if (titleName.equals(appointColumn) && StringUtils.isNotBlank(field)) {
                    if (columnDefinition.getColumnDataType().equals("STRING")) {
                        if (selection.length() > 0) {
                            selection.append(" and " + "lower(o." + field + ") like '%'||lower(:" + columnAlias
                                    + ")||'%'");
                        } else {
                            selection.append("lower(o." + field + ") like '%'||lower(:" + columnAlias + ")||'%'");
                        }
                        aclQueryInfo.addQueryParams(columnAlias, optionValue);
                    } else if (columnDefinition.getColumnDataType().equals("BOOLEAN")) {
                        if (selection.length() > 0) {
                            selection.append(" and o." + field + " = " + ":" + columnAlias);
                        } else {
                            selection.append("o." + field + " = " + ":" + columnAlias);
                        }
                        if (optionValue.equals("false")) {
                            aclQueryInfo.addQueryParams(columnAlias, false);
                        } else {
                            aclQueryInfo.addQueryParams(columnAlias, true);
                        }
                    }
                }

                if (columnDefinition.getColumnType().equals("用户")) {
                    choorseNames.add(columnDefinition.getColumnAliase());
                }
                if (columnDefinition.getColumnType().equals("单位")) {
                    unitNames.add(columnDefinition.getColumnAliase());
                }
                if (columnDefinition.getColumnType().equals("acl用户")) {
                    String value = columnDefinition.getValue();
                    if (StringUtils.isNotBlank(value) && value.indexOf("|") != -1) {
                        String[] values = StringUtils.split(value, "|");
                        if (values.length == 2 && StringUtils.isNotBlank(values[1])) {
                            aclChoorseNames.put(columnDefinition.getColumnAliase() + "entityClass", values[0]);
                            aclChoorseNames.put(columnDefinition.getColumnAliase(), values[1]);
                        }
                    }
                }
                // }
            }
        }

        if (defaultCondition != null && !defaultCondition.equals("")) {
            if (!StringUtils.isBlank(selection.toString())) {
                aclQueryInfo.setWhereHql("(" + defaultCondition + ")" + " and ("
                        + selection.toString().replaceFirst("or", "") + ")");
                DateUtil du = new DateUtil();
                if (defaultCondition.indexOf("preDate") > -1) {
                    aclQueryInfo.addQueryParams("preDate", du.getDateByType("pre"));
                    aclQueryInfo.addQueryParams("nowDate", du.getDateByType("next"));
                }
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

        // aclQueryInfo.addOrderby(orderBy.toString().replaceFirst(",", " "));
        // aclQueryInfo.setWhereHql(selection.toString().replaceFirst("or",
        // " "));
        aclQueryInfo.setSelectionHql(selectionHql.toString().replaceFirst(",", ""));
        // 处理权限角色值
        List<Permission> roTypeList = new ArrayList<Permission>();
        if (StringUtils.isNotBlank(roleType)) {
            String[] roTypeArray = roleType.split(";");
            for (int i = 0; i < roTypeArray.length; i++) {
                roTypeList.add(new AclPermission(Integer.valueOf(roTypeArray[i])));
            }
        }

        List<String> sids = new ArrayList<String>();
        sids.add(SpringSecurityUtils.getCurrentUserId());
        // 个人权限
        if (roleValue.equals("person")) {
            queryItems = aclService.queryForItem(Entityname, aclQueryInfo, roTypeList, sids);
            dyViewQueryInfo.getPageInfo().setTotalCount(aclQueryInfo.getPage().getTotalCount());
            getDataByUser(queryItems, choorseNames);
            getDataByUnit(queryItems, unitNames);
            getDataByAclUser(rowIdKey, queryItems, aclChoorseNames);
        }
        // 群组权限
        else if (roleValue.equals("group")) {
            queryItems = aclService.queryForItem(Entityname, aclQueryInfo, roTypeList,
                    SpringSecurityUtils.getCurrentUserId(), moduleId);
            dyViewQueryInfo.getPageInfo().setTotalCount(aclQueryInfo.getPage().getTotalCount());
            getDataByUser(queryItems, choorseNames);
            getDataByUnit(queryItems, unitNames);
            getDataByAclUser(rowIdKey, queryItems, aclChoorseNames);
        } else if (roleValue.equals("all")) {
            queryItems = aclService.queryAllForItem(Entityname, aclQueryInfo, roTypeList,
                    SpringSecurityUtils.getCurrentUserId(), moduleId);
            dyViewQueryInfo.getPageInfo().setTotalCount(aclQueryInfo.getPage().getTotalCount());
            getDataByUser(queryItems, choorseNames);
            getDataByUnit(queryItems, unitNames);
            getDataByAclUser(rowIdKey, queryItems, aclChoorseNames);
        }
        return queryItems;
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
                                              Set<ColumnDefinition> columnDefinitions, String rowIdKey, String title, PageDefinition pageDefinition,
                                              PagingInfo page, String roleType, String roleValue, String orderbyArr) {
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
        if (pageDefinition.getIsPaging() == true) {
            // 设置当前页
            aclQueryInfo.getPage().setPageNo(page.getCurrentPage());
            // 设置每页的页数
            aclQueryInfo.getPage().setPageSize(pageDefinition.getPageNum());
        }
        StringBuilder selectionHql = new StringBuilder();
        StringBuilder orderBy = new StringBuilder();
        Class<IdEntity> Entityname = null;
        List<SystemTableRelationship> relationShip = getViewDataService.getAttributesByrelationship(tableUuid);

        SystemTable st = basicDataApiFacade.getTable(tableUuid);
        String moduleId = st.getModuleName();// 模块id
        // 多表时
        if (relationShip.size() != 0) {
            for (int index = 0; index < relationShip.size(); index++) {
                String mainTableName = relationShip.get(index).getMainTableName();// 主表名
                try {
                    Entityname = (Class<IdEntity>) Class.forName(mainTableName);
                } catch (ClassNotFoundException e) {
                    logger.error(ExceptionUtils.getStackTrace(e));
                }
                String SecondaryTableName = relationShip.get(index).getSecondaryTableName();// 从表名
                String associateAttribute = relationShip.get(index).getAssociatedAttributes();// 关联属性
                String tableRelationShip = relationShip.get(index).getTableRelationship();

                String[] mainTNs = mainTableName.split("entity.");
                String[] secondaryTNs = SecondaryTableName.split("entity.");
                // 计数器
                int i = 0;
                for (ColumnDefinition columnDefinition : columnDefinitions) {
                    String field = columnDefinition.getFieldName();
                    String entityName = columnDefinition.getEntityName();
                    String columnAlias = columnDefinition.getColumnAliase();
                    String defaultSort = columnDefinition.getDefaultSort();

                    // if (tableRelationShip.equals("一对一")) {
                    if (i < 1) {
                        for (int j = 1; j < mainTNs.length; j++) {
                            String mainTN = mainTNs[1].toUpperCase();
                            String entityN = entityName.toUpperCase();
                            String secondaryTN = secondaryTNs[1];
                            if (mainTN.equals(entityN)) {
                                selectionHql.append("o." + field + " as " + columnAlias);
                                if (title.equals(columnDefinition.getOtherName())) {
                                    aclQueryInfo.addOrderby(field + " " + orderbyArr);
                                }
                            } else {
                                selectionHql.append("o." + associateAttribute + "." + field + " as " + columnAlias);
                                if (title.equals(columnDefinition.getOtherName())) {
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
                                if (title.equals(columnDefinition.getOtherName())) {
                                    aclQueryInfo.addOrderby(field + " " + orderbyArr);
                                }

                            } else {
                                selectionHql.append(",o." + associateAttribute + "." + field + " as " + columnAlias);
                                if (title.equals(columnDefinition.getOtherName())) {
                                    aclQueryInfo.addOrderby(associateAttribute + "." + field + " " + orderbyArr);
                                }
                            }
                        }
                    }
                    // }

                    if (columnDefinition.getColumnType().equals("用户")) {
                        choorseNames.add(columnDefinition.getColumnAliase());
                    }
                    if (columnDefinition.getColumnType().equals("单位")) {
                        unitNames.add(columnDefinition.getColumnAliase());
                    }
                    if (columnDefinition.getColumnType().equals("acl用户")) {
                        String value = columnDefinition.getValue();
                        if (StringUtils.isNotBlank(value) && value.indexOf("|") != -1) {
                            String[] values = StringUtils.split(value, "|");
                            if (values.length == 2 && StringUtils.isNotBlank(values[1])) {
                                aclChoorseNames.put(columnDefinition.getColumnAliase() + "entityClass", values[0]);
                                aclChoorseNames.put(columnDefinition.getColumnAliase(), values[1]);
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
                logger.error(ExceptionUtils.getStackTrace(e));
            }

            // 计数器
            int i = 0;
            for (ColumnDefinition columnDefinition : columnDefinitions) {
                String field = columnDefinition.getFieldName();
                String columnAlias = columnDefinition.getColumnAliase();

                // if (tableRelationShip.equals("一对一")) {
                if (i < 1) {
                    selectionHql.append("o." + field + " as " + columnAlias);
                    if (title.equals(columnDefinition.getOtherName())) {
                        aclQueryInfo.addOrderby(field + " " + orderbyArr);
                    }
                } else {
                    selectionHql.append(",o." + field + " as " + columnAlias);
                    if (title.equals(columnDefinition.getOtherName())) {
                        aclQueryInfo.addOrderby(field + " " + orderbyArr);
                    }

                }
                // }

                if (columnDefinition.getColumnType().equals("用户")) {
                    choorseNames.add(columnDefinition.getColumnAliase());
                }
                if (columnDefinition.getColumnType().equals("单位")) {
                    unitNames.add(columnDefinition.getColumnAliase());
                }
                if (columnDefinition.getColumnType().equals("acl用户")) {
                    String value = columnDefinition.getValue();
                    if (StringUtils.isNotBlank(value) && value.indexOf("|") != -1) {
                        String[] values = StringUtils.split(value, "|");
                        if (values.length == 2 && StringUtils.isNotBlank(values[1])) {
                            aclChoorseNames.put(columnDefinition.getColumnAliase() + "entityClass", values[0]);
                            aclChoorseNames.put(columnDefinition.getColumnAliase(), values[1]);
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
                                                Set<ColumnDefinition> columnDefinitions, String rowIdKey, PageDefinition pageDefinition, PagingInfo page,
                                                List keyWords, String roleType, String roleValue, String beginTime, String endTime, String searchField) {
        List<QueryItem> queryItems = new ArrayList<QueryItem>();

        Set<String> choorseNames = new HashSet<String>();
        Set<String> unitNames = new HashSet<String>();
        Map<String, String> aclChoorseNames = new HashMap<String, String>();
        com.wellsoft.pt.security.acl.support.QueryInfo aclQueryInfo = new com.wellsoft.pt.security.acl.support.QueryInfo();
        // 设置当前页
        aclQueryInfo.getPage().setPageNo(page.getCurrentPage());
        // 设置每页的页数
        aclQueryInfo.getPage().setPageSize(pageDefinition.getPageNum());
        StringBuilder selection = new StringBuilder();
        StringBuilder selectionHql = new StringBuilder();
        String timeSql = "";
        StringBuilder orderBy = new StringBuilder();
        Class<IdEntity> Entityname = null;
        List<SystemTableRelationship> relationShip = getViewDataService.getAttributesByrelationship(tableUuid);

        SystemTable st = basicDataApiFacade.getTable(tableUuid);
        String moduleId = st.getModuleName();// 模块id
        if (relationShip.size() != 0) {
            for (int index = 0; index < relationShip.size(); index++) {
                String mainTableName = relationShip.get(index).getMainTableName();// 主表名
                try {
                    Entityname = (Class<IdEntity>) Class.forName(mainTableName);
                } catch (ClassNotFoundException e) {
                    logger.error(ExceptionUtils.getStackTrace(e));
                }
                String SecondaryTableName = relationShip.get(index).getSecondaryTableName();// 从表名
                String associateAttribute = relationShip.get(index).getAssociatedAttributes();// 关联属性
                String tableRelationShip = relationShip.get(index).getTableRelationship();// 表关系

                String[] mainTNs = mainTableName.split("entity.");
                String[] secondaryTNs = SecondaryTableName.split("entity.");
                for (ColumnDefinition columnDefinition : columnDefinitions) {
                    String field = columnDefinition.getFieldName();
                    String entityName = columnDefinition.getEntityName();
                    String columnAlias = columnDefinition.getColumnAliase();
                    String defaultSort = columnDefinition.getDefaultSort();
                    String columnName = columnDefinition.getTitleName();
                    // for (int j = 1; j < mainTNs.length; j++) {
                    String mainTN = mainTNs[1].toUpperCase();
                    String entityN = entityName.toUpperCase();
                    String secondaryTN = secondaryTNs[1].toUpperCase();
                    if (mainTN.equals(entityN)) {
                        selectionHql.append(",o." + field + " as " + columnAlias);
                        // 默认排序
                        if (defaultSort != null) {
                            if (defaultSort.equals(DyviewConfig.DYVIEW_SORT_ASC)) {
                                orderBy.append("," + field).append(" asc");
                            } else if (defaultSort.equals(DyviewConfig.DYVIEW_SORT_DESC)) {
                                orderBy.append("," + field).append(" desc");
                            }
                        }

                        for (int l = 0; l < keyWords.size(); l++) {
                            if (columnDefinition.getColumnDataType().equals("STRING")
                                    && !StringUtils.isBlank(keyWords.get(l).toString())) {
                                if ("用户".equals(columnDefinition.getColumnType())) {
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
                                            "%" + columnDefinition.getColumnDataType((String) keyWords.get(l)) + "%");
                                }
                            } else if (columnDefinition.getColumnDataType().equals("DATE")
                                    && !StringUtils.isBlank(searchField) && searchField.equals(field)) {
                                DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                                if (!StringUtils.isBlank(beginTime) && StringUtils.isBlank(endTime)) {
                                    try {
                                        Date startDate = format.parse(beginTime);
                                        timeSql += " o." + field + " > :beginTime";
                                        aclQueryInfo.addQueryParams("beginTime", startDate);
                                    } catch (ParseException e) {
                                        logger.error(ExceptionUtils.getStackTrace(e));
                                    }
                                } else if (StringUtils.isBlank(beginTime) && !StringUtils.isBlank(endTime)) {
                                    try {
                                        Date endDate = format.parse(endTime);
                                        timeSql += " o." + field + " < :endTime";
                                        aclQueryInfo.addQueryParams("endTime", endDate);
                                    } catch (ParseException e) {
                                        logger.error(ExceptionUtils.getStackTrace(e));
                                    }
                                } else if (!StringUtils.isBlank(beginTime) && !StringUtils.isBlank(endTime)) {
                                    try {
                                        Date startDate = format.parse(beginTime);
                                        Date endDate = format.parse(endTime);
                                        timeSql += " o." + field + " > :beginTime";
                                        timeSql += " and o." + field + " < :endTime";
                                        aclQueryInfo.addQueryParams("beginTime", startDate);
                                        aclQueryInfo.addQueryParams("endTime", endDate);
                                    } catch (ParseException e) {
                                        logger.error(ExceptionUtils.getStackTrace(e));
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
                            if (columnDefinition.getColumnDataType().equals("STRING")
                                    && !StringUtils.isBlank(keyWords.get(l).toString())) {
                                if ("用户".equals(columnDefinition.getColumnType())) {
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
                                    aclQueryInfo.addQueryParams(columnAlias,
                                            "%" + columnDefinition.getColumnDataType((String) keyWords.get(l)) + "%");
                                }
                            } else if (columnDefinition.getColumnDataType().equals("DATE")
                                    && !StringUtils.isBlank(searchField) && searchField.equals(field)) {
                                DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                                if (!StringUtils.isBlank(beginTime) && StringUtils.isBlank(endTime)) {
                                    try {
                                        Date startDate = format.parse(beginTime);
                                        timeSql += " o." + associateAttribute + "." + field + " > :beginTime";
                                        aclQueryInfo.addQueryParams("beginTime", startDate);
                                    } catch (ParseException e) {
                                        logger.error(ExceptionUtils.getStackTrace(e));
                                    }
                                } else if (StringUtils.isBlank(beginTime) && !StringUtils.isBlank(endTime)) {
                                    try {
                                        Date endDate = format.parse(endTime);
                                        timeSql += " o." + associateAttribute + "." + field + " < :endTime";
                                        aclQueryInfo.addQueryParams("endTime", endDate);
                                    } catch (ParseException e) {
                                        logger.error(ExceptionUtils.getStackTrace(e));
                                    }
                                } else if (!StringUtils.isBlank(beginTime) && !StringUtils.isBlank(endTime)) {
                                    try {
                                        Date startDate = format.parse(beginTime);
                                        Date endDate = format.parse(endTime);
                                        timeSql += " o." + associateAttribute + "." + field + " > :beginTime";
                                        timeSql += " and o." + associateAttribute + "." + field + " < :endTime";
                                        aclQueryInfo.addQueryParams("beginTime", startDate);
                                        aclQueryInfo.addQueryParams("endTime", endDate);
                                    } catch (ParseException e) {
                                        logger.error(ExceptionUtils.getStackTrace(e));
                                    }
                                }
                            }
                        }
                    }
                    // }
                    if (columnDefinition.getColumnType().equals("用户")) {
                        choorseNames.add(columnDefinition.getColumnAliase());
                    }
                    if (columnDefinition.getColumnType().equals("单位")) {
                        unitNames.add(columnDefinition.getColumnAliase());
                    }
                    if (columnDefinition.getColumnType().equals("acl用户")) {
                        String value = columnDefinition.getValue();
                        if (StringUtils.isNotBlank(value) && value.indexOf("|") != -1) {
                            String[] values = StringUtils.split(value, "|");
                            if (values.length == 2 && StringUtils.isNotBlank(values[1])) {
                                aclChoorseNames.put(columnDefinition.getColumnAliase() + "entityClass", values[0]);
                                aclChoorseNames.put(columnDefinition.getColumnAliase(), values[1]);
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
                logger.error(ExceptionUtils.getStackTrace(e));
            }
            for (ColumnDefinition columnDefinition : columnDefinitions) {
                String field = columnDefinition.getFieldName();
                String columnAlias = columnDefinition.getColumnAliase();
                String defaultSort = columnDefinition.getDefaultSort();
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
                    if (columnDefinition.getColumnDataType().equals("STRING")
                            && !StringUtils.isBlank(keyWords.get(l).toString())) {
                        if ("用户".equals(columnDefinition.getColumnType())) {
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
                                    "%" + columnDefinition.getColumnDataType((String) keyWords.get(l)) + "%");
                        }
                    } else if (columnDefinition.getColumnDataType().equals("DATE") && !StringUtils.isBlank(searchField)
                            && searchField.equals(field)) {
                        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                        if (!StringUtils.isBlank(beginTime) && StringUtils.isBlank(endTime)) {
                            try {
                                Date startDate = format.parse(beginTime);
                                timeSql += " o." + field + " > :beginTime";
                                aclQueryInfo.addQueryParams("beginTime", startDate);
                            } catch (ParseException e) {
                                logger.error(ExceptionUtils.getStackTrace(e));
                            }
                        } else if (StringUtils.isBlank(beginTime) && !StringUtils.isBlank(endTime)) {
                            try {
                                Date endDate = format.parse(endTime);
                                timeSql += " o." + field + " < :endTime";
                                aclQueryInfo.addQueryParams("endTime", endDate);
                            } catch (ParseException e) {
                                logger.error(ExceptionUtils.getStackTrace(e));
                            }
                        } else if (!StringUtils.isBlank(beginTime) && !StringUtils.isBlank(endTime)) {
                            try {
                                Date startDate = format.parse(beginTime);
                                Date endDate = format.parse(endTime);
                                timeSql += " o." + field + " > :beginTime";
                                timeSql += " and o." + field + " < :endTime";
                                aclQueryInfo.addQueryParams("beginTime", startDate);
                                aclQueryInfo.addQueryParams("endTime", endDate);
                            } catch (ParseException e) {
                                logger.error(ExceptionUtils.getStackTrace(e));
                            }
                        }
                    }

                }
                if (columnDefinition.getColumnType().equals("用户")) {
                    choorseNames.add(columnDefinition.getColumnAliase());
                }
                if (columnDefinition.getColumnType().equals("单位")) {
                    unitNames.add(columnDefinition.getColumnAliase());
                }
                if (columnDefinition.getColumnType().equals("acl用户")) {
                    String value = columnDefinition.getValue();
                    if (StringUtils.isNotBlank(value) && value.indexOf("|") != -1) {
                        String[] values = StringUtils.split(value, "|");
                        if (values.length == 2 && StringUtils.isNotBlank(values[1])) {
                            aclChoorseNames.put(columnDefinition.getColumnAliase() + "entityClass", values[0]);
                            aclChoorseNames.put(columnDefinition.getColumnAliase(), values[1]);
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
                                              Set<ColumnDefinition> columnDefinitions, String rowIdKey, PageDefinition pageDefinition, PagingInfo page,
                                              String condValue, String appointColumn, String roleType, String roleValue) {
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
        List<SystemTableRelationship> relationShip = getViewDataService.getAttributesByrelationship(tableUuid);
        SystemTable table = getViewDataService.getSystemTable(tableUuid);
        SystemTable st = basicDataApiFacade.getTable(tableUuid);
        String moduleId = st.getModuleName();// 模块id

        if (pageDefinition.getIsPaging() == true) {
        }
        if (relationShip.size() > 0) {
            for (int index = 0; index < relationShip.size(); index++) {
                String mainTableName = relationShip.get(index).getMainTableName();// 主表名
                try {
                    Entityname = (Class<IdEntity>) Class.forName(mainTableName);
                } catch (ClassNotFoundException e) {
                    logger.error(ExceptionUtils.getStackTrace(e));
                }
                String SecondaryTableName = relationShip.get(index).getSecondaryTableName();// 从表名
                String associateAttribute = relationShip.get(index).getAssociatedAttributes();// 关联属性
                String tableRelationShip = relationShip.get(index).getTableRelationship();

                String[] mainTNs = mainTableName.split("entity.");
                String[] secondaryTNs = SecondaryTableName.split("entity.");
                // 计数器
                int i = 0;
                for (ColumnDefinition columnDefinition : columnDefinitions) {
                    String field = columnDefinition.getFieldName();
                    String entityName = columnDefinition.getEntityName();
                    String columnAlias = columnDefinition.getColumnAliase();
                    String defaultSort = columnDefinition.getDefaultSort();
                    String columnName = columnDefinition.getTitleName();

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

                    if (columnName.equals(appointColumn)) {
                        if (field != null && !"".equals(field)) {
                            if (columnDefinition.getColumnDataType().equals("STRING")) {
                                if (selection.length() > 0) {
                                    selection.append(" and o." + field + " like " + ":" + columnAlias);
                                } else {
                                    selection.append("o." + field + " like " + ":" + columnAlias);
                                }
                                aclQueryInfo.addQueryParams(columnAlias, "%" + condValue + "%");
                            } else if (columnDefinition.getColumnDataType().equals("BOOLEAN")) {
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
                    }

                    if (columnDefinition.getColumnType().equals("用户")) {
                        choorseNames.add(columnDefinition.getFieldName());
                    }
                    if (columnDefinition.getColumnType().equals("单位")) {
                        unitNames.add(columnDefinition.getColumnAliase());
                    }
                    if (columnDefinition.getColumnType().equals("acl用户")) {
                        String value = columnDefinition.getValue();
                        if (StringUtils.isNotBlank(value) && value.indexOf("|") != -1) {
                            String[] values = StringUtils.split(value, "|");
                            if (values.length == 2 && StringUtils.isNotBlank(values[1])) {
                                aclChoorseNames.put(columnDefinition.getColumnAliase() + "entityClass", values[0]);
                                aclChoorseNames.put(columnDefinition.getColumnAliase(), values[1]);
                            }
                        }
                    }
                    i++;
                }
            }
        } else {
            int i = 0;
            for (ColumnDefinition columnDefinition : columnDefinitions) {
                String tableName = table.getFullEntityName();
                try {
                    Entityname = (Class<IdEntity>) Class.forName(tableName);
                } catch (ClassNotFoundException e) {
                    logger.error(ExceptionUtils.getStackTrace(e));
                }
                String field = columnDefinition.getFieldName();
                String columnAlias = columnDefinition.getColumnAliase();
                String defaultSort = columnDefinition.getDefaultSort();
                String columnName = columnDefinition.getTitleName();
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
                    if (columnDefinition.getColumnDataType().equals("STRING")) {
                        if (selection.length() > 0) {
                            selection.append(" and o." + field + " like " + ":" + columnAlias);
                        } else {
                            selection.append("o." + field + " like " + ":" + columnAlias);
                        }
                        aclQueryInfo.addQueryParams(columnAlias, "%" + condValue + "%");
                    } else if (columnDefinition.getColumnDataType().equals("BOOLEAN")) {
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

                if (columnDefinition.getColumnType().equals("用户")) {
                    choorseNames.add(columnDefinition.getFieldName());
                }
                if (columnDefinition.getColumnType().equals("单位")) {
                    unitNames.add(columnDefinition.getColumnAliase());
                }
                if (columnDefinition.getColumnType().equals("acl用户")) {
                    String value = columnDefinition.getValue();
                    if (StringUtils.isNotBlank(value) && value.indexOf("|") != -1) {
                        String[] values = StringUtils.split(value, "|");
                        if (values.length == 2 && StringUtils.isNotBlank(values[1])) {
                            aclChoorseNames.put(columnDefinition.getColumnAliase() + "entityClass", values[0]);
                            aclChoorseNames.put(columnDefinition.getColumnAliase(), values[1]);
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

        List<SystemTableAttribute> systemTableAttributes = getViewDataService.getSystemTableColumns(systemTableUuid);

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
                    logger.error(ExceptionUtils.getStackTrace(e));
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
            logger.error(ExceptionUtils.getStackTrace(e));
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
        DyViewQueryInfo dyViewQueryInfo = new DyViewQueryInfo();
        ViewDefinitionBean viewDefinitionBean = new ViewDefinitionBean();
        if (key != null) {
            ViewDefinition viewDefinition = viewAttributeDao.findUniqueBy(key, val);
            viewDefinitionBean = this.getBeanByUuid(viewDefinition.getUuid());
        } else {
            viewDefinitionBean = this.getBeanByUuid(val);
        }
        String readKey = viewDefinitionBean.getReadKey();
        if (readKey == null || (readKey != null && readKey.equals(""))) {
            readKey = "uuid";
        }
        // 获取数据源范围下的表名
        String tableName = viewDefinitionBean.getTableDefinitionName();
        int datascope = viewDefinitionBean.getDataScope();
        // 获取视图对应的表uuid
        String tableUuid = viewDefinitionBean.getFormuuid();
        String rowIdKey = StringUtils.isBlank(viewDefinitionBean.getCheckKey()) ? "uuid" : viewDefinitionBean
                .getCheckKey();
        // 获取视图的默认搜索条件
        String defaultCondition = viewDefinitionBean.getDefaultCondition();
        defaultCondition = StringUtils.replace(defaultCondition, "{当前登录人}", SpringSecurityUtils.getCurrentUserName());
        // 获取视图的分页信息
        PagingInfo page = new PagingInfo();
        PageDefinition pageDefinition = new PageDefinition();
        if (0 == (page.getCurrentPage())) {
            page.setCurrentPage(1);
        }
        pageDefinition = viewDefinitionBean.getPageDefinitions();
        viewDefinitionBean.setPageAble(viewDefinitionBean.getPageDefinitions().getIsPaging());
        // 获取视图的角色权限信息
        String roleType = viewDefinitionBean.getRoleType();
        // 获取视图的角色类型
        String roleValue = viewDefinitionBean.getRoleValue();

        // 获取视图下所有的列字段的数据
        Set<ColumnDefinition> columnDefinitions = viewDefinitionBean.getColumnDefinitions();

        // 获取表的总记录数
        Long totalCount = null;
        if (datascope == 1) {
            totalCount = dyFormApiFacade.queryTotalCountOfFormDataOfMainform(tableName, "");
            // 如果分页存在,设置分页信息
            if (pageDefinition.getIsPaging() == true) {
                page.setTotalCount(totalCount);
                page.setPageSize(pageDefinition.getPageNum());
            }
        }
        if ((datascope == 2 || datascope == 3) && pageDefinition.getIsPaging() == true) {
            // 如果分页存在,设置分页信息
            page.setPageSize(pageDefinition.getPageNum());
        }
        List<QueryItem> queryItems = new ArrayList<QueryItem>();
        if (datascope == 1) {
            queryItems = this.getColumnData(defaultCondition, tableName, columnDefinitions, pageDefinition,
                    dyViewQueryInfo);
            if (viewDefinitionBean.getIsRead() != null && viewDefinitionBean.getIsRead()) {
                readMarkerService.markList(queryItems, SpringSecurityUtils.getCurrentUserId(), readKey, "readFlag");
            }
        } else if (datascope == 2) {
            queryItems = this.getColumnData2(tableUuid, defaultCondition, columnDefinitions, rowIdKey, roleType,
                    roleValue, pageDefinition, dyViewQueryInfo, null);
            if (viewDefinitionBean.getIsRead() != null && viewDefinitionBean.getIsRead()) {
                readMarkerService.markList(queryItems, SpringSecurityUtils.getCurrentUserId(), readKey, "readFlag");
            }
        } else if (datascope == 3) {
            queryItems = this.getColumnData3(defaultCondition, tableName, columnDefinitions, pageDefinition,
                    dyViewQueryInfo, null);
            if (viewDefinitionBean.getIsRead() != null && viewDefinitionBean.getIsRead()) {
                readMarkerService.markList(queryItems, SpringSecurityUtils.getCurrentUserId(), readKey, "readFlag");
            }
        }
        return queryItems;
    }

    /**
     * 根据视图uuid获得所有的列定义
     *
     * @param viewUuid
     * @return
     */
    @Override
    public Set<ColumnDefinition> getColumnDefinitions(String viewUuid) {
        ViewDefinitionBean viewDefinitionBean = new ViewDefinitionBean();
        viewDefinitionBean = this.getBeanByUuid(viewUuid);
        Set<ColumnDefinition> columnDefinitions = viewDefinitionBean.getColumnDefinitions();
        return columnDefinitions;
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
        DyViewQueryInfo dyViewQueryInfo = new DyViewQueryInfo();
        ViewDefinitionBean viewDefinitionBean = new ViewDefinitionBean();
        viewDefinitionBean = getBeanByUuid(viewUuid);
        // 获取数据源范围下的表名
        String tableName = viewDefinitionBean.getTableDefinitionName();
        int datascope = viewDefinitionBean.getDataScope();
        // 获取视图的默认搜索条件
        String defaultCondition = "o.uuid = '" + TempId + "' and o.status != '0'";
        // 获取视图的分页信息
        PageDefinition pageDefinition = viewDefinitionBean.getPageDefinitions();
        // 获取视图下所有的列字段的数据
        Set<ColumnDefinition> columnDefinitionsTemp = viewDefinitionBean.getColumnDefinitions();
        Set<ColumnDefinition> columnDefinitions = new HashSet<ColumnDefinition>();
        for (ColumnDefinition columnDefinition : columnDefinitionsTemp) {
            if (relationDataDefiantion != null && !relationDataDefiantion.equals("")) {
                if (columnDefinition.getColumnAliase() != null
                        && relationDataDefiantion.indexOf(columnDefinition.getColumnAliase()) > -1) {
                    columnDefinitions.add(columnDefinition);
                } else if (columnDefinition.getFieldName() != null
                        && relationDataDefiantion.indexOf(columnDefinition.getFieldName()) > -1) {
                    columnDefinitions.add(columnDefinition);
                }

            }
        }

        PagingInfo page = new PagingInfo();
        List<QueryItem> queryItems = getColumnData3(defaultCondition, tableName, columnDefinitions, pageDefinition,
                dyViewQueryInfo, null);
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
    public List<ColumnDefinition> getViewColumnByPermission() {
        List<ColumnDefinition> columnDefinitionNew = new ArrayList<ColumnDefinition>();
        List<ViewDefinition> viewDefinitions = this.searchViewDefinition();
        for (int i = 0; i < viewDefinitions.size(); i++) {
            Set<ColumnDefinition> columnDefinitions = viewDefinitions.get(i).getColumnDefinitions();
            for (ColumnDefinition columnDefinition : columnDefinitions) {
                if (columnDefinition.getFieldPermission() != null && columnDefinition.getFieldPermission() == true) {
                    columnDefinitionNew.add(columnDefinition);
                }
            }
        }
        return columnDefinitionNew;
    }

}
