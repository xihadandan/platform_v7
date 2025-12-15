<template>
  <div :style="{ minHeight: '500px' }">
    <template v-if="xmlDefinition">
      <a-tabs default-active-key="1">
        <a-tab-pane key="1" tab="差异内容">
          <div v-if="diff" v-html="diff"></div>
          <div v-else>暂无差异内容！</div>
        </a-tab-pane>
        <a-tab-pane key="2" tab="日志XML">{{ logXml }}</a-tab-pane>
        <a-tab-pane key="3" tab="最新XML">{{ flowXml }}</a-tab-pane>
      </a-tabs>
    </template>
    <template v-if="jsonDefiniton">
      <a-tabs default-active-key="1">
        <a-tab-pane key="1" tab="差异内容">
          <JsonViewer :value="diffJson" :expand-depth="3" copyable boxed sort>
            <div slot="copy">复制</div>
          </JsonViewer>
        </a-tab-pane>
        <a-tab-pane key="2" tab="日志JSON">
          <JsonViewer :value="logJson" :expand-depth="3" copyable boxed sort>
            <div slot="copy">复制</div>
          </JsonViewer>
        </a-tab-pane>
        <a-tab-pane key="3" tab="最新JSON">
          <JsonViewer :value="flowJson" :expand-depth="3" copyable boxed sort>
            <div slot="copy">复制</div>
          </JsonViewer>
        </a-tab-pane>
      </a-tabs>
    </template>
  </div>
</template>

<script>
import JsonViewer from 'vue-json-viewer';
import 'vue-json-viewer/style.css';
import { deepClone } from '@framework/vue/utils/util';
import { isEmpty } from 'lodash';

