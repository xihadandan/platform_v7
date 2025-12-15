var gsCurUser= getCookie("cookie.current.username");

/** 
 * @author WELL
 * @description 新建正文
 * @param {object} poOffice 对象 正文控件对象
 * @param {String} psAcceptRevision 字符串 是否接受修订,"0"不接受，"1"接受
 * @param {String} psFileURL 字符串 模板地址路径
 * @version 
 */
function NewOfficeDocument(poOffice,psAcceptRevision,psFileURL){
	/*var htmlStr = '<ul class="attach-list2" ><li><img src="'+ctx+'/resources/form/word.png" alt="" /><span>正文.doc</span></li></ul>';
	htmlStr += '<input type="hidden" id="s_BodyName" name="s_BodyName" value="" />';*/
	var caller = poOffice.caller;//caller是由调用方传入的(调用方，例如:WellFileUpload4Icon.prototype.defineUploadBodyEvent函数)
	 
	var lsRevisionCanAccept = psAcceptRevision;
	if(psFileURL==undefined || psFileURL == ""){
		if(typeof(poOffice)!="object") {
			return false;
		}
//		var fileName = $("#s_BodyName").val();
		var lsFileURL=ctx+"/resources/form/aaa.doc";
//		var lsFileURL="http://192.168.0.59/Template.doc";
		//console.log("正文ID:" + caller.fileID);
		
		poOffice.SetIni(lsFileURL, caller.fileName  ,"");//第一个参数为模板地址，第二个参数为生成的正文的文件名字
		window.LastModify = poOffice.getLastModify();
		poOffice.OpenFile(gsCurUser,"0","0",false,"1");
		if(lsRevisionCanAccept=="1"){
			BodySignRevisionCanAccept(poOffice);
		}
		var fileName = poOffice.GetLocalFile();
		
		return true;
	}else{
		lsFileURL = psFileURL;
		poOffice.SetIni(lsFileURL,"",ID_BodyLastModified.innerText);
		window.LastModify = poOffice.getLastModify();
		poOffice.OpenFile(gsCurUser,"0","0",false,"1");
		if(lsRevisionCanAccept=="1"){
				BodySignRevisionCanAccept(poOffice);
		}
		if(s_BodySaved)		//clear bodysaved flag
			s_BodySaved.value="";
		return true;
	}
}

/** 
 * @author WELL
 * @description 编辑正文
 * @param {object} poOffice 对象 正文控件对象
 * @param {String} psRevision 字符串 是否显示痕迹,"0"不显示，"1"显示
 * @param {String} psAcceptRevision 字符串 是否接受修订,"0"不接受，"1"接受
 * @param {String} psRevisionCanAccept 字符串 标记可以“接受/拒绝修订”
 * @param {String} psReadOnly 字符串 是否打开读,"0"打开读，"1"打开编辑
 * @param {String} psRevisionEdit 字符串 是否保留痕迹,"0"不保留，"1"保留
 * @version 
 */
function EditWordBody(poOffice,psRevision,psAcceptRevision,psRevisionCanAccept,psReadOnly,psRevisionEdit){
	var lsFileURL;
	var lsFileName = "";
	 
	var lsRevision=psRevision;
	var lsReadOnly=psReadOnly;
	var lsAcceptRevisions = psAcceptRevision;
	var lsRevisionEdit = psRevisionEdit;
	var lsRevisionCanAccept = psRevisionCanAccept;
	var s_BodyName = "Template.doc";
	
	 
	
	
	if(poOffice.GetLocalFile()==""){
		if(s_BodyName.value==""){
			NewOfficeDocument(poOffice,"1");
		}else{
			lsFileName="aaa.doc";
			var caller = poOffice.caller;//caller是由调用方传入的(调用方，例如:WellFileUpload4Icon.prototype.defineUploadBodyEvent函数)
			lsFileURL= ctx + fileServiceURL.downloadBody + caller.oldFileID;
			//var lsFileURL=ctx+"/resources/form/aaa.doc";
			
			if (typeof ID_CopyURLPath=="object" && ID_CopyURLPath.innerText!=""){
				lsFileURL= ID_CopyURLPath.innerText+lsFileName;
			}
			
			////console.log("地址:" + "http"+lsFileURL);
			//lsFileURL = "http://localhost:8080" + lsFileURL;
			//console.log("地址:"  +lsFileURL);
			if(!poOffice.SetIni(lsFileURL,caller.fileName, "")){
				alert(sGetLang("P_OFFICE_PROMPTFILEEXIST"));
				return false;
			}
			
			 
			 
			if(!poOffice.WordIsOpen()){
				poOffice.OpenFile(gsCurUser,lsRevisionEdit,lsRevision,lsReadOnly,"1");
				if(lsAcceptRevisions != "0"){
					BodySignAccept(poOffice);
				}
				if(lsRevisionCanAccept=="1"){
					BodySignRevisionCanAccept(poOffice);
				}
			}else{
				poOffice.SetWordFocus();
			}
			window.LastModify = poOffice.getLastModify();
		}
	}else{
			
			if(!poOffice.WordIsOpen()){			
			poOffice.OpenFile(gsCurUser,lsRevisionEdit,lsRevision,lsReadOnly,"1");
			if(lsAcceptRevisions != "0"){
					BodySignAccept(poOffice);
			}
			if(lsRevisionCanAccept=="1"){
					BodySignRevisionCanAccept(poOffice);
			}	
		}else{
				poOffice.SetWordFocus();
		}
	}
	top.IsOpenOffice; 
}

/** 
 * @author WELL
 * @description 标记接受所有修订
 * @param {object} poOffice 对象 正文控件对象
 * @version 
 */
function BodySignAccept(poOffice){
	BodyRunMacro(poOffice,"OASignAccept");
}


