package com.zk.zkclient;

import org.I0Itec.zkclient.ZkClient;
import org.apache.zookeeper.ZooDefs.Ids;

import com.zk.utils.StaticVar;

/**利用ZkClient递归创建、删除节点以及写入和读取节点数据
 * @author snailfast
 *
 */
public class CRUDWithZkClient {
	public static void main(String[] args) {
		ZkClient  zkClient = new ZkClient(StaticVar.connectString, StaticVar.sessionTimeout);
		System.out.println("Zookeeper session has established");
		
		//定义节点路径
		String path = "/lock";
		
		//定义节点数据
		String data = "1";
		
		//递归创建节点
		zkClient.createPersistent(path, true, Ids.OPEN_ACL_UNSAFE);
		
		//以字符串的形式写入节点数据
		zkClient.writeData(path, data);
		String dataRead = zkClient.readData(path);
		System.out.println("dataRead : " + dataRead);
		
		//递归删除节点
		zkClient.deleteRecursive(path);
		boolean exists = zkClient.exists(path);
		System.out.println("exists : " + exists);
		zkClient.close();
	}
}
