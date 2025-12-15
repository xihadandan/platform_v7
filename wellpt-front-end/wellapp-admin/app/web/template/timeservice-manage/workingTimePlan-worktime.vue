<template>
  <a-form-model :model="work" :rules="rules" ref="form" :label-col="labelCol" :wrapper-col="wrapperCol">
    <a-form-model-item label="工作日" prop="workDayArr">
      <a-checkbox-group v-model="work.workDayArr" name="workDay" :options="workDayOptions" @change="workDayChange" />
    </a-form-model-item>
    <a-form-model-item label="类型" prop="type">
      <a-radio-group v-model="work.type">
        <a-radio value="1" name="type">固定时间</a-radio>
        <a-radio value="2" name="type">弹性时间</a-radio>
      </a-radio-group>
    </a-form-model-item>
    <a-form-model-item label="每日工作时长" prop="workHoursPerDay" v-show="work.type == '2'">
      <a-input-number v-model="work.workHoursPerDay" :min="0" :precision="2" />
      <a-checkbox v-model="work.coreWorkPeriod" style="margin-left: var(--w-padding-md)" @change="coreWorkPeriodChange">
        设置核心工作时段
      </a-checkbox>
    </a-form-model-item>
    <div v-show="work.type == '1' || (work.type == '2' && work.coreWorkPeriod)">
      <div class="time-wrapper">
        <div class="time-line">
          <span class="time-text">时段名称</span>
          <span class="time-range">起止时间</span>
        </div>
        <div class="time-line time-content" v-for="(item, index) in work.timePeriods" :key="index">
          <i class="time-drag iconfont icon-ptkj-liebiaoshitu"></i>
          <a-form-model-item
            :prop="`timePeriods:${index}:name`"
            :name="['timePeriods', index, 'name']"
            :rules="{ required: true, message: '时段名称必填', trigger: ['blur'] }"
          >
            <a-input class="time-text-input" v-model="item.name" />
          </a-form-model-item>
          <a-form-model-item
            :wrapperCol="{ span: 24 }"
            :prop="`timePeriods:${index}:fromTime`"
            :name="['timePeriods', index, 'fromTime']"
            :rules="[{ validator: (rule, value, callback) => validateFromTimes(rule, value, callback, item), trigger: ['blur', 'change'] }]"
          >
            <a-time-picker :format="dateFormat" :valueFormat="dateFormat" v-model="item.fromTime" />
            <span style="margin: 8px 6px">至</span>
            <a-time-picker
              :format="dateFormat"
              :valueFormat="dateFormat"
              v-model="item.toTime"
              @change="value => toTimeChange(value, `timePeriods:${index}:fromTime`)"
            />
          </a-form-model-item>
          <a-checkbox
            v-model="item.toNextDay"
            style="margin-left: var(--w-padding-md); margin-top: 6px"
            @change="e => toNextDayChange(e, `timePeriods:${index}:fromTime`)"
          >
            +1天
          </a-checkbox>
          <i class="iconfont icon-ptkj-shanchu time-delete" @click="delItem(index)" v-if="work.timePeriods.length > 1"></i>
        </div>
      </div>
      <div style="padding-bottom: var(--w-padding-md)">
        <a-button type="link" @click="addItem">添加</a-button>
      </div>
    </div>
  </a-form-model>
</template>

