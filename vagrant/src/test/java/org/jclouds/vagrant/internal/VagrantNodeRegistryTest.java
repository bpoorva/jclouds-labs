/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.vagrant.internal;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

import java.io.File;

import org.easymock.EasyMock;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.HardwareBuilder;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.ImageBuilder;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.domain.Processor;
import org.jclouds.vagrant.domain.VagrantNode;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;

public class VagrantNodeRegistryTest {

   @Test
   public void testNodeRegistry() {
      VagrantExistingMachines loader = EasyMock.createMock(VagrantExistingMachines.class);
      EasyMock.expect(loader.get()).andReturn(ImmutableList.<VagrantNode>of());
      EasyMock.replay(loader);

      VagrantNodeRegistry registry = new VagrantNodeRegistry(loader);
      OperatingSystem os = new OperatingSystem(OsFamily.UNRECOGNIZED, "Jclouds OS", "10", "x64", "Jclouds Test Image", true);
      Image image = new ImageBuilder()
            .ids("jclouds/box")
            .operatingSystem(os)
            .status(Image.Status.AVAILABLE)
            .build();
      Hardware hardware = new HardwareBuilder().ids("mini").ram(100).processor(new Processor(1.0, 1)).build();

      ImmutableList<String> networks = ImmutableList.of("172.28.128.3");
      VagrantNode node = VagrantNode.builder()
            .setPath(new File("/path/to/machine"))
            .setId("vagrant/node")
            .setGroup("vagrant")
            .setName("node")
            .setImage(image)
            .setNetworks(networks)
            .setHardware(hardware)
            .setHostname("vagrant-node")
            .build();

      assertNull(registry.get(node.id()));
      registry.add(node);
      assertEquals(registry.get(node.id()), node);
      registry.onTerminated(node);
      assertNull(registry.get(node.id()));
   }
}
