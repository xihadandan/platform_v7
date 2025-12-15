<template>
  <div>
    <a-form-model ref="form" :model="button" :rules="rules">
      <a-form-model-item label="按钮名称" prop="title">
        <a-input v-model="button.title">
          <template slot="addonAfter">
            <WI18nInput :widget="widget" :target="button" :designer="designer" :code="button.id" v-model="button.title" />
          </template>
        </a-input>
      </a-form-model-item>
      <a-form-model-item label="编码" prop="code" v-if="editRule && editRule.code !== false">
        <a-input v-model="button.code" />
      </a-form-model-item>
      <a-form-model-item label="按钮类型">
        <a-select
          :options="buttonTypeOptions"
          v-model="button.style.type"
          :style="{ width: '100%' }"
          @change="onChangeButtonType"
        ></a-select>
      </a-form-model-item>
      <a-form-model-item label="隐藏文本">
        <a-switch v-model="button.style.textHidden" />
      </a-form-model-item>
      <DefaultVisibleConfiguration :designer="designer" :configuration="button" v-if="allowVisibleCondition">
        <template slot="extraAutoCompleteSelectGroup">
          <slot name="extraVisibleCompleteSelectGroup" />
        </template>
      </DefaultVisibleConfiguration>

      <div v-if="button.style.type === 'switch' && button.switch != undefined">
        <a-form-model-item label="选中时内容">
          <a-input v-model="button.switch.checkedText" />
        </a-form-model-item>
        <a-form-model-item label="非选中时内容">
          <a-input v-model="button.switch.UnCheckedText" />
        </a-form-model-item>

        <!-- <a-form-model-item label="默认选中">
          <a-switch v-model="button.switch.defaultChecked" />
        </a-form-model-item> -->
        <a-form-model-item label="选中条件">
          <a-input-group compact :style="{ display: 'flex' }">
            <a-auto-complete v-model="button.switch.checkedCondition.code" style="width: 100%" allowClear :filterOption="filterOption">
              <template slot="dataSource">
                <a-select-opt-group>
                  <span slot="label">
                    <a-icon type="code" />
                    页面变量
                  </span>
                  <a-select-option v-for="opt in pageVarOptions" :key="opt.value">{{ opt.label }}</a-select-option>
                </a-select-opt-group>
              </template>
            </a-auto-complete>
            <a-select :options="operatorOptions" v-model="button.switch.checkedCondition.operator" :style="{ width: '100px' }" />
            <a-input
              v-model="button.switch.checkedCondition.value"
              v-show="!['true', 'false'].includes(button.switch.checkedCondition.operator)"
            />
          </a-input-group>
        </a-form-model-item>
      </div>

      <a-form-model-item label="按钮图标" v-show="button.style.type != 'switch'">
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
          <IconSetBadge v-model="button.style.icon"></IconSetBadge>
          <template slot="content">
            <WidgetIconLib v-model="button.style.icon" />
          </template>
        </WidgetDesignModal>
      </a-form-model-item>
      <BehaviorLogConfiguration :widget="widget" :configuration="button" :designer="designer" />
    </a-form-model>
    <WidgetEventHandler
      v-if="button.eventHandler != undefined"
      :eventModel="button.eventHandler"
      :designer="designer"
      :widget="widget"
      :button="button"
      :rule="{ name: false }"
    >
      <template slot="urlHelpSlot">
        <slot name="eventUrlHelpSlot"></slot>
      </template>
      <template slot="eventParamValueHelpSlot">
        <slot name="eventParamValueHelpSlot"></slot>
      </template>
    </WidgetEventHandler>
  </div>
</template>
<script type="text/babel">
import { buttonTypeOptions, buttonShapeOptions, operatorOptions } from '../constant';
import { generateId, deepClone } from '@framework/vue/utils/util';

export default {
  name: 'ButtonDetailsConfiguration',
  mixins: [],
  props: {
    widget: Object,
    designer: Object,
    button: Object,
    allowVisibleCondition: {
      type: Boolean,
      default: true
    },
    editRule: Object
  },
  components: {},
  data() {
    return {
      options: {},
      buttonTypeOptions,
      buttonShapeOptions,
      selectedButtonRowKeys: [],
      selectedButtonRows: [],
      buttonTableColumn: [
        { title: '名称', dataIndex: 'title', width: 120, scopedSlots: { customRender: 'titleSlot' } },
        { title: '编码', dataIndex: 'code', width: 150, scopedSlots: { customRender: 'codeSlot' } },
        { title: '展示位置', dataIndex: 'dislayPosition', scopedSlots: { customRender: 'displayPositionSlot' } },
        { title: '组别', dataIndex: 'group', scopedSlots: { customRender: 'groupSlot' } },
        { title: '按钮样式', dataIndex: 'style', scopedSlots: { customRender: 'styleSlot' } },
        { title: '按钮约束', dataIndex: 'constraint', scopedSlots: { customRender: 'constraintSlot' } },
        { title: '事件', dataIndex: 'customEvent', scopedSlots: { customRender: 'eventHandlerSlot' } }
      ],
      operatorOptions,
      rules: {
        title: [{ required: true, message: '按钮名称不能为空！', trigger: 'blur' }],
        code: [{ required: true, message: '按钮编码不能为空！', trigger: 'blur' }]
      }
    };
  },
  computed: {
    pageVarOptions() {
      // 页面变量路径
      let paths = this.designer.pageVarKeyPaths(),
        options = [];
      for (let i = 0, len = paths.length; i < len; i++) {
        options.push({ label: paths[i], value: paths[i] });
      }
      return options;
    }
  },

  beforeCreate() {},
  created() {
    if (this.button.id == undefined) {
      this.button.id = generateId(9);
    }
  },
  mounted() {},
  methods: {
    onChangeButtonType() {
      if (this.button.style.type == 'switch' && this.button.switch == undefined) {
        this.$set(this.button, 'switch', {
          checkedText: undefined,
          UnCheckedText: undefined,
          defaultChecked: true,
          checkedCondition: { code: undefined, operator: 'true', value: undefined }
        });
      }
    },

    filterOption(input, option) {
      return (
        option.componentOptions.tag != 'a-select-opt-group' &&
        (option.componentOptions.children[0].text.toUpperCase().indexOf(input.toUpperCase()) >= 0 ||
          (option.key && option.key.indexOf(input.toUpperCase()) >= 0))
      );
    },

    validate() {
      return this.$refs.form.validate();
    }
  }
};
</script>
