class SearchSetting {
  constructor({ enabled = false } = {}) {
    this.enabled = enabled;
    this.scopeList = [
      { name: '流程', title: '流程', value: 'workflow', visible: true },
      { name: '文档', title: '文档', value: 'dms_file', visible: true }
    ];
    this.interactiveMode = 'default'; // 交互模式 default page
    this.pageConfig = {
      enabledLogo: true,
      logo: '/static/images/full-search-logo.png',
      enabledTitle: true,
      title: '<p><span style="color: rgb(51, 51, 51);" class="ql-size-48px">全文检索</span></p>',
      backgroundColor: undefined,
      enableBgCarousel: false,
      backgroundImage: ['/static/images/full-search-bg.png'],
      backgroundPosition: undefined,
      backgroundRepeat: undefined
    };
    this.enabledCategory = false; // 搜索分类

    this.enabledHot = true; // 热门搜索
    this.enabledHistory = true; // 搜索历史
    this.enabledCommon = true; // 常用搜索
    this.enabledRecommendedCompletion = true; // 推荐词补全
    this.recommendCompleteSource = ['history', 'word']; // 推荐词补全依据

    this.resultConfig = {
      logo: '/static/images/full-search-logo.png',
      enabledColumns: true,
      pageSize: '10',
      customPageSize: ''
    }; // 结果页面设置
    this.resultDuplication = 'flow'; // 结果去重 flowAndForm flow form
    this.resultOpenMode = 'tab'; // 结果详情打开方式 tab window draw
    this.formPermissionTips = '无查看数据权限！'; // 表单数据权限提示

    this.enabledVoice = true; // 语言输入

    this.fileSearchType = 'onlyFileTile'
  }
}

export default SearchSetting;

export const interactiveModeOptions = [
  { label: '搜索框', value: 'default' },
  { label: '搜索页面', value: 'page' }
];

export const recommendCompleteSourceOptions = [
  { label: '历史搜索', value: 'history' },
  { label: '文档内容', value: 'word' }
];

export const resultDuplicationOptions = [
  { label: '同时显示流程和表单数据', value: 'flowAndForm' },
  { label: '仅显示流程数据', value: 'flow' },
  { label: '仅显示表单数据', value: 'form' }
];

export const resultOpenModeOptions = [
  { label: '新标签页打开', value: 'tab' },
  { label: '浏览器标签页打开', value: 'window' },
  { label: '抽屉打开', value: 'draw' }
];

export const backgroundPositionOptions = [
  { label: 'top', value: 'top' },
  { label: 'center', value: 'center' },
  { label: 'left', value: 'left' },
  { label: 'right', value: 'right' }
];

export const backgroundRepeatOptions = [
  { label: 'no-repeat', value: 'no-repeat' },
  { label: 'repeat-x', value: 'repeat-x' },
  { label: 'repeat-y', value: 'repeat-y' },
  { label: 'repeat', value: 'repeat' }
];

export const acceptTypes = ['image/gif', 'image/jpeg', 'image/png', 'image/svg+xml'];
export const acceptTip = '只允许上传JPG、PNG、GIF、SVG 的图片格式';
export const limitSize = 50;

export const pageSizeOptions = [
  { label: '10', value: '10' },
  { label: '15', value: '15' },
  { label: '20', value: '20' },
  { label: '30', value: '30' },
  { label: '自定义', value: 'custom' }
];


export const fileSearchTypeOptions = [
  { label: '仅搜索附件标题', value: 'onlyFileTile' },
  { label: '搜索附件标题和附件内容', value: 'fileTitleAndContent' },
]