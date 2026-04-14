package com.changhong.openvdb.app.model;

import com.changhong.openvdb.app.navigator.node.UIConnectionNode;
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
public class UINodeGlobalStatus
{
        private static UINodeGlobalStatus status = null;

        private final List<UIConnectionNode> connections = new CopyOnWriteArrayList<>();

        private UIConnectionNode selectedConnection;

        public static synchronized UINodeGlobalStatus getInstance()
        {
                if (status == null)
                        status = new UINodeGlobalStatus();

                return status;
        }

        public void addConnection(UIConnectionNode connection)
        {
                connections.add(connection);
        }

        public void removeConnection(UIConnectionNode connection)
        {
                connections.remove(connection);

                if (connection == selectedConnection)
                        selectedConnection = null;
        }

        public void selectedConnection(UIConnectionNode connection)
        {
                selectedConnection = connection;
        }
}
