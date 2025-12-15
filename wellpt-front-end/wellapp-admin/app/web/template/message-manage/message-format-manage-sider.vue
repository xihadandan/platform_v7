<template>
  <a-skeleton active :loading="loading" :paragraph="{ rows: 50 }">
    <div :class="['msg-category-content', contentClass]">
      <!-- <div class="msg-category-folder" @click="contentShowEvent"><i class="iconfont icon-ptkj-zuoshouzhan"></i></div> -->
      <div style="margin-bottom: 12px">
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
            <span id="msgAllCategory" @click="onClick">消息分类</span>
            <i id="addMsgCategory" class="iconfont icon-ptkj-jiahao add-msg-category" @click.stop="addCategory"></i>
          </div>
          <ul :class="['msg_category_tree', ulClass]" key="msg_category_tree">
            <li
              class="msg-category-item"
              v-for="item in treeData"
              :key="item.uuid"
              @click="onClick(item)"
              :class="{ active: item.uuid == searchUuid || item.uuid == dropdownVisibleUuid }"
            >
              <span :style="{ background: item.iconBg || defaultIconBg }">
                <Icon :type="item.icon || defalutIcon" />
              </span>
              <span class="msg-category-text" :title="item.name">
                {{ item.name }}
              </span>
              <a-dropdown @visibleChange="visible => dropdownVisibleChange(visible, item)">
                <a-menu slot="overlay" @click="e => onClickTreeNodeMenu(e, item)">
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
            </li>
          </ul>
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
          <ColorPicker v-model="categoryData.iconBg" @ok="onColorPickOk" style="margin-left: 10px" />
        </a-form-model-item>
        <a-form-model-item label="编号">
          <a-input v-model="categoryData.code" placeholder="编号" allow-clear />
        </a-form-model-item>
        <a-form-model-item label="启用分类">
          <a-switch v-model="categoryData.isEnable" />
        </a-form-model-item>
        <a-form-model-item label="描述">
          <a-textarea v-model="categoryData.note" placeholder="描述" allow-clear />
        </a-form-model-item>
      </a-form-model>
    </a-modal>
  </a-skeleton>
</template>
<script type="text/babel">
import './msg-category.less';
import WidgetIconLibModal from '@pageAssembly/app/web/lib/widget-icon-lib-modal.vue';
import ColorPicker from '@pageAssembly/app/web/widget/commons/color-picker.vue';
export default {
  name: 'MessageFormatManageSider',
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
      defalutIcon: 'pticon iconfont icon-xmch-wodexiaoxi',
      defaultIconBg: '#64B3EA',
      ulShow: true,
      ulClass: '',
      contentShow: true,
      contentClass: '',
      categoryData: { icon: '' },
      labelCol: { span: 4 },
      wrapperCol: { span: 19 },
      categoryRules: {
        name: { required: true, message: '分类名称必填', trigger: ['blur', 'change'] }
      },
      categoryModalVisible: false,
      iconSelectModalVisible: false,
      searchUuid: '',
      dropdownVisibleUuid: ''
    };
  },
  computed: {},
  watch: {},
  created() {
    this.getTreeData();
  },
  mounted() {},
  methods: {
    onSearch() {
      this.getTreeData();
    },
    afterTreeListSelected() {},
    getTreeData() {
      this.loading = true;
      $axios.get('/proxy/api/message/classify/queryList', { params: { name: this.searchWord } }).then(({ data }) => {
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
        this.categoryData.uuid = item.uuid;
        this.deleteCategory(item.uuid);
      } else if (e.key == 'edit') {
        this.getDetail(item.uuid, () => {
          this.openEditModal();
        });
      }
    },
    saveCategory() {
      this.categoryData.isEnable = this.categoryData.isEnable ? 1 : 0;
      $axios.post('/proxy/api/message/classify/saveOrupdateClassify', this.categoryData).then(({ data }) => {
        if (data.code === 0) {
          this.$message.success('保存成功');
          this.getTreeData();
          this.categoryModalVisible = false;
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
          $axios.post('/proxy/api/message/classify/delClassifys', [uuid]).then(({ data }) => {
            if (data.code === 0) {
              $this.$message.success('删除成功');
              $this.getTreeData();
              // 删除当前列表分类，重新获取全部数据
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
      this.categoryData = { isEnable: true, icon: '' };
      this.openEditModal();
    },
    getDetail(uuid, callback) {
      $axios.get('/proxy/api/message/classify/getOne', { params: { uuid: uuid } }).then(({ data }) => {
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
      this.categoryData.iconBg = color;
    },
    openEditModal() {
      this.categoryData.iconBg = this.categoryData.iconBg || this.defaultIconBg;
      this.categoryData.icon = this.categoryData.icon || this.defaultIcon;
      this.categoryModalVisible = true;
    },
    // 第一次点击选中，再次点击选中项则取消
    onClick(item) {
      if (this.searchUuid && item && this.searchUuid == item.uuid) {
        this.searchUuid = '';
      } else {
        this.searchUuid = item ? item.uuid : '';
      }
      this.pageContext.emitEvent(`refetchMessageFormatManangeTable`, { classifyUuid: this.searchUuid });
    },
    dropdownVisibleChange(visible, item) {
      if (visible) {
        this.dropdownVisibleUuid = item ? item.uuid : '';
      } else {
        this.dropdownVisibleUuid = '';
      }
    }
  }
};
</script>
