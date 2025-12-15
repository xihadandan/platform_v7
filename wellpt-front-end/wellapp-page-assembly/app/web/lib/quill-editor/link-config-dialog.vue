<template>
  <a-modal
    :visible="sVisible"
    :title="editMode ? '编辑链接' : '插入链接'"
    :maskClosable="false"
    :maskStyle="{ background: 'transparent' }"
    @ok="handleOk"
    @cancel="handleCancel"
    okText="保存"
    cancelText="取消"
  >
    <a-form-model ref="form" :model="localForm" :rules="rules">
      <a-form-model-item prop="text" label="显示文字">
        <a-input v-model="localForm.text" :placeholder="rules['text']['message']" />
      </a-form-model-item>
      <a-form-model-item prop="href" label="链接地址">
        <a-input v-model="localForm.href" :placeholder="rules['href']['message']" />
      </a-form-model-item>
      <a-form-model-item label="目标窗口">
        <!-- <a-input v-model="localForm.target" /> -->
        <a-select :options="targetOptions" v-model="localForm.target" />
      </a-form-model-item>
      <!-- <a-form-model-item label="rel">
        <a-input v-model="localForm.rel" placeholder="noopener noreferrer" />
       </a-form-model-item>
      <a-form-model-item label="data-id">
        <a-input v-model="localForm.dataId" placeholder="自定义ID" />
       </a-form-model-item> -->
    </a-form-model>
  </a-modal>
</template>

<script>
export default {
  name: 'LinkConfigDialog',
  props: {
    visible: { type: Boolean, default: false },
    editMode: { type: Boolean, default: false },
    value: {
      type: Object,
      default: () => ({
        text: '',
        href: '',
        target: '_blank',
        rel: '',
        dataId: ''
      })
    }
  },
  data() {
    return {
      sVisible: !!this.visible,
      localForm: { ...this.value },
      rules: {
        text: { required: true, message: '请输入显示文字' },
        href: { required: true, message: '请输入链接地址' }
      },
      targetOptions: [
        { label: '新窗口 (_blank)', value: '_blank' },
        { label: '本窗口 (_self)', value: '_self' }
        // { label: '父窗口 (_parent)', value: '_parent' }
      ]
    };
  },
  watch: {
    visible: function (val) {
      this.sVisible = val;
    },
    value: {
      handler(val) {
        this.localForm = { ...val };
      },
      deep: true
    }
    // localForm: {
    //   handler(val) {
    //     this.$emit('input', val);
    //   },
    //   deep: true
    // }
  },
  methods: {
    handleOk() {
      this.$refs.form.validate((valid, error) => {
        if (valid) {
          this.$emit('save', this.localForm);
        }
      });
    },
    handleCancel() {
      this.$emit('cancel');
    }
  }
};
</script>
