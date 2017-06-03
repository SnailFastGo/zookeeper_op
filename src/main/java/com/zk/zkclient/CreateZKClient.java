package com.zk.zkclient;

import org.I0Itec.zkclient.ZkClient;

import com.zk.utils.StaticVar;

/**ZkClient的创建方式有很多，通过参数可以理解如何创建，和创建Zookeeper很相似
 * @author snailfast
 *
 */
public class CreateZKClient {
	public static void main(String[] args) {
		ZkClient  zkClient = new ZkClient(StaticVar.connectString, StaticVar.sessionTimeout);
		System.out.println("Zookeeper session has established");
		zkClient.close();
	}
}
