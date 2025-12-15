<template>
  <div class="message-list-sider">
    <div style="margin-bottom: 12px">
      <a-input-search @search="onSearch" v-model="searchWord" allow-clear />
    </div>
    <PerfectScrollbar :style="{ height: vHeight, 'padding-right': '12px', marginRight: '-12px', marginLeft: '-20px' }">
      <ul class="msg-category-list">
        <li
          class="msg-category-item"
          v-for="item in catData"
          :key="item.uuid"
          @click="onClick(item)"
          :class="{ active: item.uuid == activeUuid }"
        >
          <span :style="{ background: item.iconBg || defaultIconBg }">
            <Icon :type="item.icon || defalutIcon" />
          </span>
          <span class="msg-category-text" :title="item.name">
            {{ item.name }}
          </span>
          <span class="msg-badge" v-if="item.unReadCount">{{ item.unReadCount }}</span>
        </li>
      </ul>
    </PerfectScrollbar>
  </div>
</template>
<script type="text/babel">
import './msg-category.less';
export default {
  name: 'MessageListSider',
  inject: ['pageContext'],
  data() {
    return {
      catData: [],
      defalutIcon: 'pticon iconfont icon-xmch-wodexiaoxi',
      defaultIconBg: '#64B3EA',
      searchWord: '',
      activeKey: 'unread',
      activeUuid: 'all',
      vHeight: ''
    };
  },
  created() {
    this.onSearch();
  },
  mounted() {
    let height = window.localStorage.getItem('msgListModalHeight');
    if (height) {
      this.vHeight = `calc(${height} - 76px)`;
    }
    this.pageContext.handleEvent('refetchMessageBoxClassifyData', () => {
      this.queryList();
    });
  },
  methods: {
    onSearch() {
      this.queryList();
    },
    queryList() {
      this.loading = true;
      $axios.get('/proxy/api/message/classify/facadeQueryList', { params: { name: this.searchWord } }).then(({ data }) => {
        if (data.code == 0 && data.data) {
          this.catData = data.data;
          if (!this.searchWord) {
            window.localStorage.setItem('msgClassifyList', JSON.stringify(data.data));
          }
        }
      });
    },
    onClick(item) {
      this.activeUuid = item.uuid;
      this.pageContext.emitEvent(`refetchMessageInBoxManangeTable`, { classifyUuid: this.activeUuid });
    }
  }
};
</script>
<style lang="less" scoped>
.message-list-sider {
  margin-right: -12px !important;
  padding: 16px 20px;
  border-right: 1px solid var(--w-border-color-light);
}
.msg-category-list {
  padding-inline-start: 20px;
  margin-top: 0px;

  .msg-category-item {
    width: 100%;

    span.msg-category-text {
      width: e('calc(100% - 90px)');
    }
  }
}
</style>
