import WidgetTabDevelopment from '@develop/WidgetTabDevelopment';
import '@admin/app/web/template/organization/biz-org/biz-org.less';
class BizOrgManageTabDevelopment extends WidgetTabDevelopment {
  mounted() {
    this.addClass('biz-org-manager-tab');

    let vpageHeight = this.$widget.$el.closest('.widget-vpage').offsetHeight;
    if (this.$widget.$root.$el.classList.contains('preview')) {
      vpageHeight = this.$widget.$root.$el.offsetHeight;
    }
    this.setWidgetColHeight(vpageHeight - 46);

    this.$widget.$root.$el.style.overflowX = 'hidden';
  }

  // 添加样式
  addClass(className, $el) {
    if (className) {
      if (!$el) {
        $el = this.$widget.$el.querySelector('.ant-tabs');
      }
      let classList = $el.classList;
      if (!classList.contains(className)) {
        classList.add(className);
      }
    }
  }

  // 设置栅格内容高度
  setWidgetColHeight(height) {
    this.$widget.$el.querySelectorAll('.widget-col').forEach((col, index) => {
      let style = col.getAttribute('style');
      col.setAttribute('style', style + ';height:' + height + 'px');
    });
    let tableIds = ['pEEOtycqOFCSXHeaEIeqRmulAOVnbucp', 'PQHJoCaIVhpCbNMQAhOODnbWbqhsKFUE', 'JlzeqoHXKADUKgHZjPWWmjbyhFfLfzNI'];
    tableIds.forEach((id, index) => {
      let $tableWidget = this.$widget.getVueWidgetById(id);
      if ($tableWidget) {
        $tableWidget.widget.configuration.scrollY = height - 140;
      }
    });
  }

  get META() {
    return {
      name: '业务组织管理左侧tab二开',
      hook: {}
    };
  }
}

export default BizOrgManageTabDevelopment;
