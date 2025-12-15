let portOpcity = 0;
let nodePaddingSize = 15;
let markup = [
  {
    tagName: 'circle',
    selector: 'circle',
    attrs: {
      r: 9,
      fill: '#ffffff',
      stroke: 'var(--w-primary-color-3)',
      style: {
        strokeWidth: 2
      },
      cursor: 'pointer'
    }
  },
  {
    tagName: 'line',
    selector: 'line1',
    attrs: {
      x1: '-5',
      y1: '0',
      x2: '5',
      y2: '0',
      stroke: 'var(--w-primary-color-3)',
      strokeWidth: 2,
      radius: 2,
      cursor: 'pointer'
    }
  },
  {
    tagName: 'line',
    selector: 'line2',
    attrs: {
      x1: '0',
      y1: '-5',
      x2: '0',
      y2: '5',
      stroke: 'var(--w-primary-color-3)',
      strokeWidth: 2,
      radius: 2,
      cursor: 'pointer'
    }
  }
];
let circleMarkup = [
  {
    tagName: 'circle',
    selector: 'circle1',
    attrs: {
      r: 5, // 长度为0表示不显示
      magnet: true,
      stroke: 'var(--w-primary-color)',
      strokeWidth: 1,
      fill: '#fff',
      style: {
        visibility: 'visible'
      },
      opacity: 1
    }
  }
];

const createRightLineGroup = () => {
  return {
    markup: [
      {
        tagName: 'rect',
        selector: 'rightLine',
        attrs: {
          width: 48,
          height: 20,
          y: '-10',
          fill: 'transparent'
        },
        children: [
        ]
      },
      {
        tagName: 'line',
        selector: 'rightLineContent',
        attrs: {
          x1: '0',
          y1: '0',
          x2: '48',
          y2: '0',
          stroke: 'var(--w-primary-color-3)',
          strokeWidth: 2,
        },
      }
    ],
    position: {
      name: 'node-port',
      args: {
        position: 'rightLine'
      }
    },
  }
}

const createBottomLineGroup = () => {
  return {
    markup: [
      {
        tagName: 'rect',
        selector: 'bottomLine',
        attrs: {
          width: 20,
          height: 48,
          x: '-10',
          fill: 'transparent'
        }
      },
      {
        tagName: 'line',
        selector: 'bottomLineContent',
        attrs: {
          x1: '0',
          y1: '0',
          x2: '0',
          y2: '48',
          stroke: 'var(--w-primary-color-3)',
          strokeWidth: 2,
        },
      }
    ],
    position: {
      name: 'node-port',
      args: {
        position: 'bottomLine'
      }
    },
  }
}

let processNodePorts = {
  groups: {
    top: {
      markup: circleMarkup,
      position: {
        name: 'node-port',
        args: {
          position: 'top'
        }
      },
      attrs: {
        circle1: {}
      },
      slient: true
    },
    rightLine: createRightLineGroup(),
    right: {
      markup,
      position: {
        name: 'node-port',
        args: {
          position: 'right'
        }
      },
      attrs: {
        circle: {
          event: 'node:right:addNodeOrItem',
          //   r: 4,
          //   magnet: true,
          //   stroke: '#5F95FF',
          //   strokeWidth: 1,
          //   fill: '#fff',
          //   style: {
          //     visibility: 'visible'
          //   },
          // opacity: portOpcity
        },
        line1: {
          event: 'node:right:addNodeOrItem',
          // opacity: portOpcity
        },
        line2: {
          event: 'node:right:addNodeOrItem',
          // opacity: portOpcity
        }
        // // text: { text: 'right' },
      }
    },
    bottomLine: createBottomLineGroup(),
    bottom: {
      markup,
      position: {
        name: 'node-port',
        args: {
          position: 'bottom'
        }
      },
      attrs: {
        circle: {
          event: 'node:bottom:addNodeOrItem',
          hover: {
            stroke: 'var(--w-primary-color)'
          }
          //   r: 4,
          //   magnet: true,
          //   stroke: '#5F95FF',
          //   strokeWidth: 1,
          //   fill: '#fff',
          //   style: {
          //     visibility: 'visible'
          //   },
          // opacity: portOpcity
        },
        line1: {
          event: 'node:bottom:addNodeOrItem',
          hover: {
            stroke: 'var(--w-primary-color)'
          }
          // opacity: portOpcity
        },
        line2: {
          event: 'node:bottom:addNodeOrItem',
          hover: {
            stroke: 'var(--w-primary-color)'
          }
          // opacity: portOpcity
        }
        // // text: { text: 'bottom' },
      }
    },
    left: {
      markup: circleMarkup,
      position: {
        name: 'node-port',
        args: {
          position: 'left'
        }
      },
      attrs: {
        circle1: {}
      },
      slient: true
    }
  },
  items: [
    {
      group: 'top'
    },
    {
      group: 'rightLine'
    },
    {
      group: 'right'
    },
    {
      group: 'bottomLine'
    },
    {
      group: 'bottom'
    },
    {
      group: 'left'
    }
  ]
};

let itemNodePorts = {
  groups: {
    left: {
      markup: circleMarkup,
      position: 'left',
      attrs: {
        circle1: {}
      }
    },
    top: {
      markup: circleMarkup,
      position: 'top',
      attrs: {
        circle1: {}
      }
    }
  },
  items: [
    {
      group: 'left'
    },
    {
      group: 'top'
    }
  ]
};


