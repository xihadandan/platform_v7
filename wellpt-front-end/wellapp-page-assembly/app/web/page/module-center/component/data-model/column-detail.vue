<template>
  <a-form-model :model="form" layout="vertical" :rules="rules" ref="form" class="pt-form">
    <a-form-model-item label="显示名称" prop="title">
      <a-input v-model="form.title" @change="onChangeColumnTitle"></a-input>
    </a-form-model-item>
    <a-form-model-item label="编码" prop="column">
      <a-input :maxLength="30" v-model="form.column" @change="e => onInputId2CaseFormate(e, 'toUpperCase')">
        <a-icon
          slot="suffix"
          title="自动翻译"
          :type="translating ? 'loading' : 'code'"
          style="color: rgba(0, 0, 0, 0.45); cursor: pointer"
          @click="translateTitle2Id"
        />
      </a-input>
    </a-form-model-item>
    <a-form-model-item label="类型" prop="dataType">
      <a-select :options="dataTypeOptions" v-model="form.dataType" @change="onChangeDataType" />
    </a-form-model-item>

    <a-form-model-item label="长度" v-show="form.dataType == 'number' || form.dataType == 'varchar'" prop="length">
      <a-input-number
        style="width: 100%"
        :min="1"
        v-model="form.length"
        :defaultValue="form.dataType == 'varchar' ? 64 : 9"
        :max="form.dataType == 'varchar' ? 1333 : 65"
      />
    </a-form-model-item>
    <a-form-model-item label="小数位" v-show="form.dataType == 'number'">
      <a-input-number v-model="form.scale" :min="0" :max="30" />
    </a-form-model-item>

    <a-form-model-item label="必填"><a-switch v-model="form.notNull" /></a-form-model-item>
    <a-form-model-item label="是否唯一" v-if="form.dataType != 'clob'">
      <a-radio-group v-model="form.unique" button-style="solid">
        <a-radio-button value="NOT">非唯一</a-radio-button>
        <a-radio-button value="TENANT">租户唯一</a-radio-button>
        <a-radio-button value="GLOBAL">全局唯一</a-radio-button>
      </a-radio-group>
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
  name: 'ColumnDetail',
  props: {
    column: Object,
    columnRows: Array,
    preserveCodes: Array
  },
  components: {},
  computed: {},
  data() {
    let form = { notNull: false, dataType: 'varchar', unique: 'NOT', length: 64 };
    if (this.column != undefined) {
      form = { ...this.column };
    }

    return {
      form,
      dataTypeOptions: [
        { label: '字符', value: 'varchar' },
        { label: '数字', value: 'number' },
        { label: '日期', value: 'timestamp' },
        { label: '大文本', value: 'clob' }
      ],
      labelCol: { span: 4 },
      wrapperCol: { span: 16 },
      rules: {
        title: [{ required: true, message: '显示名称必填', trigger: 'blur' }],
        column: [
          { required: true, message: '编码必填', trigger: 'blur' },
          { pattern: /^[a-zA-Z_]+\w+$/, message: '编码只允许包含字母、数字以及下划线', trigger: 'blur' },
          { trigger: ['blur', 'change'], validator: this.validateIfRepeat }
        ],
        length: [
          { required: form.dataType == 'varchar' || form.dataType == 'number', message: '长度必填', trigger: 'blur' },
          { trigger: ['blur', 'change'], validator: this.validateLength }
        ]
      },
      translating: false
    };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {},
  mounted() {},
  methods: {
    onChangeColumnTitle: debounce(function () {
      if (this.form.title && (this.form.column == undefined || this.form.column.trim() == '')) {
        this.translateTitle2Id();
      }
    }, 600),
    translateTitle2Id: debounce(function () {
      this.translating = true;
      this.$translate(this.form.title, 'zh', 'en')
        .then(text => {
          this.translating = false;
          let val = text.toUpperCase().replace(/( )/g, '_');
          if (val.length > 30) {
            val = val.substring(0, 30);
          }
          this.$set(this.form, 'column', val);
        })
        .catch(error => {
          this.translating = false;
        });
    }, 200),
    onChangeDataType() {
      this.rules.length[0].required = this.form.dataType == 'varchar' || this.form.dataType == 'number';
      if (this.form.dataType == 'number') {
        this.form.length = 12;
      } else if (this.form.dataType == 'varchar') {
        this.form.length = 64;
      } else if (this.form.dataType == 'clob') {
        this.form.unique = undefined;
      } else {
        this.form.length = undefined;
      }
    },
    validateLength: debounce(function (rule, value, callback) {
      callback();
    }, 200),
    validateIfRepeat: debounce(function (rule, value, callback) {
      if (this.columnRows) {
        for (let i = 0, len = this.columnRows.length; i < len; i++) {
          if (this.columnRows[i].column == value && this.form.uuid != this.columnRows[i].uuid) {
            // 编码重复
            callback(this.columnRows[i].isSysDefault ? '与内置属性编码重复' : '编码重复');
            return;
          }
        }
      }
      if (this.preserveCodes) {
        let conflict =
          this.preserveCodes.includes(value) ||
          this.preserveCodes.includes(value.toLowerCase()) ||
          this.preserveCodes.includes(value.toUpperCase());
        console.warn('系统预留编码: ', this.preserveCodes);
        callback(conflict ? '为系统预留编码, 不可使用' : undefined);
      }

      callback(undefined);
    }, 200),
    setFormData(formData) {
      this.form = formData;
    },
    onInputId2CaseFormate(e, caseType) {
      if (this.form.column != undefined) {
        if (caseType === 'toUpperCase' || caseType === 'toLowerCase') {
          // 自动转大写
          this.form.column = this.form.column[caseType]();
          let start = e.target.selectionStart;
          this.$nextTick(() => {
            e.target.setSelectionRange && e.target.setSelectionRange(start, start);
          });
        }
      }
    }
  }
};
</script>
