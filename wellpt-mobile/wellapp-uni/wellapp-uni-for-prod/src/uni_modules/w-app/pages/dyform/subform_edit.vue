<template>
  <view :style="theme" class="subform-edit">
    <view class="subform-content">
      <w-dyform-field
        v-for="(field, index) in fields"
        :key="index"
        :fieldDefinition="field"
        :formScope="formScope"
      ></w-dyform-field>
    </view>
    <view v-show="!displayAsLabel" class="org-dialog-button-group">
      <view class="org-dialog-button" @tap="onCancel">
        <text class="org-dialog-button-text">取消</text>
      </view>
      <view class="org-dialog-button uni-border-left" @tap="onOk">
        <text class="org-dialog-button-text org-button-color">确定</text>
      </view>
    </view>
  </view>
</template>

<script>
import { each } from "lodash";
export default {
  data() {
    return {
      formScope: null,
      fields: [],
      record: null,
      originalRecordJson: "",
      subform: null,
      displayAsLabel: false,
    };
  },
  onLoad() {
    var _self = this;
    var record = _self.getPageParameter("record");
    var subformDefinition = _self.getPageParameter("subformDefinition");
    var formScope = _self.getPageParameter("formScope");
    var subform = _self.getPageParameter("subform");
    // console.log(formScope);
    _self.originalRecordJson = JSON.stringify(record);
    // 初始化表单对象
    _self.formScope = formScope;
    _self.fields = subformDefinition.fieldList || subformDefinition.fields;
    _self.record = record;
    _self.subform = subform;
    _self.displayAsLabel = formScope.isDisplayAsLabel();
  },
  onBackPress() {
    var _self = this;
    // 返回处理，取消时恢复数据
    if (!_self.isOk) {
      this.restoreRecord();
    }
  },
  methods: {
    restoreRecord() {
      var _self = this;
      if (_self.originalRecordJson) {
        var originalRecord = JSON.parse(_self.originalRecordJson);
        each(_self.fields, function (field) {
          _self.record[field.name] = originalRecord[field.name];
        });
      }
    },
    setUpdatedData() {
      var _self = this;
      if (_self.originalRecordJson) {
        // var originalRecord = JSON.parse(_self.originalRecordJson);
        _self.subform.setUpdatedData(_self.record);
      }
    },
    onCancel() {
      var _self = this;
      // 返回取消更改
      _self.restoreRecord();
      // 清空生成的字段
      _self.subform.clearFields();
      uni.navigateBack({
        delta: 1,
      });
    },
    onOk() {
      var _self = this;
      // 验证
      var fieldErrors = _self.subform.validate();
      if (fieldErrors.length > 0) {
        return;
      }

      _self.isOk = true;
      // 设置更新的数据
      _self.setUpdatedData();

      // 清空生成的字段
      _self.subform.clearFields();
      uni.navigateBack({
        delta: 1,
      });
    },
  },
};
</script>

<style lang="scss" scoped>
.subform-edit {
  width: 100%;
  background-color: $uni-bg-color;

  .subform-content {
    padding: 15px;
  }

  .org-dialog-button-group {
    position: fixed;
    width: 100%;
    bottom: var(--window-bottom, 0);
    background-color: $uni-bg-color;

    /* #ifndef APP-NVUE */
    display: flex;
    /* #endif */
    flex-direction: row;
    border-top-color: #f5f5f5;
    border-top-style: solid;
    border-top-width: 1px;

    .org-dialog-button {
      /* #ifndef APP-NVUE */
      display: flex;
      /* #endif */

      box-shadow: $uni-shadow-base;

      flex: 1;
      flex-direction: row;
      justify-content: center;
      align-items: center;
      height: 40px;
    }

    .org-border-left {
      border-left-color: #f0f0f0;
      border-left-style: solid;
      border-left-width: 1px;
    }

    .org-dialog-button-text {
      font-size: 16px;
      color: #333;
    }

    .org-button-color {
      color: #007aff;
    }
  }
}
</style>
