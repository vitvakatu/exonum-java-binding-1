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

package com.exonum.binding.common.serialization.json;

import static com.google.common.base.Preconditions.checkNotNull;

import com.exonum.binding.common.crypto.PublicKey;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;

/**
 * {@link PublicKey} string serializer. Used to serialize/deserialize PublicKey values from/to hex
 * strings.
 *
 * <p>All method arguments are non-null by default.
 */
public final class PublicKeyJsonSerializer
    implements JsonSerializer<PublicKey>, JsonDeserializer<PublicKey> {

  /**
   * Serialize PublicKey to JsonElement.
   *
   * @throws NullPointerException in case of src is null
   */
  @Override
  public JsonElement serialize(PublicKey src, Type typeOfSrc, JsonSerializationContext context) {
    checkNotNull(src, "PublicKey value is null");

    return new JsonPrimitive(src.toString());
  }

  /**
   * Deserialize PublicKey from JsonElement.
   *
   * @throws NullPointerException in case of src is null
   */
  @Override
  public PublicKey deserialize(JsonElement json, Type typeOfT,
      JsonDeserializationContext context) throws JsonParseException {
    checkNotNull(json, "PublicKey json input is null");

    return PublicKey.fromHexString(json.getAsString());
  }
}
