package kr.co.makao.entity;

import jakarta.persistence.*;

@Entity
public class Review extends TimeStampEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(optional = false)
    private Product product;

    private int score;

    private String contents;
}
