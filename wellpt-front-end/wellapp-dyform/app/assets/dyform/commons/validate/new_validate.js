(function ($) {
  function getValidatorContainer(elem) {
    if ($(elem.form).hasClass('editableform')) {
      return $(elem.form).closest('.dyform')[0];
    } else {
      return elem.form || $(elem).closest('.dyform')[0];
    }
  }

  $.extend($.fn, {
    wValidate: function (options) {
      // if nothing is selected, return nothing; can't chain anyway
      if (!this.length) {
        if (options && options.debug && window.console) {
          console.warn("Nothing selected, can't validate, returning nothing.");
        }
        return;
      }

      // check if a validator for this form was already created
      var validator = $.data(this[0], 'wValidator');
      if (validator) {
        return validator;
      }

      // Add novalidate tag if HTML5.
      this.attr('novalidate', 'novalidate');

      validator = new $.wValidator(options, this[0]);
      $.data(this[0], 'wValidator', validator);

      return validator;
    },
    wValid: function () {
      var valid = true;
      var validator = getValidatorContainer(this[0]).wValidate();
      this.each(function () {
        valid = valid && validator.element(this);
      });
      return valid;
    },
    // attributes: space seperated list of attributes to retrieve and remove
    wRemoveAttrs: function (attributes) {
      var result = {},
        $element = this;
      $.each(attributes.split(/\s/), function (index, value) {
        result[value] = $element.attr(value);
        $element.removeAttr(value);
      });
      return result;
    },
    wRules: function (command, argument) {
      var element = this[0];

      if (command) {
        var settings = $.data(getValidatorContainer(element), 'wValidator').settings;
        var staticRules = settings.rules;
        var existingRules = $.wValidator.staticRules(element);
        switch (command) {
          case 'add':
            $.extend(existingRules, $.wValidator.normalizeRule(argument));
            // remove messages from rules, but allow them to be set
            // separetely
            delete existingRules.messages;
            staticRules[this.getName(element)] = existingRules;
            if (argument.messages) {
              settings.messages[this.getName(element)] = $.extend(settings.messages[this.getName(element)], argument.messages);
            }
            break;
          case 'remove':
            if (!argument) {
              delete staticRules[this.getName(element)];
              return existingRules;
            }
            var filtered = {};
            $.each(argument.split(/\s/), function (index, method) {
              filtered[method] = existingRules[method];
              delete existingRules[method];
            });
            return filtered;
        }
      }

      var data = $.wValidator.normalizeRules(
        $.extend(
          {},
          $.wValidator.classRules(element),
          $.wValidator.attributeRules(element),
          $.wValidator.dataRules(element),
          $.wValidator.staticRules(element)
        ),
        element
      );

      // make sure required is at front
      if (data.required) {
        var param = data.required;
        delete data.required;
        data = $.extend(
          {
            required: param
          },
          data
        );
      }

      return data;
    }
  });

  // Custom selectors
  $.extend($.expr[':'], {
    blank: function (a) {
      return !$.trim('' + $(a).val());
    },

    filled: function (a) {
      return !!$.trim('' + $(a).val());
    },

    unchecked: function (a) {
      return !$(a).prop('checked');
    }
  });

  // constructor for validator
  $.wValidator = function (options, form) {
    this.settings = $.extend(true, {}, $.wValidator.defaults, options);
    this.currentForm = form;
    this.init();
  };

  $.wValidator.format = function (source, params) {
    if (arguments.length === 1) {
      return function () {
        var args = $.makeArray(arguments);
        args.unshift(source);
        return $.wValidator.format.apply(this, args);
      };
    }
    if (arguments.length > 2 && params.constructor !== Array) {
      params = $.makeArray(arguments).slice(1);
    }
    if (params.constructor !== Array) {
      params = [params];
    }
    $.each(params, function (i, n) {
      source = source.replace(new RegExp('\\{' + i + '\\}', 'g'), function () {
        return n;
      });
    });
    return source;
  };

  $.extend($.wValidator, {
    defaults: {
      messages: {},
      groups: {},
      rules: {},
      errorClass: 'error',
      validClass: 'valid',
      errorElement: 'span',
      focusInvalid: true,
      errorContainer: $([]),
      errorLabelContainer: $([]),
      onsubmit: true,
      ignore: ':hidden',
      ignoreTitle: false,
      onfocusin: function (ctl, event) {
        this.lastActive = ctl;
        if (this.settings.focusCleanup && !this.blockFocusCleanup) {
          if (this.settings.unhighlight) {
            this.settings.unhighlight.call(this, ctl, this.settings.errorClass, this.settings.validClass);
          }
          this.addWrapper(this.errorsFor(ctl)).hide();
        }
      },
      onfocusout: function (ctl, event) {
        if (!this.checkable(ctl) && (this.getName(ctl) in this.submitted || !this.optional(ctl))) {
          this.element(ctl);
        }
      },
      onkeyup: function (ctl, event) {
        if (event.which === 9 && this.elementValue(ctl) === '') {
          return;
        } else if (this.getName(ctl) in this.submitted || ctl === this.lastElement) {
          this.element(ctl);
        }
      },
      onclick: function (ctl, event) {
        if (this.getName(ctl) in this.submitted) {
          this.element(ctl);
        } else if (ctl.parentNode.name in this.submitted) {
          this.element(ctl.parentNode);
        }
      },
      highlight: function (ctl) {
        var $editElement = $(ctl.get$InputElem());
        if ($editElement.not(':hidden')) {
          if ($editElement.is('[type=file]')) {
            $editElement.closest('.btn-image-uploader').css('border-color', '#E33033');
            $editElement.closest('.btn-primary').css('border-color', '#E33033');
          } else if ($editElement.parents('td').find('a.editable').size() > 0) {
            // $editElement.parent().addClass('error-style');
          } else {
            switch (ctl.getInputMode()) {
              case dyFormInputMode.number:
                $editElement.parent().addClass('error-style');
                break;
              case dyFormInputMode.treeSelect:
                ctl.$editableElem && ctl.$editableElem.next().addClass('error-style');
                break;
              case dyFormInputMode.ckedit:
                ctl.$editableElem && ctl.$editableElem.next().addClass('editableClass error-style');
                break;
              case dyFormInputMode.radio:
              case dyFormInputMode.checkbox:
                $editElement.parents('.editableClass').addClass('error-style');
                break;
              case dyFormInputMode.select:
              case dyFormInputMode.comboSelect:
                $editElement.prev().addClass('error-style');
                break;
              case dyFormInputMode.orgSelect:
              case dyFormInputMode.orgSelectStaff:
              case dyFormInputMode.orgSelectDepartment:
              case dyFormInputMode.orgSelectStaDep:
              case dyFormInputMode.orgSelectAddress:
              case dyFormInputMode.orgSelect2:
                $editElement.next().addClass('error-style');
                break;
              default:
                $editElement.addClass('error-style');
                break;
            }
          }
        }
        // if ($editElement.parents("td").find(".Validform_checktip").size() > 0) {
        //     $editElement.parents("td").find(".Validform_checktip").show();
        //     return;
        // }
      },
      unhighlight: function (ctl) {
        var $editElement = $(ctl.get$InputElem());
        var name = ctl.getCtlName();
        if ($editElement.is('[type=file]')) {
          $editElement.closest('.btn-primary').css('border-color', '');
          $editElement.parents('td').find('.Validform_checktip').hide();
        } else if ($editElement.parents('td').find('a.editable').size() > 0) {
          $editElement.parent().parent('td').find('.Validform_checktip').hide();
        } else {
          switch (ctl.getInputMode()) {
            case dyFormInputMode.number:
              $editElement.parent().removeClass('error-style');
              break;
            case dyFormInputMode.treeSelect:
              ctl.$editableElem && ctl.$editableElem.next().removeClass('error-style');
              break;
            case dyFormInputMode.ckedit:
              ctl.$editableElem && ctl.$editableElem.next().removeClass('error-style');
              break;
            case dyFormInputMode.radio:
            case dyFormInputMode.checkbox:
              $editElement.parents('.editableClass').removeClass('error-style');
              break;
            case dyFormInputMode.select:
            case dyFormInputMode.comboSelect:
              $editElement.prev().removeClass('error-style');
              break;
            case dyFormInputMode.orgSelect:
            case dyFormInputMode.orgSelectStaff:
            case dyFormInputMode.orgSelectDepartment:
            case dyFormInputMode.orgSelectStaDep:
            case dyFormInputMode.orgSelectAddress:
            case dyFormInputMode.orgSelect2:
              $editElement.next().removeClass('error-style');
              break;
            default:
              $editElement.removeClass('error-style');
              break;
          }
          $editElement.parents('td').find('.Validform_checktip').hide();
        }
        if (typeof bubble != 'undefined' && bubble != null) {
          // 删除气泡中的错误信息
          bubble.removeErrorItem(name);
        }
      },

      // 对于统一上传控件的控件，第一个参数errorMsgElement的值为null
      success: function (errorMsgElement, validatedCtl) {
        var name = validatedCtl.getCtlName();
        var validatedElement = validatedCtl.$editableElem;
        if (validatedElement == null) {
          validatedElement = validatedCtl.$placeHolder;
        }
        $(validatedElement).attr('valid', 'true');
        if (
          $(validatedElement).parents('td').find('a.editable').size() > 0 &&
          $(validatedElement).parent('td').find('.Validform_checktip')
        ) {
          $(validatedElement).parents('.editable-container,.control-container').siblings('.Validform_checktip').remove();
        } else if ($(validatedElement).parent('td').find('.Validform_checktip')) {
          $(validatedElement).parents('td').find('.Validform_checktip').remove();
        } else {
          $(validatedElement)
            .parent('td')
            .find('.error[name=' + name + ']')
            .html('');
        }
      },
      errorPlacement: function (error, ctl) {
        var errorWrapper = $("<div class='Validform_checktip'/>");
        var name = ctl[0].getCtlName();
        var placeHolder = ctl[0].$editableElem;
        if (placeHolder == null) {
          placeHolder = ctl[0].$placeHolder;
        }
        var placeHolderPosition = $(placeHolder).offset();
        if (ctl[0].isVisible()) {
          errorWrapper.css({
            left: placeHolderPosition.left + 'px'
          });
          errorWrapper.html(error);
          $(placeHolder).attr('valid', 'false');

          if (
            $(placeHolder).parents('td').find('a.editable').size() > 0 &&
            $(placeHolder).parents('.editable-container,.control-container').size() > 0
          ) {
            $(placeHolder).parents('.editable-container,.control-container').parent('td').append(errorWrapper);
          } else if ($(placeHolder).parents('td').find('a.editable').size() > 0) {
            $(placeHolder).parent().parent('td').append(errorWrapper);
          } else {
            $(placeHolder).parents('td').first().append(errorWrapper);
          }

          $('.Validform_checktip .error').attr('style', 'display: block;position:relative;width:auto;').addClass('iconfont');
        }
      },
      onchange: function (ctl, event) {
        if (this.getName(ctl) in this.submitted) {
          this.element(ctl);
        } else if (ctl.parentNode.name in this.submitted) {
          this.element(ctl.parentNode);
        }
      },
      onreValidate: function (ctl) {
        // click on selects, radiobuttons and checkboxes
        if (this.getName(ctl) in this.submitted || ctl === this.lastElement) {
          this.element(ctl);
        } else if (ctl.parentNode && ctl.parentNode.name in this.submitted) {
          this.element(ctl.parentNode);
        }
      }
    },
    setDefaults: function (settings) {
      $.extend($.wValidator.defaults, settings);
    },

    messages: {
      required: '请填写必填项',
      email: '请填写正确的邮箱地址',
      url: '请填写有效的url地址',
      date: '请输入合法的日期',
      dateISO: '请输入合法的日期 (ISO)',
      number: '请输入合法的数字',
      digits: '只能输入整数',
      creditcard: '请输入合法的信用卡号',
      equalTo: '请再次输入相同的值',
      maxlength: $.wValidator.format('请输入一个长度最多是 {0} 的字符串'),
      minlength: $.wValidator.format('请输入一个长度最少是 {0} 的字符串'),
      rangelength: $.wValidator.format('请输入一个长度介于 {0} 和 {1} 之间的字符串'),
      range: $.wValidator.format('请输入一个介于 {0} 和 {1} 之间的值'),
      max: $.wValidator.format('请输入一个最大为 {0} 的值'),
      min: $.wValidator.format('请输入一个最小为 {0} 的值')
    },

    autoCreateRanges: false,

    prototype: {
      init: function () {
        this.labelContainer = $(this.settings.errorLabelContainer);
        this.errorContext = (this.labelContainer.length && this.labelContainer) || $(this.currentForm);
        this.containers = $(this.settings.errorContainer).add(this.settings.errorLabelContainer);
        this.submitted = {};
        this.valueCache = {};
        this.invalid = {};
        this.reset();

        var groups = (this.groups = {});
        $.each(this.settings.groups, function (key, value) {
          if (typeof value === 'string') {
            value = value.split(/\s/);
          }
          $.each(value, function (index, name) {
            groups[name] = key;
          });
        });
        var rules = this.settings.rules;
        $.each(rules, function (key, value) {
          rules[key] = $.wValidator.normalizeRule(value);
        });
        var _this = this;

        function delegate(event) {
          var validator = $.data(getValidatorContainer(this[0]), 'wValidator'),
            eventType = 'on' + event.type.replace(/^validate/, '');

          if (validator && validator.settings[eventType]) {
            if (eventType == 'onreValidate') {
              validator.settings[eventType].call(validator, arguments[1], event);
            } else {
              validator.settings[eventType].call(validator, this[0], event);
            }
          }
          try {
            var bubble = window.bubble;
            var ctrl = _this.getControl(arguments[1]);
            var subformDefinition = ctrl.getFormDefinition();
            if ($(getValidatorContainer(this[0])).getSubformControl) {
              var subformcontrol = $(getValidatorContainer(this[0])).getSubformControl(subformDefinition.uuid);
              if (bubble && subformcontrol && subformcontrol.validateBlankRow()) {
                bubble.removeErrorItem(subformDefinition.uuid + '-blankrow');
              }
            }
          } catch (e) {
            console.log(e);
          }
        }
        $(this.currentForm).validateDelegate(
          ":text, [type='password'], [type='file'], select, textarea, " +
            "[type='number'], [type='search'] ,[type='tel'], [type='url'], " +
            "[type='email'], [type='datetime'], [type='date'], [type='month'], " +
            "[type='week'], [type='time'], [type='datetime-local'], " +
            "[type='range'], [type='color'] ," +
            "[type='radio'], [type='checkbox'], option, span",
          'reValidate',
          delegate
        );

        if (this.settings.invalidHandler) {
          $(this.currentForm).bind('invalid-form.validate', this.settings.invalidHandler);
        }
      },
      form: function () {
        this.checkForm();
        $.extend(this.submitted, this.errorMap);
        this.invalid = $.extend({}, this.errorMap);
        if (!this.valid()) {
          $(this.currentForm).triggerHandler('invalid-form', [this]);
        }
        this.showErrors();
        return this.valid();
      },

      checkForm: function () {
        this.prepareForm();
        for (var i = 0, elements = (this.currentElements = this.elements()); elements[i]; i++) {
          this.check(elements[i]);
        }
        return this.valid();
      },
      element: function (ctl) {
        this.prepareElement(ctl);
        if (!this.isControl(ctl)) {
          ctl = this.getControl(ctl);
        }

        this.lastElement = ctl;
        this.currentElements = $(ctl);

        var result = this.check(ctl) !== false;
        if (result) {
          delete this.invalid[this.getName(ctl)];
        } else {
          this.invalid[this.getName(ctl)] = true;
        }

        this.showErrors();
        return result;
      },

      control: function (ctl) {
        this.noVisibleError = true;
        var result = this.element(ctl);
        this.noVisibleError = false;
        if (result == false) {
          console.log(ctl.getCtlName() + '的验证结果是' + result);
        }
        // console.log(this.invalid);
        return result;
      },

      showErrors: function (errors) {
        if (errors) {
          // add items to error list and map
          $.extend(this.errorMap, errors);
          this.errorList = [];
          for (var name in errors) {
            this.errorList.push({
              message: errors[name],
              ctl: this.findByName(name)[0]
            });
          }
          // remove items from success list
          this.successList = $.grep(this.successList, function (ctl) {
            return !(this.getName(ctl) in errors);
          });
        }
        if (this.settings.showErrors) {
          this.settings.showErrors.call(this, this.errorMap, this.errorList);
        } else {
          this.defaultShowErrors();
        }
      },
      objectLength: function (obj) {
        var count = 0;
        for (var i in obj) {
          count++;
        }
        return count;
      },
      hideErrors: function () {
        this.addWrapper(this.toHide).hide();
      },

      valid: function () {
        return this.size() === 0;
      },

      size: function () {
        return this.errorList.length;
      },
      elements: function () {
        var validator = this,
          rulesCache = {};

        // select all valid inputs inside the form (no submit or reset buttons)
        return $(this.currentForm)
          .find('input, select, textarea')
          .not(':submit, :reset, :image, [disabled]')
          .not(this.settings.ignore)
          .filter(function () {
            if (!this.name && validator.settings.debug && window.console) {
              console.error('%o has no name assigned', this);
            }

            // select only the first element for each name, and only those with rules specified
            if (this.name in rulesCache || !validator.objectLength($(this).rules())) {
              return false;
            }

            rulesCache[this.name] = true;
            return true;
          });
      },
      focusInvalid: function () {
        if (this.settings.focusInvalid) {
          try {
            $(this.findLastActive() || (this.errorList.length && this.errorList[0].ctl) || [])
              .filter(':visible')
              .focus()
              .trigger('focusin');
          } catch (e) {}
        }
      },

      findLastActive: function () {
        var lastActive = this.lastActive;
        return (
          lastActive &&
          $.grep(this.errorList, function (n) {
            return n.element.name === lastActive.name;
          }).length === 1 &&
          lastActive
        );
      },

      isControl: function (ctl) {
        return typeof ctl == 'object' && ctl.$placeHolder ? true : false;
      },

      getName: function (ctl) {
        return ctl.getCtlName();
      },

      getControl: function (ctl) {
        if (this.isControl(ctl)) {
          return ctl;
        } else {
          var filedName = this.getName(ctl);
          ctl = $.ControlManager.getCtl(filedName);
          if (ctl != null) {
            return ctl;
          } else {
            console.error('获取不到该控件，请核对控件名称');
          }
        }
      },

      errors: function () {
        var errorClass = this.settings.errorClass.replace(' ', '.');
        return $(this.settings.errorElement + '.' + errorClass, this.errorContext);
      },

      reset: function () {
        this.successList = [];
        this.errorList = [];
        this.errorMap = {};
        this.toShow = $([]);
        this.toHide = $([]);
        this.currentElements = $([]);
      },
      prepareForm: function () {
        this.reset();
        this.toHide = this.errors().add(this.containers);
      },

      prepareElement: function (ctl) {
        this.reset();
        this.toHide = this.errorsFor(ctl);
      },

      elementValue: function (ctl) {
        var val = this.getValue(ctl);
        if (typeof val === 'string') {
          return val.replace(/\r/g, '');
        }
        return val;
      },

      // 获取控件的值
      getValue: function (ctl) {
        var value = null;
        if (ctl.isAttachCtl && ctl.isAttachCtl()) {
          // 校验器对附件控件在检验时，只要确认是否已有做上传文件即可
          ctl.getValue(function (ret) {
            value = ret;
          });
        } else {
          value = ctl.getValue();
        }
        return value;
      },

      getRules: function (ctl) {
        var ctlName = ctl.getCtlName();
        var rule = this.settings.rules[ctlName];
        if (ctl.isAttachCtl && ctl.isAttachCtl()) {
          rule.validFiles = true;
        }
        return rule;
      },

      check: function (ctl) {
        var ctlName = ctl.getCtlName();
        var rules = this.getRules(ctl);
        var dependencyMismatch = false;
        var val = this.elementValue(ctl);
        var result;
        for (var method in rules) {
          var rule = {
            method: method,
            parameters: rules[method]
          };
          try {
            result = $.wValidator.methods[method].call(this, val, ctl, rule.parameters);

            if (result === 'dependency-mismatch') {
              dependencyMismatch = true;
              continue;
            }
            dependencyMismatch = false;

            if (!result) {
              this.formatAndAdd(ctl, rule);
              return false;
            }
          } catch (e) {
            if (this.settings.debug && window.console) {
              console.log('Exception occurred when checking element ' + element.id + ", check the '" + rule.method + "' method.", e);
            }
            throw e;
          }
        }
        if (dependencyMismatch) {
          return;
        }
        if (this.objectLength(rules)) {
          this.successList.push(ctl);
        }
        return true;
      },

      customDataMessage: function (ctl, method) {
        return $(ctl).data('msg-' + method.toLowerCase()) || (ctl.attributes && $(ctl).attr('data-msg-' + method.toLowerCase()));
      },

      customMessage: function (name, method) {
        var m = this.settings.messages[name];
        return m && (m.constructor === String ? m : m[method]);
      },

      findDefined: function () {
        for (var i = 0; i < arguments.length; i++) {
          if (arguments[i] !== undefined) {
            return arguments[i];
          }
        }
        return undefined;
      },

      defaultMessage: function (ctl, method) {
        return this.findDefined(
          this.customMessage(this.getName(ctl), method),
          this.customDataMessage(ctl, method),
          // title is never undefined, so handle empty
          // string as undefined
          (!this.settings.ignoreTitle && ctl.title) || undefined,
          $.wValidator.messages[method],
          '<strong>Warning: No message defined for ' + this.getName(ctl) + '</strong>'
        );
      },

      formatAndAdd: function (ctl, rule) {
        var message = this.defaultMessage(ctl, rule.method),
          theregex = /\$?\{(\d+)\}/g;
        if (typeof message === 'function') {
          message = message.call(this, rule.parameters, ctl);
        } else if (theregex.test(message)) {
          message = $.wValidator.format(message.replace(theregex, '{$1}'), rule.parameters);
        }
        this.errorList.push({
          message: message,
          ctl: ctl
        });

        this.errorMap[this.getName(ctl)] = message;
        this.submitted[this.getName(ctl)] = message;
      },

      addWrapper: function (toToggle) {
        if (this.settings.wrapper) {
          toToggle = toToggle.add(toToggle.parent(this.settings.wrapper));
        }
        return toToggle;
      },

      defaultShowErrors: function () {
        var i, elements;
        for (i = 0; this.errorList[i]; i++) {
          var error = this.errorList[i];
          if (this.settings.highlight) {
            this.settings.highlight.call(this, error.ctl, this.settings.errorClass, this.settings.validClass);
          }
          this.showLabel(error.ctl, error.message);
        }
        if (this.errorList.length) {
          this.toShow = this.toShow.add(this.containers);
        }
        if (this.settings.success) {
          for (i = 0; this.successList[i]; i++) {
            this.showLabel(this.successList[i]);
          }
        }
        if (this.settings.unhighlight) {
          for (i = 0, elements = this.validElements(); elements[i]; i++) {
            this.settings.unhighlight.call(this, elements[i], this.settings.errorClass, this.settings.validClass);
          }
        }
        this.toHide = this.toHide.not(this.toShow);
        this.hideErrors();
        this.addWrapper(this.toShow).show();
      },

      validElements: function () {
        return this.currentElements.not(this.invalidElements());
      },

      invalidElements: function () {
        return $(this.errorList).map(function () {
          return this.ctl;
        });
      },

      showLabel: function (ctl, message) {
        var label = this.errorsFor(ctl);
        if (label.length && $.trim(message).length) {
          label.removeClass(this.settings.validClass).addClass(this.settings.errorClass);
          label.html(message);
          // bug#46015
          label.closest('.Validform_checktip').show();
        } else if ($.trim(message).length) {
          label = $('<' + this.settings.errorElement + '>')
            .attr('for', this.idOrName(ctl))
            .attr('name', this.idOrName(ctl))
            .addClass(this.settings.errorClass)
            .html(message || '');
          if (this.settings.wrapper) {
            label = label
              .hide()
              .show()
              .wrap('<' + this.settings.wrapper + '/>')
              .parent();
          }
          if (!this.labelContainer.append(label).length) {
            if (this.settings.errorPlacement) {
              this.settings.errorPlacement(label, $(ctl));
            } else {
              label.insertAfter(ctl);
            }
          }
        }
        if (!message && this.settings.success) {
          label.text('');
          if (typeof this.settings.success === 'string') {
            label.addClass(this.settings.success);
          } else {
            this.settings.success(label, ctl);
          }
        }
        this.toShow = this.toShow.add(label);
      },

      errorsFor: function (ctl) {
        var name = this.idOrName(ctl);
        return this.errors().filter(function () {
          return $(this).attr('for') === name;
        });
      },

      idOrName: function (ctl) {
        return this.groups[this.getName(ctl)] || (this.checkable(ctl) ? this.getName(ctl) : ctl.id || this.getName(ctl));
      },

      checkable: function (element) {
        return /radio|checkbox/i.test(element.type);
      },

      findByName: function (name) {
        return $(this.currentForm).find("[name='" + name + "']");
      },

      getLength: function (value, ctl) {
        if (ctl.$editableElem != null) {
          switch (ctl.$editableElem[0].nodeName.toLowerCase()) {
            case 'select':
              return $('option:selected', ctl.$editableElem[0]).length;
            case 'input':
              if (this.checkable(ctl.$editableElem[0])) {
                return this.findByName(this.getName(ctl)).filter(':checked').length;
              }
          }
        }
        return value.length;
      },

      optional: function (ctl) {
        var val = this.elementValue(ctl);
        return !$.wValidator.methods.required.call(this, val, ctl);
      }
    },

    classRuleSettings: {
      required: {
        required: true
      },
      email: {
        email: true
      },
      url: {
        url: true
      },
      date: {
        date: true
      },
      dateISO: {
        dateISO: true
      },
      number: {
        number: true
      },
      digits: {
        digits: true
      },
      creditcard: {
        creditcard: true
      }
    },

    addClassRules: function (className, rules) {
      if (className.constructor === String) {
        this.classRuleSettings[className] = rules;
      } else {
        $.extend(this.classRuleSettings, className);
      }
    },

    classRules: function (ctl) {
      var rules = {};
      var classes = $(ctl).attr('class');
      if (classes) {
        $.each(classes.split(' '), function () {
          if (this in $.wValidator.classRuleSettings) {
            $.extend(rules, $.wValidator.classRuleSettings[this]);
          }
        });
      }
      return rules;
    },

    attributeRules: function (ctl) {
      var rules = {};
      var $element = $(ctl);
      var type = $element[0].getAttribute('type');

      for (var method in $.wValidator.methods) {
        var value;

        if (method === 'required') {
          value = $element.get(0).getAttribute(method);

          if (value === '') {
            value = true;
          }
          value = !!value;
        } else {
          value = $element.attr(method);
        }

        if (/min|max/.test(method) && (type === null || /number|range|text/.test(type))) {
          value = Number(value);
        }

        if (value) {
          rules[method] = value;
        } else if (type === method && type !== 'range') {
          rules[method] = true;
        }
      }

      if (rules.maxlength && /-1|2147483647|524288/.test(rules.maxlength)) {
        delete rules.maxlength;
      }

      return rules;
    },

    dataRules: function (ctl) {
      var method,
        value,
        rules = {},
        $element = $(ctl);
      for (method in $.wValidator.methods) {
        value = $element.data('rule-' + method.toLowerCase());
        if (value !== undefined) {
          rules[method] = value;
        }
      }
      return rules;
    },

    staticRules: function (ctl) {
      var rules = {};
      var validator = $.data(getValidatorContainer(ctl), 'wValidator');
      if (validator.settings.rules) {
        rules = $.wValidator.normalizeRule(validator.settings.rules[validator.getName(ctl)]) || {};
      }
      return rules;
    },

    normalizeRules: function (rules, ctl) {
      // handle dependency check
      $.each(rules, function (prop, val) {
        if (val === false) {
          delete rules[prop];
          return;
        }
        if (val.param || val.depends) {
          var keepRule = true;
          switch (typeof val.depends) {
            case 'string':
              keepRule = !!$(val.depends, getValidatorContainer(ctl)).length;
              break;
            case 'function':
              keepRule = val.depends.call(ctl, element);
              break;
          }
          if (keepRule) {
            rules[prop] = val.param !== undefined ? val.param : true;
          } else {
            delete rules[prop];
          }
        }
      });

      $.each(rules, function (rule, parameter) {
        rules[rule] = $.isFunction(parameter) ? parameter(ctl) : parameter;
      });

      $.each(['minlength', 'maxlength'], function () {
        if (rules[this]) {
          rules[this] = Number(rules[this]);
        }
      });
      $.each(['rangelength', 'range'], function () {
        var parts;
        if (rules[this]) {
          if ($.isArray(rules[this])) {
            rules[this] = [Number(rules[this][0]), Number(rules[this][1])];
          } else if (typeof rules[this] === 'string') {
            parts = rules[this].split(/[\s,]+/);
            rules[this] = [Number(parts[0]), Number(parts[1])];
          }
        }
      });

      if ($.wValidator.autoCreateRanges) {
        if (rules.min && rules.max) {
          rules.range = [rules.min, rules.max];
          delete rules.min;
          delete rules.max;
        }
        if (rules.minlength && rules.maxlength) {
          rules.rangelength = [rules.minlength, rules.maxlength];
          delete rules.minlength;
          delete rules.maxlength;
        }
      }

      return rules;
    },

    normalizeRule: function (data) {
      if (typeof data === 'string') {
        var transformed = {};
        $.each(data.split(/\s/), function () {
          transformed[this] = true;
        });
        data = transformed;
      }
      return data;
    },

    addMethod: function (name, method, message) {
      $.wValidator.methods[name] = method;
      $.wValidator.messages[name] = message !== undefined ? message : $.wValidator.messages[name];
      if (method.length < 3) {
        $.wValidator.addClassRules(name, $.wValidator.normalizeRule(name));
      }
    },

    methods: {
      required: function (value, ctl, param) {
        if (ctl != null) {
          // 表单控件验证
          var isAttachCtl = ctl.isAttachCtl && ctl.isAttachCtl();
        }

        if (isAttachCtl) {
          // 附件控件另外处理
          if (value == null || value == undefined) {
            return false;
          } else if (value instanceof Array) {
            if (value.length > 0) {
              return true;
            } else {
              return false;
            }
          } else {
            return false;
          }
        } else {
          return $.trim(value).length > 0;
        }
      },

      email: function (value, ctl) {
        return (
          this.optional(ctl) ||
          /^((([a-z]|\d|[!#\$%&'\*\+\-\/=\?\^_`{\|}~]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])+(\.([a-z]|\d|[!#\$%&'\*\+\-\/=\?\^_`{\|}~]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])+)*)|((\x22)((((\x20|\x09)*(\x0d\x0a))?(\x20|\x09)+)?(([\x01-\x08\x0b\x0c\x0e-\x1f\x7f]|\x21|[\x23-\x5b]|[\x5d-\x7e]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(\\([\x01-\x09\x0b\x0c\x0d-\x7f]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]))))*(((\x20|\x09)*(\x0d\x0a))?(\x20|\x09)+)?(\x22)))@((([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.)+(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))$/i.test(
            value
          )
        );
      },
      url: function (value, ctl) {
        return (
          this.optional(ctl) ||
          /^(https?|s?ftp):\/\/(((([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:)*@)?(((\d|[1-9]\d|1\d\d|2[0-4]\d|25[0-5])\.(\d|[1-9]\d|1\d\d|2[0-4]\d|25[0-5])\.(\d|[1-9]\d|1\d\d|2[0-4]\d|25[0-5])\.(\d|[1-9]\d|1\d\d|2[0-4]\d|25[0-5]))|((([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.)+(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.?)(:\d*)?)(\/((([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:|@)+(\/(([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:|@)*)*)?)?(\?((([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:|@)|[\uE000-\uF8FF]|\/|\?)*)?(#((([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:|@)|\/|\?)*)?$/i.test(
            value
          )
        );
      },

      date: function (value, ctl) {
        return this.optional(ctl) || !/Invalid|NaN/.test(new Date(value).toString());
      },

      dateISO: function (value, ctl) {
        return this.optional(ctl) || /^\d{4}[\/\-]\d{1,2}[\/\-]\d{1,2}$/.test(value);
      },
      number: function (value, ctl) {
        return this.optional(ctl) || /^-?(?:\d+|\d{1,3}(?:,\d{3})+)?(?:\.\d+)?$/.test(value);
      },

      digits: function (value, ctl) {
        return this.optional(ctl) || /^\d+$/.test(value);
      },

      creditcard: function (value, ctl) {
        if (this.optional(ctl)) {
          return 'dependency-mismatch';
        }
        // accept only spaces, digits and dashes
        if (/[^0-9 \-]+/.test(value)) {
          return false;
        }
        var nCheck = 0,
          nDigit = 0,
          bEven = false;

        value = value.replace(/\D/g, '');

        for (var n = value.length - 1; n >= 0; n--) {
          var cDigit = value.charAt(n);
          nDigit = parseInt(cDigit, 10);
          if (bEven) {
            if ((nDigit *= 2) > 9) {
              nDigit -= 9;
            }
          }
          nCheck += nDigit;
          bEven = !bEven;
        }

        return nCheck % 10 === 0;
      },

      minlength: function (value, ctl, param) {
        var length = $.isArray(value) ? value.length : this.getLength($.trim(value), ctl);
        return this.optional(ctl) || length >= param;
      },

      maxlength: function (value, ctl, param) {
        var length = $.isArray(value) ? value.length : this.getLength($.trim(value), ctl);
        return this.optional(ctl) || length <= param;
      },

      rangelength: function (value, ctl, param) {
        var length = $.isArray(value) ? value.length : this.getLength($.trim(value), ctl);
        return this.optional(ctl) || (length >= param[0] && length <= param[1]);
      },

      min: function (value, ctl, param) {
        return this.optional(ctl) || value >= param;
      },

      max: function (value, ctl, param) {
        return this.optional(ctl) || value <= param;
      },

      range: function (value, ctl, param) {
        return this.optional(ctl) || (value >= param[0] && value <= param[1]);
      }
    }
  });

  $.format = $.wValidator.format;
})(jQuery);

(function ($) {
  $.extend($.fn, {
    validateDelegate: function (delegate, type, handler) {
      return this.bind(type, function (event) {
        var target = $(event.target);
        if (target.is(delegate)) {
          return handler.apply(target, arguments);
        }
      });
    }
  });
})(jQuery);
