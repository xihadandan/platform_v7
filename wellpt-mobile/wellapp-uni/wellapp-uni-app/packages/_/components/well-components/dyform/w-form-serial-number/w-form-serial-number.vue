<template>
  <uni-forms-item
    :label="itemLabel"
    :name="formModelItemProp"
    :label-position="widget.configuration.labelPosition"
    :ref="fieldCode"
    :class="widgetClass"
    :style="itemStyle"
  >
    <template v-if="!displayAsLabel">
      <view class="w-form-serial-container" @tap="onClick">
        <uni-w-easyinput
          v-show="!displayAsLabel"
          v-model="formData[fieldCode]"
          :disabled="disable || readonly"
          :placeholder="placeholder"
          :clearable="false"
          :inputBorder="widget.configuration.uniConfiguration ? widget.configuration.uniConfiguration.bordered : false"
          ref="serial"
          @blur="onBlur"
          @change="onChange"
        />
      </view>
      <!-- 流水号弹窗 -->
      <uni-popup ref="poppup" type="dialog" class="uni-w-popup">
        <uni-popup-dialog
          class="uni-w-popup-dialog"
          mode="input"
          :title="dialogTitle"
          :beforeClose="true"
          :confirmText="$t('global.confirm', '确定')"
          :cancelText="$t('global.cancel', '取消')"
          @close="closeDialog"
          @confirm="onChooseSerialNumberOk"
        >
          <view class="uni-flex uni-column">
            <text class="_label">{{ $t("WidgetFormSerialNumber.serialNumberName", "流水号名称") }}：</text>
            <view class="uni-flex uni-row">
              <uni-data-select
                :clear="false"
                :placeholder="$t('WidgetFormSerialNumber.selectDefinition', '请选择流水号定义！')"
                :localdata="formatDefinitionSelectOptions"
                v-model="serialNumberDefinitionSelectId"
                @change="onSerialNumberDefinitionSelectIdChange"
                class="serial-number-def-select"
                :emptyTips="$t('global.emptyTips', '无选项')"
              />
              <button
                type="default"
                v-if="widget.configuration.serialNumberFill == 'custom'"
                @click="onFillSerialNumberClick"
                class="serial-number-fill-button"
              >
                {{ $t("WidgetFormSerialNumber.fillNumber", "补号") }}
              </button>
            </view>
          </view>
          <view
            class="uni-flex uni-column"
            v-if="widget.configuration.serialNumberFill == 'custom' && serialNumberSelectOptions.length"
          >
            <text class="_label">{{ dialogSnSelectLabel }}：</text>
            <view class="uni-flex uni-row">
              <uni-data-select
                :placeholder="$t('WidgetFormSerialNumber.selectSerialNumber', '请选择流水号')"
                :clear="false"
                :localdata="formatserialNumberOptions"
                v-model="serialNumberSelectValue"
                class="serial-number-select"
              />
            </view>
          </view>
          <view class="uni-flex uni-column">
            <text class="_label">{{ dialogSnLabel }}：</text>
            <view class="uni-flex uni-row" style="align-items: center">
              <text class="_label" style="margin-right: 16px" v-show="serialNumberSelectRecord.prefix">{{
                serialNumberSelectRecord.prefix
              }}</text>
              <text class="_label" style="margin-right: 8px" v-show="pointerFormatValuePrefix">{{
                pointerFormatValuePrefix
              }}</text>

              <uni-easyinput v-model="serialNumberSelectInputPointer" :clearable="false" class="serial-number-select" />
              <text class="_label" style="margin-left: 8px" v-show="serialNumberSelectRecord.suffix">{{
                serialNumberSelectRecord.suffix
              }}</text>
            </view>
          </view>
        </uni-popup-dialog>
      </uni-popup>
    </template>
    <view v-else class="textonly">{{ displayValue() }} </view>
  </uni-forms-item>
</template>

