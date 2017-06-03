package com.zk.zookeeper;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooKeeper;

import com.zk.utils.StaticVar;

/** 以同步的方式删除节点
 * @author snailfast
 *
 */
public class DeleteNodeSyc implements Watcher{
	private static CountDownLatch connectedSemaphore = new CountDownLatch(1);
	
	public static void main(String[] args) throws IOException, InterruptedException, KeeperException {
		ZooKeeper zookeeper = new ZooKeeper(StaticVar.connectString, StaticVar.sessionTimeout, new DeleteNodeSyc());
		connectedSemaphore.await();
		String path = "/zk-asyc-persistent0000000009";
		int version = -1;
		zookeeper.delete(path, version);
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
