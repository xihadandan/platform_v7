<template>
  <view class="w-form-text" :class="customClassCom" :ref="fieldCode">
    <rich-text :nodes="text" style="word-break: break-all"></rich-text>
  </view>
</template>

<script>
import formElement from "../w-dyform/form-element.mixin";
import formCommonMixin from "../w-dyform/form-common.mixin";
// #ifndef APP-PLUS
import "./index.scss";
// #endif
export default {
  name: "w-text",
  mixins: [formElement, formCommonMixin],
  data() {
    return {
      text: this.$t(this.widget.id, this.widget.configuration.content),
    };
  },
  components: {},
  computed: {},
  created() {
    if (this.dyform.$textset == undefined) {
      this.dyform.$textset = {};
    }
    this.dyform.$textset[this.widget.id] = this;
  },
  methods: {
    setFormulaCalculateFieldValue() {
      this.tryCalculateFormulaApiDataValue();
      let v = this.getFormulaCalculateValue();
      this.setValue(v);
    },
    setValue(text) {
      this.text = text;
    },

    getValue() {
      return this.text;
    },
  },
  mounted() {},
};
</script>
<style lang="scss">
/* #ifdef APP-PLUS */
@import "./index.scss";
/* #endif */
</style>
