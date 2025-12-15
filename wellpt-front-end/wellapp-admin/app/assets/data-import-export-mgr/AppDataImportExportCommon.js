define(['jquery', 'constant', 'commons', 'appModal'], function ($, constant, commons, appModal) {
  var common = {};
  var ctx = 'http://' + window.location.host;
  var StringBuilder = commons.StringBuilder;

  //导出数据类型
  common.dataTypeListExp = [
    {
      id: 'org_data',
      text: '组织数据',
      children: [
        {
          id: 'version',
          text: '组织版本'
        },
        {
          id: 'user',
          text: '用户'
        },
        {
          id: 'group',
          text: '群组'
        },
        {
          id: 'type',
          text: '组织类型'
        },
        {
          id: 'duty',
          text: '职务'
        },
        {
          id: 'rank',
          text: '职级'
        }
      ]
    },
    {
      id: 'flow_data',
      text: '流程数据',
      children: [
        {
          id: 'flow_data_c',
          text: '全部已办结流程'
        }
      ]
    },
    {
      id: 'email_data',
      text: '邮件',
      children: [
        {
          id: 'email',
          text: '邮件',
          children: [
            {
              id: 'receivce',
              text: '收件',
              disabled: 'disabled'
            },
            {
              id: 'send',
              text: '发件',
              disabled: 'disabled'
            },
            {
              id: 'draft',
              text: '草稿'
            },
            {
              id: 'recovery',
              text: '回收站'
            }
          ]
        },
        {
          id: 'folder',
          text: '文件夹'
        },
        {
          id: 'contact_group',
          text: '联系人分组'
        },
        {
          id: 'contact',
          text: '联系人'
        },
        {
          id: 'tag',
          text: '自定义标签'
        }
        // {
        //   id: '8640036',
        //   text: '信纸'
        // }
      ]
    }
  ];

  //导入数据类型
  common.dataTypeListImp = [
    {
      id: 'org_data',
      text: '组织数据',
      children: [
        {
          id: 'version',
          text: '组织架构'
        },
        {
          id: 'user',
          text: '用户'
        },
        {
          id: 'group',
          text: '群组'
        },
        {
          id: 'duty',
          text: '职务'
        },
        {
          id: 'rank',
          text: '职级'
        },
        {
          id: 'type',
          text: '组织类型'
        }
      ]
    },
    {
      id: 'flow_data',
      text: '流程数据',
      children: [
        {
          id: 'flow_data_c',
          text: '全部已办结流程'
        }
      ]
    },
    {
      id: 'email_data',
      text: '邮件',
      children: [
        {
          id: 'email',
          text: '邮件',
          children: [
            {
              id: 'receivce',
              text: '收件',
              disabled: 'disabled'
            },
            {
              id: 'send',
              text: '发件',
              disabled: 'disabled'
            },
            {
              id: 'draft',
              text: '草稿'
              // }, {
              //   id: 'recovery',
              //   text: '回收站'
            }
          ]
        },
        {
          id: 'folder',
          text: '文件夹'
        },
        {
          id: 'contact_group',
          text: '联系人分组'
        },
        {
          id: 'contact',
          text: '联系人'
        },
        {
          id: 'tag',
          text: '自定义标签'
        }
        // {
        //   id: '8640036',
        //   text: '信纸'
        // }
      ]
    }
  ];

  //导入记录基本信息字段
  common.dataImportParams = [
    {
      param: 'dataType',
      text: '导入数据类型'
    },
    {
      param: 'sourceUuid',
      text: '源数据UUID字段'
    },
    {
      param: 'importFiles',
      text: '导入的数据'
    },
    {
      param: 'importUnitName',
      text: '数据归属'
    },
    {
      param: 'versionName',
      text: '选择组织版本'
    },
    {
      param: 'settingPwd',
      text: '设置登录密码'
    },
    {
      param: 'repeatStrategy',
      text: '数据重复时'
    },
    {
      param: 'errorStrategy',
      text: '数据异常时'
    },
    {
      param: 'operator',
      text: '操作人'
    }
  ];

  //导出记录基本信息字段
  common.dataExportParams = [
    {
      param: 'dataType',
      text: '导出数据类型'
    },
    {
      param: 'systemUnitNames',
      text: '数据归属'
    },
    // {
    //   param: '123',
    //   text: '导入的数据'
    // },
    {
      param: 'exportPath',
      text: '导出到'
    },
    {
      param: 'operator',
      text: '操作人'
    }
  ];

  //0：取消，1：完成，2：导入中，3：异常终止
  common.statusJson = [
    {
      id: 2,
      text: '导入中',
      class: 'status status_488CEE' //蓝色
    },
    {
      id: 1,
      text: '完成',
      class: 'status status_4BB633' //绿色
    },
    {
      id: 0,
      text: '取消',
      class: 'status status_666' //灰色
    },
    {
      id: 3,
      text: '异常终止',
      class: 'status status_E33033' //红色
    }
  ];

  //校验路径
  common.valiPath = function (id, data, msg) {
    var self = this;
    var data = {
      url: '/api/dataIO/checkPath',
      params: { path: data },
      type: 'GET',
      async: true //同步
    };
    self
      .ajaxFunction(data)
      .then(function (res) {
        // console.log("checkPath")
        if (res.code == 0) {
          // if(res.data.code != 0 ){
          self.valiInput(id, true, msg);
          // }
        } else {
          self.valiInput(id, false, res.msg || msg);
        }
      })
      .catch(function (err) {
        // console.error(err.msg);
      });
  };

  //校验输入
  common.valiInput = function (id, data, msg, isfind) {
    var ispass = true;
    if (!data) {
      if (isfind) {
        $(id).find('.error').html(msg).show();
      } else {
        $(id).next('.error').html(msg).show();
      }
      ispass = false;
    } else {
      if (isfind) {
        $(id).find('.error').hide();
      } else {
        $(id).next('.error').hide();
      }
    }
    return ispass;
  };

  //设置千位分隔符
  (common.getDisplayValue = function (value) {
    var self = this;
    if ($.trim(value).length > 0) {
      value = value + '';
      var scale = 0;
      if (value.indexOf('.') > -1) {
        scale = value.length - value.indexOf('.') - 1;
      }
      value = new Number(value).toLocaleString('en', {
        minimumFractionDigits: scale
      });
    }
    return value;
  }),
    //ajax请求
    (common.ajaxFunction = function (data) {
      return new Promise(function (resovle, reject) {
        $.ajax({
          url: ctx + data.url, //接口地址
          type: data.type || 'GET', //方式，默认GET
          data: data.params, //post：要传字符串JSON.stringify(),get:{}
          async: data.async || true, //默认异步
          dataType: 'json',
          contentType: 'application/json',
          success: function (res) {
            resovle(res);
          },
          error: function (err) {
            reject(err);
          }
        });
      });
    });

  return common;
});
