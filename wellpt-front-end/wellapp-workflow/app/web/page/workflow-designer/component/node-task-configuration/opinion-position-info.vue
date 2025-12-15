<template>
  <a-form-model
    ref="form"
    :model="formData"
    :rules="rules"
    :colon="false"
    labelAlign="right"
    :label-col="{ span: 3 }"
    :wrapper-col="{ span: 19, style: { textAlign: 'right' } }"
  >
    <a-form-model-item prop="argValue" label="意见名称">
      <a-input v-model="formData.argValue">
        <template slot="addonAfter">
          <w-i18n-input :target="formData" :code="vCode" v-model="formData.argValue" />
        </template>
      </a-input>
    </a-form-model-item>
    <a-form-model-item prop="value" label="意见值">
      <a-input v-model="formData.value"></a-input>
    </a-form-model-item>
  </a-form-model>
</template>

<script>
import { deepClone } from '@framework/vue/utils/util';
import WI18nInput from '@framework/vue/designer/w-i18n-input.vue';

export default {
  name: 'OpinionPositionInfo',
  inject: ['designer'],
  props: {
    formData: {
      type: Object,
      default: () => {}
    },
    nodeData: {
      type: Object,
      default: () => {}
    }
  },
  data() {
    let vCode = this.nodeData.id + '.opinionPosition';
    if (this.formData.value) {
      vCode = vCode + '.' + this.formData.value;
    }
    return {
      vCode,
      rules: {
        argValue: { required: true, message: '请输入意见名称', trigger: ['blur'] },
        value: { required: true, message: '请输入意见值', trigger: ['blur'] }
      }
    };
  },
  components: {
    WI18nInput
  },
  methods: {
    save(callback) {
      this.$refs.form.validate((valid, error) => {
        let data = deepClone(this.formData);
        callback({ valid, error, data: data });
      });
    }
  }
};
</script>
