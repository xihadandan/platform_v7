<template>
  <HtmlWrapper :title="htmlTitle">
    <a-layout class="widget-dyform-setting">
      <a-layout-header class="widget-dyform-setting-head flex" ref="wHeader">
        <h1 class="f_g_1 w-ellipsis">
          {{ title }}
        </h1>
        <div class="widget-dyform-setting-buttons f_s_0">
          <a-button ghost icon="save" @click.stop="saveFormData" :loading="saveLoading" v-if="displayState == 'edit'">保存</a-button>
          <Drawer v-if="showDataVersion" title="数据版本列表" ref="versionDrawer">
            <template slot="content">
              <a-timeline>
                <a-timeline-item v-for="(item, i) in dataVerList" :key="'datav' + i">
                  <div style="display: flex; align-items: baseline; justify-content: space-between; font-weight: bolder">
                    版本 {{ Number(item.version).toFixed(1) }}
                    <a-button size="small" type="link" @click="showDataVersionDetail(item)">查看</a-button>
                  </div>

                  <p style="color: #999">{{ item.create_time }}</p>
                </a-timeline-item>
              </a-timeline>
            </template>
            <template slot="footer">
              <a-button type="primary" @click="backToLatestForm">返回最新版本</a-button>
            </template>
            <a-button type="primary" ghost icon="history" @click="fetchDataVersionList">查看版本列表</a-button>
          </Drawer>
        </div>
      </a-layout-header>
      <a-layout-content :class="['widget-dyform-setting-content top']" ref="wContent">
        <Scroll :style="{ height: 'calc(100vh - 100px)' }">
          <div v-if="dyformComponentLoading" class="spin-center">
            <a-spin />
          </div>
          <component
            v-else
            is="WidgetDyform"
            :dataUuid="dataUuid"
            :formUuid="formUuid"
            :formDatas="formDatas"
            :key="key"
            :displayState="displayState"
            @formDataChanged="onFormDataChanged"
            ref="wDyform"
            :dyformStyle="{ padding: 'var(--w-padding-md)' }"
          />
        </Scroll>
      </a-layout-content>
    </a-layout>
  </HtmlWrapper>
</template>
<style lang="less"></style>
<script type="text/babel">
import { debounce } from 'lodash';
import Drawer from '@pageAssembly/app/web/lib/drawer.vue';
import '@dyform/app/web/framework/vue/install';
import '@installPageWidget';
import '@modules/.webpack.runtime.devjs.js'; // 运行期二开文件

export default {
  name: 'DyformDataViewer',
  props: {},
  components: { Drawer },
  computed: {},
  data() {
    return {
      dyformComponentLoading: true,
      title: undefined,
      saveLoading: false,
      htmlTitle: '',
      dataVerList: [],
      versionDataLoading: false,
      formDatas: undefined,
      key: new Date().getTime()
    };
  },
  beforeCreate() {
    if (EASY_ENV_IS_BROWSER) {
      import('@dyform/app/web/framework/vue/install').then(m => {
        this.dyformComponentLoading = false;
      });
    }
  },
  created() {
    this.onFormDataChanged = debounce(this.onFormDataChanged.bind(this), 500);
    this.originalDisplayState = this.displayState;
  },
  beforeMount() {},
  mounted() {},
  methods: {
    backToLatestForm() {
      this.formDatas = undefined;
      this.isVersionDataView = false;
      this.key = new Date().getTime();
      this.displayState = this.originalDisplayState;
      this.$refs.versionDrawer.hide();
    },
    showDataVersionDetail(item) {
      $axios
        .post('/json/data/services', {
          serviceName: 'dyFormFacade',
          methodName: 'getVersionFormData',
          args: JSON.stringify([this.formUuid, item.uuid])
        })
        .then(({ data }) => {
          console.log('数据版本', data.data);
          this.formDatas = data.data;
          this.key = new Date().getTime();
          this.$refs.versionDrawer.hide();
          this.isVersionDataView = true;
          this.displayState = 'label';
        })
        .catch(() => {});
    },
    fetchDataVersionList() {
      this.versionDataLoading = true;
      this.dataVerList.splice(0, this.dataVerList.length);
      $axios
        .post('/json/data/services', {
          serviceName: 'dyFormFacade',
          methodName: 'getAllVersionFormData',
          args: JSON.stringify([this.formUuid, this.dataUuid])
        })
        .then(({ data }) => {
          console.log('数据版本列表', data.data);
          this.versionDataLoading = false;
          let dataMap = data.data;
          if (dataMap && dataMap[this.formUuid]) {
            this.dataVerList.push(...dataMap[this.formUuid]);
          }
        })
        .catch(() => {
          this.versionDataLoading = false;
        });
    },
    saveFormData() {
      let _this = this;
      _this.$loading();
      this.$refs.wDyform.collectFormData(true, function (valid, msg, formData) {
        if (valid) {
          _this.$loading(false);
          _this.$confirm({
            title: '提示',
            content: '确认保存吗？',
            onOk() {
              _this.$loading();
              $axios
                .post('/proxy/api/dyform/data/saveFormData', formData.dyFormData)
                .then(({ data }) => {
                  _this.$loading(false);
                  if (data.code === 0) {
                    _this.$message.success('保存成功');
                    if (_this.dataUuid) {
                      window.location.reload();
                    } else {
                      window.location.href += '&dataUuid=' + data.data;
                    }
                  } else {
                    _this.saveLoading = false;
                    _this.$message.error('保存失败');
                    console.error(data);
                  }
                })
                .catch(error => {
                  _this.$loading(false);
                  _this.saveLoading = false;
                  _this.$message.error('保存失败');
                  console.error(error);
                });
            }
          });
        } else {
          _this.$loading(false);
        }
      });
    },
    onFormDataChanged(e) {
      let formData = JSON.parse(JSON.stringify(this.$refs.wDyform.dyform.formData));
      formData.formUuid = this.formUuid;
      this.getTitle(formData, this.$refs.wDyform.dyformTitleContent);
    },

    getTitle(formData, titleExpression) {
      $axios
        .post(`/proxy/api/dyform/data/getDyformTitle?titleExpression=${encodeURIComponent(titleExpression)}`, formData)
        .then(({ data }) => {
          this.title = data.data;
          this.htmlTitle = this.title;
        })
        .catch(() => {});
    }
  }
};
</script>
