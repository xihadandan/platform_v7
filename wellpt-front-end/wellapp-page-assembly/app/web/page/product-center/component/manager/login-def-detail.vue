<template>
  <a-form-model :model="form" :label-col="labelCol" :wrapper-col="wrapperCol" :rules="rules" ref="form">
    <a-form-model-item label="名称" prop="name">
      <a-input v-model="form.name" />
    </a-form-model-item>
    <a-form-model-item label="显示标题">
      <a-input v-model="form.title" />
    </a-form-model-item>
    <a-form-model-item label="描述">
      <a-textarea v-model="form.remark" />
    </a-form-model-item>
  </a-form-model>
</template>
<style lang="less"></style>
<script type="text/babel">
import BasicConfig from '../../../login/component/login-basic/config.vue';
export default {
  name: 'LoginDefDetail',
  props: {
    prodVersionUuid: String,
    prodId: String,
    detail: Object
  },
  components: {},
  computed: {},
  data() {
    let form = {},
      rules = { name: [{ required: true, message: '名称必填', trigger: 'blur' }] };
    if (this.detail != undefined) {
      Object.assign(form, this.detail);
    }
    return {
      form,
      rules,
      labelCol: { span: 4 },
      wrapperCol: { span: 16 }
    };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {},
  mounted() {},
  methods: {
    save(isPc) {
      let submitData = {
        title: this.form.title,
        name: this.form.name,
        uuid: this.form.uuid,
        prodVersionUuid: this.prodVersionUuid,
        remark: this.form.remark,
        isDefault: this.form.isDefault,
        isPc
      };
      if (submitData.uuid == undefined) {
        submitData.defJson = JSON.stringify({
          type: 'LoginBasic',
          config: BasicConfig.methods.generateDefaultConfig()
        });
      }
      return new Promise((resolve, reject) => {
        $axios
          .post(`/proxy/api/system/${this.form.uuid != undefined ? 'updateLoginPageWithoutDefJson' : 'saveLoginPage'}`, submitData)
          .then(({ data }) => {
            if (data.code == 0) {
              if (submitData.uuid == undefined) {
                submitData.uuid = data.data;
                this.authLoginPageToDefaultTenant(submitData);
              }

              resolve(submitData);
            }
          })
          .catch(error => {});
      });
    },
    authLoginPageToDefaultTenant(source) {
      if (this._$USER.tenantId == 'T001') {
        $axios.post(`/proxy/api/system/saveLoginPage`, {
          sourceUuid: source.uuid,
          isPc: source.isPc,
          defJson: source.defJson,
          name: source.name,
          title: source.title,
          remark: source.remark,
          system: this.prodId,
          tenant: this._$USER.tenantId
        });
      }
    }
  }
};
</script>
