一、服务器部署说明（参见文档《BCIAJavaChain平台CA节点子系统-服务器部署手册.docx》）
1、范围
	本部署文档规定了BCIA Javachain CA服务端部署在Ubuntu系统上的标准步骤。供BCIA javachain的平台开发者、应用开发者及应用最终用户参考使用。

2、CA服务端环境要求
	1）	操作系统支持Ubuntu 16.04 x64系统；
	2）	内存要求8GB 大小；
	3）	硬盘要求100GB 大小；
	4）	JAVA要求 Oracle JDK 1.8以上；
	5）	安装mogodb数据库

3、服务器部署步骤
	3.1	部署
		使用前需要首先手动建立工作目录（例如：/home/user/ca-server）, 然后将服务器打包的JAR包放入该目录下，并且将日志的默认配置配置文件logback.xml放入当前目录下的config目录，还要讲RA的配置文件ratk.properties放入其中。
		 
		在工作目录下建立CFCA目录，表示默认使用的CFCA 的CA，然后在目录中放入CA的根证书链文件以及RA的双向通信证书等。
		执行Linux命令:
		nohup java –jar RAServer-****.jar 2>&1>/dev/null &
		上述命令中nohup是后台执行命令;
		2>&1>/dev/null 将所有输出输出到空设备,因为有日志记录;
		此时服务器进程将运行到后台,日志目录RAServerLogs 会生成在工作目录下,8089端口会被监听。具体目录结构如下图所示:
		  
		如果需要变更端口等，需要使用java运行时参数-D另行指定配置文件路径:
		-Dspring.config.location=./config/config.properties
		指定日志配置文件路径:
		-Dlogback.configurationFile=./config/logback.xml
		完整版命令如下所示：
		nohup java -Dlogback.configurationFile=./config/logback.xml -Dspring.config.location=./config/application-prod.properties -jar  RAServer-xxxx.jar 2>&1>/dev/null &

	3.2	配置文件说明
		3.2.1	application-prod.properties文件
			config目录下application-prod.properties文件如下：
			 
			server.port 指定服务器的监听端口。
			logging.config 指定日志配置文件路径。
			ca.name 指定CA名称。
			ca.chainfile指定CA证书链文件。
			spring.data.mongodb.uri 指定数据库连接。
		3.2.2	ratk.properties文件
			config目录下ratk.properties文件如下：
			 
			ratk.http.warningTime：日志关键操作报警时间，单位毫秒
			ratk.http.connect.timeout：连接RA服务器超时时间，单位毫秒
			ratk.http.read.timeout：读取RA服务器超时时间，单位毫秒
			其他参数是连接RA服务器采用https连接时的通信参数。

	3.3	mogodb数据库部署
		服务器：下载压缩包后解压启动，默认没有mongo.conf  （账号与数据库根据官方文档创建）。
		 
		备注：日志目录和数据存储路径需要自己手工创建。
		数据库默认端口27017。
		 
		注意：需要执行两条数据库语句
		1、增加admin数据库的用户，需要创建用户名密码，用户名 adminUser，使用密码 adminPass。
		db.createUser({ user: "adminUser", pwd: "adminPass", roles: [{ role: "userAdminAnyDatabase", db: "admin" }] })
		  
		2、创建注册用户表t_registryuser，并插入一条管理员数据：
		用户名 ： admin
		密码 ： YWRtaW46MTIzNA==
		use admin;
		db.createCollection("t_registryuser")
		db.t_registryuser.insert({
		  "caName": "CFCA",
		  "name": "admin",
		  "pass": "YWRtaW46MTIzNA==",
		  "type": "client",
		  "affiliation": "",
		  "attributes": [
			{
			  "name": "hf.Registrar.Roles",
			  "value": "client,user,peer,validator,auditor",
			  "eCert": false
			},
			{
			  "name": "hf.Registrar.DelegateRoles",
			  "value": "client,user,validator,auditor",
			  "eCert": false
			},
			{
			  "name": "hf.Revoker",
			  "value": "true",
			  "eCert": false
			}
		  ],
		  "maxEnrollments": -1,
		  "state": 0
		})

	3.4	部署常见问题
		3.4.1	端口已经被其他应用监听
			默认监听端口是8089,请保证该端口没有其他应用程序监听。使用netstat 查看。
		3.4.2	没有建立CFCA目录导致服务失败
			因为这些命令都需要用到CA的证书，该证书将用来验签或者getcainfo命令时下发证书。
			
