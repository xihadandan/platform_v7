(function (factory) {
  'use strict';
  if (typeof define === 'function' && define.amd) {
    // AMD. Register as an anonymous module.
    define(['jquery'], factory);
  } else {
    // Browser globals
    factory(window.jQuery);
  }
})(function ($) {
  'use strict';
  if (!$.fn.bootstrapTable) {
    return;
  }
  var initBodyCaller;

  // it only does '%s', and return '' when arguments are undefined
  var sprintf = function (str) {
    var args = arguments;
    var flag = true;
    var i = 1;

    str = str.replace(/%s/g, function () {
      var arg = args[i++];

      if (typeof arg === 'undefined') {
        flag = false;
        return '';
      }
      return arg;
    });
    return flag ? str : '';
  };

  var groupBy = function (array, f) {
    var tmpGroups = {};
    array.forEach(function (o) {
      var groups = f(o);
      tmpGroups[groups] = tmpGroups[groups] || [];
      tmpGroups[groups].push(o);
    });

    return tmpGroups;
  };

  $.extend($.fn.bootstrapTable.defaults, {
    groupBy: false,
    groupByField: '',
    gCollapseClass: 'glyphicon glyphicon-menu-right',
    gExpandedClass: 'glyphicon glyphicon-menu-down',
    groupByFormatter: undefined
  });

  var Utils = $.fn.bootstrapTable.utils;
  var BootstrapTable = $.fn.bootstrapTable.Constructor;
  var _initSort = BootstrapTable.prototype.initSort;
  var _initBody = BootstrapTable.prototype.initBody;
  var _scrollTo = BootstrapTable.prototype.scrollTo;
  var _updateSelected = BootstrapTable.prototype.updateSelected;

  BootstrapTable.prototype.initSort = function () {
    _initSort.apply(this, Array.prototype.slice.apply(arguments));

    var that = this;
    this.tableGroups = [];

    if (this.options.groupBy && this.options.groupByField !== '') {
      if (this.options.sortName !== this.options.groupByField) {
        if (this.options.customSort && this.options.customSort !== $.noop) {
          Utils.calculateObjectValue(this.options, this.options.customSort, [this.options.sortName, this.options.sortOrder, this.data]);
        } else {
          this.data.sort(function (a, b) {
            var groupByFields = that.getGroupByFields();
            var fieldValuesA = [];
            var fieldValuesB = [];

            $.each(groupByFields, function (i, field) {
              fieldValuesA.push(a[field]);
              fieldValuesB.push(b[field]);
            });

            a = fieldValuesA.join();
            b = fieldValuesB.join();
            return a.localeCompare(b, undefined, { numeric: true });
          });
        }
      }

      var groups = groupBy(that.data, function (item) {
        var groupByFields = that.getGroupByFields();
        var groupValues = [];
        $.each(groupByFields, function (i, field) {
          groupValues.push(item[field]);
        });

        return groupValues.join(', ');
      });

      var index = 0;
      $.each(groups, function (key, value) {
        that.tableGroups.push({
          id: index,
          name: key,
          data: value
        });

        value.forEach(function (item) {
          if (!item._data) {
            item._data = {};
          }

          item._data['parent-index'] = index;
        });

        index++;
      });
    }
  };

  BootstrapTable.prototype.initBody = function () {
    initBodyCaller = true;

    _initBody.apply(this, Array.prototype.slice.apply(arguments));

    if (this.options.groupBy && this.options.groupByField !== '') {
      var that = this;
      var checkBox = false;
      var visibleColumns = 0;

      this.columns.forEach(function (column) {
        if (column.checkbox) {
          checkBox = true;
        } else {
          if (column.visible) {
            visibleColumns += 1;
          }
        }
      });

      if (this.options.detailView && !this.options.cardView) {
        visibleColumns += 1;
      }

      this.tableGroups.forEach(function (item) {
        var $groupBy = that.$body.find(sprintf('tr[data-group-index="%s"].groupBy', item.id));
        if ($groupBy.length <= 0) {
          var html = [];

          html.push(sprintf('<tr class="info groupBy expanded" data-group-index="%s">', item.id));

          if (that.options.detailView && !that.options.cardView) {
            html.push('<td class="detail"></td>');
          }

          if (checkBox) {
            html.push('<td class="bs-checkbox">', '<input name="btSelectGroup" type="checkbox" />', '</td>');
          }
          var formattedValue = item.name;
          if (typeof that.options.groupByFormatter === 'function') {
            formattedValue = that.options.groupByFormatter(item.name, item.id, item.data);
          }
          html.push(
            '<td',
            sprintf(' colspan="%s"', visibleColumns),
            sprintf(' class="%s"', that.options.gExpandedClass),
            '>',
            formattedValue,
            '</td>'
          );

          html.push('</tr>');

          that.$body.find('tr[data-parent-index="' + item.id + '"]:first').before($(html.join('')));
        }

        that.$body.find('tr[data-parent-index="' + item.id + '"]').each(function (idx, element) {
          var $el = $(element);
          var index = that.options.stackSeqNo ? $el.data('index') : idx;

          $el.find('td.bs-seqno').text(index + 1);
        });
      });

      this.$selectGroup = [];
      this.$body.find('[name="btSelectGroup"]').each(function () {
        var self = $(this);

        that.$selectGroup.push({
          group: self,
          item: that.$selectItem.filter(function () {
            return $(this).closest('tr').data('parent-index') === self.closest('tr').data('group-index');
          })
        });
      });

      this.$container.off('click', '.groupBy').on('click', '.groupBy', function () {
        var $this = $(this);
        if ($this.hasClass('expanded')) {
          $this.removeClass('expanded').find('td').removeClass(that.options.gExpandedClass).addClass(that.options.gCollapseClass);
        } else {
          $this.addClass('expanded').find('td').removeClass(that.options.gCollapseClass).addClass(that.options.gExpandedClass);
        }
        that.$body.find('tr[data-parent-index="' + $(this).closest('tr').data('group-index') + '"]').toggleClass('hidden');
      });

      this.$container.off('click', '[name="btSelectGroup"]').on('click', '[name="btSelectGroup"]', function (event) {
        event.stopImmediatePropagation();

        var self = $(this);
        var checked = self.prop('checked');
        that[checked ? 'checkGroup' : 'uncheckGroup']($(this).closest('tr').data('group-index'));
      });
    }

    initBodyCaller = false;
    this.updateSelected();
  };

  BootstrapTable.prototype.updateSelected = function () {
    if (!initBodyCaller) {
      _updateSelected.apply(this, Array.prototype.slice.apply(arguments));

      if (this.options.groupBy && this.options.groupByField !== '') {
        this.$selectGroup.forEach(function (item) {
          var checkGroup = item.item.filter(':enabled').length === item.item.filter(':enabled').filter(':checked').length;

          item.group.prop('checked', checkGroup);
        });
      }
    }
  };

  BootstrapTable.prototype.getGroupSelections = function (index) {
    var that = this;

    return this.data.filter(function (row) {
      return row[that.header.stateField] && row._data['parent-index'] === index;
    });
  };

  BootstrapTable.prototype.checkGroup = function (index) {
    this.checkGroup_(index, true);
  };

  BootstrapTable.prototype.uncheckGroup = function (index) {
    this.checkGroup_(index, false);
  };

  BootstrapTable.prototype.checkGroup_ = function (index, checked) {
    var rows;
    var filter = function () {
      return $(this).closest('tr').data('parent-index') === index;
    };

    if (!checked) {
      rows = this.getGroupSelections(index);
    }

    this.$selectItem.filter(filter).prop('checked', checked);

    this.updateRows();
    this.updateSelected();
    if (checked) {
      rows = this.getGroupSelections(index);
    }
    this.trigger(checked ? 'check-all' : 'uncheck-all', rows);
  };

  BootstrapTable.prototype.getGroupByFields = function () {
    var groupByFields = this.options.groupByField;
    if (!$.isArray(this.options.groupByField)) {
      groupByFields = [this.options.groupByField];
    }

    return groupByFields;
  };

  BootstrapTable.prototype.scrollTo = function (params) {
    if (this.options.groupBy) {
      var options = { unit: 'px', value: 0 };
      if (typeof params === 'object') {
        options = Object.assign(options, params);
      }

      if (options.unit === 'rows') {
        var scrollTo = 0;
        this.$body.find('> tr:lt("' + options.value + '")').each(function (i, el) {
          scrollTo += $(el).outerHeight(true);
        });

        var $targetColumn = this.$body.find('> tr:not(.groupBy):eq(' + options.value + ')');
        $targetColumn.prevAll('.groupBy').each(function (i, el) {
          scrollTo += $(el).outerHeight(true);
        });

        this.$tableBody.scrollTop(scrollTo);
        return;
      }
    }
    _updateSelected.apply(this, Array.prototype.slice.apply(arguments));
  };
});

