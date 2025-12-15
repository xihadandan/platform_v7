<template>
  <a-form-model-item :style="itemStyle" v-if="vShow">
    <template slot="label" v-if="widget.configuration.titleHidden !== true">
      {{ fieldName }}
      <template v-if="widget.configuration.enableTooltip && widget.configuration.tooltip != undefined">
        <a-tooltip
          v-if="widget.configuration.tooltipDisplayType == 'tooltip'"
          :title="$t(widget.id + '_tooltip', widget.configuration.tooltip)"
        >
          <a-button size="small" icon="info-circle" type="link" />
        </a-tooltip>
        <a-popover v-else trigger="hover" :content="$t(widget.id + '_tooltip', widget.configuration.tooltip)" :title="null">
          <a-button size="small" icon="info-circle" type="link" />
        </a-popover>
      </template>
    </template>
    <template v-if="editable || readonly">
      <template v-if="widget.configuration.range">
        <!-- 范围日期选择 -->
        <a-range-picker
          :style="{ width: width }"
          allowClear
          :disabled="designMode ? false : disable || readonly"
          :inputReadOnly="inputReadOnly"
          :disabled-date="disabledDate"
          :disabled-time="disabledDateTime"
          @change="onChange"
          :showTime="showTime"
          v-model="date"
          :format="datePattern"
        />
      </template>
      <template v-else-if="weekPicker">
        <a-week-picker
          :style="{ width: width }"
          :disabled="designMode ? false : disable || readonly"
          :inputReadOnly="inputReadOnly"
          format="datePattern"
          :allowClear="allowClear"
          v-model="date"
          @change="onChange"
        />
      </template>
      <template v-else-if="monthPicker">
        <a-month-picker
          :style="{ width: width }"
          :allowClear="allowClear"
          :disabled="designMode ? false : disable"
          :inputReadOnly="inputReadOnly"
          v-model="date"
          :format="datePattern"
          @change="onChange"
        />
      </template>
      <template v-else-if="timePicker">
        <a-time-picker
          :disabled="designMode ? false : disable || readonly"
          :format="getFixedFormat"
          v-model="defaultValue"
          style="width: 100%"
          @ok="onChangeDate"
          allowClear
        />
      </template>
      <template v-else-if="datetimePicker">
        <a-date-picker
          :format="getFixedFormat"
          :show-time="showTime"
          :disabled="designMode ? false : disable || readonly"
          :disabled-date="disabledDate"
          :disabled-time="disabledDateTime"
          v-model="date"
          :style="{ width: width }"
          @ok="onChangeDate"
          allowClear
        />
      </template>
      <template v-else>
        <a-date-picker
          allowClear
          :mode="getFixedMode"
          :style="{ width: width }"
          :showTime="showTime"
          :disabled="designMode ? false : disable || readonly"
          :disabled-date="disabledDate"
          :inputReadOnly="inputReadOnly"
          :placeholder="$t('placeholder', widget.configuration.placeholder)"
          @change="onChange"
          :format="datePattern"
          v-model="date"
        />
      </template>
    </template>
    <template v-else>
      <span class="textonly">{{ computedLabel }}</span>
    </template>
  </a-form-model-item>
</template>

