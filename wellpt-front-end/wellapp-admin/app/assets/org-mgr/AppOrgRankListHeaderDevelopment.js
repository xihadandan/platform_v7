define(['constant', 'commons', 'server', 'appContext', 'appModal', 'HtmlWidgetDevelopment', 'AppOrgRankListViewDevelopment'], function (
  constant,
  commons,
  server,
  appContext,
  appModal,
  HtmlWidgetDevelopment,
  viewDevelopment
) {
  var JDS = server.JDS;

  var AppOrgRankListHeaderDevelopment = function () {
    HtmlWidgetDevelopment.apply(this, arguments);
  };
  commons.inherit(AppOrgRankListHeaderDevelopment, HtmlWidgetDevelopment, {
    init: function () {
      var $element = this.widget.element;
      toggleSwitch();
      $('.switch-wrap', $element)
        .off()
        .on('click', function () {
          var isEnable = $(this).hasClass('active');
          $(this)[isEnable ? 'removeClass' : 'addClass']('active');

          toggleSwitch(isEnable ? 0 : 1);
        });

      $('.rank-mgr-header-right', $element)
        .off('click', '.iconfont')
        .on('click', '.iconfont', function () {
          $(this).addClass('active').siblings().removeClass('active');
          var id1 = $(this).data('id');
          var id2 = $(this).siblings().data('id');
          if (id1 == 2) {
            viewDevelopment._refresh();
          }
          $('.rank-view' + id1, $element.parent()).show();
          $('.rank-view' + id2, $element.parent()).hide();
        });

      function toggleSwitch(isEnable) {
        $.ajax({
          type: 'get',
          url: ctx + '/api/org/duty/hierarchy/switch',
          dataType: 'json',
          data: {
            isEnable: isEnable
          },
          success: function (res) {
            if (res.code == 0) {
              if (res.data == '0') {
                $('.switch-wrap', $element).removeClass('active');
              }
            }
          }
        });
      }
      var html = "<span title='帮助' class='rank-help iconfont icon-ptkj-tishishuoming'></span>";
      $element.parents('.panel-body').first().prev().find('.title-text').after(html);

      $('.rank-help')
        .off('click')
        .on('click', function () {
          var message = getMessage();
          appModal.dialog({
            message: message,
            title: '职务体系说明',
            size: 'large',
            buttons: {
              close: {
                className: 'btn btn-default',
                label: '关闭'
              }
            }
          });
        });

      function getMessage() {
        var messageHtml = '';
        messageHtml +=
          "<div class='rank-help-info'>" +
          "<div class='name'>名词解释</div>" +
          '<p>职务、职位体系是指单位内部所有不同领域的职位按照所属关系和等级关系，对职位进行纵向分类和横向分层，形成的职务组合体系。合理的职务体系可以为建立人力资源系统化层级结构、为职级的宽带化管理和员工的职业发展提供依据。</p>' +
          "<div class='title'>职务序列</div>" +
          '<p>从一个组织整体以及职能规划的角度出发，对组织内的职务性质和任职要求等相近的职务进行分类和管理，例如企业中职务通常可分为管理序列（M）、专业序列（P）、技术序列（T），政府机关中可分为领导序列、非领导序列（非领导职务）。</p>' +
          "<div class='title'>职务</div>" +
          '<p>组织内具有相近或相同职权、职责的一系列职位的集合，例如：部门经理。</p>' +
          "<div class='title'>职等</div>" +
          '<p>从一个组织整体的维度，针对员工承担的责任、掌握知识经验和专业技能的程度差异，进行等级划分。例如政府机关中公务员从1至27划分27级；企业中按职能规划，职等从高至低等次不一，如12级至1级。</p>' +
          "<div class='title'>职级</div>" +
          '<p>从职务序列的维度，以及某个职务所从事业务的广度和深度，同时对照职等，对职务进行级别划分。例如职务“部门经理”，在管理序列中职级为M6，对应到组织整体的职等为6级。</p>' +
          "<div class='title'>职档</div>" +
          '<p>对职级的进一步细化，例如职级M6可细分为 M6A、M6B、M6C三档。</p>' +
          "<div class='title'>职位</div>" +
          '<p> 职位是组织中特定的时间内，由一个或多个人员所承担的一个或多个工作任务的组合，是职务附加工作内容后的一个实例。例如，职务 部门经理，职位可分为 销售部经理、开发部经理。</p>' +
          "<div class='name'>功能说明</div>" +
          "<div class='title'>职务体系的启用</div>" +
          '<p>默认开启职务体系，组织架构中添加职位时需要先梳理职务体系，创建职务后根据职务创建职位。</p>' +
          "<div class='title'>职务体系的构建</div>" +
          '<p> 职务序列、职等、职级是职务的前置信息，先通过职务序列构建纵向的职务分类，再通过职等和职级构建横向的职务层级，最后按照单位、部门职能创建职务。</p>' +
          '</div>';
        return messageHtml;
      }

      // 查看体系视图
      if ($element.hasClass('onlyshow-rank-view2')) {
        $('.rank-mgr-header', $element).hide();
		setTimeout(function() {
        	$("span[data-id='2']", $element).trigger('click');
		}, 100);
      }
    },
    refresh: function () {
      this.init();
    }
  });
  return AppOrgRankListHeaderDevelopment;
});
