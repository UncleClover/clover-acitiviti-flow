package com.clover.activiti.flow.initDB;

import java.util.List;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.Test;

import com.alibaba.fastjson.JSON;

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
	public void testCreateTableByCfg() {
		ProcessEngine processEngine = ProcessEngineConfiguration.createProcessEngineConfigurationFromResource("activiti.cfg.xml").buildProcessEngine();
		System.out.println(processEngine);
	}

	@Test
	public void testDeployment() {
		ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
		Deployment deployment = processEngine.getRepositoryService().createDeployment().addClasspathResource("LeaveProcess.bpmn").deploy();
		System.out.println(JSON.toJSONString(deployment));
	}

	@Test
	public void testStartProcess() {
		ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
		ProcessInstance processInstance = processEngine.getRuntimeService().startProcessInstanceByKey("leaveProcess");
		System.out.println(processInstance.getActivityId());
		System.out.println(processInstance.getId());
		System.out.println(processInstance.getProcessDefinitionKey());
		System.out.println(processInstance.getProcessDefinitionId());
	}

	@Test
	public void testQueryTask() {
		ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
		List<Task> taskList = processEngine.getTaskService().createTaskQuery().taskAssignee("zhangdq").list();
		if (taskList != null && taskList.size() > 0) {
			for (Task task : taskList) {
				System.out.println(task.getId());
				System.out.println(task.getParentTaskId());
				System.out.println(task.getFormKey());
				System.out.println(task.getCreateTime());
			}
		}
	}

	@Test
	public void testCompleteTask() {
		ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
		processEngine.getTaskService().complete("22504");
	}
}
