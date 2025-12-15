/*
 * Chained - jQuery / Zepto Cascader selects plugin
 *
 * Copyright (c) Well-Soft
 *
 * Licensed under the MIT license:
 *   http://www.opensource.org/licenses/mit-license.php
 *
 * Project home:
 *   http://www.well-soft.com
 *
 * Version: 1.0.0
 * author : zhongzh@well-soft.com
 *
 */

(function ($, window, document, undefined) {
  'use strict';
  /**
   * options.nodes
   * 		支持ztree的treeNode格式
   *
   * $("cascaderElement").cascader({
   *		// changeOnSelect : true,
   *		triggerType : "click",
   *		navTitls : ["level1","level2","level3"],
   *		nodes : treeNodes
   *	})
   *
   */
  function Cascader(element, options) {
    var self = this;
    self.options = options;
    self.positionArr = [];
    self.changeOnSelect = options.changeOnSelect || false;
    self.$element = $(element)
      .attr({
        autocomplete: 'off',
        readonly: 'readonly'
      })
      .addClass('ui-cascader');
    self.triggerType = options.triggerType === 'change' ? 'mouseenter' : 'click';
    self.$content = $('<div class="cascader-content"></div>'); // .appendTo($body);
    // 弹出层点击无响应
    self.$content.on('click.cascader', function (event) {
      event.preventDefault();
      event.stopPropagation();
    });
    self.$content.on('click.cascader', '.cascader-child li', function (event) {
      var $this = $(this); // 点击的对象
      var level = 0; // 当前点击的是第几层
      self.$content.find('.cascader-child').each(function (i, item) {
        if ($(item).is($this.parent())) {
          level = i;
        }
      });
      var index = $this.index(); // 当前点击的是这一层的第几个
      self.pushData(level, index);
      var data = self.popData();
      // 判断当前是否存在子层
      if (data.children && data.children.length > 0) {
        // 初始化子层
        self.initChild(data);
        $.fn.niceScroll && $('.cascader-child').niceScroll({ cursorcolor: '##999' });
      } else {
        // 判断触发方式
        if (self.triggerType === 'mouseenter') {
          self.$content.find('.cascader-child:gt(' + self.floor + ')').remove();
          // click事件先解除再定义，防止多次定义
          $this.off('click').on('click', function () {
            self.finishInitData();
          });
        } else {
          self.finishInitData(index);
        }
      }
    });
    self.$element.off(self.triggerType).on(self.triggerType, function (event) {
      event.preventDefault();
      event.stopPropagation();

      if (self.$element.parent().find('.icon-ptkj-xianmiaojiantou-shang').length == 0) {
        $(document.body).trigger('click.cascader');
        self.show(event);
      } else {
        self.hide(event);
        $(document.body).trigger('click.cascader');
      }
    });

    self.$element.siblings('.clear-btn').on('click', function () {
      self.clear();
    });

    // 渲染第一层
    self.$content.append(self._tpl((self.nodes = options.nodes)));
    $.fn.niceScroll && $('.cascader-child').niceScroll({ cursorcolor: '##999' });

    $('.widget-main')
      .add('#dyform')
      .add(window)
      .each(function () {
        $(this).on('scroll', function (e) {
          self.resetPosition();
        });
      });
  }
  $.extend(Cascader.prototype, {
    destroy: function () {
      this.$element.data('bs.cascader', null);
      this.$element.off(this.triggerType + '.cascader');
    },
    hide: function () {
      var self = this;
      self.$content.slideUp(100, function () {
        self.$content.detach();
      });
      self.$element.siblings('i.iconfont').replaceWith('<i class="iconfont icon-ptkj-xianmiaojiantou-xia"></i>');
    },
    show: function () {
      var self = this;
      var offset = self.$element.offset();
      var styles = {};
      // 总是在最前面
      $('.ui-front:visible').each(function (idx, element) {
        var zz = $(element).css('z-index');
        if (zz > 0 && (styles['z-index'] == null || styles['z-index'] < zz)) {
          styles['z-index'] = parseInt(zz, 10) + 1;
        }
      });
      self.$content.css(styles).addClass('ui-front');
      self.resetPosition();
      var $body = $(document.body).one('click.cascader', function (event) {
        self.hide(event);
      });
      self.$content.appendTo($body).slideDown(100);
      // 点击外层文档隐藏
      self.$element.siblings('i.iconfont').replaceWith('<i class="iconfont icon-ptkj-xianmiaojiantou-shang"></i>');
    },
    resetPosition: function () {
      var self = this;
      var contentHeight = self.$content.height() || 204;
      var offset = self.$element.offset();
      var wHeight = window.innerHeight;
      var styles = {
        left: offset.left
      };
      if (offset.top + contentHeight > wHeight) {
        styles.top = 'auto';
        styles.bottom = wHeight - offset.top;
      } else {
        styles.top = offset.top + self.$element.outerHeight();
        styles.bottom = 'auto';
      }

      self.$content.css(styles);
    },
    pushData: function (level, index) {
      var self = this;
      self.positionArr.length = self.floor = level; // 当前点击的是第几层
      self.positionArr.push(index); // 当前点击的是这一层的第几个
    },
    popData: function () {
      var self = this;
      // 等同下方注释
      var data = self.nodes[self.positionArr[0]];
      for (var i = 1; i <= self.floor; i++) {
        // console.log(data,data["children"],i,self.positionArr);
        data = data['children'][self.positionArr[i]];
      }
      return data;
    },
    getValue: function () {
      var self = this;
      var ids = [],
        texts = [],
        objs = [];
      if (self.positionArr.length > 0) {
        var data = self.nodes[self.positionArr[0]];
        objs.push(data);
        ids.push(data.id);
        texts.push(data.name);
        for (var i = 1; i <= self.floor; i++) {
          data = data['children'][self.positionArr[i]];
          objs.push(data);
          ids.push(data.id);
          texts.push(data.name);
        }
      }
      var separator = self.options.separator == undefined ? '/' : self.options.separator;
      return {
        objs: objs,
        ids: ids,
        texts: texts,
        id: ids.join(separator),
        text: texts.join(separator)
      };
    },
    setValue: function (value) {
      var self = this;
      var separator = self.options.separator == undefined ? '/' : self.options.separator;
      if (typeof value === 'string' && value.indexOf(separator) > 0) {
        value = value.split(separator);
      } else if (typeof value === 'string') {
        value = [value];
      }
      if ($.isArray(value) && value.length > 0) {
        var children = self.nodes; // 需要遍历的子数组
        // 等同于下面的注释
        $.each(value, function (index, val) {
          for (var i = 0; i < children.length; i++) {
            if (children[i].id == val) {
              self.pushData(index, i);
              var data = self.popData();
              children = data.children;
              if (children.length > 0) {
                self.initChild(data);
              } else {
                self.finishInitData();
              }
              return; // continue;
            }
          }
        });
      }
    },
    updateActive: function () {
      var self = this;
      // 删除后面的面板
      self.$content.find('.cascader-child:gt(' + self.floor + ')').remove();
      // 设置选中高亮
      var index = self.positionArr[self.floor];
      var $floor = self.$content.find('.cascader-child:eq(' + self.floor + ')');
      $floor
        .find('li:eq(' + index + ')')
        .addClass('active')
        .siblings('li')
        .removeClass('active');
    },
    triggerSelect: function () {
      var self = this;
      // 文本拼接
      var valueObj = self.getValue();
      var idStr = self.options.showLastLevels ? valueObj.ids[valueObj.ids.length - 1] : valueObj.id;
      var textStr = self.options.showLastLevels ? valueObj.ids[valueObj.texts.length - 1] : valueObj.text;
      self.$element.val(textStr);
      self.$element.attr('data-value', idStr);
      self.$element.trigger('change', valueObj);
    },
    initChild: function (data) {
      var self = this;
      self.updateActive();
      // 获取text值
      self.$content.append(self._tpl(data['children']));
      if (self.changeOnSelect) {
        self.triggerSelect();
      }
    },
    // 结束之后拿取数据
    finishInitData: function () {
      var self = this;
      // 设置选中高亮
      self.updateActive();
      // 隐藏
      self.hide();
      // 设值
      self.triggerSelect();
    },
    _tpl: function (nodes) {
      var html = '<ul class="cascader-child">';
      for (var i = 0; i < nodes.length; i++) {
        var node = nodes[i];
        if (!node.children || !node.children.length) {
          html += '<li data-index="' + i + '">' + node.name + '</li>';
        } else {
          html += '<li data-index="' + i + '">' + node.name + '<i class="iconfont icon-ptkj-xianmiaojiantou-you"></i></li>';
        }
      }
      return (html += '</ul>');
    },
    clear: function () {
      var self = this;
      self.floor = 0;
      self.updateActive();
      self.$content.find('.active').removeClass('active');
    }
  });

  $.fn.cascader = function (option) {
    var $this = $(this);
    var data = $this.data('bs.cascader');
    if (!data) {
      $this.data('bs.cascader', (data = new Cascader(this, option)));
    } else if (typeof option == 'string') {
      return data[option].apply(data, $.makeArray(arguments).slice(1));
    }
  };
  $.fn.cascader.Constructor = Cascader;
})(window.jQuery || window.Zepto, window, document);
