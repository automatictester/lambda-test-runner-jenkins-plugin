package org.jenkinsci.plugins.lambdatestrunner.jenkins.it;

import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.jenkinsci.plugins.workflow.job.WorkflowRun;
import org.junit.*;
import org.jvnet.hudson.test.BuildWatcher;
import org.jvnet.hudson.test.JenkinsRule;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;

/*
 * To run this test you need AWS Lambda Test Runner to be deployed and ready to use as described in its readme:
 * https://github.com/automatictester/lambda-test-runner
 * There is no publicly available instance of AWS Lambda Test Runner due to cost implications.
 * Unless you set it up in your own AWS account, do not expect this test to pass.
 */
@Ignore
public class DeclarativePipelineIT {

    @Rule
    public JenkinsRule jenkinsRule = new JenkinsRule();

    @ClassRule
    public static BuildWatcher buildWatcher = new BuildWatcher();

    @BeforeClass
    public static void removeEnvVar() {
        System.clearProperty("mockS3");
    }

    @Test
    public void declarativePipelineTest() throws Exception {
        String pipeline = readFileToString("src/test/resources/Jenkinsfile");
        WorkflowJob project = jenkinsRule.createProject(WorkflowJob.class);
        CpsFlowDefinition pipelineDefinition = new CpsFlowDefinition(pipeline, true);
        project.setDefinition(pipelineDefinition);

        WorkflowRun build = jenkinsRule.buildAndAssertSuccess(project);

        jenkinsRule.assertLogContains("Starting Lambda function 'LambdaTestRunner' in region 'eu-west-2'", build);
        jenkinsRule.assertLogContains("command: ./mvnw test -Dtest=SmokeTest -Dmaven.repo.local=${MAVEN_USER_HOME}", build);
        jenkinsRule.assertLogContains("Command exit code: 0", build);
        jenkinsRule.assertLogContains("Running uk.co.automatictester.lambdatestrunner.SmokeTest", build);
        jenkinsRule.assertLogContains("Tests run: 1, Failures: 0, Errors: 0, Skipped: 0", build);
    }

    private String readFileToString(String file) throws IOException {
        Path path = Paths.get(file);
        return Files.lines(path).collect(Collectors.joining("\n"));
    }
}
