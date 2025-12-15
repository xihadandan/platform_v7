<template>
  <view class="dyform">
    <view>
      <uni-forms
        v-if="renderReady"
        :model="dyform.formData"
        ref="form"
        :label-position="labelPosition"
        :label-width="labelWidth"
      >
        <!-- 新版本表单设计器定义 -->
        <template>
          <view v-for="(widget, key) in formDefinition.widgets" :key="key" :index="key">
            <widget :widget="widget" />
            <!-- <w-widget :widget="widget"> </w-widget> -->
            <!-- 						<widget: widget="widget"> </widget> -->
          </view>
        </template>
      </uni-forms>
    </view>

    <!-- 表单验证 -->
    <uni-popup ref="validationErrorPopup" background-color="#ffffff" borderRadius="16px 16px 0 0">
      <view class="pt-popop-title">
        <view class="left"></view>
        <view class="center">
          <text>{{ $t("widgetDyform.validateFormData", "表单数据校验") }}</text>
        </view>
        <view class="right">
          <uni-w-button
            type="text"
            @click="closeValidationErrorPopup"
            icon="iconfont icon-ptkj-dacha-xiao"
          ></uni-w-button>
        </view>
      </view>
      <view class="validation-error-popup-content">
        <scroll-view style="height: 100%; max-height: 300px" scroll-y="true">
          <uni-list class="uni-list">
            <uni-list-item
              v-for="(error, index) in validationResult.errors"
              :key="index"
              :title="error.name"
              :rightText="error.msg"
              clickable
              @click="focusWidget($event, error)"
            />
          </uni-list>
        </scroll-view>
      </view>
    </uni-popup>
    <u-notify ref="uNotify">
      <template slot="icon">
        <view style="color: #fff; position: relative; width: 100%" @click="onErrorFabClick">
          校验失败
          <uni-icons
            type="closeempty"
            size="18"
            color="#fff"
            style="position: absolute; right: 0px"
            @click.native.stop="closeErrorNotify"
          ></uni-icons>
        </view>
      </template>
    </u-notify>
  </view>
</template>

