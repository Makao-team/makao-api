package kr.co.makao.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import kr.co.makao.entity.base.HistoryMetaData;

@Entity
public class StoreHistory extends HistoryMetaData {
    @ManyToOne(optional = false)
    private Store store;
}
