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

(function ($) {
  //定义WellSelect的构造函数
  var WellSelect = function (ele, opt) {
    this.element = ele;
    this.$element = $(ele);
    this.defaults = {
      searchable: true, //是否开启查询
      multiple: false, //是否开启多选
      maxSelectNum: null, //最大可选数
      chooseAll: false, //是否开启全选
      showEmpty: true, //显示空值
      defaultValue: null, //默认值
      placeholder: '请选择', //提示语
      separator: ';', //多选时分隔符 默认';'
      callback: null, //收起下拉列表触发回调事件
      noMatchValueClear: false, //无匹配值时清除
      defaultValueByFirstOption: false, //默认选择第一项
      clearAll: false
      //todo
      // queryMethod: '',// 查询下拉数据时的方法，配置该参数，则对应service无需继承Select2QueryApi
      // selectionMethod: '',// 根据数据IDS返回Text，配置该参数，则对应service无需继承Select2QueryApi
      // params: {},// ajax向后台透传的参数
      // pageSize: options&&options.searchable===false ? 1000 :20,
      // defaultBlank: true,// 默认空选项
      // defaultBlankText: "",// 默认空选项的显示值
      // defaultBlankValue:'',
      // remoteSearch: true,// 是否远程查询.远程查询时，每次打开和查询都会带条件到后台查询，非远程查询一次将所有数据拉取到前端赋值给option.data，不在访问后端。
      // valueField: null,// 下拉框值回填input的ID
      // labelField: null,
    };
    this._options = opt; // 原选项数据
    this.options = $.extend({}, this.defaults, opt);
  };
  //定义wellSelect的方法
  WellSelect.prototype = {
    destroy: function () {
      var _self = this;
      _self.container.remove();
      _self.$element.data('wellSelect', null);
    },
    init: function () {
      var _self = this;
      if (_self.options.tokenSeparators) {
        _self.options.separator = _self.options.tokenSeparators[0];
      }
      _self.container_id = 'wellSelect_' + (_self.options.valueField || 'index_' + $('.well-select').length);
      if (_self.$element.data('wellSelect') !== undefined && _self.$element.data('wellSelect') !== null) {
        _self.$element.data('wellSelect').destroy();
        _self.$element.removeData('wellSelect');
      }
      if (_self.$element.data('wellSelect') === undefined && _self.$element.prev('.well-select').length) {
        _self.$element.prev('.well-select').remove();
      }

      _self.render();
      if (_self.options.tags) {
        _self.renderTagsDom();
      }
      if (_self.options.style) {
        _self.container.find('.well-select-selection').css(_self.options.style);
      }
      _self.bindEvent();
      if (_self.options.source && _self.options.source === 'wSelect') {
        // setTimeout(function () {
        //     if(_self.$element.val()) {
        //         _self.setInitValue();
        //     }
        // },0);
      } else {
        _self.setInitValue();
      }

      if (_self.options.ajax && _self.$element.val()) {
        //当远程获取数据时，如果调用的控件有值则立即获取数据
        _self._ajax();
        _self.setInitValue('ajax');
      }

      if (_self.$element.attr('disabled') === 'disabled') {
        _self.disable(true);
      }

      _self.$element.data('wellSelect', _self);

      _self.$element.on('change.wellSelect', function (e) {
        if (_self.options.initSelection && _self.$element.data('wellSelect-change-triggered') !== true) {
          _self.initSelection();
        }
        _self.$element.data('wellSelect-change-triggered', false);
        //todo
      });
    },
    render: function () {
      var _self = this;
      var data = _self.renderData();
      var selectTypeHtml = '';
      _self.$element.hide();
      if (_self.options.multiple) {
        selectTypeHtml = '<div class="well-tag-list scrollbar"></div>';
      } else {
        selectTypeHtml = '<span class="well-select-selected-value" style="display:none;"></span>';
      }
      var $html = $('<div>', {
        tabindex: '0',
        id: _self.container_id,
        class: 'well-select well-select-default ' +
          (_self.options.multiple ? 'well-select-multiple' : 'well-select-single') +
          ' ' +
          (_self.options.editableClass || ''),
        name: _self.options.labelField
      });
      $html.append(
        '<div class="well-select-selection">' +
        // '<input type="hidden" name="selectResult" value="">' +
        '<div>' +
        selectTypeHtml +
        '<span class="well-select-placeholder">' +
        _self.options.placeholder +
        '</span>' +
        (_self.options.clearAll ? '<span class="well-select-clear iconfont icon-ptkj-shanchu1" style="display:none;"></span>' : '') +
        '<i class="iconfont ' +
        (_self.options.multiple ? 'icon-ptkj-xianmiaoshuangjiantou-xia' : 'icon-ptkj-xianmiaojiantou-xia') +
        ' well-select-arrow"></i>' +
        '</div>' +
        '</div>'
      );
      $html.append(data);
      _self.$element.before($html);
      _self.container = _self.$element.prev();
      _self.initSelectWidth();
      $(window).resize(function () {
        _self.setDropdownWidth();
      });
    },

    initSelectWidth: function () {
      var self = this;
      var _wellSelectWidth = '';
      var $wellSelect = self.$element.prev('.well-select');
      var ctlWidth = (self.options.ctlWidth || self.options.width || '100%') + '';
      if (ctlWidth.indexOf('%') > -1 || ctlWidth.indexOf('px') > -1) {
        _wellSelectWidth = ctlWidth;
      } else {
        _wellSelectWidth = ctlWidth + 'px';
      }
      $wellSelect.css('width', _wellSelectWidth);
    },

    setDropdownWidth: function () {
      var self = this;
      var $wellSelect = self.$element.prev('.well-select');
      var $dropdown = $wellSelect.find('.well-select-dropdown');
      $dropdown.css('width', $wellSelect.width());
    },

    renderData: function () {
      var _self = this;
      var searchHtml = '';
      var chooseAllHtml = '';

      var tagFieldHtml = '';
      if (_self.options.tags) {
        tagFieldHtml =
          '<div class="well-select-tags-input"><input class="well-select-input" placeholder="请输入"><div class="well-select-tags-ok">确定</div></div>';
      }

      if (_self.options.searchable) {
        searchHtml =
          '<div class="well-select-search"><input class="well-select-input" placeholder="搜索"><div class="search-icon"><i class="iconfont icon-ptkj-sousuochaxun"></i></div></div>';
      }
      if (_self.options.chooseAll) {
        chooseAllHtml = '<div class="well-select-choose-all">全选</div>';
      }

      var operationHtml = '<div class="well-select-control iconfont icon-ptkj-gengduocaozuo"></div>';
      if (_self.options.custom_more_style === 2) {
        operationHtml = '<div class="well-select-control2"><i class="iconfont add-icon icon-ptkj-jiahao"></i></div>';
      }

      var $wellSelectDropdown = $(
        '<div class="well-select-dropdown"' +
        'x-placement="bottom-start">' +
        tagFieldHtml +
        searchHtml +
        chooseAllHtml +
        '<ul class="well-default-add" style="display: none">' +
        '<li class="well-select-item">' +
        '<span>无备选项,点右侧按钮添加</span>' +
        operationHtml +
        '</li>' +
        '</ul>' +
        '<ul class="well-select-not-found" style="display: none;">' +
        '<li>无匹配数据</li>' +
        '</ul>' +
        '<ul class="well-select-dropdown-list"></ul>' +
        '<ul class="well-select-loading" style="display: none;">加载中</ul>' +
        '</div>'
      );
      _self.renderOption($wellSelectDropdown);
      return $wellSelectDropdown;
    },

    renderOption: function ($wellSelectDropdown) {
      var _self = this;
      var Data = _self.formatSelectData();
      var controlHtml = '';
      $wellSelectDropdown = $wellSelectDropdown || _self.container.find('.well-select-dropdown');
      _self.setFirstOption(Data);

      if (Data.length) {
        $wellSelectDropdown.find('.well-select-not-found').hide();
        $.each(Data, function (i, v) {
          var descHtml = '';

          if (_self.options.multiple && !v.value && !v.label) {
            return true;
          }
          var _desc = v.desc || (v.data && v.data.desc);
          if (_desc) {
            descHtml = '<span style="float:right;color:#ccc">' + _desc + '</span>';
          }
          if (_self.options.custom_more && v.value && v.name) {
            controlHtml = '<div class="well-select-control iconfont icon-ptkj-gengduocaozuo"></div>';
          }
          if (_self.optgroup) {
            var $LiGroup = $('<li>', {
              class: 'well-select-group'
            });
            var $LiGroupLabel = $('<div class="well-select-group-label"><span>' + v.label + '</span></div>');
            var $LiGroupList = $('<ul>', {
              class: 'well-select-group-list'
            });

            $.each(v.optgroup, function (_i, _v) {
              var $Li = $('<li>', {
                class: 'well-select-item'
              });
              $Li.data({
                data: _v.data,
                uuid: _v.data && _v.data.uuid,
                value: _v.value,
                name: _v.name,
                desc: _v.desc || ''
              });
              $Li.html('<span title="' + _v.name + '">' + _v.name + '</span>' + descHtml + controlHtml);
              if (controlHtml) {
                $Li.addClass('well-select-controllable');
              }
              $LiGroupList.append($Li);
            });
            $LiGroup.append($LiGroupLabel).append($LiGroupList);
            $wellSelectDropdown.find('.well-select-dropdown-list').append($LiGroup);
          } else {
            var $Li = $('<li>', {
              class: 'well-select-item'
            });
            $Li.data({
              data: v.data,
              uuid: v.data && v.data.uuid,
              value: v.value,
              name: v.name,
              desc: _desc
            });
            $Li.html('<span title="' + v.name + '">' + v.name + '</span>' + descHtml + controlHtml);
            if (controlHtml) {
              $Li.addClass('well-select-controllable');
            }
            $wellSelectDropdown.find('.well-select-dropdown-list').append($Li);
          }
        });
        if (Data.length === 1 && Data[0].value === '') {
          if ((_self.options.customEvent && _self.options.customEvent.add) || _self.options.enableAdd) {
            if (!_self.options.ajax) {
              $wellSelectDropdown.find('.well-default-add').show();
              $wellSelectDropdown.find('.well-select-choose-all').hide();
            }
          }
        }
      } else {
        $wellSelectDropdown.find('.well-select-not-found').show();
        if ((_self.options.customEvent && _self.options.customEvent.add) || _self.options.enableAdd) {
          if (!_self.options.ajax) {
            $wellSelectDropdown.find('.well-default-add').show();
          }
        }
      }
    },

    ajaxRenderData: function (data) {
      var _self = this;
      var controlHtml = '';
      _self.container.find('.well-select-dropdown-list').empty();
      if (data.length) {
        _self._ajaxData = data;
        if (data.length === 1 && !data[0].id) {
          _self.container.find('.well-select-loading').hide();
          _self.container.find('.well-select-not-found').show();
          if ((_self.options.customEvent && _self.options.customEvent.add) || _self.options.enableAdd) {
            _self.container.find('.well-default-add').show();
          }
          return;
        } else {
          _self.container.find('.well-select-not-found').hide();
          _self.container.find('.well-default-add').hide();
        }
        _self.setFirstOption(data);
        $.each(data, function (i, v) {
          var descHtml = '';
          if (_self.options.multiple && !v.id) {
            return true;
          }
          if (v.desc) {
            descHtml = '<span style="float:right;color:#ccc">' + v.desc + '</span>';
          }
          if (_self.options.custom_more || _self.options.enableAdd || _self.options.enableDel) {
            if (_self.options.custom_more_style === 2) {
              var addIcon = '';
              var moveUpIcon = '';
              var moveDownIcon = '';
              var delIcon = '';
              if ((_self.options.customEvent && _self.options.customEvent.add) || _self.options.enableAdd) {
                addIcon = '<i class="iconfont add-icon icon-ptkj-jiahao"></i>';
              }
              var _searchValue = _self.container ? _self.container.find('.well-select-input').val() : '';
              if (!_searchValue) {
                //有搜索值时，不显示上移下移操作
                if (_self.options.customEvent && _self.options.customEvent.moveUp) {
                  moveUpIcon = '<i class="iconfont move-up icon-ptkj-shangyi"></i>';
                }
                if (_self.options.customEvent && _self.options.customEvent.moveDown) {
                  moveDownIcon = '<i class="iconfont move-down icon-ptkj-xiayi"></i>';
                }
              }

              if ((_self.options.customEvent && _self.options.customEvent.delete) || _self.options.enableDel) {
                delIcon = '<i class="iconfont del-node icon-ptkj-shanchu"></i>';
              }
              controlHtml = '<div class="well-select-control2">' + moveUpIcon + moveDownIcon + addIcon + delIcon + '</span></div>';
            } else {
              controlHtml = '<div class="well-select-control iconfont icon-ptkj-gengduocaozuo"></div>';
            }
          }
          var $Li = $('<li>', {
            class: 'well-select-item'
          });
          $Li.data({
            data: v,
            uuid: v.id,
            value: v.id,
            name: v.text,
            desc: v.desc || ''
          });
          $Li.html('<span>' + v.text + '</span>' + descHtml + controlHtml);
          _self.container.find('.well-select-dropdown-list').append($Li);
        });
      } else {
        _self.container.find('.well-select-not-found').show();
        if ((_self.options.customEvent && _self.options.customEvent.add) || _self.options.enableAdd) {
          _self.container.find('.well-default-add').show();
        }
      }
      _self.container.find('.well-select-loading').hide();
      _self.bindEvent();
      _self.setInitValue('ajax');
    },
    setFirstOption: function (data) {
      var _self = this;
      $.each(data, function (i, item) {
        if (item.id || item.value) {
          _self.firstOption = item;
          return false;
        }
      });
    },
    formatSelectData: function () {
      var _self = this;
      var optionsData = [];
      var _options = _self.options;
      if ('ajax' in _options) {
        return [];
      } else if (_options.data && _options.data.length) {
        if (_options.isGroup) {
          _self.optgroup = true;
          $.each(_options.data, function (i, v) {
            var optgroupArr = [];
            $.each(v.children, function (_i, _v) {
              optgroupArr.push({
                value: _v.id,
                name: _v.text,
                data: _v
              });
            });
            optionsData.push({
              label: v.text,
              optgroup: optgroupArr
            });
          });
        } else {
          if (_options.data[0].id && _options.showEmpty) {
            if (_options.data[0].id !== _options.defaultBlankValue) {
              _options.data.unshift({
                id: _options.defaultBlank ? _options.defaultBlankValue : '',
                text: _options.defaultBlank ? _options.defaultBlankText : ''
              });
            }
          }
          $.each(_options.data, function (i, v) {
            optionsData.push({
              value: v.id,
              name: v.text,
              data: v
            });
          });
        }
      } else {
        if (_self.$element.find('optgroup').length) {
          _self.optgroup = true;
          var $optgroup = _self.$element.find('optgroup');
          $optgroup.each(function () {
            var $this = $(this);
            var optgroupArr = [];
            $this.find('option').each(function () {
              var _$this = $(this);
              optgroupArr.push({
                value: _$this.val(),
                name: _$this.text(),
                data: _$this.data('data')
              });
            });
            optionsData.push({
              label: $this.attr('label'),
              optgroup: optgroupArr
            });
          });
        } else {
          _self.$element.find('option').each(function () {
            var $this = $(this);
            optionsData.push({
              value: $this.val(),
              name: $this.text(),
              data: $this.data('data')
            });
          });
        }
      }
      return optionsData;
    },
    optionToData: function (element) {
      return {
        id: element.prop('value'),
        text: element.text(),
        element: element.get(),
        css: element.attr('class'),
        disabled: element.prop('disabled'),
        locked: equal(element.attr('locked'), 'locked') || equal(element.data('locked'), true)
      };
      // if (element.is("option")) {
      //
      // } else if (element.is("optgroup")) {
      //     return {
      //         text:element.attr("label"),
      //         children:[],
      //         element: element.get(),
      //         css: element.attr("class")
      //     };
      // }
    },

    renderTagsDom: function () {
      var _self = this;
      var tags = _self.options.tags;
      if ($.isArray(tags)) {
        if (tags.length) {
          _self.container.find('.well-select-not-found').hide();
        }
        $.each(tags, function (i, item) {
          var $Li = $('<li>', {
            class: 'well-select-item'
          });
          $Li.data({
            data: item,
            value: item.value || item.id || item.name || item.text || item,
            name: item.name || item.text || item.value || item.id || item
          });
          $Li.html('<span>' + $Li.data('name') + '</span>');
          _self.container.find('.well-select-dropdown-list').prepend($Li);
        });
      }
    },
    renderSelectTag: function (val) {
      var _self = this;

      var $Li = $('<li>', {
        class: 'well-select-item well-select-item-tag'
      });
      $Li.data({
        value: val,
        name: val
      });
      $Li.html('<span>' + val + '</span>');
      _self.container.find('.well-select-dropdown-list').append($Li);
      $Li.trigger('click');
    },
    removeSelectTag: function () {},
    renderShowTag: function (val) {
      var _self = this;
      _self.container
        .find('.well-tag-list')
        .append(
          '<div class="well-tag well-tag-checked" data-value="' +
          val +
          '" data-name="' +
          val +
          '">' +
          '<span class="well-tag-text" title="' +
          val +
          '">' +
          val +
          '</span>' +
          '<i class="iconfont icon-ptkj-dacha"></i>' +
          '</div>'
        );
    },
    _ajax: function () {
      var _self = this;

      _self.loading = true;
      var options_ajax = _self.options.ajax;
      var _data = options_ajax.data();

      var _searchValue = _self.container ? _self.container.find('.well-select-input').val() : '';
      _data.searchValue = _searchValue;
      $.ajax({
        type: options_ajax.type,
        url: options_ajax.url,
        dataType: options_ajax.dataType,
        contentType: options_ajax.contentType,
        async: false,
        data: _data,
        success: function (result) {
          _self.firstRendered = true;
          _self.loading = false;
          if (_self.options.defaultBlank) {
            result.results.unshift({
              id: _self.options.defaultBlankValue,
              text: _self.options.defaultBlankText
            });
          }

          _self.ajaxRenderData(result.results);

          if (_self.valueToBeChecked) {
            _self.valueToBeChecked = false;
            _self.checkValue();
          }
        }
      });
    },
    setDropdownPosition: function (_mount) {
      var self = this;
      _mount = _mount || self.container;
      var windowInnerHeight = window.innerHeight;
      var _scrollTop = $(document).scrollTop();
      var _mountOffset = _mount.offset();
      var _mountOuterHeight = _mount.outerHeight();
      var $dropdown = _mount.find('.well-select-dropdown');
      var $dropdownOuterHeight = $dropdown.outerHeight();
      var _w = _mount.outerWidth();
      if (windowInnerHeight + _scrollTop - $dropdownOuterHeight - _mountOuterHeight - _mountOffset.top >= 0) {
        $dropdown.css({
          position: 'fixed',
          left: _mountOffset.left + 'px',
          top: _mountOffset.top + _mountOuterHeight - _scrollTop + 'px',
          bottom: 'auto',
          width: _w
        });
      } else {
        $dropdown.css({
          position: 'fixed',
          left: _mountOffset.left + 'px',
          top: 'auto',
          bottom: windowInnerHeight - _mountOffset.top + _scrollTop + 'px',
          width: _w
        });
      }
    },
    opening: function () {
      var self = this;
      var resize = 'resize.' + self.container_id;
      var scroll = 'scroll.' + self.container_id;

      if (window.openSelect && window.openSelect !== self) {
        window.openSelect.close();
      } {
        window.openSelect = self;
      }

      if (self.options.beforeOpen) {
        self.options.beforeOpen(self, self.checkValue);
      } else {
        if (self.options.ajax && self.firstRendered && !self.loading) {
          self.checkValue();
        } else {
          self.valueToBeChecked = true;
        }
      }
      this.container
        .parents()
        .add(window)
        .each(function () {
          $(this).on(resize + ' ' + scroll, function (e) {
            self.setDropdownPosition();
          });
        });
    },

    checkValue: function (value) {
      var self = this;
      var $container = self.container;
      var value = value || self.getValue();
      var multiple = self.options.multiple;
      var $wellselectItem = $container.find('.well-select-dropdown .well-select-item');
      var $placeholder = $container.find('.well-select-placeholder');
      var $selectedValue = $container.find('.well-select-selected-value');
      var $wellTagList = $container.find('.well-tag-list');
      $wellselectItem.removeClass('well-select-item-selected');
      $wellTagList.empty();
      if (!value || !value.length) {
        $placeholder.show();
        $selectedValue.empty().hide();
        return;
      }
      $placeholder.hide();
      $wellselectItem.each(function () {
        var $this = $(this);
        var _value = $this.data('value');
        var _name = $this.data('name');
        if (!multiple) {
          if (_value === value) {
            $this.addClass('well-select-item-selected');
            $selectedValue.data('value', _value).text(_name).show();
          }
        } else {
          value = typeof value === 'string' ? value.split(self.options.separator) : value;
          if (value.indexOf(_value) > -1) {
            $this.addClass('well-select-item-selected');
            $wellTagList.append(
              '<div class="well-tag well-tag-checked" data-value="' +
              _value +
              '" data-name="' +
              _name +
              '">' +
              '<span class="well-tag-text" title="' +
              _name +
              '">' +
              _name +
              '</span>' +
              '<i class="iconfont icon-ptkj-dacha"></i>' +
              '</div>'
            );
          }
        }
      });
    },

    close: function () {
      if (window.openSelect === this) {
        window.openSelect = null;
      }

      this.$element.prev('.well-select').removeClass('well-select-visible');
      if (this.options.callback && this.options.multiple) {
        this.options.callback(this.getData());
      }
    },
    bindEvent: function () {
      var self = this;
      var $wellSelect = self.$element.prev('.well-select');
      var $wellSelectItem = $wellSelect.find('.well-select-item');
      var $wellSelectSelectedValue = $wellSelect.find('.well-select-selected-value');
      var $placeholder = $wellSelect.find('.well-select-placeholder');
      var $wellSelectSearch = $wellSelect.find('.well-select-search');
      var $searchInput = $wellSelectSearch.find('.well-select-input');
      var $wellSelectChooseAll = $wellSelect.find('.well-select-choose-all');
      var $wellSelectControl = $wellSelect.find('.well-select-control');
      var $wellSelectControl2 = $wellSelect.find('.well-select-control2');
      var $clearAll = $wellSelect.find(".well-select-clear");

      $wellSelect.off().on('click', function (e) {
        var $this = $(this);
        if ($this.hasClass('wellSelect-container-disabled')) {
          return false;
        }
        self.setDropdownPosition($this);
        $('.well-select').each(function () {
          var _$this = $(this);
          if ($(self.element).is('select[disabled]')) {
            return;
          }
          if (_$this.is($this)) {
            if (_$this.hasClass('well-select-visible')) {
              self.close();
            } else {
              _$this.addClass('well-select-visible');
            }
          } else {
            _$this.removeClass('well-select-visible');
          }
        });

        if ($this.hasClass('well-select-visible')) {
          self.opening();
        } else {
          self.close();
          return;
        }
        self.setDropdownWidth();
        var _options = self.options;
        if ('ajax' in _options && !self.firstRendered) {
          self.container.find('.well-select-loading').show();
          self.container.find('.well-select-not-found').hide();
          self._ajax();
        }
        // e.stopPropagation();
      }).on("mouseover", function () {
        if ($(this).hasClass('wellSelect-container-disabled')) {
          return false;
        }
        var text = $wellSelectSelectedValue.text();
        var $wellTagChecked = $wellSelect.find(".well-tag-checked");
        if ((!self.options.multiple && text) || (self.options.multiple && $wellTagChecked.length > 0)) {
          $clearAll.show();
        }

      }).on("mouseleave", function () {
        $clearAll.hide();
      });
      $clearAll.off('click').on('click', function (e) {
        e.stopPropagation();
        if (self.options.multiple) {
          self.setValue([]);
        } else {
          self.setValue('');
        }

        $(this).hide();
      });

      $wellSelectSearch.off('click').on('click', function (e) {
        e.stopPropagation();
      });

      $wellSelect.find('.well-select-tags-input').on('click', function (e) {
        e.stopPropagation();
      });
      $wellSelect.find('.well-select-tags-input .well-select-tags-ok').on('click', function () {
        var $input = $(this).prev();
        self.renderSelectTag($input.val());
        $input.val('');
      });

      $wellSelectChooseAll.off('click').on('click', function (e) {
        e.stopPropagation();
        var $this = $(this);
        $this.toggleClass('checked');
        if ($this.hasClass('checked')) {
          $wellSelect.find('.well-select-item').each(function () {
            var _this = $(this);
            if (_this.css('display') === 'none') return true; //跳过当前循环
            _this.addClass('well-select-item-selected');
          });
        } else {
          $wellSelect.find('.well-select-item').removeClass('well-select-item-selected');
        }
        self.SelectItemHandle('item');
      });
      var cpLock = true;
      $searchInput.off().on('compositionstart', function () {
        cpLock = false;
      });
      $searchInput.off().on('compositionend', function () {
        cpLock = true;
      });
      var timeoutId = 0;
      $searchInput.off().on('input propertychange', function () {
        if (self.options.ajax) {
          clearTimeout(timeoutId);
          self.container.find('.well-select-dropdown-list').empty();
          self.container.find('.well-select-loading').show();
          self.container.find('.well-select-not-found').hide();
          timeoutId = setTimeout(function () {
            self._ajax();
          }, 1000);
          return;
        }
        var $this = $(this);
        setTimeout(function () {
          if (cpLock) {
            self.searchResult($.trim($this.val()));
          }
        }, 0);
      });

      $wellSelect
        .find('.well-select-item')
        .off('click')
        .on('click', function (e) {
          if (self.options.multiple || self.options.ajax || e.isTrigger || self.options.stopPropagation) {
            e.stopPropagation();
          }
          var $this = $(this);

          if ($this.hasClass('well-select-item-disabled')) {
            return;
          }

          if (self.options.multiple) {
            $this.toggleClass('well-select-item-selected');
            if (!$this.hasClass('well-select-item-selected')) {
              $wellSelectChooseAll.removeClass('checked');
              self.container.find('.well-tag').each(function () {
                var _$this = $(this);
                if (_$this.data('value').toString() === $this.data('value').toString()) {
                  _$this.remove();
                  if ($this.hasClass('well-select-item-tag')) {
                    $this.remove();
                  }
                  return false;
                }
              });
            }
            if (
              self.options.maxSelectNum &&
              self.options.maxSelectNum != '0' &&
              $wellSelect.find('.well-select-item-selected').length > self.options.maxSelectNum
            ) {
              $this.removeClass('well-select-item-selected');
              appModal.alert('最多只能选择' + self.options.maxSelectNum + '项！');
            }
            if (self.options.disorderValue) {
              self.SelectItemDisorderHandle($this);
            } else {
              self.SelectItemHandle('item');
            }

          } else {
            if (!$this.data('value').toString()) {
              $wellSelectItem.removeClass('well-select-item-selected');
              self.setSelectValue('');
              if (self.options.defaultBlankText) {
                $wellSelectSelectedValue.text(self.options.defaultBlankText).show();
                $placeholder.hide();
              } else {
                $placeholder.show();
                $wellSelectSelectedValue.hide();
              }
              if (self.options.ajax) {
                if (e.isTrigger) {
                  //判断是不是trigger触发的
                  return;
                }
                self.close();
              }
              return;
            }
            self.container.find('.well-select-item').removeClass('well-select-item-selected');
            $this.addClass('well-select-item-selected');
            $wellSelectSelectedValue.data('value', $this.data('value')).text($this.data('name')).show();
            $placeholder.hide();
            self.setData($this.data('data'));
            self.$element.data('wellSelect-change-triggered', true);
            var _val = $this.data('value').toString();
            self.setSelectValue(_val);
            if (self.options.ajax) {
              if (e.isTrigger) {
                //判断是不是trigger触发的
                return;
              }
              self.close();
            }
          }
        });

      $wellSelect.on('click', '.well-tag-list .well-tag i', function (e) {
        e.stopPropagation();
        $(this).parent().remove();
        if (self.options.disorderValue) {
          var $wellSelect = self.$element.prev('.well-select');
          var $wellSelectItem = $wellSelect.find('.well-select-dropdown-list .well-select-item');
          var tagValue = $(this).parents(".well-tag").data("value").toString();
          $wellSelectItem.each(function () {
            var $this = $(this);
            if ($this.data('value').toString() == tagValue) {
              $this.trigger("click");
            }
          });
        } else {
          self.SelectItemHandle('tag');
        }

      });

      if (self.options.custom_more_style === 2) {
        $wellSelectControl2.mouseover(function () {
          debugger;
          var $wellSelectControl2Item = $(this);
          var mount = $(this).parent();
          $wellSelectControl2Item
            .find('.add-icon')
            .off('click')
            .on('click', function (event) {
              event.stopPropagation();
              self.buildAddControlElement2(mount);
            });
          $wellSelectControl2Item
            .find('.move-up')
            .off('click')
            .on('click', function (event) {
              event.stopPropagation();
              self.sortOption(mount, 'up');
            });
          $wellSelectControl2Item
            .find('.move-down')
            .off('click')
            .on('click', function (event) {
              event.stopPropagation();
              self.sortOption(mount, 'down');
            });
          $wellSelectControl2Item
            .find('.del-node')
            .off('click')
            .on('click', function (event) {
              event.stopPropagation();
              self.delOption(mount, mount.data('value'));
            });
        });
      } else {
        $wellSelectControl.off('click').on('click', function (e) {
          e.stopPropagation();
          if ($(this).siblings('.well-select-control-element').length) {
            return;
          }
          $wellSelect.find('.well-select-dropdown-list').addClass('no-scrollbar');
          self.buildSelectControlElement($(this).parent());
        });
      }

      $wellSelect.find('.well-select-dropdown-list').scroll(function () {
        $(this).removeClass('no-scrollbar');
      });

      if (self.options.defaultValueByFirstOption) {
        setTimeout(function () {
          self.setValue(self.firstOption.value || self.firstOption.id);
        }, 0);
      }

      // bug#46513: 资源日历组件-资源分组字段无法选择
      if (!window.selectGlobalEventRegistered) {
        window.selectGlobalEventRegistered = true;

        $(document).on('click mousedown', function (e) {
          var openSelect = window.openSelect;
          var $target = $(e.target);
          if (!openSelect) {
            return;
          }

          if ($target.parents('.well-select').length) {
            if (openSelect.container_id !== $target.closest('.well-select')[0].id) {
              openSelect.close();
            }
          } else {
            openSelect.close();
          }
        });
      }
    },

    hasEventAdd: function (type) {
      var _self = this;
      if ((_self.options.customEvent && _self.options.customEvent.add) || _self.options.enableAdd) {
        if (type === 'btn') {
          // return '<div class="event-item event-add pull-left active">添加</div>';
          return '';
        } else {
          return (
            '<div class="event-field">' +
            '<input class="form-control" name="nodeNames" placeholder="选项名称">' +
            '<div class="add-node">添加</div></div>'
          );
        }
      } else {
        return '';
      }
    },
    hasEventDel: function () {
      var _self = this;

      if ((_self.options.customEvent && _self.options.customEvent.delete) || _self.options.enableDel) {
        return '<div class="event-item event-del pull-right">' + '<span class="iconfont del-node icon-ptkj-shanchu"></span>' + '</div>';
      } else {
        return '';
      }
    },
    hasEventSort: function () {
      var _self = this;
      var moveUpIcon = '';
      var moveDownIcon = '';
      if (_self.options.customEvent && _self.options.customEvent.moveUp) {
        moveUpIcon = '<span class="iconfont move-up icon-ptkj-shangyi"></span>';
      }
      if (_self.options.customEvent && _self.options.customEvent.moveDown) {
        moveDownIcon = '<span class="iconfont move-down icon-ptkj-xiayi"></span>';
      }
      if (!moveUpIcon && !moveDownIcon) return '';
      return '<div class="event-item event-sort pull-right">' + moveUpIcon + moveDownIcon + '</div>';
    },

    buildSelectControlElement: function (mount) {
      var _self = this;

      var _html = '';
      if (mount.parent().hasClass('well-select-dropdown-list')) {
        _html = _self.hasEventAdd('btn') + _self.hasEventDel() + _self.hasEventSort();
      }
      var controlElement =
        '<div class="well-select-control-element" style="display: none"><div class="control-area"></div>' +
        '        <div class="well-select-control-element-event clearfix">' +
        _html +
        '        </div>' +
        _self.hasEventAdd('field') +
        '    </div>';
      var mountWidth = mount.outerWidth();
      var mountHeight = mount.outerHeight();
      var mountOffsetTop = mount.offset().top - $(window).scrollTop();
      var mountOffsetLeft = mount.offset().left - $(window).scrollLeft();
      mount.append(controlElement);
      var $controlElement = mount.find('.well-select-control-element');
      $controlElement.slideDown('fast');
      if (mountOffsetTop + mountHeight + 90 > $(window).height()) {
        $controlElement.addClass('drop-top').css({
          bottom: $(window).height() - mountOffsetTop + 8 + 'px',
          left: mountOffsetLeft + mountWidth - $controlElement.outerWidth() + 'px'
        });
      } else {
        $controlElement.css({
          top: mountOffsetTop + mountHeight + 'px',
          left: mountOffsetLeft + mountWidth - $controlElement.outerWidth() + 'px'
        });
      }

      $controlElement.on('click', function (e) {
        e.stopPropagation();
      });
      mount.mouseleave(function () {
        $controlElement.remove();
      });
      $controlElement.mouseleave(function (e) {
        if (!e.relatedTarget.isSameNode($(this).parents('.well-select-item')[0])) {
          //判断移出时鼠标是否还在当前选项上
          $(this).remove();
        }
      });
      var $eventField = $controlElement.find('.event-field');
      $controlElement.find('.event-add').on('click', function (e) {
        e.stopPropagation();
        $(this).addClass('active');
      });
      $eventField.find('.add-node').on('click', function () {
        var $this = $(this);
        var addNodeValue = $.trim($this.prev().val());
        if (!addNodeValue) return;
        _self.addNewOption(mount, addNodeValue);
      });
      $controlElement.find('.move-up').on('click', function () {
        _self.sortOption(mount, 'up');
      });
      $controlElement.find('.move-down').on('click', function () {
        _self.sortOption(mount, 'down');
      });
      $controlElement.find('.event-del').on('click', function () {
        _self.delOption(mount, mount.data('value'));
      });
    },

    buildAddControlElement2: function (mount) {
      var _self = this;

      var _html = '';
      // if(mount.parent().hasClass('well-select-dropdown-list')) {
      // _html = _self.hasEventAdd('btn') + _self.hasEventDel() + _self.hasEventSort();
      // }
      var controlElement =
        '<div class="well-select-control-element" style="display: none"><div class="control-area"></div>' +
        '        <div class="well-select-control-element-event clearfix">' +
        _html +
        '        </div>' +
        _self.hasEventAdd('field') +
        '    </div>';
      var mountWidth = mount.outerWidth();
      var mountHeight = mount.outerHeight();
      var mountOffsetTop = mount.offset().top - $(window).scrollTop();
      var mountOffsetLeft = mount.offset().left - $(window).scrollLeft();
      mount.append(controlElement);
      var $controlElement = mount.find('.well-select-control-element');
      $controlElement.slideDown('fast');
      if (mountOffsetTop + mountHeight + 90 > $(window).height()) {
        $controlElement.addClass('drop-top').css({
          bottom: $(window).height() - mountOffsetTop + 8 + 'px',
          left: mountOffsetLeft + mountWidth - $controlElement.outerWidth() + 'px'
        });
      } else {
        $controlElement.css({
          top: mountOffsetTop + mountHeight + 'px',
          left: mountOffsetLeft + mountWidth - $controlElement.outerWidth() + 'px'
        });
      }

      $controlElement.on('click', function (e) {
        e.stopPropagation();
      });
      mount.mouseleave(function () {
        $controlElement.remove();
      });
      $controlElement.mouseleave(function (e) {
        if (!e.relatedTarget.isSameNode($(this).parents('.well-select-item')[0])) {
          //判断移出时鼠标是否还在当前选项上
          $(this).remove();
        }
      });
      var $eventField = $controlElement.find('.event-field');
      $controlElement.find('.event-add').on('click', function (e) {
        e.stopPropagation();
        $(this).addClass('active');
      });
      $eventField.find('.add-node').on('click', function () {
        var $this = $(this);
        var addNodeValue = $.trim($this.prev().val());
        if (!addNodeValue) return;
        _self.addNewOption(mount, addNodeValue);
      });
      // $controlElement.find('.move-up').on('click',function () {
      //     _self.sortOption(mount,'up');
      // });
      // $controlElement.find('.move-down').on('click',function () {
      //     _self.sortOption(mount,'down');
      // });
      // $controlElement.find('.event-del').on('click',function () {
      //     _self.delOption(mount,mount.data('value'));
      // });
    },

    addNewOption: function (currElement, val) {
      var _self = this;
      if (_self.options.enableAdd) {
        _self.$element.trigger('Select2add', [{
            text: val
          },
          function (result) {
            var newOption = currElement.clone(true);
            newOption
              .data({
                uuid: result.id,
                value: result.id,
                name: result.text
              })
              .removeClass('well-select-item-selected')
              .find('.well-select-control-element')
              .remove();
            newOption.find('span').text(result.text);
            currElement.after(newOption);
            if (_self.$element.is('select')) {
              _self.$element.append('<option value="' + result.id + '">' + result.text + '</option>');
            }
          }
        ]);
      } else {
        var currentUuid = currElement.data('uuid') || currElement.data('id') || currElement.data('data');
        JDS.call({
          service: 'dataDictionaryService.quickAddDataDic',
          data: [val, currentUuid, _self.options.dictUuid],
          success: function (result) {
            var newOption = currElement.clone(true);
            newOption
              .data({
                uuid: result.data.uuid,
                value: result.data.code,
                name: result.data.name
              })
              .removeClass('well-select-item-selected')
              .find('.well-select-control-element')
              .remove();
            newOption.find('span').text(result.data.name);
            if (currentUuid) {
              currElement.after(newOption);
            } else {
              _self.container.find('.well-default-add').hide();
              if (_self.options.chooseAll) {
                _self.container.find('.well-select-choose-all').show();
              }
              _self.container.find('.well-select-dropdown-list').append(newOption);
            }
            if (_self.$element.is('select')) {
              _self.$element.append('<option value="' + result.data.code + '">' + result.data.name + '</option>');
            }
          }
        });
      }
    },
    delOption: function (currElement, val) {
      var _self = this;

      if (_self.options.enableDel) {
        _self.$element.trigger('Select2del', [{
            val: val
          },
          function (result) {
            currElement.remove();
            if (_self.getValue().indexOf(result.id) > -1) {
              if (_self.options.multiple) {
                _self.container.find('.well-tag').each(function () {
                  var $this = $(this);
                  if ($this.data('value') === result.id) {
                    $this.find('i').trigger('click');
                    return false;
                  }
                });
              } else {
                _self.container.setValue('');
              }
            }
          }
        ]);
      } else {
        var uuid = currElement.data('uuid') || currElement.data('data') || currElement.data('id');
        $.ajax({
          type: 'delete',
          url: ctx + '/dict/' + uuid,
          success: function (result) {
            currElement.remove();
          }
        });
      }
    },
    sortOption: function (currElement, type) {
      var _self = this;
      if (type === 'up') {
        var $prev = currElement.prev();
        if ($prev.length === 0 || $prev.children('.well-select-control').length === 0) {
          return;
        }
        if (_self.options.sortCallBack) {
          _self.options.sortCallBack('up', $prev, currElement, function () {
            currElement.after($prev.clone(true));
            $prev.remove();
          });
        } else {
          var prevUuid = $prev.data('uuid') || $prev.data('data') || $prev.data('id');
          var currElementUuid = currElement.data('uuid') || currElement.data('data') || currElement.data('id');
          JDS.call({
            service: 'dataDictionaryService.moveDataDicAfterOther',
            data: [prevUuid, currElementUuid],
            success: function (result) {
              currElement.after($prev.clone(true));
              $prev.remove();
            }
          });
        }
      } else {
        var $next = currElement.next();
        if ($next.length === 0 || $next.children('.well-select-control').length === 0) {
          return;
        }
        if (_self.options.sortCallBack) {
          _self.options.sortCallBack('down', currElement, $next, function () {
            currElement.before($next.clone(true));
            $next.remove();
          });
        } else {
          var nextUuid = $next.data('uuid') || $next.data('data') || $next.data('id');
          var currElementUuid = currElement.data('uuid') || currElement.data('data') || currElement.data('id');
          JDS.call({
            service: 'dataDictionaryService.moveDataDicAfterOther',
            data: [currElementUuid, nextUuid],
            success: function (result) {
              currElement.before($next.clone(true));
              $next.remove();
            }
          });
        }
      }
    },
    searchResult: function (keyword) {
      var self = this;
      var $wellSelect = self.$element.prev('.well-select');
      var $wellSelectItem = $wellSelect.find('.well-select-item');
      var $wellSelectNotFound = $wellSelect.find('.well-select-not-found');
      var $wellSelectChooseAll = $wellSelect.find('.well-select-choose-all');
      var showNum = 0;
      $wellSelectItem.each(function () {
        var $this = $(this);
        if (!$this.data('name')) {
          return true;
        }
        if (
          $this.data('name').toUpperCase().indexOf(keyword.toString().toUpperCase()) > -1 ||
          ($this.data('desc') && $this.data('desc').toUpperCase().indexOf(keyword.toString().toUpperCase()) > -1)
        ) {
          $this.show();
          $this.closest('.well-select-group').show();
          showNum++;
        } else {
          $this.hide();
          var $siblings = $this.siblings();
          var allHide = true;
          $siblings.each(function (i, item) {
            if ($(this).css('display') === 'block' || $(this).css('display') === 'list-item') {
              allHide = false;
              return false;
            }
          });
          if (allHide) {
            $this.closest('.well-select-group').hide();
          }
        }
      });
      if (showNum) {
        $wellSelectNotFound.hide();
        $wellSelectChooseAll.show();
      } else {
        $wellSelectNotFound.show();
        $wellSelectChooseAll.hide();
      }
    },
    SelectItemDisorderHandle: function ($this) {
      var self = this;
      var $wellSelect = self.$element.prev('.well-select');
      var $wellSelectItem = $wellSelect.find('.well-select-dropdown-list .well-select-item');
      var $wellTagList = $wellSelect.find('.well-tag-list');
      var $wellTag = $wellTagList.find('.well-tag');
      var $placeholder = $wellSelect.find('.well-select-placeholder');
      var selectItemValue = [];
      if (!this.disorderValues) {
        this.disorderValues = [];
      }
      if (!$this.hasClass("well-select-item-selected")) {
        var vIndex = _.findIndex(this.disorderValues, function (o) {
          return o.value == $this.data('value').toString();
        })
        this.disorderValues.splice(vIndex, 1);
      } else {
        this.disorderValues.push({
          value: $this.data('value').toString(),
          name: $this.data('name')
        })
      }

      $.each(this.disorderValues, function (index, item) {
        selectItemValue.push(item);
      })

      if (this.disorderValues.length) {
        var tagArr = [];
        $wellTag.each(function () {
          var $this = $(this);
          tagArr.push({
            value: $this.data('value').toString(),
            name: $this.data('name')
          });
        });

        $.each(tagArr, function (i, v) {
          var isExist = true;
          $wellSelectItem.each(function () {
            if (!$(this).data('value')) {
              return true;
            }
            if (v.value === $(this).data('value').toString()) {
              isExist = false;
              return false;
            }
          });
          if (isExist) {
            self.disorderValues.push(v);
            selectItemValue.push(v.value);
          }
        });
      } else {
        $wellTagList.empty();
        $wellTag = $wellTagList.find('.well-tag');
      }
      console.log(selectItemValue)
      self.setSelectValue(selectItemValue);
      $wellTagList.empty();
      $.each(self.disorderValues, function (i, v) {
        $wellTagList.append(
          '<div class="well-tag well-tag-checked" data-value="' +
          v.value +
          '" data-name="' +
          v.name +
          '">' +
          '<span class="well-tag-text" title="' +
          v.name +
          '">' +
          v.name +
          '</span>' +
          '<i class="iconfont icon-ptkj-dacha"></i>' +
          '</div>'
        );
      });

      if (self.container.find('.well-tag').length === 0 && $wellTag.length > 0) {
        $wellTag.each(function () {
          var $this = $(this);
          $wellTagList.append($this);
        });
      }
      if (self.disorderValues.length) {
        $placeholder.hide();
      } else {
        $placeholder.show();
      }

      self.setDropdownPosition($wellSelect);
    },
    SelectItemHandle: function (type) {
      var self = this;
      var $wellSelect = self.$element.prev('.well-select');
      var $wellSelectItem = $wellSelect.find('.well-select-dropdown-list .well-select-item');
      var $wellSelectItemSelected = $wellSelect.find('.well-select-dropdown-list .well-select-item.well-select-item-selected');
      var $wellTagList = $wellSelect.find('.well-tag-list');
      var $wellTag = $wellTagList.find('.well-tag');
      var $placeholder = $wellSelect.find('.well-select-placeholder');
      var selectItemArr = [];
      var selectItemValue = [];
      if (type === 'item') {
        $wellSelectItemSelected.each(function () {
          var $this = $(this);
          if (!$this.data('value').toString()) {
            return;
          }
          selectItemArr.push({
            value: $this.data('value').toString(),
            name: $this.data('name')
          });
          selectItemValue.push($this.data('value').toString());
        });
        if (selectItemArr.length) {
          var tagArr = [];
          $wellTag.each(function () {
            var $this = $(this);
            tagArr.push({
              value: $this.data('value').toString(),
              name: $this.data('name')
            });
          });

          $.each(tagArr, function (i, v) {
            var isExist = true;
            $wellSelectItem.each(function () {
              if (!$(this).data('value')) {
                return true;
              }
              if (v.value === $(this).data('value').toString()) {
                isExist = false;
                return false;
              }
            });
            if (isExist) {
              selectItemArr.push(v);
              selectItemValue.push(v.value);
            }
          });
        } else {
          $wellTagList.empty();
          $wellTag = $wellTagList.find('.well-tag');
        }
        self.setSelectValue(selectItemValue);
        $wellTagList.empty();
        $.each(selectItemArr, function (i, v) {
          $wellTagList.append(
            '<div class="well-tag well-tag-checked" data-value="' +
            v.value +
            '" data-name="' +
            v.name +
            '">' +
            '<span class="well-tag-text" title="' +
            v.name +
            '">' +
            v.name +
            '</span>' +
            '<i class="iconfont icon-ptkj-dacha"></i>' +
            '</div>'
          );
        });
      } else {
        $wellTag.each(function () {
          var $this = $(this);
          selectItemArr.push($this.data('value').toString());
        });
        $wellSelectItem.each(function () {
          var $this = $(this);
          if ($.inArray($this.data('value').toString(), selectItemArr) > -1) {
            $this.addClass('well-select-item-selected');
          } else {
            if ($this.hasClass('well-select-item-tag')) {
              $this.remove();
            }
            $this.removeClass('well-select-item-selected');
            $wellSelect.find('.well-select-choose-all').prop('checked', false).removeClass('checked');
          }
        });
        self.setSelectValue(selectItemArr);
      }
      if (self.container.find('.well-tag').length === 0 && $wellTag.length > 0) {
        $wellTag.each(function () {
          var $this = $(this);
          $wellTagList.append($this);
        });
      }
      if (selectItemArr.length) {
        $placeholder.hide();
      } else {
        $placeholder.show();
      }

      self.setDropdownPosition($wellSelect);
    },
    //初始化数据
    initSelection: function () {
      var _self = this;
      _self.options.initSelection.call(null, _self.$element, function (selected) {
        if (selected !== undefined && selected !== null) {
          if (!_self.container.parent().length) {
            //当container丢失时重新赋值
            _self.container = _self.$element.prev();
          }
          _self.container.data('data', selected);
          var $wellTagList = _self.container.find('.well-tag-list');
          if (_self.options.multiple) {
            $wellTagList.empty();
            var _hasValue = false;
            $.each(selected, function (i, v) {
              if (!v.id && !v.text) {
                return;
              }
              _hasValue = true;
              $wellTagList.append(
                '<div class="well-tag well-tag-checked" data-value="' +
                v.id +
                '" data-name="' +
                v.text +
                '">' +
                '<span class="well-tag-text" title="' +
                v.text +
                '">' +
                v.text +
                '</span>' +
                '<i class="iconfont icon-ptkj-dacha"></i>' +
                '</div>'
              );
            });
            if (_hasValue) {
              _self.container.find('.well-select-placeholder').hide();
            }
          } else {
            _self.container.find('.well-select-selected-value').text(selected.text).show().siblings('.well-select-placeholder').hide();
          }
        }
      });
    },
    setInitValue: function (type) {
      var _self = this;
      var $valueField = null;
      if (_self.options.valueField) {
        $valueField = _self.container.siblings('#' + _self.options.valueField);
        // $valueField = $('#' + _self.options.valueField);
      } else {
        $valueField = _self.$element;
      }
      var _value = $valueField.val();
      if (!_value) {
        _value = _self.options.defaultValue;
      }
      var valArr = [];
      if (typeof _value === 'string') {
        valArr = _value.split(_self.options.separator);
      } else {
        valArr = _value;
      }

      var $wellSelectItem = _self.container.find('.well-select-item');

      var isAjaxShow = type === 'ajax' && _self.container.hasClass('well-select-visible');
      $wellSelectItem.each(function () {
        var $this = $(this);
        if ($this.data('value')) {
          var _v = $this.data('value').toString();
          if ($.inArray(_v, valArr) > -1) {
            $this.trigger('click');
            isAjaxShow && _self.container.addClass('well-select-visible');
            valArr.splice($.inArray(_v, valArr), 1);
          }
        }
      });

      if (type === 'ajax' || _self.options.ajax) {
        if (_self.options.noMatchValueClear) {
          var dataIds = $.map(_self._ajaxData, function (item) {
            return item.id;
          });
          if (dataIds.indexOf(_self.val()) < 0) {
            _self.val('');
          }
        }
        return;
      }
      //当前value存在无对应选项时直接渲染(需要延时处理，因为display值不一定在value之前渲染)
      if (valArr && valArr.length > 0 && valArr[0] !== '') {
        setTimeout(function () {
          _self.renderValue();
        }, 300);
      }
      _self.container.removeClass('well-select-visible');
    },
    renderValue: function () {
      var _self = this;
      var _value = _self.getValue();
      var _label = '';
      if (_self.options.labelField) {
        _label = $('#' + _self.options.labelField).val();
      }
      if (!_self.options.multiple) {
        var data = _self.data();
        if (data && data.id === _value) {
          _label = data.text;
        }
        _self.container
          .find('.well-select-selected-value')
          .data('value', _value)
          .text(_label || _value)
          .show();
        _self.container.find('.well-select-placeholder').hide();
      } else {
        _value = _value.split(_self.options.separator);
        _label = _label.split(_self.options.separator);
        if (!_label.length) {
          _label = _value;
        }
        var $wellTagList = _self.container.find('.well-tag-list');
        $.each(_value, function (i, v) {
          $wellTagList.append(
            '<div class="well-tag well-tag-checked" data-value="' +
            v +
            '" data-name="' +
            _label[i] +
            '">' +
            '<span class="well-tag-text" title="' +
            _label[i] +
            '">' +
            _label[i] +
            '</span>' +
            '<i class="iconfont icon-ptkj-dacha"></i>' +
            '</div>'
          );
        });
      }
    },
    //给$element赋值
    setSelectValue: function (value) {
      var _self = this;
      var $valueField = null;
      var $labelField = null;
      if (!value) {
        value = '';
      }
      if (_self.options.valueField && _self.options.source !== 'wSelect') {
        // $valueField = $('#' + _self.options.valueField);
        $valueField = _self.container.siblings('#' + _self.options.valueField);
      } else {
        $valueField = _self.$element;
      }
      if (_self.options.labelField) {
        $labelField = $('#' + _self.options.labelField);
      }
      var changed = true;
      if ($valueField.val() != null) {
        changed = $valueField.val() != value.toString();
      }
      if ($valueField.length > 0 && ($valueField[0].nodeName === 'INPUT' || $valueField[0].nodeName === 'TEXTAREA')) {
        if (_self.options.multiple && value) {
          var _val = value.join(_self.options.separator);
          changed = $valueField.val() != _val;
          $valueField.val(_val);
        } else {
          $valueField.val(typeof value === 'object' ? value.toString() : value);
          $labelField && $labelField.val(_self.getData().text);
        }
      } else {
        $valueField.val(value);
        $labelField && $labelField.val(_self.getData().text);
      }

      if (value === '') {
        _self.container.data('data', {});
      }
      if (changed) {
        _self.$element.trigger('change');
      }
      if (!$valueField.val()) {
        _self.container.find('.well-tag-list').empty();
      }
    },
    //设置值
    setValue: function (val) {
      var _self = this;
      var _val = val;

      if (val === '' || val == null) {
        if (_self.container.find('.well-select-dropdown-list .well-select-item').hasClass("well-select-item-selected") || _self.container.find('.well-select-selected-value').text()) {
          _self.container.find('.well-select-dropdown-list .well-select-item').removeClass('well-select-item-selected');
          _self.container.find('.well-select-selected-value').text('').hide();
          _self.container.find('.well-tag-list').empty();
          _self.container.find('.well-select-placeholder').show();
          _self.container.find('.well-select-choose-all').attr('checked', false);
          _self.setSelectValue(val);
          return false;
        }

      }

      if (typeof val === 'string') {
        _val = val.split(_self.options.separator);
      }
      var new_val = JSON.parse(JSON.stringify(_val));
      _self.container.find('.well-select-dropdown-list .well-select-item').each(function () {
        var $this = $(this);
        if (!$this.data('value')) {
          return true;
        }
        var _value = $this.data('value').toString();
        if (!_value) {
          return true;
        }
        new_val = $.map(new_val, function (item) {
          return item.toString();
        });
        if (
          (new_val.indexOf(_value) > -1 && !$this.hasClass('well-select-item-selected')) ||
          (new_val.indexOf(_value) < 0 && $this.hasClass('well-select-item-selected') && _self.options.multiple)
        ) {
          $this.trigger('click');
        }
        if (new_val.indexOf(_value) > -1) {
          new_val.splice(new_val.indexOf(_value), 1);
        }
      });

      if (new_val && new_val.length) {
        if (_self.options.multiple) {
          $.each(new_val, function (index, item) {
            // _self.renderShowTag(item);
          });
        } else {
          _self.container.find('.well-select-selected-value').text(new_val.toString());
        }
      }

      if (_self.options.source !== 'wSelect') {
        _self.setSelectValue(_val);
      }
    },
    //获取选中值
    getValue: function () {
      var _self = this;
      if (_self.options.valueField && _self.options.valueField !== _self.$element.attr('name')) {
        return $('#' + _self.options.valueField).val();
      }
      if (!_self.options.multiple || _self.element.nodeName === 'SELECT') {
        var _val = _self.$element.val();
        if (_val && _val.length === 1 && _val[0] === '') {
          _val = [];
        }
        return _val;
      } else {
        return _self.$element.val();
      }
    },
    //设置data
    setData: function (val) {
      var _self = this;
      _self.container.data('data', val);
    },
    //获取选中data
    getData: function () {
      var _self = this;
      var _dataArr = [];

      var _data = _self.container.data('data');
      var _value = _self.getValue();

      if (_data && _value) {
        if ($.isArray(_data)) {
          var _newData = [];
          $.each(_data, function (i, item) {
            if (_value.indexOf(item.id) > -1) {
              _newData.push(item);
            }
          });
          _data = _newData;
        }
      }

      if ($.isArray(_data)) {
        var hasData = false;
        $.each(_data, function (i, item) {
          if (item.id) {
            hasData = true;
            return false;
          }
        });
        if (hasData) {
          return _data;
        }
      }
      if (_data && _data.id) {
        return _data;
      } else {
        if (_self.options.disorderValue) {
          if (_self.disorderValues && _self.disorderValues.length > 0) {
            $.each(_self.disorderValues, function (vIndex, vItem) {
              _dataArr.push({
                id: vItem.value,
                text: vItem.name
              });
            })
          }
        } else {
          _self.container.find('.well-select-item-selected').each(function () {
            var $this = $(this);
            if ($this.data('data')) {
              _dataArr.push($this.data('data'));
            } else {
              _dataArr.push({
                id: $this.data('value'),
                text: $this.data('name')
              });
            }
          });
        }

        if (_self.options.multiple) {
          return _dataArr.length ? _dataArr : [{
            id: '',
            text: ''
          }];
        } else {
          return _dataArr.length ? _dataArr[0] : {
            id: '',
            text: ''
          };
        }
      }
    },
    val: function (val) {
      var _self = this;
      if (val !== undefined) {
        _self.setValue(val);
      } else {
        return _self.getValue();
      }
    },
    data: function (val) {
      var _self = this;
      if (val) {
        _self.setData(val);
      } else {
        return _self.getData();
      }
    },
    reRenderOption: function (option) {
      var _self = this;
      if (typeof option === 'object') {
        $.extend(_self.options, option);
        $.extend(_self._options, option);
      }
      _self.container.find('.well-select-dropdown-list').empty();
      _self.renderOption();
      _self.checkValue();
      _self.bindEvent();
    },

    /**
     * 设置选项状态
     * options 数组形式,对应选项的value
     * status true 恢复为可选 false 禁止选择
     **/
    setOptionsStatus: function (options, status) {
      var _self = this;
      var sleectItems = _self.container.find('.well-select-dropdown-list .well-select-item');
      sleectItems.each(function () {
        var $this = $(this);
        var data = $this.data();
        if (options.indexOf(data.value) > -1) {
          if (status) {
            $this.removeClass('well-select-item-disabled');
          } else {
            $this.addClass('well-select-item-disabled');
          }
        }
      });
    },

    // abstract
    disable: function (enabled) {
      if (enabled === undefined) enabled = false;
      if (this._enabled === enabled) return;
      this._enabled = enabled;

      this.$element.attr('disabled', enabled);
      this.container.toggleClass('wellSelect-container-disabled', enabled);
    },

    // abstract
    readonly: function (enabled) {
      if (enabled === undefined) enabled = false;
      if (this._readonly === enabled) return;
      this._readonly = enabled;

      this.$element.attr('readonly', enabled);
      this.container.toggleClass('wellSelect-container-disabled', enabled);
    },

    // abstract
    opened: function () {
      return this.container ? this.container.hasClass('select2-dropdown-open') : false;
    },
    enable: function () {
      this.disable(false);
    },
    hide: function () {
      var container = this.container;
      container && container.hide();
    },
    show: function () {
      var container = this.container;
      container && container.show();
    }
  };
  //在插件中使用wellSelect对象
  $.fn.wellSelect = function (options) {
    var args = Array.prototype.slice.call(arguments, 0),
      opts,
      wellSelect,
      method,
      value,
      multiple,
      allowedMethods = [
        'val',
        'destroy',
        'opened',
        'hide',
        'show',
        'open',
        'close',
        'focus',
        'isFocused',
        'container',
        'dropdown',
        '_ajax',
        'onSortStart',
        'onSortEnd',
        'enable',
        'disable',
        'readonly',
        'positionDropdown',
        'data',
        'search',
        'reRenderOption',
        'setOptionsStatus'
      ],
      valueMethods = ['opened', 'isFocused', 'container', 'dropdown'],
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

        //创建wellSelect的实体
        wellSelect = new WellSelect(this, options);
        wellSelect.init(opts);
      } else if (typeof args[0] === 'string') {
        if (allowedMethods.indexOf(args[0]) < 0) {
          throw 'Unknown method: ' + args[0];
        }

        value = undefined;
        wellSelect = $(this).data('wellSelect');
        if (wellSelect === undefined) return;

        method = args[0];

        if (method === 'container') {
          value = wellSelect.container;
        } else if (method === 'dropdown') {
          value = wellSelect.dropdown;
        } else {
          if (methodsMap[method]) method = methodsMap[method];
          value = wellSelect && wellSelect[method].apply(wellSelect, args.slice(1));
        }
        if (valueMethods.indexOf(args[0]) >= 0 || (propertyMethods.indexOf(args[0]) >= 0 && args.length === 1)) {
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
