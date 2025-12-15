<template>
  <div>
    <a-form-model-item label="字段类型">
      <a-select
        v-model="widget.configuration.dbDataType"
        :options="dbDataTypeOptions"
        :style="{ width: '100%' }"
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
      <a-form-model-item label="精度位（有效数）">
        <a-input-number v-model="widget.configuration.precision" :min="0"></a-input-number>
      </a-form-model-item>
      <a-form-model-item label="小数位">
        <a-input-number v-model="widget.configuration.scale" :min="0"></a-input-number>
      </a-form-model-item>
    </template>
    <a-form-model-item label="字段长度">
      <a-input-number v-model="widget.configuration.length" :min="1" :max="lengthMax" />
    </a-form-model-item>
    <a-form-model-item label="千位分隔符" class="item-lh" :wrapper-col="{ style: { textAlign: 'right' } }">
      <a-switch checked-children="是" un-checked-children="否" v-model="widget.configuration.formatNumber" />
    </a-form-model-item>
    <FieldDefaultValue :configuration="widget.configuration" :variables="[]" />
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
        <template v-if="widget.configuration.stepBtnShow">
          <numberEditSetting :widget="widget" :designer="designer"></numberEditSetting>
        </template>
        <a-form-model-item label="最小值">
          <a-input-number v-model="widget.configuration.min" :step="1" />
        </a-form-model-item>
        <a-form-model-item label="最大值">
          <a-input-number v-model="widget.configuration.max" :step="1" />
        </a-form-model-item>
      </a-collapse-panel>
      <a-collapse-panel key="un_edit_mode_properties" header="编辑模式属性">
        <a-form-model-item label="不可编辑状态" style="padding-right: 0" class="item-lh">
          <a-radio-group size="small" v-model="widget.configuration.uneditableDisplayState" button-style="solid">
            <a-radio-button value="label">纯文本</a-radio-button>
            <a-radio-button value="readonly">只读(显示组件样式)</a-radio-button>
          </a-radio-group>
        </a-form-model-item>
      </a-collapse-panel>
      <a-collapse-panel key="other_properties" header="其他属性">
        <a-form-model-item label="应用于">
          <FieldApplySelect v-model="widget.configuration.applyToDatas" />
        </a-form-model-item>
        <a-form-model-item label="输入框提示语">
          <a-input placeholder="输入框提示语" v-model="widget.configuration.placeholder" style="width: 100%">
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
  name: 'inputBaseConfiguration',
  mixins: [formConfigureMixin],
  props: {
    widget: Object,
    designer: Object
  },
  data() {
    return {
      dbDataTypeOptions: [
        { label: '整数', value: '13' },
        { label: '正整数', value: '131' },
        { label: '负整数', value: '132' },
        { label: '长整数', value: '14' },
        { label: '浮点数', value: '15' },
        { label: '双精度浮点数', value: '12' },
        { label: 'Number类型', value: '17' }
      ]
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
    lengthMax: {
      get() {
        return this.onChangeDbDataType(this.widget.configuration.dbDataType);
      },
      set(val) {}
    }
  },
  created() {},
  mounted() {},
  methods: {
    onChangeDbDataType(val) {
      var lengthMax = 9;
      if (val == '13' || val == '131' || val == '132') {
        lengthMax = 9;
        if (this.widget.configuration.length > 9) {
          this.widget.configuration.length = '9';
        }
      } else if (val == '14') {
        lengthMax = 16;
        if (this.widget.configuration.length > 16) {
          this.widget.configuration.length = '16';
        }
      } else if (val == '15') {
        lengthMax = 12;
        if (this.widget.configuration.length > 12) {
          this.widget.configuration.length = '12';
        }
      } else if (val == '12') {
        lengthMax = 18;
        if (this.widget.configuration.length > 18) {
          this.widget.configuration.length = '18';
        }
      } else {
        lengthMax = null;
      }
      this.lengthMax = lengthMax;
      return lengthMax;
    }
  }
};
</script>
