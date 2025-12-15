<template>
  <div>
    <DeviceVisible :widget="widget" />
    <a-form-model-item label="标题" v-if="editRule.title == undefined || editRule.title.hidden !== true">
      <a-input v-model="widget.title">
        <template slot="addonAfter">
          <a-switch size="small" v-model="widget.configuration.enableTitle" title="显示标题" />
          <WI18nInput :widget="widget" :designer="designer" code="title" v-model="widget.title" />
        </template>
      </a-input>
    </a-form-model-item>
    <a-form-model-item label="标题颜色">
      <ColorPicker v-model="widget.configuration.titleColor" width="100%" :allowClear="true" />
    </a-form-model-item>
    <a-form-model-item label="JS模块" v-if="editRule.jsModules == undefined || !editRule.jsModules.hidden">
      <JsModuleSelect v-model="widget.configuration.jsModules" dependencyFilter="WidgetTableDevelopment" />
    </a-form-model-item>
    <template v-if="editRule.rowDataFrom == undefined || editRule.rowDataFrom.hidden !== true">
      <a-form-model-item label="数据来源">
        <a-radio-group v-model="widget.configuration.rowDataFrom" button-style="solid" size="small" @change="onChangeRowDataFrom">
          <a-radio-button value="dataSource">数据仓库</a-radio-button>
          <a-radio-button value="developSource">数据脚本</a-radio-button>
          <a-radio-button value="dataModel">数据模型</a-radio-button>
        </a-radio-group>
      </a-form-model-item>
      <div v-show="widget.configuration.rowDataFrom === 'developSource'">
        <a-form-model-item label="执行方法">
          <JsHookSelect
            :style="{ width: '200px' }"
            :widget="widget"
            :designer="designer"
            v-model="widget.configuration.developSourceMethod"
          />
        </a-form-model-item>
      </div>
    </template>
    <div v-show="widget.configuration.rowDataFrom === 'dataSource'">
      <a-form-model-item label="数据仓库">
        <a-tag color="blue" closable @close="onCloseDataSource" :title="dataSourceName" v-if="widget.configuration.dataSourceId">
          <label
            :style="{
              display: 'inline-block',
              'text-align': 'center',
              width: '100px',
              'text-overflow': 'ellipsis',
              'overflow-x': 'hidden',
              'vertical-align': 'middle'
            }"
          >
            {{ dataSourceName }}
          </label>
        </a-tag>
        <WidgetDesignDrawer :id="'widgetTableDsSet' + widget.id" title="数据仓库" :designer="designer">
          <a-button type="primary" size="small">设置</a-button>
          <template slot="content">
            <DatastoreConfiguration :widget="widget" :designer="designer" ref="datastoreConf" />
          </template>
          <template slot="footer">
            <!-- <WidgetCodeEditor lang="json" title="数据模拟" v-model="widget.configuration.mockDatas">
                <a-button size="small">数据模拟</a-button>
              </WidgetCodeEditor> -->

            <a-button size="small" type="primary" @click.stop="onConfirmDatastoreSetOk">完成配置</a-button>
          </template>
        </WidgetDesignDrawer>
      </a-form-model-item>
    </div>
    <template v-if="widget.configuration.rowDataFrom == 'dataModel'">
      <a-form-model-item label="模型类型" v-if="editRule.dataModelType == undefined || editRule.dataModelType.hidden !== true">
        <a-radio-group v-model="widget.configuration.dataModelType" button-style="solid" size="small" @change="onChangeDataModelType">
          <a-radio-button value="TABLE">存储对象</a-radio-button>
          <a-radio-button value="VIEW">视图对象</a-radio-button>
        </a-radio-group>
      </a-form-model-item>
      <a-form-model-item>
        <template slot="label">
          <span
            style="cursor: pointer"
            :class="widget.configuration.dataModelUuid ? 'ant-btn-link' : ''"
            @click="redirectDataModelDesign(widget.configuration.dataModelUuid)"
            :title="widget.configuration.dataModelUuid ? '打开数据模型' : ''"
          >
            数据模型
            <a-icon type="environment" v-show="widget.configuration.dataModelUuid" style="color: inherit; line-height: 1" />
          </span>
        </template>
        <DataModelSelectModal
          v-model="widget.configuration.dataModelUuid"
          :dtype="widget.configuration.dataModelType"
          :displayModal="true"
          ref="dataModelSelect"
          @change="onDataModelChange"
          @optionsReady="onDataModelOptionsReady"
        />
      </a-form-model-item>
    </template>

    <a-form-model-item v-if="widget.configuration.rowDataFrom == 'dataModel' || widget.configuration.rowDataFrom == 'dataSource'">
      <template slot="label">
        <a-popover title="支持编写动态SQL" placement="left" :mouseEnterDelay="0.5">
          <template slot="content">
            <div>
              <label style="font-weight: bold; line-height: 32px">一、支持使用系统内置变量</label>
              <ol>
                <li v-for="(item, i) in sqlVarOptions" style="margin-bottom: 8px">
                  <a-tag class="primary-color">{{ item.value }}</a-tag>
                  : {{ item.label }}
                </li>
              </ol>
              <p>
                例如: SQL 编写为
                <a-tag>creator = :currentUserId</a-tag>
              </p>
              <template v-if="designer.isDyformDesign">
                <label style="font-weight: bold; line-height: 32px">
                  二、支持通过
                  <a-tag class="primary-color">:FORM_DATA.字段编码</a-tag>
                  使用主表字段变量用于SQL字段比较 (注意：当表单字段数据发生变更后会自动刷新表格)
                </label>
                <p>
                  例如: SQL 编写为
                  <a-tag>create_time > :FORM_DATA.create_time</a-tag>
                </p>
                <label style="font-weight: bold; line-height: 32px">
                  三、支持通过模板标签
                  <a-tag class="primary-color"><% %></a-tag>
                  内使用js语法输出动态SQL
                </label>
                <p style="line-height: 28px">
                  例如: 判断某个字段不为空的情况下, 拼接SQL
                  <br />
                  <a-tag>
                    <label style="color: red"><% if ( FORM_DATA.user_name !=null ) { %></label>
                    creator = :FORM_DATA.user_name
                    <label style="color: red"><% } else { %></label>
                    creator is null
                    <label style="color: red"><% } %></label>
                  </a-tag>
                </p>
              </template>
            </div>
          </template>
          <label>
            默认查询条件
            <Icon type="pticon iconfont icon-ptkj-xinxiwenxintishi" />
          </label>
        </a-popover>
      </template>
      <a-switch v-model="widget.configuration.enableDefaultCondition" />
    </a-form-model-item>
    <a-form-model-item :label="null" v-show="widget.configuration.enableDefaultCondition">
      <a-textarea
        placeholder="默认查询条件"
        v-model="widget.configuration.defaultCondition"
        allowClear
        :autoSize="{ minRows: 3, maxRows: 6 }"
      />
    </a-form-model-item>

    <a-collapse :bordered="false" expandIconPosition="right">
      <a-collapse-panel v-if="designer.terminalType == 'mobile'" key="mobileSetting" header="移动端设置" forceRender>
        <a-form-model-item label="数据展示风格">
          <a-radio-group size="small" v-model="widget.configuration.uniConfiguration.displayStyle" button-style="solid">
            <a-radio-button value="1">列表</a-radio-button>
            <a-radio-button value="2">卡片</a-radio-button>
          </a-radio-group>
        </a-form-model-item>
        <a-form-model-item label="字段定义">
          <WidgetDesignDrawer :id="'columnConfigureSet' + widget.id" title="字段定义" :width="1000" :designer="designer">
            <a-button size="small" title="字段定义">
              <Icon type="pticon iconfont icon-ptkj-shezhi"></Icon>
            </a-button>
            <template slot="content">
              <WidgetUniListViewColumnConfiguration
                :widget="widget"
                :configuration="widget.configuration.uniConfiguration"
                :designer="designer"
                ref="columns"
                :columnIndexOptions="columnIndexOptions"
                :columnTemplateOptions="uniColumnTemplateOptions"
              ></WidgetUniListViewColumnConfiguration>
            </template>
          </WidgetDesignDrawer>
        </a-form-model-item>
        <a-form-model-item>
          <template slot="label">
            <a-tooltip placement="bottomRight">
              <template slot="title">
                <ul style="padding-inline-start: 20px; margin-block-end: 0px">
                  <li>排序字段是字段定义内有排序设置的字段</li>
                </ul>
              </template>
              显示排序
              <Icon type="pticon iconfont icon-ptkj-xinxiwenxintishi" />
            </a-tooltip>
          </template>
          <a-switch v-model="widget.configuration.uniConfiguration.showSortOrder" />
        </a-form-model-item>
        <a-form-model-item label="多选">
          <a-switch
            :checked="widget.configuration.rowSelectType == 'checkbox'"
            @change="
              checked => {
                widget.configuration.rowSelectType = checked ? 'checkbox' : 'no';
              }
            "
          />
        </a-form-model-item>
        <a-form-model-item label="行按钮显示">
          <a-radio-group v-model="widget.configuration.uniConfiguration.rowButtonPosition" button-style="solid" size="small">
            <a-radio-button :value="undefined">行下</a-radio-button>
            <a-radio-button value="swipe">左滑</a-radio-button>
          </a-radio-group>
        </a-form-model-item>
        <a-form-model-item v-if="!widget.configuration.uniConfiguration.rowButtonPosition">
          <template slot="label">
            <a-tooltip placement="bottomRight">
              <template slot="title">
                <ul style="padding-inline-start: 20px; margin-block-end: 0px">
                  <li>行下按钮超过3个按钮，两个显示，其余放在【更多】按钮里</li>
                </ul>
              </template>
              行下【更多】按钮类型
              <Icon type="pticon iconfont icon-ptkj-xinxiwenxintishi" />
            </a-tooltip>
          </template>
          <a-select
            :options="buttonTypeOptions"
            v-model="widget.configuration.uniConfiguration.rowBottomMoreButtonType"
            :style="{ width: '100%' }"
          ></a-select>
        </a-form-model-item>
      </a-collapse-panel>
      <template v-else>
        <a-collapse-panel key="columnSetting" header="列定义" forceRender>
          <TableColumnsConfiguration :widget="widget" :designer="designer" :columnIndexOptions="columnIndexOptions" />
          <a-form-model-item label="选择列">
            <a-switch
              :checked="widget.configuration.rowSelectType != 'no'"
              @change="
                checked => {
                  widget.configuration.rowSelectType = checked ? 'checkbox' : 'no';
                }
              "
            />
          </a-form-model-item>
          <a-form-model-item label="选择模式" v-show="widget.configuration.rowSelectType != 'no'">
            <a-radio-group v-model="widget.configuration.rowSelectType" button-style="solid" size="small">
              <a-radio-button value="checkbox">多选</a-radio-button>
              <a-radio-button value="radio">单选</a-radio-button>
            </a-radio-group>
          </a-form-model-item>
          <a-form-model-item label="翻页保留已选" v-show="widget.configuration.rowSelectType != 'no'">
            <a-switch v-model="widget.configuration.notCancelSelectedAfterPage" />
          </a-form-model-item>
          <a-form-model-item label="行单击选中" v-show="widget.configuration.rowSelectType != 'no'">
            <a-switch v-model="widget.configuration.clickRowSelect" />
          </a-form-model-item>

          <a-form-model-item label="序号列">
            <a-switch v-model="widget.configuration.addSerialNumber" />
          </a-form-model-item>
          <a-form-model-item label="序号规则" v-show="widget.configuration.addSerialNumber">
            <a-switch
              v-model="widget.configuration.serialNumberPageIncrease"
              v-show="widget.configuration.addSerialNumber"
              checked-children="按页递增"
              un-checked-children="按页递增"
            />
          </a-form-model-item>
          <a-form-model-item>
            <template slot="label">
              <a-tooltip placement="bottomRight">
                <template slot="title">
                  <ul style="padding-inline-start: 20px; margin-block-end: 0px">
                    <li>若列头与内容不对齐或出现列重复，请指定固定列的宽度</li>
                    <li>如果指定不生效或出现白色垂直空隙，请尝试建议留一非固定列不设宽度以适应弹性布局</li>
                    <li>开启冻结列，默认冻结操作列</li>
                    <li>冻结后n列时，n不包含操作列</li>
                    <li>冻结前n列时，n不包含序号列</li>
                  </ul>
                </template>
                列冻结
                <Icon type="pticon iconfont icon-ptkj-xinxiwenxintishi" />
              </a-tooltip>
            </template>
            <a-switch v-model="widget.configuration.freezeColumn" style="margin-right: 5px" />
            <a-space v-show="widget.configuration.freezeColumn">
              <span>
                前
                <a-input-number
                  size="small"
                  :style="{ width: '50px' }"
                  v-model="widget.configuration.leftFreezeColNum"
                  :min="0"
                  :precision="0"
                  placeholder="0"
                />
              </span>
              <span>
                后
                <a-input-number
                  size="small"
                  :style="{ width: '50px' }"
                  v-model="widget.configuration.rightFreezeColNum"
                  :min="0"
                  :precision="0"
                  placeholder="0"
                />
              </span>
            </a-space>
          </a-form-model-item>
          <a-form-model-item label="自定义表格">
            <a-switch v-model="widget.configuration.enableCustomTable" />
          </a-form-model-item>

          <a-form-model-item label="列边框">
            <a-switch v-model="widget.configuration.bordered" />
          </a-form-model-item>
          <a-form-model-item label="隐藏标题">
            <a-switch v-model="widget.configuration.columnTitleHidden" />
          </a-form-model-item>
          <a-form-model-item v-if="!widget.configuration.columnTitleHidden">
            <template slot="label">
              <a-tooltip placement="right">
                <template slot="title">
                  <span>拖动列需要设置宽度</span>
                </template>
                拖动列宽
                <Icon type="pticon iconfont icon-ptkj-xinxiwenxintishi" />
              </a-tooltip>
            </template>
            <a-switch v-model="widget.configuration.colWidthDrag" @change="onChangeColDragEnable" />
          </a-form-model-item>
          <!-- <a-form-model-item>
          <template slot="label">
            <a-tooltip placement="right">
              <template slot="title">
                <span>定义表格操作列的宽度, 放空则自适应</span>
              </template>
              操作列宽
              <Icon type="pticon iconfont icon-ptkj-xinxiwenxintishi"  />
            </a-tooltip>
          </template>
          <a-input-number :min="75" v-model="widget.configuration.operationColWidth" />
        </a-form-model-item> -->
        </a-collapse-panel>
        <a-collapse-panel key="visibleSetting" header="显示设置">
          <a-form-model-item label="表头插入内容" v-if="widget.configuration.supportsBeforeTableHeaderWidget !== false">
            <a-switch v-model="widget.configuration.enableBeforeTableHeaderWidget" @change="onChangeEnableBeforeTableHeaderWidget" />
          </a-form-model-item>
          <DefaultVisibleConfiguration :designer="designer" :configuration="widget.configuration" :widget="widget" :hasDyformField="true" />
          <ColumnGroupConfiguration
            :designer="designer"
            :configuration="widget.configuration"
            :widget="widget"
            :columnIndexOptions="columnIndexOptions"
          ></ColumnGroupConfiguration>
          <ColumnMergeCellConfiguration
            :designer="designer"
            :configuration="widget.configuration"
            :widget="widget"
            :columnIndexOptions="columnIndexOptions"
          ></ColumnMergeCellConfiguration>
          <a-form-model-item>
            <template slot="label">
              滚动高度
              <a-checkbox v-model="widget.configuration.enableScrollY" />
            </template>
            <a-input-number v-model="widget.configuration.scrollY" v-show="widget.configuration.enableScrollY" />
          </a-form-model-item>
          <a-form-model-item>
            <template slot="label">表格行内容区最小高度</template>
            <a-input-number v-model="widget.configuration.tbodyMinHeight" :min="0" />
          </a-form-model-item>
          <a-form-model-item>
            <template slot="label">
              表格行条纹效果
              <a-checkbox v-model="widget.configuration.rowStyle.enable" />
            </template>
            <WidgetDesignDrawer
              v-if="widget.configuration.rowStyle.enable"
              :id="'widgetTableRowStyleSet' + widget.id"
              title="表格行条纹效果"
              :designer="designer"
            >
              <a-button type="link" size="small">
                <Icon type="pticon iconfont icon-ptkj-shezhi"></Icon>
                设置
              </a-button>
              <template slot="content">
                <RowStyleConfiguration :widget="widget" :designer="designer" />
              </template>
            </WidgetDesignDrawer>
          </a-form-model-item>
          <RowStateRenderConfiguration
            :designer="designer"
            :configuration="widget.configuration"
            :widget="widget"
            :columnIndexOptions="columnIndexOptions"
          ></RowStateRenderConfiguration>
          <a-form-model-item>
            <template slot="label">
              <a-tooltip placement="bottomRight">
                <template slot="title">选择父级容器时，父级容器需要设置高度，目前只支持卡片</template>
                计算高度相对容器
                <Icon type="pticon iconfont icon-ptkj-xinxiwenxintishi" />
              </a-tooltip>
            </template>
            <a-radio-group v-model="widget.configuration.scrollCalculateContainer" button-style="solid" size="small">
              <a-radio-button value="viewport">可视窗口</a-radio-button>
              <a-radio-button value="parent">父级容器</a-radio-button>
            </a-radio-group>
          </a-form-model-item>
          <a-form-model-item>
            <template slot="label">
              <a-tooltip placement="bottomRight">
                <template slot="title">
                  <ul style="padding-inline-start: 20px; margin-block-end: 0px">
                    <li>只对表格视图生效</li>
                    <li>若列头与内容不对齐或出现列重复，请指定固定列的宽度</li>
                    <li>如果指定不生效或出现白色垂直空隙，请尝试建议留一固定列不设宽度以适应弹性布局</li>
                  </ul>
                </template>
                固定表格头部
                <Icon type="pticon iconfont icon-ptkj-xinxiwenxintishi" />
              </a-tooltip>
            </template>
            <a-switch v-model="widget.configuration.fixedTableHeader" />
          </a-form-model-item>
          <a-form-model-item label="固定分页" v-if="!widget.configuration.freezeColumn">
            <a-switch v-model="widget.configuration.fixedPagination" />
          </a-form-model-item>
        </a-collapse-panel>
      </template>
      <a-collapse-panel key="searchSetting" header="查询设置">
        <a-form-model-item label="查询方式">
          <a-radio-group v-model="widget.configuration.search.type" button-style="solid" size="small">
            <a-radio-button value="keywordAdvanceSearch">关键字/高级搜索</a-radio-button>
            <a-radio-button value="attrSearch">属性搜索</a-radio-button>
          </a-radio-group>
        </a-form-model-item>

        <div v-show="widget.configuration.search.type == 'keywordAdvanceSearch'">
          <KeywordSearchConfiguration :widget="widget" ref="keywordSearch" :columnIndexOptions="columnIndexOptions" :designer="designer" />
          <AdvanceSearchConfiguration :widget="widget" ref="advanceSearch" :columnIndexOptions="columnIndexOptions" :designer="designer" />
        </div>
        <template v-if="designer.terminalType !== 'mobile'">
          <a-form-model-item>
            <template slot="label">
              <a-popover placement="left" title="说明">
                <template slot="content">
                  <p>支持自定义表格头部搜索区域</p>
                  <p>
                    <a-button size="small" type="link" @click="e => clickCopySnippetCode(e, 'tableSearchTemplate')">复制代码样例</a-button>
                  </p>
                </template>
                <label>
                  查询模板
                  <Icon type="pticon iconfont icon-ptkj-xinxiwenxintishi" />
                </label>
              </a-popover>
              <a-checkbox v-model="widget.configuration.search.enableTemplate" />
            </template>
            <VueTemplateSelect
              v-if="widget.configuration.search.enableTemplate"
              v-model="widget.configuration.search.searchTemplateName"
              :style="{ width: '100%' }"
            />
          </a-form-model-item>
          <a-form-model-item label="模板位置">
            <a-radio-group v-model="widget.configuration.search.searchTemplatePosition" button-style="solid" size="small">
              <a-radio-button value="top">居上</a-radio-button>
              <a-radio-button value="bottom">居下</a-radio-button>
            </a-radio-group>
          </a-form-model-item>
        </template>
      </a-collapse-panel>
      <a-collapse-panel key="operationSetting" header="操作设置" forceRender>
        <TableRowClickConfiguration :widget="widget" :designer="designer" />
        <TableButtonsConfiguration
          :widget="widget"
          :designer="designer"
          :button="widget.configuration.headerButton"
          title="表头按钮"
          :editRule="editRule"
        />
        <TableButtonsConfiguration
          :widget="widget"
          :designer="designer"
          :button="widget.configuration.rowButton"
          :editRule="editRule"
          position="row"
          title="行按钮"
        />
        <template v-if="designer.terminalType !== 'mobile'">
          <a-form-model-item label="启用卡片列表">
            <a-select
              placeholder="列数"
              :style="{ width: '90px', marginRight: '10px' }"
              v-model="widget.configuration.cardColumnNum"
              v-show="widget.configuration.displayCardList"
            >
              <a-select-option v-for="i in [1, 2, 3, 4, 6, 8]" :value="i" :key="'cardColumnNum_' + i">{{ i }}列</a-select-option>
            </a-select>
            <a-switch v-model="widget.configuration.displayCardList" />
          </a-form-model-item>
          <a-form-model-item label="默认视图" v-show="widget.configuration.displayCardList">
            <a-radio-group v-model="widget.configuration.defaultDisplayState" button-style="solid" size="small">
              <a-radio-button value="table">表格</a-radio-button>
              <a-radio-button value="cardList">卡片</a-radio-button>
              <!-- <a-radio-button value="treeTable">树形</a-radio-button> -->
            </a-radio-group>
          </a-form-model-item>
          <a-form-model-item v-show="widget.configuration.displayCardList">
            <template slot="label">
              卡片内容渲染模板
              <a-checkbox v-model="widget.configuration.useCardTemplate" @change="onChangeUseCardTemplate" />
            </template>
            <WidgetDesignDrawer
              v-if="widget.configuration.useCardTemplate"
              :id="'widgetTableCardItemTemplateSet' + widget.id"
              title="卡片模板"
              :designer="designer"
            >
              <a-button type="primary" size="small">设置</a-button>
              <template slot="content">
                <div>
                  <a-alert message="模板支持以下属性、具名插槽" banner type="info">
                    <template slot="description">
                      <a-row type="flex">
                        <a-col flex="130px">row :</a-col>
                        <a-col flex="auto">当前行数据</a-col>
                      </a-row>
                      <a-row type="flex">
                        <a-col flex="130px">rowIndex :</a-col>
                        <a-col flex="auto">当前行数据</a-col>
                      </a-row>
                      <a-row type="flex" style="flex-wrap: nowrap">
                        <a-col flex="130px">actions :</a-col>
                        <a-col flex="auto">
                          操作按钮插槽名, 通过
                          <a-tag>&lt;slot name = "actions" /&gt;</a-tag>
                          语法渲染操作按钮
                        </a-col>
                      </a-row>
                      <a-row type="flex" style="flex-wrap: nowrap">
                        <a-col flex="130px">selection :</a-col>
                        <a-col flex="auto">
                          行多选框插槽名, 通过
                          <a-tag>&lt;slot name = "selection" /&gt;</a-tag>
                          语法渲染多选框
                        </a-col>
                      </a-row>
                      <a-row type="flex" style="flex-wrap: nowrap">
                        <a-col flex="130px">列字段渲染插槽 :</a-col>
                        <a-col flex="auto">
                          仅对于列的渲染采用渲染器渲染的情况下, 可以通过
                          <a-tag>字段+Slot</a-tag>
                          的具名插槽来渲染, 例如字段 name 在列定义配置了渲染器, 则通过
                          <a-tag>&lt;slot name = "nameSlot" /&gt;</a-tag>
                          语法进行渲染
                        </a-col>
                      </a-row>
                    </template>
                  </a-alert>

                  <a-collapse :defaultActiveKey="['dataCardRender']" :bordered="false" style="background: #fff" expandIconPosition="right">
                    <a-collapse-panel header="数据卡片渲染" key="dataCardRender">
                      <div style="padding: 0px 12px">
                        <TemplateSelect :options="widget.configuration.cardTemplateConfig" />
                      </div>
                    </a-collapse-panel>
                    <a-collapse-panel key="prefixCardRender">
                      <template slot="header">
                        前置卡片渲染
                        <a-checkbox v-model="widget.configuration.prefixCardTemplateConfig.enable" @click.stop="() => {}" />
                      </template>
                      <div style="padding: 0px 12px">
                        <TemplateSelect :options="widget.configuration.prefixCardTemplateConfig" />
                      </div>
                    </a-collapse-panel>
                    <a-collapse-panel key="suffixCardRender">
                      <template slot="header">
                        后置卡片渲染
                        <a-checkbox v-model="widget.configuration.suffixCardTemplateConfig.enable" @click.stop="() => {}" />
                      </template>
                      <div style="padding: 0px 12px">
                        <TemplateSelect :options="widget.configuration.suffixCardTemplateConfig" />
                      </div>
                    </a-collapse-panel>
                  </a-collapse>
                </div>
              </template>
            </WidgetDesignDrawer>
          </a-form-model-item>
          <a-form-model-item label="允许切换视图" v-show="widget.configuration.displayCardList">
            <a-switch v-model="widget.configuration.toggleDisplay" />
          </a-form-model-item>
        </template>
      </a-collapse-panel>
      <a-collapse-panel key="paginationSetting" header="分页设置" v-if="designer.terminalType !== 'mobile'">
        <a-form-model-item :label="null">
          <a-radio-group v-model="widget.configuration.pagination.type" button-style="solid" size="small">
            <a-radio-button value="default">默认分页</a-radio-button>
            <a-radio-button value="waterfall">瀑布式分页</a-radio-button>
            <a-radio-button value="no">不分页</a-radio-button>
          </a-radio-group>
        </a-form-model-item>
        <a-form-model-item v-if="widget.configuration.pagination.type == 'no'">
          <template slot="label">
            <a-tooltip title="为空时候显示全部数据" placement="left" :mouseEnterDelay="0.5">
              <label>
                只显示前n条数据
                <Icon type="pticon iconfont icon-ptkj-xinxiwenxintishi" />
              </label>
            </a-tooltip>

            <label style="color: #999"></label>
          </template>
          <a-input-number v-model="widget.configuration.pagination.onlyFetchCount" :min="1" />
        </a-form-model-item>

        <a-form-model-item label="默认每页条数" v-if="widget.configuration.pagination.type != 'no'">
          <a-input-number v-model="widget.configuration.pagination.pageSize" :min="1" />
        </a-form-model-item>
        <div v-show="widget.configuration.pagination.type === 'default'">
          <a-form-model-item label="显示总数/页数">
            <a-switch v-model="widget.configuration.pagination.showTotalPage" />
          </a-form-model-item>
          <a-form-model-item label="显示切换分页数">
            <a-switch v-model="widget.configuration.pagination.showSizeChanger" />
          </a-form-model-item>
          <a-form-model-item :label="null" v-show="widget.configuration.pagination.showSizeChanger">
            <a-select
              :options="vPageSizeOptions"
              :style="{ width: '100%', display: 'block' }"
              mode="multiple"
              v-model="widget.configuration.pagination.pageSizeOptions"
              @change="onPageSizeOptionsChange"
            ></a-select>
          </a-form-model-item>
          <a-form-model-item label="显示跳页">
            <a-switch v-model="widget.configuration.pagination.showQuickJumper" />
          </a-form-model-item>
          <a-form-model-item label="页码为1隐藏分页器">
            <a-switch v-model="widget.configuration.pagination.hideOnSinglePage" />
          </a-form-model-item>
          <a-form-model-item label="显示更少的页码">
            <a-switch v-model="widget.configuration.pagination.showLessItems" />
          </a-form-model-item>
        </div>
      </a-collapse-panel>
      <a-collapse-panel key="sortSetting" header="默认排序">
        <TableSortConfiguration :widget="widget" :columnIndexOptions="columnIndexOptions" />
      </a-collapse-panel>
      <a-collapse-panel key="exportSetting" header="导出设置" :forceRender="true" v-if="designer.terminalType !== 'mobile'">
        <ExportConfiguration :widget="widget" :designer="designer" />
      </a-collapse-panel>
      <a-collapse-panel key="importSetting" header="导入设置" :forceRender="true" v-if="designer.terminalType !== 'mobile'">
        <ImportConfiguration :widget="widget" :designer="designer" />
      </a-collapse-panel>
      <slot></slot>
    </a-collapse>
  </div>