(function (factory) {
  'use strict';
  if (typeof define === 'function' && define.amd) {
    // AMD. Register as an anonymous module.
    define(['jquery', 'bootbox'], factory);
  } else {
    // Browser globals
    factory(window.jQuery, window.bootbox);
  }
})(function ($, bootbox) {
  'use strict';
  if (!$.fn.bootstrapTable) {
    return;
  }
  var BootstrapTable = $.fn.bootstrapTable.Constructor;
  var _initBody = BootstrapTable.prototype.initBody;
  BootstrapTable.prototype.initBody = function () {
    var that = this;
    $.each(that.columns, function (i, column) {
      if (!column.editable) {
        return;
      }
      // 设置编辑弹出框标题
      column.editable.title = column.editable.title || column.title;
    });
    _initBody.apply(that, Array.prototype.slice.apply(arguments));
  };

  var Popover = function (element, options) {
    var self = this;
    self.init('popmodal', element, options);
  };
  Popover.DEFAULTS = $.extend({}, $.fn.popover.Constructor.DEFAULTS, {
    trigger: 'manual',
    template:
      "<div class='bootbox modal ui-front' tabindex='-1' role='dialog' aria-hidden='true' data-keyboard='false' data-backdrop='false'><div class='modal-dialog'><div class='modal-content'><div class='modal-header'><h4 class='modal-title'></h4></div><div class='modal-body'></div><div class='modal-footer'></div></div></div></div>"
  });
  Popover.prototype = $.extend({}, $.fn.popover.Constructor.prototype, {
    constructor: Popover,
    getDefaults: function () {
      return Popover.DEFAULTS;
    },
    setContent: function () {
      var that = this;
      var $tip = that.tip();
      var title = that.getTitle();
      var content = that.getContent();

      // bootstrap-editable start
      var editable = that.options.editable || {};
      var editableOptions = editable.options || {};
      var editableContainer = editable.container || {};
      if (editableOptions.contentWidth > 0) {
        $tip.find('.modal-body').width(editableOptions.contentWidth);
      }
      if (editableOptions.contentHeight > 0) {
        $tip.find('.modal-body').height(editableOptions.contentHeight);
      }
      // title = title || editableOptions.title;
      // bootstrap-editable end

      $tip.find('.modal-title')[that.options.html ? 'html' : 'text'](title);
      $tip
        .find('.modal-body')
        .children()
        .detach()
        .end() // we use append
        [
          // for html
          // objects to
          // maintain js
          // events
          this.options.html ? (typeof content == 'string' ? 'html' : 'append') : 'text'
        ](content);

      $tip.removeClass('fade top bottom left right in');

      // IE8 doesn't accept hiding via the `:empty` pseudo selector, we
      // have to do
      // this manually by checking the contents.
      if (!$tip.find('.modal-title').html()) $tip.find('.popover-title').hide();
    },
    show: function () {
      var that = this;
      var e = $.Event('show.bs.' + that.type);
      if (that.hasContent() && that.enabled) {
        that.$element.trigger(e);

        var inDom = $.contains(that.$element[0].ownerDocument.documentElement, that.$element[0]);
        if (e.isDefaultPrevented() || !inDom) return;

        var $tip = that.tip();

        var tipId = that.getUID(that.type);
        that.setContent();
        $tip.attr('id', tipId);
        that.$element.attr('aria-describedby', tipId);

        if (that.options.animation) $tip.addClass('fade');

        $tip.detach().data('bs.' + that.type, that);

        that.options.container ? $tip.appendTo(that.options.container) : $tip.insertAfter(that.$element);
        that.$element.trigger('inserted.bs.' + that.type);

        $tip.modal('show');

        var complete = function () {
          var prevHoverState = that.hoverState;
          that.$element.trigger('shown.bs.' + that.type);
          that.hoverState = null;

          if (prevHoverState == 'out') that.leave(that);
        };

        $.support.transition && this.$tip.hasClass('fade') ? $tip.one('bsTransitionEnd', complete).emulateTransitionEnd(150) : complete();
      }
    },
    hide: function (callback) {
      var that = this;
      var $tip = $(that.$tip);
      var e = $.Event('hide.bs.' + that.type);

      function complete() {
        if (that.hoverState != 'in') $tip.detach();
        if (that.$element) {
          // TODO: Check whether guarding this code
          // with this `if` is really necessary.
          that.$element.removeAttr('aria-describedby').trigger('hidden.bs.' + that.type);
        }
        callback && callback();
      }

      that.$element.trigger(e);

      if (e.isDefaultPrevented()) return;

      $tip.removeClass('in');
      $tip.modal('hide').detach();
      $.support.transition && $tip.hasClass('fade') ? $tip.one('bsTransitionEnd', complete).emulateTransitionEnd(150) : complete();

      that.hoverState = null;

      return that;
    }
  });

  $.fn.popmodal = function (option) {
    return this.each(function () {
      var $this = $(this),
        data = $this.data('popmodal'),
        options = typeof option == 'object' && option;
      if (!data) $this.data('popmodal', (data = new Popover(this, options)));
      if (typeof option == 'string') data[option]();
    });
  };
  $.fn.popmodal.Constructor = Popover;

  // 拓展Container
  var Modal = function (element, options) {
    this.init(element, options);
  };
  $.fn.editableContainer.Modal = Modal;
  // extend methods
  $.extend($.fn.editableContainer.Modal.prototype, $.fn.editableContainer.Popup.prototype, {
    containerName: 'popmodal',
    containerDataName: 'bs.popmodal',
    innerCss: '.modal-content>.modal-body',
    defaults: $.fn.popmodal.Constructor.DEFAULTS,
    setPosition: function () {}
  });

  var oldEditableContainer = $.fn.editableContainer;
  $.fn.editableContainer = function (option) {
    var args = arguments;
    return this.each(function () {
      var Constructor,
        $this = $(this),
        dataKey = 'editableContainer',
        data = $this.data(dataKey),
        options = typeof option === 'object' && option;
      if (options.mode === 'inline') {
        Constructor = $.fn.editableContainer.Inline;
      } else if (options.mode === 'modal') {
        options.showbuttons = 'bottom'; // 弹出框只支持按钮在底部
        Constructor = $.fn.editableContainer.Modal;
      } else {
        Constructor = $.fn.editableContainer.Popup;
      }
      if (!data) {
        $this.data(dataKey, (data = new Constructor(this, options)));
      }

      if (typeof option === 'string') {
        // call method
        data[option].apply(data, Array.prototype.slice.call(args, 1));
      }
    });
  };
  $.extend($.fn.editableContainer, oldEditableContainer);

  $.fn.editableContainer.noConflict = function () {
    $.fn.editableContainer = oldEditableContainer;
    return this;
  };
});
/**
 * 自行扩展bootstrap table行编辑样式
 *
 * @param $
 */

