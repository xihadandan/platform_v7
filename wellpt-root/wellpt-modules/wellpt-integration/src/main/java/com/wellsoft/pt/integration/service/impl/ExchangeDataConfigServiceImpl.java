package com.wellsoft.pt.integration.service.impl;

import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.util.reflection.ConvertUtils;
import com.wellsoft.pt.integration.entity.*;
import com.wellsoft.pt.integration.service.*;
import com.wellsoft.pt.jpa.util.BeanUtils;
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
 * Description: 数据交换配置管理类
 *
 * @author Administrator
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-11-17.1	wbx		2013-11-17		Create
 * </pre>
 * @date 2013-11-17
 */
@Service
public class ExchangeDataConfigServiceImpl implements ExchangeDataConfigService {

    @Autowired
    private ExchangeSystemService exchangeSystemService;

    @Autowired
    private UnitApiFacade unitApiFacade;

    @Autowired
    private ExchangeDataTypeService exchangeDataTypeService;
    @Autowired
    private ExchangeDataLogService exchangeDataLogService;
    @Autowired
    private ExchangeDataSGSetService exchangeDataSGSetService;
    @Autowired
    private ExchangeLogService exchangeLogService;
    @Autowired
    private SysPropertiesService sysPropertiesService;

    @Override
    public List<ExchangeSystem> getExSystemList() {
        return exchangeSystemService.listAll();
    }

    @Override
    public ExchangeSystem getBeanByUuid(String uuid) {
        ExchangeSystem exchangeSystem = this.exchangeSystemService.getOne(uuid);
        CommonUnit cu = unitApiFacade.getCommonUnitById(exchangeSystem.getUnitId());
        ExchangeSystem result = new ExchangeSystem();
        BeanUtils.copyProperties(exchangeSystem, result);
        if (cu != null) {
            String unitname = cu.getName();
            String comm = exchangeSystem.getUnitId() + ";" + unitname;
            result.setUnitId(comm);
        }
        // 库中(id;id) 转成(id;name,id;name)
        String ids = exchangeSystem.getTypeId();
        List<ExchangeDataType> list = exchangeDataTypeService.getListByIds(ids);
        if (list != null) {
            String typeids = "";
            for (ExchangeDataType e : list) {
                typeids += e.getId() + ";" + e.getName() + ",";
            }
            if (!"".equals(typeids)) {
                result.setTypeId(typeids.substring(0, typeids.lastIndexOf(",")));
            } else {
                result.setTypeId("");
            }
        } else {
            result.setTypeId("");
        }
        // 库中(id;id) 转成(id;name,id;name)
        String ids1 = exchangeSystem.getTypeId1();
        List<ExchangeDataType> list1 = exchangeDataTypeService.getListByIds(ids1);
        if (list1 != null) {
            String typeids1 = "";
            for (ExchangeDataType e : list1) {
                typeids1 += e.getId() + ";" + e.getName() + ",";
            }
            if (!"".equals(typeids1)) {
                result.setTypeId1(typeids1.substring(0, typeids1.lastIndexOf(",")));
            } else {
                result.setTypeId1("");
            }
        } else {
            result.setTypeId1("");
        }
        return result;
    }

    @Override
    public void saveBean(ExchangeSystem bean) {
        if (!StringUtils.isBlank(bean.getUuid())) {
            ExchangeSystem sys = exchangeSystemService.getOne(bean.getUuid());
            BeanUtils.copyProperties(bean, sys);
            exchangeSystemService.save(sys);
        } else {
            exchangeSystemService.save(bean);
        }
    }

    @Override
    public void remove(String uuid) {
        exchangeSystemService.delete(uuid);
    }

    @Override
    public void deleteAllByIds(String[] uuids) {
        for (int i = 0; i < uuids.length; i++) {
            exchangeSystemService.delete(uuids[i]);
        }
    }

    @Override
    public List<ExchangeSystem> getExchangeSystems(String unitId) {
        return this.exchangeSystemService.getExchangeSystemsByUnit(unitId);
    }

    @Override
    public List<TreeNode> getViewAsTreeAsync(String rootid, String unids) {
        List<TreeNode> treeNodes = new ArrayList<TreeNode>();
        List<ExchangeSystem> ddList = exchangeSystemService.getExSystemListByUnids(unids);
        for (ExchangeSystem d : ddList) {
            TreeNode node = new TreeNode();
            node.setId(d.getId());
            node.setData(d.getId());
            node.setName(d.getName());
            node.setNocheck(false);
            treeNodes.add(node);
        }
        return treeNodes;
    }

