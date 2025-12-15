<template>
  <div class="workingTimePlan">
    <a-form-model class="pt-form" :model="form" :rules="rules" ref="form" :label-col="labelCol" :wrapper-col="wrapperCol">
      <a-card :bordered="false">
        <template slot="title">
          <template v-if="!uuid">新建工作时间方案</template>
          <template v-else-if="name">
            {{ name }}v{{ form.version }}
            <template v-if="form.newVersionTip">
              <i class="iconfont icon-ptkj-lishi" :title="form.newVersionTip" style="color: var(--w-primary-color)"></i>
            </template>
          </template>
          <template v-if="isHistory">({{ form.activeTime }} ~ {{ form.deactiveTime }})</template>
        </template>
        <template slot="extra" v-if="!isCheck">
          <a-button type="primary" @click="saveForm(false)">保存</a-button>
          <template v-if="uuid">
            <a-button @click="saveForm(true)">保存新版本</a-button>
            <a-button @click="checkVersion">查看版本历史</a-button>
            <a-button @click="checkHistory">历史工作时间</a-button>
          </template>
        </template>
        <a-card title="基础信息" :bordered="false">
          <div class="flex">
            <a-form-model-item label="名称" prop="name" style="width: 400px">
              <template v-if="isCheck">
                {{ form.name }}
              </template>
              <a-input v-else v-model="form.name" allow-clear />
            </a-form-model-item>
            <a-form-model-item label="编号" prop="code" style="width: 400px">
              <template v-if="isCheck">
                {{ form.code }}
              </template>
              <a-input v-else v-model="form.code" allow-clear />
            </a-form-model-item>
          </div>
        </a-card>
        <a-card title="工作时间" :bordered="false">
          <a-button type="primary" @click="addWorkTimeSchedule" v-if="!isCheck">添加工作时间安排</a-button>
          <a-card class="work-time-item" v-for="(item, index) in workTimeSchedule" :key="index">
            <div class="work-time-title" slot="title">
              <span>
                <i class="iconfont icon-ptkj-rilixuanzeriqi"></i>
                {{ item.fromDate }} 至 {{ item.toDate }}
              </span>
              <template v-if="item.periodType == '2' && item.toNextYeay">
                <span class="add-year">
                  <i class="add-year-angle"></i>
                  +1年
                </span>
              </template>
            </div>
            <div slot="extra" v-if="!isCheck">
              <div class="time-header-btn">
                <a-button type="link" @click="setWorkTime(item, index)">
                  <i class="iconfont icon-ptkj-shezhi"></i>
                  设置
                </a-button>
                <a-button type="link" @click="delWorkTime(item, index)">
                  <i class="iconfont icon-ptkj-shanchu"></i>
                  删除
                </a-button>
              </div>
            </div>
            <WorkingTimePlanDetailWorkTimePlan
              ref="planRef"
              :workTimeSchedule="item"
              :currentIndex="index"
              :isOperate="!isCheck"
              @add="addWorkTimePlanDetail"
              @edit="setWorkTimePlanDetail"
              @del="delWorkTimePlanDetail"
            ></WorkingTimePlanDetailWorkTimePlan>
          </a-card>
        </a-card>
        <a-card title="节假日安排" :bordered="false" :bodyStyle="{ padding: 0 }">
          <HolidayArrangeDetail
            ref="holidayRef"
            :holidaySchedule="holidaySchedule"
            :autoUpdate="true"
            :isCheck="isCheck"
          ></HolidayArrangeDetail>
        </a-card>
      </a-card>
    </a-form-model>
    <a-modal v-model="modalVisible" title="工作时间安排" @ok="handleOk" @cancel="handleCancel" :width="1200" ref="modalRef">
      <workingTimePlanAdd ref="addRef" :workTimeSchedule="editItem" :currentIndex="editIndex"></workingTimePlanAdd>
    </a-modal>
    <a-modal
      v-model="modalPlanDetailVisible"
      title="工作时间"
      @ok="handleDetailOk"
      @cancel="handleDetailCancel"
      :width="900"
      centered
      class="workingTimePlan"
    >
      <WorkingTimePlanDetailWorkTime
        ref="timeRef"
        :workData="editDetailItem"
        :currentIndex="editDetailIndex"
      ></WorkingTimePlanDetailWorkTime>
    </a-modal>
    <a-modal v-model="modalNewVisible" centered title="工作时间安排" @ok="handleNewOk" @cancel="handleNewCancel" ref="modalNewRef">
      <a-form-model :model="newVersionForm" :rules="newVersionRules" ref="newVersionForm" :label-col="labelCol" :wrapper-col="wrapperCol">
        <a-form-model-item label="新版本">
          {{ newVersionForm.newVersion }}
        </a-form-model-item>
        <a-form-model-item label=" " :colon="false">
          <a-radio-group v-model="newVersionForm.activeRightNow">
            <a-radio :value="true" name="activeRightNow">立即生效</a-radio>
            <a-radio :value="false" name="activeRightNow">指定时间生效</a-radio>
          </a-radio-group>
        </a-form-model-item>
        <a-form-model-item v-if="!newVersionForm.activeRightNow" prop="activeTime" label="生效时间">
          <a-date-picker
            :show-time="{ format: 'HH:mm' }"
            format="YYYY-MM-DD HH:mm"
            v-model="newVersionForm.activeTime"
            :disabled-date="disabledDate"
            :disabled-time="disabledTime"
          />
        </a-form-model-item>
      </a-form-model>
    </a-modal>
  </div>
