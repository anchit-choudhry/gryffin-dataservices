package com.scarletgryffin.data.config;

import com.scarletgryffin.data.util.CommonConstants;
import liquibase.Liquibase;
import liquibase.database.DatabaseFactory;
import liquibase.exception.DatabaseException;
import liquibase.exception.LiquibaseException;
import liquibase.ext.mongodb.database.MongoLiquibaseDatabase;
import liquibase.integration.spring.SpringResourceAccessor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ResourceLoader;

/**
 * Liquibase configuration to setup initial MongoDB Database/Collection.
 */
@Slf4j
@Profile(CommonConstants.LIQUIBASE)
@ConditionalOnProperty(prefix = "scarletgryffin.mongodb", name = "connection",
    havingValue = BooleanUtils.TRUE, matchIfMissing = true)
@Configuration(proxyBeanMethods = false)
public class LiquibaseConfiguration implements ResourceLoaderAware {

  @Setter
  private ResourceLoader resourceLoader;

  @Value("${spring.liquibase.url}")
  private String mongodbUrl;

  @Value("${spring.liquibase.changeLogFile}")
  private String changeLogFile;

  /**
   * Initializes the MongoDB connection.
   *
   * @return Database with connection
   * @throws DatabaseException when cannot connect
   */
  @Bean
  public MongoLiquibaseDatabase mongoLiquibaseDatabase() throws DatabaseException {
    log.info("Connecting to: {}", mongodbUrl);
    return (MongoLiquibaseDatabase) DatabaseFactory.getInstance()
        .openDatabase(mongodbUrl, null, null, null, null);
  }

  /**
   * Applies the changesets on MongoDB using Liquibase.
   *
   * @param mongoLiquibaseDatabase the {@link MongoLiquibaseDatabase} object
   * @return the Liquibase object after applying the changesets
   * @throws LiquibaseException theows LiquibaseException if thrown
   */
  @Bean
  public Liquibase setupMongoDb(final MongoLiquibaseDatabase mongoLiquibaseDatabase)
      throws LiquibaseException {
    log.info("Using changelog file {} to update changes", changeLogFile);
    Liquibase liquiBase = new Liquibase(changeLogFile, new SpringResourceAccessor(resourceLoader),
        mongoLiquibaseDatabase);
    liquiBase.update();
    return liquiBase;
  }
}
