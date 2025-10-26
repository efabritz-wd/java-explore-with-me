package ru.practicum.categories;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;


@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "categories")
@Builder
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Size(min = 1, max = 50, message = "Name must be between 1 and 50 characters")
    private String name;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ru.practicum.categories.Category)) return false;
        return id != null && id.equals(((ru.practicum.categories.Category) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}

