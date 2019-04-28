package com.epam.finaltask.connectionpool;

import java.util.TimerTask;

class PoolSupervisor extends TimerTask {

    @Override
    public void run() {
        ConnectionPool.instance.removeClosedConnections();
    }
}
