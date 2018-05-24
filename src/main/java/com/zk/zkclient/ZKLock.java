package com.zk.zkclient;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.ZkClient;
import org.apache.zookeeper.data.Stat;

public class ZKLock {
  //提前创建好锁对象的结点"/lock" CreateMode.PERSISTENT  
    public static final String LOCK_ZNODE = "/lock";  
    //分布式锁实现分布式seq生成  
    public static class Task2 implements Runnable, IZkChildListener {  
       
        private final String taskName;  
       
        private final ZkClient zkClient;  
       
        private final String lockPrefix = "/loc";  
       
        private final String selfZnode;  
       
        public Task2(String taskName) {  
            this.taskName = taskName;  
            zkClient = new ZkClient("172.24.8.31", 30000, 50000);  
            selfZnode = zkClient.createEphemeralSequential(LOCK_ZNODE + lockPrefix, new byte[0]);  
        }  
       
        @Override  
        public void run() {  
  
              createSeq();  
        }      
       
        private void createSeq() {  
            Stat stat = new Stat();  
            String oldData = zkClient.readData(LOCK_ZNODE, stat);  
            String newData = update(oldData);
            zkClient.writeData(LOCK_ZNODE, newData);  
            System.out.println(taskName + selfZnode + " obtain seq=" + newData);  
        }  
       
        private String update(String currentData) {  
            int d = Integer.parseInt(currentData);  
            d = d + 1;  
            currentData = String.valueOf(d);  
            return currentData;  
        }  
  
        @Override  
        public void handleChildChange(String parentPath,  
                List<String> currentChildren) throws Exception {  
            // TODO Auto-generated method stub  
              
        }      
         
    }  
  
    public static void main(String[] args) {  
        final ExecutorService service = Executors.newFixedThreadPool(20);  
        for (int i = 0; i < 100; i++) {  
            service.execute(new Task2("[Concurrent-" + i + "]"));  
        }  
        service.shutdown();  
    }  
}
