(function (factory) {
  'use strict';
  if (typeof define === 'function' && define.amd) {
    // AMD. Register as an anonymous module.
    define(['jquery', 'commons', 'server', 'constant', 'dataStoreBase'], factory);
  } else {
    // Browser globals
    factory(jQuery);
  }
})(function ($, commons, server, constant, DataStore) {
  'use strict';
  var panelTemplate = '<div class="panel panel-default">';
  panelTemplate += '<div class="panel-heading" style="display:none;"></div>';
  panelTemplate += '<div class="panel-body"></div></div>';
  var StringUtils = commons.StringUtils;
  var getBgColor = function (color) {
    if (StringUtils.isBlank(color)) {
      return '';
    }
    return 'background-color:' + color + ' !important;';
  };
  var getFgColor = function (color) {
    if (StringUtils.isBlank(color)) {
      return '';
    }
    return 'color:' + color + ' !important;';
  };
  $.widget('ui.wTiles', $.ui.wWidget, {
    options: {
      // 组件定义
      widgetDefinition: {},
      // 上级容器定义
      containerDefinition: {},
      jsModules: {}
    },
    _createView: function () {
      var _self = this;
      var coniguration = _self.getConfiguration();
      _self.invokeDevelopmentMethod('beforeRender', [_self.options, coniguration]);
      _self._renderView();
      _self.invokeDevelopmentMethod('afterRender', [_self.options, coniguration]);
    },
    _setOption: function (key, value) {
      // 设置参数
      this.options[key] = value;
      return this;
    },
    /**
     * 获取配置对象
     */
    getConfiguration: function () {
      return this.options.widgetDefinition.configuration;
    },
    _renderTiles: function () {
      // 生成页面组件
      var self = this;
      var configuration = self.getConfiguration();
      $(self.element).append(panelTemplate);
      if (configuration.showTitleToolbar) {
        $('.panel-heading', self.element).show().append(configuration.title);
      }
      var $panelBody = $('.panel-body', self.element);
      if (StringUtils.isNotBlank(configuration.backgroundColor)) {
        $panelBody.css('background-color', configuration.backgroundColor);
      }

      if (StringUtils.isNotBlank(configuration.width)) {
        $('.panel', self.element).css('width', configuration.width);
      }
      if (StringUtils.isNotBlank(configuration.height)) {
        $('.panel', self.element).css('height', configuration.height);
      }
      var content = new commons.StringBuilder();
      var hasCarousel = false;
      content.append("<div class='tile-container'>");
      $.each(configuration.tiles, function (index, tile) {
        var returnTile = self.invokeDevelopmentMethod('initTile', [tile, self.getConfiguration()]);
        if (returnTile && typeof returnTile != 'undefined' && $.isPlainObject(returnTile)) {
          tile = returnTile;
        }
        var tileTpl = "<div class='tile-{0} wTiles {3}'  style='{1} {2}'  data-role='tile'>";
        var fgColor = getFgColor(tile.foregroundColor);
        var bgColor = getBgColor(tile.backgroundColor);
        content.appendFormat(tileTpl, tile.size, fgColor, bgColor, tile.code);
        var contentOptions = tile.contentOptions;
        if (contentOptions.type == 'icon' || tile.contentOptions.type == 'text') {
          content.append(self._getIconOrTextTile(tile));
        } else if (contentOptions.type == 'image') {
          if (contentOptions.effect == 'carousel') {
            hasCarousel = true;
            content.append(self._getCarouselTile(tile));
          } else if (contentOptions.effect == 'slide') {
            content.append(self._getSideTile(tile));
          } else if (contentOptions.effect == 'slide2') {
            content.append(self._getSide2Tile(tile));
          } else if (contentOptions.effect == 'zooming' || contentOptions.effect == 'zooming-out') {
            content.append(self._getZoominglTile(tile));
          } else if (contentOptions.effect == 'container') {
            content.append(self._getImageContainerTile(tile));
          } else if (contentOptions.effect == 'imageSet') {
            content.append(self._getImageSetTile(tile));
          } else if (contentOptions.effect == 'fit') {
            content.append(self._getImageTile(tile));
          }
        }
        content.appendFormat('</div>');
        if (!$.isEmptyObject(tile.eventHandler)) {
          var eventHandler = tile.eventHandler;
          var eventParams = tile.eventParams || {};
          var target = tile.target || {};
          var opt = {
            target: target.position,
            targetWidgetId: target.widgetId,
            refreshIfExists: target.refreshIfExists,
            appType: eventHandler.type,
            appPath: eventHandler.path,
            params: $.extend({}, eventParams.params, appContext.parseEventHashParams(eventHandler, 'menuid')),
            selection: eventHandler.id,
            view: self,
            viewOptions: self.options
          };
          $panelBody.on('click', '.' + tile.code, function (event) {
            if ($(event.target).is('img')) {
              opt.index = $(event.target).attr('index');
            }
            opt.event = event;
            self.startApp(opt);
          });
        } else {
          $panelBody.on('click', '.' + tile.code, function (event) {
            var args = [event, tile, configuration];
            if ($(event.target).is('img')) {
              args.push($(event.target).attr('index'));
            }
            self.invokeDevelopmentMethod(tile.code, args);
          });
        }
      });
      content.append('</div>');
      $panelBody.append(content.toString());
      if (hasCarousel) {
        $.metro.carousel({}, '.carousel');
      }
      this._initBadgetCount($panelBody);
      this._listeningBootstrapTableChange();
    },
    _renderListView: function () {
      var self = this;
      var configuration = self.getConfiguration();
      $.extend(configuration, {
        type: '1'
      });
      $(self.element).append(panelTemplate);
      var $panel = $('.panel', self.element).addClass('ui-wTiles-tip'); // 磁贴详情
      var $panelHeading = $('.panel-heading', self.element).addClass('col-xs-2'); // 磁贴详情
      var $panelBody = $('.panel-body', self.element).addClass('col-xs-10 clearfix'); // 磁贴详情
      if (StringUtils.isNotBlank(configuration.width)) {
        $panel.css('width', configuration.width);
      }
      if (StringUtils.isNotBlank(configuration.height)) {
        $panel.css('height', configuration.height); // px
        $panelHeading.css('line-height', configuration.height + 'px');
      }
      if (configuration.showTitleToolbar) {
        $('.panel-heading', self.element).show().append(configuration.title);
      }
      $(configuration.tiles).each(function (idx, tile) {
        if (idx >= 3) {
          return false; // 只展示前三个
        }
        var content = new commons.StringBuilder();
        content.appendFormat('<div class="col-xs-4 column {0}">', 'col-idx-' + idx);
        content.appendFormat('<div class="panel ui-wTiles-panel">');
        content.appendFormat(
          '<div class="panel-heading"><span class="badge pull-left">{0}</span><h3 class="panel-title event-handler">{1}</h3></div>',
          idx,
          tile.subhead
        );
        content.appendFormat(
          '<div class="panel-body"><div class="bootstrap-table"><div class="fixed-table-toolbar"></div><div class="fixed-table-container" style="padding-bottom: 0px;"><div class="fixed-table-header" style="display: none;"><table></table></div><div class="fixed-table-body"><div class="fixed-table-loading" style="top: 41px; display: none;">数据加载中，请稍候……</div><div class="JCLRgrips" style="width: 341px;"><div class="JCLRgrip" style="left: 37px; height: 423px;"><div class="JColResizer"></div></div><div class="JCLRgrip" style="left: 197px; height: 423px;"><div class="JColResizer"></div></div><div class="JCLRgrip JCLRLastGrip" style="left: 358px; height: 423px;"></div></div><table id="wBootstrapTable_C78E607DB0700001764419FD129A1AE9_table" class="table table-hover table-striped JColResizer"><tbody><tr data-index="0"><td style="">手机流程测试1_系统管理员-开发部_2017-06-01</td></tr><tr data-index="1"><td style="">手机并行环节测试_系统管理员-开发部_2017-05-24</td></tr><tr data-index="2"><td style="">采购合同_开发部_系统管理员_2017-05-06</td></tr><tr data-index="3"><td style="">0408_系统管理员-开发部_2017-04-08</td></tr><tr data-index="4"><td style="">0408_系统管理员-开发部_2017-04-08</td></tr><tr data-index="5"><td style="">手机_zhuang（表单1.4）_系统管理员-开发部_2017-04-06</td></tr></tbody></table></div><div class="fixed-table-footer" style="display: none;"><table><tbody><tr></tr></tbody></table></div></div></div></div>'
        );
        $panelBody.append(content.toString());
      });
      $panelBody.append("<div class='arrow glyphicon glyphicon-chevron-down'></div>");
    },
    _renderView: function () {
      var self = this;
      var configuration = self.getConfiguration();
      if (configuration.type === '1') {
        // 磁贴展开
        self._renderListView();
      } else {
        // 磁贴折叠
        self._renderTiles();
      }
    },
    _listeningBootstrapTableChange: function () {
      var self = this;
      var configuration = self.getConfiguration();
      var pageContainer = self.pageContainer;
      pageContainer.on(constant.WIDGET_EVENT.PageContainerCreationComplete, function () {
        $.each(configuration.tiles, function (index, tile) {
          if ($.isEmptyObject(tile.badge)) {
            return;
          }
          if (!tile.badge.countWay && tile.badge.countJsModule) {
            tile.badge.countWay = 'countJs';
          }
          var listViewId = tile.badge.widgetDefId;
          if (tile.badge.countWay === 'countJs' && listViewId) {
            var listViewWidget = appContext.getWidgetById(listViewId);
            if (listViewWidget != null) {
              listViewWidget.on(constant.WIDGET_EVENT.Change, function (e, ui) {
                var totalCount = ui.getDataProvider().getCount();
                self._setBadgetCount(tile, totalCount);
              });
            }
          } else if (tile.badge.countWay === 'dataStoreCount' && tile.badge.dataStoreCounter) {
            //按数据仓库的数据量计算徽章数量
            if (!self._badgeCountDataProvider) {
              self._badgeCountDataProvider = new DataStore({
                dataStoreId: tile.badge.dataStoreCounter,
                onDataChange: function (data, count) {
                  if (count != 0) {
                    self._setBadgetCount(tile, count, count);
                  }
                }
              });
            }
            self._badgeCountDataProvider.getCount(true);
          }
        });
      });
    },
    _getBadge: function (tile) {
      if ($.isEmptyObject(tile.badge)) {
        return '';
      }
      if (!tile.badge.dataStoreCounter && !tile.badge.countJsModule) {
        return '';
      }
      var content = new commons.StringBuilder();
      var spanTpl = "	<span class='tile-badge' style='{0}'></span>";
      content.appendFormat(spanTpl, getBgColor(tile.badgeBackgroundColor));
      return content.toString();
    },
    _getBadgetCount: function (tile) {
      var self = this;
      if (!$.isEmptyObject(tile.badge)) {
        if (!tile.badge.countWay && tile.badge.countJsModule) {
          tile.badge.countWay = 'countJs';
        }
        if (tile.badge.countWay === 'countJs' && tile.badge.countJsModule) {
          var countJsModule = tile.badge.countJsModule;
          if (StringUtils.isNotBlank(countJsModule)) {
            self.startApp({
              isJsModule: true,
              jsModule: countJsModule,
              action: 'getCount',
              data: tile.badge,
              tileCode: tile.code,
              callback: function (count, realCount, options) {
                self._setBadgetCount(tile, count, realCount);
              }
            });
          }
        } else if (tile.badge.countWay === 'dataStoreCount' && tile.badge.dataStoreCounter) {
          //按数据仓库的数据量计算徽章数量
          if (!self._badgeCountDataProvider) {
            self._badgeCountDataProvider = new DataStore({
              dataStoreId: tile.badge.dataStoreCounter,
              onDataChange: function (data, count) {
                if (count != 0) {
                  self._setBadgetCount(tile, count, count);
                }
              }
            });
          }
          self._badgeCountDataProvider.getCount(true);
        }
      }
    },
    _setBadgetCount: function (tile, count, realCount) {
      var _self = this;
      $('.' + tile.code + ' .tile-badge', $(_self.element)).html(count);
    },
    _initBadgetCount: function () {
      var self = this;
      var configuration = self.getConfiguration();
      $.each(configuration.tiles, function (index, tile) {
        self._getBadgetCount(tile);
      });
    },
    _getLabel: function (tile) {
      if (StringUtils.isBlank(tile.subhead)) {
        return '';
      }
      var content = new commons.StringBuilder();
      content.appendFormat("	<span class='tile-label'>{0}</span>", tile.subhead);
      return content.toString();
    },
    _getIconOrTextTile: function (tile) {
      var content = new commons.StringBuilder();
      content.appendFormat("<div class='tile-content iconic'>");
      if (tile.contentOptions.type == 'icon') {
        content.appendFormat("	<span class='icon {0}'></span>", tile.contentOptions.icon);
      } else {
        content.appendFormat("	<span class='icon'>{0}</span>", tile.contentOptions.text);
      }
      content.appendFormat(this._getBadge(tile));
      content.appendFormat(this._getLabel(tile));
      content.appendFormat('</div>');
      return content.toString();
    },
    _getCarouselTile: function (tile) {
      var content = new commons.StringBuilder();
      content.appendFormat("    <div class='tile-content'>");
      var carouselTpl = "        <div class='carousel' data-role='carousel' data-controls='false' ";
      carouselTpl += "data-markers='true' data-auto='true' style='width: 100%; height: 150px;'>";
      content.appendFormat(carouselTpl);
      var slideTpl = "<div class='slide'><img src='{0}' index='{1}'></div>";
      $.each(tile.contentOptions.imgPaths, function (index, imagePath) {
        content.appendFormat(slideTpl, imagePath, index);
      });
      content.appendFormat('        </div>');
      content.appendFormat(this._getBadge(tile));
      content.appendFormat(this._getLabel(tile));
      content.appendFormat('    </div>');
      return content.toString();
    },
    _getZoominglTile: function (tile) {
      var content = new commons.StringBuilder();
      content.appendFormat("    <div class='tile-content {0}'>", tile.contentOptions.effect);
      var slideTpl = "        <div class='slide'><img src='{0}' index='0'></div>";
      content.appendFormat(slideTpl, tile.contentOptions.imgPaths[0]);
      content.appendFormat(this._getBadge(tile));
      content.appendFormat(this._getLabel(tile));
      content.appendFormat('    </div>');
      return content.toString();
    },
    _getImageSetTile: function (tile) {
      var content = new commons.StringBuilder();
      var contentOptions = tile.contentOptions;
      content.appendFormat("    <div class='tile-content image-set'>");
      $.each(tile.contentOptions.imgPaths, function (index, imagePath) {
        content.appendFormat("    <img src='{0}' index='{1}'>", imagePath, index);
      });
      content.appendFormat(this._getBadge(tile));
      content.appendFormat(this._getLabel(tile));
      content.appendFormat('    </div>');
      return content.toString();
    },
    _getImageTile: function (tile) {
      var content = new commons.StringBuilder();
      var contentOptions = tile.contentOptions;
      content.appendFormat("    <div class='tile-content'>");
      content.appendFormat("        <img src='{0}'  index='0'>", tile.contentOptions.imgPaths[0]);
      content.appendFormat(this._getBadge(tile));
      content.appendFormat(this._getLabel(tile));
      content.appendFormat('    </div>');
      return content.toString();
    },
    _getImageContainerTile: function (tile) {
      var content = new commons.StringBuilder();
      var contentOptions = tile.contentOptions;
      content.appendFormat("    <div class='tile-content '>");
      content.appendFormat("        <div class='image-container'>");
      content.appendFormat("        <div class='frame'>");
      content.appendFormat("            <img src='{0}' index='0'>", contentOptions.imgPaths[0]);
      content.appendFormat('        </div>');
      content.appendFormat("        <div class='image-overlay' style='padding: 0 0.625rem;'>");
      if (StringUtils.isNotBlank(contentOptions.contentTitle)) {
        content.appendFormat('      <h3>{0}</h3>', contentOptions.contentTitle);
      }
      content.appendFormat('        	<p>{0}</p>', contentOptions.contentText);
      content.appendFormat('        </div>');
      content.appendFormat('        </div>');
      content.appendFormat(this._getBadge(tile));
      content.appendFormat(this._getLabel(tile));
      content.appendFormat('    </div>');
      return content.toString();
    },
    _getSideTile: function (tile) {
      var content = new commons.StringBuilder();
      var contentOptions = tile.contentOptions;
      content.appendFormat("    <div class='tile-content slide-{0}'>", contentOptions.slideDirection);
      content.appendFormat("        <div class='slide'>");
      content.appendFormat("            <img src='{0}' index='0'>", contentOptions.imgPaths[0]);
      content.appendFormat('        </div>');
      content.appendFormat("        <div class='slide-over ' style='padding: 0 0.625rem;'>");
      if (StringUtils.isNotBlank(contentOptions.contentTitle)) {
        content.appendFormat('      <h3>{0}</h3>', contentOptions.contentTitle);
      }
      content.appendFormat('        	<p>{0}</p>', contentOptions.contentText);
      content.appendFormat('        </div>');
      content.appendFormat(this._getBadge(tile));
      content.appendFormat(this._getLabel(tile));
      content.appendFormat('    </div>');
      return content.toString();
    },
    _getSide2Tile: function (tile) {
      var content = new commons.StringBuilder();
      var contentOptions = tile.contentOptions;
      content.appendFormat("    <div class='tile-content slide-{0}-2'>", contentOptions.slideDirection);
      content.appendFormat("        <div class='slide'>");
      content.appendFormat("            <img src='{0}' index='0'>", contentOptions.imgPaths[0]);
      content.appendFormat('        </div>');
      var sile2TileTpl = "<div class='slide-over ' style='padding: 0 0.625rem;background-color:{0} !important'>";
      content.appendFormat(sile2TileTpl, contentOptions.slideBg);
      if (StringUtils.isNotBlank(contentOptions.contentTitle)) {
        content.appendFormat('      <h3>{0}</h3>', contentOptions.contentTitle);
      }
      content.appendFormat('        	<p>{0}</p>', contentOptions.contentText);
      content.appendFormat('        </div>');
      content.appendFormat(this._getBadge(tile));
      content.appendFormat(this._getLabel(tile));
      content.appendFormat('    </div>');
      return content.toString();
    }
  });
});
