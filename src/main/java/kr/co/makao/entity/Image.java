package kr.co.makao.entity;

import jakarta.persistence.*;

@Entity
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(nullable = false)
    private String url;

    @ManyToOne(optional = false)
    private Product product;
}
