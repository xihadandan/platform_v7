import G6 from '@antv/g6';

G6.registerNode(
  'icon-node',
  {
    options: {
      size: [60, 20],
      stroke: '#488CEE',
      fill: '#488CEE'
    },
    draw(cfg, group) {
      const styles = this.getShapeStyle(cfg);
      const { labelCfg = {} } = cfg;
      let padding = 30;
      // let calculateLabelWidth = label => {
      //   let w = 0;
      //   label.split('').forEach(l => {
      //     w += G6.Util.getLetterWidth(l, cfg.labelCfg.style.fontSize);
      //   });
      //   return w;
      // };
      //G6.Util.getLetterWidth
      styles.width = G6.Util.getTextSize(cfg.label, cfg.labelCfg.style.fontSize)[0];
      styles.width += padding;
      const h = styles.height;
      const keyShape = group.addShape('rect', {
        attrs: {
          ...styles,
          x: -padding / 2
        }
      });

      if (cfg.label) {
        group.addShape('text', {
          attrs: {
            ...labelCfg.style,
            text: cfg.label,
            y: h / 5
          }
        });
      }

      return keyShape;
    },
    update: undefined
  },
  'rect'
);

G6.registerEdge('flow-line', {
  draw(cfg, group) {
    const startPoint = cfg.startPoint;
    const endPoint = cfg.endPoint;

    const { style } = cfg;
    const shape = group.addShape('path', {
      attrs: {
        stroke: style.stroke,
        endArrow: style.endArrow,
        lineWidth: style.lineWidth,
        path: [
          ['M', startPoint.x, startPoint.y],
          ['L', startPoint.x, (startPoint.y + endPoint.y) / 2],
          ['L', endPoint.x, (startPoint.y + endPoint.y) / 2],
          ['L', endPoint.x, endPoint.y]
        ]
      }
    });

    return shape;
  }
});

// 悬停
const defaultStateStyles = {
  hover: {
    stroke: '#E8F1FF',
    lineWidth: 2
  }
};

// 节点
const defaultNodeStyle = {
  fill: '#488CEE',
  stroke: '#488CEE',
  radius: 4
};

// 线
const defaultEdgeStyle = {
  stroke: '#E8F1FF',
  lineWidth: 2,
  endArrow: {
    path: 'M 0,0 L 12, 6 L 9,0 L 12, -6 Z',
    fill: '#FF0',
    d: 0
  }
};

const defaultLayout = {
  type: 'compactBox',
  direction: 'TB',
  getId: function getId(d) {
    return d.id;
  },
  // getHeight: function getHeight() {
  //   return 16;
  // },
  // getWidth: function getWidth(d) {
  //   return 100;
  // },
  getVGap: function getVGap() {
    return 60;
  },
  getHGap: function getHGap(d) {
    return 20;
  }
};

const defaultLabelCfg = {
  style: {
    fill: '#FFFFFF',
    fontSize: 16
  }
};

export function draw(id, data, noMinimap) {
  const container = document.getElementById(id);
  const width = container.scrollWidth;
  const height = container.scrollHeight || 500;

  const minimap = new G6.Minimap({
    size: [150, 100]
  });

  const graph = new G6.TreeGraph({
    container: id,
    width,
    height,
    linkCenter: true,
    plugins: noMinimap ? [] : [minimap],
    modes: {
      default: ['drag-canvas', 'zoom-canvas']
    },
    defaultNode: {
      type: 'icon-node',
      size: [130, 48],
      style: defaultNodeStyle,
      labelCfg: defaultLabelCfg
    },
    defaultEdge: {
      type: 'flow-line',
      style: defaultEdgeStyle
    },
    nodeStateStyles: defaultStateStyles,
    edgeStateStyles: defaultStateStyles,
    layout: defaultLayout
  });

  // G6.Util.traverseTree(data, d => {
  //   d.leftIcon = {
  //     style: {
  //       fill: '#e6fffb',
  //       stroke: '#e6fffb'
  //     }
  //   };
  //   return true;
  // });
  // console.log(JSON.stringify(data));
  graph.data(data);
  graph.render();
  graph.fitCenter();
  // graph.fitView();

  graph.on('node:mouseenter', evt => {
    const { item } = evt;
    graph.setItemState(item, 'hover', true);
  });

  graph.on('node:mouseleave', evt => {
    const { item } = evt;
    graph.setItemState(item, 'hover', false);
  });

  graph.on('node:click', evt => {});

  if (typeof window !== 'undefined')
    window.onresize = () => {
      if (!graph || graph.get('destroyed')) return;
      if (!container || !container.scrollWidth || !container.scrollHeight) return;
      graph.changeSize(container.scrollWidth, container.scrollHeight);
    };

  return graph;
}
