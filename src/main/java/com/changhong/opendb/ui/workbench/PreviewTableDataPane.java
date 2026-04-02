package com.changhong.opendb.ui.workbench;

import com.changhong.opendb.driver.executor.SQLExecutor;
import com.changhong.opendb.driver.QueryResultSet;
import com.changhong.opendb.driver.TableMetadata;
import com.changhong.opendb.resource.Assets;
import com.changhong.opendb.utils.Catcher;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.layout.BorderPane;

/**
 * @author Luo Tiansheng
 * @since 2026/3/30
 */
@SuppressWarnings({"FieldCanBeLocal", "FieldMayBeFinal"})
public class PreviewTableDataPane extends BorderPane
{
        private int start = 0;
        private int size = 1024;
        private Node oldGraphic;

        private final Tab ownerTab;
        private final SQLExecutor sqlExecutor;
        private final String database;
        private final TableMetadata tableInfo;
        private final ResultSetViewPane resultSetViewPane;

        public PreviewTableDataPane(Tab ownerTab,
                                    SQLExecutor sqlExecutor,
                                    String database,
                                    TableMetadata tableInfo)
        {
                this.ownerTab = ownerTab;
                this.sqlExecutor = sqlExecutor;
                this.database = database;
                this.tableInfo = tableInfo;
                this.resultSetViewPane = new ResultSetViewPane();

                setCenter(resultSetViewPane);
        }

        private void setLoadingIndicator()
        {
                oldGraphic = ownerTab.getGraphic();
                ownerTab.setGraphic(Assets.newProgressIndicator());
        }

        private void removeLoadingIndicator()
        {
                ownerTab.setGraphic(oldGraphic);
        }


        public void asyncUpdate()
        {
                setLoadingIndicator();

                new Thread(() -> {
                        try {
                                QueryResultSet rs = sqlExecutor.select(
                                        database,
                                        tableInfo,
                                        start,
                                        size);
                                Platform.runLater(() -> resultSetViewPane.refresh(rs));
                        } catch (Exception e) {
                                Catcher.ithrow(e);
                        } finally {
                                Platform.runLater(this::removeLoadingIndicator);
                        }
                }).start();
        }
}
