// webpack 查找组件目录下的 META.js 形成组件元数据在设计器中管理
import thumbnail from './thumbnail.png';
import icon from './icon.png';
export default {
  wtype: 'WidgetMenu',
  name: '导航菜单',
  scope: ['page'],
  category: 'navComponent', // 通过分类把组件归类到设计器内
  iconClass: 'pticon iconfont icon-ptkj-daohangcaidan-01',
  icon, // 图标, 考虑支持图标类 \ svg \ base64
  thumbnail,
  description: '平台用于展示表格数据的组件' // 描述性文案
};
