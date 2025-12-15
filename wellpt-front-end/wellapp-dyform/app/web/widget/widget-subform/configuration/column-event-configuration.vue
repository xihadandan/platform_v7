<template>
  <div>
    <a-collapse :bordered="false" expandIconPosition="right">
      <template v-for="(record, index) in column.domEvents">
        <a-collapse-panel :key="record.id" style="background-color: #fff">
          <template slot="header">
            {{ record.title }}
            <a-tooltip title="开启后, 将会覆盖从表使用表单上该字段对应组件配置的值变更交互事件" placement="right">
              <label @click.stop="() => {}">
                <a-checkbox v-model="record.enable" />
              </label>
            </a-tooltip>
          </template>

          <a-form-model-item :wrapper-col="{ style: { width: '100%!important', 'text-align': 'center' } }">
            <a-radio-group v-model="record.codeSource" button-style="solid" size="small">
              <a-radio-button value="codeEditor">自定义代码</a-radio-button>
              <a-radio-button value="developJsFileCode">JS模块</a-radio-button>
              <a-radio-button value="widgetEvent">组件动作事件</a-radio-button>
            </a-radio-group>
          </a-form-model-item>
          <a-form-model
            layout="vertical"
            :label-col="{ style: { width: '100%!important' } }"
            :wrapper-col="{ style: { width: '100%!important' } }"
          >
            <a-form-model-item v-show="record.codeSource === 'codeEditor'">
              <WidgetCodeEditor v-model="record.customScript" width="auto" height="200px" />
            </a-form-model-item>
            <a-form-model-item v-show="record.codeSource === 'developJsFileCode'">
              <a-select
                placeholder="请选择执行脚本"
                :getPopupContainer="getPopupContainerNearestPs()"
                :filter-option="filterSelectOption"
                showSearch
                allowClear
                :style="{ width: '100%' }"
                :options="jsOptions"
                v-model="record.jsFunction"
              ></a-select>
            </a-form-model-item>
            <template v-if="record.codeSource === 'widgetEvent'">
              <template v-for="(evt, t) in record.widgetEvent">
                <div style="padding: 0px 5px">
                  <div style="padding: 12px; outline: 1px solid #e8e8e8; border-radius: 4px; position: relative; margin-bottom: 12px">
                    <a-button
                      size="small"
                      type="link"
                      style="position: absolute; top: 0px; right: 0px"
                      @click="record.widgetEvent.splice(t, 1)"
                      title="删除"
                    >
                      <Icon type="pticon iconfont icon-ptkj-shanchu"></Icon>
                    </a-button>

                    <a-form-model-item :label-col="{ style: { width: '100%' } }">
                      <template slot="label">
                        <a-checkbox v-model="evt.condition.enable" />
                        满足
                        <a-select
                          size="small"
                          :getPopupContainer="getPopupContainerNearestPs()"
                          :options="[
                            { label: '全部', value: 'all' },
                            { label: '任一', value: 'any' }
                          ]"
                          style="width: 65px"
                          v-model="evt.condition.match"
                        />
                        条件时执行
                        <a-button
                          v-show="evt.condition.enable"
                          size="small"
                          icon="plus-square"
                          title="添加条件"
                          type="link"
                          @click="
                            () => {
                              evt.condition.conditions.push({
                                code: undefined,
                                value: undefined,
                                operator: '=='
                              });
                            }
                          "
                        >
                          添加条件
                        </a-button>
                      </template>
                    </a-form-model-item>
                    <a-form-model-item
                      v-if="evt.condition.enable && evt.condition.conditions.length > 0"
                      :wrapper-col="{ style: { width: '100%' } }"
                    >
                      <div style="outline: 1px solid #e8e8e8; padding: 6px 5px 1px 5px; border-radius: 2px">
                        <template v-for="(item, i) in evt.condition.conditions">
                          <a-row type="flex" :key="'stateCon_' + i + t" style="margin-bottom: 5px">
                            <a-col flex="calc(100% - 30px)">
                              <a-input-group compact>
                                <a-select
                                  v-model="item.code"
                                  :allowClear="true"
                                  :showSearch="true"
                                  :filterSelectOption="filterSelectOption"
                                  :style="{ width: '30%' }"
                                  size="small"
                                  :getPopupContainer="getPopupContainerNearestPs()"
                                  v-if="fieldVarOptions.length > 0 || varOptions.length > 0"
                                >
                                  <a-select-opt-group v-if="fieldVarOptions.length > 0">
                                    <span slot="label">
                                      <a-icon type="code" />
                                      表单数据
                                    </span>
                                    <a-select-option v-for="opt in fieldVarOptions" :key="opt.value" :title="opt.label">
                                      {{ opt.label }}
                                    </a-select-option>
                                  </a-select-opt-group>
                                  <a-select-opt-group>
                                    <span slot="label">
                                      <a-icon type="code" />
                                      行字段数据
                                    </span>
                                    <a-select-option
                                      v-for="opt in widget.configuration.columns"
                                      :key="'CURRENT_ROW.' + opt.dataIndex"
                                      :title="opt.title"
                                    >
                                      {{ opt.title }}
                                    </a-select-option>
                                  </a-select-opt-group>
                                </a-select>
                                <a-input v-else v-model="item.code" :style="{ width: '30%' }" size="small" />
                                <a-select
                                  size="small"
                                  :getPopupContainer="getPopupContainerNearestPs()"
                                  :options="operatorOptions"
                                  v-model="item.operator"
                                  :style="{ width: '40%' }"
                                />
                                <a-input
                                  size="small"
                                  v-model="item.value"
                                  v-show="!['true', 'false'].includes(item.operator)"
                                  :style="{ width: '30%' }"
                                />
                              </a-input-group>
                            </a-col>
                            <a-col flex="25px">
                              <a-button type="link" size="small" @click="evt.condition.conditions.splice(i, 1)" title="删除">
                                <Icon type="pticon iconfont icon-ptkj-shanchu"></Icon>
                              </a-button>
                            </a-col>
                          </a-row>
                        </template>
                      </div>
                    </a-form-model-item>
                    <template v-if="evt.event != undefined">
                      <template v-for="(evtItem, itemIndex) in evt.event">
                        <div style="padding: 15px; background: rgb(249, 249, 249); position: relative; margin-bottom: 12px">
                          <a-button
                            style="position: absolute; top: 0px; right: 5px"
                            size="small"
                            type="link"
                            @click="evt.event.splice(itemIndex, 1)"
                            title="删除"
                          >
                            <Icon type="pticon iconfont icon-ptkj-shanchu" />
                          </a-button>
                          <a-form-model-item label="选择组件">
                            <a-select
                              :getPopupContainer="getPopupContainerNearestPs()"
                              :style="{ width: '100%' }"
                              :options="vWidgetOptions"
                              v-model="evtItem.eventWid"
                              show-search
                              :filter-option="filterSelectOption"
                              @change="e => changeWidgetEventParams(evtItem)"
                            ></a-select>
                          </a-form-model-item>
                          <a-form-model-item label="选择组件事件">
                            <a-select
                              :getPopupContainer="getPopupContainerNearestPs()"
                              :key="'selectWid_' + t + evtItem.eventWid + itemIndex"
                              :style="{ width: evtWidgetParamCompName(evtItem) != undefined ? 'calc(100% - 65px)' : '100%' }"
                              :options="getWidgetCustomEvents(evtItem.eventWid)"
                              @change="e => changeWidgetEventParams(evtItem)"
                              v-model="evtItem.eventId"
                            ></a-select>
                            <WidgetDesignModal
                              :z-index="2000"
                              v-if="evtWidgetParamCompName(evtItem) != undefined"
                              :key="evtItem.eventWid + '_' + t + itemIndex"
                            >
                              <template slot="title">
                                事件配置
                                <a-popover placement="bottom" v-if="hasWgtEvtParamsHelpSlot" :overlayStyle="{ 'z-index': 2001 }">
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
                                    :is="evtWidgetParamCompName(evtItem)"
                                    :widget="evtWidget(evtItem)"
                                    :designer="designer"
                                    :eventHandler="evtItem"
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
                          <a-form-model-item label="组件事件参数" v-if="evtItem.eventWid && evtItem.eventId">
                            <a-table
                              size="small"
                              :locale="{ emptyText: '暂无数据' }"
                              rowKey="id"
                              :pagination="false"
                              :columns="eventParamColumns"
                              :data-source="evtItem.eventParams"
                            >
                              <template slot="paramValueTitleSlot">
                                参数值
                                <!-- <a-popover placement="rightTop" :title="null">
                                <template slot="content">
                                  支持通过
                                  <a-tag>${ 字段 }</a-tag>
                                  取值
                                </template>
                                <a-button type="link" icon="info-circle" size="small" />
                              </a-popover> -->

                                <!-- <slot name="eventParamValueHelpSlot"></slot> -->
                              </template>
                              <template slot="paramKeySlot" slot-scope="text, record">
                                <span style="color: red; line-height: 32px" v-if="record.required">*</span>
                                <a-input v-model="record.paramKey" style="width: 100px; float: right" />
                              </template>
                              <template slot="paramValueSlot" slot-scope="text, record">
                                <template v-if="record.valueSource != undefined">
                                  <a-select
                                    v-if="record.valueSource.inputType == 'select'"
                                    :options="record.valueSource.options"
                                    v-model="record.paramValue"
                                    allow-clear
                                  />
                                  <a-select
                                    v-else-if="record.valueSource.inputType == 'multi-select'"
                                    :options="record.valueSource.options"
                                    v-model="record.paramValue"
                                    allow-clear
                                    mode="multiple"
                                  />
                                  <a-input v-else v-model="record.paramValue" />
                                </template>
                                <a-auto-complete v-else :data-source="record.valueScope" v-model.trim="record.paramValue" allow-clear />
                              </template>
                              <template slot="remarkSlot" slot-scope="text, record, index">
                                <div style="display: flex; align-items: baseline">
                                  <a-input style="width: 150px" v-model="record.remark" />
                                  <a-icon
                                    @click="evtItem.eventParams.splice(index, 1)"
                                    type="minus-circle"
                                    theme="filled"
                                    style="width: 30px; color: var(--w-danger-color)"
                                  />
                                </div>
                              </template>
                              <template slot="footer">
                                <div style="text-align: right">
                                  <a-button type="link" size="small" @click="e => addEventParams(evtItem.eventParams)">
                                    <Icon type="pticon iconfont icon-ptkj-jiahao"></Icon>
                                    添加参数
                                  </a-button>
                                </div>
                              </template>
                            </a-table>
                          </a-form-model-item>
                        </div>
                      </template>
                    </template>
                    <a-button :block="true" type="link" @click="e => addConditionEvent(evt)">
                      <Icon type="pticon iconfont icon-ptkj-jiahao" />
                      添加组件事件
                    </a-button>
                  </div>
                </div>
              </template>

              <a-button :block="true" type="link" @click="e => addWidgetActionEvent(record)">
                <Icon type="pticon iconfont icon-ptkj-jiahao"></Icon>
                添加一组组件动作事件
              </a-button>
            </template>
          </a-form-model>
        </a-collapse-panel>
      </template>
    </a-collapse>
  </div>
