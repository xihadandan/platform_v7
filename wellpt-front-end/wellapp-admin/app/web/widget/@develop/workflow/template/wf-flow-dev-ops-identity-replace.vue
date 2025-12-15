<template>
  <Modal
    :bodyStyle="bodyStyle"
    :title="title"
    :cancel="onCancel"
    :width="1100"
    maxHeight="calc(-200px + 100vh)"
    :destroyOnClose="true"
    :visible="visible"
    ref="wfFlowDevOpsIdentityReplaceModal"
    centered
  >
    <template slot="content">
      <div class="identity-replace">
        <a-steps :current="currentStep" class="identity-replace-steps" labelPlacement="vertical">
          <a-step title="查找替换" description="" />
          <a-step title="完成" description="" />
        </a-steps>
        <div class="steps-content">
          <div v-show="currentStep == 0">
            <a-alert
              class="mb12"
              message="查找指定人员参与的流程和环节，可批量替换、追加或删除指定的办理人、抄送人等"
              type="info"
              show-icon
              closable
            />
            <a-config-provider :locale="locale">
              <IdentityReplaceSearchForm
                :searchForm="searchForm"
                @search="onSearch"
                @expandSearch="expandSearch"
              ></IdentityReplaceSearchForm>
            </a-config-provider>
            <div class="search-result">
              <span class="search-result-label">查找结果</span>
              <span style="margin-left: 10px">选择需要修改的内容</span>
            </div>
            <WidgetTable
              v-if="tableWidget"
              ref="tableWidget"
              :widget="tableWidget"
              @beforeLoadData="beforeLoadData"
              @tbodyRendered="onTbodyRendered"
            ></WidgetTable>
          </div>
          <IdentityReplaceResult
            :searchForm="searchForm"
            :records="modifyResults"
            v-if="currentStep == 1"
            @changeTableStyle="changeTableStyle"
          ></IdentityReplaceResult>
        </div>
      </div>
    </template>
    <template slot="footer">
      <a-button v-show="currentStep == 0" type="default" @click="close">取消</a-button>
      <a-button v-show="currentStep == 0" type="primary" @click="identityReplace">执行修改</a-button>
      <a-button v-show="currentStep == 1" type="default" @click="viewModifyRecord">查看修改记录</a-button>
      <a-button v-show="currentStep == 1" type="primary" @click="currentStep = 0">返回查找替换</a-button>
      <a-button v-show="currentStep == 1" type="primary" @click="close">关闭</a-button>
    </template>
  </Modal>
</template>

