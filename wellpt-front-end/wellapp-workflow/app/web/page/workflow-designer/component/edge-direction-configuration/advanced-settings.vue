<template>
  <!-- 流向属性-高级设置 -->
  <div>
    <a-form-model-item class="form-item-vertical" label="流向作为提交按钮使用">
      <w-switch v-model="formData.useAsButton" />
    </a-form-model-item>
    <template v-if="formData.useAsButton == '1'">
      <div class="useAsButton-div form-model-vertical">
        <AdminSettingButtonConfiguration labelAlign="left" :button="button" :hasHandler="false" hideParams="code,shape,txtHidden">
          <template slot="textTips">
            <span style="color: var(--w-gray-color-7)">（为空时使用流向名称）</span>
          </template>
          <template #textAfter>
            <w-i18n-input :target="formData" :code="formData.id + '.useAsButtonName'" v-model="formData.buttonName" />
          </template>
        </AdminSettingButtonConfiguration>
        <a-form-model-item label="按钮排序号" prop="buttonOrder" required>
          <a-input v-model="formData.buttonOrder"></a-input>
        </a-form-model-item>
      </div>
    </template>
    <a-form-model-item class="form-item-vertical">
      <template slot="label">事件监听</template>
      <direction-listener-tree-select v-model="formData.listener" :treeCheckable="true" />
    </a-form-model-item>
    <!-- 事件脚本 -->
    <a-form-model-item class="form-item-vertical" label="事件脚本">
      <event-scripts v-model="eventScripts" showPointcut="direction" @change="eventScriptsChange"></event-scripts>
    </a-form-model-item>
  </div>
</template>

<script>
import WSwitch from '../components/w-switch';
import DirectionListenerTreeSelect from '../commons/direction-listener-tree-select';
import EventScripts from '../commons/event-scripts.vue';
import AdminSettingButtonConfiguration from '@admin/app/web/template/common/button-configuration-admin';
import WI18nInput from '@framework/vue/designer/w-i18n-input.vue';

export default {
  name: 'EdgeDirectionAdvancedSettings',
  inject: ['designer', 'workFlowData'],
  provide() {
    return {
      appId: () => (this.workFlowData.moduleId ? [this.workFlowData.moduleId] : [])
    };
  },
  props: {
    formData: {
      type: Object,
      default: () => {}
    }
  },
  components: {
    WSwitch,
    DirectionListenerTreeSelect,
    EventScripts,
    AdminSettingButtonConfiguration,
    WI18nInput
  },
  data() {
    let eventScripts = [];
    if (this.formData.eventScript) {
      eventScripts.push(this.formData.eventScript);
    }
    let button = {
      text: this.formData.buttonName || '',
      code: '',
      style: this.formData.buttonClassName
    };
    return {
      button,
      eventScripts
    };
  },
  methods: {
    eventScriptsChange(value) {
      if (value.length) {
        this.formData.eventScript = value[0];
      } else {
        this.formData.eventScript = null;
      }
    }
  },
  watch: {
    button: {
      deep: true,
      handler(v) {
        this.formData.buttonName = v.text;
        this.formData.buttonClassName = JSON.stringify(v.style);
      }
    }
  }
};
</script>
<style lang="less" scoped>
.useAsButton-div {
  border: 1px dashed var(--w-border-color-light);
  border-radius: var(--w-border-radius-base);
  margin: 0 12px;
  padding-top: 8px;
}
</style>
