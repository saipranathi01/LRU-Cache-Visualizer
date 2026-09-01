public class CDLL {

    Node head = null;


    // Insert a new node at the beginning.
    // Beginning represents MRU.
    public Node insertBegin(int key, int value) {

        Node newNode = new Node(key, value);

        // Empty list
        if (head == null) {

            head = newNode;

            head.next = head;
            head.prev = head;
        }

        // Non-empty list
        else {

            newNode.prev = head.prev;
            newNode.next = head;

            head.prev.next = newNode;
            head.prev = newNode;

            head = newNode;
        }

        return newNode;
    }


    // Delete the last node.
    // Last node represents LRU.
    public int deleteAtEnd() {

        if (head == null) {
            return -1;
        }

        Node lru = head.prev;

        // Only one node
        if (head.next == head) {

            head = null;

            return lru.key;
        }

        Node previous = lru.prev;

        previous.next = head;
        head.prev = previous;

        return lru.key;
    }


    // Move an existing node to the front.
    // Front represents MRU.
    public void moveToFront(Node node) {

        if (head == null || head == node) {
            return;
        }

        // Remove node from current position
        node.prev.next = node.next;
        node.next.prev = node.prev;

        // Insert node at beginning
        node.prev = head.prev;
        node.next = head;

        head.prev.next = node;
        head.prev = node;

        head = node;
    }


    // Convert linked list into JSON.
    // Order: MRU -> LRU
    public String toJson() {

        if (head == null) {
            return "[]";
        }

        StringBuilder result = new StringBuilder();

        result.append("[");

        Node current = head;

        do {

            result.append("{");

            result.append("\"key\":");
            result.append(current.key);

            result.append(",");

            result.append("\"value\":");
            result.append(current.value);

            result.append("}");

            current = current.next;

            if (current != head) {
                result.append(",");
            }

        } while (current != head);

        result.append("]");

        return result.toString();
    }
}