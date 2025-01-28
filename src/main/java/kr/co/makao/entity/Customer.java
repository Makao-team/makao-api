package kr.co.makao.entity;

import jakarta.persistence.*;
import kr.co.makao.entity.base.BaseEntity;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@SuperBuilder
@Entity
@Table(indexes = @Index(columnList = "code"))
public class Customer extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(unique = true, length = 40)
    private String name;
}
