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

import static com.exonum.binding.common.hash.Hashing.DEFAULT_HASH_SIZE_BITS;
import static com.exonum.binding.common.hash.Hashing.DEFAULT_HASH_SIZE_BYTES;
import static com.exonum.binding.core.storage.indices.ProofListContainsMatcher.provesThatContains;
import static com.exonum.binding.core.storage.indices.TestStorageItems.V1;
import static java.util.Collections.singletonList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNot.not;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.exonum.binding.common.hash.HashCode;
import com.exonum.binding.common.serialization.StandardSerializers;
import com.exonum.binding.core.proxy.Cleaner;
import com.exonum.binding.core.storage.database.View;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import org.junit.jupiter.api.Test;

/**
 * Contains tests of ProofListIndexProxy methods
 * that are not present in {@link ListIndex} interface.
 */
class ProofListIndexProxyIntegrationTest extends BaseListIndexIntegrationTestable {

  /**
   * An empty list root hash: an all-zero hash code.
   */
  private static final HashCode EMPTY_LIST_ROOT_HASH =
      HashCode.fromBytes(new byte[DEFAULT_HASH_SIZE_BYTES]);

  private static final String LIST_NAME = "test_proof_list";

  @Override
  ProofListIndexProxy<String> create(String name, View view) {
    return ProofListIndexProxy.newInstance(name, view, StandardSerializers.string());
  }

  @Override
  Object getAnyElement(AbstractListIndexProxy<String> index) {
    return index.get(0L);
  }

  @Test
  void getRootHashEmptyList() {
    runTestWithView(database::createSnapshot, (list) -> {
      assertThat(list.getRootHash(), equalTo(EMPTY_LIST_ROOT_HASH));
    });
  }

  @Test
  void getRootHashSingletonList() {
    runTestWithView(database::createFork, (list) -> {
      list.add(V1);

      HashCode rootHash = list.getRootHash();
      assertThat(rootHash.bits(), equalTo(DEFAULT_HASH_SIZE_BITS));
      assertThat(rootHash, not(equalTo(EMPTY_LIST_ROOT_HASH)));
    });
  }

  @Test
  void getProofFailsIfEmptyList() {
    runTestWithView(database::createSnapshot,
        (list) -> assertThrows(IndexOutOfBoundsException.class, () -> list.getProof(0)));
  }

  @Test
  void getProofSingletonList() {
    runTestWithView(database::createFork, (list) -> {
      list.add(V1);

      assertThat(list, provesThatContains(0, V1));
    });
  }

  @Test
  void getRangeProofSingletonList() {
    runTestWithView(database::createFork, (list) -> {
      list.add(V1);

      assertThat(list, provesThatContains(0, singletonList(V1)));
    });
  }

  @Test
  void getProofMultipleItemList() {
    runTestWithView(database::createFork, (list) -> {
      List<String> values = TestStorageItems.values;

      list.addAll(values);

      for (int i = 0; i < values.size(); i++) {
        assertThat(list, provesThatContains(i, values.get(i)));
      }
    });
  }

  @Test
  void getRangeProofMultipleItemList_FullRange() {
    runTestWithView(database::createFork, (list) -> {
      List<String> values = TestStorageItems.values;
      list.addAll(values);

      assertThat(list, provesThatContains(0, values));
    });
  }

  @Test
  void getRangeProofMultipleItemList_1stHalf() {
    runTestWithView(database::createFork, (list) -> {
      List<String> values = TestStorageItems.values;
      list.addAll(values);

      int from = 0;
      int to = values.size() / 2;
      assertThat(list, provesThatContains(from, values.subList(from, to)));
    });
  }

  @Test
  void getRangeProofMultipleItemList_2ndHalf() {
    runTestWithView(database::createFork, (list) -> {
      List<String> values = TestStorageItems.values;
      list.addAll(values);

      int from = values.size() / 2;
      int to = values.size();
      assertThat(list, provesThatContains(from, values.subList(from, to)));
    });
  }

  private static void runTestWithView(Function<Cleaner, View> viewFactory,
      Consumer<ProofListIndexProxy<String>> listTest) {
    runTestWithView(viewFactory, (ignoredView, list) -> listTest.accept(list));
  }

  private static void runTestWithView(Function<Cleaner, View> viewFactory,
      BiConsumer<View, ProofListIndexProxy<String>> listTest) {
    IndicesTests.runTestWithView(
        viewFactory,
        LIST_NAME,
        ProofListIndexProxy::newInstance,
        listTest
    );
  }
}
