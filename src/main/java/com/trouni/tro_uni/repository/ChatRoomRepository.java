package com.trouni.tro_uni.repository;

import com.trouni.tro_uni.entity.ChatRoom;

import com.trouni.tro_uni.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;

import org.springframework.data.repository.query.Param;

import org.springframework.stereotype.Repository;



import java.util.List;

import java.util.Optional;

import java.util.UUID;



@Repository

public interface ChatRoomRepository extends JpaRepository<ChatRoom, UUID> {



    /**

     * Finds a chat room that contains exactly the two specified users as participants.

     *

     * @param user1 The first user.

     * @param user2 The second user.

     * @return An Optional containing the ChatRoom if found, otherwise empty.

     */

    @Query("SELECT cr FROM ChatRoom cr WHERE :user1 MEMBER OF cr.participants AND :user2 MEMBER OF cr.participants AND SIZE(cr.participants) = 2")

    Optional<ChatRoom> findChatRoomByParticipants(@Param("user1") User user1, @Param("user2") User user2);



    /**

     * Finds all chat rooms that a specific user is a participant of.

     *

     * @param user The user to search for.

     * @return A list of ChatRooms the user is a participant of.

     */

    List<ChatRoom> findByParticipantsContains(User user);

}
