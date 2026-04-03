package com.changhong.opendb.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * 连接属性
 *
 * @author Luo Tiansheng
 * @since 2026/3/26
 */
@SuppressWarnings("unused")
public class ConnectionInfo
{
        private final StringProperty name = new SimpleStringProperty();
        private final StringProperty type = new SimpleStringProperty();
        private final StringProperty host = new SimpleStringProperty();
        private final StringProperty port = new SimpleStringProperty();
        private final StringProperty db = new SimpleStringProperty();
        private final StringProperty username = new SimpleStringProperty();
        private final StringProperty password = new SimpleStringProperty();
        private final BooleanProperty savePassword = new SimpleBooleanProperty();
        private final StringProperty jdbcUrl = new SimpleStringProperty();
        private final StringProperty timezone = new SimpleStringProperty();
        private final BooleanProperty useSSL = new SimpleBooleanProperty();

        /* jdbc url 属性 */
        private final Map<String, String> jdbcQuery = new HashMap<>();

        public ConnectionInfo()
        {
                /* DO NOTHING */
        }

        public ConnectionInfo(String type)
        {
                this.name.set("本地数据库");
                this.host.set("127.0.0.1");
                this.port.set("3306");
                this.username.set("root");
                this.useSSL.set(true);

                this.type.set(type);

                setupListener();
        }

        private void setupListener()
        {
                host.addListener(event -> update());
                port.addListener(event -> update());
                timezone.addListener(event -> update());
                useSSL.addListener(event -> update());

                jdbcUrl.addListener(event -> parse());
        }

        private void updateQuery(StringBuilder builder)
        {
                jdbcQuery.forEach((k, v) -> {

                        builder.append(k)
                                .append("=")
                                .append(v)
                                .append("&");

                });

                builder.deleteCharAt(builder.length() - 1);
        }

        private void parseQuery(String uriQuery)
        {
                if (uriQuery != null) {

                        String[] queries = uriQuery.split("&");

                        for (String query : queries) {
                                String[] entry = query.split("=");
                                jdbcQuery.put(entry[0], entry[1]);
                        }

                }

                String jqUseSSL = jdbcQuery.get("useSSL");
                useSSL.set(jqUseSSL != null && jqUseSSL.equals("true"));

                String jqTimezone = jdbcQuery.get("timezone");
                timezone.set(jqTimezone);
        }

        private void update()
        {
                StringBuilder builder = new StringBuilder(
                        String.format("jdbc:%s://%s:%s?", type.get(), host.get(), port.get())
                );

                jdbcQuery.put("timezone", timezone.get());
                jdbcQuery.put("useSSL", String.valueOf(useSSL.get()));

                updateQuery(builder);

                jdbcUrl.setValue(builder.toString());
        }

        private void parse()
        {
                // jdbc:mysql://localhost:3306/mydatabase?useSSL=false&serverTimezone=UTC

                String withUrl = jdbcUrl.get().substring(5);

                try {

                        URI uri = URI.create(withUrl);

                        String[] authority = uri.getAuthority().split(":");

                        type.set(uri.getScheme());
                        host.set(authority[0]);
                        port.set(String.valueOf(authority[1]));

                        String path = uri.getPath();
                        if (path != null && !path.isEmpty())
                                db.set(path.substring(1));

                        parseQuery(uri.getQuery());

                } catch (Exception e) {

                        /* ignore exception */

                }

        }

        /* property */
        public StringProperty nameProperty() { return name; }
        public StringProperty typeProperty() { return type; }
        public StringProperty hostProperty() { return host; }
        public StringProperty portProperty() { return port; }
        public StringProperty dbProperty() { return db; }
        public StringProperty usernameProperty() { return username; }
        public StringProperty passwordProperty() { return password; }
        public BooleanProperty savePasswordProperty() { return savePassword; }
        public StringProperty jdbcUrlProperty() { return jdbcUrl; }
        public StringProperty timezoneProperty() { return timezone; }
        public BooleanProperty useSSLProperty() { return useSSL; }

        /* get */
        public String getName() { return name.get();  }
        public String getType() { return type.get();  }
        public String getHost() { return host.get();  }
        public String getPort() { return port.get();  }
        public String getDb() { return db.get();  }
        public String getUsername() { return username.get();  }
        public String getPassword() { return password.get();  }
        public Boolean getSavePassword() { return savePassword.get();  }
        public String getJdbcUrl() { return jdbcUrl.get();  }
        public String getTimezone() { return timezone.get();  }
        public Boolean getUseSSL() { return useSSL.get();  }

        /* get */
        public void setName(String name) { this.name.set(name);  }
        public void setType(String type) { this.type.set(type);  }
        public void setHost(String host) { this.host.set(host);  }
        public void setPort(String port) { this.port.set(port);  }
        public void setDb(String db) { this.db.set(db);  }
        public void setUsername(String username) { this.username.set(username);  }
        public void setPassword(String password) { this.password.set(password);  }
        public void setSavePassword(Boolean savePassword) { this.savePassword.set(savePassword);  }
        public void setJdbcUrl(String jdbcUrl) { this.jdbcUrl.set(jdbcUrl);  }
        public void setTimezone(String timezone) { this.timezone.set(timezone);  }
        public void setUseSSL(Boolean useSSL) { this.useSSL.set(useSSL);  }
}
