package kr.co.makao.entity;

import jakarta.persistence.*;
import kr.co.makao.entity.base.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@SuperBuilder
@Entity
public class Product extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(length = 40, nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    private int price;

    private int count;

    private boolean isActivated;

    @ManyToOne(optional = false)
    private Category category;

    @ManyToOne(optional = false)
    private Store store;
}
