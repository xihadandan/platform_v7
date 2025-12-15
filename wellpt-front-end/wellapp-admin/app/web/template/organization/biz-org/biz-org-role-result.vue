<template>
  <div style="padding: 0px 7px">
    <div class="spin-center" v-if="loading">
      <a-spin />
    </div>
    <a-row v-else>
      <a-col :span="12" style="border: 1px solid #e8e8e8; padding: 7px">
        <PerfectScrollbar :style="{ height: vHeight }">
          <DraggableTreeList
            v-model="treeSource"
            :draggable="false"
            :dragButton="false"
            selectMode="single"
            noCheckbox
            expandIcon="plus"
            :title-width="230"
            titleField="name"
            ref="treeList"
            @mounted="onTreeMounted"
            :afterSelected="afterTreeListSelected"
          >
            <template slot="title" slot-scope="scope">
              <Icon :type="scope.item.type === 'ROLE' ? 'pticon iconfont icon-ptkj-jiaose' : 'pticon iconfont icon-ptkj-quanxian'" />
              <span>{{ scope.item.name }}</span>
            </template>
          </DraggableTreeList>
          <a-empty v-if="treeSource.length == 0" style="margin-top: 47px" />
        </PerfectScrollbar>
      </a-col>
      <a-col :span="12" style="border: 1px solid #e8e8e8; padding: 7px; border-left: unset">
        <PerfectScrollbar :style="{ height: vHeight }">
          <a-tooltip>
            <template slot="title">点击左侧节点后, 显示该角色或权限关联的功能信息</template>
            <a-alert banner type="info" message="关联功能信息" class="pt-alert" style="margin-bottom: 7px"></a-alert>
          </a-tooltip>
          <div class="spin-center" v-if="loadingResource">
            <a-spin />
          </div>
          <DraggableTreeList
            v-else
            v-model="functionTree"
            :draggable="false"
            :dragButton="false"
            selectMode="single"
            noCheckbox
            expandIcon="plus"
            :title-width="230"
            titleField="name"
          >
            <template slot="title" slot-scope="scope">
              <template v-if="scope.item.type == 'appPageDefinition'">
                <Icon type="pticon iconfont icon-ptkj-yemian" />
                <span>页面: {{ scope.item.name }}</span>
              </template>
              <template v-else-if="scope.item.type == 'appModule'">
                <Icon type="pticon iconfont icon-ptkj-mokuaiqukuai" />
                <span>模块: {{ scope.item.name }}</span>
              </template>
              <template v-else>
                <Icon type="pticon iconfont icon-ptkj-kapian-01" />
                <span>功能: {{ scope.item.name }}</span>
              </template>
            </template>
          </DraggableTreeList>
          <a-empty v-if="!loadingResource && functionTree.length == 0" />
        </PerfectScrollbar>
      </a-col>
    </a-row>
  </div>
</template>

<script type="text/babel">
import DraggableTreeList from '@pageAssembly/app/web/widget/commons/draggable-tree-list';

export default {
  name: 'BizOrgRoleResult',
  props: { panelHeight: String, bizOrgElementId: String },
  inject: ['pageContext'],
  data() {
    return { treeSource: [], loading: true, functionTree: [], loadingResource: false };
  },
  watch: {},
  beforeCreate() {},
  components: { DraggableTreeList },
  computed: {
    vHeight() {
      return this.panelHeight != undefined ? this.panelHeight : 'calc(100vh - 200px)';
    }
  },
  created() {},
  methods: {
    onTreeMounted() {
      this.$refs.treeList.expandAll();
    },
    fetchOrgElementRolePrivilegeTree(bizOrgElementId) {
      $axios
        .get(`/proxy/api/org/biz/getBizOrgElementRolePrivilegeTree`, {
          params: {
            bizOrgElementId
          }
        })
        .then(({ data }) => {
          this.treeSource = data.data;
          this.loading = false;
        })
        .catch(error => {});
    },

    fetchRoleOrPrivilegeResourceTree(item) {
      this.loadingResource = true;
      this.functionTree.splice(0, this.functionTree.length);
      $axios
        .get(`/proxy/api/security/${item.type == 'PRIVILEGE' ? 'privilege' : 'role'}/getResources`, {
          params: {
            uuid: item.id
          }
        })
        .then(({ data }) => {
          this.loadingResource = false;
          this.functionTree = data.data || [];
          this.loading = false;
        })
        .catch(error => {});
    },
    afterTreeListSelected(item) {
      this.fetchRoleOrPrivilegeResourceTree(item);
    }
  },
  beforeMount() {
    if (this.bizOrgElementId != undefined) {
      this.fetchOrgElementRolePrivilegeTree(this.bizOrgElementId);
    }
  },
  mounted() {}
};
</script>
