package com.wellsoft.pt.integration.dao;

import com.wellsoft.pt.integration.entity.ExchangeDataRepest;
import com.wellsoft.pt.jpa.dao.JpaDao;

import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author Administrator
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-1-4.1	Administrator		2014-1-4		Create
 * </pre>
 * @date 2014-1-4
 */
public interface ExchangeDataRepestDao extends JpaDao<ExchangeDataRepest, String> {

    List<ExchangeDataRepest> getExchangeDataRepestIngYZYM();

    List<ExchangeDataRepest> getExchangeDataRepestIngGSDJ();

    List<ExchangeDataRepest> getExchangeDataRepestIngSSDJ();

    List<ExchangeDataRepest> getExchangeDataRepestIng();

}