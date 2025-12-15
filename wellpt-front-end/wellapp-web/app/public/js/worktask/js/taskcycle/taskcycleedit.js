$(function() {
	init();
	$('#reptyType1').click(function() {

		$('#rpty1').show();
		$('#rpty2').hide();
	});
	$('#reptyType2').click(function() {

		$('#rpty2').show();
		$('#rpty1').hide();
	});

	$('#sharernames').click(function() {
		$.unit.open({
			labelField : "sharernames",
			valueField : "shareruuids",
			selectType : 4,
			multiple : true
		});
	});
	$('#closebtn').click(function() {
		window.close();
	});
	$('#submitbtn').click(function() {
		var beginDate = $('#beginDate').val();
		var endDate = $('#endDate').val();
		var timeAfter = $('#timeAfter').val();
		if ((timeAfter != null || timeAfter != '') && !isInt(timeAfter)) {

			//$('#timeAftererr').show();
			//return false;
		} else {
			$('#timeAftererr').hide();
		}
		if (beginDate == null || beginDate == '') {
			$('#beginDateerr').show();
			return false;
		} else {
			$('#beginDateerr').hide();
		}
		if (endDate == null || endDate == '') {
			$('#endDateerr').show();
			return false;
		} else {
			$('#endDateerr').hide();
		}

		var reptyType = $('input[name="reptyType"]:checked').val();
		if (reptyType == 1) {
			var rps = document.getElementsByName('reptyTime1');
			var v = "";
			for ( var i = 0; i < rps.length; i++) {
				if (rps[i].checked) {
					v = v + rps[i].value + ";";
				}
			}
			document.getElementById('reptyTime').value = v;
		} else {
			var rps = document.getElementsByName('reptyTime2');
			var v = "";
			for ( var i = 0; i < rps.length; i++) {
				if (rps[i].checked) {
					v = v + rps[i].value + ";";
				}
			}
			document.getElementById('reptyTime').value = v;
		}

	});
});
function isInt(str) {
	var reg = /^(-|\+)?\d+$/;
	return reg.test(str);
}

function init() {
	var reptyType = $('input[name="reptyType"]:checked').val();
	var reptyTime = $('#reptyTime').val();

	reptyTime = ";" + reptyTime + ";";

	if (reptyType == 1) {
		var rps = document.getElementsByName('reptyTime1');
		for ( var i = 0; i < rps.length; i++) {
			var tval = rps[i].value;
			if (reptyTime.indexOf(";" + tval + ";") > -1) {
				rps[i].checked = true;

			} else {
				rps[i].checked = false;
			}
		}

	} else {
		var rps = document.getElementsByName('reptyTime2');
		for ( var i = 0; i < rps.length; i++) {
			var tval = rps[i].value;
			if (reptyTime.indexOf(";" + tval + ";") > -1) {
				rps[i].checked = true;

			} else {
				rps[i].checked = false;
			}
		}
	}

}
