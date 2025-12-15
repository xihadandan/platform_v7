<template>
  <a-form-model labelAlign="left" :wrapper-col="{ style: { textAlign: 'right', width: '177px' } }">
    <a-checkbox-group v-model="widget.configuration.addonValue" style="width: 100%">
      <a-form-model-item>
        <template #label>
          <a-checkbox value="addonFront">前置元素</a-checkbox>
        </template>
        <a-select
          mode="tags"
          style="width: 100%; margin-bottom: 4px"
          placeholder="请输入前置元素值"
          :options="addonFrontOptions"
          :allowClear="true"
          v-model="widget.configuration.addonFrontValue"
          :showArrow="true"
          :getPopupContainer="getPopupContainerByPs()"
        ></a-select>
        <a-select
          v-model="widget.configuration.realDisplayAddonFront"
          placeholder="关联字段"
          :allowClear="true"
          :options="formFieldOptions"
          :style="{ width: '100%' }"
          @change="onChange"
          :getPopupContainer="getPopupContainerByPs()"
          :dropdownClassName="getDropdownClassName()"
        ></a-select>
      </a-form-model-item>
      <a-form-model-item>
        <template #label>
          <a-checkbox value="addonEnd">后置元素</a-checkbox>
        </template>
        <a-select
          mode="tags"
          style="width: 100%; margin-bottom: 4px"
          placeholder="请输入后置元素值"
          :allowClear="true"
          :options="addonEndOptions"
          v-model="widget.configuration.addonEndValue"
          :showArrow="true"
          :getPopupContainer="getPopupContainerByPs()"
        ></a-select>
        <a-select
          v-model="widget.configuration.realDisplayAddonEnd"
          placeholder="关联字段"
          :allowClear="true"
          :options="formFieldOptions"
          :style="{ width: '100%' }"
          @change="onChange"
          :getPopupContainer="getPopupContainerByPs()"
          :dropdownClassName="getDropdownClassName()"
        ></a-select>
      </a-form-model-item>
      <a-form-model-item>
        <template #label>
          <a-checkbox value="addonFrontIcon">图标(前置)</a-checkbox>
          <a-tooltip placement="bottomRight">
            <template slot="title">
              <ul style="padding-inline-start: 20px; margin-block-end: 0px">
                <li>移动端请使用工程图标库图标</li>
              </ul>
            </template>
            <Icon type="pticon iconfont icon-ptkj-xinxiwenxintishi" />
          </a-tooltip>
        </template>

        <WidgetDesignDrawer
          :id="'widgetFormInputAddonIconPrefix' + widget.id"
          title="选择图标"
          :width="640"
          :bodyStyle="{ height: '100%' }"
          :designer="designer"
        >
          <IconSetBadge v-model="widget.configuration.addonFrontIconValue" onlyIconClass />

          <template slot="content">
            <WidgetIconLib v-model="widget.configuration.addonFrontIconValue" />
          </template>
        </WidgetDesignDrawer>
      </a-form-model-item>
      <a-form-model-item>
        <template #label>
          <a-checkbox value="addonEndIcon">图标(后置)</a-checkbox>
          <a-tooltip placement="bottomRight">
            <template slot="title">
              <ul style="padding-inline-start: 20px; margin-block-end: 0px">
                <li>移动端请使用工程图标库图标</li>
              </ul>
            </template>
            <Icon type="pticon iconfont icon-ptkj-xinxiwenxintishi" />
          </a-tooltip>
        </template>

        <WidgetDesignDrawer
          :id="'widgetFormInputAddonIconSuffix' + widget.id"
          title="选择图标"
          :width="640"
          :bodyStyle="{ height: '100%' }"
          :designer="designer"
        >
          <IconSetBadge v-model="widget.configuration.addonEndIconValue" onlyIconClass />
          <template slot="content">
            <WidgetIconLib v-model="widget.configuration.addonEndIconValue" />
          </template>
        </WidgetDesignDrawer>
      </a-form-model-item>
    </a-checkbox-group>
  </a-form-model>
</template>
<style lang="less"></style>
<script type="text/babel">
import { getPopupContainerByPs, getDropdownClassName } from '@dyform/app/web/page/dyform-designer/utils';
export default {
  name: 'inputDefaultSetting',
  props: {
    widget: Object,
    designer: Object,
    value: String
  },
  data() {
    return {
      iconSelectVisible: false,
      iconSetKey: '',
      addonFrontOptions: [
        { label: 'https://', value: 'https://' },
        { label: 'http://', value: 'http://' }
      ],
      addonEndOptions: [
        { label: '.com', value: '.com' },
        { label: '@qq.com', value: '@qq.com' }
      ]
    };
  },
  beforeCreate() {},
  components: {},
  watch: {},
  computed: {
    formFieldOptions() {
      // this.designer.widgetIdMap[id].configuraiont.default ==111
      let opt = [];
      this.designer.WidgetFormInputs;
      if (this.designer.FieldWidgets && this.designer.FieldWidgets.length) {
        for (let k = 0, len = this.designer.FieldWidgets.length; k < len; k++) {
          let field = this.designer.FieldWidgets[k];
          if (
            field.configuration.code &&
            field.id != this.widget.id &&
            field.wtype == 'WidgetFormInput' &&
            (field.configuration.type == 'input' || field.configuration.type == 'textarea')
          ) {
            opt.push({
              id: field.id,
              label: field.configuration.name || field.configuration.code,
              value: field.configuration.code
            });
          }
        }
      }
      return opt;
    }
  },
  created() {},
  methods: {
    getPopupContainerByPs,
    getDropdownClassName,
    iconSelected(icon) {
      if (this.iconSetKey) {
        this.widget.configuration[this.iconSetKey] = icon;
      }
    },
    onChange(val, opt) {
      if (opt.data.props.id && this.designer.widgetIdMap[opt.data.props.id]) {
        this.designer.widgetIdMap[opt.data.props.id].configuration.defaultDisplayState = 'hidden';
      }
    }
  },
  mounted() {}
};
</script>
