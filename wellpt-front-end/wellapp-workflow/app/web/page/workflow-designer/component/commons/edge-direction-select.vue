<template>
  <!-- 流向下拉框 -->
  <w-select
    :value="value"
    :mode="mode"
    :formDataFieldName="formDataFieldName"
    :formData="formData"
    :options="options"
    :getPopupContainer="getPopupContainer"
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
  name: 'EdgeDirectionSelect',
  inject: ['graph'],
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
    getPopupContainer: {
      type: Function,
      default: triggerNode => {
        return triggerNode.parentNode;
      }
    }
  },
  components: {
    WSelect
  },
  computed: {
    options() {
      let options = [];
      if (this.graph.instance) {
        options = this.graph.instance.directionsData;
      }
      return options;
    }
  },
  methods: {
    onChange(value, option) {
      this.$emit('input', value);
      this.$emit('change', value, option);
    }
  }
};
</script>
