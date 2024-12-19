package kr.co.makao.entity;

import jakarta.persistence.*;
import lombok.Getter;


@Getter
@Entity
public class Product extends TimeStampEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(length = 40, nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    private int price;

    private int stock;

    private boolean isActivated;

    @OneToOne
    private Category category;

    @ManyToOne(optional = false)
    private Store store;
}
