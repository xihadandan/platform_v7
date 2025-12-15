<template>
  <a-collapse :bordered="false" expandIconPosition="right" defaultActiveKey="1" class="prod-nav-setting" :accordion="true" v-if="loaded">
    <a-collapse-panel key="1">
      <template slot="header">
        <label class="">主导航</label>
      </template>
      <a-tabs class="radio-group-style">
        <a-tab-pane key="1" tab="导航项">
          <a-row type="flex" style="margin-bottom: 12px; margin-right: 20px; flex-wrap: nowrap; align-items: center">
            <a-col flex="auto">
              <WidgetDesignDrawer
                id="sysFuncMenu_1"
                title="添加"
                :designer="designer"
                placement="left"
                :width="drawerWidth"
                :zIndex="drawerZIndex"
              >
                <a-button @click="resetNewMenu(1)" block>
                  <Icon type="pticon iconfont icon-ptkj-jiahao"></Icon>
                  添加同级
                </a-button>
                <template slot="content">
                  <MenuConfiguration :widget="widgetMenu" :designer="designer" :menu="newMenu" />
                </template>
                <template slot="footer" slot-scope="drawerScope">
                  <a-button type="primary" @click="onAddNewMenu(widgetMenu.configuration.sysFunctionMenus, 1, drawerScope)">保存</a-button>
                </template>
              </WidgetDesignDrawer>
            </a-col>
            <!-- <a-col flex="130px">
              <span style="font-size: var(--w-font-size-base); color: var(--w-text-color-dark)">拖动排序</span>
              <a-switch v-model="funcMenuSortable">
                <a-icon slot="checkedChildren" type="swap" style="transform: rotate(-90deg)" />
                <a-icon slot="unCheckedChildren" type="swap" style="transform: rotate(-90deg)" />
              </a-switch>
            </a-col> -->
          </a-row>
          <PerfectScrollbar style="max-height: calc(100vh - 180px); padding-right: 20px">
            <DraggableTreeList
              v-if="widgetMenu != undefined"
              v-model="widgetMenu.configuration.sysFunctionMenus"
              childrenField="menus"
              @add="onAddSysFunctionMenu"
              :titleWidth="110"
              :draggable="funcMenuSortable"
              :dragButton="true"
              :hoverShowButtons="true"
              dragButtonType="text"
              :maxLevel="2"
              class="menu-tree"
              :itemClass="getMenuHiddenClass"
              style="
                --w-pt-draggable-tree-list-color-hover: var(--w-primary-color);
                --w-pt-draggable-tree-list-bg-color-hover: var(--w-primary-color-1);
              "
            >
              <template slot="operation" slot-scope="scope">
                <a-button size="small" type="text" v-if="!scope.item.code" icon="delete" @click.stop="scope.items.splice(scope.index, 1)" />
                <WidgetDesignDrawer
                  v-if="scope.level < 2"
                  :id="'sysFuncMenuAdd_' + scope.item.id"
                  title="添加"
                  :designer="designer"
                  placement="left"
                  :width="drawerWidth"
                  :zIndex="drawerZIndex"
                >
                  <a-button size="small" type="text" title="添加"><Icon type="pticon iconfont icon-ptkj-jiahao"></Icon></a-button>
                  <template slot="content">
                    <MenuConfiguration :widget="widgetMenu" :designer="designer" :menu="newMenu" />
                  </template>
                  <template slot="footer" slot-scope="drawerScope">
                    <a-button type="primary" size="small" @click="onAddNewMenu(scope.item.menus, scope.level + 1, drawerScope)">
                      保存
                    </a-button>
                  </template>
                </WidgetDesignDrawer>
                <WidgetDesignDrawer
                  :id="'sysFuncMenuEdit_' + scope.item.id"
                  title="编辑"
                  :designer="designer"
                  placement="left"
                  :width="drawerWidth"
                  :zIndex="drawerZIndex"
                >
                  <a-button size="small" type="text" title="编辑"><Icon type="pticon iconfont icon-ptkj-bianji"></Icon></a-button>
                  <template slot="content">
                    <MenuConfiguration :widget="widgetMenu" :designer="designer" :menu="scope.item" />
                  </template>
                </WidgetDesignDrawer>
                <a-button title="显示/隐藏" size="small" type="text" @click.stop="scope.item.hidden = !scope.item.hidden">
                  <Icon :type="scope.item.hidden ? 'pticon iconfont icon-wsbs-yincang' : 'pticon iconfont icon-wsbs-xianshi'"></Icon>
                </a-button>
              </template>
            </DraggableTreeList>
          </PerfectScrollbar>
        </a-tab-pane>
        <a-tab-pane key="2" tab="导航设置">
          <PerfectScrollbar style="height: calc(100vh - 180px); padding-right: 20px">
            <a-form-model :colon="false" class="pt-form">
              <a-form-model-item>
                <template slot="label">
                  主标题
                  <WI18nInput
                    :widget="widgetLayout.configuration.header"
                    :target="widgetLayout.configuration.header.configuration"
                    v-model="widgetLayout.configuration.header.configuration.title"
                    code="headerTitle"
                    :htmlEditor="true"
                  />
                  <a-checkbox
                    v-model="widgetLayout.configuration.header.configuration.enablePrefixTitleLogo"
                    @change="e => onChangeHeaderTitleType(e)"
                    style="margin-left: 80px; --w-checkbox-lr-padding: 4px"
                  >
                    前置logo
                  </a-checkbox>
                  <!-- <a-switch v-model="widgetMenu.configuration.menuTitleVisible" size="small" /> -->
                </template>

                <template v-if="widgetLayout.configuration.header.configuration.enablePrefixTitleLogo">
                  <ImageLibrary
                    v-model="widgetLayout.configuration.header.configuration.prefixTitleLogo"
                    width="100%"
                    :height="100"
                    :limitSize="limitSize"
                    :acceptType="acceptTypes"
                    :acceptTip="acceptTip"
                    :emptyVisible="false"
                  />
                  <div class="img-upload-tip" style="margin-bottom: 12px">
                    支持格式: JPG、PNG、GIF、SVG; 大小限制: 不超过50M; 建议尺寸: 150px * 100px
                  </div>
                </template>
                <QuillEditor
                  min-height="unset"
                  @input="onInputTitle"
                  v-model="widgetLayout.configuration.header.configuration.title"
                  :hiddenButtons="['image', 'underline', 'minusIndent', 'addIndent', 'align', 'ordered', 'list', 'source', 'fullscreen']"
                />
              </a-form-model-item>
              <a-form-model-item>
                <template slot="label">
                  logo (通用)
                  <WidgetDesignDrawer
                    id="moreLogoSet"
                    v-model="moreLogoSetVisible"
                    title="更多logo设置"
                    :designer="designer"
                    placement="left"
                    :zIndex="drawerZIndex"
                  >
                    <a-button
                      size="small"
                      type="link"
                      style="margin-left: 50px"
                      @click.stop="
                        () => {
                          moreLogoSetVisible = true;
                        }
                      "
                    >
                      <Icon type="pticon iconfont icon-ptkj-shezhi"></Icon>
                      更多logo设置
                    </a-button>
                    <template slot="content">
                      <div class="more-logo-set">
                        <a-alert
                          type="info"
                          :show-icon="false"
                          message="为了在不同的页面布局和导航背景色中, 均有良好的视觉效果, 您可以对不同场景设置不同的logo, 未设置则使用通用logo"
                          banner
                          style="margin-bottom: 20px"
                        />
                        <a-tabs default-active-key="header">
                          <a-tab-pane v-for="(pos, i) in ['header', 'sider']" :key="pos" :tab="pos == 'sider' ? '左导航logo' : '横幅logo'">
                            <template v-for="(type, i) in ['light', 'dark']">
                              <a-row :key="type + 'bgLogo_' + i" style="padding: 15px; outline: 1px solid #e8e8e8">
                                <a-col :span="12">
                                  <div style="line-height: 39px; font-weight: 600">
                                    {{ type == 'light' ? '浅色背景' : pos == 'sider' ? '深色/主题色背景' : '主题色背景' }}
                                  </div>
                                  <ImageLibrary
                                    v-model="widgetLayout.configuration[pos].configuration[type + 'BgColorTypeLogo']"
                                    width="100%"
                                    :height="100"
                                    :limitSize="limitSize"
                                    :acceptType="acceptTypes"
                                    :acceptTip="acceptTip"
                                  />
                                  <!-- <div style="width: 100%; height: 100px; border-radius: 2px; position: relative" class="transparent-bg">
                                      <img
                                        :src="widgetLayout.configuration[pos].configuration[type + 'BgColorTypeLogo']"
                                        v-if="widgetLayout.configuration[pos].configuration[type + 'BgColorTypeLogo']"
                                        class="nav-logo"
                                      />
                                      <a-button
                                        size="small"
                                        icon="delete"
                                        type="danger"
                                        class="delete-img"
                                        @click.stop="clearLogo(widgetLayout.configuration[pos].configuration, type + 'BgColorTypeLogo')"
                                        v-if="widgetLayout.configuration[pos].configuration[type + 'BgColorTypeLogo']"
                                      ></a-button>
                                    </div>

                                    <a-upload
                                      name="file"
                                      :file-list="[]"
                                      :show-upload-list="false"
                                      :before-upload="e => beforeUpload(e, 50)"
                                      :customRequest="
                                        e =>
                                          customRequest(e, 'logo', url =>
                                            setTargetUrl(widgetLayout.configuration[pos].configuration, type + 'BgColorTypeLogo', url)
                                          )
                                      "
                                    >
                                      <a-button size="small" type="link" icon="upload">上传</a-button>
                                    </a-upload> -->
                                  <div class="img-upload-tip">
                                    支持格式: JPG、PNG、GIF、SVG; 大小限制: 不超过50M; 建议尺寸: 150px * 100px
                                  </div>
                                </a-col>
                                <a-col :span="12"></a-col>
                              </a-row>
                            </template>
                          </a-tab-pane>
                        </a-tabs>
                      </div>
                    </template>
                  </WidgetDesignDrawer>
                </template>
                <ImageLibrary
                  v-model="widgetLayout.configuration.header.configuration.logo"
                  width="100%"
                  :height="100"
                  :limitSize="limitSize"
                  :acceptType="acceptTypes"
                  :acceptTip="acceptTip"
                  :emptyVisible="false"
                />

                <!-- <div
                  class="transparent-bg"
                  style="width: 100%; height: 100px"
                  v-show="widgetLayout.configuration.header.configuration.logo"
                >
                  <img
                    :src="widgetLayout.configuration.header.configuration.logo"
                    v-if="widgetLayout.configuration.header.configuration.logo"
                    class="nav-logo"
                  />
                  <a-button
                    size="small"
                    icon="delete"
                    type="danger"
                    class="delete-img"
                    @click.stop="clearLogo(widgetLayout.configuration.header.configuration, 'logo')"
                    v-if="widgetLayout.configuration.header.configuration.logo"
                  ></a-button>
                </div>
                <a-upload
                  name="file"
                  :file-list="[]"
                  :show-upload-list="false"
                  :before-upload="e => beforeUpload(e, 50)"
                  :customRequest="
                    e => customRequest(e, 'logo', url => setTargetUrl(widgetLayout.configuration.header.configuration, 'logo', url))
                  "
                >
                  <a-button size="small" type="link" icon="upload">上传</a-button>

                </a-upload> -->
                <div class="img-upload-tip">支持格式: JPG、PNG、GIF、SVG; 大小限制: 不超过50M; 建议尺寸: 150px * 100px</div>
              </a-form-model-item>
              <a-form-model-item label="logo位置" v-if="layoutConf.layoutType == 'siderTopMiddleBottom'">
                <a-radio-group size="small" v-model="logoPosition" button-style="solid" @change="e => onChangeLogoPosition(e)">
                  <a-radio-button value="sider">左导航</a-radio-button>
                  <a-radio-button value="header">横幅</a-radio-button>
                  <a-radio-button :value="undefined">按布局自适应</a-radio-button>
                </a-radio-group>
              </a-form-model-item>
              <a-form-model-item label="横幅背景色">
                <a-radio-group
                  size="small"
                  v-model="widgetLayout.configuration.header.configuration.backgroundColorType"
                  button-style="solid"
                  @change="refreshLayoutPreview"
                >
                  <a-radio-button :value="undefined">按布局自适应</a-radio-button>
                  <!-- <a-radio-button value="dark">固定深色</a-radio-button> -->
                  <a-radio-button value="light">固定浅色</a-radio-button>
                </a-radio-group>
              </a-form-model-item>
              <a-form-model-item label="横幅背景图片">
                <ImageLibrary
                  v-model="widgetLayout.configuration.header.configuration.bgImage"
                  width="100%"
                  :height="100"
                  :limitSize="limitSize"
                  :acceptType="acceptTypes"
                  :acceptTip="acceptTip"
                  :emptyVisible="false"
                />
                <!-- <div
                  style="width: 100%; height: 100px"
                  class="transparent-bg"
                  v-show="widgetLayout.configuration.header.configuration.bgImage"
                >
                  <img
                    :src="widgetLayout.configuration.header.configuration.bgImage"
                    v-if="widgetLayout.configuration.header.configuration.bgImage"
                    class="nav-logo transparent-bg"
                  />
                </div>
                <a-upload
                  name="file"
                  :file-list="[]"
                  :show-upload-list="false"
                  :before-upload="e => beforeUpload(e, 50)"
                  :customRequest="
                    e => customRequest(e, 'logo', url => setTargetUrl(widgetLayout.configuration.header.configuration, 'bgImage', url))
                  "
                >
                  <a-button size="small" type="link" icon="upload">上传</a-button>
                  <a-button
                    size="small"
                    icon="delete"
                    type="danger"
                    class="delete-img"
                    @click.stop="clearLogo(widgetLayout.configuration.header.configuration, 'bgImage')"
                    v-if="widgetLayout.configuration.header.configuration.bgImage"
                  ></a-button>
                </a-upload> -->
                <div class="img-upload-tip">支持格式: JPG、PNG、GIF、SVG; 大小限制: 不超过50M; 建议尺寸: 150px * 100px</div>
              </a-form-model-item>

              <a-form-model-item label="徽标风格">
                <a-radio-group size="small" v-model="widgetMenu.configuration.sysMenuBadge.badgeDisplayType" button-style="solid">
                  <a-radio-button value="text">普通文本</a-radio-button>
                  <a-radio-button value="badge">胶囊</a-radio-button>
                  <a-radio-button value="dot">红点</a-radio-button>
                </a-radio-group>
              </a-form-model-item>

              <div
                v-show="widgetMenu.configuration.sysMenuBadge.badgeDisplayType != 'dot'"
                :style="{
                  lineHeight: '30px'
                }"
              >
                <a-form-model-item label="徽标封顶数字">
                  <a-radio-group
                    size="small"
                    v-model="widgetMenu.configuration.sysMenuBadge.badgeOverflowCountShowType"
                    button-style="solid"
                  >
                    <a-radio-button value="systemDefault">系统默认</a-radio-button>
                    <a-radio-button value="limitless">无封顶</a-radio-button>
                    <a-radio-button value="customize">自定义</a-radio-button>
                  </a-radio-group>
                  <a-input-number
                    v-model="widgetMenu.configuration.sysMenuBadge.badgeOverflowCount"
                    :min="1"
                    size="small"
                    v-show="widgetMenu.configuration.sysMenuBadge.badgeOverflowCountShowType == 'customize'"
                  />
                </a-form-model-item>

                <a-form-model-item label="徽标数值为0时">
                  <a-radio-group size="small" v-model="widgetMenu.configuration.sysMenuBadge.badgeShowZero" button-style="solid">
                    <a-radio-button value="systemDefault">系统默认</a-radio-button>
                    <a-radio-button value="yes">显示</a-radio-button>
                    <a-radio-button value="no">不显示</a-radio-button>
                  </a-radio-group>
                </a-form-model-item>
              </div>
            </a-form-model>
          </PerfectScrollbar>
        </a-tab-pane>
      </a-tabs>
    </a-collapse-panel>
    <a-collapse-panel key="2">
      <template slot="header">
        <label class="">产品导航</label>
      </template>
      <a-tabs class="radio-group-style">
        <a-tab-pane key="1" tab="导航项">
          <a-row type="flex" :gutter="8" style="margin-bottom: 12px; margin-right: 20px; flex-wrap: nowrap; align-items: center">
            <a-col flex="124px">
              <WidgetDesignDrawer
                id="sysFuncMenu_2"
                title="添加"
                :designer="designer"
                placement="left"
                :width="drawerWidth"
                :zIndex="drawerZIndex"
              >
                <a-button block @click="resetNewMenu">
                  <Icon type="pticon iconfont icon-ptkj-jiahao"></Icon>
                  添加同级
                </a-button>
                <template slot="content">
                  <MenuConfiguration :widget="widgetMenu" :designer="designer" :menu="newMenu" />
                </template>
                <template slot="footer" slot-scope="drawerScope">
                  <a-button type="primary" @click="onAddNewMenu(widgetMenu.configuration.menus, 1, drawerScope)">保存</a-button>
                </template>
              </WidgetDesignDrawer>
            </a-col>
            <a-col flex="124px">
              <WidgetDesignDrawer
                id="quickAddProdMenu"
                title="快速添加"
                :designer="designer"
                placement="left"
                :width="400"
                :zIndex="drawerZIndex"
                v-model="quickAddDrawerVisible"
              >
                <a-button block>
                  <Icon type="pticon iconfont icon-ptkj-jiahao"></Icon>
                  快速添加
                </a-button>
                <template slot="content">
                  <div class="module-quick-add-sider">
                    <a-alert message="您可以根据产品版本下的模块批量添加导航项" type="info" show-icon style="margin-bottom: 10px" />

                    <PerfectScrollbar style="height: calc(100vh - 335px)">
                      <a-row type="flex" class="res-item-row" v-for="(res, i) in modulesAsMenus" :key="'nav_' + res.id">
                        <a-col flex="auto">
                          <span style="min-width: 20px; display: inline-block">
                            <Icon :type="res.icon" />
                          </span>
                          <a-input size="small" v-model="res.title" style="width: 150px" />
                        </a-col>
                        <a-col flex="100px">
                          <WidgetIconLibModal v-model="res.icon">
                            <a-button type="link" size="small">
                              <Icon type="pticon iconfont icon-ptkj-shezhi" />
                              图标
                            </a-button>
                          </WidgetIconLibModal>
                          <a-checkbox :checked="quickChecked.includes(res.id)" @change="onQuickChecked(res.id)" />
                        </a-col>
                      </a-row>
                    </PerfectScrollbar>
                  </div>
                  <div class="quick-add-type-select" v-show="widgetMenu && widgetMenu.configuration.menus.length > 0">
                    <div style="color: var(--w-text-color-dark); margin-bottom: 8px">当前模块已设置导航项, 请选择</div>
                    <a-row :gutter="12" class="quick-add-type-select-row">
                      <a-col :span="12">
                        <div :class="quickAddTypeSelect == 'add' ? 'selected' : ''" @click="quickAddTypeSelect = 'add'">
                          <span class="icon-div"><Icon type="pticon iconfont icon-luojizujian-xinzeng"></Icon></span>
                          批量追加
                        </div>
                      </a-col>
                      <a-col :span="12">
                        <div :class="quickAddTypeSelect == 'replace' ? 'selected' : ''" @click="quickAddTypeSelect = 'replace'">
                          <span class="icon-div"><Icon type="pticon iconfont icon-oa-qiehuan"></Icon></span>
                          全部替换
                        </div>
                      </a-col>
                    </a-row>
                  </div>
                </template>
                <template slot="footer">
                  <a-checkbox
                    :indeterminate="indeterminate"
                    :checked="checkAll"
                    @change="onCheckAllChange"
                    style="float: left; margin-top: 7px"
                  >
                    全选|已选
                    <span style="color: var(--w-primary-color)">{{ quickChecked.length }}</span>
                    个导航项
                  </a-checkbox>
                  <a-button type="primary" @click="addQuickCheckedModuleAsMenus">添加</a-button>
                </template>
              </WidgetDesignDrawer>
            </a-col>
            <!-- <a-col flex="auto">
              <a-switch v-model="menuSortable" title="开启拖动排序">
                <a-icon slot="checkedChildren" type="swap" style="transform: rotate(-90deg)" />
                <a-icon slot="unCheckedChildren" type="swap" style="transform: rotate(-90deg)" />
              </a-switch>
            </a-col> -->
          </a-row>
          <PerfectScrollbar style="max-height: calc(100vh - 240px); padding-right: 12px">
            <DraggableTreeList
              v-if="widgetMenu != undefined"
              v-model="widgetMenu.configuration.menus"
              childrenField="menus"
              :titleWidth="110"
              :draggable="menuSortable"
              :dragButton="true"
              dragButtonType="text"
              :hoverShowButtons="true"
              :maxLevel="3"
              class="menu-tree"
              :itemClass="getMenuHiddenClass"
              style="
                --w-pt-draggable-tree-list-color-hover: var(--w-primary-color);
                --w-pt-draggable-tree-list-bg-color-hover: var(--w-primary-color-1);
              "
            >
              <template slot="operation" slot-scope="scope">
                <WidgetDesignDrawer
                  v-if="scope.level < 3"
                  :id="'sysFuncMenuAdd_' + scope.item.id"
                  title="添加"
                  :designer="designer"
                  placement="left"
                  :width="drawerWidth"
                  :zIndex="drawerZIndex"
                >
                  <a-button size="small" type="text" @click="resetNewMenu" title="添加">
                    <Icon type="pticon iconfont icon-ptkj-jiahao"></Icon>
                  </a-button>
                  <template slot="content">
                    <MenuConfiguration :widget="widgetMenu" :designer="designer" :menu="newMenu" />
                  </template>
                  <template slot="footer" slot-scope="drawerScope">
                    <a-button type="primary" size="small" @click="onAddNewMenu(scope.item.menus, scope.level + 1, drawerScope)">
                      保存
                    </a-button>
                  </template>
                </WidgetDesignDrawer>
                <WidgetDesignDrawer
                  :id="'sysFuncMenuEdit_' + scope.item.id"
                  title="编辑"
                  :designer="designer"
                  placement="left"
                  :width="drawerWidth"
                  :zIndex="drawerZIndex"
                >
                  <a-button size="small" type="text" title="编辑"><Icon type="pticon iconfont icon-ptkj-bianji"></Icon></a-button>
                  <template slot="content">
                    <a-form-model>
                      <a-form-model-item v-if="scope.item.appId != undefined && moduleIdName[scope.item.appId] != undefined" label="模块">
                        <a-tag color="blue">{{ moduleIdName[scope.item.appId] }}</a-tag>
                      </a-form-model-item>
                      <a-form-model-item v-if="scope.item.index">
                        <template slot="label">
                          <a-button size="small" type="link" icon="home">首页</a-button>
                        </template>
                      </a-form-model-item>
                    </a-form-model>

                    <MenuConfiguration :widget="widgetMenu" :designer="designer" :menu="scope.item" />
                  </template>
                </WidgetDesignDrawer>
                <a-button size="small" type="text" @click.stop="scope.items.splice(scope.index, 1)" title="删除">
                  <Icon type="pticon iconfont icon-ptkj-shanchu"></Icon>
                </a-button>
              </template>
            </DraggableTreeList>
          </PerfectScrollbar>
        </a-tab-pane>
        <a-tab-pane key="2" tab="导航设置">
          <a-form-model :colon="false" class="pt-form" style="padding-right: 12px">
            <a-form-model-item label="默认选中导航">
              <a-tree-select
                v-model="widgetMenu.configuration.defaultSelectedKey"
                show-search
                style="width: 100%"
                :dropdown-style="{ maxHeight: '600px', overflow: 'auto' }"
                :replaceFields="{ value: 'id', key: 'id', children: 'menus' }"
                :treeData="widgetMenu.configuration.menus"
                treeNodeFilterProp="title"
                allow-clear
                tree-default-expand-all
              ></a-tree-select>
            </a-form-model-item>
            <!-- <a-form-model-item label="当默认选中导航不存在时候选中第一个" v-if="widgetMenu.configuration.defaultSelectedKey">
              <a-switch v-model="widgetMenu.configuration.selectFirstIfNotFoundDefaultSelectedKey" />
            </a-form-model-item> -->
            <a-form-model-item label="导航背景色">
              <a-radio-group
                v-model="widgetLayout.configuration.sider.configuration.backgroundColorType"
                button-style="solid"
                size="small"
                @change="refreshLayoutPreview"
              >
                <a-radio-button :value="undefined">按布局自适应</a-radio-button>
                <a-radio-button value="dark">固定深色</a-radio-button>
                <a-radio-button value="light">固定浅色</a-radio-button>
              </a-radio-group>
            </a-form-model-item>
            <a-form-model-item label="徽标风格">
              <a-radio-group size="small" v-model="widgetMenu.configuration.menuBadge.badgeDisplayType" button-style="solid">
                <a-radio-button value="text">普通文本</a-radio-button>
                <a-radio-button value="badge">胶囊</a-radio-button>
                <a-radio-button value="dot">红点</a-radio-button>
              </a-radio-group>
            </a-form-model-item>

            <div
              v-show="widgetMenu.configuration.menuBadge.badgeDisplayType != 'dot'"
              :style="{
                lineHeight: '30px'
              }"
            >
              <a-form-model-item label="徽标封顶数字">
                <a-radio-group size="small" v-model="widgetMenu.configuration.menuBadge.badgeOverflowCountShowType" button-style="solid">
                  <a-radio-button value="systemDefault">系统默认</a-radio-button>
                  <a-radio-button value="limitless">无封顶</a-radio-button>
                  <a-radio-button value="customize">自定义</a-radio-button>
                </a-radio-group>
                <a-input-number
                  v-model="widgetMenu.configuration.menuBadge.badgeOverflowCount"
                  :min="1"
                  size="small"
                  v-show="widgetMenu.configuration.menuBadge.badgeOverflowCountShowType == 'customize'"
                />
              </a-form-model-item>

              <a-form-model-item label="徽标数值为0时">
                <a-radio-group size="small" v-model="widgetMenu.configuration.menuBadge.badgeShowZero" button-style="solid">
                  <a-radio-button value="systemDefault">按系统默认</a-radio-button>
                  <a-radio-button value="yes">显示</a-radio-button>
                  <a-radio-button value="no">不显示</a-radio-button>
                </a-radio-group>
              </a-form-model-item>
            </div>
          </a-form-model>
        </a-tab-pane>
      </a-tabs>
    </a-collapse-panel>
  </a-collapse>
