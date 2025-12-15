<template>
  <a-tabs v-model="activeTabKey" class="setting-pane-tabs">
    <a-tab-pane v-for="tab in tabs" :key="tab.key" :tab="tab.title">
      <div style="margin-bottom: 10px">
        <template v-if="!isDel[tab.key]">
          <Modal title="新增" :destroyOnClose="true" ref="newModal" v-model="newModalVisible">
            <a-button icon="plus" type="primary" style="margin-right: 8px">新增</a-button>
            <template slot="content">
              <LoginDefDetail :prodVersionUuid="prodVersionUuid" ref="newPage" :prodId="prodId" />
            </template>
            <template slot="footer">
              <a-button type="primary" @click="e => saveNewLoginDefAndDesign(tab.key == 'pc')">保存并配置</a-button>
              <a-button type="primary" @click="e => saveNewLoginDef(tab.key == 'pc')">保存</a-button>
              <a-button @click="newModalVisible = false">取消</a-button>
            </template>
          </Modal>
          <!-- <a-popconfirm
          placement="right"
          :arrowPointAtCenter="true"
          title="确认要删除吗?"
          ok-text="删除"
          cancel-text="取消"
          @confirm="confirmDelete(loginChecked[tab.key], tab.key)"
        >
          <a-button icon="delete" size="small" @click.stop="e => onClickBatchDelete(e, tab.key)">删除</a-button>
        </a-popconfirm> -->
          <a-button @click.stop="batchDel(tab.key)">
            <Icon type="pticon iconfont icon-ptkj-guolvxuanxiang"></Icon>
            批量删除
          </a-button>
        </template>
        <template v-else>
          <a-checkbox :indeterminate="indeiterminate[tab.key]" :checked="checkAll[tab.key]" @change="e => onCheckAll(e, tab.key)">
            全选
          </a-checkbox>
          <span class="checked-data">
            已选择
            <span style="color: var(--w-primary-color)">{{ loginChecked[tab.key].length }}</span>
            个页面
          </span>
          <a-button @click.stop="confirmDeleteBatch(loginChecked[tab.key], tab.key)" style="margin-right: 8px">
            <Icon type="pticon iconfont icon-ptkj-shanchu"></Icon>
            批量删除
          </a-button>
          <a-button @click="batchDel(tab.key)">
            <Icon type="pticon iconfont icon-ptkj-dacha"></Icon>
            取消
          </a-button>
        </template>
      </div>
      <PerfectScrollbar style="height: calc(100vh - 400px)">
        <a-list
          :grid="grid"
          :data-source="tab.key == 'pc' ? pcLoginDefList : mobileLoginDefList"
          rowKey="uuid"
          :loading="loading"
          class="setting-login-list"
        >
          <a-list-item
            slot="renderItem"
            slot-scope="item, index"
            v-show="(isDel[tab.key] && !item.isDefault) || !isDel[tab.key]"
            style="min-width: 320px"
          >
            <a-card hoverable :bodyStyle="{ padding: '0px' }">
              <a-checkbox
                v-if="!item.isDefault"
                class="item-checkbox"
                :checked="loginChecked[tab.key].includes(item.uuid)"
                @change="onSelectLoginItem(item, tab.key)"
                v-show="isDel[tab.key]"
              />
              <Icon type="pticon iconfont icon-ptkj-moren" v-if="item.isDefault" class="default-icon"></Icon>

              <div class="item-img" @click.stop="onSelectLoginItem(item, tab.key)">
                <img
                  slot="cover"
                  :src="item.thumbnail || defaultThumbnail"
                  style="width: 300px; height: 170px; object-fit: contain; border-radius: 4px"
                  @error="onImageError(item)"
                />
              </div>
              <div class="flex item-detail">
                <div class="f_s_0" style="padding: 24px 0px 24px 12px; width: 52px">
                  <a-avatar :size="32" style="background-color: var(--w-primary-color-2); color: var(--w-primary-color)">
                    <Icon slot="icon" :type="tab.key == 'pc' ? 'desktop' : 'mobile'"></Icon>
                  </a-avatar>
                </div>
                <div class="f_g_1">
                  <div class="flex">
                    <div
                      :title="item.name"
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
                      {{ item.name }}
                    </div>
                    <a-tag color="blue" class="tag-no-border w-ellipsis-1">
                      {{ item.backgroundCarouselText }}
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
                    {{ item.remark }}
                  </div>
                </div>

                <div class="f_s_0" :style="{ width: item.isDefault ? '34px' : '64px' }">
                  <a-popconfirm
                    v-if="!item.isDefault"
                    placement="top"
                    :arrowPointAtCenter="true"
                    title="确认要删除吗?"
                    ok-text="删除"
                    cancel-text="取消"
                    @confirm="confirmDelete([item.uuid], tab.key, index)"
                  >
                    <a-button size="small" type="link" title="删除">
                      <Icon :type="item.deleting ? 'loading' : 'pticon iconfont icon-ptkj-shanchu'"></Icon>
                    </a-button>
                  </a-popconfirm>
                  <a-dropdown>
                    <a-button type="icon" size="small" title="更多操作">
                      <Icon type="pticon iconfont icon-ptkj-gengduocaidan"></Icon>
                    </a-button>
                    <a-menu slot="overlay" @click="e => handleMenuClick(e, item, index)">
                      <a-menu-item key="edit">
                        <Modal title="编辑属性" :ok="e => onConfirmSave(e, item, tab.key == 'pc')">
                          属性
                          <template slot="content">
                            <LoginDefDetail :detail="item" :ref="'page_' + item.uuid" :prodVersionUuid="prodVersionUuid" />
                          </template>
                        </Modal>
                      </a-menu-item>
                      <a-menu-item key="design">配置</a-menu-item>
                      <a-menu-item key="setDefaultLoginDef" v-if="!item.isDefault">设为默认</a-menu-item>
                      <a-menu-item key="copy">复制</a-menu-item>
                    </a-menu>
                  </a-dropdown>
                </div>
              </div>
            </a-card>
          </a-list-item>
        </a-list>
      </PerfectScrollbar>
    </a-tab-pane>
  </a-tabs>
