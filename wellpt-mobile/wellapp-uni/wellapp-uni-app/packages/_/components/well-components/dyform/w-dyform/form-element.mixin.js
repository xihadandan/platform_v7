"use strict";
import pageWidgetMixin from "../../page/page-widget-mixin.js";
import { kebabCase, map, template, cloneDeep, debounce, findIndex } from "lodash";
import { utils } from "wellapp-uni-framework";
import * as formulaApi from "./formula-api.js";

export default {
  inject: ["dyform", , "pageContext", "widgetDyformContext", "widgetSubformContext"],
  mixins: [pageWidgetMixin],
  props: {
    formObject: {
      type: Object,
    },
    formDataProp: {
      // 默认为form对象内的表单数据模型
      type: Object,
    },
    formModelItem: {
      type: String | Array,
    },
    isSubformCell: Boolean,
  },
  computed: {
    _vShowByData() {
      return this.formData;
    },
    itemLabel() {
      // 在表单列的不展示字段名称，或者从表单元格内需要隐藏字段名
      if ((this.parent != undefined && this.parent.wtype === "WidgetFormItem") || this.isSubformCell) {
        // || labelHidden
        return undefined;
      }

      return this.widget.configuration.fieldNameVisible
        ? this.$t(this.widget.id, this.widget.configuration.name)
        : null;
    },
    itemStyle() {
      let style = {};
      // if (!this.itemLabel) {
      //   style['vertical-align'] = 'bottom';
      // }

      if (!this.designMode) {
        if (this.hidden) {
          style.display = "none";
        }
        // 宽度100%时，会导致不在表单布局的组件超出屏幕区域，先注释
        // if (!this.hidden && this.widget.configuration.style) {
        //   style.display = "inline-flex";
        //   style.width =
        //     typeof this.widget.configuration.style.width === "number"
        //       ? this.widget.configuration.style.width + "px"
        //       : this.widget.configuration.style.width || "100%";
        // }
      }
      return style;
    },
    widgetClass() {
      let className = [];
      if (this.isMounted && this.$el && this.$el.nodeName !== "#comment") {
        let wClass = kebabCase(this.widget.wtype);
        className.push("widget");
        className.push("widget_" + this.widget.id);
        className.push(`${wClass}`);
        if (this.widget.subtype) {
          wClass = kebabCase(this.widget.subtype);
          className.push(wClass);
        }
        if (this.widget.configuration.isDatabaseField) {
          if (this.widget.configuration.labelPosition == "top") {
            className.push("label-top");
          }
        }
      }
      return className;
    },

    // 相关方法暴露为事件，提供外部调用
    supperDefaultEvents() {
      return [
        {
          id: "setVisible",
          title: "设置为显示或者隐藏",
          eventParams: [
            {
              paramKey: "visible",
              remark: "是否显示",
              valueScope: (() => {
                return ["true", "false"];
              })(),
            },
          ],
        },

        {
          id: "setEditable",
          title: "设置为可编辑或者不可编辑",
          eventParams: [
            {
              paramKey: "editable",
              remark: "是否可编辑",
              valueScope: (() => {
                return ["true", "false"];
              })(),
            },
          ],
        },
        {
          id: "setValueByEventParams",
          title: "设值",
          eventParams: [
            {
              paramKey: "value",
              remark: "值",
            },
          ],
        },
        {
          id: "setRequired",
          title: "设置为必填或者非必填",
          eventParams: [
            {
              paramKey: "required",
              remark: "是否必填",
              valueScope: (() => {
                return ["true", "false"];
              })(),
            },
          ],
        },
        {
          id: "clearValidate",
          title: "清空字段校验信息",
        },
      ];
    },
    calculateExpression() {
      let expression = [];
      if (this.widget.configuration.formula != undefined && this.widget.configuration.formula.enable) {
        this.widget.configuration.formula.items.forEach((i) => {
          expression.push(i.value);
        });
      }
      return expression.join("");
    },
    relaFormulaFields() {
      let rela = {
        field: [],
      };
      if (
        this.widget.configuration.formula != undefined &&
        this.widget.configuration.formula.enable &&
        this.widget.configuration.formula.items.length > 0
      ) {
        this.widget.configuration.formula.items.forEach((i) => {
          if (i.dataIndex != undefined) {
            if (Array.isArray(i.dataIndex)) {
              rela.field.push(...i.dataIndex);
            } else {
              rela.field.push(i.dataIndex);
            }
          }
        });
      }
      return rela;
    },
    formulaValueDataMD5() {
      if (this.calculateExpression != "") {
        let data = { ...this.formulaApiData };
        if (this.formData != undefined && this.relaFormulaFields.field.length > 0) {
          // 根据依赖字段的数据md5值, 用于判断发生变更
          for (let k in this.formData) {
            if (this.relaFormulaFields.field.includes(k) && k != this.widget.configuration.code) {
              data[k] = this.formData[k];
            }
          }
        }
        let str = utils.md5(JSON.stringify(JSON.stringify(data)));
        console.log(this.widget.configuration.code, "formulaValueDataMD5", str, this.relaFormulaFields, data);
        return str;
      }
      return "-1";
    },
    formRuleTrigger() {
      let validateRule = this.widget.configuration.validateRule;
      if (validateRule && validateRule.trigger.length) {
        return validateRule.trigger;
      }
      return [];
    },
  },

  data() {
    let form = this.formObject || this.dyform;
    let data = {
      form,
      fieldCode: this.widget.configuration.code,
      // 表单字段组件的展示状态初始化以表单为主
      editable: form.displayState == undefined || form.displayState == "edit",
      displayAsLabel: form.displayState == "label", // 初始化展示为不可编辑的文本展示
      readonly: false,
      disable: false,
      hidden: this.widget.configuration.hidden === true,
      hiddenByRule: undefined,
      required: this.widget.configuration.required || false,
      customRules: [], // 组件自定义规则
      isMounted: false,
      formModelItemProp: this.formModelItem,
      formData: this.formDataProp,
      rules: [],
    };

    // 针对字段型、交互型组件存在默认的组件状态
    if (this.widget.configuration.hasOwnProperty("defaultDisplayState")) {
      data.hidden = this.widget.configuration.defaultDisplayState === "hidden";
      if (data.editable) {
        // 初始可编辑的前提下，组件是否可编辑，要需要有默认决定
        data.editable = this.widget.configuration.defaultDisplayState === "edit";
        data.displayAsLabel = this.widget.configuration.defaultDisplayState === "unedit";
      }
    }

    // 从表内组件，默认不可编辑
    if (this.isSubformCell) {
      data.editable = false;
    }

    // 不可编辑情况下，要判断组件是否只读展示、或者文本展示
    if (!data.editable && this.widget.configuration.hasOwnProperty("uneditableDisplayState")) {
      data.readonly = this.widget.configuration.uneditableDisplayState === "readonly";
      data.displayAsLabel = this.widget.configuration.uneditableDisplayState === "label";
    }

    // 表单元素的规则修改
    if (form.formElementRules && form.formElementRules[this.widget.id]) {
      let rule = form.formElementRules[this.widget.id];
      if (rule.hidden != undefined) {
        data.hidden = rule.hidden;
        data.hiddenByRule = rule.hidden;
        if (rule.hidden) {
          // 隐藏情况下，非必填
          data.required = false;
        }
      }
      if (rule.editable !== undefined) {
        data.editable = rule.editable;
      }

      // 从表内组件，默认不可编辑
      if (this.isSubformCell) {
        data.editable = false;
      }

      if (data.editable) {
        data.displayAsLabel = false;
      }
      if (rule.required != undefined) {
        // 如果有传递必填性规则，按传入的为准（可能存在隐藏情况下，业务上还是需要校验必填性
        data.required = rule.required;
      }
      if (!data.hidden) {
        // 非隐藏情况下，需要决定组件的展示形态
        if (
          data.editable === false &&
          !!data.hidden &&
          rule.readonly === undefined &&
          rule.displayAsLabel === undefined &&
          this.widget.configuration.hasOwnProperty("uneditableDisplayState")
        ) {
          // 不可编辑情况下的可见，要根据组件配置进行展示
          data.readonly = this.widget.configuration.uneditableDisplayState === "readonly";
          data.displayAsLabel = this.widget.configuration.uneditableDisplayState === "label";
        }

        // 支持直接传readonly 、 displayAsLabel 来决定组件在不可编辑(可见)情况下的展示方式
        if (rule.readonly != undefined) {
          data.readonly = rule.readonly;
        }
        if (rule.displayAsLabel != undefined) {
          data.displayAsLabel = rule.displayAsLabel;
        }
        if (rule.disable != undefined) {
          data.disable = rule.disable;
        }
        if (!data.editable && this.widget.configuration.hasOwnProperty("uneditableDisplayState")) {
          data.readonly = this.widget.configuration.uneditableDisplayState === "readonly";
          data.displayAsLabel = this.widget.configuration.uneditableDisplayState === "label";
        }
      }
    }

    data.formulaApiData = this.initFormulaApiData();

    return data;
  },

  created() {
    this.emitChange = debounce(this.emitChange.bind(this), 300);
    this.triggerOnChange = debounce(this.triggerOnChange.bind(this), 300);
    this.triggerOnValueChange = debounce(this.triggerOnValueChange.bind(this), 300);
    this.setFormulaCalculateFieldValue = debounce(this.setFormulaCalculateFieldValue.bind(this), 300);

    if (this.form == undefined) {
      this.form = this.dyform;
    }
    if (this.formData == undefined) {
      this.formData = this.form.formData;
    }
    if (this.formModelItemProp == undefined) {
      this.formModelItemProp = this.widget.configuration != undefined ? this.widget.configuration.code + "" : undefined;
    }
    if (this.widget.configuration.isDatabaseField) {
      // 初始化字段属性，双向绑定生效
      if (this.fieldCode && !this.form.formData.hasOwnProperty(this.fieldCode)) {
        this.$set(this.form.formData, this.fieldCode, undefined);
      }
      if (this.$attrs.stopEvent) {
        return;
      }

      if (this.form.$fieldset == undefined) {
        // 该种方式设值，可以使得对象不被监听
        this.form.$fieldset = {};
      }
      if (this.fieldCode) {
        this.form.$fieldset[this.fieldCode] = this;
      }
    }
  },

  methods: {
    /**
     * 重写翻译方法：主要针对组件实例上的国际化信息进行解析，支持返回默认值，针对未做国际化的相关配置
     * @returns
     */
    $t() {
      let params = {},
        defaultValue = undefined;
      if (arguments.length > 1) {
        if (arguments[1] != undefined) {
          if (typeof arguments[1] !== "string") {
            // 参数化翻译
            params = arguments[1];
          } else {
            // 默认值
            defaultValue = arguments[1];
          }
        }
        if (arguments[2] != undefined && typeof arguments[2] == "string") {
          // 默认值
          defaultValue = arguments[2];
        }
      }
      let wKey = `Widget.${this.widget.id}.${arguments[0]}`;
      if (this.$te(wKey)) {
        // 组件内的国际化信息，按照国际化信息嵌套路径优先解析: Widget.组件ID.key
        return this.$i18n.t.apply(this.$i18n, [wKey, params]);
      } else if (this.$i18n._root && this.$i18n._root.$te(wKey)) {
        // 从顶级国际化信息获取
        return this.$i18n._root.$t.apply(this.$i18n._root, [wKey, params]);
      } else if (this.$te(arguments[0])) {
        return this.$i18n.t.apply(this.$i18n, [arguments[0], params]);
      } else if (this.$i18n._root && this.$i18n._root.$te(arguments[0])) {
        // 从顶级国际化信息获取
        return this.$i18n._root.$t.apply(this.$i18n._root, [arguments[0], params]);
      } else if (
        this.$i18n.fallbackLocale &&
        this.$i18n._te(arguments[0], this.$i18n.fallbackLocale, this.$i18n.messages)
      ) {
        return this.$i18n._t(arguments[0], this.$i18n.fallbackLocale, this.$i18n.messages, params);
      }
      return defaultValue || "";
    },
    getCurrentState() {
      return {
        hidden: this.hidden,
        editable: this.editable,
        displayAsLabel: this.displayAsLabel,
        disable: this.disable,
        readonly: this.readonly,
      };
    },
    setVisible(visible = true) {
      if (typeof visible !== "boolean" && visible.eventParams != undefined) {
        // 由事件传递进来的参数
        visible = visible.eventParams.visible !== "false";
      }
      let oldState = this.getCurrentState();
      // FIXME: 显示切换是否要更新是否编辑状态

      // this.editable = this.form.displayState == 'edit';
      // this.displayAsLabel = this.form.displayState == 'label';

      this.hidden = !visible;
      if (!this.hidden && this.form.formElementRules && this.form.formElementRules[this.widget.id]) {
        let rule = this.form.formElementRules[this.widget.id];
        // 存在规则不可编辑情况下，以规则为主，显示出来还是不可编辑情况
        if (rule.editable != undefined) {
          this.editable = rule.editable;
        }
      }
      // 不可编辑情况下，要判断组件是否只读展示、或者文本展示
      if (!this.editable && this.widget.configuration.hasOwnProperty("uneditableDisplayState")) {
        this.readonly = this.widget.configuration.uneditableDisplayState === "readonly";
        this.displayAsLabel = this.widget.configuration.uneditableDisplayState === "label";
      }

      this.hiddenByRule = undefined;
      this.emitVisibleChange(!this.hidden);
      this.afterDisplayStateChanged(oldState, this.getCurrentState());
    },
    // 设置该组件只读状态
    setReadonly(readonly = true) {
      let oldState = this.getCurrentState();
      this.readonly = readonly;
      this.editable = !readonly;
      this.disable = false;
      this.displayAsLabel = false;
      this.afterDisplayStateChanged(oldState, this.getCurrentState());
    },
    // 可编辑
    setEditable(editable = true) {
      if (typeof editable !== "boolean" && editable.eventParams != undefined) {
        // 由事件传递进来的参数
        editable = editable.eventParams.editable !== "false";
      }
      this.hidden = false;
      this.hiddenByRule = undefined;
      let oldState = this.getCurrentState();
      this.editable = editable;
      this.displayAsLabel = !editable; // 默认情况下，不可编辑就是展示为文本
      this.readonly = false;
      this.disable = false;
      // 不可编辑情况下，如果组件配置存在不可编辑的展示状态设值，应该按设值的状态显示
      if (!this.editable && this.widget.configuration.hasOwnProperty("uneditableDisplayState")) {
        this.readonly = this.widget.configuration.uneditableDisplayState === "readonly";
        this.displayAsLabel = this.widget.configuration.uneditableDisplayState === "label";
      }

      this.afterDisplayStateChanged(oldState, this.getCurrentState());
    },

    // 禁用
    setDisable(disable = true) {
      let oldState = this.getCurrentState();
      this.disable = disable;
      this.editable = !disable;
      this.readonly = false;
      this.displayAsLabel = false;
      this.afterDisplayStateChanged(oldState, this.getCurrentState());
    },

    setDisplayAsLabel(asLabel = true) {
      let oldState = this.getCurrentState();
      this.displayAsLabel = asLabel;
      this.editable = !asLabel;
      this.readonly = false;
      this.disable = false;
      this.afterDisplayStateChanged(oldState, this.getCurrentState());
    },
    createWidgetValidateRules(widget, isRequired) {
      let _this = this,
        required = isRequired != undefined ? isRequired : this.required,
        validateRule = widget.configuration.validateRule;
      if (validateRule == undefined) {
        return [];
      }

      // 未渲染的字段组件，判断是否也要强制校验规则
      if (this.isFormModelItemHiddenField && validateRule.validateWhenFieldIsHidden !== true) {
        return [];
      }

      let rules = [],
        trigger = validateRule ? validateRule.trigger : "blur",
        type = widget.configuration.dataType || "string";
      if (required) {
        // 必填规则
        rules.push({
          required: true,
          errorMessage: widget.configuration.requiredMsg
            ? this.$t(`validateRuleRequiredMessage`, widget.configuration.requiredMsg)
            : this.$t(`widgetDyform.required`, `必填`),
          validateFunction: function (rule, v, data, callback) {
            let value = _this.getValue();
            let result = !(value == undefined || value == null || value === "") ? undefined : new Error(rule.message);
            if (_this.hidden && validateRule.validateWhenFieldIsHidden !== true) {
              result = undefined;
            }
            _this.$emit(
              "validateStatusChange",
              result != undefined ? "error" : undefined,
              result != undefined ? rule.errorMessage : undefined
            );
            return result == undefined;
          },
        });
      }

      if (validateRule) {
        // 正则规则
        let regExpStr = validateRule.regExp.value;
        if (regExpStr) {
          let start = regExpStr.indexOf("/"),
            end = regExpStr.lastIndexOf("/"),
            regExp,
            errorMsg = validateRule.errorMsg
              ? this.$t(`validateRuleDataFormateError`, validateRule.errorMsg)
              : this.$t(`widgetDyform.formatError`, `数据格式错误`);

          if (start === 0) {
            regExp =
              end == regExpStr.length - 1
                ? new RegExp(regExpStr.substring(1, end))
                : new RegExp(regExpStr.substring(1, end), regExpStr.substr(end + 1));
          } else {
            regExp = new RegExp(regExpStr);
          }
          rules.push({
            validateFunction: function (rule, v, data, callback) {
              let result = true;
              let value = _this.getValue();
              if (value) {
                if (_this.hidden && validateRule.validateWhenFieldIsHidden !== true) {
                  result = true;
                } else {
                  result = regExp.test(value);
                  regExp.lastIndex = 0;
                }
              }
              _this.$emit(
                "validateStatusChange",
                result === false ? "error" : undefined,
                result === false ? errorMsg : undefined
              );
              return result;
            },
          });
        }

        // 校验函数
        let script = validateRule.script,
          _this = this;

        if (script) {
          utils.javascriptParser(script).then((script) => {
            if (script) {
              const scriptRule = {
                isScript: true, // 用于判断校验规则是否为脚本校验
                validateFunction: function (rule, v, data, callback) {
                  if (_this.hidden && validateRule.validateWhenFieldIsHidden !== true) {
                    callback(undefined);
                    _this.$emit("validateStatusChange", undefined);
                  } else {
                    _this.pageContext.executeCodeSegment(
                      script,
                      {
                        rule,
                        v,
                        data,
                        callback: function (res) {
                          callback(res === undefined || res === true ? undefined : res);
                          _this.$emit(
                            "validateStatusChange",
                            res === undefined || res === true ? undefined : "error",
                            res === undefined || res === true ? undefined : res
                          );
                        },
                      },
                      _this
                    );
                  }
                },
              };
              rules.push(scriptRule);
              let hasIndex = findIndex(_this.rules, (item) => item.isScript);
              if (hasIndex == -1) {
                _this.rules.push(scriptRule);
              }
            }
          });
        }

        // 数据唯一性校验
        let uniqueType = validateRule.uniqueType,
          uniqueMsg = validateRule.uniqueMsg;
        // 大字段类型不存在唯一性校验
        if (uniqueType && this.widget.configuration.dbDataType != "16") {
          rules.push({
            validateFunction: function (rule, value, callback) {
              return new Promise((resolve, reject) => {
                if (_this.hidden && validateRule.validateWhenFieldIsHidden !== true) {
                  _this.$emit("validateStatusChange", undefined);
                  resolve();
                  return;
                }
                if (uniqueType) {
                  _this.uniqueCheck(
                    function (exists) {
                      _this.$emit(
                        "validateStatusChange",
                        exists ? "error" : undefined,
                        exists
                          ? uniqueMsg
                            ? _this.$t("validateRuleDataExistError", uniqueMsg)
                            : _this.$t("widgetDyform.existData", "数据已存在")
                          : undefined
                      );

                      if (exists) {
                        reject(
                          new Error(
                            uniqueMsg
                              ? _this.$t("validateRuleDataExistError", uniqueMsg)
                              : _this.$t("widgetDyform.existData", "数据已存在")
                          )
                        );
                      } else {
                        resolve();
                      }
                    },
                    undefined,
                    uniqueType !== "globalUnique"
                  );
                }
              });
            },
          });
        }
      }

      // 添加组件自定义规则
      if (typeof this.addCustomRules === "function") {
        let customRules = this.addCustomRules();
        rules = rules.concat(customRules);
      }

      // 组件自定义规则
      if (this.customRules && this.customRules.length) {
        rules = rules.concat(this.customRules);
      }
      this.rules = rules;
      return rules;
    },

    // 判断数据唯一性
    uniqueCheck(callback, params, unitUnique = false) {
      if (params == undefined) {
        let fieldValue = this.getValue();
        if (fieldValue) {
          fieldValue = fieldValue + "";
        }
        params = {
          fieldName: this.fieldCode,
          fieldValue: fieldValue,
          tblName: this.form.tableName,
          uuid: this.formData.uuid,
        };
      }
      this.$axios.post("/pt/dyform/data/validate/exists", { ...params, unitUnique }).then(({ data }) => {
        callback(data.data);
      });
    },
    getValue() {
      return this.formData[this.fieldCode];
    },
    //值设置或者获取控件当前值
    setValue(val) {
      if (val && val.eventParams != undefined) {
        // 由事件传递进来的参数
        val = val.eventParams.value;
      }
      this.form.formData[this.fieldCode] = val;
      this.clearValidate();
      this.afterSetValue();
      this.emitChange();
    },
    afterSetValue() {},
    setValueByEventParams(evt) {
      if (evt != undefined && evt.eventParams != undefined) {
        this.setValue(evt.eventParams.value);
        this.emitChange();
      }
    },
    isEmptyValue() {
      return (
        this.getValue() == undefined || this.getValue() === "" || this.getValue() == null || this.getValue().length == 0
      );
    },
    // 显示值
    displayValue(value) {
      if (value && value != this.form.formData[this.fieldCode]) {
        this.form.formData[this.fieldCode] = value;
      }
      return this.form.formData[this.fieldCode];
    },
    onBlur() {
      this.emitBlur();
    },
    emitBlur(data) {
      this.$emit("blur", {
        $vue: this,
        ...data,
      });
      // 触发字段变更校验
      if (this.formRuleTrigger.indexOf("blur") > -1) {
        this.emitValidate();
      }
    },
    emitComplete() {
      this.$emit("complete", this);
    },
    emitChange(data, validate = true) {
      this.$emit("change", { $vue: this, ...data });
      // 从表内代理行数据组件(用于分页数据校验)，设值不触发字段变更校验
      if (this.$attrs.unValidateWidget) {
        return;
      }
      // 触发字段变更校验
      if (
        validate &&
        this.$refs[this.fieldCode] &&
        this.$refs[this.fieldCode].onFieldChange &&
        this.formRuleTrigger.indexOf("change") > -1
      ) {
        this.emitValidate();
      }
    },
    resetField() {
      if (this.$refs[this.fieldCode]) {
        this.$refs[this.fieldCode].clearValidate();
        this.setValue(undefined);
      }
    },
    // 过滤器
    onFilter({ searchValue, comparator, source, ignoreCase = true }) {
      return searchValue != undefined && source != undefined
        ? ignoreCase
          ? source.toString().toLowerCase().indexOf(searchValue.toLowerCase()) > -1
          : source.toString().indexOf(searchValue) > -1
        : false;
    },
    clearValidate() {
      this.widgetDyformContext.$refs.form && this.widgetDyformContext.$refs.form.clearValidate([this.fieldCode]);
    },
    /**
     * 触发字段的校验
     */
    emitValidate() {
      try {
        if (this.$refs[this.fieldCode]) {
          this.$refs[this.fieldCode].onFieldChange(this.formData[this.fieldCode]); // 触发字段必填性校验
        }
      } catch (error) {}
    },
    updateFormItemRequired(required = true) {
      if (
        this.widget.configuration.isDatabaseField &&
        !(this.parent != undefined && this.parent.wtype === "WidgetFormItem")
      ) {
        let uniForm = this.widgetDyformContext.$refs.form;
        if (uniForm.childrens) {
          for (let child of uniForm.childrens) {
            if (child.name == this.widget.configuration.code) {
              child.required = required;
              break;
            }
          }
        }

        console.log("表单校验规则", uniForm.formRules);
      }
    },

    setRequired(required = true) {
      if (typeof required !== "boolean" && required.eventParams != undefined) {
        // 由事件传递进来的参数
        required = required.eventParams.required === "true";
      }
      let originalRequired = this.required;
      this.required = required;
      this.initValidateRules();

      this.widget.configuration.required = this.required;

      // 向上通知子元素的必填性变更通知
      if (this.parent && this.parent.id) {
        this.form.emitEvent("onChildRequiredChanged." + this.parent.id, required, this.widget.id);
      }
      // 必填改为非必填，并且有必填错误校验信息存在，则清除掉
      if (
        originalRequired &&
        !required &&
        this.$refs[this.fieldCode] &&
        this.$refs[this.fieldCode].validateState == "error" &&
        (this.formData[this.fieldCode] == undefined ||
          this.formData[this.fieldCode] == "" ||
          this.formData[this.fieldCode].length == 0)
      ) {
        this.clearValidate();
      }

      this.$emit("requiredChange", this.required);
    },

    initValidateRules() {
      // 设置校验规则
      if (this.widget.configuration.isDatabaseField) {
        let uniForm =
          this.widgetDyformContext.$refs.form ||
          (this.widgetSubformContext && this.widgetSubformContext.$refs.proxyFormRef);
        // 从表使用的是[fromUuid,rowId,code]生成的真实参数
        if (uniForm) {
          uniForm.setRules(
            Object.assign({}, uniForm.formRules, {
              [uniForm._realName(this.formModelItemProp)]: {
                rules: this.createWidgetValidateRules(this.widget, this.required),
              },
            })
          );
          this.updateFormItemRequired(this.required);
        }
      }
    },

    initFormulaApiData() {
      let obj = {};
      if (
        this.widget.configuration.formula != undefined &&
        this.widget.configuration.formula.enable &&
        this.widget.configuration.formula.items.length > 0
      ) {
        this.widget.configuration.formula.items.forEach((i) => {
          if (i.type == "js-api") {
            obj[i.value.substr(2, i.value.length - 3)] = undefined;
          }
        });
      }
      return obj;
    },
    getFormulaCalculateValue() {
      let v = null;
      if (this.widget.configuration.formula != undefined) {
        try {
          console.log(this.widget.configuration.code + " 计算公式表达式: ", this.calculateExpression);
          let compiler = template(this.calculateExpression);
          // 排除 undefined 属性，避免清空字段值后仍然能计算成功返回值
          let data = {};
          for (let key in this.formData) {
            if (this.formData[key] != undefined && this.formData[key] !== "") {
              data[key] = this.formData[key];
            }
          }
          for (let key in this.formulaApiData) {
            if (this.formulaApiData[key] != undefined && this.formulaApiData[key] !== "") {
              data[key] = this.formulaApiData[key];
            }
          }
          v = compiler(data);
          console.log(this.widget.configuration.code + " 执行计算公式结果: ", v);
          v = new Function("return " + v)();
          if (Number.isNaN(v)) {
            v = null;
          }
          if (this.widget.configuration.formula.toFixedNumber != undefined) {
            v = parseFloat(v.toFixed(this.widget.configuration.formula.toFixedNumber));
          } else {
            if (this.widget.wtype == "WidgetFormInputNumber") {
              v = parseFloat(
                v.toFixed(
                  this.widget.configuration.dbDataType == "17" && this.widget.configuration.scale != undefined
                    ? this.widget.configuration.scale
                    : 0
                )
              );
            }
          }
        } catch (error) {
          console.warn(this.widget.configuration.code + " 计算公式输出结果失败: ", error);
          v = null;
        }
      }

      return v;
    },

    setFormulaCalculateFieldValue() {
      this.tryCalculateFormulaApiDataValue();
      let v = this.getFormulaCalculateValue();
      if (v != this.form.formData[this.widget.configuration.code]) {
        this.setValue(v);
      }
    },

    tryCalculateFormulaApiDataValue() {
      if (
        this.widget.configuration.formula != undefined &&
        this.widget.configuration.formula.enable &&
        this.widget.configuration.formula.items.length > 0
      ) {
        this.widget.configuration.formula.items.forEach((i) => {
          // 从表类api计算有监听从表的列值变更进行触发，此处不计算
          if (i.type == "js-api" && !i.apiCode.startsWith("subformFormulaApi.")) {
            let varName = i.value.substr(2, i.value.length - 3),
              apiCode = i.apiCode.split(".");
            if (formulaApi[apiCode[0]] != undefined && typeof formulaApi[apiCode[0]][apiCode[1]] == "function") {
              let result = formulaApi[apiCode[0]][apiCode[1]].call(this, {
                form: this.form,
                formula: JSON.parse(JSON.stringify({ varName, ...i })),
              });
              if (result != undefined) {
                if (result instanceof Promise) {
                  result.then((rst) => {
                    if (rst != undefined) {
                      Object.assign(this.formulaApiData, rst);
                    }
                  });
                } else {
                  Object.assign(this.formulaApiData, result);
                }
              }
            }
          }
        });
      }
    },

    fireDefineDocumentEvent(evt, e) {
      evt = cloneDeep(evt);
      let containWidgetEvent = false;
      if (Array.isArray(evt.codeSource) && evt.codeSource.includes("widgetEvent")) {
        // 剔除组件事件，避免 executeEvent 重复执行
        evt.codeSource.splice(evt.codeSource.indexOf("widgetEvent"), 1);
        containWidgetEvent = true;
      }
      if (
        (typeof evt.codeSource == "string" && evt.codeSource !== "widgetEvent") ||
        (Array.isArray(evt.codeSource) && !evt.codeSource.includes("widgetEvent") && evt.codeSource.length > 0)
      ) {
        this.executeEvent(evt, { originEvent: e });
      }
      if (containWidgetEvent) {
        evt.codeSource = "widgetEvent";
      }
      if (evt.codeSource == "widgetEvent" || evt.codeSource.includes("widgetEvent")) {
        try {
          let widgetEvent = evt.widgetEvent;
          if (widgetEvent) {
            let events = [widgetEvent];
            if (Array.isArray(widgetEvent)) {
              events = widgetEvent;
            }
            events.forEach((e) => {
              let executeEvent = true;
              if (e.condition && e.condition.enable && e.condition.conditions.length > 0) {
                // 判断条件是否成立
                let { conditions, match } = e.condition;
                executeEvent = match == "all";
                for (let c = 0, clen = conditions.length; c < clen; c++) {
                  let { code, operator, value } = conditions[c];
                  // if (/\$\{.+\}/.test(value)) {
                  //   // 字段编码占位符解析
                  //   value = stringTemplate(value, {
                  //     ...this.formData,
                  //   })
                  // }
                  let isTrue = utils.expressionCompare(
                    { ...this.formData, __DYFORM__: { editable: this.form.displayState === "edit" } },
                    code,
                    operator,
                    value
                  );
                  if (match == "any") {
                    // 满足任一条件就执行
                    if (isTrue) {
                      executeEvent = true;
                      break;
                    }
                  } else {
                    // 全部情况下，只要一个条件不满足就不执行
                    if (!isTrue) {
                      executeEvent = false;
                      break;
                    }
                  }
                }
              }

              if (executeEvent) {
                for (let item of Array.isArray(e.event) ? e.event : [e]) {
                  let { eventId, eventParams, eventWid, wEventParams } = item;
                  if (eventId == undefined || eventWid == undefined) {
                    continue;
                  }
                  if (
                    !this.editable &&
                    eventId == "setEditable" &&
                    /**
                     * 字段不可编辑情况下，触发字段能进行可编辑状态切换的前提:
                     * 1. 当前整体可编辑
                     * 2. 无规则限制字段不可编辑
                     *
                     * 否则要把编辑切换修改为不可编辑情况进行展示
                     */
                    (this.form.displayState !== "edit" ||
                      (this.form.formElementRules &&
                        this.form.formElementRules[eventWid] &&
                        this.form.formElementRules[eventWid].editable !== true))
                  ) {
                    let paramGet = false;
                    eventParams = JSON.parse(JSON.stringify(eventParams || []));
                    if (eventParams) {
                      for (let i = 0, len = eventParams.length; i < len; i++) {
                        if (eventParams[i].paramKey == "editable") {
                          eventParams[i].paramValue = "false";
                          paramGet = true;
                          break;
                        }
                      }
                    }
                    if (!paramGet) {
                      eventParams.push({
                        paramKey: "editable",
                        paramValue: "false",
                      });
                    }
                    eventId = "setEditable";
                  }
                  let handler = {
                    actionType: "widgetEvent",
                    $evtWidget: this,
                    meta: this.formData,
                    eventWid,
                    eventId,
                    eventParams,
                    wEventParams,
                    pageContext: this.form,
                  };
                  if (this.$attrs.isSubformCell) {
                    handler.pageContext = this.form; // 在从表单元内，派发事件的上下文使用当前从表的form，使得事件处理在从表行内组件间派发
                    // console.log('从表实例', this.widgetSubformContext);
                    // 判断派发的组件是否从表的列组件，如果不是，则说明派发到页面级组件上
                    if (!this.widgetSubformContext.isColumnWidget(eventWid)) {
                      handler.pageContext = this.pageContext;
                    }
                  }
                  this.dispatchEventHandler(handler);
                }
              }
            });
          }
        } catch (error) {
          console.error("执行组件事件异常", error);
        }
      }
    },
    triggerOnValueChange(newValue, oldValue) {
      // console.log(this.widget.configuration.code, '值变更')
      if (
        (newValue == undefined || newValue == null || newValue == "") &&
        (oldValue == undefined || oldValue == null || oldValue == "")
      ) {
        return;
      }
      // console.log(`${this.widget.configuration.name} - ${this.fieldCode} 字段发生了值变更`, oldValue, newValue)
      this.triggerFormElementDomEvent("onChange");

      this.$emit("valueChange", { newValue, oldValue });
    },
    triggerOnChange(evt) {
      if (evt) {
        this.fireDefineDocumentEvent(evt);
      }
    },
    triggerFormElementDomEvent(eventName) {
      if (this.widget.configuration.domEvents != undefined) {
        for (let i = 0, len = this.widget.configuration.domEvents.length; i < len; i++) {
          let evt = this.widget.configuration.domEvents[i];
          if (evt.id == "onChange") {
            this.triggerOnChange(evt);
            continue;
          }
          if (evt.id == eventName) {
            this.fireDefineDocumentEvent(evt);
          }
        }
      }
    },
    initRequiredHook() {
      if (
        this.required &&
        this.widget.configuration.requiredCondition &&
        this.widget.configuration.requiredCondition.conditions.length
      ) {
        let evaluateRequired = (dyform) => {
          let formData = { ...dyform.formData };
          let { conditions, match } = this.widget.configuration.requiredCondition;
          for (let i = 0, len = conditions.length; i < len; i++) {
            let { code, operator, value } = conditions[i];
            let isTrue = utils.expressionCompare(formData, code, operator, value);
            if (match == "any") {
              // 满足任一条件就返回必填
              if (isTrue) {
                this.setRequired(true);
                return;
              }
            } else {
              // 全部情况下，只要一个条件不满足就返回非必填
              if (!isTrue) {
                this.setRequired(false);
                return;
              }
            }
          }
          this.setRequired(match == "all");
        };
        this.dyform.handleEvent("formDataChanged", ({ wDyform }) => {
          wDyform && evaluateRequired(wDyform.dyform);
        });
        // 初始化时进行条件判断
        evaluateRequired(this.dyform);
      }
    },

    initFormulaCalculateFieldValue() {
      if (!this.$attrs.isSubformCell) {
        // 非从表单元格的组件计算公式
        this.$watch("formulaValueDataMD5", () => {
          this.setFormulaCalculateFieldValue();
        });
        // 挂载成功，判断是否有关联计算字段，存在则计算初始值
        if (this.relaFormulaFields.field.length > 0) {
          this.setFormulaCalculateFieldValue();
        }
        // 判断是否有从表API的计算方式
        if (
          this.widget.configuration.formula != undefined &&
          this.widget.configuration.formula.enable &&
          this.widget.configuration.formula.items.length > 0
        ) {
          console.log(this.widget.configuration.name, "值计算公式");
          let subformWid = new Set(),
            subformFormula = {};
          this.widget.configuration.formula.items.forEach((i) => {
            if (i.type == "js-api") {
              if (i.apiCode.startsWith("subformFormulaApi.")) {
                subformWid.add(i.params.widgetId);
                if (subformFormula[i.params.widgetId] == undefined) {
                  subformFormula[i.params.widgetId] = [];
                }
                subformFormula[i.params.widgetId].push(i);
              }
            }
          });
          if (subformWid.size > 0) {
            for (let wid of subformWid) {
              this.form.handleEvent(`${wid}:rowChange`, ({ data, wSubform }) => {
                subformFormula[wid].forEach((item) => {
                  let varName = item.value.substr(2, item.value.length - 3),
                    apiCode = item.apiCode.split(".");
                  if (formulaApi[apiCode[0]] != undefined && typeof formulaApi[apiCode[0]][apiCode[1]] == "function") {
                    let result = formulaApi[apiCode[0]][apiCode[1]].call(this, {
                      $subform: wSubform,
                      formula: JSON.parse(JSON.stringify({ varName, ...item })),
                    });
                    if (result != undefined) {
                      if (result instanceof Promise) {
                        result.then((rst) => {
                          if (rst != undefined) {
                            Object.assign(this.formulaApiData, rst);
                          }
                        });
                      } else {
                        Object.assign(this.formulaApiData, result);
                      }
                    }
                  }
                });
              });
            }
          }
        }
      }
    },
    initDomEvent() {
      if (this.widget.configuration.code && this.widget.configuration.domEvents) {
        for (let i = 0, len = this.widget.configuration.domEvents.length; i < len; i++) {
          let evt = this.widget.configuration.domEvents[i];
          if (evt.id == "onChange" && evt.changeTriggerOnDataInit) {
            let { widgetEvent, jsFunction, customScript } = evt;
            if (widgetEvent.length > 0 || jsFunction != undefined || customScript != undefined) {
              if (
                this.formData[this.fieldCode] != undefined &&
                this.formData[this.fieldCode] != null &&
                this.formData[this.fieldCode] != ""
              ) {
                this.triggerFormElementDomEvent("onChange");
              }
              break;
            }
          }
        }
      }
    },
    // 组件状态变更时触发的逻辑
    afterDisplayStateChanged() {},
  },

  mounted() {
    this.isMounted = true;
    // 监听值变更
    this.$watch("form.formData." + this.widget.configuration.code, (newValue, oldValue) => {
      if (
        this.widgetDyformContext != undefined &&
        this.widgetDyformContext.originFormData != undefined &&
        this.widgetDyformContext.originFormData[this.formData.uuid] &&
        this.widgetDyformContext.originFormData[this.formData.uuid][this.fieldCode] == newValue &&
        !this.__fieldValueInitedChanged
      ) {
        let _continue = true;
        if (this.widget.configuration.domEvents) {
          for (let i = 0, len = this.widget.configuration.domEvents.length; i < len; i++) {
            let evt = this.widget.configuration.domEvents[i];
            if (evt.id == "onChange" && !evt.changeTriggerOnDataInit) {
              _continue = false;
              return;
            }
          }
        }
        if (!_continue) {
          this.__fieldValueInitedChanged = true;
          return;
        }
      }

      this.triggerOnValueChange(newValue, oldValue);
    });
    if (this.isSubformCell) {
      this.$watch(
        "formDataProp",
        (newData) => {
          let formDataStr = utils.md5(JSON.stringify(JSON.stringify(this.formData)));
          let newDataStr = utils.md5(JSON.stringify(JSON.stringify(newData)));
          if (formDataStr != newDataStr) {
            this.formData = newData;
            this.form.formData = newData;
          }
        },
        { deep: true }
      );
    }
    this.$emit("mounted", { $vue: this });
    this.initValidateRules();
    this.initRequiredHook();
    this.initFormulaCalculateFieldValue();
    this.initDomEvent();
  },
};
