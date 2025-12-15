<template>
  <a-form-model-item label="字段长度">
    <a-input-number v-model="widget.configuration.length" :min="minNum" :max="maxNum" :disabled="isDisabled || disabled" />
  </a-form-model-item>
</template>
<style></style>
<script type="text/babel">
export default {
  name: 'FieldLengthInput',
  __ANT_NEW_FORM_ITEM: true,
  inject: ['designer'],
  props: {
    widget: Object,
    max: {
      type: Number,
      default: 3000
    },
    min: {
      type: Number,
      default: 1
    },
    isDisabled: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      // maxNum: this.widget.column != undefined && this.widget.column.length != undefined ? this.widget.column.length : this.max,
      minNum: this.min,
      disabled: this.widget.column != undefined && this.widget.column.length != undefined
    };
  },

  beforeCreate() {},
  components: {},
  computed: {
    maxNum() {
      return this.max;
    }
  },
  created() {},
  methods: {},
  mounted() {
    let _this = this;
    this.designer.handleEvent(`${this.widget.id}:dataModelColumnChanged`, () => {
      // _this.maxNum = _this.widget.column != undefined && _this.widget.column.length != undefined ? _this.widget.column.length : _this.max;
      _this.disabled = _this.widget.column != undefined && _this.widget.column.length != undefined;
    });
  }
};
</script>
