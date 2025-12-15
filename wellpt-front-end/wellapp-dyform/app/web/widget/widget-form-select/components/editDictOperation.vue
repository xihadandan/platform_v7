<template>
  <!-- 选项右侧操作-->
  <a-popconfirm
    ref="editDictOperationPopoverRef"
    overlayClassName="edit-dict-operation-select"
    placement="right"
    :trigger="trigger"
    :overlayStyle="{
      width: '300px'
    }"
    @visibleChange="visibleChange"
  >
    <template #title>
      <div v-if="optionSort || optionDel" class="edit-dict-operation-select-operation">
        <a-button
          type="link"
          size="small"
          icon="arrow-up"
          v-if="optionSort && currentIndex > 0"
          :title="$t('WidgetFormSelect.moveUp', '上移')"
          @click.stop="sortClick('forward')"
        />
        <a-button
          type="link"
          size="small"
          icon="arrow-down"
          v-if="optionSort && selectOptions.length - 1 > currentIndex"
          :title="$t('WidgetFormSelect.moveDown', '下移')"
          @click.stop="sortClick()"
        />
        <a-button
          type="link"
          size="small"
          icon="delete"
          v-if="optionDel && selectOptions.length > 0"
          :title="$t('WidgetFormSelect.delete', '删除')"
          @click.stop="delClick"
        />
      </div>
      <div v-if="optionAdd" class="edit-dict-operation-select-add">
        <a-input-group compact>
          <a-input
            v-model="value"
            @compositionstart="onCompositionstart"
            @compositionend="onCompositionend"
            style="width: calc(100% - 68px)"
            allowClear
          />
          <a-button type="primary" @click.stop="addClick" :disabled="!value.trim()">{{ $t('WidgetFormSelect.add', '添加') }}</a-button>
        </a-input-group>
      </div>
    </template>
    <a-icon type="more" v-if="showMore" class="edit-dict-operation-select-more-icon" :title="$t('WidgetFormSelect.more', '更多')" />
  </a-popconfirm>
</template>

<script>
import { generateId } from '@framework/vue/utils/util';
export default {
  name: 'editDictOperation',
  inject: ['widgetContext'],
  props: {
    // 当前选项
    current: {
      type: Object,
      default: {}
    },
    // 当前索引
    currentIndex: {
      type: Number,
      default: 0
    },
    parentIndex: {
      type: Number
    },
    // 全部选项
    selectOptions: {
      type: Array,
      default: []
    },
    // 配置
    configuration: {
      type: Object,
      default: []
    }
  },

  data() {
    return {
      value: '',
      trigger: 'contextmenu'
    };
  },
  computed: {
    // 显示更多操作
    showMore() {
      if (this.configuration && this.configuration.editMode) {
        let showMore = this.configuration.editMode.showMore;
        return showMore && (this.optionAdd || this.optionSort || this.optionDel);
      }
      return false;
    },
    // 添加
    optionAdd() {
      return this.configuration && this.configuration.editMode && this.configuration.editMode.optionAdd;
    },
    // 排序
    optionSort() {
      return this.configuration && this.configuration.editMode && this.configuration.editMode.optionSort;
    },
    // 删除
    optionDel() {
      return this.configuration && this.configuration.editMode && this.configuration.editMode.optionDel;
    },
    newOption() {
      let newOption = {
        value: '',
        label: ''
      };
      if (this.configuration && this.configuration.option) {
        let dataDictionaryUuid = this.configuration.option.dataDictionaryUuid;
        let length = this.selectOptions.length;
        // newOption.value = dataDictionaryUuid + (length + 1);
      }
      return newOption;
    }
  },
  mounted() {},
  methods: {
    $t() {
      if (this.widgetContext != undefined) {
        return this.widgetContext.$t(...arguments);
      }
      return this.$i18n.t(...arguments);
    },
    onCompositionstart() {
      // this.trigger = 'click'
    },
    onCompositionend() {
      // this.trigger = 'hover'
    },
    visibleChange(visible) {
      if (visible) {
        this.$emit('editMoreItemChange', this.current.value);
      }
      this.$emit('visible', visible);
    },
    getPopupContainer() {
      return triggerNode => {
        if (triggerNode.closest('.widget-subform')) {
          return triggerNode.closest('.widget-subform');
        }
        return triggerNode.parentNode;
      };
    },
    sortClick(direction) {
      this.$emit('sortOption', {
        direction,
        item: this.current,
        index: this.currentIndex,
        parentIndex: this.parentIndex,
        parentUuid: this.current.parentUuid
      });
    },
    delClick() {
      this.$emit('delOption', {
        item: this.current,
        index: this.currentIndex,
        parentIndex: this.parentIndex,
        parentUuid: this.current.parentUuid
      });
    },
    addClick() {
      let parentUuid = null;
      if (this.current.parentUuid) {
        parentUuid = this.current.parentUuid;
      }
      this.$emit('addOption', {
        label: this.value,
        value: generateId(),
        item: this.current,
        index: this.currentIndex,
        parentIndex: this.parentIndex,
        parentUuid
      });
    }
  }
};
</script>

<style lang="less">
.edit-dict-operation-select {
  .ant-popover-message {
    padding: 0;
    > i {
      display: none;
    }
  }
  .ant-popover-message-title {
    padding-left: 0;
  }
  .ant-popover-buttons {
    display: none;
  }
}
</style>
