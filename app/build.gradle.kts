plugins {
    application
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":fastcgi-lib"))
}

tasks.jar {
    archiveFileName.set("server.jar")

    manifest {
        attributes["Main-Class"] = "ru.itmo.se.web.fastcgi.Server"
        attributes["Class-Path"] = "fastcgi-lib.jar"
    }
}

application {
    mainClass.set("ru.itmo.se.web.fastcgi.Server")
}
