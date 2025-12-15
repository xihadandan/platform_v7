<template>
  <view>
    <!-- 页面组件 start -->
    <w-tab-bar v-if="widget.wtype == 'WidgetUniTabBar'" :widget="widget" :parent="parent"></w-tab-bar>
    <w-swiper v-else-if="widget.wtype == 'WidgetUniSwiper'" :widget="widget" :parent="parent"></w-swiper>
    <w-grid-view v-else-if="widget.wtype == 'WidgetUniGridView'" :widget="widget" :parent="parent"></w-grid-view>
    <w-nav v-else-if="widget.wtype == 'WidgetUniNav'" :widget="widget" :parent="parent"></w-nav>
    <w-list-view v-else-if="widget.wtype == 'WidgetUniListView'" :widget="widget" :parent="parent"></w-list-view>
    <w-tabs
      v-else-if="widget.wtype == 'WidgetUniTab' || widget.wtype == 'WidgetTab'"
      :widget="widget"
      :parent="parent"
    ></w-tabs>
    <w-html v-else-if="widget.wtype == 'WidgetUniHtml'" :widget="widget" :parent="parent"></w-html>
    <w-tiles v-else-if="widget.wtype == 'WidgetUniTiles'" :widget="widget" :parent="parent"></w-tiles>
    <w-title-section v-else-if="widget.wtype == 'WidgetUniSection'" :widget="widget" :parent="parent"></w-title-section>
    <w-tip v-else-if="widget.wtype == 'WidgetTip'" :widget="widget" :parent="parent"></w-tip>
    <w-video v-else-if="widget.wtype == 'WidgetVideo'" :widget="widget" :parent="parent"></w-video>
    <w-button-group v-else-if="widget.wtype == 'WidgetButton'" :widget="widget" :parent="parent"></w-button-group>
    <w-template v-else-if="widget.wtype == 'WidgetTemplate'" :widget="widget" :parent="parent"> </w-template>
    <w-grid v-else-if="widget.wtype == 'WidgetGrid'" :widget="widget" :parent="parent"> </w-grid>
    <w-card v-else-if="widget.wtype == 'WidgetCard'" :widget="widget" :parent="parent"> </w-card>

    <!-- 页面组件 end -->

    <!-- 表单组件 start -->
    <w-form-layout
      v-else-if="widget.wtype == 'WidgetFormLayout'"
      :widget="widget"
      :parent="parent"
      :form="form"
      :formData="formData"
      :formModelItemProp="formModelItemProp"
    ></w-form-layout>
    <w-form-input
      v-else-if="widget.wtype == 'WidgetFormInput'"
      :widget="widget"
      :parent="parent"
      :form="form"
      :formData="formData"
      :formModelItemProp="formModelItemProp"
      ref="widget"
    />
    <w-form-rich-text-editor
      v-else-if="widget.wtype == 'WidgetFormRichTextEditor'"
      :widget="widget"
      :parent="parent"
      :form="form"
      :formData="formData"
      :formModelItemProp="formModelItemProp"
      ref="widget"
    />
    <w-form-input-number
      v-else-if="widget.wtype == 'WidgetFormInputNumber'"
      :widget="widget"
      :parent="parent"
      :form="form"
      :formData="formData"
      :formModelItemProp="formModelItemProp"
      ref="widget"
    />
    <w-form-checkbox
      v-else-if="widget.wtype == 'WidgetFormCheckbox'"
      :widget="widget"
      :parent="parent"
      :form="form"
      :formData="formData"
      :formModelItemProp="formModelItemProp"
      ref="widget"
    />
    <w-form-radio
      v-else-if="widget.wtype == 'WidgetFormRadio'"
      :widget="widget"
      :parent="parent"
      :form="form"
      :formData="formData"
      :formModelItemProp="formModelItemProp"
      @change="onChange"
      ref="widget"
    />
    <w-form-datetime-picker
      v-else-if="widget.wtype == 'WidgetFormDatePicker'"
      :widget="widget"
      :parent="parent"
      :form="form"
      :formData="formData"
      :formModelItemProp="formModelItemProp"
      @change="onChange"
      ref="widget"
    />
    <w-form-file-upload
      v-else-if="widget.wtype == 'WidgetFormFileUpload'"
      :widget="widget"
      :parent="parent"
      :form="form"
      :formData="formData"
      :formModelItemProp="formModelItemProp"
      @change="onChange"
      ref="widget"
    />
    <w-form-serial-number
      v-else-if="widget.wtype == 'WidgetFormSerialNumber'"
      :widget="widget"
      :parent="parent"
      :form="form"
      :formData="formData"
      :formModelItemProp="formModelItemProp"
      @change="onChange"
      ref="widget"
    />
    <w-form-job-select
      v-else-if="widget.wtype == 'WidgetFormJobSelect'"
      :widget="widget"
      :parent="parent"
      :form="form"
      :formData="formData"
      :formModelItemProp="formModelItemProp"
      @change="onChange"
      ref="widget"
    />
    <w-form-select
      v-else-if="widget.wtype == 'WidgetFormSelect'"
      :widget="widget"
      :parent="parent"
      :form="form"
      :formData="formData"
      :formModelItemProp="formModelItemProp"
      @change="onChange"
      ref="widget"
    />
    <w-form-subform
      v-else-if="widget.wtype == 'WidgetSubform'"
      :widget="widget"
      :parent="parent"
      :form="form"
      :formData="formData"
      :formModelItemProp="formModelItemProp"
      @change="onChange"
      ref="widget"
    />
    <w-form-switch
      v-else-if="widget.wtype == 'WidgetFormSwitch'"
      :widget="widget"
      :parent="parent"
      :form="form"
      :formData="formData"
      :formModelItemProp="formModelItemProp"
      @change="onChange"
      ref="widget"
    />
    <w-form-org-select
      v-else-if="widget.wtype == 'WidgetFormOrgSelect'"
      :widget="widget"
      :parent="parent"
      :form="form"
      :formData="formData"
      :formModelItemProp="formModelItemProp"
      @change="onChange"
      ref="widget"
    />
    <w-form-numerical-bar
      v-else-if="widget.wtype == 'WidgetFormNumericalBar'"
      :widget="widget"
      :parent="parent"
      :form="form"
      :formData="formData"
      :formModelItemProp="formModelItemProp"
      @change="onChange"
      ref="widget"
    ></w-form-numerical-bar>
    <w-form-color
      v-else-if="widget.wtype == 'WidgetFormColor'"
      :widget="widget"
      :parent="parent"
      :form="form"
      :formData="formData"
      :formModelItemProp="formModelItemProp"
      @change="onChange"
      ref="widget"
    ></w-form-color>
    <!-- 表单组件 end -->
  </view>
</template>

<script>
export default {
  name: "w-widget",
  props: {
    id: String,
    widget: {
      type: Object,
      required: true,
    },
    parent: Object,
    form: Object,
    formData: Object,
    formModelItemProp: String,
  },
  components: {
    "w-tabs": () => import("../w-tabs/w-tabs.vue"),
    wFormLayout: () => import("../w-form-layout/w-form-layout.vue"),
  },
  methods: {
    onChange() {
      let args = ["change"];
      args.push(...Array.from(arguments));
      this.$emit.apply(this, args);
    },
  },
};
</script>

<style></style>
