<template>
  <div>
    <a-form-model ref="form" :model="formData" :rules="rules" :colon="false">
      <a-form-model-item label="标题" prop="title">
        <a-input v-model="formData.title">
          <template slot="addonAfter">
            <w-i18n-input
              :widget="widget"
              :designer="designer"
              :target="formData"
              :code="formData.id + '.title'"
              v-model="formData.title"
            />
          </template>
        </a-input>
      </a-form-model-item>
      <a-form-model-item label="子标题">
        <a-input v-model="formData.subTitle">
          <template slot="addonAfter">
            <w-i18n-input
              :widget="widget"
              :designer="designer"
              :target="formData"
              :code="formData.id + '.subTitle'"
              v-model="formData.subTitle"
            />
          </template>
        </a-input>
      </a-form-model-item>
      <a-form-model-item label="图标">
        <WidgetIconLibModal v-model="formData.style.icon" :zIndex="1000" onlyIconClass></WidgetIconLibModal>
      </a-form-model-item>
      <a-form-model-item label="状态">
        <a-select v-model="formData.status" :options="statusOptions" />
      </a-form-model-item>
      <a-form-model-item label="描述">
        <div style="display: flex">
          <a-textarea
            v-model="formData.description"
            :rows="4"
            :style="{
              height: 'auto',
              'margin-top': '10px',
              'margin-right': '10px'
            }"
          />
          <w-i18n-input
            :widget="widget"
            :designer="designer"
            :target="formData"
            :code="formData.id + '.description'"
            v-model="formData.description"
          />
        </div>
      </a-form-model-item>
      <DefaultVisibleConfiguration
        v-if="designer.terminalType == 'pc'"
        compact
        :designer="designer"
        :configuration="formData.configuration"
        :widget="widget"
      >
        <template slot="extraAutoCompleteSelectGroup" v-if="designer.SimpleFieldInfos != undefined">
          <a-select-opt-group>
            <span slot="label">
              <a-icon type="code" />
              表单数据
            </span>
            <a-select-option v-for="opt in fieldVarOptions" :key="opt.value" readonly :value="opt.value" :title="opt.label">
              {{ opt.label }}
            </a-select-option>
          </a-select-opt-group>
        </template>
      </DefaultVisibleConfiguration>
    </a-form-model>
    <WidgetEventHandler
      :widget="widget"
      :eventModel="formData.configuration.eventHandler"
      :designer="designer"
      :rule="{
        name: false,
        triggerSelectable: false,
        actionTypeSelectable: false,
        pageTypeSelectable: true,
        positionSelectable: false
      }"
      :allowEventParams="false"
    />
  </div>
</template>

<script>
import WidgetIconLibModal from '@pageAssembly/app/web/lib/widget-icon-lib-modal.vue';
import { statusOptions } from './constant';

export default {
  name: 'StepInfo',
  inject: ['designer', 'widget'],
  props: {
    formData: {
      type: Object,
      default: () => {}
    }
  },
  components: {
    WidgetIconLibModal
  },
  data() {
    return {
      statusOptions
    };
  },
  computed: {
    fieldVarOptions() {
      let opt = [];
      for (let i = 0, len = this.designer.SimpleFieldInfos.length; i < len; i++) {
        let info = this.designer.SimpleFieldInfos[i];
        opt.push({
          label: info.name,
          value: info.code
        });
      }
      return opt;
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
