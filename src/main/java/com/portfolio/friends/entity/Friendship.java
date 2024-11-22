package com.portfolio.friends.entity;

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
    Long id;
    @ManyToOne
    @JoinColumn(name = "requester_id")
    User requester;
    @ManyToOne
    @JoinColumn(name = "receiver_id")
    User receiver;
    boolean accepted;

    public Friendship(User requester, User receiver) {
        this.requester = requester;
        this.receiver = receiver;
    }
}
