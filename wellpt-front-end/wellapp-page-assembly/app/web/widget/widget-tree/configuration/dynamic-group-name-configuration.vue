<template>
  <div>
    <a-form-model-item>
      <template slot="label">
        当按钮超过
        <a-input-number v-model="button.buttonGroup.dynamicGroupBtnThreshold" :min="0" style="width: 60px" size="small" />
        个时进行分组
      </template>
    </a-form-model-item>
    <a-form-model-item label="分组名称">
      <a-input v-model="button.buttonGroup.dynamicGroupName">
        <template slot="addonAfter" v-if="button.buttonGroup.style != undefined">
          <WidgetDesignDrawer :id="'WidgetTreeBtnConfigDynamicGroupName'" title="分组按钮设置" :designer="designer">
            <a-button type="link" size="small" title="分组按钮设置">
              <Icon type="pticon iconfont icon-ptkj-shezhi"></Icon>
            </a-button>
            <template slot="content">
              <a-form-model>
                <a-form-model-item label="名称">
                  <a-input v-model="button.buttonGroup.dynamicGroupName">
                    <template slot="addonAfter">
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
                      <WI18nInput
                        v-show="button.buttonGroup.style.textHidden !== true"
                        :widget="widget"
                        :target="button.buttonGroup"
                        :designer="designer"
                        :code="widget.id + '_dynamicGroupName'"
                        v-model="button.buttonGroup.dynamicGroupName"
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

                <!-- <a-form-model-item label="右侧下拉箭头">
                  <a-switch v-model="button.buttonGroup.style.rightDownIconVisible" />
                </a-form-model-item> -->
              </a-form-model>
            </template>
          </WidgetDesignDrawer>
          <WI18nInput
            v-show="button.buttonGroup.style.textHidden !== true"
            :widget="widget"
            :target="button.buttonGroup"
            :designer="designer"
            :code="widget.id + '_dynamicGroupName'"
            v-model="button.buttonGroup.dynamicGroupName"
          />
        </template>
      </a-input>
    </a-form-model-item>
  </div>
</template>
<script type="text/babel">
import { generateId, deepClone } from '@framework/vue/utils/util';
export default {
  name: 'DynamicGroupNameConfiguration',
  mixins: [],
  props: {
    widget: Object,
    designer: Object,
    button: Object
  },
  data() {
    return {};
  },

  beforeCreate() {},
  components: {},
  computed: {},
  created() {},
  methods: {},
  mounted() {}
};
</script>
