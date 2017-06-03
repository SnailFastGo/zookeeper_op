package com.zk.zookeeper;

import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

/** 以异步的方式读取zookeeper节点数据
 * @author snailfast
 *
 */
public class GetDataAsyc implements Watcher{
	
	private static CountDownLatch connectedSemaphore = new CountDownLatch(1);
	private static ZooKeeper zookeeper = null;
	private static Stat stat = new Stat();
	
	@Override
	public void process(WatchedEvent event) {
		System.out.println("receive watcher event: " + event);
		if(KeeperState.SyncConnected == event.getState()){
			if(EventType.None == event.getType() && null == event.getPath()){
				connectedSemaphore.countDown();
			}
		}else if(EventType.NodeDataChanged == event.getType()){
			try {
				byte[] data = zookeeper.getData(event.getPath(), new GetDataAsyc(), stat);
				System.out.println(new String(data));
			} catch (KeeperException | InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
