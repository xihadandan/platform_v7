package com.wellsoft.pt.multi.org.service;

import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.multi.org.dao.UnitParamDao;
import com.wellsoft.pt.multi.org.entity.UnitParamEntity;

public interface UnitParamService extends JpaService<UnitParamEntity, UnitParamDao, String> {

    /**
     * 方法描述
     *
     * @param key
     * @return
     * @author baozh
     * @date 2021/10/22 12:01
     */
    UnitParamEntity getUnitParam(String key);

    /**
     * 根据key获取值
     *
     * @param key
     * @return
     * @author baozh
     * @date 2021/10/22 11:39
     */
    String getValue(String key);


    /**
     * 设置值
     *
     * @param key,value
     * @return
     * @author baozh
     * @date 2021/10/22 11:43
     */
    void setValue(String key, String value);
}
