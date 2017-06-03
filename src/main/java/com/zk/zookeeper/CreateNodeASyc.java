package com.zk.zookeeper;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.AsyncCallback.StringCallback;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;

import com.zk.utils.StaticVar;

/**使用异步的方式创建ZooKeeper节点
 * @author snailfast
 *
 */
public class CreateNodeASyc implements Watcher{
	private static CountDownLatch connectedSemaphore = new CountDownLatch(1);
	public static void main(String[] args) throws IOException, InterruptedException {
		ZooKeeper zookeeper = new ZooKeeper(StaticVar.connectString, StaticVar.sessionTimeout, new CreateNodeASyc());
		connectedSemaphore.await();
		String data = "hello";
		String path = "/zk-asyc-persistent";
		zookeeper.create(path, data.getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL, new IStringCallback(), "I am context");
		zookeeper.create(path, data.getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL, new IStringCallback(), "I am context");
		zookeeper.create(path, data.getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL, new IStringCallback(), "I am context");
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

class IStringCallback implements StringCallback{
	@Override
	public void processResult(int rc, String path, Object ctx, String name) {
		System.out.println("return code: " + rc + "  return path: " + path + " return ctx: " + ctx + " return name: " + name);
	}
	
}



