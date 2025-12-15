<!-- 消息列表弹出框 -->
<template>
  <a-modal
    title=""
    :visible="visible"
    :width="width"
    centered
    :bodyStyle="{ padding: '0', height: this.height + 'px' }"
    :maskClosable="false"
    @cancel="handleCancel"
    class="myMsgWrapModal pt-modal"
    :footer="null"
  >
    <div class="flex list-container">
      <div class="f_s_0 title_tabs" style="width: 60px">
        <div :class="{ active: activeKey == 'inbox' }" @click="changeTab('inbox')">
          <Icon type="pticon iconfont icon-xmch-wodexiaoxi"></Icon>
          <div>消息通知</div>
        </div>
        <div :class="{ active: activeKey == 'outbox' }" @click="changeTab('outbox')">
          <Icon type="pticon iconfont icon-ptkj-tijiaofabufasong"></Icon>
          <div>已发送</div>
        </div>
      </div>
      <div class="f_g_1 list-content">
        <template v-for="item in tabs">
          <PerfectScrollbar :style="{ height: vHeight }" :key="item.code" v-show="item.code == activeKey">
            <div v-show="item.code == activeKey">
              <widget-vpage :pageId="item.pageId" :security="false"></widget-vpage>
              <!-- <template v-for="(wgt, index) in item.widgets">
                <component
                  v-if="wgt.forceRender !== false"
                  :is="resolveWidgetType(wgt)"
                  :widget="wgt"
                  :key="wgt.id"
                  :index="index"
                  :widgetsOfParent="item.widgets"
                  :parent="{ id: item.pageUuid }"
                ></component>
              </template> -->
            </div>
          </PerfectScrollbar>
        </template>
      </div>
    </div>
  </a-modal>
</template>
<script type="text/babel">
import WidgetVpage from '@pageAssembly/app/web/widget/widget-vpage.vue';
import './msg.less';
export default {
  name: 'MessageListModal',
  props: {
    value: { type: Boolean, default: false },
    height: {
      type: Number,
      default: 700
    },
    width: {
      type: Number,
      default: 1000
    }
  },
  components: {
    WidgetVpage
  },
  computed: {
    vHeight() {
      // 配置的栅格有个margin-bottom:var(--w-margin-xs)
      return `calc(var(--w-margin-xs) + ${this.height}px)`;
    }
  },
  data: function () {
    return {
      visible: this.value,
      activeKey: 'inbox',
      tabs: [
        {
          code: 'inbox',
          pageId: 'page_20240306114043'
        },
        {
          code: 'outbox',
          pageId: 'page_20240311092740'
        }
      ]
    };
  },
  META: {
    method: {
      showModal: '打开弹框'
    }
  },
  created() {
    window.localStorage.setItem('msgListModalHeight', `${this.height}px`);
  },
  computed: {
    title() {
      return '全部消息';
    }
  },
  mounted() {},
  methods: {
    resolveWidgetType(widget) {
      return widget.wtype;
    },
    // 打开消息弹框
    showModal(params) {
      this.visible = true;
      this.$emit('input', this.visible);
    },

    changeTab(tab) {
      this.activeKey = tab;
    },
    handleCancel() {
      this.visible = false;
      this.$emit('input', this.visible);
    },
    handleOk() {
      this.visible = false;
      this.$emit('input', this.visible);
    }
  },
  watch: {
    value: {
      handler(v) {
        this.visible = v;
      }
    }
  }
};
</script>
