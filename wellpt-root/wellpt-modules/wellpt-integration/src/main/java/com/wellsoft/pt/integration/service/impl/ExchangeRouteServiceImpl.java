package com.wellsoft.pt.integration.service.impl;

import com.wellsoft.pt.dyform.facade.dto.DyFormFormDefinition;
import com.wellsoft.pt.dyform.facade.dto.DyformFieldDefinition;
import com.wellsoft.pt.dyform.facade.dto.DyformSubformFieldDefinition;
import com.wellsoft.pt.dyform.facade.dto.DyformSubformFormDefinition;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import com.wellsoft.pt.integration.dao.ExchangeRouteDao;
import com.wellsoft.pt.integration.entity.ExchangeDataTransform;
import com.wellsoft.pt.integration.entity.ExchangeDataType;
import com.wellsoft.pt.integration.entity.ExchangeRoute;
import com.wellsoft.pt.integration.service.ExchangeDataTransformService;
import com.wellsoft.pt.integration.service.ExchangeDataTypeService;
import com.wellsoft.pt.integration.service.ExchangeRouteService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.unit.entity.CommonUnit;
import com.wellsoft.pt.unit.facade.service.UnitApiFacade;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description: 路由规则业务类
 *
 * @author Administrator
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本    修改人     修改日期            修改内容
 * 2013-11-18.1 wbx     2013-11-18      Create
 * </pre>
 * @date 2013-11-18
 */
