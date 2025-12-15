

function VueWidgetDevelopment($widget) {
  this.$widget = $widget; // vue组件实例
}

VueWidgetDevelopment.prototype.getWidget = function () {
  return this.$widget;
};

VueWidgetDevelopment.prototype.getPageContext = function () {
  return this.$widget.pageContext;
};

VueWidgetDevelopment.prototype.getVueWidgetById = function (id) {
  return this.getWidget().getVueWidgetById(id);
};

VueWidgetDevelopment.prototype.getVueWidgetByCode = function (code) {
  return this.getWidget().getVueWidgetByCode(code);
};

VueWidgetDevelopment.prototype.emitEvent = function () {
  this.getWidget().pageContext.emitEvent.apply(this.getWidget().pageContext, arguments);
};

VueWidgetDevelopment.prototype.handleEvent = function (eventName, callback) {
  this.getWidget().pageContext.handleEvent(eventName, callback);
};

VueWidgetDevelopment.prototype.offEvent = function (eventName) {
  this.getWidget().pageContext.offEvent(eventName);
};

VueWidgetDevelopment.prototype.getUser = function () {
  return this.getWidget()._$USER;
};
VueWidgetDevelopment.prototype.getTenantID = function () {
  return this.getWidget()._$USER != undefined ? this.getWidget()._$USER.tenantId : 'T001';
};
VueWidgetDevelopment.prototype.getSystemID = function () {
  return this.getWidget()._$SYSTEM_ID;
};

VueWidgetDevelopment.prototype.created = function () { };
VueWidgetDevelopment.prototype.beforeUpdate = function () { };
VueWidgetDevelopment.prototype.updated = function () { };
VueWidgetDevelopment.prototype.beforeDestroy = function () { };
VueWidgetDevelopment.prototype.destroyed = function () { };
VueWidgetDevelopment.prototype.beforeMount = function () { };
VueWidgetDevelopment.prototype.mounted = function () { };
VueWidgetDevelopment.prototype.ROOT_CLASS = "VueWidgetDevelopment"

export default VueWidgetDevelopment;
