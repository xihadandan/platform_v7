<template>
  <div>
    <a-form-model :model="formData" :rules="rules" ref="form" :label-col="labelCol" :wrapper-col="wrapperCol" :colon="false">
      <a-form-model-item label="编码" prop="code">
        <a-input v-model="formData.code" allow-clear :max-length="120" />
      </a-form-model-item>
      <a-form-model-item label="内容" prop="content">
        <a-input v-model="formData.content" allow-clear :max-length="300" />
      </a-form-model-item>
      <a-form-model-item label="应用于" prop="applyTo">
        <a-input v-model="formData.applyTo" allow-clear :max-length="120" />
      </a-form-model-item>
    </a-form-model>
  </div>
</template>
<style lang="less"></style>
<script type="text/babel">
import { debounce, camelCase } from 'lodash';

export default {
  name: 'EditI18nWord',
  inject: ['currentWindow', 'pageContext', '$event'],
  props: {},
  components: {},
  computed: {},
  data() {
    let formData = {};
    if (this.$event && this.$event.meta) {
      for (let key in this.$event.meta) {
        formData[camelCase(key)] = this.$event.meta[key];
      }
    }
    return {
      rules: {
        code: [{ required: true, message: '必填', trigger: 'blur' }],
        content: [{ required: true, message: '必填', trigger: 'blur' }],
        applyTo: [{ required: true, message: '必填', trigger: 'blur' }]
      },
      languageOptions: [],
      labelCol: { span: 4 },
      wrapperCol: { span: 18 },
      formData,
      loading: true,
      translating: {},
      lang: []
    };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {
    // this.fetchI18nOptions().then(() => {
    //   for (let opt of this.languageOptions) {
    //     opt.valueCase = camelCase(opt.value);
    //     this.$set(this.translating, opt.value, false);
    //   }
    //   this.loading = false;
    // });
  },
  mounted() {},
  methods: {
    save() {
      this.$loading('保存中');
      this.$refs.form.validate(valid => {
        if (valid) {
          this.checkExist().then(exist => {
            if (exist) {
              this.$message.error('该编码与应用于必须要唯一');
            } else {
              this.$axios
                .post(`/proxy/api/app/codeI18n/save`, [this.formData])
                .then(({ data }) => {
                  this.$loading(false);
                  this.currentWindow.close();
                  this.pageContext.emitEvent('csgmMfYjSikYEzqXUPUkenJtUASgunLL:refetch');
                })
                .catch(error => {
                  this.$loading(false);
                });
            }
          });
        } else {
          this.$loading(false);
        }
      });
    },
    checkExist() {
      return new Promise((resolve, reject) => {
        this.$axios
          .get(`/proxy/api/app/codeI18n/getAppCodeI18nByCodeAndApplyTo`, {
            params: {
              code: this.formData.code,
              applyTo: this.formData.applyTo
            }
          })
          .then(({ data }) => {
            let uuids = [];
            if (data.data.length > 0) {
              for (let d of data.data) {
                uuids.push(d.uuid);
              }
            }
            resolve(data.data.length > 0 && !uuids.includes(this.formData.uuid));
          });
      });
    },
    translateText: debounce(function (record, option) {
      if (option.value == 'zh_CN') {
        let promise = [];
        for (let i = 1, len = this.languageOptions.length; i < len; i++) {
          let to = this.languageOptions[i].transCode || this.languageOptions[i].value.split('_')[0];
          this.translating[this.languageOptions[i].value] = true;
          promise.push(this.invokeTranslateApi(record.zhCn, 'zh', to));
        }
        Promise.all(promise).then(res => {
          console.log(res);
          for (let i = 0, len = res.length; i < len; i++) {
            record[this.languageOptions[i + 1].valueCase] = res[i];
            this.translating[this.languageOptions[i + 1].value] = false;
          }
        });
      } else {
        this.translating[option.value] = true;
        this.invokeTranslateApi(record.zhCn, 'zh', option.transCode || option.value.split('_')[0]).then(text => {
          this.translating[option.value] = false;
          record[option.valueCase] = text;
        });
      }
    }, 300),
    invokeTranslateApi(word, from, to) {
      return new Promise((resolve, reject) => {
        this.$translate(word, from, to)
          .then(text => {
            resolve(text.toLowerCase());
          })
          .catch(error => {});
      });
    },

    fetchI18nOptions() {
      return new Promise((resolve, reject) => {
        this.languageOptions.splice(0, this.languageOptions.length);
        this.$axios
          .get(`/proxy/api/app/codeI18n/getAllLocales`, { params: {} })
          .then(({ data }) => {
            if (data.code == 0) {
              for (let d of data.data) {
                this.languageOptions.push({
                  label: d.name,
                  value: d.locale,
                  description: d.remark || d.name,
                  transCode: d.translateCode
                });
              }
            }
            resolve();
          })
          .catch(error => {});
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
