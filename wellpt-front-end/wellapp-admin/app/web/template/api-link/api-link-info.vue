<template>
  <a-form-model :model="form" :label-col="labelCol" :wrapper-col="wrapperCol" :rules="rules" ref="form" class="pt-form">
    <a-form-model-item label="名称" prop="name">
      <a-input v-model="form.name" :maxLength="120"></a-input>
    </a-form-model-item>
    <a-form-model-item label="ID" prop="id">
      <a-input v-model="form.id" :maxLength="64"></a-input>
    </a-form-model-item>
    <a-form-model-item label="协议类型">
      <a-radio-group v-model="form.protocol" button-style="solid">
        <a-radio-button value="REST">REST</a-radio-button>
        <a-radio-button value="SOAP">SOAP</a-radio-button>
      </a-radio-group>
    </a-form-model-item>
    <a-form-model-item label="备注">
      <a-textarea v-model="form.remark" :maxLength="300"></a-textarea>
    </a-form-model-item>
  </a-form-model>
</template>
<style lang="less"></style>
<script type="text/babel">
import { debounce } from 'lodash';
export default {
  name: 'ApiLinkInfo',
  inject: ['pageContext', '$event', 'currentWindow'],
  props: {},
  components: {},
  computed: {},
  data() {
    let form = {
      protocol: 'REST'
    };
    return {
      labelCol: { span: 4 },
      wrapperCol: { span: 19 },
      form,
      rules: {
        name: { required: true, message: '名称必填', trigger: ['blur', 'change'] },
        id: [
          { required: true, message: 'ID必填', trigger: ['blur', 'change'] },
          { trigger: ['blur', 'change'], validator: this.checkIdExist }
        ]
      }
    };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {},
  mounted() {},
  methods: {
    checkIdExist: debounce(function (rule, value, callback) {
      $axios
        .get(`/proxy/api/apiLink/existApiLinkId`, {
          params: {
            id: value
          }
        })
        .then(({ data }) => {
          if (data.code == 0) {
            callback(data.data ? 'ID 已被使用' : undefined);
          } else {
            callback('服务异常');
          }
        });
    }, 500),
    save() {
      let _this = this;
      this.$refs.form.validate(valid => {
        if (valid) {
          _this.$axios
            .post(`/proxy/api/apiLink/saveApiLink`, _this.form)
            .then(({ data }) => {
              if (data.code == 0) {
                _this.$message.success('保存成功');
                // 刷新表格
                _this.pageContext.emitEvent('NWKBEGDALjNhYtXCyZsEWafgBUKaaqCz:refetch');
                if (_this.currentWindow) {
                  _this.currentWindow.close();
                }
              }
            })
            .catch(error => {
              console.log(error);
            });
        }
      });
    }
  },
  META: {
    method: {
      save: '保存'
    }
  }
};
</script>
