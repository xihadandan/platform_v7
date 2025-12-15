<template>
  <div>
    <a-form-model-item label="字段类型">
      <a-select
        v-model="widget.configuration.dbDataType"
        :options="dbDataTypeOptions"
        :style="{ width: '100%' }"
        :disabled="widget.column != undefined"
        @change="onChangeDbDataType"
        :getPopupContainer="getPopupContainerByPs()"
      ></a-select>
    </a-form-model-item>
    <template v-if="widget.configuration.dbDataType == '15' || widget.configuration.dbDataType == '12'">
      <div class="item-sub-title">
        <a-icon type="minus" :rotate="90" :style="{ color: '#1890ff' }" />
        <span>位数</span>
      </div>
      <a-form-model-item label="小数位数">
        <a-input-number v-model="widget.configuration.decimalPlacesNumber" :min="0" />
      </a-form-model-item>
    </template>
    <template v-if="widget.configuration.dbDataType == '17'">
      <div class="item-sub-title">
        <a-icon type="minus" :rotate="90" :style="{ color: '#1890ff' }" />
        <span>位数</span>
      </div>
      <a-form-model-item label="精度位（有效数）" prop="precision">
        <a-input-number v-model="widget.configuration.precision" :min="0" :max="16" :disabled="widget.column != undefined"></a-input-number>
      </a-form-model-item>
      <a-form-model-item label="小数位">
        <a-input-number v-model="widget.configuration.scale" :min="0" :disabled="widget.column != undefined"></a-input-number>
      </a-form-model-item>
    </template>
    <template v-else>
      <FieldLengthInput :widget="widget" :max="lengthMax" :min="1" :isDisabled="true" />
    </template>
    <a-form-model-item label="千位分隔符" class="item-lh" :wrapper-col="{ style: { textAlign: 'right' } }">
      <a-switch checked-children="是" un-checked-children="否" v-model="widget.configuration.formatNumber" />
    </a-form-model-item>
    <FieldDefaultValue :configuration="widget.configuration" :variables="[]" :dataType="widget.configuration.dataType" />
    <FieldFormulaInput :configuration="widget.configuration" :widget="widget" :designer="designer" />
    <a-collapse :bordered="false" expandIconPosition="right">
      <a-collapse-panel key="component_mode_properties" header="组件模式属性">
        <a-form-model-item label="默认状态" class="item-lh">
          <a-radio-group size="small" v-model="widget.configuration.defaultDisplayState" button-style="solid">
            <a-radio-button value="edit">可编辑</a-radio-button>
            <a-radio-button value="unedit">不可编辑</a-radio-button>
            <a-radio-button value="hidden">隐藏</a-radio-button>
          </a-radio-group>
        </a-form-model-item>
      </a-collapse-panel>
      <a-collapse-panel key="edit_mode_properties" header="编辑模式属性">
        <!-- <a-form-model-item label="显示清空按钮" :wrapper-col="{ style: { textAlign: 'right' } }">
          <a-switch checked-children="是" un-checked-children="否" v-model="widget.configuration.clearBtnShow" />
        </a-form-model-item> -->
        <a-form-model-item label="加减按钮" class="item-lh" :wrapper-col="{ style: { textAlign: 'right' } }">
          <a-switch checked-children="是" un-checked-children="否" v-model="widget.configuration.stepBtnShow" />
        </a-form-model-item>
        <template v-if="widget.configuration.stepBtnShow && designer.terminalType == 'pc'">
          <numberEditSetting :widget="widget" :designer="designer"></numberEditSetting>
        </template>
        <a-form-model-item label="最小值">
          <a-input-number v-model="widget.configuration.min" :step="1" :max="minDataMax" />
        </a-form-model-item>
        <a-form-model-item label="最大值">
          <a-input-number v-model="widget.configuration.max" :step="1" @blur="maxBlur" />
        </a-form-model-item>
        <a-form-model-item label="金额大写">
          <a-switch v-model="widget.configuration.isCapital" />
        </a-form-model-item>
        <a-form-model-item label="大写显示位置" class="display-b" :label-col="{}" :wrapper-col="{}" v-if="widget.configuration.isCapital">
          <a-radio-group size="small" v-model="widget.configuration.capitalPosition" button-style="solid">
            <a-radio-button value="inside">数字输入框内</a-radio-button>
            <a-radio-button value="outside">数字输入框外</a-radio-button>
            <a-radio-button value="other">其他文本字段</a-radio-button>
          </a-radio-group>
          <a-select
            v-show="widget.configuration.capitalPosition === 'other'"
            v-model="widget.configuration.capitalPositionOther"
            :options="inputFieldOptions"
          />
        </a-form-model-item>
      </a-collapse-panel>
      <a-collapse-panel key="un_edit_mode_properties" header="不可编辑模式属性">
        <a-form-model-item label="不可编辑状态" class="item-lh">
          <a-radio-group size="small" v-model="widget.configuration.uneditableDisplayState" button-style="solid">
            <a-radio-button value="label">纯文本</a-radio-button>
            <a-radio-button value="readonly">只读(显示组件样式)</a-radio-button>
          </a-radio-group>
        </a-form-model-item>
      </a-collapse-panel>
      <a-collapse-panel key="widget-style" header="组件样式">
        <template v-if="designer.terminalType == 'pc'">
          <a-form-model-item label="宽度">
            <a-input-number v-model="width" @change="onInputWidth" />
            <a-select
              v-model="widthUnit"
              @change="onInputWidth"
              style="width: 80px; margin-left: 8px"
              :getPopupContainer="getPopupContainerByPs()"
            >
              <a-select-option value="px">px</a-select-option>
              <a-select-option value="%">%</a-select-option>
            </a-select>
          </a-form-model-item>
        </template>
        <template v-if="designer.terminalType == 'mobile'">
          <a-form-model-item label="显示边框" class="item-lh" :wrapper-col="{ style: { textAlign: 'right' } }">
            <a-switch checked-children="是" un-checked-children="否" v-model="widget.configuration.uniConfiguration.inputBorder" />
          </a-form-model-item>
        </template>
      </a-collapse-panel>
      <a-collapse-panel key="other_properties" header="其他属性">
        <a-form-model-item label="应用于">
          <FieldApplySelect v-model="widget.configuration.applyToDatas" />
        </a-form-model-item>
        <a-form-model-item label="输入框提示语" prop="placeholder">
          <a-input
            placeholder="输入框提示语"
            v-model="widget.configuration.placeholder"
            style="width: 100%"
            v-decorator="['placeholder', { rules: [{ required: true, message: 'Please input your note!', trigger: 'blur' }] }]"
          >
            <template slot="addonAfter">
              <WI18nInput :widget="widget" :designer="designer" code="placeholder" v-model="widget.configuration.placeholder" />
            </template>
          </a-input>
        </a-form-model-item>
        <a-form-model-item label="描述" :wrapper-col="{ style: { marginTop: '2px' } }">
          <a-textarea :rows="4" placeholder="请输入内容" v-model="widget.configuration.note" :maxLength="200" />
          <span class="textLengthShow">{{ widget.configuration.note | textLengthFilter }}/200</span>
        </a-form-model-item>
      </a-collapse-panel>
    </a-collapse>
  </div>
