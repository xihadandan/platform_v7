<template>
  <a-form-model
    :layout="formLayout != undefined ? formLayout.layout : undefined"
    :colon="formLayout != undefined ? formLayout.colon : false"
    :label-col="formLayout && formLayout.labelCol ? formLayout.labelCol : undefined"
    :wrapper-col="formLayout && formLayout.labelCol ? formLayout.wrapperCol : undefined"
    style="position: relative; /* 设置为相对定位，避免下来菜单在滚动弹窗内无法跟随滚动 */"
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
      v-show="eventModel.actionType === 'jsFunction'"
    >
      <JsHookSelect :designer="designer" :widget="widget" v-model="eventModel.jsFunction" @change="onChangeJsFunction" />
      <!-- <a-select showSearch allowClear :style="{ width: '100%' }" :options="defaultJsHooks" v-model="eventModel.jsFunction"></a-select> -->
    </a-form-model-item>

    <a-form-model-item v-show="eventModel.actionType === 'dataManager'" label="动作" v-if="eventRule.actionSelect !== false">
      <a-select
        showSearch
        :style="{ width: '100%' }"
        :options="actionOptions"
        v-model="eventModel.action"
        :getPopupContainer="getSelectPopupContainer"
        @change="onChangeActionSelect"
      ></a-select>
    </a-form-model-item>
    <a-form-model-item
      v-if="designer != undefined && eventRule.dyformSetable != false"
      v-show="eventModel.actionType === 'dataManager' && isDyformAction"
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
              <Icon type="pticon iconfont icon-ptkj-jiahao"></Icon>
              新增并设置
            </div>
          </div>
          <a-select-option v-for="(opt, i) in dyformSetOptions" :key="opt.value" :value="opt.value">
            {{ opt.label }}
            <a-button
              type="link"
              size="small"
              style="float: right"
              class="select-option-operation"
              v-if="opt.deleted !== false"
              @click.native.stop="deleteDyformSettingWgt(opt.value)"
            >
              <Icon type="pticon iconfont icon-ptkj-shanchu"></Icon>
            </a-button>
          </a-select-option>
        </a-select>
        <a-button
          @click="onClickDyformSetting"
          v-show="eventModel.dmsId != null && designer.widgetIdMap[eventModel.dmsId] != undefined"
          :style="{ marginLeft: '10px' }"
        >
          <Icon type="pticon iconfont icon-ptkj-shezhi"></Icon>
        </a-button>
      </div>
    </a-form-model-item>
    <a-form-model-item
      v-if="designer != undefined && eventRule.dyformSetable != false && widget.wtype !== 'WidgetTable'"
      v-show="eventModel.actionType === 'dataManager'"
      label="关联数据表格"
    >
      <a-select
        :style="{ width: '100%' }"
        :options="vWidgetOptions"
        v-model="eventModel.dmsTableId"
        show-search
        allow-clear
        :filter-option="filterSelectOption"
        :getPopupContainer="getSelectPopupContainer"
      ></a-select>
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
              :options="vPageOptions"
              v-model="eventModel.pageId"
              @change="(v, node) => onSelectPageChange(v, node)"
              :getPopupContainer="getSelectPopupContainer"
            ></a-select>
          </a-col>
          <a-col flex="70px">
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
      <slot name="pageTypeSlot"></slot>
    </div>

    <div v-show="eventModel.actionType === 'workflow'" v-if="eventRule.workflowSelect !== false">
      <a-form-model-item label="选择流程">
        <a-select
          allowClear
          showSearch
          :filter-option="filterSelectOption"
          :style="{ width: '100%' }"
          :options="workflowOptions"
          v-model="eventModel.workflowId"
          @change="onSelectWorkflowChange"
          :getPopupContainer="getSelectPopupContainer"
        ></a-select>
      </a-form-model-item>
      <a-form-model-item>
        <template slot="label">
          <a-space>
            送审批
            <a-popover>
              <template slot="content">
                开启送审批需在事件参数中配置送审批的内容参数，例如
                <br />
                linkTitle: 送审批的文档链接标题，多个以分号隔开并与linkUrl匹配
                <br />
                linkUrl: 送审批的文档链接URL，多个以分号隔开并与linkTitle匹配
              </template>
              <a-icon type="info-circle" />
            </a-popover>
          </a-space>
        </template>
        <a-switch v-model="eventModel.sendToApprove"></a-switch>
      </a-form-model-item>
    </div>
    <a-form-model-item label="自定义" v-show="eventModel.actionType === 'jsFunction'" v-if="eventRule.jsFunction !== false">
      <WidgetCodeEditor @save="value => (eventModel.customScript = value)" :value="eventModel.customScript" :snippets="snippets">
        <a-button icon="code" type="link" size="small">编写代码</a-button>
      </WidgetCodeEditor>
    </a-form-model-item>
    <div v-show="eventModel.actionType === 'dataManager'"></div>
    <div v-show="eventModel.actionType === 'widgetEvent'" v-if="eventRule.widgetEvent !== false">
      <a-form-model-item label="选择组件">
        <a-select
          :style="{ width: '100%' }"
          :options="vEvtWidgetOptions"
          v-model="eventModel.eventWid"
          @change="onChangeWidgetEventW"
          show-search
          :filter-option="filterSelectOption"
          :getPopupContainer="getSelectPopupContainer"
        ></a-select>
      </a-form-model-item>
      <a-form-model-item label="选择组件事件">
        <a-select
          :style="{ width: evtWidgetParamCompName != undefined ? 'calc(100% - 65px)' : '100%' }"
          :options="vWidgetCustomEvents"
          v-model="eventModel.eventId"
          @change="onChangeWidgetEvent"
          :getPopupContainer="getSelectPopupContainer"
        ></a-select>
        <WidgetDesignModal
          v-if="evtWidgetParamCompName != undefined"
          :key="eventModel.eventWid + eventModel.eventId + eventModel.actionType"
        >
          <template slot="title">
            事件配置
            <a-popover placement="bottom" v-if="hasWgtEvtParamsHelpSlot">
              <template slot="content">
                <slot name="eventParamValueHelpSlot"></slot>
              </template>
              <template slot="title">
                <span>帮助文档</span>
              </template>
              <Icon type="pticon iconfont icon-ptkj-xinxiwenxintishi"></Icon>
            </a-popover>
          </template>
          <template slot="content">
            <div>
              <component
                :is="evtWidgetParamCompName"
                :widget="evtWidget"
                :designer="designer"
                :eventHandler="eventModel"
                :fromWidget="widget"
              ></component>
            </div>
          </template>
          <a-button type="link" size="small">
            <Icon type="pticon iconfont icon-ptkj-shezhi"></Icon>
            配置
          </a-button>
        </WidgetDesignModal>
      </a-form-model-item>
    </div>
    <a-form-model-item label="JS模块" v-show="eventModel.actionType === 'customJsModule'">
      <custom-js-module v-model="eventModel.customJsModule" />
    </a-form-model-item>
    <a-form-model-item
      label="目标位置"
      v-if="
        eventRule.positionSelectable !== false &&
        ['redirectPage', 'workflow', 'dataManager'].includes(eventModel.actionType) &&
        eventRule.targetPosition !== false
      "
    >
      <a-select
        :style="{ width: '100%' }"
        :options="targetPosition"
        v-model="eventModel.targetPosition"
        :getPopupContainer="getSelectPopupContainer"
        @change="onChangeTargetPosition"
      ></a-select>
    </a-form-model-item>
    <a-form-model-item v-show="vShowContainerSelect" v-if="eventRule.positionSelectable !== false && eventRule.targetPosition !== false">
      <template slot="label">
        {{ vContainerLabel }}
        <a-popover placement="topLeft" v-if="vContainerLabel == '当前布局'">
          <template slot="content">
            <div style="width: 400px">
              未选择布局的情况下, 将通过当前内容的父级布局进行内容渲染 , 当无任何父级布局时, 将以新窗口方式进行内容渲染。
            </div>
          </template>
          <a-button type="link" size="small"><Icon type="pticon iconfont icon-ptkj-xinxiwenxintishi"></Icon></a-button>
        </a-popover>
      </template>
      <a-select
        :style="{ width: '100%' }"
        :options="vContainerOptions"
        v-model="eventModel.containerWid"
        :getPopupContainer="getSelectPopupContainer"
        allow-clear
      ></a-select>
    </a-form-model-item>
    <a-form-model-item
      style="display: flex"
      :label-col="{ style: { width: '200px' } }"
      :wrapper-col="{ style: { width: 'calc(100% - 200px)' } }"
      v-if="
        eventModel.targetPosition == 'widgetLayout' &&
        eventModel.actionType == 'redirectPage' &&
        eventModel.pageType == 'page' &&
        eventRule.positionSelectable !== false &&
        eventRule.targetPosition !== false &&
        eventRule.locateNavigation !== false
      "
    >
      <template slot="label">
        当布局无标签页时打开方式
        <a-popover placement="topLeft">
          <template slot="content">
            <div style="width: 400px">
              当布局未开启动态标签页打开页面时候 , 可以通过设置该方式来打开页面 , 页面将不会覆盖布局内容区原有页面
            </div>
          </template>
          <a-button type="link" size="small"><Icon type="pticon iconfont icon-ptkj-xinxiwenxintishi"></Icon></a-button>
        </a-popover>
      </template>
      <a-select
        :style="{ width: '100%' }"
        :options="[{ label: '浮层', value: 'drawer' }]"
        v-model="eventModel.layoutNoTabsOpenType"
        allow-clear
      ></a-select>
    </a-form-model-item>

    <a-form-model-item
      style="display: flex"
      :label-col="{ style: { width: '200px' } }"
      :wrapper-col="{ style: { width: 'calc(100% - 200px)' } }"
      v-if="
        eventModel.targetPosition == 'widgetLayout' &&
        eventRule.positionSelectable !== false &&
        eventRule.targetPosition !== false &&
        eventRule.locateNavigation !== false
      "
    >
      <template slot="label">
        页面始终新开标签打开
        <a-popover placement="topLeft">
          <template slot="content">
            <div style="width: 400px">当布局开启动态标签页打开页面时候 , 可以通过开启该配置使页面始终新开标签方式打开</div>
          </template>
          <a-button type="link" size="small"><Icon type="pticon iconfont icon-ptkj-xinxiwenxintishi"></Icon></a-button>
        </a-popover>
      </template>
      <a-switch v-model="eventModel.layoutNewTabOpen" />
    </a-form-model-item>
    <a-form-model-item
      v-show="eventModel.targetPosition == 'widgetLayout' && eventModel.actionType == 'redirectPage' && eventModel.pageType == 'page'"
      v-if="eventRule.positionSelectable !== false && eventRule.targetPosition !== false && eventRule.locateNavigation !== false"
    >
      <template slot="label">
        定位导航
        <a-popover placement="left">
          <template slot="content">页面跳转后, 将自动定位页面所属模块以及模块下对应该页面的导航</template>
          <a-button type="link" size="small"><Icon type="pticon iconfont icon-ptkj-xinxiwenxintishi"></Icon></a-button>
        </a-popover>
      </template>
      <a-switch v-model="eventModel.locateNavigation" />
    </a-form-model-item>
    <div v-if="eventModel.actionType == 'apiLinkService'">
      <a-form-model-item label="执行 API 服务">
        <WidgetDesignModal
          :bodyContainer="true"
          :id="'apiLinkService' + widget.id"
          title="API 服务设置"
          :designer="designer"
          :width="1200"
          :z-index="1000"
        >
          <a-button type="primary" size="small">设置</a-button>
          <template slot="content">
            <ApiOperationDataset :widget="widget" :configuration="eventModel" :designer="designer"></ApiOperationDataset>
          </template>
        </WidgetDesignModal>
      </a-form-model-item>
    </div>

    <!-- 用于显示事件内容的标题 -->
    <a-form-model-item label="目标标题" v-if="eventModel.targetPosition && eventModel.actionType == 'redirectPage'">
      <a-input v-model="eventModel.title" />
    </a-form-model-item>
    <div v-if="allowEventParams">
      <!-- FIXME 组件事件可以实现定义可支持的事件参数，在此处展示 -->
      <a-divider>事件参数</a-divider>
      <a-alert
        type="info"
        v-if="getWidgetEventInformation"
        :message="getWidgetEventInformation"
        show-icon
        style="margin-bottom: 8px"
      ></a-alert>
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
            <Icon type="pticon iconfont icon-ptkj-xinxiwenxintishi"></Icon>
          </a-popover>
        </template>
        <template slot="paramKeySlot" slot-scope="text, record">
          <span style="color: red; line-height: 32px; position: absolute; left: 0px" v-if="record.required">*</span>
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
import WidgetCodeEditor from '@pageAssembly/app/web/widget/commons/widget-code-editor.vue';
import { filterSelectOption } from '@framework/vue/utils/function';
import ApiOperationDataset from '@dyform/app/web/widget/commons/api-operation-dataset.vue';
import customJsModule from './custom-js-module.vue';

