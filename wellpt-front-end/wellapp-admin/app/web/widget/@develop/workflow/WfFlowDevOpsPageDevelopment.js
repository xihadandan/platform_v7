import VuePageDevelopment from '@develop/VuePageDevelopment';
import WfFlowDevOpsIdentityReplace from './template/wf-flow-dev-ops-identity-replace.vue';
import WfFlowDevOpsLog from './template/wf-flow-dev-ops-log.vue';
class WfFlowDefinitionPageDevelopment extends VuePageDevelopment {
  get META() {
    return {
      name: '流程管理_流程运维页面二开',
      hook: {
        identityReplace: '批量查找替换',
        viewLogs: '批量维护记录'
      }
    };
  }

  mounted() {
    const _this = this;
    this.$widget.pageContext.handleEvent('devOpsCategorySelect', (selectedKeys, selectedKeysIncludeChildren, sourceCode) => {
      // 表格数据过滤
      let tableWidget = _this.getVueWidgetById('VBsDCEwGHHHExzJczWlrWxGxzVTaKWJX');
      let dataSource = tableWidget.getDataSourceProvider();
      if (selectedKeys.length > 0) {
        dataSource.removeParam('categoryUuid');
        dataSource.addParam('categoryUuid', selectedKeys[0]);
      } else {
        dataSource.removeParam('categoryUuid');
      }
      tableWidget.refetch(true);
    });
  }

  /**
   * 批量查找替换
   *
   * @param {*} evt
   * @returns
   */
  identityReplace(evt) {
    let IdentityReplaceComponent = Vue.extend(WfFlowDevOpsIdentityReplace);
    let identityReplace = new IdentityReplaceComponent({
      propsData:
      {
        title: '批量查找替换',
        pageContext: this.$widget.pageContext,
        $pageJsInstance: this.$widget.$pageJsInstance,
        locale: evt.$evtWidget.locale
      },
      i18n: $app && $app.$options && $app.$options.i18n
    });
    let identityReplaceDiv = document.createElement('div');
    identityReplaceDiv.classList.add('identity-replace-container');
    document.body.appendChild(identityReplaceDiv);
    identityReplace.$mount(identityReplaceDiv);
    identityReplace.show();
  }

  /**
   *
   * @param {*} evt
   */
  viewLogs(evt) {
    let FlowDevOpsLogComponent = Vue.extend(WfFlowDevOpsLog);
    let devOpsLog = new FlowDevOpsLogComponent({
      propsData: {
        title: '批量维护记录',
        pageContext: this.$widget.pageContext,
        $pageJsInstance: this.$widget.$pageJsInstance,
        locale: evt.$evtWidget.locale
      },
      i18n: $app && $app.$options && $app.$options.i18n
    });
    let devOpsLogDiv = document.createElement('div');
    devOpsLogDiv.classList.add('dev-ops-log-container');
    document.body.appendChild(devOpsLogDiv);
    devOpsLog.$mount(devOpsLogDiv);
    devOpsLog.show();
  }

}

export default WfFlowDefinitionPageDevelopment;
