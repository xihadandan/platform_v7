(function ($) {
  /*
   * TABLEVIEW CLASS DEFINITION ======================
   */
  var TableView = function ($tableElement, options) {
    this.options = $.extend({}, $.fn['wTableView'].defaults, options);
    this.widgetId = options.widgetId;
    this.$tableElement = $tableElement;
    this.columns = options.columns;
    this.defaultCondition = options.defaultCondition;
    this.formData = options.formData;
  };

  TableView.prototype = {
    constructor: TableView
  };

  TableView.prototype.render = function () {
    var _self = this;
    var cid = _self.widgetId + '' + new Date().getTime();
    var $container = $('<div>', { id: cid });
    if (_self.options.showType == '5') {
      $container.hide();
    }
    _self.$tableElement.wrap($container);
    $container.find('table').remove();
    var onPreapre = {};
    onPreapre[_self.widgetId] = function () {
      //重写列定义配置
      var columns = this.options.widgetDefinition.configuration.columns;
      for (var i = 0, len = columns.length; i < len; i++) {
        for (var j = 0, jlen = _self.columns.length; j < jlen; j++) {
          if (_self.columns[j].uuid == columns[i].uuid) {
            columns[i].hidden = _self.columns[j].hidden;
          }
        }
      }
      if (_self.defaultCondition) {
        var returnParams = appContext.resolveParams({ value: _self.defaultCondition }, _self.formData);
        this.options.widgetDefinition.configuration.defaultCondition = returnParams.value;
      }
    };
    appContext.renderWidget({
      refreshIfExists: false,
      renderTo: cid,
      widgetDefId: _self.widgetId,
      onPrepare: onPreapre,
      callback: function () {}
    });
  };

  $.TableView = {};

  /*
   * TABLEVIEW PLUGIN DEFINITION =========================
   */
  $.fn.wTableView = function (option) {
    new TableView($(this), option).render();
  };

  $.fn.wTableView.Constructor = TableView;

  $.fn.wTableView.defaults = {
    widgetId: null,
    columns: [],
    defaultCondition: null,
    formData: null
  };
})(jQuery);
