<template>
  <!-- 归档设置表单 -->
  <a-form-model
    ref="form"
    :model="formData"
    :rules="rules"
    :colon="false"
    labelAlign="right"
    :label-col="{ span: 5 }"
    :wrapper-col="{ span: 19 }"
  >
    <a-form-model-item prop="archiveWay" label="归档方式">
      <w-select
        v-model="formData.archiveWay"
        :options="archiveWayOptions"
        :formData="formData"
        formDataFieldName="archiveWayName"
        @change="changeArchiveWay"
      />
    </a-form-model-item>
    <template v-if="formData.archiveWay === archiveWayOptions[2]['id']">
      <a-form-model-item prop="archiveStrategy" label="归档策略">
        <a-radio-group v-model="formData.archiveStrategy" :options="archiveStrategyOptions" />
      </a-form-model-item>
    </template>

    <template v-if="formData.archiveWay === archiveWayOptions[2]['id'] || formData.archiveWay === archiveWayOptions[4]['id']">
      <a-form-model-item :prop="formData.archiveWay === archiveWayOptions[2]['id'] ? 'botRuleId' : ''" label="转换规则">
        <w-select v-model="formData.botRuleId" :formData="formData" formDataFieldName="botRuleName" :options="botRuleOptions" />
        注：没有配置归档夹时，数据直接归档到单据转换的目标单据
      </a-form-model-item>
    </template>

    <template
      v-if="
        formData.archiveWay === archiveWayOptions[0]['id'] ||
        formData.archiveWay === archiveWayOptions[1]['id'] ||
        formData.archiveWay === archiveWayOptions[2]['id'] ||
        formData.archiveWay === archiveWayOptions[4]['id']
      "
    >
      <a-form-model-item
        :prop="
          formData.archiveWay !== archiveWayOptions[2]['id'] && formData.archiveWay !== archiveWayOptions[4]['id'] ? 'destFolderUuid' : ''
        "
        label="文件夹"
      >
        <w-tree-select
          v-model="formData.destFolderUuid"
          formDataFieldName="destFolderName"
          :formData="formData"
          :treeData="treeData"
          :loadData="onLoadTreeData"
          :replaceFields="{
            children: 'children',
            title: 'name',
            key: 'id',
            value: 'id'
          }"
          :treeCheckable="true"
          :treeCheckStrictly="true"
          @change="changeFolder"
        />
      </a-form-model-item>
    </template>

    <template
      v-if="
        formData.archiveWay === archiveWayOptions[0]['id'] ||
        formData.archiveWay === archiveWayOptions[1]['id'] ||
        formData.archiveWay === archiveWayOptions[2]['id']
      "
    >
      <a-form-model-item label="生成子夹">
        <w-switch v-model="subFolderState" :checkedValue="true" :unCheckedValue="false" />
      </a-form-model-item>
      <template v-if="subFolderState">
        <a-form-model-item prop="subFolderRule" label="子夹生成规则">
          <a-textarea
            v-model="formData.subFolderRule"
            :rows="4"
            :style="{
              height: 'auto'
            }"
          />
        </a-form-model-item>
      </template>
    </template>

    <template
      v-if="
        formData.archiveWay === archiveWayOptions[0]['id'] ||
        formData.archiveWay === archiveWayOptions[1]['id'] ||
        formData.archiveWay === archiveWayOptions[2]['id']
      "
    >
      <a-row>
        <a-col :span="19" :offset="5">
          <div>
            <w-checkbox v-model="formData.fillDateTime" :checkedValue="true" :unCheckedValue="false">时间变量补位</w-checkbox>
            子夹名称中月、日、时、分、秒的时间变量，选中即补位，如1月显示为01月
          </div>
          <div>
            1、绑定流程内置变量：${流程名称},${流程ID}
            <br />
            2、绑定当前用户数据变量：${当前用户名},${当前用户登录名},${当前用户ID},${当前用户主部门名称},${当前用户主部门ID}
            <br />
            3、绑定表单的所有字段编码：${dyform.字段编码 }
            <br />
            4、绑定通用的内置变量：${大写年},${大写月},${大写日},${简年},${年},${月},${日},${时},${分},${秒}
            <br />
            例如按年/月份的格式：${年} 年/${月} 月
          </div>
        </a-col>
      </a-row>
    </template>

    <template v-if="formData.archiveWay === archiveWayOptions[3]['id']">
      <a-form-model-item prop="archiveScriptType" label="归档脚本类型">
        <w-select v-model="formData.archiveScriptType" :options="[]" />
      </a-form-model-item>
    </template>
    <template v-if="formData.archiveWay === archiveWayOptions[3]['id']">
      <a-form-model-item label="归档脚本">
        <a-textarea
          v-model="formData.archiveScript"
          :rows="4"
          :style="{
            height: 'auto'
          }"
        />
      </a-form-model-item>
    </template>
    <template v-if="formData.archiveWay === archiveWayOptions[3]['id']">
      <a-row>
        <a-col :span="19" :offset="5">
          groovy流程数据归档示例：
          <br />
          def workflowArchiveService= applicationContext.getBean("workflowArchiveService");
          <br />
          def folderUuid = "2a3b8709-5532-4800-b4f4-16d042b75e11";
          <br />
          workflowArchiveService.archive(folderUuid, event);
          <br />
          支持的脚本变量
          <br />
          applicationContext：spring应用上下文 currentUser：当前用户信息
          <br />
          event：流程相关事件信息 flowInstUuid：流程实例UUID
          <br />
          taskInstUuid：环节实例UUID taskData：环节数据
          <br />
          formUuid：表单定义UUID dataUuid：表单数据UUID
          <br />
          dyFormData：表单数据 dyFormFacade：表单接口
          <br />
          actionType：操作类型，如Submit、Rollback等 opinionText：办理意见
          <br />
          resultMessage：事件脚本执行结果，调用resultMessage.isSuccess()方法返回true，通过resultMessage.setSuccess(true/false)设置脚本执行是否成功
        </a-col>
      </a-row>
    </template>
  </a-form-model>
