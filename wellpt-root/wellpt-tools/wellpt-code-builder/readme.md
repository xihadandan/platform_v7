## 说明

代码生成工具使用maven构建，目前仅支持自动生成基于平台代码规范的`Entity/Service/Dao/FacadeService`的代码接口与实现类，代码文件会按照包目录结构生成。


###配置文件说明

配置文件在`\bin\config.properties`

- dbUrl：连接数据库的URL
- dbUser：连接数据库的用户名
- dbPassword：连接数据库的密码
- tables：需要自动生成代码的表，多个表用英文格式逗号`,`  进行分隔
- javaPackage：java代码的包目录
- outputDir：输出代码的路径，默认是`target\代码`目录
- author：代码注释的作者



###执行说明
进入bin目录，有两个批处理，按顺序执行前，请按照自己的需要对配置文件进行修改。