var WUnitClass = function() {
	this.mutiSelect = true;// 是否多选
	this.showUnitType = true;// 分类是否显示
	this.filterCondition = "";
	this.toJSON = toJSON;
	this.orgVersionId = ""; // 组织对应的版本ID

};

WUnitClass.prototype = new MainFormFieldClass();
