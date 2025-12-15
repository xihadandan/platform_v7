<template>
  <div>
    <a-form-model-item label="默认值">
      <a-select :style="{ width: '100%' }" v-model="defaultValueType" @change="onSelectChange" :getPopupContainer="getPopupContainerByPs()">
        <a-select-option value="no">无</a-select-option>
        <a-select-option value="define">自定义</a-select-option>
      </a-select>

      <div v-show="configuration.hasDefaultValue">
        <template v-if="defineSlots">
          <slot name="define"></slot>
        </template>
        <template v-else-if="dataType == 'number'">
          <a-input-number v-model="configuration.defaultValue" style="width: 100%"></a-input-number>
        </template>
        <template v-else-if="dataType == 'options'">
          <a-select
            v-model="selectValue"
            :options="options"
            :style="{ width: '100%' }"
            :mode="multiple ? 'multiple' : 'default'"
            :getPopupContainer="getPopupContainerByPs()"
            :showArrow="true"
            @change="onChangeSelectValue"
          ></a-select>
        </template>
        <template v-else>
          <VariableDefineTemplate
            v-model="configuration.defaultValueOption"
            ref="variableTpt"
            :valueStringIsLable="true"
            :variableTreeData="treeData"
            :enableSysVar="false"
            @input="inputChange"
          />
        </template>
      </div>
    </a-form-model-item>
    <a-form-model-item label="计算模式" v-show="defaultValueType === 'define'">
      <a-radio-group size="small" v-model="configuration.valueCreateMethod" button-style="solid">
        <a-radio-button value="4">显示时计算</a-radio-button>
        <a-radio-button value="3">提交时计算</a-radio-button>
      </a-radio-group>
    </a-form-model-item>
  </div>
</template>
<style lang="less"></style>
<script type="text/babel">
import { dateSysVariables, userSysVariables } from './constant';
import { getPopupContainerByPs } from '@dyform/app/web/page/dyform-designer/utils';
export default {
  name: 'FieldDefaultValue',
  mixins: [],
  props: {
    selectVariable: {
      type: Boolean,
      default: false
    },
    dataType: String,
    configuration: Object,
    variables: Array,
    options: Array,
    multiple: Boolean,
    separator: {
      type: String,
      default: ';'
    }
  },
  data() {
    let dateChildren = [];
    for (let k in dateSysVariables) {
      dateChildren.push({
        value: dateSysVariables[k],
        key: dateSysVariables[k],
        title: k
      });
    }
    let userInfoVarChildren = [];
    for (let k in userSysVariables) {
      userInfoVarChildren.push({
        value: userSysVariables[k],
        key: userSysVariables[k],
        title: k
      });
    }
    let selectValue = this.configuration.defaultValue;
    if (this.multiple && this.dataType == 'options') {
      if (selectValue) {
        selectValue = selectValue.split(this.separator);
      } else {
        selectValue = [];
      }
    }
    let defineSlots = this.$scopedSlots.define != undefined;
    return {
      defaultValueType: 'no',
      treeData: this.variables || [
        {
          title: '当前日期',
          value: 'varTypeCurrentDate',
          key: 'varTypeCurrentDate',
          selectable: false,
          children: dateChildren
        },
        {
          title: '当前用户',
          value: 'varTypeCurrentUser',
          key: 'varTypeCurrentUser',
          selectable: false,
          children: userInfoVarChildren
        }
      ],
      selectValue: selectValue,
      defineSlots
    };
  },

  beforeCreate() {},
  components: {},
  computed: {},
  created() {
    if (!this.configuration.hasOwnProperty('hasDefaultValue')) {
      this.$set(this.configuration, 'hasDefaultValue', false);
    } else {
      this.defaultValueType = this.configuration.hasDefaultValue ? 'define' : 'no';
    }
    if (!this.configuration.hasOwnProperty('defaultValue')) {
      this.$set(this.configuration, 'defaultValue', null);
    }
    if (!this.configuration.hasOwnProperty('valueCreateMethod')) {
      this.$set(this.configuration, 'valueCreateMethod', '4');
    }
  },
  methods: {
    getPopupContainerByPs,
    onSelectChange(v) {
      this.configuration.hasDefaultValue = v === 'define';
      if (v === 'no') {
        this.clear();
      }
    },
    clear() {
      this.configuration.valueCreateMethod = '4';
      this.configuration.defaultValue = '';
      this.configuration.defaultValueOption = null;
      if (this.$refs.variableTpt) {
        this.$refs.variableTpt.onClearAll();
      }
    },
    inputChange(val) {
      this.configuration.defaultValue = val.value;
    },
    defaultValueValidate(options) {
      let values = this.configuration.defaultValue || '';
      let newValues = [];
      if (values) {
        if (typeof values === 'string') {
          values = values.split(this.separator);
        }
      }
      if (values.length) {
        values.forEach(item => {
          let hasIndex = options.findIndex(option => {
            return item === option.value;
          });
          if (hasIndex > -1) {
            newValues.push(item);
          }
        });
      }
      this.selectValue = this.multiple ? newValues : newValues.join('');
      this.onChangeSelectValue();
    },
    onChangeSelectValue() {
      this.configuration.defaultValue = this.multiple ? this.selectValue.join(this.separator) : this.selectValue;
    }
  },
  mounted() {},
  watch: {
    options: {
      deep: true,
      handler(v) {
        if (this.dataType == 'options' && v) {
          if (v.length) {
            this.defaultValueValidate(v);
          } else {
            this.selectValue = this.multiple ? [] : '';
            this.onChangeSelectValue();
          }
        }
      }
    },
    multiple(v) {
      if (this.dataType == 'options') {
        if (v) {
          // 多选是数组
          if (this.selectValue && typeof this.selectValue == 'string') {
            this.selectValue = this.selectValue.split(this.separator);
          }
        } else {
          // 单选是字符串
          if (typeof this.selectValue == 'object') {
            this.selectValue = this.selectValue && this.selectValue.length ? this.selectValue[0] : '';
            this.onChangeSelectValue();
          }
        }
      }
    }
  }
};
</script>
