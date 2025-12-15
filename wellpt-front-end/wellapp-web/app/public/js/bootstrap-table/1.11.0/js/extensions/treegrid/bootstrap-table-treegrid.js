(function (factory) {
  'use strict';
  if (typeof define === 'function' && define.amd) {
    // AMD. Register as an anonymous module.
    define(['jquery', 'commons', 'server', 'appModal', 'lodash'], factory);
  } else {
    // Browser globals
    factory(jQuery);
  }
})(function (jquery, commons, server, appModal, lodash) {
  'use strict';

  // 调用bootstrapTable组件的构造器得到对象
  var BootstrapTable = $.fn.bootstrapTable.Constructor,
    _initBody = BootstrapTable.prototype.initBody;

  var seq = 1; //序号
  var existFieldMultiType = false; //表示当前树形数据是存在单个字段多分类情况

  /**
   * 插入树列表
   */
  function insertTreeRow($table, rows, rowParam) {
    //$table.bootstrapTable('removeAll');
    for (var i = 0, len = rows.length; i < len; i++) {
      $table.bootstrapTable('insertRow', {
        row: rows[i],
        index: rowParam.rowIndex++
      });
      if (rows[i].children && rows[i].children.length > 0) {
        insertTreeRow($table, rows[i].children, rowParam);
      }
    }
  }

  /**
   * 批量加载树节点数据到表格
   */
  function batchInsertRow(that, lineRows) {
    var data = {
      fixedScroll: true,
      total: that.options.totalRows
    };
    data[that.options.dataField] = lineRows;
    BootstrapTableMethodOverride.call(this); //重写Bootstrape表格方法
    that.$el.bootstrapTable('load', data);
    BootstrapTable.prototype.initBody = _initBody;
  }

  /**
   * 格式化树形列表，增加展开折叠按钮，增加上下级节点序号属性
   */
  function formateTreeRow(that, rows, rowParam, level, parentDataIndex) {
    for (var i = 0, len = rows.length; i < len; i++) {
      var index = rowParam.rowIndex++;
      var $paddingLeft = $('<span>');
      $paddingLeft.css('padding-left', 15 * level);
      var $tr = that.$el.find('tr[data-index=' + index + ']');
      var $td = $tr.find('td:not(.bs-checkbox ):eq(0)');
      if (rows[i][that.options.idField]) {
        $tr.attr('uuid', rows[i][that.options.idField]);
      }
      if (parentDataIndex != undefined) {
        $td.parent().attr('parent-data-index', parentDataIndex);
      }
      if (rows[i].children && rows[i].children.length > 0) {
        formateTreeRow(that, rows[i].children, rowParam, level + 1, index);
        var $icon = $('<span>', {
          class: 'tree-icon ' + that.options.expandIcon,
          'end-child-index': rowParam.rowIndex - 1,
          title: '折叠',
          style: 'margin-right:5px;padding:2px 6px;'
        });
        $td.html($paddingLeft[0].outerHTML + $icon[0].outerHTML + $td.html());
      } else {
        var $icon = $('<span>', {
          class: ''
        });
        $icon.css('padding-left', 15);
        $td.html($paddingLeft[0].outerHTML + $icon[0].outerHTML + $td.html());
      }
    }
  }

  /**
   * 遍历获取子节点
   * @returns {*}
   */
  function getChild(node, source, idField, parentIdField, treeIds, lineRows, rowDataParam) {
    if (node && $.isArray(node.children)) {
      return node.children;
    } else if (parentIdField == null || typeof parentIdField === 'undefined' || parentIdField == '') {
      return [];
    }
    var data = [];
    var items = $.grep(source, function (item, index) {
      if (node == null || node[idField] == null || typeof node[idField] === 'undefined') {
        return false; // id不允许为空
      }
      return item[parentIdField] == node[idField];
    });
    var parentIndex = rowDataParam.rowIndex - 1;
    $.each(items, function (index, item) {
      item._treeNodeLevel = node._treeNodeLevel + 1;
      item._parentDataIndex = parentIndex; //父节点的行级序号
      data.push(item);
      rowDataParam.rowIndex++;
      lineRows.push(item);
      treeIds.push(item[idField]);
      var child = getChild(item, source, idField, parentIdField, treeIds, lineRows, rowDataParam);
      $.each(child, function (i, n) {
        n[parentIdField] = item[idField];
      });
      item._hasChildren = child && child.length > 0;
      if (item._hasChildren) {
        item._endChildIndex = rowDataParam.rowIndex - 1;
        item.children = child;
      }
    });
    return data;
  }

  /**
   * 获取表格的列表数据到树形节点数据的转换
   * @param that
   * @param data
   * @returns {*}
   */
  function loadTreeNodeData(that, data) {
    var parentIdField = that.options.parentIdField;
    //var initTreeShowStyle = that.options.initTreeShowStyle;//展示方式：EXPAND_ALL/COLLAPSE_ALL/FIRST_EXPAND
    var idField = that.options.idField;
    var displayField = (function () {
      //取表格字段可显示的第一列（不包括单选/多选框所在的列）
      for (var i = 0, len = that.options.columns[0].length; i < len; i++) {
        var col = that.options.columns[0][i];
        if (col.field != 'rowCheckItem' && col.field != 'sequenceIndex' && col.visible) {
          return $.isFunction(col.formatter) ? col.field + 'RenderValue' : col.field;
        }
      }
    })(); //that.options.treeNodeDisplayField;
    var treeNodeFields = that.options.treeNodeFields;

    //按分类多字段构造
    if (that.options.treeBuildType == 'multiField' && treeNodeFields && treeNodeFields.length > 0) {
      var lineRows = [];
      var rowDataParam = {
        rowIndex: 0
      };
      var treeRows = groupByNodeFields(treeNodeFields, data, 0, idField, displayField, lineRows, rowDataParam, parentIdField);
      return {
        treeRows: treeRows,
        lineRows: lineRows
      };
    }

    //按父级字段构造
    return buildTreeNodeByParentField(data, parentIdField, idField, 0);
  }

  /**
   * 通过父级字段构造树形节点
   * @param data
   * @param parentIdField
   * @param idField
   */
  function buildTreeNodeByParentField(data, parentIdField, idField, rowIndex) {
    var treeRows = [];
    var lineRows = [];
    var treeIds = [];
    var rowDataParam = {
      rowIndex: rowIndex == undefined ? 0 : rowIndex
    };
    //按父级字段构造
    var roots = $.grep(data, function (row, i) {
      var parentId = row[parentIdField];
      return parentId == null || typeof parentId === 'undefined';
    });
    var nodeLevel = rowIndex == undefined ? 0 : rowIndex;
    $.each(roots, function (index, item) {
      item._treeNodeLevel = nodeLevel;
      rowDataParam.rowIndex++;
      treeRows.push(item);
      lineRows.push(item);
      treeIds.push(item[idField]);
      var child = getChild(item, data, idField, parentIdField, treeIds, lineRows, rowDataParam);
      $.each(child, function (i, n) {
        n[parentIdField] = item[idField];
      });
      item._hasChildren = child && child.length > 0;
      if (item._hasChildren) {
        item._endChildIndex = rowDataParam.rowIndex - 1;
        item.children = child;
      }
    });

    if ($.isEmptyObject(treeRows)) {
      //未构造出树形节点数据
      return {
        treeRows: data,
        lineRows: data
      };
    }

    //未添加到树形节点数据内的，也需要展示
    for (var d = 0, len = data.length; d < len; d++) {
      if (treeIds.indexOf(data[d][idField]) == -1) {
        treeRows.splice(d,0,data[d]);
        // treeRows.push(data[d]);
        lineRows.splice(d,0,data[d]);
        // lineRows.push(data[d]);
      }
    }

    return {
      treeRows: treeRows,
      lineRows: lineRows
    };
  }

  /**
   * 按分类字段对列表数据进行分类树节点构造
   */
  function groupByNodeFields(treeNodeFields, data, index, idField, displayField, lineRows, rowDataParam, parentIdField) {
    var nodeList = [];
    var muliDisplayField = treeNodeFields[index]['nodeFieldDisplay'] && treeNodeFields[index]['multiNodeData'] == 1; //是否是多分类的字段显示
    // 按节点分类
    var groupNodes = _.groupBy(data, function (d) {
      var nodeFieldValue = d[treeNodeFields[index]['nodeField']];
      if (muliDisplayField && d._seq == undefined) {
        //多分类的字段需要设置序号值，用作最后节点数据集排序
        d._seq = seq++;
        existFieldMultiType = true;
      }
      return !$.isEmptyObject(nodeFieldValue) ? nodeFieldValue : '_GRP_UNKNOWN';
    });

    //分号区分属于多个分类的数据节点
    var key2Display = {}; //分类key对应的显示值
    if (treeNodeFields[index]['multiNodeData'] == 1) {
      for (var k in groupNodes) {
        if (k.indexOf(';') != -1) {
          //多分类的数据按分号进行拆分成不同组
          var ks = k.split(';');
          for (var i = 0; i < ks.length; i++) {
            groupNodes[ks[i]] = groupNodes[ks[i]] ? groupNodes[ks[i]].concat(groupNodes[k]) : [].concat(groupNodes[k]);
            if (muliDisplayField && key2Display[ks[i]] == undefined) {
              //获取分类对应的显示值
              key2Display[ks[i]] = (function () {
                var splitDisplay = groupNodes[k][0][treeNodeFields[index]['nodeFieldDisplay']].split(';');
                return splitDisplay.length >= i ? splitDisplay[i] : ks[i];
              })();
            }
          }
          delete groupNodes[k];
        } else if (k != '_GRP_UNKNOWN') {
          key2Display[k] = $.isEmptyObject(groupNodes[k][0][treeNodeFields[index]['nodeFieldDisplay']]) ?
            k :
            groupNodes[k][0][treeNodeFields[index]['nodeFieldDisplay']];
        }
      }
    }

    var parentDataIndex = index == 0 ? null : rowDataParam.rowIndex - 1;
    for (var k in groupNodes) {
      if (k === '_GRP_UNKNOWN') {
        //未分类到的数据，也需要添加到节点列表
        nodeList = nodeList.concat(groupNodes[k]);
        $.each(groupNodes[k], function (i, ni) {
          var treeNode = $.extend({}, ni);
          treeNode._value = ni[idField];
          treeNode._treeNodeLevel = index;
          treeNode._hasChildren = false;
          treeNode._parentDataIndex = parentDataIndex;
          rowDataParam.rowIndex++;
          lineRows.push(treeNode);
        });
        continue;
      }

      //构造节点表格行数据
      var node = {};
      //node[idField] = _.uniqueId("TreeNode_");//树节点的ID
      node[displayField] = (function () {
        //获取树节点显示名称
        if (treeNodeFields[index]['nodeFieldDisplay']) {
          if (treeNodeFields[index]['multiNodeData'] != 1) {
            return groupNodes[k][0][treeNodeFields[index]['nodeFieldDisplay']];
          }

          if (muliDisplayField) {
            return key2Display[k];
          }
        }
        return k;
      })();

      if (index == treeNodeFields.length - 1) {
        //最后一级分类，把数据都放到最后一级节点的子集
        nodeList.push(node);
        node._treeNodeLevel = index;
        node._value = k;
        node._nodeType = treeNodeFields[index]['nodeField'];
        if (parentIdField) {
          //末级数据按父级字段构造树
          var childTreeNodes = buildTreeNodeByParentField(groupNodes[k], parentIdField, idField, rowDataParam.rowIndex);
          node.children = [].concat(childTreeNodes.treeRows);
        } else {
          node.children = [].concat(groupNodes[k]);
        }
        node._hasChildren = node.children.length > 0;
        node._parentDataIndex = parentDataIndex;
        rowDataParam.rowIndex++;
        lineRows.push(node);
        if (parentIdField) {
          treeRows2LineRows(childTreeNodes.treeRows, lineRows, rowDataParam, index + 1);
        } else {
          var pid = rowDataParam.rowIndex - 1;
          if (existFieldMultiType) {
            //存在字段多分类的情况，需要进行排序，以符合初始化的排序
            node.children = _.sortBy(node.children, '_seq');
          }
          $.each(node.children, function (i, ni) {
            var treeNode = $.extend({}, ni);
            rowDataParam.rowIndex++;
            treeNode._value = ni[idField];
            treeNode._treeNodeLevel = index + 1;
            treeNode._hasChildren = false;
            treeNode._parentDataIndex = pid;
            lineRows.push(treeNode);
          });
        }
        node._endChildIndex = rowDataParam.rowIndex - 1;
        continue;
      }
      rowDataParam.rowIndex++;
      lineRows.push(node);
      node.children = [].concat(groupByNodeFields(treeNodeFields, groupNodes[k], index + 1, idField, displayField, lineRows, rowDataParam));
      node._treeNodeLevel = index;
      node._value = k;
      node._nodeType = treeNodeFields[index]['nodeField'];
      node._hasChildren = node.children.length > 0;
      node._parentDataIndex = parentDataIndex;
      if (node._hasChildren) {
        node._endChildIndex = rowDataParam.rowIndex - 1;
      }
      nodeList.push(node);
    }
    return nodeList;
  }

  function treeRows2LineRows(treeRows, lineRows, rowDataParam, nodeLevel) {
    var parentDataIndex = nodeLevel == 0 ? null : rowDataParam.rowIndex - 1;
    for (var i = 0, len = treeRows.length; i < len; i++) {
      var node = $.extend({}, treeRows[i]);
      node._treeNodeLevel = nodeLevel;
      node._parentDataIndex = parentDataIndex;
      lineRows.push(node);
      rowDataParam.rowIndex++;
      if (treeRows[i].children && treeRows[i].children.length > 0) {
        node._hasChildren = true;
        treeRows2LineRows(treeRows[i].children, lineRows, rowDataParam, nodeLevel + 1);
        node._endChildIndex = rowDataParam.rowIndex - 1;
      }
    }
  }

  /**
   * 设置事件
   */
  function setEvent(that) {
    /**
     * 展开、折叠逻辑事件
     */
    that.$el.off('expandToggle', '.tree-icon').on('expandToggle', '.tree-icon', function (e, expand) {
      var isExpand = expand == undefined || expand;
      var treeIconClass = isExpand ? that.options.expandIcon : that.options.collapseIcon;
      $(this).attr('class', 'tree-icon ' + treeIconClass);
      $(this).attr('title', isExpand ? '折叠' : '展开');
      var beginIndex = parseInt($(this).parents('tr').attr('data-index'));
      if (!isExpand) {
        //折叠
        var endIndex = parseInt($(this).attr('end-child-index'));
        for (var i = beginIndex + 1; i <= endIndex; i++) {
          that.$el.bootstrapTable('hideRow', {
            index: i
          });
          var $treeIcon = that.$el.find('tr[data-index=' + i + ']').find('.tree-icon');
          if ($treeIcon.length > 0) {
            $treeIcon.attr('class', 'tree-icon ' + treeIconClass);
          }
        }
      } else {
        //只展开直接子节点
        $('tr[parent-data-index=' + beginIndex + ']', that.$el).each(function () {
          that.$el.bootstrapTable('showRow', {
            index: parseInt($(this).attr('data-index'))
          });
        });
      }
    });

    /*that.$el.off('click', '.tree-icon').on('click', '.tree-icon', function (e) {
            // $(this).trigger('expandToggle', true);
            if ($(this).hasClass(that.options.collapseIcon)) {
                //展示子节点
                $(this).trigger('expandToggle');
            } else {
                //隐藏子节点
                $(this).trigger('expandToggle', false);
            }

            that.$el.bootstrapTable('resetView');
            e.stopPropagation();

        });*/

    //全部展开
    that.$container
      .find('.allTreeNodeExpand')
      .off('click')
      .on('click', function () {
        $('.tree-icon')
          .filter("[class*='" + that.options.collapseIcon + "']")
          .trigger('expandToggle');
        that.$el.bootstrapTable('resetView');
      });

    //全部折叠
    that.$container
      .find('.allTreeNodeCollapse')
      .off('click')
      .on('click', function () {
        $('.tree-icon')
          .filter("[class*='" + that.options.expandIcon + "']")
          .trigger('expandToggle', false);
        that.$el.bootstrapTable('resetView');
      });

    var cascadeCheckOrUnCheckNodes = function (method, row, $element) {
      if (row._endChildIndex != undefined) {
        that.$el.find('tr[parent-data-index=' + parseInt($element.attr('data-index')) + ']').each(function () {
          that.$el.bootstrapTable(method, parseInt($(this).attr('data-index')));
        });
      }
    };

    if (that.options.treeCascadeCheck) {
      //勾选父节点，级联勾选子节点
      that.$el.on('check.bs.table', function (e, row, $element) {
        cascadeCheckOrUnCheckNodes('check', row, $element);
      });
      //反选父节点，级联反选子节点
      that.$el.on('uncheck.bs.table', function (e, row, $element) {
        cascadeCheckOrUnCheckNodes('uncheck', row, $element);
      });
    }
  }

  /*function TestData() {
        var data = [];
        for (var i = 1; i <= 1000; i++) {
            data.push({
                UUID: i,
                TITLE: '节点_' + i,
                YEAR_NUM: _.random(2000, 2018) + "年",
                MONTH_NUM: _.random(1, 12) + "月份",
                MOOK_TYPE: _.random(1, 5) + "类型"
            });
        }
        return data;
    }*/

  /**
   * 切换到树形结构列表
   */
  BootstrapTable.prototype.toggleTreeView = function () {
    var that = this;
    var idField = that.options.idField;
    if (that.options.treeView) {
      BootstrapTable.prototype.initBody = _initBody;
      that.options.treeView = false;
      this.$el.bootstrapTable('refresh');
      that.$el.trigger('post-tree-body.bs.table', [false]); //触发树形表格渲染完成事件
      delete that.options.postBody2TreeView; //转换完，删除该属性，避免死循环
      return;
    }
    that.options.treeView = true; //表格处于树形结构化形态中

    if (!idField) {
      throw new Error('表格列定义未指定主键列！');
    }

    that.$el.bootstrapTable('showLoading');

    if (that.options.cardView) {
      that.options.cardView = false;
      this.initHeader();
    }

    var data = [].concat(
      (function () {
        if (that.options.allPageDataTreeFormate) {
          var wBtableWidget = that.$container.parent().data('uiWBootstrapTable');
          var wFileWidget = that.$container.parent().data('uiWFileLibrary'); // 文件目录管理器的数据
          var wWidget = wBtableWidget || wFileWidget;

          if (wWidget) {
            var params = JSON.parse(JSON.stringify(wWidget.getDataProvider().getParams()));
            delete params.pagingInfo;
            var allData = [];
            server.JDS.call({
              service: 'cdDataStoreService.loadData',
              data: [params],
              async: false,
              success: function (result) {
                allData = result.data.data;
              }
            });
            return allData;
          }
        }
        return that.$el.bootstrapTable('getData');
      })()
    );

    var treeRows = []; //树形结构数据，按父-子层级
    var lineRows = []; //线性结构数据，按父-子顺序
    //二开脚本进行树形结构的数据封装返回
    if (that.$container.parent().data('uiWBootstrapTable')) {
      that.$container
        .parent()
        .data('uiWBootstrapTable')
        .invokeDevelopmentMethod('loadTreeNodeData', [
          that.data,
          that.options,
          function (rows) {
            treeRows = rows;
          }
        ]);
      treeRows = treeRows || [];
      console.log('调用二开脚本构造树形节点数据：', treeRows);
    }

    //二开脚本未构造树形节点，通过树形配置自动生成树形节点数据
    if ($.isEmptyObject(treeRows)) {
      var now = _.now();
      var treeRowObj = loadTreeNodeData(that, data);
      console.log('构造树形节点数据耗时：[%s]ms，数据：', _.now() - now, treeRowObj);
      lineRows = treeRowObj.lineRows;
    }

    //绑定事件
    setEvent(that);

    if (!$.isEmptyObject(lineRows)) {
      var now = _.now();
      batchInsertRow(that, lineRows);
      console.log('插入表格耗时[%s]ms', _.now() - now);
    } else if (!$.isEmptyObject(treeRows)) {
      var now = _.now();
      lineRows = [];
      treeRows2LineRows(treeRows, lineRows, {
        rowIndex: 0
      }, 0);
      batchInsertRow(that, lineRows);
      console.log('插入表格耗时[%s]ms', _.now() - now);
    }
    that.$el.bootstrapTable('hideLoading');
    that.$el.trigger('post-tree-body.bs.table', [true]); //触发树形表格渲染完成事件
    delete that.options.postBody2TreeView; //转换完，删除该属性，避免死循环
  };

  var BootstrapTableMethodOverride = function () {
    /**
     * 重写bootstrapTable的表格html初始化，增加树节点需要的参数
     * @param fixedScroll
     */
    BootstrapTable.prototype.initBody = function (fixedScroll) {
      var that = this,
        html = [],
        data = this.getData();

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
      var firstTreeNodeIndexRange = [];
      for (var i = this.pageFrom - 1; i < this.pageTo; i++) {
        var key,
          item = data[i],
          style = {},
          csses = [],
          data_ = '',
          attributes = {},
          htmlAttributes = [];

        style = $.fn.bootstrapTable.utils.calculateObjectValue(this.options, this.options.rowStyle, [item, i], style);

        if (style && style.css) {
          for (key in style.css) {
            csses.push(key + ': ' + style.css[key]);
          }
        }

        attributes = $.fn.bootstrapTable.utils.calculateObjectValue(this.options, this.options.rowAttributes, [item, i], attributes);

        if (attributes) {
          for (key in attributes) {
            htmlAttributes.push($.fn.bootstrapTable.utils.sprintf('%s="%s"', key, escapeHTML(attributes[key])));
          }
        }

        if (item._data && !$.isEmptyObject(item._data)) {
          $.each(item._data, function (k, v) {
            // ignore data-index
            if (k === 'index') {
              return;
            }
            data_ += $.fn.bootstrapTable.utils.sprintf(' data-%s="%s"', k, v);
          });
        }
        if (
          that.options.initTreeShowStyle == 'FIRST_EXPAND' &&
          firstTreeNodeIndexRange.length == 0 &&
          item._hasChildren &&
          item._endChildIndex > 0
        ) {
          firstTreeNodeIndexRange = [i, item._endChildIndex]; //确定第一个节点的节点下标范围，起始-结束的子节点范围
        }
        html.push(
          '<tr',
          $.fn.bootstrapTable.utils.sprintf(' %s', htmlAttributes.join(' ')),
          $.fn.bootstrapTable.utils.sprintf(' id="%s"', $.isArray(item) ? undefined : item._id),
          $.fn.bootstrapTable.utils.sprintf(' class="%s"', style.classes || ($.isArray(item) ? undefined : item._class)),
          $.fn.bootstrapTable.utils.sprintf(' data-index="%s"', i),
          $.fn.bootstrapTable.utils.sprintf(' uuid="%s"', item._value),
          $.fn.bootstrapTable.utils.sprintf(' node-type="%s"', item._nodeType),
          item._parentDataIndex != undefined ? $.fn.bootstrapTable.utils.sprintf(' parent-data-index="%s"', item._parentDataIndex) : '',
          $.fn.bootstrapTable.utils.sprintf(' data-uniqueid="%s"', item[this.options.uniqueId]),
          $.fn.bootstrapTable.utils.sprintf(
            ' style="display:%s"',
            (function () {
              //计算子节点是否折叠展开
              if (item._treeNodeLevel > 0) {
                //全部折叠或者仅展示第一个节点类型时候非第一节点，则隐藏表格行
                if (
                  that.options.initTreeShowStyle == 'COLLAPSE_ALL' ||
                  (that.options.initTreeShowStyle == 'FIRST_EXPAND' &&
                    firstTreeNodeIndexRange.length == 2 &&
                    i < firstTreeNodeIndexRange[0]) ||
                  i > firstTreeNodeIndexRange[1]
                ) {
                  return 'none;';
                }
              }
              return 'table-row;';
            })()
          ),
          $.fn.bootstrapTable.utils.sprintf('%s', data_),
          '>'
        );

        if (this.options.cardView) {
          html.push($.fn.bootstrapTable.utils.sprintf('<td colspan="%s"><div class="card-views">', this.header.fields.length));
        }

        if (!this.options.cardView && this.options.detailView) {
          html.push(
            '<td>',
            '<a class="detail-icon" href="javascript:">',
            $.fn.bootstrapTable.utils.sprintf('<i class="%s %s"></i>', this.options.iconsPrefix, this.options.icons.detailOpen),
            '</a>',
            '</td>'
          );
        }
        var treeIconRender = false;
        $.each(this.header.fields, function (j, field) {
          var text = '',
            value = $.fn.bootstrapTable.utils.getItemField(item, field, that.options.escape),
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

          style = $.fn.bootstrapTable.utils.sprintf('style="%s"', csses.concat(that.header.styles[j]).join('; '));

          // handle td's id and class
          if (item['_' + field + '_id']) {
            id_ = $.fn.bootstrapTable.utils.sprintf(' id="%s"', item['_' + field + '_id']);
          }
          if (item['_' + field + '_class']) {
            class_ = $.fn.bootstrapTable.utils.sprintf(' class="%s"', item['_' + field + '_class']);
          }
          if (item['_' + field + '_rowspan']) {
            rowspan_ = $.fn.bootstrapTable.utils.sprintf(' rowspan="%s"', item['_' + field + '_rowspan']);
          }
          if (item['_' + field + '_colspan']) {
            colspan_ = $.fn.bootstrapTable.utils.sprintf(' colspan="%s"', item['_' + field + '_colspan']);
          }
          if (item['_' + field + '_title']) {
            title_ = $.fn.bootstrapTable.utils.sprintf(' title="%s"', item['_' + field + '_title']);
          }
          cellStyle = $.fn.bootstrapTable.utils.calculateObjectValue(
            that.header,
            that.header.cellStyles[j],
            [value, item, i, field],
            cellStyle
          );
          if (cellStyle.classes) {
            class_ = $.fn.bootstrapTable.utils.sprintf(' class="%s"', cellStyle.classes);
          }
          if (cellStyle.css) {
            var csses_ = [];
            for (var key in cellStyle.css) {
              csses_.push(key + ': ' + cellStyle.css[key]);
            }
            style = $.fn.bootstrapTable.utils.sprintf('style="%s"', csses_.concat(that.header.styles[j]).join('; '));
          }

          value = $.fn.bootstrapTable.utils.calculateObjectValue(column, that.header.formatters[j], [value, item, i], value);

          //树状列表的操作列，需要区分数据行与分类行，分类行不展示按钮操作
          if (column.field == 'lineEnderToolbar' && (item[that.options.idField] == undefined || item[that.options.idField] == null)) {
            value = '';
          }

          if (item['_' + field + '_data'] && !$.isEmptyObject(item['_' + field + '_data'])) {
            $.each(item['_' + field + '_data'], function (k, v) {
              // ignore data-index
              if (k === 'index') {
                return;
              }
              data_ += $.fn.bootstrapTable.utils.sprintf(' data-%s="%s"', k, v);
            });
          }

          if (column.checkbox || column.radio) {
            type = column.checkbox ? 'checkbox' : type;
            type = column.radio ? 'radio' : type;

            text = [
              $.fn.bootstrapTable.utils.sprintf(
                that.options.cardView ? '<div class="card-view %s">' : '<td class="bs-checkbox %s">',
                column['class'] || ''
              ),
              '<input' +
              $.fn.bootstrapTable.utils.sprintf(' data-index="%s"', i) +
              $.fn.bootstrapTable.utils.sprintf(' name="%s"', that.options.selectItemName) +
              $.fn.bootstrapTable.utils.sprintf(' type="%s"', type) +
              $.fn.bootstrapTable.utils.sprintf(' value="%s"', item[that.options.idField]) +
              $.fn.bootstrapTable.utils.sprintf(' checked="%s"', value === true || (value && value.checked) ? 'checked' : undefined) +
              $.fn.bootstrapTable.utils.sprintf(
                ' disabled="%s"',
                !column.checkboxEnabled || (value && value.disabled) ? 'disabled' : undefined
              ) +
              ' />',
              that.options.clickToSelect ? '<label></label>' : '<label onclick="$(this).prev().click();"></label>',
              that.header.formatters[j] && typeof value === 'string' ? value : '',
              that.options.cardView ? '</div>' : '</td>'
            ].join('');

            item[that.header.stateField] = value === true || (value && value.checked);
          } else {
            value = typeof value === 'undefined' || value === null ? that.options.undefinedText : value;

            if (that.options.treeView && column.field !== 'sequenceIndex' && !treeIconRender) {
              var isCollapseIcon = false;
              if (
                that.options.initTreeShowStyle == 'COLLAPSE_ALL' ||
                (that.options.initTreeShowStyle == 'FIRST_EXPAND' &&
                  firstTreeNodeIndexRange.length == 2 &&
                  i < firstTreeNodeIndexRange[0]) ||
                i > firstTreeNodeIndexRange[1]
              ) {
                isCollapseIcon = true;
              }
              var $paddingLeft = $('<span>');
              $paddingLeft.css('padding-left', 15 * parseInt(item._treeNodeLevel));
              var $icon = item._hasChildren ?
                $('<span>', {
                  class: 'tree-icon ' + (isCollapseIcon ? that.options.collapseIcon : that.options.expandIcon),
                  'end-child-index': parseInt(item._endChildIndex),
                  title: isCollapseIcon ? '展开' : '折叠',
                  style: 'margin-right:5px;padding:2px 6px;'
                }) :
                $('<span>', {
                  class: '',
                  style: 'padding-left:30px;'
                });
              //树形结构
              value = $paddingLeft[0].outerHTML + $icon[0].outerHTML + value;
              treeIconRender = true;
            }

            text = that.options.cardView ? [
              '<div class="card-view">',
              that.options.showHeader ?
              $.fn.bootstrapTable.utils.sprintf(
                '<span class="title" %s>%s</span>',
                style,
                (function () {
                  var result = '';
                  $.each(that.columns, function (i, item) {
                    if (item['field'] === field) {
                      result = item['title'];
                      return false;
                    }
                    return true;
                  });
                  return result;
                })()
              ) :
              '',
              $.fn.bootstrapTable.utils.sprintf('<span class="value">%s</span>', value),
              '</div>'
            ].join('') : [
              $.fn.bootstrapTable.utils.sprintf('<td%s %s %s %s %s %s %s>', id_, class_, style, data_, rowspan_, colspan_, title_),
              value,
              '</td>'
            ].join('');

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
      }

      // show no records
      if (!html.length) {
        html.push(
          '<tr class="no-records-found">',
          $.fn.bootstrapTable.utils.sprintf('<td colspan="%s">%s</td>', this.$header.find('th').length, this.options.formatNoMatches()),
          '</tr>'
        );
      }

      this.$body.html(html.join(''));

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
            field = fields[that.options.detailView && !that.options.cardView ? index - 1 : index],
            column = that.columns[$.fn.bootstrapTable.utils.getFieldIndex(that.columns, field)],
            value = $.fn.bootstrapTable.utils.getItemField(item, field, that.options.escape);

          if ($td.find('.detail-icon').length) {
            return;
          }

          that.trigger(e.type === 'click' ? 'click-cell' : 'dbl-click-cell', field, value, item, $td);
          that.trigger(e.type === 'click' ? 'click-row' : 'dbl-click-row', item, $tr, field);

          // if click to select - then trigger the checkbox/radio click
          if (e.type === 'click' && that.options.clickToSelect && column.clickToSelect) {
            var $selectItem = $tr.find($.fn.bootstrapTable.utils.sprintf('[name="%s"]', that.options.selectItemName));
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
            $this
              .find('i')
              .attr('class', $.fn.bootstrapTable.utils.sprintf('%s %s', that.options.iconsPrefix, that.options.icons.detailOpen));
            $tr.next().remove();
            that.trigger('collapse-row', index, row);
          } else {
            $this
              .find('i')
              .attr('class', $.fn.bootstrapTable.utils.sprintf('%s %s', that.options.iconsPrefix, that.options.icons.detailClose));
            $tr.after($.fn.bootstrapTable.utils.sprintf('<tr class="detail-view"><td colspan="%s"></td></tr>', $tr.find('td').length));
            var $element = $tr.next().find('td');
            var content = $.fn.bootstrapTable.utils.calculateObjectValue(
              that.options,
              that.options.detailFormatter,
              [index, row, $element],
              ''
            );
            if ($element.length === 1) {
              $element.append(content);
            }
            that.trigger('expand-row', index, row, $element);
          }
          that.resetView();
        });

      this.$body
        .find('> tr[data-index] > td > .tree-icon')
        .off('click')
        .on('click', function (e) {
          // $(this).trigger('expandToggle', true);
          if ($(this).hasClass(that.options.collapseIcon)) {
            //展示子节点
            $(this).trigger('expandToggle');
          } else {
            //隐藏子节点
            $(this).trigger('expandToggle', false);
          }

          that.resetView();
          e.preventDefault();
          e.stopPropagation();
        });

      this.$selectItem = this.$body.find($.fn.bootstrapTable.utils.sprintf('[name="%s"]', this.options.selectItemName));
      this.$selectItem.off('change').on('change', function (event) {
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
          events = $.fn.bootstrapTable.utils.calculateObjectValue(null, events);
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
  };

  function expandOrCollapseTree(dataIndex, isExpand) {
    isExpand = isExpand == undefined || isExpand;
    if (dataIndex != undefined) {
      //展开某个节点
      var $tr = this.$el.find('tr[data-index=' + dataIndex + ']');
      if ($tr.length > 0) {
        var $treeIcon = $tr.find('.tree-icon');
        if ($treeIcon.length > 0) {
          $treeIcon.trigger('expandToggle', isExpand);
          this.$el.bootstrapTable('resetView');
        }
      }
      return;
    }
    //展开全部
    this.$el
      .find('.tree-icon')
      .filter("[class*='" + (isExpand ? this.options.collapseIcon : this.options.expandIcon) + "']")
      .trigger('expandToggle', isExpand);
    this.$el.bootstrapTable('resetView');
  }

  /**
   * 展开树形视图
   * @param dataIndex  null或者undefined，则全部展开
   */
  BootstrapTable.prototype.expandTreeView = function (dataIndex) {
    expandOrCollapseTree.call(this, dataIndex, true);
  };

  /**
   * 折叠树形视图
   * @param dataIndex null或者undefined，则全部折叠
   */
  BootstrapTable.prototype.collapseTreeView = function (dataIndex) {
    expandOrCollapseTree.call(this, dataIndex, false);
  };

  $.fn.bootstrapTable.methods.push('toggleTreeView'); //切换树形结构表格方法
  $.fn.bootstrapTable.methods.push('expandTreeView'); //展开，参数tr上的data-index，如果不传，默认全部展开
  $.fn.bootstrapTable.methods.push('collapseTreeView'); //折叠，参数tr上的data-index，如果不传，默认全部折叠

  // 给组件增加默认参数列表
  $.extend($.fn.bootstrapTable.defaults, {
    treeNodeFields: [], //树节点字段
    treeNodeDisplayField: 'name', //节点展示字段
    treeBuildType: 'parentField', //树节点构造方式
    treeView: false, // treeView视图
    initTreeShowStyle: 'EXPAND_ALL', //默认全部展开
    treeCascadeCheck: false, //默认不级联勾选/反选子节点
    idField: 'id', // id,节点Id（继承自bootstrap-table）
    parentIdField: 'parentId', //parentId, 父节点ID
    collapseIcon: 'glyphicon glyphicon-chevron-right', // 折叠样式
    expandIcon: 'glyphicon glyphicon-chevron-down', // 展开样式
    allPageDataTreeFormate: false //默认当前页数据进行树形结构化
  });
});
