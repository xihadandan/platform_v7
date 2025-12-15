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
          <a-collapse :bordered="false" expandIconPosition="right" defaultActiveKey="title_properties">
            <a-collapse-panel key="title_properties" header="文本内容属性">
              <a-form-model-item label="标题文本内容" class="display-b">
                <div style="display: flex; justify-content: space-between">
                  <VariableDefineTemplate
                    style="width: 100%"
                    v-model="widget.configuration.title"
                    ref="variableTpt"
                    :variableTreeData="designer.variableTreeData"
                    :enableSysVar="false"
                  />
                  <WI18nInput
                    style="padding: 0px 15px"
                    :widget="widget"
                    :designer="designer"
                    code="title"
                    v-model="widget.configuration.title.value"
                  />
                </div>

                <!-- <a-input placeholder="输入标题文本内容" v-model="widget.configuration.title" style="width: 100%" /> -->
                <div class="example-txt">样例：${流程名称}_${发起人姓名}(${发起年}${发起月}${发起日})</div>
              </a-form-model-item>
              <title-style-configuration :widget="widget"></title-style-configuration>
            </a-collapse-panel>
            <a-collapse-panel key="divider_properties" header="副标题属性">
              <a-form-model-item label="显示副标题" class="item-lh" :wrapper-col="{ style: { textAlign: 'right' } }">
                <a-switch checked-children="是" un-checked-children="否" v-model="widget.configuration.subtitleShow" />
              </a-form-model-item>
              <template v-if="widget.configuration.subtitleShow">
                <a-form-model-item label="标题文本内容" class="display-b">
                  <div style="display: flex; justify-content: space-between">
                    <VariableDefineTemplate
                      style="width: 100%"
                      v-model="widget.configuration.subtitle"
                      ref="variableTpt"
                      :variableTreeData="designer.variableTreeData"
                      :enableSysVar="false"
                    />
                    <WI18nInput
                      style="padding: 0px 15px"
                      :widget="widget"
                      :designer="designer"
                      code="subtitle"
                      v-model="widget.configuration.subtitle.value"
                    />
                  </div>

                  <!-- <a-input placeholder="输入标题文本内容" v-model="widget.configuration.title" style="width: 100%" /> -->
                  <div class="example-txt">样例：${流程名称}_${发起人姓名}(${发起年}${发起月}${发起日})</div>
                </a-form-model-item>
                <title-style-configuration :widget="widget" type="subtitleStyle"></title-style-configuration>
                <a-form-model-item label="上外边距">
                  <a-input-number v-model="widget.configuration.subtitleStyle.marginTop" :min="0" :defaultValue="28" />
                  <span style="padding-left: 10px">px</span>
                </a-form-model-item>
                <a-form-model-item label="左右外边距">
                  <a-input-number v-model="widget.configuration.subtitleStyle.marginLR" :min="10" :defaultValue="28" />
                  <span style="padding-left: 10px">px</span>
                </a-form-model-item>
              </template>
            </a-collapse-panel>
            <a-collapse-panel key="divider_properties" header="分隔线属性">
              <a-form-model-item label="显示分隔线" class="item-lh" :wrapper-col="{ style: { textAlign: 'right' } }">
                <a-switch checked-children="是" un-checked-children="否" v-model="widget.configuration.dividerShow" />
              </a-form-model-item>
              <template v-if="widget.configuration.dividerShow">
                <a-form-model-item label="分隔线样式">
                  <a-select
                    v-model="widget.configuration.dividerStyle.style"
                    :style="{ width: '100%' }"
                    :getPopupContainer="getPopupContainerByPs()"
                  >
                    <a-select-option v-for="item in deviderTypeOptions" :key="item.code" :value="item.code">
                      {{ item.name }}
                    </a-select-option>
                  </a-select>
                </a-form-model-item>
                <template v-if="widget.configuration.dividerStyle.style == 'single'">
                  <divider-type-configuration :widget="widget"></divider-type-configuration>
                  <divider-Width-configuration :widget="widget"></divider-Width-configuration>
                  <ColorSelectConfiguration
                    label="分隔线颜色"
                    v-model="widget.configuration.dividerStyle"
                    onlyValue
                    colorField="color"
                    radioSize="small"
                  ></ColorSelectConfiguration>
                </template>
                <template v-else-if="widget.configuration.dividerStyle.style == 'double'">
                  <divider-type-configuration :widget="widget" label="上分隔线类型"></divider-type-configuration>
                  <divider-Width-configuration :widget="widget" label="上分隔线宽度"></divider-Width-configuration>
                  <ColorSelectConfiguration
                    label="分隔线颜色"
                    v-model="widget.configuration.dividerStyle"
                    onlyValue
                    colorField="color"
                    radioSize="small"
                  ></ColorSelectConfiguration>
                  <divider-type-configuration :widget="widget" label="下分隔线类型" code="type_buttom"></divider-type-configuration>
                  <divider-Width-configuration :widget="widget" label="下分隔线宽度" code="width_buttom"></divider-Width-configuration>
                  <ColorSelectConfiguration
                    label="下分隔线颜色"
                    v-model="widget.configuration.dividerStyle"
                    onlyValue
                    colorField="color_buttom"
                    radioSize="small"
                  ></ColorSelectConfiguration>
                </template>
                <template v-else-if="widget.configuration.dividerStyle.style == 'single-icon'">
                  <divider-type-configuration :widget="widget"></divider-type-configuration>
                  <divider-Width-configuration :widget="widget"></divider-Width-configuration>
                  <ColorSelectConfiguration
                    label="下分隔线颜色"
                    v-model="widget.configuration.dividerStyle"
                    onlyValue
                    colorField="color"
                    radioSize="small"
                  ></ColorSelectConfiguration>
                  <a-form-model-item label="分割线图标">
                    <WidgetDesignModal
                      title="选择图标"
                      :zIndex="1000"
                      :width="640"
                      dialogClass="pt-modal widget-icon-lib-modal"
                      :bodyStyle="{ height: '560px' }"
                      :maxHeight="560"
                      mask
                      bodyContainer
                    >
                      <IconSetBadge v-model="widget.configuration.dividerStyle.icon" onlyIconClass></IconSetBadge>
                      <template slot="content">
                        <WidgetIconLib v-model="widget.configuration.dividerStyle.icon" onlyIconClass />
                      </template>
                    </WidgetDesignModal>
                  </a-form-model-item>

                  <ColorSelectConfiguration
                    label="分隔图标颜色"
                    v-model="widget.configuration.dividerStyle"
                    onlyValue
                    colorField="color_icon"
                    radioSize="small"
                  ></ColorSelectConfiguration>
                </template>
              </template>
            </a-collapse-panel>
            <a-collapse-panel key="other_properties" header="组件其他属性">
              <a-form-model-item class="display-b">
                <template #label>
                  <span>文本超链接地址</span>
                  <a-tooltip placement="topRight" :arrowPointAtCenter="true">
                    <template #title>
                      <div>
                        引用格式：${A}，其中A为表单字段编码。
                        <br />
                        示例：https://www.baidu.com?pa=${pa}
                      </div>
                    </template>
                    <a-icon type="exclamation-circle" />
                  </a-tooltip>
                </template>
                <a-input placeholder="请输入超链接地址" v-model="widget.configuration.url" style="width: 100%" />
              </a-form-model-item>
            </a-collapse-panel>
            <a-collaspe-panel key="styleSetting" header="样式设置">
              <a-form-model-item label="高度">
                <a-input-number v-model="widget.configuration.style.height" :min="10" :defaultValue="28" />
                <span style="padding-left: 10px">px</span>
              </a-form-model-item>
              <a-form-model-item label="左右外边距">
                <a-input-number v-model="widget.configuration.style.marginLR" :min="10" :defaultValue="28" />
                <span style="padding-left: 10px">px</span>
              </a-form-model-item>
              <a-form-model-item label="上外边距">
                <a-input-number v-model="widget.configuration.style.marginTop" :min="0" :defaultValue="28" />
                <span style="padding-left: 10px">px</span>
              </a-form-model-item>
              <a-form-model-item label="下外边距">
                <a-input-number v-model="widget.configuration.style.marginBottom" :min="0" :defaultValue="28" />
                <span style="padding-left: 10px">px</span>
              </a-form-model-item>
              <a-form-model-item label="上内边距">
                <a-input-number v-model="widget.configuration.style.paddingTop" :min="10" :defaultValue="28" />
                <span style="padding-left: 10px">px</span>
              </a-form-model-item>
              <a-form-model-item label="下内边距">
                <a-input-number v-model="widget.configuration.style.paddingBottom" :min="10" :defaultValue="28" />
                <span style="padding-left: 10px">px</span>
              </a-form-model-item>
            </a-collaspe-panel>
          </a-collapse>
        </a-tab-pane>
        <a-tab-pane key="2" tab="校验规则">
          <ValidateRuleConfiguration :widget="widget" :unique="true"></ValidateRuleConfiguration>
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

