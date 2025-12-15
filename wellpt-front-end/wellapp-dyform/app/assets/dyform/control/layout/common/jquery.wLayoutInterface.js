(function ($) {
  $.wLayoutInterface = {
    /**
     * 初始化方法
     */
    init: function () {
      this.initSelf();
    },
    /**
     * 隐藏
     */
    hide: function () {},
    /**
     * 显示
     */
    show: function () {},
    /**
     * 隐藏标题
     */
    hideDisplayName: function () {},
    /**
     * 显示标题
     */
    showDisplayName: function () {},
    /**
     * 设置标题
     */
    setDisplayName: function (title) {},

    getType: function () {
      return this.options.mode;
    }
  };
})(jQuery);
