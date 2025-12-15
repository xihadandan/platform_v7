// webpack 查找组件目录下的 META.js 形成组件元数据在设计器中管理

export default {
  wtype: 'WidgetTable',
  name: '表格',
  iconClass: 'pticon iconfont icon-ptkj-biaogeshitu',
  category: 'displayComponent',
  scope: ['page', 'dyform', 'bigScreen', 'mobileDyform', 'mobilePage'],
  icon: undefined, // 图标, 考虑支持图标类 \ svg \ base64
  thumbnail:
    '<svg t="1690352304273" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="6850" width="128" height="128"><path d="M0 0h1024v292.571429H0z" fill="#4185F4" p-id="6851"></path><path d="M0 365.714286h292.571429v292.571428H0z" fill="#A0C2F9" p-id="6852"></path><path d="M0 731.428571h292.571429v292.571429H0z" fill="#A0C2F9" p-id="6853"></path><path d="M365.714286 365.714286h292.571428v292.571428H365.714286zM365.714286 731.428571h292.571428v292.571429H365.714286z" fill="#A0C2F9" p-id="6854"></path><path d="M731.428571 365.714286h292.571429v292.571428h-292.571429z" fill="#A0C2F9" p-id="6855"></path><path d="M731.428571 731.428571h292.571429v292.571429h-292.571429z" fill="#4185F4" p-id="6856"></path></svg>', // 缩略图, 考虑支持图标类 \ svg \ base64
  description: '平台用于展示表格数据的组件' // 描述性文案
};
