# ngineureka
一个监控spring cloud注册中心(eureka),并将服务映射到nginx负载均衡的工具

![][1]

当我们用spring cloud部署一套微服务集群后，想要把集群中的服务以rest api的形式给用户使用，显然，我们不可能把所有的application用到的ip和端口暴露给用户。这时候，我们就可以用ngineureka来帮助我们把服务将application服务映射给nginx，然后只需把nginx的端口暴露给用户即可。

ngineureka定期查询注册中心内可用的application，并将它们转换成nginx的配置并reload，这样，便可以通过nginx的反向代理，负载均衡地访问各服务了。

## 修改现有application的配置
使用ngineureka，需要对您application的配置做少量约定,例如:
```
eureka:
  client:
    serviceUrl:
      defaultZone: http://192.168.1.1:10000/eureka/
server:
  tomcat:
    uri-encoding: UTF-8
  port: 11000
  context-path: /esrielevation
spring:
  application:
    name: esrielevation
```
application name必须全小写，context-path需要与application name相同，这也意味着，您application中所有的Controller，都将被加上application name前缀


## 配置和启动ngineureka
下载ngineureka.zip或自行编译
1.1版本后，由于springcloud编译出的文件较大，已放到网盘：[https://pan.baidu.com/s/1RlKr9Z8f0Tm89uMlrRZwSg][2]

然后进行配置和启动:

[windows下配置和启动][3]

[linux下配置和启动][4]

## RESTful API

```
<ngineurekaURL>/ngineureka/cmd/reload
```
立即读取当前注册中心内的服务，并强制刷新nginx(由于eureka的延迟机制，新注册的服务可能不能立即读到，如果遇到这种情况，请过几秒钟再尝试)

```
<ngineurekaURL>/ngineureka/record/lately
```
查询最近几次执行情况，返回一个json，包含执行状态，注册过的服务等


  [1]: http://7xlvcv.com1.z0.glb.clouddn.com/01bbc543-059f-4f2a-8364-dd95a7505deb
  [2]: https://pan.baidu.com/s/1RlKr9Z8f0Tm89uMlrRZwSg
  [3]: https://github.com/codingmiao/ngineureka/blob/master/wincfg.md "windows下配置和启动"
  [4]: https://github.com/codingmiao/ngineureka/blob/master/linuxcfg.md "linux下配置和启动"