import VuePageDevelopment from '@develop/VuePageDevelopment';

class SystemManagerWorkbenchesDevelopment extends VuePageDevelopment {


  mounted() {
    if (this.getTenantID() == 'T001' && this.getSystemID() != undefined) {
      // 兼容默认租户系统下无授权环节的数据初始化
      this.initOrgModelSetting();
      // 初始化节假日
      this.initTsHoliday();
      // 初始化流程设置
      this.initFlowSetting();
      // 初始化文件库权限模型
      this.initFilePermissionModel();
    }
  }

  initOrgModelSetting() {
    $axios.get(`/proxy/api/org/elementModel/createOrgElementModelAndSetting`, {
      params: {
        system: this.getSystemID(),
        tenant: this.getTenantID()
      }
    })
  }

  /**
   * 初始化节假日
   */
  initTsHoliday() {
    $axios.get(`/proxy/api/ts/holiday/initBuildInHoliday`, {
      params: {
        system: this.getSystemID(),
        tenant: this.getTenantID()
      }
    })
  }

  /**
   * 初始化流程设置
   */
  initFlowSetting() {
    $axios.get(`/proxy/api/workflow/setting/init`, {
      params: {
        system: this.getSystemID(),
        tenant: this.getTenantID()
      }
    })
  }

  /**
   * 初始化文件库权限模型
   */
  initFilePermissionModel() {
    $axios.get(`/proxy/api/dms/role/init`, {
      params: {
        system: this.getSystemID(),
        tenant: this.getTenantID()
      }
    })
  }

  get META() {
    return {
      name: '系统管理工作台页面二开'
    }
  }
}
export default SystemManagerWorkbenchesDevelopment;
