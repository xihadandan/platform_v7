<template>
  <a-dropdown placement="bottomRight" :trigger="['click']" overlayClassName="header-switch-wk-dropdown">
    <slot></slot>
    <!-- <div slot="overlay" v-if="workbenches.length == 0">
      <a-empty />
    </div> -->
    <div slot="overlay">
      <div class="workbenches-content">
        <div class="title" style="min-width: 150px">
          <a-icon :type="loadingAuthPages ? 'loading' : 'appstore'" />
          {{ $t('HeaderUserCenter.workbench', '工作台') }}
        </div>
        <a-menu @click="handleSelectPage" :selectedKeys="selectedKeys" v-if="!loadingAuthPages">
          <template v-for="(page, i) in workbenches">
            <a-menu-item :key="page.pageUuid" :page-id="page.pageId">
              <div style="display: flex; justify-content: space-between; min-width: 150px; align-items: center">
                <label>{{ page.pageName }}</label>
                <a-icon
                  type="check-circle"
                  theme="filled"
                  :style="{
                    opacity: ENVIRONMENT.pageUuid == page.pageUuid ? 1 : 0
                  }"
                />
                <!-- <a-tag v-if="ENVIRONMENT.pageUuid == page.pageUuid" color="var(--w-primary-color)" style="margin-left: 12px">当前使用</a-tag> -->
              </div>
            </a-menu-item>
          </template>
        </a-menu>
      </div>
    </div>
  </a-dropdown>
</template>
<style lang="less">
.header-switch-wk-dropdown {
  .workbenches-content {
    padding: 8px 12px 12px 12px;
    outline: 1px solid #e8e8e8;
    border-radius: 4px;
    background-color: #fff;
    > .title {
      font-weight: bold;
      line-height: 30px;
      border-bottom: 1px solid #e8e8e8;
    }
    > ul {
      border: unset;
      > li {
        padding: 0 8px;
        margin-bottom: 4px !important;
        height: 30px;
        line-height: 30px;
        .anticon-check-circle {
          margin-left: 12px;
          margin-right: 0px;
          color: var(--w-primary-color);
        }
      }
    }
  }
}
</style>
<script type="text/babel">
export default {
  name: 'HeaderSwitchWorkbenches',
  inject: ['ENVIRONMENT'],
  props: {},
  components: {},
  computed: {
    pageUuidMap() {
      let map = {};
      if (this.workbenches.length > 0) {
        for (let i = 0, len = this.workbenches.length; i < len; i++) {
          map[this.workbenches[i].pageUuid] = this.workbenches[i];
        }
      }
      return map;
    }
  },
  data() {
    return {
      loadingAuthPages: false,
      selectedKeys: [this.ENVIRONMENT.pageUuid],
      workbenches: this.ENVIRONMENT != undefined && this.ENVIRONMENT.allAuthPages != undefined ? this.ENVIRONMENT.allAuthPages : []
    };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {
    if (this.ENVIRONMENT == undefined || this.ENVIRONMENT.allAuthPages == undefined) {
      this.loadingAuthPages = true;
      this.fetchUserSystemAuthPages();
    }
  },
  mounted() {},
  methods: {
    fetchUserSystemAuthPages() {
      this.$axios
        .get(`/proxy/webapp/systemAuthenticatePage`, {
          params: {
            tenant: this._$USER.tenantId,
            system: this._$SYSTEM_ID
          }
        })
        .then(({ data }) => {
          if (data.data && data.data.allAuthPages) {
            this.workbenches.push(...data.data.allAuthPages);
          }
          this.loadingAuthPages = false;
        })
        .catch(error => {});
    },
    handleSelectPage(e) {
      if (this.$root.refresh != undefined) {
        if (e.key !== this.ENVIRONMENT.pageUuid) {
          this.ENVIRONMENT.pageUuid = e.key;
          this.ENVIRONMENT.pageId = this.pageUuidMap[e.key].pageId;
          if (!window.__INITIAL_STATE__.SYSTEM_ADMIN_MANAGER_WORKBENCHES) {
            // 非系统管理后台直接刷新，系统管理后台则需要地址跳转
            this.$root.refresh({
              pageUuid: e.key
            });

            this.selectedKeys = [e.key];
          } else {
            this.$loading('工作台切换中 , 请稍后...');
          }

          let data = new FormData();
          data.append('dataKey', 'WORKBENCH');
          data.append('dataValue', this.ENVIRONMENT.pageId);
          data.append('moduleId', this._$USER.tenantId + ':' + this._$SYSTEM_ID);
          data.append('remark', '用户当前系统选择的工作台');
          $axios.post('/proxy/api/user/preferences/save', data, {}).then(() => {
            if (window.__INITIAL_STATE__.SYSTEM_ADMIN_MANAGER_WORKBENCHES) {
              location.href = `/sys/${this._$SYSTEM_ID}/index`;
            }
          });
        }
      }
    }
  }
};
</script>
