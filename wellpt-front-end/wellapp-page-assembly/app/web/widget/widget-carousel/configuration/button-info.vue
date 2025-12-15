<template>
  <div class="button-info-container">
    <a-form-model ref="form" :model="formData" :colon="false" :rules="rules" :label-col="{ flex: '120px' }" :wrapper-col="{ flex: 'auto' }">
      <a-form-model-item label="按钮名称" prop="title">
        <a-input v-model="formData.title" allowClear>
          <template slot="addonAfter">
            <WI18nInput :target="formData" :code="widget.id + '.' + formData.id" v-model="formData.title" />
          </template>
        </a-input>
      </a-form-model-item>
      <a-form-model-item label="按钮类型">
        <a-select :options="buttonTypeOptions" v-model="formData.style.type" :style="{ width: '100%' }" />
      </a-form-model-item>
      <a-form-model-item label="隐藏文本">
        <a-switch v-model="formData.style.textHidden" />
      </a-form-model-item>
      <a-form-model-item label="按钮图标">
        <WidgetIconLibModal v-model="formData.style.icon" :zIndex="1000"></WidgetIconLibModal>
      </a-form-model-item>
      <slot name="fieldExtend"></slot>
    </a-form-model>
    <slot name="buttonEvent">
      <widget-event-handler
        v-if="formData.eventHandler != undefined"
        ref="eventHandler"
        :eventModel="formData.eventHandler"
        :designer="designer"
        :widget="widget"
        :rule="{
          name: false
        }"
        :formLayout="{
          layout: 'horizontal',
          colon: false,
          labelCol: { flex: '120px' },
          wrapperCol: { flex: 'auto' }
        }"
      >
        <template slot="redirectPageTitle">
          <a-input v-model="formData.eventHandler.title">
            <template slot="addonAfter">
              <WI18nInput
                :target="formData.eventHandler"
                :code="formData.id + '.redirectPageTitle'"
                v-model="formData.eventHandler.title"
              />
            </template>
          </a-input>
        </template>
      </widget-event-handler>
    </slot>
  </div>
</template>
<script>
import { buttonTypeOptions } from '../../commons/constant';
import WidgetIconLibModal from '@pageAssembly/app/web/lib/widget-icon-lib-modal.vue';

export default {
  name: 'ButtonInfo',
  inject: ['designer', 'widget'],
  props: {
    formData: {
      type: Object,
      default: () => {}
    },
    getButtonInfoVm: {
      type: Function
    },
    actionTypeOptions: {
      type: Array
    }
  },
  components: {
    WidgetIconLibModal
  },
  data() {
    const typeOptions = buttonTypeOptions.slice(0, 5);
    return {
      rules: {
        title: [{ required: true, message: '按钮名称不能为空！', trigger: 'blur' }],
        code: [{ required: true, message: '按钮编码不能为空！', trigger: 'blur' }]
      },
      buttonTypeOptions: typeOptions
    };
  },
  mounted() {
    const setOperationStyle = el => {
      if (!el) {
        return;
      }
      const operationEl = el.querySelector('.table-header-operation');
      if (operationEl) {
        const parentNode = operationEl.parentNode;
        parentNode.style.cssText += ';margin-left: 120px';
      }
    };
    if (this.$refs.eventHandler) {
      const el = this.$refs.eventHandler.$el;
      setOperationStyle(el);
    } else if (this.$slots.buttonEvent) {
      const el = this.$slots.buttonEvent[0].context.$el;
      // setOperationStyle(el);
    }
    if (typeof this.getButtonInfoVm === 'function') {
      this.getButtonInfoVm(this);
    }
    if (this.actionTypeOptions) {
      this.$refs.eventHandler.actionTypeOptions = this.actionTypeOptions;
    }
  },
  methods: {
    validate(callback) {
      this.$refs.form.validate((valid, error) => {
        callback({ valid, error, data: this.formData });
      });
    }
  }
};
</script>

<style lang="less">
.button-info-container {
  .ant-row {
    display: flex;
    margin-bottom: 5px;
    .ant-form-item-control-wrapper {
      flex: 1 1 auto;
    }
  }
}
</style>
