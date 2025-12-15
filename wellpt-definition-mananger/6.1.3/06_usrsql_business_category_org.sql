-- 备份BUSINESS_CATEGORY_ORG 用于批量更新分类Id
create table BUSINESS_CATEGORY_ORG_BAK as select * from BUSINESS_CATEGORY_ORG;

-- 以上脚本执行完后 按如下操作执行 历史数据调整
/*

组织弹出框中的"业务通讯录"的不同的节点类型有不同的图标
需求：http://zen.well-soft.com:81/zentao/task-view-4253.html
项目重构启动后，登陆
然后在页面控制台执行：
JDS.call({
	service : 'businessCategoryOrgService.updateOldId',
	data : [],
	async : false,
	success : function(result) {
		console.log(result);
	},
	error: function(error) {
	    console.log(error);
	}
});
返回数据如下：

{code: 0, msg: "success", data: "读取数据表或字段错误，请检查表或字段是否存在：{"uf_test_zj":["zuzhi","bm"…],"uf_test_bdjs":["bdjs"],"uf_test_drbd1":["z

出现如上情况是 查询有配置 BusinessBook 的组织弹出框字段的 表单定义存在，但数据库表或字段可能没有生成
根据提示的表，字段 排查数据库是否存在
根据实际情况 排查问题后，可选择忽略以上提示。或再次执行以上JDS操作。

 */
