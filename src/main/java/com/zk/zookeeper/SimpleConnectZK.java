package com.zk.zookeeper;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.Watcher.Event.KeeperState;

import com.zk.utils.StaticVar;

import org.apache.zookeeper.ZooKeeper;

/**使用ZooKeeper建立连接
 * @author snailfast
 *
 */
public class SimpleConnectZK implements Watcher{
	private static CountDownLatch connectedSemaphore = new CountDownLatch(1);
	public static void main(String[] args) throws IOException, InterruptedException {
		ZooKeeper zookeeper = new ZooKeeper(StaticVar.connectString, StaticVar.sessionTimeout, new SimpleConnectZK());
		System.out.println(zookeeper.getState());
		connectedSemaphore.await();
		System.out.println(zookeeper.getState());
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
