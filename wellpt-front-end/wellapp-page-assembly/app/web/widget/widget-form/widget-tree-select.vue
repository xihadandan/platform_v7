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

    <template v-if="widget.configuration.type === 'select-tree'">
      <div v-if="designMode">
        <!-- 设计模式情况下的显示效果 -->
        <a-tree-select
          :allowClear="allowClear"
          :showSearch="showSearch"
          :multiple="isMultiple"
          :placeholder="$t('placeholder', widget.configuration.placeholder)"
          :dropdownStyle="{ maxHeight: '400px', overflow: 'auto' }"
          style="width: 100%"
          :showArrow="true"
        />
      </div>
      <template v-else>
        <template v-if="!displayAsLabel">
          <w-tree-select
            ref="curCompRef"
            :style="{ width: '100%' }"
            :treeData="treeData"
            :value="value"
            :multiple="isMultiple"
            :editMode="widget.configuration.editMode"
            :showCheckedStrategy="widget.configuration.editMode.selectParent ? 'SHOW_ALL' : 'SHOW_CHILD'"
            :placeholder="$t('placeholder', widget.configuration.placeholder)"
            :disabled="disable || readonly"
            @change="onChangeTree"
            @blur="onBlur"
            style="max-width: 1920px"
            :dropdownClassName="dropdownClassName"
            :getPopupContainer="getPopupContainer()"
            :class="[showSearch ? '' : 'select-multiple-readonly']"
          />
        </template>
        <span v-else class="textonly">{{ selectedLabel }}</span>
      </template>
    </template>
  </a-form-model-item>
</template>

<script type="text/babel">
import formElementMinxin from './form-element.mixin';
import { debounce, uniqBy, orderBy, groupBy, kebabCase, map, each, union, filter, isArray, findIndex } from 'lodash';
import DataSourceBase from '@pageAssembly/app/web/assets/js/commons/dataSource.base.js';
import { getJsonValue, swapArrayElements, deepClone } from '@framework/vue/utils/util';
import WTreeSelect from '@dyformWidget/widget-form-select/components/WTreeSelect.vue';
import formCommonMixin from '@dyformWidget/mixin/form-common.mixin.js';
import md5 from '@framework/vue/utils/md5';
import { getPopupContainerNearestPs } from '@framework/vue/utils/function';

