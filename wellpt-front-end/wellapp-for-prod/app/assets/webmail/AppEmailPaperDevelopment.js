define(['jquery', 'commons', 'constant', 'server', 'TileWidgetDevelopment', 'appModal'], function (
  $,
  commons,
  constant,
  server,
  TileWidgetDevelopment,
  appModal
) {
  var AppEmailPaperDevelopment = function () {
    TileWidgetDevelopment.apply(this, arguments);
  };

  commons.inherit(AppEmailPaperDevelopment, TileWidgetDevelopment, {
    systemPapers: [],

    init: function () {
      var _self = this;
    },

    beforeRender: function (options, configuration) {
      this.loadSytemMailPapers(configuration);
    },

    afterRender: function (options, configuration) {
      this.loadCssLink();
      this.bindEvent();
      this.selectUserDefaultPaper();
      this.tilePaperDataBind();
    },

    loadCssLink: function () {
      // $("head").append(
      //     $("<link>", { "href": staticPrefix + "/js/pt/css/webmail/wm_webmail_v2.css", "rel": "stylesheet" }),
      //     $("<link>", { "href": staticPrefix + "/css/widget/jquery-ui-wTiles.css", "rel": "stylesheet" })
      // );
    },

    bindEvent: function () {
      var _self = this;
      var $pageElement = _self.widget.pageContainer.element;

      $pageElement.off('click', '.wTiles .tile-content').on('click', '.wTiles .tile-content', function () {
        $('.previewContainerDiv').remove();
        $('.paperSelected').removeClass('paperSelected');
        $(this).addClass('paperSelected');
      });

      $pageElement.off('click', '.paperSaveBtn').on('click', '.paperSaveBtn', function () {
        var $paperSelected = $('.paperSelected');
        if ($paperSelected.length == 0) {
          appModal.alert('请选择信纸');
          return;
        }

        _self.updateUserDefaultPaper($paperSelected.parent().is('.noUseMailPaper') ? {} : $paperSelected.parent().data('backgroundData'));
      });

      $pageElement.off('hover', '.wTiles:gt(0)').on('hover', '.wTiles:gt(0)', function (e) {
        if (e.type == 'mouseenter') {
          if ($(this))
            //添加工具栏
            $(this).append($('<div>', { class: 'paperToolIconDiv' }).append($('<span>', { class: 'iconfont icon-ptkj-sousuochaxun' })));
        } else {
          $('.paperToolIconDiv').remove();
        }
      });

      $pageElement
        .off('click', '.paperToolIconDiv .icon-ptkj-sousuochaxun')
        .on('click', '.paperToolIconDiv .icon-ptkj-sousuochaxun', function (e) {
          $('.previewContainerDiv').remove();
          _self.showPreviewPaper($(this).parent().parent());
        });
    },

    tilePaperDataBind: function () {
      for (var i = 0, len = this.systemPapers.length; i < len; i++) {
        if (!this.systemPapers[i].userId) {
          this.systemPapers[i].uuid = null;
        }
        $('.paper_' + i).data('backgroundData', this.systemPapers[i]);
      }
    },

    transferRgbColorToHex: function (color) {
      if (!$.browser.msie) {
        color = color.match(/^rgb\((\d+),\s*(\d+),\s*(\d+)\)$/);

        function hex(x) {
          return ('0' + parseInt(x).toString(16)).slice(-2);
        }
        color = '#' + hex(color[1]) + hex(color[2]) + hex(color[3]);
      }
      return color;
    },

    showPreviewPaper: function ($target) {
      var imgSrc = $target.find('img').attr('src').replace('.png', '_big.jpg');
      var $containerDiv = $('<div>', { class: 'previewContainerDiv' });
      var $upIcon = $('<div>', { class: 'previewUpIcon' });
      var $closeIcon = $('<span>', {
        class: 'previewCloseIcon iconfont icon-ptkj-dacha',
        onclick: "document.querySelector('.previewContainerDiv').remove();"
      });
      var $contentDiv = $('<div>', { class: 'previewContentDiv' });
      var $subcontentDiv = $('<div>', { class: 'subcontentDiv', style: 'background-image:url(' + imgSrc + ')' }).html(
        '<h3>您好：</h3><p>这是一封测试邮件，用来预览信纸的效果。</p><p>正文 AaBbCc</p>'
      );
      $contentDiv.append($upIcon, $closeIcon, $subcontentDiv);
      $containerDiv.append($contentDiv);
      $containerDiv.insertAfter($target);
      //调整显示位置
      if ($target.offset().left > $(window).width() / 2) {
        $('.previewContainerDiv').css('left', '-350px');
        $('.previewUpIcon').css('left', '370px');
      }
    },

    selectUserDefaultPaper: function () {
      server.JDS.call({
        service: 'wmMailPaperFacadeService.queryCurrentUserDefaultPaper',
        data: [],
        success: function (result) {
          if (result.data) {
            var $img = $("img[src='" + staticPrefix + result.data.backgroundImgUrl + "']");
            if ($img.length > 0) {
              $img.parent().addClass('paperSelected');
            } else {
              //不使用信纸
            }
          }
        },
        error: function (jqXHR) {
          appModal.info('保存更改信纸失败');
        }
      });
    },

    updateUserDefaultPaper: function (background) {
      server.JDS.call({
        service: 'wmMailPaperFacadeService.updateCurrentUserDefaultPaper',
        data: [background],
        success: function (result) {
          if (result.success) {
            appModal.info('保存更改信纸成功');
          }
        },
        error: function (jqXHR) {
          appModal.info('保存更改信纸失败');
        }
      });
    },

    //加载系统默认配置信纸
    loadSytemMailPapers: function (configuration) {
      var _self = this;
      var noPicTile = configuration.tiles[0];
      server.JDS.call({
        service: 'wmMailPaperFacadeService.querySystemMailPapers',
        data: [],
        success: function (result) {
          if (result.data) {
            //configuration.tiles=[];
            _self.systemPapers = result.data;
            for (var i = 0, len = result.data.length; i < len; i++) {
              var paper = result.data[i];
              var tile = {
                backgroundColor: paper.backgroundColor,
                size: 'small',
                uuid: paper.uuid,
                contentOptions: {
                  effect: 'fit',
                  imgPaths: [staticPrefix + paper.backgroundImgUrl],
                  type: 'image'
                },
                code: 'paper_' + i
              };
              configuration.tiles.push(tile);
            }
          }
        },
        error: function (jqXHR) {
          appModal.info('保存更改信纸失败');
        },
        async: false
      });
    }
  });

  return AppEmailPaperDevelopment;
});
