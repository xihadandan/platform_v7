/**
 * 1、值的回填，主要针对单选。 2、如果为ajax取值，默认值可以通过initSelection函数设置，参考下文initSelection方法。
 * 或者通过触发change事件设置值，如：$("#tableDefinitionText").val("something").trigger("change");
 * 3、queryMethod
 * 参数说明：查询下拉数据时的方法，方法接收一个参数Select2QueryInfo，具体参考Select2QueryApi.loadSelectData的定义
 * 4、selectionMethod
 * 参数说明：根据数据IDS返回Text，方法接收一个参数Select2QueryInfo。具体参考Select2QueryApi.loadSelectDataByIds的定义
 */
(function (factory) {
  'use strict';
  if (typeof define === 'function' && define.amd) {
    // AMD. Register as an anonymous module.
    define(['jquery', 'wellSelect'], factory);
  } else {
    // Browser globals
    // $('head').append('<link rel="stylesheet" href="/static/js/jquery.wellSelect/wellSelect.css">');
    $('body').append('<script src="/static/js/jquery.wellSelect/wellSelect.js"></script>');
    factory(jQuery);
  }
})(function ($) {
  'use strict';

  function getCookie(name) {
    var arr,
      reg = new RegExp('(^| )' + name + '=([^;]*)(;|$)');
    if ((arr = document.cookie.match(reg))) {
      return unescape(arr[2]);
    } else {
      return '';
    }
  }
  $.fn.select2 = function (options) {
    return $(this).wellSelect(options);
  };
  $.fn.wSelect2 = function (options) {
    if (typeof arguments[0] === 'string') {
      if (arguments.length > 1) {
        return $(this).wellSelect(arguments[0], arguments[1]);
      } else {
        return $(this).wellSelect(arguments[0]);
      }
    }
    var markMatch = Select2.util.markMatch;
    var defaultOptions = {
      queryMethod: '', // 查询下拉数据时的方法，配置该参数，则对应service无需继承Select2QueryApi
      selectionMethod: '', // 根据数据IDS返回Text，配置该参数，则对应service无需继承Select2QueryApi
      params: {}, // ajax向后台透传的参数
      queryCallBack: null,
      pageSize: options && options.searchable === false ? 1000 : 20,
      separator: ';', // 多选时的分割符号
      defaultBlank: true, // 默认空选项
      defaultBlankText: '', // 默认空选项的显示值
      defaultBlankValue: '',
      remoteSearch: true, // 是否远程查询.远程查询时，每次打开和查询都会带条件到后台查询，非远程查询一次将所有数据拉取到前端赋值给option.data，不在访问后端。
      valueField: null, // 下拉框值回填input的ID
      labelField: null,
      enableAdd: false,
      enableDel: false,
      enableUpdate: false,
      async: false,
      container: $('body'),
      defaultUpdateJson: {
        text: '添加'
      },
      onSelect2del: function (event, callback) {
        // 修改、校验
        // 返回id或者修改event.val,有必要的话可以修改event.object(text不可修改，会被覆盖)
        var id = event.val;
        var object = event.object;
        if (confirm('删除' + object.text)) {
          // 可以调用callback(event),return false,
          return id;
        } else {
          // id必填
          return false;
        }
      },
      onSelect2add: function (event, callback) {
        // 修改、校验
        // 返回text或者修改event.val,有必要的话可以修改event.object(text不可修改，会被覆盖)
        var text = event.val;
        var object = event.object;
        text = prompt('添加', text);
        if ($.trim(text).length > 0) {
          // 可以调用callback(event),return false,
          return text;
        } else {
          // text必填
          return false;
        }
      },
      onSelect2delSuccess: function (data) {
        var id = data.id;
        var text = data.text;
      },
      onSelect2addSuccess: function (data) {
        var id = data.id;
        var text = data.text;
      },
      formatResult: function (result, container, query, escapeMarkup) {
        var self = this;
        var markup = [];
        var defaultUpdateJson = self.defaultUpdateJson;
        var id = self.id(result),
          text = self.text(result);
        if (result === defaultUpdateJson) {
          var term = $.trim(query.term);
          if (term.length <= 0) {
            // 空值忽略添加
            return; // return undefined
          }
          var matchs = $(self.element).find('.select2-result');
          for (var i = 0; i < matchs.length; i++) {
            var data = $(matchs[i]).data('select2-data');
            if (data && self.text(data) === term) {
              return;
            }
          }
          markup.push('<span class="select2-input-text" style="color:#3875d7">');
          markup.push(term);
          markup.push('</span>');
          markup.push('<span class="select2-button select2-button-add" style="float:right;">');
          markup.push(text);
          markup.push('</span>');
          // one防止多次点击
          container.on('mouseup', '.select2-button-add', function (event) {
            // val = text
            if (container.lock) {
              return;
            }
            container.lock = true;
            try {
              var jsonObject = $.extend({}, result);
              jsonObject.text = term;
              var evt = $.Event('Select2add', {
                val: term,
                object: jsonObject,
                oEvent: event
              });
              $(self.element).trigger(evt);
              event.preventDefault();
              event.stopPropagation();
              return false;
            } finally {
              delete container.lock;
            }
          });
        } else {
          markMatch(text, query.term, markup, escapeMarkup);
          if ((options.enableUpdate || options.enableDel) && $.trim(id).length > 0) {
            // one防止多次点击
            container.on('mouseup', '.select2-button-del', function (event) {
              // val = id
              if (container.lock) {
                return;
              }
              container.lock = true;
              try {
                var jsonObject = $.extend({}, result);
                var evt = $.Event('Select2del', {
                  val: id,
                  object: result,
                  oEvent: event
                });
                $(self.element).trigger(evt);
                event.preventDefault();
                event.stopPropagation();
                return false;
              } finally {
                delete container.lock;
              }
            });
            markup.push("<span class='select2-button select2-button-del' style='float:right;'>删除</span>");
          }
        }
        return markup.join('');
      },
      theme: 'classic'
      // 下拉框展示值回填input的ID
    };
    // 关闭
    var nextSearchTerm = function (selectedObject, currentSearchTerm) {
      return currentSearchTerm;
    };
    var options = $.extend(defaultOptions, options);
    if (options.multiple) {
      $.extend(defaultOptions, {
        closeOnSelect: false,
        nextSearchTerm: nextSearchTerm
        // 返回下一次搜索的searchText，多选默认为当前搜索。即不清空搜索值。
      });
    }
    var _this = this;
    if (!options.data && options.serviceName) {
      // options.remoteSearch = false;
      if (!options.remoteSearch) {
        var params = options.params;
        if ($.isFunction(params)) {
          params = params.call(this);
        }
        $.ajax({
          type: 'POST',
          url: '/common/select2/query',
          dataType: 'json',
          async: options.async,
          data: $.extend(
            {
              serviceName: options.serviceName,
              queryMethod: options.queryMethod,
              pageSize: 1000,
              pageNo: 1
            },
            params
          ),
          success: function (result) {
            if (options.defaultBlank && !options.multiple) {
              result.results.unshift({
                id: options.defaultBlankValue,
                text: options.defaultBlankText
              });
            }
            if (options.enableAdd || options.enableDel || options.enableUpdate) {
              // 本地数据(非搜索)不支持修改
              options.enableAdd = false;
              options.enableDel = false;
              options.enableUpdate = false;
            }
            options.data = result.results;

            if (options.queryCallBack) {
              options.queryCallBack();
            }
            $(_this).wellSelect(options);
          }
        });
      } else {
        options.ajax = $.extend(
          {
            type: 'POST',
            url: '/common/select2/query', // 地址
            dataType: 'json', // 接收的数据类型
            data: function (term, pageNo) {
              // 在查询时向服务器端传输的数据
              term = $.trim(term);
              var params = options.params;
              if ($.isFunction(params)) {
                params = params.call(this);
              }
              return $.extend(
                {
                  searchValue: term,
                  serviceName: options.serviceName,
                  queryMethod: options.queryMethod,
                  pageSize: options.pageSize,
                  pageNo: pageNo
                },
                params
              );
            },
            results: function (data, pageNo, query) {
              var results = data.results;
              if (options.defaultBlank && !options.multiple) {
                results.unshift({
                  id: options.defaultBlankValue,
                  text: options.defaultBlankText
                });
              }
              if ((options.enableUpdate || options.enableAdd) && $.trim(query.term).length > 0) {
                var isExist = false;
                for (var i = 0; i < results.length; i++) {
                  var result = results[i];
                  if (result.text === query.term) {
                    isExist = true;
                    break;
                  }
                }
                // 已经存在（不添加）
                if (isExist === false) {
                  data.results.push(options.defaultUpdateJson);
                }
              }
              return data;
            }
          },
          options.ajax
        );
      }
    } else if (options.data && options.defaultBlank) {
      options.data = [].concat(options.data);
      if (!options.multiple) {
        options.data.unshift({
          id: options.defaultBlankValue,
          text: options.defaultBlankText
        });
      }
      if (options.enableAdd || options.enableDel || options.enableUpdate) {
        // 本地数据(非搜索)不支持修改
        options.enableAdd = false;
        options.enableDel = false;
        options.enableUpdate = false;
        // options.data.push(options.defaultUpdateJson);
      }
    }
    if (options.data || options.serviceName) {
      options.initSelection = function (element, callback) {
        var ids = element.val();
        if (options.valueField) {
          ids = $('#' + options.valueField, options.container).val();
        }
        if (ids) {
          if (options.multiple) {
            ids = ids.split(options.separator);
          } else {
            ids = [ids];
          }
          var data = [];
          if (options.data) {
            for (var j = 0; j < ids.length; j++) {
              var id = ids[j];
              for (var i = 0; i < options.data.length; i++) {
                if (options.data[i].id == id) {
                  var text = options.data[i].text;
                  data.push({
                    id: id,
                    text: text
                  });
                  break;
                }
              }
            }
          } else {
            if (options.serviceName) {
              var params = options.params;
              if ($.isFunction(params)) {
                params = params.call(this);
              }
              $.ajax({
                type: 'POST',
                url: '/common/select2/selection',
                dataType: 'json',
                async: false,
                data: $.extend(
                  {
                    serviceName: options.serviceName,
                    selectionMethod: options.selectionMethod,
                    pageSize: 1000,
                    pageNo: 1,
                    selectIds: ids
                  },
                  params
                ),
                success: function (result) {
                  data = $.map(result.results, function (item) {
                    if (ids.indexOf(item.id) > -1) {
                      return item;
                    }
                  });
                }
              });
            }
          }

          if (!options.multiple) {
            data = data[0];
          }
          if (data) {
            // add by wujx 20160714 begin
            addSelect2ChosenTitle(element, data.text);
            if (options.labelField) {
              $('#' + options.labelField, options.container).val(data.text);
            }
            // add by wujx 20160714 end

            callback(data);
          } else {
            // 修复wellSelect每次选择都会调用initSelection造成数据显示被清空
            if (ids == null || ids.length == 0) {
              callback({
                id: ids,
                text: ''
              });
            }
          }
        } else {
          callback({
            id: '',
            text: ''
          });
        }
      };
    }

    // $(this).select2(options);
    $(this).wellSelect(options);
    var $select2Obje = $(this);
    $select2Obje.on('select2-close', function () {
      // 多选且nextSearchTerm为默认配置（即返回当前搜索值）时，关闭时清空下一次搜索得字符串，使再次打开的时候，不会保留上次打开的搜索信息。
      if (options.multiple && options.nextSearchTrm == nextSearchTerm) {
        $(this).data('select2').nextSearchTerm = '';
      }
    });
    $select2Obje.on('change', function () {
      var _data = $select2Obje.wellSelect('data');
      if (_data) {
        var id = '';
        var text = '';
        if (options.multiple) {
          for (var i = 0; i < _data.length; i++) {
            var obj = _data[i];
            if (i > 0) {
              id = id + options.separator;
              text = text + options.separator;
            }
            id = id + obj.id;
            text = text + obj.text;
          }
        } else {
          id = _data.id;
          text = _data.text;
        }
        if (options.valueField) {
          $('#' + options.valueField, options.container).val(id);
        }
        if (options.labelField) {
          text = typeof text === 'string' ? text : text();
          $('#' + options.labelField, options.container).val(text);
        }
      }

      //设置失去焦点，触发validator验证
      // $("#" + options.valueField,options.container).blur();
      // $("#" + options.labelField,options.container).blur();
    });
    if (options.enableAdd || options.enableDel || options.enableUpdate) {
      $select2Obje.on('Select2add Select2del', function (event, msg, cb) {
        function callback(event, msg, cb) {
          var cret = false;
          var ajaxOptions = {
            type: 'POST',
            url: '/common/select2/update',
            dataType: 'json',
            async: false,
            data: $.extend(params, {
              params: {},
              serviceName: options.serviceName,
              queryMethod: options.updateMethod
            }),
            success: function (result) {
              return alert('操作成功');
            }
          };
          if (event.type === 'Select2del') {
            msg.id = msg.val;
            $.ajax(
              $.extend(true, ajaxOptions, {
                data: {
                  params: {
                    id: msg.id,
                    json: JSON.stringify(msg)
                  }
                },
                success: function (result) {
                  var select2 = $select2Obje.data('wellSelect');
                  if (select2 && result.results[0]) {
                    var rData = result.results[0];
                    if ($.isFunction(options['on' + event.type + 'Success'])) {
                      var ret = options['on' + event.type + 'Success'].call(this, rData);
                      if (ret === false) {
                        return;
                      }
                      rData = ret || rData;
                    }
                    cb && cb(rData);
                    cret = true;
                  }
                }
              })
            );
          } else if (event.type === 'Select2add') {
            $.ajax(
              $.extend(true, ajaxOptions, {
                data: {
                  params: {
                    text: msg.text,
                    json: JSON.stringify(msg)
                  }
                },
                success: function (result) {
                  var wellSelect = $select2Obje.data('wellSelect');
                  if (wellSelect && result.results[0]) {
                    var rData = result.results[0];
                    if ($.isFunction(options['on' + event.type + 'Success'])) {
                      var ret = options['on' + event.type + 'Success'].call(this, rData);
                      if (ret === false) {
                        return;
                      }
                      rData = ret || rData;
                    }
                    cb && cb(rData);
                    cret = true;
                  }
                }
              })
            );
          }
          return cret;
        }
        // 回调取数据
        if ($.isFunction(options['on' + event.type])) {
          event.object = msg;
          event.val = event.type === 'Select2add' ? msg.text : msg.val;
          var ret = options['on' + event.type].call(this, event, callback);
          if (ret === false) {
            return;
          }
          event.val = ret || event.val;
        }
        callback(event, msg, cb);
      });
    }
    /**
     * 对select2中显示文本span，增加title，鼠标聚焦时会显示全部文本 add by wujx 20160714
     */
    function addSelect2ChosenTitle($select2Obje, text) {
      $select2Obje.parent().find('.select2-chosen').attr('title', text);
    }
    return $(this);
  };
});
