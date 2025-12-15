<template>
  <a-skeleton active :loading="loading" :paragraph="{ rows: 50 }">
    <div :class="['msg-category-content', contentClass]">
      <!-- <div class="msg-category-folder" @click="contentShowEvent"><i class="iconfont icon-ptkj-zuoshouzhan"></i></div> -->
      <div>
        <a-input-search @search="onSearch" v-model="searchWord" allow-clear />
      </div>
      <PerfectScrollbar style="height: calc(100% - 50px); margin-right: -20px; padding-right: 12px">
        <div class="msg-category-list">
          <div class="msg-category-title">
            <i
              :class="[
                'iconfont  icon-folders',
                ulShow ? 'icon-ptkj-shixinjiantou-xia' : 'icon-ptkj-shixinjiantou-you',
                treeData.length ? 'hasList' : ''
              ]"
              @click="ulShowEvent"
            ></i>
            <i class="iconfont icon-ptkj-fenlei"></i>
            <span id="msgAllCategory" @click="onClick">模板分类</span>
            <i id="addMsgCategory" class="iconfont icon-ptkj-jiahao add-msg-category" @click.stop="addCategory"></i>
          </div>
          <a-tree
            :tree-data="treeData"
            @select="onClick"
            show-icon
            :replaceFields="{ children: 'children', title: 'name', key: 'id' }"
            :class="['msg_category_tree', ulClass]"
            :blockNode="true"
          >
            <a-icon type="caret-down" slot="switcherIcon" style="font-size: 18px" />
            <template slot="title" slot-scope="data">
              <div class="msg-category-item">
                <span :style="{ background: data.data.iconColor || defaultIconBg }">
                  <Icon :type="data.data.icon || defalutIcon" />
                </span>
                <span class="msg-category-text">
                  {{ data.name }}
                </span>
                <a-dropdown>
                  <a-menu slot="overlay" @click="e => onClickTreeNodeMenu(e, data)">
                    <a-menu-item key="edit">
                      <i class="iconfont icon-ptkj-bianji"></i>
                      编辑
                    </a-menu-item>
                    <a-menu-item key="del">
                      <i class="iconfont icon-ptkj-shanchu"></i>
                      删除
                    </a-menu-item>
                  </a-menu>
                  <i class="iconfont icon-ptkj-gengduocaozuo icon-operate"></i>
                </a-dropdown>
              </div>
            </template>
          </a-tree>
          <a-empty v-if="!loading && treeData.length == 0" />
        </div>
      </PerfectScrollbar>
    </div>
    <a-modal
      :title="categoryData.uuid ? '编辑分类' : '新增分类'"
      :visible="categoryModalVisible"
      :destroyOnClose="true"
      :maskClosable="false"
      @ok="saveCategory"
      @cancel="categoryModalVisible = false"
      :width="744"
      :bodyStyle="{ height: '450px', overflowY: 'auto' }"
    >
      <a-form-model
        class="msg-category-form"
        :model="categoryData"
        :label-col="labelCol"
        :wrapper-col="wrapperCol"
        :rules="categoryRules"
        ref="categoryForm"
      >
        <a-form-model-item label="分类名称" prop="name">
          <a-input v-model="categoryData.name" placeholder="分类名称" allow-clear />
        </a-form-model-item>
        <a-form-model-item label="图标">
          <WidgetIconLibModal v-model="categoryData.icon" :zIndex="1000" :onlyIconClass="true">
            <a-badge>
              <a-icon
                v-if="categoryData.icon"
                slot="count"
                type="close-circle"
                style="color: #f5222d"
                theme="filled"
                @click.stop="categoryData.icon = undefined"
                title="删除图标"
              />
              <a-button class="msg-category-icon">
                {{ categoryData.icon ? '' : '选择图标' }}
                <Icon :type="categoryData.icon" v-show="categoryData.icon" />
              </a-button>
            </a-badge>
          </WidgetIconLibModal>
          <ColorPicker v-model="categoryData.iconColor" @ok="onColorPickOk" style="margin-left: 10px" />
        </a-form-model-item>
        <a-form-model-item label="编号" prop="code">
          <a-input v-model="categoryData.code" placeholder="编号" allow-clear />
        </a-form-model-item>
        <a-form-model-item label="父级分类" prop="parentUuid">
          <a-tree-select
            v-model="categoryData.parentUuid"
            style="width: 100%"
            :replaceFields="{ children: 'children', title: 'name', key: 'id', value: 'id' }"
            :tree-data="treeData"
            allow-clear
            show-search
          />
        </a-form-model-item>
        <a-form-model-item label="描述">
          <a-textarea v-model="categoryData.remark" placeholder="描述" allow-clear />
        </a-form-model-item>
      </a-form-model>
    </a-modal>
  </a-skeleton>
