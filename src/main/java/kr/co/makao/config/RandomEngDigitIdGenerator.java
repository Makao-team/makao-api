package kr.co.makao.config;

import kr.co.makao.annotation.EngDigitIdGenerator;
import kr.co.makao.util.StringUtil;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.generator.BeforeExecutionGenerator;
import org.hibernate.generator.EventType;

import java.util.EnumSet;

import static org.hibernate.generator.EventTypeSets.INSERT_ONLY;

public class RandomEngDigitIdGenerator implements BeforeExecutionGenerator {
    private final int length;

    public RandomEngDigitIdGenerator(EngDigitIdGenerator config) {
        this.length = config.length();
    }

    @Override
    public EnumSet<EventType> getEventTypes() {
        return INSERT_ONLY;
    }

    @Override
    public Object generate(SharedSessionContractImplementor session, Object owner, Object currentValue, EventType eventType) {
        return StringUtil.random(length);
    }
}
