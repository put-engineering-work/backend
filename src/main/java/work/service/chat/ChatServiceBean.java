package work.service.chat;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import work.dto.chat.MessageDTO;
import work.dto.chat.MessageGetDTO;
import work.repository.MemberRepository;
import work.repository.MessageRepository;
import work.repository.UserDetailsRepository;
import work.repository.UserRepository;
import work.service.authentication.AuthenticationService;
import work.util.exception.AuthenticationException;
import work.util.exception.CustomException;
import work.util.exception.UserNotFoundException;
import work.util.mapstruct.MessageMapper;

import java.security.Principal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ChatServiceBean implements ChatService {
    private final MessageRepository messageRepository;
    private final UserDetailsRepository userDetailsRepository;
    private final MemberRepository memberRepository;
    private final MessageMapper messageMapper;
    private final AuthenticationService authenticationService;

    private final UserRepository userRepository;

    public ChatServiceBean(MessageRepository messageRepository, UserDetailsRepository userDetailsRepository, MemberRepository memberRepository, MessageMapper messageMapper, AuthenticationService authenticationService, UserRepository userRepository) {
        this.messageRepository = messageRepository;
        this.userDetailsRepository = userDetailsRepository;
        this.memberRepository = memberRepository;
        this.messageMapper = messageMapper;
        this.authenticationService = authenticationService;
        this.userRepository = userRepository;
    }


    @Override
    @Transactional
    public MessageDTO sendMessage(Principal principal, UUID eventId, MessageGetDTO messageDTO) {
        String userEmail = principal.getName();
        var user = userRepository.findByEmail(userEmail).orElseThrow(()-> new UserNotFoundException("USER_NOT_FOUND"));
        var message = messageMapper.fromMessageGetDTO(messageDTO);
        var member = memberRepository.findMemberByUserIdAndEventId(user.getId(), eventId).orElseThrow(AuthenticationException::new);
        var event = member.getEvent();
        message.setUserId(user.getId());
        message.setEvent(event);
        message.setMember(member);
        message.setCreatedDate(ZonedDateTime.now());
        message = messageRepository.saveAndFlush(message);
        var response = messageMapper.toMessageDTO(message);
        var userDetails = userDetailsRepository.findDetailsByUserId(user.getId()).orElseThrow(AuthenticationException::new);
        response.getSender().setName(userDetails.getName());
        response.getSender().setLastname(userDetails.getLastName());
        if(response.getSender().getSenderId().equals(user.getId())){
            response.setIsOwner(Boolean.TRUE);
        }
        else
            response.setIsOwner(Boolean.FALSE);
        return response;
    }

    @Override
    @Transactional
    public List<MessageDTO> getHistory(Principal principal, UUID eventId) {
        var messages = messageRepository.findByEventId(eventId);
        var response = messageMapper.toListMessageDTO(messages);
        response = response.stream().peek(r -> {
            var userDetails = userDetailsRepository.findDetailsByUserId(r.getSender().getSenderId()).orElseThrow(() ->
                    new CustomException("USER_NOT_FOUND", HttpStatus.FORBIDDEN));
            r.getSender().setName(userDetails.getName());
            r.getSender().setLastname(userDetails.getLastName());
            if (r.getSender().getSenderId().equals(userDetails.getUser().getId())) {
                r.setIsOwner(Boolean.TRUE);
            } else {
                r.setIsOwner(Boolean.FALSE);
            }
        }).toList();
        return response;
    }
}
