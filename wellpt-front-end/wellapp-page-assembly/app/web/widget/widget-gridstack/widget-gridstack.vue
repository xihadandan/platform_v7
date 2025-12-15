<template>
  <div class="widget-gridstack">
    <template v-if="widget.configuration.enableUserCustom">
      <a-affix
        v-if="widget.configuration.customBarDisplayPosition == 'rightPopover'"
        :offset-top="120"
        :style="{ position: 'absolute' }"
        :class="['gridstack-affix', swapping ? 'swapping' : undefined]"
      >
        <div>
          <div>
            <a-tooltip placement="left" :mouseEnterDelay="0.3">
              <template slot="title">
                {{
                  swapping
                    ? $t('WidgetGridstack.saveAndEndDragLayout', '保存并结束拖动排版')
                    : $t('WidgetGridstack.startDragLayout', '开始拖动排版')
                }}
              </template>
              <a-switch size="small" v-model="swapping" @change="onSwapChange" :loading="swapSaving">
                <a-icon slot="checkedChildren" type="swap" />
                <a-icon slot="unCheckedChildren" type="swap" />
              </a-switch>
            </a-tooltip>
          </div>

          <div v-show="hasUserCustom">
            <a-button type="link" size="small" icon="rollback" style="color: #fff" @click.stop="restoreDefaultLayout">
              {{ $t('WidgetGridstack.resetDefault', '恢复默认') }}
            </a-button>
          </div>
        </div>
      </a-affix>
      <div v-else style="text-align: right; padding-bottom: var(--w-padding-2xs)">
        <a-tooltip placement="left" :mouseEnterDelay="0.3">
          <template slot="title">
            {{
              swapping
                ? $t('WidgetGridstack.saveAndEndDragLayout', '保存并结束拖动排版')
                : $t('WidgetGridstack.startDragLayout', '开始拖动排版')
            }}
          </template>
          <a-switch size="small" v-model="swapping" @change="onSwapChange" :loading="swapSaving">
            <a-icon slot="checkedChildren" type="swap" />
            <a-icon slot="unCheckedChildren" type="swap" />
          </a-switch>
        </a-tooltip>
        <a-divider type="vertical" />
        <a-button v-show="hasUserCustom" type="link" size="small" icon="rollback" @click.stop="restoreDefaultLayout">
          {{ $t('WidgetGridstack.resetDefault', '恢复默认') }}
        </a-button>
      </div>
    </template>

    <div class="grid-stack" v-if="!loading" :id="gridstackInstId">
      <template v-for="(item, t) in widget.configuration.gridItems">
        <div
          v-if="item.configuration.widgets.length > 0"
          :key="'grid-item-' + item.id"
          :class="['grid-stack-item']"
          :gs-w="item.configuration.itemPosition.w"
          :gs-x="item.configuration.itemPosition.x"
          :gs-y="item.configuration.itemPosition.y"
          :gs-h="item.configuration.itemPosition.h"
          :gs-id="item.id"
          :gs-no-resize="true"
          :id="item.id"
        >
          <div
            :class="['grid-stack-item-content', item.configuration.resizeToContent ? 'resize-to-content' : undefined]"
            :style="{
              overflow: 'hidden',
              borderRadius: formateBorderRadius(item.configuration.style.borderRadius),
              backgroundColor: item.configuration.style.backgroundColor || undefined
            }"
          >
            <div v-if="item.configuration.resizeToContent" style="height: auto">
              <div class="grid-stack-item-drag-cover" v-show="swapping"></div>
              <template v-for="(wgt, windex) in item.configuration.widgets">
                <component
                  :key="wgt.id"
                  :is="wgt.wtype"
                  :widget="wgt"
                  :index="windex"
                  :widgetsOfParent="widget.configuration.gridItems"
                  :parent="widget"
                  class="grid-stack-item-widget"
                  :style="{ marginBottom: windex == item.configuration.widgets.length - 1 ? '0px' : undefined }"
                ></component>
              </template>
            </div>
            <div v-else>
              <Scroll style="height: 100%" class="grid-stack-item-scroll">
                <div class="grid-stack-item-drag-cover" v-show="swapping"></div>
                <template v-for="(wgt, windex) in item.configuration.widgets">
                  <component
                    :key="wgt.id"
                    :is="wgt.wtype"
                    :widget="wgt"
                    :index="windex"
                    :widgetsOfParent="widget.configuration.gridItems"
                    :parent="widget"
                    :style="{ marginBottom: windex == item.configuration.widgets.length - 1 ? '0px' : undefined }"
                  ></component>
                </template>
              </Scroll>
            </div>
          </div>
        </div>
      </template>
    </div>
  </div>
