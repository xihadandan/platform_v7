var a = function (options, appFunction) {
    var targetWidgetId = options.targetWidgetId;
    // 当前窗口组件没有指定渲染的目标组件，使用当前页面容器
    if (target === _widget && StringUtils.isBlank(targetWidgetId)) {
        targetWidgetId = appContext.getPageContainer().getId();
    } else if (target === _self) {
        // 当前窗口，使用当前页面容器
        targetWidgetId = appContext.getPageContainer().getId();
    } else if (target === _blank) {
        // 新窗口，暂时使用当前页面容器
        // TODO
        targetWidgetId = appContext.getPageContainer().getId();
    }
    var renderOptions = {
        renderTo: "#" + targetWidgetId,
        widgetDefId: appFunction.id,
        refreshIfExists: options.refreshIfExists
    };
    options.appContext.renderWidget(renderOptions);
    return true;
};