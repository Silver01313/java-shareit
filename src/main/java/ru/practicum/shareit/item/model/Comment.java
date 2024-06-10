package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Entity
@Table(name = "comments")
public class Comment {

    @Id
    private long id;
    @NonNull
    private String text;
    @NonNull
    @Column(name = "item_id")
    private long itemId;
    @NonNull
    @Column(name = "author_id")
    private long authorId;

}
