<template>
  <div class="designer-configuration-form">
    <a-form-model
      ref="form"
      :model="widget.configuration"
      :rules="rules"
      labelAlign="left"
      :wrapper-col="{ style: { textAlign: 'right' } }"
    >
      <a-tabs default-active-key="1">
        <a-tab-pane key="1" tab="设置">
          <a-form-model-item label="类型" prop="type" style="display: none">
            <a-select
              v-model="widget.configuration.type"
              :options="typeOptions"
              :style="{ width: '100%' }"
              @change="onChangeType"
              :getPopupContainer="getPopupContainerByPs()"
            ></a-select>
          </a-form-model-item>
          <FieldNameInput :widget="widget" />
          <FieldCodeInput :widget="widget" />

          <number-base-configuration :widget="widget" :designer="designer" v-bind="$attrs"></number-base-configuration>
        </a-tab-pane>
        <a-tab-pane key="2" tab="校验规则" :forceRender="true">
          <ValidateRuleConfiguration
            ref="validateRef"
            :widget="widget"
            :trigger="true"
            :required="true"
            :unique="true"
            :validatorFunction="true"
          />
        </a-tab-pane>
        <a-tab-pane key="3" tab="事件设置">
          <WidgetEventConfiguration :widget="widget" :designer="designer">
            <template slot="eventParamValueHelpSlot"><FormEventParamHelp /></template>
          </WidgetEventConfiguration>
        </a-tab-pane>
      </a-tabs>
    </a-form-model>
  </div>
</template>

<style lang="less"></style>
<script type="text/babel">
import formConfigureMixin from '../../mixin/formConfigure.mixin';
import NumberBaseConfiguration from './number-base-configuration.vue';
import FormEventParamHelp from '../../commons/form-event-param-help.vue';
export default {
  name: 'WidgetFormInputNumberConfiguration',
  mixins: [formConfigureMixin],
  props: {
    widget: Object,
    designer: Object
  },
  data() {
    return {
      typeOptions: [
        { label: '输入框', value: 'input' },
        { label: '密码框', value: 'input-password' },
        { label: '数字输入框', value: 'input-number' },
        { label: '文本域', value: 'textarea' }
      ],
      dbDataTypeOptions: [
        { label: '整数', value: '13' },
        { label: '正整数', value: '131' },
        { label: '负整数', value: '132' },
        { label: '长整数', value: '14' },
        { label: '浮点数', value: '15' },
        { label: '双精度浮点数', value: '12' },
        { label: 'Number类型', value: '17' }
      ],

      rules: {
        name: { required: true, message: <a-icon type="close-circle" theme="filled" />, trigger: ['blur', 'change'], whitespace: true },
        code: { required: true, message: <a-icon type="close-circle" theme="filled" />, trigger: ['blur', 'change'], whitespace: true }
      }
    };
  },

  beforeCreate() {},
  components: { NumberBaseConfiguration, FormEventParamHelp },
  computed: {},
  created() {
    if (this.widget.column) {
      // Number类型
      let { length, scale } = this.widget.column;
      this.widget.configuration.dbDataType = '17';
      this.widget.configuration.precision = length;
      this.widget.configuration.scale = scale;
    }
    if (!this.widget.configuration.hasOwnProperty('uniConfiguration')) {
      this.$set(this.widget.configuration, 'uniConfiguration', { inputBorder: false });
    }
  },
  methods: {
    onChangeType(val, opt) {
      if (!this.widget.configuration.name) {
        this.widget.title = opt.componentOptions.children[0].text;
      }
      // 初始变更为数字输入框，默认为整型
      if (val == 'input-number' && this.widget.configuration.dbDataType == null) {
        this.widget.configuration.dbDataType = '13';
      }
      if (val == 'input' || val == 'input-password') {
        // 默认后端null情况是字符串类型
        this.widget.configuration.dbDataType = null;
      }
    },
    // 关联校验规则相关切换
    onRegExpChange(val) {
      this.$refs.validateRef.onRegExpChange(val);
    }
    // onTitleChange($evt) {
    //   if (
    //     this.designer.parentOfSelectedWidget &&
    //     this.designer.parentOfSelectedWidget.wtype == 'WidgetFormItem' &&
    //     !this.designer.parentOfSelectedWidget.configuration.label
    //   ) {
    //     this.designer.parentOfSelectedWidget.configuration.label = $evt.target.value;
    //   }
    // },
  },
  mounted() {}
};
</script>
