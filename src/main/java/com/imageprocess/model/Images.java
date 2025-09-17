package com.imageprocess.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Images {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String imageName;
    private String imageOriName;

    @ManyToOne
    @JoinColumn(name="user_id", referencedColumnName = "id",nullable = false)
    private User user;

}
