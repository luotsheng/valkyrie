package com.changhong.opendb.app.ui.pane;

import com.changhong.driver.api.DataGrid;
import com.changhong.driver.api.Driver;
import com.changhong.driver.api.Session;
import com.changhong.driver.api.Table;
import com.changhong.opendb.app.resource.Assets;
import com.changhong.opendb.app.ui.widgets.dialog.VFXDialogHelper;
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
        private final Driver driver;
        private final Session session;
        private final Table table;
        private final DataGridViewPane mutableDataGridViewPane;

        public PreviewTableDataPane(Tab ownerTab,
                                    Session session,
                                    Driver driver,
                                    Table table)
        {
                this.ownerTab = ownerTab;
                this.session = session;
                this.driver = driver;
                this.table = table;
                this.mutableDataGridViewPane = new DataGridViewPane(true);

                mutableDataGridViewPane.setReloadProgressListener(new DataGridViewPane.ReloadProgressListener()
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
                                DataGrid rs = driver.selectByPage(session, table.getName(), start, size);
                                Platform.runLater(() -> mutableDataGridViewPane.render(rs));
                        } catch (Exception e) {
                                VFXDialogHelper.alert(e);
                        } finally {
                                Platform.runLater(this::removeLoadingIndicator);
                        }
                }).start();
        }
}
