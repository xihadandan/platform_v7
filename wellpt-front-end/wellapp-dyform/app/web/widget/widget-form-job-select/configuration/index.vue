<template>
  <div>
    <a-tabs default-active-key="1">
      <a-tab-pane key="1" tab="设置">
        <a-form-model
          ref="form"
          :model="widget.configuration"
          :rules="rules"
          labelAlign="left"
          :wrapper-col="{ style: { textAlign: 'right' } }"
        >
          <FieldNameInput :widget="widget" />
          <FieldCodeInput :widget="widget" />
          <FieldLengthInput :widget="widget" />

          <a-form-model-item label="职位显示格式" class="item-lh">
            <a-radio-group size="small" v-model="widget.configuration.jobDisplayPattern" button-style="solid">
              <a-radio-button value="full">长名称</a-radio-button>
              <a-radio-button value="simple">短名称</a-radio-button>
            </a-radio-group>
          </a-form-model-item>
          <template v-if="designer.terminalType == 'mobile'">
            <a-form-model-item label="弹出类型">
              <a-radio-group size="small" v-model="widget.configuration.uniConfiguration.dropType">
                <a-radio-button value="dropdown">下拉框</a-radio-button>
                <a-radio-button value="picker">底部弹出</a-radio-button>
              </a-radio-group>
            </a-form-model-item>
          </template>
          <a-collapse :bordered="false" expandIconPosition="right" defaultActiveKey="component_mode_properties">
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
              <a-form-model-item>
                <template #label>
                  <FormItemTooltip
                    label="显示值字段"
                    text="当选项选择后，显示值将一并更新至该字段。可配置隐藏字段，用于数据提交、存储。"
                  ></FormItemTooltip>
                </template>
                <a-select
                  allowClear
                  :options="inputFieldOptions"
                  v-model="widget.configuration.displayValueField"
                  :getPopupContainer="getPopupContainerByPs()"
                ></a-select>
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
            <a-collapse-panel key="other_properties" header="其他属性">
              <a-form-model-item label="应用于">
                <FieldApplySelect v-model="widget.configuration.applyToDatas" />
              </a-form-model-item>
            </a-collapse-panel>
            <template v-if="designer.terminalType == 'mobile'">
              <a-collapse-panel key="widget-style" header="组件样式">
                <a-form-model-item label="显示边框" class="item-lh" :wrapper-col="{ style: { textAlign: 'right' } }">
                  <a-switch checked-children="是" un-checked-children="否" v-model="widget.configuration.uniConfiguration.bordered" />
                </a-form-model-item>
              </a-collapse-panel>
            </template>
          </a-collapse>
        </a-form-model>
      </a-tab-pane>
      <!-- <a-tab-pane key="2" tab="校验规则" :forceRender="true">
        <ValidateRuleConfiguration
          ref="validateRef"
          :widget="widget"
          :trigger="false"
          :required="true"
          :unique="false"
          :regExp="false"
          :validatorFunction="false"
        ></ValidateRuleConfiguration>
      </a-tab-pane> -->
      <a-tab-pane key="2" tab="事件设置">
        <WidgetEventConfiguration :widget="widget" :designer="designer"></WidgetEventConfiguration>
      </a-tab-pane>
    </a-tabs>
  </div>
</template>
<style></style>
<script type="text/babel">
import formConfigureMixin from '../../mixin/formConfigure.mixin';

export default {
  name: 'WidgetFormJobSelectConfiguration',
  mixins: [formConfigureMixin],
  props: {
    widget: Object,
    designer: Object
  },
  data() {
    return {
      jobDefaultValueOptions: [],
      rules: {
        name: {
          required: true,
          message: <a-icon type="close-circle" theme="filled" title="字段名称必填" />,
          trigger: ['blur', 'change'],
          whitespace: true
        },
        code: {
          required: true,
          message: <a-icon type="close-circle" theme="filled" title="字段编码必填" />,
          trigger: ['blur', 'change'],
          whitespace: true
        }
      }
    };
  },

  components: {},
  computed: {},
  created() {
    if (!this.widget.configuration.hasOwnProperty('uniConfiguration')) {
      this.$set(this.widget.configuration, 'uniConfiguration', { dropType: 'picker', bordered: false });
    }
  },
  methods: {},
  beforeMount() {},
  mounted() {}
};
</script>
