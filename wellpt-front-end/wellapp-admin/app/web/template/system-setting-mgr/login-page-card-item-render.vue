<template>
  <div>
    <div style="position: absolute; left: 20px; top: 12px; padding: 2px 5px 2px 0px"><slot name="selection"></slot></div>

    <div class="login-item-img">
      <img
        slot="cover"
        :src="thumbnail"
        style="width: 247px; height: 139px; object-fit: contain; border-radius: 4px; object-fit: contain"
        ref="image"
        @error="onImageError(row)"
      />
    </div>
    <a-switch
      :checked="enabled"
      :loading="enabling"
      checked-children="启用"
      un-checked-children="启用"
      @change="onChangeEnabled"
      :style="{
        background: enabled ? 'var(--w-primary-color)' : '#d8d8d8',
        position: 'absolute',
        top: '15px',
        right: '17px'
      }"
    />
    <a-row type="flex" style="align-items: center; outline: 1px solid #e6e6e6; box-shadow: 0px -5px 4px 0px #f1f0f0">
      <a-col flex="52px" style="padding: 24px 0px 24px 12px">
        <a-avatar :size="32" style="background-color: var(--w-primary-color-2); color: var(--w-primary-color)">
          <Icon slot="icon" type="pticon iconfont icon-oa-xitongdanweiguanli"></Icon>
        </a-avatar>
      </a-col>
      <a-col flex="auto">
        <div style="display: flex">
          <div
            :title="row.NAME"
            style="
              max-width: 150px;
              text-overflow: ellipsis;
              overflow: hidden;
              white-space: nowrap;
              padding-right: 5px;
              color: var(--w-text-color-dark);
              font-size: var(--w-font-size-base);
              font-weight: bold;
            "
          >
            {{ row.NAME || row.TITLE }}
          </div>
          <a-tag color="blue">
            {{ defJson.config && defJson.config.backgroundCarousel && defJson.config.backgroundCarousel.enable ? '轮播背景' : '单屏背景' }}
          </a-tag>
        </div>

        <div
          style="
            max-width: 150px;
            text-overflow: ellipsis;
            overflow: hidden;
            white-space: nowrap;
            padding-right: 5px;
            color: var(--w-text-color-light);
            font-size: var(--w-font-size-sm);
          "
        >
          {{ row.REMARK }}
        </div>
      </a-col>
      <a-col flex="60px">
        <slot name="actions" />
      </a-col>
    </a-row>
  </div>
</template>
<style lang="less" scoped>
.login-item-img {
  text-align: center;
  height: 204px;
  line-height: 204px;
  width: 100%;
  background-color: var(--w-gray-color-2);
}
.ant-avatar {
  background-color: var(--w-primary-color-2);

  > i {
    font-size: 18px;
    color: var(--w-primary-color);
  }
}
</style>
<script type="text/babel">
import defaultThumbnail from '@pageAssembly/app/web/page/login/component/login-basic/thumbnail.png';

export default {
  name: 'LoginPageCardItemRender',
  inject: ['widgetTableContext'],
  props: {
    row: Object,
    invokeDevelopmentMethod: Function
  },
  components: {},
  computed: {
    enabled() {
      return this.row.ENABLED == 1;
    },
    thumbnail() {
      return `/proxy-repository/repository/file/mongo/download?folderUuid=${this.row.UUID}&purpose=thumbnail`;
    }
  },
  data() {
    return { loginTemplateThumbnail: {}, defJson: JSON.parse(this.row.DEF_JSON), enabling: false, defaultThumbnail };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {
    import('@pageAssembly/app/web/page/login/component/index.js').then(() => {
      this.getLoginTemplates();
    });
  },
  mounted() {},
  methods: {
    onImageError(item) {
      this.$refs.image.src = this.loginTemplateThumbnail[this.defJson.type] || this.defaultThumbnail;
    },
    onChangeEnabled() {
      this.enabling = true;
      this.invokeDevelopmentMethod('enableLoginPage', this.row).then(d => {
        this.enabling = false;
        this.row.ENABLED = this.row.ENABLED == 1 ? 0 : 1;
        this.$message.success(`${this.enabled ? '启用成功' : '禁用成功'}`);
        for (let i = 0, len = this.widgetTableContext.rows.length; i < len; i++) {
          if (this.widgetTableContext.rows[i].UUID != this.row.UUID) {
            this.widgetTableContext.rows[i].ENABLED = 0;
          }
        }
      });
    },

    getLoginTemplates() {
      let components = window.Vue.options.components;
      for (let k in components) {
        let options = components[k].options;
        if (options && options.name.startsWith('Login') && options.__file.indexOf('login-widget') && options.__file.endsWith('index.vue')) {
          this.$set(this.loginTemplateThumbnail, options.name, options.thumbnail || this.defaultThumbnail);
        }
      }
    }
  }
};
</script>
