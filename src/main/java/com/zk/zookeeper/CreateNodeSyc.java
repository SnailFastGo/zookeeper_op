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

import com.zk.utils.StaticVar;

/**使用同步的方式创建ZooKeeper节点
 * @author snailfast
 *
 */
public class CreateNodeSyc implements Watcher{
	private static CountDownLatch connectedSemaphore = new CountDownLatch(1);
	public static void main(String[] args) throws IOException, InterruptedException, KeeperException {
		ZooKeeper zookeeper = new ZooKeeper(StaticVar.connectString, StaticVar.sessionTimeout, new CreateNodeSyc());
		connectedSemaphore.await();
		String data = "hello";
		String path = "/zk-test-persistent";
		String path1 = zookeeper.create(path, data.getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		String path2 = zookeeper.create(path, data.getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT_SEQUENTIAL);
		System.out.println("path1: " + path1);
		System.out.println("path2: " + path2);
		zookeeper.close();
	}
	
	@Override
	public void process(WatchedEvent event) {
		System.out.println("receive watcher event: " + event);
		if(KeeperState.SyncConnected == event.getState()){
			if(EventType.None == event.getType() && null == event.getPath()){
				connectedSemaphore.countDown();
			}
		}
	}
}
