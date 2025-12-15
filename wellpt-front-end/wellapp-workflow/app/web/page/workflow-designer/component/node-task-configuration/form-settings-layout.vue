<template>
  <!-- 环节属性-表单设置-表单布局 -->
  <div class="ant-form-item">
    <div style="flex: 1">
      <a-table class="form-settings-table" rowKey="value" :pagination="false" size="small" :columns="columns" :dataSource="dataSource">
        <template slot="title">
          <div class="flex f_x_s">
            <div>
              <a-tooltip placement="topRight" :arrowPointAtCenter="true">
                <div slot="title">设置当前环节的办理人可见的表单布局，例如标签页、表单布局等</div>
                <label class="form-settings-table-title">
                  表单布局
                  <a-icon type="exclamation-circle" />
                </label>
              </a-tooltip>
            </div>
            <div style="text-align: right">
              <form-setting-search @search="onSearch"></form-setting-search>
            </div>
          </div>
        </template>
        <template slot="titleSlot" slot-scope="text, record">
          <i v-if="record.wtype === 'WidgetFormLayout'" :title="`${record.name}-${text}`" class="iconfont icon-ptkj-mokuaiqukuai" />
          <i v-else :title="`${record.name}-${text}`" class="iconfont icon-ptkj-xianxingyeqian" />
          <span v-html="textFilter(text)"></span>
        </template>
        <!-- 显示 -->
        <template slot="visibleTitle">
          <w-checkbox v-model="checkAll" :checkedValue="true" :unCheckedValue="false" @change="changeCheckAll">显示</w-checkbox>
        </template>
        <template slot="visibleSlot" slot-scope="text, record">
          <w-checkbox
            v-model="record.visible"
            :checkedValue="true"
            :unCheckedValue="false"
            @change="event => changeVisible(event, record)"
          />
        </template>
      </a-table>
    </div>
  </div>
</template>

