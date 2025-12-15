<template>
  <a-form-model
    :class="['widget-form']"
    :layout="widget.configuration.layout"
    :colon="widget.configuration.colon"
    :model="form"
    ref="form"
    :label-col="widget.configuration.layout === 'horizontal' ? widget.configuration.labelCol : null"
    :wrapper-col="widget.configuration.layout === 'horizontal' ? widget.configuration.wrapperCol : null"
  >
    <template v-for="(wgt, i) in widget.configuration.widgets">
      <component
        :key="wgt.id"
        :is="resolveWidgetType(wgt)"
        :widget="wgt"
        :index="i"
        :widgetsOfParent="widget.configuration.widgets"
        :parent="widget"
      ></component>
    </template>
  </a-form-model>
</template>
<script type="text/babel">
import widgetMixin from '@framework/vue/mixin/widgetMixin';
import { getJsonValue, deepClone } from '@framework/vue/utils/util';
import { createForm } from './form.js';
import { DispatchEvent } from '@framework/vue/lib/dispatchEvent';
import { debounce } from 'lodash';

export default {
  name: 'WidgetForm',
  mixins: [widgetMixin],
  inject: ['$event'],
  data() {
    let formConfiguration = { layout: this.widget.configuration.layout };
    let form = {};
    if (this.widget.configuration.formDataFrom == 'eventMetaData' && this.$event != undefined) {
      if (this.$event.meta != undefined) {
        Object.assign(form, this.$event.meta);
      }
      if (this.widget.configuration.explainFormDataFromEventParams && this.$event.eventParams != undefined) {
        for (let k in this.$event.eventParams) {
          if (k.startsWith('formData.')) {
            form[k.split('formData.')[1].trim()] = this.$event.eventParams[k];
          }
        }
      }
    }

    let formHandler = createForm(this);
    formHandler.formData = form;
    return {
      form,
      formHandler,
      formConfiguration,
      formCodeWidgetConfigures: {},
      formElementRules: {}
    };
  },
  watch: {},
  beforeCreate() {},
  provide() {
    return {
      form: this.form,
      formHandler: this.formHandler,
      formCodeWidgetConfigures: this.formCodeWidgetConfigures,
      formConfiguration: this.formConfiguration,
      widgetFormContext: this,
      formElementRules: {}
    };
  },
  computed: {
    defaultEvents() {
      return [
        { id: 'submitForm', title: '提交表单' },
        { id: 'validateForm', title: '校验表单' },
        { id: 'resetForm', title: '重置表单' }
      ];
    }
  },
  created() {},
  methods: {
    setField(fieldName, fieldValue) {
      if (this.form.hasOwnProperty(fieldName)) {
        this.form[fieldName] = fieldValue;
      } else {
        this.$set(this.form, fieldName, fieldValue);
      }
    },
    setFormData(formData) {
      for (let k in formData) {
        this.$set(this.form, k, formData[k]);
      }
    },
    clearValidate() {
      this.$refs.form.clearValidate();
    },
    resetForm() {
      this.$refs.form.resetFields();
    },
    setFormData(formData) {
      if (formData) {
        for (let k in formData) {
          this.form[k] = formData[k];
        }
      }
    },
    doSubmitWrapper(action, formData, data = {}) {
      if (action != undefined) {
        if (action.type == 'codeEditor') {
          this.executeCodeSegment(action.customScript, { form: formData, data }, this);
        } else if (action.type == 'widgetEvent') {
          new DispatchEvent({
            $evtWidget: this,
            actionType: 'widgetEvent',
            meta: this.form,
            eventParams: action.eventParams,
            eventWid: action.widgetId,
            eventId: action.widgetEventId,
            pageContext: this.pageContext
          }).dispatch();
        } else if (action.type == 'developJsFileCode' && action.jsFunction != undefined) {
          this.invokeJsFunction(action.jsFunction);
        }
      }
    },
    submitForm() {
      let { url, mask, submitType, beforeSubmit, afterSubmit } = this.widget.configuration.submitAction,
        _this = this;
      let formData = deepClone(this.form);
      if (submitType == 'remote') {
        this.doSubmitWrapper(beforeSubmit, formData, undefined);
      }
      return new Promise((resolve, reject) => {
        this.validateFormData((vali, msg) => {
          if (vali) {
            if (url && submitType === 'remote') {
              if (mask) {
                _this.$loading('保存中');
              }
              $axios
                .post(url, formData)
                .then(({ data }) => {
                  if (mask) {
                    _this.$loading(false);
                  }
                  _this.doSubmitWrapper(afterSubmit, undefined, data);
                  resolve();
                })
                .catch(error => {
                  reject(error);
                  _this.$loading(false);
                });
            } else if (submitType == 'local') {
              _this.executeEvent(this.widget.configuration.submitAction, { $widget: _this, form: _this.form });
              resolve();
            }
          }
        });
      });
    },
    getFieldConfiguration(field) {
      return this.formCodeWidgetConfigures[field];
    },

    setFieldReadOnly(field, readOnly = true) {
      this.formHandler.setFieldReadOnly(field, readOnly);
    },
    validateFormData(callback) {
      this.$refs.form.validate((vali, msg) => {
        callback(vali, msg);
      });
    },
    afterFormDataChanged: debounce(function (v, o) {
      this.invokeDevelopmentMethod('afterFormDataChanged', v, o);
    }, 500),
    requiredHook() {
      let _this = this;
      for (let i = 0, len = this.widget.configuration.widgets.length; i < len; i++) {
        let item = this.widget.configuration.widgets[i];
        this.formHandler.handleEvent('onChildRequiredChanged.' + item.id, function (required, id) {
          item.configuration.required = required;
        });
      }
    }
  },
  beforeMount() {
    this.formHandler.vueInstance = this;
    this.formHandler.$form = this.$refs.form;
    if (this.widget.configuration.formDataFrom == 'jsMethod' && this.widget.configuration.formDataFromJsFunction != undefined) {
      let result = this.invokeJsFunction(this.widget.configuration.formDataFromJsFunction);
      if (result instanceof Promise) {
        result.then(d => {
          this.setFormData(d);
        });
      } else if (typeof result == 'object') {
        this.setFormData(result);
      }
    }

    // 兼容旧配置：
    if (this.widget.configuration.submitAction.afterSubmit == undefined && this.widget.configuration.submitAction.after != undefined) {
      this.widget.configuration.submitAction.afterSubmit = {
        type: 'codeEditor',
        customScript: this.widget.configuration.submitAction.after
      };
    }

    if (this.widget.configuration.submitAction.beforeSubmit == undefined && this.widget.configuration.submitAction.before != undefined) {
      this.widget.configuration.submitAction.beforeSubmit = {
        type: 'codeEditor',
        customScript: this.widget.configuration.submitAction.before
      };
    }
  },
  mounted() {},
  destroyed() {},
  watch: {
    form: {
      deep: true,
      handler(v, o) {
        this.afterFormDataChanged(v, o);
        this.formHandler.emitEvent('formDataChanged');
      }
    }
  }
};
</script>