@Service
public class ExchangeRouteServiceImpl extends AbstractJpaServiceImpl<ExchangeRoute, ExchangeRouteDao, String> implements
        ExchangeRouteService {

    @Autowired
    private UnitApiFacade unitApiFacade;
    @Autowired
    private ExchangeDataTransformService exchangeDataTransformService;
    @Autowired
    private DyFormFacade dyFormApiFacade;
    @Autowired
    private ExchangeDataTypeService exchangeDataTypeService;

    @Override
    public List<ExchangeRoute> getExRouteList() {
        return listAll();
    }

    @Override
    public ExchangeRoute getBeanByUuid(String uuid) {
        ExchangeRoute exchangeRoute = this.dao.getOne(uuid);
        List<CommonUnit> cuTos = unitApiFacade.getCommonUnitListByIds(exchangeRoute.getToId());
        ExchangeRoute result = new ExchangeRoute();
        BeanUtils.copyProperties(exchangeRoute, result);
        if (cuTos != null && cuTos.size() > 0) {
            String unitIds = "";
            for (CommonUnit e : cuTos) {
                if (e != null) {
                    unitIds += "," + e.getId() + ";" + e.getName();
                }
            }
            result.setToId(unitIds.replaceFirst(",", ""));

        }
        List<ExchangeDataTransform> tfList = exchangeDataTransformService.getBeanByids(result.getTransformId());

        String transForms = "";
        if (tfList != null) {
            for (ExchangeDataTransform e : tfList) {
                transForms += "," + e.getId() + ";" + e.getName();
            }
            result.setTransformId(transForms.replaceFirst(",", ""));
        } else {
            result.setTransformId("");
        }

        return result;
    }

    @Override
    @Transactional
    public void saveBean(ExchangeRoute bean) {
        ExchangeRoute route = dao.getOne(bean.getUuid());
        if (route == null) {
            route = new ExchangeRoute();
        }
        BeanUtils.copyProperties(bean, route);
        dao.save(route);
    }

    /**
     * select方法调用
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.integration.service.ExchangeRouteService#getToFieldsOption(java.lang.String)
     */
    @Override
    public String getToFieldsOption(String typeId) {
        // TODO Auto-generated method stub
        StringBuffer sb = new StringBuffer();
        ExchangeDataType edt = exchangeDataTypeService.getByTypeId(typeId);
        if (!StringUtils.isBlank(edt.getFormId())) {
            DyFormFormDefinition dyFormDefinition = dyFormApiFacade.getFormDefinition(edt.getFormId());
            List<DyformFieldDefinition> fieldDefintions = dyFormDefinition.doGetFieldDefintions();
            for (DyformFieldDefinition fieldDefinition : fieldDefintions) {
                sb.append("<option ");
                sb.append(" value='main:" + dyFormDefinition.getName() + ":" + fieldDefinition.getName() + "'>");
                if (!StringUtils.isBlank(fieldDefinition.getDisplayName())) {
                    sb.append(fieldDefinition.getDisplayName());
                } else {
                    sb.append(fieldDefinition.getName());
                }
                sb.append("</option>");
            }

            List<DyformSubformFormDefinition> subformDefinitions = dyFormDefinition.doGetSubformDefinitions();
            for (DyformSubformFormDefinition subformDefinition : subformDefinitions) {
                sb.append("<option ");
                sb.append("disabled=true>" + subformDefinition.getDisplayName());
                sb.append("</option>");
                List<DyformSubformFieldDefinition> subformFieldDefinitions = subformDefinition
                        .getSubformFieldDefinitions();
                for (DyformSubformFieldDefinition subformFieldDefinition : subformFieldDefinitions) {
                    sb.append("<option ");
                    sb.append(" value='sub:" + subformDefinition.getName() + ":" + subformFieldDefinition.getName()
                            + "'>");
                    if (!StringUtils.isBlank(subformFieldDefinition.getDisplayName())) {
                        sb.append("&nbsp;&nbsp;" + subformFieldDefinition.getDisplayName());
                    } else {
                        sb.append("&nbsp;&nbsp;" + subformFieldDefinition.getName());
                    }
                    sb.append("</option>");
                }
            }
        }
        return sb.toString();
    }

    /**
     * @param typeId
     * @return
     */
    public List<Map<String, Object>> getToFieldsOptionList(String typeId) {
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        ExchangeDataType edt = exchangeDataTypeService.getByTypeId(typeId);
        if (!StringUtils.isBlank(edt.getFormId())) {
            DyFormFormDefinition dyFormDefinition = dyFormApiFacade.getFormDefinition(edt.getFormId());
            List<DyformFieldDefinition> fieldDefintions = dyFormDefinition.doGetFieldDefintions();
            for (DyformFieldDefinition fieldDefinition : fieldDefintions) {
                Map<String, Object> data = new HashMap<String, Object>();
                data.put("dyName", dyFormDefinition.getName());
                data.put("fieldName", fieldDefinition.getName());
                if (!StringUtils.isBlank(fieldDefinition.getDisplayName())) {
                    data.put("fieldShow", fieldDefinition.getDisplayName());
                } else {
                    data.put("fieldShow", fieldDefinition.getName());
                }
                // 是否是子表单数据
                data.put("isSubDy", false);
                result.add(data);
            }
            List<DyformSubformFormDefinition> subformDefinitions = dyFormDefinition.doGetSubformDefinitions();
            for (DyformSubformFormDefinition subformDefinition : subformDefinitions) {
                String subDyShowName = subformDefinition.getDisplayName();
                List<DyformSubformFieldDefinition> subformFieldDefinitions = subformDefinition
                        .getSubformFieldDefinitions();
                for (DyformSubformFieldDefinition subformFieldDefinition : subformFieldDefinitions) {
                    Map<String, Object> data = new HashMap<String, Object>();
                    data.put("subDyShowName", subDyShowName);
                    data.put("subDyName", subformDefinition.getName());
                    data.put("fieldName", subformFieldDefinition.getName());
                    if (!StringUtils.isBlank(subformFieldDefinition.getDisplayName())) {
                        data.put("fieldShow", subformFieldDefinition.getDisplayName());
                    } else {
                        data.put("fieldShow", subformFieldDefinition.getName());
                    }
                    data.put("isSubDy", true);
                    result.add(data);
                }
            }
        }
        return result;
    }

    @Override
    public List<ExchangeRoute> getExRouteListBySource(String fromTypeId) {
        String hql = "from ExchangeRoute e where e.fromTypeId=:fromTypeId";
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("fromTypeId", fromTypeId);
        return this.listByHQL(hql, values);
    }

    @Override
    @Transactional
    public void deleteAllByIds(List<String> uuids) {
        this.dao.deleteByUuids(uuids);
    }

    @Override
    @Transactional
    public void remove(String uuid) {
        this.dao.delete(uuid);
    }
}
