<template>
  <div>
    <a-collapse :bordered="false" expandIconPosition="right" v-model="activeKey">
      <a-collapse-panel key="1" header="创建过程事件" :style="customStyle" v-if="allowRegisterEvent">
        <a-table
          rowKey="id"
          size="small"
          :pagination="false"
          :bordered="false"
          :showHeader="false"
          :data-source="lifecycleHookEventRows"
          :columns="defineEventsColumns"
          class="widget-event-conf-custom-event-table no-border"
        >
          <template slot="titleSlot" slot-scope="text, record">
            {{ text }}
          </template>

          <template slot="operationSlot" slot-scope="text, record, index">
            <a-button
              v-show="!lifecycleIds.includes(record.id)"
              size="small"
              title="复制代码"
              type="link"
              @click.stop="evt => copyEventCode(evt, record)"
            >
              <Icon type="pticon iconfont icon-ptkj-fuzhi" />
            </a-button>

            <WidgetCodeEditor v-model="record.customScript">
              <!-- 私有的事件，默认执行自定义代码以及二开脚本里面的同名方法 -->
              <a-button size="small" title="编写代码" type="link" icon="code"></a-button>
            </WidgetCodeEditor>
          </template>
        </a-table>
      </a-collapse-panel>
      <a-collapse-panel key="2" :style="customStyle" v-if="allowRegisterEvent">
        <template slot="header">
          <a-popover placement="left" title="说明">
            <template slot="content">
              <p>通过注册指定逻辑的事件，提供给脚本或者其他组件触发联动效果</p>
            </template>
            <Icon type="pticon iconfont icon-ptkj-xinxiwenxintishi"></Icon>
          </a-popover>
          注册动作事件
        </template>
        <a-table
          rowKey="id"
          size="small"
          :pagination="false"
          :bordered="false"
          :showHeader="false"
          :data-source="widgetEventRows"
          :columns="defineEventsColumns"
          class="widget-event-conf-custom-event-table no-border"
        >
          <template slot="titleSlot" slot-scope="text, record">
            <a-input size="small" :style="{ width: '180px' }" v-model="record.title" v-if="!defaultIds.includes(record.id)" />
            <span v-else>
              {{ text }}
            </span>
          </template>

          <template slot="operationSlot" slot-scope="text, record, index">
            <a-button size="small" title="复制代码" type="link" @click.stop="evt => copyEventCode(evt, record)">
              <Icon type="pticon iconfont icon-ptkj-fuzhi" />
            </a-button>
            <a-button v-show="!defaultIds.includes(record.id)" size="small" title="删除" type="link" @click="delEvent(record, index)">
              <Icon type="pticon iconfont icon-ptkj-shanchu" />
            </a-button>

            <!-- 自定义代码 -->
            <WidgetDesignDrawer
              :id="'widgetEventConfigurationMore' + record.id"
              title="更多"
              :designer="designer"
              :zIndex="1010"
              :width="800"
              v-if="!defaultIds.includes(record.id)"
            >
              <a-button size="small" title="更多设置" type="link">
                <Icon type="pticon iconfont icon-ptkj-gengduocaozuo" />
              </a-button>
              <template slot="content">
                <a-form-model :label-col="{ span: 6 }" :wrapper-col="{ span: 15 }">
                  <a-form-model-item label="事件执行">
                    <a-radio-group v-model="record.codeSource" button-style="solid">
                      <a-radio-button value="codeEditor">自定义代码</a-radio-button>
                      <a-radio-button value="developJsFileCode">JS模块</a-radio-button>
                      <a-radio-button value="widgetMethod">组件方法</a-radio-button>
                      <a-radio-button value="widgetEvent">组件动作事件</a-radio-button>
                    </a-radio-group>
                  </a-form-model-item>
                  <a-form-model-item label="编写代码" v-show="record.codeSource === 'codeEditor'">
                    <WidgetCodeEditor v-model="record.customScript" width="auto" height="200px" :snippets="codeSnippets" />
                  </a-form-model-item>
                  <a-form-model-item label="脚本代码" v-show="record.codeSource === 'developJsFileCode'">
                    <a-select
                      :getPopupContainer="getPopupContainer()"
                      :filter-option="filterOption"
                      showSearch
                      allowClear
                      :style="{ width: '100%' }"
                      :options="jsOptions"
                      v-model="record.jsFunction"
                    ></a-select>
                  </a-form-model-item>
                  <a-form-model-item label="组件方法" v-show="record.codeSource === 'widgetMethod'">
                    <a-select
                      :getPopupContainer="getPopupContainer()"
                      showSearch
                      allowClear
                      :style="{ width: '100%' }"
                      :options="widgetMethodOptions"
                      v-model="record.widgetMethod"
                    ></a-select>
                  </a-form-model-item>

                  <template v-if="record.codeSource === 'widgetEvent'">
                    <template v-for="(evt, t) in record.widgetEvent">
                      <div style="padding: 12px; outline: 1px solid #e8e8e8; border-radius: 4px; position: relative; margin-bottom: 12px">
                        <a-button
                          size="small"
                          type="link"
                          style="position: absolute; top: 0px; right: 0px; z-index: 1"
                          @click="record.widgetEvent.splice(t, 1)"
                          title="删除"
                        >
                          <Icon type="pticon iconfont icon-ptkj-shanchu" />
                        </a-button>

                        <a-form-model-item :label-col="{ style: { width: '100%' } }">
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
                                    <a-select
                                      v-model="item.code"
                                      :allowClear="true"
                                      :showSearch="true"
                                      :filterOption="filterOption"
                                      :style="{ width: '30%' }"
                                      size="small"
                                      :getPopupContainer="getPopupContainer()"
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
                                      <a-select-opt-group v-if="fieldVarOptions.length > 0">
                                        <span slot="label">
                                          <a-icon type="code" />
                                          表单状态
                                        </span>
                                        <a-select-option readonly value="__DYFORM__.editable" title="表单可编辑">
                                          表单可编辑
                                        </a-select-option>
                                      </a-select-opt-group>
                                      <a-select-opt-group v-if="varOptions.length > 0">
                                        <span slot="label">
                                          <a-icon type="code" />
                                          参数
                                        </span>
                                        <a-select-option v-for="opt in varOptions" :key="opt.value" :title="opt.label">
                                          {{ opt.label }}
                                        </a-select-option>
                                      </a-select-opt-group>
                                    </a-select>
                                    <a-input v-else v-model="item.code" :style="{ width: '30%' }" size="small" />
                                    <a-select
                                      size="small"
                                      :getPopupContainer="getPopupContainer()"
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
                            :filter-option="filterOption"
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
                                <Icon type="pticon iconfont icon-ptkj-xinxiwenxintishi" title="帮助文档"></Icon>
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
                                <Icon type="pticon iconfont icon-ptkj-xinxiwenxintishi" title="帮助文档"></Icon>
                                <!-- <a-icon type="info-circle" theme="twoTone" /> -->
                              </a-popover>
                            </template>
                            <template slot="paramKeySlot" slot-scope="text, record">
                              <span style="color: red; line-height: 32px" v-if="record.required">*</span>
                              <a-input v-model="record.paramKey" style="width: 100px; float: right" />
                            </template>
                            <template slot="paramValueSlot" slot-scope="text, record">
                              <template v-if="originalEventParamValueSourceMap[evt.eventWid + evt.eventId + record.paramKey]">
                                <template v-if="originalEventParamValueSourceMap[evt.eventWid + evt.eventId + record.paramKey].valueSource">
                                  <a-select
                                    v-if="
                                      originalEventParamValueSourceMap[evt.eventWid + evt.eventId + record.paramKey].valueSource
                                        .inputType == 'select'
                                    "
                                    :getPopupContainer="getPopupContainer()"
                                    :options="
                                      originalEventParamValueSourceMap[evt.eventWid + evt.eventId + record.paramKey].valueSource.options ||
                                      []
                                    "
                                    v-model="record.paramValue"
                                    show-arrow
                                    allow-clear
                                  />
                                  <a-select
                                    v-else-if="
                                      originalEventParamValueSourceMap[evt.eventWid + evt.eventId + record.paramKey].valueSource
                                        .inputType == 'multi-select'
                                    "
                                    :options="
                                      originalEventParamValueSourceMap[evt.eventWid + evt.eventId + record.paramKey].valueSource.options ||
                                      []
                                    "
                                    :getPopupContainer="getPopupContainer()"
                                    v-model="record.paramValue"
                                    show-arrow
                                    allow-clear
                                    mode="multiple"
                                  />
                                  <a-select
                                    v-else-if="
                                      originalEventParamValueSourceMap[evt.eventWid + evt.eventId + record.paramKey].valueSource
                                        .inputType == 'group-select'
                                    "
                                    :getPopupContainer="getPopupContainer()"
                                    v-model="record.paramValue"
                                    show-arrow
                                    allow-clear
                                    mode="multiple"
                                  >
                                    <a-select-opt-group
                                      v-for="(grp, i) in originalEventParamValueSourceMap[evt.eventWid + evt.eventId + record.paramKey]
                                        .valueSource.options || []"
                                      :key="'optionGroup-' + grp.uuid"
                                    >
                                      <span slot="label" class="select-group-divider">
                                        <label :title="grp.label">{{ grp.label }}</label>
                                      </span>
                                      <a-select-option
                                        v-for="(opt, index) in grp.options"
                                        :key="opt.value"
                                        :value="opt.value"
                                        :title="opt.label"
                                      >
                                        {{ opt.label }}
                                      </a-select-option>
                                    </a-select-opt-group>
                                  </a-select>
                                  <a-tree-select
                                    v-else-if="
                                      originalEventParamValueSourceMap[evt.eventWid + evt.eventId + record.paramKey].valueSource
                                        .inputType == 'tree-select'
                                    "
                                    show-search
                                    treeNodeFilterProp="title"
                                    v-model="record.paramValue"
                                    :dropdownStyle="{ maxHeight: '400px', overflow: 'auto' }"
                                    :getPopupContainer="getPopupContainer()"
                                    multiple
                                    treeCheckable
                                    show-arrow
                                    allow-clear
                                    :tree-data="
                                      originalEventParamValueSourceMap[evt.eventWid + evt.eventId + record.paramKey].valueSource.options ||
                                      []
                                    "
                                    :replaceFields="
                                      originalEventParamValueSourceMap[evt.eventWid + evt.eventId + record.paramKey].replaceFields || {
                                        children: 'children',
                                        title: 'title',
                                        key: 'key',
                                        value: 'value'
                                      }
                                    "
                                    :showCheckedStrategy="
                                      originalEventParamValueSourceMap[evt.eventWid + evt.eventId + record.paramKey].showCheckedStrategy ||
                                      'SHOW_CHILD'
                                    "
                                  ></a-tree-select>
                                </template>

                                <a-auto-complete
                                  v-else
                                  :data-source="record.valueScope || []"
                                  v-model.trim="record.paramValue"
                                  allow-clear
                                />
                              </template>
                              <a-input v-else v-model.trim="record.paramValue" allow-clear />
                            </template>
                            <template slot="remarkSlot" slot-scope="text, record, index">
                              <div style="display: flex; align-items: baseline">
                                <a-input style="width: 150px" v-model="record.remark" />
                                <a-icon
                                  @click="evt.eventParams.splice(index, 1)"
                                  type="minus-circle"
                                  theme="filled"
                                  style="width: 30px; color: var(--w-danger-color)"
                                  title="删除"
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
                      </div>
                    </template>
                    <a-button :block="true" type="link" @click="e => addWidgetActionEvent(record)">
                      <Icon type="pticon iconfont icon-ptkj-jiahao" />
                      添加动作事件
                    </a-button>
                  </template>
                </a-form-model>
              </template>
            </WidgetDesignDrawer>
          </template>
          <div slot="footer" :style="{ padding: '10px' }">
            <a-button size="small" :block="true" @click="addEvent">
              <Icon type="pticon iconfont icon-ptkj-jiahao" />
              添加事件
            </a-button>
          </div>
        </a-table>
      </a-collapse-panel>

      <a-collapse-panel key="3" header="交互事件" v-if="widget.configuration.domEvents != undefined" :style="customStyle">
        <a-table
          rowKey="id"
          size="small"
          :pagination="false"
          :bordered="false"
          :showHeader="false"
          :data-source="widget.configuration.domEvents"
          :columns="defineEventsColumns"
          class="widget-event-conf-custom-event-table no-border"
        >
          <template slot="operationSlot" slot-scope="text, record, index">
            <WidgetDesignDrawer
              :id="'widgetEventConfigurationMore' + record.id"
              title="更多"
              :designer="designer"
              :zIndex="1010"
              :width="800"
            >
              <a-button size="small" title="更多设置" type="link">
                <Icon type="pticon iconfont icon-ptkj-gengduocaozuo" />
              </a-button>
              <template slot="content">
                <a-form-model :label-col="{ span: 6 }" :wrapper-col="{ span: 15 }">
                  <a-form-model-item>
                    <template slot="label">
                      <a-tooltip title="组件数据初始化阶段, 是否可以触发值变更事件">
                        初始化数据时可执行
                        <a-icon type="question-circle-o" />
                      </a-tooltip>
                    </template>
                    <a-switch v-model="record.changeTriggerOnDataInit" />
                  </a-form-model-item>
                  <a-form-model-item label="事件执行">
                    <a-radio-group @change="e => onChangeCodeSource(record)" v-model="domEventShowType">
                      <a-radio-button value="codeEditor">
                        <a-checkbox
                          :checked="record.codeSource.includes('codeEditor')"
                          @change="e => onChangeEventCodeSource(e, record, 'codeEditor')"
                        />
                        自定义代码
                      </a-radio-button>
                      <a-radio-button value="developJsFileCode">
                        <a-checkbox
                          :checked="record.codeSource.includes('developJsFileCode')"
                          @change="e => onChangeEventCodeSource(e, record, 'developJsFileCode')"
                        />
                        JS模块
                      </a-radio-button>
                      <a-radio-button value="widgetEvent">
                        <a-checkbox
                          :checked="record.codeSource.includes('widgetEvent')"
                          @change="e => onChangeEventCodeSource(e, record, 'widgetEvent')"
                        />
                        组件动作事件
                      </a-radio-button>
                      <a-radio-button value="pageEvent" v-if="configuration.isDatabaseField !== true">
                        <a-checkbox
                          :checked="record.codeSource.includes('pageEvent')"
                          @change="e => onChangeEventCodeSource(e, record, 'pageEvent')"
                        />
                        打开页面
                      </a-radio-button>
                    </a-radio-group>
                  </a-form-model-item>
                  <a-form-model-item label="编写代码" v-show="domEventShowType === 'codeEditor'">
                    <WidgetCodeEditor v-model="record.customScript" width="auto" height="200px" :snippets="codeSnippets" />
                  </a-form-model-item>
                  <a-form-model-item label="脚本代码" v-show="domEventShowType === 'developJsFileCode'">
                    <a-select
                      :getPopupContainer="getPopupContainer()"
                      :filter-option="filterOption"
                      showSearch
                      allowClear
                      :style="{ width: '100%' }"
                      :options="jsOptions"
                      v-model="record.jsFunction"
                    ></a-select>
                  </a-form-model-item>
                  <template v-if="domEventShowType === 'widgetEvent'">
                    <template v-for="(evt, t) in record.widgetEvent">
                      <div style="padding: 12px; outline: 1px solid #e8e8e8; border-radius: 4px; position: relative; margin-bottom: 12px">
                        <a-button
                          size="small"
                          type="link"
                          style="position: absolute; top: 0px; right: 0px; z-index: 1"
                          @click="record.widgetEvent.splice(t, 1)"
                          title="删除"
                        >
                          <Icon type="pticon iconfont icon-ptkj-shanchu" />
                        </a-button>

                        <a-form-model-item :label-col="{ style: { width: '100%' } }">
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
                                    <a-select
                                      v-model="item.code"
                                      :allowClear="true"
                                      :showSearch="true"
                                      :filterOption="filterOption"
                                      :style="{ width: '30%' }"
                                      size="small"
                                      :getPopupContainer="getPopupContainer()"
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
                                      <a-select-opt-group v-if="varOptions.length > 0">
                                        <span slot="label">
                                          <a-icon type="code" />
                                          参数
                                        </span>
                                        <a-select-option v-for="opt in varOptions" :key="opt.value" :title="opt.label">
                                          {{ opt.label }}
                                        </a-select-option>
                                      </a-select-opt-group>
                                    </a-select>
                                    <a-input v-else v-model="item.code" :style="{ width: '30%' }" size="small" />
                                    <a-select
                                      size="small"
                                      :getPopupContainer="getPopupContainer()"
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
                                    <Icon type="pticon iconfont icon-ptkj-shanchu" />
                                  </a-button>
                                </a-col>
                              </a-row>
                            </template>
                          </div>
                        </a-form-model-item>
                        <template v-if="evt.event != undefined">
                          <template v-for="(evtItem, itemIndex) in evt.event">
                            <div
                              :key="'widgetEventItem_' + itemIndex"
                              style="padding: 30px; background: #f9f9f9; position: relative; margin-bottom: 12px"
                            >
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
                                  :getPopupContainer="getPopupContainer()"
                                  :style="{ width: '100%' }"
                                  :options="vWidgetOptions"
                                  v-model="evtItem.eventWid"
                                  show-search
                                  :filter-option="filterOption"
                                  @change="e => changeWidgetEventParams(evtItem, 'eventWid')"
                                ></a-select>
                              </a-form-model-item>
                              <a-form-model-item label="选择组件事件">
                                <a-select
                                  :getPopupContainer="getPopupContainer()"
                                  :key="'selectWid_' + t + evtItem.eventWid"
                                  :style="{ width: evtWidgetParamCompName(evtItem) != undefined ? 'calc(100% - 65px)' : '100%' }"
                                  :options="getWidgetCustomEvents(evtItem.eventWid)"
                                  @change="e => changeWidgetEventParams(evtItem)"
                                  v-model="evtItem.eventId"
                                ></a-select>
                                <WidgetDesignModal
                                  :z-index="2000"
                                  v-if="evtWidgetParamCompName(evtItem) != undefined"
                                  :key="evtItem.eventWid + '_' + t + '_' + itemIndex"
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
                                      <Icon type="pticon iconfont icon-ptkj-xinxiwenxintishi" title="帮助文档"></Icon>
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
                                    <Icon type="pticon iconfont icon-ptkj-shezhi" />
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
                                    <a-popover placement="bottom" v-if="hasWgtEvtParamsHelpSlot" :overlayStyle="{ 'z-index': 2001 }">
                                      <template slot="content">
                                        <slot name="eventParamValueHelpSlot"></slot>
                                      </template>
                                      <template slot="title">
                                        <span>帮助文档</span>
                                      </template>
                                      <Icon type="pticon iconfont icon-ptkj-xinxiwenxintishi" title="帮助文档"></Icon>
                                      <!-- <a-icon type="info-circle" theme="twoTone" /> -->
                                    </a-popover>
                                  </template>
                                  <template slot="paramKeySlot" slot-scope="text, record">
                                    <span style="color: red; line-height: 32px" v-if="record.required">*</span>
                                    <a-input v-model="record.paramKey" style="width: 100px; float: right" />
                                  </template>
                                  <template slot="paramValueSlot" slot-scope="text, record">
                                    <template v-if="originalEventParamValueSourceMap[evtItem.eventWid + evtItem.eventId + record.paramKey]">
                                      <template
                                        v-if="
                                          originalEventParamValueSourceMap[evtItem.eventWid + evtItem.eventId + record.paramKey].valueSource
                                        "
                                      >
                                        <a-select
                                          v-if="
                                            originalEventParamValueSourceMap[evtItem.eventWid + evtItem.eventId + record.paramKey]
                                              .valueSource.inputType == 'select'
                                          "
                                          :getPopupContainer="getPopupContainer()"
                                          :options="
                                            originalEventParamValueSourceMap[evtItem.eventWid + evtItem.eventId + record.paramKey]
                                              .valueSource.options || []
                                          "
                                          v-model="record.paramValue"
                                          show-arrow
                                          allow-clear
                                        />
                                        <a-select
                                          v-else-if="
                                            originalEventParamValueSourceMap[evtItem.eventWid + evtItem.eventId + record.paramKey]
                                              .valueSource.inputType == 'multi-select'
                                          "
                                          :options="
                                            originalEventParamValueSourceMap[evtItem.eventWid + evtItem.eventId + record.paramKey]
                                              .valueSource.options || []
                                          "
                                          :getPopupContainer="getPopupContainer()"
                                          v-model="record.paramValue"
                                          show-arrow
                                          allow-clear
                                          mode="multiple"
                                        />
                                        <a-select
                                          v-else-if="
                                            originalEventParamValueSourceMap[evtItem.eventWid + evtItem.eventId + record.paramKey]
                                              .valueSource.inputType == 'group-select'
                                          "
                                          :getPopupContainer="getPopupContainer()"
                                          v-model="record.paramValue"
                                          show-arrow
                                          allow-clear
                                          mode="multiple"
                                        >
                                          <a-select-opt-group
                                            v-for="(grp, i) in originalEventParamValueSourceMap[
                                              evtItem.eventWid + evtItem.eventId + record.paramKey
                                            ].valueSource.options || []"
                                            :key="'optionGroup-' + grp.uuid"
                                          >
                                            <span slot="label" class="select-group-divider">
                                              <label :title="grp.label">{{ grp.label }}</label>
                                            </span>
                                            <a-select-option
                                              v-for="(opt, index) in grp.options"
                                              :key="opt.value"
                                              :value="opt.value"
                                              :title="opt.label"
                                            >
                                              {{ opt.label }}
                                            </a-select-option>
                                          </a-select-opt-group>
                                        </a-select>
                                        <a-tree-select
                                          v-else-if="
                                            originalEventParamValueSourceMap[evtItem.eventWid + evtItem.eventId + record.paramKey]
                                              .valueSource.inputType == 'tree-select'
                                          "
                                          show-search
                                          treeNodeFilterProp="title"
                                          v-model="record.paramValue"
                                          :dropdownStyle="{ maxHeight: '400px', overflow: 'auto' }"
                                          :getPopupContainer="getPopupContainer()"
                                          multiple
                                          treeCheckable
                                          show-arrow
                                          allow-clear
                                          :tree-data="
                                            originalEventParamValueSourceMap[evtItem.eventWid + evtItem.eventId + record.paramKey]
                                              .valueSource.options || []
                                          "
                                          :replaceFields="
                                            originalEventParamValueSourceMap[evtItem.eventWid + evtItem.eventId + record.paramKey]
                                              .replaceFields || {
                                              children: 'children',
                                              title: 'title',
                                              key: 'key',
                                              value: 'value'
                                            }
                                          "
                                          :showCheckedStrategy="
                                            originalEventParamValueSourceMap[evtItem.eventWid + evtItem.eventId + record.paramKey]
                                              .showCheckedStrategy || 'SHOW_CHILD'
                                          "
                                        ></a-tree-select>
                                        <a-auto-complete
                                          v-else
                                          :data-source="
                                            originalEventParamValueSourceMap[evtItem.eventWid + evtItem.eventId + record.paramKey]
                                              .valueScope || []
                                          "
                                          v-model.trim="record.paramValue"
                                          allow-clear
                                        />
                                      </template>
                                      <a-input v-else v-model="record.paramValue" allow-clear />
                                    </template>
                                    <a-input v-else v-model="record.paramValue" allow-clear />
                                  </template>
                                  <template slot="remarkSlot" slot-scope="text, record, index">
                                    <div style="display: flex; align-items: baseline">
                                      <a-input style="width: 150px" v-model="record.remark" />
                                      <a-icon
                                        @click="evtItem.eventParams.splice(index, 1)"
                                        type="minus-circle"
                                        theme="filled"
                                        style="width: 30px; color: var(--w-danger-color)"
                                        title="删除"
                                      />
                                    </div>
                                  </template>
                                  <template slot="footer">
                                    <div style="text-align: right">
                                      <a-button type="link" size="small" @click="e => addEventParams(evtItem.eventParams)">
                                        <Icon type="pticon iconfont icon-ptkj-jiahao" />
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
                    </template>
                    <a-button :block="true" type="link" @click="e => addWidgetActionEvent(record)">
                      <Icon type="pticon iconfont icon-ptkj-jiahao" />
                      添加一组组件动作事件
                    </a-button>
                  </template>
                  <template v-if="domEventShowType == 'pageEvent'">
                    <!-- FIXME: 待补充配置 -->
                    <a-form-model-item label="页面类型">
                      <a-radio-group
                        v-model="record.pageEvent.pageType"
                        :options="[
                          { label: '页面', value: 'page' },
                          { label: 'url', value: 'url' }
                        ]"
                      ></a-radio-group>
                    </a-form-model-item>

                    <a-form-model-item label="选择页面" v-show="record.pageEvent.pageType === 'page'">
                      <a-row type="flex">
                        <a-col flex="auto" :style="{ width: record.pageEvent.pageUuid ? 'calc(100% - 80px)' : '100%' }">
                          <a-select
                            allowClear
                            :filter-option="filterSelectOption"
                            :showSearch="true"
                            :style="{ width: '100%' }"
                            :options="pageOptions"
                            v-model="record.pageEvent.pageId"
                            @change="(v, node) => onSelectPageChange(v, node, record)"
                          ></a-select>
                        </a-col>
                        <a-col flex="70px" v-show="record.pageEvent.pageUuid">
                          <a-button type="link" @click.stop="e => onClickOpenDesignPage(record.pageEvent.pageUuid)">
                            <Icon type="pticon iconfont icon-szgy-zonghechaxun" />
                            查看
                          </a-button>
                        </a-col>
                      </a-row>
                    </a-form-model-item>
                    <a-form-model-item v-show="record.pageEvent.pageType === 'url'">
                      <template slot="label">
                        url地址
                        <slot name="urlHelpSlot"></slot>
                      </template>
                      <a-input v-model="record.pageEvent.url" />
                    </a-form-model-item>

                    <a-form-model-item label="页面参数">
                      <a-table
                        size="small"
                        :locale="{ emptyText: '暂无数据' }"
                        rowKey="id"
                        :pagination="false"
                        :columns="eventParamColumns"
                        :data-source="record.pageEvent.eventParams"
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
                            <Icon type="pticon iconfont icon-ptkj-xinxiwenxintishi" title="帮助文档"></Icon>
                          </a-popover>
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
                              :getPopupContainer="getPopupContainer()"
                              v-model="record.paramValue"
                              show-arrow
                              allow-clear
                            />
                            <a-select
                              v-else-if="record.valueSource.inputType == 'multi-select'"
                              :options="record.valueSource.options"
                              :getPopupContainer="getPopupContainer()"
                              v-model="record.paramValue"
                              show-arrow
                              allow-clear
                              mode="multiple"
                            />
                            <a-auto-complete v-else :data-source="record.valueScope || []" v-model.trim="record.paramValue" allow-clear />
                          </template>
                          <a-auto-complete v-else :data-source="record.valueScope || []" v-model.trim="record.paramValue" allow-clear />
                        </template>
                        <template slot="remarkSlot" slot-scope="text, record, index">
                          <div style="display: flex; align-items: baseline">
                            <a-input style="width: 150px" v-model="record.remark" />
                            <a-icon
                              @click="evt.eventParams.splice(index, 1)"
                              type="minus-circle"
                              theme="filled"
                              style="width: 30px; color: var(--w-danger-color)"
                              title="删除"
                            />
                          </div>
                        </template>
                        <template slot="footer">
                          <div style="text-align: right">
                            <a-button type="link" size="small" @click="e => addEventParams(record.pageEvent.eventParams)">
                              <Icon type="pticon iconfont icon-ptkj-jiahao" />
                              添加参数
                            </a-button>
                          </div>
                        </template>
                      </a-table>
                    </a-form-model-item>
                    <a-form-model-item label="目标位置">
                      <a-select
                        :style="{ width: '100%' }"
                        :options="[
                          { label: '新窗口', value: 'newWindow' },
                          { label: '当前窗口', value: 'currentWindow' },
                          { label: '当前布局', value: 'widgetLayout' },
                          { label: '标签页', value: 'widgetTab' },
                          { label: '弹窗', value: 'widgetModal' }
                        ]"
                        v-model="record.pageEvent.targetPosition"
                        @change="record.pageEvent.containerWid = undefined"
                      ></a-select>
                    </a-form-model-item>
                    <!-- 用于显示事件内容的标题 -->
                    <a-form-model-item label="目标标题" v-if="!['newWindow', 'currentWindow'].includes(record.pageEvent.targetPosition)">
                      <a-input v-model="record.pageEvent.title" />
                    </a-form-model-item>
                    <template v-if="!['newWindow', 'currentWindow'].includes(record.pageEvent.targetPosition)">
                      <a-form-model-item>
                        <template slot="label">
                          {{ targetPositionMap[record.pageEvent.targetPosition].label }}
                          <a-popover placement="topLeft" v-if="record.pageEvent.targetPosition == 'widgetLayout'">
                            <template slot="content">
                              <div style="width: 400px">
                                未选择布局的情况下, 将通过当前内容的父级布局进行内容渲染 , 当无任何父级布局时, 将以新窗口方式进行内容渲染。
                              </div>
                            </template>

                            <a-button type="link" size="small">
                              <Icon type="pticon iconfont icon-ptkj-xinxiwenxintishi"></Icon>
                            </a-button>
                          </a-popover>
                        </template>
                        <a-select
                          :style="{ width: '100%' }"
                          :key="record.pageEvent.targetPosition + '_' + record.id"
                          :options="getPageContainerOptions(record.pageEvent)"
                          v-model="record.pageEvent.containerWid"
                          allow-clear
                        ></a-select>
                      </a-form-model-item>
                      <a-form-model-item
                        style="display: flex"
                        :label-col="{ style: { width: '200px' } }"
                        :wrapper-col="{ style: { width: 'calc(100% - 200px)' } }"
                        v-if="record.pageEvent.targetPosition == 'widgetLayout' && record.pageEvent.pageType == 'page'"
                      >
                        <template slot="label">
                          当布局无标签页时打开方式
                          <a-popover placement="topLeft">
                            <template slot="content">
                              <div style="width: 400px">
                                当布局未开启动态标签页打开页面时候 , 可以通过设置该方式来打开页面 , 页面将不会覆盖布局内容区原有页面
                              </div>
                            </template>

                            <a-button type="link" size="small">
                              <Icon type="pticon iconfont icon-ptkj-xinxiwenxintishi"></Icon>
                            </a-button>
                          </a-popover>
                        </template>
                        <a-select
                          :style="{ width: '100%' }"
                          :options="[{ label: '浮层', value: 'drawer' }]"
                          v-model="record.pageEvent.layoutNoTabsOpenType"
                          allow-clear
                        ></a-select>
                      </a-form-model-item>
                      <a-form-model-item v-show="record.pageEvent.targetPosition == 'widgetLayout' && record.pageEvent.pageType == 'page'">
                        <template slot="label">
                          定位导航
                          <a-popover placement="left">
                            <template slot="content">页面跳转后, 将自动定位页面所属模块以及模块下对应该页面的导航</template>

                            <a-button type="link" size="small">
                              <Icon type="pticon iconfont icon-ptkj-xinxiwenxintishi"></Icon>
                            </a-button>
                          </a-popover>
                        </template>
                        <a-switch v-model="record.pageEvent.locateNavigation" />
                      </a-form-model-item>
                    </template>
                  </template>
                </a-form-model>
              </template>
            </WidgetDesignDrawer>
          </template>
        </a-table>
      </a-collapse-panel>
    </a-collapse>
  </div>
