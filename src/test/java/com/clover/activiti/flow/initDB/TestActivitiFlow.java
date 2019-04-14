package com.clover.activiti.flow.initDB;

import java.io.InputStream;
import java.util.zip.ZipInputStream;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.DeploymentBuilder;
import org.junit.Test;

public class TestActivitiFlow {
	@Test
	public void testCreateDataSource() {
		ProcessEngine processEngine = ProcessEngineConfiguration.createProcessEngineConfigurationFromResource("activiti.cfg.xml").buildProcessEngine();
		System.out.println(processEngine);
	}

	@Test
	public void testCreateTableByCode() {
		ProcessEngineConfiguration pecfg = ProcessEngineConfiguration.createStandaloneInMemProcessEngineConfiguration();
		pecfg.setJdbcDriver("oracle.jdbc.driver.OracleDriver");
		pecfg.setJdbcUrl("jdbc:oracle:thin:@127.0.0.1:1521:orcl");
		pecfg.setJdbcUsername("clover");
		pecfg.setJdbcPassword("123456");

		pecfg.setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE);
		ProcessEngine processEngine = pecfg.buildProcessEngine();
		System.out.println(processEngine);
	}

	@Test
	public void testDeployment() {
		ProcessEngine processEngine = ProcessEngineConfiguration.createProcessEngineConfigurationFromResource("activiti.cfg.xml").buildProcessEngine();
		RepositoryService repositoryService = processEngine.getRepositoryService();
		DeploymentBuilder deploymentBuilder = repositoryService.createDeployment();
		InputStream in = this.getClass().getResourceAsStream("HnBossIdMgrFlow.zip");
		ZipInputStream zipInputStream = new ZipInputStream(in);
		deploymentBuilder.addZipInputStream(zipInputStream);

		// 发布
		deploymentBuilder.deploy();
	}
}
