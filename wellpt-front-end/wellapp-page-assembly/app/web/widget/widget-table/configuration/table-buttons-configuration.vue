<template>
  <div>
    <!-- <a-form-model-item :label="title">
      <a-switch v-model="button.enable" />
    </a-form-model-item> -->

    <a-table
      rowKey="id"
      :showHeader="false"
      size="small"
      :pagination="false"
      :bordered="false"
      :columns="buttonTableColumn"
      :locale="locale"
      :data-source="button.buttons"
      style="--pt-table-td-padding: var(--w-padding-2xs)"
      :class="['pt-table widget-table-button-table no-border', !button.enable ? 'hidden-table-content' : '']"
    >
      <template slot="title">
        <i class="line" />
        {{ title }}
        <a-switch v-model="button.enable" :style="{ float: 'right', 'margin-right': '4px;' }" />
      </template>
      <template slot="titleSlot" slot-scope="text, record">
        <Icon
          title="拖动排序"
          type="pticon iconfont icon-ptkj-tuodong"
          class="drag-btn-handler drag-handler"
          :style="{ cursor: 'move' }"
        ></Icon>
        <a-input v-model="record.title" size="small" :style="{ width: '100px' }" class="addon-padding-3xs">
          <Icon slot="prefix" :type="record.style.icon" v-if="record.style.icon" />
          <template slot="addonAfter">
            <WI18nInput :widget="widget" :designer="designer" :code="record.id" :target="record" v-model="record.title" />
          </template>
        </a-input>
        <a-tag color="blue" v-if="record.eventHandler && record.eventHandler.actionTypeName" :style="{ marginRight: '0px' }">
          {{ record.eventHandler.actionTypeName }}
        </a-tag>
        <a-tag color="blue" v-if="record.id.startsWith('export_')" :style="{ marginRight: '0px' }">数据导出</a-tag>
        <a-tag color="blue" v-if="record.id.startsWith('import_')" :style="{ marginRight: '0px' }">数据导入</a-tag>

        <!-- <a-tag color="blue" v-show="record.displayPositionName" :style="{ marginRight: '0px' }">
          {{ record.displayPositionName }}
        </a-tag> -->
      </template>

      <template slot="operationSlot" slot-scope="text, record, index">
        <a-button
          type="link"
          size="small"
          @click="record.defaultVisible = !record.defaultVisible"
          :title="record.defaultVisible ? '隐藏' : '显示'"
        >
          <Icon :type="record.defaultVisible ? 'pticon iconfont icon-wsbs-xianshi' : 'pticon iconfont icon-wsbs-yincang'"></Icon>
        </a-button>
        <a-button
          type="link"
          v-if="
            record.unDeleted !== true &&
            (editRule.button == undefined || editRule.button[record.id] == undefined || editRule.button[record.id].deleteHidden != true)
          "
          size="small"
          title="删除"
          @click="delButton(record, index)"
        >
          <Icon type="pticon iconfont icon-ptkj-shanchu"></Icon>
        </a-button>
        <WidgetDesignDrawer :id="'WidgetTableBtnConfig' + record.id" title="编辑按钮" :designer="designer">
          <a-button type="link" size="small" @click="e => onClickEditButton(e, record)" title="编辑按钮">
            <Icon type="pticon iconfont icon-ptkj-shezhi"></Icon>
          </a-button>
          <template slot="content">
            <ButtonConfiguration
              :button="record"
              :widget="widget"
              :designer="designer"
              :setDisplayPosition="position === 'row'"
              :position="position"
              :editRule="editRule"
            ></ButtonConfiguration>
          </template>
        </WidgetDesignDrawer>
      </template>
      <template slot="footer">
        <WidgetDesignDrawer :id="'WidgetTableBtnConfig' + position + widget.id" :title="'添加' + title" :designer="designer">
          <div style="text-align: right">
            <a-button type="link" :style="{ paddingLeft: '7px' }" size="small">
              <Icon type="pticon iconfont icon-ptkj-jiahao"></Icon>
              添加
            </a-button>
          </div>
          <template slot="content">
            <ButtonConfiguration
              ref="addWidgetTableBtnConfig"
              :editRule="editRule"
              :button="newButton"
              :widget="widget"
              :designer="designer"
              :setDisplayPosition="position === 'row'"
              :position="position"
            />
          </template>
          <template slot="footer">
            <a-button size="small" type="primary" @click.stop="onConfirmOk">确定</a-button>
          </template>
        </WidgetDesignDrawer>
        <template v-if="designer.terminalType !== 'mobile'">
          <a-form-model-item label="按钮分组" class="item-lh">
            <a-radio-group size="small" v-model="button.buttonGroup.type" button-style="solid" @change="onChangeButtonGroupType">
              <a-radio-button value="notGroup">不分组</a-radio-button>
              <a-radio-button value="fixedGroup">固定分组</a-radio-button>
              <a-radio-button value="dynamicGroup">动态分组</a-radio-button>
            </a-radio-group>
          </a-form-model-item>
          <div v-if="button.buttonGroup.type === 'dynamicGroup'">
            <a-form-model-item>
              <template slot="label">
                当按钮超过
                <a-input-number v-model="button.buttonGroup.dynamicGroupBtnThreshold" :min="1" style="width: 60px" size="small" />
                个时进行分组
              </template>
            </a-form-model-item>
            <a-form-model-item label="分组名称">
              <a-input v-model="button.buttonGroup.dynamicGroupName">
                <template slot="addonAfter">
                  <WI18nInput
                    :target="button.buttonGroup"
                    code="dynamicGroupName"
                    v-model="button.buttonGroup.dynamicGroupName"
                    :widget="widget"
                  />
                  <WidgetDesignDrawer :id="'WidgetTableBtnConfigDynamicGroupName'" title="分组按钮设置" :designer="designer">
                    <a-button type="link" size="small" v-if="button.buttonGroup.style != undefined" title="分组按钮设置">
                      <Icon type="pticon iconfont icon-ptkj-shezhi"></Icon>
                    </a-button>
                    <template slot="content">
                      <a-form-model>
                        <a-form-model-item label="名称">
                          <a-input v-model="button.buttonGroup.dynamicGroupName">
                            <template slot="addonAfter">
                              <WI18nInput
                                :target="button.buttonGroup"
                                code="dynamicGroupName"
                                v-model="button.buttonGroup.dynamicGroupName"
                                :widget="widget"
                              />
                              <a-switch
                                :checked="button.buttonGroup.style.textHidden !== true"
                                @change="
                                  e => {
                                    button.buttonGroup.style.textHidden = !e;
                                  }
                                "
                                checked-children="显示名称"
                                un-checked-children="显示名称"
                              />
                            </template>
                          </a-input>
                        </a-form-model-item>
                        <a-form-model-item label="按钮类型">
                          <a-select
                            :options="[
                              { label: '主按钮', value: 'primary' },
                              { label: '次按钮', value: 'default' },
                              { label: '链接按钮', value: 'link' }
                            ]"
                            v-model="button.buttonGroup.style.type"
                            :style="{ width: '100%' }"
                          ></a-select>
                        </a-form-model-item>
                        <a-form-model-item label="按钮图标">
                          <WidgetDesignModal
                            title="选择图标"
                            :zIndex="1000"
                            :width="640"
                            dialogClass="pt-modal widget-icon-lib-modal"
                            :bodyStyle="{ height: '560px' }"
                            :maxHeight="560"
                            mask
                            bodyContainer
                          >
                            <IconSetBadge v-model="button.buttonGroup.style.icon"></IconSetBadge>
                            <template slot="content">
                              <WidgetIconLib v-model="button.buttonGroup.style.icon" />
                            </template>
                          </WidgetDesignModal>
                        </a-form-model-item>

                        <a-form-model-item label="右侧下拉箭头">
                          <a-switch v-model="button.buttonGroup.style.rightDownIconVisible" />
                        </a-form-model-item>
                      </a-form-model>
                    </template>
                  </WidgetDesignDrawer>
                </template>
              </a-input>
            </a-form-model-item>
          </div>
          <a-table
            v-if="button.buttonGroup.type === 'fixedGroup'"
            :showHeader="false"
            rowKey="id"
            :pagination="false"
            :bordered="false"
            size="small"
            :locale="locale"
            :columns="groupButtonTableColumn"
            :data-source="button.buttonGroup.groups"
          >
            <template slot="titleSlot" slot-scope="text, record">
              <a-form :colon="false">
                <a-form-item label="分组名称" :style="{ padding: 0 }">
                  <a-input v-model="record.name" size="small">
                    <template slot="addonAfter">
                      <WI18nInput :target="record" :code="record.id" v-model="record.name" :widget="widget" />
                      <WidgetDesignDrawer :id="'WidgetTableBtnConfigGroup' + record.id" title="分组按钮设置" :designer="designer">
                        <a-button type="link" size="small" title="分组按钮设置">
                          <Icon type="pticon iconfont icon-ptkj-shezhi"></Icon>
                        </a-button>
                        <template slot="content">
                          <a-form-model>
                            <a-form-model-item label="名称">
                              <a-input v-model="record.name">
                                <template slot="addonAfter">
                                  <WI18nInput :target="record" :code="record.id" v-model="record.name" :widget="widget" />
                                  <a-switch
                                    :checked="record.style.textHidden !== true"
                                    @change="
                                      e => {
                                        record.style.textHidden = !e;
                                      }
                                    "
                                    checked-children="显示名称"
                                    un-checked-children="显示名称"
                                  />
                                </template>
                              </a-input>
                            </a-form-model-item>
                            <a-form-model-item label="按钮类型">
                              <a-select
                                :options="[
                                  { label: '主按钮', value: 'primary' },
                                  { label: '次按钮', value: 'default' },
                                  { label: '链接按钮', value: 'link' }
                                ]"
                                v-model="record.style.type"
                                :style="{ width: '100%' }"
                              ></a-select>
                            </a-form-model-item>
                            <a-form-model-item label="按钮图标">
                              <WidgetDesignModal
                                title="选择图标"
                                :zIndex="1000"
                                :width="640"
                                dialogClass="pt-modal widget-icon-lib-modal"
                                :bodyStyle="{ height: '560px' }"
                                :maxHeight="560"
                                mask
                                bodyContainer
                              >
                                <IconSetBadge v-model="record.style.icon"></IconSetBadge>
                                <template slot="content">
                                  <WidgetIconLib v-model="record.style.icon" />
                                </template>
                              </WidgetDesignModal>
                            </a-form-model-item>
                            <a-form-model-item label="右侧下拉箭头">
                              <a-switch v-model="record.style.rightDownIconVisible" />
                            </a-form-model-item>
                          </a-form-model>
                        </template>
                      </WidgetDesignDrawer>
                    </template>
                  </a-input>
                </a-form-item>
                <a-form-item label="分组按钮" :style="{ padding: 0 }">
                  <a-select
                    size="small"
                    mode="multiple"
                    v-model="record.buttonIds"
                    style="width: 100%"
                    :options="groupButtonOptions"
                  ></a-select>
                </a-form-item>
              </a-form>
            </template>
            <template slot="operationSlot" slot-scope="text, record, index">
              <a-button type="link" size="small" v-if="record.unDeleted !== true" @click="delGroupButton(index)" title="删除">
                <Icon type="pticon iconfont icon-ptkj-shanchu"></Icon>
              </a-button>
            </template>
            <template slot="footer">
              <a-button type="link" @click="addGroupButton">
                <Icon type="pticon iconfont icon-ptkj-jiahao"></Icon>
                添加
              </a-button>
            </template>
          </a-table>
        </template>
      </template>
    </a-table>
  </div>
