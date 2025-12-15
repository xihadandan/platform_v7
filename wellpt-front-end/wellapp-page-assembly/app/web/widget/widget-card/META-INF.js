import icon from './icon.png';
import thumbnail from './thumbnail.png';
export default {
  wtype: 'WidgetCard',
  name: '卡片',
  scope: ['page', 'dyform', 'mobileDyform', 'mobilePage'],
  category: 'advanceContainer',
  iconClass: 'pticon iconfont icon-ptkj-kapian-01',
  icon, // 图标, 考虑支持图标类 \ svg \ base64
  thumbnail,
  description: '' // 描述性文案
};