<script>
import Dyform from "./uni-Dyform.js";
import { createDyform } from "./dyform.js";
import FormScope from "./uni-DyformScope.js";
import { isEmpty, debounce, set, isFunction } from "lodash";
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
      refDyforms: this.refDyforms,
      namespace: this.formUuid || "DYFORM",
    };
  },
  components: {},
  computed: {
    renderReady() {
      return this.formDefinition != null && this.formDataFetched;
    },
  },
  options: {
    styleIsolation: "shared",
  },
  data() {
    let dyform = null;
    if (this.options.inheritForm) {
      dyform = this.options.inheritForm;
    } else {
      dyform = createDyform();
      dyform.vueInstance = this;
    }
    if (!dyform.vueInstance) {
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
      labelWidth: "75px",
      msgType: "info",
      messageText: this.$t("widgetDyform.validateFailed", "数据校验不通过"),
      errorPattern: {
        color: "#7A7E83",
        backgroundColor: "#fff",
        selectedColor: "var(--w-primary-color)",
        buttonColor: "#FF9933",
        iconColor: "#FF9933",
      },
      scrollTop: 0.01,
      refDyforms: [],
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
    if (options.labelWidth != undefined) {
      _self.labelWidth = options.labelWidth;
    }

    if (_self.formDefinition) {
      if (typeof _self.formDefinition == "string") {
        _self.formDefinition = JSON.parse(_self.formDefinition);
        _self.createPageJsInstance(_self.formDefinition.jsModule);
        _self.invokeDevelopmentMethod("created");
      } else if (_self.formDefinition.widgets) {
        _self.createPageJsInstance(_self.formDefinition.jsModule);
        _self.invokeDevelopmentMethod("created");
      }
      this.dyform.tableName = _self.formDefinition.tableName;
      this.dyformTitleContent = _self.formDefinition.titleContent;
      _self.prepareRefDyform(_self.formDefinition.refDyform);

      if (options.formData && options.formData.i18ns) {
        this.setI18nMessage(options.formData.i18ns);
      }
      _self.lazyLoadFormDefinition = false;
      _self.fetchFormData();
    } else if (this.formUuid) {
      this.fetchingDefinition = true;
      this.fetchFormDefinition(this.formUuid).then((def) => {
        let vjson = JSON.parse(def.definitionVjson);
        this.formDefinition = {
          widgets: vjson.widgets,
          jsModule: vjson.jsModule,
        };
        this.dyform.tableName = def.tableName;
        this.dyformTitleContent = vjson.titleContent;
        this.prepareRefDyform(vjson.refDyform);

        this.setI18nMessage(def.i18ns);

        if (vjson.jsModule) {
          this.createPageJsInstance(vjson.jsModule);
        }
        this.fetchingDefinition = false;

        // 从表表单编辑保存后，再次打开不再对从表表单数据进行获取
        if (
          this.isSubform &&
          this.options.formDatas &&
          this.options.formDatas[this.formUuid] &&
          this.options.formDatas[this.formUuid][0].$$subDyformEdit
        ) {
          return false;
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
    setTimeout(() => {
      this.$emit(EVENTS.initSuccess, this);
    }, 1000);

    if (this.lazyLoadFormDefinition === false) {
      this.$emit("mounted");
      this.invokeDevelopmentMethod("mounted");
    }
  },

  updated() {
    if (this.fetchingDefinition === false) {
      delete this.fetchingDefinition;
      this.$emit("mounted");
      setTimeout(() => {
        this.invokeDevelopmentMethod("mounted");
      }, 500);
    }
  },

  methods: {
    createRefDyformHolder(id, config) {
      if (this.refDyformHolder == undefined) {
        this.refDyformHolder = {};
      }
      let _this = this;
      class Holder {
        constructor(id, config) {
          this.id = id;
          this.config = config;

          /**
           * 引用表单数据会在主表单数据域下以嵌套形式存在 :
           * { ... 主表单属性值 , __REF_FORM_DATA__ : { 引用表单ID : { 引用表单属性值 } } }
           * 这么处理是为了能够通过表单域校验，避免与主表单属性名冲突的情况
           */
          if (_this.dyform.formData.__REF_FORM_DATA__ == undefined) {
            _this.$set(_this.dyform.formData, "__REF_FORM_DATA__", {});
          }
          _this.$set(_this.dyform.formData.__REF_FORM_DATA__, this.id, {});
          this.form = _this.dyform.createRefDyForm(this.id, undefined);
          this.form.namespace = "ref-dyform-" + this.id;
          this.form.formData = _this.dyform.formData.__REF_FORM_DATA__[this.id];
          this.loadRefDyformDefinition().then(() => {
            _this.refDyforms.push(this.form);
            this.dependencyWatch();
            if (_this.formDatas || this.dependencyCodes.length == Object.keys(this.dependencyData).length) {
              this.setFormDatas();
            }
          });
        }
        setFormDatas() {
          return new Promise((resolve, reject) => {
            let condition = this.config.condition,
              params = {};
            if (condition != undefined) {
              for (let i = 0, len = condition.length; i < len; i++) {
                if (
                  condition[i].valueType == "prop" &&
                  condition[i].value &&
                  condition[i].value.startsWith(":MAIN_FORM_DATA_")
                ) {
                  let code = condition[i].value.split(":MAIN_FORM_DATA_")[1];
                  if (this.dependencyData[code] != undefined && this.dependencyData[code] != "") {
                    params[condition[i].value.substring(1)] = this.dependencyData[code];
                  }
                }
              }
            }
            // 存在通过条件查询关联表单字段数据的，则发起后端条件查询
            if (
              this.dependencyCodes.length == 0 ||
              (Object.keys(params).length == this.dependencyCodes.length && this.dependencyCodes.length > 0)
            ) {
              if (JSON.stringify(this.tempNamedParams) != JSON.stringify(params)) {
                this.tempNamedParams = params;
                this.getFormDataByWhere(condition, params).then((list) => {
                  if (list && list.length == 1) {
                    _this.originFormData[list[0].uuid] = JSON.parse(JSON.stringify(list[0]));
                    for (const key in list[0]) {
                      _this.$set(this.form.formData, key, list[0][key]);
                      this.form.setFieldValue(key, list[0][key], false);
                    }
                  } else {
                    for (let key in this.form.formData) {
                      _this.$set(this.form.formData, key, undefined);
                      this.form.setFieldValue(key, undefined, false);
                    }
                  }
                  resolve();
                });
              }
            } else if (this.dependencyCodes.length > 0) {
              this.tempNamedParams = {};
              for (let key in this.form.formData) {
                this.form.setFieldValue(key, undefined, false);
              }
              resolve();
            }
          });
        }

        getFormDataByWhere(where, namedParams = {}) {
          return new Promise((resolve, reject) => {
            _this.$axios
              .post(`/api/dyform/data/getFormDataByWhere/${this.id}`, {
                where,
                namedParams: { ..._this.namedParams, ...namedParams },
              })
              .then(({ data }) => {
                resolve(data.data);
              })
              .catch((error) => {});
          });
        }
        loadRefDyformDefinition() {
          return new Promise((resolve, reject) => {
            _this.fetchFormDefinition(undefined, this.id).then((def) => {
              this.form.formId = this.id;
              this.form.displayState = this.config.displayState;
              this.form.formUuid = def.uuid;
              this.form.formDefinitionJson = JSON.parse(def.definitionVjson);
              this.form.version = this.form.formDefinitionJson.version || "1.0";
              _this.setI18nMessage(def.i18ns);
              resolve();
            });
          });
        }
        dependencyWatch() {
          let condition = this.config.condition;
          this.dependencyData = {};
          if (condition != undefined) {
            this.dependencyCodes = [];
            for (let i = 0, len = condition.length; i < len; i++) {
              if (
                condition[i].valueType == "prop" &&
                condition[i].value &&
                condition[i].value.startsWith(":MAIN_FORM_DATA_")
              ) {
                let code = condition[i].value.split(":MAIN_FORM_DATA_")[1];
                if (!_this.dyform.formData.hasOwnProperty(code)) {
                  _this.$set(_this.dyform.formData, code, undefined);
                } else if (
                  _this.dyform.formData[code] != undefined &&
                  _this.dyform.formData[code] != null &&
                  _this.dyform.formData[code] != ""
                ) {
                  this.dependencyData[code] = _this.dyform.formData[code];
                }
                if (!this.dependencyCodes.includes(code)) {
                  this.dependencyCodes.push(code);
                }
              }
            }
            _this.$watch(
              () => {
                let props = [];
                for (let c of this.dependencyCodes) {
                  props.push(_this.dyform.formData[c]);
                }
                return props;
              },
              (newValues, oldValues) => {
                for (let i = 0, len = newValues.length; i < len; i++) {
                  this.dependencyData[this.dependencyCodes[i]] = newValues[i];
                }
                this.setFormDatas();
              },
              { deep: true }
            );
          }
        }
      }
      this.refDyformHolder[id] = new Holder(id, config);
    },
    prepareRefDyform(refDyform) {
      if (refDyform) {
        this.refDyformConfig = refDyform;
        for (let id in refDyform) {
          if (Object.keys(refDyform[id].field).length) {
            this.createRefDyformHolder(id, refDyform[id]);
          }
        }
      }
    },
    fetchFormDefinition(formUuid, formId) {
      return new Promise((resolve, reject) => {
        this.$axios
          .post(
            `/api/dyform/definition/${
              formUuid ? "getFormDefinitionByUuid?formUuid=" + formUuid : "getFormDefinitionById?id=" + formId
            }&i18n=true`,
            {}
          )
          .then(({ data }) => {
            if (data.code == 403 || data.code == -7000) {
              uni.showModal({
                content: this.$t("global.noPermission", "您无权限访问该页面，请联系管理员！"),
                showCancel: false,
                confirmText: this.$t("global.confirm", "确认"),
                success: function (res) {
                  if (res.confirm) {
                    uni.navigateBack({
                      delta: 1,
                    });
                  }
                },
              });
            } else if (data.errorCode === "SessionExpired") {
              uni.setStorageSync("loginSuccessRedirect", INDEX_PAGE_PATH);
              uni.reLaunch({
                url: LOGIN_PAGE_PATH,
              });
            } else {
              resolve(data);
            }
          })
          .catch((error) => {
            if (error && error.response.status == "403") {
              let { fullPath: loginSuccessRedirect } = getCurrentPages()[getCurrentPages().length - 1].__page__;
              uni.setStorageSync("loginSuccessRedirect", loginSuccessRedirect);
              uni.reLaunch({
                url: LOGIN_PAGE_PATH,
              });
            }
          });
      });
    },
    createPageJsInstance(jsModule) {
      // 页面二开脚本实例
      if (jsModule) {
        this.$pageJsInstance = framework.appContext.jsInstance(jsModule, this);
        if (this.$pageJsInstance) {
          this._provided.$pageJsInstance = this.$pageJsInstance;
          this._provided.$pageJsInstance._JS_META_ = jsModule;
          this.$developJsInstance[jsModule] = this.$pageJsInstance;
        }
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
          // 表单校验需要从表数据按结构嵌套
          this.$set(this.dyform.formData, key, this.originFormData);
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
    invokeDevelopmentMethod() {
      for (let k in this.$developJsInstance) {
        let method = arguments[0],
          args = [];
        if (arguments.length > 1) {
          for (let i = 1, len = arguments.length; i < len; i++) {
            args.push(arguments[i]);
          }
        }
        if (typeof this.$developJsInstance[k][method] === "function") {
          try {
            this.$developJsInstance[k][method].apply(this.$developJsInstance[k], args);
          } catch (error) {
            console.error("调用二开脚本方法失败：", error);
          }
        }
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

          // 移除主表数据里面的从表数据
          for (let subkey in this.dyform.subform) {
            delete formData[subkey];
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
      let _this = this;
      this.dyform.validate((vali, msg) => {
        let subformValidatePromise = [],
          suid = [];
        if (_this.dyform.$subform) {
          for (let k in _this.dyform.$subform) {
            let sf = _this.dyform.$subform[k];
            if (!suid.includes(sf._uid)) {
              subformValidatePromise.push(sf.validateSubform());
              suid.push(sf._uid);
            }
          }
        }
        let _continue = () => {
          if (!vali) {
            _this.dyform.showValidationErrors(false, msg);
          } else {
            _this.dyform.hideValidationErrors();
          }
          if (typeof callback == "function") {
            console.error("表单校验消息", msg);
            callback(vali, msg);
          }
        };
        if (subformValidatePromise.length) {
          Promise.all(subformValidatePromise)
            .then((res) => {
              if (res) {
                res.forEach((r) => {
                  if (r && r.validate === false) {
                    if (!msg) {
                      msg = [];
                    }
                    msg = msg.concat(r.msg);
                    vali = false;
                  }
                });
              }
              _continue();
            })
            .catch((err) => {
              console.error(err);
            });
        } else {
          _continue();
        }
      });
      // this.$refs.form
      //   .validate()
      //   .then((res) => {
      //     this.dyform.hideValidationErrors();
      //     console.info("表单验证通过", res);
      //     if (typeof callback == "function") {
      //       callback(true, undefined);
      //     }
      //   })
      //   .catch((err) => {
      //     this.dyform.showValidationErrors(false, err);
      //     console.error("表单验证失败", err);
      //   });
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

            // if (this.originFormData[formData.uuid]) {
            //   let _dyformData = {
            //     formUuid: uuid,
            //     formDatas: {},
            //     deletedFormDatas: {},
            //     addedFormDatas: {},
            //     updatedFormDatas: {},
            //   };

            //   this.collectSubFormData(_dyformData, dyforms[i]);
            //   formData.nestformDatas = JSON.stringify(_dyformData);
            // }
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
      uni.$off("showValidationErrors" + this.formUuid);
      uni.$on("showValidationErrors" + this.formUuid, (res) => {
        _self.validationResult.errors = res;
        this.$refs.uNotify.show({
          type: "error",
          duration: 0,
        });
        this.$refs.validationErrorPopup.open("bottom");
      });

      uni.$off("hideValidationErrors" + this.formUuid);
      uni.$on("hideValidationErrors" + this.formUuid, () => {
        _self.validationResult = {
          errors: [],
        };
      });

      uni.$off("anchorWidgetInContent");
      uni.$on("anchorWidgetInContent", (res) => {
        if (res.id) {
          this.focusWidget(undefined, res);
        }
      });
    },
    // 跳转到锚点
    focusWidget: function (event, widget) {
      let _self = this;
      const query = uni.createSelectorQuery().in(this);
      query
        .select(".widget_" + widget.id)
        .boundingClientRect((data) => {
          if (data) {
            _self.getDyformRect(data.top);
          }
        })
        .exec();
      this.closeValidationErrorPopup();
    },
    getDyformRect(top) {
      let _self = this;
      const query = uni.createSelectorQuery().in(this);
      query
        .select(".uni-forms")
        .boundingClientRect((res) => {
          if (res) {
            let toTop = top - res.top - 50;
            _self.scrollTop = toTop; // - Math.random() * 10;
            console.log(toTop);
            uni.pageScrollTo({
              scrollTop: _self.scrollTop, // 滚动到1000px位置
              duration: 300, // 动画时长（ms）
            });
          }
        })
        .exec();
    },
    showValidationErrors(msg) {
      this.validationResult.errors = msg;
      this.onErrorFabClick();
    },
    hideValidationErrors() {
      this.validationResult = {
        errors: [],
      };
      this.closeValidationErrorPopup();
    },
    closeValidationErrorPopup() {
      this.$refs.validationErrorPopup.close();
    },
    closeErrorNotify() {
      this.$refs.uNotify.close();
    },
    onErrorFabClick: function () {
      this.$refs.validationErrorPopup.open("bottom");
    },
    afterFormDataChanged: debounce(function (v) {
      this.$emit("formDataChanged", true);
      this.dyform.emitEvent("formDataChanged", {
        wDyform: this,
      });
    }, 500),
    dyformDataOptions: function (_this) {
      var self = this;
      _this.dyform.dyformDataOptions = _this.dyform.dyformDataOptions || {};
      return {
        getOptions: function (key) {
          return _this.dyformDataOptions[key];
        },
        putOptions: function (key, value) {
          if (null == value) {
            delete _this.dyform.dyformDataOptions[key];
          } else {
            _this.dyform.dyformDataOptions[key] = value;
          }
        },
      };
    },
    handleException(exceptionData, options) {
      var subcode = exceptionData.subcode;
      if (subcode === "SerialNumberOccupy") {
        var dyformDataOptions =
          (options.dyformDataOptions && options.dyformDataOptions()) || this.dyformDataOptions(this);
        uni.showModal({
          content: exceptionData.title,
          showCancel: false,
          confirmText: this.$t("global.confirm", "确认"),
          success: function (res) {
            dyformDataOptions.putOptions("serialNumberConfirmed-" + exceptionData.fieldName, true);
            isFunction(options.callback) && options.callback.call();
          },
        });
      } else {
        console.error("表单数据保存失败: ", exceptionData);
        try {
          if (exceptionData.msg) {
            uni.showModal({
              content: exceptionData.msg,
              showCancel: false,
              confirmText: this.$t("global.confirm", "确认"),
              success: function (res) {},
            });
          }
        } catch (error) {
          uni.showModal({
            content: this.$t("widgetDyform.message.saveFailed", "表单数据保存失败！"),
            showCancel: false,
            confirmText: this.$t("global.confirm", "确认"),
            success: function (res) {},
          });
        }
      }
    },
    /**
     * 处理异常
     */
    handleError(exceptionData, options) {
      let self = this;

      function errorFun(message) {
        uni.showModal({
          content: message || this.$t("widgetDyform.message.saveFailed", "表单数据保存失败！"),
          showCancel: false,
          confirmText: this.$t("global.confirm", "确认"),
          success: function (res) {},
        });
      }

      try {
        let msg = JSON.parse(exceptionData);
        if (msg.errorCode == "SQLGRAM") {
          errorFun(
            this.$t(
              "widgetDyform.message.syntaxError",
              "保存时,后台语法错误!!!有可能是人为去修改了表单后台数据库表字段,更详细的信息如下:"
            ) +
              "\n" +
              msg.msg
          );
        } else if (msg.errorCode == "DATA_OUT_OF_DATE") {
          errorFun(this.$t("widgetDyform.message.toReload", "请重新加载并修改数据:") + "\n" + msg.msg);
        } else if (msg.errorCode == "SaveData") {
          self.handleException(msg.data, options);
        } else {
          if (msg.msg) {
            errorFun(msg.msg);
          } else {
            errorFun();
          }
        }
      } catch (e) {
        errorFun();
      }
    },
    setI18nMessage(i18ns) {
      let widgetI18ns = undefined;
      if (i18ns && i18ns.length) {
        widgetI18ns = {
          [this.$i18n.locale]: {
            Widget: {},
          },
        };
        for (let item of i18ns) {
          if (item.locale == this.$i18n.locale) {
            set(widgetI18ns[this.$i18n.locale].Widget, item.code, item.content);
          }
        }
        this.$i18n.mergeLocaleMessage(this.$i18n.locale, widgetI18ns[this.$i18n.locale]);
      }
    },
    $t() {
      return this.$i18n.$t(this, ...arguments);
    },
  },

  watch: {
    renderReady: {
      handler(v) {
        if (v) {
          uni.hideLoading();
          this.$nextTick(() => {
            this.dyform.$form = this.$refs.form; // 表单实例
            this.dyform.$validationErrorPopup = this.$refs.validationErrorPopup; // 表单实例
          });
        }
      },
    },
    "dyform.formData": {
      deep: true,
      handler(v, o) {
        // console.log("dyform.formData", v);
        this.afterFormDataChanged(v);
      },
    },
  },
};
</script>

<style lang="scss" scoped>
.dyform {
  // height: 100vh;

  .dyform-scroll-view {
    overflow: hidden;
    height: 100%;
  }

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
}

//   ::v-deep {
//     // .label-text {
//     //   font-size: 13px;
//     //   color: $uni-text-color;
//     // }
//     // .uni-forms-item__label .label-text {
//     //   font-size: 13px;
//     //   color: $uni-text-color;
//     // }
//     // .uni-easyinput .uni-input-placeholder {
//     //   color: $uni-text-color-placeholder;
//     // }
//     // .uni-easyinput .uni-textarea-placeholder {
//     //   color: $uni-text-color-placeholder;
//     // }
//     // .uni-easyinput .uni-easyinput__content {
//     //   color: $uni-text-color;
//     // }
//     .field-collapse-item-label {
//       color: $uni-text-color-label;
//     }

//     .uni-forms-item__content uni-label {
//       color: $uni-text-color;
//     }

//     .uni-forms-item__inner {
//       // border-bottom: 1px solid $uni-border-1;
//     }

//     .w-file-upload {
//       .field-collapse {
//         border-bottom: 1px solid $uni-border-1;
//       }
//     }

//     .w-rich-editor {
//       border-bottom: 1px solid $uni-border-1;

//       .ql-editor {
//         color: $uni-text-color;
//       }
//       .ql-blank {
//         color: $uni-text-color-placeholder;
//       }
//     }

//     .w-date-picker {
//       .uni-date-x {
//         background-color: $uni-bg-secondary-color;
//         color: $uni-text-color;
//       }
//     }

//     // 禁用软键盘弹起的处理
//     .w-select,
//     .w-group-select,
//     .w-dialog,
//     .w-unit {
//       .is-disabled {
//         background-color: var(--bg-disable-color);
//         color: var(--text-color-disable);
//       }
//     }

//     .error-fab {
//       top: 65px;
//       /* #ifdef H5 */
//       top: calc(65px + var(--window-top));
//       /* #endif */
//     }

//     .validation-error-popup-title {
//       background-color: $uni-bg-secondary-color;
//       text-align: center;
//       color: $uni-text-color;
//       font-size: var(--w-font-size-lg);
//       border-radius: 8px 8px 0 0;
//       line-height: 40px;
//     }

//     .validation-error-popup-content {
//       max-height: 300px;
//       background-color: $uni-bg-secondary-color;

//       .uni-list-item {
//         background-color: $uni-bg-secondary-color;

//         .uni-list-item__content-title,
//         .uni-list-item__extra-text {
//           color: $uni-text-color;
//         }
//       }
//     }
//   }
// }
</style>
