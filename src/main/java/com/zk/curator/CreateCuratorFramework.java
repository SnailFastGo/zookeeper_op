package com.zk.curator;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

import com.zk.utils.StaticVar;

/**使用Curator创建zookeeper客户端，需要注意Curator和zookeeper的版本兼容问题
 * Curator 2.x.x - compatible with both ZooKeeper 3.4.x and ZooKeeper 3.5.x
*  Curator 3.x.x - compatible only with ZooKeeper 3.5.x and includes support for new features such as dynamic reconfiguration, etc.
 * @author snailfast
 *
 */
public class CreateCuratorFramework {
	public static void main(String[] args) {
		//重试策略
		RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
		
		//利用工厂方法创建curator client
		CuratorFramework client = CuratorFrameworkFactory.newClient(StaticVar.connectString, 
				StaticVar.sessionTimeout, StaticVar.connectionTimeout, retryPolicy);
		
		//启动client
		client.start();
		System.out.println("Zookeeper client has established");
		
		//关闭client
		client.close();
	}
}
