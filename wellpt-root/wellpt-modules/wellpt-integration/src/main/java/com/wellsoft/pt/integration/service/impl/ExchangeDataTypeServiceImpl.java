package com.wellsoft.pt.integration.service.impl;

import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.pt.dyform.facade.dto.DyFormFormDefinition;
import com.wellsoft.pt.dyform.facade.dto.DyformFieldDefinition;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import com.wellsoft.pt.integration.dao.ExchangeDataTypeDao;
import com.wellsoft.pt.integration.entity.ExchangeData;
import com.wellsoft.pt.integration.entity.ExchangeDataTransform;
import com.wellsoft.pt.integration.entity.ExchangeDataType;
import com.wellsoft.pt.integration.provider.BusinessHandleSource;
import com.wellsoft.pt.integration.provider.UnitSystemSource;
import com.wellsoft.pt.integration.security.ExchangeConfig;
import com.wellsoft.pt.integration.service.ExchangeDataService;
import com.wellsoft.pt.integration.service.ExchangeDataTransformService;
import com.wellsoft.pt.integration.service.ExchangeDataTypeService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import com.wellsoft.pt.unit.entity.BusinessType;
import com.wellsoft.pt.unit.entity.CommonUnit;
import com.wellsoft.pt.unit.facade.service.UnitApiFacade;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
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
 * 2013-11-18.1	wbx		2013-11-18		Create
 * </pre>
 * @date 2013-11-18
 */
