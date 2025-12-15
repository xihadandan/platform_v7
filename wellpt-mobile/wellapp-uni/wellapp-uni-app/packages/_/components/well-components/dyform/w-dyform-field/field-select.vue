<template>
  <dyform-field-base class="w-select" :dyformField="dyformField">
    <template slot="editable-input">
      <view @click="onSelectFocus">
        <uni-easyinput
          v-model="getDisplayValue"
          type="text"
          :clearable="false"
          :disabled="true"
          placeholder="请选择"
          @focus="onSelectFocus"
        >
          <template slot="right">
            <view class="field-popup-arrow">
              <uni-icons class="icon" type="right" :size="22" />
            </view>
          </template>
        </uni-easyinput>
      </view>
      <!-- 弹窗内容 -->
      <uni-popup ref="popup">
        <w-popup-select :title="fieldLabel" :items="items" :multiple="isMultiSelect" @ok="onOk"></w-popup-select>
      </uni-popup>
    </template>
    <template slot="displayAsLabel">
      <uni-forms-item :label="fieldLabel">
        <label>{{ getDisplayValue }}</label>
      </uni-forms-item>
    </template>
    <template slot="disabled">
      <uni-forms-item :label="fieldLabel">
        <label>{{ getDisplayValue }}</label>
      </uni-forms-item>
    </template>
  </dyform-field-base>
</template>

<script>
const _ = require("lodash");
import dyformFieldBase from "./field-base.vue";
import dyformCommons from "../w-dyform/uni.DyformCommons";
import mixin from "./field-mixin";
export default {
  mixins: [mixin],
  components: { dyformFieldBase },
  data() {
    return {
      items: [],
    };
  },
  created() {
    var _self = this;
    var realValue = _self.formData[_self.fieldDefinition.name];
    if (_.isEmpty(_.trim(realValue))) {
      realValue = "";
    }
    var realValues = realValue.split(";");
    _self.getDataProvider(function (dataProvider) {
      for (var i = 0; i < dataProvider.length; i++) {
        for (var j = 0; j < realValues.length; j++) {
          if (realValues[j] == dataProvider[i].value) {
            dataProvider[i].checked = true;
          }
        }
      }
      _self.items = dataProvider;
    });
  },
  methods: {
    getDataProvider: function (callback) {
      var _self = this;
      if (_self.dataProvider != null) {
        callback.call(_self, _self.dataProvider);
        return;
      }
      dyformCommons.getOptionData(_self.dyformField, function (dataProvider) {
        _self.dataProvider = dataProvider;
        callback.call(_self, _self.dataProvider);
      });
    },
    onSelectFocus: function (event) {
      console.log(event);
      // open 方法传入参数 等同在 uni-popup 组件上绑定 type属性
      this.$refs.popup.open("bottom");
    },
    setDisplayFieldValue: function () {
      const _self = this;
      let realDisplay = _self.fieldDefinition.realDisplay || {};
      if (!_.isEmpty(realDisplay.display)) {
        _self.formData[realDisplay.display] = _self.getDisplayValue;
      }
    },
    onCheckboxChange: function (event) {
      var _self = this;
      var items = _self.items;
      var values = event.detail.value;
      for (let i = 0; i < items.length; i++) {
        const item = items[i];
        if (values.indexOf(item.value) >= 0) {
          // item.checked = true;
          _self.$set(item, "checked", true);
        } else {
          // item.checked = false;
          _self.$set(item, "checked", false);
        }
      }
      _self.setDisplayFieldValue();
    },
    onRadioChange: function (event) {
      var _self = this;
      var items = _self.items;
      for (let i = 0; i < items.length; i++) {
        const item = items[i];
        if (item.value === event.detail.value) {
          // item.checked = true;
          _self.$set(item, "checked", true);
        } else {
          // item.checked = false;
          _self.$set(item, "checked", false);
        }
      }
      _self.setDisplayFieldValue();
      // 单选关闭弹出框
      _self.onOk();
    },
    onOk: function (value, displayValue) {
      const _self = this;
      _self.$refs.popup.close("bottom");
      // _self.formData[_self.fieldDefinition.name] = checkedValues.join(";");
      _self.$set(_self.formData, _self.fieldDefinition.name, value);
    },
  },
  computed: {
    isRequired: function () {
      return this.dyformField.isRequired();
    },
    getDisplayValue: function () {
      var _self = this;
      var value = _self.formData[_self.fieldDefinition.name];
      return dyformCommons.getDisplayValue2(value, _self.items);
    },
    // 是否多选
    isMultiSelect: function () {
      return this.fieldDefinition.multiSelect == true || this.fieldDefinition.selectMultiple == true;
    },
  },
};
</script>

<style lang="scss" scoped>
.w-select {
  .uni-list-cell {
    justify-content: flex-start;
  }

  .field-popup-arrow {
    width: 36px;

    .icon {
      color: $uni-icon-color !important;
    }
  }
}
</style>
