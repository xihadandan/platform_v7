<template>
  <a-card
    v-show="vShow"
    :key="widget.id"
    class="widget-card"
    :size="widget.configuration.size"
    :bordered="widget.configuration.bordered"
    :bodyStyle="{}"
    :style="vCardStyle"
  >
    <template slot="title" v-if="!widget.configuration.hideTitle">
      <div class="flex">
        <div
          class="w-ellipsis"
          style="max-width: calc(100% - 80px); padding-right: var(--w-padding-2xs)"
          :title="title || $t('title', widget.configuration.title)"
        >
          <span v-if="widget.configuration.titleIcon" style="margin-right: var(--w-margin-2xs)">
            <Icon :type="widget.configuration.titleIcon" :size="widget.configuration.size" />
          </span>
          <!-- 更新名称后使用更新后名称 -->
          {{ title || $t('title', widget.configuration.title) }}
        </div>
        <div class="f_s_0" v-if="widget.configuration.badge && widget.configuration.badge.enable">
          <a-badge :count="widget.configuration.badge.count" :showZero="true" />
        </div>
      </div>
      <div v-if="!widget.configuration.hideSubTitle && widget.configuration.subTitle" style="color: rgba(0, 0, 0, 0.45)">
        {{ $t('subTitle', widget.configuration.subTitle) }}
      </div>
    </template>
    <template slot="extra">
      <keyword-search :widget="widget" :configuration="widget.configuration" />
      <template v-if="widget.configuration.headerButton && widget.configuration.headerButton.enable">
        <WidgetTableButtons
          :button="widget.configuration.headerButton"
          :developJsInstance="developJsInstance"
          :visibleJudgementData="visibleJudgementData"
        />
      </template>
    </template>
    <Scroll :style="{ height: bodyHeight }">
      <div>
        <template v-for="(wgt, windex) in widget.configuration.widgets">
          <component
            :key="wgt.id"
            :is="wgt.wtype"
            :widget="wgt"
            :index="windex"
            :widgetsOfParent="widget.configuration.widgets"
            :parent="widget"
          ></component>
        </template>
      </div>
    </Scroll>
  </a-card>
</template>

<script type="text/babel">
import widgetMixin from '@framework/vue/mixin/widgetMixin';
import { DispatchEvent } from '@framework/vue/lib/dispatchEvent';
import DataSourceBase from '../../assets/js/commons/dataSource.base';
import KeywordSearch from './components/keyword-search.vue';
import './css/index.less';
export default {
  name: 'WidgetCard',
  mixins: [widgetMixin],
  data() {
    return {
      title: '',
      borderHeight: 0
    };
  },
  provide() {
    return {
      containerStyle: {
        height: undefined
      }
      // $containerHeight: undefined // FIXME: 计算容器高度，以便提供给子组件需要容器高度来计算的需要，例如布局组件
    };
  },
  components: {
    KeywordSearch
  },
  computed: {
    visibleJudgementData() {
      return {
        ...this._vShowByDateTime,
        ...this._vShowByUserData,
        ...this._vShowByWorkflowData,
        ...(this.dyform != undefined ? { __DYFORM__: { editable: this.dyform.displayState == 'edit' } } : {}),
        ...(this.dyform != undefined ? this.dyform.formData || {} : this._showByData || {}),
        _URL_PARAM_: this.vUrlParams
      };
    },
    vCardStyle() {
      let style = {};
      if (this.widget.configuration.height == '100%') {
        style.height = '100%';
      }
      if (this.widget.configuration.fillContainer) {
        style.height = '100%';
        style.width = '100%';
        style.position = 'absolute';
      }
      return style;
    },
    bodyHeight() {
      if (this.widget.configuration.height == 'auto' || this.widget.configuration.height == undefined) {
        return 'auto';
      }
      let padding = this.widget.configuration.style.padding[0] + this.widget.configuration.style.padding[2];
      return `calc( ${this.widget.configuration.height} - ${padding}px - ${this.borderHeight}px )`;
    }
  },
  mounted() {
    let _this = this;
    // if (this.widget.configuration.fillHeight) {
    //   this.recomputeProvideHeight();
    // }
    if (this.widget.configuration.bordered) {
      let _style = window.getComputedStyle(this.$el);
      this.borderHeight = parseInt(_style.borderTopWidth) + parseInt(_style.borderBottomWidth);
    }
    // 计算徽标
    this.computeBadge();
    this.pageContext.handleEvent(`${this.widget.id}:refetchBadge`, function () {
      _this.computeBadge();
    });
  },
  methods: {
    updateTitle(title) {
      this.title = title;
    },
    refetchBadge() {
      this.computeBadge();
    },
    computeBadge() {
      let _this = this;
      if (this.widget.configuration.badge && this.widget.configuration.badge.enable) {
        let badge = this.widget.configuration.badge;
        let { badgeSourceType, dataSourceId, countJsFunction, defaultCondition, dataModelUuid } = badge;
        if (badgeSourceType == 'jsFunction' && countJsFunction) {
          new DispatchEvent({
            actionType: 'jsFunction',
            jsFunction: countJsFunction,
            $developJsInstance: this.$developJsInstance,
            $evtWidget: _this,
            meta: _this.widget,
            after: num => {
              _this.$set(badge, 'count', num);
            }
          }).dispatch();
        } else if (badgeSourceType == 'dataModel' || badgeSourceType == 'dataSource') {
          let _this = this;
          // 创建数据源
          let dsOptions = {
            onDataChange: function (data, count, params) {
              _this.$set(badge, 'count', count);
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
      }
    }
    // 重新计算可以提供给子组件的最大高度
    // recomputeProvideHeight() {
    //   if (this.$el) {
    //     //FIXME: 计算高度
    //     // 父级有传递可用高度下，计算本容器的可用高度上限
    //     let $head = this.$el.querySelector('.ant-card-head'),
    //       headHeight = 0,
    //       padding = 0,
    //       $cardBody = this.$el.querySelector('.ant-card-body');
    //     if ($head) {
    //       let _style = window.getComputedStyle($head);
    //       headHeight = $head.getBoundingClientRect().height + parseFloat(_style.marginBottom) + parseFloat(_style.marginTop);
    //     }
    //     if ($cardBody) {
    //       let _style = window.getComputedStyle($cardBody);
    //       padding = parseFloat(_style.paddingTop) + parseFloat(_style.paddingBottom);
    //     }
    //     let height = parseFloat(this.vProvideHeight) - headHeight;
    //     return {
    //       height: height + 'px',
    //       vProvideHeight: height - padding + 'px'
    //     };
    //   }
    //   return {
    //     height: 'auto',
    //     vProvideHeight: this.vProvideHeight
    //   };
    // }
  }
};
</script>
