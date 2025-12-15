export const langsOptions = {
  "zh-CN": "简体中文(中国)",
  "zh-TW": "繁体中文(台湾地区)",
  "zh-HK": "繁体中文(香港)",
  "en-HK": "英语(香港)",
  "en-US": "英语(美国)",
  "en-GB": "英语(英国)",
  "en-WW": "英语(全球)",
  "en-CA": "英语(加拿大)",
  "en-AU": "英语(澳大利亚)",
  "en-IE": "英语(爱尔兰)",
  "en-FI": "英语(芬兰)",
  "fi-FI": "芬兰语(芬兰)",
  "en-DK": "英语(丹麦)",
  "da-DK": "丹麦语(丹麦)",
  "en-IL": "英语(以色列)",
  "he-IL": "希伯来语(以色列)",
  "en-ZA": "英语(南非)",
  "en-IN": "英语(印度)",
  "en-NO": "英语(挪威)",
  "en-SG": "英语(新加坡)",
  "en-NZ": "英语(新西兰)",
  "en-ID": "英语(印度尼西亚)",
  "en-PH": "英语(菲律宾)",
  "en-TH": "英语(泰国)",
  "en-MY": "英语(马来西亚)",
  "en-XA": "英语(阿拉伯)",
  "ko-KR": "韩文(韩国)",
  "ja-JP": "日语(日本)",
  "nl-NL": "荷兰语(荷兰)",
  "nl-BE": "荷兰语(比利时)",
  "pt-PT": "葡萄牙语(葡萄牙)",
  "pt-BR": "葡萄牙语(巴西)",
  "fr-FR": "法语(法国)",
  "fr-LU": "法语(卢森堡)",
  "fr-CH": "法语(瑞士)",
  "fr-BE": "法语(比利时)",
  "fr-CA": "法语(加拿大)",
  "es-LA": "西班牙语(拉丁美洲)",
  "es-ES": "西班牙语(西班牙)",
  "es-AR": "西班牙语(阿根廷)",
  "es-US": "西班牙语(美国)",
  "es-MX": "西班牙语(墨西哥)",
  "es-CO": "西班牙语(哥伦比亚)",
  "es-PR": "西班牙语(波多黎各)",
  "de-DE": "德语(德国)",
  "de-AT": "德语(奥地利)",
  "de-CH": "德语(瑞士)",
  "ru-RU": "俄语(俄罗斯)",
  "it-IT": "意大利语(意大利)",
  "el-GR": "希腊语(希腊)",
  "no-NO": "挪威语(挪威)",
  "hu-HU": "匈牙利语(匈牙利)",
  "tr-TR": "土耳其语(土耳其)",
  "cs-CZ": "捷克语(捷克共和国)",
  "sl-SL": "斯洛文尼亚语",
  "pl-PL": "波兰语(波兰)",
  "sv-SE": "瑞典语(瑞典)",
  "es-CL": "西班牙语(智利)"
}

