<template>
  <div>
    <a-form-model :model="widget.configuration" labelAlign="left" :wrapper-col="{ style: { textAlign: 'right' } }" :colon="false">
      <a-tabs default-active-key="1">
        <a-tab-pane key="1" tab="设置">
          <a-form-model-item label="名称">
            <a-input v-model="widget.title">
              <template slot="addonAfter">
                <WI18nInput :widget="widget" :designer="designer" :code="widget.id" v-model="widget.title" />
              </template>
            </a-input>
          </a-form-model-item>
          <a-form-model-item label="按钮类型">
            <a-select :options="buttonTypeOptions" v-model="widget.configuration.type" :style="{ width: '100%' }"></a-select>
          </a-form-model-item>
          <div v-show="widget.configuration.type === 'switch'">
            <a-form-model-item label="选中时内容">
              <a-input v-model="widget.configuration.switch.checkedText" />
            </a-form-model-item>
            <a-form-model-item label="非选中时内容">
              <a-input v-model="widget.configuration.switch.UnCheckedText" />
            </a-form-model-item>
            <a-form-model-item label="默认选中">
              <a-switch v-model="widget.configuration.switch.defaultChecked" />
            </a-form-model-item>
          </div>

          <a-form-model-item label="按钮尺寸">
            <a-radio-group v-model="widget.configuration.size" button-style="solid" size="small">
              <a-radio-button v-for="(size, i) in vButtonSizeOptions" :key="i" :value="size.value">{{ size.label }}</a-radio-button>
            </a-radio-group>
          </a-form-model-item>
          <a-form-model-item label="按钮位置">
            <a-radio-group v-model="widget.configuration.align" button-style="solid" size="small">
              <a-radio-button
                v-for="(align, i) in [
                  { label: '居左', value: 'left' },
                  { label: '居中', value: 'center' },
                  { label: '居右', value: 'right' }
                ]"
                :key="i"
                :value="align.value"
              >
                {{ align.label }}
              </a-radio-button>
            </a-radio-group>
          </a-form-model-item>

          <div v-show="widget.configuration.type != 'switch'">
            <a-form-model-item label="隐藏文本">
              <a-switch v-model="widget.configuration.textHidden" />
            </a-form-model-item>
            <a-form-model-item label="块级">
              <a-switch v-model="widget.configuration.block" />
            </a-form-model-item>
            <a-form-model-item label="前置图标" v-show="widget.configuration.groupType == null">
              <WidgetDesignDrawer
                :id="'buttonPrefixIconConfigureDrawer' + widget.id"
                title="选择图标"
                :width="640"
                :bodyStyle="{ height: '100%' }"
                :designer="designer"
              >
                <IconSetBadge v-model="widget.configuration.icon"></IconSetBadge>
                <template slot="content">
                  <WidgetIconLib v-model="widget.configuration.icon" />
                </template>
              </WidgetDesignDrawer>
            </a-form-model-item>
            <a-form-model-item label="后置图标" v-show="widget.configuration.groupType == null">
              <WidgetDesignDrawer
                :id="'buttonSuffixIconConfigureDrawer' + widget.id"
                title="选择图标"
                :width="640"
                :bodyStyle="{ height: '100%' }"
                :designer="designer"
              >
                <IconSetBadge v-model="widget.configuration.suffixIcon"></IconSetBadge>
                <template slot="content">
                  <WidgetIconLib v-model="widget.configuration.suffixIcon" />
                </template>
              </WidgetDesignDrawer>
            </a-form-model-item>
            <a-form-model-item label="按钮形状">
              <a-select :style="{ width: '100%' }" :options="buttonShapeOptions" v-model="widget.configuration.shape" allowClear></a-select>
            </a-form-model-item>

            <a-form-model-item label="按钮组类型">
              <a-select
                :options="[
                  { label: '平铺按钮组', value: 'buttonGroup' },
                  { label: '下拉按钮组', value: 'dropdown' }
                ]"
                v-model="widget.configuration.groupType"
                allowClear
                :style="{ width: '100%' }"
              ></a-select>
            </a-form-model-item>
            <a-form-model-item v-if="widget.configuration.groupType == 'buttonGroup'">
              <template slot="label">
                按钮间距
                <a-checkbox v-model="widget.configuration.enableSpace" />
              </template>
              <a-input-number v-show="widget.configuration.enableSpace" :min="0" v-model="widget.configuration.buttonSpace" />
            </a-form-model-item>
          </div>

          <a-form-model-item label="事件处理" v-if="widget.configuration.groupType == undefined">
            <WidgetDesignDrawer :id="'buttonEventSettingConfigureDrawer' + widget.id" title="操作设置" :designer="designer">
              <a-button title="事件处理" icon="setting"></a-button>
              <template slot="content">
                <div>
                  <a-form-model>
                    <BehaviorLogConfiguration :widget="widget" :configuration="widget.configuration" :designer="designer" />
                  </a-form-model>
                  <WidgetEventHandler :eventModel="widget.configuration.eventHandler" :designer="designer" :widget="widget" />
                </div>
              </template>
            </WidgetDesignDrawer>
          </a-form-model-item>

          <div v-show="hasGroupType">
            <a-divider>按钮组</a-divider>

            <div>
              <a-button type="link" @click="addButton" icon="plus-circle">添加按钮</a-button>
              <a-table
                class="widget-button-table no-border"
                :showHeader="false"
                rowKey="id"
                :pagination="false"
                :bordered="false"
                size="small"
                :locale="{ emptyText: '暂无数据' }"
                :data-source="widget.configuration.group"
                :columns="buttonColumns"
              >
                <template slot="titleSlot" slot-scope="text, record">
                  <a-input class="widget-menu-title-edit-input" v-model="record.title" size="small" style="110px">
                    <template slot="addonAfter">
                      <WI18nInput :widget="widget" :designer="designer" :code="record.id" v-model="record.title" />
                    </template>
                  </a-input>
                </template>
                <template slot="operationSlot" slot-scope="text, record">
                  <a-button title="删除按钮" type="link" @click="delButton(record.id)">
                    <Icon type="pticon iconfont icon-ptkj-shanchu"></Icon>
                  </a-button>
                  <WidgetDesignDrawer :id="'btnMoreConfig' + record.id" title="设置" :designer="designer">
                    <a-button size="small" title="更多设置" type="link">
                      <Icon type="pticon iconfont icon-ptkj-shezhi"></Icon>
                    </a-button>
                    <template slot="content">
                      <ButtonConfiguration :widget="widget" :designer="designer" :button="record" />
                    </template>
                  </WidgetDesignDrawer>
                </template>
              </a-table>
            </div>
          </div>
          <a-collapse :bordered="false" expandIconPosition="right">
            <a-collapse-panel key="visibleSetting" header="显示设置" v-if="!hasGroupType">
              <DefaultVisibleConfiguration
                compact
                :designer="designer"
                :configuration="widget.configuration"
                :widget="widget"
                hasDyformField
              ></DefaultVisibleConfiguration>
            </a-collapse-panel>
          </a-collapse>
        </a-tab-pane>
        <a-tab-pane key="2" tab="事件设置">
          <WidgetEventConfiguration :widget="widget" :designer="designer"></WidgetEventConfiguration>
        </a-tab-pane>
      </a-tabs>
    </a-form-model>
  </div>
