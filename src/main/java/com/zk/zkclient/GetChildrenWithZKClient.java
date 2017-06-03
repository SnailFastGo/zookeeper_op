package com.zk.zkclient;

import java.util.List;

import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.ZkClient;

import com.zk.utils.StaticVar;

/**获取子节点并注册监听器，监听子节点的新增、减少以及本节点的删除（监听器不是一次性的）
 * @author snailfast
 *
 */
public class GetChildrenWithZKClient {
	public static void main(String[] args) throws InterruptedException {
		ZkClient  zkClient = new ZkClient(StaticVar.connectString, StaticVar.sessionTimeout);
		System.out.println("Zookeeper session has established");
		String path = "/";
		List<String> children = zkClient.subscribeChildChanges(path, new IZkChildListener() {
			
			@Override
			public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception {
				System.out.println("Children changed: parentPath -> " + parentPath + ",  currentChilds -> " + currentChilds);
			}
		});
		
		System.out.println("children : " + children);
		
		zkClient.createEphemeral("/test");
		
		Thread.sleep(5000);
		zkClient.close();
	}
}
