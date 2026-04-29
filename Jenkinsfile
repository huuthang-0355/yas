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

@com.cloudbees.groovy.cps.NonCPS
def extractUniqueFolders(List paths) {
    def folders = [] as Set
    for (path in paths) {
        if (path.contains('/')) {
            folders.add(path.split('/')[0])
        }
    }
    return folders.toList()
}

def getChangedServices() {
    def changedServices = [] as Set

    // Ưu tiên git diff với main để xác định chính xác service thay đổi
    def gitDiffOutput = ''
    try {
        sh(script: 'git fetch --no-tags --prune --depth=1 origin +refs/heads/main:refs/remotes/origin/main', returnStdout: false)
        gitDiffOutput = sh(
            script: 'git diff --name-only origin/main...HEAD',
            returnStdout: true
        ).trim()
    } catch (e) {
        echo "git diff failed, falling back to changeSets: ${e.message}"
    }

    def paths = []
    if (gitDiffOutput) {
        paths = gitDiffOutput.split('\n').toList()
    } else {
        paths = getAffectedPaths()
    }

    def uniqueFolders = extractUniqueFolders(paths)
    for (folder in uniqueFolders) {
        if (fileExists("${folder}/pom.xml")) {
            changedServices.add(folder)
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
                    def services = getChangedServices().toList().sort()
                    
                    if (services.isEmpty()) {
                        echo 'Không phát hiện service thay đổi. Bỏ qua Test & Coverage theo phạm vi service.'
                    } else {
                        def serviceSelector = services.join(',')
                        echo "Đang chạy Unit Test và tạo report Coverage cho CÁC SERVICE BỊ THAY ĐỔI: ${services}"
                        sh "mvn -pl ${serviceSelector} -am clean"
                        for (String svc : services) {
                            stage("Test: ${svc}") {
                                sh "mvn test jacoco:report -pl ${svc} -am '-Dsurefire.excludes=**/*IT.java,**/*IT\$*.java,**/ProductCdcConsumerTest.java,**/ProductVectorRepositoryTest.java,**/VectorQueryTest.java'"
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
                    script {
                        def services = getChangedServices().toList().sort()
                        if (services.isEmpty()) {
                            echo 'Không phát hiện service thay đổi. Bỏ qua publish JaCoCo.'
                        } else {
                            def execPatterns = services.collect { "${it}/target/jacoco.exec" }.join(',')
                            def classPatterns = services.collect { "${it}/target/classes" }.join(',')
                            def sourcePatterns = services.collect { "${it}/src/main/java" }.join(',')

                            jacoco execPattern: execPatterns,
                                   classPattern: classPatterns,
                                   sourcePattern: sourcePatterns
                        }
                    }
                }
            }
        }

        stage('Build') {
            steps {
                script {
                    def services = getChangedServices().toList().sort()
                    
                    if (services.isEmpty()) {
                        echo 'Đang đóng gói TOÀN BỘ ứng dụng (Bỏ qua test vì đã chạy ở stage trước)...'
                        sh 'mvn package -DskipTests -DskipCompile=false'
                    } else {
                        def serviceSelector = services.join(',')
                        echo "Đang đóng gói CÁC SERVICE BỊ THAY ĐỔI: ${services}"
                        sh "mvn package -pl ${serviceSelector} -am -DskipTests -DskipCompile=false"
                    }
                }
            }
        }
    }
}
