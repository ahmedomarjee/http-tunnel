package es.malvarez.http_tunnel.util;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Case insensitive map
 */
public class CaseInsensitiveMap<V> implements Map<String, V> {

    private final Map<String, V> delegated;

    public CaseInsensitiveMap(Map<String, V> delegated) {
        this.delegated = delegated;
    }

    @Override
    public boolean containsKey(Object key) {
        return delegated.containsKey(toLowerCase(key));
    }

    @Override
    public V get(Object key) {
        return delegated.get(toLowerCase(key));
    }

    public V put(String key, V value) {
        return delegated.put(toLowerCase(key), value);
    }

    public void putAll(Map<? extends String, ? extends V> m) {
        for (Map.Entry<? extends String, ? extends V> e : m.entrySet()) {
            put(e.getKey(), e.getValue());
        }
    }

    @Override
    public V remove(Object key) {
        return delegated.remove(key);
    }

    @Override
    public void clear() {
        delegated.clear();
    }

    @Override
    public boolean containsValue(Object value) {
        return delegated.containsValue(value);
    }

    @Override
    public Set<Entry<String, V>> entrySet() {
        return delegated.entrySet();
    }

    @Override
    public boolean equals(Object o) {
        return delegated.equals(o);
    }

    @Override
    public int hashCode() {
        return delegated.hashCode();
    }

    @Override
    public boolean isEmpty() {
        return delegated.isEmpty();
    }

    @Override
    public Set<String> keySet() {
        return delegated.keySet();
    }

    @Override
    public int size() {
        return delegated.size();
    }

    @Override
    public Collection<V> values() {
        return delegated.values();
    }

    protected String toLowerCase(Object key) {
        return key == null ? null : key.toString().toLowerCase();
    }
}
