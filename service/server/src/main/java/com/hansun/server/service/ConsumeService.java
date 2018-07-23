package com.hansun.server.service;

import com.hansun.server.dto.Consume;
import com.hansun.server.db.DataStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by yuanl2 on 2017/3/29.
 */
@Service
public class ConsumeService {

    @Autowired
    private DataStore dataStore;

    public Consume createConsume(Consume consume) {
        Consume consume1 = dataStore.createConsume(consume);
        return consume1;
    }

    public Consume updateconsume(Consume consume) {
        return dataStore.updateConsume(consume);
    }

    public void deleteConsume(int consumeID) {
        dataStore.deleteConsumeByConsumeID(consumeID);
    }

    public Consume getConsume(int consumeID) {
        return dataStore.queryConsume(consumeID);
    }

    public List<Consume> getAllConsume() {
        return dataStore.queryAllConsume();
    }
}


