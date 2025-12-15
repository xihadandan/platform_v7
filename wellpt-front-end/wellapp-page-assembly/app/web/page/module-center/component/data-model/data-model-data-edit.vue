<template>
  <a-form-model :model="form" :rules="rules" ref="form">
    <a-empty v-show="columns.length == 0" />
    <a-descriptions bordered :column="1" size="small" class="data-model-data-descriptions">
      <template v-for="(col, i) in columns">
        <!-- <a-form-model-item :label="col.title" :prop="col.columnIndex" v-if="!col.hidden">
        <template v-if="col.readonly">
          {{ form[col.columnIndex] }}
        </template>
        <template v-else-if="col.dataType === 'timestamp'">
          <a-date-picker
            show-time
            v-model="dateForm[col.columnIndex]"
            format="YYYY-MM-DD HH:mm:ss"
            @change="(date, dateString) => onChangeDate(date, dateString, col.columnIndex)"
          />
        </template>

        <template v-else>
          <a-input v-model="form[col.columnIndex]" />
        </template>
      </a-form-model-item> -->

        <a-descriptions-item v-if="!col.hidden" :span="1">
          <template slot="label">
            <label
              :title="col.title"
              :style="{
                display: 'block',
                maxWidth: '150px',
                textOverflow: 'ellipsis',
                overflow: 'hidden',
                'white-space': 'nowrap',
                position: 'relative',
                paddingLeft: '8px'
              }"
            >
              <span style="color: red; position: absolute; left: 0px; top: 4px" v-if="col.required">*</span>
              {{ col.title }}
            </label>
          </template>
          <a-form-model-item :label="null" :prop="col.columnIndex" style="margin: 0px; min-height: 40px">
            <template v-if="col.readonly">
              {{ form[col.columnIndex] }}
            </template>
            <template v-else-if="col.dataType === 'timestamp'">
              <a-date-picker
                show-time
                v-model="dateForm[col.columnIndex]"
                format="YYYY-MM-DD HH:mm:ss"
                @change="(date, dateString) => onChangeDate(date, dateString, col.columnIndex)"
              />
            </template>
            <template v-else-if="col.dataType === 'number'">
              <a-input-number v-model="form[col.columnIndex]" :precision="col.scale" :max="col.max" style="width: 100%" />
            </template>
            <template v-else-if="col.dataType === 'clob'">
              <a-textarea v-model="form[col.columnIndex]" />
            </template>
            <template v-else>
              <a-input v-model="form[col.columnIndex]" :maxLength="col.length" />
            </template>
          </a-form-model-item>
        </a-descriptions-item>
      </template>
    </a-descriptions>
  </a-form-model>
