exports.defaultThemeSpecify = {
  colorConfig: {
    themeColor: {
      title: '主题色',
      classify: [
        {
          value: '#488cee',
          code: '--w-primary-color',
          title: '基础主题色',
          default: true,
          formula: false,
          derive: [
            {
              code: '--w-primary-color-1',
              value: '#f6f9fe',
              data: 'color-mix(in srgb, var(--w-primary-color) 5%, white)'
            },
            {
              code: '--w-primary-color-2',
              value: '#edf4fd',
              data: 'color-mix(in srgb, var(--w-primary-color) 10%, white)'
            },
            {
              code: '--w-primary-color-3',
              value: '#dae8fc',
              data: 'color-mix(in srgb, var(--w-primary-color) 20%, white)'
            },
            {
              code: '--w-primary-color-4',
              value: '#a3c6f7',
              data: 'color-mix(in srgb, var(--w-primary-color) 50%, white)'
            },
            {
              code: '--w-primary-color-5',
              value: '#6da3f1',
              data: 'color-mix(in srgb, var(--w-primary-color) 80%, white)'
            },
            {
              code: '--w-primary-color-6',
              value: 'var(--w-primary-color)'
            },
            {
              code: '--w-primary-color-7',
              value: '#3a70be',
              data: 'color-mix(in srgb, var(--w-primary-color) 80%, black)'
            },
            {
              code: '--w-primary-color-8',
              value: '#2b548f',
              data: 'color-mix(in srgb, var(--w-primary-color) 60%, black)'
            },
            {
              code: '--w-primary-color-9',
              value: '#1d385f',
              data: 'color-mix(in srgb, var(--w-primary-color) 40%, black)'
            },
            {
              code: '--w-primary-color-10',
              value: '#0e1c30',
              data: 'color-mix(in srgb, var(--w-primary-color) 20%, black)'
            }
          ]
        }
      ]
    },

    neutralColor: {
      title: '中性色',
      classify: [
        {
          title: '中性色',
          code: '--w-gray-color',
          remark: undefined,
          derive: [
            {
              code: '--w-gray-color-1',
              value: 'white'
            },
            {
              code: '--w-gray-color-2',
              value: '#fafafa',
              data: 'color-mix(in srgb, white , black 2%)'
            },
            {
              code: '--w-gray-color-3',
              value: '#f5f5f5',
              data: 'color-mix(in srgb, white , black 4%)'
            },
            {
              code: '--w-gray-color-4',
              value: '#f2f2f2',
              data: 'color-mix(in srgb, white , black 5%)'
            },
            {
              code: '--w-gray-color-5',
              value: '#e6e6e6',
              data: 'color-mix(in srgb, white , black 10%)'
            },
            {
              code: '--w-gray-color-6',
              value: '#d9d9d9',
              data: 'color-mix(in srgb, white , black 15%)'
            },
            {
              code: '--w-gray-color-7',
              value: '#cccccc',
              data: 'color-mix(in srgb, white , black 20%)'
            },
            {
              code: '--w-gray-color-8',
              value: '#b3b3b3',
              data: 'color-mix(in srgb, white , black 30%)'
            },
            {
              code: '--w-gray-color-9',
              value: '#999999',
              data: 'color-mix(in srgb, white , black 40%)'
            },
            {
              code: '--w-gray-color-10',
              value: '#808080',
              data: 'color-mix(in srgb, white , black 50%)'
            },
            {
              code: '--w-gray-color-11',
              value: '#666666',
              data: 'color-mix(in srgb, white , black 60%)'
            },
            {
              code: '--w-gray-color-12',
              value: '#4d4d4d',
              data: 'color-mix(in srgb, white , black 70%)'
            },
            {
              code: '--w-gray-color-13',
              value: '#333333',
              data: 'color-mix(in srgb, white , black 80%)'
            },
            {
              code: '--w-gray-color-14',
              value: 'black'
            }
          ]
        },
        {
          title: '文字',
          remark: undefined,
          code: '--w-text-color', // 变量前缀
          derive: [
            {
              code: '--w-text-color-darker',
              value: 'var(--w-gray-color-14)'
            },
            {
              code: '--w-text-color-dark',
              value: 'var(--w-gray-color-13)'
            },
            {
              code: '--w-text-color-base',
              value: 'var(--w-gray-color-11)'
            },
            {
              code: '--w-text-color-light',
              value: 'var(--w-gray-color-9)'
            },
            {
              code: '--w-text-color-lighter',
              value: 'var(--w-gray-color-7)'
            },
            {
              code: '--w-text-color-inverse',
              value: 'white'
            }
          ]
        },
        {
          title: '线条',
          remark: undefined,
          code: '--w-border-color',
          derive: [
            {
              code: '--w-border-color-darker',
              value: 'var(--w-gray-color-8)',
              remark: '深色线条/边框'
            },
            {
              code: '--w-border-color-dark',
              value: 'var(--w-gray-color-7)'
            },
            {
              code: '--w-border-color-base',
              value: 'var(--w-gray-color-6)'
            },
            {
              code: '--w-border-color-light',
              value: 'var(--w-gray-color-5)'
            },
            {
              code: '--w-border-color-lighter',
              value: 'var(--w-gray-color-4)'
            },
            {
              code: '--w-border-color-inverse',
              value: '#fff'
            }
          ]
        },
        {
          title: '填充',
          code: '--w-fill-color',
          remark: undefined,
          derive: [
            {
              code: '--w-fill-color-base',
              value: 'var(--w-color-white)'
            },
            {
              code: '--w-fill-color-light',
              value: 'var(--w-gray-color-4)'
            },
            {
              code: '--w-fill-color-dark',
              value: 'var(--w-gray-color-5)'
            },
            {
              code: '--w-fill-color-darker',
              value: 'var(--w-gray-color-7)'
            }
          ]
        },
        {
          title: '背景',
          code: '--w-bg-color',
          remark: undefined,
          derive: [
            {
              code: '--w-bg-color-body',
              value: 'var(--w-color-white)'
            },
            {
              code: '--w-bg-color-popup',
              value: 'var(--w-color-white)'
            },
            {
              code: '--w-bg-color-component',
              value: 'var(--w-color-white)'
            },
            {
              code: '--w-bg-color-popover',
              value: 'var(--w-color-white)'
            },
            {
              code: '--w-bg-color-layout',
              value: 'var(--w-gray-color-4)'
            },
            {
              code: '--w-bg-color-spotlight',
              value: 'var(--w-gray-color-13)'
            }
          ]
        }
      ]
    },

    functionColor: {
      title: '功能色',
      classify: [
        {
          title: '成功色',
          remark: undefined,
          code: '--w-success-color',
          value: '#52c41a',
          formula: false,
          derive: [
            {
              code: '--w-success-color-1',
              value: '#f6fcf4',
              data: 'color-mix(in srgb, var(--w-success-color) 5%, var(--w-color-white))'
            },
            {
              code: '--w-success-color-2',
              value: '#eef9e8',
              data: 'color-mix(in srgb, var(--w-success-color) 10%, var(--w-color-white))'
            },
            {
              code: '--w-success-color-3',
              value: '#dcf3d1',
              data: 'color-mix(in srgb, var(--w-success-color) 20%, var(--w-color-white))'
            },
            {
              code: '--w-success-color-4',
              value: '#a8e28c',
              data: 'color-mix(in srgb, var(--w-success-color) 50%, var(--w-color-white))'
            },
            {
              code: '--w-success-color-5',
              value: '#75d048',
              data: 'color-mix(in srgb, var(--w-success-color) 80%, var(--w-color-white))'
            },
            { code: '--w-success-color-6', value: '#52c41a', data: 'var(--w-success-color)' },
            {
              code: '--w-success-color-7',
              value: '#429d15',
              data: 'color-mix(in srgb, var(--w-success-color) 80%, var(--w-color-black))'
            },
            {
              code: '--w-success-color-8',
              value: '#317610',
              data: 'color-mix(in srgb, var(--w-success-color) 70%, var(--w-color-black))'
            },
            {
              code: '--w-success-color-9',
              value: '#214e0a',
              data: 'color-mix(in srgb, var(--w-success-color) 40%, var(--w-color-black))'
            },
            {
              code: '--w-success-color-10',
              value: '#102705',
              data: 'color-mix(in srgb, var(--w-success-color) 20%, var(--w-color-black))'
            }
          ]
        },

        {
          title: '警告色',
          remark: undefined,
          code: '--w-warning-color',
          value: '#faad14',
          formula: false,
          derive: [
            {
              code: '--w-warning-color-1',
              value: '#fffbf3',
              data: 'color-mix(in srgb, var(--w-warning-color) 5%, var(--w-color-white))'
            },
            {
              code: '--w-warning-color-2',
              value: '#fef7e7',
              data: 'color-mix(in srgb, var(--w-warning-color) 10%, var(--w-color-white))'
            },
            {
              code: '--w-warning-color-3',
              value: '#feefd0',
              data: 'color-mix(in srgb, var(--w-warning-color) 20%, var(--w-color-white))'
            },
            {
              code: '--w-warning-color-4',
              value: '#fcd68a',
              data: 'color-mix(in srgb, var(--w-warning-color) 50%, var(--w-color-white))'
            },
            {
              code: '--w-warning-color-5',
              value: '#fbbd43',
              data: 'color-mix(in srgb, var(--w-warning-color) 80%, var(--w-color-white))'
            },
            {
              code: '--w-warning-color-6',
              value: '#faad14',
              data: 'var(--w-warning-color)'
            },
            {
              code: '--w-warning-color-7',
              value: '#c88a10',
              data: 'color-mix(in srgb, var(--w-warning-color) 80%, var(--w-color-black))'
            },
            {
              code: '--w-warning-color-8',
              value: '#96680c',
              data: 'color-mix(in srgb, var(--w-warning-color) 60%, var(--w-color-black))'
            },
            {
              code: '--w-warning-color-9',
              value: '#644508',
              data: 'color-mix(in srgb, var(--w-warning-color) 40%, var(--w-color-black))'
            },
            {
              code: '--w-warning-color-10',
              value: '#322304',
              data: 'color-mix(in srgb, var(--w-warning-color) 20%, var(--w-color-black))'
            }
          ]
        },

        {
          title: '错误色',
          remark: undefined,
          code: '--w-danger-color',
          value: '#e33033',
          formula: false,
          derive: [
            {
              code: '--w-danger-color-1',
              value: '#fef5f5',
              data: 'color-mix(in srgb, var(--w-danger-color) 5%, var(--w-color-white))'
            },
            {
              code: '--w-danger-color-2',
              value: '#fceaeb',
              data: 'color-mix(in srgb, var(--w-danger-color) 10%, var(--w-color-white))'
            },
            {
              code: '--w-danger-color-3',
              value: '#f9d6d6',
              data: 'color-mix(in srgb, var(--w-danger-color) 20%, var(--w-color-white))'
            },
            {
              code: '--w-danger-color-4',
              value: '#f19899',
              data: 'color-mix(in srgb, var(--w-danger-color) 50%, var(--w-color-white))'
            },
            {
              code: '--w-danger-color-5',
              value: '#e9595c',
              data: 'color-mix(in srgb, var(--w-danger-color) 80%, var(--w-color-white))'
            },
            {
              code: '--w-danger-color-6',
              value: '#e33033',
              data: 'var(--w-danger-color)'
            },
            {
              code: '--w-danger-color-7',
              value: '#b62629',
              data: 'color-mix(in srgb, var(--w-danger-color) 80%, var(--w-color-black))'
            },
            {
              code: '--w-danger-color-8',
              value: '#881d1f',
              data: 'color-mix(in srgb, var(--w-danger-color) 60%, var(--w-color-black))'
            },
            {
              code: '--w-danger-color-9',
              value: '#5b1314',
              data: 'color-mix(in srgb, var(--w-danger-color) 40%, var(--w-color-black))'
            },
            {
              code: '--w-danger-color-10',
              value: '#2d0a0a',
              data: 'color-mix(in srgb, var(--w-danger-color) 20%, var(--w-color-black))'
            }
          ]
        },

        {
          title: '提示色',
          remark: undefined,
          code: '--w-info-color',
          value: '#2aaedd',
          formula: false,
          derive: [
            {
              code: '--w-info-color-1',
              value: '#f4fbfd',
              data: 'color-mix(in srgb, var(--w-info-color) 5%, var(--w-color-white))'
            },
            {
              code: '--w-info-color-2',
              value: '#eaf7fc',
              data: 'color-mix(in srgb, var(--w-info-color) 10%, var(--w-color-white))'
            },
            {
              code: '--w-info-color-3',
              value: '#d4eff8',
              data: 'color-mix(in srgb, var(--w-info-color) 20%, var(--w-color-white))'
            },
            {
              code: '--w-info-color-4',
              value: '#95d7ee',
              data: 'color-mix(in srgb, var(--w-info-color) 50%, var(--w-color-white))'
            },
            {
              code: '--w-info-color-5',
              value: '#55bee4',
              data: 'color-mix(in srgb, var(--w-info-color) 80%, var(--w-color-white))'
            },
            {
              code: '--w-info-color-6',
              value: '#2aaedd',
              data: 'var(--w-info-color)'
            },
            {
              code: '--w-info-color-7',
              value: '#228bb1',
              data: 'color-mix(in srgb, var(--w-info-color) 80%, var(--w-color-black))'
            },
            {
              code: '--w-info-color-8',
              value: '#196885',
              data: 'color-mix(in srgb, var(--w-info-color) 60%, var(--w-color-black))'
            },
            {
              code: '--w-info-color-9',
              value: '#114658',
              data: 'color-mix(in srgb, var(--w-info-color) 40%, var(--w-color-black))'
            },
            {
              code: '--w-info-color-10',
              value: '#08232c',
              data: 'color-mix(in srgb, var(--w-info-color) 20%, var(--w-color-black))'
            }
          ]
        },

        {
          title: '链接色',
          remark: undefined,
          code: '--w-link-color',
          value: '#165dff',
          formula: false,
          derive: [
            {
              code: '--w-link-color-1',
              value: '#f3f7ff',
              data: 'color-mix(in srgb, var(--w-link-color) 5%, var(--w-color-white))'
            },
            {
              code: '--w-link-color-2',
              value: '#e8efff',
              data: 'color-mix(in srgb, var(--w-link-color) 10%, var(--w-color-white))'
            },
            {
              code: '--w-link-color-3',
              value: '#d0dfff',
              data: 'color-mix(in srgb, var(--w-link-color) 20%, var(--w-color-white))'
            },
            {
              code: '--w-link-color-4',
              value: '#8aaeff',
              data: 'color-mix(in srgb, var(--w-link-color) 50%, var(--w-color-white))'
            },
            {
              code: '--w-link-color-5',
              value: '#457dff',
              data: 'color-mix(in srgb, var(--w-link-color) 80%, var(--w-color-white))'
            },
            {
              code: '--w-link-color-6',
              value: '#165dff',
              data: 'var(--w-link-color)'
            },
            {
              code: '--w-link-color-7',
              value: '#124acc',
              data: 'color-mix(in srgb, var(--w-link-color) 80%, var(--w-color-black))'
            },
            {
              code: '--w-link-color-8',
              value: '#0d3899',
              data: 'color-mix(in srgb, var(--w-link-color) 60%, var(--w-color-black))'
            },
            {
              code: '--w-link-color-9',
              value: '#092566',
              data: 'color-mix(in srgb, var(--w-link-color) 40%, var(--w-color-black))'
            },
            {
              code: '--w-link-color-10',
              value: '#041333',
              data: 'color-mix(in srgb, var(--w-link-color) 20%, var(--w-color-black))'
            }
          ]
        }
      ]
    }
  },
  borderConfig: {
    borderStyle: {
      code: '--w-border-style',
      derive: [
        { code: '--w-border-style-base', value: 'solid' },
        { code: '--w-border-style-dashed', value: 'dashed' }
      ]
    },
    borderWidth: {
      code: '--w-border-width',
      derive: [
        { code: '--w-border-width-base', value: '1px', remark: '常规' },
        { code: '--w-border-width-lg', value: '2px', remark: '较粗边框' },
        { code: '--w-border-width-xl', value: '3px', remark: '粗边框' },
        { code: '--w-border-width-2xl', value: '4px', remark: '加粗边框' },
        { code: '--w-border-width-0', value: '0px', remark: '无边框' }
      ]
    }
  },
  heightConfig: {
    code: '--w-height',
    derive: [
      { code: '--w-height-3xs', value: '20px', remark: '如徽标高度、开关高度' },
      { code: '--w-height-2xs', value: '24px', remark: '如简要附件列表行高、树形列表行高' },
      { code: '--w-height-xs', value: '28px', remark: '如小号btninputdropdown等组件高度' },
      { code: '--w-height-sm', value: '32px', remark: '如中号btninputdropdown等组件高度' },
      { code: '--w-height-md', value: '40px', remark: '左侧导航栏行高；底部最小高度' },
      { code: '--w-height-lg', value: '44px', remark: '如大号btninputdropdown等组件高度' },
      { code: '--w-height-xl', value: '48px', remark: '面板标题栏、Tab栏高度、弹窗标题栏高度' },
      { code: '--w-height-2xl', value: '60px', remark: '头部组件高度' },
      { code: '--w-height-3xl', value: '72px', remark: '表单页文本域高度' }
    ]
  },
  radiusConfig: {
    code: '--w-border-radius',
    derive: [
      { code: '--w-border-radius-base', value: '2px', remark: '常规' },
      { code: '--w-border-radius-1', value: '0px', remark: '直角' },
      { code: '--w-border-radius-2', value: '4px', remark: '较大圆角' },
      { code: '--w-border-radius-3', value: '6px', remark: '大圆角' },
      { code: '--w-border-radius-4', value: '16px', remark: '特大圆角' }
    ]
  },
  fontConfig: {
    fontFamily: {
      code: '--w-font-family',
      scope: ['Microsoft YaHei', 'PingFang SC'],
      value: 'Microsoft YaHei',
      remark: undefined
    },
    codeFamily: {
      code: '--w-code-family',
      scope: ['SF Mono', 'Menlo'],
      value: 'SF Mono',
      remark: undefined
    },
    fontSize: {
      title: '字号',
      classify: [
        {
          code: '--w-font-size',
          value: '14px',
          title: '默认字号',
          derive: [
            {
              code: '--w-font-size-6xl',
              value: 'calc(var(--w-font-size) + 34px)'
            },
            {
              code: '--w-font-size-5xl',
              value: 'calc(var(--w-font-size) + 22px)'
            },
            {
              code: '--w-font-size-4xl',
              value: 'calc(var(--w-font-size) + 16px)'
            },
            {
              code: '--w-font-size-3xl',
              value: 'calc(var(--w-font-size) + 10px)'
            },
            {
              code: '--w-font-size-2xl',
              value: 'calc(var(--w-font-size) + 6px)'
            },
            {
              code: '--w-font-size-xl',
              value: 'calc(var(--w-font-size) + 4px)'
            },
            {
              code: '--w-font-size-lg',
              value: 'calc(var(--w-font-size) + 2px)'
            },
            {
              code: '--w-font-size-base',
              value: 'var(--w-font-size)'
            },
            {
              code: '--w-font-size-sm',
              value: 'calc(var(--w-font-size) - 2px)'
            }
          ]
        }
      ]
    },
    fontWeight: {
      code: '--w-font-weight',
      title: '字重',
      derive: [
        {
          code: '--w-font-weight-regular',
          value: '400'
        },
        {
          code: '--w-font-weight-medium',
          value: '500'
        },
        {
          code: '--w-font-weight-semibold',
          value: '600'
        },
        {
          code: '--w-font-weight-bold',
          value: '700'
        }
      ]
    },
    lineHeight: {
      code: '--w-line-height',
      title: '行高',
      value: 'calc(var(--w-font-size) * 1.5715)'
    }
  },
  shadowConfig: {
    levelOne: {
      code: '--w-shadow-1',
      derive: [
        { code: '--w-shadow-1', value: '0 0 4px 0 #000' },
        { code: '--w-shadow-1-up', value: '0 -2px 4px 0 #000' },
        { code: '--w-shadow-1-down', value: '0 2px 4px 0 #000' },
        { code: '--w-shadow-1-left', value: '-2px 0 4px 0 #000' },
        { code: '--w-shadow-1-right', value: '2px 0 4px 0 #000' }
      ]
    },
    levelTwo: {
      code: '--w-shadow-2',
      derive: [
        { code: '--w-shadow-2', value: '0 0 10px 0 #000' },
        { code: '--w-shadow-2-up', value: '0 -4px 10px 0 #000' },
        { code: '--w-shadow-2-down', value: '0 4px 10px 0 #000' },
        { code: '--w-shadow-2-left', value: '-4px 0 10px 0 #000' },
        { code: '--w-shadow-2-right', value: '4px 0 10px 0 #000' }
      ]
    },
    levelThree: {
      code: '--w-shadow-3',
      derive: [
        { code: '--w-shadow-3', value: '0 0 20px 0 #000' },
        { code: '--w-shadow-3-up', value: '0 -8px 20px 0 #000' },
        { code: '--w-shadow-3-down', value: '0 8px 20px 0 #000' },
        { code: '--w-shadow-3-left', value: '-8px 0 20px 0 #000' },
        { code: '--w-shadow-3-right', value: '8px 0 20px 0 #000' }
      ]
    }
  },
  spaceConfig: {
    paddingConfig: {
      code: '--w-padding',
      derive: [
        { code: '--w-padding-0', value: '0px' },
        { code: '--w-padding-3xs', value: '4px' },
        { code: '--w-padding-2xs', value: '8px' },
        { code: '--w-padding-xs', value: '12px' },
        { code: '--w-padding-sm', value: '16px' },
        { code: '--w-padding-md', value: '20px' },
        { code: '--w-padding-lg', value: '24px' },
        { code: '--w-padding-xl', value: '28px' },
        { code: '--w-padding-2xl', value: '32px' },
        { code: '--w-padding-3xl', value: '36px' },
        { code: '--w-padding-4xl', value: '40px' }
      ]
    },
    marginConfig: {
      code: '--w-margin',
      derive: [
        { code: '--w-margin-0', value: '0px' },
        { code: '--w-margin-3xs', value: '4px' },
        { code: '--w-margin-2xs', value: '8px' },
        { code: '--w-margin-xs', value: '12px' },
        { code: '--w-margin-sm', value: '14px' },
        { code: '--w-margin-md', value: '20px' },
        { code: '--w-margin-lg', value: '24px' },
        { code: '--w-margin-xl', value: '28px' },
        { code: '--w-margin-2xl', value: '32px' },
        { code: '--w-margin-3xl', value: '48px' }
      ]
    }
  }
};
