<template>
  <!-- 标签组配置 -->
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
          <a-form-model-item label="标签编辑方式">
            <a-radio-group size="small" v-model="widget.configuration.tagEditMode">
              <a-radio-button value="select">选择标签</a-radio-button>
              <a-radio-button value="input">输入标签</a-radio-button>
            </a-radio-group>
          </a-form-model-item>
          <template v-if="widget.configuration.tagEditMode === 'select'">
            <option-source-configuration :widget="widget" :designer="designer" :options="widget.configuration.options" />
          </template>
          <a-form-model-item label="边框">
            <a-switch v-model="widget.configuration.hasBorder" />
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
              <a-collapse-panel key="editMode" header="编辑模式属性">
                <a-form-model-item class="display-b" :label-col="{}" :wrapper-col="{}">
                  <template #label>
                    <FormItemTooltip
                      label="显示值字段"
                      text="当选项选择后，显示值将一并更新至该字段。可配置隐藏字段，用于数据提交、存储。"
                    ></FormItemTooltip>
                  </template>
                  <a-select
                    allowClear
                    placeholder="请选择"
                    :options="inputFieldOptions"
                    :style="{ width: '100%' }"
                    v-model="widget.configuration.displayValueField"
                    :getPopupContainer="getPopupContainerByPs()"
                    :dropdownClassName="getDropdownClassName()"
                  ></a-select>
                </a-form-model-item>
                <template v-if="widget.configuration.tagEditMode === 'select'">
                  <a-form-model-item label="允许全选">
                    <a-switch v-model="widget.configuration.selectCheckAll" />
                  </a-form-model-item>
                  <selected-count-configuration :widget="widget" />
                </template>
              </a-collapse-panel>
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
          <WidgetEventConfiguration :widget="widget" :designer="designer"></WidgetEventConfiguration>
        </a-tab-pane>
      </a-tabs>
    </a-form-model>
  </div>
</template>

<script>
import formConfigureMixin from '../../mixin/formConfigure.mixin';
import { optionSourceTypes } from '../../commons/constant';
export default {
  name: 'WidgetFormTagConfiguration',
  mixins: [formConfigureMixin],
  props: {
    widget: Object,
    designer: Object
  },
  data() {
    return {
      optionSourceTypes, // 备选项来源类型
      rules: {
        name: { required: true, message: <a-icon type="close-circle" theme="filled" />, trigger: ['blur', 'change'], whitespace: true },
        code: { required: true, message: <a-icon type="close-circle" theme="filled" />, trigger: ['blur', 'change'], whitespace: true }
      }
    };
  },
  methods: {
    // 改变备选项来源 常量、数据字典、数据仓库
    changeOptionSource(val) {
      this.widget.configuration.optionDataAutoSet = false;
    }
  }
};
</script>
