<template>
  <div v-if="text" class="cell-render cell-data-org-element-render" :title="data.title">
    <org-element-avatar :nodeData="data" :orgElementIcon="orgElementIcon" :titleField="titleField"></org-element-avatar>
    {{ data.title }}
  </div>
</template>
<style lang="less"></style>
<script type="text/babel">
import orgElementAvatar from '@admin/app/web/lib/org-element-avatar.vue';
import cellRenderMixin from './cellRenderMixin';
export default {
  name: 'CellDataOrgElementRender',
  mixins: [cellRenderMixin],
  title: '用户头像及其名称渲染',
  scope: ['pc', 'mobile'],
  props: {},
  components: {
    orgElementAvatar
  },
  computed: {
    titleField() {
      return 'title';
    }
  },
  data() {
    return {
      loading: false,
      orgElementIcon: { user: 'user', group: 'team' },
      data: {
        type: 'user',
        title: '',
        data: {
          avatar: ''
        }
      }
    };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {},
  mounted() {
    this.fetchOrgElementModel();
    if (this.text) {
      this.fetchOrgUserDetail();
    }
  },
  methods: {
    fetchOrgElementModel() {
      let _this = this;
      let request = () => {
        return new Promise((resolve, reject) => {
          $axios.get('/proxy/api/org/elementModel/getAllOrgElementModels', { params: { system: this._$SYSTEM_ID } }).then(({ data }) => {
            if (data.code == 0 && data.data) {
              resolve(data.data);
            }
          });
        });
      };
      let callback = results => {
        for (let i = 0, len = results.length; i < len; i++) {
          if (results[i].enable) {
            _this.orgElementIcon[results[i].id] = results[i].icon;
          }
        }
      };
      this.$tempStorage.getCache(
        'getAllOrgElementModels',
        () => {
          return request();
        },
        results => {
          callback(results);
        }
      );
    },
    fetchOrgUserDetail() {
      let _this = this;
      let request = () => {
        return new Promise((resolve, reject) => {
          $axios.get(`/proxy/api/user/org/getUserDetailsUnderSystem`, { params: { userId: this.text } }).then(({ data }) => {
            if (data.code == 0 && data.data) {
              resolve(data.data);
            }
          });
        });
      };
      let callback = results => {
        console.log('业务组织用户信息', results.data);
        _this.data.data.avatar = results.avatar;
        _this.data.title = results.userName;
        _this.data.userId = results.userId;
      };
      this.$tempStorage.getCache(
        'getUserDetailsUnderSystem_USERID=' + this.text,
        () => {
          return request();
        },
        results => {
          callback(results);
        }
      );
    }
  }
};
</script>
