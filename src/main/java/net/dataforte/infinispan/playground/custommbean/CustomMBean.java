package net.dataforte.infinispan.playground.custommbean;

import org.infinispan.jmx.annotations.MBean;
import org.infinispan.jmx.annotations.ManagedOperation;

@MBean(objectName = "CustomMBean",
      description = "Custom MBean")
public interface CustomMBean {

   @ManagedOperation(description = "Evicts a key", displayName = "Evicts a key")
   void evictKey(String key);

}