</template>
<style lang="less">
.prod-nav-setting {
  > .ant-collapse-item {
    > .ant-collapse-content {
      > .ant-collapse-content-box {
        padding-right: 0 !important;
      }
    }
  }
  .radio-group-style {
    > .ant-tabs-bar {
      --w-tabs-radio-button-solid-border-radius: 4px;
      margin-right: 20px;
    }
    > .ant-tabs-content {
    }
  }
  .menu-tree {
    .menu-hidden {
      --w-pt-draggable-tree-list-color: var(--w-text-color-lighter);
    }
  }
}
.prod-nav-setting,
.more-logo-set {
  .nav-logo {
    width: 100%;
    height: 100px;
    object-fit: scale-down;
  }
  .transparent-bg {
    position: relative;
    background: url('data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACAAAAAgCAYAAABzenr0AAAABGdBTUEAALGPC/xhBQAAAFpJREFUWAntljEKADAIA23p6v//qQ+wfUEcCu1yriEgp0FHRJSJcnehmmWm1Dv/lO4HIg1AAAKjTqm03ea88zMCCEDgO4HV5bS757f+7wRoAAIQ4B9gByAAgQ3pfiDmXmAeEwAAAABJRU5ErkJggg==')
      0% 0% / 28px;
  }
  .img-upload-tip {
    padding: 12px;
    border-radius: 4px;
    background-color: var(--w-gray-color-2);
    line-height: 20px;
    font-size: var(--w-font-size-sm);
    color: var(--w-text-color-light);
  }
  .delete-img {
    position: absolute;
    right: 0px;
    top: 0px;
    border-radius: 0px;
  }
}

