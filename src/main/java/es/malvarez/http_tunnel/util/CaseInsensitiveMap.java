package es.malvarez.http_tunnel.util;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * http_tunnel
 *
 * @author malvarez
 */

public class CaseInsensitiveMap<V> implements Map<String, V> {

    private final Map<String, V> delegated;

    public CaseInsensitiveMap(Map<String, V> delegated) {
        this.delegated = delegated;
    }

    public boolean containsKey(Object key) {
        return delegated.containsKey(toLowerCase(key));
    }

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

    public V remove(Object key) {
        return delegated.remove(key);
    }

    public void clear() {
        delegated.clear();
    }

    public boolean containsValue(Object value) {
        return delegated.containsValue(value);
    }

    public Set<Entry<String, V>> entrySet() {
        return delegated.entrySet();
    }

    public boolean equals(Object o) {
        return delegated.equals(o);
    }

    public int hashCode() {
        return delegated.hashCode();
    }

    public boolean isEmpty() {
        return delegated.isEmpty();
    }

    public Set<String> keySet() {
        return delegated.keySet();
    }

    public int size() {
        return delegated.size();
    }

    public Collection<V> values() {
        return delegated.values();
    }

    protected String toLowerCase(Object key) {
        return key == null ? null : key.toString().toLowerCase();
    }
}
