$(function() {

	function showErrorMsg(msg) {
		if ($('.error-div').length > 0) {
			$('.error-div').html(msg);
		} else {
			var html = '<div class="error-div">' + msg + '</div>';
			$('.box').append(html);
		}
	}
	var R_FAIL = -1; // 函数执行失败
	var R_OK = 0x00000000; // 函数执行成功
	var R_NOKEY = 0x00000001; // 没有检测到电子钥匙盘
	var R_NOUNIT = 0x00000002; // 没有进行正确初始化的电子钥匙盘
	var R_NODRIVER = 0x00000003; // 没有找到电子钥匙盘的驱动文件
	var R_KEYLONG = 0x00000004; // 电脑上插入的电子钥匙盘太多
	var R_NOUSERKEY = 0x00000005; // 没有插入用户的电子钥匙盘
	var R_NOSOKEY = 0x00000006; // 没有插入管理员电子钥匙盘
	var R_ERRORPIN = 0x00000007; // 错误的PIN码
	var R_NOTLAW = 0x00000008; // 非法的钥匙盘
	var R_NOSIGNIMG = 0x00000009; // 没有签章图片可以制作
	var R_SAVEERROR = 0x0000000A; // 写入签章图片数据失败
	var R_NOSIGN = 0x0000000B; // 钥匙盘里不存在签章
	var R_LOADERROR = 0x0000000C; // 读取签章图片数据失败
	var R_DELERROR = 0x0000000D; // 删除签章图片数据失败
	var R_NOSAMESIGN = 0x0000000E; // 钥匙盘里不存在该签章
	var R_NOSPACE = 0x0000000F; // 钥匙盘剩余空间不足
	var R_MAXUSER = 0x00000010; // 已达到最大用户量
	var R_KEYLOCK = 0x00000016; // 已达到最大用户量
	function checkAndPrepare() {
		var SMObj, hKey;
		try {
			// 这里检测是否安装客户端版本
			SMObj = document.getElementById("SMObj");
			if (SMObj == null) {
				var NV = {};
				var UA = navigator.userAgent.toLowerCase();
				NV.name = !-[ 1, ] ? 'ie' : (UA.indexOf("firefox") > 0) ? 'firefox' : (UA.indexOf("chrome") > 0) ? 'chrome' : window.opera ? 'opera'
						: window.openDatabase ? 'safari' : 'unkonw';
				if (NV.name == "firefox" || NV.name == "chrome") {
					$(document.body).append('<object id="SMObj"   width="1" height="1" type="application/signature_manager"></object>');
				} else {
					var str = '<div id="DivID">';
					str += '<OBJECT id="SMObj"  width="1"  height="1" classid="clsid:014A514F-A762-4869-BFFE-7999CDFEC055"  VIEWASTEXT>';
					str += '</object>';
					str += '</div>';
					$(document.body).append(str);
				}
				SMObj = document.getElementById("SMObj");
			}
			var keyCount = SMObj.WebGetKeyCount();
			if (keyCount <= 0) {
				return showErrorMsg("请插入电子钥匙盘！");
			} else if (keyCount > 1) {
				return showErrorMsg("找到多个电子钥匙盘！");
			}
			var hKey = SMObj.WebOpenKey(0);
//			if(R_OK != SMObj.WebVerifyPin(hKey, "123456") ){
//				return showErrorMsg("密码错误");
//			}
			document.getElementById("textCert").value = SMObj.WebGetKeySerialNumber(hKey);
			SMObj.WebCloseKey(hKey);
			return true;
		} catch (e) {
			showErrorMsg("请确认您的电脑上是否已\"OFD签章工具\"且使用IE登录");
			if (SMObj && hKey) {
				SMObj.WebCloseKey(hKey);
			}
			return false;
		}
	}

	function loginByKey(formId) {
		if (formId) {
			var keyPin = $(".key input[name=keyPin]").val();
			if($.trim(keyPin).length <= 0){
				return showErrorMsg("请输入密码");
			}
			if (checkAndPrepare()) {
				var tenant = $("input[name=tenant]").val();
				var textCert = $("input[name=textCert]").val();
				var jsonData = {};
				jsonData.version = "";
				jsonData.serviceName = "certificateLoginService";
				jsonData.methodName = "getLoginNameByKey";
				jsonData.args = JSON.stringify([ tenant, textCert ]);

				$.ajax({
					type : "POST",
					url : contextPath + "/json/data/services",
					data : JSON.stringify(jsonData),
					contentType : "application/json",
					dataType : "json",
					success : function(result, statusText, jqXHR) {
						var data = result.data;
						if (data['error']) {
							showErrorMsg(data['error']);
						} else {
							$("input[name=textCert]").val("");
							var loginNameHashAlgorithmCode = SystemParams.getValue('security.login_name.hash_algorithm.code', '1');
							var passwordhHshAlgorithmCode = SystemParams.getValue('security.password.hash_algorithm.code', '1');
							var encPassword = keyPin, encUsername = data["loginName"];
//							 登录名MD5加密
//							if (loginNameHashAlgorithmCode == "2") {
//								encUsername = md5().fromUTF8(username + "{" + username + "}");
//							}
//							 密码MD5加密
//							if (passwordhHshAlgorithmCode == "2") {
//								encPassword = md5().fromUTF8(password + "{" + username + "}");
//							}							
							$("input[name='password']", "#loginForm").val(encPassword);
							$("input[name='username']", "#loginForm").val(encUsername);
							$("input[name='j_lnalg_code']", "#loginForm").val(loginNameHashAlgorithmCode);
							$("input[name='j_pwdalg_code']", "#loginForm").val(passwordhHshAlgorithmCode);
							document.getElementById(formId).submit();
						}
					},
					error : function(jqXHR, statusText, error) {
						showErrorMsg("error");
					}
				});
			}
		}
	}

	$('.key button').click(function() {
		loginByKey('loginForm');
	});
})