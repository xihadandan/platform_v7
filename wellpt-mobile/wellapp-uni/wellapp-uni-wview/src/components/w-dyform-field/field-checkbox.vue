<template>
  <dyform-field-base class="w-checkbox" :dyformField="dyformField">
    <template slot="editable">
      <!-- 单选模式 -->
      <view v-if="fieldDefinition.selectMode == '1'">
        <view class="input-row">
          <view class="field-label">
            <text v-if="isRequired" class="is-required">*</text>
            <text class="label-text">{{ fieldLabel }}</text>
          </view>
          <view class="field-content">
            <checkbox-group class="field-content-checkbox-group" @change="onSingleCheckboxChange">
              <label>
                <checkbox
                  :value="formData[fieldDefinition.name]"
                  :checked="formData[fieldDefinition.name] == singleCheckItem.value"
                />
                {{
                  formData[fieldDefinition.name] == singleCheckItem.value
                    ? singleCheckItem.text
                    : singleCheckItem.unCheckText
                }}
              </label>
            </checkbox-group>
          </view>
        </view>
        <view v-if="dyformField.errorMessage != null && dyformField.errorMessage != ''">
          <text class="error-message-text">{{ dyformField.errorMessage }}</text>
        </view>
      </view>
      <!-- 多选模式 -->
      <uni-collapse v-else-if="editorInline" class="field-collapse">
        <uni-collapse-item open>
          <template slot="title">
            <view class="field-collapse-item-label">
              <text v-if="isRequired" class="is-required">*</text>
              <text class="label-text">{{ fieldLabel }}</text>
            </view>
          </template>
          <view class="uni-list">
            <checkbox-group @change="onCheckboxChange">
              <label class="uni-list-cell uni-list-cell-pd" v-for="(item, index) in items" :key="index">
                <view>
                  <checkbox :value="item.value" :checked="item.checked" />
                </view>
                <view>{{ item.text }}</view>
              </label>
            </checkbox-group>
            <view v-if="dyformField.errorMessage != null && dyformField.errorMessage != ''">
              <text class="error-message-text">{{ dyformField.errorMessage }}</text>
            </view>
          </view>
        </uni-collapse-item>
      </uni-collapse>
      <!-- 弹出框 -->
      <view v-else class="field-popup uni-flex uni-row uni-forms-item__inner" @click="openCheckboxPopup">
        <view style="flex: 1">
          <view class="field-collapse-item-label">
            <text v-if="isRequired" class="is-required">*</text>
            <text class="label-text">{{ fieldLabel }}</text>
          </view>
          <view class="selectd-display-value">
            <label>{{ getDisplayValue }}</label>
          </view>
          <view v-if="dyformField.errorMessage != null && dyformField.errorMessage != ''">
            <text class="error-message-text">{{ dyformField.errorMessage }}</text>
          </view>
        </view>
        <view class="field-popup-arrow">
          <uni-icons class="icon" type="bottom" size="22"></uni-icons>
        </view>
      </view>

      <!-- 弹窗内容 -->
      <uni-popup ref="popup">
        <view class="field-popup-dialog">
          <scroll-view class="scroll-view" scroll-y="true">
            <view class="radio-list">
              <checkbox-group @change="onCheckboxChange($event, true)">
                <label class="uni-list-cell uni-list-cell-pd" v-for="(item, index) in items" :key="index">
                  <view style="display: none">
                    <checkbox :value="item.value" :checked="item.checked" />
                  </view>
                  <view class="uni-flex uni-row popup-radio-item">
                    <view style="flex: 1">{{ item.text }}</view>
                    <view v-if="item.checked" style="width: 20px">
                      <uni-icons type="checkmarkempty" :size="22" color="#2979ff" />
                    </view>
                  </view>
                </label>
              </checkbox-group>
            </view>
          </scroll-view>
          <view class="dialog-button-group">
            <view class="dialog-button uni-border-left" @click="closePopupDialog">
              <text class="dialog-button-text button-color">确定</text>
            </view>
          </view>
        </view>
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
      singleCheckItem: {},
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
      if (_self.isSingleCheck()) {
        _self.singleCheckItem = dataProvider[0];
      } else {
        _self.items = dataProvider;
      }
    });
  },
  methods: {
    isSingleCheck: function () {
      return this.fieldDefinition.selectMode == "1";
    },
    getDataProvider: function (callback) {
      var _self = this;
      if (_self.dataProvider != null) {
        callback.call(_self, _self.dataProvider);
        return;
      }
      _self.dataProvider = [];
      // 单选
      if (_self.isSingleCheck()) {
        var singleCheckContent = _self.fieldDefinition.singleCheckContent;
        var singleUnCheckContent = _self.fieldDefinition.singleUnCheckContent;
        var checkValue = dyformCommons.getRealValue(singleCheckContent);
        var checkText = dyformCommons.getDisplayValue(singleCheckContent);
        var unCheckValue = dyformCommons.getRealValue(singleUnCheckContent);
        var unCheckText = dyformCommons.getDisplayValue(singleUnCheckContent);
        var item = {
          id: dyformCommons.createUUID(),
          value: checkValue,
          text: checkText, //  + "/" + unCheckText
          unCheckValue: unCheckValue,
          unCheckText: unCheckText,
          name: _self.fieldDefinition.name,
        };
        _self.dataProvider.push(item);
        callback.call(_self, _self.dataProvider);
      } else {
        dyformCommons.getOptionData(_self.dyformField, function (dataProvider) {
          _self.dataProvider = dataProvider;
          callback.call(_self, _self.dataProvider);
        });
      }
    },
    setDisplayFieldValue: function () {
      const _self = this;
      let realDisplay = _self.fieldDefinition.realDisplay || {};
      if (!_.isEmpty(realDisplay.display)) {
        _self.formData[realDisplay.display] = _self.getDisplayValue;
      }
    },
    onSingleCheckboxChange: function (event) {
      var _self = this;
      var values = event.detail.value;
      // 选中
      if (values.length > 0) {
        _self.formData[_self.fieldDefinition.name] = _self.singleCheckItem.value;
      } else {
        // 取消
        _self.formData[_self.fieldDefinition.name] = _self.singleCheckItem.unCheckValue;
      }
      _self.setDisplayFieldValue();
    },
    onCheckboxChange: function (event) {
      var _self = this;
      var items = _self.items;
      var values = event.detail.value;
      for (let i = 0; i < items.length; i++) {
        const item = items[i];
        if (values.indexOf(item.value) >= 0) {
          item.checked = true;
          // _self.$set(item,'checked',true);
        } else {
          item.checked = false;
          //_self.$set(item,'checked',false);
        }
      }
      // _self.formData[_self.fieldDefinition.name] = values.join(";");
      _self.$set(_self.formData, _self.fieldDefinition.name, values.join(";"));
      _self.setDisplayFieldValue();
    },
    openCheckboxPopup: function (event) {
      this.$refs.popup.open("center");
    },
    closePopupDialog: function () {
      this.$refs.popup.close();
    },
  },
  computed: {
    getDisplayValue: function () {
      var _self = this;
      // 单选
      if (_self.isSingleCheck()) {
        if (_self.singleCheckItem.value == _self.formData[_self.fieldDefinition.name]) {
          return _self.singleCheckItem.text;
        } else {
          return _self.singleCheckItem.unCheckText;
        }
      } else {
        var value = _self.formData[_self.fieldDefinition.name];
        return dyformCommons.getDisplayValue2(value, _self.items);
      }
    },
    isRequired: function () {
      return this.dyformField.isRequired();
    },
    editorInline: function () {
      return false;
    },
  },
};
</script>

