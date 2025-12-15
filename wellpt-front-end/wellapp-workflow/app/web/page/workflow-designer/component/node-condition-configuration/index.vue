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
      <a-tab-pane key="0" tab="基本属性">
        <a-form-model-item class="form-item-vertical" prop="name" label="名称">
          <a-input v-model.trim="formData.name" @blur="changeName">
            <template slot="addonAfter">
              <w-i18n-input :target="formData" :code="formData.id + '.conditionName'" v-model="formData.name" />
            </template>
          </a-input>
        </a-form-model-item>
        <a-form-model-item class="form-item-vertical" label="描述">
          <a-textarea
            v-model="formData.remark"
            :rows="4"
            :style="{
              height: 'auto'
            }"
          />
        </a-form-model-item>
      </a-tab-pane>
    </a-tabs>
  </a-form-model>
</template>

<script>
import { debounce } from 'lodash';
import { conditionRules as rules, getCustomRules } from '../designer/constant';
import WI18nInput from '@framework/vue/designer/w-i18n-input.vue';

export default {
  name: 'NodeConditionConfiguration',
  inject: ['graph'],
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
    WI18nInput
  },
  data() {
    const rulesCustom = getCustomRules(rules);
    return {
      tabKey: '0',
      rules: rulesCustom,
      selectedCellId: null
    };
  },
  mounted() {
    this.$emit('mounted', this);
    this.setSelectedCellId();
  },
  methods: {
    setSelectedCellId() {
      this.selectedCellId = this.graph.instance.selectedId;
    },
    // 更改名称
    changeName: function (event) {
      const value = event.target.value;
      if (this.graph.instance) {
        this.graph.instance.setEdgesLablesByName(value, this.selectedCellId);
      }
    },
    validate(callback) {
      this.$refs.form.validate((valid, error) => {
        callback({ valid, error, data: this.formData });
      });
    }
  }
};
</script>
