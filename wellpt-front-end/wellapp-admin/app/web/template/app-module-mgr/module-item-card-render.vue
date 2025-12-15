<template>
  <div class="module-item-card-render">
    <div style="width: 100%; height: 100%">
      <a-row type="flex" style="height: 74px; flex-wrap: nowrap">
        <a-col flex="70px">
          <a-avatar
            shape="square"
            :size="48"
            :style="{
              background: moduleIcon.bgColor
                ? moduleIcon.bgColor.startsWith('--')
                  ? 'var(' + moduleIcon.bgColor + ')'
                  : moduleIcon.bgColor
                : 'var(--w-primary-color)',
              borderRadius: '8px'
            }"
          >
            <Icon slot="icon" :type="moduleIcon.icon || 'pticon iconfont icon-ptkj-zaitechengjian'" style="font-size: 32px" />
          </a-avatar>
        </a-col>
        <a-col flex="auto">
          <div class="w-ellipsis-1 card-title" style="width: calc(100% - 20px)">
            <slot name="NAMESlot"></slot>
          </div>
          <div class="w-ellipsis-1 card-description">
            <slot name="REMARKSlot"></slot>
          </div>
        </a-col>
      </a-row>
      <a-row>
        <a-col :span="12"></a-col>
        <a-col :span="12" style="display: flex; justify-content: flex-end">
          <slot name="actions" />
        </a-col>
      </a-row>
    </div>
  </div>
</template>
<style lang="less">
.module-item-card-render {
  height: 127px;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px 20px;
  border-radius: var(--w-card-border-radius);
  .card-title {
    color: var(--w-text-color-dark);
    font-size: var(--w-font-size-base);
    font-weight: bold;
    line-height: 32px;
    margin-bottom: 4px;
  }

  .card-description {
    white-space: nowrap;
    text-overflow: ellipsis;
    overflow: hidden;
    color: var(--w-text-color-light);
  }
}
</style>
<script type="text/babel">
export default {
  name: 'ModuleItemCardRender',
  inject: ['widgetTableContext'],
  props: {
    row: Object,
    rowIndex: Number
  },
  components: {},
  computed: {
    moduleIcon() {
      return this.iconDataToJson(this.row.ICON);
    }
  },
  data() {
    return {};
  },
  beforeCreate() {},
  created() {},
  beforeMount() {},
  mounted() {},
  methods: {
    iconDataToJson(data) {
      if (!data) {
        data = {
          icon: '',
          bgColor: ''
        };
      } else {
        try {
          let iconJson = JSON.parse(data);
          if (iconJson) {
            data = iconJson;
          }
        } catch (e) {
          if (typeof data == 'string') {
            let iconJson = {
              icon: data,
              bgColor: ''
            };
            data = iconJson;
          }
          return data;
        }
      }
      return data;
    }
  }
};
</script>
