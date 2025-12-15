<template>
  <HtmlWrapper title="流程委托">
    <a-layout class="delegation-settings-container" theme="light">
      <a-layout-header class="delegation-settings-header">
        <a-row>
          <a-col span="18" class="title">
            <h1>流程委托</h1>
          </a-col>
          <a-col span="6" class="delegation-settings-action-container">
            <a-space v-if="settings.status === 2">
              <a-button type="primary" @click="delegationActive">同意委托</a-button>
              <a-button type="primary" @click="delegationRefuse">拒绝委托</a-button>
            </a-space>
          </a-col>
        </a-row>
      </a-layout-header>
      <a-layout-content class="delegation-settings-content">
        <PerfectScrollbar style="width: 100%; height: calc(100vh - 65px)" @ps-scroll-y="handleScroll">
          <div ref="maskDiv" style="width: 100%; height: calc(100vh - 65px); display: block; position: absolute; z-index: 99"></div>
          <DelegationSettingsDetail ref="delegationSettingsDetail" :settings="settings" style="margin-top: 16px"></DelegationSettingsDetail>
        </PerfectScrollbar>
      </a-layout-content>
    </a-layout>
  </HtmlWrapper>
</template>

<script>
import DelegationSettingsDetail from '@workflow/app/web/template/workflow/workflow-delegation-settings-detail.vue';
export default {
  components: {
    DelegationSettingsDetail
  },
  mounted() {},
  methods: {
    delegationActive() {
      const _this = this;
      _this.$loading('委托生效中...');
      $axios
        .post(`/proxy/api/workflow/delegation/settiongs/delegationActive?uuid=${_this.settings.uuid}`)
        .then(({ data: result }) => {
          _this.$loading(false);
          if (result.code === 0) {
            _this.$message.success('委托已生效！');
            setTimeout(() => {
              window.close();
            }, 3000);
          } else {
            _this.$message.error(result.msg || '服务异常！');
          }
        })
        .catch(({ response }) => {
          _this.$loading(false);
          _this.$message.error((response && response.data && response.data.msg) || '服务异常！');
        });
    },
    delegationRefuse() {
      const _this = this;
      _this.$loading('拒绝委托中...');
      $axios
        .post(`/proxy/api/workflow/delegation/settiongs/delegationRefuse?uuid=${_this.settings.uuid}`)
        .then(({ data: result }) => {
          _this.$loading(false);
          if (result.code === 0) {
            _this.$message.success('委托已拒绝!');
            setTimeout(() => {
              window.close();
            }, 3000);
          } else {
            _this.$message.error(result.msg || '服务异常！');
          }
        })
        .catch(({ response }) => {
          _this.$loading(false);
          _this.$message.error((response && response.data && response.data.msg) || '服务异常！');
        });
    },
    handleScroll(e) {
      this.$refs.maskDiv.style.height = this.$refs.delegationSettingsDetail.$el.clientHeight + 'px';
    }
  }
};
</script>

<style lang="less" scoped>
.delegation-settings-container {
  background: var(--w-bg-color-body);

  .delegation-settings-header {
    padding: 0 15px;
    background: var(--w-primary-color);

    .title {
      color: var(--w-white);
      overflow: hidden;
      white-space: nowrap;
      text-overflow: ellipsis;
      line-height: 60px;
      font-size: var(--w-font-size-lg);
      display: inline-block;
      padding-left: var(--w-padding-md);
      font-weight: bold;

      h1 {
        color: #fff;
        font-size: var(--w-font-size-3xl);
        padding-right: var(--w-padding-md);
      }
    }
  }
  .delegation-settings-content {
    position: relative;
    // background: linear-gradient(to bottom, var(--w-primary-color), var(--w-widget-page-layout-bg-color) 40%);
    // padding: var(--w-padding-xs) var(--w-padding-md);
  }
  .delegation-settings-action-container {
    text-align: right;
  }
}
</style>
