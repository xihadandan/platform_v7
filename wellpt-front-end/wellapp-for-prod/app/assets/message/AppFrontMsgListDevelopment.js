define(['jquery', 'commons', 'constant', 'server', 'appModal', 'multiOrg', 'PanelWidgetDevelopment'], function (
  $,
  commons,
  constant,
  server,
  appModal,
  multiOrg,
  PanelWidgetDevelopment
) {
  var JDS = server.JDS;
  var AppFrontMsgListDevelopment = function () {
    PanelWidgetDevelopment.apply(this, arguments);
  };

  commons.inherit(AppFrontMsgListDevelopment, PanelWidgetDevelopment, {
    beforeRender: function () {
      var iframes = top.$('iframe');
      var src = '';
      $.each(iframes, function (index, item) {
        if ($(item).attr('src') && $(item).attr('src').indexOf('myMsg') > -1) {
          src = $(item).attr('src');
        }
      });
      if (src.indexOf('246174de446c31abe74fb2285e1ffcd9') > -1) {
        console.log(this);
        this.widget.options.widgetDefinition.configuration.body.tabs.pop();
      }
    },
    init: function () {
      var height = ($(window).height() - 70) * 0.94;
      $(window.top.document)
        .find('.modal')
        .not('.frontLoginMsgDialog')
        .find('.modal-body')
        .css({
          padding: 0,
          height: height + 'px'
        });

      $('.wellpt-msg-wrapper').parents('.web-app-container.container-fluid').first().css({
        background: '#fff',
        paddingBottom: 0
      });
      $('.wellpt-msg-wrapper').parents('body').css({
        overflow: 'hidden'
      });
      $(window.top.document)
        .find('.embed-responsive-4by3')
        .css({
          paddingBottom: height + 'px'
        });
      $(window.top.document).find('.bootbox-close-button').wrap("<div class='bootbox-close-wrap'></div>").css({
        position: 'absolute',
        right: '15px',
        top: '7px',
        fontSize: 0,
        lineHeight: 1,
        height: 'auto',
        zIndex: 10,
        marginTop: 0
      });

      $('.wellpt-msg-wrapper > .panel-body > .panel-tab-header').css({
        height: height + 2 + 'px'
      });

      $('.pt-message').css({
        overflow: 'hidden'
      });

      var $element = this.widget.element;
      $element.parents('.ui-wPage').first().css({
        overflow: 'hidden'
      });
    }
  });

  return AppFrontMsgListDevelopment;
});
