
function OnAboutAssist()
{
	var url = GetUrlPath();
	if (url.length!=0){
		url = url.concat("/about.html");
	}
	else{
		url = url.concat("./about.html");
	}
	wps.ShowDialog(url);
}