<script>
import formElement from "../w-dyform/form-element.mixin";
import { isEmpty, map, each as forEach } from "lodash";
export default {
  mixins: [formElement],
  options: {
    styleIsolation: "shared",
  },
  data() {
    return {
      serialnumber: undefined,
      serialNumberModalVisible: false, // 流水号选择弹出框是否可见
      serialNumberDefinitionSelectOptions: [], // 流水号定义列表
      serialNumberDefinitionSelectId: "", // 流水号定义选择的ID
      serialNumberSelectOptions: [], // 流水号列表
      serialNumberSelectValue: "", // 流水号补号选择的记录指针
      serialNumberSelectRecord: { uuid: "", prefix: "", pointer: "", pointerFormatValue: "", subfix: "" }, //流水号补号选择的记录
      serialNumberSelectInputPointer: "",
    };
  },
  computed: {
    formatDefinitionSelectOptions() {
      return this.serialNumberDefinitionSelectOptions.map((item) => {
        return {
          text: item.label,
          value: item.value,
        };
      });
    },
    formatserialNumberOptions() {
      return this.serialNumberSelectOptions.map((item) => {
        return {
          text: item.label,
          value: item.value,
        };
      });
    },
    placeholder() {
      const _this = this;
      if (_this.isAutoGenerate) {
        return "";
      }
      return this.widget.configuration.placeholder
        ? this.$t("placeholder", this.widget.configuration.placeholder)
        : this.$t("WidgetFormSerialNumber.clickToEdit", "点击编辑流水号");
    },
    dialogTitle() {
      return this.widget.configuration.dialogTitle
        ? this.$t("placeholder", this.widget.configuration.dialogTitle)
        : this.$t("WidgetFormSerialNumber.editable", "可编辑流水号");
    },
    dialogSnDefSelectLabel() {
      return this.widget.configuration.dialogSnDefSelectLabel
        ? this.$t("dialogSnDefSelectLabel", this.widget.configuration.dialogSnDefSelectLabel)
        : this.$t("WidgetFormSerialNumber.serialNumberName", "流水号名称");
    },
    dialogSnSelectLabel() {
      return this.widget.configuration.dialogSnSelectLabel
        ? this.$t("dialogSnSelectLabel", this.widget.configuration.dialogSnSelectLabel)
        : this.$t("WidgetFormSerialNumber.selectable", "可选择流水号");
    },
    dialogSnLabel() {
      return this.widget.configuration.dialogSnLabel
        ? this.$t("dialogSnLabel", this.widget.configuration.dialogSnLabel)
        : this.$t("WidgetFormSerialNumber.serialNumber", "流水号");
    },
    // 指针格式化前缀
    pointerFormatValuePrefix() {
      const _this = this;
      let pointer = _this.serialNumberSelectRecord.pointer + "";
      let pointerFormatValue = _this.serialNumberSelectRecord.pointerFormatValue;
      if (isEmpty(pointer) || isEmpty(pointerFormatValue) || pointerFormatValue.indexOf(pointer) == -1) {
        return "";
      }
      return pointerFormatValue.substring(0, pointerFormatValue.indexOf(pointer));
    },
    // 是否自动生成
    isAutoGenerate() {
      return this.widget.configuration.generateMode == "auto";
    },
  },
  watch: {
    serialNumberSelectValue: function (newVal, oldVal) {
      var _this = this;
      var selectRecord = {};
      // 根据选择的指针值设置对应的记录
      forEach(_this.serialNumberRecordList, function (record) {
        if (record.pointer == _this.serialNumberSelectValue) {
          selectRecord = record;
          _this.serialNumberSelectInputPointer = record.pointer;
        }
      });
      _this.serialNumberSelectRecord = selectRecord;
    },
  },
  created() {
    const _this = this;
    if (!this.widget.configuration.hasOwnProperty("uniConfiguration")) {
      this.$set(this.widget.configuration, "uniConfiguration", { bordered: false });
    }
    let value = _this.getValue();
    if (isEmpty(value) && _this.isAutoGenerate) {
      _this.autoGenerateSerialNumberIfRequired();
    }
  },
  methods: {
    closeDialog() {
      this.$refs.poppup.close();
    },
    // 获取字段数据选项
    getFieldDataOptions() {
      const _this = this;
      let fieldName = _this.fieldCode;
      let formUuid = _this.form.formUuid;
      let dataUuid = this.formData.uuid;
      return {
        getOptions: function (key) {
          let realKey = formUuid + "." + dataUuid + "." + fieldName + "." + key;
          return _this.form.dyformDataOptions[realKey];
        },
        putOptions: function (key, value) {
          let realKey = formUuid + "." + dataUuid + "." + fieldName + "." + key;
          _this.form.dyformDataOptions[realKey] = value;
        },
      };
    },
    afterSaveDataError(params) {
      const data = params.res.data;
      if (data.fieldName === this.fieldCode) {
        const h = this.$createElement;
        uni.showModal({
          title: this.$t("WidgetFormSerialNumber.prompt", "提示"),
          content: h("div", {}, [
            h("p", `${this.$t("WidgetFormSerialNumber.fieldName", "字段名称")}：${data.displayName}`),
            h(
              "p",
              `${data.title}，${this.$t("WidgetFormSerialNumber.isResubmit", "是否重新提交")}` ||
                `${this.$t("WidgetFormSerialNumber.error", "报错失败")}`
            ),
          ]),
          confirmText: this.$t("global.confirm", "确认"),
          cancelText: this.$t("global.cancel", "取消"),
          success: (res) => {
            if (res.confirm) {
              this.formData[this.fieldCode] = data.newValue;
              let option = this.getFieldDataOptions().getOptions("serialNumberConfirm");
              option = JSON.parse(option);
              option.value = data.newValue;
              option.pointer = data.newValue.substr(-2);
              this.getFieldDataOptions().putOptions("serialNumberConfirm", option);
              params.callback();
            } else if (res.cancel) {
            }
          },
        });
      }
    },
    onBlur() {
      this.emitBlur();
    },
    onChange() {
      this.emitChange();
    },
    // 可编辑流水号点击
    onClick() {
      const _this = this;
      if (_this.isAutoGenerate) {
        return;
      }
      _this.serialNumberModalVisible = true;
      uni.hideKeyboard();
      this.$refs.poppup.open();
      // 加载流水号定义
      if (isEmpty(_this.serialNumberDefinitionSelectOptions)) {
        _this.loadSerialNumberDefinitionList();
      }
    },
    // 加载流水号定义列表
    loadSerialNumberDefinitionList() {
      const _this = this;
      this.$axios
        .post("/json/data/services", {
          serviceName: "snSerialNumberFacadeService",
          methodName: "listByCategoryUuidOrId",
          args: JSON.stringify([_this.widget.configuration.serialNumberDefinitionId]),
        })
        .then((result) => {
          _this.serialNumberDefinitionList = result.data.data;
          _this.serialNumberDefinitionSelectOptions = map(_this.serialNumberDefinitionList, function (definition) {
            return {
              label: definition.name,
              value: definition.id,
            };
          });
          if (this.serialNumberDefinitionSelectOptions.length == 1) {
            this.serialNumberDefinitionSelectId = this.serialNumberDefinitionSelectOptions[0]["value"];
            this.onSerialNumberDefinitionSelectIdChange();
          }
        });
    },
    // 流水号定义选择变更
    onSerialNumberDefinitionSelectIdChange() {
      const _this = this;
      // 无选择流水号定义，清空关联选择数据
      if (isEmpty(_this.serialNumberDefinitionSelectId)) {
        _this.serialNumberSelectOptions = [];
        _this.serialNumberSelectValue = "";
        _this.serialNumberSelectRecord = {};
        return;
      }
      let configuration = _this.widget.configuration;
      // 不补号
      if (configuration.serialNumberFill == "none") {
        _this.noFillSerialNumber();
      } else if (configuration.serialNumberFill == "auto" || configuration.serialNumberFill == "custom") {
        // 自动补号
        _this.autoFillSerialNumber();
      } else {
        // 点击补号按钮选择补号
      }
    },
    // 不补号
    noFillSerialNumber() {
      const _this = this;
      let params = _this.getSerialNumberBuildParams();
      params.serialNumberId = _this.serialNumberDefinitionSelectId;
      // 生成时占用
      params.occupied = _this.isAutoOccupy();
      this.$axios
        .post("/json/data/services", {
          serviceName: "snSerialNumberFacadeService",
          methodName: "generateSerialNumber",
          args: JSON.stringify([params]),
        })
        .then((result) => {
          var serialNumberInfo = result.data.data || {};
          _this.serialNumberRecordList = [serialNumberInfo];
          _this.serialNumberSelectRecord = serialNumberInfo;
          _this.serialNumberSelectInputPointer = serialNumberInfo.pointer;
        });
    },
    // 自动补号
    autoFillSerialNumber() {
      const _this = this;
      let params = _this.getSerialNumberBuildParams();
      params.serialNumberId = _this.serialNumberDefinitionSelectId;
      this.$axios
        .post("/json/data/services", {
          serviceName: "snSerialNumberFacadeService",
          methodName: "listAvailableSerialNumber",
          args: JSON.stringify([params]),
        })
        .then((result) => {
          var recordList = result.data.data;
          _this.serialNumberRecordList = recordList;
          // 自动补最小号
          if (!isEmpty(recordList)) {
            _this.serialNumberSelectRecord = recordList[0];
            _this.serialNumberSelectInputPointer = recordList[0].pointer;
          }
        });
    },
    // 补号
    onFillSerialNumberClick() {
      const _this = this;
      if (isEmpty(_this.serialNumberDefinitionSelectId)) {
        uni.showToast({
          icon: "error",
          title: this.$t("WidgetFormSerialNumber.selectDefinition", "请选择流水号定义！"),
        });
        return;
      }
      let params = _this.getSerialNumberBuildParams();
      params.serialNumberId = _this.serialNumberDefinitionSelectId;
      this.$axios
        .post("/json/data/services", {
          serviceName: "snSerialNumberFacadeService",
          methodName: "listAvailableSerialNumber",
          args: JSON.stringify([params]),
        })
        .then((result) => {
          _this.serialNumberRecordList = result.data.data;
          _this.serialNumberSelectOptions = map(_this.serialNumberRecordList, function (record) {
            return {
              label: record.serialNo,
              value: record.pointer,
            };
          });
          // 清空流水号
          _this.serialNumberSelectRecord = {};
          this.serialNumberSelectInputPointer = "";
        });
    },
    // 选择流水号
    onChooseSerialNumberOk() {
      const _this = this;
      let selectRecord = _this.serialNumberSelectRecord;
      if (isEmpty(selectRecord.serialNo)) {
        uni.showToast({ icon: "error", title: this.$t("WidgetFormSerialNumber.selectSerialNumber", "请选择流水号") });
        return;
      }

      // 判断用户是否更改指针
      if (_this.isUserCustomPointer()) {
        _this.userCustomSerialNumberOk();
      } else {
        _this.serialNumberOk();
      }
    },
    // 用户更改指针的补号
    userCustomSerialNumberOk() {
      const _this = this;
      let selectRecord = _this.serialNumberSelectRecord;
      var pointer = parseInt(_this.serialNumberSelectInputPointer);
      if (!pointer) {
        uni.showToast({ icon: "error", title: this.$t("WidgetFormSerialNumber.enterPointer", "请输入有效的指针!") });
        return;
      }

      let params = _this.getSerialNumberBuildParams();
      params.snValue = selectRecord.serialNo;
      params.uuid = selectRecord.maintainUuid;
      params.serialNumberId = _this.serialNumberDefinitionSelectId;
      this.$axios
        .post("/json/data/services", {
          serviceName: "snSerialNumberFacadeService",
          methodName: "generateSerialNumberWithPointer",
          args: JSON.stringify([pointer, params]),
        })
        .then((result) => {
          let serialNumberInfo = result.data.data;
          if (serialNumberInfo == null) {
            uni.showToast({
              icon: "error",
              title: this.$t("WidgetFormSerialNumber.pointerOccupied", "该指针无效或已经被占用!"),
            });
          } else {
            _this.setValue(serialNumberInfo.serialNo);
            let serialnumber = {
              snid: _this.serialNumberDefinitionSelectId, //流水号定义ID
              headPart: serialNumberInfo.prefix,
              pointer: serialNumberInfo.pointer,
              lastPart: serialNumberInfo.suffix,
              value: serialNumberInfo.serialNo,
              occupied: false,
              automaticNumberSupplement: params.automaticNumberSupplement,
              uuid: serialNumberInfo.maintainUuid || "", // 流水号维护UUID
              recordUuid: serialNumberInfo.recordUuid || "", // 流水号记录UUID，补号时传入对应的记录UUID
              occupiedPointer: pointer,
            };
            let dyformDataOptions = _this.getFieldDataOptions();
            _this.serialnumber = serialnumber;
            dyformDataOptions.putOptions("serialNumberConfirm", JSON.stringify(serialnumber));

            _this.serialNumberModalVisible = false;
            _this.$refs.poppup.close();
          }
        })
        .catch((error) => {
          if (error.response.data && error.response.data.msg) {
            uni.showToast({ icon: "error", title: error.response.data.msg });
          } else {
            uni.showToast({
              icon: "error",
              title: this.$t("WidgetFormSerialNumber.requestException", "服务请求异常！"),
            });
          }
        });
    },
    // 用户选择的补号
    serialNumberOk() {
      const _this = this;
      let configuration = _this.widget.configuration;
      let selectRecord = _this.serialNumberSelectRecord;
      let requiredRemoteCheck = true;
      // 生成时占用
      if (_this.isAutoOccupy()) {
        // 不补号
        if (configuration.serialNumberFill == "none") {
          requiredRemoteCheck = false;
        } else if (configuration.serialNumberFill == "auto") {
          // 自动补号
          requiredRemoteCheck = false;
        } else {
          // 点击补号按钮选择补号
        }
      } else {
        // 保存时占用
        // 不补号
        if (configuration.serialNumberFill == "none") {
          requiredRemoteCheck = true;
        } else if (configuration.serialNumberFill == "auto") {
          // 自动补号
          requiredRemoteCheck = true;
        } else {
          // 点击补号按钮选择补号
        }
      }

      var checkOk = function () {
        _this.setValue(selectRecord.serialNo);
        let serialnumber = {
          snid: _this.serialNumberDefinitionSelectId, //流水号定义ID
          headPart: selectRecord.prefix,
          pointer: selectRecord.pointer,
          lastPart: selectRecord.suffix,
          value: selectRecord.serialNo,
          occupied: false,
          automaticNumberSupplement: _this.getSerialNumberBuildParams().automaticNumberSupplement,
          uuid: selectRecord.maintainUuid || "", // 流水号维护UUID
          recordUuid: selectRecord.recordUuid || selectRecord.uuid || "", // 流水号记录UUID，补号时传入对应的记录UUID
          occupiedPointer: selectRecord.skip ? selectRecord.pointer : "", // 跳号记录，占用指针
        };
        let dyformDataOptions = _this.getFieldDataOptions();
        _this.serialnumber = serialnumber;
        dyformDataOptions.putOptions("serialNumberConfirm", JSON.stringify(serialnumber));

        _this.serialNumberModalVisible = false;
        _this.$refs.poppup.close();
      };

      // 保存时占用检查是否已经被占用
      if (requiredRemoteCheck) {
        let params = _this.getSerialNumberBuildParams();
        params.snValue = selectRecord.serialNo;
        params.uuid = selectRecord.maintainUuid;
        params.serialNumberId = _this.serialNumberDefinitionSelectId;
        this.$axios
          .post("/json/data/services", {
            serviceName: "snSerialNumberFacadeService",
            methodName: "checkIsOccupied",
            args: JSON.stringify([selectRecord.pointer, params]),
          })
          .then((result) => {
            let isOccupied = result.data.data;
            if (isOccupied == true) {
              uni.showToast({
                icon: "error",
                title: this.$t("WidgetFormSerialNumber.serialNumberOccupied", "该流水号已经被占用"),
              });
            } else {
              checkOk(false);
            }
          });
      } else {
        checkOk(true);
      }
    },
    isUserCustomPointer() {
      return this.serialNumberSelectRecord.pointer != this.serialNumberSelectInputPointer;
    },
    // 是否生成时占用
    isAutoOccupy() {
      // 不补号才有生成时占用，其他都为保存时占用
      return this.widget.configuration.serialNumberFill == "none" && this.widget.configuration.occupyMode == "auto";
    },
    // 生成流水号
    autoGenerateSerialNumberIfRequired() {
      const _this = this;
      let params = _this.getSerialNumberBuildParams();
      // 生成时占用
      params.occupied = _this.isAutoOccupy();
      if (this.widget.configuration.occupyMode == "auto") {
        params.occupied = true;
      }
      this.$axios
        .post("/json/data/services", {
          serviceName: "snSerialNumberFacadeService",
          methodName: "generateSerialNumber",
          args: JSON.stringify([params]),
        })
        .then((result) => {
          // 表单数据异步加载时有设置值不更新
          var value = _this.getValue();
          var serialNumberInfo = result.data.data || {};
          if (isEmpty(value)) {
            _this.setValue(serialNumberInfo.serialNo);

            let serialnumber = {
              snid: _this.widget.configuration.serialNumberDefinitionId, //流水号定义ID
              headPart: serialNumberInfo.prefix,
              pointer: serialNumberInfo.pointer,
              lastPart: serialNumberInfo.suffix,
              value: serialNumberInfo.serialNo,
              occupied: params.occupied,
              automaticNumberSupplement: params.automaticNumberSupplement,
              uuid: serialNumberInfo.maintainUuid || "", // 流水号维护UUID
              recordUuid: serialNumberInfo.recordUuid || "", // 占用的流水号记录UUID
            };
            let dyformDataOptions = _this.getFieldDataOptions();
            _this.serialnumber = serialnumber;
            dyformDataOptions.putOptions("serialNumberConfirm", JSON.stringify(serialnumber));
          }
        });
    },
    getSerialNumberBuildParams() {
      const _this = this;
      var configuration = _this.widget.configuration;
      return {
        serialNumberId: configuration.serialNumberDefinitionId,
        formUuid: _this.dyform.formUuid,
        dataUuid: _this.formData.uuid || "",
        formField: configuration.code,
        occupied: false,
        automaticNumberSupplement: configuration.serialNumberFill == "auto",
      };
    },
    setValue(value) {
      this.form.formData[this.fieldCode] = value;
      this.emitChange();
    },
  },
};
</script>

