<template>
  <div class="widget-work-process">
    <WorkProcessStandard
      v-if="widget.configuration.displayStyle == 'standard' && workProcess"
      :items="workProcess.items"
      :widget="widget"
    ></WorkProcessStandard>
    <WorkProcessSimple
      v-else-if="widget.configuration.displayStyle == 'simple' && workProcess"
      :items="workProcess.items"
      :widget="widget"
    ></WorkProcessSimple>
    <WorkProcessTable
      v-else-if="widget.configuration.displayStyle == 'table' && workProcess"
      :items="workProcess.workProcesses"
      :widget="widget"
    ></WorkProcessTable>
  </div>
</template>

<script>
import widgetMixin from '@framework/vue/mixin/widgetMixin';
import WorkProcess from '../../page/workflow-work/component/WorkProcess.js';
import WorkProcessStandard from './work-process-standard.vue';
import WorkProcessSimple from './work-process-simple.vue';
import WorkProcessTable from './work-process-table.vue';
export default {
  name: 'WidgetWorkProcess',
  mixins: [widgetMixin],
  components: { WorkProcessStandard, WorkProcessSimple, WorkProcessTable },
  inject: ['$workView', 'dyform', 'designMode'],
  data() {
    return {
      workProcess: null,
      displayWhenUnlinkData: this.designMode || this.widget.configuration.unlinkDataDisplayState == 'label'
    };
  },
  beforeMount() {
    this.loadWorkProcess();
  },
  methods: {
    loadWorkProcess() {
      const _this = this;
      if (_this.$workView) {
        _this.$workView.workView
          .getWorkProcess(_this.widget.configuration.displayStyle)
          .then(data => {
            if (data) {
              _this.workProcess = data;
            } else if (_this.displayWhenUnlinkData) {
              _this.workProcess = { opinionPositionConfig: {}, workProcesses: [], items: [] };
            }
          })
          .catch(() => {
            if (_this.displayWhenUnlinkData) {
              _this.workProcess = { opinionPositionConfig: {}, workProcesses: [], items: [] };
            }
          });
      } else if (_this.dyform && _this.dyform.dataUuid) {
        new WorkProcess(null, this)
          .getWorkProcessByDataUuid(_this.dyform.dataUuid, _this.widget.configuration.displayStyle)
          .then(data => {
            if (data) {
              _this.workProcess = data;
            } else if (_this.displayWhenUnlinkData) {
              _this.workProcess = { opinionPositionConfig: {}, workProcesses: [], items: [] };
            }
          })
          .catch(() => {
            if (_this.displayWhenUnlinkData) {
              _this.workProcess = { opinionPositionConfig: {}, workProcesses: [], items: [] };
            }
          });
      } else {
        if (_this.displayWhenUnlinkData) {
          _this.workProcess = { opinionPositionConfig: {}, workProcesses: [], items: [] };
        }
      }
    }
  }
};
</script>

<style></style>
