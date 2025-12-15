export default {
  wtype: 'WidgetCarousel',
  name: '轮播图',
  iconClass: 'pticon iconfont icon-ptkj-zhedie',
  scope: ['page', 'dyform'],
  category: 'displayComponent',
  configuration: {
    jsModules: [],
    autoplay: false,
    autoplaySpeed: 3,
    dots: true,
    dotPosition: 'bottom',
    dotsClass: '',
    effect: 'scrollx',
    height: 100,
    heightUnit: 'px',
    arrows: false,
    pauseOnHover: true,
    children: [],
    /* domEvents: [
      {
        id: 'click',
        title: '图片点击触发',
        codeSource: 'codeEditor',
        jsFunction: null,
        widgetEvent: [
          {
            condition: {
              enable: false,
              conditions: [],
              match: 'all'
            },
            eventParams: []
          }
        ],
        customScript: null // 事件脚本
      }
    ] */
  }
};
