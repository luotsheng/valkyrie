package valkyrie.monacofx;

import javafx.scene.layout.StackPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

/**
 * @author Luo Tiansheng
 * @since 2026/5/6
 */
public class MonacoFx extends StackPane
{
        private final WebView webView = new WebView();
        private final WebEngine engine = webView.getEngine();

        @SuppressWarnings("DataFlowIssue")
        public MonacoFx()
        {
                webView.setContextMenuEnabled(false);
                engine.load(getClass().getResource("/static/editor.html").toExternalForm());
                getChildren().add(webView);

                webView.prefWidthProperty().bind(widthProperty());
                webView.prefHeightProperty().bind(heightProperty());
        }

        public WebEngine engine()
        {
                return engine;
        }
}
