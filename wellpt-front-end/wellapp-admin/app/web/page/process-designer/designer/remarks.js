const freemarkerScriptRemark = `1、freemarker支持的内置变量：<br/>
        event: 触发类型对应的事件对象，可获取当前流程信息，例如：当前环节id, \${event.taskId}<br/>
        dyFormData: 表单dyFormData对象
        formData: 表单数据对象,可以通过\${formData.表单ID.表单字段}的方式取值，例如：002_\${formData.item_handling_form.project_code};<br/>
        <br/>
        2、其他系统默认的变量：<br/>
        currentUserName ：当前用户的名称<br/>
        currentLoginName ：当前用户的登录名<br/>
        currentUserId ：当前用户ID<br/>
        currentUserUnitId ：当前用户归属的组织ID<br/>
        currentUserUnitName ：当前用户归属的组织名称<br/>
        currentUserDepartmentId ：当前用户归属的部门ID<br/>
        currentUserDepartmentName ：当前用户归属的部门名称<br/>
        sysdate ：当前时间。\${sysdate?datetime}获取当前完整的时间格式
      `;

const groovyScriptRemark = `1、groovy支持的内置变量：<br/>
          event: 触发类型对应的事件对象，可获取当前流程信息，例如：当前环节id, event.taskId<br/>
          dyFormData: 表单dyFormData对象<br/>
          formData: 表单数据对象,可以通过formData.表单ID.表单字段的方式取值，例如："003_" + formData.item_handling_form.project_code;<br/>
          <br/>
          currentUserName ：当前用户的名称<br/>
          currentLoginName ：当前用户的登录名<br/>
          currentUserId ：当前用户ID<br/>
          currentUserUnitId ：当前用户归属的组织ID<br/>
          currentUserUnitName ：当前用户归属的组织名称<br/>
          currentUserDepartmentId ：当前用户归属的部门ID<br/>
          currentUserDepartmentName ：当前用户归属的部门名称<br/>
          sysdate ：当前时间
        `;

const groovyItemEventScriptRemark = `1、groovy支持的内置变量：<br/>
          event: 触发类型对应的事件对象，可获取当前事件信息，例如：事件类型, event.eventType<br/>
          dyFormData: 表单dyFormData对象<br/>
          formData: 表单数据对象,可以通过formData.表单ID.表单字段的方式取值，例如："003_" + formData.item_handling_form.project_code;<br/>
          <br/>
          currentUserName ：当前用户的名称<br/>
          currentLoginName ：当前用户的登录名<br/>
          currentUserId ：当前用户ID<br/>
          currentUserUnitId ：当前用户归属的组织ID<br/>
          currentUserUnitName ：当前用户归属的组织名称<br/>
          currentUserDepartmentId ：当前用户归属的部门ID<br/>
          currentUserDepartmentName ：当前用户归属的部门名称<br/>
          sysdate ：当前时间
        `;

const groovyEntityTimerScriptRemark = `1、groovy支持的内置变量：<br/>
          processInstance: 业务流程实例对象<br/>
          entityTimer: 业务实体计时器对象<br/>
          tsTimer: 计时服务计时器对象<br/>
          dyFormData: 业务主体表单dyFormData对象<br/>
          formData: 业务主体表单数据对象,可以通过formData.表单ID.表单字段的方式取值，例如："003_" + formData.item_handling_form.project_code;<br/>
          <br/>
          currentUserName ：当前用户的名称<br/>
          currentLoginName ：当前用户的登录名<br/>
          currentUserId ：当前用户ID<br/>
          currentUserUnitId ：当前用户归属的组织ID<br/>
          currentUserUnitName ：当前用户归属的组织名称<br/>
          currentUserDepartmentId ：当前用户归属的部门ID<br/>
          currentUserDepartmentName ：当前用户归属的部门名称<br/>
          sysdate ：当前时间
        `;

export { freemarkerScriptRemark, groovyScriptRemark, groovyItemEventScriptRemark, groovyEntityTimerScriptRemark };
