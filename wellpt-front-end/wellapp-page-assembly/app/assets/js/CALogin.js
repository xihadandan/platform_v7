define([ "jquery", "constant", "commons" ], function($, constant, commons) {
	// 服务器交互相关对象
	var CALogin = {};
	//检查是否具备CA登录的环境
	CALogin.checkCAEnv = function(){
		console.log("CALogin.checkCAEnv=======================");
		console.log( fjcaWs );
		//检查必须的控件必须存在
		if($("#textCert").length == 0 ){
			console.error("textCert input 不存在");
			return;
		}
		if($("#idNumber").length == 0){
			console.error("idNumber input 不存在");
			return;
		}
		if($("#certType").length == 0){
			console.error("certType input 不存在");
			return;
		}
		if($("#textOriginData").length == 0){
			console.error("textOriginData input 不存在");
			return;
		}
		if($("#textSignData").length == 0){
			console.error("textSignData input 不存在");
			return;
		}
		if($("#fjcaWs").length == 0){
			console.error("fjcaWs object 不存在");
			return;
		}
		if($("#fjcaControl").length == 0){
			console.error("fjcaControl object 不存在");
			return;
		}
		if( !$.browser.msie ){
			return "请确认您的电脑上是否已安装福建CA数字证书客户端软件3.1或以上版本且使用IE登录"
		}
		try {
			if( fjcaWs.OpenUSBKey() ){
				if (fjcaWs.ReadCertFromKey()) {
					var cert = fjcaWs.GetCertData();
					document.getElementById("textCert").value = cert;
					// 读取身份证
					var idNumber = fjcaControl.GetCertExtensionInfoById(cert, "1.2.156.10260.4.1.1");
					var certType = "personal";// 个人证书
					//个人证书拿不到idNumber，那就是企业证书了
					if (idNumber == null || $.trim(idNumber) == "") {
						certType = "enterprise";// 企业证书
						idNumber = fjcaControl.GetCertExtensionInfoById(cert, "1.2.156.10260.4.1.3");
						if (idNumber == null || $.trim(idNumber) == "") {
							idNumber = fjcaControl.GetCertExtensionInfoById(cert, "1.2.156.10260.4.1.4");
						}else{
							//idNumber去掉减号
							if (idNumber.indexOf("-") != -1) {
								idNumber = idNumber.replace(/-/, "");
							}
						}
					}
					document.getElementById("idNumber").value = idNumber;
					document.getElementById("certType").value = certType;

					// 对随机数签名
					var strData = document.getElementById("textOriginData").value;
					if (!fjcaWs.SignDataWithKey(strData)) {
						return "对数据签名失败！";
					}
					document.getElementById("textSignData").value = fjcaWs.GetSignData();
					return "ok";
				}else{
					console.log("CALogin.checkCAEnv=======================1");
					return "请确认您的电脑上是否已安装福建CA数字证书客户端软件3.1或以上版本且使用IE登录。"
				}
			}else{
				console.log("CALogin.checkCAEnv=======================2");
				return "请确认您的电脑上是否已安装福建CA数字证书客户端软件3.1或以上版本且使用IE登录。";
			}
		} catch (e) {
			console.log("CALogin.checkCAEnv=======================3");
			return "请确认您的电脑上是否已安装福建CA数字证书客户端软件3.1或以上版本且使用IE登录。";
		} finally {
			fjcaWs.CloseUSBKey();
		}
	}

	return CALogin;
});