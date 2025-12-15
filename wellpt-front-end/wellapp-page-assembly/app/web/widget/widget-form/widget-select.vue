<template>
  <a-form-model-item :ref="widget.configuration.code" :prop="widget.configuration.code" :rules="rules" :style="itemStyle" v-if="vShow">
    <template slot="label" v-if="widget.configuration.titleHidden !== true">
      {{ fieldName }}
      <template v-if="widget.configuration.enableTooltip && widget.configuration.tooltip != undefined">
        <a-tooltip
          v-if="widget.configuration.tooltipDisplayType == 'tooltip'"
          :title="$t(widget.id + '_tooltip', widget.configuration.tooltip)"
        >
          <a-button size="small" icon="info-circle" type="link" />
        </a-tooltip>
        <a-popover v-else trigger="hover" :content="$t(widget.id + '_tooltip', widget.configuration.tooltip)" :title="null">
          <a-button size="small" icon="info-circle" type="link" />
        </a-popover>
      </template>
    </template>
    <template v-if="editable || readonly">
      <template v-if="widget.configuration.type == 'select'">
        <a-select
          v-model="form[widget.configuration.code]"
          :mode="widget.configuration.selectMode"
          :allowClear="widget.configuration.allowClear"
          :disabled="designMode ? false : disable || readonly"
          :style="{ width: '100%' }"
          :showSearch="widget.configuration.showSearch"
          :filter-option="filterOption"
          @search="onSearchSelectOption"
          @change="onSelectChange"
          @popupScroll="onPopupScroll"
          @dropdownVisibleChange="onDropdownVisibleChange"
          :getPopupContainer="getPopupContainer()"
          :dropdownClassName="dropdownClassName"
        >
          <a-icon type="loading" v-if="loading" slot="suffixIcon" />
          <div slot="dropdownRender" slot-scope="menu">
            <v-nodes :vnodes="menu" />
            <a-spin size="small" :style="{ marginLeft: '10px' }" v-show="loading" />
          </div>
          <a-select-option v-for="item in selectOptions" :key="item.value" :value="item.value">
            {{ item.label }}
          </a-select-option>
        </a-select>
      </template>
    </template>
    <span v-else class="textonly">{{ selectedLabel }}</span>
  </a-form-model-item>
</template>

<script type="text/babel">
import formElementMinxin from './form-element.mixin';
import formMixin from '@dyform/app/web/widget/mixin/form-common.mixin';
import { debounce, uniqBy, orderBy, groupBy } from 'lodash';
import DataSourceBase from '@pageAssembly/app/web/assets/js/commons/dataSource.base.js';
import { getJsonValue } from '@framework/vue/utils/util';
import { getPopupContainerNearestPs } from '@framework/vue/utils/function';

