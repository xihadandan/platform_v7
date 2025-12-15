<template>
  <div :class="['widget-grid-info-block']" :style="widgetStyle">
    <div v-if="designMode" style="position: absolute; width: 100%; height: 100%; z-index: 1; top: 0; left: 0"></div>
    <div class="block-head" v-if="widget.configuration.title">
      <div class="title">{{ $t('title', widget.configuration.title) }}</div>
    </div>
    <div
      :style="{
        display: 'flex',
        flexWrap: 'wrap'
      }"
    >
      <template v-for="(item, i) in infoItems">
        <div :key="'item_' + i" :style="getItemBlockStyle(item, i)">
          <div
            :style="[vItemStyle, item.hoverStyle || {}]"
            class="item"
            @mouseover="mouseoverHandle(i)"
            @mouseleave="mouseleaveHandle(i)"
            :class="[
              widget.configuration.itemStyle.displayStyle || 'iconLeft_textRight',
              itemBadgeNum[item.id] != undefined ? 'hasBadgeNum' : ''
            ]"
          >
            <div class="icon-box-container">
              <div class="icon-box" :style="vAvatarStyle" v-if="item.avatar.icon != undefined">
                <!-- 图片 -->
                <img
                  :src="item.avatar.icon"
                  v-if="item.avatar.icon.startsWith('/')"
                  :style="{
                    width: '100%',
                    height: '100%'
                  }"
                />
                <Icon class="icon" :type="item.avatar.icon" />
              </div>
            </div>
            <div class="text-box" :style="[titleStyle]" @click.stop="e => onClickTitle(e, item)">
              <div :class="titleClass">{{ $t(item.id + '_title', item.title) }}</div>
              <div v-if="item.subTitle" :class="subTitleClass" class="subtext">{{ $t(item.id + '_subTitle', item.subTitle) }}</div>
            </div>
            <div class="badge-num" v-if="itemBadgeNum[item.id] != undefined" :style="getBadgeStyle(item)">
              <a-spin v-if="itemBadgeNum[item.id] === true">
                <a-icon slot="indicator" type="loading" style="font-size: 24px" spin />
              </a-spin>
              <template v-else>
                <a-badge
                  :count="itemBadgeNum[item.id]"
                  :dot="widget.configuration.badgeStyle.badgeDisplayType == 'dot'"
                  :overflowCount="overflowCount"
                  :class="[widget.configuration.badgeStyle.badgeDisplayType == 'text' ? 'just-label' : '']"
                  :showZero="widget.configuration.badgeStyle.showZero"
                />
              </template>
            </div>
          </div>
        </div>
      </template>
    </div>

    <a-empty v-if="designMode && infoItems.length == 0" />
  </div>
</template>
<script type="text/babel">
import './css/index.less';
import widgetMixin from '@framework/vue/mixin/widgetMixin';
import DataSourceBase from '../../assets/js/commons/dataSource.base';
import { assign } from 'lodash';
import { over } from 'lodash';