</template>
<style lang="less"></style>
<script type="text/babel">
import { filterSelectOption, getPopupContainerNearestPs } from '@framework/vue/utils/function';
import { generateId } from '@framework/vue/utils/util';
import { upperFirst } from 'lodash';
export default {
  name: 'ColumnEventConfiguration',
  props: {
    widget: Object,
    column: Object,
    designer: Object
  },
  components: {},
  computed: {
    jsOptions() {
      let keys = [],
        options = [];
      if (this.designer != undefined && this.designer.pageJsModule != undefined) {
        // 页面脚本
        keys.push(this.designer.pageJsModule.key);
      }

      if (this.widget != undefined && this.widget.configuration.jsModules) {
        // 组件脚本
        let widgetJsModules = Array.isArray(this.widget.configuration.jsModules)
          ? this.widget.configuration.jsModules
          : [this.widget.configuration.jsModules];
        for (let i = 0, len = widgetJsModules.length; i < len; i++) {
          keys.push(widgetJsModules[i].key);
        }
      }
      if (this.jsKeys != undefined) {
        keys = keys.concat(this.jsKeys);
      }

      for (let i = 0, len = keys.length; i < len; i++) {
        let _d = this.__developScript[keys[i]];
        if (_d) {
          try {
            let meta = _d.default.prototype.META;
            if (meta && meta.hook) {
              for (let h in meta.hook) {
                options.push({ label: meta.hook[h], value: `${keys[i]}.${h}` });
              }
            }
          } catch (error) {
            console.error(`二开脚本 ${keys[i]} 解析脚本数据异常: `, error);
          }
        }
      }
      return options;
    },
    fieldVarOptions() {
      let opt = [];
      if (this.designer.SimpleFieldInfos != undefined) {
        for (let i = 0, len = this.designer.SimpleFieldInfos.length; i < len; i++) {
          let info = this.designer.SimpleFieldInfos[i];
          opt.push({
            label: info.name,
            value: info.code
          });
        }
      }

      return opt;
    },
    vWidgetOptions() {
      let options = [];
      if (this.designer) {
        for (let wid in this.designer.widgetIdMap) {
          let w = this.designer.widgetIdMap[wid];
          let defaultEvents = this.designer.widgetDefaultEvents[w.id];
          if (
            wid == this.widget.id ||
            ((w.configuration.defineEvents == undefined || w.configuration.defineEvents.length == 0) &&
              (defaultEvents == undefined || defaultEvents.length == 0))
          ) {
            continue;
          }

          options.push({ label: w.configuration.name || w.title, value: w.id });
        }
      }
      for (let i = 0, len = this.widget.configuration.columns.length; i < len; i++) {
        if (this.widget.configuration.columns[i].dataIndex != this.column.dataIndex) {
          options.push({
            label: '行内字段 - ' + this.widget.configuration.columns[i].title,
            value: this.widget.configuration.columns[i].widget.id
          });
        }
      }
      return options;
    },
    widgetMethodOptions() {
      if (EASY_ENV_IS_NODE) {
        return [];
      }

      let options = [],
        comp = window.Vue.options.components[this.widget.wtype],
        methods = comp.options.methods,
        META = comp.options.META;

      if (this.widget.wtype === 'WidgetTemplate') {
        // 模板组件，取模板中vue模板的方法
        if (this.widget.configuration.type === 'vue') {
          if (this.widget.configuration.sourceType == 'codeEditor') {
            let templateMethods = this.widget.configuration.templateMethods;
            if (templateMethods.length > 0) {
              META = { method: {} };
              for (let i = 0, len = templateMethods.length; i < len; i++) {
                META.method[templateMethods[i].name] = templateMethods[i].name;
              }
            }
          } else if (this.widget.configuration.templateName) {
            let _v = document.querySelector("[w-id='" + this.widget.id + "']").__vue__;
            let tag = _v.$parent.$options._componentTag;
            if (tag == this.widget.configuration.templateName) {
              if (_v.$parent.$options.methods) {
                META = _v.$parent.$options.META;
              }
            } else {
              let wtemplate = _v.$refs.wTemplate;
              if (wtemplate && wtemplate.$options._componentTag == this.widget.configuration.templateName && wtemplate.$options.methods) {
                META = wtemplate.$options.META;
              }
            }
          }
        } else {
          return [];
        }
      }

      if (META && META.method) {
        for (let m in META.method) {
          options.push({
            label: META.method[m],
            value: m
          });
        }
        return options;
      }

      return options;
    },
    columnWidgetIdMap() {
      let map = {};
      for (let i = 0, len = this.widget.configuration.columns.length; i < len; i++) {
        if (this.widget.configuration.columns[i].widget != undefined) {
          map[this.widget.configuration.columns[i].widget.id] = this.widget.configuration.columns[i].widget;
        }
      }
      return map;
    },
    columnFieldEvent() {
      return [
        {
          id: 'setVisible',
          title: '设置为显示或者隐藏',
          eventParams: [
            {
              paramKey: 'visible',
              remark: '是否显示',
              valueSource: {
                inputType: 'select', // multi-select , checkbox , radio, input
                options: [
                  { label: '显示', value: 'true' },
                  { label: '隐藏', value: 'false' }
                ]
              }
            }
          ]
        },

        {
          id: 'setEditable',
          title: '设置为可编辑或者不可编辑',
          eventParams: [
            {
              paramKey: 'editable',
              remark: '是否可编辑',
              valueSource: {
                inputType: 'select', // multi-select , checkbox , radio, input
                options: [
                  { label: '编辑', value: 'true' },
                  { label: '不可编辑', value: 'false' }
                ]
              }
            }
          ]
        },
        {
          id: 'setValueByEventParams',
          title: '设值',
          eventParams: [
            {
              paramKey: 'value',
              remark: '值'
            }
          ]
        },
        {
          id: 'setRequired',
          title: '设置为必填或者非必填',
          eventParams: [
            {
              paramKey: 'required',
              remark: '是否必填',
              valueSource: {
                inputType: 'select', // multi-select , checkbox , radio, input
                options: [
                  { label: '必填', value: 'true' },
                  { label: '非必填', value: 'false' }
                ]
              }
            }
          ]
        },
        {
          id: 'clearValidate',
          title: '清空字段校验信息'
        }
      ];
    }
  },
  data() {
    return {
      defineEventsColumns: [
        {
          title: '名称',
          dataIndex: 'title',
          scopedSlots: { customRender: 'titleSlot' }
        },
        {
          title: '操作',
          dataIndex: 'operation',
          align: 'right',
          class: 'operation',
          scopedSlots: { customRender: 'operationSlot' }
        }
      ],
      operatorOptions: [
        { label: '等于', value: '==' },
        { label: '不等于', value: '!=' },
        { label: '为真', value: 'true' },
        { label: '为假', value: 'false' },
        { label: '包含于', value: 'in' },
        { label: '不包含于', value: 'not in' },
        { label: '包含', value: 'contain' },
        { label: '不包含', value: 'not contain' }
      ],
      eventParamColumns: [
        { title: '参数', dataIndex: 'param', scopedSlots: { customRender: 'paramKeySlot' } },
        {
          dataIndex: 'paramValue',
          width: 150,
          scopedSlots: { customRender: 'paramValueSlot' },
          slots: { title: 'paramValueTitleSlot' }
        },
        { title: '备注', dataIndex: 'remark', scopedSlots: { customRender: 'remarkSlot' } }
      ],

      operatorOptions: [
        { label: '等于', value: '==' },
        { label: '不等于', value: '!=' },
        { label: '为真', value: 'true' },
        { label: '为假', value: 'false' },
        { label: '包含于', value: 'in' },
        { label: '不包含于', value: 'not in' },
        { label: '包含', value: 'contain' },
        { label: '不包含', value: 'not contain' }
      ],
      widgetEventParamComps: []
    };
  },
  beforeCreate() {},
  created() {
    if (this.column.domEvents == undefined) {
      this.$set(this.column, 'domEvents', [
        {
          id: 'onChange',
          title: '值变更时候触发',
          codeSource: 'codeEditor',
          enable: false,
          jsFunction: undefined,
          widgetEvent: [],
          customScript: undefined // 事件脚本
        }
      ]);
    } else {
      for (let d of this.column.domEvents) {
        if (d.widgetEvent) {
          for (let w of d.widgetEvent) {
            if (w.eventId != undefined) {
              this.$set(w, 'event', [
                {
                  eventId: w.eventId,
                  eventParams: w.eventParams,
                  eventWid: w.eventWid
                }
              ]);
            }
          }
        }
      }
    }
  },
  beforeMount() {},
  mounted() {},
  methods: {
    getPopupContainerNearestPs,
    filterSelectOption,
    evtWidget(evt) {
      if (evt.eventWid && evt.eventId) {
        if (this.columnWidgetIdMap[evt.eventWid]) {
          return this.columnWidgetIdMap[evt.eventWid];
        }
        return this.designer.widgetIdMap[evt.eventWid];
      }
      return undefined;
    },
    evtWidgetParamCompName(evt) {
      if (evt.eventWid && evt.eventId) {
        let wtype = undefined;
        if (this.designer.widgetIdMap[evt.eventWid] != undefined) {
          wtype = this.designer.widgetIdMap[evt.eventWid].wtype;
        }
        if (this.columnWidgetIdMap[evt.eventWid]) {
          wtype = this.columnWidgetIdMap[evt.eventWid].wtype;
        }
        if (wtype) {
          let compName = `${wtype}${upperFirst(evt.eventId)}EventConfiguration`;
          if (this.widgetEventParamComps.includes(compName)) {
            return compName;
          }
        }
      }
      return undefined;
    },

    addEventParams(params) {
      params.push({
        id: generateId(),
        paramKey: undefined,
        paramValue: undefined
      });
    },
    addConditionEvent(evt) {
      if (evt.event == undefined) {
        this.$set(evt, 'event', []);
      }
      evt.event.push({
        eventWid: undefined,
        eventParams: [],
        eventId: undefined
      });
    },
    addWidgetActionEvent(item) {
      item.widgetEvent.push({
        eventWid: undefined,
        eventParams: [],
        eventId: undefined,
        condition: {
          enable: false,
          conditions: [],
          match: 'all'
        }
      });
    },

    changeWidgetEventParams(evt) {
      let wid = evt.eventWid,
        eventId = evt.eventId,
        eventParams = evt.eventParams;
      eventParams.splice(0, eventParams.length);
      if (wid && eventId && this.designer) {
        let w = this.designer.widgetIdMap[wid];

        if (w) {
          let defaultEvents = this.designer.widgetDefaultEvents[w.id];
          if (defaultEvents) {
            for (let i = 0, len = defaultEvents.length; i < len; i++) {
              if (defaultEvents[i].id === eventId && defaultEvents[i].eventParams != undefined)
                for (let e = 0, elen = defaultEvents[i].eventParams.length; e < elen; e++) {
                  let p = { id: generateId(), ...defaultEvents[i].eventParams[e] };
                  eventParams.push(p);
                }
            }
          }
          if (w.configuration.defineEvents) {
            for (let i = 0, len = w.configuration.defineEvents.length; i < len; i++) {
              if (w.configuration.defineEvents[i].id === eventId && w.configuration.defineEvents[i].eventParams != undefined)
                for (let e = 0, elen = w.configuration.defineEvents[i].eventParams.length; e < elen; e++) {
                  let p = { id: generateId(), ...w.configuration.defineEvents[i].eventParams[e] };
                  eventParams.push(p);
                }
            }
          }
        }
        w = this.columnWidgetIdMap[wid];
        if (w) {
          for (let i = 0, len = this.columnFieldEvent.length; i < len; i++) {
            if (this.columnFieldEvent[i].id == eventId) {
              if (this.columnFieldEvent[i].eventParams != undefined) {
                eventParams.push(...this.columnFieldEvent[i].eventParams);
                break;
              }
            }
          }
        }
      }
      return eventParams;
    },
    getWidgetCustomEvents(wid) {
      let options = [];
      if (this.designer) {
        let w = this.designer.widgetIdMap[wid];
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
        w = this.columnWidgetIdMap[wid];
        if (w) {
          this.columnFieldEvent.forEach(e => {
            options.push({
              label: e.title,
              value: e.id
            });
          });
        }
      }
      return options;
    }
  }
};
</script>
