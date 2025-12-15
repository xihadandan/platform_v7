
$(function() {
	$('#addBtn')
			.click(
					function() {

						var task = $('#task').val();
						var taskname = $('#task').find("option:selected")
								.text();
						var workcount = $('#workcount').val();
						var selfEvaluate = $('#selfEvaluate').val();
						var state = $('#stateUuid').val();
						var statename = $('#stateUuid').find("option:selected")
								.text();
						var memo=$('#memo').val();
						var re = /^[0-9]*[1-9][0-9]*$/;
						if (workcount == null || workcount == ''
								|| !re.test(workcount)) {
							$.jBox.info(editpage.workcount, glob.title);
							return;
						}
						if (state == done_state
								&& (selfEvaluate == null || selfEvaluate == '')) {
							$.jBox.info(editpage.selfEvaluate, glob.title);
							return;

						}
						if(memo==null||memo==''){
							$.jBox.info(editpage.memo, glob.title);
							return;
						}
						if(selfEvaluate==null||selfEvaluate==''){
							selfEvaluate="  ";
						}
						if (!findTasks(task)) {
							tasks[tasks.length] = task;
							appendTbody(task, taskname, workcount,
									selfEvaluate, state, statename,memo);
						} else {
							var submit = function(v, h, f) {
								if (v == 'ok') {
									changeTbody(task, taskname, workcount,
											selfEvaluate, state, statename,memo);
								}

							};

							$.jBox.confirm(editpage.editmiss, glob.title,
									submit);
						}

					});
	$('#delBtn').click(function() {
		var checks = document.getElementsByName("tsks");
		 
		for ( var i = checks.length - 1; i >= 0; i--) {
			 
			if (checks[i].checked) {
				delTasks(checks[i].value);
				var tr = checks[i].parentNode.parentNode;
				var t = document.getElementById("tbdy");
				t.removeChild(tr);
			}
		}

	});
	$('#submitBtn').click(function() {
		var b=false;
		for(var i=0;i<tasks.length;i++	){
			if(tasks[i]!=null){ 
				b=true;
			}
		}
		if(b==false){
			$.jBox.info(editpage.submitdetail, glob.title);
			return false;
		}
	});

});

function changeTbody(task, taskname, workcount, selfEvaluate, state, statename,memo) {
	var t = document.getElementById("tbdy");
	for ( var i = 0; i < t.childNodes.length; i++) {
	 
		var tr = t.childNodes[i];
		 if(tr.childNodes.length==0){
			 continue;
		 }
		var td = tr.childNodes[0];
		var task_value = td.childNodes[1].value;
		if (task == task_value) {
			td.childNodes[2].value = workcount;
			td.childNodes[3].value = selfEvaluate;
			td.childNodes[4].value = state;
			td.childNodes[5].innerText = taskname;
			td.childNodes[6].value = memo;
			var td2 = tr.childNodes[1];
			td2.innerHTML = statename;
		}
	}

}
function appendTbody(task, taskname, workcount, selfEvaluate, state, statename,memo,detailUuid) {

	var td1 = document.createElement("td");
	var htm = "<input type='checkbox' name='tsks' value='" + task + "'/>";
	htm = htm + "<input type='hidden' name='task' value='" + task + "'/>";
	htm = htm + "<input type='hidden' name='workcount' value='" + workcount
			+ "'/>";
	htm = htm + "<input type='hidden' name='selfEvaluate' value='"
			+ selfEvaluate + "'/>";
	htm = htm + "<input type='hidden' name='state' value='" + state + "'/>";
	htm = htm + "<span>" + taskname + "</span>";
	htm = htm + "<input type='hidden' name='memo' value='" + memo + "'/>";
	htm = htm + "<input type='hidden' name='detailUuids' value='" + detailUuid + "'/>";
	td1.innerHTML = htm;
	var td2 = document.createElement("td");
	td2.innerHTML = statename;
	var tr = document.createElement("tr");
	tr.appendChild(td1);
	tr.appendChild(td2);
	
	tr.ondblclick=function( ){
		var td = this.childNodes[0];  
		 $('#selfEvaluate').val(td.childNodes[3].value);
		 $('#workcount').val(td.childNodes[2].value);
		 $('#stateUuid').val(td.childNodes[4].value );
		 $('#task').val(td.childNodes[1].value); 
		 $('#memo').val(td.childNodes[6].value);
//		 document.getElementById('memo').value=td.childNodes[6].value;
	};
	var t = document.getElementById("tbdy");
	t.appendChild(tr); 

} 
function delTasks(uuid) {
	for ( var i = tasks.length - 1; i >= 0; i--) {
		if (tasks[i] == uuid) {
			tasks[i] = null;
		}
	}
}
function findTasks(uuid) {
	for ( var i = 0; i < tasks.length; i++) {
		if (tasks[i] == uuid) {
			return true;
		}
	}
	return false;
}