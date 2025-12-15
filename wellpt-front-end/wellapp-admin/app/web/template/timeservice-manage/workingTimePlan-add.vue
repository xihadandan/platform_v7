<template>
  <div class="workingTimePlan">
    <a-form-model :model="workTimeSchedule" :rules="rules" ref="form" :label-col="labelCol" :wrapper-col="wrapperCol">
      <a-form-model-item label="应用时间周期" prop="periodType">
        <a-radio-group v-model="workTimeSchedule.periodType">
          <a-radio value="1" name="periodType">全年</a-radio>
          <a-radio value="2" name="periodType">指定时间周期</a-radio>
        </a-radio-group>
      </a-form-model-item>
      <a-form-model-item label="时间周期" prop="fromDate" v-show="workTimeSchedule.periodType == '2'">
        <a-date-picker
          :format="dateFormat"
          v-model="workTimeSchedule.fromDate"
          :valueFormat="dateFormat"
          :getCalendarContainer="getPopupContainerByPs()"
        />
        <span style="margin: 0px 6px">至</span>
        <a-date-picker
          :format="dateFormat"
          v-model="workTimeSchedule.toDate"
          :valueFormat="dateFormat"
          :getCalendarContainer="getPopupContainerByPs()"
          @change="toDateChange"
        />
        <a-checkbox v-model="workTimeSchedule.toNextYeay" style="margin-left: var(--w-padding-md)" @change="toNextYeayChange">
          +1年
        </a-checkbox>
      </a-form-model-item>
      <a-form-model-item label="类型" prop="workTimeType">
        <a-radio-group v-model="workTimeSchedule.workTimeType" @change="workTimeTypeChange">
          <a-radio value="1" name="workTimeType">固定工时</a-radio>
          <a-radio value="2" name="workTimeType">单双周</a-radio>
          <a-radio value="3" name="workTimeType">弹性工时</a-radio>
        </a-radio-group>
      </a-form-model-item>
      <div v-show="workTimeSchedule.workTimeType == '3'">
        <a-form-model-item label="每周工作时长" prop="workHoursPerWeek">
          <a-input-number v-model="workTimeSchedule.workHoursPerWeek" :min="0" :precision="2" />
          <a-checkbox v-model="workTimeSchedule.coreWorkDay" style="margin-left: var(--w-padding-md)" @change="coreWorkDayChange">
            设置核心工作时段
          </a-checkbox>
        </a-form-model-item>
      </div>
      <div v-show="workTimeSchedule.workTimeType != '3' || (workTimeSchedule.workTimeType == '3' && workTimeSchedule.coreWorkDay)">
        <WorkingTimePlanDetailWorkTimePlan
          ref="planRef"
          :workTimeSchedule="workTimeSchedule"
          :currentIndex="currentIndex"
          :isOperate="true"
          @add="addWorkTime"
          @edit="setWorkTime"
          @del="delWorkTime"
        ></WorkingTimePlanDetailWorkTimePlan>
      </div>
    </a-form-model>
    <a-modal v-model="modalVisible" title="工作时间" @ok="handleOk" @cancel="handleCancel" :width="900" class="workingTimePlan">
      <WorkingTimePlanDetailWorkTime ref="timeRef" :workData="editItem" :currentIndex="editIndex"></WorkingTimePlanDetailWorkTime>
    </a-modal>
  </div>
</template>

