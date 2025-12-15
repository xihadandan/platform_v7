define(['jquery', 'server', 'commons', 'appModal'], function ($, server, commons, appModal) {
  var select2 =
    "<input id='widget_ref_def_uuid' name='widget_ref_def_uuid' type='text'></input>" +
    "<input id='widget_ref_def_title' name='widget_ref_def_title' type='hidden'></input>";
  var tipMsg = '<div>注：被引用的组件包含子组件定义，在页面定义保存后会覆盖引用组件的子组件定义</div>';
  var widget_template_container = "<div class='widget-template-container'></div>";

  function getWidgetDefinition(widgetDefUuid) {
    var widgetDefinition = null;
    server.JDS.call({
      service: 'appWidgetDefinitionMgr.getBean',
      data: [widgetDefUuid],
      async: false,
      success: function (result) {
        widgetDefinition = result.data;
      }
    });
    return widgetDefinition;
  }

  function copyBasicProperties(target, options) {
    target.title = options.title;
    target.id = options.id;
    target.wtype = options.wtype;
  }

  function disabledInputs($container) {
    setTimeout(function () {
      $('input', $container).attr('disabled', true);
      $('select', $container).attr('disabled', true);
      $('button', $container).attr('disabled', true);
      $('textarea', $container).attr('disabled', true);
      $('a.editable-click', $container).off('click');
    }, 500);
  }
  var dialog = function (designer, component) {
    var wconfigurer = component.getPropertyConfigurer();
    var configurer = wconfigurer;
    if ($.isFunction(wconfigurer)) {
      configurer = new wconfigurer(component);
    }
    component.setPropertyConfigurer(configurer);

    var StringUtils = commons.StringUtils;
    var templateContent = configurer.getTemplate.call(configurer);
    if (StringUtils.isBlank(templateContent)) {
      var templateUrl = configurer.getTemplateUrl.call(configurer);
      if (StringUtils.isNotBlank(templateUrl)) {
        $.ajaxSetup({
          async: false
        });
        $.get(templateUrl, function (content) {
          templateContent = content;
        });
        $.ajaxSetup({
          async: true
        });
      }
    }

    var options = component.getOptions();
    var wtype = options.wtype;
    var refWidgetDefUuid = options.refWidgetDefUuid;
    var refWidgetDefTitle = options.refWidgetDefTitle;
    // 组件定义选择
    var selectWidgetDefinition = function (onOk, isReference) {
      var msg = select2;
      if (isReference) {
        msg += tipMsg;
      }
      appModal.dialog({
        title: '选择组件定义',
        size: 'middle',
        message: msg,
        shown: function () {
          $('#widget_ref_def_uuid').val(refWidgetDefUuid);
          $('#widget_ref_def_uuid').wSelect2({
            serviceName: 'appWidgetDefinitionMgr',
            labelField: 'widget_ref_def_title',
            valueField: 'widget_ref_def_uuid',
            params: {
              wtype: wtype,
              excludeWidgetIds: options.id
            },
            width: '100%',
            height: 250
          });
        },
        buttons: {
          confirm: {
            label: '确定',
            className: 'well-btn w-btn-primary',
            callback: function () {
              return onOk.call(this);
            }
          },
          cancel: {
            label: '关闭',
            className: 'well-btn w-btn-default'
          }
        }
      });
    };

    var buttons = {
      refWidgetDefUuid: {
        label: '引用组件定义',
        className: 'well-btn w-btn-primary',
        callback: function () {
          selectWidgetDefinition(function () {
            refWidgetDefUuid = $('#widget_ref_def_uuid').val();
            if (StringUtils.isBlank(refWidgetDefUuid)) {
              appModal.alert('请选择组件定义!');
              return false;
            }
            var widgetDefinition = getWidgetDefinition(refWidgetDefUuid);
            refWidgetDefTitle = widgetDefinition.title;
            var definitionJson = JSON.parse(widgetDefinition.definitionJson);
            copyBasicProperties(definitionJson, options);
            definitionJson.refWidgetDefUuid = refWidgetDefUuid;
            definitionJson.refWidgetDefTitle = refWidgetDefTitle;
            var $template = $(templateContent);
            var $container = $('.widget-template-container');
            $container.html('');
            $container.html($template);
            var tempOptions = $.extend(true, {}, definitionJson);
            configurer.onReferenceWidgetDefinition.call(configurer, $container, tempOptions);
            disabledInputs($container);
          }, true);
          return false;
        }
      },
      loadFromWidgetDefinition: {
        label: '从组件定义加载',
        className: 'well-btn w-btn-primary',
        callback: function () {
          selectWidgetDefinition(function () {
            var widgetDefUuid = $('#widget_ref_def_uuid').val();
            if (StringUtils.isBlank(widgetDefUuid)) {
              appModal.alert('请选择组件定义!');
              return false;
            }
            var widgetDefinition = getWidgetDefinition(widgetDefUuid);
            var definitionJson = JSON.parse(widgetDefinition.definitionJson);
            if ($.isFunction(component.beforeFromOtherDefintionLoad)) {
              var ans = component.beforeFromOtherDefintionLoad(definitionJson);
              if (ans === false) {
                return false;
              }
            }
            if ($.isFunction(component.changeWidgetUuidBeforeFromOtherDefintionLoad)) {
              definitionJson = component.changeWidgetUuidBeforeFromOtherDefintionLoad(definitionJson);
            }
            copyBasicProperties(definitionJson, options);
            var $template = $(templateContent);
            var $container = $('.widget-template-container');
            $container.html('');
            $container.html($template);
            var tempOptions = $.extend(true, {}, definitionJson);
            configurer.onLoadFromWidgetDefinition.call(configurer, $container, tempOptions);
            refWidgetDefUuid = null;
            refWidgetDefTitle = null;
          });
          return false;
        }
      },
      confirm: {
        label: '确定',
        className: 'well-btn w-btn-primary',
        callback: function () {
          var result = configurer.onOk.call(configurer, $('.widget-template-container'));
          if (result === false) {
            return result;
          }
          var lastestOptions = component.getOptions();
          if (StringUtils.isNotBlank(refWidgetDefUuid)) {
            lastestOptions.refWidgetDefUuid = refWidgetDefUuid;
            lastestOptions.refWidgetDefTitle = refWidgetDefTitle;
          } else {
            delete lastestOptions['refWidgetDefUuid'];
            delete lastestOptions['refWidgetDefTitle'];
          }
          designer.preview.call(designer, component);
        }
      },
      clear: {
        label: '清空',
        className: 'well-btn w-btn-primary',
        callback: function () {
          var $template = $(templateContent);
          var $container = $('.widget-template-container');
          $container.html('');
          $container.html($template);
          var tempOptions = designer.getWidgetDefaultOptions(wtype);
          copyBasicProperties(tempOptions, options);
          configurer.onClear.call(configurer, $container, tempOptions);
          refWidgetDefUuid = null;
          refWidgetDefTitle = null;
          return false;
        }
      },
      reset: {
        label: '重置',
        className: 'well-btn w-btn-primary',
        callback: function () {
          var $template = $(templateContent);
          var $container = $('.widget-template-container');
          $container.html('');
          $container.html($template);
          var tempOptions = $.extend(true, {}, options);
          configurer.onReset.call(configurer, $container, tempOptions);
          refWidgetDefUuid = null;
          refWidgetDefTitle = null;
          return false;
        }
      },
      cancel: {
        label: '关闭',
        className: 'well-btn w-btn-default',
        callback: function () {
          configurer.onCancel.call(configurer);
        }
      }
    };

    var title = '属性配置——' + options.title;
    if (StringUtils.isNotBlank(refWidgetDefTitle)) {
      title += '(引用——' + refWidgetDefTitle + ')';
    }
    var $dialog = appModal.dialog({
      title: title,
      size: 'large',
      zIndex: 1031,
      width: 1000,
      message: widget_template_container,
      buttons: buttons,
      shown: function () {
        var $template = $(templateContent);
        var $container = $('.widget-template-container');
        $container.html('');
        $container.html($template);
        var tempOptions = $.extend(true, {}, options);
        if (StringUtils.isBlank(refWidgetDefUuid)) {
          configurer.onLoad.call(configurer, $container, tempOptions);
        } else {
          configurer.onReferenceWidgetDefinition.call(configurer, $container, tempOptions);
          disabledInputs($container);
        }
      }
    });
    $dialog
      .find('.modal-footer')
      .find('button')
      .on('mousedown', function () {
        $dialog.find('.modal-footer').trigger('click');
      });
  };
  return dialog;
});
