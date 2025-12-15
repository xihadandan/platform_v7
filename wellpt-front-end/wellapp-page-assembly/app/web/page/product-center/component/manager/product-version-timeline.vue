<template>
  <a-row type="flex" class="product-version-container">
    <a-col flex="400px" class="product-version-timeline-container">
      <div class="sub-title">版本迭代</div>
      <PerfectScrollbar style="height: calc(100vh - 300px); padding-top: 10px">
        <div v-if="loading" class="spin-loading-center">
          <a-spin />
        </div>
        <a-timeline class="product-version-timeline">
          <a-timeline-item v-for="(ver, i) in versionList" :key="'prodVerTimeline_' + i" @click="editVersionLog(ver)">
            <Icon slot="dot" type="pticon iconfont icon-ptkj-jiedian" style="font-size: 7px; color: #d9d9d9"></Icon>
            <div class="flex" style="width: 100%; padding-top: 5px">
              <div class="f_g_1 flex">
                <div
                  :class="ver.uuid == currentVersionLog.versionUuid ? 'name-div selected' : 'name-div'"
                  :title="product.name + ' ' + ver.version"
                >
                  <label class="name">{{ product.name }}</label>
                  <label class="version">({{ ver.version }})</label>
                </div>
                <div class="publish-time" v-show="ver.status === 'PUBLISHED'">
                  {{ ver.publishTime }}
                </div>
              </div>
              <div class="f_s_0">
                <a-tag class="tag-no-border" :color="statusMap[ver.status].color">{{ statusMap[ver.status].label }}</a-tag>
              </div>
            </div>
          </a-timeline-item>
        </a-timeline>
      </PerfectScrollbar>
    </a-col>
    <a-col flex="auto">
      <a-card
        size="small"
        :bodyStyle="{ height: 'calc(100vh - 232px)', position: 'relative', padding: '24px 40px', '--w-card-head-min-height': '40px' }"
        :bordered="false"
      >
        <template slot="extra">
          <a-button
            size="small"
            type="link"
            @click="e => updateVersionStatus('PUBLISHED')"
            v-show="
              currentVersionLog.versionUuid != undefined &&
              (currentVersionLog.status == 'DEPRECATED' || currentVersionLog.status == 'BUILDING')
            "
          >
            <Icon type="pticon iconfont icon-ptkj-tijiaofabufasong"></Icon>
            发布
          </a-button>
          <a-button size="small" type="link" @click="jumpToProdSetting">
            <Icon type="pticon iconfont icon-ptkj-shezhi"></Icon>
            产品配置
          </a-button>
          <a-button size="small" type="link" @click="editMode = true" v-show="currentVersionLog.versionUuid != undefined">
            <Icon type="pticon iconfont icon-ptkj-bianji"></Icon>
            编辑
          </a-button>
          <a-popconfirm
            placement="bottomRight"
            title="确定作废吗?"
            ok-text="确定"
            cancel-text="取消"
            @confirm="e => updateVersionStatus('DEPRECATED')"
          >
            <a-button
              size="small"
              type="link"
              v-show="currentVersionLog.versionUuid != undefined && currentVersionLog.status != 'DEPRECATED'"
            >
              <Icon type="pticon iconfont icon-ptkj-yizuofeixiangmu"></Icon>
              作废
            </a-button>
          </a-popconfirm>
          <a-popconfirm placement="bottomRight" title="确定删除吗?" ok-text="确定" cancel-text="取消" @confirm="deleteVersion">
            <a-button size="small" type="link" v-show="currentVersionLog.versionUuid != undefined">
              <Icon type="pticon iconfont icon-ptkj-shanchu"></Icon>
              删除
            </a-button>
          </a-popconfirm>
        </template>
        <a-form-model :model="currentVersionLog" :colon="false" layout="vertical">
          <a-form-model-item prop="version">
            <div class="sub-title" slot="label">版本号</div>
            <a-input v-model="currentVersionLog.version" v-if="editMode" />
            <span v-else style="color: #333333; font-size: var(--w-font-size-base)">{{ currentVersionLog.version }}</span>
          </a-form-model-item>
          <a-form-model-item>
            <div class="sub-title" slot="label">版本日志</div>
            <a-spin v-show="loadingDetail" />
            <QuillEditor v-show="!loadingDetail" ref="quillEditor" v-model="currentVersionLog.detail" :displayAsLabel="!editMode" />
          </a-form-model-item>
          <div style="text-align: center; position: absolute; border: 0px; width: 100%; bottom: 29px" v-show="editMode">
            <a-button type="primary" @click="saveVersionLog">保存</a-button>
            <a-button @click="editMode = false">取消</a-button>
          </div>
        </a-form-model>
      </a-card>
    </a-col>
  </a-row>
