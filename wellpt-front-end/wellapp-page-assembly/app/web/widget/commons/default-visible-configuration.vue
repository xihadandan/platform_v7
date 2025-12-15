<template>
  <div>
    <a-form-model-item label="显示" class="item-lh">
      <a-radio-group size="small" v-model="configuration.defaultVisible" button-style="solid">
        <a-switch v-model="configuration.defaultVisible" />
      </a-radio-group>
    </a-form-model-item>

    <a-form-model-item class="item-lh" :label-col="{ style: { width: '100%' } }">
      <template slot="label">
        <a-checkbox v-model="configuration.defaultVisibleVar.enable" />
        <template v-if="compact">
          满足
          <a-select
            size="small"
            :options="[
              { label: '全部', value: 'all' },
              { label: '任一', value: 'any' }
            ]"
            style="width: 65px"
            v-model="configuration.defaultVisibleVar.match"
          />
          条件时{{ configuration.defaultVisible ? '显示' : '隐藏' }}
          <a-button v-if="compact && configuration.defaultVisibleVar.enable" size="small" type="link" @click="addVisibleCondition">
            <Icon type="pticon iconfont icon-ptkj-jiahao"></Icon>
            添加
          </a-button>
        </template>
        <template v-else>满足条件时{{ configuration.defaultVisible ? '显示' : '隐藏' }}</template>
      </template>
    </a-form-model-item>
    <a-form-model-item
      :label="null"
      v-if="configuration.defaultVisibleVar.enable"
      :wrapperCol="{ style: { width: '100%', borderRadius: '5px' } }"
    >
      <div style="padding: 5px; background: #fff; border-radius: 2px; outline: 1px solid #e8e8e8">
        <template v-if="compact && configuration.defaultVisibleVar.conditions != undefined">
          <div v-show="configuration.defaultVisibleVar.conditions.length == 0" style="text-align: center">
            <span>
              <label style="color: rgba(0, 0, 0, 0.25)">暂无条件</label>
              <a-divider type="vertical" />
              <a-button size="small" type="link" @click="addVisibleCondition">点击添加</a-button>
            </span>
          </div>
          <template v-for="(con, i) in configuration.defaultVisibleVar.conditions">
            <a-row type="flex" :key="'visible_con_' + i">
              <a-col flex="calc(100% - 60px)">
                <div>
                  <a-input v-if="codeInput" v-model="con.code" :style="{ width: '100%' }"></a-input>
                  <a-select
                    v-else
                    v-model="con.code"
                    :allowClear="true"
                    :showSearch="true"
                    :filterOption="filterOption"
                    :style="{ width: '100%' }"
                  >
                    <slot name="extraAutoCompleteSelectGroup"></slot>
                    <template v-if="allowFormVariable && underDyformScope && fieldVarOptions.length">
                      <a-select-opt-group>
                        <span slot="label">
                          <a-icon type="code" />
                          表单数据
                        </span>
                        <a-select-option v-for="opt in fieldVarOptions" :key="opt.value" readonly :value="opt.value" :title="opt.label">
                          {{ opt.label }}
                        </a-select-option>
                      </a-select-opt-group>
                    </template>
                    <a-select-opt-group v-if="underDyformScope">
                      <span slot="label">
                        <a-icon type="code" />
                        表单状态
                      </span>
                      <a-select-option readonly value="__DYFORM__.editable" title="表单可编辑">表单可编辑</a-select-option>
                    </a-select-opt-group>
                    <a-select-opt-group v-if="pageParamOptions.length">
                      <span slot="label">
                        <a-icon type="code" />
                        页面参数
                      </span>
                      <a-select-option
                        v-for="(opt, i) in pageParamOptions"
                        :title="opt.label"
                        :value="opt.value"
                        :key="'page_param_opt_' + i"
                      >
                        <div style="width: 220px; display: flex; align-items: center; justify-content: space-between">
                          {{ opt.label }}
                          <a-tag style="float: right">{{ opt.value }}</a-tag>
                        </div>
                      </a-select-option>
                    </a-select-opt-group>
                    <a-select-opt-group v-if="underDyformScope && subformFieldVarOptions.length > 0">
                      <span slot="label">
                        <a-icon type="code" />
                        从表数据
                      </span>
                      <a-select-option
                        v-for="opt in subformFieldVarOptions"
                        :key="opt.value"
                        readonly
                        :value="opt.value"
                        :title="opt.label"
                      >
                        {{ opt.label }}
                      </a-select-option>
                    </a-select-opt-group>
                    <a-select-opt-group>
                      <span slot="label">
                        <a-icon type="code" />
                        用户数据
                      </span>
                      <a-select-option v-for="opt in userDataOptions" :key="opt.value" readonly :value="opt.value" :title="opt.label">
                        {{ opt.label }}
                      </a-select-option>
                    </a-select-opt-group>
                    <a-select-opt-group>
                      <span slot="label">
                        <a-icon type="code" />
                        日期时间
                      </span>
                      <a-select-option v-for="opt in timeDataOptions" :key="opt.value" readonly :value="opt.value" :title="opt.label">
                        {{ opt.label }}
                      </a-select-option>
                    </a-select-opt-group>
                    <a-select-opt-group>
                      <span slot="label">
                        <a-icon type="code" />
                        工作流数据
                      </span>
                      <a-select-option v-for="opt in flowDataOptions" :key="opt.value" readonly :value="opt.value" :title="opt.label">
                        {{ opt.label }}
                      </a-select-option>
                    </a-select-opt-group>
                    <a-select-opt-group>
                      <span slot="label">
                        <a-icon type="code" />
                        其他
                      </span>
                      <a-select-option readonly value="_URL_PARAM_." title="URL地址参数">URL地址参数</a-select-option>
                      <a-select-option readonly value="TERMINALTYPE.MOBILE" title="是否移动端">是否移动端</a-select-option>
                    </a-select-opt-group>
                  </a-select>
                  <a-input
                    v-if="con.code != undefined && con.code.endsWith('.')"
                    v-model="con.codeValue"
                    :placeholder="'请输入' + { '_URL_PARAM_.': 'URL地址参数' }[con.code]"
                  />
                </div>
                <a-input-group compact>
                  <a-select
                    :options="getOperatorOptions(con)"
                    :key="'key_' + con.code + i"
                    v-model="con.operator"
                    :style="{ width: !['true', 'false'].includes(con.operator) ? '100px' : '100%' }"
                    @change="changeOperatorSelected(con)"
                  />

                  <template v-if="!['true', 'false'].includes(con.operator)">
                    <a-select v-model="con.valueType" style="width: 70px" @change="con.value = undefined">
                      <a-select-option value="constant">常量</a-select-option>
                      <a-select-option value="variable">变量</a-select-option>
                    </a-select>
                    <a-input
                      v-if="con.valueType !== 'variable'"
                      :style="{ width: 'calc(100% - 170px)' }"
                      v-model="con.value"
                      class="design-constant-variable-input"
                    />
                    <a-select
                      v-else
                      v-model="con.value"
                      :allowClear="true"
                      :showSearch="true"
                      :filterOption="filterOption"
                      :style="{ width: 'calc(100% - 170px)' }"
                    >
                      <slot name="extraAutoCompleteSelectGroup"></slot>
                      <template v-if="allowFormVariable && underDyformScope && fieldVarOptions.length">
                        <a-select-opt-group>
                          <span slot="label">
                            <a-icon type="code" />
                            表单数据
                          </span>
                          <a-select-option v-for="opt in fieldVarOptions" :key="opt.value" readonly :value="opt.value" :title="opt.label">
                            {{ opt.label }}
                          </a-select-option>
                        </a-select-opt-group>
                      </template>
                      <a-select-opt-group v-if="underDyformScope">
                        <span slot="label">
                          <a-icon type="code" />
                          表单状态
                        </span>
                        <a-select-option readonly value="__DYFORM__.editable" title="表单可编辑">表单可编辑</a-select-option>
                      </a-select-opt-group>
                      <a-select-opt-group v-if="pageParamOptions.length">
                        <span slot="label">
                          <a-icon type="code" />
                          页面参数
                        </span>
                        <a-select-option
                          v-for="(opt, i) in pageParamOptions"
                          :title="opt.label"
                          :value="opt.value"
                          :key="'page_param_opt_' + i"
                        >
                          <div style="width: 220px; display: flex; align-items: center; justify-content: space-between">
                            {{ opt.label }}
                            <a-tag style="float: right">{{ opt.value }}</a-tag>
                          </div>
                        </a-select-option>
                      </a-select-opt-group>

                      <a-select-opt-group>
                        <span slot="label">
                          <a-icon type="code" />
                          用户数据
                        </span>
                        <a-select-option v-for="opt in userDataOptions" :key="opt.value" readonly :value="opt.value" :title="opt.label">
                          {{ opt.label }}
                        </a-select-option>
                      </a-select-opt-group>
                      <a-select-opt-group>
                        <span slot="label">
                          <a-icon type="code" />
                          日期时间
                        </span>
                        <a-select-option v-for="opt in timeDataOptions" :key="opt.value" readonly :value="opt.value" :title="opt.label">
                          {{ opt.label }}
                        </a-select-option>
                      </a-select-opt-group>
                      <a-select-opt-group>
                        <span slot="label">
                          <a-icon type="code" />
                          工作流数据
                        </span>
                        <a-select-option v-for="opt in flowDataOptions" :key="opt.value" readonly :value="opt.value" :title="opt.label">
                          {{ opt.label }}
                        </a-select-option>
                      </a-select-opt-group>
                    </a-select>
                  </template>
                </a-input-group>
              </a-col>
              <a-col flex="50px" style="align-self: center; text-align: center">
                <a-button type="link" size="small" @click="configuration.defaultVisibleVar.conditions.splice(i, 1)" title="删除">
                  <Icon type="pticon iconfont icon-ptkj-shanchu"></Icon>
                </a-button>
              </a-col>
            </a-row>
            <a-divider style="margin: 7px 0px" v-if="i != configuration.defaultVisibleVar.conditions.length - 1" />
          </template>
        </template>
        <template v-else>
          <div>
            <a-select
              v-model="configuration.defaultVisibleVar.code"
              :allowClear="true"
              :showSearch="true"
              :filterOption="filterOption"
              :style="{ width: '100%' }"
            >
              <slot name="extraAutoCompleteSelectGroup"></slot>
              <template v-if="underDyformScope && fieldVarOptions.length > 0">
                <a-select-opt-group>
                  <span slot="label">
                    <a-icon type="code" />
                    表单数据
                  </span>
                  <a-select-option v-for="opt in fieldVarOptions" :key="opt.value" readonly :value="opt.value" :title="opt.label">
                    {{ opt.label }}
                  </a-select-option>
                </a-select-opt-group>
              </template>
              <a-select-opt-group v-if="underDyformScope">
                <span slot="label">
                  <a-icon type="code" />
                  表单状态
                </span>
                <a-select-option readonly value="__DYFORM__.editable" title="表单可编辑">表单可编辑</a-select-option>
              </a-select-opt-group>
              <a-select-opt-group v-if="pageParamOptions.length">
                <span slot="label">
                  <a-icon type="code" />
                  页面参数
                </span>
                <a-select-option v-for="(opt, i) in pageParamOptions" :title="opt.label" :value="opt.value" :key="'page_param_opt_' + i">
                  <div style="width: 220px; display: flex; align-items: center; justify-content: space-between">
                    {{ opt.label }}
                    <a-tag style="float: right">{{ opt.value }}</a-tag>
                  </div>
                </a-select-option>
              </a-select-opt-group>

              <a-select-opt-group>
                <span slot="label">
                  <a-icon type="code" />
                  用户数据
                </span>
                <a-select-option v-for="opt in userDataOptions" :key="opt.value" readonly :value="opt.value" :title="opt.label">
                  {{ opt.label }}
                </a-select-option>
              </a-select-opt-group>
              <a-select-opt-group>
                <span slot="label">
                  <a-icon type="code" />
                  日期时间
                </span>
                <a-select-option v-for="opt in timeDataOptions" :key="opt.value" readonly :value="opt.value" :title="opt.label">
                  {{ opt.label }}
                </a-select-option>
              </a-select-opt-group>
              <a-select-opt-group>
                <span slot="label">
                  <a-icon type="code" />
                  工作流数据
                </span>
                <a-select-option v-for="opt in flowDataOptions" :key="opt.value" readonly :value="opt.value" :title="opt.label">
                  {{ opt.label }}
                </a-select-option>
              </a-select-opt-group>
              <a-select-opt-group>
                <span slot="label">
                  <a-icon type="code" />
                  其他
                </span>
                <a-select-option readonly value="_URL_PARAM_." title="URL地址参数">URL地址参数</a-select-option>
                <a-select-option readonly value="TERMINALTYPE.MOBILE" title="是否移动端">是否移动端</a-select-option>
              </a-select-opt-group>
            </a-select>
            <a-input
              v-if="configuration.defaultVisibleVar.code != undefined && configuration.defaultVisibleVar.code.endsWith('.')"
              v-model="configuration.defaultVisibleVar.codeValue"
              :placeholder="'请输入' + { '_URL_PARAM_.': 'URL地址参数' }[configuration.defaultVisibleVar.code]"
            />

            <a-input-group compact>
              <a-select
                :options="operatorOptions"
                v-model="configuration.defaultVisibleVar.operator"
                :style="{ width: !['true', 'false'].includes(configuration.defaultVisibleVar.operator) ? '100px' : '100%' }"
                @change="changeOperatorSelected(configuration.defaultVisibleVar)"
              />

              <template v-if="!['true', 'false'].includes(configuration.defaultVisibleVar.operator)">
                <a-select
                  v-model="configuration.defaultVisibleVar.valueType"
                  style="width: 70px"
                  @change="configuration.defaultVisibleVar.value = undefined"
                >
                  <a-select-option value="constant">常量</a-select-option>
                  <a-select-option value="variable">变量</a-select-option>
                </a-select>
                <a-input
                  v-if="configuration.defaultVisibleVar.valueType !== 'variable'"
                  :style="{ width: 'calc(100% - 170px)' }"
                  v-model="configuration.defaultVisibleVar.value"
                  class="design-constant-variable-input"
                />
                <a-select
                  v-else
                  v-model="configuration.defaultVisibleVar.value"
                  :allowClear="true"
                  :showSearch="true"
                  :filterOption="filterOption"
                  :style="{ width: 'calc(100% - 170px)' }"
                >
                  <slot name="extraAutoCompleteSelectGroup"></slot>
                  <template v-if="underDyformScope && fieldVarOptions.length > 0">
                    <a-select-opt-group>
                      <span slot="label">
                        <a-icon type="code" />
                        表单数据
                      </span>
                      <a-select-option v-for="opt in fieldVarOptions" :key="opt.value" readonly :value="opt.value" :title="opt.label">
                        {{ opt.label }}
                      </a-select-option>
                    </a-select-opt-group>
                  </template>
                  <a-select-opt-group v-if="underDyformScope">
                    <span slot="label">
                      <a-icon type="code" />
                      表单状态
                    </span>
                    <a-select-option readonly value="__DYFORM__.editable" title="表单可编辑">表单可编辑</a-select-option>
                  </a-select-opt-group>
                  <a-select-opt-group v-if="pageParamOptions.length">
                    <span slot="label">
                      <a-icon type="code" />
                      页面参数
                    </span>
                    <a-select-option
                      v-for="(opt, i) in pageParamOptions"
                      :title="opt.label"
                      :value="opt.value"
                      :key="'page_param_opt_' + i"
                    >
                      <div style="width: 220px; display: flex; align-items: center; justify-content: space-between">
                        {{ opt.label }}
                        <a-tag style="float: right">{{ opt.value }}</a-tag>
                      </div>
                    </a-select-option>
                  </a-select-opt-group>

                  <a-select-opt-group>
                    <span slot="label">
                      <a-icon type="code" />
                      用户数据
                    </span>
                    <a-select-option v-for="opt in userDataOptions" :key="opt.value" readonly :value="opt.value" :title="opt.label">
                      {{ opt.label }}
                    </a-select-option>
                  </a-select-opt-group>
                  <a-select-opt-group>
                    <span slot="label">
                      <a-icon type="code" />
                      日期时间
                    </span>
                    <a-select-option v-for="opt in timeDataOptions" :key="opt.value" readonly :value="opt.value" :title="opt.label">
                      {{ opt.label }}
                    </a-select-option>
                  </a-select-opt-group>
                  <a-select-opt-group>
                    <span slot="label">
                      <a-icon type="code" />
                      工作流数据
                    </span>
                    <a-select-option v-for="opt in flowDataOptions" :key="opt.value" readonly :value="opt.value" :title="opt.label">
                      {{ opt.label }}
                    </a-select-option>
                  </a-select-opt-group>
                  <a-select-opt-group>
                    <span slot="label">
                      <a-icon type="code" />
                      其他
                    </span>
                    <a-select-option readonly value="_URL_PARAM_." title="URL地址参数">URL地址参数</a-select-option>
                  </a-select-opt-group>
                </a-select>
              </template>
            </a-input-group>
          </div>
        </template>
      </div>
    </a-form-model-item>
    <a-form-model-item label="显示隐藏切换执行事件" v-if="allowToggleEvent">
      <!-- 自定义代码 -->
      <WidgetDesignDrawer id="widgetVisibleStateToggle" title="事件" :designer="designer" :zIndex="1010">
        <a-button size="small" title="更多设置" type="link" icon="more"></a-button>
        <template slot="content">
          <a-form-model :label-col="{ span: 6 }" :wrapper-col="{ span: 15 }">
            <a-form-model-item label="事件代码来源">
              <a-radio-group v-model="configuration.visibleToggle.codeSource" button-style="solid">
                <a-radio-button value="codeEditor">自定义代码</a-radio-button>
                <a-radio-button value="developJsFileCode">JS模块</a-radio-button>
                <a-radio-button value="widgetMethod">组件方法</a-radio-button>
              </a-radio-group>
            </a-form-model-item>
            <a-form-model-item label="编写代码" v-show="configuration.visibleToggle.codeSource === 'codeEditor'">
              <WidgetCodeEditor v-model="configuration.visibleToggle.customScript" width="auto" height="200px" />
            </a-form-model-item>
            <a-form-model-item label="脚本代码" v-show="configuration.visibleToggle.codeSource === 'developJsFileCode'">
              <JsHookSelect :designer="designer" :widget="widget" v-model="configuration.visibleToggle.jsFunction" />
            </a-form-model-item>
            <a-form-model-item label="组件方法" v-show="configuration.visibleToggle.codeSource === 'widgetMethod'">
              <a-select
                showSearch
                allowClear
                :style="{ width: '100%' }"
                :options="widgetMethodOptions()"
                v-model="configuration.visibleToggle.widgetMethod"
              ></a-select>
            </a-form-model-item>
          </a-form-model>
        </template>
      </WidgetDesignDrawer>
    </a-form-model-item>
  </div>
