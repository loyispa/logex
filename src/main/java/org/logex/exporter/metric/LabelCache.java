/*
 * Copyright 2025 loyispa
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
package org.logex.exporter.metric;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.RemovalCause;
import com.github.benmanes.caffeine.cache.RemovalListener;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class LabelCache<T> {
  private final Cache<List<String>, T> cache;
  private final Consumer<List<String>> removeCallback;

  public LabelCache(int maxSize, int expirationSeconds, Consumer<List<String>> removeCallback) {
    this.removeCallback = removeCallback;

    Caffeine<Object, Object> builder = Caffeine.newBuilder();

    if (maxSize > 0) {
      builder.maximumSize(maxSize);
    }

    if (expirationSeconds > 0) {
      builder.expireAfterAccess(expirationSeconds, TimeUnit.SECONDS);
    }

    builder.removalListener(
        (RemovalListener<List<String>, T>)
            (key, value, cause) -> {
              if (cause == RemovalCause.EXPIRED || cause == RemovalCause.SIZE) {
                this.removeCallback.accept(key);
              }
            });

    this.cache = builder.build();
  }

  public synchronized T getOrCreate(List<String> labelValues, Supplier<T> creator) {
    return cache.get(labelValues, k -> creator.get());
  }
}
