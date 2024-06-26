package carrot.candy.app.chat.service;

import static carrot.candy.app.chat.exception.ChatRoomErrorCode.MEMBER_NOT_IN_CHAR_ROOM;

import carrot.candy.app.auth.domain.AuthMember;
import carrot.candy.app.chat.domain.chatroom.ChatRoom;
import carrot.candy.app.chat.domain.chatroom.ChatRoomRepository;
import carrot.candy.app.chat.domain.message.MessageRepository;
import carrot.candy.app.chat.dto.response.MessageResponse;
import carrot.candy.app.common.dto.SliceResponse;
import carrot.candy.app.common.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessageQueryService {

    private final MessageRepository messageRepository;
    private final ChatRoomRepository chatRoomRepository;

    public SliceResponse<MessageResponse> findAllByChatRoomId(
            AuthMember authMember,
            Long id,
            int pageSize,
            String cursor
    ) {
        ChatRoom chatRoom = findChatRoom(id);
        validateMemberInChatRoom(authMember.getId(), id);
        return messageRepository.findAllByChatRoomOrderByIdDesc(pageSize, cursor, chatRoom);
    }

    private ChatRoom findChatRoom(Long id) {
        return chatRoomRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("chatRoom not found"));
    }

    private void validateMemberInChatRoom(Long id, Long chatRoomId) {
        if (!chatRoomRepository.existsByMemberIdAndChatRoomId(id, chatRoomId)) {
            throw new BusinessException(MEMBER_NOT_IN_CHAR_ROOM);
        }
    }
}
