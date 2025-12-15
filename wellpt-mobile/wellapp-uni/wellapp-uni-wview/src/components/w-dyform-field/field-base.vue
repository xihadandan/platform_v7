<template>
  <view :class="'field' + ' field-' + fieldDefinition.name">
    <!-- 非隐藏 -->
    <!-- 可编辑 -->
    <slot v-if="isEditable && !isDisplayAsLabel && !isHidden" name="editable">
      <uni-forms-item
        :labelPosition="labelPosition"
        :class="'label-position-' + labelPosition"
        :errorMessage="dyformField.errorMessage"
        :label="fieldLabel"
        :required="isRequired"
      >
        <slot name="editable-input">
          <uni-easyinput v-model="formData[fieldDefinition.name]" placeholder="请输入" />
        </slot>
      </uni-forms-item>
      <!-- <view class="input-row">
        <text class="title">{{fieldLabel}}
          <text v-if="isRequired" style="color: red">*</text>
        </text>
        <input class="uni-input" placeholder="请输入" :password="isPassword" v-model="formData[fieldDefinition.name]" />
      </view> -->
    </slot>
    <!-- 非隐藏 -->
    <!-- 不可编辑，显示为文本 -->
    <slot
      v-else-if="
        (isDisplayAsLabel || (fieldDefinition.showType == '2' && fieldDefinition.readStyle == '2')) && !isHidden
      "
      name="displayAsLabel"
    >
      <uni-forms-item :label="fieldLabel" :required="isRequired">
        <label>{{ formData[fieldDefinition.name] }}</label>
      </uni-forms-item>
      <!-- <view class="input-row">
        <text class="title">{{fieldLabel}}
          <text v-if="isRequired" style="color: red">*</text>
        </text>
        <label >{{formData[fieldDefinition.name]}}</label>
      </view> -->
    </slot>
    <!-- 非隐藏 -->
    <!-- 不可编辑，只读 -->
    <slot v-else-if="fieldDefinition.showType == '2' && fieldDefinition.readStyle == '3' && !isHidden" name="disabled">
      <uni-forms-item :label="fieldLabel" :required="isRequired">
        <uni-easyinput v-model="formData[fieldDefinition.name]" placeholder="请输入" disabled="disabled" />
      </uni-forms-item>
      <!-- <view class="input-row">
        <text class="title">{{fieldLabel}}
          <text v-if="isRequired" style="color: red">*</text>
        </text>
        <input  class="uni-input" disabled="disabled" :password="isPassword" v-model="formData[fieldDefinition.name]" />
      </view> -->
    </slot>
  </view>
</template>

<script>
// const _ = require('lodash');
import mixin from "./field-mixin";
export default {
  mixins: [mixin],
  created() {
    let _self = this;
    if (!_self.formData.hasOwnProperty(_self.fieldDefinition.name)) {
      _self.$set(_self.formData, _self.fieldDefinition.name, null);
    }
  },
  methods: {},
  computed: {
    isPassword: function () {
      return this.fieldDefinition.isPasswdInput == "true";
    },
    isRequired: function () {
      return this.dyformField.isRequired();
    },
    isDisplayAsLabel: function () {
      return this.formScope.isDisplayAsLabel();
    },
    isEditable: function () {
      return this.fieldDefinition.showType == "1";
    },
    isHidden: function () {
      return this.fieldDefinition.isShow === false || this.fieldDefinition.showType == "5";
    },
    labelPosition: function () {
      let displayMode = this.fieldDefinition.displayMode || this.fieldDefinition.displayStyle || "2";
      return displayMode == "2" ? "top" : "left";
    },
  },
};
</script>

<style scoped>
.field {
}
/* .field-row-multiple >>> .is-direction-left {
  flex-direction: column;
} */
/* .field-row-2 >>> .is-direction-left {
  flex-direction: column;
}
.field-row-2 >>> .uni-forms-item__label {
  width: 100% !important;
} */
</style>
