define(['jquery', 'server', 'commons', 'constant', 'appContext', 'appModal', 'ProcessViewer'], function (
  $,
  server,
  commons,
  constant,
  appContext,
  appModal,
  ProcessViewer
) {
  var StringUtils = commons.StringUtils;
  // 办理过程
  var WorkFlowCustomerWorkProcessViewer = function (workView, options) {
    ProcessViewer.call(this, workView, options);
  };
  commons.inherit(WorkFlowCustomerWorkProcessViewer, ProcessViewer, {
    init: function () {
      var _self = this;
      var options = _self.options;
      // 配置隐藏按钮或草稿件，隐藏按钮
      if (options.clickButtonToShow === false || this.workView.isDraft()) {
      } else {
        _self.show();
      }
    },
    flowIndex: -1,
    currNum: 0,
    hasShow: false,
    show: function () {
      var _self = this;
      if (_self.loaded === true) {
        return;
      }

      var workData = _self.workView.getWorkData();
      var flowInstUuid = workData.flowInstUuid;
      appModal.showMask();
      $.get({
        url: ctx + '/api/workflow/work/getWorkProcessAndOpinionPositionConfigs',
        data: {
          flowInstUuid: flowInstUuid
        },
        async: true,
        success: function (result) {
          appModal.showMask();
          _self.opinionPositionConfig = result.data.opinionPositionConfig;
          var data = result.data.workProcesses;
          var newData = _self.getData(data); // 拆分数据
          var content = _self.initPage(newData); // 页面布局
          $('.work-process-tab-pane')[0].innerHTML = '';
          $('.work-process-tab-pane')[0].innerHTML = content;

          $('#wf_work_process_content .timeline-item').each(function (i) {
            if ($(this).height() > 400) {
              $(this).addClass('max400');
              $(this).find('.read-more').show();
            }
          });
          $('#wf_work_process_content .timeline-item img').each(function (i) {
            $(this).on('error', function () {
              $(this).hide().next('i').show();
            });
          });

          // $("#wf_work_process_content .tab-content").slimScroll({
          //     "height" : $("#wf_work_process_content .tab-content").height(),
          //     wheelStep: 10
          // });
          if ($('#rightMenuTabContainer').find('.timeline-now').size() > 0) {
            var height = $('#rightMenuTabContainer').find('.timeline-now').offset().top;
          } else if ($('#rightMenuTabContainer').find('.isOver').size() > 0) {
            var height = $('#rightMenuTabContainer').find('.isOver').offset().top;
          } else {
            var height = $('#rightMenuTabContainer').find('.notOver').offset().top;
          }
          if (!_self.hasShow) {
            $('#rightMenuTabContainer .work-process-viewer .tab-content').animate({
              scrollTop: height
            });
            _self.hasShow = true;
          }
          appModal.hideMask();
        }
      });
      _self._getWorkFlowLink();

      $('#rightMenuTabContainer')
        .off('click', '.read-more')
        .on('click', '.read-more', function () {
          // 查阅更多
          _self.currNum++;
          _self.flowIndex = $('.timeline-item').index($(this).parents('.timeline-item'));
          $(this).parents('.timeline-item').siblings().find('.liucheng-time').removeClass('liuchengFixed').css({
            width: 'initial'
          });
          $(this).hide().next().show();
          $(this).parents('.timeline-item').removeClass('max400').data('open', true);
        });

      $('#rightMenuTabContainer')
        .off('click', '.read-pack')
        .on('click', '.read-pack', function () {
          // 收起
          _self.currNum--;
          $(this).hide().prev().show();
          $(this).parents('.timeline-item').addClass('max400').data('open', false);
          _self.scrollToTop();
        });

      $('#rightMenuTabContainer .work-process-viewer .tab-content')
        .off('scroll')
        .on('scroll', function () {
          if (_self.currNum <= 0) {
            return false;
          }

          var timeLine = $('.inspinia-timeline').find('.timeline-item');
          timeLine.each(function (i) {
            var top = $(this).offset().top;
            var height = $(this).height();
            var parentH = $(this).parents('.tab-content').height();

            if (top < 120 && top + height > parentH + 100 && height > parentH) {
              // var width = $(this).find(".liucheng-time").parent().width()
              $(this)
                .find('.liucheng-time')
                .addClass('liuchengFixed')
                .css({
                  width: 420 + 'px'
                });
            } else {
              $(this).find('.liucheng-time').removeClass('liuchengFixed').css({
                width: 'initial'
              });
            }
          });
        });
    },
    getHeight: function () {
      // 获取当前展开环节到顶部的距离
      var height = 0;
      var ele = $('.inspinia-timeline').find('.timeline-item');
      var self = this;
      ele.each(function (index) {
        if (index < self.flowIndex) {
          height += $(this).height();
        }
      });
      return height;
    },
    scrollToTop: function (callback) {
      // 当前环节置顶
      var height = this.getHeight();
      $('#rightMenuTabContainer .work-process-viewer .tab-content').animate(
        {
          scrollTop: height - 15
        },
        function () {
          callback && callback();
        }
      );
    },
    initPage: function (data) {
      var _self = this;
      var isOver = this.workView.isOver() ? 'isOver' : 'notOver';
      var html = '<div class="ibox-content inspinia-timeline">';
      for (var i = 0; i < data.length; i++) {
        var paddingbs = i == data.length - 1 ? '80' : '30';
        var now = data[i].taskStatus == '未完成' ? 'timeline-now' : '';
        html +=
          '<div class="timeline-item" data-index="' +
          i +
          '" data-open="' +
          false +
          '">' +
          '<div class="liucheng-time ' +
          now +
          '"><div>' +
          '<strong>' +
          data[i].taskName +
          '</strong>' +
          '<span class="pull-right">' +
          '<span>' +
          data[i].arriveTime +
          '</span>' +
          '<span class="timeline-label">到达</span>' +
          '</span></div>' +
          '</div>';
        var detaile = data[i].handleDetail;
        html += '<div class="liucheng-content" style="padding-bottom:' + paddingbs + 'px;">';
        var opinionIndex = _.findIndex(_self.opinionPositionConfig, function (o) {
          return o.taskId == data[i].taskId;
        });
        for (var j = 0; j < detaile.length; j++) {
          html += "<div class='liucheng-content-wrap'>";
          if (detaile[j].status == '未完成') {
            var assignee = detaile[j].assignee.replace(/;/g, '，');
            html +=
              '<div class="liucheng-user">' +
              '<div class="liucheng-user-avatar">' +
              '<img src="' +
              ctx +
              '/personalinfo/userImg?id=' +
              detaile[j].assigneeId +
              '" />' +
              '<i class="iconfont icon-ptkj-morentouxiang" style="display: none"></i>' +
              '</div>' +
              '<div class="liucheng-user-name" title="' +
              assignee +
              '">' +
              assignee +
              '</div>' +
              '</div>';
          } else {
            var opinion = detaile[j].opinion || '未填写办理意见';
            var defalutColor = detaile[j].opinion == null ? 'no-opinions' : '';
            var actionName = detaile[j].actionName == null ? '提交' : detaile[j].actionName;

            if (_self.opinionPositionConfig[opinionIndex].showUserOpinionPosition && detaile[j].opinionLabel != null) {
              //todo立场意见
              html +=
                '<div class="sign-yinzhang iconfont icon-ptkj-yinzhang" style="color:' +
                defalutColor +
                '"><div class="sing-yinzhang-text">' +
                detaile[j].opinionLabel +
                '</div></div>';
            }
            var liuchengUserJob = '';
            if (detaile[j].deptName && detaile[j].mainJobName) {
              liuchengUserJob = '(' + detaile[j].deptName + '&nbsp;&nbsp;&nbsp;' + detaile[j].mainJobName + ')';
            } else if (detaile[j].deptName || detaile[j].mainJobName) {
              liuchengUserJob = '(';
              liuchengUserJob += detaile[j].deptName || detaile[j].mainJobName;
              liuchengUserJob += ')';
            }
            html +=
              '<div class="liucheng-user">' +
              '<div class="liucheng-user-avatar">' +
              '<img src="' +
              ctx +
              '/personalinfo/userImg?id=' +
              detaile[j].assigneeId +
              '"/>' +
              '<i class="iconfont icon-ptkj-morentouxiang" style="display: none"></i>' +
              '</div>' +
              '<div class="liucheng-user-name" title="' +
              detaile[j].assignee +
              '">' +
              detaile[j].assignee +
              '</div>' +
              '<div class="liucheng-user-job">' +
              liuchengUserJob +
              '</div>' +
              '</div>' +
              '<div class="liucheng-opinion-content">' +
              '<p class="liucheng-opinion-text ' +
              defalutColor +
              '">' +
              opinion +
              '</p>' +
              '<p>' +
              detaile[j].endTime +
              '<span>' +
              actionName +
              '</span></p>' +
              '</div>';
          }
          html += '</div>';
        }

        if (_self.opinionPositionConfig[opinionIndex].showOpinionPositionStatistics) {
          var opinionPositionStatistics = _self.getOpinionPositionStatistics(detaile);
          if (opinionPositionStatistics.length > 0) {
            //todo开启意见立场且离开环节,才显示立场统计
            html += '<div class="sign-yinzhang_statistics"><div class="sign-yinzhang_statistics_box">';
            for (var k = 0; k < opinionPositionStatistics.length; k++) {
              html +=
                '<div class="sign-yinzhang_statistics_item"><span class="text">' +
                opinionPositionStatistics[k].opinionLabel +
                '：</span><span class="num">' +
                opinionPositionStatistics[k].count +
                '</span><span class="unit">人</span></div>';
            }
            html += '</div></div>';
          }
        }

        html += '<div class="read-more">查阅更多<i class="iconfont icon-ptkj-xianmiaoshuangjiantou-xia"></i></div>';
        html += '<div class="read-pack">收起<i class="iconfont icon-ptkj-xianmiaoshuangjiantou-shang"></i></div>';
        html += '</div>';
        html += '</div>';
      }
      html +=
        '<div class="timeline-item"><div class="liucheng-time liucheng-time-end ' +
        isOver +
        '"><div><strong>结束</strong><div class="not-over-dott"></div></div>';
      return html;
    },
    getOpinionPositionStatistics: function (detaile) {
      var opinionPositionStatistics = [];
      for (var i = 0; i < detaile.length; i++) {
        if (detaile[i].status == '未完成') {
          opinionPositionStatistics = [];
          break;
        }
        if (detaile[i].opinionValue != null) {
          var newIndex = _.findIndex(opinionPositionStatistics, function (o) {
            return o.opinionValue == detaile[i].opinionValue;
          });
          if (newIndex == -1) {
            opinionPositionStatistics.push({
              opinionValue: detaile[i].opinionValue,
              opinionLabel: detaile[i].opinionLabel,
              count: 1
            });
          } else {
            opinionPositionStatistics[newIndex].count += 1;
          }
        }
      }
      return opinionPositionStatistics;
    },
    getData: function (data) {
      var index = 0;
      var newData = new Array();
      var first = data.shift();
      newData.push({
        taskName: first.taskName,
        arriveTime: first.submitTime,
        taskStatus: first.status,
        handleDetail: [first],
        taskId: first.taskId
      });

      while (data.length >= 1) {
        var item = data.shift();
        if (item.taskName == newData[index].taskName) {
          newData[index].taskStatus = item.status;
          newData[index].handleDetail.push(item);
        } else {
          index++;
          newData.push({
            taskName: item.taskName,
            arriveTime: item.submitTime,
            taskStatus: item.status,
            handleDetail: [item],
            taskId: item.taskId
          });
        }
      }
      return newData;
    },
    _getWorkFlowLink: function () {
      var _self = this;
      var workData = _self.workView.getWorkData();
      var taskInstUuid = workData.taskInstUuid;
      var flowDef = workData.flowDefId;

      $.ajax({
        dataType: 'json',
        type: 'get',
        contentType: 'application/json',
        url: contextPath + '/api/workflow/work/getTaskProcess',
        data: {
          taskInstUuid: taskInstUuid,
          flowDefId: flowDef
        },
        sync: false,
        mask: false,
        success: function (result) {
          $('#wf_work_process_content .work-flow-link').remove();
          var flowLink = result.data;
          var html = "<div class='work-flow-link clearfix'>";
          if (flowLink.previous) {
            html +=
              "<div class='work-flow-link-item'>" +
              "<div class='work-flow-link-content'>" +
              flowLink.previous.taskName +
              '</div>' +
              "<div class='work-flow-link-member mPrevious'>" +
              flowLink.previous.taskName +
              "<br><span class='doLabel'>办理人：</span><span class='doUser'>" +
              flowLink.previous.assignee.split(';').join('、') +
              '</span></div>' +
              '</div>';
          }
          if (flowLink.current) {
            html +=
              "<div class='work-flow-link-item work-flow-link-current'>" +
              "<div class='work-flow-link-content'>" +
              flowLink.current.taskName +
              '</div>';

            if (flowLink.current.taskName != '结束') {
              var curClassName = flowLink.previous ? 'mCurrent' : 'mPrevious';
              html += "<div class='work-flow-link-member " + curClassName + "'>" + flowLink.current.taskName + '<br>';
              if (flowLink.current.todoUserNames && flowLink.current.todoUserNames.length > 0) {
                html +=
                  "<div><span class='doLabel'>待办办理人：</span><span class='work-flow-link-members doUser'>" +
                  flowLink.current.todoUserNames.join('、') +
                  '</span></div>';
              }
              if (flowLink.current.doneUserNames && flowLink.current.doneUserNames.length > 0) {
                html +=
                  "<div><span class='doLabel'>已办办理人：</span><span class='work-flow-link-members doUser'>" +
                  flowLink.current.doneUserNames.join('、') +
                  '</span></div>';
              }
            }

            html += '</div>' + '</div>';
          }
          if (flowLink.next) {
            html += "<div class='work-flow-link-item'>" + "<div class='work-flow-link-content'>" + flowLink.next.taskName + '</div>';

            if (flowLink.next.taskName != '结束') {
              var nextClassName = flowLink.previous ? 'mNext' : 'mCurrent';
              html +=
                "<div class='work-flow-link-member " +
                nextClassName +
                "'>" +
                flowLink.next.taskName +
                '<br>' +
                "<span class='doLabel'>办理人：</span><span class='doUser'>" +
                flowLink.next.assignee.split(';').join('、') +
                '</span></div>';
            }
            html += '</div>';
          }
          html += '</div>';
          $('#wf_work_process_content').prepend(html);
        }
      });
    }
  });

  return WorkFlowCustomerWorkProcessViewer;
});
