<template>
  <a-form-model
    style="position: relative; /* 设置为相对定位，避免下来菜单在滚动弹窗内无法跟随滚动 */"
    :labelAlign="labelAlign"
    :label-col="labelCol"
    :wrapper-col="wrapperCol"
    :colon="colon"
  >
    <a-form-model-item label="事件名称" v-if="eventRule.name !== false">
      <a-input v-model="eventModel.name" />
    </a-form-model-item>
    <a-form-model-item label="触发类型" v-if="eventRule.triggerSelectable !== false">
      <a-select
        :style="{ width: '100%' }"
        :options="triggerOptions"
        v-model="eventModel.trigger"
        :getPopupContainer="getSelectPopupContainer"
      ></a-select>
    </a-form-model-item>
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
      :label-col="{ span: 4 }"
      :wrapper-col="{ span: 20 }"
      v-show="eventModel.actionType === 'jsFunction'"
    >
      <JsHookSelect v-model="eventModel.jsFunction" />
    </a-form-model-item>

    <a-form-model-item v-show="eventModel.actionType === 'dataManager'" label="动作" v-if="eventRule.actionSelect !== false">
      <a-select
        showSearch
        :style="{ width: '100%' }"
        :options="actionOptions"
        v-model="eventModel.action"
        :getPopupContainer="getSelectPopupContainer"
      ></a-select>
    </a-form-model-item>
    <a-form-model-item
      v-if="designer != undefined && eventRule.dyformSetable != false"
      v-show="eventModel.actionType === 'dataManager'"
      label="表单设置"
    >
      <div :style="{ display: 'flex', alignItems: 'center' }">
        <a-select
          v-model="eventModel.dmsId"
          style="width: 100%"
          allowClear
          notFoundContent="暂无数据"
          :getPopupContainer="getSelectPopupContainer"
        >
          <div slot="dropdownRender" slot-scope="menu">
            <v-nodes :vnodes="menu" />
            <a-divider style="margin: 4px 0" />
            <div style="padding: 4px 8px; cursor: pointer" @mousedown="e => e.preventDefault()" @click="newDyformSettingWidget">
              <a-icon type="plus" />
              新增并设置
            </div>
          </div>
          <a-select-option v-for="opt in dyformSetOptions" :key="opt.value" :value="opt.value">
            {{ opt.label }}
            <a-button
              type="link"
              size="small"
              style="float: right"
              icon="delete"
              class="select-option-operation"
              @click.native.stop="deleteDyformSettingWgt(opt.value)"
            ></a-button>
          </a-select-option>
        </a-select>
        <a-button icon="setting" @click="onClickDyformSetting" v-show="eventModel.dmsId != null" :style="{ marginLeft: '10px' }" />
      </div>
    </a-form-model-item>

    <a-form-model-item
      v-show="eventModel.action === 'BasicWidgetDyformDevelopment.open'"
      label="打开数据状态"
      v-if="eventRule.displayState"
    >
      <a-radio-group v-model="eventModel.displayState" button-style="solid">
        <a-radio-button value="edit">编辑</a-radio-button>
        <a-radio-button value="label">查询</a-radio-button>
      </a-radio-group>
    </a-form-model-item>
    <div v-show="eventModel.actionType === 'redirectPage'" v-if="eventRule.pageSelect !== false">
      <a-form-model-item label="页面类型" v-if="pageTypeSelectable">
        <a-radio-group v-model="eventModel.pageType" :options="pageTypeOptions"></a-radio-group>
      </a-form-model-item>

      <a-form-model-item label="选择页面" v-show="eventModel.pageType === 'page'">
        <a-row type="flex">
          <a-col flex="auto">
            <a-select
              allowClear
              :filter-option="filterOption"
              :showSearch="true"
              :style="{ width: '100%' }"
              :options="pageOptions"
              v-model="eventModel.pageId"
              @change="onSelectPageChange"
              :getPopupContainer="getSelectPopupContainer"
            ></a-select>
          </a-col>
          <a-col flex="70px" v-show="eventModel.pageUuid">
            <a-button icon="link" type="link" @click.stop="onClickOpenDesignPage">查看</a-button>
          </a-col>
        </a-row>
      </a-form-model-item>
      <a-form-model-item label="url地址" v-show="eventModel.pageType === 'url'">
        <a-input v-model="eventModel.url" />
      </a-form-model-item>
    </div>

    <div v-show="eventModel.actionType === 'workflow'" v-if="eventRule.workflowSelect !== false">
      <a-form-model-item label="选择流程">
        <a-select
          showSearch
          :filter-option="filterOption"
          :style="{ width: '100%' }"
          :options="workflowOptions"
          v-model="eventModel.workflowId"
          @change="onSelectWorkflowChange"
          :getPopupContainer="getSelectPopupContainer"
        ></a-select>
      </a-form-model-item>
    </div>
    <a-form-model-item label="自定义" v-show="eventModel.actionType === 'jsFunction'" v-if="eventRule.jsFunction !== false">
      <WidgetCodeEditor @save="value => (eventModel.customScript = value)" :value="eventModel.customScript" :snippets="snippets">
        <a-button icon="code">编写代码</a-button>
      </WidgetCodeEditor>
    </a-form-model-item>
    <div v-show="eventModel.actionType === 'dataManager'"></div>
    <div v-show="eventModel.actionType === 'widgetEvent'" v-if="eventRule.widgetEvent !== false">
      <a-form-model-item label="选择组件">
        <a-select
          :style="{ width: '100%' }"
          :options="vWidgetOptions"
          v-model="eventModel.eventWid"
          @change="onChangeWidgetEvent"
          show-search
          :filter-option="filterOption"
          :getPopupContainer="getSelectPopupContainer"
        ></a-select>
      </a-form-model-item>
      <a-form-model-item label="选择组件事件">
        <a-select
          :style="{ width: '100%' }"
          :options="vWidgetCustomEvents"
          v-model="eventModel.eventId"
          @change="onChangeWidgetEvent"
          :getPopupContainer="getSelectPopupContainer"
        ></a-select>
      </a-form-model-item>
    </div>
    <a-form-model-item
      label="目标位置"
      v-if="
        positionSelectable &&
        ['redirectPage', 'workflow', 'dataManager'].includes(eventModel.actionType) &&
        eventRule.targetPosition !== false
      "
    >
      <a-select
        :style="{ width: '100%' }"
        :options="targetPosition"
        v-model="eventModel.targetPosition"
        :getPopupContainer="getSelectPopupContainer"
      ></a-select>
    </a-form-model-item>
    <a-form-model-item
      v-show="vShowContainerSelect"
      :label="vContainerLabel"
      v-if="positionSelectable && eventRule.targetPosition !== false"
    >
      <a-select
        :style="{ width: '100%' }"
        :options="vContainerOptions"
        v-model="eventModel.containerWid"
        :getPopupContainer="getSelectPopupContainer"
      ></a-select>
    </a-form-model-item>
    <!-- 用于显示事件内容的标题 -->
    <a-form-model-item label="目标标题" v-if="eventModel.targetPosition && eventModel.actionType == 'redirectPage'">
      <a-input v-model="eventModel.title" />
    </a-form-model-item>
    <div v-if="allowEventParams && eventRule.widgetEvent !== false">
      <!-- FIXME 组件事件可以实现定义可支持的事件参数，在此处展示 -->
      <a-divider>事件参数</a-divider>
      <a-button-group class="table-header-operation" size="small" :style="{ marginBottom: '5px' }">
        <a-button @click="addEventParam" icon="plus">新增</a-button>
        <a-button @click="delEventParam" icon="delete">删除</a-button>
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
        <template slot="paramKeySlot" slot-scope="text, record">
          <span style="color: red" v-if="record.required">*</span>
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
import { orderBy, debounce } from 'lodash';
import WidgetCodeEditor from '@pageWidget/commons/widget-code-editor.vue';

