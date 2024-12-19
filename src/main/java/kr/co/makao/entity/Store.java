package kr.co.makao.entity;

import jakarta.persistence.*;

@Entity
public class Store extends TimeStampEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(length = 40, unique = true, nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String refundInfo;

    @Column(nullable = false)
    private String deliveryInfo;
}
