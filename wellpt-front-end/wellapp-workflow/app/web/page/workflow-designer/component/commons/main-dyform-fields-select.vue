<template>
  <!-- 流向下拉框 -->
  <w-select
    :value="value"
    :mode="mode"
    :formDataFieldName="formDataFieldName"
    :formData="formData"
    :options="options"
    :placeholder="placeholder"
    :replaceFields="{
      title: 'name',
      key: 'id',
      value: 'id'
    }"
    @change="onChange"
  />
</template>
<script>
import WSelect from '../components/w-select';
import { each, map } from 'lodash';
export default {
  name: 'mainDyformFieldsSelect',
  inject: ['designer', 'workFlowData'],
  props: {
    value: {
      type: String
    },
    mode: {
      type: String
    },
    placeholder: {
      type: String,
      default: '请选择字段'
    },
    formDataFieldName: {
      type: String,
      default: ''
    },
    formData: {
      type: Object,
      default: () => {}
    },
    isSubflow: {
      type: Boolean,
      default: false
    },
    subflowId: {
      type: String,
      default: ''
    }
  },
  components: {
    WSelect
  },
  computed: {
    options() {
      let options = [];
      if (this.isSubflow && this.subOptions) {
        return this.subOptions;
      } else if (this.workFlowData.property.formID) {
        let formDefinition = JSON.parse(this.designer.formDefinition.definitionVjson);
        options = map(formDefinition.fields, item => {
          return {
            id: item.configuration.code,
            name: item.configuration.name
          };
        });
      }
      return options;
    }
  },
  data() {
    return {
      subOptions: [],
      currentSubflowId: ''
    };
  },
  mounted() {
    if (this.isSubflow) {
      if (this.subflowId) {
        this.getSubOptions(this.subflowId);
      }
    }
  },
  methods: {
    onChange(value, option) {
      this.$emit('input', value);
      this.$emit('change', value, option);
    },
    getSubOptions(subflowId) {
      let subOptions = [];
      this.$axios.get('/api/workflow/definition/getFormFieldsByFlowDefId?flowDefId=' + subflowId).then(({ data }) => {
        if (data.data) {
          each(data.data, item => {
            if (item.children && item.children.length) {
              // subOptions = undefined;
              //从表不要
            } else {
              subOptions.push({
                id: item.id,
                name: item.name,
                type: item.type
              });
            }
          });
          if (this.subflowId == subflowId && subOptions.length) {
            this.$set(this, 'subOptions', subOptions);
            this.$emit('fieldOptionsChanged', { subflowId: this.subflowId, subOptions: this.subOptions });
          }
        }
      });
    }
  },
  watch: {
    subflowId(v) {
      if (this.isSubflow) {
        if (this.subflowId) {
          this.getSubOptions(this.subflowId);
        }
      }
    }
  }
};
</script>
