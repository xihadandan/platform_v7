 //=======================these scripts must be the begin=======================
 if (typeof(window.wps) == "undefined") {
     window.wps = window;
 }
 document.write("<script language='javascript' src='js/function/wps.js'></script>")
 document.write("<script language='javascript' src='js/wpsenum/et_enum.js'></script>");
 document.write("<script language='javascript' src='js/wpsenum/wps_enum.js'></script>");
 document.write("<script language='javascript' src='plugin/vendor/jquery.min.js'></script>");
 document.write("<script language='javascript' src='plugin/js/base64.js'></script>");

 //=======================基础JS文件=======================


 document.write("<script language='javascript' src='js/common.js'></script>");
 document.write("<script language='javascript' src='js/monitor.js'></script>");
 document.write("<script language='javascript' src='js/about.js'></script>");

 //=======================功能JS文件=======================


 document.write("<script language='javascript' src='js/function/importDoc.js'></script>");
 document.write("<script language='javascript' src='js/function/openDoc.js'></script>");
 document.write("<script language='javascript' src='js/function/printDoc.js'></script>");
 document.write("<script language='javascript' src='js/function/uploadoa.js'></script>");
 document.write("<script language='javascript' src='js/function/saveDoc.js'></script>");

 
 //=======================模块JS文件=======================
 
 
 document.write("<script language='javascript' src='js/module/oastart.js'></script>");

 //=======================this script must be the last=======================


 document.write("<script language='javascript' src='js/ribbon.js'></script>");