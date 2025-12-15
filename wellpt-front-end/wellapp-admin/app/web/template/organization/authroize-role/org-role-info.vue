<template>
  <a-skeleton active :loading="loading" :paragraph="{ rows: 10 }">
    <a-row :gutter="16" class="org-role-info">
      <a-col :span="12">
        <a-card title="可选角色">
          <a-tree v-model="checkedKeys" checkable :tree-data="roleTreeData" @check="onCheck" :defaultExpandedKeys="['-1']" />
        </a-card>
      </a-col>
      <a-col :span="12">
        <a-card title="已选角色">
          <a-empty v-if="checkedOptions.length === 0" />
          <ul class="role-selected" v-else>
            <template v-for="(opt, i) in checkedOptions">
              <li :key="i" :title="opt.label">
                <div>
                  {{ opt.label }}
                  <a-button type="link" icon="close" class="close" size="small" @click.stop="delSelectedRole(i, opt.value)" />
                </div>
              </li>
            </template>
          </ul>
        </a-card>
      </a-col>
    </a-row>
  </a-skeleton>
</template>
<style lang="less" scoped>
.org-role-info {
  ul.role-selected {
    list-style-type: none;
    margin-block-start: 0px;
    margin-block-end: 0px;
    margin-inline-start: 0px;
    margin-inline-end: 0px;
    padding: 5px;

    li {
      height: 25px;
      padding: 2px 0px 2px 7px;
      cursor: pointer;
      position: relative;

      // padding-right: 20px;
      border-radius: 3px;
      .close {
        right: 0;
        position: absolute;
        top: 0;
        display: none;
      }
      > div {
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
        padding-right: 20px;
      }
    }
    li:hover {
      background-color: #e6f7ff;
      .close {
        display: inline;
      }
    }
  }
}
</style>
<script type="text/babel">
export default {
  name: 'OrgRoleInfo',
  props: {
    value: Array
  },
  inject: ['pageContext'],
  data() {
    let checkedKeys = this.value != undefined && this.value.length > 0 ? [].concat(this.value) : [];
    return { loading: true, roleTreeData: [], checkedKeys, roles: {}, checkedOptions: [] };
  },
  watch: {},
  beforeCreate() {},
  components: {},
  computed: {},
  created() {},
  methods: {
    delSelectedRole(i, v) {
      this.checkedOptions.splice(i, 1);
      let j = this.checkedKeys.indexOf(v);
      if (j != -1) {
        this.checkedKeys.splice(j, 1);
      }
      this.$emit('input', this.checkedKeys);
    },
    onCheck(checkedKeys, e) {
      console.log(arguments);
      let { checkedNodes } = e;
      let opt = [];
      for (let i = 0, len = checkedNodes.length; i < len; i++) {
        opt.push({ label: checkedNodes[i].data.props.title, value: checkedNodes[i].data.key });
      }
      this.checkedOptions = opt;
      this.$emit('input', checkedKeys);
    },
    getRoleTree() {
      let _this = this;
      $axios.get(`/proxy/api/security/role/getRoleTree`, {}).then(({ data }) => {
        if (data.code == 0) {
          // 处理后端返回的结果树
          let root = { title: '角色列表', checkable: false, key: '-1', children: [] };
          let cascadeAppendChild = (children, parent) => {
            for (let i = 0, len = children.length; i < len; i++) {
              let child = children[i];
              let p = {
                title: child.name,
                checkable: child.type == 'R',
                key: child.id,
                isLeaf: true
              };
              if (p.checkable) {
                _this.roles[p.key] = p;
              }
              parent.children.push(p);
              if (child.children && child.children.length > 0) {
                p.isLeaf = false;
                p.children = [];
                cascadeAppendChild(child.children, p);
              }
            }
          };
          cascadeAppendChild(data.data.children, root);
          _this.roleTreeData = [root];
          if (_this.checkedKeys.length) {
            for (let i = 0, len = _this.checkedKeys.length; i < len; i++) {
              _this.checkedOptions.push({
                label: _this.roles[_this.checkedKeys[i]] ? _this.roles[_this.checkedKeys[i]].title : _this.checkedKeys[i],
                value: _this.checkedKeys[i]
              });
            }
          }
          _this.loading = false;
        }
      });
    }
  },
  beforeMount() {
    this.getRoleTree();
  },
  mounted() {}
};
</script>
