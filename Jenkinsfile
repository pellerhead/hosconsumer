@Library('global-pipeline-libraries@java') _

def appConsts = new HosShipperConstants()

BUILD_AGENT='CR1-CICD-DEV'

pipeline {
    agent { label BUILD_AGENT }

	environment {
    JENKINS_NODE_COOKIE = "dontKillMe"
	}
    options {
	timeout(time: 60, unit: 'MINUTES')
	buildDiscarder(logRotator(numToKeepStr: '5'))
    }

    stages {
		
	stage('buildApp') {
		steps { buildApp(appConsts)
	} }
	
	stage('staticAnalysis') {
		steps {
			script{
				staticAnalysis(appConsts)
	} } }
	
	stage('releaseApp') {
		steps {
			script{
				releaseApp(appConsts)
	} } }
	
	stage('deployApp') {
		options { skipDefaultCheckout() }
		steps {
			script{
				deployApp(appConsts)
	} } }
	
	stage('gitChanges') {
		steps {
			script{
				gitChanges(appConsts)
	} } }
	
    }//Stages
	
	post {
    success {
      echo "SUCCESS"
    }
    unstable {
      echo "UNSTABLE"
    }
    failure {
      echo "FAILURE"
    }
    changed {
      echo "Build Status Changed: [From: $currentBuild.previousBuild.result, To: $currentBuild.result]"
    }
	
    always {
      script {
        //send Status mail
        notifyBuild(currentBuild.result, appConsts)
        result = currentBuild.result
        echo "Final Result is : $result"
      }
    }
  }
}//pipeline