/*
* @(#)${createDate} V1.0
*
* Copyright 2015 WELL-SOFT, Inc. All rights reserved.
*/
package ${package}.facade.service.impl;

import java.util.Collection;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ${package}.bean.${bean};
import ${package}.entity.${entity};
import ${package}.facade.service.${entity}ViewMaintain;
import ${package}.service.${service};
import com.wellsoft.pt.core.service.impl.BaseServiceImpl;
import com.wellsoft.pt.utils.bean.BeanUtils;

/**
* Description: 如何描述该类
*
* @author ${author}
* @date ${createDate}
* @version 1.0
*
*
<pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * ${createDate}.1	${author}        ${createDate}		Create
 * </pre>
*
*/
@Service
@Transactional
public class ${entity}ViewMaintainImpl extends BaseServiceImpl implements ${entity}ViewMaintain {

@Autowired
private ${service} ${serviceLowFirst};

/**
* (non-Javadoc)
* @see ${package}.facade.service.${interface}ViewMaintain#getBean(java.lang.String)
*/
@Override
public ${valObj} getBean(String uuid) {
${entity} entity = ${serviceLowFirst}.get(uuid);
${valObj} bean = new ${valObj}();
BeanUtils.copyProperties(entity, bean);
return bean;
}

/**
* (non-Javadoc)
* @see ${package}.facade.service.${interface}ViewMaintain#saveBean(${package}.bean.${valObj})
*/
@Override
public void saveBean(${valObj} bean) {
String uuid = bean.getUuid();
${entity} entity = new ${entity}();
if (StringUtils.isNotBlank(uuid)) {
entity = ${serviceLowFirst}.get(uuid);
}
BeanUtils.copyProperties(bean, entity);
${serviceLowFirst}.save(entity);
}

/**
* (non-Javadoc)
* @see ${package}.facade.service.${interface}ViewMaintain#remove(java.lang.String)
*/
@Override
public void remove(String uuid) {
${serviceLowFirst}.remove(uuid);
}

/**
* (non-Javadoc)
* @see ${package}.facade.service.${interface}ViewMaintain#removeAll(java.util.Collection)
*/
@Override
public void removeAll(Collection
<String> uuids) {
    ${serviceLowFirst}.removeAllByPk(uuids);
    }

    }