</template>
<style></style>
<script type="text/babel">
import formConfigureMixin from '../../mixin/formConfigure.mixin';

import numberEditSetting from './components/numberEditSetting.vue';

export default {
  name: 'NumberBaseConfiguration',
  mixins: [formConfigureMixin],
  props: {
    widget: Object,
    designer: Object
  },
  data() {
    let width = undefined,
      widthUnit = 'px';
    if (this.widget.configuration.style.width) {
      if (this.widget.configuration.style.width === 'auto') {
        widthUnit = 'auto';
        width = 'auto';
      } else {
        width = parseInt(this.widget.configuration.style.width);
        widthUnit = this.widget.configuration.style.width.indexOf('%') != -1 ? '%' : widthUnit;
      }
    }
    return {
      lengthMax: 9,
      dbDataTypeOptions: [
        { label: '整数', value: '13' },
        { label: '正整数', value: '131' },
        { label: '负整数', value: '132' },
        { label: '长整数', value: '14' },
        // { label: '浮点数', value: '15' },
        // { label: '双精度浮点数', value: '12' },
        { label: 'Number类型', value: '17' }
      ],
      minDataMax: Infinity, //最小值的最大输入判断
      width,
      widthUnit
    };
  },
  filters: {
    textLengthFilter(text) {
      return text ? text.length : 0;
    }
  },
  beforeCreate() {},
  components: { numberEditSetting },
  computed: {
    // lengthMax: {
    //   get() {
    //     return this.onChangeDbDataType(this.widget.configuration.dbDataType);
    //   },
    //   set(val) {}
    // }
  },
  created() {
    if (!this.widget.configuration.style) {
      this.widget.configuration.style = {};
    }
    if (this.widget.configuration.formula == undefined) {
      this.$set(this.widget.configuration, 'formula', {
        enable: false,
        items: []
      });
    }
    if (this.widget.configuration.isCapital === undefined) {
      this.$set(this.widget.configuration, 'isCapital', false);
    }
    if (this.widget.configuration.capitalPosition === undefined) {
      this.$set(this.widget.configuration, 'capitalPosition', 'outside');
    }
  },
  mounted() {
    this.onChangeDbDataType(this.widget.configuration.dbDataType);
  },
  methods: {
    onChangeDbDataType(val) {
      var lengthMax = 10;
      if (val == '131' || val == '13' || val == '132') {
        lengthMax = 10;
        if (!this.widget.configuration.length || this.widget.configuration.length > 9) {
          this.widget.configuration.length = 10;
        }
      } else if (val == '14') {
        lengthMax = 16;
        if (!this.widget.configuration.length || this.widget.configuration.length > 16) {
          this.widget.configuration.length = 16;
        }
      } else if (val == '15') {
        lengthMax = 7;
        if (!this.widget.configuration.length || this.widget.configuration.length > lengthMax) {
          this.widget.configuration.length = lengthMax;
        }
        if (!this.widget.configuration.decimalPlacesNumber) {
          this.widget.configuration.decimalPlacesNumber = 2;
        }
      } else if (val == '12') {
        lengthMax = 16;
        if (!this.widget.configuration.length || this.widget.configuration.length > lengthMax) {
          this.widget.configuration.length = lengthMax;
        }
        if (!this.widget.configuration.decimalPlacesNumber) {
          this.widget.configuration.decimalPlacesNumber = 2;
        }
      } else if (val == '17') {
        lengthMax = 16;
        if (!this.widget.configuration.hasOwnProperty('precision')) {
          this.$set(this.widget.configuration, 'precision', 12);
        }
        if (!this.widget.configuration.hasOwnProperty('scale')) {
          this.$set(this.widget.configuration, 'scale', 0);
        }
      } else {
        lengthMax = null;
      }
      this.lengthMax = lengthMax;
      this.widget.configuration.length = lengthMax;
      return lengthMax;
    },
    // 最大值失焦时，设置最小值可输入的最大值
    maxBlur() {
      if (this.widget.configuration.dbDataType == '132' && this.widget.configuration.max > 0) {
        this.widget.configuration.max = 0;
        this.maxBlur();
        return false;
      }
      let minDataMax = Infinity;
      if (['17', '13', '14', '132'].indexOf(this.widget.configuration.dbDataType) > -1) {
        if (_.isNumber(this.widget.configuration.max)) {
          if (this.widget.configuration.dbDataType == '17') {
            minDataMax = this.widget.configuration.max - 0.1;
          } else {
            minDataMax = this.widget.configuration.max - 1;
          }
        }
      } else if (dbDataTypeOptions == '131') {
        if (_.isNumber(this.widget.configuration.max)) {
          minDataMax = this.widget.configuration.max - 1;
        }
        minDataMax = 0;
      }
      if (
        _.isNumber(this.widget.configuration.min) &&
        _.isNumber(this.widget.configuration.max) &&
        this.widget.configuration.min >= this.widget.configuration.max
      ) {
        this.$message.warning('数字输入框的最小值应小于最大值');
      }
      this.minDataMax = minDataMax;
    },
    onInputWidth() {
      if (this.widthUnit == 'auto') {
        this.widget.configuration.style.width = this.widthUnit;
      } else {
        if (this.width) {
          this.widget.configuration.style.width = this.width + this.widthUnit;
        } else {
          this.widget.configuration.style.width = null;
        }
      }
    }
  }
};
</script>
