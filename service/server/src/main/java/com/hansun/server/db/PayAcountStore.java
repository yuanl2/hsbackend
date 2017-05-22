package com.hansun.server.db;

import com.hansun.dto.PayAccount;
import com.hansun.server.common.ServerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by yuanl2 on 2017/4/27.
 */
@Repository
public class PayAcountStore {

    private Map<String, PayAccount> payAccountCache = new ConcurrentHashMap<>();

    @Autowired
    private ConnectionPoolManager connectionPoolManager;

    private PayAccountTable payAccountTable;

    @PostConstruct
    private void init() {
        payAccountTable = new PayAccountTable(connectionPoolManager);
        initCache();
    }

    @PreDestroy
    public void destroy() {
        try {
            payAccountCache.clear();
            connectionPoolManager.destroy();
        } catch (SQLException e) {
            throw new ServerException(e);
        }
    }

    private void initCache() {
        Optional<List<PayAccount>> payAccountList = payAccountTable.selectAll();
        if (payAccountList.isPresent()) {
            payAccountList.get().forEach(d -> payAccountCache.put(d.getAccountName(), d));
        }
    }

    public PayAccount getPayAccount(String name) {
        return payAccountCache.computeIfAbsent(name, k -> {
            Optional<PayAccount> result = payAccountTable.select(k);
            if (result.isPresent()) {
                PayAccount d = result.get();
                return d;
            }
            return null;
        });
    }
    public PayAccount updatePayAccount(PayAccount device) {
        PayAccount device2 = payAccountCache.get(device.getAccountName());
        if (device2 == null) {
            Optional<PayAccount> device1 = payAccountTable.select(device.getAccountName());
            if (device1 == null || !device1.isPresent()) {
                throw ServerException.conflict("Cannot update PayAccount for not exist.");
            }
        }
        payAccountTable.update(device, device.getId());
        payAccountCache.put(device.getAccountName(), device);
        return device;
    }

    public void deletePayAccount(String name) {
        payAccountTable.delete(name);
        payAccountCache.remove(name);
    }

}
