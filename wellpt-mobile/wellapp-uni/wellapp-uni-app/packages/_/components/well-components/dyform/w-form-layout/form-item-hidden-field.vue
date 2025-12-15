<template>
  <uni-forms-item
    v-show="false"
    :name="formModelItemProp"
    @fieldStateChange="(e) => onHideFieldStateChange(e, obj)"
  ></uni-forms-item>
</template>
<style lang="scss"></style>
<script type="text/babel">
import formElement from "../w-dyform/form-element.mixin";

export default {
  name: "FormItemHiddenField",
  mixins: [formElement],
  props: {},
  components: {},
  computed: {},
  data() {
    return {};
  },
  beforeCreate() {},
  created() {
    this.isFormModelItemHiddenField = true;
  },
  beforeMount() {},
  mounted() {},
  methods: {
    afterDisplayStateChanged(oldState, currentState) {
      this.$emit("fieldStateChange", { oldState, currentState });
    },
  },
};
</script>