=======================================================================================================================================
二、服务器使用说明（参见文档《BCIAJavaChain平台CA节点子系统-命令行用户操作手册.docx》）
	1、项目描述
	该项目的主要内容是为JavaChain平台提供CA服务器，主要功能包括：申请下载，重新申请并下载以及吊销SM2 国密算法签名的证书，管理用户信息和权限，管理CA信息。
		1.1	文档目标
		CA子系统服务器系统使用说明书。
		1.2	读者对象
		供BCIA javachain的平台开发者、应用开发者及应用最终用户参考使用。
		
	2、环境需求
		2.1	部署需求
		JAVA环境需要Oracle JDK 1.8以上，具体部署步骤参阅《JavaChain平台CA节点子系统服务器_部署手册》。

		2.2	证书要求
		2.2.1	CA服务器签名证书
		必须要求是使用 SM2 国密算法签名的证书，来自于自建RA所连接的CA的签名证书或者证书链文件。必须事先将该证书放置于指定CA名称的目录下（/工作目录/CA_Name/），便于getcainfo命令下载以及enroll命令需要。
		2.2.2	CA签证的身份证书
		必须要求是使用 SM2 国密算法签名的证书。
		2.2.3	RA通信证书配置
		在config目录下配置ratk.properties文件。其中指定连接RA的双向SSL通信证书：
		ratk.https.keyStorePath=CFCA/ratk-server-cs.jks
		ratk.https.keyStorePassword=cfca1234
		ratk.https.trustStorePath=CFCA/ratk-server-cs.jks
		ratk.https.trustStorePassword=cfca1234

	 
	3、使用说明
		3.1	Getcainfo服务使用
			用户通过命令行客户端或者Restful接口进行访问，返回给用户指定CA的p7b证书链文件(是二进制编码,没有经过Base64编码的)以及CA的相关信息，比如名称，版本等。
			注意，使用时首先必须将ca的P7B证书链文件(是二进制编码,没有经过Base64编码的)放入指定CA名称目录下的，要保证该目录下有证书文件。
		3.2	Enroll服务使用
			用户通过命令行客户端或者Restful接口进行访问，返回给用户身份证书(ECert)，之后用户除了GetCaInfo 操作的所有操作，都会使用身份证书(ECert)进行签名。同时，服务器会绑定用户与身份证书，用于后续操作签名的验签。
			注意，使用时首先必须将ca的证书和证书链文件放入指定CA名称目录下的，要保证该目录下有证书文件和证书链文件。
			3.2.1	Enrollment身份证书管理
				在用户调用enroll 命令对应的Restful接口后，服务器会将生成的身份证书（ECert）与调用者的身份进行绑定，绑定的方式是：身份证书会以证书的 SubjectName作为文件名进行存储，存储的目录在/工作目录/ <对应CA 名称>/目录下，之后用户的其他操作都会采用身份证书签名来生成  token，token会放入报文的头部，token 的结构是B64SubjectNameAndBody.B64Signature，其中就有证书的 SubjectName以告知服务器用哪一个公钥证书来验签。

		3.3	Reenroll服务使用
			用户通过命令行客户端或者Restful接口进行访问，假如你的证书期满终止了，可以重新enroll一个。成功则返回给用户新的身份证书(ECert)，之后用户除了GetCaInfo 操作的所有操作，都会使用新的身份证书(ECert)进行签名。同时，服务器会绑定用户与身份证书，用于后续操作签名的验签。
			注意，使用时首先必须将ca的证书和证书链文件放入指定CA名称目录下的，要保证该目录下有证书文件和证书链文件。
			3.3.1	Reenrollment身份证书管理
				在用户调用reenroll 命令或者reenroll对应的Restful接口后，服务器会将生成的身份证书（ECert）与调用者的身份进行绑定，绑定的方式是：身份证书会以证书的 SubjectName作为文件名进行存储，存储的目录在/工作目录/ <对应CA 名称>/目录下，之后用户的其他操作都会采用身份证书签名来生成  token，token会放入报文的头部，token 的结构是B64SubjectNameAndBody.B64Signature，其中就有证书的 SubjectName以告知服务器用哪一个公钥证书来验签。

		3.4	Register服务使用
			该服务提供用户的注册功能，成功注册则返回该用户的服务器加密密码。
			执行注册请求的身份必须是已经执行过enroll过的，并且还必须具有正在注册的身份类型的适当权限(该版本权限管理暂不支持)。
			权限管理主要分三方面：
			（1）	需要支持注册服务商（即调用者）必须具有用逗号分隔值列表表示的“hf.Registrar.Roles”属性，其中一个值等于正在注册的标识类型; 例如，如果注册服务商的“hf.Registrar.Roles”属性的值为“peer，app，user”，则注册服务商可以注册peer，app和user类型的身份，但不能注册 order 身份。
			（2）	注册的发起者所具备的分组隶属关系必须是将注册的用户的分组隶属关系的前缀或者直接就是这个隶属关系，也就是说分组隶属关系必须具有继承性。例如，一个调用者的从属关系是“a.b”，那么他可以register一个拥有“a.b.c”的用户实例，但不能是“a.c”。
			（3）	如果满足以下所有条件，才可以注册用户属性：
			a)	只有当注册的发起者拥有该属性并且它是hf.Registrar.Attributes属性值的其中之一时，注册服务商才能注册具有前缀“hf.”的 CA保留属性。 此外，如果属性是类型列表，则被注册的属性的值必须等于或者是注册发起者具有的值的子集。 如果该属性的类型为布尔型，则注册发起者只有在自己这个属性的值为‘true’的情况下才能注册该属性。
			b)	注册自定义属性（即名称不以'hf.'开头的任何属性）要求注册d发起者已经注册了“hf.Registar.Attributes”属性。 唯一支持的模式是末尾带有“*”的字符串。 例如，“a.b. *”是匹配以“a.b.”开头的所有属性名称的模式。 例如，如果注册发起者的hf.Registrar.Attributes = orgAdmin，则注册服务商可以添加或删除身份的唯一属性是'orgAdmin'属性。
			3.4.1	Register用户信息管理
				会将注册的用户信息保留在数据库admin/t_registryuser/表中
		
		3.5	Revoke服务使用
			用户通过命令行客户端或者Restful接口进行访问，吊销制定序列号的证书。
			撤销一个用户会撤销他的所有证书，并阻止他再得到新的证书。撤销一个证书只是使一个证书无效。该版本只支持撤销一个证书,并没有管理用户名下申请的证书。
		3.6	更换CA
			首先修改config/ application-prod.properties中字段ca.name
			ca.name=XXXXX
			然后在jar包目录创建对应名称的目录，目录下需要放入证书链文件以及通信证书jks。
			 
			请增加admin数据库中表t_registryuser 中的admin管理员用户名和密码，并且设置caName字段为对应的CA名称。

	4、HTTP接口定义
		4.1	接口概要
			CA给应用接入方提供的接口采用http协议。所有参数组装成JSON字符串，其中JSON的key严格遵循请求参数名称，将组装好的JSON放入http body域中用POST方式发送到请求地址。设置http请求的Header为“Content-Type=application/json;charset=UTF-8”。并需新增一个HEAD：“ca-protocol-version=0.0.1”，用来描述本协议内容的版本号。
			4.1.1	接口协议版本
				本接口协议版本号为：0.0.1。
		4.2	获取CA信息接口（cainfo）
			获取CA服务的基本信息。
			4.2.1	接口地址
				http://IP:Port/cainfo
			4.2.2	HTTP请求方式
				Post 方式，请求参数置于报文body中。
			4.2.3	同步异步
				同步。
			4.2.4	请求参数
				参数	参数名称	类型	参数说明	是否可空	样例
				业务参数
				caname	ca名称	String	"caname": "string"指定ca名称	不可空	CFCA

			4.2.5	返回参数
				参数	参数名称	类型	参数说明	是否可空	样例
				业务参数
				Result	处理结果	JsonObject	{“caname”:ca名称(string), “cachain”:ca证书链文件的B64编码字符串}	可空	
				Success	成功标识	boolean	布尔值，指示请求是否成功	不可空	true
				Errors	处理错误	array	每个元素是一个对象{ code:xxxxx,message:xxxx}
				Success为false时,会有值	可空	一组错误消息（即代码和字符串消息）
				Messages	处理信息	array	每个元素是一个对象{ code:xxxxx,message:xxxx}	可空	一系列信息性消息（即代码和字符串消息）

		4.3	签发证书接口（enroll）
			接收用户信息，进行用户登记，签发证书接口。
			4.3.1	接口地址
				http://IP:Port/enroll
			4.3.2	HTTP请求方式
				Post 方式，请求参数置于报文body中。
			4.3.3	同步异步
				同步。
			4.3.4	请求参数
				参数	参数名称	类型	参数说明	是否可空	样例
				业务参数
				request	请求详细信息	String	构建的csr的b64字符串	不可空	样例太长,自行生成
				profile	profile信息	String	签名模板，暂时不传	可空	
				label	label信息	String	HSM操作中使用的标签	不可空	暂时可以传空字符串
				caname	ca名称	String	请求指定的CA名称	不可空	CFCA
			4.3.5	返回参数
				参数	参数名称	类型	参数说明	是否可空	样例
				业务参数
				Result	处理结果	String	Base64编码格式的身份证书, Success为true时,会有值	可空	样例太长,自行生成
				Success	成功标识	boolean	布尔值，指示请求是否成功	不可空	true
				Errors	处理错误	array	每个元素是一个对象{ code:xxxxx,message:xxxx}
				Success为false时,会有值	可空	一组错误消息（即代码和字符串消息）
				Messages	处理信息	array	每个元素是一个对象{ code:xxxxx,message:xxxx}	可空	一系列信息性消息（即代码和字符串消息）
		4.4	重新签发证书接口（reenroll）
			用户重新登记和签发证书接口。
			4.4.1	接口地址
				http://IP:Port/reenroll
			4.4.2	HTTP请求方式
				Post 方式，请求参数置于报文body中。
			4.4.3	同步异步
				同步。
			4.4.4	请求参数
				参数	参数名称	类型	参数说明	是否可空	样例
				业务参数
				request	请求详细信息	String	构建的csr的b64字符串 	不可空	样例太长,自行生成
				profile	profile信息	String	签名模板，暂时不传	可空	
				label	label信息	String	HSM操作中使用的标签	不可空	暂时可以传空字符串
				caname	ca名称	String	请求指定的CA名称	不可空	CFCA

			4.4.5	返回参数
				参数	参数名称	类型	参数说明	是否可空	样例
				业务参数
				Result	处理结果	String	Base64编码格式的身份证书, Success为true时,会有值	可空	样例太长,自行生成
				Success	成功标识	boolean	布尔值，指示请求是否成功	不可空	true
				Errors	处理错误	array	每个元素是一个对象{ code:xxxxx,message:xxxx}
				Success为false时,会有值	可空	一组错误消息（即代码和字符串消息）
				Messages	处理信息	array	每个元素是一个对象{ code:xxxxx,message:xxxx}	可空	一系列信息性消息（即代码和字符串消息）
		4.5	注册用户实体身份接口（register）
			接收用户信息，进行新用户注册接口。
			4.5.1	接口地址
				http://IP:Port/register
			4.5.2	HTTP请求方式
				Post 方式，请求参数置于报文body中。
			4.5.3	同步异步
				同步。
			4.5.4	请求参数
				参数	参数名称	类型	参数说明	是否可空	样例
				业务参数
				id	id	String	用户id	不可空	test
				type	类型	String	类型	不可空	user
				secret	密码	String	密码	不可空	xxxxxx
				max_enrollments	数量	Integer	最大证书数量	不可空	4
				affiliation_path	路径	String	路径	不可空	org.department
				attrs	注册属性	Json	"attrs": [
					{
					  "name": "string",
					  "value": "string"
					}
				  ],用数组传入多个属性值	不可空	
				caname	ca名称	String	ca名称	不可空	CFCA

			4.5.5	返回参数
				参数	参数名称	类型	参数说明	是否可空	样例
				业务参数
				Result	处理结果	JsonObject	新注册身份的base64编码注册密码。	可空	
				Success	成功标识	boolean	布尔值，指示请求是否成功	不可空	true
				Errors	处理错误	array	每个元素是一个对象{ code:xxxxx,message:xxxx}
				Success为false时,会有值	可空	一组错误消息（即代码和字符串消息）
				Messages	处理信息	array	每个元素是一个对象{ code:xxxxx,message:xxxx}	可空	一系列信息性消息（即代码和字符串消息）
		4.6	吊销证书接口（revoke）
			吊销制定序列号的证书。
			4.6.1	接口地址
				http://IP:Port/revoke
			4.6.2	HTTP请求方式
				Post 方式，请求参数置于报文body中。
			4.6.3	同步异步
				同步。
			4.6.4	请求参数
				参数	参数名称	类型	参数说明	是否可空	样例
				业务参数
				id	Id	String	用户名	不可空	test
				aki	Aki	String	要撤销的证书的授权密钥标识符（Authority Key Identifier）	不可空	“”
				serial	Serial	String	证书序列号	不可空	1032137068
				reason	Reason	String	吊销原因	不可空	expire
				caname	Caname	String	Ca名称	不可空	CFCA

			4.6.5	返回参数
				参数	参数名称	类型	参数说明	是否可空	样例
				业务参数
				Result	处理结果	String	吊销相关信息	可空	
				Success	成功标识	boolean	布尔值，指示请求是否成功	不可空	true
				Errors	处理错误	array	每个元素是一个对象{ code:xxxxx,message:xxxx}
				Success为false时,会有值	可空	一组错误消息（即代码和字符串消息）
				Messages	处理信息	array	每个元素是一个对象{ code:xxxxx,message:xxxx}	可空	一系列信息性消息（即代码和字符串消息）
		4.7	批量查询证书（tcert）
			批量查询证书接口。该版本暂不支持

	5、HTTP请求示例
		POST http://<host:post>/reenroll/ HTTP/1.1
		Host: ca.bcia.net.cn
		Accept: application/json
		Accept-Encoding: gzip,deflate,sdch
		Accept-Language: en-US,en;q=0.8,fa;q=0.6,sv;q=0.4
		Cache-Control: no-cache
		Connection: keep-alive
		User-Agent: Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/64.0.3282.186 Safari/537.36
		Content-Length: 120
		Content-Type: application/json
		{
		  "request": "requestExample",
		  "profile": "profileExample",
		  "label": "labelExample",
		  "caname": "canameExample"
		}

	6、数据库使用说明
		使用mongodb://adminUser:adminPass@localhost:27017/admin 这个url进行连接后,可查看admin数据库中表t_enrollcert内容，其中使用caname-dn作为主键

