package valkyrie.app.model;

import lombok.Getter;
import valkyrie.app.explorer.UIConnectionNode;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 节点状态管理器
 *
 * @author Luo Tiansheng
 * @since 2026/3/30
 */
@Getter
public class UIExplorerStatus
{
        private static UIExplorerStatus status = null;

        private final List<UIConnectionNode> connections = new CopyOnWriteArrayList<>();

        private UIConnectionNode selectedConnection;

        public static synchronized UIExplorerStatus getInstance()
        {
                if (status == null)
                        status = new UIExplorerStatus();

                return status;
        }

        public void addConnection(UIConnectionNode connection)
        {
                connections.add(connection);
        }

        public void unselectConnection(UIConnectionNode connection)
        {
                if (connection == selectedConnection)
                        selectedConnection = null;
        }

        public void removeConnection(UIConnectionNode connection)
        {
                unselectConnection(connection);
                connections.remove(connection);
        }

        public void selectedConnection(UIConnectionNode connection)
        {
                selectedConnection = connection;
        }
}
