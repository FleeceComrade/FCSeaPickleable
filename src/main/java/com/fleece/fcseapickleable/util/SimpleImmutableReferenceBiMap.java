package com.fleece.fcseapickleable.util;

import java.util.HashMap;
import java.util.Map;

public class SimpleImmutableReferenceBiMap<K, V> {
  private boolean initialized = false;
  private final HashMap<K, V> verse;
  private final HashMap<V, K> inverse;

  public SimpleImmutableReferenceBiMap() {
    verse = new HashMap<>();
    inverse = new HashMap<>();
  }

  public SimpleImmutableReferenceBiMap<K, V> init(Map<K, V> verse) {
    if (initialized) throw new UnsupportedOperationException("You may NOT initialize a SimpleImmutableReferenceBiMap twice!");
    this.verse.putAll(verse);
    verse.forEach((k, v) -> {
      if (inverse.containsKey(v)) throw new IllegalArgumentException("Input map must not have duplicate values for this type of BiMap");
      inverse.put(v, k);
    });
    initialized = true;
    return this;
  }

  public V getValue(K k) {
    return verse.get(k);
  }

  public K getKey(V v) {
    return inverse.get(v);
  }

  public V getValueOrDefault(K k, V d) {
    return verse.getOrDefault(k, d);
  }

  public K getKeyOrDefault(V v, K k) {
    return inverse.getOrDefault(v, k);
  }

  @Override
  public String toString() {
    return String.format("Verse: [%s]\nInverse: [%s]", verse, inverse);
  }
}