<template>
  <uni-forms v-if="renderReady" class="dyform" :model="dyform.formData" ref="form" :label-position="labelPosition">
    <!-- 新版本表单设计器定义 -->
    <template>
      <view v-for="(widget, key) in formDefinition.widgets" :key="key" :index="key">
        <w-widget :widget="widget"> </w-widget>
      </view>
    </template>

    <!-- 表单验证 -->
    <uni-popup ref="validationErrorPopup">
      <view class="validation-error-popup-content">
        <scroll-view style="height: 100%" scroll-y="true">
          <uni-list class="uni-list">
            <uni-list-item
              v-for="(error, index) in validationResult.errors"
              :key="index"
              :title="error.displayName"
              :rightText="error.message"
              clickable
              @click="errorItemClick($event, error)"
            />
          </uni-list>
        </scroll-view>
      </view>
    </uni-popup>
    <uni-fab
      v-if="validationResult.errors.length > 0"
      class="error-fab"
      :pattern="errorPattern"
      horizontal="right"
      vertical="top"
      direction="horizontal"
      @fabClick="onErrorFabClick"
    ></uni-fab>

    <!-- 提示信息弹窗 -->
    <!-- <uni-popup ref="message" type="message">
      <uni-popup-message :type="msgType" :message="messageText" :duration="2000"></uni-popup-message>
    </uni-popup> -->
  </uni-forms>
</template>

<script>
const _ = require("lodash");
import Dyform from "./uni-Dyform.js";
import { createDyform } from "./dyform.js";
import FormScope from "./uni-DyformScope.js";
import { isEmpty, debounce } from "lodash";
const framework = require("wellapp-uni-framework");
const EVENTS = {
  initSuccess: "initSuccess",
};

