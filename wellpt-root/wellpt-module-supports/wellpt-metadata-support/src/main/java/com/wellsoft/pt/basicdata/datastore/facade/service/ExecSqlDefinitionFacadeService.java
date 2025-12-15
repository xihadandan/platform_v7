package com.wellsoft.pt.basicdata.datastore.facade.service;

import com.wellsoft.context.service.Facade;
import com.wellsoft.context.util.groovy.GroovyUseable;
import com.wellsoft.pt.basicdata.datastore.bean.ExecuteSqlDefinitionDto;

import java.util.List;
import java.util.Map;

/**
 * Description:
 *
 * @author chenq
 * @date 2018/9/4
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/9/4    chenq		2018/9/4		Create
 * </pre>
 */
@GroovyUseable
public interface ExecSqlDefinitionFacadeService extends Facade {

    String getSqlById(String id);

    void saveSql(ExecuteSqlDefinitionDto dto);

    ExecuteSqlDefinitionDto getDtoById(String id);

    void deleteByIds(List<String> ids);

    Object execute(String id, Map<String, Object> params);
}
