package com.trouni.tro_uni.service;

import com.trouni.tro_uni.dto.response.BookmarkResponse;
import com.trouni.tro_uni.entity.Bookmark;
import com.trouni.tro_uni.entity.Room;
import com.trouni.tro_uni.entity.User;
import com.trouni.tro_uni.exception.AppException;
import com.trouni.tro_uni.exception.errorcode.GeneralErrorCode;
import com.trouni.tro_uni.repository.BookmarkRepository;
import com.trouni.tro_uni.repository.RoomRepository;
import com.trouni.tro_uni.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * BookmarkService - Service xử lý các thao tác bookmark
 * 
 * Chức năng chính:
 * - Bookmark/unbookmark phòng
 * - Lấy danh sách phòng đã bookmark
 * - Kiểm tra trạng thái bookmark
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BookmarkService {
    
    private final BookmarkRepository bookmarkRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    
    /**
     * Bookmark một phòng
     */
    @Transactional
    public BookmarkResponse bookmarkRoom(UUID roomId) {
        User currentUser = getCurrentUser();
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new AppException(GeneralErrorCode.RESOURCE_NOT_FOUND));
        
        // Kiểm tra xem đã bookmark chưa
        if (bookmarkRepository.existsByUserAndRoom(currentUser, room)) {
            throw new AppException(GeneralErrorCode.RESOURCE_ALREADY_EXISTS, "Room already bookmarked");
        }
        
        Bookmark bookmark = new Bookmark();
        bookmark.setUser(currentUser);
        bookmark.setRoom(room);
        
        bookmark = bookmarkRepository.save(bookmark);
        
        log.info("User {} bookmarked room {}", currentUser.getUsername(), roomId);
        return BookmarkResponse.fromBookmark(bookmark);
    }
    
    /**
     * Unbookmark một phòng
     */
    @Transactional
    public void unbookmarkRoom(UUID roomId) {
        User currentUser = getCurrentUser();
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new AppException(GeneralErrorCode.RESOURCE_NOT_FOUND));
        
        // Kiểm tra xem có bookmark không
        if (!bookmarkRepository.existsByUserAndRoom(currentUser, room)) {
            throw new AppException(GeneralErrorCode.RESOURCE_NOT_FOUND, "Room not bookmarked");
        }
        
        bookmarkRepository.deleteByUserAndRoom(currentUser, room);
        
        log.info("User {} unbookmarked room {}", currentUser.getUsername(), roomId);
    }
    
    /**
     * Lấy danh sách phòng đã bookmark của user hiện tại
     */
    public Page<BookmarkResponse> getUserBookmarks(Pageable pageable) {
        User currentUser = getCurrentUser();
        Page<Bookmark> bookmarks = bookmarkRepository.findByUserOrderByCreatedAtDesc(currentUser, pageable);
        
        return bookmarks.map(BookmarkResponse::fromBookmark);
    }
    
    /**
     * Lấy danh sách tất cả bookmark của user (không phân trang)
     */
    public List<BookmarkResponse> getAllUserBookmarks() {
        User currentUser = getCurrentUser();
        List<Bookmark> bookmarks = bookmarkRepository.findByUser(currentUser);
        
        return bookmarks != null ? bookmarks.stream()
                .filter(Objects::nonNull)
                .map(BookmarkResponse::fromBookmark)
                .collect(Collectors.toList()) : new ArrayList<>();
    }
    
    /**
     * Kiểm tra một phòng có được bookmark bởi user hiện tại không
     */
    public boolean isRoomBookmarked(UUID roomId) {
        User currentUser = getCurrentUser();
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new AppException(GeneralErrorCode.RESOURCE_NOT_FOUND));
        
        return bookmarkRepository.existsByUserAndRoom(currentUser, room);
    }
    
    /**
     * Toggle bookmark status của một phòng
     */
    @Transactional
    public BookmarkResponse toggleBookmark(UUID roomId) {
        if (isRoomBookmarked(roomId)) {
            unbookmarkRoom(roomId);
            return null; // Indicate unbookmarked
        } else {
            return bookmarkRoom(roomId);
        }
    }
    
    /**
     * Lấy thông tin user hiện tại từ SecurityContext
     */
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(GeneralErrorCode.RESOURCE_NOT_FOUND, "User not found"));
    }
}
