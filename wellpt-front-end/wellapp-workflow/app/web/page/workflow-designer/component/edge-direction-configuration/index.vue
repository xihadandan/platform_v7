<template>
  <a-form-model
    ref="form"
    :model="formData"
    :rules="rules"
    :colon="false"
    labelAlign="left"
    :label-col="{ span: 6 }"
    :wrapper-col="{ span: 17, style: { textAlign: 'right' } }"
  >
    <a-tabs v-model="tabKey" size="small" class="flex-card-tabs flow-flex-card-tabs">
      <a-tab-pane key="0" tab="基本设置">
        <edge-direction-base-info :graphItem="graphItem" :formData="formData" :rules="rules" />
      </a-tab-pane>
      <template v-if="formData.type !== '3'">
        <!-- '3' 是判断点指向判断点只有分支条件设置，环节指向判断点没有没有配置 -->
        <a-tab-pane key="1" tab="分支流">
          <edge-direction-branch-flow :formData="formData" />
        </a-tab-pane>
        <a-tab-pane key="2" tab="归档设置">
          <edge-direction-archives-settings :formData="formData" :dataSource="formData.archives" />
        </a-tab-pane>
        <a-tab-pane key="3" tab="高级设置" :forceRender="true">
          <edge-direction-advanced-settings :formData="formData" />
        </a-tab-pane>
      </template>
    </a-tabs>
  </a-form-model>
</template>

<script>
import { directionRules as rules, getCustomRules } from '../designer/constant';
import EdgeDirectionBaseInfo from './base-info.vue';
import EdgeDirectionBranchFlow from './branch-flow.vue';
import EdgeDirectionArchivesSettings from './archives-settings.vue';
import EdgeDirectionAdvancedSettings from './advanced-settings.vue';

export default {
  name: 'EdgeDirectionConfiguration',
  props: {
    formData: {
      type: Object,
      default: () => {}
    },
    graphItem: {
      type: Object,
      default: () => {}
    }
  },
  components: {
    EdgeDirectionBaseInfo,
    EdgeDirectionBranchFlow,
    EdgeDirectionArchivesSettings,
    EdgeDirectionAdvancedSettings
  },
  data() {
    const rulesCustom = getCustomRules(rules);
    return {
      tabKey: '0',
      rules: rulesCustom
    };
  },
  mounted() {
    this.$emit('mounted', this);
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
