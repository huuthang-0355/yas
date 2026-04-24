@com.cloudbees.groovy.cps.NonCPS
def getAffectedPaths() {
    def paths = []
    for (changeSet in currentBuild.changeSets) {
        for (entry in changeSet.items) {
            for (file in entry.affectedFiles) {
                paths.add(file.path)
            }
        }
    }
    return paths
}

def getChangedServices() {
    def changedServices = [] as Set

    // Ưu tiên dùng git diff so với main — bắt được cả branch mới hoàn toàn
    def gitDiffOutput = ''
    try {
        // Fetch để chắc chắn có ref origin/main
        sh(script: 'git fetch origin main --no-tags --depth=1', returnStdout: false)
        gitDiffOutput = sh(
            script: 'git diff --name-only origin/main...HEAD',
            returnStdout: true
        ).trim()
    } catch (e) {
        echo "git diff thất bại, fallback sang changeSets: ${e.message}"
    }

    def paths = []
    if (gitDiffOutput) {
        paths = gitDiffOutput.split('\n').toList()
    } else {
        // Fallback: dùng changeSets (push thêm commit lên branch đã tồn tại)
        paths = getAffectedPaths()
    }

    for (path in paths) {
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

    environment {
        PATH_TO_JAVA = tool name: 'Java21', type: 'jdk'
        JAVA_HOME    = "${PATH_TO_JAVA}"
        PATH         = "${PATH_TO_JAVA}/bin:${env.PATH}"
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Test & Coverage') {
            steps {
                echo 'Đang kiểm tra phiên bản Java...'
                sh 'java -version'

                script {
                    def services = getChangedServices()
                    def isManualTrigger = currentBuild.getBuildCauses('hudson.model.Cause$UserIdCause').size() > 0
                    if (isManualTrigger && services.isEmpty()) {
                        sh "mvn clean install -DskipTests -Djacoco.skip=true"
                        sh "mvn verify '-Dsurefire.excludes=**/*IT.java,**/*IT\$*.java,**/ProductCdcConsumerTest.java,**/ProductVectorRepositoryTest.java,**/VectorQueryTest.java' '-Dfailsafe.excludes=**/*IT.java,**/*IT\$*.java'"
                    } else if (services.isEmpty()) {
                        echo 'Không có service nào thay đổi so với main. Bỏ qua bước Test.'
                    } else {
                        echo 'Đang chạy Unit Test và kiểm tra Coverage cho CÁC SERVICE BỊ THAY ĐỔI...'
                        for (service in services) {
                            stage("Test ${service}") {
                                sh "mvn clean install -am -pl ${service} -DskipTests -Djacoco.skip=true"
                                sh "mvn verify -pl ${service} '-Dsurefire.excludes=**/*IT.java,**/*IT\$*.java,**/ProductCdcConsumerTest.java,**/ProductVectorRepositoryTest.java,**/VectorQueryTest.java' '-Dfailsafe.excludes=**/*IT.java,**/*IT\$*.java'"
                            }
                        }
                    }
                }
            }

            post {
                always {
                    echo 'Upload Test Result và TestCoverage cho Phase Test...'
                    junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml'
                    script {
                        def classPatterns = '**/target/classes'
                        def sourcePatterns = '**/src/main/java'
                        def services = getChangedServices()
                        def isManualTrigger = currentBuild.getBuildCauses('hudson.model.Cause$UserIdCause').size() > 0
                        
                        if (!services.isEmpty() && !(isManualTrigger && services.isEmpty())) {
                            classPatterns = services.collect { "${it}/target/classes" }.join(',')
                            sourcePatterns = services.collect { "${it}/src/main/java" }.join(',')
                        }
                        
                        jacoco execPattern: '**/target/jacoco.exec',
                               classPattern: classPatterns,
                               sourcePattern: sourcePatterns,
                               exclusionPattern: '**/*Application.class,**/config/**,**/exception/**,**/constants/**,**/mapper/**,**/model/**,**/dto/**,**/viewmodel/**'
                    }
                }
            }
        }

        stage('Build') {
            steps {
                script {
                    def services = getChangedServices()

                    if (services.isEmpty()) {
                        echo 'Đang đóng gói TOÀN BỘ ứng dụng (Bỏ qua test vì đã chạy ở stage trước)...'
                        sh 'mvn package -DskipTests -DskipCompile=false'
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
