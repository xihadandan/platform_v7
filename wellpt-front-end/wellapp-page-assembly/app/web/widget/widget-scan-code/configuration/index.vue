<template>
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
        <!-- <a-form-model-item label="类型">
            <a-select :options="typeOptions" v-model="widget.configuration.type" :style="{ width: '100%' }"></a-select>
          </a-form-model-item> -->
        <a-form-model-item label="尺寸">
          <a-radio-group v-model="widget.configuration.size" button-style="solid" size="small">
            <a-radio-button v-for="(size, i) in sizeOptions" :key="i" :value="size.value">{{ size.label }}</a-radio-button>
          </a-radio-group>
        </a-form-model-item>
        <a-form-model-item label="位置">
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
        <a-form-model-item label="隐藏文本">
          <a-switch v-model="widget.configuration.textHidden" />
        </a-form-model-item>
        <a-form-model-item label="块级">
          <a-switch v-model="widget.configuration.block" />
        </a-form-model-item>
        <a-form-model-item label="前置图标">
          <WidgetDesignDrawer
            :id="'buttonPrefixIconConfigureDrawer' + widget.id"
            title="选择图标"
            :width="640"
            :bodyStyle="{ height: '100%' }"
            :designer="designer"
          >
            <IconSetBadge v-model="widget.configuration.icon" />
            <template slot="content">
              <WidgetIconLib v-model="widget.configuration.icon" />
            </template>
          </WidgetDesignDrawer>
        </a-form-model-item>
        <!--  <a-form-model-item label="后置图标">
          <WidgetDesignDrawer
            :id="'buttonSuffixIconConfigureDrawer' + widget.id"
            title="选择图标"
            :width="640"
            :bodyStyle="{ height: '100%' }"
            :designer="designer"
          >
            <a-badge>
              <a-icon
                v-if="widget.configuration.suffixIcon"
                slot="count"
                type="close-circle"
                style="color: #f5222d"
                theme="filled"
                @click.stop="widget.configuration.suffixIcon = undefined"
                title="删除图标"
              />
              <a-button size="small" shape="round">
                {{ widget.configuration.suffixIcon ? '' : '设置图标' }}
                <Icon :type="widget.configuration.suffixIcon || 'setting'" />
              </a-button>
            </a-badge>

            <template slot="content">
              <WidgetIconLib v-model="widget.configuration.suffixIcon" />
            </template>
          </WidgetDesignDrawer>
        </a-form-model-item> -->
      </a-tab-pane>
    </a-tabs>
  </a-form-model>
</template>

<script>
export default {
  name: 'WidgetScanCodeConfiguration',
  props: {
    widget: Object,
    designer: Object
  },
  data() {
    return {
      typeOptions: [
        { label: '主按钮', value: 'primary' },
        { label: '次按钮', value: 'default' },
        { label: '链接按钮', value: 'link' },
        { label: '危险按钮', value: 'danger' },
        { label: '虚线按钮', value: 'dashed' }
      ],
      sizeOptions: [
        { value: 'default', label: '默认' },
        { value: 'small', label: '小' },
        { value: 'large', label: '大' }
      ]
    };
  },
  methods: {}
};
</script>