</template>
<style></style>
<script type="text/babel">
import { buttonTypeOptions, buttonShapeOptions, buttonSizeOptions } from '../../commons/constant.js';
import { generateId } from '@framework/vue/utils/util';
import ButtonConfiguration from './button-configuration.vue';
export default {
  name: 'WidgetButtonConfiguration',
  mixins: [],
  props: {
    widget: Object,
    designer: Object
  },
  data() {
    return {
      buttonShapeOptions,
      buttonTypeOptions,
      buttonSizeOptions,
      suffixIconVisible: this.widget.configuration.suffixIcon != null,
      iconVisible: this.widget.configuration.icon != null,
      buttonColumns: [
        {
          title: '名称',
          dataIndex: 'title',
          scopedSlots: { customRender: 'titleSlot' }
        },
        {
          title: '操作',
          dataIndex: 'opeartion',
          width: 112,
          align: 'right',
          class: 'operation',
          scopedSlots: { customRender: 'operationSlot' }
        }
      ]
    };
  },

  beforeCreate() {},
  components: { ButtonConfiguration },
  computed: {
    vButtonSizeOptions() {
      if (this.widget.configuration.type === 'switch') {
        return this.buttonSizeOptions.slice(0, 2);
      }
      return this.buttonSizeOptions;
    },
    hasGroupType() {
      return this.widget.configuration.groupType != null;
    }
  },
  created() {},
  methods: {
    // 生成后端的功能元素数据，进行角色权限控制
    getFunctionElements() {
      return {};
    },

    // 生成组件定义数据保存
    getWidgetDefinitionElements(widget) {
      return [
        {
          wtype: widget.wtype,
          id: widget.id,
          title: widget.title,
          definitionJson: JSON.stringify(widget)
        }
      ];
    },
    addButton() {
      this.widget.configuration.group.push({
        id: `button-${generateId()}`,
        title: '按钮',
        textHidden: false,
        type: 'default',
        suffixIcon: null,
        eventHandler: {},
        icon: null
      });
    },

    delButton(id) {
      let btns = this.widget.configuration.group;
      for (var i = 0, len = btns.length; i < len; i++) {
        if (btns[i].id === id) {
          btns.splice(i, 1);
          break;
        }
      }
    },
    iconSelected(icon, iconTarget, iconKey) {
      iconTarget[iconKey] = icon;
    }
  },
  mounted() {}
};
</script>
