(function test(cc){
	var start = (new Date()).getTime(), end;
	for(var i =0;i<cc;i++){
		var $div = $("<div class='downloadAll'></div>");
	}
	end = (new Date()).getTime();
	console.log("1------------end:", end - start);
	start = end;
	for(var i =0;i<cc;i++){
		var $div = $div.clone();
	}
	end = (new Date()).getTime();
	console.log("2------------end:", end - start);
})(10000);