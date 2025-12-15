export default {
  wtype: 'WidgetQrBarCode',
  name: '二维/条形码',
  iconClass: 'pticon iconfont icon-a-icjichuzujianzhiwei',
  scope: ['page', 'dyform'],
  category: 'basicComponent',
  configuration: {
    enabledTitle: false,
    titleIcon: null,
    type: 'qrCode',
    qrContentType: 'custom',
    qrContentForm: '',
    qrContentLink: {
      type: 'custom',
      value: null,
      workflowType: 'start_new_work',
      formMode: 'new_form',
      formOpenType: 'current',
      formSpecify: null
    },
    qrSize: 140,
    logo: '',
    logoSize: 30,
    scanTimeLimitType: 'permanent',
    showValidityDate: false,
    expirationDateRange: [],

    barContentType: 'code128',
    barContentForm: '',
    barCodeWidth: 'auto',
    barCodeHeight: 100,
    barShowLabel: false
  }
};