export default {
  name: 'WidgetSelect',
  mixins: [formElementMinxin, formMixin],
  props: {},
  data() {
    let selectOptions = [];
    if (this.widget.configuration.options.type == 'pageVar' && this.widget.configuration.options.bindPageVar) {
      selectOptions = getJsonValue(this.vPageState, this.widget.configuration.options.bindPageVar);
    }
    return {
      tokenSeparators: ';',
      options: [], // 全部的选项数据
      selectOptions, // 下拉框选项
      fetched: false,
      treeData: [],
      value: this.widget.configuration.selectMode === 'multiple' ? [] : undefined,
      tempValue: null, // 临时值，用于当选项未加载时候的值存储
      selectedLabels: [], // 选中label数组
      treeFocus: false,
      valueLabelMap: {},
      dataSourceResults: [],
      optionsValueMap: {},
      pageSize: 50,
      pageIndex: 0,
      loading: false,
      dropdownClassName: 'ps__child--consume', //阻止外层下拉滚动
      relateValue: undefined // 联动值
    };
  },
  beforeCreate() {},
  components: {
    VNodes: {
      functional: true,
      render: (h, ctx) => ctx.props.vnodes
    }
  },
  computed: {
    // 选中label字符串
    selectedLabel() {
      return this.selectedLabels.join(this.tokenSeparators);
    }
  },
  created() {},
  methods: {
    setSelectedLabels() {
      this.selectedLabels.splice(0, this.selectedLabels.length);
      // this.selectedLabels.length = 0;
      let values = this.form[this.widget.configuration.code];
      if (values == undefined) {
        return [];
      }
      if (typeof values == 'string') {
        values = values.split(this.tokenSeparators);
      }
      for (let i = 0, len = values.length; i < len; i++) {
        if (this.valueLabelMap[values[i]] != undefined) {
          this.selectedLabels.push(this.valueLabelMap[values[i]]);
        }
      }
      return this.selectedLabels;
    },
    getPopupContainer() {
      return getPopupContainerNearestPs(400);
    },
    onSelectChange(value, option) {
      this.value = value;
    },
    refetchOption() {
      this.fetchSelectOptions();
    },
    setValueLabelMap(options) {
      for (let i = 0, len = options.length; i < len; i++) {
        const value = options[i].value;
        const label = options[i].label;
        if (value && label) {
          this.valueLabelMap[value] = label;
        }
        if (options[i]['options']) {
          this.setValueLabelMap(options[i]['options']);
        }
      }
    },
    fetchSelectOptions() {
      // 常量
      if (this.widget.configuration.options.type == 'selfDefine') {
        this.options = this.widget.configuration.options.defineOptions;
        this.selectOptions = this.widget.configuration.options.defineOptions;
        for (let o of this.selectOptions) {
          o.label = this.$t(o.id, o.label);
        }
        this.setValueLabelMap(this.selectOptions);
        this.setSelectedLabels();
        this.fetched = true;
      } else if (this.widget.configuration.options.type == 'dataSource') {
        this.fetchSelectOptionByDataSource(this.widget.configuration.options);
      } else if (this.widget.configuration.options.type == 'dataDictionary') {
        this.fetchSelectOptionByDataDic(this.widget.configuration.options.dataDictionaryUuid);
      } else if (this.widget.configuration.options.type == 'dataModel') {
        this.fetchSelectOptionByDataSource(
          this.widget.configuration.options,
          `/proxy/api/dm/loadData/${this.widget.configuration.options.dataModelUuid}`,
          `/proxy/api/dm/loadDataCount/${this.widget.configuration.options.dataModelUuid}`
        );
      } else if (this.widget.configuration.options.type == 'apiLinkService') {
        this.fetchSelectOptionByApiLink(this.widget.configuration.options);
      }

      return [];
    },
    fetchSelectOptionByApiLink(options) {
      let _this = this;
      _this.selectOptions.splice(0, _this.selectOptions.length);
      this.fetchDataByApiLinkInvocation(options.apiInvocationConfig).then(results => {
        _this.loading = false;
        if (Array.isArray(results) && results.length > 0) {
          let opts = [];
          let initValue = _this.getValue();
          if (typeof initValue === 'string') {
            initValue = [initValue];
          }
          let initValOptions = [],
            labels = [];
          for (let i = 0, len = results.length; i < len; i++) {
            let opt = { label: results[i].label, value: results[i].value };
            if (opt.label != undefined) {
              labels.push(opt.label);
            }
            if (results[i].extend_label) {
              opt.extend_label = results[i].extend_label;
              if (opt.extend_label != undefined) {
                labels.push(opt.extend_label);
              }
            }
            if (_this.componentType === 'select-group') {
              opt.group = results[i].group;
              if (opt.group != undefined) {
                labels.push(opt.group);
              }
            }
            if (initValue && initValue.indexOf(results[i].value) != -1) {
              initValOptions.push(opt);
            }
            opts.push(opt);
            _this.optionsValueMap[results[i].value] = results[i];
          }
          let _continue = () => {
            if (_this.componentType === 'select-group') {
              opts = orderBy(opts, ['group']);
            }
            _this.options = opts;
            _this.fetched = true;
            _this.loading = false;
            _this.onPageOptions((_this.pageIndex = 0), 0, initValOptions, 'group');
            _this.setValueLabelMap(opts);
            _this.setSelectedLabels();
            _this.emitChange({}, false);
            _this.setRelatedValue();
          };

          if (options.autoTranslate && _this.$i18n.locale !== 'zh_CN') {
            _this.$translate(labels, 'zh', _this.$i18n.locale.split('_')[0]).then(map => {
              for (let o of opts) {
                let rst = _this.optionsValueMap[o.value];
                if (o.label && map[o.label.trim()]) {
                  o.label = map[o.label.trim()];
                  rst.label = o.label;
                }
                if (o.extend_label && map[o.extend_label.trim()]) {
                  o.extend_label = map[o.extend_label.trim()];
                  rst.extend_label = o.extend_label;
                }
                if (o.group && map[o.group]) {
                  o.group = map[o.group];
                  rst.group = o.group;
                }
              }
              _continue();
            });
          } else {
            _continue();
          }
        }
      });
    },
    // 获取下拉框数据仓库
    fetchSelectOptionByDataSource(options, loadDataUrl, loadDataCntUrl) {
      let _this = this;
      let valueColIndex = options.dataSourceValueColumn,
        labelColIndex = options.dataSourceLabelColumn,
        groupColIndex = options.groupColumn;
      let opt = { dataStoreId: options.dataSourceId };
      if (loadDataUrl != undefined) {
        opt.loadDataUrl = loadDataUrl;
      }
      if (loadDataCntUrl != undefined) {
        opt.loadDataCntUrl = loadDataCntUrl;
      }
      this.loading = true;
      this.dataSourceProvider = new DataSourceBase({
        ...opt,
        onDataChange: function (data, count, params) {
          _this.loading = false;
          let results = data.data;
          if (results.length > 0) {
            let opts = [];
            let initValue = _this.getValue();
            if (typeof initValue === 'string') {
              initValue = [initValue];
            }
            let initValOptions = [],
              labels = [];
            for (let i = 0, len = results.length; i < len; i++) {
              let opt = { label: results[i][labelColIndex], value: results[i][valueColIndex] };
              if (opt.label != undefined) {
                labels.push(opt.label);
              }
              if (initValue && initValue.indexOf(results[i][valueColIndex]) != -1) {
                initValOptions.push(opt);
              }
              opts.push(opt);
              _this.optionsValueMap[results[i][valueColIndex]] = results[i];
            }
            _this.dataSourceResults = results;
            if (_this.widget.configuration.type === 'select-group') {
              opts = orderBy(opts, [groupColIndex]);
            }

            let _continue = () => {
              _this.options = opts;
              _this.fetched = true;
              _this.selectOptions = opts;
              _this.setValueLabelMap(opts);
              _this.setSelectedLabels();
              _this.onPageOptions((_this.pageIndex = 0), 0, initValOptions);
            };

            if (options.autoTranslate && _this.$i18n.locale !== 'zh_CN') {
              _this.$translate(labels, 'zh', _this.$i18n.locale.split('_')[0]).then(map => {
                for (let o of opts) {
                  let rst = _this.optionsValueMap[o.value];
                  if (o.label && map[o.label.trim()]) {
                    o.label = map[o.label.trim()];
                    rst[labelColIndex] = o.label;
                  }
                }
                _continue();
              });
            } else {
              _continue();
            }
          }
        },
        receiver: this,
        defaultCriterions: options.defaultCondition //默认条件
          ? [
              {
                sql: options.defaultCondition
              }
            ]
          : [],
        pageSize: this.pageSize * 1000 // FIXME: 下拉数据多的情况下，会明显卡顿，是否增加分页加载数据(需要引导用户知道需要滚动加载更多)
      });
      this.dataSourceProvider.load();
    },

    // 获取下拉框数据字典
    fetchSelectOptionByDataDic(dataDicUuid) {
      let _this = this,
        isGroup = this.widget.configuration.type === 'select-group';
      this.loading = true;
      $axios
        .post('/json/data/services', {
          serviceName: 'cdDataDictionaryFacadeService',
          methodName: 'listItemByDictionaryCode',
          args: JSON.stringify([dataDicUuid])
        })
        .then(({ data }) => {
          let selectOptions = [];
          if (data.code == 0 && data.data) {
            for (let i = 0, len = data.data.length; i < len; i++) {
              let opt = {};
              if (isGroup) {
                let group = { label: data.data[i].name, options: [] },
                  jlen = data.data[i].children.length;
                for (let j = 0; j < jlen; j++) {
                  opt = {
                    label: data.data[i].children[j].label,
                    value: data.data[i].children[j].value
                  };
                  group.options.push(opt);
                  _this.options.push(opt);
                }
                if (jlen > 0) selectOptions.push(group);
              } else {
                opt = {
                  label: data.data[i].label,
                  value: data.data[i].value
                };
                selectOptions.push(opt);
                _this.options.push(opt);
              }
            }
          }
          _this.loading = false;
          _this.fetched = true;
          _this.selectOptions = selectOptions;
          _this.setValueLabelMap(selectOptions);
          _this.setSelectedLabels();
        });
    },

    // 下拉框搜索 文本框值变化时回调
    onSearchSelectOption: debounce(function (v) {
      if (v) {
        this.selectOptions = this.options.filter(o => o.label.indexOf(v) != -1 || o.value.indexOf(v) != -1);
        this.searchValue = v;
      } else {
        this.onPageOptions((this.pageIndex = 0));
      }
    }, 500),

    // 展开下拉菜单的回调
    onDropdownVisibleChange: function (visible) {
      if (visible && this.searchValue != undefined) {
        // 单选的清空下，要自动清理掉搜索值，搜索第一页的数据
        this.onPageOptions((this.pageIndex = 0), 0, this.selectOptions);
        this.searchValue = null;
      }
    },

    // 分页选项数据
    onPageOptions(pageIndex, delay, extraOptions, groupCol) {
      this.loading = true;
      let end = (pageIndex + 1) * this.pageSize,
        start = pageIndex * this.pageSize,
        _this = this;
      if (this.options.length <= end) {
        end = this.options.length;
        this.optionsCompletedRendered = true;
      } else {
        this.optionsCompletedRendered = false;
      }
      if (pageIndex === 0) {
        _this.selectOptions = [];
      }
      let newSelection = function () {
        let _options = uniqBy(_this.selectOptions.concat(_this.options.slice(start, end)).concat(extraOptions || []), 'value');
        _this.selectOptions = _options;
        _this.loading = false;
      };
      if (delay == undefined) {
        newSelection();
      } else {
        setTimeout(function () {
          newSelection();
        }, delay);
      }
    },
    onPopupScroll: debounce(function (evt) {
      if (evt.target.scrollHeight != 0 && evt.target.scrollHeight - evt.target.scrollTop <= 300 && !this.optionsCompletedRendered) {
        // 加载更多
        this.onPageOptions(++this.pageIndex, 200);
      }
    }, 200),
    draggable(data, parentSelector, dragHandleClass, onEvent) {
      let _this = this;
      this.$nextTick(() => {
        import('sortablejs').then(Sortable => {
          Sortable.default.create(parentSelector, {
            handle: dragHandleClass,
            onChoose: e => {
              console.log(e);
            },
            onUnchoose: (onEvent && onEvent.onUnchoose) || function () {},
            onMove: (onEvent && onEvent.onMove) || function () {},
            onEnd:
              (onEvent && onEvent.onEnd) ||
              function (e) {
                let temp = data.splice(e.oldIndex, 1)[0];
                data.splice(e.newIndex, 0, temp);
                _this.draggable(data, parentSelector, dragHandleClass, onEvent);
                if (onEvent && onEvent.afterOnEnd) {
                  onEvent.afterOnEnd();
                }
              }
          });
        });
      });
    }
  },
  mounted() {
    // if (this.widget.configuration.selectMode === 'multiple' && this.widget.configuration.selectedSortable) {
    //   this.draggable(this.value, this.$el.querySelector('.ant-select-selection__rendered ul'), 'li', {});
    // }
  },

  beforeMount() {
    if (this.widget.configuration.type == 'select' || this.widget.configuration.type == 'select-group') {
      this.fetchSelectOptions();
    }
  }
};
</script>
