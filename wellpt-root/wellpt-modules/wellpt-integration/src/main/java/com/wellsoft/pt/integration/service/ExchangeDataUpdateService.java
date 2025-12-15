package com.wellsoft.pt.integration.service;

import com.wellsoft.pt.integration.entity.ExchangeData;

import java.util.List;
import java.util.Map;

/**
 * Description: 更新数据
 *
 * @author ruanhg
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-6-23.1	ruanhg		2014-6-23		Create
 * </pre>
 * @date 2014-6-23
 */
public interface ExchangeDataUpdateService {

    public void deleteDataByDataId(String dataId);

    public List<ExchangeData> getExchangeDataByFileSubFormData();

    public void xmlUpdateSubFormDate(String xml, String mainDataUuid, String mainFormUuid);

    public void moniCallbackClientAndreceiveClient(String dataUuid);

    /**
     * 导入旧建管系统项目
     *
     * @return
     */
    public void importproject(Integer begin, Integer end);

    public void test1();

    public void test2();

    public List<Map<String, Object>> queryList(String sql);

    public void executeUpdate(String sql);

    String saveYhq(Map<String, Object> dataItem);
}
