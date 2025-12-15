<template>
  <a-layout :hasSider="true" class="theme-specify-version-container">
    <a-layout-sider theme="light">
      <a-menu mode="inline" style="width: 195px" v-model="selectedKey" @select="onSelectMenu">
        <a-menu-item :key="item.uuid" v-for="(item, index) in specifyList">
          {{ item.version | versionTitle }}
          <div
            v-show="item.enabled"
            style="float: right; border-radius: 4px; margin-top: 14px; width: 20px; height: 10px; background-color: rgba(75, 182, 51, 1)"
          ></div>
          <a-button
            size="small"
            type="link"
            icon="delete"
            v-if="!item.enabled && !currentSpecify.newVersion"
            @click="onClickDelete(item, index)"
          />
        </a-menu-item>
      </a-menu>
    </a-layout-sider>
    <a-layout-content>
      <template v-if="currentSpecify.newVersion">
        <a-form-model :label-col="labelCol" :wrapper-col="wrapperCol">
          <a-form-model-item label="版本号">
            <a-input-number v-model="currentSpecify.version" :disabled="true" />
          </a-form-model-item>
          <a-form-model-item label="版本说明">
            <a-textarea v-model="currentSpecify.remark"></a-textarea>
          </a-form-model-item>
          <a-form-model-item label="设为启用版本">
            <a-switch v-model="currentSpecify.enabled"></a-switch>
          </a-form-model-item>
          <a-form-model-item label="源版本">
            <a-select :options="versionOptions" v-model="currentSpecify.sourceUuid" />
          </a-form-model-item>
        </a-form-model>
      </template>
      <PerfectScrollbar class="layout-content-scroll" v-else>
        <a-card style="width: 100%" :bordered="false" :bodyStyle="{}">
          <template slot="title">
            {{ currentSpecify.version | versionTitle }}
            <div class="subTitle">
              <label>创建人</label>
              <a-button type="link" size="small">张三</a-button>
            </div>
            <div class="subTitle">
              <label>创建时间</label>
              <span style="padding: 0 7px">{{ currentSpecify.createTime }}</span>
            </div>
          </template>
          <template slot="extra">
            <a-switch :checked="currentSpecify.enabled" @change="onChangeChecked" />
          </template>
          <div>
            <div>版本说明</div>
            <div>{{ currentSpecify.remark }}</div>
          </div>
        </a-card>
      </PerfectScrollbar>
    </a-layout-content>
  </a-layout>
</template>
<style lang="less"></style>
<script type="text/babel">
import { deepClone } from '@framework/vue/utils/util';
import { findIndex, map, some, groupBy } from 'lodash';

export default {
  name: 'ThemeSpecifyVersion',
  props: {
    specify: Object
  },
  components: {},
  computed: {},
  data() {
    return {
      labelCol: { span: 7 },
      wrapperCol: { span: 17 },
      selectedKey: this.specify.uuid ? [this.specify.uuid] : [],
      specifyList: [],
      loading: true,
      newVersion: false,
      currentSpecify: this.specify ? deepClone(this.specify) : {},
      versionOptions: [],
      newVersionNo: 1
    };
  },
  filters: {
    versionTitle(v) {
      return '主题规范  ' + parseFloat(v).toFixed(1);
    }
  },
  beforeCreate() {},
  created() {},
  beforeMount() {
    this.getAllSpecify();
  },
  mounted() {},
  methods: {
    onChangeChecked(ck) {
      if (!this.currentSpecify.enabled && ck) {
        this.currentSpecify.enabled = true;
      }
    },
    getAllSpecify(uuid, callback) {
      $axios.get('/proxy/api/theme/specify/getAll', {}).then(({ data }) => {
        this.specifyList = data.data;
        this.loading = false;
        this.versionOptions = [];
        for (let s of this.specifyList) {
          this.versionOptions.push({
            label: '主题规范 ' + parseFloat(s.version).toFixed(1),
            value: s.uuid
          });
          if (uuid == s.uuid) {
            this.currentSpecify = deepClone(s);
            this.selectedKey = uuid ? [uuid] : [];
          }
        }
        if (typeof callback == 'function') {
          callback.call(this);
        }
        this.getNewVersionNo();
      });
    },
    onSelectMenu({ item, key, selectedKeys }) {
      this.selectedKey = selectedKeys;
      for (let s of this.specifyList) {
        if (s.uuid == key) {
          this.currentSpecify = deepClone(s);
          break;
        }
      }
    },
    refresh(uuid, event) {
      this.currentSpecify.newVersion = false;
      this.getAllSpecify(uuid, () => {
        if (!event) {
          this.$emit('onEditVersionDetails', { isChange: true });
        }
      });
    },
    switchNewVersionEdit(callback) {
      if (!this.currentSpecify.newVersion) {
        this.currentSpecify.version = this.newVersionNo;
        let enabledIndex = findIndex(this.specifyList, { enabled: true });
        // 新版本默认使用启动版本
        this.currentSpecify.sourceUuid = enabledIndex > -1 ? this.specifyList[enabledIndex].uuid : this.currentSpecify.uuid;
        this.currentSpecify.remark = undefined;
        this.currentSpecify.enabled = true;
        this.currentSpecify.newVersion = true;
        this.currentSpecify.uuid = '';
      }
      if (typeof callback == 'function') {
        callback(this.currentSpecify);
      }
    },
    // 获取新增版本的版本号
    getNewVersionNo() {
      let groupVersionNo = groupBy(this.specifyList, 'version');
      this.newVersionNo = 1;
      some(groupVersionNo, (item, index) => {
        if (index == this.newVersionNo) {
          this.newVersionNo++;
          return false;
        } else {
          return true;
        }
      });
    },
    onClickDelete(item, index) {
      let _this = this;
      this.$confirm({
        title: '提示',
        content: '确认要删除吗?',
        onOk() {
          $axios.get('/proxy/api/theme/specify/delete/' + item.uuid, {}).then(({ data }) => {
            _this.specifyList.splice(index, 1);
            if (item.uuid == _this.currentSpecify.uuid && _this.specifyList.length > 0) {
              // 如果当前版本是已删除版本，则自动跳转到启用版本或第一个版本，并配置
              var enabledIndex = findIndex(_this.specifyList, { enabled: true });
              if (enabledIndex > -1) {
                _this.onSelectMenu({
                  item: _this.specifyList[enabledIndex],
                  key: _this.specifyList[enabledIndex].uuid,
                  selectedKeys: [_this.specifyList[enabledIndex].uuid]
                });
              } else {
                _this.onSelectMenu({
                  item: _this.specifyList[0],
                  key: _this.specifyList[0].uuid,
                  selectedKeys: [_this.specifyList[0].uuid]
                });
              }
              _this.versionOptions = map(_this.specifyList, s => {
                return {
                  label: '主题规范 ' + s.version,
                  value: s.uuid
                };
              });
              _this.$emit('onEditVersionDetails', { isChange: true, visible: true });
            }
            _this.getNewVersionNo();
          });
        },
        onCancel() {}
      });
    }
  }
};
</script>
