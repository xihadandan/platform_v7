<template>
  <w-select
    :value="value"
    :replaceFields="{
      title: 'name',
      key: 'id',
      value: 'id'
    }"
    :options="useOrgTreeData"
    :allowClear="allowClear"
    @change="changeOrgId"
  />
</template>

<script>
import WSelect from '../components/w-select';
import mixins from '../mixins';

export default {
  name: 'UseOrgSelect',
  mixins: [mixins],
  props: {
    value: {
      type: String
    },
    allowClear: {
      type: Boolean,
      default: true
    }
  },
  components: {
    WSelect
  },
  methods: {
    changeOrgId(value, option) {
      this.$emit('input', value);
      this.$emit('change', ...arguments);
    }
  }
};
</script>
