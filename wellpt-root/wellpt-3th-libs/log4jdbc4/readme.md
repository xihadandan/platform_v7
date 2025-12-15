##平台对sql打印日志的修改，依赖wellpt-security-support的TenantContextHolder类，获取当前登录租户信息。


##注意：打包的时候，需要先单独引入wellpt-security-support.jar到编译环境。（不要在pom.xml添加该包依赖，否则会造成maven依赖循环）