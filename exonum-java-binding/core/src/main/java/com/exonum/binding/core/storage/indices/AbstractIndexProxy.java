/*
 * Copyright 2019 The Exonum Team
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

package com.exonum.binding.core.storage.indices;

import static com.exonum.binding.core.storage.indices.StoragePreconditions.checkIndexName;
import static com.google.common.base.Preconditions.checkNotNull;

import com.exonum.binding.core.proxy.AbstractNativeProxy;
import com.exonum.binding.core.proxy.NativeHandle;
import com.exonum.binding.core.storage.database.Fork;
import com.exonum.binding.core.storage.database.ModificationCounter;
import com.exonum.binding.core.storage.database.View;

/**
 * An abstract super class for proxies of all indices.
 *
 * <p>Each index is created with a database view, either an immutable Snapshot or a read-write Fork.
 * An index has a modification counter to detect when it or the corresponding view is modified.
 */
abstract class AbstractIndexProxy extends AbstractNativeProxy implements StorageIndex {

  final View dbView;

  /**
   * Needed to detect modifications of this index during iteration over this (or other) indices.
   */
  final ModificationCounter modCounter;

  private final String name;

  /**
   * Creates a new index.
   *
   * <p>Subclasses shall create a native object and pass a native handle to this constructor.
   *
   * @param nativeHandle a native handle of the created index
   * @param name a name of this index
   * @param view a database view from which the index has been created
   * @throws NullPointerException if any parameter is null
   */
  AbstractIndexProxy(NativeHandle nativeHandle, String name, View view) {
    super(nativeHandle);
    this.name = checkIndexName(name);
    this.dbView = checkNotNull(view);
    this.modCounter = view.getModificationCounter();
  }

  /** Returns the name of this index. */
  @Override
  public final String getName() {
    return name;
  }

  /**
   * Checks that this index <em>can</em> be modified and changes the modification counter.
   *
   * @throws UnsupportedOperationException if the database view is read-only
   */
  void notifyModified() {
    checkCanModify();
    modCounter.notifyModified();
  }

  /**
   * Checks that a database view is an instance of {@link Fork} — a modifiable database view.
   *
   * @throws UnsupportedOperationException if view is read-only or null.
   */
  private void checkCanModify() {
    if (!(dbView.canModify())) {
      throw new UnsupportedOperationException("Cannot modify the view: " + dbView
          + "\nUse a Fork to modify any collection.");
    }
  }

  @Override
  public String toString() {
    // test_map: ProofMap
    return name + ": " + getClass().getName();
  }
}
