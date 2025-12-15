import WidgetCardDevelopment from '@develop/WidgetCardDevelopment';
import './css/index.less';
class gridInCardDevelopment extends WidgetCardDevelopment {
  mounted() {
    this.addClass('grid-in-card');

    this.handleEvent(`SET_TEMPLATE_WIDGET-ROW-COL-HEIGHT`, ({ callback }) => {
      let vpageHeight = this.getVpageHeight();
      let headerHeight = this.getHeaderHeight();
      this.setWidgetColHeight(vpageHeight - headerHeight, callback);
    });

    let vpageHeight = this.getVpageHeight();
    let headerHeight = this.getHeaderHeight();
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

  getHeaderHeight() {
    let headerHeight = 1;
    if (this.$widget.$el.querySelector('.ant-card-head')) {
      headerHeight = this.$widget.$el.querySelector('.ant-card-head').offsetHeight;
    }
    // 如果卡片有边框，要加上上下边框宽度
    if (this.$widget.configuration.bordered) {
      let borderWidth = window.getComputedStyle(this.$widget.$el).borderWidth;
      headerHeight = headerHeight + parseFloat(borderWidth) * 2;
    }
    return headerHeight;
  }

  // 添加样式
  addClass(className, $el) {
    if (className) {
      if (!$el) {
        $el = this.$widget.$el;
      }
      let classList = $el.classList;
      if (!classList.contains(className)) {
        classList.add(className);
      }
    }
  }

  // 设置栅格内容高度
  setWidgetColHeight(height) {
    let $cols = this.$widget.$el.querySelectorAll('.widget-col');
    if ($cols.length) {
      $cols.forEach((col, index) => {
        let style = col.getAttribute('style');
        col.setAttribute('style', style ? style + ';height:' + height + 'px' : 'height:' + height + 'px');
      });
      if (typeof callback === 'function') {
        callback();
      }
    }
  }

  get META() {
    return {
      name: '卡片与左右栅格组合card二开',
      hook: {}
    };
  }
}

export default gridInCardDevelopment;