</template>
<script type="text/babel">
import '../message-manage/msg-category.less';
import WidgetIconLibModal from '@pageAssembly/app/web/lib/widget-icon-lib-modal.vue';
import ColorPicker from '@pageAssembly/app/web/widget/commons/color-picker.vue';
import { each } from 'lodash';
export default {
  name: 'PrintTemplateManageSider',
  inject: ['pageContext'],
  components: {
    ColorPicker,
    WidgetIconLibModal
  },
  data() {
    return {
      loading: true,
      searchWord: '',
      treeData: [],
      treeKey: 'treeData',
      defalutIcon: 'pticon iconfont icon-ptkj-qiehuanshitu',
      defaultIconBg: '#64B3EA',
      ulShow: true,
      ulClass: '',
      contentShow: true,
      contentClass: '',
      categoryData: {},
      labelCol: { span: 4 },
      wrapperCol: { span: 19 },
      categoryRules: {
        name: { required: true, message: '分类名称必填', trigger: ['blur', 'change'] },
        code: { required: true, message: '分类编码必填', trigger: ['blur', 'change'] },
        parentUuid: { validator: this.parentUuidValidator, trigger: ['blur', 'change'] }
      },
      categoryModalVisible: false,
      iconSelectModalVisible: false,
      searchUuid: '',
      editCateUuidList: []
    };
  },
  computed: {},
  watch: {},
  created() {
    this.getTreeData();
  },
  mounted() {
    this.pageContext.handleEvent('refetchPrintTemplateManageTableByClassifyUuid', () => {
      this.pageContext.emitEvent(`refetchPrintTemplateManageTable`, { classifyUuid: this.searchUuid });
    });
  },
  methods: {
    onSearch() {
      this.getTreeData();
    },
    afterTreeListSelected() {},
    getTreeData() {
      this.loading = true;
      $axios.post('/api/printTemplate/category/getTreeAllBySystemUnitIdsLikeName', { name: this.searchWord }).then(({ data }) => {
        this.loading = false;
        if (data.code == 0 && data.data) {
          this.treeData = data.data;
        }
      });
    },
    ulShowEvent() {
      if (this.treeData.length) {
        if (this.ulShow) {
          this.ulClass = 'item-height';
          setTimeout(() => {
            this.ulClass = 'item-height-0';
          }, 1);
        } else {
          this.ulClass = 'item-height';
          setTimeout(() => {
            this.ulClass = '';
          }, 300);
        }
        this.ulShow = !this.ulShow;
      }
    },
    contentShowEvent() {
      if (this.contentShow) {
        this.contentClass = 'item-width-0';
      } else {
        this.contentClass = '';
      }
      this.contentShow = !this.contentShow;
    },
    onClickTreeNodeMenu(e, item) {
      if (e.key == 'del') {
        this.categoryData.id = item.id;
        this.deleteCategory(item.id);
      } else if (e.key == 'edit') {
        this.editCateUuidList = this.getSelectUuid(item);
        this.getDetail(item.id, () => {
          this.openEditModal();
        });
      }
    },
    parentUuidValidator(rule, value, callback) {
      if (value) {
        if (value && this.editCateUuidList.toString().indexOf(value) > -1) {
          callback(new Error('父级分类不能选择当前类型或其子类型！'));
        }
      }
      callback();
    },
    getSelectUuid(object) {
      var arr = [];
      function pushUUid(object) {
        arr.push(object.data.uuid);
        if (object.children.length > 0) {
          for (var i = 0; i < object.children.length; i++) {
            arr.push(object.children[i].data.uuid);
            if (object.children[i].children.length > 0) {
              pushUUid(object.children[i]);
            }
          }
        }
      }
      pushUUid(object);
      return arr;
    },
    saveCategory() {
      this.$refs.categoryForm.validate(valid => {
        if (valid) {
          $axios.post('/api/printTemplate/category/save', this.categoryData).then(({ data }) => {
            if (data.code === 0) {
              this.$message.success('保存成功');
              this.getTreeData();
              this.categoryModalVisible = false;
            }
          });
        } else {
          console.log('error submit!!');
          return false;
        }
      });
    },
    deleteCategory(uuid) {
      let $this = this;
      this.$confirm({
        title: '确定删除分类吗',
        okText: '确认',
        cancelText: '取消',
        onOk() {
          $axios.post('/api/printTemplate/category/deleteWhenNotUsed', { uuid: uuid }).then(({ data }) => {
            if (data.code === 0) {
              $this.$message.success('删除成功');
              $this.getTreeData();
              // 删除当前列表分类，重新获取全部模板
              if (this.searchUuid == uuid) {
                this.onClick();
              }
            }
          });
        },
        onCancel() {}
      });
    },
    addCategory() {
      this.categoryData = { icon: '' };
      this.editCateUuidList = [];
      this.openEditModal();
    },
    getDetail(uuid, callback) {
      $axios.get('/api/printTemplate/category/get', { params: { uuid: uuid } }).then(({ data }) => {
        if (data.code === 0) {
          this.categoryData = data.data;
          if (typeof callback == 'function') {
            callback();
          }
        }
      });
    },
    onClickIcon() {
      this.iconSelectModalVisible = true;
    },
    onColorPickOk(color) {
      this.categoryData.iconColor = color;
    },
    openEditModal() {
      this.categoryData.iconColor = this.categoryData.iconColor || this.defaultIconBg;
      this.categoryData.icon = this.categoryData.icon || this.defaultIcon;
      this.categoryModalVisible = true;
    },
    onClick(item) {
      this.searchUuid = item && item.length ? item[0] : '';
      this.pageContext.emitEvent(`refetchPrintTemplateManageTable`, { classifyUuid: this.searchUuid });
    }
  }
};
</script>
