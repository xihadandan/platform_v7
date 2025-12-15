<template>
  <div>
    <div v-show="!widget.configuration.dbDataType">
      <FieldLengthInput :widget="widget" :min="1" :max="1333" />
      <!-- <a-form-model-item label="字段长度">
        <a-input-number v-model="widget.configuration.length"/>
      </a-form-model-item> -->
      <a-form-model-item label="显示字符数提示" class="item-lh" :wrapper-col="{ style: { textAlign: 'right' } }">
        <a-switch checked-children="是" un-checked-children="否" v-model="widget.configuration.wordCountPrompt" />
      </a-form-model-item>
    </div>
    <FieldDefaultValue :configuration="widget.configuration" />
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
        <a-form-model-item label="显示清空按钮" class="item-lh" :wrapper-col="{ style: { textAlign: 'right' } }">
          <a-switch checked-children="是" un-checked-children="否" v-model="widget.configuration.clearBtnShow" />
        </a-form-model-item>
        <template v-if="designer.terminalType == 'mobile'">
          <a-form-model-item label="根据文本内容自动高度" class="item-lh" :wrapper-col="{ style: { textAlign: 'right' } }">
            <a-switch checked-children="是" un-checked-children="否" v-model="widget.configuration.uniConfiguration.autoHeight" />
          </a-form-model-item>
        </template>
        <template v-else>
          <a-form-model-item label="允许拖动调整输入框大小" class="item-lh" :wrapper-col="{ style: { textAlign: 'right' } }">
            <a-switch checked-children="是" un-checked-children="否" v-model="widget.configuration.allowResize" />
          </a-form-model-item>
        </template>
      </a-collapse-panel>
      <a-collapse-panel key="un_edit_mode_properties" header="不可编辑模式属性">
        <a-form-model-item label="不可编辑状态" class="item-lh">
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
        <a-form-model-item label="文本是否HTML转义" class="item-lh" :wrapper-col="{ style: { textAlign: 'right' } }">
          <a-switch checked-children="是" un-checked-children="否" v-model="widget.configuration.htmlCodec" />
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
      <a-collapse-panel key="widget-style" header="组件样式">
        <template v-if="designer.terminalType == 'pc'">
          <a-form-model-item label="宽度">
            <a-input-number v-model="width" @change="onInputWidth" :min="10" :defaultValue="28" />
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
      <a-collapse-panel key="text-style" header="文本样式" v-if="designer.terminalType !== 'mobile'">
        <a-form-model-item label="对齐方式" class="item-lh" :wrapper-col="{ style: { textAlign: 'right' } }">
          <a-radio-group size="small" v-model="widget.configuration.style.textAlign" button-style="solid">
            <a-radio-button value="left"><a-icon type="align-left" /></a-radio-button>
            <a-radio-button value="center"><a-icon type="align-center" /></a-radio-button>
            <a-radio-button value="right"><a-icon type="align-right" /></a-radio-button>
          </a-radio-group>
        </a-form-model-item>
      </a-collapse-panel>
    </a-collapse>
  </div>
</template>
<style></style>
<script type="text/babel">
import formConfigureMixin from '../../mixin/formConfigure.mixin';

export default {
  name: 'inputBaseConfiguration',
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
  components: {},
  computed: {},
  created() {},
  mounted() {},
  methods: {
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
