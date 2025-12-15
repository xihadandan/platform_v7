/**
 * 表单对象
 * @param {vue 实例}} vueInstance
 * @returns
 */
export function createForm() {
  return {

    dataUuid: null,


    displayState: 'edit',

    /**
     * 表单数据：
     * {
     *    字段1 : 字段值
     *    ...
     * }
     */
    formData: {},

    /**
     * 表单字段对象实例
     */
    $fieldset: {},

    // 表单控件规则：可以通过该数据对组件的编辑、只读或者必填的规则进行修改
    formElementRules: {},




    readonly() {
      this.displayState = 'readonly';
    },

    disable() {
      this.displayState = 'disable';
    },

    editable() {
      this.displayState = 'edit';
    },

    setFieldFormItemVisible(fieldName, visible) {

    },

    setFieldValue(fieldName, value, emitChange = true) {
      if (this.$fieldset && this.$fieldset[fieldName]) {
        this.$fieldset[fieldName].setValue(value);
        if (emitChange) {
          this.$fieldset[fieldName].emitChange(); // 触发数据变更通知
        }
      } else {
        // 可能并没有初始化的情况下，直接赋值表单数据
        Vue.set(this.formData, fieldName, value);
      }
    },

    getFieldValue(fieldName) {
      let $field = this.$fieldset[fieldName];
      if ($field) {
        return $field.getValue();
      }
      return this.formData[fieldName];
    },

    getField(fieldName) {
      return this.$fieldset[fieldName];
    },

    getFieldState(fieldName) {
      if (this.$fieldset[fieldName]) {
        return this.$fieldset[fieldName].getCurrentState();
      }
      return {};
    },



    setFieldVisible(fieldName, visible) {
      let $field = this.$fieldset[fieldName];
      if ($field) {
        $field.setVisible(visible);
      }
    },

    setFieldReadOnly(fieldName, readonly = true) {
      let $field = this.$fieldset[fieldName];
      if ($field) {
        $field.setReadonly(readonly);
      }
    },

    setFieldEditable(fieldName, editable = true) {
      let $field = this.$fieldset[fieldName];
      if ($field) {
        $field.setEditable(editable);
      }
    },

    setFieldDisable(fieldName, disable = true) {
      let $field = this.$fieldset[fieldName];
      if ($field) {
        $field.setDisable(disable);
      }
    },

    setFieldRequired(fieldName, required = true) {
      let $field = this.$fieldset[fieldName];
      if ($field) {
        $field.setRequired(required);
      }
    },

    setFieldDisplayAsLabel(fieldName) {
      let $field = this.$fieldset[fieldName];
      if ($field) {
        $field.setDisplayAsLabel();
      }
    },




    emitEvent() {
      //用于兄弟组件发射事件
      if (this.vueInstance) {
        this.vueInstance.$emit.apply(this.vueInstance, arguments);
      }
    },

    offEvent(evtName) {
      if (this.vueInstance) {
        this.vueInstance.$off(evtName);
      }
    },

    handleEvent(evtName, callback) {
      //用于兄弟组件接收事件
      if (this.vueInstance) {
        this.vueInstance.$on(evtName, function () {
          callback.apply(this, arguments);
        });
      }
    },

    validate(callback) {
      if (this.$form) {
        this.$form.validate(function (valid, messages) {
          if (typeof callback === 'function') {
            callback(valid, messages);
          }
        });
      }
    },

    clearValidate() {
      if (this.$form) {
        this.$form.clearValidate();
      }
    },


  };
}
