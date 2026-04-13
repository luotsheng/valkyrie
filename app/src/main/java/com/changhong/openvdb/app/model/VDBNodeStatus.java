package com.changhong.openvdb.app.model;

import com.changhong.openvdb.app.navigator.node.VDBConnectionNode;
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

        private final List<VDBConnectionNode> connections = new CopyOnWriteArrayList<>();

        private VDBConnectionNode selectedConnection;

        public static synchronized VDBNodeStatus getInstance()
        {
                if (status == null)
                        status = new VDBNodeStatus();

                return status;
        }

        public void addConnection(VDBConnectionNode connection)
        {
                connections.add(connection);
        }

        public void removeConnection(VDBConnectionNode connection)
        {
                connections.remove(connection);
        }

        public void selectedConnection(VDBConnectionNode connection)
        {
                selectedConnection = connection;
        }
}