/** 
 * @author WELL
 * @description 标记可以“接受/拒绝修订”
 * @param {object} poOffice 对象 正文控件对象
 * @version 
 */
function BodySignRevisionCanAccept(poOffice){
	BodyRunMacro(poOffice,"OASignRevisionCanAccept");
}

/** 
 * @author WELL
 * @description 接受对文档的所有修订
 * @param {object} poOffice 对象 正文控件对象
 * @version 
 */
function BodyAcceptRevision(poOffice){
	BodyRunMacro(poOffice,"OAAcceptAllRevisions");
}

/** 
 * @author WELL
 * @description 执行正文控件的VBA函数
 * @param {object} poOffice 对象 正文控件对象
 * @param {String} psMacroName 字符串 VBA函数名 
 * @param {String} psArg0 字符串 VBA函数参数0
 * @param {String} psArg1 字符串 VBA函数参数1 
 * @param {String} psArg2 字符串 VBA函数参数2
 * @param {String} psArg3 字符串 VBA函数参数3 
 * @param {String} psArg4 字符串 VBA函数参数4
 * @param {String} psArg5 字符串 VBA函数参数5
 * @param {String} psArg6 字符串 VBA函数参数6
 * @param {String} psArg7 字符串 VBA函数参数7
 * @param {String} psArg8 字符串 VBA函数参数8
 * @param {String} psArg9 字符串 VBA函数参数9
 * @version 
 */
function BodyRunMacro(poOffice,psMacroName,psArg0,psArg1,psArg2,psArg3,psArg4,psArg5,psArg6,psArg7,psArg8,psArg9){
	var lsArg0 = (psArg0==null)?"":psArg0;
	var lsArg1 = (psArg1==null)?"":psArg1;
	var lsArg2 = (psArg2==null)?"":psArg2;
	var lsArg3 = (psArg3==null)?"":psArg3;
	var lsArg4 = (psArg4==null)?"":psArg4;
	var lsArg5 = (psArg5==null)?"":psArg5;
	var lsArg6 = (psArg6==null)?"":psArg6;
	var lsArg7 = (psArg7==null)?"":psArg7;
	var lsArg8 = (psArg8==null)?"":psArg8;
	var lsArg9 = (psArg9==null)?"":psArg9;
	return poOffice.RunVBA(psMacroName,lsArg0,lsArg1,lsArg2,lsArg3,lsArg4,lsArg5,lsArg6,lsArg7,lsArg8,lsArg9);
}

/** 
 * @author WELL
 * @description 本地保存正文控件中的文档
 * @param {object} poOffice 对象 正文控件对象
 * @param {Boolean} pbForce 布尔型 是否强制保存，true-强制保存，false-不强制保存
 * @param {String} psReadOnly 字符串 是否打开读,"0"打开读，"1"打开编辑
 * @version 
 */
function QuerySaveBody(poOffice,pbForce,psReadOnly,psRevisionAccept){
	//if (bValidServerSession(true)==false) return false;
	if(typeof(poOffice)!="object" || psReadOnly=="1") return true;
	var lbForce = (pbForce == null)?false:pbForce;
	var lsAcceptRevisions = psRevisionAccept;
	var liConfirm;
	if(poOffice.WordIsOpen()){
		if(!poOffice.IsSaved()){
			if(s_BodySaved.value=="1")
				liConfirm = 4;
			else
				liConfirm = 4;
				//liConfirm = bConfirm(sGetLang("P_OFFICE_PROMPTQUERYSAVE"),MB_YES+MB_NO+MB_CANCEL+MB_ICONQUESTION,sGetLang("P_OFFICE_TITLE"));
			if(liConfirm == 4){
				if(lsAcceptRevisions != "0") BodyAcceptRevision(poOffice);
				poOffice.SaveWord();
				poOffice.CloseWord(2);
				s_BodySaved.value="1";
				return true;
			}
			else if(liConfirm == 8){
				poOffice.CloseWord(3);
				return false;	
			}
			else
				return -1;
		}
		else
			poOffice.CloseWord(2);
	}
	if(typeof window.LastModify!="undefined" && poOffice.GetLocalFile()!="" && (window.LastModify=="" || poOffice.getLastModify()=="" || window.LastModify < poOffice.getLastModify())){
		if(s_BodySaved.value=="1")
			liConfirm = 4;
		else{
			if(!lbForce){
				liConfirm = 4;
			}else
				liConfirm = 4;
		}
		if(liConfirm == 4){
			s_BodySaved.value="1";
			return true;
		}
		else if(liConfirm == 8)
			return false;
		else
			return -1;
	}
	else{
		return false;
	}
}


/** 
 * @author WELL
 * @description 保存正文控件中的文档，并上传
 * @param {object} poOffice 对象 正文控件对象
 * @param {Boolean} pbSaveBody 布尔型 是否保存正文，true-保存，false-不保存
 * @param {String} psReadOnly 字符串 是否打开读,"0"打开读，"1"打开编辑
 * @return {Boolean} 布尔型 保存成功返回true，否则返回false
 * @version 
 */
function SaveBody(poOffice,pbSaveBody,psReadOnly){
	if(typeof(poOffice)!="object" || psReadOnly=="1") {
		return true;
	}
	var lbQuery = (pbSaveBody==null)?QuerySaveBody(poOffice,null,psReadOnly,"1"):pbSaveBody;
	if(lbQuery == -1){
		return false;
	}else if(lbQuery == true){ 
		var object  = poOffice.Upload("");
		if(object !=""){
			var lsFilePath = poOffice.GetFileName("");
			//var lsFilePaths = lsFilePath.split(".")[0];
		}else{
			
		} 
	}
	return true;
}
