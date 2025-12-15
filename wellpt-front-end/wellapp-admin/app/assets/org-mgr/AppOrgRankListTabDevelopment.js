define(['constant', 'commons', 'server', 'appContext', 'appModal', 'HtmlWidgetDevelopment', 'lodash'], function (
  constant,
  commons,
  server,
  appContext,
  appModal,
  HtmlWidgetDevelopment,
  lodash
) {
  var JDS = server.JDS;
  var _debounce = lodash.debounce;
  var AppOrgRankListTabDevelopment = function () {
    HtmlWidgetDevelopment.apply(this, arguments);
  };
  commons.inherit(AppOrgRankListTabDevelopment, HtmlWidgetDevelopment, {
    init: function () {
      var $element = this.widget.element;
      var self = this;
      getPostList(self); // 获取职务序列列表
      getLevelList(self); // 获取职等列表
      getRankList(self); // 获取职级列表

      $('.rank-tab-item', $element) // 页签切换
        .off()
        .on('click', function () {
          var index = $(this).data('type');
          $(this).siblings().removeClass('active');
          $(this).addClass('active');
          $('.rank-tab-content', $element).hide();
          $('.rank-tab-content' + index, $element).show();
          if (index == '1') {
            changeListLiWidth($('#postList'));
          } else if (index == '2') {
            changeLevelListRankItemWidth($('#levelList'));
          } else if (index == '3') {
            changeListLiWidth($('#rankList'));
          }
        });

      $('input', $element) // 输入框事件
        .off()
        .on('keyup', function (e) {
          var val = $(this).val();
          $(this).siblings('.rank-close')[val == '' ? 'hide' : 'show']();
          if (e.keyCode == 13) {
            $(this).siblings('.rank-search').trigger('click');
          }
        });

      $('.rank-close', $element) // 清空输入框内容
        .off()
        .on('click', function () {
          $(this).prev().val('');
          $(this).hide();
          var id = $(this).parent().find('input').attr('id');
          if (id == 'rankInput') {
            getRankList(self, '');
          } else {
            getPostList(self, '');
          }
        });

      $('.rank-search', $element) // 搜索
        .off()
        .on('click', function () {
          var id = $(this).parent().find('input').attr('id');
          var val = $(this).parent().find('input').val();
          if (id == 'rankInput') {
            getRankList(self, val);
          } else {
            getPostList(self, val);
          }
        });

      //鼠标移入事件
      $element.off('mouseover', '.lay-operate-icon .iconfont').on('mouseover', '.lay-operate-icon .iconfont', function () {
        // $(this).parent().next().show();
        var $layOperateBtns = $(this).parent().next();
        showLayOperateBtns($layOperateBtns);
      });

      //鼠标移入事件
      $element.off('mouseover', '.lay-operate-btns').on('mouseover', '.lay-operate-btns', function () {
        // $(this).show();
        $(this).data('mouseover', 'true');
        showLayOperateBtns($(this));
      });
      //鼠标移入事件
      $element.off('mouseenter', '.post-list .lay').on('mouseenter', '.post-list .lay', function () {
        var offset = $(this).offset();
        var $layOperateIcon = $(this).find('.lay-operate-icon');
        var $layOperateBtns = $(this).find('.lay-operate-btns');
        var left = $(this).parents('.post-list').first().closest('.rank-content-body').offset().left;
        $layOperateIcon.css({
          position: 'fixed',
          top: offset.top + 3,
          left: left + 180
        });
        $layOperateBtns.css({
          position: 'fixed',
          top: offset.top + 36,
          left: left + 116
        });
        $layOperateBtns.find('li').removeAttr('style');
        $layOperateBtns.hide();
      });
      //鼠标移出事件
      $element.off('mouseout', '.lay-operate-btns').on('mouseout', '.lay-operate-btns', function () {
        $(this).hide();
      });

      function showLayOperateBtns($layOperateBtns) {
        if ($layOperateBtns.closest('li').next().is(':hidden')) {
          return;
        }
        $layOperateBtns.show();
        //        if($layOperateBtns.find("li.btn-delete").is(":visible")) {
        //	       return;
        //        }
        $('#postList', $element).slimScroll();
      }

      $('#rankList', $element).slimScroll({
        height: 500,
        width: '220px',
        oneaxismousemode: false,
        cursorcolor: '#ccc',
        cursorwidth: '8px',
        scrollCLass: 'formScrollClass'
      });
      $('#postList', $element).slimScroll({
        height: 500,
        width: '220px',
        oneaxismousemode: false,
        cursorcolor: '#ccc',
        cursorwidth: '8px',
        scrollCLass: 'formScrollClass'
      });

      // 职务序列、职级编辑
      $element.off('click', '.btn-edit').on('click', '.btn-edit', function () {
        var id = $(this).parents('.post-list').attr('id');
        var uuid = $(this).parents('.lay').first().data('uuid');
        if (id == 'rankList') {
          rankEvent(self, uuid);
        } else {
          postEvent(self, uuid);
        }
      });

      // 职务序列、职级全部折叠
      $element.off('click', '.arrow').on('click', '.arrow', function () {
        if ($(this).hasClass('icon-ptkj-shixinjiantou-you')) {
          $(this).removeClass('icon-ptkj-shixinjiantou-you').addClass('icon-ptkj-shixinjiantou-xia');
          $(this).parents('.rank-cate-header').next().slideDown();
        } else {
          $(this).removeClass('icon-ptkj-shixinjiantou-xia').addClass('icon-ptkj-shixinjiantou-you');
          $(this).parents('.rank-cate-header').next().slideUp();
        }
        var id = $(this).parents('.rank-cate-header').next().attr('id');
        $('#' + id, $element)
          .getNiceScroll()
          .resize();
      });

      // 职务序列、职级折叠
      $element.off('click', '.folder').on('click', '.folder', function () {
        if ($(this).hasClass('icon-ptkj-shixinjiantou-you')) {
          $(this).removeClass('icon-ptkj-shixinjiantou-you').addClass('icon-ptkj-shixinjiantou-xia');
          $(this).parent().next().slideDown();
        } else {
          $(this).removeClass('icon-ptkj-shixinjiantou-xia').addClass('icon-ptkj-shixinjiantou-you');
          $(this).parent().next().slideUp();
        }
        var id = $(this).parents('.post-list').attr('id');
        $('#' + id, $element)
          .getNiceScroll()
          .resize();
      });

      // 职务序列、职级删除
      $element.off('click', '.btn-delete').on('click', '.btn-delete', function () {
        var $this = $(this);
        var id = $(this).parents('.post-list').attr('id');
        appModal.confirm('确定要删除？', function (res) {
          if (res) {
            var uuid = $this.parents('.lay').first().data('uuid');
            if (id == 'rankList') {
              $.ajax({
                type: 'DELETE',
                url: ctx + '/api/org/multi/deleteJobRank/' + uuid,
                dataType: 'json',
                success: function (res) {
                  if (res.code == 0) {
                    appModal.success('删除成功！');
                    getRankList(self);
                    getPostList(self);
                  } else {
                    appModal.error(res.msg);
                  }
                }
              });
            } else {
              $.ajax({
                type: 'DELETE',
                url: ctx + '/api/org/duty/hierarchy/dutySeqDelete/' + uuid,
                dataType: 'json',
                success: function (res) {
                  if (res.code == 0) {
                    appModal.success('删除成功！');
                    getRankList(self);
                    getPostList(self);
                  } else {
                    appModal.error(res.msg);
                  }
                }
              });
            }
          }
        });
      });

      // 添加职务序列
      $('#addPost', $element)
        .off()
        .on('click', function () {
          postEvent(self);
        });

      // 添加职级
      $('#addRank', $element)
        .off()
        .on('click', function () {
          rankEvent(self);
        });

      //1-100正则表达式
      var reg = new RegExp('^([1-9]|[1-9]\\d|100)$');

      // 添加职等
      $('#addLevel', $element)
        .off()
        .on('click', function () {
          var html = getLevelDialogContent();
          var $levelDialog = appModal.dialog({
            message: html,
            title: '职等设置',
            width: 800,
            shown: function () {
              var data = [];
              $.each(self.levelList, function (index, item) {
                if (item.isValid == 1) {
                  data.push(item);
                }
              });
              initTable($levelDialog, data);
              $('#levelTable', $levelDialog).parents('.bootstrap-table').addClass('ui-wBootstrapTable');
              if (self.levelList.length > 0) {
                var num1 = -1;
                var num2 = -1;
                $.each(self.levelList, function (index, item) {
                  if (num1 == -1 && item.isValid == 1) {
                    num1 = item.jobGrade;
                    $('#num1', $levelDialog).val(item.jobGrade);
                  }
                  if (item.isValid == 1) {
                    num2 = item.jobGrade;
                  }
                });
                if (num2 > -1) {
                  $('#num2', $levelDialog).val(num2);
                }
              }

              $('#num1', $levelDialog)
                .off()
                .on(
                  'keyup',
                  _debounce(function () {
                    if (reg.test($(this).val())) {
                      triggerTableEvent();
                    } else {
                      appModal.error('请输入1-100的整数！');
                    }
                  }, 300)
                );
              $('#num2', $levelDialog)
                .off()
                .on(
                  'keyup',
                  _debounce(function () {
                    if (reg.test($(this).val())) {
                      triggerTableEvent();
                    } else {
                      appModal.error('请输入1-100的整数！');
                    }
                  }, 300)
                );

              $('.num-arrows-up', $levelDialog)
                .off()
                .on(
                  'click',
                  _debounce(function () {
                    var $input = $(this).parent().prev();
                    var val = parseInt($input.val());
                    if (reg.test(val) && val != 100) {
                      //属于1-99
                      $input.val(val + 1);
                      triggerTableEvent();
                    }
                  }, 300)
                );

              $('.num-arrows-down', $levelDialog)
                .off()
                .on(
                  'click',
                  _debounce(function () {
                    var $input = $(this).parent().prev();
                    var val = parseInt($input.val());
                    if (reg.test(val) && val != 1) {
                      //属于2-100
                      $input.val(val - 1);
                      triggerTableEvent();
                    }
                  }, 300)
                );

              function triggerTableEvent() {
                var num1 = parseInt($('#num1', $levelDialog).val());
                var num2 = parseInt($('#num2', $levelDialog).val());
                if (reg.test(num1) && reg.test(num2) && num1 != num2) {
                  $('#levelTable', $levelDialog).bootstrapTable('removeAll');

                  if (self.levelList.length > 0) {
                    self.levelMap = {};
                    $.each(self.levelList, function (index, item) {
                      self.levelMap[item.jobGrade] = item;
                    });
                  }
                  initTableBody(num1, num2, self.levelMap);
                }
              }

              function initTableBody(num1, num2, levelMap) {
                var levelGrade = [];
                if (levelMap) {
                  levelGrade = Object.keys(levelMap);
                }
                if (num1 - num2 > 0) {
                  var count = -1;
                  for (var i = num1; i >= num2; i--) {
                    (count += 1), (uuid = ''), (jobGradeName = ''), (describe = '');
                    if (levelGrade.includes(i + '')) {
                      uuid = levelMap[i].uuid;
                      jobGradeName = levelMap[i].jobGradeName;
                      describe = levelMap[i].describe;
                    }
                    $('#levelTable', $levelDialog).bootstrapTable('insertRow', {
                      index: count,
                      row: {
                        uuid: uuid,
                        jobGrade: i,
                        jobGradeName: jobGradeName,
                        describe: describe
                      }
                    });
                  }
                } else {
                  for (var i = num1; i <= num2; i++) {
                    var uuid = '',
                      jobGradeName = '',
                      describe = '';
                    if (levelGrade.includes(i + '')) {
                      uuid = levelMap[i].uuid;
                      jobGradeName = levelMap[i].jobGradeName;
                      describe = levelMap[i].describe;
                    }
                    $('#levelTable', $levelDialog).bootstrapTable('insertRow', {
                      index: i,
                      row: {
                        uuid: uuid,
                        jobGrade: i,
                        jobGradeName: jobGradeName,
                        describe: describe
                      }
                    });
                  }
                }
              }
            },
            buttons: {
              save: {
                label: '保存',
                className: 'well-btn w-btn-primary',
                callback: function () {
                  var num1 = $('#num1', $levelDialog).val();
                  var num2 = $('#num2', $levelDialog).val();
                  if (num1 == '' || num2 == '') {
                    appModal.error('职等范围不能为空！');
                    return false;
                  }
                  if (num1 == num2) {
                    appModal.error('起始值和结束值不能相同！');
                    return false;
                  }
                  var reg = /^[0-9]*[1-9][0-9]*$/;
                  if (!reg.test(num1) || !reg.test(num2) || num1 > 100 || num2 > 100) {
                    appModal.error('职等范围只能输入1-100内的正整数！');
                    return false;
                  }
                  var obj = {
                    order: num1 - num2 > 0 ? 'desc' : 'asc'
                  };
                  obj.orgJobGrades = [];
                  var $tr = $('#levelTable', $levelDialog).find('tbody').find('tr');
                  $.each($tr, function (index, item) {
                    var jobGrade = $(item).find("td[data-field='jobGrade']").text();
                    var jobGradeName = $(item).find("td[data-field='jobGradeName']").find('input').val();
                    var describe = $(item).find("td[data-field='describe']").find('input').val();
                    obj.orgJobGrades.push({
                      uuid: '',
                      jobGrade: jobGrade,
                      jobGradeName: jobGradeName,
                      describe: describe
                    });
                  });
                  console.log(obj.orgJobGrades);
                  $.ajax({
                    type: 'post',
                    url: ctx + '/api/org/duty/hierarchy/jobGradeSave',
                    contentType: 'application/json',
                    data: JSON.stringify(obj),
                    success: function (result) {
                      if (result.code == 0) {
                        if (result.data) {
                          showExceedJobRankInfoDialog(result.data, obj, self);
                          // appModal.error(result.data);
                        }
                        appModal.success('保存成功！');
                        getLevelList(self);
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
        });

      function showExceedJobRankInfoDialog(msg, obj, ui) {
        appModal.dialog({
          message: "<br/><br/><br/><div style='text-align: center;'>" + msg + '</div>',
          buttons: {
            view: {
              label: '查看',
              className: 'well-btn w-btn-primary',
              callback: function () {
                var options = {
                  widgetDefId: 'page_20211209145823',
                  appType: 4,
                  target: '_targetWidget',
                  renderTo: '#wNavTab_1164A36C665A4FEF93F193861277C06B',
                  renderNavTab: true,
                  forceRenderIfConflict: true,
                  text: '查看职务体系'
                };
                appContext.renderWidget(options);
              }
            },
            cancel: {
              label: '取消',
              className: 'btn-default'
            }
          }
        });
      }

      $('#postList').css({
        'overflow-x': 'auto',
        'padding-bottom': '100px'
      });
      $('#rankList').css({
        'overflow-x': 'auto',
        'padding-bottom': '100px'
      });

      $('#postList')
        .off('click', '.post-name')
        .on('click', '.post-name', function () {
          $('li', $('#postList')).removeClass('active');
          $(this).closest('li').addClass('active');
          searchDuty($(this), 'dutySeqUuid', 'uuid');
        });

      $('#rankList')
        .off('click', '.post-name')
        .on('click', '.post-name', function () {
          // bug#59476
          if ($(this).closest('.lay').attr('data-type') == 'Q') {
            return;
          }
          $('li', $('#rankList')).removeClass('active');
          $(this).closest('li').addClass('active');
          searchDuty($(this), 'jobRankId', 'id');
        });

      $('.rank-cate-name')
        .off('click')
        .on('click', function () {
          $('li', $('#postList')).removeClass('active');
          $('li', $('#rankList')).removeClass('active');
          var dutyTable = $element.parent().siblings().find('.ui-wBootstrapTable').attr('id');
          $('#' + dutyTable).wBootstrapTable('removeParam', 'dutySeqUuid');
          $('#' + dutyTable).wBootstrapTable('removeParam', 'jobRankId');
          $('#' + dutyTable).wBootstrapTable('refresh');
        });

      function searchDuty($el, name, data) {
        var uuid = $el.parents('.lay').first().data(data);
        var dutyTable = $element.parent().siblings().find('.ui-wBootstrapTable').attr('id');

        $('#' + dutyTable)
          .wBootstrapTable('addParam', name, uuid)
          .wBootstrapTable('refresh');
      }

      // 职务序列弹窗
      function postEvent($this, uuid) {
        var bean = {
          uuid: '',
          dutySeqName: '',
          icon: '',
          backgroundColor: '',
          dutySeqCode: '',
          parentUuid: '',
          parentSeqName: '',
          describe: ''
        };
        var html = getPostDialogContent();
        var $postDialog = appModal.dialog({
          message: html,
          title: '职务序列',
          width: 800,
          shown: function () {
            // 选择图标
            $('#addIcon', $postDialog)
              .off()
              .on('click', function () {
                $.WCommonPictureLib.show({
                  selectTypes: [3],
                  confirm: function (data) {
                    var fileIDs = data.fileIDs;
                    $('#icon', $postDialog).val(fileIDs);
                    $('#msgIconShow', $postDialog).attr('iconClass', fileIDs);
                    $('#msgIconShow', $postDialog).attr('class', fileIDs);
                  }
                });
              });

            var items = initBgColor();
            $('.bg-choose-box').append(items);

            // 色块选择
            $('.bg-choose-item', '.icon-bg-wrap')
              .off()
              .on('click', function () {
                var color = $(this).data('color');
                $(this).addClass('hasChoose').siblings().removeClass('hasChoose');
                $('#backgroundColor', $postDialog).val(color);
                $('#selectIconBg', $postDialog).find('div').data('color', color).css('background', color);
                $('#msgIconShow', $postDialog).css({
                  background: color
                });
              });

            // 关闭色块
            $(document).on('click', function (e) {
              if (!$(e.target).hasClass('icon-bg-wrap') && !$(e.target).parents().hasClass('icon-bg-wrap')) {
                $('.bg-choose-list', $postDialog).hide();
                $('.minicolors', $postDialog).hide();
              }
            });

            // 显示色块
            $('#selectIconBg', $postDialog)
              .off()
              .on('click', function () {
                var currColor = $(this).find('div').data('color');
                $('.bg-choose-list', $postDialog).css('display', 'inline-block');
                var chooseItem = $('.bg-choose-list', $postDialog).find('.bg-choose-item');
                chooseItem.removeClass('hasChoose');
                $.each(chooseItem, function (index, item) {
                  if ($(item).data('color') == currColor) {
                    $(item).addClass('hasChoose');
                  }
                });
              });

            // 更多
            $('.bg-choose-more', '.icon-bg-wrap')
              .off()
              .on('click', function () {
                $('.bg-choose-list', $postDialog).hide();
                if ($('.minicolors', $postDialog).size() > 0) {
                  $('.minicolors', $postDialog).show();
                } else {
                  var opacity = false;
                  $('#backgroundColor', $postDialog).minicolors({
                    control: 'hue',
                    format: 'hex',
                    color: '#0070C0',
                    letterCase: 'lowercase',
                    opacity: opacity,
                    position: 'bottom left',
                    theme: 'bootstrap',
                    change: function (value, opacity) {
                      $('#backgroundColor', $postDialog).focus();
                      $('#selectIconBg', $postDialog).find('div').data('color', value).css('background', value);
                      $('#msgIconShow', $postDialog).css({
                        background: value
                      });
                    },
                    hide: function () {
                      $('#backgroundColor', $postDialog).hide();
                      $('.icon-bg-wrap', $postDialog).find('.minicolors').hide();
                    },
                    show: function () {
                      $('#backgroundColor', $postDialog).focus();
                    }
                  });
                  $('#backgroundColor', $postDialog).focus();
                  if (!opacity) {
                    $('.minicolors-input-swatch', $postDialog).hide();
                  }
                }
              });

            var setting = {
              check: {
                radioType: 'level',
                chkStyle: 'radio',
                enable: true
              },
              view: {
                autoCancelSelected: true
              },
              async: {
                otherParam: {
                  serviceName: 'orgDutySeqService',
                  methodName: 'queryDutySeqSelect',
                  data: ['', uuid ? uuid : '']
                }
              },
              callback: {
                onNodeCreated: function (event, treeId, treeNode) {},
                onClick: null,
                beforeClick: function (treeId, treeNode) {}
              }
            };

            $('#parentSeqName')
              .comboTree({
                labelField: 'parentSeqName',
                valueField: 'parentUuid',
                treeSetting: setting,
                width: 220,
                height: 220,
                multiple: true,
                autoInitValue: false,
                autoCheckByValue: true,
                labelBy: 'name',
                showParentCheck: true,
                selectParent: true
              })
              .trigger('change');

            if (uuid) {
              $.ajax({
                type: 'get',
                url: ctx + '/api/org/duty/hierarchy/dutySeqInfo/' + uuid,
                dataType: 'json',
                contentType: 'application/json',
                success: function (res) {
                  bean = res.data;
                  $('#postForm', $postDialog).json2form(bean);
                  $('#parentSeqName', $postDialog).comboTree('initValue', bean.parentUuid).trigger('change');
                  $('#msgIconShow', $postDialog).addClass(bean.icon).css({
                    background: bean.backgroundColor
                  });
                  $('#selectIconBg', $postDialog).find('div').attr('data-color', bean.backgroundColor).css({
                    background: bean.backgroundColor
                  });
                }
              });
            }
          },
          buttons: {
            save: {
              label: '保存',
              className: 'well-btn w-btn-primary',
              callback: function () {
                $('#postForm', $postDialog).form2json(bean);
                if (bean.dutySeqName == '') {
                  appModal.error('职务序列名称不能为空！');
                  return false;
                }
                $.ajax({
                  url: ctx + '/api/org/duty/hierarchy/' + (uuid ? 'dutySeqUpdate' : 'dutySeqSave'),
                  type: 'post',
                  data: JSON.stringify(bean),
                  contentType: 'application/json',
                  success: function (result) {
                    if (result.code === 0) {
                      appModal.success('保存成功');
                      getRankList($this);
                      getPostList($this);
                    } else {
                      appModal.error(result.msg);
                    }
                  },
                  error: function (err) {
                    appModal.error({
                      message: err.responseJSON.msg,
                      timer: 3000
                    });
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
      }

      // 职级弹窗
      function rankEvent($this, uuid) {
        var bean = {
          code: '',
          uuid: '',
          name: '',
          dutySeqUuid: '',
          id: '',
          jobGrade: '',
          jobGradeName: '',
          jobLevel: [],
          jobRank: '',
          describe: ''
        };
        var html = getRankDialogContent();
        var $rankDialog = appModal.dialog({
          message: html,
          title: '职级',
          width: 800,
          size: 'large',
          shown: function () {
            var jobGradeData = [];
            $.each($this.levelList, function (index, item) {
              if (item.isValid == 1) {
                jobGradeData.push({
                  id: item.jobGrade,
                  text: item.jobGradeName || item.jobGrade
                });
              }
            });

            $('#jobGrade', $rankDialog).wellSelect({
              data: jobGradeData,
              valueField: 'jobGrade',
              showEmpty: false
            });

            debugger;
            var setting = {
              check: {
                radioType: 'level',
                chkStyle: 'radio',
                enable: true
              },
              view: {
                autoCancelSelected: true
              },
              async: {
                otherParam: {
                  serviceName: 'orgDutySeqService',
                  methodName: 'queryDutySeqSelect',
                  data: ['', '']
                }
              },
              callback: {
                onNodeCreated: function (event, treeId, treeNode) {},
                onClick: null,
                beforeClick: function (treeId, treeNode) {}
              }
            };

            $('#dutySeqName').comboTree({
              labelField: 'dutySeqName',
              valueField: 'dutySeqUuid',
              treeSetting: setting,
              width: 220,
              height: 220,
              multiple: true,
              autoInitValue: false,
              autoCheckByValue: true
            });

            $('.add-job-level', $rankDialog)
              .off()
              .on('click', function () {
                $(this).before(
                  "<div class='level-item'><input name='isEnable' id='isEnable' class='form-control'/><span class='level-minus'><i class='iconfont icon-ptkj-jianhao'></i></span></div>"
                );
              });

            $rankDialog.off('click', '.level-minus').on('click', '.level-minus', function () {
              // bug#58849
              //if ($('.level-item', $rankDialog).length != 1) {
              $(this).parent().remove();
              //}
            });

            if (uuid) {
              $.ajax({
                url: ctx + '/api/org/multi/getJobRank',
                type: 'get',
                data: {
                  uuid: uuid
                },
                contentType: 'application/json',
                success: function (result) {
                  if (result.code === 0) {
                    bean = result.data;
                    $('#rankForm', $rankDialog).json2form(bean);
                    $('#dutySeqName', $rankDialog).comboTree('initValue', bean.dutySeqUuid).trigger('change');
                    $('#jobGrade', $rankDialog).wellSelect('val', bean.jobGrade.toString());
                    if (bean.jobLevel.length > 0) {
                      var items = '';
                      $.each(bean.jobLevel, function (index, item) {
                        items +=
                          "<div class='level-item'><input class='form-control' value='" +
                          (item == null ? '' : item) +
                          "'/><span class='level-minus'><i class='iconfont icon-ptkj-jianhao'></i></span></div>";
                      });

                      $('.add-job-level', $rankDialog).prev().remove();
                      $('.add-job-level', $rankDialog).before(items);
                    }
                  } else {
                    appModal.error(result.msg);
                  }
                },
                error: function (err) {
                  appModal.error({
                    message: err.responseJSON.msg,
                    timer: 3000
                  });
                }
              });
            }
          },
          buttons: {
            save: {
              label: '保存',
              className: 'well-btn w-btn-primary',
              callback: function () {
                $('#rankForm', $rankDialog).form2json(bean);
                if (bean.jobRank == '') {
                  appModal.error('职级不能为空！');
                  return false;
                }
                if (bean.dutySeqUuid == '') {
                  appModal.error('关联职务序列不能为空！');
                  return false;
                }
                if (bean.jobGrade == '') {
                  appModal.error('对应职等不能为空！');
                  return false;
                }
                bean.jobLevel = [];
                var $item = $('.level-item', $rankDialog);
                var hasEmptyJobLevel = false;
                $.each($item, function (index, item) {
                  var val = $(item).find('input').val();
                  if (val == null || $.trim(val) == '') {
                    hasEmptyJobLevel = true;
                  }
                  bean.jobLevel.push(val);
                });
                if (hasEmptyJobLevel) {
                  appModal.error('职档不能为空！');
                  return false;
                }

                $.ajax({
                  url: ctx + '/api/org/multi/' + (uuid ? 'modifyJobRank' : 'addJobRank'),
                  type: 'post',
                  data: JSON.stringify(bean),
                  contentType: 'application/json',
                  success: function (result) {
                    if (result.code === 0) {
                      appModal.success('保存成功');
                      getRankList($this);
                      getPostList($this);
                    } else {
                      appModal.error(result.msg);
                    }
                  },
                  error: function (err) {
                    appModal.error({
                      message: err.responseJSON.msg,
                      timer: 3000
                    });
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
      }

      // 职等表格
      function initTable($levelDialog, data) {
        $('#dataTable').bootstrapTable('destroy');
        $('#levelTable', $levelDialog).bootstrapTable({
          data: data,
          idField: 'id',
          striped: false,
          showColumns: false,
          sortable: true,
          undefinedText: '',
          columns: [
            {
              field: 'uuid',
              title: 'uuid',
              visible: false
            },
            {
              field: 'jobGrade',
              title: '职等',
              width: 80
            },
            {
              field: 'jobGradeName',
              title: '名称',
              width: 160,
              formatter: function (value, row, index) {
                var html = "<input class='form-control' value='" + (value || '') + "'/>";
                return html;
              }
            },

            {
              field: 'describe',
              title: '描述',
              formatter: function (value, row, index) {
                var html = "<input class='form-control' value='" + (value || '') + "'/>";
                return html;
              }
            }
          ]
        });
      }

      // 职务序列列表
      function getPostList($this, val) {
        $.ajax({
          url: ctx + '/api/org/duty/hierarchy/dutySeqTree?keyword=' + (val || ''),
          type: 'get',
          dataType: 'json',
          success: function (result) {
            if (result.code === 0) {
              $this.postList = result.data;
              var html = initPostList(result.data, 'Q', val);
              $('#postList').html(html);
              if (html != '') {
                $('#postList').prev().find('.arrow').removeClass('icon-ptkj-shixinjiantou-you').addClass('icon-ptkj-shixinjiantou-xia');
                if ($('.rank-cate-header-left .arrow', '.rank-tab-content1').hasClass('icon-ptkj-shixinjiantou-you')) {
                  $('.rank-cate-header-left .arrow', '.rank-tab-content1').trigger('click');
                }
              }
              changeListLiWidth($('#postList'));
            }
          }
        });
      }

      function changeListLiWidth($container) {
        var maxWidth = 220;
        $container.find('.post-name').each(function (i) {
          var $postName = $(this);
          var width = $postName.width();
          var layPadding = 0;
          var $lay = $postName.closest('.lay');
          var paddingLeft = $lay.css('padding-left');
          if (paddingLeft != null && paddingLeft.indexOf('px') != -1) {
            layPadding = parseInt(paddingLeft.substring(0, paddingLeft.length - 2));
          }
          width = width + layPadding + 30;
          if (width > maxWidth) {
            maxWidth = width;
          }
        });
        $container.find('li').width(maxWidth);
      }

      // 职等列表
      function getLevelList($this) {
        $.ajax({
          url: ctx + '/api/org/duty/hierarchy/jobGradeList',
          type: 'get',
          dataType: 'json',
          success: function (result) {
            if (result.code === 0) {
              $this.levelList = result.data;
              var html = '';
              $.each(result.data, function (index, item) {
                if (item.isValid == 1) {
                  html +=
                    '<div class="rank-item" title="' +
                    (item.jobGradeName || item.jobGrade) +
                    '">' +
                    (item.jobGradeName || item.jobGrade) +
                    '</div>';
                }
              });
              $('#levelList', $element).html(html);
              changeLevelListRankItemWidth($('#levelList', $element));
            }
          }
        });
      }

      function isChinese(value) {
        return /^[\u0391-\uFFE5]+$/.test(value);
      }

      function changeLevelListRankItemWidth($container) {
        var minWidth = 160;
        var maxWidth = 0;
        $container.find('.rank-item').each(function (i) {
          var itemText = $(this).text();
          if (itemText != null && itemText.length * 30 > maxWidth) {
            for (var i = 0; i < itemText.length; i++) {
              var c = itemText.charAt(i);
              if (isChinese(c)) {
                maxWidth += 14;
              } else {
                maxWidth += 7.5;
              }
            }
          }
        });
        if (maxWidth < minWidth) {
          maxWidth = minWidth;
        }
        $container.find('.rank-item').width(maxWidth);
      }

      // 职级列表
      function getRankList($this, val) {
        $.ajax({
          url: ctx + '/api/org/duty/hierarchy/jobRankTree?keyword=' + (val || ''),
          type: 'get',
          dataType: 'json',
          success: function (result) {
            if (result.code === 0) {
              $this.rankList = result.data || [];
              var html = initPostList($this.rankList, 'R', val);
              $('#rankList').html(html);
              if (html != '') {
                $('#rankList').prev().find('.arrow').removeClass('icon-ptkj-shixinjiantou-you').addClass('icon-ptkj-shixinjiantou-xia');
                if ($('.rank-cate-header-left .arrow', '.rank-tab-content3').hasClass('icon-ptkj-shixinjiantou-you')) {
                  $('.rank-cate-header-left .arrow', '.rank-tab-content3').trigger('click');
                }
              }
              changeListLiWidth($('#rankList'));
            }
          }
        });
      }

      // 职务序列列表树
      // val用于搜素，搜素时展开全部，不搜索时展开第一级
      function initPostList(datas, type, val) {
        var html = '';
        for (var i = 0; i < datas.length; i++) {
          var data = datas[i];
          var iconfont = data.icon ? data.icon : 'iconfont icon-ptkj-chakanbiaodanxiangqing';
          var background = data.backgroundBg ? data.backgroundBg : data.backgroundColor ? data.backgroundColor : '#64B3EA';
          var title = data.describe ? "title='" + data.describe + "'" : '';
          html +=
            "<li><div class='lay lay" +
            data.level +
            "' " +
            title +
            " data-uuid='" +
            data.uuid +
            "' data-id='" +
            data.id +
            "' data-type='" +
            data.childrenType +
            "'>";
          if (data.childrens.length > 0) {
            var folderIcon = val && val != '' ? 'icon-ptkj-shixinjiantou-xia' : 'icon-ptkj-shixinjiantou-you';
            html += "<i class='folder iconfont " + folderIcon + "'></i>";
          }
          if (data.childrenType == 'Q') {
            html += "<span class='icon-style' style='background:" + background + "'><i class='" + iconfont + "'></i></span>";
          }

          var name =
            val && val != ''
              ? data.dutySeqName.indexOf(val) > -1
                ? data.dutySeqName.replace(new RegExp(val, 'g'), "<span style='color:#f00;'>" + val + '</span>')
                : data.dutySeqName
              : data.dutySeqName;

          html += "<span class='post-name'>" + name + '</span>';
          if (data.childrenType == 'Q' && data.dutySeqCode) {
            var dutySeqCode =
              val && val != ''
                ? data.dutySeqCode.indexOf(val) > -1
                  ? data.dutySeqCode.replace(new RegExp(val, 'g'), "<span style='color:#f00;'>" + val + '</span>')
                  : data.dutySeqCode
                : data.dutySeqCode;
            html += "<span class='post-code'>&nbsp;&nbsp;-&nbsp;&nbsp;" + dutySeqCode + '</span>';
          }

          if (type == data.childrenType) {
            html +=
              "<div class='lay-operate'>" +
              "<span class='lay-operate-icon'><i class='iconfont icon-ptkj-gengduocaozuo'></i></span>" +
              "<ul class='lay-operate-btns'>" +
              "<li class='btn-edit'><i class='iconfont icon-ptkj-bianji'></i>编辑</li>" +
              "<li class='btn-delete'><i class='iconfont icon-ptkj-shanchu'></i>删除</li>" +
              '</ul>' +
              '</div>';
          }

          html += '</div>';

          if (data.childrens.length > 0) {
            var display = val && val != '' ? '' : 'style="display:none;"';
            html += '<ul ' + display + '>';
            html += initPostList(data.childrens, type, val);
            html += '</ul>';
          }
          html += '</li>';
        }
        return html;
      }

      // 职务序列弹窗背景颜色
      function initBgColor() {
        var colors = [
          '#64B3EA',
          '#FCB862',
          '#92D678',
          '#F38F8A',
          '#9584EE',
          '#44C8E5',
          '#E28DE9',
          '#65DEAA',
          '#FE88A7',
          '#77A8EE',
          '#D9D688',
          '#F6ABBB'
        ];
        var items = '';
        // 渲染背景颜色
        $.each(colors, function (index, item) {
          items +=
            '<li class="bg-choose-item" data-color="' +
            item +
            '" style="background: ' +
            item +
            '"><i class="iconfont icon-ptkj-dagou"></i></li>';
        });

        return items;
      }

      // 职务序列弹窗html
      function getPostDialogContent() {
        var html = '';
        html +=
          "<form id='postForm' class='dyform'>" +
          "<table class='well-form form-horizontal'>" +
          "<tr class='field'>" +
          "<td class='label-td' style='width:20%'><font style='color:#f00;'>*</font>名称</td>" +
          "<td><input type='text' name='dutySeqName' id='dutySeqName' class='form-control' placeholder='名称'/></td>" +
          '</tr>' +
          "<tr class='field'>" +
          "<td class='label-td' style='width:20%'>图标</td>" +
          '<td>' +
          "<input type='hidden' name='icon' id='icon'/>" +
          "<span id='msgIconShow' style='margin-right: 5px;' style='background: transparent'></span>" +
          "<button id='addIcon' type='button' class='well-btn w-btn-primary' style='vertical-align: top;'>选择图标</button>" +
          "<div class='icon-bg-wrap'>" +
          "<div class='currBg' id='selectIconBg' data-color=''><div></div></div>" +
          "<div class='bg-choose-list'>" +
          "<ul class='bg-choose-box'></ul>" +
          "<div class='bg-choose-more'>更多<i></i></div>" +
          '</div>' +
          "<input type='hidden' name='backgroundColor' id='backgroundColor'/>" +
          '</div>' +
          '</td>' +
          '</tr>' +
          "<tr class='field'>" +
          "<td class='label-td' style='width:20%'>编号</td>" +
          "<td><input type='text' name='dutySeqCode' id='dutySeqCode' class='form-control' placeholder='编号'/></td>" +
          '</tr>' +
          "<tr class='field'>" +
          "<td class='label-td' style='width:20%'>父级序列</td>" +
          '<td>' +
          "<input name='parentSeqName' id='parentSeqName' class='form-control' placeholder='请选择'/>" +
          "<input name='parentUuid' id='parentUuid' type='hidden'/>" +
          '</td>' +
          '</tr>' +
          "<tr class='field'>" +
          "<td class='label-td' style='width:20%'>描述</td>" +
          "<td><textarea name='describe' id='describe' cols='30' rows='5' class='form-control'></textarea></td>" +
          '</tr>' +
          '</table>' +
          '</form>';

        return html;
      }

      // 职级弹窗html
      function getRankDialogContent() {
        var html = '';
        html +=
          "<form id='rankForm' class='dyform'>" +
          "<input id='id' type='hidden'/>" +
          "<table class='well-form form-horizontal'>" +
          "<tr class='field'>" +
          "<td class='label-td' style='width:20%'><font style='color:#f00;'>*</font>职级</td>" +
          "<td><input type='text' name='jobRank' id='jobRank' class='form-control' placeholder='职级'/></td>" +
          '</tr>' +
          "<tr class='field'>" +
          "<td class='label-td' style='width:20%'>职称</td>" +
          "<td><input type='text' name='name' id='name' class='form-control' placeholder='职称'/></td>" +
          '</tr>' +
          "<tr class='field'>" +
          "<td class='label-td' style='width:20%'>编号</td>" +
          "<td><input type='text' name='code' id='code' class='form-control' placeholder='编号'/></td>" +
          '</tr>' +
          "<tr class='field'>" +
          "<td class='label-td' style='width:20%'><font style='color:#f00;'>*</font>关联职务序列</td>" +
          '<td>' +
          "<input name='dutySeqName' id='dutySeqName' class='form-control' placeholder='请选择'/>" +
          "<input name='dutySeqUuid' id='dutySeqUuid' type='hidden' placeholder='请选择'/>" +
          '</td>' +
          '</tr>' +
          "<tr class='field'>" +
          "<td class='label-td' style='width:20%'><font style='color:#f00;'>*</font>对应职等</td>" +
          "<td><input name='jobGrade' id='jobGrade' class='form-control' placeholder='请选择'/></td>" +
          '</tr>' +
          "<tr class='field'>" +
          "<td class='label-td' style='width:20%'>描述</td>" +
          "<td><textarea name='describe' id='describe' cols='30' rows='5' class='form-control'></textarea></td>" +
          '</tr>' +
          "<tr class='field'>" +
          "<td class='label-td' style='width:20%'>职级包含职档 " +
          "<div class='w-tooltip'>" +
          "<i class='iconfont icon-ptkj-tishishuoming'></i>" +
          "<div class='w-tooltip-content'>可以为职级添加细化的档位，例如职档A、职档B</div>" +
          '</div>' +
          '</td>' +
          '<td>' +
          // "<div class='level-item'><input class='form-control'/><span class='level-minus'><i class='iconfont icon-ptkj-jianhao'></i></span></div>" +
          "<button type='button' class='well-btn w-btn-primary w-noLine-btn add-job-level'><i class='iconfont icon-ptkj-jiahao'></i>添加</button>" +
          '</td>' +
          '</tr>' +
          '</table>' +
          '</form>';

        return html;
      }

      // 职等弹窗html
      function getLevelDialogContent() {
        var html = '';
        html +=
          '<div class="dyform" id="levelForm">' +
          '<div class="level-header">' +
          '<div class="level-header-title">职等范围：</div>' +
          '<div class="level-header-content">' +
          '<div class="level-num-wrap">' +
          '<input class="level-num-input" id="num1"/>' +
          '<div class="level-num-arrows">' +
          '<span class="num-arrows-up"><i class="iconfont icon-ptkj-xianmiaojiantou-shang"></i></span>' +
          '<span class="num-arrows-down"><i class="iconfont icon-ptkj-xianmiaojiantou-xia"></i></span></div>' +
          '</div>' +
          '-' +
          '<div class="level-num-wrap">' +
          '<input  class="level-num-input" id="num2"/>' +
          '<div class="level-num-arrows">' +
          '<span class="num-arrows-up"><i class="iconfont icon-ptkj-xianmiaojiantou-shang"></i></span>' +
          '<span class="num-arrows-down"><i class="iconfont icon-ptkj-xianmiaojiantou-xia"></i></span>' +
          '</div>' +
          '</div>' +
          '</div>' +
          '<div class="level-header-tip">(请从高至低设置职等范围，不可重叠，例如 12 ~ 1)</div>' +
          '</div>' +
          '<div class="level-table"><table id="levelTable"></table></div>' +
          '</div>';
        return html;
      }
    },
    refresh: function () {
      this.init();
    }
  });
  return AppOrgRankListTabDevelopment;
});
