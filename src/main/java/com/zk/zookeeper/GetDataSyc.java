package com.zk.zookeeper;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import com.zk.utils.StaticVar;

/** 以同步的方式读取zookeeper节点数据
 * @author snailfast
 *
 */
public class GetDataSyc implements Watcher{
	private static CountDownLatch connectedSemaphore = new CountDownLatch(1);
	private static ZooKeeper zookeeper = null;
	private static Stat stat = new Stat();
	
	public static void main(String[] args) throws IOException, InterruptedException, KeeperException {
		zookeeper = new ZooKeeper(StaticVar.connectString, StaticVar.sessionTimeout, new GetDataSyc());
		connectedSemaphore.await();
		String path = "/zk-test-persistent";
		byte[] data = zookeeper.getData(path, true, stat);
		System.out.println(new String(data));
		zookeeper.setData(path, "hello world".getBytes(), -1);
		data = zookeeper.getData(path, true, stat);
		System.out.println(new String(data));
		zookeeper.close();
	}
	
	@Override
	public void process(WatchedEvent event) {
		System.out.println("receive watcher event: " + event);
		if(KeeperState.SyncConnected == event.getState()){
			if(EventType.None == event.getType() && null == event.getPath()){
				connectedSemaphore.countDown();
			}else if(EventType.NodeDataChanged == event.getType()){
				try {
					byte[] data = zookeeper.getData(event.getPath(), true, stat);
					System.out.println(new String(data));
				} catch (KeeperException | InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
