# windows下的配置和使用
## 0、配置注册中心
用解压缩工具打开ngineureka.jar\BOOT-INF\classes\application.yml文件，这是一个标注的spring cloud配置
```
eureka:
  client:
    serviceUrl:
      defaultZone: http://10.111.58.121:10000/eureka/
server:
  tomcat:
    uri-encoding: UTF-8
  port: 10001
  context-path: /ngineureka
spring:
  application:
    name: ngineureka
```
请将defaultZone改为注册中心地址

## 1、修改config.properties配置信息
一个配置的例子如下：
confPath指定一个文件夹，用于存放生成的nginx配置信息；

heartbeatCycle指定查询注册中心的周期(秒)；

recordCacheSize是可选的，记录最近操作的次数；
```
confPath=D:/nginx-1.8.1/conf/apps
heartbeatCycle=300
```

## 2、修改nginx.conf
在相应的位置添加 “include {confPath}/ngineureka_upstream.conf;” “include {confPath}/ngineureka_upstream.conf;”两行，引入ngineureka启动后生成的配置

一个配置的例子如下(仅演示http块):

```
http {
    include       mime.types;
    default_type  application/octet-stream;
    sendfile        on;
    keepalive_timeout  65;
    #gzip  on;
    
    include apps/ngineureka_upstream.conf;
    
    server {
        listen       7082;
        server_name  localhost;
		
	    proxy_buffer_size   128k;
	    proxy_buffers   4 256k;
	    proxy_busy_buffers_size   256k;
	    default_type 'text/html';
	    charset utf-8;
	    include apps/ngineureka_location.conf;
    }
}
```

## 3、修改reload.bat文件
reload.bat文件如下:
```
D:
cd D:/nginx-1.8.1/
nginx -s reload
```
修改前两行，使得cmd命令能够切换到您的nginx根目录

## 可选操作1、为location块添加参数(自版本1.1.1)
有时候，我们需要对location块添加一下参数，以满足性能，安全性等需求，比如:
```
location ^~ /xx/ {
	proxy_pass http://upstream_xx;
	deny 192.168.1.2;
	deny 192.168.1.3;
}
```
此时，我们可以配置根目录下的文件locationParam.txt(没有则新建一个)
```
>xx
deny 192.168.1.2;
deny 192.168.1.3;

>xx1
proxy_set_header  X-Real-IP  $remote_addr;
```
">"开头的行表示应用名称，根据前面的约定，会被转为全小写
下面跟着的行就是该应用对应的location块下要加的参数了

## 4、启动
双击startup.bat启动服务
您可以通过查看{confPath}/ngineureka_upstream.conf文件，检查配置信息是否正确地添加到nginx

然后，就能通过nginx，访问到您的rest服务了~