</template>

<script type="text/babel">
import widgetMixin from '@framework/vue/mixin/widgetMixin';
import 'gridstack/dist/gridstack.min.css';
import 'gridstack/dist/gridstack-extra.min.css';
import './css/index.less';
import { GridStack } from 'gridstack/dist/es5/gridstack';
import md5 from '@framework/vue/utils/md5';
import { generateId } from '@framework/vue/utils/util';
import { debounce, throttle } from 'lodash';
import { CELL_HEIGHT } from './constant';
import ElementResizeDetector from 'element-resize-detector';
export default {
  name: 'WidgetGridstack',
  mixins: [widgetMixin],
  inject: ['sentinelContext'],
  data() {
    return {
      cellHeight: CELL_HEIGHT,
      swapping: false,
      loading: this.widget.configuration.enableUserCustom,
      sortMD5: undefined,
      swapSaving: false,
      hasUserCustom: false,
      gridstackInstId: `gs-inst-${generateId(6)}`,
      timeoutCheck: {}
    };
  },

  beforeCreate() {},
  components: {},
  computed: {},
  created() {
    // this.resizeToContent = debounce(this.resizeToContent.bind(this), 500);
  },
  methods: {
    refresh() {
      if (this.grid) {
        this.grid.destroy(false);
      }
      this.initGrid();
    },
    resizeToContent(id) {
      console.log('调整');
      let items = this.$el.querySelectorAll('.grid-stack-item');
      if (items) {
        for (let item of items) {
          if ((id && item.getAttribute('id') == id) || id == undefined) {
            this.grid.resizeToContent(item, false);
          }
        }
      }
    },
    formateBorderRadius(borderRadius) {
      if (borderRadius != undefined) {
        if (typeof borderRadius == 'number') {
          return `${borderRadius}px`;
        }
        return borderRadius.join('px ') + 'px';
      }
    },
    onSwapChange() {
      if (this.swapping === false) {
        let itemSaved = this.grid.save(),
          map = {},
          sortPosition = [];
        for (let item of itemSaved) {
          map[item.id] = item;
        }
        for (let item of this.widget.configuration.gridItems) {
          if (item.configuration.widgets.length > 0) {
            item.configuration.itemPosition.x = map[item.id].x;
            item.configuration.itemPosition.y = map[item.id].y;
            item.configuration.itemPosition.w = map[item.id].w || 1;
            item.configuration.itemPosition.h = map[item.id].h;
            sortPosition.push(item.configuration.itemPosition);
          }
        }

        let _sortMd5 = md5(JSON.stringify(sortPosition));
        if (this.sortMD5 !== _sortMd5) {
          // 保存排版配置
          this.saveUserCustomLayout().then(() => {
            this.sortMD5 = _sortMd5;
          });
        }
      } else {
        // 记录排版顺序
        let sortPosition = [];
        for (let item of this.widget.configuration.gridItems) {
          if (item.configuration.widgets.length > 0) {
            sortPosition.push(item.configuration.itemPosition);
          }
        }
        this.sortMD5 = md5(JSON.stringify(sortPosition));
      }
    },
    restoreDefaultLayout() {
      this.$confirm({
        title: this.$t('WidgetGridstack.resetDefaultLayout', '恢复默认排版'),
        content: this.$t('WidgetGridstack.resetDefaultLayoutTips', '确定要恢复默认排版吗？'),
        onOk: () => {
          this.swapping = false;
          if (this.defaultGridItemPosition) {
            this.hasUserCustom = false;
            $axios.post(`/proxy/api/user/preferences/dropUserPreference`, {
              ...this.userCustomLayoutParams()
            });
            for (let gridItem of this.widget.configuration.gridItems) {
              if (this.defaultGridItemPosition[gridItem.id]) {
                gridItem.configuration.itemPosition.x = this.defaultGridItemPosition[gridItem.id].x;
                gridItem.configuration.itemPosition.y = this.defaultGridItemPosition[gridItem.id].y;
                gridItem.configuration.itemPosition.h = this.defaultGridItemPosition[gridItem.id].h;
                gridItem.configuration.itemPosition.w = this.defaultGridItemPosition[gridItem.id].w;
              }
            }
            this.loading = true;
            this.$nextTick(() => {
              this.refresh();
            });
          }
        }
      });
    },
    saveUserCustomLayout() {
      this.$loading(true);
      this.swapSaving = true;
      return new Promise((resolve, reject) => {
        let params = this.userCustomLayoutParams();
        let wgt = JSON.parse(JSON.stringify(this.widget));
        for (let item of wgt.configuration.gridItems) {
          delete item.configuration.widgets;
        }
        $axios
          .post(`/proxy/api/user/preferences/saveUserPreference`, {
            ...params,
            dataValue: JSON.stringify(wgt),
            remark: '用户自定义网格排版'
          })
          .then(({ data }) => {
            this.swapSaving = false;
            this.hasUserCustom = true;
            this.$loading(false);
            resolve();
          })
          .catch(() => {
            this.swapping = true;
            this.$message.error('保存失败');
            this.swapSaving = false;
            this.$loading(false);
          });
      });
    },
    userCustomLayoutParams() {
      return { moduleId: this.widget.wtype, functionId: this.widget.wtype.toUpperCase(), dataKey: `${this.namespace}:${this.widget.id}` };
    },
    fetchUserCustomLayout() {
      return new Promise((resolve, reject) => {
        let params = this.userCustomLayoutParams();
        $axios
          .get(`/api/user/preferences/get`, {
            params: { ...params }
          })
          .then(({ data }) => {
            if (data.code == 0) {
              resolve(data.data ? JSON.parse(data.data.dataValue) : null);
            }
          });
      });
    },

    initGrid() {
      this.loading = false;
      this.$nextTick(() => {
        this.grid = GridStack.init(
          {
            column: this.widget.configuration.column,
            handleClass: 'grid-stack-item-drag-cover',
            cellHeight: this.cellHeight,
            cellHeightThrottle: 5,
            sizeToContent: false,
            margin: '0px 0px 12px 12px'
          },
          `#${this.gridstackInstId}`
        );
        window.grid = this.grid;
        let _this = this,
          contents = this.$el.querySelectorAll('.resize-to-content > div ');
        this.elementResizeDetector.removeAllListeners(contents);
        this.elementResizeDetector.listenTo(contents, function (element) {
          var height = element.scrollHeight;
          let id = element.parentElement.parentElement.getAttribute('id');
          let gridItems = _this.widget.configuration.gridItems;
          for (let i = 0, len = gridItems.length; i < len; i++) {
            if (id == gridItems[i].id) {
              if (gridItems[i].configuration.resizeToContent) {
                gridItems[i].configuration.itemPosition.h = Math.ceil(height / _this.cellHeight);
                _this.resizeToContent();
              }
              // if (_this.timeoutCheck['timeout_' + id]) {
              //   clearTimeout(_this.timeoutCheck['timeout_' + id]);
              //   console.log('== 清定时');
              // }
              // _this.timeoutCheck['timeout_' + id] = setTimeout(function () {
              //   console.log('执行定时', element.style.height);
              //   if (element.style.height == 'auto') {
              //     element.style.height = '100%';
              //   }
              // }, 500);
              return;
            }
          }
        });
      });
    }
  },
  beforeMount() {
    this.elementResizeDetector = new ElementResizeDetector();
  },
  mounted() {
    if (this.sentinelContext != undefined) {
      // 监听 tab 下的显示切换，当 tab 显示时候，需要重新初始化 grid ，否则会导致内容堆叠在一起或者内容没有高度等异常视图
      this.pageContext.handleEvent(`tab:${this.sentinelContext._uid}:changeTab`, activeKey => {
        if (this.$el.closest(`#${activeKey}`)) {
          this.refresh();
        }
      });
    }
    if (this.widget.configuration.enableUserCustom) {
      this.defaultGridItemPosition = {};
      let originalGridItems = this.widget.configuration.gridItems,
        itemPosition = {};
      for (let item of originalGridItems) {
        this.defaultGridItemPosition[item.id] = JSON.parse(JSON.stringify(item.configuration.itemPosition));
        itemPosition[item.id] = item.configuration.itemPosition;
      }
      // 获取用户的自定义排版
      this.fetchUserCustomLayout().then(wgt => {
        if (wgt) {
          this.hasUserCustom = true;
          // 更新位置
          let gridItems = wgt.configuration.gridItems;
          for (let item of gridItems) {
            if (itemPosition[item.id]) {
              itemPosition[item.id].x = item.configuration.itemPosition.x;
              itemPosition[item.id].y = item.configuration.itemPosition.y;
              itemPosition[item.id].h = item.configuration.itemPosition.h;
            }
          }
        }

        this.initGrid();
      });
    } else {
      this.initGrid();
    }
  }
};
</script>
