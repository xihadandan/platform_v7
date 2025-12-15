<template>
  <!-- 表单布局下拉框 -->
  <w-select
    :value="value"
    :mode="mode"
    :formDataFieldName="formDataFieldName"
    :formData="formData"
    :options="options"
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
export default {
  name: 'FormBlocksSelect',
  inject: ['designer'],
  props: {
    value: {
      type: String
    },
    mode: {
      type: String
    },
    formDataFieldName: {
      type: String,
      default: ''
    },
    formData: {
      type: Object,
      default: () => {}
    },
    formUuid: {
      type: String,
      default: ''
    }
  },
  components: {
    WSelect
  },
  watch: {
    'designer.formDefinition': {
      deep: true,
      handler(formDefinition) {
        // this.getFormBlocks(formDefinition.uuid);
        this.getFormLayout();
      }
    }
  },
  data() {
    return {
      options: []
    };
  },
  created() {
    let uuid = '';
    if (this.formUuid) {
      uuid = this.formUuid;
    } else if (this.designer.formDefinition) {
      uuid = this.designer.formDefinition.uuid;
    }
    if (uuid) {
      // this.getFormBlocks(uuid);
      this.getFormLayout();
    }
  },
  methods: {
    getFormLayout() {
      const layouts = this.designer.getFormLayout();
      this.options = layouts.map(item => {
        let id = item.value;
        if (item.wtype === 'WidgetFormLayout') {
          id = item.widget.configuration.code + '';
        }
        return {
          name: `${item.name}-${item.title}`,
          id
        };
      });
    },
    getFormBlocks(uuid) {
      const params = {
        formUuid: uuid
      };
      this.$axios
        .get('/api/workflow/definition/getFormBlocks', {
          params
        })
        .then(res => {
          if (res.status === 200) {
            if (res.data && res.data.code === 0) {
              const data = res.data.data;
              this.options = data;
            }
          }
        });
    },
    onChange(value, option) {
      this.$emit('input', value);
      this.$emit('change', value, option);
    }
  }
};
</script>
