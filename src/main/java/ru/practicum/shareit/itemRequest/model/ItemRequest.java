package ru.practicum.shareit.itemRequest.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import org.hibernate.mapping.ToOne;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Entity
@Table(name = "item_requests")
public class ItemRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @NonNull
    private String description;
    @NonNull
    @JoinColumn(name = "requestor_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private User requestor;
    @NonNull
    private LocalDateTime created;
}
