import WidgetTabDevelopment from '@develop/WidgetTabDevelopment';
class OrgPartnerSysTabDevelopment extends WidgetTabDevelopment {
  countOrgPartnerSysApplyCount() {

  }

  countOrgPartnerSysOrgCount() {

  }

  get META() {
    return {
      name: '协作系统外部组织tab二开',
      hook: {
        countOrgPartnerSysOrgCount: '外部组织的徽标数量计算',
        countOrgPartnerSysApplyCount: '协作系统申请的徽标数量计算'
      }
    }
  }
}



export default OrgPartnerSysTabDevelopment;
