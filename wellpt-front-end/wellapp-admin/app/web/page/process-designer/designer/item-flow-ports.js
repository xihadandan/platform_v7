let portAttrs = {
  circle: {
    r: 5,
    magnet: true,
    stroke: '#999999',
    strokeWidth: 1,
    fill: '#fff',
    opacity: 1
  }
};
let itemFlowPorts = {
  groups: {
    top: {
      position: 'top',
      attrs: {
        ...portAttrs
      }
    },
    right: {
      position: 'right',
      attrs: {
        ...portAttrs
      }
    },
    bottom: {
      position: 'bottom',
      attrs: {
        ...portAttrs
      }
    },
    left: {
      position: 'left',
      attrs: {
        ...portAttrs
      }
    }
  },
  items: [
    {
      group: 'top'
    },
    {
      group: 'right'
    },
    {
      group: 'bottom'
    },
    {
      group: 'left'
    }
  ]
};
let itemFlowRightPorts = {
  groups: {
    right: {
      position: 'right',
      attrs: {
        ...portAttrs
      }
    }
  },
  items: [
    {
      group: 'right'
    }
  ]
};
let itemFlowLeftPorts = {
  groups: {
    left: {
      position: 'left',
      attrs: {
        ...portAttrs
      }
    }
  },
  items: [
    {
      group: 'left'
    }
  ]
};

let deleteToolMarkup = {
  markup: [
    {
      tagName: 'rect',
      selector: 'body',
      attrs: {
        width: 32,
        height: 32,
        fill: '#ffffff',
        stroke: 'ffffff',
        strokeWidth: 4,
        rx: 4,
        ry: 4,
        cursor: 'pointer'
      }
    },
    {
      tagName: 'g',
      shape: 'path',
      selector: 'path',
      children: [
        {
          tagName: 'path',
          selector: 'delicon1',
          attrs: {
            fill: '#333333',
            d: 'M10.969,2.812H7.031c-0.466,0-0.844-0.378-0.844-0.844l0,0c0-0.466,0.378-0.844,0.844-0.844h3.938  c0.466,0,0.844,0.378,0.844,0.844l0,0C11.812,2.435,11.435,2.812,10.969,2.812z'
          }
        },
        {
          tagName: 'path',
          selector: 'delicon2',
          attrs: {
            fill: '#333333',
            d: 'M16.031,3.938h-0.844H2.812H1.969c-0.466,0-0.844,0.378-0.844,0.844c0,0.466,0.378,0.844,0.844,0.844h0.844v10.406  c0,0.466,0.378,0.844,0.844,0.844h10.688c0.466,0,0.844-0.378,0.844-0.844V5.625h0.844c0.466,0,0.844-0.378,0.844-0.844  S16.497,3.938,16.031,3.938z M13.5,15.188h-9V5.625h9V15.188z'
          }
        },
        {
          tagName: 'path',
          selector: 'delicon3',
          attrs: {
            fill: '#333333',
            d: 'M7.031,13.5L7.031,13.5c-0.466,0-0.844-0.378-0.844-0.844v-4.5c0-0.466,0.378-0.844,0.844-0.844l0,0  c0.466,0,0.844,0.378,0.844,0.844v4.5C7.875,13.122,7.497,13.5,7.031,13.5z'
          }
        },
        {
          tagName: 'path',
          selector: 'delicon4',
          attrs: {
            fill: '#333333',
            d: 'M10.969,13.5L10.969,13.5c-0.466,0-0.844-0.378-0.844-0.844v-4.5c0-0.466,0.378-0.844,0.844-0.844l0,0  c0.466,0,0.844,0.378,0.844,0.844v4.5C11.812,13.122,11.435,13.5,10.969,13.5z'
          }
        }
      ],
      attrs: {
        ref: 'body',
        style: 'transform: translate(7px, 7px);',
        rx: 4,
        ry: 4
      }
    }
  ]
};

export { itemFlowPorts, deleteToolMarkup };