(function ($) {
  'use strict';
  var Constructor = function (options) {
    this.init('wCommonComboTree', options, Constructor.defaults);

    options.wCommonComboTree = options.wCommonComboTree || {};
    this.options.wCommonComboTree = $.extend({}, Constructor.defaults.wCommonComboTree, options.wCommonComboTree);
  };

  $.fn.editableutils.inherit(Constructor, $.fn.editabletypes.abstractinput);

  $.extend(Constructor.prototype, {
    /*
     * this method called before render to init $tpl that is inserted in DOM
     */
    prerender: function () {
      console.log('prerender');
      this.$tpl = $(this.options.tpl); // whole tpl as jquery object
      this.$input = this.$tpl; // control itself, can be changed in
      // render method
      this.$clear = null; // clear button
      this.error = null; // error message, if input cannot be rendered
    },

    /**
     * Renders input from tpl. Can return jQuery deferred object. Can be
     * overwritten in child objects 控件初始化的时候调用。
     *
     * @method render()
     */
    render: function () {
      var self = this;
      console.log('render');
      if ($.fn.editableform.engine === 'bs3') {
        var emptyInputClass = this.options.inputclass === null || this.options.inputclass === false;
        var defaultClass = 'input-sm';
        self.$input.addClass('form-control');
        if (emptyInputClass) {
          self.options.inputclass = defaultClass;
          self.$input.addClass(defaultClass);
        }
      }
      if (this.options.inputclass) {
        this.$input.addClass(this.options.inputclass);
      }
      // 树形组件弹出框宽度100% start
      $(self.$input).closest('div.form-group').css({
        display: 'block',
        'margin-left': 'auto',
        'margin-right': 'auto'
      });
      $(self.$input).closest('div.editable-input').css('display', 'block');
      // 树形组件弹出框宽度100% end
    },

    /**
     * Sets element's html by value. 初始化的时候把value转换为展示值
     *
     * @method value2html(value, element)
     * @param {mixed}
     *            value
     * @param {DOMElement}
     *            element
     */
    value2html: function (value, element) {
      $(element)[this.options.escape ? 'text' : 'html']($.trim(this.value2display(value, 'name')));
      $(element).attr('title', $.trim(this.value2display(value, 'name')));
    },

    /**
     * Converts element's html to value
     *
     * @method html2value(html)
     * @param {string}
     *            html
     * @returns {mixed}
     */
    html2value: function (html) {
      console.log('html2value');
      return $('<div>').html(html).text();
    },

    /**
     * Converts value to string (for internal compare). For submitting to
     * server used value2submit(). 对比值有没有变更，有变更才可能返回。
     *
     * @method value2str(value)
     * @param {mixed}
     *            value
     * @returns {string}
     */
    value2str: function (value) {
      var str = '';
      if (value) {
        for (var k in value) {
          str = str + k + ':' + value[k] + ';';
        }
      }
      return str;
    },

    /**
     * Converts string received from server into value. Usually from
     * `data-value` attribute. 初始化的时候，将a标签上的data-value转换为值
     *
     * @method str2value(str)
     * @param {string}
     *            str
     * @returns {mixed}
     */
    str2value: function (str) {
      if (typeof str === 'object') {
        return str;
      }
      if (str === 'undefined') {
        return {};
      } else if (str == null || typeof str === 'undefined' || str == 'null' || str == 'undefined') {
        return {};
      }
      // if (str) {
      try {
        // escape的JSON字符解码
        // if("{}" != str && str.length > 3 && str.substring(0, 3) ==
        // "%7B") {
        // str = unescape(str);
        // }
        return $.parseJSON(str);
      } catch (e) {
        console.error('字符串值转json异常：' + e);
      }
      // }
      return {};
    },

    /**
     * Converts value for submitting to server. Result can be string or
     * object.
     *
     * @method value2submit(value)
     * @param {mixed}
     *            value
     * @returns {mixed}
     */
    value2submit: function (value) {
      console.log('value2submit');
      return value;
    },
    value2display: function (value, key) {
      if (!key) {
        key = 'id';
      }
      var text = [];
      if ($.isArray(value)) {
        $.each(value, function (index, val) {
          text.push(val[key]);
        });
      } else {
        text.push(value[key]);
      }
      return text.join(this.getSeparator());
    },
    /**
     * Sets value of input. 点击进入编辑状态的时候，通过值初始化编辑状态下的控件值。
     *
     * @method value2input(value)
     * @param {mixed}
     *            value
     */
    value2input: function (value) {
      var self = this;
      console.log('value2input');
      if (value) {
        self.$input.val(self.value2display(value, 'id'));
      }
      if (!self.$input.attr('id')) {
        self.$input.attr('id', new Date().getTime());
        self.$input.wCommonComboTree(self.options.wCommonComboTree);
      }
      // this.$input.val(value);
    },

    /**
     * Returns value of input. Value can be object (e.g. datepicker)
     * 控件失去焦点时，将控件的值，转换为编辑行的值。
     *
     * @method input2value()
     */
    input2value: function () {
      var options = this.options;
      var valueNodes = this.$input.wCommonComboTree('getValueNodes');
      var result = [];
      $.each(valueNodes, function (index, node) {
        var value = {};
        value.id = node.id;
        value.name = node.name;
        if (options.otherProperties) {
          if ($.isArray(options.otherProperties)) {
            $.each(options.otherProperties, function (index, property) {
              if (node[property]) {
                value[property] = node[property];
              }
            });
          } else if ($.isPlainObject(options.otherProperties)) {
            $.each(options.otherProperties, function (key, property) {
              var data = node;
              $.each(property.split('.'), function (index, path) {
                data = data[path];
              });
              if (data) {
                value[key] = data;
              }
            });
          }
        }

        result.push(value);
      });
      if (!options.wCommonComboTree.multiSelect) {
        if (result.length == 1) {
          return result[0];
        }
        return {};
      }
      console.log('input2value');
      return result;
    },

    /**
     * Activates input. For text it sets focus.
     *
     * @method activate()
     */
    activate: function () {
      if (this.$input.is(':visible')) {
        this.$input.focus();
        this.$input.wCommonComboTree('showMenu');
      }
    },

    /**
     * Creates input.
     *
     * @method clear()
     */
    clear: function () {
      console.log('clear');
      this.$input.val(null);
    },

    /**
     * method to escape html.
     */
    escape: function (str) {
      console.log('escape');
      return $('<div>').text(str).html();
    },

    /**
     * attach handler to automatically submit form when value changed
     * (useful when buttons not shown)
     */
    autosubmit: function () {
      this.$input.keydown(function (e) {
        if (e.which === 13) {
          $(this).closest('form').submit();
        }
      });
    },

    /**
     * Additional actions when destroying element
     */
    destroy: function () {
      this.$input.wCommonComboTree('destroy');
      console.log('destroy');
    },

    // -------- helper functions --------
    setClass: function () {
      if (this.options.inputclass) {
        this.$input.addClass(this.options.inputclass);
      }
    },

    setAttr: function (attr) {
      if (this.options[attr] !== undefined && this.options[attr] !== null) {
        this.$input.attr(attr, this.options[attr]);
      }
    },

    getSeparator: function () {
      return ',';
    }
  });

  Constructor.defaults = $.extend({}, $.fn.editabletypes.abstractinput.defaults, {
    /**
     * @property tpl
     * @default <input type="hidden">
     */
    tpl: '<input type="text" style="width:100%;">',
    /**
     * wCommonComboTree 参数
     *
     * @property wCommonComboTree
     * @type object
     * @default null
     */
    wCommonComboTree: null,
    /**
     * Source data for select. It will be assigned to select2 `data`
     * property and kept here just for convenience. Please note, that format
     * is different from simple `select` input: use 'id' instead of 'value'.
     * E.g. `[{id: 1, text: "text1"}, {id: 2, text: "text2"}, ...]`.
     *
     * @property source
     * @type array|string|function
     * @default null
     */
    source: null,
    /**
     * Separator used to display tags.
     *
     * @property viewseparator
     * @type string
     * @default ', '
     */
    viewseparator: ',',
    /**
     * 除了ID和NAME外，其他要返回的数据在中的属性名。属性取值对象由otherPropertyPath指定路径。
     * 可传json格式或这字符串。json格式，value待办node中的值路径（属性的属性用“.”分割），key为返回值中的key
     */
    otherProperties: null
  });

  $.fn.editabletypes.wCommonComboTree = Constructor;
})(window.jQuery);

