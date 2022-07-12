dependencies {
    //remark
    compile("org.springframework.boot:spring-boot-starter-aop")
    compile("org.redisson:redisson:3.11.2")
    compile("org.springframework.boot:spring-boot-starter-data-redis")
    compile("com.google.guava:guava:28.0-jre")
    compile("commons-beanutils:commons-beanutils:1.9.3")
    annotationProcessor("org.projectlombok:lombok:1.18.10")
    compileOnly("org.projectlombok:lombok:1.18.10")
    compile(project(":json-starter"))
}

