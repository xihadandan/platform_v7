/**
  表单组件基类：定义了表单组件的基本属性、方法等操作
 */
import widgetMixin from '@framework/vue/mixin/widgetMixin';
import { expressionCompare, deepClone, javascriptParser } from '@framework/vue/utils/util';
import { debounce, template as stringTemplate, isEmpty, uniqBy, each, findIndex, cloneDeep } from 'lodash';
import md5 from '@framework/vue/utils/md5';
import * as formulaApi from './formula-api.js';
import { compile as mathCompile, evaluate as mathEvaluate } from 'mathjs';

export const FormElement = {
  name: 'FormElement',
  mixins: [widgetMixin],
  inject: ['dyform', 'pageContext', 'widgetSubformContext', 'widgetDyformContext', 'rootWidgetDyformContext'],
  props: {
    form: {
      type: Object,
      default: function () {
        return this.dyform;
      }
    },
    formData: {
      // 默认为form对象内的表单数据模型
      type: Object,
      default: function () {
        return this.form.formData;
      }
    },
    formModelItemProp: {
      type: String,
      default: function () {
        return this.widget.configuration != undefined ? this.widget.configuration.code + '' : undefined;
      }
    }
  },
  data() {
    let data = {
      fieldCode: this.widget.configuration.code,
      // 表单字段组件的展示状态初始化以表单为主
      editable: this.form.displayState == undefined || this.form.displayState == 'edit',
      displayAsLabel: this.form.displayState == 'label', // 初始化展示为不可编辑的文本展示
      readonly: false,
      disable: false,
      hidden: this.widget.configuration.hidden === true,
      hiddenByRule: undefined,
      required: this.widget.configuration.required || false,
      initRequired: this.widget.configuration.required || false // 表单字段组件配置中必填的必填值
    };

    if (!this.designMode) {
      // 针对字段型、交互型组件存在默认的组件状态
      if (this.widget.configuration.hasOwnProperty('defaultDisplayState')) {
        data.hidden = this.widget.configuration.defaultDisplayState === 'hidden';
        if (data.editable) {
          // 初始可编辑的前提下，组件是否可编辑，要需要有默认决定
          data.editable = this.widget.configuration.defaultDisplayState === 'edit';
          data.displayAsLabel = this.widget.configuration.defaultDisplayState === 'unedit';
        }
      }

      // 不可编辑情况下，要判断组件是否只读展示、或者文本展示
      if (!data.editable && this.widget.configuration.hasOwnProperty('uneditableDisplayState')) {
        data.readonly = this.widget.configuration.uneditableDisplayState === 'readonly';
        data.displayAsLabel = this.widget.configuration.uneditableDisplayState === 'label';
      }

      // 表单元素的规则修改
      if (this.form.formElementRules && this.form.formElementRules[this.widget.id]) {
        let rule = this.form.formElementRules[this.widget.id];
        if (rule.hidden != undefined) {
          data.hidden = rule.hidden;
          data.hiddenByRule = rule.hidden;
          if (rule.hidden) {
            // 隐藏情况下，非必填
            data.required = false;
          }
        }
        if (rule.editable != undefined) {
          data.editable = rule.editable;
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
            this.widget.configuration.hasOwnProperty('uneditableDisplayState')
          ) {
            // 不可编辑情况下的可见，要根据组件配置进行展示
            data.readonly = this.widget.configuration.uneditableDisplayState === 'readonly';
            data.displayAsLabel = this.widget.configuration.uneditableDisplayState === 'label';
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
          if (!data.editable && this.widget.configuration.hasOwnProperty('uneditableDisplayState')) {
            data.readonly = this.widget.configuration.uneditableDisplayState === 'readonly';
            data.displayAsLabel = this.widget.configuration.uneditableDisplayState === 'label';
          }
        }
      }
    }

    // 数据模型的字段组件，则长度、必填性、唯一性以数据模型为主（不以其他业务定制的规则为主）
    if (this.widget.column != undefined) {
      // console.log(`字段 [ ${this.widget.configuration.code} ] 来源数据模型字段`,this.widget.column)
      let { dataType, notNull, length, scale, unique } = this.widget.column;
      this.widget.configuration.length = length;
      if (dataType == 'number') {
        if (this.widget.wtype == 'WidgetFormInputNumber' && scale != undefined) {
          this.widget.configuration.decimalPlacesNumber = scale;
          this.widget.configuration.scale = scale;
        }
      }
      if (notNull) {
        data.required = true;
      }
      if (this.widget.configuration.validateRule != undefined && (unique == 'GLOBAL' || unique == 'TENANT')) {
        this.widget.configuration.validateRule.uniqueType = unique == 'GLOBAL' ? 'globalUnique' : 'tenantUnique';
      }
    }

    data.rules = this.initValidateRules(data.required);
    data.formulaApiData = this.initFormulaApiData();
    return data;
  },

  beforeCreate() { },
  components: {},
  computed: {
    itemLabel() {
      // 在表单列的不展示字段名称，或者从表单元格内需要隐藏字段名
      if ((this.parent != undefined && this.parent.wtype === 'WidgetFormItem') || this.$attrs.isSubformCell || this.$attrs.labelHidden) {
        return null;
      }

      return this.widget.configuration.fieldNameVisible ? this.$t(this.widget.id, this.widget.configuration.name) : null;
    },
    itemStyle() {
      let style = {};
      // if (!this.itemLabel) {
      //   style['vertical-align'] = 'bottom';
      // }

      if (this.hidden && !this.designMode) {
        style.display = 'none';
      }
      if (!this.hidden && this.widget.configuration.style) {
        style.display = 'inline-flex';
        style.width =
          typeof this.widget.configuration.style.width === 'number'
            ? this.widget.configuration.style.width + 'px'
            : this.widget.configuration.style.width;
      }
      return style;
    },
    _vShowByData() {
      return this.formData;
    },

    // 相关方法暴露为事件，提供外部调用
    supperDefaultEvents() {
      return [
        {
          id: 'setVisible',
          title: '设置为显示或者隐藏',
          eventParams: [
            {
              paramKey: 'visible',
              remark: '是否显示',
              valueSource: {
                inputType: 'select', // multi-select , checkbox , radio, input
                options: [
                  { label: '显示', value: 'true' },
                  { label: '隐藏', value: 'false' }
                ]
              }
            }
          ]
        },

        {
          id: 'setEditable',
          title: '设置为可编辑或者不可编辑',
          eventParams: [
            {
              paramKey: 'editable',
              remark: '是否可编辑',
              valueSource: {
                inputType: 'select', // multi-select , checkbox , radio, input
                options: [
                  { label: '可编辑', value: 'true' },
                  { label: '不可编辑', value: 'false' }
                ]
              }
            }
          ]
        },
        {
          id: 'setValueByEventParams',
          title: '设值',
          eventParams: [
            {
              paramKey: 'value',
              remark: '值'
            }
          ]
        },
        {
          id: 'setRequired',
          title: '设置为必填或者非必填',
          eventParams: [
            {
              paramKey: 'required',
              remark: '是否必填',
              valueSource: {
                inputType: 'select', // multi-select , checkbox , radio, input
                options: [
                  { label: '必填', value: 'true' },
                  { label: '非必填', value: 'false' }
                ]
              }
            }
          ]
        },
        {
          id: 'clearValidate',
          title: '清空字段校验信息'
        }
      ];
    },

    relaFormulaFields() {
      let rela = {
        field: []
      };
      if (
        this.widget.configuration.formula != undefined &&
        this.widget.configuration.formula.enable &&
        this.widget.configuration.formula.items.length > 0
      ) {
        this.widget.configuration.formula.items.forEach(i => {
          if (i.dataIndex != undefined) {
            if (Array.isArray(i.dataIndex)) {
              for (let d of i.dataIndex) {
                if (typeof d === 'string') {
                  rela.field.push(...i.dataIndex);
                } else if (typeof d == 'object' && d.dataIndex && !d.widgetId) {
                  // 从表会指定 widgetId ，这边只会就判断主表字段变更
                  // 嵌套字段
                  if (typeof d.dataIndex === 'string') {
                    rela.field.push(d.dataIndex);
                  } else if (Array.isArray(d.dataIndex)) {
                    rela.field.push(...d.dataIndex);
                  }
                }
              }
            } else {
              rela.field.push(i.dataIndex);
            }
          }
        });
      }
      return rela;
    },
    calculateExpression() {
      let expression = [];
      if (this.widget.configuration.formula != undefined && this.widget.configuration.formula.enable) {
        this.widget.configuration.formula.items.forEach(i => {
          expression.push(i.value);
        });
      }
      return expression.join('');
    },
    formulaValueDataMD5() {
      if (this.calculateExpression != '') {
        let data = { ...this.formulaApiData };
        if (this.formData != undefined && this.relaFormulaFields.field.length > 0) {
          // 根据依赖字段的数据md5值, 用于判断发生变更
          for (let k in this.formData) {
            if (this.relaFormulaFields.field.includes(k) && k != this.widget.configuration.code) {
              data[k] = this.formData[k];
            }
          }
        }
        let str = md5(JSON.stringify(JSON.stringify(data)));
        console.log(this.widget.configuration.code, 'formulaValueDataMD5', str, this.relaFormulaFields, data);
        return str;
      }
      return '-1';
    }
  },
  created() {
    this.emitChange = debounce(this.emitChange.bind(this), 300);
    this.triggerOnChange = debounce(this.triggerOnChange.bind(this), 300);
    this.triggerOnValueChange = debounce(this.triggerOnValueChange.bind(this), 300);
    this.setFormulaCalculateFieldValue = debounce(this.setFormulaCalculateFieldValue.bind(this), 300);

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
      if (!this.designMode) {
        if (this.fieldCode) {
          this.form.$fieldset[this.fieldCode] = this;
          this.form.emitEvent(`FieldWidget:${this.form.formUuid}:${this.fieldCode}:Created`);
        }
      }
    }

    if (!this.designMode) {
      if (this.form.$element == undefined) {
        this.form.$element = {};
      }
      this.form.$element[this.widget.id] = this;
    }
  },
  beforeMount() {
    if (this.$attrs.stopEvent) {
      return;
    }
    // 初始化自定义事件处理
    let _this = this;
    // 表单组件重新绑定事件
    if (this.$attrs.isSubformCell || (this.widgetDyformContext && this.widgetDyformContext.isSubform)) {
      // 从表行内，单元格组件重新绑定事件（覆盖掉 widgetMixin 的事件）
      for (let k in this.vDefineEvents) {
        let e = this.vDefineEvents[k];
        if (!['created', 'beforeMount', 'mounted'].includes(e.id)) {
          // 对外开放的事件
          _this.pageContext.offEvent(`${this.widget.id}:${e.id}`);
          _this.form.offEvent(`${this.widget.id}:${e.id}`).handleEvent(`${this.widget.id}:${e.id}`, function () {
            if (!e.default) {
              let args = Array.from(arguments);
              args = [e].concat(args); // 参数合并
              _this.executeEvent.apply(_this, args);
            } else if (_this[e.id]) {
              // 默认的事件，存在对应的方法函数，执行该方法函数
              _this[e.id].apply(_this, arguments);
            }
          });
        }
      }
    }
  },
  mounted() {
    // console.log(`组件: [ ${this.widget.title} ] 挂载结束`);
    if (this.$attrs.stopEvent) {
      return;
    }
    if (!this.designMode) {
      this.pageContext.handleEvent(`${this.form.formUuid}:saveDataError`, params => {
        this.afterSaveDataError(params);
      });
    }

    this.$watch('formData.' + this.widget.configuration.code, (newValue, oldValue) => {
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
            if (evt.id == 'onChange' && !evt.changeTriggerOnDataInit) {
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
      this.triggerOnValueChange(newValue, oldValue, window.event);
      if (this.widgetSubformContext != undefined && this.form.modalEditFormNeedConfirm !== true) {
        // 从表域内需要向上级主表反馈从表列值的变更，用于触发相关列值变更的逻辑使用，例如：计算公式等
        this.widgetSubformContext.dyform.emitEvent(`${this.widgetSubformContext.widget.id}:${this.widget.configuration.code}:valueChange`, {
          $subform: this.widgetSubformContext,
          dataIndex: this.widget.configuration.code,
          newValue,
          oldValue,
          rowData: this.formData,
          rowKey: this.form.namespace
        });
      }
    });
    this.initDomEvent();
    this.initFormulaCalculateFieldValue();
    if (this.stopEmitFormElementMountedEvent !== true) {
      this.form.emitEvent('form-element:mounted', this);
    }
    setTimeout(() => {
      this.initEvaluateRequiredHook();
    }, 300);
  },

  methods: {
    initFormulaCalculateFieldValue() {
      if (!this.$attrs.isSubformCell && !this.designMode) {
        // 非从表单元格的组件计算公式
        this.$watch('formulaValueDataMD5', () => {
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
          console.log(this.widget.configuration.name, '值计算公式');
          let subformColValChangeMap = {},
            subformMap = {};
          this.widget.configuration.formula.items.forEach(i => {
            if (i.type == 'js-api') {
              if (i.apiCode.startsWith('subformFormulaApi.')) {
                // 主表字段的计算依赖从表API，需要注册依赖从表的列字段变更
                if (i.params.widgetId != undefined && i.params.dataIndex != undefined) {
                  if (subformMap[i.params.widgetId] == undefined) {
                    subformMap[i.params.widgetId] = [];
                  }
                  subformMap[i.params.widgetId].push(i);
                  if (subformColValChangeMap[`${i.params.widgetId}:${i.params.dataIndex}:valueChange`] == undefined) {
                    subformColValChangeMap[`${i.params.widgetId}:${i.params.dataIndex}:valueChange`] = [];
                  }
                  subformColValChangeMap[`${i.params.widgetId}:${i.params.dataIndex}:valueChange`].push(i);
                }
              } else if (Array.isArray(i.dataIndex)) {
                for (let d of i.dataIndex) {
                  if (d.widgetId) {
                    if (subformMap[d.widgetId] == undefined) {
                      subformMap[d.widgetId] = [];
                    }
                    subformMap[d.widgetId].push(i);
                    let dataIndexes = Array.isArray(d.dataIndex) ? d.dataIndex : [d.dataIndex];
                    for (let p of dataIndexes) {
                      if (subformColValChangeMap[`${d.widgetId}:${p}:valueChange`] == undefined) {
                        subformColValChangeMap[`${d.widgetId}:${p}:valueChange`] = [];
                      }
                      subformColValChangeMap[`${d.widgetId}:${p}:valueChange`].push(i);
                    }
                  }
                }
              }
            }
          });
          if (Object.keys(subformColValChangeMap).length > 0) {
            let calculateSubformFormula = (item, $subform) => {
              let varName = item.value.substr(2, item.value.length - 3),
                apiCode = item.apiCode.split('.');
              if (formulaApi[apiCode[0]] != undefined && typeof formulaApi[apiCode[0]][apiCode[1]] == 'function') {
                let result = formulaApi[apiCode[0]][apiCode[1]].call(this, {
                  $subform,
                  formula: JSON.parse(JSON.stringify({ varName, ...item }))
                });
                if (result != undefined) {
                  if (result instanceof Promise) {
                    result.then(rst => {
                      if (rst != undefined) {
                        Object.assign(this.formulaApiData, rst);
                      }
                    });
                  } else {
                    Object.assign(this.formulaApiData, result);
                  }
                }
              }
            };
            for (let key in subformMap) {
              this.form.handleEvent(`${key}:rowLengthChange`, data => {
                subformMap[key].forEach(item => {
                  calculateSubformFormula(item, data.$subform);
                });
              });
            }
            for (let key in subformColValChangeMap) {
              this.form.handleEvent(`${key}`, data => {
                subformColValChangeMap[key].forEach(item => {
                  calculateSubformFormula(item, data.$subform);
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
          if (evt.id == 'onChange' && evt.changeTriggerOnDataInit) {
            let { widgetEvent, jsFunction, customScript } = evt;
            if (widgetEvent.length > 0 || jsFunction != undefined || customScript != undefined) {
              if (
                this.formData[this.fieldCode] != undefined &&
                this.formData[this.fieldCode] != null &&
                this.formData[this.fieldCode] != ''
              ) {
                this.triggerFormElementDomEvent('onChange');
              }
              break;
            }
          }
        }
      }
    },
    initEvaluateRequiredHook() {
      let requiredCondition = this.widget.configuration.requiredCondition;
      if (this.form.formElementRules[this.widget.id] && this.form.formElementRules[this.widget.id].requiredCondition) {
        this.initRequired = true; //规则有加必填条件，该字段必填就有开启
        requiredCondition = this.form.formElementRules[this.widget.id].requiredCondition;
      }
      // 表单字段组件配置中必填打开,就根据条件计算是否必填
      if (
        this.initRequired &&
        requiredCondition &&
        requiredCondition.conditions.length
      ) {
        let evaluateRequired = () => {
          let formData = this.widgetDependentVariableDataSource();
          let { conditions, match } = requiredCondition;
          for (let i = 0, len = conditions.length; i < len; i++) {
            let { code, operator, value, codeValue } = conditions[i];
            if (code == undefined) {
              continue;
            }
            if (code.endsWith('.') && codeValue) {
              code = `${code}${codeValue}`;
            }
            let isTrue = expressionCompare(formData, code, operator, value);
            if (match == 'any') {
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
          this.setRequired(match == 'all');
        };
        this.dyform.handleEvent('formDataChanged', () => {
          evaluateRequired();
        });
        // 初始化时进行条件判断
        evaluateRequired();
      }
    },
    getFormulaCalculateValue() {
      let v = null;
      if (this.widget.configuration.formula != undefined) {
        try {
          console.log(this.widget.configuration.code + ' 计算公式表达式: ', this.calculateExpression);
          let compiler = stringTemplate(this.calculateExpression);
          // 排除 undefined 属性，避免清空字段值后仍然能计算成功返回值
          let data = {};
          for (let key in this.formData) {
            if (this.formData[key] != undefined && this.formData[key] !== '') {
              data[key] = this.formData[key];
            }
          }
          for (let key in this.formulaApiData) {
            if (this.formulaApiData[key] != undefined && this.formulaApiData[key] !== '') {
              data[key] = this.formulaApiData[key];
            }
          }
          let expr = compiler(data);
          console.log(this.widget.configuration.code + ' 执行计算公式结果: ', v);
          let isNumber = false;
          try {
            v = mathEvaluate(expr);
            isNumber = true;
          } catch (error) {
            isNumber = false;
            v = expr;
          }

          let isWidgetNumber = ['13', '131', '132', '14', '15', '12', '17'].includes(this.widget.configuration.dbDataType);
          if (!isNumber && isWidgetNumber) {
            v = null;
            console.warn('计算结果非数值, 无法赋值');
            return;
          }
          if (isWidgetNumber) {
            if (this.widget.configuration.formula.toFixedNumber != undefined) {
              v = parseFloat(v.toFixed(this.widget.configuration.formula.toFixedNumber));
            } else {
              if (this.widget.wtype == 'WidgetFormInputNumber') {
                v = parseFloat(
                  v.toFixed(
                    this.widget.configuration.dbDataType == '17' && this.widget.configuration.scale != undefined
                      ? this.widget.configuration.scale
                      : 0
                  )
                );
              }
            }
          }
        } catch (error) {
          console.warn(this.widget.configuration.code + ' 计算公式输出结果失败: ', error);
          v = null;
        }
      }

      return v;
    },

    tryCalculateFormulaApiDataValue() {
      if (
        this.widget.configuration.formula != undefined &&
        this.widget.configuration.formula.enable &&
        this.widget.configuration.formula.items.length > 0
      ) {
        this.widget.configuration.formula.items.forEach(i => {
          // 从表类api计算有监听从表的列值变更进行触发，此处不计算
          if (i.type == 'js-api' && !i.apiCode.startsWith('subformFormulaApi.')) {
            let varName = i.value.substr(2, i.value.length - 3),
              apiCode = i.apiCode.split('.');
            if (formulaApi[apiCode[0]] != undefined && typeof formulaApi[apiCode[0]][apiCode[1]] == 'function') {
              let result = formulaApi[apiCode[0]][apiCode[1]].call(this, {
                form: this.form,
                formula: JSON.parse(JSON.stringify({ varName, ...i }))
              });
              if (result != undefined) {
                if (result instanceof Promise) {
                  result.then(rst => {
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

    setFormulaCalculateFieldValue() {
      this.tryCalculateFormulaApiDataValue();
      let v = this.getFormulaCalculateValue();
      if (v != this.form.formData[this.widget.configuration.code]) {
        this.setValue(v);
      }
    },

    triggerOnValueChange(newValue, oldValue, e) {
      // console.log(this.widget.configuration.code, '值变更')
      if (
        (newValue == undefined || newValue === null || newValue === '') &&
        (oldValue === undefined || oldValue === null || oldValue === '')
      ) {
        return;
      }
      // console.log(`${this.widget.configuration.name} - ${this.fieldCode} 字段发生了值变更`, oldValue, newValue)
      this.triggerFormElementDomEvent('onChange', e);

      this.$emit('valueChange', { newValue, oldValue });
    },

    afterSaveDataError() { },
    // 初始化校验规则
    initValidateRules(required) {
      return this.createWidgetValidateRules(this.widget, required);
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
        trigger = validateRule ? validateRule.trigger : 'blur',
        type = widget.configuration.dataType || 'string';
      if (required) {
        // 必填规则
        rules.push({
          required: true,
          type,
          message: widget.configuration.requiredMsg
            ? this.$t(`validateRuleRequiredMessage`, widget.configuration.requiredMsg)
            : this.$t(`WidgetFormInput.validateMessage.required`, `必填`),
          trigger,
          field: widget.configuration.name,
          validator: function (rule, v, callback) {
            let value = _this.getValue();
            let result =
              _this.designMode || !(value == undefined || value == null || value === '' || (typeof value == 'object' && isEmpty(value)))
                ? undefined
                : new Error(rule.message);
            if (_this.hidden && validateRule.validateWhenFieldIsHidden !== true) {
              result = undefined;
            }
            _this.$emit('validateStatusChange', result != undefined ? 'error' : undefined, result != undefined ? rule.message : undefined);
            callback(result);
          }
        });
      }

      if (validateRule) {
        // 正则规则
        let regExpStr = validateRule.regExp.value;
        if (regExpStr) {
          let start = regExpStr.indexOf('/'),
            end = regExpStr.lastIndexOf('/'),
            regExp,
            errorMsg = validateRule.errorMsg
              ? this.$t(`validateRuleDataFormateError`, validateRule.errorMsg)
              : this.$t(`WidgetFormInput.validateMessage.dataFormateError`, `数据格式错误`);

          if (start === 0) {
            regExp =
              end == regExpStr.length - 1
                ? new RegExp(regExpStr.substring(1, end))
                : new RegExp(regExpStr.substring(1, end), regExpStr.substr(end + 1));
          } else {
            regExp = new RegExp(regExpStr);
          }

          rules.push({
            trigger,
            type,
            validator: function (rule, v, callback) {
              let result = undefined;
              let value = _this.getValue();
              if (value) {
                if (_this.hidden && validateRule.validateWhenFieldIsHidden !== true) {
                  result = undefined;
                } else {
                  result = regExp.test(value);
                  regExp.lastIndex = 0;
                }
                callback(result ? undefined : new Error(errorMsg));
              } else {
                callback();
              }
              _this.$emit('validateStatusChange', result === false ? 'error' : undefined, result === false ? errorMsg : undefined);
            }
          });
        }

        // 校验函数
        let script = validateRule.script,
          _this = this;

        if (script) {
          javascriptParser(script).then(script => {
            if (script) {
              const scriptRule = {
                isScript: true, // 用于判断校验规则是否为脚本校验
                trigger,
                type,
                validator: function (rule, value, callback) {
                  if (_this.hidden && validateRule.validateWhenFieldIsHidden !== true) {
                    callback(undefined);
                    _this.$emit('validateStatusChange', undefined);
                  } else {
                    _this.pageContext.executeCodeSegment(
                      script,
                      {
                        callback: function (res) {
                          callback(res === undefined || res === true ? undefined : res);
                          _this.$emit(
                            'validateStatusChange',
                            res === undefined || res === true ? undefined : 'error',
                            res === undefined || res === true ? undefined : res
                          );
                        }
                      },
                      _this
                    );
                  }
                }
              };
              rules.push(scriptRule);
              let hasIndex = findIndex(_this.rules, item => item.isScript);
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
        if (uniqueType && widget.configuration.dbDataType != '16') {
          rules.push({
            trigger,
            type,
            validator: function (rule, v, callback) {
              if (_this.hidden && validateRule.validateWhenFieldIsHidden !== true) {
                callback(undefined);
                _this.$emit('validateStatusChange', undefined);
                return;
              }
              let value = _this.getValue();
              if (uniqueType) {
                _this.uniqueCheck(
                  function (exists) {
                    let msg = uniqueMsg ? _this.$t('validateRuleDataExistError') : undefined;
                    callback(
                      exists ? new Error(msg || _this.$t(`WidgetFormInput.validateMessage.dataExistError`, '数据已存在')) : undefined
                    );
                    _this.$emit(
                      'validateStatusChange',
                      exists ? 'error' : undefined,
                      exists ? msg || _this.$t(`WidgetFormInput.validateMessage.dataExistError`, '数据已存在') : undefined
                    );
                  },
                  {
                    fieldName: widget.configuration.code,
                    fieldValue: value,
                    tblName: _this.form.tableName,
                    uuid: _this.formData.uuid || (_this.form.formData && _this.form.formData.uuid)
                  },
                  uniqueType !== 'globalUnique'
                );
              }
            }
          });
        }
      }

      // 添加组件自定义规则
      if (typeof this.addCustomRules === 'function') {
        let customRules = this.addCustomRules();
        rules = rules.concat(customRules);
      }
      return rules;
    },
    onBlur() {
      this.emitBlur();
    },
    emitBlur(data) {
      this.$emit('blur', {
        $vue: this,
        ...data
      });
      // 触发字段变更校验
      if (this.$refs[this.fieldCode] && this.$refs[this.fieldCode].onFieldBlur) {
        this.$refs[this.fieldCode].onFieldBlur();
      }
    },
    emitComplete() {
      this.$emit('complete', this);
    },

    emitChange(data, validate = true) {
      this.$emit('change', { $vue: this, ...data });
      // 触发字段变更校验
      if (this.rootWidgetDyformContext && this.rootWidgetDyformContext.isVersionDataView === true) {
        return;
      }
      // 从表内代理行数据组件(用于分页数据校验)，设值不触发字段变更校验
      if (this.$attrs.labelHidden) {
        return;
      }
      if (validate && this.$refs[this.fieldCode] && this.$refs[this.fieldCode].onFieldChange) {
        this.$refs[this.fieldCode].onFieldChange();
      }
    },
    setVisible(visible = true) {
      if (typeof visible !== 'boolean' && visible.eventParams != undefined) {
        // 由事件传递进来的参数
        visible = visible.eventParams.visible !== 'false';
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
      if (!this.editable && this.widget.configuration.hasOwnProperty('uneditableDisplayState')) {
        this.readonly = this.widget.configuration.uneditableDisplayState === 'readonly';
        this.displayAsLabel = this.widget.configuration.uneditableDisplayState === 'label';
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

    getCurrentState() {
      return {
        hidden: this.hidden,
        editable: this.editable,
        displayAsLabel: this.displayAsLabel,
        disable: this.disable,
        readonly: this.readonly
      };
    },

    // 组件状态变更时触发的逻辑
    afterDisplayStateChanged() { },

    // 可编辑
    setEditable(editable = true) {
      if (typeof editable !== 'boolean' && editable.eventParams != undefined) {
        // 由事件传递进来的参数
        editable = editable.eventParams.editable !== 'false';
      }
      this.hidden = false;
      this.hiddenByRule = undefined;
      let oldState = this.getCurrentState();
      this.editable = editable;
      this.displayAsLabel = !editable; // 默认情况下，不可编辑就是展示为文本
      this.readonly = false;
      this.disable = false;
      // 不可编辑情况下，如果组件配置存在不可编辑的展示状态设值，应该按设值的状态显示
      if (!this.editable && this.widget.configuration.hasOwnProperty('uneditableDisplayState')) {
        this.readonly = this.widget.configuration.uneditableDisplayState === 'readonly';
        this.displayAsLabel = this.widget.configuration.uneditableDisplayState === 'label';
      }
      this.$emit('stateChange', this.getCurrentState());
      this.afterDisplayStateChanged(oldState, this.getCurrentState());
    },

    // 禁用
    setDisable(disable = true) {
      let oldState = this.getCurrentState();
      this.disable = disable;
      this.editable = !disable;
      this.readonly = false;
      this.displayAsLabel = false;
      this.$emit('stateChange', this.getCurrentState());
      this.afterDisplayStateChanged(oldState, this.getCurrentState());
    },

    setDisplayAsLabel(asLabel = true) {
      let oldState = this.getCurrentState();
      this.displayAsLabel = asLabel;
      this.editable = !asLabel;
      this.readonly = false;
      this.disable = false;
      this.$emit('stateChange', this.getCurrentState());
      this.afterDisplayStateChanged(oldState, this.getCurrentState());
    },

    setRequired(required = true) {
      if (typeof required !== 'boolean' && required.eventParams != undefined) {
        // 由事件传递进来的参数
        required = required.eventParams.required === 'true';
      }
      let originalRequired = this.required;
      this.required = required;
      this.rules.splice(0, this.rules.length);
      this.rules.push(...this.initValidateRules(required));
      if (!this.designMode) {
        // 在设计时，修改该值会影响组件配置页的必填开关，所以不修改
        this.widget.configuration.required = this.required;
      }

      // 向上通知子元素的必填性变更通知
      if (this.parent && this.parent.id) {
        let getParent = parent => {
          if (parent && parent.id) {
            if (parent.wtype == 'WidgetFormItem') {
              this.form.emitEvent('onChildRequiredChanged.' + parent.id, required, this.widget.id);
            } else {
              let _parent = this.pageContext.getVueWidgetById(parent.id);
              if (_parent && _parent.parent && _parent.parent.id) {
                getParent(_parent.parent);
              }
            }
          }
        };
        getParent(this.parent);
      }
      // 必填改为非必填，并且有必填错误校验信息存在，则清除掉
      if (
        originalRequired &&
        !required &&
        this.$refs[this.fieldCode] &&
        this.$refs[this.fieldCode].validateState == 'error' &&
        (this.formData[this.fieldCode] == undefined || this.formData[this.fieldCode] == '' || this.formData[this.fieldCode].length == 0)
      ) {
        this.clearValidate();
      }

      this.$emit('requiredChange', this.required);
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
      if (this.$refs[this.fieldCode]) {
        this.$refs[this.fieldCode].clearValidate();
        this.$emit('validateStatusChange');
      }
    },

    /**
     * 触发字段的校验
     */
    emitValidate() {
      try {
        if (this.$refs[this.fieldCode]) {
          this.$refs[this.fieldCode].onFieldChange(); // 触发字段必填性校验
        }
      } catch (error) { }
    },

    //值设置或者获取控件当前值
    setValue(val) {
      if (val !== undefined && !isEmpty(val) && val.eventParams != undefined) {
        // 由事件传递进来的参数
        val = val.eventParams.value;
      }
      this.form.formData[this.fieldCode] = val;
      this.clearValidate();
      this.afterSetValue();
      this.emitChange();
    },

    setValueByEventParams(evt) {
      if (evt != undefined && !isEmpty(evt) && evt.eventParams != undefined) {
        this.setValue(evt.eventParams.value);
        this.emitChange();
      }
    },

    afterSetValue() { },

    getValue() {
      return this.form.formData[this.fieldCode];
    },

    isEmptyValue() {
      return this.getValue() == undefined || this.getValue() === '' || this.getValue() == null || this.getValue().length == 0;
    },

    // 显示值
    displayValue() {
      return this.form.formData[this.fieldCode];
    },

    // 判断数据唯一性
    uniqueCheck(callback, params, unitUnique = false) {
      let fieldValue = this.getValue();
      if (params == undefined) {
        if (fieldValue) {
          fieldValue = fieldValue + '';
        }
        params = { fieldName: this.fieldCode, fieldValue: fieldValue, tblName: this.form.tableName, uuid: this.formData.uuid };
      }
      // 从表内数据，需判断当前从表数据该项有没有重复值
      let isUniq = true;
      if (
        fieldValue &&
        (this.$attrs.isSubformCell ||
          this.$attrs.labelHidden ||
          (this.form && this.form.$form.$parent && this.form.$form.$parent.isSubform))
      ) {
        let $dyform = undefined;
        if (this.$attrs.isSubformCell || this.$attrs.labelHidden) {
          $dyform = this.dyform;
        } else if (this.form && this.form.$form.$parent && this.form.$form.$parent.isSubform) {
          $dyform = this.form.$form.$parent.rootWidgetDyformContext && this.form.$form.$parent.rootWidgetDyformContext.dyform;
        }
        if ($dyform) {
          if ($dyform.subform && $dyform.subform[this.form.formUuid]) {
            let subformData = [];
            each($dyform.subform[this.form.formUuid], item => {
              if (item.row && item.row[this.fieldCode]) {
                subformData.push(item.row);
              }
            });
            // 打开表单编辑，需要加入当前表单值
            if (
              !(this.$attrs.isSubformCell || this.$attrs.labelHidden) &&
              this.form &&
              this.form.$form.$parent &&
              this.form.$form.$parent.isSubform
            ) {
              let currentEditRowKey = $dyform.$subform[this.form.formUuid].currentEditRowKey;
              if (currentEditRowKey) {
                let hasIndex = findIndex(subformData, { $$id: currentEditRowKey });
                if (hasIndex > -1) {
                  subformData[hasIndex] = this.formData;
                } else {
                  subformData.push(this.formData);
                }
              }
            }
            let uniqData = uniqBy(subformData, this.fieldCode); // 除去重复项
            isUniq = subformData.length == uniqData.length; // 去重后length与从表length一致，则无重复项
          }
        }
      }
      if (isUniq) {
        $axios.post('/pt/dyform/data/validate/exists', { ...params, unitUnique }).then(({ data }) => {
          callback(data.data);
        });
      } else {
        callback(true);
      }
    },

    focus() {
      let _this = this;
      setTimeout(function () {
        let input = _this.$el.querySelector('input');
        if (input) {
          input.focus();
        }
      }, 200);
    },
    // 获取字段数据选项
    getFieldDataOptions() {
      const _this = this;
      let fieldName = _this.fieldCode;
      let formUuid = _this.form.formUuid;
      let dataUuid = this.formData.uuid;
      return {
        getOptions: function (key) {
          let realKey = formUuid + '.' + dataUuid + '.' + fieldName + '.' + key;
          return _this.form.dyformDataOptions[realKey];
        },
        putOptions: function (key, value) {
          let realKey = formUuid + '.' + dataUuid + '.' + fieldName + '.' + key;
          _this.form.dyformDataOptions[realKey] = value;
        }
      };
    },
    fireDefineDocumentEvent(evt, e) {
      evt = cloneDeep(evt);
      let containWidgetEvent = false;
      if (Array.isArray(evt.codeSource) && evt.codeSource.includes('widgetEvent')) {
        // 剔除组件事件，避免 executeEvent 重复执行
        evt.codeSource.splice(evt.codeSource.indexOf('widgetEvent'), 1);
        containWidgetEvent = true;
      }
      if (
        (typeof evt.codeSource == 'string' && evt.codeSource !== 'widgetEvent') ||
        (Array.isArray(evt.codeSource) && !evt.codeSource.includes('widgetEvent') && evt.codeSource.length > 0)
      ) {
        this.executeEvent(evt, { originEvent: e });
      }
      if (containWidgetEvent) {
        evt.codeSource = 'widgetEvent';
      }
      if (evt.codeSource == 'widgetEvent' || evt.codeSource.includes('widgetEvent')) {
        try {
          let widgetEvent = evt.widgetEvent;
          if (widgetEvent) {
            let events = [widgetEvent];
            if (Array.isArray(widgetEvent)) {
              events = widgetEvent;
            }
            events.forEach(e => {
              let executeEvent = true;
              if (e.condition && e.condition.enable && e.condition.conditions.length > 0) {
                // 判断条件是否成立
                let { conditions, match } = e.condition;
                executeEvent = match == 'all';
                for (let c = 0, clen = conditions.length; c < clen; c++) {
                  let { code, operator, value } = conditions[c];
                  // if (/\$\{.+\}/.test(value)) {
                  //   // 字段编码占位符解析
                  //   value = stringTemplate(value, {
                  //     ...this.formData,
                  //   })
                  // }
                  let isTrue = expressionCompare(
                    { ...this.formData, __DYFORM__: { editable: this.form.displayState === 'edit' } },
                    code,
                    operator,
                    value
                  );
                  if (match == 'any') {
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
                    eventId == 'setEditable' &&
                    /**
                     * 字段不可编辑情况下，触发字段能进行可编辑状态切换的前提:
                     * 1. 当前整体可编辑
                     * 2. 无规则限制字段不可编辑
                     *
                     * 否则要把编辑切换修改为不可编辑情况进行展示
                     */
                    (this.form.displayState !== 'edit' ||
                      (this.form.formElementRules &&
                        this.form.formElementRules[eventWid] &&
                        this.form.formElementRules[eventWid].editable !== true))
                  ) {
                    let paramGet = false;
                    eventParams = JSON.parse(JSON.stringify(eventParams || []));
                    if (eventParams) {
                      for (let i = 0, len = eventParams.length; i < len; i++) {
                        if (eventParams[i].paramKey == 'editable') {
                          eventParams[i].paramValue = 'false';
                          paramGet = true;
                          break;
                        }
                      }
                    }
                    if (!paramGet) {
                      eventParams.push({
                        paramKey: 'editable',
                        paramValue: 'false'
                      });
                    }
                    eventId = 'setEditable';
                  }
                  let handler = {
                    actionType: 'widgetEvent',
                    $evtWidget: this,
                    meta: this.formData,
                    eventWid,
                    eventId,
                    eventParams,
                    wEventParams,
                    pageContext: this.form
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
          console.error('执行组件事件异常', error);
        }
      }
    },
    triggerOnChange(evt, e) {
      if (evt) {
        this.fireDefineDocumentEvent(evt, e);
      }
    },
    triggerFormElementDomEvent(eventName, e) {
      if (this.widget.configuration.domEvents != undefined) {
        for (let i = 0, len = this.widget.configuration.domEvents.length; i < len; i++) {
          let evt = this.widget.configuration.domEvents[i];
          if (evt.id == 'onChange') {
            this.triggerOnChange(evt, e);
            continue;
          }
          if (evt.id == eventName) {
            this.fireDefineDocumentEvent(evt, e);
          }
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
        this.widget.configuration.formula.items.forEach(i => {
          if (i.type == 'js-api') {
            obj[i.value.substr(2, i.value.length - 3)] = undefined;
          }
        });
      }
      return obj;
    }
  }
};
