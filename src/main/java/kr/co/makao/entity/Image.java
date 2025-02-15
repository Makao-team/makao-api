package kr.co.makao.entity;

import jakarta.persistence.*;
import kr.co.makao.annotation.EngDigitIdGenerator;
import kr.co.makao.entity.base.TimeStamp;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Table(indexes = @Index(columnList = "code"))
@Entity
public class Image extends TimeStamp {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @EngDigitIdGenerator(length = 8)
    @Column(nullable = false, unique = true, length = 8)
    private String key;

    @ManyToOne(optional = false)
    private Product product;
}