</template>
<style lang="less">
.product-version-container {
  .sub-title {
    font-weight: bold;
    font-size: var(--w-font-size-lg);
    line-height: 32px;
    color: var(--w-text-color-dark);
    margin-bottom: 12px;
  }
  .product-version-timeline-container {
    padding: var(--w-padding-xs) var(--w-padding-md);
    background: #fff;
    margin-right: 12px;
  }
}
.product-version-timeline {
  .ant-timeline-item-last > .ant-timeline-item-content {
    min-height: unset !important;
  }
  .ant-timeline-item-content {
    display: flex;
    align-items: center;
    justify-content: space-between;
    flex-wrap: wrap;
    .name-div {
      color: var(--w-text-color-dark);
      font-size: var(--w-font-size-base);

      &.selected {
        color: var(--w-primary-color);
      }
      label {
        cursor: pointer;
        .name {
          white-space: nowrap;
          max-width: 120px;
          overflow: hidden;
          text-overflow: ellipsis;
          display: inline-block;
        }
        .version {
          white-space: nowrap;
          max-width: 60px;
          overflow: hidden;
          text-overflow: ellipsis;
          display: inline-block;
        }
      }
    }
    .publish-time {
      color: var(--w-text-color-light);
      font-size: var(--w-font-size-sm);
      padding-left: 12px;
      padding-top: 2px;
    }
  }
}
</style>
<script type="text/babel">
import ProductVersionSetting from './product-version-setting.vue';
import QuillEditor from '@pageAssembly/app/web/lib/quill-editor';

export default {
  name: 'ProductVersionTimeline',
  props: {
    product: Object
  },
  components: { QuillEditor },
  computed: {},
  data() {
    return {
      versionList: [],
      editMode: false,
      loading: true,
      loadingDetail: false,
      currentVersionLog: { uuid: undefined, detail: '', version: undefined, versionUuid: undefined, status: undefined },
      statusMap: {
        BUILDING: {
          label: '设计中',
          color: 'blue'
        },
        PUBLISHED: {
          label: '已发布',
          color: 'green'
        },
        DEPRECATED: {
          label: '已废弃',
          color: undefined
        }
      }
    };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {
    this.refresh(() => {
      this.editVersionLog(this.versionList[0]);
    });
  },
  mounted() {},
  methods: {
    updateVersionStatus(status) {
      if (this.currentVersionLog.versionUuid != undefined) {
        return new Promise((resolve, reject) => {
          $axios
            .get(`/proxy/api/app/prod/version/updateStatus`, { params: { uuid: this.currentVersionLog.versionUuid, status } })
            .then(({ data }) => {
              if (data.code == 0) {
                this.currentVersionLog.status = status;
                this.$message.success(`${status == 'PUBLISHED' ? '发布' : '作废'}版本成功`);
                this.refresh();
                resolve();
              }
            })
            .catch(error => {});
        });
      }
    },
    deleteVersion() {
      if (this.currentVersionLog.versionUuid != undefined) {
        $axios
          .get(`/proxy/api/app/prod/version/delete/${this.currentVersionLog.versionUuid}`, { params: {} })
          .then(({ data }) => {
            if (data.code == 0) {
              this.$message.success(`删除成功`);
              this.$emit(`deleteVersion`, this.currentVersionLog.versionUuid);
              this.currentVersionLog = { uuid: undefined, detail: '', version: undefined, versionUuid: undefined };
              this.refresh(() => {
                if (this.versionList.length > 0) {
                  this.editVersionLog(this.versionList[0]);
                }
              });
            }
          })
          .catch(error => {});
      }
    },
    jumpToProdSetting() {
      this.$emit('toProdVersionSetting', this.currentVersionLog.versionUuid);
    },
    refresh(callback) {
      ProductVersionSetting.methods.fetchAllProdVersions(this.product.id).then(list => {
        this.versionList = list;
        this.loading = false;
        if (typeof callback == 'function') {
          callback.call(this);
        }
      });
    },
    saveVersionLog() {
      if (this.currentVersionLog.version != undefined) {
        $axios
          .post(`/proxy/api/app/prod/version/log/save`, {
            uuid: this.currentVersionLog.versionUuid,
            version: this.currentVersionLog.version,
            log: {
              detail: this.currentVersionLog.detail,
              uuid: this.currentVersionLog.uuid
            }
          })
          .then(({ data }) => {
            if (data.code == 0) {
              this.$message.success('保存版本日志成功');
              this.editMode = false;
              this.refresh();
            }
          })
          .catch(error => {});
      }
    },
    editVersionLog(version) {
      this.currentVersionLog.version = version.version;
      this.currentVersionLog.versionUuid = version.uuid;
      this.currentVersionLog.status = version.status;
      this.loadingDetail = true;
      this.currentVersionLog.uuid = undefined;
      this.currentVersionLog.detail = '';
      this.getProdVersionLogDetail(version.uuid).then(() => {
        this.loadingDetail = false;
      });
    },
    getProdVersionLogDetail(versionUuid) {
      return new Promise((resolve, reject) => {
        $axios
          .get(`/proxy/api/app/prod/version/log/detail`, { params: { prodVersionUuid: versionUuid } })
          .then(({ data }) => {
            if (data.code == 0 && data.data) {
              this.currentVersionLog.uuid = data.data.uuid;
              this.currentVersionLog.detail = data.data.detail || '';
            }
            resolve();
          })
          .catch(error => {});
      });
    }
  }
};
</script>
