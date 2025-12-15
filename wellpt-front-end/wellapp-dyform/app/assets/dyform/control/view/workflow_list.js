$(function(){
	
	var parmStr = $("#parmStr").val();
	 
	var whereSqlParam = "&whereSql=";
	parmStr = parmStr.substring(parmStr.indexOf(whereSqlParam) + whereSqlParam);//
 
	//console.log((parmStr));
	var decodedParmStr = urldecode(parmStr);
	var beginIndexChars = "whereSql={";
	var beginIndex = decodedParmStr.indexOf(beginIndexChars);
	var workflowInfoJsonstr = decodedParmStr.substring(beginIndex + beginIndexChars.length - 1);
	var endIndexChars = "}&";
	var endIndex = workflowInfoJsonstr.indexOf(endIndexChars);
	workflowInfoJsonstr = workflowInfoJsonstr.substring(0, endIndex + 1);
	console.log(workflowInfoJsonstr);
	var workflowInfo = JSON.parse(workflowInfoJsonstr);
	console.log(JSON.stringify(workflowInfo));
	//{"workflowId":"DDJ_CWGL_THD;DDJ_CWGL_FKD;DDJ_CWGL_FHD","workflowName":"DDJ_财务管理/退货单;DDJ_财务管理/付款单;DDJ_财务管理/发货单"}
	var workflowId = workflowInfo.workflowId;
	var workflowName = workflowInfo.workflowName;
	var workflowIds = workflowId.split(";");
	var workflowNames = workflowName.split(";");
	fillFlowDefUuid(workflowIds, workflowNames);
	//var $workflowSelect = $("<select name='workFlowId' id='workFlowId'><option value=''></option></select>");
	//FLOW_DEF_UUID
	
	$("#showButton").hide();
	$("#keySelect").hide();
	$("#fieldSelect").show();
	
	//var viewUuid = $("#viewUuid").val();
	//var containerClass = "abc" + viewUuid;
	//$("." + containerClass).prepend($workflowSelect);
	$("#keyWord").hide();
	//alert($(".view_search").is(":hidden"));
	window.setTimeout(function(){
		$(".view_search").show();
	}, 1*1000);
});


function fillFlowDefUuid(workflowIds, workflowNames){
	window.setTimeout(function(){ 
		var $workflowSelect = $("select[name='FLOW_DEF_UUID']"); 
		if($workflowSelect.size() == 0){
			fillFlowDefUuid(workflowIds, workflowNames);
			return;
		}
		
		var control = $("#FLOW_DEF_UUID").wcomboBox("getObject");
		var optionSet = {};
		for(var i = 0; i < workflowIds.length; i++){
			optionSet[workflowIds[i] ] = workflowNames[i];
		}
		control.setOptionSet(optionSet);
		control.reRender();//重新渲染
	}, 100);
}