export default {
  name: 'WidgetGridInfoBlock',
  mixins: [widgetMixin],
  props: {},
  components: {},
  computed: {
    widgetStyle() {
      let style = {};
      if (this.widget.configuration.padding) {
        style.padding = this.widget.configuration.padding;
      }
      return style;
    },

    vAvatarStyle() {
      let avatarStyle = this.widget.configuration.avatarStyle;
      let style = {
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        width: avatarStyle.width + 'px',
        height: avatarStyle.width + 'px'
      };
      if (avatarStyle.backgroundColor != undefined) {
        style.backgroundColor = avatarStyle.backgroundColor.startsWith('--w-')
          ? `var(${avatarStyle.backgroundColor})`
          : avatarStyle.backgroundColor;
      }
      if (avatarStyle.borderRadius != undefined) {
        if (Array.isArray(avatarStyle.borderRadius)) {
          style.borderRadius = avatarStyle.borderRadius
            .map(item => {
              return item + 'px';
            })
            .join(' ');
        } else {
          style.borderRadius = avatarStyle.borderRadius + 'px';
        }
      }
      if (avatarStyle.borderWidth != undefined && avatarStyle.borderWidth > 0) {
        style.borderWidth = avatarStyle.borderWidth + 'px';
        if (avatarStyle.borderColor != undefined) {
          style.borderColor = avatarStyle.borderColor.startsWith('--w-') ? `var(${avatarStyle.borderColor})` : avatarStyle.borderColor;
        }
        style.borderStyle = 'solid';
      }
      style.fontSize = `${avatarStyle.fontSize}px`;
      if (avatarStyle.color != undefined) {
        style.color = avatarStyle.color.startsWith('--w-') ? `var(${avatarStyle.color})` : avatarStyle.color;
      }

      return style;
    },

    vItemStyle() {
      let style = {};

      let itemStyle = this.widget.configuration.itemStyle;
      if (itemStyle.backgroundColor != undefined) {
        style.backgroundColor = itemStyle.backgroundColor.startsWith('--w-')
          ? `var(${itemStyle.backgroundColor})`
          : itemStyle.backgroundColor;
      }
      if (itemStyle.borderRadius != undefined) {
        if (Array.isArray(itemStyle.borderRadius)) {
          style.borderRadius = itemStyle.borderRadius
            .map(item => {
              return item + 'px';
            })
            .join(' ');
        } else {
          style.borderRadius = itemStyle.borderRadius + 'px';
        }
      }
      if (itemStyle.borderWidth != undefined && itemStyle.borderWidth > 0) {
        style.borderWidth = itemStyle.borderWidth + 'px';
        style.borderStyle = 'solid';
        if (itemStyle.borderColor != undefined) {
          style.borderColor = itemStyle.borderColor.startsWith('--w-') ? `var(${itemStyle.borderColor})` : itemStyle.borderColor;
        }
      }
      if (itemStyle.padding) {
        style.padding = itemStyle.padding;
      }
      if (itemStyle.cursorPointer) {
        style.cursor = 'pointer';
      }

      assign(style, this.getBackGroundStyle(itemStyle.backgroundStyle));

      return style;
    },
    vItemHoverStyle() {
      let style = {};

      let hoverStyle = this.widget.configuration.itemStyle.hoverStyle || {};
      if (hoverStyle.type) {
        if (hoverStyle.type.indexOf('backgroundColor') > -1 && hoverStyle.backgroundColor) {
          style.backgroundColor = hoverStyle.backgroundColor.startsWith('--w-')
            ? `var(${hoverStyle.backgroundColor})`
            : hoverStyle.backgroundColor;
        }
        if (hoverStyle.type.indexOf('borderColor') > -1 && hoverStyle.borderColor) {
          style.borderColor = hoverStyle.borderColor.startsWith('--w-') ? `var(${hoverStyle.borderColor})` : hoverStyle.borderColor;
        }
        if (hoverStyle.type.indexOf('boxShadow') > -1 && hoverStyle.boxShadow) {
          style.boxShadow = hoverStyle.boxShadow;
        }
      }
      console.log('vItemHoverStyle', style);
      return style;
    },
    titleClass() {
      let arr = [];
      let titleStyle = this.widget.configuration.titleStyle;
      if (titleStyle) {
        if (titleStyle.titleEllipsis) {
          arr.push('w-ellipsis-' + titleStyle.titleEllipsis);
        }
      }
      return arr.join(' ');
    },
    subTitleClass() {
      let arr = [];
      let titleStyle = this.widget.configuration.titleStyle;
      if (titleStyle) {
        if (titleStyle.subTitleEllipsis) {
          arr.push('w-ellipsis-' + titleStyle.subTitleEllipsis);
        }
      }
      return arr.join(' ');
    },
    titleStyle() {
      let titleStyle = this.widget.configuration.titleStyle || {};
      let style = {};
      if (titleStyle.padding) {
        style.padding = titleStyle.padding;
      }
      if (titleStyle.margin) {
        style.margin = titleStyle.margin;
      }
      if (titleStyle.size) {
        style.fontSize = titleStyle.size + 'px';
      }
      if (titleStyle.weight) {
        style.fontWeight = titleStyle.weight;
      }
      if (titleStyle.color) {
        style.color = this.getColorValue(titleStyle.color);
      }
      if (titleStyle.subSize) {
        style['--w-widget-grid-info-item-subtitle-size'] = titleStyle.subSize + 'px';
      }
      if (titleStyle.subColor) {
        style['--w-widget-grid-info-item-subtitle-color'] = titleStyle.subColor;
      }
      if (titleStyle.subMargin) {
        style['--w-widget-grid-info-item-subtitle-margin'] = titleStyle.subMargin;
      }
      return style;
    },
    overflowCount() {
      let badgeStyle = this.widget.configuration.badgeStyle || {};
      if (badgeStyle.badgeOverflowCountShowType == 'customize') {
        return badgeStyle.badgeOverflowCount;
      } else if (badgeStyle.badgeOverflowCountShowType == 'limitless') {
        return Infinity;
      }
      return undefined;
    }
  },
  data() {
    return { itemBadgeNum: {}, infoItems: this.widget.configuration.buildType == 'define' ? this.widget.configuration.infoItems : [] };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {
    if (!this.designMode && this.widget.configuration.buildType == 'define' && this.widget.configuration.infoItems.length > 0) {
      this.widget.configuration.infoItems.forEach(item => {
        if (item.badge != undefined && item.badge.enable) {
          this.$set(this.itemBadgeNum, item.id, true);
          this.fetchBadgeNum(item.badge).then(count => {
            this.$set(this.itemBadgeNum, item.id, count);
          });
        }
      });
    }
  },
  mounted() {
    if (this.widget.configuration.buildType != 'define') {
      let ds = this.getDataSourceProvider();
      if (ds) {
        ds.load();
      }
    }
  },
  methods: {
    getItemBlockStyle(item, i) {
      let column = this.widget.configuration.column;
      let style = {
        marginBottom: this.widget.configuration.gutter + 'px',
        width: 'calc(100% / ' + column + ')'
      };
      style.paddingRight = this.widget.configuration.gutter / 2 + 'px';
      style.paddingLeft = this.widget.configuration.gutter / 2 + 'px';
      if (i == 0 || i % column == 0) {
        // 每行的第一个
        style.paddingLeft = '0px';
        if ((i + 1) % column == 0) {
          // 每行的最后一个
          style.paddingRight = '0px';
        }
      } else if ((i + 1) % column == 0) {
        // 每行的最后一个
        style.paddingRight = '0px';
      }
      // 最后一行
      if (Math.floor(i / column) + 1 == Math.ceil(this.infoItems.length / column)) {
        style.marginBottom = '0px';
      }

      return style;
    },
    getBackGroundStyle(data) {
      let style = {};
      if (data) {
        let {
          backgroundColor,
          backgroundImage,
          backgroundImageInput,
          bgImageUseInput,
          backgroundPosition,
          backgroundRepeat,
          backgroundSize
        } = data;

        if (backgroundColor) {
          style.backgroundColor = this.getColorValue(backgroundColor);
        }
        let bgImgStyle = bgImageUseInput ? backgroundImageInput : backgroundImage;
        if (bgImgStyle) {
          let isUrl =
            bgImgStyle.startsWith('data:') ||
            bgImgStyle.startsWith('http') ||
            bgImgStyle.startsWith('/') ||
            bgImgStyle.startsWith('../') ||
            bgImgStyle.startsWith('./');
          style.backgroundImage = isUrl ? `url("${bgImgStyle}")` : bgImgStyle;
        }
        if (backgroundPosition) {
          style.backgroundPosition = backgroundPosition;
        }
        if (backgroundRepeat) {
          style.backgroundRepeat = backgroundRepeat;
        }
        if (backgroundSize) {
          style.backgroundSize = backgroundSize;
        }
      }
      return style;
    },
    getColorValue(color) {
      if (color) {
        return color.startsWith('#') ? color : `var(${color})`;
      }
      return '';
    },
    onClickTitle(e, item) {
      if (item.clickEvent && item.clickEvent.enable) {
        let eventHandler = item.clickEvent.eventHandler;
        if (eventHandler.trigger === 'click') {
          eventHandler.meta = {};
          eventHandler.$evt = e;
          eventHandler.$evtWidget = this;
          eventHandler.key = item.id;
          this.dispatchEventHandler(eventHandler);
        }
        e.stopPropagation();
      } else {
        this.triggerDomEvent('onClickTitle', undefined, item, item.id);
      }
    },
    getDataSourceProvider() {
      if (this.dataSourceProvider == undefined) {
        let _this = this;
        // 创建数据源
        let dsOptions = {
          onDataChange: function (data, count, params) {
            _this.onLoadItemData(data, count, params);
          },
          receiver: this,
          params: this.widget.configuration.dataSourceParams || {},
          // exportColumns: _this.getExportColumns(),
          // renderers: _this.getRenderers(),
          defaultCriterions: this.getDefaultCondition(),
          pageSize: 20
        };
        if (this.widget.configuration.dataSourceId && this.widget.configuration.buildType == 'dataSource') {
          dsOptions.dataStoreId = this.widget.configuration.dataSourceId;
          this.dataSourceProvider = new DataSourceBase(dsOptions);
        } else if (this.widget.configuration.buildType == 'dataModel' && this.widget.configuration.dataModelUuid) {
          dsOptions.loadDataUrl = '/proxy/api/dm/loadData/' + this.widget.configuration.dataModelUuid;
          dsOptions.loadDataCntUrl = '/proxy/api/dm/loadDataCount/' + this.widget.configuration.dataModelUuid;
          this.dataSourceProvider = new DataSourceBase(dsOptions);
        }
      }
      return this.dataSourceProvider;
    },
    onLoadItemData(result) {
      let list = result.data,
        { itemTitleColumn, itemSubTitleColumn, itemAvatarIconColumn, itemBadgeNumColumn } = this.widget.configuration,
        labels = [],
        items = [];
      for (let i = 0, len = list.length; i < len; i++) {
        this.$set(this.itemBadgeNum, i, list[i][itemBadgeNumColumn]);
        items.push({
          id: i,
          title: list[i][itemTitleColumn],
          subTitle: list[i][itemSubTitleColumn],
          avatar: { icon: list[i][itemAvatarIconColumn] },
          data: list[i]
        });
        if (list[i][itemTitleColumn] != undefined) {
          labels.push(list[i][itemTitleColumn]);
        }
        if (list[i][itemSubTitleColumn] != undefined) {
          labels.push(list[i][itemSubTitleColumn]);
        }
      }
      if (this.widget.configuration.autoTranslate && this.$i18n.locale !== 'zh_CN') {
        this.$translate(labels, 'zh', this.$i18n.locale.split('_')[0]).then(map => {
          for (let i of items) {
            if (i.title && map[i.title.trim()]) {
              i.title = map[i.title.trim()];
            }
            if (i.subTitle && map[i.subTitle.trim()]) {
              i.subTitle = map[i.subTitle.trim()];
            }
          }
          this.infoItems.push(...items);
        });
      } else {
        this.infoItems.push(...items);
      }
    },
    getDefaultCondition() {
      if (this.configuration.defaultCondition) {
        return [
          {
            sql: this.configuration.defaultCondition // this.pageContext.resolveStringVueTemplate(this.configuration.defaultCondition, { location: location })
          }
        ];
      }
      return [];
    },
    getBadgeStyle(item) {
      let style = {},
        badgeStyle = this.widget.configuration.badgeStyle;
      if (badgeStyle.colorType == 'value') {
        if (badgeStyle.color != undefined) {
          style.color = badgeStyle.color.startsWith('--w-') ? `var(${badgeStyle.color})` : badgeStyle.color;
        }
      } else if (badgeStyle.colorType == 'function') {
        try {
          let func = new Function('item', 'count', badgeStyle.color);
          style.color = func(item, this.itemBadgeNum[item.id]);
        } catch (error) {
          console.error(error);
        }
      }
      ['left', 'top', 'right', 'bottom'].forEach(b => {
        let unit = badgeStyle[b + 'Unit'];
        if (badgeStyle[b] != null && unit) {
          style[b] = badgeStyle[b] + unit;
        }
      });
      return style;
    },
    fetchBadgeNum(badge) {
      let _this = this;
      return new Promise((resolve, reject) => {
        let { badgeSourceType, dataSourceId, defaultCondition, dataModelUuid } = badge;
        if (badgeSourceType == 'countJsFunction') {
          let parts = badge.countJsFunction.split('.');
          let promise = this.developJsInstance()[parts[0]][parts[1]]();
          promise.then(count => {
            resolve(count);
          });
        } else if (badgeSourceType == 'dataModel' || badgeSourceType == 'dataSource') {
          let _this = this;
          // 创建数据源
          let dsOptions = {
            onDataChange: function (data, count, params) {
              resolve(count);
            },
            receiver: _this,
            params: {},
            defaultCriterions: defaultCondition
              ? [
                  {
                    sql: defaultCondition
                  }
                ]
              : []
          };
          if (dataSourceId && badgeSourceType == 'dataSource') {
            dsOptions.dataStoreId = dataSourceId;
            new DataSourceBase(dsOptions).getCount(true);
          } else if (badgeSourceType == 'dataModel' && dataModelUuid) {
            dsOptions.loadDataUrl = '/proxy/api/dm/loadData/' + dataModelUuid;
            dsOptions.loadDataCntUrl = '/proxy/api/dm/loadDataCount/' + dataModelUuid;
            new DataSourceBase(dsOptions).getCount(true);
          }
        }
      });
    },
    mouseoverHandle(index) {
      if (!this.infoItems[index].hoverStyle) {
        this.$set(this.infoItems[index], 'hoverStyle', this.vItemHoverStyle);
      }
    },
    mouseleaveHandle(index) {
      this.$set(this.infoItems[index], 'hoverStyle', undefined);
    }
  }
};
</script>