</template>
<style></style>
<script type="text/babel">
import { debounce } from 'lodash';
import { operatorOptions, userVariableOptions, timeDataVariableOptions, flowDataVariableOptions } from './constant.js';
import moment from 'moment';

export default {
  name: 'DefaultVisibleConfiguration',
  inject: ['pageParams', 'dyform'],
  props: {
    configuration: Object,
    designer: Object,
    widget: Object,
    allowToggleEvent: {
      type: Boolean,
      default: false
    },
    compact: Boolean,
    codeRule: Object,
    allowFormVariable: {
      type: Boolean,
      default: true
    }
  },
  data() {
    return {
      inputReadonly: false,
      dataSource: [],
      operatorOptions: operatorOptions,
      userDataOptions: userVariableOptions,
      timeDataOptions: timeDataVariableOptions,
      flowDataOptions: flowDataVariableOptions
    };
  },

  beforeCreate() {},
  components: {},
  computed: {
    underDyformScope() {
      return this.dyform != undefined;
    },

    pageParamOptions() {
      let opt = [];
      if (this.pageParams != undefined) {
        for (let i = 0, len = this.pageParams.length; i < len; i++) {
          opt.push({
            label: this.pageParams[i].name || this.pageParams[i].code,
            value: this.pageParams[i].code
          });
        }
      }
      return opt;
    },
    pageVarOptions() {
      // 页面变量路径
      let paths = this.designer.pageVarKeyPaths(),
        options = [];
      for (let i = 0, len = paths.length; i < len; i++) {
        options.push({ label: paths[i], value: paths[i] });
      }
      return options;
    },
    // 表单字段
    fieldVarOptions() {
      let opt = [];
      if (this.designer.SimpleFieldInfos) {
        // 表单设计
        for (let i = 0, len = this.designer.SimpleFieldInfos.length; i < len; i++) {
          let info = this.designer.SimpleFieldInfos[i];
          opt.push({
            label: info.name,
            value: `_FORM_DATA_.${info.code}`
          });
        }
      }
      return opt;
    },
    subformFieldVarOptions() {
      let opt = [];
      if (this.designer.WidgetSubforms) {
        for (let widget of this.designer.WidgetSubforms) {
          let { columns, title, formUuid, formName } = widget.configuration;
          for (let c of columns) {
            opt.push({
              label: `${formName} - [字段] ${c.title}`,
              value: '_SUBFORM_DATA_.`' + formUuid + '`.' + c.dataIndex
            });
          }
          opt.push({
            label: `${formName} - 总记录数`,
            value: '$count(_SUBFORM_DATA_.`' + formUuid + '`)'
          });
        }
      }
      return opt;
    }
    // autocompleteOptions() {
    //   // let options = [],
    //   //   paths = this.designer.pageVarKeyPaths();
    //   // for (let i = 0, len = paths.length; i < len; i++) {
    //   //   options.push({ label: paths[i], value: paths[i] });
    //   // }
    //   return this.designer.pageVarKeyPaths();
    // }
  },
  created() {
    if (!this.configuration.hasOwnProperty('defaultVisibleVar')) {
      let obj = this.compact
        ? {
            enable: false,
            match: 'all',
            conditions: []
          }
        : { enable: false, operator: 'true', code: undefined, value: undefined };
      this.$set(this.configuration, 'defaultVisibleVar', obj);
    }

    if (!this.configuration.hasOwnProperty('defaultVisible')) {
      this.$set(this.configuration, 'defaultVisible', true);
    } else {
      // 旧数据处理: 支持多个条件判断
      if (this.compact) {
        if (this.configuration.defaultVisibleVar.match == undefined) {
          this.$set(this.configuration.defaultVisibleVar, 'match', 'all');
        }
        if (this.configuration.defaultVisibleVar.conditions == undefined) {
          this.$set(this.configuration.defaultVisibleVar, 'conditions', [
            {
              operator: this.configuration.defaultVisibleVar.operator,
              code: this.configuration.defaultVisibleVar.code,
              value: this.configuration.defaultVisibleVar.value
            }
          ]);
          delete this.configuration.defaultVisibleVar.operator;
          delete this.configuration.defaultVisibleVar.code;
          delete this.configuration.defaultVisibleVar.value;
        }
      }
    }

    if (!this.configuration.hasOwnProperty('visibleToggle')) {
      this.$set(this.configuration, 'visibleToggle', {
        codeSource: undefined,
        customScript: undefined,
        jsFunction: undefined
      });
    }
  },
  methods: {
    getOperatorOptions(con) {
      if (con.code) {
        let opt = [];
        for (let i = 0, len = this.operatorOptions.length; i < len; i++) {
          if (con.code.startsWith('_SUBFORM_DATA_.') && ['contain', 'not contain'].includes(this.operatorOptions[i].value)) {
            // 从表字段只能用包含或者包含于
            opt.push(this.operatorOptions[i]);
            continue;
          } else if (con.code.startsWith('$count(') && ['==', '!=', '>', '<', '<=', '>='].includes(this.operatorOptions[i].value)) {
            opt.push(this.operatorOptions[i]);
            continue;
          }
          if (
            this.codeRule &&
            this.codeRule[con.code] &&
            this.codeRule[con.code].allowOperator &&
            this.codeRule[con.code].allowOperator.includes(this.operatorOptions[i].value)
          ) {
            opt.push(this.operatorOptions[i]);
          } else {
            opt.push(this.operatorOptions[i]);
          }
        }
        return opt;
      }
      return this.operatorOptions;
    },
    addVisibleCondition() {
      this.configuration.defaultVisibleVar.conditions.push({
        code: undefined,
        operator: true,
        value: undefined
      });
    },

    widgetMethodOptions() {
      if (EASY_ENV_IS_NODE || this.widget == undefined) {
        return [];
      }

      let options = [],
        comp = window.Vue.options.components[this.widget.wtype],
        methods = comp.options.methods,
        META = comp.options.META;

      if (this.widget.wtype === 'WidgetTemplate') {
        // 模板组件，取模板中vue模板的方法
        if (this.widget.configuration.type === 'vue' && this.widget.configuration.templateName) {
          let wTemplate = document.querySelector(`[w-id='${this.widget.id}']`).__vue__.wTemplate;
          if (wTemplate && wTemplate.$options.methods) {
            methods = wTemplate.$options.methods;
            META = wTemplate.$options.META;
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

      // for (let m in methods) {
      //   options.push({
      //     label: m,
      //     value: m
      //   });
      // }

      return options;
    },
    filterOption(input, option) {
      if (option.componentOptions.tag == 'a-select-option') {
        let { title, value } = option.componentOptions.propsData;
        return value.toLowerCase().indexOf(input.toLowerCase()) >= 0 || (title && title.toLowerCase().indexOf(input.toLowerCase()) >= 0);
      }
      return false;
    },
    cascadeObjectKeyPath(obj, keyPaths, parentKeyPath) {
      for (let k in obj) {
        if (['string', 'number', 'boolean'].includes(typeof obj[k])) {
          keyPaths.push(parentKeyPath ? `${parentKeyPath}.${k}` : k);
        } else if (!Array.isArray(obj[k])) {
          // 非数组对象则继续遍历路径
          this.cascadeObjectKeyPath(obj[k], keyPaths, parentKeyPath ? `${parentKeyPath}.${k}` : k);
        }
      }
    },
    refetchPageVarKeyPaths: debounce(function (v) {
      this.dataSource = this.designer.pageVarKeyPaths();
    }, 500),
    changeOperatorSelected(condition) {
      this.$set(condition, 'value', undefined);
    }
  },
  beforeMount() {},
  mounted() {
    // 修复旧配置表单字段未使用嵌套对象路径表示
    if (this.configuration.defaultVisibleVar && this.configuration.defaultVisibleVar.conditions && this.underDyformScope) {
      for (let c of this.configuration.defaultVisibleVar.conditions) {
        if (c.code && !c.code.includes('.')) {
          c.code = `_FORM_DATA_.${c.code}`;
        }
      }
    }
  }
};
</script>