export default {
  name: 'WidgetTreeSelect',
  mixins: [formElementMinxin, formCommonMixin],
  props: {},
  data() {
    let selectOptions = [];
    if (this.widget.configuration.options.type == 'pageVar' && this.widget.configuration.options.bindPageVar) {
      selectOptions = getJsonValue(this.vPageState, this.widget.configuration.options.bindPageVar);
    }
    return {
      openSelect: undefined,
      extraProps: {},
      options: [], // 全部的选项数据
      selectOptions, // 下拉框选项
      fetched: false,
      treeData: [],
      treeDatas: [], // 全部的树选项数据
      value: undefined,
      tempValue: null, // 临时值，用于当选项未加载时候的值存储
      selectedLabels: [], // 选中label数组
      treeFocus: false,
      valueLabelMap: {},
      dataSourceResults: [],
      optionsValueMap: {},
      pageSize: 50,
      pageIndex: 0,
      loading: false,
      checkAll: false, // 全选
      componentType: this.widget.configuration.type,
      allowClear: this.widget.configuration.editMode.allowClear,
      showSearch: this.widget.configuration.editMode.showSearch,
      selectMode: this.widget.configuration.editMode.selectMode,
      tokenSeparators: this.widget.configuration.tokenSeparators || ';',
      dropdownClassName: 'ps__child--consume', //阻止外层下拉滚动
      popoverVisible: false,
      curDictUuid: '',
      dropdownScope: false,
      dropdownListener: false,
      editMoreItem: undefined,
      isOpen: false,
      searchLoading: false,
      treeOptionParams: {}, // 树形下拉框数据仓库搜索参数
      treeOptionDefaultCondition: undefined, // 树形下拉框数据仓库默认筛选条件
      cacheKey: undefined
    };
  },
  components: {
    WTreeSelect
  },
  computed: {
    // 是否多选
    isMultiple() {
      return this.selectMode === 'multiple';
    },
    // 选中label字符串
    selectedLabel() {
      return this.selectedLabels.join(this.tokenSeparators);
    }
  },
  created() {
    if (!this.designMode) {
      this.fetchTreeData();
    }
  },
  mounted() {
    if (this.form[this.widget.configuration.code]) {
      this.setValue(this.form[this.widget.configuration.code]);
    }
  },
  methods: {
    setValue(v, validate = true) {
      this.value = v;
      if (this.isMultiple) {
        // 多选情况
        if (v && typeof v === 'string') {
          this.value = v.split(this.tokenSeparators);
        } else if (!v) {
          this.value = [];
        }
      }

      this.form[this.fieldCode] = Array.isArray(v) ? v.join(this.tokenSeparators) : v;

      this.setSelectedLabes();
      this.clearValidate();
    },
    getPopupContainer() {
      return getPopupContainerNearestPs(400);
    },
    refetchOption() {
      this.fetchTreeData();
    },
    // 获取树形数据
    fetchTreeData() {
      const options = this.widget.configuration.options;
      if (options.type == 'selfDefine') {
        this.getTreeDataBySelfDefine();
      } else if (options.type == 'dataDictionary') {
        this.fetchTreeDataByDataDic(options.dataDictionaryUuid);
      } else if (options.type == 'dataSource') {
        this.fetchTreeDataByDataSource(options);
      } else if (options.type == 'apiLinkService') {
        this.fetchTreeDataByApiLink(options);
      }
    },
    // 获取自定义下拉树数据
    getTreeDataBySelfDefine() {
      this.treeData = this.reSetTreeData(this.widget.configuration.treeData);
      this.treeDatas = deepClone(this.treeData);
      this.setValueLabelMap(this.treeData);
      this.setSelectedLabes();
    },
    // 获取树形下拉框 数据字典备选项
    fetchTreeDataByDataDic(dataDicUuid) {
      this.getLabelValueOptionByDataDic(dataDicUuid, (results, key) => {
        this.treeData = this.reSetTreeData(results);
        this.treeDatas = deepClone(this.treeData);
        this.setValueLabelMap(this.treeData);
        this.setSelectedLabes();
        if (key) {
          this.cacheKey = key;
        }
      });
    },
    // 获取树形下拉框 数据仓库备选项
    fetchTreeDataByDataSource(options) {
      this.fetched = false;
      options = deepClone(options);
      options.defaultCondition = this.treeOptionDefaultCondition === undefined ? options.defaultCondition : this.treeOptionDefaultCondition;
      options.params = this.treeOptionParams;
      const key = `TreeNodesByDataSource:${options.dataSourceId}:${md5(JSON.stringify(options))}`;
      this.cacheKey = key;
      this.$tempStorage.getCache(
        key,
        () => {
          return new Promise((resolve, reject) => {
            $axios
              .post('/json/data/services', {
                serviceName: 'cdDataStoreService',
                methodName: 'loadTreeNodes',
                args: JSON.stringify([
                  {
                    dataStoreId: options.dataSourceId,
                    uniqueColumn: options.dataSourceKeyColumn,
                    parentColumn: options.dataSourceParentColumn,
                    displayColumn: options.dataSourceLabelColumn,
                    valueColumn: options.dataSourceValueColumn,
                    defaultCondition: options.defaultCondition,
                    params: options.params,
                    async: false
                  }
                ])
              })
              .then(({ data }) => {
                if (data.code == 0) {
                  if (this.$i18n.locale !== 'zh_CN' && options.autoTranslate) {
                    let words = [];
                    let cascadeMap = list => {
                      for (let l of list) {
                        let data = l.data;
                        words.push(data[options.dataSourceLabelColumn]);
                        if (options.dataSourceExtendColumn) {
                          words.push(data[options.dataSourceExtendColumn]);
                        }
                        if (l.children && l.children.length) {
                          cascadeMap(l.children);
                        }
                      }
                    };
                    cascadeMap(data.data);
                    this.$translate(words, 'zh', this.$i18n.locale.split('_')[0]).then(result => {
                      let setTranslateLabel = list => {
                        for (let l of list) {
                          let data = l.data;
                          if (result[l.name]) {
                            l.name = result[l.name];
                            data[options.dataSourceLabelColumn] = l.name;
                          }
                          if (options.dataSourceExtendColumn && result[data[options.dataSourceExtendColumn]]) {
                            data[options.dataSourceExtendColumn] = result[data[options.dataSourceExtendColumn]];
                          }
                          if (l.children && l.children.length) {
                            setTranslateLabel(l.children);
                          }
                        }
                      };
                      setTranslateLabel(data.data);
                      resolve(data.data || []);
                    });
                  } else {
                    resolve(data.data || []);
                  }
                }
              });
          });
        },
        results => {
          this.treeData = this.reSetTreeData(results);
          this.treeDatas = deepClone(this.treeData);
          this.fetched = true;
          this.setValueLabelMap(this.treeData);
          this.setSelectedLabes();
        }
      );
    },

    fetchTreeDataByApiLink(options) {
      let _this = this;
      this.fetchDataByApiLinkInvocation(options.apiInvocationConfig).then(results => {
        let list = results != undefined ? (Array.isArray ? results : [results]) : [];
        if (list.length > 0) {
          let _continue = () => {
            _this.treeData = _this.reSetTreeData(list);
            _this.treeDatas = deepClone(_this.treeData);
            _this.fetched = true;
            _this.setValueLabelMap(this.treeData);
            _this.setSelectedLabes();
          };
          if (_this.$i18n.locale !== 'zh_CN' && options.autoTranslate) {
            let words = [];
            let cascadeMap = list => {
              for (let l of list) {
                words.push(l.name);
                if (l.subTitle) {
                  words.push(l.subTitle);
                }
                if (l.children && l.children.length) {
                  cascadeMap(l.children);
                }
              }
            };
            cascadeMap(list);
            _this.$translate(words, 'zh', _this.$i18n.locale.split('_')[0]).then(result => {
              let setTranslateLabel = list => {
                for (let l of list) {
                  if (result[l.name]) {
                    l.name = result[l.name];
                  }

                  if (l.subTitle != undefined && result[l.subTitle]) {
                    l.subTitle = result[l.subTitle];
                  }

                  if (l.children && l.children.length) {
                    setTranslateLabel(l.children);
                  }
                }
              };
              setTranslateLabel(list);
              _continue();
            });
          } else {
            _continue();
          }
        }
      });
    },
    sortTreeData(data) {
      if (this.widget.configuration.options.type == 'dataSource') {
        if (this.widget.configuration.options.sortField) {
          return orderBy(
            data,
            item => {
              return item.data[this.widget.configuration.options.sortField];
            },
            [this.widget.configuration.options.sortType || 'asc']
          );
        }
      }
      return data;
    },
    reSetTreeData(treeData) {
      const tree2Map = data => {
        data = this.sortTreeData(data);
        return map(data, child => {
          child.s_tree_key = child.id || child.uuid;
          child.s_tree_value = child.real || child.value || child.s_tree_key;
          child.s_tree_title = child.display || child.name || child.label;
          if (this.widget.configuration.options.type == 'selfDefine') {
            child.s_tree_title = this.$t(child.id, child.s_tree_title);
          }
          // 所有选项data值
          this.optionsValueMap[child.s_tree_value] = child.data;
          if (this.widget.configuration.options.dataSourceExtendColumn) {
            child.s_tree_extend = child.data[this.widget.configuration.options.dataSourceExtendColumn];
          }
          if (!child.s_tree_label) {
            child.s_tree_label = child.s_tree_title;
          }

          if (child.children && child.children.length) {
            each(child.children, c_child => {
              c_child.s_tree_title = c_child.display || c_child.name || c_child.label;
              if (this.widget.configuration.editMode.allPath) {
                c_child.s_tree_label = child.s_tree_label + '-' + c_child.s_tree_title;
              } else {
                c_child.s_tree_label = c_child.s_tree_title;
              }
            });
            child.children = tree2Map(child.children);
          }
          return child;
        });
      };
      return tree2Map(treeData);
    },
    onChangeTree(value, label, extra) {
      if (!label && isArray(value)) {
        label = map(value, 'label');
      }
      this.selectTreeNodeChange(value, label, extra);
    },
    // 选中树节点
    selectTreeNodeChange(val, label, extra) {
      this.form[this.fieldCode] = null;
      if (Array.isArray(val)) {
        this.selectedLabels = label;
        this.form[this.fieldCode] = val.join(this.tokenSeparators);
      } else {
        this.selectedLabels = [label];
        this.form[this.fieldCode] = val;
      }
      this.value = val;
    },
    setValueLabelMap(options) {
      if (this.componentType === 'select-tree') {
        const tree2Map = data => {
          data.map(child => {
            this.valueLabelMap[child.s_tree_value] = child.s_tree_label;
            if (child.children) {
              tree2Map(child.children);
            }
          });
        };
        tree2Map(options);
      }
    },
    setSelectedLabes() {
      this.selectedLabels.splice(0, this.selectedLabels.length);
      // this.selectedLabels.length = 0;
      let values = this.value;
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
    addDataSourceParams(params) {
      for (let k in params) {
        this.treeOptionParams[k] = params[k];
      }
    },
    clearDataSourceParams() {
      this.treeOptionParams = {};
    },
    deleteDataSourceParams(...key) {
      for (let k of key) {
        delete this.treeOptionParams[k];
      }
    }
  }
};
</script>
