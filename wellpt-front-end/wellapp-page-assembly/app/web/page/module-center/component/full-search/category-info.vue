<template>
  <a-form-model
    ref="form"
    :model="formData"
    :rules="rules"
    :labelCol="{ style: { width: '70px', float: 'left' } }"
    :wrapperCol="{ style: { width: 'calc(100% - 70px)', float: 'left' } }"
  >
    <a-form-model-item prop="name" label="名称">
      <a-input v-model="formData.name" />
    </a-form-model-item>
  </a-form-model>
</template>

<script>
import { fetchSaveCategory } from './api';

export default {
  name: 'CategoryInfo',
  props: {
    info: {
      type: Object,
      default: () => {}
    }
  },
  data() {
    let formData = JSON.parse(JSON.stringify(this.info.data || this.info));

    return {
      formData,
      rules: {
        name: { required: true, message: '请输入名称' }
      }
    };
  },
  methods: {
    validate(callback) {
      this.$refs.form.validate((valid, error) => {
        // if (valid) {
        //   fetchSaveCategory(this.formData).then(uuid => {
        //     this.formData.uuid = uuid;
        //     callback({ valid, error, data: this.formData });
        //   });
        //   return;
        // }
        callback({ valid, error, data: this.formData });
      });
    }
  }
};
</script>
