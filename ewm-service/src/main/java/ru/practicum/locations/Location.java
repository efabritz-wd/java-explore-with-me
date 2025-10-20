package ru.practicum.locations;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "locations")
@Builder(toBuilder = true)
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private Float lat;
    @Column(nullable = false)
    private Float lon;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ru.practicum.locations.Location)) return false;
        return id != null && id.equals(((ru.practicum.locations.Location) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
