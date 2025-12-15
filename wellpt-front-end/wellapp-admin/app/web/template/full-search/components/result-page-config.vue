<template>
  <div class="result-page-config">
    <a-button type="primary" @click="openDrawer">
      <a-icon type="setting" />
      自定义
    </a-button>
    <drawer v-model="visible" title="结果页面设置" :width="500" :container="getContainer" :mask="true" wrapClassName="result-page-drawer">
      <template slot="content">
        <a-form-model :colon="false" layout="vertical" class="pt-form">
          <a-form-model-item label="LOGO">
            <ImageLibrary
              v-model="config.logo"
              width="100%"
              :height="150"
              :limitSize="limitSize"
              :acceptType="acceptTypes"
              :acceptTip="acceptTip"
            />
            <div class="tip-div">支持格式: JPG、PNG、GIF、SVG; 大小限制: 不超过50M; 建议尺寸: 800px*800px</div>
          </a-form-model-item>
          <a-form-model-item>
            <template slot="label">
              <label>分栏显示实体文件</label>
              <a-tooltip placement="top" :arrowPointAtCenter="true">
                <div slot="title">在结果页中，单独分栏显示搜索出来的实体文件，支持预览</div>
                <a-icon type="exclamation-circle" style="vertical-align: middle; line-height: normal" />
              </a-tooltip>
            </template>
            <a-switch v-model="config.enabledColumns" />
          </a-form-model-item>
          <a-form-model-item label="分页">
            <div>默认每页条数</div>
            <a-select v-model="config.pageSize" :options="pageSizeOptions" />
            <a-input v-model="config.customPageSize" v-if="config.pageSize === pageSizeOptions[4]['value']" style="margin-top: 10px" />
          </a-form-model-item>
        </a-form-model>
      </template>
      <template slot="footer">
        <a-button type="primary" @click="handleConfirm">确定</a-button>
        <a-button @click="closeDrawer">取消</a-button>
      </template>
    </drawer>
  </div>
</template>

<script>
import { pageSizeOptions, acceptTypes, acceptTip, limitSize } from '../SearchSetting';
import ImageLibrary from '@pageAssembly/app/web/widget/commons/image-library.vue';
import Drawer from '@pageAssembly/app/web/lib/drawer.vue';

export default {
  name: 'ResultPageConfig',
  props: {
    value: {
      type: Object,
      default: () => {}
    }
  },
  components: {
    Drawer,
    ImageLibrary
  },
  data() {
    return {
      visible: false,
      acceptTypes,
      acceptTip,
      limitSize,
      pageSizeOptions,
      config: null
    };
  },
  watch: {
    value: {
      immediate: true,
      handler(value) {
        this.config = JSON.parse(JSON.stringify(value));
      }
    }
  },
  methods: {
    handleConfirm() {
      this.$emit('input', this.config);
      this.closeDrawer();
    },
    getContainer() {
      return document.body;
    },
    openDrawer() {
      this.visible = true;
    },
    closeDrawer() {
      this.visible = false;
    }
  }
};
</script>
