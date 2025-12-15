<template>
  <div>
    <a-tabs class="setting-pane-tabs">
      <a-tab-pane v-for="tab in tabs" :key="tab.key" :tab="tab.title">
        <div style="margin-bottom: 10px">
          <Modal title="新增" :ok="e => onConfirmSavePage(e, null, tab.key == 'pc')" :destroyOnClose="true">
            <a-button icon="plus" type="primary">新增</a-button>
            <template slot="content">
              <PageDetail
                :app-id="prodId"
                :isPc="tab.key == 'pc'"
                ref="newPage"
                :prodVersionId="versionId"
                :prodVersionUuid="versionUuid"
              />
            </template>
          </Modal>
        </div>
        <PerfectScrollbar style="height: calc(100vh - 400px)">
          <a-list
            :grid="grid"
            :data-source="tab.key == 'pc' ? pcPageList : mobilePageList"
            rowKey="uuid"
            :loading="loading"
            style="width: calc(100% - 20px)"
            class="setting-login-list"
          >
            <a-list-item slot="renderItem" slot-scope="item, index" style="min-width: 320px">
              <a-card hoverable :bodyStyle="{ padding: '0px' }">
                <svg
                  v-if="item.isDefault"
                  t="1693481370202"
                  class="icon"
                  viewBox="0 0 1024 1024"
                  version="1.1"
                  xmlns="http://www.w3.org/2000/svg"
                  p-id="4899"
                  width="52"
                  height="52"
                  style="position: absolute; right: -4px; top: -4px"
                >
                  <path
                    d="M553.6 205.312l-11.52-11.328-6.208 6.272 7.424 3.2a423.68 423.68 0 0 1-9.728 22.144l20.032-20.288z m18.304 26.624l3.52 7.36 6.208-6.336-11.52-11.328-19.904 20.16a559.36 559.36 0 0 0 21.696-9.856z m-25.6 13.888l-6.208 6.208 11.52 11.328 16.896-17.152c-6.272 2.944-12.48 5.76-18.688 8.256l-3.584-8.64z m-16-17.024l-9.728-4.096c3.584-6.4 6.848-12.8 9.728-18.88l-18.24 18.56 11.52 11.328 6.784-6.912z m50.752-143.488H85.312l853.376 853.376V442.88L581.056 85.312z m142.72 275.328l26.112 2.496c0.32 21.312 0.32 37.12-0.064 47.424l-29.184-2.432c1.152-11.776 2.176-27.584 3.2-47.488zM409.6 290.304a272 272 0 0 0 34.176-16.64l7.488 15.36a471.04 471.04 0 0 1-34.752 16.448L409.6 290.304z m26.112 29.12c6.4-8.96 12.416-18.688 17.92-29.12l13.44 8.064a346.24 346.24 0 0 1-16.96 29.696l-14.4-8.64z m23.36 16.96c5.12-10.624 9.728-21.12 13.632-31.424l14.08 6.4c-4.16 11.84-8.256 22.464-12.352 31.872l-15.36-6.848zM563.84 414.976c-8.64 1.664-18.56 3.904-29.888 6.72-1.92-23.488 7.232-50.304 27.328-80.384-25.472 18.944-53.056 28.48-82.688 28.544 1.472-9.728 2.368-19.456 2.56-29.248l6.208 0.256c2.048-7.36 3.584-14.72 4.672-22.272l13.632 3.904c-22.016-19.2-44.352-37.952-67.008-56.32l13.76-17.664 30.72 28.032 7.872-7.936-26.24-25.856 13.056-13.184 26.24 25.856 7.04-7.104L486.4 224l55.04-55.68 65.792 65.024-54.912 55.68-24.704-24.32-7.04 7.04 26.496 26.24-13.056 13.12-26.432-26.176-7.04 7.168 30.656 27.008c-5.12 3.968-10.112 8.256-14.784 12.8l-9.6-8.32c-0.64 5.44-1.536 10.88-2.752 16.192 21.888-2.88 45.76-15.552 71.744-38.08l-14.976-14.848 18.112-18.304 16 15.744c12.416-12.224 24.768-24.576 37.056-36.992l18.496 18.24c-16.256 16.448-28.672 28.8-37.248 36.864l28.096 27.648-18.176 18.368-25.728-25.408c-24.576 32.64-35.776 63.296-33.6 91.968z m99.2-130.432a346.24 346.24 0 0 1-8.576 34.496l-22.592-7.424c4.608-12.544 8.384-24.128 11.264-34.752l19.84 7.68zM617.28 472.96a26.432 26.432 0 0 0 17.92-8.064l46.208-46.848-14.336-14.08 17.984-18.24 32.96 32.512-49.536 50.176 31.232 1.216a156.032 156.032 0 0 0-11.2 22.72 608.256 608.256 0 0 0-73.472 3.84l2.24-23.232z m176.512-1.92c-37.504 45.568-51.2 87.04-41.216 124.416-13.312 2.688-24.96 5.248-34.88 7.616-2.944-34.432 4.288-64.832 21.504-91.136a169.536 169.536 0 0 1-92.992 20.288c3.136-10.816 5.44-21.952 6.912-33.344 29.888 2.88 54.016-0.64 72.448-10.496 18.368-9.92 45.12-32.896 80.128-68.864l20.928 20.608c-11.712 11.52-22.592 21.824-32.832 30.912z"
                    fill="var(--w-primary-color)"
                    p-id="4900"
                  ></path>
                </svg>
                <div class="item-img">
                  <img
                    slot="cover"
                    :src="item.thumbnail || defaultThumbnail"
                    @error="onImageError(item)"
                    style="width: 300px; height: 170px; object-fit: contain"
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
                      <a-tag class="tag-no-border w-ellipsis-1" :color="item.layoutFixed ? 'blue' : undefined">
                        {{ item.layoutFixed ? '统一导航布局' : '自定义' }}
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
                      placement="top"
                      :arrowPointAtCenter="true"
                      title="确认要删除吗?"
                      ok-text="删除"
                      cancel-text="取消"
                      @confirm="confirmDeletePage(item, index)"
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
                          <Modal title="编辑" :ok="e => onConfirmSavePage(e, item, tab.key == 'pc')" :destroyOnClose="true">
                            属性
                            <template slot="content">
                              <PageDetail
                                :app-id="versionId"
                                :isPc="tab.key == 'pc'"
                                :detail="item"
                                :ref="'page_' + item.uuid"
                                :prodVersionUuid="versionUuid"
                              />
                            </template>
                          </Modal>
                        </a-menu-item>
                        <a-menu-item key="design">配置</a-menu-item>
                        <a-menu-item key="copy">复制</a-menu-item>
                        <!-- <a-menu-item key="setDefaultPage">设置为默认访问</a-menu-item> -->
                        <!-- <a-menu-item key="delete">删除</a-menu-item> -->
                      </a-menu>
                    </a-dropdown>
                  </div>
                </div>
                <!-- <template slot="actions" class="ant-card-actions">

              </template> -->
                <!-- <a-card-meta>
                  <div slot="title" style="display: flex; align-items: center">
                    <div
                      :title="item.name"
                      style="max-width: 150px; text-overflow: ellipsis; overflow: hidden; white-space: nowrap; padding-right: 5px"
                    >
                      {{ item.name }}
                    </div>
                    <a-tag :color="item.layoutFixed ? 'blue' : undefined">{{ item.layoutFixed ? '统一导航布局' : '自定义' }}</a-tag>
                    <a-popconfirm
                      placement="top"
                      :arrowPointAtCenter="true"
                      title="确认要删除吗?"
                      ok-text="删除"
                      cancel-text="取消"
                      @confirm="confirmDeletePage(item, index)"
                    >
                      <a-button
                        size="small"
                        :icon="item.deleting ? 'loading' : 'delete'"
                        type="link"
                        style="position: absolute; top: -4px; right: 5px"
                      />
                    </a-popconfirm>
                  </div>
                  <div slot="description" style="display: flex; justify-content: space-between; align-items: center">
                    <div style="max-width: 150px; text-overflow: ellipsis; overflow: hidden; white-space: nowrap">
                      {{ item.remark }}
                    </div>
                    <a-dropdown>
                      <a-button type="link" size="small" icon="more" />
                      <a-menu slot="overlay" @click="e => handleMenuClick(e, item, index)">
                        <a-menu-item key="edit">
                          <Modal title="编辑" :ok="e => onConfirmSavePage(e, item, tab.key == 'pc')">
                            属性
                            <template slot="content">
                              <PageDetail
                                :app-id="versionId"
                                :isPc="tab.key == 'pc'"
                                :detail="item"
                                :ref="'page_' + item.uuid"
                                :prodVersionUuid="versionUuid"
                              />
                            </template>
                          </Modal>
                        </a-menu-item>
                        <a-menu-item key="design">配置</a-menu-item>
                        <a-menu-item key="copy">复制</a-menu-item>
                       <a-menu-item key="setDefaultPage">设置为默认访问</a-menu-item> -->
                <!-- <a-menu-item key="delete">删除</a-menu-item> -->
                <!-- </a-menu>
                    </a-dropdown>
                  </div>
                  <a-avatar
                    shape="square"
                    :size="60"
                    slot="avatar"
                    style="background: var(--w-primary-color); display: flex; justify-content: center; align-items: center"
                  >
                    <Icon slot="icon" :type="item.icon || 'desktop'" style="font-size: 30px" />
                  </a-avatar>
                </a-card-meta> -->
              </a-card>
            </a-list-item>
          </a-list>
        </PerfectScrollbar>
      </a-tab-pane>
    </a-tabs>
    <Modal :ok="confirmPageCopy" v-model="copyPageVisible" :title="'复制页面: ' + copyPageFrom.fromName + '(' + copyPageFrom.fromId + ')'">
      <template slot="content">
        <a-form-model
          :model="copyPageFrom"
          :rules="copyPageRules"
          ref="copyPage"
          :colon="false"
          :label-col="{ span: 4 }"
          :wrapper-col="{ span: 19 }"
        >
          <a-form-model-item prop="name" label="名称">
            <a-input v-model.trim="copyPageFrom.name" />
          </a-form-model-item>
          <a-form-model-item prop="id" label="ID">
            <a-input :maxLength="120" v-model.trim="copyPageFrom.id" />
          </a-form-model-item>
        </a-form-model>
      </template>
    </Modal>
  </div>
