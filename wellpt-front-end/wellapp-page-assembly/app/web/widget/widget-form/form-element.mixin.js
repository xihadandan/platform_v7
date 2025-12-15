import widgetMixin from '@framework/vue/mixin/widgetMixin';
import { expressionCompare } from '@framework/vue/utils/util';
import './css/index.less';
import { debounce } from 'lodash';
export default {
  name: 'FormElementMixin',
  mixins: [widgetMixin],
  inject: ['form', 'formConfiguration', 'formCodeWidgetConfigures', 'formHandler', 'widgetFormContext'],
  data() {
    let rules = this.initValidateRules(this.widget.configuration.required);
    let data = {
      editable: this.widget.configuration.defaultDisplayState == undefined || this.widget.configuration.defaultDisplayState == 'edit',
      displayAsLabel:
        this.widget.configuration.defaultDisplayState === 'unedit' && this.widget.configuration.uneditableDisplayState == 'label', // 初始化展示为不可编辑的文本展示
      readonly:
        this.widget.configuration.defaultDisplayState === 'unedit' && this.widget.configuration.uneditableDisplayState == 'readonly',
      disable: this.widget.configuration.defaultDisplayState === 'unedit',
      hidden: this.widget.configuration.defaultDisplayState === 'hidden',
      rules,
      fieldCode: this.widget.configuration.code,
      required: this.widget.configuration.required || false
    };
    if (this.designMode) {
      // 设计器模型下默认都是可编辑效果
      data.editable = true;
    }
    return data;
  },

  beforeCreate() {},
  components: {},
  computed: {
    fieldName() {
      return this.$t(this.widget.id, this.widget.configuration.title);
    },
    itemStyle() {
      let style = {};
      // if (!this.designMode) {
      style.display =
        this.formConfiguration && this.formConfiguration.layout === 'horizontal'
          ? this.widget.configuration.style && this.widget.configuration.style.block === false
            ? 'inline-flex'
            : 'flex'
          : this.widget.configuration.style && this.widget.configuration.style.block === false
          ? 'inline-block'
          : 'block';
      if (this.widget.configuration.style) {
        style.width =
          typeof this.widget.configuration.style.width === 'number'
            ? this.widget.configuration.style.width + 'px'
            : this.widget.configuration.style.width;
        // }

        if (!this.designMode && this.hidden) {
          style.display = 'none';
        }
      }
      return style;
    }
  },
  created() {
    if (this.form == undefined) {
      this.form = {};
    }
    this.formHandler.$fieldset[this.widget.configuration.code] = this;
    if (this.form && this.widget.configuration.code && !this.form.hasOwnProperty(this.widget.configuration.code)) {
      this.$set(this.form, this.widget.configuration.code, undefined);
      this.formCodeWidgetConfigures[this.widget.configuration.code] = this.widget;
    }
  },
  mounted() {
    this.initEvaluateRequiredHook();
    // 监听值变更
    this.$watch('form.' + this.widget.configuration.code, (newValue, oldValue) => {
      this.triggerOnValueChange(newValue, oldValue);
    });
    this.initDomEvent();
  },
  methods: {
    // 初始化校验规则
    initValidateRules(required) {
      let _this = this,
        validateRule = _this.widget.configuration.validateRule;

      let rules = [],
        trigger = validateRule ? validateRule.trigger : 'blur',
        type = this.widget.configuration.dataType || 'string';
      if (required) {
        // 必填规则
        rules.push({
          required: true,
          type,
          message: this.widget.configuration.requiredMsg || `必填`,
          trigger,
          validator: function (rule, value, callback) {
            callback(!_this.isEmptyValue() ? undefined : new Error(rule.message));
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
            errorMsg = validateRule.errorMsg || '数据格式错误';
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
            validator: function (rule, value, callback) {
              let result = regExp.test(_this.getValue());
              regExp.lastIndex = 0;
              callback(result ? undefined : new Error(errorMsg));
            }
          });
        }

        // 校验函数
        let script = validateRule.script,
          _this = this;

        if (script) {
          rules.push({
            trigger,
            type,
            validator: debounce(function (rule, value, callback) {
              _this.pageContext.executeCodeSegment(
                script,
                {
                  callback: function (res) {
                    callback(res === undefined || res === true ? undefined : res);
                  }
                },
                _this
              );
            }, 300)
          });
        }

        // // 数据唯一性校验
        // let uniqueType = validateRule.uniqueType,
        //   uniqueMsg = validateRule.uniqueMsg;
        // if (uniqueType) {
        //   rules.push({
        //     trigger,
        //     type,
        //     validator: function (rule, value, callback) {
        //       if (uniqueType) {
        //       }
        //     }
        //   });
        // }
      }

      return rules;
    },
    getValue() {
      return this.form[this.widget.configuration.code];
    },

    setVisible(visible = true) {
      this.hidden = !visible;
      this.emitVisibleChange(!this.hidden);
      this.adjustElementWidth();
    },
    // 设置该组件只读状态
    setReadonly(readonly = true) {
      this.readonly = readonly;
      this.editable = !readonly;
      this.disable = false;
      this.displayAsLabel = false;
    },

    setValue(value) {
      this.$set(this.form, this.widget.configuration.code, value);
    },
    isEmptyValue() {
      return this.getValue() == undefined || this.getValue() === '' || this.getValue() == null || this.getValue().length == 0;
    },
    initEvaluateRequiredHook() {
      if (this.required && this.widget.configuration.requiredCondition && this.widget.configuration.requiredCondition.conditions.length) {
        let evaluateRequired = () => {
          let form = this.form;
          let { conditions, match } = this.widget.configuration.requiredCondition;
          for (let i = 0, len = conditions.length; i < len; i++) {
            let { code, operator, value } = conditions[i];
            let isTrue = expressionCompare(form, code, operator, value);
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
        this.formHandler.handleEvent('formChanged', () => {
          evaluateRequired();
        });
        // 初始化时进行条件判断
        evaluateRequired();
      }
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
        this.$refs[this.widget.configuration.code] &&
        this.$refs[this.widget.configuration.code].validateState == 'error' &&
        (this.form[this.widget.configuration.code] == undefined ||
          this.form[this.widget.configuration.code] == '' ||
          this.form[this.widget.configuration.code].length == 0)
      ) {
        this.clearValidate();
      }
    },
    clearValidate() {
      if (this.$refs[this.widget.configuration.code]) {
        this.$refs[this.widget.configuration.code].clearValidate();
      }
    },

    /**
     * 触发字段的校验
     */
    emitValidate() {
      try {
        if (this.$refs[this.widget.configuration.code]) {
          this.$refs[this.widget.configuration.code].onFieldChange(); // 触发字段必填性校验
        }
      } catch (error) {}
    },
    initDomEvent() {
      if (this.widget.configuration.code && this.widget.configuration.domEvents) {
        for (let i = 0, len = this.widget.configuration.domEvents.length; i < len; i++) {
          let evt = this.widget.configuration.domEvents[i];
          if (evt.id == 'onChange') {
            let { widgetEvent, jsFunction, customScript } = evt;
            if (widgetEvent.length > 0 || jsFunction != undefined || customScript != undefined) {
              if (this.form[this.fieldCode] != undefined && this.form[this.fieldCode] != null && this.form[this.fieldCode] != '') {
                this.triggerFormElementDomEvent('onChange', this.form);
              }
              break;
            }
          }
        }
      }
    },
    // 表单值变化
    triggerOnValueChange(newValue, oldValue) {
      // console.log(this.widget.configuration.code, '值变更')
      if ((newValue == undefined || newValue == null || newValue == '') && (oldValue == undefined || oldValue == null || oldValue == '')) {
        return;
      }
      // console.log(`${this.widget.configuration.name} - ${this.fieldCode} 字段发生了值变更`, oldValue, newValue)
      this.triggerFormElementDomEvent('onChange', this.form);

      this.$emit('valueChange', { newValue, oldValue });
    },
    fireDefineDocumentEvent(evt) {
      if (evt.codeSource == 'widgetEvent') {
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
                let isTrue = expressionCompare(this.form, code, operator, value);
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
              let { eventId, eventParams, eventWid, wEventParams } = e;
              if (!this.editable && eventId == 'setEditable') {
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
                meta: this.form,
                eventWid,
                eventId,
                eventParams,
                wEventParams,
                pageContext: this.pageContext
              };
              this.dispatchEventHandler(handler);
            }
          });
        }
      } else {
        this.executeEvent(evt);
      }
    },
    triggerOnChange(evt) {
      if (evt) {
        this.fireDefineDocumentEvent(evt);
      }
    },
    triggerFormElementDomEvent(eventName, data) {
      if (this.widget.configuration.domEvents != undefined) {
        for (let i = 0, len = this.widget.configuration.domEvents.length; i < len; i++) {
          let evt = this.widget.configuration.domEvents[i];
          evt.meta = data;
          if (evt.id == 'onChange') {
            this.triggerOnChange(evt);
            continue;
          }
          if (evt.id == eventName) {
            this.fireDefineDocumentEvent(evt);
          }
        }
      }
    }
  }
};
