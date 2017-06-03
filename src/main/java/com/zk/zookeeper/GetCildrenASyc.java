package com.zk.zookeeper;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.AsyncCallback.Children2Callback;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import com.zk.utils.StaticVar;

import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.Watcher.Event.KeeperState;

public class GetCildrenASyc implements Watcher{
	private static CountDownLatch connectedSemaphore = new CountDownLatch(1);
	private static ZooKeeper zookeeper = null;
	
	public static void main(String[] args) throws IOException, InterruptedException {
		zookeeper = new ZooKeeper(StaticVar.connectString, StaticVar.sessionTimeout, new GetCildrenASyc());
		connectedSemaphore.await();
		String path = "/";
		zookeeper.getChildren(path, true, new IChildren2Callback(), "I get children");
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

class IChildren2Callback implements Children2Callback{
	@Override
	public void processResult(int rc, String path, Object ctx, List<String> children, Stat stat) {
		System.out.println("return code: " + rc + "  return path: " + path + " return ctx: " + ctx + " return children: " + children
				 + "   stat: " + stat);
	}
	
}
