/*
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gradle.launcher.daemon.server.health.memory;

import static org.gradle.launcher.daemon.server.health.memory.MBeanAttributeProvider.getMbeanAttribute;

public class MBeanAvailableMemory implements AvailableMemory {
    @Override
    public long get() throws UnsupportedOperationException {
        // MBean value takes reclaimable memory into account on Windows and Solaris
        // See https://msdn.microsoft.com/en-us/library/windows/desktop/aa366770(v=vs.85).aspx
        // See https://github.com/dmlloyd/openjdk/blob/jdk8u/jdk8u/jdk/src/solaris/native/sun/management/OperatingSystemImpl.c#L341
        return getMbeanAttribute("java.lang:type=OperatingSystem", "FreePhysicalMemorySize", Long.class);
    }
}
