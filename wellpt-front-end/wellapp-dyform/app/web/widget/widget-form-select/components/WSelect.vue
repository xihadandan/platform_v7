<template>
  <!-- 下拉框组件 -->
  <div
    @mousedown="
      e => {
        e.preventDefault();
        this.selectOpen = true;
        return false;
      }
    "
  >
    <a-select
      ref="select"
      default-value="lucy"
      style="width: 100%"
      :mode="mode"
      :autoFocus="true"
      dropdownClassName="well-select-dropdown"
      @focus="focusSelect"
      @blur="blurSelect"
      @select="handleSelect"
      @dropdownVisibleChange="dropdownSelect"
    >
      <div slot="dropdownRender" slot-scope="menu">
        <div style="padding: 4px 8px; cursor: pointer; display: flex">
          <a-input ref="newOption" v-model="newOption" @click="handleNewOption" @pressEnter="addNewOption">
            <div slot="addonAfter" @click="addNewOption">{{ $t('WidgetFormSelect.add', '添加') }}</div>
          </a-input>
        </div>
        <a-divider style="margin: 4px 0" />
        <v-nodes :vnodes="menu" />
      </div>
      <a-select-option v-for="item in items" :key="item" :value="item">
        {{ item }}
      </a-select-option>
    </a-select>
  </div>
</template>

<script>
import { optionSourceTypes } from '../../../page/dyform-designer/component/widget-editor/constant';
export default {
  name: 'WSelect',
  inject: ['widgetContext'],
  props: {
    showCheckAll: {
      // 全选
      type: Boolean,
      default: false
    },
    multiple: {
      // 多选
      type: Boolean,
      default: true
    },
    allowClear: {
      // 清空
      type: Boolean,
      default: true
    },
    showSearch: {
      // 搜索
      type: Boolean,
      default: true
    },
    showMore: {
      // 显示选项更多操作
      type: Boolean,
      default: false
    },
    optionAdd: {
      // 新增备选项
      type: Boolean,
      default: true
    },
    optionSort: {
      // 备选项排序
      type: Boolean,
      default: true
    },
    optionDel: {
      // 删除备选项
      type: Boolean,
      default: true
    }
  },

  data() {
    return {
      optionSourceTypes,
      items: ['jack', 'lucy'],
      selectOpen: false,
      newfocus: false,
      selectfocus: false,
      newOption: ''
    };
  },
  components: {
    VNodes: {
      functional: true,
      render: (h, ctx) => ctx.props.vnodes
    }
  },
  computed: {
    mode() {
      return this.multiple ? 'multiple' : 'default';
    }
  },
  methods: {
    $t() {
      if (this.widgetContext != undefined) {
        return this.widgetContext.$t(...arguments);
      }
      return this.$i18n.t(...arguments);
    },
    focusCustom() {
      console.log('Custom获取焦点');
    },
    blurCustom() {
      console.log('Custom失去焦点');
    },
    focusSelect() {
      console.log('Select获取焦点');
    },
    blurSelect() {
      console.log('Select失去焦点');
      if (this.newfocus) {
        return;
      }
      this.selectOpen = false;
    },
    focusNewOption() {
      console.log('NewOption获取焦点');
      this.newfocus = true;
    },

    blurNewOption() {
      console.log('NewOption失去焦点');
      this.newfocus = false;
      this.selectOpen = false;
    },

    handleNewOption(e) {
      console.log('NewOption点击');
      e.target.focus();
      // this.$refs.select.focus();
    },
    addNewOption() {
      if (!this.newOption) {
        return;
      }
      console.log('addItem');
      this.items.push(this.newOption);
      this.newOption = '';
    },
    handleSelect(value) {
      console.log('Select选中', value);
      if (this.multiple) {
        this.selectOpen = true;
      } else {
        this.selectOpen = false;
      }
    },
    dropdownSelect(state) {
      console.log('打开状态', state);
    }
  }
};
</script>
