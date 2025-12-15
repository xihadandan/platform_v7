<template>
  <div class="work-time-block">
    <div class="work-time-header" v-if="workTimeSchedule.workTimeType == '3'">
      <div class="time-header-info">
        <span>
          <i class="iconfont icon-ptkj-lishi"></i>
          {{ workTimeSchedule.type | getWorkTimeType(workTimeSchedule, '', workTimeType) }}（合计
          <span class="time-count">{{ workTimeSchedule.workHoursPerDay | getTimeCount(workTimeSchedule, '') }}</span>
          小时）
        </span>
      </div>
    </div>
    <div v-for="(item, index) in workTimes" :key="index">
      <div class="work-time-header" v-if="workTimeSchedule.workTimeType != '3'">
        <div class="time-header-info">
          <span>
            <i class="iconfont icon-ptkj-lishi"></i>
            {{ item[0].type | getWorkTimeType(workTimeSchedule, item[0], workTimeType) }}（合计
            <span class="time-count">{{ item[0].workHoursPerDay | getTimeCount(workTimeSchedule, item) }}</span>
            小时）
          </span>
        </div>
      </div>
      <table class="work-time-body">
        <tbody>
          <tr>
            <th>工作日</th>
            <th>工作时间</th>
            <th>日工作时长</th>
            <th v-if="isOperate" style="width: 230px">操作</th>
          </tr>
          <template v-for="(work, cindex) in item">
            <tr :key="cindex" v-if="work.workDay">
              <td>{{ work.workDay | getWeekDay(workDay) }}</td>
              <td>
                <span class="time-tag" v-if="work.type && work.timePeriods.length > 0">
                  <a-tag color="blue">{{ work.type == '1' ? '固定工作时段' : '核心工作时段' }}</a-tag>
                </span>
                {{ work | getWorkTime }}
              </td>
              <td>{{ work.workHoursPerDay || 0 }} 小时</td>
              <td v-if="isOperate">
                <a-button type="link" size="small" @click="setWorkTime(item, index, work, cindex)">
                  <i class="iconfont icon-ptkj-shezhi"></i>
                  设置
                </a-button>
                <a-button type="link" size="small" @click="delWorkTime(item, index, work, cindex)">
                  <i class="iconfont icon-ptkj-shanchu"></i>
                  删除
                </a-button>
              </td>
            </tr>
          </template>
        </tbody>
      </table>
      <div v-if="isOperate" style="padding-bottom: var(--w-padding-md)">
        <a-button type="link" @click="addWorkTime(item, index)">添加</a-button>
      </div>
    </div>
  </div>
</template>

<script type="text/babel">
import './timeservice.less';
import { workingTimePlanPeriodType, workingTimePlanwWorkTimeType, workingTimePlanWorkDay } from '../common/constant';
import { each, map, findIndex, groupBy } from 'lodash';
// 工作时间方案详情
export default {
  name: 'WorkingTimePlanDetailWorkTimePlan',
  props: {
    workTimeSchedule: {
      type: Object,
      default: () => {
        return {
          workTimes: []
        };
      }
    },
    currentIndex: {
      type: Number,
      default: -1
    },
    isOperate: {
      type: Boolean,
      default: false
    }
  },
  components: {},
  inject: ['pageContext', 'vPageState', '$event'],
  data() {
    return {
      periodType: workingTimePlanPeriodType,
      workTimeType: workingTimePlanwWorkTimeType,
      workDay: workingTimePlanWorkDay,
      workTimes: [],
      editItem: {},
      eidtIndex: -1
    };
  },
  filters: {
    getWorkTimeType(v, item, work, workTimeType) {
      let hasOdd = findIndex(item.workTimes, { oddWeeks: true });
      if (work && work.oddWeeks) {
        return '单周工时';
      } else {
        return hasOdd > -1 ? '双周工时' : workTimeType[item.workTimeType];
      }
    },
    getTimeCount(v, item, work) {
      let hours = 0;
      if (work && work.length) {
        each(work, citem => {
          if (citem.workDay) {
            hours = Number(hours) + Number(citem.workHoursPerDay || 0) * citem.workDay.split(';').length;
          }
        });
      }
      if (work && work.length && work[0].oddWeeks) {
        return (hours - 0).toFixed(2);
      } else {
        return item.workTimeType == '3' ? item.workHoursPerWeek : (hours - 0).toFixed(2);
      }
    },
    getWeekDay(days, workDay) {
      let day = days && days.split(';');
      let newDay = map(day, function (item) {
        return workDay[item];
      });
      return newDay.join(';');
    },
    getWorkTime(work) {
      let period = map(work.timePeriods, (item, index) => {
        return item.name + '：' + item.fromTime + '-' + item.toTime;
      });
      return period.join(';');
    }
  },
  watch: {
    currentIndex(val) {
      this.initWorkTimes();
    }
  },
  computed: {},
  beforeCreate() {},
  created() {},
  beforeMount() {
    let _this = this;
  },
  mounted() {
    this.initWorkTimes();
  },
  methods: {
    initWorkTimes() {
      if (this.workTimeSchedule && this.workTimeSchedule.workTimes) {
        each(this.workTimeSchedule.workTimes, (item, index) => {
          item.index = index; // 所有时间的索引
        });
        this.workTimes = map(groupBy(this.workTimeSchedule.workTimes, 'oddWeeks'), item => {
          return item;
        });
        each(this.workTimes, (item, index) => {
          if (item.length > 1 && !item[0].workDay) {
            this.workTimes[index].splice(0, 1);
          }
        });
      }
    },
    addWorkTime(works, index) {
      this.$emit('add', ...arguments, {
        workTimeSchedule: this.workTimeSchedule,
        currentIndex: this.currentIndex
      });
    },
    setWorkTime(works, index, work, cindex) {
      this.$emit('edit', ...arguments, {
        workTimeSchedule: this.workTimeSchedule,
        currentIndex: this.currentIndex
      });
    },
    delWorkTime(works, index, work, cindex) {
      this.$emit('del', ...arguments, {
        workTimeSchedule: this.workTimeSchedule,
        currentIndex: this.currentIndex
      });
    }
  }
};
</script>
<style lang="less" scoped></style>
