package com.portfolio.friends.controller;

import com.portfolio.friends.dto.UserDTO;
import com.portfolio.friends.entity.Friendship;
import com.portfolio.friends.entity.User;
import com.portfolio.friends.infra.exception.RestErrorMessage;
import com.portfolio.friends.infra.security.SecurityConfigurations;
import com.portfolio.friends.service.FriendshipService;
import com.portfolio.friends.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/friendship")
@Tag(name = "Friendship", description = "Possui todas os recursos disponiveis para amizades com outros usuários")
@SecurityRequirement(name = SecurityConfigurations.SECURITY)
public class FriendshipController {

    UserService userService;
    FriendshipService friendshipService;

    @Operation(summary = "Envia uma solicitação de amizade para um usuário",
            description = "Recursos para mandar uma solicitação de amizade para outros usuários.",
            security = @SecurityRequirement(name = "security"),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Recurso criado com sucesso"),
                    @ApiResponse(responseCode = "403", description = "Usuário possui perfil privado",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = RestErrorMessage.class))),
                    @ApiResponse(responseCode = "404", description = "Usuário não encontrado",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = RestErrorMessage.class))),
                    @ApiResponse(responseCode = "409", description = "Solicitação de amizade já existente",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = RestErrorMessage.class))),
                    @ApiResponse(responseCode = "409", description = "Amizade já existente",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = RestErrorMessage.class))),
            }
    )
    @PostMapping("/request")
    public ResponseEntity<Void> request(@RequestBody UserDTO dto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User reciever = userService.findByUsername(dto.username());
        User request = userService.findByUsername(authentication.getName());
        friendshipService.friendshipRequest(request, reciever);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "Aceita uma solicitação de amizade de outro usuário",
            description = "Recursos para aceitar uma solicitação de amizade de outro usuário",
            security = @SecurityRequirement(name = "security"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Amizade aceita"),
                    @ApiResponse(responseCode = "404", description = "Solicitação de amizade não encontrada",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = RestErrorMessage.class))),
                    @ApiResponse(responseCode = "404", description = "Usuário não encontrado",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = RestErrorMessage.class)))
            }
    )
    @PatchMapping("/accept")
    public ResponseEntity<Void> accept(@RequestBody UserDTO dto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User reciever = userService.findByUsername(authentication.getName());
        User request = userService.findByUsername(dto.username());
        friendshipService.friendshipAccept(request, reciever);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Exibe uma lista paginada das solicitações de amizades requisitadas que ainda não foram aceitas do usuário logado",
            description = "Recursos para exibir uma lista paginada das solicitações de amizades requisitadas que ainda não foram aceitas do usuário logado",
            security = @SecurityRequirement(name = "security"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Recurso recuperado com sucesso",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDTO.class))),
            }
    )
    @GetMapping("/receiver")
    public ResponseEntity<Page<UserDTO>> getReceivedRequests(@Parameter(hidden = true) @PageableDefault(size = 5) Pageable pageable) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User receiver = userService.findByUsername(authentication.getName());
        Page<Friendship> friendships = friendshipService.getReceivedRequests(receiver, pageable);
        Page<UserDTO> userDTOPage = friendships.map(friendship -> new UserDTO(friendship.getRequester().getUsername()));
        return ResponseEntity.ok(userDTOPage);
    }

    @Operation(summary = "Exibe uma lista paginada das solicitações de amizades recebidas que ainda não foram aceitas do usuario logado",
            description = "Recursos para exibir uma lista paginada das solicitações de amizades recebidas que ainda não foram aceitas do usuario logado",
            security = @SecurityRequirement(name = "security"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Recurso recuperado com sucesso",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDTO.class))),
            }
    )
    @GetMapping("/requester")
    public ResponseEntity<Page<UserDTO>> requester(@Parameter(hidden = true) @PageableDefault(size = 5) Pageable pageable) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findByUsername(authentication.getName());
        Page<Friendship> friendships = friendshipService.getSentRequests(user, pageable);
        Page<UserDTO> userDTOPage = friendships.map(friendship -> new UserDTO(friendship.getReceiver().getUsername()));
        return ResponseEntity.ok(userDTOPage);
    }

    @Operation(summary = "Exibe uma lista paginada das amizades do usuário logado",
            description = "Recursos para exibir uma lista paginada das amizades do usuário logado",
            security = @SecurityRequirement(name = "security"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Recurso recuperado com sucesso",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDTO.class))),
            }
    )
    @GetMapping("/list")
    public ResponseEntity<Page<UserDTO>> list(@PageableDefault(size = 5) Pageable pageable) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User receiver = userService.findByUsername(authentication.getName());
        Page<Friendship> friendships = friendshipService.getAcceptedFriendships(receiver, pageable);
        Page<UserDTO> friendDTOPage = friendships.map(friendship -> {
            User friend = friendship.getRequester().equals(receiver)
                    ? friendship.getReceiver()
                    : friendship.getRequester();
            return new UserDTO(friend.getUsername());
        });
        return ResponseEntity.ok(friendDTOPage);
    }

    @Operation(summary = "recusa uma solicitação de amizade de outro usuário", description = "Recursos para recusar uma solicitação de amizade de outro usuário",
            security = @SecurityRequirement(name = "security"),
            responses = {
                    @ApiResponse(responseCode = "204", description = "Amizade recusada"),
                    @ApiResponse(responseCode = "404", description = "Solicitação de amizade não encontrada",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = RestErrorMessage.class))),
                    @ApiResponse(responseCode = "404", description = "Usuário não encontrado",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = RestErrorMessage.class)))
            }
    )
    @DeleteMapping("/decline")
    public ResponseEntity<Void> decline(@RequestBody UserDTO dto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User receiver = userService.findByUsername(authentication.getName());
        User requester = userService.findByUsername(dto.username());
        friendshipService.declineFriendship(requester, receiver);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Cancela uma solicitação de amizade enviada pelo usuário logado",
            description = "Recursos para cancelar uma solicitação de amizade enviada pelo usuário logado",
            security = @SecurityRequirement(name = "security"),
            responses = {
                    @ApiResponse(responseCode = "204", description = "Solicitação de amizade cancelada"),
                    @ApiResponse(responseCode = "404", description = "Solicitação de amizade não encontrada",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = RestErrorMessage.class))),
                    @ApiResponse(responseCode = "404", description = "Usuário não encontrado",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = RestErrorMessage.class)))
            }
    )
    @DeleteMapping("/cancel")
    public ResponseEntity<Void> cancel(@RequestBody UserDTO dto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User receiver = userService.findByUsername(authentication.getName());
        User requester = userService.findByUsername(dto.username());
        friendshipService.cancelRequestFriendship(receiver, requester);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Desfaz uma amizade com outro usuário",
            description = "Recursos para desfazer uma amizade com outro usuário",
            security = @SecurityRequirement(name = "security"),
            responses = {
                    @ApiResponse(responseCode = "204", description = "Amizade desfeita"),
                    @ApiResponse(responseCode = "404", description = "Solicitação de amizade não encontrada",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = RestErrorMessage.class))),
                    @ApiResponse(responseCode = "404", description = "Usuário não encontrado",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = RestErrorMessage.class)))
            }
    )
    @DeleteMapping("/undo")
    public ResponseEntity<Void> undo(@RequestBody UserDTO dto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User receiver = userService.findByUsername(authentication.getName());
        User requester = userService.findByUsername(dto.username());
        friendshipService.undoFriendship(receiver, requester);
        return ResponseEntity.noContent().build();
    }

}