<style lang="scss" scoped>
.w-checkbox {
  .uni-list-cell {
    justify-content: flex-start;
  }
  .selectd-display-value {
    margin-left: 10px;
    color: $uni-text-color;
  }

  .field-label {
    /* #ifndef APP-NVUE */
    display: flex;
    flex-shrink: 0;
    box-sizing: border-box;
    /* #endif */
    flex-direction: row;
    align-items: center;
    width: 65px;
    // line-height: 2;
    // margin-top: 3px;
    padding: 5px 0;
    height: 36px;
    // margin-right: 5px;

    .label-text {
      font-size: 13px;
      color: $uni-text-color-label;
    }
    .label-seat {
      margin-right: 5px;
    }
  }
  .field-content {
    /* #ifndef APP-NVUE */
    width: 100%;
    box-sizing: border-box;
    min-height: 36px;
    /* #endif */
    flex: 1;
  }
  .field-content-checkbox-group {
    margin-left: 5px;
  }
  .error-input-row {
    height: 25px;
    line-height: 25px;
  }

  .field-popup {
    justify-content: center;
    align-items: center;
  }
  .field-popup-arrow {
    width: 36px;

    .icon {
      color: $uni-icon-color !important;
    }
  }

  .field-popup-dialog {
    padding-top: 11px;
    width: 300px;
    height: 240px;
    border-radius: 11px;
    background-color: $uni-bg-secondary-color;

    .scroll-view {
      height: 200px;
    }

    .popup-radio-item {
      width: 100%;
      height: 22px;
      justify-content: center;
      align-items: center;
      color: $uni-text-color;
    }

    .dialog-button-group {
      /* #ifndef APP-NVUE */
      display: flex;
      /* #endif */
      flex-direction: row;
      border-top-color: #f5f5f5;
      border-top-style: solid;
      border-top-width: 1px;
    }

    .dialog-button {
      /* #ifndef APP-NVUE */
      display: flex;
      /* #endif */

      flex: 1;
      flex-direction: row;
      justify-content: center;
      align-items: center;
      height: 45px;
    }
    .dialog-button-text {
      font-size: 16px;
      color: #333;
    }

    .border-left {
      border-left-color: #f0f0f0;
      border-left-style: solid;
      border-left-width: 1px;
    }

    .button-color {
      color: #007aff;
    }
  }
}
</style>
