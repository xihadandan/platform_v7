import { expressionCompare, deepClone } from '@framework/vue/utils/util';
import { template as stringTemplate, isEmpty, debounce } from 'lodash';
import md5 from '@framework/vue/utils/md5';
import * as formulaApi from '../../framework/vue/dyform/formula-api.js';
export default {
  inject: ['widgetSubformContext', 'dyform', 'widgetDyformContext', 'pageContext'],
  props: {
    widget: Object,
    widgetSubform: Object,
    editMode: String,
    row: Object,
    rowIndex: Number,
    form: Object,
    formDefaultData: Object,
    jsModule: String,
    option: Object
  },
  computed: {
    vTableButtonVisibleJudgementData() {
      let __TABLE__ = {};
      __TABLE__.selectedRowCount = this.widgetSubformContext.selectedRowKeys.length;
      return {
        MAIN_FORM_DATA: this.widgetSubformContext.form.formData,
        __TABLE__
      };
    },
    columnConfig() {
      for (let i = 0, len = this.widgetSubform.configuration.columns.length; i < len; i++) {
        if (this.widgetSubform.configuration.columns[i].widget.id === this.widget.id) {
          return this.widgetSubform.configuration.columns[i];
        }
      }
      return null;
    },
    colValueChangeDomEvent() {
      if (this.columnConfig && this.columnConfig.domEvents != undefined) {
        for (let i = 0, len = this.columnConfig.domEvents.length; i < len; i++) {
          if (this.columnConfig.domEvents[i].id == 'onChange') {
            return this.columnConfig.domEvents[i];
          }
        }
      }
      return undefined;
    },

    dataIndex() {
      return this.widget.configuration.code;
    },
    calculateExpression() {
      let expression = [];
      if (this.option.formula != undefined && this.option.formula.enable) {
        this.option.formula.items.forEach(i => {
          expression.push(i.value);
        });
      }
      return expression.join('');
    },
    relaFormulaFields() {
      let field = {
        field: [],
        subformField: []
      };
      if (this.option.formula != undefined && this.option.formula.enable && this.option.formula.items.length > 0) {
        this.option.formula.items.forEach(i => {
          if (i.dataIndex != undefined) {
            if (i.value.startsWith('${__SUBFORM_DATA__.')) {
              // 当前从表表字段
              field.subformField.push(i.dataIndex);
            } else if (i.type == 'js-api' && i.apiCode != undefined) {
              // 时间差 api，要判断字段是否是从表内，还是主表上
              let _dataIndex = Array.isArray(i.dataIndex) ? i.dataIndex : [i.dataIndex];
              _dataIndex.forEach(d => {
                if (d.startsWith('SUBFORM.')) {
                  field.subformField.push(d.replace('SUBFORM.', ''));
                } else {
                  field.field.push(d);
                }
              });
            } else {
              if (Array.isArray(i.dataIndex)) {
                field.field.push(...i.dataIndex);
              } else {
                field.field.push(i.dataIndex);
              }
            }
          }
        });
      }
      return field;
    },

    formulaValueDataMD5() {
      if (this.calculateExpression != '') {
        let data = { __SUBFORM_DATA__: {}, ...this.formulaApiData },
          mainFormData = this.dyform.formData;
        if (this.relaFormulaFields.subformField.length > 0) {
          for (let k in this.form.formData) {
            if (k != this.dataIndex) {
              // 要排除当前字段值，否则会当该字段被计算变更后会再次触发一次计算
              if (this.relaFormulaFields.subformField.includes(k)) {
                data.__SUBFORM_DATA__[k] = this.form.formData[k];
              }
            }
          }
        }
        if (mainFormData != undefined && this.relaFormulaFields.field.length > 0) {
          for (let k in mainFormData) {
            if (this.relaFormulaFields.field.includes(k)) {
              data[k] = mainFormData[k];
            }
          }
        }
        let str = md5(JSON.stringify(JSON.stringify(data)));
        console.log(this.dataIndex, 'formulaValueDataMD5', str, this.relaFormulaFields);
        return str;
      }
      return '-1';
    }
  },
  data() {
    return {
      formulaApiData: this.initFormulaApiData()
    };
  },

  created() {
    if (this.jsModule != undefined && this.__developScript[this.jsModule]) {
      this.$pageJsInstance = new this.__developScript[this.jsModule].default(this);
      this._provided.$pageJsInstance = this.$pageJsInstance;
      this._provided.$pageJsInstance._JS_META_ = this.jsModule;

      // 二开中的对象调用切换到该组件实例上
      this.dyform = this.form;
      this.wDyform = this; // TODO：后续要实现 widget-dyform 在二开上支持调用的方法
    }
    if (this.colValueChangeDomEvent != undefined && this.colValueChangeDomEvent.enable) {
      // 去除从表字段组件上的值变更事件
      let domEvents = this.widget.configuration.domEvents;
      if (domEvents != undefined) {
        for (let i = 0; i < domEvents.length; i++) {
          if (domEvents[i].id == 'onChange') {
            domEvents.splice(i, 1);
            break;
          }
        }
      }
    }
    this.setFormulaCalculateFieldValue = debounce(this.setFormulaCalculateFieldValue.bind(this), 300);

    if (
      this.widget.configuration.requiredCondition &&
      this.widget.configuration.requiredCondition.enable &&
      this.widget.configuration.requiredCondition.conditions.length > 0
    ) {
      this.widget.configuration.required = true;
      //如果规则传递了必填性要重置掉，如果不重置则会导致必填性条件无效
      if (this.form.formElementRules[this.widget.id].required === false) {
        this.form.formElementRules[this.widget.id].required = undefined;
      }
    }
  },

  methods: {
    $t() {
      if (this.widgetSubformContext != undefined) {
        return this.widgetSubformContext.$t(...arguments);
      }
      return this.$i18n.t(...arguments);
    },
    fireEvent(evt) {
      if (evt.codeSource == 'widgetEvent') {
        let metaData = { CURRENT_ROW: this.form.formData, ...this.dyform.formData };
        let widgetEvent = evt.widgetEvent;
        if (widgetEvent) {
          let events = [widgetEvent];
          if (Array.isArray(widgetEvent)) {
            events = widgetEvent;
          }
          events.forEach(eventItem => {
            let executeEvent = true;
            if (eventItem.condition && eventItem.condition.enable && eventItem.condition.conditions.length > 0) {
              // 判断条件是否成立
              let { conditions, match } = eventItem.condition;
              executeEvent = match == 'all';
              for (let c = 0, clen = conditions.length; c < clen; c++) {
                let { code, operator, value } = conditions[c];
                let isTrue = expressionCompare(metaData, code, operator, value);
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
              let eventArray = eventItem.event != undefined ? eventItem.event : [eventItem];
              for (let i = 0; i < eventArray.length; i++) {
                let { eventId, eventParams, eventWid, wEventParams } = eventArray[i];
                try {
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
                          eventParams[i].paramValue =
                            eventParams[i].paramValue == 'true'
                              ? this.editable === false || this.form.displayState !== 'edit'
                                ? 'false'
                                : 'true'
                              : eventParams[i].paramValue;
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
                    meta: metaData,
                    eventWid,
                    eventId,
                    eventParams,
                    wEventParams,
                    pageContext: this.form
                  };

                  // 判断派发的组件是否从表的列组件，如果不是，则说明派发到页面级组件上
                  if (!this.widgetSubformContext.isColumnWidget(eventWid)) {
                    handler.pageContext = this.pageContext;
                  }

                  this.$refs.widget.dispatchEventHandler(handler);
                } catch (error) {
                  console.error(error);
                }
              }
            }
          });
        }
      } else {
        this.$refs.widget.executeEvent(evt, {
          $widget: this.$refs.widget,
          $widgetSubform: this.widgetSubformContext,
          $widgetDyform: this.widgetDyformContext,
          currentSubForm: this.form
        });
      }
    },
    onValueChange({ newValue, oldValue }) {
      this.dyform.emitEvent('formDataChanged', true);
      if (this.colValueChangeDomEvent && this.colValueChangeDomEvent.enable) {
        this.fireEvent(this.colValueChangeDomEvent);
      }
      this.dyform.emitEvent(`${this.widgetSubformContext.widget.id}:${this.dataIndex}:valueChange`, {
        $subform: this.widgetSubformContext,
        dataIndex: this.dataIndex,
        newValue,
        oldValue,
        rowData: this.row,
        rowKey: this.row['$$id']
      });
    },

    getFormulaCalculateValue() {
      let v = null;
      if (this.option.formula != undefined) {
        try {
          console.log(this.dataIndex + ' 计算公式表达式: ', this.calculateExpression);
          let compiler = stringTemplate(this.calculateExpression);
          v = compiler({ __SUBFORM_DATA__: { ...this.form.formData }, ...this.dyform.formData, ...this.formulaApiData });
          console.log(this.dataIndex + ' 执行计算公式结果: ', v);
          v = new Function('return ' + v)();
          if (Number.isNaN(v)) {
            v = null;
          }
          if (this.option.formula.toFixedNumber != undefined) {
            v = parseFloat(v.toFixed(this.option.formula.toFixedNumber));
          }
        } catch (error) {
          console.warn('计算公式输出结果失败: ', error);
          v = null;
        }
      }

      return v;
    },
    setFormulaCalculateFieldValue() {
      if (this.option.formula != undefined && this.option.formula.enable && this.option.formula.items.length > 0) {
        this.option.formula.items.forEach(i => {
          if (i.type == 'js-api') {
            let varName = i.value.substr(2, i.value.length - 3),
              apiCode = i.apiCode.split('.');
            if (formulaApi[apiCode[0]] != undefined && typeof formulaApi[apiCode[0]][apiCode[1]] == 'function') {
              let result = formulaApi[apiCode[0]][apiCode[1]].call(this, {
                form: this.widgetDyformContext.dyform, // 从表的上级主表
                subform: this.form,
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

      let v = this.getFormulaCalculateValue();
      if (v != this.form.formData[this.dataIndex]) {
        this.form.setFieldValue(this.dataIndex, v);
      }
    },

    initFormulaApiData() {
      let obj = {};
      if (this.option.formula != undefined && this.option.formula.enable && this.option.formula.items.length > 0) {
        this.option.formula.items.forEach(i => {
          if (i.type == 'js-api') {
            obj[i.value.substr(2, i.value.length - 3)] = undefined;
          }
        });
      }
      return obj;
    }
  },

  mounted() {
    this.$watch('formulaValueDataMD5', () => {
      this.setFormulaCalculateFieldValue();
    });
    // 挂载成功，判断是否有关联计算字段，存在则计算初始值
    if (this.relaFormulaFields.field.length + this.relaFormulaFields.subformField.length > 0) {
      this.setFormulaCalculateFieldValue();
    }
  }
};
