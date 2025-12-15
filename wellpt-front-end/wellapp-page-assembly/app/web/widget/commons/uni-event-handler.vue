<template>
  <a-form-model
    :layout="formLayout != undefined ? formLayout.layout : undefined"
    :colon="formLayout != undefined ? formLayout.colon : false"
    :label-col="formLayout && formLayout.labelCol ? formLayout.labelCol : undefined"
    :wrapper-col="formLayout && formLayout.labelCol ? formLayout.wrapperCol : undefined"
    style="position: relative; /* 设置为相对定位，避免下来菜单在滚动弹窗内无法跟随滚动 */"
  >
    <a-form-model-item label="动作类型" v-if="eventRule.actionTypeSelectable !== false">
      <a-select
        allowClear
        :style="{ width: '100%' }"
        :options="actionTypeOptions"
        v-model="eventModel.actionType"
        @change="onChangeActionType"
        :getPopupContainer="getSelectPopupContainer"
      ></a-select>
    </a-form-model-item>
    <a-form-model-item
      v-if="designer != undefined && eventRule.jsFunction !== false"
      label="脚本代码"
      v-show="eventModel.actionType === 'jsFunction'"
    >
      <JsHookSelect :designer="designer" :widget="widget" v-model="eventModel.jsFunction" />
    </a-form-model-item>

    <div v-show="eventModel.actionType === 'redirectPage'" v-if="eventRule.pageSelect !== false">
      <a-form-model-item label="页面类型" v-if="eventRule.pageTypeSelectable !== false">
        <a-radio-group v-model="eventModel.pageType" :options="pageTypeOptions"></a-radio-group>
      </a-form-model-item>

      <a-form-model-item label="选择页面" v-show="eventModel.pageType === 'page'">
        <a-row type="flex">
          <a-col flex="auto" :style="{ width: eventModel.pageUuid ? 'calc(100% - 80px)' : '100%' }">
            <a-select
              allowClear
              :filter-option="filterSelectOption"
              :showSearch="true"
              :style="{ width: '100%' }"
              :options="pageOptions"
              v-model="eventModel.pageId"
              @change="onSelectPageChange"
              :getPopupContainer="getSelectPopupContainer"
            ></a-select>
          </a-col>
          <a-col flex="70px" v-show="eventModel.pageUuid">
            <a-button type="link" @click.stop="onClickOpenDesignPage">
              <Icon type="pticon iconfont icon-szgy-zonghechaxun"></Icon>
              查看
            </a-button>
          </a-col>
        </a-row>
      </a-form-model-item>
      <a-form-model-item v-show="eventModel.pageType === 'url'">
        <template slot="label">
          url地址
          <slot name="urlHelpSlot"></slot>
        </template>
        <a-input v-model="eventModel.url" />
      </a-form-model-item>
    </div>

    <div v-if="allowEventParams">
      <!-- FIXME 组件事件可以实现定义可支持的事件参数，在此处展示 -->
      <a-divider>事件参数</a-divider>
      <a-button-group class="table-header-operation" size="small" :style="{ marginBottom: '5px' }">
        <a-button @click="addEventParam">
          <Icon type="pticon iconfont icon-ptkj-jiahao"></Icon>
          新增
        </a-button>
        <a-button @click="delEventParam">
          <Icon type="pticon iconfont icon-ptkj-shanchu"></Icon>
          删除
        </a-button>
      </a-button-group>
      <a-table
        size="small"
        :locale="{ emptyText: '暂无数据' }"
        rowKey="id"
        :row-selection="{ selectedRowKeys: selectedEventParamRowKeys, onChange: onSelectedEventParamRowChange }"
        :pagination="false"
        :columns="eventParamColumns"
        :data-source="eventModel.eventParams"
      >
        <template slot="paramValueTitleSlot">
          参数值
          <a-popover placement="bottom" v-if="hasWgtEvtParamsHelpSlot">
            <template slot="content">
              <slot name="eventParamValueHelpSlot"></slot>
            </template>
            <template slot="title">
              <span>帮助文档</span>
            </template>
            <Icon type="pticon iconfont icon-ptkj-xinxiwenxintishi" title="帮助文档"></Icon>
          </a-popover>
        </template>
        <template slot="paramKeySlot" slot-scope="text, record">
          <span style="color: red; line-height: 32px" v-if="record.required">*</span>
          <a-input v-model="record.paramKey" style="width: 100px; float: right" />
        </template>
        <template slot="paramValueSlot" slot-scope="text, record">
          <a-auto-complete :data-source="record.valueScope" v-model.trim="record.paramValue" allow-clear />
        </template>
        <template slot="remarkSlot" slot-scope="text, record">
          <a-input v-model="record.remark" />
        </template>
      </a-table>
    </div>
  </a-form-model>