#quickAddProdMenu {
  .module-quick-add-sider {
    .res-item-row {
      padding: 8px 8px 8px 20px;
      cursor: pointer;
      border-radius: 4px;

      .ant-input {
        border-width: 0px;

        &:hover {
          border-right-width: 0px !important;
        }

        &:active,
        &:focus {
          border-width: 1px !important;
        }
      }

      // .res-item-col {
      //   align-items: center;
      //   display: flex;
      //   justify-content: flex-start;
      //   > span {
      //     padding-left: 4px;
      //     text-overflow: ellipsis;
      //     white-space: nowrap;
      //     width: 180px;
      //     overflow: hidden;
      //   }
      // }

      &:hover {
        background-color: var(--w-primary-color-1);
      }
    }

    + .quick-add-type-select {
      padding: 12px 0 0;

      .quick-add-type-select-row {
        > .ant-col > div {
          border-radius: 4px;
          border: 1px solid var(--w-border-color-light);
          height: 56px;
          line-height: 56px;
          width: 100%;
          color: var(--w-text-color-dark);
          font-size: var(--w-font-size-base);
          text-align: center;
          cursor: pointer;

          .icon-div {
            padding: 6px 8px;
            background-color: var(--w-gray-color-2);
            border-radius: 4px;
            font-size: var(--w-font-size-lg);
            color: var(--w-text-color-darker);
            margin-right: 12px;
            > i {
              font-size: var(--w-font-size-lg);
            }
          }

          &:hover {
            border: 1px solid var(--w-primary-color);
          }
          &.selected {
            border: 1px solid var(--w-primary-color);
            .icon-div {
              color: var(--w-primary-color);
              background-color: var(--w-primary-color-1);
            }
          }
        }
      }
    }
  }
}
</style>
<script type="text/babel">
import DraggableTreeList from '@pageAssembly/app/web/widget/commons/draggable-tree-list';
import Modal from '@pageAssembly/app/web/lib/modal.vue';
import WidgetIconLibModal from '@pageAssembly/app/web/lib/widget-icon-lib-modal.vue';
import { generateId, deepClone } from '@framework/vue/utils/util';
import MenuConfiguration from '../../../../widget/widget-menu/configuration/menu-configuration.vue';
import ImageLibrary from '@pageAssembly/app/web/widget/commons/image-library.vue';
import QuillEditor from '@pageAssembly/app/web/lib/quill-editor';
import { debounce } from 'lodash';
export default {
  name: 'ProdIndexNavDesign',
  props: {
    prodVersionUuid: String,
    designer: Object,
    systemInfo: Object
  },
  components: { DraggableTreeList, Modal, MenuConfiguration, QuillEditor, ImageLibrary, WidgetIconLibModal },
  computed: {},
  provide() {
    return {
      subAppIds: this.subAppIds
    };
  },
  data() {
    return {
      logoPosition: undefined,
      modules: [],
      moduleIdName: {},
      modulesAsMenus: [],
      quickChecked: [],
      allCheckKeys: [],
      widgetLayout: undefined,
      widgetMenu: undefined,
      funcMenuSortable: true,
      menuSortable: true,
      moduleMenus: [],
      sysFuncMenuDrawerVisible: false,
      indeterminate: false,
      checkAll: false,
      quickAddTypeSelect: undefined,
      quickAddDrawerVisible: false,
      loaded: false,
      layoutConf: {},
      drawerWidth: 600,
      drawerZIndex: 10,
      newMenu: {
        id: undefined,
        title: undefined,
        icon: undefined,
        hidden: false,
        menus: [],
        level: undefined,
        eventHandler: {}
      },
      moreLogoSetVisible: false,
      subAppIds: [],
      acceptTypes: ['image/gif', 'image/jpeg', 'image/png', 'image/svg+xml'],
      acceptTip: '只允许上传JPG、PNG、GIF、SVG 的图片格式',
      limitSize: 50
    };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {
    this.fetchProdVersionModules();
    let afterFetched = data => {
      if (data.layoutConf) {
        this.layoutConf = JSON.parse(data.layoutConf);
        this.$emit('layoutConfChange', this.layoutConf);
      }
    };
    if (this.systemInfo != undefined) {
      this.fetchSystemPageSetting().then(data => {
        afterFetched.call(this, data);
      });
    } else {
      this.fetchVersionSetting().then(data => {
        afterFetched.call(this, data);
      });
    }
  },
  mounted() {
    if (this.designer.widgets.length) {
      this.widgetMenu = this.findWidget(this.designer.widgets, obj => {
        return obj.wtype == 'WidgetMenu' && obj.main;
      });
      if (this.widgetMenu != undefined) {
        this.fixConfiguration();
      }

      this.widgetLayout = this.findWidget(this.designer.widgets, obj => {
        return obj.wtype == 'WidgetLayout' && obj.main;
      });
      this.loaded = true;
      this.logoPosition = this.widgetLayout.configuration.logoPositionSelfControl
        ? this.widgetLayout.configuration.logoPosition
        : undefined;
      console.log('布局配置', this.widgetLayout);
    }
  },
  methods: {
    fixConfiguration() {
      // 补充增加相关系统性导航
      let sysFunctionMenus = this.widgetMenu.configuration.sysFunctionMenus;
      if (sysFunctionMenus && sysFunctionMenus.length > 0) {
        let hasWorkbenches = false,
          hasOnlineUser = false;
        for (let i = 0, len = sysFunctionMenus.length; i < len; i++) {
          if (sysFunctionMenus[i].code == 'HeaderSwitchWorkbenches') {
            hasWorkbenches = true;
          }
          if (sysFunctionMenus[i].code == 'HeaderUserAvatar') {
            // 删除旧的个性化设置菜单
            let subMenus = sysFunctionMenus[i].menus;
            if (subMenus && subMenus.length > 0) {
              for (let j = 0; j < subMenus.length; j++) {
                if (subMenus[j].code == 'HeaderPersonality') {
                  subMenus.splice(j, 1);
                  break;
                }
              }
            }
          }
          if (sysFunctionMenus[i].code == 'HeaderOnlineUserList') {
            hasOnlineUser = true;
          }
        }
        if (!hasWorkbenches) {
          // 插入工作台功能
          sysFunctionMenus.splice(0, 0, {
            id: `menu-${generateId()}`,
            title: '工作台',
            titleHidden: true,
            code: 'HeaderSwitchWorkbenches',
            level: 1,
            icon: 'appstore',
            hidden: false,
            eventHandler: {}
          });
        }

        if (!hasOnlineUser) {
          sysFunctionMenus.splice(0, 0, {
            id: `menu-${generateId()}`,
            title: '在线人数',
            titleHidden: false,
            code: 'HeaderOnlineUserList',
            level: 1,
            icon: 'iconfont icon-luojizujian-huoquyonghuliebiao',
            hidden: false,
            eventHandler: {}
          });
        }
      }
    },
    fetchSystemPageSetting() {
      return new Promise((resolve, reject) => {
        $axios
          .get(`/proxy/api/system/getTenantSystemPageSetting`, {
            params: { system: this.systemInfo.system, tenant: this.systemInfo.tenant }
          })
          .then(({ data }) => {
            if (data.code == 0) {
              resolve(data.data);
            }
          })
          .catch(error => {});
      });
    },
    fetchVersionSetting() {
      return new Promise((resolve, reject) => {
        $axios
          .get(`/proxy/api/app/prod/version/getSetting`, { params: { versionUuid: this.prodVersionUuid } })
          .then(({ data }) => {
            if (data.code == 0) {
              resolve(data.data);
            }
          })
          .catch(error => {});
      });
    },
    clearLogo(target, propName) {
      target[propName] = undefined;
      if (propName.toLowerCase().indexOf('logo') != -1) {
        this.$set(target, propName + 'AspectRatio', undefined);
      }
      this.refreshLayoutPreview();
    },
    onInputTitle() {
      this.refreshLayoutPreview();
    },
    setTargetUrl(target, propName, url) {
      if (propName.toLowerCase().indexOf('logo') != -1) {
        let img = new Image();
        img.onload = e => {
          this.$set(target, propName, url);
          // 设置logo图片的宽高比，用于实际渲染时候初始计算宽度或者高度
          this.$set(target, propName + 'AspectRatio', parseFloat((img.width / img.height).toFixed(1)));
          this.refreshLayoutPreview();
        };
        img.src = url;
      } else {
        this.$set(target, propName, url);
        this.refreshLayoutPreview();
      }
    },
    customRequest(options, key, afterUpload) {
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
            if (typeof afterUpload == 'function') {
              afterUpload.call(this, `/proxy-repository/repository/file/mongo/download?fileID=${data.data[0].fileID}`);
            }
          }
        });
    },

    beforeUpload(file, limitSize) {
      let isJpgOrPng = ['image/gif', 'image/jpeg', 'image/png', 'image/svg+xml'].includes(file.type);
      console.log(file);
      if (!isJpgOrPng) {
        this.$message.error('只允许上传 jpeg、png 或者 gif 图片格式');
      }
      let limit = true;
      if (limitSize != undefined) {
        limit = file.size / 1024 / 1024 < limitSize;
        if (!limit) {
          this.$message.error(`图片大小应小于 ${limitSize}M`);
        }
      }

      return isJpgOrPng && limit;
    },
    getNewMenu() {
      return {
        id: undefined,
        title: undefined,
        icon: undefined,
        hidden: undefined,
        menus: [],
        level: undefined,
        eventHandler: {},
        titleHidden: undefined
      };
    },
    resetNewMenu(level) {
      this.newMenu = this.getNewMenu();
      if (level == 1) {
        // 主导航的第一级默认隐藏名称
        this.$set(this.newMenu, 'titleHidden', false);
      }
    },
    onAddNewMenu(list, level, e) {
      this.newMenu.id = generateId();
      this.newMenu.level = level;
      list.push(deepClone(this.newMenu));
      this.resetNewMenu();
      this.$emit('close');
      if (e) {
        e.close();
      }
    },

    addQuickCheckedModuleAsMenus() {
      let menus = [],
        _length = this.widgetMenu.configuration.menus.length,
        override = _length == 0 || this.quickAddTypeSelect == 'replace';
      if (this.quickAddTypeSelect == undefined && _length != 0) {
        this.$message.info('请选择添加方式');
        return;
      }
      for (let n of this.modulesAsMenus) {
        let m = null;
        if (this.quickChecked.includes(n.id)) {
          m = deepClone(n);
          m.menus = [];
          m.level = 1;
          menus.push(m);
        }
        if (n.menus != undefined) {
          for (let nn of n.menus) {
            if (this.quickChecked.includes(nn.id)) {
              let _nn = deepClone(nn);
              _nn.level = 2;
              _nn.menus = [];
              if (m == null) {
                menus.push(_nn);
              } else {
                m.menus.push(_nn);
              }
            }
          }
        }
      }
      console.log('当前添加的导航信息: ', menus);
      if (override) {
        this.widgetMenu.configuration.menus.splice(0, this.widgetMenu.configuration.menus.length);
      }
      this.widgetMenu.configuration.menus.push(...menus);
      this.quickAddDrawerVisible = false;
    },
    onCheckAllChange(e) {
      Object.assign(this, {
        indeterminate: false,
        checkAll: e.target.checked
      });
      this.quickChecked = [];
      if (e.target.checked) {
        this.quickChecked = [].concat(this.allCheckKeys);
      }
    },
    onQuickChecked(key) {
      let idx = this.quickChecked.indexOf(key);
      if (idx == -1) {
        this.quickChecked.push(key);
      } else {
        this.quickChecked.splice(idx, 1);
      }
      this.triggerCheckIndeterminate();
    },
    triggerCheckIndeterminate() {
      this.indeterminate = !!this.quickChecked.length && this.quickChecked.length < this.allCheckKeys.length;
      this.checkAll = this.quickChecked.length === this.allCheckKeys.length;
    },
    onAddSysFunctionMenu() {
      this.sysFuncMenuDrawerVisible = true;
    },
    findWidget(obj, filter) {
      if (obj != undefined) {
        if (Array.isArray(obj)) {
          for (let i = 0, len = obj.length; i < len; i++) {
            let _get = this.findWidget(obj[i], filter);
            if (_get) {
              return _get;
            }
          }
        } else if (typeof obj == 'object') {
          if (filter(obj)) {
            return obj;
          } else {
            for (let key in obj) {
              let _get = this.findWidget(obj[key], filter);
              if (_get) {
                return _get;
              }
            }
          }
        }
      }
    },
    onChangeLogoPosition(e) {
      this.widgetLayout.configuration.logoPosition = e.target.value;
      this.widgetLayout.configuration.logoPositionSelfControl = e.target.value !== undefined;
      this.refreshLayoutPreview();
    },
    refreshLayoutPreview: debounce(function () {
      console.log('布局配置', this.widgetLayout);
      this.$emit('navChanged', true);
    }, 600),
    onChangeHeaderTitleType(e) {
      this.refreshLayoutPreview();
    },
    fetchProdVersionModules() {
      $axios
        .get(this.systemInfo ? `/proxy/api/app/module/listModuleUnderSystem` : `/proxy/api/app/prod/version/modules`, {
          params: this.systemInfo
            ? { system: this.systemInfo.system, tenant: this.systemInfo.tenant }
            : { prodVersionUuid: this.prodVersionUuid }
        })
        .then(({ data }) => {
          if (data.code == 0 && data.data) {
            this.modules = data.data;
            this.modulesAsMenus = [
              {
                id: generateId(),
                index: true,
                title: '首页',
                hidden: false,
                eventHandler: {
                  trigger: 'click',
                  actionType: 'widgetEvent',
                  actionTypeName: '组件事件',
                  eventWid: this.widgetLayout.id,
                  eventId: 'restoreLayoutContent'
                },
                icon: 'home'
              }
            ];
            this.allCheckKeys.push(this.modulesAsMenus[0].id);
            for (let d of data.data) {
              let id = generateId();
              this.allCheckKeys.push(id);
              this.subAppIds.push(d.id);
              this.moduleIdName[d.id] = d.name;
              this.modulesAsMenus.push({
                id,
                title: d.name,
                icon: undefined,
                eventHandler: {
                  // 指定模块下的导航页面容器为当前布局内容区
                  containerWid: this.widgetLayout.configuration.content.id,
                  targetPosition: 'widgetLayout'
                },
                appId: d.id,
                menus: []
              });
            }
          }
        })
        .catch(error => {});
    },
    getMenuHiddenClass() {
      let { item, parent } = arguments[0];
      return item.hidden || (parent && parent.hidden) ? 'menu-hidden' : '';
    }
  }

  // watch: {
  //   widgetMenu: {
  //     deep: true,
  //     handler(v, o) {
  //       if (v && o && md5(JSON.stringify(v)) != md5(JSON.stringify(o))) {
  //         this.refreshLayoutPreview();
  //       }
  //     }
  //   }
  // }
};
</script>
