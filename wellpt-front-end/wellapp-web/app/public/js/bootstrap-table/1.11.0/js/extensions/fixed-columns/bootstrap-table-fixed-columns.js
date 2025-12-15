(function (factory) {
  'use strict';
  if (typeof define === 'function' && define.amd) {
    // AMD. Register as an anonymous module.
    define([
      'jquery',
      'commons',
      'server',
      'appModal',
      'lodash',
      'css!' + ctx + '/static/js/bootstrap-table/1.11.0/js/extensions/fixed-columns/bootstrap-table-fixed-columns'
    ], factory);
  } else {
    // Browser globals
    factory(jQuery);
  }
})(function (jquery, commons, server, appModal, lodash) {
  'use strict';

  // 调用bootstrapTable组件的构造器得到对象
  var BootstrapTable = $.fn.bootstrapTable.Constructor;

  BootstrapTable.prototype.getFixedColumnsWidth = function () {
    var _this = this;
    var visibleFields = this.getVisibleFields();
    var width = 0;
    var rightWidth = 0;
    var columnsArr = [];

    var _num = parseInt($('#fixedNumber').val() || 0);

    var _rightNum = parseInt($('#fixedRightNumber').val() || 0);

    // 固定列数不包含复选框列
    if ($.inArray('rowCheckItem', visibleFields) > -1) {
      _num++;
    }

    // 固定列数不包含序号列
    if ($.inArray('sequenceIndex', visibleFields) > -1) {
      _num++;
    }

    // 固定右侧列不包含操作列
    if ($.inArray('lineEnderToolbar', visibleFields) > -1) {
      _rightNum++;
    }

    for (var i = 0; i < _num; i++) {
      var $headers = this.$header.find('th[data-field]');
      width += $($headers.get(i)).outerWidth(true);
      columnsArr.push(i);
    }
    for (var i = 0; i < _rightNum; i++) {
      var $headers = this.$header.find('th[data-field]');
      var visibleFieldsLength = $headers.length;
      rightWidth += $($headers.get(visibleFieldsLength - i - 1)).outerWidth(true);
      columnsArr.push(visibleFieldsLength - i - 1);
    }

    //不可拖动更改列宽的列
    _this.options.disabledColumns = columnsArr;

    return {
      width: width + 2 * parseInt(_num) + 1,
      rightWidth: rightWidth + parseInt(_rightNum) + 1
    };
  };


  // 绑定事件
  BootstrapTable.prototype.initFixedColumnsEvents = function () {
    var _this = this;
    var $table = _this.$tableBody;
    var $table_l = _this.$fixedBody; //左侧
    var $table_r = _this.$fixedRightBody; //右侧
    // events
    var toggleHover = function (e, toggle) {
      var tr = 'tr[data-index="' + $(e.currentTarget).data('index') + '"]';
      var $trs = $(tr, $table),
        $trs_l = $(tr, $table_l),
        $trs_r = $(tr, $table_r);
      if (toggle) {
        $trs.addClass('row-hover');
        $trs_l.addClass('row-hover');
        $trs_r.addClass('row-hover');
      } else {
        $trs.removeClass('row-hover');
        $trs_l.removeClass('row-hover');
        $trs_r.removeClass('row-hover');
      }
    };

    $('tr', $table).hover(
      function (e) {
        toggleHover(e, true);
      },
      function (e) {
        toggleHover(e, false);
      }
    );

    $('#fixedNumber').on('change', function () {
      _this.fixedColumns();
    });

    $('#fixedRightNumber').on('change', function () {
      _this.fixedColumns();
    });
  };
  BootstrapTable.prototype.initFixedColumnsBody = function () {
    this.$container.find('.fixed-table-body-columns').remove();
    this.$fixedBody = $('<div class="fixed-table-body-columns fixed-table-body" style="overflow: hidden"></div>');
    var _this = this;
    setTimeout(function () {
      var $table = _this.$tableBody.find('>table');
      var $cloneTable = $table.clone(true);
      _this.$fixedBody.find("#" + $cloneTable.attr('id') + "_fixed_left").remove();
      $cloneTable.attr('id', $cloneTable.attr('id') + "_fixed_left");
      _this.$fixedBody.append($cloneTable);
      _this.$tableBody.after(_this.$fixedBody);
      $table.trigger('fixedTableColumnsSuccess', ['initFixedColumnsBody']);
    }, 0);
  };

  BootstrapTable.prototype.appendFixedColumns = function () {
    var _fixedNumber = $('#fixedNumber').val() || this.options.fixedNumber;

    if (this.$container.find('#fixedNumber')[0]) {
      this.$container.find('#fixedNumber').val(_fixedNumber);
    } else {
      this.$container
        .find('.fixed-table-toolbar')
        .append(
          '<div class="js-fixed-columns-num bs-bars pull-right">' +
          '<div class="toolbar form-inline">' +
          '  <span>固定列数: </span>' +
          '  <input class="form-control" id="fixedNumber" style="width: 80px;" type="number" value="' +
          _fixedNumber +
          '" min="0" max="' +
          this.getVisibleFields() +
          '">' +
          '</div></div>'
        );

      if (!!this.options.widgetConfiguration) {
        this.$container.find('.js-fixed-columns-num').css('display', 'none');
      }
    }
  };

  // 右侧固定
  BootstrapTable.prototype.appendFixedRightColumns = function () {
    var _fixedRightNumber = $('#fixedRightNumber').val() || this.options.fixedRightNumber;

    if (this.$container.find('#fixedRightNumber')[0]) {
      this.$container.find('#fixedRightNumber').val(_fixedRightNumber);
    } else {
      this.$container
        .find('.fixed-table-toolbar')
        .append(
          '<div class="js-fixed-columns-num bs-bars pull-right">' +
          '<div class="toolbar form-inline">' +
          '  <span>固定列数: </span>' +
          '  <input class="form-control" id="fixedRightNumber" style="width: 80px;" type="number" value="' +
          _fixedRightNumber +
          '" min="0" max="' +
          this.getVisibleFields() +
          '">' +
          '</div></div>'
        );

      if (!!this.options.widgetConfiguration) {
        this.$container.find('.js-fixed-columns-num').css('display', 'none');
      }
    }
  };

  BootstrapTable.prototype.initFixedColumnsRightBody = function () {
    this.$container.find('.fixed-table-body-columns-right').remove();
    this.$fixedRightBody = $('<div class="fixed-table-body-columns-right fixed-table-body" style="overflow: hidden"></div>');
    var _this = this;
    setTimeout(function () {
      var $table = _this.$tableBody.find('>table');
      var $cloneTable = $table.clone(true);
      _this.$fixedRightBody.find("#" + $cloneTable.attr('id') + "_fixed_right").remove();
      $cloneTable.attr('id', $cloneTable.attr('id') + "_fixed_right");
      _this.$fixedRightBody.append($cloneTable);
      _this.$tableBody.after(_this.$fixedRightBody);
      $table.trigger('fixedTableColumnsSuccess', ['initFixedColumnsRightBody:' + window.timetest]);
    }, 0);
  };

  BootstrapTable.prototype.fixedColumns = function () {
    var _this = this;

    if (!this.options.fixedColumns) {
      return;
    }

    if (this.$el.is(':hidden')) {
      return;
    }

    var fixedLeft = false;
    var fixedRight = false;
    if (this.options.fixedNumber || this.options.fixedNumber == 0) {
      fixedLeft = true;
      this.appendFixedColumns();
      this.$container.find('.fixed-table-header-columns').remove();
      this.$fixedHeader = $('<div class="fixed-table-header-columns"></div>');
      this.$fixedHeader.append(this.$tableHeader.find('>table').clone(true));
      this.$tableHeader.after(this.$fixedHeader);
    }

    // if (!_this.initialized) {
    //   setTimeout(function () {
    //     _this.initialized = true;
    //     _this.fixedColumns();
    //   }, 150);
    // }

    if (this.options.fixedRightNumber || this.options.fixedRightNumber == 0) {
      fixedRight = this.options.fixedRightNumber > 0;
      this.appendFixedRightColumns();
      this.$container.find('.fixed-table-header-columns-right').remove();
      this.$fixedHeader = $('<div class="fixed-table-header-columns-right"></div>');
      this.$fixedHeader.append(this.$tableHeader.find('>table').clone(true));
      this.$tableHeader.after(this.$fixedHeader);
    }
    var widthObj = this.getFixedColumnsWidth();
    if (fixedLeft) {
      this.$fixedHeader.css({
        top: 0,
        left: 0,
        width: widthObj.width,
        height: this.$tableHeader.outerHeight(true)
      });
      this.initFixedColumnsBody();

      this.$fixedBody.css({
        top: this.$tableHeader.outerHeight(true),
        left: 0,
        width: widthObj.width,
        height: this.$tableBody.find('table:first').outerHeight()
      });
    }

    if (this.options.fixedRightNumber || this.options.fixedRightNumber == 0) {
      //var widthObj = this.getFixedColumnsWidth();
      this.$fixedHeader.css({
        top: 0,
        right: 0,
        left: 'auto',
        width: widthObj.rightWidth,
        height: this.$tableHeader.outerHeight(true)
      });

      this.initFixedColumnsRightBody();
      this.$fixedRightBody.css({
        top: this.$tableHeader.outerHeight(true),
        right: 0,
        left: 'auto',
        width: widthObj.rightWidth + ((fixedLeft && fixedRight) ? 1 : 0),
        height: this.$tableBody.find('table:first').outerHeight()
      });
    }

    this.initFixedColumnsEvents();
  };

  $.fn.bootstrapTable.methods.push('fixedColumns');
});
