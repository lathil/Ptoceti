pipeline {
    agent any
    tools {
            maven 'Maven 3.6.1'
            jdk 'Jdk 11'
        }
    stages {
        stage('Build') {
            steps {
               sh 'mvn -B -Dmaven.test.failure.ignore=true install deploy'
            }
        }
    }
}