<template>
  <a-form-model-item
    v-show="!hidden"
    :prop="formModelItemProp"
    :rules="rules"
    :ref="widget.configuration.code"
    :style="itemStyle"
    :label="itemLabel"
    :colon="displayAsLabel"
    :class="widgetClass"
  >
    <a-config-provider :locale="locale">
      <template v-if="!widget.configuration.asEndDate">
        <template v-if="!displayAsLabel">
          <template v-if="widget.configuration.range">
            <!-- 范围日期选择 -->
            <a-range-picker
              :ref="'picker_' + fieldCode"
              :style="{ width: width }"
              :allowClear="allowClear"
              :disabled="disable || readonly"
              :inputReadOnly="inputReadOnly"
              :disabledDate="disabledDate"
              :disabled-time="disabledDateTime"
              :placeholder="placeholder"
              @change="onChange"
              @panelChange="handlePanelChange"
              @openChange="handleOpenChange"
              @calendarChange="calendarChange"
              :mode="mode"
              :showTime="showTime"
              v-model="date"
              :format="getFixedFormat"
              :dropdownClassName="dropdownRange"
            />
          </template>
          <template v-else-if="weekPicker">
            <a-week-picker
              :ref="'picker_' + fieldCode"
              :style="{ width: width }"
              :disabled="disable || readonly"
              :inputReadOnly="inputReadOnly"
              :format="getFixedFormat"
              :allowClear="allowClear"
              :placeholder="placeholder"
              :disabled-date="disabledDate"
              v-model="date"
              @change="onChange"
            >
              <a-icon type="calendar" slot="suffixIcon" />
            </a-week-picker>
          </template>
          <template v-else-if="monthPicker">
            <a-month-picker
              :ref="'picker_' + fieldCode"
              :style="{ width: width }"
              :allowClear="allowClear"
              :disabled="disable || readonly"
              :inputReadOnly="inputReadOnly"
              :placeholder="placeholder"
              :disabled-date="disabledDate"
              v-model="date"
              :format="getFixedFormat"
              @change="onChange"
            />
          </template>
          <template v-else-if="timePicker">
            <a-time-picker
              :ref="'picker_' + fieldCode"
              :format="getFixedFormat"
              v-model="date"
              :disabled="disable || readonly"
              :style="{ width: width }"
              :defaultOpenValue="timePickerDefaultOpenValue"
              :disabledHours="disabledHours"
              :disabledMinutes="disabledMinutes"
              :disabledSeconds="disabledSeconds"
              @change="onChange"
              :allowClear="allowClear"
              :placeholder="placeholder"
            />
          </template>
          <template v-else-if="datetimePicker">
            <a-date-picker
              :ref="'picker_' + fieldCode"
              :format="getFixedFormat"
              :showTime="showTime"
              :disabled="disable || readonly"
              :disabled-date="disabledDate"
              :disabled-time="disabledDateTime"
              :placeholder="placeholder"
              v-model="date"
              :style="{ width: width, 'min-width': '60px' }"
              @change="onChange"
              :allowClear="allowClear"
              @panelChange="handlePanelChangeDatetimePicker"
            />
          </template>
          <template v-else-if="datePicker">
            <a-date-picker
              :ref="'picker_' + fieldCode"
              :format="getFixedFormat"
              :showTime="showTime"
              :allowClear="allowClear"
              :style="{ width: width }"
              :disabled="disable || readonly"
              :disabled-date="disabledDate"
              :inputReadOnly="inputReadOnly"
              :placeholder="placeholder"
              @change="onChange"
              @panelChange="datePickerHandlePanelChange"
              v-model="date"
            />
          </template>
          <template v-else>
            <a-date-picker
              :ref="'picker_' + fieldCode"
              :format="getFixedFormat"
              :showTime="showTime"
              :allowClear="allowClear"
              :style="{ width: width }"
              :disabled="disable || readonly"
              :disabled-date="disabledDate"
              :inputReadOnly="inputReadOnly"
              :placeholder="placeholder"
              @change="onChange"
              @panelChange="datePickerHandlePanelChange"
              :mode="mode"
              v-model="date"
            />
          </template>
        </template>
        <span v-show="displayAsLabel" class="textonly" :title="computedLabel">{{ computedLabel }}</span>
      </template>
    </a-config-provider>
  </a-form-model-item>
</template>

