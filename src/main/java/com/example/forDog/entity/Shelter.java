package com.example.forDog.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Shelter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int no;

    @Column(nullable = false)
    private String region;

    @Column(nullable = false)
    private String shelterName;

    @Column(nullable = false)
    private String phone;

    @Column(nullable = false)
    private String address;

}
