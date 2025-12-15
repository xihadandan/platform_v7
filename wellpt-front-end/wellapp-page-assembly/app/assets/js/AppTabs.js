/**
 * 应用tab标签页
 */
(function (root, factory) {
  'use strict';
  if (typeof define === 'function' && define.amd) {
    define(['jquery', 'appContext'], factory);
  } else if (typeof exports === 'object') {
    module.exports = factory(require('jquery', 'appContext'));
  } else {
    factory(root.jQuery, root.appContext);
  }
})(this, function ($, appContext) {
  var AppTabs = {
    defaultOptions: {
      siblings: null, //兄弟节点,用于标签页生成在兄弟节点后面，以及拖动的时候调整兄弟节点宽度
      tabs: [], //tab数据项
      tabShowType: 'float', //float:浮动层  extrusion：挤压
      defaultShowTab: '', //默认展示的标签页
      dragable: false, //是否可拖动
      tabPrefix: 'app_tab_', //定义tab页签的id前缀
      top: 0, //距离顶部距离
      bottom: 0, //距离底部距离
      tabWidth: '30%', //初始化的tab页签宽度
      dragMinLeftsetLimit: 1000,
      startDragPx: 10, //拖拽起步长度，只有拖拽移动鼠标超过这个数才会开始拖拽
      successCallback: $.noop //初始化成功后的回调函数
    },
    renderTabContainerLayout: function () {
      var layout =
        '<div id=rightMenuContainer>\n' +
        '\t<div style="top:' +
        this.options.top +
        'px!important;width:' +
        this.options.tabWidth +
        '" id="rightMenuTabContainer">\n' +
        '\t\t<div class="tab-content"></div>\n' +
        '\t</div>\n' +
        '\t<div class="tab-nav" style="top:160px !important;" id="rightMenuTabNavContainer">\n' +
        '\t\t<ul class="nav nav-pills nav-stacked pull-right-nav" role="tablist"></ul>\n' +
        '\t</div>\n' +
        '</div>';
      $(layout).insertAfter($(this.options.siblings));

      this.$tabContainer = $('#rightMenuContainer');
      this.$menuTabContainer = $('#rightMenuTabContainer');
      this.$tabNavContainer = $('#rightMenuTabNavContainer');
      this.$tabUl = this.$tabNavContainer.find('ul');
      this.$tabContent = this.$menuTabContainer.find('.tab-content');
    },

    renderTabs: function () {
      var _self = this;
      var options = this.options;

      for (var i = 0, len = options.tabs.length; i < len; i++) {
        var tab = options.tabs[i];
        var tabid = tab.id || options.tabPrefix + i;
        var tabname = tab.name;
        if (tabid == 'wf_work_flow') {
          this.$tabUl.append(
            $('<div>', {
              id: tabid
            }).append($('<a>').text(tabname))
          );
        } else {
          this.$tabUl.append(
            $('<li>', {
              role: 'presentation'
            }).append(
              $('<a>', {
                href: '#' + tabid,
                'aria-controls': tabid,
                role: 'tab',
                'data-toggle': 'tab',
                'data-title': tabname
              }).text(tabname)
            )
          );
        }

        var $child = [];
        if (options.dragable) {
          $child.push(
            $('<div>', {
              class: 'drag-line'
            })
          );
        }
        $child.push(
          $('<div>', {
            id: tabid + '_content'
          })
        );
        if (tabid != 'wf_work_flow') {
          this.$tabContent.append(
            $('<div>', {
              role: 'tabpanel',
              class: 'tab-pane',
              id: tabid
            }).append($child)
          );
        }
      }

      //如果是浮动的，则展示固定按钮：点击固定后才挤压表单内容
      if (options.tabShowType == 'float') {
        $('<button>', {
          // 分屏按钮
          class: 'btn iconfont icon-ptkj-dingzhu stick ',
          title: '取消分屏'
        }).insertAfter(this.$tabUl);

        $('.stick', this.$tabContainer)
          .off()
          .on('click', function () {
            var isStickOpen = $(this).is('.stick-active');
            if (isStickOpen) {
              _self.floatScreen(options);
              appModal.toast('取消分屏');
            } else {
              _self.splitScreen();
              appModal.toast('已分屏');
            }
            var tabStatus = isStickOpen ? 'float' : 'split';
            _self.setStatus('workflow.sign.process.bar.state', tabStatus);

            _self.showScrollBar();

            // $(window).trigger("resize");              // 作用于重新计算从表宽度
            $(document).trigger('siblingContainerResize');
          });

        //如果系统参数设置隐藏分屏按钮，则默认分屏
        var stickButtonMode = SystemParams.getValue('workflow.tabShowType.stickButton.mode');
        if (stickButtonMode == 'off') {
          $('#rightMenuTabNavContainer button.btn.stick').hide();
          setTimeout(function () {
            _self.splitScreen();
          }, 500);
        }
      }

      $('<button>', {
        // 展开按钮
        class: 'btn iconfont icon-ptkj-zuoshouzhan open-tab',
        title: '展开'
      }).insertAfter(this.$tabUl);

      var activeTab = ''; // 记录当前展开哪个页签
      $('.open-tab', this.$tabContainer)
        .off()
        .on('click', function () {
          if (_self.$tabUl.find('.active').size() > 0) {
            activeTab = _self.$tabUl.find('.active a');
            _self.$tabUl.find('.active a').trigger('click', false);
          } else if (activeTab != '') {
            activeTab.trigger('click');
          } else {
            if (_self.options.defaultShowTab == '') {
              _self.options.defaultShowTab = _self.$tabUl.find('li:eq(0) > a').text();
              _self.$tabUl.find('li:eq(0) > a').trigger('click');
            } else {
              if ($(this).hasClass('open-tab-active')) {
                _self.openType = 'close';
              } else {
                _self.openType = 'open';
              }
              _self.tabEvent();
            }
          }
        });

      _self.getStatus('workflow.sign.process.bar.state', 'bar');

      // 窗口大小调整事件
      $(window).bind(
        'resize',
        _.debounce(function (e) {
          // 调整自适应表单宽度
          if ($('#rightMenuTabNavContainer').find('li.active').length == 1) {
            _self.siblingContainerResize();
          }
        })
      );
    },

    floatScreen: function (options) {
      $('.stick', this.$tabContainer).removeClass('stick-active').removeClass('icon-ptkj-quxiaodingzhu').addClass('icon-ptkj-dingzhu');
      $(options.siblings).css('width', '100%');
      $('.stick', this.$tabContainer).attr('title', '分屏');
      this.stickOpen = false;
      this.showScrollBar();
    },
    splitScreen: function () {
      var self = this;
      this.stickOpen = true;
      $('.stick', this.$tabContainer).addClass('stick-active').removeClass('icon-ptkj-dingzhu').addClass('icon-ptkj-quxiaodingzhu');
      $('.stick', this.$tabContainer).attr('title', '取消分屏');
      if ($('#rightMenuTabNavContainer').find('li.active').length == 1) {
        self.siblingContainerResize();
      }
      this.showScrollBar();
    },
    openStyles: function () {
      $('.open-tab').addClass('open-tab-active').removeClass('icon-ptkj-zuoshouzhan').addClass('icon-ptkj-youshouzhan');
      $('.open-tab').attr('title', '关闭');
      this.showScrollBar();
    },
    closeStyles: function () {
      $('.open-tab').removeClass('open-tab-active').removeClass('icon-ptkj-youshouzhan').addClass('icon-ptkj-zuoshouzhan');
      $('.open-tab').attr('title', '展开');
      this.showScrollBar();
    },

    siblingContainerResize: function () {
      var self = this;
      //挤压展示、或者固定打开的情况下，计算表单容器的宽度
      if (self.options.tabShowType == 'extrusion' || self.stickOpen) {
        var bodyWidth = $(self.options.siblings).parent().width();
        // $(self.options.siblings).css('width', (bodyWidth - self.$menuTabContainer.width() - 5) / bodyWidth * 100 + "%");
        $(self.options.siblings).css('width', 'calc(100% - ' + (self.$menuTabContainer.width() + 5) + 'px)');
        $(document).trigger('siblingContainerResize');
        setTimeout(function () {
          $('.widget-main').getNiceScroll().resize();
        }, 50);
      }
    },

    resizePaddingBottom: function (targetTpane) {
      var _self = this;
      var $embedElement = $('#' + targetTpane + ' .embed-responsive:eq(0)');
      if ($embedElement.length > 0) {
        $embedElement.css(
          'padding-bottom',
          (function () {
            //计算padding-bottom:height/width
            var $parent = $('#' + targetTpane).parent();
            return (($('body').height() - _self.options.top - _self.options.bottom) / $parent.width()) * 100 + '%';
          })()
        );
      }
    },

    tabEvent: function () {
      var _self = this;

      //标签页点击事件
      $('li>a', this.$tabUl)
        .off()
        .on('click', function (e, setStatus) {
          if ($(this).attr('event-loaded')) {
            var tabState = $(this).parent().is('.active') ? 'close' : 'open';
            // if (setStatus !== false) {
            _self.setStatus('workflow.sign.process.tab.state', tabState);
            // }
            //显示状态下，再点击，则隐藏
            var tabpaneId = $(this).attr('aria-controls');
            if ($('#' + tabpaneId).is('.active')) {
              $('#' + tabpaneId).removeClass('active');
              $(this).parent().removeClass('active');
              if (_self.options.dragable) {
                $(_self.options.siblings).css('width', '100%');
              }

              _self.closeStyles();
              $(document).trigger('siblingContainerResize');
              _self.$menuTabContainer.removeClass('box-shadow');
            } else {
              $(this).tab('show');

              if (_self.options.tabShowType == 'float') {
                _self.$menuTabContainer.addClass('box-shadow');
              }

              _self.openStyles();

              _self.siblingContainerResize();
              //修复chrome浏览器下iframe滚动条丢失的情况
              var iframe = $('#' + tabpaneId).find('iframe')[0];
              if (iframe) {
                iframe.style.height = '99.9%';
                window.setTimeout(function () {
                  iframe.style.height = '100%';
                }, 100);
              }
            }

            // $(window).trigger("resize");
            e.preventDefault();
            return false;
          }
          // else {
          //   var tabState = $(this).parent().is('.active') ? 'close' : 'open';
          //   _self.setStatus('workflow.sign.process.tab.state', tabState);
          //   _self.openStyles();
          // }

          if (!_self.$menuTabContainer.attr('resizedone')) {
            _self.$menuTabContainer.css('width', _self.options.tabWidth);
            _self.actualInitWidth = _self.$menuTabContainer.width(); //实际的初始化后宽度，用于拖拽时候限制最小宽度
          }
          if (_self.options.tabShowType == 'float') {
            _self.$menuTabContainer.addClass('box-shadow');
          }

          _self.siblingContainerResize();

          $(this).attr('event-loaded', true);
          $(this).tab('show');

          var targetTabpane = $(this).attr('aria-controls');
          var index = parseInt(targetTabpane.replace(_self.options.tabPrefix, ''));
          var tab = _self.options.tabs[index];
          if (tab && tab.html) {
            $('#' + targetTabpane)
              .find('#' + targetTabpane + '_content')
              .html(tab.html);
          } else {
            var appTabOptions = {
              target: '_targetWidget',
              targetWidgetId: targetTabpane + '_content',
              refreshIfExists: false,
              container: $('#' + targetTabpane).find('div:eq(1)')
            };
            appContext.startApp($.extend(true, appTabOptions, tab));
            _self.resizePaddingBottom(targetTabpane);
          }
        });

      // 默认展开页签
      if (_self.options.defaultShowTab && _self.openType && _self.openType != 'close') {
        var $a = _self.$tabUl.find("a[data-title='" + _self.options.defaultShowTab + "']");
        if ($a.length == 1) {
          $a.trigger('click');
        }
      }

      if (_self.options.dragable) {
        _self.dragEvent();
      }

      $('li>a', this.$tabUl).attr('event-loaded', true);
    },

    showScrollBar: function () {
      // 是否显示表单滚动条
      var zIndex = -1;
      var tabActive = $('.open-tab').hasClass('open-tab-active');
      var stackActive = $('.stick').hasClass('stick-active');
      if (stackActive || !tabActive) {
        zIndex = 80;
      }

      $('body').find('.formScrollClass').css({
        zIndex: zIndex
      });
      setTimeout(function () {
        $('.widget-main').getNiceScroll().resize();
      }, 50);
    },

    dragEvent: function () {
      var beginDrag = false;
      var clickX;
      var bodyWidth = $('body').width();
      var $curentDragLine;
      var options = this.options;
      var _self = this;
      var startPagex = 0;
      var drag = function (e) {
        if (beginDrag) {
          $curentDragLine.parent().find('iframe').css('pointer-events', 'none'); //解决iframe拖拽卡顿现象
          clickX = e.pageX;
          if (Math.abs(clickX - startPagex) < options.startDragPx) {
            //拖动小于长度不触发
            e.preventDefault();
            return false;
          }
          if (clickX >= options.dragMinLeftsetLimit && bodyWidth - clickX >= _self.actualInitWidth) {
            _self.$menuTabContainer.width(bodyWidth - clickX - 38);
            _self.siblingContainerResize();
            _self.resizePaddingBottom($curentDragLine.parent().attr('id'));
          }
        }
      };
      var dragOver = function (e) {
        var el = $curentDragLine[0];
        if (el.releaseCapture) {
          el.releaseCapture();
          el.onmousemove = null;
          el.onmouseup = null;
        } else {
          document.onmousemove = null;
          document.onmouseup = null;
        }

        $curentDragLine.parent().find('iframe').css('pointer-events', 'auto');
        _self.$menuTabContainer.attr('resizedone', true);
        beginDrag = false;
        e.preventDefault();
        $('body').removeAttr('onselectstart');
      };

      //拖动效果
      $('.drag-line').on('mousedown', function (e) {
        startPagex = e.pageX;
        beginDrag = true;
        $curentDragLine = $(this);
        $('body').attr('onselectstart', 'return false;'); //移动过程中html的文字内容不可选择
        var el = $(this)[0];
        if (el.setCapture) {
          el.setCapture();
          el.onmousemove = drag;
          el.onmouseup = dragOver;
        } else {
          document.onmousemove = drag;
          document.onmouseup = dragOver;
        }
        e.preventDefault();
      });
    },

    init: function (options) {
      var _self = this;
      options = $.extend(true, this.defaultOptions, options);
      this.options = options;
      this.renderTabContainerLayout();
      this.renderTabs();
      this.tabEvent();
      _self.getStatus('workflow.sign.process.tab.state', 'tab');
      if ($.isFunction(options.successCallback)) {
        options.successCallback.call(_self);
      }
    },
    getStatus: function (datas, type) {
      var _self = this;
      $.ajax({
        url: ctx + '/api/user/preferences/getValue',
        type: 'get',
        data: {
          dataKey: datas,
          dataValue: type,
          moduleId: 'WORKFLOW'
        },
        success: function (result) {
          if (type == 'tab') {
            _self.openType = result.data;
            if (!result.data) {
              var tabsOpenMode = SystemParams.getValue('workflow.tabShowType.tabs.mode');
              if (tabsOpenMode == '2') {
                _self.closeStyles();
              } else if (tabsOpenMode != '2') {
                $('.open-tab', _self.$tabContainer).trigger('click');
                _self.openStyles();
              }
            } else if (result.data == 'close') {
              _self.closeStyles();
            } else if (result.data != 'close') {
              $('.open-tab', _self.$tabContainer).trigger('click');
              _self.openStyles();
            }
          } else {
            if (result.data == 'float') {
              _self.floatScreen(_self.options);
            } else {
              _self.splitScreen();
            }
          }
        }
      });
    },
    setStatus: function (datas, tabStatus) {
      $.ajax({
        url: ctx + '/api/user/preferences/save',
        type: 'POST',
        data: {
          dataKey: datas,
          dataValue: tabStatus,
          moduleId: 'WORKFLOW',
          remark: '流程样式记住上一次选择（分屏/悬浮）'
        },
        dataType: 'json',
        success: function () {
          console.log('保存状态码为：' + tabStatus);
        }
      });

      // JDS.call({
      //   service: 'orgApiFacade.saveUserProperty',
      //   data: [SpringSecurityUtils.getCurrentUserId(), datas, tabStatus],
      //   mask: false,
      //   success: function (result) {
      //     console.log('保存状态码为：' + tabStatus);
      //   }
      // });
    }
  };

  $.fn.appTabs = function (options) {
    AppTabs.init(options);
  };
});
