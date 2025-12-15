<template>
  <!-- 计时表单 -->
  <PerfectScrollbar :style="{ height: '480px' }" ref="scroll">
    <a-form-model
      ref="form"
      :model="formData"
      :rules="rules"
      :colon="false"
      labelAlign="left"
      :label-col="{ span: 6 }"
      :wrapper-col="{ span: 17 }"
    >
      <a-form-model-item prop="name" label="计时器名称">
        <a-input v-model="formData.name" />
      </a-form-model-item>
      <a-form-model-item prop="timingModeType">
        <template slot="label">
          <label>计时方式</label>
          <a-tooltip placement="topRight" :arrowPointAtCenter="true">
            <div slot="title">
              <strong>工作日：</strong>
              根据工作时间中的工作日，以及每个工作日的工作时段进行计时
              <br />
              <strong>工作日（一天24小时）：</strong>
              根据工作时间中的工作日，每个工作日固定按24小时进行计时
              <br />
              <strong>自然日：</strong>
              根据自然日，每天固定按24小时进行计时
            </div>
            <a-icon type="info-circle" />
          </a-tooltip>
        </template>
        <div>
          <w-select
            v-model="formData.timingModeType"
            :options="timingModeOptions"
            :disabled="disableds.timingModeType"
            :placeholder="rules['timingModeType']['message']"
            @change="changeTimingMode"
            style="width: 190px"
          />
          <w-select
            v-show="formData.timingModeType === timingModeOptions[3]['id']"
            v-model="formData.limitUnitField"
            :options="dynamicFieldOptions"
            style="width: 190px"
          />
          <a-tooltip placement="topRight" :arrowPointAtCenter="true">
            <div slot="title">
              计时方式真实值：
              <span v-for="(item, index) in timingModeOptions" :key="item.value">
                <template v-if="index < 3">{{ `${index === 0 ? '' : '、'}${item.text}` }}（{{ item.id }}）</template>
              </span>
            </div>
            <a-icon type="info-circle" v-show="formData.timingModeType === timingModeOptions[3]['id']" />
          </a-tooltip>
          <a-button type="danger" @click="handleTimerService">
            <Icon type="iconfont icon-ptkj-yinyong" />
            引用计时服务
          </a-button>
        </div>
        <div>
          <span style="padding-right: 4px">启动后</span>
          <a-radio-group v-model="formData.includeStartTimePoint" :disabled="disableds.includeStartTimePoint">
            <a-radio value="0">
              <span style="padding-right: 7px">从下一</span>
              <w-select
                v-model="formData.timingModeUnit"
                :options="timingModeUnitOptions"
                :disabled="disableds.timingModeUnit"
                :allowClear="false"
                style="width: 130px"
              />
              <span>开始计时</span>
            </a-radio>
            <a-radio value="1">立即开始计时</a-radio>
          </a-radio-group>
        </div>
        <div v-if="formData.timingModeType !== '3'">
          到期时间在非工作时间时
          <w-checkbox v-model="formData.autoDelay" :disabled="disableds.autoDelay">自动推迟到下一工作时间的起始点前</w-checkbox>
        </div>
      </a-form-model-item>

      <a-form-model-item prop="limitTimeType" label="时限类型">
        <w-select
          v-model="formData.limitTimeType"
          :options="timeLimitType"
          :disabled="disableds.limitTimeType"
          :placeholder="rules['limitTimeType']['message']"
          @change="changeLimitTimeType"
        />
      </a-form-model-item>
      <template v-if="formData.limitTimeType === '1'">
        <!-- 固定时限 -->
        <a-form-model-item prop="limitTime1" label="时限">
          <div class="limit-time-fixed">
            <a-input v-model="formData.limitTime1" :disabled="disableds.limitTime1" />
            <w-select
              v-model="formData.timeLimitUnit"
              :disabled="disableds.timeLimitUnit"
              :options="optionsTimeLimitUnit1"
              class="limit-time-right"
            />
          </div>
        </a-form-model-item>
      </template>
      <template v-else-if="formData.limitTimeType === '4'">
        <!-- 固定截止时间 -->
        <a-form-model-item prop="limitTime1" label="截止时间">
          <div class="limit-time-fixed">
            <w-select v-model="formData.timeLimitUnit" :disabled="disableds.timeLimitUnit" :options="optionsTimeLimitUnit4" />
            <a-date-picker
              v-model="dateLimitTime1"
              :format="getFixedFormat"
              :showTime="showTime"
              :allowClear="true"
              :disabled="disableds.limitTime1"
              @change="changeDate"
              class="limit-time-right"
            />
          </div>
        </a-form-model-item>
      </template>
      <template v-else-if="formData.limitTimeType === '2'">
        <!-- 动态时限 -->
        <a-form-model-item prop="limitTime1" label="时限字段">
          <div class="limit-time-fixed">
            <w-select v-model="formData.limitTime" :options="dynamicLimitTimeOptions" />
            <w-select v-model="formData.timeLimitUnit" :options="optionsTimeLimitUnit1" class="limit-time-right" />
          </div>
        </a-form-model-item>
      </template>
      <template v-else-if="formData.limitTimeType === '3'">
        <!-- 动态截止时间 -->
        <a-form-model-item prop="limitTime1" label="截止时间字段">
          <div class="limit-time-fixed">
            <w-select v-model="formData.timeLimitUnit" :options="optionsTimeLimitUnit4" />
            <w-select v-model="formData.limitTime" :options="dynamicEndOptions" class="limit-time-right" />
          </div>
        </a-form-model-item>
      </template>

      <a-form-model-item prop="workTimePlanId" label="工作时间">
        <w-select
          v-model="formData.workTimePlanId"
          :options="workTimeOptions"
          :placeholder="rules['workTimePlanId']['message']"
          :formData="formData"
          formDataFieldName="workTimePlanName"
          @change="changeWorkTimePlanId"
        />
      </a-form-model-item>
      <a-form-model-item prop="tasks" label="计时环节(普通)">
        <node-task-select
          v-model="formData.tasks"
          mode="multiple"
          :isUnit="true"
          placeholder="请选择计时环节"
          :filterId="existTimerTaskId"
          @dropdownVisibleChange="onDropdownVisibleChange"
          :getPopupContainer="getPopupContainer"
        />
      </a-form-model-item>
      <a-form-model-item label="计时环节(子流程)">
        <flow-timer-subflow v-model="formData.subTasks" :formData="formData" />
      </a-form-model-item>
      <a-form-model-item prop="enableAlarm" label="预警提醒">
        <w-switch v-model="formData.enableAlarm" @change="changeEnableAlarm" />
      </a-form-model-item>
      <template v-if="formData.enableAlarm === '1'">
        <div class="ant-form-item">
          <div class="timer-warn-container">
            <div class="timer-warn-item" v-for="(record, index) in formData.alarmElements" :key="index">
              <div class="timer-warn-top">
                <div class="timer-warn-label">提前</div>
                <w-select
                  v-model="record.alarmTime"
                  :options="dynamicFieldOptions"
                  v-show="record.alarmTimeType === fieldAndConstantOptions[1]['value']"
                  class="select-dynamic-field"
                />
                <a-input
                  v-model="record.alarmTime"
                  class="timer-warn-time"
                  v-show="record.alarmTimeType !== fieldAndConstantOptions[1]['value']"
                />
                <w-select
                  v-model="record.alarmTimeType"
                  :options="fieldAndConstantOptions"
                  :allowClear="false"
                  @change="() => (record.alarmTime = '')"
                  class="select-field-constant"
                />
                <w-select
                  v-model="record.alarmUnit"
                  :options="dynamicFieldOptions"
                  v-show="record.alarmUnitType === fieldAndConstantOptions[1]['value']"
                  class="select-dynamic-field"
                  style="margin-left: 7px"
                />
                <w-select
                  v-model="record.alarmUnit"
                  :options="warnMsgUnitList"
                  class="timer-warn-msg-unit"
                  v-show="record.alarmUnitType !== fieldAndConstantOptions[1]['value']"
                />
                <a-tooltip placement="topRight" :arrowPointAtCenter="true">
                  <div slot="title">
                    真实值：
                    <span v-for="(item, index) in warnMsgUnitList" :key="item.value">
                      {{ `${index === 0 ? '' : '、'}${item.text}` }}（{{ item.id }}）
                    </span>
                  </div>
                  <a-icon type="info-circle" v-show="record.alarmUnitType === fieldAndConstantOptions[1]['value']" />
                </a-tooltip>
                <w-select
                  v-model="record.alarmUnitType"
                  :options="fieldAndConstantOptions"
                  :allowClear="false"
                  @change="() => (record.alarmUnit = '')"
                  class="select-field-constant"
                />
                <div>开始消息提醒，共</div>
                <w-select
                  v-model="record.alarmFrequency"
                  :options="dynamicFieldOptions"
                  v-show="record.alarmFrequencyType === fieldAndConstantOptions[1]['value']"
                  class="select-dynamic-field"
                />
                <a-input
                  v-model="record.alarmFrequency"
                  class="timer-warn-frequency"
                  v-show="record.alarmFrequencyType !== fieldAndConstantOptions[1]['value']"
                />
                <w-select
                  v-model="record.alarmFrequencyType"
                  :options="fieldAndConstantOptions"
                  :allowClear="false"
                  @change="() => (record.alarmFrequency = '')"
                  class="select-field-constant"
                />
                <div>次</div>
              </div>
              <div class="timer-warn-bottom">
                <div class="timer-warn-label">提醒：</div>
                <div class="timer-warn-content">
                  <a-checkbox-group
                    v-model="record.msgUserType"
                    :options="warnMsgUserTypeList"
                    @change="checkedValue => changeAlarmUserType(checkedValue, record, index)"
                  />
                  <template v-if="record.msgUserType.includes(warnMsgUserTypeOther)">
                    <user-select-list v-model="record.alarmUsers" types="unit/bizOrg/field/task/custom/filter" text="其他人员" />
                  </template>
                </div>
              </div>
              <div class="timer-warn-del">
                <a-button type="link" size="small" @click="delAlarm(record, index)">
                  <Icon type="pticon iconfont icon-ptkj-shanchu" />
                </a-button>
              </div>
            </div>
            <a-button type="link" @click="addAlarm" icon="plus">添加消息提醒</a-button>
          </div>
        </div>
      </template>
      <a-form-model-item prop="enableDueDoing" label="逾期提醒">
        <w-switch v-model="formData.enableDueDoing" />
      </a-form-model-item>
      <template v-if="formData.enableDueDoing === '1'">
        <div class="ant-form-item">
          <div class="timer-warn-container timer-due-container">
            <div class="timer-warn-item">
              <div class="timer-warn-top">
                <div class="timer-warn-label timer-due-label">消息催办：每</div>
                <w-select
                  v-model="formData.dueTime"
                  :options="dynamicFieldOptions"
                  v-show="formData.dueTimeType === fieldAndConstantOptions[1]['value']"
                  class="select-dynamic-field"
                />
                <a-input
                  v-model="formData.dueTime"
                  class="timer-warn-time"
                  v-show="formData.dueTimeType !== fieldAndConstantOptions[1]['value']"
                />
                <w-select
                  v-model="formData.dueTimeType"
                  :options="fieldAndConstantOptions"
                  :allowClear="false"
                  @change="() => (formData.dueTime = '')"
                  class="select-field-constant"
                />
                <w-select
                  v-model="formData.dueUnit"
                  :options="dynamicFieldOptions"
                  v-show="formData.dueUnitType === fieldAndConstantOptions[1]['value']"
                  class="select-dynamic-field"
                  style="margin-left: 7px"
                />
                <w-select
                  v-model="formData.dueUnit"
                  :options="warnMsgUnitList"
                  class="timer-warn-msg-unit"
                  v-show="formData.dueUnitType !== fieldAndConstantOptions[1]['value']"
                />
                <a-tooltip placement="topRight" :arrowPointAtCenter="true">
                  <div slot="title">
                    真实值：
                    <span v-for="(item, index) in warnMsgUnitList" :key="item.value">
                      {{ `${index === 0 ? '' : '、'}${item.text}` }}（{{ item.id }}）
                    </span>
                  </div>
                  <a-icon type="info-circle" v-show="formData.dueUnitType === fieldAndConstantOptions[1]['value']" />
                </a-tooltip>
                <w-select
                  v-model="formData.dueUnitType"
                  :options="fieldAndConstantOptions"
                  :allowClear="false"
                  @change="() => (formData.dueUnit = '')"
                  class="select-field-constant"
                />
                <div>自动催办在办人员一次，共</div>
                <w-select
                  v-model="formData.dueFrequency"
                  :options="dynamicFieldOptions"
                  v-show="formData.dueFrequencyType === fieldAndConstantOptions[1]['value']"
                  class="select-dynamic-field"
                />
                <a-input
                  v-model="formData.dueFrequency"
                  class="timer-warn-frequency"
                  v-show="formData.dueFrequencyType !== fieldAndConstantOptions[1]['value']"
                />
                <w-select
                  v-model="formData.dueFrequencyType"
                  :options="fieldAndConstantOptions"
                  :allowClear="false"
                  @change="() => (formData.dueFrequency = '')"
                  class="select-field-constant"
                />
                <div>次</div>
              </div>
              <div class="timer-warn-bottom">
                <div class="timer-warn-label">消息通知：</div>
                <div class="timer-warn-content">
                  <a-checkbox-group v-model="dueMsgUserType" :options="warnMsgUserTypeList" @change="changeDueUserType" />
                  <template v-if="dueMsgUserType.includes(warnMsgUserTypeOther)">
                    <user-select-list v-model="formData.dueUsers" types="unit/bizOrg/field/task/custom/filter" text="其他人员" />
                  </template>
                </div>
              </div>
            </div>
          </div>
        </div>
      </template>
      <more-show-component position="bottom" @change="updateScroll">
        <a-form-model-item label="逾期时">
          <w-select v-model="formData.dueAction" :options="dueActionOptions" :getPopupContainer="getPopupContainer" />
          <template v-if="formData.dueAction === '4'">
            <user-select-list v-model="formData.dueToUsers" types="unit/bizOrg/field/task/custom/filter" />
          </template>
          <template v-if="formData.dueAction === '16'">
            <w-select
              v-model="formData.dueToTask"
              :options="dueToTaskOptions"
              :replaceFields="replaceFields"
              :getPopupContainer="getPopupContainer"
            />
          </template>
        </a-form-model-item>
        <a-form-model-item label="计时结束设置">
          <a-radio-group v-model="formData.timeEndType" size="small">
            <a-radio v-for="item in timeEndTypeConfig" :key="item.value" :value="item.value">
              {{ item.label }}
            </a-radio>
          </a-radio-group>
          <template v-if="formData.timeEndType === '2'">
            <edge-direction-select v-model="formData.overDirections" mode="multiple" :getPopupContainer="getPopupContainer" />
          </template>
        </a-form-model-item>
        <a-form-model-item label="其他设置">
          <div>
            <w-checkbox v-model="formData.autoUpdateLimitTime">
              自动更新时限
              <a-tooltip placement="topRight" :arrowPointAtCenter="true">
                <div slot="title">设置时限的字段值变更时，自动更新时限；设置固定时限值时，可通过代码调用自动变更时限</div>
                <a-icon type="info-circle" />
              </a-tooltip>
            </w-checkbox>
          </div>
          <div>
            <w-checkbox v-model="formData.ignoreEmptyLimitTime">
              时限为空时不计时
              <a-tooltip placement="topRight" :arrowPointAtCenter="true">
                <div slot="title">设置时限的字段值为空时，本计时器不计时</div>
                <a-icon type="info-circle" />
              </a-tooltip>
            </w-checkbox>
          </div>
        </a-form-model-item>
      </more-show-component>
    </a-form-model>
    <modal title="引用计时服务" v-model="visible" :maxHeight="580" :width="1100" wrapperClass="flow-timer-modal-wrap">
      <template slot="content">
        <flow-timer-service v-if="visible" :uuid="formData.timerConfigUuid" ref="timerService" />
      </template>
      <template slot="footer">
        <a-button type="primary" @click="confirmTimerService">确定</a-button>
        <a-button :disabled="formData.introductionType === ''" @click="cancelTimerService">取消引用</a-button>
        <a-button @click="closeModal">取消</a-button>
      </template>
    </modal>
  </PerfectScrollbar>
