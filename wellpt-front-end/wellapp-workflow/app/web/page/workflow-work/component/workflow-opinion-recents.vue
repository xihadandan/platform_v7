<template>
  <div class="opinion-tabs-container">
    <a-tabs default-active-key="1" :tabBarGutter="24">
      <a-tab-pane key="1" :tab="$t('WorkflowWork.opinionManager.recentTabTitle', '最近使用')">
        <Scroll :style="listStyle">
          <a-list :data-source="recents" :style="listStyle">
            <a-list-item
              slot="renderItem"
              slot-scope="item, index"
              :title="item.content"
              @click="onOpinionItemClick($event, item)"
              @mouseenter="onOpinionItemMouseEnter($event, item)"
              @mouseleave="onOpinionItemMouseLeave($event, item)"
            >
              <span class="item-content">{{ item.content }}</span>
              <a-button
                v-if="allowQuickSubmit && quickSubmit && item.showSignAndSubmitBtn && (workView.isNewWork() || workView.isTodo())"
                type="primary"
                size="small"
                :disabled="(workView.$widget && workView.$widget.displayState == 'disable') || workView.loading.visible"
                @click.stop="onSignAndSubmitClick($event, item, index)"
              >
                {{ $t('WorkflowWork.opinionManager.operation.signOpinionAndSubmit', '签署并提交') }}
              </a-button>
            </a-list-item>
          </a-list>
        </Scroll>
      </a-tab-pane>
      <a-tab-pane key="2" :tab="$t('WorkflowWork.opinionManager.commonTabTitle', '常用意见')">
        <Scroll :style="listStyle">
          <a-list :data-source="commonOpinionCategory.opinions" :style="listStyle">
            <a-list-item
              slot="renderItem"
              slot-scope="item, index"
              :title="item.content"
              @click="onOpinionItemClick($event, item)"
              @mouseenter="onOpinionItemMouseEnter($event, item)"
              @mouseleave="onOpinionItemMouseLeave($event, item)"
            >
              <span class="item-content">{{ item.content }}</span>
              <a-button
                v-if="allowQuickSubmit && quickSubmit && item.showSignAndSubmitBtn && (workView.isNewWork() || workView.isTodo())"
                type="primary"
                size="small"
                :disabled="(workView.$widget && workView.$widget.displayState == 'disable') || workView.loading.visible"
                @click.stop="onSignAndSubmitClick($event, item, index)"
              >
                {{ $t('WorkflowWork.opinionManager.operation.signOpinionAndSubmit', '签署并提交') }}
              </a-button>
            </a-list-item>
          </a-list>
        </Scroll>
      </a-tab-pane>
      <template slot="tabBarExtraContent">
        <a-space class="setting-icon">
          <a-button slot="extra" type="icon" class="icon-only" @click="onOpinionManagerClick">
            <Icon type="pticon iconfont icon-ptkj-shezhi" :title="$t('WorkflowWork.opinionManager.operation.setting', '设置')" />
          </a-button>
          <slot name="extraIcon"></slot>
        </a-space>
      </template>
    </a-tabs>

    <!-- 意见管理 -->
    <div v-if="initOpinionManager">
      <WorkflowOpinionManager
        :ref="opinionManagerName"
        :recents="recents"
        :commonOpinionCategory="commonOpinionCategory"
        :publics="publics"
        @recentOpinionDeleted="onRecentOpinionDeleted"
      ></WorkflowOpinionManager>
    </div>
  </div>
</template>

<script>
export default {
  name: 'WorkflowOpinionRecents',
  props: {
    workView: Object,
    opinionData: {
      type: Object,
      default() {
        return {
          recents: [],
          commonOpinionCategory: { opinions: [] },
          publics: []
        };
      }
    },
    listStyle: {
      type: Object,
      default() {
        return {
          height: '196px'
        };
      }
    },
    quickSubmit: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      allowQuickSubmit: true,
      opinionManagerName: 'opinionManager',
      initOpinionManager: false,
      recents: this.opinionData.recents,
      commonOpinionCategory: this.opinionData.commonOpinionCategory,
      publics: this.opinionData.publics
    };
  },
  created() {
    this.workView.getSettings().then(settings => {
      let opinionEditorSetting = settings.get('OPINION_EDITOR') || {};
      this.allowQuickSubmit = opinionEditorSetting.showSignAndSubmitButton != null ? opinionEditorSetting.showSignAndSubmitButton : true;
    });
  },
  watch: {
    opinionData: {
      deep: false,
      handler: function (newValue) {
        this.recents = newValue.recents;
        this.commonOpinionCategory = newValue.commonOpinionCategory;
        this.publics = newValue.publics;
      }
    }
  },
  methods: {
    // 意见管理
    onOpinionManagerClick: function () {
      const _self = this;
      _self.initOpinionManager = true;
      _self.$nextTick(() => {
        if (_self.$refs[_self.opinionManagerName]) {
          _self.$refs[_self.opinionManagerName].open();
        } else {
          setTimeout(function () {
            _self.onOpinionManagerClick();
          }, 50);
        }
      });
    },
    onRecentOpinionDeleted() {
      this.$emit('recentOpinionDeleted');
    },
    onOpinionItemClick(e, item) {
      this.$emit('opinionItemClick', e, item);
    },
    onSignAndSubmitClick(e, item) {
      this.$emit('signAndSubmitClick', e, item);
    },
    onOpinionItemMouseEnter: function (e, item) {
      this.$set(item, 'showSignAndSubmitBtn', true);
    },
    onOpinionItemMouseLeave: function (e, item) {
      this.$set(item, 'showSignAndSubmitBtn', false);
    }
  }
};
</script>

<style lang="less" scoped>
.opinion-tabs-container {
  position: relative;

  ::v-deep > .ant-tabs {
    border: 1px solid var(--w-border-color-light);
    border-radius: var(--w-wf-opinion-editor-border-radius);

    > .ant-tabs-bar {
      padding: 4px 4px 0 12px;
      background-color: var(--w-gray-color-2);
      .ant-tabs-nav .ant-tabs-tab {
        padding: 0 0 5px;
        line-height: 32px;

        &.ant-tabs-tab-active {
          font-weight: bold;
        }
      }
      .ant-tabs-extra-content {
        line-height: 32px;
      }
      .ant-tabs-ink-bar {
        height: 3px;
      }
    }
  }

  ::v-deep .ant-tabs-bar {
    margin: 0;
  }

  ::v-deep .ant-list-item {
    padding: 4px 8px 4px 12px;
    border-bottom: 0;
    .item-content {
      font-size: var(--w-font-size-base);
      color: var(--w-text-color-darker);
      white-space: nowrap;
      text-overflow: ellipsis;
      width: 100%; /*calc(100% - 100px);*/
      overflow: hidden;
      line-height: 24px;
    }
    &:hover {
      background-color: var(--w-primary-color-1);
      .item-content {
        color: var(--w-primary-color);
      }
    }
  }
}
</style>
