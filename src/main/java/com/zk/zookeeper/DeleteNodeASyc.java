package com.zk.zookeeper;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.AsyncCallback.VoidCallback;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooKeeper;

import com.zk.utils.StaticVar;

/**以异步的方式删除节点
 * @author snailfast
 *
 */
public class DeleteNodeASyc implements Watcher{
	private static CountDownLatch connectedSemaphore = new CountDownLatch(1);
	
	public static void main(String[] args) throws IOException, InterruptedException {
		ZooKeeper zookeeper = new ZooKeeper(StaticVar.connectString, StaticVar.sessionTimeout, new DeleteNodeASyc());
		connectedSemaphore.await();
		String path = "/zk-asyc-persistent0000000008";
		int version = -1;
		zookeeper.delete(path, version, new IVoidCallback(), "aysc delete node");
		Thread.sleep(Integer.MAX_VALUE);
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
class IVoidCallback implements VoidCallback{
	@Override
	public void processResult(int rc, String path, Object ctx) {
		System.out.println("return code: " + rc + "  return path: " + path + " return ctx: " + ctx);
	}
}