</template>

<script>
import constant, {
  timingMode,
  timingModeUnit1,
  timingModeUnit2,
  timingModeUnit3,
  timeLimitType,
  timeLimitUnit1,
  timeLimitUnit4,
  warnMsgUnitList,
  warnMsgUserTypeList,
  warnMsgUserTypeOther,
  timeEndTypeConfig,
  dueActionOptions,
  fieldAndConstantOptions,
  timingModeUnitOptions
} from '../designer/constant';
import WSwitch from '../components/w-switch';
import WCheckbox from '../components/w-checkbox';
import WSelect from '../components/w-select';
import NodeTaskSelect from '../commons/node-task-select';
import moment from 'moment';
import Modal from '@pageAssembly/app/web/lib/modal.vue';
import FlowTimerService from './flow-timer-service.vue';
import UserSelectList from '../commons/user-select-list.vue';
import EdgeDirectionSelect from '../commons/edge-direction-select.vue';
import FlowTimerSubflow from './flow-timer-subflow.vue';
import MoreShowComponent from '../commons/more-show-component.vue';
import WI18nInput from '@framework/vue/designer/w-i18n-input.vue';

export default {
  name: 'FlowTimerInfo',
  inject: ['designer', 'workFlowData', 'graph'],
  props: {
    formData: {
      type: Object,
      default: () => {}
    },
    graphItem: {
      type: Object,
      default: () => {}
    }
  },
  data() {
    if (!this.formData.hasOwnProperty('timingMode')) {
      this.$set(this.formData, 'timingMode', '');
    }

    const limitUnit = this.formData.limitUnit;
    if (['3', '1', '2'].includes(limitUnit)) {
      this.formData.timingMode = '1';
    } else if (['13', '11', '12'].includes(limitUnit)) {
      this.formData.timingMode = '2';
    } else if (['86400', '3600', '60'].includes(limitUnit)) {
      this.formData.timingMode = '3';
    }

    if (!this.formData.timingModeType) {
      if (this.formData.limitUnitField) {
        this.formData.timingMode = timingMode[3]['id'];
      }
      this.formData.timingModeType = this.formData.timingMode;
    }
    if (!this.formData.timingModeUnit) {
      if (['3', '13', '86400'].includes(limitUnit)) {
        this.formData.timingModeUnit = '1';
      } else if (['2', '12', '3600'].includes(limitUnit)) {
        this.formData.timingModeUnit = '2';
      } else if (['1', '11', '60'].includes(limitUnit)) {
        this.formData.timingModeUnit = '3';
      }
    }

    if (this.formData.alarmElements) {
      this.formData.alarmElements.forEach(item => {
        let msgUserType = []; // 预警消息需要提醒的人员类型
        item.alarmObjects.forEach(record => {
          msgUserType.push(record.value);
        });
        item.msgUserType = msgUserType;

        let msgUsers = []; // 预警消息需要提醒的人员
        item.alarmUsers.forEach(record => {
          msgUsers.push(record.value);
        });
        item.msgUsers = msgUsers;
      });
    }

    let dueMsgUserType = []; // 逾期消息需要提醒的人员类型
    if (this.formData.dueObjects) {
      this.formData.dueObjects.forEach(item => {
        dueMsgUserType.push(item.value);
      });
    }
    let dueUsers = []; // 逾期消息需要提醒的人员
    if (this.formData.dueUsers) {
      this.formData.dueUsers.forEach(item => {
        dueUsers.push(item.value);
      });
    }
    return {
      rules: {
        name: { required: true, message: '请输入计时器名称' },
        timingMode: { required: true, message: '请选择计时方式' },
        timingModeType: { required: true, message: '请选择计时方式' },
        limitTimeType: { required: true, message: '请选择时限类型' },
        workTimePlanId: { required: true, message: '请选择工作时间' }
        // tasks: { required: true, message: '请选择计时环节' }
      },
      replaceFields: {
        title: 'name',
        key: 'id',
        value: 'id'
      },
      timingModeOptions: timingMode,
      timingModeUnit1,
      timingModeUnit2,
      timingModeUnit3,
      timeLimitType,
      timeLimitUnit1,
      timeLimitUnit4,
      workTimeOptions: [],
      warnMsgUnitList,
      warnMsgUserTypeList,
      warnMsgUserTypeOther,
      timeEndTypeConfig,
      dueActionOptions,
      fieldAndConstantOptions,
      timingModeUnitOptions,
      dueMsgUserType,
      dueUsers,
      dateLimitTime1: null,
      detePatternConfig: {
        1: 'yyyy-MM-DD',
        2: 'yyyy-MM-DD HH',
        3: 'yyyy-MM-DD HH:mm'
      },
      visible: false,
      disableds: {
        timingMode: false,
        limitUnit: false,
        includeStartTimePoint: false,
        autoDelay: false,
        limitTimeType: false,
        timeLimitUnit: false,
        limitTime1: false,
        timingModeType: false,
        timingModeUnit: false
      },
      createAlarm: () => {
        return {
          alarmTime: '1',
          alarmUnit: '2',
          alarmFrequency: '1',
          alarmObjects: [
            {
              type: 32,
              value: 'Doing',
              argValue: null
            }
          ],
          alarmUsers: [],
          alarmFlow: null,
          alarmFlowDoings: [],
          alarmFlowDoingUsers: [],
          msgUserType: ['Doing'], // 从alarmObjects得来
          alarmTimeType: '1', // 提醒时间类型，1常量、2字段值
          alarmUnitType: '1', // 提醒单位类型，1常量、2字段值
          alarmFrequencyType: '1' // // 预警提醒次数类型，1常量、2字段值
        };
      },
      unitElement: () => {
        return {
          type: 32,
          value: '',
          argValue: null
        };
      }
    };
  },
  components: {
    WSwitch,
    WCheckbox,
    WSelect,
    NodeTaskSelect,
    Modal,
    FlowTimerService,
    UserSelectList,
    EdgeDirectionSelect,
    FlowTimerSubflow,
    MoreShowComponent,
    WI18nInput
  },
  computed: {
    // 已经存在计时的环节
    existTimerTaskId() {
      let taskIds = [];
      this.workFlowData.timers.forEach(item => {
        if (item.timerId !== this.formData.timerId) {
          item.tasks.forEach(t => {
            taskIds.push(t.value);
          });
        }
      });
      return taskIds;
    },
    // 逾期时的下一环节
    dueToTaskOptions() {
      let options = this.tasksData;
      options.push({
        name: '流程结束',
        id: constant.EndFlowId
      });
      return options;
    },
    // 环节数据
    tasksData() {
      let tasksData = [];
      if (this.graph.instance) {
        tasksData = this.graph.instance.tasksData;
      }
      return tasksData;
    },
    orgVersionId() {
      return this.workFlowData.property.orgVersionId || '';
    },
    // 动态时限字段选项
    dynamicLimitTimeOptions() {
      const includes = ['WidgetFormInput', 'WidgetFormSelect', 'WidgetFormRadio', 'WidgetFormCheckbox', 'WidgetFormInputNumber'];
      let options = [];
      const { formFieldDefinition } = this.designer;
      const { fields } = formFieldDefinition;
      if (fields) {
        fields.forEach(item => {
          if (includes.includes(item.wtype)) {
            options.push({
              label: item.name,
              value: item.code,
              title: item.name
            });
          }
        });
      }
      return options;
    },
    // 动态截止时间字段
    dynamicEndOptions() {
      const includes = ['WidgetFormDatePicker'];
      let options = [];
      const { formFieldDefinition } = this.designer;
      const { fields } = formFieldDefinition;
      if (fields) {
        fields.forEach(item => {
          if (includes.includes(item.wtype)) {
            options.push({
              label: item.name,
              value: item.code
            });
            if (item.widget && item.widget.subtype) {
              options.push({
                label: `${item.name}(结束)`,
                value: item.widget.configuration.endDateField
              });
            }
          }
        });
      }
      return options;
    },
    // 动态字段
    dynamicFieldOptions() {
      const notIncludes = ['WidgetFormDatePicker'];
      let options = [];
      const { formFieldDefinition } = this.designer;
      const { fields } = formFieldDefinition;
      if (fields) {
        fields.forEach(item => {
          if (!notIncludes.includes(item.wtype)) {
            options.push({
              label: item.name,
              value: item.code,
              title: item.name
            });
          }
        });
      }
      return options;
    },
    // 计时方式的单位-下拉选项
    limitUnitOptions() {
      let options = [];
      if (this.formData.timingMode) {
        options =
          this.formData.timingMode == '1'
            ? this.timingModeUnit1
            : this.formData.timingMode == '2'
            ? this.timingModeUnit2
            : this.timingModeUnit3;
      }

      return options;
    },
    optionsTimeLimitUnit1() {
      let options = [];

      const val = this.formData.timingModeUnit;
      const includeStartTimePoint = this.formData.includeStartTimePoint;
      options = this.dropRightTimeLimitUnit(val, this.timeLimitUnit1, includeStartTimePoint);

      return options;
    },
    optionsTimeLimitUnit4() {
      let options = [];

      const val = this.formData.timingModeUnit;
      const includeStartTimePoint = this.formData.includeStartTimePoint;
      options = this.dropRightTimeLimitUnit(val, this.timeLimitUnit4, includeStartTimePoint);

      return options;
    },
    getFixedFormat() {
      let format = 'yyyy-MM-DD HH:mm';
      if (this.formData.timeLimitUnit) {
        format = this.detePatternConfig[this.formData.timeLimitUnit];
      }
      return format;
    },
    showTime() {
      let show = { format: this.getFixedFormat };
      if (this.formData.timeLimitUnit === '1') {
        show = false;
      }
      return show;
    }
  },
  created() {
    if (this.formData.limitTimeType === '4' && this.formData.limitTime1) {
      this.dateLimitTime1 = moment(this.formData.limitTime1, this.getFixedFormat);
    }
    if (this.formData.introductionType) {
      for (const key in this.disableds) {
        this.disableds[key] = true;
      }
    }
    this.getAllBySystemUnitIdsLikeName();
  },
  mounted() {
    this.delayUpdateScroll();
  },
  methods: {
    changeLimitTimeType(type) {
      if (type === '1') {
        this.formData.limitTime1 = '';
      }
    },
    // 确定引用
    confirmTimerService() {
      let data;
      const selectedRows = this.$refs.timerService.selectedRows;
      if (selectedRows && selectedRows.length) {
        data = selectedRows[0];
      }
      if (!data) {
        this.$message.error('请选择计时服务');
        return;
      }

      this.formData.timerConfigUuid = data.uuid;
      this.formData.introductionType = 'introduction';

      // const timingMode = [
      //   ['3', '2', '1'],
      //   ['13', '12', '11'],
      //   ['86400', '3600', '60']
      // ];
      // for (var i = 0; i < timingMode.length; i++) {
      //   for (var j = 0; j < timingMode[i].length; j++) {
      //     if (data.timingMode === timingMode[i][j]) {
      //       this.formData.timingMode = i + 1 + '';
      //       this.formData.limitUnit = data.timingMode;
      //       this.disableds.timingMode = true;
      //       this.disableds.limitUnit = true;
      //       break;
      //     }
      //   }
      // }

      this.formData.timingModeType = data.timingModeType;
      this.formData.timingModeUnit = data.timingModeUnit;
      this.disableds.timingModeType = true;
      this.disableds.timingModeUnit = true;

      this.formData.includeStartTimePoint = data.includeStartTimePoint;
      this.disableds.includeStartTimePoint = true;

      this.formData.autoDelay = data.autoDelay;
      this.disableds.autoDelay = true;

      if (data.timeLimitType === '10') {
        this.formData.limitTimeType = '1';
        this.formData.timeLimitUnit = data.timeLimitUnit;
        this.formData.limitTime1 = data.timeLimit;

        this.disableds.limitTimeType = true;
        this.disableds.timeLimitUnit = true;
        this.disableds.limitTime1 = true;
      } else if (data.timeLimitType === '20') {
        this.formData.limitTimeType = '4';
        this.formData.timeLimitUnit = data.timeLimitUnit;
        this.formData.limitTime1 = data.timeLimit;
        this.dateLimitTime1 = data.timeLimit;

        this.disableds.limitTimeType = true;
        this.disableds.timeLimitUnit = true;
        this.disableds.limitTime1 = true;
      } else if (data.timeLimitType === '30') {
        this.formData.limitTimeType = '2';
        this.disableds.limitTimeType = true;
      } else if (data.timeLimitType === '40') {
        this.formData.limitTimeType = '3';
        this.disableds.limitTimeType = true;
      }

      this.visible = false;
    },
    // 取消引用
    cancelTimerService() {
      for (const key in this.disableds) {
        this.disableds[key] = false;
      }
      this.formData.timerConfigUuid = '';
      this.formData.introductionType = '';
      this.visible = false;
    },
    // 关闭弹窗
    closeModal() {
      this.visible = false;
    },
    // 点击引用计时服务
    handleTimerService() {
      this.visible = true;
      this.$nextTick(() => {
        this.$refs.timerService.selectedRowKeys = [this.formData.timerConfigUuid];
      });
    },
    changeDate(date, dateString) {
      this.formData.limitTime1 = dateString;
    },
    dropRightTimeLimitUnit(val, timeLimitUnit, includeStartTimePoint) {
      const num = includeStartTimePoint == '0' ? (val == '1' ? 2 : val == '2' ? 1 : 0) : 0;

      return timeLimitUnit.slice(0, timeLimitUnit.length - num);
    },
    validate(callback) {
      if (this.formData.timingModeType === this.timingModeOptions[3]['id'] && !this.formData.limitUnitField) {
        this.$message.error('请选择从表单读取的具体字段');
        callback({ valid: false });
        return;
      }
      if (!this.formData.tasks.length && !this.formData.subTasks.length) {
        this.$message.error('计时环节(普通)或者计时环节(子流程)必选其一！');
        callback({ valid: false });
        return;
      }
      this.$refs.form.validate((valid, error) => {
        callback({ valid, error, data: this.formData });
      });
    },
    // 获取工作时间
    getAllBySystemUnitIdsLikeName() {
      this.$axios.post('/api/ts/work/time/plan/getAllBySystemUnitIdsLikeName').then(res => {
        if (res.status === 200) {
          if (res.data && res.data.code === 0) {
            const data = res.data.data;
            this.workTimeOptions = data.map(item => {
              const text = `${item.name}(V${item.version})`;
              item.text = text;
              return item;
            });
          }
        }
      });
    },
    // 更改预警开关
    changeEnableAlarm(value) {
      if (value === '1') {
        if (this.formData.alarmElements.length === 0) {
          const alarmItem = this.createAlarm();
          this.formData.alarmElements.push(alarmItem);
        }
      }
    },
    // 添加预警提醒
    addAlarm() {
      const alarmItem = this.createAlarm();
      this.formData.alarmElements.push(alarmItem);
    },
    // 删除预警提醒
    delAlarm(item, index) {
      this.$confirm({
        title: '确认',
        content: '确认删除本条预警提醒？',
        onOk: () => {
          this.formData.alarmElements.splice(index, 1);
        }
      });
    },
    // 更改预警提醒人员
    changeAlarmUserType(checkedValue, item, index) {
      let alarmObjects = [];
      checkedValue.forEach(item => {
        const userType = this.unitElement();
        userType.value = item;
        alarmObjects.push(userType);
      });

      this.formData.alarmElements[index]['alarmObjects'] = alarmObjects;
    },
    // 更改逾期提醒人员
    changeDueUserType(checkedValue) {
      let dueObjects = [];
      checkedValue.forEach(item => {
        const userType = this.unitElement();
        userType.value = item;
        dueObjects.push(userType);
      });
      this.formData.dueObjects = dueObjects;
    },
    // 更改计时方式
    changeTimingMode(value) {
      if (value) {
        if (this.timingModeUnitOptions.length) {
          this.formData.timingModeUnit = this.timingModeUnitOptions[0]['id'];
        }
      }
      this.formData.limitUnitField = '';
    },
    // 更改工作时间
    changeWorkTimePlanId(value, option) {
      if (value) {
        const msg = '该工作时间方案包含弹性工时，弹性工时部分将不参与计时！';
        const detail = this.workTimeOptions.find(item => item.id === value);
        if (detail) {
          const item = JSON.parse(detail.workTimeSchedule || '[]');
          for (var i = 0; i < item.length; i++) {
            if (item[i].workTimeType == '3') {
              this.$message.error(msg);
              break;
            } else {
              var workTimes = item[i].workTimes;
              for (var j = 0; j < workTimes.length; j++) {
                if (workTimes[j].type == '2') {
                  this.$message.error(msg);
                  break;
                }
              }
            }
          }
        }
      }
    },
    onDropdownVisibleChange(open) {
      this.delayUpdateScroll();
    },
    delayUpdateScroll() {
      const timer = setTimeout(() => {
        clearTimeout(timer);
        this.$refs.scroll.update();
      }, 300);
    },
    updateScroll() {
      this.$nextTick(() => {
        this.$refs.scroll.update();
      });
    },
    getPopupContainer(triggerNode) {
      return triggerNode.closest('.ant-form');
    }
  }
};
</script>