<script>
import Modal from '@pageAssembly/app/web/lib/modal.vue';
import OrgSelect from '@admin/app/web/lib/org-select.vue';
import IdentityReplaceResult from './identity-replace-result.vue';
import IdentityReplaceSearchForm from './identity-replace-search-form.vue';
import WfFlowDevOpsLog from './wf-flow-dev-ops-log.vue';
import { isEmpty } from 'lodash';
import { getElSpacingForTarget } from '@framework/vue/utils/util';
export default {
  props: {
    title: {
      type: String,
      default: '批量查找替换'
    },
    bodyStyle: {
      type: Object,
      default() {
        return { height: 'calc(-200px + 100vh)', overflow: 'hidden' };
      }
    },
    pageContext: Object,
    locale: Object
  },
  components: { Modal, OrgSelect, IdentityReplaceResult, IdentityReplaceSearchForm },
  provide() {
    return {
      pageContext: this.pageContext,
      locale: this.locale
    };
  },
  data() {
    return {
      currentStep: 0,
      visible: false,
      searchForm: {
        expandSearch: false,
        flowRange: 'all',
        flowDefId: '',
        orgRange: 'all',
        modifyMode: 'replace',
        saveAs: 'current'
      },
      // flowRangeOptions: [
      //   { label: '全部流程', value: 'all' },
      //   { label: '指定流程', value: 'specify' }
      // ],
      // orgRangeOptions: [
      //   { label: '办理人来源全部组织', value: 'all' },
      //   { label: '办理人来源全部行政组织', value: 'xz' },
      //   { label: '办理人来源全部业务组织', value: 'biz' },
      //   { label: '办理人来源指定行政组织', value: 'specify' }
      // ],
      // orgIdOptions: [],
      // expandSearch: false,
      // modifyModeOptions: [
      //   { label: '替换为', value: 'replace' },
      //   { label: '新增', value: 'add' },
      //   { label: '删除', value: 'delete' }
      // ],
      // saveAsOptions: [
      //   { label: '当前版本', value: 'current' },
      //   { label: '新版本', value: 'newVersion' }
      // ],
      tableWidget: null,
      modifyResults: []
    };
  },
  methods: {
    show() {
      const _this = this;
      _this.visible = true;
      if (!_this.tableWidget) {
        _this.getTableDefinition().then(widget => {
          _this.tableWidget = widget;
          _this.changeTableStyle();
        });
      }
    },
    close() {
      const _this = this;
      _this.visible = false;
      _this.onCancel();
    },
    onCancel() {
      this.currentStep = 0;
    },
    onSearch() {
      console.log(this.searchForm);
      this.$refs.tableWidget.refetch({ force: true });
    },
    getTableDefinition() {
      // 取表格定义 /page-designer/index?uuid=140514494085332992
      let widgetTableId = 'eQwOWciFRYimihLJciXrPTRGPNevSqqb';
      return new Promise((resolve, reject) => {
        $axios
          .post('/json/data/services', {
            serviceName: 'appContextService',
            methodName: 'getAppWidgetDefinitionById',
            args: JSON.stringify([widgetTableId, false])
          })
          .then(({ data: { data = {} } }) => {
            if (data.definitionJson) {
              let tableWidgetDefinition = JSON.parse(data.definitionJson);
              resolve(tableWidgetDefinition);
            } else {
              reject(data);
            }
          })
          .catch(res => {
            reject(res);
          });
      });
    },
    isSearchFormChanged() {
      const _this = this;
      if (!_this.latestSearchForm) {
        _this.latestSearchForm = JSON.parse(JSON.stringify(_this.searchForm));
      }
      let changed = JSON.stringify(_this.searchForm) != JSON.stringify(_this.latestSearchForm);
      if (changed) {
        _this.latestSearchForm = JSON.parse(JSON.stringify(_this.searchForm));
      }
      return changed;
    },
    expandSearch(expand) {
      this.changeTableStyle();
    },
    changeTableStyle($el) {
      this.$nextTick(() => {
        setTimeout(() => {
          if (!$el) {
            $el = this.$refs.tableWidget.$el.querySelector('.ant-table-content');
          }
          let { maxHeight } = getElSpacingForTarget(
            $el,
            this.$refs.wfFlowDevOpsIdentityReplaceModal.$refs.modalComponentRef.$el.querySelector('.ant-modal-body')
          );
          if (maxHeight) {
            maxHeight = maxHeight;
            $el.style.cssText += `;overflow-y:auto; height:${maxHeight}px;translate:height 0.3s;`;
          }
        }, 200);
      });
    },
    beforeLoadData(params) {
      const _this = this;
      let tableWidget = _this.$refs.tableWidget;
      if (_this.isSearchFormChanged(tableWidget)) {
        _this.$refs.tableWidget.selectedRowKeys = [];
        _this.$refs.tableWidget.selectedRows = [];
      }
      if (_this.searchForm.oldUserId) {
        params.criterions = [
          ...(params.criterions || []),
          { columnIndex: 'userValue', type: 'like', value: _this.searchForm.oldUserId },
          { columnIndex: 'userValue', type: 'nlike', value: _this.searchForm.oldUserId + '/' }
        ];
      }
      if (!_this.searchForm.includeHisVersion) {
        tableWidget.addDataSourceParams({ distinctVersion: 'true' });
      } else {
        tableWidget.deleteDataSourceParams(['distinctVersion']);
      }
      tableWidget.addDataSourceParams({ queryTaskUsers: 'true' });

      // 指定流程
      if (_this.flowDefIdCondition) {
        tableWidget.clearOtherConditions(_this.flowDefIdCondition);
      }
      if (_this.searchForm.flowRange == 'specify' && _this.searchForm.flowDefId) {
        _this.flowDefIdCondition = {
          conditions: [
            {
              columnIndex: 'id',
              value: _this.searchForm.flowDefId.split(';'),
              type: 'in'
            },
            {
              columnIndex: 'category',
              value: _this.searchForm.flowDefId.split(';'),
              type: 'in'
            }
          ],
          type: 'or'
        };
        tableWidget.addOtherConditions([_this.flowDefIdCondition]);
      }

      // 指定组织
      if (_this.orgIdCondition) {
        tableWidget.clearOtherConditions(_this.orgIdCondition);
      }
      if (_this.searchForm.orgRange == 'xz') {
        _this.orgIdCondition = {
          sql: "t3.user_org_id like 'O_%'"
        };
        tableWidget.addOtherConditions([_this.orgIdCondition]);
      } else if (_this.searchForm.orgRange == 'biz') {
        _this.orgIdCondition = {
          sql: "t3.user_org_id like 'BO_%'"
        };
        tableWidget.addOtherConditions([_this.orgIdCondition]);
      } else if (_this.searchForm.orgRange == 'specify' && _this.searchForm.orgId) {
        _this.orgIdCondition = {
          columnIndex: 'userOrgId',
          value: _this.searchForm.orgId,
          type: 'eq'
        };
        tableWidget.addOtherConditions([_this.orgIdCondition]);
      }
    },
    onTbodyRendered({ rows = [] }) {
      const _this = this;
      let oldUserName = _this.searchForm.oldUserName;
      if (oldUserName) {
        rows.forEach(row => {
          if (row.userArgValue) {
            let hightLightSpan = '';
            let displayName = row.userArgValue;
            if (row.userValue && row.userValue.includes(oldUserName)) {
              displayName = row.userValue + ';' + displayName;
            }
            hightLightSpan += `<span style="color: var(--w-primary-color); font-weight: bold">${oldUserName}</span>`;
            row.userArgValue = displayName.replaceAll(oldUserName, hightLightSpan);
          }
        });
      }
    },
    identityReplace() {
      const _this = this;
      if (isEmpty(_this.searchForm.oldUserId)) {
        _this.$message.error('被替换的办理人不能为空！');
        return;
      }
      if (isEmpty(_this.searchForm.newUserId) && _this.searchForm.modifyMode != 'delete') {
        _this.$message.error('新的办理人不能为空！');
        return;
      }
      let selectedRows = _this.$refs.tableWidget.selectedRows;
      if (!selectedRows || !selectedRows.length) {
        _this.$message.error('请选择要替换的记录！');
        return;
      }

      _this.$loading('修改中...');
      $axios
        .post('/proxy/api/workflow/identity/replace/modify', {
          params: _this.searchForm,
          records: selectedRows
        })
        .then(({ data: result }) => {
          _this.$loading(false);
          _this.$refs.tableWidget.selectedRowKeys = [];
          _this.$refs.tableWidget.selectedRows = [];
          _this.currentStep = 1;
          _this.modifyResults = result.data || [];
          _this.$refs.tableWidget.refetch({ force: true });
        })
        .catch(({ response }) => {
          _this.$loading(false);
          _this.$message.error((response && response.data && response.data.msg) || '服务异常！');
        });
    },
    viewModifyRecord() {
      let FlowDevOpsLogComponent = Vue.extend(WfFlowDevOpsLog);
      let devOpsLog = new FlowDevOpsLogComponent({
        propsData: {
          title: '批量维护记录',
          pageContext: this.pageContext,
          locale: this.locale
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
};
</script>

<style lang="less" scoped>
.identity-replace {
  .mb12 {
    margin-bottom: 12px;
  }
  .identity-replace-steps {
    width: e('calc(100% - 150px)');
    margin: 0 auto;
    --w-steps-title-color: var(--w-text-color-darker);
    --w-steps-wait-title-color: var(--w-text-color-darker);

    ::v-deep .ant-steps-item {
      .ant-steps-item-icon {
        width: 36px;
        height: 36px;
        line-height: 36px;
      }
      .ant-steps-item-title {
        font-weight: bold;
      }
      .ant-steps-item-wait {
        .ant-steps-item-title {
          color: var(--w-steps-title-color);
        }
      }
    }
    &:not(.ant-steps-vertical) .ant-steps-item-custom .ant-steps-item-icon {
      width: 36px;
    }
  }
  .steps-content {
    margin-top: 12px;
  }
  .search-result {
    margin-top: 8px;
    margin-bottom: 8px;
    line-height: 32px;
    color: var(--w-text-color-light);
    font-size: var(--w-font-size-sm);
    .search-result-label {
      color: var(--w-text-color-dark);
      font-size: var(--w-font-size-base);
      font-weight: bold;
    }
  }
}
</style>
