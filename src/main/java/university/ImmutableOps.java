package university;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.UnaryOperator;

final class ImmutableOps {
    private ImmutableOps() {}

    // Maps
    static <K, V> LinkedHashMap<K, V> mapPut(Map<K, V> src, K key, V value) {
        Objects.requireNonNull(src, "src");
        Objects.requireNonNull(key, "key");
        Objects.requireNonNull(value, "value");

        LinkedHashMap<K, V> copy = new LinkedHashMap<>(src);
        copy.put(key, value);
        return copy;
    }

    static <K, V> LinkedHashMap<K, V> mapRemove(Map<K, V> src, K key) {
        Objects.requireNonNull(src, "src");
        Objects.requireNonNull(key, "key");

        LinkedHashMap<K, V> copy = new LinkedHashMap<>(src);
        copy.remove(key);
        return copy;
    }

    // Sets
    static <T> LinkedHashSet<T> setAdd(Set<T> src, T value) {
        Objects.requireNonNull(src, "src");
        Objects.requireNonNull(value, "value");

        LinkedHashSet<T> copy = new LinkedHashSet<>(src);
        copy.add(value);
        return copy;
    }

    static <T> LinkedHashSet<T> setRemove(Set<T> src, T value) {
        Objects.requireNonNull(src, "src");
        Objects.requireNonNull(value, "value");

        LinkedHashSet<T> copy = new LinkedHashSet<>(src);
        copy.remove(value);
        return copy;
    }

    @SafeVarargs
    static <T> UnaryOperator<T> pipe(UnaryOperator<T>... steps) {
        Objects.requireNonNull(steps, "steps");
        return t -> {
            T cur = t;
            for (UnaryOperator<T> f : steps) {
                if (f == null) continue;
                cur = f.apply(cur);
            }
            return cur;
        };
    }
}
