import VueWidgetDevelopment from '@develop/VueWidgetDevelopment';

class AppEmailLeftMenuWidgetDevelopment extends VueWidgetDevelopment {
  get META() {
    return {
      name: '平台邮件_左导航二开',
      hook: {
        initMyFolders: '初始化,我的文件夹导航',
        initMyTags: '初始化,我的标签导航',
        initOtherUsers: '初始化,其他邮箱账号导航'
      }
    };
  }

  /**
   *
   */
  mounted() {
    const _this = this;
    _this.widget = _this.getWidget().widget;
    _this.configuration = _this.getWidget().configuration;
    // 监听页面提示信息
    _this.$widget.pageContext.handleEvent('app:email:leftMenu:selectClear', result => {
      // 清除除result.id外的其他导航的选中项
      if (_this.widget.id == result.id) {
        _this.getWidget().selectedKeys.splice(0, _this.getWidget().selectedKeys.length);
      }
    });
  }

  //初始化我的文件夹导航
  initMyFolders() {
    const _this = this;
    console.log('我的文件夹', _this.widget.id);
  }

  //初始化我的标签导航
  initMyTags() {
    const _this = this;
    console.log('我的文件夹', _this.$widget.id);
  }

  //初始化其他邮箱账号导航
  initOtherUsers() {
    const _this = this;
    console.log('其他邮箱账号', _this.$widget.id);
  }
}

export default AppEmailLeftMenuWidgetDevelopment;
