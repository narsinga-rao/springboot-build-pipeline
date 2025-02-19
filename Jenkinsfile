
pipeline {
  //agent { label 'any' }
  agent any
   environment {  
        ECR_REGISTRY_CREDS = "AWS_CREDENTIALS"
        AWS_ACCOUNT_ID = "AWS_ACCOUNT_ID"
        AWS_REGION = "us-east-1"
        ECR_REGISTRY = "${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com/sample-non-prod/springboot"
        ECR_REPO = "sample-non-prod/springboot"
        PLATFORM = "linux/arm64/v8"
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
            sh 'mvn sonar:sonar -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml -Dsonar.dependencyCheck.jsonReportPath=target/dependency-check-report.json -Dsonar.dependencyCheck.htmlReportPath=target/dependency-check-report.html -Dsonar.projectName=springboot-build-pipeline'
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
   
    stage('Stage VI: Logging into AWS ECR') {
      steps {
        script {
          sh "aws ecr get-login-password --region ${AWS_REGION} --profile devou-admin | docker login --username AWS --password-stdin ${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com"
        }
      }
    }

   stage('Stage VII: Build Image') {
      steps { 
        echo "Build Docker Image"
        script {
              docker.withRegistry( "https://" + ${ECR_REGISTRY} ) { 
                myImage = docker.build("${ECR_REPO}:springboot-build-pipeline-${env.BUILD_ID}", "--build-arg BUILD_PLATFORM=${PLATFORM} .")
                myImage.push()
              }
        }
      }
    }
        
   stage('Stage VIII: Scan Image ') {
      steps { 
        echo "Scanning Image for Vulnerabilities"
        sh "trivy image --scanners vuln --offline-scan ${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com/${ECR_REPO}:springboot-build-pipeline-${env.BUILD_ID} > trivyresults.txt"
        }
    }
          
   stage('Stage IX: Smoke Test ') {
      steps { 
        echo "Smoke Test the Image"
        sh "docker run -d --name smokerun -p 8081:8081 ${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com/${ECR_REPO}:springboot-build-pipeline-${env.BUILD_ID}"
        sh "sleep 90; ./check.sh"
        sh "docker rm --force smokerun"
        }
    }

  }
}