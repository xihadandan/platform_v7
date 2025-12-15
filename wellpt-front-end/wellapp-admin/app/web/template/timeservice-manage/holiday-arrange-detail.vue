<template>
  <div class="holiday_arrangement holiday_arrangement_detail">
    <div style="text-align: right">
      <a-button @click="saveForm" type="primary" v-if="hiddenBtn.indexOf('saveForm') == -1 && !autoUpdate && !disabledSet('edit')">
        保存
      </a-button>
    </div>
    <div>
      <a-button @click="addHoliday" type="primary" v-if="!disabledSet('edit')">添加节假日</a-button>
      <a-form-model :model="form" :rules="formRules" ref="form" :label-col="labelCol" :wrapper-col="wrapperCol">
        <a-collapse default-active-key="0" :bordered="false" class="holiday-content">
          <a-collapse-panel
            v-for="(item, index) in form.list"
            :key="index"
            :showArrow="false"
            class="holiday-content-item"
            :forceRender="true"
          >
            <div slot="header" class="holiday-content-header">
              <div class="holiday-item-time">
                <i class="time-yuandian"></i>
                <span class="time-date">{{ item.holidayInstanceDate | timeDateGet(thisYear) }}</span>
                <span :class="['time-text', isThisYear(item.holidayInstanceDate) ? '' : 'otherYear']">{{ item.holidayName }}</span>
              </div>
              <div class="holiday-detail-header">
                <span class="holiday-header-text">
                  <i class="iconfont icon-ptkj-shixinjiantou-xia holiday-header-collpase"></i>
                  <span class="holiday-date">
                    <template v-if="item.fromDate">{{ item.fromDate }}至{{ item.toDate }}</template>
                    <template v-else>{{ item.holidayName }}</template>
                  </span>
                  放假，共
                  <span class="holiday-count">{{ getHolidayCount(item.fromDate, item.toDate) }}</span>
                  天。{{ item.makeupDate | makeupDateTextGet(item.fromDate, item.toDate) }}
                </span>
                <span class="holiday-header-btn" @click.stop="delItem(index)" v-if="!disabledSet('edit')">
                  <i class="iconfont icon-ptkj-shanchu"></i>
                  删除
                </span>
              </div>
            </div>
            <div class="holiday-item-detail">
              <div class="holiday-detail-left"></div>
              <div class="holiday-detail-body">
                <a-form-model-item label="自动更新假期" v-if="autoUpdate">
                  <a-switch v-model="item.autoUpdate" @change="value => autoUpdateChange(value, index)" :disabled="isCheck" />
                </a-form-model-item>
                <a-form-model-item label="假期起止日期" :prop="`list:${index}:fromDate`" :rules="formRules.fromDate">
                  <span v-if="isCheck">{{ item.fromDate }}~{{ item.toDate }}</span>
                  <a-range-picker
                    v-else
                    v-model="item.rangeDate"
                    :format="dateFormat"
                    :valueFormat="dateFormat"
                    style="width: 420px"
                    @change="value => rangeDateChange(value, index)"
                    :disabled="disabledSet(index)"
                  />
                </a-form-model-item>
                <a-form-model-item label="补班日期">
                  <a-form-model-item
                    v-for="(citem, cindex) in item.makeupDateList"
                    :key="cindex"
                    :prop="`list:${index}:makeupDateList:${cindex}`"
                    :rules="formRules.from"
                  >
                    <span v-if="isCheck">
                      {{ citem.from }}
                      <span style="padding: 0 8px">补</span>
                      {{ citem.to }}
                    </span>
                    <template v-else>
                      <a-date-picker
                        v-model="citem.from"
                        :format="dateFormat"
                        :valueFormat="dateFormat"
                        @change="value => makeupDateChange(value, '', index)"
                        :disabled="disabledSet(index)"
                      />
                      <span style="padding: 0 8px">补</span>
                      <a-date-picker
                        v-model="citem.to"
                        :format="dateFormat"
                        :valueFormat="dateFormat"
                        :disabled="disabledSet(index)"
                        @change="value => makeupDateChange(value, `list:${index}:makeupDateList:${cindex}`, index)"
                      />
                      <span class="holiday-markup-del-btn" @click="delMakeupDate(index, cindex)" v-show="!disabledSet(index)">
                        <i class="iconfont icon-ptkj-jianhao"></i>
                      </span>
                    </template>
                  </a-form-model-item>
                  <div v-if="!isCheck">
                    <a-button @click="addMakeupDate(index)" type="primary" :disabled="disabledSet(index)">
                      <i class="iconfont icon-ptkj-jiahao"></i>
                      添加补班日期
                    </a-button>
                  </div>
                </a-form-model-item>
                <a-form-model-item label="备注">
                  <span v-if="isCheck">
                    {{ item.remark }}
                  </span>
                  <a-textarea
                    v-else
                    v-model="item.remark"
                    :auto-size="{ minRows: 3, maxRows: 5 }"
                    allow-clear
                    style="width: 420px"
                    :disabled="disabledSet(index)"
                  />
                </a-form-model-item>
              </div>
            </div>
          </a-collapse-panel>
        </a-collapse>
      </a-form-model>
    </div>
    <a-modal v-model="modalVisible" title="添加节假日" @ok="handleOk" :width="900" class="pt-modal">
      <HolidayArrangeAdd
        ref="addRef"
        :holiday="form.list"
        :autoUpdate="autoUpdate"
        :year="activeYear"
        :baseHoliday="baseholidaySchedule"
      ></HolidayArrangeAdd>
    </a-modal>
  </div>
