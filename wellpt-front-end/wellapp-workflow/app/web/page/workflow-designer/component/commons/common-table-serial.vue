<template>
  <div>{{ order }}</div>
</template>

<script>
export default {
  name: 'CommonTableSerial',
  inject: ['table'],
  props: {
    record: {
      type: Object,
      default: () => {}
    },
    order: {
      type: Number
    },
    index: {
      type: Number,
      default: 0
    }
  },
  mounted() {
    this.$emit('mounted', { record: this.record, index: this.index, vcTable: this.table });
  }
};
</script>