</template>

<script type="text/babel">
import { generateId, deepClone } from '@framework/vue/utils/util';
import ButtonConfiguration from './button-configuration.vue';
import draggable from '@framework/vue/designer/draggable';

export default {
  name: 'TableButtonsConfiguration',
  mixins: [draggable],
  inject: ['pageContext'],
  props: {
    widget: Object,
    designer: Object,
    button: Object,
    title: String,
    position: {
      type: String,
      default: 'header'
    },
    editRule: Object
  },
  data() {
    return {
      locale: {
        emptyText: <span>暂无数据</span>
      },
      newButton: {
        id: generateId(),
        code: undefined,
        title: undefined,
        visible: true,
        role: [],
        displayPosition: this.title == '行按钮' ? 'rowEnd' : undefined,
        style: { icon: undefined, textHidden: false, type: 'default' },
        switch: {
          checkedText: undefined,
          UnCheckedText: undefined,
          defaultChecked: true,
          checkedCondition: { code: undefined, operator: 'true', value: undefined }
        },
        eventHandler: { eventParams: [] },
        customEventScript: undefined,
        defaultVisible: true,
        defaultVisibleVar: { operator: 'true', code: undefined, value: undefined }
      },
      locale: {
        emptyText: <span>暂无数据</span>
      },

      buttonTableColumn: [
        { title: '名称', dataIndex: 'title', scopedSlots: { customRender: 'titleSlot' } },
        { title: '操作', dataIndex: 'operation', width: 110, scopedSlots: { customRender: 'operationSlot' } }
      ],
      groupButtonTableColumn: [
        { title: '名称', dataIndex: 'title', scopedSlots: { customRender: 'titleSlot' } },
        { title: '操作', dataIndex: 'operation', width: 60, scopedSlots: { customRender: 'operationSlot' } }
      ]
    };
  },

  beforeCreate() {},
  components: { ButtonConfiguration },
  computed: {
    groupButtonOptions() {
      let options = [];
      for (let i = 0, len = this.button.buttons.length; i < len; i++) {
        options.push({ label: this.button.buttons[i].title, value: this.button.buttons[i].id });
      }
      return options;
    }
  },
  created() {},
  methods: {
    onClickEditButton(e, item) {
      if (item.id.startsWith('export_') || item.id.startsWith('import_')) {
        // 导入导出按钮由导出按钮配置负责打开
        this.pageContext.emitEvent(
          `${this.widget.id}:${item.id.startsWith('export_') ? 'openExportButtonConfig' : 'openImportButtonConfig'}`,
          item.id
        );
        e.stopPropagation();
      }
    },
    onConfirmOk() {
      this.$refs.addWidgetTableBtnConfig.validate().then(valid => {
        if (valid) {
          this.button.buttons.push(deepClone(this.newButton));
          this.newButton = deepClone(this.defaultNewBtn);
          this.newButton.id = generateId();
          this.designer.emitEvent('closeDrawer:' + this.designer.drawerVisibleKey);
        }
      });
    },

    addGroupButton() {
      this.button.buttonGroup.groups.push({
        id: generateId(),
        name: '',
        buttonIds: [],
        style: {
          textHidden: false,
          type: 'default',
          icon: undefined,
          rightDownIconVisible: false
        }
      });
    },
    delGroupButton(index) {
      this.button.buttonGroup.groups.splice(index, 1);
    },
    delButton(button, index) {
      this.button.buttons.splice(index, 1);
      if (button.id.startsWith('export_')) {
        delete this.widget.configuration.export.exportButtonRule[button.id];
      }
    },
    onChangeButtonGroupType() {
      if (this.button.buttonGroup.type === 'fixedGroup' && this.button.buttonGroup.groups.length > 0) {
        // 补充旧数据配置
        for (let i = 0, len = this.button.buttonGroup.groups.length; i < len; i++) {
          if (this.button.buttonGroup.groups[i].style == undefined) {
            this.$set(this.button.buttonGroup.groups[i], 'style', {
              textHidden: false,
              type: 'default',
              icon: undefined,
              rightDownIconVisible: false
            });
          }
        }
      }
      if (this.button.buttonGroup.type === 'dynamicGroup' && this.button.buttonGroup.style == undefined) {
        this.$set(this.button.buttonGroup, 'style', { textHidden: false, type: 'default', icon: undefined, rightDownIconVisible: false });
      }
    }
  },
  beforeMount() {
    this.defaultNewBtn = deepClone(this.newButton);
    this.onChangeButtonGroupType();
  },
  mounted() {
    this.tableDraggable(this.button.buttons, this.$el.querySelector('.widget-table-button-table tbody'), '.drag-btn-handler');
  }
};
</script>
