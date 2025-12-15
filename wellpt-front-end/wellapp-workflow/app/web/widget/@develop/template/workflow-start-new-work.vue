<template>
  <div class="workflow-start-new-work">
    <a-spin :spinning="loading" :tip="`${evtWidget.$t('WorkflowWork.loading', '加载中')}...`">
      <a-row></a-row>
      <a-row>
        <a-col style="border: 1px solid var(--w-border-color-light); border-radius: 4px">
          <a-tabs :default-active-key="defaultActiveKey" v-model="activeKey" class="pt-tabs">
            <a-tab-pane v-if="isShowRecentUsed" key="recentUsed" :tab="evtWidget.$t('WorkflowWork.recentlyUsed', '最近使用')">
              <div class="scrool-panel" v-if="recentUsedNode" :style="[bodyStyle, { overflowY: 'auto' }]">
                <div v-for="(item, index) in recentUsedNode.children" :key="index">
                  <a-row class="item-row" v-if="index % itemLayout.count == 0" :gutter="12">
                    <a-col class="item-column" :span="itemLayout.span" v-for="(number, columnIndex) of itemLayout.count" :key="columnIndex">
                      <div
                        class="item-title"
                        v-if="index + columnIndex < recentUsedNode.children.length"
                        :title="recentUsedNode.children[index + columnIndex].name"
                        @click="onItemClick(recentUsedNode.children[index + columnIndex])"
                      >
                        {{ recentUsedNode.children[index + columnIndex].name }}
                      </div>
                    </a-col>
                  </a-row>
                </div>
              </div>
            </a-tab-pane>
            <a-tab-pane key="all" :tab="evtWidget.$t('WorkflowWork.all', '全部')" force-render>
              <PerfectScrollbar class="scrool-panel" :style="bodyStyle">
                <draggable
                  v-if="allCategoryNodes.length > 0"
                  :list="allCategoryNodes"
                  :disabled="isSearching"
                  class="list-group"
                  ghost-class="ghost"
                  forceFallback="true"
                  handle=".drag-btn-handler"
                  @end="onDragEnd"
                >
                  <a-card class="flow-category-card" :bordered="false" v-for="categoryNode in allCategoryNodes" :key="categoryNode.id">
                    <template slot="title">
                      <Icon
                        :type="categoryNode.iconSkin ? categoryNode.iconSkin : 'pticon iconfont icon-ptkj-liucheng'"
                        :style="categoryNode.iconStyle ? categoryNode.iconStyle : 'color: var(--w-primary-color)'"
                      ></Icon>
                      <span class="category-title" v-html="categoryNode.name"></span>
                      ({{ categoryNode.children.length }})
                    </template>
                    <template slot="extra">
                      <Icon v-show="!isSearching" class="drag-btn-handler" type="pticon iconfont icon-ptkj-tuodong"></Icon>
                    </template>
                    <div v-for="(item, index) in categoryNode.children" :key="index">
                      <a-row class="item-row" v-if="index % itemLayout.count == 0" :gutter="12">
                        <a-col
                          class="item-column"
                          :span="itemLayout.span"
                          v-for="(number, columnIndex) of itemLayout.count"
                          :key="columnIndex"
                        >
                          <div
                            class="item-title"
                            v-if="index + columnIndex < categoryNode.children.length"
                            :title="categoryNode.children[index + columnIndex].title"
                            @click="onItemClick(categoryNode.children[index + columnIndex])"
                            v-html="categoryNode.children[index + columnIndex].name"
                          ></div>
                        </a-col>
                      </a-row>
                    </div>
                  </a-card>
                </draggable>
                <div v-else-if="isSearching">
                  <a-empty :image="searchEmptyImg" :image-style="emptyStyle">
                    <span slot="description" style="color: var(--w-text-color-light)">
                      {{ evtWidget.$t('WorkflowWork.noMatchData', '无匹配数据') }}！
                    </span>
                  </a-empty>
                </div>
                <div v-else>
                  <a-empty :image="emptyImg" :image-style="emptyStyle">
                    <span slot="description" style="color: var(--w-text-color-light)">
                      {{ evtWidget.$t('WorkflowWork.noData', '暂无数据') }}！
                    </span>
                  </a-empty>
                </div>
              </PerfectScrollbar>
            </a-tab-pane>
            <template slot="tabBarExtraContent">
              <div style="padding-right: 20px; line-height: 56px">
                <a-input-search
                  :placeholder="evtWidget.$t('WorkflowWork.searchPlaceholder', '搜索')"
                  v-model="searchText"
                  allow-clear
                  @change="onSearchFlow"
                />
              </div>
            </template>
          </a-tabs>
        </a-col>
      </a-row>
    </a-spin>
  </div>
</template>

