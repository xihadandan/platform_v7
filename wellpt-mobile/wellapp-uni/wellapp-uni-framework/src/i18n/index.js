import Vue from "vue";
import VueI18n from "vue-i18n";
Vue.use(VueI18n);

const locale = uni.getStorageSync("locale") || "zh_CN"; //获取缓存中的语言"zh_CN"

let message = {};

// VueI18n构造函数所需要的配置
const i18nConfig = {
  locale, //当前语言
  fallbackLocale: "zh_CN",
  fallbackRoot: false,
  messages: {
    [locale]: message,
  },
};
const i18n = new VueI18n(i18nConfig);

i18n.$t = (_this, key, value) => {
  var params = {};
  if (value && value != undefined && typeof value !== "string") {
    params = value;
  }
  if (_this.$te(key)) {
    return _this.$i18n.t.apply(_this.$i18n, [key, params]);
  }
  return value || "";
};

// 设置本地国际化数据
i18n.setLocaleMessageByCode = (_this, code, data) => {
  let allMessage = _this.$i18n._getMessages();
  allMessage = allMessage[_this.$i18n.locale];
  if (data) {
    allMessage[code] = data;
  } else {
    delete allMessage[code];
  }
  _this.$i18n.setLocaleMessage(_this.$i18n.locale, allMessage);
};

// 根据map字段将msg替换成国际化
i18n.getMsgI18nsByMap = (_this, msgMap, msg, prefix) => {
  let msgI18n = msg;
  if (!msg) {
    return msgI18n;
  }
  for (const key in msgMap) {
    if (msg.indexOf(msgMap[key]) !== -1) {
      msgI18n = msg.replace(new RegExp(msgMap[key], "g"), _this.$t(`${prefix}.${key}`, msgMap[key]));
    }
  }
  return msgI18n;
};

// 将i18ns格式的i18n，实例：fileSourceOptions
i18n.i18nsToI18n = (_this, i18ns, code = "code") => {
  let i18n = {};
  for (let m in i18ns) {
    i18ns.forEach((item) => {
      if (!i18n[item.locale]) {
        i18n[item.locale] = {};
      }
      if (item.elementId && item[code].indexOf(item.elementId) > -1) {
        item[code] = item[code].split(".");
        item[code].splice(item[code].indexOf(item.elementId), 1);
        item[code].join(".");
      }
      i18n[item.locale][item[code]] = item.content;
    });
  }
  return i18n;
};
// 合并语言包
i18n.mergeWidgetI18nMessages = (_this, i18n, id) => {
  let allMessage = _this.$i18n._getMessages();
  if (i18n != undefined) {
    for (let m in i18n) {
      if (m == _this.$i18n.locale) {
        let I18nMsg = allMessage[m];
        if (!I18nMsg) {
          I18nMsg = {};
        }
        if (!I18nMsg[id]) {
          I18nMsg[id] = {};
        }
        _this.$i18n.mergeLocaleMessage(m, { [id]: i18n[m] });
      }
    }
  }
};

/**
 * 翻译服务调用
 * @param {*} word 字符串或者字符串数组
 * @param {*} from 源文本国家编码，如: zh , en
 * @param {*} to 翻译为目标国家编码
 * @returns 当 word 为数组时候，返回 map 对象，否则返回翻译后的文本
 */
i18n.$translate = function (_this, word, from, to) {
  return new Promise((resolve, reject) => {
    _this.$axios[typeof word == "string" ? "get" : "post"](
      "/api/translate/text",
      typeof word == "string" ? { params: { word, from, to } } : { word, from, to }
    )
      .then(({ data }) => {
        if (data.code == 0) {
          resolve(data.data);
        }
      })
      .catch((error) => {
        console.error("翻译服务异常:", error);
        reject();
      });
  });
};

export default i18n;
