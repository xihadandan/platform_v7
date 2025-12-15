<template>
  <div class="action-buttons">
    <div>
      <a-input-search style="width: 250px" v-model="keyword" @search="onSearch"></a-input-search>
      <a-button type="primary" @click="onSearch">查询</a-button>
    </div>
    <p />
    <div>
      <a-button type="primary" icon="plus" @click="onAddButtonClick">新增</a-button>
      <a-button type="danger" icon="delete" @click="onDeleteAllButtonClick">删除</a-button>
    </div>
    <p />
    <a-table
      rowKey="id"
      :showHeader="true"
      size="small"
      :pagination="false"
      :bordered="false"
      :columns="buttonsTableColumns"
      :data-source="setting.buttons"
      :row-selection="buttonRowSelection"
      :class="['widget-table-button-table no-border']"
      :scroll="{ y: 'calc(100vh - 400px)' }"
    >
      <template slot="titleSlot" slot-scope="text, record, index">
        {{ text }}
        <span v-if="record.buildIn && text != record.name">({{ record.name }})</span>
      </template>
      <template slot="sortOrderSlot" slot-scope="text, record, index">
        <a-row>
          <a-col span="12">{{ text }}</a-col>
          <a-col span="6">
            <a-tag class="tag-build-in" v-if="record.buildIn">平台内置</a-tag>
            <a-tag v-else>自定义</a-tag>
          </a-col>
          <a-col span="6">
            <a-tag v-if="record.multistate" color="cyan">多状态</a-tag>
          </a-col>
        </a-row>
      </template>
      <template slot="confirmConfigSlot" slot-scope="text, record, index">
        <Icon
          v-if="record.confirmConfig && record.confirmConfig.enable"
          type="pticon iconfont icon-ptkj-zhengquechenggongtishi"
          style="font-size: 22px; color: green"
        ></Icon>
      </template>
      <template slot="operationSlot" slot-scope="text, record, index">
        <a-button type="link" @click="onEditButtonClick(record)">配置</a-button>
        <a-button type="link" :disabled="record.buildIn" @click="onDeleteButtonClick(record)">删除</a-button>
      </template>
    </a-table>

    <Drawer
      :title="buttonDrawerTitle"
      :width="640"
      :mask="true"
      :closable="true"
      :destroyOnClose="true"
      v-model="buttonDrawerVisible"
      :container="getDrawerContainer"
      :bodyStyle="{ paddingRight: 0 }"
      drawerClass="pt-drawer"
    >
      <template slot="content">
        <PerfectScrollbar :style="{ height: '100%', paddingRight: 'var(--w-drawer-padding-lr)' }">
          <ButtonConfiguration ref="buttonConfig" :button="editButton"></ButtonConfiguration>
        </PerfectScrollbar>
      </template>
      <div v-if="addDrawerButton" slot="footer">
        <a-button type="default" @click="buttonDrawerVisible = false">取消</a-button>
        <a-button type="primary" @click="onAddButtonOkClick">确定</a-button>
      </div>
    </Drawer>
    <!-- <a-drawer
      :title="buttonDrawerTitle"
      placement="right"
      :closable="true"
      :destroyOnClose="true"
      :visible="buttonDrawerVisible"
      width="640"
      @close="buttonDrawerVisible = false"
      :getContainer="$el"
    >
      <ButtonConfiguration ref="buttonConfig" :button="editButton"></ButtonConfiguration>
      <div class="drawer-buttons" v-if="addDrawerButton">
        <a-button type="default" @click="buttonDrawerVisible = false">取消</a-button>
        <a-button type="primary" @click="onAddButtonOkClick">确定</a-button>
      </div>
    </a-drawer> -->
  </div>
</template>

<script>
import ButtonConfiguration from './button-configuration.vue';
import { trim, isEmpty } from 'lodash';
import { deepClone } from '@framework/vue/utils/util';
import Drawer from '@pageAssembly/app/web/lib/drawer.vue';