// 获取节假日管理，日期选择下拉框选项
export const getDateData = function () {
  var lunarInfo = [
    0x0ab50, //1899
    0x04bd8,
    0x04ae0,
    0x0a570,
    0x054d5,
    0x0d260,
    0x0d950,
    0x16554,
    0x056a0,
    0x09ad0,
    0x055d2, //1900-1909
    0x04ae0,
    0x0a5b6,
    0x0a4d0,
    0x0d250,
    0x1d255,
    0x0b540,
    0x0d6a0,
    0x0ada2,
    0x095b0,
    0x14977, //1910-1919
    0x04970,
    0x0a4b0,
    0x0b4b5,
    0x06a50,
    0x06d40,
    0x1ab54,
    0x02b60,
    0x09570,
    0x052f2,
    0x04970, //1920-1929
    0x06566,
    0x0d4a0,
    0x0ea50,
    0x06e95,
    0x05ad0,
    0x02b60,
    0x186e3,
    0x092e0,
    0x1c8d7,
    0x0c950, //1930-1939
    0x0d4a0,
    0x1d8a6,
    0x0b550,
    0x056a0,
    0x1a5b4,
    0x025d0,
    0x092d0,
    0x0d2b2,
    0x0a950,
    0x0b557, //1940-1949
    0x06ca0,
    0x0b550,
    0x15355,
    0x04da0,
    0x0a5b0,
    0x14573,
    0x052b0,
    0x0a9a8,
    0x0e950,
    0x06aa0, //1950-1959
    0x0aea6,
    0x0ab50,
    0x04b60,
    0x0aae4,
    0x0a570,
    0x05260,
    0x0f263,
    0x0d950,
    0x05b57,
    0x056a0, //1960-1969
    0x096d0,
    0x04dd5,
    0x04ad0,
    0x0a4d0,
    0x0d4d4,
    0x0d250,
    0x0d558,
    0x0b540,
    0x0b6a0,
    0x195a6, //1970-1979
    0x095b0,
    0x049b0,
    0x0a974,
    0x0a4b0,
    0x0b27a,
    0x06a50,
    0x06d40,
    0x0af46,
    0x0ab60,
    0x09570, //1980-1989
    0x04af5,
    0x04970,
    0x064b0,
    0x074a3,
    0x0ea50,
    0x06b58,
    0x055c0,
    0x0ab60,
    0x096d5,
    0x092e0, //1990-1999
    0x0c960,
    0x0d954,
    0x0d4a0,
    0x0da50,
    0x07552,
    0x056a0,
    0x0abb7,
    0x025d0,
    0x092d0,
    0x0cab5, //2000-2009
    0x0a950,
    0x0b4a0,
    0x0baa4,
    0x0ad50,
    0x055d9,
    0x04ba0,
    0x0a5b0,
    0x15176,
    0x052b0,
    0x0a930, //2010-2019
    0x07954,
    0x06aa0,
    0x0ad50,
    0x05b52,
    0x04b60,
    0x0a6e6,
    0x0a4e0,
    0x0d260,
    0x0ea65,
    0x0d530, //2020-2029
    0x05aa0,
    0x076a3,
    0x096d0,
    0x04bd7,
    0x04ad0,
    0x0a4d0,
    0x1d0b6,
    0x0d250,
    0x0d520,
    0x0dd45, //2030-2039
    0x0b5a0,
    0x056d0,
    0x055b2,
    0x049b0,
    0x0a577,
    0x0a4b0,
    0x0aa50,
    0x1b255,
    0x06d20,
    0x0ada0, //2040-2049
    0x14b63,
    0x09370,
    0x049f8,
    0x04970,
    0x064b0,
    0x168a6,
    0x0ea50,
    0x06b20,
    0x1a6c4,
    0x0aae0, //2050-2059
    0x0a2e0,
    0x0d2e3,
    0x0c960,
    0x0d557,
    0x0d4a0,
    0x0da50,
    0x05d55,
    0x056a0,
    0x0a6d0,
    0x055d4, //2060-2069
    0x052d0,
    0x0a9b8,
    0x0a950,
    0x0b4a0,
    0x0b6a6,
    0x0ad50,
    0x055a0,
    0x0aba4,
    0x0a5b0,
    0x052b0, //2070-2079
    0x0b273,
    0x06930,
    0x07337,
    0x06aa0,
    0x0ad50,
    0x14b55,
    0x04b60,
    0x0a570,
    0x054e4,
    0x0d160, //2080-2089
    0x0e968,
    0x0d520,
    0x0daa0,
    0x16aa6,
    0x056d0,
    0x04ae0,
    0x0a9d4,
    0x0a2d0,
    0x0d150,
    0x0f252, //2090-2099
    0x0d520
  ]; //2100
  var year = new Date().getFullYear();
  var dateData = {
    // 日期数据
    ying: {
      mon: ['正月', '二月', '三月', '四月', '五月', '六月', '七月', '八月', '九月', '十月', '十一月', '十二月'],
      num: monthDays(year, lunarInfo),
      day: [
        '初一',
        '初二',
        '初三',
        '初四',
        '初五',
        '初六',
        '初七',
        '初八',
        '初九',
        '初十',
        '十一',
        '十二',
        '十三',
        '十四',
        '十五',
        '十六',
        '十七',
        '十八',
        '十九',
        '二十',
        '廿一',
        '廿二',
        '廿三',
        '廿四',
        '廿五',
        '廿六',
        '廿七',
        '廿八',
        '廿九',
        '三十'
      ],
      data: []
    },
    yang: {
      num: [31, isLeap(year) ? 29 : 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31],
      data: []
    }
  };

  var obj = {
    checked: false,
    children: [],
    data: null,
    iconSkin: null,
    iconStyle: null,
    id: '',
    isParent: false,
    name: '',
    nocheck: false,
    nodeLevel: 0,
    open: false,
    path: '',
    total: 0,
    type: null,
    url: null
  };

  for (var i in dateData) {
    dateData[i] = getDateOptionsData(dateData[i], obj, 12, i);
  }

  return dateData;
}
function getDateOptionsData(data, obj, num, type, date, parent, day) {
  for (var i = 0; i < num; i++) {
    var newObj = _.cloneDeep(obj);
    newObj.id = setDateVal(i - 0 + 1);
    newObj.name = date ? (type == 'ying' ? day[i] : i - 0 + 1 + '日') : type == 'ying' ? data.mon[i] : i - 0 + 1 + '月';
    newObj.path = parent ? parent.path + '/' + newObj.name : newObj.name;
    if (data.num && data.num.length > 0) {
      getDateOptionsData(newObj.children, obj, data.num[i], type, true, newObj, data.day);
    } else {
      delete newObj.children;
    }
    if (data.data) {
      data.data.push(newObj);
    } else {
      data.push(newObj);
    }
  }
  return data;
}
function setDateVal(val) {
  return val > 9 ? val : '0' + val;
}
// 获取农历指定月份的天数
function monthDays(y, lunarInfo) {
  var num = [];
  for (var i = 1; i < 13; i++) {
    num.push(lunarInfo[y - 1899] & (0x10000 >> i) ? 30 : 29);
  }
  return num;
}
// 判断是闰年还是平年
function isLeap(y) {
  return (y % 4 == 0 && y % 100 != 0) || y % 400 == 0;
}

// 工作时间计划
export const workingTimePlanPeriodType = {
  1: '全年', 2: '指定时间周期'
}
export const workingTimePlanwWorkTimeType = {
  1: '固定工时', 2: '单双周', 3: '弹性工时'
}
export const workingTimePlanWorkDay = {
  MON: '周一', TUE: '周二', WED: '周三', THU: '周四', FRI: '周五', SAT: '周六', SUN: '周日'
}

export const hoursOfDay = function () {
  return Array.from({ length: 24 }, (v, k) => k);
}
export const minutesOfHour = function () {
  return Array.from({ length: 60 }, (v, k) => k);
}
export const secondsOfMinute = function () {
  return Array.from({ length: 60 }, (v, k) => k);
}

