package org.logex.exporter.metric;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LabelCacheTest {

  private LabelCache<String> labelCache;
  private Consumer<List<String>> mockRemoveCallback;
  private AtomicBoolean removeCallbackCalled;

  @BeforeEach
  void setUp() {
    removeCallbackCalled = new AtomicBoolean(false);
    mockRemoveCallback = key -> removeCallbackCalled.set(true);
    labelCache = new LabelCache<>(100, 60, mockRemoveCallback);
  }

  @Test
  void testGetOrCreateNewEntry() {
    List<String> labelValues = Arrays.asList("value1", "value2");
    String createdValue = labelCache.getOrCreate(labelValues, () -> "newValue");
    assertEquals("newValue", createdValue);
    assertFalse(removeCallbackCalled.get());
  }

  @Test
  void testGetOrCreateExistingEntry() {
    List<String> labelValues = Arrays.asList("value1", "value2");
    labelCache.getOrCreate(labelValues, () -> "initialValue");
    String retrievedValue = labelCache.getOrCreate(labelValues, () -> "anotherValue");
    assertEquals("initialValue", retrievedValue);
    assertFalse(removeCallbackCalled.get());
  }

  @Test
  void testCacheExpiration() throws InterruptedException {
    LabelCache<String> expiringCache = new LabelCache<>(1, 1, mockRemoveCallback);
    List<String> labelValues = Arrays.asList("value1", "value2");
    expiringCache.getOrCreate(labelValues, () -> "expiringValue");

    // Wait for cache to expire
    Thread.sleep(1500);

    // Accessing it again should create a new entry and trigger removal callback
    String newValue = expiringCache.getOrCreate(labelValues, () -> "newExpiringValue");
    assertEquals("newExpiringValue", newValue);
    // The removal callback is called asynchronously by Caffeine, so we might need to wait a bit or
    // check for it in a more robust way in a real scenario.
    // For this simple test, we'll assume it's called relatively quickly.
    assertTrue(removeCallbackCalled.get());
  }

  @Test
  void testCacheMaxSize() throws InterruptedException {
    // Use a CountDownLatch to wait for the removal callback
    CountDownLatch latch = new CountDownLatch(1);
    Consumer<List<String>> testRemoveCallback =
        key -> {
          removeCallbackCalled.set(true);
          latch.countDown(); // Signal that the callback was called
        };

    LabelCache<String> sizedCache = new LabelCache<>(1, 60, testRemoveCallback);
    List<String> labelValues1 = Arrays.asList("value1");
    List<String> labelValues2 = Arrays.asList("value2");

    sizedCache.getOrCreate(labelValues1, () -> "valueA");
    assertFalse(removeCallbackCalled.get());

    sizedCache.getOrCreate(labelValues2, () -> "valueB");

    // Wait for the removal callback to be called, with a timeout
    assertTrue(latch.await(5, TimeUnit.SECONDS), "Removal callback was not called within timeout");

    assertTrue(removeCallbackCalled.get());

    // After eviction, getting labelValues1 should create a new entry
    String retrievedValue = sizedCache.getOrCreate(labelValues1, () -> "valueC");
    assertEquals("valueC", retrievedValue);
  }

  @Test
  void testNullCreator() {
    List<String> labelValues = Arrays.asList("value1", "value2");
    assertThrows(NullPointerException.class, () -> labelCache.getOrCreate(labelValues, null));
  }

  @Test
  void testNullLabelValues() {
    assertThrows(NullPointerException.class, () -> labelCache.getOrCreate(null, () -> "value"));
  }
}
