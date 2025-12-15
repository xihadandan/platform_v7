<template>
  <div>
    <a-form-model
      ref="form"
      :model="widget.configuration"
      :rules="rules"
      labelAlign="left"
      :label-col="{ span: 8 }"
      :wrapper-col="{ span: 15, style: { textAlign: 'right' } }"
    >
      <a-tabs default-active-key="1">
        <a-tab-pane key="1" tab="设置">
          <FieldNameInput :widget="widget" />
          <FieldCodeInput :widget="widget" />
          <FieldLengthInput :widget="widget" />
          <a-form-model-item label="默认值" prop="">
            <a-radio-group size="small" v-model="defaultValueRadio" @change="onDefaultValueChange">
              <a-radio-button :value="false">关</a-radio-button>
              <a-radio-button :value="true">开</a-radio-button>
            </a-radio-group>
            <!-- <a-radio-group size="small" v-model="widget.configuration.defaultValue" @change="onDefaultValueChange">
              <a-radio-button :value="widget.configuration.uncheckedValue">关</a-radio-button>
              <a-radio-button :value="widget.configuration.checkedValue">开</a-radio-button>
            </a-radio-group> -->
          </a-form-model-item>

          <a-form-model-item label="开关风格">
            <template v-if="designer.terminalType == 'pc'">
              <a-radio-group size="small" v-model="widget.configuration.switchStyle">
                <a-radio-button v-for="item in styleOptions" :key="item.value" :value="item.value">{{ item.label }}</a-radio-button>
              </a-radio-group>
            </template>
            <template v-else>
              <a-radio-group size="small" v-model="widget.configuration.uniConfiguration.switchStyle">
                <a-radio-button v-for="item in styleOptions" :key="item.value" :value="item.value" v-if="item.value != 'icon'">
                  {{ item.label }}
                </a-radio-button>
              </a-radio-group>
            </template>
          </a-form-model-item>
          <a-form-model-item
            label="开启文字"
            v-show="
              (designer.terminalType == 'pc' && widget.configuration.switchStyle == 'label') ||
              (designer.terminalType == 'mobile' && widget.configuration.uniConfiguration.switchStyle == 'label')
            "
          >
            <a-input v-model="widget.configuration.checkedLabel">
              <template slot="addonAfter">
                <WI18nInput :widget="widget" :designer="designer" code="checkedLabel" v-model="widget.configuration.checkedLabel" />
              </template>
            </a-input>
          </a-form-model-item>
          <a-form-model-item
            label="关闭文字"
            v-show="
              (designer.terminalType == 'pc' && widget.configuration.switchStyle == 'label') ||
              (designer.terminalType == 'mobile' && widget.configuration.uniConfiguration.switchStyle == 'label')
            "
          >
            <a-input v-model="widget.configuration.uncheckedLabel">
              <template slot="addonAfter">
                <WI18nInput :widget="widget" :designer="designer" code="uncheckedLabel" v-model="widget.configuration.uncheckedLabel" />
              </template>
            </a-input>
          </a-form-model-item>
          <a-form-model-item label="开启图标" v-show="widget.configuration.switchStyle == 'icon' && designer.terminalType !== 'mobile'">
            <WidgetDesignDrawer
              :id="'widgetFormSwitchIconSet1' + widget.id"
              title="选中图标"
              :width="640"
              :bodyStyle="{ height: '100%' }"
              :designer="designer"
            >
              <IconSetBadge v-model="widget.configuration.checkedIcon" onlyIconClass />
              <template slot="content">
                <WidgetIconLib v-model="widget.configuration.checkedIcon" />
              </template>
            </WidgetDesignDrawer>
          </a-form-model-item>
          <a-form-model-item label="关闭图标" v-show="widget.configuration.switchStyle == 'icon' && designer.terminalType !== 'mobile'">
            <WidgetDesignDrawer
              :id="'widgetFormSwitchIconSet2' + widget.id"
              title="选中图标"
              :width="640"
              :bodyStyle="{ height: '100%' }"
              :designer="designer"
            >
              <IconSetBadge v-model="widget.configuration.uncheckedIcon" onlyIconClass />
              <template slot="content">
                <WidgetIconLib v-model="widget.configuration.uncheckedIcon" />
              </template>
            </WidgetDesignDrawer>
          </a-form-model-item>
          <a-form-model-item label="开启真实值">
            <a-input v-model="widget.configuration.checkedValue" @change="changeCheckedValue" />
          </a-form-model-item>
          <a-form-model-item label="关闭真实值">
            <a-input v-model="widget.configuration.uncheckedValue" @change="changeUnCheckedValue" />
          </a-form-model-item>
          <a-form-model-item label="默认状态">
            <a-radio-group size="small" v-model="widget.configuration.defaultDisplayState">
              <a-radio-button value="edit">可编辑</a-radio-button>
              <a-radio-button value="unedit">不可编辑</a-radio-button>
              <a-radio-button value="hidden">隐藏</a-radio-button>
            </a-radio-group>
          </a-form-model-item>
          <div>
            <a-collapse :bordered="false" expandIconPosition="right">
              <a-collapse-panel key="uneditMode" header="不可编辑模式属性">
                <a-form-model-item label="不可编辑状态">
                  <a-radio-group size="small" v-model="widget.configuration.uneditableDisplayState">
                    <a-radio-button value="label">纯文本</a-radio-button>
                    <a-radio-button value="readonly">只读(显示组件样式)</a-radio-button>
                  </a-radio-group>
                </a-form-model-item>
              </a-collapse-panel>
              <a-collapse-panel key="otherProp" header="其它属性">
                <a-form-model-item label="应用于">
                  <!-- <a-tooltip slot="label" title="字段映射的说明" placement="topRight" :arrowPointAtCenter="true">
                    字段映射
                    <a-icon type="info-circle" />
                  </a-tooltip> -->
                  <FieldApplySelect v-model="widget.configuration.applyToDatas" />
                </a-form-model-item>
                <a-form-model-item label="描述">
                  <a-textarea v-model="widget.configuration.description" />
                </a-form-model-item>
              </a-collapse-panel>
            </a-collapse>
          </div>
        </a-tab-pane>
        <a-tab-pane key="2" tab="校验规则">
          <ValidateRuleConfiguration :widget="widget"></ValidateRuleConfiguration>
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
<style></style>
<script type="text/babel">
import formConfigureMixin from '../../mixin/formConfigure.mixin';
import FormEventParamHelp from '../../commons/form-event-param-help.vue';

