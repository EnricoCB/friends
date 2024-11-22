package com.portfolio.friends.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Friendship {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "requester_id")
    private User requester;
    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "receiver_id")
    private User receiver;
    private boolean accepted;

    public Friendship(User requester, User receiver) {
        this.requester = requester;
        this.receiver = receiver;
    }
}
