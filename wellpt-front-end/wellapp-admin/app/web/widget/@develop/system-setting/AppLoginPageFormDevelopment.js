import WidgetFormDevelopment from '@develop/WidgetFormDevelopment';
import BasicConfig from '@pageAssembly/app/web/page/login/component/login-basic/config.vue';

class AppLoginPageFormDevelopment extends WidgetFormDevelopment {



  submitData(e) {
    let formData = e.form, $wgt = e.$widget;
    let form = {
      uuid: formData.UUID, name: formData.NAME, title: formData.TITLE,
      enabled: formData.ENABLED == 1, isPc: formData.IS_PC == 1,
      remark: formData.REMARK
    }
    if (form.uuid == undefined) {
      form.defJson = JSON.stringify({
        type: 'LoginBasic',
        config: BasicConfig.methods.generateDefaultConfig()
      });
      form.tenant = $wgt._$USER.tenantId;
      form.system = window.__INITIAL_STATE__.SYSTEM_ID;
    }
    $axios.post(`/proxy/api/system/${formData.UUID == undefined ? 'saveLoginPage' : 'updateLoginPageWithoutDefJson'}`, form).then(({ data }) => {
      $wgt.$message.success('保存成功')
      this.clearLoginPageCache();
      this.emitEvent(`EliwIiphqpNoqapjwntwZUkTLcGsdsNA:closeModal`);
      this.emitEvent(`JCTNqFKEHYSiZWCHnLXWCWfhIUKXVDTE:refetch`);

    }).catch(error => { })

  }


  clearLoginPageCache() {
    $axios
      .get(`/api/cache/deleteByKey`, {
        params: {
          key: `${this.getSystemID()}:${this.getTenantID()}:systemLoginPagePolicy`
        }
      })
      .then(({ data }) => { })
      .catch(error => { });
  }


  get META() {
    return {
      name: '登录页表单二开',
      hook: {
        submitData: '提交表单数据',
      }
    }
  }
}
export default AppLoginPageFormDevelopment;
