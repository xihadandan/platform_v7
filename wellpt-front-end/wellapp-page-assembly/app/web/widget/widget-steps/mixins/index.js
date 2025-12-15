import { generateId } from '@framework/vue/utils/util';
import { template as stringTemplate } from 'lodash';
/* 
  let compiler = stringTemplate('/sys/${systemId}/_/workflow-designer/index?uuid=${uuid}&system_id=${systemId}')
  let url = compiler({
      uuid:'249492782471708672',
      systemId: 'prod_20230921170909'
  })
  console.log(url) */
export default {
  methods: {
    // 获取数据仓库
    fetchDataStore(options, callback) {
      this.dataSourceProvider = undefined
      this.getLabelValueOptionByDataSource(options, (o, res) => {
        if (typeof callback === 'function') {
          callback(res)
        }
      });
    },
    formatDataStore(res, options, callback) {
      let steps = [];
      for (let index = 0; index < res.length; index++) {
        const item = res[index];
        let disabled = false;
        const status = this.getStatus(item, options.dataSourceStatusConditions, item[options.dataSourceStatusColumn]);
        if (status === 'wait') {
          disabled = true;
        }
        let stepItem = {
          id: generateId(),
          wtype: 'WidgetStepItem',
          title: item[options.dataSourceLabelColumn],
          subTitle: item[options.dataSourceSubTileColumn],
          description: item[options.dataSourceDescColumn],
          disabled,
          status,
          style: {
            icon: undefined
          }
        };
        if (options.pageType === 'page') {
          stepItem.configuration = {
            eventHandler: {
              pageType: 'page',
              pageId: item[options.dataSourcePageId]
            }
          };
        } else {
          let renderUrl;
          if (options.dataSourceRenderUrl) {
            const compiler = stringTemplate(options.dataSourceRenderUrl);
            renderUrl = compiler(item || {});
          }

          stepItem.configuration = {
            url: renderUrl
          };
        }

        steps.push(stepItem);
      }
      this.steps = steps;
      if (typeof callback === 'function') {
        callback(steps)
      }
    },
    // 获取数据模型
    fetchDataModel(options, callback) {
      this.dataSourceProvider = undefined
      options.dataModelUuid = options.uuid;
      this.getLabelValueOptionByDataModel(options, (o, res) => {
        if (typeof callback === 'function') {
          callback(res)
        }
      });
    },
    formatDataModel(res, options, callback) {
      let steps = [];
      for (let index = 0; index < res.length; index++) {
        const item = res[index];
        let disabled = false;
        const status = this.getStatus(item, options.statusConditions, item[options.statusColumn]);
        if (status === 'wait') {
          disabled = true;
        }
        let stepItem = {
          id: generateId(),
          wtype: 'WidgetStepItem',
          title: item[options.labelColumn],
          subTitle: item[options.subTileColumn],
          description: item[options.descColumn],
          disabled,
          status,
          style: {
            icon: undefined
          }
        };
        if (options.pageType === 'page') {
          stepItem.configuration = {
            eventHandler: {
              pageType: 'page',
              pageId: item[options.pageId]
            }
          };
        } else {
          let renderUrl;
          if (options.renderUrl) {
            const compiler = stringTemplate(options.renderUrl);
            renderUrl = compiler(item || {});
          }

          stepItem.configuration = {
            url: renderUrl
          };
        }

        steps.push(stepItem);
      }
      this.steps = steps;
      if (typeof callback === 'function') {
        callback(steps)
      }
    },
    getStatus(item, statusConditions, statusValue) {
      let status = 'wait';
      const hasItem = statusConditions.find(s => {
        return s.value === statusValue;
      });
      if (hasItem) {
        status = hasItem.status;
      }
      return status;
    },

  }
}