package com.wellsoft.pt.integration.facade;

import com.wellsoft.context.service.AbstractApiFacade;
import com.wellsoft.pt.integration.entity.SysProperties;
import com.wellsoft.pt.integration.service.ExchangeDataClientService;
import com.wellsoft.pt.integration.service.ExchangeDataConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ExchangedataApiFacade extends AbstractApiFacade {
    @Autowired
    private ExchangeDataClientService exchangeDataClientService;
    @Autowired
    private ExchangeDataConfigService exchangeDataConfigService;

    /**
     * 记录删除的日志
     *
     * @param tableName
     * @param dataUuid
     * @return
     * @throws Exception
     */
    public boolean delDataLog(String tableName, String dataUuid) throws Exception {
        return exchangeDataClientService.delDataLog(tableName, dataUuid);
    }

    /**
     * 获取所有系统配置
     *
     * @param moduleId 为空时查所有
     * @return
     */
    public Map<String, SysProperties> getAllSysProperties(String moduleId) {
        return exchangeDataConfigService.getAllSysProperties(moduleId);
    }

    /**
     * 获取系统配置
     *
     * @param moduleId 为空时查所有
     * @return
     */
    public SysProperties getSysProperties(String moduleId, String key) {
        return exchangeDataConfigService.getSysProperty(moduleId, key);
    }

    public Boolean savaSysProperties(SysProperties s) {
        return exchangeDataConfigService.savaSysProperties(s);
    }
}
