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
          <FieldNameInput :widget="widget" />
          <FieldCodeInput :widget="widget" />
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
              <!-- <a-form-model-item label="允许拖动调整输入框大小" class="item-lh" :wrapper-col="{ style: { textAlign: 'right' } }">
                <a-switch checked-children="是" un-checked-children="否" v-model="widget.configuration.allowResize" />
              </a-form-model-item> -->
              <a-form-model-item label="文本是否HTML转义" class="item-lh" :wrapper-col="{ style: { textAlign: 'right' } }">
                <a-switch checked-children="是" un-checked-children="否" v-model="widget.configuration.htmlCodec" />
              </a-form-model-item>
              <!-- <a-form-model-item label="输入框提示语">
                <a-input placeholder="输入框提示语" v-model="widget.configuration.placeholder" style="width: 100%" />
              </a-form-model-item> -->
              <a-form-model-item label="描述" :wrapper-col="{ style: { marginTop: '2px' } }">
                <a-textarea :rows="4" placeholder="请输入内容" v-model="widget.configuration.note" :maxLength="200" />
                <span class="textLengthShow">{{ widget.configuration.note | textLengthFilter }}/200</span>
              </a-form-model-item>
            </a-collapse-panel>
          </a-collapse>
        </a-tab-pane>
        <a-tab-pane key="2" tab="校验规则">
          <ValidateRuleConfiguration :widget="widget" :unique="false"></ValidateRuleConfiguration>
        </a-tab-pane>
        <a-tab-pane key="3" tab="事件设置">
          <WidgetEventConfiguration :widget="widget" :designer="designer"></WidgetEventConfiguration>
        </a-tab-pane>
      </a-tabs>
    </a-form-model>
  </div>
</template>
<style></style>
<script type="text/babel">
import formConfigureMixin from '../../mixin/formConfigure.mixin';

export default {
  name: 'WidgetFormRichTextEditorConfiguration',
  mixins: [formConfigureMixin],
  props: {
    widget: Object,
    designer: Object
  },
  data() {
    return {
      rules: {
        name: { required: true, message: <a-icon type="close-circle" theme="filled" />, trigger: ['blur', 'change'], whitespace: true },
        code: { required: true, message: <a-icon type="close-circle" theme="filled" />, trigger: ['blur', 'change'], whitespace: true }
      }
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
  methods: {},
  mounted() {},
  configuration() {
    return {
      code: '',
      dbDataType: '16',
      defaultDisplayState: 'edit',
      uneditableDisplayState: 'label',
      syncLabel2FormItem: true,
      style: {}
    };
  }
};
</script>