<script>
import WCheckbox from '../components/w-checkbox';
import FormSettingSearch from './form-setting-search.vue';
export default {
  name: 'TaskFormSettingsLayout',
  inject: ['designer'],
  props: {
    value: {
      type: Array,
      default: () => []
    },
    formData: {
      type: Object,
      default: () => {}
    },
    formDefinition: {
      type: Object,
      default: () => {}
    },
    modified: {
      type: Boolean,
      default: true
    }
  },
  components: {
    WCheckbox,
    FormSettingSearch
  },
  data() {
    return {
      dataSource: [],
      columns: [
        { title: '布局名称', dataIndex: 'title', scopedSlots: { customRender: 'titleSlot' } },
        {
          dataIndex: 'visible',
          width: 80,
          slots: { title: 'visibleTitle' },
          scopedSlots: { customRender: 'visibleSlot' },
          align: 'left'
        }
      ],
      checkAll: false,
      hideItem: () => {
        return {
          type: 32,
          value: '',
          argValue: null
        };
      },
      searchVal: ''
    };
  },
  watch: {
    formDefinition: {
      deep: true,
      handler(formDefinition) {
        this.formData.hideBlocks = [];
        this.formData.hideTabs = [];
        this.dataSource = this.designer.getFormLayout(formDefinition);
        this.mergeDataSource();
      }
    }
  },
  computed: {
    hideData() {
      return this.formData.hideBlocks.concat(this.formData.hideTabs);
    }
  },
  created() {
    this.dataSource = this.designer.getFormLayout(this.formDefinition);
    this.mergeDataSource();
  },
  methods: {
    // 获取表单布局数据,表单设置的是否隐藏显示
    getDataSource(formDefinition) {
      let dataSource = [];
      if (!formDefinition) {
        return dataSource;
      }
      const { widgets } = formDefinition;
      if (widgets && widgets.length) {
        const formatDatas = datas => {
          if (datas && datas.length) {
            for (let index = 0; index < datas.length; index++) {
              const item = datas[index];
              let visible = item.configuration.defaultVisible;
              if (item.wtype === 'WidgetFormLayout') {
                // if (!visible) {
                //   // 和62一致表单隐藏的布局不显示在列表
                //   continue;
                // }
                // 布局
                dataSource.push({
                  title: item.title,
                  value: item.id,
                  wtype: item.wtype,
                  name: item.name,
                  visible
                });
              } else if (item.wtype === 'WidgetTab') {
                // 标签
                const tabs = item.configuration.tabs;
                for (let i = 0; i < tabs.length; i++) {
                  const tab = tabs[i];
                  let visible = tab.configuration.defaultVisible !== undefined ? tab.configuration.defaultVisible : true;
                  dataSource.push({
                    title: tab.title,
                    value: tab.id,
                    wtype: tab.wtype, // WidgetTabItem
                    name: '页签',
                    visible
                  });
                  formatDatas(tab.configuration.widgets);
                }
              }
              // else if (item.wtype === 'WidgetCard') {
              //   // 卡片
              //   dataSource.push({
              //     title: item.title,
              //     value: item.id,
              //     wtype: item.wtype,
              //     visible
              //   });
              // }
              // WidgetAnchor:锚点 WidgetStatePanel:状态面板 表单上没有显示设置
              formatDatas(item.configuration.widgets);
            }
          }
        };
        formatDatas(widgets);
      }
      return dataSource;
    },
    mergeDataSource() {
      const hideData = this.hideData;
      if (this.modified) {
        // 修改过
        this.dataSource.forEach(item => {
          const hideItem = hideData.find(h => h.value === item.value);
          if (hideItem) {
            // 流程已设置隐藏，以流程设置的为主
            item.visible = false;
          } else {
            // 表单设置时为隐藏的，流程设置非隐藏的，要设置为显示
            if (!item.visible) {
              item.visible = true;
            }
          }
        });
      } else {
        // 未修改过
        this.dataSource.forEach(item => {
          if (!item.visible) {
            // 表单设置的隐藏向流程添加
            this.addHideItem(item);
          }
        });
      }

      this.setCheckAll();
    },
    // 更改全选按钮
    changeCheckAll(event) {
      const targetChecked = event.target.checked;
      this.formData.hideBlocks = [];
      this.formData.hideTabs = [];
      if (targetChecked) {
        // 全显示
        this.dataSource.forEach(item => {
          item.visible = true;
        });
      } else {
        // 全隐藏
        this.dataSource.forEach(item => {
          item.visible = false;
          this.addHideItem(item);
        });
      }
    },
    changeVisible(event, item) {
      const targetChecked = event.target.checked;
      if (targetChecked) {
        this.delHideItem(item);
      } else {
        this.addHideItem(item);
      }
      this.setCheckAll();
    },
    setCheckAll() {
      const hideIndex = this.dataSource.findIndex(item => !item.visible);
      if (hideIndex !== -1) {
        this.checkAll = false;
      } else {
        this.checkAll = true;
      }
    },
    // 添加隐藏布局
    addHideItem(item) {
      let hideItem = this.hideItem();
      if (item.wtype === 'WidgetFormLayout') {
        hideItem.argValue = item.title;
        hideItem.value = item.value;
        this.formData.hideBlocks.push(hideItem);
      } else if (item.wtype === 'WidgetTabItem') {
        hideItem.argValue = item.title;
        hideItem.value = item.value;
        this.formData.hideTabs.push(hideItem);
      }
    },
    // 删除隐藏布局
    delHideItem(item) {
      const hideIndex = this.hideData.findIndex(h => h.value === item.value);
      if (hideIndex !== -1) {
        if (item.wtype === 'WidgetFormLayout') {
          const _hideIndex = this.formData.hideBlocks.findIndex(h => h.value === item.value);
          this.formData.hideBlocks.splice(_hideIndex, 1);
        } else if (item.wtype === 'WidgetTabItem') {
          const _hideIndex = this.formData.hideTabs.findIndex(h => h.value === item.value);
          this.formData.hideTabs.splice(_hideIndex, 1);
        }
      }
    },
    onSearch(value) {
      this.searchVal = value || '';
    },
    textFilter(text) {
      let hasIndex = text.indexOf(this.searchVal);
      if (hasIndex > -1) {
        var newText = text.split(this.searchVal);
        var text = newText.join("<span style='color:red;'>" + this.searchVal + '</span>');
      }
      return text;
    }
  }
};
</script>
