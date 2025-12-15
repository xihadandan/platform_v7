define([ "ui_component", "commons", "formBuilder", "appContext", "design_commons" ], function(ui_component, commons,
                                                                                              formBuilder, appContext, designCommons) {
    var StringUtils = commons.StringUtils;
    var StringBuilder = commons.StringBuilder;
    var component = $.ui.component.BaseComponent();
    component.prototype.create = function() {
        var _self = this;
        var $element = $(_self.element);
        var options = _self.options;
        function callback() {
            // 创建列数
            _self.createColumns();
            _self.$columns = $element.find("div.column");
            _self.$columns.addClass("ui-sortable");
            _self.pageDesigner.sortable(_self, _self.$columns, $element);
            // 初始化容器结点
            if (options.items != null && options.items.length > 0) {
                $element.find(".ui-sortable").each(function(i) {
                    var $placeHolder = $(this);
                    $.each(options.items, function(j) {
                        var item = this;
                        if (item.columnIndex === i) {
                            var $draggable = _self.pageDesigner.createDraggableByDefinitionJson(item);
                            _self.pageDesigner.drop(_self, $placeHolder, $draggable, item);
                        }
                    });
                });
            }
        }
        return callback();
    };
    // 创建列信息
    component.prototype.createColumns = function() {
        var _self = this;
        var $bootgrid = $(_self.element).find(".bootgrid");
        var columns = _self.options.configuration.columns;
        $.each(columns, function(index, column) {
            $bootgrid.append(getColumnHtml(column));
        });
    };

    // 获取列HTML
    function getColumnHtml(column) {
        return '<div class="col-xs-' + column.colspan + '"><div class="column" colspan="' + column.colspan + '" style="min-height: 100px"></div></div>';
    }

    // 返回定义的HTML
    component.prototype.toHtml = function($element) {
        var _self = this;
        var $element = $(_self.element);
        var options = _self.options;
        var columnMap = converColumnMap(options);
        var id = _self.getId();
        var html = new StringBuilder();
        html.appendFormat('<div id="{0}" class="row clearfix">', id);
        var children = _self.getChildren();
        var placeHolders = _self.$columns;
        $.each(placeHolders, function(i) {
            var $holder = $(this);
            var colspan = $holder.attr("colspan");
            var holderChildren = [];
            for (var index = 0; index < children.length; index++) {
                var child = children[index];
                if ($holder.has(child.element).length > 0) {
                    holderChildren.push(child);
                }
            }
            // 渲染的列宽度
            var renderWidth = columnMap[i].renderWidth;
            var widthStyle = '';
            if (StringUtils.isNotBlank(renderWidth)) {
                widthStyle = ' style="width:' + renderWidth + '" ';
            }
            // 子结点HTML
            var childHtml = "";
            $.each(holderChildren, function(i, child) {
                childHtml += child.toHtml();
            });
            html.appendFormat('<div class="col-xs-{0} col-idx-{1} column {2}" {3}>', colspan, i, columnMap[i]["class"], widthStyle);
            html.appendFormat(childHtml);
            html.appendFormat('</div>');
        });
        html.appendFormat('</div>');
        return html.toString();
    };

    // 使用属性配置器
    component.prototype.usePropertyConfigurer = function() {
        return true;
    };
    // 返回属性配置器
    component.prototype.getPropertyConfigurer = function() {

    };

    // 返回组件定义
    component.prototype.getDefinitionJson = function() {
        var _self = this;
        var $element = $(_self.element);
        var definitionJson = _self.options;
        var columnMap = converColumnMap(definitionJson);
        var id = _self.getId();
        definitionJson.id = id;
        definitionJson.items = [];
        definitionJson.configuration.columns = [];
        var children = this.getChildren();
        var placeHolders = this.$columns;
        $.each(placeHolders, function(i) {
            var $holder = $(this);
            var colspan = $holder.attr("colspan");
            var holderChildren = [];
            for (var index = 0; index < children.length; index++) {
                var child = children[index];
                if ($holder.has(child.element).length > 0) {
                    holderChildren.push(child);
                }
            }
            // 收集子结点定义
            $.each(holderChildren, function(j, child) {
                var childJson = child.getDefinitionJson();
                childJson.columnIndex = i;
                definitionJson.items.push(childJson);
            });
            // 标记所在列
            var column = {};
            column.index = i;
            column.colspan = colspan;
            if (columnMap[i]) {
                column.width = columnMap[i].width;
                column["class"] = columnMap[i]["class"];
            }
            definitionJson.configuration.columns.push(column);
        });
        return definitionJson;
    };

    function converColumnMap(definitionJson) {
        if (definitionJson == null || definitionJson.configuration == null
            || definitionJson.configuration.columns == null) {
            return {};
        }
        var map = {};
        var columns = definitionJson.configuration.columns;
        $.each(columns, function(i) {
            map[this.index] = this;
        })
        return map;
    }

    return component;
});