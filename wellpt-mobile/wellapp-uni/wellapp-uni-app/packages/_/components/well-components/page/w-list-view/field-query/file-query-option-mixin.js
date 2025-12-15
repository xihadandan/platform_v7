import { map, each as forEach, groupBy } from "lodash";
import { DataStore, storage } from "wellapp-uni-framework";
module.exports = {
  inject: ["widgetListContext"],
  props: {
    criterion: Object,
    cacheKey: String,
  },
  data() {
    return {
      items: [],
      options: {},
    };
  },
  created() {
    let _self = this;
    let queryOptions = _self.criterion.queryOptions || {};
    let options = queryOptions.options || {};
    this.options = options;
    this.criterion.label = this.$t(this.criterion.uuid, this.criterion.label);
    if (this.cacheKey) {
      storage.getStorageCache(
        this.cacheKey,
        () => {
          return new Promise((resolve, reject) => {
            this.fetchOptions().then((data) => {
              resolve(data);
            });
          });
        },
        (items) => {
          this.items = items;
        }
      );
    }
  },
  methods: {
    getDisplayValue() {
      const _self = this;
      let values = (_self.criterion.value && _self.criterion.value.split(";")) || [];
      let displayValue = [];
      forEach(_self.items, function (item) {
        if (values.indexOf(item.value) >= 0) {
          displayValue.push(item.text);
        }
      });
      return displayValue.join(";");
    },
    itemsComplete() {},
    $t() {
      if (this.widgetListContext != undefined) {
        return this.widgetListContext.$t(...arguments);
      }
      return this.$i18n.t(...arguments);
    },
    fetchOptions() {
      let _self = this;
      let queryType = _self.criterion.queryType;
      let optionType = this.options.type;
      this.items.splice(0, this.items.length);
      return new Promise((resolve, reject) => {
        if (["select2", "select", "checkbox", "radio", "treeSelect"].indexOf(queryType) != -1) {
          if (optionType == "selfDefine") {
            resolve(
              map(this.options.defineOptions || [], (item) => {
                return {
                  text: item.label,
                  value: item.value,
                };
              })
            );
          } else if (optionType == "dataDictionary") {
            this.fetchSelectOptionByDataDic(this.options.dataDictionaryUuid).then((data) => {
              resolve(data);
            });
          } else if (optionType == "dataSource") {
            if (queryType == "treeSelect") {
              this.fetchDataSourceDataToTreeOption(this.options).then((data) => {
                resolve(data);
              });
            } else {
              this.fetchSelectOptionByDataSource(this.options).then((data) => {
                resolve(data);
              });
            }
          }
        } else if (queryType == "groupSelect") {
          this.fetchDataSourceData(
            this.options.dataSourceId,
            this.options.defaultCondition ? [{ sql: this.options.defaultCondition }] : []
          ).then((results) => {
            if (results) {
              resolve(this.convertList2GroupOptions(results, this.options));
            }
          });
        }
      });
    },
    convertList2GroupOptions(list, opt) {
      let options = [];
      let { dataSourceValueColumn, dataSourceLabelColumn, dataSourceGroupColumn } = opt;
      let groupMap = groupBy(list, (d) => {
        return d[dataSourceGroupColumn];
      });
      for (let g in groupMap) {
        let group = {
          label: g,
          options: [],
        };
        let children = groupMap[g];
        for (let i = 0, len = children.length; i < len; i++) {
          group.options.push({
            text: children[i][dataSourceLabelColumn],
            value: children[i][dataSourceValueColumn],
          });
        }
        options.push(group);
      }

      return options;
    },

    fetchSelectOptionByDataSource(options) {
      return new Promise((resolve, reject) => {
        this.$axios
          .post(
            "/common/select2/query?serviceName=select2DataStoreQueryService&queryMethod=loadSelectData&dataStoreId=" +
              options.dataSourceId +
              "&idColumnIndex=" +
              options.dataSourceValueColumn +
              "&textColumnIndex=" +
              options.dataSourceLabelColumn,
            {}
          )
          .then(({ data }) => {
            let o = [];
            if (data.results && data.results.length) {
              for (let i = 0, len = data.results.length; i < len; i++) {
                o.push({
                  value: data.results[i].id,
                  text: data.results[i].text,
                });
              }
              resolve(o);
            }
          });
      });
    },
    fetchDataSourceData(dataSourceId, defaultCriterions) {
      return new Promise((resolve, reject) => {
        this.dataSourceProvider = new DataStore({
          dataStoreId: dataSourceId,
          onDataChange: function (data, count, params) {
            let results = data.data;
            resolve(results);
          },
          receiver: this,
          defaultCriterions,
          pageSize: -1,
        });
        this.dataSourceProvider.load();
      });
    },

    fetchDataSourceDataToTreeOption(options) {
      return new Promise((resolve, reject) => {
        this.$axios
          .post("/json/data/services", {
            serviceName: "cdDataStoreService",
            methodName: "loadTreeNodes",
            args: JSON.stringify([
              {
                dataStoreId: options.dataSourceId,
                uniqueColumn: options.dataSourceKeyColumn,
                parentColumn: options.dataSourceParentColumn,
                displayColumn: options.dataSourceLabelColumn,
                valueColumn: options.dataSourceValueColumn,
                async: false,
              },
            ]),
          })
          .then(({ data }) => {
            if (data.code == 0) {
              let treeNodeValueReplace = (list) => {
                if (list) {
                  for (let i = 0, len = list.length; i < len; i++) {
                    treeNodeValueReplace(list[i].children);
                    list[i].id = list[i].data[options.dataSourceValueColumn];
                  }
                }
              };
              treeNodeValueReplace(data.data);
              resolve(data.data);
            }
          });
      });
    },
    fetchSelectOptionByDataDic(dataDicUuid) {
      let _this = this;
      return new Promise((resolve, reject) => {
        this.$axios
          .post("/json/data/services", {
            serviceName: "cdDataDictionaryFacadeService",
            methodName: "listItemByDictionaryCode",
            args: JSON.stringify([dataDicUuid]),
          })
          .then(({ data }) => {
            if (data.code == 0 && data.data) {
              let options = data.data;
              resolve(
                map(options, function (data) {
                  return {
                    value: data.value,
                    text: data.label,
                  };
                })
              );
            }
          });
      });
    },
  },
};
