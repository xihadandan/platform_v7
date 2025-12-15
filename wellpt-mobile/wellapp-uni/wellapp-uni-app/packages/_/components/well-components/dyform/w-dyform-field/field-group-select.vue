<template>
  <dyform-field-base class="w-group-select" :dyformField="dyformField">
    <template slot="editable-input">
      <view @click="onSelectFocus">
        <uni-easyinput
          v-model="getDisplayValue"
          type="text"
          :clearable="false"
          :disabled="true"
          placeholder="请选择"
          @focus="onSelectFocus"
        />
      </view>
      <!-- 弹窗内容 -->
      <uni-popup ref="popup">
        <view class="popup-group-select">
          <view class="popup-title">
            <text class="popup-title-text">{{ fieldLabel }}</text>
          </view>
          <view class="popup-content">
            <scroll-view
              class="popup-content-scroll-view"
              :class="{ 'hide-confirm-btn': !isMultiSelect }"
              scroll-y="true"
            >
              <view class="uni-list">
                <checkbox-group v-if="isMultiSelect" @change="onCheckboxChange">
                  <uni-collapse accordion>
                    <uni-collapse-item v-for="(group, groupIndex) in groups" :key="groupIndex" :title="group.group">
                      <label class="uni-list-cell uni-list-cell-pd" v-for="(item, index) in group.list" :key="index">
                        <view style="display: none">
                          <checkbox :value="item.value" :checked="item.checked" />
                        </view>
                        <view class="popup-check-item">
                          <view style="flex: 1">{{ item.text }}</view>
                          <view class="popup-check-icon" v-if="item.checked">
                            <uni-icons type="checkmarkempty" :size="22" color="#2979ff" />
                          </view>
                        </view>
                      </label>
                    </uni-collapse-item>
                  </uni-collapse>
                </checkbox-group>
                <radio-group v-else @change="onRadioChange">
                  <uni-collapse accordion>
                    <uni-collapse-item v-for="(group, groupIndex) in groups" :key="groupIndex" :title="group.group">
                      <label class="uni-list-cell uni-list-cell-pd" v-for="(item, index) in group.list" :key="index">
                        <view style="display: none">
                          <radio :value="item.value" :checked="item.checked" />
                        </view>
                        <view class="popup-check-item">
                          <view style="flex: 1">{{ item.text }}</view>
                          <view class="popup-check-icon" v-if="item.checked">
                            <uni-icons type="checkmarkempty" :size="22" color="#2979ff" />
                          </view>
                        </view>
                      </label>
                    </uni-collapse-item>
                  </uni-collapse>
                </radio-group>
              </view>
            </scroll-view>
          </view>
          <view v-if="isMultiSelect" class="popup-button-box">
            <button class="popup-button" @tap="onOk">确定</button>
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
      groups: [],
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
    var dataProvider = _self.getDataProvider();
    for (var i = 0; i < dataProvider.length; i++) {
      for (var j = 0; j < realValues.length; j++) {
        if (realValues[j] == dataProvider[i].value) {
          dataProvider[i].checked = true;
        }
      }
    }
    _self.groups = dataProvider;
    var items = [];
    _.each(_self.groups, function (group) {
      _.each(group.list, function (item) {
        items.push(item);
      });
    });
    _self.items = items;
  },
  methods: {
    getDataProvider: function () {
      var _self = this;
      if (_self.dataProvider != null) {
        return _self.dataProvider;
      }
      var optionSet = _self.fieldDefinition.optionSet || [];
      _self.dataProvider = _self.convertGroupSelectOptionSet(optionSet); //dyformCommons.getOptionData(_self.dyformField);
      return _self.dataProvider;
    },
    convertGroupSelectOptionSet: function (optionSet) {
      var groupMap = {};
      var dataList = [];
      _.each(optionSet, function (item) {
        item.text = item.name;
        if (groupMap[item.group]) {
          var list = groupMap[item.group];
          list.push(item);
        } else {
          groupMap[item.group] = [item];
          var groupData = {
            group: item.group,
            list: groupMap[item.group],
          };
          dataList.push(groupData);
        }
      });
      return dataList;
    },
    onSelectFocus: function (event) {
      console.log(event);
      // open 方法传入参数 等同在 uni-popup 组件上绑定 type属性
      this.$refs.popup.open("bottom");
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
      // 单选关闭弹出框
      _self.onOk();
    },
    onOk: function () {
      var _self = this;
      _self.$refs.popup.close("bottom");
      var items = _self.items;
      var checkedValues = [];
      for (var i = 0; i < items.length; i++) {
        const item = items[i];
        if (item.checked === true) {
          checkedValues.push(item.value);
        }
      }
      // _self.formData[_self.fieldDefinition.name] = checkedValues.join(";");
      _self.$set(_self.formData, _self.fieldDefinition.name, checkedValues.join(";"));
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
.w-group-select {
  .uni-list-cell {
    justify-content: flex-start;
    background-color: $uni-bg-secondary-color;
  }

  .popup-group-select {
    background-color: $uni-bg-secondary-color;
    height: 300px;

    .popup-title {
      /* #ifndef APP-NVUE */
      display: flex;
      /* #endif */
      flex-direction: row;
      align-items: center;
      justify-content: center;
      height: 40px;
      border-bottom: 1px solid $uni-border-3;
    }

    .popup-title-text {
      font-size: 16px;
      color: $uni-text-color;
      font-weight: bold;
    }

    .popup-content {
      /* #ifndef APP-NVUE */
      display: flex;
      /* #endif */
      flex-direction: row;
      justify-content: center;
      // padding-top: 10px;
      background-color: $uni-bg-color;

      ::v-deep .uni-collapse {
        background-color: $uni-bg-color;
      }
      ::v-deep .uni-collapse-item__title-box {
        background-color: $uni-bg-color;
        color: $uni-text-color;
      }

      .popup-content-scroll-view {
        height: 200px;
      }

      .hide-confirm-btn {
        height: 250px;
      }

      .popup-check-item {
        display: flex;
        flex-direction: row;
        width: 100%;
        justify-content: center;
        align-items: center;
        margin-left: 15px;

        .popup-check-icon {
          width: 20px;
          padding-right: 6px;
        }
      }
    }

    .popup-button-box {
      /* #ifndef APP-NVUE */
      display: flex;
      /* #endif */
      flex-direction: row;
      padding: 10px 15px;

      .popup-button {
        flex: 1;
        border-radius: 50px;
        background-color: $uni-primary;
        color: #fff;
        font-size: 16px;
      }

      .popup-button::after {
        border-radius: 50px;
      }
    }
  }
}
</style>
