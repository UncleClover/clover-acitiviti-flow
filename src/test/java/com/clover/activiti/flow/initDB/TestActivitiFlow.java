package com.clover.activiti.flow.initDB;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipInputStream;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

import com.alibaba.fastjson.JSON;

public class TestActivitiFlow {
	@Test
	public void testCreateTableByCode() {
		ProcessEngineConfiguration pecfg = ProcessEngineConfiguration.createStandaloneInMemProcessEngineConfiguration();
		
		// Oracle
//		pecfg.setJdbcDriver("oracle.jdbc.driver.OracleDriver");
//		pecfg.setJdbcUrl("jdbc:oracle:thin:@127.0.0.1:1521:orcl");
//		pecfg.setJdbcUsername("clover");
//		pecfg.setJdbcPassword("123456");
		
		// MySQL
		pecfg.setJdbcDriver("com.mysql.jdbc.Driver");
		pecfg.setJdbcUrl("jdbc:mysql://127.0.0.1:3306/clover_flow?characterEncoding=utf8");
		pecfg.setJdbcUsername("root");
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

	/**
	 * 发布工作流
	 */
	@Test
	public void testDeployment() {
		ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
		Deployment deployment = processEngine.getRepositoryService().createDeployment().addClasspathResource("LeaveProcess.bpmn").deploy();
		System.out.println(JSON.toJSONString(deployment));
	}

	/**
	 * 通过压缩包发布工作流
	 */
	@Test
	public void testDeploymentByZip() {
		ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
		InputStream in = this.getClass().getClassLoader().getResourceAsStream("HnBossIdMgrFlow.zip");
		ZipInputStream zipInputStream = new ZipInputStream(in);
		Deployment deployment = processEngine.getRepositoryService().createDeployment().addZipInputStream(zipInputStream).deploy();
		System.out.println(JSON.toJSONString(deployment));
	}

	/**
	 * 开启工作流
	 */
	@Test
	public void testStartProcess() {
		ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
		RuntimeService runtimeService = processEngine.getRuntimeService();
		ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("leaveProcess");
		System.out.println(processInstance.getActivityId());
		System.out.println(processInstance.getId());
		System.out.println(processInstance.getProcessDefinitionKey());
		System.out.println(processInstance.getProcessDefinitionId());
		System.out.println(processInstance.getSuperExecutionId());
	}

	/**
	 * 查询工作流信息
	 */
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

	/**
	 * 审批工作流
	 */
	@Test
	public void testCompleteTask() {
		ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
		processEngine.getTaskService().complete("25002");
	}

	@Test
	public void testQueryProcDef() {
		ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
		List<ProcessDefinition> procDefList = processEngine.getRepositoryService().createProcessDefinitionQuery().processDefinitionKey("leaveProcess").orderByProcessDefinitionVersion().desc().list();
		if (procDefList == null || procDefList.size() < 1) {
			return;
		}

		for (ProcessDefinition procDef : procDefList) {
			System.out.println(procDef.getId());
			System.out.println(procDef.getDeploymentId());
			System.out.println(procDef.getKey());
			System.out.println(procDef.getName());
			System.out.println(procDef.getVersion());
			System.out.println("===============================");
		}
	}

	@Test
	public void testDelProcDef() {
		ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
		processEngine.getRepositoryService().deleteDeployment("1", true);
	}

	@Test
	public void testQueryProcDefImg() throws IOException {
		ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
		InputStream in = processEngine.getRepositoryService().getResourceAsStream("30001", "LeaveProcess.png");
		File destination = new File("D:\\setting\\LeaveProcess.png");
		FileUtils.copyInputStreamToFile(in, destination);
	}

	@Test
	public void testSetTaskProcessVariable() {
		ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
		TaskService taskService = processEngine.getTaskService();
		taskService.setVariableLocal("32504", "reason", "哈哈");
	}

	@Test
	public void testSetRuntimeProcessVariable() {
		ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
		RuntimeService runtimeService = processEngine.getRuntimeService();
		runtimeService.setVariableLocal("32501", "reason", "回去睡个回笼觉");
	}

	@Test
	public void testQueryProcessVariable() {
		ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
		TaskService taskService = processEngine.getTaskService();
		Map<String, Object> map = taskService.getVariables("32504");
		System.out.println(map.size());

		RuntimeService runtimeService = processEngine.getRuntimeService();
		Map<String, Object> runTimeMap = runtimeService.getVariablesLocal("32501");
		System.out.println(runTimeMap.size());
	}

	@Test
	public void testQueryTaskHis() {
		ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
		List<HistoricTaskInstance> hisTaskList = processEngine.getHistoryService().createHistoricTaskInstanceQuery().taskId("32504").orderByHistoricTaskInstanceEndTime().asc().list();

		if (hisTaskList == null || hisTaskList.size() == 0) {
			return;
		}

		for (HistoricTaskInstance historicTaskInstance : hisTaskList) {
			System.out.print(historicTaskInstance.getAssignee() + "\t");
			System.out.print(historicTaskInstance.getName() + "\t");
			System.out.print(historicTaskInstance.getTaskDefinitionKey() + "\t");
			System.out.print(historicTaskInstance.getTaskDefinitionKey() + "\t");
			System.out.println();
		}
	}

	@Test
	public void testQueryProcHis() {
		ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
		List<HistoricProcessInstance> hisProcessInstanceList = processEngine.getHistoryService().createHistoricProcessInstanceQuery().processInstanceId("10001").orderByProcessInstanceStartTime().asc().list();

		if (hisProcessInstanceList == null || hisProcessInstanceList.size() == 0) {
			return;
		}

		for (HistoricProcessInstance historicProcessInstance : hisProcessInstanceList) {
			System.out.print(historicProcessInstance.getDeploymentId() + "\t");
			System.out.print(historicProcessInstance.getStartTime() + "\t");
			System.out.println(historicProcessInstance.getProcessDefinitionKey());
		}
	}

	@Test
	public void testQueryHis() {
		ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
		List<HistoricActivityInstance> list = processEngine.getHistoryService().createHistoricActivityInstanceQuery().processInstanceId("10001").orderByHistoricActivityInstanceStartTime().asc().list();

		if (list == null || list.size() == 0) {
			return;
		}

		for (HistoricActivityInstance historicActivityInstance : list) {
			System.out.print(historicActivityInstance.getActivityId() + "\t");
			System.out.print(historicActivityInstance.getActivityName() + "\t");
			System.out.print(historicActivityInstance.getCalledProcessInstanceId() + "\t");
			System.out.print(historicActivityInstance.getExecutionId() + "\t");
			System.out.print(historicActivityInstance.getProcessDefinitionId() + "\t");
			System.out.print(historicActivityInstance.getAssignee() + "\t");
			System.out.print(historicActivityInstance.getTaskId() + "\t");
			System.out.println(historicActivityInstance.getId());
		}
	}
}
