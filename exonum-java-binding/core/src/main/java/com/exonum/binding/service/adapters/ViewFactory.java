/*
 * Copyright 2018 The Exonum Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.exonum.binding.service.adapters;

import com.exonum.binding.proxy.Cleaner;
import com.exonum.binding.storage.database.Fork;
import com.exonum.binding.storage.database.Snapshot;

/**
 * A factory of views.
 *
 * <p>Enables easier testing of service and transaction adapters: {@link UserServiceAdapter}
 * and {@link UserTransactionAdapter}.
 */
public interface ViewFactory {

  /**
   * Creates a new owning snapshot.
   *
   * @param nativeHandle a handle to the native snapshot object
   * @param cleaner a cleaner to register the destructor
   * @return a new owning snapshot proxy
   */
  Snapshot createSnapshot(long nativeHandle, Cleaner cleaner);

  /**
   * Creates a new owning fork.
   *
   * @param nativeHandle a handle to the native fork object
   * @param cleaner a cleaner to register the destructor
   * @return a new owning fork proxy
   */
  Fork createFork(long nativeHandle, Cleaner cleaner);
}