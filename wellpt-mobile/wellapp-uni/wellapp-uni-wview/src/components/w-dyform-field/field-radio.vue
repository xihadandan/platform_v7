<template>
  <dyform-field-base class="w-radio" :dyformField="dyformField">
    <template slot="editable">
      <!-- 内嵌编辑 -->
      <uni-collapse v-if="editorInline" class="field-collapse">
        <uni-collapse-item open>
          <template slot="title">
            <view class="field-collapse-item-label">
              <text v-if="isRequired" class="is-required">*</text>
              <text class="label-text">{{ fieldLabel }}</text>
            </view>
          </template>
          <view class="uni-list">
            <radio-group @change="onRadioChange">
              <label class="uni-list-cell uni-list-cell-pd" v-for="(item, index) in items" :key="index">
                <view>
                  <radio :value="item.value" :checked="item.value === formData[fieldDefinition.name]" />
                </view>
                <view>{{ item.text }}</view>
              </label>
            </radio-group>
            <view v-if="dyformField.errorMessage != null && dyformField.errorMessage != ''">
              <text class="error-message-text">{{ dyformField.errorMessage }}</text>
            </view>
          </view>
        </uni-collapse-item>
      </uni-collapse>
      <!-- 弹出框 -->
      <view v-else class="field-popup uni-flex uni-row uni-forms-item__inner" @click="openRadioPopup">
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
              <radio-group @change="onRadioChange($event, true)">
                <label class="uni-list-cell uni-list-cell-pd" v-for="(item, index) in items" :key="index">
                  <view style="display: none">
                    <radio :value="item.value" :checked="item.value === formData[fieldDefinition.name]" />
                  </view>
                  <view class="uni-flex uni-row popup-radio-item">
                    <view style="flex: 1">{{ item.text }}</view>
                    <view v-if="item.value === formData[fieldDefinition.name]" style="width: 20px">
                      <uni-icons type="checkmarkempty" :size="22" color="#2979ff" />
                    </view>
                  </view>
                </label>
              </radio-group>
            </view>
          </scroll-view>
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
    _self.getDataProvider(function (dataProvider) {
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
    setDisplayFieldValue: function () {
      const _self = this;
      let realDisplay = _self.fieldDefinition.realDisplay || {};
      if (!_.isEmpty(realDisplay.display)) {
        _self.formData[realDisplay.display] = _self.getDisplayValue;
      }
    },
    onRadioChange: function (event, closePop) {
      var _self = this;
      var items = _self.items;
      for (let i = 0; i < items.length; i++) {
        const item = items[i];
        if (item.value === event.detail.value) {
          // _self.formData[_self.fieldDefinition.name] = item.value;
          _self.$set(_self.formData, _self.fieldDefinition.name, item.value);
          _self.setDisplayFieldValue();
          break;
        }
      }
      if (closePop) {
        _self.$nextTick(() => {
          this.$refs.popup.close();
        });
      }
    },
    // 打开选择弹出框
    openRadioPopup: function () {
      this.$refs.popup.open("center");
    },
  },
  computed: {
    getDisplayValue: function () {
      var _self = this;
      var value = _self.formData[_self.fieldDefinition.name];
      return dyformCommons.getDisplayValue2(value, _self.items);
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
.w-radio {
  .label-text {
    color: $uni-text-color-label;
  }

  .uni-list-cell {
    justify-content: flex-start;
  }
  .selectd-display-value {
    margin-left: 10px;
    color: $uni-text-color;
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
    height: 210px;
    border-radius: 11px;
    background-color: $uni-bg-secondary-color;

    .scroll-view {
      height: 200px;
    }

    .popup-radio-item {
      width: 100%;
      justify-content: center;
      align-items: center;
      color: $uni-text-color;
    }
  }
}
</style>
