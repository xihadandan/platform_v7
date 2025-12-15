<template>
  <div>
    <a-form-model-item :label="label">
      <a-select v-model="defaultValueType" @change="onSelectChange" :getPopupContainer="getPopupContainerByPs()">
        <a-select-option value="no">无</a-select-option>
        <a-select-option value="fixed">固定日期时间</a-select-option>
        <a-select-option value="system">当前系统时间</a-select-option>
        <a-select-option value="field" v-if="!isDefault">表单字段</a-select-option>
      </a-select>
      <template v-if="defaultValueType == 'fixed'">
        <template v-if="isDefault">
          <a-date-picker
            :format="defaultPattern"
            :showTime="{ format: defaultPattern }"
            v-model="defaultValue"
            style="width: 100%"
            @change="onChangeDate"
            allowClear
          />
        </template>
        <template v-else>
          <template v-if="configuration.datePatternType == 'time'">
            <a-time-picker :format="fixedFormat" v-model="defaultValue" style="width: 100%" @change="onChangeDate" allowClear />
          </template>
          <template v-else-if="configuration.datePatternJson.mode == 'week'">
            <a-week-picker
              :format="fixedFormat"
              :showTime="{ format: fixedFormat }"
              v-model="defaultValue"
              style="width: 100%"
              @change="onChangeWeekDate"
              allowClear
            />
          </template>
          <template v-else-if="configuration.datePatternType == 'datetime'">
            <a-date-picker
              :format="fixedFormat"
              :showTime="{ format: fixedFormat }"
              v-model="defaultValue"
              style="width: 100%"
              @change="onChangeDate"
              allowClear
            />
          </template>
          <template v-else>
            <a-date-picker
              :format="fixedFormat"
              :mode="getFixedMode"
              v-model="defaultValue"
              style="width: 100%"
              @change="onChangeDate"
              @panelChange="panelChange"
              allowClear
            />
          </template>
        </template>
      </template>
      <template v-if="defaultValueType == 'field'">
        <a-select
          show-search
          v-model="setting.field"
          placeholder="请选择表单字段"
          :allowClear="true"
          :filter-option="filterOption"
          :options="formDatePickerFieldOptions"
          :getPopupContainer="getPopupContainerByPs()"
        ></a-select>
      </template>
    </a-form-model-item>
    <template v-if="defaultValueType == 'system'">
      <a-form-model-item>
        <template #label>
          <FormItemTooltip
            label="日期偏移量"
            text="即设置默认时间和当前系统时间的偏移量，例如：当前系统时间为01-18，偏移量为1自然日，则默认时间为01-19，偏移量支持设置负值。"
          ></FormItemTooltip>
        </template>
        <a-input-number :precision="0" v-model="setting.offset" style="width: 80px; margin-right: 8px"></a-input-number>
        <a-select v-model="setting.offsetUnit" style="width: 80px" :getPopupContainer="getPopupContainerByPs()">
          <a-select-option v-for="item in dateTypeOptions" :key="item.key">
            {{ item.label }}
          </a-select-option>
        </a-select>
      </a-form-model-item>
      <a-form-model-item label="时间计算" v-show="showOffsetType">
        <a-radio-group size="small" v-model="setting.offsetType" button-style="solid">
          <a-radio-button value="weekday">按工作日计算</a-radio-button>
          <a-radio-button value="naturalDay">按自然日计算</a-radio-button>
        </a-radio-group>
      </a-form-model-item>
    </template>
  </div>
