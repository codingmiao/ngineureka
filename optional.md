# 可选的配置

ngineureka还提供了一些可选的配置，方便您针对某个应用进行特殊配置。

这些配置都是初始化时加载到内存中的，所以如果您修改了这些文件，需要重启ngineureka方可生效

## 1、为location块添加参数(自版本1.1.1)

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

## 2、为某个应用指定负载均衡策略(自版本1.2.1)

ngineureka默认采用最小连接数策略(least_conn)进行负载均衡

有时候，我们需要指定某个应用的负载均衡策略，例如应用myApp有一个服务地址
```
/myApp/getUser/<UserId>
```
并且在应用内部，我们将查询结果以UserId作为key进行缓存，为了提高缓存命中率，我们此时可选择url_hash策略，将相同的id转发到相同的节点上：
配置根目录下的文件loadBalancingStrategy.properties(没有则新建一个)
```
myapp=url_hash
```

支持的负载均衡策略如下：

- least_conn 默认，最小连接数
- ip_hash 通过客户端请求ip进行hash，再通过hash值选择后端server
- polling 轮询
- url_hash 通过请求url进行hash，再通过hash值选择后端server（部分版本的nginx需安装对应模块）
- fair 按后端服务器的响应时间来分配请求，响应时间短的优先分配（部分版本的nginx需安装对应模块）