<script type="text/babel">
import { getPopupContainerByPs } from '@dyform/app/web/page/dyform-designer/utils';
import moment from 'moment';
import WorkingTimePlanDetailWorkTimePlan from './workingTimePlan-worktimePlan';
import WorkingTimePlanDetailWorkTime from './workingTimePlan-worktime';
import { deepClone } from '@framework/vue/utils/util';
import { each, assign, gt, filter } from 'lodash';
export default {
  name: 'workingTimePlanAdd',
  props: {
    workTimeSchedule: {
      type: Object,
      default: () => {
        return {
          periodType: '1',
          workTimeType: '1',
          workTimes: []
        };
      }
    },
    currentIndex: {
      type: Number,
      default: -1
    }
  },
  components: { WorkingTimePlanDetailWorkTimePlan, WorkingTimePlanDetailWorkTime },
  inject: ['pageContext', 'vPageState', '$event'],
  data() {
    return {
      thisYear: '',
      labelCol: { span: 4 },
      wrapperCol: { span: 19 },
      dateFormat: 'MM-DD',
      modalVisible: false,
      editItem: {},
      editIndex: -1,
      wTemplate: {
        $options: {
          methods: {
            saveForm: this.saveForm
          },
          META: {
            method: {
              saveForm: '保存'
            }
          }
        }
      }
    };
  },
  META: {
    method: {
      saveForm: '保存'
    }
  },
  computed: {
    rules() {
      let rules = {};
      if (this.workTimeSchedule.periodType == '2') {
        rules.fromDate = { validator: this.validateDate, trigger: ['blur', 'change'] };
      }
      if (this.workTimeSchedule.workTimeType == '3') {
        rules.workHoursPerWeek = { validator: this.validatePerWeek, trigger: ['blur', 'change'] };
      }
      return rules;
    }
  },
  watch: {
    currentIndex(val) {
      this.initForm();
    }
  },
  beforeCreate() {},
  created() {},
  beforeMount() {
    let _this = this;
  },
  mounted() {
    let $event = this._provided && this._provided.$event;
    if ($event) {
      this.$evtWidget = $event && $event.$evtWidget;
      this.$dialogWidget = this._provided && this._provided.dialogContext;
    }
    this.initForm();
  },
  methods: {
    getPopupContainerByPs,
    initForm() {
      if (this.workTimeSchedule.workTimeType == '2') {
        let oddWeeks = filter(this.workTimeSchedule.workTimes, { oddWeeks: true });
        let _oddWeeks = filter(this.workTimeSchedule.workTimes, { oddWeeks: false });
        if (oddWeeks.length == 0) {
          this.workTimeSchedule.workTimes.push({
            oddWeeks: true
          });
        }
        if (_oddWeeks.length == 0) {
          this.workTimeSchedule.workTimes.push({
            oddWeeks: false
          });
        }
      }
      this.refreshWorkTime();
    },
    validatePerWeek(rule, value, callback) {
      if (value) {
        if (value - this.getWorkHoursPerWeekAndWorkTimes().workHoursPerWeek < 0 && this.workTimeSchedule.coreWorkDay) {
          callback('核心工作日时长超出每周工作日时长！');
        }
      } else {
        callback('每周工作时长不能为空！');
      }
      callback();
    },
    validateDate(rule, value, callback) {
      if (!value || !this.workTimeSchedule.toDate) {
        callback('时间周期必填！');
      } else if (value && this.workTimeSchedule.toDate) {
        if (!this.workTimeSchedule.toNextYeay && gt(value, this.workTimeSchedule.toDate)) {
          callback('结束时间应大于开始时间！');
        }
      }
      callback();
    },
    toDateChange(value) {
      this.$refs.form.validateField('fromDate');
    },
    toNextYeayChange(e) {
      this.$refs.form.validateField('fromDate');
    },
    workTimeTypeChange(e) {
      if (e.target.value == '2') {
        this.$set(this.workTimeSchedule, 'workTimes', [
          {
            oddWeeks: true
          },
          {
            oddWeeks: false
          }
        ]);
      } else {
        this.$set(this.workTimeSchedule, 'workTimes', [
          {
            oddWeeks: false
          }
        ]);
      }
      this.refreshWorkTime();
    },
    coreWorkDayChange(e) {
      if (e.target.checked && this.workTimeSchedule.workTimes.length == 0) {
        this.$set(this.workTimeSchedule, 'workTimes', [
          {
            oddWeeks: false
          }
        ]);
        this.refreshWorkTime();
      }
    },
    // 添加工作时间
    addWorkTime(works, index) {
      let work = assign(
        {
          workDayArr: [],
          type: '1',
          timePeriods: [
            {
              name: '上午'
            },
            {
              name: '下午'
            }
          ]
        },
        { oddWeeks: works[0].oddWeeks }
      );
      this.editItem = {
        works,
        index,
        work,
        cindex: -1
      };
      this.modalVisible = true;
      this.editIndex = index;
    },
    // 设置工作时间
    setWorkTime(works, index, work, cindex) {
      this.editItem = { works, index, work, cindex };
      this.modalVisible = true;
      this.editIndex = index;
    },
    // 删除工作时间
    delWorkTime(works, index, work, cindex) {
      if (work.index > -1) {
        this.workTimeSchedule.workTimes.splice(work.index, 1);
        if (works.length == 1) {
          this.workTimeSchedule.workTimes.push({
            oddWeeks: work.oddWeeks
          });
        }
        this.refreshWorkTime();
      }
    },
    // 重新渲染工作时间表格
    refreshWorkTime() {
      this.$nextTick(() => {
        this.$refs.planRef && this.$refs.planRef.initWorkTimes();
      });
    },
    // 弹框确定
    handleOk() {
      this.$refs.timeRef &&
        this.$refs.timeRef.saveForm(work => {
          if (this.editItem.cindex == -1) {
            // 添加
            this.workTimeSchedule.workTimes.push(work);
          } else {
            this.workTimeSchedule.workTimes.splice(this.editItem.work.index, 1, work);
          }
          this.refreshWorkTime();
          this.editIndex = -1;
          this.modalVisible = false;
        });
    },
    // 弹框取消
    handleCancel() {
      this.editIndex = -1;
      this.modalVisible = false;
    },
    getWorkHoursPerWeekAndWorkTimes() {
      let workTimes = [];
      let totalHour = 0;
      each(this.workTimeSchedule.workTimes, item => {
        if (item.workDay) {
          workTimes.push(item);
          totalHour = totalHour - 0 + item.workHoursPerDay * item.workDay.split(';').length;
        }
      });
      return {
        workTimes: workTimes,
        workHoursPerWeek: totalHour
      };
    },
    saveForm(callback) {
      this.$refs.form.validate(valid => {
        if (valid) {
          let formData = deepClone(this.workTimeSchedule);
          let workHoursPerWeekAndWorkTime = this.getWorkHoursPerWeekAndWorkTimes();
          formData.workTimes = workHoursPerWeekAndWorkTime.workTimes;
          if (!(formData.workTimeType == '3' && !formData.coreWorkDay) && formData.workTimes.length == 0) {
            this.$message.error('请设置周工作时间！');
            return false;
          }
          if (formData.periodType == '1') {
            formData.fromDate = '01-01';
            formData.toDate = '12-31';
          }
          if (formData.workTimeType != '3') {
            formData.workHoursPerWeek = workHoursPerWeekAndWorkTime.workHoursPerWeek;
          }
          if (typeof callback == 'function') {
            callback(formData);
          }
        } else {
          console.log('error submit!!');
          return false;
        }
      });
    }
  }
};
</script>
