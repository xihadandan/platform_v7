package com.wellsoft.pt.multi.org.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * Description:
 * es全文检索实现
 * <pre>
 * 修改记录:
 * 修改后版本    修改人     修改日期    修改内容
 * 1.0         baozh    2021/10/26   Create
 * </pre>
 */
@ApiModel("职等列表")
public class OrgJobGradesVo {

    @ApiModelProperty("排序")
    private String order;

    @ApiModelProperty("职等信息")
    private List<OrgJobGradeVo> orgJobGrades;

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public List<OrgJobGradeVo> getOrgJobGrades() {
        return orgJobGrades;
    }

    public void setOrgJobGrades(List<OrgJobGradeVo> orgJobGrades) {
        this.orgJobGrades = orgJobGrades;
    }
}