</template>

<script type="text/babel">
import moment from 'moment';
import { getPopupContainerNearestPs } from '@framework/vue/utils/function';
import HolidayArrangeAdd from './holiday-arrange-add.vue';
import { each, assignIn, map, find, sortBy } from 'lodash';
export default {
  name: 'HolidayArrangeDetail',
  props: {
    holidaySchedule: {
      type: Array,
      default: () => {
        return [];
      }
    },
    autoUpdate: {
      type: Boolean,
      default: false
    },
    hiddenBtn: {
      type: Array,
      default: () => {
        return [];
      }
    },
    isCheck: {
      type: Boolean,
      default: false
    }
  },
  components: { HolidayArrangeAdd },
  inject: ['pageContext', 'vPageState', '$event'],
  data() {
    return {
      thisYear: '',
      activeYear: '',
      form: {
        list: []
      },
      rules: {
        fromDate: { required: true, validator: this.validateFromDate, message: '假期起止日期必填', trigger: ['blur', 'change'] },
        from: { required: true, trigger: ['blur', 'change'], validator: this.validateMakeup }
      },
      dateFormat: 'YYYY-MM-DD',
      labelCol: { span: 4 },
      wrapperCol: { span: 19 },
      modalVisible: false,
      baseholidaySchedule: [],
      wTemplate: {
        $options: {
          methods: {
            saveForm: this.saveForm
          },
          META: {
            method: {
              saveForm: '保存表单'
            }
          }
        }
      }
    };
  },
  META: {
    method: {
      saveForm: '保存表单'
    }
  },
  filters: {
    timeDateGet(v, thisYear) {
      return v.indexOf(thisYear) > -1 ? v.substring(5) : v;
    },
    makeupDateTextGet(v) {
      let makeupDate = v ? v.split(';') : [];
      let buTitle = [];
      each(makeupDate, (dItem, dIndex) => {
        buTitle.push(dItem.split('|')[0]);
      });
      return buTitle.length ? buTitle.join('、') + '补班。' : '';
    }
  },
  computed: {
    formRules() {
      let rules = assignIn({}, this.rules);
      return rules;
    }
  },
  watch: {},
  beforeCreate() {},
  created() {},
  beforeMount() {
    let _this = this;
  },
  mounted() {
    this.thisYear = moment().year();
    this.activeYear = this.thisYear;
    this.getFormData();
    this.pageContext.handleEvent('SetHolidayArrangeYear', ({ year }) => {
      this.activeYear = Number(year);
      this.getFormData();
    });
  },
  methods: {
    getPopupContainerNearestPs,
    moment,
    getFormData() {
      let _this = this;
      $axios
        .get('/api/ts/holiday/schedule/listByYear', {
          params: {
            year: this.activeYear
          }
        })
        .then(({ data }) => {
          if (data.code == 0 && data.data) {
            if (this.autoUpdate) {
              this.form.list = this.holidaySchedule.length ? this.initData(this.holidaySchedule) : []; //this.initData(data.data);
              this.baseholidaySchedule = this.initData(data.data);
            } else {
              this.form.list = this.initData(data.data);
            }
          }
        });
    },
    initData(data) {
      return sortBy(
        map(data, (item, index) => {
          item.rangeDate = [];
          if (item.fromDate && item.toDate) {
            item.rangeDate.push(item.fromDate);
            item.rangeDate.push(item.toDate);
          }
          let makeupDate = item.makeupDate ? item.makeupDate.split(';') : [];
          item.makeupDateList = map(makeupDate, (citem, cindex) => {
            return {
              from: citem.split('|')[0],
              to: citem.split('|')[1]
            };
          });
          if (this.autoUpdate) {
            let schedule = find(this.holidaySchedule, { holidayUuid: item.holidayUuid });
            item.autoUpdate = schedule ? schedule.autoUpdate : false;
          }
          item.holidayInstanceDate = this.holidayInstanceDateGet(item.holidayInstanceDate);
          return item;
        }),
        'holidayInstanceDate'
      );
    },
    holidayInstanceDateGet(v) {
      return v && v.indexOf(this.thisYear) > -1 ? v.substring(5) : v;
    },
    isThisYear(v) {
      return v.indexOf(this.thisYear) > -1;
    },
    getHolidayCount(fromDate, toDate) {
      var sDate1 = Date.parse(fromDate);
      var sDate2 = Date.parse(toDate);
      var dateSpan = sDate2 - sDate1;
      var days = Math.floor(dateSpan / (24 * 3600 * 1000)) + 1;
      return days > 0 ? days : 0;
    },
    autoUpdateChange(value, index) {
      this.disabledSet(index);
    },
    disabledSet(index) {
      if (this.isCheck) {
        return true;
      } else if (moment(this.activeYear).isBefore(moment(this.thisYear))) {
        return true;
      } else if (index !== 'edit') {
        if (this.autoUpdate && this.form.list[index].autoUpdate) {
          return true;
        }
      }
      return false;
    },
    addHoliday() {
      this.modalVisible = true;
      if (this.$refs.addRef) {
        this.$refs.addRef.initSelectedRowKeys();
      }
    },
    handleOk() {
      this.$refs.addRef.saveForm(list => {
        this.$set(this.form, 'list', list);
        this.modalVisible = false;
      });
    },
    delItem(index) {
      if (index > -1) {
        this.form.list.splice(index, 1);
      }
    },
    addMakeupDate(index) {
      this.form.list[index].makeupDateList.push({
        from: '',
        to: ''
      });
      this.$set(this.form.list, index, this.form.list[index]);
    },
    delMakeupDate(index, cindex) {
      if (index > -1) {
        this.form.list[index].makeupDateList.splice(cindex, 1);
      }
      this.makeupDateReset(index);
    },
    rangeDateChange(value, index) {
      if (value && value.length == 2) {
        this.$set(this.form.list[index], 'fromDate', value[0]);
        this.$set(this.form.list[index], 'toDate', value[1]);
      } else {
        this.$set(this.form.list[index], 'fromDate', '');
        this.$set(this.form.list[index], 'toDate', '');
      }
    },
    makeupDateChange(value, field, index) {
      if (field) {
        this.$refs.form.validateField(field);
      }
      this.makeupDateReset(index);
    },
    makeupDateReset(index) {
      let data = this.form.list[index];
      let markupList = [];
      each(data.makeupDateList, item => {
        if (item.from && item.to) {
          markupList.push(item.from + '|' + item.to);
        }
      });
      this.form.list[index].makeupDate = markupList.join(';');
      this.$set(this.form.list, index, this.form.list[index]);
    },
    validateMakeup(rule, value, callback) {
      let field = rule.field.split(':');
      // if ((value && value.from && value.to) || this.form.list[field[1]].autoUpdate) {
      if (value && value.from && value.to) {
        callback();
      } else {
        callback('补班时间不能为空');
      }
    },
    validateFromDate(rule, value, callback) {
      let field = rule.field.split(':');
      if (value || this.form.list[field[1]].autoUpdate) {
        callback();
      } else {
        callback('假期起止日期必填');
      }
    },
    getForm(callback) {
      this.$refs.form.validate(valid => {
        if (typeof callback == 'function') {
          callback(this.form);
        }
      });
    },
    saveForm(callback) {
      console.log('保存表单');
      this.$refs.form.validate(valid => {
        if (valid) {
          this.beforeSaveReq(callback);
        } else {
          if (typeof callback == 'function') {
            callback(valid);
          }
          return false;
        }
      });
    },
    beforeSaveReq(callback) {
      let bean = JSON.parse(JSON.stringify(this.form));
      let list = map(bean.list, item => {
        return {
          autoUpdate: item.autoUpdate,
          fromDate: item.fromDate,
          holidayInstanceDate: item.holidayInstanceDate,
          holidayName: item.holidayName,
          holidayUuid: item.holidayUuid,
          makeupDate: item.makeupDate,
          remark: item.remark || '',
          toDate: item.toDate,
          uuid: item.uuid || '',
          year: Number(item.year)
        };
      });
      if (typeof callback == 'function') {
        callback(list);
      } else {
        this.saveFormData(list);
      }
    },
    saveFormData(bean) {
      let _this = this;
      $axios
        .post('/api/ts/holiday/schedule/saveAll?year=' + this.activeYear, bean)
        .then(({ data }) => {
          if (data.code == 0) {
            this.$message.success('保存成功');
            this.pageContext.emitEvent(`refreshHolidayArrangeYear`);
          } else {
            this.$message.error(data.msg || '保存失败');
          }
        })
        .catch(err => {
          let data = err && err.response && err.response.data;
          this.$message.error(data.msg || '保存失败');
        });
    }
  }
};
</script>