</template>

<script>
import { archiveWayOptions, archiveStrategyOptions } from '../designer/constant';
import { fetchBotRuleConfFacadeService } from '../api/index';
import WSelect from '../components/w-select';
import WTreeSelect from '../components/w-tree-select';
import WSwitch from '../components/w-switch';
import WCheckbox from '../components/w-checkbox';

export default {
  name: 'ArchiveInfo',
  props: {
    formData: {
      type: Object,
      default: () => {}
    }
  },
  data() {
    let subFolderState = false;
    if (this.formData.subFolderRule !== '') {
      subFolderState = true;
    }
    return {
      subFolderState,
      archiveWayOptions,
      archiveStrategyOptions,
      treeData: [],
      botRuleOptions: [],
      rules: {
        archiveWay: { required: true, message: '请选择归档方式' },
        destFolderUuid: { required: true, message: '请选择文件夹' },
        subFolderRule: { required: true, message: '请输入子夹生成规则' },
        botRuleId: { required: true, message: '请选择单据转换规则' }
      },
      replaceFields: {
        title: 'name',
        key: 'id',
        value: 'id'
      }
    };
  },
  components: {
    WSelect,
    WTreeSelect,
    WSwitch,
    WCheckbox
  },
  created() {
    this.getTreeData().then(res => {
      this.treeData = this.formatTreeData(res);
    });
    this.getBotRuleConfFacadeService();
  },
  methods: {
    // 获取单据转换
    getBotRuleConfFacadeService() {
      fetchBotRuleConfFacadeService().then(res => {
        this.botRuleOptions = res;
      });
    },
    // 更改归档方式
    changeArchiveWay(value) {
      if (
        value !== this.archiveWayOptions[0]['id'] &&
        value !== this.archiveWayOptions[1]['id'] &&
        value !== this.archiveWayOptions[2]['id']
      ) {
        this.subFolderState = false;
      }
      this.$nextTick(() => {
        this.$refs.form.clearValidate();
      });
    },
    // 获取树型数据
    getTreeData(nodeId = -1) {
      const params = {
        args: JSON.stringify([nodeId]),
        methodName: 'getFolderTreeAsync',
        serviceName: 'dmsFileManagerService'
      };

      return new Promise((resolve, reject) => {
        this.$axios
          .post('/json/data/services', {
            ...params
          })
          .then(res => {
            if (res.status === 200) {
              if (res.data && res.data.code === 0) {
                const data = res.data.data;
                resolve(data);
              }
            }
          });
      });
    },
    // 加载树型数据
    onLoadTreeData(treeNode) {
      const { id } = treeNode.dataRef;
      return new Promise((resolve, reject) => {
        this.getTreeData(id).then(res => {
          treeNode.dataRef.children = this.formatTreeData(res);
          resolve();
        });
      });
    },
    formatTreeData(data) {
      data.forEach(item => {
        if (item.isParent) {
          item.isLeaf = false;
        } else {
          item.isLeaf = true;
        }
      });
      return data;
    },
    // 更改文件夹
    changeFolder(value, label, extra) {},
    validate(callback) {
      this.$refs.form.validate((valid, error) => {
        callback({ valid, error, data: this.formData });
      });
    }
  }
};
</script>
