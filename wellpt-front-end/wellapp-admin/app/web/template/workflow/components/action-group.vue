<template>
  <a-form-model
    :model="setting.group"
    class="action-group"
    :label-col="{ span: 5 }"
    labelAlign="left"
    :wrapper-col="{ span: 19 }"
    :colon="false"
  >
    <a-alert message="流程签批时操作按钮较多，可将按钮设置分组折叠" type="info" show-icon closable />
    <p />
    <a-form-model-item>
      <span style="font-weight: bold" slot="label">按钮分组</span>
      <a-radio-group v-model="setting.group.type">
        <a-radio-button value="notGroup">不分组</a-radio-button>
        <a-radio-button value="fixedGroup">固定分组</a-radio-button>
        <a-radio-button value="dynamicGroup">动态分组</a-radio-button>
      </a-radio-group>
    </a-form-model-item>
    <a-table
      v-show="setting.group.type === 'fixedGroup'"
      rowKey="id"
      :pagination="false"
      :bordered="false"
      size="small"
      :columns="groupButtonTableColumns"
      :data-source="setting.group.groups"
    >
      <template slot="nameSlot" slot-scope="text, record, index">
        <a-input v-model="record.name">
          <template slot="addonAfter">
            <WI18nInput :code="record.id" :target="record" v-model="record.name" />
          </template>
        </a-input>
      </template>
      <template slot="buttonCodesSlot" slot-scope="text, record, index">
        <a-select mode="multiple" v-model="record.buttonCodes" style="width: 100%" :options="groupButtonOptions"></a-select>
      </template>
      <template slot="operationSlot" slot-scope="text, record, index">
        <a-icon type="delete" @click="deleteGroupButton(index)" />
      </template>
      <template slot="footer">
        <a-button type="link" @click="addGroupButton" icon="plus">添加</a-button>
      </template>
    </a-table>
    <div v-show="setting.group.type === 'dynamicGroup'">
      <a-form-model-item label="分组名称">
        <a-input v-model="setting.group.dynamicGroupName">
          <template slot="addonAfter">
            <WI18nInput code="dynamicGroupName" :target="setting.group" v-model="setting.group.dynamicGroupName" />
          </template>
        </a-input>
      </a-form-model-item>
      <a-form-model-item>
        <template slot="label">
          <span></span>
        </template>
        当按钮超过
        <a-input-number v-model="setting.group.dynamicGroupBtnThreshold" :min="1" :precision="0" />
        个时进行分组
      </a-form-model-item>
    </div>
    <a-form-model-item>
      <span style="font-weight: bold" slot="label">移动端动态分组</span>
      <a-form-model-item label="中文">
        当按钮超过
        <a-input-number v-model="setting.group.mobile.zh_CN" :min="1" :precision="0" />
        个时进行分组
      </a-form-model-item>
      <a-form-model-item label="其他语言">
        当按钮超过
        <a-input-number v-model="setting.group.mobile.otherLocale" :min="1" :precision="0" />
        个时进行分组
      </a-form-model-item>
    </a-form-model-item>
  </a-form-model>
</template>

<script>
import { generateId } from '@framework/vue/utils/util';
import WI18nInput from '@framework/vue/designer/w-i18n-input.vue';

export default {
  props: {
    setting: Object
  },
  components: { WI18nInput },

  data() {
    return {
      groupButtonTableColumns: [
        { title: '分组名称', dataIndex: 'name', width: 250, scopedSlots: { customRender: 'nameSlot' } },
        { title: '分组按钮', dataIndex: 'buttonCodes', scopedSlots: { customRender: 'buttonCodesSlot' } },
        { title: '操作', dataIndex: 'operation', width: 50, scopedSlots: { customRender: 'operationSlot', align: 'right' } }
      ]
    };
  },
  computed: {
    groupButtonOptions() {
      return this.setting.buttons.map(button => ({ label: button.title, value: button.code }));
    }
  },
  methods: {
    deleteGroupButton(index) {
      this.setting.group.groups.splice(index, 1);
    },
    addGroupButton() {
      this.setting.group.groups.push({
        id: generateId(),
        name: '',
        buttonIds: [],
        style: {}
      });
    }
  }
};
</script>

<style lang="less" scoped>
.action-group {
  padding: 15px;
}
</style>