<style lang="scss">
.w-form-serial-container {
  width: 100%;

  .uni-easyinput__placeholder-class {
    color: var(--text-color-placeholder);
    font-size: var(--w-font-size-base);
    font-weight: 200;
  }
}
::v-deep .uni-w-popup {
  .uni-popup__wrapper {
    display: flex;
    justify-content: center;
    width: 100%;
    .uni-w-popup-dialog {
      width: 100%;
      margin: 0 20px;
    }
    .uni-dialog-title {
      justify-content: normal;
      padding-top: 10px;
      padding-left: 20px;
      .uni-popup__info {
        color: var(--text-color);
      }
    }
    .uni-dialog-content {
      flex-direction: column;
      align-items: normal;
      padding: 0 20px 20px;
      .uni-column {
        margin-top: 10px;
        > ._label {
          padding-bottom: 5px;
        }
      }
      ._label {
        color: var(--text-color-label);
      }
    }

    .serial-number-def-select {
      flex: 1;
      margin-right: 7px;
      .uni-select__input-box {
        text-align: left !important;
      }
    }
    .serial-number-select {
      .uni-select__input-box,
      .uni-easyinput__content-input {
        text-align: left !important;
      }
    }
    .serial-number-fill-button {
      width: 56px;
      height: 35px;
      background-color: #fff;
      margin-left: initial;
      margin-right: initial;
      color: #333;
      font-size: 14px;
    }
  }
}
</style>