(function ($) {
  'use strict';
  var Constructor = function (options) {
    this.init('wCustomForm', options, Constructor.defaults);
  };

  $.fn.editableutils.inherit(Constructor, $.fn.editabletypes.abstractinput);

  $.extend(Constructor.prototype, {
    /*
     * this method called before render to init $tpl that is inserted in DOM
     */
    prerender: function () {
      console.log('prerender');
      this.$tpl = $(this.options.tpl); // whole tpl as jquery object
      this.$input = this.$tpl; // control itself, can be changed in
      // render method
      this.$clear = null; // clear button
      this.error = null; // error message, if input cannot be rendered
    },

    /**
     * Renders input from tpl. Can return jQuery deferred object. Can be
     * overwritten in child objects 控件初始化的时候调用。
     *
     * @method render()
     */
    render: function () {
      console.log('render');
    },

    /**
     * Sets element's html by value. 初始化的时候把value转换为展示值
     *
     * @method value2html(value, element)
     * @param {mixed}
     *            value
     * @param {DOMElement}
     *            element
     */
    value2html: function (value, element) {
      if (this.options.value2html) {
        this.options.value2html.call(this, value, element);
      } else {
        $(element)[this.options.escape ? 'text' : 'html']($.trim(this.value2display(value)));
        $(element).attr('title', $.trim(this.value2display(value)));
      }
    },

    /**
     * Converts element's html to value
     *
     * @method html2value(html)
     * @param {string}
     *            html
     * @returns {mixed}
     */
    html2value: function (html) {
      console.log('html2value');
      if (this.options.html2value) {
        return this.options.html2value.call(this, html);
      }
      return $('<div>').html(html).text();
    },

    /**
     * Converts value to string (for internal compare). For submitting to
     * server used value2submit(). 对比值有没有变更，有变更才可能返回。
     *
     * @method value2str(value)
     * @param {mixed}
     *            value
     * @returns {string}
     */
    value2str: function (value) {
      if (this.options.value2str) {
        return this.options.value2str.call(this, value);
      }
      if (typeof value == 'object') {
        try {
          return JSON.stringify(value); // .replace(/"/g, "'");
        } catch (e) {
          console.log(e);
        }
      }
      var str = '';
      if (value) {
        for (var k in value) {
          str = str + k + ':' + value[k] + ';';
        }
      }
      return str;
    },

    /**
     * Converts string received from server into value. Usually from
     * `data-value` attribute. 初始化的时候，将a标签上的data-value转换为值
     *
     * @method str2value(str)
     * @param {string}
     *            str
     * @returns {mixed}
     */
    str2value: function (str) {
      if (this.options.str2value) {
        return this.options.str2value.call(this, str);
      }
      console.log('str2value:str=' + str);
      if (typeof str === 'object') {
        return str;
      } else if (str == null || typeof str === 'undefined' || str == 'null' || str == 'undefined') {
        return {};
      }
      try {
        // escape的JSON字符解码
        // if("{}" != str && str.length > 3 && str.substring(0, 3) ==
        // "%7B") {
        // str = unescape(str);
        // }
        return typeof str === 'string' && str.indexOf('{') > -1 ? $.parseJSON(str) : str;
      } catch (e) {
        console.error('字符串值转json异常：' + e);
      }
      return {};
    },

    /**
     * Converts value for submitting to server. Result can be string or
     * object.
     *
     * @method value2submit(value)
     * @param {mixed}
     *            value
     * @returns {mixed}
     */
    value2submit: function (value) {
      if (this.options.value2submit) {
        return this.options.value2submit.call(this, value);
      }
      console.log('value2submit');
      return value;
    },
    value2display: function (value) {
      if (this.options.value2display) {
        return this.options.value2display.call(this, value);
      }
      if ($.isPlainObject(value)) {
        var text = [];
        $.each(value, function (key, val) {
          text.push(val);
        });
        return text.join(this.getSeparator());
      }
      return value;
    },
    /**
     * Sets value of input. 点击进入编辑状态的时候，通过值初始化编辑状态下的控件值。
     *
     * @method value2input(value)
     * @param {mixed}
     *            value
     */
    value2input: function (value) {
      console.log('value2input');
      if (this.options.value2input) {
        this.options.value2input.call(this, value);
      } else {
        if (value) {
          this.$input.find('.' + this.options.collectClass).each(function () {
            var tagName = this.tagName;
            var key = $(this).attr('name');
            if (key) {
              if (tagName == 'INPUT') {
                if ($.inArray($(this).attr('type'), ['text', 'hidden']) != -1) {
                  $(this).val($.trim(value[key]));
                }
                if ($.inArray($(this).attr('type'), ['radio', 'checkbox']) != -1) {
                  if ($(this).val() == value[key]) {
                    $(this).attr('checked', 'checked');
                  }
                }
              } else if (tagName == 'SELECT') {
                $(this).val($.trim(value[key]));
              }
            }
          });
        }
      }
      if (this.options.inputCompleted) {
        this.options.inputCompleted.call(this, value);
      }
      // this.$input.val(value);
    },

    /**
     * Returns value of input. Value can be object (e.g. datepicker)
     * 控件失去焦点时，将控件的值，转换为编辑行的值。
     *
     * @method input2value()
     */
    input2value: function () {
      var value = {};
      this.$input.find('.' + this.options.collectClass).each(function () {
        var tagName = this.tagName;
        var key = $(this).attr('name');
        if (key) {
          if (tagName == 'INPUT') {
            if ($.inArray($(this).attr('type'), ['text', 'hidden']) != -1) {
              value[key] = $(this).val();
            }
            if ($.inArray($(this).attr('type'), ['radio', 'checkbox']) != -1) {
              if ($(this).is(':checked')) {
                value[key] = $(this).val();
              }
            }
          } else if (tagName == 'SELECT') {
            value[key] = $(this).val();
          } else if (tagName == 'TEXTAREA') {
            value[key] = $(this).val();
          }
        }
      });
      if (this.options.input2value) {
        return this.options.input2value.call(this, value);
      }
      return value;
    },

    /**
     * Activates input. For text it sets focus.
     *
     * @method activate()
     */
    activate: function () {
      if (this.options.activate) {
        this.options.activate.call(this);
      }
    },

    /**
     * Creates input.
     *
     * @method clear()
     */
    clear: function () {
      console.log('clear');
      this.$input.find('.' + this.options.collectClass).each(function () {
        var tagName = this.tagName;
        if (tagName == 'INPUT') {
          if ($(this).attr('type') == 'text' || $(this).attr('type') == 'hidden') {
            $(this).val(null);
          }
        } else if (tagName == 'SELECT') {
          $(this).val(null);
        }
      });
    },

    /**
     * method to escape html.
     */
    escape: function (str) {
      console.log('escape');
      return $('<div>').text(str).html();
    },

    /**
     * attach handler to automatically submit form when value changed
     * (useful when buttons not shown)
     */
    autosubmit: function () {
      this.$input.keydown(function (e) {
        if (e.which === 13) {
          $(this).closest('form').submit();
        }
      });
    },

    /**
     * Additional actions when destroying element
     */
    destroy: function () {
      console.log('destroy');
      this.$input.empty();
    },

    // -------- helper functions --------
    setClass: function () {
      if (this.options.inputclass) {
        this.$input.addClass(this.options.inputclass);
      }
    },

    setAttr: function (attr) {
      if (this.options[attr] !== undefined && this.options[attr] !== null) {
        this.$input.attr(attr, this.options[attr]);
      }
    },

    getSeparator: function () {
      return ',';
    }
  });

  Constructor.defaults = $.extend({}, $.fn.editabletypes.abstractinput.defaults, {
    /**
     * @property tpl
     * @default <input type="hidden">
     */
    tpl: '<div></div>',
    /**
     * Source data for select. It will be assigned to select2 `data`
     * property and kept here just for convenience. Please note, that format
     * is different from simple `select` input: use 'id' instead of 'value'.
     * E.g. `[{id: 1, text: "text1"}, {id: 2, text: "text2"}, ...]`.
     *
     * @property source
     * @type array|string|function
     * @default null
     */
    source: null,
    /**
     * Separator used to display tags.
     *
     * @property viewseparator
     * @type string
     * @default ', '
     */
    viewseparator: ',',
    /**
     * 收集的表单的class
     */
    collectClass: 'w-custom-collect',
    /**
     * 图标选择的类型
     */
    iconSelectTypes: null,
    /**
     * 图标是否可编辑
     */
    iconEditable: null,
    /**
     * 提供渲染时候的参数
     */
    renderParams: {},
    savenochange: true,
    value2html: null,
    html2value: null,
    value2str: null,
    str2value: null,
    value2submit: null,
    value2display: null,
    value2input: null,
    input2value: null,
    inputCompleted: null,
    activate: null
  });

  $.fn.editabletypes.wCustomForm = Constructor;
})(window.jQuery);

