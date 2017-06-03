package com.zk.zookeeper;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.AsyncCallback.DataCallback;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import com.zk.utils.StaticVar;

/** 以异步的方式读取zookeeper节点数据
 * @author snailfast
 *
 */
public class GetDataAsyc implements Watcher{
	
	private static CountDownLatch connectedSemaphore = new CountDownLatch(1);
	private static ZooKeeper zookeeper = null;
	private static Stat stat = new Stat();
	
	public static void main(String[] args) throws IOException, InterruptedException, KeeperException {
		zookeeper = new ZooKeeper(StaticVar.connectString, StaticVar.sessionTimeout, new GetDataAsyc());
		connectedSemaphore.await();
		String path = "/zk-test-persistent";
		zookeeper.getData(path, true, new IDataCallback(), "I get data");
		zookeeper.setData(path, "hello world".getBytes(), -1);
		zookeeper.getData(path, true, new IDataCallback(), "I get data");
		Thread.sleep(Integer.MAX_VALUE);
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
class IDataCallback implements DataCallback{

	@Override
	public void processResult(int rc, String path, Object ctx, byte[] data, Stat stat) {
		System.out.println("return code: " + rc + "  return path: " + path + " return ctx: " + ctx 
				+ " return data: " + new String(data) + "  stat: " + stat);
	}
	
}