@Service
public class ExchangeDataTypeServiceImpl extends AbstractJpaServiceImpl<ExchangeDataType, ExchangeDataTypeDao, String>
        implements ExchangeDataTypeService {
    @Autowired
    private ExchangeDataService exchangeDataService;
    @Autowired
    private DyFormFacade dyFormApiFacade;
    @Autowired
    private Map<String, BusinessHandleSource> BusinessHandleSourceMap;
    @Autowired
    private Map<String, UnitSystemSource> unitSystemSourceMap;
    @Autowired
    private ExchangeDataTransformService exchangeDataTransformService;
    @Autowired
    private UnitApiFacade unitApiFacade;

    @Override
    public ExchangeDataType getBeanByUuid(String uuid) {

        ExchangeDataType e = new ExchangeDataType();
        ExchangeDataType exchangeDataType = this.dao.getOne(uuid);
        if (exchangeDataType != null) {
            BeanUtils.copyProperties(exchangeDataType, e);

            if (exchangeDataType.getFormId() != null && !exchangeDataType.getFormId().equals("")) {
            } else {
                e.setFormId("");
            }

            if (exchangeDataType.getUnitId() != null && !exchangeDataType.getUnitId().equals("")) {
                List<CommonUnit> cus = unitApiFacade.getCommonUnitListByIds(exchangeDataType.getUnitId());
                if (cus != null && cus.size() > 0) {
                    String unitIds = "";
                    for (CommonUnit cu : cus) {
                        if (cu != null) {
                            unitIds += "," + cu.getId() + ";" + cu.getName();
                        }
                    }
                    e.setUnitId(unitIds.replaceFirst(",", ""));
                }
            } else {
                e.setUnitId("");
            }
        }
        return e;
    }

    @Override
    @Transactional
    public void saveBean(ExchangeDataType bean) {
        ExchangeDataType table = new ExchangeDataType();
        if (!StringUtils.isBlank(bean.getUuid())) {
            table = dao.getOne(bean.getUuid());
        }
        BeanUtils.copyProperties(bean, table);
        if (!StringUtils.isBlank(bean.getFormId())) {
            String xml = dyFormApiFacade.formDefinationToXml(dyFormApiFacade.getFormUuidById(bean.getFormId()));
            table.setText(exchangeDataService.convertString2Clob(xml));
        }
        dao.save(table);
    }

    @Override
    public List<ExchangeDataType> getExDataTypeList() {
        List<ExchangeDataType> source = listAll();
        List<ExchangeDataType> res = new ArrayList<ExchangeDataType>(source.size());
        for (ExchangeDataType o : source) {
            ExchangeDataType e = new ExchangeDataType();
            BeanUtils.copyProperties(o, e);
            res.add(e);
        }
        return res;
    }

    @Override
    public List<TreeNode> getViewAsTreeAsync(String nodeId, String typeId) {
        List<TreeNode> treeNodes = new ArrayList<TreeNode>();
        if (typeId.equals("null")) {
            // 接入系统界面获得数据类型树形列表
            List<ExchangeDataType> ddList = this.getExDataTypeList();
            for (ExchangeDataType d : ddList) {
                TreeNode node = new TreeNode();
                node.setId(d.getId());
                node.setData(d.getId());
                node.setName(d.getName());
                node.setNocheck(false);
                treeNodes.add(node);
            }

        } else if (typeId != null || typeId != "") {
            // 路由规则界面获取匹配后的数据转换规则树形列表(通过源类型)
            List<ExchangeDataTransform> dataTransList = exchangeDataTransformService.getListBySourceTypeId(typeId);
            for (ExchangeDataTransform d : dataTransList) {
                TreeNode node = new TreeNode();
                node.setId(d.getId());
                node.setData(d.getId());
                node.setName(d.getName());
                node.setNocheck(false);
                treeNodes.add(node);
            }
        }
        return treeNodes;
    }

    @Override
    public ExchangeDataType getExchangeDataTypeByUuid(String uuid) {
        return dao.getOne(uuid);
    }

    @Override
    public ExchangeDataType getByTypeId(String typeId) {
        return dao.getOneByHQL("from ExchangeDataType where id='" + typeId + "'", null);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.integration.service.ExchangeDataTypeService#getColumnInfoMapByTypeId(java.lang.String)
     */
    @Override
    public Map<String, DyformFieldDefinition> getColumnInfoMapByTypeId(String typeId) {
        ExchangeDataType dataType = getByTypeId(typeId);
        DyFormFormDefinition dyFormDefinition = dyFormApiFacade.getFormDefinition(dataType.getFormId());
        List<DyformFieldDefinition> fieldDefintions = dyFormDefinition.doGetFieldDefintions();
        Map<String, DyformFieldDefinition> attachmentFields = new HashMap<String, DyformFieldDefinition>();
        for (DyformFieldDefinition fieldDefinition : fieldDefintions) {
            attachmentFields.put(fieldDefinition.getName(), fieldDefinition);
        }
        return attachmentFields;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.integration.service.ExchangeDataTypeService#findByExample(com.wellsoft.pt.integration.entity.ExchangeDataType)
     */
    @Override
    public List<ExchangeDataType> findByExample(ExchangeDataType example) {
        return this.dao.listByEntity(example);
    }

    @Override
    public List<BusinessType> getBusinessTypeList() {
        List<BusinessType> list = unitApiFacade.getBusinessTypeList();
        return BeanUtils.convertCollection(list, BusinessType.class);
    }

    @Override
    public List<ExchangeDataType> getExchangeDataTypesByTypeIds(String typeIds) {
        return dao.getListByIds(typeIds);
    }

    /**
     * 通过业务类型和单位Id获取数据类型列表
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.integration.service.ExchangeDataTypeService#getExDataTypeListByUnitId()
     */
    @Override
    public List<ExchangeDataType> getExDataTypeListByUnitId() {
        List<CommonUnit> unitList = unitApiFacade.getCommonUnitsByBusinessTypeIdAndUserId(
                ExchangeConfig.EXCHANGE_BUSINESS_TYPE, SpringSecurityUtils.getCurrentUserId());
        if (unitList == null || unitList.size() == 0) {
            return new ArrayList<ExchangeDataType>();
        }
        CommonUnit unit = unitList.get(0);
        String hql = "from ExchangeDataType e where e.unitId like '%" + unit.getId()
                + "%' or e.unitId = null order by e.code";
        List<ExchangeDataType> typeList = dao.listByHQL(hql, null);
        if (typeList != null && typeList.size() > 0) {
            List<ExchangeDataType> res = new ArrayList<ExchangeDataType>(typeList.size());
            for (ExchangeDataType o : typeList) {
                ExchangeDataType e = new ExchangeDataType();
                BeanUtils.copyProperties(o, e);
                res.add(e);
            }
            return res;
        }
        return new ArrayList<ExchangeDataType>();
    }

    @Override
    public ExchangeDataType getExchangeDataTypeByExchangeDataUuid(String uuid) {
        ExchangeData exchangeData = exchangeDataService.getOne(uuid);
        ExchangeDataType exchangeDataType = getByTypeId(exchangeData.getExchangeDataBatch().getTypeId());
        return exchangeDataType;
    }

    @Override
    public List<TreeNode> getBusinessHandleList(String treeNodeId) {
        List<TreeNode> treeNodes = new ArrayList<TreeNode>();

        TreeNode treeNode;
        for (String key : BusinessHandleSourceMap.keySet()) {
            BusinessHandleSource source = BusinessHandleSourceMap.get(key);
            treeNode = new TreeNode();
            treeNode.setName(source.getBusinessName());
            treeNode.setId(key);
            treeNode.setData(key);
            treeNodes.add(treeNode);
        }
        return treeNodes;
    }

    @Override
    public List<TreeNode> getUnitSystemSourceList(String treeNodeId) {
        List<TreeNode> treeNodes = new ArrayList<TreeNode>();
        TreeNode treeNode;
        for (String key : unitSystemSourceMap.keySet()) {
            UnitSystemSource source = unitSystemSourceMap.get(key);
            treeNode = new TreeNode();
            treeNode.setName(source.getUnitSystemSourceName());
            treeNode.setId(key);
            treeNode.setData(key);
            treeNodes.add(treeNode);
        }
        return treeNodes;
    }

    @Override
    public List<ExchangeDataType> getListByIds(String arr) {
        return dao.getListByIds(arr);
    }

    @Override
    public ExchangeDataType getByFormId(String formId) {
        return dao.getOneByHQL("from ExchangeDataType where formId='" + formId + "'", null);
    }

    @Override
    @Transactional
    public void deleteAllByIds(List<String> ids) {
        this.dao.deleteByEntities(this.dao.listByFieldInValues("id", ids));
    }
}
