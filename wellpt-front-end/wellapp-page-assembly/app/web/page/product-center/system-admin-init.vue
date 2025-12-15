<template>
  <HtmlWrapper title="系统管理后台">
    <Error v-if="errorCode != undefined" :error-code="errorCode"></Error>
    <a-result v-else>
      <template slot="title">
        {{ tip }}
        <template v-if="prodStatus == undefined">
          <div v-if="process">
            <a-progress status="active" :percent="percent" style="width: 500px" />
          </div>
          <a-icon v-else type="loading" style="font-size: 24px" spin />
        </template>
        <template v-else-if="prodStatus == 'BUILDING'">
          <a-progress :percent="percent" status="active" />
        </template>
      </template>
    </a-result>
  </HtmlWrapper>
</template>
<style lang="less"></style>
<script type="text/babel">
import BasicConfig from '../login/component/login-basic/config.vue';
import ProductPageSetting from './component/manager/product-page-setting.vue';
export default {
  name: 'SystemAdminInit',
  props: {},
  components: {},
  computed: {},
  data() {
    return {
      errorCode: undefined,
      percent: 0,
      notFound: false,
      prodStatus: undefined,
      process: false,
      tip: '系统管理初始化检测中...'
    };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {},
  mounted() {
    this.init(this._$SYSTEM_ID, this._$USER.tenantId).then(() => {
      // window.location.reload();
    });
  },
  methods: {
    init(systemId, tenant) {
      return new Promise((resolve, reject) => {
        $axios
          .get('/proxy/api/app/prod/getDetail', {
            params: {
              id: systemId
            }
          })
          .then(({ data }) => {
            if (data.data) {
              let product = data.data;
              if (product.status == 'BUILDING') {
                this.prodStatus = product.status;
                this.tip = '产品构建中, 无法访问系统管理';
                reject();
                return;
              }
              this.tip = '系统管理初始化中...';
              this.process = true;
              let promises = [
                // 生成系统信息
                $axios.post(`/proxy/api/system/saveSystemInfo`, {
                  prodVersionUuid: product.latestVersion.uuid,
                  title: product.name,
                  tenant: tenant,
                  adminTitle: '',
                  system: product.id
                }),
                // 生成系统页面设置
                $axios.post(`/proxy/api/system/saveAppSystemPageSetting`, {
                  layoutConf: JSON.stringify(ProductPageSetting.methods.defaultLayoutConf()),
                  tenant: tenant,
                  system: product.id
                }),
                // 生成默认系统登录页
                $axios.post(`/proxy/api/system/saveLoginPage`, {
                  title: product.name,
                  name: '默认登录页',
                  system: product.id,
                  tenant: tenant,
                  enabled: true,
                  isPc: true,
                  defJson: JSON.stringify({
                    type: 'LoginBasic',
                    config: BasicConfig.methods.generateDefaultConfig()
                  })
                }),
                $axios.get(`/proxy/api/org/elementModel/createOrgElementModelAndSetting`, {
                  params: {
                    system: product.id,
                    tenant: tenant
                  }
                }),
                $axios.get(`/proxy/api/system/createAppSystemParamsFromAppProdVersion`, {
                  params: {
                    system: product.id,
                    tenant: tenant
                  }
                })
              ];
              for (let i = 0, len = promises.length; i < len; i++) {
                promises[i].then(() => {
                  this.percent += 100 / len;
                });
              }
            } else {
              this.errorCode = 404;
              this.notFound = true;
            }
          })
          .catch(error => {
            this.errorCode = 500;
          });
      });
    },

    initSystemModuleRoles(systemId, tenant) {}
  },

  watch: {
    percent(val) {
      if (val >= 100) {
        setTimeout(() => {
          window.location.reload();
        }, 500);
      }
    }
  }
};
</script>
