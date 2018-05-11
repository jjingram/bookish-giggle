package net.schmuse;

import org.flywaydb.core.Flyway;

class Migration {

    private Flyway flyway;

    Migration(String url, String username, String password) {
        flyway = new Flyway();
        flyway.setDataSource(url, username, password);
    }

    void migrate() {
        flyway.migrate();
    }
}
