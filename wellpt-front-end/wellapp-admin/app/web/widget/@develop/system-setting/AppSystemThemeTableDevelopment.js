import WidgetTableDevelopment from '@develop/WidgetTableDevelopment';
import { generateId } from '@framework/vue/utils/util';

class AppSystemThemeTableDevelopment extends WidgetTableDevelopment {



  fetchSystemPcThemeOptions() {
    return new Promise((resolve, reject) => {
      $axios
        .get(`/proxy/api/system/getTenantSystemPageSetting`, {
          params: {
            tenant: this.getTenantID(),
            system: this.getSystemID()
          }
        })
        .then(({ data }) => {
          let config = data.data;
          if (config) {
            // 设置
            let wForm = this.getVueWidgetById('VFwPHKPWdleINkcvxQRMcyRXBsOueqKn');
            wForm.setField('userThemeDefinable', config.userThemeDefinable);
            if (config.themeStyle) {
              let themeStyle = JSON.parse(config.themeStyle);
              resolve({
                rows: themeStyle.pc
              })
            }
          }
        })
        .catch(error => { });
    })
  }


  fetchPcThemPacks({ tableParams, $widget }) {
    debugger
    return new Promise((resolve, reject) => {
      $axios
        .post(`/proxy/api/theme/pack/query`, {
          page: {
            currentPage: $widget.pagination.current,
            pageSize: $widget.pagination.pageSize,
          },
          status: 'PUBLISHED', tagUuids: tableParams.tagUuids, type: 'PC'
        })
        .then(({ data }) => {
          resolve({ rows: { data: data.data.data } });
        })
        .catch(() => { });
    });
  }


  get META() {
    return {
      name: '系统主题风格表格二开',
      hook: {
        fetchSystemPcThemeOptions: '获取桌面端当前系统可用的主题数据',
        fetchPcThemPacks: '获取桌面端主题包数据'
      }
    }
  }
}
export default AppSystemThemeTableDevelopment;
