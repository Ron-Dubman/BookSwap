package com.BookSwap.demo.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User {

   public User(String username2, String hashedPassword, String email, Role roles) {
        //TODO Auto-generated constructor stub
        this.username = username2;
        this.password = hashedPassword;
        this.email = email;
        this.role = roles;
    }

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;

   @Column(nullable = false, unique = true)
   private String username;

   @Column(nullable = false, unique = true)
   private String email;

   @Column(length = 255,nullable = false)
   private String password;

   @Enumerated(EnumType.STRING)
   @Column(nullable = false)
   private Role role;

   private String location;

   @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
   private List<Book> books;
    
}
