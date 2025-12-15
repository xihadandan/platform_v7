<template>
  <div>
    <a-row :style="{ 'flex-flow': 'column' }">
      <a-col>
        <a-input-search />
      </a-col>
      <a-col>
        <a-tree
          :tree-data="categoryTreeData"
          show-icon
          :blockNode="true"
          @select="onSelectTreeNode"
          :selectedKeys.sync="selectedTreeKeys"
          class="ant-tree-directory"
          defaultExpandAll
          @mouseenter="onTreeMouseenter"
          @mouseleave="onTreeMouseleave"
        >
          <template slot="roleTreeRootTitle" slot-scope="{ title, selected }">
            {{ title }}
            <a-button icon="plus" size="small" :style="{ float: 'right', marginRight: '-8px' }" @click.stop="categoryModalVisible = true" />
          </template>

          <template slot="nodeTitle" slot-scope="scope">
            {{ scope.title }}
            <a-dropdown v-model="scope.dataRef.menuVisible">
              <a-menu slot="overlay" @click="e => onClickTreeNodeMenu(e, scope)">
                <a-menu-item key="edit">编辑</a-menu-item>
                <a-menu-item key="del">删除</a-menu-item>
              </a-menu>
              <a-button
                v-show="scope.hover || scope.menuVisible"
                size="small"
                icon="more"
                type="link"
                :style="{ float: 'right', color: scope.selected ? '#fff' : 'inhreit' }"
              />
            </a-dropdown>
          </template>
        </a-tree>
      </a-col>
      <a-col></a-col>
    </a-row>

    <a-modal
      :title="categoryModalTitle"
      :visible="categoryModalVisible"
      :destroyOnClose="true"
      :maskClosable="false"
      @ok="saveCategory"
      @cancel="categoryModalVisible = false"
      :width="744"
      :bodyStyle="{ height: '300px', overflowY: 'auto' }"
    >
      <a-form-model :model="category" :label-col="labelCol" :wrapper-col="wrapperCol" :rules="categoryRules" ref="categoryForm">
        <a-form-model-item label="分类名称" prop="name">
          <a-input v-model="category.name" />
        </a-form-model-item>
        <a-form-model-item label="图标">
          <!--
          <WidgetDesignModal title="选择图标" :zIndex="1002" :width="600">
            <a-badge>
              <a-icon
                v-if="iconClass"
                slot="count"
                type="close-circle"
                style="color: #f5222d"
                theme="filled"
                @click.stop="iconClass = undefined"
                title="删除图标"
              />
              <a-button size="small" shape="round">
                {{ iconClass ? '' : '设置图标' }}
                <Icon :type="iconClass || 'setting'" />
              </a-button>
            </a-badge>
            <template slot="content">
              <WidgetIconLib v-model="iconClass" />
            </template>
          </WidgetDesignModal> -->
        </a-form-model-item>
        <a-form-model-item label="编号">
          <a-input v-model="category.code" />
        </a-form-model-item>
        <a-form-model-item label="备注">
          <a-textarea v-model="category.remark" />
        </a-form-model-item>
      </a-form-model>
    </a-modal>
  </div>
</template>

<script type="text/babel">
export default {
  name: 'OrgPartnerSysCategory',
  props: {},
  inject: ['pageContext', 'vPageState', '$event', 'namespace'],
  data() {
    return {
      selectUuid: undefined,
      selectTreeNode: undefined,
      labelCol: { span: 4 },
      wrapperCol: { span: 19 },
      category: {},
      iconClass: undefined,
      iconColor: undefined,
      categoryModalTitle: '协作系统分类',
      categoryModalVisible: false,
      selectedTreeKeys: [],
      categoryRules: {
        name: { required: true, message: '分类名称必填', trigger: ['blur', 'change'] }
      },
      categoryOptions: [],
      categoryTreeData: [
        {
          title: '协作系统分类',
          scopedSlots: { title: 'roleTreeRootTitle' },
          key: '-1',
          children: []
        }
      ]
    };
  },
  watch: {},
  beforeCreate() {},
  components: {},
  computed: {},
  created() {
    this.getCategoryTreeData();
  },
  methods: {
    closeModal() {},
    onSelectTreeNode(keys) {
      // 更新表格
      this.pageContext.emitEvent(`refetchOrgPartnerSysApplyTable`, { categoryUuid: keys[0] });
    },
    delCategory(uuid) {
      let _this = this;
      $axios.get(`/proxy/api/org/organization/orgPartnerSysCategory/delete`, { params: { uuid } }).then(({ data }) => {
        if (data.code == 0) {
          _this.$message.success('删除成功');
          _this.getCategoryTreeData();
        }
      });
    },
    saveCategory() {
      let _this = this;
      let iconStyle = { iconClass: this.iconClass, iconColor: this.iconColor };
      this.category.iconStyle = JSON.stringify(iconStyle);
      $axios.post('/proxy/api/org/organization/orgPartnerSysCategory/save', this.category).then(({ data }) => {
        if (data.code == 0) {
          _this.$message.success('保存成功');
          _this.getCategoryTreeData();
          // if (_this.category.uuid) {
          //   // 更新节点
          //   _this.selectTreeNode.title = _this.category.name;
          //   for (let i = 0, len = _this.categoryOptions.length; i < len; i++) {
          //     if (_this.categoryOptions[i].value === _this.category.uuid && _this.categoryOptions[i].label != _this.category.name) {
          //       _this.categoryOptions[i].label = _this.category.name;
          //       _this.commitCategoryOptions();
          //       break;
          //     }
          //   }
          // }
          _this.categoryModalVisible = false;
        }
      });
    },
    getCategoryDetails(uuid, callback) {
      $axios.get(`/proxy/api/org/organization/orgPartnerSysCategory/details/${uuid}`, { params: {} }).then(({ data }) => {
        if (data.code == 0) {
          callback(data.data);
        }
      });
    },
    getCategoryTreeData() {
      let _this = this;

      $axios.get('/proxy/api/org/organization/orgPartnerSysCategory/list', { params: {} }).then(({ data }) => {
        if (data.code == 0) {
          _this.categoryTreeData[0].children = [];
          if (data.data.length > 0) {
            _this.categoryOptions = [];
            for (let i = 0, len = data.data.length; i < len; i++) {
              _this.categoryTreeData[0].children.push({
                title: data.data[i].name,
                key: data.data[i].uuid,
                scopedSlots: { title: 'nodeTitle' },
                menuVisible: false,
                hover: false
              });

              _this.categoryOptions.push({
                label: data.data[i].name,
                value: data.data[i].uuid
              });
            }

            _this.commitCategoryOptions();
          }
        }
      });
    },
    commitCategoryOptions() {
      this.pageContext.commitPageState(this.namespace, { categoryOptions: this.categoryOptions });
    },
    onClickTreeNodeMenu(e, scope) {
      scope.dataRef.menuVisible = false;
      let _this = this;
      if (e.key == 'del') {
        this.delCategory(scope.key);
      } else if (e.key == 'edit') {
        this.categoryModalVisible = true;
        this.category = {};
        this.selectTreeNode = scope.dataRef;
        this.getCategoryDetails(scope.key, d => {
          _this.category = d;
          if (_this.category.iconStyle) {
            let style = JSON.parse(d.iconStyle);
            _this.iconClass = style.iconClass;
            _this.iconColor = style.iconColor;
          }
        });
      }
    },
    onTreeMouseenter(e) {
      e.node.dataRef.hover = true;
    },
    onTreeMouseleave(e) {
      e.node.dataRef.hover = false;
    }
  },
  beforeMount() {},
  mounted() {}
};
</script>
