/*
 * @(#)2013-1-14 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.org.service;

import com.wellsoft.pt.org.bean.OptionBean;
import com.wellsoft.pt.org.entity.Option;

import java.util.Collection;
import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-1-14.1	zhulh		2013-1-14		Create
 * </pre>
 * @date 2013-1-14
 */
public interface OptionService {

    /**
     * 如何描述该方法
     *
     * @param uuid
     * @return
     */
    OptionBean getBean(String uuid);

    /**
     * 如何描述该方法
     *
     * @param bean
     */
    void saveBean(OptionBean bean);

    /**
     * 根据UUID删除组织选择项
     *
     * @param uuid
     */
    void remove(String uuid);

    /**
     * 根据UUID，批量删除组织选择项
     *
     * @param uuids
     */
    void removeAll(Collection<String> uuids);

    /**
     * 如何描述该方法
     *
     * @return
     */
    List<Option> getAll();

    Boolean checkUnique(Option option);

}