</template>

<script type="text/babel">
import './timeservice.less';
import moment from 'moment';
import { deepClone, queryString } from '@framework/vue/utils/util';
import WorkingTimePlanDetailWorkTimePlan from './workingTimePlan-worktimePlan';
import WorkingTimePlanDetailWorkTime from './workingTimePlan-worktime';
import HolidayArrangeDetail from './holiday-arrange-detail';
import workingTimePlanAdd from './workingTimePlan-add';
import { each, every, gt, assign, sortBy } from 'lodash';
// 工作时间方案详情
export default {
  name: 'WorkingTimePlanDetail',
  props: {},
  components: { HolidayArrangeDetail, workingTimePlanAdd, WorkingTimePlanDetailWorkTimePlan, WorkingTimePlanDetailWorkTime },
  inject: ['pageContext', 'vPageState', '$event'],
  data() {
    return {
      uuid: '',
      isCheck: false,
      isHistory: false,
      activeYear: '',
      form: {
        name: '',
        list: []
      },
      rules: {
        name: { required: true, message: '名称必填', trigger: ['blur', 'change'] }
      },
      workTimeSchedule: [],
      holidaySchedule: [],
      dateFormat: 'YYYY-MM-DD',
      labelCol: { span: 4 },
      wrapperCol: { span: 19 },
      modalVisible: false,
      editItem: {},
      editIndex: -1,
      modalNewVisible: false,
      newVersionForm: {
        newVersion: '',
        activeRightNow: true,
        activeTime: '',
        bean: {}
      },
      newVersionRules: {
        activeTime: { required: true, message: '生效时间必填', trigger: ['blur', 'change'] }
      },
      minDate: '',
      modalPlanDetailVisible: false,
      editCurrentSchedule: {},
      editDetailItem: {},
      editDetailIndex: -1
    };
  },
  filters: {},
  watch: {},
  computed: {
    hoursOfDay() {
      return Array.from({ length: 24 }, (v, k) => k);
    },
    minutesOfHour() {
      return Array.from({ length: 60 }, (v, k) => k);
    }
  },
  beforeCreate() {},
  created() {
    let urlParams = queryString(location.search.substr(1));
    this.uuid = urlParams.uuid || '';
    this.isHistory = urlParams.isHistory == '1' ? true : false;
    this.isCheck = urlParams.isCheck == '1' ? true : false;
    if (this.uuid) {
      this.getFormData();
    }
  },
  beforeMount() {
    let _this = this;
  },
  mounted() {},
  methods: {
    getFormData() {
      $axios
        .get(this.isHistory ? '/api/ts/work/time/plan/history/get' : '/api/ts/work/time/plan/get', {
          params: {
            uuid: this.uuid
          }
        })
        .then(({ data }) => {
          if (data.code == 0 && data.data) {
            this.form = data.data;
            this.name = data.data.name;
            this.workTimeSchedule = data.data.workTimeSchedule ? JSON.parse(data.data.workTimeSchedule) : [];
            console.log(this.workTimeSchedule);
            this.holidaySchedule = data.data.holidaySchedule ? JSON.parse(data.data.holidaySchedule) : [];
          }
        });
    },
    addWorkTimeSchedule() {
      this.editItem = {
        periodType: '1',
        workTimeType: '1',
        workTimes: [
          {
            oddWeeks: false
          }
        ]
      };
      this.editIndex = this.workTimeSchedule.length;
      this.modalVisible = true;
    },
    // 弹框确定
    handleOk() {
      this.$refs.addRef &&
        this.$refs.addRef.saveForm(plan => {
          if (this.editIndex == this.workTimeSchedule.length) {
            // 添加
            this.workTimeSchedule.push(plan);
          } else {
            this.workTimeSchedule.splice(this.editIndex, 1, plan);
          }
          this.refreshWorkTime();
          this.editIndex = -1;
          this.modalVisible = false;
        });
    },
    handleCancel() {
      this.editIndex = -1;
      this.modalVisible = false;
    },
    // 重新渲染工作时间表格
    refreshWorkTime() {
      this.$nextTick(() => {
        this.$refs.planRef &&
          each(this.$refs.planRef, item => {
            item.initWorkTimes();
          });
      });
    },
    setWorkTime(item, index) {
      this.$set(this, 'editItem', deepClone(item));
      this.editIndex = index;
      this.modalVisible = true;
    },
    delWorkTime(item, index) {
      this.workTimeSchedule.splice(index, 1);
      this.refreshWorkTime();
    },
    checkVersion() {
      this.pageContext.getVueWidgetById('DItMigcffdOPRMLcxmWHTWBXlCTaNwuO').showModal();
      // this.$nextTick(() => {
      //   this.pageContext.emitEvent('refetchWorkingTimePlanCheckVersionManangeTable', this.form.id);
      // });
    },
    checkHistory() {
      this.pageContext.getVueWidgetById('aKJgABGVxkRpeLHBmXdrDBVQiGDBGLHI').showModal();
      // this.$nextTick(() => {
      //   this.pageContext.emitEvent('refetchWorkingTimePlanCheckHistoryManangeTable', this.uuid);
      // });
    },
    isConflict() {
      let tips = [];
      let checkRes = true;
      let workTimeSchedule = [];
      let timeList = [];
      if (this.workTimeSchedule.length == 0) {
        tips.push('工作时间的应用范围设置不合理，请检查！');
        checkRes = false;
      } else {
        checkRes = every(this.workTimeSchedule, (item, index) => {
          if (item.periodType == '1' && (this.workTimeSchedule.length > 1 || item.fromDate != '01-01' || item.toDate != '12-31')) {
            tips.push('工作时间的应用范围设置不合理，请检查！');
            return false;
          } else if (item.periodType == '2') {
            if (item.toNextYeay && !gt(item.fromDate, item.toDate)) {
              tips.push('工作时间的应用范围设置不合理，请检查！');
              timeList = [];
              return false;
            }
            timeList.push({
              fromDate: item.fromDate,
              toDate: item.toDate,
              toNextYeay: item.toNextYeay
            });
          }
          each(item.workTimes, (iItem, iIndex) => {
            delete iItem.workDayText;
            delete iItem.timeText;
            delete iItem.index;
          });
          workTimeSchedule.push(item);
          return true;
        });
        if (checkRes) {
          timeList = sortBy(timeList, function (o) {
            return o.fromDate; // 按开始时间升序
          });
          if (timeList.length > 1) {
            var year = new Date().getFullYear();
            for (var k = 0; k < timeList.length; k++) {
              var curDate = Date.parse(year + '-' + timeList[k].toDate);
              if (k == timeList.length - 1) {
                if (timeList[k].toDate == '12-31' && timeList[0].fromDate == '01-01') {
                  continue;
                } else {
                  var nextDate = Date.parse(year + '-' + timeList[0].fromDate);
                  if ((nextDate - curDate) / 24 / 60 / 60 / 1000 != 1) {
                    checkRes = false;
                    tips.push('工作时间的应用范围设置不合理，请检查！');
                    break;
                  }
                }
              } else {
                if (timeList[k].toNextYeay) {
                  checkRes = false;
                  tips.push('工作时间的应用范围设置不合理，请检查！');
                  break;
                } else {
                  var nextDate = Date.parse(year + '-' + timeList[k + 1].fromDate);
                  if (timeList[k + 1].toNextYeay) {
                    var date0 = Date.parse(year + '-' + timeList[0].fromDate);
                    var date1 = Date.parse(year + '-' + timeList[k + 1].toDate);
                    var next0 = Date.parse(year + '-' + timeList[k + 1].fromDate);
                    var next1 = Date.parse(year + '-' + timeList[k].toDate);
                    if ((date0 - date1) / 24 / 60 / 60 / 1000 != 1 || (next0 - next1) / 24 / 60 / 60 / 1000 != 1) {
                      checkRes = false;
                      tips.push('工作时间的应用范围设置不合理，请检查！');
                      break;
                    }
                  } else {
                    if ((nextDate - curDate) / 24 / 60 / 60 / 1000 != 1) {
                      checkRes = false;
                      tips.push('工作时间的应用范围设置不合理，请检查！');
                      break;
                    }
                  }
                }
              }
            }
          } else if (timeList.length == 1) {
            var tDate = Date.parse(year + '-' + timeList[0].toDate);
            var fDate = Date.parse(year + '-' + timeList[0].fromDate);
            var type1 = !timeList[0].toNextYeay && (timeList[0].fromDate != '01-01' || timeList[0].toDate != '12-31'); // 不+1,则开始是0101且结束是1231
            var type2 = timeList[0].toNextYeay && (fDate - tDate) / 24 / 60 / 60 / 1000 != 1; // +1，开始等于结束+1天
            if (type1 || type2) {
              checkRes = false;
              tips.push('工作时间的应用范围设置不合理，请检查！');
            }
          }
        }
      }
      if (tips.length > 0) {
        this.$message.error(tips.join(''));
      }
      return checkRes ? workTimeSchedule : false;
    },
    saveForm(newVersion) {
      console.log('保存表单');
      this.$refs.form.validate(valid => {
        if (valid) {
          // 校验节假日
          this.$refs.holidayRef.saveForm(data => {
            if (data) {
              // 校验工作时间
              let workTimeSchedule = this.isConflict();
              if (data && workTimeSchedule) {
                this.beforeSaveReq(data, workTimeSchedule, newVersion);
              }
            }
          });
        } else {
          console.log('error submit!!');
          return false;
        }
      });
    },
    beforeSaveReq(holidaySchedule, workTimeSchedule, newVersion) {
      let bean = JSON.parse(JSON.stringify(this.form));
      bean.holidaySchedule = JSON.stringify(holidaySchedule);
      bean.workTimeSchedule = JSON.stringify(workTimeSchedule);
      console.log(bean);
      if (newVersion) {
        this.openNewModal(bean);
      } else {
        this.saveFormData(bean);
      }
    },
    saveFormData(bean) {
      let _this = this;
      $axios
        .post('/api/ts/work/time/plan/save', bean)
        .then(({ data }) => {
          if (data.code == 0) {
            this.$message.success('保存成功');
            this.pageContext.emitEvent(`refetchWorkingTimePlanManangeTable`);

            let url = window.location.pathname + '?uuid=' + data.data;
            window.location.href = url;
          } else {
            this.$message.error(data.msg || '保存失败');
          }
        })
        .catch(err => {
          let data = err && err.response && err.response.data;
          this.$message.error(data.msg || '保存失败');
        });
    },
    openNewModal(bean) {
      $axios
        .get('/api/ts/work/time/plan/getMaxVersionByUuid', {
          params: {
            uuid: this.uuid
          }
        })
        .then(({ data }) => {
          if (data.code == 0 && data.data) {
            this.newVersionForm.newVersion = (Number(data.data) + 0.1).toFixed(1);
          }
        });
      this.getSysDate();
      this.newVersionForm.bean = bean;
      this.modalNewVisible = true;
    },
    getSysDate() {
      $axios.get('/api/ts/work/time/plan/getSysDate').then(({ data }) => {
        if (data.code == 0 && data.data) {
          this.minDate = moment(data.data);
          this.newVersionForm.activeTime = moment(data.data).add('1', 'minute');
        }
      });
    },
    disabledDate(current) {
      return current < this.minDate;
    },
    disabledTime(date) {
      return {
        disabledHours: () => this.disabledHours(date),
        disabledMinutes: hour => this.disabledMinutes(hour, date)
      };
    },
    disabledHours(date) {
      let hours = [];
      // 同一天
      if (this.minDate.isSame(date, 'day')) {
        hours = this.hoursOfDay.filter(e => e < this.minDate.hour());
      }
      return hours;
    },
    disabledMinutes(hour, date) {
      let minutes = [];
      // 同一个小时
      if (this.minDate.isSame(date, 'day') && this.minDate.hour() == hour) {
        minutes = this.minutesOfHour.filter(e => e <= this.minDate.minute());
      }
      return minutes;
    },
    handleNewOk() {
      this.$refs.newVersionForm.validate(valid => {
        if (valid) {
          if (this.newVersionForm.activeRightNow) {
            this.newVersionForm.activeTime = moment();
          }
          let bean = this.newVersionForm.bean;
          bean.version = this.newVersionForm.newVersion;
          bean.activeRightNow = this.newVersionForm.activeRightNow;
          bean.activeTime = moment(this.newVersionForm.activeTime).format('YYYY-MM-DD HH:mm') + ':00';
          $axios
            .post('/api/ts/work/time/plan/saveAsNewVersion', bean)
            .then(({ data }) => {
              if (data.code == 0) {
                this.$message.success('保存成功');
                if (window.opener && window.opener.$app && window.opener.$app.pageContext) {
                  window.opener.$app.pageContext.emitEvent(`refetchWorkingTimePlanManangeTable`);
                }
                let url = '/webpage/page_20240204163811?uuid=' + data.data;
                window.location.href = url;
              } else {
                this.$message.error(data.msg || '保存失败');
              }
            })
            .catch(err => {
              let data = err && err.response && err.response.data;
              this.$message.error(data.msg || '保存失败');
            });
        } else {
          console.log('error submit!!');
          return false;
        }
      });
    },
    handleNewCancel() {},
    addWorkTimePlanDetail(works, index, schedule) {
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
      this.editDetailItem = {
        works,
        index,
        work,
        cindex: -1
      };
      this.editDetailIndex = index;
      this.editCurrentSchedule = schedule;
      this.modalPlanDetailVisible = true;
    },
    setWorkTimePlanDetail(works, index, work, cindex, schedule) {
      this.editDetailItem = { works, index, work, cindex };
      this.editDetailIndex = index;
      this.editCurrentSchedule = schedule;
      this.modalPlanDetailVisible = true;
    },
    delWorkTimePlanDetail(works, index, work, cindex, schedule) {
      if (work.index > -1) {
        this.workTimeSchedule[schedule.currentIndex].workTimes.splice(work.index, 1);
        if (works.length == 1) {
          this.workTimeSchedule[schedule.currentIndex].workTimes.push({
            oddWeeks: work.oddWeeks
          });
        }
        this.refreshWorkTime();
      }
    },
    handleDetailOk() {
      this.$refs.timeRef &&
        this.$refs.timeRef.saveForm(work => {
          if (this.editDetailItem.cindex == -1) {
            // 添加
            this.workTimeSchedule[this.editCurrentSchedule.currentIndex].workTimes.push(work);
          } else {
            this.workTimeSchedule[this.editCurrentSchedule.currentIndex].workTimes.splice(this.editDetailItem.work.index, 1, work);
          }
          this.refreshWorkTime();
          this.editDetailIndex = -1;
          this.modalPlanDetailVisible = false;
        });
    }
  }
};
</script>
<style lang="less" scoped></style>
