def getChangedServices() {
    def changedServices = [] as Set

    // Use git diff to compare current branch against main.
    // This works correctly even for brand-new branches where
    // Jenkins has no previous build state (changeSets would be empty).
    def diffOutput = sh(
        script: "git diff --name-only origin/main...HEAD",
        returnStdout: true
    ).trim()

    if (diffOutput.isEmpty()) {
        return changedServices
    }

    for (path in diffOutput.split('\n')) {
        if (path.contains('/')) {
            def folder = path.split('/')[0]
            if (fileExists("${folder}/pom.xml")) {
                changedServices.add(folder)
            }
        }
    }
    return changedServices
}


pipeline {
    agent any
    
    tools {
        maven 'Maven3' 
        jdk 'Java21'   
    }

    // Ép Java21
    environment {
        PATH_TO_JAVA = tool name: 'Java21', type: 'jdk'
        JAVA_HOME = "${PATH_TO_JAVA}"
        PATH = "${PATH_TO_JAVA}/bin:${env.PATH}"
    }

    stages {
        stage('Checkout') {
            steps {
                // Lấy code từ GitHub về
                checkout scm
            }
        }

        stage('Test & Coverage') {
            steps {
                echo 'Đang kiểm tra phiên bản Java...'
                sh 'java -version'
                
                script {
                    // Detect if this build was triggered manually via 'Build Now'.
                    // currentBuild.getBuildCauses() returns an empty cause for manual triggers.
                    def isManualTrigger = currentBuild.getBuildCauses('hudson.model.Cause$UserIdCause').size() > 0

                    def services = getChangedServices()

                    if (isManualTrigger && services.isEmpty()) {
                        // 'Build Now' clicked in Jenkins AND no git diff: full project build
                        echo 'Manual trigger detected: Đang chạy Unit Test cho TOÀN BỘ dự án...'
                        sh "mvn clean test jacoco:report '-Dsurefire.excludes=**/*IT.java,**/*IT\$*.java,**/ProductCdcConsumerTest.java,**/ProductVectorRepositoryTest.java,**/VectorQueryTest.java'"
                    } else if (services.isEmpty()) {
                        // Git push detected, but no service folder changed (e.g. only root files changed)
                        echo 'Không có service nào thay đổi so với main. Bỏ qua bước Test.'
                    } else {
                        echo 'Đang chạy Unit Test và tạo report Coverage cho CÁC SERVICE BỊ THAY ĐỔI...'
                        for (service in services) {
                            stage("Test ${service}") {
                                sh "mvn clean test jacoco:report -pl ${service} -am '-Dsurefire.excludes=**/*IT.java,**/*IT\$*.java,**/ProductCdcConsumerTest.java,**/ProductVectorRepositoryTest.java,**/VectorQueryTest.java'"
                            }
                        }
                    }
                }
            }

            // Di chuyển logic upload sang Phase Test theo yêu cầu của bài
            post {
                always {
                    echo 'Upload Test Result và TestCoverage cho Phase Test...'
                    junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml'
                    jacoco execPattern: '**/target/jacoco.exec',
                           classPattern: '**/target/classes',
                           sourcePattern: '**/src/main/java'
                }
            }
        }

        stage('Build') {
            steps {
                script {
                    def isManualTrigger = currentBuild.getBuildCauses('hudson.model.Cause$UserIdCause').size() > 0

                    def services = getChangedServices()

                    if (isManualTrigger && services.isEmpty()) {
                        // 'Build Now' clicked in Jenkins AND no git diff: full project build
                        echo 'Manual trigger detected: Đang đóng gói TOÀN BỘ ứng dụng...'
                        sh 'mvn package -DskipTests -DskipCompile=false'
                    } else if (services.isEmpty()) {
                        // Git push with no service changes
                        echo 'Không có service nào thay đổi so với main. Bỏ qua bước Build.'
                    } else {
                        echo 'Đang đóng gói CÁC SERVICE BỊ THAY ĐỔI...'
                        for (service in services) {
                            stage("Build ${service}") {
                                sh "mvn package -pl ${service} -am -DskipTests -DskipCompile=false"
                            }
                        }
                    }
                }
            }
        }
    }
}