export default {
  props: {
    setting: Object
  },
  components: { ButtonConfiguration, Drawer },
  data() {
    return {
      keyword: null,
      buttonsTableColumns: [
        { title: '操作名称', dataIndex: 'title', scopedSlots: { customRender: 'titleSlot' } },
        { title: '编号', dataIndex: 'code', scopedSlots: { customRender: 'codeSlot' }, sorter: (a, b) => a.code.localeCompare(b.code) },
        {
          title: '排序号',
          dataIndex: 'sortOrder',
          width: 300,
          scopedSlots: { customRender: 'sortOrderSlot' },
          sorter: (a, b) => a.sortOrder - b.sortOrder
        },
        {
          title: '操作二次确认',
          dataIndex: 'confirmConfig',
          width: 120,
          align: 'center',
          scopedSlots: { customRender: 'confirmConfigSlot' }
        },
        { title: '操作', dataIndex: 'operation', width: 150, align: 'center', scopedSlots: { customRender: 'operationSlot' } }
      ],
      selectedButtonRowKeys: [],
      selectedButtonRows: [],
      buttonDrawerTitle: '',
      buttonDrawerVisible: false,
      addDrawerButton: true,
      editButton: undefined
    };
  },
  computed: {
    buttonRowSelection() {
      return {
        onChange: (selectedRowKeys, selectedRows) => {
          this.selectedButtonRowKeys = selectedRowKeys;
          this.selectedButtonRows = selectedRows;
        }
      };
    }
  },
  methods: {
    removeSelectedButtonByKey(key) {
      let keyIndex = this.selectedButtonRowKeys.findIndex(rowKey => rowKey == key);
      if (keyIndex != -1) {
        this.selectedButtonRowKeys.splice(keyIndex, 1);
      }

      let rowIndex = this.selectedButtonRows.findIndex(rowKey => rowKey == key);
      if (rowIndex != -1) {
        this.selectedButtonRows.splice(rowIndex, 1);
      }
    },
    clearSelectedButtons() {
      this.selectedButtonRowKeys = [];
      this.selectedButtonRows = [];
    },
    onSearch() {
      let searchVal = trim(this.keyword);
      if (isEmpty(searchVal)) {
        if (this.initButtons) {
          this.setting.buttons = this.initButtons;
          this.initButtons = null;
        }
      } else {
        if (!this.initButtons) {
          this.initButtons = deepClone(this.setting.buttons);
        }
        this.setting.buttons = this.initButtons.filter(
          button => (button.title && button.title.indexOf(searchVal) != -1) || (button.code && button.code.indexOf(searchVal) != -1)
        );
      }
    },
    onAddButtonClick() {
      this.buttonDrawerTitle = '新增流程操作';
      this.buttonDrawerVisible = true;
      this.addDrawerButton = true;
      this.editButton = undefined;
    },
    onEditButtonClick(button) {
      this.buttonDrawerTitle = '编辑流程操作';
      this.buttonDrawerVisible = true;
      this.addDrawerButton = false;
      this.editButton = button;
    },
    onDeleteAllButtonClick() {
      const _this = this;
      if (!_this.selectedButtonRowKeys.length) {
        _this.$message.error('请选择记录！');
        return;
      }
      let buildInBtns = _this.selectedButtonRows.filter(item => item.buildIn);
      if (buildInBtns.length) {
        _this.$message.error(`平台内置按钮[${buildInBtns.map(item => item.title)}]不能删除！`);
        return;
      }

      _this.$confirm({
        title: '确认',
        content: `确认删除按钮[${_this.selectedButtonRows.map(item => item.title)}]？`,
        onOk() {
          _this.selectedButtonRows.forEach(row => {
            let buttonIndex = _this.setting.buttons.findIndex(item => item.id == row.id);
            if (buttonIndex != -1) {
              _this.setting.buttons.splice(buttonIndex, 1);
            }
          });
          _this.clearSelectedButtons();
        }
      });
    },
    onDeleteButtonClick(button) {
      const _this = this;
      _this.$confirm({
        title: '确认',
        content: `确认删除按钮[${button.title}]？`,
        onOk() {
          let buttonIndex = _this.setting.buttons.findIndex(item => item.id == button.id);
          if (buttonIndex != -1) {
            _this.setting.buttons.splice(buttonIndex, 1);
          }
          _this.removeSelectedButtonByKey(button.id);
        }
      });
    },
    onAddButtonOkClick() {
      this.$refs.buttonConfig.collect().then(button => {
        this.setting.buttons.push(button);
        this.buttonDrawerVisible = false;
      });
    },
    getDrawerContainer() {
      return document.body;
    }
  }
};
</script>

<style lang="less" scoped>
.action-buttons {
  padding: 15px;

  .tag-build-in {
    color: #fff;
    background: var(--w-primary-color);
  }

  .drawer-buttons {
    position: absolute;
    bottom: 0;
    right: 24px;
    z-index: 1;

    width: 100%;
    height: 50px;
    line-height: 50px;
    text-align: right;
    background: var(--w-bg-color-body);
  }
}
</style>
