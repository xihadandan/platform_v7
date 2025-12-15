define(['constant', 'commons', 'server', 'ListViewWidgetDevelopment'], function (constant, commons, server, ListViewWidgetDevelopment) {
  // 平台应用_基础数据管理_计时服务用于流程_列表二开
  var AppWorkTimeServiceListDevelopment = function () {
    ListViewWidgetDevelopment.apply(this, arguments);
  };
  commons.inherit(AppWorkTimeServiceListDevelopment, ListViewWidgetDevelopment, {
    init: function () {
      var uuid = GetRequestParam().timerConfigUuidValue || this.getWidgetParams().timerConfigUuidValue;
    },
    afterRender: function () {
      var $element = this.widget.element;
      parent.$('iframe.embed-responsive-item').parent().css({
        paddingBottom: '52%'
      });
      $element.parents('.container-fluid').css({
        background: '#fff'
      });
      $element.parents('.ui-wPanel').css({
        'margin-bottom': 0
      });
    },
    onLoadSuccess: function (data) {
      var $element = this.widget.element;
      var id = $element.attr('id');
      var uuid = GetRequestParam().timerConfigUuidValue || this.getWidgetParams().timerConfigUuidValue;

      $.each(data.rows, function (index, item) {
        $('#' + id + '_table', $element)
          .find('tbody tr:eq(' + index + ')')
          .attr('data-obj', JSON.stringify(item));
        // 设置选中
        if (uuid && uuid == item.uuid) {
          $('#' + id + '_table', $element)
            .find('tbody tr:eq(' + index + ')')
            .find('td input')
            .prop('checked', true);
        }
      });

      $('#' + id + '_table', $element)
        .find('tbody')
        .find('tr')
        .css({
          cursor: 'pointer'
        });
    },
    onClickRow: function (rowNum, row, $element, field) {
      var url =
        ctx +
        '/web/app/page/preview/1526fca2143e81de76c3e0b7569fbd3f?pageUuid=6606477d-e115-4452-83b0-b5dc655d77dd&uuid=' +
        row.uuid +
        '&from=workflow&editable=false';
      var iframe =
        '<div class="embed-responsive embed-responsive-4by3"><iframe src="' +
        url +
        '" class="embed-responsive-item" style="width:100%;"></iframe></div>';
      var $tsDetailDialog = appModal.dialog({
        title: row.name,
        message: iframe,
        size: 'large',
        width: '1000',
        shown: function () {
          console.log($tsDetailDialog.find('iframe')[0].contentWindow);
        },
        buttons: {}
      });
    }
  });

  return AppWorkTimeServiceListDevelopment;
});
