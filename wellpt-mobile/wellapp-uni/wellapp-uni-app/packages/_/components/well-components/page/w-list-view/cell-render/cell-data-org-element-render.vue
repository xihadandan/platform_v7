<template>
  <view v-if="text" class="cell-render cell-data-org-element-render flex f_y_c" :title="data.title">
    <org-node-avatar :nodeData="data" :orgElementIcon="orgElementIcon" :titleField="titleField" />
    <text class="org-title">{{ data.title }}</text>
  </view>
</template>
<style lang="scss">
.cell-data-org-element-render {
  --cell-data-org-element-render-title-margin-left: 4px;
  --cell-data-org-element-render-title-color: var(--w-text-color-mobile);
  --cell-data-org-element-render-title-size: var(--w-font-size-base);
  --cell-data-org-element-render-title-weight: normal;
  .org-title {
    margin-left: var(--cell-data-org-element-render-title-margin-left);
    color: var(--cell-data-org-element-render-title-color);
    font-size: var(--cell-data-org-element-render-title-size);
    font-weight: var(--cell-data-org-element-render-title-weight);
  }
}
</style>
<script type="text/babel">
import { storage, appContext } from "wellapp-uni-framework";
import OrgNodeAvatar from "../../w-org-select/component/org-node-avatar.vue";
import cellRenderMixin from "./cellRenderMixin";
export default {
  name: "CellDataOrgElementRender",
  mixins: [cellRenderMixin],
  title: "用户头像及其名称渲染",
  props: {},
  components: {
    OrgNodeAvatar,
  },
  computed: {
    titleField() {
      return "title";
    },
  },
  data() {
    return {
      loading: false,
      orgElementIcon: { user: "user", group: "team" },
      data: {
        type: "user",
        title: "",
        data: {
          avatar: "",
        },
      },
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
      let key = "getAllOrgElementModels";
      let request = () => {
        return new Promise((resolve, reject) => {
          _this.$axios
            .get("/api/org/elementModel/getAllOrgElementModels?system=" + this._$SYSTEM_ID)
            .then(({ data }) => {
              if (data.code == 0 && data.data) {
                storage.setStorageSync(key, JSON.stringify(data.data));
                resolve(data.data);
              }
            });
        });
      };
      let callback = (results) => {
        for (let i = 0, len = results.length; i < len; i++) {
          if (results[i].enable) {
            _this.orgElementIcon[results[i].id] = results[i].icon;
          }
        }
      };
      let results = storage.getStorageSync(key);
      if (results) {
        callback(JSON.parse(results));
      } else {
        request();
      }
    },
    fetchOrgUserDetail() {
      let _this = this;
      let key = "getUserDetailsUnderSystem_USERID=" + this.text;
      let request = () => {
        return new Promise((resolve, reject) => {
          _this.$axios.get(`/api/user/org/getUserDetailsUnderSystem?userId=${this.text}`).then(({ data }) => {
            if (data.code == 0 && data.data) {
              storage.setStorageSync(key, JSON.stringify(data.data));
              resolve(data.data);
            }
          });
        });
      };
      let callback = (results) => {
        console.log("业务组织用户信息", results.data);
        _this.data.data.avatar = results.avatar;
        _this.data.title = results.userName;
        _this.data.userId = results.userId;
      };
      let results = storage.getStorageSync(key);
      if (results) {
        callback(JSON.parse(results));
      } else {
        request();
      }
    },
  },
};
</script>