// port || port.id
const showNodePort = (cell, port, pathKeys) => {
  if (pathKeys && Array.isArray(pathKeys)) {
    pathKeys.map(item => {
      cell.setPortProp(port, `attrs/${item}/style`, { visibility: 'visible' });
    })
    return
  }
  cell.setPortProp(port, 'attrs/circle/style', { visibility: 'visible' });
  cell.setPortProp(port, 'attrs/line1/style', { visibility: 'visible' });
  cell.setPortProp(port, 'attrs/line2/style', { visibility: 'visible' });
}
// 显示连接桩
const showNodePorts = (cell) => {
  const ports = cell.getPorts();
  ports.forEach(port => {
    if (port.group === 'right' || port.group === 'bottom') {
      showNodePort(cell, port.id)
    }
  });
}
const showNodePortByGroups = (cell, groups = ['right', 'bottom']) => {
  if (!groups) {
    return
  }
  const ports = cell.getPorts();
  if (cell.data.layout === 'horizontal') {
    groups.push('rightLine')
  } else {
    groups.push('bottomLine')
  }
  for (let index = 0; index < ports.length; index++) {
    const port = ports[index];
    if (groups && groups.includes(port.group)) {
      if (port.group === 'rightLine') {
        showNodePort(cell, port.id, ['rightLine', 'rightLineContent'])
      } else if (port.group === 'bottomLine') {
        showNodePort(cell, port.id, ['bottomLine', 'bottomLineContent'])
      } else {
        showNodePort(cell, port.id)
      }
    }
  }
}

const hideNodePort = (cell, port, pathKeys) => {
  if (pathKeys && Array.isArray(pathKeys)) {
    pathKeys.map(item => {
      cell.setPortProp(port, `attrs/${item}/style`, { visibility: 'hidden' });
    })
    return
  }
  cell.setPortProp(port, 'attrs/circle/style', { visibility: 'hidden' });
  cell.setPortProp(port, 'attrs/line1/style', { visibility: 'hidden' });
  cell.setPortProp(port, 'attrs/line2/style', { visibility: 'hidden' });
}
// 隐藏连接桩
const hideNodePorts = (cell) => {
  const ports = cell.getPorts();
  ports.forEach(port => {
    // this.layout.direction  === 'horizontal'
    // if (cell.data.layout === 'horizontal') {
    if (port.group === 'right' || port.group === 'bottom') {
      hideNodePort(cell, port.id)
    }
    // }
  });
}

// 节点布局
let portLayout = function (options) {
  return (portsPositionArgs, elemBBox) => {
    return portsPositionArgs.map((arg, index) => {
      let portLayout = {};
      let currentNode = options.graph.getCellById(arg.cell);
      let nodeData = currentNode.getData();
      // 水平方向的节点
      if (nodeData.layout == 'horizontal') {
        switch (arg.position) {
          case 'top':
            portLayout = {
              position: {
                x: elemBBox.x + elemBBox.width / 2,
                y: elemBBox.y
              },
              angle: 0
            };
            break;
          case 'rightLine':
            portLayout = {
              position: {
                x: elemBBox.x + elemBBox.width,
                y: elemBBox.y + elemBBox.height / 2
              },
              angle: 0
            };
            break;
          case 'right':
            portLayout = {
              position: {
                x: elemBBox.x + elemBBox.width + 24, // 端口右移半径9+n
                y: elemBBox.y + elemBBox.height / 2
              },
              angle: 0
            };
            break;
          case 'bottomLine':
            portLayout = {
              position: {
                x: elemBBox.x + elemBBox.width / 2,
                y: elemBBox.y + elemBBox.height
              },
              angle: 0
            };
            break;
          case 'bottom':
            portLayout = {
              position: {
                x: elemBBox.x + elemBBox.width / 2,
                y: elemBBox.y + elemBBox.height
              },
              angle: 0
            };
            break;
          case 'left':
            portLayout = {
              position: {
                x: elemBBox.x - nodePaddingSize,
                y: elemBBox.y + elemBBox.height / 2
              },
              angle: 0
            };
            break;
        }
      } else {
        // 垂直方向的节点
        switch (arg.position) {
          case 'top':
            portLayout = {
              position: {
                x: elemBBox.x + elemBBox.width / 2,
                y: elemBBox.y
              },
              angle: 0
            };
            break;
          case 'rightLine':
            portLayout = {
              position: {
                x: elemBBox.x + elemBBox.width,
                y: elemBBox.y + elemBBox.height / 2
              },
              angle: 0
            };
            break;
          case 'right':
            portLayout = {
              position: {
                x: elemBBox.x + elemBBox.width,
                y: elemBBox.y + elemBBox.height / 2
              },
              angle: 0
            };
            // }
            break;
          case 'bottomLine':
            portLayout = {
              position: {
                x: elemBBox.x + elemBBox.width / 2,
                y: elemBBox.y + elemBBox.height
              },
              angle: 0
            };
            break;
          case 'bottom':
            portLayout = {
              position: {
                x: elemBBox.x + elemBBox.width / 2,
                y: elemBBox.y + elemBBox.height + 24 // 端口下移半径9+n
              },
              angle: 0
            };
            break;
          case 'left':
            portLayout = {
              position: {
                x: elemBBox.x,
                y: elemBBox.y + elemBBox.height / 2
              },
              angle: 0
            };
            break;
        }
      }
      return portLayout;
    });
  };
};
export {
  processNodePorts,
  itemNodePorts,
  portLayout,
  showNodePort,
  showNodePorts,
  hideNodePort,
  hideNodePorts,
  showNodePortByGroups,
  createRightLineGroup,
  createBottomLineGroup
};
