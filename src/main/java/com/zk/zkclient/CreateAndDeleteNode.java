package com.zk.zkclient;

import org.I0Itec.zkclient.ZkClient;
import org.apache.zookeeper.ZooDefs.Ids;

import com.zk.utils.StaticVar;

public class CreateAndDeleteNode {
	public static void main(String[] args) {
		ZkClient  zkClient = new ZkClient(StaticVar.connectString, StaticVar.sessionTimeout);
		System.out.println("Zookeeper session has established");
		
		//定义节点路径
		String path = "/zkclient/test";
		
		//定义节点数据
		String data = "hello world";
		
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
