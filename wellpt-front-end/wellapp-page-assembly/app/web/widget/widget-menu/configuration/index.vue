<template>
  <div>
    <a-tabs default-active-key="1">
      <a-tab-pane key="1" tab="设置">
        <a-form-model :model="widget.configuration" labelAlign="left" :wrapper-col="{ style: { textAlign: 'right' } }" :colon="false">
          <a-form-model-item label="名称">
            <a-input v-model="widget.title" />
          </a-form-model-item>
          <a-collapse :bordered="false" expandIconPosition="right" defaultActiveKey="basicSet">
            <a-collapse-panel key="basicSet" header="整体设置">
              <a-form-model-item label="JS模块">
                <JsModuleSelect v-model="widget.configuration.jsModules" />
              </a-form-model-item>
              <a-form-model-item label="导航类型">
                <a-radio-group size="small" v-model="widget.configuration.mode" button-style="solid" @change="onChangeMode">
                  <a-radio-button value="inline">侧边导航</a-radio-button>
                  <a-radio-button value="horizontal">顶部导航</a-radio-button>
                </a-radio-group>
              </a-form-model-item>
              <a-form-model-item label="水平位置" v-show="widget.configuration.mode == 'horizontal'">
                <a-radio-group size="small" v-model="widget.configuration.align" button-style="solid">
                  <a-radio-button value="left">居左</a-radio-button>
                  <a-radio-button value="center">居中</a-radio-button>
                  <a-radio-button value="right">居右</a-radio-button>
                </a-radio-group>
              </a-form-model-item>

              <a-form-model-item label="导航头部">
                <a-switch v-model="widget.configuration.menuHeader" />
              </a-form-model-item>

              <div v-show="widget.configuration.menuHeader">
                <a-form-model-item label="头部内容">
                  <a-radio-group size="small" v-model="widget.configuration.headerContentType" button-style="solid">
                    <a-radio-button value="iconTitle">图标标题</a-radio-button>
                    <a-radio-button value="logo">设置LOGO</a-radio-button>
                  </a-radio-group>
                </a-form-model-item>
                <div v-show="widget.configuration.headerContentType == 'iconTitle'">
                  <a-form-model-item label="导航标题">
                    <a-input v-model="widget.configuration.menuTitle">
                      <template slot="addonAfter">
                        <a-switch v-model="widget.configuration.menuTitleVisible" size="small" />
                        <WI18nInput
                          v-show="widget.configuration.menuTitleVisible"
                          :widget="widget"
                          :designer="designer"
                          :code="widget.id + '_menuTitle'"
                          v-model="widget.configuration.menuTitle"
                        />
                      </template>
                    </a-input>
                  </a-form-model-item>

                  <a-form-model-item label="标题图标">
                    <WidgetIconDesignDrawer
                      v-model="widget.configuration.menuTitleIcon"
                      :widget="widget"
                      :designer="designer"
                      onlyIconClass
                    />
                  </a-form-model-item>
                </div>

                <a-form-model-item label="LOGO" v-if="widget.configuration.headerContentType == 'logo'">
                  <div>
                    <a-switch v-model="widget.configuration.logoUseInput" size="small">
                      <Icon type="pticon iconfont icon-ptkj-bianji" slot="checkedChildren" />
                      <Icon type="pticon iconfont icon-ptkj-bianji" slot="unCheckedChildren" />
                    </a-switch>
                  </div>
                  <a-textarea
                    v-show="widget.configuration.logoUseInput"
                    style="width: 100%"
                    v-model.trim="widget.configuration.logoInputValue"
                    :auto-size="{ minRows: 3, maxRows: 20 }"
                  />

                  <span v-show="!widget.configuration.logoUseInput" style="position: relative; float: right">
                    <a-upload
                      name="thumbnail"
                      list-type="picture-card"
                      :file-list="[]"
                      :show-upload-list="false"
                      :before-upload="e => beforeUpload(e, 'thumbnail')"
                      :customRequest="e => customRequest(e, 'thumbnail')"
                    >
                      <img
                        v-if="widget.configuration.logo"
                        :src="widget.configuration.logo"
                        alt="avatar"
                        style="width: 86px; height: 86px; object-fit: scale-down"
                      />
                      <div v-else>
                        <Icon :type="uploading ? 'loading' : 'pticon iconfont icon-ptkj-jiahao'" />
                        <div class="ant-upload-text">点击上传</div>
                      </div>
                      <a-button
                        style="position: absolute; top: 0px; right: 10px"
                        size="small"
                        type="link"
                        title="删除"
                        @click.stop="widget.configuration.logo = undefined"
                      >
                        <Icon type="pticon iconfont icon-ptkj-shanchu"></Icon>
                      </a-button>
                    </a-upload>
                  </span>
                </a-form-model-item>
              </div>

              <!-- <a-form-model-item label="导航底部">
                <a-switch v-model="widget.configuration.menuFooter" />
              </a-form-model-item>
              <div v-show="widget.configuration.menuFooter">
                <a-form-model-item label="底部文本">
                  <a-input v-model="widget.configuration.menuFooterTitle"></a-input>
                </a-form-model-item>
                <a-form-model-item label="底部图标">
                  <WidgetDesignDrawer :id="'widgetMenuFooterTitleIconPrefix' + widget.id" title="选择图标" :designer="designer">
                    <a-badge>
                      <a-icon
                        v-if="widget.configuration.menuFooterTitleIcon"
                        slot="count"
                        type="close-circle"
                        style="color: #f5222d"
                        theme="filled"
                        @click.stop="widget.configuration.menuFooterTitleIcon = undefined"
                        title="删除图标"
                      />
                      <a-button size="small" shape="round">
                        {{ widget.configuration.menuFooterTitleIcon ? '' : '设置图标' }}
                        <Icon :type="widget.configuration.menuFooterTitleIcon || 'setting'" />
                      </a-button>
                    </a-badge>

                    <template slot="content">
                      <WidgetIconLib v-model="widget.configuration.menuFooterTitleIcon" />
                    </template>
                  </WidgetDesignDrawer>
                </a-form-model-item>
              </div> -->
              <a-form-model-item label="子菜单交互模式">
                <a-radio-group size="small" v-model="widget.configuration.subMenuExpandType" button-style="solid">
                  <a-radio-button value="dropdown">下拉展开</a-radio-button>
                  <a-radio-button value="popover" v-if="widget.configuration.mode == 'inline'">浮层展开</a-radio-button>
                  <a-radio-button value="topDrawer" v-if="widget.configuration.mode == 'horizontal'">抽屉展开</a-radio-button>
                </a-radio-group>
              </a-form-model-item>
              <a-form-model-item
                label="菜单手风琴效果"
                v-show="widget.configuration.mode == 'inline' && widget.configuration.subMenuExpandType == 'dropdown'"
              >
                <a-switch v-model="widget.configuration.accordion" />
              </a-form-model-item>
              <a-form-model-item label="默认展开" v-if="widget.configuration.mode == 'inline'">
                <a-select style="width: 100%" :options="defaultExpandOptions" v-model="widget.configuration.defaultExpandType" />
              </a-form-model-item>
              <a-form-model-item label="默认选中">
                <a-tree-select
                  v-model="widget.configuration.defaultSelectedKey"
                  style="width: 100%"
                  :tree-data="widget.configuration.menus"
                  :replaceFields="{ children: 'menus', key: 'id', value: 'id' }"
                  allow-clear
                />
              </a-form-model-item>
              <a-form-model-item label="徽标风格">
                <a-radio-group size="small" v-model="widget.configuration.menuBadge.badgeDisplayType" button-style="solid">
                  <a-radio-button value="text">普通文本</a-radio-button>
                  <a-radio-button value="badge">胶囊</a-radio-button>
                  <a-radio-button value="dot">红点</a-radio-button>
                </a-radio-group>
              </a-form-model-item>

              <div
                v-show="widget.configuration.menuBadge.badgeDisplayType != 'dot'"
                :style="{
                  lineHeight: '30px'
                }"
              >
                <a-form-model-item label="徽标封顶数字">
                  <a-radio-group size="small" v-model="widget.configuration.menuBadge.badgeOverflowCountShowType" button-style="solid">
                    <a-radio-button value="systemDefault">系统默认</a-radio-button>
                    <a-radio-button value="limitless">无封顶</a-radio-button>
                    <a-radio-button value="customize">自定义</a-radio-button>
                  </a-radio-group>
                  <a-input-number
                    v-model="widget.configuration.menuBadge.badgeOverflowCount"
                    :min="1"
                    size="small"
                    v-show="widget.configuration.menuBadge.badgeOverflowCountShowType == 'customize'"
                  />
                </a-form-model-item>

                <a-form-model-item label="徽标数值为0时">
                  <a-radio-group size="small" v-model="widget.configuration.menuBadge.badgeShowZero" button-style="solid">
                    <a-radio-button value="systemDefault">系统默认</a-radio-button>
                    <a-radio-button value="yes">显示</a-radio-button>
                    <a-radio-button value="no">不显示</a-radio-button>
                  </a-radio-group>
                </a-form-model-item>
              </div>
            </a-collapse-panel>
            <a-collapse-panel key="menuSet" header="导航设置">
              <!-- <a-form-model-item label="导航项来源">
                <a-radio-group size="small" v-model="widget.configuration.menuSource" button-style="solid">
                  <a-radio-button value="customize">自定义</a-radio-button>
                  <a-radio-button value="api">接口服务</a-radio-button>
                </a-radio-group>
              </a-form-model-item> -->
              <div v-show="widget.configuration.menuSource == 'customize'" style="padding: 5px; margin: 0px 5px; background-color: #fff">
                <DraggableTreeList
                  v-model="widget.configuration.menus"
                  childrenField="menus"
                  :maxLevel="3"
                  draggable
                  expandIcon="plus"
                  dragButton
                  dragButtonPosition="end"
                  :titleWidth="180"
                >
                  <template slot="title" slot-scope="scope">
                    <a-input size="small" v-model="scope.item.title" @click.stop="() => {}">
                      <template slot="prefix" v-if="scope.item.icon">
                        <Icon :type="scope.item.icon" />
                      </template>
                      <template slot="suffix" v-if="scope.item.hidden">
                        <Icon type="iconfont icon-wsbs-yincang" />
                      </template>
                    </a-input>
                  </template>
                  <template slot="operation" slot-scope="scope">
                    <a-button v-show="scope.level < 3" size="small" title="添加子菜单" type="link" @click.stop="addMenu(scope.item)">
                      <Icon type="pticon iconfont icon-ptkj-jiahao"></Icon>
                    </a-button>

                    <WidgetDesignDrawer :id="'menuMoreConfig' + scope.item.id" title="设置" :designer="designer">
                      <a-button size="small" title="更多设置" type="link"><Icon type="pticon iconfont icon-ptkj-shezhi"></Icon></a-button>
                      <template slot="content">
                        <MenuConfiguration :widget="widget" :designer="designer" :menu="scope.item" />
                      </template>
                    </WidgetDesignDrawer>

                    <a-button size="small" title="删除菜单" type="link" @click.stop="delMenu(scope.item)">
                      <Icon type="pticon iconfont icon-ptkj-shanchu"></Icon>
                    </a-button>
                  </template>
                </DraggableTreeList>
                <a-button type="link" @click="addMenu(undefined)" size="small">
                  <Icon type="pticon iconfont icon-ptkj-jiahao"></Icon>
                  添加菜单
                </a-button>
              </div>
            </a-collapse-panel>
            <a-collapse-panel key="functionMenuSet" header="右侧导航" v-if="widget.configuration.mode == 'horizontal'">
              <a-form-model-item label="启用">
                <a-switch v-model="widget.configuration.enableSysMenu" />
              </a-form-model-item>
              <div v-show="widget.configuration.enableSysMenu">
                <DraggableTreeList
                  v-model="widget.configuration.sysFunctionMenus"
                  childrenField="menus"
                  :maxLevel="2"
                  draggable
                  expandIcon="plus"
                  dragButton
                  dragButtonPosition="end"
                  :titleWidth="180"
                >
                  <template slot="title" slot-scope="scope">
                    <a-input size="small" v-model="scope.item.title" style="width: 130px" @click.stop="() => {}">
                      <template slot="prefix" v-if="scope.item.icon">
                        <Icon :type="scope.item.icon" />
                      </template>
                      <template slot="suffix" v-if="scope.item.hidden">
                        <Icon type="iconfont icon-wsbs-yincang" />
                      </template>
                    </a-input>
                  </template>
                  <template slot="operation" slot-scope="scope">
                    <a-button v-show="scope.level < 2" size="small" title="添加子菜单" type="link" @click.stop="addSysMenu(scope.item)">
                      <Icon type="pticon iconfont icon-ptkj-jiahao"></Icon>
                    </a-button>

                    <WidgetDesignDrawer :id="'sysMenuMoreConfig' + scope.item.id" title="设置" :designer="designer">
                      <a-button size="small" title="更多设置" type="link"><Icon type="pticon iconfont icon-ptkj-shezhi"></Icon></a-button>
                      <template slot="content">
                        <MenuConfiguration :widget="widget" :designer="designer" :menu="scope.item" />
                      </template>
                    </WidgetDesignDrawer>

                    <a-button size="small" title="删除菜单" type="link" @click.stop="scope.items.splice(scope.index, 1)">
                      <Icon type="pticon iconfont icon-ptkj-shanchu"></Icon>
                    </a-button>
                  </template>
                </DraggableTreeList>
                <a-button type="link" @click="addSysMenu(undefined)" size="small">
                  <Icon type="pticon iconfont icon-ptkj-jiahao"></Icon>
                  添加菜单
                </a-button>
              </div>
            </a-collapse-panel>
          </a-collapse>

          <!-- <a-form-model-item label="菜单下拉展开方式" v-if="widget.configuration.mode == 'horizontal'">
            <a-radio-group  size="small"v-model="widget.configuration.menuDropdownType" button-style="solid">
              <a-radio-button value="ownDropdown">单列下拉展开</a-radio-button>
              <a-radio-button value="dropdownAll">全部菜单下拉展开</a-radio-button>
            </a-radio-group>
          </a-form-model-item> -->
        </a-form-model>
      </a-tab-pane>
      <a-tab-pane key="2" tab="事件设置">
        <WidgetEventConfiguration :widget="widget" :designer="designer"></WidgetEventConfiguration>
      </a-tab-pane>
    </a-tabs>
  </div>
