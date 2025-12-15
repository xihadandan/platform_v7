<template>
  <a-form-model ref="form" :model="formData" :rules="rules" :colon="false">
    <a-form-model-item label="ID">
      {{ formData.id }}
    </a-form-model-item>
    <a-form-model-item prop="src" label="图片">
      <ImageLibrary v-model="formData.src" :key="formData.id" />
    </a-form-model-item>
    <a-form-model-item label="图片比例适应">
      <a-select v-model="formData.size" :options="bgSizeOptions" />
    </a-form-model-item>
    <a-form-model-item label="显示按钮">
      <a-switch v-model="formData.buttonEnable" />
    </a-form-model-item>
    <template v-if="formData.buttonEnable">
      <a-form-model-item label="按钮">
        <button-list-table :dataSource="formData.buttons" :enableTable="true" />
      </a-form-model-item>
      <a-form-model-item label="按钮分组" class="item-lh">
        <a-radio-group size="small" v-model="formData.buttonGroup.type" button-style="solid">
          <a-radio-button v-for="item in buttonGroupOptions" :key="item.value" :value="item.value">
            {{ item.label }}
          </a-radio-button>
        </a-radio-group>

        <template v-if="formData.buttonGroup.type === 'fixedGroup'">
          <button-fixed-group :dataSource="formData.buttonGroup.groups" :buttons="formData.buttons" />
        </template>
      </a-form-model-item>
      <template v-if="formData.buttonGroup.type === 'dynamicGroup'">
        <a-form-model-item label="分组名称">
          <a-input v-model="formData.buttonGroup.dynamicGroupName" allowClear>
            <template slot="addonAfter">
              <WI18nInput
                :widget="widget"
                :target="formData"
                :designer="designer"
                :code="formData.id + '_dynamicGroupName'"
                v-model="formData.buttonGroup.dynamicGroupName"
              />
            </template>
          </a-input>
        </a-form-model-item>
        <a-form-model-item label="分组按钮数">
          <a-input-number v-model="formData.buttonGroup.dynamicGroupBtnThreshold" />
        </a-form-model-item>
      </template>
      <a-form-model-item label="按钮位置">
        <div>
          水平方向
          <w-input-number v-model="formData.buttonStyle.xAxis" style="width: auto">
            <template slot="addonBefore">
              <a-select v-model="formData.buttonStyle.xAxisDir" :options="xAxisDirOptions" style="width: 100px" />
            </template>
            <template slot="addonAfter">
              <a-select v-model="formData.buttonStyle.xAxisUnit" :options="lengthUnitOptions" style="width: 80px" />
            </template>
          </w-input-number>
        </div>
        <div>
          垂直方向
          <w-input-number v-model="formData.buttonStyle.yAxis" style="width: auto">
            <template slot="addonBefore">
              <a-select v-model="formData.buttonStyle.yAxisDir" :options="yAxisDirOptions" style="width: 100px" />
            </template>
            <template slot="addonAfter">
              <a-select v-model="formData.buttonStyle.yAxisUnit" :options="lengthUnitOptions" style="width: 80px" />
            </template>
          </w-input-number>
        </div>
      </a-form-model-item>
    </template>
  </a-form-model>
</template>

<script>
import { bgSizeOptions, buttonGroupOptions, xAxisDirOptions, yAxisDirOptions, lengthUnitOptions } from './constant';
import ImageLibrary from '@pageAssembly/app/web/widget/commons/image-library.vue';
import ButtonListTable from './button-list-table.vue';
import ButtonFixedGroup from './button-fixed-group.vue';
import WInputNumber from '../components/w-input-number/index';

export default {
  name: 'CarouselInfo',
  inject: ['designer', 'widget'],
  props: {
    formData: {
      type: Object,
      default: () => {}
    }
  },
  data() {
    return {
      bgSizeOptions,
      buttonGroupOptions,
      xAxisDirOptions,
      yAxisDirOptions,
      lengthUnitOptions,
      rules: {
        src: { required: true, message: '请选择或上传图片' }
      }
    };
  },
  components: {
    WInputNumber,
    ImageLibrary,
    ButtonListTable,
    ButtonFixedGroup
  },
  methods: {
    validate(callback) {
      this.$refs.form.validate((valid, error) => {
        callback({ valid, error, data: this.formData });
      });
    }
  }
};
</script>