<script type="text/babel">
import './timeservice.less';
import moment from 'moment';
import { deepClone } from '@framework/vue/utils/util';
import { workingTimePlanWorkDay } from '../common/constant';
import { each, map, sortBy } from 'lodash';
// 工作时间方案详情
export default {
  name: 'WorkingTimePlanDetailWorkTime',
  props: {
    workData: {
      type: Object,
      default: () => {
        return {
          work: {}
        };
      }
    },
    currentIndex: {
      type: Number,
      default: -1
    }
  },
  components: {},
  inject: ['pageContext', 'vPageState', '$event'],
  data() {
    return {
      work: {},
      workDayOptions: [],
      mode: ['time', 'time'],
      dateFormat: 'HH:mm',
      labelCol: { span: 4 },
      wrapperCol: { span: 19 }
    };
  },
  filters: {},
  watch: {
    currentIndex(val) {
      this.initWorkTimes();
    }
  },
  computed: {
    rules() {
      let rules = {
        workDayArr: { required: true, message: '工作日必填', trigger: ['change'] }
      };
      if (this.work.type == '2') {
        rules.workHoursPerDay = [
          { required: true, message: '每日工作时长必填', trigger: ['blur', 'change'] },
          { validator: this.validateWorkHours }
        ];
      }
      return rules;
    }
  },
  beforeCreate() {},
  created() {},
  beforeMount() {
    let _this = this;
  },
  mounted() {
    this.initWorkTimes();
  },
  methods: {
    moment,
    initWorkTimes() {
      this.work = this.workData && this.workData.work;
      if (this.work) {
        if (!this.work.workDayArr) {
          this.work.workDayArr = this.work.workDay ? this.work.workDay.split(';') : [];
        }
      }
      this.getWorkDayOptions();
    },
    getWorkDayOptions() {
      let hasWorkDay = [];
      each(this.workData.works, (citem, cindex) => {
        if (cindex != this.workData.cindex) {
          hasWorkDay = hasWorkDay.concat(citem.workDay ? citem.workDay.split(';') : []);
        }
      });
      this.workDayOptions = map(workingTimePlanWorkDay, (item, index) => {
        return { label: item, value: index, disabled: hasWorkDay.indexOf(index) > -1 };
      });
    },
    addItem() {
      this.work.timePeriods.push({});
    },
    delItem(index) {
      this.work.timePeriods.splice(index, 1);
    },
    workDayChange(value) {
      if (value && value.length) {
        this.$set(this.work, 'workDay', value.join(';'));
      } else {
        this.$set(this.work, 'workDay', '');
      }
      this.$forceUpdate();
    },
    coreWorkPeriodChange(e) {
      if (e.target.checked && this.work.timePeriods.length == 0) {
        this.$set(this.work, 'timePeriods', [
          {
            name: '上午'
          },
          {
            name: '下午'
          }
        ]);
      }
    },
    validateWorkHours(rule, value, callback) {
      if (value && this.work.coreWorkPeriod) {
        if (value - this.getWorkHoursPerDayAndTimeText().workHoursPerDay < 0) {
          callback('核心工作时段不能大于日工作时长！');
        }
      }
      callback();
    },
    validateFromTimes(rule, value, callback, item) {
      if (this.work.type == '2' && this.work.coreWorkPeriod) {
        if (!value || !item.toTime) {
          callback('起始时间必填！');
        } else if (value && item && item.toTime) {
          var newHours = this.countTime(value, item.toTime, item.toNextDay);
          if (newHours - 0 < 0) {
            callback('结束时间不能小于开始时间！');
          }
        }
      }
      callback();
    },
    toTimeChange(value, field) {
      if (field) {
        this.$refs.form.validateField(field);
      }
    },
    toNextDayChange(e, field) {
      if (field) {
        this.$refs.form.validateField(field);
      }
    },
    countTime(fromTime, toTime, toNextDay) {
      var from = parseInt(fromTime.split(':')[0]) * 60 + parseInt(fromTime.split(':')[1]);
      var to = parseInt(toTime.split(':')[0]) * 60 + parseInt(toTime.split(':')[1]);
      if (toNextDay) {
        return Number(((to - from) / 60).toFixed(2) - 0 + 24);
      } else {
        return Number(((to - from) / 60).toFixed(2));
      }
    },
    compareTime(fromTime, toTime) {
      var from = parseInt(fromTime.split(':')[0]) * 60 + parseInt(fromTime.split(':')[1]);
      var to = parseInt(toTime.split(':')[0]) * 60 + parseInt(toTime.split(':')[1]);
      return to - from > 0;
    },
    isConflict() {
      var workDayEn = ['MON', 'TUE', 'WED', 'THU', 'FRI', 'SAT', 'SUN'];

      var noConflict = true;
      var timeObj = [];

      each(this.workData.works, (rItem, rIndex) => {
        if (rItem) {
          var newObj = {};
          if (rIndex == this.workData.cindex) {
            newObj = deepClone(this.work);
          } else {
            newObj = deepClone(rItem);
          }
          var workDays = newObj.workDay ? newObj.workDay.split(';') : [];
          each(workDays, (wItem, wIndex) => {
            timeObj.push({
              day: wItem,
              timePeriods: newObj.timePeriods
            });
          });
        }
      });

      if (this.workData.cindex == -1) {
        var workDays = this.work.workDay ? this.work.workDay.split(';') : [];
        each(workDays, (wItem, wIndex) => {
          timeObj.push({
            day: wItem,
            timePeriods: this.work.timePeriods
          });
        });
      }

      timeObj = sortBy(timeObj, function (o) {
        return workDayEn.indexOf(o.day);
      });

      for (var i = 0; i < timeObj.length; i++) {
        // 天

        var timePeriods = sortBy(timeObj[i].timePeriods, function (o) {
          return o.fromTime;
        });

        for (var j = 0; j < timePeriods.length; j++) {
          // 时段
          var num = this.countTime(timePeriods[j].fromTime, timePeriods[j].toTime, timePeriods[j].toNextDay); // 判断时段内数值是否小于0
          if (num < 0) {
            noConflict = false;
            break;
          }
          if (timePeriods[j].toNextDay) {
            // 时间段是否+1
            if (j < timePeriods.length - 1) {
              // +1时间段后有值，必冲突
              noConflict = false;
              break;
            } else if (i < timeObj.length - 1 && timeObj[i + 1].day == workDayEn[i + 1] && timeObj[i + 1].timePeriods.length > 0) {
              // +1 时间段后没有其他时间段
              if (!this.compareTime(timePeriods[j].toTime, timeObj[i + 1].timePeriods[0].fromTime)) {
                // 判断第二天的开始时间是否大于第一天的结束时间
                noConflict = false;
                break;
              }
            }
          } else if (j < timePeriods.length - 1 && !this.compareTime(timePeriods[j].toTime, timePeriods[j + 1].fromTime)) {
            noConflict = false;
            break;
          }
        }
      }

      console.log(timeObj);
      return noConflict;
    },
    // 获取总每日工作时长和时间描述
    getWorkHoursPerDayAndTimeText() {
      let workHoursPerDay = 0;
      let timeText = [];
      each(this.work.timePeriods, item => {
        if (item.fromTime && item.toTime) {
          timeText.push(item.name + '：' + item.fromTime + '-' + item.toTime);
          var newHours = Number(this.countTime(item.fromTime, item.toTime, item.toNextDay));
          workHoursPerDay = Number(workHoursPerDay) + newHours;
        }
      });
      return {
        timeText: timeText.join(';'),
        workHoursPerDay: workHoursPerDay.toFixed(2)
      };
    },
    saveForm(callback) {
      this.$refs.form.validate(valid => {
        if (valid) {
          if (this.work.type == '1' || (this.work.type == '2' && this.work.coreWorkPeriod)) {
            var noConflict = this.isConflict();
            if (!noConflict) {
              this.$message.error('工作时段存在冲突，不可重叠！');
              return false;
            }
          }
          let workHoursPerDayAndTimeText = this.getWorkHoursPerDayAndTimeText();
          this.work.timeText = workHoursPerDayAndTimeText.timeText;
          if (this.work.type == '1') {
            this.work.workHoursPerDay = workHoursPerDayAndTimeText.workHoursPerDay;
          } else if (this.work.type == '2' && !this.work.coreWorkPeriod) {
            this.work.timePeriods = [];
          }
          this.work.workHoursPerDay = this.work.workHoursPerDay ? Number(this.work.workHoursPerDay).toFixed(2) : 0;
          if (typeof callback == 'function') {
            callback(this.work);
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
<style lang="less" scoped></style>
