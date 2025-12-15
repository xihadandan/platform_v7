import WidgetTabDevelopment from '@develop/WidgetTabDevelopment';
import '@admin/app/web/template/org-setting/org-setting.less';
class OrgParamsSettingTabDevelopment extends WidgetTabDevelopment {
  mounted() {
    this.addClass('ant-tabs-menu-style');
    this.addClass('org-params-setting');

    let vpageHeight = this.$widget.$el.closest('.widget-vpage').offsetHeight;
    if (this.$widget.$root.$el.classList.contains('preview')) {
      vpageHeight = this.$widget.$root.$el.offsetHeight;
    }
    this.setCardBodyHeight(vpageHeight - 60 - 50);

    this.$widget.$root.$el.style.overflowX = 'hidden';
  }

  // 添加样式
  addClass(className) {
    if (className) {
      let $el = this.$widget.$el.querySelector('.ant-tabs');
      let classList = $el.classList;
      if (!classList.contains(className)) {
        classList.add(className);
      }
    }
  }

  // 设置卡片内容高度
  setCardBodyHeight(height) {
    this.$widget.tabs.forEach((tab, index) => {
      let cardWidget = tab.configuration.widgets[0];
      if (cardWidget && cardWidget.wtype === 'WidgetCard') {
        let $cardWidget = this.$widget.getVueWidgetById(cardWidget.id);
        if ($cardWidget) {
          $cardWidget.$set($cardWidget.widget.configuration, 'height', height + 'px');
        }
      }
    });
  }

  get META() {
    return {
      name: '组织参数左侧tab二开',
      hook: {}
    };
  }
}

export default OrgParamsSettingTabDevelopment;
