package kr.co.makao.entity;

import jakarta.persistence.*;

@Entity
@Table(indexes = @Index(columnList = "name"))
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(unique = true)
    private String name;

    @ManyToOne
    private Category parent;
}