</template>
<script type="text/babel">
import moment, { months } from 'moment';
import { getPopupContainerByPs } from '@dyform/app/web/page/dyform-designer/utils';
export default {
  name: 'DateFieldDefaultValue',
  props: {
    designer: Object,
    widget: Object,
    configuration: {
      type: Object
    },
    setting: {
      type: Object
    },
    isDefault: {
      type: Boolean,
      default: false
    }, // true默认 false最大值/最小值
    starDefDate: {
      type: Boolean,
      default: false
    },
    label: {
      type: String,
      default: '默认时间'
    },
    weekMax: Boolean
  },
  data() {
    return {
      defaultPattern: 'yyyy-MM-DD HH:mm:ss',
      defaultValue: null,
      defaultValueType: 'no',
      dateTypeOptions: [
        { key: 'year', label: '年' },
        { key: 'month', label: '月' },
        { key: 'day', label: '日' },
        { key: 'hour', label: '时' },
        { key: 'minute', label: '分' }
        // { key: 'second', label: '秒' }
      ]
    };
  },
  computed: {
    showOffsetType() {
      let show = true;
      if (this.setting.offsetUnit == 'year' || this.setting.offsetUnit == 'month') {
        show = false;
        this.setting.offsetType = 'naturalDay';
      }
      return show;
    },
    // 固定时间 输出格式
    fixedFormat() {
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
    formDatePickerFieldOptions() {
      // this.designer.widgetIdMap[id].configuraiont.default ==111
      let opt = [];
      if (this.designer && this.widget && this.designer.FieldWidgets && this.designer.FieldWidgets.length) {
        for (let k = 0, len = this.designer.FieldWidgets.length; k < len; k++) {
          let field = this.designer.FieldWidgets[k];
          if (field.configuration.code && field.id != this.widget.id && field.wtype == 'WidgetFormDatePicker') {
            opt.push({
              id: field.id,
              label: field.configuration.name || field.configuration.code,
              value: field.configuration.code
            });
            if (field.configuration.range) {
              opt.push({
                id: field.id + '_endDateField',
                label: (field.configuration.name || field.configuration.code) + '(结束时间)',
                value: field.configuration.endDateField + ';' + field.configuration.code
              });
            }
          }
        }
      }
      return opt;
    }
  },
  created() {
    this.defaultValueType = this.setting.valueType;

    if (this.setting.fixedDateValue) {
      //  '2024年10月23日'.split(/[\u4e00-\u9fa5]/g)
      this.defaultValue = moment(this.setting.fixedDateValue, this.defaultPattern);
    }
    if (this.setting.field == undefined) {
      this.$set(this.setting, 'field', '');
    }
  },
  methods: {
    getPopupContainerByPs,
    // 改变类型
    onSelectChange(v) {
      this.setting.valueType = v;
      if (v === 'fixed') {
        this.defaultValue = this.setting.fixedDateValue;
      }
      if (this.isDefault) {
        this.configuration.hasDefaultValue = v !== 'no';
        if (this.starDefDate && v === 'system') {
          this.configuration.defaultValue = '{CURRENTDATETIMESEC}';
        }
        if (v === 'no') {
          this.clear();
        }
      }
      if (v === 'system') {
        this.setting.systemValue = '{CURRENTDATETIMESEC}';
      }
      this.$emit('changeDefType', v);
    },
    onChangeWeekDate(value, dateStr) {
      if (value) {
        let date = moment(moment(value._d).endOf('week').format('yyyy-MM-DD HH:mm:ss'));
        if (!date.isSame(value)) {
          this.defaultValue = date;
          this.onChangeDate(date, dateStr);
        } else {
          this.onChangeDate(value, dateStr);
        }
      }
    },
    // 改变固定时间
    onChangeDate(value, dateStr) {
      this.setEmit({ value, dateStr });
    },
    panelChange(value, mode) {
      this.setEmit({ value, mode });
    },
    setEmit({ value, mode, dateStr }) {
      this.defaultValue = value;
      if (value) {
        let date = value.format(this.defaultPattern);
        this.setting.fixedDateValue = date;
        if (this.isDefault && this.starDefDate) {
          this.configuration.defaultValue = date;
        }
      } else {
        this.setting.fixedDateValue = null;
        if (this.isDefault && this.starDefDate) {
          this.configuration.defaultValue = null;
        }
      }
      this.$emit('changeFixedDate', value);
    },
    clear() {
      this.configuration.valueCreateMethod = '4';
      this.configuration.defaultValue = null;
      this.defaultValue = null;
    },
    filterOption(input, option) {
      return option.componentOptions.children[0].text.toLowerCase().indexOf(input.toLowerCase()) >= 0;
    }
  }
};
</script>
