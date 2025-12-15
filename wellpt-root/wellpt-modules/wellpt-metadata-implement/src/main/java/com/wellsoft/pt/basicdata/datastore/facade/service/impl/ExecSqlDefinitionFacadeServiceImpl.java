package com.wellsoft.pt.basicdata.datastore.facade.service.impl;

import com.wellsoft.context.service.AbstractApiFacade;
import com.wellsoft.pt.basicdata.datastore.bean.ExecuteSqlDefinitionDto;
import com.wellsoft.pt.basicdata.datastore.entity.ExecuteSqlDefinitionEntity;
import com.wellsoft.pt.basicdata.datastore.facade.service.ExecSqlDefinitionFacadeService;
import com.wellsoft.pt.basicdata.datastore.service.ExecuteSqlDefinitionService;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sql.rowset.serial.SerialClob;
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
@Service
public class ExecSqlDefinitionFacadeServiceImpl extends AbstractApiFacade implements
        ExecSqlDefinitionFacadeService {

    @Autowired
    ExecuteSqlDefinitionService executeSqlDefinitionService;

    @Override
    public String getSqlById(String id) {
        return executeSqlDefinitionService.getSqlById(id);
    }

    @Override
    public void saveSql(ExecuteSqlDefinitionDto dto) {
        try {
            ExecuteSqlDefinitionEntity entity = new ExecuteSqlDefinitionEntity();
            if (StringUtils.isNotBlank(dto.getUuid())) {
                entity = executeSqlDefinitionService.getOne(dto.getUuid());
            } else {
                entity.setId(dto.getId());
            }
            entity.setName(dto.getName());
            entity.setSqlContent(new SerialClob(dto.getSqlContent().toCharArray()));

            executeSqlDefinitionService.save(entity);
        } catch (Exception e) {
            logger.error("保存可执行sql配置异常：", e);
        }


    }

    @Override
    public ExecuteSqlDefinitionDto getDtoById(String id) {
        ExecuteSqlDefinitionEntity entity = executeSqlDefinitionService.getById(id);
        ExecuteSqlDefinitionDto dto = new ExecuteSqlDefinitionDto();
        BeanUtils.copyProperties(entity, dto, "sqlContent");
        try {
            if (entity.getSqlContent() != null)
                dto.setSqlContent(IOUtils.toString(entity.getSqlContent().getCharacterStream()));
        } catch (Exception e) {
            logger.error("设置sqlContent异常：", e);
        }
        return dto;
    }

    @Override
    public void deleteByIds(List<String> ids) {
        executeSqlDefinitionService.deleteByIds(ids);
    }


    @Override
    public Object execute(String id, Map<String, Object> params) {
        String sql = getSqlById(id);
        if (StringUtils.isNotBlank(sql)) {
            String upperSql = sql.trim().toUpperCase();
            if (upperSql.startsWith("SELECT")) {
                return executeSqlDefinitionService.executeQrySQL(sql, params);
            } else {
                return executeSqlDefinitionService.executeUpdate(sql, params);
            }
        }
        return null;
    }


}
