def getChangedServices() {
    def changedServices = [] as Set
    for (changeset in currentBuild.changeSets) {
        for (entry in changeset.items) {
            for (file in entry.affectedFiles) {
                if (file.path.contains('/')) {
                    def folder = file.path.split('/')[0]
                    if (fileExists("${folder}/pom.xml")) {
                        changedServices.add(folder)
                    }
                }
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
                    
                    if (services.isEmpty()) {
                        echo 'Đang chạy Unit Test và tạo report Coverage cho TOÀN BỘ dự án...'
                        sh "mvn clean test jacoco:report '-Dsurefire.excludes=**/*IT.java,**/*IT\$*.java,**/ProductCdcConsumerTest.java,**/ProductVectorRepositoryTest.java,**/VectorQueryTest.java'"
                    } else {
                        echo 'Đang chạy Unit Test và tạo report Coverage cho CÁC SERVICE BỊ THAY ĐỔI...'
                        for (service in services) {
                            stage("Test ${service}") {
                                sh "mvn clean test jacoco:report -pl ${service} '-Dsurefire.excludes=**/*IT.java,**/*IT\$*.java,**/ProductCdcConsumerTest.java,**/ProductVectorRepositoryTest.java,**/VectorQueryTest.java'"
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
                                sh "mvn package -pl ${service} -DskipTests -DskipCompile=false"
                            }
                        }
                    }
                }
            }
        }
    }
}