export default {
  components: { JsonViewer },
  inject: ['pageContext', '$event', 'vPageState'],
  data() {
    let $event = this.$event || {};
    let formData = $event.meta || {};
    let uuid = formData.uuid;
    return {
      uuid,
      xmlDefinition: false,
      flowXml: '',
      logXml: '',
      diff: '',
      jsonDefiniton: false,
      flowJson: null,
      logJson: null,
      diffJson: null
    };
  },
  created() {
    if (this.uuid) {
      this.getLogDiff();
    }
  },
  methods: {
    getLogDiff() {
      $axios.get(`/proxy/api/workflow/definition/getLogDiff?flowSchemaLogUuid=${this.uuid}`).then(({ data: result }) => {
        if (result.data) {
          if (result.data.xmlDefinition == 'true') {
            this.xmlDefinition = true;
            this.flowXml = result.data.flowXml;
            this.logXml = result.data.logXml;
            this.diff = result.data.diff;
          } else {
            this.jsonDefiniton = true;
            let flowJson = JSON.parse(result.data.flowJson || '{}');
            let logJson = JSON.parse(result.data.logJson || '{}');
            delete flowJson['graphData'];
            delete logJson['graphData'];
            this.flowJson = flowJson;
            this.logJson = logJson;
            this.deffJson = this.createDiffJson(deepClone(logJson), deepClone(flowJson));
          }
        }
      });
    },
    createDiffJson(oldJson, newJson) {
      if (JSON.stringify(oldJson) == JSON.stringify(newJson)) {
        return '暂无差异内容！';
      }

      let keyPaths = ['/'];
      this.diffJson = this.compareDiffJson(oldJson, newJson, keyPaths);
    },
    compareDiffJson(oldJson, newJson, keyPaths) {
      for (let key in newJson) {
        keyPaths.push(key);
        let oldVal = oldJson[key];
        let newVal = newJson[key];
        if (oldVal == newVal) {
          delete oldJson[key];
        } else if (oldVal != null && typeof oldVal == 'object') {
          if (Array.isArray(oldVal) || Array.isArray(newVal)) {
            this.compareDiffArray(key, keyPaths, oldVal, newVal, oldJson, newJson);
          } else if (JSON.stringify(oldVal) == JSON.stringify(newVal)) {
            delete oldJson[key];
          } else {
            this.compareDiffJson(oldVal, newVal, keyPaths);
          }
        } else {
          if (newVal != null && typeof oldVal == 'object') {
            oldJson[key] = oldVal + '(新值: ' + JSON.stringify(newVal) + ')';
          } else {
            oldJson[key] = oldVal + '(新值: ' + newVal + ')';
          }
        }
        keyPaths.pop();
      }
      return oldJson;
    },
    compareDiffArray(key, keyPaths, oldVal, newVal, oldJson, newJson) {
      const _this = this;
      if (JSON.stringify(oldVal) == JSON.stringify(newVal)) {
        delete oldJson[key];
      }

      // 环节、流向变更
      if ((key == 'tasks' || key == 'directions') && keyPaths.length == 2) {
        _this.compareDiffArrayObjects('id', 'name', keyPaths, oldVal, newVal);
      } else if (key == 'timers' && keyPaths.length == 2) {
        _this.compareDiffArrayObjects('timerId', 'name', keyPaths, oldVal, newVal);
      } else if (
        [
          'untreadTasks',
          'readFields',
          'editFields',
          'hideFields',
          'notNullFields',
          'fileRights',
          'allFormFields',
          'allFormFieldWidgetIds',
          'formBtnRightSettings',
          'hideBlocks',
          'hideTabs',
          'optNames',
          'users',
          'transferUsers',
          'copyUsers',
          'emptyToUsers',
          'monitors',
          'counterSignUsers',
          'addSignUsers',
          'bakUsers',
          'creators',
          'admins',
          'viewers',
          'conditions',
          'fileRecipients',
          'msgRecipients',
          'beginDirections',
          'endDirections',
          'dutys',
          'tasks',
          'alarmObjects',
          'alarmUsers',
          'alarmFlowDoings',
          'alarmFlowDoingUsers',
          'dueObjects',
          'dueUsers',
          'dueToUsers',
          'dueFlowDoings',
          'dueFlowDoingUsers'
        ].includes(key)
      ) {
        _this.compareDiffArrayObjects('value', null, keyPaths, oldVal, newVal);
      } else if (['startRights', 'rights', 'doneRights', 'monitorRights', 'adminRights', 'copyToRights', 'viewerRights'].includes(key)) {
        _this.compareDiffArrayObjects('value', 'name', keyPaths, oldVal, newVal);
      } else if (key == 'flowStates') {
        _this.compareDiffArrayObjects('code', 'name', keyPaths, oldVal, newVal);
      } else if (key == 'messageTemplates') {
        _this.compareDiffArrayObjects('type', 'typeName', keyPaths, oldVal, newVal);
      } else if (key == 'records') {
        _this.compareDiffArrayObjects('name', null, keyPaths, oldVal, newVal);
      } else if (key == 'opinionCheckSets') {
        _this.compareDiffArrayObjects('id', null, keyPaths, oldVal, newVal);
      } else if (key == 'eventScripts') {
        _this.compareDiffArrayObjects('pointcut', null, keyPaths, oldVal, newVal);
      } else if (key == 'archives') {
        _this.compareDiffArrayObjects('archiveId', null, keyPaths, oldVal, newVal);
      } else if (key == 'subTasks') {
        _this.compareDiffArrayObjects('taskId', null, keyPaths, oldVal, newVal);
      } else if (key == 'alarmElements') {
        _this.compareDiffArrayObjects('alarmTime', null, keyPaths, oldVal, newVal);
      } else if (key == 'timers') {
        // 子流程计时器列表
        _this.compareDiffArrayObjects('newFlowId', 'newFlowTimerName', keyPaths, oldVal, newVal);
      }
    },
    compareDiffArrayObjects(keyProp, nameProp, keyPaths, oldArray, newArray) {
      const _this = this;
      let addedObjects = newArray.filter(newObject => {
        let oldObject = oldArray.find(oldObject => oldObject[keyProp] == newObject[keyProp]);
        return oldObject == null ? true : false;
      });

      for (let index = 0; index < oldArray.length; index++) {
        let oldObject = oldArray[index];
        let newObject = newArray.find(newObject => newObject[keyProp] == oldObject[keyProp]);
        if (newObject) {
          _this.compareDiffJson(oldObject, newObject, keyPaths);
          if (!isEmpty(oldObject)) {
            if (nameProp && !oldObject[nameProp]) {
              oldObject[nameProp] = newObject[nameProp];
            }
          } else {
            oldArray.splice(index, 1);
            index--;
          }
        } else {
          if (nameProp && oldObject[nameProp]) {
            oldObject[nameProp] += '(删除)';
          } else {
            oldObject['deleted'] = '(删除)';
          }
        }
      }

      addedObjects.forEach(addObject => {
        if (nameProp && addObject[nameProp]) {
          addObject[nameProp] += '(新增)';
        } else {
          addObject['added'] = '(新增)';
        }
        oldArray.push(addObject);
      });
    }
  }
};
</script>

<style></style>
