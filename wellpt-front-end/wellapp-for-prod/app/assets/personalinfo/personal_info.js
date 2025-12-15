define(["jquery", "server", "bootstrap", "commons"], function ($, server, bootstrap, commons) {
  var JDS = server.JDS;
  var StringUtils = commons.StringUtils;
  var showPersonalInfo = function (callback) {
    $.load(ctx + "/personalinfo/show/info", callback);
  }
  var modifyPersonalPwd = function (callback) {

  }
  var modifyPersonalInfo = function (callback) {

  }

  function bindEvent($currentTarget, $target, eventType) {
    // 隐藏弹出框
    $("body").on("mousedown", function (event) {
      if ($(event.target).parents(".nav-menu-item[menuid=personInfo]").length <= 0) {
        $target.popover('hide');
      }
    });
    // 阻止弹出框事件传播（鼠标移进或点击）
    $currentTarget.on(eventType, ".popover", function (event) {
      event.preventDefault();
      event.stopPropagation();
    });
    // 显示弹出框
    $currentTarget.on(eventType, ">a[href]", function (event) {
      console.log($currentTarget);
      event.preventDefault();
      event.stopPropagation();
      $target.popover('show');
    });
    // 修改密码
    $currentTarget.on("click", ".popover .modify-pwd", function (event) {
      return $.get(ctx + "/personalinfo/modify/password", function (modifyPwdHtml) {
        $.WCommonConfirm({
          title: "修改密码",
          message: modifyPwdHtml,
          callback: function (flag) {
            if (flag) {
              var userUuid = $("#personalInfoModifyPwd input[name='userUuid']").val();
              var oldPwd = $("#personalInfoModifyPwd input[name='oldPwd']").val();
              var newPwd = $("#personalInfoModifyPwd input[name='newPwd']").val();
              var confirmPwd = $("#personalInfoModifyPwd input[name='confirmPwd']").val();
              if (StringUtils.isBlank(oldPwd)) {
                $.WCommonAlert("请输入旧密码");
                return false;
              }
              if (StringUtils.isBlank(newPwd)) {
                $.WCommonAlert("请输入新密码");
                return false;
              }

              if (newPwd != confirmPwd) {
                $.WCommonAlert("输入的两次新密码不一致");
                return false;
              }
              JDS.call({
                service: "personalInfoService.modifyCurrentUserPassword",
                async: false,
                data: [oldPwd, newPwd],
                success: function (result) {
                  if (result.data == "success") {
                    $.WCommonAlert("修改成功");
                  } else {
                    $.WCommonAlert(result.data);
                  }
                },
                error: function () {
                  $.WCommonAlert("未知错误");
                }
              })
            }
          },
          shown: function () {
            $("body").trigger("mousedown");
          }
        });
      });
    })

    // 设置
    $currentTarget.on("click", ".popover .setting", function (event) {
      return $.get(ctx + "/personalinfo/modify/info", function (settingHtml) {
        $.WCommonConfirm({
          title: "设置",
          message: settingHtml,
          size: "middle",
          callback: function (flag) {
            if (flag) {
              var userUuid = $("#personalInfoSetting input[name='userUuid']").val();
              var sex = $("#personalInfoSetting input[name='sex']:checked").val();
              var idNumber = $("#personalInfoSetting input[name='idNumber']").val();
              var photoUuid = $("#personalInfoSetting input[name='photoUuid']").val();
              var mobilePhone = $("#personalInfoSetting input[name='mobilePhone']").val();
              // var $receiveSmsMsg = $("#personalInfoSetting input[name='receiveSmsMessage']:checked");
              // var receiveSmsMessage = $receiveSmsMsg.size() > 0 ? true : false;
              var homePhone = $("#personalInfoSetting input[name='homePhone']").val();
              var officePhone = $("#personalInfoSetting input[name='officePhone']").val();
              var fax = $("#personalInfoSetting input[name='fax']").val();
              var mainEmail = $("#personalInfoSetting input[name='mainEmail']").val();
              // var otherEmail = $("#personalInfoSetting input[name='otherEmail']").val();

              if (StringUtils.isBlank(mobilePhone)) {
                $.WCommonAlert('请输入手机号！');
                return false;
              }
              if (StringUtils.isBlank(officePhone)) {
                $.WCommonAlert('请输入办公电话！');
                return false;
              }
              var userBean = $("#personalInfoSetting").data("user-json") || {};
              $.extend(userBean, {
                "uuid": userUuid,
                "sex": sex,
                "fax": fax,
                "password": "",
                "idNumber": idNumber,
                "photoUuid": photoUuid,
                "homePhone": homePhone,
                "mainEmail": mainEmail,
                "mobilePhone": mobilePhone,
                "officePhone": officePhone
              });
              $.ajax({
                type: "POST",
                url: "/api/org/user/modifyUser",
                async: false,
                dataType: 'json',
                data: userBean,
                success: function (result) {
                  $.WCommonAlert("修改成功");
                },
                error: function () {
                  $.WCommonAlert("未知错误");
                }
              });

            }
          },
          shown: function () {
            $("body").trigger("mousedown");
            $("#personalInfoPhotoSelectBtn").on("click", function () {
              $.WCommonPictureLib.show({
                selectTypes: [1],// 可选择的类型
                initPrevImg: $("#personalInfoPhotoImage").attr("src"),
                confirm: function (data) {
                  var pictureFilePath = data.filePaths;
                  if (StringUtils.isBlank(pictureFilePath)) {
                    return;
                  }
                  $("input[name=photoUuid]").val(data.fileIDs);
                  $("#personalInfoPhotoImage").show();
                  $("#personalInfoPhotoImage").attr("src", ctx + pictureFilePath);
                }
              });
            });
          }
        });
      });
    });

    // 管理
    $currentTarget.on("click", ".popover .admin", function (event) {
      window.open(ctx + "/passport/admin/main", "_self");
    });

    // 注销
    $currentTarget.on("click", ".popover .logout", function (event) {
      window.open(ctx + "/security_logout", "_self");
    });
  }

  var personalInfo = function (options) {
    var eventType = options.event.handleObj.type;
    var $currentTarget = $(options.event.currentTarget);
    var $target = $currentTarget.find(">a[href]");
    if ($target.is("[inited=true]") === false) {
      $target.attr("inited", "true");
      return $.get(ctx + "/personalinfo/show/info", function (text) {
        $target.popover({
          html: true,
          content: text,
          placement: 'auto',
          trigger: 'manual'
        });
        bindEvent($currentTarget, $target, eventType);
        $target.popover('show');
      });
    }
    $target.popover('show');
  }

  return personalInfo;
});
