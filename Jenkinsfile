// Loại bỏ @NonCPS vì chúng ta dùng lệnh sh và fileExists của Pipeline
def detectChangedServices() {
    def changedServices = [] as Set

    // 1. Fetch nhánh main (Bỏ --depth=1 để đảm bảo có đủ lịch sử so sánh)
    try {
        sh 'git fetch origin main'
    } catch (Exception e) {
        echo "Cảnh báo: Không thể fetch origin main. Có thể là lần build đầu tiên."
    }

    // 2. Lấy danh sách file thay đổi (thêm || true để không crash pipeline nếu git diff lỗi)
    def diff = sh(
        script: 'git diff --name-only origin/main...HEAD || true',
        returnStdout: true
    ).trim()

    if (!diff) {
        return []
    }

    // 3. Tách chuỗi và lọc các service hợp lệ
    def files = diff.split('\n')
    for (int i = 0; i < files.length; i++) {
        def path = files[i]
        if (path.contains('/')) {
            def folder = path.split('/')[0]
            if (fileExists("${folder}/pom.xml")) {
                changedServices.add(folder)
            }
        }
    }

    return changedServices.toList()
}

pipeline {
    agent any
    
    tools {
        maven 'Maven3' 
        jdk 'Java21'   
    }

    environment {
        PATH_TO_JAVA = tool name: 'Java21', type: 'jdk'
        JAVA_HOME = "${PATH_TO_JAVA}"
        PATH = "${PATH_TO_JAVA}/bin:${env.PATH}"
        
        // Khởi tạo biến môi trường rỗng để lưu danh sách service
        CHANGED_SERVICES = ""
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        // Thêm stage này để tính toán sự thay đổi 1 LẦN DUY NHẤT
        stage('Detect Changes') {
            steps {
                script {
                    def services = detectChangedServices()
                    if (services.isEmpty()) {
                        echo "⚠️ Không phát hiện thay đổi ở service nào. Sẽ build TOÀN BỘ dự án như cấu hình mặc định."
                    } else {
                        // Lưu danh sách thành chuỗi phân cách bằng dấu phẩy
                        env.CHANGED_SERVICES = services.join(',')
                        echo "✅ Phát hiện các service bị thay đổi: ${env.CHANGED_SERVICES}"
                    }
                }
            }
        }

        stage('Test & Coverage') {
            steps {
                echo 'Đang kiểm tra phiên bản Java...'
                sh 'java -version'
                
                script {
                    if (!env.CHANGED_SERVICES) {
                        echo 'Đang chạy Unit Test và tạo report Coverage cho TOÀN BỘ dự án...'
                        sh "mvn clean test jacoco:report '-Dsurefire.excludes=**/*IT.java,**/*IT\$*.java,**/ProductCdcConsumerTest.java,**/ProductVectorRepositoryTest.java,**/VectorQueryTest.java'"
                    } else {
                        echo 'Đang chạy Unit Test và tạo report Coverage cho CÁC SERVICE BỊ THAY ĐỔI...'
                        def servicesList = env.CHANGED_SERVICES.split(',')
                        for (service in servicesList) {
                            stage("Test ${service}") {
                                sh "mvn clean test jacoco:report -pl ${service} -am '-Dsurefire.excludes=**/*IT.java,**/*IT\$*.java,**/ProductCdcConsumerTest.java,**/ProductVectorRepositoryTest.java,**/VectorQueryTest.java'"
                            }
                        }
                    }
                }
            }
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
                    if (!env.CHANGED_SERVICES) {
                        echo 'Đang đóng gói TOÀN BỘ ứng dụng...'
                        sh 'mvn package -DskipTests -DskipCompile=false'
                    } else {
                        echo 'Đang đóng gói CÁC SERVICE BỊ THAY ĐỔI...'
                        def servicesList = env.CHANGED_SERVICES.split(',')
                        for (service in servicesList) {
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
