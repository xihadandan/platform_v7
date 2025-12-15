<template>
  <a-form-model
    ref="form"
    :model="formData"
    :colon="false"
    labelAlign="left"
    :label-col="{ flex: '100px' }"
    :wrapper-col="{ flex: 'auto' }"
  >
    <a-form-model-item prop="type" label="来源">
      <a-radio-group v-model="formData.type" size="small" @change="changeTypeValue">
        <a-radio-button v-for="item in sourceTypeOptions" :key="item.value" :value="item.value">
          {{ item.label }}
        </a-radio-button>
      </a-radio-group>
    </a-form-model-item>
    <template v-if="formData.type === sourceTypeOptions[0]['value']">
      <a-form-model-item prop="unit" label="组织机构" v-if="types.indexOf('unit') > -1">
        <org-select
          :orgVersionId="orgVersionId"
          :orgVersionIds="orgVersionIds"
          v-model="formData.value"
          v-if="orgVersionId"
          :orgType="['MyOrg', 'MyLeader', 'PublicGroup']"
          @change="changeOrg"
        />
      </a-form-model-item>

      <a-form-model-item prop="option1" label="人员选项">
        <a-row>
          <a-col span="12" v-for="litem in handlerList" :key="litem.type">
            <div class="checked-div">{{ litem.label }}</div>
            <div class="checked-div" v-for="(item, index) in litem.list" :key="litem.type + index">
              <a-checkbox :checked="selectedUserOptions.includes(item.value)" :value="item" @change="changeOptions">
                {{ item.name }}
              </a-checkbox>
            </div>
          </a-col>
        </a-row>
      </a-form-model-item>

      <a-form-model-item label="职等职级">
        <a-form-model-item :label-col="{ flex: '120px' }">
          <template slot="label">
            <w-checkbox v-model="formData.enabledJobGrade">指定职等人员</w-checkbox>
          </template>
          <w-select
            v-model="formData.jobGrade"
            optionValueType="number"
            mode="multiple"
            :options="jobGradeOptions"
            :replaceFields="{
              title: 'jobGradeName',
              key: 'uuid',
              value: 'jobGrade'
            }"
          />
        </a-form-model-item>
        <a-form-model-item :label-col="{ flex: '120px' }">
          <template slot="label">
            <w-checkbox v-model="formData.enabledJobRank">指定职级人员</w-checkbox>
          </template>
          <w-tree-select
            v-model="formData.jobRankId"
            :treeData="jobRankTreeData"
            :treeCheckable="true"
            :replaceFields="{
              children: 'children',
              title: 'name',
              key: 'id',
              value: 'id'
            }"
          />
        </a-form-model-item>
      </a-form-model-item>
    </template>
    <template v-if="formData.type === sourceTypeOptions[1]['value']">
      <a-form-model-item label="业务组织">
        <w-tree-select
          v-model="formData.bizOrgId"
          :treeData="bizOrgTreeData"
          :replaceFields="{
            children: 'bizOrgs',
            title: 'name',
            key: 'id',
            value: 'id'
          }"
          :showSearch="false"
          :treeDefaultExpandAll="true"
          @change="changeBizOrgId"
        />
        <template v-if="currentBizOrgUuid">
          <org-select
            ref="bizOrg"
            :orgUuid="currentBizOrgUuid"
            v-model="formData.value"
            :key="currentBizOrgUuid"
            :orgType="['MyOrg', 'MyLeader', 'PublicGroup']"
            @change="changeBizOrgValue"
          />
        </template>
      </a-form-model-item>
      <a-form-model-item prop="option2" label="人员选项">
        <a-row>
          <a-col span="12" v-for="litem in bizOrgUserOptions" :key="litem.type">
            <div class="checked-div">{{ litem.label }}</div>
            <div class="checked-div" v-for="(item, index) in litem.list" :key="litem.type + index">
              <a-checkbox :checked="selectedUserOptions.includes(item.value)" :value="item" @change="changeOptions">
                {{ item.name }}
              </a-checkbox>
              <template v-if="selectedUserOptions.includes(item.value) && bizOrgAssignRole.includes(item.value)">
                <w-select
                  mode="multiple"
                  :options="bizOrgRolesOptions"
                  :replaceFields="{
                    title: 'name',
                    key: 'uuid',
                    value: 'id'
                  }"
                  @change="value => changeBizRole(value, item)"
                  style="width: 90%"
                />
              </template>
            </div>
          </a-col>
        </a-row>
      </a-form-model-item>
    </template>
    <template v-if="formData.type === sourceTypeOptions[2]['value']">
      <a-form-model-item prop="field" label="表单字段" v-if="types.indexOf('field') > -1">
        <dyform-fields-tree-select v-model="formData.value" :formData="formData" formDataFieldName="argValue" @change="changeFields" />
      </a-form-model-item>
    </template>
    <template v-if="formData.type === sourceTypeOptions[3]['value']">
      <a-form-model-item prop="task" label="环节" v-if="types.indexOf('task') > -1">
        <node-task-select v-model="formData.value" :formData="formData" formDataFieldName="argValue" mode="multiple" @change="changeTask" />
      </a-form-model-item>
    </template>
    <template v-if="formData.type === sourceTypeOptions[4]['value']">
      <a-form-model-item prop="custom" label="自定义" v-if="types.indexOf('custom') > -1">
        <user-select-custom
          v-model="formData.value"
          :data="[formData]"
          :orgVersionId="orgVersionId"
          :orgVersionIds="orgVersionIds"
          @change="changCustom"
        />
      </a-form-model-item>
    </template>
  </a-form-model>
</template>

<script>
import { handlerList, userSelectType, bizOrgUserOptions, availableBizOrgOptions, bizOrgAssignRole } from '../../designer/constant';

export default {
  name: 'UserSelectInfo',
  props: {
    formData: {
      type: Object,
      default: () => {}
    }
  },
  data() {
    let typesMap = {},
      typesValueMap = {};
    userSelectType.forEach(item => {
      typesMap[item['type']] = item;
      typesValueMap[item['value']] = item;
    });
    const sourceTypeOptions = userSelectType.slice(0, userSelectType.length - 1);
    const currentTypeValue = sourceTypeOptions[0]['value'];

    return {
      handlerList,
      userSelectType,
      bizOrgUserOptions,
      availableBizOrgOptions,
      bizOrgAssignRole,
      currentTypeValue
    };
  },
  methods: {
    // 更改来源值
    changeTypeValue(event) {
      const typeValue = event.target.value;

      if (this.currentTypeValue !== typeValue) {
        const type = this.typesValueMap[typeValue]['type'];
        this.formData = this.tempData[type];
        this.formData.type = typeValue;
        this.currentTypeValue = typeValue;
      }
    }
  }
};
</script>
