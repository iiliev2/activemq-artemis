/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.activemq.artemis.core.server.balancing.policies;

import org.apache.activemq.artemis.core.server.balancing.targets.MockTarget;
import org.apache.activemq.artemis.core.server.balancing.targets.Target;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;

public abstract class PolicyTestBase {
   public static final int MULTIPLE_TARGETS = 10;

   protected abstract AbstractPolicy createPolicy();

   @Test
   public void testPolicyWithNoTarget() {
      AbstractPolicy policy = createPolicy();

      Target selectedTarget = policy.selectTarget(Collections.emptyList(), "test");

      Assert.assertNull(selectedTarget);
   }

   @Test
   public void testPolicyWithSingleTarget() {
      AbstractPolicy policy = createPolicy();

      ArrayList<Target> targets = new ArrayList<>();
      targets.add(new MockTarget());

      Target selectedTarget = policy.selectTarget(targets, "test");
      Assert.assertEquals(selectedTarget, targets.get(0));
   }
}