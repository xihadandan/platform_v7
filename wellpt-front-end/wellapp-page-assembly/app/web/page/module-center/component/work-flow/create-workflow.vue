<template>
  <a-form-model :model="form" :label-col="labelCol" :wrapper-col="wrapperCol" :rules="rules" ref="form">
    <a-form-model-item label="名称" prop="name">
      <a-input v-model="form.name" @change="onChangeFormName" />
    </a-form-model-item>
    <a-form-model-item label="ID" prop="id" help="流程ID只允许包含字母、数字以及下划线">
      <a-input v-model="form.id">
        <a-icon
          slot="suffix"
          :type="translating ? 'loading' : 'code'"
          style="color: rgba(0, 0, 0, 0.45); cursor: pointer"
          @click="translateName2Id"
          title="自动翻译"
        />
      </a-input>
    </a-form-model-item>
    <a-form-model-item label="分类">
      <a-select :options="categoryOptions" v-model="form.category" style="width: 100%" />
    </a-form-model-item>
    <a-form-model-item label="描述">
      <a-textarea v-model="form.remark" />
    </a-form-model-item>
  </a-form-model>
</template>
<style lang="less"></style>
<script type="text/babel">
import { debounce } from 'lodash';
import WorkFlow from '@workflow/app/web/page/workflow-designer/component/designer/WorkFlow';
import FlowProperty from '@workflow/app/web/page/workflow-designer/component/designer/FlowProperty';

export default {
  name: 'CreateWorkflow',
  inject: ['currentModule'],
  props: { category: Array },
  components: {},
  computed: {
    categoryOptions() {
      let options = [];
      if (this.category) {
        for (let c of this.category) {
          options.push({
            label: c.name,
            value: c.uuid
          });
        }
      }
      return options;
    }
  },
  data() {
    return {
      form: { name: undefined, id: undefined, remark: undefined },
      labelCol: { span: 4 },
      wrapperCol: { span: 16 },
      rules: {
        name: [{ required: true, message: '名称必填', trigger: 'blur' }],
        id: [
          { required: true, message: 'ID必填', trigger: 'blur' },
          { pattern: /^\w+$/, message: '流程ID只允许包含字母、数字以及下划线', trigger: 'blur' },
          { trigger: ['blur', 'change'], validator: this.validateId }
        ]
      }
    };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {},
  mounted() {},
  methods: {
    validateId: debounce(function (rule, value, callback) {
      $axios
        .get('/proxy/api/workflow/definition/countById', {
          params: {
            flowDefId: value
          }
        })
        .then(({ data }) => {
          callback(data.data == '0' ? undefined : 'ID已存在');
        });
    }, 300),
    save(callback, pbNew = false) {
      this.$loading('保存中');
      this.$refs.form.validate(passed => {
        if (passed) {
          let flowProperty = new FlowProperty({
            categorySN: this.form.category
          });
          let workFlow = new WorkFlow({
            name: this.form.name,
            id: this.form.id,
            moduleId: this.currentModule.id,
            systemUnitId: 'S0000000000',
            property: flowProperty
          });
          $axios
            .post(`/proxy/api/workflow/scheme/json/save.action?pbNew=${pbNew}`, {
              ...workFlow
            })
            .then(
              res => {
                if (res.status === 200) {
                  const { data } = res;
                  callback({
                    uuid: data.uuid,
                    id: this.form.id,
                    name: this.form.name,
                    category: this.form.category,
                    version: '1.0'
                  });
                  this.toDesign(data);
                } else {
                  this.$message.error(res.message);
                }
                this.$loading(false);
              },
              err => {
                console.log(err.toJSON());
                let msg = '出错了';
                this.$message.error(msg);
                this.$loading(false);
              }
            );
        } else {
          this.$loading(false);
        }
      });
    },
    saveOld(callback) {
      let _this = this;
      this.$loading('保存中');
      let xml = `<flow name="${this.form.name}" id="${this.form.id}" code="" systemUnitId="S0000000000" moduleId="${
        this.currentModule.id
      }" titleExpression="" version="1.0" applyId="">
        <property>
        <categorySN>${this.form.category || ''}</categorySN>
        <moduleId>${this.currentModule.id}</moduleId>
        <formID></formID>
        <isActive>1</isActive>
        <pcShowFlag>1</pcShowFlag>
        <isMobileShow>1</isMobileShow>
        <remark></remark>
        </property>
      </flow>
        `;
      this.$refs.form.validate(passed => {
        if (passed) {
          $axios
            .post('/workflow/scheme/save.action?pbNew=false', { xmlString: xml })
            .then(({ data }) => {
              _this.$loading(false);
              if (data) {
                console.log('返回流程定义保存', data);
                let parser = new DOMParser();
                let doc = parser.parseFromString(data, 'text/html');
                callback({
                  uuid: doc.querySelector('flow').getAttribute('uuid'),
                  id: _this.form.id,
                  name: _this.form.name,
                  category: _this.form.category,
                  version: '1.0'
                });
                _this.form = {};
              }
            })
            .catch(() => {
              _this.$loading(false);
            });
        } else {
          _this.$loading(false);
        }
      });
    },
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
          this.$set(this.form, 'id', val);
        })
        .catch(error => {
          this.translating = false;
        });
    }, 200),
    toDesign(data) {
      const newUrl = `/workflow-designer/index?uuid=${data.uuid}`;
      window.open(newUrl, '_blank');
    }
  }
};
</script>