import DividerTypeConfiguration from './components/divider-type-configuration.vue';
import DividerWidthConfiguration from './components/divider-width-configuration.vue';
// import DividerColorConfiguration from './components/divider-color-configuration.vue';
import TitleStyleConfiguration from './components/title-style-configuration.vue';

export default {
  name: 'WidgetFormTitleConfiguration',
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
      },
      deviderTypeOptions: [
        {
          code: 'single',
          name: '单分隔线'
        },
        {
          code: 'double',
          name: '双分隔线'
        },
        {
          code: 'single-icon',
          name: '单分隔线（含分隔图标）'
        }
      ]
    };
  },

  beforeCreate() {},
  components: {
    DividerTypeConfiguration,
    DividerWidthConfiguration,
    // DividerColorConfiguration,
    TitleStyleConfiguration
  },
  computed: {},
  created() {
    if (!this.widget.configuration.hasOwnProperty('subtitle')) {
      this.$set(this.widget.configuration, 'subtitleShow', false);
      this.$set(this.widget.configuration, 'subtitle', {
        value: '副标题',
        variables: [{ edit: false, label: '副标题', value: '副标题' }]
      });
      this.$set(this.widget.configuration, 'subtitleStyle', {
        align: 'center',
        fontSize: 14
      });
    }
  },
  methods: {},
  mounted() {
    // this.widget.configuration.title = null;
  },
  updated() {}
};
</script>