</template>
<style lang="less">
.data-model-data-descriptions {
  .ant-descriptions-item-label {
    width: 180px;
  }
  table {
    width: 100%;
    border-collapse: collapse;
  }
}
</style>
<script type="text/babel">
export default {
  name: 'DataModelDataEdit',
  mixins: [],
  props: {
    dataModel: Object,
    rowData: Object,
    showSysDefaultColumn: {
      type: Boolean,
      default: false
    }
  },
  data() {
    let originalData = {};
    if (this.rowData) {
      originalData = { ...this.rowData };
    }
    return {
      originalData,
      dateForm: {},
      add: this.rowData == undefined,
      labelCol: { span: 4 },
      wrapperCol: { span: 19 },
      form: {},
      columns: [],
      columnConfigMap: {},
      defaultFields: [
        'UUID',
        'CREATOR',
        'MODIFIER',
        'MODIFY_TIME',
        'CREATE_TIME',
        'TENANT',
        'SYSTEM',
        'REC_VER',
        'STATUS',
        'SYSTEM_UNIT_ID',
        'FORM_UUID'
      ],
      hideFields: ['STATUS', 'SYSTEM_UNIT_ID', 'FORM_UUID'],
      rules: {}
    };
  },
  watch: {
    showSysDefaultColumn: {
      handler(value) {
        this.setColumnHiddenOrShow();
      }
    }
  },
  beforeMount() {
    let cols = [],
      _this = this;
    this.rules = {};
    let columnJson = JSON.parse(this.dataModel.columnJson);
    for (let i = 0, len = columnJson.length; i < len; i++) {
      let columnIndex = columnJson[i].column,
        isDefaultField = this.defaultFields.includes(columnIndex),
        isHiddenField = this.hideFields.includes(columnIndex);
      if (isHiddenField) {
        continue;
      }
      this.columnConfigMap[columnJson[i].column] = columnJson[i];
      let col = {
        dataType: columnJson[i].dataType,
        columnIndex,
        readonly: isDefaultField,
        hidden: this.add && isDefaultField
      };
      if (!this.showSysDefaultColumn && isDefaultField) {
        col.hidden = true;
      }
      if (this.rowData != undefined) {
        this.$set(this.form, columnIndex, this.rowData[columnIndex]);
        if (col.dataType == 'timestamp') {
          this.$set(this.dateForm, columnIndex, this.rowData[columnIndex]);
        }
      }
      if (col.dataType == 'varchar') {
        col.length = columnJson[i].length;
      }
      if (col.dataType == 'number') {
        col.length = columnJson[i].length;
        col.scale = columnJson[i].scale || 0;
        col.max = ((intLen, precision = 0) => {
          const integerPart = Math.pow(10, intLen) - 1;
          const decimalPart = precision === 0 ? 0 : (Math.pow(10, precision) - 1) / Math.pow(10, precision);
          return parseFloat((integerPart + decimalPart).toFixed(precision));
        })(col.length, col.scale);
      }

      let colConfig = this.columnConfigMap[columnIndex];
      if (colConfig) {
        col.title = colConfig.title;
        col.required = colConfig.notNull === true;
        this.rules[columnIndex] = [{ required: colConfig.notNull, message: '必填', trigger: ['blur', 'change'] }];
        if (colConfig.unique == 'GLOBAL' || colConfig.unique == 'TENANT') {
          this.rules[columnIndex].push({
            trigger: ['blur'],
            validator: (rule, value, callback) => {
              if (value != undefined && value !== '') {
                _this.uniqueColumnDataValidator.apply(_this, [rule, value, callback, columnIndex, colConfig.unique]);
              } else {
                callback();
              }
            }
          });
        }
      }

      cols.push(col);
    }

    this.columns = cols;
    console.log('数据模型列', this.columns);
  },
  methods: {
    setColumnHiddenOrShow() {
      this.columns.map(item => {
        if (item.columnIndex && this.defaultFields.includes(item.columnIndex)) {
          item.hidden = this.showSysDefaultColumn ? false : true;
        }
      });
    },
    onChangeDate(date, dateString, code) {
      this.form[code] = dateString;
    },
    uniqueColumnDataValidator(rule, value, callback, columnIndex, unique) {
      if (value != '') {
        let params = {
          uuid: this.form.UUID,
          table: `UF_${this.dataModel.id}`.toUpperCase(),
          props: [
            {
              code: columnIndex,
              value
            }
          ]
        };
        $axios.post(`/proxy/api/dm/tableDataUnique/${unique}/uf_${this.dataModel.id}`, params).then(({ data }) => {
          callback(data.data ? undefined : new Error('数据不唯一'));
        });
      } else {
        callback();
      }
      callback(!_this.isEmptyValue() ? undefined : new Error(rule.message));
    },
    validateFormData() {
      this.$refs.form.validate((vali, msg) => {});
    },
    submit(callback) {
      let _this = this,
        formData = { table: `UF_${this.dataModel.id}`, props: [] };
      _this.$refs.form.validate(valid => {
        if (valid) {
          if (_this.add) {
            for (let k in this.form) {
              if (this.defaultFields.includes(k)) {
                formData[k] = this.form[k];
              } else {
                formData.props.push({
                  code: k,
                  value: this.form[k],
                  type: this.columnConfigMap[k].dataType
                });
              }
            }
          } else {
            // 比较有变更的值
            formData.uuid = this.originalData.UUID;
            formData.recVer = this.originalData.REC_VER;
            for (let k in this.form) {
              if (this.originalData[k] != this.form[k]) {
                formData.props.push({ code: k, value: this.form[k], type: this.columnConfigMap[k].dataType });
              }
            }
            if (formData.props.length == 0) {
              this.$message.info('数据无变更');
              return;
            }
          }

          $axios.post(`/proxy/api/dm/saveOrUpdateModelData`, formData).then(({ data }) => {
            if (data.code === 0) {
              // 提交成功
              _this.$message.success('保存成功');
              if (typeof callback == 'function') {
                callback();
              }
            }
          });
        }
      });
    }
  }
};
</script>
