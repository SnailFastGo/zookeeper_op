package com.zk.zookeeper;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import com.zk.utils.StaticVar;

/**Zooeeper的权限控制，Zookeeper支持的权限scheme： world、auth、digest、ip、super
 * 删除节点比较例外，删除节点不需要权限
 * @author snailfast
 *
 */
public class AccessControl implements Watcher{
	private static CountDownLatch connectedSemaphore = new CountDownLatch(1);
	private static ZooKeeper zookeeper = null;
	private static Stat stat = new Stat();
	private static final String scheme = "digest";
	private static final String username = "zk";
	private static final String password = "123456";
	
	public static void main(String[] args) throws IOException, InterruptedException, KeeperException {
		zookeeper = new ZooKeeper(StaticVar.connectString, StaticVar.sessionTimeout, new AccessControl());
		connectedSemaphore.await();
		zookeeper.addAuthInfo(scheme, (username + ":" + password).getBytes());
		String path = "/auth-test";
		zookeeper.create(path, "hello".getBytes(), Ids.CREATOR_ALL_ACL, CreateMode.EPHEMERAL);
		
		//正确权限
		ZooKeeper zookeeper2 = new ZooKeeper(StaticVar.connectString, StaticVar.sessionTimeout, null); 
		zookeeper2.addAuthInfo(scheme, (username + ":" + password).getBytes());
		byte[] data = zookeeper2.getData(path, false, stat);
		System.out.println(new String(data));
		
		//错误权限
		ZooKeeper zookeeper3 = new ZooKeeper(StaticVar.connectString, StaticVar.sessionTimeout, null); 
		data = zookeeper3.getData(path, false, stat);
		System.out.println(new String(data));
		
		zookeeper.close();
		zookeeper2.close();
		zookeeper3.close();
		
	}
	
	@Override
	public void process(WatchedEvent event) {
		try{
			System.out.println("receive watcher event: " + event);
			if(KeeperState.SyncConnected == event.getState()){
				if(EventType.None == event.getType() && null == event.getPath()){
					connectedSemaphore.countDown();
					
				}else if(EventType.NodeDataChanged == event.getType()){
					byte[] data = zookeeper.getData(event.getPath(), true, stat);
					System.out.println(new String(data));
					
				}else if(EventType.NodeCreated == event.getType()){
					System.out.println("Node " + event.getPath() + " Created");
					zookeeper.exists(event.getPath(), true);
					
				}else if(EventType.NodeDeleted == event.getType()){
					System.out.println("Node " + event.getPath() + " Delete");
					zookeeper.exists(event.getPath(), true);
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
