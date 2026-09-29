import java.util.List;

// Module 6: System Integration & OOP
public interface Manageable<T> {
    void add(T item) throws Exception;
    void update(T item) throws Exception;
    void delete(String id) throws Exception;
    T get(String id) throws Exception;
    List<T> getAll();
}
