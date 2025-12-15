(function (factory) {
  'use strict';
  if (typeof define === 'function' && define.amd) {
    // AMD. Register as an anonymous module.
    define(['jquery'], factory);
  } else {
    // Browser globals
    factory(jQuery);
  }
})(function ($) {
  'use strict';
  // bootstrap table卡片网格视图扩展插件
  $.extend($.fn.bootstrapTable.defaults, {
    cardGridEnable: false,
    cardGridColumn: 3
  });

  // 切换卡片视图方法
  $.fn.bootstrapTable.methods.push('toggleCardView');
  $.fn.bootstrapTable.methods.push('switchToCardView');

  var sprintf = $.fn.bootstrapTable.utils.sprintf;
  var getItemField = $.fn.bootstrapTable.utils.getItemField;
  var getFieldIndex = $.fn.bootstrapTable.utils.getFieldIndex;
  var calculateObjectValue = $.fn.bootstrapTable.utils.calculateObjectValue;

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

  var BootstrapTable = $.fn.bootstrapTable.Constructor;
  var _initTable = BootstrapTable.prototype.initTable;
  var _initHeader = BootstrapTable.prototype.initHeader;
  var _initBody = BootstrapTable.prototype.initBody;

  BootstrapTable.prototype.toggleCardView = function () {
    if (this.options.treeView && this.toggleTreeView) {
      this.toggleTreeView();
    }
    this.toggleView();
  };

  BootstrapTable.prototype.switchToCardView = function (cardGridColumn) {
    var _self = this;
    if (_self.options.treeView && _self.toggleTreeView) {
      _self.toggleTreeView();
    }
    _self.options.cardView = false;
    _self.options.cardGridColumn = cardGridColumn;
    _self.toggleView();
  };

  BootstrapTable.prototype.initTable = function () {
    var _self = this;
    _initTable.apply(_self, Array.prototype.slice.apply(arguments));
  };

  BootstrapTable.prototype.initHeader = function () {
    var _self = this;
    _initHeader.apply(_self, Array.prototype.slice.apply(arguments));
  };

  BootstrapTable.prototype.initBody = function () {
    var _self = this;

    // 卡片网格视图
    if (_self.options.cardView && _self.options.cardGridEnable) {
      _self.initCardGridBody.apply(_self, Array.prototype.slice.apply(arguments));
    } else {
      _initBody.apply(_self, Array.prototype.slice.apply(arguments));
    }
  };

  BootstrapTable.prototype.initCardGridBody = function (fixedScroll) {
    var that = this,
      html = [],
      data = this.getData();
    var cardGridColumn = 3;
    if ((that.options.cardGridColumn + '').indexOf(',') != -1) {
      cardGridColumn = parseInt((that.options.cardGridColumn + '').split(',')[0]);
    } else {
      cardGridColumn = parseInt(that.options.cardGridColumn);
    }

    this.trigger('pre-body', data);

    this.$body = this.$el.find('>tbody');
    if (!this.$body.length) {
      this.$body = $('<tbody></tbody>').appendTo(this.$el);
    }

    //Fix #389 Bootstrap-table-flatJSON is not working

    if (!this.options.pagination || this.options.sidePagination === 'server') {
      this.pageFrom = 1;
      this.pageTo = data.length;
    }
    var index = 0;
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

      // 卡片网格视图处理
      if (index % cardGridColumn == 0) {
        html.push('<tr', sprintf(' class="card-view-row %s"', 'card-column-' + cardGridColumn), '>');
      }
      html.push('<td><table class="card-view-table"><tbody>');

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
          data_ = '',
          rowspan_ = '',
          colspan_ = '',
          title_ = '',
          column = that.columns[j];

        if (that.fromHtml && typeof value === 'undefined') {
          return;
        }

        if (!column.visible) {
          return;
        }

        if (that.options.cardView && !column.cardVisible) {
          return;
        }

        style = sprintf('style="%s"', csses.concat(column.field === 'lineEnderToolbar' ? '' : that.header.styles[j]).join('; '));

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
          title_ = sprintf(' title="%s"', item['_' + field + '_title']);
          title_ = title_.replace(/\</g, '&lt;').replace(/\>/g, '&gt;').replace(/\"/g, "'");
        }
        cellStyle = calculateObjectValue(that.header, that.header.cellStyles[j], [value, item, i, field], cellStyle);
        if (cellStyle.classes) {
          class_ = sprintf(' class="%s"', cellStyle.classes);
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
            sprintf(that.options.cardView ? '<div class="card-view %s">' : '<td class="bs-checkbox %s">', column['class'] || ''),
            '<input' +
              sprintf(' data-index="%s"', i) +
              sprintf(' name="%s"', that.options.selectItemName) +
              sprintf(' type="%s"', type) +
              sprintf(' value="%s"', item[that.options.idField]) +
              sprintf(' checked="%s"', value === true || (value && value.checked) ? 'checked' : undefined) +
              sprintf(' disabled="%s"', !column.checkboxEnabled || (value && value.disabled) ? 'disabled' : undefined) +
              ' />',
            that.header.formatters[j] && typeof value === 'string' ? value : '',
            that.options.cardView ? '</div>' : '</td>'
          ].join('');

          item[that.header.stateField] = value === true || (value && value.checked);
        } else {
          value = typeof value === 'undefined' || value === null ? that.options.undefinedText : value;
          var title = ((column.field === 'lineEnderToolbar' ? column.title : value) || '')
            .replace(/\</g, '&lt;')
            .replace(/\>/g, '&gt;')
            .replace(/\"/g, "'");

          text = that.options.cardView
            ? [
                '<div class="card-view">',
                that.options.showHeader
                  ? sprintf('<span class="title" %s>%s</span>', style, getPropertyFromOther(that.columns, 'field', 'title', field))
                  : '',
                sprintf('<span title="%s" class="value">%s</span>', title, value),
                '</div>'
              ].join('')
            : [sprintf('<td%s %s %s %s %s %s %s>', id_, class_, style, data_, rowspan_, colspan_, title_), value, '</td>'].join('');

          // Hide empty data on Card view when smartDisplay is set to true.
          // if (that.options.cardView && that.options.smartDisplay && value === '') {
          //   // Should set a placeholder for event binding correct fieldIndex
          //   text = '<div class="card-view"></div>';
          // }
        }

        html.push(text);
      });

      if (this.options.cardView) {
        html.push('</div></td>');
      }

      html.push('</tr>');

      // 卡片网格视图处理
      html.push('</tbody></table></td>');
      if ((index + 1) % cardGridColumn == 0) {
        html.push('</tr>');
      }
      index++;
    }
    // 卡片网格视图处理
    // 列补齐
    if (index > 0 && index < cardGridColumn) {
      for (var i = 0; i < cardGridColumn - index; i++) {
        html.push('<td class="fill-td"></td>');
      }
    }
    // 行补齐
    if (index > 0 && index % cardGridColumn != 0) {
      html.push('</tr>');
    }

    // show no records
    if (!html.length) {
      html.push(
        '<tr class="no-records-found">',
        sprintf('<td colspan="%s">%s</td>', this.$header.find('th').length, this.options.formatNoMatches()),
        '</tr>'
      );
    }

    this.$body.html(html.join(''));

    if (!fixedScroll) {
      this.scrollTo(0);
    }

    // click to select by column
    this.$body
      .find('.card-view-table>tbody')
      .find('> tr[data-index] > td')
      .off('click dblclick')
      .on('click dblclick', function (e) {
        var $td = $(this),
          $tr = $td.parent(),
          item = that.data[$tr.data('index')],
          index = $td[0].cellIndex,
          fields = that.getVisibleFields(),
          field = fields[that.options.detailView && !that.options.cardView ? index - 1 : index],
          column = that.columns[getFieldIndex(that.columns, field)],
          value = getItemField(item, field, that.options.escape),
          target = e.target;
        if (field == 'rowCheckItem' && target && $(target).attr('type') != 'checkbox' && fields.length > 1) {
          field = fields[1];
        }

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
      .find('.card-view-table>tbody')
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

    this.$selectItem = this.$body.find('.card-view-table>tbody').find(sprintf('[name="%s"]', this.options.selectItemName));
    this.$selectItem.off('click').on('click', function (event) {
      event.stopImmediatePropagation();

      var $this = $(this),
        checked = $this.prop('checked'),
        row = that.data[$this.data('index')];

      if (that.options.maintainSelected && $(this).is(':radio')) {
        $.each(that.options.data, function (i, row) {
          row[that.header.stateField] = false;
        });
      }

      row[that.header.stateField] = checked;

      if (that.options.singleSelect) {
        that.$selectItem.not(this).each(function () {
          that.data[$(this).data('index')][that.header.stateField] = false;
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
        that.$body
          .find('.card-view-table>tbody')
          .find('>tr:not(.no-records-found)')
          .each(function () {
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
});
