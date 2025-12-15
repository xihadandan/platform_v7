###cipher-maven-plugin说明

class代码加密插件
1、加密信息及class等元数据生成到META-INF\resources\WEB-INF\{项目artifactId}.mdb文件
2、class加密数据生成到META-INF\resources\WEB-INF\{项目artifactId}.cdb文件
3、原class进行方法体清空处理

TODO
元数据文件格式版本化生成，结构设计
mdb文件数据组成结构
	版本号
	密码套件名的base64编码字节长度
	密码套件名的base64编码字节
	加密项目名的base64编码字节长度
	加密项目名的base64编码字节
	对称加密算法的密码器的base64编码字节长度
	对称加密算法的密码器的base64编码
	对称加密算法的base64编码字节长度
	对称加密算法的base64编码
	加密密钥长度
	加密密钥数据
	初始化加密向量长度
	初始化加密向量数据
	class文件1类名索引
	class文件1加密数据长度
	class文件2类名索引
	class文件2加密数据长度
	...
	class文件n加密数据长度
	class文件n类名索引
	
cdb文件数据组成结构
	class文件1加密数据
	class文件2加密数据
	...
	class文件n加密数据
	
	