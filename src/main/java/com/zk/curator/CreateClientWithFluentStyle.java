package com.zk.curator;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

import com.zk.utils.StaticVar;

/**使用Fluent风格创建zookeeper客户端，需要注意Curator和zookeeper的版本兼容问题
 * Curator 2.x.x - compatible with both ZooKeeper 3.4.x and ZooKeeper 3.5.x
*  Curator 3.x.x - compatible only with ZooKeeper 3.5.x and includes support for new features such as dynamic reconfiguration, etc.
 * @author snailfast
 *
 */
public class CreateClientWithFluentStyle {
	public static void main(String[] args) {
		//重试策略
		RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
		
		//利用工厂方法创建curator client
		CuratorFramework client = CuratorFrameworkFactory.builder().connectString(StaticVar.connectString)
				.sessionTimeoutMs(StaticVar.sessionTimeout)
				.connectionTimeoutMs(StaticVar.connectionTimeout)
				.retryPolicy(retryPolicy)
				//可以利用命名空间来隔离不同的业务，所有操作都基于该命名空间
//				.namespace("base")
				.build();
		
		//启动client
		client.start();
		System.out.println("Zookeeper client has established");
		
		//关闭client
		client.close();
	}
}
