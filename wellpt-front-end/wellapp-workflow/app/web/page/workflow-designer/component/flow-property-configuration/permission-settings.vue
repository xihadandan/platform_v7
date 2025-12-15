<template>
  <!-- 流程属性-流程权限 -->
  <div>
    <a-form-model-item>
      <template slot="label">
        <a-tooltip placement="topRight" :arrowPointAtCenter="true">
          <div slot="title">
            发起人：发起人可发起本流程
            <br />
            参与人：指定的参与人可参与本流程的办理和审批
            <br />
            监控者：监控者可查看和管理本流程的流转数据，在特殊情况下可对流程进行人工干预，例如跳转环节、移交办理人等
            <br />
            阅读者：阅读者可查看本流程的流转数据，不论有无参与流程的办理和审批
          </div>
          <label>
            权限设置
            <a-icon type="exclamation-circle" />
          </label>
        </a-tooltip>
      </template>
    </a-form-model-item>
    <a-form-model-item label="发起人">
      <a-radio-group v-model="creatorsMode" size="small" button-style="solid" @change="e => modeChange(e, 'creators')">
        <a-radio-button v-for="item in permissionDefaultOrCustom" :key="item.value" :value="item.value">
          {{ item.label }}
        </a-radio-button>
      </a-radio-group>
    </a-form-model-item>
    <div class="ant-form-item" v-if="creatorsMode === constCustom">
      <org-select
        class="flow-org-select"
        :orgIdOptions="useOrgIdMap"
        v-model="creators"
        v-if="orgVersionId"
        @change="arg => changeOrg('creators', arg)"
      />
    </div>
    <a-form-model-item label="参与人">
      <a-radio-group v-model="usersMode" size="small" button-style="solid" @change="e => modeChange(e, 'users')">
        <a-radio-button v-for="item in permissionDefaultOrCustom" :key="item.value" :value="item.value">
          {{ item.label }}
        </a-radio-button>
      </a-radio-group>
    </a-form-model-item>
    <div class="ant-form-item" v-if="usersMode === constCustom">
      <org-select
        class="flow-org-select"
        :orgIdOptions="useOrgIdMap"
        v-model="users"
        v-if="orgVersionId"
        @change="arg => changeOrg('users', arg)"
      />
    </div>
    <a-form-model-item label="督办人" class="form-item-vertical">
      <user-select-list v-model="formData.monitors" types="unit/bizOrg/field/task/custom/filter" title="选择督办人" text="督办人" />
    </a-form-model-item>
    <a-form-model-item label="监控者" class="form-item-vertical">
      <user-select-list v-model="formData.admins" types="unit/bizOrg/field/task/custom/filter" title="选择监控者" text="监控者" />
    </a-form-model-item>
    <div class="ant-form-item">
      <w-checkbox v-model="formData.keepRuntimePermission">办结时保留监控权限</w-checkbox>
    </div>
    <a-form-model-item label="阅读者" class="form-item-vertical">
      <user-select-list v-model="formData.viewers" types="unit/bizOrg/field/task/custom/filter" title="选择阅读者" text="阅读者" />
    </a-form-model-item>
    <more-show-component position="bottom">
      <!-- <a-form-model-item prop="granularity" :label-col="{ span: 9 }" :wrapper-col="{ span: 14, style: { textAlign: 'right' } }">
        <template slot="label">
          <a-tooltip placement="topRight" :arrowPointAtCenter="true">
            <div slot="title">设置用户的办理权限粒度为具体某个人员可办理，或者某个职位的人员都可办理</div>
            <label>
              最大权限粒度
              <a-icon type="exclamation-circle" />
            </label>
          </a-tooltip>
        </template>
        <w-select
          v-model="formData.granularity"
          :options="granularityOptions"
          :replaceFields="{
            title: 'label',
            key: 'value',
            value: 'value'
          }"
        />
      </a-form-model-item> -->
      <a-form-model-item :label-col="{ span: 10 }" :wrapper-col="{ span: 13, style: { textAlign: 'right' } }">
        <template slot="label">
          <a-tooltip placement="topRight" :arrowPointAtCenter="true">
            <div slot="title">
              用于设置特殊场景下用户对本流程的流程数据权限
              <br />
              通过接口鉴权：开启后流程通过指定的接口的返回值，判定用户是否有本流程数据的相关权限
            </div>
            <label>
              流程数据鉴权设置
              <a-icon type="exclamation-circle" />
            </label>
          </a-tooltip>
        </template>
      </a-form-model-item>
      <a-form-model-item prop="enableAccessPermissionProvider" label="通过接口鉴权">
        <w-switch v-model="formData.enableAccessPermissionProvider" @change="enableAccessPermissionProviderChange" />
      </a-form-model-item>
      <template v-if="formData.enableAccessPermissionProvider === '1'">
        <a-form-model-item prop="accessPermissionProvider" label="接口">
          <w-tree-select
            :treeData="accessPermission"
            v-model="formData.accessPermissionProvider"
            :treeCheckable="true"
            :formData="formData"
            formDataFieldName="accessPermissionProviderName"
            :replaceFields="{
              children: 'children',
              title: 'name',
              key: 'id',
              value: 'id'
            }"
          />
        </a-form-model-item>
        <div class="ant-form-item">
          <w-checkbox v-model="formData.onlyUseAccessPermissionProvider">仅通过接口鉴权（流程权限设置不再生效）</w-checkbox>
        </div>
      </template>
    </more-show-component>
  </div>
