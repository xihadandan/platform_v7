/**
 * 表单对象
 * @param {vue 实例}} vueInstance
 * @returns
 */
export function createDyform(formUuid, namespace) {
  return {
    formUuid: formUuid || "",

    dataUuid: null,

    tableName: null,

    namespace: namespace || "",

    displayState: "edit",

    formDefinitionJson: undefined,
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

    $subform: {},

    // 表单控件规则：可以通过该数据对组件的编辑、只读或者必填的规则进行修改
    formElementRules: {},

    // 表单数据选项
    dyformDataOptions: {},

    removeCache(key) {
      return this.$tempStorage != undefined
        ? this.$tempStorage.removeCache(key)
        : new Promise((resolve, reject) => {
            resolve();
          });
    },

    createTempDyform() {
      return createDyform(...arguments);
    },

    createRefDyForm(formId, formUuid) {
      if (this.refDyform == undefined) {
        this.refDyform = {};
      }
      let f = createDyform(formId);
      f.formUuid = formUuid;
      this.refDyform[formId] = f;
      return f;
    },

    deleteRefDyForm(formId) {
      if (this.refDyform != undefined) {
        delete this.refDyform[formId];
      }
    },

    createSubform(formUuid, namespace, alone) {
      /**
       * 从表对象
       * {
       *  从表UUID : [
       *    dyform 实例 , dyform 实例  , ...  // 每行一个独立的dyform实例
       *  ]
       * }
       */
      if (this.subform == undefined) {
        this.subform = {};
        this.deletedSubformData = {};
      }
      let subDyform = createDyform(formUuid, namespace);
      if (this.subform[formUuid] == undefined) {
        this.subform[formUuid] = [];
        this.deletedSubformData[formUuid] = [];
      }
      if (!alone) {
        this.subform[formUuid].push(subDyform);
      }
      return subDyform;
    },
    getSubforms(formUuid) {
      return this.subform ? this.subform[formUuid] : undefined;
    },
    initSubform(formUuid) {
      if (this.subform == undefined) {
        this.subform = {};
        this.deletedSubformData = {};
      }
      this.subform[formUuid] = [];
    },
    hasSubform() {
      return this.subform != undefined && Object.keys(this.subform).length > 0;
    },
    deleteSubform(formUuid, i) {
      if (this.subform[formUuid] != undefined) {
        let del = this.subform[formUuid].splice(i, 1);
        if (del[0].formData.uuid) {
          // 记录删除的从表数据
          if (this.deletedSubformData[formUuid] == undefined) {
            this.deletedSubformData[formUuid] = [];
          }
          this.deletedSubformData[formUuid].push(del[0].formData.uuid);
        }
      }
    },
    pushSubform(formUuid, subDyform) {
      if (this.subform == undefined) {
        this.subform = {};
        this.deletedSubformData = {};
      }
      if (this.subform[formUuid] == undefined) {
        this.subform[formUuid] = [];
      }
      this.subform[formUuid].push(subDyform);
    },

    swapSubform(id, i, j) {
      if (this.subform[id] != undefined) {
        let temp = this.subform[id].splice(i, 1)[0];
        this.subform[id].splice(j, 0, temp);
      }
    },

    readonly() {
      this.displayState = "readonly";
    },

    disable() {
      this.displayState = "disable";
    },

    editable() {
      this.displayState = "edit";
    },

    setFieldFormItemVisible(fieldName, visible) {
      let $field = this.$fieldset[fieldName];
      if ($field && $field.parent.wtype == "WidgetFormItem") {
        this.$fieldset[fieldName].parent.configuration.hidden = !visible;
      }
    },

    setFieldValue(fieldName, value, emitChange = true) {
      if (this.$fieldset && this.$fieldset[fieldName]) {
        this.$fieldset[fieldName].setValue(value);
        if (emitChange) {
          this.$fieldset[fieldName].emitChange(); // 触发数据变更通知
        }
      } else {
        // 可能并没有初始化的情况下，直接赋值表单数据
        this.vueInstance.$set(this.formData, fieldName, value);
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

    setText(textId, value) {
      this.$textset[textId].setValue(value);
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

    setSubformFieldVisible(formId, fieldName, visible = true) {
      let $subform = this.$subform[formId];
      if ($subform) {
        $subform.setColumnVisible(fieldName, visible);
      }
    },

    setSubformFieldEditable(formId, fieldName, editable = true) {
      let $subform = this.$subform[formId];
      if ($subform) {
        $subform.setColumnEditable(fieldName, editable);
      }
    },

    setSubformFieldRequired(formId, fieldName, required = true) {
      let $subform = this.$subform[formId];
      if ($subform) {
        $subform.setColumnRequired(fieldName, required);
      }
    },

    addSubformRows(formId, rows) {
      let $subform = this.$subform[formId];
      if ($subform) {
        for (let i = 0; i < rows.length; i++) {
          $subform.addRow(rows[i]);
        }
      }
    },

    getSubformRows(formId) {
      let $subform = this.$subform[formId];
      if ($subform) {
        return $subform.rows;
      }
      return undefined;
    },
    getSubformRowForm(formId, index) {
      let $subform = this.$subform[formId];
      if ($subform) {
        return $subform.getRowForm(index);
      }
      return undefined;
    },

    deleteSubformByFilter(formId, filter) {
      let $subform = this.$subform[formId];
      if ($subform) {
        $subform.deleteSubformByFilter(filter);
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

    setFormLayoutVisible(code, visible = true) {
      let el = document.querySelector(`[w-code="${code}"]`);
      if (el && el.__vue__) {
        if (visible) {
          el.__vue__.showFormLayout();
        } else {
          el.__vue__.hideFormLayout();
        }
      }
    },

    emitEvent() {
      //用于兄弟组件发射事件
      if (this.vueInstance) {
        this.vueInstance.$emit.apply(this.vueInstance, arguments);
      }
    },

    offEvent(evtName) {
      if (this.vueInstance) this.vueInstance.$off(evtName);
      return this;
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
          if (typeof callback === "function") {
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

    // 获取数据发生变更的字段
    getUpdatedFields() {
      let fields = [];
      for (let key in this.formData) {
        if (this.formData[key] != this.originFormData[key]) {
          fields.push(key);
        }
      }
      return fields;
    },
  };
}
