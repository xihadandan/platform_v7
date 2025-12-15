<template>
  <dyform-field-select class="w-job-select" :dyformField="dyformField"></dyform-field-select>
</template>

<script>
import dyformFieldSelect from "./field-select.vue";
export default {
  components: { dyformFieldSelect },
  props: {
    dyformField: {
      type: Object,
      required: true,
    },
  },
  data() {
    return {};
  },
  created() {},
  methods: {},
  computed: {},
};
</script>

<style lang="scss">
.w-job-select {
}
</style>
