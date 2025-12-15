define([ "ui_component", "design_commons", "constant", "commons"], function() {
	var component = $.ui.component.BaseComponent();
	component.prototype.create = function() {
		
	}
	component.prototype.configure = function() {
		
	}
	component.prototype.getDefinitionJson = function() {
		var options = this.options;
		var id = this.getId();
		options.id = id;
		return options;
	}
	return component;
});