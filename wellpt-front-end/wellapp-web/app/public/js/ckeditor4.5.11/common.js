
function selColor(color){
	 $("#scolor").val(color);
    $("#subject").css("color",color);
    $("#fontcolorElement").css("background-color",color);
    $("#fontColor").val(color); 
}

function checkMust(_this){
	if(!_this.checked){  
        changeStatus("checkRule_6",false);
        changeStatus("checkRule_5",false);
        $("#unitUnique").val(null)
	}
}
function checkUnique(_this){
	if(_this.checked){
        changeStatus("checkRule_6",false);
        changeStatus("checkRule_1",true);
        $("#unitUnique").val("false")
	}else{
        $("#unitUnique").val(null)
    }
}

function checkUnitUnique(_this){
    if(_this.checked){
        changeStatus("checkRule_5",false);
        changeStatus("checkRule_1",true);
        $("#unitUnique").val("true")
    }else{
        $("#unitUnique").val(null)
    }
}

function changeStatus(ele,status){
    $("#"+ele).each(function(){
        this.checked  = status;
    }).trigger("change");
}