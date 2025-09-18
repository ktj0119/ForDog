package com.example.forDog.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShelterDTO {

    private int no;

    private String region;

    private String shelterName;

    private String phone;

    private String address;

}
