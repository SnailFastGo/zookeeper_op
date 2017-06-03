package com.zk.zookeeper;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.AsyncCallback.StatCallback;
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

/** 以异步的方式检查节点是否存在
 * @author snailfast
 *
 */
public class ExistsAsyc implements Watcher{
	private static CountDownLatch connectedSemaphore = new CountDownLatch(1);
	private static ZooKeeper zookeeper = null;
	private static Stat stat = new Stat();
	
	public static void main(String[] args) throws IOException, InterruptedException, KeeperException {
		zookeeper = new ZooKeeper(StaticVar.connectString, StaticVar.sessionTimeout, new ExistsAsyc());
		connectedSemaphore.await();
		String path = "/zk-asyc-exists";
		int version = -1;
		zookeeper.create(path, "hello".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
		zookeeper.exists(path, true, new IStatCallback(), "I check exists");
		zookeeper.delete(path, version);
		Thread.sleep(Integer.MAX_VALUE);
		zookeeper.close();
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

class IStatCallback implements StatCallback{

	@Override
	public void processResult(int rc, String path, Object ctx, Stat stat) {
		// 节点存在时，返回rc为0，节点不存在时，返回rc为 -110，个人感觉有点问题，待后续验证
		System.out.println("return code: " + rc + "  return path: " + path + " return ctx: " + ctx + "  stat: " + stat);
	}
	
}
