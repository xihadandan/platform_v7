<template>
  <!-- 表单字段选择 -->
  <w-tree-select
    v-model="formData[prop]"
    :treeData="treeData"
    :treeCheckable="treeCheckable"
    treeNodeFilterProp="title"
    :formData="formData"
    :formDataFieldName="formDataFieldName"
    :replaceFields="{
      children: 'children',
      title: 'name',
      key: 'id',
      value: 'id'
    }"
    @change="onChange"
  />
</template>
<script>
import WTreeSelect from '../components/w-tree-select';
import { each, map } from 'lodash';
export default {
  name: 'DyformFieldsTreeSelect',
  inject: ['designer', 'workFlowData'],
  props: {
    value: {
      type: [String, Object, Array]
    },
    formData: {
      type: Object,
      default: () => {}
    },
    prop: {
      type: String,
      default: 'field'
    },
    formDataFieldName: {
      type: String,
      default: ''
    },
    treeCheckable: {
      type: Boolean,
      default: true // 显示checkbox, true时multiple为true表示多选
    }
  },
  components: {
    WTreeSelect
  },
  computed: {
    treeData() {
      let treeData = [];
      if (this.workFlowData.property.formID) {
        let formDefinition = JSON.parse(this.designer.formDefinition.definitionVjson);
        treeData = map(formDefinition.fields, item => {
          return {
            id: item.configuration.code,
            name: item.configuration.name,
            children: []
          };
        });
        each(formDefinition.subforms, item => {
          treeData.push({
            id: item.configuration.formId,
            name: item.configuration.formName,
            checkable: false,
            children: map(item.configuration.columns, citem => {
              return {
                id: item.configuration.formUuid + ':' + citem.dataIndex,
                name: citem.title,
                children: []
              };
            })
          });
        });
        return treeData;
      }
      return treeData;
    }
  },
  data() {
    this.changeValue(this.formData[this.prop]);
    return {};
  },
  created() {},
  methods: {
    onChange(value, label, extra) {
      this.$emit('input', value);
      this.$emit('change', { value, label, extra });
    },
    changeValue(val) {
      if (val && typeof val == 'object') {
        this.formData[this.prop] = val.join(';');
      } else {
        this.formData[this.prop] = val;
      }
    }
  },
  watch: {
    value(val) {
      this.changeValue(val);
    }
  }
};
</script>
