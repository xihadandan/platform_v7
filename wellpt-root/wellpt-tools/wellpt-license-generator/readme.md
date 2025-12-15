###wellpt-license-generator说明

运行LicensePublisher的main函数即可进行许可证的生成、安装、验证处理，项目的唯一识别码，例如机器码，从平台的系统设置->许可管理页面中获取

许可证使用的私钥是自签名的测试密钥，正式的许可生成应隔离部署使用，确保私钥不泄露，并进行规范化的许可申请、审批、发布等流程化的管理

生成的license.lic文件数据组成
	aes密钥数据长度
	aes密钥数据
	license内容加密数据长度,
	license内容加密数据,
	license内容签名算法base64编码数据长度
	license内容签名算法base64编码数据
	license内容签名数据长度,
	license内容签名数据

资源文件说明
1、privateKeys.store
私钥密码库

2、publicCerts.store
公钥密码库

3、certfile.cer
证书

4、license-generator.properties
许可证生成配置文件

5、license-authenticator.properties
许可证验证配置文件
