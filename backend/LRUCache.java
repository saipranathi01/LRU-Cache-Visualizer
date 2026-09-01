import java.util.HashMap;
import java.util.Map;

public class LRUCache {

    private final CDLL list;
    private final Map<Integer, Node> map;
    private final int capacity;

    public LRUCache(int capacity) {

        if (capacity <= 0) {
            throw new IllegalArgumentException(
                "Capacity must be greater than 0"
            );
        }

        this.capacity = capacity;
        this.list = new CDLL();
        this.map = new HashMap<>();
    }


    // Get value for a key.
    // Accessed node becomes MRU.
    public int get(int key) {

        if (!map.containsKey(key)) {
            return -1;
        }

        Node node = map.get(key);

        list.moveToFront(node);

        return node.value;
    }


    // Insert or update a key-value pair.
    public void put(int key, int value) {

        // Key already exists
        if (map.containsKey(key)) {

            Node node = map.get(key);

            node.value = value;

            // Updated node becomes MRU
            list.moveToFront(node);

            return;
        }


        // Cache is full
        if (map.size() >= capacity) {

            int deletedKey = list.deleteAtEnd();

            map.remove(deletedKey);
        }


        // Insert new node as MRU
        Node newNode =
            list.insertBegin(key, value);

        map.put(key, newNode);
    }


    // Returns cache in MRU -> LRU order.
    public String getCacheAsJson() {

        return list.toJson();
    }


    // Returns current cache size.
    public int getSize() {

        return map.size();
    }


    // Returns cache capacity.
    public int getCapacity() {

        return capacity;
    }


    // Clear the cache.
    public void clear() {

        map.clear();

        list.head = null;
    }
}