</template>
<script type="text/babel">
import { generateId, copyToClipboard } from '@framework/vue/utils/util';
import ColumnConfiguration from './column-configuration.vue';
import ColumnGroupConfiguration from './column-group-configuration.vue';
import ColumnMergeCellConfiguration from './column-merge-cell-configuration.vue';
import RowStateRenderConfiguration from './row-state-render-configuration.vue';
import KeywordSearchConfiguration from './keyword-search-configuration.vue';
import AdvanceSearchConfiguration from './advance-search-configuration.vue';
import DatastoreConfiguration from './datastore-configuration.vue';
import TableColumnsConfiguration from './table-columns-configuration.vue';
import TableButtonsConfiguration from './table-buttons-configuration.vue';
import TableRowClickConfiguration from './table-row-click-configuration.vue';
import TableSortConfiguration from './table-sort-configuration.vue';
import draggable from '@framework/vue/designer/draggable';
import ImportConfiguration from './import-configuration.vue';
import ExportConfiguration from './export-configuration.vue';
import RowStyleConfiguration from './row-style-configuration.vue';
import WidgetUniListViewColumnConfiguration from '../../mobile/widget-uni-list-view/configuration/column-configuration.vue';
import { buttonTypeOptions } from '../../commons/constant';

export default {
  name: 'TableBasicConfiguration',
  mixins: [draggable],
  props: {
    widget: Object,
    designer: Object,
    editRule: {
      type: Object,
      default: function () {
        return {};
      }
    }
  },
  data() {
    let uniDefaultTemplateOptions = [
      { label: '标题', value: 'title' },
      { label: '小标题', value: 'subtitle' },
      { label: '备注', value: 'note' },
      { label: '右侧文本', value: 'rightText' }
    ];
    return {
      dataSourceOptions: [],
      pageSizeOptions: [],
      columnIndexOptions: [],
      codeSnippets: [],
      columnHistory: {},
      history: {},
      columnIndexOptionHistory: {},
      lastRowDataFrom: this.widget.configuration != undefined ? this.widget.configuration.rowDataFrom : undefined,
      sqlVarOptions: [
        { label: '当前用户名', value: ':currentUserName' },
        { label: '当前用户登录名', value: ':currentLoginName' },
        { label: '当前用户ID', value: ':currentUserId' },
        { label: '当前系统', value: ':currentSystem' },
        { label: '当前用户主部门ID', value: ':currentUserDepartmentId' },
        { label: '当前用户主部门名称', value: ':currentUserDepartmentName' },
        { label: '当前用户归属单位ID', value: ':currentUserUnitId' },
        { label: '系统当前时间', value: ':sysdate' }
      ],
      uniColumnTemplateOptions: undefined,
      uniDefaultTemplateOptions,
      buttonTypeOptions
    };
  },

  beforeCreate() {},
  components: {
    TableColumnsConfiguration,
    DatastoreConfiguration,
    ColumnConfiguration,
    ColumnGroupConfiguration,
    ColumnMergeCellConfiguration,
    RowStateRenderConfiguration,
    KeywordSearchConfiguration,
    AdvanceSearchConfiguration,
    TableButtonsConfiguration,
    TableRowClickConfiguration,
    TableSortConfiguration,
    ImportConfiguration,
    ExportConfiguration,
    RowStyleConfiguration,
    WidgetUniListViewColumnConfiguration
  },
  provide() {
    return {
      columnIndexOptions: this.columnIndexOptions
    };
  },
  computed: {
    vPageSizeOptions() {
      let sizes = ['5', '10', '15', '20', '50', '100', '200', '500', '1000', '5000'];
      let options = [];
      for (let i = 0, len = sizes.length; i < len; i++) {
        options.push({
          label: sizes[i],
          value: sizes[i]
        });
      }
      return options;
    },
    dataSourceName() {
      if (this.widget.configuration.dataSourceId) {
        let dataSource = this.dataSourceOptions.find(item => item.id == this.widget.configuration.dataSourceId);
        if (dataSource) {
          this.widget.configuration.dataSourceName = dataSource.text;
          return dataSource.text;
        }
      }
      return this.widget.configuration.dataSourceName;
    }
  },
  created() {
    // 代码片段注入:
    this.codeSnippets = [{ name: '重新刷新表格', content: 'this.refetch();', tabTrigger: 'fetch', trigger: 'fetch|刷新|refresh' }];
    if (this.widget.configuration.search.searchTemplateName == undefined) {
      this.$set(this.widget.configuration.search, 'searchTemplateName', undefined);
      this.$set(this.widget.configuration.search, 'searchTemplatePosition', 'top');
    }

    if (this.widget.configuration.rowStyle == undefined) {
      this.$set(this.widget.configuration, 'rowStyle', {
        enable: false,
        bgStyleType: 'default',
        backgroundColor: undefined,
        backgroundColorType: 'value'
      });
    }

    if (this.widget.configuration.suffixCardTemplateConfig == undefined) {
      this.$set(this.widget.configuration, 'suffixCardTemplateConfig', { enable: false });
      this.$set(this.widget.configuration, 'prefixCardTemplateConfig', { enable: false });
    }
    if (this.widget.configuration.scrollCalculateContainer === undefined) {
      this.$set(this.widget.configuration, 'scrollCalculateContainer', 'viewport');
    }

    if (!this.widget.configuration.uniConfiguration.hasOwnProperty('templateProperties')) {
      let showColums = this.widget.configuration.columns.filter(col => !col.hidden);
      let uniDefaultTemplateOptions = [
        { label: '标题', value: 'title' },
        { label: '小标题', value: 'subtitle' },
        { label: '备注', value: 'note' },
        { label: '右侧文本', value: 'rightText' }
      ];
      let templateProperties = [];
      if (showColums.length > 0) {
        for (let i = 0; i < showColums.length; i++) {
          let col = showColums[i];
          let templateOption = uniDefaultTemplateOptions[i];
          if (templateOption) {
            templateProperties.push({
              uuid: generateId(),
              title: col.title,
              name: templateOption.value,
              mapColumn: col.dataIndex,
              mapColumnName: col.title,
              renderer: {},
              sortOrder: ''
            });
          } else {
            break;
          }
        }
      } else {
        for (let i = 0; i < uniDefaultTemplateOptions.length; i++) {
          let item = uniDefaultTemplateOptions[i];
          // 字段定义默认先添加需要定义的字段
          templateProperties.push({
            uuid: generateId(),
            title: item.label,
            name: item.value,
            mapColumn: '',
            mapColumnName: '',
            renderer: {},
            sortOrder: ''
          });
        }
      }
      this.$set(this.widget.configuration.uniConfiguration, 'templateProperties', templateProperties);
    }

    if (!this.widget.configuration.hasOwnProperty('isColumnsGroup')) {
      this.$set(this.widget.configuration, 'isColumnsGroup', false);
      this.$set(this.widget.configuration, 'columnsGroupNodes', []);
    }
    if (!this.widget.configuration.hasOwnProperty('mergeCell')) {
      this.$set(this.widget.configuration, 'mergeCell', {
        isMergeCell: false, // 启用行合并
        mergeCellMode: 'sort', //合并模式
        mergeCellType: 'auto', //合并方式
        mergeCellCols: [],
        mergeIfNull: false,
        isRowMergeCell: false, // 启用列合并
        rowMergeType: 'default',
        rowMergeData: [], // 行合并数据
        rowMergeFunction: undefined
      });
    }
    if (!this.widget.configuration.hasOwnProperty('renderRowByState')) {
      this.$set(this.widget.configuration, 'renderRowByState', {
        enable: false,
        type: 'default', //渲染方式
        stateData: [], //状态值及其对应的渲染样式
        function: undefined
      });
    }
  },
  methods: {
    onChangeUseCardTemplate() {
      if (this.widget.configuration.useCardTemplate && this.widget.configuration.cardTemplateConfig == undefined) {
        this.$set(this.widget.configuration, 'cardTemplateConfig', {});
      }
    },
    redirectDataModelDesign(uuid) {
      if (uuid) {
        window.open(`/data-model-design/index?uuid=${uuid}`, '_blank');
      }
    },
    onChangeRowDataFrom() {
      // 变更前，记录上一次类型对应的列定义
      this.columnHistory[this.lastRowDataFrom] = JSON.stringify(this.widget.configuration.columns);
      this.columnIndexOptionHistory[this.lastRowDataFrom] = JSON.stringify(this.columnIndexOptions);
      this.history[this.lastRowDataFrom] = JSON.stringify(this.widget.configuration);
      // 获取对应类型的上一次列定义
      if (this.columnHistory[this.widget.configuration.rowDataFrom] != undefined) {
        this.widget.configuration.columns.splice(0, this.widget.configuration.columns.length);
        this.columnIndexOptions.splice(0, this.columnIndexOptions.length);
        this.widget.configuration.columns.push(...JSON.parse(this.columnHistory[this.widget.configuration.rowDataFrom]));
        this.columnIndexOptions.push(...JSON.parse(this.columnIndexOptionHistory[this.widget.configuration.rowDataFrom]));
        this.$set(this.widget.configuration, 'search', JSON.parse(this.history[this.widget.configuration.rowDataFrom]).search);
        this.$set(this.widget.configuration, 'defaultSort', JSON.parse(this.history[this.widget.configuration.rowDataFrom]).defaultSort);
      } else {
        // 需要清空跟列有关的配置
        this.clearColumnInfos();
      }
      this.lastRowDataFrom = this.widget.configuration.rowDataFrom;
    },
    clearColumnInfos() {
      this.widget.configuration.columns.splice(0, this.widget.configuration.columns.length);
      this.columnIndexOptions.splice(0, this.columnIndexOptions.length);
      let search = this.widget.configuration.search;
      search.keywordSearchColumns.splice(0, search.keywordSearchColumns.length);
      search.columnSearchGroup.splice(0, search.columnSearchGroup.length);
      search.columnAdvanceSearchGroup.splice(0, search.columnAdvanceSearchGroup.length);
      this.widget.configuration.defaultSort.splice(0, this.widget.configuration.defaultSort.length);
    },
    onChangeEnableBeforeTableHeaderWidget() {
      if (this.widget.configuration.enableBeforeTableHeaderWidget && this.widget.configuration.beforeTableHeaderWidgets == undefined) {
        this.$set(this.widget.configuration, 'beforeTableHeaderWidgets', []);
      }
    },
    onChangeColDragEnable() {
      if (this.widget.configuration.colWidthDrag) {
        // 允许列拖动的前提是：要设置列宽度
        let visibleCnt = 0,
          unsetWidthCnt = 0;
        for (let i = 0, len = this.widget.configuration.columns.length; i < len; i++) {
          let col = this.widget.configuration.columns[i];
          if (!col.hidden) {
            visibleCnt++;
            unsetWidthCnt += col.width != undefined ? 1 : 0;
          }
        }

        if (visibleCnt > 1 && unsetWidthCnt == 0) {
          this.widget.configuration.colWidthDrag = false;
          this.$message.error('请设置可见拖动列的宽度');
        }
      }
    },
    onCloseDataSource() {
      this.widget.configuration.dataSourceId = undefined;
      this.clearColumnInfos();
      this.$refs.datastoreConf.reset();
    },
    onConfirmDatastoreSetOk() {
      let result = this.$refs.datastoreConf.onConfirmOk();
      this.columnIndexOptions.splice(0, this.columnIndexOptions.length);
      this.columnIndexOptions.push(...result.columnIndexOptions);
    },
    fetchDataSourceOptions(value) {
      let _this = this;
      this.$axios
        .post('/common/select2/query', {
          serviceName: 'viewComponentService',
          queryMethod: 'loadSelectData'
        })
        .then(({ data }) => {
          if (data.results) {
            _this.dataSourceOptions = data.results;
          }
        });
    },

    filterOption(input, option) {
      return option.componentOptions.children[0].text.toLowerCase().indexOf(input.toLowerCase()) >= 0;
    },

    onChange(checked) {
      this.widget.configuration.bordered = checked;
    },

    getWidgetDefinitionElements(wgt, EWidgets) {
      let wgtDefinitionElements = [
        {
          wtype: wgt.wtype,
          title: wgt.title || wgt.name,
          id: wgt.id,
          definitionJson: JSON.stringify(wgt)
        }
      ];

      return wgtDefinitionElements;
    },

    fetchColumns() {
      $axios
        .post('/json/data/services', {
          serviceName: 'viewComponentService',
          methodName: 'getColumnsById',
          args: JSON.stringify([this.widget.configuration.dataSourceId])
        })
        .then(({ data }) => {
          if (data.code == 0 && data.data) {
            this.columnIndexOptions.splice(0, this.columnIndexOptions.length);
            for (let i = 0, len = data.data.length; i < len; i++) {
              this.columnIndexOptions.push({
                value: data.data[i].columnIndex,
                label: data.data[i].title,
                dataType: { NUMBER: 'number', 'TIMESTAMP(6)': 'date', VARCHAR2: 'string' }[data.data[i].columnType] || 'string'
              });
            }
          }
        });
    },
    onChangeDataModelType() {
      this.$refs.dataModelSelect.reset(this.widget.configuration.dataModelType);
    },
    fetchDataModelDetails(uuid, callback) {
      if (uuid) {
        $axios.get(`/proxy/api/dm/getDetails`, { params: { uuid } }).then(({ data, headers }) => {
          if (data.code == 0) {
            this.columnIndexOptions.splice(0, this.columnIndexOptions.length);
            let detail = data.data,
              columns = JSON.parse(detail.columnJson);
            for (let col of columns) {
              if (detail.type == 'TABLE' || (detail.type == 'VIEW' && col.hidden !== true && col.return === true)) {
                this.columnIndexOptions.push({
                  value: col.alias || col.column,
                  label: col.title,
                  isSysDefault: col.isSysDefault,
                  dataType: { number: 'number', varchar: 'string', timestamp: 'date' }[col.dataType] || 'string'
                });
              }
            }
            if (typeof callback == 'function') {
              callback.call(this);
            }
          }
        });
      }
    },
    onDataModelOptionsReady(optionsMap) {
      if (this.widget.configuration.dataModelUuid && optionsMap[this.widget.configuration.dataModelUuid]) {
        this.widget.configuration.dataModelName = optionsMap[this.widget.configuration.dataModelUuid].name;
      }
    },
    onDataModelChange(uuid, { id, name }) {
      this.columnIndexOptions.splice(0, this.columnIndexOptions.length);
      this.widget.configuration.dataModelName = name;
      this.fetchDataModelDetails(uuid, () => {
        this.widget.configuration.columns.splice(0, this.widget.configuration.columns.length);
        this.widget.configuration.dataModelId = id;
        for (let col of this.columnIndexOptions) {
          if (col.isSysDefault && col.value != 'UUID') {
            continue;
          }
          this.widget.configuration.columns.push({
            id: generateId(),
            title: col.label,
            dataIndex: col.value,
            primaryKey: col.value == 'UUID',
            ellipsis: false,
            sortable: false,
            hidden: col.value == 'UUID',
            keywordQuery: false,
            width: undefined,
            renderFunction: { type: undefined, options: {} },
            exportFunction: { type: undefined, options: {} },
            customVisibleType: 'chooseVisible',
            showTip: false,
            tipContent: undefined,
            titleAlign: 'left',
            contentAlign: 'left',
            ellipsis: true
          });
        }
      });
    },
    onPageSizeOptionsChange() {
      this.widget.configuration.pagination.pageSizeOptions.sort((a, b) => {
        return parseInt(a) - parseInt(b);
      });
    },

    clickCopySnippetCode(e, key) {
      let _this = this;
      if (this.codeSnippets && this.codeSnippets[key]) {
        copyToClipboard(this.codeSnippets[key], e, function (success) {
          if (success) {
            _this.$message.success('已复制');
          }
        });
      }
    },
    // 重置字段定义列表
    resetUniTemplateProperties() {
      // 字段定义默认先添加需要定义的字段
      each(this.uniColumnTemplateOptions, item => {
        this.widget.configuration.uniConfiguration.templateProperties.push({
          uuid: generateId(),
          title: item.label,
          name: item.value,
          mapColumn: '',
          mapColumnName: '',
          renderer: {},
          sortOrder: ''
        });
      });
    }
  },
  mounted() {
    this.fetchDataSourceOptions();
    if (this.widget.configuration.dataSourceId) {
      this.fetchColumns();
    }
    if (this.widget.configuration.dataModelUuid && this.widget.configuration.rowDataFrom == 'dataModel') {
      this.fetchDataModelDetails(this.widget.configuration.dataModelUuid);
    }

    import('./code-snippets').then(m => {
      this.codeSnippets = m.default;
    });
  }
};
</script>