export default {
  props: {
    options: {
      type: Object,
      required: true,
    },
  },
  provide() {
    return {
      dyform: this.dyform,
      widgetDyformContext: this,
      namespace: this.formUuid || "DYFORM",
    };
  },
  components: {
    "w-widget": () => import("../w-widget/w-widget.vue"),
  },
  computed: {
    renderReady() {
      return this.formDefinition != null && this.formDataFetched;
    },
  },

  data() {
    let dyform = null;
    if (this.options.inheritForm) {
      dyform = this.options.inheritForm;
    } else {
      dyform = createDyform();
      dyform.vueInstance = this;
    }

    return {
      formDefinition: null,
      dyform,
      validationResult: {
        errors: [],
      },
      originFormData: {},
      formDatas: null,
      loading: true,
      isSubform: false,
      isNewFormData: false,
      formDataFetched: false,
      dataUuid: null,
      formUuid: null,
      ready: false,
      labelPosition: "left",
      msgType: "info",
      messageText: "数据校验不通过",
      errorPattern: {
        color: "#7A7E83",
        backgroundColor: "#fff",
        selectedColor: "#007AFF",
        buttonColor: "#FF9933",
        iconColor: "#FF9933",
      },
    };
  },
  created() {
    uni.showLoading();
    this.$developJsInstance = {};
    var _self = this;
    var options = _self.options;
    _self.dyform.displayState = options.displayState || "edit";
    if (options.formUuid) {
      _self.dyform.formUuid = options.formUuid;
      _self.formUuid = options.formUuid;
    }
    if (options.formElementRules) {
      _self.dyform.formElementRules = options.formElementRules;
    }
    if (options.dataUuid) {
      _self.dataUuid = options.dataUuid;
      _self.dyform.dataUuid = _self.dataUuid;
    }
    if (options.formDatas) {
      _self.setFormData(options.formDatas);
    }
    if (options.isNewFormData != undefined) {
      // 是否新增表单数据，后端表单数据自动生成的临时表单数据
      _self.isNewFormData = options.isNewFormData;
    }
    _self.dyform.isNewFormData = _self.isNewFormData;

    if (options.isSubform != undefined) {
      _self.isSubform = options.isSubform;
    }

    if (options.formDefinition != undefined) {
      _self.formDefinition = options.formDefinition;
    }

    if (options.labelPosition != undefined) {
      _self.labelPosition = options.labelPosition;
    }

    if (_self.formDefinition) {
      if (typeof _self.formDefinition == "string") {
        _self.formDefinition = JSON.parse(_self.formDefinition);
        _self.createPageJsInstance(_self.formDefinition.jsModule);
      } else if (_self.formDefinition.widgets) {
        _self.createPageJsInstance(_self.formDefinition.jsModule);
      }
      this.dyform.tableName = _self.formDefinition.tableName;
      this.dyformTitleContent = _self.formDefinition.titleContent;
      _self.fetchFormData();
    } else if (this.formUuid) {
      this.fetchFormDefinition(this.formUuid).then((def) => {
        let vjson = JSON.parse(def.definitionVjson);
        this.formDefinition = {
          widgets: vjson.widgets,
          jsModule: vjson.jsModule,
        };
        this.dyform.tableName = def.tableName;
        this.dyformTitleContent = vjson.titleContent;

        if (vjson.jsModule) {
          this.createPageJsInstance(vjson.jsModule);
        }
        this.fetchFormData();
      });
    }
    // 绑定事件
    _self.bindEvents();
  },
  mounted() {
    this.fetchFormDefaultValueData();
    // #ifdef H5
    // 测试需要
    window.wDyform = this;
    // #endif
  },

  methods: {
    fetchFormDefinition(formUuid, formId) {
      return new Promise((resolve, reject) => {
        this.$axios
          .post(
            `/api/dyform/definition/${
              formUuid ? "getFormDefinitionByUuid?formUuid=" + formUuid : "getFormDefinitionById?id=" + formId
            }`,
            {}
          )
          .then(({ data }) => {
            resolve(data);
          })
          .catch((error) => {});
      });
    },
    createPageJsInstance(jsModule) {
      // 页面二开脚本实例
      if (jsModule) {
        this.$pageJsInstance = framework.appContext.jsInstance(jsModule);
        this._provided.$pageJsInstance = this.$pageJsInstance;
        this._provided.$pageJsInstance._JS_META_ = jsModule;
        this.$developJsInstance[jsModule] = this.$pageJsInstance;
      }
    },
    fetchFormDefaultValueData() {
      let _this = this;
      if ((this.dataUuid == null || this.isNewFormData) && this.formUuid) {
        this.$axios
          .post(`/api/dyform/data/getDefaultFormData?formUuid=${this.formUuid}`, {})
          .then(({ data, headers }) => {
            // resolve(data.code == 0 && data.data && Object.keys(data.data).length ? data.data : {});
            if (data.code == 0 && data.data && Object.keys(data.data).length) {
              if (_this.dataUuid) {
                data.data.uuid = this.dataUuid;
              }
              _this.setFormData({
                [_this.formUuid]: [data.data],
              });
            }
          });
      }
    },
    setFormData(formDatas, setOriginalData = true) {
      for (let key in formDatas) {
        if (key === this.formUuid) {
          // 主表数据
          if (setOriginalData) {
            this.originFormData[formDatas[key][0].uuid] = formDatas[key][0];
          }
          let data = formDatas[key][0];
          const dataTemp = framework.utils.deepClone(data);
          for (const key in dataTemp) {
            this.$set(this.dyform.formData, key, dataTemp[key]);
          }
          for (let k in data) {
            if (k.toLowerCase() === "uuid") {
              // 设置数据UUID
              this.dataUuid = data[k];
            }
            if (data[k] != null) {
              this.dyform.setFieldValue(k, data[k], false);
            }
          }
        } else {
          let rowData = framework.utils.deepClone(formDatas[key]);
          if (setOriginalData) {
            this.originFormData[key] = rowData; // 从表数据
          }

          // 初始化传入表单数据情况下，需等待从表加载完成
          // this.$nextTick(() => {
          //   this.dyform.emitEvent(`WidgetSubform:${key}:addRow`, rowData);
          // });
          if (setOriginalData) {
            for (let i = 0, len = formDatas[key].length; i < len; i++) {
              this.originFormData[formDatas[key][i].uuid] = formDatas[key][i];
            }
          }
        }
      }
      this.loading = false;
      this.formDataFetched = true;
      this.$emit("formDataChanged", true);
    },
    fetchFormData() {
      let _this = this;
      if (this.dataUuid != null && this.formDatas == null) {
        this.$axios
          .post("/json/data/services", {
            serviceName: "dyFormFacade",
            methodName: "getFormData",
            args: JSON.stringify([this.formUuid, this.dataUuid]),
          })
          .then(({ data }) => {
            console.log(`表单数据[${_this.dataUuid}] : `, data);
            // _this.loading = false;
            if (data.code == 0 && data.data) {
              _this.originFormData = {}; // 原始数据
              _this.setFormData(data.data);
            }
          });
      } else {
        this.formDataFetched = true;
      }
    },

    collectFormData(validate, callback) {
      var collect = function () {
        let data = {
          formUuid: this.formUuid,
          dataUuid: this.dataUuid,
          dyFormData: {
            formUuid: this.formUuid,
            formDatas: {},
            deletedFormDatas: {},
            addedFormDatas: {},
            updatedFormDatas: {},
            dyformDataOptions: this.dyform.dyformDataOptions || {},
          },
        };
        let formData = framework.utils.deepClone(this.dyform.formData);
        if (formData.__REF_FORM_DATA__ != undefined) {
          delete formData.__REF_FORM_DATA__;
        }
        if (this.dataUuid == null && !this.isSubform) {
          formData.uuid = framework.utils.generateId();
          data.dataUuid = formData.uuid;
          data.dyFormData.addedFormDatas[this.formUuid] = [formData.uuid];
        } else if (this.isNewFormData) {
          // 后端新增的表单数据
          data.dyFormData.addedFormDatas[this.formUuid] = [formData.uuid];
        }
        //主表数据
        data.dyFormData.formDatas[this.formUuid] = [formData];
        this.compareOriginFormData(data.dyFormData.updatedFormDatas, formData, this.formUuid, this.dataUuid);

        // 从表数据
        if (this.dyform.subform) {
          let dyFormData = data.dyFormData;
          if (this.isSubform) {
            // 从表嵌套从表的情况下
            dyFormData = {
              formUuid: this.formUuid,
              formDatas: {},
              deletedFormDatas: {},
              addedFormDatas: {},
              updatedFormDatas: {},
            };
          }

          this.collectSubFormData(dyFormData, this.dyform);

          if (this.isSubform) {
            // 从表嵌套从表的情况下
            formData.nestformDatas = JSON.stringify(dyFormData);
          }
        }

        // 引用表数据
        if (this.dyform.refDyform && Object.keys(this.dyform.refDyform).length) {
          for (let key in this.dyform.refDyform) {
            let refDyform = this.dyform.refDyform[key];
            this.compareOriginFormData(
              data.dyFormData.updatedFormDatas,
              refDyform.formData,
              refDyform.formUuid,
              refDyform.formData.uuid
            );
            if (
              data.dyFormData.updatedFormDatas[refDyform.formUuid] &&
              data.dyFormData.updatedFormDatas[refDyform.formUuid][refDyform.formData.uuid].length
            ) {
              data.dyFormData.formDatas[refDyform.formUuid] = [refDyform.formData];
            }
          }
        }
        console.log("收集表单数据: ", data);
        return data;
      };
      if (validate) {
        // 校验
        let _this = this;
        this.validateFormData(function (vali, msg) {
          callback(vali, msg, collect.call(_this));
        });
        return;
      }
      return collect.call(this);
    },
    validateFormData(callback) {
      this.$refs.form
        .validate()
        .then((res) => {
          console.info("表单验证通过", res);
          if (typeof callback == "function") {
            callback(true, undefined);
          }
        })
        .catch((err) => {
          console.error("表单验证失败", err);
        });
    },
    collectSubFormData(dyFormData, dyform) {
      if (dyform.subform) {
        if (dyform.deletedSubformData) {
          Object.assign(dyFormData.deletedFormDatas, dyform.deletedSubformData);
        }

        for (let uuid in dyform.subform) {
          let dyforms = dyform.subform[uuid];
          dyFormData.formDatas[uuid] = [];
          dyFormData.addedFormDatas[uuid] = [];
          for (let i = 0, len = dyforms.length; i < len; i++) {
            let formData = framework.utils.deepClone(dyforms[i].formData);
            formData.sort_order = `${i + 1}`; // 序号
            // 前端或后端添加的从表临时数据
            if (!formData.uuid || this.isNewFormData) {
              // 新增的数据
              formData.uuid = formData.uuid || framework.utils.generateId();
              dyFormData.addedFormDatas[uuid].push(formData.uuid);
            } else {
              this.compareOriginFormData(dyFormData.updatedFormDatas, formData, uuid, formData.uuid);
            }
            dyFormData.formDatas[uuid].push(formData);

            let _dyformData = {
              formUuid: uuid,
              formDatas: {},
              deletedFormDatas: {},
              addedFormDatas: {},
              updatedFormDatas: {},
            };

            this.collectSubFormData(_dyformData, dyforms[i]);
            formData.nestformDatas = JSON.stringify(_dyformData);
          }
        }
      }
    },
    compareOriginFormData(updatedFormDatas, newFormData, formUuid, dataUuid) {
      if (!isEmpty(this.originFormData) && dataUuid) {
        let originData = this.originFormData[dataUuid];
        if (!originData) {
          return;
        }
        if (updatedFormDatas[formUuid] === undefined) {
          updatedFormDatas[formUuid] = {};
        }
        if (updatedFormDatas[formUuid][dataUuid] === undefined) {
          updatedFormDatas[formUuid][dataUuid] = [];
        }
        let newKeys = Object.keys(newFormData);
        for (let key in originData) {
          if (originData[key] != newFormData[key]) {
            updatedFormDatas[formUuid][dataUuid].push(key);
          }
          if (newKeys.includes(key)) {
            newKeys.splice(newKeys.indexOf(key), 1);
          }
        }
        if (newKeys.length) {
          updatedFormDatas[formUuid][dataUuid].push(...newKeys);
        }
      }
    },
    bindEvents: function () {
      var _self = this;
      uni.$off("showValidationErrors");
      uni.$on("showValidationErrors", (res) => {
        _self.validationResult = res;
        this.$refs.validationErrorPopup.open("bottom");
      });

      uni.$off("hideValidationErrors");
      uni.$on("hideValidationErrors", () => {
        _self.validationResult = { errors: [] };
      });
    },
    errorItemClick: function (event, error) {
      const query = uni.createSelectorQuery().in(this);
      query
        .select(".field-" + error.field.getName())
        .boundingClientRect((data) => {
          if (data) {
            uni.pageScrollTo({ scrollTop: data.top });
          }
        })
        .exec();
      // dom.scrollToElement
      this.$refs.validationErrorPopup.close();
    },
    onErrorFabClick: function () {
      this.$refs.validationErrorPopup.open("bottom");
    },
    afterFormDataChanged: debounce(function (v) {
      this.$emit("formDataChanged", true);
      this.dyform.emitEvent("formDataChanged", { wDyform: this });
    }, 500),
  },

  watch: {
    renderReady: {
      handler(v) {
        if (v) {
          uni.hideLoading();
        }
      },
    },
    "dyform.formData": {
      deep: true,
      handler(v, o) {
        this.afterFormDataChanged(v);
      },
    },
  },
};
</script>

