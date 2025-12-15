import Vue from 'vue';
import './installWidget';

// 要使用客户端异步按需渲染需去掉下面的require.context
let components = require.context('../../page/workflow-work/component', true, /\w+\.vue$/);

const install = function (vue) {
  vue = vue || Vue;
  components.keys().map(fileName => {
    let comp = components(fileName).default;
    vue.component(comp.name, comp);
  });
};

const installClient = function (vue) {
  // asyncComponent(vue, 'WorkView', '../../widget/workflow-work-view.vue');
  // 同步注册的组件
  vue.component('WorkView', require('../../page/workflow-work/component/work-view.vue').default);
  vue.component('WorkflowToolbar', require('../../page/workflow-work/component/workflow-toolbar.vue').default);
  vue.component('WorkflowActionPopover', require('../../page/workflow-work/component/workflow-action-popover.vue').default);
  vue.component('WorkflowSidebarLayout', require('../../page/workflow-work/component/workflow-sidebar-layout.vue').default);

  // 异步注册的组件
  // 签署意见及办理过程
  vue.component('WorkflowOpinionEditor', () =>
    import(/* webpackChunkName: "WorkflowOpinionEditor" */ '../../page/workflow-work/component/workflow-opinion-editor.vue')
  );
  vue.component('WorkflowOpinionManager', () =>
    import(/* webpackChunkName: "WorkflowOpinionManager" */ '../../page/workflow-work/component/workflow-opinion-manager.vue')
  );
  vue.component('WorkflowProcessViewer', () =>
    import(/* webpackChunkName: "WorkflowProcessViewer" */ '../../page/workflow-work/component/workflow-process-viewer.vue')
  );
  vue.component('WorkflowTaskProcess', () =>
    import(/* webpackChunkName: "WorkflowTaskProcess" */ '../../page/workflow-work/component/workflow-task-process.vue')
  );
  // vue.component('WidgetWorkProcess', () =>
  //   import(/* webpackChunkName: "WidgetWorkProcess" */ '../../widget/widget-work-process/widget-work-process.vue')
  // );

  // 子流程
  vue.component('WorkflowSubflowViewer', () =>
    import(/* webpackChunkName: "WorkflowSubflowViewer" */ '../../page/workflow-work/component/workflow-subflow-viewer.vue')
  );
  vue.component('WorkflowSubflowShareData', () =>
    import(/* webpackChunkName: "WorkflowSubflowShareData" */ '../../page/workflow-work/component/workflow-subflow-share-data.vue')
  );
  vue.component('WorkflowSubflowWorkProcess', () =>
    import(/* webpackChunkName: "WorkflowSubflowWorkProcess" */ '../../page/workflow-work/component/workflow-subflow-work-process.vue')
  );
  vue.component('WorkflowSubflowDistributeInfo', () =>
    import(
      /* webpackChunkName: "WorkflowSubflowDistributeInfo" */ '../../page/workflow-work/component/workflow-subflow-distribute-info.vue'
    )
  );
  vue.component('WorkflowSubflowRelateOperation', () =>
    import(
      /* webpackChunkName: "WorkflowSubflowRelateOperation" */ '../../page/workflow-work/component/workflow-subflow-relate-operation.vue'
    )
  );

  // 分支流
  vue.component('WorkflowBranchTaskShareData', () =>
    import(/* webpackChunkName: "WorkflowBranchTaskShareData" */ '../../page/workflow-work/component/workflow-branch-task-share-data.vue')
  );
  vue.component('WorkflowBranchTaskDistributeInfo', () =>
    import(
      /* webpackChunkName: "WorkflowBranchTaskDistributeInfo" */ '../../page/workflow-work/component/workflow-branch-task-distribute-info.vue'
    )
  );
  vue.component('WorkflowBranchTaskRelateOperation', () =>
    import(
      /* webpackChunkName: "WorkflowBranchTaskRelateOperation" */ '../../page/workflow-work/component/workflow-branch-task-relate-operation.vue'
    )
  );
  vue.component('WorkflowViewerDrawer', () =>
    import(
      /* webpackChunkName: "WorkflowViewerDrawer" */ '../../page/workflow-work/component/workflow-viewer-drawer.vue'
    )
  );
};

if (typeof window !== 'undefined' && window.Vue) {
  // 客户端渲染使用的install
  installClient(window.Vue);
} else {
  // 服务端渲染使用的install
  install();
}