export default {
  name: 'EventHandlerAdmin',
  inject: ['appId'],
  props: {
    eventModel: Object, // 事件数据对象
    designer: Object,
    widget: Object,
    widgetSource: Array,
    triggerSelectable: {
      type: Boolean,
      default: true
    },
    triggerScope: {
      type: Array,
      default: function () {
        return ['click'];
      }
    },
    actionTypeSelectable: {
      type: Boolean,
      default: true
    },
    pageTypeSelectable: {
      type: Boolean,
      default: true
    },

    positionSelectable: {
      type: Boolean,
      default: true
    },
    allowEventParams: {
      type: Boolean,
      default: true
    },
    eventNamed: {
      type: Boolean,
      default: true
    },
    snippets: {
      // 快速代码片段
      type: Array
    },

    rule: {
      type: Object
    },
    labelAlign: {
      type: String,
      default: 'right'
    },
    labelCol: {
      type: Object,
      default() {
        return { span: 4 };
      }
    },
    wrapperCol: {
      type: Object,
      default() {
        return { span: 19 };
      }
    },
    colon: {
      type: Boolean,
      default: true
    }
  },
  data() {
    let pageOptions = [],
      workflowOptions = [];
    if (this.eventModel.pageId) {
      pageOptions.push({ label: this.eventModel.pageName, value: this.eventModel.pageId });
    }
    if (this.eventModel.workflowId) {
      workflowOptions.push({ label: this.eventModel.workflowName, value: this.eventModel.workflowId });
    }
    let rule = {
      name: {
        hidden: false
      }
    }; // 默认事件可编辑规则
    if (this.rule != undefined) {
      Object.assign(rule, this.rule);
    }

    let triggerOptions = [
      { label: '单击', value: 'click' },
      { label: '鼠标移入', value: 'mouseenter' },
      { label: '鼠标移出', value: 'mouseleave' },
      { label: '双击', value: 'dblclick' }
    ];
    for (let i = 0; i < triggerOptions.length; i++) {
      if (!this.triggerScope.includes(triggerOptions[i].value)) {
        triggerOptions.splice(i--, 1);
      }
    }

    return {
      eventRule: rule,
      eventParamColumns: [
        { title: '参数', dataIndex: 'param', width: 125, scopedSlots: { customRender: 'paramKeySlot' } },
        { title: '参数值', dataIndex: 'paramValue', scopedSlots: { customRender: 'paramValueSlot' } },
        { title: '备注', dataIndex: 'remark', width: 200, scopedSlots: { customRender: 'remarkSlot' } }
      ],
      selectedEventParamRowKeys: [],
      actionOptions: [],
      pageTypeOptions: [
        { label: '页面', value: 'page' },
        { label: 'url', value: 'url' }
      ],
      triggerOptions,
      pageOptions,
      workflowOptions: [],
      eventParams: []
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
    dyformSetOptions() {
      let options = [];
      if (this.designer.WidgetDyformSettings) {
        for (let i = 0, len = this.designer.WidgetDyformSettings.length; i < len; i++) {
          let w = this.designer.WidgetDyformSettings[i];
          if (this.designer.widgetIdMap[w.id]) {
            options.push({ label: w.title, value: w.id });
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
    vWidgetOptions() {
      let options = [];
      if (this.designer) {
        for (let wid in this.designer.widgetIdMap) {
          let w = this.designer.widgetIdMap[wid];
          options.push({ label: w.title, value: w.id });
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
    },
    actionTypeOptions() {
      let options = [
        { label: '页面跳转', value: 'redirectPage' },
        { label: '工作流', value: 'workflow' },
        { label: '脚本代码', value: 'jsFunction' }
      ];
      if (this.designer) {
        options.push({ label: '触发组件事件', value: 'widgetEvent' });
        options.push({ label: '数据管理', value: 'dataManager' });
      }
      return options;
    },
    targetPosition() {
      let options = [
        { label: '新窗口', value: 'newWindow' },
        { label: '当前窗口', value: 'currentWindow' }
      ];
      if (this.designer) {
        options.push({ label: '当前布局', value: 'widgetLayout' });
        options.push({ label: '标签页', value: 'widgetTab' });
        options.push({ label: '弹窗', value: 'widgetModal' });
      }
      return options;
    },
    getAppId() {
      return this.appId();
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
        window.open('/page-designer/index?uuid=' + this.eventModel.pageUuid, '_blank');
      }
    },
    filterOption(input, option) {
      return (
        option.componentOptions.propsData.value.toLowerCase().indexOf(input.toLowerCase()) >= 0 ||
        option.componentOptions.children[0].text.toLowerCase().indexOf(input.toLowerCase()) >= 0
      );
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
    onChangeActionType(v, option) {
      this.eventModel.actionTypeName = v ? option.componentOptions.children[0].text.trim() : undefined;
      if (v === 'openModalDialog') {
        this.$set(this.eventModel, 'targetPosition', 'widgetModal');
      }
    },
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
      let _this = this;
      this.designer.setSelected(wgt);
      this.eventModel.dmsId = wgt.id;
    },
    filterOption(input, option) {
      return (
        option.componentOptions.propsData.value.toLowerCase().indexOf(input.toLowerCase()) >= 0 ||
        option.componentOptions.children[0].text.toLowerCase().indexOf(input.toLowerCase()) >= 0
      );
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
      let appIds = [].concat(this.getAppId || []); //.concat(this.subAppIds || []);
      let promise = null;
      if (appIds.length) {
        promise = $axios.post(`/proxy/api/webapp/page/definition/queryLatestPageDefinitionByAppIds`, appIds);
      } else {
        promise = $axios.post(`/proxy/api/webapp/page/definition/queryLatestPageDefinitionByCurrentSystem`);
      }
      promise
        .then(({ data }) => {
          this.pageOptions.splice(0, this.pageOptions.length);
          if (data.data) {
            for (let d of data.data) {
              if (d.wtype == 'vPage') {
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
    fetchActionOptions() {
      let _d = this.__developScript && this.__developScript.BasicWidgetDyformDevelopment;
      if (_d) {
        let meta = _d.default.prototype.META;
        if (meta && meta.hook) {
          for (let h in meta.hook) {
            this.actionOptions.push({ label: meta.hook[h], value: `BasicWidgetDyformDevelopment.${h}` });
          }
        }
      }
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
    }
  },
  mounted() {
    this.fetchPageOptions();
    this.fetchWorkflowOptions();

    this.fetchActionOptions();
    this.getWidgetEventParams();
  },
  watch: {}
};
</script>