<script type="text/babel">
import moment, { months } from 'moment';
import widgetMixin from '@framework/vue/mixin/widgetMixin';
import { FormElement } from '../../framework/vue/dyform/form-element';
import formMixin from '../mixin/form-common.mixin';
import { getOptions } from './configuration/components/date-pattern-options.js';
import { find, debounce } from 'lodash';
import { deepClone } from '@framework/vue/utils/util';
export default {
  extends: FormElement,
  name: 'WidgetFormDatePicker',
  mixins: [widgetMixin, formMixin],
  inject: ['isNewFormData', 'locale'],
  data() {
    let mode =
      this.widget.configuration.datePatternJson && this.widget.configuration.datePatternJson.mode
        ? this.widget.configuration.datePatternJson.mode
        : 'date';
    const range = this.widget.configuration.range;
    const date = range ? [null, null] : null;
    if (range) {
      mode = [mode, mode];
    }
    return {
      range,
      date,
      mode,
      minWorkDay: null, //最大值
      maxWorkDay: null, //最小值
      inputReadOnly: true, //设置输入框为只读（避免在移动设备上打开虚拟键盘）
      allowClear: this.widget.configuration.clearBtnShow,
      minValueSetting: this.widget.configuration.minValueSetting,
      minDate: null,
      maxValueSetting: this.widget.configuration.maxValueSetting,
      maxDate: null,
      defaultValueSetting: this.widget.configuration.defaultValueSetting,
      endDefaultValueSetting: this.widget.configuration.endDefaultValueSetting,
      dateCalculating: false,
      defaultPattern: 'yyyy-MM-DD HH:mm:ss',
      isModePanelChangeFlag: 0,
      isOnChangeFlag: false,
      inputRefs: undefined, // 输入框
      calendarInstance: undefined, //时间选择
      rangeCalendar: undefined //区间日期选择
    };
  },
  computed: {
    minMaxValidate() {
      if (this.minDate && this.maxDate) {
        return this.minDate.isBefore(this.maxDate);
      }
      return true;
    },
    dropdownRange() {
      let classRange = '';
      if (this.range && this.timePicker) {
        classRange = 'time-range-container';
      }
      return classRange;
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
    placeholder() {
      let placeholder = this.$t('WidgetFormDatePicker.placeholder', '请选择时间');
      if (this.widget.configuration.range) {
        placeholder = [this.$t('WidgetFormDatePicker.startDate', '开始时间'), this.$t('WidgetFormDatePicker.endDate', '结束时间')];
        if (this.widget.configuration.placeholder) {
          let _placeholder = this.$t('placeholder', this.widget.configuration.placeholder).split(';');
          placeholder = [
            _placeholder[0] || this.$t('WidgetFormDatePicker.startDate', '开始时间'),
            _placeholder[1] || this.$t('WidgetFormDatePicker.endDate', '结束时间')
          ];
        }
        return placeholder;
      }
      return this.$t('placeholder', this.widget.configuration.placeholder || placeholder);
    },
    datePicker() {
      return this.widget.configuration.datePatternJson.mode == 'date';
    },
    weekPicker() {
      return this.widget.configuration.datePatternJson.mode == 'week';
    },
    monthPicker() {
      return this.widget.configuration.datePatternJson.mode == 'month';
    },
    yearPicker() {
      return this.widget.configuration.datePatternJson.mode == 'year';
    },
    timePicker() {
      return this.widget.configuration.datePatternJson.mode == 'time';
    },
    datetimePicker() {
      return this.widget.configuration.datePatternType == 'datetime';
    },
    showTime() {
      var data = false;
      if (this.datetimePicker || (this.range && this.timePicker)) {
        data = { format: this.getFixedFormat };
      }
      return data;
    },
    // 补全的日期格式
    datePattern() {
      // 通过补0获取格式
      const dateAddYear = val => {
        let day = val.split(' ')[0];
        const year = 'yyyy';
        if (day.indexOf('-') > -1) {
          if (day.split('-')[0].length != 4) {
            val = year + '-' + val;
          }
        } else if (day.indexOf('/') > -1 && day.split('/')[0].length != 4) {
          if (day.split('/')[0].length != 4) {
            val = year + '/' + val;
          }
        } else if (day.indexOf('年') == -1) {
          val = year + '年' + val;
        }
        return val;
      };
      let datePatternJson = this.widget.configuration.datePatternJson;
      if (datePatternJson) {
        const datePattern = this.widget.configuration.zeroShow ? datePatternJson.key0 : datePatternJson.key;
        if (datePatternJson.mode == 'date' && datePattern.indexOf('yyyy') == -1) {
          return dateAddYear(datePattern);
        }
        return datePattern;
      }
    },
    // 针对没有完整日期，无法通过moment处理的值，使用内容格式用于保存
    contentFormat() {
      let datePatternOptions = getOptions();
      let datePatternJson = this.widget.configuration.datePatternJson;
      if (!datePatternJson.contentFormat) {
        datePatternJson.contentFormat = find(datePatternOptions[this.widget.configuration.datePatternType], {
          key0: datePatternJson.key0
        }).contentFormat;
      }
      return datePatternJson.contentFormat;
    },
    width() {
      if (this.widget.configuration.width != 'auto') {
        return this.widget.configuration.width + 'px';
      }
      return 'auto';
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
      let date = this.formData[this.widget.configuration.code];
      let endDate = this.formData[this.widget.configuration.endDateField];
      if (this.range && (!date || !endDate)) {
        return '';
      }
      if (date == undefined) {
        date = '';
      }
      if (endDate == undefined) {
        endDate = '';
      }
      if (this.range) {
        return `${this.getMomentData(date, this.datePattern).format(this.getFixedFormat)} ~ ${this.getMomentData(
          endDate,
          this.datePattern
        ).format(this.getFixedFormat)}`;
      }
      return date ? this.getMomentData(date, this.datePattern).format(this.getFixedFormat) : '';
    },
    // 时间选择器默认打开时间
    timePickerDefaultOpenValue() {
      if (this.minDate && this.minDate.isAfter(moment())) {
        return this.minDate;
      } else if (this.maxDate && this.maxDate.isBefore(moment())) {
        return this.maxDate;
      }
      return moment();
    }
  },
  created() {
    if (this.widget.configuration.isDatabaseField && this.widget.subtype == 'Range') {
      if (
        this.widget.configuration.endDateField != undefined &&
        !this.form.formData.hasOwnProperty(this.widget.configuration.endDateField)
      ) {
        this.$set(this.form.formData, this.widget.configuration.endDateField, undefined);
      }
      if (this.form.$fieldset == undefined) {
        // 该种方式设值，可以使得对象不被监听
        this.form.$fieldset = {};
      }
      if (!this.designMode) {
        if (this.widget.configuration.endDateField != undefined) {
          this.form.$fieldset[this.widget.configuration.endDateField] = this.proxyEndFieldInst();
        }
      }
    }

    this.trySetDefaultDateValue();
  },
  beforeUpdate() {
    // this.formatFormDataValueIfNotNull();
  },
  mounted() {
    if (!this.designMode) {
      this.setMinMaxSystemDate();

      this.setFixedDate();
      setTimeout(() => {
        this.watchMinMaxFieldDateChange();
      }, 3000);
      // this.formatFormDataValueIfNotNull();
      if (
        !this.dateCalculating &&
        (this.formData[this.fieldCode] != undefined || (this.range && this.formData[this.configuration.endDateField] != undefined))
      ) {
        this.setValue(this.formData[this.fieldCode], true);
      }
      this.timePickerChangeByMinMax = debounce(this.timePickerChangeByMinMax.bind(this), 300);
    }
  },
  methods: {
    pickerMounted(open) {
      let _this = this;
      if (this.widget.configuration.range) {
        if (open) {
          if (
            this.$refs['picker_' + this.fieldCode] &&
            this.$refs['picker_' + this.fieldCode].$refs.picker &&
            this.$refs['picker_' + this.fieldCode].$refs.picker.$refs.calendarInstance
          ) {
            this.rangeCalendar = this.$refs['picker_' + this.fieldCode].$refs.picker.$refs.calendarInstance;
            this.rangeCalendar.disabledStartMonth = month => {
              var sValue = _this.rangeCalendar.sValue;
              return month.isAfter(sValue[1], 'month') || _this.disabledDate(month);
            };
            this.rangeCalendar.disabledEndMonth = month => {
              var sValue = _this.rangeCalendar.sValue;
              return month.isBefore(sValue[0], 'month') || _this.disabledDate(month);
            };
            if (this.monthPicker || this.yearPicker) {
              this.rangeCalendar.onStartPanelChange = (value, mode) => {
                var sMode = this.rangeCalendar.sMode,
                  sValue = this.rangeCalendar.sValue;
                var newMode = [mode, sMode[1]];
                var newValue = [value || sValue[0], sValue[1]];
                this.rangeCalendar.__emit('panelChange', newValue, newMode, this.date[1]);
                var newState = {
                  sPanelTriggerSource: 'start'
                };
                if (!this.rangeCalendar.$props.hasOwnProperty('mode')) {
                  newState.sMode = newMode;
                }
                this.rangeCalendar.setState(newState);
              };
              this.rangeCalendar.onEndPanelChange = (value, mode) => {
                var sMode = this.rangeCalendar.sMode,
                  sValue = this.rangeCalendar.sValue;
                var newMode = [sMode[0], mode];
                var newValue = [sValue[0], value || sValue[1]];
                this.rangeCalendar.__emit('panelChange', newValue, newMode, this.date[0]);
                var newState = {
                  sPanelTriggerSource: 'end'
                };
                if (!this.rangeCalendar.$props.hasOwnProperty('mode')) {
                  newState.sMode = newMode;
                }
                this.rangeCalendar.setState(newState);
              };
            }
            this.rangeCalendar.fireValueChange(_this.rangeCalendar.sValue);
          } else {
            setTimeout(() => {
              this.pickerMounted(open);
            }, 10);
          }
        } else {
          if ((this.monthPicker || this.yearPicker) && this.date[0] && this.date[1]) {
            var newValue = [this.rangeCalendar.sValue[0], this.rangeCalendar.sValue[1]];
            this.onChange(newValue, [newValue[0].format(this.datePattern), newValue[1].format(this.datePattern)]);
          }
        }
      } else {
        if (open) {
          if (
            this.$refs['picker_' + this.fieldCode] &&
            this.$refs['picker_' + this.fieldCode].$refs.picker &&
            this.$refs['picker_' + this.fieldCode].$refs.picker.$refs.calendarInstance
          ) {
            this.calendarInstance = this.$refs['picker_' + this.fieldCode].$refs.picker.$refs.calendarInstance;
          } else {
            setTimeout(() => {
              this.pickerMounted(open);
            }, 10);
          }
        }
      }
    },

    trySetDefaultDateValue() {
      if (
        (this.date == null || (Array.isArray(this.date) && this.date[0] == null && this.date[1] == null)) &&
        (!this.form.dataUuid || this.isNewFormData)
      ) {
        // 添加的数据已经有值，则不设置默认值
        if (this.formData[this.configuration.endDateField] && this.formData[this.configuration.endDateField]) {
          return;
        }
        let calculateDate = () => {
          // 时间字段得用完整的moment时间
          this.setDefaultValueByFixed({
            startDate: this.patternDate(this.formData[this.fieldCode], undefined, true),
            endDate: this.patternDate(this.formData[this.configuration.endDateField], undefined, true)
          });
          this.setDefaultValueBySystem({
            startDate: this.patternDate(this.formData[this.fieldCode], undefined, true),
            endDate: this.patternDate(this.formData[this.configuration.endDateField], undefined, true)
          });
        };
        // 新增表单数据时候才允许设置默认值
        if (this.defaultValueSetting.valueType !== 'no' && !this.formData[this.fieldCode]) {
          // 存在默认值设置的情况下，但是表单字段数据暂未获取到默认值
          this.lazySetDefaultValue = () => {
            calculateDate();
            delete this.lazySetDefaultValue;
          };
        } else {
          calculateDate();
        }
      }
    },
    proxyEndFieldInst() {
      let _this = this;
      let handler = {
        setValue(val) {
          _this.formData[_this.widget.configuration.endDateField] = val;
        },
        getFormattedMoment() {
          if (_this.date != undefined) {
            let _moment = Array.isArray(_this.date) ? _this.date[1] : null;
            return _moment ? moment(_moment.format(_this.datePattern), _this.datePattern) : undefined;
          }
          return undefined;
        },
        getValue() {
          return _this.formData[_this.widget.configuration.endDateField];
        }
      };
      return new Proxy(this, {
        get(target, propKey) {
          if (typeof target[propKey] == 'function') {
            if (handler[propKey] != undefined) {
              return handler[propKey];
            }
          }
          return target[propKey];
        }
      });
    },
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

    getFormattedMoment() {
      if (this.date != undefined) {
        let _moment = Array.isArray(this.date) ? this.date[0] : this.date;
        return _moment ? moment(_moment.format(this.datePattern), this.datePattern) : undefined;
      }
      return undefined;
    },

    // 设置默认时间（系统时间）
    setDefaultValueBySystem({ startDate, endDate }) {
      if (startDate && this.defaultValueSetting && this.defaultValueSetting.valueType == 'system') {
        if (this.defaultValueSetting && this.defaultValueSetting.offset) {
          if (this.range) {
            this.date && (this.date[0] = null);
          } else {
            this.date = null;
          }
        }

        this.dateCalculating =
          this.defaultValueSetting && this.defaultValueSetting.valueType === 'system' && this.defaultValueSetting.offset;

        this.getSystemDate({
          date: startDate,
          setting: this.defaultValueSetting
        }).then(({ date }) => {
          this.formData[this.widget.configuration.code] = date.format(this.contentFormat);
          let start = this.getMomentData(date.format(this.defaultPattern), this.defaultPattern);
          if (this.range) {
            this.date[0] = start;
          } else {
            this.date = start;
          }
          this.dateCalculating = false;
          this.emitChange();
        });
      }

      if (this.range && endDate && this.endDefaultValueSetting && this.endDefaultValueSetting.valueType == 'system') {
        if (this.endDefaultValueSetting && this.endDefaultValueSetting.offset) {
          this.date && (this.date[1] = null);
        }
        this.getSystemDate({
          date: endDate,
          setting: this.endDefaultValueSetting
        }).then(({ date }) => {
          const endDateStr = date.format(this.contentFormat);
          let end = this.getMomentData(date.format(this.defaultPattern), this.defaultPattern);
          this.$set(this.date, 1, end);
          const endDateFieldKey = this.widget.configuration.endDateField;
          if (endDateFieldKey) {
            this.form.setFieldValue(endDateFieldKey, endDateStr);
          }
          this.emitChange();
        });
      }
    },
    // 设置默认时间（固定日期）
    setDefaultValueByFixed({ startDate, endDate }) {
      if (startDate && this.defaultValueSetting && this.defaultValueSetting.valueType === 'fixed') {
        if (this.range) {
          this.date[0] = startDate;
        } else {
          this.date = startDate;
        }
      }
      if (this.range && endDate) {
        if (this.endDefaultValueSetting && this.endDefaultValueSetting.valueType === 'fixed') {
          this.date[1] = endDate;
        }
      }
    },
    // 获取系统日期
    getSystemDate({ date, setting }) {
      return new Promise((resolve, reject) => {
        if (setting && setting.valueType === 'system') {
          if (setting.offset) {
            this.getSystemOffset({
              date,
              setting
            }).then(date => {
              resolve({ date, setting });
            });
          } else {
            // 没有偏移量
            resolve({ date, setting });
          }
        }
      });
    },
    // 设置系统最大最小值
    setMinMaxSystemDate() {
      let date = moment(window.__INITIAL_STATE__._CONTEXT_STATE_.SERVER_TIMESTAMP);
      if (this.minValueSetting && this.minValueSetting.valueType === 'system') {
        this.getSystemDate({
          date: this.getMomentData(date, this.defaultPattern),
          setting: this.minValueSetting
        }).then(({ date }) => {
          let min = this.toValueMin(moment(date), this);
          this.minDate = min;
          this.setDateValueByMinMax();
        });
      }
      if (this.maxValueSetting && this.maxValueSetting.valueType === 'system') {
        this.getSystemDate({
          date: this.getMomentData(date, this.defaultPattern),
          setting: this.maxValueSetting
        }).then(({ date }) => {
          let max = this.toValueMax(moment(date), this);
          this.maxDate = max;
          this.setDateValueByMinMax();
        });
      }
    },
    // 设置固定日期(最大最小值)
    setFixedDate() {
      if (this.minValueSetting && this.minValueSetting.valueType === 'fixed' && this.minValueSetting.fixedDateValue) {
        this.minValueSetting.fixedDateValue = this.toValueMin(moment(this.minValueSetting.fixedDateValue), this);
        this.minDate = this.getMomentData(this.minValueSetting.fixedDateValue, this.defaultPattern);
        this.setDateValueByMinMax();
      }
      if (this.maxValueSetting && this.maxValueSetting.valueType === 'fixed' && this.maxValueSetting.fixedDateValue) {
        this.maxValueSetting.fixedDateValue = this.toValueMax(moment(this.maxValueSetting.fixedDateValue), this);
        this.maxDate = this.getMomentData(this.maxValueSetting.fixedDateValue, this.defaultPattern);
        this.setDateValueByMinMax();
      }
    },
    // 监听表单中最大最小值对应字段值变化
    watchMinMaxFieldDateChange() {
      if (this.minValueSetting && this.minValueSetting.valueType === 'field' && this.minValueSetting.field) {
        let fieldMin = this.minValueSetting.field.split(';');
        let $fieldsetMin = this.form.$fieldset[fieldMin[0]];
        if (fieldMin[1]) {
          $fieldsetMin = this.form.$fieldset[fieldMin[1]];
        }
        this.$watch('formData.' + fieldMin[0], (newValue, oldValue) => {
          if (newValue) {
            newValue = this.patternDate(newValue, undefined, true, $fieldsetMin);
            if ($fieldsetMin) {
              newValue = this.toValueMin(newValue, $fieldsetMin);
            }
            newValue = this.toValueMin(newValue, this);
            this.minDate = this.getMomentData(newValue, this.datePattern);
          } else {
            this.minDate = null;
          }
          if (!this.contentFormat.startsWith('yyyy') && !this.contentFormat.startsWith('HH')) {
            if (this.maxDate) {
              let maxDate = this.toValueMax(this.maxDate, this);
              this.maxDate = this.getMomentData(maxDate, this.datePattern);
            }
            this.setDateValueByMinMax();
          }
        });
        let minValue = this.formData[fieldMin[0]];
        if (minValue) {
          minValue = this.patternDate(minValue, undefined, true, $fieldsetMin);
          if ($fieldsetMin) {
            minValue = this.toValueMin(minValue, $fieldsetMin);
          }
          minValue = this.toValueMin(minValue, this);
          this.minDate = this.getMomentData(minValue, this.datePattern);
        }
        this.setDateValueByMinMax();
      }
      if (this.maxValueSetting && this.maxValueSetting.valueType === 'field' && this.maxValueSetting.field) {
        let fieldMax = this.maxValueSetting.field.split(';');
        let $fieldsetMax = this.form.$fieldset[fieldMax[0]];
        if (fieldMax[1]) {
          $fieldsetMax = this.form.$fieldset[fieldMax[1]];
        }
        this.$watch('formData.' + fieldMax[0], (newValue, oldValue) => {
          if (newValue) {
            newValue = this.patternDate(newValue, undefined, true, $fieldsetMax);
            if ($fieldsetMax) {
              newValue = this.toValueMax(newValue, $fieldsetMax);
            }
            newValue = this.toValueMax(newValue, this);
            this.maxDate = this.getMomentData(newValue, this.datePattern);
          } else {
            this.maxDate = null;
          }
        });
        let maxValue = this.formData[fieldMax[0]];
        if (maxValue) {
          maxValue = this.patternDate(maxValue, undefined, true, $fieldsetMax);
          if ($fieldsetMax) {
            maxValue = this.toValueMax(maxValue, $fieldsetMax);
          }
          maxValue = this.toValueMax(maxValue, this);
          this.maxDate = this.getMomentData(maxValue, this.datePattern);
        }
        this.setDateValueByMinMax();
      }
    },
    // 计算系统时间偏移量
    getSystemOffset({ date, setting }) {
      let _this = this;
      return new Promise((resolve, reject) => {
        if (setting.offsetType == 'naturalDay' || setting.offsetUnit == 'year' || setting.offsetUnit == 'month') {
          // 偏移“年、月”按自然日计算
          const date = _this.getNaturalDayOffset({ setting });
          resolve(date);
        } else {
          this.getWorkingDayOffset({
            date,
            setting
          }).then(date => {
            resolve(date);
          });
        }
      });
    },
    // 计算自然日偏移量
    getNaturalDayOffset({ date, setting }) {
      let calculateDate = null;
      if (setting.offset > 0) {
        calculateDate = moment(date).add(setting.offset, setting.offsetUnit);
      } else {
        calculateDate = moment(date).subtract(Math.abs(setting.offset), setting.offsetUnit);
      }
      return calculateDate;
    },
    // 计算工作日偏移量
    getWorkingDayOffset({ date, setting }) {
      const offset = setting.offset || 0; //偏移量
      let offsetUnit = 'WorkingDay'; //WorkingDay WorkingHour WorkingMinute
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
      date = date.format('yyyy-MM-DD HH:mm:ss');
      return new Promise((resolve, reject) => {
        $axios
          .get('/api/ts/work/time/plan/getWorkDate', {
            params: {
              code: this.configuration.code,
              amout: offset,
              fromDate: date,
              workUnit: offsetUnit,
              autoDelay: true
            }
          })
          .then(({ data }) => {
            if (data.code == 0 && data.data) {
              const dateStr = moment(data.data, 'yyyy-MM-DD HH:mm:ss').format(this.datePattern);
              resolve(this.getMomentData(dateStr, this.datePattern));
            }
          });
      });
    },
    setValue(value) {
      if (value) {
        value = this.patternDate(value, undefined, true);
      }
      let date = null;
      let endDate = this.formData[this.widget.configuration.endDateField];
      if (value) {
        date = this.getMomentData(value, this.datePattern);
        if (this.weekPicker && date) {
          date = date.endOf('week');
        }
        this.formData[this.widget.configuration.code] = date.format(this.contentFormat);
      } else {
        this.formData[this.widget.configuration.code] = '';
        // 如果没有开始时间，结束时间也置空
        if (this.range && endDate) {
          this.form.setFieldValue(this.widget.configuration.endDateField, '');
          endDate = null;
        }
      }
      this.setDateValue(date, endDate);
      if (typeof this.lazySetDefaultValue == 'function') {
        this.lazySetDefaultValue();
      }
    },
    setDateValue(date, endDate) {
      if (this.range) {
        this.date[0] = date;
        if (endDate) {
          endDate = this.patternDate(endDate, undefined, true);
          // 如果是格式是月开头的，开始时间的月份大于结束时间月份（跨年），结束时间的年为明年
          if (!this.contentFormat.startsWith('yyyy') && !this.contentFormat.startsWith('HH')) {
            if (date && (endDate.month() < date.month() || (endDate.month() == date.month() && endDate.date() < date.date()))) {
              endDate = endDate.year(date.year() + 1);
            }
          }
          if (this.weekPicker) {
            this.date[1] = endDate.endOf('week');
          } else {
            this.date[1] = this.getMomentData(endDate, this.datePattern);
          }
        } else {
          this.date[1] = null;
        }
      } else {
        this.date = date;
      }
      if (Array.isArray(this.date)) {
        this.date = [...this.date];
      }
      this.setDateValueByMinMax();
    },
    handleOpenChange(open) {
      if (open) {
        this.isModePanelChangeFlag = 0;
        this.pickerMounted(open);
        if (!this.minMaxValidate) {
          // 最大最小值存在冲突，提示
          this.$message.warning('没有可选的日期时间范围！');
        }
      } else {
        this.pickerMounted(open);
      }
    },
    handlePanelChangeDatetimePicker(value, mode) {
      if (mode == 'time' && !this.minMaxValidate) {
        // 最大最小值存在冲突，提示
        this.$message.warning('没有可选的日期时间范围！');
      }
    },
    /**
     *
     * @param value 日历面板选中值
     * @param mode 日历面板模式[]
     * @param intact 开始结束时间是否全部选上，未选上不关闭弹框
     */
    handlePanelChange(value, mode, intact) {
      this.date = value;
      let newMode = deepClone(mode);
      // 当前组件面板切换到其他面板且不是年月面板，手动修改面板模式为面板当前模式
      if (this.rangeCalendar && mode) {
        let sMode = this.rangeCalendar.sMode;
        if ((mode[0] == 'date' || mode[0] == 'decade') && sMode[0] != mode[0]) {
          newMode[0] = sMode[0];
        }
        if ((mode[1] == 'date' || mode[1] == 'decade') && sMode[1] != mode[1]) {
          newMode[1] = sMode[1];
        }
      }
      if (newMode && Array.isArray(newMode) && (newMode.includes('month') || newMode.includes('year'))) {
        // 月份范围选择，需要这边触发变更（onChange 不会自动触发)
        if (intact && value[0] && value[1]) {
          this.onChange(value, [value[0].format(this.datePattern), value[1].format(this.datePattern)]);
        }
        if (this.$refs['picker_' + this.fieldCode] && mode) {
          let index = this.rangeModeIndex(mode);
          if (index > -1) {
            this.$refs['picker_' + this.fieldCode].$refs.picker.$refs.calendarInstance.setState({ sMode: mode });
            this.isModePanelChangeFlag++;
          } else if (this.isModePanelChangeFlag) {
            this.$refs['picker_' + this.fieldCode].$refs.picker.$refs.calendarInstance.setState({ sMode: mode });
            this.isModePanelChangeFlag--;
          } else if (intact && value[0] && value[1]) {
            // 关闭弹框
            this.$refs['picker_' + this.fieldCode].$refs.picker.setState({ sOpen: false });
          }
        }
      }
    },
    // 计算日期区间面板模式，如果存在且与当前模式不一致，则切换日历模式
    rangeModeIndex(mode) {
      // 日历面板模式，如果要切换的模式排在当前模式前面，则不做改变
      let modeArr = ['date', 'month', 'year', 'decade'];
      if (mode[0] && mode[0] != this.mode[0]) {
        if (modeArr.indexOf(mode[0]) < modeArr.indexOf(this.mode[0])) {
          this.isModePanelChangeFlag = 0;
          return -1;
        }
        return 0;
      } else if (mode[1] && mode[1] != 'date' && mode[1] != this.mode[1]) {
        if (modeArr.indexOf(mode[1]) < modeArr.indexOf(this.mode[1])) {
          this.isModePanelChangeFlag = 0;
          return -1;
        }
        return 1;
      }
      return -1;
    },
    datePickerHandlePanelChange(value, mode) {
      // 日期组件，除了date模式外的其他模式，选中后不会触发onChange
      if (mode != 'date') {
        this.date = value;
        this.onChange(value, value.format(this.datePattern));
        if (this.$refs['picker_' + this.fieldCode]) {
          if (mode) {
            // 切换日历模式
            this.$refs['picker_' + this.fieldCode].$refs.picker.$refs.calendarInstance.setState({ sMode: mode });
          } else if (value) {
            // 关闭弹框
            this.$refs['picker_' + this.fieldCode].$refs.picker.$children[0].setState({ sOpen: false });
          }
        }
      }
    },
    calendarChange(dates, dateStrings) {
      // console.log(dates, dateStrings);
    },
    //设置默认值
    async setDefaultDateValue() {
      if (this.range) {
        this.defaultValue = await this.getDefaultDateValue(this.widget.configuration.defaultValueSetting);
        this.endDefaultValue = await this.getDefaultDateValue(this.widget.configuration.endDefaultValueSetting);
        this.date = [this.defaultValue, this.endDefaultValue];
        this.form.setFieldValue(this.widget.configuration.endDateField, this.endDefaultValue.format(this.contentFormat));
      } else {
        this.defaultValue = await this.getDefaultDateValue(this.widget.configuration.defaultValueSetting);
        this.date = this.defaultValue;
      }
      this.formData[this.widget.configuration.code] = this.defaultValue.format(this.contentFormat);
    },
    // 获取默认值
    getDefaultDateValue(setting) {
      if (setting && setting.valueType !== 'no') {
        if (setting.valueType === 'fixed' && setting.fixedDateValue) {
          // 固定时间
          return this.getMomentData(setting.fixedDateValue, this.datePattern);
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
      var now = moment().format(this.defaultPattern); //当前时间
      var offset = 2; //偏移量
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
      return this.getMomentData(res.data.data, this.datePattern);
    },
    // 不可选日期
    disabledDate(date) {
      let minDate = this.minDate;
      let maxDate = null;
      //如果最大最小值设置有问题，则所有不可选
      if (!this.minMaxValidate) {
        return true;
      }
      // 仅时间选择时，不考虑天的问题
      if (this.contentFormat.startsWith('HH')) {
        return false;
      }
      if (this.maxDate) {
        const maxDateStr = this.maxDate.format(this.datePattern);
        maxDate = this.getMomentData(maxDateStr, this.datePattern);
        //   maxDate = maxDate.add(1, this.maxValueSetting.offsetUnit);
        if (this.weekPicker) {
          maxDate._d = this.maxDate._d;
        }
      }
      if (date) {
        if (minDate != null && maxDate != null) {
          if (this.monthPicker || this.yearPicker) {
            return (
              date.isBefore(minDate, this.monthPicker ? 'month' : 'year') || date.isAfter(maxDate, this.monthPicker ? 'month' : 'year')
            );
          }
          return date.isBefore(minDate, 'day') || date.isAfter(maxDate, 'day');
        } else if (minDate != null) {
          if (this.monthPicker || this.yearPicker) {
            return date.isBefore(minDate, this.monthPicker ? 'month' : 'year');
          }
          return date.isBefore(minDate, 'day');
        } else if (maxDate != null) {
          if (this.monthPicker || this.yearPicker) {
            return date.isAfter(maxDate, this.monthPicker ? 'month' : 'year');
          }
          return date.isAfter(maxDate, 'day');
        }
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
      // 如果最大最小值设置有问题，则所有不可选
      if (!this.minMaxValidate) {
        return this.hoursOfDay;
      }
      // 时间格式下面有分钟数，且有最大最小时间
      if ((this.date || this.timePickerDefaultOpenValue) && this.contentFormat.startsWith('HH') && this.contentFormat.indexOf(':mm') > -1) {
        if (Array.isArray(this.date)) {
        } else {
          let sDate = this.date || this.timePickerDefaultOpenValue;
          let minute = sDate.minute();
          // 当前值的分钟数大于最大分钟数，则小时中的最大值不可选
          if (this.maxDate != null && minute > this.maxDate.minute()) {
            hours = hours.concat(this.hoursOfDay.filter(e => e == this.maxDate.hour()));
          }
          // 当前值的分钟数小于最小分钟数，则小时中的最小值不可选
          if (this.minDate != null && minute < this.minDate.minute()) {
            hours = hours.concat(this.hoursOfDay.filter(e => e == this.minDate.hour()));
          }
        }
      }
      // 时间选择器时，不考虑天的问题
      if (this.contentFormat.startsWith('HH')) {
        if (partial != 'end' && this.minDate != null) {
          hours = hours.concat(this.disableTimeSetting('hour', partial, 'min'));
        }
        if (partial == 'start' && this.maxDate != null) {
          hours = hours.concat(this.disableTimeSetting('hour', partial, 'max'));
        }
        if (partial != 'start' && this.maxDate != null) {
          hours = hours.concat(this.disableTimeSetting('hour', partial, 'max'));
        }
        if (partial == 'end' && this.minDate != null) {
          hours = hours.concat(this.disableTimeSetting('hour', partial, 'min'));
        }
      } else {
        let todayend = this.moment(date).endOf('day');
        let minSameDay = this.minDate != null && todayend.isSame(this.moment(this.minDate).endOf('day'));
        let maxSameDay = this.maxDate != null && todayend.isSame(this.moment(this.maxDate).endOf('day'));
        if (partial != 'end' && minSameDay) {
          hours = hours.concat(this.disableTimeSetting('hour', partial, 'min'));
        }
        if (partial == 'start' && maxSameDay) {
          hours = hours.concat(this.disableTimeSetting('hour', partial, 'max'));
        }

        if (partial != 'start' && maxSameDay) {
          hours = hours.concat(this.disableTimeSetting('hour', partial, 'max'));
        }
        if (partial == 'end' && minSameDay) {
          hours = hours.concat(this.disableTimeSetting('hour', partial, 'min'));
        }
      }
      this.timePickerChangeByMinMax(partial);
      return hours;
    },

    disabledMinutes(hour, date, partial) {
      /**
       * 不可选的分钟：选择的日期为限制日期同一天时候，且在限制小时内
       */
      let minutes = [];
      // 如果最大最小值设置有问题，则所有分钟数不可选
      if (!this.minMaxValidate) {
        return this.minutesOfHour;
      }
      if (!hour && this.rangeCalendar) {
        if (partial == 'end') {
          hour = this.rangeCalendar.sValue[1].hour();
        }
        if (partial == 'start') {
          hour = this.rangeCalendar.sValue[0].hour();
        }
      }
      // 时间格式下面有秒，且有最大最小时间
      if ((this.date || this.timePickerDefaultOpenValue) && this.contentFormat.startsWith('HH') && this.contentFormat.indexOf(':ss') > -1) {
        if (Array.isArray(this.date)) {
        } else {
          let sDate = this.date || this.timePickerDefaultOpenValue;
          let second = sDate.second();
          // 当前值的秒数大于最大秒数，则分钟数中的最大值不可选
          if (this.maxDate != null && second > this.maxDate.second() && hour == this.maxDate.hour()) {
            minutes = minutes.concat(this.minutesOfHour.filter(e => e == this.maxDate.minute()));
          }
          // 当前值的秒数小于最小秒数，则分钟中的最小值不可选
          if (this.minDate != null && second < this.minDate.second() && hour == this.minDate.hour()) {
            minutes = minutes.concat(this.minutesOfHour.filter(e => e == this.minDate.minute()));
          }
        }
      }
      // 时间选择器时，不考虑天的问题
      if (this.contentFormat.startsWith('HH')) {
        if (partial != 'end' && this.minDate != null && hour == this.minDate.hour()) {
          minutes = minutes.concat(this.disableTimeSetting('minute', partial, 'min'));
        }
        if (partial == 'start' && this.maxDate != null && hour == this.maxDate.hour()) {
          minutes = minutes.concat(this.disableTimeSetting('minute', partial, 'max'));
        }
        if (partial != 'start' && this.maxDate != null && hour == this.maxDate.hour()) {
          minutes = minutes.concat(this.disableTimeSetting('minute', partial, 'max'));
        }
        if (partial == 'end' && this.minDate != null && hour == this.minDate.hour()) {
          minutes = minutes.concat(this.disableTimeSetting('minute', partial, 'min'));
        }
      } else {
        let todayend = this.moment(date).endOf('day');
        let minSameDay = this.minDate != null && todayend.isSame(this.moment(this.minDate).endOf('day'));
        let maxSameDay = this.maxDate != null && todayend.isSame(this.moment(this.maxDate).endOf('day'));
        if (partial != 'end' && minSameDay && hour == this.minDate.hour()) {
          minutes = minutes.concat(this.disableTimeSetting('minute', partial, 'min'));
        }

        if (partial == 'start' && maxSameDay && hour == this.maxDate.hour()) {
          minutes = minutes.concat(this.disableTimeSetting('minute', partial, 'max'));
        }

        if (partial != 'start' && maxSameDay && hour == this.maxDate.hour()) {
          minutes = minutes.concat(this.disableTimeSetting('minute', partial, 'max'));
        }
        if (partial == 'end' && minSameDay && hour == this.minDate.hour()) {
          minutes = minutes.concat(this.disableTimeSetting('minute', partial, 'min'));
        }
      }
      this.timePickerChangeByMinMax(partial);
      return minutes;
    },
    disabledSeconds(hour, minute, date, partial) {
      /**
       * 不可选的秒数：选择的日期为限制日期同一天时候，且在限制小时与限制分钟内
       */
      let seconds = [];
      // 如果最大最小值设置有问题，则所有不可选
      if (!this.minMaxValidate) {
        return this.secondsOfMinute;
      }
      if (this.rangeCalendar) {
        if (partial == 'end') {
          if (!hour) {
            hour = this.rangeCalendar.sValue[1].hour();
          }
          if (!minute) {
            minute = this.rangeCalendar.sValue[1].minute();
          }
        }
        if (partial == 'start') {
          if (!hour) {
            hour = this.rangeCalendar.sValue[0].hour();
          }
          if (!minute) {
            minute = this.rangeCalendar.sValue[0].minute();
          }
        }
      }
      // 时间选择器时，不考虑天的问题
      if (this.contentFormat.startsWith('HH')) {
        if (partial != 'end' && this.minDate != null && hour == this.minDate.hour() && minute == this.minDate.minute()) {
          seconds = seconds.concat(this.disableTimeSetting('second', partial, 'min'));
        }
        if (partial == 'start' && this.maxDate != null && hour == this.maxDate.hour() && minute == this.maxDate.minute()) {
          seconds = seconds.concat(this.disableTimeSetting('second', partial, 'max'));
        }
        if (partial != 'start' && this.maxDate != null && hour == this.maxDate.hour() && minute == this.maxDate.minute()) {
          seconds = seconds.concat(this.disableTimeSetting('second', partial, 'max'));
        }
        if (partial == 'end' && this.minDate != null && hour == this.minDate.hour() && minute == this.minDate.minute()) {
          seconds = seconds.concat(this.disableTimeSetting('second', partial, 'min'));
        }
      } else {
        let todayend = this.moment(date).endOf('day');
        let minSameDay = this.minDate != null && todayend.isSame(this.moment(this.minDate).endOf('day'));
        let maxSameDay = this.maxDate != null && todayend.isSame(this.moment(this.maxDate).endOf('day'));
        if (partial != 'end' && minSameDay && hour == this.minDate.hour() && minute == this.minDate.minute()) {
          seconds = seconds.concat(this.disableTimeSetting('second', partial, 'min'));
        }

        if (partial == 'start' && maxSameDay && hour == this.maxDate.hour() && minute == this.maxDate.minute()) {
          seconds = seconds.concat(this.disableTimeSetting('second', partial, 'max'));
        }

        if (partial != 'start' && maxSameDay && hour == this.maxDate.hour() && minute == this.maxDate.minute()) {
          seconds = seconds.concat(this.disableTimeSetting('second', partial, 'max'));
        }
        if (partial == 'end' && minSameDay && hour == this.minDate.hour() && minute == this.minDate.minute()) {
          seconds = seconds.concat(this.disableTimeSetting('second', partial, 'min'));
        }
      }
      this.timePickerChangeByMinMax(partial);
      return seconds;
    },
    disableTimeSetting(type, partial, minOrMax) {
      let times = [];
      let list = this[{ second: 'secondsOfMinute', minute: 'minutesOfHour', hour: 'hoursOfDay' }[type]];
      if (partial != 'end' && minOrMax == 'min') {
        times = list.filter(e => e < this.minDate[type]());
      }

      if (partial == 'start' && minOrMax == 'max') {
        times = times.concat(list.slice(this.maxDate[type]() + 1));
      }

      if (partial != 'start' && minOrMax == 'max') {
        times = times.concat(list.slice(this.maxDate[type]() + 1));
      }
      if (partial == 'end' && minOrMax == 'min') {
        times = times.concat(list.filter(e => e < this.minDate[type]()));
      }
      return times;
    },
    onChange(date, dateString) {
      if (this.datetimePicker && !this.isOnChangeFlag) {
        this.isOnChangeFlag = true;
        setTimeout(() => {
          // 留2秒，让时间数判断
          this.isOnChangeFlag = false;
        }, 2000);
      }
      if (this.weekPicker) {
        /**
         * 周选择器在年末时，如果年底(2024年12月31日)最后一周与来年(2025)第一周在同一周，算是在来年第一周
         * 但是moment（yyyy-ww）格式化后，如果是年底最后一周数据被选中，文本会显示为年底(2024年)的第一周
         * */
        if (this.range) {
          if (date[0] && date[1]) {
            let date0 = moment(date[0]).endOf('week');
            let date1 = moment(date[1]).endOf('week');
            if (date0.year() != date[0].year()) {
              date[0] = date0;
              dateString[0] = date0.format(this.getFixedFormat);
            }
            if (date1.year() != date[1].year()) {
              date[1] = date1;
              dateString[1] = date1.format(this.getFixedFormat);
            }
          }
          // 设置结束日期字段值
          this.formData[this.widget.configuration.code] = date[0] ? date[0].format(this.contentFormat) : null;
          this.form.setFieldValue(this.widget.configuration.endDateField, date[1] ? date[1].format(this.contentFormat) : null);
        } else {
          if (date) {
            let date0 = moment(date).endOf('week');
            if (date0.year() != date.year()) {
              this.date = date0;
              date = date0;
              dateString = date.format(this.getFixedFormat);
            }
          }
          this.formData[this.widget.configuration.code] = date ? date.format(this.contentFormat) : null;
        }
      } else {
        if (this.range) {
          // 设置结束日期字段值
          this.formData[this.widget.configuration.code] = date[0] ? this.patternDate(dateString[0], date[0]) : null;
          this.form.setFieldValue(this.widget.configuration.endDateField, date[1] ? this.patternDate(dateString[1], date[1]) : null);
        } else {
          this.formData[this.widget.configuration.code] = date ? this.patternDate(dateString, date) : null;
        }
      }
      this.emitChange();
    },
    // 显示值
    displayValue() {
      return this.computedLabel;
    },
    // 表单值不为空
    formatFormDataValueIfNotNull() {
      if (this.formData[this.widget.configuration.code] != undefined) {
        let _date = null;
        if (this.formData[this.widget.configuration.code]) {
          _date = this.getMomentData(this.formData[this.widget.configuration.code], this.datePattern);
        }
        if (this.range) {
          this.date = [_date, null];
          const endDateFieldKey = this.widget.configuration.endDateField;
          const endDateField = this.formData[endDateFieldKey];
          if (endDateField) {
            this.date[1] = this.getMomentData(endDateField, this.datePattern);
            this.form.setFieldValue(endDateFieldKey, this.date[1].format(this.contentFormat));
          }
        } else {
          this.date = _date;
        }
        this.emitChange();
      }
    },
    // 格式化日期/时间
    patternDate(val, date, toComponent, $field) {
      // $field字段组件
      if (!$field) {
        $field = this;
      }
      if (val) {
        let datePatternJson = $field.widget.configuration.datePatternJson;
        if (datePatternJson && datePatternJson.mode) {
          // value的格式不含年的，应默认给予当前年，方可正常显示组件
          const datePattern = $field.widget.configuration.zeroShow ? datePatternJson.key0 : datePatternJson.key;
          if (datePatternJson.mode == 'date' && datePattern.indexOf('yyyy') == -1) {
            val = this.dateAddYear(val, date);
          }
        }
        // val = this.moment(val, this.datePattern);
        if (toComponent) {
          //用于组件显示，该值为完整的moment时间值
          val = this.getMomentData(val, $field.datePattern);
        } else {
          val = this.getMomentData(val, $field.datePattern).format($field.contentFormat);
        }
      }
      return val;
    },
    // 当前值结构有没有包含年(仅对格式化模式date,日期格式没有年的情况）
    dateAddYear(val, date) {
      let day = val.split(' ')[0];
      const year = date ? date.format('yyyy') : moment().year();
      if (day.indexOf('-') > -1) {
        if (day.split('-')[0].length != 4) {
          val = year + '-' + val;
        }
      } else if (day.indexOf('/') > -1 && day.split('/')[0].length != 4) {
        if (day.split('/')[0].length != 4) {
          val = year + '/' + val;
        }
      } else if (day.indexOf('年') == -1) {
        val = year + '年' + val;
      }
      return val;
    },
    onFilter({ searchValue, comparator, source, ignoreCase }) {
      let sourceMoment = typeof source == 'string' ? this.getMomentData(source, this.datePattern) : source;
      let searchMoment = null;
      if (Array.isArray(searchValue)) {
        searchMoment =
          typeof searchValue[0] == 'string' || searchValue[0] == undefined
            ? [
                searchValue == '' ? undefined : this.getMomentData(searchValue[0], this.datePattern),
                searchValue[1] == '' ? undefined : this.getMomentData(searchValue[1], this.datePattern)
              ]
            : searchValue;
      } else {
        searchMoment = typeof searchValue == 'string' ? this.getMomentData(searchValue, this.datePattern) : searchValue;
      }
      if (sourceMoment != undefined) {
        if (comparator == 'like') {
          return typeof source == 'string ' ? source.indexOf(searchValue) != -1 : false;
        } else if (comparator == '>=') {
          return sourceMoment.isSameOrAfter(searchMoment);
        } else if (comparator == '<=') {
          return sourceMoment.isSameOrBefore(searchMoment);
        } else if (comparator == '<') {
          return sourceMoment.isBefore(searchMoment);
        } else if (comparator == '>') {
          return sourceMoment.isAfter(searchMoment);
        } else if (comparator == '!=') {
          return !searchMoment.isSame(sourceMoment);
        } else if (comparator == 'between') {
          return sourceMoment.isBetween(searchMoment[0], searchMoment[1], undefined, '[]');
        }
        return searchMoment.isSame(sourceMoment);
      }

      return false;
    },
    toValueMin(value, $field) {
      if (value && $field) {
        let { contentFormat } = $field;
        let mode = $field.widget.configuration.datePatternJson.mode;
        let datePatternType = $field.widget.configuration.datePatternType;
        let _value = '';
        if (datePatternType == 'date') {
          if (this == $field && !this.contentFormat.startsWith('yyyy')) {
            value = value.year(moment().year());
          }
          if (mode == 'year') {
            _value = value.startOf('year').format('yyyy-MM-DD');
          } else if (mode == 'month') {
            _value = value.startOf('month').format('yyyy-MM-DD');
          } else if (mode == 'week') {
            _value = value.startOf('week').format('yyyy-MM-DD');
          } else if (mode == 'date') {
            _value = value.format('yyyy-MM-DD');
          }
          _value += ' 00:00:00';
          value = moment(_value);
        } else if (datePatternType == 'datetime') {
          let datePatternJson = $field.widget.configuration.datePatternJson;
          if (this == $field && !datePatternJson.key0.startsWith('yyyy')) {
            value = value.year(moment().year());
          }
          if (datePatternJson.key0.indexOf('ss') > -1) {
            _value = value.format('yyyy-MM-DD HH:mm:ss');
          } else if (datePatternJson.key0.indexOf('mm') > -1) {
            _value = value.format('yyyy-MM-DD HH:mm');
            _value += ':00';
          } else if (datePatternJson.key0.indexOf('HH') > -1) {
            _value = value.format('yyyy-MM-DD HH');
            _value += ':00:00';
          }
          value = moment(_value);
        } else if (datePatternType == 'time') {
          if (this == $field) {
            value = value.year(moment().year()).month(moment().month()).date(moment().date());
          }
          if (contentFormat.indexOf('ss') > -1) {
            _value = value.format('yyyy-MM-DD HH:mm:ss');
          } else if (contentFormat.indexOf('mm') > -1) {
            _value = value.format('yyyy-MM-DD HH:mm');
            _value += ':00';
          } else if (contentFormat.indexOf('HH') > -1) {
            _value = value.format('yyyy-MM-DD HH');
            _value += ':00:00';
          }
          value = moment(_value);
        }
      }
      return value;
    },
    toValueMax(value, $field) {
      if (value && $field) {
        let { contentFormat } = $field;
        let mode = $field.widget.configuration.datePatternJson.mode;
        let datePatternType = $field.widget.configuration.datePatternType;
        let _value = '';
        if (datePatternType == 'date') {
          // 无年的时候，默认当前年
          if (this == $field && !this.contentFormat.startsWith('yyyy')) {
            // 如果有最小值，最小值的月份大于最大值月份（跨年），最大值的年为明年，反之为今年
            if (
              this.minDate &&
              (value.month() < this.minDate.month() || (value.month() == this.minDate.month() && value.date() < this.minDate.date()))
            ) {
              value = value.year(moment().year() + 1);
            } else {
              value = value.year(moment().year());
            }
          }
          if (mode == 'year') {
            _value = value.endOf('year').format('yyyy-MM-DD');
          } else if (mode == 'month') {
            _value = value.endOf('month').format('yyyy-MM-DD');
          } else if (mode == 'week') {
            _value = value.endOf('week').format('yyyy-MM-DD');
          } else if (mode == 'date') {
            _value = value.format('yyyy-MM-DD');
          }
          _value += ' 23:59:59';
          value = moment(_value);
        } else if (datePatternType == 'datetime') {
          let datePatternJson = $field.widget.configuration.datePatternJson;
          // 无年的时候，默认当前年
          if (this == $field && !datePatternJson.key0.startsWith('yyyy')) {
            // 如果有最小值，最小值的月份大于最大值月份（跨年），最大值的年为明年，反之为今年
            if (
              this.minDate &&
              (value.month() < this.minDate.month() || (value.month() == this.minDate.month() && value.date() < this.minDate.date()))
            ) {
              value = value.year(moment().year() + 1);
            } else {
              value = value.year(moment().year());
            }
          }
          if (datePatternJson.key0.indexOf('ss') > -1) {
            _value = value.format('yyyy-MM-DD HH:mm:ss');
          } else if (datePatternJson.key0.indexOf('mm') > -1) {
            _value = value.format('yyyy-MM-DD HH:mm');
            _value += ':59';
          } else if (datePatternJson.key0.indexOf('HH') > -1) {
            _value = value.format('yyyy-MM-DD HH');
            _value += ':59:59';
          }
          value = moment(_value);
        } else if (datePatternType == 'time') {
          if (this == $field) {
            value = value.year(moment().year()).month(moment().month()).date(moment().date());
          }
          if (contentFormat.indexOf('ss') > -1) {
            _value = value.format('yyyy-MM-DD HH:mm:ss');
          } else if (contentFormat.indexOf('mm') > -1) {
            _value = value.format('yyyy-MM-DD HH:mm');
            _value += ':59';
          } else if (contentFormat.indexOf('HH') > -1) {
            _value = value.format('yyyy-MM-DD HH');
            _value += ':59:59';
          }
          value = moment(_value);
        }
      }
      return value;
    },
    // 最大最小值生成后重新计算日期组件值
    setDateValueByMinMax() {
      // 如果是格式是月开头的，开始时间的月份大于结束时间月份（跨年），结束时间的年为明年
      if (!this.contentFormat.startsWith('yyyy') && !this.contentFormat.startsWith('HH')) {
        if (this.range) {
          if (this.date[0] && this.date[1]) {
            if (this.minDate && this.maxDate) {
              // 有最大最小值，且年份不一样，以最小值年份为基准
              if (this.minDate.year() !== this.maxDate.year()) {
                // 开始时间的月比最小时间小或者同月但日期小，则开始时间和结束时间都是明年的
                if (
                  this.date[0].month() < this.minDate.month() ||
                  (this.date[0].month() == this.minDate.month() && this.date[0].date() < this.minDate.date())
                ) {
                  this.date[0].set('year', this.minDate.year() + 1);
                  this.date[1].set('year', this.minDate.year() + 1);
                }
              }
            } else if (this.maxDate) {
              // 有最大时间没有最小时间,开始时间的月比最大时间大或者同月但日期大，则开始时间是去年的
              if (
                this.date[0].month() > this.maxDate.month() ||
                (this.date[0].month() == this.maxDate.month() && this.date[0].date() > this.maxDate.date())
              ) {
                this.date[0].set('year', this.maxDate.year() - 1);
              }
              // 有最大时间没有最小时间,开始时间的月比最大时间大或者同月但日期大，则开始时间是去年的
              if (
                this.date[1].month() > this.maxDate.month() ||
                (this.date[1].month() == this.maxDate.month() && this.date[1].date() > this.maxDate.date())
              ) {
                this.date[1].set('year', this.maxDate.year() - 1);
              } else {
                this.date[1].set('year', this.maxDate.year());
              }
            } else if (this.minDate) {
              // 有最小时间没有最大时间,开始时间的月比最小时间小或者同月但日期小，则开始时间是明年的
              if (
                this.date[0].month() < this.minDate.month() ||
                (this.date[0].month() == this.minDate.month() && this.date[0].date() < this.minDate.date())
              ) {
                this.date[0].set('year', this.minDate.year() + 1);
                this.date[1].set('year', this.minDate.year() + 1);
              }
            }
          }
        } else if (this.date) {
          if (this.minDate && this.maxDate) {
            // 有最大最小值，且年份不一样，以最小值年份为基准
            if (this.minDate.year() !== this.maxDate.year()) {
              // 开时间的月比最小时间小或者同月但日期小，则时间和结束时间都是明年的
              if (
                this.date.month() < this.minDate.month() ||
                (this.date.month() == this.minDate.month() && this.date.date() < this.minDate.date())
              ) {
                this.date.set('year', this.minDate.year() + 1);
              }
            }
          } else if (this.maxDate) {
            // 有最大时间没有最小时间,时间的月比最大时间大或者同月但日期大，则时间是去年的
            if (
              this.date.month() > this.maxDate.month() ||
              (this.date.month() == this.maxDate.month() && this.date.date() > this.maxDate.date())
            ) {
              this.date.set('year', this.maxDate.year() - 1);
            }
          } else if (this.minDate) {
            // 有最小时间没有最大时间,开始时间的月比最小时间小或者同月但日期小，则开始时间是明年的
            if (
              this.date.month() < this.minDate.month() ||
              (this.date.month() == this.minDate.month() && this.date.date() < this.minDate.date())
            ) {
              this.date.set('year', this.minDate.year() + 1);
            }
          }
        }
      }
    },
    // 时间选择器修改后，更新组件和字段值
    timePickerChangeByMinMax(partial) {
      if (this.isOnChangeFlag) {
        // 日期时间选择器,如果时间超出范围，则约束在最大最小值范围内
        if (this.datetimePicker) {
          if (this.range) {
            if (partial == 'start') {
              this.date[0] = this.setDateTimeByMinMax(this.date[0]);
            }
            if (partial == 'end') {
              this.date[1] = this.setDateTimeByMinMax(this.date[1]);
            }
            this.onChange(this.date, [
              this.date[0] ? this.date[0].format(this.getFixedFormat) : null,
              this.date[1] ? this.date[1].format(this.getFixedFormat) : null
            ]);
          } else {
            if (this.date) {
              this.date = this.setDateTimeByMinMax(this.date);
            }
            this.onChange(this.date, this.date ? this.date.format(this.getFixedFormat) : null);
          }
        }
      }
    },
    setDateTimeByMinMax(date) {
      if (date) {
        if (this.minDate != null) {
          if (date.isBefore(this.minDate)) {
            date = this.minDate;
          }
        }
        if (this.maxDate != null) {
          if (date.isAfter(this.maxDate)) {
            date = this.maxDate;
          }
        }
      }
      return date;
    },
    addCustomRules() {
      return this.addDatePickerErrorRules();
    },
    // 增加时间选择校验
    addDatePickerErrorRules() {
      let rule = {
        trigger: 'change',
        validator: (rule, value, callback) => {
          if (this.date) {
            if (Array.isArray(this.date)) {
              if (this.date[0] && this.date[1]) {
                let date0 = moment(this.date[0].format(this.defaultPattern));
                let date1 = moment(this.date[1].format(this.defaultPattern));
                if (date0.isAfter(date1)) {
                  callback(this.$t('WidgetFormDatePicker.validateError.startBeforeEndMsg', '开始时间应在结束时间之前'));
                  return false;
                } else if (
                  !(this.minValueSetting && this.minValueSetting.valueType === 'system') &&
                  this.minDate &&
                  date0.isBefore(this.minDate)
                ) {
                  callback(this.$t('WidgetFormDatePicker.validateError.startBeforeMinMsg', '开始时间应在最小时间之后'));
                  return false;
                } else if (
                  !(this.maxValueSetting && this.maxValueSetting.valueType === 'system') &&
                  this.maxDate &&
                  date1.isAfter(this.maxDate)
                ) {
                  callback(this.$t('WidgetFormDatePicker.validateError.endAfterMaxMsg', '结束时间应在最大时间之前'));
                  return false;
                }
                // } else if (this.date[0]) {
                //   callback(this.$t('WidgetFormDatePicker.validateError.endTimeNull', '请选择结束时间'));
                //   return false;
                // } else if (this.date[1]) {
                //   callback(this.$t('WidgetFormDatePicker.validateError.startTimeNull', '请选择开始时间'));
                //   return false;
              }
            } else {
              let date0 = moment(this.date.format(this.defaultPattern));
              if (!(this.minValueSetting && this.minValueSetting.valueType === 'system') && this.minDate && date0.isBefore(this.minDate)) {
                callback(this.$t('WidgetFormDatePicker.validateError.dateBeforeMinMsg', '选择时间应在最小时间之后'));
                return false;
              } else if (
                !(this.maxValueSetting && this.maxValueSetting.valueType === 'system') &&
                this.maxDate &&
                date0.isAfter(this.maxDate)
              ) {
                callback(this.$t('WidgetFormDatePicker.validateError.dateAfterMaxMsg', '选择时间应在最大时间之前'));
                return false;
              }
            }
          }
          callback();
        }
      };
      if (this.rules) {
        this.rules.push(rule);
      }
      return rule;
    }
  }
};
</script>

<style lang="less">
.time-range-container {
  .ant-calendar-range.ant-calendar-time {
    .ant-calendar-time-picker-inner {
      padding-top: 0;
    }
    .ant-calendar-footer-show-ok {
      display: none;
    }
  }
}
</style>
