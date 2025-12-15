import appContext from "./app-context";
import DataStore from "./datastore";
import device from "./device";
import dmsDataServices from "./dms-data-services";
import TableViewGetCount from "./datastore/TableViewGetCount.js";
import { dataStoreCount, dataModelCount } from "./datastore/dataStoreCount.js";
import errorHandler from "./error-handler";
import uniRequest from "./request";
import storage from "./storage";
import theme from "./theme";
import { install as installOrgSelect } from "./unit";
import { install as installPopup } from "./popup";
import { install as installToast } from "./toast";
import pageContext from "./page-context";
import { install as installAxios } from "./axios";
import utils from "./utils";
import uView from "uview-ui";
import workFlowUtils from "./work-flow-utils";
import manifestUtils from "./manifest-utils";
import fileApi from "./file";
import { install as extend } from "./extends";
import i18n from "./i18n";

const install = function (Vue) {
  Vue.use(installAxios);
  Vue.use(uView);

  Vue.use(extend);
  Vue.use(installOrgSelect);
  Vue.use(installPopup);
  Vue.use(installToast);

  // FIXME: 主题由7.0主题服务获取
  // theme.install(Vue);

  // 注册应用功能
  appContext.registerAppFunctions();
};

export {
  appContext,
  DataStore,
  device,
  dmsDataServices,
  TableViewGetCount,
  dataStoreCount,
  dataModelCount,
  errorHandler,
  uniRequest,
  storage,
  pageContext,
  utils,
  workFlowUtils,
  manifestUtils,
  fileApi,
  install,
  i18n,
};
