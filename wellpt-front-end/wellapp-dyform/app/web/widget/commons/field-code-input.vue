<template>
  <div>
    <a-form-model-item prop="code">
      <template slot="label">
        <a-tooltip placement="topRight" :arrowPointAtCenter="true">
          <ul slot="title">
            <li>单个表单中的字段编码不可重复</li>
            <li>只能以小写英文字母开头</li>
            <li>仅支持字母、数字、下划线</li>
            <li>不能为系统预留字段</li>
          </ul>
          <label>
            <span>{{ label }}</span>
            <a-icon type="exclamation-circle" />
          </label>
        </a-tooltip>
      </template>
      <a-input
        :maxLength="30"
        v-model.trim="widget.configuration[propName]"
        @change="onInputCodeFormater"
        :readOnly="readonly"
        @blur="onCodeInputBlur"
      >
        <template slot="prefix" v-if="readonly">
          <a-icon type="lock" title="锁定" />
        </template>
        <a-icon
          v-else
          slot="prefix"
          title="自动翻译"
          :type="translating ? 'loading' : 'code'"
          style="color: rgba(0, 0, 0, 0.45); cursor: pointer"
          @click="translateCodeFromName"
        />

        <template slot="addonAfter" v-if="showPeristAsColumn">
          <a-tooltip :title="widget.configuration.persistAsColumn ? '保存' : '不保存'" placement="topRight" :arrowPointAtCenter="true">
            <a-switch v-model="widget.configuration.persistAsColumn" size="small" />
          </a-tooltip>
        </template>
      </a-input>
    </a-form-model-item>

    <!-- <a-form-model-item label="名称显示位置" v-if="!parentIsWidgetFormItem">
      <a-radio-group v-model="widget.configuration.labelPosition" button-style="solid" size="small">
        <a-radio-button value="left">左侧</a-radio-button>
        <a-radio-button value="top">顶部</a-radio-button>
      </a-radio-group>
    </a-form-model-item> -->

    <a-form-model-item v-if="allowClobDataType" label="是否开启大字段" class="item-lh" :wrapper-col="{ style: { textAlign: 'right' } }">
      <a-switch
        checked-children="是"
        un-checked-children="否"
        :checked="widget.configuration.dbDataType == '16'"
        :loading="dataTypeChangeChecking"
        @change="e => onChangeToClobDataType(e)"
      />
    </a-form-model-item>
  </div>
</template>
<style></style>
<script type="text/babel">
import { debounce } from 'lodash';

