<template>
  <div class="widget-anchor-container">
    <template>
      <a-row
        type="flex"
        :class="'flex-direction-' + widget.configuration.flexDirection"
        :style="{ flexDirection: widget.configuration.flexDirection }"
      >
        <!-- <a-col flex="30px" v-if="levitate">
          <div class="collapse-bar" @click.stop="onCollapseAnchor">
            <div class="line line-1"></div>
            <div class="line line-1"></div>
            <div class="line line-2 active"></div>
            <div class="line line-2"></div>
            <div class="line line-1"></div>
          </div>
        </a-col> -->
        <a-col :flex="flex" class="anchor-col">
          <template v-if="widget.configuration.flexDirection === 'column'">
            <a-tabs
              @tabClick="onClickTabAnchor"
              :activeKey="selectedAnchorId"
              :style="tabStyle"
              :class="['widget-anchor-tab', widget.configuration.inkShape]"
            >
              <template v-for="anchor in widget.configuration.anchors">
                <a-tab-pane :key="anchor.id" v-if="anchor.label && !anchor.hidden">
                  <template slot="tab">
                    <!-- <span class="unactive-point"></span> -->
                    <label :title="anchor.label">{{ anchor.label }}</label>
                  </template>
                </a-tab-pane>
              </template>
            </a-tabs>
          </template>

          <div :style="{ position: 'fixed' }" :class="collapsed ? 'collapsed' : ''">
            <a-anchor
              :targetOffset="targetOffset"
              :wrapperClass="wrapperClass"
              @change="onAnchorChange"
              v-show="widget.configuration.flexDirection != 'column'"
              :wrapperStyle="{ padding: 'var(--w-padding-xs) var(--w-padding-md) var(--w-padding-xs) 0' }"
              :getContainer="getContainer"
            >
              <template v-for="(anchor, i) in widget.configuration.anchors">
                <AnchorLink :anchor="anchor" :key="'anchorlink_' + i" v-if="!anchor.hidden" :titleStyle="{ width: rowStyleWidth }" />
              </template>
            </a-anchor>
          </div>
        </a-col>
        <a-col flex="auto" :style="{ overflow: 'auto', width: widget.configuration.flexDirection == 'column' ? '' : '0' }">
          <template v-for="(wgt, ii) in widget.configuration.widgets">
            <component
              :key="wgt.id"
              :is="wgt.wtype"
              :widget="wgt"
              :index="ii"
              :widgetsOfParent="widget.configuration.widgets"
              :parent="widget"
            ></component>
          </template>
        </a-col>
      </a-row>
    </template>
  </div>
