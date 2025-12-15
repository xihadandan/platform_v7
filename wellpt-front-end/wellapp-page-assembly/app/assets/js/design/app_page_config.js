define([
  'jquery',
  'jquery-ui',
  'bootstrap',
  'server',
  'commons',
  'appContext',
  'appWindowManager',
  'appModal',
  'AppPageDesigner',
  'jsonview'
], function ($, ui, bootstrap, server, commons, appContext, appWindowManager, appModal, AppPageDesigner) {
  // 页面引用资源的视图组件ID
  var pageResourceListWidgetDefId = null;
  var UUID = commons.UUID;
  var Browser = commons.Browser;
  var StringUtils = commons.StringUtils;
  var SystemParams = server.SystemParams;

  // 1、创建页面设计器对象并初始化
  var pageDesigner = new AppPageDesigner({
    onCreate: function () {
      bindEvents(this);
    }
  });
  window.appPageDesigner = pageDesigner;

  // 2、绑定事件
  function bindEvents(pageDesigner) {
    var $container = $('.web-app-container');
    var container = pageDesigner.getPageContainer();
    var pageUuid = pageDesigner.getPageUuid();
    // 页面引用资源的视图组件ID
    pageResourceListWidgetDefId = Browser.getQueryString('prlvId');
    // 显示操作按钮
    if (StringUtils.isNotBlank(pageUuid)) {
      if (WebApp.containerDefaults && WebApp.containerDefaults.supportsPortal === true) {
        $('a.btn_portal_config').removeClass('hide');
      }
      if (StringUtils.isNotBlank(pageResourceListWidgetDefId)) {
        $('#btn_view_ref_resource').removeClass('hide');
      }
      $('#btn_preview').removeClass('hide');
      $('#btn_save_new_version').removeClass('hide');
    }
    $('#btn_save').removeClass('hide');

    // 页面JSON查看器
    var storage = commons.StorageUtils.getStorage(3);
    var isJsonViewer = storage.getItem('app_designer_json_viewer');
    if (isJsonViewer === 'true') {
      $('.btn-container-json-viewer', $container).removeClass('hide');
      $('.btn-container-json-viewer', $container).on('click', function () {
        pageDesigner.showJsonViewer(container);
      });
    }

    // 检测组件标题的合法性
    function checkedWidgetTitle(components) {
      for (var i = 0; i < components.length; i++) {
        var component = components[i];
        var $widgetTitle = $(component.element).find('.widget-title').first();
        var widgetTitle = $widgetTitle.text();
        if ($widgetTitle.find('input').length > 0) {
          if ($widgetTitle.find('input').val().length > 255) {
            alert('组件标题不能大于255个字符!');
            return false;
          }
        }
        if (StringUtils.isBlank(widgetTitle)) {
          alert('组件标题不能为空!');
          $widgetTitle.focus();
          return false;
        }
        if (widgetTitle.length > 255) {
          alert('组件标题不能大于255个字符!');
          return false;
        }
        if (!checkedWidgetTitle(component.getChildren())) {
          return false;
        }
      }
      return true;
    }

    // 保存页面定义JSON
    var saveDefinitionJson = function (newVersion) {
      // 页面定义JSON
      var definitionJson = container.getDefinitionJson.call(container, $container);
      // 检测组件标题
      if (!checkedWidgetTitle(container.getChildren())) {
        return;
      }
      var html = container.toHtml.call(container, $container);
      definitionJson.html = html;

      var piUuid = $('input[id=pi_uuid]').val();
      var pageUuid = $('input[id=page_uuid]').val();
      definitionJson.uuid = pageUuid;
      appModal.showMask('页面保存中');
      $.ajax({
        url: '/web/design/saveDefinitionJson',
        type: 'POST',
        async: true,
        data: {
          uuid: pageUuid,
          piUuid: piUuid,
          definitionJson: JSON.stringify(definitionJson),
          newVersion: newVersion
        },
        success: function (result) {
          if (result == 'success') {
            try {
              $('body').trigger('ace_$saveCodeHis'); // 触发代码编辑器的保存历史事件
            } catch (e) {}

            pageDesigner.saveSuccess = true;
            appModal.hideMask();
            appModal.success('保存成功！', function () {
              appContext.getWindowManager().refreshParent();
              if (newVersion && result.data) {
                var href = window.location.href;
                var value = Browser.getQueryString('pageUuid');
                if (StringUtils.isNotBlank(value)) {
                  href = href.replace('pageUuid=' + value, 'pageUuid=' + result.data);
                }
                window.location.href = href;
              } else {
                window.location.reload();
              }
            });
          }
        },
        error: function () {
          appModal.hideMask();
        }
      });
    };
    // 保存
    $('#btn_save').on('click', function (e) {
      saveDefinitionJson(false);
    });
    // 保存新版本
    $('#btn_save_new_version').on('click', function () {
      saveDefinitionJson(true);
    });
    // 预览
    $('#btn_preview').on('click', function (e) {
      if (pageDesigner.isWidgetDefinitionChanged()) {
        appModal.error('组件定义已变更，请先保存再预览!');
        return;
      }
      var piUuid = $('input[id=pi_uuid]').val();
      var pageUuid = $('input[id=page_uuid]').val();
      if (pageUuid == null || $.trim(pageUuid) === '') {
        var html = container.toHtml.call(container, $container);
        $('#preview_html').html(html);
      } else {
        // 是否启用uni-app页面预览 uni-app.page-desinger.preview.enabled
        // uni-app页面预览url uni-app.page-desinger.preview.url
        if (pageDesigner.appPageDefinition && pageDesigner.appPageDefinition.wtype == 'wMobilePage') {
          var url = ctx + '/web/app/page/preview/' + piUuid + '?pageUuid=' + pageUuid;
          var uniAppPreviewEnabled = SystemParams.getValue('uni-app.page-desinger.preview.enabled');
          var uniAppPreviewUrl = SystemParams.getValue('uni-app.page-desinger.preview.url');
          if (uniAppPreviewEnabled == 'true' && StringUtils.isNotBlank(uniAppPreviewUrl)) {
            var piItem = appContext.getPiItem(piUuid);
            var accessToken = getCookie('jwt');
            uniAppPreviewUrl += `#/uni_modules/w-app/pages/app/preview?appPiPath=${piItem.path}&pageUuid=${pageUuid}&accessToken=${accessToken}`;
            window.open(uniAppPreviewUrl, piUuid + pageUuid + '_preview');
          } else {
            window.open(url, piUuid + '_preview');
          }
        } else {
          window.open(url, piUuid + '_preview');
        }
      }
    });
    var storage = commons.StorageUtils.getStorage(3);
    // 开启组件定义查看器
    $('#btn_enable_json_viewer')
      .on('click', function () {
        if (pageDesigner.isWidgetDefinitionChanged()) {
          appModal.error('组件定义已变更，请先保存再开启!');
          return;
        }
        storage.setItem('app_designer_json_viewer', 'true');
        window.location.reload();
      })
      .removeClass('hide');
    // 关闭组件定义查看器
    $('#btn_disable_json_viewer')
      .on('click', function () {
        if (pageDesigner.isWidgetDefinitionChanged()) {
          appModal.error('组件定义已变更，请先保存再关闭!');
          return;
        }
        storage.setItem('app_designer_json_viewer', 'false');
        window.location.reload();
      })
      .removeClass('hide');
    var isJsonViewer = storage.getItem('app_designer_json_viewer');
    if (isJsonViewer === 'true') {
      $('#btn_enable_json_viewer').hide();
      $('#btn_disable_json_viewer').show();
    } else {
      $('#btn_enable_json_viewer').show();
      $('#btn_disable_json_viewer').hide();
    }

    // 开启所见即所得
    $('#btn_enable_wysiwyg')
      .on('click', function () {
        if (pageDesigner.isWidgetDefinitionChanged()) {
          appModal.error('组件定义已变更，请先保存再开启!');
          return;
        }
        storage.setItem('app_designer_wysiwyg', 'true');
        window.location.reload();
      })
      .removeClass('hide');
    // 关闭所见即所得
    $('#btn_disable_wysiwyg')
      .on('click', function () {
        if (pageDesigner.isWidgetDefinitionChanged()) {
          appModal.error('组件定义已变更，请先保存再关闭!');
          return;
        }
        storage.setItem('app_designer_wysiwyg', 'false');
        window.location.reload();
      })
      .removeClass('hide');
    var isWysiwyg = storage.getItem('app_designer_wysiwyg');
    if (isWysiwyg === 'true') {
      $('#btn_enable_wysiwyg').hide();
      $('#btn_disable_wysiwyg').show();
    } else {
      $('#btn_enable_wysiwyg').show();
      $('#btn_disable_wysiwyg').hide();
    }
    // 查看引用资源
    $('#btn_view_ref_resource').on('click', function () {
      var piUuid = $('input[id=pi_uuid]').val();
      var pageUuid = $('input[id=page_uuid]').val();
      var dlgId = UUID.createUUID();
      var title = '引用资源';
      var message = "<div id='" + dlgId + "'></div>";
      var dlgOptions = {
        title: title,
        message: message,
        size: 'large',
        shown: function () {
          appContext.renderWidget({
            renderTo: '#' + dlgId,
            widgetDefId: pageResourceListWidgetDefId,
            forceRenderIfConflict: true,
            params: {
              source: '1',
              page: {
                uuid: pageUuid
              }
            },
            callback: function () {
              $('#' + dlgId + ' > .ui-wBootstrapTable').css({
                overflow: 'auto'
              });
            },
            onPrepare: function () {}
          });
        },
        buttons: {
          confirm: {
            label: '关闭',
            className: 'btn-primary',
            callback: function () {
              return true;
            }
          }
        }
      };
      appModal.dialog(dlgOptions);
    });
    // 门户设置
    $('a.btn_portal_config').on('click', function () {
      pageDesigner.configure.call(pageDesigner, container);
    });
  }
});
