<template>
  <WidgetDyformSetting v-if="widgetDyformSetting" :parent="getSelf" :widget="widgetDyformSetting" :initFormData="initFormData" />
  <a-layout v-else-if="bizData" class="process-node-view-container" theme="light">
    <a-layout-header class="process-node-view-header">
      <a-row>
        <a-col span="18" class="title">
          <h1>{{ bizData.title }}</h1>
        </a-col>
        <a-col span="6" class="process-node-action-container">
          <a-space>
            <a-button v-for="action in actions" :key="action.id" @click="onActionClick($event, action)">
              {{ action.name }}
            </a-button>
          </a-space>
        </a-col>
      </a-row>
    </a-layout-header>
    <a-layout-content class="process-node-view-content">
      <WidgetDyform
        v-if="bizData.dyFormData && !dyformComponentLoading"
        ref="dyform"
        :isNewFormData="isNewFormData"
        :displayState="dyformDisplayState"
        :definitionVjson="definitionVjson"
        :formUuid="bizData.dyFormData.formUuid"
        :dataUuid="bizData.dyFormData.dataUuid || bizData.dataUuid"
        :formDatas="bizData.dyFormData.formDatas"
        @mounted="onDyformMounted"
        :dyformStyle="{ padding: 'var(--w-padding-md)', background: 'var(--w-bg-color-body)' }"
      />
    </a-layout-content>
  </a-layout>
</template>

