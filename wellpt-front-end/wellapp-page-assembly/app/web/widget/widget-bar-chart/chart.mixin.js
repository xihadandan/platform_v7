import DataSourceBase from '@pageAssembly/app/web/assets/js/commons/dataSource.base.js';
import { template as stringTemplate, cloneDeep } from 'lodash';

export default {
  computed: {
    vContainerHeight() {
      let height = this.widget.configuration.style.height;
      if (height != undefined) {
        return typeof height == 'number' ? `${height}px` : height;
      }
      return 'auto';
    },
    vContainerWidth() {
      let width = this.widget.configuration.style.width;
      if (width != undefined) {
        return typeof width == 'number' ? `${width}px` : width;
      }
      return '100%';
    },
    vSize() {
      return this.vContainerHeight + this.vContainerWidth;
    }
  },
  data() {
    return {
      loading: true,
      params: {},
      dataset: []
    };
  },
  mounted() {
    import('echarts').then(echarts => {
      this.loading = false;
      this.echarts = echarts;
      this.chart = echarts.init(this.$el.querySelector('div'), undefined, {
        renderer: this.widget.configuration.renderer || 'canvas'
      }); // 绘制图表
      this.chart.showLoading('default', { text: '加载中', fontSize: 12, textColor: '#000' });
      this.initEvents();
      this.initChart();
    });
  },
  methods: {
    getDataSourceDefaultCondition() {
      if (this.widget.configuration.defaultCondition) {
        let compiler = stringTemplate(this.widget.configuration.defaultCondition);
        let sql = this.widget.configuration.defaultCondition;
        try {
          // 在表单域内
          let data = cloneDeep(this.vPageState || {});
          sql = compiler(data);
        } catch (error) {
          console.error('解析模板字符串错误: ', error);
          throw new Error('表格默认条件变量解析异常');
        }
        return [{ sql }];
      }
      return [];
    },
    getDataSourceProvider(dataCallback) {
      if (this.dataSourceProvider == undefined) {
        // 创建数据源
        let dsOptions = {
          onDataChange: function (data, count, params) {
            if (typeof dataCallback == 'function') {
              dataCallback(data);
            }
          },
          receiver: this,
          params: {},
          defaultCriterions: this.widget.configuration.enableDefaultCondition ? this.getDataSourceDefaultCondition() : [],
          pageSize: -1
        };
        if (this.widget.configuration.dataSourceType == 'dataSource' && this.widget.configuration.dataSourceId) {
          dsOptions.dataStoreId = this.widget.configuration.dataSourceId;
          this.dataSourceProvider = new DataSourceBase(dsOptions);
        } else if (this.widget.configuration.dataSourceType == 'dataModel' && this.widget.configuration.dataModelUuid) {
          dsOptions.loadDataUrl = '/proxy/api/dm/loadData/' + this.widget.configuration.dataModelUuid;
          dsOptions.loadDataCntUrl = '/proxy/api/dm/loadDataCount/' + this.widget.configuration.dataModelUuid;
          this.dataSourceProvider = new DataSourceBase(dsOptions);
        }
      }
      return this.dataSourceProvider;
    },
    startTimeoutRefreshChart(timeout) {
      if (!this.designMode) {
        setTimeout(() => {
          this.initChart();
        }, timeout);
      }
    },
    initChart() {
      let { autoRefresh, refreshWaitSeconds } = this.widget.configuration;
      this.loadDataset().then(() => {
        this.convertSeriesOptions().then(series => {
          this.initOption({
            dataset: this.dataset,
            series,
            ...this.convertAxisOptions(),
            ...this.convertTitleOptions(),
            ...this.convertLegendOptions()
          });
          if (autoRefresh) {
            this.startTimeoutRefreshChart(1000 * refreshWaitSeconds);
          }
        });
      });
    },
    handleResize() {
      if (this.chart) {
        this.chart.resize();
      }
    },
    initOption(option) {
      let _option = { legend: {}, tooltip: {}, ...option };
      if (this.widget.configuration.color && this.widget.configuration.color.length) {
        _option.color = JSON.parse(JSON.stringify(this.widget.configuration.color));
      }
      console.log('图表初始化配置', _option);
      this.invokeDevelopmentMethod('beforeChartRender', _option);
      this.chart.setOption(_option);
      this.chart.hideLoading();
    },
    initEvents() {
      let _this = this;
      this.chart.on('click', function (chartParams) {
        let data = {};
        for (let key in chartParams) {
          if (key != 'event') {
            data[key] = chartParams[key];
          }
        }
        data = JSON.parse(JSON.stringify(data));
        console.log('点击图表入参', chartParams);
        _this.triggerDomEvent('onClick', data, data);
      });

      this.chart.on('finished', function () {
        _this.invokeDevelopmentMethod('afterChartFinished');
      });

      this.chart.on('rendered', function () {
        _this.invokeDevelopmentMethod('afterChartRendered');
      });
    },

    convertLegendOptions() {
      let legend = JSON.parse(JSON.stringify(this.widget.configuration.legendConfig));
      legend.selectedMode = legend.selectedMode === 'false' ? false : legend.selectedMode;
      ['left', 'top', 'right', 'bottom', 'width', 'height'].forEach(b => {
        if (legend[b + 'Unit'] == '%' && legend[b] != null) {
          legend[b] = legend[b] + '%';
        }
        if (legend[b + 'Unit'] == 'auto' || (legend[b] == null && (b == 'width' || b == 'height'))) {
          legend[b] = 'auto';
        }
        delete legend[b + 'Unit'];
        if (legend[b] == null || legend[b] == undefined) {
          delete legend[b];
        }
      });
      if (legend.formatter != undefined) {
        try {
          let func = new Function('name', legend.formatter);
          legend.formatter = function (name) {
            return func(name);
          };
        } catch (error) {}
      }
      return { legend };
    },
    convertTitleOptions() {
      let title = JSON.parse(JSON.stringify(this.widget.configuration.titleConfig));
      if (title.text) {
        title.text = this.$t('title', title.text);
      }
      if (title.subtext) {
        title.subtext = this.$t('subTitle', title.subtext);
      }
      ['left', 'top', 'right', 'bottom'].forEach(b => {
        let unit = title[b + 'Unit'];
        if (unit != '%' && unit != 'px') {
          title[b] = unit;
        } else if (unit == '%' && title[b] != null) {
          title[b] = title[b] + '%';
        }
        delete title[b + 'Unit'];
        if (title[b] == null || title[b] == undefined) {
          delete title[b];
        }
      });

      return { title };
    },

    convertAxisOptions() {
      if (this.widget.configuration.axis != undefined) {
        let xAxis = [],
          yAxis = [],
          grid = [],
          axis = JSON.parse(JSON.stringify(this.widget.configuration.axis));
        axis.forEach(a => {
          xAxis.push(a.x);
          yAxis.push(a.y);
          ['left', 'top', 'right', 'bottom', 'width', 'height'].forEach(b => {
            if (a.grid[b + 'Unit'] == '%' && a.grid[b] != null) {
              a.grid[b] = a.grid[b] + '%';
            }
            if (a.grid[b + 'Unit'] == 'auto' || (a.grid[b] == null && (b == 'width' || b == 'height'))) {
              a.grid[b] = 'auto';
            }

            delete a.grid[b + 'Unit'];
            if (a.grid[b] == null || a.grid[b] == undefined) {
              delete a.grid[b];
            }
          });
          grid.push(a.grid);
        });
        return {
          xAxis,
          yAxis,
          grid
        };
      }
      return {};
    },
    convertSeriesOptions() {
      let series = JSON.parse(JSON.stringify(this.widget.configuration.series)),
        dataPromise = [];
      series.forEach(s => {
        if (s.itemStyle) {
          if (s.itemStyle.color != undefined && !s.itemStyle.color.startsWith('#')) {
            // 转换为颜色函数
            try {
              let func = new Function('params', s.itemStyle.color);
              s.itemStyle.color = function (params) {
                return func(params);
              };
            } catch (error) {}
          }
        }
        if (s.label) {
          if (s.label.show && s.label.formatter) {
            // 转换为颜色函数
            try {
              let func = new Function('params', s.label.formatter);
              s.label.formatter = function (params) {
                return func(params);
              };
            } catch (error) {}
          }
          if (s.type == 'pie') {
            s.minAngle = 1; // 数据量过小，也能显示出来
            if (s.label.position == 'center') {
              // 中心显示label需要修改标签配置
              s.label.show = false;
              s.emphasis = {
                label: {
                  show: true
                }
              };
              if (s.label.formatter != undefined) {
                s.emphasis.label.formatter = s.label.formatter;
              }
            }
          }
        }
        if (s.type == 'bar') {
          if (s.barWidth != undefined && s.barWidthUnit == '%') {
            s.barWidth = `${s.barWidth}%`;
          }
          delete s.barWidthUnit;
        }

        let dataFrom = s.dataFrom;
        let { dataJson, from, service, type } = dataFrom;
        delete s.dataFrom;
        if (type == 'data') {
          if (from == 'static' && dataJson) {
            try {
              s.data = new Function('return ' + dataJson)() || [];
            } catch (error) {
              console.error('系列的数据异常: ', error);
            }
          } else if (from == 'service' && service) {
            dataPromise.push(
              new Promise((resolve, reject) => {
                this.fetchDataByService(service, this.params, 'loadSeriesData').then(d => {
                  s.data = d || [];
                  resolve();
                });
              })
            );
          }
        }

        if (
          ['dataSource', 'dataModel'].includes(this.widget.configuration.dataSourceType) &&
          type == 'dataset' &&
          s.type == 'pie' &&
          s.dataNameProp &&
          s.dataValueProp
        ) {
          // 饼图需要把数据仓库的数据集转换成饼图的数据
          let dataset = this.dataset[s.datasetIndex];
          if (dataset && dataset.source && dataset.source.length > 0) {
            delete s.encode;
            s.data = dataset.source.map(d => {
              return {
                name: d[s.dataNameProp],
                value: d[s.dataValueProp]
              };
            });
          }
        }

        if (s.areaStyle != undefined) {
          if (!s.areaStyle.enable) {
            delete s.areaStyle;
          } else {
            delete s.areaStyle.enable;
          }
        }
        ['center', 'radius'].forEach(c => {
          if (s[c] != undefined) {
            for (let i = 0, len = s[c + 'Unit'].length; i < len; i++) {
              s[c][i] = s[c + 'Unit'][i] == 'px' ? s[c][i] : s[c][i] + '%';
            }
          }
        });
      });

      return new Promise((resolve, reject) => {
        if (dataPromise.length) {
          Promise.all(dataPromise).then(() => {
            resolve(series);
          });
        } else {
          resolve(series);
        }
      });
    },
    fetchDataByService(serviceName, params, methodName = 'loadDataset') {
      let reqParams = params || {};
      this.invokeDevelopmentMethod('beforeLoadData', reqParams);
      return new Promise((resolve, reject) => {
        $axios
          .post('/json/data/services', {
            serviceName: 'reportFacadeService',
            methodName,
            args: JSON.stringify([serviceName, reqParams])
          })
          .then(({ data }) => {
            resolve(data.data);
          });
      });
    },
    loadDataset() {
      let _this = this;
      return new Promise((resolve, reject) => {
        this.dataset.splice(0, this.dataset.length);
        if (this.widget.configuration.dataSourceType == 'static') {
          if (this.widget.configuration.datasetJson != undefined) {
            let datasetJson = new Function('return ' + this.widget.configuration.datasetJson)();
            if (Array.isArray(datasetJson)) {
              this.dataset.push(...datasetJson);
            } else {
              this.dataset.push(datasetJson);
            }
            resolve();
          }
        } else if (this.widget.configuration.dataSourceType == 'datasetService') {
          this.fetchDataByService(this.widget.configuration.datasetService, this.params).then(ds => {
            if (ds) {
              this.dataset.push(...ds);
            }
            resolve();
          });
        } else if (['dataSource', 'dataModel'].includes(this.widget.configuration.dataSourceType)) {
          this.getDataSourceProvider()
            .load()
            .then(data => {
              this.dataset.push({
                source: data.data || []
              });

              resolve();
            });
        }
      });
    }
  },

  watch: {
    vSize: {
      handler() {
        this.handleResize();
      }
    }
  }
};
