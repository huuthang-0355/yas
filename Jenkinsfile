@com.cloudbees.groovy.cps.NonCPS
def getAffectedPaths() {
    def paths = []
    for (changeSet in currentBuild.changeSets) {
        for (entry in changeSet.items) {
            for (file in entry.affectedFiles) {
                paths.add(file.path) // Lưu vào mảng String đơn giản
            }
        }
    }
    return paths
}

def getChangedServices() {
    def changedServices = [] as Set
    def paths = getAffectedPaths()
    
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
                    def services = getChangedServices()
                    
                    if (isManualTrigger && services.isEmpty()) {
                        // 'Build Now' in Jenkins AND no git diff → full project build
                        // Step 1: build all dependencies, skip jacoco to avoid failures in other modules
                        sh "mvn clean install -DskipTests -Djacoco.skip=true"
                        // Step 2: run verify on the full project so jacoco:check applies everywhere
                        sh "mvn verify '-Dsurefire.excludes=**/*IT.java,**/*IT\$*.java,**/ProductCdcConsumerTest.java,**/ProductVectorRepositoryTest.java,**/VectorQueryTest.java' '-Dfailsafe.excludes=**/*IT.java,**/*IT\$*.java'"
                    } else if (services.isEmpty()) {
                        echo 'Kh\u00f4ng c\u00f3 service n\u00e0o thay \u0111\u1ed5i so v\u1edbi main. B\u1ecf qua b\u01b0\u1edbc Test.'
                    } else {
                        echo '\u0110ang ch\u1ea1y Unit Test v\u00e0 ki\u1ec3m tra Coverage cho C\u00c1C SERVICE B\u1eca THAY \u0110\u1ed4I...'
                        for (service in services) {
                            stage("Test ${service}") {
                                // Step 1: install all dependency modules WITHOUT running their jacoco:check
                                // -am pulls in all upstream deps; -DskipTests skips their tests;
                                // -Djacoco.skip=true prevents common-library (and others) from failing
                                // the 70% threshold — we only care about enforcing it on OUR changed service.
                                sh "mvn clean install -am -pl ${service} -DskipTests -Djacoco.skip=true"

                                // Step 2: now run verify on ONLY the changed service.
                                // Dependencies are already installed, so Maven won't rebuild them.
                                // jacoco:check now runs exclusively for this service module.
                                sh "mvn verify -pl ${service} '-Dsurefire.excludes=**/*IT.java,**/*IT\$*.java,**/ProductCdcConsumerTest.java,**/ProductVectorRepositoryTest.java,**/VectorQueryTest.java' '-Dfailsafe.excludes=**/*IT.java,**/*IT\$*.java'"
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
