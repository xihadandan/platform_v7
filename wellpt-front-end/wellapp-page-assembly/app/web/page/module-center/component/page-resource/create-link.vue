<template>
  <a-form-model :model="form" :label-col="labelCol" :wrapper-col="wrapperCol" :rules="rules" ref="form">
    <a-form-model-item label="名称" prop="name">
      <a-input v-model="form.name" />
    </a-form-model-item>
    <a-form-model-item label="URL" prop="url">
      <a-input v-model="form.url" />
    </a-form-model-item>
    <a-form-model-item label="分组">
      <a-select :options="groupOptions" v-model="form.groupUuid" allow-clear />
    </a-form-model-item>
    <a-form-model-item label="描述">
      <a-textarea v-model="form.remark" />
    </a-form-model-item>
  </a-form-model>
</template>
<style lang="less"></style>
<script type="text/babel">
import { generateId, deepClone } from '@framework/vue/utils/util';

export default {
  name: 'CreateLink',
  inject: ['currentModule'],
  props: {
    groupOptions: Array,
    resource: Object,
    isPc: Boolean
  },
  components: {},
  computed: {},
  data() {
    let form = {};
    if (this.resource != undefined) {
      form = deepClone(this.resource);
    }
    return {
      form,
      labelCol: { span: 4 },
      wrapperCol: { span: 16 },
      rules: {
        name: [{ required: true, message: '名称必填', trigger: 'blur' }],
        url: [
          { required: true, message: 'URL必填', trigger: 'blur' },
          {
            pattern: /^(https?:\/\/[^\s]+)?\/[^\s\/]+(\/.*)?$/,
            trigger: 'blur',
            message: '请输入正确的URL地址'
          }
        ]
      }
    };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {},
  mounted() {},
  methods: {
    save(callback) {
      let _this = this;
      this.$loading('保存中');
      this.$refs.form.validate(passed => {
        if (passed) {
          $axios
            .post('/proxy/api/security/resource/saveBean', {
              ..._this.form,
              moduleId: _this.currentModule.id,
              code: generateId(),
              type: 'MENU',
              applyTo: _this.isPc ? 'PC' : 'MOBILE'
            })
            .then(({ data }) => {
              _this.$loading(false);
              if (data.data) {
                if (_this.form.groupUuid) {
                  $axios.get('/proxy/api/app/module/resGroup/updateMember', {
                    params: {
                      memberUuid: data.data,
                      groupUuid: _this.form.groupUuid,
                      type: 'resource'
                    }
                  });
                }
                callback({
                  uuid: data.data,
                  name: _this.form.name,
                  url: _this.form.url,
                  remark: _this.form.remark,
                  groupUuid: _this.form.groupUuid
                });
                if (_this.resource != undefined) {
                  _this.resource.name = _this.form.name;
                  _this.resource.url = _this.form.url;
                  _this.resource.remark = _this.form.remark;
                }
                _this.form = {};
              }
            });
        } else {
          _this.$loading(false);
        }
      });
    }
  }
};
</script>