/**
 * dyform control adapter
 *
 */
(function ($) {
  'use strict';

  // 普通字符转换成转意符
  var arrEntities1 = {
    '<': '&lt;',
    '>': '&gt;',
    '&': '&amp;',
    '"': '&quot;',
    "'": '&apos;'
  };
  function html2Escape(sHtml) {
    if (typeof sHtml === 'string') {
      return sHtml.replace(/[<>&"']/g, function (c) {
        return arrEntities1[c];
      });
    }
    return sHtml;
  }

  /**
   * 与从表保持一致(TODO 提取)
   *
   */
  function getCellInfoKey(rowId, fieldName) {
    return rowId + '___' + fieldName;
  }

  var Constructor = function (options) {
    var self = this;
    self.$scope = $(options.scope);
    self.init('control', options, Constructor.defaults);
    options.control = options.control || {};
    self.options.control = $.extend({}, Constructor.defaults.control, options.control);
    self.initControl(self.$scope);
  };

  $.fn.editableutils.inherit(Constructor, $.fn.editabletypes.abstractinput);
  var $tplSpan = $('<span class="value"></span>');
  var $tplInput = $('<div style="" class="control-container"></div>');
  $.extend(Constructor.prototype, {
    initControl: function ($scope) {},
    createControl: function (force) {
      var self = this;
      if (force === true || !self.controlObj) {
        var $tpl = self.$tpl;
        var $scope = self.$scope;
        var options = self.options;
        var controlDef = options.control;
        $.extend(controlDef.fieldDefinition, {
          pos: dyControlPos.subForm,
          dataUuid: $scope.attr('data-pk')
        });
        controlDef['$placeHolder'] = $tpl;
        self.controlObj = $.ControlManager.createCtl(controlDef);
        self.controlObj.initControlOver && self.controlObj.initControlOver();
        self.controlObj['___setValue'] = self.controlObj.setValue;
        self.controlObj.setValue = function (value) {
          var controlObj = self.controlObj;
          if (controlObj.bSettingValue === true) {
            return;
          }
          try {
            controlObj.bSettingValue = true;
            controlObj['___setValue'].apply(this, arguments);
            self.control2html(controlObj, $scope[0]);
            var submitValue = self.input2value();
            return $scope.triggerHandler('save', {
              newValue: submitValue,
              submitValue: submitValue
            });
          } finally {
            delete controlObj.bSettingValue;
          }
        };
        $tpl.on('filechange valuechange', function (event) {
          var controlObj = self.controlObj;
          if (controlObj.bSettingValue === true) {
            return;
          }
          try {
            controlObj.bSettingValue = true;
            self.control2html(controlObj, $scope[0]);
            var submitValue = self.input2value();
            return $scope.triggerHandler('save', {
              newValue: submitValue,
              submitValue: submitValue
            });
          } finally {
            delete controlObj.bSettingValue;
          }
        });
        // 放入表单控件缓存
        var ctlName = $tpl.attr('name');
        var $dyform = controlDef.$currentForm;
        $dyform.cache.put.call($dyform, cacheType.control, $.ControlManager[ctlName]);
      }
      return self.controlObj;
    },
    getControl: function () {
      var self = this;
      return self.prerender();
    },
    prerender: function (force) {
      var self = this;
      var controlObj = self.controlObj;
      if (force === true || !self.$tpl) {
        var $scope = self.$scope;
        var options = self.options;
        // whole tpl as jquery object
        $scope.removeClass('editable-click');
        var dataPk = $scope.attr('data-pk');
        var dataName = $scope.attr('data-name');
        var ctlName = getCellInfoKey(dataPk, dataName);
        var $tpl = (self.$tpl = $tplSpan.clone().attr({
          rowId: dataPk,
          name: ctlName,
          pos: dyControlPos.subForm
        }));
        // control itself, can be changed inrender method
        $scope.after((self.$input = $tplInput.clone().append($tpl).hide()));
        // 隐藏之前，先保存$input的内部数据
        $scope.off('hide.before').on('hide.before', function (event) {
          if (controlObj.isAttachCtl && controlObj.isAttachCtl()) {
            return self.$input.append($tpl);
          }
          self.$input.hide();
          $scope.after(self.$input.append($tpl));
        });
        controlObj = self.createControl(force);
        if (self.isControlable()) {
          setTimeout(function (t) {
            // 展示为控件
            (self.triggerClick = true) && $scope.trigger('cell-click');
            $scope.removeClass('editable-open').blur();
            delete self.triggerClick;
            $scope.triggerHandler('show');
          }, 0);
        }
      }
      return controlObj;
    },
    render: function () {
      var self = this;
      var $scope = self.$scope;
      var options = self.options;
      var inputMode = self.options.control.fieldDefinition.inputMode;
      if (formDefinitionMethod.isAttach(inputMode)) {
        // 附件渲染在self.$tpl内部
        return;
      }
      self.$tpl.after(self.$input);
      self.$input.prepend(self.$tpl).show();
      // form.editableform 等显示
      if (true === self.triggerClick) {
        return;
      }
      var tid = setTimeout(function (t) {
        self.$input.find('.editableClass:visible').trigger('click').focus();
        tid = setTimeout(function (t) {
          var exclude_selectors = ['.ui-dialog ', '.modal', '.cascader-content', '.autocomplete'];
          $(exclude_selectors.join(',')).addClass('editable-container');
        }, 500);
      }, 0);
    },
    postrender: function () {
      var self = this;
      var $scope = self.$scope;
      var controlObj = self.getControl();
      var inputMode = self.options.control.fieldDefinition.inputMode;
      if (inputMode === dyFormInputMode.ckedit) {
        // var editor = controlObj.getCkeditor();
        // iframe无法append内容，重新初始化iframe
        controlObj.createInstance(true);
      }
    },
    control2html: function (controlObj, element) {
      var self = this,
        displayValue;
      var $element = $(element);
      var options = self.options;
      if (controlObj.isAttachCtl && controlObj.isAttachCtl()) {
        // self.$input.hide();
        $element.closest('td').attr('title', '');
        return $element.append(self.$input.show());
      }
      if (controlObj.isValueMap && controlObj.isValueMap()) {
        displayValue = controlObj.getDisplayValue();
      } else {
        displayValue = controlObj.getValue();
      }
      var oDisplayValue = displayValue;
      if ($.isFunction(options.formatter)) {
        var data = self.options.subform.getData(),
          index = $element.parents('tr[data-index]').data('index'),
          row = data[index];
        displayValue = options.formatter.apply(self, [oDisplayValue, row, index]);
      } else {
        // 展示为控件只读时可以对html转义
        // displayValue = html2Escape(oDisplayValue);
        var inputMode = controlObj.getInputMode();
        if (inputMode === dyFormInputMode.number) {
          displayValue = controlObj.getDisplayValue();
        } else {
          displayValue = '<pre>' + displayValue + '</pre>';
          // if (typeof displayValue == 'string' && displayValue.indexOf('\n') != -1) {
          //   displayValue = displayValue.replaceAll('\n', '<br>');
          // }
          // if (typeof displayValue == 'string' && displayValue.indexOf(' ') != -1) {
          //   displayValue = displayValue.replaceAll(' ', '&nbsp;');
          // }
        }
      }
      $element.attr('title', oDisplayValue).html(displayValue);
      $element.closest('td').attr('title', '');
      $element.next().attr('title', oDisplayValue);
      $element.next().find('div.control-container').attr('title', oDisplayValue);
    },
    value2html: function (value, element) {
      // console.log("value2html");
      var self = this;
      var options = self.options;
      var controlObj = self.getControl();
      if (controlObj.isAttachCtl && controlObj.isAttachCtl() && $.isArray(value) === false) {
        value = [];
      }
      controlObj.setValue(value);
      self.control2html(controlObj, element);
    },

    html2value: function (html) {
      // console.log("html2value");
      return $('<div>').html(html).text();
    },

    value2input: function (value) {
      var self = this;
      if (self.triggerClick && null == value) {
        return;
      }
      // console.log("value2input");
      var controlObj = self.getControl();
      if (controlObj && controlObj.setValue) {
        controlObj.setValue(value);
        if (controlObj.isAttachCtl && controlObj.isAttachCtl()) {
          // self.$tpl.show();
        } else {
          // 设置后，触发自动提交
        }
      }
    },

    input2value: function () {
      // console.log("input2value");
      var value;
      var self = this;
      var controlObj = self.getControl();
      if (controlObj.isAttachCtl && controlObj.isAttachCtl()) {
        controlObj.getValue(function (files) {
          value = files || [];
        }, $.noop);
      } else {
        value = self.controlObj.getValue();
      }
      return value;
    },

    str2value: function (str, separator) {
      // console.log("str2value");
      return str;
    },
    autosubmit: function () {
      var self = this;
    },
    isControlable: function () {
      var self = this;
      var control = self.options.control;
      var inputMode = control.fieldDefinition.inputMode;
      if (formDefinitionMethod.isAttach(inputMode)) {
        return true;
      }
      return control.controlable;
    },
    isDisabled: function (event) {
      var self = this;
      var subformConfig = self.options.control.subformConfig;
      if (subformConfig.editMode === '2') {
        // 弹出框编辑
        return true;
      }
      return self.isReadOnly();
    },
    isReadOnly: function () {
      var self = this;
      var controlObj = self.getControl();
      if (controlObj.isReadOnly && controlObj.isReadOnly()) {
        return true;
      }
      if (controlObj.isShowAsLabel() || !controlObj.isEditable()) {
        return true;
      }
      return false;
    },
    destroy: function () {
      var self = this;
      self.$scope = self.$tpl = self.$input = null;
      if (self.controlObj && $.isFunction(self.controlObj.destory)) {
        self.controlObj.destory();
        self.controlObj = null;
      }
    }
  });

  Constructor.defaults = $.extend({}, $.fn.editabletypes.abstractinput.defaults, {
    control: null,
    subform: null,
    formatter: null,
    /**
     * @property tpl
     * @default <input type="hidden">
     */
    tpl: '<span class="value"></span>',
    /**
     * Placeholder attribute of select
     *
     * @property placeholder
     * @type string
     * @default null
     */
    placeholder: null,
    /**
     * Source data for select. It will be assigned to control `data`
     * property and kept here just for convenience. Please note, that format
     * is different from simple `select` input: use 'id' instead of 'value'.
     * E.g. `[{id: 1, text: "text1"}, {id: 2, text: "text2"}, ...]`.
     *
     * @property source
     * @type array|string|function
     * @default null
     */
    source: null,
    /**
     * Separator used to display tags.
     *
     * @property viewseparator
     * @type string
     * @default ', '
     */
    viewseparator: ', ',
    /**
     *
     */
    onblur: 'cancel',
    editable: false
  });

  $.fn.editabletypes.control = Constructor;
})(window.jQuery);
