import { merge, camelCase, set } from 'lodash';
export default function createClientCommonApi() {
  return {
    getAppCodeI18nsApplyTo(applyTo) {
      return new Promise((resolve, reject) => {
        $axios
          .get(`/proxy/api/app/codeI18n/getAppCodeI18nByApplyTo`, {
            params: {
              applyTo: applyTo
            }
          })
          .then(({ data }) => {
            let result = {};
            if (data.data && data.data.length) {
              for (let d of data.data) {
                set(result, d.code, d[camelCase($app.$i18n.locale)]);
              }
              resolve(result);
            } else {
              resolve({});
            }
          })
          .catch(error => {});
      });
    },
    getSystemParamValue(key, callback) {
      return $axios
        .get(`/basicdata/system/param/get`, {
          params: { key: key }
        })
        .then(function (result) {
          if (typeof callback === 'function') {
            callback(result.data.data);
          }
          return result.data.data;
        });
    },

    getWorkTimePeriod(requestParams) {
      return new Promise((resolve, reject) => {
        $axios
          .get(`/proxy/api/ts/work/time/plan/getWorkTimePeriod`, {
            params: {
              workTimePlanUuid: requestParams.workTimePlanUuid,
              fromTime: requestParams.fromTime,
              toTime: requestParams.toTime
            }
          })
          .then(({ data }) => {
            if (data.code == 0) {
              resolve(data.data);
            }
          })
          .catch(error => {});
      });
    },

    getUserNamesByIds(userIds) {
      return new Promise((resolve, reject) => {
        $axios
          .post(`/proxy/api/user/getUserNamesByIds`, userIds)
          .then(({ data }) => {
            if (data.code == 0) {
              console.log('获取用户名称', data);
              resolve(data.data);
            }
          })
          .catch(error => {});
      });
    },

    translate(word, from, to) {
      return new Promise((resolve, reject) => {
        $axios[typeof word == 'string' ? 'get' : 'post'](
          '/proxy/api/translate/text',
          typeof word == 'string' ? { params: { word: encodeURIComponent(word), from, to } } : { word, from, to }
        )
          .then(({ data }) => {
            if (data.code == 0) {
              resolve(data.data);
            }
          })
          .catch(error => {
            console.error('翻译服务异常:', error);
            reject();
          });
      });
    },

    getAMapAddress() {
      return new Promise((resolve, reject) => {
        const getAddress = () => {
          return new Promise((resolve, reject) => {
            // this.geolocation.on("complete", (result) => {
            //   this.geocoder.getAddress(result.position, (status, result) => {
            //     if (status === 'complete' && result.info === 'OK') {
            //       resolve(result.regeocode)
            //     }
            //   });
            // }).on("error", (err) => {
            //   reject(err)
            // });

            this.geolocation.getCurrentPosition((status, result) => {
              if (status === 'complete') {
                resolve(result);
              } else {
                reject(result);
              }
            });
          });
        };

        const initAMap = () => {
          return new Promise((resolve, reject) => {
            import('@amap/amap-jsapi-loader').then(AMapLoader => {
              $axios.get('/webConfig/getAmapConfig').then(({ data: appConfig }) => {
                this.appConfig = appConfig;
                this.AMapLoader = AMapLoader;
                resolve();
              });
            });
          });
        };

        const loadAMap = () => {
          return new Promise((resolve, reject) => {
            window._AMapSecurityConfig = {
              securityJsCode: this.appConfig.securityJsCode
            };
            this.AMapLoader.load({
              key: this.appConfig.key, // 申请好的Web端开发者Key，首次调用 load 时必填
              version: this.appConfig.version || '2.0', // 指定要加载的 JSAPI 的版本，缺省时默认为 1.4.15
              plugins: ['AMap.Geolocation'] //需要使用的的插件列表，如比例尺'AMap.Scale'，支持添加多个如：['...','...']
            }).then(AMap => {
              this.AMap = AMap;
              this.geolocation = new AMap.Geolocation({
                needAddress: true,
                getCityWhenFail: true
              });
              // this.geocoder = new AMap.Geocoder();
              resolve();
            });
          });
        };

        if (this.geolocation) {
          getAddress().then(
            address => {
              resolve(address);
            },
            error => {
              reject(error);
            }
          );
        } else {
          initAMap().then(() => {
            loadAMap().then(() => {
              getAddress().then(
                address => {
                  resolve(address);
                },
                error => {
                  reject(error);
                }
              );
            });
          });
        }
      });
    }
  };
}
