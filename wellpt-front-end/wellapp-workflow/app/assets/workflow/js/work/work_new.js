$(function() {
	var bean = {
		"taskUuid" : null,
		"flowInstUuid" : null,
		"flowDefUuid" : null,
		"title" : null,
		"name" : null,
		"formUuid" : null,
		"DataUuid" : null,
		"formName" : null,
		"formVersion" : null,
		"formAndDataBean" : null,
		"applicant" : null,
		"fromTime" : null,
		"toTime" : null,
		"reason" : null,
		"createTime" : null
	};

	var dytableId = "dyform";

	var saveService = "workService.save";
	var submitService = "workService.submit";
	var service = saveService;

	bean.flowDefUuid = $("#flowDefUuid").val();
	bean.flowInstUuid = $("#flowInstUuid").val();
	bean.taskUuid = $("#taskUuid").val();
	bean.formUuid = $("#formUuid").val();
	bean.dataUuid = $("#dataUuid").val();

	// JQuery UI按钮
	$("input[type=submit], a, button", $("#toolbar")).button();

	// 初使化
	JDS.call({
		service : "workService.getWorkData",
		data : [ bean ],
		success : function(result) {
			$("#" + dytableId).dytable({
				data : result.data.formAndDataBean,
				btnSubmit : "btn_save",
				beforeSubmit : save
			});
		}
	});

	// 保存
	function save() {
		var rootFormData = $("#" + dytableId).dytable("formData");
		bean.rootFormDataBean = rootFormData;
		JDS.call({
			service : service,
			data : [ bean ],
			success : function(result) {
				alert("操作成功！");
				service = saveService;
			},
			error : function(jqXHR) {
				service = saveService;
				alert(JSON.stringify(jqXHR));
			}
		});
	}

	// 提交
	$("#btn_submit").click(function() {
		service = submitService;
		$('#btn_save').trigger('click');
	});
});