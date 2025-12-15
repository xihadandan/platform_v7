<template>
  <div>
    <FieldLengthInput :widget="widget" />
    <FieldFormulaInput :configuration="widget.configuration" :widget="widget" :designer="designer" />
    <a-form-model-item label="显示字符数提示" class="item-lh" :wrapper-col="{ style: { textAlign: 'right' } }">
      <a-switch checked-children="是" un-checked-children="否" v-model="widget.configuration.wordCountPrompt" />
    </a-form-model-item>
    <a-form-model-item label="文本类型">
      <a-select v-model="widget.configuration.contentFormat" :options="contentFormatOptions" :style="{ width: '100%' }"
        @change="onChangeContentFormat" :getPopupContainer="getPopupContainerByPs()"></a-select>
    </a-form-model-item>
    <!-- 默认值只有文本时显示 -->
    <FieldDefaultValue :configuration="widget.configuration" :selectVariable="true"
      v-if="widget.configuration.contentFormat == 'text'" />
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
        <!-- 除密码外其余都显示 widget.configuration.contentFormat && -->
        <!-- <a-form-model-item
          class="item-lh"
          label="关联已有数据并填充"
          v-if="['password'].indexOf(widget.configuration.contentFormat) == -1"
          :wrapper-col="{ style: { textAlign: 'right' } }"
        >
          <a-switch checked-children="是" un-checked-children="否" v-model="widget.configuration.autoComplete" default-checked />
        </a-form-model-item> -->
        <a-form-model-item label="显示清空按钮" class="item-lh" :wrapper-col="{ style: { textAlign: 'right' } }">
          <a-switch checked-children="是" un-checked-children="否" v-model="widget.configuration.clearBtnShow" />
        </a-form-model-item>
        <!-- 除普通文本和邮编外其余都显示 widget.configuration.contentFormat && -->
        <template v-if="['text', 'postCode', 'password'].indexOf(widget.configuration.contentFormat) == -1">
          <a-form-model-item label="默认脱敏显示" class="item-lh" :wrapper-col="{ style: { textAlign: 'right' } }">
            <a-switch checked-children="是" un-checked-children="否" v-model="widget.configuration.istextDesensitization" />
          </a-form-model-item>
          <a-form-model-item label="显示脱敏切换按钮" class="item-lh" default-checked
            :wrapper-col="{ style: { textAlign: 'right' } }">
            <a-switch checked-children="是" un-checked-children="否" v-model="widget.configuration.showPasswordEye" />
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
        <a-form-model-item class="item-lh" :wrapper-col="{ style: { textAlign: 'right' } }">
          <template slot="label">
            添加后置插槽
            <!-- <a-checkbox v-model="widget.configuration.addonAfterSlot" @change="onChangeAddonAfterSlot" /> -->
          </template>
          <a-switch checked-children="是" un-checked-children="否" v-model="widget.configuration.addonAfterSlot"
            @change="onChangeAddonAfterSlot" />
          <!-- <a-radio-group
            v-show="widget.configuration.addonAfterSlot"
            size="small"
            v-model="widget.configuration.addonAfterSlotPosition"
            button-style="solid"
          >
            <a-radio-button value="inside">内部</a-radio-button>
            <a-radio-button value="outside">外部</a-radio-button>
          </a-radio-group> -->
        </a-form-model-item>

        <!-- 普通文本、邮件、电话显示 widget.configuration.contentFormat && -->
        <template v-if="['text', 'email', 'telephoneNumber'].indexOf(widget.configuration.contentFormat) > -1">
          <a-form-model-item label="添加额外元素" class="item-lh" :wrapper-col="{ style: { textAlign: 'right' } }">
            <a-switch checked-children="是" un-checked-children="否" v-model="widget.configuration.addonSetting" />
          </a-form-model-item>
          <template v-if="widget.configuration.addonSetting">
            <addonSetting :widget="widget" :designer="designer"></addonSetting>
          </template>
        </template>
        <a-form-model-item label="应用于">
          <FieldApplySelect v-model="widget.configuration.applyToDatas" />
        </a-form-model-item>
        <a-form-model-item label="输入框提示语">
          <a-input placeholder="输入框提示语" v-model="widget.configuration.placeholder" style="width: 100%">
            <template slot="addonAfter">
              <WI18nInput :widget="widget" :designer="designer" code="placeholder"
                v-model="widget.configuration.placeholder" />
            </template>
          </a-input>
        </a-form-model-item>
        <a-form-model-item label="描述" :wrapper-col="{ style: { marginTop: '2px' } }">
          <a-textarea :rows="4" placeholder="请输入内容" v-model="widget.configuration.note" :maxLength="200" />
          <span class="textLengthShow">{{ widget.configuration.note | textLengthFilter }}/200</span>
        </a-form-model-item>
      </a-collapse-panel>
      <template v-if="designer.terminalType == 'mobile'">
        <a-collapse-panel key="widget-style" header="组件样式">
          <!-- <StyleConfiguration :widget="widget" /> -->
          <a-form-model-item label="显示边框" class="item-lh" :wrapper-col="{ style: { textAlign: 'right' } }">
            <a-switch checked-children="是" un-checked-children="否"
              v-model="widget.configuration.uniConfiguration.inputBorder" />
          </a-form-model-item>
        </a-collapse-panel>
      </template>
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

import addonSetting from './components/addonSetting.vue';

export default {
  name: 'inputBaseConfiguration',
  mixins: [formConfigureMixin],
  props: {
    widget: Object,
    designer: Object
  },
  data() {
    return {
      contentFormatOptions: [
        { label: '普通文本', value: 'text' },
        { label: '密码', value: 'password' },
        // { label: 'URL', value: 'url' },
        { label: 'EMAIL', value: 'email' },
        { label: '身份证', value: 'identity' },
        { label: '电话', value: 'telephoneNumber' },
        { label: '手机', value: 'cellphoneNumber' },
        { label: '邮编', value: 'postCode' }
        // { label: '自定义', value: 'custom' }
      ]
    };
  },
  filters: {
    textLengthFilter(text) {
      return text ? text.length : 0;
    }
  },
  beforeCreate() { },
  components: { addonSetting },
  computed: {},
  created() { },
  mounted() { },
  methods: {
    onChangeAddonAfterSlot() {
      if (!this.widget.configuration.hasOwnProperty('addonAfterSlotWidgets')) {
        this.$set(this.widget.configuration, 'addonAfterSlotWidgets', []);
      }
    },

    onChangeContentFormat(val, opt) {
      if (val == 'password') {
        this.widget.configuration.type = 'input-password';
      } else {
        this.widget.configuration.type = 'input';
      }
      // 增加校验规则
      if (['email', 'identity', 'telephoneNumber', 'cellphoneNumber', 'postCode'].indexOf(val) > -1) {
        this.$set(this.widget.configuration.validateRule.regExp, 'type', val);
        this.$emit('onRegExpChange', val);
      } else {
        // 无校验规则的，清空校验规则
        this.$set(this.widget.configuration.validateRule.regExp, 'type', '');
        this.$set(this.widget.configuration.validateRule.regExp, 'value', '');
      }
    }
  }
};
</script>
