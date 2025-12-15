<template>
  <view>
    <!-- 页面组件 start -->
    <w-tab-bar
      v-if="widget.wtype == 'WidgetUniTabBar'"
      :widget="widget"
      :parent="parent"
      :widgetsOfParent="widgetsOfParent"
      :key="widget.wtype + '_' + widget.id"
    ></w-tab-bar>
    <w-swiper
      v-else-if="widget.wtype == 'WidgetUniSwiper'"
      :widget="widget"
      :parent="parent"
      :widgetsOfParent="widgetsOfParent"
      :key="widget.wtype + '_' + widget.id"
    ></w-swiper>
    <w-grid-view
      v-else-if="widget.wtype == 'WidgetUniGridView'"
      :widget="widget"
      :parent="parent"
      :widgetsOfParent="widgetsOfParent"
      :key="widget.wtype + '_' + widget.id"
    ></w-grid-view>
    <w-nav-bar
      v-else-if="widget.wtype == 'WidgetUniNavBar'"
      :widget="widget"
      :parent="parent"
      :widgetsOfParent="widgetsOfParent"
      :key="widget.wtype + '_' + widget.id"
    ></w-nav-bar>
    <w-nav
      v-else-if="widget.wtype == 'WidgetUniNav'"
      :widget="widget"
      :parent="parent"
      :widgetsOfParent="widgetsOfParent"
      :key="widget.wtype + '_' + widget.id"
    ></w-nav>
    <w-list-view
      v-else-if="widget.wtype == 'WidgetUniListView'"
      :widget="widget"
      :parent="parent"
      :widgetsOfParent="widgetsOfParent"
      :key="widget.wtype + '_' + widget.id"
    ></w-list-view>
    <w-tabs
      v-else-if="widget.wtype == 'WidgetUniTab' || widget.wtype == 'WidgetTab'"
      :widget="widget"
      :parent="parent"
      :widgetsOfParent="widgetsOfParent"
      :key="widget.wtype + '_' + widget.id"
    ></w-tabs>
    <w-html
      v-else-if="widget.wtype == 'WidgetUniHtml'"
      :widget="widget"
      :parent="parent"
      :widgetsOfParent="widgetsOfParent"
      :key="widget.wtype + '_' + widget.id"
    ></w-html>
    <w-tiles
      v-else-if="widget.wtype == 'WidgetUniTiles'"
      :widget="widget"
      :parent="parent"
      :widgetsOfParent="widgetsOfParent"
      :key="widget.wtype + '_' + widget.id"
    ></w-tiles>
    <w-title-section
      v-else-if="widget.wtype == 'WidgetUniSection'"
      :widget="widget"
      :parent="parent"
      :widgetsOfParent="widgetsOfParent"
      :key="widget.wtype + '_' + widget.id"
    ></w-title-section>
    <w-tip
      v-else-if="widget.wtype == 'WidgetTip'"
      :widget="widget"
      :parent="parent"
      :widgetsOfParent="widgetsOfParent"
      :key="widget.wtype + '_' + widget.id"
    ></w-tip>
    <w-video
      v-else-if="widget.wtype == 'WidgetVideo'"
      :widget="widget"
      :parent="parent"
      :widgetsOfParent="widgetsOfParent"
      :key="widget.wtype + '_' + widget.id"
    ></w-video>
    <w-button-group
      v-else-if="widget.wtype == 'WidgetButton'"
      :widget="widget"
      :parent="parent"
      :widgetsOfParent="widgetsOfParent"
      :key="widget.wtype + '_' + widget.id"
    ></w-button-group>
    <w-template
      v-else-if="widget.wtype == 'WidgetTemplate'"
      :widget="widget"
      :parent="parent"
      :widgetsOfParent="widgetsOfParent"
      :key="widget.wtype + '_' + widget.id"
    >
    </w-template>
    <w-grid
      v-else-if="widget.wtype == 'WidgetGrid'"
      :widget="widget"
      :parent="parent"
      :widgetsOfParent="widgetsOfParent"
      :key="widget.wtype + '_' + widget.id"
    >
    </w-grid>
    <w-card
      v-else-if="widget.wtype == 'WidgetCard'"
      :widget="widget"
      :parent="parent"
      :widgetsOfParent="widgetsOfParent"
      :key="widget.wtype + '_' + widget.id"
    >
    </w-card>
    <w-anchor
      v-else-if="widget.wtype == 'WidgetAnchor'"
      :widget="widget"
      :parent="parent"
      :widgetsOfParent="widgetsOfParent"
      :key="widget.wtype + '_' + widget.id"
    >
    </w-anchor>
    <w-scan-code
      v-else-if="widget.wtype == 'WidgetScanCode'"
      :widget="widget"
      :parent="parent"
      :widgetsOfParent="widgetsOfParent"
      :key="widget.wtype + '_' + widget.id"
    >
    </w-scan-code>
    <w-table
      v-else-if="widget.wtype == 'WidgetTable'"
      :widget="widget"
      :parent="parent"
      :widgetsOfParent="widgetsOfParent"
      :key="widget.wtype + '_' + widget.id"
    ></w-table>
    <w-data-management-viewer
      v-else-if="widget.wtype == 'WidgetDataManagerView'"
      :widget="widget"
      :parent="parent"
      :widgetsOfParent="widgetsOfParent"
      :key="widget.wtype + '_' + widget.id"
    ></w-data-management-viewer>

    <!-- 页面组件 end -->

    <!-- 表单组件 start -->
    <w-form-layout
      v-else-if="widget.wtype == 'WidgetFormLayout'"
      :widget="widget"
      :parent="parent"
      :formObject="form"
      :formDataProp="formData"
      :formModelItem="formModelItemProp"
      :widgetsOfParent="widgetsOfParent"
      :key="widget.wtype + '_' + widget.id"
    ></w-form-layout>
    <w-form-input
      v-else-if="widget.wtype == 'WidgetFormInput'"
      :widget="widget"
      :parent="parent"
      :formObject="form"
      :formDataProp="formData"
      :formModelItem="formModelItemProp"
      :key="widget.wtype + '_' + widget.id"
      :isSubformCell="isSubformCell"
      :unValidateWidget="unValidateWidget"
      @change="onChange"
      @mounted="onMounted"
      @validateStatusChange="onValidateStatusChange"
      :widgetsOfParent="widgetsOfParent"
      ref="widget"
    />
    <w-form-rich-text-editor
      v-else-if="widget.wtype == 'WidgetFormRichTextEditor'"
      :widget="widget"
      :parent="parent"
      :formObject="form"
      :formDataProp="formData"
      :formModelItem="formModelItemProp"
      :key="widget.wtype + '_' + widget.id"
      :isSubformCell="isSubformCell"
      :unValidateWidget="unValidateWidget"
      @change="onChange"
      @mounted="onMounted"
      @validateStatusChange="onValidateStatusChange"
      :widgetsOfParent="widgetsOfParent"
      ref="widget"
    />
    <w-form-input-number
      v-else-if="widget.wtype == 'WidgetFormInputNumber'"
      :widget="widget"
      :parent="parent"
      :formObject="form"
      :formDataProp="formData"
      :formModelItem="formModelItemProp"
      :key="widget.wtype + '_' + widget.id"
      :isSubformCell="isSubformCell"
      :unValidateWidget="unValidateWidget"
      @change="onChange"
      @mounted="onMounted"
      @validateStatusChange="onValidateStatusChange"
      :widgetsOfParent="widgetsOfParent"
      ref="widget"
    />
    <w-form-checkbox
      v-else-if="widget.wtype == 'WidgetFormCheckbox'"
      :widget="widget"
      :parent="parent"
      :formObject="form"
      :formDataProp="formData"
      :formModelItem="formModelItemProp"
      :key="widget.wtype + '_' + widget.id"
      :isSubformCell="isSubformCell"
      :unValidateWidget="unValidateWidget"
      @change="onChange"
      @mounted="onMounted"
      @validateStatusChange="onValidateStatusChange"
      :widgetsOfParent="widgetsOfParent"
      ref="widget"
    />
    <w-form-radio
      v-else-if="widget.wtype == 'WidgetFormRadio'"
      :widget="widget"
      :parent="parent"
      :formObject="form"
      :formDataProp="formData"
      :formModelItem="formModelItemProp"
      :key="widget.wtype + '_' + widget.id"
      :isSubformCell="isSubformCell"
      :unValidateWidget="unValidateWidget"
      @change="onChange"
      @mounted="onMounted"
      @validateStatusChange="onValidateStatusChange"
      :widgetsOfParent="widgetsOfParent"
      ref="widget"
    />
    <w-form-datetime-picker
      v-else-if="widget.wtype == 'WidgetFormDatePicker'"
      :widget="widget"
      :parent="parent"
      :formObject="form"
      :formDataProp="formData"
      :formModelItem="formModelItemProp"
      :key="widget.wtype + '_' + widget.id"
      :isSubformCell="isSubformCell"
      :unValidateWidget="unValidateWidget"
      @change="onChange"
      @mounted="onMounted"
      @validateStatusChange="onValidateStatusChange"
      :widgetsOfParent="widgetsOfParent"
      ref="widget"
    />
    <w-form-file-upload
      v-else-if="widget.wtype == 'WidgetFormFileUpload'"
      :widget="widget"
      :parent="parent"
      :formObject="form"
      :formDataProp="formData"
      :formModelItem="formModelItemProp"
      :key="widget.wtype + '_' + widget.id"
      :isSubformCell="isSubformCell"
      :unValidateWidget="unValidateWidget"
      @change="onChange"
      @mounted="onMounted"
      @validateStatusChange="onValidateStatusChange"
      :widgetsOfParent="widgetsOfParent"
      ref="widget"
    />
    <w-form-serial-number
      v-else-if="widget.wtype == 'WidgetFormSerialNumber'"
      :widget="widget"
      :parent="parent"
      :formObject="form"
      :formDataProp="formData"
      :formModelItem="formModelItemProp"
      :key="widget.wtype + '_' + widget.id"
      :isSubformCell="isSubformCell"
      :unValidateWidget="unValidateWidget"
      @change="onChange"
      @mounted="onMounted"
      @validateStatusChange="onValidateStatusChange"
      :widgetsOfParent="widgetsOfParent"
      ref="widget"
    />
    <w-form-job-select
      v-else-if="widget.wtype == 'WidgetFormJobSelect'"
      :widget="widget"
      :parent="parent"
      :formObject="form"
      :formDataProp="formData"
      :formModelItem="formModelItemProp"
      :key="widget.wtype + '_' + widget.id"
      :isSubformCell="isSubformCell"
      :unValidateWidget="unValidateWidget"
      @change="onChange"
      @mounted="onMounted"
      @validateStatusChange="onValidateStatusChange"
      :widgetsOfParent="widgetsOfParent"
      ref="widget"
    />
    <w-form-select
      v-else-if="widget.wtype == 'WidgetFormSelect'"
      :widget="widget"
      :parent="parent"
      :formObject="form"
      :formDataProp="formData"
      :formModelItem="formModelItemProp"
      :key="widget.wtype + '_' + widget.id"
      :isSubformCell="isSubformCell"
      :unValidateWidget="unValidateWidget"
      @change="onChange"
      @mounted="onMounted"
      @validateStatusChange="onValidateStatusChange"
      :widgetsOfParent="widgetsOfParent"
      ref="widget"
    />
    <w-form-subform
      v-else-if="widget.wtype == 'WidgetSubform'"
      :widget="widget"
      :parent="parent"
      :formObject="form"
      :formDataProp="formData"
      :formModelItem="formModelItemProp"
      :key="widget.wtype + '_' + widget.id"
      :isSubformCell="isSubformCell"
      @change="onChange"
      @mounted="onMounted"
      @validateStatusChange="onValidateStatusChange"
      :widgetsOfParent="widgetsOfParent"
      ref="widget"
    />
    <w-form-switch
      v-else-if="widget.wtype == 'WidgetFormSwitch'"
      :widget="widget"
      :parent="parent"
      :formObject="form"
      :formDataProp="formData"
      :formModelItem="formModelItemProp"
      :key="widget.wtype + '_' + widget.id"
      :isSubformCell="isSubformCell"
      :unValidateWidget="unValidateWidget"
      @change="onChange"
      @mounted="onMounted"
      @validateStatusChange="onValidateStatusChange"
      :widgetsOfParent="widgetsOfParent"
      ref="widget"
    />
    <w-form-org-select
      v-else-if="widget.wtype == 'WidgetFormOrgSelect'"
      :widget="widget"
      :parent="parent"
      :formObject="form"
      :formDataProp="formData"
      :formModelItem="formModelItemProp"
      :key="widget.wtype + '_' + widget.id"
      :isSubformCell="isSubformCell"
      :unValidateWidget="unValidateWidget"
      @change="onChange"
      @mounted="onMounted"
      @validateStatusChange="onValidateStatusChange"
      :widgetsOfParent="widgetsOfParent"
      ref="widget"
    />
    <w-form-numerical-bar
      v-else-if="widget.wtype == 'WidgetFormNumericalBar'"
      :widget="widget"
      :parent="parent"
      :formObject="form"
      :formDataProp="formData"
      :formModelItem="formModelItemProp"
      :key="widget.wtype + '_' + widget.id"
      :isSubformCell="isSubformCell"
      :unValidateWidget="unValidateWidget"
      @change="onChange"
      @mounted="onMounted"
      @validateStatusChange="onValidateStatusChange"
      :widgetsOfParent="widgetsOfParent"
      ref="widget"
    ></w-form-numerical-bar>
    <w-form-color
      v-else-if="widget.wtype == 'WidgetFormColor'"
      :widget="widget"
      :parent="parent"
      :formObject="form"
      :formDataProp="formData"
      :formModelItem="formModelItemProp"
      :isSubformCell="isSubformCell"
      :unValidateWidget="unValidateWidget"
      :key="widget.wtype + '_' + widget.id"
      @change="onChange"
      @mounted="onMounted"
      @validateStatusChange="onValidateStatusChange"
      :widgetsOfParent="widgetsOfParent"
      ref="widget"
    ></w-form-color>
    <w-form-text
      v-else-if="widget.wtype == 'WidgetFormText'"
      :widget="widget"
      :parent="parent"
      :formObject="form"
      :formDataProp="formData"
      :formModelItem="formModelItemProp"
      :isSubformCell="isSubformCell"
      :key="widget.wtype + '_' + widget.id"
      :widgetsOfParent="widgetsOfParent"
      ref="widget"
    ></w-form-text>
    <w-form-cascader
      v-else-if="widget.wtype == 'WidgetFormCascader'"
      :widget="widget"
      :parent="parent"
      :formObject="form"
      :formDataProp="formData"
      :formModelItem="formModelItemProp"
      :isSubformCell="isSubformCell"
      @change="onChange"
      @mounted="onMounted"
      @validateStatusChange="onValidateStatusChange"
      :key="widget.wtype + '_' + widget.id"
      :widgetsOfParent="widgetsOfParent"
      ref="widget"
    ></w-form-cascader>
    <w-form-ref-field
      v-else-if="widget.wtype == 'WidgetRefFormFieldPlaceholder'"
      :widget="widget"
      :parent="parent"
      :formObject="form"
      :formDataProp="formData"
      :widgetsOfParent="widgetsOfParent"
      :key="widget.wtype + '_' + widget.id"
    ></w-form-ref-field>
    <!-- 表单组件 end -->
  </view>
</template>

<script>
export default {
  name: "widget",
  props: {
    id: String,
    widget: {
      type: Object,
      required: true,
    },
    parent: Object,
    form: Object,
    formData: Object,
    widgetsOfParent: Array, // 父组件的所有子组件
    formModelItemProp: String | Array,
    isSubformCell: Boolean,
    unValidateWidget: Boolean, // 从表内代理行数据组件(用于分页数据校验)，设值不触发字段变更校验
  },
  components: {},
  data() {
    return {};
  },
  created() {},
  mounted() {},
  methods: {
    onChange() {
      let args = ["change"];
      args.push(...Array.from(arguments));
      this.$emit.apply(this, args);
    },
    onMounted() {
      let args = ["mounted"];
      args.push(...Array.from(arguments));
      this.$emit.apply(this, args);
    },
    onValidateStatusChange() {
      let args = ["validateStatusChange"];
      args.push(...Array.from(arguments));
      this.$emit.apply(this, args);
    },
  },
};
</script>

<style></style>
