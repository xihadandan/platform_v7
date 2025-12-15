<template>
  <a-form-model :model="form" :label-col="labelCol" :wrapper-col="wrapperCol" :rules="rules" ref="form">
    <a-form-model-item label="表单名称" prop="name">
      <a-input v-model="form.name" @change="onChangeFormName" />
    </a-form-model-item>
    <a-form-model-item label="表单ID" prop="id" help="表单ID只允许包含字母、数字以及下划线">
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
    <a-form-model-item label="桌面表单" prop="pFormUuid" v-if="!isPc">
      <a-select
        :showSearch="true"
        :allowClear="true"
        defaultValue="1"
        v-model="form.pFormUuid"
        :options="allPFormOptions"
        :style="{ width: '100%' }"
        @change="onPFormChange"
      ></a-select>
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
import { debounce } from 'lodash';
export default {
  name: 'CreateDyform',
  inject: ['currentModule'],
  props: {
    groupOptions: Array,
    isPc: Boolean
  },
  components: {},
  computed: {},
  data() {
    return {
      form: {},
      translating: false,
      labelCol: { span: 4 },
      wrapperCol: { span: 16 },
      allPFormOptions: [],
      rules: {
        name: [{ required: true, message: '表单名称必填', trigger: 'blur' }],
        pFormUuid: [{ required: !this.isPc, message: '请选择桌面表单', trigger: 'blur' }],
        id: [
          { required: true, message: '表单ID', trigger: 'blur' },
          { pattern: /^\w+$/, message: '表单ID只允许包含字母、数字以及下划线', trigger: 'blur' },
          { trigger: ['blur', 'change'], validator: this.validateFormId }
        ]
      }
    };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {
    if (!this.isPc) {
      this.getAllPforms();
    }
  },
  mounted() {},
  methods: {
    validateFormId: debounce(function (rule, value, callback) {
      $axios
        .post('/json/data/services', {
          serviceName: 'formDefinitionService',
          methodName: 'isFormExistById',
          args: JSON.stringify([value])
        })
        .then(({ data }) => {
          callback(data.data === false ? undefined : '表单ID已存在');
        });
    }, 300),
    onPFormChange() {
      if (this.form.pFormUuid) this.getPformDetails(this.form.pFormUuid);
    },
    getPformDetails(uuid) {
      $axios
        .get('/pt/dyform/definition/getFormDefinitionByUuid', {
          params: {
            uuid
          }
        })
        .then(({ data }) => {
          if (data && data.definitionVjson) {
            // 存储单据表名
            this.form.tableName = data.tableName;
            this.form.relationTbl = data.relationTbl;
          }
        });
    },
    getAllPforms() {
      $axios
        .post('/common/select2/query', {
          serviceName: 'dyFormFacade',
          queryMethod: 'queryAllPforms'
        })
        .then(({ data }) => {
          if (data.results) {
            this.allPFormOptions = [];
            for (let i = 0, len = data.results.length; i < len; i++) {
              this.allPFormOptions.push({
                label: data.results[i].text,
                value: data.results[i].id
              });
            }
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
    save(callback) {
      let _this = this;
      this.$loading('保存中');
      let formData = new FormData();
      let definition = {
        id: this.form.id,
        name: this.form.name,
        remark: this.form.remark,
        tableName: this.form.tableName || `UF_${this.form.id}`,
        moduleId: this.currentModule.id,
        formType: this.isPc ? 'P' : 'M',
        definitionJson: JSON.stringify({
          tableName: `UF_${this.form.id}`,
          blocks: {},
          fields: {},
          databaseFields: {},
          subforms: {},
          useDataModel: false
        }),
        definitionVjson: JSON.stringify({
          tableName: `UF_${this.form.id}`,
          widgets: [],
          lifecycleHook: {},
          fields: [],
          subforms: [],
          useDataModel: false
        })
      };
      if (!this.isPc) {
        definition.relationTbl = this.form.relationTbl;
      }

      formData.set('formDefinition', JSON.stringify(definition));
      formData.set('deletedFieldNames', JSON.stringify([]));
      this.$refs.form.validate(passed => {
        if (passed) {
          $axios.post('/proxy/pt/dyform/definition/save', formData).then(({ data }) => {
            _this.$loading(false);
            if (data.data) {
              if (_this.form.groupUuid) {
                $axios.get('/proxy/api/app/module/resGroup/updateMember', {
                  params: {
                    memberUuid: data.data,
                    groupUuid: _this.form.groupUuid,
                    type: 'formDefinition'
                  }
                });
              }
              callback({
                uuid: data.data,
                id: _this.form.id,
                name: _this.form.name,
                tableName: definition.tableName,
                version: '1.0',
                groupUuid: _this.form.groupUuid,
                justCreated: true
              });
              _this.form = {};
              this.toDesign(data);
            }
          });
        } else {
          _this.$loading(false);
        }
      });
    },
    toDesign(data) {
      const newUrl = `/dyform-designer/index?uuid=${data.data}`;
      window.open(newUrl, '_blank');
    }
  }
};
</script>
