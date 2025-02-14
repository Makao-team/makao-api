package kr.co.makao.entity.base;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
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
    /**
     * (Upper)Eng-Digit code
     */
    @Column(unique = true, nullable = false, length = 6)
    protected String code;
}