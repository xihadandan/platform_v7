<template>
  <a-form-model-item class="form-item-vertical" :label="label">
    <div class="set-and-clear">
      <modal :title="title" :width="750" v-model="visible">
        <template slot="content">
          <permission-checkbox v-model="permission" :options="options" :columns="columns" />
        </template>
        <template slot="footer">
          <a-button type="primary" @click="savePermission">保存</a-button>
          <a-button @click="resetDefault">恢复默认</a-button>
          <a-button @click="closeModal">关闭</a-button>
        </template>
      </modal>
      <a-button type="link" @click="setPermission">
        <Icon type="pticon iconfont icon-ptkj-shezhi" />
        <span>设置</span>
      </a-button>
      <a-button type="link" @click="clearPermission">
        <Icon type="pticon iconfont icon-ptkj-shanchu" />
        <span>清空</span>
      </a-button>
    </div>
    <div class="work-flow-auth-list">
      <a-tag v-for="item in permissionAddName" :key="item.value">{{ item.name }}</a-tag>
    </div>
  </a-form-model-item>
</template>

<script>
import Modal from '@pageAssembly/app/web/lib/modal.vue';
import PermissionCheckbox from './permission-checkbox.vue';

export default {
  name: 'PermissionItem',
  inject: ['designer'],
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
    columns: {
      // 默认3个一列
      type: Number,
      default: 3
    }
  },
  components: {
    Modal,
    PermissionCheckbox
  },
  data() {
    let permission = this.value;
    let options = [];
    const { diction } = this.designer;
    if (diction) {
      options = diction[this.type];
    }
    return {
      visible: false,
      permission,
      options
    };
  },
  watch: {
    value: {
      deep: true,
      handler(value) {
        this.permission = value;
      }
    }
  },
  computed: {
    permissionAddName() {
      let permission = [];
      if (this.value) {
        this.value.forEach(item => {
          const option = this.options.find(r => r.value === item.value);
          permission.push({
            ...item,
            name: option.name
          });
        });
      }
      return permission;
    }
  },
  methods: {
    // 设置权限
    setPermission() {
      this.visible = true;
    },
    // 保存权限
    savePermission() {
      this.$emit('input', this.permission);
      this.visible = false;
    },
    // 清空权限
    clearPermission() {
      this.$emit('input', []);
    },
    // 恢复默认
    resetDefault() {
      const defaultPermission = this.designer.getDefaultRights(this.type);
      this.$emit('input', defaultPermission);
      this.visible = false;
    },
    // 关闭弹窗
    closeModal() {
      this.visible = false;
    }
  }
};
</script>
