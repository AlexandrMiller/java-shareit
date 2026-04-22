package ru.practicum.shareit.item.jpaItem;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item,Long> {

    List<Item> getItemsByOwnerId(Long ownerId);

    @Query("SELECT i FROM Item i WHERE i.available = true AND " +
            "(LOWER(i.name) LIKE LOWER(CONCAT('%', :text, '%')) OR " +
            "LOWER(i.description) LIKE LOWER(CONCAT('%', :text, '%')))")
    List<Item> searchItems(@Param("text") String text);

    boolean existsItemsByOwnerId(Long userId);

    List<Item> findItemsByRequestId(Long requestId);


}