</template>
<style></style>
<script type="text/babel">
import { generateId } from '@framework/vue/utils/util';
import { orderBy, debounce, upperFirst } from 'lodash';
import WidgetCodeEditor from '@pageWidget/commons/widget-code-editor.vue';
import { filterSelectOption } from '@framework/vue/utils/function';

export default {
  name: 'UniEventHandler',
  inject: ['appId', 'subAppIds'],
  props: {
    eventModel: Object, // 事件数据对象
    designer: Object,
    widget: Object,
    widgetSource: Array,
    formLayout: Object,
    triggerScope: {
      type: Array,
      default: function () {
        return ['click'];
      }
    },

    allowEventParams: {
      type: Boolean,
      default: true
    },

    snippets: {
      // 快速代码片段
      type: Array
    },

    rule: {
      type: Object
    }
  },
  data() {
    let pageOptions = [],
      workflowOptions = [];
    if (this.eventModel.pageId) {
      pageOptions.push({ label: this.eventModel.pageName, value: this.eventModel.pageId });
    }

    let rule = {
      name: {
        hidden: false
      }
    }; // 默认事件可编辑规则
    if (this.rule != undefined) {
      Object.assign(rule, this.rule);
    }

    return {
      eventRule: rule,
      eventParamColumns: [
        { title: '参数', dataIndex: 'param', scopedSlots: { customRender: 'paramKeySlot' } },
        {
          dataIndex: 'paramValue',
          scopedSlots: { customRender: 'paramValueSlot' },
          slots: { title: 'paramValueTitleSlot' }
        },
        { title: '备注', dataIndex: 'remark', scopedSlots: { customRender: 'remarkSlot' } }
      ],
      selectedEventParamRowKeys: [],
      actionOptions: [],
      pageTypeOptions: [
        { label: '页面', value: 'page' },
        { label: 'url', value: 'url' }
      ],
      pageOptions,
      workflowOptions: [],
      eventParams: [],
      widgetEventParamComps: [],
      hasWgtEvtParamsHelpSlot: this.$slots.eventParamValueHelpSlot && this.$slots.eventParamValueHelpSlot.length > 0
    };
  },

  beforeCreate() {},
  components: {
    WidgetCodeEditor,
    VNodes: {
      functional: true,
      render: (h, ctx) => ctx.props.vnodes
    }
  },
  computed: {
    actionTypeOptions() {
      let options = [
        { label: '页面跳转', value: 'redirectPage' },
        { label: '脚本代码', value: 'jsFunction' }
      ];

      return options;
    },
    targetPosition() {
      let options = [
        { label: '新窗口', value: 'newWindow' },
        { label: '当前窗口', value: 'currentWindow' },
        { label: '当前布局', value: 'widgetLayout' }
      ];
      // 在设计器里
      if (this.designer) {
        options.push({ label: '标签页', value: 'widgetTab' });
        options.push({ label: '弹窗', value: 'widgetModal' });
      }
      return options;
    },
    dyformSetOptions() {
      let options = [],
        ids = [];
      if (this.designer.WidgetDyformSettings) {
        for (let i = 0, len = this.designer.WidgetDyformSettings.length; i < len; i++) {
          let w = this.designer.WidgetDyformSettings[i];
          if (this.designer.widgetIdMap[w.id] && !ids.includes(w.id)) {
            options.push({ label: w.title, value: w.id });
            ids.push(w.id);
          }
        }
      }

      // 使用数据模型的表单设置
      if (this.designer.WidgetDataManagerViews) {
        for (let i = 0, len = this.designer.WidgetDataManagerViews.length; i < len; i++) {
          let w = this.designer.WidgetDataManagerViews[i];
          if (w) {
            options.push({
              deleted: false,
              label: w.configuration.WidgetDyformSetting.title,
              value: w.configuration.WidgetDyformSetting.id
            });
          }
        }
      }

      if (this.designer.widgets.length) {
        for (let i = 0, len = this.designer.widgets.length; i < len; i++) {
          let wgt = this.designer.widgets[i];
          if (wgt.wtype == 'WidgetDyformSetting' && !ids.includes(wgt.id)) {
            options.push({ label: wgt.title, value: wgt.id });
            ids.push(wgt.id);
          }
        }
      }

      return options;
    },
    vShowContainerSelect() {
      return (
        ['redirectPage', 'openModalDialog', 'workflow', 'dataManager'].includes(this.eventModel.actionType) &&
        this.eventModel.targetPosition != undefined &&
        this.eventModel.targetPosition != 'newWindow' &&
        this.eventModel.targetPosition != 'currentWindow'
      );
    },
    vContainerLabel() {
      let targetPosition = this.eventModel.targetPosition;
      return { widgetLayout: '当前布局', widgetModal: '弹窗', widgetTab: '标签页' }[targetPosition];
    },
    vContainerOptions() {
      let targetPosition = this.eventModel.targetPosition;
      if (targetPosition == undefined) {
        return [];
      }
      let options = [];
      if (this.designer) {
        if (targetPosition === 'widgetLayout') {
          if (!this.designer.WidgetLayouts) {
            return [];
          }
          for (let i = 0, len = this.designer.WidgetLayouts.length; i < len; i++) {
            let w = this.designer.WidgetLayouts[i];
            options.push({ label: w.configuration.name || w.title, value: w.configuration.content.id });
          }
        } else if (targetPosition === 'widgetModal') {
          if (this.designer.WidgetModals) {
            for (let i = 0, len = this.designer.WidgetModals.length; i < len; i++) {
              let w = this.designer.WidgetModals[i];
              options.push({ label: w.title, value: w.id });
            }
          }
          if (this.designer.WidgetDataManagerViews) {
            for (let i = 0, len = this.designer.WidgetDataManagerViews.length; i < len; i++) {
              let w = this.designer.WidgetDataManagerViews[i].configuration.WidgetModal;
              options.push({ label: w.title, value: w.id });
            }
          }
        } else if (targetPosition === 'widgetTab') {
          options.push({ label: '父级标签窗口', value: '-1' });
          if (!this.designer.WidgetTabs) {
            return options;
          }
          for (let i = 0, len = this.designer.WidgetTabs.length; i < len; i++) {
            let w = this.designer.WidgetTabs[i];
            options.push({ label: w.configuration.name || w.title, value: w.id });
          }
        }
      } else if (this.widgetSource != undefined && this.widgetSource.length > 0) {
        let options = [];
        for (let w of this.widgetSource) {
          if (w.wtype.toLowerCase() == targetPosition.toLowerCase()) {
            options.push({ label: w.configuration.name || w.title, value: w.configuration.content.id });
          }
        }
        return options;
      }
      return options;
    },
    evtWidget() {
      if (this.eventModel.eventWid && this.eventModel.eventId && this.eventModel.actionType == 'widgetEvent') {
        return this.designer.widgetIdMap[this.eventModel.eventWid];
      }
      return undefined;
    },
    evtWidgetParamCompName() {
      // 组件事件方法对应的配置组件名： 组件类型+事件ID+EventConfiguration，例如, WidgetTableRefetchEventConfiguration
      if (this.eventModel.eventWid && this.eventModel.eventId && this.eventModel.actionType == 'widgetEvent') {
        let wtype = this.designer.widgetIdMap[this.eventModel.eventWid].wtype;
        let compName = `${wtype}${upperFirst(this.eventModel.eventId)}EventConfiguration`;
        if (this.widgetEventParamComps.includes(compName)) {
          return compName;
        }
      }
      return undefined;
    },

    vWidgetOptions() {
      let options = [];
      if (this.designer) {
        for (let wid in this.designer.widgetIdMap) {
          let w = this.designer.widgetIdMap[wid];
          let defaultEvents = this.designer.widgetDefaultEvents[w.id];
          if (
            (w.configuration.defineEvents == undefined || w.configuration.defineEvents.length == 0) &&
            (defaultEvents == undefined || defaultEvents.length == 0)
          ) {
            continue;
          }

          options.push({ label: w.configuration.isDatabaseField ? w.configuration.name : w.title, value: w.id });
        }
      }
      return options;
    },
    vWidgetCustomEvents() {
      let options = [];
      if (this.designer) {
        let w = this.designer.widgetIdMap[this.eventModel.eventWid];
        if (w) {
          if (w.configuration.defineEvents) {
            for (let i = 0, len = w.configuration.defineEvents.length; i < len; i++) {
              if (!['created', 'beforeMount', 'mounted'].includes(w.configuration.defineEvents[i].id)) {
                options.push({ label: w.configuration.defineEvents[i].title, value: w.configuration.defineEvents[i].id });
              }
            }
          }
          let defaultEvents = this.designer.widgetDefaultEvents[w.id];
          if (defaultEvents) {
            for (let i = 0, len = defaultEvents.length; i < len; i++) {
              options.push({ label: defaultEvents[i].title, value: defaultEvents[i].id });
            }
          }
        }
      }
      return options;
    }
  },
  created() {
    if (this.eventModel.trigger === undefined) {
      this.$set(this.eventModel, 'trigger', 'click');
    }
    if (this.eventModel.dmsId === undefined) {
      this.$set(this.eventModel, 'dmsId', undefined);
    }
  },
  methods: {
    filterSelectOption,
    onChangeTargetPosition() {
      this.$set(this.eventModel, 'containerWid', undefined);
    },
    getSelectPopupContainer() {
      // 下拉框渲染到当前dom域内且AForm设置为相对定位，避免事件处理渲染在滚动的弹窗内时候菜单不跟随滚动问题
      return this.$el;
    },
    deleteDyformSettingWgt(id) {
      if (id == this.eventModel.dmsId) {
        this.eventModel.dmsId = undefined;
      }
      this.designer.emitEvent(`${id}:delete`);
    },
    onClickOpenDesignPage() {
      if (this.eventModel.pageUuid) {
        window.open('/uni-page-designer/index?uuid=' + this.eventModel.pageUuid, '_blank');
      }
    },

    onChangeWidgetEvent() {
      this.getWidgetEventParams();
    },
    getWidgetEventParams() {
      if (this.eventModel.eventWid && this.eventModel.eventId && this.designer) {
        let w = this.designer.widgetIdMap[this.eventModel.eventWid];
        let modelParams = this.eventModel.eventParams,
          eventParams = [],
          params = {};

        if (w) {
          let defaultEvents = this.designer.widgetDefaultEvents[w.id];
          if (defaultEvents) {
            for (let i = 0, len = defaultEvents.length; i < len; i++) {
              if (defaultEvents[i].id === this.eventModel.eventId && defaultEvents[i].eventParams != undefined)
                for (let e = 0, elen = defaultEvents[i].eventParams.length; e < elen; e++) {
                  let p = { id: generateId(), ...defaultEvents[i].eventParams[e] };
                  eventParams.push(p);
                  params[p.paramKey] = p;
                }
            }
          }
          if (w.configuration.defineEvents) {
            for (let i = 0, len = w.configuration.defineEvents.length; i < len; i++) {
              if (
                w.configuration.defineEvents[i].id === this.eventModel.eventId &&
                w.configuration.defineEvents[i].eventParams != undefined
              )
                for (let e = 0, elen = w.configuration.defineEvents[i].eventParams.length; e < elen; e++) {
                  let p = { id: generateId(), ...w.configuration.defineEvents[i].eventParams[e] };
                  eventParams.push(p);
                  params[p.paramKey] = p;
                }
            }
          }
        }

        if (modelParams && modelParams.length) {
          for (let i = 0, len = modelParams.length; i < len; i++) {
            let p = modelParams[i];
            if (params[p.paramKey]) {
              params[p.paramKey].paramValue = p.paramValue;
            } else {
              eventParams.push(p);
            }
          }
        }
        this.eventModel.eventParams = eventParams;
      }
    },
    onChangeActionType(v, option) {},
    onClickDyformSetting() {
      if (this.designer && this.designer.widgetIdMap.hasOwnProperty(this.eventModel.dmsId)) {
        this.designer.setSelected(this.designer.widgetIdMap[this.eventModel.dmsId]);
      }
    },
    newDyformSettingWidget() {
      let wgt = {
        id: generateId(),
        wtype: 'WidgetDyformSetting',
        forceRender: false,
        title: '数据管理设置'
      };

      this.designer.widgets.push(wgt);
      this.designer.setSelected(wgt);
      this.eventModel.dmsId = wgt.id;
    },

    onSelectWorkflowChange(v, opt) {
      this.eventModel.workflowName = null;
      if (v) {
        this.eventModel.workflowName = opt.componentOptions.children[0].text.trim();
      }
    },
    onSelectPageChange(v, node) {
      this.eventModel.pageName = null;
      if (v) {
        let uuid = node.data.props.uuid;
        this.eventModel.pageUuid = uuid;
        this.eventModel.pageName = node.componentOptions.children[0].text.trim(); // 获取页面定义json
        this.getPageDefinitionJson(uuid).then(data => {
          let json = JSON.parse(data.definitionJson);
          let pageParams = json.pageParams;
          if (pageParams) {
            this.eventModel.eventParams = [];
            for (let p of pageParams) {
              let paramValue = undefined,
                valueOptions = [];
              if (p.valueScope) {
                for (let v of p.valueScope) {
                  valueOptions.push(v.value);
                  if (v.default) {
                    paramValue = v.value;
                  }
                }
              }
              this.eventModel.eventParams.push({
                paramKey: p.code,
                paramValue,
                required: p.required,
                valueScope: valueOptions
              });
            }
          }
        });
      } else {
        this.eventModel.pageUuid = undefined;
      }
    },
    getPageDefinitionJson(uuid) {
      return new Promise((resolve, reject) => {
        $axios
          .post('/json/data/services', {
            serviceName: 'appPageDefinitionService',
            methodName: 'get',
            args: JSON.stringify([uuid])
          })
          .then(({ data }) => {
            if (data.code == 0 && data.data) {
              resolve(data.data);
            } else {
              reject();
            }
          })
          .catch(error => {
            reject();
          });
      });
    },
    onSelectedEventParamRowChange(selectedRowKeys, selectedRows) {
      this.selectedEventParamRowKeys = selectedRowKeys;
    },
    fetchPageOptions() {
      let appIds = [].concat(this.appId || []).concat(this.subAppIds || []);
      if (appIds.length) {
        $axios
          .post(`/proxy/api/webapp/page/definition/queryLatestPageDefinitionByAppIds`, appIds)
          .then(({ data }) => {
            this.pageOptions.splice(0, this.pageOptions.length);
            if (data.data) {
              for (let d of data.data) {
                if (d.wtype == 'vUniPage') {
                  this.pageOptions.push({
                    label: `${d.name} v${d.version}`,
                    value: d.id,
                    wtype: d.wtype,
                    uuid: d.uuid
                  });
                }
              }
            }
          })
          .catch(error => {});
      }
    },

    fetchWorkflowOptions() {
      let _this = this;
      $axios
        .post('/json/data/services', {
          serviceName: 'flowDefineService',
          methodName: 'query',
          args: JSON.stringify([{}])
        })
        .then(({ data }) => {
          let options = [];
          if (data.code == 0 && data.data) {
            //FIXME: 获取模块下工作流

            let id = [];
            for (let i = 0, len = data.data.length; i < len; i++) {
              if (!id.includes(data.data[i].id)) {
                options.push({
                  label: data.data[i].name,
                  value: data.data[i].id
                });
                id.push(data.data[i].id);
              }
            }
          }
          _this.workflowOptions = options;
        });
    },

    addEventParam() {
      if (!this.eventModel.hasOwnProperty('eventParams')) {
        this.$set(this.eventModel, 'eventParams', []);
      }
      this.eventModel.eventParams.push({
        id: generateId()
      });
    },
    delEventParam() {
      for (let i = 0, len = this.selectedEventParamRowKeys.length; i < len; i++) {
        for (let j = 0, jlen = this.eventModel.eventParams.length; j < jlen; j++) {
          if (this.eventModel.eventParams[j].id == this.selectedEventParamRowKeys[i]) {
            this.eventModel.eventParams.splice(j, 1);
            break;
          }
        }
      }
    },
    onChangeActionSelect() {
      this.eventModel.eventParams.splice(0, this.eventModel.eventParams.length);
      if (
        this.eventModel.actionType == 'dataManager' &&
        this.eventModel.action &&
        this.dataManagerActionEventParamsMap[this.eventModel.action]
      ) {
        this.eventModel.eventParams.push(...this.dataManagerActionEventParamsMap[this.eventModel.action]);
      }
    }
  },
  mounted() {
    this.fetchPageOptions();
    this.getWidgetEventParams();

    let components = window.Vue.options.components;
    for (let key in components) {
      if (key.endsWith('EventConfiguration') && key.startsWith('Widget')) {
        this.widgetEventParamComps.push(key);
      }
    }
  },
  watch: {}
};
</script>
