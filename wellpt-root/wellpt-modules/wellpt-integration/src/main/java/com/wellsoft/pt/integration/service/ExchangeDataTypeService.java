package com.wellsoft.pt.integration.service;

import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.pt.dyform.facade.dto.DyformFieldDefinition;
import com.wellsoft.pt.integration.dao.ExchangeDataTypeDao;
import com.wellsoft.pt.integration.entity.ExchangeDataType;
import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.unit.entity.BusinessType;

import java.util.List;
import java.util.Map;

/**
 * Description: ExchangeDataTypeService
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
public interface ExchangeDataTypeService extends JpaService<ExchangeDataType, ExchangeDataTypeDao, String> {
    public List<ExchangeDataType> getExDataTypeList();

    public ExchangeDataType getBeanByUuid(String uuid);

    public void saveBean(ExchangeDataType bean);

    public ExchangeDataType getExchangeDataTypeByUuid(String uuid);

    public List<TreeNode> getViewAsTreeAsync(String nodeId, String id);

    public ExchangeDataType getByTypeId(String typeId);

    public Map<String, DyformFieldDefinition> getColumnInfoMapByTypeId(String typeId);

    public ExchangeDataType getExchangeDataTypeByExchangeDataUuid(String uuid);

    public List<ExchangeDataType> findByExample(ExchangeDataType example);

    public List<ExchangeDataType> getExchangeDataTypesByTypeIds(String typeIds);

    public List<BusinessType> getBusinessTypeList();

    public List<ExchangeDataType> getExDataTypeListByUnitId();

    public List<TreeNode> getBusinessHandleList(String treeNodeId);

    public List<TreeNode> getUnitSystemSourceList(String treeNodeId);

    List<ExchangeDataType> getListByIds(String arr);

    /**
     * @param formId
     * @return
     */
    public ExchangeDataType getByFormId(String formId);


    public void deleteAllByIds(List<String> ids);
}
