package com.changhong.opendb.app.model;

import com.changhong.opendb.app.ui.navigator.node.VDNConnectionNode;
import lombok.Getter;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 节点状态管理器
 *
 * @author Luo Tiansheng
 * @since 2026/3/30
 */
@Getter
public class VDBNodeStatus
{
        private static VDBNodeStatus status = null;

        private final List<VDNConnectionNode> connections = new CopyOnWriteArrayList<>();

        private VDNConnectionNode selectedConnection;

        public static synchronized VDBNodeStatus getInstance()
        {
                if (status == null)
                        status = new VDBNodeStatus();

                return status;
        }

        public void addConnection(VDNConnectionNode connection)
        {
                connections.add(connection);
        }

        public void removeConnection(VDNConnectionNode connection)
        {
                connections.remove(connection);
        }

        public void selectedConnection(VDNConnectionNode connection)
        {
                selectedConnection = connection;
        }
}