</template>
<style lang="less"></style>
<script type="text/babel">
import Modal from '@pageAssembly/app/web/lib/modal.vue';
import PageDetail from './page-detail.vue';
import { generateId } from '@framework/vue/utils/util';
import moment from 'moment';
import { orderBy, debounce } from 'lodash';
import defaultThumbnail from '../../../login/component/login-right/thumbnail.png';

export default {
  name: 'ProductPageList',
  props: { versionId: String, versionUuid: String, setting: Object, prodId: String },
  components: { Modal, PageDetail },
  computed: {},
  data() {
    return {
      tabs: [
        { key: 'pc', title: '桌面端' },
        { key: 'mobile', title: '移动端' }
      ],
      grid: { gutter: 16, xs: 1, sm: 1, md: 2, lg: 2, xl: 3, xxl: 4 },
      loading: true,
      pcPageList: [],
      mobilePageList: [],

      copyPageVisible: false,
      copyPageFrom: {
        uuid: undefined,
        fromId: undefined,
        fromName: undefined,
        id: undefined,
        name: undefined
      },
      copyPageRules: {
        name: [{ required: true, trigger: ['blur'], message: '必填' }],
        id: [
          { required: true, trigger: ['blur', 'change'], message: '必填' },
          {
            pattern: /^\w+$/,
            message: 'ID只允许包含字母、数字以及下划线',
            trigger: ['blur', 'change']
          },
          { trigger: ['blur', 'change'], validator: this.validatePageId }
        ]
      },
      defaultThumbnail
    };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {
    this.fetchPageDefinitions(this.versionUuid).then(result => {
      console.log('查询页面返回: ', result);
      result.pc.forEach(item => {
        item.thumbnail = `/proxy-repository/repository/file/mongo/download?folderUuid=${item.id}&purpose=thumbnail`;
        this.pcPageList.push(item);
      });
      this.mobilePageList = result.mobile;
      this.loading = false;
      this.emitPageListChange();
    });
  },
  mounted() {},
  methods: {
    validatePageId: debounce(function (rule, value, callback) {
      $axios
        .get('/proxy/api/webapp/page/definition/existId', {
          params: { id: value }
        })
        .then(({ data }) => {
          callback(data.data === false ? undefined : '页面ID已存在');
        });
    }, 300),
    emitPageListChange() {
      this.$emit('pageListChange', {
        pc: this.pcPageList,
        mobile: this.mobilePageList
      });
    },
    confirmDeletePage(item, index) {
      this.$set(item, 'deleting', true);
      this.deletePage(item.id).then(() => {
        this[item.isPc == '1' ? 'pcPageList' : 'mobilePageList'].splice(index, 1);
        this.emitPageListChange();
      });
    },
    handleMenuClick(e, item, index) {
      if (e.key == 'delete') {
        this.deletePage(item.id).then(() => {
          this[item.isPc == '1' ? 'pcPageList' : 'mobilePageList'].splice(index, 1);
        });
      } else if (e.key == 'copy') {
        this.readyCopyPage(item, this[item.isPc == '1' ? 'pcPageList' : 'mobilePageList'], index);
      } else if (e.key == 'design') {
        window.open(`/index-designer/${item.appId}/${this.versionUuid}?uuid=${item.uuid}`, '_blank');
      } else if (e.key == 'setDefaultPage') {
        this.$set(this.setting, item.isPc == '1' ? 'pcIndexUrl' : 'mobileIndexUrl', `/web/prod/${this.versionUuid}/${item.id}`);
      }
    },

    readyCopyPage(item, siblings, index) {
      this.copyPageFrom.id = 'page_' + moment().format('yyyyMMDDHHmmss');
      this.copyPageFrom.name = this.getCopyName(siblings, item.name);
      this.copyPageFrom.uuid = item.uuid;
      this.copyPageFrom.fromName = item.name;
      this.copyPageFrom.fromId = item.id;
      this.copyPageVisible = true;
      this.copyPageFrom.index = index;
      this.copyPageFrom.isPc = item.isPc;
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
    confirmPageCopy() {
      this.copyPage().then(data => {
        this.$loading(false);
        this[this.copyPageFrom.isPc == '1' ? 'pcPageList' : 'mobilePageList'].splice(this.copyPageFrom.index, 0, {
          ...data
        });
        this.emitPageListChange();
      });
    },
    copyPage() {
      return new Promise((resolve, reject) => {
        this.$refs.copyPage.validate(pass => {
          if (pass) {
            this.$loading('复制中...');
            this.copyPageVisible = false;
            $axios
              .post('/json/data/services', {
                serviceName: 'appPageDefinitionService',
                methodName: 'get',
                args: JSON.stringify([this.copyPageFrom.uuid])
              })
              .then(({ data }) => {
                if (data.code == 0) {
                  let pageDefinition = data.data;
                  pageDefinition.uuid = undefined;
                  pageDefinition.name = this.copyPageFrom.name;
                  let jsonSource = JSON.parse(pageDefinition.definitionJson);
                  delete jsonSource.id;
                  let json = jsonSource;
                  json.uuid = undefined;
                  json.title = pageDefinition.name;
                  json.id = this.copyPageFrom.id;
                  pageDefinition.id = json.id;
                  pageDefinition.definitionJson = JSON.stringify(json);
                  pageDefinition.appWidgetDefinitionElements = json.appWidgetDefinitionElements;
                  pageDefinition.functionElements = json.functionElements;
                  $axios
                    .post('/web/design/savePageDefinition', pageDefinition)
                    .then(({ data }) => {
                      if (data.code == 0) {
                        pageDefinition.uuid = data.data;
                        PageDetail.methods.updateProdVersionRelaPage(this.versionUuid, pageDefinition.uuid);
                        this.createDefaultPagePrivilege(pageDefinition.id, pageDefinition.appId);
                        resolve(pageDefinition);
                      }
                    })
                    .catch(() => {
                      this.$loading(false);
                    });
                }
              })
              .catch(() => {
                this.$loading(false);
              });
          }
        });
      });
    },
    // 拷贝定义json，需要重置内部的组件ID
    copyDefinitionJson(jsonSource) {
      console.log('复制页面, 修改定义前: ', jsonSource);
      try {
        let json = JSON.parse(JSON.stringify(jsonSource));
        // json 组件定义的id替换
        let oldId2New = {};
        let _generateNewId = obj => {
          if (Array.isArray(obj) && obj.length > 0) {
            for (let i = 0, len = obj.length; i < len; i++) {
              _generateNewId(obj[i]);
            }
          } else if (typeof obj == 'object') {
            for (let key in obj) {
              if (key == 'id') {
                // 新ID进行替换
                let _id = oldId2New[obj.id] || generateId();
                oldId2New[obj.id] = _id;
                obj.id = _id;
              } else if (typeof obj[key] == 'object' || Array.isArray(obj[key])) {
                _generateNewId(obj[key]);
              }
            }
          }
        };
        _generateNewId(json);

        // console.log('递归复制新的定义json', JSON.parse(JSON.stringify(json)));
        // 替换引用到旧ID值的定义
        let jsonStr = JSON.stringify(json);
        console.log('修改定义生成的ID映射表: ', oldId2New);
        for (let id in oldId2New) {
          jsonStr = jsonStr.replace(new RegExp(id, 'g'), oldId2New[id]);
        }
        json = JSON.parse(jsonStr);
        console.log('复制页面, 修改定义后: ', json);
        return json;
      } catch (error) {
        this.$loading(false);
        console.error(error);
        this.$message.error('复制页面失败');
      }
    },
    deletePage(pageId) {
      return new Promise((resolve, reject) => {
        $axios
          .get(`/proxy/api/app/prod/version/deletePage`, {
            params: {
              pageId,
              prodVersionUuid: this.versionUuid
            }
          })
          .then(({ data }) => {
            if (data.code == 0) {
              this.$message.success('删除成功');
              resolve();
            } else {
              this.$message.success('删除异常');
              reject();
            }
          })
          .catch(error => {
            this.$message.success('删除异常');
          });
      });
    },
    onConfirmSavePage(e, item, isPc) {
      let $el = item ? this.$refs['page_' + item.uuid][0] : this.$refs.newPage[isPc ? 0 : 1];
      $el.save().then(data => {
        e(true);
        if (item == undefined) {
          this[isPc ? 'pcPageList' : 'mobilePageList'].splice(0, 0, { ...data });
        } else {
          item.remark = data.remark;
          item.name = data.name;
          item.layoutFixed = data.layoutFixed;
        }
        // 默认创建该页面的权限资源，用于角色勾选该首页时候，实际是与该权限建立角色、权限的关系
        if (item == null) {
          this.createDefaultPagePrivilege(data.id, this.versionId);
        }
        // 创建页面版本与产品版本的关系
        this.emitPageListChange();
      });
    },
    createDefaultPagePrivilege(pageId, appId, system) {
      return new Promise((resolve, reject) => {
        $axios
          .post('/proxy/api/security/privilege/savePrivilegeResource', {
            appId,
            name: '页面访问权限',
            code: 'PRIVILEGE_PAGE_' + pageId,
            enabled: true,
            systemDef: 1,
            system,
            otherResources: [{ type: 'appPageDefinition', resourceUuid: pageId }]
          })
          .then(({ data }) => {
            this.createDefaultPageRole(pageId, appId, data.data, system);
            resolve(data.data);
          });
      });
    },
    createDefaultPageRole(pageId, appId, privilegeUuid, system) {
      $axios
        .post(`/proxy/api/security/role/updateRoleMember`, [
          {
            role: {
              id: 'ROLE_VIEW_PAGE_' + pageId,
              code: 'ROLE_VIEW_PAGE_' + pageId,
              name: '页面访问角色',
              systemDef: 1,
              system,
              appId
            },
            privilegeAdded: [privilegeUuid]
          }
        ])
        .then(({ data }) => {})
        .catch(error => {});
    },

    fetchPageDefinitions(prodVersionUuid) {
      return new Promise((resolve, reject) => {
        $axios
          .get('/proxy/api/app/prod/version/pages', { params: { prodVersionUuid } })
          .then(({ data }) => {
            if (data.code == 0 && data.data) {
              let result = {
                pc: [],
                mobile: []
              };
              // 仅展示最新版本的数据
              let list = orderBy(
                data.data,
                [
                  o => {
                    return parseFloat(o.version);
                  }
                ],
                ['desc']
              );
              let id = [];
              for (let d of list) {
                if (id.includes(d.id)) {
                  continue;
                }
                id.push(d.id);
                if (d.isPc == 1) {
                  result.pc.push(d);
                } else {
                  result.mobile.push(d);
                }
              }
              resolve(result);
            }
          })
          .catch(error => {});
      });
    },
    onImageError(item) {
      item.thumbnail = this.defaultThumbnail;
    }
  }
};
</script>