export default {
  name: 'EventHandlerConfig',
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
    },
    actionTypesProp: Array
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
        {
          title: '参数',
          dataIndex: 'param',
          customCell: function () {
            return { style: 'position:relative;' };
          },
          scopedSlots: { customRender: 'paramKeySlot' }
        },
        {
          dataIndex: 'paramValue',
          scopedSlots: { customRender: 'paramValueSlot' },
          slots: { title: 'paramValueTitleSlot' }
        },
        { title: '备注', dataIndex: 'remark', scopedSlots: { customRender: 'remarkSlot' } }
      ],
      selectedEventParamRowKeys: [],
      actionTypeOptions: [],
      actionOptions: [],
      pageTypeOptions: [
        { label: '页面', value: 'page' },
        { label: 'url', value: 'url' }
      ],
      triggerOptions,
      targetPosition: [
        { label: '新窗口', value: 'newWindow' },
        { label: '当前窗口', value: 'currentWindow' },
        { label: '当前布局', value: 'widgetLayout' }
      ],
      pageOptions,
      workflowOptions: [],
      eventParams: [],
      widgetEventParamComps: [],
      hasWgtEvtParamsHelpSlot: this.$slots.eventParamValueHelpSlot && this.$slots.eventParamValueHelpSlot.length > 0
    };
  },
  components: {
    ApiOperationDataset,
    WidgetCodeEditor,
    customJsModule,
    VNodes: {
      functional: true,
      render: (h, ctx) => ctx.props.vnodes
    }
  },
  computed: {
    vPageOptions() {
      let opt = [];
      for (let i = 0, len = this.pageOptions.length; i < len; i++) {
        let wtype = this.pageOptions[i].wtype;
        if (['vUniPage', 'vPage'].includes(wtype)) {
          opt.push(this.pageOptions[i]);
        }
      }
      return opt;
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
        let wtype = this.designer.widgetIdMap[this.eventModel.eventWid] && this.designer.widgetIdMap[this.eventModel.eventWid].wtype;
        let compName = `${wtype}${upperFirst(this.eventModel.eventId)}EventConfiguration`;
        if (this.widgetEventParamComps.includes(compName)) {
          return compName;
        }
      }
      return undefined;
    },

    vEvtWidgetOptions() {
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

          options.push({ label: w.configuration.isDatabaseField ? w.configuration.name || w.title : w.title, value: w.id });
        }
      }
      return options;
    },
    vWidgetOptions() {
      let options = [];
      if (this.designer) {
        for (let wid in this.designer.widgetIdMap) {
          let w = this.designer.widgetIdMap[wid];
          options.push({ label: w.configuration.isDatabaseField ? w.configuration.name || w.title : w.title, value: w.id });
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
              options.push({ label: defaultEvents[i].title, value: defaultEvents[i].id, information: defaultEvents[i].information });
            }
          }
        }
      }
      return options;
    },
    getWidgetEventInformation() {
      if (this.eventModel.eventWid) {
        for (let e of this.vWidgetCustomEvents) {
          if (e.value == this.eventModel.eventId) {
            return e.information;
          }
        }
      }
      return undefined;
    },
    isDyformAction() {
      return this.eventModel.action && this.eventModel.action.startsWith('BasicWidgetDyformDevelopment');
    }
  },
  created() {
    if (this.eventModel.trigger === undefined) {
      this.$set(this.eventModel, 'trigger', 'click');
    }
    if (this.eventModel.dmsId === undefined) {
      this.$set(this.eventModel, 'dmsId', undefined);
    }
    if (this.eventModel.eventId == undefined) {
      this.$set(this.eventModel, 'eventId', undefined);
    }
    if (this.actionTypesProp) {
      this.actionTypeOptions = this.actionTypesProp;
    } else {
      this.actionTypeOptions = [
        { label: '页面跳转', value: 'redirectPage' },
        { label: '工作流', value: 'workflow' },
        { label: '脚本代码', value: 'jsFunction' },
        { label: '执行 API 服务', value: 'apiLinkService' }
      ];
      if (this.designer) {
        this.actionTypeOptions.push({ label: '触发组件事件', value: 'widgetEvent' });
        this.actionTypeOptions.push({ label: '数据管理', value: 'dataManager' });
      }
    }
    if (this.designer) {
      this.targetPosition.push({ label: '标签页', value: 'widgetTab' });
      this.targetPosition.push({ label: '弹窗', value: 'widgetModal' });
    }
  },
  methods: {
    getDrawerContainer() {
      return this.$el.closest('.widget-design-drawer');
    },
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
      let key = this.eventModel.pageUuid;
      if (key) {
        let page = this.pageUuidMap[key];
        window.open(`/${page.wtype == 'vUniPage' ? 'uni-' : ''}page-designer/index?uuid=${key}`, '_blank');
      }
    },
    onChangeWidgetEventW() {
      // 切换组件后，清除事件和它对应的配置
      this.eventModel.eventId = '';
      delete this.eventModel.wEventParams;
      this.getWidgetEventParams();
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
      if (v === 'dataManager' && this.widget) {
        // 判断是在数据模型组件里的事件设置
        if (this.designer.WidgetDataManagerViews) {
          for (let i = 0, len = this.designer.WidgetDataManagerViews.length; i < len; i++) {
            let w = this.designer.WidgetDataManagerViews[i];
            if (w) {
              if (w.configuration.WidgetTable.id == this.widget.id) {
                this.$set(this.eventModel, 'dmsId', w.configuration.WidgetDyformSetting.id);
                return;
              }
            }
          }
        }
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
            this.pageUuidMap = {};
            if (data.data) {
              for (let d of data.data) {
                // if (d.wtype == 'vPage') {
                this.pageUuidMap[d.uuid] = d;
                this.pageOptions.push({
                  label: `${d.name} v${d.version}`,
                  value: d.id,
                  wtype: d.wtype,
                  uuid: d.uuid
                });
                // }
              }
            }
          })
          .catch(error => {});
      }
    },

    fetchWorkflowOptions() {
      let _this = this;

      let reqOptions = {
        method: 'post',
        url: '/json/data/services'
      };
      let reqData = {
        serviceName: 'flowDefineService',
        methodName: 'query',
        args: JSON.stringify([{}])
      };
      if (
        this.designer &&
        this.designer.vueInstance &&
        this.designer.vueInstance.pageDefinition &&
        this.designer.vueInstance.pageDefinition.system === this.appId
      ) {
        reqOptions.headers = {
          system_id: this.appId
        };
      }
      reqOptions.data = reqData;
      $axios(reqOptions).then(({ data }) => {
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
      let defaultScript = ['BasicWidgetDyformDevelopment', 'DefIexportDevelopment'];
      for (let scriptKey of defaultScript) {
        let _d = this.__developScript[scriptKey];
        this.dataManagerActionEventParamsMap = {};
        if (_d) {
          let meta = _d.default.prototype.META;
          if (meta && meta.hook) {
            let paramsMap = {};
            if (this.eventModel.eventParams) {
              let eventParams = this.eventModel.eventParams;
              eventParams.forEach(p => {
                paramsMap[p.paramKey] = p;
              });
            }

            for (let h in meta.hook) {
              let label = typeof meta.hook[h] === 'string' ? meta.hook[h] : meta.hook[h].title;
              let value = `${scriptKey}.${h}`;
              this.actionOptions.push({ label: label, value });
              if (typeof meta.hook[h] !== 'string' && meta.hook[h].eventParams) {
                this.dataManagerActionEventParamsMap[value] = meta.hook[h].eventParams;
                if (this.eventModel.action == value && this.eventModel.actionType == 'dataManager') {
                  meta.hook[h].eventParams.forEach(p => {
                    if (paramsMap[p.paramKey] == undefined) {
                      paramsMap[p.paramKey] = true;
                      if (this.eventModel.eventParams == undefined) {
                        this.$set(this.eventModel, 'eventParams', []);
                      }
                      this.eventModel.eventParams.push({
                        id: generateId(),
                        valueScope: p.valueScope || [],
                        paramKey: p.paramKey,
                        paramValue: undefined,
                        remark: p.remark
                      });
                    }
                  });
                }
              }
            }
          }
          // 解析出事件参数
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
    },
    onChangeJsFunction(v, option) {
      if (this.eventModel.eventParams == undefined) {
        this.$set(this.eventModel, 'eventParams', []);
      }
      if (option.eventParams) {
        for (let e of option.eventParams) {
          this.eventModel.eventParams.push({
            id: generateId(),
            valueScope: e.valueScope || [],
            paramKey: e.paramKey,
            paramValue: undefined,
            remark: e.remark
          });
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
    this.fetchWorkflowOptions();
    this.fetchActionOptions();
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
