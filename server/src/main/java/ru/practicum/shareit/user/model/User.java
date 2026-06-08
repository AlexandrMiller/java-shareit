package ru.practicum.shareit.user.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * TODO Sprint add-controllers.
 */

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotBlank(message = "Имя должно быть указано")
    @Column(nullable = false)
    private String name;

    @Email(message = "Некорректный email")
    @NotBlank(message = "email должен быть указан")
    @Column(unique = true,nullable = false)
    private String email;

    public User(String name,String email) {
        this.name = name;
        this.email = email;
    }

}
