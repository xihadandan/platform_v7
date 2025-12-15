/**
 * @author zhixin wen <wenzhixin2010@gmail.com>
 * version: 1.11.0
 * https://github.com/wenzhixin/bootstrap-table/
 */

(function ($) {
  'use strict';

  // TOOLS DEFINITION
  // ======================

  var cachedWidth = null;

  // it only does '%s', and return '' when arguments are undefined
  var sprintf = function (str) {
    var args = arguments,
      flag = true,
      i = 1;

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

  var getPropertyFromOther = function (list, from, to, value) {
    var result = '';
    $.each(list, function (i, item) {
      if (item[from] === value) {
        result = item[to];
        return false;
      }
      return true;
    });
    return result;
  };

  var getFieldIndex = function (columns, field) {
    var index = -1;

    $.each(columns, function (i, column) {
      if (column.field === field) {
        index = i;
        return false;
      }
      return true;
    });
    return index;
  };

  // http://jsfiddle.net/wenyi/47nz7ez9/3/
  var setFieldIndex = function (columns, hideInvisible) {
    var i,
      j,
      k,
      totalCol = 0,
      flag = [];

    for (i = 0; i < columns[0].length; i++) {
      totalCol += columns[0][i].colspan || 1;
    }
    if (hideInvisible && columns.length > 1) {
      var lastColumns = columns[columns.length - 1];
      for (i = 0; i < lastColumns.length; i++) {
        if (lastColumns[i].visible) {
          continue;
        }
        totalCol += lastColumns[i].colspan || 1;
      }
    }

    for (i = 0; i < columns.length; i++) {
      flag[i] = [];
      for (j = 0; j < totalCol; j++) {
        flag[i][j] = false;
      }
    }

    for (i = 0; i < columns.length; i++) {
      for (j = 0; j < columns[i].length; j++) {
        var r = columns[i][j],
          rowspan = r.rowspan || 1,
          colspan = r.colspan || 1,
          index = $.inArray(false, flag[i]);

        if (colspan === 1) {
          r.fieldIndex = index;
          // when field is undefined, use index instead
          if (typeof r.field === 'undefined') {
            r.field = index;
          }
        }

        for (k = 0; k < rowspan; k++) {
          flag[i + k][index] = true;
        }
        for (k = 0; k < colspan; k++) {
          flag[i][index + k] = true;
        }
      }
    }
  };

  var getScrollBarWidth = function () {
    if (cachedWidth === null) {
      var inner = $('<p/>').addClass('fixed-table-scroll-inner').css({
          display: 'block'
        }),
        outer = $('<div/>').addClass('fixed-table-scroll-outer'),
        w1,
        w2;

      outer.append(inner);
      $('body').append(outer);

      w1 = inner[0].offsetWidth;
      outer.css('overflow', 'scroll');
      w2 = inner[0].offsetWidth;

      if (w1 === w2) {
        w2 = outer[0].clientWidth;
      }

      outer.remove();
      cachedWidth = w1 - w2;
    }
    return cachedWidth;
  };

  var calculateObjectValue = function (self, name, args, defaultValue) {
    var func = name;

    if (typeof name === 'string') {
      // support obj.func1.func2
      var names = name.split('.');

      if (names.length > 1) {
        func = window;
        $.each(names, function (i, f) {
          func = func[f];
        });
      } else {
        func = window[name];
      }
    }
    if (typeof func === 'object') {
      return func;
    }
    if (typeof func === 'function') {
      return func.apply(self, args);
    }
    if (!func && typeof name === 'string' && sprintf.apply(this, [name].concat(args))) {
      return sprintf.apply(this, [name].concat(args));
    }
    return defaultValue;
  };

  var compareObjects = function (objectA, objectB, compareLength) {
    // Create arrays of property names
    var objectAProperties = Object.getOwnPropertyNames(objectA),
      objectBProperties = Object.getOwnPropertyNames(objectB),
      propName = '';

    if (compareLength) {
      // If number of properties is different, objects are not equivalent
      if (objectAProperties.length !== objectBProperties.length) {
        return false;
      }
    }

    for (var i = 0; i < objectAProperties.length; i++) {
      propName = objectAProperties[i];

      // If the property is not in the object B properties, continue with the next property
      if ($.inArray(propName, objectBProperties) > -1) {
        // If values of same property are not equal, objects are not equivalent
        if (objectA[propName] !== objectB[propName]) {
          return false;
        }
      }
    }

    // If we made it this far, objects are considered equivalent
    return true;
  };

  var escapeHTML = function (text) {
    if (typeof text === 'string') {
      return text
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;')
        .replace(/'/g, '&#039;')
        .replace(/`/g, '&#x60;');
    }
    return text;
  };

  var getRealHeight = function ($el) {
    var height = 0;
    $el.children().each(function () {
      if (height < $(this).outerHeight(true)) {
        height = $(this).outerHeight(true);
      }
    });
    return height;
  };

  var getRealDataAttr = function (dataAttr) {
    for (var attr in dataAttr) {
      var auxAttr = attr
        .split(/(?=[A-Z])/)
        .join('-')
        .toLowerCase();
      if (auxAttr !== attr) {
        dataAttr[auxAttr] = dataAttr[attr];
        delete dataAttr[attr];
      }
    }

    return dataAttr;
  };

  var getItemField = function (item, field, escape) {
    var value = item;
    // field 忽略大小写
    if (typeof field === 'string') {
      if (item.hasOwnProperty(field)) {
        return escape ? escapeHTML(item[field]) : item[field];
      } else if (item.hasOwnProperty(field.toLowerCase())) {
        return escape ? escapeHTML(item[field.toLowerCase()]) : item[field.toLowerCase()];
      } else if (item.hasOwnProperty(field.toUpperCase())) {
        return escape ? escapeHTML(item[field.toUpperCase()]) : item[field.toUpperCase()];
      }
    } else if (typeof field !== 'string') {
      return escape ? escapeHTML(item[field]) : item[field];
    }
    var props = field.split('.');
    for (var p in props) {
      value = value && value[props[p]];
    }
    return escape ? escapeHTML(value) : value;
  };

  var isIEBrowser = function () {
    return !!(navigator.userAgent.indexOf('MSIE ') > 0 || !!navigator.userAgent.match(/Trident.*rv\:11\./));
  };

  var objectKeys = function () {
    // From https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Object/keys
    if (!Object.keys) {
      Object.keys = (function () {
        var hasOwnProperty = Object.prototype.hasOwnProperty,
          hasDontEnumBug = !{
            toString: null
          }.propertyIsEnumerable('toString'),
          dontEnums = ['toString', 'toLocaleString', 'valueOf', 'hasOwnProperty', 'isPrototypeOf', 'propertyIsEnumerable', 'constructor'],
          dontEnumsLength = dontEnums.length;

        return function (obj) {
          if (typeof obj !== 'object' && (typeof obj !== 'function' || obj === null)) {
            throw new TypeError('Object.keys called on non-object');
          }

          var result = [],
            prop,
            i;

          for (prop in obj) {
            if (hasOwnProperty.call(obj, prop)) {
              result.push(prop);
            }
          }

          if (hasDontEnumBug) {
            for (i = 0; i < dontEnumsLength; i++) {
              if (hasOwnProperty.call(obj, dontEnums[i])) {
                result.push(dontEnums[i]);
              }
            }
          }
          return result;
        };
      })();
    }
  };

  // BOOTSTRAP TABLE CLASS DEFINITION
  // ======================

  var BootstrapTable = function (el, options) {
    this.options = options;
    this.$el = $(el);
    this.$el_ = this.$el.clone();
    this.timeoutId_ = 0;
    this.timeoutFooter_ = 0;

    this.init();
  };

  BootstrapTable.DEFAULTS = {
    classes: 'table table-hover',
    locale: undefined,
    height: undefined,
    undefinedText: '-',
    sortName: undefined,
    sortOrder: 'asc',
    sortStable: false,
    striped: false,
    columns: [
      []
    ],
    data: [],
    dataField: 'rows',
    method: 'get',
    url: undefined,
    ajax: undefined,
    cache: true,
    contentType: 'application/json',
    dataType: 'json',
    ajaxOptions: {},
    queryParams: function (params) {
      return params;
    },
    queryParamsType: 'limit', // undefined
    responseHandler: function (res) {
      return res;
    },
    pagination: false,
    onlyInfoPagination: false,
    sidePagination: 'client', // client or server
    totalRows: 0, // server side need to set
    pageNumber: 1,
    pageSize: 10,
    pageList: [10, 25, 50, 100],
    paginationHAlign: 'right', //right, left
    paginationVAlign: 'bottom', //bottom, top, both
    paginationDetailHAlign: 'left', //right, left
    paginationPreText: '&lsaquo;',
    paginationNextText: '&rsaquo;',
    search: false,
    searchOnEnterKey: false,
    strictSearch: false,
    searchAlign: 'right',
    selectItemName: 'btSelectItem',
    showHeader: true,
    showFooter: false,
    showColumns: false,
    showPaginationSwitch: false,
    showRefresh: false,
    showToggle: false,
    buttonsAlign: 'right',
    smartDisplay: true,
    escape: false,
    minimumCountColumns: 1,
    idField: undefined,
    uniqueId: undefined,
    cardView: false,
    detailView: false,
    detailFormatter: function (index, row) {
      return '';
    },
    trimOnSearch: true,
    clickToSelect: false,
    singleSelect: false,
    toolbar: undefined,
    toolbarAlign: 'left',
    checkboxHeader: true,
    sortable: true,
    silentSort: true,
    maintainSelected: false,
    searchTimeOut: 500,
    searchText: '',
    iconSize: undefined,
    buttonsClass: 'default',
    iconsPrefix: 'glyphicon', // glyphicon of fa (font awesome)
    icons: {
      paginationSwitchDown: 'glyphicon-collapse-down icon-chevron-down',
      paginationSwitchUp: 'glyphicon-collapse-up icon-chevron-up',
      refresh: 'glyphicon-refresh icon-refresh',
      toggle: 'glyphicon-list-alt icon-list-alt',
      columns: 'glyphicon-th icon-th',
      detailOpen: 'glyphicon-plus icon-plus',
      detailClose: 'glyphicon-minus icon-minus'
    },

    customSearch: $.noop,

    customSort: $.noop,

    rowStyle: function (row, index) {
      return {};
    },

    rowAttributes: function (row, index) {
      return {};
    },

    footerStyle: function (row, index) {
      return {};
    },

    onAll: function (name, args) {
      return false;
    },
    onClickCell: function (field, value, row, $element) {
      return false;
    },
    onDblClickCell: function (field, value, row, $element) {
      return false;
    },
    onClickRow: function (item, $element) {
      return false;
    },
    onDblClickRow: function (item, $element) {
      return false;
    },
    onSort: function (name, order) {
      return false;
    },
    onCheck: function (row) {
      return false;
    },
    onUncheck: function (row) {
      return false;
    },
    onCheckAll: function (rows) {
      return false;
    },
    onUncheckAll: function (rows) {
      return false;
    },
    onCheckSome: function (rows) {
      return false;
    },
    onUncheckSome: function (rows) {
      return false;
    },
    onLoadSuccess: function (data) {
      return false;
    },
    onLoadError: function (status) {
      return false;
    },
    onColumnSwitch: function (field, checked) {
      return false;
    },
    onPageChange: function (number, size) {
      return false;
    },
    onSearch: function (text) {
      return false;
    },
    onToggle: function (cardView) {
      return false;
    },
    onPreBody: function (data) {
      return false;
    },
    onPostBody: function () {
      return false;
    },
    onPostHeader: function () {
      return false;
    },
    onExpandRow: function (index, row, $detail) {
      return false;
    },
    onCollapseRow: function (index, row) {
      return false;
    },
    onRefreshOptions: function (options) {
      return false;
    },
    onRefresh: function (params) {
      return false;
    },
    onResetView: function () {
      return false;
    }
  };

  BootstrapTable.LOCALES = {};

  // BootstrapTable.LOCALES['en-US'] = BootstrapTable.LOCALES.en = {
  //     formatLoadingMessage: function () {
  //         return 'Loading, please wait...';
  //     },
  //     formatRecordsPerPage: function (pageNumber) {
  //         return sprintf('%s rows per page', pageNumber);
  //     },
  //     formatShowingRows: function (pageFrom, pageTo, totalRows) {
  //         return sprintf('Showing %s to %s of %s rows', pageFrom, pageTo, totalRows);
  //     },
  //     formatDetailPagination: function (totalRows) {
  //         return sprintf('Showing %s rows', totalRows);
  //     },
  //     formatSearch: function () {
  //         return 'Search';
  //     },
  //     formatNoMatches: function () {
  //         return 'No matching records found';
  //     },
  //     formatPaginationSwitch: function () {
  //         return 'Hide/Show pagination';
  //     },
  //     formatRefresh: function () {
  //         return 'Refresh';
  //     },
  //     formatToggle: function () {
  //         return 'Toggle';
  //     },
  //     formatColumns: function () {
  //         return 'Columns';
  //     },
  //     formatAllRows: function () {
  //         return 'All';
  //     }
  // };

  BootstrapTable.LOCALES['zh-CN'] = BootstrapTable.LOCALES.cn = {
    formatLoadingMessage: function () {
      return '数据加载中，请稍候';
    },
    formatRecordsPerPage: function (pageNumber) {
      return '每页显示 '.concat(pageNumber, ' 记录');
    },
    formatShowingRows: function (pageFrom, pageTo, totalRows) {
      return sprintf('显示第 %s 到第 %s 条记录，总共 %s 条记录', pageFrom, pageTo, totalRows);
      if (totalNotFiltered !== undefined && totalNotFiltered > 0 && totalNotFiltered > totalRows) {
        return '显示第 '
          .concat(pageFrom, ' 到第 ')
          .concat(pageTo, ' 条记录，总共 ')
          .concat(totalRows, ' 条记录（从 ')
          .concat(totalNotFiltered, ' 总记录中过滤）');
      }
    },
    formatDetailPagination: function (totalRows) {
      return '总共 '.concat(totalRows, ' 条记录');
    },
    formatSearch: function () {
      return '搜索';
    },
    formatNoMatches: function () {
      return '没有找到匹配的记录';
    },
    formatPaginationSwitch: function () {
      return '隐藏/显示分页';
    },
    formatRefresh: function () {
      return '刷新';
    },
    formatToggle: function () {
      return '切换';
    },
    formatColumns: function () {
      return '列';
    },
    formatAllRows: function () {
      return '所有';
    }
  };

  $.extend(BootstrapTable.DEFAULTS, BootstrapTable.LOCALES['zh-CN']);

  BootstrapTable.COLUMN_DEFAULTS = {
    radio: false,
    checkbox: false,
    checkboxEnabled: true,
    field: undefined,
    title: undefined,
    titleTooltip: undefined,
    class: undefined,
    align: undefined, // left, right, center
    halign: undefined, // left, right, center
    falign: undefined, // left, right, center
    valign: undefined, // top, middle, bottom
    width: undefined,
    sortable: false,
    order: 'asc', // asc, desc
    visible: true,
    switchable: true,
    clickToSelect: true,
    formatter: undefined,
    footerFormatter: undefined,
    events: undefined,
    sorter: undefined,
    sortName: undefined,
    cellStyle: undefined,
    searchable: true,
    searchFormatter: true,
    cardVisible: true
  };

  BootstrapTable.EVENTS = {
    'all.bs.table': 'onAll',
    'click-cell.bs.table': 'onClickCell',
    'dbl-click-cell.bs.table': 'onDblClickCell',
    'click-row.bs.table': 'onClickRow',
    'dbl-click-row.bs.table': 'onDblClickRow',
    'sort.bs.table': 'onSort',
    'check.bs.table': 'onCheck',
    'uncheck.bs.table': 'onUncheck',
    'check-all.bs.table': 'onCheckAll',
    'uncheck-all.bs.table': 'onUncheckAll',
    'check-some.bs.table': 'onCheckSome',
    'uncheck-some.bs.table': 'onUncheckSome',
    'load-success.bs.table': 'onLoadSuccess',
    'load-error.bs.table': 'onLoadError',
    'column-switch.bs.table': 'onColumnSwitch',
    'page-change.bs.table': 'onPageChange',
    'search.bs.table': 'onSearch',
    'toggle.bs.table': 'onToggle',
    'pre-body.bs.table': 'onPreBody',
    'post-body.bs.table': 'onPostBody',
    'post-header.bs.table': 'onPostHeader',
    'expand-row.bs.table': 'onExpandRow',
    'collapse-row.bs.table': 'onCollapseRow',
    'refresh-options.bs.table': 'onRefreshOptions',
    'reset-view.bs.table': 'onResetView',
    'refresh.bs.table': 'onRefresh',
    'save-config.bs.table': 'onSaveConfig'
  };

  BootstrapTable.prototype.init = function () {
    this.initLocale();
    this.initContainer();
    this.initTable();
    this.initHeader();
    this.initData();
    this.initFooter();
    this.initToolbar();
    this.initPagination();
    this.initBody();
    this.initSearchText();
    this.initServer();
  };

  BootstrapTable.prototype.initLocale = function () {
    if (this.options.locale) {
      var parts = this.options.locale.split(/-|_/);
      parts[0].toLowerCase();
      if (parts[1]) parts[1].toUpperCase();
      if ($.fn.bootstrapTable.locales[this.options.locale]) {
        // locale as requested
        $.extend(this.options, $.fn.bootstrapTable.locales[this.options.locale]);
      } else if ($.fn.bootstrapTable.locales[parts.join('-')]) {
        // locale with sep set to - (in case original was specified with _)
        $.extend(this.options, $.fn.bootstrapTable.locales[parts.join('-')]);
      } else if ($.fn.bootstrapTable.locales[parts[0]]) {
        // short locale language code (i.e. 'en')
        $.extend(this.options, $.fn.bootstrapTable.locales[parts[0]]);
      }
    }
  };

  BootstrapTable.prototype.initContainer = function () {
    this.$container = $(
      [
        '<div class="bootstrap-table">',

        '<div class="fixed-table-toolbar"></div>',
        this.options.paginationVAlign === 'top' || this.options.paginationVAlign === 'both' ?
        '<div class="fixed-table-pagination" style="clear: both;"></div>' :
        '',
        '<div class="fixed-table-container">',
        '<div class="fixed-table-header"><table></table></div>',
        '<div class="fixed-table-body-container">',
        '<div class="fixed-table-body">',
        '<div class="fixed-table-loading display-none">',
        '<div class="loader"></div>',
        '<div class="fixed-table-loading-msg">',
        this.options.formatLoadingMessage(),
        '</div>',
        '</div>',
        '</div>',
        '</div>',
        '<div class="fixed-table-footer"><table><tr></tr></table></div>',
        this.options.paginationVAlign === 'bottom' || this.options.paginationVAlign === 'both' ?
        '<div class="fixed-table-pagination"></div>' :
        '',
        '</div>',
        '</div>'
      ].join('')
    );

    this.$container.insertAfter(this.$el);
    this.$tableContainer = this.$container.find('.fixed-table-container');
    this.$tableHeader = this.$container.find('.fixed-table-header');
    this.$tableBodyContainer = this.$container.find('.fixed-table-body-container');
    this.$tableBody = this.$container.find('.fixed-table-body');
    this.$tableLoading = this.$container.find('.fixed-table-loading');
    this.$tableFooter = this.$container.find('.fixed-table-footer');
    this.$toolbar = this.$container.find('.fixed-table-toolbar');
    this.$pagination = this.$container.find('.fixed-table-pagination');

    this.$tableBody.append(this.$el);
    this.$container.after('<div class="clearfix"></div>');

    this.$el.addClass(this.options.classes);
    if (this.options.striped) {
      this.$el.addClass('table-striped');
    }
    if ($.inArray('table-no-bordered', this.options.classes.split(' ')) !== -1) {
      this.$tableContainer.addClass('table-no-bordered');
    }
  };

  BootstrapTable.prototype.initTable = function () {
    var that = this,
      columns = [],
      data = [];

    this.$header = this.$el.find('>thead');
    if (!this.$header.length) {
      this.$header = $('<thead></thead>').appendTo(this.$el);
    }
    this.$header.find('tr').each(function () {
      var column = [];

      $(this)
        .find('th')
        .each(function () {
          // Fix #2014 - getFieldIndex and elsewhere assume this is string, causes issues if not
          if (typeof $(this).data('field') !== 'undefined') {
            $(this).data('field', $(this).data('field') + '');
          }
          column.push(
            $.extend({}, {
                title: $(this).html(),
                class: $(this).attr('class'),
                titleTooltip: $(this).attr('title'),
                rowspan: $(this).attr('rowspan') ? +$(this).attr('rowspan') : undefined,
                colspan: $(this).attr('colspan') ? +$(this).attr('colspan') : undefined
              },
              $(this).data()
            )
          );
        });
      columns.push(column);
    });
    if (!$.isArray(this.options.columns[0])) {
      this.options.columns = [this.options.columns];
    }
    if (columns.length > 0) {
      this.options.columns = $.extend(true, [], columns, this.options.columns);
    }
    this.columns = [];

    setFieldIndex(that.options.columns, that.options.hideInvisible);
    $.each(this.options.columns, function (i, columns) {
      $.each(columns, function (j, column) {
        column = $.extend({}, BootstrapTable.COLUMN_DEFAULTS, column);

        if (typeof column.fieldIndex !== 'undefined') {
          that.columns[column.fieldIndex] = column;
        }

        that.options.columns[i][j] = column;
      });
    });

    // if options.data is setting, do not process tbody data
    if (this.options.data.length) {
      return;
    }

    var m = [];
    this.$el.find('>tbody>tr').each(function (y) {
      var row = {};

      // save tr's id, class and data-* attributes
      row._id = $(this).attr('id');
      row._class = $(this).attr('class');
      row._data = getRealDataAttr($(this).data());

      $(this)
        .find('>td')
        .each(function (x) {
          var $this = $(this),
            cspan = +$this.attr('colspan') || 1,
            rspan = +$this.attr('rowspan') || 1,
            tx,
            ty;

          for (; m[y] && m[y][x]; x++); //skip already occupied cells in current row

          for (tx = x; tx < x + cspan; tx++) {
            //mark matrix elements occupied by current cell with true
            for (ty = y; ty < y + rspan; ty++) {
              if (!m[ty]) {
                //fill missing rows
                m[ty] = [];
              }
              m[ty][tx] = true;
            }
          }

          var field = that.columns[x].field;

          row[field] = $(this).html();
          // save td's id, class and data-* attributes
          row['_' + field + '_id'] = $(this).attr('id');
          row['_' + field + '_class'] = $(this).attr('class');
          row['_' + field + '_rowspan'] = $(this).attr('rowspan');
          row['_' + field + '_colspan'] = $(this).attr('colspan');
          row['_' + field + '_title'] = $(this).attr('title');
          row['_' + field + '_data'] = getRealDataAttr($(this).data());
        });
      data.push(row);
    });
    this.options.data = data;
    if (data.length) this.fromHtml = true;
  };

  BootstrapTable.prototype.initHeader = function () {
    var that = this,
      visibleColumns = {},
      html = [];

    this.header = {
      fields: [],
      styles: [],
      classes: [],
      formatters: [],
      events: [],
      sorters: [],
      sortNames: [],
      cellStyles: [],
      searchables: []
    };

    $.each(this.options.columns, function (i, columns) {
      html.push('<tr>');

      if (i === 0 && !that.options.cardView && that.options.detailView) {
        html.push(sprintf('<th class="detail" rowspan="%s"><div class="fht-cell"></div></th>', that.options.columns.length));
      }

      $.each(columns, function (j, column) {
        var text = '',
          halign = '', // header align style
          align = '', // body align style
          style = '',
          class_ = sprintf(' class="%s"', column['class']),
          order = that.options.sortOrder || column.order,
          unitWidth = 'px',
          width = column.width;

        if (column.width !== undefined && !that.options.cardView) {
          if (typeof column.width === 'string') {
            if (column.width.indexOf('%') !== -1) {
              unitWidth = '%';
            }
          }
        }
        if (column.width && typeof column.width === 'string') {
          width = column.width.replace('%', '').replace('px', '');
        }

        if (that.options.horizontalScroll && column.controlable && !column.width) {
          width = '300';
        }

        halign = sprintf('text-align: %s; ', column.halign ? column.halign : column.align);
        align = sprintf('text-align: %s; ', column.align);
        style = sprintf('vertical-align: %s; ', column.valign);
        style += sprintf('width: %s; ', (column.checkbox || column.radio) && !width ? '36px' : width ? width + unitWidth : undefined);

        if (typeof column.fieldIndex !== 'undefined') {
          that.header.fields[column.fieldIndex] = column.field;
          that.header.styles[column.fieldIndex] = align + style;
          that.header.classes[column.fieldIndex] = class_;
          that.header.formatters[column.fieldIndex] = column.formatter;
          that.header.events[column.fieldIndex] = column.events;
          that.header.sorters[column.fieldIndex] = column.sorter;
          that.header.sortNames[column.fieldIndex] = column.sortName;
          that.header.cellStyles[column.fieldIndex] = column.cellStyle;
          that.header.searchables[column.fieldIndex] = column.searchable;

          if (!column.visible && that.options.hideInvisible) {
            style = 'display:none;';
          } else if (!column.visible) {
            return;
          }

          if (that.options.cardView && !column.cardVisible) {
            return;
          }

          visibleColumns[column.field] = column;
        }

        var escapeTitle = (column.titleTooltip || '').replace(/\</g, '&lt;').replace(/\>/g, '&gt;').replace(/\"/g, "'");

        html.push(
          '<th' + sprintf(' title="%s"', escapeTitle),
          column.checkbox || column.radio ? sprintf(' class="bs-checkbox %s"', column['class'] || '') : class_,
          sprintf(' style="%s"', halign + style),
          sprintf(' rowspan="%s"', column.rowspan),
          sprintf(' colspan="%s"', column.colspan),
          sprintf(' data-field="%s"', column.field),
          "tabindex='0'",
          '>'
        );

        style = typeof style === 'string' ? style : style.toString();
        var innerStyle = style.indexOf('%') > -1 ? '100%' : style;

        var escapeInnerTitle = (column.editableOriginalTitle || column.title || '')
          .replace(/\</g, '&lt;')
          .replace(/\>/g, '&gt;')
          .replace(/\"/g, "'");

        html.push(
          sprintf(
            '<div class="th-inner %s" style="%s" title="' + escapeInnerTitle + '">',
            that.options.sortable && column.sortable ? 'sortable both' : '',
            innerStyle
          )
        );

        text = column.title;

        if (column.checkbox) {
          if (!that.options.singleSelect && that.options.checkboxHeader) {
            var uid = Math.random().toString(36).substring(2);
            text = '<input name="btSelectAll" type="checkbox" id="btSelectAll_' + uid + '"/>';
            text += '<label for="btSelectAll_' + uid + '"></label>';
          }
          that.header.stateField = column.field;
        }
        if (column.radio) {
          text = '';
          that.header.stateField = column.field;
          that.options.singleSelect = true;
        }

        html.push(text);
        html.push('</div>');
        html.push('<div class="fht-cell"></div>');
        html.push('</div>');
        html.push('</th>');
      });
      html.push('</tr>');
    });

    this.$header.html(html.join(''));
    this.$header.find('th[data-field]').each(function (i) {
      $(this).data(visibleColumns[$(this).data('field')]);
    });
    this.$container.off('click', '.th-inner').on('click', '.th-inner', function (event) {
      var target = $(this);

      if (that.options.detailView) {
        if (target.closest('.bootstrap-table')[0] !== that.$container[0]) return false;
      }

      if (that.options.sortable && target.parent().data().sortable) {
        that.onSort(event);
      }
    });

    this.$header
      .children()
      .children()
      .off('keypress')
      .on('keypress', function (event) {
        if (that.options.sortable && $(this).data().sortable) {
          var code = event.keyCode || event.which;
          if (code == 13) {
            //Enter keycode
            that.onSort(event);
          }
        }
      });

    $(window).off('resize.bootstrap-table');
    if (!this.options.showHeader || this.options.cardView) {
      this.$header.hide();
      this.$tableHeader.hide();
      // this.$tableLoading.css('top', 0);
    } else {
      this.$header.show();
      this.$tableHeader.show();
      // this.$tableLoading.css('top', this.$header.outerHeight() + 1);
      // Assign the correct sortable arrow
      this.getCaret();
      $(window).on('resize.bootstrap-table', $.proxy(this.resetWidth, this));
    }

    this.$selectAll = this.$header.find('[name="btSelectAll"]');
    this.$selectAll.off('click').on('click', function () {
      var checked = $(this).prop('checked');
      if ($(this).parents('.bootstrap-table').find('.fixed-table-body-columns').length > 0) {
        checked = $(this).parents('.bootstrap-table').find('.fixed-table-body-columns').find('[name="btSelectAll"]').prop('checked');
      }
      that[checked ? 'checkAll' : 'uncheckAll']();
      that.updateSelected();
    });
  };

  BootstrapTable.prototype.initFooter = function () {
    if (!this.options.showFooter || this.options.cardView) {
      this.$tableFooter.hide();
    } else {
      this.$tableFooter.show();
    }
  };

  /**
   * @param data
   * @param type: append / prepend
   */
  BootstrapTable.prototype.initData = function (data, type) {
    var self = this;
    var updateById = self.options.updateById && self.options.uniqueId;
    // 确保data中唯一值存在
    if (updateById && data) {
      var dataArr = $.isArray(data) ? data : [data];
      for (var i = 0; i < dataArr.length; i++) {
        var item = dataArr[i];
        if (item[self.options.uniqueId] != null) {
          continue; // 非空忽略
        }
        item[self.options.uniqueId] = self.options.uniqueId + '-' + $.guid++;
      }
    }
    if (type === 'append') {
      this.data = this.data.concat(data);
    } else if (type === 'prepend') {
      this.data = [].concat(data).concat(this.data);
    } else {
      this.data = data || this.options.data;
    }

    // Fix #839 Records deleted when adding new row on filtered table
    if (type === 'append') {
      this.options.data = this.options.data.concat(data);
    } else if (type === 'prepend') {
      this.options.data = [].concat(data).concat(this.options.data);
    } else {
      this.options.data = this.data;
    }

    if (this.options.sidePagination === 'server') {
      return;
    }
    this.initSort();
  };

  BootstrapTable.prototype.initSort = function () {
    var that = this,
      name = this.options.sortName,
      order = this.options.sortOrder === 'desc' ? -1 : 1,
      index = $.inArray(this.options.sortName, this.header.fields);

    if (this.options.customSort !== $.noop) {
      this.options.customSort.apply(this, [this.options.sortName, this.options.sortOrder]);
      return;
    }

    if (index !== -1) {
      if (this.options.sortStable) {
        $.each(this.data, function (i, row) {
          if (!row.hasOwnProperty('_position')) row._position = i;
        });
      }

      this.data.sort(function (a, b) {
        if (that.header.sortNames[index]) {
          name = that.header.sortNames[index];
        }
        var aa = getItemField(a, name, that.options.escape),
          bb = getItemField(b, name, that.options.escape),
          value = calculateObjectValue(that.header, that.header.sorters[index], [aa, bb]);

        if (value !== undefined) {
          return order * value;
        }

        // Fix #161: undefined or null string sort bug.
        if (aa === undefined || aa === null) {
          aa = '';
        }
        if (bb === undefined || bb === null) {
          bb = '';
        }

        if (that.options.sortStable && aa === bb) {
          aa = a._position;
          bb = b._position;
        }

        // IF both values are numeric, do a numeric comparison
        if ($.isNumeric(aa) && $.isNumeric(bb)) {
          // Convert numerical values form string to float.
          aa = parseFloat(aa);
          bb = parseFloat(bb);
          if (aa < bb) {
            return order * -1;
          }
          return order;
        }

        if (aa === bb) {
          return 0;
        }

        // If value is not a string, convert to string
        if (typeof aa !== 'string') {
          aa = aa.toString();
        }

        if (aa.localeCompare(bb) === -1) {
          return order * -1;
        }

        return order;
      });
    }
  };

  BootstrapTable.prototype.onSort = function (event) {
    var $this = event.type === 'keypress' ? $(event.currentTarget) : $(event.currentTarget).parent(),
      $this_ = this.$header.find('th').eq($this.index());

    this.$header.add(this.$header_).find('span.order').remove();

    if (this.options.sortName === $this.data('field')) {
      this.options.sortOrder = this.options.sortOrder === 'asc' ? 'desc' : 'asc';
    } else {
      this.options.sortName = $this.data('field');
      this.options.sortOrder = $this.data('order') === 'asc' ? 'desc' : 'asc';
    }
    this.trigger('sort', this.options.sortName, this.options.sortOrder);

    $this.add($this_).data('order', this.options.sortOrder);

    // Assign the correct sortable arrow
    this.getCaret();

    if (this.options.sidePagination === 'server') {
      this.initServer(this.options.silentSort);
      return;
    }

    this.initSort();
    this.initBody();
  };

  BootstrapTable.prototype.resetColumnConfigStatus = function () {
    var that = this;
    var $tableLis = $('.bt-custom-columns-wrapper .bt-custom-columns-popup-show-columns-area li', this.$toolbar);
    var fixedNumber = $('.bt-custom-columns-popup-fixed-columns-area .fixed-left-number', this.$toolbar).val();
    var fixedRightNumber = $('.bt-custom-columns-popup-fixed-columns-area .fixed-right-number', this.$toolbar).val();
    var len = $tableLis.length;
    $tableLis.removeClass('fixed-column');
    var i = 0,
      j = 0;

    for (var m = 0; m < len; m++) {
      var $li = $($tableLis[m]);
      if (i < fixedNumber && $li.find('input:checked').length) {
        $li.addClass('fixed-column');
        i++;
      }
    }

    for (var n = len - 1; n > -1; n--) {
      var $li = $($tableLis[n]);
      if (j < fixedRightNumber && $li.find('input:checked').length) {
        $li.addClass('fixed-column');
        j++;
      }
    }
  };

  BootstrapTable.prototype.collectColumnsConfig = function () {
    var that = this;
    var config = {};
    config.fixedNumber = $('.bt-custom-columns-popup-fixed-columns-area .fixed-left-number').val();
    config.fixedRightNumber = $('.bt-custom-columns-popup-fixed-columns-area .fixed-right-number').val();
    if (config.fixedNumber == '0' && config.fixedRightNumber == '0') {
      config.fixedColumns = false;
    } else {
      config.fixedColumns = true;
    }
    if (this.options.showColumns) {
      config.order = [];
      config.forcedVisibility = {};
      $.each($('.bt-custom-columns-wrapper .bt-custom-columns-popup-show-columns-area li', this.$toolbar), function (idx, li) {
        var $li = $(li);
        var uuid = $li.attr('uuid');
        config.order.push(uuid);
        config.forcedVisibility[uuid] = !!$li.find('input:checked').length;
      });
    }
    return config;
  };

  function _isColumnVisible(column, customConfig, defaultConfig) {
    if (column.hidden === '1') {
      return false;
    }

    if (column.alwaysDisplay === '1') {
      return true;
    }

    if (defaultConfig.showColumns && customConfig && customConfig.forcedVisibility && column.uuid in customConfig.forcedVisibility) {
      return customConfig.forcedVisibility[column.uuid] === 'true' || customConfig.forcedVisibility[column.uuid] === true;
    }

    if (column.defaultDisplay === '0') {
      return false;
    }

    return true;
  }

  BootstrapTable.prototype.initColumnConfig = function () {
    // CMS表格组件
    var that = this;
    var defaultConfig = this.options.widgetConfiguration;
    var customConfig = this.options.customConfig;
    // 开启自定义列 拼接dom
    if (defaultConfig && this.options.showColumns) {
      var $wrapper = $('<div class="bt-custom-columns-wrapper bs-bars pull-right" title="自定义列"></div>');
      var $hoverButton = $('<span class="bt-custom-columns-button"><i class="iconfont icon-ptkj-shezhi"></i><span>');
      $wrapper.append($hoverButton);

      // 弹出层
      var $popup = $('<div class="bt-custom-columns-popup"></div>');

      // 弹出层标题区域
      var $popupHeader = $('<div class="bt-custom-columns-popup-title"></div>');
      $popupHeader.append('<span class="bt-custom-columns-popup-title-main">自定义表格</span>');
      var subtitle = '';
      subtitle += '自定义配置表格显示列、冻结列、列的顺序';
      $popupHeader.append('<span class="bt-custom-columns-popup-title-sub">' + subtitle + '</span>');
      $popup.append($popupHeader);

      var fixedNumber = '';
      var fixedRightNumber = '';
      if (customConfig && customConfig.fixedNumber) {
        fixedNumber = customConfig.fixedNumber;
      } else {
        fixedNumber = this.options.fixedColumns ? this.options.fixedNumber : 0;
      }

      if (customConfig && customConfig.fixedRightNumber) {
        fixedRightNumber = customConfig.fixedRightNumber;
      } else {
        fixedRightNumber = this.options.fixedColumns ? this.options.fixedRightNumber : 0;
      }

      // 弹出层配置固定列数量区域
      // if (this.options.fixedColumns) {

      // }
      var $popupFixedNumber = $('<div class="bt-custom-columns-popup-fixed-columns-area"></div>');
      $popupFixedNumber.append(
        '冻结表格 ' +
        '<span style="margin-right:20px;">前 <input class="fixed-number fixed-left-number" type="tel" style="width:80px;"><span id="preSpan" style="position:relative;display:inline-block;width:0px;left:-28px;color:#000;"><i class="iconfont" id="pre_add" style="position: relative;top: -6px;padding: 0px 5px;cursor:pointer;font-size:12px;color:#000;">&#xe908;</i><i class="iconfont" id="pre_inc" style="position: relative;left: -34px;top: 5px;padding: 0 5px;cursor:pointer;font-size:12px;color:#000;">&#xe90f;</i></span> 列</span>' +
        '<span style="margin-right:20px;">后 <input class="fixed-number fixed-right-number" type="tel" style="width:80px;"><span style="position:relative;display:inline-block;width:0px;left:-28px;"><i class="iconfont" id="next_add" style="position: relative;top: -6px;padding: 0px 5px;cursor:pointer;font-size:12px;color:#000;">&#xe908;</i><i class="iconfont" id="next_inc" style="position: relative;left: -34px;top: 5px;padding: 0 5px;cursor:pointer;font-size:12px;color:#000;">&#xe90f;</i></span> 列</span>'
      );
      var $fixedNumberInput = $popupFixedNumber.find('.fixed-left-number');
      $fixedNumberInput.val(fixedNumber);
      var $fixedRightNumberInput = $popupFixedNumber.find('.fixed-right-number');
      $fixedRightNumberInput.val(fixedRightNumber);
      $popupFixedNumber.append(
        '<i class="iconfont icon-ptkj-tishishuoming" data-toggle="popover" id="example" title="设置表格的冻结列，冻结的列不受表格左右拖动影响，冻结列为0时表示不冻结"></i>'
      );

      // 绑定事件 - 加1  减1
      $('#pre_add', $popupFixedNumber)
        .off('click')
        .on('click', function (e) {
          var leftInputValue = $('.fixed-left-number', $popupFixedNumber).val();
          $('.fixed-left-number', $popupFixedNumber).val(parseInt(leftInputValue) + 1 + '');
          that.resetColumnConfigStatus();
        });
      $('#pre_inc', $popupFixedNumber)
        .off('click')
        .on('click', function (e) {
          var leftInputValue = $('.fixed-left-number', $popupFixedNumber).val();
          if (leftInputValue !== '0') {
            $('.fixed-left-number', $popupFixedNumber).val(parseInt(leftInputValue) - 1 + '');
          }
          that.resetColumnConfigStatus();
        });

      $('#next_add', $popupFixedNumber)
        .off('click')
        .on('click', function (e) {
          var rightInputValue = $('.fixed-right-number', $popupFixedNumber).val();
          $('.fixed-right-number', $popupFixedNumber).val(parseInt(rightInputValue) + 1 + '');
          that.resetColumnConfigStatus();
        });
      $('#next_inc', $popupFixedNumber)
        .off('click')
        .on('click', function (e) {
          var rightInputValue = $('.fixed-right-number', $popupFixedNumber).val();
          if (rightInputValue !== '0') {
            $('.fixed-right-number', $popupFixedNumber).val(parseInt(rightInputValue) - 1 + '');
          }
          that.resetColumnConfigStatus();
        });

      $popup.append($popupFixedNumber);

      // 弹出层配置列显示、排序区域
      var $popupShownColumnsArea = $('<div class="bt-custom-columns-popup-show-columns-area"><ul></ul></div>');
      if (!this.options.showColumns) {
        $popupShownColumnsArea.addClass('disable-custom-columns');
      }
      var shownColumns = defaultConfig.columns.filter(function (col) {
        return !+col.hidden;
      });

      window.insertionSort(shownColumns, function (col1, col2) {
        if (!customConfig || !customConfig.order) {
          return 0;
        }

        var idx1 = $.inArray(col1.uuid, customConfig.order);
        var idx2 = $.inArray(col2.uuid, customConfig.order);

        if (idx1 === -1 && idx2 === -1) {
          return 0;
        } else if (idx1 === -1) {
          return 1;
        } else if (idx2 === -1) {
          return -1;
        } else {
          return idx1 - idx2;
        }
      });

      var fixedCount = 0;

      for (var i = 0; i < shownColumns.length; i++) {
        var col = shownColumns[i];

        var $columnLi = $('<li class="bt-custom-column"><i class="iconfont icon-ptkj-tuodong" title="拖动排序"></i></li>');
        var $columnLiLabel = $('<label class="bs-checkbox"></label>');

        $columnLiLabel.append('<input type="checkbox"><label onclick="$(this).prev().click();"></label>');
        $columnLiLabel.append('<span class="bt-custom-column-title">' + col.header + '</span>');
        $columnLi.append($columnLiLabel).attr('uuid', col.uuid).attr('title', col.header);
        var $checkbox = $columnLi.find('input[type="checkbox"]');

        var visible = _isColumnVisible(col, customConfig, defaultConfig);
        if (visible) {
          $checkbox.attr('checked', 'checked');
        } else {
          $checkbox.removeAttr('checked');
        }

        if (+col.alwaysDisplay || !defaultConfig.showColumns) {
          $checkbox.attr('disabled', 'disabled');
        } else {
          $checkbox.removeAttr('disabled');
        }

        if (this.options.fixedNumber > fixedCount && this.options.fixedColumns && visible) {
          $columnLi.addClass('fixed-column');
          fixedCount++;
        }
        if (i >= shownColumns.length - this.options.fixedRightNumber && this.options.fixedColumns && visible) {
          $columnLi.addClass('fixed-column');
        }

        $popupShownColumnsArea.children('ul').append($columnLi);
      }

      setTimeout(function () {
        $popupShownColumnsArea.niceScroll({
          iframeautoresize: true,
          zindex: 100,
          cursorcolor: '#000000',
          cursoropacitymax: 0.15,
          cursorwidth: '6px',
          cursorborderradius: '3px',
          autohidemode: false,
          railpadding: {
            top: 0,
            right: 1,
            left: 0,
            bottom: 0
          }
        });
      }, 100);

      $popupShownColumnsArea.children('ul').sortable({
        axis: 'y',
        cursor: 'move',
        scroll: true,
        handle: '.iconfont',
        containment: 'parent',
        revert: false,
        stop: function () {
          that.resetColumnConfigStatus();
        }
      });

      $popup.append($popupShownColumnsArea);

      // 弹出层底部按钮区域
      var $popupFooter = $('<div class="bt-custom-columns-popup-footer"></div>');
      $popupFooter.append('<button type="button" class="well-btn w-btn-primary confirm-btn" title="确定">确定</button>');
      $popupFooter.append('<button type="button" class="well-btn w-btn-primary w-line-btn reset-btn" title="恢复默认">恢复默认</button>');
      $popupFooter.append('<button type="button" class="well-btn w-btn-primary w-line-btn cancel-btn" title="取消">取消</button>');
      $popup.append($popupFooter);
      $wrapper.append($popup);

      // 绑定事件
      // - 点击按钮打开弹出层
      $hoverButton.off('click').on('click', function () {
        var isOpen = $wrapper.hasClass("menu-open");
        $wrapper[isOpen?"removeClass":"addClass"]('menu-open');
        $popupShownColumnsArea.getNiceScroll().resize();
        $fixedNumberInput.trigger('change');
        $fixedRightNumberInput.trigger('change');
      });

      // - 固定列数
      if (this.options.showColumns) {
        // 固定左边输入款失去焦点
        $fixedNumberInput.off('change').on('change', function (e) {
          that.resetColumnConfigStatus();
        });
        $fixedNumberInput.off('input').on('input', function (val) {
          var $this = $(this);
          $this.val($this.val().replace(/[^\d]/g, '') || '');
        });

        // g固定右侧 输入框失去焦点
        $fixedRightNumberInput.off('change').on('change', function (e) {
          that.resetColumnConfigStatus();
        });
        $fixedRightNumberInput.off('input').on('input', function (val) {
          var $this = $(this);
          $this.val($this.val().replace(/[^\d]/g, '') || '');
        });
      }

      // - 点击列单选框
      $popupShownColumnsArea
        .find('input[type="checkbox"]')
        .off('click')
        .on('click', function () {
          that.resetColumnConfigStatus();
        });

      // - 确定按钮
      $popupFooter
        .find('.confirm-btn')
        .off('click')
        .on('click', function () {
          var config = that.collectColumnsConfig();
          that.trigger('save-config', config);
        });

      // - 恢复默认按钮
      $popupFooter
        .find('.reset-btn')
        .off('click')
        .on('click', function () {
          var $columns = $popupShownColumnsArea.find('ul li');

          // 重设是否显示
          $.each($columns, function (idx, col) {
            var $col = $(col);
            var uuid = $col.attr('uuid');
            var originalCol = defaultConfig.columns.find(function (column) {
              return column.uuid === uuid;
            });

            var isVisible = originalCol.hidden !== '1' && (originalCol.defaultDisplay !== '0' || originalCol.alwaysDisplay === '1');
            if (isVisible) {
              $col.find('input[type="checkbox"]').attr('checked', 'checked');
            } else {
              $col.find('input[type="checkbox"]').removeAttr('checked');
            }
          });

          // 排序
          $columns.sort(function (col1, col2) {
            var uuid1 = $(col1).attr('uuid');
            var uuid2 = $(col2).attr('uuid');
            var idx1 = defaultConfig.columns.findIndex(function (column) {
              return column.uuid === uuid1;
            });
            var idx2 = defaultConfig.columns.findIndex(function (column) {
              return column.uuid === uuid2;
            });

            return idx1 - idx2;
          });
          $popupShownColumnsArea.find('ul li').remove();
          for (var i = 0; i < $columns.length; i++) {
            $popupShownColumnsArea.find('ul').append($columns[i]);
          }

          // 重新绑定事件
          $popupShownColumnsArea
            .find('input[type="checkbox"]')
            .off('click')
            .on('click', function () {
              that.resetColumnConfigStatus();
            });

          // 重设固定列
          if (!defaultConfig.fixedColumns) {
            $wrapper.find('.fixed-left-number').val('0');
            $wrapper.find('.fixed-right-number').val('0');
          } else {
            $wrapper.find('.fixed-left-number').val(defaultConfig.fixedNumber);
            $wrapper.find('.fixed-right-number').val(defaultConfig.fixedRightNumber);
          }
          that.resetColumnConfigStatus();
        });

      // -取消按钮
      $popupFooter
        .find('.cancel-btn')
        .off('click')
        .on('click', function () {
          $wrapper.removeClass('menu-open');
        });

      return $wrapper;
    }

    return null;
  };

  BootstrapTable.prototype.initToolbar = function () {
    var that = this,
      html = [],
      timeoutId = 0,
      $keepOpen,
      $search,
      switchableCount = 0;

    if (this.$toolbar.find('.bs-bars').children().length) {
      $('body').append($(this.options.toolbar));
    }
    this.$toolbar.html('');

    if (typeof this.options.toolbar === 'string' || typeof this.options.toolbar === 'object') {
      $(sprintf('<div class="bs-bars pull-%s"></div>', this.options.toolbarAlign)).appendTo(this.$toolbar).append($(this.options.toolbar));
    }

    // showColumns, showToggle, showRefresh
    html = [sprintf('<div class="columns columns-%s btn-group pull-%s">', this.options.buttonsAlign, this.options.buttonsAlign)];

    if (typeof this.options.icons === 'string') {
      this.options.icons = calculateObjectValue(null, this.options.icons);
    }

    if (this.options.showPaginationSwitch) {
      html.push(
        sprintf(
          '<button class="btn' +
          sprintf(' btn-%s', this.options.buttonsClass) +
          sprintf(' btn-%s', this.options.iconSize) +
          '" type="button" name="paginationSwitch" title="%s">',
          this.options.formatPaginationSwitch()
        ),
        sprintf('<i class="%s %s"></i>', this.options.iconsPrefix, this.options.icons.paginationSwitchDown),
        '</button>'
      );
    }

    if (this.options.showRefresh) {
      html.push(
        sprintf(
          '<button class="btn' +
          sprintf(' btn-%s', this.options.buttonsClass) +
          sprintf(' btn-%s', this.options.iconSize) +
          '" type="button" name="refresh" title="%s">',
          this.options.formatRefresh()
        ),
        sprintf('<i class="%s %s"></i>', this.options.iconsPrefix, this.options.icons.refresh),
        '</button>'
      );
    }

    if (this.options.showToggle) {
      html.push(
        sprintf(
          '<button class="btn' +
          sprintf(' btn-%s', this.options.buttonsClass) +
          sprintf(' btn-%s', this.options.iconSize) +
          '" type="button" name="toggle" title="%s">',
          this.options.formatToggle()
        ),
        sprintf('<i class="%s %s"></i>', this.options.iconsPrefix, this.options.icons.toggle),
        '</button>'
      );
    }

    // 普通 bootstrap table 组件
    if (!this.options.widgetConfiguration && this.options.showColumns) {
      html.push(
        sprintf('<div class="keep-open show-columns-wrap btn-group" title="%s">', this.options.formatColumns()),
        '<button type="button" class="btn columns-btn-style' +
        sprintf(' btn-%s', this.options.buttonsClass) +
        sprintf(' btn-%s', this.options.iconSize) +
        ' dropdown-toggle" data-toggle="dropdown">',
        sprintf('<i class="%s %s"></i>', this.options.iconsPrefix, this.options.icons.columns),
        ' <span class="caret"></span>',
        '</button>',
        '<ul class="dropdown-menu" role="menu">'
      );
      $.each(this.columns, function (i, column) {
        if (column.radio || column.checkbox || !column.visible) {
          return;
        }

        if (that.options.cardView && !column.cardVisible) {
          return;
        }

        var checked = column.visible ? ' checked="checked"' : '';

        if (column.switchable) {
          html.push(
            sprintf(
              '<li>' +
              '<label><input type="checkbox" id="%s" data-field="%s" value="%s"%s> <label for="%s" style="padding:0;">%s</label></label>' +
              '</li>',
              column.field + i,
              column.field,
              i,
              checked,
              column.field + i,
              column.title
            )
          );
          switchableCount++;
        }
      });
      html.push('</ul>', '</div>');

      html.push('</div>');
    }

    // Fix #188: this.showToolbar is for extensions
    if (this.showToolbar || html.length > 2) {
      this.$toolbar.append(html.join(''));
    }

    if (this.options.showPaginationSwitch) {
      this.$toolbar.find('button[name="paginationSwitch"]').off('click').on('click', $.proxy(this.togglePagination, this));
    }

    if (this.options.showRefresh) {
      this.$toolbar.find('button[name="refresh"]').off('click').on('click', $.proxy(this.refresh, this));
    }

    if (this.options.showToggle) {
      this.$toolbar
        .find('button[name="toggle"]')
        .off('click')
        .on('click', function () {
          that.toggleView();
        });
    }

    if (this.options.showColumns) {
      $keepOpen = this.$toolbar.find('.keep-open');

      if (switchableCount <= this.options.minimumCountColumns) {
        $keepOpen.find('input').prop('disabled', true);
      }

      $keepOpen
        .find('li')
        .off('click')
        .on('click', function (event) {
          event.stopImmediatePropagation();
        });
      $keepOpen
        .find('input')
        .off('click')
        .on('click', function () {
          var $this = $(this);
          that.toggleColumn($(this).val(), $this.prop('checked'), false);
          that.trigger('column-switch', $(this).data('field'), $this.prop('checked'));
          if (that.__proto__.hasOwnProperty('fixedColumns')) {
            that.fixedColumns();
          }
        });
    }

    if (this.options.search) {
      html = [];
      html.push(
        '<div class="pull-' + this.options.searchAlign + ' search">',
        sprintf(
          '<input class="form-control' + sprintf(' input-%s', this.options.iconSize) + '" type="search" placeholder="%s">',
          this.options.formatSearch()
        ),
        '</div>'
      );

      this.$toolbar.append(html.join(''));
      $search = this.$toolbar.find('.search input');
      $search.off('keyup drop').on('keyup drop', function (event) {
        if (that.options.searchOnEnterKey && event.keyCode !== 13) {
          return;
        }

        if ($.inArray(event.keyCode, [37, 38, 39, 40]) > -1) {
          return;
        }

        clearTimeout(timeoutId); // doesn't matter if it's 0
        timeoutId = setTimeout(function () {
          that.onSearch(event);
        }, that.options.searchTimeOut);
      });

      if (isIEBrowser()) {
        $search.off('mouseup').on('mouseup', function (event) {
          clearTimeout(timeoutId); // doesn't matter if it's 0
          timeoutId = setTimeout(function () {
            that.onSearch(event);
          }, that.options.searchTimeOut);
        });
      }
    }

    var $popup = this.initColumnConfig();

    this.$toolbar.append($popup);
  };

  BootstrapTable.prototype.onSearch = function (event) {
    var text = $.trim($(event.currentTarget).val());

    // trim search input
    if (this.options.trimOnSearch && $(event.currentTarget).val() !== text) {
      $(event.currentTarget).val(text);
    }

    if (text === this.searchText) {
      return;
    }
    this.searchText = text;
    this.options.searchText = text;

    this.options.pageNumber = 1;
    this.initSearch();
    this.updatePagination();
    this.trigger('search', text);
  };

  BootstrapTable.prototype.initSearch = function () {
    var that = this;

    if (this.options.sidePagination !== 'server') {
      if (this.options.customSearch !== $.noop) {
        this.options.customSearch.apply(this, [this.searchText]);
        return;
      }

      var s = this.searchText && (this.options.escape ? escapeHTML(this.searchText) : this.searchText).toLowerCase();
      var f = $.isEmptyObject(this.filterColumns) ? null : this.filterColumns;

      // Check filter
      this.data = f ?
        $.grep(this.options.data, function (item, i) {
          for (var key in f) {
            if (($.isArray(f[key]) && $.inArray(item[key], f[key]) === -1) || item[key] !== f[key]) {
              return false;
            }
          }
          return true;
        }) :
        this.options.data;

      this.data = s ?
        $.grep(this.data, function (item, i) {
          for (var j = 0; j < that.header.fields.length; j++) {
            if (!that.header.searchables[j]) {
              continue;
            }

            var key = $.isNumeric(that.header.fields[j]) ? parseInt(that.header.fields[j], 10) : that.header.fields[j];
            var column = that.columns[getFieldIndex(that.columns, key)];
            var value;

            if (typeof key === 'string') {
              value = item;
              var props = key.split('.');
              for (var prop_index = 0; prop_index < props.length; prop_index++) {
                value = value[props[prop_index]];
              }

              // Fix #142: respect searchForamtter boolean
              if (column && column.searchFormatter) {
                value = calculateObjectValue(column, that.header.formatters[j], [value, item, i], value);
              }
            } else {
              value = item[key];
            }

            if (typeof value === 'string' || typeof value === 'number') {
              if (that.options.strictSearch) {
                if ((value + '').toLowerCase() === s) {
                  return true;
                }
              } else {
                if ((value + '').toLowerCase().indexOf(s) !== -1) {
                  return true;
                }
              }
            }
          }
          return false;
        }) :
        this.data;
    }
  };

  BootstrapTable.prototype.initPagination = function () {
    var _self = this;
    if (!this.options.pagination) {
      this.$pagination.hide();
      return;
    } else {
      this.$pagination.show();
    }

    this.$el.parents(".ui-wBootstrapTable").on("fixedPagination", function () {
      _self.fixedPagination()
    });

    var that = this,
      html = [],
      $allSelected = false,
      i,
      from,
      to,
      $pageList,
      $first,
      $pre,
      $next,
      $last,
      $number,
      $jumpNumber,
      $pageJump,
      data = this.getData(),
      pageList = this.options.pageList;

    if (this.options.sidePagination !== 'server') {
      this.options.totalRows = data.length;
    }

    this.totalPages = 0;
    if (this.options.totalRows) {
      if (this.options.pageSize === this.options.formatAllRows()) {
        this.options.pageSize = this.options.totalRows;
        $allSelected = true;
      } else if (this.options.pageSize === this.options.totalRows) {
        // Fix #667 Table with pagination,
        // multiple pages and a search that matches to one page throws exception
        var pageLst =
          typeof this.options.pageList === 'string' ?
          this.options.pageList.replace('[', '').replace(']', '').replace(/ /g, '').toLowerCase().split(',') :
          this.options.pageList;
        if ($.inArray(this.options.formatAllRows().toLowerCase(), pageLst) > -1) {
          $allSelected = true;
        }
      }

      this.totalPages = ~~((this.options.totalRows - 1) / this.options.pageSize) + 1;

      this.options.totalPages = this.totalPages;
    }
    if (this.totalPages > 0 && this.options.pageNumber > this.totalPages) {
      this.options.pageNumber = this.totalPages;
    }

    this.pageFrom = (this.options.pageNumber - 1) * this.options.pageSize + 1;
    this.pageTo = this.options.pageNumber * this.options.pageSize;
    if (this.pageTo > this.options.totalRows) {
      this.pageTo = this.options.totalRows;
    }

    if (!this.options.onlyInfoPagination) {
      html.push('<span class="page-list pull-left">');

      var pageNumber = [
        sprintf(
          '<span class="btn-group %s">',
          this.options.paginationVAlign === 'top' || this.options.paginationVAlign === 'both' ? 'dropdown' : 'dropup'
        ),
        '<button type="button" class="btn' +
        sprintf(' btn-%s', this.options.buttonsClass) +
        sprintf(' btn-%s', this.options.iconSize) +
        ' dropdown-toggle" data-toggle="dropdown">',
        '<span class="page-size">',
        $allSelected ?
        this.options.formatAllRows() :
        this.options.formatePageSize ?
        this.options.formatePageSize(this.options.pageSize) :
        this.options.pageSize,
        '</span>',
        ' <i class="fa fa-angle-up"></i>',
        '</button>',
        '<ul class="dropdown-menu" role="menu">'
      ];

      if (typeof this.options.pageList === 'string') {
        var list = this.options.pageList.replace('[', '').replace(']', '').replace(/ /g, '').split(',');

        pageList = [];
        $.each(list, function (i, value) {
          pageList.push(value.toUpperCase() === that.options.formatAllRows().toUpperCase() ? that.options.formatAllRows() : +value);
        });
      }

      $.each(pageList, function (i, page) {
        if (!that.options.smartDisplay || i === 0 || pageList[i - 1] <= that.options.totalRows) {
          var active;
          if ($allSelected) {
            active = page === that.options.formatAllRows() ? ' class="active"' : '';
          } else {
            active = page === that.options.pageSize ? ' class="active"' : '';
          }
          pageNumber.push(sprintf('<li%s><a href="javascript:void(0)">%s</a></li>', active, page));
        }
      });
      pageNumber.push('</ul></span>');

      html.push(this.options.formatRecordsPerPage(pageNumber.join('')));
      html.push('</span>');

      html.push(
        '</div>',
        '<div class="pull-' + this.options.paginationHAlign + ' pagination">',
        '<ul class="pagination' + sprintf(' pagination-%s', this.options.iconSize) + '">'
      );
      if(this.options.pageNumber == 1){
        html.push('<li class="page-pre disabled"><a href="javascript:void(0)">' + this.options.paginationPreText + '</a></li>');
      }else{
        html.push('<li class="page-pre"><a href="javascript:void(0)">' + this.options.paginationPreText + '</a></li>');
      }

      if (this.totalPages < 5) {
        from = 1;
        to = this.totalPages;
      } else {
        from = this.options.pageNumber - 2;
        to = from + 4;
        if (from < 1) {
          from = 1;
          to = 5;
        }
        if (to > this.totalPages) {
          to = this.totalPages;
          from = to - 4;
        }
      }

      if (this.totalPages >= 6) {
        if (this.options.pageNumber >= 3) {
          html.push(
            '<li class="page-first' + (1 === this.options.pageNumber ? ' active' : '') + '">',
            '<a href="javascript:void(0)">',
            1,
            '</a>',
            '</li>'
          );

          from++;
        }

        if (this.options.pageNumber >= 4) {
          if (this.options.pageNumber == 4 || this.totalPages == 6 || this.totalPages == 7) {
            from--;
          } else {
            html.push('<li class="page-first-separator disabled">', '<a href="javascript:void(0)">...</a>', '</li>');
          }

          to--;
        }
      }

      if (this.totalPages >= 7) {
        if (this.options.pageNumber >= this.totalPages - 2) {
          from--;
        }
      }

      if (this.totalPages == 6) {
        if (this.options.pageNumber >= this.totalPages - 2) {
          to++;
        }
      } else if (this.totalPages >= 7) {
        if (this.totalPages == 7 || this.options.pageNumber >= this.totalPages - 3) {
          to++;
        }
      }

      for (i = from; i <= to; i++) {
        html.push(
          '<li class="page-number' + (i === this.options.pageNumber ? ' active' : '') + '">',
          '<a href="javascript:void(0)">',
          i,
          '</a>',
          '</li>'
        );
      }

      if (this.totalPages >= 8) {
        if (this.options.pageNumber <= this.totalPages - 4) {
          html.push('<li class="page-last-separator disabled">', '<a href="javascript:void(0)">...</a>', '</li>');
        }
      }

      if (this.totalPages >= 6) {
        if (this.options.pageNumber <= this.totalPages - 3) {
          html.push(
            '<li class="page-last page-number' + (this.totalPages === this.options.pageNumber ? ' active' : '') + '">',
            '<a href="javascript:void(0)">',
            this.totalPages,
            '</a>',
            '</li>'
          );
        }
      }
      if(this.totalPages == this.options.pageNumber){
        html.push('<li class="page-next disabled"><a href="javascript:void(0)">' + this.options.paginationNextText + '</a></li>');
      }else{
        html.push('<li class="page-next"><a href="javascript:void(0)">' + this.options.paginationNextText + '</a></li>');
      }

      if (this.options.jumpPage) {
        //允许跳页
        html.push(
          '<li><label style="font-weight: normal;margin-left: 10px;margin-right: 6px;">跳至</label>',
          '<input type="number" style="width:70px;" class="page-jump-number" min="1">',
          '<label style="font-weight: normal;margin-left: 6px;margin-right: 6px;">页</label>',
          '<button class="well-btn w-btn-primary w-btn-minor page-jump">跳转</button>',
          '</li>'
        );
      }

      html.push('</ul>', '</div>');
    }

    html.push(
      '<div class="pull-' + this.options.paginationDetailHAlign + ' pagination-detail">',
      '<span class="pagination-info">',
      this.options.onlyInfoPagination || this.options.hideTotalPageText ?
      this.options.formatDetailPagination(this.options.totalRows) :
      this.options.formatShowingRows(this.pageFrom, this.pageTo, this.options.totalRows, this.options.totalPages),
      '</span>'
    );

    this.$pagination.html(html.join(''));

    if (!this.options.onlyInfoPagination) {
      $pageList = this.$pagination.find('.page-list a');
      $first = this.$pagination.find('.page-first');
      $pre = this.$pagination.find('.page-pre');
      $next = this.$pagination.find('.page-next');
      $last = this.$pagination.find('.page-last');
      $number = this.$pagination.find('.page-number');
      $pageJump = this.$pagination.find('.page-jump');
      $jumpNumber = this.$pagination.find('.page-jump-number');

      if (this.options.smartDisplay) {
        if (this.totalPages <= 1) {
          this.$pagination.find('div.pagination').hide();
        }
        if (pageList.length < 2 || this.options.totalRows <= pageList[0]) {
          this.$pagination.find('span.page-list').hide();
        }

        // when data is empty, hide the pagination
        this.$pagination[this.getData().length ? 'show' : 'hide']();
      }

      if (this.options.hidePageList) {
        this.$pagination.find('span.page-list').hide();
      }

      if ($allSelected) {
        this.options.pageSize = this.options.formatAllRows();
      }
      $pageList.off('click').on('click', $.proxy(this.onPageListChange, this));
      $first.off('click').on('click', $.proxy(this.onPageFirst, this));
      $pre.off('click').on('click', $.proxy(this.onPagePre, this));
      $next.off('click').on('click', $.proxy(this.onPageNext, this));
      $last.off('click').on('click', $.proxy(this.onPageLast, this));
      $number.off('click').on('click', $.proxy(this.onPageNumber, this));
      $jumpNumber.off('keyup').on('keyup', $.proxy(this.onPageJumpNumber, this));
      if ($pageJump.length > 0) {
        //跳页事件
        $pageJump.off('click').on('click', $.proxy(this.onPageJump, this));
      }
    }

    this.fixedPagination();
  };

  BootstrapTable.prototype.updatePagination = function (event) {
    // Fix #171: IE disabled button can be clicked bug.
    if (event && $(event.currentTarget).hasClass('disabled')) {
      return;
    }

    if (!this.options.maintainSelected) {
      this.resetRows();
    }
    this.initPagination();
    if (this.options.sidePagination === 'server') {
      this.initServer();
    } else {
      this.initBody();
    }

    this.trigger('page-change', this.options.pageNumber, this.options.pageSize);
  };

  BootstrapTable.prototype.onPageListChange = function (event) {
    var $this = $(event.currentTarget);

    $this.parent().addClass('active').siblings().removeClass('active');
    this.options.pageSize =
      $this.text().toUpperCase() === this.options.formatAllRows().toUpperCase() ? this.options.formatAllRows() : +$this.text();
    this.$toolbar.find('.page-size').text(this.options.pageSize);

    this.updatePagination(event);
  };

  BootstrapTable.prototype.onPageFirst = function (event) {
    this.options.pageNumber = 1;
    this.updatePagination(event);
  };

  BootstrapTable.prototype.onPagePre = function (event) {
    if (this.options.pageNumber - 1 === 0) {
      this.options.pageNumber = this.options.totalPages;
    } else {
      this.options.pageNumber--;
    }
    this.updatePagination(event);
  };

  BootstrapTable.prototype.onPageNext = function (event) {
    if (this.options.pageNumber + 1 > this.options.totalPages) {
      this.options.pageNumber = 1;
    } else {
      this.options.pageNumber++;
    }
    this.updatePagination(event);
  };

  BootstrapTable.prototype.onPageLast = function (event) {
    this.options.pageNumber = this.totalPages;
    this.updatePagination(event);
  };

  BootstrapTable.prototype.onPageJump = function (event) {
    var pagenum = $.trim(this.$pagination.find('.page-jump-number').val());
    if (/^[1-9]\d*$/.test(pagenum)) {
      this.options.pageNumber = parseInt(pagenum);
      this.updatePagination(event);
    }
  };

  BootstrapTable.prototype.onPageNumber = function (event) {
    if (this.options.pageNumber === +$(event.currentTarget).text()) {
      return;
    }
    this.options.pageNumber = +$(event.currentTarget).text();
    this.updatePagination(event);
  };

  BootstrapTable.prototype.onPageJumpNumber = function (event) {
    var val = $(event.currentTarget).val();
    var reg = /^[0-9]+$/;
    if (!reg.test(val) || val < 1) {
      $(event.currentTarget).val("");
    }
  };

  BootstrapTable.prototype.initBody = function (fixedScroll, type) {
    var that = this,
      html = [],
      data = this.getData();
    var $detachBody = null;
    var updateById = that.options.updateById && !!that.options.uniqueId;
    this.trigger('pre-body', data);

    this.$body = this.$el.find('>tbody');
    if (!this.$body.length) {
      this.$body = $('<tbody></tbody>').appendTo(this.$el);
    }
    if (updateById) {
      $detachBody = that.$body.detach();
      this.$body = $('<tbody></tbody>').appendTo(this.$el).hide();
      that.showLoading();
    }

    //Fix #389 Bootstrap-table-flatJSON is not working

    if (!this.options.pagination || this.options.sidePagination === 'server') {
      this.pageFrom = 1;
      this.pageTo = data.length;
    }
    for (var i = this.pageFrom - 1; i < this.pageTo; i++) {
      var key,
        item = data[i],
        style = {},
        csses = [],
        data_ = '',
        attributes = {},
        htmlAttributes = [];
      style = calculateObjectValue(this.options, this.options.rowStyle, [item, i], style);

      if (style && style.css) {
        for (key in style.css) {
          csses.push(key + ': ' + style.css[key]);
        }
      }

      attributes = calculateObjectValue(this.options, this.options.rowAttributes, [item, i], attributes);

      if (attributes) {
        for (key in attributes) {
          htmlAttributes.push(sprintf('%s="%s"', key, escapeHTML(attributes[key])));
        }
      }

      if (item._data && !$.isEmptyObject(item._data)) {
        $.each(item._data, function (k, v) {
          // ignore data-index
          if (k === 'index') {
            return;
          }
          data_ += sprintf(' data-%s="%s"', k, v);
        });
      }

      html.push(
        '<tr',
        sprintf(' %s', htmlAttributes.join(' ')),
        sprintf(' id="%s"', $.isArray(item) ? undefined : item._id),
        sprintf(' class="%s"', style.classes || ($.isArray(item) ? undefined : item._class)),
        sprintf(' data-index="%s"', i),
        sprintf(' data-uniqueid="%s"', item[this.options.uniqueId]),
        sprintf('%s', data_),
        '>'
      );

      if (this.options.cardView) {
        html.push(sprintf('<td colspan="%s"><div class="card-views">', this.header.fields.length));
      }

      if (!this.options.cardView && this.options.detailView) {
        html.push(
          '<td>',
          '<a class="detail-icon" href="javascript:">',
          sprintf('<i class="%s %s"></i>', this.options.iconsPrefix, this.options.icons.detailOpen),
          '</a>',
          '</td>'
        );
      }

      $.each(this.header.fields, function (j, field) {
        var text = '',
          value = getItemField(item, field, that.options.escape),
          type = '',
          cellStyle = {},
          id_ = '',
          class_ = that.header.classes[j],
          data_ = sprintf('data-field="%s"', field),
          rowspan_ = '',
          colspan_ = '',
          title_ = '',
          column = that.columns[j];
        if (that.fromHtml && typeof value === 'undefined') {
          return;
        }

        if (!column.visible && that.options.hideInvisible) {
          // 隐藏不可见,用于创建表单控件
          that.header.styles[j] = 'display:none;';
        } else if (!column.visible) {
          return;
        }

        if (that.options.cardView && !column.cardVisible) {
          return;
        }

        style = sprintf('style="%s"', csses.concat(that.header.styles[j]).join('; '));

        // handle td's id and class
        if (item['_' + field + '_id']) {
          id_ = sprintf(' id="%s"', item['_' + field + '_id']);
        }
        if (item['_' + field + '_class']) {
          class_ = sprintf(' class="%s"', item['_' + field + '_class']);
        }
        if (item['_' + field + '_rowspan']) {
          rowspan_ = sprintf(' rowspan="%s"', item['_' + field + '_rowspan']);
        }
        if (item['_' + field + '_colspan']) {
          colspan_ = sprintf(' colspan="%s"', item['_' + field + '_colspan']);
        }
        if (item['_' + field + '_title']) {
          var escapeTitle = (item['_' + field + '_title'] || '').replace(/\</g, '&lt;').replace(/\>/g, '&gt;').replace(/\"/g, "'");

          title_ = sprintf(' title="%s"', escapeTitle);
        }
        cellStyle = calculateObjectValue(that.header, that.header.cellStyles[j], [value, item, i, field], cellStyle);
        if (cellStyle.classes) {
          class_ = sprintf(' class="%s"', cellStyle.classes);
        }

        if (column.className) {
          class_ = sprintf(' class="%s"', column.className);
        }
        if (cellStyle.css) {
          var csses_ = [];
          for (var key in cellStyle.css) {
            csses_.push(key + ': ' + cellStyle.css[key]);
          }
          style = sprintf('style="%s"', csses_.concat(that.header.styles[j]).join('; '));
        }

        value = calculateObjectValue(column, that.header.formatters[j], [value, item, i], value);

        if (item['_' + field + '_data'] && !$.isEmptyObject(item['_' + field + '_data'])) {
          $.each(item['_' + field + '_data'], function (k, v) {
            // ignore data-index
            if (k === 'index') {
              return;
            }
            data_ += sprintf(' data-%s="%s"', k, v);
          });
        }
        if (column.checkbox || column.radio) {
          type = column.checkbox ? 'checkbox' : type;
          type = column.radio ? 'radio' : type;

          text = [
            sprintf(that.options.cardView ? '<div class="card-view %s">' : '<td class="bs-checkbox %s" %s>', column['class'] || '', style),
            '<input',
            sprintf(' data-index="%s"', i) +
            sprintf(' name="%s"', that.options.selectItemName) +
            sprintf(' type="%s"', type) +
            sprintf(' value="%s"', item[that.options.idField]) +
            sprintf(' checked="%s"', value === true || (value && value.checked) ? 'checked' : undefined) +
            sprintf(' disabled="%s"', !column.checkboxEnabled || (value && value.disabled) ? 'disabled' : undefined) +
            ' />',
            that.options.clickToSelect ? '<label></label>' : '<label onclick="$(this).prev().click();"></label>',
            that.header.formatters[j] && typeof value === 'string' ? value : '',
            that.options.cardView ? '</div>' : '</td>'
          ].join('');

          item[that.header.stateField] = value === true || (value && value.checked);
        } else {
          value = typeof value === 'undefined' || value === null ? that.options.undefinedText : value;

          if (!title_) {
            var tit = typeof column.titleTooltip === 'string' ? column.titleTooltip : value;

            // 去除html标签
            var escapeTitle = (tit + '' || '').replace(/<[^>]*>|/g, '');
            escapeTitle = escapeTitle.replace(/\</g, '&lt;').replace(/\>/g, '&gt;').replace(/\"/g, "'");

            title_ = sprintf(' title="%s"', escapeTitle);
          }
          text = that.options.cardView ? [
            '<div class="card-view">',
            that.options.showHeader ?
            sprintf('<span class="title" %s>%s</span>', style, getPropertyFromOther(that.columns, 'field', 'title', field)) :
            '',
            sprintf('<span class="value">%s</span>', value),
            '</div>'
          ].join('') : [sprintf('<td %s %s %s %s %s %s %s>', id_, class_, style, data_, rowspan_, colspan_, title_), value, '</td>'].join('');

          // Hide empty data on Card view when smartDisplay is set to true.
          if (that.options.cardView && that.options.smartDisplay && value === '') {
            // Should set a placeholder for event binding correct fieldIndex
            text = '<div class="card-view"></div>';
          }
        }

        html.push(text);
      });

      if (this.options.cardView) {
        html.push('</div></td>');
      }

      html.push('</tr>');
      if (updateById) {
        var id = item[that.options.uniqueId];
        var $tr = $detachBody.find('>tr[data-uniqueid="' + id + '"]'),
          $tr2 = $(html.join(''));
        if ($tr.length > 0) {
          $tr
            .attr('data-index', i)
            .data('index', i)
            .find('td.bs-seqno')
            .text(i + 1);
          $tr.find('td.bs-checkbox>input[data-index]').attr('data-index', i).data('index', i);
          if (item._data && !$.isEmptyObject(item._data)) {
            $.each(item._data, function (k, v) {
              // ignore data-index
              if (k === 'index') {
                return;
              }
              $tr.attr('data-' + k, v).data(k, v);
            });
          }
          $tr2.find('>td').each(function (idx, element) {
            var $element2 = $tr.find('>td:eq(' + idx + ')'),
              $element = $(element);
            var klass = $element.attr('class');
            var style = $element.attr('style');
            var colspan = $element.attr('colspan');
            klass && $element2.attr('class', klass);
            style && $element2.attr('style', style);
            colspan && $element2.attr('colspan', colspan);
          });
          if (that.updateRowData) {
            that.updateRowData(i, id, item, $tr, that.$body);
          }
        } else {
          $tr = $tr2;
          $tr2.find('>td').each(function (idx, element) {
            var $element = $(element);
            var _width = element.style.width;
            if (_width) {
              if (_width.indexOf('%') > -1) {
                _width = '100%';
              }
              $element.find('a').css('max-width', _width);
            }
          });
        }
        that.$body.append($tr);
        // 清空html
        html.length = 0;
      }
    }

    // show no records
    if (!html.length) {
      html.push(
        '<tr class="no-records-found">',
        sprintf(
          '<td colspan="%s"><div class="well-table-no-data"></div><span>%s</span></td>',
          this.$header.find('th').length,
          this.options.formatNoMatches() || '暂无数据'
        ),
        '</tr>'
      );
      setTimeout(function () {
        if (that.data.length) {
          that.$tableBody.scrollLeft(0);
        } else {
          that.$tableBody.scrollLeft((that.$el.width() - that.options.tableOuterTdWidth) / 2);
        }
      }, 0);
    }
    if (updateById) {
      that.hideLoading();
      // setTimeout(function () {
      //   that.hideLoading();
      // }, 600);
      if (that.$body.is(':empty')) {
        that.$body.html(html.join(''));
      }
      that.$body.show();
    } else {
      that.$body.html(html.join(''));
    }

    if (!fixedScroll) {
      this.scrollTo(0);
    }

    // click to select by column
    this.$body
      .find('> tr[data-index] > td')
      .off('click dblclick')
      .on('click dblclick', function (e) {
        var $td = $(this),
          $tr = $td.parent(),
          item = that.data[$tr.data('index')],
          index = $td[0].cellIndex,
          fields = that.getVisibleFields(),
          field = $td.attr('data-field') || fields[that.options.detailView && !that.options.cardView ? index - 1 : index],
          column = that.columns[getFieldIndex(that.columns, field)],
          value = getItemField(item, field, that.options.escape);

        if ($td.find('.detail-icon').length) {
          return;
        }

        that.trigger(e.type === 'click' ? 'click-cell' : 'dbl-click-cell', field, value, item, $td, e);
        that.trigger(e.type === 'click' ? 'click-row' : 'dbl-click-row', item, $tr, field, e);

        // if click to select - then trigger the checkbox/radio click
        if (e.type === 'click' && that.options.clickToSelect && column.clickToSelect) {
          var $selectItem = $tr.find(sprintf('[name="%s"]', that.options.selectItemName));
          if ($selectItem.length) {
            $selectItem[0].click(); // #144: .trigger('click') bug
          }
        }
      });

    this.$body
      .find('> tr[data-index] > td > .detail-icon')
      .off('click')
      .on('click', function () {
        var $this = $(this),
          $tr = $this.parent().parent(),
          index = $tr.data('index'),
          row = data[index]; // Fix #980 Detail view, when searching, returns wrong row

        // remove and update
        if ($tr.next().is('tr.detail-view')) {
          $this.find('i').attr('class', sprintf('%s %s', that.options.iconsPrefix, that.options.icons.detailOpen));
          $tr.next().remove();
          that.trigger('collapse-row', index, row);
        } else {
          $this.find('i').attr('class', sprintf('%s %s', that.options.iconsPrefix, that.options.icons.detailClose));
          $tr.after(sprintf('<tr class="detail-view"><td colspan="%s"></td></tr>', $tr.find('td').length));
          var $element = $tr.next().find('td');
          var content = calculateObjectValue(that.options, that.options.detailFormatter, [index, row, $element], '');
          if ($element.length === 1) {
            $element.append(content);
          }
          that.trigger('expand-row', index, row, $element);
        }
        that.resetView();
      });

    this.$selectItem = this.$body.find(sprintf('[name="%s"]', this.options.selectItemName));
    this.$selectItem.off('change').on('change', function (event) {
      event.stopImmediatePropagation();

      var $this = $(this),
        curIndex = $this.data('index'),
        checked = $this.prop('checked'),
        row = that.data[$this.data('index')];

      if (that.options.maintainSelected && $(this).is(':radio')) {
        $.each(that.options.data, function (i, row) {
          row[that.header.stateField] = false;
        });
      }

      row[that.header.stateField] = checked;

      if (that.options.singleSelect) {
        that.$selectItem.each(function () {
          if ($(this).data('index') == curIndex) {
            that.data[$(this).data('index')][that.header.stateField] = checked;
          } else {
            that.data[$(this).data('index')][that.header.stateField] = false;
          }

        });
        that.$selectItem.filter(':checked').not(this).prop('checked', false);
      }

      that.updateSelected();
      that.trigger(checked ? 'check' : 'uncheck', row, $this);
    });

    $.each(this.header.events, function (i, events) {
      if (!events) {
        return;
      }
      // fix bug, if events is defined with namespace
      if (typeof events === 'string') {
        events = calculateObjectValue(null, events);
      }

      var field = that.header.fields[i],
        fieldIndex = $.inArray(field, that.getVisibleFields());

      if (that.options.detailView && !that.options.cardView) {
        fieldIndex += 1;
      }

      for (var key in events) {
        that.$body.find('>tr:not(.no-records-found)').each(function () {
          var $tr = $(this),
            $td = $tr.find(that.options.cardView ? '.card-view' : 'td').eq(fieldIndex),
            index = key.indexOf(' '),
            name = key.substring(0, index),
            el = key.substring(index + 1),
            func = events[key];

          $td
            .find(el)
            .off(name)
            .on(name, function (e) {
              var index = $tr.data('index'),
                row = that.data[index],
                value = row[field];

              func.apply(this, [e, value, row, index]);
            });
        });
      }
    });

    this.updateSelected();
    this.resetView();

    this.trigger('post-body', data);
  };

  BootstrapTable.prototype.initServer = function (silent, query, url) {
    var that = this,
      data = {},
      params = {
        searchText: this.searchText,
        sortName: this.options.sortName,
        sortOrder: this.options.sortOrder
      },
      request;

    if (this.options.pagination) {
      params.pageSize = this.options.pageSize === this.options.formatAllRows() ? this.options.totalRows : this.options.pageSize;
      params.pageNumber = this.options.pageNumber;
    }

    if (!(url || this.options.url) && !this.options.ajax) {
      return;
    }

    if (this.options.queryParamsType === 'limit') {
      params = {
        search: params.searchText,
        sort: params.sortName,
        order: params.sortOrder
      };

      if (this.options.pagination) {
        params.offset = this.options.pageSize === this.options.formatAllRows() ? 0 : this.options.pageSize * (this.options.pageNumber - 1);
        params.limit = this.options.pageSize === this.options.formatAllRows() ? this.options.totalRows : this.options.pageSize;
      }
    }

    if (!$.isEmptyObject(this.filterColumnsPartial)) {
      params.filter = JSON.stringify(this.filterColumnsPartial, null);
    }

    data = calculateObjectValue(this.options, this.options.queryParams, [params], data);

    $.extend(data, query || {});

    // false to stop request
    if (data === false) {
      return;
    }
    if (!silent) {
      $('.div_lineEnd_toolbar', this.$tableContainer).remove();
      this.$tableLoading.removeClass('display-none');
      $('.fixed-table-body-columns', this.$tableContainer).remove();
      $('.fixed-table-body-columns-right', this.$tableContainer).remove();
    }
    this.$container.addClass('table-loading');
    request = $.extend({}, calculateObjectValue(null, this.options.ajaxOptions), {
      type: this.options.method,
      url: url || this.options.url,
      data: this.options.contentType === 'application/json' && this.options.method === 'post' ? JSON.stringify(data) : data,
      cache: this.options.cache,
      contentType: this.options.contentType,
      dataType: this.options.dataType,
      success: function (res) {
        res = calculateObjectValue(that.options, that.options.responseHandler, [res], res);

        that.load(res);
        that.trigger('load-success', res);
        if (!silent) {
          that.$tableLoading.addClass('display-none');
        }
        that.$container.removeClass('table-loading');
      },
      error: function (res) {
        that.trigger('load-error', res.status, res);
        if (!silent) {
          that.$tableLoading.addClass('display-none');
        }
        that.$container.removeClass('table-loading');
      }
    });

    if (this.options.ajax) {
      calculateObjectValue(this, this.options.ajax, [request], null);
    } else {
      if (this._xhr && this._xhr.readyState !== 4) {
        this._xhr.abort();
      }
      this._xhr = $.ajax(request);
    }
  };

  BootstrapTable.prototype.initSearchText = function () {
    if (this.options.search) {
      if (this.options.searchText !== '') {
        var $search = this.$toolbar.find('.search input');
        $search.val(this.options.searchText);
        this.onSearch({
          currentTarget: $search
        });
      }
    }
  };

  BootstrapTable.prototype.getCaret = function () {
    var that = this;

    $.each(this.$header.find('th'), function (i, th) {
      $(th)
        .find('.sortable')
        .removeClass('desc asc')
        .addClass($(th).data('field') === that.options.sortName ? that.options.sortOrder : 'both');
    });
  };

  BootstrapTable.prototype.updateSelected = function () {
    if (!this.$selectItem) {
      return;
    }
    var checkAll =
      this.$selectItem.filter(':enabled').length &&
      this.$selectItem.filter(':enabled').length === this.$selectItem.filter(':enabled').filter(':checked').length;

    this.$selectAll.add(this.$selectAll_).prop('checked', checkAll);
    var data = this.data;
    this.$selectItem.each(function () {
      var index = $(this).closest('tr').data("index");
      var row= data[index];
      var isChecked = $(this).prop('checked') || (row?row["rowCheckItem"]:false);
      $(this).closest('tr')[isChecked ? 'addClass' : 'removeClass']('selected');
    });
  };

  BootstrapTable.prototype.updateRows = function () {
    var that = this;

    this.$selectItem.each(function () {
      that.data[$(this).data('index')][that.header.stateField] = $(this).prop('checked');
    });
  };

  BootstrapTable.prototype.resetRows = function () {
    var that = this;

    $.each(this.data, function (i, row) {
      that.$selectAll.prop('checked', false);
      that.$selectItem.prop('checked', false);
      if (that.header.stateField) {
        row[that.header.stateField] = false;
      }
    });
  };

  BootstrapTable.prototype.trigger = function (name) {
    var args = Array.prototype.slice.call(arguments, 1);

    name += '.bs.table';
    this.options[BootstrapTable.EVENTS[name]].apply(this.options, args);
    this.$el.trigger($.Event(name), args);

    this.options.onAll(name, args);
    this.$el.trigger($.Event('all.bs.table'), [name, args]);
  };

  BootstrapTable.prototype.resetHeader = function () {
    // fix #61: the hidden table reset header bug.
    // fix bug: get $el.css('width') error sometime (height = 500)
    var that = this;
    clearTimeout(that.timeoutId_);
    that.timeoutId_ = setTimeout($.proxy(that.fitHeader, that), 1000 / 24);
  };

  BootstrapTable.prototype.fitHeader = function () {
    var that = this,
      fixedBody,
      scrollWidth,
      focused,
      focusedTemp;
    var scrollBarWidth = getScrollBarWidth();
    if (that.$el.is(':hidden')) {
      that.timeoutId_ = setTimeout($.proxy(that.fitHeader, that), 100);
      return;
    }
    fixedBody = this.$tableBody.get(0);

    scrollWidth =
      fixedBody.scrollWidth - fixedBody.clientWidth > scrollBarWidth / 2 ||
      fixedBody.scrollHeight > fixedBody.clientHeight + this.$header.outerHeight() ?
      scrollBarWidth :
      0;
    this.$el.css('margin-top', -this.$header.outerHeight());

    focused = $(':focus');
    if (focused.length > 0) {
      var $th = focused.parents('th');
      if ($th.length > 0) {
        var dataField = $th.attr('data-field');
        if (dataField !== undefined) {
          var $headerTh = this.$header.find("[data-field='" + dataField + "']");
          if ($headerTh.length > 0) {
            $headerTh.find(':input').addClass('focus-temp');
          }
        }
      }
    }

    this.$header_ = this.$header.clone(true, true);
    this.$selectAll_ = this.$header_.find('[name="btSelectAll"]');
    this.$tableHeader
      .css({
        'margin-right': scrollWidth
      })
      .find('table')
      .html('')
      .attr('class', this.$el.attr('class'))
      .append(this.$header_);
    setTimeout(function () {
      that.$tableHeader.scrollLeft(that.$tableBody.scrollLeft());
    }, 10);

    focusedTemp = $('.focus-temp:visible:eq(0)');
    if (focusedTemp.length > 0) {
      focusedTemp.focus();
      this.$header.find('.focus-temp').removeClass('focus-temp');
    }

    // fix bug: $.data() is not working as expected after $.append()
    this.$header.find('th[data-field]').each(function (i) {
      that.$header_.find(sprintf('th[data-field="%s"]', $(this).data('field'))).data($(this).data());
    });

    var visibleFields = this.getVisibleFields(),
      $ths = this.$header_.find('th');

    function syncHeaderWidth(t) {
      that.$header.find('>tr>th').each(function (i) {
        var $this = $(this);
        var field = $this.attr('data-field');
        var columnOption;
        $.each(that.options.columns[0], function (i, item) {
          // bug#59691
          if (item.field == field) {
            columnOption = item;
            return false;
          }
        });

        var _configWidth = columnOption ? columnOption.width || '' : '';
        if (that.options.horizontalScroll && columnOption.controlable && !_configWidth) {
          _configWidth = 300;
        }
        _configWidth = _configWidth.toString();
        var $th = that.$header_.find(sprintf('th:eq(%s)', i));
        var originalBodyColumnWidth = +$this[0].getBoundingClientRect().width.toFixed(2);
        $th.css('width', _configWidth || originalBodyColumnWidth + 'px');
        _configWidth = typeof _configWidth === 'string' ? _configWidth : _configWidth.toString();
        var innerStyle = _configWidth.indexOf('%') > -1 ? '100%' : _configWidth || '';
        $th.find('.th-inner').css('width', innerStyle);
        $th.find('.th-inner').css('max-width', (innerStyle + 'px') || '100%');
        var $thInBody = that.$header.find(sprintf('th:eq(%s)', i));
        $thInBody.css('width', _configWidth || originalBodyColumnWidth + 'px');
        $thInBody.find('.th-inner').css('width', innerStyle);
        $thInBody.find('.th-inner').css('max-width', (innerStyle + 'px') || '100%');
      });
      // horizontal scroll event
      // TODO: it's probably better improving the layout than binding to scroll event
      that.$tableBody.off('scroll').on('scroll', function () {
        that.$tableHeader.scrollLeft($(this).scrollLeft());

        if (that.options.showFooter && !that.options.cardView) {
          that.$tableFooter.scrollLeft($(this).scrollLeft());
        }
      });

      that.trigger('post-header');
    }
    if (that.options.syncHeaderWidth === 'async') {
      clearTimeout(that.timeoutId2_);
      that.timeoutId2_ = setTimeout(syncHeaderWidth, 0);
    } else {
      syncHeaderWidth();
    }
  };

  BootstrapTable.prototype.resetFooter = function () {
    var that = this,
      data = that.getData(),
      html = [];

    if (!this.options.showFooter || this.options.cardView) {
      //do nothing
      return;
    }

    if (!this.options.cardView && this.options.detailView) {
      html.push('<td><div class="th-inner">&nbsp;</div><div class="fht-cell"></div></td>');
    }

    $.each(this.columns, function (i, column) {
      var key,
        falign = '', // footer align style
        valign = '',
        csses = [],
        style = {},
        class_ = sprintf(' class="%s"', column['class']);

      if (!column.visible) {
        return;
      }

      if (that.options.cardView && !column.cardVisible) {
        return;
      }

      falign = sprintf('text-align: %s; ', column.falign ? column.falign : column.align);
      valign = sprintf('vertical-align: %s; ', column.valign);

      style = calculateObjectValue(null, that.options.footerStyle);

      if (style && style.css) {
        for (key in style.css) {
          csses.push(key + ': ' + style.css[key]);
        }
      }

      html.push('<td', class_, sprintf(' style="%s"', falign + valign + csses.concat().join('; ')), '>');
      html.push('<div class="th-inner">');

      html.push(calculateObjectValue(column, column.footerFormatter, [data], '&nbsp;') || '&nbsp;');

      html.push('</div>');
      html.push('<div class="fht-cell"></div>');
      html.push('</div>');
      html.push('</td>');
    });

    this.$tableFooter.find('tr').html(html.join(''));
    this.$tableFooter.show();
    clearTimeout(this.timeoutFooter_);
    this.timeoutFooter_ = setTimeout($.proxy(this.fitFooter, this), this.$el.is(':hidden') ? 100 : 0);
  };

  BootstrapTable.prototype.fitFooter = function () {
    var that = this,
      $footerTd,
      elWidth,
      scrollWidth;

    clearTimeout(this.timeoutFooter_);
    if (this.$el.is(':hidden')) {
      this.timeoutFooter_ = setTimeout($.proxy(this.fitFooter, this), 100);
      return;
    }

    elWidth = this.$el.css('width');
    scrollWidth = elWidth > this.$tableBody.width() ? getScrollBarWidth() : 0;

    this.$tableFooter
      .css({
        'margin-right': scrollWidth
      })
      .find('table')
      .css('width', elWidth)
      .attr('class', this.$el.attr('class'));
    $footerTd = this.$tableFooter.find('td');

    this.$body.find('>tr:first-child:not(.no-records-found) > *').each(function (i) {
      var $this = $(this);

      // $footerTd.eq(i).find('.fht-cell').width($this.innerWidth());
      $footerTd.eq(i).width($this.innerWidth());
    });
  };

  BootstrapTable.prototype.toggleColumn = function (index, checked, needUpdate) {
    if (index === -1) {
      return;
    }
    this.columns[index].visible = checked;
    this.initHeader();
    this.initSearch();
    this.initPagination();
    this.initBody();

    if (this.options.showColumns) {
      var $items = this.$toolbar.find('.keep-open input').prop('disabled', false);

      if (needUpdate) {
        $items.filter(sprintf('[value="%s"]', index)).prop('checked', checked);
      }

      if ($items.filter(':checked').length <= this.options.minimumCountColumns) {
        $items.filter(':checked').prop('disabled', true);
      }
    }
  };

  BootstrapTable.prototype.toggleRow = function (index, uniqueId, visible) {
    if (index === -1) {
      return;
    }

    this.$body
      .find(typeof index !== 'undefined' ? sprintf('tr[data-index="%s"]', index) : sprintf('tr[data-uniqueid="%s"]', uniqueId))[visible ? 'show' : 'hide']();
  };

  BootstrapTable.prototype.getVisibleFields = function () {
    var that = this,
      visibleFields = [];

    $.each(this.header.fields, function (j, field) {
      var column = that.columns[getFieldIndex(that.columns, field)];

      if (!column.visible) {
        return;
      }
      visibleFields.push(field);
    });
    return visibleFields;
  };

  // PUBLIC FUNCTION DEFINITION
  // =======================

  BootstrapTable.prototype.resetView = function (params) {
    var padding = 0;

    if (params && params.height) {
      this.options.height = params.height;
      if (params.maxHeight) {
        this.options.maxHeight = params.maxHeight;
      }
    }

    this.$selectAll.prop('checked', this.$selectItem.length > 0 && this.$selectItem.length === this.$selectItem.filter(':checked').length);

    if (this.options.height) {
      var toolbarHeight = getRealHeight(this.$toolbar),
        paginationHeight = getRealHeight(this.$pagination),
        height = this.options.height - toolbarHeight - paginationHeight;

      if (this.options.maxHeight) {
        this.$tableBody.css('max-height', this.options.maxHeight + 'px');
      } else {
        this.$tableContainer.css('height', height + 'px');
        var tableBodyheight = height - 42;
        this.$tableBody.css('height', tableBodyheight + 'px');
      }
    }

    if (this.options.cardView) {
      // remove the element css
      this.$el.css('margin-top', '0');
      this.$tableContainer.css('padding-bottom', '0');
      this.$tableFooter.hide();
      return;
    }

    if (this.options.showHeader && this.options.height) {
      this.$tableHeader.show();
      this.resetHeader();
      padding += this.$header.outerHeight();
    } else {
      this.$tableHeader.hide();
      this.trigger('post-header');
    }

    if (this.options.showFooter) {
      this.resetFooter();
      if (this.options.height) {
        padding += this.$tableFooter.outerHeight() + 1;
      }
    }

    // Assign the correct sortable arrow
    this.getCaret();
    // this.$tableContainer.css('padding-bottom', padding + 'px');

    // 表格容器body的横向滚动条，超过一点偏移量才出现
    // 在没有使用固定列的时候生效
    if (!this.__proto__.hasOwnProperty('fixedColumns')) {
      this.$tableBody.css('overflow-x', 'auto');
    }

    // 设置固定列的表格列宽
    if (this.options.fixedColumns) {
      var tableWidth = this.$tableBody.find('table:first').width();
      var width = this.$tableBody.closest('.fixed-table-body').width();
      console.log('tableWidth=', tableWidth);
      console.log('width=', width);
      var width = this.$tableBody.parents('.fixed-table-container').width();
      // this.$tableBody.parent('.fixed-table-container').find('.fixed-table-body-columns-right').find('table').width(width);
      // this.$tableBody.parent('.fixed-table-container').find('.fixed-table-body-columns').find('table').width(width);
      var tableWidth = tableWidth > width ? tableWidth : width; //解决第一个tab内容展示不完整的问题。
      this.$tableBody.find('table').width(tableWidth);
    }

    if (this.$el.find('.no-records-found').length) {
      this.$el.closest('.bootstrap-table').addClass('table-no-records');
    } else {
      this.$el.closest('.bootstrap-table').removeClass('table-no-records');
    }

    // this.trigger('reset-view');
  };

  BootstrapTable.prototype.getData = function (useCurrentPage) {
    return this.searchText || !$.isEmptyObject(this.filterColumns) || !$.isEmptyObject(this.filterColumnsPartial) ?
      useCurrentPage ?
      this.data.slice(this.pageFrom - 1, this.pageTo) :
      this.data :
      useCurrentPage ?
      this.options.data.slice(this.pageFrom - 1, this.pageTo) :
      this.options.data;
  };

  BootstrapTable.prototype.load = function (data) {
    var fixedScroll = false;

    // #431: support pagination
    if (this.options.sidePagination === 'server') {
      this.options.totalRows = data.total;
      fixedScroll = data.fixedScroll;
      data = data[this.options.dataField];
    } else if (!$.isArray(data)) {
      // support fixedScroll
      fixedScroll = data.fixedScroll;
      data = data.data;
    }

    this.initData(data);
    this.initSearch();
    this.initPagination();
    this.initBody(fixedScroll);
  };

  BootstrapTable.prototype.append = function (data) {
    this.initData(data, 'append');
    this.initSearch();
    this.initPagination();
    this.initSort();
    this.initBody(true, 'append');
  };

  BootstrapTable.prototype.prepend = function (data) {
    this.initData(data, 'prepend');
    this.initSearch();
    this.initPagination();
    this.initSort();
    this.initBody(true);
  };

  BootstrapTable.prototype.remove = function (params) {
    var len = this.options.data.length,
      i,
      row;

    if (!params.hasOwnProperty('field') || !params.hasOwnProperty('values')) {
      return;
    }

    for (i = len - 1; i >= 0; i--) {
      row = this.options.data[i];

      if (!row.hasOwnProperty(params.field)) {
        continue;
      }
      if ($.inArray(row[params.field], params.values) !== -1) {
        this.options.data.splice(i, 1);
      }
    }

    if (len === this.options.data.length) {
      return;
    }

    this.initSearch();
    this.initPagination();
    this.initSort();
    this.initBody(true);
  };

  BootstrapTable.prototype.removeAll = function () {
    if (this.options.data.length > 0) {
      this.options.data.splice(0, this.options.data.length);
      this.initSearch();
      this.initPagination();
      this.initBody(true);
    }
  };

  BootstrapTable.prototype.getRowByUniqueId = function (id) {
    var uniqueId = this.options.uniqueId,
      len = this.options.data.length,
      dataRow = null,
      i,
      row,
      rowUniqueId;

    for (i = len - 1; i >= 0; i--) {
      row = this.options.data[i];

      if (row.hasOwnProperty(uniqueId)) {
        // uniqueId is a column
        rowUniqueId = row[uniqueId];
      } else if (row._data.hasOwnProperty(uniqueId)) {
        // uniqueId is a row data property
        rowUniqueId = row._data[uniqueId];
      } else {
        continue;
      }

      if (typeof rowUniqueId === 'string') {
        id = id.toString();
      } else if (typeof rowUniqueId === 'number') {
        if (Number(rowUniqueId) === rowUniqueId && rowUniqueId % 1 === 0) {
          id = parseInt(id);
        } else if (rowUniqueId === Number(rowUniqueId) && rowUniqueId !== 0) {
          id = parseFloat(id);
        }
      }

      if (rowUniqueId === id) {
        dataRow = row;
        break;
      }
    }

    return dataRow;
  };

  BootstrapTable.prototype.removeByUniqueId = function (id) {
    var len = this.options.data.length,
      row = this.getRowByUniqueId(id);

    if (row) {
      this.options.data.splice(this.options.data.indexOf(row), 1);
    }

    if (len === this.options.data.length) {
      return;
    }

    this.initSearch();
    this.initPagination();
    this.initBody(true);
  };

  BootstrapTable.prototype.updateByUniqueId = function (params) {
    var that = this;
    var allParams = $.isArray(params) ? params : [params];

    $.each(allParams, function (i, params) {
      var rowId;

      if (!params.hasOwnProperty('id') || !params.hasOwnProperty('row')) {
        return;
      }

      rowId = $.inArray(that.getRowByUniqueId(params.id), that.options.data);

      if (rowId === -1) {
        return;
      }
      $.extend(that.options.data[rowId], params.row);
    });

    this.initSearch();
    this.initSort();
    this.initBody(true);
  };

  BootstrapTable.prototype.insertRow = function (params) {
    if (!params.hasOwnProperty('index') || !params.hasOwnProperty('row')) {
      return;
    }
    this.data.splice(params.index, 0, params.row);
    this.initSearch();
    this.initPagination();
    this.initSort();
    this.initBody(true);
  };

  BootstrapTable.prototype.updateRow = function (params) {
    var that = this;
    var allParams = $.isArray(params) ? params : [params];

    $.each(allParams, function (i, params) {
      if (!params.hasOwnProperty('index') || !params.hasOwnProperty('row')) {
        return;
      }
      $.extend(that.options.data[params.index], params.row);
    });

    this.initSearch();
    this.initSort();
    this.initBody(true);
  };

  BootstrapTable.prototype.showRow = function (params) {
    if (!params.hasOwnProperty('index') && !params.hasOwnProperty('uniqueId')) {
      return;
    }
    this.toggleRow(params.index, params.uniqueId, true);
  };

  BootstrapTable.prototype.hideRow = function (params) {
    if (!params.hasOwnProperty('index') && !params.hasOwnProperty('uniqueId')) {
      return;
    }
    this.toggleRow(params.index, params.uniqueId, false);
  };

  BootstrapTable.prototype.getRowsHidden = function (show) {
    var rows = $(this.$body[0]).children().filter(':hidden'),
      i = 0;
    if (show) {
      for (; i < rows.length; i++) {
        $(rows[i]).show();
      }
    }
    return rows;
  };

  BootstrapTable.prototype.mergeCells = function (options) {
    var row = options.index,
      col = $.inArray(options.field, this.getVisibleFields()),
      rowspan = options.rowspan || 1,
      colspan = options.colspan || 1,
      i,
      j,
      $tr = this.$body.find('>tr'),
      $td;

    if (this.options.detailView && !this.options.cardView) {
      col += 1;
    }

    $td = $tr.eq(row).find('>td').eq(col);

    if (row < 0 || col < 0 || row >= this.data.length) {
      return;
    }

    for (i = row; i < row + rowspan; i++) {
      for (j = col; j < col + colspan; j++) {
        $tr.eq(i).find('>td').eq(j).hide();
      }
    }

    $td.attr('rowspan', rowspan).attr('colspan', colspan).show();
  };

  BootstrapTable.prototype.updateCell = function (params) {
    if (!params.hasOwnProperty('index') || !params.hasOwnProperty('field') || !params.hasOwnProperty('value')) {
      return;
    }
    this.data[params.index][params.field] = params.value;

    if (params.reinit === false) {
      return;
    }
    this.initSort();
    this.initBody(true);
  };

  BootstrapTable.prototype.getOptions = function () {
    return this.options;
  };

  BootstrapTable.prototype.getSelections = function () {
    var that = this;

    return $.grep(this.options.data, function (row) {
      return row[that.header.stateField];
    });
  };

  BootstrapTable.prototype.getAllSelections = function () {
    var that = this;

    return $.grep(this.options.data, function (row) {
      return row[that.header.stateField];
    });
  };

  BootstrapTable.prototype.checkAll = function () {
    this.checkAll_(true);
  };

  BootstrapTable.prototype.uncheckAll = function () {
    this.checkAll_(false);
  };

  BootstrapTable.prototype.checkInvert = function () {
    var that = this;
    var rows = that.$selectItem.filter(':enabled');
    var checked = rows.filter(':checked');
    rows.each(function () {
      $(this).prop('checked', !$(this).prop('checked'));
    });
    that.updateRows();
    that.updateSelected();
    that.trigger('uncheck-some', checked);
    checked = that.getSelections();
    that.trigger('check-some', checked);
  };

  BootstrapTable.prototype.checkAll_ = function (checked) {
    var rows;
    if (!checked) {
      rows = this.getSelections();
    }
    this.$selectAll.add(this.$selectAll_).prop('checked', checked);
    this.$selectItem.filter(':enabled').prop('checked', checked);
    this.updateRows();
    if (checked) {
      rows = this.getSelections();
    }
    this.trigger(checked ? 'check-all' : 'uncheck-all', rows);
  };

  BootstrapTable.prototype.check = function (index) {
    this.check_(true, index);
  };

  BootstrapTable.prototype.uncheck = function (index) {
    this.check_(false, index);
  };

  BootstrapTable.prototype.check_ = function (checked, index) {
    var $el = this.$selectItem.filter(sprintf('[data-index="%s"]', index)).prop('checked', checked);
    this.data[index][this.header.stateField] = checked;
    this.updateSelected();
    this.trigger(checked ? 'check' : 'uncheck', this.data[index], $el);
  };

  BootstrapTable.prototype.checkBy = function (obj) {
    this.checkBy_(true, obj);
  };

  BootstrapTable.prototype.uncheckBy = function (obj) {
    this.checkBy_(false, obj);
  };

  BootstrapTable.prototype.checkBy_ = function (checked, obj) {
    if (!obj.hasOwnProperty('field') || !obj.hasOwnProperty('values')) {
      return;
    }

    var that = this,
      rows = [];
    $.each(this.options.data, function (index, row) {
      if (!row.hasOwnProperty(obj.field)) {
        return false;
      }
      if ($.inArray(row[obj.field], obj.values) !== -1) {
        var $el = that.$selectItem.filter(':enabled').filter(sprintf('[data-index="%s"]', index)).prop('checked', checked);
        row[that.header.stateField] = checked;
        rows.push(row);
        that.trigger(checked ? 'check' : 'uncheck', row, $el);
      }
    });
    this.updateSelected();
    this.trigger(checked ? 'check-some' : 'uncheck-some', rows);
  };

  BootstrapTable.prototype.destroy = function () {
    this.$el.insertBefore(this.$container);
    $(this.options.toolbar).insertBefore(this.$el);
    this.$container.next().remove();
    this.$container.remove();
    this.$el
      .html(this.$el_.html())
      .css('margin-top', '0')
      .attr('class', this.$el_.attr('class') || ''); // reset the class
  };

  BootstrapTable.prototype.showLoading = function () {
    this.$tableLoading.removeClass('display-none');
  };

  BootstrapTable.prototype.hideLoading = function () {
    this.$tableLoading.addClass('display-none');
  };

  BootstrapTable.prototype.togglePagination = function () {
    this.options.pagination = !this.options.pagination;
    var button = this.$toolbar.find('button[name="paginationSwitch"] i');
    if (this.options.pagination) {
      button.attr('class', this.options.iconsPrefix + ' ' + this.options.icons.paginationSwitchDown);
    } else {
      button.attr('class', this.options.iconsPrefix + ' ' + this.options.icons.paginationSwitchUp);
    }
    this.updatePagination();
  };

  BootstrapTable.prototype.refresh = function (params) {
    if (params && params.url) {
      this.options.pageNumber = 1;
    }
    this.initServer(params && params.silent, params && params.query, params && params.url);
    this.trigger('refresh', params);
  };

  BootstrapTable.prototype.resetWidth = function () {
    if (this.options.showHeader && this.options.height) {
      this.resetHeader(); //this.fitHeader();
    }
    if (this.options.showFooter) {
      this.fitFooter();
    }
  };

  BootstrapTable.prototype.showColumn = function (field) {
    this.toggleColumn(getFieldIndex(this.columns, field), true, true);
  };

  BootstrapTable.prototype.hideColumn = function (field) {
    this.toggleColumn(getFieldIndex(this.columns, field), false, true);
  };

  BootstrapTable.prototype.getHiddenColumns = function () {
    return $.grep(this.columns, function (column) {
      return !column.visible;
    });
  };

  BootstrapTable.prototype.getVisibleColumns = function () {
    return $.grep(this.columns, function (column) {
      return column.visible;
    });
  };

  BootstrapTable.prototype.toggleAllColumns = function (visible) {
    $.each(this.columns, function (i, column) {
      this.columns[i].visible = visible;
    });

    this.initHeader();
    this.initSearch();
    this.initPagination();
    this.initBody();
    if (this.options.showColumns) {
      var $items = this.$toolbar.find('.keep-open input').prop('disabled', false);

      if ($items.filter(':checked').length <= this.options.minimumCountColumns) {
        $items.filter(':checked').prop('disabled', true);
      }
    }
  };

  BootstrapTable.prototype.showAllColumns = function () {
    this.toggleAllColumns(true);
  };

  BootstrapTable.prototype.hideAllColumns = function () {
    this.toggleAllColumns(false);
  };

  BootstrapTable.prototype.filterBy = function (columns) {
    this.filterColumns = $.isEmptyObject(columns) ? {} : columns;
    this.options.pageNumber = 1;
    this.initSearch();
    this.updatePagination();
  };

  BootstrapTable.prototype.scrollTo = function (value) {
    if (typeof value === 'string') {
      value = value === 'bottom' ? this.$tableBody[0].scrollHeight : 0;
    }
    if (typeof value === 'number') {
      this.$tableBody.scrollTop(value);
    }
    if (typeof value === 'undefined') {
      return this.$tableBody.scrollTop();
    }
  };

  BootstrapTable.prototype.getScrollPosition = function () {
    return this.scrollTo();
  };

  BootstrapTable.prototype.selectPage = function (page) {
    if (page > 0 && page <= this.options.totalPages) {
      this.options.pageNumber = page;
      this.updatePagination();
    }
  };

  BootstrapTable.prototype.prevPage = function () {
    if (this.options.pageNumber > 1) {
      this.options.pageNumber--;
      this.updatePagination();
    }
  };

  BootstrapTable.prototype.nextPage = function () {
    if (this.options.pageNumber < this.options.totalPages) {
      this.options.pageNumber++;
      this.updatePagination();
    }
  };

  BootstrapTable.prototype.toggleView = function () {
    this.options.cardView = !this.options.cardView;
    this.initHeader();
    // Fixed remove toolbar when click cardView button.
    //that.initToolbar();
    this.initBody();
    this.trigger('toggle', this.options.cardView);
  };

  BootstrapTable.prototype.refreshOptions = function (options) {
    //If the objects are equivalent then avoid the call of destroy / init methods
    if (compareObjects(this.options, options, true)) {
      return;
    }
    this.options = $.extend(this.options, options);
    this.trigger('refresh-options', this.options);
    this.destroy();
    this.init();
  };

  BootstrapTable.prototype.resetSearch = function (text) {
    var $search = this.$toolbar.find('.search input');
    $search.val(text || '');
    this.onSearch({
      currentTarget: $search
    });
  };

  BootstrapTable.prototype.expandRow_ = function (expand, index) {
    var $tr = this.$body.find(sprintf('> tr[data-index="%s"]', index));
    if ($tr.next().is('tr.detail-view') === (expand ? false : true)) {
      $tr.find('> td > .detail-icon').click();
    }
  };

  BootstrapTable.prototype.expandRow = function (index) {
    this.expandRow_(true, index);
  };

  BootstrapTable.prototype.collapseRow = function (index) {
    this.expandRow_(false, index);
  };

  BootstrapTable.prototype.expandAllRows = function (isSubTable) {
    if (isSubTable) {
      var $tr = this.$body.find(sprintf('> tr[data-index="%s"]', 0)),
        that = this,
        detailIcon = null,
        executeInterval = false,
        idInterval = -1;

      if (!$tr.next().is('tr.detail-view')) {
        $tr.find('> td > .detail-icon').click();
        executeInterval = true;
      } else if (!$tr.next().next().is('tr.detail-view')) {
        $tr.next().find('.detail-icon').click();
        executeInterval = true;
      }

      if (executeInterval) {
        try {
          idInterval = setInterval(function () {
            detailIcon = that.$body.find('tr.detail-view').last().find('.detail-icon');
            if (detailIcon.length > 0) {
              detailIcon.click();
            } else {
              clearInterval(idInterval);
            }
          }, 1);
        } catch (ex) {
          clearInterval(idInterval);
        }
      }
    } else {
      var trs = this.$body.children();
      for (var i = 0; i < trs.length; i++) {
        this.expandRow_(true, $(trs[i]).data('index'));
      }
    }
  };

  BootstrapTable.prototype.collapseAllRows = function (isSubTable) {
    if (isSubTable) {
      this.expandRow_(false, 0);
    } else {
      var trs = this.$body.children();
      for (var i = 0; i < trs.length; i++) {
        this.expandRow_(false, $(trs[i]).data('index'));
      }
    }
  };

  BootstrapTable.prototype.updateFormatText = function (name, text) {
    if (this.options[sprintf('format%s', name)]) {
      if (typeof text === 'string') {
        this.options[sprintf('format%s', name)] = function () {
          return text;
        };
      } else if (typeof text === 'function') {
        this.options[sprintf('format%s', name)] = text;
      }
    }
    this.initToolbar();
    this.initPagination();
    this.initBody();
  };


  /**
   *  6149 视图列表的翻页导航支持配置显示位置在页面底部
   */
  BootstrapTable.prototype.fixedPagination = function () {
    var _self = this;
    // 列表显示分页时处理
    if (this.options.pagination) {
      var panelBodyHeight = 0;
      if (_self.$el.parents(".panel-body")[0] && _self.$el.parents(".panel-body")[0].style.height) {
        panelBodyHeight = parseFloat(_self.$el.parents(".panel-body")[0].style.height); //配置的面板高度
      }
      if (panelBodyHeight) {
        _self.findTableContainer($(_self.$el.parents(".panel-body")[0]), 0)
        _self.$tableBodyContainer.css({
          "overflow-x": "auto",
          "overflow-y": "auto"
        })
      }
    }
  };
  /***
   * parentEl:供查找的目标dom
   * height：无.fixed-table-body-container
   */
  BootstrapTable.prototype.findTableContainer = function (parentEl, height) {
    var _self = this;
    var targetEl = undefined;
    parentEl.find(">div").each(function (index, item) {
      if ($(item).find("#" + _self.$el.attr("id"))[0]) { //包含目标div
        targetEl = $(item);
      } else if ($(item).hasClass("panel-tab-content")) { // tabpanel 不计算高度
      } else if ($(item).find(".fixed-table-body-container")[0]) { //不是tabpanel且包含目标div
        targetEl = $(item);
      } else if ($(item).hasClass("fixed-table-body-container")) { //是目标div
        targetEl = $(item);
      } else {
        height += $(item).outerHeight(true); //不包含目标div，计算高度，包括margin
      }
    })
    if (targetEl) {
      if (targetEl.hasClass("fixed-table-body-container")) {
        var panelBodyHeight = _self.$el.parents(".panel-body")[0].style.height;
        var tableContainerHeight = parseFloat(panelBodyHeight) - height;
        targetEl[0].style.height = tableContainerHeight + "px";
      } else {
        _self.findTableContainer(targetEl, height);
      }
    }
  };


  // BOOTSTRAP TABLE PLUGIN DEFINITION
  // =======================

  var allowedMethods = [
    'getOptions',
    'getSelections',
    'getAllSelections',
    'getData',
    'load',
    'append',
    'prepend',
    'remove',
    'removeAll',
    'insertRow',
    'updateRow',
    'updateCell',
    'updateByUniqueId',
    'removeByUniqueId',
    'getRowByUniqueId',
    'showRow',
    'hideRow',
    'getRowsHidden',
    'mergeCells',
    'checkAll',
    'uncheckAll',
    'checkInvert',
    'check',
    'uncheck',
    'checkBy',
    'uncheckBy',
    'refresh',
    'resetView',
    'resetWidth',
    'destroy',
    'showLoading',
    'hideLoading',
    'showColumn',
    'hideColumn',
    'getHiddenColumns',
    'getVisibleColumns',
    'showAllColumns',
    'hideAllColumns',
    'filterBy',
    'scrollTo',
    'getScrollPosition',
    'selectPage',
    'prevPage',
    'nextPage',
    'togglePagination',
    'toggleView',
    'refreshOptions',
    'resetSearch',
    'expandRow',
    'collapseRow',
    'expandAllRows',
    'collapseAllRows',
    'updateFormatText',
    'fixedPagination'
  ];

  $.fn.bootstrapTable = function (option) {
    var value,
      args = Array.prototype.slice.call(arguments, 1);

    this.each(function () {
      var $this = $(this),
        data = $this.data('bootstrap.table'),
        options = $.extend({}, BootstrapTable.DEFAULTS, $this.data(), typeof option === 'object' && option);

      if (typeof option === 'string') {
        if ($.inArray(option, allowedMethods) < 0) {
          throw new Error('Unknown method: ' + option);
        }

        if (!data) {
          return;
        }

        value = data[option].apply(data, args);

        if (option === 'destroy') {
          $this.removeData('bootstrap.table');
        }
      }

      if (!data) {
        $this.data('bootstrap.table', (data = new BootstrapTable(this, options)));
      }
    });

    return typeof value === 'undefined' ? this : value;
  };
  $.fn.bootstrapTable.Constructor = BootstrapTable;
  $.fn.bootstrapTable.defaults = BootstrapTable.DEFAULTS;
  $.fn.bootstrapTable.columnDefaults = BootstrapTable.COLUMN_DEFAULTS;
  $.fn.bootstrapTable.locales = BootstrapTable.LOCALES;
  $.fn.bootstrapTable.methods = allowedMethods;
  $.fn.bootstrapTable.utils = {
    sprintf: sprintf,
    getFieldIndex: getFieldIndex,
    compareObjects: compareObjects,
    calculateObjectValue: calculateObjectValue,
    getItemField: getItemField,
    objectKeys: objectKeys,
    isIEBrowser: isIEBrowser
  };

  // BOOTSTRAP TABLE INIT
  // =======================

  $(function () {
    $('[data-toggle="table"]').bootstrapTable();
  });
})(jQuery);
