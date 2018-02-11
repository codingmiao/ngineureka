# linux下的配置和使用

## 1、修改config.properties配置信息
一个配置的例子如下：
eurekaUrl为您的eureka注册中心地址
confPath指定一个文件夹，用于存放生成的nginx配置信息
heartbeatCycle指定查询注册中心的周期(秒)
```
eurekaUrl=http://192.168.1.1:10000/eureka
confPath=/usr/local/nginx/conf/apps
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

## 3、修改reload.sh文件
reload.sh文件如下:
```
cd /usr/local/nginx/sbin
./nginx -s reload
```
修改第一行，使得sh命令能够切换到您的nginx根目录
通过chmod命令，赋予reload.sh文件执行权限

## 4、启动
./startup.sh启动服务
您可以通过查看{confPath}/ngineureka_upstream.conf文件，检查配置信息是否正确地添加到nginx

然后，就能通过nginx，访问到您的rest服务了~