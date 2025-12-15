define(['ui_component', 'constant', 'commons', 'formBuilder', 'appContext', 'design_commons'], function (
  ui_component,
  constant,
  commons,
  formBuilder,
  appContext,
  designCommons
) {
  var component = $.ui.component.BaseComponent();

  var StringUtils = commons.StringUtils;
  var StringBuilder = commons.StringBuilder;
  var DefaultConfiguration = {
    backgroudColor: 'themeColor',
    rightNav: {
      menuItems: [
        {
          uuid: 'onlinePerson',
          text: '在线人员',
          hiddenName: '0',
          icon: {
            className: 'iconfont icon-ptkj-zaixianrenyuanshuaxin'
          },
          shouldAlwaysRetain: true,
          checked: false,
          hidden: '1'
        },
        {
          uuid: 'personInfo',
          text: '个人信息',
          hiddenName: '0',
          icon: {
            className: 'iconfont icon-ptkj-morentouxiang'
          },
          shouldAlwaysRetain: true,
          checked: false
        },
        {
          uuid: 'myMsg',
          text: '我的消息',
          hiddenName: '1',
          icon: {
            className: 'iconfont icon-ptkj-xiaoxitongzhi'
          },
          shouldAlwaysRetain: true,
          target: {
            position: '_dialog',
            positionName: '对话框',
            refreshIfExists: false,
            widgetDialogHeight: '',
            widgetDialogTitle: '',
            widgetDialogWidth: ''
          },
          checked: false
        },
        // {
        //   uuid: 'theme',
        //   text: '主题设置',
        //   hiddenName: '1',
        //   icon: {
        //     className: 'iconfont icon-ptkj-zhutipifu'
        //   },
        //   shouldAlwaysRetain: true,
        //   checked: false
        // },
        {
          uuid: 'switchAccount',
          text: '切换账号',
          hiddenName: '1',
          icon: {
            className: 'iconfont icon-ptkj-qiehuanyonghu'
          },
          shouldAlwaysRetain: true,
          checked: false
        },
        {
          uuid: 'logOut',
          text: '退出登录',
          hiddenName: '1',
          icon: {
            className: 'iconfont icon-ptkj-tuichudenglu'
          },
          shouldAlwaysRetain: true,
          checked: false
        },
        {
          uuid: 'workbench',
          text: '我的工作台',
          hiddenName: '1',
          icon: {
            className: 'iconfont icon-ptkj-qiehuanshitu'
          },
          shouldAlwaysRetain: true,
          checked: false
        }
      ]
    }
  };

  component.prototype.create = function () {};
  // 使用属性配置器
  component.prototype.usePropertyConfigurer = function () {
    return true;
  };

  function buildMenuItemBootstrapTable($element, name, menuItems, productUuid, navType) {
    formBuilder.bootstrapTable.build({
      container: $element,
      name: name,
      // height: '430',
      ediableNest: true,
      addDefaultBean: {
        uuid: '',
        hiddenName: navType === '1' ? '1' : '0',
        text: '',
        icon: ''
      },
      table: {
        data: menuItems,
        striped: true,
        idField: 'uuid',
        onEditableSave: function (field, row, oldValue, $el) {
          if (navType !== '2') {
            return;
          }
          var $tableSubNavMenuItemsInfo = $('#table_mainNav-menuItems_info', $element);
          if (field == 'defaultSelected' && row[field] == '1') {
            var data = $tableSubNavMenuItemsInfo.bootstrapTable('getData');
            $.each(data, function (index, rowData) {
              if (row != rowData) {
                rowData.defaultSelected = 0;
                $tableSubNavMenuItemsInfo.bootstrapTable('updateRow', index, rowData);
              }
            });
          }
        },
        columns: [
          {
            field: 'checked',
            formatter: designCommons.checkedFormat,
            checkbox: true
          },
          {
            field: 'uuid',
            title: 'UUID',
            visible: false,
            editable: {
              type: 'text',
              showbuttons: false,
              onblur: 'submit',
              mode: 'inline'
            }
          },
          {
            field: 'text',
            title: '名称',
            editable: {
              type: 'text',
              mode: 'inline',
              showbuttons: false,
              onblur: 'submit'
            }
          },
          {
            field: 'hiddenName',
            title: '隐藏名称',
            visible: navType === '1' ? true : false,
            editable: {
              type: 'select',
              mode: 'inline',
              onblur: 'submit',
              showbuttons: false,
              value: '1',
              source: [
                {
                  value: '0',
                  text: '否'
                },
                {
                  value: '1',
                  text: '是'
                }
              ]
            }
          },
          {
            field: 'icon',
            title: '图标',
            editable: {
              onblur: 'save',
              type: 'wCustomForm',
              placement: 'right',
              savenochange: true,
              iconSelectTypes: [3],
              value2input: designCommons.bootstrapTable.icon.value2input,
              input2value: designCommons.bootstrapTable.icon.input2value,
              value2display: designCommons.bootstrapTable.icon.value2display,
              value2html: designCommons.bootstrapTable.icon.value2html
            }
          },
          {
            field: 'hidden',
            title: '是否隐藏',
            editable: {
              type: 'select',
              mode: 'inline',
              onblur: 'submit',
              showbuttons: false,
              source: [
                {
                  value: '-1',
                  text: ''
                },
                {
                  value: '0',
                  text: '否'
                },
                {
                  value: '1',
                  text: '是'
                }
              ]
            }
          },
          {
            field: 'badge',
            title: '徽章',
            visible: navType === '1',
            editable: {
              onblur: 'cancel',
              type: 'wCustomForm',
              placement: 'bottom',
              savenochange: true,
              value2input: designCommons.bootstrapTable.badge.value2input(),
              value2display: designCommons.bootstrapTable.badge.value2display
            }
          },
          {
            field: 'defaultSelected',
            title: '默认选中',
            visible: navType === '2',
            editable: {
              type: 'select',
              mode: 'inline',
              onblur: 'submit',
              showbuttons: false,
              source: [
                {
                  value: '-1',
                  text: ''
                },
                {
                  value: '1',
                  text: '是'
                },
                {
                  value: '0',
                  text: '否'
                }
              ]
            }
          },
          {
            field: 'eventType',
            title: '事件类型',
            editable: {
              type: 'select',
              mode: 'inline',
              onblur: 'submit',
              showbuttons: false,
              source: function () {
                var eventTypes = constant.EVENT_TYPE;
                var types = [
                  {
                    value: '-1',
                    text: ''
                  }
                ];
                var etes = $.map(eventTypes, function (eventType) {
                  return {
                    value: eventType.value,
                    text: eventType.name
                  };
                });
                types = types.concat(etes);
                return types;
              }
            }
          },
          {
            field: 'target',
            title: '目标位置',
            editable: {
              onblur: 'cancel',
              type: 'wCustomForm',
              placement: 'bottom',
              savenochange: true,
              value2input: designCommons.bootstrapTable.targePosition.value2input,
              value2display: designCommons.bootstrapTable.targePosition.value2display,
              inputCompleted: designCommons.bootstrapTable.targePosition.inputCompleted
            }
          },
          {
            field: 'eventHandler',
            title: '事件处理',
            width: 150,
            editable: {
              onblur: 'ignore',
              type: 'wCustomForm',
              placement: 'left',
              savenochange: true,
              value2input: designCommons.bootstrapTable.eventHandler.value2input,
              input2value: designCommons.bootstrapTable.eventHandler.input2value,
              validate: designCommons.bootstrapTable.eventHandler.validate,
              value2display: designCommons.bootstrapTable.eventHandler.value2display
            }
          },
          {
            field: 'eventParams',
            title: '事件参数',
            editable: {
              mode: 'modal',
              onblur: 'ignore',
              type: 'wCustomForm',
              placement: 'left',
              savenochange: true,
              value2input: designCommons.bootstrapTable.eventParams.value2input,
              input2value: designCommons.bootstrapTable.eventParams.input2value,
              value2display: designCommons.bootstrapTable.eventParams.value2display
            }
          }
        ]
      }
    });
  }
  // 返回属性配置器
  component.prototype.getPropertyConfigurer = function () {
    var collectClass = 'w-configurer-option';
    var configurer = $.ui.component.BaseComponentConfigurer();
    configurer.prototype.onLoad = function ($container, options) {
      // 初始化页签项
      $('#widget_header_tabs ul a', $container).on('click', function (e) {
        e.preventDefault();
        $(this).tab('show');
      });
      var configuration = $.extend(true, {}, options.configuration);
      // 拼接在线人员
      if (configuration.rightNav && $.isArray(configuration.rightNav.menuItems)) {
        var menuItemIndex = configuration.rightNav.menuItems.findIndex(function (item, index) {
          if (item.uuid === 'onlinePerson') {
            return item;
          }
        });
        if (menuItemIndex == -1) {
          configuration.rightNav.menuItems.unshift({
            uuid: 'onlinePerson',
            text: '在线人员',
            hiddenName: '0',
            icon: {
              className: 'iconfont icon-ptkj-zaixianrenyuanshuaxin'
            },
            shouldAlwaysRetain: true,
            checked: false,
            hidden: '1'
          });
        }
      }
      this.initConfiguration(configuration, $container);
    };
    // 初始化配置信息
    configurer.prototype.initConfiguration = function (configuration, $container) {
      // 基本信息
      this.initBaseInfo(configuration, $container);
      //主导航信息
      this.initMainNavInfo(configuration, $container);
      // 右侧导航信息
      this.initRightNavInfo(configuration, $container);
    };
    configurer.prototype.initBaseInfo = function (configuration, $container) {
      // 设置值
      designCommons.setElementValue(configuration, $container);

      if (StringUtils.isNotBlank(configuration.logoFilePath)) {
        var _auth = getCookie('_auth');
        var token = getCookie(_auth);
        $('#logoFileImage', $container).show();
        $('#logoFileImage', $container).attr('src', ctx + configuration.logoFilePath + '&' + _auth + '=' + token);
      }

      // 初始化基本信息
      $('#logoFileImageSelectBtn', $container).on('click', function () {
        $.WCommonPictureLib.show({
          selectTypes: [1, 2],
          initPrevImg: $('#logoFilePath', $container).val(),
          confirm: function (data) {
            var pictureFilePath = data.filePaths;
            var _auth = getCookie('_auth');
            var token = getCookie(_auth);
            src = getBackendUrl() + pictureFilePath;
            $('#logoFileImage', $container).attr('src', src + '&' + _auth + '=' + token);
            $('#logoFilePath', $container).val(src);
            return;

            // var pictureFilePath = data.filePaths;
            // if (StringUtils.isBlank(pictureFilePath)) {
            //     return;
            // }
            //
            // function getBase64Image(img){
            //     var canvas = document.createElement("canvas");
            //     canvas.width=img.width;
            //     canvas.height=img.height;
            //     var ctx=canvas.getContext("2d");
            //     ctx.drawImage(img, 0, 0, img.width, img.height);
            //     var ext=img.src.substring(img.src.lastIndexOf(".")+1).toLowerCase();
            //     var dataUrl=canvas.toDataURL("images/"+ext);
            //     return dataUrl;
            // };
            // var jwt = getCookie(getCookie('_auth'));
            // var image = new Image();
            // image.src = getBackendUrl() + pictureFilePath + '&jwt=' + jwt;
            // image.crossOrigin = 'Anonymous';

            // image.onload=function(){
            //     var base64 = getBase64Image(image);
            //     $("#logoFilePath", $container).val(base64);
            //     $("#logoFileImage", $container).show();
            //     $("#logoFileImage", $container).attr("src", ctx + base64);
            // }
          }
        });
      });
      $('#logoFileImageRemoveBtn', $container).on('click', function () {
        $('#logoFilePath', $container).val('');
        $('#logoFileImage', $container).removeAttr('src');
      });

      var backgroupColorOption = "<option value='themeColor'>采用主题颜色</option>";
      for (var i = 0; i < constant.WIDGET_COLOR.length; i++) {
        var color = constant.WIDGET_COLOR[i];
        var selected = color.value == configuration.backgroudColor ? 'selected' : '';
        backgroupColorOption += "<option value='" + color.value + "'" + selected + '>' + color.name + '</option>';
      }
      $('#backgroudColor', $container).append(backgroupColorOption);

      // 二开JS模块
      $('#jsModule', $container).wSelect2({
        serviceName: 'appJavaScriptModuleMgr',
        params: {
          dependencyFilter: 'HeaderWidgetDevelopment'
        },
        labelField: 'jsModuleName',
        valueField: 'jsModule',
        remoteSearch: false,
        multiple: true
      });

      if (configuration.userSearch == '1') {
        $('#searchSwitch', $container).addClass('active');
        $('.showPlaceholder', $container).show();
        $('.showSearchShowSwitch', $container).show();
      } else {
        $('#searchSwitch', $container).removeClass('active');
        $('.showPlaceholder', $container).hide();
        $('.showSearchShowSwitch', $container).hide();
      }

      $('#searchSwitch', $container).on('click', function () {
        if ($(this).hasClass('active')) {
          $(this).removeClass('active');
          $('#userSearch', $container).val('0');
          $('.showPlaceholder', $container).hide();
          $('.showSearchShowSwitch', $container).hide();
        } else {
          $(this).addClass('active');
          $('#userSearch', $container).val('1');
          $('.showPlaceholder', $container).show();
          $('.showSearchShowSwitch', $container).show();
        }
      });

      if (configuration.userSearchShow == '1') {
        $('#searchShowSwitch', $container).addClass('active');
      }

      $('#searchShowSwitch', $container).on('click', function () {
        if ($(this).hasClass('active')) {
          $(this).removeClass('active');
          $('#userSearchShow', $container).val('0');
        } else {
          $(this).addClass('active');
          $('#userSearchShow', $container).val('1');
        }
      });

      if (configuration.fixedTop !== '0') {
        $('#fixedTopSwitch', $container).addClass('active');
      }

      $('#fixedTopSwitch', $container).on('click', function () {
        if ($(this).hasClass('active')) {
          $(this).removeClass('active');
          $('#fixedTop', $container).val('0');
        } else {
          $(this).addClass('active');
          $('#fixedTop', $container).val('1');
        }
      });
    };
    configurer.prototype.initRightNavInfo = function (configuration, $container) {
      var _self = this;
      var rightNav = configuration.rightNav || {};
      // 设置值
      designCommons.setElementValue(rightNav, $container);
      var appPageUuid = _self.component.pageDesigner.getPageUuid();
      var system = appContext.getCurrentUserAppData().getSystem();
      var productUuid = system.productUuid;

      // 一级导航
      var $element = $('.rightNav-menuItems', $container);
      var menuItems = $.isArray(rightNav.menuItems) ? rightNav.menuItems : [];
      // 填充默认导航
      fillDefaultNavs(menuItems);
      buildMenuItemBootstrapTable($element, 'rightNav-menuItems', menuItems, productUuid, '1');
    };
    // 默认导航：主题设置，个人信息，我的消息，退出登录,我的工作台
    function fillDefaultNavs(menuItems) {
      var hasPersonInfoNav = false;
      var hasMyMsgNav = false;
      // var hasThemeNav = false;
      var hasSwitchAccount = false;
      var hasLogOutNav = false;
      var hasWorkbench = false;
      var hasOnlinePerson = false;
      $.each(menuItems, function (i, menuItem) {
        if (menuItem.uuid === 'personInfo') {
          menuItem.shouldAlwaysRetain = true;
          hasPersonInfoNav = true;
        }
        if (menuItem.uuid === 'myMsg') {
          menuItem.shouldAlwaysRetain = true;
          hasMyMsgNav = true;
        }
        // if (menuItem.uuid === 'theme') {
        //   menuItem.shouldAlwaysRetain = true;
        //   hasThemeNav = true;
        // }
        if (menuItem.uuid === 'switchAccount') {
          menuItem.shouldAlwaysRetain = true;
          hasSwitchAccount = true;
        }
        if (menuItem.uuid === 'logOut') {
          menuItem.shouldAlwaysRetain = true;
          hasLogOutNav = true;
        }
        if (menuItem.uuid === 'workbench') {
          menuItem.shouldAlwaysRetain = true;
          hasWorkbench = true;
        }
        if (menuItem.uuid === 'onlinePerson') {
          menuItem.shouldAlwaysRetain = true;
          hasOnlinePerson = true;
        }
      });
      if (!hasOnlinePerson) {
        menuItems.push(_getDefaultRightNavMenuItem('onlinePerson'));
      }
      if (!hasPersonInfoNav) {
        menuItems.push(_getDefaultRightNavMenuItem('personInfo'));
      }
      if (!hasMyMsgNav) {
        menuItems.push(_getDefaultRightNavMenuItem('myMsg'));
      }
      // if (!hasThemeNav) {
      //   menuItems.push(_getDefaultRightNavMenuItem('theme'));
      // }
      if (!hasSwitchAccount) {
        debugger;
        menuItems.push(_getDefaultRightNavMenuItem('switchAccount'));
      }
      if (!hasLogOutNav) {
        menuItems.push(_getDefaultRightNavMenuItem('logOut'));
      }
      if (!hasWorkbench) {
        menuItems.push(_getDefaultRightNavMenuItem('workbench'));
      }
    }

    function _getDefaultRightNavMenuItem(uuid) {
      return DefaultConfiguration.rightNav.menuItems.find(function (menuItem) {
        return menuItem.uuid === uuid;
      });
    }

    configurer.prototype.initMainNavInfo = function (configuration, $container) {
      var _self = this;
      var mainNav = configuration.mainNav || {};
      // 设置值
      designCommons.setElementValue(mainNav, $container);
      var appPageUuid = _self.component.pageDesigner.getPageUuid();
      var system = appContext.getCurrentUserAppData().getSystem();
      var productUuid = system.productUuid;
      // 一级导航
      var $element = $('.mainNav-menuItems', $container);
      var menuItems = mainNav.menuItems || [];
      buildMenuItemBootstrapTable($element, 'mainNav-menuItems', menuItems, productUuid, '2');
    };
    configurer.prototype.onOk = function ($container) {
      this.component.options.configuration = this.collectConfiguration($container);
    };
    configurer.prototype.getConfiguration = function ($container) {
      return this.collectConfiguration($container);
    };
    // 收集配置信息
    configurer.prototype.collectConfiguration = function ($container) {
      var configuration = {};
      // 基本信息
      this.collectBaseInfo(configuration, $container);
      //头部主导航
      this.collectMainNavInfo(configuration, $container);
      //右侧导航信息
      this.collectRightNavInfo(configuration, $container);
      return $.extend({}, configuration);
    };
    configurer.prototype.collectBaseInfo = function (configuration, $container) {
      var $form = $('#widget_header_tabs_base_info', $container);
      var opt = designCommons.collectConfigurerData($form, collectClass);
      opt.subNavAndToolBarHidden = Boolean(opt.subNavAndToolBarHidden);
      $.extend(configuration, opt);
    };
    configurer.prototype.collectRightNavInfo = function (configuration, $container) {
      var $form = $('#widget_header_tabs_right_nav_info', $container);
      var opt = designCommons.collectConfigurerData($form, collectClass);
      // 右侧导航
      var $tableRightNavMenuItemsInfo = $('#table_rightNav-menuItems_info', $container);
      var menuItems = $tableRightNavMenuItemsInfo.bootstrapTable('getData');
      opt.menuItems = menuItems;
      configuration.rightNav = configuration.rightNav || {};
      $.extend(configuration.rightNav, opt);
    };
    configurer.prototype.collectMainNavInfo = function (configuration, $container) {
      var $form = $('#widget_header_tabs_main_nav_info', $container);
      var opt = designCommons.collectConfigurerData($form, collectClass);
      // 一级导航
      var $tableRightNavMenuItemsInfo = $('#table_mainNav-menuItems_info', $container);
      var menuItems = $tableRightNavMenuItemsInfo.bootstrapTable('getData');
      opt.menuItems = menuItems;
      configuration.mainNav = configuration.mainNav || {};
      $.extend(configuration.mainNav, opt);
    };
    return configurer;
  };
  // 返回组件定义HTML
  component.prototype.toHtml = function () {
    var options = this.options;
    var configuration = options.configuration || {};
    var id = this.getId();
    var mainNavBarTpl = new StringBuilder();
    mainNavBarTpl.append('<div class="navbar navbar-{0} ui-wHeader-mainNavbar w-header-main-navbar" role="navigation">');
    mainNavBarTpl.append('<div class="header-content clearfix">');
    mainNavBarTpl.append('<div class="navbar-header ui-wHeader-baseInfo">');
    mainNavBarTpl.append('<a class="navbar-brand ui-wHeader-logo w-header-logo" href="#"></a>');
    mainNavBarTpl.append('<a class="navbar-brand ui-wHeader-title" href="#"></a>');
    mainNavBarTpl.append('</div>');
    mainNavBarTpl.append('<ul class="nav navbar-nav ui-wHeader-mainNav">');
    mainNavBarTpl.append('</ul>');
    mainNavBarTpl.append('<ul class="nav navbar-nav navbar-right ui-wHeader-rightNav">');
    mainNavBarTpl.append('</ul>');
    mainNavBarTpl.append('</div>');
    mainNavBarTpl.append('</div>');

    var sb = new StringBuilder();
    sb.appendFormat('<div id="{0}" class="ui-wHeader">', id);
    var backgroundColor = configuration.backgroudColor;
    sb.appendFormat(mainNavBarTpl.toString(), backgroundColor);
    sb.appendFormat('</div>');
    return sb.toString();
  };

  // 返回组件定义JSON
  component.prototype.getDefinitionJson = function () {
    if (!this.options.configuration || JSON.stringify(this.options.configuration) === '{}') {
      this.options.configuration = DefaultConfiguration;
    }
    var options = this.options;
    var id = this.getId();
    options.id = id;
    return options;
  };
  return component;
});