</template>
<style></style>
<script type="text/babel">
import { generateId, copyToClipboard } from '@framework/vue/utils/util';
import { upperFirst, findIndex, cloneDeep } from 'lodash';
import { filterSelectOption } from '@framework/vue/utils/function';

export default {
  name: 'WidgetEventConfiguration',
  mixins: [],
  inject: ['appId', 'subAppIds'],
  props: {
    designer: Object,
    widget: Object,
    widgetSource: Array,
    codeSnippets: Array,
    varOptions: { type: Array, default: [] },
    allowRegisterEvent: {
      type: Boolean,
      default: true
    },
    allowLifecycleEvent: {
      type: Boolean,
      default: true
    }
  },
  data() {
    return {
      activeKey: ['1', '2', '3'],
      customStyle: {
        'border-top': 'unset',
        'border-bottom': '1px solid #e5e5e5'
      },
      // rows: [],
      // jsHooks: [],
      defaultLifecycleEvents: [
        { id: 'created', title: '创建后', customScript: undefined },
        { id: 'beforeMount', title: '挂载前', customScript: undefined },
        { id: 'mounted', title: '挂载后', customScript: undefined }
      ],
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
      eventParamColumns: [
        { title: '参数', dataIndex: 'param', scopedSlots: { customRender: 'paramKeySlot' } },
        {
          dataIndex: 'paramValue',
          width: 200,
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
      targetPositionOptions: [
        { label: '新窗口', value: 'newWindow' },
        { label: '当前窗口', value: 'currentWindow' },
        { label: '当前布局', value: 'widgetLayout' },
        { label: '标签页', value: 'widgetTab' },
        { label: '弹窗', value: 'widgetModal' }
      ],
      widgetEventParamComps: [],
      hasWgtEvtParamsHelpSlot: this.$slots.eventParamValueHelpSlot && this.$slots.eventParamValueHelpSlot.length > 0,
      pageOptions: [],
      domEventShowType: 'codeEditor'
    };
  },

  beforeCreate() {},
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
    },
    lifecycleIds() {
      let ids = [];
      this.defaultLifecycleEvents.forEach(d => {
        ids.push(d.id);
      });
      return ids;
    },
    defaultEvents() {
      return this.designer.widgetDefaultEvents[this.widget.id] || [];
    },
    defaultIds() {
      let ids = [];
      if (this.defaultEvents) {
        this.defaultEvents.forEach(d => {
          ids.push(d.id);
        });
      }
      return ids;
    },
    configuration() {
      return this.widget.configuration;
    },
    lifecycleHookEventRows() {
      if (this.configuration.defineEvents) {
        return this.widget.configuration.defineEvents.slice(0, 3);
      }
      return [];
    },
    widgetEventRows() {
      let list = [].concat(this.defaultEvents);
      if (this.configuration.defineEvents) {
        return list.concat(this.widget.configuration.defineEvents.slice(3));
      }
      return list;
    },

    targetPositionMap() {
      let map = {};
      this.targetPositionOptions.forEach(o => {
        map[o.value] = o;
      });
      return map;
    },
    originalEventParamValueSourceMap() {
      let map = {};
      let domEvents = cloneDeep(this.widget.configuration.domEvents) || [];
      this.widgetEventRows.forEach(e => {
        if (!this.defaultIds.includes(e.id) && e.codeSource == 'widgetEvent') {
          domEvents.push({
            codeSource: ['widgetEvent'],
            widgetEvent: [{ event: e.widgetEvent }]
          });
        }
      });
      if (domEvents) {
        for (let d of domEvents) {
          if (d.codeSource != undefined && d.codeSource.includes('widgetEvent')) {
            for (let widgetEvent of d.widgetEvent) {
              for (let e of widgetEvent.event) {
                let wid = e.eventWid,
                  eventId = e.eventId;
                if (wid && eventId) {
                  // 组件事件参数
                  let w = this.designer.widgetIdMap[wid];
                  if (w) {
                    let defaultEvents = this.designer.widgetDefaultEvents[w.id];
                    if (defaultEvents) {
                      for (let i = 0, len = defaultEvents.length; i < len; i++) {
                        if (defaultEvents[i].id === eventId && defaultEvents[i].eventParams != undefined) {
                          for (let e = 0, elen = defaultEvents[i].eventParams.length; e < elen; e++) {
                            if (defaultEvents[i].eventParams[e]) {
                              map[`${wid}${eventId}${defaultEvents[i].eventParams[e].paramKey}`] = {
                                ...defaultEvents[i].eventParams[e]
                              };
                            }
                          }
                        }
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }

      return map;
    }
  },
  created() {
    if (this.configuration.defineEvents === undefined || this.configuration.defineEvents.length === 0) {
      // 默认生命周期事件
      this.$set(this.configuration, 'defineEvents', JSON.parse(JSON.stringify(this.defaultLifecycleEvents)));
    }
    if (this.configuration.isDatabaseField && (this.configuration.domEvents == undefined || this.configuration.domEvents.length == 0)) {
      this.$set(this.configuration, 'domEvents', [
        {
          id: 'onChange',
          title: '值变更时候触发',
          codeSource: ['codeEditor'],
          jsFunction: undefined,
          widgetEvent: [],
          customScript: undefined // 事件脚本
        }
        // {
        //   id: 'onFocus',
        //   title: '元素获得焦点时触发',
        //   codeSource: 'codeEditor',
        //   jsFunction: undefined,
        //   widgetEvent: [],
        //   customScript: undefined // 事件脚本
        // }
      ]);
    }
    // 处理旧数据
    if (this.configuration.domEvents && this.configuration.domEvents.length > 0) {
      for (let d of this.configuration.domEvents) {
        if (typeof d.codeSource == 'string') {
          this.$set(d, 'codeSource', [d.codeSource]);
        }
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
  methods: {
    getPageContainerOptions(pageEvent) {
      let options = [],
        { containerWid, targetPosition } = pageEvent;
      if (targetPosition != undefined) {
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
      }
      return options;
    },
    filterSelectOption,
    onChangeEventCodeSource(e, item, type) {
      if (item.codeSource.includes(type)) {
        item.codeSource.splice(item.codeSource.indexOf(type), 1);
      } else {
        item.codeSource.push(type);
      }

      if (item.codeSource.includes('pageEvent')) {
        if (!item.hasOwnProperty('pageEvent')) {
          this.$set(record, 'pageEvent', {
            pageId: undefined,
            pageUuid: undefined,
            eventParams: [],
            pageType: 'page',
            containerWid: undefined,
            targetPosition: 'newWindow'
          });
        }
      }
    },
    onChangeCodeSource(record) {},
    evtWidget(evt) {
      if (evt.eventWid && evt.eventId) {
        return this.designer.widgetIdMap[evt.eventWid];
      }
      return undefined;
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
    },
    getPopupContainer() {
      return triggerNode => triggerNode.closest('.ant-form-item');
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

    changeWidgetEventParams(evt, type) {
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
    onFocusSelectOption(evt, record) {
      let wid = evt.eventWid,
        eventId = evt.eventId,
        eventParams = evt.eventParams;
      // 重新加载备选项，且当前选项筛选key是options,更新最新的备选项列表
      if (wid && (eventId == 'refetchOption' || eventId == 'refreshOptions') && record.paramKey == 'options') {
        let w = this.designer.widgetIdMap[wid];
        if (w) {
          let defaultEvents = this.designer.widgetDefaultEvents[w.id];
          let hasIndex = findIndex(defaultEvents, e => {
            return e.id == 'refetchOption' || e.id == 'refreshOptions';
          });
          if (hasIndex > -1) {
            let _hasIndex = findIndex(defaultEvents[hasIndex].eventParams, e => {
              return e.paramKey == 'options';
            });
            if (
              _hasIndex > -1 &&
              JSON.stringify(record.valueSource.options) !==
                JSON.stringify(defaultEvents[hasIndex].eventParams[_hasIndex].valueSource.options)
            ) {
              record.valueSource.options = defaultEvents[hasIndex].eventParams[_hasIndex].valueSource.options;
            }
          }
        }
      }
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
    filterOption(input, option) {
      return (
        option.componentOptions.tag != 'a-select-opt-group' &&
        (option.componentOptions.children[0].text.toUpperCase().indexOf(input.toUpperCase()) >= 0 ||
          (option.key && option.key.indexOf(input.toUpperCase()) >= 0))
      );
    },
    copyEventCode(evt, item) {
      console.log(item);
      let _this = this;
      let id = item.id,
        text = `${this.widget.id}:${id}`;
      if (item.codeSnippet != undefined) {
        let codeSnippet = item.codeSnippet;
        text = codeSnippet.replace(/{{事件编码}}/g, `'${text}'`);
      }
      copyToClipboard(`${text}`, evt, function (success) {
        if (success) {
          _this.$message.success('已复制');
        }
      });
    },
    addEvent() {
      this.configuration.defineEvents.push({
        id: generateId(),
        title: undefined,
        codeSource: 'codeEditor',
        jsFunction: undefined,
        customScript: undefined // 事件脚本
      });
    },
    delEvent(record) {
      for (let i = 0, len = this.configuration.defineEvents.length; i < len; i++) {
        if (this.configuration.defineEvents[i].id === record.id) {
          this.configuration.defineEvents.splice(i, 1);
          break;
        }
      }
    },
    jsModuleChanged(v) {
      if (v) {
        let options = [];
        for (let i = 0, len = v.length; i < len; i++) {
          let _d = this.__developScript[v[i].key];
          if (_d) {
            try {
              let meta = _d.default.prototype.META;
              if (meta && meta.hook) {
                for (let h in meta.hook) {
                  options.push({ label: meta.hook[h], value: `${v[i].key}.${h}` });
                }
              }
            } catch (error) {
              console.error(`二开脚本 ${v[i].key} 解析脚本数据异常: `, error);
            }
          }
        }
        this.jsHooks = options;
      }
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
      }
    },

    onSelectPageChange(v, node, record) {
      record.pageEvent.pageName = null;
      if (v) {
        let uuid = node.data.props.uuid;
        record.pageEvent.pageUuid = uuid;
        record.pageEvent.pageName = node.componentOptions.children[0].text.trim(); // 获取页面定义json
        this.getPageDefinitionJson(uuid).then(data => {
          let json = JSON.parse(data.definitionJson);
          let pageParams = json.pageParams;
          if (pageParams) {
            record.pageEvent.eventParams = [];
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
              record.pageEvent.eventParams.push({
                paramKey: p.code,
                paramValue,
                required: p.required,
                valueScope: valueOptions
              });
            }
          }
        });
      } else {
        record.pageEvent.pageUuid = undefined;
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
    onClickOpenDesignPage(pageUuid) {
      if (pageUuid) {
        window.open('/page-designer/index?uuid=' + pageUuid, '_blank');
      }
    }
  },
  mounted() {
    let components = window.Vue.options.components;
    for (let key in components) {
      if (key.endsWith('EventConfiguration') && key.startsWith('Widget')) {
        this.widgetEventParamComps.push(key);
      }
    }

    this.fetchPageOptions();
  },

  watch: {
    // 'widget.configuration.jsModules': {
    //   deep: true,
    //   handler(v) {
    //     this.jsModuleChanged(v);
    //   }
    // }
  }
};
</script>
