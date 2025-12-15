<template>
  <div>
    <a-form-model :model="widget.configuration" labelAlign="left" :wrapper-col="{ style: { textAlign: 'right' } }" :colon="false">
      <a-tabs default-active-key="1">
        <a-tab-pane key="1" tab="设置">
          <a-form-model-item label="列占位数" v-if="parent != undefined">
            <a-input-number
              :min="1"
              :max="parent.configuration.column"
              v-model="widget.configuration.itemPosition.w"
              @change="onChangeItemPositionW"
            />
          </a-form-model-item>
          <a-form-model-item label="高度自适应内容">
            <a-switch v-model="widget.configuration.resizeToContent" />
          </a-form-model-item>
          <!-- <a-form-model-item label="高度">
            <a-input-number :min="50" v-model="widget.configuration.itemPosition.h" />
          </a-form-model-item> -->
          <!-- <a-form-model-item label="最小高度">
            <a-input-number :min="50" v-model="widget.configuration.itemPosition.minH" />
          </a-form-model-item> -->
          <a-form-model-item label="背景色">
            <ColorPicker v-model="widget.configuration.style.backgroundColor"></ColorPicker>
          </a-form-model-item>

          <a-form-model-item>
            <template slot="label">
              圆角半径
              <a-tooltip title="分别设置圆角半径">
                <a-checkbox :checked="vBorderRadiusChecked" @change="onChangeBorderRadius" />
              </a-tooltip>
            </template>
            <template v-if="vBorderRadiusChecked">
              <div style="display: flex; width: 200px; flex-wrap: wrap; float: right; align-items: center; justify-content: space-between">
                <template v-for="(arrItem, a) in widget.configuration.style.borderRadius">
                  <label style="margin-right: 5px">{{ ['左上', '右上', '右下', '左下'][a] }}</label>
                  <!-- <a-icon style="font-size: 32px" :type="'radius-' + ['upleft', 'upright', 'bottomright', 'bottomleft'][a]" /> -->
                  <a-input-number
                    v-model="widget.configuration.style.borderRadius[a]"
                    :min="0"
                    :key="'borderRadius' + a"
                    style="width: 55px; margin-right: 5px"
                  />
                </template>
              </div>
            </template>
            <a-input-number v-else v-model="widget.configuration.style.borderRadius" :min="0" style="width: 75px" />
          </a-form-model-item>
        </a-tab-pane>
        <!-- <a-tab-pane key="3" tab="样式设置">
          <StyleCodeConfiguration :configuration="widget.configuration" />
        </a-tab-pane> -->
      </a-tabs>
    </a-form-model>
  </div>
</template>
<style></style>
<script type="text/babel">
import ColorPicker from '@pageAssembly/app/web/widget/commons/color-picker.vue';

export default {
  name: 'GridstackItemConfiguration',
  inject: ['pageContext'],
  mixins: [],
  props: {
    widget: Object,
    designer: Object,
    parent: Object
  },
  data() {
    return {};
  },

  beforeCreate() {},
  components: { ColorPicker },
  computed: {
    vBorderRadiusChecked() {
      return Array.isArray(this.widget.configuration.style.borderRadius) && this.widget.configuration.style.borderRadius.length === 4;
    }
  },
  created() {},
  methods: {
    onChangeItemPositionW() {
      this.pageContext.emitEvent(`widget:${this.parent.id}:gridItemPositionChange`);
    },
    onChangeBorderRadius(e) {
      if (e.target.checked) {
        this.$set(
          this.widget.configuration.style,
          'borderRadius',
          Array.from({ length: 4 }, () => {
            return this.widget.configuration.style.borderRadius || 0;
          })
        );
      } else {
        this.$set(this.widget.configuration.style, 'borderRadius', this.widget.configuration.style.borderRadius[0] || 0);
      }
    }
  },
  mounted() {}
};
</script>