<script>
import { isEmpty, trim, replace } from 'lodash';
import draggable from 'vuedraggable';
export default {
  props: {
    // 过滤的流程分类，多个以分号隔开
    categoryCode: String,
    // 显示最近使用
    showRecentUsed: {
      type: Boolean,
      default: true
    },
    bodyStyle: {
      type: Object,
      default() {
        return { height: '510px' };
      }
    },
    // 每行显示的列宽及展示的条件，count * span为24
    itemLayout: {
      type: Object,
      default() {
        return { count: 3, span: 8 };
      }
    },
    evtWidget: {
      type: Object,
      default: () => {}
    }
  },
  components: { draggable },
  data() {
    return {
      loading: true,
      searchText: '',
      defaultActiveKey: this.showRecentUsed ? 'recentUsed' : 'all',
      activeKey: this.showRecentUsed ? 'recentUsed' : 'all',
      isShowRecentUsed: this.showRecentUsed,
      recentUsedNode: undefined,
      allCategoryNodes: [],
      emptyImg: '/static/images/list-empty.png',
      searchEmptyImg: '/static/images/search-empty.png',
      emptyStyle: { height: '320px' }
    };
  },
  computed: {
    isSearching() {
      return !isEmpty(trim(this.searchText));
    }
  },
  created() {
    const _this = this;
    // 加载最近使用的流程
    _this.loadRecentUsed().then(recentUsed => {
      if (recentUsed) {
        _this.initRecentUsedFlowDefinition(recentUsed);
      } else {
        _this.isShowRecentUsed = false;
        _this.activeKey = 'all';
      }
      // 加载全部流程
      _this.loadUserAllFlowDefinitions();
    });
  },
  methods: {
    checkIsShowRecentUsed() {
      return $axios.get(`/proxy/api/workflow/setting/getByKey?key=GENERAL`).then(({ data: result }) => {
        if (result.data && result.data.attrVal) {
          let setting = JSON.parse(result.data.attrVal);
          return setting.showRecentUsed != null ? setting.showRecentUsed : true;
        } else {
          return false;
        }
      });
    },
    loadRecentUsed() {
      const _this = this;
      return _this.checkIsShowRecentUsed().then(showRecentUsed => {
        if (showRecentUsed) {
          return $axios
            .get(`/proxy/api/workflow/new/work/getUserRecentUseFlowDefinitions`)
            .then(({ data: result }) => {
              _this.loading = false;
              if (result.code == 0) {
                return result.data;
              } else {
                _this.$message.error(result.msg || '系统服务异常！');
              }
            })
            .catch(err => {
              _this.loading = false;
              _this.$message.error('系统服务异常！');
            });
        } else {
          return false;
        }
      });
    },
    loadUserAllFlowDefinitions() {
      const _this = this;
      $axios.get(`/proxy/api/workflow/new/work/getUserAllFlowDefinitions`).then(({ data: result }) => {
        _this.loading = false;
        if (result.code == 0 && result.data) {
          _this.initAllFlowDefinitions(result.data);
        }
      });
    },
    initRecentUsedFlowDefinition(recentUsed) {
      const _this = this;
      let recentUsedNode = null;
      // 最近使用
      if (recentUsed && recentUsed.id == 'recent_use' && recentUsed.children && recentUsed.children.length > 0) {
        recentUsedNode = recentUsed;
      }
      _this.recentUsedNode = recentUsedNode;
    },
    initAllFlowDefinitions(treeData) {
      const _this = this;
      let allCategoryNodes = [];
      let categoryCodes = isEmpty(_this.categoryCode) ? [] : _this.categoryCode.split(';');
      treeData.forEach(categoryNode => {
        // 不存在流程定义的分类
        if (!categoryNode.children || categoryNode.children.length == 0) {
          return;
        }

        // 设置name属性值到title属性，用于搜索匹配时title属性保持不变
        _this.setNameToTitleProperty(categoryNode);

        // 所有分类
        if (isEmpty(categoryCodes)) {
          allCategoryNodes.push(categoryNode);
        } else {
          // 分类过滤
          let categoryIndex = categoryCodes.findIndex(categoryCode => 'FLOW_CATEGORY_' + categoryCode == categoryNode.data);
          if (categoryIndex != -1) {
            allCategoryNodes.push(categoryNode);
          }
        }
      });
      _this.allCategoryNodeJson = JSON.stringify(allCategoryNodes);
      // 加载用户调整的流程分类布局并重新排序
      _this.loadFlowCategoryLayout().then(categoryIds => {
        allCategoryNodes.sort((a, b) => {
          let aIndex = categoryIds.findIndex(id => id == a.id);
          let bIndex = categoryIds.findIndex(id => id == b.id);
          // 新增的分类取当前分类中的索引
          if (aIndex == -1) {
            aIndex = allCategoryNodes.indexOf(a);
          }
          if (bIndex == -1) {
            bIndex = allCategoryNodes.indexOf(b);
          }
          return aIndex - bIndex;
        });
        _this.allCategoryNodes = allCategoryNodes;
      });
    },
    setNameToTitleProperty(categoryNode) {
      categoryNode.title = categoryNode.name;
      if (categoryNode.children) {
        categoryNode.children.forEach(child => {
          child.title = child.name;
        });
      }
    },
    onSearchFlow() {
      const _this = this;
      _this.activeKey = 'all';
      let searchText = trim(_this.searchText);
      if (isEmpty(searchText)) {
        if (_this.allCategoryNodeJson) {
          _this.allCategoryNodes = JSON.parse(_this.allCategoryNodeJson);
        } else {
          console.error('allCategoryNodeJson is null');
        }
      } else {
        _this.allCategoryNodes = _this.searchFlow(searchText, JSON.parse(_this.allCategoryNodeJson));
      }
    },
    searchFlow(searchText, categoryNodes) {
      let retNodes = [];
      categoryNodes.forEach(node => {
        let categoryName = node.name || '';
        let categoryNameInclude = this.highlightText(categoryName);
        // 查询分类名称
        if (categoryNameInclude.test) {
          node.name = categoryNameInclude.text;
          retNodes.push(node);

          // 分类下的流程名称
          let children = node.children || [];
          children.forEach(child => {
            let flowName = child.name || '';
            let flowNameInclude = this.highlightText(flowName);
            if (flowNameInclude.test) {
              child.name = flowNameInclude.text;
            }
          });
        } else {
          // 查询分类下的流程名称
          let children = node.children || [];
          let matchChildren = [];
          children.forEach(child => {
            let flowName = child.name || '';
            let flowNameInclude = this.highlightText(flowName);
            if (flowNameInclude.test) {
              child.name = flowNameInclude.text;
              matchChildren.push(child);
            }
          });
          if (matchChildren.length > 0) {
            node.children = matchChildren;
            retNodes.push(node);
          }
        }
      });
      return retNodes;
    },
    // 高亮显示匹配文本
    highlightText(text) {
      if (!text || !this.searchText || typeof text !== 'string') {
        return {
          test: false,
          text: text
        };
      }
      const escapedText = this.searchText.replace(/[.*+?^${}()|[\]\\]/g, '\\$&');
      const regex = new RegExp(`(${escapedText})`, 'gi');
      return {
        test: regex.test(text),
        text: text.replace(regex, '<span class="match-text">$1</span>')
      };
    },
    loadFlowCategoryLayout() {
      return $axios
        .get('/api/user/preferences/getValue', {
          params: { dataKey: 'flow_category_layout', functionId: 'start_new_work', moduleId: 'WORKFLOW' }
        })
        .then(({ data: result }) => {
          let categoryIds = [];
          if (result.data) {
            let perference = JSON.parse(result.data);
            categoryIds = perference.categoryIds || [];
          }
          return categoryIds;
        });
    },
    onDragEnd() {
      this.allCategoryNodeJson = JSON.stringify(this.allCategoryNodes);
      let categoryIds = this.allCategoryNodes.map(item => {
        return item.id;
      });
      $axios.post(`/proxy/api/user/preferences/saveUserPreference`, {
        moduleId: 'WORKFLOW',
        functionId: 'start_new_work',
        dataKey: 'flow_category_layout',
        dataValue: JSON.stringify({
          categoryIds
        }),
        remark: '用户自定义发起工作流程分类布局'
      });
    },
    onItemClick(item) {
      this.$emit('select', item);
    }
  }
};
</script>

