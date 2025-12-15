define([ "commons", "constant", "server" ], function(commons, constant, server) {
	var MobileListDevelopment = function(wWidget) {
		this.wWidget = wWidget;
	};
	/**
	 * 视图组件渲染前回调方法，子类可覆盖
	 */
	MobileListDevelopment.prototype.beforeRender = function(options, configuration) {
	};
	/**
	 * 视图组件渲染完成回调方法，子类可覆盖
	 */
	MobileListDevelopment.prototype.afterRender = function(options, configuration) {
	};
	/**
	 * 行点击的时候回调方法，子类可覆盖
	 */
	MobileListDevelopment.prototype.onClickRow = function(index, data, $element, event) {
	};
	/**
	 * 添加行li节点的时候回调，子类可覆盖
	 */
	MobileListDevelopment.prototype.onAddLiElement = function(index, data, $element) {
	};
	/**
	 * 下拉刷新时回调 可以通过 event.preventDefault()阻止刷新
	 */
	MobileListDevelopment.prototype.onPulldown = function(event) {
	};
	/**
	 * 上拉加载时回调 可以通过 event.preventDefault()阻止加载
	 */
	MobileListDevelopment.prototype.onPullup = function(event) {
	};
	/**
	 * 根据过滤函数获取列表数据 filter参数 li对象 data列数据 index列索引 返回数据格式 { li:li对象,
	 * data:列数据,index:列索引} }
	 */
	MobileListDevelopment.prototype.getRowDatas = function(filter) {
		return this.getWidget().getRowDatas(filter);
	};
	/**
	 * 根据索引获取索引位置的li对象,索引为空返回全部
	 */
	MobileListDevelopment.prototype.getLiElement = function(index) {
		return this.getWidget().getLiElement(index);
	};
	/**
	 * 根据索引获取索引位置的列数据,索引为空返回全部
	 */
	MobileListDevelopment.prototype.getData = function(index) {
		return this.getWidget().getData(index);
	};
	/**
	 * 根据列li对象返回索引属性
	 */
	MobileListDevelopment.prototype.getIndexByLi = function(li) {
		return this.getWidget().getIndexByLi(li);
	};
	/**
	 * 设置上拉加载的状态 type = done设置上拉不再加载（没有更多数据） type=reset 将上拉状态重新设置为可以加载
	 */
	MobileListDevelopment.prototype.setPullupStatus = function(type) {
		return this.getWidget().setPullupStatus(type);
	};
	/**
	 * 清除视图参数
	 */
	MobileListDevelopment.prototype.clearParams = function() {
		this.getWidget().clearParams();
	};
	/**
	 * 添加视图参数
	 */
	MobileListDevelopment.prototype.addParam = function(key, value) {
		this.getWidget().addParam(key, value);
	};
	/**
	 * 移除视图参数
	 */
	MobileListDevelopment.prototype.removeParam = function(key) {
		return this.getWidget().removeParam(key);
	};
	/**
	 * 获取视图参数
	 */
	MobileListDevelopment.prototype.getParam = function(key) {
		return this.getWidget().getParam(key);
	};
	/**
	 * 获取选中列数据集合
	 */
	MobileListDevelopment.prototype.getSelections = function() {
		return this.getWidget().getSelections();
	};
	/**
	 * 获取选中列索引集合
	 */
	MobileListDevelopment.prototype.getSelectionIndexes = function() {
		return this.getWidget().getSelectionIndexes();
	};
	/**
	 * 获取视图数据集合
	 */
	MobileListDevelopment.prototype.getAllData = function() {
		return this.getWidget().getData();
	};
	/**
	 * 根据唯一键获取列数据
	 */
	MobileListDevelopment.prototype.getRowByUniqueId = function(id) {
		return this.getWidget().getRowByUniqueId(id);
	};
	/**
	 * 刷新数据 options{ notifyChange:false,//是否触发条数变更通知
	 * conditions:[{columnIndex:'',value:'',type:'like'}]//条件对象 }//
	 */
	MobileListDevelopment.prototype.refresh = function(options) {
		return this.getWidget().refresh(options);
	};
	/**
	 * 页码跳转
	 */
	MobileListDevelopment.prototype.selectPage = function(pageNum) {
		return this.getWidget().selectPage(pageNum);
	};
	/**
	 * 重新计算（设置）视图高度
	 */
	MobileListDevelopment.prototype.resetHeight = function(height) {
		return this.getWidget().resetHeight(height);
	};
	/**
	 * 获取视图定义信息
	 */
	MobileListDevelopment.prototype.getViewConfiguration = function() {
		return this.getWidget().getConfiguration();
	};
	/**
	 * 获取获取数据源对象
	 */
	MobileListDevelopment.prototype.getDataProvider = function() {
		return this.getWidget().getDataProvider();
	};
	MobileListDevelopment.prototype.ascOtherOrders = function(sortNames){
		return this.getWidget().ascOtherOrders(sortNames);
	},
	MobileListDevelopment.prototype.descOtherOrders = function(sortNames){
		return this.getWidget().descOtherOrders(sortNames);
	},
	/**
	 * 添加额外的查询排序
	 */
	MobileListDevelopment.prototype.addOtherOrder = function(sortName, sortOrder) {
		return this.getWidget().addOtherOrder(sortName, sortOrder);
	},
	/**
	 * 添加额外的查询条件
	 */
	MobileListDevelopment.prototype.addOtherConditions = function(conditions) {
		this.getWidget().addOtherConditions(conditions);
	};
	/**
	 * 情况额外查询条件,condition为空是清楚全部，否则清楚等于condition的一条额外查询条件
	 */
	MobileListDevelopment.prototype.clearOtherConditions = function(condition) {
		this.getWidget().clearOtherConditions(condition);
	};
	/**
	 * 情况额外查询条件,condition为空是清楚全部，否则清楚等于condition的一条额外查询条件
	 */
	MobileListDevelopment.prototype.clearOtherConditions = function(condition) {
		this.getWidget().clearOtherConditions(condition);
	};
	/**
	 * 获取视图组件对象
	 */
	MobileListDevelopment.prototype.getWidget = function() {
		return this.wWidget;
	};
	return MobileListDevelopment;
});