</template>
<script type="text/babel">
import widgetMixin from '@framework/vue/mixin/widgetMixin';
import { debounce, isEmpty } from 'lodash';
import AnchorLink from './anchor-link.vue';
export default {
  name: 'WidgetAnchor',
  inject: ['sentinelContext'],
  mixins: [widgetMixin],
  components: { AnchorLink },
  data() {
    return {
      targetOffset: 0,
      top: '200px',
      selectedAnchorId: null,
      tabStyle: {},
      anchorHrefMap: {},
      collapsed: false,
      isFirst: true,
      scrollisWindow: true
    };
  },
  watch: {},
  beforeCreate() {},

  computed: {
    wrapperClass() {
      let classes = ['widget-anchor'];
      if (this.widget.configuration.inkShape === 'line') {
        classes.push('ink-line');
      }
      // if (this.widget.configuration.flexDirection === 'row' && !this.levitate) {
      //   classes.push('align-right');
      // }
      // if (this.widget.configuration.flexDirection === 'row-reverse' && !this.levitate) {
      //   classes.push('align-left');
      // }
      return classes.join(' ');
    },
    levitate() {
      return this.widget.configuration.levitate;
    },
    flex() {
      return this.widget.configuration.flexDirection === 'column' ? '50px' : this.widget.configuration.levitate ? '0px' : '150px';
    },
    rowStyleWidth() {
      if (this.widget.configuration.flexDirection !== 'column') {
        return parseInt(this.flex) > 32 ? parseInt(this.flex) - 32 + 'px' : '';
      }
      return '';
    }
  },
  created() {},
  methods: {
    setAnchorFixed({ parent, widgets }) {
      try {
        if (
          widgets.length == 1 &&
          widgets[0].wtype == 'WidgetAnchor' &&
          widgets[0].configuration.flexDirection != 'column' &&
          this.findClosestScrollablePs() != null
        ) {
          let direction = 'left';
          if (widgets[0].configuration.flexDirection == 'row-reverse') {
            direction = 'right';
          }
          const anchor = this.$el.querySelector('.anchor-col');
          const anchorWidth = anchor.offsetWidth;
          const anchorTop = anchor.getBoundingClientRect().top;
          anchor.style.cssText += `;position:fixed; top:${anchorTop}px; width:${anchorWidth}px`;
          anchor.nextElementSibling.style.cssText += `;margin-${direction}:${anchorWidth}px`;
          this.onAnchorScroll(parent.$el, anchorTop);
        }
      } catch (error) {
        console.error(error);
      }
      try {
        if (
          widgets.length == 1 &&
          widgets[0].wtype == 'WidgetAnchor' &&
          widgets[0].configuration.flexDirection == 'column' &&
          this.findClosestScrollablePs() != null
        ) {
          const tabsBarHeight = document.querySelector('.widget-anchor-tab .ant-tabs-bar').offsetHeight;
          const anchor = this.$el.querySelector('.anchor-col');
          const anchorPosition = anchor.getBoundingClientRect();
          this.tabStyle = {
            position: 'fixed',
            'z-index': '1000',
            'background-color': '#fff',
            width: `${anchorPosition.width}px`,
            height: `${tabsBarHeight}px`,
            top: `${anchorPosition.top}px`,
            left: `${anchorPosition.left}px`
          };
        }
      } catch (error) {
        console.error(error);
      }
    },
    cascadeSetHidden(anchors) {
      if (anchors && anchors.length) {
        let formatAnchors = [];
        const setHidden = arr => {
          arr.map((item, index) => {
            const anchorTarget = document.querySelector('#' + item.href);
            const hidden = anchorTarget == null;
            if (this.selectedAnchorId === null && !hidden) {
              this.selectedAnchorId = item.id;
            }
            if (hidden && item.anchors && item.anchors.length) {
              arr.splice(index, 1, ...item.anchors);
            }
            item.hidden = hidden;
            this.anchorHrefMap[item.href] = item;

            return {
              ...item,
              anchors: item.anchors.length ? setHidden(item.anchors) : []
            };
          });
          formatAnchors = arr;
        };

        setHidden(anchors);
        this.$set(this.widget.configuration, 'anchors', formatAnchors);
      }
    },
    onAnchorScroll(dom, anchorTop) {
      for (const key in this.anchorHrefMap) {
        const anchorHref = this.anchorHrefMap[key];
        if (anchorHref.hidden) {
          continue;
        }
        anchorHref.rectTop = this.$el.querySelector(`#${key}`).getBoundingClientRect().top;
      }
      const anchorLinks = this.$el.querySelectorAll('.widget-anchor.ant-anchor-wrapper .ant-anchor-link-title');
      const anchorInkBall = this.$el.querySelector('.ant-anchor-ink-ball'); // 锚点高亮的点
      const _this = this;

      dom.addEventListener(
        'scroll',
        debounce(function (e) {
          const lastScrollTop = e.target.__vue__.ps.lastScrollTop;
          let anchorScrollTop = lastScrollTop + anchorTop;
          if (_this.configuration.offset.type == 'offsetTop') {
            // 上偏移
            anchorScrollTop += _this.configuration.offset.value;
          } else {
            anchorScrollTop -= _this.configuration.offset.value;
          }
          _this.setAnchorHighlight(anchorScrollTop, anchorLinks, anchorInkBall);
        }, 100)
      );
    },
    // 设置高亮
    setAnchorHighlight(anchorScrollTop, anchorLinks, anchorInkBall) {
      for (const key in this.anchorHrefMap) {
        const anchorHref = this.anchorHrefMap[key];
        if (anchorHref.hidden) {
          continue;
        }
        if (anchorScrollTop >= anchorHref.rectTop) {
          anchorLinks.forEach(item => {
            if (item.getAttribute('href').indexOf(key) != -1) {
              anchorInkBall.style.top = `${item.offsetTop + item.clientHeight / 2 - 4.5}px`;
              item.classList.add('ant-anchor-link-title-active');
              item.parentNode.classList.add('ant-anchor-link-active');
            } else {
              item.classList.remove('ant-anchor-link-title-active');
              item.parentNode.classList.remove('ant-anchor-link-active');
            }
          });
        }
      }
    },
    onScroll(e) {
      let offsetTop = this.$el.querySelector('.ant-row-flex').offsetTop,
        _this = this,
        psPosition = undefined;
      this.getContainer().addEventListener(
        'scroll',
        debounce(function (e) {
          let scrollTop = document.documentElement.scrollTop || window.pageYOffset || document.body.scrollTop;
          if (!_this.scrollisWindow) {
            psPosition = _this.getContainer().getBoundingClientRect();
            scrollTop = _this.getContainer().scrollTop;
          }
          if (scrollTop > offsetTop) {
            if (isEmpty(_this.tabStyle)) {
              _this.tabStyle = {
                position: 'fixed',
                'z-index': '1000', //弹框一般都是1000，不能比弹框高
                'background-color': '#fff',
                width: '100%',
                height: '50px',
                top: _this.scrollisWindow ? 0 : `${psPosition.top}px`
              };
            }
          } else {
            _this.tabStyle = {};
          }
        }, 10)
      );
    },
    translateAnchorLabel() {
      let cascadeSetLabel = anchors => {
        if (anchors)
          for (let j = 0, jlen = anchors.length; j < jlen; j++) {
            anchors[j].label = this.$t(anchors[j].id, anchors[j].label);
            cascadeSetLabel(anchors[j].anchors);
          }
      };
      for (let i = 0, len = this.widget.configuration.anchors.length; i < len; i++) {
        let anchor = this.widget.configuration.anchors[i];
        anchor.label = this.$t(anchor.id, anchor.label);
        cascadeSetLabel(anchor.anchors);
      }
      console.log(this.widget.configuration.anchors);
    },
    batchBindVisibleChange(off) {
      // 锚点是被动等待接受通知是否展示的，为了应对组件可能因为规则存在随时变化显示状态的情况
      for (let i = 0, len = this.widget.configuration.anchors.length; i < len; i++) {
        let anchor = this.widget.configuration.anchors[i];
        // anchor.hidden = document.querySelector('#' + anchor.href) == null;
        // if (this.selectedAnchorId === null && !anchor.hidden) {
        //   this.selectedAnchorId = anchor.id;
        // }

        this.bindVisibleChange(anchor.href, off);
        this.cascadeBindVisibleChange(anchor.anchors, off);
      }
    },

    cascadeBindVisibleChange(anchors, off) {
      if (anchors)
        for (let j = 0, jlen = anchors.length; j < jlen; j++) {
          this.bindVisibleChange(anchors[j].href, off);
          this.cascadeBindVisibleChange(anchors[j].anchors, off);
        }
    },

    bindVisibleChange(href, off) {
      let _this = this;
      this.pageContext.handleEvent(`Widget:${href}:VisibleChange`, function (visible) {
        // 找到 href 对应的配置设置
        if (_this.anchorHrefMap[href] != undefined) {
          _this.anchorHrefMap[href].hidden = !visible;
        }
      });
    },
    onAnchorChange(link) {
      for (let i = 0, len = this.widget.configuration.anchors.length; i < len; i++) {
        let anchor = this.widget.configuration.anchors[i];
        if (link.indexOf(anchor.href) !== -1) {
          this.selectedAnchorId = anchor.id;
          break;
        }
      }
      if (this.isFirst) {
        // 初始默认选中第一个
        this.isFirst = false;
        if (this.widget.configuration.anchors.length) {
          this.$el.querySelector('[href="#' + this.widget.configuration.anchors[0].href + '"]').click();
        }
      }
    },
    onClickTabAnchor(key) {
      this.selectedAnchorId = key;
      for (let i = 0, len = this.widget.configuration.anchors.length; i < len; i++) {
        let anchor = this.widget.configuration.anchors[i];
        if (anchor.id === key) {
          this.$el.querySelector('[href="#' + anchor.href + '"]').click();
          break;
        }
      }
    },
    onCollapseAnchor() {
      this.collapsed = !this.collapsed;
    },
    findClosestScrollablePs() {
      if (this.closestScrollablePs) {
        return this.closestScrollablePs;
      }
      let ps = this.$el.closest('.ps');
      while (ps != null) {
        if (ps.style.height == '100%' || ps.style.height == 'auto') {
          ps = ps.parentElement ? ps.parentElement.closest('.ps') : null;
        } else {
          this.closestScrollablePs = ps;
          return ps;
        }
      }
      return null;
    },
    getContainer() {
      let closetPs = this.findClosestScrollablePs();
      if (closetPs != null) {
        this.scrollisWindow = false;
        return closetPs;
      }
      return window; //this.$el.parentNode;
    }
  },
  beforeMount() {
    if (!this.designMode) {
      let value = this.widget.configuration.offset.value;
      this.targetOffset = this.widget.configuration.offset.type === 'offsetTop' ? value : document.body.scrollHeight - value;
    }
  },
  mounted() {
    if (!this.designMode) {
      this.pageContext.handleEvent('dyformParentAndwidgets', params => {
        let underInactiveTabpane = this.$el.closest('.ant-tabs-tabpane-inactive');
        if (underInactiveTabpane == null) {
          this.setAnchorFixed(params);
        } else {
          if (this.sentinelContext != undefined) {
            this.$watch('sentinelContext.activeKey', function () {
              this.$nextTick(() => {
                this.setAnchorFixed(params);
              });
            });
          }
        }
      });

      this.translateAnchorLabel();
      this.batchBindVisibleChange(false);
      // 初始化情况锚点都先隐藏，由锚点组件挂载成功后通知锚点进行展示
      this.cascadeSetHidden(this.widget.configuration.anchors);
      if (this.widget.configuration.flexDirection === 'column') {
        this.onScroll();
      }
    }
  },
  destroyed() {}
};
</script>

<style lang="less">
.widget-anchor-container .widget-anchor-tab .ant-tabs-nav-animated {
  position: static;
}
</style>
