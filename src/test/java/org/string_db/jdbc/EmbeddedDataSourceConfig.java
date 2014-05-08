package org.string_db.jdbc;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.jdbc.datasource.embedded.*;

import javax.sql.DataSource;
import java.sql.Driver;
import java.util.Properties;

@Configuration
public class EmbeddedDataSourceConfig implements DataSourceConfig {

    protected final EmbeddedDatabase hsqldb;

    public EmbeddedDataSourceConfig() {
        /**
         * HSQLDB requires by default that all VARCHAR columns declare size, but our schema export
         *   doesn't do it so let's make hsql use the default value instead:
         */
        final Properties connProperties = new Properties();
        connProperties.setProperty("sql.enforce_size", "false");
        hsqldb = new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.HSQL)
                .addScript("classpath:schema.sql")
                .addScript("classpath:test-data.sql")
                .setDataSourceFactory(
                        //cannot subclass SimpleDriverDataSourceFactory since it's package protected so copy-paste:
                        new DataSourceFactory() {
                            final SimpleDriverDataSource dataSource = new SimpleDriverDataSource(null, "lazy", connProperties);

                            @Override
                            public ConnectionProperties getConnectionProperties() {
                                return new ConnectionProperties() {
                                    @Override
                                    public void setDriverClass(Class<? extends Driver> driverClass) {
                                        dataSource.setDriverClass(driverClass);
                                    }

                                    @Override
                                    public void setUrl(String url) {
                                        dataSource.setUrl(url);
                                    }

                                    @Override
                                    public void setUsername(String username) {
                                        dataSource.setUsername(username);
                                    }

                                    @Override
                                    public void setPassword(String password) {
                                        dataSource.setPassword(password);
                                    }

                                };
                            }

                            @Override
                            public DataSource getDataSource() {
                                return dataSource;
                            }
                        }
                )
                .build();
    }

    @Override
    @Bean //need to repeat the annotation
    @Scope("singleton")
    public DataSource dataSource() {
        return hsqldb;
    }
}