=======================================================================================================================================
三、命令行工具使用说明（参见文档《BCIAJavaChain平台CA节点子系统-命令行用户操作手册.docx》）
	1、项目描述
		该项目的主要内容是为JavaChain平台提供CA服务命令行工具，主要功能包括：申请下载，重新申请并下载以及吊销SM2 国密算法签名的证书，管理用户信息和权限，管理CA信息。

		1.1	文档目标
			CA子系统命令行工具使用说明书。
		1.2	读者对象
			供BCIA javachain的平台开发者、应用开发者及应用最终用户参考使用。
	
	2、环境需求
		2.1	部署需求
			JAVA环境需要Oracle JDK 1.8以上。

		2.2	证书和密钥的相关要求
			2.2.1	签名算法
				必须要求是使用 SM2 国密算法签名算法来产生CSR。
			2.2.2	密钥存储
				按照pem格式存储密钥文件，放到指定目录（/工作目录/msp/keystore）下（详见 使用说明3.1节）。
			2.2.3	CA返回的身份证书
				是使用 SM2 国密算法签名的证书，存储于对应目录下（详见 使用说明3.1节），以pem文件格式存储证书。
			2.2.4	命令执行顺序
				除了getcainfo命令之外，其他命令运行之前都必须在enroll命令之后。其他命令都必须要使用身份证书来保证安全性。

	3	使用说明
		3.1	运行目录说明
		 
		config目录存放日志配置文件 logback.xml。
		TestData目录存放样例数据。
		tmp目录存放reenroll命令的临时私钥,在申请证书成功后会将该临时密钥移除,并覆盖写入对应的 keystore目录下的密钥文件中。

		3.2	工作目录说明
		用户先选定工作目录。命令行工具会在enroll命令执行后创建如下图3-1所示的目录结构：
		 
		图3 1
		enroll命令执行以后,默认的配置文件ca-client-config.yaml会产生在<user.dir>/ca-client/config 目录下，在ca-client目录下会建立msp目录（<user.dir>/ca-client/msp），该目录下会有以下三个目录：
		（1）	cacerts目录：用于存放ca的证书文件。
		（2）	keystore目录：用于存放用户签名csr所用的私钥文件（需要用户生成后放入该目录）。
		（3）	signcerts目录：用于存放用户enroll得到的身份证书文件（enroll命令后存入该目录）。
		注意:所有文件都以pem 格式编码.请把私钥文件也按找私钥的pem格式存储。
		等register成功后，会在msp目录下建立新用户的对应目录，如下图3-2所示：其中的test 目录等都是新用户目录，每个新用户目录下有自己对应的私钥和签名证书目录。
		 
		图3 2
		对应的用户注册信息数据文件(registers.dat)和证书与用户绑定关系的数据文件(enroll-id.dat)会在msp目录下。
		3.3	日志配置文件说明
		目前将日志配置文件置于config目录下，config/logback.xml，便于外部操作可配置。
		
	4、接口说明
		4.1	命令实现类
			Enroll命令	com.cfca.ra.command.internal.enroll.EnrollCommand
			Reenroll命令	com.cfca.ra.command.internal.reenroll. ReenrollCommand
			Register命令	com.cfca.ra.command.internal.register. RegisterCommand
			Revoke命令	com.cfca.ra.command.internal.revoke. RevokeCommand
			Getcainfo命令	com.cfca.ra.command.internal.getcainfo.GetCAInfoCommand
		

		4.2	接口概要
			用户通过命令行的方式访问ca服务器接口
			Ca client负责解析前端的cmd和参数，包装成http请求发送给CA，获取CA返回的reponse后，再返回给前端。
			注意，执行命令的用户名密码配置在配置文件的admin，adminpwd字段中，或者在命令行工具的json文件中指定，后者有更高的优先级。
			总共6个命令，对应6个http请求。
			如下表所示：其中jsonFile 是包含了Json请求的文件路径。
			获取CA信息	ca-client cainfo –h host –p port –a jsonFile
			签发证书	ca-client enroll –h host –p port –a jsonFile
			重新签发证书	ca-client reenroll –h host –p port –a jsonFile
			注册用户实体身份	ca-client register –h host –p port –a jsonFile
			吊销证书	ca-client revoke –h host –p port –a jsonFile
			批量查询证书	ca-client tcert –h host –p port –a jsonFile
			4.2.1	接口协议版本
				本接口协议版本号为：0.0.1。
		4.3	获取CA信息命令（cainfo）
			获取CA服务的基本信息命令，包括CA的签名根证书链P7B文件。
			注意这里获取的是没有经过Base64编码的二进制p7b文件
			4.3.1	使用步骤
				使用api来测试可以参照以下步骤：
				第一步先将配置文件ca-client-config.yaml放入ca-client/config目录下。
				第二步:将请求内容按照Json格式写入cainfo.json文件，通过-a参数指定 [cainfo.json 文件路径]。cainfo.json 文件内容如下节所述。具体路径名自行定义，然后文件名路径通过-a参数指定。
				第三步：在控制台执行命令。
				使用命令行控制台来测试基本与上述步骤一致。

			4.3.2	命令
				java -Djava.ext.dirs=./lib -Dlogback.configurationFile=./config/logback.xml -jar RA-Command-xxxx.jar cainfo -h localhost -p 8089 -a TestData/cainfo.json

				使用时注意指定依赖库(-Djava.ext.dirs=依赖库路径)，cainfo.json文件名以及路径可以自行指定，文件中就是json请求:
				{"caName":"CFCA"}
				下载下来的文件会放入ca-client/config/msp/cacerts目录下
				文件名以 主机名-端口号-CA名.pem命名:
				localhost-8089-CFCA.pem
			4.3.3	请求参数
				参数	参数名称	类型	参数说明	是否可空	样例
				业务参数
				canamne	ca名称	String	指定ca名称	不可空	CFCA

			4.3.4	返回参数
			如果遇到错误会报出CommandException
			参数	参数名称	类型	参数说明	是否可空	样例
			业务参数
			Result	处理结果	JsonObject	{“caname”:ca名称(string), “cachain”:ca证书链文件的B64编码字符串}	可空	

		4.4	签发身份证书命令（enroll）
			接收用户信息，进行用户登记，申请身份证书的命令。该命令会读取配置文件中admin和adminpwd两项来建立鉴权信息，表明申请证书的用户。
			服务器会校验该鉴权信息验证用户是否有申请权限。并且可以通过配置文件来定制化CSR部分，详细配置如下图所示。
			注意：每一个用户名为了表示其唯一性，也是为了防止第三方RA限制单用户申请的证书数量，在这用户第一次使用命令行工具或者api的时候，会由内部sdk产生一个随机的10位字符串拼接在用户名后,以”-”号连接。这才是服务器最终使用的申请证书的用户名，在带回来的证书dn中有所体现。

			4.4.1	使用步骤
				使用api来测试可以参照以下步骤：
				第一步:使用SM2国密算法生成密钥对，将生成的密钥存入<工作目录>/ca-client/config/msp/keystore 目录下,过程可以参考demo.EnrollCommandDemo中的代码(在调用enroll命令前,需要调用 final CsrResult result = CsrUtils.genCSR(algo, names);CsrUtils.storeMyPrivateKey(result, username); 存放生成好的密钥)。
				第二步：需要将上一步生成的CSR以及其他的请求内容按照Json格式写入enroll.json 文件，通过-a参数指定 [enroll.json 文件路径]。enroll.json 文件内容如下节所述。具体路径名自行定义，然后文件名路径通过-a参数指定。
				第三步：在控制台执行命令。
				第四步：在对应目录（ca-client/config/msp/[enroll用户目录]/signcerts）下获得身份证书。其中enroll用户目录 ,如果用户是admin则对应的就是msp目录。
				使用命令行控制台来测试基本与上述步骤一致。
			4.4.2	命令
				java -Djava.ext.dirs=./lib -Dlogback.configurationFile=./config/logback.xml -jar RA-Command-xxxx.jar enroll -h localhost -p 8089 -a enroll.json
				使用时注意指定依赖库(-Djava.ext.dirs=依赖库路径)，enroll.json是自行指定的文件名，文件中就是json请求:
				{"label":"","username":"admin","password":"1234","profile":"H09358028","csrConfig":{"cn":"C=CN,O=CFCA TEST OCA1,OU=Local RA,OU=Individual-1,CN=051@admin@ZH09358028@111","names":"CN=051@testName@Z1234567890@53,OU=Individual-3,OU=Local RA,O=CFCA TEST CA,C=CN","hosts":["<<<MYHOST>>>"],"key":{"algo":"SM2","size":256},"ca":{"pathlen":0,"pathlenzero":0,"expiry":-1}},"caName":"CFCA","request": CSR}
			4.4.3	请求参数
				参数	参数名称	类型	参数说明	是否可空	样例
				业务参数
				request	请求详细信息	String	构建的csr的b64字符串	不可空	
				profile	profile信息	String	签名模板,暂时不传	可空	
				label	label信息	String	HSM操作中使用的标签	不可空	
				caname	ca名称	String	请求指定的CA名称	不可空	

			4.4.4	返回参数
				如果遇到错误会报出CommandException
				参数	参数名称	类型	参数说明	是否可空	样例
				业务参数
				Result	处理结果	String	Base64编码格式的身份证书, Success为true时,会有值	可空	样例太长,自行生成
				Success	成功标识	boolean	布尔值，指示请求是否成功	不可空	true
				Errors	处理错误	array	每个元素是一个对象{ code:xxxxx,message:xxxx}
				Success为false时,会有值	可空	一组错误消息（即代码和字符串消息）
				Messages	处理信息	array	每个元素是一个对象{ code:xxxxx,message:xxxx}	可空	一系列信息性消息（即代码和字符串消息）
		4.5	重新签发证书接口（reenroll）
			当身份证书过期后，调用该命令重新进行用户登记，申请身份证书的命令。该命令会使用已有的身份证书进行签名，建立鉴权信息，服务器会校验该鉴权信息。
			4.5.1	使用步骤
				使用api来测试可以参照以下步骤：
				第一步:使用SM2国密算法生成密钥对，将生成的密钥存入<新密钥文件路径>, 注意新密钥不能覆盖enroll命令生成的老密钥，因为老密钥要用于生成签名。具体生产密钥对代码可以参考Demo。在命令结束后，命令行工具自身会将老密钥替换成新密钥。生成的新密钥路径通过-key 命令行参数指定新密钥路径。
				第二步：需要将上一步生成的CSR以及其他的请求内容按照Json格式写入reenroll.json 文件，通过-a参数指定 [reenroll.json 文件路径]。reenroll.json 文件内容跟上节一致。具体路径名自行定义，然后文件名路径通过-a参数指定。
				第三步：调用API执行命令。
				第四步：在对应目录（ca-client/config/msp/[reenroll用户目录]/signcerts）下获得身份证书。其中reenroll用户目录 ,如果用户是admin则对应的就是msp目录。
				使用命令行控制台来测试基本与上述步骤一致，注意一点,调用reenrol命令控制台时，所使用的身份证书的DN必须与ca-client/config/ ca-client-config.yaml配置文件中的字段Csr.cn一致。
			4.5.2	命令
				java -Djava.ext.dirs=./lib -Dlogback.configurationFile=./config/logback.xml -jar RA-Command-xxxx.jar reenroll -h localhost -p 8089 -a reenroll.json -key <newKeyFile>
				使用时注意指定依赖库(-Djava.ext.dirs=依赖库路径)，reenroll.json是指定的文件名，文件中就是json请求:
				{"label":"","username":"admin","password":"1234","profile":"H09358028","csrConfig":{"cn":"C=CN,O=CFCA TEST OCA1,OU=Local RA,OU=Individual-1,CN=051@admin@ZH09358028@111","names":"CN=051@testName@Z1234567890@53,OU=Individual-3,OU=Local RA,O=CFCA TEST CA,C=CN","hosts":["<<<MYHOST>>>"],"key":{"algo":"SM2","size":256},"ca":{"pathlen":0,"pathlenzero":0,"expiry":-1}},"caName":"CFCA","request":CSR}

			4.5.3	请求参数
				参数	参数名称	类型	参数说明	是否可空	样例
				业务参数
				request	请求详细信息	String	构建的csr的b64字符串	不可空	
				profile	profil信息	String	签名模板,暂时不可用	可空	
				label	label信息	String	HSM操作中使用的标签	不可空	
				caname	ca名称	String	请求指定的CA名称	不可空	

			4.5.4	返回参数
				如果遇到错误会报出CommandException。
				参数	参数名称	类型	参数说明	是否可空	样例
				业务参数
				Result	处理结果	JsonObject	{“cert”:Base64编码格式的身份证书}	可空	

			4.6	注册用户实体身份接口（register）
				接收用户信息，进行新用户注册命令。注册时要使用身份证书建立鉴权信息（token），注册成功后，会得到新注册用户的密码。
				注意：注册用户名长度限定在[3,64]字节，用户名包含特殊字符时注册失败。
				4.6.1	使用步骤
					使用api来测试可以参照以下步骤：
					第一步: 请求内容按照Json格式写入register.json 文件。具体路径名自行定义，然后文件名路径通过-a参数指定。
					第二步：在控制台执行命令。
					第三步：返回新用户密码。
					使用命令行控制台来测试基本与上述步骤一致。

				4.6.2	命令
					java -Djava.ext.dirs=./lib -Dlogback.configurationFile=./config/logback.xml -jar RA-Command-xxxx.jar register -h localhost -p 8089 -a register.json
					使用时注意指定依赖库(-Djava.ext.dirs=依赖库路径)，register.json是指定的文件名，文件中就是json请求:
					{"name":"zc10","type":"user","secret":"1234","maxEnrollments":2,"affiliation":"org.department.c","attributes":[{"name":"hf.Revoker","value":"true","eCert":false},{"name":"hf.Registrar.Roles","value":"client,user,peer,validator,auditor","eCert":false}],"caName":"CFCA"}
				4.6.3	请求参数
					参数	参数名称	类型	参数说明	是否可空	样例
					业务参数
					id	id	String	用户id	不可空	
					type	类型	String	类型	不可空	
					secret	密码	String	密码	不可空	
					max_enrollments	数量	Integer	最大证书数量	不可空	
					affiliation_path	路径	String	路径	不可空	
					attrs	注册属性	Json	"attrs": [
						{
						  "name": "string",
						  "value": "string"
						}
					  ],用数组传入多个属性值	不可空	
					caname	ca名称	String	ca名称	不可空	

				4.6.4	返回参数
					如果遇到错误会报出CommandException。
					参数	参数名称	类型	参数说明	是否可空	样例
					业务参数
					result	处理结果	Json	    {"credentials": "string"}	不可空	

		4.7	吊销证书接口（revoke）
			吊销证书命令。通过传入的证书序列号进行吊销。
			4.7.1	使用步骤
				使用api来测试可以参照以下步骤：
				第一步: 请求内容按照Json格式写入revoke.json 文件。具体路径名自行定义，然后文件名路径通过-a参数指定。
				第二步：在控制台执行命令。
				使用命令行控制台来测试基本与上述步骤一致。

			4.7.2	命令
				java -Dlogback.configurationFile=./config/logback.xml -Djava.ext.dirs=./lib -jar RA-Command-xxxx.jar revoke -h localhost -p 8089 -a revoke.json
				使用时注意指定依赖库(-Djava.ext.dirs=依赖库路径)，revoke.json是指定的文件名，文件中就是json请求:
				{"id":"admin","aki":"ssss","serial":"1032940241","reason":"expire","caname":"CFCA"}
			4.7.3	请求参数
				参数	参数名称	类型	参数说明	是否可空	样例
				业务参数
				id	Id	String	用户名	不可空	test
				aki	Aki	String	要撤销的证书的授权密钥标识符（Authority Key Identifier）	不可空	“”
				serial	Serial	String	证书序列号	不可空	1032137068
				reason	Reason	String	吊销原因	不可空	expire
				caname	Caname	String	Ca名称	不可空	CFCA

			4.7.4	返回参数
				如果遇到错误会报出CommandException。
				参数	参数名称	类型	参数说明	是否可空	样例
				业务参数
				result	处理结果	Json	{"result": "string"}	不可空	

		4.8	批量查询证书（tcert）
			批量查询证书命令。该版本暂不支持。

	5	配置文件

	  
	6	使用Jar进行调用的代码示例
		6.1	获取CA信息命令（cainfo）
			public static void main(String[] args) throws Exception {
			final GetCAInfoRequest caInfoRequest = new GetCAInfoRequest("CFCA");
			final Gson gson = new GsonBuilder().disableHtmlEscaping().create();
			final String jsonFile = "TestData/cainfo.json";
			final String request = gson.toJson(caInfoRequest);
			System.out.println("request=" + request);
			FileUtils.writeStringToFile(new File(jsonFile), request);

			final GetCAInfoCommand getCAInfoCommand = new GetCAInfoCommand();
			getCAInfoCommand.prepare(new String[] { "cainfo", "-h", "localhost", "-p", "8089", "-a", jsonFile });
			final JsonObject result = getCAInfoCommand.execute();
			System.out.println(result);
			}

		6.2	签发身份证书命令（enroll）
			private static ConfigBean loadConfigFile() throws CommandException {
				try {
					return ConfigUtils.load("ca-client/config/ca-client-config.yaml");
				} catch (Exception e) {
					throw new CommandException(CommandException.REASON_CODE_ENROLL_COMMAND_LOAD_CONFIG_FAILED, e);
				}
			}

			public static void main(String[] args) throws Exception {
				final ConfigBean configBean = loadConfigFile();
				String profile = configBean.getEnrollment().getProfile();
				CsrConfig csrConfig = configBean.getCsr();
				String caName = configBean.getCaname();
				final String username = "admin";
				final String password = "1234";

				final String algo = csrConfig.getKey().getAlgo();
				final String names = csrConfig.getNames();
				final CsrResult result = CsrUtils.genCSR(algo, names);
				final String csr = result.getCsr();
				System.out.println("Csr=" + csr);
				CsrUtils.storeMyPrivateKey(result, username);

				final EnrollmentRequest.Builder builder = new EnrollmentRequest.Builder(csr, username, password, profile, csrConfig, caName);
				final EnrollmentRequest enrollmentRequest = builder.build();
				final Gson gson = new GsonBuilder().disableHtmlEscaping().create();
				final String jsonFile = "TestData/enroll.json";
				FileUtils.writeStringToFile(new File(jsonFile), gson.toJson(enrollmentRequest));

				final EnrollCommand enrollCommand = new EnrollCommand();
				String[] args1 = new String[]{"enroll", "-h", "localhost", "-p", "8089", "-a", jsonFile};
				enrollCommand.prepare(args1);
				final JsonObject response = enrollCommand.execute();
				System.out.println(response);
			}

		6.3	重新签发身份证书命令（reenroll）
			private void testReenroll() throws Exception {
				final ConfigBean configBean = loadConfigFile();
				String profile = configBean.getEnrollment().getProfile();
				CsrConfig csrConfig = configBean.getCsr();
				String caName = configBean.getCaname();
				final String algo = csrConfig.getKey().getAlgo();
				final String names = csrConfig.getNames();
				final CsrResult result = CsrUtils.genCSR(algo, names);
				final String keyDir = "D:\\R15\\P1552\\dev\\blockchain\\command\\ca-client\\config\\msp\\tmp\\keystore";
				final String keyFile = "key.pem";

				CsrUtils.storePrivateKey(result, keyDir, keyFile);
				final String csr = result.getCsr();
				System.out.println("CSR=" + csr);
				final String username = "admin";
				final String password = "1234";
				final ReenrollmentRequest.Builder builder = new ReenrollmentRequest.Builder(csr, username, password, profile, csrConfig, caName);
				final ReenrollmentRequest reenrollmentRequest = builder.build();
				final Gson gson = new GsonBuilder().disableHtmlEscaping().create();
				final String jsonFile = "TestData/reenroll.json";
				final String request = gson.toJson(reenrollmentRequest);
				System.out.println("request=" + request);
				FileUtils.writeStringToFile(new File(jsonFile), request);

				final String keyFilePath = String.join(File.separator, keyDir, keyFile);
				String[] args = new String[]{"reenroll", "-h", "localhost", "-p", "8089", "-a", jsonFile, "-key", keyFilePath};
				ReenrollCommand reenrollCommand = new ReenrollCommand();
				reenrollCommand.prepare(args);
				reenrollCommand.execute();
			}
		6.4	注册用户实体身份命令（register）
			private void testRegister() throws Exception {
				final RegistrationRequest.Builder builder = new RegistrationRequest.Builder();
				final ArrayList<UserAttrs> v = new ArrayList<>();
				v.add(new UserAttrs("hf.Revoker", "true"));
				v.add(new UserAttrs("hf.Registrar.Roles", "client,user,peer,validator,auditor"));
				builder.name("zc10").caName("CFCA").affiliation("org.department.c").attributes(v).maxEnrollments(2).secret("1234").type("user");
				final RegistrationRequest registrationRequest = builder.build();


				final String jsonFile = "TestData/register.json";
				final Gson gson = new GsonBuilder().disableHtmlEscaping().create();
				final String request = gson.toJson(registrationRequest);
				System.out.println("request=" + request);
				FileUtils.writeStringToFile(new File(jsonFile), request);

				final RegisterCommand registerCommand = new RegisterCommand();
				String[] args = new String[]{"register", "-h", "localhost", "-p", "8089", "-a", jsonFile};
				registerCommand.prepare(args);
				registerCommand.execute();
			}
		6.5	吊销证书命令（revoke）
			private void testRevoke() throws Exception{
				String id = "admin";
				String aki = "ssss";
				String serial = "1032940241";
				String reason = "expire";
				String caname = "CFCA";

				final RevokeRequest revokeRequest = new RevokeRequest(id, aki, serial, reason, caname);

				final String jsonFile = "TestData/revoke.json";
				final Gson gson = new GsonBuilder().disableHtmlEscaping().create();
				final String request = gson.toJson(revokeRequest);
				System.out.println("request=" + request);
				FileUtils.writeStringToFile(new File(jsonFile), request);

				String[] args = new String[]{"revoke", "-h", "localhost", "-p", "8089", "-a", jsonFile};
				RevokeCommand revokeCommand = new RevokeCommand();
				revokeCommand.prepare(args);
				final JsonObject result = revokeCommand.execute();
				System.out.println(result);
			}
