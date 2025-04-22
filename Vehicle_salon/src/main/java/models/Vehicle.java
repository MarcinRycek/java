package models;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import java.util.Map;
import jakarta.persistence.*;
import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import org.hibernate.annotations.Type;
import java.util.HashMap;


@Entity
@Table(name = "vehicle")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vehicle {
    @Id
    @Column(nullable = false, unique = true)
    private String id;
    @Column(columnDefinition = "NUMERIC")
    private double price;
    private String category;
    private String brand;
    private String model;
    @Column(nullable = true)
    private int year;
    private String plate;

    @Type(JsonBinaryType.class)
    @Column(columnDefinition = "jsonb")
    @Builder.Default
    private Map<String, Object> attributes = new HashMap<>();
    public Object getAttribute(String key) {
        return attributes.get(key);
    }
    public void addAttribute(String key, Object value) {
        attributes.put(key, value);
    }
    public void removeAttribute(String key) {
        attributes.remove(key);
    }
}
