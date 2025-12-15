package com.wellsoft.pt.integration.service;

import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.pt.integration.entity.*;

import java.util.List;
import java.util.Map;

/**
 * Description: 数据交换配置管理接口类
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
public interface ExchangeDataConfigService {

    public ExchangeSystem getBeanByUuid(String uuid);

    public void saveBean(ExchangeSystem bean);

    public void remove(String uuid);

    public void deleteAllByIds(String[] uuids);

    public List<TreeNode> getViewAsTreeAsync(String rootid, String unid);

    public List<ExchangeSystem> getExSystemList();

    public List<ExchangeSystem> getBeanByIds(String sysIds);

    public List<ExchangeSystem> findByExample(ExchangeSystem example);

    public List<ExchangeDataLog> getExchangeLogList();

    public List<ExchangeLog> getDXExchangeLogList();

    public ExchangeDataLog getExchangeDataLog(String uuid);

    public ExchangeLog getDXExchangeDataLog(String uuid);

    public Boolean removeLog(String uuid);

    public Boolean removeLogs(String[] uuids);

    public Boolean removeDXLog(String uuid);

    public Boolean removeDXLogs(String[] uuids);

    public Map<String, String> getSGSet();

    public Boolean saveSGSet(Map<String, String> map);

    public Map<String, SysProperties> getAllSysProperties(String moduleId);

    public SysProperties getSysProperty(String moduleId, String key);

    public Boolean saveSysPropertiesList(List<SysProperties> sysPropertiesList);

    public Boolean savaSysProperties(SysProperties s);

    public ExchangeDataType getExType(String id);

    List<ExchangeSystem> getExchangeSystems(String unitId);
}
