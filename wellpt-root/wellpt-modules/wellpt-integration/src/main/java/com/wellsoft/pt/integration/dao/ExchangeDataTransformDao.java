package com.wellsoft.pt.integration.dao;

import com.wellsoft.pt.integration.entity.ExchangeDataTransform;
import com.wellsoft.pt.jpa.dao.JpaDao;

import java.util.List;

/**
 * Description: ExchangeDataTransformDao
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
public interface ExchangeDataTransformDao extends JpaDao<ExchangeDataTransform, String> {

    List<ExchangeDataTransform> getBeanByids(String transformIds);

    List<ExchangeDataTransform> getListBySourceTypeId(String typeid);

}