</template>
<style lang="less"></style>
<script type="text/babel">
import LoginDefDetail from './login-def-detail.vue';
import Modal from '@pageAssembly/app/web/lib/modal.vue';
import '../../../login/component/index.js';
import defaultThumbnail from '../../../login/component/login-basic/thumbnail.png';
import { filter, map } from 'lodash';

export default {
  name: 'ProdVersionLogin',
  props: {
    prodVersionUuid: String,
    prodId: String
  },
  components: { LoginDefDetail, Modal },
  computed: {},
  data() {
    return {
      activeTabKey: 'pc',
      tabs: [
        { key: 'pc', title: '桌面端' },
        { key: 'mobile', title: '移动端' }
      ],
      grid: { gutter: 16, xs: 1, sm: 1, md: 2, lg: 2, xl: 3, xxl: 4 },
      loading: true,
      pcLoginDefList: [],
      mobileLoginDefList: [],
      newModalVisible: false,
      loginTemplateThumbnail: {},
      defaultThumbnail,
      loginChecked: {
        pc: [],
        mobile: []
      },
      isDel: {
        pc: false,
        mobile: false
      },
      checkAll: {
        pc: false,
        mobile: false
      },
      indeiterminate: {
        pc: false,
        mobile: false
      }
    };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {
    this.fetchLoginDefs(this.prodVersionUuid).then(result => {
      this.getLoginTemplates();
      result.pc.forEach(item => {
        item.thumbnail = `/proxy-repository/repository/file/mongo/download?folderUuid=${item.uuid}&purpose=thumbnail`;
        this.pcLoginDefList.push(item);
      });
      this.mobileLoginDefList = result.mobile;
      this.loading = false;
    });
  },
  mounted() {},
  methods: {
    onClickBatchDelete(e, key) {
      if (this.loginChecked[key].length == 0) {
        this.$message.info('请选择要删除的登录页配置');
        // 无法阻止事件传播，只能抛异常
        throw new Error('无选中行数据');
      }
    },
    // 批量删除切换
    batchDel(type) {
      if (this.isDel[type]) {
        // 取消批量删除
        this.isDel[type] = false;
      } else {
        this.isDel[type] = true;
        this.changeCheckAllData(type);
      }
    },
    // 全选
    onCheckAll(e, type) {
      this.checkAll[type] = e.target.checked;
      this.indeiterminate[type] = false;
      let canDelList = filter(this[type + 'LoginDefList'], item => {
        return !item.isDefault;
      });
      this.loginChecked[type] = e.target.checked ? map(canDelList, 'uuid') : [];
    },
    // 修改全选框状态
    changeCheckAllData(type) {
      if (this.isDel[type]) {
        let canDelList = filter(this[type + 'LoginDefList'], item => {
          return !item.isDefault;
        });
        let checkedList = this.loginChecked[type];
        this.checkAll[type] = checkedList.length > 0 && canDelList.length == checkedList.length;
        this.indeiterminate[type] = checkedList.length > 0 && canDelList.length > checkedList.length;
      }
    },
    // 选中切换
    onSelectLoginItem(item, type) {
      if (!this.isDel[type]) {
        // 批量删除关闭时不触发
        return false;
      }
      let checked = this.loginChecked[type];
      let idx = checked.indexOf(item.uuid);
      if (item.isDefault) {
        if (idx > -1) {
          checked.splice(idx, 1);
        }
        return;
      }
      if (idx == -1) {
        checked.push(item.uuid);
      } else {
        checked.splice(idx, 1);
      }
      this.changeCheckAllData(type);
    },
    getLoginTemplates() {
      let components = window.Vue.options.components;
      for (let k in components) {
        let options = components[k].options;
        if (
          options &&
          options.name.startsWith('Login') &&
          options.__file.indexOf('page/login/component') != -1 &&
          options.__file.endsWith('index.vue')
        ) {
          this.loginTemplateThumbnail[options.name] = options.thumbnail || this.defaultThumbnail;
        }
      }
    },
    getCopyName(sources, originName) {
      let names = [],
        name = originName + ' - 副本';
      for (let p of sources) {
        names.push(p.name);
      }
      let num = 0;
      while (names.includes(name)) {
        name = originName + ` - 副本(${++num})`;
      }
      return name;
    },
    copyLoginDef(item) {
      return new Promise((resolve, reject) => {
        let submitData = {
          ...item,
          isDefault: false,
          uuid: undefined,
          name: this.getCopyName(this[item.isPc ? 'pcLoginDefList' : 'mobileLoginDefList'], item.name)
        };
        $axios
          .post(`/proxy/api/system/saveLoginPage`, submitData)
          .then(({ data }) => {
            if (data.code == 0) {
              submitData.uuid = data.data;
              this.$message.success('复制成功');
              resolve(submitData);
            }
          })
          .catch(error => {});
      });
    },
    handleMenuClick(e, item, index) {
      // if (e.key == 'delete') {
      //   this.deletePage(item.id).then(() => {
      //     this[item.isPc == '1' ? 'pcPageList' : 'mobilePageList'].splice(index, 1);
      //   });
      // }

      if (e.key == 'copy') {
        this.copyLoginDef(item).then(data => {
          this[item.isPc ? 'pcLoginDefList' : 'mobileLoginDefList'].splice(index, 0, {
            ...data
          });
        });
      } else if (e.key == 'design') {
        window.open(`/login-design/${item.uuid}`, '_blank');
      } else if (e.key == 'setDefaultLoginDef') {
        this.setDefaultLoginDef(item, data => {
          let list = item.isPc ? this.pcLoginDefList : this.mobileLoginDefList;
          let t = list.splice(index, 1)[0];
          list.splice(0, 0, t);

          for (let l of list) {
            this.$set(l, 'isDefault', l.uuid == item.uuid);
          }
          this.$message.success('默认登录页设置成功');

          // 如果设为默认且被选中，要移除
          item.isDefault = true;
          this.onSelectLoginItem(item, item.isPc ? 'pc' : 'mobile');
        });
      }
    },
    // 设为默认
    setDefaultLoginDef(item, callback) {
      $axios
        .get(`/proxy/api/system/setDefaultLoginDef`, { params: { uuid: item.uuid } })
        .then(({ data }) => {
          if (data.code == 0) {
            if (typeof callback == 'function') {
              callback(data);
            }
          }
        })
        .catch(error => {});
    },
    confirmDeleteBatch(uuids, key) {
      let _this = this;
      this.$confirm({
        title: '提示',
        content: '确定删除选中页面?',
        okText: '确定',
        cancelText: '取消',
        onOk() {
          _this.confirmDelete(uuids, key);
        },
        onCancel() {}
      });
    },
    confirmDelete(uuid, key, index) {
      if (uuid.length) {
        $axios
          .post(`/proxy/api/system/deleteLoginPages`, uuid)
          .then(({ data }) => {
            if (data.code == 0) {
              let list = this[key == 'pc' ? 'pcLoginDefList' : 'mobileLoginDefList'];
              if (index != undefined) {
                list.splice(index, 1);
              } else {
                for (let i = 0; i < list.length; i++) {
                  if (uuid.includes(list[i].uuid)) {
                    list.splice(i--, 1);
                  }
                }
                this.loginChecked[key] = []; // 清空选中值
              }
            }
          })
          .catch(error => {});
      } else {
        this.$message.info('请选择要删除的登录页配置');
      }
    },
    saveNewLoginDef() {
      let _this = this;
      return new Promise((resolve, reject) => {
        this.onConfirmSave(null, null, _this.activeTabKey == 'pc').then(data => {
          _this.newModalVisible = false;
          resolve(data);
        });
      });
    },
    saveNewLoginDefAndDesign() {
      let _this = this;
      this.saveNewLoginDef(this.activeTabKey == 'pc').then(data => {
        _this.newModalVisible = false;
        window.open(`/login-design/${data.uuid}`, '_blank');
      });
    },
    onConfirmSave(e, item, isPc) {
      let $el = item ? this.$refs['page_' + item.uuid][0] : this.$refs.newPage[isPc ? 0 : 1];
      return new Promise((resolve, reject) => {
        $el.save(isPc).then(data => {
          if (e) {
            e(true);
          }
          if (item == undefined) {
            let list = isPc ? this.pcLoginDefList : this.mobileLoginDefList;
            if (list.length == 0) {
              data.isDefault = true;
              this.setDefaultLoginDef(data); // 手动设为默认
            }
            if (data.defJson) {
              let defJson = JSON.parse(data.defJson);
              data.type = defJson.type;
              data.backgroundCarouselText =
                defJson.config && defJson.config.backgroundCarousel && defJson.config.backgroundCarousel.enable ? '轮播背景' : '单屏背景';
            }
            list.splice(1, 0, { ...data });
          } else {
            item.remark = data.remark;
            item.name = data.name;
          }
          resolve(data);
        });
      });
    },
    onImageError(item) {
      item.thumbnail = this.loginTemplateThumbnail[item.type] || this.defaultThumbnail;
    },
    fetchLoginDefs(prodVersionUuid) {
      return new Promise((resolve, reject) => {
        $axios
          .get(`/proxy/api/system/getAllProdVersionLoginPage`, { params: { prodVersionUuid } })
          .then(({ data }) => {
            if (data.code == 0 && data.data) {
              let result = {
                mobile: [],
                pc: []
              };
              let pcDefault = null,
                mobilDefault = null;
              for (let d of data.data) {
                let defJson = JSON.parse(d.defJson);
                d.type = defJson.type;
                d.backgroundCarouselText =
                  defJson.config && defJson.config.backgroundCarousel && defJson.config.backgroundCarousel.enable ? '轮播背景' : '单屏背景';
                if (d.isPc) {
                  if (d.isDefault) {
                    pcDefault = d;
                    continue;
                  }
                  result.pc.push(d);
                } else {
                  if (d.isDefault) {
                    mobilDefault = d;
                    continue;
                  }
                  result.mobile.push(d);
                }
              }
              if (pcDefault) {
                result.pc.splice(0, 0, pcDefault);
              }
              if (mobilDefault) {
                result.mobile.splice(0, 0, mobilDefault);
              }
              resolve(result);
            }
          })
          .catch(error => {});
      });
    }
  }
};
</script>