    @Override
    public List<ExchangeSystem> getBeanByIds(String sysIds) {
        return exchangeSystemService.getBeanByIds(sysIds);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.integration.service.ExchangeDataConfigService#findByExample(com.wellsoft.pt.integration.entity.ExchangeSystem)
     */
    @Override
    public List<ExchangeSystem> findByExample(ExchangeSystem example) {
        return this.exchangeSystemService.findByExample(example);
    }

    @Override
    public ExchangeDataLog getExchangeDataLog(String uuid) {
        ExchangeDataLog e = new ExchangeDataLog();
        ExchangeDataLog log = exchangeDataLogService.getOne(uuid);
        BeanUtils.copyProperties(log, e);
        if (log.getFromUnitId() != null && !"".equals(log.getFromUnitId())) {
            CommonUnit fromUnit = unitApiFacade.getCommonUnitById(log.getFromUnitId());
            e.setFromUnitId(fromUnit.getName() + "(" + fromUnit.getId() + ")");
        }
        if (log.getToUnitId() != null && !"".equals(log.getToUnitId())) {
            CommonUnit toUnit = unitApiFacade.getCommonUnitById(log.getToUnitId());
            if (toUnit == null) {
                e.setToUnitId(log.getToUnitId());
            } else {
                e.setToUnitId(toUnit.getName() + "(" + toUnit.getId() + ")");
            }
        }
        return e;
    }

    @Override
    public ExchangeLog getDXExchangeDataLog(String uuid) {

        ExchangeLog e = new ExchangeLog();
        ExchangeLog log = exchangeLogService.getOne(uuid);
        BeanUtils.copyProperties(log, e);
        if (log.getFromUnitId() != null && !"".equals(log.getFromUnitId())) {
            CommonUnit fromUnit = unitApiFacade.getCommonUnitById(log.getFromUnitId());
            e.setFromUnitId(fromUnit.getName() + "(" + fromUnit.getId() + ")");
        }
        return e;
    }

    @Override
    public Boolean removeLog(String uuid) {
        exchangeDataLogService.delete(uuid);
        return true;
    }

    @Override
    @Transactional
    public Boolean removeLogs(String[] uuids) {
        for (int i = 0; i < uuids.length; i++) {
            exchangeDataLogService.delete(uuids[i]);
        }
        return true;
    }

    @Override
    public Boolean removeDXLog(String uuid) {
        exchangeLogService.delete(uuid);
        return true;
    }

    @Override
    @Transactional
    public Boolean removeDXLogs(String[] uuids) {
        for (int i = 0; i < uuids.length; i++) {
            exchangeLogService.delete(uuids[i]);
        }
        return true;
    }

    @Override
    public Map<String, String> getSGSet() {
        Map<String, String> map = new HashMap<String, String>();
        List<ExchangeDataSGSet> exchangeDataSGSets = exchangeDataSGSetService.listAll();
        for (ExchangeDataSGSet exchangeDataSGSet : exchangeDataSGSets) {
            if (exchangeDataSGSet.getKey_().equals("receiveLimit")) {
                map.put("receiveLimit", exchangeDataSGSet.getValue_());
            } else if (exchangeDataSGSet.getKey_().equals("reportLimit")) {
                map.put("reportLimit", exchangeDataSGSet.getValue_());
            }
        }
        return map;
    }

    @Override
    public Boolean saveSGSet(Map<String, String> map) {
        try {
            for (String key_ : map.keySet()) {
                ExchangeDataSGSet exchangeDataSGSet = exchangeDataSGSetService.getByKey(key_);
                if (exchangeDataSGSet == null) {
                    exchangeDataSGSet = new ExchangeDataSGSet();
                }
                exchangeDataSGSet.setValue_(map.get(key_));
                exchangeDataSGSetService.save(exchangeDataSGSet);
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public List<ExchangeDataLog> getExchangeLogList() {
        return exchangeDataLogService.listAll();
    }

    @Override
    public List<ExchangeLog> getDXExchangeLogList() {
        return exchangeLogService.listAll();

    }

    @Override
    public Map<String, SysProperties> getAllSysProperties(String moduleId) {
        List<SysProperties> sysPropertiesList = sysPropertiesService.getAllSysProperties(moduleId);
        Map<String, SysProperties> map = ConvertUtils.convertElementToMap(sysPropertiesList, "proEnName");
        return map;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.integration.service.ExchangeDataConfigService#getSysProperty(java.lang.String, java.lang.String)
     */
    @Override
    public SysProperties getSysProperty(String moduleId, String key) {
        SysProperties example = new SysProperties();
        example.setModuleId(moduleId);
        example.setProEnName(key);
        List<SysProperties> sysProperties = sysPropertiesService.findByExample(example);
        return sysProperties.isEmpty() ? null : sysProperties.get(0);
    }

    @Override
    public Boolean saveSysPropertiesList(List<SysProperties> sysPropertiesList) {
        return sysPropertiesService.saveSysProperties(sysPropertiesList);
    }

    @Override
    public Boolean savaSysProperties(SysProperties s) {
        try {
            sysPropertiesService.save(s);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public ExchangeDataType getExType(String id) {
        return exchangeDataTypeService.getByTypeId(id);
    }

}
