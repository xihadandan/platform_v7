export const typeOptions = [
  { label: '二维码', value: 'qrCode' },
  { label: '条形码', value: 'barCode' },
]

export const qrContentTypeOptions = [
  { label: '自定义', value: 'custom' },
  { label: '链接地址', value: 'link' },
]

export const scanTimeLimitTypeOptions = [
  { label: '长期有效', value: 'permanent' },
  { label: '一天', value: 'oneDay', addNum: 1, addUnit: 'days' },
  { label: '三天', value: 'threeDay', addNum: 3, addUnit: 'days' },
  { label: '一周', value: 'oneWeek', addNum: 1, addUnit: 'weeks' },
  { label: '一个月', value: 'oneMonth', addNum: 1, addUnit: 'months' },
  { label: '六个月', value: 'sixMonth', addNum: 6, addUnit: 'months' },
  { label: '一年', value: 'oneYear', addNum: 1, addUnit: 'years' },
  { label: '自定义', value: 'custom' },
]

export const barContentTypeOptions = [
  { label: 'CODE128', value: 'code128' },
  { label: 'PDF417', value: 'pdf417' },
]