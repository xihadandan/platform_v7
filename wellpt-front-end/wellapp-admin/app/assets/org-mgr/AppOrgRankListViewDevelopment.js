define(['constant', 'commons', 'server', 'appContext', 'appModal', 'HtmlWidgetDevelopment'], function (
  constant,
  commons,
  server,
  appContext,
  appModal,
  HtmlWidgetDevelopment
) {
  var StringUtils = commons.StringUtils;
  var JDS = server.JDS;

  var AppOrgRankListViewDevelopment = function () {
    HtmlWidgetDevelopment.apply(this, arguments);
  };
  AppOrgRankListViewDevelopment._refresh = function () {
    this.getDutyData();
  };

  // 接口方法
  commons.inherit(AppOrgRankListViewDevelopment, HtmlWidgetDevelopment, {
    init: function () {
      var $element = this.widget.element;

      $element.off('click', 'button').on('click', 'button', function () {
        if ($(this).hasClass('rank-export')) {
          var url = getBackendUrl() + '/api/org/duty/hierarchy/dutyHierarchyExport?fileName=职务体系';
          $.ajax({
            url: url,
            type: 'get',
            contentType: 'json',
            dataType: 'json',
            success: function (res) {
              if (res.code == 0) {
                appModal.success('导出成功！');
              } else {
                appModal.error(res.msg);
              }
            }
          });
          window.open(url);
        } else {
          require(['html2canvas'], function (html2canvas) {
            var $table = $element.find('#dutyTable');
            if ($table.length < 1) {
              return;
            }
            var self = $table[0];
            html2canvas(self, {
              scale: 1,
              width: self.offsetWidth,
              heigth: self.offsetHeight
            }).then(function (canvas) {
              var imgURL = canvas.toDataURL('image/png');
              var a = document.createElement('a');
              a.href = imgURL;
              a.download = '职务体系';
              a.click();
              a.remove();
            });
          });
        }
      });

      AppOrgRankListViewDevelopment.getDutyData = function () {
        $.ajax({
          url: ctx + '/api/org/duty/hierarchy/dutyHierarchyView',
          type: 'get',
          dataType: 'json',
          contentType: 'application/json',
          success: function (res) {
            if (res.code == 0) {
              renderDutyData(res.data);
            }
          }
        });
      };

      function renderDutyData(data) {
        var $table = "<table id='dutyTable'><thead>";
        for (var i = 0; i < data.childDepth + 1; i++) {
          $table += '<tr></tr>';
        }
        $table += '</thead><tbody></tbody></table>';
        $('.duty-table', $element).html($table);
        renderDutyHeader(data.orgDutyHeaderDto, data.childDepth, 0);
        $th = $('#dutyTable', $element)
          .find('thead tr:eq(' + data.childDepth + ')')
          .find('th');
        $.each(data.jobGrade, function (index, grade) {
          var item = grade.jobGrade;
          var color = grade.isException;
          var $tr = "<tr data-level='" + item + "'";
          if (color == 1) {
            $tr = $tr + "style='background:#fcebeb;'";
          }
          $tr = $tr + '><td>' + (grade.jobGradeName || item) + '</td>';
          var len = $th.length;
          for (var j = 0; j < len; j++) {
            var uuid = $($th[j]).data('uuid');
            var type = $($th[j]).data('type');
            var html = getTdHtml(data.orgDutyBodyDto, uuid, type, item);
            vhtml = html.join(',');
            $tr += "<td title='" + vhtml + "'>" + vhtml + '</td>';
          }
          $tr += '</tr>';
          $('#dutyTable', $element).find('tbody').append($tr);
        });
      }

      function getTdHtml(orgDutyBodyDto, uuid, type, item) {
        var html = [];
        $.each(orgDutyBodyDto, function (index, dto) {
          if (dto.dutySeqUuid == uuid) {
            var jobGrade = dto.jobGrade.split(',');
            var gradeIndex = jobGrade.indexOf(item.toString());
            if (gradeIndex > -1) {
              if (type == '1') {
                var jobRankName = dto.jobRankName.split(',');
                if (html.indexOf(jobRankName[gradeIndex]) == -1) {
                  html.push(jobRankName[gradeIndex]);
                }
              } else {
                if (html.indexOf(dto.dutyName) == -1) {
                  html.push(dto.dutyName);
                }
              }
            }
          }
        });
        return html;
      }

      function renderDutyHeader(list, childDepth, depth) {
        var $tr = $('#dutyTable', $element).find('thead tr:eq(' + depth + ')');
        var $tr1 = $('#dutyTable', $element).find('thead tr:eq(' + childDepth + ')');

        if (depth == 0) {
          $tr.append("<th rowspan='" + (childDepth + 1) + "'>职等</th>");
        }

        for (var i = 0; i < list.length; i++) {
          var num = 0;
          var childrens = list[i].childrens;
          var item = list[i];
          if (childrens.length > 0) {
            var newDepth = depth + 1;
            num += renderDutyHeader(childrens, childDepth, newDepth);
            $tr.append("<th colspan='" + num + "'>" + item.dutySeqName + '</th>');
          } else {
            $tr.append("<th colspan='2' rowspan='" + (childDepth - depth) + "'>" + item.dutySeqName + '</th>');
            $tr1.append(
              "<th data-type='1' data-uuid='" + item.uuid + "'>职级</th><th data-type='2' data-uuid='" + item.uuid + "'>职务</th>"
            );
          }
        }
        return list.length * 2;
      }
    },
    refresh: function () {
      var _self = this;
      _self.init();
    }
  });
  return AppOrgRankListViewDevelopment;
});
