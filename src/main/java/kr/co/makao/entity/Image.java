package kr.co.makao.entity;

import jakarta.persistence.*;
import kr.co.makao.entity.base.BaseEntity;

@Table(indexes = @Index(columnList = "code"))
@Entity
public class Image extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(nullable = false, unique = true)
    private String key;

    @ManyToOne(optional = false)
    private Product product;
}