export default {
  name: 'FieldCodeInput',
  __ANT_NEW_FORM_ITEM: true,
  inject: ['designer', 'dataModelColumnMap', 'definition'],
  props: {
    widget: Object,
    label: {
      type: String,
      default: '字段编码'
    },
    propName: {
      type: String,
      default: 'code'
    },
    isReadonly: {
      type: Boolean,
      default: false
    },
    formatTranslateValue: Function,
    allowClobDataType: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      translating: false,
      readonly: this.widget.column != undefined,
      showPeristAsColumn: false,
      dataTypeChangeChecking: false
    };
  },

  beforeCreate() {},
  components: {},
  computed: {
    parentIsWidgetFormItem() {
      let node = this.designer.widgetTreeMap[this.widget.id];
      if (node) {
        let parent = node.parentKey ? this.designer.widgetTreeMap[node.parentKey] : null;
        return parent && parent.wtype == 'WidgetFormItem';
      }
      return false;
    }
  },
  created() {
    if (this.isReadonly) {
      this.readonly = true;
    }

    if (this.$root.definitionVjson && this.$root.definitionVjson.dataModelUuid && this.$root.definitionVjson.useDataModel) {
      if (this.widget.column == undefined) {
        this.showPeristAsColumn = true;
        if (!this.widget.configuration.hasOwnProperty('persistAsColumn')) {
          this.$set(this.widget.configuration, 'persistAsColumn', true);
        }
      }
    }
    // 与作为字段组件相关的必要属性：
    if (!this.widget.configuration.hasOwnProperty('isDatabaseField')) {
      this.widget.configuration.isDatabaseField = true; // 默认为数据库持久化字段
    }
    if (!this.widget.configuration.hasOwnProperty('isLabelValueWidget')) {
      this.widget.configuration.isLabelValueWidget = false; // 默认非文本/值组件
    }
    if (!this.widget.configuration.hasOwnProperty('uneditableDisplayState')) {
      this.widget.configuration.uneditableDisplayState = 'label'; // 默认不可编辑的展示方式为文本：label
    }
    // 字段的校验规则设置
    if (this.widget.configuration.isDatabaseField && !this.widget.configuration.hasOwnProperty('validateRule')) {
      this.$set(this.widget.configuration, 'validateRule', { trigger: ['change'], regExp: {} });
    }
    if (this.widget.column) {
      // 取最新的数据模型字段数据进行赋值，修正长度、唯一性、必填性
      let { title, column, notNull, dataType, length, scale, unique } = this.dataModelColumnMap[this.widget.column.code.toLowerCase()];
      if (this.widget.configuration.code == undefined || this.widget.configuration.code == '') {
        // 初始赋值
        this.$set(this.widget.configuration, 'name', title);
        this.$set(this.widget.configuration, 'code', this.widget.column.code);
        if (dataType == 'clob') {
          this.$set(this.widget.configuration, 'dbDataType', '16');
        }
      }
      if (notNull) {
        this.$set(this.widget.configuration, 'required', true);
        this.widget.column.notNull = true;
      }
      this.$set(this.widget.configuration, 'length', length);
      this.widget.column.length = length;
      if (this.widget.wtype == 'WidgetFormInputNumber') {
        this.$set(this.widget.configuration, 'precision', length);
        this.$set(this.widget.configuration, 'decimalPlacesNumber', scale);
        this.$set(this.widget.configuration, 'scale', scale);
        this.widget.column.scale = scale;
      }
      if (unique == 'GLOBAL' || unique == 'TENANT') {
        this.widget.column.unique = unique;
        this.$set(this.widget.configuration.validateRule, 'uniqueType', unique == 'GLOBAL' ? 'globalUnique' : 'tenantUnique');
      }
    }
  },
  methods: {
    onChangeToClobDataType(checked) {
      let _this = this,
        existColumn = false;
      if (this.definition.uuid != undefined) {
        let fields = JSON.parse(this.definition.definitionVjson).fields;
        if (fields) {
          for (let f of fields) {
            if (f.id == this.widget.id) {
              existColumn = true;
              if (f.configuration.dbDataType == '16' && !checked && this.widget.configuration.dbDataType == '16') {
                // 判断是否存在字段非空
                this.dataTypeChangeChecking = true;
                this.$axios
                  .post('/json/data/services', {
                    serviceName: 'formDataService',
                    methodName: 'queryTotalCountOfFormDataOfMainform',
                    args: JSON.stringify([this.definition.tableName, f.configuration.code + ' is not null '])
                  })
                  .then(({ data }) => {
                    this.dataTypeChangeChecking = false;
                    if (data.code == 0) {
                      if (parseInt(data.data) > 0) {
                        this.$message.error('该字段存在非空数据, 无法变更数据类型');
                      } else {
                        this.$set(this.widget.configuration, 'dbDataType', null);
                      }
                    }
                  })
                  .catch(error => {
                    this.dataTypeChangeChecking = false;
                  });
                return;
              }
              break;
            }
          }
        }
      }
      if (checked) {
        if (existColumn) {
          this.$confirm({
            title: '确定要开启大字段吗?',
            content: '开启前, 请确认对该表完成最新数据备份, 并且暂停所有涉及该表的业务操作, 避免数据丢失或不一致',
            onOk() {
              return new Promise((resolve, reject) => {
                _this.$set(_this.widget.configuration, 'dbDataType', '16');
                resolve();
              }).catch();
            },
            onCancel() {}
          });
        } else {
          _this.$set(_this.widget.configuration, 'dbDataType', '16');
        }
      } else {
        this.widget.configuration.dbDataType = null;
      }
    },
    translateCodeFromName: debounce(function () {
      if (this.widget.configuration.name) {
        this.translating = true;
        this.$translate(this.widget.configuration.name, 'zh', 'en')
          .then(text => {
            this.translating = false;
            let val = text.toLowerCase().replace(/( )/g, '_');
            if (val.length > 30) {
              val = val.substring(0, 30);
            }
            if (typeof this.formatTranslateValue == 'function') {
              val = this.formatTranslateValue(val);
            }
            this.$el.querySelector('input').value = val;
            this.$el.querySelector('input').dispatchEvent(new Event('input'));
            this.onCodeInputBlur();
          })
          .catch(error => {
            this.translating = false;
          });
      }
    }, 300),
    onCodeInputBlur() {
      if (this.widget.configuration[this.propName] != undefined && this.widget.configuration[this.propName].length && !this.readonly) {
        // 保留字判断
        this.preservedCheck();
        this.$emit('change', this.widget.configuration[this.propName]);
      }
    },
    onInputCodeFormater: debounce(function (e) {
      if (!/^[a-z]/.test(this.widget.configuration[this.propName]) || /^[0-9]/.test(this.widget.configuration[this.propName])) {
        let _this = this;
        this.$message.error('只能以小写英文字母开头, 且编码只允许包含字母、数字以及下划线', 2);
        _this.widget.configuration[_this.propName] = this.widget.configuration[this.propName].replace(/^[^a-zA-Z]+/, '').toLowerCase();
        return;
      }

      if (this.widget.configuration[this.propName] != undefined && this.widget.configuration[this.propName].length) {
        try {
          this.widget.configuration[this.propName] = this.widget.configuration[this.propName].toLowerCase();
          let start = e.target.selectionStart;
          this.$nextTick(() => {
            e.target.setSelectionRange && e.target.setSelectionRange(start, start);
          });
        } catch (error) {}
      }
    }, 300),
    // 字段保留字校验、字段重复性校验
    preservedCheck: debounce(function (v) {
      let _code = this.widget.configuration[this.propName],
        _this = this;

      for (let i = 0, len = this.designer.SimpleFieldInfos.length; i < len; i++) {
        // 判断字段编码是否重复
        if (this.widget.id != this.designer.SimpleFieldInfos[i].id) {
          const relaFields = this.designer.SimpleFieldInfos[i].relaFields;
          if (relaFields && relaFields.length) {
            for (let index = 0; index < relaFields.length; index++) {
              if (relaFields[index]['code'] && relaFields[index]['code'].toLowerCase() == _code.toLowerCase()) {
                this.$message.error('字段编码 ' + _code + ' 重复', 2).then(() => {
                  _this.widget.configuration[this.propName] = null;
                });
                return;
              }
            }
          } else if (
            this.designer.SimpleFieldInfos[i].code &&
            this.designer.SimpleFieldInfos[i].code.toLowerCase() == _code.toLowerCase()
          ) {
            this.$message.error('字段编码 ' + _code + ' 重复', 2).then(() => {
              _this.widget.configuration[this.propName] = null;
            });
            return;
          }
        }
      }
      let preserved = this.designer.isPreservedField(_code);
      if (preserved) {
        this.$message.error('字段编码 ' + _code + ' 为系统预留编码', 2).then(() => {
          _this.widget.configuration[this.propName] = null;
        });
      }
    }, 300)
  },
  mounted() {
    let _this = this;
    this.designer.handleEvent(`${this.widget.id}:dataModelColumnChanged`, () => {
      _this.readonly = _this.widget.column != undefined;
    });
  }
};
</script>
