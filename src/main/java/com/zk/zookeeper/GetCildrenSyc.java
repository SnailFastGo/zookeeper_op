package com.zk.zookeeper;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooKeeper;

import com.zk.utils.StaticVar;

/**以同步的方式获取子节点路径
 * @author snailfast
 *
 */
public class GetCildrenSyc implements Watcher{
	private static CountDownLatch connectedSemaphore = new CountDownLatch(1);
	private static ZooKeeper zookeeper = null;
	
	public static void main(String[] args) throws IOException, InterruptedException, KeeperException {
		zookeeper = new ZooKeeper(StaticVar.connectString, StaticVar.sessionTimeout, new GetCildrenSyc());
		connectedSemaphore.await();
		String path = "/";
		List<String> children = zookeeper.getChildren(path, true);
		if(null != children && children.size() > 0){
			System.out.println(children);
		}else{
			System.out.println("路径" + path + " 下面不存在子节点");
		}
		zookeeper.close();
	}
	
	@Override
	public void process(WatchedEvent event) {
		System.out.println("receive watcher event: " + event);
		if(KeeperState.SyncConnected == event.getState()){
			if(EventType.None == event.getType() && null == event.getPath()){
				connectedSemaphore.countDown();
			}
		}else if(EventType.NodeChildrenChanged == event.getType()){
			try {
				List<String> children = zookeeper.getChildren(event.getPath(), true);
				System.out.println(children);
			} catch (KeeperException | InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
