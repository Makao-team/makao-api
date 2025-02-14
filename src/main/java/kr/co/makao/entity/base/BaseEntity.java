package kr.co.makao.entity.base;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import kr.co.makao.annotation.EngDigitIdGenerator;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@MappedSuperclass
public abstract class BaseEntity extends TimeStamp {
    @EngDigitIdGenerator(length = 6)
    @Column(unique = true, nullable = false, length = 6)
    protected String code;
}