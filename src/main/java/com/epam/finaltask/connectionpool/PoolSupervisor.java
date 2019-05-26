package com.epam.finaltask.connectionpool;

import java.util.TimerTask;

/**
 * TimerTask subclass that purpose is to clear connection pool from closed connections.
 */
class PoolSupervisor extends TimerTask {

    /**
     * Removes closed connections from the connection pool.
     */
    @Override
    public void run() {
        ConnectionPool.INSTANCE.removeClosedConnections();
    }
}
