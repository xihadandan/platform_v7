<template>
  <div v-if="configuration.apiInvocationConfig != undefined" class="api-operation-dataset">
    <a-steps v-model="currentStep" size="small" type="navigation">
      <a-step status="finish" title="API 调用配置">
        <a-icon slot="icon" type="api" />
      </a-step>
      <a-step status="finish" title="API 调用前处理">
        <a-icon slot="icon" type="code" />
      </a-step>
      <a-step status="finish" title="API 返回数据转换">
        <a-icon slot="icon" type="interaction" />
      </a-step>
      <a-step status="finish" title="API 调用结束">
        <a-icon slot="icon" type="export" />
      </a-step>
    </a-steps>
    <Scroll style="height: calc(100vh - 200px); padding-top: 15px; padding-right: 12px">
      <div v-show="currentStep == 0">
        <a-card size="small" title="API服务接口" style="margin-bottom: 12px">
          <a-select
            style="width: 100%"
            placeholder="请选择服务接口"
            v-model="configuration.apiInvocationConfig.apiOperationUuid"
            @change="onChangeApiOperation"
            :showSearch="true"
            :filterOption="filterSelectOption"
          >
            <a-select-opt-group v-for="(item, i) in apiLinks" :key="item.uuid">
              <span slot="label">
                {{ item.name }}
              </span>
              <a-select-option v-for="(opr, o) in item.apiOperations" :key="opr.uuid" :value="opr.uuid" :data="opr">
                <div>
                  {{ opr.name }}
                  <div style="color: var(--w-link-color)">
                    <a-tag :color="methodColor[opr.method]">{{ opr.method }}</a-tag>
                    {{ opr.path }}
                  </div>
                </div>
              </a-select-option>
            </a-select-opt-group>
          </a-select>
          <div
            style="padding-top: 12px"
            v-if="configuration.apiInvocationConfig.apiOperationUuid != undefined && apiOperation.apiLink != undefined"
          >
            <a-tag :color="methodColor[apiOperation.method]">{{ apiOperation.method }}</a-tag>
            {{ getApiOperationUrl(apiOperation) }}
          </div>
        </a-card>
        <a-card size="small" title="服务参数配置" v-if="configuration.apiInvocationConfig.apiOperationUuid != undefined">
          <a-tabs :key="key" :default-active-key="apiOperation.apiLink.protocol == 'REST' ? 'Params' : 'Body'" size="small">
            <a-tab-pane key="Params" tab="查询参数 (Query)" v-if="apiOperation.apiLink.protocol == 'REST'">
              <a-table
                size="small"
                :columns="queryParamColumns"
                :data-source="queryParams"
                rowKey="id"
                :pagination="false"
                :locale="{ emptyText: '无数据' }"
              >
                <template slot="title">Query 参数</template>
                <template slot="nameSlot" slot-scope="text, record, index">
                  <a-input v-model="record.name" />
                </template>
                <template slot="valueSlot" slot-scope="text, record, index">
                  <ValueBinding :configuration="record" :designer="designer" />
                </template>
                <template slot="exampleValueSlot" slot-scope="text, record, index">
                  <a-input v-model="record.exampleValue" />
                </template>
                <template slot="remarkSlot" slot-scope="text, record, index">
                  <a-input v-model="record.remark" />
                </template>

                <template slot="rowOperationsSlot" slot-scope="text, record, index">
                  <a-button icon="delete" type="link" size="small" @click="deleteParameter(record)"></a-button>
                </template>
                <template slot="footer">
                  <a-button
                    type="link"
                    size="small"
                    icon="plus"
                    @click="
                      addParameter({
                        paramType: 'query'
                      })
                    "
                  >
                    Query 参数
                  </a-button>
                </template>
              </a-table>

              <a-table
                size="small"
                v-show="pathVariables.length > 0"
                :columns="queryParamColumns"
                :data-source="pathVariables"
                rowKey="id"
                :pagination="false"
                :locale="{ emptyText: '无数据' }"
                style="margin-top: 12px"
              >
                <template slot="title">路径参数 (Path)</template>
                <template slot="nameSlot" slot-scope="text, record, index">
                  {{ record.name }}
                </template>
                <template slot="valueSlot" slot-scope="text, record, index">
                  <ValueBinding :configuration="record" :designer="designer" />
                </template>
                <template slot="exampleValueSlot" slot-scope="text, record, index">{{ record.exampleValue }}</template>
                <template slot="remarkSlot" slot-scope="text, record, index">
                  {{ record.remark }}
                </template>
              </a-table>
            </a-tab-pane>
            <a-tab-pane
              key="Body"
              tab="请求体参数 (Body)"
              :forceRender="true"
              v-if="apiOperation.method == 'POST' || apiOperation.method == 'PUT' || apiOperation.method == 'PATCH'"
            >
              <!-- <div style="margin-bottom: 12px; padding-bottom: 12px; border-bottom: 1px solid rgb(234, 234, 234); text-align: right">
                <label style="color: #999">请求体格式: {{ configuration.apiInvocationConfig.reqFormatType }}</label>
              </div> -->
              <JsonDataSchemaValueBuild
                v-show="configuration.apiInvocationConfig.reqFormatType == 'json'"
                ref="reqApiBodySchema"
                :key="'reqSchema_' + key"
                :initValue="{
                  schema: configuration.apiInvocationConfig.reqSchema,
                  exampleBody: apiOperation.reqExampleBody
                }"
                :designer="designer"
                @change="e => onChangeJsonDataSchemaValue(e, 'reqSchema')"
              />
              <a-table
                v-show="
                  configuration.apiInvocationConfig.reqFormatType == 'form-data' ||
                  configuration.apiInvocationConfig.reqFormatType == 'x-www-form-urlencoded'
                "
                size="small"
                :columns="queryParamColumns"
                :data-source="bodyParams"
                rowKey="id"
                :pagination="false"
                :locale="{ emptyText: '无数据' }"
              >
                <template slot="nameSlot" slot-scope="text, record, index">
                  <a-input v-model="record.name">
                    <template slot="suffix" v-if="defaultParameterNameTypeMap[record.name + ':' + record.paramType]">
                      <a-tag style="margin-right: 0px; line-height: 22px">
                        {{
                          { string: '字符', file: '附件', number: '数字', boolean: '布尔' }[
                            defaultParameterNameTypeMap[record.name + ':' + record.paramType].dataType || 'string'
                          ]
                        }}
                      </a-tag>
                    </template>
                  </a-input>
                </template>
                <template slot="valueSlot" slot-scope="text, record, index">
                  <ValueBinding :configuration="record" :designer="designer" />
                </template>
                <template slot="exampleValueSlot" slot-scope="text, record, index">
                  <a-input v-model="record.exampleValue" />
                </template>
                <template slot="remarkSlot" slot-scope="text, record, index">
                  <a-input v-model="record.remark" />
                </template>
                <template slot="rowOperationsSlot" slot-scope="text, record, index">
                  <a-button icon="delete" type="link" size="small" @click="deleteParameter(record)"></a-button>
                </template>
                <template slot="footer">
                  <a-button
                    type="link"
                    size="small"
                    icon="plus"
                    @click="
                      addParameter({
                        paramType: 'body'
                      })
                    "
                  >
                    参数
                  </a-button>
                </template>
              </a-table>
            </a-tab-pane>
            <a-tab-pane key="Headers" tab="请求头参数 (Headers)">
              <a-table
                size="small"
                :columns="queryParamColumns"
                :data-source="headers"
                rowKey="id"
                :pagination="false"
                :locale="{ emptyText: '无数据' }"
              >
                <template slot="title">Headers 参数</template>
                <template slot="nameSlot" slot-scope="text, record, index">
                  <a-auto-complete v-model="record.name" :data-source="commonHeaders" style="width: 200px" :filterOption="true" />
                </template>
                <template slot="valueSlot" slot-scope="text, record, index">
                  <ValueBinding :configuration="record" :designer="designer" />
                </template>
                <template slot="exampleValueSlot" slot-scope="text, record, index">
                  <a-input v-model="record.exampleValue" />
                </template>
                <template slot="remarkSlot" slot-scope="text, record, index">
                  <a-input v-model="record.remark" />
                </template>
                <template slot="rowOperationsSlot" slot-scope="text, record, index">
                  <a-button icon="delete" type="link" size="small" @click="deleteParameter(record)"></a-button>
                </template>
                <template slot="footer">
                  <a-button
                    type="link"
                    size="small"
                    icon="plus"
                    @click="
                      addParameter({
                        paramType: 'header'
                      })
                    "
                  >
                    Header 参数
                  </a-button>
                </template>
              </a-table>
            </a-tab-pane>

            <a-tab-pane key="response" tab="返回响应" :forceRender="true">
              <JsonDataSchemaValueBuild
                v-if="configuration.apiInvocationConfig.resFormatType == 'JSON'"
                :key="'resSchema_' + key"
                ref="resApiBodySchema"
                :initValue="{
                  exampleBody: apiOperation.resExampleBody,
                  schema: apiOperation.resSchema
                }"
                :designer="designer"
                :options="{
                  editable: false
                }"
                :scroll="{
                  y: 300
                }"
                @change="e => onChangeJsonDataSchemaValue(e, 'resSchema')"
              />
            </a-tab-pane>
          </a-tabs>
        </a-card>
      </div>

      <div v-show="currentStep == 1">
        <a-alert type="info" message="入参 request 对象, 其中包含属性" style="margin-bottom: 8px">
          <template slot="description">
            <ul>
              <li>queryParams: 查询参数</li>
              <li>headers: 头部参数</li>
              <li>pathParams: url路径参数</li>
              <li>body: 请求体参数</li>
            </ul>
            <p>可通过返回 promise 对象, 用于实现调用前的异步逻辑</p>
          </template>
        </a-alert>
        <WidgetCodeEditor v-model="configuration.apiInvocationConfig.beforeInvokeScript" width="100%" />
      </div>
      <div v-show="currentStep == 2">
        <div style="text-align: center; margin-bottom: 12px">
          <a-radio-group v-model="configuration.apiInvocationConfig.dataTransformMethod" button-style="solid" class="sub-radio-group">
            <a-radio-button value="setSchemaValue">数据结构赋值</a-radio-button>
            <a-radio-button value="function">自定义转换逻辑</a-radio-button>
            <a-radio-button :value="undefined">无需数据转换</a-radio-button>
          </a-radio-group>
        </div>
        <JsonDataSchemaValueBuild
          :key="'transform_' + key"
          v-if="
            configuration.apiInvocationConfig.resFormatType == 'JSON' &&
            configuration.apiInvocationConfig.dataTransformMethod == 'setSchemaValue'
          "
          ref="transformApiBodySchema"
          :designer="designer"
          :options="{
            jsonPaths: apiResponseJsonPaths
          }"
          :initValue="{
            schema: resTransformSchema
          }"
          @change="e => onChangeJsonDataSchemaValue(e, 'resTransformSchema')"
        />
        <template v-else-if="configuration.apiInvocationConfig.dataTransformMethod == 'function'">
          <slot name="dataTransformFunctionPrefix"></slot>
          <WidgetCodeEditor v-model="configuration.apiInvocationConfig.dataTransformFunction" width="100%" />
        </template>

        <a-result v-else title="无数据转换处理" subTitle="以 API 返回的数据结果为准"></a-result>
      </div>
      <div v-show="currentStep == 3">
        <div style="text-align: center; margin-bottom: 12px">
          <a-radio-group v-model="configuration.apiInvocationConfig.endAction.actionType" button-style="solid" class="sub-radio-group">
            <a-radio-button value="widgetEvent">触发组件事件</a-radio-button>
            <a-radio-button value="jsFunction">脚本代码</a-radio-button>
            <a-radio-button :value="undefined">结束调用</a-radio-button>
          </a-radio-group>
        </div>
        <a-result
          status="success"
          title="API 执行结束"
          v-show="configuration.apiInvocationConfig.endAction.actionType == undefined"
        ></a-result>

        <JsHookSelect
          v-if="configuration.apiInvocationConfig.endAction.actionType == 'jsFunction'"
          :designer="designer"
          :widget="widget"
          v-model="configuration.apiInvocationConfig.endAction.jsFunction"
        />

        <div v-if="configuration.apiInvocationConfig.endAction.actionType === 'widgetEvent'">
          <!-- <a-form-model-item label="选择组件">
            <a-select
              :style="{ width: '100%' }"
              :options="vEvtWidgetOptions"
              v-model="configuration.apiInvocationConfig.endAction.eventWid"
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
              v-model="configuration.apiInvocationConfig.endAction.eventId"
              @change="onChangeWidgetEvent"
              :getPopupContainer="getSelectPopupContainer"
            ></a-select>
            <WidgetDesignModal v-if="evtWidgetParamCompName != undefined">
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
                    :eventHandler="configuration.apiInvocationConfig.endAction"
                    :fromWidget="widget"
                  ></component>
                </div>
              </template>
              <a-button type="link" size="small">
                <Icon type="pticon iconfont icon-ptkj-shezhi"></Icon>
                配置
              </a-button>
            </WidgetDesignModal>
          </a-form-model-item> -->

          <template v-for="(evt, t) in configuration.apiInvocationConfig.endAction.widgetEvent">
            <div
              style="
                text-align: left;
                padding: 12px;
                border: 1px solid #e8e8e8;
                border-radius: 4px;
                position: relative;
                margin-bottom: 12px;
              "
            >
              <a-button
                size="small"
                type="link"
                style="position: absolute; top: 0px; right: 0px"
                @click="configuration.apiInvocationConfig.endAction.widgetEvent.splice(t, 1)"
                title="删除"
              >
                <Icon type="pticon iconfont icon-ptkj-shanchu" />
              </a-button>
              <a-form-model :colon="false" class="api-operation-we-form-model">
                <a-form-model-item :label-col="{ style: { width: '100%', textAlign: 'left' } }">
                  <template slot="label">
                    <a-checkbox v-model="evt.condition.enable" />
                    满足
                    <a-select
                      size="small"
                      :getPopupContainer="getPopupContainer()"
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
                            <a-input v-model="item.code" :style="{ width: '30%' }" placeholder="通过json路径获取数据值" />
                            <a-select
                              :getPopupContainer="getPopupContainer()"
                              :options="operatorOptions"
                              v-model="item.operator"
                              :style="{ width: '40%' }"
                            />
                            <a-input v-model="item.value" v-show="!['true', 'false'].includes(item.operator)" :style="{ width: '30%' }" />
                          </a-input-group>
                        </a-col>
                        <a-col flex="25px">
                          <a-button type="link" @click="evt.condition.conditions.splice(i, 1)" title="删除">
                            <Icon type="pticon iconfont icon-ptkj-shanchu" />
                          </a-button>
                        </a-col>
                      </a-row>
                    </template>
                  </div>
                </a-form-model-item>

                <a-form-model-item label="选择组件">
                  <a-select
                    :getPopupContainer="getPopupContainer()"
                    :style="{ width: '100%' }"
                    :options="vWidgetOptions"
                    v-model="evt.eventWid"
                    show-search
                    :filter-option="filterSelectOption"
                    @change="e => changeWidgetEventParams(evt)"
                  ></a-select>
                </a-form-model-item>
                <a-form-model-item label="选择组件事件">
                  <a-select
                    :getPopupContainer="getPopupContainer()"
                    :key="'selectWid_' + t + evt.eventWid"
                    :style="{ width: evtWidgetParamCompName(evt) != undefined ? 'calc(100% - 65px)' : '100%' }"
                    :options="getWidgetCustomEvents(evt.eventWid)"
                    @change="e => changeWidgetEventParams(evt)"
                    v-model="evt.eventId"
                  ></a-select>
                  <WidgetDesignModal :z-index="2000" v-if="evtWidgetParamCompName(evt) != undefined" :key="evt.eventWid + '_' + t">
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
                          :is="evtWidgetParamCompName(evt)"
                          :widget="evtWidget(evt)"
                          :designer="designer"
                          :eventHandler="evt"
                          :fromWidget="widget"
                        ></component>
                      </div>
                    </template>
                    <a-button type="link" size="small">
                      <Icon type="pticon iconfont icon-ptkj-shezhi" />
                      配置
                    </a-button>
                  </WidgetDesignModal>
                </a-form-model-item>
                <a-form-model-item label="组件事件参数" v-if="evt.eventWid && evt.eventId">
                  <a-table
                    size="small"
                    :locale="{ emptyText: '暂无数据' }"
                    rowKey="id"
                    :pagination="false"
                    :columns="eventParamColumns"
                    :data-source="evt.eventParams"
                  >
                    <template slot="paramValueTitleSlot">
                      参数值
                      <a-popover placement="bottom" v-if="hasWgtEvtParamsHelpSlot" :overlayStyle="{ 'z-index': 2001 }">
                        <template slot="content">
                          <slot name="eventParamValueHelpSlot"></slot>
                        </template>
                        <template slot="title">
                          <span>帮助文档</span>
                        </template>
                        <Icon type="pticon iconfont icon-ptkj-xinxiwenxintishi"></Icon>
                        <!-- <a-icon type="info-circle" theme="twoTone" /> -->
                      </a-popover>
                    </template>
                    <template slot="paramKeySlot" slot-scope="text, record">
                      <span style="color: red; line-height: 32px" v-if="record.required">*</span>
                      <a-input v-model="record.paramKey" style="width: 100px; float: right" />
                    </template>
                    <template slot="paramValueSlot" slot-scope="text, record">
                      <a-auto-complete :data-source="record.valueScope" v-model.trim="record.paramValue" allow-clear />
                    </template>
                    <template slot="remarkSlot" slot-scope="text, record, index">
                      <div style="display: flex; align-items: baseline">
                        <a-input style="width: 150px" v-model="record.remark" />
                        <a-icon
                          @click="evt.eventParams.splice(index, 1)"
                          type="minus-circle"
                          theme="filled"
                          style="width: 30px; color: var(--w-danger-color)"
                        />
                      </div>
                    </template>
                    <template slot="footer">
                      <div style="text-align: right">
                        <a-button type="link" size="small" @click="e => addEventParams(evt.eventParams)">
                          <Icon type="pticon iconfont icon-ptkj-jiahao" />
                          添加参数
                        </a-button>
                      </div>
                    </template>
                  </a-table>
                </a-form-model-item>
              </a-form-model>
            </div>
          </template>
          <a-button :block="true" type="link" @click="e => addWidgetActionEvent(configuration.apiInvocationConfig.endAction)">
            <Icon type="pticon iconfont icon-ptkj-jiahao" />
            添加动作事件
          </a-button>
        </div>
      </div>
    </Scroll>
  </div>