<style lang="scss" scoped>
.dyform {
  padding: 15px;
  background-color: $uni-bg-color;

  .tabs {
    flex: 1;
    flex-direction: column;
    overflow: hidden;
    background-color: $uni-bg-color;
    /* #ifndef APP-PLUS */
    height: 100vh;
    /* #endif */

    .scroll-h {
      width: 750rpx;
      /* #ifdef H5 */
      width: 100%;
      /* #endif */
      height: 80rpx;
      white-space: nowrap;
      position: absolute;
      background-color: $uni-bg-color;
      z-index: 1;
      border-bottom: 1px solid #cccccc;
    }

    .line-h {
      height: 1rpx;
      // background-color: #cccccc;
      margin-top: 40px;
    }

    .uni-tab-item {
      display: inline-block;
      flex-wrap: nowrap;
      padding-left: 34rpx;
      padding-right: 34rpx;
    }

    .uni-tab-item-icon {
      margin-right: 4px;
    }

    .uni-tab-item-icon-active {
      color: $uni-icon-color !important;
    }

    .uni-tab-item-title {
      color: #555;
      font-size: 30rpx;
      height: 80rpx;
      line-height: 80rpx;
      flex-wrap: nowrap;
      /* #ifndef APP-PLUS */
      white-space: nowrap;
      /* #endif */
    }

    .uni-tab-item-title-active {
      color: $uni-color-primary; //#007aff;
    }

    .swiper-box {
      flex: 1;
    }

    .swiper-item {
      flex: 1;
      flex-direction: row;
    }
  }

  ::v-deep {
    .label-text {
      font-size: 13px;
      color: $uni-text-color;
    }
    .uni-forms-item__label .label-text {
      font-size: 13px;
      color: $uni-text-color;
    }
    .uni-easyinput .uni-input-placeholder {
      color: $uni-text-color-placeholder;
    }
    .uni-easyinput .uni-textarea-placeholder {
      color: $uni-text-color-placeholder;
    }
    .uni-easyinput .uni-easyinput__content {
      color: $uni-text-color;
    }
    .field-collapse-item-label {
      color: $uni-text-color-label;
    }

    .uni-forms-item__content uni-label {
      color: $uni-text-color;
    }

    .uni-forms-item__inner {
      // border-bottom: 1px solid $uni-border-1;
    }

    .w-file-upload {
      .field-collapse {
        border-bottom: 1px solid $uni-border-1;
      }
    }

    .w-rich-editor {
      border-bottom: 1px solid $uni-border-1;

      .ql-editor {
        color: $uni-text-color;
      }
      .ql-blank {
        color: $uni-text-color-placeholder;
      }
    }

    .w-date-picker {
      .uni-date-x {
        background-color: $uni-bg-secondary-color;
        color: $uni-text-color;
      }
    }

    // 禁用软键盘弹起的处理
    .w-select,
    .w-group-select,
    .w-dialog,
    .w-unit {
      .is-disabled {
        background-color: $uni-bg-secondary-color !important;
        color: $uni-text-color;
      }
    }

    .error-fab {
      top: 65px;
      /* #ifdef H5 */
      top: calc(65px + var(--window-top));
      /* #endif */
    }

    .validation-error-popup-content {
      height: 300px;
      background-color: $uni-bg-secondary-color;

      .uni-list-item {
        background-color: $uni-bg-secondary-color;

        .uni-list-item__content-title,
        .uni-list-item__extra-text {
          color: $uni-text-color;
        }
      }
    }
  }
}
</style>