</template>

<script>
import { constDefault, constCustom, permissionDefaultOrCustom, granularityOptions } from '../designer/constant';
import OrgSelect from '@admin/app/web/lib/org-select.vue';
import WSwitch from '../components/w-switch.js';
import WCheckbox from '../components/w-checkbox.js';
import WTreeSelect from '../components/w-tree-select';
import { deepClone } from '@framework/vue/utils/util';
import UserSelectList from '../commons/user-select-list.vue';
import MoreShowComponent from '../commons/more-show-component.vue';
import WSelect from '../components/w-select';
import { map } from 'lodash';
import mixins from '../mixins';

export default {
  name: 'FlowPropertyPermissionSettings',
  mixins: [mixins],
  props: {
    formData: {
      type: Object,
      default: () => {}
    }
  },
  data() {
    // 发起人
    let creatorsMode = '';
    let creators = '';
    let creatorsData = deepClone(this.formData.creators);
    if (this.formData.creators.length) {
      creatorsMode = constCustom;
      let creatorsArr = [];
      this.formData.creators.forEach(item => {
        creatorsArr.push(item.value);
      });
      creators = creatorsArr.join(';');
    } else {
      creatorsMode = constDefault;
    }
    // 参与人
    let usersMode = '';
    let users = '';
    let usersData = deepClone(this.formData.users);
    if (this.formData.users.length) {
      usersMode = constCustom;
      let usersArr = [];
      this.formData.users.forEach(item => {
        usersArr.push(item.value);
      });
      users = usersArr.join(';');
    } else {
      usersMode = constDefault;
    }
    // 阅读者
    let viewerMode = '';
    let viewers = '';
    let viewersData = deepClone(this.formData.viewers);
    if (this.formData.viewers.length) {
      viewerMode = constCustom;
      let viewersArr = [];
      this.formData.viewers.forEach(item => {
        viewersArr.push(item.value);
      });
      viewers = viewersArr.join(';');
    } else {
      viewerMode = constDefault;
    }

    return {
      permissionDefaultOrCustom,
      constCustom,
      creatorsMode,
      creators,
      creatorsData,
      usersMode,
      users,
      usersData,
      viewerMode,
      viewers,
      viewersData,
      granularityOptions,
      accessPermission: []
    };
  },
  components: {
    OrgSelect,
    WSwitch,
    WCheckbox,
    UserSelectList,
    WTreeSelect,
    MoreShowComponent,
    WSelect
  },
  computed: {
    orgVersionId() {
      return this.formData.orgVersionId || '';
    },
    orgVersionIds() {
      return this.formData.orgVersionIds || [];
    }
  },
  created() {
    if (this.formData.accessPermissionProvider) {
      this.getAccessPermissionProvider();
    }
  },
  methods: {
    // 获取流程访问权限
    getAccessPermissionProvider() {
      const params = {
        args: JSON.stringify([-1]),
        serviceName: 'flowSchemeService',
        methodName: 'getFlowAccessPermissionProvider'
      };
      this.$axios
        .post('/json/data/services', {
          ...params
        })
        .then(res => {
          if (res.status === 200) {
            if (res.data && res.data.code === 0) {
              const data = res.data.data;
              this.accessPermission = data;
            }
          }
        });
    },
    enableAccessPermissionProviderChange(checked) {
      if (checked && this.accessPermission.length == 0) {
        this.getAccessPermissionProvider();
      }
    },
    modeChange(e, param) {
      let val = e.target.value;
      if (val === constCustom) {
        this.$set(this.formData, param, this[param + 'Data']);
      } else {
        this.$set(this.formData, param, []);
      }
    },
    changeOrg(param, { value, label, nodes }) {
      this.$set(
        this.formData,
        param,
        map(nodes, item => {
          return {
            value: item.key,
            argValue: item.title,
            type: '1'
          };
        })
      );
      this[param + 'Data'] = deepClone(this.formData[param]);
      // console.log('组织弹出框选择变更：', arguments);
    }
  }
};
</script>