<style lang="less" scoped>
.workflow-start-new-work {
  .item-row {
    margin-bottom: 12px;
  }
  .item-title {
    padding: 1px 12px 1px 24px;
    height: 40px;
    line-height: 40px;
    background: var(--w-primary-color-2);
    color: var(--w-text-color-dark);
    font-size: var(--w-font-size-base);
    border-radius: 4px;
    border: 1px solid var(--w-primary-color-2);

    white-space: nowrap;
    text-overflow: ellipsis;
    overflow: hidden;

    cursor: pointer;

    &:hover {
      border-color: var(--w-primary-color);
      color: var(--w-primary-color);

      &::before {
        background: var(--w-primary-color);
      }
    }
  }
  .item-title::before {
    position: absolute;
    top: 18px;
    left: 16px;
    width: 6px;
    height: 6px;
    content: '';
    border-radius: 50%;
    background: var(--w-text-color-dark);
  }

  .scrool-panel {
    margin: -20px;
    padding: 20px;
    .ghost {
      margin: 2px;
      border: 2px solid transparent;
      background: linear-gradient(white, white) padding-box, repeating-linear-gradient(-45deg, #ccc 0, #ccc 0.5em, white 0, white 0.75em);
    }

    .flow-category-card {
      .category-title {
        font-weight: bold;
        font-size: var(--w-font-size-base);
        color: var(--w-text-color-dark);
      }
      ::v-deep .ant-card-head {
        border-bottom: 0;
        .ant-card-head-title {
          padding-top: 12px;
          padding-bottom: 0px;
          line-height: 32px;
        }
      }
      ::v-deep .ant-card-body {
        padding: 8px 20px;
      }
      margin-bottom: 12px;
      border-radius: 4px;
      box-shadow: 0px 9px 18px 0px rgba(0, 0, 0, 0.05);

      .drag-btn-handler {
        color: var(--w-text-color-light);
        &:hover {
          color: var(--w-primary-color);
          cursor: move;
        }
      }
    }
  }

  ::v-deep .match-text {
    color: #e33033;
  }

  .ant-tabs-tabpane {
    --w-pt-tabs-tabpane-padding: 20px;
    background-color: var(--w-gray-color-2);
  }
}
</style>
