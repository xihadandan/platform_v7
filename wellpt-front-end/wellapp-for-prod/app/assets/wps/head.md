## 对接前说明文档
### 对接准备
1. 说明：前期测试验证阶段可以直接使用我们公网的[安装包](http://dev.wpseco.cn/html/development.html).
<img src="../image/developement.png" width = "100%" />
2. 安装后启动WPS会根据（安装目录\Kingsoft\WPS Office\11.8.2.8265\office6\cfgs\oem.ini)中的JSPluginsServer地址在%appdata%\kingsoft\wps\jsaddons\ 下会自动下载jsplugins.xml文件。
<img src="../image/oem.png" width = "100%"/>

3. 通过演示页面启动 wps/et/wpt 会根据jsplugins.xml中的插件地址（url）和版本号(version)自动在%appdata%\kingsoft\wps\jsaddons\ 下载对应的压缩包.
<img src="../image/jsplugin.png" width = "100%"  />
<img src="../image/jsaddons.png" width = "100%"  />

4. 后期如果根据自己的需求需要修改OA插件的代码，需要把我们提供的插件包（oaassit.7z）和配置文件（jsplugins.xml）放在自己的服务器中，并提供能访问jsplugins.xml的地址。
5. 我们的客户经理需要在提供WPS客户端（集成了OA助手版）时把对应地址打包进去，并提供WPS客户端。

### 公网演示地址

[***演示地址***](http://dev.wpseco.cn/html/show.html)
<img src="../image/show.png" width = "100%"  />

### 对接OA助手说明以及文档地址

1. 前端调用说明地址：http://dev.wpseco.cn/apidoc/index.html
2. ***第一步***：下载WPS调用[JS文件](http://dev.wpseco.cn/plugins/openSource/oaassist/20190128/wps.7z)（WPS包、包含wps.js、checkwps.js、launchwps.js。js中依赖jquery 和base64,如果项目中没有也可以用WPS包的。）
    - index.html 是验证的demo，里面有对接的基本用法可以用作参考。
    - apidoc/index.html是输出的说明文档。

3. ***第二步***：如下图所示，页面中引入JS文件：
<img src="../image/index_png.png" width = "100%"  />
4. ***第三步***：方法调用参考说明文档
5. 举例说明：填充模板
    - 需要打开oaassist_3.0/js/function/useTemplate.js文件，填充模板的主方法是UseTemplate(params) 
    - 当前的填充逻辑是根据前端传递的 templateURL （模板路径）下载模板文档，然后在根据dataFromWeb（前台数据） 和 dataFromServer（从服务器获取数据）以此填充到模板的书签中
    - 填充规则，如果dataFromWeb和dataFromServer都传，并且有重叠的数据，服务器数据会覆盖前台传递的数据。
    - 根据自己的逻辑修改代码，关闭WPS客户端，再次通过OA调起的时候会自动加载新的代码。可以在打开的页面通过alt+f12 调出开发者工具，并可以在控制台中调试自己的代码。
