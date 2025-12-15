<template>
  <div>
    <a-form-model-item :label="label">
      <a-select v-model="defaultValueType" @change="onSelectChange" :getPopupContainer="getPopupContainerByPs()">
        <a-select-option value="no">无</a-select-option>
        <a-select-option value="fixed">固定日期时间</a-select-option>
        <a-select-option value="system">当前系统时间</a-select-option>
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
import moment from 'moment';
import { getPopupContainerNearestPs as getPopupContainerByPs } from '@framework/vue/utils/function.js';
export default {
  name: 'DateFieldDefaultValue',
  props: {
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
    }
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
    }
  },
  created() {
    this.defaultValueType = this.setting.valueType;

    if (this.setting.fixedDateValue) {
      //  '2024年10月23日'.split(/[\u4e00-\u9fa5]/g)
      this.defaultValue = moment(this.setting.fixedDateValue, this.defaultPattern);
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
    // 改变固定时间
    onChangeDate(value, dateStr) {
      this.setEmit({ value, dateStr });
    },
    panelChange(value, mode) {
      this.setEmit({ value, mode });
    },
    setEmit({ value, mode, dateStr }) {
      this.defaultValue = value;
      let date = value.format(this.defaultPattern);
      this.setting.fixedDateValue = date;
      if (this.isDefault && this.starDefDate) {
        this.configuration.defaultValue = date;
      }
      this.$emit('changeFixedDate', value);
    },
    clear() {
      this.configuration.valueCreateMethod = '4';
      this.configuration.defaultValue = null;
      this.defaultValue = null;
    }
  }
};
</script>
