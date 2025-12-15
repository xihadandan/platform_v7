define(['mui', 'commons', 'server', 'appContext', 'appModal', 'WorkView', 'WorkViewProxy', 'formBuilder'], function (
  $,
  commons,
  server,
  appContext,
  appModal,
  WorkView,
  workView,
  formBuilder
) {
  var StringUtils = commons.StringUtils;
  var showUserInfo = function (option) {
    var userIds = option.userId ? option.userId : '';
    var userArr = StringUtils.isBlank(userIds) ? [] : userIds.split(';');
    userArr = userArr.filter(function (id, idx) {
      return id.indexOf('U') === 0;
    });
    if (userArr.length <= 0) {
      return $.toast('参数有误，可展示的用户记录');
    }
    $.ajax({
      url: ctx + '/static/mobile/mui/showPersonalData.html',
      async: false,
      success: function (data) {
        var wrapper = document.createElement('div');
        wrapper.id = 'mobile_userInfo';
        wrapper.classList.add('mui-transitioning');
        var pageContainer = appContext.getPageContainer();
        var renderPlaceholder = pageContainer.getRenderPlaceholder();
        renderPlaceholder[0].appendChild(wrapper);
        formBuilder.buildPanel({
          title: '个人信息',
          content: data.toString(),
          container: '#mobile_userInfo'
        });
        $.ui.loadContent('#mobile_userInfo');
      }
    });

    function getDetail(userId) {
      var JDS = server.JDS;
      JDS.call({
        service: 'personalInfoService.getUserInfo',
        async: false,
        data: [userId],
        success: function (result) {
          var data = result.data;
          // console.log(data);
          if (data.photoUuid) {
            $('.personal-img')[0].setAttribute('src', ctx + '/org/user/view/photo/' + data.photoUuid);
          } else if (data.sex == 1) {
            $('.personal-img')[0].setAttribute('src', ctx + '/static/js/pt/images/org/man.png');
          } else {
            $('.personal-img')[0].setAttribute('src', ctx + '/static/js/pt/images/org/woman.png');
          }

          $('.user-name')[0].innerHTML = data.userName;
          if (data.sex == 1) {
            $('.user-sex')[0].innerHTML = '男';
          } else {
            $('.user-sex')[0].innerHTML = '女';
          }
          if (data.receiveSmsMessage == true) {
            $('.user-recevie-flag')[0].innerHTML = '是';
          } else {
            $('.user-recevie-flag')[0].innerHTML = '否';
          }
          $('.user-id')[0].innerHTML = data.idNumber == null ? '' : data.idNumber;
          $('.user-phone')[0].innerHTML = data.mobilePhone || '';
          $('.func-icons').off('tap');
          if (data.mobilePhone) {
            $('.icon-phone')[0].classList.remove('mui-hidden');
            $('.icon-commenting')[0].classList.remove('mui-hidden');
            $('.icon-phone')[0].setAttribute('href', 'tel:' + data.mobilePhone);
            $('.icon-commenting')[0].setAttribute('href', 'tel:' + data.mobilePhone);
            $('.func-icons')
              .on('tap', '.icon-phone', function (event) {
                event.preventDefault();
                event.stopPropagation();
                var phoneNum = this.getAttribute('href');
                $.trigger(document.body, 'phone.call', {
                  phoneNum: phoneNum.replace('tel:', '')
                });
              })
              .on('tap', '.icon-commenting', function (event) {
                event.preventDefault();
                event.stopPropagation();
                var phoneNum = this.getAttribute('href');
                $.trigger(document.body, 'sms.send', {
                  to: phoneNum.replace('tel:', '')
                });
              });
          }
          $('.user-phone-other')[0].innerHTML = data.otherMobilePhone || '';
          $('.user-job-phone')[0].innerHTML = data.officePhone || '';
          $('.user-mail')[0].innerHTMLvalue = data.mainEmail || '';
          if (data.mainEmail) {
            $('.icon-envelope')[0].setAttribute('href', 'mailto:' + data.mainEmail);
            $('.icon-envelope')[0].classList.remove('mui-hidden');
            $('.func-icons').on('tap', '.icon-envelope', function (event) {
              event.preventDefault();
              event.stopPropagation();
              var mailto = this.getAttribute('href');
              $.trigger(document.body, 'email.send', {
                to: mailto.replace('mailto:', '')
              });
            });
          }
          $('.user-mail-other')[0].innerHTML = data.otherEmail || '';
          $('.user-uuid')[0].value = data.uuid;
        }
      });
    }
    if (userArr.length > 1) {
      $('.personal-wrapper #prev')[0].classList.remove('mui-hidden');
      $('.personal-wrapper #next')[0].classList.remove('mui-hidden');
      var currentPos = -1;

      function btnAction(event) {
        var self = this;
        var action = self.id;
        if (action === 'prev' && currentPos > 0) {
          getDetail(userArr[--currentPos]);
        } else if (action === 'next' && currentPos < userArr.length - 1) {
          getDetail(userArr[++currentPos]);
        } else {
          $.toast('没有更多用户记录');
        }
      }
      $('.personal-wrapper #prev')[0].addEventListener('tap', btnAction);
      $('.personal-wrapper #next')[0].addEventListener('tap', btnAction);
      $.trigger($('.personal-wrapper #next')[0], 'tap');
    } else {
      getDetail(userArr[0]);
    }
  };
  return showUserInfo;
});
