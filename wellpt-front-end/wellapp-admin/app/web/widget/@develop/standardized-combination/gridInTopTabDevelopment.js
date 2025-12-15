import WidgetTabDevelopment from '@develop/WidgetTabDevelopment';
import './css/index.less';

class gridInTopTabDevelopment extends WidgetTabDevelopment {
  mounted() {
    this.addClass('grid-in-top-tab');
    this.addClass('grid-in-layout-widget');

    this.isSetHeightTab = [];

    let $header = this.$widget.$el.querySelector('.ant-tabs-bar');
    const $headerStyle = window.getComputedStyle($header);
    let headerHeight =
      this.$widget.$el.querySelector('.ant-tabs-bar').offsetHeight +
      (parseFloat($headerStyle.marginBottom) || 0) +
      (parseFloat($headerStyle.marginTop) || 0);
    this.handleEvent(`${this.$widget.widget.id}:change`, ({ activeKey }) => {
      if (!this.isSetHeightTab[activeKey]) {
        let vpageHeight = this.getVpageHeight();
        this.$widget.tabContentScrollHeight = vpageHeight - headerHeight + 'px';
        this.setWidgetColHeight(vpageHeight - headerHeight);
      }
    });
    this.handleEvent(`SET_TEMPLATE_WIDGET-ROW-COL-HEIGHT`, ({ callback }) => {
      let vpageHeight = this.getVpageHeight();
      this.$widget.tabContentScrollHeight = vpageHeight - headerHeight + 'px';
      this.setWidgetColHeight(vpageHeight - headerHeight, callback);
    });

    let vpageHeight = this.getVpageHeight();
    this.$widget.tabContentScrollHeight = vpageHeight - headerHeight + 'px';
    this.setWidgetColHeight(vpageHeight - headerHeight);

    this.$widget.$root.$el.style.overflowX = 'hidden';
  }

  getVpageHeight() {
    let vpageHeight = this.$widget.$el.closest('.widget-vpage').offsetHeight;
    if (this.$widget.$root.$el.classList.contains('preview')) {
      vpageHeight = this.$widget.$root.$el.offsetHeight;
    }
    return vpageHeight;
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
  setWidgetColHeight(height, callback) {
    let $cols = this.$widget.$el.querySelectorAll('.widget-col');
    if ($cols.length) {
      $cols.forEach((col, index) => {
        let style = col.getAttribute('style');
        col.setAttribute('style', style ? style + ';height:' + height + 'px' : 'height:' + height + 'px');
      });
      this.isSetHeightTab[this.$widget.activeKey] = true;
      if (typeof callback === 'function') {
        callback();
      }
    }
  }

  get META() {
    return {
      name: '顶部标签页与左右栅格组合tab二开',
      hook: {}
    };
  }
}

export default gridInTopTabDevelopment;