</template>

<style lang="less" scoped></style>

<script type="text/babel">
import { generateId } from '@framework/vue/utils/util';
import draggable from '@framework/vue/designer/draggable';
import MenuConfiguration from './menu-configuration.vue';
import WidgetIconDesignDrawer from '@pageAssembly/app/web/lib/widget-icon-design-drawer.vue';

export default {
  name: 'WidgetMenuConfiguration',
  mixins: [draggable],
  props: {
    widget: Object,
    designer: Object
  },
  data() {
    return {
      selectedMenu: {},
      expandedRowKeys: [],
      overflowCountTags: ['9', '99', '999', '9999', '按实际数值展示'],
      defaultExpandOptions: [
        {
          label: '默认不展开',
          value: 'defaultCollapseAll'
        },
        {
          label: '默认展开第一菜单',
          value: 'defaultExpandFirst'
        },
        {
          label: '默认展开全部一级菜单',
          value: 'defaultExpandAllLevelOne'
        },
        {
          label: '默认展开全部菜单',
          value: 'defaultExpandAll'
        },
        {
          label: '默认按默认选中展开',
          value: 'defaultByDefaultSelected'
        }
      ]
    };
  },

  beforeCreate() {},
  components: { MenuConfiguration, WidgetIconDesignDrawer },
  computed: {
    menuKeyMap() {
      let map = {};
      let cascadeSetKeyMap = function (menus) {
        if (menus)
          for (let i = 0, len = menus.length; i < len; i++) {
            let menu = menus[i];
            map[menu.id] = menu;
            cascadeSetKeyMap.call(this, menus[i].menus);
          }
      };

      for (let i = 0, len = this.widget.configuration.menus.length; i < len; i++) {
        let menu = this.widget.configuration.menus[i];
        map[menu.id] = menu;
        cascadeSetKeyMap.call(this, menu.menus);
      }
      return map;
    }
  },
  created() {},
  methods: {
    getWidgetI18nObjects(widget) {
      function findI18nObjects(json, i18n) {
        function traverse(obj, i18n) {
          if (typeof obj !== 'object' || obj === null || (obj.wtype && obj.id)) return;
          if (Array.isArray(obj)) {
            for (let item of obj) {
              traverse(item, i18n);
            }
          } else {
            for (let key in obj) {
              if (key === 'i18n') {
                i18n.push(obj[key]);
              }
              traverse(obj[key], i18n);
            }
          }
        }
        traverse(json, i18n);
      }
      const i18n = [];
      findI18nObjects(widget.configuration, i18n);
      return i18n;
    },

    // 生成组件定义数据保存
    getWidgetDefinitionElements(widget) {
      return [
        {
          wtype: widget.wtype,
          id: widget.id,
          title: widget.title,
          main: !!widget.main, // 是否被标记为主组件，用于模块的主导航标记
          definitionJson: JSON.stringify(widget)
        }
      ];
    },
    customRequest(options, key) {
      this.uploading = true;
      let file = options.file,
        fileSize = file.size,
        fileName = file.name,
        formData = new FormData();
      formData.set('frontUUID', file.uid);
      formData.set('localFileSourceIcon', '');
      formData.set('size', fileSize);
      let headers = {
        'Content-Disposition': `attachment; filename="${encodeURIComponent(fileName)}"`,
        'Content-Type': 'multipart/form-data'
      };
      formData.set('file', file);
      $axios
        .post('/proxy-repository/repository/file/mongo/savefilesChunk', formData, {
          headers: headers
        })
        .then(({ data }) => {
          this.uploading = false;
          if (data.code == 0 && data.data) {
            options.onSuccess();
            this.$set(this.widget.configuration, 'logo', `/proxy-repository/repository/file/mongo/download?fileID=${data.data[0].fileID}`);
          }
        });
    },
    beforeUpload(file) {
      let isJpgOrPng = ['image/gif', 'image/jpeg', 'image/png'].includes(file.type);
      if (!isJpgOrPng) {
        this.$message.error('只允许上传 jpeg、png 或者 gif 图片格式');
      }
      return isJpgOrPng;
    },

    onChangeMode() {
      if (this.widget.configuration.mode == 'inline' && this.widget.configuration.subMenuExpandType === 'dropdownAllSecondarySubMenu') {
        this.widget.configuration.subMenuExpandType = 'dropdown';
      }
      if (this.widget.configuration.mode == 'horizontal' && this.widget.configuration.subMenuExpandType === 'popover') {
        this.widget.configuration.subMenuExpandType = 'dropdown';
      }
    },
    addMenu(parent) {
      let menu = {
        id: `menu-${generateId()}`,
        title: '菜单',
        icon: undefined,
        hidden: false,
        menus: [],
        badge: {
          enable: false,
          getNumBy: 'jsFunction',
          countJsFunction: undefined
        },
        eventHandler: {}
      };
      if (parent == undefined) {
        menu.level = 1;
        menu.icon = 'appstore';
        this.widget.configuration.menus.push(menu);
      } else {
        if (parent.menus === undefined) {
          this.$set(parent, 'menus', []);
        }
        menu.level = parent.level + 1;
        menu.pid = parent.id;
        parent.menus.push(menu);
      }
      if (parent != undefined && !this.expandedRowKeys.includes(parent.id)) {
        this.expandedRowKeys.push(parent.id);
      }
    },
    addSysMenu(parent) {
      let menu = {
        id: `menu-${generateId()}`,
        title: '功能',
        icon: undefined,
        hidden: false,
        menus: [],
        badge: {
          enable: false,
          getNumBy: 'jsFunction',
          countJsFunction: undefined
        },
        eventHandler: {}
      };
      if (parent == undefined) {
        menu.level = 1;
        this.widget.configuration.sysFunctionMenus.push(menu);
      } else {
        if (parent.menus === undefined) {
          this.$set(parent, 'menus', []);
        }
        menu.level = parent.level + 1;
        menu.pid = parent.id;
        parent.menus.push(menu);
      }
    },
    delMenu(record) {
      let menus = record.pid ? this.menuKeyMap[record.pid].menus : this.widget.configuration.menus,
        id = record.id;

      for (var i = 0, len = menus.length; i < len; i++) {
        if (menus[i].id === id) {
          menus.splice(i, 1);

          break;
        }
      }
      if (record.pid && menus.length === 0) {
        delete this.menuKeyMap[record.pid].menus;
      }
    },

    setLanguage(evt, menu) {
      //FIXME: 测试国际化语言
      if (!this.widget.configuration.i18n) {
        this.widget.configuration.i18n = {
          zh_CN: {},
          en_US: {}
        };
      }
      this.widget.configuration.i18n.en_US[menu.id] = 'abcd';
    },
    getFunctionElements(wgt, EWidgets) {
      let elements = {};
      elements[wgt.id] = [];
      if (wgt.configuration.menus.length != 0) {
        let menusElements = [];
        let cascade = (menus, level) => {
          if (menus != undefined) {
            for (let i = 0, len = menus.length; i < len; i++) {
              let menu = menus[i];
              let m = {
                id: menu.id,
                uuid: menu.id,
                name: `导航_${menu.title}`,
                code: 'sidebar_' + level + '_' + (i + 1),
                type: 'nav'
              };

              if (menu.appId != undefined) {
                m.resourceId = menu.appId;
                m.resourceType = 'appModule';
              }
              // FIXME: 导航打开页面时要直接不展示无权限的菜单，还是等菜单打开后提示403内容
              else if (
                menu.eventHandler &&
                menu.eventHandler.pageId != undefined &&
                menu.eventHandler.actionType == 'redirectPage' &&
                menu.eventHandler.pageType == 'page'
              ) {
                m.resourceId = menu.eventHandler.pageId;
                m.resourceType = 'appPageDefinition';
              }
              menusElements.push(m);
              cascade(menu.menus, level + 1);
            }
          }
        };
        cascade(wgt.configuration.menus, 1);
        elements[wgt.id] = menusElements;
      }

      // 导出logo图片
      if (wgt.configuration.logo && wgt.configuration.logo.startsWith('/proxy-repository/repository/file/mongo/download')) {
        elements[wgt.id].push({
          ref: true,
          functionType: 'logicFileInfo',
          exportType: 'logicFileInfo',
          configType: '1',
          uuid: wgt.configuration.logo.split('/proxy-repository/repository/file/mongo/download?fileID=')[1],
          isProtected: false
        });
      }

      return elements;
    }
  },
  mounted() {},
  configuration() {
    let generateSysFunctionMenus = () => {
      return [
        // {
        //   id: `menu-${generateId()}`,
        //   title: '在线人数',
        //   titleHidden: true,
        //   code: 'onlineUserCount',
        //   icon:'mail',
        //   level: 1,
        //   hidden: false,
        //   eventHandler: {}
        // },
        {
          id: `menu-${generateId()}`,
          title: '消息',
          titleHidden: true,
          code: 'HeaderOnlineMsg',
          level: 1,
          icon: 'message',
          hidden: false,
          eventHandler: {}
        },
        {
          id: `menu-${generateId()}`,
          title: '邮件',
          titleHidden: true,
          code: 'HeaderMail',
          level: 1,
          icon: 'mail',
          hidden: false,
          eventHandler: {}
        },
        {
          id: `menu-${generateId()}`,
          title: '个性化设置',
          titleHidden: true,
          code: 'HeaderThemeSet',
          level: 1,
          icon: 'skin',
          hidden: false,
          eventHandler: {}
        },
        {
          id: `menu-${generateId()}`,
          title: '工作台',
          titleHidden: true,
          code: 'HeaderSwitchWorkbenches',
          level: 1,
          icon: 'appstore',
          hidden: false,
          eventHandler: {}
        },
        {
          id: `menu-${generateId()}`,
          title: '用户',
          titleHidden: true,
          level: 1,
          icon: 'user',
          code: 'HeaderUserAvatar',
          hidden: false,
          menus: [
            {
              id: `menu-${generateId()}`,
              title: '个人中心',
              titleHidden: true,
              code: 'HeaderUserCenter',
              level: 2,
              icon: 'solution',
              hidden: false,
              eventHandler: {}
            },

            {
              id: `menu-${generateId()}`,
              title: '修改密码',
              titleHidden: true,
              code: 'HeaderModifyPassword',
              icon: 'lock',
              hidden: false,
              level: 2,
              eventHandler: {}
            }
          ],
          eventHandler: {}
        },
        {
          id: `menu-${generateId()}`,
          title: '退出登录',
          titleHidden: true,
          code: 'HeaderLogout',
          icon: 'poweroff',
          hidden: false,
          level: 1,
          eventHandler: {}
        }
      ];
    };
    return {
      bgColorType: undefined,
      menuSource: 'customize', // customize 自定义 \ API 接口
      mode: 'inline', // inline: 左侧导航 \ horizontal: 水平导航（用于顶部导航时效果）
      defaultExpandType: 'defaultCollapseAll',
      // menuDropdownType: 'ownDropdown', // 单列菜单下拉展开 ownDropdown 下拉展开全部（一级）菜单 dropdownAll
      menuHeader: false, // 菜单头部
      menuTitle: undefined,
      menuTitleVisible: false,
      menuTitleIcon: undefined,
      menuFooter: false,
      menuFooterTitle: undefined,
      menuFooterTitleIcon: undefined,
      subMenuExpandType: 'dropdown', // dropdown: 下拉展开 \ popover: 浮层展开 \ topDrawer: 顶部浮层
      accordion: false,
      align: 'left',
      themeType: 'light',
      enableCollapse: false,
      breadcrumbVisible: false,
      menuBadge: {
        badgeDisplayType: 'text',
        badgeOverflowCountShowType: 'limitless',
        badgeOverflowCount: undefined,
        badgeShowZero: 'no'
      },
      logoUseInput: false,
      logoInputValue: undefined,
      logo: undefined,
      menus: [
        {
          id: generateId(),
          title: '菜单一',
          icon: 'appstore',
          level: 1,
          eventHandler: {},
          menus: []
        },
        {
          id: generateId(),
          title: '菜单二',
          icon: 'appstore',
          level: 1,
          eventHandler: {},
          menus: []
        },
        {
          id: generateId(),
          title: '菜单三',
          icon: 'appstore',
          level: 1,
          eventHandler: {},
          menus: []
        }
      ],
      sysFunctionMenus: generateSysFunctionMenus(), // 系统级功能菜单：在线人事、登录退出等
      sysMenuBadge: {
        badgeDisplayType: 'text',
        badgeOverflowCountShowType: 'limitless',
        badgeOverflowCount: undefined,
        badgeShowZero: 'no'
      }
    };
  }
};
</script>
