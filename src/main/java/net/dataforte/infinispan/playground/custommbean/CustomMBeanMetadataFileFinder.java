package net.dataforte.infinispan.playground.custommbean;

import org.infinispan.factories.components.ModuleMetadataFileFinder;
import org.kohsuke.MetaInfServices;

@MetaInfServices
public class CustomMBeanMetadataFileFinder implements ModuleMetadataFileFinder {
   @Override
   public String getMetadataFilename() {
      // This is the artifact name prepended to "-component-metadata.dat"
      return "infinispan-playground-custom-mbean-component-metadata.dat";
   }
}
