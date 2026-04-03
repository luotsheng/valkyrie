package com.changhong.opendb.app.ui.pane;

import com.changhong.opendb.app.driver.executor.SQLExecutor;
import com.changhong.opendb.app.driver.MutableDataGrid;
import com.changhong.opendb.app.driver.TableMetaData;
import com.changhong.opendb.app.resource.Assets;
import com.changhong.opendb.app.utils.Catcher;
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
        private final TableMetaData tableInfo;
        private final MutableDataGridViewPane mutableDataGridViewPane;

        public PreviewTableDataPane(Tab ownerTab,
                                    SQLExecutor sqlExecutor,
                                    String database,
                                    TableMetaData tableInfo)
        {
                this.ownerTab = ownerTab;
                this.sqlExecutor = sqlExecutor;
                this.database = database;
                this.tableInfo = tableInfo;
                this.mutableDataGridViewPane = new MutableDataGridViewPane();

                mutableDataGridViewPane.setReloadProgressListener(new MutableDataGridViewPane.ReloadProgressListener()
                {
                        @Override
                        public void start()
                        {
                                setLoadingIndicator();
                        }

                        @Override
                        public void end()
                        {
                                removeLoadingIndicator();
                        }
                });

                setCenter(mutableDataGridViewPane);
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
                                MutableDataGrid rs = sqlExecutor.select(
                                        tableInfo,
                                        start,
                                        size);
                                Platform.runLater(() -> mutableDataGridViewPane.render(rs));
                        } catch (Exception e) {
                                Catcher.ithrow(e);
                        } finally {
                                Platform.runLater(this::removeLoadingIndicator);
                        }
                }).start();
        }
}