<script>
import WidgetDyformSetting from '@pageAssembly/app/web/widget/widget-dyform-setting/widget-dyform-setting.vue';
import WidgetTable from '@pageAssembly/app/web/widget/widget-table/widget-table.vue';
import WidgetTableSearchForm from '@pageAssembly/app/web/widget/widget-table/widget-table-search-form.vue';
import WidgetTableButtons from '@pageAssembly/app/web/widget/widget-table/widget-table-buttons.vue';
import { isEmpty } from 'lodash';
import '@installPageWidget';
import '@modules/.webpack.runtime.devjs.js'; // 运行期二开文件
export default {
  props: {
    nodeData: Object
  },
  components: { WidgetDyformSetting },
  inject: ['pageContext', 'locale'],
  data() {
    if (EASY_ENV_IS_BROWSER) {
      this.registerComponentOfWidgetDyformSetting();
    }
    return {
      bizData: null,
      definitionVjson: null,
      dyformComponentLoading: true,
      actions: [
        { id: 'save', name: '保存', validate: false },
        { id: 'printForm', name: '打印表单', validate: false }
      ],
      widgetDyformSetting: null,
      initFormData: null
    };
  },
  computed: {
    isNewFormData() {
      return isEmpty(this.bizData.processNodeInstUuid);
    },
    dataUuid() {
      return (this.bizData.dyFormData && this.bizData.dyFormData.dataUuid) || this.bizData.dataUuid;
    },
    dyformDisplayState() {
      let displayState = this.getQueryString('displayState');
      if (displayState) {
        return displayState;
      }
      return this.bizData && this.bizData.state != '30' ? 'edit' : 'label';
    },
    dyformWidget() {
      return this.$dyformWidget || this.$refs.dyform;
    }
  },
  beforeCreate() {
    import('@dyform/app/web/framework/vue/install').then(m => {
      this.dyformComponentLoading = false;
    });
  },
  created() {
    if (EASY_ENV_IS_BROWSER) {
      this.loadData();
    }
  },
  methods: {
    addSystemPrefix(url) {
      const _this = this;
      if (_this._$SYSTEM_ID && url && !url.startsWith('/sys/')) {
        url = `/sys/${_this._$SYSTEM_ID}/_${url}`;
      }
      return url;
    },
    getSelf() {
      return this;
    },
    registerComponentOfWidgetDyformSetting() {
      const _this = this;
      let components = { WidgetTable, WidgetTableSearchForm, WidgetTableButtons };
      for (let componentName in components) {
        if (!_this.isComponentRegistered(componentName)) {
          Vue.component(componentName, components[componentName]);
        }
      }
    },
    isComponentRegistered(componentName) {
      return Vue.options.components[componentName];
    },
    getQueryString(name, defaultValue) {
      var reg = new RegExp('(^|&)' + name + '=([^&]*)(&|$)');
      var values = window.location.search.substr(1).match(reg);
      if (values != null) {
        return decodeURIComponent(values[2]);
      }
      if (defaultValue != null) {
        return defaultValue;
      }
      return null;
    },
    onDyformMounted() {
      this.loadProcessItemInstances();
    },
    loadData() {
      const _this = this;
      let nodeData = _this.nodeData || {};
      let processNodeInstUuid = nodeData.processNodeInstUuid || _this.getQueryString('processNodeInstUuid', '');
      $axios.post('/proxy/api/biz/process/node/instance/get', { processNodeInstUuid }).then(({ data }) => {
        if (data.data) {
          _this.bizData = data.data;
          _this.definitionVjson = JSON.parse(_this.bizData.dyFormData.definitionVjson);

          if (
            _this.bizData.formConfig &&
            _this.bizData.formConfig &&
            _this.bizData.formConfig.enabledDyformSetting &&
            _this.bizData.formConfig.widgetDyformSetting
          ) {
            let widgetDyformSetting = _this.bizData.formConfig.widgetDyformSetting;
            _this.setDyformTitleIfRequired(widgetDyformSetting, _this.bizData.title);
            let formElementRules = _this.getFormElementRules(widgetDyformSetting);
            widgetDyformSetting.props = {
              isNewFormData: _this.isNewFormData,
              displayState: _this.dyformDisplayState,
              definitionVjson: _this.definitionVjson,
              formUuid: _this.bizData.dyFormData.formUuid,
              dataUuid: _this.dataUuid,
              formDatas: _this.bizData.dyFormData.formDatas,
              formElementRules
            };
            _this.widgetDyformSetting = widgetDyformSetting;
          }

          document.querySelector('title').innerText = _this.bizData.title;
        }
      });
    },
    setDyformTitleIfRequired(widgetDyformSetting, title) {
      let configuration = widgetDyformSetting.configuration || {};
      if (isEmpty(configuration.title)) {
        configuration.title = title;
      }
      if (isEmpty(configuration.editStateTitle)) {
        configuration.editStateTitle = title;
      }
      if (isEmpty(configuration.labelStateTitle)) {
        configuration.labelStateTitle = title;
      }
    },
    getFormElementRules(widgetDyformSetting) {
      const _this = this;
      let formElementRules = {};
      let configuration = widgetDyformSetting.configuration || {};
      let configFormElementRules = configuration.formElementRules || [];
      if (_this.dataUuid) {
        configFormElementRules =
          _this.dyformDisplayState == 'edit' ? configuration.editStateFormElementRules : configuration.labelStateFormElementRules;
      }
      configFormElementRules &&
        configFormElementRules.forEach(rule => {
          formElementRules[rule.id] = rule;
          if (rule.children) {
            let childrenRules = {};
            rule.children.forEach(childRule => {
              childrenRules[childRule.id] = childRule;
            });
            rule.children = childrenRules;
          }
        });
      return formElementRules;
    },
    //  加载业务事项办件实例数据
    loadProcessItemInstances() {
      if (!this.bizData.itemPlaceHolder) {
        return;
      }

      this.getTableWidget().then(widget => {
        let $el = document.querySelector(`[w-code='${this.bizData.itemPlaceHolder}']`);
        if ($el && $el.parentNode) {
          let divEl = document.createElement('div');
          $el.parentNode.appendChild(divEl);
          widget.$mount(divEl);
        }
      });
    },
    getTableWidget() {
      const _this = this;
      if (Vue) {
        Vue.component(WidgetTable.name, WidgetTable);
        Vue.component(WidgetTableSearchForm.name, WidgetTableSearchForm);
      }
      return _this
        .getTableDefinition()
        .then(widgetDefinition => {
          // 创建构造器
          let WidgetTable = Vue.extend({
            template: '<WidgetTable ref="widgetTable" :widget="widget" @beforeLoadData="beforeLoadData"></WidgetTable>', //
            provide() {
              return {
                pageContext: _this.pageContext,
                namespace: _this.namespace,
                vPageState: _this.vPageState,
                $pageJsInstance: _this.$pageJsInstance,
                locale: _this.locale
              };
            },
            inject: {},
            data: function () {
              return { widget: widgetDefinition };
            },
            methods: {
              beforeLoadData() {
                let tableWidget = this.$refs.widgetTable;
                let dataSource = tableWidget.getDataSourceProvider();
                dataSource.addParam('processNodeInstUuid', _this.bizData.processNodeInstUuid);
              }
            }
          });
          return new WidgetTable();
        })
        .catch(res => {
          _this.$message.error('业务事项办件实例数据加载失败！');
        });
    },
    getTableDefinition() {
      let widgetTableId = 'caawwofxACcRnqUQyBTbWnfkChwUfCJi';
      return new Promise((resolve, reject) => {
        $axios
          .post('/json/data/services', {
            serviceName: 'appContextService',
            methodName: 'getAppWidgetDefinitionById',
            args: JSON.stringify([widgetTableId, false])
          })
          .then(({ data: { data = {} } }) => {
            if (data.definitionJson) {
              resolve(JSON.parse(data.definitionJson));
            } else {
              reject(data);
            }
          })
          .catch(res => {
            reject(res);
          });
      });
    },
    onActionClick(event, action) {
      if (action.validate) {
        this.dyformWidget.validateFormData(validate => {
          if (!validate) {
            return;
          }
          this[action.id]();
        });
      } else {
        this[action.id]();
      }
    },
    setDyformWidget($dyformWidget) {
      this.$dyformWidget = $dyformWidget;
    },
    collectFormData() {
      let data = this.dyformWidget.collectFormData();
      if (data.dataUuid && !(data.dyFormData && data.dyFormData.dataUuid)) {
        this.bizData.dataUuid = data.dataUuid;
      }
      return data.dyFormData ? data.dyFormData : data;
    },
    // 重新加载单据
    reload: function (processNodeInstUuid) {
      if (!isEmpty(processNodeInstUuid)) {
        window.location.href = this.addSystemPrefix(`/biz/process/node/instance/view?processNodeInstUuid=${processNodeInstUuid}`);
      } else {
        window.location.reload();
      }
    },
    // 保存
    save() {
      var _this = this;
      _this.$loading('保存中');
      var bizData = _this.bizData;
      bizData.dyFormData = _this.collectFormData();
      $axios
        .post('/proxy/api/biz/process/node/instance/save', bizData)
        .then(({ data }) => {
          if (data.data) {
            _this.$message.success('保存成功！', 2, () => {
              _this.reload(data.data);
            });
          }
          _this.$loading(false);
        })
        .catch(({ response }) => {
          _this.$loading(false);
          _this.$message.error((response.data && response.data.msg) || '保存失败！');
        });
    },
    printForm() {
      this.dyformWidget.print();
    }
  }
};
</script>

<style lang="less" scoped>
.process-node-view-header {
  padding: 0 15px;
  background: var(--w-primary-color);

  .title {
    color: var(--w-white);
    overflow: hidden;
    white-space: nowrap;
    text-overflow: ellipsis;
    line-height: 60px;
    font-size: var(--w-font-size-lg);
    display: inline-block;
    padding-left: var(--w-padding-md);
    font-weight: bold;

    h1 {
      color: #fff;
      font-size: var(--w-font-size-3xl);
      padding-right: var(--w-padding-md);
    }
  }
}
.process-node-view-content {
  background: linear-gradient(to bottom, var(--w-primary-color), var(--w-widget-page-layout-bg-color) 40%);
  padding: var(--w-padding-xs) var(--w-padding-md);
}

.process-node-action-container {
  text-align: right;
}
</style>
