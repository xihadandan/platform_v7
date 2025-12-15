<template>
  <a-form-model :model="form" :label-col="labelCol" :wrapper-col="wrapperCol" :rules="rules" ref="form">
    <a-form-model-item label="名称" prop="name">
      <a-input v-model="form.name" @change="onChangeFormName"></a-input>
    </a-form-model-item>
    <a-form-model-item label="ID" prop="id">
      <a-input :maxLength="24" v-model="form.id" @change="e => onInputId2CaseFormate(e, 'toUpperCase')">
        <a-icon
          slot="suffix"
          :type="translating ? 'loading' : 'code'"
          style="color: rgba(0, 0, 0, 0.45); cursor: pointer"
          @click="translateName2Id"
          title="自动翻译"
        />
      </a-input>
    </a-form-model-item>
    <a-form-model-item label="描述">
      <a-textarea v-model="form.remark" />
    </a-form-model-item>
  </a-form-model>
</template>
<style lang="less"></style>
<script type="text/babel">
import { DEFAULT_SYS_COLUMNS } from './const';
import { debounce } from 'lodash';

export default {
  name: 'CreateDataModel',
  inject: ['currentModule'],
  props: {
    type: String
  },
  components: {},
  computed: {},
  data() {
    return {
      form: {},
      translating: false,
      labelCol: { span: 4 },
      wrapperCol: { span: 16 },
      rules: {
        name: [{ required: true, message: '名称必填', trigger: 'blur' }],
        id: [
          { required: true, message: 'ID必填', trigger: 'blur' },
          { pattern: /^\w+$/, message: 'ID只允许包含字母、数字以及下划线', trigger: 'blur' },
          { trigger: ['blur', 'change'], validator: this.validateIdExist }
        ]
      }
    };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {},
  mounted() {},
  methods: {
    validateIdExist: debounce(function (rule, value, callback) {
      $axios.get(`/proxy/api/dm/exist/${value}`, {}).then(({ data }) => {
        callback(data.data === false ? undefined : 'ID已存在');
      });
    }, 300),
    onChangeFormName: debounce(function () {
      if ((this.form.id == undefined || this.form.id.trim() == '') && this.form.name) {
        this.translateName2Id();
      }
    }, 600),
    translateName2Id: debounce(function () {
      this.translating = true;
      this.$translate(this.form.name, 'zh', 'en')
        .then(text => {
          this.translating = false;
          let val = text.toUpperCase().replace(/( )/g, '_');
          if (val.length > 24) {
            val = val.substring(0, 24);
          }
          this.$set(this.form, 'id', val);
        })
        .catch(error => {
          this.translating = false;
        });
    }, 200),
    onInputId2CaseFormate(e, caseType) {
      if (this.form.id != undefined) {
        if (caseType === 'toUpperCase' || caseType === 'toLowerCase') {
          // 自动转大写
          this.form.id = this.form.id[caseType]();
          let start = e.target.selectionStart;
          this.$nextTick(() => {
            e.target.setSelectionRange && e.target.setSelectionRange(start, start);
          });
        }
      }
    },
    getDefaultTableColumnJson() {
      return JSON.stringify(DEFAULT_SYS_COLUMNS);
    },
    save(callback) {
      let _this = this;
      this.$loading('保存中');
      let formData = {
        type: this.type,
        module: this.currentModule.id,
        columnJson: this.type == 'TABLE' ? this.getDefaultTableColumnJson() : JSON.stringify([]),
        ...this.form
      };

      this.$refs.form.validate(passed => {
        if (passed) {
          $axios
            .post(`/proxy/api/dm/save`, formData)
            .then(({ data }) => {
              _this.$loading(false);
              if (data.code != 0) {
                _this.$message.$error({
                  title: '保存失败',
                  content: _this.$createElement('div', {
                    domProps: {
                      innerHTML: Array.from(new Set(data.msg.split('\n'))).join('<br>')
                    }
                  })
                });
                console.error(data);
              } else {
                _this.$message.success('保存成功');
                _this.form = {};
                callback({
                  uuid: data.data,
                  id: formData.id,
                  name: formData.name
                });
              }
            })
            .catch(() => {
              _this.$message.error('保存失败');
              _this.$loading(false);
            });
        } else {
          _this.$loading(false);
        }
      });
    }
  }
};
</script>
