<template>
  <a-dropdown :trigger="['click']" v-model="visible" v-if="menu.menus.length > 0">
    <span :title="title + (userName ? ': ' + userName : '')">
      <a-avatar
        icon="user"
        class="header-user-avatar"
        :style="{
          border: USER.userId ? '2px solid' : 'none'
        }"
        :src="avatar"
      />
      <template v-if="!menu.titleHidden">{{ userName }}</template>
      <!-- <a-icon type="caret-down" v-if="!menu.titleHidden" /> -->
    </span>
    <a-menu slot="overlay" @click="handleMenuClick">
      <a-menu-item v-for="(subMenu, s) in menu.menus" :key="subMenu.code" :title="subMenu.title">
        <component :is="subMenu.code">
          <span>
            <Icon :type="subMenu.icon" />
            {{ subMenu.title }}
          </span>
        </component>
      </a-menu-item>
    </a-menu>
  </a-dropdown>
  <span :title="title + (userName ? ': ' + userName : '')" v-else>
    <a-avatar
      icon="user"
      class="header-user-avatar"
      :style="{
        border: USER.userId ? '2px solid' : 'none'
      }"
      :src="avatar"
    />
    <template v-if="!menu.titleHidden">{{ userName }}</template>
  </span>
</template>
<style lang="less">
.header-user-avatar {
  background-color: transparent;
  > i {
    vertical-align: unset;
  }
}
</style>
<script type="text/babel">
import HeaderModifyPassword from './header-modify-password.vue';
import HeaderPersonality from './header-personality.vue';
import HeaderUserCenter from './header-user-center.vue';
export default {
  name: 'HeaderUserAvatar',
  props: { menu: Object },
  inject: ['USER', 'pageContext'],
  components: { HeaderModifyPassword, HeaderPersonality, HeaderUserCenter },
  computed: {},
  data() {
    return {
      title: this.menu.title,
      userName: this.USER.userName,
      visible: false,
      avatar: this.USER ? '/proxy/org/user/view/photo/' + this.USER.userId : undefined
    };
  },
  created() {
    if (this.$i18n.locale !== 'zh_CN') {
      this.userName = this.USER.localUserName;

      if (this.menu && this.menu.i18n) {
        const getId = (i18nObj, obj) => {
          let id;
          Object.keys(i18nObj).map(k => {
            if (k.indexOf(obj.id) !== -1) {
              id = k;
            }
          });
          return id;
        };

        this.title = this.$t('Widget.' + getId(this.menu.i18n['zh_CN'], this.menu), this.menu.title);
        if (this.menu.menus && this.menu.menus.length) {
          this.menu.menus.map(item => {
            if (item.i18n) {
              item.title = this.$t('Widget.' + getId(item.i18n['zh_CN'], item), item.title);
            }
          });
        }
      }
    }
  },
  mounted() {
    this.pageContext.handleEvent(`User:AvatarRefresh`, () => {
      this.avatar = '/proxy/org/user/view/photo/' + this.USER.userId + '?time=' + new Date().getTime();
    });
  },
  methods: {
    handleMenuClick(e) {
      this.visible = false;
    }
  }
};
</script>
