package kr.co.makao.entity;

import jakarta.persistence.*;
import kr.co.makao.entity.base.BaseEntity;

@Table(indexes = @Index(columnList = "code"))
@Entity
public class Review extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private String id;

    private int score;

    @Column(nullable = false)
    private String contents;

    @ManyToOne(optional = false)
    private Product product;
}
