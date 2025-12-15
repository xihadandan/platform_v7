<template>
  <div class="module-resource-sider-container">
    <div class="res-loading" v-if="loading"><a-spin /></div>
    <a-radio-group v-model="deviceType" button-style="solid" class="page-type-select">
      <a-radio-button value="PC">桌面端</a-radio-button>
      <a-radio-button value="MOBILE">移动端</a-radio-button>
    </a-radio-group>
    <div class="resource-sider">
      <a-row
        v-if="modulePage.id != undefined"
        :class="['res-item-row', 'level-one']"
        type="flex"
        @click.native.stop="selectResource(modulePage, modulePage.uuid)"
      >
        <a-col flex="auto">
          <Icon type="pticon iconfont icon-szgy-zhuye" />
          <div :title="modulePage.name">模块主页</div>
        </a-col>
        <a-col flex="35px">
          <!-- 模块主页不通过页面权限控制访问，通过模块授权访问控制 -->
          <!-- <a-switch size="small" :checked="checkedKeys.includes(modulePage.id)" @change="e => onChangeSwitch(e, modulePage.id)" /> -->
        </a-col>
      </a-row>

      <template v-for="(res, i) in resources">
        <template v-if="res.children != undefined">
          <!-- 分组 -->
          <a-row type="flex" class="level-one" :data-uuid="res.uuid" :key="'group_' + res.uuid">
            <a-col flex="100%">
              <a-row type="flex" class="res-item-row">
                <a-col flex="auto" :data-group="res.uuid" @click.native.stop="onClickGroupRow(res.uuid)">
                  <div>
                    <img
                      class="svg-iconfont"
                      :src="groupOpened.includes(res.uuid) ? '/static/svg/folder-open.svg' : '/static/svg/folder-close.svg'"
                    />
                    {{ res.name }}
                  </div>
                </a-col>
              </a-row>
            </a-col>
            <a-col flex="100%" v-show="groupOpened.includes(res.uuid)" class="sub-res-item-col">
              <!-- 分组下的子元素 -->
              <template v-for="(p, i) in res.children">
                <a-row
                  v-show="p._PAGE_TYPE == deviceType && (filterResType == undefined || filterResType.includes(p._RES_TYPE))"
                  type="flex"
                  :data-uuid="p.uuid"
                  :data-group="res.uuid"
                  :class="['res-item-row', 'level-two', selectKey == p.uuid ? 'selected' : '']"
                  :key="p.uuid"
                  @click.native.stop="selectResource(p, p.uuid)"
                >
                  <a-col flex="auto">
                    <Icon :type="iconType(p._RES_TYPE)" />
                    <div :title="p.name">
                      {{ p.name }}
                    </div>
                  </a-col>
                  <a-col flex="35px" :data-group="res.uuid">
                    <a-switch
                      :checked="checkedKeys.includes(p._RES_TYPE == 'page' ? p.id : p.uuid)"
                      @change="e => onChangeSwitch(e, p._RES_TYPE == 'page' ? p.id : p.uuid)"
                    />
                  </a-col>
                </a-row>
              </template>
            </a-col>
          </a-row>
        </template>
        <template v-else>
          <a-row
            :class="['res-item-row', 'level-one', selectKey == res.uuid ? 'selected' : '']"
            v-show="res._PAGE_TYPE == deviceType && (filterResType == undefined || filterResType.includes(res._RES_TYPE))"
            :data-uuid="res.uuid"
            type="flex"
            :key="'no_group_res_' + res.uuid"
            @click.native.stop="selectResource(res, res.uuid)"
          >
            <a-col flex="auto">
              <Icon :type="iconType(res._RES_TYPE)" />
              <div :title="res.name">{{ res.name }}</div>
            </a-col>
            <a-col flex="35px">
              <a-switch
                :checked="checkedKeys.includes(res._RES_TYPE == 'page' ? res.id : res.uuid)"
                @change="e => onChangeSwitch(e, res._RES_TYPE == 'page' ? res.id : res.uuid)"
              />
            </a-col>
          </a-row>
        </template>
      </template>
    </div>
  </div>
</template>
<style lang="less">
.module-resource-sider-container {
  .anticon {
    vertical-align: unset;
  }
  .res-loading {
    text-align: center;
    background: #e8e8e829;
    position: absolute;
    top: 34px;
    width: 100%;
    height: 100%;
    z-index: 1;
    > div {
      position: absolute;
      top: 40%;
    }
  }
  .page-type-select {
    display: flex;
    margin-bottom: 10px;
    > label {
      flex: 1;
      text-align: center;
    }
  }
}
</style>
<script type="text/babel">
export default {
  name: 'ResourceSider',
  inject: ['getModulePage'],
  props: {
    resources: Array,
    filterResType: Array,

    loading: { type: Boolean, default: false }
  },
  components: {},
  computed: {},
  data() {
    return { groupOpened: [], selectKey: undefined, deviceType: 'PC', defaultExpandAll: true, checkedKeys: [], modulePage: {} };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {
    if (this.resources != undefined && this.defaultExpandAll) {
      for (let r of this.resources) {
        if (r.children != undefined) {
          this.groupOpened.push(r.uuid);
        }
      }
    }
  },
  mounted() {
    let mPage = this.getModulePage();
    if (mPage) {
      this.modulePage.id = mPage.id;
      this.modulePage.name = mPage.name;
      this.modulePage.uuid = mPage.uuid;
      this.modulePage._RES_TYPE = 'page';
    }
  },
  methods: {
    setCheckedKeys(keys) {
      if (keys != undefined) {
        this.checkedKeys.push(...keys);
      }
    },
    onChangeSwitch(checked, key) {
      if (checked) {
        this.checkedKeys.push(key);
      } else {
        let idx = this.checkedKeys.indexOf(key);
        if (idx != -1) {
          this.checkedKeys.splice(idx, 1);
        }
      }
      this.$emit('checkChange', this.checkedKeys);
    },
    selectResource(res, key) {
      this.selectKey = key;
      this.$emit('select', res);
    },
    iconType(key) {
      if (key == 'form') {
        return 'pticon iconfont icon-ptkj-biaogeshitu';
      } else if (key == 'page') {
        return 'pticon iconfont icon-ptkj-yemian';
      } else if (key == 'link') {
        return 'pticon iconfont icon-ptkj-lianjiebangding';
      }
      return 'file';
    },
    onClickGroupRow(uuid) {
      let idx = this.groupOpened.indexOf(uuid);
      if (idx != -1) {
        this.groupOpened.splice(idx, 1);
      } else {
        this.groupOpened.push(uuid);
      }
    }
  }
};
</script>
