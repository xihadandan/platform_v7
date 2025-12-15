<!-- 环节属性-权限设置-抽屉 -->
<template>
  <a-form-model-item class="form-item-vertical">
    <div class="set-and-clear" slot="label">
      <div>{{ label }}</div>
      <div>
        <a-button type="link" @click="setPermission">
          <Icon type="pticon iconfont icon-ptkj-shezhi" />
          <span>设置</span>
        </a-button>
        <a-button type="link" @click="clearPermission">
          <Icon type="pticon iconfont icon-ptkj-shanchu" />
          <span>清空</span>
        </a-button>
      </div>
    </div>
    <div class="work-flow-auth-list">
      <a-tag v-for="item in permissionVisible" :key="item.value">{{ item.title }}</a-tag>
    </div>
    <drawer
      v-if="containerCreated"
      v-model="visible"
      :title="title"
      :width="500"
      :closable="false"
      :container="getContainer"
      :wrapStyle="{ position: 'absolute' }"
      wrapClassName="permission-drawer-wrap"
    >
      <template slot="content">
        <permission-info :formData="formData" :dataSource="permission" :rightConfig="rightConfig" />
      </template>
      <template slot="footer">
        <a-button type="primary" @click="savePermission">保存</a-button>
        <a-button @click="resetDefault">恢复默认</a-button>
        <a-button @click="closeDrawer">关闭</a-button>
      </template>
    </drawer>
  </a-form-model-item>
</template>

<script>
import { flowTaskRights } from '../designer/constant';
import Drawer from '@pageAssembly/app/web/lib/drawer.vue';
import PermissionInfo from './permission-info.vue';
const drawerSelector = '.configuration-drawer-container';

export default {
  name: 'PermissionDrawer',
  inject: ['designer', 'pageContext'],
  props: {
    label: {
      type: String,
      default: '发起权限'
    },
    title: {
      type: String,
      default: '发起权限设置'
    },
    type: {
      type: String,
      default: 'startRights'
    },
    value: {
      type: Array,
      default: () => []
    },
    formData: {
      type: Object,
      default: () => {}
    }
  },
  components: {
    Drawer,
    PermissionInfo
  },
  data() {
    const permission = JSON.parse(JSON.stringify(this.value));

    const settingKey = flowTaskRights[this.type]['settingKey'];
    const rightConfigKey = flowTaskRights[this.type]['configKey'];
    const rightConfig = JSON.parse(JSON.stringify(this.formData[rightConfigKey]));
    return {
      containerCreated: false,
      visible: false,
      permission,
      settingKey,
      rightConfigKey,
      rightConfig
    };
  },
  watch: {
    value: {
      deep: true,
      handler(value) {
        this.permission = JSON.parse(JSON.stringify(value));
      }
    }
  },
  computed: {
    permissionVisible() {
      return this.value.filter(r => r.defaultVisible);
    }
  },
  mounted() {
    this.pageContext.handleEvent('closeAllDrawer', this.closeDrawer);
  },
  beforeDestroy() {
    this.pageContext.offEvent('closeAllDrawer');
  },
  methods: {
    closeDrawer() {
      this.visible = false;
    },
    openDrawer() {
      this.pageContext.emitEvent('closeAllDrawer');
      if (!this.containerCreated) {
        this.containerCreated = true;
      }
      this.visible = true;
    },
    // 设置权限
    setPermission() {
      this.openDrawer();
    },
    // 保存权限
    savePermission() {
      this.$emit('input', this.permission);
      this.formData[this.rightConfigKey] = JSON.parse(JSON.stringify(this.rightConfig));
      this.visible = false;
    },
    // 清空权限
    clearPermission() {
      this.permission.map(item => (item.defaultVisible = false));
      this.$emit('input', this.permission);
    },
    // 恢复默认
    resetDefault() {
      const defaultPermission = this.designer.getRightOptions(this.settingKey);
      this.$emit('input', defaultPermission);

      const defRightConfig = this.designer.initRightConfig(defaultPermission);
      this.formData[this.rightConfigKey] = JSON.parse(JSON.stringify(defRightConfig));
      this.rightConfig = defRightConfig;

      this.visible = false;
    },
    getContainer() {
      return document.querySelector(drawerSelector);
    }
  }
};
</script>
