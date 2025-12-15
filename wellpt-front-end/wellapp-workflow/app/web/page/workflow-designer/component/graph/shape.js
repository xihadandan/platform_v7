import { register } from '@antv/x6-vue-shape'
import NodeTask from './custom-vue-node/node-task.vue'
import NodeCircle from './custom-vue-node/node-circle.vue'
import NodeCondition from './custom-vue-node/node-condition.vue'
import NodeSubflow from './custom-vue-node/node-subflow.vue'
import NodeLabel from './custom-vue-node/node-label.vue'
import NodeSwimlane from './custom-vue-node/node-swimlane.vue'
import NodeGuide from './custom-vue-node/node-guide.vue'
import NodeRobot from './custom-vue-node/node-robot.vue'
import NodeCollab from './custom-vue-node/node-collab.vue'
import { ports } from './ports'
import { Graph, Point } from '@antv/x6'
import { NumberExt } from '@antv/x6-common';
import constant from '../designer/constant'

export const createShape = (designer, themeClass, graphInstance) => {
  register({
    shape: 'NodeTask',
    width: constant.NodeTaskWidth,
    height: constant.NodeTaskHeight,
    attrs: {
      body: {
        class: themeClass
      }
    },
    component: {
      provide() {
        return {
          designer: designer
        };
      },
      render: h => h(NodeTask)
    },
    ports: { ...ports }
  });
  register({
    shape: 'NodeCircle',
    width: constant.NodeCircleWidth,
    height: constant.NodeCircleHeight,
    attrs: {
      body: {
        class: themeClass
      }
    },
    component: {
      provide() {
        return {
          designer: designer
        };
      },
      render: h => h(NodeCircle)
    },
    ports: { ...ports }
  });
  register({
    shape: 'NodeCondition',
    width: constant.NodeConditionWidth,
    height: constant.NodeConditionHeight,
    attrs: {
      body: {
        class: themeClass
      }
    },
    component: {
      provide() {
        return {
          designer: designer
        };
      },
      render: h => h(NodeCondition)
    },
    ports: { ...ports }
  });
  register({
    shape: 'NodeSubflow',
    width: constant.NodeSubflowWidth,
    height: constant.NodeSubflowHeight,
    attrs: {
      body: {
        class: themeClass
      }
    },
    component: {
      provide() {
        return {
          designer: designer
        };
      },
      render: h => h(NodeSubflow)
    },
    ports: { ...ports }
  });
  register({
    shape: 'NodeLabel',
    width: constant.NodeDefaultWidth,
    height: constant.NodeDefaultHeight,
    attrs: {
      body: {
        class: themeClass
      }
    },
    component: {
      provide() {
        return {
          designer: designer,
          graphInstance
        };
      },
      render: h => h(NodeLabel)
    }
  });
  register({
    shape: 'NodeSwimlane',
    width: constant.NodeSwimlaneIconWidth,
    height: constant.NodeSwimlaneIconHeight,
    attrs: {
      body: {
        class: themeClass
      }
    },
    component: {
      provide() {
        return {
          designer: designer,
          graphInstance
        };
      },
      render: h => h(NodeSwimlane)
    }
  });
  register({
    shape: 'NodeGuide',
    width: constant.NodeGuideWidth,
    height: constant.NodeGuideHeight,
    attrs: {
      body: {
        class: themeClass
      }
    },
    component: {
      provide() {
        return {
          designer: designer,
          graphInstance
        };
      },
      render: h => h(NodeGuide)
    }
  });
  // 机器节点
  register({
    shape: 'NodeRobot',
    width: constant.NodeDefaultWidth,
    height: constant.NodeDefaultHeight,
    attrs: {
      body: {
        class: themeClass
      }
    },
    component: {
      provide() {
        return {
          graphInstance
        };
      },
      render: h => h(NodeRobot)
    },
    ports: { ...ports }
  });
  // 协作节点
  register({
    shape: 'NodeCollab',
    width: constant.NodeDefaultWidth,
    height: constant.NodeDefaultHeight,
    attrs: {
      body: {
        class: themeClass
      }
    },
    component: {
      provide() {
        return {
          graphInstance
        };
      },
      render: h => h(NodeCollab)
    },
    ports: { ...ports }
  });
}

Graph.registerEdge(
  'EdgeDirection',
  {
    inherit: 'edge',
    attrs: {
      container: {
        connection: true,
        stroke: 'transparent',
        strokeWidth: 'var(--workflow-edge-container-width)',
        strokeLinejoin: 'round',
      },
      line: {
        connection: true,
        stroke: 'var(--workflow-edge-stroke)',
        strokeWidth: 'var(--workflow-edge-stroke-width)',
        strokeLinejoin: 'round',
        targetMarker: {
          name: 'block',
          size: 10,
          open: true,
          tagName: 'path',
          stroke: 'var(--workflow-edge-stroke)',
          strokeWidth: 'var(--workflow-edge-stroke-width)'
        },
      },
    },
    markup: [
      {
        tagName: 'path',
        selector: 'container',
        className: 'edge-direction-container',
        attrs: {
          fill: 'none',
        },
      },
      {
        tagName: 'path',
        selector: 'line',
        className: 'edge-direction',
        attrs: {
          fill: 'none',
        },
      }
    ]
  },
  true,
)