</template>
<style lang="less">
.api-operation-dataset {
  .sub-radio-group {
    display: flex;
    width: 100%;
    > label {
      flex: 1;
    }
  }
  .api-operation-we-form-model {
    padding: 0px 8px;
    .ant-form-item {
      margin-bottom: 5px !important;
      > .ant-form-item-label {
        width: 140px;
        display: block;
        text-align: left;
        float: left;
      }

      > .ant-form-item-control-wrapper {
        width: e('calc(100% - 140px)');
        display: block;
        float: left;
      }
    }
  }
}
</style>
<script type="text/babel">
import { generateId, copyToClipboard } from '@framework/vue/utils/util';
import { filterSelectOption } from '@framework/vue/utils/function.js';

import ApiBodySchema from '@admin/app/web/template/api-link/component/api-body-schema.vue';
import { merge, cloneDeep, upperFirst } from 'lodash';
import ValueBinding from './value-binding.vue';
import JsonDataSchemaValueBuild from './json-data-schema-value-build.vue';
import WidgetCodeEditor from '@pageWidget/commons/widget-code-editor.vue';

export default {
  name: 'ApiOperationDataset',
  inject: ['appId'],
  props: {
    widget: Object,
    configuration: Object,
    designer: Object,
    apiResultTransformSchema: Object
  },
  components: { ApiBodySchema, ValueBinding, JsonDataSchemaValueBuild, WidgetCodeEditor },
  computed: {
    queryParams() {
      if (this.configuration.apiInvocationConfig == undefined) {
        return [];
      }
      return this.configuration.apiInvocationConfig.parameters.filter(p => p.paramType == 'query');
    },
    headers() {
      if (this.configuration.apiInvocationConfig == undefined) {
        return [];
      }
      return this.configuration.apiInvocationConfig.parameters.filter(p => p.paramType == 'header');
    },
    pathVariables() {
      if (this.configuration.apiInvocationConfig == undefined) {
        return [];
      }
      return this.configuration.apiInvocationConfig.parameters.filter(p => p.paramType == 'path');
    },
    bodyParams() {
      if (this.configuration.apiInvocationConfig == undefined) {
        return [];
      }
      return this.configuration.apiInvocationConfig.parameters.filter(p => p.paramType == 'body');
    },

    methodColor() {
      return {
        GET: '#17b26a',
        POST: '#ef6820',
        DELETE: '#f04438',
        PUT: '#2e90fa',
        OPTIONS: '#2e90fa'
      };
    },

    defaultParameterNameTypeMap() {
      let map = {};
      if (this.parameters) {
        for (let p of this.parameters) {
          map[p.name + ':' + p.paramType] = p;
        }
      }
      return map;
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

          options.push({ label: w.configuration.name || w.title, value: w.id });
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
            if (tag == 'a-skeleton' && !_v.$refs.wTemplate) {
              _v = _v.$parent;
              tag = _v.$parent.$options._componentTag;
            }
            if (tag == this.widget.configuration.templateName) {
              if (_v.$parent.$options.methods) {
                META = _v.$parent.$options.META;
              }
            } else if (_v.$vnode.componentOptions.tag == this.widget.configuration.templateName) {
              META = _v.$options.META;
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
    }
  },
  data() {
    return {
      currentStep: 0,
      key: new Date().getTime(),
      apiLinkTreeData: [],
      apiLinks: [],
      parameters: [],
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
      apiOperation: {
        uuid: undefined,
        method: 'POST',
        parameters: [],
        resSchema: {},
        reqSchema: {},
        reqFormatType: 'json',
        resFormatType: 'JSON',
        resExampleBody: undefined,
        reqExampleBody: undefined,
        apiLink: {}
      },
      resTransformSchema: {},
      queryParamColumns: [
        {
          title: '参数名',
          dataIndex: 'name',
          width: 180,
          scopedSlots: { customRender: 'nameSlot' }
        },
        {
          title: '值',
          dataIndex: 'valueSlot',
          scopedSlots: { customRender: 'valueSlot' }
        },
        {
          title: '示例值',
          width: 120,
          dataIndex: 'exampleValue',
          scopedSlots: { customRender: 'exampleValueSlot' }
        },
        {
          title: '说明',
          width: 120,
          dataIndex: 'remark',
          scopedSlots: { customRender: 'remarkSlot' }
        },
        {
          title: '  ',
          width: 60,
          dataIndex: 'rowOperations',
          scopedSlots: { customRender: 'rowOperationsSlot' }
        }
      ],
      commonHeaders: [
        // 请求控制
        'Host', // 目标域名（HTTP/1.1 必需字段）
        'Connection', // 控制连接（如 `keep-alive` 或 `close`）
        'Cache-Control', // 缓存策略（如 `no-cache`）

        // 内容协商
        'Accept', // 客户端接受的响应类型（如 `application/json`）
        'Accept-Encoding', // 支持的压缩编码（如 `gzip`）
        'Accept-Language', // 优先语言（如 `en-US`）
        'Accept-Charset', // 优先字符集（如 `utf-8`）

        // 身份认证
        'Authorization', // 凭证（如 `Bearer <token>`）
        'Cookie', // 客户端发送的 Cookie

        // 请求来源
        'Referer', // 来源页面 URL
        'User-Agent', // 客户端标识（如浏览器信息）

        // 请求体相关
        'Content-Type', // 请求体的媒体类型（如 `application/json`）
        'Content-Length', // 请求体的字节长度

        // 代理和调试
        'X-Forwarded-For', // 客户端原始 IP（代理链中使用）
        'X-Request-ID' // 请求唯一标识（用于追踪）
      ],
      // apiOperations: []
      apiResponseJsonPaths: [],
      widgetEventParamComps: [],
      eventParamColumns: [
        { title: '参数', dataIndex: 'param', scopedSlots: { customRender: 'paramKeySlot' } },
        {
          dataIndex: 'paramValue',
          scopedSlots: { customRender: 'paramValueSlot' },
          slots: { title: 'paramValueTitleSlot' }
        },
        { title: '备注', dataIndex: 'remark', scopedSlots: { customRender: 'remarkSlot' } }
      ],
      hasWgtEvtParamsHelpSlot: this.$slots.eventParamValueHelpSlot && this.$slots.eventParamValueHelpSlot.length > 0
    };
  },
  beforeCreate() {},
  created() {
    if (!this.configuration.hasOwnProperty('apiInvocationConfig')) {
      this.$set(this.configuration, 'apiInvocationConfig', {
        beforeInvokeScript: undefined,
        apiOperationUuid: undefined,
        apiLinkUuid: undefined,
        parameters: [],
        reqFormatType: undefined,
        resFormatType: 'JSON',
        dataTransformMethod: 'setSchemaValue',
        endAction: {
          actionType: undefined,
          widgetEvent: []
        }
      });
    }

    let resTransformSchema = merge(this.configuration.apiInvocationConfig.resTransformSchema || {}, this.apiResultTransformSchema || {});
    for (let k in resTransformSchema) {
      this.$set(this.resTransformSchema, k, resTransformSchema[k]);
    }
  },
  beforeMount() {
    this.fetchApiLinks();
  },
  mounted() {
    if (this.configuration.apiInvocationConfig.apiOperationUuid != undefined) {
      this.fetchApiOperation(this.configuration.apiInvocationConfig.apiOperationUuid).then(d => {
        this.apiOperation = d;
        this.key = new Date().getTime();
        this.parameters.splice(0, this.parameters.length);
        this.parameters.push(...this.apiOperation.parameters);
        if (this.apiOperation.bodySchema) {
          for (let b of this.apiOperation.bodySchema) {
            if (b.applyTo == 'response') {
              if (b.schemaConfig) {
                this.apiResponseJsonPaths.splice(0, this.apiResponseJsonPaths.length);
                this.apiOperation.resSchema = JSON.parse(b.schemaDefinition);
                if (this.apiOperation.resSchema != undefined) {
                  this.apiResponseJsonPaths.push(...this.extractSchemaPaths(this.apiOperation.resSchema));
                  console.log('解析得json路径: ', this.apiResponseJsonPaths);
                }
              }
              if (b.exampleBody) {
                this.apiOperation.resExampleBody = b.exampleBody;
              }
            }
          }
        }
        this.configuration.apiInvocationConfig.reqFormatType = this.apiOperation.reqFormatType;
        this.configuration.apiInvocationConfig.resFormatType = this.apiOperation.resFormatType;
      });
    }

    let components = window.Vue.options.components;
    for (let key in components) {
      if (key.endsWith('EventConfiguration') && key.startsWith('Widget')) {
        this.widgetEventParamComps.push(key);
      }
    }
  },
  methods: {
    getPopupContainer() {
      return triggerNode => triggerNode.closest('.ant-form-item');
    },
    getSelectPopupContainer() {
      // 下拉框渲染到当前dom域内且AForm设置为相对定位，避免事件处理渲染在滚动的弹窗内时候菜单不跟随滚动问题
      return this.$el;
    },
    onChangeWidgetEventW() {
      // 切换组件后，清除事件和它对应的配置
      this.$set(this.configuration.apiInvocationConfig.endAction, 'eventId', undefined);
      delete this.configuration.apiInvocationConfig.endAction.wEventParams;
      this.getWidgetEventParams();
    },
    onChangeWidgetEvent() {
      this.getWidgetEventParams();
    },
    getWidgetEventParams() {
      if (
        this.configuration.apiInvocationConfig.endAction.eventWid &&
        this.configuration.apiInvocationConfig.endAction.eventId &&
        this.designer
      ) {
        let w = this.designer.widgetIdMap[this.configuration.apiInvocationConfig.endAction.eventWid];
        let modelParams = this.configuration.apiInvocationConfig.endAction.eventParams,
          eventParams = [],
          params = {};

        if (w) {
          let defaultEvents = this.designer.widgetDefaultEvents[w.id];
          if (defaultEvents) {
            for (let i = 0, len = defaultEvents.length; i < len; i++) {
              if (
                defaultEvents[i].id === this.configuration.apiInvocationConfig.endAction.eventId &&
                defaultEvents[i].eventParams != undefined
              )
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
                w.configuration.defineEvents[i].id === this.configuration.apiInvocationConfig.endAction.eventId &&
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
        this.configuration.apiInvocationConfig.endAction.eventParams = eventParams;
      }
    },
    extractSchemaPaths(schema, currentPath = '', underArray = false, keys) {
      const options = [];
      if (keys == undefined) {
        keys = [];
      }
      // 处理普通对象属性
      if (schema.properties) {
        for (const [key, value] of Object.entries(schema.properties)) {
          const newPath = currentPath ? `${currentPath}.${key}` : key;

          // 处理数组类型的属性
          if (value.type === 'array') {
            // continue;
            if (!keys.includes(newPath)) {
              keys.push(newPath);
              options.push({
                label: `${newPath}`,
                value: `${newPath}`,
                description: value.description
              });
            }

            // 递归处理数组元素
            if (value.items) {
              options.push(...this.extractSchemaPaths(value.items, `${newPath}`, true, keys));
            }
          } else if (value.properties) {
            // 递归处理对象属性
            if (!keys.includes(newPath)) {
              keys.push(newPath);
              options.push({
                label: `${newPath}`,
                value: `${newPath}`,
                description: value.description
              });
            }
            options.push(...this.extractSchemaPaths(value, newPath, false, keys));
          } else {
            // 处理基本类型属性
            if (!keys.includes(newPath)) {
              keys.push(newPath);
              options.push({
                label: `${newPath}`,
                value: `${newPath}`,
                description: [value.description || '', underArray ? ' (返回数组内的所有该属性值)' : ''].join('')
              });
            }
            // 为数组内的属性生成特殊选项
            if (underArray) {
              if (!keys.includes(`_$loopItem.${key}`)) {
                keys.push(`_$loopItem.${key}`);
                options.push({
                  label: `_$loopItem.${key}`,
                  value: `_$loopItem.${key}`,
                  description: [value.description || '', underArray ? ' (访问当前遍历的数据项对象属性)' : ''].join('')
                });
              }
            }
          }
        }
      }

      return options;
    },

    filterSelectOption(input, option) {
      if (option.tag && option.tag.endsWith('ASelectOption')) {
        let data = option.data.attrs.data;
        if (data.name.toLowerCase().indexOf(input.toLowerCase()) >= 0 || data.path.toLowerCase().indexOf(input.toLowerCase()) >= 0) {
          return true;
        }
      }
      return false;
    },
    propertyEditable(item) {
      return {
        propertyNameEditable: true,
        propertyTypeEditable: true,
        propertyDescriptionEditable: true,
        propertyAddable: true,
        propertyDeletable: true
      };
    },
    getApiOperationUrl(item) {
      let key = {
        local: 'devEndpoint',
        prod: 'endpoint',
        unittest: 'testEndpoint',
        stag: 'stagEndpoint'
      }[window.__INITIAL_STATE__._CONTEXT_STATE_.ENV];
      if (key == undefined) {
        key = 'endpoint';
      }
      return item.apiLink != undefined && item.apiLink[key] != undefined
        ? (item.apiLink[key].endsWith('/') ? item.apiLink[key].substring(0, item.apiLink[key].length - 1) : item.apiLink[key]) + item.path
        : item.path;
    },
    deleteParameter(item) {
      for (let i = 0, len = this.configuration.apiInvocationConfig.parameters.length; i < len; i++) {
        if (this.configuration.apiInvocationConfig.parameters[i].id == item.id) {
          this.configuration.apiInvocationConfig.parameters.splice(i, 1);
          break;
        }
      }
    },
    addParameter(origin) {
      this.configuration.apiInvocationConfig.parameters.push(
        Object.assign(
          {
            id: generateId() + '_custom',
            name: undefined,
            remark: undefined,
            exampleValue: undefined
          },
          origin
        )
      );
    },
    onChangeApiOperation() {
      this.configuration.apiInvocationConfig.parameters.splice(0, this.configuration.apiInvocationConfig.parameters.length);
      this.configuration.apiInvocationConfig.reqSchema = undefined;
      this.fetchApiOperation(this.configuration.apiInvocationConfig.apiOperationUuid).then(d => {
        this.apiOperation = d;
        this.key = new Date().getTime();
        this.parameters.splice(0, this.parameters.length);
        this.parameters.push(...this.apiOperation.parameters);
        if (this.apiOperation.parameters) {
          this.configuration.apiInvocationConfig.parameters.push(...this.apiOperation.parameters);
        }

        if (this.apiOperation.bodySchema) {
          for (let b of this.apiOperation.bodySchema) {
            if (b.applyTo == 'request') {
              if (b.exampleBody) {
                this.apiOperation.reqExampleBody = b.exampleBody;
              }
              if (b.schemaDefinition) {
                this.configuration.apiInvocationConfig.reqSchema = JSON.parse(b.schemaDefinition);
              }
            } else if (b.applyTo == 'response') {
              if (b.schemaConfig) {
                this.apiResponseJsonPaths.splice(0, this.apiResponseJsonPaths.length);
                if (b.schemaDefinition) {
                  this.apiOperation.resSchema = JSON.parse(b.schemaDefinition);
                  if (this.apiOperation.resSchema != undefined) {
                    this.apiResponseJsonPaths.push(...this.extractSchemaPaths(this.apiOperation.resSchema));
                    console.log('解析得json路径: ', this.apiResponseJsonPaths);
                  }
                }
              }
              if (b.exampleBody) {
                this.apiOperation.resExampleBody = b.exampleBody;
              }
            }
          }
        }
        this.configuration.apiInvocationConfig.reqFormatType = this.apiOperation.reqFormatType;
        this.configuration.apiInvocationConfig.resFormatType = this.apiOperation.resFormatType;
      });
    },
    fetchApiOperation(apiOperationUuid) {
      return new Promise((resolve, reject) => {
        $axios
          .get(`/proxy/api/apiLink/operationDetails/${apiOperationUuid}`, { params: {} })
          .then(({ data }) => {
            resolve(data.data);
          })
          .catch(error => {});
      });
    },
    fetchApiLinks() {
      $axios
        .get(`/proxy/api/apiLink/getApiLinksByAppId`, { params: { appId: this.appId } })
        .then(({ data }) => {
          if (data.data) {
            this.apiLinks.push(...data.data);
          }
        })
        .catch(error => {});
    },
    onChangeJsonDataSchemaValue(e, prop) {
      this.$set(this.configuration.apiInvocationConfig, prop, e.schema);
      if (prop == 'resTransformSchema') {
        for (let key in e.schema) {
          this.$set(this.resTransformSchema, key, e.schema[key]);
        }
      }
      // this.configuration.apiInvocationConfig[prop.replace('Rows', '')] = e.schema;
    },

    addEventParams(params) {
      params.push({
        id: generateId(),
        paramKey: undefined,
        paramValue: undefined
      });
    },
    addWidgetActionEvent(item) {
      if (item.widgetEvent == undefined) {
        this.$set(item, 'widgetEvent', []);
      }
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
      }
      return options;
    },
    evtWidget(evt) {
      if (evt.eventWid && evt.eventId) {
        return this.designer.widgetIdMap[evt.eventWid];
      }
    },
    evtWidgetParamCompName(evt) {
      if (evt.eventWid && evt.eventId) {
        let wtype = this.designer.widgetIdMap[evt.eventWid] && this.designer.widgetIdMap[evt.eventWid].wtype;
        let compName = `${wtype}${upperFirst(evt.eventId)}EventConfiguration`;
        if (this.widgetEventParamComps.includes(compName)) {
          return compName;
        }
      }
      return undefined;
    }
  }
};
</script>
