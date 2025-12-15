package com.wellsoft.pt.dms.web;

import com.wellsoft.context.component.select2.Select2DataBean;
import com.wellsoft.context.enums.IdPrefix;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.web.controller.ApiResult;
import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.pt.basicdata.business.entity.BusinessCategoryEntity;
import com.wellsoft.pt.basicdata.business.entity.BusinessCategoryOrgEntity;
import com.wellsoft.pt.basicdata.business.entity.BusinessRoleEntity;
import com.wellsoft.pt.basicdata.business.service.BusinessCategoryOrgService;
import com.wellsoft.pt.basicdata.business.service.BusinessCategoryService;
import com.wellsoft.pt.basicdata.business.service.BusinessRoleService;
import com.wellsoft.pt.dms.dto.DmsDocExchangeConfigDto;
import com.wellsoft.pt.dms.dto.DocExcConfig;
import com.wellsoft.pt.dms.entity.DmsDocExchangeConfigEntity;
import com.wellsoft.pt.dms.event.DocExchangeEvent;
import com.wellsoft.pt.dms.service.DmsDocExchangeConfigService;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.multi.org.entity.MultiOrgElement;
import com.wellsoft.pt.multi.org.entity.MultiOrgUserWorkInfo;
import com.wellsoft.pt.multi.org.service.MultiOrgElementService;
import com.wellsoft.pt.multi.org.service.MultiOrgUserWorkInfoService;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import io.jsonwebtoken.lang.Collections;
import io.swagger.annotations.*;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * @Auther: yt
 * @Date: 2021/7/13 13:36
 * @Description:
 */
