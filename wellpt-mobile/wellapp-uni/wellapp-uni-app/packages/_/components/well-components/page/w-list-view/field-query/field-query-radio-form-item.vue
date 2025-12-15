<template>
  <uni-forms-item :label="criterion.label">
    <uni-w-switch v-if="isSwitch" ref="switchRef" v-model="checked" @change="changeSwitch">
      <template>
        <text slot="checkedChildren">{{ items[0].text }}</text>
        <text slot="unCheckedChildren">{{ items[1].text }}</text>
      </template>
    </uni-w-switch>
    <uni-w-data-checkbox v-else :multiple="false" mode="default" v-model="criterion.value" :localdata="items" />
  </uni-forms-item>
</template>

<script>
import mixin from "./file-query-option-mixin";
export default {
  mixins: [mixin],
  data() {
    return {
      checked: false,
    };
  },
  computed: {
    isSwitch: function () {
      let queryOptions = this.criterion.queryOptions || {};
      let options = queryOptions.options || {};
      return options.switchStyle && this.items.length == 2;
    },
  },
  methods: {
    changeSwitch(e) {
      let checked = e.detail.value;
      this.checked = checked;
      this.$set(this.criterion, "value", checked ? this.items[0].value : this.items[1].value);
    },
    itemsComplete() {
      if (this.options.switchStyle) {
        if (this.items.length == 0) {
          this.items.push({
            text: "",
            value: true,
          });
          this.items.push({
            text: "",
            value: false,
          });
        }
        this.checked = this.criterion.value == this.items[0].value;
        if (this.criterion.value == undefined && !this.checked && this.items.length == 2) {
          this.$set(this.criterion, "value", this.items[1].value);
        }
      }
    },
  },
  mounted() {},
};
</script>

<style lang="scss" scoped></style>
