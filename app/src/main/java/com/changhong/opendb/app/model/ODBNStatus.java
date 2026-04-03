package com.changhong.opendb.app.model;

import com.changhong.opendb.app.ui.navigator.node.ODBNConnection;
import lombok.Getter;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 节点状态管理器
 *
 * @author Luo Tiansheng
 * @since 2026/3/30
 */
public class ODBNStatus
{
        private static ODBNStatus status = null;

        @Getter
        private final List<ODBNConnection> connections = new CopyOnWriteArrayList<>();

        @Getter
        private ODBNConnection selectedConnection;

        public static synchronized ODBNStatus getInstance()
        {
                if (status == null)
                        status = new ODBNStatus();

                return status;
        }

        public void addConnection(ODBNConnection connection)
        {
                connections.add(connection);
        }

        public void selectedConnection(ODBNConnection connection)
        {
                selectedConnection = connection;
        }
}