export default {
  name: 'WidgetFormSwitchConfiguration',
  mixins: [formConfigureMixin],
  props: {
    widget: Object,
    designer: Object
  },
  data() {
    return {
      styleOptions: [
        { label: '默认', value: '' },
        { label: '显示文字', value: 'label' },
        { label: '显示图标', value: 'icon' }
      ],
      rules: {
        name: { required: true, message: <a-icon type="close-circle" theme="filled" />, trigger: ['blur', 'change'], whitespace: true },
        code: { required: true, message: <a-icon type="close-circle" theme="filled" />, trigger: ['blur', 'change'], whitespace: true }
      },
      defaultValueRadio: this.widget.configuration.defaultValue == this.widget.configuration.checkedValue
    };
  },

  beforeCreate() {},
  components: { FormEventParamHelp },
  computed: {},
  created() {
    if (!this.widget.configuration.hasOwnProperty('uniConfiguration')) {
      this.$set(this.widget.configuration, 'uniConfiguration', {
        switchStyle: this.widget.configuration.switchStyle == 'icon' ? '' : this.widget.configuration.switchStyle
      });
    }
  },
  methods: {
    onDefaultValueChange() {
      this.widget.configuration.defaultValue = this.defaultValueRadio
        ? this.widget.configuration.checkedValue
        : this.widget.configuration.uncheckedValue;
    },
    changeCheckedValue() {
      if (this.defaultValueRadio) {
        this.widget.configuration.defaultValue = this.widget.configuration.checkedValue;
      }
    },
    changeUnCheckedValue() {
      if (!this.defaultValueRadio) {
        this.widget.configuration.defaultValue = this.widget.configuration.uncheckedValue;
      }
    }
  },
  mounted() {},
  updated() {}
};
</script>
