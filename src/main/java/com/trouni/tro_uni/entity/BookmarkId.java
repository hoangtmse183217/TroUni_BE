package com.trouni.tro_uni.entity;

import lombok.*;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookmarkId implements Serializable {
    
    private UUID user;
    private UUID room;
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookmarkId that = (BookmarkId) o;
        return Objects.equals(user, that.user) && Objects.equals(room, that.room);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(user, room);
    }
}