define(['ui_component', 'constant', 'commons', 'server', 'appContext', 'design_commons', 'appModal', 'formBuilder'], function (
  ui_component,
  constant,
  commons,
  server,
  appContext,
  designCommons,
  appModal,
  formBuilder
) {
  var component = $.ui.component.BaseComponent();

  var StringUtils = commons.StringUtils;
  var UUID = commons.UUID;
  var NAV_TREE_APP_TYPE = {
    // 业务应用树
    ADD: 1, // 添加分类
    ADD_CHILD: 2, // 添加链接
    EDIT: 3 // 修改
  };
  var NAV_TREE_LINK_TYPE = {
    // 常用链接树
    ADD: 1, // 添加分类
    ADD_CHILD: 2, // 添加链接
    EDIT: 3 // 修改
  };

  component.prototype.create = function () {};
  // 使用属性配置器
  component.prototype.usePropertyConfigurer = function () {
    return true;
  };
  // 返回属性配置器
  component.prototype.getPropertyConfigurer = function () {
    var collectClass = 'w-configurer-option';
    var navTreeCollectClass = 'w-nav-tree-option';
    var configurer = $.ui.component.BaseComponentConfigurer();
    configurer.prototype.onLoad = function ($container, options) {
      var _self = this.component;
      // 初始化页签项
      $('#widget_unified_portal_tabs ul a', $container).on('click', function (e) {
        e.preventDefault();
        $(this).tab('show');
      });
      var configuration = $.extend(true, {}, options.configuration);
      console.log(11, configuration);
      this.initConfiguration(configuration, $container);
    };
    // 初始化配置信息
    configurer.prototype.initConfiguration = function (configuration, $container) {
      this.initBaseInfo(configuration, $container);
      this.initAppInfo(configuration, $container);
      this.initLinkInfo(configuration, $container);
    };
    configurer.prototype.initBaseInfo = function (configuration, $container) {
      // 设置值
      designCommons.setElementValue(configuration, $container);
      if (StringUtils.isNotBlank(configuration.logoFilePath)) {
        $('#logoFileImage', $container).show();
        $('#logoFileImage', $container).attr('src', ctx + configuration.logoFilePath);
      }
      if (configuration.themesObj && configuration.themesObj.length > 0) {
        var html = '';
        $.each(configuration.themesObj, function (index, item) {
          html +=
            "<div class='themes-items'>" +
            "<input type='text' placeholder='请输入主题' value='" +
            item.themeName +
            "'>" +
            "<button type='button' class='well-btn w-btn-primary themes-items-btn'>选择主题背景</button>";
          if (item.themeImg) {
            html += "<div class='themes-img themes-img-show'><img src='" + item.themeImg + "'/></div>";
          } else {
            html += "<div class='themes-img'><img src='" + item.themeImg + "'/></div>";
          }
          html += "<i class='remove-themes-btn iconfont icon-ptkj-shanchu'></i>";
          html += "<i class='theme-default-tag iconfont icon-ptkj-youshangjiao-xuanzhong'></i>";
          html += '</div>';
        });
        $('#addThemes').before($(html));
      }
      $('#logoFileImageSelectBtn', $container).on('click', function () {
        $.WCommonPictureLib.show({
          selectTypes: [1, 2],
          initPrevImg: $('#logoFilePath', $container).val(),
          confirm: function (data) {
            var pictureFilePath = data.filePaths;
            if (StringUtils.isBlank(pictureFilePath)) {
              return;
            }
            $('#logoFilePath', $container).val(pictureFilePath);
            $('#logoFileImage', $container).show();
            $('#logoFileImage', $container).attr('src', ctx + pictureFilePath);
          }
        });
      });
      $('#logoFileImageRemoveBtn', $container).on('click', function () {
        $('#logoFilePath', $container).val('');
        $('#logoFileImage', $container).removeAttr('src');
      });

      $('#addThemes', $container).on('click', function () {
        var html =
          "<div class='themes-items'>" +
          "<input type='text' placeholder='请输入主题'>" +
          "<button type='button' class='well-btn w-btn-primary themes-items-btn'>选择主题背景</button>" +
          "<div class='themes-img'><img src=''/></div><i class='remove-themes-btn iconfont icon-ptkj-shanchu'></i>";
        html += "<i class='theme-default-tag iconfont icon-ptkj-youshangjiao-xuanzhong'></i>";
        html += '</div>';
        $(this).before($(html));

        $('.themes-items-btn')
          .off()
          .on('click', function () {
            var img = $(this).next('.themes-img').find('img');
            $.WCommonPictureLib.show({
              selectTypes: [1, 2],
              initPrevImg: img.attr('src'),
              confirm: function (data) {
                var pictureFilePath = data.filePaths;
                if (StringUtils.isBlank(pictureFilePath)) {
                  return;
                }
                img
                  .attr('src', ctx + pictureFilePath)
                  .parent()
                  .css('opacity', 1);
              }
            });
          });
        $('.remove-themes-btn').click(function () {
          $(this).parents('.themes-items').remove();
        });
      });
      $('.themes-items-btn')
        .off()
        .on('click', function () {
          var img = $(this).next('.themes-img').find('img');
          $.WCommonPictureLib.show({
            selectTypes: [1, 2],
            initPrevImg: img.attr('src'),
            confirm: function (data) {
              var pictureFilePath = data.filePaths;
              if (StringUtils.isBlank(pictureFilePath)) {
                return;
              }
              img
                .attr('src', ctx + pictureFilePath)
                .parent()
                .css('opacity', 1);
            }
          });
        });
      $('.remove-themes-btn').click(function () {
        $(this).parents('.themes-items').remove();
      });

      // 二开JS模块
      $('#jsModule', $container).wSelect2({
        serviceName: 'appJavaScriptModuleMgr',
        params: {
          dependencyFilter: 'UnifiedPortalWidgetDevelopment'
        },
        labelField: 'jsModuleName',
        valueField: 'jsModule',
        remoteSearch: false,
        multiple: true
      });
    };
    configurer.prototype.initAppInfo = function (configuration, $container) {
      var _self = this;
      // 设置值
      designCommons.setElementValue(configuration, $container);

      var appPageUuid = _self.component.pageDesigner.getPageUuid();
      var system = appContext.getCurrentUserAppData().getSystem();
      var productUuid = system.productUuid;

      var $nav = $('#widget_unified_portal_tabs_app_info');
      var navTreeId = 'unifiedPortalTree';
      var $navTree = $('#' + navTreeId);
      var $navTreeNodeProp = $('#appNavRightInfo');
      // 图标背景
      var colors = [
        '#64B3EA',
        '#FCB862',
        '#92D678',
        '#F38F8A',
        '#9584EE',
        '#44C8E5',
        '#E28DE9',
        '#65DEAA',
        '#FE88A7',
        '#77A8EE',
        '#D9D688',
        '#F6ABBB'
      ];
      var items = '';
      // 渲染背景颜色
      $.each(colors, function (index, item) {
        items +=
          '<li class="bg-choose-item" data-color="' +
          item +
          '" style="background: ' +
          item +
          '"><i class="iconfont icon-ptkj-dagou"></i></li>';
      });
      $('.bg-choose-box').append(items);
      // 色块选择
      $('.bg-choose-item', $container).click(function () {
        var color = $(this).data('color');
        $(this).addClass('hasChoose').siblings().removeClass('hasChoose');
        $('#navTreeIconBg').val(color);
        $('#selectIconBg').find('div').data('color', color).css('background', color);
      });
      // 关闭色块
      $(document).click(function (e) {
        if (!$(e.target).hasClass('icon-bg-wrap') && !$(e.target).parents().hasClass('icon-bg-wrap')) {
          $('.bg-choose-list').hide();
          $('.minicolors').hide();
        }
      });
      // 显示色块
      $('#selectIconBg').click(function () {
        var currColor = $(this).find('div').data('color');
        $('.bg-choose-list').css('display', 'inline-block');
        var chooseItem = $('.bg-choose-list').find('.bg-choose-item');
        chooseItem.removeClass('hasChoose');
        $.each(chooseItem, function (index, item) {
          if ($(item).data('color') == currColor) {
            $(item).addClass('hasChoose');
          }
        });
      });
      // 更多
      $('.bg-choose-more', $container).click(function () {
        $('.bg-choose-list').hide();
        if ($('.minicolors').size() > 0) {
          $('.minicolors').show();
        } else {
          $('#navTreeIconBg').minicolors({
            control: 'hue',
            format: 'hex',
            color: '#0070C0',
            letterCase: 'lowercase',
            opacity: false,
            position: 'bottom left',
            theme: 'bootstrap',
            change: function (value, opacity) {
              $('#navTreeIconBg').focus();
              $('#selectIconBg').find('div').data('color', value).css('background', value);
            },
            hide: function () {
              $('#navTreeIconBg').hide();
              $($container).find('.minicolors').hide();
            },
            show: function () {
              $('#navTreeIconBg').focus();
            }
          });
        }
      });

      // 初始化自定义导航数据
      var zNodes = [];
      if (configuration && configuration.nav) {
        zNodes = configuration.nav;
      }
      // 选择图标
      $('#navTreeIconSelectBtn', $container).on('click', function () {
        $.WCommonPictureLib.show({
          selectTypes: [3],
          confirm: function (data) {
            var fileIDs = data.fileIDs;
            $('#navTreeIcon').val(fileIDs);
            $('#navTreeIconSnap', $container).attr('iconClass', fileIDs);
            $('#navTreeIconSnap', $container).attr('class', fileIDs);
          }
        });
      });

      // 绑定目标位置值变化事件
      $("input[name='navTreeTargetPosition'],input[name='targetPosition']", $nav)
        .on('change', function () {
          var navTreeTargetPosition = $("input[name='" + this.name + "']:checked", $nav).val();
          if (navTreeTargetPosition == constant.TARGET_POSITION.TARGET_WIDGET) {
            $('.navTreeTargetWidgetIdRow,.intfNavTreeTargetWidgetIdRow', $nav).show();
          } else {
            $('.navTreeTargetWidgetIdRow,.intfNavTreeTargetWidgetIdRow', $nav).hide();
          }
        })
        .trigger('change');

      // 目标组件ID
      $('#navTreeTargetWidgetId', $container).wSelect2({
        serviceName: 'appWidgetDefinitionMgr',
        queryMethod: 'loadLayoutSelectData',
        selectionMethod: 'loadLayoutSelectDataByIds',
        labelField: 'navTreeTargetWidgetName',
        valueField: 'navTreeTargetWidgetId',
        params: {
          appPageUuid: appPageUuid,
          uniqueKey: 'id',
          includeWidgetRef: 'true'
        },
        remoteSearch: false
      });

      $('#targetWidgetId', $container).wSelect2({
        serviceName: 'appWidgetDefinitionMgr',
        queryMethod: 'loadLayoutSelectData',
        selectionMethod: 'loadLayoutSelectDataByIds',
        labelField: 'targetWidgetName',
        valueField: 'targetWidgetId',
        params: {
          appPageUuid: appPageUuid,
          uniqueKey: 'id',
          includeWidgetRef: 'true'
        },
        remoteSearch: false
      });

      $('#navTreeEventHanlderName,#eventHanlderName', $container).each(function () {
        $(this).AppEvent({
          ztree: {
            params: [productUuid]
          },
          okCallback: function ($el, data) {
            if (data) {
              if ($el.attr('id') === 'eventHanlderName') {
                $('#eventHanlderId', $container).val(data.id);
                $('#eventHanlderType', $container).val(data.data.type);
                $('#eventHanlderPath', $container).val(data.data.path);
                $('#widgetViewName').trigger('refresh');
                // 锚点设置
                $("input[name='eventHashType']", $container).removeAttr('checked');
                $("input[name='eventHashType']", $container).trigger('change');
                if (designCommons.isSupportsAppHashByAppPath(data.data.path)) {
                  $("input[name='eventHashType']", $container).removeAttr('disabled');
                } else {
                  $("input[name='eventHashType']", $container).attr('disabled', 'disabled');
                }
                $("input[name='eventHash']", $container).val('');
                $('#eventHashTree', $container).wCommonComboTree({
                  value: ''
                });
                return;
              }
              $('#navTreeEventHanlderId', $container).val(data.id);
              $('#navTreeEventHanlderType', $container).val(data.data.type);
              $('#navTreeEventHanlderPath', $container).val(data.data.path);

              // 锚点设置
              $("input[name='navTreeEventHashType']", $container).removeAttr('checked');
              $("input[name='navTreeEventHashType']", $container).trigger('change');
              if (designCommons.isSupportsAppHashByAppPath(data.data.path)) {
                $("input[name='navTreeEventHashType']", $container).removeAttr('disabled');
              } else {
                $("input[name='navTreeEventHashType']", $container).attr('disabled', 'disabled');
              }
              $("input[name='navTreeEventHash']", $container).val('');
              $('#navTreeEventHashTree', $container).wCommonComboTree({
                value: ''
              });
            }
          },
          clearCallback: function ($el) {
            if ($el.attr('id') === 'eventHanlderName') {
              $('#eventHanlderId,#eventHanlderType,#eventHanlderPath', $container).val('');
              $('#widgetViewName').trigger('refresh');
              // 锚点设置
              $("input[name='eventHashType']", $container).removeAttr('checked');
              $("input[name='eventHashType']", $container).trigger('change');
              $("input[name='eventHashType']", $container).attr('disabled', 'disabled');
              $("input[name='eventHash']", $container).val('');
              $('#eventHashTree', $container).wCommonComboTree({
                value: ''
              });
              return;
            }
            $('#navTreeEventHanlderId,#navTreeEventHanlderType,#navTreeEventHanlderPath', $container).val('');
            // 锚点设置
            $("input[name='navTreeEventHashType']", $container).removeAttr('checked');
            $("input[name='navTreeEventHashType']", $container).trigger('change');
            $("input[name='navTreeEventHashType']", $container).attr('disabled', 'disabled');
            $("input[name='navTreeEventHash']", $container).val('');
            $('#navTreeEventHashTree', $container).wCommonComboTree({
              value: ''
            });
          }
        });
      });
      // 锚点设置单选框取消选中
      $("input[name='navTreeEventHashType'], input[name='eventHashType']", $nav)
        .parent()
        .on('mouseup', function () {
          var $radio = $(this).find('input');
          if ($radio.attr('checked') == 'checked') {
            setTimeout(function () {
              $radio.removeAttr('checked');
              $radio.trigger('change');
            }, 1);
          }
        });
      // 锚点设置值变化事件
      $("input[name='navTreeEventHashType']", $nav)
        .on('change', function () {
          var navTreeEventHashType = $("input[name='" + this.name + "']:checked", $nav).val();
          $('.navTreeEventHashTypeRow', $nav).hide();
          if (navTreeEventHashType == '1') {
            $('.navTreeEventHashType' + navTreeEventHashType, $nav).show();
            // 事件处理
            $('#navTreeEventHashTree', $nav).wCommonComboTree({
              service: 'pageDefinitionService.getAppHashTreeByAppPath',
              serviceParams: [$('#navTreeEventHanlderPath', $nav).val()],
              multiSelect: false, // 是否多选
              parentSelect: false, // 父节点选择有效，默认无效
              onAfterSetValue: function (event, self, value) {
                $('#navTreeEventHash', $nav).val(value);
                var valueNodes = self.options.valueNodes;
                if (valueNodes && valueNodes.length == 1) {
                  var parantNode = valueNodes[0].getParentNode();
                  if (parantNode) {
                    $(this).val('/' + parantNode.name + '/' + valueNodes[0].name);
                  } else {
                    $(this).val('/' + valueNodes[0].name);
                  }
                }
              }
            });
          } else if (navTreeEventHashType == '2') {
            $('.navTreeEventHashType' + navTreeEventHashType, $nav).show();
          }
        })
        .trigger('change');
      // 锚点设置值变化事件
      $("input[name='eventHashType']", $nav)
        .on('change', function () {
          var eventHashType = $("input[name='" + this.name + "']:checked", $nav).val();
          $('.eventHashTypeRow', $nav).hide();
          if (eventHashType == '1') {
            $('.eventHashType' + eventHashType, $nav).show();
            // 事件处理
            $('#eventHashTree', $nav).wCommonComboTree({
              service: 'pageDefinitionService.getAppHashTreeByAppPath',
              serviceParams: [$('#eventHanlderPath', $nav).val()],
              multiSelect: false, // 是否多选
              parentSelect: false, // 父节点选择有效，默认无效
              onAfterSetValue: function (event, self, value) {
                $('#eventHash', $nav).val(value);
                var valueNodes = self.options.valueNodes;
                if (valueNodes && valueNodes.length == 1) {
                  var parantNode = valueNodes[0].getParentNode();
                  if (parantNode) {
                    $(this).val('/' + parantNode.name + '/' + valueNodes[0].name);
                  } else {
                    $(this).val('/' + valueNodes[0].name);
                  }
                }
              }
            });
          } else if (eventHashType == '2') {
            $('.eventHashType' + eventHashType, $nav).show();
          }
        })
        .trigger('change');

      // 事件参数
      var $element = $('.event-params', $container);
      builEventParamsBootstrapTable($element, 'navTreeEventParams', []);

      // 显示标签数量的视图组件选择控制
      $('#navTreeShowBadgeCount', $container)
        .on('change', function () {
          if (this.checked === true) {
            $('.show-badge-count', $container).show();
            if ($('input[name="navTreeGetBadgeCountWay"]:checked', $container).length == 0) {
              $('input[name="navTreeGetBadgeCountWay"][value="tableWidgetCount"]', $container).prop('checked', true);
            }
            $('input[name="navTreeGetBadgeCountWay"]', $container).trigger('change');
            $("input[name='navTreeGetBadgeDigit']", $container).trigger('change');
            $('#navTreeGetBadgeCountDataStore', $container).trigger('change');
            $("input[name='navTreeGetBadgeZero']", $container).trigger('change');
          } else {
            $('.show-badge-count', $container).hide();
            $('.show-badge-count-digit', $container).hide();
          }
        })
        .trigger('change');

      //徽章获取数量变化
      $('input[name="navTreeGetBadgeCountWay"]', $container)
        .on('change', function () {
          var val = $('input[name="navTreeGetBadgeCountWay"]:checked', $container).val();
          $('.badge-count-way', $container).hide();
          if (val) {
            $('.' + val, $container).show();
            if ($(this).val() == 'countJs') {
              $('#navTreeGetBadgeCountJs', $container).val('BootstrapTableViewGetCount');
            }
          }
        })
        .trigger('change');

      // 统计数量的数据仓库
      $('#navTreeGetBadgeCountDataStore', $container).wSelect2({
        serviceName: 'viewComponentService',
        queryMethod: 'loadSelectData',
        valueField: 'navTreeGetBadgeCountDataStore',
        labelField: 'navTreeGetBadgeCountDataStoreName',
        remoteSearch: false,
        params: {
          piUuid: _self.component.pageDesigner.getPiUuid()
        }
      });

      //统计数量的js脚本
      // $('#navTreeGetBadgeCountJs', $container).wSelect2({
      //   serviceName: 'appJavaScriptModuleMgr',
      //   valueField: 'navTreeGetBadgeCountJs',
      //   labelField: 'navTreeGetBadgeCountJsName',
      //   params: {
      //     dependencyFilter: 'GetCountBase'
      //   },
      //   queryCallBack: function () {
      //     // 去除tab获取徽章的js的方法
      //     var hasIndex = _.findIndex(this.data, {
      //       id: 'GetModuleMgrTabBadgeCountDevelopment'
      //     });
      //     if (hasIndex > -1) {
      //       this.data.splice(hasIndex, 1);
      //     }
      //   },
      //   remoteSearch: false
      // });

      //统计数量的js脚本对应的表格视图
      $('#navTreeGetBadgeCountJsTable', $container).wSelect2({
        serviceName: 'appWidgetDefinitionMgr',
        valueField: 'navTreeGetBadgeCountJsTable',
        params: {
          wtype: 'wBootstrapTable',
          uniqueKey: 'id',
          includeWidgetRef: 'true'
        },
        remoteSearch: false
      });

      // $('#navTreeGetBadgeCountJs', $container).on('change', function () {
      //   $('.countJsTable[js]', $container).hide();
      //   if ($(this).val() == 'BootstrapTableViewGetCount') {
      //     $(".countJsTable[js='BootstrapTableViewGetCount']", $container).show();
      //   }
      // });

      // 获取数量的视图组件
      $('#navTreeGetBadgeCountListViewId', $container).wSelect2({
        serviceName: 'appWidgetDefinitionMgr',
        labelField: 'navTreeGetBadgeCountListViewName',
        valueField: 'navTreeGetBadgeCountListViewId',
        params: {
          wtype: 'wBootstrapTable',
          uniqueKey: 'id',
          includeWidgetRef: 'true'
        },
        defaultBlank: true,
        remoteSearch: false
      });

      // 绑定徽章数值显示位数radio值变化事件
      $("input[name='navTreeGetBadgeDigit']", $container)
        .on('change', function () {
          var navTreeGetBadgeDigit = $("input[name='" + this.name + "']:checked", $container).val();
          defaultBadgeVal = _self.getBadgeNumberdisplayBit($container);
          if (navTreeGetBadgeDigit == 'default') {
            $('.show-badge-count-digit', $container).hide();
            var badgeTip = '徽章将按照全局设置显示数值的最大位数，当前最大显示为：';
            if (defaultBadgeVal == 'off') {
              $('.navTreeGetBadgeDigitTip').html(badgeTip + '不控制显示位数');
            } else {
              var badgeNo = '';
              for (var i = 0; i < parseInt(defaultBadgeVal); i++) {
                badgeNo += '9';
              }
              $('.navTreeGetBadgeDigitTip').html(badgeTip + badgeNo + '+');
            }
          } else {
            $('.show-badge-count-digit', $container).show();
            $('.navTreeGetBadgeDigitTip').html('请选择自徽章数值显示位数，自定义设置后将不受全局的徽章设置影响。');
            if ($('li.active', '.youshangjiao-xuanzhong-ul', $container).length == 0) {
              //当前未选中具体值,设置默认选中
              if (defaultBadgeVal < '5' || defaultBadgeVal == 'off') {
                $('li[data-digit=' + defaultBadgeVal + ']', '.youshangjiao-xuanzhong-ul', $container).trigger('click');
              } else {
                // $('li[data-digit="off"]', '.youshangjiao-xuanzhong-ul', $container).addClass('active').trigger('click');
              }
            }
          }
        })
        .trigger('change');

      //绑定徽章数值显示位数自定义值变化事件
      $('li', '.youshangjiao-xuanzhong-ul', $container)
        .on('click', function () {
          $('li', '.youshangjiao-xuanzhong-ul', $container).removeClass('active');
          $(this).addClass('active');
          $('#navTreeGetBadgeDigitNumber', $container).val($(this).attr('data-digit'));
        })
        .trigger('change');

      // 绑定徽章数值 是否显示0
      $("input[name='navTreeGetBadgeZero']", $container)
        .on('change', function () {
          var navTreeGetBadgeZero = $("input[name='" + this.name + "']:checked", $container).val();
          //全局设置 徽章数量为0的显示开关，为0不显示，1显示，默认0
          var defaultVal = SystemParams.getValue('badge.number.zero.show.switch');
          if (navTreeGetBadgeZero == 'default') {
            var badgeTip = '徽章数值为0时，将按照全局设置显示。当前徽章数值为0时';
            if (defaultVal == '0') {
              $('.navTreeGetBadgeZeroTip').html(badgeTip + '不显示徽章');
            } else {
              $('.navTreeGetBadgeZeroTip').html(badgeTip + '显示徽章');
            }
          } else if (navTreeGetBadgeZero == '1') {
            $('.navTreeGetBadgeZeroTip').html('徽章数值为0时，将显示徽章（自定义设置后将不受全局的徽章设置影响）');
          } else if (navTreeGetBadgeZero == '0') {
            $('.navTreeGetBadgeZeroTip').html('徽章数值为0时，将不显示徽章（自定义设置后将不受全局的徽章设置影响）');
          }
        })
        .trigger('change');

      var treeSetting = {
        edit: {
          drag: {
            autoExpandTrigger: true,
            isCopy: false,
            isMove: true,
            prev: true,
            inner: true,
            next: true
          },
          enable: true,
          showRemoveBtn: false,
          showRenameBtn: false
        },
        view: {
          dblClickExpand: false,
          selectedMulti: false
        },
        data: {
          simpleData: {
            enable: false
          }
        },
        callback: {
          onClick: function (event, treeId, treeNode) {
            clearNavTree(); // 清理配置
            navTreeOpType = NAV_TREE_APP_TYPE.EDIT;
            var bean = treeNodeData2Bean(treeNode.data);
            designCommons.setElementValue(bean, $container);
            if (treeNode.level == 0) {
              $('.unified-portal-app-info').hide();
            } else {
              $('.unified-portal-app-info').show();

              var iconClass = $('#navTreeIconSnap', $nav).attr('iconClass');
              $('#navTreeIconSnap', $nav).removeClass(iconClass);
              $('#navTreeIconSnap', $nav).attr('iconClass', bean.navTreeIcon);
              $('#navTreeIconSnap', $nav).addClass(bean.navTreeIcon);
              $("input[name='navTreeShowBadgeCount']", $nav).trigger('change');
              // $("input[name='navTreeGetBadgeCountListViewId']", $nav).trigger('change');
              if (bean.navTreeGetBadgeDigit == 'defined') {
                //位数自定义
                if (bean.navTreeGetBadgeDigitNumber) {
                  $('li[data-digit=' + bean.navTreeGetBadgeDigitNumber + ']', '.youshangjiao-xuanzhong-ul', $nav).trigger('click');
                }
              } else {
                // 初始化时，位数使用系统默认时，清除自定义选中
                $('#navTreeGetBadgeDigit_default', $container).trigger('click');
                $('#navTreeGetBadgeDigitNumber', $nav).val('');
                $('li', '.youshangjiao-xuanzhong-ul', $container).removeClass('active');
              }
              $("input[name='navTreeTargetPosition']", $nav).trigger('change');
              $("input[name='navTreeTargetWidgetId']", $nav).trigger('change');
              $('#navTreeIconBg').val(bean.navTreeIconBg);
              var $div = $('#selectIconBg').find('div');
              $div.css('background', bean.navTreeIconBg);
              if ($div.data('color')) {
                $div.data('color', bean.navTreeIconBg);
              } else {
                $div.attr('data-color', bean.navTreeIconBg);
              }

              $('#navTreeEventHanlderId', $nav).val(bean.navTreeEventHanlderId);
              $('#navTreeEventHanlderName', $nav).val(bean.navTreeEventHanlderName);
              $('#navTreeEventHanlderName', $nav).AppEvent('setValue', bean.navTreeEventHanlderId);

              if (bean.navTreeDefaultShow) {
                $('#defaultShow').prop('checked', true);
              } else {
                $('#defaultShow').prop('checked', false);
              }
              $("input[name='navTreeEventHashType']", $nav).trigger('change');
              if (designCommons.isSupportsAppHashByAppPath(bean.navTreeEventHanlderPath)) {
                $("input[name='navTreeEventHashType']", $nav).removeAttr('disabled');
              } else {
                $("input[name='navTreeEventHashType']", $nav).attr('disabled', 'disabled');
              }
              $('#navTreeEventHashTree', $nav).wCommonComboTree({
                value: bean.navTreeEventHash
              });
              // 事件参数
              var $element = $('.event-params', $nav);
              var eventParameters = bean.navTreeEventParameters || [];
              builEventParamsBootstrapTable($element, 'navTreeEventParams', eventParameters);
            }
          }
        }
      };
      // 清空导航树属性设置
      var clearNavTree = function ($container) {
        var data = {
          uuid: '',
          name: '',
          icon: '',
          isParent: false,
          eventType: '',
          targetPosition: '_self',
          targetWidgetName: '',
          targetWidgetId: '',
          refreshIfExists: false,
          eventHanlderName: '',
          eventHanlderId: '',
          eventHanlderPath: '',
          eventHanlderType: '',
          eventParameters: [],
          eventParams: {},
          navTreeIconBg: '',
          defaultShow: false
        };
        var bean = treeNodeData2Bean(data);
        designCommons.setElementValue(bean, $container);
        // 清空图标显示
        var iconClass = $('#navTreeIconSnap', $container).attr('iconClass');
        $('#navTreeIconSnap', $container).removeClass(iconClass);
        // 清空目标窗口
        $("input[name='navTreeTargetPosition']", $container).trigger('change');
        $('#navTreeEventHanlderId,#navTreeEventHanlderName', $container).val('');
        $("input[name='navTreeShowBadgeCount']", $nav).trigger('change');
        $('li', '.youshangjiao-xuanzhong-ul', $container).removeClass('active');
      };
      var zTree = $.fn.zTree.init($navTree, treeSetting, zNodes);
      _self.navTree = zTree;
      // 导航界面当前编辑状态
      var navTreeOpType = 0;
      // 检测选择的结点
      var checkSelectedNode = function (type) {
        var nodes = zTree.getNodes();
        if (nodes.length == 0 && type == 'child') {
          appModal.warning('请先添加分类!');
          return false;
        } else if (nodes.length == 0) {
          return true;
        }
        var selectedNodes = zTree.getSelectedNodes();
        if (selectedNodes.length == 0) {
          appModal.warning('请先选择节点!');
          return false;
        }
        if (selectedNodes.length > 1) {
          appModal.warning('只能选择一个节点!');
          return false;
        }
        return true;
      };
      // 获取选择的结点
      var getSelectedNode = function () {
        var selectedNodes = zTree.getSelectedNodes();
        if (selectedNodes.length == 0) {
          return null;
        }
        return selectedNodes[0];
      };

      // 设置事件
      var zTree = $.fn.zTree.getZTreeObj(navTreeId);
      // 添加分类
      $('#unified_portal_tree_add').on('click', function () {
        if (!checkSelectedNode('')) {
          return;
        }
        clearNavTree();
        $('.unified-portal-app-info').hide();
        var uuid = UUID.createUUID();
        $('#navTreeUuid', $container).val(uuid);
        navTreeOpType = NAV_TREE_APP_TYPE.ADD;
      });
      // 添加应用
      $('#unified_portal_tree_add_child').on('click', function () {
        if (!checkSelectedNode('child')) {
          return;
        }
        clearNavTree();
        $('.unified-portal-app-info').show();
        var uuid = UUID.createUUID();
        $('#navTreeUuid', $container).val(uuid);
        navTreeOpType = NAV_TREE_APP_TYPE.ADD_CHILD;
      });
      // 删除
      $('#unified_portal_tree_remove').on('click', function () {
        if (!checkSelectedNode()) {
          return;
        }
        appModal.confirm('确认要删除吗?', function (result) {
          if (result) {
            clearNavTree();
            var selectedNode = getSelectedNode();
            zTree.removeNode(selectedNode);
          }
        });
      });
      $('#unified_portal_tree_up').on('click', function () {
        moveUp(zTree);
      });
      $('#unified_portal_tree_down').on('click', function () {
        moveDown(zTree);
      });
      // 保存
      $('#unified_portal_tree_save').on('click', function () {
        if (navTreeOpType == 0) {
          var nodes = zTree.getNodes();
          if (nodes.length != 0) {
            return;
          } else {
            navTreeOpType = NAV_TREE_APP_TYPE.ADD;
          }
        }
        var selectedNode = getSelectedNode();
        if (selectedNode == null && zTree.getNodes().length != -0) {
          return;
        }
        var navTreeParentNode = selectedNode;
        if (navTreeOpType == NAV_TREE_APP_TYPE.ADD) {
          if (selectedNode && selectedNode.level == 0) {
            navTreeParentNode = selectedNode.getParentNode();
          } else {
            navTreeParentNode = '';
          }
        } else if (navTreeOpType == NAV_TREE_APP_TYPE.ADD_CHILD && selectedNode.level == 1) {
          navTreeParentNode = selectedNode.getParentNode();
        }

        var navTreeName = $("input[name='navTreeName']", $navTreeNodeProp).val();
        var navTreeUuid = $("input[name='navTreeUuid']", $navTreeNodeProp).val();
        if (StringUtils.isBlank(navTreeName)) {
          appModal.warning('名称不能为空！');
          return;
        }
        var node = {
          id: navTreeUuid,
          name: navTreeName,
          data: {}
        };

        var bean = designCommons.collectConfigurerData($navTreeNodeProp, navTreeCollectClass);
        node.data = bean2TreeNodeData(bean);
        node.data.isParent = Boolean(node.data.isParent);
        node.data.refreshIfExists = Boolean(node.data.refreshIfExists);
        node.data.defaultShow = Boolean(node.data.defaultShow);

        // 事件参数
        var eventParameters = $navTreeNodeProp.find('#table_navTreeEventParams_info').bootstrapTable('getData');
        node.data.eventParameters = eventParameters || [];
        var eventParams = {};
        $.map(node.data.eventParameters, function (option) {
          eventParams[option.name] = option.value;
        });
        node.data.eventParams = eventParams;

        // 添加/更新节点
        if (navTreeOpType == NAV_TREE_APP_TYPE.ADD || navTreeOpType == NAV_TREE_APP_TYPE.ADD_CHILD) {
          selectedNodes = zTree.addNodes(navTreeParentNode, node);
          zTree.selectNode(selectedNodes[0]);
          navTreeOpType = NAV_TREE_APP_TYPE.EDIT;
        } else if (navTreeOpType == NAV_TREE_APP_TYPE.EDIT) {
          $.extend(selectedNode, node, true);
          zTree.updateNode(selectedNode);
        }

        $.WCommonAlert('保存成功！');
      });
    };
    configurer.prototype.initLinkInfo = function (configuration, $container) {
      var _self = this;
      // 设置值
      designCommons.setElementValue(configuration, $container);

      var navTreeId = 'linkNavTree';
      var $navTree = $('#' + navTreeId);
      var $navTreeNodeProp = $('#linkTreeInfo');
      // 初始化自定义导航数据
      var zNodes = [];
      if (configuration && configuration.linkNavTree) {
        zNodes = configuration.linkNavTree;
      }

      var treeSetting = {
        edit: {
          drag: {
            autoExpandTrigger: true,
            isCopy: false,
            isMove: true,
            prev: true,
            inner: false,
            next: true
          },
          enable: true,
          showRemoveBtn: false,
          showRenameBtn: false
        },
        view: {
          dblClickExpand: false,
          selectedMulti: false
        },
        data: {
          simpleData: {
            enable: false
          }
        },
        callback: {
          onClick: function (event, treeId, treeNode) {
            clearNavTree(); // 清理配置
            navTreeOpType = NAV_TREE_LINK_TYPE.EDIT;
            var bean = treeNodeData2Bean(treeNode.data);
            if (treeNode.level == 0) {
              $('.link-nav-address').hide();
            } else {
              $('.link-nav-address').show();
            }
            designCommons.setElementValue(bean, $container);
          }
        }
      };
      // 清空导航树属性设置
      var clearNavTree = function ($container) {
        var data = {
          linkUuid: '',
          linkName: '',
          linkAddress: ''
        };
        var bean = treeNodeData2Bean(data);
        designCommons.setElementValue(bean, $container);
      };
      var zTree = $.fn.zTree.init($navTree, treeSetting, zNodes);
      _self.linkNavTree = zTree;
      // 导航界面当前编辑状态
      var navTreeOpType = 0;
      // 检测选择的结点
      var checkSelectedNode = function (type) {
        var nodes = zTree.getNodes();
        if (nodes.length == 0 && type == 'child') {
          appModal.warning('请先添加分类!');
          return false;
        } else if (nodes.length == 0) {
          return true;
        }
        var selectedNodes = zTree.getSelectedNodes();
        if (selectedNodes.length == 0) {
          appModal.warning('请先选择节点!');
          return false;
        }
        if (selectedNodes.length > 1) {
          appModal.warning('只能选择一个节点!');
          return false;
        }
        return true;
      };
      // 获取选择的结点
      var getSelectedNode = function () {
        var selectedNodes = zTree.getSelectedNodes();
        if (selectedNodes.length == 0) {
          return null;
        }
        return selectedNodes[0];
      };

      // 设置事件
      var zTree = $.fn.zTree.getZTreeObj(navTreeId);
      // 添加同级
      $('#link_tree_add').on('click', function () {
        if (!checkSelectedNode('')) {
          return;
        }
        clearNavTree();
        $('.link-nav-address').hide();
        var uuid = UUID.createUUID();
        $('#navTreeLinkUuid', $container).val(uuid);
        navTreeOpType = NAV_TREE_LINK_TYPE.ADD;
      });
      // 添加子节点
      $('#link_add_child').on('click', function () {
        if (!checkSelectedNode('child')) {
          return;
        }
        clearNavTree();
        $('.link-nav-address').show();
        var uuid = UUID.createUUID();
        $('#navTreeLinkUuid', $container).val(uuid);
        navTreeOpType = NAV_TREE_LINK_TYPE.ADD_CHILD;
      });
      // 删除
      $('#link_tree_remove').on('click', function () {
        if (!checkSelectedNode()) {
          return;
        }
        appModal.confirm('确认要删除吗?', function (result) {
          if (result) {
            clearNavTree();
            var selectedNode = getSelectedNode();
            zTree.removeNode(selectedNode);
          }
        });
      });
      $('#link_tree_up').on('click', function () {
        moveUp(zTree);
      });
      $('#link_tree_down').on('click', function () {
        moveDown(zTree);
      });
      // 保存
      $('#link_tree_save').on('click', function () {
        if (navTreeOpType == 0) {
          var nodes = zTree.getNodes();
          if (nodes.length != 0) {
            return;
          } else {
            navTreeOpType = NAV_TREE_LINK_TYPE.ADD;
          }
        }
        var selectedNode = getSelectedNode();
        if (selectedNode == null && zTree.getNodes().length != -0) {
          return;
        }
        var navTreeParentNode = selectedNode;
        if (navTreeOpType == NAV_TREE_LINK_TYPE.ADD) {
          if (selectedNode && selectedNode.level == 0) {
            navTreeParentNode = selectedNode.getParentNode();
          } else {
            navTreeParentNode = '';
          }
        } else if (navTreeOpType == NAV_TREE_LINK_TYPE.ADD_CHILD && selectedNode.level == 1) {
          navTreeParentNode = selectedNode.getParentNode();
        }

        var navTreeName = $("input[name='navTreeLinkName']", $navTreeNodeProp).val();
        var navTreeUuid = $("input[name='navTreeLinkUuid']", $navTreeNodeProp).val();
        if (StringUtils.isBlank(navTreeName)) {
          appModal.warning('名称不能为空！');
          return;
        }
        var node = {
          id: navTreeUuid,
          name: navTreeName,
          isParent: '',
          data: {
            linkAddress: '',
            linkUuid: '',
            linkName: ''
          }
        };

        var bean = designCommons.collectConfigurerData($navTreeNodeProp, navTreeCollectClass);
        node.data = bean2TreeNodeData(bean);
        node.data.isParent = Boolean(node.data.isParent);

        // 添加/更新节点
        if (navTreeOpType == NAV_TREE_LINK_TYPE.ADD || navTreeOpType == NAV_TREE_LINK_TYPE.ADD_CHILD) {
          selectedNodes = zTree.addNodes(navTreeParentNode, node);
          zTree.selectNode(selectedNodes[0]);
          navTreeOpType = NAV_TREE_LINK_TYPE.EDIT;
        } else if (navTreeOpType == NAV_TREE_LINK_TYPE.EDIT) {
          $.extend(selectedNode, node, true);
          zTree.updateNode(selectedNode);
        }

        $.WCommonAlert('保存成功！');
      });
    };

    //获取徽章数量显示的系统参数，并处理
    configurer.prototype.getBadgeNumberdisplayBit = function ($container) {
      var _self = this;
      var defaultBadgeVal = 'off';
      //徽章数量显示位数默认值（全局）：2
      var defaultVal = SystemParams.getValue('badge.number.display.bit.default');
      if (defaultVal && parseInt(defaultVal)) {
        var defaultVal1 = parseInt(defaultVal);
        if (defaultVal1 < 1) {
          defaultVal = 'off';
        }
      } else {
        defaultVal = 'off';
      }
      //徽章数量显示位数开关（全局）：0 为关，表示不做处理；1 为开，表示使用默认值。
      var switchVal = SystemParams.getValue('badge.number.display.bit.switch');
      if (switchVal == '1') {
        defaultBadgeVal = defaultVal;
      }
      return defaultBadgeVal;
    };

    //验证徽章逻辑必填项
    configurer.prototype.validateBadgeInput = function (bean, $container) {
      var _self = this;
      var ispass = true;
      if (bean.navTreeShowBadgeCount) {
        //显示徽章
        if (bean.navTreeGetBadgeCountWay) {
          //选择获取数量的方式
          var wayItem = _.find(BADGE_WAY, {
            id: bean.navTreeGetBadgeCountWay
          }); //获取方式对应的下拉
          if (!bean[wayItem.$id]) {
            appModal.warning(wayItem.$valiTxt + '！');
            ispass = false;
          }
          if (bean[wayItem.$id] == 'BootstrapTableViewGetCount') {
            if (!bean.navTreeGetBadgeCountJsTable) {
              appModal.warning('请选择表格视图！');
              ispass = false;
            }
          }
        } else {
          appModal.warning('请选择获取数量的方式！');
          ispass = false;
        }
        if (bean.navTreeGetBadgeDigit == 'defined') {
          //位数自定义
          if (!bean.navTreeGetBadgeDigitNumber) {
            appModal.warning('请选择徽章自定义显示方式！');
            ispass = false;
          }
        }
      }
      return ispass;
    };

    configurer.prototype.onOk = function ($container) {
      this.component.options.configuration = this.collectConfiguration($container);
      if (this.component.options.configuration.name == '') {
        appModal.error('统一门户名称不能为空！');
        return false;
      }
    };
    // 收集配置信息
    configurer.prototype.collectConfiguration = function ($container) {
      var configuration = {};
      this.collectBaseInfo(configuration, $container);

      this.collectAppInfo(configuration, $container);
      this.collectLinkInfo(configuration, $container);
      return $.extend({}, configuration);
    };
    configurer.prototype.collectBaseInfo = function (configuration, $container) {
      var $form = $('#widget_unified_portal_tabs_base_info', $container);
      var opt = designCommons.collectConfigurerData($form, collectClass);

      opt.showCategory = Boolean(opt.showCategory);
      var themes = $('#themesContainer').find('.themes-items');
      var themesObj = [];
      if (themes.size() > 0) {
        $.each(themes, function (index, item) {
          var themeName = $(item).find('input').val();
          var themeImg = $(item).find('img').attr('src');
          themesObj.push({
            themeName: themeName,
            themeImg: themeImg
          });
        });
      }
      opt.themesObj = themesObj;
      opt.defaultThemes = themesObj[0];
      $.extend(configuration, opt);
    };
    configurer.prototype.collectAppInfo = function (configuration, $container) {
      var _self = this;
      var $form = $('#widget_unified_portal_tabs_app_info', $container);
      var opt = designCommons.collectConfigurerData($form, collectClass);
      opt.customDefined = Boolean(opt.customDefined);
      opt.nav = _self.navTree.getNodes();
      $.extend(configuration, opt);
    };
    configurer.prototype.collectLinkInfo = function (configuration, $container) {
      var _self = this;
      var $form = $('#widget_unified_portal_tabs_link_info', $container);
      var opt = designCommons.collectConfigurerData($form, collectClass);
      opt.linkNavTree = _self.linkNavTree.getNodes();
      opt.linkCustomDefined = Boolean(opt.linkCustomDefined);
      $.extend(configuration, opt);
    };
    return configurer;
  };

  var builEventParamsBootstrapTable = function ($element, name, data) {
    $element.bootstrapTable('destroy');
    $($element).html('');
    formBuilder.bootstrapTable.build({
      container: $element,
      name: name,
      ediableNest: true,
      table: {
        data: data,
        striped: true,
        idField: 'uuid',
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
            title: '参数名称',
            editable: {
              type: 'text',
              showbuttons: false,
              onblur: 'submit',
              mode: 'inline'
            }
          },
          {
            field: 'name',
            title: '参数名',
            editable: {
              type: 'text',
              showbuttons: false,
              onblur: 'submit',
              mode: 'inline'
            }
          },
          {
            field: 'value',
            title: '参数值',
            editable: {
              type: 'text',
              showbuttons: false,
              onblur: 'submit',
              mode: 'inline'
            }
          }
        ]
      }
    });
  };
  var bean2TreeNodeData = function (bean) {
    var data = {};
    for (var p in bean) {
      if (typeof p === 'string' && StringUtils.contains(p, 'navTree')) {
        data[StringUtils.uncapitalise(p.substring('navTree'.length))] = bean[p];
      } else {
        data[p] = bean[p];
      }
    }
    return data;
  };
  var treeNodeData2Bean = function (data) {
    var bean = {};
    for (var p in data) {
      if (typeof p === 'string') {
        bean['navTree' + StringUtils.capitalise(p)] = data[p];
      } else {
        bean[p] = data[p];
      }
    }
    return bean;
  };
  var moveUp = function (zTree) {
    var selectedNodes = zTree.getSelectedNodes();
    var nodes = zTree.getNodes();
    if (selectedNodes.length === 0) {
      appModal.warning('未选择要上移的节点！');
      return false;
    }
    for (var i = 0; i < nodes.length; i++) {
      if (selectedNodes[0].level == 0 && nodes.length > 1) {
        if (nodes[i].id == selectedNodes[0].id && i > 0) {
          zTree.moveNode(nodes[i - 1], selectedNodes[0], 'prev');
          return false;
        }
      } else {
        var children = nodes[i].children;
        for (var j = 0; j < children.length; j++) {
          if (children[j].id == selectedNodes[0].id && j > 0) {
            zTree.moveNode(children[j - 1], selectedNodes[0], 'prev');
            return false;
          }
        }
      }
    }
  };
  var moveDown = function (zTree) {
    var selectedNodes = zTree.getSelectedNodes();
    var nodes = zTree.getNodes();
    console.log(selectedNodes, nodes);
    if (selectedNodes.length === 0) {
      appModal.warning('未选择要下移的节点！');
      return false;
    }
    for (var i = 0; i < nodes.length; i++) {
      if (selectedNodes[0].level == 0 && nodes.length > 1) {
        if (nodes[i].id == selectedNodes[0].id && i < nodes.length - 1) {
          zTree.moveNode(nodes[i + 1], selectedNodes[0], 'next');
          return false;
        }
      } else {
        var children = nodes[i].children;
        for (var j = 0; j < children.length; j++) {
          if (children[j].id == selectedNodes[0].id && j < children.length - 1) {
            zTree.moveNode(children[j + 1], selectedNodes[0], 'next');
            return false;
          }
        }
      }
    }
  };

  // 返回组件定义
  component.prototype.getDefinitionJson = function () {
    var options = this.options;
    var id = this.getId();
    options.id = id;
    return options;
  };
  return component;
});
