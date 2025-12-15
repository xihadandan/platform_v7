define(['constant', 'commons', 'server', 'appContext', 'appModal', 'ztree', 'HtmlWidgetDevelopment', 'js-base64'], function (
  constant,
  commons,
  server,
  appContext,
  appModal,
  ztree,
  HtmlWidgetDevelopment,
  base64
) {
  var StringUtils = commons.StringUtils;
  var JDS = server.JDS;

  var AppOrgUserWidgetDevelopment = function () {
    HtmlWidgetDevelopment.apply(this, arguments);
  };
  // 接口方法
  commons.inherit(AppOrgUserWidgetDevelopment, HtmlWidgetDevelopment, {
    init: function () {
      var _this = this;
      var formBean = {
        uuid: null,
        id: null,
        loginName: null,
        loginNameZh: null,
        password: null,
        type: 0,
        code: null,
        remark: null,
        systemUnitId: null,
        isForbidden: 0,
        isLocked: 0,
        employeeNumber: null, // 员工编号
        userName: null, // 姓名
        englishName: null,
        sex: null,
        fax: null,
        idNumber: null,
        mobilePhone: null,
        officePhone: null,
        homePhone: null,
        mainEmail: null,
        mainJobId: null,
        mainJobNamePath: null,
        jobRank: null,
        jobRankId: null,
        jobLevel: null,
        otherJobIds: null,
        otherJobNamePaths: null,
        directLeaderIds: null,
        directLeaderNames: null,
        directLeaderNamePaths: null,
        roleUuids: null,
        photoUuid: null
      };
      var uuid = GetRequestParam().uuid;
      this.uuid = uuid;
      var _self = this;
      this.$element = this.widget.element;
      this.$element.on('click', function () {
        // 修改非只读（只读的情况是为了避免浏览器“记住密码”的行为）
        $('#password', _self.$element).prop('readonly', false);
      });

      var pwdReg = new RegExp('(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[^a-zA-Z0-9]).{8,20}');
      $('#org_user_form', this.$element).json2form(formBean);
      initFormLayout();
      jQuery.validator.addMethod('checkLoginNameFormat', function (value, element) {
        var reg = new RegExp('^[a-zA-Z0-9_-]{2,20}$');
        if (reg.test(value)) {
          return true;
        }
        return false;
      });

      var validator = $.common.validation.validate('#org_user_form', 'orgUserVo', function (options) {
        options.ignore = '';
        options.rules.loginName = {
          required: true,
          checkLoginNameFormat: true
        };
        options.messages.loginName = {
          required: '不能为空!',
          checkLoginNameFormat: '账号名只能是大小写字母，数字，_ 组成，长度为2-20个字符'
        };
      });

      $('#org_user_form', this.$element).find('#org-user-tabs-1').find("input[type='text'],input[type='password'],textarea").css({
        width: 'calc(100% - 110px)',
        display: 'inline-block'
      });

      this.getLoginSetting();
      this.getPwdRules(uuid);

      if (uuid) {
        appContext.getNavTabWidget().setCloseActiveNavNotRefreshParent(); // 编辑页关闭，不刷新列表页面
        showUserInfo(uuid);
        getWorkBenchList();
        $('.user-detail-edit').show();
      } else {
        $('.user-detail-edit').hide();
        $('#loginName').removeAttr('readonly');
        $('.userResetPwdBtn', this.$element).hide();
        setTimeout(function () {
          var $zTree = $.fn.zTree.getZTreeObj('user_role_tree');
          if ($zTree) {
            $zTree.checkAllNodes(false);
          }
          $('#user_roleUuids').empty();
        }, 500);
        $('#user_privilege_result_tree').empty();
      }

      $('#roleSource').wellSelect({
        data: [
          {
            id: '0',
            text: '全部'
          },
          {
            id: '1',
            text: '用户'
          },
          {
            id: '2',
            text: '组织'
          },
          {
            id: '3',
            text: '群组'
          },
          {
            id: '4',
            text: '父角色'
          }
        ],
        labelField: 'roleSource',
        valueField: 'roleSourceId',
        ctlWidth: '100',
        searchable: false,
        placeholder: '来源',
        showEmpty: false
      });

      $('#originName').wellSelect({
        data: [
          {
            id: '0',
            text: '全部'
          },
          {
            id: '1',
            text: '用户'
          },
          {
            id: '2',
            text: '组织'
          },
          {
            id: '3',
            text: '角色'
          }
        ],
        labelField: 'originName',
        valueField: 'originId',
        ctlWidth: '100',
        searchable: false,
        placeholder: '来源',
        showEmpty: false
      });

      $('#keyword')
        .off()
        .on('keyup', function (e) {
          if ($(this).val() != '') {
            $(this).siblings('.user-search-clear').show();
          } else {
            $(this).siblings('.user-search-clear').hide();
          }
          if (e.keyCode == 13) {
            $('#userSearchBtn').trigger('click');
          }
        });

      $('.user-search-clear')
        .off()
        .on('click', function () {
          $('#keyword').val('');
          $(this).hide();
          $('#userSearchBtn').trigger('click');
        });

      $('#userSearchBtn')
        .off()
        .on('click', function () {
          var keyword = $('#keyword').val();
          var originId = $('#originId').val();
          getWorkBenchList(originId, keyword);
        });

      $('#roleSearchBtn')
        .off()
        .on('click', function () {
          resetRoleListTable(_userSelectedRoles);
        });

      $('#roleKeyword')
        .off()
        .on('keyup', function (e) {
          if (e.keyCode === 13) {
            $('#roleSearchBtn').trigger('click');
          }
        });

      $('#user-role-integrate-info-search-btn').on('click', function () {
        var nodeName = $('#user-role-integrate-info-search-input').val().trim();
        if (nodeName) {
          searchZtreeNode(0, 'user-role-integrate-tree', nodeName);
          return false;
        }
      });

      $('#user-role-integrate-info-search-input').on('keyup', function (e) {
        if (e.keyCode === 13) {
          $('#user-role-integrate-info-search-btn').trigger('click');
        }
      });

      $('#setUserRolesBtn')
        .off()
        .on('click', function () {
          var _html = get_set_user_role_dialog_html();
          var $dialog = appModal.dialog({
            title: '配置用户角色',
            message: _html,
            height: '600px',
            size: 'large',
            shown: function () {
              $.ajax({
                url: ctx + '/api/security/role/queryRoleByCurrentUserUnitId',
                type: 'get',
                data: {},
                async: false,
                success: function (result) {
                  if (result.code != 0) {
                    appModal.error(result.msg);
                  }
                  _userSelectableRoles = result.data;
                  var $selectableRolesUl = $('#user-roles-dialog-selectable-roles');
                  var $selectedRolesUl = $('#user-roles-dialog-selected-roles');
                  _addedRolesCache = [];
                  _removedRolesCache = [];
                  var selectedRolesFromUser = _userSelectedRoles.filter(function (role) {
                    return role.source === '用户';
                  });

                  for (var i = 0; i < _userSelectableRoles.length; i++) {
                    var role = _userSelectableRoles[i];

                    var isSelected = selectedRolesFromUser.some(function (userRole) {
                      return userRole.roleUuid === role.uuid;
                    });
                    $selectableli = $('<li></li>').attr('title', role.name).attr('uuid', role.uuid);
                    $selectableli.append(
                      '<label><input type="checkbox" class="role-checkbox" id="' +
                        role.uuid +
                        '">' +
                        '<label for="' +
                        role.uuid +
                        '"></label>' +
                        '<span>' +
                        role.name +
                        '</span>' +
                        '</label>'
                    );
                    $selectableRolesUl.append($selectableli);

                    if (isSelected) {
                      $selectableli.find('.role-checkbox').prop('checked', true);
                      var $selectedLi = $('<li></li>').attr('title', role.name).attr('uuid', role.uuid);
                      $selectedLi.append('<span class="role-name">' + role.name + '</span> <span class="role-delete-btn">X</span>');
                      $selectedRolesUl.append($selectedLi);
                    }
                  }

                  resetSelectableRolesUl();

                  function resetSelectableRolesUl() {
                    var keyword = $('#user-roles-dialog-keyword').val().toLowerCase().trim();
                    if (!keyword) {
                      $('li', $selectableRolesUl).show();
                      setNoneDataPlaceholderVisible(_userSelectableRoles.length > 0);
                      return;
                    }

                    var selectableItemsCount = 0;
                    $.each($('li', $selectableRolesUl), function (i, li) {
                      var $li = $(li);
                      var roleName = $li.attr('title');
                      if (roleName.toLowerCase().trim().includes(keyword)) {
                        $li.show();
                        selectableItemsCount++;
                      } else {
                        $li.hide();
                      }
                    });

                    setNoneDataPlaceholderVisible(selectableItemsCount > 0);
                  }

                  function setNoneDataPlaceholderVisible(visible) {
                    if (visible) {
                      $('#user-roles-dialog-selectable-roles-none-data-placeholder').hide();
                    } else {
                      $('#user-roles-dialog-selectable-roles-none-data-placeholder').show();
                    }
                  }

                  $('#user-roles-dialog-keyword')
                    .off()
                    .on('keyup', function (e) {
                      if (e.keyCode === 13) {
                        $('#user-roles-dialog-search-btn').trigger('click');
                      }
                    });

                  $('#user-roles-dialog-search-btn')
                    .off('click')
                    .on('click', function () {
                      resetSelectableRolesUl();
                    });

                  $($selectableRolesUl)
                    .off('click')
                    .on('click', 'li .role-checkbox', function (e) {
                      var $this = $(this);
                      var checked = $this.attr('checked');
                      var uuid = $this.closest('li').attr('uuid');
                      if (checked) {
                        if ($selectedRolesUl.find('li[uuid="' + uuid + '"]').length) {
                          return;
                        }
                        var checkedRole = _userSelectableRoles.find(function (role) {
                          return role.uuid === uuid;
                        });
                        var $selectedLi = $('<li></li>').attr('title', checkedRole.name).attr('uuid', checkedRole.uuid);
                        $selectedLi.append(
                          '<span class="role-name">' + checkedRole.name + '</span> <span class="role-delete-btn">X</span>'
                        );
                        $selectedRolesUl.append($selectedLi);
                      } else {
                        $('li[uuid="' + uuid + '"]', $selectedRolesUl).remove();
                      }
                    });

                  $($selectedRolesUl)
                    .off('click')
                    .on('click', 'li .role-delete-btn', function (e) {
                      var $this = $(this);
                      var uuid = $this.closest('li').attr('uuid');
                      $('li[uuid="' + uuid + '"] input', $selectableRolesUl).removeAttr('checked');
                      $('li[uuid="' + uuid + '"]', $selectedRolesUl).remove();
                    });
                }
              });
            },
            buttons: {
              save: {
                label: '确定',
                className: 'well-btn w-btn-primary',
                callback: function () {
                  var uuids = [];
                  $.each($('#user-roles-dialog-selected-roles li'), function (i, li) {
                    uuids.push($(li).attr('uuid'));
                  });

                  handleRolesDialogConfirm(uuids);
                }
              },
              close: {
                label: '关闭',
                className: 'btn-default',
                callback: function () {}
              }
            }
          });
        });

      $('#org_user_btn_save').click(function () {
        if (!validator.form()) {
          checkUserPwd(uuid, _this);
          return false;
        } else {
          checkUserPwd(uuid, _this);
        }
        if ($('#loginNameZh').val() == '') {
          $('#loginNameZh').val($('#userName').val());
        }
        $('#org_user_form').form2json(formBean);
        formBean.password = base64.encode(urlencode(formBean.password));
        // 获取角色的值
        var roleUuids = [];
        _userSelectedRoles
          .filter(function (role) {
            return role.source === '用户';
          })
          .forEach(function (role) {
            roleUuids.push(role.roleUuid);
          });
        var oldRoleUuids = formBean.roleUuids || '';
        formBean.roleUuids = roleUuids.join(';');
        var updateRoleUuids = oldRoleUuids + ';' + formBean.roleUuids;
        formBean.systemUnitId = formBean.systemUnitId || SpringSecurityUtils.getCurrentUserUnitId();
        formBean.relationList = [];
        if (formBean.mainJobId && $.trim(formBean.mainJobId) != '') {
          formBean.relationList.push({
            jobGrade: '',
            jobId: formBean.mainJobId,
            jobLevel: $('#jobLevel').val(),
            jobRank: $('#jobRank').val(),
            jobRankId: $('#jobRankId').val()
          });
        }
        var otherJobIds = [];
        var otherJobNamePaths = [];
        $.each($("input[name='otherJobIds']", self.$element), function (index, item) {
          var val = $(item).val();
          if (val != '') {
            otherJobIds.push(val);
            otherJobNamePaths.push($(item).next().val());
            var jobLevel = $(this).parents('.job-item').find("input[name='jobLevel']").val();
            var jobRank = $(this).parents('.job-item').find("input[name='jobRank']").val();
            var jobRankId = $(this).parents('.job-item').find("input[name='jobRankId']").val();

            formBean.relationList.push({
              jobGrade: '',
              jobId: val,
              jobLevel: jobLevel,
              jobRank: jobRank,
              jobRankId: jobRankId
            });
          }
        });
        if (otherJobIds.includes(formBean.mainJobId)) {
          appModal.error('职位不能重复！');
          return false;
        }
        formBean.otherJobIds = otherJobIds.join(';');
        formBean.otherJobNamePaths = otherJobNamePaths.join(';');

        if (commons.StringUtils.isBlank(formBean.mainJobId) && otherJobIds.length == 0) {
          appModal.error('工作信息中的主职和其他职位至少需选择一个');
          return false;
        }

        // 因为组织ID是select2控件，所以无法通过validate来配置检查,只能自己检查让服务端检查了
        var url = '/api/org/multi/' + (formBean.uuid ? 'modifyUser' : 'addUser');
        JDS.restfulPost({
          url: url,
          dataType: 'json',
          data: formBean,
          mask: true,
          success: function (result) {
            if (result.code === 0) {
              appModal.success('保存成功！', function () {
                // 更新角色资源信息
                var rids = updateRoleUuids.split(';');
                for (var i = 0, len = rids.length; i < len; i++) {
                  if (rids[i]) {
                    (function (ruuid) {
                      $.ajax({
                        url: ctx + '/api/security/role/publishRoleUpdatedEvent',
                        type: 'POST',
                        data: {
                          uuid: ruuid
                        },
                        dataType: 'json',
                        success: function (result) {}
                      });
                    })(rids[i]);
                  }
                }
                if (!formBean.uuid && _this.rules.adminSetPwdType != 'ASPT02') {
                  var $pwdDialog = appModal.dialog({
                    message: '新增用户成功！用户的初始登录密码为 <span id="initialPwd"></span><br>关闭弹窗后，将无法查看密码！',
                    title: '新增用户',
                    shown: function () {
                      $('#initialPwd', $pwdDialog).text(result.data.password);
                    },
                    buttons: {
                      copy: {
                        label: '复制密码',
                        className: 'well-btn w-btn-primary',
                        callback: function () {
                          $('.bootbox-body', $pwdDialog).append("<textarea id='copyPwdContent' style='opacity: 0;'>");
                          var copyContent = $('#initialPwd', $pwdDialog).text();
                          var inputElement = document.getElementById('copyPwdContent');
                          inputElement.value = copyContent;
                          inputElement.select();
                          document.execCommand('Copy');
                          appModal.success('复制成功！');
                          $('#copyPwdContent').remove();
                          appContext.getNavTabWidget().closeTab();
                        }
                      },
                      cancel: {
                        label: '关闭',
                        className: 'btn-default'
                      }
                    }
                  });
                } else {
                  if (formBean.uuid) {
                    // 编辑页面保存 不刷新列表
                    appContext.getNavTabWidget().closeTabAndRefreshTrData(result.data);
                  } else {
                    // 新增页面保存 刷新列表
                    appContext.getNavTabWidget().closeTab();
                  }
                }
              });
            } else if (result.code === 1) {
              result.data.forEach(name => {
                appModal.error(name + '已被使用！');
              });
            } else {
              appModal.error(result.msg);
            }
          }
        });
      });
      $("input[type='password']").blur(function () {
        var val = $(this).val();
        if (val != '' && (!pwdReg.test(val) || val.length > 20)) {
          $(this).next('.error').show();
          return false;
        } else {
          $(this).next('.error').hide();
        }
      });

      $('#mainJobNamePath').click(function () {
        // 需要剔除其他职位
        var excludeIdPaths = $('#otherJobIds').val();
        // var excludeIds = computeExcludeIds(excludeIdPaths);
        $.unit2.open({
          valueField: 'mainJobId',
          labelField: 'mainJobNamePath',
          title: '选择职位',
          type: 'MyUnit',
          multiple: false,
          selectTypes: 'J',
          valueFormat: 'all',
          callback: function (values, labels, treeNodes) {
            _self.renderJobField();
            _self.getRankData(values[0], $('#mainJobId'));
          }
        });
      });

      $('#jobRank0').wellSelect({
        data: [],
        labelField: 'jobRank0',
        valueField: 'jobRankId0',
        searchable: false,
        showEmpty: false,
        placeholder: '请选择职级'
      });

      $('#jobLevel0').wellSelect({
        data: [],
        searchable: false,
        showEmpty: false,
        placeholder: '请选择职档'
      });

      $('#jobRank').wellSelect({
        data: [],
        labelField: 'jobRank',
        valueField: 'jobRankId',
        searchable: false,
        showEmpty: false,
        placeholder: '请选择职级'
      });

      $('#jobLevel').wellSelect({
        data: [],
        searchable: false,
        showEmpty: false,
        placeholder: '请选择职档'
      });

      // 其他职位
      _this.$element.off('click', 'input[name="otherJobNamePaths"]').on('click', 'input[name="otherJobNamePaths"]', function () {
        var labelField = $(this).attr('id');
        var valueField = $(this).prev().attr('id');

        // 需要扣除主职
        var excludeIdPaths = $('#mainJobId').val();
        // var excludeIds = computeExcludeIds(excludeIdPaths);
        $.unit2.open({
          valueField: valueField,
          labelField: labelField,
          title: '选择职位',
          type: 'MyUnit',
          multiple: false,
          selectTypes: 'J',
          valueFormat: 'all',
          callback: function (values, labels, treeNodes) {
            _self.renderJobField();
            _self.getRankData(values[0], $('#' + valueField));
          }
        });
      });

      // 直属上级领导
      $('#directLeaderNamePaths').click(function () {
        $.unit2.open({
          valueField: 'directLeaderIds',
          labelField: 'directLeaderNamePaths',
          title: '选择直属领导',
          type: 'MyUnit',
          multiple: true,
          selectTypes: 'U;J',
          valueFormat: 'all',
          callback: function (values, labels, treeNodes) {
            _self.renderJobField();
          }
        });
      });

      $('.userResetPwdBtn', this.$element)
        .off()
        .on('click', function () {
          var url = '/api/org/user/account/resetUserDefinedPwd';
          var uuid = $('#uuid', _this.$element).val();
          if (_this.rules.adminSetPwdType == 'ASPT02') {
            _this.getResetPwdDialog(url, [uuid]);
          } else {
            appModal.confirm('确定要重置用户的登录密码吗？<br>重置后，已锁定的账号将会自动解锁！', function (confirmed) {
              if (!confirmed) {
                return;
              }
              $.ajax({
                url: ctx + '/api/org/user/account/resetUserPwd',
                type: 'POST',
                data: {
                  userUuids: [uuid]
                },
                dataType: 'json',
                success: function (result) {
                  if (result.msg == '成功') {
                    var $pwdDialog = top.appModal.dialog({
                      message:
                        '重置成功！重置后，用户的登录密码为 <span id="initialPwd">' +
                        result.data +
                        '</span><br>关闭弹窗后，将无法查看密码！',
                      title: '重置密码',
                      buttons: {
                        copy: {
                          label: '复制密码',
                          className: 'well-btn w-btn-primary',
                          callback: function () {
                            $('.bootbox-body', $pwdDialog).append("<textarea id='copyPwdContent' style='opacity: 0;'>");
                            var copyContent = $('#initialPwd', $pwdDialog).html();
                            var inputElement = top.document.getElementById('copyPwdContent');
                            inputElement.value = copyContent;
                            inputElement.select();
                            top.document.execCommand('Copy');
                            appModal.success('复制成功！');
                            $('#copyPwdContent').remove();
                          }
                        },
                        cancel: {
                          label: '关闭',
                          className: 'btn-default'
                        }
                      }
                    });
                  } else {
                    appModal.error(result.msg);
                  }
                }
              });
            });
          }
        });

      _this.$element.off('change', 'input[name="jobRank"]').on('change', 'input[name="jobRank"]', function () {
        var data = $(this).wellSelect('data').data;
        var level = [];
        var jobLevel = $(this).parents('.job-item').find("input[name='jobLevel']").val();
        var clearJobLevel = true;
        $.each(data, function (index, item) {
          if (jobLevel == item) {
            keepJobLevel = false;
          }
          level.push({
            id: item,
            text: item
          });
        });

        $(this).parents('.job-item').find("input[name='jobLevel']").wellSelect('reRenderOption', {
          data: level
        });
        // 职级变更清空职档
        if (clearJobLevel) {
          $(this).parents('.job-item').find("input[name='jobLevel']").wellSelect('val', '');
        }
        if (level.length > 0) {
          $(this).parents('.job-item').find('.job-level').show();
        } else {
          $(this).parents('.job-item').find('.job-level').hide();
        }
      });

      $('.add-job-level')
        .off('click')
        .on('click', function () {
          var $jobItem = $(this).parent().find('.job-item');
          var index = $jobItem.length > 0 ? $jobItem.last().data('index') + 1 : 0;
          var html = '';
          html +=
            '<div class="job-item" style="margin-bottom: 10px" data-index="' +
            index +
            '">' +
            '<div class="main-job">' +
            '<input type="hidden" name="otherJobIds" id="otherJobIds' +
            index +
            '">' +
            '<input id="otherJobNamePaths' +
            index +
            '" class="modal-input" name="otherJobNamePaths" type="text" readonly="readonly">' +
            '</div>' +
            '<div class="main-job job-rank" style="display: none">' +
            '<input class="form-control" type="text" name="jobRank" id="jobRank' +
            index +
            '">' +
            '<input class="form-control" type="hidden" name="jobRankId" id="jobRankId' +
            index +
            '">' +
            '</div>' +
            '<div class="main-job job-level" style="display: none">' +
            '<input class="form-control" type="text" name="jobLevel" id="jobLevel' +
            index +
            '">' +
            '<input class="form-control" type="hidden" name="jobGrade" id="jobGrade' +
            index +
            '">' +
            '</div>' +
            '<span class="del-job-level">-</span>' +
            '</div>';
          $(this).before(html);

          $('#jobRank' + index).wellSelect({
            data: [],
            labelField: 'jobRank' + index,
            valueField: 'jobRankId' + index,
            searchable: false,
            showEmpty: false,
            placeholder: '请选择职级'
          });

          $('#jobLevel' + index).wellSelect({
            data: [],
            searchable: false,
            showEmpty: false,
            placeholder: '请选择职档'
          });
        });

      _this.$element.off('click', '.del-job-level').on('click', '.del-job-level', function () {
        // if ($(this).parents('.well-form-control').first().find('.job-item').length == 1) {
        //   return false;
        // }
        $(this).parent().remove();
      });

      function checkUserPwd(uuid, self) {
        var _this = self;
        if (!uuid && _this.rules.adminSetPwdType == 'ASPT02') {
          var isSave = true;
          var len = $('#org-user-tabs-1', _this.$element).find('.error:visible').length;
          var password = $('#password', _this.$element).val();
          var confirmPwd = $('#confirmPwd', _this.$element).val();
          if (password == '') {
            $('#password', _this.$element).next('.error').html(self.placeholder).show();
            isSave = false;
          } else {
            var latter = self.rules.letterAsk;
            var minLength = self.rules.minLength || 4;
            var maxLength = self.rules.maxLength || 20;
            var latterRegLower = /[a-z]+/;
            var latterRegupper = /[A-Z]+/;
            var latters = /[a-zA-Z]+/;
            var numReg = /[0-9]+/;
            var others = /[`~!@#$%^&*()_\-+=<>?:"{}|,.\/;'\\[\]·~！@#￥%……&*（）——\-+={}|《》？：“”【】、；‘'，。、]+/im;

            if (minLength > password.length || maxLength < password.length) {
              isSave = false;
            } else if (/[\u4E00-\u9FA5]+/.test(password)) {
              isSave = false;
            } else {
              var i = 0;
              if (latterRegLower.test(password) || latterRegupper.test(password)) {
                i++;
              }

              if (numReg.test(password)) {
                i++;
              }
              if (others.test(password)) {
                i++;
              }
              if (i - (latter == 'LA02' ? '2' : latter == 'LA01' ? '1' : '3') < 0) {
                isSave = false;
              } else if (
                self.rules.letterLimited == 'LL01' &&
                latters.test(password) &&
                (!latterRegLower.test(password) || !latterRegupper.test(password))
              ) {
                isSave = false;
              }
            }
            if (!isSave) {
              $('#password', _this.$element)
                .next('.error')
                .html('不符合密码格式：' + self.placeholder)
                .show();
            }
          }
          if (confirmPwd == '') {
            $('#confirmPwd', _this.$element).next('.error').html('请再次输入登录密码').show();
            isSave = false;
          }
          if (password != confirmPwd) {
            $('#confirmPwd', _this.$element).next('.error').html('两次密码输入不一致，请再次输入登录密码').show();
            isSave = false;
          }
          if (!isSave || len > 0) {
            return false;
          }
        }
      }

      function get_set_user_role_dialog_html() {
        return (
          '<div id="user-roles-setting-dialog" class="row"">' +
          '   <div class="col-md-6" style="border-right: 1px solid #E8E8E8">' +
          '       <p>选择角色：</p>' +
          '       <p>' +
          '           <input id="user-roles-dialog-keyword" type="text" placeholder="关键字" style="width: 200px;">' +
          '           <button id="user-roles-dialog-search-btn" type="button" class="well-btn w-btn-primary">查询</button>' +
          '       </p>' +
          '       <ul id="user-roles-dialog-selectable-roles">' +
          '           <div id="user-roles-dialog-selectable-roles-none-data-placeholder" style="display: none;">' +
          '               <div class="well-table-no-data"></div>' +
          '               <div style="text-align: center">没有找到匹配的记录</div>' +
          '           </div>' +
          '       </ul>' +
          '   </div>' +
          '   <div class="col-md-6" >' +
          '       <p>已选角色：</p>' +
          '       <ul id="user-roles-dialog-selected-roles"></ul>' +
          '   </div>' +
          '</div>'
        );
      }

      function computeExcludeIds(excludeIdPaths) {
        var excludeIds = [];
        if (excludeIdPaths) {
          excludeIdPaths = excludeIdPaths.split(';');
          for (var i = 0; i < excludeIdPaths.length; i++) {
            var eleId = $.common.treePath.getEleId(excludeIdPaths[i]);
            excludeIds.push(eleId);
          }
        }
        return excludeIds;
      }

      function showUserInfo(uuid) {
        $.ajax({
          url: ctx + '/api/org/multi/getUser',
          type: 'get',
          data: {
            uuid: uuid
          },
          success: function (result) {
            formBean = result.data;
            formBean.password = null;
            $('#org_user_form').json2form(formBean);
            $('#mainJobNamePath', _self.$element).val(formBean.mainJobSmartNamePath || formBean.mainJobNamePath);
            _self.getRankData(formBean.mainJobId, $('#mainJobNamePath'), formBean.relationList);
            $('#directLeaderNamePaths', _self.$element).val(formBean.directLeaderSmartNamePaths || formBean.directLeaderNamePaths);
            // 设置登录名只读状态,登录名不能修改,归属单位不能修改
            $('#loginName').attr('readonly', 'readonly');
            if (formBean.photoUuid != null && $.trim(formBean.photoUuid) != '') {
              var photoUrl = ctx + '/org/user/view/photo/' + formBean.photoUuid;
              $('#user_photo').attr('src', photoUrl);
              $('#user_photo').show();
              $('#user_default_photo').hide();
            } else {
              $('#user_photo').attr('src', '#');
              $('#user_photo').hide();
              $('#user_default_photo').show();
            }
            $('#lastLoginTime').html(formBean.lastLoginTime);

            if (formBean.otherJobIds && $.trim(formBean.otherJobIds) != '') {
              var otherJobIds = formBean.otherJobIds.split(';');
              var otherJobNamePaths = (formBean.otherJobSmartNamePaths || formBean.otherJobNamePaths).split(';');

              $('.add-job-level', _self.$element).parent().find('.job-item').remove();
              var html = '';
              $.each(otherJobIds, function (index, item) {
                html +=
                  '<div class="job-item" style="margin-bottom: 10px" data-index="' +
                  index +
                  '">' +
                  '<div class="main-job">' +
                  '<input type="hidden" name="otherJobIds" id="otherJobIds' +
                  index +
                  '" value="' +
                  item +
                  '">' +
                  '<input id="otherJobNamePaths' +
                  index +
                  '" class="modal-input" name="otherJobNamePaths" type="text" readonly="readonly" value="' +
                  otherJobNamePaths[index] +
                  '">' +
                  '</div>' +
                  '<div class="main-job job-rank" style="display: none">' +
                  '<input class="form-control" type="text" name="jobRank" id="jobRank' +
                  index +
                  '" >' +
                  '<input class="form-control" type="hidden" name="jobRankId" id="jobRankId' +
                  index +
                  '">' +
                  '</div>' +
                  '<div class="main-job job-level" style="display: none">' +
                  '<input class="form-control" type="text" name="jobLevel" id="jobLevel' +
                  index +
                  '">' +
                  '</div>' +
                  '<span class="del-job-level">-</span>' +
                  '</div>';
              });
              $('.add-job-level', _self.$element).parent().prepend(html);

              $.each($("input[name='otherJobIds']", _self.$element), function (index, item) {
                $('#jobRank' + index).wellSelect({
                  data: [],
                  labelField: 'jobRank' + index,
                  valueField: 'jobRankId' + index,
                  searchable: false,
                  showEmpty: false,
                  placeholder: '请选择职级'
                });
                $('#jobLevel' + index).wellSelect({
                  data: [],
                  searchable: false,
                  showEmpty: false,
                  placeholder: '请选择职档'
                });
                _self.getRankData($(item).val(), $(item), formBean.relationList);
              });
            } else {
              $('.del-job-level', _self.$element).trigger('click');
            }

            $('#otherJobNamePaths', _self.$element).val(formBean.otherJobSmartNamePaths || formBean.otherJobNamePaths);

            // setTimeout(function(){
            //     var $zTree = $.fn.zTree.getZTreeObj("user_role_tree");
            //     $zTree.checkAllNodes(false);
            //     $("#user_roleUuids").empty();
            //     if (formBean.roleUuids) {
            //         var roles = formBean.roleUuids.split(";");
            //         for (var i = 0; i < roles.length; i++) {
            //             var nodes = $zTree.getNodesByParam("id", roles[i]);
            //             if (nodes.length > 0) {
            //                 $zTree.checkNode(nodes[0], true, false, true);
            //             }
            //         }
            //     }
            // },500)
            showAllRoleAndPrivilege(true);
            $('#btn_view_all_role').trigger('click');
          }
        });
        showPrivilegeResultTree(uuid);
      }

      function showAllRoleAndPrivilege(isRefresh) {
        if (isRefresh) {
          var userId = $('#userId').val();
          if (StringUtils.isNotBlank(userId)) {
            $('#all_role_list').bootstrapTable('removeAll');
            $.ajax({
              url: ctx + '/api/org/facade/queryAllUserRoleInfoDtoListByUserId',
              type: 'get',
              data: {
                userId: userId
              },
              async: false,
              success: function (result) {
                if (result.code === 0) {
                  var data = result.data;
                  _userSelectedRoles = [].concat(data);
                  resetRoleListTable(data);
                } else {
                  appModal.error(result.msg);
                }
              }
            });
          }
          $('#all_privilege_result_tree').empty();
        }
        $('#div_all_role').show();
      }

      var _userSelectedRoles = [];
      var _userSelectableRoles = [];
      var _addedRolesCache = [];
      var _removedRolesCache = [];

      function resetRoleListTable(data) {
        $('#all_role_list').bootstrapTable('removeAll');
        if (!data.length) {
          return;
        }

        // 排序：先按编号升序，后按ID升序
        data.sort(function (a, b) {
          return b.roleCode.localeCompare(a.roleCode) || b.roleId.localeCompare(a.roleId);
        });

        // 筛选
        var keyword = $('#roleKeyword').val().toLowerCase().trim();
        var roleSource = $('#roleSource').val().toLowerCase().trim();
        var tableData = data.filter(function (item) {
          if (roleSource !== '' && roleSource !== '全部' && !item.source.includes(roleSource)) {
            return false;
          }
          if (item.roleName.toLowerCase().trim().includes(keyword)) {
            return true;
          }
          if (item.roleCode.toLowerCase().trim().includes(keyword)) {
            return true;
          }
          if (item.roleId.toLowerCase().includes(keyword)) {
            return true;
          }
          return false;
        });

        for (var i = 0; i < tableData.length; i++) {
          var item = tableData[i];
          if (item.roleName) {
            $('#all_role_list').bootstrapTable('insertRow', {
              index: 0,
              row: {
                roleId: item.roleId,
                roleName: item.roleName,
                source: item.source,
                calculatePath: item.calculatePath,
                roleCode: item.roleCode
              }
            });
          }
        }
      }

      function handleRolesDialogConfirm(selectedRolesUuids) {
        var beanObj = {
          userId: null,
          roleUuids: null
        };
        beanObj.userId = formBean.id;
        beanObj.roleUuids = selectedRolesUuids;
        $.ajax({
          type: 'POST',
          url: ctx + '/api/org/facade/queryAllUserRoleInfoDtoListByUserIdAndRoleUuids',
          dataType: 'json',
          contentType: 'application/json',
          data: JSON.stringify(beanObj),
          async: false,
          success: function (result) {
            if (result.code === 0) {
              _userSelectedRoles = result.data;
              resetRoleListTable(_userSelectedRoles);
            } else {
              appModal.error(result.msg);
            }
          }
        });
      }

      function showAllPrivilegeResultTree() {
        // 没有数据才需要重新加载
        if ($('#all_privilege_result_tree li').length == 0) {
          if (StringUtils.isNotBlank(_this.uuid)) {
            var setting = {};
            $.ajax({
              url: ctx + '/api/org/multi/getUserAllPrivilegeResultTree',
              type: 'get',
              data: {
                uuid: _this.uuid
              },
              success: function (result) {
                var zTree = $.fn.zTree.init($('#all_privilege_result_tree', _this.$element), setting, result.data);
                var nodes = zTree.getNodes();
                // 默认展开第一个节点
                if (nodes.length > 0) {
                  var node = nodes[0];
                  zTree.expandNode(node, true, false, false, true);
                }
              }
            });
          }
        }
        $('#div_all_privilege', _this.$element).show();
      }

      function showPrivilegeResultTree(userUuid) {
        var $zTreeContainer = $('#user_resource_tree', _this.$element);
        var setting = {
          callback: {
            onClick: function (event, treeId, treeNode) {
              if (treeNode.isParent || treeNode.id.substr(0, 1) === 'R') {
                // 只点击权限节点时生效
                return;
              }

              $('#user_resource_tree .title-row .title-role', _this.$element).show();
              $('#user_resource_tree .title-row .title-role-name', _this.$element).text(treeNode.name);
              appModal.showMask('权限数据加载中...');
              $.ajax({
                url: ctx + '/security/privilege/getOtherResourceTreeOnlyCheck',
                type: 'get',
                data: {
                  uuid: null,
                  privilegeUuid: treeNode.id.substr(1)
                },
                success: function (result) {
                  $.fn.zTree.destroy('user-role-integrate-tree');

                  if (result.data && result.data[0]) {
                    for (var i = 0; i < result.data[0].children.length; i++) {
                      result.data[0].children[i].name = '产品: ' + result.data[0].children[i].name;
                    }
                    $('#user-role-integrate-info-none-data-placeholder', _this.$element).hide();
                    $('#user-role-integrate-info-search', _this.$element).show();
                    var zTree = $.fn.zTree.init($('#user-role-integrate-tree', _this.$element), {}, result.data[0].children);
                    zTree.expandAll(true);
                  } else {
                    $('#user-role-integrate-info-none-data-placeholder', _this.$element).show();
                    $('#user-role-integrate-info-search', _this.$element).hide();
                    var zTree = $.fn.zTree.init($('#user-role-integrate-tree', _this.$element), {}, []);
                  }
                  $('#user-role-integrate-info-search-input', _this.$element).val('');
                  appModal.hideMask();
                },
                error: function (error) {
                  console.log(error);
                  appModal.hideMask($);
                }
              });
            }
          }
        };
        $.ajax({
          url: ctx + '/api/org/multi/getUserAllPrivilegeResultTree',
          type: 'get',
          data: {
            uuid: userUuid
          },
          success: function (result) {
            var data = result.data.children;
            setUserPrivilegeResultTreeIcon(data, true);
            if (data && data.length) {
              $('#user_privilege_result_tree-none-data-placeholder', _this.$element).hide();
            }
            var zTree = $.fn.zTree.init($('#user_privilege_result_tree', _this.$element), setting, data);
            var nodes = zTree.getNodes();
            // 默认展开第一个节点
            if (nodes.length > 0) {
              var node = nodes[0];
              zTree.expandNode(node, true, false, false, true);
            }
          }
        });
      }

      function setUserPrivilegeResultTreeIcon(nodesArray, firstLevel) {
        var roleIconUrl = '../../../../static/js/pt/img/role.png';
        var rightIconUrl = '../../../../static/js/pt/img/right.png';

        for (var i = 0; i < nodesArray.length; i++) {
          var node = nodesArray[i];
          var nodeTypeSymbol = node.id.substr(0, 1);
          node.icon = firstLevel || nodeTypeSymbol === 'R' ? roleIconUrl : rightIconUrl;
          if (node.children && node.children.length) {
            setUserPrivilegeResultTreeIcon(node.children);
          }
        }
      }

      function initFormLayout() {
        // 初始化角色树
        $.common.ztree.initRoleTree('user_role_tree', 'user_roleUuids');
        initUploadMethod();
        // 初始化头像上传和删除按钮
        $('#btn_delete', _this.$element).click(function () {
          $('#photoUuid', _this.$element).val('');
          $('#user_photo', _this.$element).attr('src', '');
          $('#upload', _this.$element).val('');
          $('#user_photo', _this.$element).hide();
          $('#user_default_photo').show();
        });

        $('#upload', _this.$element).bind('change', function () {
          var file = this.value;
          if (file == '') {
            appModal.error('请选择.jpeg,.jpg,.png文件');
            return;
          }
          if (file.indexOf('.') < 0) {
            return;
          }
          var fileType = file.substring(file.lastIndexOf('.'), file.length);
          if (fileType != '.jpeg' && fileType != '.jpg' && fileType != '.png') {
            appModal.error('请选择.jpeg,.jpg,.png文件');
            $('#upload').val('');
            return false;
          }
        });

        $('#btn_upload', _this.$element).click(function () {
          $('#upload', _this.$element).trigger('click');
        });

        initAllRoleAndPrivilegeTab();
        initWorkBenchTable();
        $('#userWorkBenchInfo').parents('.bootstrap-table').addClass('ui-wBootstrapTable');
        $('#all_role_list').parents('.bootstrap-table').addClass('ui-wBootstrapTable');
      }

      function initUploadMethod() {
        var url = ctx + fileServiceURL.saveFiles; // 上传文件的地址
        var $fileElement = $('#upload', _this.$element);
        $fileElement
          .fileupload({
            url: url,
            iframe: $.browser.msie && $.browser.version < 10,
            dataType: 'json',
            autoUpload: true,
            formData: {
              signUploadFile: ''
            },
            maxFileSize: 50000000 // 2000 MB
          })
          .on('fileuploaddone', function (e, data) {
            var photoCode = data._response.result.data[0].fileID;
            $('#photoUuid', _this.$element).val(photoCode);
            $('#user_photo', _this.$element).attr('src', ctx + '/org/user/view/photo/' + photoCode);
            $('#user_photo', _this.$element).show();
            $('#user_default_photo').hide();
          })
          .on('fileuploadfail', function (e, data) {
            appModal.error(data);
          });
      }

      function initAllRoleAndPrivilegeTab() {
        // 初始化成员数
        $('#all_role_list', _this.$element).bootstrapTable({
          data: [],
          idField: 'id',
          striped: false,
          showColumns: false,
          sortable: true,
          undefinedText: '',
          columns: [
            {
              field: 'roleName',
              title: '名称'
            },
            {
              field: 'roleId',
              title: 'ID'
            },
            {
              field: 'roleCode',
              title: '编号'
            },
            {
              field: 'source',
              title: '来源'
            },
            {
              field: 'calculatePath',
              width: 280,
              editableOriginalTitle: '计算路径',
              title:
                "计算路径 <div class='user-role-path'><i class='iconfont icon-ptkj-tishishuoming'></i>" +
                "<div class='user-role-path-tips'>" +
                '来源和计算路径的说明如下：</br>' +
                '● 用户：配置用户角色中的已选角色，或角色成员中选择了用户，计算路径显示为空 </br>' +
                '● 组织：角色成员中选择了组织节点，用户的职位（工作信息）为该组织节点或其递归下级，计算路径为【组织节点全路径（组织树从组织版本开始，到选择节点的路径）】</br>' +
                '● 群组：角色成员中选择了群组，群组的成员中选择了用户或组织（用户的职位归属该组织），计算路径为【群组名称】</br>' +
                '● 父角色：角色的父角色为来源（用户、组织、群组、父角色）的角色，计算路径为【直属父角色的角色名称】</div>' +
                '</div>'
            }
          ]
        });

        // 查看所有角色
        $('#btn_view_all_role', _this.$element).click(function () {
          showAllRoleAndPrivilege(false);
          $('#div_all_privilege', _this.$element).hide();
          $('#btn_view_all_role', _this.$element).hide();
          $('#btn_view_all_privilege', _this.$element).show();
        });

        setTableTdTitle('all_role_list');

        // 查看所有权限
        // $("#btn_view_all_privilege",_this.$element).click(function() {
        //     $("#div_all_role",_this.$element).hide();
        //     $("#div_all_privilege",_this.$element).show();
        //     $("#btn_view_all_role",_this.$element).show();
        //     $("#btn_view_all_privilege",_this.$element).hide();
        //     // 检查
        //     showAllPrivilegeResultTree();
        // });
      }

      $('#userWorkBenchInfo', _this.$element).parents('.fixed-table-body').css({
        minHeight: '200px'
      });

      function initWorkBenchTable() {
        var $userWorkBenchInfo = $('#userWorkBenchInfo');
        $userWorkBenchInfo.bootstrapTable({
          data: [],
          pagination: false,
          showColumns: false,
          undefinedText: '',
          onPostBody: function () {
            $userWorkBenchInfo.find('.work-bench-user-default').parent().css({
              position: 'relative'
            });
          },
          columns: [
            {
              field: 'uuid',
              title: 'uuid',
              visible: false
            },
            {
              field: 'name',
              title: '名称',
              formatter: function (v, row, i) {
                if (row.userDef) {
                  return v + "<span class='work-bench-user-default iconfont icon-ptkj-yonghumoren'></span>";
                } else {
                  return v;
                }
              }
            },
            {
              field: 'id',
              title: 'ID'
            },
            {
              field: 'code',
              title: '编号'
            },
            {
              field: 'ascription',
              title: '归属'
            },
            {
              field: 'source',
              title: '来源',
              formatter: function (v, row, i) {
                if (v == 1) {
                  return '用户';
                } else if (v == 2) {
                  return '组织';
                } else if (v == 3) {
                  return '角色';
                } else if (v == 4) {
                  return '默认';
                }
              }
            },
            {
              field: 'calculatePath',
              width: 280,
              editableOriginalTitle: '计算路径',
              title:
                "计算路径<div class='workbench-route'><i class='iconfont icon-ptkj-tishishuoming'></i>" +
                "<div class='workbench-route-tips'>根据来源显示具体路径：<br>用户：用户姓名<br>角色：角色名称<br>组织：组织树上从组织版本开始，到选择节点的路径，选择节点为工作台使用者管理中选择的组织节点</div>" +
                '</div>'
            }
          ]
        });
      }

      function getWorkBenchList(source, keyword) {
        $.ajax({
          url: ctx + '/api/page/definition/listPath',
          type: 'get',
          data: {
            userUuid: _self.uuid,
            source: source || '0',
            keyword: keyword || ''
          },
          success: function (result) {
            var benchList = result.data;
            $('#userWorkBenchInfo', _this.$element).bootstrapTable('removeAll');
            if (benchList.length > 0) {
              for (var i = 0; i < benchList.length; i++) {
                $('#userWorkBenchInfo').bootstrapTable('insertRow', {
                  index: i,
                  row: benchList[i]
                });
              }
              setTableTdTitle('userWorkBenchInfo');
            }
          },
          error: function (err) {
            appModal.dialogError(err.responseJSON.msg);
          }
        });
      }

      function setTableTdTitle(id) {
        $('#' + id + ' tbody', _this.$element).on('mouseover', 'tr td:not(#fixedtd)', function () {
          var $this = $(this);
          $this.attr('title', $this.text());
        });
      }
    },
    getRankData: function (id, $el, datas) {
      if (id == undefined || id == null || id == '') {
        return;
      }
      // var jobIds = id.split('/');
      $.ajax({
        type: 'get',
        url: ctx + '/api/org/multi/getJobRankByJobId/' + id,
        async: false,
        dataType: 'json',
        success: function (result) {
          if (result.code == 0) {
            if (result.data && result.data.length > 0) {
              $el.parent().siblings().show();
              var rankList = [];
              var jobLevel = [];
              $.each(result.data, function (index, item) {
                if (datas) {
                  $.each(datas, function (j, data) {
                    if (data.jobId == id) {
                      jobLevel = item.jobLevel || [];
                    }
                  });
                }
                rankList.push({
                  text: item.jobRank,
                  id: item.id,
                  data: item.jobLevel,
                  desc: item.jobGrade
                });
              });

              $el
                .parents('.job-item')
                .find("input[name='jobRank']")
                .wellSelect('reRenderOption', {
                  data: rankList
                })
                .wellSelect('val', '')
                .trigger('change');

              if (datas) {
                var rankIndex = _.findIndex(datas, function (o) {
                  return o.jobId == id;
                });
                if (rankIndex > -1) {
                  var rank = datas[rankIndex];
                  $el.parents('.job-item').find("input[name='jobRank']").wellSelect('val', rank.jobRankId).trigger('change');
                  $el.parents('.job-item').find("input[name='jobLevel']").wellSelect('val', rank.jobLevel);
                }
              }
              if (jobLevel.length > 0) {
                $el.parents('.job-item').find('.job-level').show();
              } else {
                $el.parents('.job-item').find('.job-level').hide();
              }
            } else {
              //如果对应的职位没有选择职务则显示历史职位，处理历史数据问题
              //$el.parents('.job-item').find('.main-job').hide();
              $el
                .parents('.job-item')
                .find("input[name='jobRank']")
                .wellSelect('reRenderOption', {
                  data: rankList
                })
                .wellSelect('val', '')
                .trigger('change');
              $el.parent().siblings('.main-job').hide();
            }
          }
        }
      });
    },
    renderJobField: function () {
      var self = this;
      var allJobIds = [];
      var mainJobIdField = $('#mainJobId', self.$element).val();
      var $mainJobPathField = $('#mainJobNamePath', self.$element);
      var otherJobIds = [];
      $.each($("input[name='otherJobIds']", self.$element), function (index, item) {
        otherJobIds.push($(item).val());
      });
      // var otherJobIdsField = otherJobIds;
      // var $otherJobPathsField = $('#otherJobNamePaths', self.$element);
      var directLeaderIdsField = $('#directLeaderIds', self.$element).val();
      var $directLeaderNamePathsField = $('#directLeaderNamePaths', self.$element);
      var mainJobId = mainJobIdField.split('/').pop() || '';
      // var otherJobIds = otherJobIdsField ? otherJobIdsField.split(';') : [];
      var _otherJobIds = otherJobIds.map(function (item) {
        return item.split('/').pop();
      });
      var directLeaderIds = directLeaderIdsField ? directLeaderIdsField.split(';') : [];
      var _directLeaderIds = directLeaderIds.map(function (item) {
        return item.split('/').pop();
      });
      allJobIds = _otherJobIds.concat(mainJobId).concat(_directLeaderIds);

      var n = [];
      for (var i = 0; i < allJobIds.length; i++) {
        if (n.indexOf(allJobIds[i]) == -1) n.push(allJobIds[i]);
      }
      allJobIds = n;
      var smartNames = self.getSmartName(allJobIds);
      if (mainJobId) {
        $mainJobPathField.val(smartNames[mainJobId].smartNamePath);
      }
      if (_otherJobIds.length) {
        if (_otherJobIds.length == 1 && _otherJobIds[0] == '') {
        } else {
          $.each($("input[name='otherJobIds']", self.$element), function (index, item) {
            // otherJobIds.push($(item).val());
            var val = $(item).val();
            var ids = val.split('/').pop();
            $(item).next().val(smartNames[ids].smartNamePath);
          });
        }
        // var paths = _otherJobIds.map(function (item) {
        //   return smartNames[item].smartNamePath;
        // });
        // $otherJobPathsField.val(paths.join(';'));
      }
      if (_directLeaderIds.length) {
        var paths = _directLeaderIds.map(function (item) {
          if (item[0] === 'U') {
            return smartNames[item].name;
          }
          return smartNames[item].smartNamePath;
        });
        $directLeaderNamePathsField.val(paths.join(';'));
      }
    },
    getSmartName: function (ids) {
      var res = {};
      if (ids instanceof String) {
        ids = ids.split(',');
      }
      server.JDS.restfulPost({
        url: '/proxy/api/org/tree/dialog/smartName',
        async: false,
        contentType: 'application/json',
        dataType: 'json',
        data: {
          nodeIds: ids,
          nameDisplayMethod: '1'
        },
        success: function (result) {
          res = result.data;
        }
      });
      return res;
    },
    getResetPwdDialog: function (url, uuid) {
      var self = this;
      var html = self.getResetPwdHtml();
      var $dialog = appModal.dialog({
        message: html,
        title: '重置密码',
        width: 540,
        shown: function () {
          $('#newPwd', $dialog).attr('placeholder', self.placeholder);
          self.checkPwd('newPwd', 'confirmPwd', $dialog);
        },
        buttons: {
          ok: {
            label: '确定',
            className: 'well-btn w-btn-primary',
            callback: function () {
              var isSave = true;
              var len = $('#userResetPwdForm', $dialog).find('.error:visible').length;
              var newPwd = $('#newPwd', $dialog).val();
              var confirmPwd = $('#confirmPwd', $dialog).val();
              if (newPwd == '') {
                $('#newPwd', $dialog).next('.error').html(self.placeholder).show();
                isSave = false;
              }
              if (confirmPwd == '') {
                $('#confirmPwd', $dialog).next('.error').html('请再次输入登录密码').show();
                isSave = false;
              }
              if (newPwd != confirmPwd) {
                $('#confirmPwd', $dialog).next('.error').html('两次密码输入不一致，请再次输入登录密码').show();
                isSave = false;
              }
              if (!isSave || len > 0) {
                return false;
              }

              $.ajax({
                type: 'POST',
                url: ctx + url,
                dataType: 'json',
                data: {
                  userDefinedPwd: base64.encode(urlencode(newPwd)),
                  userUuids: uuid
                },
                success: function (result) {
                  if (result.msg == '成功') {
                    appModal.success({
                      message: '重置密码成功！'
                    });
                  } else {
                    appModal.error('重置密码失败！');
                    return false;
                  }
                }
              });
            }
          },
          cancel: {
            label: '取消',
            className: 'btn-default'
          }
        }
      });
    },
    getLoginSetting: function () {
      $('.login-name-zh').hide();
      $.ajax({
        type: 'GET',
        url: ctx + '/proxy/web/login/config/getLoginSettings',
        dataType: 'json',
        success: function (result) {
          if (result.code == 0 && result.data) {
            var loginSetting = result.data;
            if (loginSetting.accountZhEnable == 1) {
              $('.login-name-zh').show();
            }
          }
        }
      });
    },
    getResetPwdHtml: function () {
      var html = '';
      html +=
        '<form class="form-vertical " role="form" id="userResetPwdForm" style="padding-top:15px;">' +
        '<div class="form-group">' +
        '<label class="col-sm-3 control-label"><font style="color:red;margin-right:3px;">*</font>请设置重置后，用户的登录密码：</label>' +
        '<div class="col-sm-8">' +
        '<input type="password" class="form-control" name="newPwd" id="newPwd" placeholder="' +
        this.placeholder +
        '">' +
        '<label class="error iconfont icon-ptkj-wentiziduantishi" style="text-align: left;"></label>' +
        '</div>' +
        '</div>' +
        '<div class="form-group">' +
        '<label class="col-sm-3 control-label"><font style="color:red;margin-right:3px;">*</font>确认密码：</label>' +
        '<div class="col-sm-8">' +
        '<input type="password" class="form-control" name="confirmPwd" id="confirmPwd" placeholder="请再次输入登录密码">' +
        '<label class="error iconfont icon-ptkj-wentiziduantishi" style="text-align: left;"></label>' +
        '</div>' +
        '</div>' +
        '<p class="reset-pwd-tips">重置后，已锁定的账号将会自动解锁！</p>' +
        '</form>';
      return html;
    },
    getPwdRules: function (uuid) {
      var self = this;
      $.ajax({
        type: 'GET',
        url: ctx + '/api/pwd/setting/getMultiOrgPwdSetting',
        dataType: 'json',
        success: function (result) {
          self.rules = result.data;
          var latter = self.rules.letterAsk == 'LA02' ? '至少包含2种' : self.rules.letterAsk == 'LA01' ? '至少包含1种' : '包含3种';
          var minLength = self.rules.minLength || 4;
          var maxLength = self.rules.maxLength || 20;
          var letterLimited = self.rules.letterLimited == 'LL01' ? '(必须要有大写、小写)' : '';
          self.placeholder = '字母' + letterLimited + '、数字、特殊字符中' + latter + '，' + minLength + '~' + maxLength + '位';
          $('#password', self.$element).attr('placeholder', self.placeholder);
          if (!uuid && self.rules.adminSetPwdType == 'ASPT02') {
            $('.user-detail-add', self.$element).show();
            self.checkPwd('password', 'confirmPwd', self.$element);
          }
        }
      });
    },
    checkPwd: function (newPwd, comfirmPwd, $dialog) {
      var self = this;
      $('#' + newPwd, $dialog)
        .off('blur')
        .on('blur', function () {
          var val = $(this).val();
          if (val == '') {
            $(this).next('.error').html('登录密码不能为空').show();
          } else {
            var latter = self.rules.letterAsk;
            var minLength = self.rules.minLength || 4;
            var maxLength = self.rules.maxLength || 20;
            var isHide = true;
            var latterRegLower = /[a-z]+/;
            var latterRegupper = /[A-Z]+/;
            var latters = /[a-zA-Z]+/;
            var numReg = /[0-9]+/;
            var others = /[`~!@#$%^&*()_\-+=<>?:"{}|,.\/;'\\[\]·~！@#￥%……&*（）——\-+={}|《》？：“”【】、；‘'，。、]+/im;

            if (minLength > val.length || maxLength < val.length) {
              isHide = false;
            } else if (/[\u4E00-\u9FA5]+/.test(val)) {
              isHide = false;
            } else {
              var i = 0;
              if (latterRegLower.test(val) || latterRegupper.test(val)) {
                i++;
              }

              if (numReg.test(val)) {
                i++;
              }
              if (others.test(val)) {
                i++;
              }
              if (i - (latter == 'LA02' ? '2' : latter == 'LA01' ? '1' : '3') < 0) {
                isHide = false;
              } else if (
                self.rules.letterLimited == 'LL01' &&
                latters.test(val) &&
                (!latterRegLower.test(val) || !latterRegupper.test(val))
              ) {
                isHide = false;
              }
            }
            if (!isHide) {
              $(this)
                .next('.error')
                .html('不符合密码格式：' + self.placeholder)
                .show();
              return false;
            } else {
              $(this).next('.error').html('').hide();
            }
          }
        });

      $('#' + comfirmPwd, $dialog)
        .off('blur')
        .on('blur', function () {
          if ($(this).val() == '') {
            $(this).next('.error').html('请再次输入登录密码').show();
          } else if ($(this).val() != $('#' + newPwd, $dialog).val()) {
            $(this).next('.error').html('两次密码输入不一致，请再次输入登录密码').show();
          } else {
            $(this).next('.error').html('').hide();
          }
        });
    },
    refresh: function () {
      var _self = this;
      _self.init();
    }
  });
  return AppOrgUserWidgetDevelopment;
});
