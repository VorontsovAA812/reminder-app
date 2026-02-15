package ru.reminder.app.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @Column(name = "username", unique = true, nullable = false)
    private String userName;

    @Column(nullable = false)
    private String password;

    @Column(name = "chat_id", unique = true)
    private Long chatId;

    public User(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }
    @Column(length = 30)
    private String email;
}