@Api(tags = "公文交换配置")
@RestController
@RequestMapping(value = "/api/dms/doc/exc/config", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@Validated
public class DmsDocExchangeConfigController extends BaseController {

    @Autowired
    private DmsDocExchangeConfigService dmsDocExchangeConfigService;
    @Autowired
    private List<DocExchangeEvent> docExchangeEvents;
    @Autowired
    private BusinessCategoryService businessCategoryService;
    @Autowired
    private BusinessCategoryOrgService businessCategoryOrgService;
    @Autowired
    private BusinessRoleService businessRoleService;
    @Autowired
    private MultiOrgUserWorkInfoService multiOrgUserWorkInfoService;
    @Autowired
    private MultiOrgElementService multiOrgElementService;

    @GetMapping(value = "/getOne")
    @ApiOperation(value = "根据主键uuid查询数据", notes = "根据主键uuid查询数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uuid", value = "主键uuid", paramType = "query", dataType = "String", required = false)})
    public ApiResult<DmsDocExchangeConfigDto> getOne(
            @NotBlank(message = "uuid不能为空") @RequestParam(name = "uuid") String uuid) {
        DmsDocExchangeConfigEntity configEntity = dmsDocExchangeConfigService.getOne(uuid);
        if (configEntity == null) {
            throw new RuntimeException("资源不存在");
        }
        DmsDocExchangeConfigDto configDto = new DmsDocExchangeConfigDto();
        BeanUtils.copyProperties(configEntity, configDto);
        return ApiResult.success(configDto);
    }

    @GetMapping(value = "/queryList")
    @ApiOperation(value = "查询列表", notes = "查询列表")
    public ApiResult<List<DocExcConfig>> queryList() {
        List<DocExcConfig> docExcConfigList = dmsDocExchangeConfigService.queryList();
        return ApiResult.success(docExcConfigList);
    }

    @PostMapping(value = "/saveOrUpdate")
    @ApiOperation(value = "保存或更新", notes = "保存或更新")
    public ApiResult<String> saveOrUpdate(
            @ApiParam(value = "公文交换配置") @Validated @RequestBody DmsDocExchangeConfigDto configDto) {
        String uuid = dmsDocExchangeConfigService.saveOrUpdate(configDto);
        return ApiResult.success(uuid);
    }

    @PostMapping(value = "/del")
    @ApiOperation(value = "删除", notes = "删除")
    public ApiResult del(
            @ApiParam(value = "uuid") @NotBlank(message = "uuid不能为空") @RequestParam(name = "uuid") String uuid) {
        dmsDocExchangeConfigService.del(uuid);
        return ApiResult.success();
    }

    @PostMapping(value = "/sequence")
    @ApiOperation(value = "排序", notes = "sequence")
    public ApiResult sequence(@ApiParam(value = "uuids新序号集合") @Validated @RequestBody List<DocExcConfig> configList) {
        dmsDocExchangeConfigService.sequence(configList);
        return ApiResult.success();
    }

    @GetMapping(value = "/queryEvents")
    @ApiOperation(value = "查询事件列表", notes = "查询事件列表")
    public ApiResult<List<Select2DataBean>> queryEvents() {
        List<Select2DataBean> select2DataBeanList = new ArrayList<>();
        for (DocExchangeEvent docExchangeEvent : docExchangeEvents) {
            select2DataBeanList.add(new Select2DataBean(docExchangeEvent.getId(), docExchangeEvent.getName()));
        }
        return ApiResult.success(select2DataBeanList);
    }

    @GetMapping(value = "/queryBusinessCategorys")
    @ApiOperation(value = "查询业务分类", notes = "查询业务分类")
    public ApiResult<List<Select2DataBean>> queryBusinessCategorys() {
        BusinessCategoryEntity categoryEntity = new BusinessCategoryEntity();
        categoryEntity.setSystemUnitId(SpringSecurityUtils.getCurrentUserUnitId());
        List<BusinessCategoryEntity> categoryEntityList = businessCategoryService.listByEntity(categoryEntity);
        List<Select2DataBean> select2DataBeanList = new ArrayList<>();
        for (BusinessCategoryEntity category : categoryEntityList) {
            select2DataBeanList.add(new Select2DataBean(category.getUuid(), category.getName()));
        }
        return ApiResult.success(select2DataBeanList);
    }

    @GetMapping(value = "/queryBusinessCategorOrgs")
    @ApiOperation(value = "查询业务单位", notes = "查询业务单位")
    public ApiResult<List<Select2DataBean>> queryBusinessCategorOrgs(
            @ApiParam(value = "业务分类uuid") @RequestParam(name = "categoryUuid") String categoryUuid) {
        // BusinessCategoryOrgEntity orgEntity = new BusinessCategoryOrgEntity();
        // orgEntity.setBusinessCategoryUuid(categoryUuid);
        // orgEntity.setType(BusinessCategoryOrgEntity.TYPE_1);//单位
        // List<BusinessCategoryOrgEntity> orgEntityList =
        // businessCategoryOrgService.listByEntity(orgEntity);
        List<Select2DataBean> select2DataBeanList = new ArrayList<>();
        if (StringUtils.isNotBlank(categoryUuid)) {
            // 配置通讯录，获取通讯录的业务单位
            List<BusinessCategoryOrgEntity> orgEntityList = businessCategoryOrgService.listByUuid(categoryUuid);
            for (BusinessCategoryOrgEntity org : orgEntityList) {
                select2DataBeanList.add(new Select2DataBean(org.getId(), org.getName()));
            }
        } else {
            // 未配置通讯录，获取发件人所在组织版本下的所在单位名称
            MultiOrgUserWorkInfo userWorkInfo = multiOrgUserWorkInfoService
                    .getUserWorkInfo(SpringSecurityUtils.getCurrentUserId());
            List<MultiOrgElement> result = null;
            if (userWorkInfo != null) {
                String[] eleIdPaths = userWorkInfo.getEleIdPaths().split(Separator.SEMICOLON.getValue());
                List<String> businessIds = new ArrayList<>();
                for (int i = 0, len = eleIdPaths.length; i < len; i++) {
                    String[] eleIdPath = eleIdPaths[i].split(Separator.SLASH.getValue());
                    for (int j = eleIdPath.length - 1; j >= 0; j--) {
                        String eleId = eleIdPath[j];
                        if (eleId.contains(IdPrefix.BUSINESS_UNIT.getValue())) {
                            businessIds.add(eleId);
                        }
                    }
                }
                result = multiOrgElementService.getOrgElementsByIds(businessIds);
                result.sort(new Comparator<MultiOrgElement>() {
                    @Override
                    public int compare(MultiOrgElement o1, MultiOrgElement o2) {
                        return o1.getCode().compareTo(o2.getCode());
                    }
                });
                for (MultiOrgElement ele : result) {
                    select2DataBeanList.add(new Select2DataBean(ele.getId(), ele.getName()));
                }
            }
            // 没有设置业务通讯录，也没有业务单位，先按照用户的归属单位显示
            if (Collections.isEmpty(result)) {
                select2DataBeanList.add(new Select2DataBean(SpringSecurityUtils.getCurrentUserUnitId(),
                        SpringSecurityUtils.getCurrentUserUnitName()));
            }
        }
        return ApiResult.success(select2DataBeanList);
    }

    @GetMapping(value = "/queryBusinessRoles")
    @ApiOperation(value = "查询业务角色", notes = "查询业务角色")
    public ApiResult<List<Select2DataBean>> queryBusinessRoles(
            @ApiParam(value = "业务分类uuid") @NotBlank(message = "业务分类uuid不能为空") @RequestParam(name = "categoryUuid") String categoryUuid) {
        BusinessRoleEntity businessRoleEntity = new BusinessRoleEntity();
        businessRoleEntity.setBusinessCategoryUuid(categoryUuid);
        List<BusinessRoleEntity> roleEntityList = businessRoleService.listByEntity(businessRoleEntity);
        List<Select2DataBean> select2DataBeanList = new ArrayList<>();
        for (BusinessRoleEntity role : roleEntityList) {
            select2DataBeanList.add(new Select2DataBean(role.getUuid(), role.getName()));
        }
        return ApiResult.success(select2DataBeanList);
    }

}
