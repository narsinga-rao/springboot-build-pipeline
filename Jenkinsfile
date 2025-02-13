
pipeline {
  //agent { label 'any' }
  agent any
   environment { 
        registry = "841162706196.dkr.ecr.us-east-1.amazonaws.com/sample-non-prod/springboot" 
        registryCredential = "AWS_CREDENTIALS" 
   }

  stages {
    stage('Checkout') {
      steps {
        git branch: 'master', credentialsId: 'GITHUB_CREDENTIALS', url: 'https://github.com/narsinga-rao/springboot-build-pipeline.git'
      }
    }
  
   stage('Stage I: Build') {
      steps {
        echo "Building Jar Component ..."
        sh "export JAVA_HOME=/Users/pnrao/.sdkman/candidates/java/17.0.11-amzn; mvn clean package "
      }
    }

   stage('Stage II: Code Coverage ') {
      steps {
	    echo "Running Code Coverage ..."
        sh "export JAVA_HOME=/Users/pnrao/.sdkman/candidates/java/17.0.11-amzn; mvn jacoco:report"
      }
    }

   stage('Stage III: SCA') {
      steps { 
        echo "Running Software Composition Analysis using OWASP Dependency-Check ..."
        sh "export JAVA_HOME=/Users/pnrao/.sdkman/candidates/java/17.0.11-amzn; mvn org.owasp:dependency-check-maven:check"
      }
    }

   stage('Stage IV: SAST') {
      steps { 
        echo "Running Static application security testing using SonarQube Scanner ..."
        withSonarQubeEnv('SONARQUBE') {
            sh 'mvn sonar:sonar -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml -Dsonar.dependencyCheck.jsonReportPath=target/dependency-check-report.json -Dsonar.dependencyCheck.htmlReportPath=target/dependency-check-report.html -Dsonar.projectName=wezvatech'
       }
      }
    }

   stage('Stage V: QualityGates') {
      steps { 
        echo "Running Quality Gates to verify the code quality"
        script {
          timeout(time: 1, unit: 'MINUTES') {
            def qg = waitForQualityGate()
            if (qg.status != 'OK') {
              error "Pipeline aborted due to quality gate failure: ${qg.status}"
            }
           }
        }
      }
    }
   
   stage('Stage VI: Build Image') {
      steps { 
        echo "Build Docker Image"
        script {
               docker.withRegistry( "https://" + registry, "ecr:us-east-1" + registryCredential ) { 
                 myImage = docker.build(registry + "springboot-build-pipeline:${env.BUILD_ID}")
                 myImage.push()
                }
        }
      }
    }
        
   stage('Stage VII: Scan Image ') {
      steps { 
        echo "Scanning Image for Vulnerabilities"
        sh "trivy image --scanners vuln --offline-scan springboot-build-pipeline:${env.BUILD_ID} > trivyresults.txt"
        }
    }
          
   stage('Stage VIII: Smoke Test ') {
      steps { 
        echo "Smoke Test the Image"
        sh "docker run -d --name smokerun -p 8081:8081 springboot-build-pipeline:${env.BUILD_ID}"
        sh "sleep 90; ./check.sh"
        sh "docker rm --force smokerun"
        }
    }

  }
}