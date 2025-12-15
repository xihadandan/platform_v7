package com.wellsoft.pt.integration.service;

import com.wellsoft.pt.integration.dao.ExchangeRouteDao;
import com.wellsoft.pt.integration.entity.ExchangeRoute;
import com.wellsoft.pt.jpa.service.JpaService;

import java.util.List;
import java.util.Map;

/**
 * Description: 路由规则接口
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
public interface ExchangeRouteService extends JpaService<ExchangeRoute, ExchangeRouteDao, String> {

    public List<ExchangeRoute> getExRouteList();

    public ExchangeRoute getBeanByUuid(String uuid);

    public void saveBean(ExchangeRoute bean);

    public String getToFieldsOption(String typeId);

    /**
     * 改造自方法getToFieldsOption(String typeId)
     *
     * @param typeId
     * @return
     */
    public List<Map<String, Object>> getToFieldsOptionList(String typeId);

    /**
     * @param typeId
     * @return
     */
    public List<ExchangeRoute> getExRouteListBySource(String typeId);

    void deleteAllByIds(List<String> uuids);

    void remove(String uuid);
}
