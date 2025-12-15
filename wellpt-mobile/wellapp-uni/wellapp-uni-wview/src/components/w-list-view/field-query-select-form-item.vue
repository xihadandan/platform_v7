<template>
  <uni-forms-item :label="criterion.label">
    <view class="input-container">
      <input
        class="input-text"
        type="text"
        v-model="criterion.displayValue"
        disabled
        placeholder="请选择"
        @tap="openSelectPopup()"
      />
    </view>
    <!-- 弹窗内容 -->
    <uni-popup ref="popup">
      <w-popup-select
        class="w-list-view-popup"
        :title="criterion.label"
        :items="items"
        :multiple="isMultiSelect"
        @ok="onOk"
      ></w-popup-select>
    </uni-popup>
  </uni-forms-item>
</template>

<script>
import _ from "lodash";
import mixin from "./file-query-option-mixin";
export default {
  mixins: [mixin],
  data() {
    return {};
  },
  computed: {
    isMultiSelect: function () {
      let queryOptions = this.criterion.queryOptions || {};
      let options = queryOptions.options || {};
      return queryOptions && (queryOptions.multiple == "1" || options.multiSelectEnable);
    },
  },
  mounted() {
    const _self = this;
    _self.$set(_self.criterion, "displayValue", this.getDisplayValue());
  },
  methods: {
    openSelectPopup: function () {
      const _self = this;
      let values = (_self.criterion.value && _self.criterion.value.split(";")) || [];
      _.forEach(_self.items, function (item) {
        _self.$set(item, "checked", values.indexOf(item.value) >= 0);
      });
      _self.$refs.popup.open("bottom");
    },
    onOk: function (value, displayValue) {
      const _self = this;
      _self.$set(_self.criterion, "value", value);
      _self.$set(_self.criterion, "displayValue", displayValue);
      // _self.$forceUpdate();
      _self.$refs.popup.close();
    },
  },
};
</script>

<style lang="scss" scoped>
.input-container {
  /* #ifndef APP-NVUE */
  display: flex;
  box-sizing: border-box;
  /* #endif */
  flex-direction: row;
  align-items: center;
  border: 1px solid $uni-border-1;
  border-radius: 4px;

  font-size: 14px;
  min-height: 36px;
}

.input-text {
  width: 100%;
  padding-left: 10px;
}
</style>
