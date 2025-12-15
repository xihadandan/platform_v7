var port = window.location.port;
var host = window.location.host.replace(":"+port,"");
document.write('<OBJECT ID="MSOffice" WIDTH=0 HEIGHT=0 STYLE="display:none" CLASSID="clsid:12FCCF9A-24C5-4556-AD8B-ACFAE87E7C4A" codebase="${ctx}/resources/pt/js/dytable/WebFun.ocx#version=1.0.1.3">');
document.write('<PARAM NAME="protocol" VALUE="http">');
document.write('<PARAM NAME="username" VALUE="Admin">');
document.write('<PARAM NAME="domain" VALUE="'+host+'">');

document.write('<PARAM NAME="port" VALUE="'+port+'">');
document.write('<PARAM NAME="cgi" VALUE="'+ctx+'/fileUpload">');
document.write('<PARAM NAME="downloadPath" VALUE="c:\\temp\\BodyTemp">');
document.write('<PARAM NAME="DelTempPath" VALUE=true>');
document.write('</OBJECT>');