package com.arcade.vp.gameservice.postgres;



import org.hibernate.dialect.PostgreSQL94Dialect;

import java.sql.Types;

public class CustomPostgresSqlDialect extends PostgreSQL94Dialect {

  public CustomPostgresSqlDialect() {
    this.registerColumnType(Types.JAVA_OBJECT, "jsonb");
    this.registerColumnType(Types.JAVA_OBJECT, "json");  }
}