<script type="text/babel">
import formElementMinxin from './form-element.mixin';
import formMixin from '@dyform/app/web/widget/mixin/form-common.mixin';
import moment from 'moment';
export default {
  name: 'WidgetDatepicker',
  mixins: [formElementMinxin, formMixin],
  props: {},
  data() {
    return {
      date: null,
      minWorkDay: null, //最大值
      maxWorkDay: null, //最小值
      defaultValue: null, //默认值
      endDefaultValue: null //结束值
    };
  },
  beforeCreate() {},
  components: {},
  computed: {
    allowClear() {
      return this.widget.configuration.clearBtnShow;
    },
    // 固定时间 输出格式
    getFixedFormat() {
      var format = this.configuration.datePattern;
      //需要判断是否补0，同步格式
      if (this.configuration.datePatternJson && this.configuration.datePatternJson.key) {
        format =
          this.configuration.zeroShow && this.configuration.datePatternJson
            ? this.configuration.datePatternJson.key0
            : this.configuration.datePatternJson.key;
      }
      return format;
    },
    // 固定时间 选择模式
    getFixedMode() {
      var mode = 'date';
      // 同步时间输入模式
      if (this.configuration.datePatternJson && this.configuration.datePatternJson.mode) {
        mode = this.configuration.datePatternJson.mode;
      }
      return mode;
    },
    weekPicker() {
      return this.widget.configuration.datePatternJson && this.widget.configuration.datePatternJson.mode == 'week';
    },
    monthPicker() {
      return this.widget.configuration.datePatternJson && this.widget.configuration.datePatternJson.mode == 'month';
    },
    timePicker() {
      return this.widget.configuration.datePatternJson && this.widget.configuration.datePatternJson.mode == 'time';
    },
    datetimePicker() {
      return this.widget.configuration.datePatternType == 'datetime';
    },
    showTime() {
      var data = false;
      if (this.datetimePicker) {
        data = { format: this.getFixedFormat };
      }
      return data;
    },
    datePattern() {
      // 通过补0获取格式
      return this.widget.configuration.zeroShow && this.configuration.datePatternJson
        ? this.configuration.datePatternJson.key0
        : this.configuration.datePatternJson.key;
    },
    range() {
      return this.widget.configuration.range;
    },
    width() {
      if (this.widget.configuration.width != 'auto') {
        return this.widget.configuration.width + 'px';
      }
      return 'auto';
    },
    // 计算最小日期
    calculateMinDate() {
      let setting = this.widget.configuration.minValueSetting;
      if (setting && setting.valueType != 'no') {
        if (setting.valueType == 'fixed' && setting.fixedDateValue) {
          return moment(setting.fixedDateValue);
        } else if (setting.valueType == 'system' && setting.offset != undefined) {
          let now = moment();
          // 按工作日计算
          if (setting.offsetType == 'weekday') {
            if (!this.minWorkDay) {
              this.getWorkingHour(setting, 'minWorkDay');
            }
            return this.minWorkDay;
          } else {
            // 计算偏移量
            if (setting.offset > 0) {
              now = now.add(setting.offset, setting.offsetUnit);
            } else {
              now = now.add(setting.offset, setting.offsetUnit);
            }
            return now;
          }
        }
      }
      return null;
    },
    // 计算最大日期
    calculateMaxDate() {
      let setting = this.widget.configuration.maxValueSetting;
      if (setting && setting.valueType != 'no') {
        if (setting.valueType == 'fixed' && setting.fixedDateValue) {
          return moment(setting.fixedDateValue);
        } else if (setting.valueType == 'system' && setting.offset) {
          let now = moment();
          // 按工作日计算
          if (setting.offsetType == 'weekday') {
            if (!this.maxWorkDay) {
              this.getWorkingHour(setting, 'maxWorkDay');
            }
            return this.minWorkDay;
          } else {
            // 计算偏移量
            if (setting.offset > 0) {
              now = now.add(setting.offset, setting.offsetUnit);
            } else {
              now = now.subtract(setting.offset, setting.offsetUnit);
            }
            return now;
          }
        }
      }
      return null;
    },
    hoursOfDay() {
      return Array.from({ length: 24 }, (v, k) => k);
    },
    minutesOfHour() {
      return Array.from({ length: 60 }, (v, k) => k);
    },
    secondsOfMinute() {
      return Array.from({ length: 60 }, (v, k) => k);
    },
    computedLabel() {
      let date = this.form[this.widget.configuration.code];
      if (date == undefined) {
        date = '';
      }
      return date ? this.getMomentData(date, this.datePattern).format(this.getFixedFormat) : '';
    }
  },
  created() {
    if (typeof this.form[this.widget.configuration.code] == 'string' && this.form[this.widget.configuration.code]) {
      this.date = this.moment(this.form[this.widget.configuration.code], 'YYYY-MM-DD HH:mm:ss');
    }
  },
  methods: {
    moment,
    // 主要用于生成moment时间，便以实现时间转换
    getMomentData(val, datePattern) {
      let data = moment(val, datePattern);
      if (data._d == 'Invalid Date') {
        if (moment(val)._d == 'Invalid Date') {
          return moment(val, this.defaultPattern);
        }
        return moment(val);
      }
      return data;
    },
    //设置默认值
    async setDefaultDateValue() {
      if (this.range) {
        this.defaultValue = await this.getDefaultDateValue(this.widget.configuration.defaultValueSetting);
        this.endDefaultValue = await this.getDefaultDateValue(this.widget.configuration.endDefaultValueSetting);
        this.date = [this.defaultValue, this.endDefaultValue];
        this.form.setFieldValue(this.widget.configuration.endDateField, this.endDefaultValue.format(this.datePattern));
      } else {
        this.defaultValue = await this.getDefaultDateValue(this.widget.configuration.defaultValueSetting);
        this.date = this.defaultValue;
      }
      this.form[this.widget.configuration.code] = this.defaultValue.format(this.datePattern);
    },
    // 获取默认值
    getDefaultDateValue(setting) {
      if (setting && setting.valueType !== 'no') {
        if (setting.valueType === 'fixed' && setting.fixedDateValue) {
          // 固定时间
          return moment(setting.fixedDateValue, this.datePattern);
        }
        if (setting.valueType === 'system' && setting.offset) {
          // 服务器系统时间
          let now = moment();
          if (setting.offsetType == 'weekday') {
            // 按工作日计算
            return this.getWorkingHour(setting);
          } else {
            // 按自然日计算
            if (setting.offset > 0) {
              now = now.add(setting.offset, setting.offsetUnit);
            } else {
              now = now.subtract(setting.offset, setting.offsetUnit);
            }
            return now;
          }
        }
      }
      return null;
    },
    // 计算工作日
    async getWorkingHour(setting) {
      var now = moment().format('yyyy-MM-DD HH:mm:ss'); //当前时间
      var offset = setting.offset || 0; //偏移量
      var offsetUnit = 'WorkingDay'; //WorkingDay WorkingHour WorkingMinute
      switch (setting.offsetUnit) {
        case 'minute': //'分'
          offsetUnit = 'WorkingMinute';
          break;
        case 'hour': //'时'
          offsetUnit = 'WorkingHour';
          break;
        case 'month': //'月'
          // offsetUnit = "WorkingHour";
          break;
        case 'year': //'年'
          // offsetUnit = "WorkingHour";
          break;
      }
      const res = await $axios.post('/json/data/services', {
        serviceName: 'workHourFacadeService',
        methodName: 'getWorkDate',
        args: JSON.stringify([now, offset, offsetUnit])
      });
      return moment(res.data.data, this.datePattern);
    },
    // 不可选日期
    disabledDate(date) {
      let minDate = this.calculateMinDate;
      let maxDate = this.calculateMaxDate;

      if (minDate != null && maxDate != null) {
        return date < minDate || date > maxDate;
      } else if (minDate != null) {
        return date < minDate;
      } else if (maxDate != null) {
        return date > maxDate;
      }
      return false;
    },
    numberRange(start, end) {
      let result = [];
      for (let i = start; i < end; i++) {
        result.push(i);
      }
      return result;
    },
    disabledDateTime(date, partial) {
      // 计算需要禁止选择的: 时/分/秒
      let _date = null;
      if (partial === undefined) {
        _date = date || this.moment();
      } else {
        // 范围日期
        if (date == undefined) {
          return {};
        }

        if (partial == 'start') {
          _date = Array.isArray(date) ? date[0] : date;
        } else if (Array.isArray(date) && date.length == 2) {
          _date = date[1];
        }
      }

      return {
        hideDisabledOptions: true,
        disabledHours: () => this.disabledHours(_date, partial),
        disabledMinutes: hour => this.disabledMinutes(hour, _date, partial),
        disabledSeconds: (hour, minute) => this.disabledSeconds(hour, minute, _date, partial)
      };
    },

    disabledHours(date, partial) {
      /**
       * 不可选的小时：选择的日期为限制日期同一天时候
       */
      let hours = [];
      let todayend = this.moment(date).endOf('day');
      let minSameDay = this.calculateMinDate != null && todayend.isSame(this.moment(this.calculateMinDate).endOf('day'));
      if (partial != 'end' && minSameDay) {
        hours = this.hoursOfDay.filter(e => e < this.calculateMinDate.hour());
        // 小时数不可以小于最小小时数
        // if (date.hour() < this.calculateMinDate.hour()) {
        //   date.hour(this.calculateMinDate.hour());
        // }
      }

      if (partial != 'start' && this.calculateMaxDate != null && todayend.isSame(this.moment(this.calculateMaxDate).endOf('day'))) {
        hours = hours.concat(this.hoursOfDay.slice(this.calculateMaxDate.hour() + 1));
        // if (hours.length == 0 && date.hour() > this.calculateMaxDate.hour()) {
        //   // 小时数不可以大于最大小时数
        //   date.hour(0);
        // }
      }

      return hours;
    },

    disabledMinutes(hour, date, partial) {
      /**
       * 不可选的分钟：选择的日期为限制日期同一天时候，且在限制小时内
       */
      let minutes = [];
      let todayend = this.moment(date).endOf('day');
      if (
        partial != 'end' &&
        this.calculateMinDate != null &&
        todayend.isSame(this.moment(this.calculateMinDate).endOf('day')) &&
        hour == this.calculateMinDate.hour()
      ) {
        minutes = this.minutesOfHour.filter(e => e < this.calculateMinDate.minute());
        // 分钟数不可以小于最小分钟数
        if (date.minute() < this.calculateMinDate.minute()) {
          date.minute(this.calculateMinDate.minute());
        }
      }

      if (
        partial != 'start' &&
        this.calculateMaxDate != null &&
        todayend.isSame(this.moment(this.calculateMaxDate).endOf('day')) &&
        hour == this.calculateMaxDate.hour()
      ) {
        if (minutes.length == 0 && date.minute() > this.calculateMaxDate.minute()) {
          // 分钟数不可以大于最大分钟数
          date.minute(0);
        }
        minutes = minutes.concat(this.minutesOfHour.slice(this.calculateMaxDate.minute() + 1));
      }
      return minutes;
    },
    disabledSeconds(hour, minute, date, partial) {
      /**
       * 不可选的秒数：选择的日期为限制日期同一天时候，且在限制小时与限制分钟内
       */
      let seconds = [];
      let todayend = this.moment(date).endOf('day');
      if (
        partial != 'end' &&
        this.calculateMinDate != null &&
        todayend.isSame(this.moment(this.calculateMinDate).endOf('day')) &&
        hour == this.calculateMinDate.hour() &&
        minute == this.calculateMinDate.minute()
      ) {
        seconds = this.secondsOfMinute.filter(e => e < this.calculateMinDate.second());
        // 秒数不可以小于最小秒数
        if (date.second() < this.calculateMinDate.second()) {
          date.second(this.calculateMinDate.second());
        }
      }

      if (
        partial != 'start' &&
        this.calculateMaxDate != null &&
        todayend.isSame(this.moment(this.calculateMaxDate).endOf('day')) &&
        hour == this.calculateMaxDate.hour() &&
        hour == this.calculateMaxDate.hour() &&
        minute == this.calculateMaxDate.minute()
      ) {
        if (seconds.length == 0 && date.second() > this.calculateMaxDate.second()) {
          // 秒数不可以超过最大秒数
          date.second(0);
        }
        seconds = seconds.concat(this.secondsOfMinute.slice(this.calculateMaxDate.second() + 1));
      }
      return seconds;
    },

    onChange(date, dateString) {
      if (this.range) {
        // 设置结束日期字段值
        this.form[this.widget.configuration.code] = dateString[0];
        this.form.setFieldValue(this.widget.configuration.endDateField, dateString[1]);
      } else {
        this.form[this.widget.configuration.code] = dateString;
      }
    },
    setValue(val) {
      if (val) {
        if (!this.form.dataUuid) {
          // 设置默认值
          this.setDefaultDateValue();
          return;
        }
        if (this.range) {
          // 范围日期
          let rangeDate = ['', ''];
          rangeDate[0] = this.moment(val, this.datePattern);
          this.form[this.widget.configuration.code] = rangeDate[0].format(this.datePattern);
          let endDate = this.form[this.widget.configuration.endDateField];
          if (endDate) {
            rangeDate[1] = this.moment(endDate, this.datePattern);
            const endDateStr = rangeDate[1].format(this.datePattern);
            this.form.setFieldValue(this.widget.configuration.endDateField, endDateStr);
          }
          this.date = rangeDate;
        } else {
          this.date = this.moment(val, this.datePattern);
          this.form[this.widget.configuration.code] = this.date.format(this.datePattern) || null;
        }
      }
    },
    displayValue() {
      if (this.range) {
        return this.form[this.widget.configuration.code] + ' - ' + this.form[this.widget.configuration.endDateField];
      }
      return this.form[this.widget.configuration.code];
    },
    // 表单值不为空
    formatFormDataValueIfNotNull() {
      if (this.form[this.widget.configuration.code] != undefined) {
        let _date = null;
        if (this.form[this.widget.configuration.code]) {
          _date = moment(this.form[this.widget.configuration.code], this.datePattern);
        }
        if (this.range) {
          this.date = [_date, null];
          const endDateFieldKey = this.widget.configuration.endDateField;
          const endDateField = this.form[endDateFieldKey];
          if (endDateField) {
            this.date[1] = moment(endDateField, this.datePattern);
            this.form.setFieldValue(endDateFieldKey, this.date[1].format(this.datePattern));
          }
        } else {
          this.date = _date;
        }
      }
    },
    // 格式化日期/时间
    patternDate(val) {
      if (val) {
        let datePatternJson = this.widget.configuration.datePatternJson;
        if (datePatternJson && datePatternJson.mode) {
          // value的格式不含年的，应默认给予当前年，方可正常显示组件
          if (datePatternJson.mode == 'date' && this.datePattern.indexOf('yyyy') == -1) {
            val = this.dateAddYear(val);
          }
        }
        // val = this.moment(val, this.datePattern);
      }
      return val;
    },
    // 当前值结构有没有包含年(仅对格式化模式date,日期格式没有年的情况）
    dateAddYear(val) {
      let day = val.split(' ')[0];
      if (day.indexOf('-') > -1) {
        if (day.split('-')[0].length != 4) {
          val = moment().year() + '-' + val;
        }
      } else if (day.indexOf('/') > -1 && day.split('/')[0].length != 4) {
        if (day.split('/')[0].length != 4) {
          val = moment().year() + '/' + val;
        }
      } else if (day.indexOf('年') == -1) {
        val = moment().year() + '年' + val;
      }
      return val;
    }
  },
  mounted() {},
  beforeUpdate() {},
  beforeMount() {}
};
</script>
