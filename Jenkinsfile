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

    // Priority: git diff vs main — catches brand-new branches too
    def gitDiffOutput = ''
    try {
        sh(script: 'git fetch origin main --no-tags --depth=1', returnStdout: false)
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
        // Fallback: use changeSets for additional commits pushed to an existing branch
        paths = getAffectedPaths()
    }

    // Deduplicate folders BEFORE calling fileExists to avoid O(n) filesystem checks
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
                echo 'Đang kiểm tra phiên bản Java'
                sh 'java -version'

                script {
                    def services = getChangedServices()
                    def isManualTrigger = currentBuild.getBuildCauses('hudson.model.Cause$UserIdCause').size() > 0
                    def isMainBranch = env.BRANCH_NAME == 'main'

                    if (isManualTrigger && services.isEmpty()) {
                        sh "mvn clean install -DskipTests -Djacoco.skip=true"
                        sh "mvn verify '-Dsurefire.excludes=**/*IT.java,**/*IT\$*.java,**/ProductCdcConsumerTest.java,**/ProductVectorRepositoryTest.java,**/VectorQueryTest.java' '-Dfailsafe.excludes=**/*IT.java,**/*IT\$*.java'"
                    } else if (isMainBranch && services.isEmpty()) {
                        echo 'Branch main không phát hiện service thay đổi, chạy verify toàn bộ để đảm bảo coverage ổn định.'
                        sh "mvn clean install -DskipTests -Djacoco.skip=true"
                        sh "mvn verify '-Dsurefire.excludes=**/*IT.java,**/*IT\$*.java,**/ProductCdcConsumerTest.java,**/ProductVectorRepositoryTest.java,**/VectorQueryTest.java' '-Dfailsafe.excludes=**/*IT.java,**/*IT\$*.java'"
                    } else if (services.isEmpty()) {
                        echo 'Không có service nào thay đổi so với main. Bỏ qua bước Test.'
                    } else {
                        echo "Đang chạy Unit Test và kiểm tra Coverage cho CÁC SERVICE BỊ THAY ĐỔI: ${services}"
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
                    echo 'Đang Upload Test Result và Test Coverage cho Phase Test...'
                    junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml'
                    script {
                        def services = getChangedServices()
                        def classPatterns = '**/target/classes'
                        def sourcePatterns = '**/src/main/java'

                        if (!services.isEmpty()) {
                            classPatterns = services.collect { "${it}/target/classes" }.join(',')
                            sourcePatterns = services.collect { "${it}/src/main/java" }.join(',')
                            echo "JaCoCo scope theo service thay đổi: ${services}"
                        } else {
                            echo 'JaCoCo scope toàn bộ workspace vì không xác định được service thay đổi.'
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
                        echo "Đang đóng gói CÁC SERVICE BỊ THAY ĐỔI: ${services}"
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
