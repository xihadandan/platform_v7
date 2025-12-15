/**
 * 控件管理类，主要负责控件的动态创建以及控件的实例对象获取。
 */
(function ($) {
  $.ContainerManager = $.ContainerManager || {
    layouts: {},
    /** 创建布局容器 */
    createContainer: function ($placeHolder, name, formDefinition, otherOptions) {
      // otherOptions 包括有位置信息等等
      if (typeof $placeHolder == 'undefined' || $placeHolder == null || $placeHolder.size() == 0) {
        console.error('placeholder not defined');
        return null;
      }

      // 布局id
      var containerName = $placeHolder.attr('name');
      if (typeof containerName == 'undefined' || containerName.length == 0) {
        console.error('placeholder must have a name property ,used to save the layout id');
        return null;
      }

      // 容器类型
      var clazz = this.getClazz($placeHolder);
      if (clazz == null) {
        console.error('unknown layout :' + $placeHolder.attr('class'));
        return null;
      }

      // 参数信息
      var options = {
        name: '',
        displayName: '',
        mode: clazz,
        formUuid: formDefinition.uuid
      };

      switch (clazz) {
        case layoutMode.tab:
          // 页签容器
          var definition = formDefinition.layouts[name];
          $.extend(true, options, definition, otherOptions);

          $placeHolder.wTab(options);
          var containerObj = $placeHolder.wTab('getObject');
          this[name] = { $placeHolder: $placeHolder, containerObj: containerObj };
          return containerObj;
        case layoutMode.block:
          // 区块容器
          var definition = formDefinition.blocks[name];
          $.extend(true, options, definition, otherOptions);

          $placeHolder.wBlock(options);
          var containerObj = $placeHolder.wBlock('getObject');
          this[name] = { $placeHolder: $placeHolder, containerObj: containerObj };
          return containerObj;
        default:
          console.error('no layout exists :' + clazz);
          return null;
      }
    },

    /** 通过容器id获取容器对象 */
    getContainer: function (name) {
      var containerData = this[name];
      if (containerData && containerData.containerObj) {
        return containerData.containerObj;
      }

      return null;
    },

    /** 通过布局id获取容器对象 */
    getLayout: function (name) {
      var containerData = this.layouts[name];
      if (containerData && containerData.layoutObj) {
        return containerData.layoutObj;
      }

      return null;
    },

    /** 获取占位符 */
    get$placeHolder: function (name) {
      var containerData = this[name];
      return typeof containerData == 'undefined' ? null : containerData.$placeHolder;
    },

    getClazz: function ($placeHolder) {
      for (var clazz in layoutMode) {
        if ($placeHolder.is('.' + layoutMode[clazz])) {
          return layoutMode[clazz];
        }
      }
      return null;
    }
  };
})(jQuery);