Graph.registerEdge(
  'EdgeDirectionDash',
  {
    inherit: 'edge',
    attrs: {
      container: {
        connection: true,
        stroke: 'transparent',
        strokeWidth: 'var(--workflow-edge-container-width)',
        strokeLinejoin: 'round',
      },
      line: {
        connection: true,
        stroke: 'var(--workflow-edge-dash-stroke)',
        strokeWidth: 'var(--workflow-edge-stroke-width)',
        strokeLinejoin: 'round',
        targetMarker: {
          name: 'block',
          size: 10,
          open: true,
          tagName: 'path',
          stroke: 'var(--workflow-edge-dash-stroke)',
          strokeWidth: 'var(--workflow-edge-stroke-width)'
        },
        strokeDasharray: 'var(--workflow-edge-stroke-dash-array)',
        // style: {
        //   animation: 'ant-line 30s infinite linear',
        // },
      },
    },
    markup: [
      {
        tagName: 'path',
        selector: 'container',
        className: 'edge-dash-container',
        attrs: {
          fill: 'none',
          cursor: 'default',
        },
      },
      {
        tagName: 'path',
        selector: 'line',
        className: 'edge-direction-dash',
        attrs: {
          fill: 'none',
          cursor: 'default',
        },
      }
    ]
  },
  true,
)

export const edgeTargetMarker = () => {
  return {
    stroke: "var(--workflow-edge-stroke)",
    fill: "none",
    transform: "rotate(180)",
    tagName: "path",
    strokeWidth: "var(--workflow-edge-stroke-width)",
    d: "M 10 -5 L 0 0 L 10 5"
  }
}

// x6/es/registry/attr/marker.js --> createMarker() --> options
export const edgeTargetMarkerSelected = () => {
  return {
    stroke: "var(--workflow-edge-selected-stroke)",
    fill: "none",
    transform: "rotate(180)",
    tagName: "path",
    strokeWidth: "var(--workflow-edge-selected-width)",
    d: "M 10 -5 L 0 0 L 10 5"
  }
}

/* 
getBearing 获取方位
isOrthogonal 是否正交
 */

Graph.registerRouter(
  'curve2',
  (vertices, options, edgeView) => {
    const side = options.side || 'bottom';
    const padding = NumberExt.normalizeSides(options.padding || 20);
    const sourceBBox = edgeView.sourceBBox;
    const targetBBox = edgeView.targetBBox;

    // const sourcePoint = sourceBBox.getCenter();
    // const targetPoint = targetBBox.getCenter();

    const sourceAnchor = edgeView.sourceAnchor
    const sourcePoint = sourceAnchor.clone()
    const targetAnchor = edgeView.targetAnchor
    const targetPoint = targetAnchor.clone()

    if (options.dy > 0) {
      // sourcePoint.y = sourceAnchor.y
      sourcePoint.x = sourcePoint.x + options.dy
    }

    let coord;
    let dim;
    let factor;
    switch (side) {
      case 'top':
        factor = -1;
        coord = 'y';
        dim = 'height';
        break;
      case 'left':
        factor = -1;
        coord = 'x';
        dim = 'width';
        break;
      case 'right':
        factor = 1;
        coord = 'x';
        dim = 'width';
        break;
      case 'bottom':
      default:
        factor = 1;
        coord = 'y';
        dim = 'height';
        break;
    }

    sourcePoint[coord] += factor * (sourceBBox[dim] / 2 + padding[side]);
    targetPoint[coord] += factor * (targetBBox[dim] / 2 + padding[side]);

    if (factor * (sourcePoint[coord] - targetPoint[coord]) > 0) {
      targetPoint[coord] = sourcePoint[coord];
    }
    else {
      sourcePoint[coord] = targetPoint[coord];
    }

    return [sourcePoint.toJSON(), ...vertices, targetPoint.toJSON()];
  },
  true,
)


// Graph.registerNode(
//   'NodeConditionPolygon',
//   {
//     inherit: 'polygon',
//     width: 193,
//     height: 65,
//     attrs: {
//       body: {
//         strokeWidth: 1,
//         stroke: '#8f8f8f',
//         fill: '#ffffff',
//         refPoints: '0,10 10,0 20,10 10,20'
//       },
//       text: {
//         fontSize: 12,
//         fill: '#262626'
//       }
//     },
//     markup: [
//       { tagName: 'polygon', selector: 'body' },
//       { tagName: 'text', selector: 'text' },
//     ],
//     ports: { ...ports }
//   },
//   true
// )