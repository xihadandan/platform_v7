package com.wellsoft.pt.common.home.service.impl;

import com.wellsoft.context.util.date.DateUtil;
import com.wellsoft.pt.app.service.AppApplicationService;
import com.wellsoft.pt.app.service.AppModuleService;
import com.wellsoft.pt.app.service.AppProductService;
import com.wellsoft.pt.app.service.AppSystemService;
import com.wellsoft.pt.bpm.engine.service.FlowDefinitionService;
import com.wellsoft.pt.common.home.service.AdminHomeService;
import com.wellsoft.pt.common.home.service.vo.DataItemVo;
import com.wellsoft.pt.common.home.service.vo.DataSourceVo;
import com.wellsoft.pt.dyform.implement.definition.enums.DyformTypeEnum;
import com.wellsoft.pt.dyform.implement.definition.service.FormDefinitionService;
import com.wellsoft.pt.multi.org.entity.MultiOrgSystemUnit;
import com.wellsoft.pt.multi.org.facade.service.MultiOrgSystemUnitFacadeService;
import com.wellsoft.pt.multi.org.facade.service.MultiOrgUserAccountFacadeService;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author yt
 * @title: AdminHomeServiceImpl
 * @date 2020/7/20 16:51
 */
@Service
public class AdminHomeServiceImpl implements AdminHomeService {


    @Autowired
    private FlowDefinitionService flowDefinitionService;
    @Autowired
    private FormDefinitionService formDefinitionService;
    @Autowired
    private AppApplicationService appApplicationService;
    @Autowired
    private AppModuleService appModuleService;
    @Autowired
    private AppProductService appProductService;
    @Autowired
    private AppSystemService appSystemService;
    @Autowired
    private MultiOrgSystemUnitFacadeService multiOrgSystemUnitFacadeService;
    @Autowired
    private MultiOrgUserAccountFacadeService multiOrgUserAccountFacadeService;

    @Override
    public List<DataSourceVo> dataOverview() {
        String systemUnitId = SpringSecurityUtils.getCurrentUserUnitId();
        List<DataSourceVo> dataSourceVoList = new ArrayList<>();
        if (systemUnitId.equals(MultiOrgSystemUnit.PT_ID)) {
            dataSourceVoList.add(this.authorizedUnits(systemUnitId));
            dataSourceVoList.add(this.processes(systemUnitId));
            dataSourceVoList.add(this.form(systemUnitId));
            dataSourceVoList.add(this.productIntegration(systemUnitId));
        } else {
            dataSourceVoList.add(this.productIntegration(systemUnitId));
            dataSourceVoList.add(this.user(systemUnitId));
            dataSourceVoList.add(this.processes(systemUnitId));
            dataSourceVoList.add(this.form(systemUnitId));
        }
        return dataSourceVoList;
    }

    private DataSourceVo getDataSourceVo(String title) {
        DataSourceVo dataSourceVo = new DataSourceVo();
        dataSourceVo.setTitle(title);
        return dataSourceVo;
    }

    private DataItemVo getDataItemVo(String name, long count) {
        DataItemVo itemVo = new DataItemVo();
        itemVo.setName(name);
        itemVo.setCount(count);
        return itemVo;
    }

    private DataSourceVo authorizedUnits(String systemUnitId) {
        DataSourceVo dataSourceVo = this.getDataSourceVo("授权单位数");
        dataSourceVo.getItems().add(this.getDataItemVo("总计", multiOrgSystemUnitFacadeService.count()));
        return dataSourceVo;
    }

    private DataSourceVo processes(String systemUnitId) {
        DataSourceVo dataSourceVo = this.getDataSourceVo("流程数");
        dataSourceVo.getItems().add(this.getDataItemVo("总计", flowDefinitionService.countBySystemUnitId(systemUnitId)));
        return dataSourceVo;
    }

    private DataSourceVo form(String systemUnitId) {
        DataSourceVo dataSourceVo = this.getDataSourceVo("表单数");
        dataSourceVo.getItems().add(this.getDataItemVo("总计", formDefinitionService.countBySystemUnitId(systemUnitId, null)));
        dataSourceVo.getItems().add(this.getDataItemVo("手机单据", formDefinitionService.countBySystemUnitId(systemUnitId, DyformTypeEnum.M.getValue())));
        return dataSourceVo;
    }

    private DataSourceVo productIntegration(String systemUnitId) {
        DataSourceVo dataSourceVo = this.getDataSourceVo("产品集成数");
        dataSourceVo.getItems().add(this.getDataItemVo("产品", appProductService.countBySystemUnitId(systemUnitId)));
        dataSourceVo.getItems().add(this.getDataItemVo("系统", appSystemService.countBySystemUnitId(systemUnitId)));
        dataSourceVo.getItems().add(this.getDataItemVo("模块", appModuleService.countBySystemUnitId(systemUnitId)));
        dataSourceVo.getItems().add(this.getDataItemVo("应用", appApplicationService.countBySystemUnitId(systemUnitId)));
        return dataSourceVo;
    }

    private DataSourceVo user(String systemUnitId) {
        DataSourceVo dataSourceVo = this.getDataSourceVo("用户数");
        Date startTime = DateUtil.getFirstDayOfMonth();
        dataSourceVo.getItems().add(this.getDataItemVo("总计", multiOrgUserAccountFacadeService.countBySystemUnitId(systemUnitId, null, null)));
        dataSourceVo.getItems().add(this.getDataItemVo("本月新增", multiOrgUserAccountFacadeService.countBySystemUnitId(systemUnitId, startTime, null)));
        return dataSourceVo;
    }

}
