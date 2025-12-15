package com.wellsoft.pt.org.dto;

import com.wellsoft.pt.org.entity.OrgElementI18nEntity;
import com.wellsoft.pt.org.entity.OrgUnitCodeEntity;
import com.wellsoft.pt.org.entity.OrgUnitEntity;
import com.wellsoft.pt.org.entity.OrgUnitExtAttrEntity;

import java.util.List;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2022年11月16日   chenq	 Create
 * </pre>
 */
public class OrgUnitDto extends OrgUnitEntity {
    private static final long serialVersionUID = -919403179342332879L;

    private List<OrgUnitExtAttrEntity> orgUnitExtAttrs;

    private List<OrgUnitCodeEntity> orgUnitCodes;

    private List<OrgElementI18nEntity> i18ns;


    public List<OrgUnitExtAttrEntity> getOrgUnitExtAttrs() {
        return this.orgUnitExtAttrs;
    }

    public void setOrgUnitExtAttrs(final List<OrgUnitExtAttrEntity> orgUnitExtAttrs) {
        this.orgUnitExtAttrs = orgUnitExtAttrs;
    }

    public List<OrgUnitCodeEntity> getOrgUnitCodes() {
        return this.orgUnitCodes;
    }

    public void setOrgUnitCodes(final List<OrgUnitCodeEntity> orgUnitCodes) {
        this.orgUnitCodes = orgUnitCodes;
    }

    public List<OrgElementI18nEntity> getI18ns() {
        return i18ns;
    }

    public void setI18ns(List<OrgElementI18nEntity> i18ns) {
        this.i18ns = i18ns;
    }
}
