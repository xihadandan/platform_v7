const detePatternTypeOptions = [
  {
    key: 'date',
    label: '日期'
  },
  {
    key: 'time',
    label: '时间'
  },
  {
    key: 'datetime',
    label: '日期时间'
  }
];

/**
 * key0\label0：补0对应值
 * key\label：无补0对应值
 * mode主要用于判断日期选择框模式<a-date-picker :mode="mode"></a-date-picker>
 * mode=time，使用<a-time-picker></a-time-picker>
 * mode=week，使用<a-week-picker></a-week-picker>
 * mode=date，如果Type=datetime,需要有show-time,<a-date-picker show-time></a-date-picker>
 * contentFormat:只有日期的部分按日期的，如yyyy，yyyy-MM。只有时间部分按时间的，如HH，HH:ss。包含日期、时间的按完整格式，如yyyy-MM-dd HH，MM-dd HH都按yyyy-MM-dd HH:mm:ss
 * 前端格式化 '天'用大写D, 服务端格式化， '天'用小写d
 */
//
const datePatternOptions = {
  date: [
    {
      mode: 'year',
      key0: 'yyyy',
      label0: 'yyyy（eg：2022）',
      key: 'yyyy',
      label: 'yyyy（eg：2022）',
      contentFormat: 'yyyy'
    },
    {
      mode: 'year',
      key0: 'yyyy年',
      label0: 'yyyy年（eg：2022年）',
      key: 'yyyy年',
      label: 'yyyy年（eg：2022年）',
      contentFormat: 'yyyy'
    },
    {
      mode: 'month',
      key0: 'yyyy-MM',
      label0: 'yyyy-MM（eg：2022-01）',
      key: 'yyyy-M',
      label: 'yyyy-M（eg：2022-1）',
      contentFormat: 'yyyy-MM'
    },
    {
      mode: 'month',
      key0: 'yyyy/MM',
      label0: 'yyyy/MM（eg：2022/01）',
      key: 'yyyy/M',
      label: 'yyyy/M（eg：2022/1）',
      contentFormat: 'yyyy-MM'
    },
    {
      mode: 'month',
      key0: 'yyyy年MM月',
      label0: 'yyyy年MM月（eg：2022年01月）',
      key: 'yyyy年M月',
      label: 'yyyy年M月（eg：2022年1月）',
      contentFormat: 'yyyy-MM'
    },
    {
      mode: 'date',
      key0: 'MM-DD',
      label0: 'MM-DD（eg：01-01）',
      key: 'M-D',
      label: 'M-D（eg：1-1）',
      contentFormat: 'MM-DD'
    },
    {
      mode: 'date',
      key0: 'MM/DD',
      label0: 'MM/DD（eg：01/01）',
      key: 'M/D',
      label: 'M/D（eg：1/1）',
      contentFormat: 'MM-DD'
    },
    {
      mode: 'date',
      key0: 'MM月DD日',
      label0: 'MM月DD日（eg：01月01日）',
      key: 'M月D日',
      label: 'M月D日（eg：1月1日）',
      contentFormat: 'MM-DD'
    },
    {
      mode: 'date',
      key0: 'yyyy-MM-DD',
      label0: 'yyyy-MM-DD（eg：2022-01-01）',
      key: 'yyyy-M-D',
      label: 'yyyy-M-D（eg：2022-1-1）',
      contentFormat: 'yyyy-MM-DD'
    },
    {
      mode: 'date',
      key0: 'yyyy/MM/DD',
      label0: 'yyyy/MM/DD（eg：2022/01/01）',
      key: 'yyyy/M/D',
      label: 'yyyy/M/D（eg：2022/1/1）',
      contentFormat: 'yyyy-MM-DD'
    },
    {
      mode: 'date',
      key0: 'yyyy年MM月DD日',
      label0: 'yyyy年MM月DD日（eg：2022年01月01日）',
      key: 'yyyy年M月D日',
      label: 'yyyy年M月D日（eg：2022年1月1日）',
      contentFormat: 'yyyy-MM-DD'
    },
    {
      mode: 'week',
      key0: 'yyyy年ww周',
      label0: 'yyyy年ww周（eg：2022年第01周）',
      key: 'yyyy年w周',
      label: 'yyyy年w周（eg：2022年第1周）',
      contentFormat: 'yyyy-ww'
    }
  ],
  time: [
    {
      mode: 'time',
      key0: 'HH时',
      label0: 'HH时（eg：13时）',
      key: 'H时',
      label: 'H时（eg：13时）',
      contentFormat: 'HH'
    },
    {
      mode: 'time',
      key0: 'HH:mm',
      label0: 'HH:mm（eg：13:00）',
      key: 'H:m',
      label: 'H:m（eg：13:0）',
      contentFormat: 'HH:mm'
    },
    {
      mode: 'time',
      key0: 'HH时mm分',
      label0: 'HH时mm分（eg：13时00分）',
      key: 'H时m分',
      label: 'H时m分（eg：13时0分）',
      contentFormat: 'HH:mm'
    },
    {
      mode: 'time',
      key0: 'HH:mm:ss',
      label0: 'HH:mm:ss（eg：13:00:00）',
      key: 'H:m:s',
      label: 'H:m:s（eg：13:0:0）',
      contentFormat: 'HH:mm:ss'
    },
    {
      mode: 'time',
      key0: 'HH时mm分ss秒',
      label0: 'HH时mm分ss秒（eg：13时00分00秒）',
      key: 'H时m分s秒',
      label: 'H时m分s秒（eg：13时0分0秒）',
      contentFormat: 'HH:mm:ss'
    }
  ],
  datetime: [
    {
      mode: 'date',
      key0: 'MM-DD HH:mm',
      label0: 'MM-DD HH:mm（eg：01-01 13:00）',
      key: 'M-D H:m',
      label: 'M-D H:m（eg：1-1 13:0）',
      contentFormat: 'yyyy-MM-DD HH:mm:ss'
    },
    {
      mode: 'date',
      key0: 'MM-DD HH:mm:ss',
      label0: 'MM-DD HH:mm:ss（eg：01-01 13:00:00）',
      key: 'M-D H:m:s',
      label: 'M-D H:m:s（eg：1-1 13:0:0）',
      contentFormat: 'yyyy-MM-DD HH:mm:ss'
    },
    {
      mode: 'date',
      key0: 'MM/DD HH:mm',
      label0: 'MM/DD HH:mm（eg：01/01 13:00）',
      key: 'M/D H:m',
      label: 'M/D H:m（eg：1/1 13:0）',
      contentFormat: 'yyyy-MM-DD HH:mm:ss'
    },
    {
      mode: 'date',
      key0: 'MM/DD HH:mm:ss',
      label0: 'MM/DD HH:mm:ss（eg：01/01 13:00:00）',
      key: 'M/D H:m:s',
      label: 'M/D H:m:s（eg：1/1 13:0:0）',
      contentFormat: 'yyyy-MM-DD HH:mm:ss'
    },
    {
      mode: 'date',
      key0: 'MM月DD日 HH时',
      label0: 'MM月DD日 HH时（eg：01月01日 13时）',
      key: 'M月D日 H时',
      label: 'M月D日 H时（eg：1月1日 13时）',
      contentFormat: 'yyyy-MM-DD HH:mm:ss'
    },
    {
      mode: 'date',
      key0: 'MM月DD日 HH时mm分',
      label0: 'MM月DD日 HH时mm分（eg：01月01日 13时00分）',
      key: 'M月D日 H时m分',
      label: 'M月D日 H时m分（eg：1月1日 13时0分）',
      contentFormat: 'yyyy-MM-DD HH:mm:ss'
    },
    {
      mode: 'date',
      key0: 'MM月DD日 HH时mm分ss秒',
      label0: 'MM月DD日 HH时mm分ss秒（eg：01月01日 13时00分00秒）',
      key: 'M月D日 H时m分s秒',
      label: 'M月D日 H时m分s秒（eg：1月1日 13时0分0秒）',
      contentFormat: 'yyyy-MM-DD HH:mm:ss'
    },
    {
      mode: 'date',
      key0: 'yyyy-MM-DD HH:mm',
      label0: 'yyyy-MM-DD HH:mm（eg：2022-01-01 13:00）',
      key: 'yyyy-M-D H:m',
      label: 'yyyy-M-D H:m（eg：2022-1-1 13:0）',
      contentFormat: 'yyyy-MM-DD HH:mm:ss'
    },
    {
      mode: 'date',
      key0: 'yyyy-MM-DD HH:mm:ss',
      label0: 'yyyy-MM-DD HH:mm:ss（eg：2022-01-01 13:00:00）',
      key: 'yyyy-M-D H:m:s',
      label: 'yyyy-M-D H:m:s（eg：2022-1-1 13:0:0）',
      contentFormat: 'yyyy-MM-DD HH:mm:ss'
    },
    {
      mode: 'date',
      key0: 'yyyy/MM/DD HH:mm:ss',
      label0: 'yyyy/MM/DD HH:mm:ss（eg：2022/01/01 13:00:00）',
      key: 'yyyy/M/D H:m:s',
      label: 'yyyy/M/D H:m:s（eg：2022/1/1 13:0:0）',
      contentFormat: 'yyyy-MM-DD HH:mm:ss'
    },
    {
      mode: 'date',
      key0: 'yyyy/MM/DD HH:mm',
      label0: 'yyyy/MM/DD HH:mm（eg：2022/01/01 13:00）',
      key: 'yyyy/M/D H:m',
      label: 'yyyy/M/D H:m（eg：2022/1/1 13:0）',
      contentFormat: 'yyyy-MM-DD HH:mm:ss'
    },
    {
      mode: 'date',
      key0: 'yyyy年MM月DD日 HH时',
      label0: 'yyyy年MM月DD日 HH时（eg：2022年01月01日 13时）',
      key: 'yyyy年M月D日 H时',
      label: 'yyyy年M月D日 H时（eg：2022年1月1日 13时）',
      contentFormat: 'yyyy-MM-DD HH:mm:ss'
    },
    {
      mode: 'date',
      key0: 'yyyy年MM月DD日 HH时mm分',
      label0: 'yyyy年MM月DD日 HH时mm分（eg：2022年01月01日 13时00分）',
      key: 'yyyy年M月D日 H时m分',
      label: 'yyyy年M月D日 H时m分（eg：2022年1月1日 13时0分）',
      contentFormat: 'yyyy-MM-DD HH:mm:ss'
    },
    {
      mode: 'date',
      key0: 'yyyy年MM月DD日 HH时mm分ss秒',
      label0: 'yyyy年MM月DD日 HH时mm分ss秒（eg：2022年01月01日 13时00分00秒）',
      key: 'yyyy年M月D日 H时m分s秒',
      label: 'yyyy年M月D日 H时m分s秒（eg：2022年1月1日 13时0分0秒）',
      contentFormat: 'yyyy-MM-DD HH:mm:ss'
    }
  ]
};
export function getTypeOptions() {
  return detePatternTypeOptions;
}

export function getOptions() {
  return datePatternOptions;
}
