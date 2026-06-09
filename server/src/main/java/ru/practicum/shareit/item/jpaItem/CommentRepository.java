package ru.practicum.shareit.item.jpaItem;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.model.Comment;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment,Long> {

    List<Comment> findByItemIdOrderByCreatedDesc(long id);


}
