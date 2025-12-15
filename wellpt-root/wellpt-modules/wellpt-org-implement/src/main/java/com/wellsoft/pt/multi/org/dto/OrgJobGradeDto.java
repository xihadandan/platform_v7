package com.wellsoft.pt.multi.org.dto;

import com.wellsoft.pt.multi.org.entity.OrgJobGradeEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * Description:
 * 职等请求返回
 * <pre>
 * 修改记录:
 * 修改后版本    修改人     修改日期    修改内容
 * 1.0         baozh    2021/10/22   Create
 * </pre>
 */
@ApiModel("职等")
public class OrgJobGradeDto {

    @ApiModelProperty("排序")
    private String order;

    @ApiModelProperty("职等信息")
    private List<OrgJobGradeEntity> orgJobGrades;

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public List<OrgJobGradeEntity> getOrgJobGrades() {
        return orgJobGrades;
    }

    public void setOrgJobGrades(List<OrgJobGradeEntity> orgJobGrades) {
        this.orgJobGrades = orgJobGrades;
    }
}
