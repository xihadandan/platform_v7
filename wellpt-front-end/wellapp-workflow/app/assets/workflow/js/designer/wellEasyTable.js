function equal(a, b) {
  if (a === b) return true;
  if (a === undefined || b === undefined) return false;
  if (a === null || b === null) return false;
  // Check whether 'a' or 'b' is a string (primitive or object).
  // The concatenation of an empty string (+'') converts its argument to a string's primitive.
  if (a.constructor === String) return a + '' === b + ''; // a+'' - in case 'a' is a String object
  if (b.constructor === String) return b + '' === a + ''; // b+'' - in case 'b' is a String object
  return false;
}

function randomString(len) {
  len = len || 32;
  var $chars = 'ABCDEFGHJKMNPQRSTWXYZabcdefhijkmnprstwxyz2345678'; /****默认去掉了容易混淆的字符oOLl,9gq,Vv,Uu,I1****/
  var maxPos = $chars.length;
  var pwd = '';
  for (var i = 0; i < len; i++) {
    pwd += $chars.charAt(Math.floor(Math.random() * maxPos));
  }
  return pwd;
}

(function ($) {
  var WellEasyTable = function (ele, opt) {
    this.element = ele;
    this.$element = $(ele);
    this.defaults = {
      data: [],
      columns: []
    };
    this._options = opt; // 原选项数据
    this.options = $.extend({}, this.defaults, opt);
  };
  WellEasyTable.prototype = {
    destroy: function () {
      var _self = this;
    },
    init: function () {
      var _self = this;
      if (!_self.tableWrap) {
        _self.renderDom(_self);
      }
      if (_self.options.columns && _self.options.columns.length) {
        _self.renderHeader(_self);
      } else {
        console.error('WellEasyTable缺少列定义');
        return;
      }
      if (_self.options.data.length) {
        _self.renderData(_self);
      } else {
      }
      _self.bindEvent();
      _self.$element.data('wellEasyTable', _self);
    },
    renderDom: function (_self) {
      _self.tableWrap = $('<table class="well-easy-table">');
      _self.tableHeader = $('<thead class="well-easy-table-header">');
      _self.tableHeader.append('<tr></tr>');
      _self.tableBody = $('<tbody class="well-easy-table-body">');
      _self.tableWrap.append(_self.tableHeader).append(_self.tableBody);
      _self.$element.append(_self.tableWrap);
    },
    renderHeader: function (_self) {
      _self.showColumns = [];
      $.each(_self.options.columns, function (i, item) {
        if (item.visible !== undefined && !item.visible) {
          return true;
        }
        if (item.title) {
          var _width = item.width ? 'width: ' + item.width : '';
          _self.tableHeader
            .find('tr')
            .append('<th class="well-easy-table-header-title" key="' + item.field + '" style="' + _width + '">' + item.title + '</th>');
          _self.showColumns.push(item);
        }
        if (item.checkbox) {
          if (_self.options.checkAll) {
            var ele_id = _self.$element.attr('id');
            _self.tableHeader
              .find('tr')
              .append(
                '<th class="well-easy-table-header-title" style="width: 35px">' +
                  '<input type="checkbox" class="table-checkbox checkAll" id="checkAll_' +
                  ele_id +
                  '" name="checkAll">' +
                  '<label for="checkAll_' +
                  ele_id +
                  '"></label>' +
                  '</th>'
              );
          } else {
            _self.tableHeader.find('tr').append('<th class="well-easy-table-header-title" style="width: 35px"></th>');
          }
          _self.showColumns.push(item);
        }
      });
    },
    renderData: function (_self) {
      $.each(_self.options.data, function (i, data) {
        _self.renderRowData(_self, data);
      });
    },
    renderRowData: function (_self, data) {
      var rowKey = randomString(12);
      var $tr = $('<tr data-row-key="' + rowKey + '">');
      data.rowKey = rowKey;
      $tr.data('rowData', data);
      $.each(_self.showColumns, function (j, item) {
        var $td = $('<td data-field="' + item.field + '">');
        if (item.checkbox && item.checkbox) {
          var $checkbox = $(
            '<input type="checkbox" class="table-checkbox" id="' + item.field + '_' + rowKey + '" name="' + item.field + '_' + rowKey + '">'
          );
          $checkbox[0].checked = data[item.field] || false;
          $td.append($checkbox);
          $td.append('<label for="' + item.field + '_' + rowKey + '"></label>');
          $tr.append($td);
          return true;
        }
        if (item.editable) {
          var $input = $(
            '<input type="text" class="form-control" id="' + item.field + '_' + rowKey + '" name="' + item.field + '_' + rowKey + '">'
          );
          $input[0].value = data[item.field] || '';
          switch (item.editable.type) {
            case 'input':
              break;
            case 'select':
              setTimeout(function () {
                $input.wellSelect(item.editable.options);
              }, 0);
              break;
            case 'checkbox':
              //todo
              break;
          }
          $td.append($input);
          $tr.append($td);
          setTimeout(function () {
            $input.on('change', function () {
              var _val = $(this).val();
              var _label = '';
              switch (item.editable.type) {
                case 'input':
                  _label = _val;
                  break;
                case 'select':
                  _label = $input.wellSelect('data').text;
                  break;
                case 'checkbox':
                  //todo
                  break;
              }
              _self.updateRowFieldData(rowKey, item.field, _val, _label);
            });
          }, 50);
          return true;
        }
        if (item.formatter) {
          var newVal = item.formatter(data[item.field], data, rowKey);
          $td.append('<div style="text-align: center" id="' + item.field + '_' + rowKey + '">' + (newVal || '') + '</div>');
          $tr.append($td);
        } else {
          $td.append('<div style="text-align: center" id="' + item.field + '_' + rowKey + '">' + (data[item.field] || '') + '</div>');
          $tr.append($td);
        }
      });
      _self.tableBody.append($tr);

      if (_self.options.rowDataChangeEvent) {
        _self.options.rowDataChangeEvent($tr, rowKey);
      }
      _self.tableBody.trigger('updateRowData');
    },
    updateRowFieldData: function (rowKey, field, value, label) {
      var _self = this;
      var curRow = _self.tableBody.find('[data-row-key="' + rowKey + '"]');
      var rowData = curRow.data('rowData');
      if (rowData[field] === value) {
        return;
      }
      rowData[field] = value;
      curRow.data('rowData', rowData);
      if (_self.options.afterEditCell) {
        _self.options.afterEditCell(rowKey, field, value, label);
      }
    },
    updateRowData: function (rowKey, data) {
      var _self = this;
      var curRow = _self.tableBody.find('[data-row-key="' + rowKey + '"]');
      curRow.data('rowData', data);
      $.each(_self.showColumns, function (j, item) {
        if (item.editable) {
          switch (item.editable.type) {
            case 'input':
              curRow.find('#' + item.field + '_' + rowKey).val(data[item.field]);
              break;
            case 'select':
              curRow.find('#' + item.field + '_' + rowKey).wellSelect('val', data[item.field]);
              break;
            case 'checkbox':
              //todo
              break;
          }
          return true;
        }
        curRow.find('#' + item.field + '_' + rowKey).text(data[item.field]);
      });
      _self.tableBody.trigger('updateRowData');
    },
    addRowData: function (data) {
      var _self = this;
      _self.renderRowData(_self, data || {});
    },
    delRowData: function (rowKey) {
      var _self = this;
      _self.tableBody.find('[data-row-key="' + rowKey + '"]').remove();
      _self.tableBody.trigger('updateRowData');
    },
    rowMoveUp: function (rows) {
      var _self = this;
      $.each(rows, function (i, item) {
        var curRow = _self.tableBody.find('[data-row-key="' + item + '"]');
        var $prev = curRow.prev();
        if (!$prev.length || rows.indexOf($prev.attr('data-row-key')) > -1) {
          return true;
        }
        $prev.before(curRow.clone(true));
        curRow.remove();
      });
    },
    rowMoveDown: function (rows) {
      var _self = this;
      $.each(rows.reverse(), function (i, item) {
        var curRow = _self.tableBody.find('[data-row-key="' + item + '"]');
        var $next = curRow.next();
        if (!$next.length || rows.indexOf($next.attr('data-row-key')) > -1) {
          return true;
        }
        $next.after(curRow.clone(true));
        curRow.remove();
      });
    },
    getChooseRow: function () {
      var _self = this;
      var checkedRows = [];
      _self.tableBody.find('.table-checkbox').each(function () {
        var $this = $(this);
        var checked = $this[0].checked;
        if (checked) {
          checkedRows.push($this.closest('tr').attr('data-row-key'));
        }
      });
      return checkedRows;
    },
    getRowData: function (rowkey) {
      var _self = this;
      var isMultiple = $.isArray(rowkey);
      if (isMultiple) {
        var rowDatas = [];
        $.each(rowkey, function (i, item) {
          rowDatas.push(_self.getSingleRowData(item));
        });
        return rowDatas;
      } else {
        return _self.getSingleRowData(rowkey);
      }
    },
    getSingleRowData: function (rowKey) {
      var _self = this;
      var curRow = _self.tableBody.find('[data-row-key="' + rowKey + '"]');
      return curRow.data('rowData');
    },
    getAllData: function () {
      var _self = this;
      var allData = [];
      $.each(_self.tableBody.find('tr'), function () {
        var $this = $(this);
        var rowKey = $this.attr('data-row-key');
        allData.push(_self.getSingleRowData(rowKey));
      });
      return allData;
    },
    bindEvent: function () {
      var _self = this;
      if (_self.options.addBtn) {
        _self.$element.on('click', _self.options.addBtn, function () {
          _self.addRowData();
        });
      }
      if (_self.options.removeBtn) {
        _self.$element.on('click', _self.options.removeBtn, function () {
          var chooseRows = _self.getChooseRow();
          if (!chooseRows || chooseRows.length === 0) {
            top.appModal.error('请选择要删除的数据！');
            return;
          }
          if ($.isArray(chooseRows)) {
            $.each(chooseRows, function (i, item) {
              _self.delRowData(item);
            });
            $(_self.tableHeader).find('.checkAll').attr('checked', false);
          } else {
            _self.delRowData(chooseRows);
          }
        });
      }
      if (_self.options.moveUpBtn) {
        _self.$element.on('click', _self.options.moveUpBtn, function () {
          var chooseRows = _self.getChooseRow();
          if (!chooseRows || chooseRows.length === 0) {
            top.appModal.error('请选择要上移的数据！');
            return;
          }
          if ($.isArray(chooseRows)) {
            _self.rowMoveUp(chooseRows);
          } else {
            _self.rowMoveUp([chooseRows]);
          }
        });
      }
      if (_self.options.moveDownBtn) {
        _self.$element.on('click', _self.options.moveDownBtn, function () {
          var chooseRows = _self.getChooseRow();
          if (!chooseRows || chooseRows.length === 0) {
            top.appModal.error('请选择要下移的数据！');
            return;
          }
          if ($.isArray(chooseRows)) {
            _self.rowMoveDown(chooseRows);
          } else {
            _self.rowMoveDown([chooseRows]);
          }
        });
      }

      //全选
      $(_self.tableHeader).on('change', '.checkAll', function () {
        var checked = $(this).attr('checked') ? true : false;
        $(_self.tableBody).find('.table-checkbox').attr('checked', checked);
      });

      $(_self.tableBody).on('change', '.table-checkbox', function () {
        var checked = $(this).attr('checked');
        if (checked) {
          var allChecked = true;
          $(_self.tableBody)
            .find('.table-checkbox')
            .each(function () {
              if (!$(this).attr('checked')) {
                allChecked = false;
                return false;
              }
            });
          $(_self.tableHeader).find('.table-checkbox').attr('checked', allChecked);
        } else {
          $(_self.tableHeader).find('.table-checkbox').attr('checked', false);
        }
      });
    }
  };
  $.fn.wellEasyTable = function (options) {
    var args = Array.prototype.slice.call(arguments, 0),
      opts,
      wellEasyTable,
      method,
      value,
      multiple,
      allowedMethods = [
        'val',
        'destroy',
        'addRowData',
        'delRowData',
        'rowMoveUp',
        'rowMoveDown',
        'updateRowFieldData',
        'updateRowData',
        'getChooseRow',
        'getRowData',
        'getAllData'
      ],
      propertyMethods = ['val', 'data'],
      methodsMap = {
        search: 'externalSearch'
      };

    this.each(function () {
      if (args.length && args[0] === undefined) {
        args.length = 0;
      }
      if (args.length === 0 || typeof args[0] === 'object') {
        opts = args.length === 0 ? {} : $.extend({}, args[0]);
        opts.element = $(this);

        if (opts.element.get(0).tagName.toLowerCase() === 'select') {
          multiple = opts.element.prop('multiple');
        } else {
          multiple = opts.multiple || false;
          if ('tags' in opts) {
            opts.multiple = multiple = true;
          }
        }

        //创建wellEasyTable的实体
        wellEasyTable = new WellEasyTable(this, options);
        wellEasyTable.init(opts);
      } else if (typeof args[0] === 'string') {
        if (allowedMethods.indexOf(args[0]) < 0) {
          throw 'Unknown method: ' + args[0];
        }

        value = undefined;
        wellEasyTable = $(this).data('wellEasyTable');
        if (wellEasyTable === undefined) return;

        method = args[0];

        if (method === 'container') {
          value = wellEasyTable.container;
        } else if (method === 'dropdown') {
          value = wellEasyTable.dropdown;
        } else {
          if (methodsMap[method]) method = methodsMap[method];
          value = wellEasyTable[method].apply(wellEasyTable, args.slice(1));
        }
        if (propertyMethods.indexOf(args[0]) >= 0 && args.length === 1) {
          return false; // abort the iteration, ready to return first matched value
        }
      } else {
        throw 'Invalid arguments to select2 plugin: ' + args;
      }
    });
    // return typeof(args[0]) === "string" ? value : this;
    return value === undefined ? this : value;
  };
})(jQuery);
