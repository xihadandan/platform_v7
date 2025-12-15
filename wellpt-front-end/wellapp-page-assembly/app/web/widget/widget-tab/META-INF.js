// webpack 查找组件目录下的 META.js 形成组件元数据在设计器中管理
import thumbnail from './thumbnail.svg';
export default {
  wtype: 'WidgetTab',
  name: '标签页',
  iconClass: 'pticon iconfont icon-a-bujuzujianbiaoqianye',
  category: 'advanceContainer',
  scope: ['page', 'dyform', 'mobilePage', 'mobileDyform'],
  icon: undefined, // 图标, 考虑支持图标类 \ svg \ base64
  thumbnail, // 缩略图, 考虑支持图标类 \ svg \ base64
  description: '' // 描述性文案
};
